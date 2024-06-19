package io.github.gaming32.pyjabr.object;

import com.google.common.base.Suppliers;
import io.github.gaming32.pyjabr.interop.InteropConversions;
import io.github.gaming32.pyjabr.lowlevel.TupleUtil;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.github.gaming32.pyjabr.lowlevel.PythonSystem.callPython;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public final class PythonObjects {
    private static final MemorySegment BUILTINS = Arena.global().allocateFrom("builtins");

    private static final Supplier<PythonObject> NONE = Suppliers.memoize(() -> PythonObject.steal(_Py_NoneStruct()));
    private static final Supplier<PythonObject> FALSE = Suppliers.memoize(() -> PythonObject.steal(_Py_FalseStruct()));
    private static final Supplier<PythonObject> TRUE = Suppliers.memoize(() -> PythonObject.steal(_Py_TrueStruct()));

    private PythonObjects() {
    }

    public static PythonObject none() {
        return NONE.get();
    }

    public static PythonObject pythonInt(int value) {
        return PythonObject.checkAndSteal(callPython(() -> PyLong_FromLong(value)));
    }

    public static PythonObject pythonInt(long value) {
        return PythonObject.checkAndSteal(callPython(() -> PyLong_FromLongLong(value)));
    }

    public static PythonObject pythonFloat(double value) {
        return PythonObject.checkAndSteal(callPython(() -> PyFloat_FromDouble(value)));
    }

    public static PythonObject str(String value) {
        return PythonObject.steal(callPython(() -> InteropConversions.createPythonString(value)));
    }

    public static PythonObject bool(boolean value) {
        return value ? TRUE.get() : FALSE.get();
    }

    public static PythonObject tuple(PythonObject... values) {
        final MemorySegment[] pyObjects = new MemorySegment[values.length];
        for (int i = 0; i < values.length; i++) {
            final MemorySegment pyObject = values[i].borrow();
            Py_IncRef(pyObject);
            pyObjects[i] = pyObject;
        }
        return PythonObject.checkAndSteal(callPython(() -> TupleUtil.createTuple(pyObjects)));
    }

    public static PythonObject list(List<PythonObject> values) {
        return callPython(() -> {
            final MemorySegment result = PyList_New(values.size());
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            int i = 0;
            for (final PythonObject value : values) {
                final MemorySegment pyObject = value.borrow();
                Py_IncRef(pyObject);
                PyList_SetItem(result, i++, pyObject);
            }
            return PythonObject.steal(result);
        });
    }

    public static PythonObject dict(Map<PythonObject, PythonObject> values) {
        return callPython(() -> {
            final MemorySegment result = PyDict_New();
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            for (final var entry : values.entrySet()) {
                final MemorySegment key = entry.getKey().borrow();
                final MemorySegment value = entry.getValue().borrow();
                if (PyDict_SetItem(result, key, value) == -1) {
                    Py_DecRef(result);
                    throw PythonException.moveFromPython();
                }
            }
            return PythonObject.steal(result);
        });
    }

    public static PythonObject stringDict(Map<String, PythonObject> values) {
        return callPython(() -> {
            final MemorySegment result = PyDict_New();
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            final Arena arena = Arena.ofAuto();
            for (final var entry : values.entrySet()) {
                final MemorySegment key = arena.allocateFrom(entry.getKey());
                final MemorySegment value = entry.getValue().borrow();
                if (PyDict_SetItemString(result, key, value) == -1) {
                    Py_DecRef(result);
                    throw PythonException.moveFromPython();
                }
            }
            return PythonObject.steal(result);
        });
    }

    public static PythonObject importModule(String module) {
        try (Arena arena = Arena.ofConfined()) {
            return PythonObject.checkAndSteal(callPython(() -> PyImport_ImportModuleLevel(
                arena.allocateFrom(module), MemorySegment.NULL, MemorySegment.NULL, MemorySegment.NULL, 0
            )));
        }
    }

    public static PythonObject getBuiltins() {
        return callPython(() -> PythonObject.checkAndSteal(PyImport_ImportModuleLevel(
            BUILTINS, MemorySegment.NULL, MemorySegment.NULL, MemorySegment.NULL, 0
        )));
    }

    public static PythonObject getBuiltin(String name) {
        return getBuiltins().getAttr(name);
    }

    public static PythonObject callBuiltin(String name) {
        return getBuiltins().callMethod(name);
    }

    public static PythonObject callBuiltin(String name, PythonObject arg) {
        return getBuiltins().callMethod(name, arg);
    }

    public static PythonObject callBuiltin(String name, PythonObject... args) {
        return getBuiltins().callMethod(name, args);
    }
}
