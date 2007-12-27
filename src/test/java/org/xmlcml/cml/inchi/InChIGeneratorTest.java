package org.xmlcml.cml.inchi;

import java.util.ArrayList;
import java.util.List;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;
import net.sf.jniinchi.JniInchiNativeCodeLoader;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Point3;
import org.xmlcml.molutil.ChemicalElement.AS;

/** test for inchi generation.
 *
 * @author pm286
 *
 */
public class InChIGeneratorTest {

    protected static CMLMolecule getLAlanineInput() {
    	CMLMolecule molecule = new CMLMolecule();

        CMLAtom a1 = new CMLAtom("a1");
        a1.setElementType(AS.C.value);
        a1.setXYZ3(new Point3(-0.358, 0.819, 20.655));
        a1.setHydrogenCount(1);
        CMLAtom a2 = new CMLAtom("a2");
        a2.setElementType(AS.C.value);
        a2.setXYZ3(new Point3(-1.598, -0.032, 20.905));
        CMLAtom a3 = new CMLAtom("a3");
        a3.setElementType(AS.N.value);
        a3.setXYZ3(new Point3(-0.275, 2.014, 21.574));
        a3.setHydrogenCount(2);
        CMLAtom a4 = new CMLAtom("a4");
        a4.setElementType(AS.C.value);
        a4.setXYZ3(new Point3(0.952, 0.043, 20.838));
        a4.setHydrogenCount(3);
        CMLAtom a5 = new CMLAtom("a5");
        a5.setElementType(AS.O.value);
        a5.setXYZ3(new Point3(-2.678, 0.479, 21.093));
        CMLAtom a6 = new CMLAtom("a6");
        a6.setElementType(AS.O.value);
        a6.setXYZ3(new Point3(-1.596, -1.239, 20.958));
        a6.setHydrogenCount(1);

        molecule.addAtom(a1);
        molecule.addAtom(a2);
        molecule.addAtom(a3);
        molecule.addAtom(a4);
        molecule.addAtom(a5);
        molecule.addAtom(a6);

        CMLBond b0 = new CMLBond(a1, a2);
        b0.setOrder(CMLBond.SINGLE);
        molecule.addBond(b0);
        CMLBond b1 = new CMLBond(a1, a3);
        b1.setOrder(CMLBond.SINGLE);
        molecule.addBond(b1);
        CMLBond b2 = new CMLBond(a1, a4);
        b2.setOrder(CMLBond.SINGLE);
        molecule.addBond(b2);
        CMLBond b3 = new CMLBond(a2, a5);
        b3.setOrder(CMLBond.DOUBLE);
        molecule.addBond(b3);
        CMLBond b4 = new CMLBond(a2, a6);
        b4.setOrder(CMLBond.SINGLE);
        molecule.addBond(b4);

    	return(molecule);
    }


    protected static CMLMolecule getRAlanineInput() {
    	CMLMolecule molecule = new CMLMolecule();

        CMLAtom a1 = new CMLAtom("a1");
        a1.setElementType(AS.C.value);
        a1.setXYZ3(new Point3(0.358, 0.819, 20.655));
        a1.setHydrogenCount(1);
        CMLAtom a2 = new CMLAtom("a2");
        a2.setElementType(AS.C.value);
        a2.setXYZ3(new Point3(1.598, -0.032, 20.905));
        CMLAtom a3 = new CMLAtom("a3");
        a3.setElementType(AS.N.value);
        a3.setXYZ3(new Point3(0.275, 2.014, 21.574));
        a3.setHydrogenCount(2);
        CMLAtom a4 = new CMLAtom("a4");
        a4.setElementType(AS.C.value);
        a4.setXYZ3(new Point3(-0.952, 0.043, 20.838));
        a4.setHydrogenCount(3);
        CMLAtom a5 = new CMLAtom("a5");
        a5.setElementType(AS.O.value);
        a5.setXYZ3(new Point3(2.678, 0.479, 21.093));
        CMLAtom a6 = new CMLAtom("a6");
        a6.setElementType(AS.O.value);
        a6.setXYZ3(new Point3(1.596, -1.239, 20.958));
        a6.setHydrogenCount(1);

        molecule.addAtom(a1);
        molecule.addAtom(a2);
        molecule.addAtom(a3);
        molecule.addAtom(a4);
        molecule.addAtom(a5);
        molecule.addAtom(a6);

        CMLBond b0 = new CMLBond(a1, a2);
        b0.setOrder(CMLBond.SINGLE);
        molecule.addBond(b0);
        CMLBond b1 = new CMLBond(a1, a3);
        b1.setOrder(CMLBond.SINGLE);
        molecule.addBond(b1);
        CMLBond b2 = new CMLBond(a1, a4);
        b2.setOrder(CMLBond.SINGLE);
        molecule.addBond(b2);
        CMLBond b3 = new CMLBond(a2, a5);
        b3.setOrder(CMLBond.DOUBLE);
        molecule.addBond(b3);
        CMLBond b4 = new CMLBond(a2, a6);
        b4.setOrder(CMLBond.SINGLE);
        molecule.addBond(b4);

    	return(molecule);
    }


