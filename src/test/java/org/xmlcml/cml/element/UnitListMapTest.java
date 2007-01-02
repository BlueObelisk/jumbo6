package org.xmlcml.cml.element;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.CMLUnitList;
import org.xmlcml.cml.element.CMLUnitType;
import org.xmlcml.cml.element.CMLUnitTypeList;
import org.xmlcml.cml.element.NamespaceToUnitListMap;
import org.xmlcml.cml.element.UnitAttribute;
import org.xmlcml.cml.element.UnitTypeAttribute;
import org.xmlcml.euclid.Util;

/**
 * test for NamespaceToUnitListMap.
 *
 * @author pm286
 *
 */
public class UnitListMapTest extends AbstractTest {

	String scalar1S = "<scalar units='units:deg' " + "xmlns:units='" + UNIT_NS
			+ "' " + CML_XMLNS + ">123.4</scalar>";

	String scalar2S = "<scalar units='siUnits:m' " + "xmlns:siUnits='"
			+ SIUNIT_NS + "' " + CML_XMLNS + ">123.4</scalar>";

	CMLScalar scalar1 = null;

	CMLScalar scalar2 = null;

	UnitAttribute unitAttribute = null;

	UnitTypeAttribute unitTypeAttribute = null;

	NamespaceToUnitListMap unitListMap = null;

	boolean recurse = true;

	/**
	 * setup.
	 * @throws Exception
	 *
	 */
	@Before
	public void setUp() throws Exception {
        super.setUp();
		makeUnitList();
		makeScalar();
		makeUnitTypeAttribute();
	}

	void makeScalar() {
		try {
			scalar1 = (CMLScalar) parseValidString(scalar1S);
			scalar2 = (CMLScalar) parseValidString(scalar2S);
		} catch (Exception e) {
			neverThrow(e);
		}
		unitAttribute = (UnitAttribute) scalar1.getUnitsAttribute();
		Assert.assertNotNull("unitAttribute not null", unitAttribute);
		Assert.assertEquals("unitAttribute ", "units:deg", unitAttribute
				.getCMLValue());
		Assert.assertEquals("unitAttribute prefix", "units", unitAttribute
				.getPrefix());
		Assert.assertEquals("unitAttribute namespace", UNIT_NS, unitAttribute
				.getNamespaceURIString());
	}

	void makeUnitList() throws IOException {
		unitListMap = new NamespaceToUnitListMap(Util
				.getResource(UNIT_RESOURCE + U_S + CATALOG_XML));
	}

	void makeUnitTypeAttribute() {
		// get a typical UnitTypeAttribute
		CMLUnit unit = scalar2.getUnit(unitListMap);
		Assert.assertNotNull("unit not null", unit);
		Assert.assertEquals("unit id", "m", unit.getId());
		unitTypeAttribute = (UnitTypeAttribute) unit.getUnitTypeAttribute();
		Assert.assertNotNull("unitTypeAttribute not null", unitTypeAttribute);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.NamespaceToUnitListMap.UnitListMap(File,
	 * boolean)'
	 */
	@Test
	public void testUnitListMapFileBoolean() {
		Assert.assertNotNull("unitListMap not null", unitListMap);
		Assert.assertEquals("unitListMap size", NUNIT_DICT, unitListMap.size());
		CMLUnitList unitList = (CMLUnitList) unitListMap.get(UNIT_NS);
		Assert.assertNotNull("unitListMap units", unitList);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.NamespaceToUnitListMap.getUnitList(UnitAttribute)'
	 */
	@Test
	public void testGetUnitList() {
		CMLUnitList unitList = unitListMap.getUnitList(unitAttribute);
		Assert.assertNotNull("unitList not null", unitList);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.NamespaceToUnitListMap.getUnitTypeList(UnitTypeAttribute)'
	 */
	@Test
	public void testGetUnitTypeList() {
		CMLUnitTypeList unitTypeList = unitListMap
				.getUnitTypeList(unitTypeAttribute);
		Assert.assertNotNull("unitTypeList not null", unitTypeList);
		String id = unitTypeAttribute.getIdRef();
		Assert.assertNotNull("unitType id", id);
		Assert.assertEquals("unitType id", "length", id);
		CMLUnitType lengthType = unitTypeList.getUnitType(id);
		Assert.assertNotNull("unitTypeList not null", lengthType);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.NamespaceToUnitListMap.getUnit(UnitAttribute)'
	 */
	@Test
	public void testGetUnit() {
		CMLUnit unit = unitListMap.getUnit(unitAttribute);
		Assert.assertNotNull("unit not null", unit);
		Assert.assertEquals("unit id", "deg", unit.getId());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.NamespaceToUnitListMap.getUnitType(UnitTypeAttribute)'
	 */
	@Test
	public void testGetUnitType() {

		Assert.assertEquals("unitTypeId", "unitType:length", unitTypeAttribute
				.getValue());
		CMLUnitType unitType = unitListMap.getUnitType(unitTypeAttribute);
		Assert.assertNotNull("unitType not null", unitType);
	}


}
