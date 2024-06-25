package io.github.gaming32.pyjabr.run;

import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;
import io.github.gaming32.pyjabr.lowlevel.LowLevelAccess;
import io.github.gaming32.pyjabr.object.PythonException;
import io.github.gaming32.pyjabr.object.PythonObject;
import io.github.gaming32.pyjabr.object.PythonObjects;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Map;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

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

    public static PythonObject eval(String source, PythonObject globals, PythonObject locals) {
        return GilStateUtil.runPython(() -> {
            PythonExec.checkGlobalsLocals(globals, locals);

            final MemorySegment code;
            try (Arena arena = Arena.ofConfined()) {
                code = Py_CompileString(arena.allocateFrom(trimSource(source)), FILENAME, Py_eval_input());
            }
            if (code.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            final var access = LowLevelAccess.pythonObject();
            final MemorySegment result = PyEval_EvalCode(code, access.borrow(globals), access.borrow(locals));
            Py_DecRef(code);
            return access.checkAndSteal(result);
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

    public static PythonObject eval(PythonObject code, PythonObject globals, PythonObject locals) {
        return GilStateUtil.runPython(() -> {
            PythonExec.checkGlobalsLocals(globals, locals);
            final var access = LowLevelAccess.pythonObject();
            return access.checkAndSteal(PyEval_EvalCode(access.borrow(code), access.borrow(globals), access.borrow(locals)));
        });
    }
}
