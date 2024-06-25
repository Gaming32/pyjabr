package io.github.gaming32.pyjabr.test.interop;

import io.github.gaming32.pyjabr.PythonSystem;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;
import io.github.gaming32.pyjabr.run.PythonEval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.*;

public class ObjectConversionTests {
    @BeforeAll
    public static void initialize() {
        PythonSystem.initialize();
    }

    @Test
    public void testFromJavaObject() {
        assertThat(PythonObject.fromJavaObject(null)).isEqualTo(PythonObjects.none());
        assertThat(PythonObject.fromJavaObject("hello")).isEqualTo(PythonObjects.str("hello"));
        assertThat(PythonObject.fromJavaObject(5)).isEqualTo(PythonObjects.pythonInt(5));
        assertThat(PythonObject.fromJavaObject(1L << 40)).isEqualTo(PythonObjects.pythonInt(1L << 40));
        assertThat(PythonObject.fromJavaObject(3.14f)).isEqualTo(PythonObjects.pythonFloat(3.14f));
        assertThat(PythonObject.fromJavaObject(6.28)).isEqualTo(PythonObjects.pythonFloat(6.28));
        assertThat(PythonObject.fromJavaObject(false)).isEqualTo(PythonObjects.false_());
        assertThat(PythonObject.fromJavaObject(true)).isEqualTo(PythonObjects.true_());
        assertThat(PythonObject.fromJavaObject((byte)3)).isEqualTo(PythonObjects.pythonInt(3));
        assertThat(PythonObject.fromJavaObject((short)256)).isEqualTo(PythonObjects.pythonInt(256));
        assertThat(PythonObject.fromJavaObject(PythonObjects.none())).isEqualTo(PythonObjects.none());
        assertThat(PythonObject.fromJavaObject(new TestRecord()).toString()).isEqualTo(new TestRecord().toString());
    }

    @Test
    public void testAsJavaObjectUntyped() {
        assertThat(PythonObjects.none().asJavaObject()).isEqualTo(null);
        assertThat(PythonObjects.false_().asJavaObject()).isEqualTo(false);
        assertThat(PythonObjects.true_().asJavaObject()).isEqualTo(true);
        assertThat(PythonObjects.pythonInt(5).asJavaObject()).isEqualTo(5);
        assertThat(PythonObjects.pythonInt(-1).asJavaObject()).isEqualTo(-1);
        assertThat(PythonObjects.pythonInt(1L << 42).asJavaObject()).isEqualTo(1L << 42);
        assertThat(PythonObjects.str("hello").asJavaObject()).isEqualTo("hello");
        assertThat(PythonObjects.pythonFloat(3.14).asJavaObject()).isEqualTo(3.14);
        assertThat(PythonObjects.unreflectClass(String.class).asJavaObject()).isEqualTo(String.class);
        assertThat(PythonObject.fromJavaObject(new TestRecord()).asJavaObject()).isEqualTo(new TestRecord());
        assertThat(PythonObjects.complex(5, 3).asJavaObject()).isEqualTo(PythonObjects.complex(5, 3));
    }

    @Test
    public void testAsJavaObjectTyped() {
        assertThat(PythonObjects.none().asJavaObject(void.class)).isEqualTo(null);
        assertThatThrownBy(() -> PythonObjects.str("hi").asJavaObject(void.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Only None can be assigned to void");
        assertThat(PythonObjects.none().asJavaObject(Integer.class)).isEqualTo(null);
        assertThatThrownBy(() -> PythonObjects.none().asJavaObject(int.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot convert None to primitive type int");
        assertThatNoException().isThrownBy(() -> PythonObjects.pythonInt(5).asJavaObject(Long.class));
        assertThat(PythonObjects.pythonInt(5).asJavaObject(Long.class)).isEqualTo(5L);
        assertThat(PythonObjects.str("hello").asJavaObject(String.class)).isEqualTo("hello");
        assertThat(PythonObjects.str("hello").asJavaObject(CharSequence.class)).isEqualTo("hello");
        assertThat(PythonObjects.str("hello").asJavaObject(PythonObject.class)).isEqualTo(PythonObjects.str("hello"));
        assertThat(PythonObjects.unreflectClass(String.class).asJavaObject(Class.class)).isEqualTo(String.class);
        assertThat(PythonObject.fromJavaObject(String.class).asJavaObject(Class.class)).isEqualTo(String.class);
        assertThat(PythonObject.fromJavaObject(BigInteger.valueOf(5)).asJavaObject(BigInteger.class)).isEqualTo(BigInteger.valueOf(5));
        assertThat(PythonObjects.unreflectClass(String.class).asJavaObject(Type.class)).isEqualTo(String.class);
    }

    @Test
    public void testTypedPrimitiveConversions() {
        assertThat(PythonObjects.pythonInt(5).asJavaObject(int.class)).isEqualTo(5);
        assertThatThrownBy(() -> PythonObjects.pythonInt(1L << 42).asJavaObject(int.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit " + (1L << 42) + " into an int");
        assertThatThrownBy(() -> PythonObjects.pythonInt(129).asJavaObject(byte.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit 129 into a byte");
        assertThatThrownBy(() -> PythonObjects.pythonInt(35_000).asJavaObject(short.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit 35000 into a short");

        assertThatThrownBy(() -> PythonObjects.pythonInt(Long.MAX_VALUE)
            .pow(PythonObjects.pythonInt(2))
            .asJavaObject(long.class)
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit " + BigInteger.valueOf(Long.MAX_VALUE).pow(2) + " into a long");

        assertThat(PythonObjects.pythonFloat(3.14).asJavaObject(double.class)).isEqualTo(3.14);
        assertThat(PythonObjects.pythonFloat(6.28).asJavaObject(float.class)).isNotEqualTo(6.28);
        assertThat(PythonObjects.pythonFloat(6.28).asJavaObject(float.class)).isEqualTo(6.28f);

        assertThat(PythonObjects.str("").asJavaObject(boolean.class)).isFalse();
        assertThat(PythonObjects.str("hi").asJavaObject(boolean.class)).isTrue();

        assertThat(PythonObjects.str("a").asJavaObject(char.class)).isEqualTo('a');
        assertThatThrownBy(() -> PythonObjects.str("ab").asJavaObject(char.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("str must be one character long to be interpreted as char");
        assertThatThrownBy(() -> PythonEval.eval("'\\N{GRINNING FACE}'").asJavaObject(char.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit \ud83d\ude00 into a char");
        assertThat(PythonObjects.pythonInt(33).asJavaObject(char.class)).isEqualTo('!');
        assertThatThrownBy(() -> PythonObjects.pythonInt(-1).asJavaObject(char.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not fit -1 into a char");
        assertThatThrownBy(() -> PythonObjects.complex(5, 3).asJavaObject(char.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Could not convert to char")
            .cause()
            .isInstanceOf(PythonException.class)
            .hasMessage("TypeError: 'complex' object cannot be interpreted as an integer");
    }

    private record TestRecord() {
    }
}
