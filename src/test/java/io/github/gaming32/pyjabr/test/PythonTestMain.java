package io.github.gaming32.pyjabr.test;

import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.run.PythonEval;
import io.github.gaming32.pyjabr.run.PythonExec;

import java.util.Map;

public class PythonTestMain {
    public static void main(String[] args) throws Exception {
        PythonEval.eval(
            "__import__('threading').Thread(name='a_thread', target=arg.run, daemon=False).start()",
            Map.of("arg", PythonObject.fromJavaObject((Runnable)() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread());
            }))
        );

        Thread.ofPlatform()
            .start(() -> PythonEval.eval("print(__import__('threading').current_thread(), flush=True)"))
            .join();
        PythonEval.eval("print(__import__('threading').current_thread(), flush=True)");

        final Runnable action = PythonEval.eval("lambda: print(__import__('threading').current_thread(), flush=True)")
            .asJavaLambda(Runnable.class);
        Thread.ofPlatform().start(action).join();

        PythonExec.execResource("interop_test.py");
    }
}
