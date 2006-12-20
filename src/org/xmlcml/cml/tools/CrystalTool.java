package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real3Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;

/**
 * tool for managing crystals
 * 
 * @author pmr
 * 
 */
public class CrystalTool extends AbstractTool {
    final static Logger logger = Logger.getLogger(CrystalTool.class.getName());

	Map<String, Integer> formulaCountMap = new HashMap<String, Integer>();

    /** multiplicity of atom symmetry.
     */
    public final static String DICT_MULTIPLICITY = "cml:mult";

    /** cif stuff */
    public final static String IUCR = "iucr";
    /** cif stuff */
    public final static String DISORDER_GROUP = 
        IUCR+S_COLON+"_atom_site_disorder_group";
    /** cif stuff */
    public final static String DISORDER_ASSEMBLY = 
        IUCR+S_COLON+"_atom_site_disorder_assembly";
    /** cif stuff */
    public final static String ATOM_LABEL = 
        IUCR+S_COLON+"_atom_site_label";

    /** tolerance for comparing occupancies.
     */
    public final static double OCCUPANCY_EPS = 0.0001;
    
    CMLMolecule molecule;
    MoleculeTool moleculeTool = null;
    CMLCrystal crystal = null;
    CMLSymmetry symmetry = null;
    List<CMLScalar> cellParams = null;

    double squaredDistanceTolerance = 0.001;

    /** constructor.
     * requires molecule to contain <crystal> and optionally <symmetry>
     * @param molecule
     * @throws CMLRuntimeException must contain a crystal
     */
    public CrystalTool(CMLMolecule molecule) throws CMLRuntimeException {
        init();
        setMolecule(molecule);
        this.crystal = CMLCrystal.getContainedCrystal(molecule);
        if (this.crystal == null) {
            throw new CMLRuntimeException("molecule should contain a <crystal> child");
        }
        this.symmetry = CMLSymmetry.getContainedSymmetry(molecule);
    }

    /** sets molecule and moleculeTool.
     * @param molecule
     */
    void setMolecule(CMLMolecule molecule) {
        this.molecule = molecule;
        if (molecule != null) {
            moleculeTool = new MoleculeTool(molecule);
        }
    }

    /** constructor.
     * 
     * @param molecule
     * @param crystal may, but not must, contain symmetry 
     * 
     */
    public CrystalTool(CMLMolecule molecule, CMLCrystal crystal) {
        init();
        setMolecule(molecule);
        this.crystal = crystal;
        try {   
            this.symmetry = CMLSymmetry.getContainedSymmetry(crystal);
        } catch (CMLRuntimeException e) {
            // ignore no symmetry
        }
    }

    void init() {
        crystal = null;
        // cellParams = null;
        symmetry = null;
        logger.setLevel(Level.INFO);
    }

    /** constructor with embedded molecule.
     * 
     * @param molecule (should include a crystal child)
     * @param symmetry
     * @throws CMLRuntimeException molecule does not contain <crystal> child
     */
    public CrystalTool(CMLMolecule molecule, CMLSymmetry symmetry) throws CMLRuntimeException {
        setMolecule(molecule);
        this.symmetry = symmetry;
        this.crystal = CMLCrystal.getContainedCrystal(molecule);
        if (this.crystal == null) {
            throw new CMLRuntimeException("molecule should contain a <crystal> child");
        }
    }
    
    /** constructor.
     * 
     * @param molecule
     * @param crystal
     * @param symmetry 
     * 
     */
    public CrystalTool(CMLMolecule molecule, CMLCrystal crystal, CMLSymmetry symmetry) {
        init();
        setMolecule(molecule);
        this.crystal = crystal;
        this.symmetry = symmetry;
    }

    /**
     * get crystal.
     * 
     * @return the crystal or null
     */
    public CMLCrystal getCrystal() {
        return this.crystal;
    }

