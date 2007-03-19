package org.xmlcml.cml.attribute;

import nu.xom.Attribute;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.AbstractTest;
import org.xmlcml.euclid.test.StringTest;

/** test
 * 
 * @author pm286
 *
 */
public class DelimiterAttributeTest extends AbstractTest {

	/** set up */
	@Before
	public void setUp() throws Exception {
	}

	/** */
	@Test
	public final void testSetCMLValue() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute();
		delimiterAttribute.setCMLValue(S_SLASH);
		Assert.assertEquals("simple delim ", S_SLASH, delimiterAttribute.getConcat());
	}

	/** */
	@Test
	public final void testDelimiterAttribute() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute();
		Assert.assertNull("simple concat ", delimiterAttribute.getConcat());
		Assert.assertNotNull("simple delim ", delimiterAttribute.getCMLValue());
		Assert.assertEquals("simple delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertNull("simple splitter ", delimiterAttribute.getSplitter());
	}

	/** */
	@Test
	public final void testDelimiterAttributeString() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());
		
		delimiterAttribute = new DelimiterAttribute(S_EMPTY);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());
		
		delimiterAttribute = new DelimiterAttribute(S_SPACE);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());
		
		delimiterAttribute = new DelimiterAttribute(S_SPACE+S_NL+S_TAB);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());
		
		delimiterAttribute = new DelimiterAttribute(S_PIPE);
		Assert.assertEquals("concat ", S_PIPE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_PIPE, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", "\\|", delimiterAttribute.getSplitter());
	}

	/**
	 */
	@Test
	public final void testDelimiterAttributeAttribute() {
		Attribute att = new Attribute(DelimiterAttribute.NAME, S_SLASH);
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(att);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());
		
		att = new Attribute(DelimiterAttribute.NAME, S_EMPTY);
		delimiterAttribute = new DelimiterAttribute(att);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());
	}

	/**
	 */
	@Test
	public final void testGetSplitContent() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());
		String content = "/1/2/3/4/5/";
		String[] ss = delimiterAttribute.getSplitContent(content);
		String[] sss = new String[] {"1", "2", "3", "4", "5"};
		StringTest.assertEquals("split ", sss, ss);
		
		content = "1/2/3/4/5/";
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		content = "1/2/3/4/5";
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		content = "/1/2/3/4/5";
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		content = "/1/2/3/4//5";
		sss = new String[] {"1", "2", "3", "4", "", "5"};
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		content = "//1/2/3/4//5";
		sss = new String[] {"", "1", "2", "3", "4", "", "5"};
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		delimiterAttribute = new DelimiterAttribute(S_SPACE);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());
		
		content = "1 2 3 4 5";
		sss = new String[] {"1", "2", "3", "4", "5"};
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
		
		content = " 1   2     3\n  4 \t 5  ";
		sss = new String[] {"1", "2", "3", "4", "5"};
		ss = delimiterAttribute.getSplitContent(content);
		StringTest.assertEquals("split ", sss, ss);
	}

	/**
	 */
	@Test
	public final void testCheckDelimiter() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		String s = "123";
		try {
			delimiterAttribute.checkDelimiter(s);
		} catch (CMLRuntimeException e) {
			Assert.fail("Should not throw "+e);
		}
		
		s = "1/23";
		try {
			delimiterAttribute.checkDelimiter(s);
			Assert.fail("Should throw exception");
		} catch (CMLRuntimeException e) {
			Assert.assertEquals("Should throw ", "cannot delimit {1/23} with {/}", e.getMessage());
		}
		
		delimiterAttribute = new DelimiterAttribute(S_SPACE);
		Assert.assertEquals("concat ", S_SPACE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_EMPTY, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_WHITEREGEX, delimiterAttribute.getSplitter());

		s = "123";
		try {
			delimiterAttribute.checkDelimiter(s);
		} catch (CMLRuntimeException e) {
			Assert.fail("Should not throw "+e);
		}
		
		s = "";
		try {
			delimiterAttribute.checkDelimiter(s);
		} catch (CMLRuntimeException e) {
			Assert.fail("Should not throw "+e);
		}
				
		s = " 123 ";
		try {
			delimiterAttribute.checkDelimiter(s);
			Assert.fail("Should throw exception");
		} catch (CMLRuntimeException e) {
			Assert.assertEquals("Should throw ", "cannot delimit { 123 } with { }", e.getMessage());
		}
		
		s = "1 23";
		try {
			delimiterAttribute.checkDelimiter(s);
			Assert.fail("Should throw exception");
		} catch (CMLRuntimeException e) {
			Assert.assertEquals("Should throw ", "cannot delimit {1 23} with { }", e.getMessage());
		}
		
	}

	/**
	 */
	@Test
	public final void testGetDelimitedXMLContentString() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		String s = "1/2/3/";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", "/1/2/3/", s);
		
		s = "";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", S_EMPTY, s);
		
		s = "/1/2/3/";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", "/1/2/3/", s);
		
		s = "//";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", "//", s);
		
		s = "/";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", "/", s);
		
		s = " ";
		s = delimiterAttribute.getDelimitedXMLContent(s);
		Assert.assertEquals("delimit content ", "/ /", s);
	}

	/**
	 */
	@Test
	public final void testAppendXMLContent() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		String s = "1/2/3/";
		s = delimiterAttribute.appendXMLContent(s, "x");
		Assert.assertEquals("append content ", "/1/2/3/x/", s);
		
		s = "";
		s = delimiterAttribute.appendXMLContent(s, "x");
		Assert.assertEquals("append content ", "x/", s);
		
		s = "/1/2/3//";
		s = delimiterAttribute.appendXMLContent(s, "x");
		Assert.assertEquals("append content ", "/1/2/3//x/", s);
		
	}

	/**
	 */
	@Test
	public final void testGetDelimitedXMLContentStringArray() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		String[] ss = new String[]{"a", "b", "c"};
		String s = delimiterAttribute.getDelimitedXMLContent(ss);
		Assert.assertEquals("append content ", "/a/b/c/", s);
		
		ss = new String[]{"a ", "", "b", "c"};
		s = delimiterAttribute.getDelimitedXMLContent(ss);
		Assert.assertEquals("append content ", "/a //b/c/", s);
		
		delimiterAttribute = new DelimiterAttribute(S_PIPE);
		Assert.assertEquals("concat ", S_PIPE, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_PIPE, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_BACKSLASH+S_PIPE, delimiterAttribute.getSplitter());

		ss = new String[]{"O1", "O2"};
		s = delimiterAttribute.getDelimitedXMLContent(ss);
		Assert.assertEquals("append content ", "|O1|O2|", s);
		
	}

	/**
	 */
	@Test
	public final void testGetDelimitedXMLContentDoubleArray() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		double[] dd = new double[]{1.1, 2.2, 3.3};
		String s = delimiterAttribute.getDelimitedXMLContent(dd);
		Assert.assertEquals("append content ", "/1.1/2.2/3.3/", s);
		
	}

	/**
	 */
	@Test
	public final void testGetDelimitedXMLContentIntArray() {
		DelimiterAttribute delimiterAttribute = new DelimiterAttribute(S_SLASH);
		Assert.assertEquals("concat ", S_SLASH, delimiterAttribute.getConcat());
		Assert.assertEquals("delim ", S_SLASH, delimiterAttribute.getCMLValue());
		Assert.assertEquals("splitter ", S_SLASH, delimiterAttribute.getSplitter());

		int[] dd = new int[]{1, 2, 3};
		String s = delimiterAttribute.getDelimitedXMLContent(dd);
		Assert.assertEquals("append content ", "/1/2/3/", s);
	}

}
