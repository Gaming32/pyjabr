package io.github.gaming32.pyjabr.object;

import io.github.gaming32.pyjabr.lowlevel.PythonSystem;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Map;

import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.PyDict_Check;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

// Based on builtin_eval_impl
public final class PythonEval {
    private static final MemorySegment FILENAME = Arena.global().allocateFrom("<PythonEval>");

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
        return PythonSystem.callPython(() -> {
            checkGlobalsLocals(globals, locals);

            final MemorySegment code;
            try (Arena arena = Arena.ofConfined()) {
                code = Py_CompileString(arena.allocateFrom(trimSource(source)), FILENAME, Py_eval_input());
            }
            final MemorySegment result = PyEval_EvalCode(code, globals.borrow(), locals.borrow());
            Py_DecRef(code);
            return PythonObject.checkAndSteal(result);
        });
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
        return PythonSystem.callPython(() -> {
            checkGlobalsLocals(globals, locals);
            return PythonObject.checkAndSteal(PyEval_EvalCode(code.borrow(), globals.borrow(), locals.borrow()));
        });
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
