package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.PythonException;
import io.github.gaming32.pythonfiddle.TupleUtil;
import io.github.gaming32.pythonfiddle.module.PythonFunction;
import io.github.gaming32.pythonfiddle.module.PythonModule;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import java.util.StringJoiner;

import static io.github.gaming32.pythonfiddle.PythonUtil.*;
import static org.python.Python_h.*;

@PythonModule("_java")
public class InteropModule {
    private static final MemorySegment JAVA_EXCEPTION_FIELD = Arena.global().allocateFrom("java_exception");

    /**
     * {@code find_class(name: str) -> int | None}
     */
    @PythonFunction
    public static MemorySegment findClass(String name) {
        final Integer index = JavaObjectIndex.findClass(name);
        if (index == null) {
            return _Py_NoneStruct();
        }
        return PyLong_FromLong(index);
    }

    /**
     * {@code remove_class(id: int) -> None}
     */
    @PythonFunction
    public static void removeClass(int id) {
        JavaObjectIndex.removeClass(id);
    }

    /**
     * {@code find_class_attribute(owner_name: str, owner_id: int, name: str) -> FakeJavaStaticMethod | int | _JavaAttributeNotFoundType}
     */
    @PythonFunction
    public static MemorySegment findClassAttribute(MemorySegment ownerName, int ownerId, MemorySegment name) {
        if (!PyUnicode_Check(ownerName)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "owner_name must be str");
        }

        final String attrName = InteropUtils.getString(name);
        if (attrName == null) {
            return MemorySegment.NULL;
        }

        return switch (JavaObjectIndex.findClassAttribute(ownerId, attrName)) {
            case null -> InteropPythonObjects.JAVA_ATTRIBUTE_NOT_FOUND.get();
            case FieldOrMethod.MethodWrapper method -> {
                final int id = JavaObjectIndex.STATIC_METHODS.getId(method);
                yield InteropPythonObjects.createFakeJavaStaticMethod(ownerName, name, id);
            }
            case FieldOrMethod.FieldWrapper field -> PyLong_FromLong(JavaObjectIndex.STATIC_FIELDS.getId(field));
        };
    }

    /**
     * {@code invoke_static_method(method_id: int, args: tuple[Any, ...]) -> Any}
     */
    @PythonFunction
    public static MemorySegment invokeStaticMethod(int methodId, MemorySegment args) {
        if (!PyTuple_Check(args)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "expected tuple for args in invoke_static_method");
        }
        final MemorySegment[] argsArray = TupleUtil.unpackTuple(args);
        if (argsArray == null) {
            return MemorySegment.NULL;
        }

        final FieldOrMethod.MethodWrapper method = JavaObjectIndex.STATIC_METHODS.get(methodId);
        if (method == null) {
            return InteropUtils.raiseException(PyExc_SystemError(), "method with id " + methodId + " doesn't exist");
        }
        final MemorySegment result;
        try {
            result = InvokeHandler.invoke(method, null, argsArray);
        } catch (Throwable t) {
            final MemorySegment errorClass = InteropPythonObjects.JAVA_ERROR.get();
            if (errorClass.equals(MemorySegment.NULL)) {
                return MemorySegment.NULL;
            }
            final MemorySegment exception = PyObject_CallOneArg(errorClass, InteropConversions.createPythonString(t.toString()));
            if (exception == null) {
                return MemorySegment.NULL;
            }
            final MemorySegment fakeException = InteropConversions.javaToPython(t);
            if (PyObject_SetAttrString(exception, JAVA_EXCEPTION_FIELD, fakeException) == -1) {
                PyErr_Clear();
            }
            Py_DecRef(fakeException);
            PyErr_SetRaisedException(exception);
            return MemorySegment.NULL;
        }
        if (result == null) {
            final StringJoiner error = new StringJoiner(", ", "no overload matches args (", ")");
            for (final MemorySegment arg : argsArray) {
                error.add(InteropConversions.toString(Py_TYPE(arg)));
            }
            return InteropUtils.raiseException(PyExc_TypeError(), error.toString());
        }
        return result;
    }

    /**
     * {@code get_static_field(field_id: int) -> Any}
     */
    @PythonFunction
    public static MemorySegment getStaticField(int fieldId) {
        final FieldOrMethod.FieldWrapper field = getStaticFieldFromArg(fieldId);
        if (field == null) {
            return MemorySegment.NULL;
        }

        try {
            return InteropConversions.javaToPython(field.field().get(null));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * {@code set_static_field(field_id: int, value: Any) -> None}
     */
    @PythonFunction
    public static MemorySegment setStaticField(int fieldId, MemorySegment value) {
        final FieldOrMethod.FieldWrapper field = getStaticFieldFromArg(fieldId);
        if (field == null) {
            return MemorySegment.NULL;
        }

        try {
            field.field().set(null, InteropConversions.pythonToJava(value, field.field().getType()));
        } catch (IllegalArgumentException e) {
            InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
            if (e.getCause() instanceof PythonException pythonException && pythonException.getOriginalException() != null) {
                final MemorySegment raised = PyErr_GetRaisedException();
                PyException_SetCause(raised, pythonException.acquireOriginalException());
                PyErr_SetRaisedException(raised);
            }
            return MemorySegment.NULL;
        } catch (IllegalAccessException e) {
            return InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
        }
        return _Py_NoneStruct();
    }

    @Nullable
    private static FieldOrMethod.FieldWrapper getStaticFieldFromArg(int fieldId) {
        final FieldOrMethod.FieldWrapper field = JavaObjectIndex.STATIC_FIELDS.get(fieldId);
        if (field == null) {
            InteropUtils.raiseException(PyExc_SystemError(), "field with id " + fieldId + " doesn't exist");
            return null;
        }
        return field;
    }

    /**
     * {@code remove_static_method(id: int) -> None}
     */
    @PythonFunction
    public static void removeStaticMethod(int id) {
        JavaObjectIndex.STATIC_METHODS.remove(id);
    }

    /**
     * {@code remove_static_field(id: int) -> None}
     */
    @PythonFunction
    public static void removeStaticField(int id) {
        JavaObjectIndex.STATIC_FIELDS.remove(id);
    }

    /**
     * {@code reflect_class_object(id: int) -> FakeJavaObject}
     */
    @PythonFunction
    public static Object reflectClassObject(int id) {
        return JavaObjectIndex.getClassById(id);
    }

    /**
     * {@code remove_object(id: int) -> None}
     */
    @PythonFunction
    private static void removeObject(int id) {
        JavaObjectIndex.OBJECTS.remove(id);
    }

    /**
     * {@code to_string(id: int) -> str}
     */
    @PythonFunction
    private static String toString(int id) {
        return Objects.toString(JavaObjectIndex.OBJECTS.get(id));
    }

    /**
     * {@code hash_code(id: int) -> int}
     */
    @PythonFunction
    public static int hashCode(int id) {
        return Objects.hashCode(JavaObjectIndex.OBJECTS.get(id));
    }

    /**
     * {@code identity_string(id: int) -> str}
     */
    @PythonFunction
    public static String identityString(int id) {
        return Objects.toIdentityString(JavaObjectIndex.OBJECTS.get(id));
    }

    /**
     * {@code identity_hash(id: int) -> int}
     */
    @PythonFunction
    public static int identityHash(int id) {
        return System.identityHashCode(JavaObjectIndex.OBJECTS.get(id));
    }
}
