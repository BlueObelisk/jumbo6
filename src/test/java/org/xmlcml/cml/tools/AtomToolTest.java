package org.xmlcml.cml.tools;
import org.junit.Ignore;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.test.MoleculeAtomBondTest;
import org.xmlcml.cml.tools.AtomTool;
import org.xmlcml.euclid.Point3;
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
        atom.setElementType("C");
        atom.setId("a1");
        atom.setHydrogenCount(4);
        atom.setPoint3(new Point3(0, 0, 0), CoordinateType.CARTESIAN);
        molecule.addAtom(atom);
        for (int i = 0; i < 4; i++) {
            atom = new CMLAtom();
            atom.setElementType("H");
            atom.setId("h" + (i + 1));
            molecule.addAtom(atom);
            CMLBond bond = new CMLBond();
            bond.setId("b" + (i + 1));
            bond.setAtomRefs2(new String[] { "a1", "h" + (i + 1) });
            molecule.addBond(bond);
        }
        AtomTool atomTool = new AtomTool(atom);
        atomTool.addCalculatedCoordinatesForHydrogens(HydrogenControl.USE_HYDROGEN_COUNT);
    }

 }
