from java.io.github.gaming32.pythonfiddle import TestClass


def print_fields() -> None:
    print(TestClass.intField)
    print(TestClass.stringField)
    print(TestClass.charField)


print_fields()
TestClass.intField = 5
TestClass.stringField = 'bye'
TestClass.charField = 'f'
print_fields()

TestClass.charField = 33
print(TestClass.charField)
