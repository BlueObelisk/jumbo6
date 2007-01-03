/**
 * 
 */
package org.xmlcml.euclid.test;

import static org.junit.Assert.fail;

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
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.BaseTest;
import org.xmlcml.euclid.EuclidConstants;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class UtilTest extends EuclidTest implements EuclidConstants {

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
        StringTest
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
        StringTest.assertEquals("array", new String[] { "a", "c" }, array1);
    }

    /**
     * Test method for 'org.xmlcml.cml.base.CMLUtil.createFile(File, String)'
     */
    @Test
    public final void testCreateFile() {
        File dir = null;
        try {
            dir = Util.getResourceFile(BaseTest.BASE_RESOURCE);
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
            Assert.assertEquals("bug", "BUG: (foo)should never throw", e
                    .getMessage());
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
            Assert.assertEquals("bug", "BUG: should never throw", e
                    .getMessage());
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
            Assert.assertEquals("bug", "BUG: (foo)should never throw", e
                    .getMessage());
        } catch (Exception e) {
            neverThrow(e);
        }
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getInputStreamFromResource(java.lang.String)}.
     */
    @Test
    public final void testGetInputStreamFromResource() {
        String filename = BaseTest.BASE_RESOURCE + U_S + "cml0.xml";
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
     * {@link org.xmlcml.cml.base.CMLUtil#getResource(java.lang.String)}.
     */
    @Test
    public final void testGetResource() {
        String filename = BaseTest.BASE_RESOURCE + U_S + "cml0.xml";
        URL url = Util.getResource(filename);
        Assert.assertNotNull("url", url);
        Assert.assertTrue("target", url.toString().endsWith(
                "/target/classes/org/xmlcml/cml/base/cml0.xml"));
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#getResourceFile(java.lang.String[])}.
     */
    @Test
    public final void testGetResourceFile() {
        String filename = BaseTest.BASE_RESOURCE + U_S + "cml0.xml";
        File file = null;
        try {
            file = Util.getResourceFile(filename);
        } catch (Exception e) {
            neverFail(e);
        }
        Assert.assertNotNull("url", file);
        Assert.assertTrue("target", file.toString().endsWith(
                "" + F_S + "target" + F_S + "classes" + F_S + "org" + F_S
                        + "xmlcml" + F_S + "cml" + F_S + "base" 
                        + F_S + "cml0.xml"));
        Assert.assertTrue("file", file.exists());
    }

    /**
     * Test method for
     * {@link org.xmlcml.cml.base.CMLUtil#buildPath(java.lang.String[])}.
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
            URL url = file.toURL();
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
    @Ignore
    public final void testSubstituteDOSbyAscii() {
        // not yet tested
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#substituteEquals(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testSubstituteEquals() {
        // not yet tested
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
    @Ignore
    public final void testReadByteArrayString() {
        // not yet tested
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#readByteArray(java.io.DataInputStream)}.
     */
    @Test
    @Ignore
    public final void testReadByteArrayDataInputStream() {
        // not yet tested
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#stripISOControls(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testStripISOControls() {
        // not yet tested
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
     * Test method for {@link org.xmlcml.euclid.Util#makeDirectory(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testMakeDirectory() {
        // not yet implemented
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#makeAbsoluteURL(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testMakeAbsoluteURL() {
        // not yet implemented
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#getFileOutputStream(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testGetFileOutputStream() {
        // not yet implemented
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#outputInteger(int, int)}.
     */
    @Test
    @Ignore
    public final void testOutputInteger() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#outputFloat(int, int, double)}.
     */
    @Test
    @Ignore
    public final void testOutputFloat() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#outputNumber(int, int, double)}.
     */
    @Test
    @Ignore
    public final void testOutputNumber() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#invert(java.util.Hashtable)}.
     */
    @Test
    @Ignore
    public final void testInvert() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#check(double[], int)}.
     */
    @Test
    @Ignore
    public final void testCheckDoubleArrayInt() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#check(org.xmlcml.euclid.RealArray, int)}.
     */
    @Test
    @Ignore
    public final void testCheckRealArrayInt() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#check(int, int, int)}.
     */
    @Test
    @Ignore
    public final void testCheckIntIntInt() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#checkNotNull(org.xmlcml.euclid.Transform3)}.
     */
    @Test
    @Ignore
    public final void testCheckNotNull() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#isEqual(double[], double[], double)}.
     */
    @Test
    @Ignore
    public final void testIsEqualDoubleArrayDoubleArrayDouble() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#isEqual(int[], int[], int)}.
     */
    @Test
    @Ignore
    public final void testIsEqualIntArrayIntArrayInt() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(boolean[], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testConcatenateBooleanArrayString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(double[], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testConcatenateDoubleArrayString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(double[][], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testConcatenateDoubleArrayArrayString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#splitToIntArray(java.lang.String, java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testSplitToIntArray() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#splitToDoubleArray(java.lang.String, java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testSplitToDoubleArray() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(int[], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testConcatenateIntArrayString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#concatenate(java.lang.String[], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testConcatenateStringArrayString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#containsString(java.lang.String[], java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testContainsString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#format(double, int)}.
     */
    @Test
    @Ignore
    public final void testFormat() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#trim(double)}.
     */
    @Test
    @Ignore
    public final void testTrim() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.xmlcml.euclid.Util#createList(java.lang.String[])}.
     */
    @Test
    @Ignore
    public final void testCreateList() {
        fail("Not yet implemented"); // TODO
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

}
