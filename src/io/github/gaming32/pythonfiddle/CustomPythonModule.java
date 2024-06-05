package io.github.gaming32.pythonfiddle;

import org.jetbrains.annotations.Nullable;
import org.python.PyImport_AppendInittab$initfunc;
import org.python.PyModuleDef;
import org.python.PyModuleDef_Base;
import org.python.Python_h;
import org.python._object;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public record CustomPythonModule(String name, @Nullable String doc, CustomPythonFunction... functions) {
    public CustomPythonModule(String name, CustomPythonFunction... functions) {
        this(name, null, functions);
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
