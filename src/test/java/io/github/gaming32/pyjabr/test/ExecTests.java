package io.github.gaming32.pyjabr.test;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.object.PythonObjects;
import io.github.gaming32.pyjabr.run.PythonExec;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExecTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    @Test
    public void testString() {
        @Language("python")
        final String test = """
            raise ValueError('string')
            """;
        assertThatThrownBy(() -> PythonExec.execString(test, "<testString>"))
            .isInstanceOf(PythonException.class)
            .hasMessage("ValueError: string");
    }

    @Test
    public void testResource() {
        assertThatThrownBy(() -> PythonExec.execResource("exec_test.py"))
            .isInstanceOf(PythonException.class)
            .hasMessage("ValueError: None");
    }

    @Test
    public void testStringGlobals() {
        @Language("python")
        final String test = """
            raise ValueError(test)
            """;
        assertThatThrownBy(() -> PythonExec.execString(
            test, "<testString>", Map.of("test", PythonObjects.str("hello"))
        ))
            .isInstanceOf(PythonException.class)
            .hasMessage("ValueError: hello");
    }

    @Test
    public void testResourceGlobals() {
        assertThatThrownBy(() -> PythonExec.execResource(
            "exec_test.py", Map.of("test", PythonObjects.str("resource"))
        ))
            .isInstanceOf(PythonException.class)
            .hasMessage("ValueError: resource");
    }
}
