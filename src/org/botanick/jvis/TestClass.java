package org.botanick.jvis;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by Bot_A_Nick with love on 5/18/2016.
 */
public class TestClass {
    private float floatValue = 42.3f;
    private int intValue = 42323;
    private long value = 4213L;
    private double dblValue = 32131231.0213712;
    @JsonIgnore
    private int ignoredField = 555;

    public float getFloatValue() {
        return floatValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getValue() {
        return value;
    }

    public double getDblValue() {
        return dblValue;
    }
}
