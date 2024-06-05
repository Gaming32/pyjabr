package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;
import io.github.gaming32.pythonfiddle.PythonException;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonFunction REMOVE_CLASS = new CustomPythonFunction("remove_class", InteropModule::removeClass);
    public static final CustomPythonFunction FIND_CLASS_ATTRIBUTE = new CustomPythonFunction("find_class_attribute", InteropModule::findClassAttribute);
    public static final CustomPythonFunction GET_STATIC_FIELD = new CustomPythonFunction("get_static_field", InteropModule::getStaticField);
    public static final CustomPythonFunction SET_STATIC_FIELD = new CustomPythonFunction("set_static_field", InteropModule::setStaticField);
    public static final CustomPythonFunction REMOVE_STATIC_METHOD = new CustomPythonFunction("remove_static_method", InteropModule::removeStaticMethod);
    public static final CustomPythonFunction REMOVE_STATIC_FIELD = new CustomPythonFunction("remove_static_field", InteropModule::removeStaticField);

    public static final CustomPythonModule MODULE = new CustomPythonModule(
        "_java",
        FIND_CLASS,
        REMOVE_CLASS,
        FIND_CLASS_ATTRIBUTE,
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
        final MemorySegment owner = args[0];
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
            case FieldOrExecutable.ExecutablesWrapper executables -> {
                final int id = JavaObjectIndex.getStaticExecutablesId(executables);
                yield InteropPythonObjects.createFakeJavaStaticMethod(owner, nameObject, id);
            }
            case FieldOrExecutable.FieldWrapper field -> PyLong_FromLong(JavaObjectIndex.getStaticFieldId(field));
        };
    }

    /**
     * {@code get_static_field(field_id: int) -> Any}
     */
    private static MemorySegment getStaticField(MemorySegment self, MemorySegment... args) {
        if (!InteropUtils.checkArity(args, 1)) {
            return MemorySegment.NULL;
        }
        final FieldOrExecutable.FieldWrapper field = getStaticFieldFromArg(args[0]);
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
        final FieldOrExecutable.FieldWrapper field = getStaticFieldFromArg(args[0]);
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
    private static FieldOrExecutable.FieldWrapper getStaticFieldFromArg(MemorySegment idArg) {
        final Integer fieldId = InteropUtils.getInt(idArg);
        if (fieldId == null) {
            return null;
        }
        final FieldOrExecutable.FieldWrapper field = JavaObjectIndex.getStaticField(fieldId);
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
        JavaObjectIndex.removeStaticExecutables(methodId);
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
        JavaObjectIndex.removeStaticField(fieldId);
        return _Py_NoneStruct();
    }
}
