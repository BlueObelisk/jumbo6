package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLEntry;

public class EntryToolTest {

	@Test
	@Ignore
	public void testCreateStringScalar() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleScalar() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerScalar() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateStringArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDoubleScalarOrDoubleArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateIntegerScalarOrIntegerArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateStringScalarOrStringArray() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateParameter() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateDate() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateFormula() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateVector3() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckArrayLength() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckNumericValue() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckDoubleRange() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckIntegerRange() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testEnsureEnumerations() {
		Assert.fail("Not yet implemented");
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
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCheckEmptyName() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAddValue() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testUpdateEnumerations() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testCreateTerm() {
		Assert.fail("Not yet implemented");
	}

}
