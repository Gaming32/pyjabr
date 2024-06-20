package io.github.gaming32.pyjabr.test.run;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonObjects;
import io.github.gaming32.pyjabr.run.PythonRun;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RunTests {
    @BeforeEach
    public void initialize() {
        PythonSystem.initialize();
    }

    @AfterEach
    public void shutdown() {
        PythonSystem.shutdown();
    }

    @Test
    public void loadResource() throws IOException {
        PythonRun.runResource("run_test.py", "run_test");
        assertThat(PythonObjects.importModule("run_test").callMethod("my_function"))
            .isEqualTo(PythonObjects.pythonInt(1234));
    }

    @Test
    public void loadString() {
        @Language("python")
        final String module = """
            MY_VALUE = 'world'
            """;
        PythonRun.runString(module, "<loadString>", "string_test");
        assertThat(PythonObjects.importModule("string_test").getAttr("MY_VALUE"))
            .isEqualTo(PythonObjects.str("world"));
    }
}
