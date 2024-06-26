package io.github.gaming32.pyjabr.lowlevel.interop;

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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class JavaObjectIndex {
    public static final int NO_ID = -1;

    private static final Object CLASSES_LOCK = new Object();
    private static final Object2IntMap<String> CLASS_IDS_BY_NAME = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<Class<?>> CLASS_IDS_BY_CLASS = new Object2IntOpenHashMap<>();
    private static final Int2ObjectMap<Class<?>> CLASSES_BY_ID = new Int2ObjectOpenHashMap<>();
    private static final Int2IntOpenHashMap CLASS_REFCOUNTS = new Int2IntOpenHashMap();
    private static int nextClassId;

    private static final Object CLASS_ATTRIBUTES_LOCK = new Object();
    private static final Map<ClassAttributeKey, FieldOrMethod> CLASS_ATTRIBUTES = new HashMap<>();

    public static final ObjectIndex<FieldOrMethod.FieldWrapper> FIELDS = new ObjectIndex<>();
    public static final ObjectIndex<FieldOrMethod.MethodWrapper> METHODS = new ObjectIndex<>();
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
                if (!Modifier.isPublic(clazz.getModifiers())) {
                    return null;
                }
                id = nextClassId++;
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
                id = nextClassId++;
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
                CLASS_REFCOUNTS.remove(classId);
                throw new IllegalStateException("refcount for class " + classId + " became negative");
            }
        }
    }

    @Nullable
    public static FieldOrMethod findClassAttribute(int ownerId, String name, boolean isStatic) {
        final Class<?> owner = getClassById(ownerId);
        if (owner == null) {
            return null;
        }
        final ClassAttributeKey key = new ClassAttributeKey(owner, name, isStatic);
        synchronized (CLASS_ATTRIBUTES_LOCK) {
            return CLASS_ATTRIBUTES.computeIfAbsent(key, k -> FieldOrMethod.lookup(k.clazz, k.name, k.isStatic));
        }
    }

    public static void clear() {
        clearClasses();
        clearClassAttributes();
        FIELDS.clear();
        METHODS.clear();
        OBJECTS.clear();
    }

    private static void clearClasses() {
        synchronized (CLASSES_LOCK) {
            CLASS_IDS_BY_NAME.clear();
            CLASS_IDS_BY_CLASS.clear();
            CLASSES_BY_ID.clear();
            CLASS_REFCOUNTS.clear();
            nextClassId = 0;
        }
    }

    private static void clearClassAttributes() {
        synchronized (CLASS_ATTRIBUTES_LOCK) {
            CLASS_ATTRIBUTES.clear();
        }
    }

    private record ClassAttributeKey(Class<?> clazz, String name, boolean isStatic) {
    }

    public static final class ObjectIndex<T> {
        private final Reference2IntMap<T> ids = new Reference2IntOpenHashMap<>();
        private final Int2ReferenceMap<T> objects = new Int2ReferenceOpenHashMap<>();
        private final Int2IntOpenHashMap refcounts = new Int2IntOpenHashMap();
        private int nextId;

        public ObjectIndex() {
            ids.defaultReturnValue(NO_ID);
        }

        public synchronized int getId(T obj) {
            int id = ids.getInt(obj);
            if (id == NO_ID) {
                id = nextId++;
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
                refcounts.remove(id);
                throw new IllegalStateException("refcount for " + id + " became negative");
            }
        }

        public synchronized void clear() {
            ids.clear();
            objects.clear();
            refcounts.clear();
            nextId = 0;
        }
    }
}
