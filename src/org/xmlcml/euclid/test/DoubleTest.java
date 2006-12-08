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
public class DoubleTest extends EuclidTest {

    /**
     * tests equality of doubles.
     * 
     * @param a
     * @param b
     * @param eps
     *            margin of identity
     * @return true if a == b within eps
     */
    public static boolean equals(double a, double b, double eps) {
        return (Math.abs(a - b) < Math.abs(eps));
    }

    /**
     * tests equality of double arrays. arrays must be of same length
     * 
     * @param a
     *            first array
     * @param b
     *            second array
     * @param eps
     *            margin of identity
     * @return array elements equal within eps
     */
    public static boolean equals(double[] a, double[] b, double eps) {
        boolean result = false;
        if (a.length == b.length) {
            result = true;
            for (int i = 0; i < a.length; i++) {
                if (Math.abs(a[i] - b[i]) > Math.abs(eps)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Assert.asserts equality of double arrays.
     * 
     * checks for non-null, then equality of length, then individual elements
     * 
     * @param message
     * @param a
     *            expected array
     * @param b
     *            actual array
     * @param eps
     *            tolerance for agreement
     */
    public static void assertEquals(String message, double[] a, double[] b,
            double eps) {
        String s = testEquals(a, b, eps);
        if (s != null) {
            Assert.fail(message + "; " + s);
        }
    }

    /**
     * Assert.asserts non equality of double arrays.
     * 
     * checks for non-null, then equality of length, then individual elements
     * 
     * @param message
     * @param a
     *            expected array
     * @param b
     *            actual array
     * @param eps
     *            tolerance for agreement
     */
    public static void assertNotEquals(String message, double[] a, double[] b,
            double eps) {
        String s = testEquals(a, b, eps);
        if (s == null) {
            Assert.fail(message + "; arrays are equal");
        }
    }

    /**
     * returns a message if arrays differ.
     * 
     * @param a
     *            array to compare
     * @param b
     *            array to compare
     * @param eps
     *            tolerance
     * @return null if arrays are equal else indicative message
     */
    static String testEquals(double[] a, double[] b, double eps) {
        String s = null;
        if (a == null) {
            s = "a is null";
        } else if (b == null) {
            s = "b is null";
        } else if (a.length != b.length) {
            s = "unequal arrays: " + a.length + "/" + b.length;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (!equals(a[i], b[i], eps)) {
                    s = "unequal element at (" + i + "), " + a[i] + " != "
                            + b[i];
                    break;
                }
            }
        }
        return s;
    }

    /**
     * returns a message if arrays of arrays differ.
     * 
     * @param a
     *            array to compare
     * @param b
     *            array to compare
     * @param eps
     *            tolerance
     * @return null if array are equal else indicative message
     */
    static String testEquals(double[][] a, double[][] b, double eps) {
        String s = null;
        if (a == null) {
            s = "a is null";
        } else if (b == null) {
            s = "b is null";
        } else if (a.length != b.length) {
            s = "unequal arrays: " + a.length + "/" + b.length;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i].length != b[i].length) {
                    s = "row (" + i + ") has unequal lengths: " + a[i].length
                            + "/" + b[i].length;
                    break;
                }
                for (int j = 0; j < a[i].length; j++) {
                    if (!equals(a[i][j], b[i][j], eps)) {
                        s = "unequal element at (" + i + ", " + j + "), ("
                                + a[i][j] + " != " + b[i][j] + ")";
                        break;
                    }
                }
            }
        }
        return s;
    }

}
