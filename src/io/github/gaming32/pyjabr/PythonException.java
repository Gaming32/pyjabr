package io.github.gaming32.pyjabr;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

import static io.github.gaming32.pyjabr.PythonUtil.PyObject_CallMethodOneArg;
import static io.github.gaming32.pyjabr.PythonUtil.Py_TYPE;
import static org.python.Python_h.*;

public class PythonException extends RuntimeException {
    private static final MemorySegment TRACEBACK_MODULE = Arena.global().allocateFrom("traceback");
    private static final MemorySegment FORMAT_FUNCTION = Arena.global().allocateFrom("format_exception");
    private static final MemorySegment BLANK_STRING = Arena.global().allocateFrom("");
    private static final MemorySegment JOIN_METHOD = Arena.global().allocateFrom("join");

    private final String pythonClass;
    private final String pythonMessage;
    private final String pythonTraceback;
    private MemorySegment originalException;

    public PythonException(String pythonClass, String pythonMessage, String pythonTraceback, MemorySegment originalException) {
        super(pythonClass + ": " + pythonMessage);
        this.pythonClass = pythonClass;
        this.pythonMessage = pythonMessage;
        this.pythonTraceback = pythonTraceback;
        this.originalException = originalException;
    }

    public static PythonException of(MemorySegment pythonException, boolean retainReference) {
        return new PythonException(
            getPythonClass(pythonException),
            getPythonMessage(pythonException),
            getPythonTraceback(pythonException),
            retainReference ? pythonException : null
        );
    }

    public static PythonException moveFromPython(boolean retainReference) {
        final MemorySegment exception = PyErr_GetRaisedException();
        if (exception.equals(MemorySegment.NULL)) {
            throw new IllegalStateException("PythonException.currentlyRaised called without raised");
        }
        final PythonException result = of(exception, retainReference);
        if (!retainReference) {
            Py_DecRef(exception);
        }
        return result;
    }

    private static String getPythonClass(MemorySegment pythonException) {
        final MemorySegment pythonType = Py_TYPE(pythonException);
        final MemorySegment typeName = PyType_GetQualName(pythonType);
        return typeName.getString(0L, StandardCharsets.UTF_8);
    }

    private static String getPythonMessage(MemorySegment pythonException) {
        final MemorySegment messageString = PyObject_Str(pythonException);
        if (messageString.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<exception str() failed>";
        }
        final MemorySegment argsUtf8String = PyUnicode_AsUTF8(messageString);
        if (argsUtf8String.equals(MemorySegment.NULL)) {
            Py_DecRef(messageString);
            PyErr_Clear();
            return "<failed to convert message to Java string>";
        }
        final String result = argsUtf8String.getString(0L, StandardCharsets.UTF_8);
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
        final MemorySegment resultUtf8String = PyUnicode_AsUTF8(resultString);
        if (resultUtf8String.equals(MemorySegment.NULL)) {
            PyErr_Clear();
            return "<failed to convert traceback to Java string>";
        }
        final String result = resultUtf8String.getString(0L, StandardCharsets.UTF_8);
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

    @Nullable
    public MemorySegment getOriginalException() {
        return originalException;
    }

    public PythonException clearOriginalException() {
        if (originalException == null) {
            return this;
        }
        Py_DecRef(originalException);
        originalException = null;
        return this;
    }

    @Nullable
    public MemorySegment acquireOriginalException() {
        final MemorySegment result = originalException;
        originalException = null;
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + '\n' + pythonTraceback;
    }
}
