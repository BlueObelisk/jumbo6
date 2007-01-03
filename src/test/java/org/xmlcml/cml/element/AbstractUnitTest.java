package org.xmlcml.cml.element;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.Util;

/**
 * tests CMLUnit.
 * 
 * @author pmr
 * 
 */
public class AbstractUnitTest extends AbstractTest {

	String TEST_UNITS_DICT = "unitsDict.xml";

	int TEST_NUNITS = 40;

	String TEST_SI_UNITS_DICT = "siUnitsDict.xml";

	String TEST_UNIT_TYPE_DICT = "unitTypeDict.xml";

	static CMLUnitList siUnitList = null;

	CMLUnit siUnit;

	static CMLUnitList unitList = null;

	CMLUnit unit;

	static NamespaceToUnitListMap unitListMap = null;

	static CMLUnitTypeList unitTypeList = null;

	CMLUnitType unitType;

	protected String newUnitS = S_EMPTY + "<unit id='g.s-1'" + "  xmlns='" + CML_NS
			+ "' " + "  xmlns:siUnits='" + SIUNIT_NS + "' " + "  xmlns:units='"
			+ UNIT_NS + "' " + "  xmlns:unitType='" + UNITTYPES_NS + "' "
			+ "  unitType='newUnitType:mass.length-1'" + ">"
			+ "  <unit id='u1' units='units:g' power='1'/>"
			+ "  <unit id='u2' units='siUnits:s' power='-1'/>" + "</unit>" + S_EMPTY;

	protected String unitList1S = S_EMPTY + "<unitList" + "  id='fooUnits'"
			+ "  namespace='" + _UNIT_NS + S_SLASH + "fooUnits'"
			+ "  dictionaryPrefix='fooUnits'" + "  title='simple foo units'"
			+ "  siNamespace='" + SIUNIT_NS + "' " + "  xmlns:siUnits='"
			+ SIUNIT_NS + "' " + "  xmlns:units='" + UNIT_NS + "' "
			+ "  xmlns:unitType='" + UNITTYPES_NS + "' " + "    xmlns='"
			+ CML_NS + "' " + ">" + "  <unit id='g'"
			+ "    unitType='unitType:mass'" + S_EMPTY + "    parentSI='siUnits:m'"
			+ "    multiplierToSI='0.001'" + "  />" + "</unitList>";

	protected CMLUnitList unitList1 = null;

	protected CMLUnit unit1 = null;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		if (siUnitList == null) {
			siUnitList = CMLUnitList.createUnitList(Util
					.getResource(UNIT_RESOURCE + U_S + TEST_SI_UNITS_DICT));
		}
		siUnit = siUnitList.getUnitElements().get(11);
		if (unitList == null) {
			unitList = CMLUnitList.createUnitList(Util
					.getResource(UNIT_RESOURCE + File.separator
							+ TEST_UNITS_DICT));
		}
		unit = unitList.getUnitElements().get(1);

		if (unitListMap == null) {
			unitListMap = new NamespaceToUnitListMap(Util
					.getResource(UNIT_RESOURCE + U_S + CATALOG_XML));
			unitList.setUnitListMap(unitListMap);
			siUnitList.setUnitListMap(unitListMap);
		}

		Assert.assertNotNull("siUnitList not null", siUnitList);
		Assert.assertTrue("siUnitList size ", siUnitList.getUnitElements()
				.size() > 15);
		Assert.assertNotNull("siUnit 15 not null", siUnit);

		Assert.assertNotNull("unitList not null", unitList);
		Assert.assertNotNull("unit 1 not null", unit);

		Assert.assertNotNull("unitListMap  not null", unitListMap);
		Assert.assertEquals("unitListMap size", NUNIT_DICT, unitListMap.size());

		if (unitTypeList == null) {
			unitTypeList = CMLUnitTypeList.createUnitTypeList(Util
					.getResource(UNIT_RESOURCE + U_S + TEST_UNIT_TYPE_DICT));
		}
		unitType = unitTypeList.getUnitTypeElements().get(1);

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

}