    /** gets crystallochemical unit.
     * @param dist2Range minimum and maximum SQUARED distances
     * @return merged molecule
     */
    public CMLMolecule calculateCrystallochemicalUnit(RealRange dist2Range, CMLFormula moietyFormula) {
        List<Contact> contactList = moleculeTool.getSymmetryContacts(dist2Range, this);
        
        boolean addBonds = true;
        new ConnectionTableTool(molecule).partitionIntoMolecules();
        CMLMolecule mergedMolecule = this.getMergedMolecule(
            molecule, contactList, addBonds);
        
        // need to flatten then partition molecules as at this point the symmetry related
        // molecules (which had been partitioned) have been merged, so just partitioning the
        // container molecules could lead to sub-molecules of sub-molecules, which isn't what we want.
        ConnectionTableTool ct = new ConnectionTableTool(mergedMolecule);
        ct.flattenMolecules();
        ct.partitionIntoMolecules();

        List<CMLMolecule> mols = mergedMolecule.getDescendantsOrMolecule();
        for (CMLMolecule mol : mols) {
        	if (!MoleculeTool.isDisordered(mol)) {
        		MoleculeTool subMolTool = new MoleculeTool(mol);
        		subMolTool.adjustBondOrdersAndChargesToValency(moietyFormula);
        	} else {
        		System.out.println("molecule is disoredered");
        	}
        }
        return mergedMolecule;
    }
    
    public CMLMolecule calculateCrystallochemicalUnit(RealRange dist2Range) {
    	return calculateCrystallochemicalUnit(dist2Range, null);
    }    
    
    /**
     * calculate cartesians and bonds.
     * do not apply symmetry
     */
    public void calculateCartesiansAndBonds() {
        molecule.createCartesiansFromFractionals(crystal);
        MoleculeTool moleculeTool = new MoleculeTool(molecule);
        moleculeTool.calculateBondedAtoms();
        new ConnectionTableTool(molecule).partitionIntoMolecules();
    }

    /** find multiplicities for all atoms.
     * adds scalar children to any atom with multiplicity > 1
     *
     */
    public void annotateSpaceGroupMultiplicities() {
        if (molecule != null && symmetry != null) {
            List<CMLAtom> atoms = molecule.getAtoms();
            for (CMLAtom atom : atoms) {
                Point3 xyzFract = atom.getXYZFract();
                if (xyzFract != null) {
                    int mult = symmetry.getSpaceGroupMultiplicity(xyzFract);
                    if (mult > 1) {
                        CMLScalar scalar = new CMLScalar(mult);
                        scalar.setDictRef(DICT_MULTIPLICITY);
                        atom.appendChild(scalar);
                    }
                }
            }
        }
    }
    

    /** gets list of contacts to symmetry-related molecules.
     * experimental.
     * @param dist2Range max and min squared distances to consider
     * @return contacts
     */
    public List<Contact> getSymmetryContactsToMolecule(RealRange dist2Range) {
        molecule.createCartesiansFromFractionals(crystal);
        List<Contact> contactList = new ArrayList<Contact>();
        if (symmetry != null && molecule != null) {
            
            Real3Range range3Fract = molecule.calculateRange3(CoordinateType.FRACTIONAL);
            // create a wider border round the molecule
            expandRange(range3Fract, crystal, dist2Range);
            Transform3 orthMat = null;
            try {
                orthMat = new Transform3(crystal.getOrthogonalizationMatrix());
            } catch (Exception e) {
                throw new CMLRuntimeException("invalid orthogonalMatrix");
            }
            CMLSymmetry nonTranslateSymmetry = symmetry.getNonTranslations();
            List<CMLMolecule> subMolecules = molecule.getDescendantsOrMolecule();
            for (CMLMolecule subMolecule : subMolecules) {
                subMolecule.createCartesiansFromFractionals(orthMat);
                List<Contact> subContactList = findMoleculeMoleculeContacts(
                    subMolecule, range3Fract, nonTranslateSymmetry, orthMat, dist2Range);
                for (Contact subContact : subContactList) {
                    contactList.add(subContact);
                }
            }
        }
        return contactList;
    }

    // VERY CRUDE METHOD to create a border round molecule
    private void expandRange(Real3Range range3Fract, CMLCrystal crystal, RealRange dist2Range) {
        double dist = Math.sqrt(dist2Range.getMax());
        double[] cellParam = null;
        try {
            cellParam = crystal.getCellParameterValues();
        } catch (CMLException e) {
            throw new CMLRuntimeException("BUG "+e);
        }
        double deltax = dist / cellParam[0];
        double deltay = dist / cellParam[1];
        double deltaz = dist / cellParam[2];
        RealRange xr = range3Fract.getXRange();
        xr.setRange(xr.getMin()-deltax, xr.getMax()+deltax);
        RealRange yr = range3Fract.getYRange();
        yr.setRange(yr.getMin()-deltay, yr.getMax()+deltay);
        RealRange zr = range3Fract.getZRange();
        zr.setRange(zr.getMin()-deltaz, zr.getMax()+deltaz);
        range3Fract.setRanges(xr, yr, zr);
    }

