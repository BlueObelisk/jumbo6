package org.xmlcml.cml.element;

import static org.xmlcml.cml.base.CMLConstants.CATALOG_XML;
import static org.xmlcml.cml.base.CMLConstants.NUNIT_DICT;
import static org.xmlcml.cml.base.CMLConstants.SIUNIT_NS;
import static org.xmlcml.cml.base.CMLConstants.UNITTYPES_NS;
import static org.xmlcml.cml.base.CMLConstants.UNIT_NS;
import static org.xmlcml.cml.element.AbstractTest.UNIT_RESOURCE;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.neverThrow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.map.NamespaceToUnitListMap;
import org.xmlcml.euclid.Util;
/**
 * test CMLUnitList.
 *
 * @author pmr
 *
 */
public class CMLUnitListTest extends AbstractUnitTest {

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.CMLUnitList()'
	 */
	@Test
	public void testCMLUnitList() {
		CMLUnitList unitList = new CMLUnitList();
		CMLUnit unit = unitList.getUnit("foo");
		Assert.assertNull("no units yet", unit);
		CMLElements<CMLUnit> units = unitList.getUnitElements();
		Assert.assertEquals("no units yet", 0, units.size());
		NamespaceToUnitListMap unitListMap = unitList.getUnitListMap();
		Assert.assertNull("no unit map yet", unitListMap);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitList.CMLUnitList(CMLUnitList)'
	 */
	@Test
	public void testCMLUnitListCMLUnitList() {
		CMLUnitList unitListX = new CMLUnitList(unitList);
		Assert.assertNotNull("copy not null", unitListX);
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.createUnitList(File)'
	 */
	@Test
	public void testCreateUnitList() {
		CMLUnitList unitListX = null;
		try {
			unitListX = CMLUnitList.createUnitList(Util
					.getResource(UNIT_RESOURCE + U_S + TEST_UNITS_DICT));
		} catch (IOException e) {
			throw new CMLRuntimeException("Cannot open unitList file: " + e);
		} catch (CMLException e) {
			throw new CMLRuntimeException("Cannot parse unitList file: " + e);
		}
		Assert.assertEquals("unitList ", TEST_NUNITS, unitListX.size());
		CMLUnit unitX = unitListX.getUnit("ang");
		Assert.assertNotNull("unit ", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.addUnit(CMLUnit)'
	 *
	 * @exception Exception
	 */
	@Test
	public void testAddUnitCMLUnit() throws Exception {
		// the unitList has been modified in some test - fix it
		// unitList = null;
		// super.setUp();
		// FIXME
		// Assert.assertEquals("unitList ", TEST_NUNITS, unitList.size());
		CMLUnit unitF = unitList.getUnit("foo");
		Assert.assertNull("unit ", unitF);
		CMLUnit unitX = unitList.getUnit("ang");
		Assert.assertNotNull("unit ", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());

		CMLUnit unitZ = new CMLUnit();
		unitZ.setId("foo");
		unitZ.setMultiplierToSI(100.0);
		unitZ.setTitle("foo units");
		unitZ.setUnitType(NamespaceRefAttribute.createValue("unitType", "bar"));
		try {
			unitList.addUnit(unitZ);
		} catch (CMLRuntimeException e) {
			neverThrow(e);
		}
		// FIXME
		// Assert.assertEquals("unitList ", 41, unitList.size());
		unitF = unitList.getUnit("foo");
		Assert.assertNotNull("unit ", unitF);
		Assert.assertEquals("unit title", "foo units", unitF.getTitle());
		unitX = unitList.getUnit("ang");
		Assert.assertNotNull("unit ", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());
		// now remove the damage
		unitList.removeUnit(unitF);
		// FIXME
		// Assert.assertEquals("unitList ", TEST_NUNITS, unitList.size());
		unitF = unitList.getUnit("foo");
		Assert.assertNull("unit ", unitF);
		unitX = unitList.getUnit("ang");
		Assert.assertNotNull("unit ", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitList.createDictionary(File)'
	 */
	@Test
	public void testCreateDictionary() {
		CMLUnitList unitListX = null;
		try {
			unitListX = (CMLUnitList) new CMLUnitList()
					.createDictionary(Util.getResource(UNIT_RESOURCE + U_S
							+ TEST_UNITS_DICT));
		} catch (IOException e) {
			throw new CMLRuntimeException("Cannot open unitList file: " + e);
		} catch (CMLRuntimeException e) {
			throw new CMLRuntimeException("Cannot parse unitList file: " + e);
		}
		Assert.assertEquals("unitList ", TEST_NUNITS, unitListX.size());
		CMLUnit unitX = unitListX.getUnit("ang");
		Assert.assertNotNull("unit ", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.writeHTML(Writer)'
	 *
	 * @throws IOException
	 * @throws CMLException
	 */
	@Test
	public void testWriteHTML() throws IOException, CMLException {
		// this just writes HTML to the same directory. Check by eye!
		AbstractTest.writeHTML(siUnitList, Util.getTEMP_DIRECTORY() + File.separator
				+ "html" + File.separator + "siUnitsDict.html");
		AbstractTest.writeHTML(unitList, Util.getTEMP_DIRECTORY() + File.separator
				+ "html" + File.separator + "unitsDict.html");
		writeHTML("siestaUnits.xml");
	}

	private void writeHTML(String filename) throws IOException, CMLException {
        String xmlFile = UNIT_RESOURCE + File.separator + filename;
		String outFile = Util.getTEMP_DIRECTORY() + File.separator  + filename;
		CMLUnitList unitList = CMLUnitList.createUnitList(Util.getResource(xmlFile));
		NamespaceToUnitListMap unitListMap = new NamespaceToUnitListMap(Util
				.getResource(UNIT_RESOURCE + U_S + CATALOG_XML), new CMLUnitList());
		unitList.setUnitListMap(unitListMap);
		AbstractTest.writeHTML(unitList, outFile + ".html");
	}

	/**
	 * test getUnitListMap.
	 */
	@Test
	public void testGetUnitListMap() {
		Assert.assertNotNull("unitListMap", unitListMap);
		Assert.assertEquals("unitListMap size", NUNIT_DICT, unitListMap.size());
		String[] ss = unitListMap.keySet().toArray(new String[0]);
		Arrays.sort(ss);
		int i = 0;
		Assert.assertEquals("siestaUnitListMap " + i,
				"http://www.uam.es/siesta/units", ss[i++]);
		Assert.assertEquals("castepUnitListMap " + i,
				"http://www.xml-cml.org/units/castepUnits", ss[i++]);
		Assert.assertEquals("siUnitListMap " + i, SIUNIT_NS, ss[i++]);
		Assert.assertEquals("unitTypeListMap " + i, UNITTYPES_NS, ss[i++]);
		Assert.assertEquals("unitListMap " + i, UNIT_NS, ss[i++]);
	}

	/**
	 * tes get SI Unit List
	 *
	 */
	@Test
	public void testGetSIUnitList() {
		CMLUnitList unitList = (CMLUnitList) unitListMap.get(UNIT_NS);
		Assert.assertNotNull("unitList", unitList);
		String siNamespace = unitList.getSiNamespace();
		Assert.assertNotNull("siNamespace not null", siNamespace);
		Assert.assertEquals("siNamespace", SIUNIT_NS, siNamespace);
		CMLUnitList siUnitList = (CMLUnitList) unitListMap.get(siNamespace);
		Assert.assertNotNull("siUnitList not null", siUnitList);
		Assert.assertEquals("siUnitList title", "si units dictionary",
				siUnitList.getTitle());
		Assert.assertEquals("siUnitList namespace", SIUNIT_NS, siUnitList
				.getNamespace());
		Assert.assertEquals("siUnitList siNamespace", SIUNIT_NS, siUnitList
				.getSiNamespace());
		Assert.assertTrue("is SIUnitList", siUnitList.isSIUnitList());
		Assert.assertFalse("is not SIUnitList", unitList.isSIUnitList());
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.getUnit(String)'
	 */
	@Test
	public void testGetUnit() {
		CMLUnit unitX = unitList.getUnit("ang");
		Assert.assertNotNull("unit not null", unitX);
		Assert.assertEquals("unit title", "Angstrom", unitX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitList.setSIUnitList(CMLUnitList)'
	 */
	@Test
	public void testSetSIUnitList() {
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitList.getSIUnitList(NamespaceToUnitListMap)'
	 */
	@Test
	public void testGetSIUnitListUnitListMap() {
		// just a setter at present, so nothing to test
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.isSIUnitList()'
	 */
	@Test
	public void testIsSIUnitList() {
		Assert.assertFalse("not si list", unitList.isSIUnitList());
		Assert.assertTrue("is si list", siUnitList.isSIUnitList());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitList.setUnitListMap(NamespaceToUnitListMap)'
	 */
	@Test
	public void testSetUnitListMap() {
		// simple setter
	}

	/**
	 * check SI lists for consistency.
	 *
	 */
	@Test
	public void testCheckSIUnitList() {
		CMLElements<CMLUnit> siUnits = siUnitList.getUnitElements();
		for (CMLUnit siUnit : siUnits) {
			if (!siUnit.isSIUnit()) {
				Assert.fail("should be si unit " + siUnit.getId());
			}
			CMLUnitType unitType = siUnit.getCMLUnitType();
			Assert.assertNotNull("unit type not null: " + siUnit.getId(),
					unitType);
		}
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.indexEntries()'
	 */
	@Test
	public void testIndexEntries() {
		// tested by getUnit
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitList.size()'
	 */
	@Test
	public void testSize() {
		// tested elsewhere
	}


}
