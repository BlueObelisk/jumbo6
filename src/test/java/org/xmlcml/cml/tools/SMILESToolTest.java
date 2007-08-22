/**
 * 
 */
package org.xmlcml.cml.tools;

import org.junit.Before;
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
	public final void testParseSMILES() {
		SMILESTool smilesTool = new SMILESTool();
		// furan - atoms, rings bonds
		String ss = "C1=CC=CO1";
		smilesTool.parseSMILES(ss);
		CMLMolecule mol = smilesTool.getMolecule();
		mol.debug();
		
		// strange atom and silly ring and DOT
		ss = "[Si]O1.[Ge]N1";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		mol.debug();
		
		// proline zwitterion - charges and chirality and DOT
		ss = "[N+]1CCC[C@@]1(C(=O)[O-]).CCO";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		mol.debug();

		// multiple aromatics (fails at present)
		ss = "c1nccc2c1cccc2";
		// saturated
//		ss = "C1NCCC2C1CCCC2";
		smilesTool.parseSMILES(ss);
		mol = smilesTool.getMolecule();
		mol.debug();
	}

}
