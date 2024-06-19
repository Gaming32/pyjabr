package io.github.gaming32.pyjabr.module;

import com.google.common.primitives.Primitives;
import io.github.gaming32.pyjabr.lowlevel.cpython.Python_h;
import io.github.gaming32.pyjabr.lowlevel.interop.InteropConversions;
import io.github.gaming32.pyjabr.lowlevel.interop.InteropUtils;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h._Py_NoneStruct;

class FunctionAdapter {
    private static final MethodHandle NULL_POINTER = MethodHandles.constant(MemorySegment.class, MemorySegment.NULL);
    private static final MethodHandle THROW_PYTHON_EXCEPTION = MethodHandles.dropArguments(NULL_POINTER, 0, MemorySegment[].class);
    private static final MethodHandle CATCH_ADAPT_FAILED = MethodHandles.dropArguments(NULL_POINTER, 0, AdaptFailedException.class);
    private static final MethodHandle CONSTANT_NONE = MethodHandles.constant(MemorySegment.class, _Py_NoneStruct());

    private static final MethodHandle NON_NULL;
    private static final MethodHandle CHECK_ARITY;
    private static final MethodHandle PYTHON_TO_JAVA;
    private static final MethodHandle JAVA_TO_PYTHON;

    private static final Map<Class<?>, MethodHandle> SPECIALIZED_ARG_ADAPTERS;
    private static final Map<Class<?>, MethodHandle> SPECIALIZED_RETURN_ADAPTERS;

    private static final MethodHandle IMPLEMENTATION_CONSTRUCTOR;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            NON_NULL = lookup.findStatic(
                Objects.class, "nonNull",
                MethodType.methodType(boolean.class, Object.class)
            );
            CHECK_ARITY = lookup.findStatic(
                InteropUtils.class, "checkArity",
                MethodType.methodType(boolean.class, MemorySegment[].class, int.class)
            );
            PYTHON_TO_JAVA = lookup.findStatic(
                InteropConversions.class, "pythonToJava",
                MethodType.methodType(Object.class, MemorySegment.class, Class.class)
            );
            JAVA_TO_PYTHON = lookup.findStatic(
                InteropConversions.class, "javaToPython",
                MethodType.methodType(MemorySegment.class, Object.class)
            );

            SPECIALIZED_ARG_ADAPTERS = Map.of(
                String.class, findSpecializedArgAdapter(lookup, "getString", String.class),
                int.class, findSpecializedArgAdapter(lookup, "getInt", int.class),
                long.class, findSpecializedArgAdapter(lookup, "getLong", long.class),
                double.class, findSpecializedArgAdapter(lookup, "getDouble", double.class)
            );
            SPECIALIZED_RETURN_ADAPTERS = Map.of(
                String.class, lookup.findStatic(
                    InteropConversions.class, "createPythonString",
                    MethodType.methodType(MemorySegment.class, String.class)
                ),
                int.class, findSpecializedReturnAdapter(lookup, "PyLong_FromLong", int.class),
                long.class, findSpecializedReturnAdapter(lookup, "PyLong_FromLongLong", long.class),
                double.class, findSpecializedReturnAdapter(lookup, "PyFloat_FromDouble", double.class)
            );

