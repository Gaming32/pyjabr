package io.github.gaming32.pyjabr.lowlevel;

import java.util.function.Supplier;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyGILState_Ensure;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyGILState_Release;

public class PythonSystem {
    public static void callPython(Runnable action) {
        final int state = PyGILState_Ensure();
        try {
            action.run();
        } finally {
            PyGILState_Release(state);
        }
    }

    public static <T> T callPython(Supplier<T> action) {
        final int state = PyGILState_Ensure();
        try {
            return action.get();
        } finally {
            PyGILState_Release(state);
        }
    }
}
