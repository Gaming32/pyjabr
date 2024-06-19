package io.github.gaming32.pyjabr.interop;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropUtils {
    public static MemorySegment raiseException(MemorySegment exceptionClass, String message) {
        try (Arena arena = Arena.ofConfined()) {
            PyErr_SetString(exceptionClass, arena.allocateFrom(message));
        }
        return MemorySegment.NULL;
    }

    public static boolean checkArity(MemorySegment[] args, int arity) {
        if (args.length != arity) {
            raiseException(PyExc_TypeError(), "expected " + arity + " arguments, got " + args.length);
            return false;
        }
        return true;
    }

    public static String getString(MemorySegment unicode) {
        final MemorySegment utf8 = PyUnicode_AsUTF8AndSize(unicode, MemorySegment.NULL);
        if (utf8.equals(MemorySegment.NULL)) {
            return null;
        }
        return utf8.getString(0L);
    }

    public static Integer getInt(MemorySegment pyLong) {
        final int asInt = PyLong_AsLong(pyLong);
        if (asInt == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
            return null;
        }
        return asInt;
    }

    public static Long getLong(MemorySegment pyLong) {
        final long asLong = PyLong_AsLongLong(pyLong);
        if (asLong == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
            return null;
        }
        return asLong;
    }

    public static Double getDouble(MemorySegment pyFloat) {
        final double asDouble = PyFloat_AsDouble(pyFloat);
        if (asDouble == -1.0 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
            return null;
        }
        return asDouble;
    }

    public static MemorySegment invokeCallable(MemorySegment callable, MemorySegment... args) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment argsArray = arena.allocate(C_POINTER, args.length);
            for (int i = 0; i < args.length; i++) {
                argsArray.setAtIndex(C_POINTER, i, args[i]);
            }
            final long nargsf = args.length | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return PyObject_Vectorcall(callable, argsArray, nargsf, _Py_NULL());
        }
    }
}
