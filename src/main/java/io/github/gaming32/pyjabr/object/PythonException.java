package io.github.gaming32.pyjabr.object;

import io.github.gaming32.pyjabr.lowlevel.GilStateUtil;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.gaming32.pyjabr.lowlevel.PythonUtil.*;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonException extends RuntimeException {
    private static final MemorySegment TRACEBACK_MODULE = Arena.global().allocateFrom("traceback");
    private static final MemorySegment FORMAT_FUNCTION = Arena.global().allocateFrom("format_exception");
    private static final MemorySegment BLANK_STRING = Arena.global().allocateFrom("");
    private static final MemorySegment JOIN_METHOD = Arena.global().allocateFrom("join");

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
            final StackTraceElement[] extended = convertTracebackToStackTrace(originalException);
            final StackTraceElement[] newStackTrace = Arrays.copyOf(extended, extended.length + stackTrace.length - chop);
            System.arraycopy(stackTrace, chop, newStackTrace, extended.length, stackTrace.length - chop);
            setStackTrace(newStackTrace);
        } catch (PythonException _) {
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

    public static StackTraceElement[] convertTracebackToStackTrace(PythonObject exception) {
        final List<StackTraceElement> result = new ArrayList<>();
        final PythonObject tracebackList = PythonObjects.importModule("traceback")
            .callMethod("extract_tb", exception.getAttr("__traceback__"));
        for (final PythonObject frame : tracebackList) {
            String fileName = frame.getAttr("filename").toString().replace('\\', '/');
            final int slashIndex = fileName.lastIndexOf('/');
            if (slashIndex != -1) {
                fileName = fileName.substring(slashIndex + 1);
            }

            String moduleName = fileName;
            final int dotIndex = moduleName.lastIndexOf('.');
            if (dotIndex != -1) {
                moduleName = moduleName.substring(0, dotIndex);
            }

            result.add(new StackTraceElement(
                moduleName,
                frame.getAttr("name").toString(),
                fileName,
                frame.getAttr("lineno").asJavaObject(int.class)
            ));
        }
        return result.reversed().toArray(new StackTraceElement[0]);
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
