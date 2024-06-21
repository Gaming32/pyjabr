To regenerate the `cpython` package, use `jextract` like so:

```shell
jextract \
  --output src/main/java \
  -I /path/to/python/include \
  -l python3 \
  -t io.github.gaming32.pyjabr.lowlevel.cpython \
  -D Py_LIMITED_API=<PythonVersion.TARGET_VERSION> \
  -D _typeobject=_object \
  -D _longobject=_object \
  /path/to/python/include/Python.h
```

Now open `Python_h_2.java` and make this change:

```diff
- static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("python3"), LIBRARY_ARENA)
+ static final SymbolLookup SYMBOL_LOOKUP = io.github.gaming32.pyjabr.lowlevel.CPythonFinder.findCPython(LIBRARY_ARENA)
```