	/** Test method for 'org.xmlcml.cml.inchi.InChIGenerator.InChIGenerator(CMLAtomContainer)'
    * @throws CMLException
	 */
    @Test
	public void testInChIGeneratorCMLAtomContainer() throws CMLException {
        JniInchiNativeCodeLoader.setDebug(true);
		InChIGeneratorFactory factory = new InChIGeneratorFactory();

		CMLMolecule lAlanine = getLAlanineInput();
		InChIGenerator gen = factory.getInChIGenerator(lAlanine);
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:4,1,2,3,5,6/E:(5,6)/it:im/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:-.358,.819,20.655;-1.598,-.032,20.905;-.275,2.014,21.574;.952,.043,20.838;-2.678,.479,21.093;-1.596,-1.239,20.958;");

		CMLMolecule rAlanine = getRAlanineInput();
		gen = factory.getInChIGenerator(rAlanine);
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:4,1,2,3,5,6/E:(5,6)/it:im/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:.358,.819,20.655;1.598,-.032,20.905;.275,2.014,21.574;-.952,.043,20.838;2.678,.479,21.093;1.596,-1.239,20.958;");
	}


	/** Test method for 'org.xmlcml.cml.inchi.InChIGenerator.InChIGenerator(CMLMolecule, String)'
    * @throws CMLException
	 */
    @Test
	public void testInChIGeneratorCMLMoleculeString() throws CMLException {
		InChIGeneratorFactory factory = new InChIGeneratorFactory();

		CMLMolecule lAlanine = getLAlanineInput();
		InChIGenerator gen = factory.getInChIGenerator(lAlanine, "-SRel");
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/s2");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:4,1,2,3,5,6/E:(5,6)/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:-.358,.819,20.655;-1.598,-.032,20.905;-.275,2.014,21.574;.952,.043,20.838;-2.678,.479,21.093;-1.596,-1.239,20.958;");

		CMLMolecule rAlanine = getRAlanineInput();
		gen = factory.getInChIGenerator(rAlanine, "/cOmPRess");
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/cABBCC/hB1D2A3,1EF/tB1/m1/s1");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:DABCEF/E:EF/it:im/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:.358,.819,20.655;1.598,-.032,20.905;.275,2.014,21.574;-.952,.043,20.838;2.678,.479,21.093;1.596,-1.239,20.958;");
	}

	/** Test method for 'org.xmlcml.cml.inchi.InChIGenerator.InChIGenerator(CMLMolecule, List)'
    * @throws CMLException
	 */
    @Test
	public void testInChIGeneratorCMLMoleculeList() throws CMLException {
		InChIGeneratorFactory factory = new InChIGeneratorFactory();

		CMLMolecule lAlanine = getLAlanineInput();
		List<INCHI_OPTION> ops = new ArrayList<INCHI_OPTION>();
		ops.add(INCHI_OPTION.SRel);
		InChIGenerator gen = factory.getInChIGenerator(lAlanine, ops);
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/s2");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:4,1,2,3,5,6/E:(5,6)/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:-.358,.819,20.655;-1.598,-.032,20.905;-.275,2.014,21.574;.952,.043,20.838;-2.678,.479,21.093;-1.596,-1.239,20.958;");

		CMLMolecule rAlanine = getRAlanineInput();
		ops = new ArrayList<INCHI_OPTION>();
		ops.add(INCHI_OPTION.Compress);
		gen = factory.getInChIGenerator(rAlanine, ops);
		Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
		Assert.assertEquals(gen.getInchi(), "InChI=1/C3H7NO2/cABBCC/hB1D2A3,1EF/tB1/m1/s1");
		Assert.assertEquals(gen.getAuxInfo(), "AuxInfo=1/1/N:DABCEF/E:EF/it:im/rA:6CCNCOO/rB:s1;s1;s1;d2;s2;/rC:.358,.819,20.655;1.598,-.032,20.905;.275,2.014,21.574;-.952,.043,20.838;2.678,.479,21.093;1.596,-1.239,20.958;");
	}


    /**
     * @throws CMLException
     */
    @Test
    public void testInChIWithImplicitHydrogen() throws CMLException {
        InChIGeneratorFactory factory = new InChIGeneratorFactory();
        CMLMolecule mol = new CMLMolecule();
        CMLAtom atom = new CMLAtom("a1");
        atom.setElementType(AS.C.value);
        mol.addAtom(atom);

        InChIGenerator gen = factory.getInChIGenerator(mol);
        String inchi = gen.getInchi();

        Assert.assertEquals("InChI=1/CH4/h1H4", inchi);
    }
    
    /**
     * @throws CMLException
     */

    @Test
    public void testInChIWithExplicitHydrogens() throws CMLException {
        InChIGeneratorFactory factory = new InChIGeneratorFactory();
        CMLMolecule mol = new CMLMolecule();
        CMLAtom atom = new CMLAtom("a1");
        atom.setElementType(AS.C.value);
        atom.setHydrogenCount("3");
        atom.setSpinMultiplicity(2);
        mol.addAtom(atom);

        InChIGenerator gen = factory.getInChIGenerator(mol);
        String inchi = gen.getInchi();

        Assert.assertEquals("InChI=1/CH3/h1H3", inchi);
    }

}
