package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.xmlcml.euclid.EuclidConstants;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real3Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;

/**
 * tool for managing crystals
 *
 * @author pmr
 *
 */
public class CrystalTool extends AbstractTool implements EuclidConstants {
	final static Logger logger = Logger.getLogger(CrystalTool.class.getName());

	Map<String, Integer> formulaCountMap = new HashMap<String, Integer>();

	/** multiplicity of atom symmetry.
	 */
	public final static String DICT_MULTIPLICITY = C_A+"mult";

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

	CMLMolecule molecule;
	MoleculeTool moleculeTool = null;
	CMLCrystal crystal = null;
	CMLSymmetry symmetry = null;
	List<CMLScalar> cellParams = null;

	static final double SYMMETRY_CONTACT_TOLERANCE = 0.4;
	/** tolerance for comparing occupancies.
	 */
	public final static double OCCUPANCY_EPS = 0.005;
	public final static double FRACT_EPS = 0.005;
	public final static double HEXAGONAL_CELL_FRACT_EPS = 0.0001;

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
	public CMLMolecule calculateCrystallochemicalUnit(RealRange dist2Range) {
		List<Contact> contactList = moleculeTool.getSymmetryContacts(dist2Range, this);
		ConnectionTableTool ct = new ConnectionTableTool(molecule);
		ct.partitionIntoMolecules();
		CMLMolecule mergedMolecule = this.getMergedMolecule(
				molecule, contactList);
		ct.flattenMolecules();
		return mergedMolecule;
	}

	/** normalize fractionals.
	 */
	public void normalizeCrystallographically() {
		Transform3 t3 = crystal.getOrthogonalizationTransform();
		for (CMLAtom atom : molecule.getAtoms()) {
			Point3 p3 = atom.getXYZFract();
			p3 = p3.normaliseCrystallographically();
			atom.setXYZFract(p3);
			Point3 newP3 = p3.transform(t3);
			atom.setXYZ3(newP3);
			for (CMLAtom at : molecule.getAtoms()) {
				if (p3.equalsCrystallographically(at.getXYZFract())
						&& !at.getId().equals(atom.getId())) {
					atom.detach();
				}
			}
		}
	}

	/** populate edges and faces.
	 */

