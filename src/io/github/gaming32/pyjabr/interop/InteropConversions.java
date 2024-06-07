package io.github.gaming32.pyjabr.interop;

import com.google.common.primitives.Primitives;
import io.github.gaming32.pyjabr.PythonException;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;
import static io.github.gaming32.pyjabr.PythonUtil.*;

public class InteropConversions {
    private static final MemorySegment ID_FIELD = Arena.global().allocateFrom("_id");

    public static Object pythonToJava(MemorySegment obj, Class<?> target) throws IllegalArgumentException {
        if (target == Object.class) {
            return pythonToJava(obj);
        }
        if (target == Void.class) {
            if (!obj.equals(_Py_NoneStruct())) {
                throw new IllegalArgumentException("Only None can be assigned to Void");
            }
            return obj;
        }
        if (obj.equals(_Py_NoneStruct())) {
            if (target.isPrimitive()) {
                throw new IllegalArgumentException("Cannot pass None to primitive type " + target);
            }
            return null;
        }
        if (target.isPrimitive() || Primitives.isWrapperType(target)) {
            return primitiveFromPython(obj, Primitives.unwrap(target));
        }
        if (target == String.class) {
            if (!PyUnicode_Check(obj)) {
                throw new IllegalArgumentException("Cannot convert " + toString(Py_TYPE(obj)) + " to String");
            }
            final String result = InteropUtils.getString(obj);
            if (result == null) {
                throw new IllegalArgumentException("Could not convert to String", PythonException.moveFromPython(true));
            }
            return result;
        }
        if (target == MemorySegment.class) {
            return obj;
        }
        final Object realObject = fakePythonToJava(obj);
        if (target.isInstance(realObject)) {
            return realObject;
        }
        throw new IllegalArgumentException("Cannot convert " + toString(Py_TYPE(obj)) + " to " + target);
    }

    public static Object pythonToJava(MemorySegment obj) {
        if (obj.equals(_Py_NoneStruct())) {
            return null;
        }
        if (PyBool_Check(obj)) {
            return obj.equals(_Py_TrueStruct());
        }
        if (PyLong_Check(obj)) {
            final int asInt = PyLong_AsLong(obj);
            if (asInt == -1 && isOverflow()) {
                final long asLong = PyLong_AsLongLong(obj);
                if (asLong == -1L && isOverflow()) {
                    return obj;
                }
                return asLong;
            }
            return asInt;
        }
        if (PyUnicode_Check(obj)) {
            return InteropUtils.getString(obj);
        }
        if (PyFloat_Check(obj)) {
            return PyLong_AsDouble(obj);
        }
        final Object realObject = fakePythonToJava(obj);
        if (realObject != null) {
            return realObject;
        }
        return obj;
    }

    public static String toString(MemorySegment obj) {
        final MemorySegment messageString = PyObject_Str(obj);
        if (messageString.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return null;
        }
        final String result = InteropUtils.getString(messageString);
        Py_DecRef(messageString);
        return result;
    }

    private static boolean isOverflow() {
        final MemorySegment error = PyErr_Occurred();
        if (error.equals(MemorySegment.NULL)) {
            return false;
        }
        if (PyErr_GivenExceptionMatches(error, PyExc_OverflowError()) != 0) {
            PyErr_Clear();
            return true;
        }
        return false;
    }

