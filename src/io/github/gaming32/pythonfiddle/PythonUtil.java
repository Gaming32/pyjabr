package io.github.gaming32.pythonfiddle;

import static org.python.Python_h.*;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class PythonUtil {
    public static MemorySegment PyObject_CallMethodOneArg(MemorySegment self, MemorySegment name, MemorySegment arg) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment args = arena.allocate(C_POINTER, 2);
            args.setAtIndex(C_POINTER, 0, self);
            args.setAtIndex(C_POINTER, 1, arg);
            final long nargsf = 2 | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return PyObject_VectorcallMethod(name, args, nargsf, _Py_NULL());
        }
    }
}
