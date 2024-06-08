from java.java.io import PrintStream, OutputStream
from java.java.lang import System


def print_numbers() -> None:
    out = System.out
    for i in range(1_000_000):
        out.println(i)

System.out.println('Start')

old_out = System.out
System.setOut(PrintStream(OutputStream.nullOutputStream()))
print_numbers()
System.setOut(old_out)

System.out.println('End')
