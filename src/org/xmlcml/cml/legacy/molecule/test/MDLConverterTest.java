package org.xmlcml.cml.legacy.molecule.test;

import java.io.ByteArrayOutputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.test.BaseTest;
import org.xmlcml.cml.legacy.molecule.MDLConverter;

/**
 * test MDLConverter
 *
 * @author pmr
 *
 */
public class MDLConverterTest extends BaseTest {

    String theExpectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            + "<molecule title=\"\" "
            + CML_XMLNS
            + ">"
            + "\r\n"
            + "  <atomArray>\r\n    <atom id=\"a1\" x2=\"0.0936\" y2=\"-0.5856\" elementType=\"C\"/>\r\n    <atom id=\"a2\" x2=\"1.5707\" y2=\"-0.5852\" elementType=\"C\"/>\r\n    <atom id=\"a3\" x2=\"0.8335\" y2=\"-0.1587\" elementType=\"C\" isotope=\"13.0\"/>\r\n    <atom id=\"a4\" x2=\"1.5707\" y2=\"-1.4397\" elementType=\"C\" spinMultiplicity=\"2\"/>\r\n    <atom id=\"a5\" x2=\"0.0936\" y2=\"-1.4435\" elementType=\"C\" spinMultiplicity=\"1\"/>\r\n    <atom id=\"a6\" x2=\"0.8354\" y2=\"-1.8661\" elementType=\"C\" spinMultiplicity=\"3\"/>\r\n    <atom id=\"a7\" x2=\"2.3068\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"1\"/>\r\n    <atom id=\"a8\" x2=\"3.0429\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"2\"/>\r\n    <atom id=\"a9\" x2=\"3.7791\" y2=\"-0.1602\" elementType=\"C\"/>\r\n    <atom id=\"a10\" x2=\"4.5152\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-1\"/>\r\n    <atom id=\"a11\" x2=\"5.2513\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"-2\"/>\r\n    <atom id=\"a12\" x2=\"5.9874\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-4\"/>\r\n    <atom id=\"a13\" x2=\"3.0429\" y2=\"-1.4352\" elementType=\"C\" formalCharge=\"3\"/>\r\n    <atom id=\"a14\" x2=\"3.7791\" y2=\"-1.8602\" elementType=\"C\" formalCharge=\"4\"/>\r\n    <atom id=\"a15\" x2=\"3.7791\" y2=\"-2.7102\" elementType=\"C\"/>\r\n    <atom id=\"a16\" x2=\"3.7791\" y2=\"0.6898\" elementType=\"C\" formalCharge=\"5\"/>\r\n    <atom id=\"a17\" x2=\"4.5152\" y2=\"1.1148\" elementType=\"C\"/>\r\n    <atom id=\"a18\" x2=\"4.5152\" y2=\"1.9648\" elementType=\"C\" formalCharge=\"-3\"/>\r\n    <atom id=\"a19\" x2=\"3.7791\" y2=\"2.3898\" elementType=\"C\" formalCharge=\"-5\"/>\r\n    <atom id=\"a20\" x2=\"-0.6426\" y2=\"-0.1606\" elementType=\"N\"/>\r\n    <atom id=\"a21\" x2=\"-1.4104\" y2=\"-0.604\" elementType=\"C\"/>\r\n    <atom id=\"a22\" x2=\"-2.0381\" y2=\"-0.0285\" elementType=\"C\"/>\r\n    <atom id=\"a23\" x2=\"-2.7852\" y2=\"-0.4279\" elementType=\"O\"/>\r\n    <atom id=\"a24\" x2=\"-1.5006\" y2=\"-1.4492\" elementType=\"C\"/>\r\n    <atom id=\"a25\" x2=\"-0.7395\" y2=\"-1.8161\" elementType=\"S\"/>\r\n  </atomArray>\r\n  <bondArray>\r\n    <bond atomRefs2=\"a3 a1\" id=\"b1\" order=\"2\"/>\r\n    <bond atomRefs2=\"a4 a2\" id=\"b2\" order=\"2\"/>\r\n    <bond atomRefs2=\"a1 a5\" id=\"b3\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a3\" id=\"b4\" order=\"1\"/>\r\n    <bond atomRefs2=\"a5 a6\" id=\"b5\" order=\"2\"/>\r\n    <bond atomRefs2=\"a6 a4\" id=\"b6\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a7\" id=\"b7\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a7 a8\" id=\"b8\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a9\" id=\"b9\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a10\" id=\"b10\" order=\"1\"/>\r\n    <bond atomRefs2=\"a10 a11\" id=\"b11\" order=\"1\"/>\r\n    <bond atomRefs2=\"a11 a12\" id=\"b12\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a13\" id=\"b13\" order=\"1\"/>\r\n    <bond atomRefs2=\"a13 a14\" id=\"b14\" order=\"1\"/>\r\n    <bond atomRefs2=\"a14 a15\" id=\"b15\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a16\" id=\"b16\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a16 a17\" id=\"b17\" order=\"1\"/>\r\n    <bond atomRefs2=\"a17 a18\" id=\"b18\" order=\"1\"/>\r\n    <bond atomRefs2=\"a18 a19\" id=\"b19\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a20\" id=\"b20\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a22\" id=\"b21\" order=\"1\"/>\r\n    <bond atomRefs2=\"a22 a23\" id=\"b22\" order=\"2\"/>\r\n    <bond atomRefs2=\"a24 a25\" id=\"b23\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a21 a24\" id=\"b24\" order=\"1\"/>\r\n    <bond atomRefs2=\"a1 a20\" id=\"b25\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n  </bondArray>\r\n  <formula concise=\"C 22 N 1 O 1 S 1\"/>\r\n</molecule>\r\n";

