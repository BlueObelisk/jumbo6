package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParsingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Util;

/**
 * test CMLDictionary.
 *
 * @author pmr
 *
 */
public class CMLDictionaryTest extends AbstractTest {
	final static Logger logger = Logger
			.getLogger(CMLDictionary.class.getName());

	CMLDictionary xomDict1 = null;

	// read into xom;
	String xmlDict1S = S_EMPTY
			+ "<dictionary title='dictionary1 example'"
			+ " xmlns:h='http://www.w3.org/XHTML' "
			+ CML_XMLNS
			+ ">"
			+ "<entry id='a001' term='Amplitude for CHARGE density mixing' >"
			+ "    <annotation>"
			+ "       <documentation>"
			+
			// " <h:div class='summary'>Amplitude for CHARGE density
			// mixing</h:div>"+
			// " <h:div class='description'>Not yet filled in...</h:div>"+
			"       </documentation>" + "   </annotation>"
			+ "   <alternative type='abbreviation'>CDMixAmp</alternative>"
			+ "</entry>" + "</dictionary>" + S_EMPTY;

	Document xmlDict1Doc = null;

	CMLDictionary xmlDict1 = null;

	/**
	 * setup.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		// build from scratch
		xomDict1 = new CMLDictionary();
		try {
			xmlDict1Doc = builder.build(new StringReader(xmlDict1S));
		} catch (IOException e) {
			Assert.fail("Should not throw IOException");
		} catch (ParsingException e) {
			e.printStackTrace();
			logger.severe("Parse exception " + e.getMessage());
			Assert.fail("Should not throw ParsingException" + e.getCause());
		}
		xmlDict1 = (CMLDictionary) xmlDict1Doc.getRootElement();
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLDictionary.copy()'
	 */
	@Test
	public void testCopy() {
		Node copy = xomDict1.copy();
		Assert.assertEquals("class should be CMLform: ", copy.getClass(),
				CMLDictionary.class);
		CMLDictionary copyDict = (CMLDictionary) copy;
		Assert.assertEquals("formula is identical", copyDict
				.compareTo(xomDict1), 0);
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLDictionary.CMLDictionary()'
	 */
	@Test
	public void testCMLDictionary() {
		CMLDictionary formula = new CMLDictionary();
		Assert.assertNotNull("constructor ", formula);
		Assert.assertNull("no id attribute", formula.getIdAttribute());
		Assert.assertEquals("no children", formula.getChildCount(), 0);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.CMLDictionary(CMLDictionary)'
	 */
	@Test
	public void testCMLDictionaryCMLDictionary() {
		// copy constructor
		CMLDictionary xformula = xomDict1;
		CMLDictionary formula = new CMLDictionary(xformula);
		Assert.assertNotNull("constructor ", formula);
		CMLDictionary copyDict = new CMLDictionary(xmlDict1);
		String s1 = copyDict.getCanonicalString();
		String s2 = xmlDict1.getCanonicalString();
		Assert.assertEquals("formula is identical", s1, s2);
		Assert.assertEquals("formula is identical", 0, copyDict
				.compareTo(xmlDict1));
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLDictionary.getEntry(String)'
	 */
	@Test
	public void testGetEntry() {
		CMLEntry entry = xomDict1.getCMLEntry("a001");
		Assert.assertNull("entry should be null", entry);
		CMLElements<CMLEntry> entrys = xmlDict1.getEntryElements();
		Assert.assertEquals("entry count ", 1, entrys.size());
		// entry = entrys.get(0);
		entry = xmlDict1.getCMLEntry("a001");
		Assert.assertNotNull("entry should not be null", entry);
		Assert.assertEquals("id ", "a001", entry.getId());
		Assert.assertEquals("term ", "Amplitude for CHARGE density mixing",
				entry.getTerm());
		entry = xmlDict1.getCMLEntry("a002");
		Assert.assertNull("entry should be null", entry);
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLDictionary.addEntry(CMLEntry)'
	 */
	@Test
	public void testAddEntryCMLEntry() {
		CMLEntry entry = xmlDict1.getCMLEntry("a002");
		Assert.assertNull("entry should be null", entry);
		entry = new CMLEntry("a002");
		try {
			xmlDict1.addEntry(entry);
		} catch (CMLException e) {
			Assert.fail("should not throw " + e);
		}
		CMLEntry entry1 = xmlDict1.getCMLEntry("a002");
		Assert.assertNotNull("entry should not be null", entry);
		Assert.assertSame("entries are the same", entry, entry1);
		entry = new CMLEntry("a002");
		try {
			xmlDict1.addEntry(entry);
			Assert.fail("should throw ");
		} catch (CMLException e) {
			Assert
					.assertEquals(
							"duplicate entry",
							"org.xmlcml.cml.base.CMLException: Entry for a002 already present",
							S_EMPTY + e);
		}
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.removeEntry(CMLEntry)'
	 */
	@Test
	public void testRemoveEntry() {
		CMLEntry entry = new CMLEntry("a002");
		try {
			xmlDict1.addEntry(entry);
		} catch (CMLException e) {
			Assert.fail("should not throw " + e);
		}
		CMLEntry entry1 = xmlDict1.getCMLEntry("a002");
		Assert.assertNotNull("entry should not be null", entry);
		Assert.assertEquals("dictionary size", 2, xmlDict1.getEntryElements()
				.size());
		xmlDict1.removeEntry(entry);
		Assert.assertEquals("dictionary size", 1, xmlDict1.getEntryElements()
				.size());
		entry1 = xmlDict1.getCMLEntry("a002");
		Assert.assertNull("entry should be null", entry1);
		Assert.assertEquals("dictionary size", 1, xmlDict1.getEntryElements()
				.size());
	}

	/** */
	@Test
	public void testGetDictionariesString() {
		Map<String, GenericDictionary> dictionaryMap = null;
		try {
			dictionaryMap = new DictionaryMap(Util.getResource(DICT_RESOURCE
					+ U_S + CATALOG_XML));
		} catch (CMLRuntimeException e) {
			Assert.fail("should not throw " + e);
		} catch (IOException e) {
			Assert.fail("should not throw " + e);
		}
		Assert.assertNotNull("dictionaries not null", dictionaryMap);
	}

	/**
	 * make an index of dictionaries by namespace.
	 *
	 */
	@Test
	public void testGetDictionaryMapListCMLDictionary() {
		DictionaryMap dictionaryMap = null;
		try {
			dictionaryMap = new DictionaryMap(Util.getResource(DICT_RESOURCE
					+ U_S + CATALOG_XML));
		} catch (IOException e) {
			Assert.fail("should not throw " + e);
		}
		for (String s : dictionaryMap.keySet()) {
			CMLDictionary dictionary = (CMLDictionary) dictionaryMap.get(s);
			// System.out.println(s+"..."+dictionary.getTitle()+"
			// ["+dictionary.getEntryElements().size()+S_RSQUARE);
			Assert.assertNotNull(dictionary);
		}
	}

	/** test dictionary.
     * @exception Exception
     * */
	@Test
	public void testDictionaryExample() throws Exception {
		DictionaryMap dictionaryMap = null;
		try {
			dictionaryMap = new DictionaryMap(Util.getResource(DICT_RESOURCE
					+ U_S + CATALOG_XML));
		} catch (IOException e) {
			Assert.fail("should not throw " + e);
		}
		Assert.assertNotNull("map", dictionaryMap);
		CMLCml cml = null;
		InputStream in = Util.getInputStreamFromResource(COMPLEX_RESOURCE
				+ U_S + "TaC.xml");
		cml = (CMLCml) new CMLBuilder().build(in).getRootElement();
		in.close();
		Assert.assertNotNull("cml", cml);
		// doesn't really do anything
		/*--
		 List<CMLElement> children = cml.getChildCMLElements();
		 for (CMLElement child : children) {
		 processDictionary(child, dictionaryMap);
		 }
		 --*/
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLDictionary.indexEntries()'
	 */
	@Test
	public void testIndexEntries() {
		// this is called by getEntry();
		CMLEntry entry = xmlDict1.getCMLEntry("a001");
		Assert.assertNotNull("entry null", entry);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.createDictionary(File)'
	 * @throws Exception
	 */
	@Test
	public void testCreateDictionary() throws Exception {
		CMLDictionary dictionary = createDictionary();
		CMLElements entries = dictionary.getEntryElements();
		// this will depend on the dictionary
		Assert.assertEquals("entries ", 40, entries.size());
	}

	private CMLDictionary createDictionary() throws Exception {
		CMLDictionary dictionary = null;
			InputStream in = Util.getInputStreamFromResource(DICT_RESOURCE + U_S
							+ "cmlDict.xml");
			dictionary = (CMLDictionary) new CMLBuilder().build(
					in).getRootElement();
			in.close();
		return dictionary;
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.getCMLEntry(String)'
	 */
	@Test
	public void testGetCMLEntry() {
		CMLEntry entry = xmlDict1.getCMLEntry("a001");
		Assert.assertNotNull("entry null", entry);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.removeEntryById(String)'
	 * @throws Exception
	 */
	@Test
	public void testRemoveEntryById() throws Exception {
		CMLDictionary dictionary = createDictionary();
		CMLElements entries = dictionary.getEntryElements();
		// this will depend on the dictionary
		Assert.assertEquals("entries ", 40, entries.size());
		dictionary.removeEntryById("wave");
		entries = dictionary.getEntryElements();
		Assert.assertEquals("entries ", 39, entries.size());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLDictionary.createDictionaryMap(File, boolean)'
	 */
	@Test
	public void testCreateDictionaryMap() {
		// no-op
	}

 }
