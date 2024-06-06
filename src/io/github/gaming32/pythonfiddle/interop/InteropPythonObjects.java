package io.github.gaming32.pythonfiddle.interop;

import com.google.common.base.Suppliers;
import io.github.gaming32.pythonfiddle.TupleUtil;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.function.Supplier;

import static org.python.Python_h.*;

public class InteropPythonObjects {
    public static final Supplier<MemorySegment> _JAVA_INIT = Suppliers.memoize(() -> {
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyImport_ImportModule(arena.allocateFrom("_java_init")));
        }
    });

    public static final Supplier<MemorySegment> JAVA_ATTRIBUTE_NOT_FOUND = Suppliers.memoize(() -> {
        final MemorySegment initModule = _JAVA_INIT.get();
        if (initModule.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyObject_GetAttrString(initModule, arena.allocateFrom("JavaAttributeNotFound")));
        }
    });

    public static final Supplier<MemorySegment> JAVA_ERROR = Suppliers.memoize(() -> {
        final MemorySegment initModule = _JAVA_INIT.get();
        if (initModule.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyObject_GetAttrString(initModule, arena.allocateFrom("JavaError")));
        }
    });

    public static final Supplier<MemorySegment> FAKE_JAVA_STATIC_METHOD = Suppliers.memoize(() -> {
        final MemorySegment initModule = _JAVA_INIT.get();
        if (initModule.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyObject_GetAttrString(initModule, arena.allocateFrom("FakeJavaStaticMethod")));
        }
    });

    public static MemorySegment createFakeJavaStaticMethod(MemorySegment owner, MemorySegment nameObject, int id) {
        final MemorySegment idObject = PyLong_FromLong(id);
        if (idObject.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        final MemorySegment constructor = FAKE_JAVA_STATIC_METHOD.get();
        if (constructor.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        final MemorySegment args = TupleUtil.createTuple(owner, nameObject, idObject);
        if (args.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        return PyObject_CallObject(constructor, args);
    }

    private static MemorySegment checkNotNull(MemorySegment segment) {
        if (!segment.equals(MemorySegment.NULL)) {
            return segment;
        }
        final MemorySegment cause = PyErr_GetRaisedException();
        try (Arena arena = Arena.ofConfined()) {
            PyErr_SetString(PyExc_SystemError(), arena.allocateFrom("failed to load critical Java interop component"));
        }
        if (!cause.equals(MemorySegment.NULL)) {
            final MemorySegment actual = PyErr_GetRaisedException();
            PyException_SetCause(actual, cause);
            PyErr_SetRaisedException(actual);
        }
        return MemorySegment.NULL;
    }
}
