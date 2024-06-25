package io.github.gaming32.pyjabr.test.interop;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;
import io.github.gaming32.pyjabr.run.PythonEval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PythonObjectsTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    private static void assertPythonEqual(String expectedPython, PythonObject object) {
        assertThat(object).isEqualTo(PythonEval.eval(expectedPython));
    }

    @Test
    public void testPrimitives() {
        assertPythonEqual("None", PythonObjects.none());
        assertPythonEqual("1234", PythonObjects.pythonInt(1234));
        assertPythonEqual("1 << 50", PythonObjects.pythonInt(1L << 50));
        assertPythonEqual("3.14", PythonObjects.pythonFloat(3.14));
        assertPythonEqual("3j+5", PythonObjects.complex(5, 3));
        assertPythonEqual("'hello'", PythonObjects.str("hello"));
        assertPythonEqual("True", PythonObjects.true_());
        assertPythonEqual("False", PythonObjects.false_());
        assertPythonEqual("True", PythonObjects.bool(true));
        assertPythonEqual("False", PythonObjects.bool(false));
    }

    @Test
    public void testTuples() {
        assertPythonEqual(
            "(1, 2, 3, 4)",
            PythonObjects.tuple(
                PythonObjects.pythonInt(1),
                PythonObjects.pythonInt(2),
                PythonObjects.pythonInt(3),
                PythonObjects.pythonInt(4)
            )
        );
    }

    @Test
    public void testLists() {
        assertPythonEqual(
            "['a', 'b', 'c', 'd']",
            PythonObjects.list(
                PythonObjects.str("a"),
                PythonObjects.str("b"),
                PythonObjects.str("c"),
                PythonObjects.str("d")
            )
        );
        assertPythonEqual(
            "[True, False, False]",
            PythonObjects.list(List.of(
                PythonObjects.true_(),
                PythonObjects.false_(),
                PythonObjects.false_()
            ))
        );
    }

    @Test
    public void testDicts() {
        assertPythonEqual(
            "{'a': 1, 'b': 2, 'c': 3}",
            PythonObjects.dict(Map.of(
                PythonObjects.str("a"), PythonObjects.pythonInt(1),
                PythonObjects.str("b"), PythonObjects.pythonInt(2),
                PythonObjects.str("c"), PythonObjects.pythonInt(3)
            ))
        );
        assertPythonEqual(
            "{'hello': 43556, 'world': 96753}",
            PythonObjects.stringDict(Map.of(
                "hello", PythonObjects.pythonInt(43556),
                "world", PythonObjects.pythonInt(96753)
            ))
        );
    }

    @Test
    public void testUnreflect() {
        assertPythonEqual("__import__('java.java.lang').java.lang.String", PythonObjects.unreflectClass(String.class));
    }

    @Test
    public void testImport() {
        assertPythonEqual("__import__('math')", PythonObjects.importModule("math"));
    }

    @Test
    public void testBuiltins() {
        assertPythonEqual("__builtins__", PythonObjects.getBuiltins());
        assertPythonEqual("str", PythonObjects.getBuiltin("str"));
        assertPythonEqual("0", PythonObjects.callBuiltin("int"));
        assertPythonEqual("'6'", PythonObjects.callBuiltin("str", PythonObjects.pythonInt(6)));
        assertPythonEqual("8", PythonObjects.callBuiltin("int", PythonObjects.str("10"), PythonObjects.pythonInt(8)));
    }
}