    String theExpectedXML1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            + "<molecule title=\"\" xmlns=\""
            + CML_NS
            + "\">\r\n  <atomArray>\r\n    <atom id=\"a1\" x2=\"0.0936\" y2=\"-0.5856\" elementType=\"C\"/>\r\n    <atom id=\"a2\" x2=\"1.5707\" y2=\"-0.5852\" elementType=\"C\"/>\r\n    <atom id=\"a3\" x2=\"0.8335\" y2=\"-0.1587\" elementType=\"C\" isotope=\"13.0\"/>\r\n    <atom id=\"a4\" x2=\"1.5707\" y2=\"-1.4397\" elementType=\"C\" spinMultiplicity=\"2\"/>\r\n    <atom id=\"a5\" x2=\"0.0936\" y2=\"-1.4435\" elementType=\"C\" spinMultiplicity=\"1\"/>\r\n    <atom id=\"a6\" x2=\"0.8354\" y2=\"-1.8661\" elementType=\"C\" spinMultiplicity=\"3\"/>\r\n    <atom id=\"a7\" x2=\"2.3068\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"1\"/>\r\n    <atom id=\"a8\" x2=\"3.0429\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"2\"/>\r\n    <atom id=\"a9\" x2=\"3.7791\" y2=\"-0.1602\" elementType=\"C\"/>\r\n    <atom id=\"a10\" x2=\"4.5152\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-1\"/>\r\n    <atom id=\"a11\" x2=\"5.2513\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"-2\"/>\r\n    <atom id=\"a12\" x2=\"5.9874\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-4\"/>\r\n    <atom id=\"a13\" x2=\"3.0429\" y2=\"-1.4352\" elementType=\"C\" formalCharge=\"3\"/>\r\n    <atom id=\"a14\" x2=\"3.7791\" y2=\"-1.8602\" elementType=\"C\" formalCharge=\"4\"/>\r\n    <atom id=\"a15\" x2=\"3.7791\" y2=\"-2.7102\" elementType=\"C\"/>\r\n    <atom id=\"a16\" x2=\"3.7791\" y2=\"0.6898\" elementType=\"C\" formalCharge=\"5\"/>\r\n    <atom id=\"a17\" x2=\"4.5152\" y2=\"1.1148\" elementType=\"C\"/>\r\n    <atom id=\"a18\" x2=\"4.5152\" y2=\"1.9648\" elementType=\"C\" formalCharge=\"-3\"/>\r\n    <atom id=\"a19\" x2=\"3.7791\" y2=\"2.3898\" elementType=\"C\" formalCharge=\"-5\"/>\r\n    <atom id=\"a20\" x2=\"-0.6426\" y2=\"-0.1606\" elementType=\"N\"/>\r\n    <atom id=\"a21\" x2=\"-1.4104\" y2=\"-0.604\" elementType=\"C\"/>\r\n    <atom id=\"a22\" x2=\"-2.0381\" y2=\"-0.0285\" elementType=\"C\"/>\r\n    <atom id=\"a23\" x2=\"-2.7852\" y2=\"-0.4279\" elementType=\"O\"/>\r\n    <atom id=\"a24\" x2=\"-1.5006\" y2=\"-1.4492\" elementType=\"C\"/>\r\n    <atom id=\"a25\" x2=\"-0.7395\" y2=\"-1.8161\" elementType=\"S\"/>\r\n  </atomArray>\r\n  <bondArray>\r\n    <bond atomRefs2=\"a3 a1\" id=\"b1\" order=\"2\"/>\r\n    <bond atomRefs2=\"a4 a2\" id=\"b2\" order=\"2\"/>\r\n    <bond atomRefs2=\"a1 a5\" id=\"b3\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a3\" id=\"b4\" order=\"1\"/>\r\n    <bond atomRefs2=\"a5 a6\" id=\"b5\" order=\"2\"/>\r\n    <bond atomRefs2=\"a6 a4\" id=\"b6\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a7\" id=\"b7\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a7 a8\" id=\"b8\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a9\" id=\"b9\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a10\" id=\"b10\" order=\"1\"/>\r\n    <bond atomRefs2=\"a10 a11\" id=\"b11\" order=\"1\"/>\r\n    <bond atomRefs2=\"a11 a12\" id=\"b12\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a13\" id=\"b13\" order=\"1\"/>\r\n    <bond atomRefs2=\"a13 a14\" id=\"b14\" order=\"1\"/>\r\n    <bond atomRefs2=\"a14 a15\" id=\"b15\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a16\" id=\"b16\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a16 a17\" id=\"b17\" order=\"1\"/>\r\n    <bond atomRefs2=\"a17 a18\" id=\"b18\" order=\"1\"/>\r\n    <bond atomRefs2=\"a18 a19\" id=\"b19\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a20\" id=\"b20\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a22\" id=\"b21\" order=\"1\"/>\r\n    <bond atomRefs2=\"a22 a23\" id=\"b22\" order=\"2\"/>\r\n    <bond atomRefs2=\"a24 a25\" id=\"b23\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a21 a24\" id=\"b24\" order=\"1\"/>\r\n    <bond atomRefs2=\"a1 a20\" id=\"b25\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n  </bondArray>\r\n</molecule>\r\n";

