package org.xmlcml.cml.element;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.CMLUnitList;
import org.xmlcml.cml.element.NamespaceToUnitListMap;
import org.xmlcml.cml.element.UnitAttribute;
import org.xmlcml.euclid.Util;

/**
 * tests for unit attribute.
 *
 * @author pmr
 *
 */
public class UnitAttributeTest extends AbstractTest {

	CMLCml cml = null;

	String unitsS = "<c:cml "
			+ "xmlns:c='"
			+ CML_NS
			+ "' "
			+ "xmlns:siUnits='"
			+ SIUNIT_NS
			+ "' "
			+ "xmlns:units='"
			+ UNIT_NS
			+ "' "
			+ "xmlns:unitsComp='http://www.xml-cml.org/units/comp'>"
			+ "<c:scalar id='s1' dictRef='cmlDict:angle' units='"
			+ U_DEGREE
			+ "'>123</c:scalar>"
			+ "<c:scalar id='s2' dictRef='foo:bar' units='units:foo'>456</c:scalar>"
			+ "<c:scalar id='s3' dictRef='cmlComp:ionPot' units='siUnits:volt'>123</c:scalar>"
			+ "</c:cml>";

	String unitsNS = "<cml "
			+ "xmlns='"
			+ CML_NS
			+ "' "
			+ "xmlns:siUnits='"
			+ SIUNIT_NS
			+ "' "
			+ "xmlns:units='"
			+ UNIT_NS
			+ "'>"
			+ "<scalar id='s1' dictRef='cmlDict:angle' units='"
			+ U_DEGREE
			+ "'>123</scalar>"
			+ "<scalar id='s2' dictRef='foo:bar' units='units:foo'>456</scalar>"
			+ "<scalar id='s3' dictRef='cmlComp:ionPot' units='siUnits:volt'>123</scalar>"
			+ "</cml>";

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
	 * test.
	 *
	 *
	 */
	@Test
	public void testGetPrefixAndNamespaceURI() {
		cml = (CMLCml) parseValidString(unitsNS);
		List<CMLElement> children = cml.getChildCMLElements();
		CMLScalar scalar0 = (CMLScalar) children.get(0);
		UnitAttribute units0 = (UnitAttribute) scalar0.getUnitsAttribute();
		String prefix0 = units0.getPrefix();
		Assert.assertEquals("prefix ", "units", prefix0);
		String namespace0 = units0.getNamespaceURIString();
		Assert.assertEquals("prefix ", UNIT_NS, namespace0);
		String unitId0 = units0.getIdRef();
		Assert.assertNotNull("namespace ", unitId0);
		Assert.assertEquals("unit ", "deg", unitId0);

		CMLScalar scalar1 = (CMLScalar) children.get(1);
		UnitAttribute units1 = (UnitAttribute) scalar1.getUnitsAttribute();
		String prefix1 = units1.getPrefix();
		Assert.assertEquals("prefix ", "units", prefix1);
		String namespace1 = units1.getNamespaceURIString();
		Assert.assertNotNull("namespace not null", namespace1);
	}

