package org.xmlcml.cml.base;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.DoubleAttribute;

/**
 * tests for doubleAttribute.
 * 
 * @author pmr
 * 
 */
public class DoubleAttributeTest extends AttributeBaseTest {

    DoubleAttribute daa1;

    DoubleAttribute daa2;

    /**
     * setup.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        daa1 = new DoubleAttribute(new CMLAttribute("foo"), " 1.2   ");

    }

    /**
     * Test method for 'org.xmlcml.cml.base.DoubleAttribute.getCMLValue()'
     */
    @Test
    public void testGetCMLValue() {
        Assert.assertEquals("get CMLValue", "1.2", (String) daa1.getCMLValue());
        // Assert.assertNull("get CMLValue", daa1.getCMLValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.DoubleAttribute.setCMLValue(String)'
     */
    @Test
    public void testSetCMLValueString() {
        daa1.setCMLValue("3.4");
        String dd = (String) daa1.getCMLValue();
        Assert.assertEquals("get CMLValue", "3.4", dd);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleAttribute.DoubleAttribute(DoubleAttribute)'
     */
    @Test
    public void testDoubleAttributeDoubleAttribute() {
        daa1.setCMLValue("3.4");
        daa2 = new DoubleAttribute(daa1);
        String dd = (String) daa2.getCMLValue();
        Assert.assertEquals("get CMLValue", "3.4", dd);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleAttribute.setCMLValue(double[])'
     */
    @Test
    public void testSetCMLValueDouble() {
        daa1.setCMLValue(5.6);
        Assert.assertEquals("get Value", "5.6", daa1.getValue());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.DoubleAttribute.checkValue(double[])'
     */
    @Test
    public void testCheckValue() {
        daa1.checkValue(5.6);
        Assert.assertEquals("get Value", "1.2", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.DoubleAttribute.getDouble()'
     */
    @Test
    public void testGetDouble() {
        daa1.setCMLValue(7.8);
        Assert.assertEquals("get Value", 7.8, daa1.getDouble(), EPS);
    }

}