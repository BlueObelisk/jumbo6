package org.xmlcml.euclid.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.IntSquareMatrix;

/**
 * test IntSquareMatrix.
 *
 * @author pmr
 *
 */
public class IntSquareMatrixTest extends MatrixTest {

    static Logger logger = Logger
            .getLogger(IntSquareMatrixTest.class.getName());

    IntSquareMatrix m0;

    IntSquareMatrix m1;

    IntSquareMatrix m2;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logger.setLevel(Level.WARNING);
        m0 = new IntSquareMatrix();
        m1 = new IntSquareMatrix(3);
        m2 = new IntSquareMatrix(3, new int[] { 11, 12, 13, 21, 22, 23, 31, 32,
                33, });
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.isOrthogonal()'
     */
    @Test
    public void testIsOrthogonal() {
        Assert.assertFalse("isOrthogonal", m2.isOrthogonal());
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3,
                    new int[] { 0, 1, 0, -1, 0, 0, 0, 0, 1, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("isOrthogonal", m.isOrthogonal());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix()'
     */
    @Test
    public void testIntSquareMatrix() {
        Assert.assertEquals("real square matrix", 0, m0.getRows());
        Assert.assertEquals("real square matrix", 0, m0.getCols());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(int)'
     */
    @Test
    public void testIntSquareMatrixInt() {
        Assert.assertEquals("real square matrix", 3, m1.getRows());
        Assert.assertEquals("real square matrix", 3, m1.getCols());
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.outerProduct(IntArray)'
     */
    @Test
    public void testOuterProduct() {
        IntArray ra = null;
        try {
            ra = new IntArray(3, new int[] { 1, 2, 3 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntSquareMatrix rsm = IntSquareMatrix.outerProduct(ra);
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("outer product", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.diagonal(IntArray)'
     */
    @Test
    public void testDiagonal() {
        IntArray ra = null;
        try {
            ra = new IntArray(3, new int[] { 1, 2, 3 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntMatrix rsm = IntSquareMatrix.diagonal(ra);
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 1, 0, 0, 0, 2, 0, 0, 0, 3, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("diagonal", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(int,
     * int[])'
     */
    @Test
    public void testIntSquareMatrixIntIntegerArray() {
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntSquareMatrix rsm = null;
        try {
            rsm = new IntSquareMatrix(3,
                    new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("int int[]", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(int,
     * int)'
     */
    @Test
    public void testIntSquareMatrixIntInteger() {
        IntMatrix rm = new IntMatrix(3, 3, 10);
        IntSquareMatrix rsm = new IntSquareMatrix(3, 10);
        MatrixTest.assertEquals("int int", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(IntMatrix, int, int,
     * int)'
     */
    @Test
    public void testIntSquareMatrixIntMatrixIntIntInt() {
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 4, new int[] { 11, 12, 13, 14, 21, 22, 23,
                    24, 31, 32, 33, 34 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntSquareMatrix rsm = null;
        try {
            rsm = new IntSquareMatrix(rm, 1, 1, 2);
        } catch (EuclidException e) {
        }
        IntMatrix rm1 = null;
        try {
            rm1 = new IntMatrix(2, 2, new int[] { 22, 23, 32, 33, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("rsm int int int", rm1, (IntMatrix) rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(IntSquareMatrix)'
     */
    @Test
    public void testIntSquareMatrixIntSquareMatrix() {
        IntSquareMatrix rsm = new IntSquareMatrix(m2);
        MatrixTest.assertEquals("copy", m2, rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(IntMatrix)'
     */
    @Test
    public void testIntSquareMatrixIntMatrix() {
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(2, 2, new int[] { 22, 23, 32, 33, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntSquareMatrix rsm = null;
        try {
            rsm = new IntSquareMatrix(rm);
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.IntSquareMatrix(int[][])'
     */
    @Test
    public void testIntSquareMatrixIntegerArrayArray() {
        int[][] mat = new int[][] { new int[] { 11, 12, 13 },
                new int[] { 21, 22, 23 }, new int[] { 31, 32, 33 }, };
        IntSquareMatrix rsm = null;
        try {
            rsm = new IntSquareMatrix(mat);
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 11, 12, 13, 21, 22, 23, 31,
                    32, 33, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.isEqualTo(IntSquareMatrix)'
     */
    @Test
    public void testIsEqualToIntSquareMatrix() {
        IntSquareMatrix rsm = new IntSquareMatrix(m2);
        Assert.assertTrue("isEqualTo", m2.isEqualTo(rsm));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.plus(IntSquareMatrix)'
     */
    @Test
    public void testPlusIntSquareMatrix() {
        IntSquareMatrix rsm = null;
        try {
            rsm = m2.plus(m2);
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 22, 24, 26, 42, 44, 46, 62,
                    64, 66, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.subtract(IntSquareMatrix)'
     */
    @Test
    public void testSubtractIntSquareMatrix() {
        IntSquareMatrix rsm = null;
        IntSquareMatrix rsm1 = null;
        try {
            rsm = m2.plus(m2);
            rsm1 = m2.subtract(rsm);
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { -11, -12, -13, -21, -22, -23,
                    -31, -32, -33, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm1);

    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSquareMatrix.multiply(IntSquareMatrix)'
     */
    @Test
    public void testMultiplyIntSquareMatrix() {
        IntSquareMatrix rsm = null;
        try {
            rsm = m2.multiply(m2);
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntMatrix rm = null;
        try {
            rm = new IntMatrix(3, 3, new int[] { 776, 812, 848, 1406, 1472,
                    1538, 2036, 2132, 2228, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.isUnit()'
     */
    @Test
    public void testIsUnit() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("unit", m.isUnit());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("unit", m.isUnit());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.isSymmetric()'
     */
    @Test
    public void testIsSymmetric() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 0, 3, 0, 1, 0, 3, 0, 1 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("unit", m.isSymmetric());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("unit", m.isSymmetric());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.isUpperTriangular()'
     */
    @Test
    public void testIsUpperTriangular() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 0, 2, 3, 0, 0, 2, 0, 0, 0 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 2, 3, 0, 1, 2, 0, 0, 1 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("upper triangular false", m.isUpperTriangular());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.isLowerTriangular()'
     */
    @Test
    public void testIsLowerTriangular() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 0, 0, 0, 2, 0, 0, 3, 2, 0 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 0, 0, 2, 1, 0, 3, 2, 1 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        try {
            m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("lower triangular false", m.isLowerTriangular());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.copyUpperToLower()'
     */
    @Test
    public void testCopyUpperToLower() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.copyUpperToLower();
        IntSquareMatrix mm = null;
        try {
            mm = new IntSquareMatrix(3, new int[] { 6, 7, 8, 7, 5, 4, 8, 4, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("copy upper", mm, m);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.copyLowerToUpper()'
     */
    @Test
    public void testCopyLowerToUpper() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.copyLowerToUpper();
        IntSquareMatrix mm = null;
        try {
            mm = new IntSquareMatrix(3, new int[] { 6, 2, 3, 2, 5, 2, 3, 2, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("copy upper", mm, m);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.lowerTriangle()'
     */
    @Test
    public void testLowerTriangle() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        IntArray ra = m.lowerTriangle();
        IntTest.assertEquals("lower triangle", new int[] { 6, 2, 5, 3, 2, 9 },
                ra.getArray());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSquareMatrix.transpose()'
     */
    @Test
    public void testTranspose() {
        IntSquareMatrix m = null;
        try {
            m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 1, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.transpose();
        IntSquareMatrix mm = null;
        try {
            mm = new IntSquareMatrix(3, new int[] { 6, 2, 3, 7, 5, 1, 8, 4, 9 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("transpose", mm, m);
    }


}
