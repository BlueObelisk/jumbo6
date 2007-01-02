package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.DictRefAttribute;
import org.xmlcml.cml.element.DictionaryMap;
import org.xmlcml.euclid.Util;

/**
 * test DictRef attribute.
 *
 * @author pmr
 *
 */
public class DictRefAttributeTest extends AbstractTest {

	CMLCml cml = null;

	String dictRefS = "<c:cml " + "xmlns:c='" + CML_NS + "'"
			+ " xmlns:cmlDict='" + CML_DICT_NS + "'"
			+ " xmlns:cmlComp='http://www.xml-cml.org/dict/" + CML_COMP_DICT
			+ "'>" + "<c:scalar id='s1' dictRef='cmlDict:angle'>123</c:scalar>"
			+ "<c:scalar id='s2' dictRef='foo:bar'>456</c:scalar>"
			+ "<c:scalar id='s3' dictRef='cmlComp:ionPot'>123</c:scalar>"
			+ "</c:cml>";

	String dictRefNS = "<cml " + "xmlns='" + CML_NS + "' " + "xmlns:cmlDict='"
			+ CML_DICT_NS + "'>"
			+ "<scalar id='s1' dictRef='cmlDict:angle'>123</scalar>"
			+ "<scalar id='s2' dictRef='foo:bar'>456</scalar>"
			+ "<scalar id='s3' dictRef='cmlComp:ionPot'>123</scalar>"
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

	/** test */
	@Test
	public void testGetPrefixAndNamespaceURI() {
		cml = (CMLCml) parseValidString(dictRefNS);
		List<CMLElement> children = cml.getChildCMLElements();
		CMLScalar scalar0 = (CMLScalar) children.get(0);
		DictRefAttribute dictRef0 = (DictRefAttribute) scalar0
				.getDictRefAttribute();
		String prefix0 = dictRef0.getPrefix();
		Assert.assertEquals("prefix ", "cmlDict", prefix0);
		String namespace0 = dictRef0.getNamespaceURIString();
		Assert.assertEquals("prefix ", CML_DICT_NS, namespace0);
		String entryId0 = dictRef0.getIdRef();
		Assert.assertNotNull("namespace ", entryId0);
		Assert.assertEquals("entry ", "angle", entryId0);

		CMLScalar scalar1 = (CMLScalar) children.get(1);
		DictRefAttribute dictRef1 = (DictRefAttribute) scalar1
				.getDictRefAttribute();
		String prefix1 = dictRef1.getPrefix();
		Assert.assertEquals("prefix ", "foo", prefix1);
		String namespace1 = dictRef1.getNamespaceURIString();
		Assert.assertNull("namespace ", namespace1);
	}

