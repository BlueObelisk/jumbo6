package org.xmlcml.cml.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.base.CMLElement.Hybridization;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLAtomArray;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLBondArray;
import org.xmlcml.cml.element.lite.CMLBondStereo;
import org.xmlcml.cml.element.lite.CMLFormula;
import org.xmlcml.cml.element.lite.CMLLabel;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.lite.CMLName;
import org.xmlcml.cml.element.lite.CMLProperty;
import org.xmlcml.cml.element.lite.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.main.CMLAmount;
import org.xmlcml.cml.element.main.CMLAngle;
import org.xmlcml.cml.element.main.CMLAtomSet;
import org.xmlcml.cml.element.main.CMLBondSet;
import org.xmlcml.cml.element.main.CMLCrystal;
import org.xmlcml.cml.element.main.CMLElectron;
import org.xmlcml.cml.element.main.CMLLength;
import org.xmlcml.cml.element.main.CMLLink;
import org.xmlcml.cml.element.main.CMLMap;
import org.xmlcml.cml.element.main.CMLMatrix;
import org.xmlcml.cml.element.main.CMLMoleculeList;
import org.xmlcml.cml.element.main.CMLSymmetry;
import org.xmlcml.cml.element.main.CMLTorsion;
import org.xmlcml.cml.element.main.CMLTransform3;
import org.xmlcml.cml.element.main.CMLMap.Direction;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGRect;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Real3Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.RealSquareMatrix;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.molutil.ChemicalElement.Type;

/**
 * additional tools for molecule. not fully developed
 *
 * @author pmr
 *
 */
public class MoleculeTool extends AbstractSVGTool {

	static final Logger LOG = Logger.getLogger(MoleculeTool.class.getName());

    /** dewisott */
	public static String HYDROGEN_COUNT = "hydrogenCount";
	
	private CMLMolecule molecule;
	private Map<CMLAtom, AtomTool> atomToolMap;
	private Map<CMLBond, BondTool> bondToolMap;
	private SelectionTool selectionTool;
	private MoleculeDisplay moleculeDisplay;
	private CMLAtom currentAtom;
	private CMLBond currentBond;

	private Morgan morgan;
	

	/**
	 * constructor
	 *
	 * @param molecule
	 * @deprecated use getOrCreateTool
	 */
	public MoleculeTool(CMLMolecule molecule) {
		this.molecule = molecule;
		this.molecule.setTool(this);
	}

	/**
	 * get molecule.
	 *
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
	}
	
	private void enableAtomToolMap() {
		if (this.atomToolMap == null) {
			this.atomToolMap = new HashMap<CMLAtom, AtomTool>();
		}
	}
	private void enableBondToolMap() {
		if (this.bondToolMap == null) {
			this.bondToolMap = new HashMap<CMLBond, BondTool>();
		}
	}

	/**
	 * @param atom 
	 * @return atomTool (created if not present)
	 */
	public AtomTool getOrCreateAtomTool(CMLAtom atom) {
		enableAtomToolMap();
		AtomTool atomTool = atomToolMap.get(atom);
		if (atomTool== null) {
			atomTool = AtomTool.getOrCreateTool(atom);
			atomToolMap.put(atom, atomTool);
		}
		return atomTool;
	}

	/**
	 * @param bond 
	 * @return bondTool (created if not present)
	 */
	public BondTool getOrCreateBondTool(CMLBond bond) {
		enableBondToolMap();
		BondTool bondTool = bondToolMap.get(bond);
		if (bondTool== null) {
			bondTool = BondTool.getOrCreateTool(bond);
			bondToolMap.put(bond, bondTool);
		}
		return bondTool;
	}
	
	/** gets MoleculeTool associated with molecule.
	 * if null creates one and sets it in molecule
	 * @param molecule
	 * @return tool
	 */
	@SuppressWarnings("all")
	public static MoleculeTool getOrCreateTool(CMLMolecule molecule) {
		MoleculeTool moleculeTool = null;
		if (molecule != null) {
			moleculeTool = (MoleculeTool) molecule.getTool();
			if (moleculeTool == null) {
				moleculeTool = new MoleculeTool(molecule);
				molecule.setTool(moleculeTool);
			}
		}
		return moleculeTool;
	}

