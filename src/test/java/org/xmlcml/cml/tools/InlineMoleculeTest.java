package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;

/** test InlineMolecule.
 * 
 * @author pm286
 *
 */ 
public class InlineMoleculeTest extends AbstractToolTest {

    /** Test method for 'org.xmlcml.cml.tools.InlineMolecule.InlineMolecule(String)'
     */
     @Test
     @Ignore
    public void testInlineMolecule() {
         // good atom
    	String formulaS = "C";
        InlineMolecule inlineMolecule = new InlineMolecule(formulaS);
        CMLMolecule molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);

        // bad atom
        formulaS = "Phe";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
            Assert.fail("Should throw "+InlineMolecule.Error.BAD_BOND+formulaS);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("Bad symbol ", e.getMessage(), InlineMolecule.Error.BAD_BOND+formulaS.substring(1)+CMLUtil.S_COLON);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);

//        System.out.println("------simple------");
        // simple molecule
        formulaS = "C-O";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
        
//        System.out.println("------complex------");
        // slightly complicated molecule
        formulaS = "C[id(a1)]-[l(1.2)]O[id(a2)]";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
        
//        System.out.println("------complex------");
        // slightly complicated molecule
        formulaS = "C[id(a1)]-[l(1.3)]N[id(a2)]=[l(1.35),a(108)]O[id(a3)]#[t(145),l(1.4),a(120)]S[id(a4)]-[t(120),l(1.45),a(100)]Cl[id(a5)]";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
        
        System.out.println("------branched------");
        // molecule with a branch
        formulaS = "C[id(a1)]-[l(1.3)]N[id(a2)](-[l(1.3),a(90)]C[id(a5)]=[l(1.2),a(115)]C[id(a6)])-[l(1.35),a(108),t(120)]O[id(a3)]#[t(145),l(1.4),a(120)]S[id(a4)]-[t(120),l(1.45),a(100)]Cl[id(a5)]";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
        
        System.out.println("------branch within branch------");
        // molecule with a branch
        formulaS = "C[id(a1)]-[l(1.3)]N[id(a2)](-[l(1.3),a(90)]C[id(a5)]=[l(1.2),a(115)]C[id(a6)](-[l(1.3)]C[id(a7)])-[l(1.35)]C[id(a8)])-[l(1.35),a(108),t(120)]O[id(a3)]#[t(145),l(1.4),a(120)]S[id(a4)]-[t(120),l(1.45),a(100)]Cl[id(a5)]";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
        
        System.out.println("------dendrimer------");
        // dedrimer
        formulaS = "C(-C(-C)(-C)(-C))(-C(-C)(-C)(-C))(-C(-C)(-C)(-C))(-C(-C)(-C)(-C))";
        try {
            inlineMolecule = new InlineMolecule(formulaS);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            neverThrow(e);
        }
        molecule = inlineMolecule.getCmlMolecule();
        Assert.assertNotNull("molecule not null", molecule);
    }
/**     @Test InlineBranch not visible
     public void testGetLength(){
    	 String s = "(jen)";
    	 int i = InlineBranch.getLength(C_LBRAK, s) ;
     }
     */
    /** Test method for 'org.xmlcml.cml.tools.InlineMolecule.createFromString(String)'
     */
     
     @Test
     @Ignore
    public void testCreateFromString() {

    }

    /** Test method for 'org.xmlcml.cml.tools.InlineMolecule.getMolecule()'
     */
     @Test
     @Ignore
    public void testGetMolecule() {

    }

}
