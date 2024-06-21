package io.github.gaming32.pyjabr.test.interop;

public class TestClass {
    public static Class<?> staticField = TestClass.class;

    public String instanceField = "hi";

    public static void voidMethod(Void arg) {
        System.out.println(arg);
    }

    public static void stringMethod(String arg) {
        System.out.println(arg);
    }

    public static void intMethod(int arg) {
        System.out.println(arg);
    }

    public static void classMethod(Class<?> arg) {
        System.out.println(arg.getName());
    }
}
