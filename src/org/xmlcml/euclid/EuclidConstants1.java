package org.xmlcml.euclid;

/**
 * 
 * <p>
 * Constants
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class EuclidConstants1 {

    /** constant */
    public final static String S_COLON = ":";

    /** constant */
    public final static String S_EMPTY = "";

    /** constant */
    public final static String S_SPACE = " ";

    /** constant */
    public final static String S_NL = "\n";

    /** constant */
    public final static String S_QUOT = "\"";

    /** constant */
    public final static String S_SLASH = "/";

    /** constant */
    public final static String S_WHITEREGEX = "\\s+"; // java regex for any
                                                        // whitespace

    /** constant */
    public final static String S_TAB = "\t";

    /** constant */
    public final static String S_RETURN = "\r";

    /** constant */
    public final static String S_NEWLINE = "\n";

    /** constant */
    public final static String S_FORMFEED = "\f";

    /** constant */
    public final static String WHITESPACE = S_SPACE + S_TAB + S_RETURN
            + S_NEWLINE + S_FORMFEED;

    /** constant */
    public final static String S_LBRAK = "(";

    /** constant */
    public final static String S_RBRAK = ")";

    /** constant */
    public final static String S_SHRIEK = "!";

    /** constant */
    public final static String S_POUND = "£";

    /** constant */
    public final static String S_DOLLAR = "$";

    /** constant */
    public final static String S_PERCENT = "%";

    /** constant */
    public final static String S_CARET = "^";

    /** constant */
    public final static String S_AMP = "&";

    /** constant */
    public final static String S_STAR = "*";

    /** constant */
    public final static String S_UNDER = "_";

    /** constant */
    public final static String S_MINUS = "-";

    /** constant */
    public final static String S_PLUS = "+";

    /** constant */
    public final static String S_EQUALS = "=";

    /** constant */
    public final static String S_LCURLY = "{";

    /** constant */
    public final static String S_RCURLY = "}";

    /** constant */
    public final static String S_LSQUARE = "[";

    /** constant */
    public final static String S_RSQUARE = "]";

    /** constant */
    public final static String S_TILDE = "~";

    /** constant */
    public final static String S_HASH = "#";

    /** constant */
    public final static String S_SEMICOLON = ";";

    /** constant */
    public final static String S_ATSIGN = "@";

    /** constant */
    public final static String S_APOS = "'";

    /** constant */
    public final static String S_COMMA = ",";

    /** constant */
    public final static String S_PERIOD = ".";

    /** constant */
    public final static String S_QUERY = "?";

    /** constant */
    public final static String S_LANGLE = "<";

    /** constant */
    public final static String S_RANGLE = ">";

    /** constant */
    public final static String S_PIPE = "|";

    /** constant */
    public final static String S_BACKSLASH = "\\";

    /** constant */
    public final static String NONWHITEPUNC = S_LBRAK + S_RBRAK + S_SHRIEK
            + S_QUOT + S_POUND + S_DOLLAR + S_PERCENT + S_CARET + S_AMP
            + S_STAR + S_UNDER + S_MINUS + S_PLUS + S_EQUALS + S_LCURLY
            + S_RCURLY + S_LSQUARE + S_RSQUARE + S_TILDE + S_HASH + S_COLON
            + S_SEMICOLON + S_ATSIGN + S_APOS + S_COMMA + S_PERIOD + S_SLASH
            + S_QUERY + S_LANGLE + S_RANGLE + S_PIPE + S_BACKSLASH;

    /** constant */
    public final static String PUNC = WHITESPACE + NONWHITEPUNC;

    /** */
    public final static double EPS = 1.0E-14;
}