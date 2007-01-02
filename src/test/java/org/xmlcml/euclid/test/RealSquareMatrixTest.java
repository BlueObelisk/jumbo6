package org.xmlcml.euclid.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealMatrix;
import org.xmlcml.euclid.RealSquareMatrix;

/**
 * tests RealSquareMatrix.
 *
 * @author pmr
 *
 */
public class RealSquareMatrixTest extends MatrixTest {

    static Logger logger = Logger.getLogger(RealSquareMatrixTest.class
            .getName());

    RealSquareMatrix m0;

    RealSquareMatrix m1;

    RealSquareMatrix m2;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logger.setLevel(Level.WARNING);
        m0 = new RealSquareMatrix();
        m1 = new RealSquareMatrix(3);
        m2 = new RealSquareMatrix(3, new double[] { 11., 12., 13., 21., 22.,
                23., 31., 32., 33., });
    }

    /**
     * equality test. true if both args not null and equal within epsilon and
     * rows are present and equals and columns are present and equals
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, RealSquareMatrix test,
            RealSquareMatrix expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        Assert.assertNotNull("expected should have columns (" + msg + S_RBRAK,
                expected.getCols());
        Assert.assertNotNull("expected should have rows (" + msg + S_RBRAK,
                expected.getRows());
        Assert.assertNotNull("test should have columns (" + msg + S_RBRAK, test
                .getCols());
        Assert.assertNotNull("test should have rows (" + msg + S_RBRAK, test
                .getRows());
        Assert.assertEquals("rows should be equal (" + msg + S_RBRAK, test
                .getRows(), expected.getRows());
        Assert.assertEquals("columns should be equal (" + msg + S_RBRAK, test
                .getCols(), expected.getCols());
        DoubleTest.assertEquals(msg, test.getMatrixAsArray(), expected
                .getMatrixAsArray(), epsilon);
    }

    /**
     * equality test. true if both args not null and equal within epsilon
     *
     * @param msg
     *            message
     * @param rows
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, int rows, double[] test,
            RealSquareMatrix expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("ref should not be null (" + msg + S_RBRAK, expected);
        Assert.assertEquals("rows should be equal (" + msg + S_RBRAK, rows,
                expected.getRows());
        DoubleTest
                .assertEquals(msg, test, expected.getMatrixAsArray(), epsilon);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isOrthogonal()'
     */
    @Test
    public void testIsOrthogonal() {
        Assert.assertFalse("isOrthogonal", m2.isOrthogonal());
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 0, 1, 0, -1, 0, 0, 0, 0,
                    1, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("isOrthogonal", m.isOrthogonal());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix()'
     */
    @Test
    public void testRealSquareMatrix() {
        Assert.assertEquals("real square matrix", 0, m0.getRows());
        Assert.assertEquals("real square matrix", 0, m0.getCols());
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(int)'
     */
    @Test
    public void testRealSquareMatrixInt() {
        Assert.assertEquals("real square matrix", 3, m1.getRows());
        Assert.assertEquals("real square matrix", 3, m1.getCols());
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.outerProduct(RealArray)'
     */
    @Test
    public void testOuterProduct() {
        RealArray ra = null;
        try {
            ra = new RealArray(3, new double[] { 1, 2, 3 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealSquareMatrix rsm = RealSquareMatrix.outerProduct(ra);
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { 1.0, 2.0, 3.0, 2.0, 4.0,
                    6.0, 3.0, 6.0, 9.0, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("outer product", rm, (RealMatrix) rsm, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.diagonal(RealArray)'
     */
    @Test
    public void testDiagonal() {
        RealArray ra = null;
        try {
            ra = new RealArray(3, new double[] { 1, 2, 3 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealMatrix rsm = RealSquareMatrix.diagonal(ra);
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3,
                    new double[] { 1, 0, 0, 0, 2, 0, 0, 0, 3, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("diagonal", rm, (RealMatrix) rsm, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(int,
     * double[])'
     */
    @Test
    public void testRealSquareMatrixIntDoubleArray() {
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { 1.0, 2.0, 3.0, 2.0, 4.0,
                    6.0, 3.0, 6.0, 9.0, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealSquareMatrix rsm = null;
        try {
            rsm = new RealSquareMatrix(3, new double[] { 1.0, 2.0, 3.0, 2.0,
                    4.0, 6.0, 3.0, 6.0, 9.0, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("int double[]", rm, (RealMatrix) rsm, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(int,
     * double)'
     */
    @Test
    public void testRealSquareMatrixIntDouble() {
        RealMatrix rm = new RealMatrix(3, 3, 10.);
        RealSquareMatrix rsm = new RealSquareMatrix(3, 10.);
        MatrixTest.assertEquals("int double", rm, (RealMatrix) rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(RealMatrix, int,
     * int, int)'
     */
    @Test
    public void testRealSquareMatrixRealMatrixIntIntInt() {
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 4, new double[] { 11., 12., 13., 14., 21.,
                    22., 23., 24., 31., 32., 33., 34. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealSquareMatrix rsm = null;
        try {
            rsm = new RealSquareMatrix(rm, 1, 1, 2);
        } catch (EuclidException e) {
        }
        RealMatrix rm1 = null;
        try {
            rm1 = new RealMatrix(2, 2, new double[] { 22., 23., 32., 33., });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("rsm int int int", rm1, (RealMatrix) rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(RealSquareMatrix)'
     */
    @Test
    public void testRealSquareMatrixRealSquareMatrix() {
        RealSquareMatrix rsm = new RealSquareMatrix(m2);
        MatrixTest.assertEquals("copy", m2, rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(RealMatrix)'
     */
    @Test
    public void testRealSquareMatrixRealMatrix() {
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(2, 2, new double[] { 22., 23., 32., 33., });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealSquareMatrix rsm = null;
        try {
            rsm = new RealSquareMatrix(rm);
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.isEqualTo(RealSquareMatrix)'
     */
    @Test
    public void testIsEqualToRealSquareMatrix() {
        RealSquareMatrix rsm = new RealSquareMatrix(m2);
        Assert.assertTrue("isEqualTo", m2.isEqualTo(rsm));
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.RealSquareMatrix(double[][])'
     */
    @Test
    public void testRealSquareMatrixDoubleArrayArray() {
        double[][] mat = new double[][] { new double[] { 11., 12., 13. },
                new double[] { 21., 22., 23. }, new double[] { 31., 32., 33. }, };
        RealSquareMatrix rsm = null;
        try {
            rsm = new RealSquareMatrix(mat);
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { 11., 12., 13., 21., 22.,
                    23., 31., 32., 33., });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.plus(RealSquareMatrix)'
     */
    @Test
    public void testPlusRealSquareMatrix() {
        RealSquareMatrix rsm = null;
        try {
            rsm = m2.plus(m2);
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { 22., 24., 26., 42., 44.,
                    46., 62., 64., 66., });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.subtract(RealSquareMatrix)'
     */
    @Test
    public void testSubtractRealSquareMatrix() {
        RealSquareMatrix rsm = null;
        RealSquareMatrix rsm1 = null;
        try {
            rsm = m2.plus(m2);
            rsm1 = m2.subtract(rsm);
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { -11., -12., -13., -21.,
                    -22., -23., -31., -32., -33., });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm1, EPS);

    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.multiply(RealSquareMatrix)'
     */
    @Test
    public void testMultiplyRealSquareMatrix() {
        RealSquareMatrix rsm = null;
        try {
            rsm = m2.multiply(m2);
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealMatrix rm = null;
        try {
            rm = new RealMatrix(3, 3, new double[] { 776.0, 812.0, 848.0,
                    1406.0, 1472.0, 1538.0, 2036.0, 2132.0, 2228.0, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.determinant()'
     */
    @Test
    public void testDeterminant() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        double d = m.determinant();
        Assert.assertEquals("determinant", 2., d, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.trace()'
     */
    @Test
    public void testTrace() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        double d = m.trace();
        Assert.assertEquals("trace", 11., d, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isUnit()'
     */
    @Test
    public void testIsUnit() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., 1., 0.,
                    0., 0., 1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("unit", m.isUnit());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("unit", m.isUnit());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isSymmetric()'
     */
    @Test
    public void testIsSymmetric() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 3., 0., 1., 0.,
                    3., 0., 1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("unit", m.isSymmetric());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("unit", m.isSymmetric());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.orthonormalize()'
     */
    @Test
    public void testOrthonormalize() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("orthonormal", m.isOrthonormal());
        m.orthonormalize();
        Assert.assertTrue("orthonormal", m.isOrthonormal());
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 0.5773502691896258,
                    0.5773502691896258, 0.5773502691896258,
                    -0.7071067811865477, 0.0, 0.7071067811865476,
                    0.40824829046386296, -0.816496580927726,
                    0.40824829046386313, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("orthonormal", mm, m, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isUpperTriangular()'
     */
    @Test
    public void testIsUpperTriangular() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 0., 2., 3., 0., 0., 2.,
                    0., 0., 0. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 2., 3., 0., 1., 2.,
                    0., 0., 1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("upper triangular false", m.isUpperTriangular());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isLowerTriangular()'
     */
    @Test
    public void testIsLowerTriangular() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 0., 0., 0., 2., 0., 0.,
                    3., 2., 0. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 2., 1., 0.,
                    3., 2., 1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4.,
                    3., 4., 7. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("lower triangular false", m.isLowerTriangular());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isImproperRotation()'
     */
    @Test
    public void testIsImproperRotation() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., 1., 0.,
                    0., 0., -1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("isImproper", m.isImproperRotation());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., -1., 0.,
                    0., 0., -1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("isImproper", m.isImproperRotation());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.isUnitary()'
     */
    @Test
    public void testIsUnitary() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., 1., 0.,
                    0., 0., -1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("isUnitary", m.isUnitary());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., -1., 0.,
                    0., 0., -1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertTrue("isUnitary", m.isUnitary());
        try {
            m = new RealSquareMatrix(3, new double[] { 1., 0., 1., 0., -1., 0.,
                    0., 0., -1. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        Assert.assertFalse("isUnitary", m.isUnitary());
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.copyUpperToLower()'
     */
    @Test
    public void testCopyUpperToLower() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 2., 5., 4.,
                    3., 2., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.copyUpperToLower();
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 6., 7., 8., 7., 5., 4.,
                    8., 4., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("copy upper", mm, m, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.copyLowerToUpper()'
     */
    @Test
    public void testCopyLowerToUpper() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 2., 5., 4.,
                    3., 2., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.copyLowerToUpper();
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 6., 2., 3., 2., 5., 2.,
                    3., 2., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("copy upper", mm, m, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.lowerTriangle()'
     */
    @Test
    public void testLowerTriangle() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 2., 5., 4.,
                    3., 2., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealArray ra = m.lowerTriangle();
        RealArrayTest.assertEquals("lower triangle", new double[] { 6.0, 2.0,
                5.0, 3.0, 2.0, 9.0 }, ra, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.transpose()'
     */
    @Test
    public void testTranspose() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 2., 5., 4.,
                    3., 1., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.transpose();
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 6., 2., 3., 7., 5., 1.,
                    8., 4., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("transpose", mm, m, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.orthogonalise()'
     */
    @Test
    public void testOrthogonalise() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 7., 5., 4.,
                    8., 4., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        m.orthogonalise();
        Assert.assertTrue("orthogonalise", m.isOrthogonal());
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 6.0, 7.0, 8.0,
                    7.7316819236624434, -0.35776420212319654,
                    -5.485717765889034, 3.8939506336049337,
                    -10.383868356279821, 6.1654218365411415, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("orthogonalise", mm, m, 0.000000000001);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.RealSquareMatrix.getCrystallographicOrthogonalisation(double[],
     * double[])'
     */
    @Test
    public void testGetCrystallographicOrthogonalisation() {

        double[] len = { 10., 11., 12. };
        double[] ang = { 80., 90., 100. }; // degrees!
        RealSquareMatrix m = RealSquareMatrix
                .getCrystallographicOrthogonalisation(len, ang);
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 9.843316493307713, 0.0,
                    0.0, -1.7632698070846495, 10.832885283134289, 0.0, 0.0,
                    1.9101299543362344, 12.0 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("orthogonalise", mm, m, 0.000000000001);
    }

    /**
     * Test method for 'org.xmlcml.euclid.RealSquareMatrix.getInverse()'
     */
    @Test
    public void testGetInverse() {
        RealSquareMatrix m = null;
        try {
            m = new RealSquareMatrix(3, new double[] { 6., 7., 8., 2., 5., 4.,
                    1., 3., 9. });
        } catch (EuclidException e) {
            neverFail(e);
        }
        RealSquareMatrix inv = null;
        try {
            inv = m.getInverse();
        } catch (EuclidException e) {
            neverThrow(e);
        }
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(3, new double[] { 0.3055555555555556,
                    -0.36111111111111116, -0.11111111111111108,
                    -0.12962962962962962, 0.42592592592592593,
                    -0.07407407407407408, 0.009259259259259259,
                    -0.10185185185185185, 0.14814814814814814, });
        } catch (EuclidException e) {
            neverFail(e);
        }
        MatrixTest.assertEquals("inverse", mm, inv, 0.000000000001);
    }


}