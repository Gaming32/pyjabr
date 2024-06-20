package io.github.gaming32.pyjabr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.C_INT;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.C_POINTER;

class Dlopen {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dlopen.class);

    static MemorySegment dlopen(String name, int flags) {
        final MemorySegment result;
        try (Arena arena = Arena.ofConfined()) {
            result = callDl(
                "dlopen", C_POINTER,
                C_POINTER, arena.allocateFrom(name),
                C_INT, flags
            );
        }
        if (result.equals(MemorySegment.NULL)) {
            LOGGER.error("dlopen failed: {}", dlerror());
        }
        return result;
    }

    static void dlclose(MemorySegment handle) {
        if (handle.equals(MemorySegment.NULL)) return;
        final int result = callDl(
            "dlclose", C_INT,
            C_POINTER, handle
        );
        if (result != 0) {
            LOGGER.error("dlclose failed: {}", dlerror());
        }
    }

    static String dlerror() {
        final MemorySegment messageSegment = callDl("dlerror", C_POINTER);
        if (messageSegment.equals(MemorySegment.NULL)) {
            return null;
        }
        return messageSegment.getString(0L);
    }

    @SuppressWarnings("unchecked")
    private static <T> T callDl(String name, MemoryLayout returnLayout, Object... args) {
        final MemoryLayout[] argLayouts = new MemoryLayout[args.length / 2];
        final Object[] argValues = new Object[args.length / 2];
        for (int i = 0; i < args.length; i += 2) {
            argLayouts[i / 2] = (MemoryLayout)args[i];
            argValues[i / 2] = args[i + 1];
        }

        final MethodHandle mh = Linker.nativeLinker().downcallHandle(
            Linker.nativeLinker()
                .defaultLookup()
                .find(name)
                .orElseThrow(() -> new IllegalStateException("Couldn't find function " + name)),
            FunctionDescriptor.of(returnLayout, argLayouts)
        );
        try {
            return (T)mh.invokeWithArguments(argValues);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
