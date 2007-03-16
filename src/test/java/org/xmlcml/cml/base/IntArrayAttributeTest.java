package org.xmlcml.cml.base;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.test.IntTest;

/**
 * tests for intAttribute.
 * 
 * @author pmr
 * 
 */
public class IntArrayAttributeTest extends AttributeBaseTest {

    IntArraySTAttribute daa1;

    IntArraySTAttribute daa2;

    /**
     * setup.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        daa1 = new IntArraySTAttribute(new CMLAttribute("foo"), " 1   3  ");
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntArraySTAttribute.getCMLValue()'
     */
    @Test
    public void testGetCMLValue() {
        // Assert.assertEquals("get CMLValue", "1 3", daa1.getCMLValue());
        Assert.assertNull("get CMLValue", daa1.getCMLValue());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.IntArraySTAttribute.setCMLValue(String)'
     */
    @Test
    public void testSetCMLValueString() {
        daa1.setCMLValue("3   5");
        int[] dd = (int[]) daa1.getCMLValue();
        IntTest.assertEquals("get CMLValue", new int[] { 3, 5 }, dd);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.IntArraySTAttribute.IntArrayAttribute(IntArraySTAttribute)'
     */
    @Test
    public void testIntArrayAttributeIntArrayAttribute() {
        daa1.setCMLValue("3  5");
        daa2 = new IntArraySTAttribute(daa1);
        int[] dd = (int[]) daa2.getCMLValue();
        IntTest.assertEquals("get CMLValue", new int[] { 3, 5 }, dd);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.IntArraySTAttribute.setCMLValue(int[])'
     */
    @Test
    public void testSetCMLValueIntArray() {
        daa1.setCMLValue(new int[] { 5, 7 });
        Assert.assertEquals("get Value", "5 7", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntArraySTAttribute.checkValue(int[])'
     */
    @Test
    public void testCheckValue() {
        daa1.checkValue(new int[] { 5, 7 });
        Assert.assertEquals("get Value", "1 3", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntArraySTAttribute.split(String,
     * String)'
     */
    @Test
    public void testSplit() {
        int[] dd = IntArraySTAttribute.split("1 3 5", S_SPACE);
        Assert.assertEquals("split", 3, dd.length);
        IntTest.assertEquals("split", new int[] { 1, 3, 5 }, dd);
        dd = IntArraySTAttribute.split("7 3 5", null);
        Assert.assertEquals("split", 3, dd.length);
        IntTest.assertEquals("split", new int[] { 7, 3, 5 }, dd);
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntArraySTAttribute.getIntArray()'
     */
    @Test
    public void testGetIntArray() {
        daa1.setCMLValue(new int[] { 5, 7 });
        IntTest.assertEquals("get Value", new int[] { 5, 7 }, daa1
                .getIntArray());
    }

}
