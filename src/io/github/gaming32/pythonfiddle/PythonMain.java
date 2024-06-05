package io.github.gaming32.pythonfiddle;

import io.github.gaming32.pythonfiddle.interop.InteropModule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.python.Python_h.*;

public class PythonMain {
    public static void main(String[] args) throws IOException {
        InteropModule.MODULE.registerAsBuiltin(Arena.global());

        Py_Initialize();
        try {
            runResource("_java_init.py", "_java_init");
            runPath(Path.of("test.py"), "test");
        } finally {
            Py_Finalize();
        }
    }

    public static void runResource(String resourceName, String moduleName) throws IOException {
        final byte[] source;
        try (InputStream resource = PythonMain.class.getClassLoader().getResourceAsStream(resourceName)) {
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
        final MemorySegment code;
        final MemorySegment mainModule;
        try (Arena arena = Arena.ofConfined()) {
            code = Py_CompileString(
                arena.allocateFrom(C_CHAR, Arrays.copyOf(source, source.length + 1)),
                arena.allocateFrom(filename),
                Py_file_input()
            );
            if (code.equals(MemorySegment.NULL)) {
                rethrowPythonException();
            }

            mainModule = PyImport_AddModule(arena.allocateFrom(moduleName));
        }
        if (mainModule.equals(MemorySegment.NULL)) {
            Py_DecRef(code);
            rethrowPythonException();
        }

        final MemorySegment dict = PyModule_GetDict(mainModule);
        if (dict.equals(MemorySegment.NULL)) {
            Py_DecRef(code);
            rethrowPythonException();
        }

        final MemorySegment result = PyEval_EvalCode(code, dict, dict);
        Py_DecRef(code);
        if (result.equals(MemorySegment.NULL)) {
            rethrowPythonException();
        }
        Py_DecRef(result);
    }

    public static void rethrowPythonException() {
        final MemorySegment exception = PyErr_GetRaisedException();
        if (exception.equals(MemorySegment.NULL)) {
            throw new IllegalStateException("rethrowPythonException called without exception");
        }
        final PythonException toThrow = PythonException.of(exception);
        Py_DecRef(exception);
        throw toThrow;
    }
}
