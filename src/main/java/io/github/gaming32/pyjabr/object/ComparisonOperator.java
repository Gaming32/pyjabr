package io.github.gaming32.pyjabr.object;

import static org.python.Python_h.*;

public enum ComparisonOperator {
    LESS_THAN(Py_LT()),
    LESS_THEN_OR_EQUAL(Py_LE()),
    EQUAL(Py_EQ()),
    NOT_EQUAL(Py_NE()),
    GREATER_THAN(Py_GT()),
    GREATER_THAN_OR_EQUAL(Py_GE());

    private final int constant;

    ComparisonOperator(int constant) {
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }
}
