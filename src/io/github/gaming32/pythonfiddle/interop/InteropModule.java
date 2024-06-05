package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonFunction REMOVE_CLASS = new CustomPythonFunction("remove_class", InteropModule::removeClass);
    public static final CustomPythonFunction GET_CLASS_ATTRIBUTE = new CustomPythonFunction("get_class_attribute", InteropModule::getClassAttribute);
    public static final CustomPythonModule MODULE = new CustomPythonModule("_java", FIND_CLASS, REMOVE_CLASS, GET_CLASS_ATTRIBUTE);

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

    private static MemorySegment removeClass(MemorySegment self, MemorySegment... args) {
        InteropUtils.checkArity(args, 1);
        final Integer classId = InteropUtils.getInt(args[0]);
        if (classId == null) {
            return MemorySegment.NULL;
        }
        JavaObjectIndex.removeClass(classId);
        return _Py_NoneStruct();
    }

    private static MemorySegment getClassAttribute(MemorySegment self, MemorySegment... args) {
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
            case FieldOrExecutable.FieldWrapper field -> InteropPythonObjects.JAVA_ATTRIBUTE_NOT_FOUND.get();
        };
    }
}
