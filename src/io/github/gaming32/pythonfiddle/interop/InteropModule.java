package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonFunction REMOVE_CLASS = new CustomPythonFunction("remove_class", InteropModule::removeClass);
    public static final CustomPythonFunction FIND_CLASS_ATTRIBUTE = new CustomPythonFunction("find_class_attribute", InteropModule::findClassAttribute);
    public static final CustomPythonFunction GET_STATIC_FIELD = new CustomPythonFunction("get_static_field", InteropModule::getStaticField);

    public static final CustomPythonModule MODULE = new CustomPythonModule(
        "_java", FIND_CLASS, REMOVE_CLASS, FIND_CLASS_ATTRIBUTE, GET_STATIC_FIELD
    );

    /**
     * {@code find_class(name: str) -> int | None}
     */
    private static MemorySegment findClass(MemorySegment self, MemorySegment... args) {
        InteropUtils.checkArity(args, 1);
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
        InteropUtils.checkArity(args, 1);
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
        InteropUtils.checkArity(args, 3);
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
        InteropUtils.checkArity(args, 1);
        final Integer fieldId = InteropUtils.getInt(args[0]);
        if (fieldId == null) {
            return MemorySegment.NULL;
        }
        final FieldOrExecutable.FieldWrapper field = JavaObjectIndex.getStaticField(fieldId);
        if (field == null) {
            return InteropUtils.raiseException(PyExc_SystemError(), "field with id " + fieldId + " doesn't exist");
        }
        try {
            return InteropConversions.javaToPython(field.field().get(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
