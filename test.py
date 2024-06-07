from java.java.lang import System
from java.java.io import PrintStream, OutputStream

System.out.println('Start')

old_out = System.out
System.setOut(PrintStream(OutputStream.nullOutputStream()))
out = System.out
for i in range(1_000_000):
    out.println(i)
System.setOut(old_out)

System.out.println('End')
