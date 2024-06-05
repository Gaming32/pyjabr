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
 * struct _stat64i32 {
 *     _dev_t st_dev;
 *     _ino_t st_ino;
 *     unsigned short st_mode;
 *     short st_nlink;
 *     short st_uid;
 *     short st_gid;
 *     _dev_t st_rdev;
 *     _off_t st_size;
 *     __time64_t st_atime;
 *     __time64_t st_mtime;
 *     __time64_t st_ctime;
 * }
 * }
 */
public class _stat64i32 {

    _stat64i32() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        Python_h.C_INT.withName("st_dev"),
        Python_h.C_SHORT.withName("st_ino"),
        Python_h.C_SHORT.withName("st_mode"),
        Python_h.C_SHORT.withName("st_nlink"),
        Python_h.C_SHORT.withName("st_uid"),
        Python_h.C_SHORT.withName("st_gid"),
        MemoryLayout.paddingLayout(2),
        Python_h.C_INT.withName("st_rdev"),
        Python_h.C_LONG.withName("st_size"),
        Python_h.C_LONG_LONG.withName("st_atime"),
        Python_h.C_LONG_LONG.withName("st_mtime"),
        Python_h.C_LONG_LONG.withName("st_ctime")
    ).withName("_stat64i32");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfInt st_dev$LAYOUT = (OfInt)$LAYOUT.select(groupElement("st_dev"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _dev_t st_dev
     * }
     */
    public static final OfInt st_dev$layout() {
        return st_dev$LAYOUT;
    }

    private static final long st_dev$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _dev_t st_dev
     * }
     */
    public static final long st_dev$offset() {
        return st_dev$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _dev_t st_dev
     * }
     */
    public static int st_dev(MemorySegment struct) {
        return struct.get(st_dev$LAYOUT, st_dev$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _dev_t st_dev
     * }
     */
    public static void st_dev(MemorySegment struct, int fieldValue) {
        struct.set(st_dev$LAYOUT, st_dev$OFFSET, fieldValue);
    }

    private static final OfShort st_ino$LAYOUT = (OfShort)$LAYOUT.select(groupElement("st_ino"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _ino_t st_ino
     * }
     */
    public static final OfShort st_ino$layout() {
        return st_ino$LAYOUT;
    }

    private static final long st_ino$OFFSET = 4;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _ino_t st_ino
     * }
     */
    public static final long st_ino$offset() {
        return st_ino$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _ino_t st_ino
     * }
     */
    public static short st_ino(MemorySegment struct) {
        return struct.get(st_ino$LAYOUT, st_ino$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _ino_t st_ino
     * }
     */
    public static void st_ino(MemorySegment struct, short fieldValue) {
        struct.set(st_ino$LAYOUT, st_ino$OFFSET, fieldValue);
    }

    private static final OfShort st_mode$LAYOUT = (OfShort)$LAYOUT.select(groupElement("st_mode"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * unsigned short st_mode
     * }
     */
    public static final OfShort st_mode$layout() {
        return st_mode$LAYOUT;
    }

    private static final long st_mode$OFFSET = 6;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * unsigned short st_mode
     * }
     */
    public static final long st_mode$offset() {
        return st_mode$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * unsigned short st_mode
     * }
     */
    public static short st_mode(MemorySegment struct) {
        return struct.get(st_mode$LAYOUT, st_mode$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * unsigned short st_mode
     * }
     */
    public static void st_mode(MemorySegment struct, short fieldValue) {
        struct.set(st_mode$LAYOUT, st_mode$OFFSET, fieldValue);
    }

    private static final OfShort st_nlink$LAYOUT = (OfShort)$LAYOUT.select(groupElement("st_nlink"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * short st_nlink
     * }
     */
    public static final OfShort st_nlink$layout() {
        return st_nlink$LAYOUT;
    }

    private static final long st_nlink$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * short st_nlink
     * }
     */
    public static final long st_nlink$offset() {
        return st_nlink$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * short st_nlink
     * }
     */
    public static short st_nlink(MemorySegment struct) {
        return struct.get(st_nlink$LAYOUT, st_nlink$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * short st_nlink
     * }
     */
    public static void st_nlink(MemorySegment struct, short fieldValue) {
        struct.set(st_nlink$LAYOUT, st_nlink$OFFSET, fieldValue);
    }

    private static final OfShort st_uid$LAYOUT = (OfShort)$LAYOUT.select(groupElement("st_uid"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * short st_uid
     * }
     */
    public static final OfShort st_uid$layout() {
        return st_uid$LAYOUT;
    }

    private static final long st_uid$OFFSET = 10;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * short st_uid
     * }
     */
    public static final long st_uid$offset() {
        return st_uid$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * short st_uid
     * }
     */
    public static short st_uid(MemorySegment struct) {
        return struct.get(st_uid$LAYOUT, st_uid$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * short st_uid
     * }
     */
    public static void st_uid(MemorySegment struct, short fieldValue) {
        struct.set(st_uid$LAYOUT, st_uid$OFFSET, fieldValue);
    }

    private static final OfShort st_gid$LAYOUT = (OfShort)$LAYOUT.select(groupElement("st_gid"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * short st_gid
     * }
     */
    public static final OfShort st_gid$layout() {
        return st_gid$LAYOUT;
    }

    private static final long st_gid$OFFSET = 12;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * short st_gid
     * }
     */
    public static final long st_gid$offset() {
        return st_gid$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * short st_gid
     * }
     */
    public static short st_gid(MemorySegment struct) {
        return struct.get(st_gid$LAYOUT, st_gid$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * short st_gid
     * }
     */
    public static void st_gid(MemorySegment struct, short fieldValue) {
        struct.set(st_gid$LAYOUT, st_gid$OFFSET, fieldValue);
    }

    private static final OfInt st_rdev$LAYOUT = (OfInt)$LAYOUT.select(groupElement("st_rdev"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _dev_t st_rdev
     * }
     */
    public static final OfInt st_rdev$layout() {
        return st_rdev$LAYOUT;
    }

    private static final long st_rdev$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _dev_t st_rdev
     * }
     */
    public static final long st_rdev$offset() {
        return st_rdev$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _dev_t st_rdev
     * }
     */
    public static int st_rdev(MemorySegment struct) {
        return struct.get(st_rdev$LAYOUT, st_rdev$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _dev_t st_rdev
     * }
     */
    public static void st_rdev(MemorySegment struct, int fieldValue) {
        struct.set(st_rdev$LAYOUT, st_rdev$OFFSET, fieldValue);
    }

    private static final OfInt st_size$LAYOUT = (OfInt)$LAYOUT.select(groupElement("st_size"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _off_t st_size
     * }
     */
    public static final OfInt st_size$layout() {
        return st_size$LAYOUT;
    }

    private static final long st_size$OFFSET = 20;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _off_t st_size
     * }
     */
    public static final long st_size$offset() {
        return st_size$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _off_t st_size
     * }
     */
    public static int st_size(MemorySegment struct) {
        return struct.get(st_size$LAYOUT, st_size$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _off_t st_size
     * }
     */
    public static void st_size(MemorySegment struct, int fieldValue) {
        struct.set(st_size$LAYOUT, st_size$OFFSET, fieldValue);
    }

    private static final OfLong st_atime$LAYOUT = (OfLong)$LAYOUT.select(groupElement("st_atime"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time64_t st_atime
     * }
     */
    public static final OfLong st_atime$layout() {
        return st_atime$LAYOUT;
    }

    private static final long st_atime$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time64_t st_atime
     * }
     */
    public static final long st_atime$offset() {
        return st_atime$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time64_t st_atime
     * }
     */
    public static long st_atime(MemorySegment struct) {
        return struct.get(st_atime$LAYOUT, st_atime$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time64_t st_atime
     * }
     */
    public static void st_atime(MemorySegment struct, long fieldValue) {
        struct.set(st_atime$LAYOUT, st_atime$OFFSET, fieldValue);
    }

    private static final OfLong st_mtime$LAYOUT = (OfLong)$LAYOUT.select(groupElement("st_mtime"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time64_t st_mtime
     * }
     */
    public static final OfLong st_mtime$layout() {
        return st_mtime$LAYOUT;
    }

    private static final long st_mtime$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time64_t st_mtime
     * }
     */
    public static final long st_mtime$offset() {
        return st_mtime$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time64_t st_mtime
     * }
     */
    public static long st_mtime(MemorySegment struct) {
        return struct.get(st_mtime$LAYOUT, st_mtime$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time64_t st_mtime
     * }
     */
    public static void st_mtime(MemorySegment struct, long fieldValue) {
        struct.set(st_mtime$LAYOUT, st_mtime$OFFSET, fieldValue);
    }

    private static final OfLong st_ctime$LAYOUT = (OfLong)$LAYOUT.select(groupElement("st_ctime"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * __time64_t st_ctime
     * }
     */
    public static final OfLong st_ctime$layout() {
        return st_ctime$LAYOUT;
    }

    private static final long st_ctime$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * __time64_t st_ctime
     * }
     */
    public static final long st_ctime$offset() {
        return st_ctime$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * __time64_t st_ctime
     * }
     */
    public static long st_ctime(MemorySegment struct) {
        return struct.get(st_ctime$LAYOUT, st_ctime$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * __time64_t st_ctime
     * }
     */
    public static void st_ctime(MemorySegment struct, long fieldValue) {
        struct.set(st_ctime$LAYOUT, st_ctime$OFFSET, fieldValue);
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

