package org.xmlcml.euclid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * A number of miscellaneous tools. Originally devised for jumbo.sgml, now
 * rewritten for jumbo.xml. Use these at your peril - some will be phased out
 * 
 * @author (C) P. Murray-Rust, 1998
 * @author 20 August 2003
 */
public class Util implements EuclidConstants {
	
    /** messages */
    public enum Message {
        /** not yet implemented */
        NYI("not yet implemented"),
        ;
        /** value*/
        public String value;
        private Message(String v) {
            value = v;
        }
    }
    
    final static Logger logger = Logger.getLogger(Util.class.getName());
    
    private final static File TEMP_DIRECTORY = new File("target"
            + File.separator + "test-outputs");

    /**
     * get temporary directory - mainly for testing methods with outputs. calls
     * mkdirs() if does not exist
     * 
     * @return temporary directory.
     */
    public static File getTEMP_DIRECTORY() {
        if (!TEMP_DIRECTORY.exists()) {
            boolean ok = TEMP_DIRECTORY.mkdirs();
            if (!ok) {
                throw new RuntimeException("Cannot create temporary directory : "
                        + TEMP_DIRECTORY.getAbsolutePath());
            }
        }
        return TEMP_DIRECTORY;
    }

    /**
     * get class-specific temporary directory - mainly for testing methods with ouputs. calls
     * mkdirs() if does not exist
     * @param classx
     * @return temporary directory.
     */
    public static File getTestOutputDirectory(Class classx) {
        File tempDir = getTEMP_DIRECTORY();
        String dirs = classx.getName().replace(S_PERIOD, File.separator);
        File testDir = new File(tempDir, dirs);
        if (!testDir.exists()) {
            boolean ok = testDir.mkdirs();
            if (!ok) {
                throw new RuntimeException("Cannot create temporary class directory : "
                        + testDir.getAbsolutePath());
            }
        }
        return testDir;
        
    }

    /**
     * convenience method to extend array of Strings.
     * 
     * @param array
     *            to extend
     * @param s
     *            element to add
     * @return extended array
     */
    public final static String[] addElementToStringArray(String[] array, String s) {
        int l = array.length;
        String[] array1 = new String[l + 1];
        for (int i = 0; i < l; i++) {
            array1[i] = array[i];
        }
        array1[l] = s;
        return array1;
    }

