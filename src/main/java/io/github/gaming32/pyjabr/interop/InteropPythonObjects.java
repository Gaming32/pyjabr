package io.github.gaming32.pyjabr.interop;

import com.google.common.base.Suppliers;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.function.Supplier;

import static org.python.Python_h.*;

public class InteropPythonObjects {
    public static final Supplier<MemorySegment> JAVA_API = Suppliers.memoize(() -> {
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyImport_ImportModule(arena.allocateFrom("java_api")));
        }
    });
    public static final Supplier<MemorySegment> JAVA_ATTRIBUTE_NOT_FOUND = Suppliers.memoize(() -> getInitAttr("JavaAttributeNotFound"));
    public static final Supplier<MemorySegment> JAVA_ERROR = Suppliers.memoize(() -> getInitAttr("JavaError"));
    public static final Supplier<MemorySegment> FAKE_JAVA_OBJECT = Suppliers.memoize(() -> getInitAttr("FakeJavaObject"));
    public static final Supplier<MemorySegment> FAKE_JAVA_METHOD = Suppliers.memoize(() -> getInitAttr("FakeJavaMethod"));
    public static final Supplier<MemorySegment> FAKE_JAVA_CLASS = Suppliers.memoize(() -> getInitAttr("FakeJavaClass"));

    public static MemorySegment createFakeJavaObject(int id, MemorySegment classNameObject, int classId) {
        final MemorySegment idObject = PyLong_FromLong(id);
        if (idObject.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        final MemorySegment classIdObject = PyLong_FromLong(classId);
        if (classIdObject.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        final MemorySegment constructor = FAKE_JAVA_OBJECT.get();
        if (constructor.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        Py_IncRef(classNameObject);
        return InteropUtils.invokeCallable(constructor, idObject, classNameObject, classIdObject);
    }

    public static MemorySegment createFakeJavaMethod(MemorySegment owner, MemorySegment nameObject, int id) {
        final MemorySegment idObject = PyLong_FromLong(id);
        if (idObject.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        final MemorySegment constructor = FAKE_JAVA_METHOD.get();
        if (constructor.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        Py_IncRef(nameObject);
        return InteropUtils.invokeCallable(constructor, owner, nameObject, idObject);
    }

    public static MemorySegment createFakeJavaClass(MemorySegment nameObject, int id) {
        final MemorySegment idObject = PyLong_FromLong(id);
        if (idObject.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        final MemorySegment constructor = FAKE_JAVA_CLASS.get();
        if (constructor.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }

        Py_IncRef(nameObject);
        return InteropUtils.invokeCallable(constructor, nameObject, idObject);
    }

    private static MemorySegment getInitAttr(String attr) {
        final MemorySegment initModule = JAVA_API.get();
        if (initModule.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        try (Arena arena = Arena.ofConfined()) {
            return checkNotNull(PyObject_GetAttrString(initModule, arena.allocateFrom(attr)));
        }
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
