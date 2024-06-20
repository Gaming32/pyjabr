package io.github.gaming32.pyjabr.lowlevel.module;

import io.github.gaming32.pyjabr.lowlevel.cpython.PyImport_AppendInittab$initfunc;
import io.github.gaming32.pyjabr.lowlevel.cpython.PyModuleDef;
import io.github.gaming32.pyjabr.lowlevel.cpython.PyModuleDef_Base;
import io.github.gaming32.pyjabr.lowlevel.cpython.Python_h;
import io.github.gaming32.pyjabr.lowlevel.cpython._object;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public record CustomPythonModule(String name, @Nullable String doc, CustomPythonFunction... functions) {
    public CustomPythonModule(String name, CustomPythonFunction... functions) {
        this(name, null, functions);
    }

    public static CustomPythonModule fromClass(Class<?> clazz) throws IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            lookup = MethodHandles.privateLookupIn(clazz, lookup);
        } catch (IllegalAccessException _) {
        }
        return fromClass(lookup, clazz);
    }

    public static CustomPythonModule fromClass(MethodHandles.Lookup lookup, Class<?> clazz) throws IllegalAccessException {
        final PythonModule annotation = clazz.getAnnotation(PythonModule.class);
        if (annotation == null) {
            throw new IllegalArgumentException(clazz + " must be annotated with @PythonModule");
        }
        final List<CustomPythonFunction> functions = new ArrayList<>();
        for (final Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PythonFunction.class)) continue;
            functions.add(CustomPythonFunction.fromMethod(lookup, method));
        }
        return new CustomPythonModule(annotation.value(), functions.toArray(CustomPythonFunction[]::new));
    }

    public MemorySegment createModuleDef(Arena arena) {
        final MemorySegment def = PyModuleDef.allocate(arena);
        initModuleBase(def);
        PyModuleDef.m_name(def, arena.allocateFrom(name));
        PyModuleDef.m_doc(def, doc != null ? arena.allocateFrom(doc) : MemorySegment.NULL);
        PyModuleDef.m_size(def, -1L);
        PyModuleDef.m_methods(def, CustomPythonFunction.createMethodDefs(arena, functions));
        PyModuleDef.m_slots(def, MemorySegment.NULL);
        PyModuleDef.m_traverse(def, MemorySegment.NULL);
        PyModuleDef.m_clear(def, MemorySegment.NULL);
        PyModuleDef.m_free(def, MemorySegment.NULL);
        return def;
    }

    public void registerAsBuiltin(Arena arena) {
        final MemorySegment def = createModuleDef(arena);
        Python_h.PyImport_AppendInittab(
            PyModuleDef.m_name(def),
            PyImport_AppendInittab$initfunc.allocate(() -> createPythonModule(def), arena)
        );
    }

    private static MemorySegment createPythonModule(MemorySegment moduleDef) {
        return Python_h.PyModule_Create2(moduleDef, Python_h.PYTHON_API_VERSION());
    }

    private static void initModuleBase(MemorySegment moduleDef) {
        final MemorySegment moduleBase = PyModuleDef.m_base(moduleDef);
        initObjectBase(moduleBase);
        PyModuleDef_Base.m_init(moduleBase, Python_h._Py_NULL());
        PyModuleDef_Base.m_index(moduleBase, 0L);
        PyModuleDef_Base.m_copy(moduleBase, Python_h._Py_NULL());
    }

    private static void initObjectBase(MemorySegment moduleBase) {
        final MemorySegment objectBase = PyModuleDef_Base.ob_base(moduleBase);
        _object.ob_refcnt(objectBase, Python_h._Py_IMMORTAL_REFCNT());
        _object.ob_type(objectBase, Python_h._Py_NULL());
    }
}
