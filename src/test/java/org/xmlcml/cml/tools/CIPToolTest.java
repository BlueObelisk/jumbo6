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

/**
 * 
 */
package org.xmlcml.cml.tools;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;
/**
 * @author pm286
 * 
 */
public class CIPToolTest {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(CIPToolTest.class);

	/**
	 * 
	 */
	@Test
	public void testgetBreadthFirstCIPTree() {
		String molS = "" +
		"<molecule " + CMLConstants.CML_XMLNS + " >" +
		"  <atomArray>" +
		"    <atom id='a1' elementType='C'/>" +
		"    <atom id='a2' elementType='N'/>" +
		"    <atom id='a3' elementType='C'/>" +
		"    <atom id='a4' elementType='O'/>" +
		"    <atom id='a5' elementType='C'/>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond id='b12' atomRefs2='a1 a2' order='S'/>" +
		"    <bond id='b23' atomRefs2='a2 a3' order='S'/>" +
		"    <bond id='b34' atomRefs2='a3 a4' order='S'/>" +
		"    <bond id='b25' atomRefs2='a2 a5' order='S'/>" +
		"  </bondArray>" +
		"</molecule>";
		CMLMolecule mol = (CMLMolecule)CMLXOMTestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = 
			"<node parent='a1' id='a2' atomicNumber='7'>" +
			  "<node parent='a2' id='a3' atomicNumber='6'>" +
			    "<node parent='a3' id='a4' atomicNumber='8'/>" +
			  "</node>" +
			  "<node parent='a2' id='a5' atomicNumber='6'/>" +
			"</node>";
		CMLXOMTestUtils.assertEqualsCanonically("node tree", CMLXOMTestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	public void testgetBreadthFirstCIPTree2() {
		String molS = "" +
		"<molecule " + CMLConstants.CML_XMLNS + " >" +
		"  <atomArray>" +
		"    <atom id='a1' elementType='C'/>" +
		"    <atom id='a2' elementType='C'/>" +
		"    <atom id='a3' elementType='C'/>" +
		"    <atom id='a4' elementType='C'/>" +
		"    <atom id='a5' elementType='C'/>" +
		"    <atom id='a6' elementType='C'/>" +
		"    <atom id='a7' elementType='C'/>" +
		"    <atom id='a8' elementType='C'/>" +
		"    <atom id='a9' elementType='C'/>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond id='b12' atomRefs2='a1 a2' order='S'/>" +
		"    <bond id='b23' atomRefs2='a2 a3' order='S'/>" +
		"    <bond id='b34' atomRefs2='a3 a4' order='S'/>" +
		"    <bond id='b45' atomRefs2='a4 a5' order='S'/>" +
		"    <bond id='b56' atomRefs2='a5 a6' order='S'/>" +
		"    <bond id='b67' atomRefs2='a6 a7' order='S'/>" +
		"    <bond id='b78' atomRefs2='a7 a8' order='S'/>" +
		"    <bond id='b28' atomRefs2='a2 a7' order='S'/>" +
		"  </bondArray>" +
		"</molecule>";
		CMLMolecule mol = (CMLMolecule)CMLXOMTestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atomicNumber='6'>" +
		  "<node parent='a2' id='a7' atomicNumber='6'>" +
		    "<node parent='a7' id='a6' atomicNumber='6'>" +
		      "<node parent='a6' id='a5' atomicNumber='6'>" +
		        "<node parent='a5' id='a4' atomicNumber='6'>" +
		          "<node parent='a4' id='a3' atomicNumber='6'>" +
		            "<node parent='a3' id='a2_ghost' atomicNumber='6' ghost='true'/>" +
		          "</node>" +
		        "</node>" +
		      "</node>" +
		    "</node>" +
		    "<node parent='a7' id='a8' atomicNumber='6'/>" +
		  "</node>" +
		  "<node parent='a2' id='a3' atomicNumber='6'>" +
		    "<node parent='a3' id='a4' atomicNumber='6'>" +
		      "<node parent='a4' id='a5' atomicNumber='6'>" +
		        "<node parent='a5' id='a6' atomicNumber='6'>" +
		          "<node parent='a6' id='a7' atomicNumber='6'>" +
		            "<node parent='a7' id='a8' atomicNumber='6'/>" +
		            "<node parent='a7' id='a2_ghost' atomicNumber='6' ghost='true'/>" +
		          "</node>" +
		        "</node>" +
		      "</node>" +
		    "</node>" +
		  "</node>" +
		"</node>";
		CMLXOMTestUtils.assertEqualsCanonically("node tree", CMLXOMTestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	public void testgetBreadthFirstCIPTree1() {
		String molS = "" +
		"<molecule " + CMLConstants.CML_XMLNS + " >" +
		"  <atomArray>" +
		"    <atom id='a1' elementType='C'/>" +
		"    <atom id='a2' elementType='C'/>" +
		"    <atom id='a3' elementType='C'/>" +
		"    <atom id='a4' elementType='C'/>" +
		"    <atom id='a5' elementType='C'/>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond id='b12' atomRefs2='a1 a2' order='S'/>" +
		"    <bond id='b23' atomRefs2='a2 a3' order='D'/>" +
		"    <bond id='b34' atomRefs2='a3 a4' order='S'/>" +
		"    <bond id='b25' atomRefs2='a2 a5' order='S'/>" +
		"  </bondArray>" +
		"</molecule>";
		CMLMolecule mol = (CMLMolecule)CMLXOMTestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atomicNumber='6'>" +
		  "<node parent='a2' id='a3' atomicNumber='6'>" +
		    "<node parent='a3' id='a4' atomicNumber='6'/>" +
		    "<node parent='a3' id='a2_ghost' atomicNumber='6' ghost='true'/>" +
		  "</node>" +
		  "<node parent='a2' id='a5' atomicNumber='6'/>" +
		  "<node parent='a2' id='a3_ghost' atomicNumber='6' ghost='true'/>" +
		"</node>";
		CMLXOMTestUtils.assertEqualsCanonically("node tree", CMLXOMTestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	public void testgetBreadthFirstCIPTree3() {
		CMLMolecule mol = parseSMILES("[R][C]([N])([F])([O])");
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atomicNumber='6'>" +
		  "<node parent='a2' id='a4' atomicNumber='9'/>" +
		  "<node parent='a2' id='a5' atomicNumber='8'/>" +
		  "<node parent='a2' id='a3' atomicNumber='7'/>" +
		"</node>";
		CMLXOMTestUtils.assertEqualsCanonically("node tree", CMLXOMTestUtils.parseValidString(expectedS), elem1, true);
		mol = parseSMILES("[R][C]([O])([N])([F])");
		st = new CIPTool(mol);
		elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		expectedS = ""+
		"<node parent='a1' id='a2' atomicNumber='6'>" +
		  "<node parent='a2' id='a5' atomicNumber='9'/>" +
		  "<node parent='a2' id='a3' atomicNumber='8'/>" +
		  "<node parent='a2' id='a4' atomicNumber='7'/>" +
		"</node>";
		CMLXOMTestUtils.assertEqualsCanonically("node tree", CMLXOMTestUtils.parseValidString(expectedS), elem1, true);
	}
	
	private CMLMolecule parseSMILES(String smiles) {
		SMILESTool st = new SMILESTool();
		st.parseSMILES(smiles);
		return st.getMolecule();
	}
	@Test
	public void testWikipedia0() {
		SMILESTool st = new SMILESTool();
		st.parseSMILES("[R]O[H]");
		CMLMolecule mol1 = st.getMolecule();
		String molS = ""+
			"<molecule cmlx:explicitHydrogens='true' xmlns='http://www.xml-cml.org/schema' xmlns:cmlx='http://www.xml-cml.org/schema/cmlx'>" +
			  "<atomArray>" +
			    "<atom id='a1' elementType='R' formalCharge='0'/>" +
			    "<atom id='a2' elementType='O'/>" +
			    "<atom id='a3' elementType='H' formalCharge='0'/>" +
			  "</atomArray>" +
			  "<bondArray>" +
			    "<bond atomRefs2='a1 a2' id='a1_a2' order='S'/>" +
			    "<bond atomRefs2='a2 a3' id='a2_a3' order='S'/>" +
			  "</bondArray>" +
			"</molecule>";
		CMLXOMTestUtils.assertEqualsCanonically("mol", CMLXOMTestUtils.parseValidString(molS), mol1, true);
	}
	
	@Test
	public void testWikipedia1() {
		// -OH > -CH3
		assertDecreasing("[R]O[H]", "[R]C([H])([H])([H])");
	}
	
	@Test
	public void testWikipedia2() {
		// -CH(OH)CH3 > CH2OH
		assertDecreasing("[R]C([H])(O([H]))C([H])([H])([H])", "[R]C([H])([H])(O([H]))");
	}
	
	@Test
	public void testWikipedia3() {
		// -CH(OCH3)CH3 > CH(OH)CH2OH
		assertDecreasing("[R]C([H])(O(C([H])([H])([H])))(C([H])([H])([H]))", 
				"[R]C([H])(O([H]))[C]([H])([H])(O([H]))");
	}
	
	@Test
	public void testWikipedia4() {
		// -CH(CH2F)OCH3 > CH(CH3)OCH2F
		assertDecreasing(
			"[R]C([H])(C([H])([H])([F]))[O](C([H])([H])([H]))", 
			"[R]C([H])(C([H])([H])([H]))[O](C([H])([H])F)");
	}
	

	@Test
	public void testWikipedia5() {
//	    * -CH=O > -CH2OH. The distance-2 lists are (O, ghost O, H) and (O, H, H); 
//		the ghost oxygen outranks the hydrogen.
		assertDecreasing(
			"[R]C([H])=[O]", 
			"[R]C([H])([H])([O]([H]))");
	}
	
	@Test
	public void testWikipedia6() {
//	    * -CH(OCH3)2 > -CH=O. The distance-2 lists are (O, O, H) and (O, ghost O, H). 
//		This is a tie, but at distance 3, nothing else is attached to the ghost oxygen, 
//		so it loses to the second oxygen of -CH(OCH3)2; the lists are ((C), (C), ( )) and 
//		((ghost C), ( ), ( )).
		assertDecreasing(
		"[R]C([H])([O]([C]([H])([H])([H])))([O]([C]([H])([H])([H])))",
		"[R]C([H])=[O]");
	}
	
	@Test
	public void testWikipedia7() {
//	    * -CH=CH2 > -CH(CH3)2. The distance-2 lists are (C, ghost C, H) and (C, C, H), a tie. 
//		However, at distance 3, the lists are ((ghost C, H, H), ( ), ( )) and ((H, H, H), (H, H, H), ( )); 
//		the ghost carbon representing the reverse direction of -CH=CH2's double bond outranks -CH(CH3)2's 
//		hydrogens.
		assertDecreasing(
			"[R]C([H])=[C]([H])([H])", 
			"[R]C([H])([C]([H])([H])([H]))([C]([H])([H])([H]))");
	}
	
	@Test
	public void testDaniel1() {
//	R-[CH](C(F)(F)F))C([H])([H])[H] == R-[CH](C([H])([H])[H])C(F)(F)F
		assertEquals(
				"[R][CH](C(F)(F)F)C([H])([H])[H]", 
				"[R][CH](C([H])([H])[H])C(F)(F)F");
	}
	
	@Test
	public void testDaniel2() {
//		R-[CH](S[H])S([H])([H])[H] vs R-[CH](S([H])[H])S([H])[H]
		assertEquals(
				"[R][CH](S[H])S([H])([H])[H]", 
				"[R][CH](S([H])[H])S([H])[H]");
	}
	                                                      
	private void assertEquals(String smiles1, String smiles2) {
		Element root1 = getOrderedBreadthFirstTree(smiles1);
//		CMLUtil.debug(root1, smiles1);
		Element root2 = getOrderedBreadthFirstTree(smiles2);
//		CMLUtil.debug(root2, smiles2);
		int compare = CIPTool.compareChildrenRecursively(root1, root2);
		Assert.assertTrue("compare "+compare, compare == 0);
	}

	private void assertDecreasing(String smiles1, String smiles2) {
		Element root1 = getOrderedBreadthFirstTree(smiles1);
//		CMLUtil.debug(root1, smiles1);
		Element root2 = getOrderedBreadthFirstTree(smiles2);
//		CMLUtil.debug(root2, smiles2);
		int compare = CIPTool.compareChildrenRecursively(root1, root2);
		Assert.assertTrue("compare "+compare, compare > 0);
	}

	/**
	 * @param largerS
	 */
	private Element getOrderedBreadthFirstTree(String largerS) {
		SMILESTool st = new SMILESTool();
		st.parseSMILES(largerS);
		CMLMolecule mol = st.getMolecule();
//		mol.debug();
		CIPTool cpt = new CIPTool(mol);
		Element root = cpt.getBreadthFirstCIPTree("a1", "a2");
//		CMLUtil.debug(root, "root");
		return root;
	}
}
