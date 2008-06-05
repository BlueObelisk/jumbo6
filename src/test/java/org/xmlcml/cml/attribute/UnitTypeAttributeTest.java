package org.xmlcml.cml.attribute;

import static org.xmlcml.cml.base.CMLConstants.CATALOG_XML;
import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.cml.base.CMLConstants.NUNIT_DICT;
import static org.xmlcml.cml.base.CMLConstants.UNITTYPES_NS;
import static org.xmlcml.cml.base.CMLConstants.UNIT_NS;
import static org.xmlcml.cml.base.CMLConstants.U_DEGREE;
import static org.xmlcml.cml.base.CMLConstants.XML_SUFF;
import static org.xmlcml.cml.element.AbstractTest.COMPLEX_RESOURCE;
import static org.xmlcml.cml.element.AbstractTest.UNIT_RESOURCE;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.CMLUnitList;
import org.xmlcml.cml.map.NamespaceToUnitListMap;
import org.xmlcml.euclid.Util;

/**
 * attribute supporting unitType.
 *
 * @author pmr
 *
 */
public class UnitTypeAttributeTest {

	CMLUnitList unitList = null;

	String unitTypeS = "<c:cml "
			+ "  type='unit'"
			+ "xmlns:c='"
			+ CML_NS
			+ "' "
			+ "xmlns:units='"
			+ UNIT_NS
			+ "' "
			+ "xmlns:unitsComp='http://www.xml-cml.org/units/comp'>"
			+ "<c:scalar id='s1' dictRef='cmlDict:angle' units='"
			+ U_DEGREE
			+ "'>123</c:scalar>"
			+ "<c:scalar id='s2' dictRef='foo:bar' units='units:foo'>456</c:scalar>"
			+ "<c:scalar id='s3' dictRef='cmlComp:ionPot' units='units:volt'>123</c:scalar>"
			+ "</c:cml>";

	String unitTypeNS = "<unitList " + "  type='unit'" + "  xmlns='" + CML_NS
			+ "' " + "  xmlns:unitType='" + UNITTYPES_NS + "'>"
			+ "  <unit id='mm' name='millimetre' unitType='unitType:length'>"
			+ "    <description>omitted</description>" + "  </unit>"
			+ "  <unit id='g' name='gram' unitType='unitType:mass'>"
			+ "    <description>omitted</description>" + "  </unit>"
			+ "</unitList>";

	/**
	 * get prefix and namespace. use namespace declaration in instance file.
	 *
	 */
	@Test
	public void testGetPrefixAndNamespaceURI() {
		unitList = (CMLUnitList) parseValidString(unitTypeNS);
		Assert.assertNotNull("unitList ", unitList);
		Assert.assertEquals("type ", "unit", unitList.getType());
		List<CMLElement> children = unitList.getChildCMLElements();
		CMLUnit unit0 = (CMLUnit) children.get(0);
		UnitTypeAttribute unitType0 = (UnitTypeAttribute) unit0
				.getUnitTypeAttribute();
		String prefix0 = unitType0.getPrefix();
		Assert.assertEquals("prefix ", "unitType", prefix0);
		String namespace0 = unitType0.getNamespaceURIString();
		Assert.assertEquals("namespaceURI ", UNITTYPES_NS, namespace0);
		String unitTypeId0 = unitType0.getIdRef();
		Assert.assertNotNull("namespace ", unitTypeId0);
		Assert.assertEquals("unitType ", "length", unitTypeId0);

		CMLUnit unit1 = (CMLUnit) children.get(1);
		UnitTypeAttribute unitType1 = (UnitTypeAttribute) unit1
				.getUnitTypeAttribute();
		String prefix1 = unitType1.getPrefix();
		Assert.assertEquals("prefix ", "unitType", prefix1);
		String namespace1 = unitType1.getNamespaceURIString();
		Assert.assertNotNull("namespace not null", namespace1);
	}

