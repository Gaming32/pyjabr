package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;
import io.github.gaming32.pythonfiddle.PythonException;
import io.github.gaming32.pythonfiddle.TupleUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.util.StringJoiner;

import static org.python.Python_h.*;
import static io.github.gaming32.pythonfiddle.PythonUtil.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonFunction REMOVE_CLASS = new CustomPythonFunction("remove_class", InteropModule::removeClass);
    public static final CustomPythonFunction FIND_CLASS_ATTRIBUTE = new CustomPythonFunction("find_class_attribute", InteropModule::findClassAttribute);
    public static final CustomPythonFunction INVOKE_STATIC_METHOD = new CustomPythonFunction("invoke_static_method", InteropModule::invokeStaticMethod);
    public static final CustomPythonFunction GET_STATIC_FIELD = new CustomPythonFunction("get_static_field", InteropModule::getStaticField);
    public static final CustomPythonFunction SET_STATIC_FIELD = new CustomPythonFunction("set_static_field", InteropModule::setStaticField);
    public static final CustomPythonFunction REMOVE_STATIC_METHOD = new CustomPythonFunction("remove_static_method", InteropModule::removeStaticMethod);
    public static final CustomPythonFunction REMOVE_STATIC_FIELD = new CustomPythonFunction("remove_static_field", InteropModule::removeStaticField);

    public static final CustomPythonModule MODULE = new CustomPythonModule(
        "_java",
        FIND_CLASS,
        REMOVE_CLASS,
        FIND_CLASS_ATTRIBUTE,
        INVOKE_STATIC_METHOD,
        GET_STATIC_FIELD,
        SET_STATIC_FIELD,
        REMOVE_STATIC_METHOD,
        REMOVE_STATIC_FIELD
    );

    /**
     * {@code find_class(name: str) -> int | None}
     */
    private static MemorySegment findClass(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }

        final String className = InteropUtils.getString(args[0]);
        if (className == null) {
            return MemorySegment.NULL;
        }

        final Integer index = JavaObjectIndex.findClass(className);
        if (index == null) {
            return _Py_NoneStruct();
        }
        return PyLong_FromLong(index);
    }

    /**
     * {@code remove_class(id: int) -> None}
     */
    private static MemorySegment removeClass(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }

        final Integer classId = InteropUtils.getInt(args[0]);
        if (classId == null) {
            return MemorySegment.NULL;
        }

        JavaObjectIndex.removeClass(classId);
        return _Py_NoneStruct();
    }

    /**
     * {@code find_class_attribute(owner: FakeJavaClass, owner_id: int, name: str) -> FakeJavaStaticMethod | int | _JavaAttributeNotFoundType}
     */
    private static MemorySegment findClassAttribute(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 3)) {
            return MemorySegment.NULL;
        }

        final MemorySegment ownerName = args[0];
        if (!PyUnicode_Check(ownerName)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "owner_name must be str");
        }

        final Integer ownerId = InteropUtils.getInt(args[1]);
        if (ownerId == null) {
            return MemorySegment.NULL;
        }

        final MemorySegment nameObject = args[2];
        final String name = InteropUtils.getString(nameObject);
        if (name == null) {
            return MemorySegment.NULL;
        }

        return switch (JavaObjectIndex.findClassAttribute(ownerId, name)) {
            case null -> InteropPythonObjects.JAVA_ATTRIBUTE_NOT_FOUND.get();
            case FieldOrMethod.MethodWrapper method -> {
                final int id = JavaObjectIndex.STATIC_METHODS.getId(method);
                yield InteropPythonObjects.createFakeJavaStaticMethod(ownerName, nameObject, id);
            }
            case FieldOrMethod.FieldWrapper field -> PyLong_FromLong(JavaObjectIndex.STATIC_FIELDS.getId(field));
        };
    }

    /**
     * {@code invoke_static_method(method_id: int, args: tuple[Any, ...]) -> Any}
     */
    private static MemorySegment invokeStaticMethod(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 2)) {
            return MemorySegment.NULL;
        }

        final Integer methodId = InteropUtils.getInt(args[0]);
        if (methodId == null) {
            return MemorySegment.NULL;
        }

        final MemorySegment argsTuple = args[1];
        if (!PyTuple_Check(argsTuple)) {
            return InteropUtils.raiseException(PyExc_TypeError(), "expected tuple for args in invoke_static_method");
        }
        final MemorySegment[] argsArray = TupleUtil.unpackTuple(argsTuple);
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
            // TODO: Create fake object for Java exception
            final MemorySegment errorClass = InteropPythonObjects.JAVA_ERROR.get();
            if (errorClass.equals(MemorySegment.NULL)) {
                return MemorySegment.NULL;
            }
            final MemorySegment exception = PyObject_CallOneArg(errorClass, InteropConversions.createPythonString(t.toString()));
            if (exception == null) {
                return MemorySegment.NULL;
            }
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
    private static MemorySegment getStaticField(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }

        final FieldOrMethod.FieldWrapper field = getStaticFieldFromArg(args[0]);
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
    private static MemorySegment setStaticField(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 2)) {
            return MemorySegment.NULL;
        }

        final FieldOrMethod.FieldWrapper field = getStaticFieldFromArg(args[0]);
        if (field == null) {
            return MemorySegment.NULL;
        }

        try {
            field.field().set(null, InteropConversions.pythonToJava(args[1], field.field().getType()));
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
        return _PyNone_Type();
    }

    @Nullable
    private static FieldOrMethod.FieldWrapper getStaticFieldFromArg(MemorySegment idArg) {
        final Integer fieldId = InteropUtils.getInt(idArg);
        if (fieldId == null) {
            return null;
        }
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
    private static MemorySegment removeStaticMethod(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }

        final Integer methodId = InteropUtils.getInt(args[0]);
        if (methodId == null) {
            return MemorySegment.NULL;
        }

        JavaObjectIndex.STATIC_METHODS.remove(methodId);
        return _Py_NoneStruct();
    }

    /**
     * {@code remove_static_field(id: int) -> None}
     */
    private static MemorySegment removeStaticField(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }

        final Integer fieldId = InteropUtils.getInt(args[0]);
        if (fieldId == null) {
            return MemorySegment.NULL;
        }

        JavaObjectIndex.STATIC_FIELDS.remove(fieldId);
        return _Py_NoneStruct();
    }
}
