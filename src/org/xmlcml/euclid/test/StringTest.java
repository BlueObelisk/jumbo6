package org.xmlcml.euclid.test;

import org.junit.Assert;

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
public class StringTest extends EuclidTest {

    /**
     * Assert.asserts equality of String arrays.
     * 
     * checks for non-null, then equality of length, then individual elements
     * equality if individual elements are equal or both elements are null
     * 
     * @param message
     * @param a
     *            expected array may include nulls
     * @param b
     *            actual array may include nulls
     */
    public static void assertEquals(String message, String[] a, String[] b) {
        String s = testEquals(a, b);
        if (s != null) {
            Assert.fail(message + "; " + s);
        }
    }

    /**
     * match arrays. error is a == null or b == null or a.length != b.length or
     * a[i] != b[i] nulls match
     * 
     * @param a
     * @param b
     * @return message if errors else null
     */
    public static String testEquals(String[] a, String[] b) {
        String s = null;
        if (a == null) {
            s = "a is null";
        } else if (b == null) {
            s = "b is null";
        } else if (a.length != b.length) {
            s = "unequal arrays: " + a.length + "/" + b.length;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null && b[i] == null) {
                    // both null, match
                } else if (a[i] == null || b[i] == null || !a[i].equals(b[i])) {
                    s = "unequal element (" + i + "), " + a[i] + " != " + b[i];
                    break;
                }
            }
        }
        return s;
    }

    /**
     * Assert.asserts non equality of String arrays.
     * 
     * checks for non-null, then equality of length, then individual elements
     * 
     * @param message
     * @param a
     *            expected array
     * @param b
     *            actual array
     */
    public static void assertNotEquals(String message, String[] a, String[] b) {
        String s = testEquals(a, b);
        if (s == null) {
            Assert.fail(message + "; arrays are equal");
        }
    }

}
