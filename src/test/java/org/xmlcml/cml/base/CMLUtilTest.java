package org.xmlcml.cml.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.alwaysFail;
import static org.xmlcml.util.TestUtils.neverThrow;

import java.text.ParseException;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Text;
import nu.xom.XPathContext;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.util.TestUtils;

/**
 * test CMLUtil.
 * 
 * @author pm286
 * 
 */
public class CMLUtilTest  {

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.checkPrefixedName(String)'
     */
    @Test
    public final void testCheckPrefixedName() {
        try {
            CMLUtil.checkPrefixedName("foo:name");
        } catch (CMLException e) {
            neverThrow(e);
        }
        try {
            CMLUtil.checkPrefixedName("name");
            alwaysFail("unprefixed name");
        } catch (CMLException e) {
            Assert.assertEquals("unprefixed", "Unprefixed name (name)", e
                    .getMessage());
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.debug(Element)'
     */
    @Test
    public final void testDebugElement() {
        // no simple test
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.debug(Element,
     * OutputStream)'
     */
    @Test
    public final void testDebugElementOutputStream() {
        // no simple test
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.getXMLResource(String)'
     */
    @Test
    public final void testGetXMLResource() {
        String filename = TestUtils.BASE_RESOURCE + U_S + "cml0.xml";
        Document doc = null;
        try {
            doc = CMLUtil.getXMLResource(filename);
        } catch (Exception e) {
            neverThrow(e);
        }
        Assert.assertNotNull("doc not null", doc);
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#debugToErr(nu.xom.Element)}.
     */
    @Test
    public final void testDebugToErr() {
        // no testable action
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getChildNodes(nu.xom.Element)}.
     */
    @Test
    public final void testGetChildNodes() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        List<Node> childNodes = CMLUtil.getChildNodes(root);
        Assert.assertEquals("count", 5, childNodes.size());
        Assert.assertEquals("class", Text.class, childNodes.get(0).getClass());
        Assert.assertEquals("value", "abc", childNodes.get(0).getValue());
        Assert.assertEquals("class", Element.class, childNodes.get(1)
                .getClass());
        Assert.assertEquals("class", "bar", ((Element) childNodes.get(1))
                .getLocalName());
        Assert.assertEquals("value", "", childNodes.get(1).getValue());
        Assert.assertEquals("class", Text.class, childNodes.get(2).getClass());
        Assert.assertEquals("value", "def", childNodes.get(2).getValue());
        Assert.assertEquals("class", Element.class, childNodes.get(3)
                .getClass());
        Assert.assertEquals("class", "bar1", ((Element) childNodes.get(3))
                .getLocalName());
        Assert.assertEquals("value", "ghi", childNodes.get(3).getValue());
        Assert.assertEquals("class", Text.class, childNodes.get(4).getClass());
        Assert.assertEquals("value", "jkl", childNodes.get(4).getValue());
    }

    /**
     * Test method for {@link org.xmlcml.cml.base.CMLUtil#parseXML(String)}.
     */
    @Test
    public final void testparseXMLString() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        Element root1 = new Element("foo");
        root1.appendChild(new Text("abc"));
        Element bar = new Element("bar");
        root1.appendChild(bar);
        root1.appendChild(new Text("def"));
        Element bar1 = new Element("bar1");
        bar1.appendChild(new Text("ghi"));
        root1.appendChild(bar1);
        root1.appendChild(new Text("jkl"));
        TestUtils.assertEqualsCanonically("parseXML", root1, root);

    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getQueryNodes(nu.xom.Node, java.lang.String, nu.xom.XPathContext)}.
     */
    @Test
    public final void testGetQueryNodesNodeStringXPathContext() {
        XPathContext XPC = new XPathContext("boo", "http://boo");
        String s = "<foo>abc<boo:bar xmlns:boo='http://boo'>xyz</boo:bar>def<bar>ghi</bar>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        List<Node> nodeList = CMLUtil.getQueryNodes(root, "/foo");
        Assert.assertEquals("nodes", 1, nodeList.size());
        nodeList = CMLUtil.getQueryNodes(root, "//bar");
        Assert.assertEquals("nodes", 1, nodeList.size());
        Assert.assertEquals("nodes", "ghi", nodeList.get(0).getValue());
        nodeList = CMLUtil.getQueryNodes(root, "//boo:bar", XPC);
        Assert.assertEquals("nodes", 1, nodeList.size());
        Assert.assertEquals("nodes", "xyz", nodeList.get(0).getValue());
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getQueryNodes(nu.xom.Node, java.lang.String)}.
     */
    @Test
    public final void testGetQueryNodesNodeString() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        List<Node> nodeList = CMLUtil.getQueryNodes(root, ".//text()");
        Assert.assertEquals("nodes", 4, nodeList.size());
        nodeList = CMLUtil.getQueryNodes(root, "/*/text()");
        Assert.assertEquals("nodes", 3, nodeList.size());
        nodeList = CMLUtil.getQueryNodes(root, "/text()");
        Assert.assertEquals("nodes", 0, nodeList.size());
        nodeList = CMLUtil.getQueryNodes(root, "/foo");
        Assert.assertEquals("nodes", 1, nodeList.size());
        nodeList = CMLUtil.getQueryNodes(root, "/foo/*");
        Assert.assertEquals("nodes", 2, nodeList.size());
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getFollowingSibling(nu.xom.Node)}.
     */
    @Test
    public final void testGetFollowingSibling() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        List<Node> nodeList = CMLUtil.getChildNodes(root);
        Node text = nodeList.get(0);
        Node sibNode = CMLUtil.getFollowingSibling(text);
        Assert.assertEquals("fsib", "bar", ((Element) sibNode).getLocalName());
        Node bar1 = nodeList.get(3);
        Assert.assertEquals("fsib", "bar1", ((Element) bar1).getLocalName());
        sibNode = CMLUtil.getFollowingSibling(bar1);
        Assert.assertEquals("fsib", "jkl", sibNode.getValue());
        text = nodeList.get(4);
        sibNode = CMLUtil.getFollowingSibling(text);
        Assert.assertNull("fsib", sibNode);
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getPrecedingSibling(nu.xom.Node)}.
     */
    @Test
    public final void testGetPrecedingSibling() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        List<Node> nodeList = CMLUtil.getChildNodes(root);
        Node text = nodeList.get(0);
        Node sibNode = CMLUtil.getPrecedingSibling(text);
        Assert.assertNull("fsib", sibNode);
        Node bar1 = nodeList.get(3);
        Assert.assertEquals("fsib", "bar1", ((Element) bar1).getLocalName());
        sibNode = CMLUtil.getPrecedingSibling(bar1);
        Assert.assertEquals("fsib", "def", sibNode.getValue());
        text = nodeList.get(4);
        sibNode = CMLUtil.getFollowingSibling(text);
        Assert.assertEquals("fsib", "bar1", ((Element) bar1).getLocalName());
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getLastTextDescendant(nu.xom.Node)}.
     */
    @Test
    public final void testGetLastTextDescendant() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        Text text = CMLUtil.getLastTextDescendant(root);
        Assert.assertNotNull("text", text);
        Assert.assertEquals("text", "jkl", text.getValue());
        s = "<foo><bar1><plugh/></bar1></foo>";
        root = CMLUtil.parseXML(s);
        text = CMLUtil.getLastTextDescendant(root);
        Assert.assertNull("text", text);
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getFirstTextDescendant(nu.xom.Node)}.
     */
    @Test
    public final void testGetFirstTextDescendant() {
        String s = "<foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        Text text = CMLUtil.getFirstTextDescendant(root);
        Assert.assertNotNull("text", text);
        Assert.assertEquals("text", "abc", text.getValue());
        s = "<foo><bar1><plugh/></bar1></foo>";
        root = CMLUtil.parseXML(s);
        text = CMLUtil.getFirstTextDescendant(root);
        Assert.assertNull("text", text);
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#transferChildren(nu.xom.Element, nu.xom.Element)}.
     */
    @Test
    public final void testTransferChildren() {
        String s = ""
                + "<root><foo>abc<bar/>def<bar1>ghi</bar1>jkl</foo>and<plugh><qqq/>zzz</plugh></root>";
        Element root = CMLUtil.parseXML(s);
        Element foo = (Element) root.getChild(0);
        Element plugh = (Element) root.getChild(2);
        CMLUtil.transferChildren(plugh, foo);
        Element newRoot = CMLUtil.parseXML("<root>"
                + "<foo>abc<bar/>def<bar1>ghi</bar1>jkl<qqq/>zzz</foo>"
                + "and<plugh/></root>");
        TestUtils.assertEqualsCanonically("new root", root, newRoot);
    }

    /**
     * Test method for
     */
    @Test
    public final void testOutput() {
        // no simple test
    }
    
    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.toArray(Elements, Object[])'
     */
    @Test
    public final void testToArray() {
        String s = "<foo>abc<bar/>def<bar>ghi</bar>jkl</foo>";
        Element root = CMLUtil.parseXML(s);
        Elements elements = root.getChildElements();
        CMLUtil.toArray(elements, new Element[] {});
    }
    
	/** test get prefixes.
	 */
	@Test
    public void testGetPrefixes() {
		Element fragment = new Element("fragment", CML_NS);
		Element fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("ref", "g:mol"));
		fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("ref", "k:mol"));
		fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("dictRef", "k:x"));
		fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("ref", "k:xxx"));
		fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("dictRef", "q:xxx"));
		fragment1 = new Element("fragment");
		fragment.appendChild(fragment1);
		fragment1.addAttribute(new Attribute("ref", "xxx"));
		
    	List<String> prefixList = CMLUtil.getPrefixes(fragment, "ref");
    	Assert.assertEquals("set", 3, prefixList.size());
    	Assert.assertTrue("set", prefixList.contains(S_EMPTY));
    	Assert.assertTrue("set", prefixList.contains("g"));
    	Assert.assertTrue("set", prefixList.contains("k"));
    	Assert.assertFalse("set", prefixList.contains("q"));
    	
    	prefixList = CMLUtil.getPrefixes(fragment, "dictRef");
    	Assert.assertEquals("set", 2, prefixList.size());
    	Assert.assertFalse("set", prefixList.contains(S_EMPTY));
    	Assert.assertFalse("set", prefixList.contains("g"));
    	Assert.assertTrue("set", prefixList.contains("k"));
    	Assert.assertTrue("set", prefixList.contains("q"));
    }

	@Test
	public void checkDoubleParsing() throws ParseException {
		assertEquals(1.0, CMLUtil.parseFlexibleDouble("1.0"));
		assertEquals(Double.NaN, CMLUtil.parseFlexibleDouble("NaN"));
		assertEquals(Double.POSITIVE_INFINITY, CMLUtil
				.parseFlexibleDouble("INF"));
		assertEquals(Double.NEGATIVE_INFINITY, CMLUtil
				.parseFlexibleDouble("-INF"));
		assertEquals(-0.001, CMLUtil.parseFlexibleDouble("-0.001"));
		assertEquals(-0.1, CMLUtil.parseFlexibleDouble("-000.1"));
		assertEquals(1000.0, CMLUtil.parseFlexibleDouble("1.0E3"));
		assertEquals(1000.0, CMLUtil.parseFlexibleDouble("1.0e3"));
		assertEquals(10000.0, CMLUtil.parseFlexibleDouble("10.0E3"));
		assertEquals(1000.0, CMLUtil.parseFlexibleDouble("1.0E+3"));
		assertEquals(0.001, CMLUtil.parseFlexibleDouble("1.0E-3"));
		assertEquals(1000.0, CMLUtil.parseFlexibleDouble("1.0E+03"));
		try {
			CMLUtil.parseFlexibleDouble("1.0e3foobar");
			fail("Parsing 1.0e3foobar should have resulted in a ParseException being raised");
		} catch (ParseException e) {
			e.printStackTrace();
			assertTrue(true);
		}
	}
	
	
	/**
	 * 
	 */
	@Test
	public void testNormalizeTexts() {
		Element test = new Element("foo");
		test.appendChild(new Text("foo"));
		test.appendChild(new Text("bar"));
		Assert.assertEquals("text child", 2, test.getChildCount());
		Assert.assertEquals("text child 1", "foo", test.getChild(0).getValue());
		Assert.assertEquals("text child 2", "bar", test.getChild(1).getValue());
		CMLUtil.normalizeTexts(test);
		Assert.assertEquals("text child", 1, test.getChildCount());
		Assert.assertEquals("text child 1", "foobar", test.getChild(0).getValue());
		
		test = new Element("foo");
		test.appendChild(new Text("foo"));
		test.appendChild(new Element("plugh"));
		test.appendChild(new Text("bar"));
		Assert.assertEquals("text child", 3, test.getChildCount());
		Assert.assertEquals("text child 1", "foo", test.getChild(0).getValue());
		Assert.assertEquals("text child 2", "plugh", ((Element)test.getChild(1)).getLocalName());
		Assert.assertEquals("text child 3", "bar", test.getChild(2).getValue());
		CMLUtil.normalizeTexts(test);
		// no change
		Assert.assertEquals("text child", 3, test.getChildCount());
		Assert.assertEquals("text child 1", "foo", test.getChild(0).getValue());
		Assert.assertEquals("text child 2", "plugh", ((Element)test.getChild(1)).getLocalName());
		Assert.assertEquals("text child 3", "bar", test.getChild(2).getValue());
		// delete element so texts touch
		test.getChild(1).detach();
		Assert.assertEquals("text child", 2, test.getChildCount());
		Assert.assertEquals("text child 1", "foo", test.getChild(0).getValue());
		Assert.assertEquals("text child 2", "bar", test.getChild(1).getValue());
		CMLUtil.normalizeTexts(test);
		Assert.assertEquals("text child", 1, test.getChildCount());
		Assert.assertEquals("text child 1", "foobar", test.getChild(0).getValue());
		
		
		String s = "<B id='/1/1/2/52/6'>(1 ONE <two/> THREE <four/><foura/> FIVE foo  </B>";
		try {
			Element el = new CMLBuilder().parseString(s);
//			CMLUtil.debug(el, "start");
			CMLUtil.normalizeTexts(el);
//			CMLUtil.debug(el, "end");
		} catch (Exception e) {
			throw new CMLRuntimeException(e);
		}
	}

	/**
	 * 
	 */
	@Test
	public void testRemoveElementAndPullUpChildren() {
		String s = ""+
		"<ne id='o288' surface='xxx'>" +
		"<B id='/1/1/2/52/6'>(1" +
		"<IT id='/1/1/2/52/6/1'>ONE <two/> THREE <four/><foura/> FIVE </IT>)-5-( " +
		"<IT id='/1/1/2/52/6/2'>tert</IT>-Butyldimethylsilyloxy)-1-phenyl-pent-2-yn-1-ol 58" +
		"</B>" +
		" </ne>";
		try {
			Element element = new CMLBuilder().parseString(s);
			CMLUtil.removeElementAndPullUpChildren(element, "IT");
			String ss = 
				"<ne id='o288' surface='xxx'>"+
			  "<B id='/1/1/2/52/6'>(1ONE "+
			    "<two/> THREE "+
			    "<four/>"+
			    "<foura/> FIVE )-5-( tert-Butyldimethylsilyloxy)-1-phenyl-pent-2-yn-1-ol 58"+
			  "</B>"+
			"</ne>";
			Element element1 = new CMLBuilder().parseString(ss);
			TestUtils.assertEqualsCanonically("after pull up", element1, element, true);
			CMLUtil.normalizeTexts(element);
			TestUtils.assertEqualsCanonically("after pull up", element1, element, true);
		} catch (Exception e) {
			throw new CMLRuntimeException(e);
		}
	}
	
	
}
