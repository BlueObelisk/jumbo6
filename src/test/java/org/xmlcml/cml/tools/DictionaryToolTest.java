package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.util.TestUtils;

public class DictionaryToolTest {

	@Test
	@Ignore
	public void testExtractAndAnalyseDictRef() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeIndex() throws Exception {
		String d = "" +
				"<dictionary xmlns='http://www.xml-cml.org/schema'>" +
				"<entry id='e1' term='t1'>" +
				"</entry>" +
				"<entry id='e2' term='t2'>" +
				"</entry>" +
				"</dictionary>";
		CMLDictionary dictionary = (CMLDictionary) new CMLBuilder().parseString(d);
		DictionaryTool dictionaryTool = DictionaryTool.getOrCreateTool(dictionary);
		Map<String, CMLEntry> idIndex = dictionaryTool.makeIndex("@id");
		Assert.assertEquals("size", 2, idIndex.size());
		CMLEntry entry = idIndex.get("e1");
		TestUtils.assertEqualsCanonically("entry1", 
				new CMLBuilder().parseString("<entry id='e1' term='t1' xmlns='http://www.xml-cml.org/schema'/>"),
				entry, true);
		entry = idIndex.get("e3");
		Assert.assertNull("entry3",entry);
		
		Map<String, CMLEntry> termIndex = dictionaryTool.makeIndex("@term");
		Assert.assertEquals("size", 2, termIndex.size());
		entry = termIndex.get("t1");
		TestUtils.assertEqualsCanonically("entry1", 
				new CMLBuilder().parseString("<entry id='e1' term='t1' xmlns='http://www.xml-cml.org/schema'/>"),
				entry, true);
		entry = termIndex.get("t3");
		Assert.assertNull("entry3",entry);
	}

			

	@Test
	@Ignore
	public void testValidateDictRefsInCML() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateTypedNameValue() {
		fail("Not yet implemented");
	}

}