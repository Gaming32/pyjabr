package io.github.gaming32.pyjabr.module;

import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.Nullable;
import org.python.PyMethodDef;
import org.python.Python_h;
import org.python._PyCFunctionFast;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public record CustomPythonFunction(String name, Implementation impl, @Nullable String doc) {
    public CustomPythonFunction(String name, Implementation impl) {
        this(name, impl, null);
    }

    public static CustomPythonFunction fromMethod(MethodHandles.Lookup lookup, Method method) throws IllegalAccessException {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(method + " must be static");
        }
        final PythonFunction annotation = method.getAnnotation(PythonFunction.class);
        if (annotation == null) {
            throw new IllegalArgumentException(method + " must be annotated with @PythonFunction");
        }
        String functionName = annotation.value();
        if (functionName.isEmpty()) {
            functionName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
        }
        final Implementation implementation = FunctionAdapter.adapt(lookup, method);
        return new CustomPythonFunction(functionName, implementation);
    }

    public MemorySegment createMethodDef(Arena arena) {
        final MemorySegment def = PyMethodDef.allocate(arena);
        PyMethodDef.ml_name(def, arena.allocateFrom(name));
        PyMethodDef.ml_meth(def, _PyCFunctionFast.allocate(impl.asFunction(), arena));
        PyMethodDef.ml_flags(def, Python_h.METH_FASTCALL());
        PyMethodDef.ml_doc(def, doc != null ? arena.allocateFrom(doc) : MemorySegment.NULL);
        return def;
    }

    public static MemorySegment createMethodDefs(Arena arena, CustomPythonFunction... functions) {
        final MemorySegment result = PyMethodDef.allocateArray(functions.length + 1, arena);
        for (int i = 0; i < functions.length; i++) {
            PyMethodDef.asSlice(result, i).copyFrom(functions[i].createMethodDef(arena));
        }
        fillNull(PyMethodDef.asSlice(result, functions.length));
        return result;
    }

    private static void fillNull(MemorySegment def) {
        PyMethodDef.ml_name(def, MemorySegment.NULL);
        PyMethodDef.ml_meth(def, MemorySegment.NULL);
        PyMethodDef.ml_flags(def, 0);
        PyMethodDef.ml_doc(def, MemorySegment.NULL);
    }

    @FunctionalInterface
    public interface Implementation {
        MemorySegment call(MemorySegment self, MemorySegment... args);

        default _PyCFunctionFast.Function asFunction() {
            return (self, args, argCount) -> {
                final MemorySegment[] argArray = new MemorySegment[(int)argCount];
                for (long i = 0; i < argCount; i++) {
                    argArray[(int)i] = args.getAtIndex(Python_h.C_POINTER, i);
                }
                return call(self, argArray);
            };
        }
    }
}
