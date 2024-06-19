package io.github.gaming32.pyjabr.interop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import io.github.gaming32.pyjabr.lowlevel.PythonUtil;
import io.github.gaming32.pyjabr.lowlevel.cpython.Python_h;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.util.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

class LambdaMaker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final MethodHandle PYTHON_TO_JAVA_TYPED;
    private static final MethodHandle PYTHON_TO_JAVA_UNTYPED;
    private static final MethodHandle JAVA_TO_PYTHON;

    private static final MethodHandle PY_OBJECT_CALL_NO_ARGS;
    private static final MethodHandle PY_OBJECT_CALL_ONE_ARG;
    private static final MethodHandle INVOKE_CALLABLE;

    private static final MethodHandle PY_GIL_STATE_ENSURE;
    private static final MethodHandle PY_GIL_STATE_RELEASE;

    private static final MethodHandle WRAP_PYTHON_EXCEPTION;

    private static final LoadingCache<Class<?>, LambdaInfo> FUNCTIONAL_INTERFACE_CACHE = CacheBuilder.newBuilder()
        .weakKeys()
        .softValues()
        .build(new CacheLoader<>() {
            @NotNull
            @Override
            public LambdaInfo load(@NotNull Class<?> key) throws LambdaConversionException {
                final Method sam = ReflectUtil.findSam(key);
                if (sam == null) {
                    throw new IllegalArgumentException("Not a functional interface: " + key);
                }
                final MethodHandle factory = createFactory(key, sam);
                return new LambdaInfo(
                    factory.asType(factory.type().changeReturnType(Object.class)),
                    sam.getParameterTypes(),
                    sam.getReturnType()
                );
            }
        });

    static {
        try {
            PYTHON_TO_JAVA_TYPED = LOOKUP.findStatic(
                InteropConversions.class, "pythonToJava",
                MethodType.methodType(Object.class, MemorySegment.class, Class.class)
            );
            PYTHON_TO_JAVA_UNTYPED = LOOKUP.findStatic(
                InteropConversions.class, "pythonToJava",
                MethodType.methodType(Object.class, MemorySegment.class)
            );
            JAVA_TO_PYTHON = LOOKUP.findStatic(
                InteropConversions.class, "javaToPython",
                MethodType.methodType(MemorySegment.class, Object.class)
            );

            PY_OBJECT_CALL_NO_ARGS = LOOKUP.findStatic(
                Python_h.class, "PyObject_CallNoArgs",
                MethodType.methodType(MemorySegment.class, MemorySegment.class)
            );
            PY_OBJECT_CALL_ONE_ARG = LOOKUP.findStatic(
                PythonUtil.class, "PyObject_CallOneArg",
                MethodType.methodType(MemorySegment.class, MemorySegment.class, MemorySegment.class)
            );
            INVOKE_CALLABLE = LOOKUP.findStatic(
                InteropUtils.class, "invokeCallable",
                MethodType.methodType(MemorySegment.class, MemorySegment.class, MemorySegment[].class)
            ).asFixedArity();

            PY_GIL_STATE_ENSURE = LOOKUP.findStatic(
                Python_h.class, "PyGILState_Ensure",
                MethodType.methodType(int.class)
            );
            PY_GIL_STATE_RELEASE = LOOKUP.findStatic(
                Python_h.class, "PyGILState_Release",
                MethodType.methodType(void.class, int.class)
            );

            WRAP_PYTHON_EXCEPTION = MethodHandles.guardWithTest(
                LOOKUP.findVirtual(
                    MemorySegment.class, "equals",
                    MethodType.methodType(boolean.class, Object.class)
                ).bindTo(MemorySegment.NULL).asType(MethodType.methodType(boolean.class, MemorySegment.class)),
                MethodHandles.filterArguments(
                    MethodHandles.throwException(MemorySegment.class, PythonException.class),
                    0,
                    MethodHandles.dropArguments(
                        LOOKUP.findStatic(
                            PythonException.class, "moveFromPython",
                            MethodType.methodType(PythonException.class)
                        ),
                        0,
                        MemorySegment.class
                    )
                ),
                MethodHandles.identity(MemorySegment.class)
            );
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static Object makeLambda(Class<?> lambdaClass, MemorySegment action) throws Throwable {
        if (!lambdaClass.isInterface()) {
            throw new IllegalArgumentException("Not a functional interface: " + lambdaClass);
        }
        final LambdaInfo lambdaInfo;
        try {
            lambdaInfo = FUNCTIONAL_INTERFACE_CACHE.get(lambdaClass);
        } catch (ExecutionException | UncheckedExecutionException e) {
            throw e.getCause();
        }
        final MethodHandle mh = switch (lambdaInfo.argTypes.length) {
            case 0 -> handleNoArgs(lambdaInfo, action);
            case 1 -> handleOneArg(lambdaInfo, action);
            default -> handleVectorcall(lambdaInfo, action);
        };
        return (Object)lambdaInfo.factory.invokeExact(wrapMethod(mh));
    }

    private static MethodHandle wrapMethod(MethodHandle mh) {
        mh = MethodHandles.dropArguments(mh, 0, int.class);

        final Class<?> returnType = mh.type().returnType();
        MethodHandle release = returnType == void.class
            ? MethodHandles.empty(mh.type())
            : MethodHandles.dropArguments(MethodHandles.identity(returnType), 1, mh.type().parameterArray());
        release = MethodHandles.filterArguments(
            release,
            returnType == void.class ? 0 : 1,
            MethodHandles.filterReturnValue(PY_GIL_STATE_RELEASE, MethodHandles.zero(int.class))
        );
        release = MethodHandles.dropArguments(release, 0, Throwable.class);

        mh = MethodHandles.tryFinally(mh, release);
        mh = MethodHandles.collectArguments(mh, 0, PY_GIL_STATE_ENSURE);
        return mh;
    }

    private static MethodHandle handleNoArgs(LambdaInfo lambdaInfo, MemorySegment action) {
        return adaptReturnType(PY_OBJECT_CALL_NO_ARGS.bindTo(action), lambdaInfo);
    }

    private static MethodHandle handleOneArg(LambdaInfo lambdaInfo, MemorySegment action) {
        MethodHandle handle = PY_OBJECT_CALL_ONE_ARG.bindTo(action);
        handle = MethodHandles.filterArguments(
            handle,
            0,
            createParamAdapters(lambdaInfo.argTypes)
        );
        return adaptReturnType(handle, lambdaInfo);
    }

    private static MethodHandle handleVectorcall(LambdaInfo lambdaInfo, MemorySegment action) {
        MethodHandle handle = INVOKE_CALLABLE.bindTo(action);
        handle = handle.asCollector(MemorySegment[].class, lambdaInfo.argTypes.length);
        handle = MethodHandles.filterArguments(handle, 0, createParamAdapters(lambdaInfo.argTypes));
        return adaptReturnType(handle, lambdaInfo);
    }

    private static MethodHandle[] createParamAdapters(Class<?>... argTypes) {
        final MethodHandle[] result = new MethodHandle[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            result[i] = JAVA_TO_PYTHON.asType(MethodType.methodType(MemorySegment.class, argTypes[i]));
        }
        return result;
    }

    private static MethodHandle adaptReturnType(MethodHandle mh, LambdaInfo lambdaInfo) {
        mh = MethodHandles.filterReturnValue(mh, WRAP_PYTHON_EXCEPTION);
        if (lambdaInfo.returnType == void.class) {
            return MethodHandles.dropReturn(mh);
        }
        if (lambdaInfo.returnType == Object.class) {
            return MethodHandles.filterReturnValue(mh, PYTHON_TO_JAVA_UNTYPED);
        }
        return MethodHandles.filterReturnValue(
            mh,
            MethodHandles.insertArguments(PYTHON_TO_JAVA_TYPED, 1, lambdaInfo.returnType)
                .asType(MethodType.methodType(lambdaInfo.returnType, MemorySegment.class))
        );
    }

    private static MethodHandle createFactory(Class<?> lambdaClass, Method sam) throws LambdaConversionException {
        final MethodType handlerType = ReflectUtil.getType(sam);
        return LambdaMetafactory.metafactory(
            LOOKUP,
            sam.getName(),
            MethodType.methodType(lambdaClass, MethodHandle.class),
            handlerType,
            MethodHandles.exactInvoker(handlerType),
            handlerType
        ).getTarget();
    }

    private record LambdaInfo(MethodHandle factory, Class<?>[] argTypes, Class<?> returnType) {
    }
}
