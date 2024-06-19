package io.github.gaming32.pyjabr;

import io.github.gaming32.pyjabr.lowlevel.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyImport_ExecCodeModule;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.Py_CompileString;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h_1.Py_DecRef;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h_2.C_CHAR;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h_2.Py_file_input;

public class PythonRun {
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

    public static void runCode(byte[] source, String filename, String moduleName) {
        PythonSystem.callPython(() -> {
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
