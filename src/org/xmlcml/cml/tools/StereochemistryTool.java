package org.xmlcml.cml.tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLAtomSet;
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
            this.addWedgeHatchBond(atom);
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
            this.addWedgeHatchBond(chiralAtom);
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
            logger.severe("No stereo for: " + this + S_SLASH + e);
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
	/**
	 * gets first bond which is not already a wedge/hatch.
	 *
	 * try to get an X-H or other acyclic bond if possible bond must have first
	 * atom equal to thisAtom so sharp end of bond can be managed
	 *
	 * @param tool TODO
	 * @param atom TODO
	 * @return the bond
	 */
	CMLBond getFirstWedgeableBond(CMLAtom atom) {
		List<CMLAtom> atomList = atom.getLigandAtoms();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		CMLBond bond = null;
		for (int i = 0; i < ligandBondList.size(); i++) {
			CMLBond bondx = ligandBondList.get(i);
			CMLAtom atomx = atomList.get(i);
			if (bondx.getBondStereo() != null) {
				continue;
			}
			// select any H
			if (atomx.getElementType().equals("H")
					&& bondx.getAtom(0).equals(atom)) {
				bond = bondx;
				break;
			}
		}
		if (bond == null) {
			for (int i = 0; i < atomList.size(); i++) {
				CMLBond bondx = ligandBondList.get(i);
				if (bondx.getBondStereo() != null) {
					continue;
				}
				// or any acyclic bond
				if (bondx.getCyclic().equals(CMLBond.ACYCLIC)
						&& bondx.getAtom(0).equals(atom)) {
					bond = bondx;
					break;
				}
			}
		}
		// OK, get first unmarked bond
		if (bond == null) {
			for (int i = 0; i < atomList.size(); i++) {
				CMLBond bondx = ligandBondList.get(i);
				if (bondx.getBondStereo() != null) {
					continue;
				}
				if (bondx.getAtom(0).equals(atom)) {
					bond = bondx;
					break;
				}
			}
		}
		return bond;
	}
	/**
	 * uses atomParity to create wedge or hatch.
	 *
	 * @param tool TODO
	 * @param atom
	 * @throws CMLRuntimeException
	 *             inconsistentencies in diagram, etc.
	 */
	public void addWedgeHatchBond(CMLAtom atom) throws CMLRuntimeException {
		CMLAtom[] atom4 = new CMLAtom[4];// getAtomRefs4();
		if (atom4 != null) {
			CMLBond bond = getFirstWedgeableBond(atom);
			int totalParity = 0;
			if (bond == null) {
				logger.info("Cannot find ANY free wedgeable bonds! "
						+ atom.getId());
			} else {
				final CMLAtomParity atomParity = (CMLAtomParity) atom
				.getFirstChildElement(CMLAtomParity.TAG, CMLConstants.CML_NS);
				if (atomParity != null) {
	
					CMLAtom[] atomRefs4x = atomParity.getAtomRefs4(molecule);
					int atomParityValue = atomParity.getIntegerValue();
					// three explicit ligands
					if (atomRefs4x[3].equals(atom)) {
						double d = this.getSenseOf3Ligands(atom, atomRefs4x);
						if (Math.abs(d) > 0.000001) {
							int sense = (d < 0) ? -1 : 1;
							totalParity = sense * atomParityValue;
						}
						// 4 explicit ligands
					} else {
						CMLAtom[] cyclicAtom4 = atom.getClockwiseLigands(atomRefs4x);
						// bond position matters here. must find bond before
						// compareAtomRefs4
						// as it scrambles the order
						int serial = -1;
						for (int i = 0; i < 4; i++) {
							if (cyclicAtom4[i].equals(bond.getOtherAtom(atom))) {
								serial = i;
								break;
							}
						}
						if (serial == -1) {
							throw new CMLRuntimeException(
									"Cannot find bond in cyclicAtom4 (bug)");
						}
						// how does this differ from the original atomRefs4?
						int sense = 1; // FIXME
						// CMLAtom.compareAtomRefs4(atomRefs4x,
						// cyclicAtom4);
						int positionParity = 1 - 2 * (serial % 2);
						totalParity = sense * atomParityValue * positionParity;
					}
					String bondType = (totalParity > 0) ? CMLBond.WEDGE
							: CMLBond.HATCH;
					CMLBondStereo bondStereo = new CMLBondStereo();
					bondStereo.setXMLContent(bondType);
					bond.addBondStereo(bondStereo);
	
				} else {
					// throw new CMLException ("BUG :: AddWedgeHatchBond ->
					// atomParity is null for atom " + atom.getId ());
				}
			}
		}
	}
	/**
	 * not fully written.
	 *
	 * @param atom
	 * @param array
	 * @return value
	 */
	double getSenseOf3Ligands(CMLAtom atom, CMLAtom[] array) {
		return 0;
	}
	/**
	 * gets atomSet for ligand in prioritized order.
	 *
	 * ligand is expanded as a breadth-first atomSet. This is ordered so that
	 * atoms appear in CIP order. (stereochemistry within ligand is ignored as
	 * too dificult at this stage, so full CIP is not supported). the set for
	 * each ligand can be retrieved by getLigandAtomSet(ligandAtoms). Where
	 * ligands are cyclic the set may continue until it reaches thisatom or
	 * ligandAtoms. thus in some cases the whole molecule except this atom may
	 * be returned
	 *
	 * @param tool TODO
	 * @param atom
	 * @param ligandAt
	 * @return the atomSet (icludes the immediate ligand atom, but not
	 *         thisatom).
	 */
	CMLAtomSet getPrioritizedLigand(CMLAtom atom, CMLAtom ligandAt) {
		CMLAtomSet[] prioritizedLigands = this.getPrioritizedLigands(atom);
		CMLAtomSet atomSet = null;
		if (ligandAt != null) {
			this.getPrioritizedLigands(atom);
			for (int i = 0; i < prioritizedLigands.length; i++) {
				List<CMLAtom> ligandList = atom.getLigandAtoms();
				if (ligandAt.equals(ligandList.get(i))) {
					atomSet = prioritizedLigands[i];
					break;
				}
			}
		}
		return atomSet;
	}
	/**
	 * gets all ligandAtomSets for atom.
	 *
	 * getLigandAtomSet(ligandAtom) is called for each ligand the results are
	 * returned *in ligand order*, not in priority, so they can be synchronized
	 * with ligandAtoms[] and ligandBonds[]
	 *
	 * @param tool TODO
	 * @param atom
	 * @return ligands as array of atomSets
	 */
	CMLAtomSet[] getPrioritizedLigands(CMLAtom atom) {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		CMLAtomSet[] prioritizedLigands = new CMLAtomSet[ligands.size()];
		for (int i = 0; i < prioritizedLigands.length; i++) {
			CMLAtom atomi = ligands.get(i);
			prioritizedLigands[i] = getPrioritizedLigand(atom, atomi);
			if (prioritizedLigands[i] == null) {
				CMLAtomSet atomSet = new CMLAtomSet();
				this.createPrioritizedLigand(atom, atomSet, atomi);
				prioritizedLigands[i] = atomSet;
			}
		}
		return prioritizedLigands;
	}
	/**
	 * not fully developed.
	 *
	 * @param atom
	 * @param atomSetTool
	 * @param upstreamAtom
	 * @deprecated
	 */
	void createPrioritizedLigand(CMLAtom atom, CMLAtomSet atomSetTool, CMLAtom upstreamAtom) {
	
		// CMLAtom ligands
	}
	
	/**
	 * get ligands of this atom not in markedAtom set sorted in decreasing
	 * atomic number.
	 *
	 * sort the unvisisted atoms in decreasing atomic number. If atomic numbers
	 * are tied, any order is permitted
	 *
	 * @param atom
	 * @param markedAtoms
	 *            atoms already visited and not to be included
	 *
	 * @return the new ligands in decreasing order of atomic mass.
	 */
	private CMLAtom[] getNewLigandsSortedByAtomicNumber(CMLAtom atom,
			CMLAtomSet markedAtoms) {
		List<CMLAtom> newLigandVector = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			if (!markedAtoms.contains(ligandAtom)) {
				newLigandVector.add(ligandAtom);
			}
		}
		CMLAtom[] newLigands = new CMLAtom[newLigandVector.size()];
		int count = 0;
		while (newLigandVector.size() > 0) {
			int heaviest = getHeaviestAtom(newLigandVector);
			CMLAtom heaviestAtom = newLigandVector.get(heaviest);
			newLigands[count++] = heaviestAtom;
			newLigandVector.remove(heaviest);
		}
		return newLigands;
	}

	/**
	 * get priority relative to other atom.
	 *
	 * uses simple CIP prioritization (does not include stereochemistry) if
	 * thisAtom has higher priority return 1 if thisAtom and otherAtom have
	 * equal priority return 0 if otherAtom has higher priority return -1
	 *
	 * compare atomic numbers. if thisAtom has higher atomicNumber return 1 if
	 * otherAtom has higher atomicNumber return -1 if equal, visit new ligands
	 * of each atom, sorted by priority until a mismatch is found or the whole
	 * of the ligand trees are visited
	 *
	 * example: CMLAtomSet thisSetTool = new AtomSetToolImpl(); CMLAtomSet
	 * otherSetTool = new AtomSetToolImpl(); int res =
	 * atomTool.compareByAtomicNumber(otherAtom, thisSetTool, otherSetTool);
	 *
	 * @param atom
	 * @param otherAtom
	 * @param markedAtoms
	 *            atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 * @param otherMarkedAtoms
	 *            atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 *
	 * @return int the comparison
	 */
	// FIXME
	@SuppressWarnings("unused")
	private int compareByAtomicNumber(CMLAtom atom, CMLAtom otherAtom,
			CMLAtomSet markedAtoms, CMLAtomSet otherMarkedAtoms) {
		// compare on atomic number
		int comp = atom.compareByAtomicNumber(otherAtom);
		if (comp == 0) {
			markedAtoms.addAtom(atom);
			otherMarkedAtoms.addAtom(otherAtom);
			CMLAtom[] thisSortedLigands = getNewLigandsSortedByAtomicNumber(
					atom, markedAtoms);
			CMLAtom[] otherSortedLigands = getNewLigandsSortedByAtomicNumber(
					otherAtom, otherMarkedAtoms);
			int length = Math.min(thisSortedLigands.length,
					otherSortedLigands.length);
			for (int i = 0; i < length; i++) {
				CMLAtom thisLigand = thisSortedLigands[i];
				comp = this.compareByAtomicNumber(thisLigand, otherSortedLigands[i],
						markedAtoms, otherMarkedAtoms);
				if (comp != 0) {
					break;
				}
			}
			if (comp == 0) {
				// if matched so far, prioritize longer list
				if (thisSortedLigands.length > otherSortedLigands.length) {
					comp = 1;
				} else if (thisSortedLigands.length < otherSortedLigands.length) {
					comp = -1;
				}
			}
		}
		return comp;
	}

	private int getHeaviestAtom(List<CMLAtom> newAtomVector) {
		int heaviestAtNum = -1;
		int heaviest = -1;
		for (int i = 0; i < newAtomVector.size(); i++) {
			CMLAtom atom = newAtomVector.get(i);
			int atnum = atom.getAtomicNumber();
			if (atnum > heaviestAtNum) {
				heaviest = i;
				heaviestAtNum = atnum;
			}
		}
		return heaviest;
	}

	
}
