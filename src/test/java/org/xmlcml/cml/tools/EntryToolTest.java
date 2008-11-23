package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLEntry;

public class EntryToolTest {

	@Test
	@Ignore
	public void testCreateStringScalar() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleScalar() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerScalar() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateStringArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleScalarOrDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerScalarOrIntegerArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateStringScalarOrStringArray() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateParameter() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDate() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateFormula() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateVector3() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckArrayLength() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckNumericValue() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckDoubleRange() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckIntegerRange() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEnsureEnumerations() {
		fail("Not yet implemented");
	}

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

	@Test
	@Ignore
	public void testCheckPattern() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckEmptyName() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAddValue() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testUpdateEnumerations() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateTerm() {
		fail("Not yet implemented");
	}

}
