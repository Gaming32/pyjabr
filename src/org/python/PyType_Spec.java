// Generated by jextract

package org.python;

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
 * struct {
 *     const char *name;
 *     int basicsize;
 *     int itemsize;
 *     unsigned int flags;
 *     PyType_Slot *slots;
 * }
 * }
 */
public class PyType_Spec {

    PyType_Spec() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        Python_h.C_POINTER.withName("name"),
        Python_h.C_INT.withName("basicsize"),
        Python_h.C_INT.withName("itemsize"),
        Python_h.C_INT.withName("flags"),
        MemoryLayout.paddingLayout(4),
        Python_h.C_POINTER.withName("slots")
    ).withName("$anon$348:9");

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

    private static final OfInt basicsize$LAYOUT = (OfInt)$LAYOUT.select(groupElement("basicsize"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int basicsize
     * }
     */
    public static final OfInt basicsize$layout() {
        return basicsize$LAYOUT;
    }

    private static final long basicsize$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int basicsize
     * }
     */
    public static final long basicsize$offset() {
        return basicsize$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int basicsize
     * }
     */
    public static int basicsize(MemorySegment struct) {
        return struct.get(basicsize$LAYOUT, basicsize$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int basicsize
     * }
     */
    public static void basicsize(MemorySegment struct, int fieldValue) {
        struct.set(basicsize$LAYOUT, basicsize$OFFSET, fieldValue);
    }

    private static final OfInt itemsize$LAYOUT = (OfInt)$LAYOUT.select(groupElement("itemsize"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int itemsize
     * }
     */
    public static final OfInt itemsize$layout() {
        return itemsize$LAYOUT;
    }

    private static final long itemsize$OFFSET = 12;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int itemsize
     * }
     */
    public static final long itemsize$offset() {
        return itemsize$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int itemsize
     * }
     */
    public static int itemsize(MemorySegment struct) {
        return struct.get(itemsize$LAYOUT, itemsize$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int itemsize
     * }
     */
    public static void itemsize(MemorySegment struct, int fieldValue) {
        struct.set(itemsize$LAYOUT, itemsize$OFFSET, fieldValue);
    }

    private static final OfInt flags$LAYOUT = (OfInt)$LAYOUT.select(groupElement("flags"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * unsigned int flags
     * }
     */
    public static final OfInt flags$layout() {
        return flags$LAYOUT;
    }

    private static final long flags$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * unsigned int flags
     * }
     */
    public static final long flags$offset() {
        return flags$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * unsigned int flags
     * }
     */
    public static int flags(MemorySegment struct) {
        return struct.get(flags$LAYOUT, flags$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * unsigned int flags
     * }
     */
    public static void flags(MemorySegment struct, int fieldValue) {
        struct.set(flags$LAYOUT, flags$OFFSET, fieldValue);
    }

    private static final AddressLayout slots$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("slots"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyType_Slot *slots
     * }
     */
    public static final AddressLayout slots$layout() {
        return slots$LAYOUT;
    }

    private static final long slots$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyType_Slot *slots
     * }
     */
    public static final long slots$offset() {
        return slots$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyType_Slot *slots
     * }
     */
    public static MemorySegment slots(MemorySegment struct) {
        return struct.get(slots$LAYOUT, slots$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyType_Slot *slots
     * }
     */
    public static void slots(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(slots$LAYOUT, slots$OFFSET, fieldValue);
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

