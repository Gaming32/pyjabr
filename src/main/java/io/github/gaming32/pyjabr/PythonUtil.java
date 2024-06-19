package io.github.gaming32.pyjabr;

import org.python.PyObject;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class PythonUtil {
    public static MemorySegment PyObject_CallOneArg(MemorySegment callable, MemorySegment arg) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment args = arena.allocate(C_POINTER, 1);
            args.setAtIndex(C_POINTER, 0, arg);
            final long nargsf = 1 | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return PyObject_Vectorcall(callable, args, nargsf, _Py_NULL());
        }
    }

    public static MemorySegment PyObject_CallMethodNoArgs(MemorySegment self, MemorySegment name) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment args = arena.allocate(C_POINTER, 1);
            args.setAtIndex(C_POINTER, 0, self);
            final long nargsf = 1 | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return PyObject_VectorcallMethod(name, args, nargsf, _Py_NULL());
        }
    }

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
        return PyObject_IsInstance(op, PyLong_Type()) != 0;
    }

    public static boolean PyUnicode_Check(MemorySegment op) {
        return PyObject_IsInstance(op, PyUnicode_Type()) != 0;
    }

    public static boolean PyTuple_Check(MemorySegment op) {
        return PyObject_IsInstance(op, PyTuple_Type()) != 0;
    }

    public static boolean PyDict_Check(MemorySegment op) {
        return PyObject_IsInstance(op, PyDict_Type()) != 0;
    }

    public static MemorySegment Py_TYPE(MemorySegment ob) {
        return PyObject.ob_type(ob);
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

    public static int PyObject_DelAttrString(MemorySegment v, MemorySegment name) {
        return PyObject_SetAttrString(v, name, MemorySegment.NULL);
    }
}
