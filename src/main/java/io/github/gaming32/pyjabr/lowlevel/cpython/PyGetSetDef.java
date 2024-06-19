// Generated by jextract

package io.github.gaming32.pyjabr.lowlevel.cpython;

import java.lang.invoke.*;
import java.lang.foreign.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.foreign.ValueLayout.*;
import static java.lang.foreign.MemoryLayout.PathElement.*;

/**
 * {@snippet lang=c :
 * struct PyGetSetDef {
 *     const char *name;
 *     getter get;
 *     setter set;
 *     const char *doc;
 *     void *closure;
 * }
 * }
 */
public class PyGetSetDef {

    PyGetSetDef() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        Python_h.C_POINTER.withName("name"),
        Python_h.C_POINTER.withName("get"),
        Python_h.C_POINTER.withName("set"),
        Python_h.C_POINTER.withName("doc"),
        Python_h.C_POINTER.withName("closure")
    ).withName("PyGetSetDef");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final AddressLayout name$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("name"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * const char *name
     * }
     */
    public static final AddressLayout name$layout() {
        return name$LAYOUT;
    }

    private static final long name$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * const char *name
     * }
     */
    public static final long name$offset() {
        return name$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * const char *name
     * }
     */
    public static MemorySegment name(MemorySegment struct) {
        return struct.get(name$LAYOUT, name$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * const char *name
     * }
     */
    public static void name(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(name$LAYOUT, name$OFFSET, fieldValue);
    }

    private static final AddressLayout get$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("get"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * getter get
     * }
     */
    public static final AddressLayout get$layout() {
        return get$LAYOUT;
    }

    private static final long get$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * getter get
     * }
     */
    public static final long get$offset() {
        return get$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * getter get
     * }
     */
    public static MemorySegment get(MemorySegment struct) {
        return struct.get(get$LAYOUT, get$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * getter get
     * }
     */
    public static void get(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(get$LAYOUT, get$OFFSET, fieldValue);
    }

    private static final AddressLayout set$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("set"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * setter set
     * }
     */
    public static final AddressLayout set$layout() {
        return set$LAYOUT;
    }

    private static final long set$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * setter set
     * }
     */
    public static final long set$offset() {
        return set$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * setter set
     * }
     */
    public static MemorySegment set(MemorySegment struct) {
        return struct.get(set$LAYOUT, set$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * setter set
     * }
     */
    public static void set(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(set$LAYOUT, set$OFFSET, fieldValue);
    }

    private static final AddressLayout doc$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("doc"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * const char *doc
     * }
     */
    public static final AddressLayout doc$layout() {
        return doc$LAYOUT;
    }

    private static final long doc$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * const char *doc
     * }
     */
    public static final long doc$offset() {
        return doc$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * const char *doc
     * }
     */
    public static MemorySegment doc(MemorySegment struct) {
        return struct.get(doc$LAYOUT, doc$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * const char *doc
     * }
     */
    public static void doc(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(doc$LAYOUT, doc$OFFSET, fieldValue);
    }

    private static final AddressLayout closure$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("closure"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * void *closure
     * }
     */
    public static final AddressLayout closure$layout() {
        return closure$LAYOUT;
    }

    private static final long closure$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * void *closure
     * }
     */
    public static final long closure$offset() {
        return closure$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * void *closure
     * }
     */
    public static MemorySegment closure(MemorySegment struct) {
        return struct.get(closure$LAYOUT, closure$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * void *closure
     * }
     */
    public static void closure(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(closure$LAYOUT, closure$OFFSET, fieldValue);
    }

    /**
     * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
     * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
     */
    public static MemorySegment asSlice(MemorySegment array, long index) {
        return array.asSlice(layout().byteSize() * index);
    }

    /**
     * The size (in bytes) of this struct
     */
    public static long sizeof() { return layout().byteSize(); }

    /**
     * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
     */
    public static MemorySegment allocate(SegmentAllocator allocator) {
        return allocator.allocate(layout());
    }

    /**
     * Allocate an array of size {@code elementCount} using {@code allocator}.
     * The returned segment has size {@code elementCount * layout().byteSize()}.
     */
    public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
        return reinterpret(addr, 1, arena, cleanup);
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code elementCount * layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
        return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
    }
}