    private static Object primitiveFromPython(MemorySegment obj, Class<?> target) {
        if (target == int.class || target == byte.class || target == short.class) {
            final int result = PyLong_AsLong(obj);
            if (result == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw new IllegalArgumentException("Could not convert to " + target, PythonException.moveFromPython(true));
            }
            if (target == byte.class) {
                if ((byte)result != result) {
                    throw new IllegalArgumentException("Could not fit " + result + " into a byte");
                }
                return (byte)result;
            }
            if (target == short.class) {
                if ((short)result != result) {
                    throw new IllegalArgumentException("Could not fit " + result + " into a short");
                }
                return (short)result;
            }
            return result;
        }
        if (target == long.class) {
            final long result = PyLong_AsLongLong(obj);
            if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw new IllegalArgumentException("Could not convert to long", PythonException.moveFromPython(true));
            }
            return result;
        }
        if (target == double.class || target == float.class) {
            final double result = PyFloat_AsDouble(obj);
            if (result == -1.0 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw new IllegalArgumentException("Could not convert to " + target, PythonException.moveFromPython(true));
            }
            if (target == float.class) {
                return (float)result;
            }
            return result;
        }
        if (target == boolean.class) {
            return PyObject_IsTrue(obj) != 0;
        }
        if (target == char.class) {
            if (PyUnicode_Check(obj)) {
                final String asString = InteropUtils.getString(obj);
                if (asString == null) {
                    throw new IllegalArgumentException("Could not convert str to String to parse as char", PythonException.moveFromPython(true));
                }
                if (asString.length() != 1) {
                    if (asString.length() == 2 && Character.isSurrogatePair(asString.charAt(0), asString.charAt(1))) {
                        throw new IllegalArgumentException("Could not fit " + asString + " into a char");
                    }
                    throw new IllegalArgumentException("str must be one character long to be interpreted as char");
                }
                return asString.charAt(0);
            } else {
                final int result = PyLong_AsLong(obj);
                if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                    throw new IllegalArgumentException("Could not convert to char", PythonException.moveFromPython(true));
                }
                if ((char)result != result) {
                    throw new IllegalArgumentException("Could not fit " + result + " into a char");
                }
                return (char)result;
            }
        }
        throw new AssertionError("primitiveFromPython called with non-primitive target " + target);
    }

    private static Object fakePythonToJava(MemorySegment obj) {
        final int isInstance = PyObject_IsInstance(obj, InteropPythonObjects.FAKE_JAVA_OBJECT.get());
        return switch (isInstance) {
            case 0 -> null;
            default -> {
                final MemorySegment idObj = PyObject_GetAttrString(obj, ID_FIELD);
                if (idObj.equals(MemorySegment.NULL)) {
                    throw new IllegalArgumentException(
                        "Could not get _id field of FakeJavaObject",
                        PythonException.moveFromPython(true)
                    );
                }
                final int id = PyLong_AsLong(idObj);
                if (id == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                    throw new IllegalArgumentException(
                        "Could not read _id field of FakeJavaObject",
                        PythonException.moveFromPython(true)
                    );
                }
                yield JavaObjectIndex.OBJECTS.get(id);
            }
            case -1 -> throw new IllegalArgumentException(
                "Could not check isinstance(FakeJavaObject)",
                PythonException.moveFromPython(true)
            );
        };
    }

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

        final int objectId = JavaObjectIndex.OBJECTS.getId(obj);
        final int classId = JavaObjectIndex.getClassId(obj.getClass());
        return InteropPythonObjects.createFakeJavaObject(
            objectId,
            createPythonString(obj.getClass().getName()),
            classId
        );
    }

    private static MemorySegment primitiveToPython(Object obj) {
        return switch (obj) {
            case Integer i -> PyLong_FromLong(i);
            case Long l -> PyLong_FromLongLong(l);
            case Float f -> PyFloat_FromDouble(f);
            case Double d -> PyFloat_FromDouble(d);
            case Boolean b -> b ? _Py_TrueStruct() : _Py_FalseStruct();
            case Character c -> createPythonString(Character.toString(c));
            case Byte b -> PyLong_FromLong(b);
            case Short s -> PyLong_FromLong(s);
            default -> throw new AssertionError("primitiveToPython called with non-primitive source " + obj.getClass());
        };
    }

    public static MemorySegment createPythonString(String javaString) {
        try (Arena arena = Arena.ofConfined()) {
            return PyUnicode_FromString(arena.allocateFrom(javaString));
        }
    }
}
