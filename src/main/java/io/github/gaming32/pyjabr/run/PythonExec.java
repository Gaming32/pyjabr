package io.github.gaming32.pyjabr.run;

import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.PyDict_Check;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonExec {
    /**
     * Runs a .py resource file, loaded from the current thread's context class loader.
     */
    public static void execResource(String resourceName) throws IOException {
        execCode(readResource(resourceName), '/' + resourceName);
    }

    /**
     * Runs a .py resource file, loaded from the current thread's context class loader.
     */
    public static void execResource(String resourceName, Map<String, PythonObject> globals) throws IOException {
        execCode(readResource(resourceName), '/' + resourceName, globals);
    }

    static byte[] readResource(String resourceName) throws IOException {
        try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (resource == null) {
                throw new FileNotFoundException("Missing resource: " + resourceName);
            }
            return resource.readAllBytes();
        }
    }

    public static void execPath(Path path) throws IOException {
        execCode(Files.readAllBytes(path), path.toString());
    }

    public static void execPath(Path path, Map<String, PythonObject> globals) throws IOException {
        execCode(Files.readAllBytes(path), path.toString(), globals);
    }

    public static void execString(String code, String fileName) {
        execCode(code.getBytes(StandardCharsets.UTF_8), fileName);
    }

    public static void execString(String code, String fileName, Map<String, PythonObject> globals) {
        execCode(code.getBytes(StandardCharsets.UTF_8), fileName, globals);
    }

    public static void execCode(byte[] source, String filename) {
        execCode(source, filename, Map.of(
            "__builtins__", PythonObjects.getBuiltins(),
            "__file__", PythonObjects.str(filename)
        ));
    }

    public static void execCode(byte[] source, String filename, Map<String, PythonObject> globals) {
        execCode(source, filename, PythonObjects.stringDict(globals));
    }

    public static void execCode(byte[] source, String filename, PythonObject globals) {
        execCode(source, filename, globals, globals);
    }

    public static void execCode(byte[] source, String filename, PythonObject globals, PythonObject locals) {
        run(source, filename, code -> {
            checkGlobalsLocals(globals, locals);
            return PyEval_EvalCode(code, globals.borrow(), locals.borrow());
        });
    }

    static void checkGlobalsLocals(PythonObject globals, PythonObject locals) {
        if (!PyDict_Check(globals.borrow())) {
            throw new IllegalArgumentException("globals must be a dict");
        }
        if (PyMapping_Check(locals.borrow()) == 0) {
            throw new IllegalArgumentException("locals must be a mapping");
        }
    }

    static void run(byte[] source, String filename, Function<MemorySegment, MemorySegment> runner) {
        GilStateUtil.runPython(() -> {
            final MemorySegment code;
            try (Arena arena = Arena.ofConfined()) {
                code = Py_CompileString(
                    arena.allocateFrom(C_CHAR, Arrays.copyOf(source, source.length + 1)),
                    arena.allocateFrom(filename),
                    Py_file_input()
                );
                if (code.equals(MemorySegment.NULL)) {
                    throw PythonException.moveFromPython();
                }
            }
            final MemorySegment result = runner.apply(code);
            Py_DecRef(code);
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            Py_DecRef(result);
        });
    }
}
