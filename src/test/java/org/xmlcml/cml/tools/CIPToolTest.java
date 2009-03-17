/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.xmlcml.cml.test.CMLAssert.CRYSTAL_EXAMPLES;

import java.net.URL;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.euclid.EC;
import org.xmlcml.euclid.Util;
import org.xmlcml.util.TestUtils;
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
	@Ignore
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
		CMLMolecule mol = (CMLMolecule)TestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = 
			"<node parent='a1' id='a2' atnum='7'>" +
			  "<node parent='a2' id='a3' atnum='6'>" +
			    "<node parent='a3' id='a4' atnum='8'/>" +
			  "</node>" +
			  "<node parent='a2' id='a5' atnum='6'/>" +
			"</node>";
		CMLAssert.assertEqualsCanonically("node tree", TestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	@Ignore
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
		CMLMolecule mol = (CMLMolecule)TestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atnum='6'>" +
		  "<node parent='a2' id='a3' atnum='6'>" +
		    "<node parent='a3' id='a4' atnum='6'>" +
		      "<node parent='a4' id='a5' atnum='6'>" +
		        "<node parent='a5' id='a6' atnum='6'>" +
		          "<node parent='a6' id='a7' atnum='6'>" +
		            "<node parent='a7' id='a8' atnum='6'/>" +
		            "<node parent='a7' id='a2_ghost' atnum='6' ghost='true'/>" +
		          "</node>" +
		        "</node>" +
		      "</node>" +
		    "</node>" +
		  "</node>" +
		  "<node parent='a2' id='a7' atnum='6'>" +
		    "<node parent='a7' id='a6' atnum='6'>" +
		      "<node parent='a6' id='a5' atnum='6'>" +
		        "<node parent='a5' id='a4' atnum='6'>" +
		          "<node parent='a4' id='a3' atnum='6'>" +
		            "<node parent='a3' id='a2_ghost' atnum='6' ghost='true'/>" +
		          "</node>" +
		        "</node>" +
		      "</node>" +
		    "</node>" +
		    "<node parent='a7' id='a8' atnum='6'/>" +
		  "</node>" +
		"</node>";
		CMLAssert.assertEqualsCanonically("node tree", TestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	@Ignore
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
		CMLMolecule mol = (CMLMolecule)TestUtils.parseValidString(molS);
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atnum='6'>" +
		  "<node parent='a2' id='a3' atnum='6'>" +
		    "<node parent='a3' id='a2_ghost' atnum='6' ghost='true'/>" +
		    "<node parent='a3' id='a4' atnum='6'/>" +
		  "</node>" +
		  "<node parent='a2' id='a3_ghost' atnum='6' ghost='true'/>" +
		  "<node parent='a2' id='a5' atnum='6'/>" +
		"</node>";
		CMLAssert.assertEqualsCanonically("node tree", TestUtils.parseValidString(expectedS), elem1, true);
	}
	
	/**
	 * 
	 */
	@Test
	@Ignore
	public void testgetBreadthFirstCIPTree3() {
		CMLMolecule mol = parseSMILES("[R][C]([N])([F])([O])");
		CIPTool st = new CIPTool(mol);
		Element elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		String expectedS = ""+
		"<node parent='a1' id='a2' atnum='6'>" +
		  "<node parent='a2' id='a4' atnum='9'/>" +
		  "<node parent='a2' id='a5' atnum='8'/>" +
		  "<node parent='a2' id='a3' atnum='7'/>" +
		"</node>";
		CMLAssert.assertEqualsCanonically("node tree", TestUtils.parseValidString(expectedS), elem1, true);
		mol = parseSMILES("[R][C]([O])([N])([F])");
		st = new CIPTool(mol);
		elem1 = st.getBreadthFirstCIPTree("a1", "a2");
		expectedS = ""+
		"<node parent='a1' id='a2' atnum='6'>" +
		  "<node parent='a2' id='a5' atnum='9'/>" +
		  "<node parent='a2' id='a3' atnum='8'/>" +
		  "<node parent='a2' id='a4' atnum='7'/>" +
		"</node>";
		CMLAssert.assertEqualsCanonically("node tree", TestUtils.parseValidString(expectedS), elem1, true);
	}
	
	private CMLMolecule parseSMILES(String smiles) {
		SMILESTool st = new SMILESTool();
		st.parseSMILES(smiles);
		return st.getMolecule();
	}
	@Test
	@Ignore
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
			    "<bond atomRefs2='a1 a2' id='a1_a2' order='1'/>" +
			    "<bond atomRefs2='a2 a3' id='a2_a3' order='1'/>" +
			  "</bondArray>" +
			"</molecule>";
		CMLAssert.assertEqualsCanonically("mol", TestUtils.parseValidString(molS), mol1, true);
	}
	
	@Test
	@Ignore
	public void testWikipedia1() {
		// -OH > -CH3
		assertDecreasing("[R]O[H]", "[R]C([H])([H])([H])");
	}
	
	@Test
	@Ignore
	public void testWikipedia2() {
		// -CH(OH)CH3 > CH2OH
		assertDecreasing("[R]C([H])(O([H]))C([H])([H])([H])", "[R]C([H])([H])(O([H]))");
	}
	
	@Test
//	@Ignore
	public void testWikipedia3() {
		// -CH(OCH3)CH3 > CH(OH)CH2OH
		assertDecreasing("[R]C([H])(O(C([H])([H])([H])))(C([H])([H])([H]))", 
				"[R]C([H])(O([H]))[C]([H])([H])(O([H]))");
	}
	
	@Test
	@Ignore
	public void testWikipedia4() {
		// -CH(CH2F)OCH3 > CH(CH3)OCH2F
		assertDecreasing("[R]C([H])(C([H])([H])([F]))[O](C([H])([H])([H]))", 
				"[R]C([H])(C([H])([H])([H]))[O](C([H])([H])F)");
	}
	
	private void assertDecreasing(String smiles1, String smiles2) {
		Element root1 = getOrderedBreadthFirstTree(smiles1);
//		CMLUtil.debug(root1, smiles1);
		Element root2 = getOrderedBreadthFirstTree(smiles2);
//		CMLUtil.debug(root2, smiles2);
		int compare = CIPTool.compare(root1, root2);
//		System.out.println("compare" + compare);
		Assert.assertTrue("compare", compare > 0);
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
		CMLUtil.debug(root, "root");
		return root;
	}
}
