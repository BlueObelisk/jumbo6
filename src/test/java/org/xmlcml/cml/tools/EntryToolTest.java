package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLEntry;

public class EntryToolTest {


	@Test
	public void testContainsEnumeratedValue() throws Exception {
		String e = "" +
		"<entry id='e1' term='t1' " +
		" xmlns='http://www.xml-cml.org/schema'" +
		" xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'" +
		">" +
		"<cmlx:enumeration>a</cmlx:enumeration>" +
		"<cmlx:enumeration>b</cmlx:enumeration>" +
		"<cmlx:enumeration>C</cmlx:enumeration>" +
		"</entry>";
		
		CMLEntry entry = (CMLEntry) new CMLBuilder().parseString(e);
		EntryTool entryTool = EntryTool.getOrCreateTool(entry);
		entryTool.setIgnoreCaseOfEnumerations(false);
		Assert.assertTrue("a", entryTool.containsEnumeratedValue("a"));
		Assert.assertFalse("c", entryTool.containsEnumeratedValue("c"));
		Assert.assertFalse("M", entryTool.containsEnumeratedValue("M"));
		entryTool.setIgnoreCaseOfEnumerations(true);
		Assert.assertTrue("a", entryTool.containsEnumeratedValue("a"));
		Assert.assertTrue("c", entryTool.containsEnumeratedValue("C"));
		Assert.assertFalse("M", entryTool.containsEnumeratedValue("M"));
	}

}
