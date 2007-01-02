package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.CMLDimension;
import org.xmlcml.cml.element.CMLUnitType;
import org.xmlcml.cml.element.CMLUnitTypeList;

/**
 * test unitType.
 * 
 * @author pm286
 * 
 */
public class CMLUnitTypeTest extends AbstractUnitTest {

    /**
     * setup.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnitType.copy()'
     */
    @Test
    public void testCopy() {
        CMLUnitType unitTypeX = new CMLUnitType(unitType);
        Assert.assertNotNull("copy not null", unitTypeX);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnitType.writeHTML(Writer)'
     * @throws IOException 
     */
    @Test
    public void testWriteHTML() throws IOException {
        writeHTML(unitTypeList, UNIT_RESOURCE + File.separator + "html" +
                File.separator + "unitsTypeDict.html");
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnitType.getId()'
     */
    @Test
    public void testGetId() {
        String id = unitType.getId();
        Assert.assertEquals("unit type id", "dimensionless", id);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnitType.getParentCMLUnitList()'
     */
    @Test
    public void testGetParentCMLUnitList() {
//        CMLElement el = (CMLElement) unitType.getParent();
        CMLUnitTypeList unitTypeListX = unitType.getParentCMLUnitTypeList();
        Assert.assertNotNull("unit type parent", unitTypeListX);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnitType.composeDimensionsFrom(CMLUnitType,
     * int)'
     */
    @Test
    public void testComposeDimensionsFromCMLUnitTypeInt() {
        CMLUnitType unitTypeZ = unitTypeList.getUnitType("length");
        CMLUnitType length4 = new CMLUnitType();
        length4.setId("length4");
        length4.composeDimensionsFrom(unitTypeZ, 4);
        Assert.assertEquals("length4 ", "length4", length4.getId());
        CMLDimension dim = length4.getDimensionElements().get(0);
        Assert.assertEquals("length4 ", 4.0, dim.getPower(), EPS);
        Assert.assertEquals("length4 ", "length", dim.getName());
        
        unitTypeZ = unitTypeList.getUnitType("energy");
        CMLUnitType energy2 = new CMLUnitType();
        energy2.setId("energy2");
        energy2.composeDimensionsFrom(unitTypeZ, 2);
        Assert.assertEquals("energy2 ", "energy2", energy2.getId());
        dim = energy2.getDimensionElements().get(0);
        Assert.assertEquals("energy2 ", 4.0, dim.getPower(), EPS);
        Assert.assertEquals("energy2 ", "length", dim.getName());
        dim = energy2.getDimensionElements().get(1);
        Assert.assertEquals("energy2 ", 2.0, dim.getPower(), EPS);
        Assert.assertEquals("energy2 ", "mass", dim.getName());
        dim = energy2.getDimensionElements().get(2);
        Assert.assertEquals("energy2 ", -4.0, dim.getPower(), EPS);
        Assert.assertEquals("energy2 ", "time", dim.getName());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnitType.composeDimensionsFrom(CMLDimension,
     * int)'
     */
    @Test
    public void testComposeDimensionsFromCMLDimensionInt() {
        CMLUnitType energy = unitTypeList.getUnitType("energy");
        CMLUnitType time = unitTypeList.getUnitType("time");
        CMLUnitType foo = new CMLUnitType();
        foo.setId("foo");
        foo.composeDimensionsFrom(energy, 2);
        foo.composeDimensionsFrom(time, -3);
        
        CMLDimension dim = foo.getDimensionElements().get(0);
        Assert.assertEquals("foo ", 4.0, dim.getPower(), EPS);
        Assert.assertEquals("foo ", "length", dim.getName());
        dim = foo.getDimensionElements().get(1);
        Assert.assertEquals("foo ", 2.0, dim.getPower(), EPS);
        Assert.assertEquals("foo ", "mass", dim.getName());
        dim = foo.getDimensionElements().get(2);
        Assert.assertEquals("foo ", -7.0, dim.getPower(), EPS);
        Assert.assertEquals("foo ", "time", dim.getName());
    }

}
