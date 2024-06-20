from java.java.lang import System
from java.java.util.function import Consumer, Function, BiConsumer
from java.java.util.stream import Stream, Collectors

from java_api import make_lambda, FakeJavaObject


def map_multi_test(x: int, consumer: FakeJavaObject) -> None:
    consumer.accept(x)
    consumer.accept(x * 3)


println = System.out.println
Stream.of(1, 2, 3).map(make_lambda(Function, lambda x: x * 2)).forEach(make_lambda(Consumer, println))
println(
    Stream.of(1, 2, 3)
    .mapMulti(make_lambda(BiConsumer, map_multi_test))
    .map(make_lambda(Function, str))
    .collect(Collectors.joining(', '))
)
