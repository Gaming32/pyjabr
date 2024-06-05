package io.github.gaming32.pythonfiddle.interop;

import com.google.common.primitives.Primitives;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropConversions {
    public static MemorySegment javaToPython(Object obj) {
        if (obj == null) {
            return _Py_NoneStruct();
        }
        if (obj instanceof String s) {
            return createPythonString(s);
        }
        final Class<?> sourceClass = obj.getClass();
        if (sourceClass.isPrimitive() || Primitives.isWrapperType(sourceClass)) {
            return primitiveToPython(obj);
        }
        throw new UnsupportedOperationException("Only primitive and string conversions are supported at this moment");
    }

    private static MemorySegment primitiveToPython(Object obj) {
        if (obj instanceof Integer i) {
            return PyLong_FromLong(i);
        }
        if (obj instanceof Long l) {
            return PyLong_FromLongLong(l);
        }
        if (obj instanceof Float f) {
            return PyFloat_FromDouble(f);
        }
        if (obj instanceof Double d) {
            return PyFloat_FromDouble(d);
        }
        if (obj instanceof Boolean b) {
            return b ? _Py_TrueStruct() : _Py_FalseStruct();
        }
        if (obj instanceof Character c) {
            return createPythonString(Character.toString(c));
        }
        if (obj instanceof Byte b) {
            return PyLong_FromLong(b);
        }
        if (obj instanceof Short s) {
            return PyLong_FromLong(s);
        }
        throw new IllegalArgumentException("primitiveToPython called with non-primitive source class");
    }

    public static MemorySegment createPythonString(String javaString) {
        try (Arena arena = Arena.ofConfined()) {
            return PyUnicode_FromString(arena.allocateFrom(javaString));
        }
    }
}