	/** test */
	@Test
	public void testGetDictionaries() {
		try {
			cml = (CMLCml) new CMLBuilder().build(new StringReader(dictRefNS))
					.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("should not throw " + e);
		}
		List<CMLElement> children = cml.getChildCMLElements();
		CMLScalar scalar0 = (CMLScalar) children.get(0);
		DictRefAttribute dictRef0 = (DictRefAttribute) scalar0
				.getDictRefAttribute();
		String namespace0 = dictRef0.getNamespaceURIString();
		Assert.assertEquals("namespace ", CML_DICT_NS, namespace0);
		DictionaryMap dictionaryMap = null;
		try {
			dictionaryMap = new DictionaryMap(Util.getResource(DICT_RESOURCE
					+ U_S + CATALOG_XML));
		} catch (IOException e) {
			Assert.fail("should not throw " + e);
		}
		Assert.assertEquals("dictionary count", NDICT, dictionaryMap.size());

		/**
		 * - <dictionary namespace='"+CML_DICT_NS+"' dictionaryPrefix="cmlDict"
		 * title="CML dictionary" xmlns=CML_NS xmlns:cmlDict='"+CML_DICT_NS+"'
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		 */
		CMLDictionary cmlDictionary0 = (CMLDictionary) dictionaryMap
				.get(namespace0);
		Assert.assertNotNull("dictionary is not null", cmlDictionary0);
		Assert.assertEquals("dictionary ", "dictionary", cmlDictionary0
				.getLocalName());
		Assert.assertEquals("dictionary prefix", CML_DICT_DICT, cmlDictionary0
				.getDictionaryPrefix());
		Assert.assertEquals("dictionary namespace", namespace0, cmlDictionary0
				.getNamespace());
		Assert.assertEquals("dictionary title", "CML dictionary",
				cmlDictionary0.getTitle());
		int namespaceCount = cmlDictionary0.getNamespaceDeclarationCount();
		Assert.assertEquals("dictionary namespace count", 3, namespaceCount);
		String namespacePrefix0 = cmlDictionary0.getNamespacePrefix(0);
		Assert.assertEquals("namespace0 ", S_EMPTY, namespacePrefix0);
		String namespaceURI0 = cmlDictionary0.getNamespaceURI(namespacePrefix0);
		Assert.assertEquals("namespaceURI0 ", CML_NS, namespaceURI0);
		String namespacePrefix1 = cmlDictionary0.getNamespacePrefix(1);
		Assert.assertEquals("namespace ", CML_DICT_DICT, namespacePrefix1);
		String namespaceURI1 = cmlDictionary0.getNamespaceURI(namespacePrefix1);
		Assert.assertEquals("namespaceURI1 ", CML_DICT_NS, namespaceURI1);
		String namespacePrefix2 = cmlDictionary0.getNamespacePrefix(2);
		Assert.assertEquals("namespace ", "xsd", namespacePrefix2);
		String namespaceURI2 = cmlDictionary0.getNamespaceURI(namespacePrefix2);
		Assert.assertEquals("namespaceURI ", XSD_NS, namespaceURI2);

		/*--
		 <entry id="angle" term="Angle">
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
		 </entry>
		 */
		String entryId0 = dictRef0.getIdRef();
		Assert.assertNotNull("entryId ", entryId0);
		Assert.assertEquals("entry ", "angle", entryId0);
		CMLEntry entry = cmlDictionary0.getCMLEntry(entryId0);
		Assert.assertNotNull("entry ", entry);
		Assert
				.assertEquals("entry cml ", 4, entry.getChildCMLElements()
						.size());

		CMLScalar scalar2 = (CMLScalar) children.get(2);
		DictRefAttribute dictRef2 = (DictRefAttribute) scalar2
				.getDictRefAttribute();
		CMLEntry entry2 = (CMLEntry) dictionaryMap.getEntry(dictRef2);
		// FIXME we need a dictionary
		Assert.assertNull("entry should be null: " + dictRef2, entry2);
		// Assert.assertEquals("entry comp ", 4,
		// entry2.getChildCMLElements().size());

		// String entryId2 = dictRef2.getIdRef();
		// Assert.assertNotNull("entryId ", entryId2);
		// Assert.assertEquals("entry ", "ionPot", entryId2);

	}

	/**
	 * large example test.
	 *
	 * @param filename
	 * @param ndict
	 * @throws IOException
	 * @throws ParsingException
	 * @throws ValidityException
	 */
	public void largeExample(String filename, int ndict) throws IOException,
			ValidityException, ParsingException {
		DictionaryMap dictionaryMap = null;
		CMLElement rootElement = null;
		InputStream in = Util.getInputStreamFromResource(COMPLEX_RESOURCE
				+ U_S + filename + XML_SUFF);
		rootElement = (CMLElement) new CMLBuilder().build(in).getRootElement();
		in.close();
		dictionaryMap = new DictionaryMap(Util.getResource(DICT_RESOURCE
				+ U_S + CATALOG_XML));
		List<String> errorList = new DictRefAttribute().checkAttribute(
				rootElement, dictionaryMap);
		if (errorList.size() > 0) {
			for (String error : errorList) {
				System.err.println(error);
			}
			Assert.fail("should not throw above errors");
		}
	}

	/**
	 * test
	 *
	 * @throws ParsingException
	 * @throws IOException
	 * @throws ValidityException
	 */
	@Test
	public void testTacExample() throws ValidityException, IOException,
			ParsingException {
		System.out.println("=========TaC=========");
		largeExample("TaC", 5);
	}

	/**
	 * test
	 *
	 * @throws ParsingException
	 * @throws IOException
	 * @throws ValidityException
	 */
	@Test
	public void test4CDFExample() throws ValidityException, IOException,
			ParsingException {
		System.out.println("=========4CDF=========");
		largeExample("4CDF", 6);
	}

	/**
	 * run tests.
	 *
	 * @return the suite.
	 *
	 */
 }
