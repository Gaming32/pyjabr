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
 *     void *buf;
 *     PyObject *obj;
 *     Py_ssize_t len;
 *     Py_ssize_t itemsize;
 *     int readonly;
 *     int ndim;
 *     char *format;
 *     Py_ssize_t *shape;
 *     Py_ssize_t *strides;
 *     Py_ssize_t *suboffsets;
 *     void *internal;
 * }
 * }
 */
public class Py_buffer {

    Py_buffer() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        Python_h.C_POINTER.withName("buf"),
        Python_h.C_POINTER.withName("obj"),
        Python_h.C_LONG_LONG.withName("len"),
        Python_h.C_LONG_LONG.withName("itemsize"),
        Python_h.C_INT.withName("readonly"),
        Python_h.C_INT.withName("ndim"),
        Python_h.C_POINTER.withName("format"),
        Python_h.C_POINTER.withName("shape"),
        Python_h.C_POINTER.withName("strides"),
        Python_h.C_POINTER.withName("suboffsets"),
        Python_h.C_POINTER.withName("internal")
    ).withName("$anon$20:9");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final AddressLayout buf$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("buf"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * void *buf
     * }
     */
    public static final AddressLayout buf$layout() {
        return buf$LAYOUT;
    }

    private static final long buf$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * void *buf
     * }
     */
    public static final long buf$offset() {
        return buf$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * void *buf
     * }
     */
    public static MemorySegment buf(MemorySegment struct) {
        return struct.get(buf$LAYOUT, buf$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * void *buf
     * }
     */
    public static void buf(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(buf$LAYOUT, buf$OFFSET, fieldValue);
    }

    private static final AddressLayout obj$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("obj"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *obj
     * }
     */
    public static final AddressLayout obj$layout() {
        return obj$LAYOUT;
    }

    private static final long obj$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *obj
     * }
     */
    public static final long obj$offset() {
        return obj$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *obj
     * }
     */
    public static MemorySegment obj(MemorySegment struct) {
        return struct.get(obj$LAYOUT, obj$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *obj
     * }
     */
    public static void obj(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(obj$LAYOUT, obj$OFFSET, fieldValue);
    }

    private static final OfLong len$LAYOUT = (OfLong)$LAYOUT.select(groupElement("len"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * Py_ssize_t len
     * }
     */
    public static final OfLong len$layout() {
        return len$LAYOUT;
    }

    private static final long len$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * Py_ssize_t len
     * }
     */
    public static final long len$offset() {
        return len$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * Py_ssize_t len
     * }
     */
    public static long len(MemorySegment struct) {
        return struct.get(len$LAYOUT, len$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * Py_ssize_t len
     * }
     */
    public static void len(MemorySegment struct, long fieldValue) {
        struct.set(len$LAYOUT, len$OFFSET, fieldValue);
    }

    private static final OfLong itemsize$LAYOUT = (OfLong)$LAYOUT.select(groupElement("itemsize"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * Py_ssize_t itemsize
     * }
     */
    public static final OfLong itemsize$layout() {
        return itemsize$LAYOUT;
    }

    private static final long itemsize$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * Py_ssize_t itemsize
     * }
     */
    public static final long itemsize$offset() {
        return itemsize$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * Py_ssize_t itemsize
     * }
     */
    public static long itemsize(MemorySegment struct) {
        return struct.get(itemsize$LAYOUT, itemsize$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * Py_ssize_t itemsize
     * }
     */
    public static void itemsize(MemorySegment struct, long fieldValue) {
        struct.set(itemsize$LAYOUT, itemsize$OFFSET, fieldValue);
    }

    private static final OfInt readonly$LAYOUT = (OfInt)$LAYOUT.select(groupElement("readonly"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int readonly
     * }
     */
    public static final OfInt readonly$layout() {
        return readonly$LAYOUT;
    }

    private static final long readonly$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int readonly
     * }
     */
    public static final long readonly$offset() {
        return readonly$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int readonly
     * }
     */
    public static int readonly(MemorySegment struct) {
        return struct.get(readonly$LAYOUT, readonly$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int readonly
     * }
     */
    public static void readonly(MemorySegment struct, int fieldValue) {
        struct.set(readonly$LAYOUT, readonly$OFFSET, fieldValue);
    }

    private static final OfInt ndim$LAYOUT = (OfInt)$LAYOUT.select(groupElement("ndim"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int ndim
     * }
     */
    public static final OfInt ndim$layout() {
        return ndim$LAYOUT;
    }

    private static final long ndim$OFFSET = 36;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int ndim
     * }
     */
    public static final long ndim$offset() {
        return ndim$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int ndim
     * }
     */
    public static int ndim(MemorySegment struct) {
        return struct.get(ndim$LAYOUT, ndim$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int ndim
     * }
     */
    public static void ndim(MemorySegment struct, int fieldValue) {
        struct.set(ndim$LAYOUT, ndim$OFFSET, fieldValue);
    }

    private static final AddressLayout format$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("format"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char *format
     * }
     */
    public static final AddressLayout format$layout() {
        return format$LAYOUT;
    }

    private static final long format$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char *format
     * }
     */
    public static final long format$offset() {
        return format$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char *format
     * }
     */
    public static MemorySegment format(MemorySegment struct) {
        return struct.get(format$LAYOUT, format$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char *format
     * }
     */
    public static void format(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(format$LAYOUT, format$OFFSET, fieldValue);
    }

    private static final AddressLayout shape$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("shape"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * Py_ssize_t *shape
     * }
     */
    public static final AddressLayout shape$layout() {
        return shape$LAYOUT;
    }

    private static final long shape$OFFSET = 48;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * Py_ssize_t *shape
     * }
     */
    public static final long shape$offset() {
        return shape$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * Py_ssize_t *shape
     * }
     */
    public static MemorySegment shape(MemorySegment struct) {
        return struct.get(shape$LAYOUT, shape$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * Py_ssize_t *shape
     * }
     */
    public static void shape(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(shape$LAYOUT, shape$OFFSET, fieldValue);
    }

    private static final AddressLayout strides$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("strides"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * Py_ssize_t *strides
     * }
     */
    public static final AddressLayout strides$layout() {
        return strides$LAYOUT;
    }

    private static final long strides$OFFSET = 56;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * Py_ssize_t *strides
     * }
     */
    public static final long strides$offset() {
        return strides$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * Py_ssize_t *strides
     * }
     */
    public static MemorySegment strides(MemorySegment struct) {
        return struct.get(strides$LAYOUT, strides$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * Py_ssize_t *strides
     * }
     */
    public static void strides(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(strides$LAYOUT, strides$OFFSET, fieldValue);
    }

    private static final AddressLayout suboffsets$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("suboffsets"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * Py_ssize_t *suboffsets
     * }
     */
    public static final AddressLayout suboffsets$layout() {
        return suboffsets$LAYOUT;
    }

    private static final long suboffsets$OFFSET = 64;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * Py_ssize_t *suboffsets
     * }
     */
    public static final long suboffsets$offset() {
        return suboffsets$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * Py_ssize_t *suboffsets
     * }
     */
    public static MemorySegment suboffsets(MemorySegment struct) {
        return struct.get(suboffsets$LAYOUT, suboffsets$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * Py_ssize_t *suboffsets
     * }
     */
    public static void suboffsets(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(suboffsets$LAYOUT, suboffsets$OFFSET, fieldValue);
    }

    private static final AddressLayout internal$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("internal"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * void *internal
     * }
     */
    public static final AddressLayout internal$layout() {
        return internal$LAYOUT;
    }

    private static final long internal$OFFSET = 72;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * void *internal
     * }
     */
    public static final long internal$offset() {
        return internal$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * void *internal
     * }
     */
    public static MemorySegment internal(MemorySegment struct) {
        return struct.get(internal$LAYOUT, internal$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * void *internal
     * }
     */
    public static void internal(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(internal$LAYOUT, internal$OFFSET, fieldValue);
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