    /**
     * convenience method to remove element from array of Strings.
     * 
     * Removes ALL occurrences of string
     * 
     * @param array
     *            to edit
     * @param s
     *            element to remove
     * @return depleted array
     */
    public final static String[] removeElementFromStringArray(String[] array, String s) {
        List<String> sList = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            if (!array[i].equals(s)) {
                sList.add(array[i]);
            }
        }
        return (String[]) sList.toArray(new String[0]);
    }

    /** traps a bug.
     * use for programming errors where this could can "never be reached"
     * concatenates msg with "BUG" and throws {@link RuntimeException}
     * @param msg
     * @param e
     */
    public static void BUG(String msg, Exception e) {
        msg = (msg == null || msg.trim().length() == 0) ? S_EMPTY : S_LBRAK+msg+S_RBRAK;
        throw new RuntimeException("BUG: "+msg+"should never throw", e);
    }
    
    /** traps a bug.
     * empty message.
     * @see #BUG(String, Throwable)
     * @param e
     */
    public static void BUG(Exception e) {
        BUG(S_EMPTY, e);
    }
    
    /** convenience method for "not yet implemented".
     * deliberately deprecated so that it requires deprecated on
     * all modules containing NYI
     * @deprecated
     * @throws RuntimeException
     */
    public static void throwNYI() {
        throw new RuntimeException(Message.NYI.value);
    }
    /** traps a bug.
     * @see #BUG(String, Throwable)
     * @param msg
     */
    public static void BUG(String msg) {
        BUG(msg, new RuntimeException());
    }
    
    /**
     * convenience method to get input stream from resource. the resource is
     * packaged with the classes for distribution. typical filename is
     * org/xmlcml/molutil/elementdata.xml for file elementdata.xml in class
     * hierarchy org.xmlcml.molutil
     * 
     * @param filename
     *            relative to current class hierarchy.
     * @return input stream
     * @throws IOException
     */
    public static InputStream getInputStreamFromResource(String filename)
            throws IOException {
        return getResource(filename).openStream();
    }

    /**
     * creates directories and files if they don't exist. 
     * creates dir/filename
     * 
     * @param dir
     * @param filename
     * @throws IOException
     */
    public static void createFile(File dir, String filename) throws IOException {
        File file = new File(dir + File.separator + filename);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok) {
                throw new IOException("cannot make dictories: "+dir+S_SPACE+filename);
            }
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /** creates resource from filename.
     * uses ClassLoader.getResource()
     * @param filename name relative to classroot
     * @return url or null
     */
    public static URL getResource(String filename) {
        URL url = null;
        if (filename != null) {
            ClassLoader l = Thread.currentThread().getContextClassLoader();
            url = l.getResource(filename);
            if (url == null) {
                throw new RuntimeException("No resource with name " + filename);
            }
        }
        return url;
    }
    
    /** gets file from build path components.
     * pm286 is not quite sure how it does this...
     * @author ojd20@cam.ac.uk
     * @param path
     * @return file or null
     * @throws URISyntaxException
     */
    public static File getResourceFile(String... path) throws URISyntaxException {
        File f = new File(Util.class.getClassLoader()
                .getResource(buildPath(path)).toURI());
        return f;
    }

    /** gets build path from its components.
     * 
     * @param parts
     * @return build path concatenated with File.separatorChar
     */
    public static String buildPath(String... parts) {
        StringBuilder sb = new StringBuilder(parts.length * 20);
        for (String part : parts) {
            sb.append(part).append(File.separatorChar);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /** simple routes to output.
     * 
     * @param s
     */
    public static void output(String s) {
        System.out.println(s);
    }

    
    /**
     * delete a file If directory==true then file will be recursively deleted
     * 
     * @param file
     *            Description of the Parameter
     * @param deleteDirectory
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static boolean deleteFile(File file, boolean deleteDirectory) {
        if (file.exists()) {
            if (file.isDirectory() && deleteDirectory) {
                String[] filenames = file.list();
                for (int i = 0; i < filenames.length; i++) {
                    File childFile = new File(file.toString() + File.separator
                            + filenames[i]);
                    deleteFile(childFile, deleteDirectory);
                }
            }
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * copy one file to another (I suspect there is a better way
     * 
     * @param inFile
     *            Description of the Parameter
     * @param outFile
     *            Description of the Parameter
     * @exception FileNotFoundException
     *                Description of the Exception
     * @exception IOException
     *                Description of the Exception
     */
    public static void copyFile(File inFile, File outFile)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                inFile));
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(outFile));
        byte[] buffer = new byte[10000];
        while (true) {
            int b = bis.read(buffer);
            if (b == -1) {
                break;
            }
            bos.write(buffer, 0, b);
        }
        bis.close();
        bos.close();
    }

    /**
     * reads a stream from url and outputs it as integer values of the
     * characters and as strings. Emulates UNIX od().
     * 
     * @param url
     *            Description of the Parameter
     * @return String tabular version of input (in 10-column chunks)
     * @exception Exception
     *                Description of the Exception
     */
    public static String dump(URL url) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(url
                .openStream()));
        int count = 0;
        StringBuffer sb = new StringBuffer();
        String s0 = "\n";
        String s1 = S_EMPTY;
        while (true) {
            int i = br.read();
            if (i == -1) {
                break;
            }
            String s = "   " + i;
            while (s.length() > 4) {
                s = s.substring(1);
            }
            s0 += s;
            if (i >= 32 && i < 128) {
                s1 += (char) i;
            } else {
                s1 += S_SPACE;
            }
            if (++count % 10 == 0) {
                sb.append(s0 + "   " + s1);
                s1 = S_EMPTY;
                s0 = "\n";
            }
        }
        if (count != 0) {
            sb.append(s0 + "   " + s1);
        }
        return sb.toString();
    }

    /**
     * make a String of a given number of spaces
     * 
     * @param nspace
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String spaces(int nspace) {
        if (nspace <= 0) {
            return S_EMPTY;
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nspace; i++) {
                sb.append(S_SPACE);
            }
            return sb.toString();
        }
    }

    /**
     * 
     * gets suffix from filename
     * 
     * 
     * 
     * @param filename
     *            Description of the Parameter
     * 
     * @return The suffix value
     * 
     */
    public static String getSuffix(String filename) {
        int idx = filename.lastIndexOf(Util.S_PERIOD);
        if (idx == -1) {
            return null;
        }
        return filename.substring(idx + 1, filename.length());
    }

    /**
     * return the first n characters of a string and add ellipses if truncated
     * 
     * @param s
     *            Description of the Parameter
     * @param maxlength
     *            Description of the Parameter
     * @return String the (possibly) truncated string
     */
    public static String truncateAndAddEllipsis(String s, int maxlength) {
        if (s == null) {
            return null;
        }
        int l = s.length();
        return (l <= maxlength) ? s : s.substring(0, maxlength) + " ... ";
    }

    /**
     * remove balanced quotes from ends of (trimmed) string, else no action
     * 
     * @param s Description of the Parameter
     * @return Description of the Return Value
     */
    public static String deQuote(String s) {
        if (s == null) {
            return null;
        }
        String ss = s.trim();
        if (ss.equals(S_EMPTY)) {
            return ss;
        }
        char c = ss.charAt(0);
        if (c == '"' || c == '\'') {
            int l = ss.length();
            if (ss.charAt(l - 1) == c) {
                return ss.substring(1, l - 1);
            }
        }
        return s;
    }

    /**
     * remove trailing blanks
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String rightTrim(String s) {
        if (s == null) {
            return null;
        }
        if (s.trim().equals(S_EMPTY)) {
            return S_EMPTY;
        }
        int l = s.length();
        while (l >= 0) {
            if (s.charAt(--l) != ' ') {
                l++;
                break;
            }
        }
        return s.substring(0, l);
    }

    /**
     * remove leading blanks
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String leftTrim(String s) {
        if (s == null) {
            return null;
        }
        if (s.trim().equals(S_EMPTY)) {
            return S_EMPTY;
        }
        int l = s.length();
        for (int i = 0; i < l; i++) {
            if (s.charAt(i) != ' ') {
                return s.substring(i);
            }
        }
        return s;
    }

    /**
     * return index of balanced bracket. 
     * String MUST start with a generic LH bracket (e.g. '{', '<', '(' '[')
     * @param lbrack starting character
     * @param s string to search
     * @return index of bracket or -1 if none
     */
    public static int indexOfBalancedBracket(char lbrack, String s) {
        if (s == null) {
            return -1;
        }
        if (s.charAt(0) != lbrack) {
            return -1;
        }
        char rbrack = ' ';
        if (lbrack == '(') {
            rbrack = ')';
        } else if (lbrack == '<') {
            rbrack = '>';
        } else if (lbrack == '[') {
            rbrack = ']';
        } else if (lbrack == '{') {
            rbrack = '}';
        }
        int l = s.length();
        int i = 0;
        int level = 0;
        while (i < l) {
            if (s.charAt(i) == lbrack) {
                level++;
            } else if (s.charAt(i) == rbrack) {
                level--;
                if (level == 0) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    /**
     * parse comma-separated Strings Note fields can be S_EMPTY (as in ,,,) and
     * fields can be quoted "...". If so, embedded quotes are represented as S_EMPTY,
     * for example A," this is a S_EMPTYBS_EMPTY character",C. An unbalanced quote returns
     * a mess
     * 
     * @param s
     *            Description of the Parameter
     * @return List the vector of Strings - any error returns null
     * @exception RuntimeException missing quote
     */
    public static List<String> getCommaSeparatedStrings(String s)
            throws RuntimeException {
        if (s == null) {
            return null;
        }
        String s0 = s;
        s = s.trim();
        List<String> v = new ArrayList<String>();
        while (!s.equals(S_EMPTY)) {
            if (s.startsWith(S_QUOT)) {
                String temp = S_EMPTY;
                s = s.substring(1);
                while (true) {
                    int idx = s.indexOf(S_QUOT);
                    if (idx == -1) {
                        throw new RuntimeException("Missing Quote:" + s0 + S_COLON);
                    }
                    int idx2 = s.indexOf(S_QUOT + S_QUOT);
                    // next quote is actually S_EMPTY
                    if (idx2 == idx) {
                        temp += s.substring(0, idx) + S_QUOT;
                        s = s.substring(idx + 2);
                        // single quote
                    } else {
                        temp += s.substring(0, idx);
                        s = s.substring(idx + 1);
                        break;
                    }
                }
                v.add(temp);
                if (s.startsWith(S_COMMA)) {
                    s = s.substring(1);
                } else if (s.equals(S_EMPTY)) {
                } else {
                    throw new RuntimeException("Unbalanced Quotes:" + s0 + S_COLON);
                }
            } else {
                int idx = s.indexOf(S_COMMA);
                // end?
                if (idx == -1) {
                    v.add(s);
                    break;
                } else {
                    // another comma
                    String temp = s.substring(0, idx);
                    v.add(temp);
                    s = s.substring(idx + 1);
                    if (s.equals(S_EMPTY)) {
                        v.add(s);
                        break;
                    }
                }
            }
        }
        return v;
    }

    /**
     * create comma-separated Strings fields include a comma or a " they are
     * wrapped with quotes ("). Note fields can be S_EMPTY (as in ,,,) and fields can
     * be quoted "...". If so, embedded quotes are represented as S_EMPTY, for
     * example A," this is a S_EMPTYBS_EMPTY character",C.
     * 
     * @param v
     *            vector of strings to be concatenated (null returns null)
     * @return String the concatenated string - any error returns null
     */
    public static String createCommaSeparatedStrings(List<String> v) {
        if (v == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            String s = v.get(i).toString();
            s = Util.substituteStrings(s, new String[] { S_QUOT },
                    new String[] { S_QUOT + S_QUOT });
            // wrap in quotes to escape comma or other quotes
            if (s.indexOf(S_COMMA) != -1 || s.indexOf(S_QUOT) != -1) {
                s = S_QUOT + s + S_QUOT;
            }
            if (i > 0) {
                sb.append(S_COMMA);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * concatenate strings into quote-separated string
     * 
     * @param s
     *            Description of the Parameter
     * @return String concatenated string
     */
    public static String quoteConcatenate(String[] s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length; i++) {
            if (i > 0) {
                sb.append(S_SPACE);
            }
            boolean quote = false;
            if (s[i].indexOf(S_SPACE) != -1) {
                sb.append(S_QUOT);
                quote = true;
            }
            sb.append(s[i]);
            if (quote) {
                sb.append(S_QUOT);
            }
        }
        return sb.toString();
    }

    /**
     * get the index of a String in an array
     * 
     * @param string
     *            Description of the Parameter
     * @param strings
     *            Description of the Parameter
     * @param ignoreCase
     *            ignore case
     * @return index of string else -1 if not found
     */
    public static int indexOf(String string, String[] strings,
            boolean ignoreCase) {
        if (string == null || strings == null) {
            return -1;
        }
        for (int i = 0; i < strings.length; i++) {
            if (ignoreCase) {
                if (string.equalsIgnoreCase(strings[i])) {
                    return i;
                }
            } else {
                if (string.equals(strings[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * remove balanced (well-formed) markup from a string. Crude (that is not
     * fully XML-compliant);</BR> Example: "This is &lt;A
     * HREF="foo"&gt;bar&lt;/A&gt; and &lt;/BR&gt; a break" goes to "This is bar
     * and a break"
     * 
     * @param s Description of the Parameter
     * @return Description of the Return Value
     */
    public static String removeHTML(String s) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int idx = s.indexOf("<");
            if (idx == -1) {
                sb.append(s);
                break;
            } else {
                sb.append(s.substring(0, idx));
                s = s.substring(idx);
                idx = s.indexOf('>');
                if (idx == -1) {
                    throw new RuntimeException("missing >");
                } else {
                    s = s.substring(idx+1);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 
     * Warning message - nothing fancy at present
     * 
     * 
     * 
     * @param s
     *            Description of the Parameter
     * 
     */
    public static void warning(String s) {
        logger.info("WARNING: " + s);
    }

    /**
     * 
     * message - nothing fancy at present
     * 
     * 
     * 
     * @param s
     *            Description of the Parameter
     * 
     */
    public static void message(String s) {
        logger.info(s);
    }

    // static jumbo.xml.gui.XText errorText;
    /**
     * 
     * Error message - nothing fancy at present. Display in Text frame
     * 
     * 
     * 
     * @param s
     *            Description of the Parameter
     * 
     */
    public static void error(String s) {
        // if (errorText == null) {
        // errorText = new jumbo.xml.gui.XText();
        // errorText.displayInFrame();
        // }
        logger.info("ERROR: " + s);
        // errorText.addText(s);
    }

    /**
     * 
     * record that we have hit a program bug!!!
     * 
     * 
     * 
     * @param s
     *            Description of the Parameter
     * 
     */
//    public static void bug(String s) {
//        bug(new Exception(s));
//    }

    /** traps a bug.
     * use for programming errors where this could can "never be reached"
     * concatenates msg with "BUG" and throws {@link RuntimeException}
     * @param msg
     * @param t
     */
    public static void BUG(String msg, Throwable t) {
        msg = (msg == null || msg.trim().length() == 0) ? S_EMPTY : S_LBRAK+msg+S_RBRAK;
        throw new RuntimeException("BUG: "+msg+"should never throw", t);
    }
    
    /** traps a bug.
     * empty message.
     * @see #BUG(String, Throwable)
     * @param t
     */
    public static void BUG(Throwable t) {
        BUG(S_EMPTY, t);
    }
    
    /** file separator.*/
    final static String FS = System.getProperty("file.separator");

    /**
     * 
     * create new file, including making directory if required This seems to be
     * 
     * a mess - f.createNewFile() doesn't seem to work A directory should have
     * 
     * a trailing file.separator
     * 
     * 
     * 
     * @param fileName
     *            Description of the Parameter
     * 
     * @return Description of the Return Value
     * 
     * @exception IOException
     *                Description of the Exception
     * 
     */
//    public static File createNewFile(String fileName) throws IOException {
//        File f = null;
//        String path = null;
//        int idx = fileName.lastIndexOf(FS);
//        if (idx != -1) {
//            path = fileName.substring(0, idx);
//            // fileN = fileName.substring(idx+1);
//        }
//        // try {
//        if (path != null) {
//            f = new File(path);
//            f.mkdirs();
//        }
//        if (!fileName.endsWith(FS)) {
//            f = new File(fileName);
//        }
//        // } catch (IOException e) {
//        // logger.info("Failed to create: "+fileName+S_LBRAK+e+S_RBRAK);
//        // }
//        return f;
//    }

    /**
     * get current directory
     * @return The pWDName value
     */
    public static String getPWDName() {
        File f = new File(S_PERIOD);
        return new File(f.getAbsolutePath()).getParent();
    }

    /**
     * create new file, including making directory if required This seems to be
     * a mess - f.createNewFile() doesn't seem to work A directory should have
     * a trailing file.separator
     * 
     * @param fileName
     * @return file
     * 
     * @exception IOException
     */
    public static File createNewFile(String fileName) throws IOException {
        File f = null;
        String path = null;
        int idx = fileName.lastIndexOf(FS);
        if (idx != -1) {
            path = fileName.substring(0, idx);
        }
        if (path != null) {
            f = new File(path);
            f.mkdirs();
        }
        if (!fileName.endsWith(FS)) {
            f = new File(fileName);
        }
        return f;
    }

    /**
     * 
     * make substitutions in a string. If oldSubtrings = "A" and newSubstrings
     *  = "aa" then count occurrences of "A" in s are replaced with "aa", etc.
     * 
     * "AAA" count=2 would be replaced by "aaaaA"
     * @param s
     * @param oldSubstring
     * @param newSubstring
     * @param count
     * @return new string
     * 
     */
    public static String substituteString(String s, String oldSubstring,
            String newSubstring, int count) {
        if (count <= 0) {
            count = Integer.MAX_VALUE;
        }
        StringBuffer sb = new StringBuffer();
        int lo = oldSubstring.length();
        for (int i = 0; i < count; i++) {
            int idx = s.indexOf(oldSubstring);
            if (idx == -1) {
                break;
            }
            sb.append(s.substring(0, idx));
            sb.append(newSubstring);
            s = s.substring(idx + lo);
        }
        sb.append(s);
        return sb.toString();
    }

    /**
     * make substitutions in a string.
     *  If oldSubtrings = {"A", "BB", "C"} and
     * newSubstrings = {"aa", "b", "zz"} then every occurrence of "A" in s is
     * 
     * replaced with "aa", etc. "BBB" would be replaced by "bB"
     * @param s
     * @param oldSubstrings
     * @param newSubstrings
     * 
     * @return Description of the Return Value
     * @throws RuntimeException
     */
    public static String substituteStrings(String s, String[] oldSubstrings,
            String[] newSubstrings) {
        int ol = oldSubstrings.length;
        int nl = newSubstrings.length;
        if (ol != nl) {
            throw new RuntimeException(
                "Util.substituteStrings  arguments of different lengths: "+ol+S_SLASH+nl);
        }
        for (int i = 0; i < ol; i++) {
            String oldS = oldSubstrings[i];
            String newS = newSubstrings[i];
            int lo = oldS.length();
            if (s.indexOf(oldS) == -1) {
                continue;
            }
            String ss = S_EMPTY;
            while (true) {
                int idx = s.indexOf(oldS);
                if (idx == -1) {
                    ss += s;
                    break;
                }
                ss += s.substring(0, idx) + newS;
                s = s.substring(idx + lo);
            }
            s = ss;
        }
        return s;
    }

    /**
     * 
     * substitute characters with =Hex values. Thus "=2E" is translated to
     * 
     * char(46); A trailing EQUALS (continuation line is not affected, nor is
     * 
     * any non-hex value
     * 
     * 
     * 
     */
    static String[] dosEquivalents = { S_EMPTY + (char) 12,
    // ??
            S_EMPTY + (char) 127,
            // ??
            S_EMPTY + (char) 128,
            // Ccedil
            S_EMPTY + (char) 129,
            // uuml
            S_EMPTY + (char) 130,
            // eacute
            S_EMPTY + (char) 131,
            // acirc
            S_EMPTY + (char) 132,
            // auml
            S_EMPTY + (char) 133,
            // agrave
            S_EMPTY + (char) 134,
            // aring
            S_EMPTY + (char) 135,
            // ccedil
            S_EMPTY + (char) 136,
            // ecirc
            S_EMPTY + (char) 137,
            // euml
            S_EMPTY + (char) 138,
            // egrave
            S_EMPTY + (char) 139,
            // iuml
            S_EMPTY + (char) 140,
            // icirc
            S_EMPTY + (char) 141,
            // igrave
            S_EMPTY + (char) 142,
            // Auml
            S_EMPTY + (char) 143,
            // Aring
            S_EMPTY + (char) 144,
            // Eacute
            S_EMPTY + (char) 145,
            // aelig
            S_EMPTY + (char) 146,
            // ff?
            S_EMPTY + (char) 147,
            // ocirc
            S_EMPTY + (char) 148,
            // ouml
            S_EMPTY + (char) 149,
            // ograve
            S_EMPTY + (char) 150,
            // ucirc
            S_EMPTY + (char) 151,
            // ugrave
            S_EMPTY + (char) 152,
            // yuml
            S_EMPTY + (char) 153,
            // Ouml
            S_EMPTY + (char) 154,
            // Uuml
            S_EMPTY + (char) 155,
            // ??
            S_EMPTY + (char) 156,
            // ??
            S_EMPTY + (char) 157,
            // ??
            S_EMPTY + (char) 158,
            // ??
            S_EMPTY + (char) 159,
            // ??
            S_EMPTY + (char) 160,
            // aacute
            S_EMPTY + (char) 161,
            // iacute
            S_EMPTY + (char) 162,
            // oacute
            S_EMPTY + (char) 163,
            // uacute
            S_EMPTY + (char) 164,
            // nwave?
            S_EMPTY + (char) 165,
            // Nwave?
            S_EMPTY + (char) 166,
            // ??
            S_EMPTY + (char) 167,
            // ??
            S_EMPTY + (char) 168,
            // ??
            S_EMPTY + (char) 169,
            // ??
            S_EMPTY + (char) 170,
            // 170
            S_EMPTY + (char) 171,
            // ??
            S_EMPTY + (char) 172,
            // ??
            S_EMPTY + (char) 173,
            // ??
            S_EMPTY + (char) 174,
            // ??
            S_EMPTY + (char) 175,
            // ??
            S_EMPTY + (char) 176,
            // ??
            S_EMPTY + (char) 177,
            // ??
            S_EMPTY + (char) 178,
            // ??
            S_EMPTY + (char) 179,
            // ??
            S_EMPTY + (char) 180,
            // 180
            //
            S_EMPTY + (char) 192,
            // eacute
            S_EMPTY + (char) 248,
            // degrees
            S_EMPTY + (char) 352,
            // egrave
            S_EMPTY + (char) 402,
            // acirc
            S_EMPTY + (char) 710,
            // ecirc
            S_EMPTY + (char) 8218,
            // eacute
            S_EMPTY + (char) 8221,
            // ouml
            S_EMPTY + (char) 8222,
            // auml
            S_EMPTY + (char) 8225,
            // ccedil
            S_EMPTY + (char) 8230,
            // agrave
            S_EMPTY + (char) 8240,
            // euml
            S_EMPTY + (char) 65533,
    // uuml
    };

    static String[] asciiEquivalents = { S_EMPTY,
    // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 199,
            // Ccedil
            S_EMPTY + (char) 252,
            // uuml
            S_EMPTY + (char) 233,
            // eacute
            S_EMPTY + (char) 226,
            // acirc
            S_EMPTY + (char) 228,
            // auml
            S_EMPTY + (char) 224,
            // agrave
            S_EMPTY + (char) 229,
            // aring
            S_EMPTY + (char) 231,
            // ccedil
            S_EMPTY + (char) 234,
            // ecirc
            S_EMPTY + (char) 235,
            // euml
            S_EMPTY + (char) 232,
            // egrave
            S_EMPTY + (char) 239,
            // iuml
            S_EMPTY + (char) 238,
            // icirc
            S_EMPTY + (char) 236,
            // igrave
            S_EMPTY + (char) 196,
            // Auml
            S_EMPTY + (char) 197,
            // Aring
            S_EMPTY + (char) 201,
            // Eacute
            S_EMPTY + (char) 230,
            // aelig
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 244,
            // ocirc
            S_EMPTY + (char) 246,
            // ouml
            S_EMPTY + (char) 242,
            // ograve
            S_EMPTY + (char) 251,
            // ucirc
            S_EMPTY + (char) 249,
            // ugrave
            S_EMPTY + (char) 255,
            // yuml
            S_EMPTY + (char) 214,
            // Ouml
            S_EMPTY + (char) 220,
            // Uuml
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 0,
            // ff?
            S_EMPTY + (char) 225,
            // aacute
            S_EMPTY + (char) 237,
            // iacute
            S_EMPTY + (char) 243,
            // oacute
            S_EMPTY + (char) 250,
            // uacute
            S_EMPTY + (char) 241,
            // nwave?
            S_EMPTY + (char) 209,
            // Nwave?
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // 170
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // ??
            S_EMPTY + (char) 0,
            // 180
            //
            S_EMPTY + (char) 233,
            // eacute
            "[degrees]",
            // degrees
            S_EMPTY + (char) 232,
            // egrave
            S_EMPTY + (char) 226,
            // acirc
            S_EMPTY + (char) 234,
            // ecirc
            S_EMPTY + (char) 233,
            // eacute
            S_EMPTY + (char) 246,
            // ouml
            S_EMPTY + (char) 228,
            // auml
            S_EMPTY + (char) 231,
            // ccedil
            S_EMPTY + (char) 224,
            // agrave
            S_EMPTY + (char) 235,
            // euml
            S_EMPTY + (char) 252,
    // uuml
    };

    /**
     * substitute certain DOS-compatible diacriticals by the Unicode value. Not
     * guaranteed to be correct. Example 130 is e-acute (==
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String substituteDOSbyAscii(String s) {
        // check for untranslated chars
        for (int i = 0; i < s.length(); i++) {
            int jj = (int) s.charAt(i);
            if (jj > 180) {
                boolean ok = false;
                for (int j = 0; j < dosEquivalents.length; j++) {
                    if (dosEquivalents[j].equals(S_EMPTY + s.charAt(i))) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    logger.severe("==Unknown DOS character==" + jj + "//" + s);
                }
            }
        }
        String s1 = substituteStrings(s, dosEquivalents, asciiEquivalents);
        return s1;
    }

    /**
     * substitute hex representation of character, for example =2E by char(46).
     * If line ends with =, ignore that character.
     * 
     * @param s
     *            Description of the Parameter
     * @return String result
     */
    public static String substituteEquals(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        StringBuffer sb = new StringBuffer(S_EMPTY);
        while (true) {
            int idx = s.indexOf(S_EQUALS);
            if (idx == -1) {
                sb.append(s);
                return sb.toString();
            }
            // remove EQUALS
            sb.append(s.substring(0, idx));
            s = s.substring(idx + 1);
            len -= idx + 1;
            // not enough chars
            if (len <= 1) {
                sb.append(S_EQUALS);
                sb.append(s);
                return sb.toString();
            }
            int hex = getIntFromHex(s.substring(0, 2));
            // wasn't a hexchar
            if (hex < 0) {
                sb.append(S_EQUALS);
            } else {
                sb.append((char) hex);
                s = s.substring(2);
                len -= 2;
            }
        }
    }

    /**
     * 
     * Translates a Hex number to its int equivalent. Thus "FE" translates to
     * 
     * 254. Horrid, but I couldn't find if Java reads hex. All results are >=
     * 
     * 0. Errors return -1
     * 
     * 
     * 
     * @param hex
     *            Description of the Parameter
     * 
     * @return The intFromHex value
     * 
     */
    public static int getIntFromHex(String hex) {
        hex = hex.toUpperCase();
        if (hex.startsWith("0X")) {
            hex = hex.substring(2);
        } else if (hex.charAt(0) == 'X') {
            hex = hex.substring(1);
        }
        int result = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            if (Character.isDigit(c)) {
                c -= '0';
            } else if (c < 'A' || c > 'F') {
                return -1;
            } else {
                c -= 'A';
                c += (char) 10;
            }
            result = 16 * result + c;
        }
        return result;
    }

    /**
     * capitalise a String (whatever the starting case)
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String capitalise(String s) {
        if (s.equals(S_EMPTY)) {
            return S_EMPTY;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        } else {
            return s.substring(0, 1).toUpperCase()
                    + s.substring(1).toLowerCase();
        }
    }

    /**
     * Description of the Method
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String toCamelCase(String s) {
        StringTokenizer st = new StringTokenizer(s, " \n\r\t");
        String out = S_EMPTY;
        while (st.hasMoreTokens()) {
            s = st.nextToken();
            if (out != S_EMPTY) {
                s = capitalise(s);
            }
            out += s;
        }
        return out;
    }

    /**
     * reads a byte array from file, *including* line feeds
     * 
     * @param filename
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception FileNotFoundException
     *                Description of the Exception
     * @exception IOException
     *                Description of the Exception
     */
    public static byte[] readByteArray(String filename)
            throws FileNotFoundException, IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filename));
        return Util.readByteArray(dis);
    }

    /**
     * reads a byte array from DataInputStream, *including* line feeds
     * 
     * @param d
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception IOException
     *                Description of the Exception
     */
    public static byte[] readByteArray(DataInputStream d) throws IOException {
        int len = 100;
        int count = 0;
        byte[] src = new byte[len];
        byte b;
        while (true) {
            try {
                b = d.readByte();
            } catch (EOFException e) {
                break;
            }
            src[count] = b;
            if (++count >= len) {
                len *= 2;
                byte[] temp = new byte[len];
                System.arraycopy(src, 0, temp, 0, count);
                src = temp;
            }
        }
        len = count;
        byte[] temp = new byte[len];
        System.arraycopy(src, 0, temp, 0, count);
        return temp;
    }

    /**
     * remove all control (non-printing) characters
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String stripISOControls(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        int l = s.length();
        for (int i = 0; i < l; i++) {
            char ch = s.charAt(i);
            if (!Character.isISOControl(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * normalise whitespace in a String (all whitespace is transformed to single
     * spaces and the string is NOT trimmed
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String normaliseWhitespace(String s) {
        if (s == null || s.equals(S_EMPTY)) {
            return s;
        }
        StringTokenizer st = new StringTokenizer(s, Util.WHITESPACE);
        int l = s.length();
        String ss = S_EMPTY;
        if (Character.isWhitespace(s.charAt(0))) {
            ss = S_SPACE;
        }
        String end = S_EMPTY;
        if (Character.isWhitespace(s.charAt(l - 1))) {
            end = S_SPACE;
        }
        boolean start = true;
        while (st.hasMoreTokens()) {
            if (start) {
                ss += st.nextToken();
                start = false;
            } else {
                ss += S_SPACE + st.nextToken();
            }
        }
        return ss + end;
    }

    /**
     * strip linefeeds from a byte array
     * 
     * @param b
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static byte[] stripNewlines(byte[] b) {
        int l = b.length;
        byte[] bb = new byte[l];
        int j = 0;
        for (int i = 0; i < l; i++) {
            if (b[i] != '\n') {
                bb[j++] = b[i];
            }
        }
        byte[] bbb = new byte[j];
        System.arraycopy(bb, 0, bbb, 0, j);
        return bbb;
    }

    /**
     * truncate filename suffix to make a directory name (without
     * file.separator)
     * 
     * @param urlString
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String makeDirectory(String urlString) {
        if (urlString == null) {
            return null;
        }
        int idx = urlString.lastIndexOf(System.getProperty("file.separator"));
        if (idx != -1) {
            urlString = urlString.substring(0, idx);
        }
        return urlString;
    }

    /**
     * If a URL is relative, make it absolute against the current directory. If
     * url already has a protocol, return unchanged
     * 
     * @param url
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception java.net.MalformedURLException
     *                Description of the Exception
     */
    public static String makeAbsoluteURL(String url)
            throws java.net.MalformedURLException {
        if (url == null) {
            throw new MalformedURLException("Null url");
        }
        URL baseURL;
        String fileSep = System.getProperty("file.separator");
        // why the final slash?
        // url = url.replace(fileSep.charAt(0), '/') + '/';
        url = url.replace(fileSep.charAt(0), '/');
        // is url alreday a valid URL?
        boolean ok = true;
        try {
            /* URL u = */new URL(url);
        } catch (MalformedURLException mue) {
            ok = false;
            // DOS filenames (for example C:\foo) gives problems
            String mueString = mue.toString().trim();
            int idx = mueString.indexOf("unknown protocol:");
            if (idx != -1) {
                mueString = mueString.substring(
                        idx + "unknown protocol:".length()).trim();
                // starts with X: assume DOS filename
                if (mueString.length() == 1) {
                    url = "file:/" + url;
                    // throws MalformedURL if wrong
                    /* URL u = */new URL(url);
                    ok = true;
                }
            }
        }
        if (ok) {
            return url;
        }
        String currentDirectory = System.getProperty("user.dir");
        String file = currentDirectory.replace(fileSep.charAt(0), '/') + '/';
        if (file.charAt(0) != '/') {
            file = S_SLASH + file;
        }
        baseURL = new URL("file", null, file);
        String newUrl = new URL(baseURL, url).toString();
        return newUrl;
    }

    /**
     * get an OutputStream from a file or URL. Required (I think) because
     * strings of the sort "file:/C:\foo\bat.txt" crash FileOutputStream, so
     * this strips off the file:/ stuff for Windows-like stuff
     * 
     * @param fileName
     *            Description of the Parameter
     * @return FileOutputStream a new (opened) FileOutputStream
     * @exception java.io.FileNotFoundException
     *                Description of the Exception
     */
    public static FileOutputStream getFileOutputStream(String fileName)
            throws java.io.FileNotFoundException {
        if (fileName == null) {
            return null;
        }
        // W-like syntax
        if (fileName.startsWith("file:")
                && fileName.substring(5).indexOf(S_COLON) != -1) {
            fileName = fileName.substring(5);
            if (fileName.startsWith(S_SLASH)
                    || fileName.startsWith(S_BACKSLASH)) {
                fileName = fileName.substring(1);
            }
        }
        return new FileOutputStream(fileName);
    }

    // cache the formats
    static Hashtable<String, DecimalFormat> formTable = new Hashtable<String, DecimalFormat>();

    /**
     * this is a mess
     * 
     * @param nPlaces
     *            Description of the Parameter
     * @param value
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception EuclidException
     *                Description of the Exception
     */
    public static String outputInteger(int nPlaces, int value)
            throws EuclidException {
        // cache formatter
        String f = "i" + nPlaces;
        DecimalFormat form = formTable.get(f);
        if (form == null) {
            String pattern = S_EMPTY;
            for (int i = 0; i < nPlaces - 1; i++) {
                pattern += "#";
            }
            pattern += "0";
            form = (DecimalFormat) NumberFormat.getInstance();
            form.setMaximumIntegerDigits(nPlaces);
            form.applyLocalizedPattern(pattern);
            formTable.put(f, form);
        }
        String result = form.format(value).trim();
        int l = result.length();
        if (l > nPlaces) {
            throw new EuclidException("Integer too big");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nPlaces - l; i++) {
            sb.append(S_SPACE);
        }
        return sb.append(result).toString();
    }

    /**
     * format for example f8.3 this is a mess; if cannot fit, then either
     * right-truncates or when that doesn't work, returns ****
     * 
     * @param nPlaces
     *            Description of the Parameter
     * @param nDec
     *            Description of the Parameter
     * @param value
     *            Description of the Parameter
     * @return Description of the Return Value
     * @exception EuclidException
     *                Description of the Exception
     */
    public static String outputFloat(int nPlaces, int nDec, double value)
            throws EuclidException {
        String f = "f" + nPlaces + S_PERIOD + nDec;
        DecimalFormat form = formTable.get(f);
        if (form == null) {
            String pattern = S_EMPTY;
            for (int i = 0; i < nPlaces - nDec - 2; i++) {
                pattern += "#";
            }
            pattern += "0.";
            for (int i = nPlaces - nDec; i < nPlaces; i++) {
                pattern += "0";
            }
            form = (DecimalFormat) NumberFormat.getInstance();
            DecimalFormatSymbols symbols = form.getDecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            form.setDecimalFormatSymbols(symbols);
            form.setMaximumIntegerDigits(nPlaces - nDec - 1);
            // form.applyLocalizedPattern(pattern);
            form.applyPattern(pattern);
            formTable.put(f, form);
        }
        String result = form.format(value).trim();
        boolean negative = false;
        if (result.charAt(0) == '-') {
            result = result.substring(1);
            negative = true;
        }
        // this removes leading zeroes :-(
        while (result.charAt(0) == '0') {
            result = result.substring(1);
        }
        if (negative) {
            result = S_MINUS + result;
        }
        StringBuffer sb = new StringBuffer();
        int l = result.length();
        for (int i = 0; i < nPlaces - l; i++) {
            sb.append(S_SPACE);
        }
        String s = sb.append(result).toString();
        if (l > nPlaces) {
            s = s.substring(0, nPlaces);
            // decimal point got truncated?
            if (s.indexOf(S_PERIOD) == -1) {
                s = S_EMPTY;
                for (int i = 0; i < nPlaces; i++) {
                    s += S_STAR;
                }
            }
        }
        return s;
    }

    /**
     * as above, but trims trailing zeros
     * 
     * @param nPlaces
     *            Description of the Parameter
     * @param nDec
     *            Description of the Parameter
     * @param c
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static String outputNumber(int nPlaces, int nDec, double c) {
        String s = null;
        try {
            s = Util.outputFloat(nPlaces, nDec, c).trim();
        } catch (EuclidException e) {
            Util.BUG(e);
        }
        if (s.indexOf(S_PERIOD) != -1) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(S_PERIOD)) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }

    /**
     * invert a Hashtable by interchanging keys and values. This assumes a 1;1
     * mapping - if not the result is probably garbage.
     * 
     * @param table
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public static Hashtable<Object, Object> invert(Hashtable table) {
        if (table == null) {
            return null;
        }
        Hashtable<Object, Object> newTable = new Hashtable<Object, Object>();
        for (Enumeration e = table.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = table.get(key);
            newTable.put(value, key);
        }
        return newTable;
    }

    /**
     * checks array is not null and is of given size.
     * 
     * @param array
     *            to check
     * @param size
     *            required size
     * @throws EuclidException
     *             if null or wrong size
     */
    public static void check(double[] array, int size) throws EuclidException {
        if (array == null) {
            throw new EuclidException("null array");
        } else if (array.length != size) {
            throw new EuclidException("array size required (" + size
                    + ") found " + array.length);
        }
    }

    /**
     * checks that an in is in the range low to high.
     * 
     * @param n
     *            to check
     * @param low
     *            inclusive lower
     * @param high
     *            inclusive higher
     * @throws EuclidException
     *             if out of range
     */
    public static void check(int n, int low, int high) throws EuclidException {
        if (n < low || n > high) {
            throw new EuclidException("index (" + n + ")out of range: " + low
                    + S_SLASH + high);
        }
    }

    /**
     * compare two arrays of doubles.
     * 
     * @param a
     *            first array
     * @param b
     *            second array
     * @param eps
     *            maximum allowed difference
     * @return true if arrays non-null and if arrays are equal length and
     *         corresonding elements agree within eps.
     */
    public static boolean isEqual(double[] a, double[] b, double eps) {
        boolean equal = (a != null && b != null && a.length == b.length);
        if (equal) {
            for (int i = 0; i < a.length; i++) {
                if (Math.abs(a[i] - b[i]) >= eps) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    /**
     * compare two arrays of ints.
     * 
     * @param a
     *            first array
     * @param b
     *            second array
     * @param eps
     *            maximum allowed difference
     * @return true if arrays non-null and if arrays are equal length and
     *         corresonding elements agree within eps.
     */
    public static boolean isEqual(int[] a, int[] b, int eps) {
        boolean equal = (a != null && b != null && a.length == b.length);
        if (equal) {
            for (int i = 0; i < a.length; i++) {
                if (Math.abs(a[i] - b[i]) >= eps) {
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }

    /**
     * concatenates array of booleans.
     * 
     * @param bb
     *            the values
     * @param separator
     * @return the String
     */
    public final static String concatenate(boolean[] bb, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bb.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(bb[i]);
        }
        return sb.toString();
    }

    /**
     * concatenates array of doubles.
     * 
     * @param ss
     *            the values
     * @param separator
     * @return the String
     */
    public final static String concatenate(double[] ss, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(ss[i]);
        }
        return sb.toString();
    }

    /**
     * concatenates array of array of doubles.
     * 
     * @param ss
     *            the values
     * @param separator
     * @return the String
     */
    public final static String concatenate(double[][] ss, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(Util.concatenate(ss[i], separator));
        }
        return sb.toString();
    }

    /**
     * splits string into ints.
     * 
     * @param s
     *            the string
     * @param delim
     *            delimiter (use S_WHITEREGEX to split whitespace)
     * @return array
     * @throws EuclidException
     *             cannot parse as ints
     */
    public final static int[] splitToIntArray(String s, String delim)
            throws EuclidException {
        String[] ss = s.split(delim);
        int[] ii = new int[ss.length];
        for (int i = 0; i < ss.length; i++) {
            try {
                ii[i] = Integer.parseInt(ss[i]);
            } catch (NumberFormatException nfe) {
                throw new EuclidException(S_EMPTY + nfe);
            }
        }
        return ii;
    }

    /**
     * splits string into doubles.
     * 
     * @param s
     *            the string
     * @param delim
     *            delimiter (use S_WHITEREGEX to split whitespace)
     * @return array
     * @throws EuclidException
     *             cannot parse as ints
     */
    public final static double[] splitToDoubleArray(String s, String delim)
            throws EuclidException {
        String[] ss = s.split(delim);
        double[] dd = new double[ss.length];
        for (int i = 0; i < ss.length; i++) {
            try {
                dd[i] = new Double(ss[i]).doubleValue();
            } catch (NumberFormatException nfe) {
                throw new EuclidException(S_EMPTY + nfe);
            }
        }
        return dd;
    }

    /**
     * concatenates array of ints.
     * 
     * @param ss
     *            the values
     * @param separator
     * @return the String
     */
    public final static String concatenate(int[] ss, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(ss[i]);
        }
        return sb.toString();
    }

    /**
     * concatenates array of Strings.
     * 
     * @param ss
     *            the values
     * @param separator
     * @return the String
     */
    public final static String concatenate(String[] ss, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(ss[i]);
        }
        String s = sb.toString();
        if (separator.trim().equals(S_EMPTY)) {
            s = s.trim();
        }
        return s;
    }

    /**
     * does an array of Strings contain a String.
     * 
     * @param strings
     * @param s
     *            string to search for
     * @return true if any ss[i] == s
     */
    public final static boolean containsString(String[] strings, String s) {
        boolean b = false;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(s)) {
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * format trailing decimals
     * 
     * @param d
     *            value to be formatted
     * @param ndec
     *            max number of decimals (3 = ****.ddd
     * @return the formatted number
     */
    public static double format(double d, int ndec) {
        int pow = 1;
        for (int i = 0; i < ndec; i++) {
            pow *= 10;
        }
        return (double) Math.round(d * (double) pow) / (double) pow;
    }

    /**
     * trim trailing zeroes and trailing decimal point.
     * 
     * @param d
     * @return trimmed string
     */
    public static String trim(double d) {
        String s = S_EMPTY + d;
        int idx = s.lastIndexOf(S_PERIOD);
        if (idx > 0) {
            int l = s.length() - 1;
            while (l > idx) {
                if (s.charAt(l) != '0') {
                    break;
                }
                l--;
            }
            if (l == idx) {
                l--;
            }
            l++;
            s = s.substring(0, l);
        }
        return s;
    }

    /**
     * translate array of Strings to a List.
     * 
     * @param ss
     *            strings (can include null)
     * @return the list
     */
    public static List<String> createList(String[] ss) {
        List<String> list = new ArrayList<String>();
        for (String s : ss) {
            list.add(s);
        }
        return list;
    }

    private static List<Integer> primeList;
    /** get i'th prime.
     * calculates it on demand if not already present and caches result.
     * @param i
     * @return the primt (starts at 2)
     */
    public static int getPrime(int i) {
        if (primeList == null) {
            primeList = new ArrayList<Integer>();
            primeList.add(new Integer(2));
            primeList.add(new Integer(3));
            primeList.add(new Integer(5));
            primeList.add(new Integer(7));
            primeList.add(new Integer(11));
        }
        int np = primeList.size();
        int p = primeList.get(np - 1).intValue();
        while (np <= i) {
            p = nextPrime(p);
            primeList.add(new Integer(p));
            np++;
        }
        return primeList.get(i).intValue();
    }
    
    private static int nextPrime(int pp) {
        int p = pp;
        for (;;) {
            p = p + 2;
            if (isPrime(p)) {
                break;
            }
        }
        return p;
    }
    
    private static boolean isPrime(int p) {
        boolean prime = true;
        int sp = (int) Math.sqrt(p) + 1;
        for (int i = 1; i < primeList.size(); i++) {
            int pp = primeList.get(i).intValue();
            if (p % pp == 0) {
                prime = false;
                break;
            }
            if (pp > sp) {
                break;
            }
        }
        return prime;
    }
}
