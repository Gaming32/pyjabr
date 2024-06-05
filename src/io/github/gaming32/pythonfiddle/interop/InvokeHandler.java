package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.PythonException;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeHandler {
    public static MemorySegment invoke(
        FieldOrExecutable.ExecutablesWrapper executables,
        Object owner, MemorySegment... args
    ) throws Throwable {
        for (final Executable executable : executables.executables()) {
            if (!isLengthApplicable(executable, args)) continue;
            final Object[] builtArgs;
            try {
                builtArgs = buildArgs(executable, args);
            } catch (IllegalArgumentException e) {
                if (e.getCause() instanceof PythonException pythonException) {
                    pythonException.clearOriginalException();
                }
                continue;
            }
            try {
                return InteropConversions.javaToPython(switch (executable) {
                    case Method m -> m.invoke(owner, builtArgs);
                    case Constructor<?> c -> c.newInstance(builtArgs);
                });
            } catch (InvocationTargetException targetException) {
                throw targetException.getCause();
            }
        }
        return null;
    }

    private static boolean isLengthApplicable(Executable executable, MemorySegment[] args) {
        final int paramCount = executable.getParameterCount();
        if (executable.isVarArgs()) {
            return args.length >= paramCount - 1;
        } else {
            return args.length == paramCount;
        }
    }

    private static Object[] buildArgs(Executable executable, MemorySegment[] args) throws IllegalArgumentException {
        final Class<?>[] paramTypes = executable.getParameterTypes();
        final Object[] result = new Object[paramTypes.length];
        final int simpleParamCount = executable.isVarArgs() ? result.length - 1 : result.length;
        for (int i = 0; i < simpleParamCount; i++) {
            result[i] = InteropConversions.pythonToJava(args[i], paramTypes[i]);
        }
        if (executable.isVarArgs()) {
            final Class<?> componentType = paramTypes[paramTypes.length - 1].componentType();
            final int extraArgCount = args.length - simpleParamCount;
            final Object finalElement = Array.newInstance(componentType, extraArgCount);
            for (int i = 0; i < extraArgCount; i++) {
                Array.set(finalElement, i, InteropConversions.pythonToJava(args[simpleParamCount + i], componentType));
            }
        }
        return result;
    }
}
