package io.github.gaming32.pyjabr.test;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;
import io.github.gaming32.pyjabr.run.PythonEval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EvalTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    @Test
    public void testString() {
        assertThat(PythonEval.eval("'hello'"))
            .isEqualTo(PythonObjects.str("hello"));
    }

    @Test
    public void testGlobals() {
        assertThat(PythonEval.eval("test", Map.of("test", PythonObjects.str("hello"))))
            .isEqualTo(PythonObjects.str("hello"));
    }

    @Test
    public void testIndented() {
        assertThat(PythonEval.eval("   print"))
            .isEqualTo(PythonObjects.getBuiltin("print"));
    }

    @Test
    public void testCompiled() {
        final PythonObject compiled = PythonEval.eval("compile('1234', '<test>', 'eval')");
        assertThat(PythonEval.eval(compiled))
            .isEqualTo(PythonObjects.pythonInt(1234));
    }

    @Test
    public void testCompiledWithGlobals() {
        final PythonObject compiled = PythonEval.eval("compile('test', '<test>', 'eval')");
        assertThat(PythonEval.eval(compiled, Map.of("test", PythonObjects.pythonInt(1234))))
            .isEqualTo(PythonObjects.pythonInt(1234));
    }
}
