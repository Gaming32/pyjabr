package io.github.gaming32.pyjabr.interop;

import com.google.common.primitives.Primitives;
import io.github.gaming32.pyjabr.python.PythonException;
import io.github.gaming32.pyjabr.python.PythonObject;
import org.python.Python_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.function.Function;

import static io.github.gaming32.pyjabr.PythonUtil.*;
import static org.python.Python_h.*;

public class InteropConversions {
    private static final MemorySegment ID_FIELD = Arena.global().allocateFrom("_id");

    public static Object pythonToJava(MemorySegment obj, Class<?> target) throws IllegalArgumentException {
        return pythonToJava(obj, target, true);
    }

    public static Object pythonToJava(MemorySegment obj) throws IllegalArgumentException {
        return pythonToJava(obj, true);
    }

    static Object pythonToJava(MemorySegment obj, Class<?> target, boolean throwDetails) throws IllegalArgumentException {
        if (target == Object.class) {
            return pythonToJava(obj, throwDetails);
        }
        if (target == Void.class) {
            if (!obj.equals(_Py_NoneStruct())) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Only None can be assigned to Void");
            }
            return obj;
        }
        if (obj.equals(_Py_NoneStruct())) {
            if (target.isPrimitive()) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Cannot pass None to primitive type " + target);
            }
            return null;
        }
        if (target.isPrimitive() || Primitives.isWrapperType(target)) {
            return primitiveFromPython(obj, Primitives.unwrap(target), throwDetails);
        }
        if (target == String.class || target == CharSequence.class) {
            if (PyUnicode_Check(obj)) {
                final String result = InteropUtils.getString(obj);
                if (result == null) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Could not convert to String", PythonException.moveFromPython());
                }
                return result;
            } else if (target == String.class) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Cannot convert " + toString(Py_TYPE(obj)) + " to String");
            }
        }
        if (target == PythonObject.class) {
            Py_IncRef(obj);
            return PythonObject.steal(obj);
        }
        if (target == Class.class) {
            final Class<?> realClass = fakePythonClassToJava(obj, throwDetails);
            if (realClass != null) {
                return realClass;
            }
        }
        final Object realObject = fakePythonObjectToJava(obj, throwDetails);
        if (target.isInstance(realObject)) {
            return realObject;
        }
        NoDetailsConversionFailed.maybeThrow(throwDetails);
        throw new IllegalArgumentException("Cannot convert " + toString(Py_TYPE(obj)) + " to " + target);
    }

    static Object pythonToJava(MemorySegment obj, boolean throwDetails) {
        if (obj.equals(_Py_NoneStruct())) {
            return null;
        }
        if (PyBool_Check(obj)) {
            return obj.equals(_Py_TrueStruct());
        }
        if (PyLong_Check(obj)) {
            final int asInt = PyLong_AsLong(obj);
            if (asInt == -1) {
                if (isOverflow()) {
                    final long asLong = PyLong_AsLongLong(obj);
                    if (asLong != -1L) {
                        return asLong;
                    }
                    if (!isOverflow()) {
                        if (!PyErr_Occurred().equals(MemorySegment.NULL)) {
                            NoDetailsConversionFailed.maybeThrow(throwDetails);
                            throw new IllegalArgumentException("Failed to convert to long", PythonException.moveFromPython());
                        }
                        return asLong;
                    }
                } else if (!PyErr_Occurred().equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Failed to convert to int", PythonException.moveFromPython());
                }
            } else {
                return asInt;
            }
        }
        if (PyUnicode_Check(obj)) {
            final String asString = InteropUtils.getString(obj);
            if (asString == null) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Failed to convert to str", PythonException.moveFromPython());
            }
            return asString;
        }
        if (PyFloat_Check(obj)) {
            return PyFloat_AsDouble(obj);
        }
        final Object realObject = fakePythonObjectToJava(obj, throwDetails);
        if (realObject != null) {
            return realObject;
        }
        final Class<?> realClass = fakePythonClassToJava(obj, throwDetails);
        if (realClass != null) {
            return realClass;
        }
        Py_IncRef(obj);
        return PythonObject.steal(obj);
    }

    public static String toString(MemorySegment obj) {
        return toString(obj, Python_h::PyObject_Str);
    }

    public static String repr(MemorySegment obj) {
        return toString(obj, Python_h::PyObject_Repr);
    }

    private static String toString(MemorySegment obj, Function<MemorySegment, MemorySegment> stringifier) {
        final MemorySegment resultStr = stringifier.apply(obj);
        if (resultStr.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return null;
        }
        final String result = InteropUtils.getString(resultStr);
        Py_DecRef(resultStr);
        if (result == null) {
            PyErr_Clear();
            return null;
        }
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

    private static Object primitiveFromPython(MemorySegment obj, Class<?> target, boolean throwDetails) {
        if (target == int.class || target == byte.class || target == short.class) {
            final int result = PyLong_AsLong(obj);
            if (result == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Could not convert to " + target, PythonException.moveFromPython());
            }
            if (target == byte.class) {
                if ((byte)result != result) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Could not fit " + result + " into a byte");
                }
                return (byte)result;
            }
            if (target == short.class) {
                if ((short)result != result) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Could not fit " + result + " into a short");
                }
                return (short)result;
            }
            return result;
        }
        if (target == long.class) {
            final long result = PyLong_AsLongLong(obj);
            if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Could not convert to long", PythonException.moveFromPython());
            }
            return result;
        }
        if (target == double.class || target == float.class) {
            final double result = PyFloat_AsDouble(obj);
            if (result == -1.0 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException("Could not convert to " + target, PythonException.moveFromPython());
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
                    throw new IllegalArgumentException("Could not convert str to String to parse as char", PythonException.moveFromPython());
                }
                if (asString.length() != 1) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    if (asString.length() == 2 && Character.isSurrogatePair(asString.charAt(0), asString.charAt(1))) {
                        throw new IllegalArgumentException("Could not fit " + asString + " into a char");
                    }
                    throw new IllegalArgumentException("str must be one character long to be interpreted as char");
                }
                return asString.charAt(0);
            } else {
                final int result = PyLong_AsLong(obj);
                if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Could not convert to char", PythonException.moveFromPython());
                }
                if ((char)result != result) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException("Could not fit " + result + " into a char");
                }
                return (char)result;
            }
        }
        throw new AssertionError("primitiveFromPython called with non-primitive target " + target);
    }

    private static Class<?> fakePythonClassToJava(MemorySegment obj, boolean throwDetails) {
        final int isInstance = PyObject_IsInstance(obj, InteropPythonObjects.FAKE_JAVA_CLASS.get());
        return switch (isInstance) {
            case 0 -> null;
            default -> {
                final MemorySegment idObj = PyObject_GetAttrString(obj, ID_FIELD);
                if (idObj.equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException(
                        "Could not get _id field of FakeJavaClass",
                        PythonException.moveFromPython()
                    );
                }
                final int id = PyLong_AsLong(idObj);
                if (id == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException(
                        "Could not read _id field of FakeJavaClass",
                        PythonException.moveFromPython()
                    );
                }
                yield JavaObjectIndex.getClassById(id);
            }
            case -1 -> {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException(
                    "Could not check isinstance(FakeJavaClass)",
                    PythonException.moveFromPython()
                );
            }
        };
    }

    private static Object fakePythonObjectToJava(MemorySegment obj, boolean throwDetails) {
        final int isInstance = PyObject_IsInstance(obj, InteropPythonObjects.FAKE_JAVA_OBJECT.get());
        return switch (isInstance) {
            case 0 -> null;
            default -> {
                final MemorySegment idObj = PyObject_GetAttrString(obj, ID_FIELD);
                if (idObj.equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException(
                        "Could not get _id field of FakeJavaObject",
                        PythonException.moveFromPython()
                    );
                }
                final int id = PyLong_AsLong(idObj);
                if (id == -1 && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                    NoDetailsConversionFailed.maybeThrow(throwDetails);
                    throw new IllegalArgumentException(
                        "Could not read _id field of FakeJavaObject",
                        PythonException.moveFromPython()
                    );
                }
                yield JavaObjectIndex.OBJECTS.get(id);
            }
            case -1 -> {
                NoDetailsConversionFailed.maybeThrow(throwDetails);
                throw new IllegalArgumentException(
                    "Could not check isinstance(FakeJavaObject)",
                    PythonException.moveFromPython()
                );
            }
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
        if (obj instanceof PythonObject pyObj) {
            final MemorySegment result = pyObj.borrow();
            Py_IncRef(result);
            return result;
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

    @SuppressWarnings("unchecked")
    public static <T> T createLambda(Class<T> lambdaClass, MemorySegment action) {
        Py_IncRef(action);
        try {
            return (T)LambdaMaker.makeLambda(lambdaClass, action);
        } catch (Throwable t) {
            Py_DecRef(action);
            throw new RuntimeException(t);
        }
    }

    static final class NoDetailsConversionFailed extends IllegalArgumentException {
        private static final NoDetailsConversionFailed INSTANCE = new NoDetailsConversionFailed();

        private NoDetailsConversionFailed() {
        }

        public static void maybeThrow(boolean throwDetails) {
            if (!throwDetails) {
                PyErr_Clear();
                throw INSTANCE;
            }
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
