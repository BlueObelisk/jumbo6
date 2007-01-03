package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Util;

/**
 * tests unitTypeList.
 *
 * @author pm286
 *
 */
public class CMLUnitTypeListTest extends AbstractUnitTest {

	/**
	 * setup.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitTypeList.copy()'
	 */
	@Test
	public void testCopy() {
		CMLUnitTypeList unitTypeListX = (CMLUnitTypeList) unitTypeList.copy();
		@SuppressWarnings("unused")
		Map unitListMap = unitTypeListX.getUnitListMap();
		// Assert.assertNotNull("unitlistMap not null", unitListMap);
		Assert.assertTrue("unitlistMap equals",
				(unitTypeList.getUnitListMap() == null && unitTypeListX
						.getUnitListMap() == null)
						|| unitTypeListX.getUnitListMap() == unitTypeList
								.getUnitListMap());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.writeHTML(Writer)'
	 * @throws IOException
	 */
	@Test
	public void testWriteHTML() throws IOException {
		// this just writes HTML to the same directory. Check by eye!
		writeHTML(unitTypeList, Util.getTEMP_DIRECTORY() + File.separator
				+ UNIT_RESOURCE + File.separator + "html" + File.separator
				+ "unitTypeList.html");
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLUnitTypeList.indexEntries()'
	 */
	@Test
	public void testIndexEntries() {
		// called by getUnitType
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.createUnitTypeList(File)'
	 */
	@Test
	public void testCreateUnitTypeList() {
		CMLUnitTypeList unitTypeListX = null;
		try {
			unitTypeListX = CMLUnitTypeList.createUnitTypeList(Util
					.getResource(UNIT_RESOURCE + U_S + "unitTypeDict.xml"));
		} catch (IOException e) {
			throw new CMLRuntimeException("Cannot open unitTypeList file: " + e);
		} catch (CMLException e) {
			throw new CMLRuntimeException("Cannot parse unitTypeList file: " + e);
		}
		Assert.assertEquals("unitTypeList ", 74, unitTypeListX.size());
		CMLUnitType unitTypeX = unitTypeListX.getUnitType("length");
		Assert.assertNotNull("unitType ", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.addUnitType(CMLUnitType)'
	 */
	@Test
	public void testAddUnitTypeCMLUnitType() {
		Assert.assertEquals("unitTypeList ", 74, unitTypeList.size());
		CMLUnitType unitTypeF = unitTypeList.getUnitType("foo");
		Assert.assertNull("unitType ", unitTypeF);
		CMLUnitType unitTypeX = unitTypeList.getUnitType("length");
		Assert.assertNotNull("unitType not null", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());

		CMLUnitType unitTypeZ = new CMLUnitType();
		unitTypeZ.setId("foo");
		// unitTypeZ.setMultiplierToSI(100.0);
		unitTypeZ.setTitle("foo unitTypes");
		// unitTypeZ.setUnitTypeType(NamespaceRefAttribute.createValue("unitTypeType",
		// "bar"));
		try {
			unitTypeList.addUnitType(unitTypeZ);
		} catch (CMLRuntimeException e) {
			neverThrow(e);
		}
		Assert.assertEquals("unitTypeList ", 75, unitTypeList.size());
		unitTypeF = unitTypeList.getUnitType("foo");
		Assert.assertNotNull("unitType ", unitTypeF);
		Assert.assertEquals("unitType title", "foo unitTypes", unitTypeF
				.getTitle());
		unitTypeX = unitTypeList.getUnitType("length");
		Assert.assertNotNull("unitType not null", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
		// now remove the damage
		unitTypeList.removeUnitType(unitTypeF);
		Assert.assertEquals("unitTypeList ", 74, unitTypeList.size());
		unitTypeF = unitTypeList.getUnitType("foo");
		Assert.assertNull("unitType ", unitTypeF);
		unitTypeX = unitTypeList.getUnitType("length");
		Assert.assertNotNull("unitType ", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.removeUnitType(CMLUnitType)'
	 */
	@Test
	public void testRemoveUnitType() {
		Assert.assertEquals("unitTypeList ", 74, unitTypeList.size());
		CMLUnitType unitTypeX = unitTypeList.getUnitType("length");
		Assert.assertNotNull("unitType ", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
		unitTypeList.removeUnitType(unitTypeX);
		Assert.assertEquals("unitTypeList ", 73, unitTypeList.size());
		CMLUnitType unitTypeXX = unitTypeList.getUnitType("length");
		Assert.assertNull("unitType null", unitTypeXX);
		unitTypeList.addUnitType(unitTypeX);
		Assert.assertEquals("unitTypeList ", 74, unitTypeList.size());
		unitTypeX = unitTypeList.getUnitType("length");
		Assert.assertNotNull("unitType ", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.getUnitType(String)'
	 */
	@Test
	public void testGetUnitType() {
		// alreadty tested
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.createDictionary(File)'
	 */
	@Test
	public void testCreateDictionary() {
		CMLUnitTypeList unitTypeListX = null;
		try {
			unitTypeListX = (CMLUnitTypeList) new CMLUnitTypeList()
					.createDictionary(Util.getResource(UNIT_RESOURCE + U_S
							+ "unitTypeDict.xml"));
		} catch (IOException e) {
			throw new CMLRuntimeException("Cannot open unitTypeList file: " + e);
		} catch (CMLRuntimeException e) {
			throw new CMLRuntimeException("Cannot parse unitTypeList file: " + e);
		}
		Assert.assertEquals("unitTypeList ", 74, unitTypeListX.size());
		CMLUnitType unitTypeX = unitTypeListX.getUnitType("length");
		Assert.assertNotNull("unitType ", unitTypeX);
		Assert.assertEquals("unitType title", "Length", unitTypeX.getTitle());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.createDictionaryMap(File,
	 * boolean)'
	 */
	@Test
	public void testCreateDictionaryMap() {
		// returns null
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.setSIUnitList(CMLUnitList)'
	 */
	@Test
	public void testGetSetSIUnitList() {
		// get/setter
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLUnitTypeList.setUnitListMap(NamespaceToUnitListMap)'
	 */
	@Test
	public void testGetSetUnitListMap() {
		// get/setter
	}

 }
