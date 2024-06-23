package io.github.gaming32.pyjabr.test;

import io.github.gaming32.pyjabr.run.PythonRun;

import java.io.IOException;

public class ExceptionTest {
    public static void main(String[] args) throws IOException {
        PythonRun.runResource("exception_test.py", "exception_test");
    }
}
