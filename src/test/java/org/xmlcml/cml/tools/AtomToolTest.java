package org.xmlcml.cml.tools;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.parseValidFile;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.MoleculeAtomBondTest;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.euclid.test.Vector3Test;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tests atomTool.
 *
 * @author pmr
 *
 */
public class AtomToolTest extends MoleculeAtomBondTest {
	private static Logger LOG = Logger.getLogger(AtomToolTest.class);

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.addCalculatedCoordinatesForHydrogens(HydrogenControl)'
     */
    @Ignore("NOT YET IMPLEMENTED")
    @Deprecated
    public final void testAddCalculatedCoordinatesForHydrogens() {
        CMLMolecule molecule = new CMLMolecule();
        CMLAtom atom = new CMLAtom();
        atom.setElementType(AS.C.value);
        atom.setId("a1");
        atom.setHydrogenCount(4);
        atom.setPoint3(new Point3(0, 0, 0), CoordinateType.CARTESIAN);
        molecule.addAtom(atom);
        for (int i = 0; i < 4; i++) {
            atom = new CMLAtom();
            atom.setElementType(AS.H.value);
            atom.setId("h" + (i + 1));
            molecule.addAtom(atom);
            CMLBond bond = new CMLBond();
            bond.setId("b" + (i + 1));
            bond.setAtomRefs2(new String[] { "a1", "h" + (i + 1) });
            molecule.addBond(bond);
        }
        AtomTool atomTool = AtomTool.getOrCreateTool(atom);
        atomTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, 10.0);
    }

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#sortListByAtomicNumber(java.util.List)}.
	 */
	@Test
	@Ignore
	public final void testSortListByAtomicNumber() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getCoordinationSphereList(int)}.
	 */
	@Test
	public final void testGetCoordinationSphereList() {
		makeMol11();
		CMLAtom atom1 = mol11.getAtomById("a1");
		
		AtomTool atomTool1 = AtomTool.getOrCreateTool(atom1);
		CMLAtomSet atomSet = atomTool1.getCoordinationSphereSet(0);
		CMLAtomSet refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='1' xmlns='http://www.xml-cml.org/schema'>a1</atomSet>");
		assertEqualsCanonically("atomSet 0", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(1);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='2' xmlns='http://www.xml-cml.org/schema'>a1 a2</atomSet>");
		assertEqualsCanonically("atomSet 1", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(2);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='4' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4</atomSet>");
		assertEqualsCanonically("atomSet 2", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(3);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='5' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5</atomSet>");
		assertEqualsCanonically("atomSet 3", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(1);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='2' xmlns='http://www.xml-cml.org/schema'>a1 a2</atomSet>");
		assertEqualsCanonically("atomSet 1", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(4);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='6' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6</atomSet>");
		assertEqualsCanonically("atomSet 4", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(5);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='8' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 5", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(6);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='8' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 6", refAtomSet, atomSet);
// now a different atom
		AtomTool atomTool5 = AtomTool.getOrCreateTool(mol11.getAtomById("a5"));
		atomSet = atomTool5.getCoordinationSphereSet(2);
		refAtomSet = (CMLAtomSet) parseValidString(
			"<atomSet size='7' xmlns='http://www.xml-cml.org/schema'>a5 a3 a6 a2 a4 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 2", refAtomSet, atomSet);
		
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#resetCoordinationSphereList()}.
	 */
	@Test
	@Ignore
	public final void testResetCoordinationSphereList() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getCoordinationSphereSet(int)}.
	 */
	@Test
	@Ignore
	public final void testGetCoordinationSphereSet() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSortedLigandList()}.
	 */
	@Test
	@Ignore
	public final void testGetSortedLigandList() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSingleLigand()}.
	 */
	@Test
	@Ignore
	public final void testGetSingleLigand() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSproutedSet(int)}.
	 */
	@Test
	@Ignore
	public final void testGetSproutedSet() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#recursiveCompare(org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	@Ignore
	public final void testRecursiveCompare() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getLoneElectronCount()}.
	 */
	@Test
	@Ignore
	public final void testGetLoneElectronCount() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getAtomWithLowestId(java.util.List)}.
	 */
	@Test
	@Ignore
	public final void testGetAtomWithLowestId() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#translateToCovalentRadius()}.
	 */
	@Test
	@Ignore
	public final void testTranslateToCovalentRadius() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getAtomDisplay()}.
	 */
	@Test
	@Ignore
	public final void testGetAtomDisplay() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#setAtomDisplay(org.xmlcml.cml.tools.AtomDisplay)}.
	 */
	@Test
	@Ignore
	public final void testSetAtomDisplay() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * 
	 * pointer...
     <atomArray>
       <atom elementType="R" id="a1" moleculeRef="otbs" x2="31.218704021442583" y2="-12.577617621516218">
         <label value="OTBS"/>
       </atom>
       
     * ... target ...
     * 
     * <moleculeList>
         <molecule id="otbs">
           <atomArray>
             <atom id="a1" elementType="R">
               <label value="OTBS"/>
             </atom>
	 */
	@Test
    public final void testGetReferencedAtoms() {
        Element cml = parseValidFile("org/xmlcml/cml/tools/rgroup1.xml");
        String otbs = "OTBS";
        CMLAtom atom = (CMLAtom) cml.query("./cml:molecule/cml:atomArray/cml:atom[@elementType='R' and cml:label[@value='"+otbs+"']]", CML_XPATH).get(0);
        AtomTool atomTool = AtomTool.getOrCreateTool(atom);
        // this uses the explicit pointer
        String molId =atom.getAttributeValue("moleculeRef");
        Element refMol = (CMLMolecule) cml.query(".//cml:molecule[@id='"+molId+"']", CML_XPATH).get(0);
    	List<CMLAtom> atomList = atomTool.getReferencedAtoms(refMol, otbs);
    	Assert.assertEquals("atom count", 1, atomList.size());
    	Assert.assertEquals("atom id", "a1", atomList.get(0).getId());
    	// this just scans the whole scope
    	atomList = atomTool.getReferencedAtoms(cml, otbs);
    	Assert.assertEquals("atom count", 1, atomList.size());
    	Assert.assertEquals("atom id", "a1", atomList.get(0).getId());

	}
	

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.get3DCrossProduct(CMLAtom, CMLAtom)'
     */
    @Test
    public void testGet3DCrossProduct() {
        Vector3 cross3d = AtomTool.getOrCreateTool(xmlAtom[0]).get3DCrossProduct(xmlAtom[1], xmlAtom[2]);
        Vector3Test.assertEquals("cross3d", new double[] { 0., 2., -2. },
                cross3d, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.get2DCrossProduct(CMLAtom, CMLAtom)'
     */
    @Test
    public void testGet2DCrossProduct() {
        Vector3 cross2d = AtomTool.getOrCreateTool(xmlAtom[0]).get2DCrossProduct(xmlAtom[1], xmlAtom[2]);
        Vector3Test.assertEquals("cross2d", new double[] { 0., 0., -2. },
                cross2d, EPS);
    }
    
    /**
     * 
     */
    @Test
    public void testAddHydrogen() {
    	CMLMolecule molecule = new CMLMolecule();
    	CMLAtom atom = new CMLAtom("a1", AS.C);
    	molecule.addAtom(atom);
    	AtomTool atomTool = AtomTool.getOrCreateTool(atom);
    	CMLAtom hAtom1 = atomTool.addHydrogen();
    	Assert.assertNotNull("h atom", hAtom1);
    	hAtom1 = molecule.getAtomById("a1_h1");
    	Assert.assertNotNull("h atom", hAtom1);
    	Assert.assertEquals("h id", "a1_h1", hAtom1.getId());
    	CMLBond hBond1 = molecule.getBond(atom.getId(), hAtom1.getId());
    	Assert.assertNotNull("h bond", hBond1);
    	Assert.assertEquals("h bond id", "a1_a1_h1", hBond1.getId());

    	CMLAtom hAtom2 = atomTool.addHydrogen();
    	Assert.assertNotNull("h atom", hAtom2);
    	hAtom2 = molecule.getAtomById("a1_h2");
    	Assert.assertNotNull("h atom", hAtom2);
    	Assert.assertEquals("h id", "a1_h2", hAtom2.getId());
    	CMLBond hBond2 = molecule.getBond(atom.getId(), hAtom2.getId());
    	Assert.assertNotNull("h bond", hBond2);
    	Assert.assertEquals("h bond id", "a1_a1_h2", hBond2.getId());
//    	molecule.debug("MOLZZZ ");

    	// delete bond but not atom (generally bad idea)
    	molecule.deleteBond(hBond1);
    	molecule.debug("MOL ");
    	hBond1 = molecule.getBond(atom.getId(), hAtom1.getId());
    	Assert.assertNull("h bond null", hBond1);
    	Assert.assertNotNull("h atom 1", molecule.getAtomById("a1_h1"));
    	Assert.assertNotNull("h atom 2", molecule.getAtomById("a1_h2"));
    	
    	atomTool.addHydrogen();
    	CMLAtom hAtom3 = molecule.getAtomById("a1_h3");
    	Assert.assertNotNull("h atom", hAtom3);
    	molecule.debug("MMMMM");
    	
    	Assert.assertEquals("h ligands", 2, atomTool.getHydrogenLigandList().size());
    	// deletes bond as well
    	molecule.deleteAtom(hAtom2);
    	hAtom2 = molecule.getAtomById("a1_h2");
    	Assert.assertNull("h atom", hAtom2);
    	molecule.debug("MOL ");
    }

 }
