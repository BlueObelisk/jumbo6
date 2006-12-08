package org.xmlcml.cml.base.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.IntAttribute;

/**
 * tests for intAttribute.
 * 
 * @author pmr
 * 
 */
public class IntAttributeTest extends AttributeBaseTest {

    IntAttribute daa1;

    IntAttribute daa2;

    /**
     * setup.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        daa1 = new IntAttribute(new CMLAttribute("foo"), " 1   ");

    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntAttribute.getCMLValue()'
     */
    @Test
    public void testGetCMLValue() {
        Assert.assertEquals("get CMLValue", 1, daa1.getCMLValue());
        // Assert.assertNull("get CMLValue", daa1.getCMLValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntAttribute.setCMLValue(String)'
     */
    @Test
    public void testSetCMLValueString() {
        daa1.setCMLValue("3");
        Integer dd = (Integer) daa1.getCMLValue();
        Assert.assertEquals("get CMLValue", 3, dd.intValue(), EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.IntAttribute.IntAttribute(IntAttribute)'
     */
    @Test
    public void testIntAttributeIntAttribute() {
        daa1.setCMLValue("3");
        daa2 = new IntAttribute(daa1);
        Integer dd = (Integer) daa2.getCMLValue();
        Assert.assertEquals("get CMLValue", 3, dd.intValue(), EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntAttribute.setCMLValue(double[])'
     */
    @Test
    public void testSetCMLValueInt() {
        daa1.setCMLValue(5);
        Assert.assertEquals("get Value", "5", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntAttribute.checkValue(double[])'
     */
    @Test
    public void testCheckValue() {
        try {
            daa1.checkValue(5);
        } catch (CMLException e) {
            Assert.fail("should not throw " + e);
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.base.IntAttribute.getInt()'
     */
    @Test
    public void testGetInt() {
        daa1.setCMLValue(7);
        Assert.assertEquals("get Value", 7, daa1.getInt());
    }

}
