package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonFunction REMOVE_CLASS = new CustomPythonFunction("remove_class", InteropModule::removeClass);
    public static final CustomPythonModule MODULE = new CustomPythonModule("_java", FIND_CLASS, REMOVE_CLASS);

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
}
