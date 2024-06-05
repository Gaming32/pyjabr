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
 * struct PyCodeObject {
 *     PyVarObject ob_base;
 *     PyObject *co_consts;
 *     PyObject *co_names;
 *     PyObject *co_exceptiontable;
 *     int co_flags;
 *     int co_argcount;
 *     int co_posonlyargcount;
 *     int co_kwonlyargcount;
 *     int co_stacksize;
 *     int co_firstlineno;
 *     int co_nlocalsplus;
 *     int co_framesize;
 *     int co_nlocals;
 *     int co_ncellvars;
 *     int co_nfreevars;
 *     uint32_t co_version;
 *     PyObject *co_localsplusnames;
 *     PyObject *co_localspluskinds;
 *     PyObject *co_filename;
 *     PyObject *co_name;
 *     PyObject *co_qualname;
 *     PyObject *co_linetable;
 *     PyObject *co_weakreflist;
 *     _PyCoCached *_co_cached;
 *     uint64_t _co_instrumentation_version;
 *     _PyCoMonitoringData *_co_monitoring;
 *     int _co_firsttraceable;
 *     void *co_extra;
 *     char co_code_adaptive[1];
 * }
 * }
 */
public class PyCodeObject {

    PyCodeObject() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        PyVarObject.layout().withName("ob_base"),
        Python_h.C_POINTER.withName("co_consts"),
        Python_h.C_POINTER.withName("co_names"),
        Python_h.C_POINTER.withName("co_exceptiontable"),
        Python_h.C_INT.withName("co_flags"),
        Python_h.C_INT.withName("co_argcount"),
        Python_h.C_INT.withName("co_posonlyargcount"),
        Python_h.C_INT.withName("co_kwonlyargcount"),
        Python_h.C_INT.withName("co_stacksize"),
        Python_h.C_INT.withName("co_firstlineno"),
        Python_h.C_INT.withName("co_nlocalsplus"),
        Python_h.C_INT.withName("co_framesize"),
        Python_h.C_INT.withName("co_nlocals"),
        Python_h.C_INT.withName("co_ncellvars"),
        Python_h.C_INT.withName("co_nfreevars"),
        Python_h.C_INT.withName("co_version"),
        Python_h.C_POINTER.withName("co_localsplusnames"),
        Python_h.C_POINTER.withName("co_localspluskinds"),
        Python_h.C_POINTER.withName("co_filename"),
        Python_h.C_POINTER.withName("co_name"),
        Python_h.C_POINTER.withName("co_qualname"),
        Python_h.C_POINTER.withName("co_linetable"),
        Python_h.C_POINTER.withName("co_weakreflist"),
        Python_h.C_POINTER.withName("_co_cached"),
        Python_h.C_LONG_LONG.withName("_co_instrumentation_version"),
        Python_h.C_POINTER.withName("_co_monitoring"),
        Python_h.C_INT.withName("_co_firsttraceable"),
        MemoryLayout.paddingLayout(4),
        Python_h.C_POINTER.withName("co_extra"),
        MemoryLayout.sequenceLayout(1, Python_h.C_CHAR).withName("co_code_adaptive"),
        MemoryLayout.paddingLayout(7)
    ).withName("PyCodeObject");

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
     * PyVarObject ob_base
     * }
     */
    public static final GroupLayout ob_base$layout() {
        return ob_base$LAYOUT;
    }

    private static final long ob_base$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyVarObject ob_base
     * }
     */
    public static final long ob_base$offset() {
        return ob_base$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyVarObject ob_base
     * }
     */
    public static MemorySegment ob_base(MemorySegment struct) {
        return struct.asSlice(ob_base$OFFSET, ob_base$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyVarObject ob_base
     * }
     */
    public static void ob_base(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, ob_base$OFFSET, ob_base$LAYOUT.byteSize());
    }

    private static final AddressLayout co_consts$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_consts"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_consts
     * }
     */
    public static final AddressLayout co_consts$layout() {
        return co_consts$LAYOUT;
    }

    private static final long co_consts$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_consts
     * }
     */
    public static final long co_consts$offset() {
        return co_consts$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_consts
     * }
     */
    public static MemorySegment co_consts(MemorySegment struct) {
        return struct.get(co_consts$LAYOUT, co_consts$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_consts
     * }
     */
    public static void co_consts(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_consts$LAYOUT, co_consts$OFFSET, fieldValue);
    }

    private static final AddressLayout co_names$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_names"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_names
     * }
     */
    public static final AddressLayout co_names$layout() {
        return co_names$LAYOUT;
    }

    private static final long co_names$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_names
     * }
     */
    public static final long co_names$offset() {
        return co_names$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_names
     * }
     */
    public static MemorySegment co_names(MemorySegment struct) {
        return struct.get(co_names$LAYOUT, co_names$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_names
     * }
     */
    public static void co_names(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_names$LAYOUT, co_names$OFFSET, fieldValue);
    }

    private static final AddressLayout co_exceptiontable$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_exceptiontable"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_exceptiontable
     * }
     */
    public static final AddressLayout co_exceptiontable$layout() {
        return co_exceptiontable$LAYOUT;
    }

    private static final long co_exceptiontable$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_exceptiontable
     * }
     */
    public static final long co_exceptiontable$offset() {
        return co_exceptiontable$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_exceptiontable
     * }
     */
    public static MemorySegment co_exceptiontable(MemorySegment struct) {
        return struct.get(co_exceptiontable$LAYOUT, co_exceptiontable$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_exceptiontable
     * }
     */
    public static void co_exceptiontable(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_exceptiontable$LAYOUT, co_exceptiontable$OFFSET, fieldValue);
    }

    private static final OfInt co_flags$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_flags"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_flags
     * }
     */
    public static final OfInt co_flags$layout() {
        return co_flags$LAYOUT;
    }

    private static final long co_flags$OFFSET = 48;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_flags
     * }
     */
    public static final long co_flags$offset() {
        return co_flags$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_flags
     * }
     */
    public static int co_flags(MemorySegment struct) {
        return struct.get(co_flags$LAYOUT, co_flags$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_flags
     * }
     */
    public static void co_flags(MemorySegment struct, int fieldValue) {
        struct.set(co_flags$LAYOUT, co_flags$OFFSET, fieldValue);
    }

    private static final OfInt co_argcount$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_argcount"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_argcount
     * }
     */
    public static final OfInt co_argcount$layout() {
        return co_argcount$LAYOUT;
    }

    private static final long co_argcount$OFFSET = 52;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_argcount
     * }
     */
    public static final long co_argcount$offset() {
        return co_argcount$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_argcount
     * }
     */
    public static int co_argcount(MemorySegment struct) {
        return struct.get(co_argcount$LAYOUT, co_argcount$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_argcount
     * }
     */
    public static void co_argcount(MemorySegment struct, int fieldValue) {
        struct.set(co_argcount$LAYOUT, co_argcount$OFFSET, fieldValue);
    }

    private static final OfInt co_posonlyargcount$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_posonlyargcount"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_posonlyargcount
     * }
     */
    public static final OfInt co_posonlyargcount$layout() {
        return co_posonlyargcount$LAYOUT;
    }

    private static final long co_posonlyargcount$OFFSET = 56;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_posonlyargcount
     * }
     */
    public static final long co_posonlyargcount$offset() {
        return co_posonlyargcount$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_posonlyargcount
     * }
     */
    public static int co_posonlyargcount(MemorySegment struct) {
        return struct.get(co_posonlyargcount$LAYOUT, co_posonlyargcount$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_posonlyargcount
     * }
     */
    public static void co_posonlyargcount(MemorySegment struct, int fieldValue) {
        struct.set(co_posonlyargcount$LAYOUT, co_posonlyargcount$OFFSET, fieldValue);
    }

    private static final OfInt co_kwonlyargcount$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_kwonlyargcount"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_kwonlyargcount
     * }
     */
    public static final OfInt co_kwonlyargcount$layout() {
        return co_kwonlyargcount$LAYOUT;
    }

    private static final long co_kwonlyargcount$OFFSET = 60;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_kwonlyargcount
     * }
     */
    public static final long co_kwonlyargcount$offset() {
        return co_kwonlyargcount$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_kwonlyargcount
     * }
     */
    public static int co_kwonlyargcount(MemorySegment struct) {
        return struct.get(co_kwonlyargcount$LAYOUT, co_kwonlyargcount$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_kwonlyargcount
     * }
     */
    public static void co_kwonlyargcount(MemorySegment struct, int fieldValue) {
        struct.set(co_kwonlyargcount$LAYOUT, co_kwonlyargcount$OFFSET, fieldValue);
    }

    private static final OfInt co_stacksize$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_stacksize"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_stacksize
     * }
     */
    public static final OfInt co_stacksize$layout() {
        return co_stacksize$LAYOUT;
    }

    private static final long co_stacksize$OFFSET = 64;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_stacksize
     * }
     */
    public static final long co_stacksize$offset() {
        return co_stacksize$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_stacksize
     * }
     */
    public static int co_stacksize(MemorySegment struct) {
        return struct.get(co_stacksize$LAYOUT, co_stacksize$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_stacksize
     * }
     */
    public static void co_stacksize(MemorySegment struct, int fieldValue) {
        struct.set(co_stacksize$LAYOUT, co_stacksize$OFFSET, fieldValue);
    }

    private static final OfInt co_firstlineno$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_firstlineno"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_firstlineno
     * }
     */
    public static final OfInt co_firstlineno$layout() {
        return co_firstlineno$LAYOUT;
    }

    private static final long co_firstlineno$OFFSET = 68;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_firstlineno
     * }
     */
    public static final long co_firstlineno$offset() {
        return co_firstlineno$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_firstlineno
     * }
     */
    public static int co_firstlineno(MemorySegment struct) {
        return struct.get(co_firstlineno$LAYOUT, co_firstlineno$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_firstlineno
     * }
     */
    public static void co_firstlineno(MemorySegment struct, int fieldValue) {
        struct.set(co_firstlineno$LAYOUT, co_firstlineno$OFFSET, fieldValue);
    }

    private static final OfInt co_nlocalsplus$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_nlocalsplus"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_nlocalsplus
     * }
     */
    public static final OfInt co_nlocalsplus$layout() {
        return co_nlocalsplus$LAYOUT;
    }

    private static final long co_nlocalsplus$OFFSET = 72;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_nlocalsplus
     * }
     */
    public static final long co_nlocalsplus$offset() {
        return co_nlocalsplus$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_nlocalsplus
     * }
     */
    public static int co_nlocalsplus(MemorySegment struct) {
        return struct.get(co_nlocalsplus$LAYOUT, co_nlocalsplus$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_nlocalsplus
     * }
     */
    public static void co_nlocalsplus(MemorySegment struct, int fieldValue) {
        struct.set(co_nlocalsplus$LAYOUT, co_nlocalsplus$OFFSET, fieldValue);
    }

    private static final OfInt co_framesize$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_framesize"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_framesize
     * }
     */
    public static final OfInt co_framesize$layout() {
        return co_framesize$LAYOUT;
    }

    private static final long co_framesize$OFFSET = 76;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_framesize
     * }
     */
    public static final long co_framesize$offset() {
        return co_framesize$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_framesize
     * }
     */
    public static int co_framesize(MemorySegment struct) {
        return struct.get(co_framesize$LAYOUT, co_framesize$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_framesize
     * }
     */
    public static void co_framesize(MemorySegment struct, int fieldValue) {
        struct.set(co_framesize$LAYOUT, co_framesize$OFFSET, fieldValue);
    }

    private static final OfInt co_nlocals$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_nlocals"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_nlocals
     * }
     */
    public static final OfInt co_nlocals$layout() {
        return co_nlocals$LAYOUT;
    }

    private static final long co_nlocals$OFFSET = 80;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_nlocals
     * }
     */
    public static final long co_nlocals$offset() {
        return co_nlocals$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_nlocals
     * }
     */
    public static int co_nlocals(MemorySegment struct) {
        return struct.get(co_nlocals$LAYOUT, co_nlocals$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_nlocals
     * }
     */
    public static void co_nlocals(MemorySegment struct, int fieldValue) {
        struct.set(co_nlocals$LAYOUT, co_nlocals$OFFSET, fieldValue);
    }

    private static final OfInt co_ncellvars$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_ncellvars"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_ncellvars
     * }
     */
    public static final OfInt co_ncellvars$layout() {
        return co_ncellvars$LAYOUT;
    }

    private static final long co_ncellvars$OFFSET = 84;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_ncellvars
     * }
     */
    public static final long co_ncellvars$offset() {
        return co_ncellvars$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_ncellvars
     * }
     */
    public static int co_ncellvars(MemorySegment struct) {
        return struct.get(co_ncellvars$LAYOUT, co_ncellvars$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_ncellvars
     * }
     */
    public static void co_ncellvars(MemorySegment struct, int fieldValue) {
        struct.set(co_ncellvars$LAYOUT, co_ncellvars$OFFSET, fieldValue);
    }

    private static final OfInt co_nfreevars$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_nfreevars"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int co_nfreevars
     * }
     */
    public static final OfInt co_nfreevars$layout() {
        return co_nfreevars$LAYOUT;
    }

    private static final long co_nfreevars$OFFSET = 88;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int co_nfreevars
     * }
     */
    public static final long co_nfreevars$offset() {
        return co_nfreevars$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int co_nfreevars
     * }
     */
    public static int co_nfreevars(MemorySegment struct) {
        return struct.get(co_nfreevars$LAYOUT, co_nfreevars$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int co_nfreevars
     * }
     */
    public static void co_nfreevars(MemorySegment struct, int fieldValue) {
        struct.set(co_nfreevars$LAYOUT, co_nfreevars$OFFSET, fieldValue);
    }

    private static final OfInt co_version$LAYOUT = (OfInt)$LAYOUT.select(groupElement("co_version"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * uint32_t co_version
     * }
     */
    public static final OfInt co_version$layout() {
        return co_version$LAYOUT;
    }

    private static final long co_version$OFFSET = 92;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * uint32_t co_version
     * }
     */
    public static final long co_version$offset() {
        return co_version$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * uint32_t co_version
     * }
     */
    public static int co_version(MemorySegment struct) {
        return struct.get(co_version$LAYOUT, co_version$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * uint32_t co_version
     * }
     */
    public static void co_version(MemorySegment struct, int fieldValue) {
        struct.set(co_version$LAYOUT, co_version$OFFSET, fieldValue);
    }

    private static final AddressLayout co_localsplusnames$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_localsplusnames"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_localsplusnames
     * }
     */
    public static final AddressLayout co_localsplusnames$layout() {
        return co_localsplusnames$LAYOUT;
    }

    private static final long co_localsplusnames$OFFSET = 96;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_localsplusnames
     * }
     */
    public static final long co_localsplusnames$offset() {
        return co_localsplusnames$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_localsplusnames
     * }
     */
    public static MemorySegment co_localsplusnames(MemorySegment struct) {
        return struct.get(co_localsplusnames$LAYOUT, co_localsplusnames$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_localsplusnames
     * }
     */
    public static void co_localsplusnames(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_localsplusnames$LAYOUT, co_localsplusnames$OFFSET, fieldValue);
    }

    private static final AddressLayout co_localspluskinds$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_localspluskinds"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_localspluskinds
     * }
     */
    public static final AddressLayout co_localspluskinds$layout() {
        return co_localspluskinds$LAYOUT;
    }

    private static final long co_localspluskinds$OFFSET = 104;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_localspluskinds
     * }
     */
    public static final long co_localspluskinds$offset() {
        return co_localspluskinds$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_localspluskinds
     * }
     */
    public static MemorySegment co_localspluskinds(MemorySegment struct) {
        return struct.get(co_localspluskinds$LAYOUT, co_localspluskinds$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_localspluskinds
     * }
     */
    public static void co_localspluskinds(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_localspluskinds$LAYOUT, co_localspluskinds$OFFSET, fieldValue);
    }

    private static final AddressLayout co_filename$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_filename"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_filename
     * }
     */
    public static final AddressLayout co_filename$layout() {
        return co_filename$LAYOUT;
    }

    private static final long co_filename$OFFSET = 112;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_filename
     * }
     */
    public static final long co_filename$offset() {
        return co_filename$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_filename
     * }
     */
    public static MemorySegment co_filename(MemorySegment struct) {
        return struct.get(co_filename$LAYOUT, co_filename$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_filename
     * }
     */
    public static void co_filename(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_filename$LAYOUT, co_filename$OFFSET, fieldValue);
    }

    private static final AddressLayout co_name$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_name"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_name
     * }
     */
    public static final AddressLayout co_name$layout() {
        return co_name$LAYOUT;
    }

    private static final long co_name$OFFSET = 120;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_name
     * }
     */
    public static final long co_name$offset() {
        return co_name$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_name
     * }
     */
    public static MemorySegment co_name(MemorySegment struct) {
        return struct.get(co_name$LAYOUT, co_name$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_name
     * }
     */
    public static void co_name(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_name$LAYOUT, co_name$OFFSET, fieldValue);
    }

    private static final AddressLayout co_qualname$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_qualname"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_qualname
     * }
     */
    public static final AddressLayout co_qualname$layout() {
        return co_qualname$LAYOUT;
    }

    private static final long co_qualname$OFFSET = 128;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_qualname
     * }
     */
    public static final long co_qualname$offset() {
        return co_qualname$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_qualname
     * }
     */
    public static MemorySegment co_qualname(MemorySegment struct) {
        return struct.get(co_qualname$LAYOUT, co_qualname$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_qualname
     * }
     */
    public static void co_qualname(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_qualname$LAYOUT, co_qualname$OFFSET, fieldValue);
    }

    private static final AddressLayout co_linetable$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_linetable"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_linetable
     * }
     */
    public static final AddressLayout co_linetable$layout() {
        return co_linetable$LAYOUT;
    }

    private static final long co_linetable$OFFSET = 136;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_linetable
     * }
     */
    public static final long co_linetable$offset() {
        return co_linetable$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_linetable
     * }
     */
    public static MemorySegment co_linetable(MemorySegment struct) {
        return struct.get(co_linetable$LAYOUT, co_linetable$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_linetable
     * }
     */
    public static void co_linetable(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_linetable$LAYOUT, co_linetable$OFFSET, fieldValue);
    }

    private static final AddressLayout co_weakreflist$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_weakreflist"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * PyObject *co_weakreflist
     * }
     */
    public static final AddressLayout co_weakreflist$layout() {
        return co_weakreflist$LAYOUT;
    }

    private static final long co_weakreflist$OFFSET = 144;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * PyObject *co_weakreflist
     * }
     */
    public static final long co_weakreflist$offset() {
        return co_weakreflist$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * PyObject *co_weakreflist
     * }
     */
    public static MemorySegment co_weakreflist(MemorySegment struct) {
        return struct.get(co_weakreflist$LAYOUT, co_weakreflist$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * PyObject *co_weakreflist
     * }
     */
    public static void co_weakreflist(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_weakreflist$LAYOUT, co_weakreflist$OFFSET, fieldValue);
    }

    private static final AddressLayout _co_cached$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("_co_cached"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _PyCoCached *_co_cached
     * }
     */
    public static final AddressLayout _co_cached$layout() {
        return _co_cached$LAYOUT;
    }

    private static final long _co_cached$OFFSET = 152;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _PyCoCached *_co_cached
     * }
     */
    public static final long _co_cached$offset() {
        return _co_cached$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _PyCoCached *_co_cached
     * }
     */
    public static MemorySegment _co_cached(MemorySegment struct) {
        return struct.get(_co_cached$LAYOUT, _co_cached$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _PyCoCached *_co_cached
     * }
     */
    public static void _co_cached(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(_co_cached$LAYOUT, _co_cached$OFFSET, fieldValue);
    }

    private static final OfLong _co_instrumentation_version$LAYOUT = (OfLong)$LAYOUT.select(groupElement("_co_instrumentation_version"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * uint64_t _co_instrumentation_version
     * }
     */
    public static final OfLong _co_instrumentation_version$layout() {
        return _co_instrumentation_version$LAYOUT;
    }

    private static final long _co_instrumentation_version$OFFSET = 160;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * uint64_t _co_instrumentation_version
     * }
     */
    public static final long _co_instrumentation_version$offset() {
        return _co_instrumentation_version$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * uint64_t _co_instrumentation_version
     * }
     */
    public static long _co_instrumentation_version(MemorySegment struct) {
        return struct.get(_co_instrumentation_version$LAYOUT, _co_instrumentation_version$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * uint64_t _co_instrumentation_version
     * }
     */
    public static void _co_instrumentation_version(MemorySegment struct, long fieldValue) {
        struct.set(_co_instrumentation_version$LAYOUT, _co_instrumentation_version$OFFSET, fieldValue);
    }

    private static final AddressLayout _co_monitoring$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("_co_monitoring"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * _PyCoMonitoringData *_co_monitoring
     * }
     */
    public static final AddressLayout _co_monitoring$layout() {
        return _co_monitoring$LAYOUT;
    }

    private static final long _co_monitoring$OFFSET = 168;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * _PyCoMonitoringData *_co_monitoring
     * }
     */
    public static final long _co_monitoring$offset() {
        return _co_monitoring$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * _PyCoMonitoringData *_co_monitoring
     * }
     */
    public static MemorySegment _co_monitoring(MemorySegment struct) {
        return struct.get(_co_monitoring$LAYOUT, _co_monitoring$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * _PyCoMonitoringData *_co_monitoring
     * }
     */
    public static void _co_monitoring(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(_co_monitoring$LAYOUT, _co_monitoring$OFFSET, fieldValue);
    }

    private static final OfInt _co_firsttraceable$LAYOUT = (OfInt)$LAYOUT.select(groupElement("_co_firsttraceable"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * int _co_firsttraceable
     * }
     */
    public static final OfInt _co_firsttraceable$layout() {
        return _co_firsttraceable$LAYOUT;
    }

    private static final long _co_firsttraceable$OFFSET = 176;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * int _co_firsttraceable
     * }
     */
    public static final long _co_firsttraceable$offset() {
        return _co_firsttraceable$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * int _co_firsttraceable
     * }
     */
    public static int _co_firsttraceable(MemorySegment struct) {
        return struct.get(_co_firsttraceable$LAYOUT, _co_firsttraceable$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * int _co_firsttraceable
     * }
     */
    public static void _co_firsttraceable(MemorySegment struct, int fieldValue) {
        struct.set(_co_firsttraceable$LAYOUT, _co_firsttraceable$OFFSET, fieldValue);
    }

    private static final AddressLayout co_extra$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("co_extra"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * void *co_extra
     * }
     */
    public static final AddressLayout co_extra$layout() {
        return co_extra$LAYOUT;
    }

    private static final long co_extra$OFFSET = 184;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * void *co_extra
     * }
     */
    public static final long co_extra$offset() {
        return co_extra$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * void *co_extra
     * }
     */
    public static MemorySegment co_extra(MemorySegment struct) {
        return struct.get(co_extra$LAYOUT, co_extra$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * void *co_extra
     * }
     */
    public static void co_extra(MemorySegment struct, MemorySegment fieldValue) {
        struct.set(co_extra$LAYOUT, co_extra$OFFSET, fieldValue);
    }

    private static final SequenceLayout co_code_adaptive$LAYOUT = (SequenceLayout)$LAYOUT.select(groupElement("co_code_adaptive"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static final SequenceLayout co_code_adaptive$layout() {
        return co_code_adaptive$LAYOUT;
    }

    private static final long co_code_adaptive$OFFSET = 192;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static final long co_code_adaptive$offset() {
        return co_code_adaptive$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static MemorySegment co_code_adaptive(MemorySegment struct) {
        return struct.asSlice(co_code_adaptive$OFFSET, co_code_adaptive$LAYOUT.byteSize());
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static void co_code_adaptive(MemorySegment struct, MemorySegment fieldValue) {
        MemorySegment.copy(fieldValue, 0L, struct, co_code_adaptive$OFFSET, co_code_adaptive$LAYOUT.byteSize());
    }

    private static long[] co_code_adaptive$DIMS = { 1 };

    /**
     * Dimensions for array field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static long[] co_code_adaptive$dimensions() {
        return co_code_adaptive$DIMS;
    }
    private static final VarHandle co_code_adaptive$ELEM_HANDLE = co_code_adaptive$LAYOUT.varHandle(sequenceElement());

    /**
     * Indexed getter for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static byte co_code_adaptive(MemorySegment struct, long index0) {
        return (byte)co_code_adaptive$ELEM_HANDLE.get(struct, 0L, index0);
    }

    /**
     * Indexed setter for field:
     * {@snippet lang=c :
     * char co_code_adaptive[1]
     * }
     */
    public static void co_code_adaptive(MemorySegment struct, long index0, byte fieldValue) {
        co_code_adaptive$ELEM_HANDLE.set(struct, 0L, index0, fieldValue);
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

