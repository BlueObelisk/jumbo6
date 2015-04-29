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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tests atomTree.
 * 
 * @author pm286
 * 
 */
public class AtomTreeTest {

	String dmfS = CMLConstants.S_EMPTY + "<molecule id='m1' " + CMLConstants.CML_XMLNS + ">"
			+ "  <atomArray>"
			+ "    <atom id='a1' elementType='N' hydrogenCount='0'/>"
			+ "    <atom id='a2' elementType='C' hydrogenCount='3'>"
			+ "      <label>C1</label>" + "    </atom>"
			+ "    <atom id='a3' elementType='C' hydrogenCount='3'>"
			+ "      <label>C2</label>" + "    </atom>"
			+ "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
			+ "    <atom id='a5' elementType='O' hydrogenCount='0'/>"
			+ "  </atomArray>" + "  <bondArray>"
			+ "    <bond atomRefs2='a1 a2'/>" + "    <bond atomRefs2='a1 a3'/>"
			+ "    <bond atomRefs2='a1 a4'/>"
			+ "    <bond atomRefs2='a5 a3' order='2'/>" + "  </bondArray>"
			+ "</molecule>" + CMLConstants.S_EMPTY;

	CMLMolecule dmf = null;

	String cnoS = CMLConstants.S_EMPTY + "<molecule id='m2' " + CMLConstants.CML_XMLNS + ">"
			+ "  <atomArray>"
			+ "    <atom id='a1' elementType='N' hydrogenCount='1'/>"
			+ "    <atom id='a2' elementType='C' hydrogenCount='2'>"
			+ "      <label>C1</label>" + "    </atom>"
			+ "    <atom id='a3' elementType='O' hydrogenCount='0'>"
			+ "      <label>C2</label>" + "    </atom>" + "  </atomArray>"
			+ "  <bondArray>" + "    <bond atomRefs2='a1 a2'/>"
			+ "    <bond atomRefs2='a2 a3'/>" + "    <bond atomRefs2='a1 a3'/>"
			+ "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

	CMLMolecule cno = null;

