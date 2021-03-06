/**
 *    Copyright 2011 Peter Murray-Rust et al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * Tool to manage stereochemistry.
 *  
 * @author pm286 & ned24
 */
public class StereochemistryTool extends AbstractTool {
	Logger LOG = Logger.getLogger(StereochemistryTool.class);

	/** 
	 * For @role
	 */
	public final static String CML_CIP = "cml:cip";
	public final static String CIP_E = "E";
	public final static String CIP_Z = "Z";
	public final static String CIP_R = "R";
	public final static String CIP_S = "S";
	public final static String CIP_RSTAR = "R*";
	public final static String CIP_SSTAR = "S*";
	
	AbstractTool moleculeTool;
	private CMLMolecule molecule;

	/**
	 * Constructor with embedded molecule.
	 * 
	 * @param molecule
	 */
	public StereochemistryTool(CMLMolecule molecule) {
		this.setMolecule(molecule);
		moleculeTool = MoleculeTool.getOrCreateTool(molecule);
	}

	/**
	 * Uses 2D coordinates to add bondStereo.
	 */
	public void add2DStereo() {
		// MoleculeTool moleculeTool = MoleculeTool.getOrCreateMoleculeTool(molecule);
		List<CMLBond> acyclicDoubleBonds = new ConnectionTableTool(getMolecule())
		.getAcyclicDoubleBonds();
		for (CMLBond bond : acyclicDoubleBonds) {
			CMLBondStereo bondStereo = this.get2DBondStereo(bond);
			if (bondStereo != null) {
				bond.addBondStereo(bondStereo);
			}
		}
	}

	/**
	 * Calculates E or Z
	 * uses calculateBondStereo(CMLBond bond)
	 * NOT TESTED
	 * @param bond
	 * @return E or Z or null if this bond isnt a stereo centre or
	 *         there isnt enough stereo information to calculate stereo
	 */
	public String calculateCIPEZ(CMLBond bond) {
		CMLBondStereo bondStereo = this.calculateBondStereoForLigandsInCIPOrder(bond, (CoordinateType) null, null);
		String ez = null;
		if (bondStereo != null) {
			ez = (bondStereo.getXMLContent() == CMLBond.CIS) ? CIP_Z : CIP_E;
		}
		return ez;
	}

	/**
	 * Calculates the bondStereo of this atom
	 * (a) uses 2D coords OR
	 * (b) uses 3D coords OR
	 * (c) inputs EZ
	 * 
	 * @param bond
	 * @param type 2D or 3D or none
	 */
	public CMLBondStereo calculateBondStereoForLigandsInCIPOrder(
			CMLBond bond, CoordinateType type, String ez) {
		if (!bond.getOrder().equals(CMLBond.DOUBLE_NORM)) {
			// only double bonds count
			return null;
		}
		CMLAtom ligand0 = getHighestCIPLigand(bond, 0);
		CMLAtom ligand1 = getHighestCIPLigand(bond, 1);
		CMLBondStereo bondStereo = new CMLBondStereo();
		bondStereo.setAtomRefs4(
				ligand0.getId()+" "+
				bond.getAtomRefs2()[0]+" "+
				bond.getAtomRefs2()[1]+" "+
				ligand1.getId()
				);
		String ct = null;
		if (type == null) {
			if (CIP_Z.equals(ez)) {
				ct = CMLBond.CIS;
			}
			if (CIP_E.equals(ez)) {
				ct = CMLBond.TRANS;
			}
		} else if (type.equals(CoordinateType.TWOD)) {
			bondStereo = get2DBondStereo(bond);
		} else if (type.equals(CoordinateType.CARTESIAN)) {
			//bondStereo = get3DBondStereo(bond);
		}
		return bondStereo;
	}

	/**
	 * @param bond
	 */
	private CMLAtom getHighestCIPLigand(CMLBond bond, int atomSerial) {
		CMLAtom atom = bond.getAtom(atomSerial);
		List<CMLAtom> ligandList = this.getLigandsInCahnIngoldPrelogOrder(atom);
		CMLAtom ligandCIP = null;
		if (ligandList.size() == 1 || ligandList.size() == 2) {
			ligandCIP = ligandList.get(0);
		}
		return ligandCIP;
	}
	
