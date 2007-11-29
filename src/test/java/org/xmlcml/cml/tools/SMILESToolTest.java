/**
 * 
 */
package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * @author pm286
 *
 */
public class SMILESToolTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#SMILESTool()}.
	 */
	@Test
	public final void testSMILESTool() {
		// does nothing at present
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.SMILESTool#parseSMILES(java.lang.String)}.
	 */
	@Test
	public final void testNormalizeRings() {
		String s = "C1CCC1C2C3CCC3C2";
		String ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "C1CCC1C2C1CCC1C2", ss);
		System.out.println("---------------");
		s = "C1CCC2C1C3CCC3C2";
		ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "C1CCC2C1C1CCC1C2", ss);
		System.out.println("---------------");
		s = "CCCC9CCCCC9C";
		ss = SMILESTool.normalizeRings(s);
		Assert.assertEquals("after normalize", "CCCC1CCCCC1C", ss);
		System.out.println("---------------");
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
		SMILESTool smilesTool = new SMILESTool();
		// furan - atoms, rings bonds
		String ss = "C1=CC=CO1";
		smilesTool.parseSMILES(ss);
		CMLMolecule mol = smilesTool.getMolecule();
		
		// strange atom and silly ring and DOT
		ss = "[Si]O1.[Ge]N1";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		
		// proline zwitterion - charges and chirality and DOT
		ss = "[N+]1CCC[C@@]1(C(=O)[O-]).CCO";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();

		// multiple aromatics (fails at present)
		ss = "c1nccc2c1cccc2";
		// saturated
//		ss = "C1NCCC2C1CCCC2";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
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