	/**
	 * set up.
	 * 
	 * @exception Exception
	 */
	@Before
	public void setUp() throws Exception {
		dmf = (CMLMolecule)CMLXOMTestUtils.parseValidString(dmfS);
		cno = (CMLMolecule)CMLXOMTestUtils.parseValidString(cnoS);
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.AtomTree(CMLAtom,
	 * CMLAtom)'
	 */
	@Test
	public void testAtomTreeCMLAtomCMLAtom() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0), dmf.getAtom(1));
		atomTree.expandTo(2);
		Assert.assertEquals("new AtomTree", AS.C.value, atomTree.toString());
		atomTree = new AtomTree(dmf.getAtom(1), dmf.getAtom(0));
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C(O))", atomTree.toString());

		atomTree = new AtomTree(cno.getAtom(0), cno.getAtom(1));
		atomTree.expandTo(2);
		Assert.assertEquals("new AtomTree", "C(O(N))", atomTree.toString());
		atomTree = new AtomTree(dmf.getAtom(1), dmf.getAtom(0));
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C(O))", atomTree.toString());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.AtomTree(CMLAtom)'
	 */
	@Test
	public void testAtomTreeCMLAtom() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
		atomTree = new AtomTree(dmf.getAtom(1));
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "C(N(C)(C(O)))", atomTree
				.toString());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.setUseCharge(boolean)'
	 */
	@Test
	public void testSetUseCharge() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.expandTo(3);
		atomTree.setUseCharge(true);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.setUseLabel(boolean)'
	 */
	@Test
	public void testSetUseLabel() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.setUseLabel(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
		atomTree.setUseLabel(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree",
				"N(C)(C)(C{C1})(C{C1})(C{C2}(O))(C{C2}(O))", atomTree.toString());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.AtomTree.setUseImplicitHydrogens(boolean)'
	 */
	@Test
	public void testSetUseImplicitHydrogens() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.setUseImplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
		atomTree.setUseImplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(CH)(CH)(CH3)(CH3)(CH3(O))(CH3(O))",
				atomTree.toString());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.AtomTree.setUseExplicitHydrogens(boolean)'
	 */
	@Test
	public void testSetUseExplicitHydrogens() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C)(C)(C(O))(C(O))",
				atomTree.toString());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.expandTo(int)'
	 */
	@Test
	public void testExpandTo() {
		AtomTree atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(0);
		Assert.assertEquals("new AtomTree", "N", atomTree.toString());
		atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.expandTo(1);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C)", atomTree.toString());
		atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.expandTo(2);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
		atomTree = new AtomTree(dmf.getAtom(0));
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree
				.toString());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomTree.compareTo(Object)'
	 */
	@Test
	public void testCompareTo() {
		AtomTree atomTree0 = new AtomTree(dmf.getAtom(0));
		atomTree0.expandTo(3);
		AtomTree atomTree1 = new AtomTree(dmf.getAtom(1));
		atomTree1.expandTo(3);
		// LOG.debug("1: "+atomTree1.toString());
		Assert.assertTrue("compare 0 1 ", atomTree0.compareTo(atomTree1) > 0);
		AtomTree atomTree2 = new AtomTree(dmf.getAtom(2));
		atomTree2.expandTo(3);
		// LOG.debug("2 "+atomTree2.toString());
		Assert.assertEquals("compare 0 1 ", -1, atomTree1.compareTo(atomTree2));
	}

	/**
	 * typical example with symmetry.
	 * 
	 */
	@Test
	public void testPhenyl() {
		String phenylS = "<molecule " + CMLConstants.CML_XMLNS + ">" + "  <atomArray>"
				+ "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a7' elementType='R'/>" + "  </atomArray>"
				+ "  <bondArray>" + "    <bond atomRefs2='a1 a2' order='A'/>"
				+ "    <bond atomRefs2='a1 a6' order='A'/>"
				+ "    <bond atomRefs2='a1 a7' order='1'/>"
				+ "    <bond atomRefs2='a3 a2' order='A'/>"
				+ "    <bond atomRefs2='a3 a4' order='A'/>"
				+ "    <bond atomRefs2='a4 a5' order='A'/>"
				+ "    <bond atomRefs2='a5 a6' order='A'/>" + "  </bondArray>"
				+ "</molecule>";
		CMLMolecule phenyl = null;
		try {
			phenyl = (CMLMolecule) new CMLBuilder().parseString(phenylS);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		AtomTree atomTree = new AtomTree(phenyl.getAtom(6));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "R(C(C(C))(C(C)))", atomTree
				.toString());
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "R(C(C(C))(C(C)))(C(C(C))(C(C)))",
				atomTree.toString());
	}

	/**
	 * typical example with symmetry.
	 * 
	 */
	@Test
	public void testAnisole() {
		String anisoleS = "<molecule " + CMLConstants.CML_XMLNS + ">" + "  <atomArray>"
				+ "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a7' elementType='O'/>"
				+ "    <atom id='a8' elementType='C' hydrogenCount='3'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='A'/>"
				+ "    <bond atomRefs2='a1 a6' order='A'/>"
				+ "    <bond atomRefs2='a1 a7' order='1'/>"
				+ "    <bond atomRefs2='a3 a2' order='A'/>"
				+ "    <bond atomRefs2='a3 a4' order='A'/>"
				+ "    <bond atomRefs2='a4 a5' order='A'/>"
				+ "    <bond atomRefs2='a5 a6' order='A'/>"
				+ "    <bond atomRefs2='a8 a7' order='1'/>" + "  </bondArray>"
				+ "</molecule>";
		CMLMolecule anisole = null;
		try {
			anisole = (CMLMolecule) new CMLBuilder().parseString(anisoleS);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		AtomTree atomTree = new AtomTree(anisole.getAtom(6));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree", "O(C)(C(C(C))(C(C)))", atomTree
				.toString());
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree",
				"O(C)(C)(C(C(C))(C(C)))(C(C(C))(C(C)))", atomTree.toString());
	}

	/**
	 * typical example with symmetry.
	 * 
	 */
	@Test
	public void testPhenylCyclohexane() {
		String phcS = "<molecule " + CMLConstants.CML_XMLNS + ">" + "  <atomArray>"
				+ "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a7' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a8' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a9' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a10' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a11' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a12' elementType='C' hydrogenCount='2'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='A'/>"
				+ "    <bond atomRefs2='a1 a6' order='A'/>"
				+ "    <bond atomRefs2='a3 a2' order='A'/>"
				+ "    <bond atomRefs2='a3 a4' order='A'/>"
				+ "    <bond atomRefs2='a4 a5' order='A'/>"
				+ "    <bond atomRefs2='a5 a6' order='A'/>"
				+ "    <bond atomRefs2='a1 a7' order='1'/>"
				+ "    <bond atomRefs2='a7 a12' order='1'/>"
				+ "    <bond atomRefs2='a7 a8' order='1'/>"
				+ "    <bond atomRefs2='a8 a9' order='1'/>"
				+ "    <bond atomRefs2='a9 a10' order='1'/>"
				+ "    <bond atomRefs2='a10 a11' order='1'/>"
				+ "    <bond atomRefs2='a11 a12' order='1'/>"
				+ "  </bondArray>" + "</molecule>";
		CMLMolecule phc = null;
		try {
			phc = (CMLMolecule) new CMLBuilder().parseString(phcS);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		AtomTree atomTree = new AtomTree(phc.getAtom(6));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree",
				"C(C(C(C)))(C(C(C)))(C(C(C))(C(C)))", atomTree.toString());
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert
				.assertEquals(
						"new AtomTree",
						"C(C(C(C)))(C(C(C)))(C(C(C)))(C(C(C)))(C(C(C))(C(C)))(C(C(C))(C(C)))",
						atomTree.toString());
		atomTree = new AtomTree(phc.getAtom(0));
		atomTree.setUseExplicitHydrogens(false);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree",
				"C(C(C(C)))(C(C(C)))(C(C(C))(C(C)))", atomTree.toString());
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert
				.assertEquals(
						"new AtomTree",
						"C(C(C(C)))(C(C(C)))(C(C(C)))(C(C(C)))(C(C(C))(C(C)))(C(C(C))(C(C)))",
						atomTree.toString());
	}

	/**
	 * typical example with symmetry.
	 * 
	 */
	@Test
	public void testAnisoleH() {
		String anisoleS = "<molecule " + CMLConstants.CML_XMLNS + ">" + "  <atomArray>"
				+ "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a7' elementType='O'/>"
				+ "    <atom id='a8' elementType='C' hydrogenCount='3'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='A'/>"
				+ "    <bond atomRefs2='a1 a6' order='A'/>"
				+ "    <bond atomRefs2='a1 a7' order='1'/>"
				+ "    <bond atomRefs2='a3 a2' order='A'/>"
				+ "    <bond atomRefs2='a3 a4' order='A'/>"
				+ "    <bond atomRefs2='a4 a5' order='A'/>"
				+ "    <bond atomRefs2='a5 a6' order='A'/>"
				+ "    <bond atomRefs2='a8 a7' order='1'/>" + "  </bondArray>"
				+ "</molecule>";

		CMLMolecule anisole = null;
		try {
			anisole = (CMLMolecule) new CMLBuilder().parseString(anisoleS);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(anisole);
		moleculeTool
				.expandImplicitHydrogens(HydrogenControl.ADD_TO_EXPLICIT_HYDROGENS);
		AtomTree atomTree = new AtomTree(anisole.getAtom(6));
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert.assertEquals("new AtomTree",
				"O(C(C(C)(H))(C(C)(H)))(C(H)(H)(H))", atomTree.toString());

		String methoxycyclohexeneS = "<molecule " + CMLConstants.CML_XMLNS + ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a4' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a7' elementType='O'/>"
				+ "    <atom id='a8' elementType='C' hydrogenCount='3'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='1'/>"
				+ "    <bond atomRefs2='a1 a6' order='1'/>"
				+ "    <bond atomRefs2='a1 a7' order='1'/>"
				+ "    <bond atomRefs2='a3 a2' order='1'/>"
				+ "    <bond atomRefs2='a3 a4' order='1'/>"
				+ "    <bond atomRefs2='a4 a5' order='1'/>"
				+ "    <bond atomRefs2='a5 a6' order='2'/>"
				+ "    <bond atomRefs2='a8 a7' order='1'/>" + "  </bondArray>"
				+ "</molecule>";
		CMLMolecule methoxycyclohexene = null;
		try {
			methoxycyclohexene = (CMLMolecule) new CMLBuilder()
					.parseString(methoxycyclohexeneS);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		moleculeTool = MoleculeTool.getOrCreateTool(methoxycyclohexene);
		moleculeTool
				.expandImplicitHydrogens(HydrogenControl.ADD_TO_EXPLICIT_HYDROGENS);
		atomTree = new AtomTree(methoxycyclohexene.getAtom(6));
		atomTree.setUseExplicitHydrogens(true);
		atomTree.expandTo(3);
		Assert
				.assertEquals("new AtomTree",
						"O(C(C(C)(H))(C(C)(H)(H))(H))(C(H)(H)(H))", atomTree
								.toString());
	}

	@Test
	public void getLevelOfAtomTreeString() {
		String s = "O(C(C(C)(H))(C(C)(H)(H))(H))(C(H)(H)(H))";
		int l = AtomTree.getLevelOfAtomTreeString(s);
		Assert.assertEquals("l", 3, l);
	}

	@Test
	public void trimToLevel() {
		String s = "O(C(C(C)(H))(C(C)(H)(H))(H))(C(H)(H)(H))";
		Assert.assertEquals("l3", 3, AtomTree.getLevelOfAtomTreeString(s));
		String ss = AtomTree.trimToLevel(s, 4); // no effect
		Assert.assertEquals("l3", s, ss);
		ss = AtomTree.trimToLevel(s, 3); // no effect
		Assert.assertEquals("l3", s, ss);
		ss = AtomTree.trimToLevel(s, 2);
		Assert.assertEquals("l2", 2, AtomTree.getLevelOfAtomTreeString(ss));
		Assert.assertEquals("l2", "O(C(C)(C)(H))(C(H)(H)(H))", ss);
		ss = AtomTree.trimToLevel(s, 1);
		Assert.assertEquals("l1", 1, AtomTree.getLevelOfAtomTreeString(ss));
		Assert.assertEquals("l1", "O(C)(C)", ss);
	}
}
