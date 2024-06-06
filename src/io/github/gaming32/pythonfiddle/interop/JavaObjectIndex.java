package io.github.gaming32.pythonfiddle.interop;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
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
    private static final Object2IntMap<String> CLASS_IDS_BY_NAME = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<Class<?>> CLASS_IDS_BY_CLASS = new Object2IntOpenHashMap<>();
    private static final Int2ObjectMap<Class<?>> CLASSES_BY_ID = new Int2ObjectOpenHashMap<>();
    private static final Int2IntOpenHashMap CLASS_REFCOUNTS = new Int2IntOpenHashMap();

    private static final Object CLASS_ATTRIBUTES_LOCK = new Object();
    private static final Map<Pair<Class<?>, String>, FieldOrMethod> CLASS_ATTRIBUTES = new HashMap<>();

    public static final ObjectIndex<FieldOrMethod.FieldWrapper> STATIC_FIELDS = new ObjectIndex<>();
    public static final ObjectIndex<FieldOrMethod.MethodWrapper> STATIC_METHODS = new ObjectIndex<>();
    public static final ObjectIndex<Object> OBJECTS = new ObjectIndex<>();

    static {
        CLASS_IDS_BY_NAME.defaultReturnValue(NO_ID);
        CLASS_IDS_BY_CLASS.defaultReturnValue(NO_ID);
    }

    public static Integer findClass(String className) {
        synchronized (CLASSES_LOCK) {
            int id = CLASS_IDS_BY_NAME.getInt(className);
            if (id == NO_ID) {
                final Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    return null;
                }
                id = CLASS_IDS_BY_NAME.size();
                CLASS_IDS_BY_NAME.put(className, id);
                CLASS_IDS_BY_CLASS.put(clazz, id);
                CLASSES_BY_ID.put(id, clazz);
            }
            CLASS_REFCOUNTS.addTo(id, 1);
            return id;
        }
    }

    public static int getClassId(Class<?> clazz) {
        synchronized (CLASSES_LOCK) {
            int id = CLASS_IDS_BY_CLASS.getInt(clazz);
            if (id == NO_ID) {
                id = CLASS_IDS_BY_CLASS.size();
                CLASS_IDS_BY_NAME.put(clazz.getName(), id);
                CLASS_IDS_BY_CLASS.put(clazz, id);
                CLASSES_BY_ID.put(id, clazz);
            }
            CLASS_REFCOUNTS.addTo(id, 1);
            return id;
        }
    }

    public static Class<?> getClassById(int classId) {
        synchronized (CLASSES_LOCK) {
            return CLASSES_BY_ID.get(classId);
        }
    }

    public static void removeClass(int classId) {
        synchronized (CLASSES_LOCK) {
            final int oldRefCount = CLASS_REFCOUNTS.addTo(classId, -1);
            if (oldRefCount == 1) {
                final Class<?> clazz = CLASSES_BY_ID.remove(classId);
                CLASS_IDS_BY_NAME.removeInt(clazz.getName());
                CLASS_IDS_BY_CLASS.removeInt(clazz);
                CLASS_REFCOUNTS.remove(classId);
            } else if (oldRefCount < 1) {
                throw new IllegalStateException("refcount for class " + classId + " became negative");
            }
        }
    }

    @Nullable
    public static FieldOrMethod findClassAttribute(int ownerId, String name) {
        final Pair<Class<?>, String> key = Pair.of(getClassById(ownerId), name);
        synchronized (CLASS_ATTRIBUTES_LOCK) {
            return CLASS_ATTRIBUTES.computeIfAbsent(key, k -> FieldOrMethod.lookup(k.left(), k.right(), true));
        }
    }

    public static final class ObjectIndex<T> {
        private final Reference2IntMap<T> ids = new Reference2IntOpenHashMap<>();
        private final Int2ReferenceMap<T> objects = new Int2ReferenceOpenHashMap<>();
        private final Int2IntOpenHashMap refcounts = new Int2IntOpenHashMap();

        public ObjectIndex() {
            ids.defaultReturnValue(NO_ID);
        }

        public synchronized int getId(T obj) {
            int id = ids.getInt(obj);
            if (id == NO_ID) {
                id = ids.size();
                ids.put(obj, id);
                objects.put(id, obj);
            }
            refcounts.addTo(id, 1);
            return id;
        }

        public synchronized T get(int id) {
            return objects.get(id);
        }

        public synchronized void remove(int id) {
            final int oldRefCount = refcounts.addTo(id, -1);
            if (oldRefCount == 1) {
                final T obj = objects.remove(id);
                ids.removeInt(obj);
                refcounts.remove(id);
            } else if (oldRefCount < 1) {
                throw new IllegalStateException("refcount for " + id + " became negative");
            }
        }
    }
}
