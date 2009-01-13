/**
 * 
 */
package org.xmlcml.cml.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.util.TestUtils;


/**
 * @author pm286
 *
 */
public class SMILESToolTest {

	private SMILESTool smilesTool;
	private CMLMolecule mol;
	private String molS;
	private CMLMolecule molex;

	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testNormalizeRings() {
		String s = "C1CCC1C2C3CCC3C2";
		String ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "C1CCC1C2C1CCC1C2", ss);
		s = "C1CCC2C1C3CCC3C2";
		ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "C1CCC2C1C1CCC1C2", ss);
		s = "CCCC9CCCCC9C";
		ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "CCCC1CCCCC1C", ss);
		s = "CCCC1CCC2CCCCC2CCC1C";
		ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "CCCC1CCC2CCCCC2CCC1C", ss);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	//@Ignore
	public final void testParseSMILES() {
		smilesTool = new SMILESTool();
		// furan - atoms, rings bonds
		String ss = "C1=CC=CO1";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		molS = "<molecule xmlns='http://www.xml-cml.org/schema'>"+
				"  <atomArray>"+
				"   <atom id='a1' elementType='C' hydrogenCount='1'/>" +
				"   <atom id='a2' elementType='C' hydrogenCount='1'/>"+
				"   <atom id='a3' elementType='C' hydrogenCount='1'/>"+
				"    <atom id='a4' elementType='C' hydrogenCount='1'/>"+
				"    <atom id='a5' elementType='O' hydrogenCount='0'/>"+
				"    <atom id='a1_h1' elementType='H'/>"+
				"    <atom id='a2_h1' elementType='H'/>"+
				"    <atom id='a3_h1' elementType='H'/>"+
				"    <atom id='a4_h1' elementType='H'/>"+
				"  </atomArray>"+
				"  <bondArray>"+
				"    <bond atomRefs2='a1 a2' id='a1_a2' order='2'/>"+
				"    <bond atomRefs2='a2 a3' id='a2_a3' order='1'/>"+
				"    <bond atomRefs2='a3 a4' id='a3_a4' order='2'/>"+
				"    <bond atomRefs2='a4 a5' id='a4_a5' order='1'/>"+
				"    <bond atomRefs2='a1 a5' id='a1_a5' order='1'/>"+
				"    <bond atomRefs2='a1 a1_h1' id='a1_a1_h1' order='1'/>"+
				"    <bond atomRefs2='a2 a2_h1' id='a2_a2_h1' order='1'/>"+
				"    <bond atomRefs2='a3 a3_h1' id='a3_a3_h1' order='1'/>"+
				"    <bond atomRefs2='a4 a4_h1' id='a4_a4_h1' order='1'/>"+
				"  </bondArray>"+
				"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
		TestUtils.assertEqualsCanonically("furan", molex, mol, true);
	}
		
	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	//@Ignore
	public final void testParseSMILES1() {
		// strange atom and silly ring and DOT
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[Si][O]1.[Ge][N]1");
		mol = smilesTool.getMolecule();
		molS = 
			"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom id='a1' elementType='Si' formalCharge='0' hydrogenCount='0'/>"+
		    "<atom id='a2' elementType='O' formalCharge='0' hydrogenCount='0'/>"+
		    "<atom id='a3' elementType='Ge' formalCharge='0' hydrogenCount='0' />"+
		    "<atom id='a4' elementType='N' formalCharge='0' hydrogenCount='0'/>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond atomRefs2='a1 a2' id='a1_a2' order='1'/>"+
		    "<bond atomRefs2='a3 a4' id='a3_a4' order='1'/>"+
		    "<bond atomRefs2='a2 a4' id='a2_a4' order='1'/>"+
		  "</bondArray>"+
		"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
		TestUtils.assertEqualsCanonically("silly", molex, mol, true);
	}
		
	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testParseSMILES2() {
		// proline zwitterion - charges and chirality and DOT
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[N+]1CCC[C@@]1(C(=O)[O-]).CCO");
		mol = smilesTool.getMolecule();
		molS =
		"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<molecule id='mol0_sub1'>"+
		    "<atomArray>"+
		      "<atom id='a12' elementType='C' hydrogenCount='2'/>"+
		      "<atom id='a14' elementType='C' chiral='@@' hydrogenCount='1'/>"+
		      "<atom id='a11' elementType='C' hydrogenCount='2'/>"+
		      "<atom id='a13' elementType='C' hydrogenCount='2'/>"+
		      "<atom id='a17' elementType='O' formalCharge='-1' hydrogenCount='0'/>"+
		      "<atom id='a15' elementType='C' hydrogenCount='0'/>"+
		      "<atom id='a16' elementType='O' hydrogenCount='0'/>"+
		      "<atom id='a10' elementType='N' formalCharge='1' hydrogenCount='2'/>"+
		      "<atom id='a12_h1' elementType='H'/>"+
		      "<atom id='a12_h2' elementType='H'/>"+
		      "<atom id='a14_h1' elementType='H'/>"+
		      "<atom id='a11_h1' elementType='H'/>"+
		      "<atom id='a11_h2' elementType='H'/>"+
		      "<atom id='a13_h1' elementType='H'/>"+
		      "<atom id='a13_h2' elementType='H'/>"+
		      "<atom id='a10_h1' elementType='H'/>"+
		      "<atom id='a10_h2' elementType='H'/>"+
		    "</atomArray>"+
		    "<bondArray>"+
		      "<bond atomRefs2='a15 a16' id='a15_a16' order='2'/>"+
		      "<bond atomRefs2='a14 a15' id='a14_a15' order='1'/>"+
		      "<bond atomRefs2='a10 a14' id='a10_a14' order='1'/>"+
		      "<bond atomRefs2='a13 a14' id='a13_a14' order='1'/>"+
		      "<bond atomRefs2='a15 a17' id='a15_a17' order='1'/>"+
		      "<bond atomRefs2='a10 a11' id='a10_a11' order='1'/>"+
		      "<bond atomRefs2='a11 a12' id='a11_a12' order='1'/>"+
		      "<bond atomRefs2='a12 a13' id='a12_a13' order='1'/>"+
		      "<bond atomRefs2='a12 a12_h1' id='a12_a12_h1' order='1'/>"+
		      "<bond atomRefs2='a12 a12_h2' id='a12_a12_h2' order='1'/>"+
		      "<bond atomRefs2='a14 a14_h1' id='a14_a14_h1' order='1'/>"+
		      "<bond atomRefs2='a11 a11_h1' id='a11_a11_h1' order='1'/>"+
		      "<bond atomRefs2='a11 a11_h2' id='a11_a11_h2' order='1'/>"+
		      "<bond atomRefs2='a13 a13_h1' id='a13_a13_h1' order='1'/>"+
		      "<bond atomRefs2='a13 a13_h2' id='a13_a13_h2' order='1'/>"+
		      "<bond atomRefs2='a10 a10_h1' id='a10_a10_h1' order='1'/>"+
		      "<bond atomRefs2='a10 a10_h2' id='a10_a10_h2' order='1'/>"+
		    "</bondArray>"+
		  "</molecule>"+
		  "<molecule id='mol0_sub2'>"+
		    "<atomArray>"+
		      "<atom id='a19' elementType='C' hydrogenCount='2'/>"+
		      "<atom id='a18' elementType='C' hydrogenCount='3'/>"+
		      "<atom id='a20' elementType='O' hydrogenCount='1'/>"+
		      "<atom id='a19_h1' elementType='H'/>"+
		      "<atom id='a19_h2' elementType='H'/>"+
		      "<atom id='a18_h1' elementType='H'/>"+
		      "<atom id='a18_h2' elementType='H'/>"+
		      "<atom id='a18_h3' elementType='H'/>"+
		      "<atom id='a20_h1' elementType='H'/>"+
		    "</atomArray>"+
		    "<bondArray>"+
		      "<bond atomRefs2='a19 a20' id='a19_a20' order='1'/>"+
		      "<bond atomRefs2='a18 a19' id='a18_a19' order='1'/>"+
		      "<bond atomRefs2='a19 a19_h1' id='a19_a19_h1' order='1'/>"+
		      "<bond atomRefs2='a19 a19_h2' id='a19_a19_h2' order='1'/>"+
		      "<bond atomRefs2='a18 a18_h1' id='a18_a18_h1' order='1'/>"+
		      "<bond atomRefs2='a18 a18_h2' id='a18_a18_h2' order='1'/>"+
		      "<bond atomRefs2='a18 a18_h3' id='a18_a18_h3' order='1'/>"+
		      "<bond atomRefs2='a20 a20_h1' id='a20_a20_h1' order='1'/>"+
		    "</bondArray>"+
		  "</molecule>"+
		"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
//		AbstractTest.TestUtils.assertEqualsCanonically("zwitterion", molex, mol, true);
	}


	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testParseSMILES3() {
		// aromatic
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("c1ncccc1");
		mol = smilesTool.getMolecule();
		molS =
			"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom id='a1' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a2' aromatic='true' elementType='N' hydrogenCount='0'/>"+
		    "<atom id='a3' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a4' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a5' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a6' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a1_h2' elementType='H'/>"+
		    "<atom id='a3_h2' elementType='H'/>"+
		    "<atom id='a4_h2' elementType='H'/>"+
		    "<atom id='a5_h2' elementType='H'/>"+
		    "<atom id='a6_h2' elementType='H'/>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond atomRefs2='a1 a2' id='a1_a2' order='A'/>"+
		    "<bond atomRefs2='a2 a3' id='a2_a3' order='A'/>"+
		    "<bond atomRefs2='a3 a4' id='a3_a4' order='A'/>"+
		    "<bond atomRefs2='a4 a5' id='a4_a5' order='A'/>"+
		    "<bond atomRefs2='a5 a6' id='a5_a6' order='A'/>"+
		    "<bond atomRefs2='a1 a6' id='a1_a6' order='A'/>"+
		    "<bond atomRefs2='a1 a1_h2' id='a1_a1_h2' order='1'/>"+
		    "<bond atomRefs2='a3 a3_h2' id='a3_a3_h2' order='1'/>"+
		    "<bond atomRefs2='a4 a4_h2' id='a4_a4_h2' order='1'/>"+
		    "<bond atomRefs2='a5 a5_h2' id='a5_a5_h2' order='1'/>"+
		    "<bond atomRefs2='a6 a6_h2' id='a6_a6_h2' order='1'/>"+
		  "</bondArray>"+
		"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
		TestUtils.assertEqualsCanonically("pyridine", molex, mol, true);
	}
		
	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testParseSMILES4() {
		// multiple aromatics
		smilesTool = new SMILESTool();
		// saturated
//		ss = "C1NCCC2C1CCCC2";
		smilesTool.parseSMILES("c1nccc2c1cccc2");
		mol = smilesTool.getMolecule();
		molS =
			"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom id='a1' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a2' aromatic='true' elementType='N' hydrogenCount='0'/>"+
		    "<atom id='a3' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a4' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a5' aromatic='true' elementType='C' hydrogenCount='0'/>"+
		    "<atom id='a6' aromatic='true' elementType='C' hydrogenCount='0'/>"+
		    "<atom id='a7' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a8' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a9' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a10' aromatic='true' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a1_h2' elementType='H'/>"+
		    "<atom id='a3_h2' elementType='H'/>"+
		    "<atom id='a4_h2' elementType='H'/>"+
		    "<atom id='a7_h2' elementType='H'/>"+
		    "<atom id='a8_h2' elementType='H'/>"+
		    "<atom id='a9_h2' elementType='H'/>"+
		    "<atom id='a10_h2' elementType='H'/>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond atomRefs2='a1 a2' id='a1_a2' order='A'/>"+
		    "<bond atomRefs2='a2 a3' id='a2_a3' order='A'/>"+
		    "<bond atomRefs2='a3 a4' id='a3_a4' order='A'/>"+
		    "<bond atomRefs2='a4 a5' id='a4_a5' order='A'/>"+
		    "<bond atomRefs2='a5 a6' id='a5_a6' order='A'/>"+
		    "<bond atomRefs2='a1 a6' id='a1_a6' order='A'/>"+
		    "<bond atomRefs2='a6 a7' id='a6_a7' order='A'/>"+
		    "<bond atomRefs2='a7 a8' id='a7_a8' order='A'/>"+
		    "<bond atomRefs2='a8 a9' id='a8_a9' order='A'/>"+
		    "<bond atomRefs2='a9 a10' id='a9_a10' order='A'/>"+
		    "<bond atomRefs2='a5 a10' id='a5_a10' order='A'/>"+
		    "<bond atomRefs2='a1 a1_h2' id='a1_a1_h2' order='1'/>"+
		    "<bond atomRefs2='a3 a3_h2' id='a3_a3_h2' order='1'/>"+
		    "<bond atomRefs2='a4 a4_h2' id='a4_a4_h2' order='1'/>"+
		    "<bond atomRefs2='a7 a7_h2' id='a7_a7_h2' order='1'/>"+
		    "<bond atomRefs2='a8 a8_h2' id='a8_a8_h2' order='1'/>"+
		    "<bond atomRefs2='a9 a9_h2' id='a9_a9_h2' order='1'/>"+
		    "<bond atomRefs2='a10 a10_h2' id='a10_a10_h2' order='1'/>"+
		  "</bondArray>"+
		"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
		TestUtils.assertEqualsCanonically("arom", molex, mol, true);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testParseSMILES5() {
		smilesTool = new SMILESTool();
		// saturated
		smilesTool.parseSMILES("C1NCCC2C1CCCC2");
		mol = smilesTool.getMolecule();
		molS =
			"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom id='a1' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a2' elementType='N' hydrogenCount='1'/>"+
		    "<atom id='a3' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a4' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a5' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a6' elementType='C' hydrogenCount='1'/>"+
		    "<atom id='a7' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a8' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a9' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a10' elementType='C' hydrogenCount='2'/>"+
		    "<atom id='a1_h1' elementType='H'/>"+
		    "<atom id='a1_h2' elementType='H'/>"+
		    "<atom id='a2_h1' elementType='H'/>"+
		    "<atom id='a3_h1' elementType='H'/>"+
		    "<atom id='a3_h2' elementType='H'/>"+
		    "<atom id='a4_h1' elementType='H'/>"+
		    "<atom id='a4_h2' elementType='H'/>"+
		    "<atom id='a5_h1' elementType='H'/>"+
		    "<atom id='a6_h1' elementType='H'/>"+
		    "<atom id='a7_h1' elementType='H'/>"+
		    "<atom id='a7_h2' elementType='H'/>"+
		    "<atom id='a8_h1' elementType='H'/>"+
		    "<atom id='a8_h2' elementType='H'/>"+
		    "<atom id='a9_h1' elementType='H'/>"+
		    "<atom id='a9_h2' elementType='H'/>"+
		    "<atom id='a10_h1' elementType='H'/>"+
		    "<atom id='a10_h2' elementType='H'/>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond atomRefs2='a1 a2' id='a1_a2' order='1'/>"+
		    "<bond atomRefs2='a2 a3' id='a2_a3' order='1'/>"+
		    "<bond atomRefs2='a3 a4' id='a3_a4' order='1'/>"+
		    "<bond atomRefs2='a4 a5' id='a4_a5' order='1'/>"+
		    "<bond atomRefs2='a5 a6' id='a5_a6' order='1'/>"+
		    "<bond atomRefs2='a1 a6' id='a1_a6' order='1'/>"+
		    "<bond atomRefs2='a6 a7' id='a6_a7' order='1'/>"+
		    "<bond atomRefs2='a7 a8' id='a7_a8' order='1'/>"+
		    "<bond atomRefs2='a8 a9' id='a8_a9' order='1'/>"+
		    "<bond atomRefs2='a9 a10' id='a9_a10' order='1'/>"+
		    "<bond atomRefs2='a5 a10' id='a5_a10' order='1'/>"+
		    "<bond atomRefs2='a1 a1_h1' id='a1_a1_h1' order='1'/>"+
		    "<bond atomRefs2='a1 a1_h2' id='a1_a1_h2' order='1'/>"+
		    "<bond atomRefs2='a2 a2_h1' id='a2_a2_h1' order='1'/>"+
		    "<bond atomRefs2='a3 a3_h1' id='a3_a3_h1' order='1'/>"+
		    "<bond atomRefs2='a3 a3_h2' id='a3_a3_h2' order='1'/>"+
		    "<bond atomRefs2='a4 a4_h1' id='a4_a4_h1' order='1'/>"+
		    "<bond atomRefs2='a4 a4_h2' id='a4_a4_h2' order='1'/>"+
		    "<bond atomRefs2='a5 a5_h1' id='a5_a5_h1' order='1'/>"+
		    "<bond atomRefs2='a6 a6_h1' id='a6_a6_h1' order='1'/>"+
		    "<bond atomRefs2='a7 a7_h1' id='a7_a7_h1' order='1'/>"+
		    "<bond atomRefs2='a7 a7_h2' id='a7_a7_h2' order='1'/>"+
		    "<bond atomRefs2='a8 a8_h1' id='a8_a8_h1' order='1'/>"+
		    "<bond atomRefs2='a8 a8_h2' id='a8_a8_h2' order='1'/>"+
		    "<bond atomRefs2='a9 a9_h1' id='a9_a9_h1' order='1'/>"+
		    "<bond atomRefs2='a9 a9_h2' id='a9_a9_h2' order='1'/>"+
		    "<bond atomRefs2='a10 a10_h1' id='a10_a10_h1' order='1'/>"+
		    "<bond atomRefs2='a10 a10_h2' id='a10_a10_h2' order='1'/>"+
		  "</bondArray>"+
		"</molecule>";
		molex = (CMLMolecule)TestUtils.parseValidString(molS);
		TestUtils.assertEqualsCanonically("zwitterion", molex, mol, true);
	}
	
	/**
	 * Jerry Winter's pseudo SMILES
	 */
	@Test
	@Ignore
	public final void testUnileverSMILES(){
		SMILESTool smilesTool = new SMILESTool();
//		CMLMolecule mol;
		String ss = "_NC(CC(O)=O)C($)=O";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
//		mol.debug(4);
	}
	
	@Test
	public void testWriteSMILES() {
		CMLMolecule molecule = (CMLMolecule) TestUtils.parseValidFile(
		"org/xmlcml/cml/tools/examples/molecule5a.xml");
		SMILESTool smilesTool = new SMILESTool(molecule);
		String smiles = smilesTool.write();
		Assert.assertEquals("smiles", "C([NH1][CH3])(=O)[CH3]", smiles);

		molecule = (CMLMolecule) TestUtils.parseValidFile(
		"org/xmlcml/cml/tools/examples/molecule5.xml");
		smilesTool = new SMILESTool(molecule);
		smiles = smilesTool.write();
		Assert.assertEquals("smiles", "C1([NH1][CH2][CH2]1)=O", smiles);
		
		molecule = (CMLMolecule) TestUtils.parseValidFile(
		"org/xmlcml/cml/tools/examples/molecule5b.xml");
		smilesTool = new SMILESTool(molecule);
		smiles = smilesTool.write();
		Assert.assertEquals("smiles", "C1([NH1][CH1]2[CH1]1S2)=O", smiles);
		
		molecule = (CMLMolecule) TestUtils.parseValidFile(
		"org/xmlcml/cml/tools/examples/molecule5c.xml");
		smilesTool = new SMILESTool(molecule);
		smiles = smilesTool.write();
		Assert.assertEquals("smiles", "C1([NH1][CH2][CH2]1)=O.C1([NH1][CH2][CH2]1)=O", smiles);
	}
	
	/**
	 * @author dl387 2008
	 */
	@Test
	public void unterminatedRingOpening1() {
		String smiles = "C1CC";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}

	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace1() {
		String smiles = "CCC-";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace2() {
		String smiles = "CCC=";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace3() {
		String smiles = "CCC#";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace4() {
		String smiles = "-CCC";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace5() {
		String smiles = "=CCC";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void bondPlacedInInvalidPlace6() {
		String smiles = "#CCC";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 * Tests whether the  phosphorus will be assigned the correct hydrogen count
	 *  of 0 instead of for example -2
	 */
	@Test
	//@Ignore
	public void phosphorusPentaFluoride() {
		String smiles = "P(F)(F)(F)(F)F";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/phosphorusPentaFluoride.xml");
		
		TestUtils.assertEqualsCanonically("PhosphorusPentaFluoride", correctCML, smilesTool.getMolecule(), true);
	}
	
	
	/**
	 * @author dl387
	 */
	@Test
	//@Ignore
	public void realisticPositiveCharge1() {
		String smiles = "[NH4+]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/realisticPositiveCharge1.xml");
		
		TestUtils.assertEqualsCanonically("Ammonium", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void realisticPositiveCharge2() {
		String smiles = "C[N+](C)(C)C";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/realisticPositiveCharge2.xml");
		
		TestUtils.assertEqualsCanonically("Tetramethylammonium", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void negativeCharge() {
		String smiles = "[O-]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/negativeCharge.xml");
		
		TestUtils.assertEqualsCanonically("Negative Oxygen", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void doublePositiveCharge1() {
		String smiles = "[C++]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/doublePositiveCharge.xml");
		
		TestUtils.assertEqualsCanonically("Double positive carbon", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void doublePositiveCharge2() {
		String smiles = "[C+2]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/doublePositiveCharge.xml");
		
		TestUtils.assertEqualsCanonically("Double positive carbon", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void doubleNegativeCharge1() {
		String smiles = "[O--]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/doubleNegativeCharge.xml");
		
		TestUtils.assertEqualsCanonically("Double negative carbon", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void doubleNegativeCharge2() {
		String smiles = "[O-2]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/doubleNegativeCharge.xml");
		
		TestUtils.assertEqualsCanonically("Double negative oxygen", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void badlyFormedSMILE1() {
		String smiles = "H5";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void badlyFormedSMILE2() {
		String smiles = "CH4";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 */
	@Test
	public void badlyFormedSMILE3() {
		String smiles = "13C";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			Assert.fail("Should throw exception for bad smiles: "+smiles);
		} catch (Exception e) {
			;
		}
	}
	
	/**
	 * @author dl387
	 * Should have one explicit and one implicit hydrogen i.e. 2 hydrogens 
	 * so it's just water!
	 */
	@Test
	@Ignore
	public void hydrogenHandling1() {//FIXME
		// depict gives water - JUMBO does not implement this
		String smiles = "O[H]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/hydrogenHandling1.xml");
		
		TestUtils.assertEqualsCanonically("Water", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Should have one hydrogen and a negative charge
	 */
	@Test
	//@Ignore
	public void hydrogenHandling2() {
		String smiles = "[OH-1]";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/hydrogenHandling2.xml");
		
		TestUtils.assertEqualsCanonically("Hydroxide", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * All three atoms should have one hydrogen each
	 * JUMBO fails on this and we shan't alter it
	 */
	@Test
	@Ignore
	public void hydrogenHandling3() {
		String smiles = "ONO";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/hydrogenHandling3.xml");
		
		TestUtils.assertEqualsCanonically("ONO", correctCML, smilesTool.getMolecule(), true);
	}

	// MISSING FEATURES
	
	/**
	 * @author dl387
	 * A test of the percentage sign syntax for describing ring openings
	 */
	@Test
	public void ringSupportGreaterThan10() {
		String smiles = "C%10CC%10";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
		} catch (RuntimeException e) {
			Assert.fail("The following SMILES failed to parse but should have: "+smiles);
		}
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/ringSupportGreaterThan10.xml");
		
		TestUtils.assertEqualsCanonically("CycloPropane", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Support for alternative ways of specifying tetrahedral chriality
	 * JUMBO ignores this
	 */
	@Test
	@Ignore
	public void chiralityTetrahedral() {//FIXME
		String smiles = "N[C@TH2H](C)C(=O)O";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
		} catch (RuntimeException e) {
			Assert.fail("The following SMILES failed to parse but should have: "+smiles);
		}
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
		"org/xmlcml/cml/tools/examples/chiralityTetrahedral.xml");

		TestUtils.assertEqualsCanonically("Alanine", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Support is missing for explicitly stating the type of chirality
	 * This is only important for square planar where there is ambiguity
	 * between this form and tetrahedral
	 */
	@Test
	public void squarePlanarGeometry() {
		String smiles = "F[Po@SP1](Cl)(Br)I";
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
		} catch (RuntimeException e) {
			Assert.fail("The following SMILES failed to parse but should have: "+smiles);
		}
		Assert.assertNotNull(smilesTool.getMolecule());
	}
	
	/**
	 * @author dl387
	 * Checks bondstereo tag is added correctly
	 */
	@Test
	public void transTest() {	
		String smiles = "F/C=C/F";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/transDiFluoroEthene.xml");
		
		TestUtils.assertEqualsCanonically("transDiFluoroEthene", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Checks the parser against a large collection of valid SMILES to check they atleast parse
	 */
	@Test
	public void largeSmilesSelection() {
		InputStream is = getClass().getResourceAsStream("examples/smilesFromJason.smi");
		Assert.assertNotNull("input stream exists", is);
		BufferedReader input = new BufferedReader(new InputStreamReader(is));
		SMILESTool smilesTool = new SMILESTool();
		
		String smile;
		int i=0;
		try {
	        while ((smile = input.readLine()) != null) {
	        	i++;
	        	if (smile.length()==0){continue;}
	        	try{
	        		smilesTool.parseSMILES(smile);
	        	}
	        	catch(Exception e){
	        		Assert.fail("Failure on line " + i);
	        	}
	        }
		} catch (IOException e) {
			Assert.fail("failed parse "+e);
		}
		Assert.assertEquals("SMILES count", 926, i);
		try {
			input.close();
		} catch (IOException e) {
			Assert.fail("BUG");
		}
	}
	
	/**
	 * @author dl387
	 * Checks anticlockwise chirality
	 */
	@Test
	//@Ignore
	public void chiralityTest() {
		String smiles = "N[C@@H](F)C";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/chirality1.xml");
		
		TestUtils.assertEqualsCanonically("chirality1", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Checks clockwise chirality
	 */
	@Test
	//@Ignore
	public void chiralityTest2() {
		String smiles = "N[C@H](F)C";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/chirality2.xml");
		
		TestUtils.assertEqualsCanonically("chirality2", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Checks rings defined before chiral centre
	 */
	@Test
	//@Ignore
	public void chiralityTest3() {
		String smiles = "C2.N1.F3.[C@@H]231";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/chirality3.xml");
		
		TestUtils.assertEqualsCanonically("chirality3", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Checks rings defined after chiral centre
	 */
	@Test
	//@Ignore
	public void chiralityTest4() {
		String smiles = "[C@@H]231.C2.N1.F3";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/chirality4.xml");
		
		TestUtils.assertEqualsCanonically("chirality4", correctCML, smilesTool.getMolecule(), true);
	}
	
	/**
	 * @author dl387
	 * Checks connected chiral centres
	 */
	@Test
	//@Ignore
	public void chiralityTest5() {
		String smiles = "[C@@H](Cl)1[C@H](C)(F).Br1";
		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES(smiles);
		CMLMolecule correctCML = (CMLMolecule) TestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/chirality5.xml");
		
		TestUtils.assertEqualsCanonically("chirality5", correctCML, smilesTool.getMolecule(), true);
	}

	@Test 
	public void LHProblem() {
		String smiles = 
			"C1(C2)C(c4ccc(Oc6ccc(N)cc6)cc4)(c5ccc(Oc7ccc(N)cc7)cc5)C(C3)CC2CC3C1";		
		SMILESTool smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
//			smilesTool.getMolecule().debug("MOL");			
		} catch (RuntimeException e) {
			Assert.fail("should not fail: "+e);
		}
	}
}
