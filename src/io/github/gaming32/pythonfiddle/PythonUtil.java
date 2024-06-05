package io.github.gaming32.pythonfiddle;

import org.python.PyObject;
import org.python.PyTypeObject;

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

    public static boolean PyLong_Check(MemorySegment op) {
        return PyType_FastSubclass(Py_TYPE(op), Py_TPFLAGS_LONG_SUBCLASS());
    }

    public static boolean PyUnicode_Check(MemorySegment op) {
        return PyType_FastSubclass(Py_TYPE(op), Py_TPFLAGS_UNICODE_SUBCLASS());
    }

    public static boolean PyTuple_Check(MemorySegment op) {
        return PyType_FastSubclass(Py_TYPE(op), Py_TPFLAGS_TUPLE_SUBCLASS());
    }

    public static MemorySegment Py_TYPE(MemorySegment ob) {
        return PyObject.ob_type(ob);
    }

    public static boolean PyType_FastSubclass(MemorySegment type, int flag) {
        return PyType_HasFeature(type, flag);
    }

    public static boolean PyType_HasFeature(MemorySegment type, int feature) {
        final int flags = PyTypeObject.tp_flags(type);
        return (flags & feature) != 0;
    }

    public static boolean PyFloat_Check(MemorySegment op) {
        return PyObject_TypeCheck(op, PyFloat_Type());
    }

    public static boolean PyObject_TypeCheck(MemorySegment ob, MemorySegment type) {
        return Py_IS_TYPE(ob, type) || PyType_IsSubtype(Py_TYPE(ob), type) != 0;
    }

    public static boolean PyBool_Check(MemorySegment x) {
        return Py_IS_TYPE(x, PyBool_Type());
    }

    public static boolean Py_IS_TYPE(MemorySegment ob, MemorySegment type) {
        return Py_TYPE(ob).equals(type);
    }
}
