package io.github.gaming32.pythonfiddle.interop;

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
            raiseException(PyExc_TypeError(), "Expected " + arity + " arguments, got " + args.length);
            return false;
        }
        return true;
    }

    public static String getString(MemorySegment unicode) {
        final MemorySegment utf8 = PyUnicode_AsUTF8(unicode);
        if (utf8.equals(MemorySegment.NULL)) {
            return null;
        }
        return utf8.getString(0L);
    }
}
