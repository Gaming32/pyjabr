package io.github.gaming32.pythonfiddle;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;

import static org.python.Python_h.*;

public class TupleUtil {
    private static final int MAX_FAST_TUPLE_SIZE = 8;
    private static final PyTuple_Pack[] SMALL_PACKS = new PyTuple_Pack[MAX_FAST_TUPLE_SIZE];

    static {
        for (int count = 1; count <= MAX_FAST_TUPLE_SIZE; count++) {
            SMALL_PACKS[count - 1] = makePack(count);
        }
    }

    public static MemorySegment createTuple(MemorySegment... args) {
        if (args.length == 0) {
            return PyTuple_New(0L);
        }
        if (args.length <= MAX_FAST_TUPLE_SIZE) {
            return SMALL_PACKS[args.length - 1].apply(args.length, (Object[])args);
        }
        final MemorySegment result = PyTuple_New(args.length);
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

    private static PyTuple_Pack makePack(int size) {
        final MemoryLayout[] args = new MemoryLayout[size];
        Arrays.fill(args, C_POINTER);
        return PyTuple_Pack.makeInvoker(args);
    }
}
