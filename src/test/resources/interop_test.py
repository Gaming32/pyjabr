from typing import Callable, Any

from java.java.lang import System, Object, String
from java.java.util.function import Consumer, Function, BiConsumer
from java.java.util.stream import Stream, Collectors
from java.io.github.gaming32.pyjabr.test.interop import TestClass

from java_api import make_lambda, FakeJavaObject

println = System.out.println


def map_multi_test(x: int, consumer: FakeJavaObject) -> None:
    consumer.accept(x)
    consumer.accept(x * 3)


def expect_error(exc: type[BaseException], expr: Callable[[], Any]) -> None:
    try:
        println(expr())
    except exc as e:
        println(e)


Stream.of(1, 2, 3).map(make_lambda(Function, lambda x: x * 2)).forEach(make_lambda(Consumer, println))
println(
    Stream.of(1, 2, 3)
    .mapMulti(make_lambda(BiConsumer, map_multi_test))
    .map(make_lambda(Function, str))
    .collect(Collectors.joining(', '))
)

println()
println(Object().getClass())

expect_error(AttributeError, lambda: System())

expect_error(AttributeError, lambda: Object().notAField)

expect_error(TypeError, lambda: TestClass.instanceField)

TestClass.voidMethod(None)
expect_error(TypeError, lambda: TestClass.voidMethod(5))

TestClass.stringMethod(None)

TestClass.intMethod(23)
expect_error(TypeError, lambda: TestClass.intMethod(None))

TestClass.classMethod(String)
TestClass.classMethod(System.reflect_java())

expect_error(TypeError, lambda: setattr(TestClass, 'staticField', 'hi'))