	/**
	 * find dictionaries relevant to document or element.
	 * @throws IOException
	 */
	@Test
	public void testGetDictionaries() throws IOException {
		try {
			unitList = (CMLUnitList) new CMLBuilder().build(
					new StringReader(unitTypeNS)).getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("should not throw " + e);
		}
		Assert.assertNotNull("unitList ", unitList);
		Assert.assertEquals("type ", "unit", unitList.getType());
		List<CMLElement> children = unitList.getChildCMLElements();
		CMLUnit unit0 = (CMLUnit) children.get(0);
		UnitTypeAttribute unitType0 = (UnitTypeAttribute) unit0
				.getUnitTypeAttribute();
		String prefix0 = unitType0.getPrefix();
		Assert.assertEquals("prefix ", "unitType", prefix0);
		String namespace0 = unitType0.getNamespaceURIString();
		Assert.assertEquals("namespaceURI ", UNITTYPES_NS, namespace0);
		String unitTypeId0 = unitType0.getIdRef();
		Assert.assertNotNull("namespace ", unitTypeId0);
		Assert.assertEquals("unitType ", "length", unitTypeId0);
		NamespaceToUnitListMap unitListMap = null;
		unitListMap = new NamespaceToUnitListMap(
				Util.getResource(UNIT_RESOURCE + U_S + CATALOG_XML), new CMLUnitList());
		Assert.assertEquals("unitList count", NUNIT_DICT, unitListMap.size());

		/**
		 * - <unitsUnitList namespace='"+CML_DICT_NS+"'
		 * unitsUnitListPrefix="cmlDict" title="CML unitsUnitList" xmlns=CML_NS
		 * xmlns:cmlDict='"+CML_DICT_NS+"'
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema/">
		 */
		// FIXME
		/*--
		 CMLUnitList cmlUnitTypeList0 = unitListMap.getUnitList(namespace0);
		 Assert.assertNotNull("unitList is not null", cmlUnitTypeList0);
		 Assert.assertEquals("unitList ", "unitList", cmlUnitTypeList0.getLocalName());
		 // FIXME
		 //		Assert.assertEquals("unitList prefix", "unitType", cmlUnitTypeList0.getDictionaryPrefix());
		 Assert.assertEquals("unitList namespace", namespace0, cmlUnitTypeList0.getNamespace());
		 Assert.assertEquals("unitList title", "unit type dictionary", cmlUnitTypeList0.getTitle());
		 int namespaceCount = cmlUnitTypeList0.getNamespaceDeclarationCount();
		 Assert.assertEquals("unitList namespace count", 1, namespaceCount);
		 String namespacePrefix = cmlUnitTypeList0.getNamespacePrefix(0);
		 Assert.assertEquals("namespace ", S_EMPTY, namespacePrefix);
		 String namespaceURI = cmlUnitTypeList0.getNamespaceURI(namespacePrefix);
		 Assert.assertEquals("namespaceURI ", CML_NS, namespaceURI);
		 --*/
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
		/*--
		 String unitId0 = unit0.getIdRef();
		 Assert.assertNotNull("unitId ", unitId0);
		 Assert.assertEquals("unit ", "degree", unitId0);
		 CMLUnit unit = cmlUnitList0.getUnit(unitId0);
		 Assert.assertNotNull("unit ", unit);
		 Assert.assertEquals("unit cml ", 1, unit.getChildCMLElements().size());


		 CMLScalar scalar2 = (CMLScalar) children.get(2);
		 UnitsAttribute units2 = (UnitsAttribute) scalar2.getUnitsAttribute();
		 CMLUnit unit2 = unitListMap.getUnit(units2);
		 Assert.assertNotNull("unit ", unit);
		 Assert.assertEquals("unit comp ", 1, unit.getChildCMLElements().size());

		 String unitId2 = units2.getIdRef();
		 Assert.assertNotNull("unitId ", unitId2);
		 Assert.assertEquals("unit ", "volt", unitId2);
		 --*/
	}

	/**
	 * large test example.
	 *
	 * @param filename
	 * @param ndict
     * @exception Exception
	 */
	public void largeExample(String filename, int ndict) throws Exception {
		int NERR = 20;
		NamespaceToUnitListMap unitsUnitListMap = null;
		unitsUnitListMap = new NamespaceToUnitListMap(
				Util.getResource(UNIT_RESOURCE + U_S + CATALOG_XML), new CMLUnitList());
		CMLCml cml = null;
		InputStream in = Util.getInputStreamFromResource(COMPLEX_RESOURCE
				+ filename + XML_SUFF);
		cml = (CMLCml) new CMLBuilder().build(in).getRootElement();
		in.close();
		int namespaceCount = cml.getNamespaceDeclarationCount();
		Assert.assertEquals("namespaces ", ndict, namespaceCount);

		// scalars
		List<CMLElement> scalars = cml.getElements(".//"+CMLScalar.NS);
		int count = 0;
		for (CMLElement scalar : scalars) {
			UnitsAttribute unitsAttribute = (UnitsAttribute) ((CMLScalar) scalar)
					.getUnitsAttribute();
			// get parent (e.gt. for property/scalar
			if (unitsAttribute == null) {
				unitsAttribute = (UnitsAttribute) ((Element) scalar.getParent())
						.getAttribute("units");
			}
			if (unitsAttribute == null) {
				Assert.fail("Missing dictRef");
			} else {
				CMLUnit unit = unitsUnitListMap.getUnit(unitsAttribute);
				if (unit == null && count++ < NERR) {
					Assert.fail("Missing units");
					// System.out.println("NOTFOUND "+unitsAttribute);
				} else {
					// System.out.println("FOUND "+unitsAttribute);
				}
			}
		}

	}

	/**
	 * run tests.
	 *
	 * @return the suite.
	 *
	 */
 }
