package org.xmlcml.cml.element.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.NamespaceToUnitListMap;
import org.xmlcml.cml.element.UnitAttribute;
import org.xmlcml.euclid.test.DoubleTest;
import org.xmlcml.euclid.test.IntTest;
import org.xmlcml.euclid.test.StringTest;

/**
 * test Array
 *
 * @author pmr
 *
 */
public class CMLArrayTest extends NumericTest {

    final static double EPS = 0.0000000001;

    int[] i0 = { 1, 2, 3, 4, 5 };

    int[] i1 = { 10, 20, 30, 40, 50 };

    double[] d0 = { 1., 2., 3., 4., 5. };

    double[] d1 = { 10., 20., 30., 40., 50. };

    String[] s0 = { "a", "b", "c", "d", "e" };

    String[] s1 = { "v", "w", "x", "y", "z" };

    String delim = S_SLASH;

    CMLArray xomS;

    CMLArray xomI0;

    CMLArray xomI1;

    CMLArray xomD0;

    CMLArray xomD1;

    CMLArray xomS0;

    CMLArray xomS1;

    String xmlSS = "<array " + CML_XMLNS + "/>";

    // note these are String arrays
    String xmlS0S = "<array " + CML_XMLNS + ">1 2 3 4 5</array>";

    String xmlS1S = "<array delimiter='/' " + CML_XMLNS
            + ">/10/20/30/40/50/</array>";

    // while these are ints
    String xmlI0S = "<array dataType='xsd:integer' " + CML_XMLNS
            + ">1 2 3 4 5</array>";

    String xmlI1S = "<array dataType='xsd:integer' delimiter='/' " + CML_XMLNS
            + ">/10/20/30/40/50/</array>";

    String xmlD0S = "<array dataType='xsd:double' " + CML_XMLNS
            + ">1. 2. 3. 4. 5.</array>";

    String xmlD1S = "<array dataType='xsd:double' delimiter='/' " + CML_XMLNS
            + ">/10./20./30./40./50./</array>";

    String xmlBad1 = "<array dataType='xsd:double' delimiter='/' " + CML_XMLNS
            + ">/a/b/</array>";

    String xmlBad2 = "<array size='2' delimiter='/' " + CML_XMLNS
            + ">a b c</array>";

    String xmlBad3 = "<array size='3' dataType='xsd:double'  " + CML_XMLNS
            + ">1 2 c</array>";

    String xmlBad4 = "<array size='3' dataType='xsd:integer'  " + CML_XMLNS
            + ">1 2 c</array>";

    String xmlBad5 = "<array delimiter=' ' " + CML_XMLNS + ">a b c</array>";

    String xmlBad6 = "<array size='2' " + CML_XMLNS + ">a b c</array>";

    CMLArray xmlS;

    CMLArray xmlI0;

    CMLArray xmlI1;

    CMLArray xmlD0;

    CMLArray xmlD1;

    CMLArray xmlS0;

    CMLArray xmlS1;

    String unitsS = "<c:cml " + "id='a234234' " +
            "xmlns:c='" + CML_NS + "' "
            + "xmlns:siUnits='"+SIUNIT_NS+"' "
            + "xmlns:units='"+UNIT_NS+"' " + ">"
            + "<c:array id='s1' dictRef='cmlDict:angle' units='" + U_DEGREE
            + "' " + "  dataType='xsd:double'>180 90 45 0</c:array>"
            + "<c:array id='s2' dictRef='foo:bar' units='" + U_KCAL + "' "
            + "  dataType='xsd:double'>100 50 0 -25</c:array>"
            + "<c:array id='s3' dictRef='foo:mpt' units='" + U_CELSIUS + "' "
            + "  dataType='xsd:double'>100 50 0 -50</c:array>" + "</c:cml>";

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        xomS = new CMLArray();

        xomD0 = new CMLArray(d0);
        xomI0 = new CMLArray(i0);
        xomS0 = new CMLArray(s0);

        xomD1 = new CMLArray(d1, delim);
        xomI1 = new CMLArray(i1, delim);
        xomS1 = new CMLArray(s1, delim);