	/**
	 * Adds wedge and hatch bonds.
	 * 
	 * Adds wedge / hatch to appropriate bonds / atoms if not already present and we
	 * have parities.
	 * 
	 * If any wedge / hatch is set, return without action (this may change).
	 * 
	 * @throws RuntimeException
	 */
	public void addWedgeHatchBonds() throws RuntimeException {
		for (CMLAtom chiralAtom : new StereochemistryTool(getMolecule()).getChiralAtoms()) {
			this.addWedgeHatchBond(chiralAtom);
		}
	}
	
	/**
	 * Calculates R or S.
	 * <p>
	 * Uses calculateAtomParity(CMLAtom atom).
	 * 
	 * @param atom
	 * @return R or S or null if this atom isn't a chiral centre or
	 *         there isn't enough stereo information to calculate parity
	 */
	public String calculateCIPRS(CMLAtom atom) {
		CMLAtomParity atomParity = this.calculateAtomParityForLigandsInCIPOrder(atom);
		String rs = null;
		if (atomParity != null) {
			rs = (atomParity.getXMLContent() > 0) ? CIP_R : CIP_S;
		}
		return rs;
	}

	/**
	 * Given R/S for atom, creates a new AtomParity element.
	 * <p>
	 * Uses calculateAtomParity(CMLAtom atom).
	 * 
	 * @param atom
	 * @return R or S or null if this atom isn't a chiral centre or
	 *         there isn't enough stereo information to calculate parity
	 */
	public CMLAtomParity calculateAtomParityFromCIPRS(CMLAtom atom, String rs) {
		if (!CIP_R.equals(rs) && !CIP_S.equals(rs)) {
			throw new RuntimeException("rs should be "+CIP_R + " or "+CIP_S);
		}
		List<CMLAtom> ligands = this.getLigandsInCahnIngoldPrelogOrder(atom);
		if (ligands == null) {
			throw new RuntimeException("null ligands");
		}
		if (ligands.size() != 4) {
			throw new RuntimeException("must have 4 discrete ligands for R/S: found "+ligands.size()+" for "+atom.getId());
		}
		CMLAtomParity atomParity = new CMLAtomParity();
		atomParity.setAtomRefs4(ligands.toArray(new CMLAtom[0]));
		// tune this by testing...
		atomParity.setXMLContent(rs.equals(CIP_R) ? 1.0 : -1.0);	
		return atomParity;
	}

