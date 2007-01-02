package org.xmlcml.cml.element.test;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLBuilder;

/**
 * test CMLNodeFactory.
 *
 * @author pmr
 *
 */
public class CMLNodeFactoryTest extends AbstractTest {

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
     * Test method for
     * 'org.xmlcml.cml.element.CMLNodeFactory.startMakingElement(String,
     * String)'
     * * DOES NOT TEST THIS ROUTINE!!!!!!!
     */
    @Test
    public void testStartMakingElementStringString() {
        String s1 = "<cml " + CML_XMLNS + "/>";
        CMLElement cmlElement = (CMLElement) parseValidString(s1);
        String namespace = cmlElement.getNamespaceURI();
        Assert.assertEquals("ok namespace", CML_NS, namespace);

        // guess namespace
        s1 = "<cml xmlns='" + CML1 + "'/>";
        try {
            cmlElement = (CMLElement) new CMLBuilder().parseString(s1);
        } catch (Exception e) {
            neverThrow(e);
        }
        Assert.assertTrue("is CMLElement", CMLElement.class
                .isAssignableFrom(cmlElement.getClass()));
        namespace = cmlElement.getNamespaceURI();
        Assert.assertEquals("old namespace -> new", CML_NS, namespace);

        Element element = null;
        // cannot guess namespace
        s1 = "<cml xmlns='http://foo'/>";
        try {
            element = (Element) new CMLBuilder().parseString(s1);
        } catch (Exception e) {
            neverThrow(e);
        }
        Assert.assertFalse("is CMLElement", CMLElement.class
                .isAssignableFrom(element.getClass()));
        namespace = element.getNamespaceURI();
        Assert.assertEquals("other namespace", "http://foo", namespace);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLNodeFactory.makeAttribute(String, String,
     * String, Type)'
     */
    @Ignore
    @Test
    public void testMakeAttributeStringStringStringType() {
//        String s1 = "<cml " + CML_XMLNS + "/>";
//        CMLElement cmlElement = (CMLElement) parseValidString(s1);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLNodeFactory.makeText(String)'
     */
    @Ignore
    @Test
    public void testMakeTextString() {
        // TODO Auto-generated method stub
    }

 }