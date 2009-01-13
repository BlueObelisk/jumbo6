package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.util.TestUtils;


/**
 * test AngleTool.
 * 
 * @author pmr
 * 
 */
public class AngleToolTest {
	private static Logger LOG = Logger.getLogger(AngleToolTest.class);

	AngleTool tool1 = null;
	String s1 = S_EMPTY + "<cml " + CMLConstants.CML_XMLNS + ">" + " <molecule id='m1'>"
			+ "  <atomArray>" + "   <atom id='a1' x3='1.0' y3='0.0' z3='0.0'/>"
			+ "   <atom id='a2' x3='0.0' y3='0.0' z3='0.0'/>"
			+ "   <atom id='a3' x3='0.0' y3='0.0' z3='2.0'/>"
			+ "  </atomArray>" + "  <bondArray>"
			+ "    <bond atomRefs2='a1 a2'/>" + "    <bond atomRefs2='a3 a2'/>"
			+ "  </bondArray>" + " </molecule>"
			+ " <angle id='aa0' atomRefs3='a1 a2 a3'/>"
			+ " <angle id='aa1' atomRefs3='a2 a1 a3'/>"
			+ " <angle id='aa2' atomRefs3='a1 a2 a4'/>" + "</cml>" + S_EMPTY;

	CMLAngle angle0;
	CMLAngle angle1;
	CMLAngle angle2;
	CMLMolecule molecule1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Element element = null;
		try {
			element =TestUtils.parseValidString(s1);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("EXC " + e);
			throw e;
		}
		CMLCml cml = (CMLCml) element;
		molecule1 = (CMLMolecule) cml.getChildCMLElements("molecule").get(0);
		angle0 = (CMLAngle) cml.getChildCMLElements("angle").get(0);
		angle1 = (CMLAngle) cml.getChildCMLElements("angle").get(1);
		angle2 = (CMLAngle) cml.getChildCMLElements("angle").get(2);
	}

	String mol1S = S_EMPTY
			+ "<molecule "
			+ CMLConstants.CML_XMLNS
			+ ">"
			+ " <atomArray>"
			+ "  <atom id='a1' xFract='0.1'  yFract='0.2'  zFract='0.3' elementType='O'/>"
			+ "  <atom id='a2' xFract='0.15' yFract='0.25' zFract='0.35' elementType='H'/>"
			+ "  <atom id='a3' xFract='0.15' yFract='0.15' zFract='0.25' elementType='H'/>"
			+ " </atomArray>" + "</molecule>" + S_EMPTY;

	CMLMolecule mol1;

	String crystal1S = S_EMPTY + "<crystal " + CMLConstants.CML_XMLNS + ">"
			+ "  <cellParameter type='length'>10. 11. 12.</cellParameter>"
			+ "  <cellParameter type='angle'>90. 90. 90.</cellParameter>"
			+ "</crystal>" + S_EMPTY;

	CMLCrystal crystal1;

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLAngle#adjustCoordinates(org.xmlcml.cml.element.CMLMolecule)}
	 * .
	 */
	@Test
	public final void testAdjustCoordinatesCMLMolecule() {
		angle0.setXMLContent(70.0);
		double d = angle0.getCalculatedAngle(molecule1);
		Assert.assertEquals("ang0 ", 90.0, d, EPS);
		AngleTool.getOrCreateTool(angle0).adjustCoordinates(molecule1);
		d = angle0.getCalculatedAngle(molecule1);
		Assert.assertEquals("ang0 ", 70.0, d, EPS);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLAngle#adjustCoordinates(org.xmlcml.euclid.Angle, org.xmlcml.cml.element.CMLAtomSet, org.xmlcml.cml.element.CMLAtomSet)}
	 * .
	 */
	@Test
	public final void testAdjustCoordinatesAngleCMLAtomSetCMLAtomSet() {
		List<CMLAtom> atomList = molecule1.getAtoms();
		List<CMLAtom> moveableList = new ArrayList<CMLAtom>();
		moveableList.add(atomList.get(1));
		moveableList.add(atomList.get(2));
		CMLAtomSet moveableAtomSet = CMLAtomSet.createFromAtoms(moveableList);
		double d = angle0.getCalculatedAngle(molecule1);
		Assert.assertEquals("ang0 ", 90.0, d, EPS);
		AngleTool.getOrCreateTool(angle0).adjustCoordinates(
				new Angle(45., Angle.Units.DEGREES),
				MoleculeTool.getOrCreateTool(molecule1).getAtomSet(),
				moveableAtomSet);
		d = angle0.getCalculatedAngle(molecule1);
		Assert.assertEquals("ang0 ", 45.0, d, EPS);
	}
}
