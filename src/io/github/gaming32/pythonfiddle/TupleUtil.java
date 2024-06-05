package io.github.gaming32.pythonfiddle;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;

import static org.python.Python_h.*;

public class TupleUtil {
    private static final int MAX_FAST_TUPLE_SIZE = 4;
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
        final PyTuple_Pack pack;
        if (args.length <= MAX_FAST_TUPLE_SIZE) {
            pack = SMALL_PACKS[args.length - 1];
        } else {
            pack = makePack(args.length);
        }
        return pack.apply(args.length, (Object[])args);
    }

    private static PyTuple_Pack makePack(int size) {
        final MemoryLayout[] args = new MemoryLayout[size];
        Arrays.fill(args, C_POINTER);
        return PyTuple_Pack.makeInvoker(args);
    }
}
