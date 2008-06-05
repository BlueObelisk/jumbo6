package org.xmlcml.cml.tools;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.parseValidFile;
import static org.xmlcml.util.TestUtils.parseValidString;
import nu.xom.Element;

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
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tests atomTool.
 *
 * @author pmr
 *
 */
public class AtomToolTest extends MoleculeAtomBondTest {
//    protected AtomTool atomTool1;

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
	 */
	@Test
    public final void testGetReferencedAtoms() {
        Element cml = parseValidFile("org/xmlcml/cml/tools/testRGroup1.xml");
//    	List<CMLAtom> atomList = atomTool.getReferencedAtoms(scopeElement);
	}
 }