	/** get charge.
	 * 
	 * @return charge
	 */
	public int getFormalCharge() {
		int formalCharge = 0;
		Nodes chargedAtoms = molecule.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge]", CML_XPATH);
		for (int i = 0; i < chargedAtoms.size(); i++) {
			formalCharge += Integer.parseInt(((Element)chargedAtoms.get(i)).getAttributeValue("formalCharge"));
		}
		return formalCharge;
	}
	
	/** get charged atoms.
	 * 
	 * @return atoms
	 */
	public List<CMLAtom> getChargedAtoms() {
		List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
		Nodes atoms = molecule.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge != 0]", CML_XPATH);
		for (int i = 0; i < atoms.size(); i++) {
			chargedAtoms.add((CMLAtom)atoms.get(i));
		}
		return chargedAtoms;
	}


	/**
	 * Adjust bond orders to satisfy valence.
	 *
	 * in impossible systems appropriate atoms are marked as radicals
	 * (spinMultiplicity) assumes explicit hydrogens
	 * uses default PISystemManager
	 */
	public void adjustBondOrdersToValency() {
        molecule.setBondOrders(CMLBond.SINGLE);
        PiSystemControls piSystemManager = new PiSystemControls();
        piSystemManager.setUpdateBonds(true);
//        piSystemManager.setKnownUnpaired(knownUnpaired);
        piSystemManager.setDistributeCharge(true);
		this.adjustBondOrdersToValency(piSystemManager);
	}
	
	/**
	 * Adjust bond orders to satisfy valence.
	 *
	 * in impossible systems appropriate atoms are marked as radicals
	 * (spinMultiplicity) assumes explicit hydrogens
	 *
	 * @param piSystemManager
	 */
	public void adjustBondOrdersToValency(PiSystemControls piSystemManager) {
		// normalize bond orders
		molecule.setNormalizedBondOrders();
		PiSystem piSystem2 = new PiSystem(molecule.getAtoms());
		piSystem2.setPiSystemManager(new PiSystemControls(piSystemManager));
		List<PiSystem> piSystemList2 = piSystem2.generatePiSystemList();
		for (PiSystem subPiSystem : piSystemList2) {
			List<CMLAtom> atomList = subPiSystem.getAtomList();
			int npi = atomList.size();
			if (npi < 2) {
//				System.out.println("Cannot find pi system for " + npi);
			} else {
				subPiSystem.identifyDoubleBonds();
			}
		}
		// remove temporary pi electrons
		List<Node> electrons = CMLUtil.getQueryNodes(molecule, ".//"+CMLElectron.NS+"[@dictRef='cml:piElectron']", CML_XPATH);
		for (Node electron : electrons) {
			electron.detach();
		}
	}

	/**
<<<<<<< .working
=======
	 * get the double bond equivalents.
	 *
	 * this is the number of double bonds the atom can make an sp2 atom has 1 an
	 * sp atom has 2
	 *
	 * @param atom
	 * @param fcd
	 * @return the bond sum (0, 1, 2)
	 * @throws RuntimeException
	 *             if cannot get formal charges
	 */
	public int getDoubleBondEquivalents(CMLAtom atom, FormalChargeControl fcd) {
		if (atom.getMolecule() == null) {
			throw new RuntimeException("WARNING skipping DBE");
		}
		int valenceElectrons = atom.getValenceElectrons();
		int formalCharge = 0;
		try {
			formalCharge = atom.getFormalCharge(fcd);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		valenceElectrons -= formalCharge;
		int maxBonds = (valenceElectrons < 4) ? valenceElectrons
				: 8 - valenceElectrons;
		// hydrogens are include in bondSum
		int doubleBondEquivalents = maxBonds - this.getBondOrderSum(atom);
		return doubleBondEquivalents;
	}

	/**
	 * get sum of formal bondOrders. uses the actual ligands (i.e. implicit
	 * hydrogens are not used) aromatic bonds are counted as 1.5, 1,2,3 aromatic
	 * bonds add 1 double bond
	 *
	 * @param atom
	 * @return sum (-1 means cannot be certain)
	 */
	public int getBondOrderSum(CMLAtom atom) {
		int multipleBondSum = 0;
		int aromaticBondSum = 0;
		boolean ok = true;
		List<CMLBond> ligandBonds = atom.getLigandBonds();
		for (CMLBond ligandBond : ligandBonds) {
			// if the bond is to a H atom then don't increment
			// multipleBondSum as H's are dealt with further down
			// the method.
			boolean hasH = false;
			for (CMLAtom bondAt : ligandBond.getAtoms()) {
				if (AS.H.equals(bondAt.getElementType()) && bondAt != atom) {
					hasH = true;
				}
			}
			if (hasH) {
				continue;
			}
			String order = ligandBond.getOrder();
			if (order == null) {
				multipleBondSum += 0;
			} else if (order.equals(CMLBond.DOUBLE)) {
				multipleBondSum += 2;
			} else if (order.equals(CMLBond.TRIPLE)) {
				multipleBondSum += 3;
			} else if (order.equals(CMLBond.SINGLE)) {
				multipleBondSum += 1;
			} else if (order.equals(CMLBond.AROMATIC)) {
				aromaticBondSum += 1;
			} else {
				LOG.info("Unknown bond order:" + order + S_COLON);
				ok = false;
			}
		}
		// any aromatic bonds dictate SP2, so...
		if (aromaticBondSum > 0) {
			multipleBondSum = atom.getLigandBonds().size() + 1;
		}
		int hydrogenCount = 0;
		try {
			hydrogenCount = atom.getHydrogenCount();
		} catch (RuntimeException e) {
		}
		multipleBondSum += hydrogenCount;

		return ((ok) ? multipleBondSum : -1);
	}

	/**
	 * calculates the geometrical hybridization.
	 *
	 * calculates the geometrical hybridisation from 3D coords tetrahedron or
	 * pyramid gives SP3 trigonal gives SP2 bent gives BENT linear gives SP
	 * square planar gives SQ
	 *
	 * pyramidal and planar are distinguished by improper dihedral of 18 degrees
	 *
	 * @param atom
	 * @return hybridisation (SP3, SP2, BENT, SP, SQUARE_PLANAR)
	 */
	public Hybridization getGeometricHybridization(CMLAtom atom) {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		Hybridization geomHybridization = null;
		if (ligands.size() <= 1 || ligands.size() > 4) {
			return null;
		}
		Point3 thisXyz = atom.getXYZ3();
		Angle angle = null;
		if (thisXyz == null) {

		} else if (ligands.size() == 2) {
			Point3 thisXyz1 = ligands.get(0).getXYZ3();
			Point3 thisXyz2 = ligands.get(1).getXYZ3();
			try {
				angle = Point3.getAngle(thisXyz1, thisXyz, thisXyz2);
				if (angle.getDegrees() > 150) {
					geomHybridization = Hybridization.SP;
				} else {
					geomHybridization = Hybridization.BENT;
					// if (angle.getDegrees() > 115) return CMLAtom.SP2;
					// return CMLAtom.SP3;
				}
			} catch (Exception e) {
				LOG.error("BUG " + e);
			}
		} else if (ligands.size() == 3) {
			Point3 thisXyz1 = ligands.get(0).getXYZ3();
			Point3 thisXyz2 = ligands.get(1).getXYZ3();
			Point3 thisXyz3 = ligands.get(2).getXYZ3();
			// improper dihedral
			try {
				angle = Point3
				.getTorsion(thisXyz1, thisXyz2, thisXyz3, thisXyz);
				geomHybridization = Hybridization.SP2;
				if (Math.abs(angle.getDegrees()) > 18) {
					geomHybridization = Hybridization.SP3;
				}
			} catch (Exception e) {
				LOG.error("BUG " + e);
			}
		} else {
			geomHybridization = Hybridization.SP3;
		}
		return geomHybridization;
	}

	/**
	 * a simple lookup for common atoms.
	 *
	 * examples are C, N, O, F, Si, P, S, Cl, Br, I if atom has electronegative
	 * ligands, (O, F, Cl...) returns -1
	 *
	 * @param atom
	 * @return group
	 */
	public int getHydrogenValencyGroup(CMLAtom atom) {
		final int[] group = { 1, 4, 5, 6, 7, 4, 5, 6, 7, 7, 7 };
		final int[] eneg0 = { 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1 };
		final int[] eneg1 = { 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1 };
		int elNum = -1;
		try {
			String elType = atom.getElementType();
			elNum = CMLAtom.getCommonElementSerialNumber(elType);
			if (elNum == -1) {
				return -1;
			}
			if (eneg0[elNum] == 0) {
				return group[elNum];
			}
			// if atom is susceptible to enegative ligands, exit if they are
			// present
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			for (CMLAtom lig : ligandList) {
				int ligElNum = CMLAtom.getCommonElementSerialNumber(lig
						.getElementType());
				if (ligElNum == -1 || eneg1[ligElNum] == 1) {
					return -2;
				}
			}
		} catch (Exception e) {
			LOG.error("BUG " + e);
		}
		int g = (elNum == -1) ? -1 : group[elNum];
		return g;
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 * ignore if hydrogenCount attribute is set
	 * Uses algorithm: nH = 8 - group - sumbondorder + formalCharge, where group
	 * is 0-8 in first two rows
	 *
	 * @param atom
	 * @param control specifies whether H are explicit or in hydrogenCount
	 */
	public void adjustHydrogenCountsToValency(CMLAtom atom,
			CMLMolecule.HydrogenControl control) {
		if (atom.getHydrogenCountAttribute() == null) {
			int group = this.getHydrogenValencyGroup(atom);
			if (group == -1) {
				return;
			} else if (group == -2) {
				return;
			}
			// hydrogen and metals
			if (group < 4) {
				return;
			}
			int sumBo = getSumNonHydrogenBondOrder(molecule, atom);
			int fc = (atom.getFormalChargeAttribute() == null) ? 0 : atom
					.getFormalCharge();
			int nh = 8 - group - sumBo + fc;
			// non-octet species
			if (group == 4 && fc == 1) {
				nh -= 2;
			}
			atom.setHydrogenCount(nh);
			this.expandImplicitHydrogens(atom, control);
		}
	}

	/**
>>>>>>> .merge-right.r915
	 * get calculated molecular mass. 
     * uses hydrogenCount attribute to check hydrogens
	 * @return calculated molecular mass.
	 * @throws RuntimeException unknown/unsupported element type (Dummy counts as zero mass)
	 */
	public double getCalculatedMolecularMass() throws RuntimeException {
		getTotalHydrogenCount();
		return this.getCalculatedMolecularMass(HydrogenControl.USE_HYDROGEN_COUNT);
	}
	
	/** gets total hydrogen count on molecule.
	 * if molecule@hydrogenCount uses that
	 * else ensures all atoms have hydrogenCount
	 * if not uses adjustHydrogenCountsToValency()
	 * then summs over all atoms
	 * then adds hydrogenCountAttribute
	 * @return total hydrogen count
	 */
	public int getTotalHydrogenCount() {
		String hydrogenCount = molecule.getAttributeValue(HYDROGEN_COUNT);
		int sum = -1;
		if (hydrogenCount == null || S_EMPTY.equals(hydrogenCount.trim())) {
			this.adjustHydrogenCountsToValency(HydrogenControl.NO_EXPLICIT_HYDROGENS);
			sum = sumHydrogenCountOnAtoms();
			molecule.addAttribute(new Attribute(HYDROGEN_COUNT, ""+sum));
		} else {
			sum = Integer.parseInt(hydrogenCount);
		}
		return sum;
	}

	private int sumHydrogenCountOnAtoms() {
		int sum = 0;
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (!AS.H.equals(atom.getElementType())) {
				sum += atom.getHydrogenCount();
			}
		}
		return sum;
	}

	/**
<<<<<<< .working
=======
	 * Sums the formal orders of all bonds from atom to non-hydrogen ligands.
	 *
	 * Uses 1,2,3,A orders and creates the nearest integer. Thus 2 aromatic
	 * bonds sum to 3 and 3 sum to 4. Bonds without order are assumed to be
	 * single
	 *
	 * @param atom
	 * @exception RuntimeException
	 *                null atom in argument
	 * @return sum of bond orders. May be 0 for isolated atom or atom with only
	 *         H ligands
	 */
	public int getSumNonHydrogenBondOrder(CMLAtom atom) {
		return MoleculeTool.getSumNonHydrogenBondOrder(this.molecule, atom);
	}
	/**
	 * Sums the formal orders of all bonds from atom to non-hydrogen ligands.
	 *
	 * Uses 1,2,3,A orders and creates the nearest integer. Thus 2 aromatic
	 * bonds sum to 3 and 3 sum to 4. Bonds without order are assumed to be
	 * single
	 * @param molecule 
	 *
	 * @param atom
	 * @exception RuntimeException
	 *                null atom in argument
	 * @return sum of bond orders. May be 0 for isolated atom or atom with only
	 *         H ligands
	 */
	public static int getSumNonHydrogenBondOrder(CMLMolecule molecule, CMLAtom atom)
	throws RuntimeException {
		float sumBo = 0.0f;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (AS.H.equals(ligand.getElementType())) {
				continue;
			}
			CMLBond bond = molecule.getBond(atom, ligand);
			if (bond == null) {
				throw new RuntimeException(
						"Serious bug in getSumNonHydrogenBondOrder");
			}
			String bo = bond.getOrder();
			if (bo != null) {
				if (bo.equals(CMLBond.SINGLE) || bo.equals(CMLBond.SINGLE_S)) {
					sumBo += 1.0;
				}
				if (bo.equals(CMLBond.DOUBLE) || bo.equals(CMLBond.DOUBLE_D)) {
					sumBo += 2.0;
				}
				if (bo.equals(CMLBond.TRIPLE) || bo.equals(CMLBond.TRIPLE_T)) {
					sumBo += 3.0;
				}
				if (bo.equals(CMLBond.AROMATIC)) {
					sumBo += 1.4;
				}
			} else {
				// if no bond order, assume single
				sumBo += 1.0;
			}
		}
		return Math.round(sumBo);
	}

	/**
>>>>>>> .merge-right.r915
	 * deletes a hydrogen from an atom.
	 *
	 * used for building up molecules. If there are implicit H atoms it reduces
	 * the hydrogenCount by 1. If H's are explicit it removes the first hydrogen
	 * ligand
	 *
	 * @param atom
<<<<<<< .working
	 * @deprecated use AtomTool method
	 * @exception RuntimeException
=======
	 * @exception RuntimeException
>>>>>>> .merge-right.r915
	 *                no hydrogen ligands on atom
	 *                
	 */
	public void deleteHydrogen(CMLAtom atom) {
		if (atom.getHydrogenCountAttribute() != null) {
			if (atom.getHydrogenCount() > 0) {
				atom.setHydrogenCount(atom.getHydrogenCount() - 1);
			} else {
				throw new RuntimeException("No hydrogens to delete");
			}
		} else {
			Set<ChemicalElement> hSet = ChemicalElement
			.getElementSet(new String[] { AS.H.value });
			List<CMLAtom> hLigandVector = CMLAtom.filter(atom.getLigandAtoms(),
					hSet);
			if (hLigandVector.size() > 0) {
				molecule.deleteAtom(hLigandVector.get(0));
			} else {
				throw new RuntimeException("No hydrogens to delete");
			}
		}
		Set<ChemicalElement> hSet = ChemicalElement.getElementSet(
				new String[] { AS.H.value });
		List<CMLAtom> hLigandVector = CMLAtom.filter(atom.getLigandAtoms(),
				hSet);
		if (hLigandVector.size() > 0) {
			molecule.deleteAtom(hLigandVector.get(0));
		}
	}

	/**
	 * gets all atoms downstream of a bond.
	 *
	 * recursively visits all atoms in branch until all leaves have been
	 * visited. if branch is cyclic, halts when it rejoins atom or otherAtom
	 * Example:
	 *
	 * <pre>
	 *
	 *  MoleculeTool moleculeTool ... contains relevant molecule
	 *  CMLBond b ... (contains atom)
	 *  CMLAtom otherAtom = b.getOtherAtom(atom);
	 *  AtomSet ast = moleculeTool.getDownstreamAtoms(atom, otherAtom);
	 *
	 *  @param atom
	 *  @param otherAtom not to be visited
	 *  @return the atomSet (empty if none)
	 *
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom atom, CMLAtom otherAtom) {
		CMLAtomSet atomSet = new CMLAtomSet();
		boolean forceUpdate = false;
		getDownstreamAtoms(atom, atomSet, otherAtom, forceUpdate);
		atomSet.updateContent();
		return atomSet;
	}

	/**
	 * gets all atoms downstream of a bond.
	 * VERY SLOW I THINK
	 * recursively visits all atoms in branch until all leaves have been
	 * visited. if branch is cyclic, halts when it rejoins atom or otherAtom the
	 * routine is passed a new AtomSetImpl to be populated
	 * <pre>
	 *   Example: CMLBond b ... (contains atom)
	 *     CMLAtom otherAtom = b.getOtherAtom(atom);
	 *     AtomSet ast = new AtomSetImpl();
	 *     atom.getDownstreamAtoms(ast, otherAtom);
	 * </pre>
	 * @param atom
	 * @param atomSet
	 *            to accumulate ligand atoms
	 * @param otherAtom
	 *            not to be visited
	 *
	 */
	private void getDownstreamAtoms(CMLAtom atom, CMLAtomSet atomSet,
			CMLAtom otherAtom, boolean forceUpdate) {

		atomSet.addAtom(atom, forceUpdate);
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			// do not revisit atoms
			if (atomSet.contains(ligandAtom)) {
				;
				// do not backtrack
			} else if (ligandAtom.equals(otherAtom)) {
				;
			} else {
				this.getDownstreamAtoms(ligandAtom, atomSet, atom, forceUpdate);
			}
		}
	}

	/**
	 * append to id creates new id, perhaps to disambiguate
	 *
	 * @param atom
	 * @param s
	 */
	public void appendToId(CMLAtom atom, final String s) {
		String id = atom.getId();
		if ((id != null) && (id.length() > 0)) {
			atom.renameId(id + s);
		} else {
			atom.renameId(s);
		}
	}

	/**
	 * Adds 3D coordinates for singly-bonded ligands of this.
	 *
	 * <pre>
	 *       this is labelled A.
	 *       Initially designed for hydrogens. The ligands of A are identified
	 *       and those with 3D coordinates used to generate the new points. (This
	 *       allows structures with partially known 3D coordinates to be used, as when
	 *       groups are added.)
	 *       &quot;Bent&quot; and &quot;non-planar&quot; groups can be formed by taking a subset of the
	 *       calculated points. Thus R-NH2 could use 2 of the 3 points calculated
	 *       from (1,iii)
	 *       nomenclature: &quot;this&quot; is point to which new ones are &quot;attached&quot;.
	 *           this may have ligands B, C...
	 *           B may have ligands J, K..
	 *           points X1, X2... are returned
	 *       The cases (see individual routines, which use idealised geometry by default):
	 *       (0) zero ligands of A. The resultant points are randomly oriented:
	 *          (i) 1 points  required; +x,0,0
	 *          (ii) 2 points: use +x,0,0 and -x,0,0
	 *          (iii) 3 points: equilateral triangle in xy plane
	 *          (iv) 4 points x,x,x, x,-x,-x, -x,x,-x, -x,-x,x
	 *       (1a) 1 ligand(B) of A which itself has a ligand (J)
	 *          (i) 1 points  required; vector along AB vector
	 *          (ii) 2 points: 2 vectors in ABJ plane, staggered and eclipsed wrt J
	 *          (iii) 3 points: 1 staggered wrt J, the others +- gauche wrt J
	 *       (1b) 1 ligand(B) of A which has no other ligands. A random J is
	 *       generated and (1a) applied
	 *       (2) 2 ligands(B, C) of this
	 *          (i) 1 points  required; vector in ABC plane bisecting AB, AC. If ABC is
	 *              linear, no points
	 *          (ii) 2 points: 2 vectors at angle ang, whose resultant is 2i
	 *       (3) 3 ligands(B, C, D) of this
	 *          (i) 1 points  required; if A, B, C, D coplanar, no points.
	 *             else vector is resultant of BA, CA, DA
	 *
	 *       The method identifies the ligands without coordinates, calculates them
	 *       and adds them. It assumes that the total number of ligands determines the
	 *       geometry. This can be overridden by the geometry parameter. Thus if there
	 *       are three ligands and TETRAHEDRAL is given a pyramidal geometry is created
	 *
	 *       Inappropriate cases throw exceptions.
	 *
	 *       fails if atom itself has no coordinates or &gt;4 ligands
	 *       see org.xmlcml.molutils.Molutils for more details
	 *
	 * </pre>
	 *
	 * @param atom
	 * @param geometry
	 *            from: Molutils.DEFAULT, Molutils.ANY, Molutils.LINEAR,
	 *            Molutils.TRIGONAL, Molutils.TETRAHEDRAL
	 * @param length
	 *            A-X length
	 * @param angle
	 *            B-A-X angle (used for some cases)
	 * @return atomSet with atoms which were calculated. If request could not be
	 *         fulfilled (e.g. too many atoms, or strange geometry) returns
	 *         empty atomSet (not null)
	 * @throws RuntimeException
	 */
	public CMLAtomSet calculate3DCoordinatesForLigands(CMLAtom atom,
			int geometry, double length, double angle) {
		Point3 thisPoint;
		// create sets of atoms with and without ligands
		CMLAtomSet noCoordsLigandsAS = new CMLAtomSet();
		if (atom.getX3Attribute() == null) {
			return noCoordsLigandsAS;
		} else {
			thisPoint = atom.getXYZ3();
		}
		CMLAtomSet coordsLigandsAS = new CMLAtomSet();

		// atomSet containing atoms without coordinates
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			if (ligandAtom.getX3Attribute() == null) {
				noCoordsLigandsAS.addAtom(ligandAtom);
			} else {
				coordsLigandsAS.addAtom(ligandAtom);
			}
		}
		int nWithoutCoords = noCoordsLigandsAS.size();
		int nWithCoords = coordsLigandsAS.size();
		if (geometry == Molutils.DEFAULT) {
			geometry = atom.getLigandAtoms().size();
		}

		// too many ligands at present
		if (nWithCoords > 3) {
			CMLAtomSet emptyAS = new CMLAtomSet();
			// FIXME??
			return emptyAS;
			// nothing needs doing
		} else if (nWithoutCoords == 0) {
			return noCoordsLigandsAS;
		}

		List<Point3> newPoints = null;
		List<CMLAtom> coordAtoms = coordsLigandsAS.getAtoms();
		List<CMLAtom> noCoordAtoms = noCoordsLigandsAS.getAtoms();
		if (nWithCoords == 0) {
			newPoints = Molutils.calculate3DCoordinates0(thisPoint,
				geometry, length);
		} else if (nWithCoords == 1) {
			// ligand on A
			CMLAtom bAtom = (CMLAtom) coordAtoms.get(0);
			// does B have a ligand (other than A)
			CMLAtom jAtom = null;
			List<CMLAtom> bLigandList = bAtom.getLigandAtoms();
			for (CMLAtom bLigand : bLigandList) {
				if (!bLigand.equals(this)) {
					jAtom = bLigand;
					break;
				}
			}
			newPoints = Molutils.calculate3DCoordinates1(thisPoint, bAtom
					.getXYZ3(), (jAtom != null) ? jAtom.getXYZ3() : null,
							geometry, length, angle);
		} else if (nWithCoords == 2) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			newPoints = Molutils.calculate3DCoordinates2(thisPoint, bPoint,
					cPoint, geometry, length, angle);
		} else if (nWithCoords == 3) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			Point3 dPoint = ((CMLAtom) coordAtoms.get(2)).getXYZ3();
			newPoints = new ArrayList<Point3>(1);
			newPoints.add(Molutils.calculate3DCoordinates3(thisPoint,
					bPoint, cPoint, dPoint, length));
		}
		int np = Math.min(noCoordsLigandsAS.size(), newPoints.size());
		for (int i = 0; i < np; i++) {
			((CMLAtom) noCoordAtoms.get(i)).setXYZ3(newPoints.get(i));
		}
		return noCoordsLigandsAS;
	}
	// ============ atom matcher ==============
	/**
	 * finds a pair with neighbours that map to each other can be called
	 * iteratively until returns null
	 *
	 * @param atomSet1
	 *            to match
	 * @param atomSet2
	 *            diminished if match found
	 * @param from1to2map
	 *            the mapping from atomSet1 to atomSet2 (i.e. from ids in
	 *            atomSet1, to in atomSet2)
	 * @param atomMatcher
	 * @return pair of matched atoms
	 *
	 */
	// FIXME
	@SuppressWarnings("unused")
	private AtomPair getAtomsWithSameMappedNeighbours00(
			AtomMatcher atomMatcher, CMLAtomSet atomSet1, CMLAtomSet atomSet2,
			CMLMap from1to2map) {
	
		Set<CMLAtom> matchingSet = new HashSet<CMLAtom>();
		AtomPair pair = null;
		for (CMLAtom atom : atomSet1.getAtoms()) {
			if (atomMatcher.skipAtom(atom)) {
				continue;
			}
			matchingSet.add(atom);
		}
	
		for (CMLAtom atom : atomSet2.getAtoms()) {
			String elementType2 = atom.getElementType();
			if (atomMatcher.skipAtom(atom)) {
				continue;
			}
	
			CMLAtomSet ligand2Set = CMLAtomSet.createFromAtoms(atom.getLigandAtoms());
	
			// for each ligandList or atom2 loop through all atom1s to find
			// match
			// which has most ligands?
			// FIXME better - store all atoms with at least one ligand
			List<CMLAtom> matchAtomList = new ArrayList<CMLAtom>();
			int maxLigands = 0;
			CMLAtom mostLigandedAtom1 = null;
			for (int j = 0; j < atomSet1.size(); j++) {
				CMLAtom atom1 = (CMLAtom) atomSet1.getAtom(j);
				if (atomMatcher.skipAtom(atom1)) {
					continue;
				}
				String elementType1 = atom1.getElementType();
				// atoms must match type
				if (!elementType1.equals(elementType2)) {
					continue;
				}
				int ligandCount = 0;
				List<CMLAtom> ligandList = atom1.getLigandAtoms();
				for (CMLAtom ligandAtom1 : ligandList) {
					if (!atomMatcher.skipLigandAtom(ligandAtom1)) {
						String id1 = ligandAtom1.getId();
						String id2 = from1to2map.getRef(id1,
								CMLMap.Direction.FROM);
						// does id2 exist and is it a ligand of atom 2
						if (id2 != null && ligand2Set.getAtomById(id2) != null) {
							ligandCount++;
						}
					}
				}
				if (ligandCount > maxLigands) {
					maxLigands = ligandCount;
					mostLigandedAtom1 = atom1;
				}
				if (ligandCount > 0) {
					matchAtomList.add(atom1);
				}
	
			}
			// ligand atoms in common?
			//
			if (matchAtomList.size() == 1) {
				CMLAtom matchAtom1 = matchAtomList.get(0);
				pair = new AtomPair(matchAtom1, atom);
				atomSet1.removeAtom(matchAtom1);
				atomSet2.removeAtom(atom);
				break;
				// old method
			} else if (false && mostLigandedAtom1 != null) {
				pair = new AtomPair(mostLigandedAtom1, atom);
				atomSet1.removeAtom(mostLigandedAtom1);
				atomSet2.removeAtom(atom);
				break;
			} else {
				pair = null;
			}
		}
		return pair;
	}
	
	public List<CMLAtom> getAtomList(CMLMap map, Direction toFrom) {
		List<String> idList = map.getRefs(toFrom);
		List<CMLAtom> atomList = new ArrayList<CMLAtom>();
		for (String id : idList) {
			CMLAtom atom = molecule.getAtomById(id);
			if (atom == null) {
				// maybe a fromSet
//				for (String ss : idList) {
//					System.out.println(ss);
//				}
//				map.debug();
//				throw new RuntimeException("missing atom: "+id);
			}
			atomList.add(atom);
		}
		return atomList;
	}

	/**
	 * Expand implicit hydrogen atoms.
	 *
	 * This needs looking at
	 *
	 * CMLMolecule.NO_EXPLICIT_HYDROGENS 
	 * CMLMolecule.USE_HYDROGEN_COUNT // no
	 * action
	 *
	 * @param atom
	 * @param control
	 * @throws RuntimeException
	 */
	public void expandImplicitHydrogens(CMLAtom atom,
			CMLMolecule.HydrogenControl control) throws RuntimeException {
		if (control.equals(CMLMolecule.HydrogenControl.USE_HYDROGEN_COUNT)) {
			return;
		}
		if (atom.getHydrogenCountAttribute() == null
				|| atom.getHydrogenCount() == 0) {
			return;
		}
		int hydrogenCount = atom.getHydrogenCount();
		int currentHCount = 0;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (ligand.getElementType().equals(AS.H.value)) {
				currentHCount++;
			}
		}
		// FIXME. This needs rethinking
		if (control.equals(CMLMolecule.HydrogenControl.NO_EXPLICIT_HYDROGENS)
				&& currentHCount != 0) {
			return;
		}
		String id = atom.getId();
		for (int i = 0; i < hydrogenCount - currentHCount; i++) {
			CMLAtom hatom = new CMLAtom(id + "_h" + (i + 1));
			molecule.addAtom(hatom);
			hatom.setElementType(AS.H.value);
			CMLBond bond = new CMLBond(atom, hatom);
			molecule.addBond(bond);
			bond.setOrder("1");
		}
	}

	/**
	 * gets lone electrons on atom. electrons not involved in bonding assumes
	 * accurate hydrogen count only currently really works for first row (C, N,
	 * O, F) calculated as getHydrogenValencyGroup() -
	 * (getSumNonHydrogenBondOrder() + getHydrogenCount()) -
	 * atom.getFormalCharge()
	 *
	 * @param atom
	 * @return number of lone electrons (< 0 means cannot calculate)
	 */
	public int getLoneElectronCount(CMLAtom atom) {
		int loneElectronCount = -1;
		int group = this.getHydrogenValencyGroup(atom);
		if (group == -1) {
			return -1;
		}
		int sumNonHBo = MoleculeTool.getSumNonHydrogenBondOrder(molecule, atom);
		int nHyd = atom.getHydrogenCount();
		int formalCharge = 0;
		if (atom.getFormalChargeAttribute() != null) {
			formalCharge = atom.getFormalCharge();
		}
		loneElectronCount = group - (sumNonHBo + nHyd) - formalCharge;
		return loneElectronCount;
	}
	
	@SuppressWarnings("unused")
	private void getHybridizationFromConnectivty() {
		// FIXME
		List<CMLAtom> atoms = molecule.getAtoms();
		// process atoms first. known terminal atoms or atoms with maximum
		// connectivity
		// gives precise information;
		int valence[] = new int[atoms.size()];
		int i = -1;
		for (CMLAtom atom : molecule.getAtoms()) {
			i++;
			int bondOrder = -1;
			int ligandCount = atom.getLigandAtoms().size();
			String elType = null;
			try {
				elType = atom.getElementType();
			} catch (Exception e) {
				LOG.error("BUG " + e);
			}
			valence[i] = -1;
			if (AS.H.equals(elType) || AS.F.equals(elType) || AS.Cl.equals(elType)
					|| AS.Br.equals(elType) || AS.I.equals(elType)) {
				valence[i] = 1;
			} else if (AS.O.equals(elType) || AS.S.equals(elType)) {
				valence[i] = 2;
			} else if (AS.N.equals(elType)) {
				valence[i] = 3;
			} else if (AS.C.equals(elType)) {
				valence[i] = 4;
			}
			int valLig = valence[i] - ligandCount;
			// FIXME many times!
			if (valLig >= 0) {
				if (valence[i] < 3) {
					if (ligandCount == 1 || valLig == 0) {
						// bondOrder = orderX[valLig];
					}
					// atom.setGeometricHybridization(spX[valLig]);
				} else if (valence[i] == 4) {
					if (ligandCount == 4) {
						// bondOrder = orderX[valLig];
						// atom.setGeometricHybridization(spX[valLig]);
					}
				}
			}
			if (bondOrder == -1) {
				continue;
			}
			// set hybridization and orders for ligand bonds for (
			// LigandIterator it = atom.getLigandIterator();
			// it.hasNext();
			// )
			{
				// CMLAtom ligand = (CMLAtom) it.next();
				// FIXME
				CMLAtom ligand = null;
				CMLBond bond = molecule.getBond(atom, ligand);
				// FIXME
				// order = bond.getOrder();
			}
			if (bondOrder == -1) {

			}
			// bond.setOrder(S_EMPTY+bondOrder);
			//
			// bond.orderKnown = true;
		}

		boolean change = true;
		while (change) {
			change = false;
			// iterate using the known bond orders and filling in any gaps...
			for (int j = 0; j < atoms.size(); j++) {
				if (valence[j] < 0) {
					continue;
				}
				int bondSum = 0;
				int aromSum = 0;
				CMLBond missingBond[] = new CMLBond[atoms.size()];
				int nMissing = 0;
				for (CMLAtom atom1 : atoms) {
					// CMLAtom ligand = (CMLAtom) it1.next();
					CMLAtom ligand = null;
					CMLBond bond = molecule.getBond(atom1, ligand);
					String order = null;
					try {
						order = bond.getOrder();
					} catch (Exception e) {
						LOG.error("BUG " + e);
					}
					if (order.equals(CMLBond.UNKNOWN_ORDER)) {
						missingBond[nMissing++] = bond;
					} else {
						if (order.equals(CMLBond.SINGLE)) {
							bondSum++;
						} else if (order.equals(CMLBond.DOUBLE)) {
							bondSum += 2;
						} else if (order.equals(CMLBond.TRIPLE)) {
							bondSum += 3;
						} else if (order.equals(CMLBond.AROMATIC)) {
							aromSum++;
						}
					}
				}
				change = assignMissingBonds(valence, j, bondSum, aromSum,
						missingBond, nMissing);
			}
		}
	}

	private boolean assignMissingBonds(int[] valence, int i, int bondSum,
			int aromSum, CMLBond[] missingBond, int nMissing) {
		boolean change = false;
		if (nMissing > 0) {
			if (aromSum == 0) {
				int delta = valence[i] - bondSum;
				if (nMissing == delta) {
					for (int j = 0; j < nMissing; j++) {
						try {
							missingBond[j].setOrder(CMLBond.SINGLE);
						} catch (Exception e) {
							LOG.error("BUG " + e);
						}
					}
					change = true;
				} else if (nMissing == 1) {
					String newOrder = null;
					if (delta == 1) {
						newOrder = CMLBond.SINGLE;
					} else if (delta == 2) {
						newOrder = CMLBond.DOUBLE;
					} else if (delta == 3) {
						newOrder = CMLBond.TRIPLE;
					} else if (newOrder != null) {
						try {
							missingBond[0].setOrder(newOrder);
						} catch (Exception e) {
							LOG.error("BUG " + e);
						}
						change = true;
					}
				}
			}
		}
		return change;
	}

	// =============== ATOMSET =========================
	/**
	 * gets bondset for all bonds in current atom set. slow order of bond is
	 * undetermined but probably in atom document order and iteration through
	 * ligands
	 *
	 * @param atomSet
	 * @return the bondSet
	 */
	public CMLBondSet getBondSet(CMLAtomSet atomSet) {
		// List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> atoms = atomSet.getAtoms();
		molecule.getBonds();
		CMLBondSet bondSet = new CMLBondSet();
		for (CMLAtom atom : atoms) {
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			List<CMLBond> ligandBondList = atom.getLigandBonds();
			// loop through ligands and examine each for membership of this set;
			// if so add bond
			int i = 0;
			for (CMLAtom ligand : ligandList) {
				CMLBond ligandBond = ligandBondList.get(i++);
				if (bondSet.contains(ligandBond)) {
					continue;
				}
				if (atomSet.contains(ligand)) {
					bondSet.addBond(ligandBond);
				}
			}
		}
		return bondSet;
	}

	/**
	 * Creates new Molecule one ligand shell larger than the atomSet.
	 *
	 * @param atomSet
	 *            to sprout from (must all be in this molecule).
	 * @return new Molecule (or null if no atoms)
	 */
	public CMLMolecule sprout(CMLAtomSet atomSet) {
		CMLMolecule newMolecule = null;
		if (atomSet != null && atomSet.getAtoms().size() > 0) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			CMLAtomSet newAtomSet = atomSetTool.sprout();
			CMLMolecule molecule = atomSetTool.getMoleculeOrAncestor();
			CMLBondSet newBondSet = MoleculeTool.getOrCreateTool(molecule)
			.getBondSet(newAtomSet);
			newMolecule = MoleculeTool.createMolecule(newAtomSet, newBondSet);
		}
		return newMolecule;
	}

	/**
	 * get bond connecting 2 atoms.
	 *
	 * @param a1
	 *            first atom
	 * @param a2
	 *            second atom
	 * @return bond or null if not found
	 */
	public CMLBond getBond(String id1, String id2) {
		String atomHash = CMLBond.atomHash(id1, id2);
		return getBondFromHash(atomHash);
	}


	private CMLBond getBondFromHash(String atomHash) {
		CMLBond theBond = null;
		if (atomHash != null) {
			for (CMLBond bond : molecule.getBonds()) {
				String bondHash = CMLBond.atomHash(bond);
				if (bondHash.equals(atomHash)) {
					theBond = bond;
					break;
				}
			}
		}
		return theBond;
	}

	/**
	 * create a cluster of connected atoms of given types.
	 *
	 * @param typeList
	 *            list of elements allowed
	 * @return list of clusters (or empty list)
	 */
	public List<CMLMolecule> createClusters(List<ChemicalElement.Type> typeList) {
		List<CMLMolecule> clusterList = new ArrayList<CMLMolecule>();
		if (molecule.getMoleculeCount() > 0) {
			for (CMLMolecule subMolecule : molecule.getMoleculeElements()) {
				MoleculeTool subMoleculeTool = MoleculeTool.getOrCreateTool(subMolecule);
				List<CMLMolecule> subClusterList = subMoleculeTool
				.createClusters(typeList);
				for (CMLMolecule subCluster : subClusterList) {
					clusterList.add(subCluster);
				}
			}
		} else {
			CMLAtomSet unusedAtomSet = this.getAtomSet();
			while (unusedAtomSet.size() > 0) {
				CMLAtomSet clusterSet = new CMLAtomSet();
				expandCluster(clusterSet, unusedAtomSet,
						typeList);
				if (clusterSet.size() > 1) {
					CMLMolecule clusterMolecule = MoleculeTool.createMolecule(clusterSet);
					MoleculeTool.getOrCreateTool(clusterMolecule).calculateBondedAtoms();
					clusterList.add(clusterMolecule);
					molecule.addToLog(Severity.INFO, "NEW CLUSTER SIZE "
							+ clusterSet.size());
				}
			}
		}
		return clusterList;
	}

	private boolean expandCluster(CMLAtomSet clusterSet,
			CMLAtomSet unusedAtomSet, List<Type> typeList) {
		boolean change = false;
		List<CMLAtom> unusedAtomList = unusedAtomSet.getAtoms();
		for (CMLAtom currentAtom : unusedAtomList) {
			if (sproutAtom(currentAtom, clusterSet,
					unusedAtomSet, typeList)) {
				change = true;
				break;
			}
		}
		return change;
	}

	private boolean sproutAtom(CMLAtom currentAtom, CMLAtomSet clusterSet,
			CMLAtomSet unusedAtomSet, List<Type> typeList) {
		boolean change = false;
		unusedAtomSet.removeAtom(currentAtom);
		if (currentAtom.atomIsCompatible(typeList)) {
			change = true;
			if (!clusterSet.contains(currentAtom)) {
				clusterSet.addAtom(currentAtom);
			}
			List<CMLAtom> ligandList = currentAtom.getLigandAtoms();
			for (CMLAtom ligand : ligandList) {
				// atom already used?
				if (!unusedAtomSet.contains(ligand)) {
					continue;
				}
				// atom not of right kind
				if (!ligand.atomIsCompatible(typeList)) {
					continue;
				}
				boolean bonded = CMLBond.areWithinBondingDistance(currentAtom,
						ligand);
				if (bonded) {
					clusterSet.addAtom(ligand);
				}
				sproutAtom(ligand, clusterSet, unusedAtomSet, typeList);
			}
		}
		return change;
	}

	/**
	 * extract ligands from connected molecule components.
	 *
	 * @param typeList
	 *            list of elements allowed
	 * @return list of clusters (or empty list)
	 */
	public List<CMLMolecule> createLigands(List<ChemicalElement.Type> typeList) {
		new ConnectionTableTool(molecule).partitionIntoMolecules();
		List<CMLMolecule> ligandList = new ArrayList<CMLMolecule>();
		for (CMLMolecule splitMol : this.getMoleculeList()) {
			CMLMolecule subMolecule = new CMLMolecule(splitMol);
			//subMolecule.setId("NEW_" + subMolecule.getId());
			boolean deleted = false;
			List<CMLAtom> atomList = subMolecule.getAtoms();
			for (CMLAtom atom : atomList) {
				if (atom.atomIsCompatible(typeList)) {
					deleted = true;
					subMolecule.deleteAtom(atom);
				}
			}
			if (deleted) {
				if (subMolecule.getAtomCount() == 0) {
					continue;
				} else {
					new ConnectionTableTool(subMolecule).partitionIntoMolecules();
					List<CMLMolecule> ligands = MoleculeTool.getOrCreateTool(subMolecule).getMoleculeList();
					for (CMLMolecule ligand : ligands) {
						ligandList.add(ligand);
					}
				}
			}
		}
		return ligandList;
	}
	
	public void addGroupLabels() {
		for (CMLAtom atom : molecule.getAtoms()) {
			AtomTool atomTool = AtomTool.getOrCreateTool(atom);
			atomTool.createGroupLabelAndAtomSet();
		}
	}

	public void addFormula() {
		if (molecule.getFormulaElements().size() == 0) {
			CMLFormula formula = new CMLFormula(molecule);
			molecule.appendChild(formula);
		}
	}

	/**
	 * Creates new Molecule one ligand shell larger than this one. uses
	 * molecule.getAtomSet()
	 *
	 * @see #sprout(CMLAtomSet)
	 * @return new Molecule
	 */
	public CMLMolecule sprout() {
		return sprout(this.getAtomSet());
	}

	// ====================== BOND ============

	/**
<<<<<<< .working
=======
	 * get substituent ligands of one end of bond.
	 *
	 * gets all substituent atoms of atom (but not otherAtom in bond)
	 *
	 * @param bond
	 * @param atom at one end of bond
	 * @return the list of substituent atoms
	 */
	private static List<CMLAtom> getSubstituentLigandList(CMLBond bond, CMLAtom atom) {
		CMLAtom otherAtom = bond.getOtherAtom(atom);
		List<CMLAtom> substituentLigandList = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!ligand.equals(otherAtom)) {
				substituentLigandList.add(ligand);
			}
		}
		return substituentLigandList;
	}

	/**
	 * gets four atoms defining cis/trans isomerism.
	 *
	 * selects 4 atoms lig0-atom0-atom1-lig1, where atom0 and atom1 are the
	 * atoms in the bond and lig0 and lig1 are atoms bonded to each end
	 * respectively Returns null unless atom0 and atom 1 each have 1 or 2
	 * ligands (besides themselves) if atom0 and/or atom1 have 2 ligands the
	 * selection is deterministic but arbitrary - hydrogens are not used if
	 * possible - normally the first Id in lexical order (i.e. no implied
	 * semantics). Exceptions are thrown if ligands are linear with atom0-atom1
	 * or 2 ligands on same atom are on the same side these may be normal
	 * exceptions (e.g. in a C=O bond) so should be caught and analysed rather
	 * than dumping the program
	 *
	 * @param bond
	 * @exception RuntimeException
	 *                2 ligand atoms on same atom on same side too few or too
	 *                many ligands at either end (any) ligand is linear with
	 *                bond
	 * @return the four atoms
	 */
	static CMLAtom[] createAtomRefs4(CMLBond bond) throws RuntimeException {
		CMLAtom[] atom4 = null;
		List<CMLAtom> atomList = bond.getAtoms();
		List<CMLAtom> ligands0 = getSubstituentLigandList(bond, atomList.get(0));
		List<CMLAtom> ligands1 = getSubstituentLigandList(bond, atomList.get(1));
		if (ligands0.size() == 0) {
			// no ligands on atom
		} else if (ligands1.size() == 0) {
			// no ligands on atom
		} else if (ligands0.size() > 2) {
			throw new RuntimeException("Too many ligands on atom: "
					+ atomList.get(0).getId());
		} else if (ligands1.size() > 2) {
			throw new RuntimeException("Too many ligands on atom: "
					+ atomList.get(1).getId());
		} else {
			CMLAtom ligand0 = ligands0.get(0);
			if (AS.H.equals(ligand0.getElementType()) && ligands0.size() > 1
					&& ligands0.get(1) != null) {
				ligand0 = ligands0.get(1);
			} else if (ligands0.size() > 1
					&& ligands0.get(1).getId().compareTo(atomList.get(0).getId()) < 0) {
				ligand0 = ligands0.get(1);
			}
			CMLAtom ligand1 = ligands1.get(0);
			if (AS.H.equals(ligand1.getElementType()) && ligands1.size() > 1
					&& ligands1.get(1) != null) {
				ligand1 = ligands1.get(1);
			} else if (ligands1.size() > 1
					&& ligands1.get(1).getId().compareTo(atomList.get(1).getId()) < 0) {
				ligand1 = ligands1.get(1);
			}
			atom4 = new CMLAtom[4];
			atom4[0] = ligand0;
			atom4[1] = atomList.get(0);
			atom4[2] = atomList.get(1);
			atom4[3] = ligand1;
		}
		return atom4;
	}

	/**
	 * gets four atoms defining atomParity isomerism.
	 * applies only to 4-coordinate atoms l1-X(l2)(l3)-l4
	 * and possibly 3-coordinate atoms l1-X(l2)-l3
	 * when central atom is added to list: l1-l2-l3-X
	 *
	 * @param atom
	 * @return the four atoms or null
	 */
	static CMLAtom[] getAtomRefs4(CMLAtom atom) throws RuntimeException {
		CMLAtom[] atom4 = null;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		if (ligandList.size() < 3) {
		} else {
			atom4 = new CMLAtom[4];
			atom4[0] = ligandList.get(0);
			atom4[1] = ligandList.get(1);
			atom4[2] = ligandList.get(2);
			atom4[3] = (ligandList.size() == 3) ? atom : ligandList.get(3);
		}
		return atom4;
	}

	/**
	 * gets atoms on one side of bond. only applicable to acyclic bonds if bond
	 * is cyclic, whole molecule will be returned! returns atom and all
	 * descendant atoms.
	 *
	 * @param bond
	 * @param atom defining side of bond
	 * @throws RuntimeException atom is not in bond
	 * @return atomSet of downstream atoms
	 */
	public CMLAtomSet getDownstreamAtoms(CMLBond bond, CMLAtom atom) {
		CMLAtomSet atomSet = new CMLAtomSet();
		CMLAtom otherAtom = bond.getOtherAtom(atom);
		if (otherAtom != null) {
			atomSet = getDownstreamAtoms(otherAtom, atom);
		}
		return atomSet;
	}

	// ==================== MOLECULE ====================

	/**
>>>>>>> .merge-right.r915
	 * add suffix to atom IDs.
	 *
	 * Add a distinguishing suffix to all atom IDs this allows multiple copies
	 * of a fragment in a molecule
	 *
	 * @param suffix
	 * @throws RuntimeException
	 */
	public void addSuffixToAtomIDs(String suffix) {
		for (CMLAtom atom : molecule.getAtoms()) {
			atom.renameId(atom.getId() + suffix);
		}
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 *
	 * does not use CDK
	 *
	 * @param control
	 *            specifies whether H are explicit or in hydorgenCount
	 */
	public void adjustHydrogenCountsToValency(HydrogenControl control) {
		if (molecule.isMoleculeContainer()) {
			CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
			for (CMLMolecule mol : molecules) {
				MoleculeTool.getOrCreateTool(mol).adjustHydrogenCountsToValency(control);
			}
		} else {
			List<CMLAtom> atoms = molecule.getAtoms();
			for (CMLAtom atom : atoms) {
				AtomTool atomTool = AtomTool.getOrCreateTool(atom);
				atomTool.adjustHydrogenCountsToValency(control);
			}
		}
	}


	 /** Traverses all non-H atoms and contracts the hydrogens on each.
	  * Can control whether H with stereogenic bonds are contracted or not.
	 *
	 * @param control
	 * @param contractStereoH
	 */
	public void contractExplicitHydrogens(HydrogenControl control, boolean contractStereoH) {
		if (molecule.isMoleculeContainer()) {
			CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
			for (CMLMolecule mol : molecules) {
				MoleculeTool.getOrCreateTool(mol).contractExplicitHydrogens(control, contractStereoH);
			}
		} else {
			List<CMLAtom> atoms = molecule.getAtoms();
			for (CMLAtom atom : atoms) {
				if (!AS.H.equals(atom.getElementType())) {
					if (contractStereoH) {
						AtomTool.getOrCreateTool(atom).contractExplicitHydrogens(control);
					} else {
						boolean contract = true;
						for (CMLBond bond : atom.getLigandBonds()) {
							List<Node> bondStereoNodes = CMLUtil.getQueryNodes(bond, ".//cml:"+CMLBondStereo.TAG, CML_XPATH);
							for (Node bondStereoNode : bondStereoNodes) {
								String stereo = bondStereoNode.getValue();
								if (CMLBond.WEDGE.equals(stereo) || CMLBond.HATCH.equals(stereo)) {
									contract = false;
								}
							}
						}
						if ("R".equals(atom.getElementType()) || "Xx".equals(atom.getElementType()) ||
								atom.getChemicalElement().isChemicalElementType(Type.METAL)) {
							contract = false;
						}
						if (contract) {
							AtomTool.getOrCreateTool(atom).contractExplicitHydrogens(control);
						}
					}
				}
			}
		}
	}

	/**
	 * Traverses all non-H atoms and calls CMLAtom.expandImplicitHydrogens on each.
	 *
	 * @param control as in CMLAtom
	 * @see CMLAtom
	 */
	public void expandImplicitHydrogens(HydrogenControl control) {
		for (CMLAtom atom : molecule.getAtoms()) {
			if (!AS.H.equals(atom.getElementType())) {
				AtomTool atomTool = AtomTool.getOrCreateTool(atom);
				atomTool.expandImplicitHydrogens(control);
			}
		}
	}

	/**
	 * removes atoms from this with identical coordinates to those in mol. uses
	 * deleteAtom (which may delete bonds if present)
	 *
	 * @param mol
	 *            should be unaltered
	 * @param type
	 *            of coordinate to use
	 */
	public void removeOverlapping3DAtoms(CMLMolecule mol, CoordinateType type) {
		CMLAtomSet atomSet = AtomSetTool.getOrCreateTool(this.getAtomSet())
		.getOverlapping3DAtoms(MoleculeTool.getOrCreateTool(mol).getAtomSet(), type);
		for (CMLAtom atom : atomSet.getAtoms()) {
			molecule.deleteAtom(atom);
		}
	}

	/**
	 * 
	 */
	public void create2DCoordinatesFrom3D(double bondLengthScale) {
		List <CMLAtom> atomList = molecule.getAtoms();
		for (CMLAtom atom : atomList) {
			double x3 = atom.getX3();
			atom.setX2(x3*bondLengthScale);
			double y3 = atom.getY3();
			atom.setY2(y3*bondLengthScale);
		}
		Real2 centroid2 = this.getAtomSet().getCentroid2D();
		molecule.translate2D(centroid2.multiplyBy(-1.0));
	}

	/**
	 * gets atomset corresponding to bondset.
	 *
	 * @param bondSet
	 * @return the atomset
	 */
	public CMLAtomSet getAtomSet(CMLBondSet bondSet) {
		CMLAtomSet atomSet = new CMLAtomSet();

		List<CMLBond> bonds = bondSet.getBonds();
		for (CMLBond bond : bonds) {
			CMLAtom atom0 = bond.getAtom(0);
			CMLAtom atom1 = bond.getAtom(1);
			atomSet.addAtom(atom0);
			atomSet.addAtom(atom1);
		}

		return atomSet;
	}

	/**
	 * calculate bondorders from coordinates if present.
	 *
	 * uses Pauling bond order/length formula. use with extreme care! many bonds
	 * will have fractional orders and these will be difficult to manage
	 */
	public void calculateBondOrdersFromXYZ3() {
		for (CMLBond bond : molecule.getBonds()) {
			String order = null;
			try {
				order = bond.getOrder();
			} catch (Exception e) {
				LOG.error("BUG " + e);
			}
			// order not available?
			if (order == null || order.equals(CMLBond.UNKNOWN_ORDER)) {
				CMLAtom at0 = bond.getAtom(0);
				if (at0 == null) {
					throw new RuntimeException("NULL atom");
				}
				String elType = at0.getElementType();
				if (elType == null) {
					throw new RuntimeException("missing elementType");
				}
				ChemicalElement el0 = ChemicalElement
				.getChemicalElement(elType);
				if (el0 == null) {
					continue;
				}
				double r0 = el0.getCovalentRadius();
				CMLAtom at1 = bond.getAtom(1);
				ChemicalElement el1 = ChemicalElement.getChemicalElement(at1
						.getElementType());
				if (el1 == null) {
					continue;
				}
				double r1 = el1.getCovalentRadius();
				double dlen = at0.getDistanceTo(at1);
				if (dlen < 0.0) {
					continue;
				}
				double paulingBO = Math.exp(2.303 * (-dlen + r0 + r1) / 0.7);
				try {
					if (paulingBO < 1.3) {
						bond.setOrder(CMLBond.SINGLE);
					} else if (paulingBO < 1.75) {
						bond.setOrder(CMLBond.AROMATIC);
					} else if (paulingBO < 2.5) {
						bond.setOrder(CMLBond.DOUBLE);
					} else if (paulingBO < 5) {
						bond.setOrder(CMLBond.TRIPLE);
					}
				} catch (Exception e) {
					Util.BUG(e);
				}
			}
		}
	}

	/**
	 * calculates which atoms are bonded by covalent radius criterion.
	 *
	 */
	public void calculateBondedAtoms() {
		for (CMLMolecule mol : molecule.getDescendantsOrMolecule()) {
			List<CMLAtom> atoms = mol.getAtoms();
			// need to remove old bonds first
			for (CMLBond bond : mol.getBonds()) {
				bond.detach();
			}
			CMLBondArray ba = mol.getBondArray();
			if (ba != null) {
				ba.indexBonds();
			}
			calculateBondedAtoms(atoms, mol);
		}
	}
	
	/**
	 * @param atoms
	 */
	public void calculateBondedAtoms(List<CMLAtom> atoms) {
		CMLMolecule mol = null;
		for (CMLAtom atom : atoms) {
			CMLMolecule m = atom.getMolecule();
			if (mol != null) {
				if (mol != atom.getMolecule()) {
					throw new RuntimeException("All CMLAtoms must belong to the same CMLMolecule");
				}
			}
			mol = m;
		}
		calculateBondedAtoms(atoms, mol);
	}

	private void calculateBondedAtoms(List<CMLAtom> atoms, CMLMolecule molecule) {
		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom atomi = (CMLAtom) atoms.get(i);
			for (int j = i + 1; j < atoms.size(); ++j) {
				CMLAtom atomj = (CMLAtom) atoms.get(j);
				if (CMLBond.areWithinBondingDistance(atomi, atomj)) {
					molecule.addBond(new CMLBond(atomi, atomj));
				}
			}
		}
	}

	/**
	 * calculate all bonds from this to molecule and join. will transfer all
	 * atoms from molecule to this except overlapping ones, thereby corrupting
	 * molecule. Overlapping atoms are discarded
	 *
	 * @param molecule2
	 *            to join (will be corrupted)
	 * @return true if 1 or more bonds were made
	 */
	boolean calculateBondsToAndJoin(CMLMolecule molecule2) {
		List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> atoms2 = molecule2.getAtoms();

		boolean madeBond = false;
		int i = 0;
		int idMax = this.getMaximumId("a");
		for (CMLAtom atomi : atoms) {
			int j = 0;
			for (CMLAtom atomj : atoms2) {
				if (CMLBond
						.areWithinBondingDistance(atomi, atomj)) {
					madeBond = true;
					if (atomi.getDistanceTo(atomj) < 0.2) {
						// remove overlapping atoms
						atomj.detach();
						//System.out.println("OVERLAP........................... "
						//		+ atomj.getId());
					} else {
						// transfer atom and add new bond
						atomj.detach();
						String id = "a" + (++idMax);
						atomj.setId(id);
						molecule.addAtom(atomj);
						molecule.addBond(new CMLBond(atomi, atomj));
					}
				}
				j++;
			}
			i++;
		}
		if (madeBond) {
			// transfer rest of atoms from molecule 2
			idMax = this.getMaximumId("a");
			atoms2 = molecule2.getAtoms();
			for (CMLAtom atom2 : atoms2) {
				atom2.detach();
				String id = "a" + (++idMax);
				atom2.setId(id);
				molecule.addAtom(atom2);
			}
			calculateBondedAtoms(atoms2, molecule);
		}
		return madeBond;
	}

	/**
	 * iterates through atom ids and finds the largest numerically. assumes they
	 * are of form: "a123" and returns largest value of integer
	 *
	 * @param prefix
	 *            (e.g. "a")
	 * @return largest integer following prefix
	 */
	private int getMaximumId(String prefix) {
		int max = -1;
		int l = prefix.length();
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			String id = atom.getId();
			try {
				int ii = Integer.parseInt(id.substring(l));
				max = (max < ii) ? ii : max;
			} catch (NumberFormatException e) {
				;//
			}
		}
		return max;
	}

	/**
	 * Generates and adds unique bond ids.
	 *
	 * Uses CMLBond.generateId.
	 */
	public void generateBondIds() {
		for (CMLBond bond : molecule.getBonds()) {
			bond.generateAndSetId();
		}
	}

	/**
	 * get the average atom-atom bond distance.
	 *
	 * This is primarily for scaling purposes and should not be taken too
	 * seriously.
	 *
	 * @param type
	 * @param omitHydrogens coordinates
	 * @return average distance in origianl units. If no bonds returns negative.
	 */
	public double getAverageBondLength(CoordinateType type, boolean omitHydrogens) {
		double bondSum = 0.0;
		int nBonds = 0;
		for (CMLBond bond : molecule.getBonds()) {
			if (omitHydrogens && (
					"H".equals(bond.getAtom(0).getElementType()) ||
					"H".equals(bond.getAtom(1).getElementType()))
					) {
				continue;
			}
			try {
				double length = bond.calculateBondLength(type);
				if (!Double.isNaN(length)) {
					LOG.trace("len "+length);
					bondSum += length;
					nBonds++;
				}
			} catch (RuntimeException e) {
				// no coordinates
			}
		}
		return (nBonds == 0 || Double.isNaN(bondSum) || Real.isZero(bondSum, Real.EPS)) 
		    ? Double.NaN : bondSum / ((double) nBonds);
	}

	/**
	 * get the average atom-atom bond distance.
	 *
	 * This is primarily for scaling purposes and should not be taken too
	 * seriously.
	 *
	 * @param type
	 * @return average distance in origianl units. If no bonds returns negative.
	 */
	public double getAverageBondLength(CoordinateType type) {
		return getAverageBondLength(type, false);
	}

	/**
	 * get matched bond using atom mapping. does not work with sets
	 *
	 * if bond atomRefs2="a1 a2" and link to="a1" from="b1" // atomId and link
	 * to="a2" from="b2" // atomId and toFrom = Direction.FROM then will return
	 * bond atomRefs2="b1 b2" or atomRefs2="b2 b1" in molecule1
	 *
	 * @param bond0 bond to search with. the values in must occur in a single
	 *            toFrom attribute
	 * @param map with links
	 * @param toFrom specifies attribute for search atoms in atomRefs2
	 * @return mapped bond or null
	 */
	public CMLBond getMappedBondViaAtoms(CMLMap map, CMLBond bond0,
			Direction toFrom) {
		CMLBond targetBond = null;
		CMLAtom atom0 = bond0.getAtom(0);
		CMLAtom atom1 = bond0.getAtom(1);
		if (atom0 != null && atom1 != null) {
			String targetRef0 = map.getRef(atom0.getId(), toFrom);
			String targetRef1 = map.getRef(atom1.getId(), toFrom);
			targetBond = molecule.getBond(molecule.getAtomById(targetRef0),
					molecule.getAtomById(targetRef1));
		}
		return targetBond;
	}

	/**
	 * make sub molecule.
	 *
	 * @param molecule
	 * @param atomIds
	 * @return molecule
	 *
	 */
	public static CMLMolecule createMolecule(CMLMolecule molecule,
			String[] atomIds) {
		CMLMolecule newMolecule = new CMLMolecule();
		for (String atomId : atomIds) {
			CMLAtom atom = new CMLAtom(atomId);
			newMolecule.addAtom(atom);
		}
		return newMolecule;
	}
	
	public CMLAtomSet createAtomSet(int[] serials) {
		CMLAtomSet atomSet = new CMLAtomSet();
		for (int serial : serials) {
			CMLAtom atom = molecule.getAtom(serial);
			if (atom != null && !atomSet.contains(atom)) {
				atomSet.addAtom(atom);
			}
		}
		return atomSet;
	}


	/**
	 * creates bonds and partitions molecules and returns contacts. currently
	 * looks only for homo-molecule contacts.
	 *
	 * @param dist2Range
	 * @param crystalTool
	 * @return sorted contact list
	 */
	public List<Contact> getSymmetryContacts(RealRange dist2Range,
			CrystalTool crystalTool) {
		this.createCartesiansFromFractionals(crystalTool.getCrystal());
		this.calculateBondedAtoms();
		List<Contact> contactList = crystalTool.getSymmetryContactsToMolecule(dist2Range);
		Collections.sort(contactList);
		return contactList;
	}

	/**
	 * convenience method to get molecule or its child molecules as a list.
	 *
	 * @return list of either just the molecule (if no children) or list of the
	 *         children
	 */
	public List<CMLMolecule> getMoleculeList() {
		List<CMLMolecule> moleculeList = molecule.getDescendantsOrMolecule();
		return moleculeList;
	}

	/**
	 * convenience method to calculate and add Cartesian coordinates. requires
	 * that molecule has descendant <crystal>
	 *
	 * @throws RuntimeException
	 *             if no crystal
	 */
	public void createCartesiansFromFractionals() throws RuntimeException {
		CMLCrystal crystal = CMLCrystal.getContainedCrystal(molecule);
		this.createCartesiansFromFractionals(crystal);
	}

	/**
	 *  get linkers
	 *
	 *  @return linkerMolecules
	 */
	public List<CMLMolecule> getChainMolecules() {
		List<CMLMolecule> linkers = new ArrayList<CMLMolecule>();
		List<CMLMolecule> subMolList = molecule.getDescendantsOrMolecule();
		for (CMLMolecule subMol : subMolList) {
			ConnectionTableTool ct = new ConnectionTableTool(subMol);
			List<CMLBond> acyclicBonds = ct.getAcyclicBonds();
			List<CMLBond> cyclicBonds = ct.getCyclicBonds();
			List<CMLAtom> acyclicBondAtoms = new ArrayList<CMLAtom>();
			List<CMLAtom> cyclicBondAtoms = new ArrayList<CMLAtom>();		
			for (CMLBond acyclicBond : acyclicBonds) {
				for (CMLAtom atom : acyclicBond.getAtoms()) {
					acyclicBondAtoms.add(atom);
				}
			}
			for (CMLBond cyclicBond : cyclicBonds) {
				for (CMLAtom atom : cyclicBond.getAtoms()) {
					cyclicBondAtoms.add(atom);
				}
			}			
			// if bond count equal to number of acyclic bonds then there
			// aren't any cyclic bonds in the molecule, hence no linkers
			if (subMol.getBondCount() != acyclicBonds.size()) {
				List<CMLAtom> acyclicAtoms = new ArrayList<CMLAtom>();
				for (CMLAtom atom : subMol.getAtoms()) {
					if (acyclicBondAtoms.contains(atom) &&
							!cyclicBondAtoms.contains(atom)) {
						acyclicAtoms.add(atom);
					}
				}

				CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(acyclicAtoms);
				CMLMolecule newMol = MoleculeTool.createMolecule(atomSet);
				MoleculeTool.getOrCreateTool(newMol).calculateBondedAtoms();
				new ConnectionTableTool(newMol).partitionIntoMolecules();
				List<CMLMolecule> linkerList = newMol.getDescendantsOrMolecule();
				for (CMLMolecule mol : linkerList) {
					if (mol.getAtomCount() > 1) {
						linkers.add(mol);
					}
				}
			}
		}
		return linkers;
	}

	/**
	 * get ring nuclei. calls connectionTableTool.getRingNucleiMolecules
	 *
	 * @return ringNucleiMolecules
	 */
	public List<CMLMolecule> getRingNucleiMolecules() {
		ConnectionTableTool connectionTableTool =
			// not sure whether we have to clone the molecule
			// new ConnectionTableTool(new CMLMolecule(subMolecule));
			new ConnectionTableTool(molecule);
		return connectionTableTool.getRingNucleiMolecules();
	}


	/**
	 * join one molecule to another. 
	 * manages the XML but not yet the geometry
	 * empties the added molecule of elements and copies them
	 * to this.molecule and then detaches the addedMolecule
	 * @param addedMolecule to be joined
	 * @param takeAtomWithLowestId DUMMY AT PRESENT
	 */
	public void addMoleculeTo(CMLMolecule addedMolecule, boolean takeAtomWithLowestId) {
		addMoleculeTo(addedMolecule);
	}
	
	/**
	 * join one molecule to another. 
	 * manages the XML but not yet the geometry
	 * empties the added molecule of elements and copies them
	 * to this.molecule and then detaches the addedMolecule
	 * @param addedMolecule to be joined
	 */
	public void addMoleculeTo(CMLMolecule addedMolecule) {

		MoleculeTool addedMoleculeTool = MoleculeTool.getOrCreateTool(addedMolecule);
		addedMoleculeTool.renumberToUniqueIds(this.getAtomIdList());
		molecule.getOrCreateAtomArray();
		// bonds must be done first as atom.detach() destroys bonds
		List<CMLBond> bondList = addedMolecule.getBonds();
		for (CMLBond bond : bondList) {
			bond.detach();
			molecule.addBond(bond);
		}
		List<CMLAtom> atomList = addedMolecule.getAtoms();
		for (CMLAtom atom : atomList) {
			atom.detach();
			molecule.addAtom(atom);
		}
		// copy any remaining nodes except atomArray and bondArray
		Nodes nodes = addedMolecule.query(".//*", CML_XPATH);
		int nnodes = nodes.size();
		for (int i = nnodes - 1; i >= 0; i--) {
			Node node = nodes.get(i);
			node.detach();
			if (node instanceof CMLAtomArray) {
				// don't copy
			} else if (node instanceof CMLBondArray) {
				// don't copy
			} else {
				molecule.appendChild(node);
			}
		}
		addedMolecule.detach();
	}

	/**
	 * @return list of atomIds in order
	 */
	public List<String> getAtomIdList() {
		List<String> idList = new ArrayList<String>();
		for (CMLAtom atom : molecule.getAtoms()) {
			idList.add(atom.getId());
		}
		return idList;
	}

	/**
	 * renumbers atoms in molecule so they do not clash with other Ids
	 * iterates through "a1", "a2" finding the free spaces.
	 * hydrogens will not be prettily numbered
	 * @param avoidList ids already alocated in other molecules
	 */
	public void renumberToUniqueIds(List<String> avoidList) {
		Set<String> stringSet = checkUnique(avoidList);
		int totalSize = molecule.getAtomCount() + avoidList.size();
		List<String> fromIdList = new ArrayList<String>();
		List<String> toIdList = new ArrayList<String>();
		// TODO hydrogens will not be pretty - 
		int ii = 1;
		for (CMLAtom atom : molecule.getAtoms()) {
			String id = atom.getId();
			if (stringSet.contains(id)) {
				fromIdList.add(id);
				String newId = null;
				while (ii < totalSize) {
					newId = "a"+ii;
					if (!stringSet.contains(newId) && molecule.getAtomById(newId) == null) {
						// new unique
						break;
					}
					ii++;
				}
				toIdList.add(newId);
				LOG.debug(id+" => "+newId);
			}
		}
		renumberAtomIds(fromIdList, toIdList);
	}

	/** adjust the cartesians to fit declared torsions.
	 */
	public void adjustTorsions() {
		List<CMLTorsion> torsions = this.getTorsionElements();
		for (CMLTorsion torsion : torsions) {
			TorsionTool.getOrCreateTool(torsion).adjustCoordinates(molecule);
		}
	}

	/** adjust the cartesians to fit declared angles.
	 */
	public void adjustAngles() {
		List<CMLAngle> angles = this.getAngleElements();
		for (CMLAngle angle : angles) {
			AngleTool.getOrCreateTool(angle).adjustCoordinates(molecule);
		}
	}

	/** adjust the cartesians to fit declared lengths.
	 */
	public void adjustLengths() {
		List<CMLLength> lengths = this.getLengthElements();
		for (CMLLength length : lengths) {
			LengthTool lengthTool = LengthTool.getOrCreateTool(length);
			lengthTool.adjustCoordinates(molecule);
		}
	}

	/** iterates through torsions in molecule.
	 * if they contain atom0 and atom1, adjusts them to value of torsion
	 * @param atom0
	 * @param atom1
	 */
	public void adjustTorsions(CMLAtom atom0, CMLAtom atom1) {
		if (molecule.hasCoordinates(CoordinateType.CARTESIAN)) {
			// set torsions
			CMLAtomSet moleculeAtomSet = this.getAtomSet();
			Nodes torsions = molecule.query(".//"+CMLTorsion.NS, CML_XPATH);
			int nTors = torsions.size();
			for (int i = 0; i < nTors; i++) {
				CMLTorsion torsion = (CMLTorsion) torsions.get(i);
				TorsionTool torsionTool = TorsionTool.getOrCreateTool(torsion);
				String[] atomRefs4 = torsion.getAtomRefs4();
				if (atomRefs4 != null
						&& (atom0.getId().equals(atomRefs4[1]) && atom1.getId().equals(atomRefs4[2]) ||
								atom0.getId().equals(atomRefs4[2]) && atom1.getId().equals(atomRefs4[1]))
				) {
					double d = Double.NaN;
					try {
						d = torsion.getXMLContent();
					} catch (RuntimeException e) {
						// no value given
						continue;
						// empty torsion;
					}
					CMLAtomSet moveableAtomSet =
						AtomTool.getOrCreateTool(atom1).getDownstreamAtoms(atom0);
					torsionTool.adjustCoordinates(new Angle(d, Angle.Units.DEGREES),
							moleculeAtomSet, moveableAtomSet);
					// this is to avoid the torsion being reused
					torsion.removeAttribute("atomRefs4");
				}
			}
		}
	}
	
	public void adjustHydrogenLengths(double factor) {
		LOG.debug("hlen factor "+factor);
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if ("H".equals(atom.getElementType())) {
				Real2 xy = atom.getXY2();
				if (xy != null) {
					List<CMLAtom> ligands = atom.getLigandAtoms();
					if (ligands.size() == 1) {
						Real2 xyLig = ligands.get(0).getXY2();
						Real2 vector = xy.subtract(xyLig);
						vector = vector.multiplyBy(factor);
						Real2 xy1 = xyLig.plus(vector);
						atom.setXY2(xy1);
					}
				}
			}
		}
	}

	/** flatten molecules.
	 *
	 * @param parent
	 */
	public void flattenMoleculeDescendants(ParentNode parent) {
		Elements moleculeLists = molecule.getChildCMLElements(CMLMoleculeList.TAG);
		List<CMLMoleculeList> moleculeListList = new ArrayList<CMLMoleculeList>();
		for (int i = 0; i < moleculeLists.size(); i++) {
			moleculeListList.add((CMLMoleculeList) moleculeLists.get(i));
		}
		for (CMLMoleculeList moleculeList : moleculeListList) {
			flattenMoleculeListDescendants(moleculeList, molecule);
			if (parent != null) {
				moleculeList.detach();
			} else {
				CMLUtil.transferChildren(moleculeList, molecule);
				moleculeList.detach();
			}
		}
	}

	private void flattenMoleculeListDescendants(
			CMLMoleculeList moleculeList, CMLMolecule parentMolecule) {
		ParentNode grandParent = parentMolecule.getParent();
		int idx = grandParent.indexOf(parentMolecule);
		//		 moleculeList.debug("MOLL");
		Elements childs = moleculeList.getChildElements();
		for (int i = 0; i < childs.size(); i++) {
			Node child = childs.get(i);
			if (child instanceof Text) {
				child.detach();
			} else {
				if (grandParent instanceof CMLElement) {
					child.detach();
					grandParent.insertChild(child, ++idx);
				}
				if (child instanceof CMLMolecule) {
					MoleculeTool childTool = MoleculeTool.getOrCreateTool((CMLMolecule) child);
					childTool.flattenMoleculeDescendants(moleculeList);
				}
			}
		}
	}

	/** copies attributes on bonds and atoms to another molecule.
	 * for each atom/bond in this.molecule finds Id and hence corresponding 
	 * atom/bond in 'to'. Copies all attributes from that atom to to.atom/@*
	 * If corresponding atom does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception RuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	public void copyAtomAndBondAttributesById(CMLMolecule to, boolean permitOverwrite) {
		copyAtomAttributesById(to, permitOverwrite);
		copyBondAttributesById(to, permitOverwrite);
	}
	
	/** copies attributes on atoms to another molecule.
	 * for each atom in this.molecule finds Id and hence corresponding 
	 * atom in 'to'. Copies all attributes from that atom to to.atom/@*
	 * If corresponding atom does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception RuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	
	public void copyAtomAttributesById(CMLMolecule to, boolean permitOverwrite) {
		List<CMLAtom> fromAtoms = molecule.getAtoms();
		for (CMLAtom fromAtom : fromAtoms) {
			String fromId = fromAtom.getId();
			if (fromId != null) {
				CMLAtom toAtom = to.getAtomById(fromId);
				if (toAtom == null) {
					throw new RuntimeException("Cannot find target atom: "+fromId);
				}
				copyAttributes(fromAtom, toAtom, permitOverwrite);
			}
		}
	}
	
	
	/** copies attributes on bonds to another molecule.
	 * for each bond in this.molecule finds Id and hence corresponding 
	 * bond in 'to'. Copies all attributes from that bond to to.bond/@*
	 * If corresponding bond does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception RuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	
	public void copyBondAttributesById(CMLMolecule to, boolean permitOverwrite) {
		List<CMLBond> fromBonds = molecule.getBonds();
		for (CMLBond fromBond : fromBonds) {
			String fromId = fromBond.getId();
			if (fromId != null) {
				CMLBond toBond = to.getBondById(fromId);
				if (toBond == null) {
					throw new RuntimeException("Cannot find target bond: "+fromId);
				}
				copyAttributes(fromBond, toBond, permitOverwrite);
			}
		}
	}

	private void copyAttributes(Element from, Element to, boolean permitOverwrite) {
		List<Node> nodes = CMLUtil.getQueryNodes(from, "./@*");
		for (Node node : nodes) {
			Attribute attribute = (Attribute) node;
			String name = attribute.getLocalName();
			if ("id".equals(name)) {
				continue;
			}
			if (!permitOverwrite && to.getAttribute(name) != null) {
				throw new RuntimeException("cannot overwrite attribute: "+name);
			}
			Attribute newAttribute = new Attribute(name, attribute.getValue());
			to.addAttribute(newAttribute);
		}
	}
	
	/**
	 * @param type
	 * @param contactDist
	 * @return pairs of atoms which bump
	 */
	public List<AtomPair> getBumps(CoordinateType type, double contactDist) {
		List<AtomPair> bumpList = new ArrayList<AtomPair>();
		
		List<CMLAtom> atomList = molecule.getAtoms();
 		CMLAtomSet allAtomSet = new CMLAtomSet(molecule);
 		int natoms = atomList.size();
 		for (int i = 0; i < natoms; i++) {
 			CMLAtom atomi = atomList.get(i);
 			if (AS.H.equals(atomi.getElementType())) {
 				continue;
 			}
 			// get all atoms within 3 bonds
 			CMLAtomSet atomSet13 = AtomTool.getOrCreateTool(atomi).getCoordinationSphereSet(3);
 			CMLAtomSet nonBonded = allAtomSet.complement(atomSet13);
 			List<CMLAtom> nonBondedAtomList = nonBonded.getAtoms();
 	 		for (CMLAtom atomj : nonBondedAtomList) {
 	 			if (AS.H.equals(atomj.getElementType())) {
 	 				continue;
 	 			}
 	 			if (atomi.getId().compareTo(atomj.getId()) <= 0) {
 	 				continue;
 	 			}
 	 			boolean bump = false;
 	 			double dist = Double.NaN;
 	 			if (type == CoordinateType.CARTESIAN) {
 	 				bump = atomi.isWithinRadiusSum(atomj, ChemicalElement.RadiusType.VDW);
 	 				dist = atomi.getDistanceTo(atomj);
 	 			} else if (type == CoordinateType.TWOD) {
 	 				dist = atomi.getDistance2(atomj);
 	 				if (dist < contactDist) {
 	 					bump = true;
 	 				}
 	 			}
 	 			if (bump) {
 	 				AtomPair atomPair = new AtomPair(atomi, atomj);
 	 				atomPair.setDistance(dist, type);
 	 				bumpList.add(atomPair);
 	 			}
 	 		}
 		}
 		return bumpList;
	}

	/**
	 * 
	 * @param molecule
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLMolecule molecule) {
		return (AbstractSVGTool) MoleculeTool.getOrCreateTool(molecule);
	}

    /** returns a "g" element
     * will require to be added to an svg element
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	moleculeDisplay = (drawable instanceof MoleculeDisplayList) ? 
    			((MoleculeDisplayList)drawable).getMoleculeDisplay() : moleculeDisplay;
		enableMoleculeDisplay();
    	BondDisplay defaultBondDisplay = moleculeDisplay.getDefaultBondDisplay();
    	AtomDisplay defaultAtomDisplay = moleculeDisplay.getDefaultAtomDisplay();
    	double avlength = MoleculeTool.getOrCreateTool(molecule).getAverageBondLength(CoordinateType.TWOD);
    	enableMoleculeDisplay();
    	Transform2 transform2 = new Transform2(
			new double[] {
				1.,  0., 0.0,
				0., -1., 0.0,
				0.,  0., 1.}
			);
    
    	List<CMLAtom> atoms = molecule.getAtoms();
    	if (atoms.size() == 0) {
    		System.out.println("No atoms to display");
    	} else if (atoms.size() == 1) {
    	} else if (applyScale) {
    		transform2 = scaleToBoundingBoxesAndScreenLimits(transform2);
    	}
    	
    	SVGElement g = null;
    	if(drawable instanceof MoleculeDisplayList) {
    		g = ((MoleculeDisplayList) drawable).getSvg();
    	}
		if (g == null) {
	    	g = createSVGElement(drawable, transform2);
	    	g.setProperties(moleculeDisplay);
    	}
    	defaultBondDisplay.setScale(avlength);
    	
    	defaultAtomDisplay.setScale(avlength);
    	if (molecule.getAtomCount() == 1) {
    		defaultAtomDisplay.setDisplayCarbons(true);
    	} 
    	List<CMLBond> bonds = molecule.getBonds();
    	setAtomBondAndGroupVisibility(atoms, bonds);
    	
    	displayBonds(drawable, g, defaultBondDisplay, defaultAtomDisplay);
    	displayAtoms(drawable, g, defaultAtomDisplay);
    	
    	if (moleculeDisplay.isDisplayFormula()) {
    		LOG.debug("FORMULA");
    		displayFormula(drawable, g);
    	}
    	if (moleculeDisplay.isDisplayLabels()) {
    		displayMoleculeLabels(drawable, g);
    	}
    	if (moleculeDisplay.isDisplayNames()) {
    		displayMoleculeNames(drawable, g);
    	}
    	if (moleculeDisplay.isDrawBoundingBox()) {
    		drawBoundingBox(drawable, g);
    	}
    	
    	if (drawable != null) {
    		try {
    			drawable.output(g);
    		} catch (IOException e) {
    			throw new RuntimeException(e);
    		}
    	}
    	return g;
    }
    
    private void drawBoundingBox(CMLDrawable drawable, SVGElement g) {
    	calculateBoundingBox2D();
    	double bondLength = moleculeDisplay.getBondLength();
    	Real2 xy0 = new Real2(
    			userBoundingBox.getXRange().getMin() - bondLength, 
    			userBoundingBox.getYRange().getMin() - bondLength
    			);
    	Real2 xy1 = new Real2(
    			userBoundingBox.getXRange().getMax() + bondLength, 
    			userBoundingBox.getYRange().getMax() + bondLength
    			);
    			
    	SVGRect rect = new SVGRect(xy0, xy1);
    	rect.setStroke("red");
    	rect.setStrokeWidth(2.0);
    	g.appendChild(rect);
    }
    
    private void setAtomBondAndGroupVisibility(
    		List<CMLAtom> atoms, List<CMLBond> bonds) {
    	if (/*true || */
			Level.DEBUG.equals(LOG.getLevel())) {
    		molecule.debug("GROUP VISIBILITY ON");
    	}
    	// add atomDisplay to each atomTool
    	for (CMLAtom atom : atoms) {
    		AtomTool atomTool = AtomTool.getOrCreateTool(atom);
    		atomTool.ensureAtomDisplay();
    		atomTool.getAtomDisplay().setMoleculeDisplay(moleculeDisplay);
    		// group visibility (NYI)
    		if (atomTool.isGroupRoot()) {
    			atomTool.setDisplay(false);
    		}
    	}
    	// add bondDisplay to each bondTool
    	for (CMLBond bond : bonds) {
    		BondTool bondTool = BondTool.getOrCreateTool(bond);
    		bondTool.ensureBondDisplay();
    		bondTool.getBondDisplay().setMoleculeDisplay(moleculeDisplay);
    	}
    }

	private Transform2 scaleToBoundingBoxesAndScreenLimits(Transform2 transform2) {
		try {
			calculateBoundingBox2D();
//			Real2Range moleculeBoundingBox = AtomSetTool.getOrCreateTool(
//					new CMLAtomSet(molecule)).getExtent2();
			Real2Interval screenBoundingBox = moleculeDisplay.getScreenExtent();
//			Real2Interval moleculeInterval = new Real2Interval(moleculeBoundingBox);
			Real2Interval moleculeInterval = new Real2Interval(userBoundingBox);
			double scale = moleculeInterval.scaleTo(screenBoundingBox);
			double[] offsets = moleculeInterval.offsetsTo(screenBoundingBox, scale);
			transform2 = new Transform2 (
				new double[] {
					scale, 0., offsets[0],
					0.,-scale, offsets[1],
					0.,    0.,   1.}
				);
		} catch (NullPointerException npe) {
			// happens with small number of atoms
		}
		return transform2;
	}

	public Real2Range calculateBoundingBox2D() {
		userBoundingBox = null;
		if (molecule != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(new CMLAtomSet(molecule));
			userBoundingBox = (atomSetTool == null) ? null : atomSetTool.getExtent2();
		}
		return userBoundingBox;
	}

	public Real3Range calculateBoundingBox3D() {
		Real3Range userBoundingBox3 = null;
		if (molecule != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(new CMLAtomSet(molecule));
			userBoundingBox3 = (atomSetTool == null) ? null : atomSetTool.getExtent3();
		}
		return userBoundingBox3;
	}

	private void displayAtoms(CMLDrawable drawable, SVGElement g, AtomDisplay atomDisplay) {
		for (CMLAtom atom : molecule.getAtoms()) {
    		AtomTool atomTool = getOrCreateAtomTool(atom);
    		atomTool.ensureAtomDisplay();
    		atomTool.getAtomDisplay().setMoleculeDisplay(moleculeDisplay);
    		atomTool.setMoleculeTool(this);
    		if (atomTool.getAtomDisplay().omitAtom(atom)) {
    			continue;
    		}
    		GraphicsElement a = atomTool.createGraphicsElement(drawable);
    		if (a != null) {
    			g.appendChild(a);
    		}
		}
	}

	private void displayBonds(
		CMLDrawable drawable, SVGElement g,
		BondDisplay bondDisplay, AtomDisplay atomDisplay) {
		for (CMLBond bond : molecule.getBonds()) {
    		BondTool bondTool = getOrCreateBondTool(bond);
    		bondTool.setBondDisplay(bondDisplay);
    		bondTool.setMoleculeTool(this);
    		if (bondTool.getBondDisplay().omitBond(bond)) {
    			continue;
    		}
    		SVGElement b = null;
    		try {
    			b = bondTool.createGraphicsElement(drawable);
        	} catch (RuntimeException e) {
    			LOG.warn("Cannot draw bond; skipped: "+e.getMessage());
        	}
    		if (b != null) {
    			g.appendChild(b);
    		}
		}
	}
	
	private void displayMoleculeNames(CMLDrawable drawable, SVGElement g) {
		double x = 50.;
		double y = 50.;
		double deltay = 20.0;
		int i = 0;
    	Transform2 transform2 = new Transform2(
			new double[] {
				1.,  0., 0.0,
				0., -1., 0.0,
				0.,  0., 1.}
			);
		for (CMLName name : molecule.getNameElements()) {
			SVGText text = new SVGText(new Real2(x, y+(i*deltay)), name.getValue());
	    	text.setTransform(transform2);
			g.appendChild(text);
			i++;
		}
	}
    
	private void displayMoleculeLabels(CMLDrawable drawable, SVGElement g) {
		calculateBoundingBox2D();
		RealRange xRange = userBoundingBox.getXRange();
		double xMid = xRange.getMidPoint();
//		double minY = userBoundingBox.getYRange().getMin();
		double maxY = userBoundingBox.getYRange().getMax();
    	Transform2 transform2 = new Transform2(
			new double[] {
				1.,  0., 0.0,
				0., -1., 0.0,
				0.,  0., 1.}
			);
		if (molecule.getLabelElements().size() > 0) {
			double labelFontSize = moleculeDisplay.getLabelFontSize();
			double labelYSpacing = moleculeDisplay.getLabelYSpacing();
//			double yLabel0 = maxY + labelYSpacing * labelFontSize;
			double yLabel0 = maxY;
			for (CMLLabel label : molecule.getLabelElements()) {
				String labelS = label.getCMLValue();
				double xLabel = xMid - labelFontSize * (labelS.length() / 2.); 
				double yLabel = labelYSpacing * labelFontSize;
				SVGText text = new SVGText(new Real2(xLabel, yLabel0 + yLabel), labelS);
				text.setFontWeight("bold");
				text.setFontSize(labelFontSize);
		    	text.setTransform(transform2);
				g.appendChild(text);
			}
		}
	}
    
	private void displayFormula(CMLDrawable drawable, SVGElement g) {
    	if (molecule.getFormulaElements().size() > 0) {
    		CMLFormula formula = molecule.getFormulaElements().get(0);
    		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
    		SVGElement f = formulaTool.createGraphicsElement(drawable);
    		if (f != null) {
    	    	Transform2 transform2 = new Transform2(
	    			new double[] {
	    				1.,  0., 0.0,
	    				0., -1., 0.0,
	    				0.,  0., 1.}
	    			);
    	    	f.setTransform(transform2);
    			g.appendChild(f);
    		}
    	}
	}
    
    public void enableMoleculeDisplay() {
    	if (moleculeDisplay == null) {
    		moleculeDisplay = MoleculeDisplay.getDEFAULT();
    	}
    }

	/**
	 * @return the atomToolMap
	 */
	public Map<CMLAtom, AtomTool> getAtomToolMap() {
		return atomToolMap;
	}

	/**
	 * @return the bondToolMap
	 */
	public Map<CMLBond, BondTool> getBondToolMap() {
		return bondToolMap;
	}

	/**
	 * @return the selectionTool
	 */
	public SelectionTool getOrCreateSelectionTool() {
		if (selectionTool == null) {
			selectionTool = new SelectionTool();
		}
		return selectionTool;
	}

	/**
	 * @return the selectionTool
	 */
	public SelectionTool getSelectionTool() {
		return selectionTool;
	}

	/**
	 * @param selectionTool the selectionTool to set
	 */
	public void setSelectionTool(SelectionTool selectionTool) {
		this.selectionTool = selectionTool;
	}

	/**
	 * @return the MoleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = new MoleculeDisplay(this);
		}
		return moleculeDisplay;
	}

	/**
	 * @param MoleculeDisplay the MoleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay MoleculeDisplay) {
		this.moleculeDisplay = MoleculeDisplay;
	}

//	
//	public void setCoordinateAxes(CoordinateType type, AxisType axis) {
//		if (CoordinateType.CARTESIAN.equals(type)) {
////			String hand = molecule.get
//		} else {
//			throw new RuntimeException("May not change 3D axes handedness");
//		}
//	}

	/**
	 * @return the currentAtom
	 */
	public CMLAtom getCurrentAtom() {
		return currentAtom;
	}

	/**
	 * @param currentAtom the currentAtom to set
	 */
	public void setCurrentAtom(CMLAtom currentAtom) {
		this.currentAtom = currentAtom;
	}

	/**
	 * @return the currentBond
	 */
	public CMLBond getCurrentBond() {
		return currentBond;
	}

	/**
	 * @param currentBond the currentBond to set
	 */
	public void setCurrentBond(CMLBond currentBond) {
		this.currentBond = currentBond;
	}

	/**
	 * clears currentBond and then ensureCurrentBond()
	 * @return currentBond
	 */
	public CMLBond resetCurrentBond() {
		this.currentBond = null;
		ensureCurrentBond();
		return currentBond;
	}

	/** makes sure there is a current atom.
	 * if none set, select first atom (fragile)
	 * @return current atom or null if no atoms
	 */
	public CMLAtom ensureCurrentAtom() {
		if (currentAtom == null && molecule.getAtomCount() > 0) {
			currentAtom = molecule.getAtoms().get(0);
		}
		return currentAtom;
	}

	/** makes sure there is a current bond.
	 * if none set, select first ligandBond on current atom (fragile)
	 * @return current bond or null if currentAtom is null or has no ligands
	 */
	public CMLBond ensureCurrentBond() {
		if (currentBond == null && currentAtom != null) {
			List<CMLBond> ligandBonds = currentAtom.getLigandBonds();
			if (ligandBonds.size() > 0) {
				currentBond = ligandBonds.get(0);
			}
		}
		return currentBond;
	}
	
	/** steps through ligands of currentAtom.
	 * if currentBond contains currentAtom, steps through
	 * ligands of currentAtom else no-op
	 * @return next ligand bond or null if no currentBond
	 */
	public CMLBond incrementCurrentBond() {
		ensureCurrentBond();
		if (currentBond != null) {
			List<CMLBond> ligands = currentAtom.getLigandBonds();
			int idx = ligands.indexOf(currentBond);
			if (idx != -1) {
				idx = (idx+1) % ligands.size();
				currentBond = ligands.get(idx);
			}
		}
		return currentBond;
	}
	
	/**
	 * normalize all molecules which are descendant of node
	 * @param node
	 */
	public static void normalizeDescendantMolecules(Node node) {
		Nodes molecules = node.query(".//*[local-name()='molecule']");
    	for (int i = 0; i < molecules.size(); i++) {
    		MoleculeTool.getOrCreateTool((CMLMolecule)molecules.get(i)).normalize();
    	}
	}
	
	/** normalize.
	 * may be obsolete
	 * currently adjusts Hydrogen counts to valency
	 * also adds formula representing atomArray contents
	 */
    public void normalize() {
    	this.adjustHydrogenCountsToValency(
    			HydrogenControl.ADD_TO_HYDROGEN_COUNT);
    	Nodes nodes = molecule.query("./*[local-name()='formula' and @convention='"+Convention.ATOMARRAY+"']");
    	for (int i = 0; i < nodes.size(); i++) {
    		nodes.get(i).detach();
    	}
    	CMLFormula formula = new CMLFormula(molecule);
    	formula.setConvention(Convention.ATOMARRAY.v);
    	molecule.appendChild(formula);
    }


    /** renumbers atoms in molecule.
     * uses renumberAtomId
     * includes any attributes including atomRef/atomRefs2/atomRefs3/atomRefs4/atomRefs
     * @param fromIdList
     * @param toIdList
     * @throws RuntimeException if toId is already in molecule or if lists contain duplicates
     */
    public void renumberAtomIds(List<String> fromIdList, List<String> toIdList) {
    	if (fromIdList == null || toIdList == null) {
    		throw new RuntimeException("lists must not be null");
    	}
    	if (fromIdList.size() != toIdList.size()) {
    		throw new RuntimeException("Lists must be of equal size, "+fromIdList.size()+ " / "+toIdList.size());
    	}
    	checkUnique(fromIdList);
    	checkUnique(toIdList);
    	for (int i = 0; i < fromIdList.size(); i++) {
    		renumberAtomId(fromIdList.get(i), toIdList.get(i));
    	}
    }
    
    private Set<String> checkUnique(List<String> stringList) {
    	Set<String> stringSet = new HashSet<String>();
    	for (String string : stringList) {
    		if (stringSet.contains(string)) {
    			throw new RuntimeException("duplicate id: "+string);
    		}
    		stringSet.add(string);
    	}
    	return stringSet;
    }

    /** renumbers atom in molecule.
     * includes any attributes including atomRef/atomRefs2/atomRefs3/atomRefs4/atomRefs
     * @param fromId
     * @param toId
     * @throws RuntimeException if toId is already in molecule
     */
    public void renumberAtomId(String fromId, String toId) {
    	if (molecule.getAtomById(toId) != null) {
    		throw new RuntimeException("Atom with id already in molecule: "+toId);
    	}
    	CMLAtom atom = molecule.getAtomById(fromId);
    	if (atom != null) {
    		Nodes nodes = molecule.query(
    				".//*[@atomRef or @atomRefs2 or @atomRefs3 or @atomRefs4 or @atomRefs]", CML_XPATH);
    		for (int i = 0; i < nodes.size(); i++) {
    			renumberAtomRefs((CMLElement)nodes.get(i), fromId, toId);
    		}
    		atom.resetId(toId);
    	}
    }
    
    private void renumberAtomRefs(CMLElement element, String fromId, String toId) {
    	for (int i = 0; i < element.getAttributeCount(); i++) {
    		Attribute attribute = element.getAttribute(i);
    		if (attribute.getLocalName().indexOf("atomRef") != -1) {
    			String[] values = attribute.getValue().split(S_WHITEREGEX);
    			for (int j = 0; j < values.length; j++) {
    				if (values[j].equals(fromId)) {
    					values[j] = toId;
    				}
    			}
				attribute.setValue(Util.concatenate(values, S_SPACE));
				element.addAttribute(attribute);
    		}
    	}
    }
    
	/** get single electron by id
	 * @param id
	 * @return electron
	 */
    public CMLElectron getElectronById(String id) {
        Nodes electronNodes = molecule.query(".//cml:electron[@id='"+id+"']", CML_XPATH);
        if (electronNodes.size() > 1) {
        	throw new RuntimeException("Electrons with duplicate id:"+id);
        }
        return (electronNodes.size() == 0) ? null : (CMLElectron) electronNodes.get(0);
    }

    /** gets molar volume.
     * if molecule has a child property, uses that.
     * else if molecule has a child property for density uses that
     * else fails
     * new property has units of CMLCUBED
     * @return property with molarVolume
     */
    public CMLProperty getMolarVolume() {
    	CMLProperty volume = CMLProperty.getProperty(molecule, CMLProperty.Prop.MOLAR_VOLUME.value);
    	if (volume == null) {
        	CMLProperty density = CMLProperty.getProperty(molecule, CMLProperty.Prop.DENSITY.value);
        	if (density == null) {
        		throw new RuntimeException("Cannot calculate molar volume without density");
        	}
        	if (!Units.GRAM_PER_CMCUBED.value.equals(density.getUnits())) {
        		throw new RuntimeException("Cannot use density without units=g.cm-3");
        	}
        	double densityV = density.getDouble();
        	double volumeV = this.getCalculatedMolecularMass() / densityV;
        	volume = new CMLProperty(CMLProperty.Prop.MOLAR_VOLUME.value, 
        			volumeV, Units.CMCUBED.value);
    	}
    	return volume;
    }
    
    /** gets molar mass.
     * if molecule has a child property, uses that.
     * else if molecule has atomArray uses that to count atoms
     * else if molecule has a child formula uses that 
     * else fails
     * new property has units of Units.GRAM_PER_MOLE
     * @return property with molarMass
     */
    public CMLProperty getMolarMass() {
    	CMLProperty mass = CMLProperty.getProperty(molecule, CMLProperty.Prop.MOLAR_MASS.value);
    	if (mass == null) {
        	double massV = this.getCalculatedMolecularMass();
        	if (!Double.isNaN(massV)) {
        		mass = new CMLProperty(CMLProperty.Prop.MOLAR_MASS.value, 
        			massV, Units.GRAM_PER_MOLE.value);
        	}
    	}
    	return mass;
    }

    /** fits two aligned molecules.
     * normally connection tables will be identical
     * ignores any atoms for which there are no 3D coordinates
     * also ignores any multiple atom sets in map
     * @param map atom-atom map
     * @param molecule
     *      * @return
     */
	public MoleculePair fitToMoleculeTool(CMLMap map, CMLMolecule moleculeRef) {
		MoleculeTool moleculeRefTool = MoleculeTool.getOrCreateTool(moleculeRef);
		List<CMLAtom> atomListi = this.getAtomList(map, Direction.FROM);
		Point3Vector p3vi = AtomTool.getPoint3Vector(atomListi);
		List<CMLAtom> atomListj = moleculeRefTool.getAtomList(map, Direction.TO);
		Point3Vector p3vj = new Point3Vector(AtomTool.getPoint3Vector(atomListj));
		Point3Vector.removeNullValues(p3vi, p3vj);
		p3vj = new Point3Vector(p3vj);
		Transform3 t3 = null;
		if (p3vi.size() > 3) {
			t3 = p3vj.fitTo(p3vi);
			Point3Vector pvtemp = new Point3Vector(p3vj);
			pvtemp.transform(t3);
			System.out.println("RMS "+pvtemp.rms(p3vi)+" / \n");
		}
		
		List<String[]> fromSets = map.getFromSetRefs();
//		for (String[] from : fromSets) {
//			for (String s : from) {
//				System.out.print(s+" ");
//			}
//			System.out.println("...");
//		}
		List<String[]> toSets = map.getToSetRefs();
//		for (String[] to : toSets) {
//			for (String s : to) {
//				System.out.print(s+" ");
//			}
//			System.out.println("...");
//		}
		AtomMatcher atomMatcher = new AtomMatcher();
		for (int i = 0; i < fromSets.size(); i++) {
			CMLAtomSet atomSet1 = new CMLAtomSet(molecule, fromSets.get(i));
			CMLAtomSet atomSet2 = new CMLAtomSet(moleculeRef, toSets.get(i));
			CMLMap geomMap = atomMatcher.matchAtomsByCoordinates(atomSet1, atomSet2, t3);
			geomMap.debug("GEOMMAP");
		}
		MoleculePair moleculePair = new MoleculePair(this.molecule, moleculeRef);
		moleculePair.setTransform3(t3);
		moleculePair.setMap(map);
		return moleculePair;
	}
    
	/**
	 * joins RGroups to molecule.
	 * Looks for atoms with elementType="R" and a child label
	 * cml:atom[@elementYype='R' and cml:label]
	 * takes value of label ( labelS = cml:label/@value)
	 * Looks for correspoding defintion of R as a descendant cml:molecule within
	 * scopeElement,
	 *   i.e. scopeElement.query(".//cml:atom[label/@value=$labelS", CML_XPATH)
	 *   @deprecated doen't work at all
	 */
	public void joinRGroupsExplicitly(Element scopeElement) {
		List<CMLAtom> atomList = molecule.getAtoms();
		for (CMLAtom atom : atomList) {
			if ("R".equals(atom.getElementType())) {
//				AtomTool atomTool = AtomTool.getOrCreateTool(atom);
				//FIXME
//				CMLAtom refAtom = atomTool.getReferencedGroup(scopeElement);
			}
		}
	}
	
	/** removes all child nodes except atomArray and bondArray and molecule.
	 * This is because many public programs only accept these nodes
	 */
	public void stripNonArray() {
		Nodes nodes = molecule.query(
				"*[not(self::cml:atomArray) and " +
				"not(self::cml:bondArray) and "+
				"not(self::cml:molecule)]",
				CML_XPATH); 
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	/**
	 * calculates Morgan (caches result)
	 * @return cached Morgan 
	 */
	public Morgan calculateMorgan() {
		if (morgan == null) {
			morgan = new Morgan(molecule);
		}
		return morgan;
	}

	/**
	 * calculate list of atom equivalence classes
	 * @return list of equivalent atoms as atomSets
	 */
	public List<CMLAtomSet> calculateListOfMorganAtomMatches() {
		calculateMorgan();
		List<CMLAtomSet> atomSetList = morgan.getAtomSetList();
		return atomSetList;
	}

	/**
	 * 
	 * @return morgan string
	 */
	public String calculateMorganString() {
		calculateMorgan();
		String morganString = morgan.getEquivalenceString();
		return morganString;
	}

	/** sets morgan to null
	 * 
	 */
	public void clearMorgan() {
		this.morgan = null;
	}

    /** converts a mass to a molarAmount.
     * @param molecule
     * @return molar Amount (units are mol)
     */
    public CMLAmount getMolarAmount(CMLAmount amount0) {
    	CMLAmount molarAmount = null;
    	UnitsAttribute units = (UnitsAttribute) amount0.getUnitsAttribute();
    	if (units == null) {
    		throw new RuntimeException("No units given on amount");
    	}
    	String unitValue = (String) units.getCMLValue();
    	if (Units.GRAM.value.equals(unitValue)) {
    		double d = this.getCalculatedMolecularMass(HydrogenControl.NO_EXPLICIT_HYDROGENS);
    		double amountx = amount0.getXMLContent();
    		molarAmount = new CMLAmount();
    		molarAmount.setUnits(Units.MMOL.value);
    		molarAmount.setXMLContent(amountx / d);
    	} else {
    		throw new RuntimeException("Cannot handle units other than gram");
    	}
    	return molarAmount;
    }
    
    /** converts a volume to a molarAmount.
     * @param molecule (must contain data for molarVolume - moleculeTool.getMolarVolume())
     * @return molar Amount (units are mol)
     */
    public CMLAmount getMolarAmountFromVolume(CMLAmount amount0) {
    	CMLAmount molarAmount = null;
    	UnitsAttribute units = (UnitsAttribute) amount0.getUnitsAttribute();
    	if (units == null) {
    		throw new RuntimeException("No units given on amount");
    	}
    	String unitValue = (String) units.getCMLValue();
    	if (unitValue != null && Units.ML.value.equals(unitValue)) {
    		CMLProperty volume = MoleculeTool.getOrCreateTool(molecule).getMolarVolume();
    		if (volume != null) {
	    		double amountv = amount0.getXMLContent();
	    		molarAmount = new CMLAmount();
	    		molarAmount.setUnits(Units.MOL.value);
	    		molarAmount.setXMLContent(amountv / volume.getDouble());
    		}
    	}
    	return molarAmount;
    }
    
	/**
	 * Calculate formula.
	 *
	 * @param control
	 *            USE_EXPLICIT_HYDROGENS (do not use hydrogenCount) OR
	 *            USE_HYDROGEN_COUNT (use hydrogenCount and ignore explicit H)
	 * @throws RuntimeException
	 * @return formula
	 */
	public CMLFormula calculateFormula(HydrogenControl control)
	throws RuntimeException {
		CMLFormula form = new CMLFormula();
		if (!control.equals(HydrogenControl.USE_HYDROGEN_COUNT)
				&& !control.equals(HydrogenControl.USE_EXPLICIT_HYDROGENS)) {
			throw new RuntimeException(
					"No hydrogen count control on Formula - found(" + control
					+ S_RBRAK);
		}

		CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(molecule.getAtoms());
		form = atomSet.getCalculatedFormula(control);
		return form;
	}
	
	/**
	 * creates and adds cartesian coordinates from crystal and fractional
	 * coordinates.
	 *
	 * @param crystal
	 */
	public void createCartesiansFromFractionals(CMLCrystal crystal) {
		this.createCartesiansFromFractionals(crystal
			.getOrthogonalizationMatrix());
	}

	/**
	 * creates and adds cartesian coordinates from orthogonalizationMatrix and
	 * fractional coordinates.
	 *
	 * @param orthogonalMatrix
	 */
	public void createCartesiansFromFractionals(
			Transform3 orthogonalMatrix) {
		for (CMLAtom atom : molecule.getAtoms()) {
			Point3 point = atom.getPoint3(CoordinateType.FRACTIONAL);
			if (point != null) {
				point = point.transform(orthogonalMatrix);
				atom.setPoint3(point, CoordinateType.CARTESIAN);
			}
		}
	}

	/**
	 * creates and adds cartesian coordinates from orthogonalizationMatrix and
	 * fractional coordinates.
	 *
	 * @param orthogonalMatrix
	 * @throws RuntimeException
	 */
	void createCartesiansFromFractionals(
			RealSquareMatrix orthogonalMatrix) {
		Transform3 t = null;
		if (orthogonalMatrix == null || orthogonalMatrix.getCols() != 3) {
			throw new RuntimeException("invalid or null orthogonalMatrix");
		}
		try {
			t = new Transform3(orthogonalMatrix);
		} catch (EuclidRuntimeException e) {
			throw new RuntimeException("invalid orthogonalMatrix");
		}
		this.createCartesiansFromFractionals(t);
	}

	/**
	 * finds angle in XOM corresponding to 3 atoms.
	 *
	 * @param at0
	 *            first atom
	 * @param at1
	 *            middle atom
	 * @param at2
	 *            third atom
	 * @return CMLAngle in XOM
	 * @throws RuntimeException
	 */
	public CMLAngle getAngle(CMLAtom at0, CMLAtom at1, CMLAtom at2)
	throws RuntimeException {
		if (at0 == null || at1 == null || at2 == null) {
			throw new RuntimeException("FindAngle: null atom(s)");
		}
		for (CMLAngle angle : this.getAngleElements()) {
			String[] atomRefs3 = angle.getAtomRefs3();
			if (atomRefs3 == null) {
				continue;
			}
			CMLAtom a1 = molecule.getAtomById(atomRefs3[1]);
			if (!at1.equals(a1)) {
				continue;
			}
			CMLAtom a0 = molecule.getAtomById(atomRefs3[0]);
			CMLAtom a2 = molecule.getAtomById(atomRefs3[2]);
			if ((a0.equals(at0) && a2.equals(at2))
					|| (a0.equals(at2) && a2.equals(at0))) {
				return angle;
			}
		}
		return null;
	}
	
	/**
	 * calculates formula for molecule or each molecule child.
	 *
	 * @param control
	 *            treatment of hydrogens
	 */
	public void calculateAndAddFormula(HydrogenControl control) {
		List<CMLMolecule> molecules = this.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule1 : molecules) {
				MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(molecule1);
				moleculeTool1.calculateAndAddFormula(control);
			}
		} else {
			CMLFormula formula = null;
			formula = this.calculateFormula(control);
			molecule.appendChild(formula);
		}
	}

	/**
	 * get atomSet for all atoms.
	 *
	 * @return the atomSet
	 */
	public CMLAtomSet getAtomSet() {
		return new CMLAtomSet(molecule);
	}

	/**
	 * get formula.
	 * if molecule has atoms uses those, else uses formula else null
	 * @param control
	 * @return calculated formula
	 * @throws RuntimeException
	 */
	public CMLFormula getCalculatedFormula(CMLMolecule.HydrogenControl control) {
		CMLFormula formula = null;
		CMLAtomSet atomSet = getAtomSet();
		if (atomSet == null || atomSet.size() == 0) {
			formula = this.getFormulaElements().get(0);
		} else {
			formula = atomSet.getCalculatedFormula(control);
		}
		return formula;
	}
	
	/**
	 * get calculated molecular mass.
	 * @param control
	 * @return calculated molecular mass.
	 * @throws RuntimeException unknown/unsupported element type (Dummy counts as zero mass)
	 */
	public double getCalculatedMolecularMass(CMLMolecule.HydrogenControl control) throws RuntimeException {
		CMLFormula formula = this.getCalculatedFormula(control);
		if (formula == null) {
			throw new RuntimeException("Cannot calculate formula");
		}
		return formula.getCalculatedMolecularMass();
	}
	
