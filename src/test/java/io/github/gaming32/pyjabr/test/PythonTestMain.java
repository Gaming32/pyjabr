package io.github.gaming32.pyjabr.test;

import io.github.gaming32.pyjabr.run.PythonRun;

import java.io.IOException;
import java.nio.file.Path;

public class PythonTestMain {
    public static void main(String[] args) throws IOException {
//        PythonEval.eval(
//            "__import__('threading').Thread(name='a_thread', target=arg.run, daemon=False).start()",
//            Map.of("arg", PythonObject.fromJavaObject((Runnable)() -> {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println(Thread.currentThread());
//            }))
//        );

//        Thread.ofPlatform()
//            .start(() -> PythonEval.eval("print(__import__('threading').current_thread())"))
//            .join();
//        PythonEval.eval("print(__import__('threading').current_thread())");

//        final Runnable action = PythonEval.eval("lambda: print(__import__('threading').current_thread())").asJavaLambda(Runnable.class);
//        Thread.ofPlatform().start(action).join();

        PythonRun.runPath(Path.of("test.py"), "__main__");
    }
}
