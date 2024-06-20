package io.github.gaming32.pyjabr.object;

import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;
import io.github.gaming32.pyjabr.lowlevel.cpython.Python_h;
import io.github.gaming32.pyjabr.lowlevel.interop.InteropConversions;
import io.github.gaming32.pyjabr.lowlevel.interop.InteropUtils;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.github.gaming32.pyjabr.lowlevel.GilStateUtil.runPython;
import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.*;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public final class PythonObject implements Iterable<PythonObject> {
    private static final Cleaner CLEANER = Cleaner.create();
    private static final Arena ARENA = Arena.ofAuto();

    private final ObjectHolder object;

    private PythonObject(MemorySegment raw) {
        object = new ObjectHolder(raw);
        CLEANER.register(this, object);
    }

    public static PythonObject steal(MemorySegment raw) {
        return new PythonObject(raw);
    }

    public static PythonObject checkAndSteal(MemorySegment raw) {
        if (raw.equals(MemorySegment.NULL)) {
            throw PythonException.moveFromPython();
        }
        return new PythonObject(raw);
    }

    public static PythonObject fromJavaObject(Object o) {
        return checkAndSteal(runPython(() -> InteropConversions.javaToPython(o)));
    }

    @Override
    public String toString() {
        return toString(Python_h::PyObject_Str);
    }

    public String repr() {
        return toString(Python_h::PyObject_Repr);
    }

    private String toString(Function<MemorySegment, MemorySegment> function) {
        return runPython(() -> {
            final MemorySegment resultStr = function.apply(borrow());
            if (resultStr.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            final String result = InteropUtils.getString(resultStr);
            Py_DecRef(resultStr);
            if (result == null) {
                throw PythonException.moveFromPython();
            }
            return result;
        });
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PythonObject pyObj && compare(pyObj, ComparisonOperator.EQUAL);
    }

    @Override
    public int hashCode() {
        return runPython(() -> {
            long result = PyObject_Hash(borrow());
            if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                if (PyErr_ExceptionMatches(PyExc_TypeError()) != 0) {
                    PyErr_Clear();
                    result = borrow().address();
                } else {
                    throw PythonException.moveFromPython();
                }
            }
            return Long.hashCode(result);
        });
    }

    public long hash() {
        return runPython(() -> {
            final long result = PyObject_Hash(borrow());
            if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            return result;
        });
    }

    public boolean compare(PythonObject to, ComparisonOperator operator) {
        // This optimization is also done in PyObject_RichCompareBool, but on the PyObject*
        if (this == to) {
            if (operator == ComparisonOperator.EQUAL) {
                return true;
            }
            if (operator == ComparisonOperator.NOT_EQUAL) {
                return false;
            }
        }
        return tristateToBoolean(runPython(() -> PyObject_RichCompareBool(borrow(), to.borrow(), operator.getConstant())));
    }

    public boolean isInstance(PythonObject type) {
        return tristateToBoolean(runPython(() -> PyObject_IsInstance(borrow(), type.borrow())));
    }

    public boolean isSubclass(PythonObject type) {
        return tristateToBoolean(runPython(() -> PyObject_IsSubclass(borrow(), type.borrow())));
    }

    public boolean isTrue() {
        return tristateToBoolean(runPython(() -> PyObject_IsTrue(borrow())));
    }

    public boolean not() {
        return tristateToBoolean(runPython(() -> PyObject_Not(borrow())));
    }

    private static boolean tristateToBoolean(int value) {
        if (value == -1) {
            throw PythonException.moveFromPython();
        }
        return value != 0;
    }

    public PythonObject call() {
        return checkAndSteal(runPython(() -> PyObject_CallNoArgs(borrow())));
    }

    public PythonObject call(PythonObject arg) {
        return checkAndSteal(runPython(() -> PyObject_CallOneArg(borrow(), arg.borrow())));
    }

    public PythonObject call(PythonObject... args) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment argsArray = arena.allocate(C_POINTER, args.length);
            for (int i = 0; i < args.length; i++) {
                argsArray.setAtIndex(C_POINTER, i, args[i].borrow());
            }
            final long nargsf = args.length | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return checkAndSteal(runPython(() -> PyObject_Vectorcall(borrow(), argsArray, nargsf, _Py_NULL())));
        }
    }

    public boolean isCallable() {
        return runPython(() -> PyCallable_Check(borrow())) != 0;
    }

    public boolean isNumber() {
        return runPython(() -> PyNumber_Check(borrow())) != 0;
    }

    public boolean isSequence() {
        return runPython(() -> PySequence_Check(borrow())) != 0;
    }

    public boolean isMapping() {
        return runPython(() -> PyMapping_Check(borrow())) != 0;
    }

    public boolean isIterator() {
        return runPython(() -> PyIter_Check(borrow())) != 0;
    }

    public PythonObject callMethod(String method) {
        return checkAndSteal(runPython(() ->
            PyObject_CallMethodNoArgs(borrow(), InteropConversions.createPythonString(method))
        ));
    }

    public PythonObject callMethod(String method, PythonObject arg) {
        return checkAndSteal(runPython(() ->
            PyObject_CallMethodOneArg(borrow(), InteropConversions.createPythonString(method), arg.borrow())
        ));
    }

    public PythonObject callMethod(String method, PythonObject... args) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment argsArray = arena.allocate(C_POINTER, args.length + 1);
            argsArray.setAtIndex(C_POINTER, 0, borrow());
            for (int i = 0; i < args.length; i++) {
                argsArray.setAtIndex(C_POINTER, i + 1, args[i].borrow());
            }
            final long nargsf = args.length | PY_VECTORCALL_ARGUMENTS_OFFSET();
            return checkAndSteal(runPython(() ->
                PyObject_VectorcallMethod(arena.allocateFrom(method), argsArray, nargsf, _Py_NULL())
            ));
        }
    }

    public PythonObject getAttr(String attr) {
        try (Arena arena = Arena.ofConfined()) {
            return checkAndSteal(runPython(() -> PyObject_GetAttrString(borrow(), arena.allocateFrom(attr))));
        }
    }

    public void setAttr(String attr, PythonObject value) {
        try (Arena arena = Arena.ofConfined()) {
            if (runPython(() -> PyObject_SetAttrString(borrow(), arena.allocateFrom(attr), value.borrow())) == -1) {
                throw PythonException.moveFromPython();
            }
        }
    }

    public boolean hasAttr(String attr) {
        return runPython(() -> {
            final MemorySegment result;
            try (Arena arena = Arena.ofConfined()) {
                result = PyObject_GetAttrString(borrow(), arena.allocateFrom(attr));
            }
            if (result.equals(MemorySegment.NULL)) {
                if (PyErr_ExceptionMatches(PyExc_AttributeError()) == 0) {
                    throw PythonException.moveFromPython();
                }
                return false;
            }
            return true;
        });
    }

    public void delAttr(String attr) {
        try (Arena arena = Arena.ofConfined()) {
            if (runPython(() -> PyObject_DelAttrString(borrow(), arena.allocateFrom(attr))) == -1) {
                throw PythonException.moveFromPython();
            }
        }
    }

    public List<PythonObject> dir() {
        return runPython(() -> {
            final MemorySegment result = PyObject_Dir(borrow());
            if (result.equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            try {
                final long size = PyList_Size(result);
                if (size == -1L) {
                    throw PythonException.moveFromPython();
                }
                if (size > Integer.MAX_VALUE - 8) {
                    throw new IllegalStateException("Object has too many entries");
                }
                final List<PythonObject> resultList = new ArrayList<>((int)size);
                for (int i = 0; i < size; i++) {
                    resultList.add(checkAndSteal(PyList_GetItem(result, i)));
                }
                return resultList;
            } finally {
                Py_DecRef(result);
            }
        });
    }

    public PythonObject dirList() {
        return checkAndSteal(runPython(() -> PyObject_Dir(borrow())));
    }

    public long len() {
        return runPython(() -> {
            final long result = PyObject_Length(borrow());
            if (result == -1L && !PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            return result;
        });
    }

    public PythonObject getItem(PythonObject key) {
        return checkAndSteal(runPython(() -> PyObject_GetItem(borrow(), key.borrow())));
    }

    public PythonObject getItem(long index) {
        return checkAndSteal(runPython(() -> PySequence_GetItem(borrow(), index)));
    }

    public PythonObject getItem(String key) {
        return checkAndSteal(runPython(() -> PyMapping_GetItemString(borrow(), ARENA.allocateFrom(key))));
    }

    public PythonObject getSlice(long start, long end) {
        return checkAndSteal(runPython(() -> PySequence_GetSlice(borrow(), start, end)));
    }

    public void setItem(PythonObject key, PythonObject value) {
        if (runPython(() -> PyObject_SetItem(borrow(), key.borrow(), value.borrow())) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void setItem(long index, PythonObject value) {
        if (runPython(() -> PySequence_SetItem(borrow(), index, value.borrow())) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void setItem(String key, PythonObject value) {
        if (runPython(() -> PyMapping_SetItemString(borrow(), ARENA.allocateFrom(key), value.borrow())) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void setSlice(long start, long end, PythonObject value) {
        if (runPython(() -> PySequence_SetSlice(borrow(), start, end, value.borrow())) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void delItem(PythonObject key) {
        if (runPython(() -> PyObject_DelItem(borrow(), key.borrow())) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void delItem(long index) {
        if (runPython(() -> PySequence_DelItem(borrow(), index)) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public void delSlice(long start, long end) {
        if (runPython(() -> PySequence_DelSlice(borrow(), start, end)) == -1) {
            throw PythonException.moveFromPython();
        }
    }

    public long count(PythonObject value) {
        return runPython(() -> PySequence_Count(borrow(), value.borrow()));
    }

    public long index(PythonObject value) {
        return runPython(() -> PySequence_Index(borrow(), value.borrow()));
    }

    public PythonObject list() {
        return checkAndSteal(runPython(() -> PySequence_List(borrow())));
    }

    public PythonObject tuple() {
        return checkAndSteal(runPython(() -> PySequence_Tuple(borrow())));
    }

    public PythonObject keysList() {
        return checkAndSteal(runPython(() -> PyMapping_Keys(borrow())));
    }

    public PythonObject valuesList() {
        return checkAndSteal(runPython(() -> PyMapping_Values(borrow())));
    }

    public PythonObject itemsList() {
        return checkAndSteal(runPython(() -> PyMapping_Values(borrow())));
    }

    public PythonObject getType() {
        return checkAndSteal(runPython(() -> PyObject_Type(borrow())));
    }

    @NotNull
    @Override
    public PythonIterator iterator() {
        return new PythonIterator(checkAndSteal(runPython(() -> PyObject_GetIter(borrow()))));
    }

    public SendResult send(PythonObject value) {
        try (Arena arena = Arena.ofConfined()) {
            final MemorySegment result = arena.allocate(C_POINTER, 1);
            final int sendResult = runPython(() -> PyIter_Send(borrow(), value.borrow(), result));
            if (sendResult == PYGEN_RETURN() || sendResult == PYGEN_NEXT()) {
                final SendResult.Type resultType = sendResult == PYGEN_RETURN()
                    ? SendResult.Type.RETURN
                    : SendResult.Type.YIELD;
                return new SendResult(resultType, PythonObject.steal(result.get(C_POINTER, 0)));
            }
        }
        throw PythonException.moveFromPython();
    }

    public <T> T asJavaLambda(Class<T> lambdaClass) {
        return runPython(() -> InteropConversions.createLambda(lambdaClass, borrow()));
    }

    public Object asJavaObject() {
        return InteropConversions.pythonToJava(borrow());
    }

    public <T> T asJavaObject(Class<T> target) {
        return target.cast(InteropConversions.pythonToJava(borrow(), target));
    }

    public PythonObject add(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Add);
    }

    public PythonObject subtract(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Subtract);
    }

    public PythonObject multiply(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Multiply);
    }

    public PythonObject matrixMultiply(PythonObject other) {
        return binOp(other, Python_h::PyNumber_MatrixMultiply);
    }

    public PythonObject floorDivide(PythonObject other) {
        return binOp(other, Python_h::PyNumber_FloorDivide);
    }

    public PythonObject trueDivide(PythonObject other) {
        return binOp(other, Python_h::PyNumber_TrueDivide);
    }

    public PythonObject modulus(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Remainder);
    }

    public Pair<PythonObject, PythonObject> divmod(PythonObject other) {
        final PythonObject result = divmodTuple(other);
        return Pair.of(
            result.getItem(PythonObjects.pythonInt(0)),
            result.getItem(PythonObjects.pythonInt(1))
        );
    }

    public PythonObject divmodTuple(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Divmod);
    }

    public PythonObject pow(PythonObject exp) {
        return pow(exp, PythonObjects.none());
    }

    public PythonObject pow(PythonObject exp, PythonObject mod) {
        return checkAndSteal(runPython(() -> PyNumber_Power(borrow(), exp.borrow(), mod.borrow())));
    }

    public PythonObject negate() {
        return unaryOp(Python_h::PyNumber_Negative);
    }

    public PythonObject positive() {
        return unaryOp(Python_h::PyNumber_Positive);
    }

    public PythonObject abs() {
        return unaryOp(Python_h::PyNumber_Negative);
    }

    public PythonObject bitInvert() {
        return unaryOp(Python_h::PyNumber_Invert);
    }

    public PythonObject leftShift(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Lshift);
    }

    public PythonObject rightShift(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Rshift);
    }

    public PythonObject bitAnd(PythonObject other) {
        return binOp(other, Python_h::PyNumber_And);
    }

    public PythonObject bitXor(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Xor);
    }

    public PythonObject bitOr(PythonObject other) {
        return binOp(other, Python_h::PyNumber_Or);
    }

    public PythonObject addAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceAdd);
    }

    public PythonObject subtractAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceSubtract);
    }

    public PythonObject multiplyAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceMultiply);
    }

    public PythonObject matrixMultiplyAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceMatrixMultiply);
    }

    public PythonObject floorDivideAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceFloorDivide);
    }

    public PythonObject trueDivideAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceTrueDivide);
    }

    public PythonObject modulusAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceRemainder);
    }

    public PythonObject powAndGet(PythonObject exp) {
        return powAndGet(exp, PythonObjects.none());
    }

    public PythonObject powAndGet(PythonObject exp, PythonObject mod) {
        return checkAndSteal(runPython(() -> PyNumber_InPlacePower(borrow(), exp.borrow(), mod.borrow())));
    }

    public PythonObject leftShiftAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceLshift);
    }

    public PythonObject rightShiftAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceRshift);
    }

    public PythonObject bitAndAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceAnd);
    }

    public PythonObject bitXorAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceXor);
    }

    public PythonObject bitOrAndGet(PythonObject other) {
        return binOp(other, Python_h::PyNumber_InPlaceOr);
    }

    private PythonObject unaryOp(UnaryOperator<MemorySegment> op) {
        return checkAndSteal(runPython(() -> op.apply(borrow())));
    }

    private PythonObject binOp(PythonObject other, BinaryOperator<MemorySegment> op) {
        return checkAndSteal(runPython(() -> op.apply(borrow(), other.borrow())));
    }

    public PythonObject toInt() {
        return checkAndSteal(runPython(() -> PyNumber_Long(borrow())));
    }

    public PythonObject toFloat() {
        return checkAndSteal(runPython(() -> PyNumber_Float(borrow())));
    }

    public PythonObject index() {
        return checkAndSteal(runPython(() -> PyNumber_Index(borrow())));
    }

    /**
     * @return The underlying {@code PyObject*}. The object's refcount is not incremented before being returned.
     */
    public MemorySegment borrow() {
        return object.object;
    }

    private static final class ObjectHolder implements Runnable {
        MemorySegment object;

        private ObjectHolder(MemorySegment object) {
            this.object = object;
        }

        @Override
        public void run() {
            if (object != null) {
                GilStateUtil.runPython(() -> Py_DecRef(object));
                object = null;
            }
        }
    }
}
