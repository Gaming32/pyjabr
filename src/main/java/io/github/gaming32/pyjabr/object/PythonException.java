package io.github.gaming32.pyjabr.object;

import com.google.common.collect.Iterables;
import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;

import java.lang.foreign.MemorySegment;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.*;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonException extends RuntimeException {
    private static final MemorySegment TRACEBACK_MODULE = PythonObject.ARENA.allocateFrom("traceback");
    private static final MemorySegment FORMAT_FUNCTION = PythonObject.ARENA.allocateFrom("format_exception");
    private static final MemorySegment BLANK_STRING = PythonObject.ARENA.allocateFrom("");
    private static final MemorySegment JOIN_METHOD = PythonObject.ARENA.allocateFrom("join");

    private static final ThreadLocal<Object> EXTENDING_STACK_TRACE = new ThreadLocal<>();

    private final String pythonClass;
    private final String pythonMessage;
    private final String pythonTraceback;
    private final PythonObject originalException;

    public PythonException(String pythonClass, String pythonMessage, String pythonTraceback, PythonObject originalException) {
        super(pythonClass + ": " + pythonMessage);
        this.pythonClass = pythonClass;
        this.pythonMessage = pythonMessage;
        this.pythonTraceback = pythonTraceback;
        this.originalException = originalException;
    }

    public static PythonException of(PythonObject pythonException) {
        return GilStateUtil.runPython(() -> new PythonException(
            getPythonClass(pythonException.borrow()),
            getPythonMessage(pythonException.borrow()),
            getPythonTraceback(pythonException.borrow()),
            pythonException
        ));
    }

    public static PythonException moveFromPython() {
        final MemorySegment exception = !PyGILState_GetThisThreadState().equals(MemorySegment.NULL)
            ? PyErr_GetRaisedException()
            : MemorySegment.NULL;
        if (exception.equals(MemorySegment.NULL)) {
            throw new IllegalStateException("PythonException.moveFromPython called without a raised exception");
        }
        final PythonException result = of(PythonObject.steal(exception));
        result.adaptStackTrace();
        return result;
    }

    private void adaptStackTrace() {
        if (EXTENDING_STACK_TRACE.get() != null) return;
        EXTENDING_STACK_TRACE.set(new Object());
        try {
            final StackTraceElement[] stackTrace = getStackTrace();
            final int chop = calculateChop(stackTrace);
            final StackTraceElement[] traceback = convertTracebackToStackTrace(originalException.getAttr("__traceback__"));
            StackTraceElement[] newStackTrace = Arrays.copyOf(traceback, traceback.length + stackTrace.length - chop);
            System.arraycopy(stackTrace, chop, newStackTrace, traceback.length, stackTrace.length - chop);

            try {
                initCause(originalException.getAttr("java_exception").asJavaObject(Throwable.class));
            } catch (Exception _) {
            }

            setStackTrace(newStackTrace);
        } catch (Exception _) {
            // Recursive exceptions are not thrown
        } finally {
            EXTENDING_STACK_TRACE.remove();
        }
    }

    private static int calculateChop(StackTraceElement[] stackTrace) {
        for (int chop = 0; chop < stackTrace.length; chop++) {
            if (!stackTrace[chop].getClassName().equals(PythonException.class.getName())) continue;
            if (!stackTrace[chop].getMethodName().equals("moveFromPython")) continue;
            return chop + 1;
        }
        return 0;
    }

    public static StackTraceElement[] convertTracebackToStackTrace(PythonObject traceback) {
        final Deque<StackTraceElement> result = new ArrayDeque<>();
        PythonObject tb = traceback;
        while (!tb.equals(PythonObjects.none())) {
            final PythonObject frame = tb.getAttr("tb_frame");
            final PythonObject code = frame.getAttr("f_code");

            String fileName = code.getAttr("co_filename").toString();
            final int slashIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
            if (slashIndex != -1) {
                fileName = fileName.substring(slashIndex + 1);
            }

            result.addFirst(new StackTraceElement(
                getQualifiedName(frame, code, fileName),
                code.getAttr("co_name").toString(),
                fileName,
                getLineNumber(tb, code)
            ));
            tb = tb.getAttr("tb_next");
        }
        return result.toArray(new StackTraceElement[0]);
    }

    private static String getQualifiedName(PythonObject frame, PythonObject code, String fileName) {
        String qualName;

        final PythonObject moduleName = frame.getAttr("f_globals")
            .callMethod("get", PythonObjects.str("__name__"));
        if (!moduleName.equals(PythonObjects.none())) {
            qualName = moduleName.toString();
        } else {
            qualName = fileName;
            int dotIndex = qualName.lastIndexOf('.');
            if (dotIndex != -1) {
                qualName = qualName.substring(0, dotIndex);
            }
        }

        final String inModuleName = code.getAttr("co_qualname").toString();
        final int dotIndex = inModuleName.lastIndexOf('.');
        if (dotIndex != -1) {
            qualName += '.' + inModuleName.substring(0, dotIndex);
        }

        return qualName;
    }

    private static int getLineNumber(PythonObject traceback, PythonObject code) {
        final int instructionIndex = traceback.getAttr("tb_lasti").asJavaObject(int.class);
        if (instructionIndex >= 0) {
            final PythonObject positionsGen = code.callMethod("co_positions");
            final PythonObject codePos = Iterables.get(positionsGen, instructionIndex / 2, null);
            if (codePos != null) {
                return codePos.getItem(0).asJavaObject(int.class);
            }
        }
        return traceback.getAttr("tb_lineno").asJavaObject(int.class);
    }

    private static String getPythonClass(MemorySegment pythonException) {
        final MemorySegment pythonType = Py_TYPE(pythonException);
        final MemorySegment typeName = PyType_GetQualName(pythonType);
        final MemorySegment typeNameUtf8 = PyUnicode_AsUTF8AndSize(typeName, MemorySegment.NULL);
        if (typeNameUtf8.equals(MemorySegment.NULL)) {
            Py_DecRef(typeName);
            PyErr_Clear();
            return "<failed to convert class name to Java string>";
        }
        final String result = typeNameUtf8.getString(0L);
        Py_DecRef(typeName);
        return result;
    }

    private static String getPythonMessage(MemorySegment pythonException) {
        final MemorySegment messageString = PyObject_Str(pythonException);
        if (messageString.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<exception str() failed>";
        }
        final MemorySegment argsUtf8String = PyUnicode_AsUTF8AndSize(messageString, MemorySegment.NULL);
        if (argsUtf8String.equals(MemorySegment.NULL)) {
            Py_DecRef(messageString);
            PyErr_Clear();
            return "<failed to convert message to Java string>";
        }
        final String result = argsUtf8String.getString(0L);
        Py_DecRef(messageString);
        return result;
    }

    private static String getPythonTraceback(MemorySegment pythonException) {
        final MemorySegment tracebackModule = PyImport_ImportModule(TRACEBACK_MODULE);
        if (tracebackModule.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to import traceback module>";
        }
        final MemorySegment formatFunction = PyObject_GetAttrString(tracebackModule, FORMAT_FUNCTION);
        Py_DecRef(tracebackModule);
        if (formatFunction.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to fine function traceback.format_exception>";
        }
        final MemorySegment stringList = PyObject_CallOneArg(formatFunction, pythonException);
        Py_DecRef(formatFunction);
        if (stringList.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to call traceback.format_exception>";
        }
        final MemorySegment blankString = PyUnicode_FromString(BLANK_STRING);
        final MemorySegment joinMethodName = PyUnicode_FromString(JOIN_METHOD);
        final MemorySegment resultString = PyObject_CallMethodOneArg(blankString, joinMethodName, stringList);
        Py_DecRef(stringList);
        Py_DecRef(blankString);
        Py_DecRef(joinMethodName);
        if (resultString.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to call str.join>";
        }
        final MemorySegment resultUtf8String = PyUnicode_AsUTF8AndSize(resultString, MemorySegment.NULL);
        if (resultUtf8String.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to convert traceback to Java string>";
        }
        final String result = resultUtf8String.getString(0L);
        Py_DecRef(resultString);
        return result.stripTrailing();
    }

    public String getPythonClass() {
        return pythonClass;
    }

    public String getPythonMessage() {
        return pythonMessage;
    }

    public String getPythonTraceback() {
        return pythonTraceback;
    }

    public PythonObject getOriginalException() {
        return originalException;
    }

    @Override
    public String toString() {
        return super.toString() + '\n' + pythonTraceback;
    }
}
