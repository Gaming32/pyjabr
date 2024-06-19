package io.github.gaming32.pyjabr.lowlevel;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class TupleUtil {
    public static MemorySegment createTuple(MemorySegment... args) {
        final MemorySegment result = PyTuple_New(args.length);
        if (result.equals(MemorySegment.NULL)) {
            for (final MemorySegment arg : args) {
                Py_DecRef(arg);
            }
            return MemorySegment.NULL;
        }
        for (int i = 0; i < args.length; i++) {
            PyTuple_SetItem(result, i, args[i]);
        }
        return result;
    }

    public static MemorySegment[] unpackTuple(MemorySegment tuple) {
        final long length = PyTuple_Size(tuple);
        if (length > Integer.MAX_VALUE - 8) {
            throw new IllegalArgumentException("Tuple has too many elements to unpack (" + length + ")");
        }
        final MemorySegment[] result = new MemorySegment[(int)length];
        for (int i = 0; i < result.length; i++) {
            result[i] = PyTuple_GetItem(tuple, i);
            if (result[i].equals(MemorySegment.NULL)) {
                return null;
            }
        }
        return result;
    }
}
