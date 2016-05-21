package org.botanick.jvis;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bot_A_Nick with love on 5/18/2016.
 */
public class TestClass {
    private float floatValue = 42.3f;
    private int intValue = 42323;
    private long value = 4213L;
    private double dblValue = 32131231.0213712;
    private String strVlaue = "lalala";

//    private final List<TestElement> children = Arrays.asList(new TestElement(), new TestElement());

    private TestElement element = new TestElement();
    private TestSubElement subElement = new TestSubElement();
    private TestSubElement subElement2 = new TestSubElement();
    private TestElement element2 = new TestElement();

    public int publicMorozoff = -32;

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

    public String getStrVlaue() {
        return strVlaue;
    }

    public TestElement getElement() {
        return element;
    }

    public TestElement getElement2() {
        return element2;
    }

    public TestSubElement getSubElement() {
        return subElement;
    }

    public TestSubElement getSubElement2() {
        return subElement2;
    }
}
