package org.xmlcml.cml.base;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.DoubleArrayAttribute;
import org.xmlcml.euclid.test.DoubleTest;

/**
 * tests for doubleAttribute.
 * 
 * @author pmr
 * 
 */
public class DoubleArrayAttributeTest extends AttributeBaseTest {

    DoubleArrayAttribute daa1;

    DoubleArrayAttribute daa2;

    /**
     * main
     * 
     * @param args
     */
    /**
     * setup.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        daa1 = new DoubleArrayAttribute(new CMLAttribute("foo"), " 1.2   3.4  ");

    }

    /**
     * Test method for 'org.xmlcml.cml.base.DoubleArrayAttribute.getCMLValue()'
     */
    @Test
    public void testGetCMLValue() {
        // Assert.assertEquals("get CMLValue", "1.2 3.4", daa1.getCMLValue());
        Assert.assertNull("get CMLValue", daa1.getCMLValue());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleArrayAttribute.setCMLValue(String)'
     */
    @Test
    public void testSetCMLValueString() {
        daa1.setCMLValue("3.4  5.6");
        double[] dd = (double[]) daa1.getCMLValue();
        DoubleTest.assertEquals("get CMLValue", new double[] { 3.4, 5.6 }, dd,
                EPS);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleArrayAttribute.DoubleArrayAttribute(DoubleArrayAttribute)'
     */
    @Test
    public void testDoubleArrayAttributeDoubleArrayAttribute() {
        daa1.setCMLValue("3.4  5.6");
        daa2 = new DoubleArrayAttribute(daa1);
        double[] dd = (double[]) daa2.getCMLValue();
        DoubleTest.assertEquals("get CMLValue", new double[] { 3.4, 5.6 }, dd,
                EPS);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleArrayAttribute.setCMLValue(double[])'
     */
    @Test
    public void testSetCMLValueDoubleArray() {
        daa1.setCMLValue(new double[] { 5.6, 7.8 });
        Assert.assertEquals("get Value", "5.6 7.8", daa1.getValue());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleArrayAttribute.checkValue(double[])'
     */
    @Test
    public void testCheckValue() {
        daa1.checkValue(new double[] { 5.6, 7.8 });
        Assert.assertEquals("get Value", "1.2 3.4", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.DoubleArrayAttribute.split(String,
     * String)'
     */
    @Test
    public void testSplit() {
        double[] dd = DoubleArrayAttribute.split("1.2 3.4 5.6", S_SPACE);
        Assert.assertEquals("split", 3, dd.length);
        DoubleTest.assertEquals("split", new double[] { 1.2, 3.4, 5.6 }, dd,
                EPS);
        dd = DoubleArrayAttribute.split("1.7 3.4 5.6", null);
        Assert.assertEquals("split", 3, dd.length);
        DoubleTest.assertEquals("split", new double[] { 1.7, 3.4, 5.6 }, dd,
                EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleArrayAttribute.getDoubleArray()'
     */
    @Test
    public void testGetDoubleArray() {
        daa1.setCMLValue(new double[] { 5.6, 7.8 });
        DoubleTest.assertEquals("get Value", new double[] { 5.6, 7.8 }, daa1
                .getDoubleArray(), EPS);
    }

}
