package org.xmlcml.cml.tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;
/**
 * tool to manage stereochemistry. not yet fully developed
 * 
 * @author pmr
 * 
 */
public class StereochemistryTool extends AbstractTool {
    Logger logger = Logger.getLogger(StereochemistryTool.class.getName());
    
    MoleculeTool moleculeTool;
    CMLMolecule molecule;
    /**
     * constructor with embedded molecule.
     * 
     * @param molecule
     */
    public StereochemistryTool(CMLMolecule molecule) {
        this.molecule = molecule;
        moleculeTool = new MoleculeTool(molecule);
    }
    /**
     * uses 2D coordinates to add bondStereo.
     * 
     * @throws CMLException
     *             inappropriate connectivity, coordinates, etc.
     */
    public void add2DStereo() throws CMLException {
        // MoleculeTool moleculeTool = new MoleculeTool(molecule);
        List<CMLBond> acyclicDoubleBonds = new ConnectionTableTool(molecule)
                .getAcyclicDoubleBonds();
        for (CMLBond bond : acyclicDoubleBonds) {
            CMLBondStereo bondStereo = this.get2DBondStereo(bond);
            if (bondStereo != null) {
                bond.addBondStereo(bondStereo);
            }
        }
    }
    /**
     * uses atomParity to add wedge and hatch.
     * 
     * @throws CMLException
     */
    public void addWedgeHatch() throws CMLException {
        // CMLBond[] acyclicDoubleBonds = getAcyclicDoubleBonds();
        // testing only
        for (CMLAtom atom : molecule.getAtoms()) {
            moleculeTool.addWedgeHatchBond(atom);
        }
    }
    /**
     * add wedge hatch bonds.
     * 
     * adds WEDGE/HATCH to appropriate bonds/atoms if not already present and we
     * have parities.
     * 
     * If ANY wedge/hatch are set, return without action (this may change)
     * 
     * @throws CMLRuntimeException
     */
    public void addWedgeHatchBonds() throws CMLRuntimeException {
        for (CMLAtom chiralAtom : new StereochemistryTool(molecule)
                .getChiralAtoms()) {
            moleculeTool.addWedgeHatchBond(chiralAtom);
        }
    }
    /**
     * Calculates the atom parity of this atom using the coords of either 4
     * explicit ligands or 3 ligands and this atom. If only 2D coords are
     * specified then the parity is calculated using bond wedge/hatch
     * information.
     * 
     * @param atom
     * @return the CMLAtomParity, or null if this atom isnt a chiral centre or
     *         there isnt enough stereo information to calculate parity
     */
    public CMLAtomParity calculateAtomParity(CMLAtom atom) {
        if (!isChiralCentre(atom)) {
            return null;
        }
        List<CMLAtom> ligandList = atom.getLigandAtoms();
        if (ligandList.size() == 3) {
            ligandList.add(atom); // use this atom as 4th atom
        }
        double[][] parityMatrix = new double[4][4];
        String[] atomRefs4 = new String[4];
        for (int i = 0; i < 4; i++) { // build matrix
            parityMatrix[0][i] = 1;
            if (ligandList.get(i).hasCoordinates(CoordinateType.CARTESIAN)) {
                parityMatrix[1][i] = ligandList.get(i).getX3();
                parityMatrix[2][i] = ligandList.get(i).getY3();
                parityMatrix[3][i] = ligandList.get(i).getZ3();
            } else if (ligandList.get(i).hasCoordinates(CoordinateType.TWOD)) {
                parityMatrix[1][i] = ligandList.get(i).getX2();
                parityMatrix[2][i] = ligandList.get(i).getY2();
                parityMatrix[3][i] = 0;
                // get z-coord from wedge/hatch bond
                CMLBond ligandBond = atom.getMolecule().getBond(atom,
                        ligandList.get(i));
                if (ligandBond != null) {
                    CMLBondStereo ligandBondStereo = ligandBond.getBondStereo();
                    if (ligandBondStereo != null) {
                        if (ligandBondStereo.getXMLContent().equals(
                                CMLBond.WEDGE)) {
                            parityMatrix[3][i] = 1.0;
                        } else if (ligandBondStereo.getXMLContent().equals(
                                CMLBond.HATCH)) {
                            parityMatrix[3][i] = -1.0;
                        }
                    }
                }
            } else {
                // no coordinates!
                throw new CMLRuntimeException(
                        "insufficient coordinates on ligands to determine parity");
            }
            atomRefs4[i] = ligandList.get(i).getId();
        }
        double parityDeterminant = determinant(parityMatrix);
        CMLAtomParity atomParity = new CMLAtomParity();
        if (Math.abs(parityDeterminant) > atomParity.minChiralDeterminant) {
            atomParity.setAtomRefs4(atomRefs4);
            atomParity.setXMLContent(parityDeterminant);
            return atomParity;
        } else {
            return null;
        }
    }
    private double determinant(double[][] matrix) {
        double determinant = 0;
        int matrixSize = matrix.length;
        double[][] minorMatrix = new double[matrixSize - 1][matrixSize - 1];
        if (matrixSize == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
        } else {
            for (int j1 = 0; j1 < matrixSize; j1++) {
                for (int i = 1; i < matrixSize; i++) {
                    int j2 = 0;
                    for (int j = 0; j < matrixSize; j++) {
                        if (j == j1)
                            continue;
                        minorMatrix[i - 1][j2] = matrix[i][j];
                        j2++;
                    }
                }
                // sum (+/-)cofactor * minor
                determinant = determinant + Math.pow(-1.0, j1) * matrix[0][j1]
                        * determinant(minorMatrix);
            }
        }
        return determinant;
    }
    /**
     * Determines whether this atom is a chiral centre, currently only works for
     * carbon atoms with 4 ligands (or 3 + an implicit hydrogen).
     * 
     * @param atom
     * @return true unless this atom has 2 or more identical ligands
     */
    public boolean isChiralCentre(CMLAtom atom) {
        boolean mayBeChiral = true;
        List<CMLAtom> ligandList = atom.getLigandAtoms();
        if (atom.getElementType().equals("C")
                && (ligandList.size() == 3 | ligandList.size() == 4)) {
            for (Iterator<CMLAtom> ligands = ligandList.iterator(); ligands
                    .hasNext();) {
                CMLAtom firstLigand = ligands.next();
                if (ligandList.size() == 3
                        && firstLigand.getElementType().equals("H")) {
                    // also have one implicit hydrogen, so not chiral
                    mayBeChiral = false;
                    return mayBeChiral;
                }
                for (Iterator<CMLAtom> ligandsCompare = ligandList.iterator(); ligandsCompare
                        .hasNext();) {
                    CMLAtom secondLigand = ligandsCompare.next();
                    if (firstLigand == secondLigand) {
                        continue;
                    }
                    AtomTree firstAtomTree = new AtomTree(atom, firstLigand);
                    AtomTree secondAtomTree = new AtomTree(atom, secondLigand);
                    firstAtomTree.expandTo(100);
                    secondAtomTree.expandTo(100);
                    if (firstAtomTree.toString().equals(
                            secondAtomTree.toString())) {
                        // identical ligands
                        mayBeChiral = false;
                        return mayBeChiral;
                    }
                }
            }
        } else {
            mayBeChiral = false;
        }
        return mayBeChiral;
    }
    /**
     * gets list of atoms with enough different ligands to show chirality.
     * 
     * at present only processes Carbon. all four ligands must be different or
     * either explicitly or with an implicit hydrogen and 3 explict ligands
     * 
     * the atom may or may not have its chirality set by other means (e.g.
     * atomParity) however an atom may not be tested for chirality if it is not
     * in this list
     * 
     * @return the list of atoms
     */
    public List<CMLAtom> getChiralAtoms() {
        List<CMLAtom> chiralAtoms = new ArrayList<CMLAtom>();
        for (CMLAtom atom : molecule.getAtoms()) {
            if (isChiralCentre(atom)) {
                chiralAtoms.add(atom);
            }
        }
        return chiralAtoms;
    }
    /**
     * get the bondstereo from 2D coordinates.
     * 
     * gets atomRefs4 (at0, at1, at2, at3), then gets scalar product of at1-at0
     * X at1-lig2 and at1-at2 X at1-lig2
     * 
     * does NOT add bondStereo as child (in case further decisions need to be
     * made)
     * 
     * @param bond
     * @return bondstereo (null if cannot calculate as CIS/TRANS)
     * @throws CMLException
     */
    public CMLBondStereo get2DBondStereo(CMLBond bond) throws CMLException {
        CMLBondStereo bondStereo = null;
        CMLAtom[] atom4 = moleculeTool.getAtomRefs4(bond);
        if (atom4 != null) {
            Vector3 v1 = atom4[1].get2DCrossProduct(atom4[2], atom4[0]);
            Vector3 v2 = atom4[2].get2DCrossProduct(atom4[1], atom4[3]);
            double d = v1.dot(v2);
            if (Math.abs(d) > 0.000001) {
                bondStereo = new CMLBondStereo();
                bondStereo.setAtomRefs4(new String[] { atom4[0].getId(),
                        atom4[1].getId(), atom4[2].getId(), atom4[3].getId() });
                bondStereo.setXMLContent((d > 0) ? CMLBond.TRANS : CMLBond.CIS);
            }
        }
        return bondStereo;
    }
    /**
     * uses bondStereo to adjust 2D coordinates.
     * 
     * @param bond
     * @throws CMLException
     */
    public void layoutDoubleBond(CMLBond bond) throws CMLException {
        CMLBondStereo bondStereo2 = null;
        CMLBondStereo bondStereo3 = null;
        // CMLMolecule molecule = this.getMolecule();
        try {
            bondStereo2 = this.get2DBondStereo(bond);
            bondStereo3 = this.get3DBondStereo(bond);
        } catch (CMLException e) {
            logger.severe("cannot layout double bond" + e);
            // e.printStackTrace ();
            // cannot layout double bond
        }
        if (bondStereo2 != null && bondStereo3 != null) {
            int match = bondStereo3.matchParity(bondStereo2, molecule);
            if (match == -1) {
                // System.out.println("FLIP ");
                this.flip2D(bond);
            }
        }
    }
    /**
     * flip (about bond axis) the 2D coordinates attached to atom0.
     * 
     * @param bond
     * @exception CMLException
     *                many, including invalid geometry operations
     */
    public void flip2D(CMLBond bond) throws CMLException {
        // FIXME
        // flip2D(bond, this.getAtom(bond, 0));
    }
    /**
     * get the bondstereo from 3D coordinates.
     * 
     * gets atomRefs4 (at0, at1, at2, at3), then gets scalar product of at1-at0
     * X at1-lig2 and at1-at2 X at1-lig2
     * 
     * does NOT add bondStereo as child (in case further decisions need to be
     * made)
     * 
     * @param bond
     * 
     * @return bondstereo (null if cannot calculate as CIS/TRANS)
     * @exception CMLException
     */
    public CMLBondStereo get3DBondStereo(CMLBond bond) throws CMLException {
        CMLBondStereo bondStereo = null;
        CMLAtom[] atom4 = null;
        try {
            atom4 = moleculeTool.getAtomRefs4(bond);
        } catch (CMLException e) {
            logger.severe("No stereo for: " + this + "/" + e);
            /* bond cannot have stereochemistry */
        }
        if (atom4 != null) {
            Vector3 v1 = atom4[1].get3DCrossProduct(atom4[2], atom4[0]);
            Vector3 v2 = atom4[2].get3DCrossProduct(atom4[1], atom4[3]);
            double d = v1.dot(v2);
            if (Math.abs(d) > 0.000001) {
                bondStereo = new CMLBondStereo();
                bondStereo.setAtomRefs4(new String[] { atom4[0].getId(),
                        atom4[1].getId(), atom4[2].getId(), atom4[3].getId() });
                bondStereo.setXMLContent((d > 0) ? CMLBond.TRANS : CMLBond.CIS);
            }
        }
        return bondStereo;
    }
    /**
     * calculates whether geometry of bond is cis or trans.
     * 
     * requires geometry of form: ligand0-atom(0)-atom(1)-ligand1 i.e. ligand0
     * is a ligand of this,getAtom(0) and ligand1 is a ligand of this,getAtom(1)
     * 
     * if connectivity is not as above throws CMLException if this.getAtom(0) or
     * this.getAtom(1) have > 3 ligands throws CMLException if either end of
     * bond is effectively linear return BondTool.LINEAR
     * 
     * @param ligand0
     *            ligand(tool) for this.getAtom(0)
     * @param ligand1
     *            ligand(tool) for this.getAtom(1)
     * 
     * @return CIS, TRANS, UNKNOWN,
     */
    /**
     * calculates whether geometry of bond is cis or trans.
     * 
     * requires geometry of form: ligand0-atom(0)-atom(1)-ligand1 i.e. ligand0
     * is a ligand of this,getAtom(0) and ligand1 is a ligand of this,getAtom(1)
     * 
     * if connectivity is not as above throws CMLException if this.getAtom(0) or
     * this.getAtom(1) have > 3 ligands throws CMLException if either end of
     * bond is effectively linear return BondTool.LINEAR
     * 
     * if torsion angle is in range pi/4 < t < 3*pi/4 return UNKNOWN
     * 
     * @param bond
     * @param ligand0
     *            ligand(tool) for this.getAtom(0)
     * @param ligand1
     *            ligand(tool) for this.getAtom(1)
     * 
     * @throws CMLException
     * @return CIS, TRANS, UNKNOWN,
     */
    public String calculate3DStereo(CMLBond bond, CMLAtom ligand0,
            CMLAtom ligand1) throws CMLException {
        String cisTrans = CMLBond.UNKNOWN_ORDER;
        // wrong sort of bond
        // String order = getOrder();
        if (!bond.getOrder().equals(CMLBond.DOUBLE)) {
            return CMLBond.UNKNOWN_ORDER;
        }
        CMLAtom atom0 = bond.getAtom(0);
        if (molecule.getBond(atom0, ligand0) == null) {
            throw new CMLException("ligand0 is not connected to bond");
        }
        CMLAtom atom1 = bond.getAtom(1);
        if (molecule.getBond(atom1, ligand1) == null) {
            throw new CMLException("ligand1 is not connected to bond");
        }
        // no meaningful ligands or too many
        int ligandCount0 = atom0.getLigandAtoms().size();
        int ligandCount1 = atom1.getLigandAtoms().size();
        if ((ligandCount0 < 2 || ligandCount0 > 3)
                || (ligandCount1 < 2 || ligandCount1 > 3)) {
            return CMLBond.UNKNOWN_ORDER;
        }
        // torsion
        Point3 p0 = ligand0.getXYZ3();
        Point3 p1 = atom0.getXYZ3();
        Point3 p2 = atom1.getXYZ3();
        Point3 p3 = ligand1.getXYZ3();
        try {
            Angle a = Point3.getTorsion(p0, p1, p2, p3);
            double ang = Math.abs(a.getRadian());
            if (ang < Math.PI / 4.) {
                cisTrans = CMLBond.CIS;
            } else if (ang > 3 * Math.PI / 4.) {
                cisTrans = CMLBond.TRANS;
            }
        } catch (Exception e) {
            cisTrans = CMLBond.LINEAR;
        }
        return cisTrans;
    }
    /**
     * uses 3D coordinates to add bondStereo.
     * 
     * @throws CMLException
     *             inappropriate connectivity, coordinates, etc.
     */
    public void add3DStereo() throws CMLException {
        // StereochemistryTool stereochemistryTool = new
        // StereochemistryTool(molecule);
        List<CMLBond> doubleBonds = molecule.getDoubleBonds();
        for (CMLBond bond : doubleBonds) {
            CMLBondStereo bondStereo3 = /* stereochemistryTool. */get3DBondStereo(bond);
            if (bondStereo3 != null) {
                bond.addBondStereo(bondStereo3);
            }
        }
        for (CMLAtom chiralAtom : new StereochemistryTool(molecule)
                .getChiralAtoms()) {
            CMLAtomParity atomParity3 = null;
            // atomParity3 = chiralAtom.get3DAtomParity();
            if (atomParity3 != null) {
                chiralAtom.addAtomParity(atomParity3);
            }
        }
    }
    /**
     * determines if 2D coordinates of atoms are suitable for bond
     * stereochemistry.
     * 
     * normally used for 2 atoms or bond and two ligands of one atom
     * 
     * gets cross products of at0-at1 X at0-lig0 and at0-at1 X at0-lig1 and then
     * gets their scalar product. If this is not negative, then the ligands are
     * unsatisfactory for determining stereo
     * 
     * @param atom0
     * @param atom1
     * @param ligand01
     * @param ligand02
     * @return scalar product or NaN
     */
    public static double get2DLigandScalarProduct(CMLAtom atom0, CMLAtom atom1,
            CMLAtom ligand01, CMLAtom ligand02) {
        double d = Double.NaN;
        Vector3 v0 = atom0.get2DCrossProduct(atom1, ligand01);
        Vector3 v1 = atom0.get2DCrossProduct(atom1, ligand02);
        if (v0 != null && v1 != null) {
            d = v0.dot(v1);
        }
        return d;
    }
    /**
     * determines if 3D coordinates of atoms are suitable for bond
     * stereochemistry.
     * 
     * normally used for 2 atoms or bond and two ligands of one atom
     * 
     * gets cross products of at0-at1 X at0-lig0 and at0-at1 X at0-lig1 and then
     * gets their scalar product. If this is not negative, then the ligands are
     * unsatisfactory for determining stereo
     * 
     * @param atom0
     * @param atom1
     * @param ligand01
     * @param ligand02
     * @return scalar product or NaN
     */
    public double get3DLigandScalarProduct(CMLAtom atom0, CMLAtom atom1,
            CMLAtom ligand01, CMLAtom ligand02) {
        double d = Double.NaN;
        Vector3 v0 = atom0.get3DCrossProduct(atom1, ligand01);
        Vector3 v1 = atom0.get3DCrossProduct(atom1, ligand02);
        if (v0 != null && v1 != null) {
            d = v0.dot(v1);
        }
        return d;
    }
}
