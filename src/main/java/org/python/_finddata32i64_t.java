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
 * struct _finddata32i64_t {
 *     unsigned int attrib;
 *     __time32_t time_create;
 *     __time32_t time_access;
 *     __time32_t time_write;
 *     long long size;
 *     char name[260];
 * }
 * }
 */
public class _finddata32i64_t {

    _finddata32i64_t() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        Python_h.C_INT.withName("attrib"),
        Python_h.C_LONG.withName("time_create"),
        Python_h.C_LONG.withName("time_access"),
        Python_h.C_LONG.withName("time_write"),
        Python_h.C_LONG_LONG.withName("size"),
        MemoryLayout.sequenceLayout(260, Python_h.C_CHAR).withName("name"),
        MemoryLayout.paddingLayout(4)
    ).withName("_finddata32i64_t");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfInt attrib$LAYOUT = (OfInt)$LAYOUT.select(groupElement("attrib"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * unsigned int attrib
     * }
     */
    public static final OfInt attrib$layout() {
        return attrib$LAYOUT;
    }

    private static final long attrib$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * unsigned int attrib
     * }
     */
    public static final long attrib$offset() {
        return attrib$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * unsigned int attrib
     * }
     */
    public static int attrib(MemorySegment struct) {
        return struct.get(attrib$LAYOUT, attrib$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * unsigned int attrib
     * }
     */
    public static void attrib(MemorySegment struct, int fieldValue) {
        struct.set(attrib$LAYOUT, attrib$OFFSET, fieldValue);
    }

    private static final OfInt time_create$LAYOUT = (OfInt)$LAYOUT.select(groupElement("time_create"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time32_t time_create
     * }
     */
    public static final OfInt time_create$layout() {
        return time_create$LAYOUT;
    }

    private static final long time_create$OFFSET = 4;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time32_t time_create
     * }
     */
    public static final long time_create$offset() {
        return time_create$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time32_t time_create
     * }
     */
    public static int time_create(MemorySegment struct) {
        return struct.get(time_create$LAYOUT, time_create$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time32_t time_create
     * }
     */
    public static void time_create(MemorySegment struct, int fieldValue) {
        struct.set(time_create$LAYOUT, time_create$OFFSET, fieldValue);
    }

    private static final OfInt time_access$LAYOUT = (OfInt)$LAYOUT.select(groupElement("time_access"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time32_t time_access
     * }
     */
    public static final OfInt time_access$layout() {
        return time_access$LAYOUT;
    }

    private static final long time_access$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time32_t time_access
     * }
     */
    public static final long time_access$offset() {
        return time_access$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time32_t time_access
     * }
     */
    public static int time_access(MemorySegment struct) {
        return struct.get(time_access$LAYOUT, time_access$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time32_t time_access
     * }
     */
    public static void time_access(MemorySegment struct, int fieldValue) {
        struct.set(time_access$LAYOUT, time_access$OFFSET, fieldValue);
    }

    private static final OfInt time_write$LAYOUT = (OfInt)$LAYOUT.select(groupElement("time_write"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time32_t time_write
     * }
     */
    public static final OfInt time_write$layout() {
        return time_write$LAYOUT;
    }

    private static final long time_write$OFFSET = 12;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time32_t time_write
     * }
     */
    public static final long time_write$offset() {
        return time_write$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time32_t time_write
     * }
     */
    public static int time_write(MemorySegment struct) {
        return struct.get(time_write$LAYOUT, time_write$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time32_t time_write
     * }
     */
    public static void time_write(MemorySegment struct, int fieldValue) {
        struct.set(time_write$LAYOUT, time_write$OFFSET, fieldValue);
    }

    private static final OfLong size$LAYOUT = (OfLong)$LAYOUT.select(groupElement("size"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * long long size
     * }
     */
    public static final OfLong size$layout() {
        return size$LAYOUT;
    }

    private static final long size$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * long long size
     * }
     */
    public static final long size$offset() {
        return size$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * long long size
     * }
     */
    public static long size(MemorySegment struct) {
        return struct.get(size$LAYOUT, size$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * long long size
     * }
     */
    public static void size(MemorySegment struct, long fieldValue) {
        struct.set(size$LAYOUT, size$OFFSET, fieldValue);
    }

    private static final SequenceLayout name$LAYOUT = (SequenceLayout)$LAYOUT.select(groupElement("name"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static final SequenceLayout name$layout() {
        return name$LAYOUT;
    }

    private static final long name$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static final long name$offset() {
        return name$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static MemorySegment name(MemorySegment struct) {
        return struct.asSlice(name$OFFSET, name$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static void name(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, name$OFFSET, name$LAYOUT.byteSize());
    }

    private static long[] name$DIMS = { 260 };

    /**
     * Dimensions for array field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static long[] name$dimensions() {
        return name$DIMS;
    }
    private static final VarHandle name$ELEM_HANDLE = name$LAYOUT.varHandle(sequenceElement());

    /**
     * Indexed getter for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static byte name(MemorySegment struct, long index0) {
        return (byte)name$ELEM_HANDLE.get(struct, 0L, index0);
    }

    /**
     * Indexed setter for field:
     * {@snippet lang=c :
     * char name[260]
     * }
     */
    public static void name(MemorySegment struct, long index0, byte fieldValue) {
        name$ELEM_HANDLE.set(struct, 0L, index0, fieldValue);
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
