from java.java.util import Objects

from java_api import JavaError

try:
    Objects.requireNonNull(None, 'This is a message')
except JavaError as e:
    print(e.java_exception)
