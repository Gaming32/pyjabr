package io.github.gaming32.pyjabr.run;

import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;
import io.github.gaming32.pyjabr.object.PythonException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonRun {
    /**
     * Runs a .py resource file, loaded from the current thread's context class loader.
     */
    public static void runResource(String resourceName, String moduleName) throws IOException {
        final byte[] source;
        try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (resource == null) {
                throw new FileNotFoundException("Missing resource: " + resourceName);
            }
            source = resource.readAllBytes();
        }
        runCode(source, '/' + resourceName, moduleName);
    }

    public static void runPath(Path path, String moduleName) throws IOException {
        runCode(Files.readAllBytes(path), path.toString(), moduleName);
    }

    public static void runString(String code, String fileName, String moduleName) {
        runCode(code.getBytes(StandardCharsets.UTF_8), fileName, moduleName);
    }

    public static void runCode(byte[] source, String filename, String moduleName) {
        GilStateUtil.runPython(() -> {
            final MemorySegment code;
            final MemorySegment result;
            try (Arena arena = Arena.ofConfined()) {
                code = Py_CompileString(
                    arena.allocateFrom(C_CHAR, Arrays.copyOf(source, source.length + 1)),
                    arena.allocateFrom(filename),
                    Py_file_input()
                );
                if (code.equals(MemorySegment.NULL)) {
                    throw PythonException.moveFromPython();
                }

                result = PyImport_ExecCodeModule(arena.allocateFrom(moduleName), code);
            }
            Py_DecRef(code);
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            Py_DecRef(result);
        });
    }
}
