package io.github.gaming32.pyjabr.object;

public record SendResult(Type type, PythonObject value) {
    public enum Type {
        RETURN, YIELD
    }
}