    private List<Contact> findMoleculeMoleculeContacts(
            CMLMolecule origMolecule, Real3Range range3Fract, 
            CMLSymmetry nonTranslateSymmetry, Transform3 orthMat,
            RealRange dist2Range) {
        boolean sameMolecule = true;
        double distMax = Math.sqrt(dist2Range.getMax());
        List<Contact> contactList = new ArrayList<Contact>();
        CMLElements<CMLTransform3> nonTranslationalOperators =
            nonTranslateSymmetry.getTransform3Elements();
        for (CMLTransform3 operator : nonTranslationalOperators) {
            // clone molecule
            CMLMolecule symMolecule = new CMLMolecule(origMolecule);
            // and transform it
            symMolecule.transformFractionalsAndCartesians(
                operator, orthMat);
            // 
            RealRange xr = range3Fract.getXRange();
            RealRange yr = range3Fract.getYRange();
            RealRange zr = range3Fract.getZRange();
            // iterate through atoms looking for contacts between cloned
            // molecule and orig
            List<CMLAtom> atomList = symMolecule.getAtoms();
            for (CMLAtom atom : atomList) {
                // move atom to outside bounding box for orig molecule
                Point3 xyzFract = atom.getXYZFract();
                Point3 maxPoint = translateOutsidePositiveBox(xyzFract, xr, yr, zr);
                Point3 minPoint = translateOutsideNegativeBox(xyzFract, xr, yr, zr);
                double[] shiftToMin = minPoint.subtract(xyzFract).getArray();
                double shiftToMinx = (int) Math.round(shiftToMin[0]);
                double shiftToMiny = (int) Math.round(shiftToMin[0]);
                double shiftToMinz = (int) Math.round(shiftToMin[0]);
                // then sweep bounding box for contacts
                double x0 = minPoint.getArray()[0];
                double x1 = maxPoint.getArray()[0]+Util.EPS;
                double y0 = minPoint.getArray()[1];
                double y1 = maxPoint.getArray()[1]+Util.EPS;
                double z0 = minPoint.getArray()[2];
                double z1 = maxPoint.getArray()[2]+Util.EPS;

                // create a point at the minimum outside the bounding box and translate it 
                // thought the bounding box
                double x = x0;
                for (double deltax = shiftToMinx; x < x1; deltax++, x += 1) {
                    double y = y0;
                    for (double deltay = shiftToMiny; y < y1; deltay++, y += 1) {
                        double z = z0;
                        for (double deltaz = shiftToMinz; z < z1; deltaz++, z += 1) {
                            Point3 atomXYZFract = new Point3(x, y, z);
                            Point3 atomXYZ3 = atomXYZFract.transform(orthMat);
                            Contact contact = checkDistances(
                                origMolecule, atomXYZ3, atomXYZFract,
                                distMax, atom, operator);
                            if (contact != null) {
                                contact.setSameMolecule(sameMolecule);
                                contactList.add(contact);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return contactList;
    }

    private Contact checkDistances(CMLMolecule origMolecule, 
        Point3 atomXYZ3, Point3 atomXYZFract,
        double distMax, CMLAtom atom,
        CMLTransform3 operator) {

        Contact contact = null;
        List<CMLAtom> origAtomList = origMolecule.getAtoms();
        // iterate through all atoms in orig molecule
        for (CMLAtom origAtom : origAtomList) {
            double d = origAtom.getXYZ3().getDistanceFromPoint(atomXYZ3);
            if (d < distMax) {
                ChemicalElement cElem = ChemicalElement.getChemicalElement(atom.getElementType()); 
                ChemicalElement cElemOrig = ChemicalElement.getChemicalElement(origAtom.getElementType()); 
                double maxBondLength = cElem.getCovalentRadius() + cElemOrig.getCovalentRadius() +
                    ChemicalElement.getBondingRadiusTolerance();
                if (maxBondLength > d) {
                    Vector3 translateVector = atomXYZFract.subtract(atom.getXYZFract());
                    translateVector.round();
                    Transform3 mergeOperator = new Transform3(operator.getEuclidTransform3());
                    mergeOperator.incrementTranslation(translateVector);
                    contact = new Contact(origAtom, atom, null, 
                        new CMLTransform3(mergeOperator), d);
                    break;
                }
            }
        }
        return contact;
    }

    /** uses the atoms in a contact to merge molecules.
     * the molecules containing the from- and to- atoms are found
     * with getMolecule() and the symmetry operator is applied to 
     * these. Then any overlapping atoms are removed.
     * 
     * @param mergedMolecule molecule which grows as new atoms are added
     * @param contact
     * @param serial number of contact (to generate unique id)
     * @param orthMat
     * @param addBonds if true add additional bonds
     * @return molecule a molecule composed of the merged molecules
     */
    public CMLMolecule mergeSymmetryMolecules(CMLMolecule mergedMolecule,
        Contact contact, int serial, Transform3 orthMat, boolean addBonds) {

        CMLMolecule fromMolecule = contact.fromAtom.getMolecule();
        CMLMolecule targetMolecule = getTargetMolecule(mergedMolecule, fromMolecule.getId());
        List<CMLAtom> targetAtomList = targetMolecule.getAtoms();
        CMLMolecule symmetryMolecule = new CMLMolecule(fromMolecule);
        symmetryMolecule.transformFractionalsAndCartesians(contact.transform3, orthMat);
        if (contact.isInSameMolecule) {
            List<CMLAtom> newAtomList = new ArrayList<CMLAtom>();
            List<CMLAtom> fromAtomList = fromMolecule.getAtoms();
            List<CMLAtom> symmetryAtomList = symmetryMolecule.getAtoms();
            for (int i = 0; i < fromAtomList.size(); i++) {
                CMLAtom fromAtom = fromAtomList.get(i);
                CMLAtom symmetryAtom = symmetryAtomList.get(i);
                double d = fromAtom.getDistanceTo(symmetryAtom);
                if (d < 0.005) {
                    // atoms overlap
                    continue;
                } else {
                    List<CMLAtom> targetAtomList1 = targetMolecule.getAtoms();
                    boolean duplicate = false;
                    for (CMLAtom targetAtom : targetAtomList1) {
                        d = targetAtom.getDistanceTo(symmetryAtom);
                        if (d < 0.00001) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        CMLAtom newAtom = new CMLAtom(symmetryAtom);
                        String newId = fromAtom.getId()+S_UNDER+serial;
                        newAtom.resetId(newId);
                        targetMolecule.addAtom(newAtom);
                        newAtomList.add(newAtom);
                        // add bonds?
                        if (addBonds) {
                            // bonds to old atoms
                            for (int j = 0; j < fromAtomList.size(); j++) {
                                CMLAtom targetAtom = targetAtomList.get(j);
                                if (CMLBond.areWithinBondingDistance(targetAtom, newAtom)) {
                                    CMLBond newBond = new CMLBond(targetAtom, newAtom);
                                    newBond.setId(targetAtom.getId()+Util.S_UNDER+newAtom.getId());
                                    targetMolecule.addBond(newBond);
                                }
                            }
                        }
                    }
                }
            }
            // any bonds to new atoms? (not very good solution)
            for (int i = 0; i < newAtomList.size(); i++) {
                CMLAtom atomi = newAtomList.get(i);
                for (int j = 0; j < newAtomList.size(); j++) {
                    if (i == j) continue;
                    CMLAtom atomj = newAtomList.get(j);
                    if (CMLBond.areWithinBondingDistance(atomi, atomj)) {
                        CMLBond newBond = new CMLBond(atomi, atomj);
                        newBond.setId(CMLBond.atomHash(atomi.getId(),atomj.getId()));
                        try {
                            targetMolecule.addBond(newBond);
                        } catch (CMLRuntimeException e) {
                            // non unique bond
//                            System.out.println("Duplicate "+newBond.getId());
                        }
                    }
                }
            }
        }
        return mergedMolecule;
    }

    // returns either mergedMolecule or any childMolecule with id = fromMoleculeId
    private CMLMolecule getTargetMolecule(CMLMolecule mergedMolecule, String fromMoleculeId) {
        CMLMolecule targetMolecule = mergedMolecule;
        CMLElements<CMLMolecule> mergedChildMolecules = mergedMolecule.getMoleculeElements();
        if (mergedChildMolecules.size() > 0) {
            for (CMLMolecule childMol : mergedChildMolecules) {
                if (childMol.getId().equals(fromMoleculeId)) {
                    targetMolecule = childMol;
                    break;
                }
            }
        }
        return targetMolecule;
    }

    private Point3 translateOutsidePositiveBox(Point3 xyzFract, 
            RealRange xr, RealRange yr, RealRange zr) {
        double x = xyzFract.getArray()[0];
        double y = xyzFract.getArray()[1];
        double z = xyzFract.getArray()[2];
        
        while (x < xr.getMax()) {
            x += 1.0;
        }
        while (y < yr.getMax()) {
            y += 1.0;
        }
        while (z < zr.getMax()) {
            z += 1.0;
        }
        return new Point3(x, y, z);
    }
    
    private Point3 translateOutsideNegativeBox(Point3 xyzFract, 
            RealRange xr, RealRange yr, RealRange zr) {
        double x = xyzFract.getArray()[0];
        double y = xyzFract.getArray()[1];
        double z = xyzFract.getArray()[2];
        
        while (x > xr.getMin()) {
            x -= 1.0;
        }
        while (y > yr.getMin()) {
            y -= 1.0;
        }
        while (z > zr.getMin()) {
            z -= 1.0;
        }
        return new Point3(x, y, z);
    }

    /** convenience routine to apply mergeSymmetryMolecules
     * 
     * @param mol
     * @param contactList
     * @param addBonds
     * @return new molecule
     */
    public CMLMolecule getMergedMolecule(CMLMolecule mol, List<Contact> contactList, boolean addBonds) {
        Transform3 orthMat = crystal.getOrthogonalizationTransform();
        CMLMolecule mergedMolecule = new CMLMolecule(mol);
        if (contactList.size() > 0) {
            for (int icontact = 0; icontact < contactList.size(); icontact++) {
                this.mergeSymmetryMolecules(mergedMolecule, 
                contactList.get(icontact), icontact+1, orthMat, addBonds);
            }
        } else {
            mergedMolecule = new CMLMolecule(mol);
        }
        return mergedMolecule;
    }
    
	/** this matches the given formulae and the actual atom counts.
	 * it is involved and complex as their are about 6 different situations
	 * and combinations. When these are finalized this routine should be
	 * modularized.
	 * adds charges if possible
     * @param cml
	 *
	 */
    public void processFormulaeAndZ2(CMLCml cml) {
    	
		// add spacegroup multiplicities as these affect the formula
		CMLElements<CMLSymmetry> symmetryElements = crystal.getSymmetryElements();
		calculateAndAddSpaceGroupMultiplicity(molecule, symmetryElements.get(0));
		
		try {
			molecule.calculateAndAddFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
		} catch (CMLRuntimeException e) {
			throw new CMLRuntimeException("Cannot generate chemical formula (?unknown element type): "+e);
		}

		CMLElements<CMLMolecule> childMolecules = molecule.getMoleculeElements();
		List<CMLFormula> childFormulaList = getChildFormulaList(childMolecules);
		createFormulaCountMap(childFormulaList);
		
		if (childMolecules.size() != formulaCountMap.size()) {
			System.out.println("Identical childTypes "+formulaCountMap.size()+" != "+childMolecules.size());
		}
		for (String concise : formulaCountMap.keySet()) {
			System.out.println("..mols.. "+concise+": "+formulaCountMap.get(concise).intValue());
		}
		CMLFormula sumFormula = getSumFormula(cml);
		CMLFormula moietyFormula = getMoietyFormula(cml);
		// analyze moiety formula
		CMLFormula publishedFormula = moietyFormula;
		
		List<CMLFormula> publishedFormulaList = createPublishedFormulaList(publishedFormula, sumFormula);

		boolean moietyMatchesMolecules = true;
		boolean publishedCompositionMatchesMolecules = true;
		if (publishedFormula != null) {
			System.out.println("PF "+publishedFormula.toFormulaString());
			for (CMLFormula f : publishedFormulaList) {
				System.out.println("..form.. "+f.getConcise()+S_LBRAK+f.getCount()+S_RBRAK);
			}
			if (publishedFormulaList.size() != formulaCountMap.size()) {
				moietyMatchesMolecules = false;
				System.out.println("Cannot match moiety and molecules");
			}
		}
		// formula units in cell
		int formulaUnitsInCell = crystal.getZ();
		// symmetry operators
		CMLSymmetry symmetry = crystal.getSymmetryElements().get(0);
		CMLElements<CMLTransform3> symmetryOperators = symmetry.getTransform3Elements();
		int operatorCount = symmetryOperators.size();
		double formulaUnitsPerOperator = 1.0;
		if (formulaUnitsInCell != operatorCount) {
			formulaUnitsPerOperator = (double) formulaUnitsInCell / (double) operatorCount;
			CMLScalar z2op = new CMLScalar(formulaUnitsPerOperator);
			z2op.setDictRef("cml:z2op");
			z2op.setTitle("ratio of Z to symmetry operators");
			molecule.appendChild(z2op);
		}
		System.out.println("Symmetry: FormUnits/Oper "+formulaUnitsPerOperator+" FormUnit/Cell "+formulaUnitsInCell+
				" NOper "+operatorCount+" ChildMols "+childMolecules.size());
//		boolean matchedFormula = false;
		// the logic of this is involved. best understood by reading through the
		// options. Note that it cannot yet detect all the possibilities. Thus
		// two independent identical molecules on symmetry operators may appear to
		// be the same as a single molecule without symmetry. This requires more work
		// reported Z == symmetry operator count
		// this requires that all child molecules (after splitting)
		// are described by the reported moiety.
//		String compositionMatch = "UNMATCHED COMPOSITION";
		String diff = checkDiff(childFormulaList, publishedFormulaList, formulaUnitsPerOperator);
		if (diff.equals(S_EMPTY)) {
//			compositionMatch = "MATCHED COMPOSITION";
		} else {
//			compositionMatch = "UNMATCHED COMPOSITION: atoms - formula = diff";
			moietyMatchesMolecules = false;
			publishedCompositionMatchesMolecules = false;
		}
		
		boolean formulaMoleculeCount = (publishedFormulaList.size() == formulaCountMap.size());
		String formulaCountMatch = S_EMPTY;
		String formulaMoleculeMatch = S_EMPTY;
//		boolean matchedFormula = false;
		
		try {
			if (!formulaMoleculeCount) {
				formulaCountMatch = "UNMATCHED FORMULA/MOLECULE COUNT "+publishedFormulaList.size()+"/"+formulaCountMap.size();
				formulaMoleculeMatch = formulaCountMatch;
			} else{
				formulaCountMatch = "MATCHED FORMULA/MOLECULE COUNT "+publishedFormulaList.size()+"/"+formulaCountMap.size();
				formulaMoleculeMatch = S_EMPTY;
				// no symmetry, possibly multiple molecules
				if (formulaUnitsInCell >= operatorCount) {
					if (formulaUnitsInCell % operatorCount == 0){
						for (CMLFormula formula : publishedFormulaList) {
							String formulaS = formula.getConciseNoCharge();
							double formulaCount = formula.getCount();
							Integer count = formulaCountMap.get(formulaS);
							count = (count == null) ? new Integer(0) : count;
							if (count != Math.round (formulaUnitsPerOperator * formula.getCount())) {
								formulaMoleculeMatch += formulaS+": found molecules : "+count+"; expected "+formulaUnitsPerOperator+" * "+formulaCount;
							}
						}
						if (!formulaMoleculeMatch.equals(S_EMPTY)) {
							formulaMoleculeMatch = "UNMATCHED FORMULA/MOLECULE COUNT: "+formulaMoleculeMatch;
							moietyMatchesMolecules = false;
						} else {
//							moietyMatchesMolecules = true;
						}
					} else {
						// irregular
						formulaMoleculeMatch = "UNMATCHED FORMULA/MOLECULE COUNT (Non-INTEGRAL): "+	((double) formulaUnitsInCell / (double) operatorCount);
						moietyMatchesMolecules = false;
					}
					// Z is less than operator counts. This means the molecule must have symmetry
				} else {
					// symmetry (not yet generated, so cannot match components)
					if (operatorCount % formulaUnitsInCell == 0){
//						crystalTool.applySymmetry();
						// iterate through all children of both formula and molecule
						formulaMoleculeMatch = "UNMATCHED (SYMMETRY NOT YET IMPLEMENTED)";
						for (CMLFormula formula : publishedFormulaList) {
							String formulaS = formula.getConciseNoCharge();
							double formulaCount = formula.getCount();
							Integer count = formulaCountMap.get(formulaS);
							count = (count == null) ? new Integer(0) : count;
							if (count != Math.round (formulaUnitsPerOperator * formulaCount)) {
								moietyMatchesMolecules = false;
								formulaMoleculeMatch += formulaS+": found molecules : "+count+"; expected "+formulaUnitsPerOperator+" * "+formulaCount;
							}
						}
					} else {
						formulaMoleculeMatch = "UNMATCHED FORMULA/MOLECULE COUNT (Non-INTEGRAL): "+	((double) formulaUnitsInCell / (double) operatorCount);
						moietyMatchesMolecules = false;
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw e;
		}
		// if matched add charges to molecules
		if (moietyMatchesMolecules) {
			for (CMLMolecule molecule : childMolecules) {
				CMLFormula molFormula = molecule.getFormulaElements().get(0);
				String molConcise = molFormula.getConcise();
				for (CMLFormula pubFormula : publishedFormulaList) {
					String publishedConcise = CMLFormula.removeChargeFromConcise(pubFormula.getConcise());
					if (publishedConcise.equals(molConcise)) {
						if (pubFormula.getFormalChargeAttribute() != null) {
							int formalCharge = pubFormula.getFormalCharge();
							molecule.setFormalCharge(formalCharge);
							molFormula.setFormalCharge(formalCharge);
							continue;
						}
					}
				}
			}
			CMLScalar scalar = new CMLScalar("MATCHED MOIETIES");
			scalar.setDictRef("cmlcif:matchedMoieties");
			cml.appendChild(scalar);
		}
		
		if (publishedCompositionMatchesMolecules) {
			CMLScalar scalar = new CMLScalar("MATCHED COMPOSITION");
			scalar.setDictRef("cmlcif:matchedComposition");
			cml.appendChild(scalar);
		}
		
		System.out.println("============= "+((moietyMatchesMolecules) ? "MATCHED" : "UNMATCHED")+ formulaMoleculeMatch+" =================");
	}

    /**
     * use spacegroup symmetry to add multiplicities. only works with fractional
     * coordinates
     * @param molecule
     * @param symmetry
     *            spacegroup operators
     */
    void calculateAndAddSpaceGroupMultiplicity(CMLMolecule molecule, CMLSymmetry symmetry) {
        List<CMLAtom> atoms = molecule.getAtoms();
        for (CMLAtom atom : atoms) {
            int m = atom.calculateSpaceGroupMultiplicity(symmetry);
            if (m > 1) {
                atom.setSpaceGroupMultiplicity(m);
            }
        }
    }
    
	
	private List<CMLFormula> getChildFormulaList(CMLElements<CMLMolecule> childMolecules) {
		List<CMLFormula> childFormulaList = new ArrayList<CMLFormula>();
		if (childMolecules.size() == 0) {
			CMLFormula formula1 = (CMLFormula) molecule.getChildCMLElement(CMLFormula.TAG, 0);
			childFormulaList.add(formula1);
		} else {
			for (CMLMolecule molecule : childMolecules) {
				CMLFormula formula1 = (CMLFormula) molecule.getChildCMLElement(CMLFormula.TAG, 0);
				childFormulaList.add(formula1);
			}
		}
		return childFormulaList;
	}
	
	private void createFormulaCountMap(List<CMLFormula> childFormulaList) {
		// child molecules
		for (CMLFormula childFormula : childFormulaList) {
			String concise = childFormula.getConcise();
			Integer count = this.formulaCountMap.get(concise);
			if (count == null) {
				count = new Integer(1);
			} else {
				count = new Integer(count.intValue()+1);
			}
			this.formulaCountMap.put(concise, count);
		}
	}
	
    private CMLFormula getMoietyFormula(CMLCml cml) {
		return getFormula("iucr:_chemical_formula_moiety", cml);
	}
	
    private CMLFormula getSumFormula(CMLCml cml) {
		return getFormula("iucr:_chemical_formula_sum", cml);
	}
	
    private CMLFormula getFormula(String dictRef, CMLCml cml) {
		CMLFormula formula = null;
		Nodes formulaElements = cml.query(".//cml:formula", X_CML);
		for (int i = 0; i < formulaElements.size(); i++) {
			CMLFormula formula0 = (CMLFormula) formulaElements.get(i);
			if (dictRef.equalsIgnoreCase(formula0.getDictRef())) {
				formula = formula0;
				break;
			}
		}
		return formula;
	}
    
    private List<CMLFormula> createPublishedFormulaList(CMLFormula publishedFormula, CMLFormula sumFormula) {
		List<CMLFormula> publishedFormulaList = new ArrayList<CMLFormula>();
		if (publishedFormula != null) {
			publishedFormulaList = new ArrayList<CMLFormula>();
			CMLElements<CMLFormula> formulaList = publishedFormula.getFormulaElements();
			if (formulaList.size() == 0) {
				publishedFormulaList.add(publishedFormula);
			} else {
				for (CMLFormula childFormula : formulaList) {
					publishedFormulaList.add(childFormula);
				}
			}
		} else {
			if (sumFormula != null) {
				publishedFormulaList.add(sumFormula);
			}
		}
		return publishedFormulaList;
	}
	
	private String checkDiff(List<CMLFormula> childFormulaList, List<CMLFormula> parentFormulaList, double z2ops) {
		String diff = "checkDiff";
		try {
			CMLFormula childAggregateFormula = new CMLFormula();
			for (CMLFormula formula : childFormulaList) {
				childAggregateFormula = childAggregateFormula.createAggregatedFormula(formula);
			}
			// scale by multiplicity
			childAggregateFormula.setCount(1.0 / z2ops);
			CMLFormula parentAggregateFormula = new CMLFormula();
			for (CMLFormula formula : parentFormulaList) {
				parentAggregateFormula = parentAggregateFormula.createAggregatedFormula(formula);
			}
			diff = childAggregateFormula.getDifference(parentAggregateFormula);
		} catch (Throwable e) {
			diff = "Cannot compare formula: "+e;
		}
		return diff.trim();
	}
    
    
//	//** count for formulae for molecules by count.
//	 *
//	 * @return formula count indexed by formula
//	 * /
    /*--
   private Map<String, Integer> getFormulaCountMap() {
		return formulaCountMap;
	}
    --*/

    /** analyses cif value for indeterminacy.
     * if value id null, "." or S_QUERY assumes it is not
     * determinate
     * @param value
     * @return true if value as above
     */
    public static boolean isIndeterminate(String value) {
        return (value == null ||
                value.equals(S_PERIOD) ||
                value.equals(S_QUERY));
    }

    /** get atom label.
     * @param atom with potential child label
     * @return value of _atom_site_label or null
     */
    public static String getCIFLabel(CMLAtom atom) {
        return getValue(atom, "*[contains(@dictRef, '"+ATOM_LABEL+"')]");
    }

    /** convenience method to get xQuery value.
     * @param element context for xQuery
     * @param xQuery string
     * @return value of first node if isDeterminate else or null
     */
    public static String getValue(CMLElement element, String xQuery) {
        Nodes nodes = element.query(xQuery, X_CML);
        String value = (nodes.size() == 0) ? null : nodes.get(0).getValue();
        return (isIndeterminate(value)) ? null : value;
    }

    /** disambiguouating code.
     * 
     * @param atom
     * @return comniation of id and child label
     */
    public static String getFullLabel(CMLAtom atom) {
        return (atom == null) ? null : 
            atom.getMolecule().getId()+S_COLON+atom.getId()+"/"+CrystalTool.getCIFLabel(atom);
    }
    
    /** gets occupancy.
     * if missing returns 1.0
     * @param atom
     * @return (default) occupancy
     */
    public static double getOccupancy(CMLAtom atom) {
        return (atom.getOccupancyAttribute() == null) ? 1.0 :
            atom.getOccupancy();
    }
    
    /** is occupancy unity.
     * @param atom
     * @return true if occupancy 1.0 or absent
     */
    public static boolean hasUnitOccupancy(CMLAtom atom) {
        return Math.abs(getOccupancy(atom) - 1.0) < OCCUPANCY_EPS;
    }
    
};