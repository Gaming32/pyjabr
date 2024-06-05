package io.github.gaming32.pythonfiddle.interop;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class JavaObjectIndex {
    public static final int NO_ID = -1;

    private static final Object CLASSES_LOCK = new Object();
    private static final Object2IntMap<String> CLASS_IDS = new Object2IntOpenHashMap<>();
    private static final Int2ObjectMap<Class<?>> FAKE_CLASSES = new Int2ObjectOpenHashMap<>();

    private static final Object CLASS_ATTRIBUTES_LOCK = new Object();
    private static final Map<Pair<Class<?>, String>, FieldOrExecutable> CLASS_ATTRIBUTES = new HashMap<>();

    private static final Object STATIC_FIELDS_LOCK = new Object();
    private static final Reference2IntMap<FieldOrExecutable.FieldWrapper> STATIC_FIELD_IDS = new Reference2IntOpenHashMap<>();
    private static final Int2ObjectMap<FieldOrExecutable.FieldWrapper> FAKE_STATIC_FIELDS = new Int2ObjectOpenHashMap<>();

    private static final Object STATIC_EXECUTABLES_LOCK = new Object();
    private static final Reference2IntMap<FieldOrExecutable.ExecutablesWrapper> STATIC_EXECUTABLES_IDS = new Reference2IntOpenHashMap<>();
    private static final Int2ObjectMap<FieldOrExecutable.ExecutablesWrapper> FAKE_STATIC_EXECUTABLES = new Int2ObjectOpenHashMap<>();

    static {
        CLASS_IDS.defaultReturnValue(NO_ID);
        STATIC_FIELD_IDS.defaultReturnValue(NO_ID);
        STATIC_EXECUTABLES_IDS.defaultReturnValue(NO_ID);
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

    public static void removeClass(int classId) {
        synchronized (CLASSES_LOCK) {
            final Class<?> clazz = FAKE_CLASSES.remove(classId);
            if (clazz != null) {
                CLASS_IDS.removeInt(clazz.getName());
            }
        }
    }

    public static Class<?> getClassById(int classId) {
        synchronized (CLASSES_LOCK) {
            return FAKE_CLASSES.get(classId);
        }
    }

    @Nullable
    public static FieldOrExecutable findClassAttribute(int ownerId, String name) {
        final Pair<Class<?>, String> key = Pair.of(getClassById(ownerId), name);
        synchronized (CLASS_ATTRIBUTES_LOCK) {
            return CLASS_ATTRIBUTES.computeIfAbsent(key, k -> FieldOrExecutable.lookup(k.left(), k.right(), true));
        }
    }

    public static int getStaticExecutablesId(FieldOrExecutable.ExecutablesWrapper executables) {
        synchronized (STATIC_EXECUTABLES_LOCK) {
            final int oldIndex = STATIC_EXECUTABLES_IDS.getInt(executables);
            if (oldIndex != NO_ID) {
                return oldIndex;
            }
            final int index = STATIC_EXECUTABLES_IDS.size();
            STATIC_EXECUTABLES_IDS.put(executables, index);
            FAKE_STATIC_EXECUTABLES.put(index, executables);
            return index;
        }
    }

    public static int getStaticFieldId(FieldOrExecutable.FieldWrapper field) {
        synchronized (STATIC_FIELDS_LOCK) {
            final int oldIndex = STATIC_FIELD_IDS.getInt(field);
            if (oldIndex != NO_ID) {
                return oldIndex;
            }
            final int index = STATIC_FIELD_IDS.size();
            STATIC_FIELD_IDS.put(field, index);
            FAKE_STATIC_FIELDS.put(index, field);
            return index;
        }
    }

    public static FieldOrExecutable.FieldWrapper getStaticField(int fieldId) {
        synchronized (STATIC_FIELDS_LOCK) {
            return FAKE_STATIC_FIELDS.get(fieldId);
        }
    }
}
