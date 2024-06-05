package io.github.gaming32.pythonfiddle.interop;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import static org.python.Python_h_2._Py_NoneStruct;

public class JavaObjectIndex {
    public static final int NO_ID = -1;

    private static final Object CLASSES_LOCK = new Object();
    private static final Object2IntMap<String> CLASS_IDS = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());
    private static final Int2ObjectMap<Class<?>> FAKE_CLASSES = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    static {
        CLASS_IDS.defaultReturnValue(NO_ID);
    }

    public static Integer findClass(String className) {
        synchronized (CLASSES_LOCK) {
            final int oldIndex = CLASS_IDS.getInt(className);
            if (oldIndex != NO_ID) {
                return oldIndex;
            }
            final Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
            final int index = CLASS_IDS.size();
            CLASS_IDS.put(className, index);
            FAKE_CLASSES.put(index, clazz);
            return index;
        }
    }
}
