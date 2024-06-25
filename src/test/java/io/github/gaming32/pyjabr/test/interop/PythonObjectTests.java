package io.github.gaming32.pyjabr.test.interop;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.ComparisonOperator;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class PythonObjectTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    @Test
    public void testToString() {
        final PythonObject simple = PythonObjects.str("hello");
        assertThat(simple.toString()).isEqualTo("hello");
        assertThat(simple.repr()).isEqualTo("'hello'");

        final PythonObject quoted = PythonObjects.str("'world'");
        assertThat(quoted.toString()).isEqualTo("'world'");
        assertThat(quoted.repr()).isEqualTo("\"'world'\"");
    }

    @Test
    public void testHash() {
        final PythonObject list = PythonObjects.str("ab").list();
        //noinspection ResultOfMethodCallIgnored
        assertThatNoException().isThrownBy(list::hashCode);
        assertThatThrownBy(list::hash)
            .isInstanceOf(PythonException.class)
            .hasMessage("TypeError: unhashable type: 'list'");

        final PythonObject integer = PythonObjects.pythonInt(1L << 63);
        assertThat(integer.hashCode()).isEqualTo(Long.hashCode(integer.hash()));
    }

    @Test
    public void testCompare() {
        final PythonObject five = PythonObjects.pythonInt(5);
        final PythonObject ten = PythonObjects.pythonInt(10);
        assertThat(five.compare(ten, ComparisonOperator.LESS_THAN)).isTrue();
        assertThat(five.compare(five, ComparisonOperator.NOT_EQUAL)).isFalse();
        assertThat(ten.compare(five, ComparisonOperator.GREATER_THAN_OR_EQUAL)).isTrue();
        assertThat(ten.compare(ten, ComparisonOperator.GREATER_THAN_OR_EQUAL)).isTrue();
        assertThat(ten.compare(five, ComparisonOperator.LESS_THEN_OR_EQUAL)).isFalse();
    }

    @Test
    public void testIsInstanceOf() {
        final PythonObject string = PythonObjects.str("hello");
        assertThat(string.isInstanceOf(PythonObjects.getBuiltin("str"))).isTrue();
        assertThat(string.isInstanceOf(PythonObjects.getBuiltin("list"))).isFalse();
    }

    @Test
    public void testIsSubclassOf() {
        assertThat(PythonObjects.getBuiltin("bool").isSubclassOf(PythonObjects.getBuiltin("int"))).isTrue();
        assertThat(PythonObjects.getBuiltin("list").isSubclassOf(PythonObjects.getBuiltin("object"))).isTrue();
        assertThat(PythonObjects.getBuiltin("list").isSubclassOf(PythonObjects.getBuiltin("int"))).isFalse();
        assertThat(PythonObjects.getBuiltin("list").isSubclassOf(PythonObjects.importModule("collections").getAttr("abc", "Collection"))).isTrue();
    }

    @Test
    public void testTruthy() {
        final PythonObject truthy = PythonObjects.str("hello");
        final PythonObject falsy = PythonObjects.str("");
        assertThat(truthy.isTrue()).isTrue();
        assertThat(falsy.isTrue()).isFalse();
        assertThat(truthy.not()).isFalse();
        assertThat(falsy.not()).isTrue();
    }

    @Test
    public void testCall() {
        assertThat(PythonObjects.getBuiltin("str").call())
            .isEqualTo(PythonObjects.str(""));
        assertThat(PythonObjects.getBuiltin("int").call(PythonObjects.str("35")))
            .isEqualTo(PythonObjects.pythonInt(35));
        assertThat(PythonObjects.getBuiltin("type").call(
            PythonObjects.str("TestType"),
            PythonObjects.tuple(),
            PythonObjects.dict(Map.of())
        ).toString()).isEqualTo("<class 'TestType'>");
    }

    @Test
    public void testAbstract() {
        assertThat(PythonObjects.getBuiltin("str").isCallable()).isTrue();
        assertThat(PythonObjects.str("str").isCallable()).isFalse();

        assertThat(PythonObjects.pythonInt(5).isNumber()).isTrue();
        assertThat(PythonObjects.pythonFloat(5.1).isNumber()).isTrue();
        assertThat(PythonObjects.str("5.1").isNumber()).isFalse();

        assertThat(PythonObjects.list().isSequence()).isTrue();
        assertThat(PythonObjects.dict(Map.of()).isSequence()).isFalse();

        assertThat(PythonObjects.dict(Map.of()).isMapping()).isTrue();
        assertThat(PythonObjects.list().isMapping()).isTrue();
        assertThat(PythonObjects.str("hello").isMapping()).isTrue();
        assertThat(PythonObjects.pythonInt(5).isMapping()).isFalse();

        assertThat(PythonObjects.list().isIterator()).isFalse();
        assertThat(PythonObjects.list().iterator().getIterator().isIterator()).isTrue();
    }
}