    /**
     * Test method to test converstion from MOL to CML
     */
    @Test
    public void testMOLtoCML() {

        StringReader theMOLStream = new StringReader(
                "\n  MDL-Draw08160515112D\n\n 25 25  0  0  0  0  0  0  0  0999 V2000\n    0.0936   -0.5856    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    1.5707   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.8335   -0.1587    0.0000 C   1  0  0  0  0  0  0  0  0  0  0  0\n    1.5707   -1.4397    0.0000 C   0  4  0  0  0  0  0  0  0  0  0  0\n    0.0936   -1.4435    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.8354   -1.8661    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    2.3068   -0.1602    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n    3.0429   -0.5852    0.0000 C   0  2  0  0  0  0  0  0  0  0  0  0\n    3.7791   -0.1602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.5152   -0.5852    0.0000 C   0  5  0  0  0  0  0  0  0  0  0  0\n    5.2513   -0.1602    0.0000 C   0  6  0  0  0  0  0  0  0  0  0  0\n    5.9874   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    3.0429   -1.4352    0.0000 C   0  1  0  0  0  0  0  0  0  0  0  0\n    3.7791   -1.8602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    3.7791   -2.7102    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    3.7791    0.6898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.5152    1.1148    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    4.5152    1.9648    0.0000 C   0  7  0  0  0  0  0  0  0  0  0  0\n    3.7791    2.3898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.6426   -0.1606    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.4104   -0.6040    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -2.0381   -0.0285    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -2.7852   -0.4279    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.5006   -1.4492    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.7395   -1.8161    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n  3  1  2  0  0  0  0\n  4  2  2  0  0  0  0\n  1  5  1  0  0  0  0\n  2  3  1  0  0  0  0\n  5  6  2  0  0  0  0\n  6  4  1  0  0  0  0\n  2  7  1  6  0  0  0\n  7  8  1  0  0  0  0\n  8  9  1  0  0  0  0\n  9 10  1  0  0  0  0\n 10 11  1  0  0  0  0\n 11 12  1  0  0  0  0\n  8 13  1  0  0  0  0\n 13 14  1  0  0  0  0\n 14 15  1  0  0  0  0\n  9 16  1  6  0  0  0\n 16 17  1  0  0  0  0\n 17 18  1  0  0  0  0\n 18 19  1  0  0  0  0\n 21 20  1  0  0  0  0\n 21 22  1  0  0  0  0\n 22 23  2  0  0  0  0\n 24 25  1  1  0  0  0\n 21 24  1  0  0  0  0\n  1 20  1  1  0  0  0\nM  CHG  8   7   1   8   2  10  -1  11  -2  12  -4  13   3  14   4  16   5\nM  CHG  2  18  -3  19  -5\nM  RAD  3   4   2   5   1   6   3\nM  ISO  1   3  13\nM  END\n");

        LineNumberReader lineReader = new LineNumberReader(theMOLStream);
        MDLConverter mdl = new MDLConverter();

        try {
            mdl.readMOL(lineReader); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading MOL:" + e);
        }

        ByteArrayOutputStream theXMLStream = new ByteArrayOutputStream();

        try {
            mdl.writeXML(theXMLStream); // simply output whats read
        } catch (Exception e) {
            Assert.fail("Exception thrown when writing CML:" + e);
        }

        // check the output is as expected (string needs to be updated as code
        // is updated)
        // dont forget /n/r in expected string.
        Assert.assertEquals("Expected CML:", theExpectedXML1.trim(),
                theXMLStream.toString().trim());
    }

    /** test */
    @Test
    public void testMOLv3toCML() {
        StringReader theMOLStream = new StringReader(
                "\r\n  MDL-Draw08220511522D\r\n\r\n  0  0  0     0  0            999 V3000\r\nM  V30 BEGIN CTAB\r\nM  V30 COUNTS 25 25 0 0 0\r\nM  V30 BEGIN ATOM\r\nM  V30 1 C 0.0936 -0.5856 0 0 \r\nM  V30 2 C 1.5707 -0.5852 0 0 \r\nM  V30 3 C 0.8335 -0.1587 0 0 MASS=13 \r\nM  V30 4 C 1.5707 -1.4397 0 0 RAD=2 \r\nM  V30 5 C 0.0936 -1.4435 0 0 RAD=1 \r\nM  V30 6 C 0.8354 -1.8661 0 0 RAD=3 \r\nM  V30 7 C 2.3068 -0.1602 0 0 CHG=1 \r\nM  V30 8 C 3.0429 -0.5852 0 0 CHG=2 \r\nM  V30 9 C 3.7791 -0.1602 0 0 \r\nM  V30 10 C 4.5152 -0.5852 0 0 CHG=-1 \r\nM  V30 11 C 5.2513 -0.1602 0 0 CHG=-2 \r\nM  V30 12 C 5.9874 -0.5852 0 0 CHG=-4 \r\nM  V30 13 C 3.0429 -1.4352 0 0 CHG=3 \r\nM  V30 14 C 3.7791 -1.8602 0 0 CHG=4 \r\nM  V30 15 C 3.7791 -2.7102 0 0 \r\nM  V30 16 C 3.7791 0.6898 0 0 CHG=5 \r\nM  V30 17 C 4.5152 1.1148 0 0 \r\nM  V30 18 C 4.5152 1.9648 0 0 CHG=-3 \r\nM  V30 19 C 3.7791 2.3898 0 0 CHG=-5 \r\nM  V30 20 N -0.6426 -0.1606 0 0 \r\nM  V30 21 C -1.4104 -0.604 0 0 \r\nM  V30 22 C -2.0381 -0.0285 0 0 \r\nM  V30 23 O -2.7852 -0.4279 0 0 \r\nM  V30 24 C -1.5006 -1.4492 0 0 \r\nM  V30 25 S -0.7395 -1.8161 0 0 \r\nM  V30 END ATOM\r\nM  V30 BEGIN BOND\r\nM  V30 1 2 3 1 \r\nM  V30 2 2 4 2 \r\nM  V30 3 1 1 5 \r\nM  V30 4 1 2 3 \r\nM  V30 5 2 5 6 \r\nM  V30 6 1 6 4 \r\nM  V30 7 1 2 7 CFG=3 \r\nM  V30 8 1 7 8 \r\nM  V30 9 1 8 9 \r\nM  V30 10 1 9 10 \r\nM  V30 11 1 10 11 \r\nM  V30 12 1 11 12 \r\nM  V30 13 1 8 13 \r\nM  V30 14 1 13 14 \r\nM  V30 15 1 14 15 \r\nM  V30 16 1 9 16 CFG=3 \r\nM  V30 17 1 16 17 \r\nM  V30 18 1 17 18 \r\nM  V30 19 1 18 19 \r\nM  V30 20 1 21 20 \r\nM  V30 21 1 21 22 \r\nM  V30 22 2 22 23 \r\nM  V30 23 1 24 25 CFG=1 \r\nM  V30 24 1 21 24 \r\nM  V30 25 1 1 20 CFG=1 \r\nM  V30 END BOND\r\nM  V30 END CTAB\r\nM  END\r\n");

        LineNumberReader lineReader = new LineNumberReader(theMOLStream);
        MDLConverter mdl = new MDLConverter();

        try {
            mdl.readMOL(lineReader); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading MOL:" + e);
        }

        ByteArrayOutputStream theXMLStream = new ByteArrayOutputStream();

        try {
            mdl.writeXML(theXMLStream); // simply output whats read
        } catch (Exception e) {
            Assert.fail("Exception thrown when retrieving CML:" + e);
        }

        // check the output is as expected (string needs to be updated as code
        // is updated)
        // dont forget /n/r in expected string.
        Assert.assertEquals("Expected CML:", theExpectedXML1.trim(),
                theXMLStream.toString().trim());
    }