	public void addAtomsToAllCornersEdgesAndFaces() {		
		for (CMLAtom atom : molecule.getAtoms()) {
			Point3 p3 = atom.getXYZFract();
			double[] coordArray = p3.getArray();
			int zeroCount = 0;
			int count = 0;
			int nonInteger = -1;
			for (Double coord : coordArray) {
				//if (coord.equals(0.0)) {
				if (coord < FRACT_EPS || (1.0 - coord) < FRACT_EPS) {
					zeroCount++;
				} else {
					nonInteger = count;
				}
				count++;
			}
			if (zeroCount > 0) {
				List<Point3> p3List = new ArrayList<Point3>();
				if (zeroCount == 1) {
					//System.out.println("face: "+atom.getId());
					//System.out.println("              - "+atom.getXYZFract());
					// atom is on a face of the unit cell
					List<Double> dList = new ArrayList<Double>(3);
					for (Double coord : coordArray) {
						if (coord < FRACT_EPS) {
							dList.add(1.0+coord);
						} else if (1.0 - coord < FRACT_EPS) {
							double d = 1.0 - coord;
							dList.add(0.0-d);
						} else {
							dList.add(coord);
						}
					}
					Point3 newP3 = new Point3(new Point3(dList.get(0), dList.get(1), dList.get(2)));
					p3List.add(newP3);
					//System.out.println("              + "+newP3);
				} else if (zeroCount == 2) {
					//System.out.println("edge: "+atom.getId());
					//System.out.println("              - "+atom.getXYZFract());
					// atom is on an edge of the unit cell
					if (nonInteger == -1) throw new CMLRuntimeException("Should be one non-intger coordinate to reach this point.");
					double[] array = {0.0, 0.0,
							1.0, 0.0,
							1.0, 1.0,
							0.0, 1.0
					};
					for (int i = 0; i < 4; i++) {
						double firstCoord = array[(1+(i*2))-1];
						double secondCoord = array[(2+(i*2))-1];
						if (nonInteger == 0) {
							Point3 newP3 = new Point3(coordArray[0], getCoord(firstCoord, coordArray[1]), getCoord(secondCoord, coordArray[2]));
							p3List.add(newP3);
							//System.out.println("              + "+newP3);
						} else if (nonInteger == 1) {
							Point3 newP3 = new Point3(getCoord(firstCoord, coordArray[0]), coordArray[1], getCoord(secondCoord, coordArray[2]));
							p3List.add(newP3);
							//System.out.println("              + "+newP3);
						} else if (nonInteger == 2) {
							Point3 newP3 = new Point3(getCoord(firstCoord, coordArray[0]), getCoord(secondCoord, coordArray[1]), coordArray[2]);
							p3List.add(newP3);
							//System.out.println("              + "+newP3);
						}
					}
				} else if (zeroCount == 3) {
					//System.out.println("corner: "+atom.getId());
					//System.out.println("              - "+atom.getXYZFract());
					// atom is at a corner of the unit cell
					double[] array = {0.0, 0.0, 0.0,
							1.0, 0.0, 0.0,
							1.0, 1.0, 0.0,
							1.0, 0.0, 1.0,
							1.0, 1.0, 1.0,
							0.0, 1.0, 0.0,
							0.0, 1.0, 1.0,
							0.0, 0.0, 1.0
					};
					for (int i = 0; i < 8; i++) {
						p3List.add(new Point3(getCoord(array[(1+(i*3))-1], coordArray[0]), 
								getCoord(array[(2+(i*3))-1], coordArray[1]), getCoord(array[(3+(i*3))-1], coordArray[2])));
					}
				} else if (zeroCount > 3) {
					throw new CMLRuntimeException("Should never throw");
				}
				int serial = 1;
				Transform3 t = crystal.getOrthogonalizationTransform();
				for (Point3 point3 : p3List) {
					CMLAtom newAtom = new CMLAtom(atom);
					newAtom.setXYZFract(point3);
					Point3 cart = point3.transform(t);
					newAtom.setXYZ3(cart);
					boolean add = true;
					for (CMLAtom at : molecule.getAtoms()) {
						if (point3.isEqualTo(at.getXYZFract())) {
							add = false;
							break;
						}
					}
					if (add) { 
						String newId = atom.getId()+S_UNDER+serial+S_UNDER+serial;
						newAtom.resetId(newId);
						molecule.addAtom(newAtom);
						serial++;
					}
				}
			}
		}
	}

	private double getCoord(double first, double coord) {
		boolean nearZero = false;
		boolean nearOne = false;
		if (coord < FRACT_EPS) {
			nearZero = true;
		} else if (1.0 - coord < FRACT_EPS) {
			nearOne = true;
		}
		if (new Double(first).equals(0.0)) {
			if (nearOne) {
				coord = coord-1.0;
			}
		} else if (new Double(first).equals(1.0)) {
			if (nearZero) {
				coord = coord+1.0;
			}
		}
		return coord;
	}

