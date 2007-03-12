package org.xmlcml.cml.element;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * test CMLDimension.
 *
 * @author pm286
 *
 */
public class CMLDimensionTest extends AbstractTest {

    /**
     * setup.
     *
     * @exception Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLDimension.copy()'
     */
    @Test
    public void testCopy() {
        CMLDimension dimension = new CMLDimension();
        dimension.setPower(1.0);
        CMLDimension dimension1 = (CMLDimension) dimension.copy();
        Assert.assertEquals("copy", 1.0, dimension1.getPower(), EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLDimension.getPower()'
     */
    @Test
    public void testGetPower() {
        CMLDimension dimension = new CMLDimension();
        Assert
                .assertEquals("default dimension", 1.0, dimension.getPower(),
                        EPS);
    }

 }
