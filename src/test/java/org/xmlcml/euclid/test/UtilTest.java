/**
 * 
 */
package org.xmlcml.euclid.test;

import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.euclid.EuclidConstants.F_S;
import static org.xmlcml.euclid.EuclidConstants.S_COMMA;
import static org.xmlcml.euclid.EuclidConstants.S_PERIOD;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.neverFail;
import static org.xmlcml.util.TestUtils.neverThrow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;
import org.xmlcml.util.TestUtils;

/**
 * @author pm286
 *
 */
public class UtilTest {

    /**
     * Test method for 'org.xmlcml.cml.base.Util.getTEMP_DIRECTORY()'
     */
    @Test
    public final void testGetTEMP_DIRECTORY() {
        File dir = Util.getTEMP_DIRECTORY();
        File f = new File(S_PERIOD + F_S + "target" + U_S + "test-outputs");
        try {
            Assert.assertEquals("temp dir", f.getCanonicalPath(), dir
                    .getAbsolutePath());
            Assert.assertTrue("temp exists", dir.exists());
        } catch (IOException e1) {
            neverThrow(e1);
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.base.Util.getTEMP_DIRECTORY()'
     */
    @Test
    public final void testGetTestOutputDirectory() {
        File dir = Util.getTestOutputDirectory(UtilTest.class);
        File f = new File(Util.getTEMP_DIRECTORY(),
                "org/xmlcml/euclid/test/UtilTest");
        try {
            Assert.assertEquals("test dir", f.getCanonicalPath(), dir
                    .getAbsolutePath());
            Assert.assertTrue("test exists", dir.exists());
        } catch (IOException e1) {
            neverThrow(e1);
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.addElement(String[],
     * String)'
     */
    @Test
    public final void testAddElementToStringArray() {
        String[] array = new String[] { "a", "b" };
        String[] array1 = Util.addElementToStringArray(array, "c");
        Assert.assertEquals("array", 3, array1.length);
        StringTestBase
                .assertEquals("array", new String[] { "a", "b", "c" }, array1);
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.removeElement(String[],
     * String)'
     */
    @Test
    public final void testRemoveElement() {
        String[] array = new String[] { "a", "b", "c" };
        String[] array1 = Util.removeElementFromStringArray(array, "b");
        Assert.assertEquals("array", 2, array1.length);
        StringTestBase.assertEquals("array", new String[] { "a", "c" }, array1);
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.createFile(File, String)'
     */
    @Test
    public final void testCreateFile() {
        File dir = null;
        try {
            dir = Util.getResourceFile(TestUtils.BASE_RESOURCE);
        } catch (Exception e1) {
            neverThrow(e1);
        }
        File junk = new File(dir, "junk");
        if (junk.exists()) {
            junk.delete();
        }
        Assert.assertTrue("create", !junk.exists());
        try {
            Util.createFile(dir, "junk");
        } catch (Exception e) {
            e.printStackTrace();
            neverThrow(e);
        }
        Assert.assertTrue("should exist: " + junk.toString(), junk.exists());
    }

    /**
     * Test method for
     * org.xmlcml.euclid.Util.BUG(java.lang.String, java.lang.Exception)}.
     */
    @Test
    public final void testBUGStringException() {
        try {
            Util.BUG("foo", new Exception("bar"));
            Assert.fail("should throw exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("bug", "BUG: (foo)should never throw: java.lang.Exception: bar", e.getMessage());
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for
     * {org.xmlcml.euclid.Util.BUG(java.lang.Exception)}.
     */
    @Test
    public final void testBUGException() {
        try {
            Util.BUG(new Exception("bar"));
            Assert.fail("should throw exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("bug", "BUG: should never throw: java.lang.Exception: bar", e.getMessage());
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * @deprecated Test method for
     *             {@link org.xmlcml.euclid.Util#throwNYI()}.
     */
    @Test
    public final void testNYI() {
        try {
            Util.throwNYI();
            Assert.fail("should throw exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("NYI", "not yet implemented", e.getMessage());
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for {org.xmlcml.euclid.Util.BUG(java.lang.String)}.
     */
    @Test
    public final void testBUGString() {
        try {
            Util.BUG("foo");
            Assert.fail("should throw exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("bug", "BUG: (foo)should never throw: java.lang.RuntimeException", e.getMessage());
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for
     * {@link org.xmlcml.euclid.Util#getInputStreamFromResource(java.lang.String)}.
     */
    @Test
    public final void testGetInputStreamFromResource() {
        String filename = TestUtils.BASE_RESOURCE + U_S + "cml0.xml";
        InputStream is = null;
        try {
            is = Util.getInputStreamFromResource(filename);
        } catch (Exception e) {
            neverThrow(e);
        }
        try {
            Document doc = new Builder().build(is);
            Assert.assertNotNull("resource", doc);
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for
     * {@link org.xmlcml.euclid.Util#getResource(java.lang.String)}.
     */
    @Test
    public final void testGetResource() {
        String filename = TestUtils.BASE_RESOURCE + U_S + "cml0.xml";
        URL url = Util.getResource(filename);
        Assert.assertNotNull("url", url);
        Assert.assertTrue("target", url.toString().endsWith(
                "/org/xmlcml/cml/base/cml0.xml"));
    }

    /**
     * Test method for
     * {@link org.xmlcml.euclid.Util#getResourceFile(java.lang.String[])}.
     */
    @Test
    public final void testGetResourceFile() {
        String filename = TestUtils.BASE_RESOURCE + U_S + "cml0.xml";
        File file = null;
        try {
            file = Util.getResourceFile(filename);
        } catch (Exception e) {
            neverFail(e);
        }
        Assert.assertNotNull("url", file);
        Assert.assertTrue("target", file.toString().endsWith(
                "" + F_S + "org" + F_S
                        + "xmlcml" + F_S + "cml" + F_S + "base" 
                        + F_S + "cml0.xml"));
        Assert.assertTrue("file", file.exists());
    }

    /**
     * Test method for
     * {at link org.xmlcml.cml.base.CMLUtil#buildPath(java.lang.String...)}.
     */
    @Test
    public final void testBuildPath() {
        String s = Util.buildPath("foo", "bar", "plugh");
        Assert.assertEquals("build", "foo" + F_S + "bar" + F_S + "plugh", s);
    }

    
    /**
     * Test method for {@link org.xmlcml.euclid.Util#deleteFile(java.io.File, boolean)}.
     */
    @Test
    public final void testDeleteFile() {
        File dir = Util.getTEMP_DIRECTORY();
        try {
            Util.createFile(dir, "grot");
        } catch (IOException e) {
            Assert.fail("IOException "+e);
        }
        File file = new File(dir, "grot");
        Assert.assertTrue("exists", file.exists());
        boolean deleteDirectory = false;
        Util.deleteFile(file, deleteDirectory);
        Assert.assertFalse("exists", file.exists());
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#copyFile(java.io.File, java.io.File)}.
     */
    @Test
    public final void testCopyFile() {
        try {
            File dir = Util.getTEMP_DIRECTORY();
            File file = new File(dir, "grot.txt");
            FileWriter fw = new FileWriter(file);
            fw.write("this is a line\n");
            fw.write("and another\n");
            fw.close();
            File outFile = new File(dir, "grotOut.txt");
            Util.copyFile(file, outFile);
            Assert.assertTrue("exists", outFile.exists());
        } catch (IOException e) {
            Assert.fail("IOException "+e);
        }
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#dump(java.net.URL)}.
     */
    @Test
    public final void testDump() {
        try {
            File dir = Util.getTEMP_DIRECTORY();
            File file = new File(dir, "grot.txt");
            FileWriter fw = new FileWriter(file);
            fw.write("this is a line\n");
            fw.write("and another\n");
            fw.close();
            URL url = file.toURI().toURL();
            String s = Util.dump(url);
            String exp = "\n" +
            " 116 104 105 115  32 105 115  32  97  32   this is a \n"+
            " 108 105 110 101  10  97 110 100  32  97   line and a\n"+
            " 110 111 116 104 101 114  10   nother ";
            Assert.assertEquals("dump", exp, s);
        } catch (Exception e) {
            Assert.fail("IOException "+e);
        }
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#spaces(int)}.
     */
    @Test
    public final void testSpaces() {
        Assert.assertEquals("spaces", "     ", Util.spaces(5));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getSuffix(java.lang.String)}.
     */
    @Test
    public final void testGetSuffix() {
        Assert.assertEquals("suffix", "txt", Util.getSuffix("foo.bar.txt"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#truncateAndAddEllipsis(java.lang.String, int)}.
     */
    @Test
    public final void testTruncateAndAddEllipsis() {
        Assert.assertEquals("suffix", "qwert ... ", Util.truncateAndAddEllipsis("qwertyuiop", 5));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#deQuote(java.lang.String)}.
     */
    @Test
    public final void testDeQuote() {
        Assert.assertEquals("deQuote", "This is a string", 
                Util.deQuote("'This is a string'"));
        Assert.assertEquals("deQuote", "This is a string", 
                Util.deQuote("\"This is a string\""));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#rightTrim(java.lang.String)}.
     */
    @Test
    public final void testRightTrim() {
        Assert.assertEquals("deQuote", " This is a string", 
                Util.rightTrim(" This is a string "));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#leftTrim(java.lang.String)}.
     */
    @Test
    public final void testLeftTrim() {
        Assert.assertEquals("deQuote", "This is a string ", 
                Util.leftTrim(" This is a string "));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#indexOfBalancedBracket(char, java.lang.String)}.
     */
    @Test
    public final void testIndexOfBalancedBracket() {
        String s = "(foo(bar)junk)grot";
        Assert.assertEquals("balanced", 13, Util.indexOfBalancedBracket('(', s));
    }
    /**
     * Test method for {@link org.xmlcml.euclid.Util#getCommaSeparatedStrings(java.lang.String)}.
     */
    @Test
    public final void testGetCommaSeparatedStrings() {
        List<String> ss = Util.getCommaSeparatedStrings("aa, bb, \"cc dd\", ee ");
        Assert.assertEquals("list", 4, ss.size());
        Assert.assertEquals("s0", "aa", ss.get(0));
        Assert.assertEquals("s1", " bb", ss.get(1));
        Assert.assertEquals("s2", " \"cc dd\"", ss.get(2));
        Assert.assertEquals("s3", " ee", ss.get(3));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#createCommaSeparatedStrings(java.util.List)}.
     */
    @Test
    public final void testCreateCommaSeparatedStrings() {
        List<String> ss = new ArrayList<String>();
        ss.add("aa");
        ss.add("bb");
        ss.add("cc \"B\" dd");
        ss.add("ee");
        String s = Util.createCommaSeparatedStrings(ss);
        Assert.assertEquals("comma", "aa,bb,\"cc \"\"B\"\" dd\",ee", s);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#quoteConcatenate(java.lang.String[])}.
     */
    @Test
    public final void testQuoteConcatenate() {
        String[] ss = new String[4];
        ss[0] = "aa";
        ss[1] = "bb";
        ss[2] = "cc \"B\" dd";
        ss[3] = "ee";
        String s = Util.quoteConcatenate(ss);
        Assert.assertEquals("quote", "aa bb \"cc \"B\" dd\" ee", s);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#indexOf(java.lang.String, java.lang.String[], boolean)}.
     */
    @Test
    public final void testIndexOf() {
        String[] ss = new String[4];
        ss[0] = "aa";
        ss[1] = "bb";
        ss[2] = "cc \"B\" dd";
        ss[3] = "ee";
        boolean ignoreCase = false;
        Assert.assertEquals("index", 1, Util.indexOf("bb", ss, ignoreCase));
        Assert.assertEquals("index", -1, Util.indexOf("BB", ss, ignoreCase));
        ignoreCase = true;
        Assert.assertEquals("index", 1, Util.indexOf("BB", ss, ignoreCase));
        Assert.assertEquals("index", -1, Util.indexOf("XX", ss, ignoreCase));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#removeHTML(java.lang.String)}.
     */
    @Test
    public final void testRemoveHTML() {
        String s = "<p>This <i>is</i> a para</p>";
        String ss = Util.removeHTML(s);
        Assert.assertEquals("html", "This is a para", ss);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#warning(java.lang.String)}.
     */
    @Test
    public final void testWarning() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#message(java.lang.String)}.
     */
    @Test
    public final void testMessage() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#error(java.lang.String)}.
     */
    @Test
    public final void testError() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#BUG(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public final void testBUGStringThrowable() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#BUG(java.lang.Throwable)}.
     */
    @Test
    public final void testBUGThrowable() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getPWDName()}.
     */
    @Test
    public final void testGetPWDName() {
        // no useful method
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#substituteString(java.lang.String, java.lang.String, java.lang.String, int)}.
     */
    @Test
    public final void testSubstituteString() {
        String s = "AAA";
        String oldSubstring="A";
        String newSubstring="aa";
        String ss = Util.substituteString(s, oldSubstring, newSubstring, 2);
        Assert.assertEquals("substitute", "aaaaA", ss);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#substituteStrings(java.lang.String, java.lang.String[], java.lang.String[])}.
     */
    @Test
    public final void testSubstituteStrings() {
        String s = "AAABBBCCCAAADDDSS";
        String[] oldSubstring = new String[]{"AA", "CC", "D"};
        String[] newSubstring = new String[]{"aa", "cc", "d"};
        String ss = Util.substituteStrings(s, oldSubstring, newSubstring);
        Assert.assertEquals("substitute", "aaABBBccCaaAdddSS", ss);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#substituteDOSbyAscii(java.lang.String)}.
     */
    @Test
    public final void testSubstituteDOSbyAscii() {
    	String ss = Util.substituteDOSbyAscii(""+(char)161);
    	Assert.assertEquals("char", 237, (int)ss.charAt(0));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#substituteEquals(java.lang.String)}.
     */
    @Test
    public final void testSubstituteEquals() {
    	String ss = Util.substituteEquals("=20");
    	Assert.assertEquals("equals", S_SPACE, ss);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getIntFromHex(java.lang.String)}.
     */
    @Test
    public final void testGetIntFromHex() {
        Assert.assertEquals("hex", 2707, Util.getIntFromHex("A93"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#capitalise(java.lang.String)}.
     */
    @Test
    public final void testCapitalise() {
        Assert.assertEquals("capital", "This is fred", Util.capitalise("this is fred"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#toCamelCase(java.lang.String)}.
     */
    @Test
    public final void testToCamelCase() {
        Assert.assertEquals("capital", "thisIsFred", Util.toCamelCase("this is fred"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#readByteArray(java.lang.String)}.
     */
    @Test
    public final void testReadByteArrayString() {
//    	String filename;
//    	byte[] bb = Util.readByteArray(filename);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#readByteArray(java.io.DataInputStream)}.
     */
    @Test
    public final void testReadByteArrayDataInputStream() {
        // not yet tested
    }
    
    @Test
    public void testReplaceISOControlsByMnemonic() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("zero is [");
    	sb.append((char)0);
    	sb.append("]");
    	Assert.assertEquals("char 0", "zero is [NUL]", Util.replaceISOControlsByMnemonic(sb.toString()));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#stripISOControls(java.lang.String)}.
     */
    @Test
    public final void testStripISOControls() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("This is character: [");
    	sb.append((char)1);
    	sb.append("] one");
    	String s = Util.stripISOControls(sb.toString());
    	Assert.assertEquals("stripped char", "This is character: [] one", s);
    }

    @Test
    public void testTranslateToMnemonic() {
    	Assert.assertEquals("char 0", "NUL", Util.translateToMnemonic((char)0));
    	Assert.assertEquals("char 1", "SOH", Util.translateToMnemonic((char)1));
    	Assert.assertEquals("char 31", "US", Util.translateToMnemonic((char)31));
    	Assert.assertEquals("char 32", "", Util.translateToMnemonic((char)32));
    	Assert.assertEquals("char 127", "DEL", Util.translateToMnemonic((char)127));
    }
    
    /**
     * 
     */
    @Test
    public final void testConvertUTF8ToLatin1() {
    	char c1;
    	char c2;
    	char c;
    	c1 = 20;
    	c2 = 20;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("no-op", (char)0, c);
    	c1 = 194;
    	c2 = 127;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("no-op", (char)0, c);
    	c1 = 194;
    	c2 = 128;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)128, c);
    	c1 = 194;
    	c2 = 191;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)191, c);
    	c1 = 194;
    	c2 = 192;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)0, c);
    	c1 = 195;
    	c2 = 127;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)0, c);
    	c1 = 195;
    	c2 = 128;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)192, c);
    	c1 = 195;
    	c2 = 191;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)255, c);
    	c1 = 195;
    	c2 = 192;
    	c = Util.convertUTF8ToLatin1(c1, c2);
    	Assert.assertEquals("nbs", (char)0, c);
    	
    }

    /**
     * 
     */
    @Test
    public final void testConvertLatin1ToUTF8() {
    	char c;
    	char [] cc;
    	c = 127;
    	cc = Util.convertLatin1ToUTF8(c);
    	Assert.assertEquals("cc", null, cc);
    	c = 128;
    	cc = Util.convertLatin1ToUTF8(c);
    	Assert.assertEquals("cc", 2, cc.length);
    	Assert.assertEquals("cc", (char)194, cc[0]);
    	Assert.assertEquals("cc", (char)128, cc[1]);
    	c = 191;
    	cc = Util.convertLatin1ToUTF8(c);
    	Assert.assertEquals("cc", 2, cc.length);
    	Assert.assertEquals("cc", (char)194, cc[0]);
    	Assert.assertEquals("cc", (char)191, cc[1]);
    	c = 192;
    	cc = Util.convertLatin1ToUTF8(c);
    	Assert.assertEquals("cc", 2, cc.length);
    	Assert.assertEquals("cc", (char)195, cc[0]);
    	Assert.assertEquals("cc", (char)128, cc[1]);
    	c = 255;
    	cc = Util.convertLatin1ToUTF8(c);
    	Assert.assertEquals("cc", 2, cc.length);
    	Assert.assertEquals("cc", (char)195, cc[0]);
    	Assert.assertEquals("cc", (char)191, cc[1]);
    	
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#normaliseWhitespace(java.lang.String)}.
     */
    @Test
    public final void testNormaliseWhitespace() {
        Assert.assertEquals("capital", "this is fred", 
                Util.normaliseWhitespace("this   is      fred"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#stripNewlines(byte[])}.
     */
    @Test
    public final void testStripNewlines() {
        Assert.assertEquals("capital", "this is fred", 
                Util.normaliseWhitespace("this\nis\nfred"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getFileOutputStream(java.lang.String)}.
     */
    @Test
    public final void testGetFileOutputStream() {
        // not yet implemented
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#outputFloat(int, int, double)}.
     */
    @Test
    public final void testOutputFloat() {
//        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#outputNumber(int, int, double)}.
     */
    @Test
    public final void testOutputNumber() {
//        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#invert(java.util.Hashtable)}.
     */
    @Test
    public final void testInvert() {
//        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(double[], java.lang.String)}.
     */
    @Test
    public final void testConcatenateDoubleArrayString() {
    	double[] ss = new double[]{1.2, 3.4, 5.6};
    	String s = Util.concatenate(ss, S_SPACE);
    	Assert.assertEquals("Concat", "1.2 3.4 5.6", s);
    	s = Util.concatenate(ss, S_COMMA);
    	Assert.assertEquals("Concat", "1.2,3.4,5.6", s);
    }
    
	@Test
	public void testConcatenateInfinityAndBeyond() {
		double[] ss = new double[] { Double.POSITIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NaN };
		Assert.assertEquals("Concat infinities according to XSD",
				"INF -INF NaN", Util.concatenate(ss, S_SPACE));
	}

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(double[][], java.lang.String)}.
     */
    @Test
    public final void testConcatenateDoubleArrayArrayString() {
    	double[][] ss = new double[][]{
    			new double[]{1.2, 3.4, 5.6}, 
    			new double[]{1.1, 2.2, 3.3, 4.4}
    			};
    	String s = Util.concatenate(ss, S_SPACE);
    	Assert.assertEquals("Concat", "1.2 3.4 5.6 1.1 2.2 3.3 4.4", s);
    	s = Util.concatenate(ss, S_COMMA);
    	Assert.assertEquals("Concat", "1.2,3.4,5.6,1.1,2.2,3.3,4.4", s);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#splitToIntArray(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testSplitToIntArray() {
    	int[] ii = Util.splitToIntArray("1 2 3 4", S_SPACE);
    	IntTest.assertEquals("int split", new int[]{1,2,3,4}, ii);
    	ii = Util.splitToIntArray("1,2,3,4", S_COMMA);
    	IntTest.assertEquals("int split", new int[]{1,2,3,4}, ii);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#splitToDoubleArray(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testSplitToDoubleArray() {
    	double[] dd = Util.splitToDoubleArray("1.1 2.2 3.3 4.4", S_SPACE);
    	DoubleTestBase.assertEquals("double split", new double[]{1.1,2.2,3.3,4.4}, dd, EPS);
    	dd = Util.splitToDoubleArray("1.1,2.2,3.3,4.4", S_COMMA);
    	DoubleTestBase.assertEquals("double split", new double[]{1.1,2.2,3.3,4.4}, dd, EPS);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(int[], java.lang.String)}.
     */
    @Test
    public final void testConcatenateIntArrayString() {
    	int[] ii = new int[]{1,2,3,4};
    	String s = Util.concatenate(ii, S_SPACE);
    	Assert.assertEquals("int split", "1 2 3 4", s);
    	ii = new int[]{1,2,3,4};
    	s = Util.concatenate(ii, S_COMMA);
    	Assert.assertEquals("int split", "1,2,3,4", s);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(java.lang.String[], java.lang.String)}.
     */
    @Test
    public final void testConcatenateStringArrayString() {
    	String[] ii = new String[]{"a", "b", "c", "d"};
    	String s = Util.concatenate(ii, S_SPACE);
    	Assert.assertEquals("int split", "a b c d", s);
    	ii = new String[]{"a", "b", "c", "d"};
    	s = Util.concatenate(ii, S_COMMA);
    	Assert.assertEquals("int split", "a,b,c,d", s);
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#containsString(java.lang.String[], java.lang.String)}.
     */
    @Test
    public final void testContainsString() {
    	Assert.assertTrue("contains", Util.containsString(new String[] {"aa", "bb", "cc"}, "bb"));
    	Assert.assertFalse("contains", Util.containsString(new String[] {"aa", "bb", "cc"}, "xx"));
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getPrime(int)}.
     */
    @Test
    public final void testGetPrime() {
        int i = Util.getPrime(0);
        Assert.assertEquals("0", 2, i);
        i = Util.getPrime(1);
        Assert.assertEquals("1", 3, i);
        i = Util.getPrime(4);
        Assert.assertEquals("4", 11, i);
        i = Util.getPrime(10);
        Assert.assertEquals("10", 31, i);
        i = Util.getPrime(100);
        Assert.assertEquals("100", 547, i);
        i = Util.getPrime(1000);
        Assert.assertEquals("1000", 7927, i);
        i = Util.getPrime(100);
        Assert.assertEquals("100", 547, i);
    }
    
    @Test
    public final void testSortByEmbeddedInteger() {
    	String[] ss = {
    			"a123",
    			"b213",
    			"aa1",
    			"ac9",
    			"ax22",
    	};
    	List<String> ssList = new ArrayList<String>();
    	for (String s : ss) {
    		ssList.add(s);
    	}
    	Util.sortByEmbeddedInteger(ssList);
    	Assert.assertEquals("0", "aa1", ssList.get(0));
    	Assert.assertEquals("1", "ac9", ssList.get(1));
    	Assert.assertEquals("2", "ax22", ssList.get(2));
    	Assert.assertEquals("3", "a123", ssList.get(3));
    	Assert.assertEquals("4", "b213", ssList.get(4));
	}

}
