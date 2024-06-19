package io.github.gaming32.pyjabr.interop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import io.github.gaming32.pyjabr.PythonUtil;
import io.github.gaming32.pyjabr.ReflectUtil;
import io.github.gaming32.pyjabr.python.PythonException;
import org.jetbrains.annotations.NotNull;
import org.python.Python_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import static org.python.Python_h.*;

class LambdaMaker {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final MethodHandle PYTHON_TO_JAVA_TYPED;
    private static final MethodHandle PYTHON_TO_JAVA_UNTYPED;
    private static final MethodHandle JAVA_TO_PYTHON;

    private static final MethodHandle PY_OBJECT_CALL_NO_ARGS;
    private static final MethodHandle PY_OBJECT_CALL_ONE_ARG;
    private static final MethodHandle PERFORM_VECTORCALL;

    private static final MethodHandle WRAP_PYTHON_EXCEPTION;

    private static final LoadingCache<Class<?>, LambdaInfo> FUNCTIONAL_INTERFACE_CACHE = CacheBuilder.newBuilder()
        .weakKeys()
        .softValues()
        .build(new CacheLoader<>() {
            @NotNull
            @Override
            public LambdaInfo load(@NotNull Class<?> key) throws Exception {
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
            PERFORM_VECTORCALL = LOOKUP.findStatic(
                LambdaMaker.class, "performVectorcall",
                MethodType.methodType(MemorySegment.class, MemorySegment.class, MemorySegment[].class)
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
        return (Object)lambdaInfo.factory.invokeExact(mh);
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
        MethodHandle handle = PERFORM_VECTORCALL.bindTo(action);
        handle = handle.asCollector(MemorySegment[].class, lambdaInfo.argTypes.length);
        handle = MethodHandles.filterArguments(handle, 0, createParamAdapters(lambdaInfo.argTypes));
        return adaptReturnType(handle, lambdaInfo);
    }

    private static MemorySegment performVectorcall(MemorySegment self, MemorySegment[] args) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment argsArray = arena.allocate(C_POINTER, args.length);
            for (int i = 0; i < args.length; i++) {
                argsArray.setAtIndex(C_POINTER, i, args[i]);
            }
            final long nargsf = args.length | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return PyObject_Vectorcall(self, argsArray, nargsf, _Py_NULL());
        }
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
            mh, MethodHandles.insertArguments(PYTHON_TO_JAVA_TYPED, 1, lambdaInfo.returnType)
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
