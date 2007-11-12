package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import java.io.IOException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.MoleculeAtomBondTest;

/**
 * 
 * @author pm286
 * 
 */
public class ValencyToolTest extends MoleculeAtomBondTest {

    /** test */
    @Test
    @Ignore
    public void testMarkupCommonMolecules() {
        fail("Not yet implemented");
    }

    /**
     * Test method for 'org.xmlcml.cml.tools.valencyTool.markSpecial()' Not all
     * methods are exhaustively tested, just the principle
     */
    @Test
    public void testMarkSpecial() {
        CMLMolecule nitroMethane = (CMLMolecule) parseValidString(MoleculeToolTest.nitroMethaneS);
        // MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(nitroMethane);
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

        // moleculeTool.markSpecial(); // hope this is right
        new ValencyTool(nitroMethane).markupSpecial();
        Assert.assertEquals("nitro", CMLBond.DOUBLE, bond.getOrder());

    }

    /**
     * Problem discovered 2007-05-16, causing an NPE when ValencyTool calls
     * MoleculeTool. Pathology case includes a mol with sub-molecules.
     * @throws IOException 
     * @throws ParsingException 
     * @throws ValidityException 
     */
    @Test
    @Ignore //(problems finding resource)
    public void testRegression1() throws ValidityException, ParsingException, IOException {
        Document doc = new CMLBuilder().build(getClass().getClassLoader()
                .getResourceAsStream("./valencytoolpathology1.cml.xml"));
        CMLMolecule mol = (CMLMolecule) doc.getRootElement();
        ValencyTool vt = new ValencyTool(mol);
        //This originally threw an NPE
        vt.adjustBondOrdersAndChargesToValency();
    }
}