	/**
	 *
	 * @return molecule containing completed unit cell
	 */
	public CMLMolecule addAllAtomsToUnitCell(boolean includeAllCornerEdgeAndFaceAtoms) {
		ConnectionTableTool ct = new ConnectionTableTool(molecule);
		ct.flattenMolecules();
		MoleculeTool mt = new MoleculeTool(molecule);
		// reset all atom fractional coordinates so that they fall inside the unit cell.
		this.normalizeCrystallographically();
		CMLElements<CMLTransform3> allSymmElements = symmetry.getTransform3Elements();
		Map<Point3, CMLAtom> newAtomMap = new HashMap<Point3, CMLAtom>();
		for (int i = 0; i < allSymmElements.size(); i++) {
			CMLMolecule symmetryMolecule = new CMLMolecule(molecule);
			CMLTransform3 transform = allSymmElements.get(i);

			// look at transform to see if is a hexagonal unit cell
			// if so then the transformations around elements of symmetry with fractional coordinates of 1/3 or 2/3
			// will not be exact, so we have to allow for this error in our calculations.
			boolean isHexagonalTransform = false;
			for (Double d : transform.getMatrixAsArray()) {
				if (d.equals(EuclidConstants.ONE_THIRD) || d.equals(EuclidConstants.TWO_THIRDS)) {
					isHexagonalTransform = true;
					break;
				}
			}

			for (CMLAtom atom : symmetryMolecule.getAtoms()) {
				Point3 originalP3 = atom.getXYZFract();
				atom.transformFractionalsAndCartesians(transform, crystal.getOrthogonalizationTransform());
				Point3 p3 = atom.getPoint3(CoordinateType.FRACTIONAL);
				p3 = p3.normaliseCrystallographically();
				if (isHexagonalTransform && 
						(originalP3.isOnNonExactHexagonalSymmetryElement() || originalP3.isOnUnitCellFaceEdgeOrCorner())) {
					double[] array = p3.getArray();
					int count = 0;
					boolean changed = false;
					for (Double d : array) {
						if (1-d < FRACT_EPS) {
							array[count] = 1.0;
							changed = true;
						} else if (d < FRACT_EPS) {
							array[count] = 0.0;
							changed = true;
						}
						count++;
					}
					if (changed) {
						try {
							p3 = new Point3(array);
							p3.normaliseCrystallographically();
						} catch (EuclidException e) {
							throw new CMLRuntimeException("Could not create Point3 with values: "+array.toString());
						}
					}
				}

				boolean inNMap = false;
				boolean inOMap = false;
				for (Iterator it = newAtomMap.keySet().iterator(); it.hasNext(); ) {
					Point3 key = (Point3)it.next();
					if (key.equalsCrystallographically(p3)) {
						inNMap = true;
						break;
					}
				}
				if (!inNMap) {
					for (CMLAtom at : molecule.getAtoms()) {
						Point3 key = at.getXYZFract();
						if (key.equalsCrystallographically(p3)) {
							inOMap = true;
							break;
						}
					}
					if (!inOMap) {
						atom.setXYZFract(p3);
						newAtomMap.put(p3, atom);
					}
				}
			}
		}
		int count = 1;
		for (CMLAtom atom : newAtomMap.values()) {
			CMLAtom newAtom = new CMLAtom(atom);
			boolean add = true;
			for (CMLAtom at : molecule.getAtoms()) {
				if (newAtom.getXYZFract().equals(at.getXYZFract())) {
					add = false;
					break;
				}
			}
			if (add) {
				String newId = atom.getId()+S_UNDER+count;
				newAtom.resetId(newId);
				molecule.addAtom(newAtom);
				count++;
			}
		}
		this.addAtomsToAllCornersEdgesAndFaces();
		if (!includeAllCornerEdgeAndFaceAtoms) {
			for (CMLAtom atom : molecule.getAtoms()) {
				Point3 fract3 = atom.getXYZFract();
				if (fract3 == null) {
					throw new RuntimeException("Each atom must have fractional coordinates.");
				}
				for (Double fract :	fract3.getArray()) {
					if (fract < 0.0 || fract.equals(1.0) || fract > 1.0) {
						atom.detach();
					}
				}
			}
		}

		mt.createCartesiansFromFractionals();
		mt.calculateBondedAtoms();	
		// detach all bonds to group 1 or 2 atoms
		for (CMLAtom atom : molecule.getAtoms()) {
			ChemicalElement ce = atom.getChemicalElement();
			if (ce.isChemicalElementType(Type.GROUP_A) || ce.isChemicalElementType(Type.GROUP_B)) {
				List<CMLBond> bondList = new ArrayList<CMLBond>();
				for (CMLBond bond : atom.getLigandBonds()) {
					bondList.add(bond);
				}
				for (CMLBond bond : bondList) {
					bond.detach();
				}
			}
		}
		for (CMLBond bond : molecule.getBonds()) {
			bond.setOrder(CMLBond.SINGLE);
		}
		return molecule;
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
			CMLSymmetry allSymmetry = symmetry;
			// used to just use non-translations.  This works better if the molecule is not a
			// inorganic or polymeric organometallic structure.  Use CrystalTool.addAllAtomsToUnitCell
			// if you have a structure that fits either of these latter examples.
			//CMLSymmetry nonTranslateSymmetry = symmetry;
			//CMLSymmetry nonTranslateSymmetry = symmetry.getNonTranslations();
			List<CMLMolecule> subMolecules = molecule.getDescendantsOrMolecule();
			List<Type> typeIgnoreList = new ArrayList<Type>();
			typeIgnoreList.add(Type.GROUP_A);
			for (CMLMolecule subMolecule : subMolecules) {
				subMolecule.createCartesiansFromFractionals(orthMat);
				List<Contact> subContactList = findMoleculeMoleculeContacts(
						subMolecule, range3Fract, allSymmetry, orthMat, dist2Range, typeIgnoreList);
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
			CMLSymmetry symmetry, Transform3 orthMat,
			RealRange dist2Range, List<Type> typeIgnoreList) {
		boolean sameMolecule = true;
		double distMax = Math.sqrt(dist2Range.getMax());
		List<Contact> contactList = new ArrayList<Contact>();
		CMLElements<CMLTransform3> symmetryOperators =
			symmetry.getTransform3Elements();
		for (CMLTransform3 operator : symmetryOperators) {
			//if (operator.isIdentity()) continue;
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
							boolean skipContact = false;
							if (contact != null) {
								// check that the contact doesn't contain an atom type
								// that we want to ignore
								for (Type type : typeIgnoreList) {
									if (contact.fromAtom.getChemicalElement().isChemicalElementType(type)) skipContact = true;
									if (contact.toAtom.getChemicalElement().isChemicalElementType(type)) skipContact = true;
								}
								if (!skipContact) {
									contact.setSameMolecule(sameMolecule);
									contactList.add(contact);
									break;
								}
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
			if (d < distMax && d > SYMMETRY_CONTACT_TOLERANCE) {
				if (CMLBond.areWithinBondingDistance(atom, origAtom)) {
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
	 * @return molecule a molecule composed of the merged molecules
	 */
	public CMLMolecule mergeSymmetryMolecules(CMLMolecule mergedMolecule,
			Contact contact, int serial, Transform3 orthMat) {
		CMLMolecule fromMolecule = contact.fromAtom.getMolecule();
		CMLMolecule targetMolecule = getTargetMolecule(mergedMolecule, fromMolecule.getId());
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
				if (d < SYMMETRY_CONTACT_TOLERANCE) {
					// atoms overlap
					continue;
				} else {
					List<CMLAtom> targetAtomList1 = targetMolecule.getAtoms();
					boolean duplicate = false;
					for (CMLAtom targetAtom : targetAtomList1) {
						d = targetAtom.getDistanceTo(symmetryAtom);
						if (d < SYMMETRY_CONTACT_TOLERANCE) {
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
	 * @return new molecule
	 */
	public CMLMolecule getMergedMolecule(CMLMolecule mol, List<Contact> contactList) {
		Transform3 orthMat = crystal.getOrthogonalizationTransform();
		CMLMolecule mergedMolecule = new CMLMolecule(mol);
		if (contactList.size() > 0) {
			for (int icontact = 0; icontact < contactList.size(); icontact++) {
				this.mergeSymmetryMolecules(mergedMolecule,
						contactList.get(icontact), icontact+1, orthMat);
			}
		} else {
			mergedMolecule = new CMLMolecule(mol);
		}
		ConnectionTableTool ct = new ConnectionTableTool(mergedMolecule);
		ct.flattenMolecules();
		new MoleculeTool(mergedMolecule).calculateBondedAtoms();
		ct.partitionIntoMolecules();
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
			z2op.setDictRef(CMLCrystal.Z2OP);
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
		Nodes formulaElements = cml.query(".//"+CMLFormula.NS, X_CML);
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
//	*
//	* @return formula count indexed by formula
//	* /
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