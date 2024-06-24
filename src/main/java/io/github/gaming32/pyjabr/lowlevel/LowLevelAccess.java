package io.github.gaming32.pyjabr.lowlevel;

import io.github.gaming32.pyjabr.object.PythonObject;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;

public final class LowLevelAccess {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    // TODO: VarHandle instead of volatile?
    private static volatile PythonObjectAccess pythonObjectAccess;

    private LowLevelAccess() {
    }

    public static PythonObjectAccess pythonObject() {
        ensureInitialized(pythonObjectAccess, PythonObject.class);
        return pythonObjectAccess;
    }

    public static void setPythonObjectAccess(PythonObjectAccess access) {
        pythonObjectAccess = access;
    }

    private static void ensureInitialized(Object guard, Class<?> clazz) {
        if (guard == null) {
            try {
                LOOKUP.ensureInitialized(clazz);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static abstract class PythonObjectAccess {
        public abstract PythonObject steal(MemorySegment raw);

        public abstract PythonObject checkAndSteal(MemorySegment raw);

        /**
         * @return The underlying {@code PyObject*}. The object's refcount is not incremented before being returned.
         */
        public abstract MemorySegment borrow(PythonObject object);

        /**
         * Cleans all {@link PythonObject} instances (that is, decrementing their refcount). Note that any non-immortal
         * objects will likely cause segfaults to operate on after calling this method. This method should only be
         * called when future operations on these objects would cause a segfault anyway (for example, when the Python
         * runtime is finalized).
         */
        public abstract void cleanAllObjects();
    }
}
