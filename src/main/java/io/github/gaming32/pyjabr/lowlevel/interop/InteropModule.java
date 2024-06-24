package io.github.gaming32.pyjabr.lowlevel.interop;

import io.github.gaming32.pyjabr.lowlevel.LowLevelAccess;
import io.github.gaming32.pyjabr.lowlevel.TupleUtil;
import io.github.gaming32.pyjabr.lowlevel.module.PythonFunction;
import io.github.gaming32.pyjabr.lowlevel.module.PythonModule;
import io.github.gaming32.pyjabr.object.PythonException;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.StringJoiner;

import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.*;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

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
     * {@code remove_class(class_id: int) -> None}
     */
    @PythonFunction
    public static void removeClass(int classId) {
        JavaObjectIndex.removeClass(classId);
    }

    /**
     * {@code find_class_attribute(
     *     owner_name: str,
     *     owner_id: int,
     *     name: str,
     *     is_static: bool
     * ) -> FakeJavaMethod | FakeJavaClass | int | _JavaAttributeNotFoundType}
     */
    @PythonFunction
    public static MemorySegment findClassAttribute(MemorySegment ownerName, int ownerId, MemorySegment name, int isStatic) {
        if (!PyUnicode_Check(ownerName)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "owner_name must be str");
        }

        final String attrName = InteropUtils.getString(name);
        if (attrName == null) {
            return MemorySegment.NULL;
        }

        return switch (JavaObjectIndex.findClassAttribute(ownerId, attrName, isStatic != 0)) {
            case null -> {
                if (isStatic != 0) {
                    final String owner = InteropUtils.getString(ownerName);
                    if (owner == null) {
                        yield MemorySegment.NULL;
                    }
                    final String innerClassName = owner + '$' + attrName;
                    final Integer index = JavaObjectIndex.findClass(innerClassName);
                    if (index != null) {
                        yield InteropPythonObjects.createFakeJavaClass(
                            InteropConversions.createPythonString(innerClassName), index
                        );
                    }
                }
                yield InteropPythonObjects.JAVA_ATTRIBUTE_NOT_FOUND.get();
            }
            case FieldOrMethod.FieldWrapper field -> PyLong_FromLong(JavaObjectIndex.FIELDS.getId(field));
            case FieldOrMethod.MethodWrapper method -> {
                final int id = JavaObjectIndex.METHODS.getId(method);
                yield InteropPythonObjects.createFakeJavaMethod(ownerName, name, id);
            }
        };
    }

    /**
     * {@code invoke_static_method(method_id: int, args: tuple[Any, ...]) -> Any}
     */
    @PythonFunction
    public static MemorySegment invokeStaticMethod(int methodId, MemorySegment args) {
        return invokeMethod(null, methodId, args);
    }

    /**
     * {@code invoke_instance_method(object_id: int, method_id: int, args: tuple[Any, ...]) -> Any}
     */
    @PythonFunction
    public static MemorySegment invokeInstanceMethod(int objectId, int methodId, MemorySegment args) {
        final Object object = getObject(objectId);
        if (object == null) {
            return MemorySegment.NULL;
        }
        return invokeMethod(object, methodId, args);
    }

    private static MemorySegment invokeMethod(Object owner, int methodId, MemorySegment args) {
        final FieldOrMethod.MethodWrapper method = JavaObjectIndex.METHODS.get(methodId);
        if (method == null) {
            return InteropUtils.raiseException(PyExc_SystemError(), "method with id " + methodId + " doesn't exist");
        }

        if (!PyTuple_Check(args)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "expected tuple for args in invoke_method");
        }
        final MemorySegment[] argsArray = TupleUtil.unpackTuple(args);
        if (argsArray == null) {
            return MemorySegment.NULL;
        }

        final MemorySegment result;
        try {
            result = InvokeHandler.invoke(method, owner, argsArray);
        } catch (InvocationTargetException e) {
            return raiseJavaError(e.getCause());
        } catch (IllegalAccessException e) {
            return InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
        } catch (NullPointerException e) {
            return raiseNotStatic(method);
        }
        if (result == null) {
            final StringJoiner error = new StringJoiner(", ", "no overload matches args (", ")");
            for (final MemorySegment arg : argsArray) {
                error.add(InteropConversions.repr(arg));
            }
            return InteropUtils.raiseException(PyExc_TypeError(), error.toString());
        }
        return result;
    }

    @SuppressWarnings("SameReturnValue")
    private static MemorySegment raiseJavaError(Throwable t) {
        final MemorySegment errorClass = InteropPythonObjects.JAVA_ERROR.get();
        if (errorClass.equals(MemorySegment.NULL)) {
            return MemorySegment.NULL;
        }
        final MemorySegment exception = PyObject_CallOneArg(
            errorClass, InteropConversions.createPythonString(t.toString())
        );
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

    /**
     * {@code get_static_field(field_id: int) -> Any}
     */
    @PythonFunction
    public static MemorySegment getStaticField(int fieldId) {
        return getField(null, fieldId);
    }

    /**
     * {@code get_instance_field(object_id: int, field_id: int) -> Any}
     */
    @PythonFunction
    public static MemorySegment getInstanceField(int objectId, int fieldId) {
        final Object object = getObject(objectId);
        if (object == null) {
            return MemorySegment.NULL;
        }
        return getField(object, fieldId);
    }

    private static MemorySegment getField(Object owner, int fieldId) {
        final FieldOrMethod.FieldWrapper field = getFieldFromArg(fieldId);
        if (field == null) {
            return MemorySegment.NULL;
        }

        final Object fieldValue;
        try {
            fieldValue = field.field().get(owner);
        } catch (NullPointerException e) {
            return raiseNotStatic(field);
        } catch (IllegalAccessException e) {
            return InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
        }
        return InteropConversions.javaToPython(fieldValue);
    }

    /**
     * {@code set_static_field(field_id: int, value: Any) -> None}
     */
    @PythonFunction
    public static MemorySegment setStaticField(int fieldId, MemorySegment value) {
        return setField(null, fieldId, value);
    }

    /**
     * {@code set_instance_field(object_id: int, field_id: int, value: Any) -> None}
     */
    @PythonFunction
    public static MemorySegment setInstanceField(int objectId, int fieldId, MemorySegment value) {
        final Object object = getObject(objectId);
        if (object == null) {
            return MemorySegment.NULL;
        }
        return setField(object, fieldId, value);
    }

    private static MemorySegment setField(Object owner, int fieldId, MemorySegment value) {
        final FieldOrMethod.FieldWrapper field = getFieldFromArg(fieldId);
        if (field == null) {
            return MemorySegment.NULL;
        }

        try {
            field.field().set(owner, InteropConversions.pythonToJava(value, field.field().getType()));
        } catch (IllegalArgumentException e) {
            InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
            if (e.getCause() instanceof PythonException pythonException && pythonException.getOriginalException() != null) {
                final MemorySegment raised = PyErr_GetRaisedException();
                PyException_SetCause(raised, LowLevelAccess.pythonObject().borrow(pythonException.getOriginalException()));
                PyErr_SetRaisedException(raised);
            }
            return MemorySegment.NULL;
        } catch (IllegalAccessException e) {
            return InteropUtils.raiseException(PyExc_TypeError(), e.getMessage());
        } catch (NullPointerException e) {
            return raiseNotStatic(field);
        }
        return _Py_NoneStruct();
    }

    @Nullable
    private static FieldOrMethod.FieldWrapper getFieldFromArg(int fieldId) {
        final FieldOrMethod.FieldWrapper field = JavaObjectIndex.FIELDS.get(fieldId);
        if (field == null) {
            InteropUtils.raiseException(PyExc_SystemError(), "field with id " + fieldId + " doesn't exist");
            return null;
        }
        return field;
    }

    private static Object getObject(int objectId) {
        final Object object = JavaObjectIndex.OBJECTS.get(objectId);
        if (object == null) {
            InteropUtils.raiseException(PyExc_SystemError(), "instance with id " + objectId + " doesn't exist");
        }
        return object;
    }

    private static MemorySegment raiseNotStatic(FieldOrMethod member) {
        final String type = switch (member) {
            case FieldOrMethod.FieldWrapper _ -> "field";
            case FieldOrMethod.MethodWrapper _ -> "method";
        };
        return InteropUtils.raiseException(
            PyExc_TypeError(),
            type + ' ' + member.owner().getName() + '.' + member.name() + " is not static"
        );
    }

    /**
     * {@code remove_method(method_id: int) -> None}
     */
    @PythonFunction
    public static void removeMethod(int methodId) {
        JavaObjectIndex.METHODS.remove(methodId);
    }

    /**
     * {@code remove_field(field_id: int) -> None}
     */
    @PythonFunction
    public static void removeField(int fieldId) {
        JavaObjectIndex.FIELDS.remove(fieldId);
    }

    /**
     * {@code reflect_class_object(class_id: int) -> FakeJavaObject}
     */
    @PythonFunction
    public static Object reflectClassObject(int classId) {
        return JavaObjectIndex.getClassById(classId);
    }

    /**
     * {@code remove_object(object_id: int) -> None}
     */
    @PythonFunction
    private static void removeObject(int objectId) {
        JavaObjectIndex.OBJECTS.remove(objectId);
    }

    /**
     * {@code to_string(object_id: int) -> str}
     */
    @PythonFunction
    private static String toString(int objectId) {
        return Objects.toString(JavaObjectIndex.OBJECTS.get(objectId));
    }

    /**
     * {@code hash_code(object_id: int) -> int}
     */
    @PythonFunction
    public static int hashCode(int objectId) {
        return Objects.hashCode(JavaObjectIndex.OBJECTS.get(objectId));
    }

    /**
     * {@code identity_string(object_id: int) -> str}
     */
    @PythonFunction
    public static String identityString(int objectId) {
        return Objects.toIdentityString(JavaObjectIndex.OBJECTS.get(objectId));
    }

    /**
     * {@code identity_hash(object_id: int) -> int}
     */
    @PythonFunction
    public static int identityHash(int objectId) {
        return System.identityHashCode(JavaObjectIndex.OBJECTS.get(objectId));
    }

    @PythonFunction
    public static MemorySegment makeLambda(int classId, MemorySegment action) {
        Py_IncRef(action);
        try {
            return InteropConversions.javaToPython(LambdaMaker.makeLambda(JavaObjectIndex.getClassById(classId), action));
        } catch (Throwable t) {
            Py_DecRef(action);
            return raiseJavaError(t);
        }
    }
}
