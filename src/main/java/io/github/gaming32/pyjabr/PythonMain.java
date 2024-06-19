package io.github.gaming32.pyjabr;

import io.github.gaming32.pyjabr.interop.InteropModule;
import io.github.gaming32.pyjabr.lowlevel.PythonSystem;
import io.github.gaming32.pyjabr.module.CustomPythonModule;
import io.github.gaming32.pyjabr.object.PythonException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonMain {
    public static void main(String[] args) throws IOException, IllegalAccessException, InterruptedException {
        PythonVersion.checkAndLog();

        CustomPythonModule.fromClass(InteropModule.class).registerAsBuiltin(Arena.global());

        Py_InitializeEx(0);
        final MemorySegment save = PyEval_SaveThread();
        try {
            runResource("java_api.py", "java_api");

//            PythonEval.eval(
//                "__import__('threading').Thread(name='a_thread', target=arg.run).start()",
//                Map.of("arg", PythonObject.fromJavaObject((Runnable)() -> {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println(Thread.currentThread());
//                }))
//            );

//            Thread.ofPlatform()
//                .start(() -> PythonEval.eval("print(__import__('threading').current_thread())"))
//                .join();
//            PythonEval.eval("print(__import__('threading').current_thread())");

//            final Runnable action = PythonEval.eval("lambda: print(__import__('threading').current_thread())").asJavaLambda(Runnable.class);
//            Thread.ofPlatform().start(action).join();

            runPath(Path.of("test.py"), "__main__");
        } finally {
            PyEval_RestoreThread(save);
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
