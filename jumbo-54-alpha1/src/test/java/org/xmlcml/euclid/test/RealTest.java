package org.xmlcml.euclid.test;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.Real;

/**
 * test Real.
 *
 * @author pmr
 *
 */
public class RealTest extends EuclidTest {

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for 'org.xmlcml.euclid.Real.zeroArray(double, double[])'
     */
    @Test
    public void testZeroArray() {
        double[] rr = new double[5];
        Real.zeroArray(5, rr);
        DoubleTest.assertEquals("double[] ", new double[] { 0.0, 0.0, 0.0, 0.0,
                0.0 }, rr, EPS);
    }

    /**
     * Test method for 'org.xmlcml.euclid.Real.initArray(double, double[],
     * double)'
     */
    @Test
    public void testInitArray() {
        double[] rr = new double[5];
        Real.initArray(5, rr, 3.0);
        DoubleTest.assertEquals("double[] ", new double[] { 3.0, 3.0, 3.0, 3.0,
                3.0 }, rr, EPS);
    }


}
