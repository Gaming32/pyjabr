package io.github.gaming32.pyjabr.python;

import com.google.common.collect.AbstractIterator;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;

import static org.python.Python_h.PyErr_Occurred;
import static org.python.Python_h.PyIter_Next;

public final class PythonIterator extends AbstractIterator<PythonObject> {
    private final PythonObject iterator;

    public PythonIterator(PythonObject iterator) {
        if (!iterator.isIterator()) {
            throw new IllegalArgumentException(iterator + " is not an iterator");
        }
        this.iterator = iterator;
    }

    public PythonObject getIterator() {
        return iterator;
    }

    @Nullable
    @Override
    protected PythonObject computeNext() {
        final MemorySegment result = PyIter_Next(iterator.borrow());
        if (result.equals(MemorySegment.NULL)) {
            if (!PyErr_Occurred().equals(MemorySegment.NULL)) {
                throw PythonException.moveFromPython();
            }
            return endOfData();
        }
        return PythonObject.steal(result);
    }

    public SendResult send(PythonObject value) {
        return iterator.send(value);
    }
}