            final MethodType implementationType = MethodType.methodType(MemorySegment.class, MemorySegment.class, MemorySegment[].class);
            IMPLEMENTATION_CONSTRUCTOR = LambdaMetafactory.metafactory(
                lookup,
                "call",
                MethodType.methodType(CustomPythonFunction.Implementation.class, MethodHandle.class),
                implementationType,
                MethodHandles.exactInvoker(implementationType),
                implementationType
            ).getTarget();
        } catch (ReflectiveOperationException | LambdaConversionException e) {
            throw new IllegalStateException(e);
        }
    }

    static CustomPythonFunction.Implementation adapt(MethodHandles.Lookup lookup, Method method) throws IllegalAccessException {
        try {
            return (CustomPythonFunction.Implementation)IMPLEMENTATION_CONSTRUCTOR.invokeExact(adaptToMH(lookup, method));
        } catch (RuntimeException | Error | IllegalAccessException e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private static MethodHandle adaptToMH(MethodHandles.Lookup lookup, Method method) throws IllegalAccessException {
        final Class<?>[] targetArgs = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        MethodHandle handle = lookup.unreflect(method);
        handle = MethodHandles.filterArguments(handle, 0, createArgAdapters(targetArgs));
        if (returnType != MemorySegment.class) {
            if (returnType == void.class) {
                handle = MethodHandles.filterReturnValue(handle, CONSTANT_NONE);
            } else {
                MethodHandle returnAdapter = SPECIALIZED_RETURN_ADAPTERS.get(returnType);
                if (returnAdapter == null) {
                    returnAdapter = JAVA_TO_PYTHON.asType(MethodType.methodType(MemorySegment.class, returnType));
                }
                handle = MethodHandles.filterReturnValue(handle, returnAdapter);
            }
        }
        if (argAdaptersCanThrowSpecial(targetArgs)) {
            handle = MethodHandles.catchException(handle, AdaptFailedException.class, CATCH_ADAPT_FAILED);
        }
        handle = handle.asSpreader(MemorySegment[].class, targetArgs.length);
        handle = MethodHandles.guardWithTest(
            MethodHandles.insertArguments(CHECK_ARITY, 1, targetArgs.length),
            handle,
            THROW_PYTHON_EXCEPTION
        );
        return MethodHandles.dropArguments(handle, 0, MemorySegment.class);
    }

    private static MethodHandle[] createArgAdapters(Class<?>[] targetArgs) {
        final MethodHandle[] result = new MethodHandle[targetArgs.length];
        for (int i = 0; i < result.length; i++) {
            if (targetArgs[i] == MemorySegment.class) continue;
            final MethodHandle specialized = SPECIALIZED_ARG_ADAPTERS.get(targetArgs[i]);
            if (specialized != null) {
                result[i] = specialized;
            } else {
                result[i] = MethodHandles.insertArguments(PYTHON_TO_JAVA, 1, targetArgs[i]);
            }
        }
        return result;
    }

    private static boolean argAdaptersCanThrowSpecial(Class<?>[] targetArgs) {
        for (final Class<?> arg : targetArgs) {
            if (SPECIALIZED_ARG_ADAPTERS.containsKey(arg)) {
                return true;
            }
        }
        return false;
    }

    private static MethodHandle findSpecializedArgAdapter(
        MethodHandles.Lookup lookup, String name, Class<?> type
    ) throws ReflectiveOperationException {
        final Class<?> wrapped = Primitives.wrap(type);
        final MethodHandle origin = lookup.findStatic(
            InteropUtils.class,
            name,
            MethodType.methodType(wrapped, MemorySegment.class)
        );
        return MethodHandles.filterReturnValue(
            origin,
            MethodHandles.guardWithTest(
                NON_NULL.asType(MethodType.methodType(boolean.class, wrapped)),
                MethodHandles.identity(type).asType(MethodType.methodType(type, wrapped)),
                MethodHandles.dropArguments(
                    MethodHandles.throwException(type, AdaptFailedException.class)
                        .bindTo(AdaptFailedException.INSTANCE),
                    0, wrapped
                )
            )
        );
    }

    private static MethodHandle findSpecializedReturnAdapter(
        MethodHandles.Lookup lookup, String name, Class<?> type
    ) throws ReflectiveOperationException {
        final MethodType targetType = MethodType.methodType(MemorySegment.class, type);
        Class<?> searchClass = Python_h.class;
        while (searchClass != Object.class) {
            try {
                return lookup.findStatic(searchClass, name, targetType);
            } catch (NoSuchMethodException _) {
            }
            searchClass = searchClass.getSuperclass();
        }
        throw new NoSuchMethodException("Python_h." + name);
    }

    private static class AdaptFailedException extends RuntimeException {
        static final AdaptFailedException INSTANCE = new AdaptFailedException();

        private AdaptFailedException() {
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
