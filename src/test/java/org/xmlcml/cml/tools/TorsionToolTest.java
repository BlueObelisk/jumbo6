/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.cml.testutil.CMLAssert;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Angle;

/**
 * test AngleTool.
 * 
 * @author pmr
 * 
 */
public class TorsionToolTest {

	String s1 = "" + "<cml " + CMLConstants.CML_XMLNS + ">" + " <molecule id='m1'>"
			+ "  <atomArray>" + "   <atom id='a1' x3='1.0' y3='0.0' z3='0.0'/>"
			+ "   <atom id='a2' x3='0.0' y3='0.0' z3='0.0'/>"
			+ "   <atom id='a3' x3='0.0' y3='0.0' z3='2.0'/>"
			+ "   <atom id='a4' x3='0.0' y3='1.0' z3='2.0'/>"
			+ "   <atom id='a5' x3='-1.0' y3='1.0' z3='2.0'/>"
			+ "  </atomArray>" + "  <bondArray>"
			+ "   <bond id='b12' atomRefs2='a1 a2'/>"
			+ "   <bond id='b23' atomRefs2='a2 a3'/>"
			+ "   <bond id='b34' atomRefs2='a3 a4'/>"
			+ "   <bond id='b45' atomRefs2='a4 a5'/>" + "  </bondArray>"
			+ " </molecule>" + " <torsion id='aa0' atomRefs4='a1 a2 a3 a4'/>"
			+ " <torsion id='aa1' atomRefs4='a2 a3 a4 a5'/>"
			+ " <torsion id='nonexistent' atomRefs4='a3 a4 a5 a6'/>" + "</cml>"
			+ "";

	CMLTorsion torsion0;
	CMLTorsion torsion1;
	CMLTorsion torsion2;

	CMLMolecule molecule1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		CMLCml cml = (CMLCml)JumboTestUtils.parseValidString(s1);
		molecule1 = (CMLMolecule) cml.getChildCMLElements("molecule").get(0);
		torsion0 = (CMLTorsion) cml.getChildCMLElements("torsion").get(0);
		torsion1 = (CMLTorsion) cml.getChildCMLElements("torsion").get(1);
		torsion2 = (CMLTorsion) cml.getChildCMLElements("torsion").get(2);
	}

	/**
	 * test
	 * 'org.xmlcml.cml.element.CMLTorsion.getTransformationToNewTorsion(Angle,
	 * CMLAtomSet)'
	 */
	@Test
	public void testTetTransformationToNewTorsionAngleCMLMolecule() {
		double t = torsion0.getCalculatedTorsion(molecule1);
		Assert.assertEquals("calculated torsion", t, 90.,  CC.EPS);
		Angle angle = new Angle(Math.PI / 3);
		TorsionTool torsionTool = TorsionTool.getOrCreateTool(torsion0);
		CMLTransform3 transform = torsionTool.getTransformationToNewTorsion(
				angle, molecule1);

		CMLTransform3 expected = null;
		double SQR32 = Math.sqrt(3. / 4.);
		try {
			expected = new CMLTransform3(new double[] { SQR32, 0.5, 0.0, 0.0,
					-0.5, SQR32, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
					1.0, });
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		double epsilon = 0.000001;
		CMLAssert.assertEquals("after transform", transform, expected, epsilon);
	}

	/**
	 * test 'org.xmlcml.cml.element.CMLTorsion.resetTorsion(Angle, CMLAtomSet,
	 * CMLAtomSet)'
	 */
	@Test
	public void testSetTorsionAngleCMLAtomSetCMLAtomSet() {
		List<CMLBond> bonds = molecule1.getBonds();
		CMLAtomSet moveableSet = new CMLAtomSet(molecule1, new String[] { "a3",
				"a4", "a5" });
		double t0 = torsion0.getCalculatedTorsion(molecule1);
		Assert.assertEquals("calculated torsion", 90., t0,  CC.EPS);
		double t1 = torsion1.getCalculatedTorsion(molecule1);
		Assert.assertEquals("calculated torsion", 90., t1,  CC.EPS);
		double b45 = bonds.get(0).getBondLength(CoordinateType.CARTESIAN);
		Assert.assertEquals("calculated b45", 1., b45,  CC.EPS);
		Angle angle = new Angle(Math.PI / 3);

		CMLAtomSet atomSet = new CMLAtomSet(molecule1);
		TorsionTool.getOrCreateTool(torsion0).adjustCoordinates(angle, atomSet,
				moveableSet);
		t0 = torsion0.getCalculatedTorsion(molecule1);
		// Assert.assertEquals("new torsion", 60., t0,  CC.EPS);
		// this should be the same...
		t1 = torsion1.getCalculatedTorsion(molecule1);
		Assert.assertEquals("unaffected torsion", 90., t1,  CC.EPS);
		b45 = bonds.get(0).getBondLength(CoordinateType.CARTESIAN);
		Assert.assertEquals("calculated b45", 1., b45,  CC.EPS);

		String filename = "org/xmlcml/cml/tools/examples/molecules/geom1/coxy.xml";
		CMLMolecule molecule2 = (CMLMolecule)JumboTestUtils.parseValidFile(filename);
		CMLTorsion torsion22 = new CMLTorsion();
		torsion22.setAtomRefs4(new String[] { "a27", "a42", "a28", "a31" });
		CMLAtomSet atomSet22 = new CMLAtomSet(molecule2);
		CMLAtomSet moveableSet22 = new CMLAtomSet(molecule2, new String[] {
				"a28", "a29", "a30", "a31", "a32", "a33", "a34" });
		double t22 = torsion22.getCalculatedTorsion(molecule2);
		Assert
				.assertEquals("calculated torsion", -177.96182350731638, t22,
						 CC.EPS);
		angle = new Angle(Math.PI / 3);

		TorsionTool.getOrCreateTool(torsion22).adjustCoordinates(angle,
				atomSet22, moveableSet22);
		t22 = torsion22.getCalculatedTorsion(molecule2);

		Assert.assertEquals("new torsion", 60., t22, 0.000000001);

	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLTorsion.getCalculatedTorsion(CMLAtomSet)'
	 */
	@Test
	public void testGetCalculatedTorsionCMLAtomSet() {
		double t = torsion0.getCalculatedTorsion(molecule1);
		Assert.assertEquals("calculated torsion", t, 90.,  CC.EPS);

	}

}