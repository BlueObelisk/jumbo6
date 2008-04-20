package org.xmlcml.cml.base;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.tests.XOMTestCase;

import org.junit.Assert;
import org.junit.Before;
import org.xmlcml.euclid.test.EuclidTestBase;

/**
 * 
 * <p>
 * superclass for manage common methods for unit tests
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class BaseTest extends EuclidTestBase implements CMLConstants {

    /** logger */
    public final static Logger logger = Logger.getLogger(BaseTest.class
            .getName());

    /** root of tests.*/
    public final static String BASE_RESOURCE = "org/xmlcml/cml/base";
    
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
     * tests 2 XML objects for equality using canonical XML.
     * 
     * @param message
     * @param refNode
     *            first node
     * @param testNode
     *            second node
     */
    public static void assertEqualsCanonically(
            String message, Node refNode, Node testNode) {
        try {
            XOMTestCase.assertEquals(message, refNode, testNode);
        } catch (ComparisonFailure e) {
            reportXMLDiff(message, e.getMessage(), refNode, testNode);
        } catch (AssertionFailedError e) {
            reportXMLDiff(message, e.getMessage(), refNode, testNode);
        }
    }
    
    /**
     * tests 2 XML objects for equality using canonical XML.
     * 
     * @param message
     * @param refNode first node
     * @param testNode second node
     * @param stripWhite if true remove w/s nodes
     */
    public static void assertEqualsCanonically(
            String message, Element refNode, Element testNode, boolean stripWhite) {
        if (stripWhite) {
            refNode = new Element(refNode);
            CMLUtil.removeWhitespaceNodes(refNode);
            testNode = new Element(testNode);
            CMLUtil.removeWhitespaceNodes(testNode);
        }
        try {
            XOMTestCase.assertEquals(message, refNode, testNode);
        } catch (ComparisonFailure e) {
            reportXMLDiffInFull(message, e.getMessage(), refNode, testNode);
        } catch (AssertionFailedError e) {
            reportXMLDiffInFull(message, e.getMessage(), refNode, testNode);
        }
    }
    
    static protected void reportXMLDiff(String message, String errorMessage,
            Node refNode, Node testNode) {
        Assert.fail(message+" ~ "+errorMessage);
    }

    static protected void reportXMLDiffInFull(String message, String errorMessage,
            Node refNode, Node testNode) {
        try {
	        System.err.println("==========XMLDIFF=========");
	        CMLUtil.debug((Element) refNode, System.err, 2); 
	        System.err.println("---------------------------------");
	        CMLUtil.debug((Element) testNode, System.err, 2); 
	        System.err.println("=================================");
        } catch (Exception e) {
        	throw new CMLRuntimeException(e);
        }
        Assert.fail(message+" ~ "+errorMessage);
    }


    /**
     * tests 2 XML objects for non-equality using canonical XML.
     * 
     * @param message
     * @param node1
     *            first node
     * @param node2
     *            second node
     */
    public static void assertNotEqualsCanonically(String message, Node node1,
            Node node2) {
        try {
            XOMTestCase.assertEquals(message, node1, node2);
            String s1 = CMLUtil.getCanonicalString(node1);
            String s2 = CMLUtil.getCanonicalString(node2);
            Assert.fail(message + "nodes should be different " + s1 + " != "
                    + s2);
        } catch (ComparisonFailure e) {
        } catch (AssertionFailedError e) {
        }
    }

    /**
     * equalsCanonically.
     * 
     */
    public static void testAssertEqualsCanonically() {
        String s1 = "<a b='c' d='e'><f g='h' i='j'>&amp;x</f><g/></a>";
        String s2 = "<a d='e' b='c'><f i='j' g='h'>&#38;x</f><g></g></a>";
        String s3 = "<a d='e' b='c'><f i='j' g='h'><![CDATA[&]]><![CDATA[x]]></f><g></g></a>";
        String s4 = "<a b='c' d='e'>\n" + "<f g='h' i='j'>&amp;x</f><g/>\n"
                + "</a>";
        Document d1 = null;
        Document d2 = null;
        Document d3 = null;
        Document d4 = null;
        try {
            d1 = new Builder().build(new StringReader(s1));
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        try {
            d2 = new Builder().build(new StringReader(s2));
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        try {
            d3 = new Builder().build(new StringReader(s3));
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        try {
            d4 = new Builder().build(new StringReader(s4));
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        assertEqualsCanonically("should be canonically equal ", d1, d1);
        assertEqualsCanonically("should be canonically equal ", d1, d2);
        assertEqualsCanonically("should be canonically equal ", d1, d3);
        assertNotEqualsCanonically("should not be canonically equal ", d1, d4);

        String s5 = "<a b='c' xmlns:h='"+XHTML_NS+"'><b/></a>";
        Document d5 = null;
        try {
            d5 = new Builder().build(new StringReader(s5));
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        Element e5 = d5.getRootElement();
        Element e5a = new Element(e5);
        String s5S = CMLUtil.getCanonicalString(e5);
        String s5Sa = CMLUtil.getCanonicalString(e5a);
        Assert.assertEquals("copy should have equal canonical", s5S, s5Sa);
    }

    /** test the writeHTML method of element.
     * 
     * @param element to test
     * @param expected HTML string
     */ 
    public void assertWriteHTML(CMLElement element, String expected) {
        StringWriter sw = new StringWriter();
        try {
            element.writeHTML(sw);
            sw.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        String s = sw.toString();
        Assert.assertEquals("HTML output ", expected, s);
    }

}
