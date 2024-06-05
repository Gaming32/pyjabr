// Generated by jextract

package org.python;

import java.lang.invoke.*;
import java.lang.foreign.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.foreign.ValueLayout.*;
import static java.lang.foreign.MemoryLayout.PathElement.*;

/**
 * {@snippet lang=c :
 * typedef int (*PyDict_WatchCallback)(PyDict_WatchEvent, PyObject *, PyObject *, PyObject *)
 * }
 */
public class PyDict_WatchCallback {

    PyDict_WatchCallback() {
        // Should not be called directly
    }

    /**
     * The function pointer signature, expressed as a functional interface
     */
    public interface Function {
        int apply(int event, MemorySegment dict, MemorySegment key, MemorySegment new_value);
    }

    private static final FunctionDescriptor $DESC = FunctionDescriptor.of(
        Python_h.C_INT,
        Python_h.C_INT,
        Python_h.C_POINTER,
        Python_h.C_POINTER,
        Python_h.C_POINTER
    );

    /**
     * The descriptor of this function pointer
     */
    public static FunctionDescriptor descriptor() {
        return $DESC;
    }

    private static final MethodHandle UP$MH = Python_h.upcallHandle(PyDict_WatchCallback.Function.class, "apply", $DESC);

    /**
     * Allocates a new upcall stub, whose implementation is defined by {@code fi}.
     * The lifetime of the returned segment is managed by {@code arena}
     */
    public static MemorySegment allocate(PyDict_WatchCallback.Function fi, Arena arena) {
        return Linker.nativeLinker().upcallStub(UP$MH.bindTo(fi), $DESC, arena);
    }

    private static final MethodHandle DOWN$MH = Linker.nativeLinker().downcallHandle($DESC);

    /**
     * Invoke the upcall stub {@code funcPtr}, with given parameters
     */
    public static int invoke(MemorySegment funcPtr,int event, MemorySegment dict, MemorySegment key, MemorySegment new_value) {
        try {
            return (int) DOWN$MH.invokeExact(funcPtr, event, dict, key, new_value);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
}

