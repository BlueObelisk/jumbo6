package org.xmlcml.cml.element.test;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.test.BaseTest;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.Point3;
/**
 * tests CMLSymmetry.
 *
 * @author pmr
 *
 */
public class CMLSymmetryTest extends BaseTest {
    // space group (Pbca) but not finite group
    static String[] pbca = { "x, y, z", "-x+1/2, -y, z+1/2",
            "x+1/2, -y+1/2, -z", "-x, y+1/2, -z+1/2", "-x, -y, -z",
            "x-1/2, y, -z-1/2", "-x-1/2, y-1/2, z", "x, -y-1/2, z-1/2", };
    // finite group (mmm = d2h)
    static String[] pmmm = { "x, y, z", "-x, -y, z", "x, -y, -z", "-x, y, -z",
            "-x, -y, -z", "x, y, -z", "-x, y, z", "x, -y, z", };
    // incomplete group (only 7 elements)
    static String[] oper3 = { "x, y, z", "-x, -y, z", "x, -y, -z", "-x, y, -z",
            "-x, -y, -z", "x, y, -z", "-x, y, z", };
    // space group without glides and screws (P222 with prigin = 1/4, 1/4, 1/4)
    static String[] p212121 = { "x, y, z", "-x, 1/2+y, 1/2-z",
            "1/2-x, -y, 1/2+z", "1/2+x, 1/2-y, -z", };
    static String[] p21c = { "x, y, z", "-x, y+1/2, -z+1/2",
            "x, -y+1/2, z+1/2", "-x, -y, -z", };
    static String[] p2m = { "x, y, z", "-x, y, -z", "x, -y, z", "-x, -y, -z", };
    static String[] abm2 = { "x, y, z", "-x, -y, z", "x, 1/2-y, z",
            "-x, 1/2+y, z", "x, 1/2+y, 1/2+z", "-x, 1/2-y, 1/2+z",
            "x, -y, 1/2+z", "-x, y, 1/2+z", };
    static String[] p21212 = { "x, y, z", "-x, -y, z", "1/2+x, 1/2-y, -z",
            "1/2-x, 1/2+y, -z", };
    static String[] p1 = { "x, y, z", };
    static String[] p21 = { "x, y, z", "x, 1/2+y, z", };
    static String[] ibar42d = { "x, y, z", "y, -x, -z", "-x, -y, z",
            "-y, x, -z", "x, -y+1/2, -z+1/4", "-y+1/2, -x, z+3/4",
            "-x, y+1/2, -z+1/4", "y+1/2, x, z+3/4", "x+1/2, y+1/2, z+1/2",
            "y+1/2, -x+1/2, -z+1/2", "-x+1/2, -y+1/2, z+1/2",
            "-y+1/2, x+1/2, -z+1/2", "x+1/2, -y, -z+3/4", "-y, -x+1/2, z+1/4",
            "-x+1/2, y, -z+3/4", "y, x+1/2, z+1/4" };
    static String[] c2c = { "x, y, z", "-x, y, -z+1/2", "x+1/2, y+1/2, z",
            "-x+1/2, y+1/2, -z+1/2", "-x, -y, -z", "x, -y, z-1/2",
            "-x+1/2, -y+1/2, -z", "x+1/2, -y+1/2, z-1/2" };
    static String[] rbar3 = { "x, y, z", "-y, x-y, z", "-x+y, -x, z",
            "x+2/3, y+1/3, z+1/3", "-y+2/3, x-y+1/3, z+1/3",
            "-x+y+2/3, -x+1/3, z+1/3", "x+1/3, y+2/3, z+2/3",
            "-y+1/3, x-y+2/3, z+2/3", "-x+y+1/3, -x+2/3, z+2/3", "-x, -y, -z",
            "y, -x+y, -z", "x-y, x, -z", "-x+2/3, -y+1/3, -z+1/3",
            "y+2/3, -x+y+1/3, -z+1/3", "x-y+2/3, x+1/3, -z+1/3",
            "-x+1/3, -y+2/3, -z+2/3", "y+1/3, -x+y+2/3, -z+2/3",
            "x-y+1/3, x+2/3, -z+2/3" };
    static String[] abar22a = { "x, y, z", "x+1/2, -y, -z", "x+1/2, -y, z",
            "x, y, -z", "x, y+1/2, z+1/2", "x+1/2, -y+1/2, -z+1/2",
            "x+1/2, -y+1/2, z+1/2", "x, y+1/2, -z+1/2" };
    static String[] fdd2 = { "x, y, z", "-x, -y, z", "-x+1/4, y+1/4, z+1/4",
            "x+1/4, -y+1/4, z+1/4", "x, y+1/2, z+1/2", "-x, -y+1/2, z+1/2",
            "-x+1/4, y+3/4, z+3/4", "x+1/4, -y+3/4, z+3/4", "x+1/2, y, z+1/2",
            "-x+1/2, -y, z+1/2", "-x+3/4, y+1/4, z+3/4",
            "x+3/4, -y+1/4, z+3/4", "x+1/2, y+1/2, z", "-x+1/2, -y+1/2, z",
            "-x+3/4, y+3/4, z+1/4", "x+3/4, -y+3/4, z+1/4" };
    CMLMatrix[] matrix1 = null;
    CMLTransform3[] transform31 = null;
    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
            matrix1 = new CMLMatrix[] {
                    new CMLMatrix(3, 4, new double[] { 1, 0, 0, 0.0, 0, 1, 0,
                            0.0, 0, 0, 1, 0.0 }),
                    new CMLMatrix(3, 4, new double[] { -1, 0, 0, 0.5, 0, -1, 0,
                            0.0, 0, 0, 1, 0.5 }),
                    new CMLMatrix(3, 4, new double[] { 1, 0, 0, 0.5, 0, -1, 0,
                            0.5, 0, 0, -1, 0.0 }),
                    new CMLMatrix(3, 4, new double[] { -1, 0, 0, 0.0, 0, 1, 0,
                            0.5, 0, 0, -1, 0.5 }),
                    new CMLMatrix(3, 4, new double[] { -1, 0, 0, 0.0, 0, -1, 0,
                            0.0, 0, 0, -1, 0.0 }),
                    new CMLMatrix(3, 4, new double[] { 1, 0, 0, -0.5, 0, 1, 0,
                            0.0, 0, 0, -1, -0.5 }),
                    new CMLMatrix(3, 4, new double[] { -1, 0, 0, -0.5, 0, 1, 0,
                            -0.5, 0, 0, 1, 0.0 }),
                    new CMLMatrix(3, 4, new double[] { 1, 0, 0, 0.0, 0, -1, 0,
                            -0.5, 0, 0, 1, -0.5 }) };
        } catch (CMLException e) {
            throw new CMLRuntimeException("bug " + e);
        }
            transform31 = new CMLTransform3[] {
                new CMLTransform3(new double[] { 1, 0, 0, 0.0, 0, 1, 0,
                        0.0, 0, 0, 1, 0.0, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { -1, 0, 0, 0.5, 0, -1, 0,
                        0.0, 0, 0, 1, 0.5, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { 1, 0, 0, 0.5, 0, -1, 0,
                        0.5, 0, 0, -1, 0.0, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { -1, 0, 0, 0.0, 0, 1, 0,
                        0.5, 0, 0, -1, 0.5, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { -1, 0, 0, 0.0, 0, -1, 0,
                        0.0, 0, 0, -1, 0.0, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { 1, 0, 0, -0.5, 0, 1, 0,
                        0.0, 0, 0, -1, -0.5, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { -1, 0, 0, -0.5, 0, 1, 0,
                        -0.5, 0, 0, 1, 0.0, 0, 0, 0, 1 }),
                new CMLTransform3(new double[] { 1, 0, 0, 0.0, 0, -1, 0,
                        -0.5, 0, 0, 1, -0.5, 0, 0, 0, 1 }), };
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLSymmetry.CMLSymmetry(String[])'
     */
    @Test
    public void testCMLSymmetryStringArray() {
        Assert.assertEquals("opercount", 8, pbca.length);
        CMLSymmetry symmetry = null;
        try {
            symmetry = new CMLSymmetry(pbca);
        } catch (CMLException e) {
            throw new CMLRuntimeException("bug " + e);
        }
        CMLElements<CMLMatrix> matrices = symmetry.getMatrixElements();
        Assert.assertEquals("matrixcount", 0, matrices.size());
        CMLElements<CMLTransform3> transform3s = symmetry
                .getTransform3Elements();
        Assert.assertEquals("transformcount", 8, transform3s.size());
        int i = 0;
        for (CMLTransform3 transform3 : transform3s) {
            CMLTransform3Test.assertEquals("symmetry element transform3 " + i,
                    transform31[i++], transform3, CMLConstants.EPS);
        }
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.isGroup()'
     */
    @Test
    public void testIsGroup() {
        try {
            CMLSymmetry symmetry = new CMLSymmetry(pbca);
            Assert.assertEquals("group", false, symmetry.isGroup());
            symmetry = new CMLSymmetry(pmmm);
            Assert.assertEquals("group", true, symmetry.isGroup());
            symmetry = new CMLSymmetry(oper3);
            Assert.assertEquals("group", false, symmetry.isGroup());
            symmetry = new CMLSymmetry(p212121);
            Assert.assertEquals("group", false, symmetry.isGroup());
        } catch (CMLException e) {
            neverThrow(e);
        }
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.isSpaceGroup()'
     */
    @Test
    public void testIsSpaceGroup() {
        CMLSymmetry symmetry = null;
        try {
            symmetry = new CMLSymmetry(pbca);
        } catch (CMLException e) {
            neverThrow(e);
        }
        // Assert.assertEquals("group", true, symmetry.isSpaceGroup());
        try {
            symmetry = new CMLSymmetry(p212121);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertEquals("group", true, symmetry.isSpaceGroup());
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.multiplyCMLSymmetry
     * sym)
     */
    @Test
    public void testCMLSymmetrymultiplyCMLSymmetry() {
        CMLSymmetry sym1 = null;
        try {
            sym1 = new CMLSymmetry(new String[] { "x, y, z", "-x, -y, -z" });
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLSymmetry sym1Copy = new CMLSymmetry(sym1);
        Assert.assertTrue("convolute", sym1Copy.isEqualTo(sym1, EPS));
        CMLSymmetry mmmGenerators = null;
        try {
            mmmGenerators = new CMLSymmetry(new String[] { "x, y, z",
                    "-x, y, z", "x, -y, z", "x, y, -z", });
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLSymmetry mmm = null;
        try {
            mmm = new CMLSymmetry(new String[] { "x, y, z", "-x, y, z",
                    "x, -y, z", "x, y, -z", "-x, -y, -z", "x, -y, -z",
                    "-x, y, -z", "-x, -y, z", });
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLSymmetry sym2Copy = new CMLSymmetry(mmmGenerators);
        Assert.assertTrue("convolute", sym2Copy.isEqualTo(mmmGenerators, EPS));
        // this generates a complete group
        CMLSymmetry sym = sym1.convolute(mmmGenerators);
        // make sure no corruption
        Assert.assertTrue("convolute", sym2Copy.isEqualTo(mmmGenerators, EPS));
        Assert.assertTrue("convolute", sym1Copy.isEqualTo(sym1, EPS));
        Assert.assertEquals("convolute", 8, sym.getTransform3Elements().size());
        Assert.assertTrue("convolute", sym.isEqualTo(mmm, EPS));
        sym = sym1.convolute(sym1);
        Assert.assertEquals("convolute", 2, sym.getTransform3Elements().size());
        sym = mmmGenerators.convolute(mmmGenerators);
        // this does not generate a complete group (-x, -y, -z is missing)
        Assert.assertEquals("convolute", 7, sym.getTransform3Elements().size());
        CMLSymmetry sym4 = null;
        try {
            sym4 = new CMLSymmetry(new String[] { "x, y, z", "-x, y, z",
                    "x, -y, z", "x, y, -z", "-x, -y, z", "-x, y, -z",
                    "x, -y, -z", });
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("convolute", sym.isEqualTo(sym4, EPS));
    }
    /** test. */
    @Test
    public void testGetNonTranslations() {
        CMLSymmetry fullGroup = null;
        CMLSymmetry nonTranslationSubGroup = null;
        CMLElements<CMLTransform3> subGroupElements = null;
        try {
            fullGroup = new CMLSymmetry(p212121);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", fullGroup.isSpaceGroup());
        nonTranslationSubGroup = fullGroup.getNonTranslations();
        subGroupElements = nonTranslationSubGroup.getTransform3Elements();
        Assert.assertEquals("group elements", 0, subGroupElements.size());
        try {
            fullGroup = new CMLSymmetry(p21c);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", fullGroup.isSpaceGroup());
        nonTranslationSubGroup = fullGroup.getNonTranslations();
        Assert.assertFalse("is group", nonTranslationSubGroup.isSpaceGroup());
        subGroupElements = nonTranslationSubGroup.getTransform3Elements();
        Assert.assertEquals("group elements", 1, subGroupElements.size());
        try {
            fullGroup = new CMLSymmetry(pbca);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", fullGroup.isSpaceGroup());
        nonTranslationSubGroup = fullGroup.getNonTranslations();
        Assert.assertFalse("is group", nonTranslationSubGroup.isSpaceGroup());
        subGroupElements = nonTranslationSubGroup.getTransform3Elements();
        Assert.assertEquals("group elements", 1, subGroupElements.size());
        try {
            fullGroup = new CMLSymmetry(abm2);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", fullGroup.isSpaceGroup());
        nonTranslationSubGroup = fullGroup.getNonTranslations();
        Assert.assertFalse("is not space group", nonTranslationSubGroup
                .isSpaceGroup());
        subGroupElements = nonTranslationSubGroup.getTransform3Elements();
        Assert.assertEquals("group elements", 2, subGroupElements.size());
    }
    /** test. */
    @Test
    public void testGetPureTranslations() {
        CMLSymmetry fullGroup = null;
        try {
            fullGroup = new CMLSymmetry(p212121);
        } catch (CMLException e) {
            neverThrow(e);
        }
        List<CMLTransform3> operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 0, operators.size());
        try {
            fullGroup = new CMLSymmetry(ibar42d);
        } catch (CMLException e) {
            neverThrow(e);
        }
        operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 1, operators.size());
        CMLVector3Test.assertEquals("centering",
                new double[] { 0.5, 0.5, 0.5 }, operators.get(0)
                        .getTranslation(), EPS);
        CMLCrystal.Centering centering = fullGroup.getCentering();
        Assert.assertTrue("centering type", CMLCrystal.Centering.I
                .equals(centering));
        try {
            fullGroup = new CMLSymmetry(c2c);
        } catch (CMLException e) {
            neverThrow(e);
        }
        operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 1, operators.size());
        CMLVector3Test.assertEquals("centering", new double[] { 0.5, 0.5, 0 },
                operators.get(0).getTranslation(), EPS);
        centering = fullGroup.getCentering();
        Assert.assertTrue("centering type", CMLCrystal.Centering.C
                .equals(centering));
        try {
            fullGroup = new CMLSymmetry(rbar3);
        } catch (CMLException e) {
            neverThrow(e);
        }
        operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 2, operators.size());
        CMLVector3Test.assertEquals("centering", new double[] { 2. / 3.,
                1. / 3., 1. / 3. }, operators.get(0).getTranslation(), EPS);
        CMLVector3Test.assertEquals("centering", new double[] { 1. / 3.,
                2. / 3., 2. / 3. }, operators.get(1).getTranslation(), EPS);
        centering = fullGroup.getCentering();
        Assert.assertTrue("centering type", CMLCrystal.Centering.R
                .equals(centering));
        try {
            fullGroup = new CMLSymmetry(abar22a);
        } catch (CMLException e) {
            neverThrow(e);
        }
        operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 1, operators.size());
        CMLVector3Test.assertEquals("centering", new double[] { 0, 0.5, 0.5 },
                operators.get(0).getTranslation(), EPS);
        centering = fullGroup.getCentering();
        Assert.assertTrue("centering type", CMLCrystal.Centering.A
                .equals(centering));
        try {
            fullGroup = new CMLSymmetry(fdd2);
        } catch (CMLException e) {
            neverThrow(e);
        }
        operators = fullGroup.getPureTranslations();
        Assert.assertEquals("translations", 3, operators.size());
        CMLVector3Test.assertEquals("centering", new double[] { 0, 0.5, 0.5 },
                operators.get(0).getTranslation(), EPS);
        CMLVector3Test.assertEquals("centering", new double[] { 0.5, 0, 0.5 },
                operators.get(1).getTranslation(), EPS);
        CMLVector3Test.assertEquals("centering", new double[] { 0.5, 0.5, 0 },
                operators.get(2).getTranslation(), EPS);
        centering = fullGroup.getCentering();
        Assert.assertTrue("centering type", CMLCrystal.Centering.F
                .equals(centering));
    }
    /** test. */
    @Test
    public void testGetPointGroupMultiplicity() {
        double eps = 0.0000001;
        CMLSymmetry group = null;
        Point3 p000 = null;
        Point3 p123 = null;
        Point3 p300 = null;
        Point3 p304 = null;
        Point3 p555 = null;
        try {
            p000 = new Point3(new double[] { 0., 0., 0. });
            p123 = new Point3(new double[] { 0.1, 0.2, 0.3 });
            p300 = new Point3(new double[] { 0.3, 0.0, 0.0 });
            p304 = new Point3(new double[] { 0.3, 0.0, 0.4 });
            p555 = new Point3(new double[] { 0.5, 0.5, 0.5 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        int multiplicity = 0;
        try {
            group = new CMLSymmetry(pmmm);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", group.isSpaceGroup());
        multiplicity = group.getPointGroupMultiplicity(p000, eps);
        Assert.assertEquals("multiplicity ", 8, multiplicity);
        multiplicity = group.getPointGroupMultiplicity(p123, eps);
        Assert.assertEquals("multiplicity ", 1, multiplicity);
        multiplicity = group.getPointGroupMultiplicity(p300, eps);
        Assert.assertEquals("multiplicity ", 4, multiplicity);
        multiplicity = group.getPointGroupMultiplicity(p304, eps);
        Assert.assertEquals("multiplicity ", 2, multiplicity);
        multiplicity = group.getPointGroupMultiplicity(p555, eps);
        Assert.assertEquals("multiplicity ", 1, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p555);
        Assert.assertEquals("multiplicity ", 8, multiplicity);
        try {
            group = new CMLSymmetry(p212121);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", group.isSpaceGroup());
        multiplicity = group.getPointGroupMultiplicity(p000, eps);
        Assert.assertEquals("multiplicity ", 1, multiplicity);
    }
    /** test. */
    @Test
    public void testGetSpaceGroupMultiplicity() {
        /*--
         - <symmetry>
         <transform3>
         1.0 0.0 0.0 0.0
         0.0 1.0 0.0 0.0
         0.0 0.0 1.0 0.0
         0.0 0.0 0.0 1.0</transform3>
         <transform3>
         -1.0 0.0 0.0 0.0
         0.0 -1.0 0.0 0.0
         0.0 0.0 1.0 0.0
         0.0 0.0 0.0 1.0</transform3>
         <transform3>
         1.0 0.0 0.0 0.5
         0.0 -1.0 0.0 0.5
         0.0 0.0 -1.0 0.0
         0.0 0.0 0.0 1.0</transform3>
         <transform3>
         -1.0 0.0 0.0 0.5
         0.0 1.0 0.0 0.5
         0.0 0.0 -1.0 0.0
         0.0 0.0 0.0 1.0</transform3>
         </symmetry>
         - <atom id="a1" elementType="O" xFract="0.5" yFract="1.0" zFract="0.8011" x3="7.52975" y3="17.1977" z3="8.689131150000003">
         </atom>
         - <atom id="a2" elementType="O" xFract="0.33569" yFract="0.98239" zFract="0.88892" x3="5.055323555" y3="16.894848503000002" z3="9.641670780000004">
         </atom>
         --*/
        CMLSymmetry group = null;
        Point3 p1 = null;
        Point3 p1a = null;
        Point3 p1b = null;
        Point3 p2 = null;
        Point3 p3a = null;
        Point3 p3b = null;
        try {
            p1 = new Point3(new double[] { 0.0, 0.5, 0.8011 });
            p1a = new Point3(new double[] { 1.0, 0.5, 0.8011 });
            p1b = new Point3(new double[] { 0.0, -0.5, 0.8011 });
            p2 = new Point3(new double[] { 0.33569, 0.98239, 0.88892 });
            p3a = new Point3(new double[] { 0.5, 1.0, 0.8011 });
            p3b = new Point3(new double[] { -0.5, 1.0, 0.8011 });
        } catch (EuclidException e) {
            neverFail(e);
        }
        int multiplicity = 0;
        try {
            group = new CMLSymmetry(p21212);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is group", group.isSpaceGroup());
        multiplicity = group.getSpaceGroupMultiplicity(p1);
        Assert.assertEquals("multiplicity ", 2, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p1a);
        Assert.assertEquals("multiplicity ", 2, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p1b);
        Assert.assertEquals("multiplicity ", 2, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p2);
        Assert.assertEquals("multiplicity ", 1, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p3a);
        Assert.assertEquals("multiplicity ", 2, multiplicity);
        multiplicity = group.getSpaceGroupMultiplicity(p3b);
        Assert.assertEquals("multiplicity ", 2, multiplicity);


        //Ni 0.0000 0.0000 0.5000 0.02783(15) Uani d SU 1 . . Ni
        //'x, y, z'
        //'x+1/2, -y+1/2, z+1/2'
        //'-x, -y, -z'
        //'-x-1/2, y-1/2, -z-1/2'
        Point3 point = new Point3(0.0000, 0.0000, 0.5000);
        CMLSymmetry symmetry = null;
        try {
            symmetry = new CMLSymmetry(
                new String[]{
                        "x, y, z",
                        "x+1/2, -y+1/2, z+1/2",
                        "-x, -y, -z",
                        "-x-1/2, y-1/2, -z-1/2"
                }
            );
        } catch (CMLException e) {
            neverThrow(e);
        }
        int mult = symmetry.getSpaceGroupMultiplicity(point);
        Assert.assertEquals("Multiplicity ", 2, mult);
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.copy()'
     */
    @Test
    public void testCopy() {
        CMLSymmetry symmetry = null;
        try {
            symmetry = new CMLSymmetry(pbca);
        } catch (CMLException e) {
            throw new CMLRuntimeException("bug " + e);
        }
        CMLSymmetry symmetry1 = (CMLSymmetry) symmetry.copy();
        Assert.assertEquals("copy", symmetry.getMatrixElements().size(),
                symmetry1.getMatrixElements().size());
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.CMLSymmetry(List<CMLTransform3>)'
     */
    @Test
    public void testCMLSymmetryListOfCMLTransform3() {
        List<CMLTransform3> list = new ArrayList<CMLTransform3>();
        try {
            list.add(new CMLTransform3("x, y, z"));
            list.add(new CMLTransform3("-x, 1/2+y, -z"));
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLSymmetry symmetry = new CMLSymmetry(list);
        Assert.assertEquals("symmetry", 2, symmetry.getTransform3Elements().size());
        CMLTransform3 tr = symmetry.getTransform3Elements().get(1);
        CMLTransform3Test.assertEquals("symmetry 2",
            new double[]{
            -1.0,  0.0,  0.0,  0.0,
             0.0,  1.0,  0.0,  0.5,
             0.0,  0.0, -1.0,  0.0,
             0.0,  0.0,  0.0,  1.0
        }, tr, EPS);
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLSymmetry.createFromXYZStrings(List<String>)'
     */
    @Test
    public void testCreateFromXYZStrings() {
        List<String> list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, 1/2+y, -z");
        CMLSymmetry symmetry = null;
        try {
            symmetry = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertEquals("symmetry", 2, symmetry.getTransform3Elements().size());
        CMLTransform3 tr = symmetry.getTransform3Elements().get(1);
        CMLTransform3Test.assertEquals("symmetry 2",
            new double[]{
            -1.0,  0.0,  0.0,  0.0,
             0.0,  1.0,  0.0,  0.5,
             0.0,  0.0, -1.0,  0.0,
             0.0,  0.0,  0.0,  1.0
        }, tr, EPS);
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLSymmetry.convolute(CMLSymmetry)'
     */
    @Test
    @Ignore ("doesn't yet work")
    public void testConvolute() {
        List<String> list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, 1/2+y, -z");
        CMLSymmetry symmetry1 = null;
        try {
            symmetry1 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        list = new ArrayList<String>();
        list.add("-x, -y, -z");
        CMLSymmetry symmetry2 = null;
        try {
            symmetry2 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLSymmetry symmetry3 = symmetry1.convolute(symmetry2);
        symmetry3.debug();
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLSymmetry.isEqualTo(CMLSymmetry, double)'
     */
    @Test
    public void testIsEqualTo() {
        List<String> list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, 1/2+y, -z");
        CMLSymmetry symmetry1 = null;
        try {
            symmetry1 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, 1/2+y, -z");
        CMLSymmetry symmetry2 = null;
        try {
            symmetry2 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertTrue("is equal", symmetry1.isEqualTo(symmetry2, EPS));
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLSymmetry.normalizeCrystallographically()'
     */
    @Test
    public void testNormalizeCrystallographically() {
        List<String> list = new ArrayList<String>();
        list.add("x, y, 2+z");
        list.add("1-x, 1/2+y, 3-z");
        CMLSymmetry symmetry1 = null;
        try {
            symmetry1 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }

        list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, 1/2+y, -z");
        CMLSymmetry symmetry2 = null;
        try {
            symmetry2 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        Assert.assertFalse("is equal", symmetry1.isEqualTo(symmetry2, EPS));
        symmetry1.normalizeCrystallographically();

        Assert.assertTrue("is equal", symmetry1.isEqualTo(symmetry2, EPS));
    }
    /**
     * Test method for 'org.xmlcml.cml.element.CMLSymmetry.getCentering()'
     */
    @Test
    public void testGetCentering() {
        List<String> list = new ArrayList<String>();
        list.add("x, y, z");
        list.add("-x, +y, -z");
        list.add("1/2+x, 1/2+y, z");
        list.add("1/2-x, 1/2+y, -z");
        CMLSymmetry symmetry1 = null;
        try {
            symmetry1 = CMLSymmetry.createFromXYZStrings(list);
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLCrystal.Centering center = symmetry1.getCentering();
        Assert.assertEquals("center", CMLCrystal.Centering.C, center);
    }
 }
