package org.xmlcml.cml.base.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.StringAttribute;

/**
 * tests for stringAttribute.
 * 
 * @author pmr
 * 
 */
public class StringAttributeTest extends AttributeBaseTest {

    StringAttribute daa1;

    StringAttribute daa2;

    /**
     * setup.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        daa1 = new StringAttribute(new CMLAttribute("foo"), "abc");
    }

    /**
     * Test method for 'org.xmlcml.cml.base.StringAttribute.getCMLValue()'
     */
    @Test
    public void testGetCMLValue() {
        Assert.assertEquals("get CMLValue", "abc", (String) daa1.getCMLValue());
        // Assert.assertNull("get CMLValue", daa1.getCMLValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.StringAttribute.setCMLValue(String)'
     */
    @Test
    public void testSetCMLValueStringX() {
        daa1.setCMLValue("abc");
        String dd = (String) daa1.getCMLValue();
        Assert.assertEquals("get CMLValue", "abc", dd);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.StringAttribute.StringAttribute(StringAttribute)'
     */
    @Test
    public void testStringAttributeStringAttribute() {
        daa1.setCMLValue("3");
        daa2 = new StringAttribute(daa1);
        String dd = (String) daa2.getCMLValue();
        Assert.assertEquals("get CMLValue", "3", dd);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.StringAttribute.setCMLValue(String[])'
     */
    @Test
    public void testSetCMLValueString() {
        daa1.setCMLValue("5");
        Assert.assertEquals("get Value", "5", daa1.getValue());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.base.StringAttribute.checkValue(String[])'
     */
    @Test
    public void testCheckValue() {
        daa1.checkValue("5");
        Assert.assertEquals("get Value", "abc", daa1.getValue());
    }

    /**
     * Test method for 'org.xmlcml.cml.base.StringAttribute.getString()'
     */
    @Test
    public void testGetString() {
        daa1.setCMLValue("6");
        Assert.assertEquals("get Value", "6", daa1.getString());
    }

}
