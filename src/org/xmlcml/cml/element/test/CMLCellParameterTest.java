package org.xmlcml.cml.element.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLCellParameter;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.test.DoubleTest;

/**
 * test for CellParameter.
 *
 * @author pmr
 *
 */
public class CMLCellParameterTest extends AbstractTest {

    CMLCrystal crystal1;

    String crystal1S = "" + "<crystal z='4' " + CML_XMLNS + ">"
            + "<cellParameter id='scp1' error='0.001 0.002 0.003' units='"
            + U_ANGSTROM + "'" + " type='length' " + CML_XMLNS
            + ">4.500 5.500 6.500</cellParameter>"
            + "<cellParameter id='cp2' error='0.01 0.02 0.03' units='"
            + U_DEGREE + "'" + " type='angle' " + CML_XMLNS
            + ">45.00 55.00 65.00</cellParameter>"
            + "<symmetry id='s1' spaceGroup='P1' " + CML_XMLNS + "/>"
            + "</crystal>" + "";

    CMLElements<CMLCellParameter> cellParameterList = null;

    /**
     * setup.
     *
     * @exception Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        crystal1 = (CMLCrystal) parseValidString(crystal1S);
        cellParameterList = crystal1.getCellParameterElements();
        Assert.assertEquals("setup ", 2, cellParameterList.size());
    }

    /**
     * equality test. true if both args not null and equal within epsilon
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, CMLCellParameter test,
            CMLCellParameter expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + ")", test);
        Assert.assertNotNull("expected should not be null (" + msg + ")",
                expected);
        CMLCellParameterTest.assertEquals(msg, test.getType(), test
                .getXMLContent(), expected, epsilon);
    }

    /**
     * equality test. true if both args not null and equal within epsilon
     *
     * @param msg
     *            message
     * @param type
     *            of parameter
     * @param test
     * @param expected
     * @param epsilon
     */
    public static void assertEquals(String msg, String type, double[] test,
            CMLCellParameter expected, double epsilon) {
        Assert.assertNotNull("test should not be null (" + msg + ")", test);
        Assert.assertEquals("must be of length 3", 3, test.length);
        Assert.assertNotNull("type should not be null (" + msg + ")", type);
        Assert.assertNotNull("expected should not be null (" + msg + ")",
                expected);
        Assert.assertNotNull(
                "expected should not have null type (" + msg + ")", expected
                        .getType());
        Assert.assertEquals("types must be equal", 3, test.length);
        DoubleTest.assertEquals(msg, test, expected.getXMLContent(), epsilon);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.CMLCellParameter(CMLScalar[],
     * Type)'
     */
    @Test
    public void testCMLCellParameterCMLScalarArrayType() {
        CMLScalar[] scalar = new CMLScalar[3];
        scalar[0] = CMLCrystal.createScalar(CMLCellParameter.dictRef[0], 10.,
                U_ANGSTROM);
        scalar[1] = CMLCrystal.createScalar(CMLCellParameter.dictRef[1], 11.,
                U_ANGSTROM);
        scalar[2] = CMLCrystal.createScalar(CMLCellParameter.dictRef[2], 12.,
                U_ANGSTROM);
        CMLCellParameter cp = new CMLCellParameter(scalar,
                CMLCellParameter.Type.LENGTH);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.LENGTH.s, new double[] { 10., 11., 12. },
                cp, EPS);
        Assert.assertEquals("units", U_ANGSTROM, cp.getUnits());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.CMLCellParameter(List<CMLScalar>,
     * Type)'
     */
    @Test
    public void testCMLCellParameterListOfCMLScalarType() {
        List<CMLScalar> scalar = new ArrayList<CMLScalar>();
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[0], 10.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[1], 11.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[2], 12.,
                U_ANGSTROM));
        CMLCellParameter cp = new CMLCellParameter(scalar,
                CMLCellParameter.Type.LENGTH);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.LENGTH.s, new double[] { 10., 11., 12. },
                cp, EPS);
        Assert.assertEquals("units", U_ANGSTROM, cp.getUnits());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.getCellParameter(List<CMLCellParameter>,
     * Type)'
     */
    @Test
    public void testGetCellParameterListOfCMLCellParameterType() {
        List<CMLCellParameter> cpList = new ArrayList<CMLCellParameter>();
        List<CMLScalar> scalar = new ArrayList<CMLScalar>();
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[0], 10.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[1], 11.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[2], 12.,
                U_ANGSTROM));
        cpList.add(new CMLCellParameter(scalar, CMLCellParameter.Type.LENGTH));
        scalar = new ArrayList<CMLScalar>();
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[3], 90.,
                U_DEGREE));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[4], 91.,
                U_DEGREE));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[5], 92.,
                U_DEGREE));
        cpList.add(new CMLCellParameter(scalar, CMLCellParameter.Type.ANGLE));
        CMLCellParameter length = CMLCellParameter.getCellParameter(cpList,
                CMLCellParameter.Type.LENGTH);
        Assert.assertNotNull("length not null", length);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.LENGTH.s, new double[] { 10., 11., 12. },
                length, EPS);
        Assert.assertEquals("units", U_ANGSTROM, length.getUnits());
        CMLCellParameter angle = CMLCellParameter.getCellParameter(cpList,
                CMLCellParameter.Type.ANGLE);
        Assert.assertNotNull("angle", angle);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.ANGLE.s, new double[] { 90., 91., 92. },
                angle, EPS);
        Assert.assertEquals("units", U_DEGREE, angle.getUnits());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.createCMLScalars(List<CMLCellParameter>)'
     */
    @Test
    public void testCreateCMLScalarsListOfCMLCellParameter() {
        List<CMLCellParameter> cpList = new ArrayList<CMLCellParameter>();
        List<CMLScalar> scalar = new ArrayList<CMLScalar>();
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[0], 10.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[1], 11.,
                U_ANGSTROM));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[2], 12.,
                U_ANGSTROM));
        cpList.add(new CMLCellParameter(scalar, CMLCellParameter.Type.LENGTH));
        scalar = new ArrayList<CMLScalar>();
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[3], 90.,
                U_DEGREE));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[4], 91.,
                U_DEGREE));
        scalar.add(CMLCrystal.createScalar(CMLCellParameter.dictRef[5], 92.,
                U_DEGREE));
        cpList.add(new CMLCellParameter(scalar, CMLCellParameter.Type.ANGLE));
        List<CMLScalar> scalarList = CMLCellParameter.createCMLScalars(cpList);
        Assert.assertNotNull("scalarList not null", scalarList);
        Assert.assertEquals("scalar count", 6, scalarList.size());
        Assert
                .assertEquals("scalar 0", 10., scalarList.get(0).getDouble(),
                        EPS);
        Assert.assertEquals("scalar 0", U_ANGSTROM, scalarList.get(0)
                .getUnits());
        Assert
                .assertEquals("scalar 0", 90., scalarList.get(3).getDouble(),
                        EPS);
        Assert.assertEquals("scalar 0", U_DEGREE, scalarList.get(3).getUnits());
        Assert
                .assertEquals("scalar 0", 92., scalarList.get(5).getDouble(),
                        EPS);
        Assert.assertEquals("scalar 0", U_DEGREE, scalarList.get(5).getUnits());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLCellParameter.copy()'
     */
    @Test
    public void testCopy() {
        CMLCellParameter cellParameter1 = (CMLCellParameter) cellParameterList
                .get(0).copy();
        // "<cellParameter id='scp1' error='0.001 0.002 0.003'
        // units='"+U_ANGSTROM+"'" +
        // " "+CML_XMLNS+">4.500 5.500 6.500</cellParameter>"+
        Assert.assertEquals("id", "scp1", cellParameter1.getId());
        Assert.assertEquals("units", U_ANGSTROM, cellParameter1.getUnits());
        DoubleTest.assertEquals("error", new double[] { 0.001, 0.002, 0.003 },
                cellParameter1.getError(), EPS);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.LENGTH.s, new double[] { 4.5, 5.5, 6.5 },
                cellParameter1, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.getCellParameter(CMLElements<CMLCellParameter>,
     * Type)'
     */
    @Test
    public void testGetCellParameterCMLElementsOfCMLCellParameterType() {
        CMLCellParameter cellParameter1 = CMLCellParameter.getCellParameter(
                cellParameterList, CMLCellParameter.Type.LENGTH);
        Assert.assertNotNull("cell parameter not null", cellParameter1);
        Assert.assertEquals("id", "scp1", cellParameter1.getId());
        Assert.assertEquals("units", U_ANGSTROM, cellParameter1.getUnits());
        DoubleTest.assertEquals("error", new double[] { 0.001, 0.002, 0.003 },
                cellParameter1.getError(), EPS);
        CMLCellParameterTest.assertEquals("content",
                CMLCellParameter.Type.LENGTH.s, new double[] { 4.5, 5.5, 6.5 },
                cellParameter1, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLCellParameter.createCMLScalars(CMLElements<CMLCellParameter>)'
     */
    @Test
    public void testCreateCMLScalarsCMLElementsOfCMLCellParameter() {
        List<CMLScalar> cmlScalarList = CMLCellParameter
                .createCMLScalars(cellParameterList);
        Assert.assertNotNull("cell scalars not null", cmlScalarList);
        Assert.assertEquals("scalars", 6, cmlScalarList.size());
        Assert.assertEquals("units", U_ANGSTROM, cmlScalarList.get(0)
                .getUnits());
        Assert.assertEquals("content", 4.5, cmlScalarList.get(0).getDouble(),
                EPS);
        Assert.assertEquals("dataType", XSD_DOUBLE, cmlScalarList.get(0)
                .getDataType());
        Assert.assertEquals("dictRef", "cml:a", cmlScalarList.get(0)
                .getDictRef());
        Assert.assertNotNull("error", cmlScalarList.get(0).getErrorValue());
        Assert.assertEquals("error", 0.001, cmlScalarList.get(0)
                .getErrorValue(), EPS);
    }


}