//	/**
//	 * get calculated molecular mass. Assumes correct hydrogen count
//	 * @return calculated molecular mass.
//	 * @throws RuntimeException unknown/unsupported element type (Dummy counts as zero mass)
//	 * @deprecated use MoleculeTool.getCalculatedMolecularMass()
//	 */
//	public double getCalculatedMolecularMass() throws RuntimeException {
//		return this.getCalculatedMolecularMass(HydrogenControl.NO_EXPLICIT_HYDROGENS);
//	}
	
	/** calculate 3D centroid.
	 *
	 * @param type CARTESIAN or FRACTIONAL
	 * @return centroid of 3D coords or null
	 */
	public Point3 calculateCentroid3(CoordinateType type) {
		CMLAtomSet atomSet = getAtomSet();
		return atomSet.getCentroid3(type);
	}

	/** calculate 3D range.
	 *
	 * @param type CARTESIAN or FRACTIONAL
	 * @return range of 3D coords or null
	 */
	public Real3Range calculateRange3(CoordinateType type) {
		CMLAtomSet atomSet = getAtomSet();
		return atomSet.calculateRange3(type);
	}

	/**
	 * get id mapping between molecules of equal size.
	 *
	 * default is to assume atoms in same order. map is owned by this document
	 * but is not appended to molecule.
	 *
	 * @param mol2
	 *            to compare
	 * @exception RuntimeException
	 *                atoms cannot be mapped
	 * @return the map
	 */
	public CMLMap getMap(CMLMolecule mol2) {
		CMLMap map = new CMLMap();
		map.addAttribute(new Attribute("toMolecule", mol2.getId()));
		List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> atoms2 = mol2.getAtoms();
		if (atoms.size() != atoms2.size()) {
			throw new RuntimeException(
					"MoleculesMolecules have different lengths: "
					+ atoms.size() + " != " + atoms2.size());
		}
		for (int i = 0; i < atoms.size(); i++) {
			String id = ((CMLAtom) atoms.get(i)).getId();
			String id2 = ((CMLAtom) atoms2.get(i)).getId();
			String e1 = ((CMLAtom) atoms.get(i)).getElementType();
			String e2 = ((CMLAtom) atoms2.get(i)).getElementType();
			if (!(e1 == null && e2 == null ||
					e1.equals(e2))) {
				throw new RuntimeException(
						"atoms have different excludeElementTypes: " + id + S_LBRAK
						+ e1 + ") != " + id2 + S_LBRAK + e2 + S_RBRAK);
			}
			CMLLink link = new CMLLink();
			link.setFrom(id);
			link.setTo(id2);
			map.addLink(link);
		}
		return map;
	}

	/** get matched atom. does not work with sets
	 *
	 * if atom id="a1" and map hass a link to="a1" from="b1" and toFrom =
	 * Direction.FROM then will return atom id="b1" in resultMolecule
	 *
	 * @param atom0
	 *            atom to search with. Its id must occur in a single toFrom
	 *            attribute
	 * @param map
	 *            with links
	 * @param toFrom
	 *            specifies attribute for target atom
	 * @return mapped atom or null
	 */
	public CMLAtom getMappedAtom(CMLMap map, CMLAtom atom0, Direction toFrom) {
		CMLAtom targetAtom = null;
		if (atom0 != null && map != null) {
			String targetId = map.getRef(atom0.getId(), toFrom);
			if (targetId != null) {
				targetAtom = molecule.getAtomById(targetId);
			}
		}
		return targetAtom;
	}

	/**
	 * gets vector of 3D coordinates.
	 *
	 * all atoms must have coordinates
	 *
	 * @param type
	 *            CARTESIAN or FRACTIONAL
	 * @return the vector (null if missing 3D coordinates)
	 */
	public Point3Vector getCoordinates3(CoordinateType type) {
		CMLAtomSet atomSet = this.getAtomSet();
		return atomSet.getCoordinates3(type);
	}

	/**
	 * generate list of transformed molecules. operates on Cartesians; does not
	 * modify this
	 *
	 * @param sym
	 *            the symmetries
	 * @return list of molecules
	 */
	public List<CMLMolecule> transformCartesians(CMLSymmetry sym) {
		List<CMLMolecule> molList = new ArrayList<CMLMolecule>();
		for (CMLTransform3 tr : sym.getTransform3Elements()) {
			CMLMolecule molecule = new CMLMolecule(this.getMolecule());
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.transformCartesians(tr);
		}
		return molList;
	}

	/**
	 * transform 3D Cartesian coordinates. modifies this
	 *
	 * @param transform
	 *            the transformation
	 */
	public void transformCartesians(CMLTransform3 transform) {
		CMLAtomSet atomSet = this.getAtomSet();
		if (atomSet != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			atomSetTool.transformCartesians(transform);
		}
	}

	/** transform 3D Cartesian coordinates. modifies this
	 *
	 * @param transform
	 *            the transformation
	 */
	public void transformCartesians(Transform3 transform) {
		CMLAtomSet atomSet = this.getAtomSet();
		if (atomSet != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			atomSetTool.transformCartesians(transform);
		}
	}

	/** generate list of transformed molecules. operates on fractionals; does
	 * modify Cartesians or modify this
	 *
	 * @param sym
	 *            the symmetries
	 * @return list of molecules
	 */
	public List<CMLMolecule> transformFractionalCoordinates(CMLSymmetry sym) {
		List<CMLMolecule> molList = new ArrayList<CMLMolecule>();
		for (CMLTransform3 tr : sym.getTransform3Elements()) {
			CMLMolecule molecule = new CMLMolecule(this.getMolecule());
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			moleculeTool.transformFractionalCoordinates(tr);
			molList.add(molecule);
		}
		return molList;
	}

	/**
	 * transform 3D fractional coordinates. modifies this does not affect x3,
	 * y3, z3 (may need to re-generate cartesians)
	 *
	 * @param transform
	 *            the transformation
	 */
	public void transformFractionalCoordinates(CMLTransform3 transform) {
		CMLAtomSet atomSet = this.getAtomSet();
		if (atomSet != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			atomSetTool.transformFractionals(transform);
		}
	}

	/**
	 * transform 3D fractional coordinates. modifies this does not affect x3,
	 * y3, z3 (may need to re-generate cartesians)
	 *
	 * @param transform
	 *            the transformation
	 */
	public void transformFractionalCoordinates(Transform3 transform) {
		CMLAtomSet atomSet = this.getAtomSet();
		if (atomSet != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			atomSetTool.transformFractionals(transform);
		}
	}

	/**
	 * transform fractional and 3D coordinates. does NOT alter 2D coordinates
	 * transforms fractionals then applies orthogonalisation to result
	 * @param transform
	 *            the fractional symmetry transformation
	 * @param orthMat
	 *            orthogonalisation matrix
	 */
	public void transformFractionalsAndCartesians(CMLTransform3 transform, Transform3 orthMat) {
		CMLAtomSet atomSet = this.getAtomSet();
		if (atomSet != null) {
			AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
			atomSetTool.transformFractionalsAndCartesians(transform, orthMat);
		}
	}
	/**
	 * transform molecule in 2D.
	 *
	 * @param matrix 3*3 matrix apply to all 2D coordinates
	 */
	public void transform(CMLMatrix matrix) {
		if (matrix == null ||
				matrix.getRows() != 3 ||
				matrix.getColumns() != 3) {
			throw new RuntimeException("bad transformation matrix");
		}
		double[] mm = matrix.getDoubleArray();
		Transform2 t2 = new Transform2(mm);
		this.transform(t2);
	}


	/**
	 * transform
	 * @param t2
	 */
	public void transform(Transform2 t2) {
		for (CMLAtom atom : molecule.getAtoms()) {
			if (atom.hasCoordinates(CoordinateType.TWOD)) {
				Real2 dd = new Real2(atom.getX2(), atom.getY2());
				dd.transformBy(t2);
				atom.setXY2(dd);
			}
		}
	}
	/**
	 * translate molecule in 3D.
	 *
	 * @param delta3
	 *            add to all 3D coordinates
	 */
	public void translate3D(Vector3 delta3) {
		CMLAtomSet atomSet = getAtomSet();
		if (atomSet != null) {
			atomSet.translate3D(delta3);
		}
	}

