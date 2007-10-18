package org.xmlcml.cml.element;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.map.NamespaceToUnitListMap;

/**
 * tests CMLUnit.
 *
 * @author pmr
 *
 */
public class CMLUnitTest extends AbstractUnitTest {

    /** setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        unitList1 = (CMLUnitList) parseValidString(unitList1S);
        unit1 = unitList1.getUnitElements().get(0);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getParentCMLUnitList()'
     */
    @Test
    public void testGetParentCMLUnitList() {
        CMLUnitList unitList1 = unit.getParentCMLUnitList();
        Assert.assertNotNull("unitList1 not null", unitList1);
        Assert.assertSame("unitList1 ", unitList1, unitList);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnit.getParentSIUnit(NamespaceToUnitListMap)'
     */
    @Test
    public void testGetSIUnitList() {
        Assert.assertEquals("si list length ", 64, siUnitList.size());
        Assert.assertNotNull("parent si not null ", siUnit);
        Assert.assertEquals("parent si name ", "Joule", siUnit.getTitle());
        Assert.assertEquals("parent si id ", "joule", siUnit.getId());
        Assert.assertEquals("parent si parentSI ", "siUnits:joule", siUnit
                .getParentSI());
        Assert.assertEquals("parent si multiplier ", 1.0, siUnit
                .getMultiplierToSI(), EPS);
        Assert.assertEquals("parent si abb ", "J", siUnit.getSymbol());
        Assert.assertEquals("parent si unitType ", "unitType:energy", siUnit
                .getUnitType());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnit.getParentSIUnit(NamespaceToUnitListMap)'
     */
    @Test
    public void testGetParentSIUnit() {
        Assert.assertEquals("si unit ", "ang", unit.getId());
        CMLUnit siUnit1 = unit.getParentSIUnit();
        Assert.assertNotNull("parent si not null " + unit.getId(), siUnit1);
        Assert.assertEquals("parent si id ", "m", siUnit1.getId());
        Assert.assertEquals("parent si parentSI ", "siUnits:m", siUnit1
                .getParentSI());
        Assert.assertEquals("parent si multiplier ", 1.0, siUnit1
                .getMultiplierToSI(), EPS);
        Assert.assertEquals("parent si abb ", "m", siUnit1.getSymbol());
        Assert.assertEquals("parent si unitType ", "unitType:length", siUnit1
                .getUnitType());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.isSIUnit()'
     */
    @Test
    public void testIsSIUnit() {
        Assert.assertFalse("is si", unit.isSIUnit());
        CMLUnit siUnit = unit.getParentSIUnit();
        Assert.assertTrue("is si", siUnit.isSIUnit());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getReferencedUnitType()'
     */
    @Test
    public void testGetReferencedUnitType() {
        Assert.assertEquals("unit type", "unitType:length", unit.getUnitType());
        CMLUnitType unitType = unit.getCMLUnitType();
        Assert.assertNotNull("unitType not null", unitType);
        Assert.assertEquals("unitType id", "length", unitType.getId());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getSIUnit()'
     */
    @Test
    public void testGetSIUnit() {
        Assert.assertEquals("non si unit", "ang", unit.getId());
        Assert.assertEquals("non si unit multiplier", 1.0E-10, unit.getMultiplierToSI(), EPS);
        CMLUnit siUnitX = unit.getSIUnit();
        Assert.assertNotNull("si unit not null", siUnitX);
        Assert.assertEquals("si unit", "m", siUnitX.getId());
        Assert.assertEquals("si unit", "metre", siUnitX.getTitle());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getUnitListMap()'
     */
    @Test
    public void testGetUnitListMap() {
        Assert.assertEquals("unit", "ang", unit.getId());
        Assert.assertEquals("unit multiplier", 1.0E-10, unit.getMultiplierToSI(), EPS);
        NamespaceToUnitListMap unitListMap = unit.getUnitListMap();
        // this is list of all unitList namespaces
        Assert.assertNotNull("unit list map not null", unitListMap);
        Assert.assertEquals("unit list map size", NUNIT_DICT, unitListMap.size());
        CMLUnitList unitListX = (CMLUnitList) unitListMap.get(UNIT_NS);
        Assert.assertNotNull("unit list", unitListX);
        Assert.assertEquals("unit list", "simpleUnits", unitListX.getId());
    }

    /**
     * test computation of new units.
     *
     */
    @Test
    public void testComputeUnitUnitListMap() {
        CMLUnit newUnit = (CMLUnit) parseValidString(newUnitS);
        /*-- should be
         <unit id="g.s-1" unitType="newUnitType:mass.length-1"
         xmlns:unitType=UNITTYPES_NS
         xmlns:siUnits=SIUNIT_NS
         xmlns:units=UNIT_NS
         xmlns=CML_NS>
         <unit id="u1" units=CML_UNITS+S_COLON+"g" power="1.0"/>
         <unit id="u2" units=CML_SIUNITS+S_COLON+"s" power="-1.0"/>
         </unit>
         --*/
        Assert.assertNotNull("new unit not null", newUnit);
        CMLElements<CMLUnit> childUnits = newUnit.getUnitElements();
        Assert.assertEquals("child units", 2, childUnits.size());
        Assert.assertEquals("child 0", CML_UNITS + S_COLON + "g", childUnits
                .get(0).getUnits());
        Assert.assertEquals("child 0", 1.0, childUnits.get(0).getPower(), EPS);
        Assert.assertEquals("child 1", CML_SIUNITS + S_COLON + "s", childUnits
                .get(1).getUnits());
        Assert.assertEquals("child 1", -1.0, childUnits.get(1).getPower(), EPS);
        newUnit.computeUnit(unitListMap, true);
        /*-- should now be
         <unit id="g.s-1" unitType="newUnitType:mass.length-1"
         multiplierToSI="0.0010"
         xmlns:unitType=UNITTYPES_NS
         xmlns:siUnits=SIUNIT_NS
         xmlns:units=UNIT_NS
         xmlns=CML_NS>
         <unit id="u1" units=CML_UNITS+S_COLON+"g" power="1.0"/>
         <unit id="u2" units=CML_SIUNITS+S_COLON+"s" power="-1.0"/>
         <unitType>
         <dimension name="mass" power="1.0"/>
         <dimension name="time" power="-1.0"/>
         </unitType>
         </unit>
         --*/
        Assert.assertEquals("multiplierToSI", 0.001, newUnit
                .getMultiplierToSI(), EPS);

        Assert.assertEquals("child 0", CML_UNITS + S_COLON + "g", childUnits
                .get(0).getUnits());
        Assert.assertEquals("child 0", 1.0, childUnits.get(0).getPower(), EPS);
        Assert.assertEquals("child 1", CML_SIUNITS + S_COLON + "s", childUnits
                .get(1).getUnits());
        Assert.assertEquals("child 1", -1.0, childUnits.get(1).getPower(), EPS);

        CMLElements<CMLUnitType> childUnitTypes = newUnit.getUnitTypeElements();
        Assert.assertEquals("child unitTypes", 1, childUnitTypes.size());
        Assert.assertNotNull("childType", childUnitTypes.get(0));
        CMLElements<CMLDimension> childDimensions = childUnitTypes.get(0)
                .getDimensionElements();
        Assert.assertEquals("dimension 0", "mass", childDimensions.get(0)
                .getName());
        Assert.assertEquals("dimension 0", 1.0, childDimensions.get(0)
                .getPower());
        Assert.assertEquals("dimension 1", "time", childDimensions.get(1)
                .getName());
        Assert.assertEquals("dimension 1", -1.0, childDimensions.get(1)
                .getPower());
        // a unit with a multiplier
        String newUnitS1 = "" + "<unit id='megawatt.hr-1'"
                + "  multiplierToData='1.0E+06'"
                + "  xmlns='"+CML_NS+"' "
                + "  xmlns:siUnits='"+SIUNIT_NS+"' "
                + "  xmlns:units='"+UNIT_NS+"' "
                + "  xmlns:unitType='"+UNITTYPES_NS+"' "
                + ">" + "  <unit id='u1' units='siUnits:watt' power='1'/>"
                + "  <unit id='u2' units='units:h' power='-1'/>" + "</unit>"
                + "";

        CMLUnit newUnit1 = (CMLUnit) parseValidString(newUnitS1);
        newUnit1.computeUnit(unitListMap, true);
        /*-- should be
         <unit id="watt.hr-1"
         multiplierToSI="2.777777777777778E-4"
         xmlns:unitType=UNITTYPES_NS
         xmlns:siUnits=SIUNIT_NS
         xmlns:units=UNIT_NS
         xmlns=CML_NS>
         <unit id="u1" units="siUnits:watt" power="1.0"/>
         <unit id="u2" units=CML_UNITS+S_COLON+"h" power="-1.0"/>
         <unitType>
         <dimension name="length" power="2.0"/>
         <dimension name="mass" power="1.0"/>
         <dimension name="time" power="-4.0"/>
         </unitType>
         </unit>
         --*/
        Assert.assertEquals("multiplierToSI", 1.0/ 3600, newUnit1
                .getMultiplierToSI(), EPS);

        CMLElements<CMLUnit> childUnits1 = newUnit.getUnitElements();
        Assert.assertEquals("child 0", CML_UNITS + S_COLON + "g", childUnits1
                .get(0).getUnits());
        Assert.assertEquals("child 0", 1.0, childUnits1.get(0).getPower(), EPS);
        Assert.assertEquals("child 1", "siUnits:s", childUnits1.get(1)
                .getUnits());
        Assert
                .assertEquals("child 1", -1.0, childUnits1.get(1).getPower(),
                        EPS);

        childUnitTypes = newUnit1.getUnitTypeElements();
        Assert.assertEquals("child unitTypes", 1, childUnitTypes.size());
        Assert.assertNotNull("childType", childUnitTypes.get(0));
        childDimensions = childUnitTypes.get(0).getDimensionElements();
        Assert.assertEquals("child dimensions", 3, childDimensions.size());
        Assert.assertEquals("dimension 0", "length", childDimensions.get(0)
                .getName());
        Assert.assertEquals("dimension 0", 2.0, childDimensions.get(0)
                .getPower());
        Assert.assertEquals("dimension 1", "mass", childDimensions.get(1)
                .getName());
        Assert.assertEquals("dimension 1", 1.0, childDimensions.get(1)
                .getPower());
        Assert.assertEquals("dimension 2", "time", childDimensions.get(2)
                .getName());
        Assert.assertEquals("dimension 2", -4.0, childDimensions.get(2)
                .getPower());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.copy()'
     */
    @Test
    public void testCopy() {
        CMLUnit unitX = (CMLUnit) unit.copy();
        // this unit does not have its own parent UnitList
        try {
            NamespaceToUnitListMap unitListMapX = unitX.getUnitListMap();
            Assert.assertNotNull("copy not null", unitListMapX);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("cannot copy unit", e.getMessage(),
                "unit ang must be contained within a unitList");
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.writeHTML(Writer)'
     */
    @Test
    public void testWriteHTML() {
        StringWriter w = new StringWriter();
        try {
            unit.writeHTML(w);
        w.close();
        } catch (Exception e) {
            neverThrow(e);
        }
        String ss =
            "<tr><td><b>ang</b></td><td><b>Angstrom</b></td><td>[Aring]</td><td>length</td><td>1.0E-10</td><td>m</td><td>\n" +
            "    The angstrom is named after the Swedish physicist Anders Jonas Angstrom \n"+
            "    (1814-1874), one of the founders of spectroscopy, after his spectrum chart \n"+
            "    of solar radiation in the electromagnetic spectrum on the order \n"+
            "    of multiples of one ten-millionth of a millimetre, or 1E-10 metres. \n"+
            "    </td><td>Length</td></tr>\n"+
            "";

        Assert.assertEquals("to html", ss, w.toString());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getId()'
     */
    @Test
    public void testGetId() {
        String id = unit.getId();
        Assert.assertEquals("id ", "ang", id);
        CMLAttribute idAttribute = unit.getIdAttribute();
        unit.removeAttribute(idAttribute);
        try {
            id = unit.getId();
            Assert.fail("should throw missing id");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("Missing id exception", "unit must have id", e.getMessage());
        }
        // this has now destroyed the integrity for the unitList so mend it
        unit.addAttribute(idAttribute);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getPower()'
     */
    @Test
    public void testGetPower() {
        double d  = unit.getPower();
        Assert.assertTrue("power ", !Double.isNaN(d));
        Assert.assertEquals("power ", 1.0, d, EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getCMLUnitType()'
     */
    @Test
    public void testGetCMLUnitType() {
        CMLUnitType unitType = unit.getCMLUnitType();
        Assert.assertNotNull("unit type not null", unitType);
        Assert.assertEquals("unit type", "length", unitType.getId());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLUnit.setUnitListMap(NamespaceToUnitListMap)'
     */
    @Test
    public void testSetUnitListMap() {
        // currently a simple setter
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.getNamespace()'
     */
    @Test
    public void testGetNamespace() {
        String s = unit.getNamespace();
        Assert.assertNotNull("namespace", s);
        Assert.assertEquals("namespace", UNIT_NS, s);
     }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.setUnitsOn(HasUnits)'
     */
    @Test
    public void testSetUnitsOn() {
        String scalarS = "<scalar "+CML_XMLNS+S_SPACE +
            "units='units:ang' " +
            "xmlns:units='"+UNIT_NS+"' " +
            "dataType='"+XSD_DOUBLE+"' " +
            ">1234</scalar>" +
            "";
        CMLScalar scalar = (CMLScalar) parseValidString(scalarS);
        CMLUnit origUnit = scalar.getUnit(unitListMap);
        Assert.assertNotNull("orig unit not null", origUnit);
        Assert.assertEquals("orig unit ", "ang", origUnit.getId());
        CMLUnitList simpleUnits = (CMLUnitList) unitListMap.get(UNIT_NS);
        CMLUnit cmUnit = simpleUnits.getUnit("cm");
        Assert.assertNotNull("cm unit not null", cmUnit);
        cmUnit.setUnitsOn(scalar);
        // of course the value is now wrong, so change it
        double scale = origUnit.getMultiplierToSI() / cmUnit.getMultiplierToSI();
        Assert.assertEquals("scale ", scale, 1.0E-8, EPS);
        scalar.setXMLContent(""+(scalar.getDouble() * scale));
        Assert.assertEquals("new value ", scalar.getDouble(), 1.234E-5, EPS);

        CMLUnit newUnit = scalar.getUnit(unitListMap);
        Assert.assertNotNull("new unit not null", newUnit);
        Assert.assertEquals("orig unit ", "cm", newUnit.getId());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLUnit.computeUnit(NamespaceToUnitListMap,
     * boolean)'
     */
    @Test
    public void testComputeUnit() {
/*
 * <unit id="a.b"> <unit units="units:u1" power="2"/> <unit units="units:u2"
 * power="-1"/> </unit>
 */
        CMLUnit unitTop = new CMLUnit();
        unitTop.setId("ttt");
        unitList.addUnit(unitTop);
        CMLUnit unit1 = new CMLUnit();
        unit1.setUnits("units:g");
        unit1.setPower(2.0);
        unit1.setId("ggg");
        unitTop.appendChild(unit1);
        CMLUnit unit2 = new CMLUnit();
        unit2.setUnits("units:cm");
        unit2.setPower(-1.0);
        unit2.setId("ccc");
        unitTop.appendChild(unit2);
        boolean add = false;
        unitTop.computeUnit(unitListMap, add);
        Assert.assertEquals("top unit multiplier", unitTop.getMultiplierToSI(), 1.0E-4, 1.0E-9);
    }

 }