    /** test */
    @Test
    public void testCMLtoMOLv3() {
        StringReader theXMLStream = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                        + "<molecule title=\"\"  "
                        + CML_XMLNS
                        + ">"
                        + "\r\n  <atomArray>\r\n    <atom id=\"a1\" x2=\"0.0936\" y2=\"-0.5856\" elementType=\"C\"/>\r\n    <atom id=\"a2\" x2=\"1.5707\" y2=\"-0.5852\" elementType=\"C\"/>\r\n    <atom id=\"a3\" x2=\"0.8335\" y2=\"-0.1587\" elementType=\"C\" isotope=\"13.0\"/>\r\n    <atom id=\"a4\" x2=\"1.5707\" y2=\"-1.4397\" elementType=\"C\" spinMultiplicity=\"2\"/>\r\n    <atom id=\"a5\" x2=\"0.0936\" y2=\"-1.4435\" elementType=\"C\" spinMultiplicity=\"1\"/>\r\n    <atom id=\"a6\" x2=\"0.8354\" y2=\"-1.8661\" elementType=\"C\" spinMultiplicity=\"3\"/>\r\n    <atom id=\"a7\" x2=\"2.3068\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"1\"/>\r\n    <atom id=\"a8\" x2=\"3.0429\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"2\"/>\r\n    <atom id=\"a9\" x2=\"3.7791\" y2=\"-0.1602\" elementType=\"C\"/>\r\n    <atom id=\"a10\" x2=\"4.5152\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-1\"/>\r\n    <atom id=\"a11\" x2=\"5.2513\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"-2\"/>\r\n    <atom id=\"a12\" x2=\"5.9874\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-4\"/>\r\n    <atom id=\"a13\" x2=\"3.0429\" y2=\"-1.4352\" elementType=\"C\" formalCharge=\"3\"/>\r\n    <atom id=\"a14\" x2=\"3.7791\" y2=\"-1.8602\" elementType=\"C\" formalCharge=\"4\"/>\r\n    <atom id=\"a15\" x2=\"3.7791\" y2=\"-2.7102\" elementType=\"C\"/>\r\n    <atom id=\"a16\" x2=\"3.7791\" y2=\"0.6898\" elementType=\"C\" formalCharge=\"5\"/>\r\n    <atom id=\"a17\" x2=\"4.5152\" y2=\"1.1148\" elementType=\"C\"/>\r\n    <atom id=\"a18\" x2=\"4.5152\" y2=\"1.9648\" elementType=\"C\" formalCharge=\"-3\"/>\r\n    <atom id=\"a19\" x2=\"3.7791\" y2=\"2.3898\" elementType=\"C\" formalCharge=\"-5\"/>\r\n    <atom id=\"a20\" x2=\"-0.6426\" y2=\"-0.1606\" elementType=\"N\"/>\r\n    <atom id=\"a21\" x2=\"-1.4104\" y2=\"-0.604\" elementType=\"C\"/>\r\n    <atom id=\"a22\" x2=\"-2.0381\" y2=\"-0.0285\" elementType=\"C\"/>\r\n    <atom id=\"a23\" x2=\"-2.7852\" y2=\"-0.4279\" elementType=\"O\"/>\r\n    <atom id=\"a24\" x2=\"-1.5006\" y2=\"-1.4492\" elementType=\"C\"/>\r\n    <atom id=\"a25\" x2=\"-0.7395\" y2=\"-1.8161\" elementType=\"S\"/>\r\n  </atomArray>\r\n  <bondArray>\r\n    <bond atomRefs2=\"a3 a1\" id=\"b1\" order=\"2\"/>\r\n    <bond atomRefs2=\"a4 a2\" id=\"b2\" order=\"2\"/>\r\n    <bond atomRefs2=\"a1 a5\" id=\"b3\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a3\" id=\"b4\" order=\"1\"/>\r\n    <bond atomRefs2=\"a5 a6\" id=\"b5\" order=\"2\"/>\r\n    <bond atomRefs2=\"a6 a4\" id=\"b6\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a7\" id=\"b7\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a7 a8\" id=\"b8\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a9\" id=\"b9\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a10\" id=\"b10\" order=\"1\"/>\r\n    <bond atomRefs2=\"a10 a11\" id=\"b11\" order=\"1\"/>\r\n    <bond atomRefs2=\"a11 a12\" id=\"b12\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a13\" id=\"b13\" order=\"1\"/>\r\n    <bond atomRefs2=\"a13 a14\" id=\"b14\" order=\"1\"/>\r\n    <bond atomRefs2=\"a14 a15\" id=\"b15\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a16\" id=\"b16\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a16 a17\" id=\"b17\" order=\"1\"/>\r\n    <bond atomRefs2=\"a17 a18\" id=\"b18\" order=\"1\"/>\r\n    <bond atomRefs2=\"a18 a19\" id=\"b19\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a20\" id=\"b20\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a22\" id=\"b21\" order=\"1\"/>\r\n    <bond atomRefs2=\"a22 a23\" id=\"b22\" order=\"2\"/>\r\n    <bond atomRefs2=\"a24 a25\" id=\"b23\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a21 a24\" id=\"b24\" order=\"1\"/>\r\n    <bond atomRefs2=\"a1 a20\" id=\"b25\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n  </bondArray>\r\n</molecule>\r\n");
        String theExpectedMOL = "\r\n  CML DOM 08240516452D\r\n\r\n  0  0  0     0  0            999 V3000\r\nM  V30 BEGIN CTAB\r\nM  V30 COUNTS 25 25 0 0 0\r\nM  V30 BEGIN ATOM\r\nM  V30 1 C 0.0936 -0.5856 0 0\r\nM  V30 2 C 1.5707 -0.5852 0 0\r\nM  V30 3 C 0.8335 -0.1587 0 0 MASS=13\r\nM  V30 4 C 1.5707 -1.4397 0 0 RAD=2\r\nM  V30 5 C 0.0936 -1.4435 0 0 RAD=1\r\nM  V30 6 C 0.8354 -1.8661 0 0 RAD=3\r\nM  V30 7 C 2.3068 -0.1602 0 0 CHG=1\r\nM  V30 8 C 3.0429 -0.5852 0 0 CHG=2\r\nM  V30 9 C 3.7791 -0.1602 0 0\r\nM  V30 10 C 4.5152 -0.5852 0 0 CHG=-1\r\nM  V30 11 C 5.2513 -0.1602 0 0 CHG=-2\r\nM  V30 12 C 5.9874 -0.5852 0 0 CHG=-4\r\nM  V30 13 C 3.0429 -1.4352 0 0 CHG=3\r\nM  V30 14 C 3.7791 -1.8602 0 0 CHG=4\r\nM  V30 15 C 3.7791 -2.7102 0 0\r\nM  V30 16 C 3.7791 0.6898 0 0 CHG=5\r\nM  V30 17 C 4.5152 1.1148 0 0\r\nM  V30 18 C 4.5152 1.9648 0 0 CHG=-3\r\nM  V30 19 C 3.7791 2.3898 0 0 CHG=-5\r\nM  V30 20 N -0.6426 -0.1606 0 0\r\nM  V30 21 C -1.4104 -0.604 0 0\r\nM  V30 22 C -2.0381 -0.0285 0 0\r\nM  V30 23 O -2.7852 -0.4279 0 0\r\nM  V30 24 C -1.5006 -1.4492 0 0\r\nM  V30 25 S -0.7395 -1.8161 0 0\r\nM  V30 END ATOM\r\nM  V30 BEGIN BOND\r\nM  V30 1 2 3 1\r\nM  V30 2 2 4 2\r\nM  V30 3 1 1 5\r\nM  V30 4 1 2 3\r\nM  V30 5 2 5 6\r\nM  V30 6 1 6 4\r\nM  V30 7 1 2 7 CFG=3\r\nM  V30 8 1 7 8\r\nM  V30 9 1 8 9\r\nM  V30 10 1 9 10\r\nM  V30 11 1 10 11\r\nM  V30 12 1 11 12\r\nM  V30 13 1 8 13\r\nM  V30 14 1 13 14\r\nM  V30 15 1 14 15\r\nM  V30 16 1 9 16 CFG=3\r\nM  V30 17 1 16 17\r\nM  V30 18 1 17 18\r\nM  V30 19 1 18 19\r\nM  V30 20 1 21 20\r\nM  V30 21 1 21 22\r\nM  V30 22 2 22 23\r\nM  V30 23 1 24 25 CFG=1\r\nM  V30 24 1 21 24\r\nM  V30 25 1 1 20 CFG=1\r\nM  V30 END BOND\r\nM  V30 END CTAB\r\nM  END\r\n";

