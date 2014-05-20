package org.xmlcml.cml.tools;

import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;

public class Fixtures {

	@Test
	public void testDummy() {
		//
	}
	public static CMLMolecule getHOCl() {
		CMLMolecule mol = SMILESTool.createMolecule("[H]OCl");
		return  mol;
	}
	
	public static CMLMolecule getAceticAcid() {
		CMLMolecule mol = SMILESTool.createMolecule("CC(=O)O");
		return mol;
	}
	
	@Test
	public void testMolecule() {
		CMLMolecule mol = getAceticAcid();
//		mol.debug();
	}
}
