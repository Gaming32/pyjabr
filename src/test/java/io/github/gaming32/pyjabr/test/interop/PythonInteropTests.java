package io.github.gaming32.pyjabr.test.interop;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.run.PythonExec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PythonInteropTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    @Test
    public void testInterop() throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream oldOut = System.out;
        System.setOut(new PrintStream(output));
        try {
            PythonExec.execResource("interop_test.py");
        } finally {
            System.setOut(oldOut);
        }
        assertThat(output.toString().replace(System.lineSeparator(), "\n"))
            .isEqualTo("""
                2
                4
                6
                1, 3, 2, 6, 3, 9
                
                class java.lang.Object
                no public constructor for Java class java.lang.System
                instance attribute 'notAField' not found on Java class java.lang.Object
                field io.github.gaming32.pyjabr.test.interop.TestClass.instanceField is not static
                null
                no overload matches args (5)
                null
                23
                no overload matches args (None)
                java.lang.String
                java.lang.System
                Cannot convert <class 'str'> to class java.lang.Class
                """);
    }
}
