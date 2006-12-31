package org.xmlcml.cml.tools.test;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.test.MoleculeAtomBondTest;
import org.xmlcml.cml.tools.ValencyTool;

public class ValencyToolTest extends MoleculeAtomBondTest {

	@Test
	@Ignore
	public void testMarkupCommonMolecules() {
		fail("Not yet implemented");
	}

    /**
     * Test method for 'org.xmlcml.cml.tools.valencyTool.markSpecial()'
     * Not all methods are exhaustively tested, just the principle
     */
    @Test
    public void testMarkSpecial() {
        CMLMolecule nitroMethane = (CMLMolecule) parseValidString(MoleculeToolTest.nitroMethaneS);
//        MoleculeTool moleculeTool = new MoleculeTool(nitroMethane);
        CMLAtom nAtom = nitroMethane.getAtom(0);
        Assert.assertEquals("nitro", "a1", nAtom.getId());
        Assert.assertNotNull("nitro", nAtom.getFormalChargeAttribute());
        Assert.assertEquals("nitro", 1, nAtom.getFormalCharge());
        CMLAtom cAtom = nitroMethane.getAtom(1);
        Assert.assertEquals("nitro", "a2", cAtom.getId());
        CMLAtom oAtom1 = nitroMethane.getAtom(2);
        Assert.assertEquals("nitro", "a3", oAtom1.getId());
        Assert.assertNotNull("nitro", oAtom1.getFormalChargeAttribute());
        Assert.assertEquals("nitro", -1, oAtom1.getFormalCharge());
        CMLAtom oAtom2 = nitroMethane.getAtom(3);
        Assert.assertEquals("nitro", "a4", oAtom2.getId());
        CMLBond bond = nitroMethane.getBonds().get(2);
        Assert.assertEquals("nitro", CMLBond.SINGLE, bond.getOrder());
        
//	        moleculeTool.markSpecial(); // hope this is right
        new ValencyTool(nitroMethane).markupSpecial();
        Assert.assertEquals("nitro", CMLBond.DOUBLE, bond.getOrder());

	}

}