        MDLConverter mdl = new MDLConverter();

        try {
            mdl.readXML(theXMLStream);
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading CML:" + e);
        }

        StringWriter theMOLStream = new StringWriter();
        try {
            mdl.setVersion(MDLConverter.V3000);
            mdl.writeMOL(theMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when writing MOL:" + e);
        }

        // check the output is as expected (string needs to be updated as code
        // is updated)
        // substring(21) removes date, which changes
        Assert.assertEquals("Expected CML:", theExpectedMOL.trim()
                .substring(22), theMOLStream.toString().trim().substring(22));

    }

    /** test */
    @Test
    public void testCMLtoMOL() {

        StringReader theXMLStream = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                        + "<molecule title=\"\"  "
                        + CML_XMLNS
                        + ">"
                        + "\r\n  <atomArray>\r\n    <atom id=\"a1\" x2=\"0.0936\" y2=\"-0.5856\" elementType=\"C\"/>\r\n    <atom id=\"a2\" x2=\"1.5707\" y2=\"-0.5852\" elementType=\"C\"/>\r\n    <atom id=\"a3\" x2=\"0.8335\" y2=\"-0.1587\" elementType=\"C\" isotope=\"13.0\"/>\r\n    <atom id=\"a4\" x2=\"1.5707\" y2=\"-1.4397\" elementType=\"C\" spinMultiplicity=\"2\"/>\r\n    <atom id=\"a5\" x2=\"0.0936\" y2=\"-1.4435\" elementType=\"C\" spinMultiplicity=\"1\"/>\r\n    <atom id=\"a6\" x2=\"0.8354\" y2=\"-1.8661\" elementType=\"C\" spinMultiplicity=\"3\"/>\r\n    <atom id=\"a7\" x2=\"2.3068\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"1\"/>\r\n    <atom id=\"a8\" x2=\"3.0429\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"2\"/>\r\n    <atom id=\"a9\" x2=\"3.7791\" y2=\"-0.1602\" elementType=\"C\"/>\r\n    <atom id=\"a10\" x2=\"4.5152\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-1\"/>\r\n    <atom id=\"a11\" x2=\"5.2513\" y2=\"-0.1602\" elementType=\"C\" formalCharge=\"-2\"/>\r\n    <atom id=\"a12\" x2=\"5.9874\" y2=\"-0.5852\" elementType=\"C\" formalCharge=\"-4\"/>\r\n    <atom id=\"a13\" x2=\"3.0429\" y2=\"-1.4352\" elementType=\"C\" formalCharge=\"3\"/>\r\n    <atom id=\"a14\" x2=\"3.7791\" y2=\"-1.8602\" elementType=\"C\" formalCharge=\"4\"/>\r\n    <atom id=\"a15\" x2=\"3.7791\" y2=\"-2.7102\" elementType=\"C\"/>\r\n    <atom id=\"a16\" x2=\"3.7791\" y2=\"0.6898\" elementType=\"C\" formalCharge=\"5\"/>\r\n    <atom id=\"a17\" x2=\"4.5152\" y2=\"1.1148\" elementType=\"C\"/>\r\n    <atom id=\"a18\" x2=\"4.5152\" y2=\"1.9648\" elementType=\"C\" formalCharge=\"-3\"/>\r\n    <atom id=\"a19\" x2=\"3.7791\" y2=\"2.3898\" elementType=\"C\" formalCharge=\"-5\"/>\r\n    <atom id=\"a20\" x2=\"-0.6426\" y2=\"-0.1606\" elementType=\"N\"/>\r\n    <atom id=\"a21\" x2=\"-1.4104\" y2=\"-0.604\" elementType=\"C\"/>\r\n    <atom id=\"a22\" x2=\"-2.0381\" y2=\"-0.0285\" elementType=\"C\"/>\r\n    <atom id=\"a23\" x2=\"-2.7852\" y2=\"-0.4279\" elementType=\"O\"/>\r\n    <atom id=\"a24\" x2=\"-1.5006\" y2=\"-1.4492\" elementType=\"C\"/>\r\n    <atom id=\"a25\" x2=\"-0.7395\" y2=\"-1.8161\" elementType=\"S\"/>\r\n  </atomArray>\r\n  <bondArray>\r\n    <bond atomRefs2=\"a3 a1\" id=\"b1\" order=\"2\"/>\r\n    <bond atomRefs2=\"a4 a2\" id=\"b2\" order=\"2\"/>\r\n    <bond atomRefs2=\"a1 a5\" id=\"b3\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a3\" id=\"b4\" order=\"1\"/>\r\n    <bond atomRefs2=\"a5 a6\" id=\"b5\" order=\"2\"/>\r\n    <bond atomRefs2=\"a6 a4\" id=\"b6\" order=\"1\"/>\r\n    <bond atomRefs2=\"a2 a7\" id=\"b7\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a7 a8\" id=\"b8\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a9\" id=\"b9\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a10\" id=\"b10\" order=\"1\"/>\r\n    <bond atomRefs2=\"a10 a11\" id=\"b11\" order=\"1\"/>\r\n    <bond atomRefs2=\"a11 a12\" id=\"b12\" order=\"1\"/>\r\n    <bond atomRefs2=\"a8 a13\" id=\"b13\" order=\"1\"/>\r\n    <bond atomRefs2=\"a13 a14\" id=\"b14\" order=\"1\"/>\r\n    <bond atomRefs2=\"a14 a15\" id=\"b15\" order=\"1\"/>\r\n    <bond atomRefs2=\"a9 a16\" id=\"b16\" order=\"1\">\r\n      <bondStereo>H</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a16 a17\" id=\"b17\" order=\"1\"/>\r\n    <bond atomRefs2=\"a17 a18\" id=\"b18\" order=\"1\"/>\r\n    <bond atomRefs2=\"a18 a19\" id=\"b19\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a20\" id=\"b20\" order=\"1\"/>\r\n    <bond atomRefs2=\"a21 a22\" id=\"b21\" order=\"1\"/>\r\n    <bond atomRefs2=\"a22 a23\" id=\"b22\" order=\"2\"/>\r\n    <bond atomRefs2=\"a24 a25\" id=\"b23\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n    <bond atomRefs2=\"a21 a24\" id=\"b24\" order=\"1\"/>\r\n    <bond atomRefs2=\"a1 a20\" id=\"b25\" order=\"1\">\r\n      <bondStereo>W</bondStereo>\r\n    </bond>\r\n  </bondArray>\r\n</molecule>\r\n");
        String theExpectedMOL = "\r\n  CML DOM 08240516382D\r\n\r\n 25 25  0  0  0  0  0  0  0  0999 V2000\r\n    0.0936   -0.5856    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    1.5707   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.8335   -0.1587    0.0000 C   1  0  0  0  0  0  0  0  0  0  0  0\r\n    1.5707   -1.4397    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.0936   -1.4435    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.8354   -1.8661    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    2.3068   -0.1602    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\r\n    3.0429   -0.5852    0.0000 C   0  2  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -0.1602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152   -0.5852    0.0000 C   0  5  0  0  0  0  0  0  0  0  0  0\r\n    5.2513   -0.1602    0.0000 C   0  6  0  0  0  0  0  0  0  0  0  0\r\n    5.9874   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.0429   -1.4352    0.0000 C   0  1  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -1.8602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -2.7102    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.7791    0.6898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152    1.1148    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152    1.9648    0.0000 C   0  7  0  0  0  0  0  0  0  0  0  0\r\n    3.7791    2.3898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -0.6426   -0.1606    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -1.4104   -0.6040    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -2.0381   -0.0285    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -2.7852   -0.4279    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -1.5006   -1.4492    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -0.7395   -1.8161    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\r\n  3  1  2  0  0  0  0\r\n  4  2  2  0  0  0  0\r\n  1  5  1  0  0  0  0\r\n  2  3  1  0  0  0  0\r\n  5  6  2  0  0  0  0\r\n  6  4  1  0  0  0  0\r\n  2  7  1  6  0  0  0\r\n  7  8  1  0  0  0  0\r\n  8  9  1  0  0  0  0\r\n  9 10  1  0  0  0  0\r\n 10 11  1  0  0  0  0\r\n 11 12  1  0  0  0  0\r\n  8 13  1  0  0  0  0\r\n 13 14  1  0  0  0  0\r\n 14 15  1  0  0  0  0\r\n  9 16  1  6  0  0  0\r\n 16 17  1  0  0  0  0\r\n 17 18  1  0  0  0  0\r\n 18 19  1  0  0  0  0\r\n 21 20  1  0  0  0  0\r\n 21 22  1  0  0  0  0\r\n 22 23  2  0  0  0  0\r\n 24 25  1  1  0  0  0\r\n 21 24  1  0  0  0  0\r\n  1 20  1  1  0  0  0\r\nM  CHG  8   7   1   8   2  10  -1  11  -2  12  -4  13   3  14   4  16   5\r\nM  CHG  2  18  -3  19  -5\r\nM  RAD  3   4   2   5   1   6   3\r\nM  ISO  1   3  13\r\nM  END\r\n";

