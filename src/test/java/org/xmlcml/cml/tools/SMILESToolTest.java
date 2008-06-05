/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.parseValidString;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;

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
		molex = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("furan", molex, mol, true);
	}
		
	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testParseSMILES1() {
		// strange atom and silly ring and DOT
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[Si]O1.[Ge]N1");
		mol = smilesTool.getMolecule();
		molS = 
			"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom id='a1' elementType='Si' hydrogenCount='3'/>"+
		    "<atom id='a2' elementType='O' hydrogenCount='0'/>"+
		    "<atom id='a3' elementType='Ge'/>"+
		    "<atom id='a4' elementType='N'/>"+
		    "<atom id='a1_h1' elementType='H'/>"+
		    "<atom id='a1_h2' elementType='H'/>"+
		    "<atom id='a1_h3' elementType='H'/>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond atomRefs2='a1 a2' id='a1_a2' order='1'/>"+
		    "<bond atomRefs2='a3 a4' id='a3_a4' order='1'/>"+
		    "<bond atomRefs2='a2 a4' id='a2_a4' order='1'/>"+
		    "<bond atomRefs2='a1 a1_h1' id='a1_a1_h1' order='1'/>"+
		    "<bond atomRefs2='a1 a1_h2' id='a1_a1_h2' order='1'/>"+
		    "<bond atomRefs2='a1 a1_h3' id='a1_a1_h3' order='1'/>"+
		  "</bondArray>"+
		"</molecule>";
		molex = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("silly", molex, mol, true);
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
		molex = (CMLMolecule) parseValidString(molS);
//		AbstractTest.assertEqualsCanonically("zwitterion", molex, mol, true);
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
		molex = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("pyridine", molex, mol, true);
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
		molex = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("arom", molex, mol, true);
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
		molex = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("zwitterion", molex, mol, true);
	}
	
	/**
	 * 
	 */
	@Test
	@Ignore
	public final void testUnileverSMILES(){
		SMILESTool smilesTool = new SMILESTool();
		CMLMolecule mol;
		String ss = "_NC(CC(O)=O)C($)=O";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		mol.debug(4);
	}
}
