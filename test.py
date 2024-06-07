from java.java.lang import System
from java.java.io import PrintStream, OutputStream

System.out.println('Start')

old_out = System.out
System.setOut(PrintStream(OutputStream.nullOutputStream()))
for i in range(1_000_000):
    System.out.println(i)
System.setOut(old_out)

System.out.println('End')
