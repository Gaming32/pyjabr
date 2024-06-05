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
 *     PyObject ob_base;
 *     PyObject *cr_weakreflist;
 *     PyObject *cr_name;
 *     PyObject *cr_qualname;
 *     _PyErr_StackItem cr_exc_state;
 *     PyObject *cr_origin_or_finalizer;
 *     char cr_hooks_inited;
 *     char cr_closed;
 *     char cr_running_async;
 *     int8_t cr_frame_state;
 *     PyObject *cr_iframe[1];
 * }
 * }
 */
public class PyCoroObject {

    PyCoroObject() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        _object.layout().withName("ob_base"),
        Python_h.C_POINTER.withName("cr_weakreflist"),
        Python_h.C_POINTER.withName("cr_name"),
        Python_h.C_POINTER.withName("cr_qualname"),
        _err_stackitem.layout().withName("cr_exc_state"),
        Python_h.C_POINTER.withName("cr_origin_or_finalizer"),
        Python_h.C_CHAR.withName("cr_hooks_inited"),
        Python_h.C_CHAR.withName("cr_closed"),
        Python_h.C_CHAR.withName("cr_running_async"),
        Python_h.C_CHAR.withName("cr_frame_state"),
        MemoryLayout.paddingLayout(4),
        MemoryLayout.sequenceLayout(1, Python_h.C_POINTER).withName("cr_iframe")
    ).withName("$anon$52:9");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final GroupLayout ob_base$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("ob_base"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject ob_base
     * }
     */
    public static final GroupLayout ob_base$layout() {
        return ob_base$LAYOUT;
    }

    private static final long ob_base$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject ob_base
     * }
     */
    public static final long ob_base$offset() {
        return ob_base$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject ob_base
     * }
     */
    public static MemorySegment ob_base(MemorySegment struct) {
        return struct.asSlice(ob_base$OFFSET, ob_base$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject ob_base
     * }
     */
    public static void ob_base(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, ob_base$OFFSET, ob_base$LAYOUT.byteSize());
    }

    private static final AddressLayout cr_weakreflist$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("cr_weakreflist"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *cr_weakreflist
     * }
     */
    public static final AddressLayout cr_weakreflist$layout() {
        return cr_weakreflist$LAYOUT;
    }

    private static final long cr_weakreflist$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *cr_weakreflist
     * }
     */
    public static final long cr_weakreflist$offset() {
        return cr_weakreflist$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *cr_weakreflist
     * }
     */
    public static MemorySegment cr_weakreflist(MemorySegment struct) {
        return struct.get(cr_weakreflist$LAYOUT, cr_weakreflist$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *cr_weakreflist
     * }
     */
    public static void cr_weakreflist(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(cr_weakreflist$LAYOUT, cr_weakreflist$OFFSET, fieldValue);
    }

    private static final AddressLayout cr_name$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("cr_name"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *cr_name
     * }
     */
    public static final AddressLayout cr_name$layout() {
        return cr_name$LAYOUT;
    }

    private static final long cr_name$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *cr_name
     * }
     */
    public static final long cr_name$offset() {
        return cr_name$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *cr_name
     * }
     */
    public static MemorySegment cr_name(MemorySegment struct) {
        return struct.get(cr_name$LAYOUT, cr_name$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *cr_name
     * }
     */
    public static void cr_name(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(cr_name$LAYOUT, cr_name$OFFSET, fieldValue);
    }

    private static final AddressLayout cr_qualname$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("cr_qualname"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *cr_qualname
     * }
     */
    public static final AddressLayout cr_qualname$layout() {
        return cr_qualname$LAYOUT;
    }

    private static final long cr_qualname$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *cr_qualname
     * }
     */
    public static final long cr_qualname$offset() {
        return cr_qualname$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *cr_qualname
     * }
     */
    public static MemorySegment cr_qualname(MemorySegment struct) {
        return struct.get(cr_qualname$LAYOUT, cr_qualname$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *cr_qualname
     * }
     */
    public static void cr_qualname(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(cr_qualname$LAYOUT, cr_qualname$OFFSET, fieldValue);
    }

    private static final GroupLayout cr_exc_state$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("cr_exc_state"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _PyErr_StackItem cr_exc_state
     * }
     */
    public static final GroupLayout cr_exc_state$layout() {
        return cr_exc_state$LAYOUT;
    }

    private static final long cr_exc_state$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _PyErr_StackItem cr_exc_state
     * }
     */
    public static final long cr_exc_state$offset() {
        return cr_exc_state$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _PyErr_StackItem cr_exc_state
     * }
     */
    public static MemorySegment cr_exc_state(MemorySegment struct) {
        return struct.asSlice(cr_exc_state$OFFSET, cr_exc_state$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _PyErr_StackItem cr_exc_state
     * }
     */
    public static void cr_exc_state(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, cr_exc_state$OFFSET, cr_exc_state$LAYOUT.byteSize());
    }

    private static final AddressLayout cr_origin_or_finalizer$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("cr_origin_or_finalizer"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *cr_origin_or_finalizer
     * }
     */
    public static final AddressLayout cr_origin_or_finalizer$layout() {
        return cr_origin_or_finalizer$LAYOUT;
    }

    private static final long cr_origin_or_finalizer$OFFSET = 56;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *cr_origin_or_finalizer
     * }
     */
    public static final long cr_origin_or_finalizer$offset() {
        return cr_origin_or_finalizer$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *cr_origin_or_finalizer
     * }
     */
    public static MemorySegment cr_origin_or_finalizer(MemorySegment struct) {
        return struct.get(cr_origin_or_finalizer$LAYOUT, cr_origin_or_finalizer$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *cr_origin_or_finalizer
     * }
     */
    public static void cr_origin_or_finalizer(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(cr_origin_or_finalizer$LAYOUT, cr_origin_or_finalizer$OFFSET, fieldValue);
    }

    private static final OfByte cr_hooks_inited$LAYOUT = (OfByte)$LAYOUT.select(groupElement("cr_hooks_inited"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char cr_hooks_inited
     * }
     */
    public static final OfByte cr_hooks_inited$layout() {
        return cr_hooks_inited$LAYOUT;
    }

    private static final long cr_hooks_inited$OFFSET = 64;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char cr_hooks_inited
     * }
     */
    public static final long cr_hooks_inited$offset() {
        return cr_hooks_inited$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char cr_hooks_inited
     * }
     */
    public static byte cr_hooks_inited(MemorySegment struct) {
        return struct.get(cr_hooks_inited$LAYOUT, cr_hooks_inited$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char cr_hooks_inited
     * }
     */
    public static void cr_hooks_inited(MemorySegment struct, byte fieldValue) {
        struct.set(cr_hooks_inited$LAYOUT, cr_hooks_inited$OFFSET, fieldValue);
    }

    private static final OfByte cr_closed$LAYOUT = (OfByte)$LAYOUT.select(groupElement("cr_closed"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char cr_closed
     * }
     */
    public static final OfByte cr_closed$layout() {
        return cr_closed$LAYOUT;
    }

    private static final long cr_closed$OFFSET = 65;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char cr_closed
     * }
     */
    public static final long cr_closed$offset() {
        return cr_closed$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char cr_closed
     * }
     */
    public static byte cr_closed(MemorySegment struct) {
        return struct.get(cr_closed$LAYOUT, cr_closed$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char cr_closed
     * }
     */
    public static void cr_closed(MemorySegment struct, byte fieldValue) {
        struct.set(cr_closed$LAYOUT, cr_closed$OFFSET, fieldValue);
    }

    private static final OfByte cr_running_async$LAYOUT = (OfByte)$LAYOUT.select(groupElement("cr_running_async"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char cr_running_async
     * }
     */
    public static final OfByte cr_running_async$layout() {
        return cr_running_async$LAYOUT;
    }

    private static final long cr_running_async$OFFSET = 66;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char cr_running_async
     * }
     */
    public static final long cr_running_async$offset() {
        return cr_running_async$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char cr_running_async
     * }
     */
    public static byte cr_running_async(MemorySegment struct) {
        return struct.get(cr_running_async$LAYOUT, cr_running_async$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char cr_running_async
     * }
     */
    public static void cr_running_async(MemorySegment struct, byte fieldValue) {
        struct.set(cr_running_async$LAYOUT, cr_running_async$OFFSET, fieldValue);
    }

    private static final OfByte cr_frame_state$LAYOUT = (OfByte)$LAYOUT.select(groupElement("cr_frame_state"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int8_t cr_frame_state
     * }
     */
    public static final OfByte cr_frame_state$layout() {
        return cr_frame_state$LAYOUT;
    }

    private static final long cr_frame_state$OFFSET = 67;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int8_t cr_frame_state
     * }
     */
    public static final long cr_frame_state$offset() {
        return cr_frame_state$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int8_t cr_frame_state
     * }
     */
    public static byte cr_frame_state(MemorySegment struct) {
        return struct.get(cr_frame_state$LAYOUT, cr_frame_state$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int8_t cr_frame_state
     * }
     */
    public static void cr_frame_state(MemorySegment struct, byte fieldValue) {
        struct.set(cr_frame_state$LAYOUT, cr_frame_state$OFFSET, fieldValue);
    }

    private static final SequenceLayout cr_iframe$LAYOUT = (SequenceLayout)$LAYOUT.select(groupElement("cr_iframe"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static final SequenceLayout cr_iframe$layout() {
        return cr_iframe$LAYOUT;
    }

    private static final long cr_iframe$OFFSET = 72;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static final long cr_iframe$offset() {
        return cr_iframe$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static MemorySegment cr_iframe(MemorySegment struct) {
        return struct.asSlice(cr_iframe$OFFSET, cr_iframe$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static void cr_iframe(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, cr_iframe$OFFSET, cr_iframe$LAYOUT.byteSize());
    }

    private static long[] cr_iframe$DIMS = { 1 };

    /**
     * Dimensions for array field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static long[] cr_iframe$dimensions() {
        return cr_iframe$DIMS;
    }
    private static final VarHandle cr_iframe$ELEM_HANDLE = cr_iframe$LAYOUT.varHandle(sequenceElement());

    /**
     * Indexed getter for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static MemorySegment cr_iframe(MemorySegment struct, long index0) {
        return (MemorySegment)cr_iframe$ELEM_HANDLE.get(struct, 0L, index0);
    }

    /**
     * Indexed setter for field:
     * {@snippet lang=c :
     * PyObject *cr_iframe[1]
     * }
     */
    public static void cr_iframe(MemorySegment struct, long index0, MemorySegment fieldValue) {
        cr_iframe$ELEM_HANDLE.set(struct, 0L, index0, fieldValue);
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