        xmlS = (CMLArray) parseValidString(xmlSS);
        xmlS0 = (CMLArray) parseValidString(xmlS0S);
        xmlS1 = (CMLArray) parseValidString(xmlS1S);
        xmlI0 = (CMLArray) parseValidString(xmlI0S);
        xmlI1 = (CMLArray) parseValidString(xmlI1S);
        xmlD0 = (CMLArray) parseValidString(xmlD0S);
        xmlD1 = (CMLArray) parseValidString(xmlD1S);

    }

    /**
     * equality test. true if both args not null and equal within epsilon
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, CMLArray test,
            CMLArray expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        if ((test.getDataType() == null || test.getDataType()
                .equals(XSD_STRING))
                && (expected.getDataType() == null || expected.getDataType()
                        .equals(XSD_STRING))) {
            StringTest.assertEquals(msg, test.getStrings(), expected
                    .getStrings());
        } else if (test.getDataType().equals(XSD_DOUBLE)
                && expected.getDataType().equals(XSD_DOUBLE)) {
            DoubleTest.assertEquals(msg, test.getDoubles(), expected
                    .getDoubles(), EPS);
        } else if (test.getDataType().equals(XSD_INTEGER)
                && expected.getDataType().equals(XSD_INTEGER)) {
            IntTest.assertEquals(msg, test.getInts(), expected.getInts());
        } else {
            Assert.fail("inconsistent dataTypes" + test.getDataType() + " / "
                    + expected.getDataType());
        }
    }

    /**
     * equality test. true if both args not null and equal within epsilon
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, double[] test,
            CMLArray expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        if (!expected.getDataType().equals(XSD_DOUBLE)) {
            Assert.fail("expected should be double");
        }
        DoubleTest.assertEquals(msg, test, expected.getDoubles(), EPS);
    }

    /**
     * equality test. true if both args not null and equal
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     */
    public static void assertEquals(String msg, int[] test, CMLArray expected) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        if (!expected.getDataType().equals(XSD_INTEGER)) {
            Assert.fail("expected should be int");
        }
        IntTest.assertEquals(msg, test, expected.getInts());
    }

    /**
     * equality test. true if both args not null and equal
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     */
    public static void assertEquals(String msg, String[] test, CMLArray expected) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        if (expected.getDataType() != null
                && !expected.getDataType().equals(XSD_STRING)) {
            Assert.fail("expected should be String");
        }
        StringTest.assertEquals(msg, test, expected.getStrings());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.finishMakingElement()'
     * /** Test method for
     * 'org.xmlcml.cml.element.CMLArray.finishMakingElement()'
     */
    @Test
    public void testFinishMakingElement() {
        CMLArray a = null;
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad1);
            Assert.fail("bad 1 should not parse");
        } catch (Exception e) {
            Assert.assertEquals("bad input", "cannot parse as doubles: /a/b/",
                    e.getMessage());
        }
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad2);
            Assert.fail("bad 2 should not parse - inconsistent size");
        } catch (Exception e) {
            Assert.assertEquals("bad input",
                    "Size attribute: 2 incompatible with content: 1", e
                            .getMessage());
        }
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad3);
            Assert.fail("bad 3 should not parse");
        } catch (Exception e) {
            Assert.assertEquals("bad input", "cannot parse as doubles: 1 2 c",
                    e.getMessage());
        }
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad4);
            Assert.fail("bad 4 should not parse");
        } catch (Exception e) {
            Assert.assertEquals("bad input", "cannot parse as ints: 1 2 c", e
                    .getMessage());
        }
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad5);
            Assert.assertNull("whitespace delim", a.getDelimiterAttribute());
            Assert.assertEquals("token length", 3, a.getStrings().length);
            Assert.assertEquals("token 0", "a", a.getStrings()[0]);
        } catch (Exception e) {
            Assert.assertEquals("inconsistent length",
                    "Size attribute: 2 incompatible with content: 3", e
                            .getMessage());
        }
        try {
            a = (CMLArray) new CMLBuilder().parseString(xmlBad6);
            Assert.fail("bad 6 should not parse - inconsistent size");
        } catch (Exception e) {
            Assert.assertEquals("inconsistent length",
                    "Size attribute: 2 incompatible with content: 3", e
                            .getMessage());
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getDataType()'
     */
    @Test
    public void testGetDataType() {
        Assert.assertEquals("dataType", XSD_STRING, xomS.getDataType());
        Assert.assertEquals("dataType", XSD_STRING, xomS0.getDataType());
        Assert.assertEquals("dataType", XSD_INTEGER, xomI0.getDataType());
        Assert.assertEquals("dataType", XSD_DOUBLE, xomD0.getDataType());

        Assert.assertEquals("dataType", XSD_STRING, xmlS.getDataType());
        Assert.assertEquals("dataType", XSD_STRING, xmlS0.getDataType());
        Assert.assertEquals("dataType", XSD_STRING, xmlS1.getDataType());
        Assert.assertEquals("dataType", XSD_INTEGER, xmlI0.getDataType());
        Assert.assertEquals("dataType", XSD_INTEGER, xmlI1.getDataType());
        Assert.assertEquals("dataType", XSD_DOUBLE, xmlD0.getDataType());
        Assert.assertEquals("dataType", XSD_DOUBLE, xmlD1.getDataType());

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setDataType(String)'
     */
    @Test
    public void testSetDataType() {
        try {
            xomS.setDataType(XSD_DOUBLE);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw CMLRuntime");
        }
        Assert.assertEquals("set data type", XSD_DOUBLE, xomS.getDataType());
        try {
            xomS.setDataType(XSD_DOUBLE);
            Assert.fail("should throw CMLRuntime");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("ok", "ok");
        }
        try {
            xomS0.setDataType(XSD_DOUBLE);
            Assert.fail("should throw CMLRuntime");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("ok", "ok");
        }
        try {
            xomD0.setDataType(XSD_DOUBLE);
            Assert.fail("should throw CMLRuntime");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("ok", "ok");
        }
        try {
            xomI0.setDataType(XSD_DOUBLE);
            Assert.fail("should throw CMLRuntime");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("ok", "ok");
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getDelimiter()'
     */
    @Test
    public void testGetDelimiter() {
        Assert.assertNull("default", xomS.getDelimiterAttribute());

        Assert.assertNull("string", xomS0.getDelimiterAttribute());
        Assert.assertNull("double", xomD0.getDelimiterAttribute());
        Assert.assertNull("int", xomI0.getDelimiterAttribute());

        Assert.assertEquals("string", S_SLASH, xomS1.getDelimiter());
        Assert.assertEquals("double", S_SLASH, xomD1.getDelimiter());
        Assert.assertEquals("int", S_SLASH, xomI1.getDelimiter());

        Assert.assertNull("string", xmlS0.getDelimiterAttribute());
        Assert.assertNull("double", xmlD0.getDelimiterAttribute());
        Assert.assertNull("int", xmlI0.getDelimiterAttribute());

        Assert.assertEquals("string", S_SLASH, xmlS1.getDelimiter());
        Assert.assertEquals("double", S_SLASH, xmlD1.getDelimiter());
        Assert.assertEquals("int", S_SLASH, xmlI1.getDelimiter());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getSize()'
     */
    @Test
    public void testGetSize() {
        Assert.assertEquals("size", 0, xomS.getSize());

        Assert.assertEquals("size", 5, xomS0.getSize());
        Assert.assertEquals("double", 5, xomD0.getSize());
        Assert.assertEquals("int", 5, xomI0.getSize());

        Assert.assertEquals("size", 5, xomS1.getSize());
        Assert.assertEquals("double", 5, xomD1.getSize());
        Assert.assertEquals("int", 5, xomI1.getSize());

        Assert.assertEquals("size", 0, xmlS.getSize());

        Assert.assertEquals("size", 5, xmlS0.getSize());
        Assert.assertEquals("double", 5, xmlD0.getSize());
        Assert.assertEquals("int", 5, xmlI0.getSize());

        Assert.assertEquals("size", 5, xmlS1.getSize());
        Assert.assertEquals("double", 5, xmlD1.getSize());
        Assert.assertEquals("int", 5, xmlI1.getSize());

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray()'
     */
    @Test
    public void testCMLArray() {
        Assert.assertEquals("size", 0, xomS.getSize());
        Assert.assertNull("delimiter", xomS.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SPACE, xomS.getDelimiter());
        Assert.assertEquals("dataType", XSD_STRING, xomS.getDataType());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(CMLArray)'
     */
    @Test
    public void testCMLArrayCMLArray() {

        CMLArray s2 = new CMLArray(xomS0);
        Assert.assertNull("delimiter", s2.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SPACE, s2.getDelimiter());
        Assert.assertEquals("size", 5, s2.getSize());
        Assert.assertEquals("dataType", XSD_STRING, s2.getDataType());
        String[] ss = s2.getStrings();
        Assert.assertEquals("element", "a", ss[0]);
        Assert.assertEquals("element", "e", ss[4]);

        s2 = new CMLArray(xomD0);
        Assert.assertNull("delimiter", s2.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SPACE, s2.getDelimiter());
        Assert.assertEquals("size", 5, s2.getSize());
        Assert.assertEquals("dataType", XSD_DOUBLE, s2.getDataType());
        double[] dd = null;
        try {
            dd = s2.getDoubles();
        } catch (CMLRuntimeException e) {
            Assert.fail("should parse doubles OK");
        }
        Assert.assertEquals("element", 1., dd[0], EPS);
        Assert.assertEquals("element", 5., dd[4], EPS);

        s2 = new CMLArray(xomI0);
        Assert.assertNull("delimiter", s2.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SPACE, s2.getDelimiter());
        Assert.assertEquals("size", 5, s2.getSize());
        Assert.assertEquals("dataType", XSD_INTEGER, s2.getDataType());
        int[] ii = null;
        try {
            ii = s2.getInts();
        } catch (CMLRuntimeException e) {
            Assert.fail("should parse doubles OK");
        }
        Assert.assertEquals("element", 1, ii[0]);
        Assert.assertEquals("element", 5, ii[4]);

        s2 = new CMLArray(xmlI0);
        Assert.assertNull("delimiter", s2.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SPACE, s2.getDelimiter());
        Assert.assertEquals("size", 5, s2.getSize());
        Assert.assertEquals("dataType", XSD_INTEGER, s2.getDataType());
        ii = null;
        try {
            ii = s2.getInts();
        } catch (CMLRuntimeException e) {
            Assert.fail("should parse doubles OK");
        }
        Assert.assertEquals("element", 1, ii[0]);
        Assert.assertEquals("element", 5, ii[4]);

        s2 = new CMLArray(xmlD1);
        Assert.assertNotNull("delimiter", s2.getDelimiterAttribute());
        Assert.assertEquals("delimiter", S_SLASH, s2.getDelimiter());
        Assert.assertEquals("size", 5, s2.getSize());
        Assert.assertEquals("dataType", XSD_DOUBLE, s2.getDataType());
        dd = null;
        try {
            dd = s2.getDoubles();
        } catch (CMLRuntimeException e) {
            Assert.fail("should parse doubles OK");
        }
        Assert.assertEquals("element", 10., dd[0], EPS);
        Assert.assertEquals("element", 50., dd[4], EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(String[])'
     */
    @Test
    public void testCMLArrayStringArray() {
        Assert.assertEquals("data type", XSD_STRING, xomS0.getDataType());
        Assert.assertEquals("size", 5, xomS0.getSize());
        Assert.assertEquals("content", "a b c d e", xomS0.getXMLContent());
        Assert.assertEquals("delimiter", S_SPACE, xomS0.getDelimiter());
        Assert.assertNull("delimiter", xomS0.getDelimiterAttribute());
        String[] ss = xomS0.getStrings();
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", "a", ss[0]);
        Assert.assertEquals("array4", "e", ss[4]);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(String[],
     * String)'
     */
    @Test
    public void testCMLArrayStringArrayString() {
        Assert.assertEquals("data type", XSD_STRING, xomS1.getDataType());
        Assert.assertEquals("size", 5, xomS1.getSize());
        Assert.assertEquals("content", "/v/w/x/y/z/", xomS1.getXMLContent());
        Assert.assertEquals("delimiter", S_SLASH, xomS1.getDelimiter());
        Assert.assertNotNull("delimiter", xomS1.getDelimiterAttribute());
        String[] ss = xomS1.getStrings();
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", "v", ss[0]);
        Assert.assertEquals("array4", "z", ss[4]);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(double[])'
     */
    @Test
    public void testCMLArrayDoubleArray() {
        Assert.assertEquals("data type", XSD_DOUBLE, xomD0.getDataType());
        Assert.assertEquals("size", 5, xomD0.getSize());
        Assert.assertEquals("content", "1.0 2.0 3.0 4.0 5.0", xomD0
                .getXMLContent());
        Assert.assertEquals("delimiter", S_SPACE, xomD0.getDelimiter());
        Assert.assertNull("delimiter", xomD0.getDelimiterAttribute());
        double[] ss = null;
        try {
            ss = xomD0.getDoubles();
        } catch (CMLRuntimeException e) {
            Assert.fail("doubles should parse OK");
        }
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", 1.0, ss[0], EPS);
        Assert.assertEquals("array4", 5.0, ss[4], EPS);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(double[],
     * Strinig)'
     */
    @Test
    public void testCMLArrayDoubleArrayString() {
        Assert.assertEquals("data type", XSD_DOUBLE, xomD1.getDataType());
        Assert.assertEquals("size", 5, xomD1.getSize());
        Assert.assertEquals("content", "/10.0/20.0/30.0/40.0/50.0/", xomD1
                .getXMLContent());
        Assert.assertEquals("delimiter", S_SLASH, xomD1.getDelimiter());
        Assert.assertNotNull("delimiter", xomD1.getDelimiterAttribute());
        double[] ss = null;
        try {
            ss = xomD1.getDoubles();
        } catch (CMLRuntimeException e) {
            Assert.fail("doubles should parse OK");
        }
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", 10.0, ss[0], EPS);
        Assert.assertEquals("array4", 50.0, ss[4], EPS);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(int[])'
     */
    @Test
    public void testCMLArrayIntArray() {
        Assert.assertEquals("data type", XSD_INTEGER, xomI0.getDataType());
        Assert.assertEquals("size", 5, xomI0.getSize());
        Assert.assertEquals("content", "1 2 3 4 5", xomI0.getXMLContent());
        Assert.assertEquals("delimiter", S_SPACE, xomI0.getDelimiter());
        Assert.assertNull("delimiter", xomI0.getDelimiterAttribute());
        int[] ss = null;
        try {
            ss = xomI0.getInts();
        } catch (CMLRuntimeException e) {
            Assert.fail("ints should parse OK");
        }
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", 1, ss[0]);
        Assert.assertEquals("array4", 5, ss[4]);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.CMLArray(int[], String)'
     */
    @Test
    public void testCMLArrayIntArrayString() {
        Assert.assertEquals("data type", XSD_INTEGER, xomI1.getDataType());
        Assert.assertEquals("size", 5, xomI1.getSize());
        Assert.assertEquals("content", "/10/20/30/40/50/", xomI1
                .getXMLContent());
        Assert.assertEquals("delimiter", S_SLASH, xomI1.getDelimiter());
        Assert.assertNotNull("delimiter", xomI1.getDelimiterAttribute());
        int[] ss = null;
        try {
            ss = xomI0.getInts();
        } catch (CMLRuntimeException e) {
            Assert.fail("ints should parse OK");
        }
        Assert.assertEquals("length", 5, ss.length);
        Assert.assertEquals("array0", 1, ss[0]);
        Assert.assertEquals("array4", 5, ss[4]);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getStrings()'
     */
    @Test
    public void testGetStrings() {
        String[] ss = xomS.getStrings();
        Assert.assertNotNull("empty content", ss);
        Assert.assertEquals("empty content", 0, ss.length);

        ss = xomS0.getStrings();
        Assert.assertNotNull("content", ss);
        Assert.assertEquals("content", 5, ss.length);
        Assert.assertEquals("array0", "a", ss[0]);
        Assert.assertEquals("array4", "e", ss[4]);

        ss = xomS1.getStrings();
        Assert.assertNotNull("content", ss);
        Assert.assertEquals("content", 5, ss.length);
        Assert.assertEquals("array0", "v", ss[0]);
        Assert.assertEquals("array4", "z", ss[4]);

        ss = xomD0.getStrings();
        Assert.assertNull("content", ss);

        ss = xomD1.getStrings();
        Assert.assertNull("content", ss);

        ss = xomI0.getStrings();
        Assert.assertNull("content", ss);

        ss = xomI1.getStrings();
        Assert.assertNull("content", ss);

        ss = xmlS.getStrings();
        Assert.assertNotNull("empty content", ss);
        Assert.assertEquals("empty content", 0, ss.length);

        ss = xmlS0.getStrings();
        Assert.assertNotNull("content", ss);
        Assert.assertEquals("content", 5, ss.length);
        Assert.assertEquals("array0", "1", ss[0]);
        Assert.assertEquals("array4", "5", ss[4]);

        ss = xmlS1.getStrings();
        Assert.assertNotNull("content", ss);
        Assert.assertEquals("content", 5, ss.length);
        Assert.assertEquals("array0", "10", ss[0]);
        Assert.assertEquals("array4", "50", ss[4]);

        ss = xmlD0.getStrings();
        Assert.assertNull("content", ss);

        ss = xmlD1.getStrings();
        Assert.assertNull("content", ss);

        ss = xmlI0.getStrings();
        Assert.assertNull("content", ss);

        ss = xmlI1.getStrings();
        Assert.assertNull("content", ss);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getDoubles()'
     */
    @Test
    public void testGetDoubles() {
        try {
            double[] ss = xomS.getDoubles();
            Assert.assertNull("empty content", ss);

            ss = xomD0.getDoubles();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 1.0, ss[0], EPS);
            Assert.assertEquals("array4", 5.0, ss[4], EPS);

            ss = xomD1.getDoubles();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 10., ss[0], EPS);
            Assert.assertEquals("array4", 50., ss[4], EPS);

            ss = xomS0.getDoubles();
            Assert.assertNull("content", ss);

            ss = xomS1.getDoubles();
            Assert.assertNull("content", ss);

            ss = xomI0.getDoubles();
            Assert.assertNull("content", ss);

            ss = xomI1.getDoubles();
            Assert.assertNull("content", ss);

            ss = xmlS.getDoubles();
            Assert.assertNull("empty content", ss);

            ss = xmlD0.getDoubles();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 1.0, ss[0], EPS);
            Assert.assertEquals("array4", 5.0, ss[4], EPS);

            ss = xmlD1.getDoubles();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 10., ss[0], EPS);
            Assert.assertEquals("array4", 50., ss[4], EPS);

            ss = xmlS0.getDoubles();
            Assert.assertNull("content", ss);

            ss = xmlS1.getDoubles();
            Assert.assertNull("content", ss);

            ss = xmlI0.getDoubles();
            Assert.assertNull("content", ss);

            ss = xmlI1.getDoubles();
            Assert.assertNull("content", ss);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw double parsing error");
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getInts()'
     */
    @Test
    public void testGetInts() {
        try {
            int[] ss = xomS.getInts();
            Assert.assertNull("empty content", ss);

            ss = xomI0.getInts();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 1, ss[0]);
            Assert.assertEquals("array4", 5, ss[4]);

            ss = xomI1.getInts();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 10, ss[0], EPS);
            Assert.assertEquals("array4", 50, ss[4], EPS);

            ss = xomS0.getInts();
            Assert.assertNull("content", ss);

            ss = xomS1.getInts();
            Assert.assertNull("content", ss);

            ss = xomD0.getInts();
            Assert.assertNull("content", ss);

            ss = xomD1.getInts();
            Assert.assertNull("content", ss);

            ss = xmlS.getInts();
            Assert.assertNull("empty content", ss);

            ss = xmlI0.getInts();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 1, ss[0]);
            Assert.assertEquals("array4", 5, ss[4]);

            ss = xmlI1.getInts();
            Assert.assertNotNull("content", ss);
            Assert.assertEquals("content", 5, ss.length);
            Assert.assertEquals("array0", 10, ss[0]);
            Assert.assertEquals("array4", 50, ss[4]);

            ss = xmlS0.getInts();
            Assert.assertNull("content", ss);

            ss = xmlS1.getInts();
            Assert.assertNull("content", ss);

            ss = xmlD0.getInts();
            Assert.assertNull("content", ss);

            ss = xmlD1.getInts();
            Assert.assertNull("content", ss);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw double parsing error");
        }

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setArray(String[])'
     */
    @Test
    public void testSetArrayStringArray() {
        Assert.assertEquals("empty", 0, xomS.getStrings().length);
        try {
            xomS.setArray(s0);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw Exception " + e.getMessage());
        }
        Assert.assertEquals("empty", 5, xomS.getStrings().length);
        try {
            String[] bad = { "a", "b c", "d" };
            xomS.setArray(bad);
            Assert.fail("should throw Exception as string has whitespace");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("should throw Exception ",
                    "cannot delimit {b c} with { }", e.getMessage());
        }
        Assert.assertEquals("empty", 5, xomS.getStrings().length);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setArray(double[])'
     */
    @Test
    public void testSetArrayDoubleArray() {
        try {
            Assert.assertEquals("double", 5, xomD0.getDoubles().length);

            xomS.setArray(d0);
            Assert.assertEquals("d", 5, xomS.getDoubles().length);
            Assert.assertEquals("d", 1.0, xomS.getDoubles()[0]);
            Assert.assertEquals("d", 5.0, xomS.getDoubles()[4]);
            xomS.setArray(d1);
            Assert.assertEquals("d", 5, xomS.getDoubles().length);
            Assert.assertEquals("d", 10.0, xomS.getDoubles()[0]);
            Assert.assertEquals("d", 50.0, xomS.getDoubles()[4]);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw parse exception");
        }

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setArray(int[])'
     */
    @Test
    public void testSetArrayIntArray() {
        try {
            Assert.assertEquals("int", 5, xomI0.getInts().length);

            xomS.setArray(i0);
            Assert.assertEquals("d delim", S_SPACE, xomS.getDelimiter());
            Assert.assertEquals("d type", XSD_INTEGER, xomS.getDataType());
            Assert.assertEquals("d type", XSD_INTEGER, xomS.getDataType());
            Assert.assertEquals("d XML", "1 2 3 4 5", xomS.getXMLContent());
            Assert.assertEquals("d", 5, xomS.getInts().length);
            Assert.assertEquals("d", 1, xomS.getInts()[0]);
            Assert.assertEquals("d", 5, xomS.getInts()[4]);
            Assert.assertNotNull(i1);
            Assert.assertEquals("i1 ", 5, i1.length);
            xomS.setArray(i1);
            Assert.assertNotNull("xomS i1 ", xomS);
            Assert.assertEquals("d delim", S_SPACE, xomS.getDelimiter());
            Assert.assertEquals("d type", XSD_INTEGER, xomS.getDataType());
            Assert
                    .assertEquals("d XML", "10 20 30 40 50", xomS
                            .getXMLContent());
            Assert.assertEquals("d", 5, xomS.getInts().length);
            Assert.assertEquals("d", 10, xomS.getInts()[0]);
            Assert.assertEquals("d", 50, xomS.getInts()[4]);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw parse exception");
        }

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLArray.checkNumericConformability(CMLArray)'
     */
    @Test
    public void testCheckNumericConformability() {
        try {
            xomD0.checkNumericConformability(xomD1);
        } catch (CMLException e) {
            Assert.fail("conformability should not throw " + e.getMessage());
        }
        try {
            xomI0.checkNumericConformability(xomI1);
        } catch (CMLException e) {
            Assert.fail("conformability should not throw " + e.getMessage());
        }
        try {
            xomD0.checkNumericConformability(xomS0);
            Assert.fail("conformability should throw exception");
        } catch (CMLException e) {
        }
        try {
            xomD0.checkNumericConformability(xomI0);
            Assert.fail("conformability should throw exception");
        } catch (CMLException e) {
        }
        try {
            xomI0.checkNumericConformability(xomD0);
            Assert.fail("conformability should throw exception");
        } catch (CMLException e) {
        }
        try {
            xomI0.checkNumericConformability(xomS0);
            Assert.fail("conformability should throw exception");
        } catch (CMLException e) {
        }
        try {
            xomS0.checkNumericConformability(xomS1);
            Assert.fail("conformability should throw exception");
        } catch (CMLException e) {
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.subtract(CMLArray)'
     */
    @Test
    public void testSubtract() {
        CMLArray d3 = null;
        try {
            d3 = xomD1.subtract(xomD0);
        } catch (CMLException e) {
            Assert.fail("subtract should not throw " + e.getMessage());
        }
        Assert.assertNotNull("subtract", d3);
        Assert.assertEquals("subtract", 5, d3.getSize());
        try {
            Assert.assertEquals("subtract", -9., d3.getDoubles()[0]);
        } catch (CMLRuntimeException e) {
            Assert.fail("subtract should not throw " + e.getMessage());
        }

        d3 = null;
        try {
            d3 = xmlD1.subtract(xmlD0);
        } catch (CMLException e) {
            Assert.fail("subtract should not throw " + e.getMessage());
        }
        Assert.assertNotNull("subtract", d3);
        Assert.assertEquals("subtract", 5, d3.getSize());
        try {
            Assert.assertEquals("subtract", -9., d3.getDoubles()[0]);
        } catch (CMLRuntimeException e) {
            Assert.fail("subtract should not throw " + e.getMessage());
        }

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.plus(CMLArray)'
     */
    @Test
    public void testPlus() {
        CMLArray d3 = null;
        try {
            d3 = xomD1.plus(xomD0);
        } catch (CMLException e) {
            Assert.fail("subtract should not throw " + e.getMessage());
        }
        Assert.assertNotNull("plus", d3);
        Assert.assertEquals("plus", 5, d3.getSize());
        try {
            Assert.assertEquals("plus", 11., d3.getDoubles()[0]);
        } catch (CMLRuntimeException e) {
            Assert.fail("plus should not throw " + e.getMessage());
        }

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.append(String)'
     */
    @Test
    public void testAppendString() {
        xomS0.append("f");
        // FIXME - this fails
        // Assert.assertEquals("append", 6, xomS0.getSize());
        Assert.assertEquals("append", "a b c d e f", xomS0.getXMLContent());

        xomS1.append(S_QUERY);
        // FIXME
        // Assert.assertEquals("append", 6, xomS0.getSize());
        Assert.assertEquals("append", "/v/w/x/y/z/?/", xomS1.getXMLContent());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.append(double)'
     */
    @Test
    public void testAppendDouble() {
        xomD0.append(6.);
        Assert.assertEquals("append", "1.0 2.0 3.0 4.0 5.0 6.0", xomD0
                .getXMLContent());
        Assert.assertEquals("append", 6, xomD0.getSize());
        try {
            xomD0.append("f");
            Assert.fail("append should not throw ");
        } catch (CMLRuntimeException e) {
            Assert
                    .assertEquals("cannot add string to double",
                            "Cannot add string to array of: xsd:double", e
                                    .getMessage());
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.append(int)'
     */
    @Test
    public void testAppendInt() {
        xomI0.append(6);
        Assert.assertEquals("append", "1 2 3 4 5 6", xomI0.getXMLContent());
        Assert.assertEquals("append", 6, xomI0.getSize());
        try {
            xomI0.append("f");
            Assert.fail("append should not throw ");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("cannot add string to int",
                    "Cannot add string to array of: xsd:integer", e
                            .getMessage());
        }
    }

    /** test units.
     *
     */
    @Test
    public void testGetUnits() {
        CMLCml cml = (CMLCml) parseValidString(unitsS);

        // arrays
        List<CMLElement> arrays = cml.getElements(".//"+CMLArray.NS);
        Assert.assertEquals("array count", 3, arrays.size());
        CMLArray array = (CMLArray) arrays.get(0);
        UnitAttribute unitsAttribute = (UnitAttribute) array.getUnitsAttribute();
        Assert.assertNotNull("units attribute not null", unitsAttribute);
        Assert.assertEquals("units attribute", "units:deg", unitsAttribute.getValue());
        String namespaceURIString = unitsAttribute.getNamespaceURIString();
        Assert.assertNotNull("unit namespace not null", namespaceURIString);
        Assert.assertEquals("unit namespace", UNIT_NS, namespaceURIString);

        Assert.assertNotNull("unitListMap", unitsUnitListMap);
        Assert.assertEquals("unitListMap size", NUNIT_DICT, unitsUnitListMap.size());
        CMLUnit unit = unitsUnitListMap.getUnit(unitsAttribute);
        Assert.assertNotNull("unit not null", unit);
        Assert.assertEquals("unit ", "deg", unit.getId());
    }

    /**
     * test conversion to SI.
     *
     */
    @Test
    public void testConvertToSI() {
        CMLCml cml = (CMLCml) parseValidString(unitsS);

        // arrays
        List<CMLElement> arrays = cml.getElements(".//"+CMLArray.NS);
        Assert.assertEquals("array count", 3, arrays.size());
        testArray((CMLArray) arrays.get(0), new double[] { 180., 90, 45., 0. },
                CML_UNITS + S_COLON + "deg", new double[] { 3.1415922,
                        1.5707961, 0.78539805, 0 }, CML_SIUNITS + S_COLON
                        + "radian");

        testArray((CMLArray) arrays.get(1),
                new double[] { 100., 50, 0., -25. }, CML_UNITS + S_COLON
                        + "kcal",
                new double[] { 418400., 209200., 0., -104600. }, CML_SIUNITS
                        + S_COLON + "joule");

        testArray((CMLArray) arrays.get(2),
                new double[] { 100., 50, 0., -50. }, CML_UNITS + S_COLON
                        + "celsius", new double[] { 373.15, 323.15, 273.15,
                        223.15 }, CML_SIUNITS + S_COLON + "k");
    }

    private void testArray(CMLArray array, double[] expected0, String units0,
            double[] expected1, String units1) {
        try {
            DoubleTest.assertEquals("array", expected0, array.getDoubles(), EPS);
            Assert.assertEquals("array", units0, array.getUnits());
            array.convertToSI(unitsUnitListMap);
            DoubleTest.assertEquals("array", expected1, array.getDoubles(), EPS);
            Assert.assertEquals("array", units1, array.getUnits());
        } catch (CMLRuntimeException e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.copy()'
     */
    @Test
    public void testCopy() {
        CMLArray array = new CMLArray(new double[] { 1., 2. });
        CMLArray array1 = (CMLArray) array.copy();
        Assert.assertEquals("copy", XSD_DOUBLE, array1.getDataType());
        CMLArrayTest.assertEquals("copy", new double[] { 1., 2. }, array1, EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setDelimiter(String)'
     */
    @Test
    public void testSetDelimiter() {
        CMLArray array = new CMLArray();
        array.setDataType(XSD_DOUBLE);
        array.setDelimiter(S_SLASH);
        array.setXMLContent("1./2.");
        double[] dd = array.getDoubles();
        Assert.assertNotNull("dd null", dd);
        DoubleTest.assertEquals("double delimiter", new double[] { 1., 2. },
                dd, EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getStringValues()'
     */
    @Test
    public void testGetStringValues() {
        CMLArray array = new CMLArray(new String[] { "a", "b" });
        StringTest.assertEquals("strings", new String[] { "a", "b" },
                (String[]) array.getStringValues().toArray(new String[0]));
        array = new CMLArray(new int[] { 1, 2 });
        StringTest.assertEquals("ints", new String[] { "1", "2" },
                (String[]) array.getStringValues().toArray(new String[0]));
        array = new CMLArray(new double[] { 1, 2 });
        StringTest.assertEquals("ints", new String[] { "1.0", "2.0" },
                (String[]) array.getStringValues().toArray(new String[0]));
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.getUnit(NamespaceToUnitListMap)'
     */
    @Test
    public void testGetUnit() {
        try {
            new AbstractUnitTest().setUp();
        } catch (Exception e) {
            neverThrow(e);
        }
        NamespaceToUnitListMap unitListMap = AbstractUnitTest.unitListMap;
        Assert.assertNotNull("unitlistMap not null", unitListMap);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLArray.setUnits(String,
     * String)'
     */
    @Test
    public void testGetSetUnitsStringString() {
        String unitsS = xmlD0.getUnits();
        Assert.assertNull("units on D0", unitsS);
        String prefix = "siUnits";
        String units = "m";
        String namespace = SIUNIT_NS;
        xmlD0.setUnits(prefix, units, namespace);
        unitsS = xmlD0.getUnits();
        Assert.assertNotNull("units on D0", unitsS);
        Assert.assertEquals("units on D0", "siUnits:m", unitsS);
        UnitAttribute unitsAttribute = (UnitAttribute) xmlD0.getUnitsAttribute();
        Assert.assertEquals("units namespace on D0", SIUNIT_NS,
                unitsAttribute.getNamespaceURIString());

        try {
            new AbstractUnitTest().setUp();
        } catch (Exception e) {
            neverThrow(e);
        }
        NamespaceToUnitListMap unitListMap = AbstractUnitTest.unitListMap;
        Assert.assertNotNull("unitlistMap not null", unitListMap);
        Assert.assertEquals("unitlistMap size", NUNIT_DICT, unitListMap.size());
        CMLUnit unit = xmlD0.getUnit(unitListMap);
        Assert.assertNotNull("unit not null", unit);
    }

 }