	/**
	 * Calculates the atom parity of this atom using the coords of either 4
	 * explicit ligands or 3 ligands and this atom. If only 2D coords are
	 * specified then the parity is calculated using bond wedge/hatch
	 * information.
	 * 
	 * @param atom
	 * @return the CMLAtomParity, or null if this atom isn't a chiral centre or
	 *         there isn't enough stereo information to calculate parity; note that 
	 *         the atomParity is not necessarily created as a child of the atom
	 */
	public CMLAtomParity calculateAtomParityForLigandsInCIPOrder(CMLAtom atom) {
		if (!isChiralCentre(atom)) {
			return null;
		}
		List<CMLAtom> ligandList = getLigandsInCahnIngoldPrelogOrder(atom);

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
				//throw new RuntimeException(
				//"Insufficient coordinates on ligands to determine parity");
				return null;
			}
			atomRefs4[i] = ligandList.get(i).getId();
		}
		double parityDeterminant = determinant(parityMatrix);
		CMLAtomParity atomParity = new CMLAtomParity();
		if (Math.abs(parityDeterminant) > atomParity.minChiralDeterminant) {
			atomParity.setAtomRefs4(atomRefs4);
			//atomParity.setXMLContent(Math.signum(parityDeterminant));
			atomParity.setXMLContent(parityDeterminant);
			return atomParity;
		} else {
			return null;
		}
	}
	
	/**
	 * Calculates the atom parity of this atom using the coords of either 4
	 * explicit ligands or 3 ligands and this atom. If only 2D coords are
	 * specified then the parity is calculated using bond wedge/hatch
	 * information.
	 * 
	 * @param atom
	 * @return the CMLAtomParity, or null if this atom isn't a chiral centre or
	 *         there isn't enough stereo information to calculate parity; note that 
	 *         the atomParity is not necessarily created as a child of the atom
	 */
	public CMLAtomParity calculateAtomParityFromWedgesAndHatches(CMLAtom atom) {
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
					if (ligandBondStereo != null && ligandBond.getAtom(0).equals(atom)) {
						if (ligandBondStereo.getXMLContent().equals(
								CMLBondStereo.WEDGE)) {
							parityMatrix[3][i] = 1.0;
						} else if (ligandBondStereo.getXMLContent().equals(
								CMLBondStereo.HATCH)) {
							parityMatrix[3][i] = -1.0;
						}
					}
				}
			} else {
				// no coordinates!
				//throw new RuntimeException(
				//"Insufficient coordinates on ligands to determine parity");
				return null;
			}
			atomRefs4[i] = ligandList.get(i).getId();
		}
		double parityDeterminant = determinant(parityMatrix);
		CMLAtomParity atomParity = new CMLAtomParity();
		if (Math.abs(parityDeterminant) > atomParity.minChiralDeterminant) {
			atomParity.setAtomRefs4(atomRefs4);
			//atomParity.setXMLContent(Math.signum(parityDeterminant));
			atomParity.setXMLContent(parityDeterminant);
			return atomParity;
		} else {
			return null;
		}
	}

	/**
	 * Does exactly what it says on the tin. In the returned list the first atom
	 * has the highest priority, the last has the lowest priority.
	 * <p>
	 * Currently only works for C atoms with 4 ligands.
	 * 
	 * @param centralAtom
	 * @return ligands
	 */
	public List<CMLAtom> getLigandsInCahnIngoldPrelogOrder(CMLAtom centralAtom) {
		List<CMLAtom> ligandList = centralAtom.getLigandAtoms();
		List<CMLAtom> orderedLigandList = new ArrayList<CMLAtom>();
		orderedLigandList.add(ligandList.get(0));
		for (CMLAtom atom : ligandList) {
			for (int i = 0; i < orderedLigandList.size(); i++) {
				if (orderedLigandList.get(i) == atom) {
					continue;
				}
				CMLAtomSet markedAtoms = new CMLAtomSet();
				CMLAtomSet otherMarkedAtoms = new CMLAtomSet();
				markedAtoms.addAtom(centralAtom);
				otherMarkedAtoms.addAtom(centralAtom);
				int value = compareRecursivelyByAtomicNumber(orderedLigandList.get(i), 
						atom, markedAtoms, otherMarkedAtoms);
				//LOG.debug(orderedLigandList.get(i).getId()+"/"+atom.getId()+" = "+value);
				if (value == 1) {
					if (i + 1 == orderedLigandList.size()) {
						orderedLigandList.add(i + 1, atom);
						break;
					} else {
						continue;
					}
				} else if (value == -1) {
					orderedLigandList.add(i, atom);
					break;
				} else {
					throw new RuntimeException("Error getting ligands in CIP order.");
				}
			}
		}
		return orderedLigandList;
	}  

	private static double determinant(double[][] matrix) {
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
	public static boolean isChiralCentre(CMLAtom atom) {
		boolean mayBeChiral = false;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		if (AS.C.equals(atom.getElementType())) {
			// skip atoms with too few ligands
			boolean c3h = ligandList.size() == 3 && 
			atom.getHydrogenCountAttribute() != null &&
			atom.getHydrogenCount() == 1;
			if (ligandList.size() == 4 || c3h) {
				mayBeChiral = true;
				for (CMLAtom firstLigand : ligandList) {
					if (c3h && AS.H.equals(firstLigand.getElementType())) {
						// also have one implicit hydrogen, so not chiral
						mayBeChiral = false;
						return mayBeChiral;
					}
					for (CMLAtom secondLigand : ligandList) {
						if (firstLigand == secondLigand) {
							continue;
						}
						AtomTree firstAtomTree = new AtomTree(atom, firstLigand);
						AtomTree secondAtomTree = new AtomTree(atom, secondLigand);
						firstAtomTree.expandTo(5);
						secondAtomTree.expandTo(5);
						if (firstAtomTree.toString().equals(
								secondAtomTree.toString())) {
							// identical ligands
							mayBeChiral = false;
							return mayBeChiral;
						}
					}
				}
			}
		} else {
			mayBeChiral = false;
		}
		return mayBeChiral;
	}
	/**
	 * Gets list of atoms with enough different ligands to show chirality.
	 * <p>
	 * At present only processes Carbon. all four ligands must be different or
	 * either explicitly or with an implicit hydrogen and 3 explict ligands.
	 * <p>
	 * The atom may or may not have its chirality set by other means (e.g.
	 * atomParity) however an atom may not be tested for chirality if it is not
	 * in this list.
	 * 
	 * @return the list of atoms
	 */
	public List<CMLAtom> getChiralAtoms() {
		List<CMLAtom> chiralAtoms = new ArrayList<CMLAtom>();
		for (CMLAtom atom : getMolecule().getAtoms()) {
			boolean isChiral = isChiralCentre(atom);
			if (isChiral) {
				chiralAtoms.add(atom);
			}
		}
		return chiralAtoms;
	}
	/**
	 * Gets the bond stereo from 2D coordinates.
	 * 
	 * Gets atomRefs4 (at0, at1, at2, at3), then gets scalar product of at1-at0
	 * X at1-lig2 and at1-at2 X at1-lig2.
	 * 
	 * Does NOT add bondStereo as child (in case further decisions need to be
	 * made).
	 * 
	 * @param bond
	 * @return bondstereo (null if cannot calculate as CIS/TRANS)
	 * @throws RuntimeException
	 */
	public CMLBondStereo get2DBondStereo(CMLBond bond) {
		CMLBondStereo bondStereo = null;
		CMLAtom[] atom4 = BondTool.createAtomRefs4(bond);
		if (atom4 != null) {
			Vector3 v1 = AtomTool.getOrCreateTool(atom4[1]).get2DCrossProduct(atom4[2], atom4[0]);
			Vector3 v2 = AtomTool.getOrCreateTool(atom4[2]).get2DCrossProduct(atom4[1], atom4[3]);
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
	 * Uses bondStereo to adjust 2D coordinates.
	 * 
	 * @param bond
	 * @throws RuntimeException
	 */
	public void layoutDoubleBond(CMLBond bond) {
		CMLBondStereo bondStereo2 = null;
		CMLBondStereo bondStereo3 = null;
		// CMLMolecule molecule = this.getMolecule();
		bondStereo2 = this.get2DBondStereo(bond);
		bondStereo3 = this.create3DBondStereo(bond);
		if (bondStereo2 != null && bondStereo3 != null) {
			int match = bondStereo3.matchParity(bondStereo2);
			if (match == -1) {
				// LOG.debug("FLIP ");
				this.flip2D(bond);
			}
		}
	}
	
	/**
	 * Flips (about bond axis) the 2D coordinates attached to atom0.
	 * 
	 * @param bond
	 * @exception RuntimeException many, including invalid geometry operations
	 */
	public void flip2D(CMLBond bond) {
		// FIXME
		// flip2D(bond, this.getAtom(bond, 0));
	}

	/**
	 * Calculates whether geometry of bond is cis or trans.
	 * 
	 * Requires geometry of form: ligand0-atom(0)-atom(1)-ligand1 i.e. ligand0
	 * is a ligand of this,getAtom(0) and ligand1 is a ligand of this,getAtom(1).
	 * 
	 * If connectivity is not as above if this.getAtom(0) or
	 * this.getAtom(1) have > 3 ligands if either end of
	 * bond is effectively linear return BondTool.LINEAR.
	 * 
	 * @param ligand0 ligand(tool) for this.getAtom(0)
	 * @param ligand1 ligand(tool) for this.getAtom(1)
	 * 
	 * @return CIS, TRANS, UNKNOWN,
	 */
	
	/**
	 * Calculates whether geometry of bond is cis or trans.
	 * 
	 * Requires geometry of form: ligand0-atom(0)-atom(1)-ligand1 i.e. ligand0
	 * is a ligand of this,getAtom(0) and ligand1 is a ligand of this,getAtom(1).
	 * 
	 * If connectivity is not as above if this.getAtom(0) or
	 * this.getAtom(1) have > 3 ligands if either end of
	 * bond is effectively linear return BondTool.LINEAR.
	 * 
	 * If torsion angle is in range pi/4 < t < 3*pi/4 return UNKNOWN
	 * 
	 * @param bond
	 * @param ligand0 ligand(tool) for this.getAtom(0)
	 * @param ligand1 ligand(tool) for this.getAtom(1)
	 * @throws RuntimeException
	 * @return CIS, TRANS, UNKNOWN,
	 */
	public CMLBondStereo create3DBondStereo(CMLBond bond, CMLAtom ligand0,
			CMLAtom ligand1) {
		CMLBondStereo bondStereo = null;
		String cisTrans = CMLBond.UNKNOWN_ORDER;
		// wrong sort of bond
		// String order = getOrder();
		if (!CMLBond.isDouble(bond.getOrder())) {
			return null;
		}
		CMLAtom atom0 = bond.getAtom(0);
		if (getMolecule().getBond(atom0, ligand0) == null) {
			throw new RuntimeException("ligand0 is not connected to bond");
		}
		CMLAtom atom1 = bond.getAtom(1);
		if (getMolecule().getBond(atom1, ligand1) == null) {
			throw new RuntimeException("ligand1 is not connected to bond");
		}
		// no meaningful ligands or too many
		int ligandCount0 = atom0.getLigandAtoms().size();
		int ligandCount1 = atom1.getLigandAtoms().size();
		if ((ligandCount0 < 2 || ligandCount0 > 3)
				|| (ligandCount1 < 2 || ligandCount1 > 3)) {
			return null;
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
		List<CMLAtom> atoms = new ArrayList<CMLAtom>();
		atoms.add(ligand0);
		atoms.add(atom0);
		atoms.add(atom1);
		atoms.add(ligand1);
		String[] atomRefs4 = CMLAtomParity.createAtomRefs4(atoms);
		bondStereo = new CMLBondStereo();
		bondStereo.setAtomRefs4(atomRefs4);
		bondStereo.setXMLContent(cisTrans);
		return bondStereo;
	}

	/**
	 * Gets the bond stereo from 3D coordinates.
	 * 
	 * Gets atomRefs4 (at0, at1, at2, at3), then gets scalar product of at1-at0
	 * X at1-lig2 and at1-at2 X at1-lig2.
	 * 
	 * Does NOT add bondStereo as child (in case further decisions need to be
	 * made).
	 * 
	 * @param bond
	 * @return bondstereo (null if cannot calculate as CIS/TRANS)
	 */
	public CMLBondStereo create3DBondStereo(CMLBond bond) {
		CMLBondStereo bondStereo = null;
		CMLAtom[] atomRefs4 = null;
		atomRefs4 = BondTool.createAtomRefs4(bond);
		if (atomRefs4 != null) {
			Vector3 v1 = AtomTool.getOrCreateTool(atomRefs4[1]).get3DCrossProduct(atomRefs4[2], atomRefs4[0]);
			Vector3 v2 = AtomTool.getOrCreateTool(atomRefs4[2]).get3DCrossProduct(atomRefs4[1], atomRefs4[3]);
			double d = v1.dot(v2);
			if (Math.abs(d) > 0.000001) {
				bondStereo = new CMLBondStereo();
				bondStereo.setAtomRefs4(new String[] { atomRefs4[0].getId(),
						atomRefs4[1].getId(), atomRefs4[2].getId(), atomRefs4[3].getId() });
				bondStereo.setXMLContent((d > 0) ? CMLBond.TRANS : CMLBond.CIS);
			}
		}
		return bondStereo;
	}

	/**
	 * Uses 3D coordinates to add bondStereo.
	 */
	public void add3DStereo() {
		ConnectionTableTool ct = new ConnectionTableTool(getMolecule());
		List<CMLBond> cyclicBonds = ct.getCyclicBonds();
		List<CMLBond> doubleBonds = getMolecule().getDoubleBonds();
		addBondStereo(cyclicBonds, doubleBonds);
		List<CMLAtom> chiralAtoms = new StereochemistryTool(getMolecule()).getChiralAtoms();
		addAtomParity(chiralAtoms);
	}

	/**
	 * @param chiralAtoms
	 */
	private void addAtomParity(List<CMLAtom> chiralAtoms) {
		for (CMLAtom chiralAtom : chiralAtoms) {
			CMLAtomParity atomParity3 = null;
			atomParity3 = calculateAtomParityForLigandsInCIPOrder(chiralAtom);
			if (atomParity3 != null) {
				chiralAtom.addAtomParity(atomParity3);
			}
		}
	}

	/**
	 * @param cyclicBonds
	 * @param doubleBonds
	 */
	private void addBondStereo(List<CMLBond> cyclicBonds, List<CMLBond> doubleBonds) {
		for (CMLBond bond : doubleBonds) {
			if (!cyclicBonds.contains(bond)) {
				CMLBondStereo bondStereo3 = create3DBondStereo(bond);
				if (bondStereo3 != null) {
					bond.addBondStereo(bondStereo3);
				}
			}
		}
	}

	/**
	 * Determines if 2D coordinates of atoms are suitable for bond
	 * stereochemistry.
	 * 
	 * Normally used for 2 atoms or bond and two ligands of one atom
	 * 
	 * Gets cross products of at0-at1 X at0-lig0 and at0-at1 X at0-lig1 and then
	 * gets their scalar product. If this is not negative, then the ligands are
	 * unsatisfactory for determining stereo.
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
		Vector3 v0 = AtomTool.getOrCreateTool(atom0).get2DCrossProduct(atom1, ligand01);
		Vector3 v1 = AtomTool.getOrCreateTool(atom0).get2DCrossProduct(atom1, ligand02);
		if (v0 != null && v1 != null) {
			d = v0.dot(v1);
		}
		return d;
	}
	
	/**
	 * Determines if 3D coordinates of atoms are suitable for bond
	 * stereochemistry.
	 * 
	 * Normally used for 2 atoms or bond and two ligands of one atom.
	 * 
	 * Gets cross products of at0-at1 X at0-lig0 and at0-at1 X at0-lig1 and then
	 * gets their scalar product. If this is not negative, then the ligands are
	 * unsatisfactory for determining stereo.
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
		Vector3 v0 = AtomTool.getOrCreateTool(atom0).get3DCrossProduct(atom1, ligand01);
		Vector3 v1 = AtomTool.getOrCreateTool(atom0).get3DCrossProduct(atom1, ligand02);
		if (v0 != null && v1 != null) {
			d = v0.dot(v1);
		}
		return d;
	}
	
	/**
	 * Gets first bond which is not already a wedge / hatch.
	 *
	 * Tries to get an X-H or other acyclic bond if possible bond must have first
	 * atom equal to thisAtom so sharp end of bond can be managed.
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
			if (AS.H.equals(atomx.getElementType())
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
				if (CMLBond.ACYCLIC.equals(bondx.getCyclic())
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
	 * Uses atomParity to create wedge or hatch.
	 *
	 * @param atom
	 * @throws RuntimeException inconsistentencies in diagram, etc.
	 */
	public void addWedgeHatchBond(CMLAtom atom) throws RuntimeException {
		CMLBond bond = getFirstWedgeableBond(atom);
		if (bond == null) {
			LOG.info("Cannot find ANY free wedgeable bonds! "
					+ atom.getId());
		} else {
			String bondType = getWedgeHatchForBondAndParity(atom, bond);
			if (bondType != null) {
				CMLBondStereo bondStereo = new CMLBondStereo();
				bondStereo.setXMLContent(bondType);
				bond.addBondStereo(bondStereo);
			}
		}
	}

	public String getWedgeHatchForBondAndParity(CMLAtom atom, CMLBond bond) {
		String bondType = null;
		CMLAtomParity atomParity = (CMLAtomParity) atom
		.getFirstChildElement(CMLAtomParity.TAG, CMLConstants.CML_NS);
		if (atomParity != null) {
			AtomTool atomTool = AtomTool.getOrCreateTool(atom);
			int totalParity = 0;
			int sense = 0;
			CMLAtom[] atomRefs4x = atomParity.getAtomRefs4(getMolecule());
			int atomParityValue = atomParity.getIntegerValue();
			int highestPriorityAtom = 1;
			// 3 explicit ligands
			if (atomRefs4x[3].equals(atom)) {
				double d = this.getSenseOf3Ligands(atom, atomRefs4x);
				if (Math.abs(d) > 0.000001) {
					sense = (d < 0) ? -1 : 1;
					totalParity = sense * atomParityValue;
				}
				// 4 explicit ligands
			} else {
				CMLAtom otherAtom = null;
				for (CMLAtom at : bond.getAtoms()) {
					if (at != atom) {
						otherAtom = at;
						break;
					}
				}
				CMLAtom[] cyclicAtom4 = atomTool.getClockwiseLigands(atomRefs4x);
				List<CMLAtom> list = new LinkedList<CMLAtom>();
				for (CMLAtom cyclicAtom : cyclicAtom4) {
					list.add(cyclicAtom);
				}
				
				if (otherAtom == atomRefs4x[0]) {
				    highestPriorityAtom = -1;
				}
				
				String intStr = "";
				for (int l = 0; l < list.size(); l++) {
					CMLAtom at = list.get(l);
					if (at.getId().equals(otherAtom.getId())) continue;
					for (int i = 0; i < atomRefs4x.length; i++) {
						if (at.getId().equals(atomRefs4x[i].getId())) {
							intStr += ""+i;
						}
					}
				}
				String[] clockwiseStrings = {"012", "201", "120", 
						"023", "302", "230",
						"013", "301", "130",
						"123", "312", "231"};
				String[] antiClockwiseStrings = {"210", "021", "102",
						"310", "031", "103",
						"320", "032", "203",
						"321", "132", "213"};
				for (String str : clockwiseStrings) {
					if (intStr.equals(str)) {
						sense = 1;
						break;
					}
				}
				if (sense == 0) {
					for (String str : antiClockwiseStrings) {
						if (intStr.equals(str)) {
							sense = -1;
							break;
						}
					}
				}
				totalParity = sense * atomParityValue * highestPriorityAtom;
			}
			bondType = (totalParity > 0) ? CMLBond.HATCH
					: CMLBond.WEDGE;
		}
		return bondType;
	}

	/**
	 * Not fully written. TODO
	 *
	 * @param atom
	 * @param array
	 * @return value
	 */
	private double getSenseOf3Ligands(CMLAtom atom, CMLAtom[] array) {
		return 0;
	}

	/**
	 * Gets ligands of this atom not in markedAtom set sorted in decreasing
	 * atomic number.
	 *
	 * Sorts the unvisisted atoms in decreasing atomic number. If atomic numbers
	 * are tied, any order is permitted.
	 *
	 * @param atom
	 * @param markedAtoms atoms already visited and not to be included
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
	 * Gets priority relative to other atom.
	 *
	 * Uses simple CIP prioritization (does not include stereochemistry) if
	 * thisAtom has higher priority return 1 if thisAtom and otherAtom have
	 * equal priority return 0 if otherAtom has higher priority return -1.
	 *
	 * Compares atomic numbers. If thisAtom has higher atomicNumber return 1 if
	 * otherAtom has higher atomicNumber return -1 if equal, visit new ligands
	 * of each atom, sorted by priority until a mismatch is found or the whole
	 * of the ligand trees are visited.
	 *
	 * Example:
	 * 
	 * <code>CMLAtomSet thisSetTool = new AtomSetToolImpl();
	 * CMLAtomSet otherSetTool = new AtomSetToolImpl();
	 * int res = atomTool.compareByAtomicNumber(otherAtom, thisSetTool, otherSetTool);</code>
	 *
	 * @param atom
	 * @param otherAtom
	 * @param markedAtoms atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 * @param otherMarkedAtoms atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 *
	 * @return int the comparison
	 */
	private int compareRecursivelyByAtomicNumber(CMLAtom atom, CMLAtom otherAtom,
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
				CMLAtom otherLigand = otherSortedLigands[i];
				comp = compareByAtomicNumber(thisLigand, otherLigand);

				if (comp != 0) {
					break;
				}
			}
			if (comp == 0) {
				for (int i = 0; i < length; i++) {
					CMLAtom thisLigand = thisSortedLigands[i];
					CMLAtom otherLigand = otherSortedLigands[i];		
					comp = compareRecursivelyByAtomicNumber(thisLigand, otherLigand, markedAtoms, otherMarkedAtoms);

					if (comp != 0) {
						break;
					}
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

	private int compareByAtomicNumber(CMLAtom atom, CMLAtom otherAtom) {	
		int a = atom.getAtomicNumber();
		int b = otherAtom.getAtomicNumber();

		if (a > b) {
			return 1;
		} else if (a < b) {
			return -1;
		} else {
			return 0;
		}
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
	
	public void addCIPLabels() {
		List<CMLAtom> atomList = getChiralAtoms();
		atomList = getChiralAtoms();
		for (CMLAtom atom : atomList) {
			addCIPLabel(atom);
		}
	}

	private void addCIPLabel(CMLAtom atom) {
		String rs = calculateCIPRS(atom);
		Nodes labels = atom.query("cml:label[@role='"+StereochemistryTool.CML_CIP+"']", CC.CML_XPATH);
		CMLLabel label = null;
		if (labels.size() == 0) {
			label = new CMLLabel();
			label.setCMLValue(rs);
			label.addAttribute(new Attribute("role", StereochemistryTool.CML_CIP));
			atom.addLabel(label);
		} else {
			label = (CMLLabel) labels.get(0);
		}
		LOG.debug("CIP: "+label.getCMLValue());
	}

	/**
	 * Returns label of form:
	 * &lt;atom&gt;
	 *   &lt;label role='cml:cip' value='R'/&gt;
	 * &lt;/atom&gt;
	 * 
	 * @param atom
	 * @return label or null
	 */
	public static CMLLabel getCIPRSLabel(CMLAtom atom) {
		CMLLabel label = null;
		Nodes labels = atom.query("cml:label[@role='"+CML_CIP+"']", CMLConstants.CML_XPATH);
		if (labels.size() != 0) {
			label = (CMLLabel) labels.get(0);
		}
		return label;
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	public List<CMLAtom> getAtomsAtPointyBondEnds() {
		CMLMolecule stereoMol = getMolecule();
		List<CMLBond> bondList = stereoMol.getBonds();
		Set<CMLAtom> pointyAtomSet = new HashSet<CMLAtom>();
		for (CMLBond bond : bondList) {
			CMLBondStereo bondStereo = bond.getBondStereo();
			if (bondStereo != null) {
				CMLAtom pointyAtom = bond.getAtom(0);
				pointyAtomSet.add(pointyAtom);
			}
		}
		return new ArrayList<CMLAtom>(pointyAtomSet);
	}

	public void addCalculatedAtomParityForPointyAtoms() {
		List<CMLAtom> pointyAtoms = getAtomsAtPointyBondEnds();
		for (CMLAtom pointyAtom : pointyAtoms) {
			CMLAtomParity atomParity = calculateAtomParityFromWedgesAndHatches(pointyAtom);
			if (atomParity != null) {
				pointyAtom.appendChild(atomParity);
			}
		}
	}
	
}