	/**
	 * test.
	 *
	 * @exception Exception
	 */
	@Test
	public void testGetDictionaries() throws Exception {
		try {
			cml = (CMLCml) new CMLBuilder().build(new StringReader(unitsNS))
					.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("should not throw " + e);
		}
		List<CMLElement> children = cml.getChildCMLElements();
		CMLScalar scalar0 = (CMLScalar) children.get(0);
		UnitAttribute units0 = (UnitAttribute) scalar0.getUnitsAttribute();
		String namespace0 = units0.getNamespaceURIString();
		Assert.assertEquals("namespace ", UNIT_NS, namespace0);
		NamespaceToUnitListMap unitListMap = null;
		unitListMap = new NamespaceToUnitListMap(Util
				.getResource(UNIT_RESOURCE + U_S + CATALOG_XML));
		Assert.assertEquals("unitsList count", NUNIT_DICT, unitListMap.size());
		for (String s : unitListMap.keySet()) {
			s = "" + s;
			// System.out.println("Unit namespace: "+s);
		}

		/**
		 * - <unitsUnitList namespace='"+CML_DICT_NS+"'
		 * unitsUnitListPrefix="cmlDict" title="CML unitsUnitList" xmlns=CML_NS
		 * xmlns:cmlDict='"+CML_DICT_NS+"'
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema/">
		 */
		CMLUnitList cmlUnitList0 = (CMLUnitList) unitListMap.get(namespace0);
		Assert.assertNotNull("unitList is not null", cmlUnitList0);
		Assert.assertEquals("unitList ", "unitList", cmlUnitList0
				.getLocalName());
		Assert.assertEquals("unitList prefix", "units", cmlUnitList0
				.getDictionaryPrefix());
		Assert.assertEquals("unitList namespace", namespace0, cmlUnitList0
				.getNamespace());
		Assert.assertEquals("unitList title", "Simple units dictionary",
				cmlUnitList0.getTitle());
		int namespaceCount = cmlUnitList0.getNamespaceDeclarationCount();
		Assert.assertEquals("unitList namespace count", 5, namespaceCount);
		String namespacePrefix0 = cmlUnitList0.getNamespacePrefix(0);
		Assert.assertEquals("namespace0 ", "", namespacePrefix0);
		String namespaceURI0 = cmlUnitList0.getNamespaceURI(namespacePrefix0);
		Assert.assertEquals("namespaceURI0 ", CML_NS, namespaceURI0);
		String namespacePrefix1 = cmlUnitList0.getNamespacePrefix(1);
		Assert.assertEquals("namespace1 ", "si", namespacePrefix1);
		String namespaceURI1 = cmlUnitList0.getNamespaceURI(namespacePrefix1);
		Assert.assertEquals("namespaceURI1 ", SIUNIT_NS, namespaceURI1);

		/*--
		 <unit id="angle" term="Angle">
		 <annotation>
		 <appinfo>
		 <angle ref="cmlxsd:angle"/>
		 </appinfo>
		 </annotation>
		 <alternative type="abbreviation">ang</alternative>
		 <definition>
		 An angle defined by three atoms
		 </definition>
		 <description>
		 The atoms are described by atomRefs.
		 </description>
		 </unit>
		 */
		String unitId0 = units0.getIdRef();
		Assert.assertNotNull("unitId ", unitId0);
		Assert.assertEquals("unit ", "deg", unitId0);
		CMLUnit unit = (CMLUnit) cmlUnitList0.getUnit(unitId0);
		Assert.assertNotNull("unit should not be null", unit);
		Assert.assertEquals("unit cml ", 2, unit.getChildCMLElements().size());

		CMLScalar scalar2 = (CMLScalar) children.get(2);
		UnitAttribute units2 = (UnitAttribute) scalar2.getUnitsAttribute();
		CMLUnit unit2 = unitListMap.getUnit(units2);
		Assert.assertNotNull("unit2 should not be null ", unit2);
		Assert
				.assertEquals("unit comp ", 4, unit2.getChildCMLElements()
						.size());

		String unitId2 = units2.getIdRef();
		Assert.assertNotNull("unitId ", unitId2);
		Assert.assertEquals("unit ", "volt", unitId2);

	}

	/**
	 * test large example.
	 *
	 * @param filename
	 * @param ndict
     * @exception Exception
	 */
	public void largeExample(String filename, int ndict) throws Exception {
		NamespaceToUnitListMap unitsUnitListMap = null;
		unitsUnitListMap = new NamespaceToUnitListMap(Util
				.getResource(UNIT_RESOURCE + U_S + CATALOG_XML));
		CMLCml cml = null;
		InputStream in = Util.getInputStreamFromResource(COMPLEX_RESOURCE
				+ U_S + filename + XML_SUFF);
		cml = (CMLCml) new CMLBuilder().build(in).getRootElement();
		in.close();
		int namespaceCount = cml.getNamespaceDeclarationCount();
		Assert.assertEquals("namespaces ", ndict, namespaceCount);

		// scalars
		List<CMLElement> scalars = cml.getElements(".//"+CMLScalar.NS);
		for (CMLElement scalar : scalars) {
			UnitAttribute unitsAttribute = (UnitAttribute) ((CMLScalar) scalar)
					.getUnitsAttribute();
			// get parent (e.gt. for property/scalar
			if (unitsAttribute == null) {
				unitsAttribute = (UnitAttribute) ((Element) scalar.getParent())
						.getAttribute("units");
			}
			if (unitsAttribute == null) {
				if (XSD_DOUBLE.equals(((CMLScalar) scalar).getDataType())) {
					Assert.fail("missing units");
				}
			} else {
				CMLUnit unit = unitsUnitListMap.getUnit(unitsAttribute);
				if (unit == null) {
					System.out.println("NOTFOUND " + unitsAttribute);
				} else {
					// System.out.println("FOUND "+unitsAttribute);
				}
			}
		}

	}

	/**
	 * test.
	 * @throws Exception
	 */
	@Test
	public void testTacExample() throws Exception {
		System.out.println("=========TaC=========");
		largeExample("TaC", 6);
	}

	/**
	 * test.
	 * @throws Exception
	 */
	@Test
	public void test4CDFExample() throws Exception {
		System.out.println("=========4CDF=========");
		largeExample("4CDF", 8);
	}

	/**
	 * run tests.
	 *
	 * @return the suite.
	 *
	 */
 }