        MDLConverter mdl = new MDLConverter();

        try {
            mdl.readXML(theXMLStream);
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading CML:" + e);
        }

        StringWriter theMOLStream = new StringWriter();
        try {
            mdl.writeMOL(theMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when writing MOL:" + e);
        }

        // check the output is as expected (string needs to be updated as code
        // is updated)
        // substring(22) removes date, which changes
        Assert.assertEquals("Expected CML:", trimEachLine(theExpectedMOL.trim()
                .substring(22)), trimEachLine(theMOLStream.toString().trim()
                .substring(22)));

    }

    /** test */
    @Test
    public void testMOLroundtrip() {
        String inputMOLString = "\r\n  MDL-Draw08160515112D\r\n\r\n 25 25  0  0  0  0  0  0  0  0999 V2000\r\n    0.0936   -0.5856    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    1.5707   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.8335   -0.1587    0.0000 C   1  0  0  0  0  0  0  0  0  0  0  0\r\n    1.5707   -1.4397    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.0936   -1.4435    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    0.8354   -1.8661    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    2.3068   -0.1602    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\r\n    3.0429   -0.5852    0.0000 C   0  2  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -0.1602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152   -0.5852    0.0000 C   0  5  0  0  0  0  0  0  0  0  0  0\r\n    5.2513   -0.1602    0.0000 C   0  6  0  0  0  0  0  0  0  0  0  0\r\n    5.9874   -0.5852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.0429   -1.4352    0.0000 C   0  1  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -1.8602    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.7791   -2.7102    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    3.7791    0.6898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152    1.1148    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n    4.5152    1.9648    0.0000 C   0  7  0  0  0  0  0  0  0  0  0  0\r\n    3.7791    2.3898    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -0.6426   -0.1606    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -1.4104   -0.6040    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -2.0381   -0.0285    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -2.7852   -0.4279    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -1.5006   -1.4492    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n   -0.7395   -1.8161    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\r\n  3  1  2  0  0  0  0\r\n  4  2  2  0  0  0  0\r\n  1  5  1  0  0  0  0\r\n  2  3  1  0  0  0  0\r\n  5  6  2  0  0  0  0\r\n  6  4  1  0  0  0  0\r\n  2  7  1  6  0  0  0\r\n  7  8  1  0  0  0  0\r\n  8  9  1  0  0  0  0\r\n  9 10  1  0  0  0  0\r\n 10 11  1  0  0  0  0\r\n 11 12  1  0  0  0  0\r\n  8 13  1  0  0  0  0\r\n 13 14  1  0  0  0  0\r\n 14 15  1  0  0  0  0\r\n  9 16  1  6  0  0  0\r\n 16 17  1  0  0  0  0\r\n 17 18  1  0  0  0  0\r\n 18 19  1  0  0  0  0\r\n 21 20  1  0  0  0  0\r\n 21 22  1  0  0  0  0\r\n 22 23  2  0  0  0  0\r\n 24 25  1  1  0  0  0\r\n 21 24  1  0  0  0  0\r\n  1 20  1  1  0  0  0\r\nM  CHG  8   7   1   8   2  10  -1  11  -2  12  -4  13   3  14   4  16   5\r\nM  CHG  2  18  -3  19  -5\r\nM  RAD  3   4   2   5   1   6   3\r\nM  ISO  1   3  13\r\nM  END\r\n";
        StringReader inputMOLStream = new StringReader(inputMOLString);

        MDLConverter mdl = new MDLConverter();

        try {
            mdl.readMOL(inputMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading MOL:" + e);
        }

        ByteArrayOutputStream outputXMLStream = new ByteArrayOutputStream();

        try {
            mdl.writeXML(outputXMLStream); // simply output whats read
        } catch (Exception e) {
            Assert.fail("Exception thrown when retrieving CML:" + e);
        }

        StringReader inputXMLStream = new StringReader(outputXMLStream
                .toString());

        try {
            mdl.readXML(inputXMLStream);
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading CML:" + e);
        }

        StringWriter outputMOLStream = new StringWriter();

        try {
            mdl.writeMOL(outputMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when writing MOL:" + e);
        }

        String outputMOLString = outputMOLStream.toString();
        // check the output MOL matches the input MOL
        // substring(22) removes date, which changes
        Assert.assertEquals("Roundtrip, output should equal input:",
                inputMOLString.substring(22), outputMOLString.substring(22));
    }

    /** test */
    @Test
    public void testMOLv3roundtrip() {
        String inputMOLString = "\r\n  MDL-Draw08220511522D\r\n\r\n  0  0  0     0  0            999 V3000\r\nM  V30 BEGIN CTAB\r\nM  V30 COUNTS 25 25 0 0 0\r\nM  V30 BEGIN ATOM\r\nM  V30 1 C 0.0936 -0.5856 0 0 \r\nM  V30 2 C 1.5707 -0.5852 0 0 \r\nM  V30 3 C 0.8335 -0.1587 0 0 MASS=13 \r\nM  V30 4 C 1.5707 -1.4397 0 0 RAD=2 \r\nM  V30 5 C 0.0936 -1.4435 0 0 RAD=1 \r\nM  V30 6 C 0.8354 -1.8661 0 0 RAD=3 \r\nM  V30 7 C 2.3068 -0.1602 0 0 CHG=1 \r\nM  V30 8 C 3.0429 -0.5852 0 0 CHG=2 \r\nM  V30 9 C 3.7791 -0.1602 0 0 \r\nM  V30 10 C 4.5152 -0.5852 0 0 CHG=-1 \r\nM  V30 11 C 5.2513 -0.1602 0 0 CHG=-2 \r\nM  V30 12 C 5.9874 -0.5852 0 0 CHG=-4 \r\nM  V30 13 C 3.0429 -1.4352 0 0 CHG=3 \r\nM  V30 14 C 3.7791 -1.8602 0 0 CHG=4 \r\nM  V30 15 C 3.7791 -2.7102 0 0 \r\nM  V30 16 C 3.7791 0.6898 0 0 CHG=5 \r\nM  V30 17 C 4.5152 1.1148 0 0 \r\nM  V30 18 C 4.5152 1.9648 0 0 CHG=-3 \r\nM  V30 19 C 3.7791 2.3898 0 0 CHG=-5 \r\nM  V30 20 N -0.6426 -0.1606 0 0 \r\nM  V30 21 C -1.4104 -0.604 0 0 \r\nM  V30 22 C -2.0381 -0.0285 0 0 \r\nM  V30 23 O -2.7852 -0.4279 0 0 \r\nM  V30 24 C -1.5006 -1.4492 0 0 \r\nM  V30 25 S -0.7395 -1.8161 0 0 \r\nM  V30 END ATOM\r\nM  V30 BEGIN BOND\r\nM  V30 1 2 3 1 \r\nM  V30 2 2 4 2 \r\nM  V30 3 1 1 5 \r\nM  V30 4 1 2 3 \r\nM  V30 5 2 5 6 \r\nM  V30 6 1 6 4 \r\nM  V30 7 1 2 7 CFG=3 \r\nM  V30 8 1 7 8 \r\nM  V30 9 1 8 9 \r\nM  V30 10 1 9 10 \r\nM  V30 11 1 10 11 \r\nM  V30 12 1 11 12 \r\nM  V30 13 1 8 13 \r\nM  V30 14 1 13 14 \r\nM  V30 15 1 14 15 \r\nM  V30 16 1 9 16 CFG=3 \r\nM  V30 17 1 16 17 \r\nM  V30 18 1 17 18 \r\nM  V30 19 1 18 19 \r\nM  V30 20 1 21 20 \r\nM  V30 21 1 21 22 \r\nM  V30 22 2 22 23 \r\nM  V30 23 1 24 25 CFG=1 \r\nM  V30 24 1 21 24 \r\nM  V30 25 1 1 20 CFG=1 \r\nM  V30 END BOND\r\nM  V30 END CTAB\r\nM  END";
        StringReader inputMOLStream = new StringReader(inputMOLString);

        MDLConverter mdl = new MDLConverter();
        // mdl.setVersion(MDLConverter.V3000);

        try {
            mdl.readMOL(inputMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading MOL:" + e);
        }

        ByteArrayOutputStream outputXMLStream = new ByteArrayOutputStream();

        try {
            mdl.writeXML(outputXMLStream); // simply output whats read
        } catch (Exception e) {
            Assert.fail("Exception thrown when retrieving CML:" + e);
        }

        StringReader inputXMLStream = new StringReader(outputXMLStream
                .toString());

        try {
            mdl.readXML(inputXMLStream);
        } catch (Exception e) {
            Assert.fail("Exception thrown when reading CML:" + e);
        }

        StringWriter outputMOLStream = new StringWriter();

        try {
            mdl.writeMOL(outputMOLStream); // function really being tested
        } catch (Exception e) {
            Assert.fail("Exception thrown when writing MOL:" + e);
        }

        String outputMOLString = outputMOLStream.toString();
        // check the output MOL matches the input MOL
        // substring(22) removes date, which changes
        Assert.assertEquals("Roundtrip, output should equal input:",
                trimEachLine(inputMOLString.substring(22)),
                trimEachLine(outputMOLString.substring(22)));
    }

    /* public */String trimEachLine(String input) {
        String output = "";
        final String newLine = "\r\n";

        int beginLine = -1;
        while (true) {
            beginLine = input.indexOf(newLine, beginLine + 1);
            int endLine = input.indexOf(newLine, beginLine + 1);
            if (endLine == -1) {
                break;
            }
            String line = input.substring(beginLine + 2, endLine);
            line = line.trim();
            output += line + newLine;
        }
        return output;
    }

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {
        MDLConverterTest test = new MDLConverterTest();

        test.testMOLtoCML();
        // test.testCMLtoMOL();
        // //test.testCMLtoMOLv3();
        // test.testMOLv3toCML();
    }

    /**
     * run tests.
     *
     * @return the suite.
     *
     */
 }