//	/** ensure integrity between list and children.
//	 * @return CMLMoleculeList.class
//	 */
//	public Class<?> getIndexableListClass() {
//		return CMLMoleculeList.class;
//	}

	
	/** create molecule from atomSet.
	 * clones atoms
	 * does not add bonds.
	 * @param atomSet
	 */
	public static CMLMolecule createMolecule(CMLAtomSet atomSet) {
//		this();
		CMLMolecule molecule = new CMLMolecule();
		List<CMLAtom> atoms = atomSet.getAtoms();
		for (CMLAtom atom : atoms) {
			molecule.addAtom(new CMLAtom(atom));
		}
		return molecule;
	}

	/** create molecule from atomSet and bondSet.
	 * clones atoms and bonds
	 * @param atomSet
	 * @param bondSet
	 */
	public static CMLMolecule createMolecule(CMLAtomSet atomSet, CMLBondSet bondSet) {
		CMLMolecule molecule = MoleculeTool.createMolecule(atomSet);
		List<CMLBond> bonds = bondSet.getBonds();
		for (CMLBond bond : bonds) {
			molecule.addBond(new CMLBond(bond));
		}
		return molecule;
	}

//    /** converts a mass to a molarAmount.
//     * @param molecule
//     * @return molar Amount (units are mol)
//     */
//    public CMLAmount getMolarAmount(CMLMolecule molecule) {
//    	CMLAmount molarAmount = null;
//    	UnitsAttribute units = (UnitsAttribute) molecule.getUnitsAttribute();
//    	if (units == null) {
//    		throw new RuntimeException("No units given on amount");
//    	}
//    	String unitValue = (String) units.getCMLValue();
//    	if (Units.GRAM.value.equals(unitValue)) {
//    		double d = this.getCalculatedMolecularMass(HydrogenControl.NO_EXPLICIT_HYDROGENS);
//    		double amountx = molecule.getXMLContent();
//    		molarAmount = new CMLAmount();
//    		molarAmount.setUnits(Units.MMOL.value);
//    		molarAmount.setXMLContent(amountx / d);
//    	} else {
//    		throw new RuntimeException("Cannot handle units other than gram");
//    	}
//    	return molarAmount;
//    }


    /** get list of length children.
     * 
     * @return list (empty if none)
     */
    public List<CMLLength> getLengthElements() {
    	Nodes nodes = molecule.query("./cml:length", CML_XPATH);
    	List<CMLLength> list = new ArrayList<CMLLength>();
    	for (int i = 0; i < nodes.size(); i++) {
    		list.add((CMLLength)nodes.get(i));
    	}
    	return list;
    }
    
    /** get list of angle children.
     * 
     * @return list (empty if none)
     */
    public List<CMLAngle> getAngleElements() {
    	Nodes nodes = molecule.query("./cml:angle", CML_XPATH);
    	List<CMLAngle> list = new ArrayList<CMLAngle>();
    	for (int i = 0; i < nodes.size(); i++) {
    		list.add((CMLAngle)nodes.get(i));
    	}
    	return list;
    }
    
    /** get list of torsion children.
     * 
     * @return list (empty if none)
     */
    public List<CMLTorsion> getTorsionElements() {
    	Nodes nodes = molecule.query("./cml:torsion", CML_XPATH);
    	List<CMLTorsion> list = new ArrayList<CMLTorsion>();
    	for (int i = 0; i < nodes.size(); i++) {
    		list.add((CMLTorsion)nodes.get(i));
    	}
    	return list;
    }
    
    /** get list of formula children.
     * 
     * @return list (empty if none)
     */
    public List<CMLFormula> getFormulaElements() {
    	Nodes nodes = molecule.query("./cml:formula", CML_XPATH);
    	List<CMLFormula> list = new ArrayList<CMLFormula>();
    	for (int i = 0; i < nodes.size(); i++) {
    		list.add((CMLFormula)nodes.get(i));
    	}
    	return list;
    }
    
    /** get list of molecule children.
     * 
     * @return list (empty if none)
     */
    public List<CMLMolecule> getMoleculeElements() {
    	Nodes nodes = molecule.query("./cml:molecule", CML_XPATH);
    	List<CMLMolecule> list = new ArrayList<CMLMolecule>();
    	for (int i = 0; i < nodes.size(); i++) {
    		list.add((CMLMolecule)nodes.get(i));
    	}
    	return list;
    }
}

