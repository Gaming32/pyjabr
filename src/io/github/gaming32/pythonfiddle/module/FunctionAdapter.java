package io.github.gaming32.pythonfiddle.module;

import com.google.common.primitives.Primitives;
import io.github.gaming32.pythonfiddle.interop.InteropConversions;
import io.github.gaming32.pythonfiddle.interop.InteropUtils;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import static org.python.Python_h._Py_NoneStruct;

class FunctionAdapter {
    private static final MethodType IMPLEMENTATION_TYPE = MethodType.methodType(
        CustomPythonFunction.Implementation.class, MethodHandle.class
    );
    private static final MethodType IMPLEMENTATION_IMPL_TYPE = MethodType.methodType(
        MemorySegment.class, MemorySegment.class, MemorySegment[].class
    );

    private static final MethodHandle NULL_POINTER = MethodHandles.constant(MemorySegment.class, MemorySegment.NULL);
    private static final MethodHandle THROW_PYTHON_EXCEPTION = MethodHandles.dropArguments(NULL_POINTER, 0, MemorySegment[].class);
    private static final MethodHandle CATCH_ADAPT_FAILED = MethodHandles.dropArguments(NULL_POINTER, 0, AdaptFailedException.class);
    private static final MethodHandle CONSTANT_NONE = MethodHandles.constant(MemorySegment.class, _Py_NoneStruct());

    private static final MethodHandle NON_NULL;
    private static final MethodHandle CHECK_ARITY;
    private static final MethodHandle PYTHON_TO_JAVA;
    private static final MethodHandle JAVA_TO_PYTHON;

    private static final Map<Class<?>, MethodHandle> TYPE_SPECIFIC_ADAPTERS;

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

            TYPE_SPECIFIC_ADAPTERS = Map.of(
                String.class, findTypeSpecificAdapter(lookup, "getString", String.class),
                int.class, findTypeSpecificAdapter(lookup, "getInt", int.class),
                long.class, findTypeSpecificAdapter(lookup, "getLong", long.class),
                double.class, findTypeSpecificAdapter(lookup, "getDouble", double.class),
                float.class, findTypeSpecificAdapter(lookup, "getFloat", float.class)
            );
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static CustomPythonFunction.Implementation adapt(MethodHandles.Lookup lookup, Method method) throws IllegalAccessException {
        final MethodHandle handle = adaptToMH(lookup, method);
        try {
            return (CustomPythonFunction.Implementation)LambdaMetafactory.metafactory(
                lookup,
                "call",
                IMPLEMENTATION_TYPE,
                IMPLEMENTATION_IMPL_TYPE,
                MethodHandles.exactInvoker(handle.type()),
                IMPLEMENTATION_IMPL_TYPE
            ).getTarget().invokeExact(handle);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static MethodHandle adaptToMH(MethodHandles.Lookup lookup, Method method) throws IllegalAccessException {
        final Class<?>[] targetArgs = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        MethodHandle handle = lookup.unreflect(method);
        handle = MethodHandles.filterArguments(handle, 0, createAdapters(targetArgs));
        if (returnType != MemorySegment.class) {
            if (returnType == void.class) {
                handle = MethodHandles.filterReturnValue(handle, CONSTANT_NONE);
            } else {
                handle = MethodHandles.filterReturnValue(
                    handle,
                    JAVA_TO_PYTHON.asType(MethodType.methodType(MemorySegment.class, returnType))
                );
            }
        }
        if (adaptCanThrowSpecial(targetArgs)) {
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

    private static MethodHandle[] createAdapters(Class<?>[] targetArgs) {
        final MethodHandle[] result = new MethodHandle[targetArgs.length];
        for (int i = 0; i < result.length; i++) {
            if (targetArgs[i] == MemorySegment.class) continue;
            final MethodHandle specific = TYPE_SPECIFIC_ADAPTERS.get(targetArgs[i]);
            if (specific != null) {
                result[i] = specific;
            } else {
                result[i] = MethodHandles.insertArguments(PYTHON_TO_JAVA, 1, targetArgs[i]);
            }
        }
        return result;
    }

    private static boolean adaptCanThrowSpecial(Class<?>[] targetArgs) {
        for (final Class<?> arg : targetArgs) {
            if (TYPE_SPECIFIC_ADAPTERS.containsKey(arg)) {
                return true;
            }
        }
        return false;
    }

    private static MethodHandle findTypeSpecificAdapter(
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
