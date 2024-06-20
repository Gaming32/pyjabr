package io.github.gaming32.pyjabr.run;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PyImport_ExecCodeModule;

/**
 * Contains methods for injecting Python modules. Note that these methods keep modules loaded permanently. If you just
 * want to run code without injecting a module, use the methods in {@link PythonExec}.
 *
 * @see PythonExec
 */
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
        PythonExec.run(source, filename, code -> {
            try (Arena arena = Arena.ofConfined()) {
                return PyImport_ExecCodeModule(arena.allocateFrom(moduleName), code);
            }
        });
    }
}
