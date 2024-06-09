package io.github.gaming32.pyjabr.python;

import org.python.PyCompilerFlags;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Map;

import static io.github.gaming32.pyjabr.PythonUtil.*;
import static org.python.Python_h.*;

// Based on builtin_eval_impl
public final class PythonEval {
    private PythonEval() {
    }

    public static PythonObject eval(String source) {
        return eval(source, Map.of("__builtins__", PythonObjects.getBuiltins()));
    }

    public static PythonObject eval(String source, Map<String, PythonObject> globals) {
        return eval(source, PythonObjects.stringDict(globals));
    }

    public static PythonObject eval(String source, PythonObject globals) {
        return eval(source, globals, globals);
    }

    public static PythonObject eval(String source, Map<String, PythonObject> globals, Map<String, PythonObject> locals) {
        return eval(source, PythonObjects.stringDict(globals), PythonObjects.stringDict(locals));
    }

    public static PythonObject eval(String source, PythonObject globals, PythonObject locals) {
        checkGlobalsLocals(globals, locals);

        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment cf = PyCompilerFlags.allocate(arena);
            PyCompilerFlags.cf_flags(cf, PyCF_SOURCE_IS_UTF8());
            PyCompilerFlags.cf_feature_version(cf, PY_MINOR_VERSION()); // This is ignored anyway

            final MemorySegment sourceC = arena.allocateFrom(trimSource(source));

            PyEval_MergeCompilerFlags(cf);
            return PythonObject.checkAndSteal(PyRun_StringFlags(sourceC, Py_eval_input(), globals.borrow(), locals.borrow(), cf));
        }
    }

    private static String trimSource(String source) {
        int indent = 0;
        while (indent < source.length() && (source.charAt(indent) == ' ' || source.charAt(indent) == '\t')) {
            indent++;
        }
        return source.substring(indent);
    }

    public static PythonObject eval(PythonObject code) {
        return eval(code, Map.of("__builtins__", PythonObjects.getBuiltins()));
    }

    public static PythonObject eval(PythonObject code, Map<String, PythonObject> globals) {
        return eval(code, PythonObjects.stringDict(globals));
    }

    public static PythonObject eval(PythonObject code, PythonObject globals) {
        return eval(code, globals, globals);
    }

    public static PythonObject eval(PythonObject code, Map<String, PythonObject> globals, Map<String, PythonObject> locals) {
        return eval(code, PythonObjects.stringDict(globals), PythonObjects.stringDict(locals));
    }

    public static PythonObject eval(PythonObject code, PythonObject globals, PythonObject locals) {
        checkGlobalsLocals(globals, locals);

        final MemorySegment codeObj = code.borrow();
        if (!PyCode_Check(codeObj)) {
            throw new IllegalArgumentException("code must be a code object");
        }
        if (PyCode_GetNumFree(codeObj) > 0) {
            throw new IllegalArgumentException("code may not contain free variables");
        }

        return PythonObject.checkAndSteal(PyEval_EvalCode(codeObj, globals.borrow(), locals.borrow()));
    }

    private static void checkGlobalsLocals(PythonObject globals, PythonObject locals) {
        if (!PyDict_Check(globals.borrow())) {
            throw new IllegalArgumentException("globals must be a dict");
        }
        if (PyMapping_Check(locals.borrow()) == 0) {
            throw new IllegalArgumentException("locals must be a mapping");
        }
    }
}
