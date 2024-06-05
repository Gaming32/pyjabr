package io.github.gaming32.pythonfiddle.interop;

import io.github.gaming32.pythonfiddle.CustomPythonFunction;
import io.github.gaming32.pythonfiddle.CustomPythonModule;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.*;

public class InteropModule {
    public static final CustomPythonFunction FIND_CLASS = new CustomPythonFunction("find_class", InteropModule::findClass);
    public static final CustomPythonModule MODULE = new CustomPythonModule("_java", FIND_CLASS);

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
}
