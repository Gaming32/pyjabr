package io.github.gaming32.pyjabr.lowlevel;

import io.github.gaming32.pyjabr.PythonSystem;

import java.util.function.Supplier;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyGILState_Ensure;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyGILState_Release;

public class GilStateUtil {
    public static void runPython(Runnable action) {
        PythonSystem.initialize();
        withGIL(action);
    }

    public static void withGIL(Runnable action) {
        final int state = PyGILState_Ensure();
        try {
            action.run();
        } finally {
            PyGILState_Release(state);
        }
    }

    public static <T> T runPython(Supplier<T> action) {
        PythonSystem.initialize();
        return withGIL(action);
    }

    public static <T> T withGIL(Supplier<T> action) {
        final int state = PyGILState_Ensure();
        try {
            return action.get();
        } finally {
            PyGILState_Release(state);
        }
    }
}
