/**
 *    Copyright 2011 Peter Murray-Rust et. al.
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.base.CMLElement.Hybridization;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.euclid.Angle.Range;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;
/**
 * additional tools for atom. not fully developed
 * 
 * @author pmr
 * 
 */
public class AtomTool extends AbstractSVGTool {

    final static Logger LOG = Logger.getLogger(AtomTool.class.getName());
    static {
    	LOG.setLevel(Level.DEBUG);
    }
	public final static String RIGHT = "RIGHT__";
	public final double fontWidthFontSizeFactor = 0.8;
	
	private static final Transform2 ROT90 = new Transform2(new Angle(Math.PI/2.));
	
    private CMLAtom atom;
    private CMLMolecule molecule;
    private MoleculeTool moleculeTool;
	private MoleculeDisplay moleculeDisplay;
    private AtomDisplay atomDisplay;
    private List<CMLAtomSet> coordinationSphereList;
    private CMLAtomSet coordinationSphereSet;

    private double fontSize;
//	private String fontFamily;
//	private String fontStyle;
//	private String fontWeight;
	private double radiusFactor = 1.0; 

	/**
     * constructor
     * 
     * @param atom
     * @deprecated use getOrCreateTool
     */
    public AtomTool(CMLAtom atom) {
    	if (atom == null) {
    		throw new RuntimeException("null atom");
    	}
        this.atom = atom;
        this.atom.setTool(this);
        setDefaults();
        molecule = atom.getMolecule();
        if (molecule == null) {
            throw new RuntimeException("Atom must be in molecule: "+atom.getId());
        }
    }
    
	/** gets AtomTool associated with atom.
	 * if null creates one and sets it in atom
	 * @param atom
	 * @return tool
	 */
	public static AtomTool getOrCreateTool(CMLAtom atom) {
		AtomTool atomTool = (AtomTool) atom.getTool();
		if (atomTool == null) {
			atomTool = new AtomTool(atom);
			atom.setTool(atomTool);
		}
		return atomTool;
	}
	
	private void setDefaults() {
//		this.ensureAtomDisplay();
//		this.ensureMoleculeDisplay();
	}

	/**
	 * 
	 * @param reaction
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLAtom atom) {
		return (AbstractSVGTool) AtomTool.getOrCreateTool(atom);
	}

    /** sort list of atoms by atomic number.
     * TODO this is all incomplete.. 
     * @param atomList of atoms
     * @return sorted list
     */
     public static List<CMLAtom> sortListByAtomicNumber(List<CMLAtom> atomList) {
        List<CMLAtom> newAtomList = new ArrayList<CMLAtom>(atomList);
        List<CMLAtom> sortedList = new ArrayList<CMLAtom>();

        while (newAtomList.size() > 0) {
            int heaviestAtomicNum = -1;
            CMLAtom heaviestAtom = null;

            for (int i = 0; i < newAtomList.size(); i++) {
                CMLAtom atom = newAtomList.get(i);
                int atomicNumber = atom.getAtomicNumber();
                if (atomicNumber > heaviestAtomicNum) {
                    heaviestAtom = atom;
                    heaviestAtomicNum = atomicNumber;
                } else if (atomicNumber == heaviestAtomicNum) {
                    // we've got two
                    // atoms of the
                    // same
                    // weight... //
                    // heaviestAtom.compareWiv(atom);
                }
            }

            sortedList.add(heaviestAtom);
            newAtomList.remove(heaviestAtom);
        }

        return sortedList;
    }

     
    /**
     * lazy evaluation
     * @param depth 0 = atom itself, 1 = first coord sphere...
	 */
	private void ensureCoordinationSphereList(int depth) {
		if (coordinationSphereList == null) {
			coordinationSphereList = new ArrayList<CMLAtomSet>();
			CMLAtomSet atomSet = new CMLAtomSet();
			atomSet.addAtom(atom);
			coordinationSphereList.add(atomSet);
		}
		if (coordinationSphereList.size() < depth+1) {
			ensureCoordinationSphereList(depth-1);
			CMLAtomSet nextLigandSphereSet = new CMLAtomSet();
			coordinationSphereList.add(nextLigandSphereSet);
			CMLAtomSet outerAtomSet = coordinationSphereList.get(depth-1);
			List<CMLAtom> outerAtomList = outerAtomSet.getAtoms();
			CMLAtomSet innerAtomSet = (depth <= 1) ? null : coordinationSphereList.get(depth-1);
			for (CMLAtom rootAtom : outerAtomList) {
				List<CMLAtom> ligandList = rootAtom.getLigandAtoms();
				for (CMLAtom ligand : ligandList) {
					// atom already in outer sphere
					if (outerAtomSet.contains(ligand)) {
						continue;
					}
					// or in inner sphere?
					if (innerAtomSet != null && innerAtomSet.contains(ligand)) {
						continue;
					}
					// form next sphere
					nextLigandSphereSet.addAtom(ligand);
				}
				coordinationSphereSet = null;
			}
		}
	}

    /**
     * @param depth 0 = atom itself, 1 = first coord sphere...
	 * @return the coordinationSphereList
	 */
	public List<CMLAtomSet> getCoordinationSphereList(int depth) {
		ensureCoordinationSphereList(depth);
		return coordinationSphereList;
	}

	/**
	 * reset to null
	 */
	public void resetCoordinationSphereList() {
		this.coordinationSphereList = null;
	}

    /**
     * always recalculated
     * @param depth 0 = atom itself, 1 = first coord sphere...
	 * @return the coordinationSphereList
	 */
	public CMLAtomSet getCoordinationSphereSet(int depth) {
		ensureCoordinationSphereList(depth);
		// always recalculate - slower but safer
		coordinationSphereSet = new CMLAtomSet();
		for (int iSphere = 0; iSphere <= depth; iSphere++) {
			CMLAtomSet atomSet = coordinationSphereList.get(iSphere);
			coordinationSphereSet.addAtomSet(atomSet);
		}
		return coordinationSphereSet;
	}

	/** gets 3D coordinates in ordered list.
	 * 
	 * @param atomList
	 * @return
	 */
	public static Point3Vector getPoint3Vector(List<CMLAtom> atomList) {
		Point3Vector p3v = new Point3Vector();
		for (CMLAtom atom : atomList) {
			Point3 p3 = (atom == null) ? null : atom.getXYZ3();
			if (p3 == null) {
//				throw new RuntimeException("Missing 3D coordinates");
			}
			p3v.add(p3);
		}
		return p3v;
	}
	/**
     * Computes a ligand list and sorts the ligands in it by atomic number if
     * two ligands have the same atomic number their order is unchanged.
     * 
     * @return a sorted ligand list.
     */
    /*
     * //TODO this is all incomplete.. 
     */
     public List<CMLAtom> getSortedLigandList() {
         List<CMLAtom> sortedLigandList = new ArrayList<CMLAtom>();
       List<CMLAtom> ligandList = atom.getLigandAtoms();
 
        while (ligandList.size() > 0) {
            int heaviestAtomicNum = -1;
            CMLAtom heaviestAtom = null;

            for (int i = 0; i < ligandList.size(); i++) {
                CMLAtom atom = ligandList.get(i);
                int atomicNumber = atom.getAtomicNumber();
                if (atomicNumber > heaviestAtomicNum) {
                    heaviestAtom = atom;
                    heaviestAtomicNum = atomicNumber;
                }
            }

            sortedLigandList.add(heaviestAtom);
            ligandList.remove(heaviestAtom);
        }

        return sortedLigandList;
    }

     /** convenience method to get single atom ligand.
      * useful for R groups know to have single attachment
      * @return the single atom (null if zero or > 1 ligands
      */
    public CMLAtom getSingleLigand() {
         List<CMLAtom> ligands = atom.getLigandAtoms();
         return (ligands.size() != 1) ? null : ligands.get(0);
    }

    /** gets sphere of atoms - topological depth
     * 
     * @param depth
     * @return atom set
     */
    public CMLAtomSet getSproutedSet(int depth) {
    	CMLAtomSet atomSet = new CMLAtomSet();
    	for (int d = 0; d < depth; d++) {
    		//
    	}
    	return atomSet;
    }

	/**
	 * append to id creates new id, perhaps to disambiguate
	 *
	 * @param atom
	 * @param s
	 */
	public void appendToId(final String s) {
		String id = atom.getId();
		if ((id != null) && (id.length() > 0)) {
			atom.renameId(id + s);
		} else {
			atom.renameId(s);
		}
	}

     
    /**
     * Compares this atom to another atom using simple CIP prioritization, if
     * both atoms are the same it then looks at both atoms ligands and recurses
     * until a mismatch is found.
     * 
     * if thisAtom has higher priority return 1 if thisAtom and otherAtom have
     * equal priority return 0 if otherAtom has higher priority return -1
     * 
     * @param otherAtom
     *            CMLAtom to compare this atom with
     * @return the comparision
     */
    /*
     * //TODO this is all incomplete.. 
     */
     public int recursiveCompare(CMLAtom otherAtom) {
         AtomTool otherAtomTool = AtomTool.getOrCreateTool(otherAtom);
        int thisAtomicNumber = atom.getAtomicNumber();
        int otherAtomicNumber = otherAtom.getAtomicNumber();
        if (thisAtomicNumber < otherAtomicNumber) {
            return -1;
        } else if (thisAtomicNumber > otherAtomicNumber) {
            return 1;
        } else {
            // okay, so both atoms are the
            // same - lets compare their ligands (step along tree once..)
            List<CMLAtom> thisAtomsLigands = this.getSortedLigandList();
            List<CMLAtom> otherAtomsLigands = otherAtomTool.getSortedLigandList();

            int length = Math.min(thisAtomsLigands.size(), otherAtomsLigands
                    .size());
            for (int i = 0; i < length; i++) {
                int compareResult = AtomTool.getOrCreateTool(thisAtomsLigands.get(i)).recursiveCompare(
                        otherAtomsLigands.get(i));
                if (compareResult != 0) { // we've found a mismatch here,
                                            // return the result
                    return compareResult;
                }
            }

            return 0;
            // still no mismatch, so far identical atoms. 
        }
    }

    // ---------------------to be looked at:-----------------------------------------
    // these functions seem to deal mostly with chirality (& associated)
    // there also appears to be a lot of duplicate functionality
    /**
     * add calculated coordinates for hydrogens.
     * 
     * @param control 2D or 3D
     */
    public void addCalculatedCoordinatesForHydrogens(CoordinateType type, double bondLength) {
    	if (CoordinateType.TWOD.equals(type)) {
    		calculateAndAddHydrogenCoordinates(bondLength);
    	} else if (CoordinateType.CARTESIAN.equals(type)) {
    		throw new RuntimeException("CARTESIAN H coords nyi");
    	} else {
    		throw new RuntimeException("THREED H coords nyi");
    	}
    }
    
    public void calculateAndAddHydrogenCoordinates(double bondLength) {
    	List<CMLAtom> ligandHydrogenList = atom.getLigandHydrogenAtoms();
    	List<CMLAtom> ligandList = atom.getLigandAtoms();
    	List<CMLAtom> nonHydrogenLigandHydrogenList = new ArrayList<CMLAtom>();
    	for (CMLAtom ligand : ligandList) {
    		if (!AS.H.equals(ligand.getElementType())) {
    			nonHydrogenLigandHydrogenList.add(ligand);
    		}
    	}
    	int hydrogenCount = atom.getHydrogenCount();
    	if (hydrogenCount != ligandHydrogenList.size()) {
    		atom.debug("HC "+hydrogenCount+" "+ligandHydrogenList.size());
    		throw new RuntimeException("inconsistent hydrogen count in add coordinates for atom "+atom.getId());
    	}
    	List<Vector2> vectorList = new ArrayList<Vector2>();
    	try {
    		vectorList = addCoords(nonHydrogenLigandHydrogenList, ligandHydrogenList, bondLength);
    	} catch (Exception e) {
    		LOG.error("Cannot add Hydrogen ", e);
    	}
    	if (vectorList.size() == 0) {
    	} else if (vectorList.size() != ligandHydrogenList.size()) {
    		LOG.error("vectorList ("+vectorList.size()+") != ligandHydrogenList ("+ligandHydrogenList.size()+")");
    	} else {
	    	Real2 xy2 = atom.getXY2();
	    	for (int i = 0; i < ligandHydrogenList.size(); i++) {
	    		ligandHydrogenList.get(i).setXY2(xy2.plus(vectorList.get(i)));
	    	}
    	}
	}
    
	private static Transform2 PI120 = new Transform2(new Angle(Math.PI * 2./3.));
	private static Transform2 PI90 = new Transform2(new Angle(Math.PI * 0.5));
	private static Transform2 PI270 = new Transform2(new Angle(Math.PI * 1.5));
	
    private List<Vector2> addCoords(List<CMLAtom> ligandList, List<CMLAtom> hydrogenList, double bondLength) {
    	List<Vector2> vectorList = new ArrayList<Vector2>();
    	if (hydrogenList.size() == 0) {
    		// nothing to do
    	} else if (ligandList.size() == 0) {
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(0, bondLength));
    		} else if (hydrogenList.size() == 2) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(0, -bondLength));
    		} else if (hydrogenList.size() == 3) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(bondLength * Math.sqrt(0.75), -bondLength *0.5));
    			vectorList.add(new Vector2(-bondLength * Math.sqrt(0.75), -bondLength *0.5));
    		} else if (hydrogenList.size() == 4) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(0, -bondLength));
    			vectorList.add(new Vector2(bondLength, 0));
    			vectorList.add(new Vector2(-bondLength, 0));
    		}

    	} else if (ligandList.size() == 1) {
    		Vector2 ligandVector = new Vector2(ligandList.get(0).getXY2().subtract(atom.getXY2()));
    		ligandVector = new Vector2(ligandVector.getUnitVector().multiplyBy(-bondLength));
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(ligandVector));
    		} else if (hydrogenList.size() == 2) {
    			Vector2 vector = new Vector2(ligandVector.multiplyBy(-1.0));
    			vector.transformBy(PI120);
    			vectorList.add(new Vector2(vector));
    			vector.transformBy(PI120);
    			vectorList.add(new Vector2(vector));
    		} else if (hydrogenList.size() == 3) {
    			Vector2 vector = new Vector2(ligandVector);
    			vectorList.add(new Vector2(vector));
    			vector.transformBy(PI90);
    			vectorList.add(new Vector2(vector));
    			vector = new Vector2(ligandVector);
    			vector.transformBy(PI270);
    			vectorList.add(new Vector2(vector));
    		} else {
    		}
    	} else if (ligandList.size() == 2) {
    		Vector2 ligandVector0 = new Vector2(ligandList.get(0).getXY2().subtract(atom.getXY2()));
    		ligandVector0 = new Vector2(ligandVector0.getUnitVector());
    		Vector2 ligandVector1 = new Vector2(ligandList.get(1).getXY2().subtract(atom.getXY2()));
    		ligandVector1 = new Vector2(ligandVector1.getUnitVector());
    		Angle angle = ligandVector0.getAngleMadeWith(ligandVector1);
    		angle.setRange(Angle.Range.SIGNED);
			Vector2 bisectVector = null;
    		boolean nearlyLinear = Math.abs(angle.getRadian()) > 0.9 * Math.PI;
        	if (nearlyLinear) {
    			bisectVector = new Vector2(ligandVector0.getUnitVector());
    			bisectVector.transformBy(ROT90);
    			bisectVector.multiplyBy(bondLength);
    		} else {
    			bisectVector = new Vector2(ligandVector0.plus(ligandVector1));
    			bisectVector = new Vector2(bisectVector.getUnitVector());
    			bisectVector = new Vector2(bisectVector.multiplyBy(-bondLength));
    		}
			if (hydrogenList.size() == 1) {
    			Vector2 vector = new Vector2(bisectVector);
    			vector = new Vector2(vector.multiplyBy(1.0));
    			vectorList.add(vector);
    		} else if (hydrogenList.size() == 2) {
        		if (nearlyLinear) {
        			vectorList.add(new Vector2(bisectVector));
        			vectorList.add(new Vector2(bisectVector.multiplyBy(-1.)));
        		} else {
        			Angle halfAngle = new Angle(Math.PI*0.5 - Math.abs(angle.getRadian()*0.5));
        			Transform2 t2 = new Transform2(halfAngle);
        			Vector2 vector = new Vector2(bisectVector);
        			vector.transformBy(t2);
	    			vectorList.add(vector);
        			t2 = new Transform2(halfAngle.multiplyBy(-1.0));
        			vector = new Vector2(bisectVector);
        			vector.transformBy(t2);
	    			vectorList.add(vector);
        		}
    		} else {
    		}
    	} else if (ligandList.size() == 3) {
    		Vector2[] vectors = new Vector2[3];
    		Vector2 bisectVector = null;
    		for (int i = 0; i < 3; i++) {
	    		vectors[i] = new Vector2(ligandList.get(i).getXY2().subtract(atom.getXY2()));
	    		bisectVector = (bisectVector == null) ? vectors[i] : new Vector2(bisectVector.plus(vectors[i]));
    		}
    		bisectVector = new Vector2(bisectVector.multiplyBy(-1.0));
    		// short vector
    		try {
    			bisectVector = new Vector2(bisectVector.getUnitVector().multiplyBy(vectors[0].getLength()*0.7));
    			// must not overlap too badly
    			for (int i = 0; i < 3; i++) {
    				Angle angle = bisectVector.getAngleMadeWith(vectors[i]);
    				angle.setRange(Range.SIGNED);
    				double angleR = Math.abs(angle.getRadian());;
    				if (angleR < 0.2) {
    					bisectVector = new Vector2(vectors[(i+1) % 3]);
    					bisectVector = new Vector2(bisectVector.multiplyBy(-1.0));
    					break;
    				}
    			}
    		} catch (EuclidRuntimeException e) {
				bisectVector = vectors[0];
				bisectVector = new Vector2(bisectVector);
    		}
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(bisectVector));
    		} else {
    		}
    	} else {
    		// skip
    	}
    	return vectorList;

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
	public void adjustHydrogenCountsToValency(HydrogenControl control) {
		if (atom.getHydrogenCountAttribute() == null) {
			AtomTool atomTool = AtomTool.getOrCreateTool(atom);
			int group = atomTool.getHydrogenValencyGroup();
			// these states cannot have hydrogen
			if (group == -1) {
				return;
			} else if (group == -2) {
				return;
			}
			// hydrogen and metals
			if (group < 4) {
				return;
			}
			int sumBo = atomTool.getSumNonHydrogenBondOrder();
			int fc = (atom.getFormalChargeAttribute() == null) ? 0 :
				atom.getFormalCharge();
			int nh = 8 - group - sumBo + fc;
			// non-octet species
			if (group == 4 && fc == 1) {
				nh -= 2;
			}
			// negative counts are meaningless
			if (nh < 0) {
				nh = 0;
			}
			atom.setHydrogenCount(nh);
		}
		this.expandImplicitHydrogens(control);
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
	public void expandImplicitHydrogens(HydrogenControl control) throws RuntimeException {
		if (HydrogenControl.USE_HYDROGEN_COUNT.equals(control)) {
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
		if (HydrogenControl.NO_EXPLICIT_HYDROGENS.equals(control) && currentHCount != 0) {
			return;
		}
		String id = atom.getId();
		for (int i = 0; i < hydrogenCount - currentHCount; i++) {
			CMLAtom hatom = new CMLAtom(id + "_h" + (i + 1));
			molecule.addAtom(hatom);
			hatom.setElementType(AS.H.value);
			CMLBond bond = new CMLBond(atom, hatom);
			molecule.addBond(bond);
			bond.setOrder(CMLBond.SINGLE_S);
		}
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
	public int getSumNonHydrogenBondOrder() throws RuntimeException {
		float sumBo = 0.0f;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		for (int i = 0; i < ligandList.size(); i++) {
			CMLAtom ligand = ligandList.get(i);
			if (AS.H.equals(ligand.getElementType())) {
				continue;
			}
			CMLBond bond = ligandBondList.get(i);
			String bo = bond.getOrder();
			if (bo != null) {
				if (CMLBond.isSingle(bo)) {
					sumBo += 1.0;
				}
				if (CMLBond.isDouble(bo)) {
					sumBo += 2.0;
				}
				if (CMLBond.isTriple(bo)) {
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

    /** gets lone electrons on atom.
     * electrons not involved in bonding
     * assumes accurate hydrogen count
     * only currently really works for first row (C, N, O, F)
     * calculated as  getHydrogenValencyGroup() -
     *   (getSumNonHydrogenBondOrder() + getHydrogenCount()) - atom.getFormalCharge()
     *   @throws RuntimeException
     * @return number of lone electrons (< 0 means cannot calculate)
     */
     public int getLoneElectronCount() {
         AtomTool atomTool = AtomTool.getOrCreateTool(atom);
         int group = atomTool.getHydrogenValencyGroup();
         if (group == -1) {
             return -1;
         }
         int sumNonHBo = atomTool.getSumNonHydrogenBondOrder();
         int nHyd = atom.getHydrogenCount();
         int loneElectronCount = group - (sumNonHBo + nHyd) - atom.getFormalCharge();
         return loneElectronCount;
     }

     static String[] elems  = {AS.H.value, AS.C.value, AS.N.value, AS.O.value, AS.F.value, AS.Si.value, AS.P.value, AS.S.value, AS.Cl.value, AS.Br.value, AS.I.value};
     static int[]    group  = { 1,   4,   5,   6,   7,   4,    5,   6,   7,    7,    7};
     static int[]    eneg0  = { 0,   0,   0,   0,   1,   0,    0,   1,   1,    1,    1};
     static int[]    eneg1  = { 0,   0,   0,   1,   1,   0,    0,   0,   1,    1,    1};
     /** a simple lookup for common atoms.
     *
     * examples are C, N, O, F, Si, P, S, Cl, Br, I
     * if atom has electronegative ligands, (O, F, Cl...) returns -1
     *
     */
     public int getHydrogenValencyGroup() {
         int elNum = -1;
         try {
             String elType = atom.getElementType();
             elNum = getElemNumb(elType);
             if (elNum == -1) {
                 return -1;
             }
             if (eneg0[elNum] == 0) {
                 return group[elNum];
             }
             List<CMLAtom> ligands = atom.getLigandAtoms();
     // if atom is susceptible to enegative ligands, exit if they are present
             for (CMLAtom ligand : ligands) {
                 int ligElNum = getElemNumb(ligand.getElementType());
                 if (ligElNum == -1 || eneg1[ligElNum] == 1) {
                     return -2;
                 }
             }
         } catch (Exception e) {
             LOG.error("BUG "+e);
         }
         int g = (elNum == -1) ? -1 : group[elNum];
         return g;
     }

     private int getElemNumb(String elemType) {
         for (int i = 0; i < elems.length; i++) {
             if (elems[i].equals(elemType)) {
                 return i;
             }
         }
         return -1;
     }

 	/**
 	 * Contracts the hydrogens on an atom.
 	 * actually deletes them
 	 *
 	 * @param moleculeTool TODO
 	 * @param control
 	 */
 	public void contractExplicitHydrogens(HydrogenControl control) {
 		int hCount = (atom.getHydrogenCountAttribute() == null) ? 0 : atom.getHydrogenCount();
 		Set<ChemicalElement> hSet = ChemicalElement.getElementSet(
 				new String[] { AS.H.value });
 		List<CMLAtom> ligands = CMLAtom.filter(atom.getLigandAtoms(), hSet);
 		CMLMolecule molecule = atom.getMolecule();
 		for (CMLAtom ligand : ligands) {
 			molecule.deleteAtom(ligand);
 		}
 		atom.setHydrogenCount(Math.max(hCount, ligands.size()));
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
	 * @param moleculeTool TODO
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
	public CMLAtomSet calculate3DCoordinatesForLigands(AtomGeometry geometry, double length, double angle) throws RuntimeException {
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
		if (geometry.equals(AtomGeometry.DEFAULT)) {
			geometry = AtomGeometry.getGeometry(atom.getLigandAtoms().size());
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
				geometry.getIntValue(), length);
		} else if (nWithCoords == 1) {
			// ligand on A
			CMLAtom bAtom = (CMLAtom) coordAtoms.get(0);
			// does B have a ligand (other than A)
			CMLAtom jAtom = null;
			List<CMLAtom> bLigandList = bAtom.getLigandAtoms();
			for (CMLAtom bLigand : bLigandList) {
				if (!bLigand.equals(moleculeTool)) {
					jAtom = bLigand;
					break;
				}
			}
			newPoints = Molutils.calculate3DCoordinates1(thisPoint, bAtom
					.getXYZ3(), (jAtom != null) ? jAtom.getXYZ3() : null,
							geometry.getIntValue(), length, angle);
		} else if (nWithCoords == 2) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			newPoints = Molutils.calculate3DCoordinates2(thisPoint, bPoint,
					cPoint, geometry.getIntValue(), length, angle);
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

	@Deprecated //("use AtomGeometry")
	public CMLAtomSet calculate3DCoordinatesForLigands(int geometryInt, double length, double angle) {
		AtomGeometry atomGeometry = AtomGeometry.getGeometry(geometryInt);
		return calculate3DCoordinatesForLigands(atomGeometry, length, angle);
	}
	/**
	 * gets all atoms downstream of a bond.
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
	 * @param atomSet to accumulate ligand atoms
	 * @param otherAtom not to be visited
	 *
	 */
	public void getDownstreamAtoms(CMLAtomSet atomSet,
			CMLAtom otherAtom, boolean forceUpdate, CMLAtomSet stopSet) {
		atomSet.addAtom(atom, forceUpdate);
		LOG.trace("============="+atom.getId()+"===="+otherAtom.getId());
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			// do not revisit atoms
			if (false) {
			} else if (stopSet != null && stopSet.contains(ligandAtom)) {
				LOG.trace("STOP "+ligandAtom.getId());
			} else if (ligandAtom.equals(otherAtom)) {
				LOG.trace("PARENT "+ligandAtom.getId());
			} else if (atomSet.contains(ligandAtom)) {
				LOG.trace("ALREADY "+ligandAtom.getId());
				// do not backtrack
			} else {
				LOG.trace("RECURSE "+ligandAtom.getId());
				AtomTool ligandTool = AtomTool.getOrCreateTool(ligandAtom);
				ligandTool.getDownstreamAtoms(atomSet, atom, forceUpdate, stopSet);
			}
		}
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
	 * @param atomSet to accumulate ligand atoms
	 * @param otherAtom not to be visited
	 *
	 */
	public void getDownstreamAtoms(CMLAtomSet atomSet,
			CMLAtom otherAtom, boolean forceUpdate) {
		getDownstreamAtoms(atomSet, otherAtom, forceUpdate, null);
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
	 *  @param moleculeTool TODO
	 * @param otherAtom not to be visited
	 * @return the atomSet (empty if none)
	 *
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom otherAtom, CMLAtomSet stopSet) {
		CMLAtomSet atomSet = new CMLAtomSet();
		boolean forceUpdate = false;
		AtomTool.getOrCreateTool(atom).getDownstreamAtoms(atomSet, otherAtom, forceUpdate, stopSet);
		atomSet.updateContent();
		return atomSet;
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
	 *  calls getDownstreamAtoms(otherAtom, allowSpiro=true)
	 *
	 * @param moleculeTool TODO
	 * @param otherAtom not to be visited
	 * @return the atomSet (empty if none)
	 *
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom otherAtom) {
		return getDownstreamAtoms(otherAtom, null);
	}
     
     /** finds atom with lowest lexical id.
      * 
      * @param atomList
      * @return atom
      */
    public static CMLAtom getAtomWithLowestId(List<CMLAtom> atomList) {
        String[] ids = new String[atomList.size()];
        int i = 0;
        for (CMLAtom atom : atomList) {
            ids[i++] = atom.getId();
        }
        Arrays.sort(ids);
        CMLAtom lowestAtom = null;
        for (CMLAtom atom : atomList) {
            if (atom.getId().equals(ids[0])) {
                lowestAtom = atom;
                break;
            }
        }
         return lowestAtom;
    }

     /** translates an atom with single ligand to the covalent radius.
      * in a bond (say C-R) where R has only one ligand will translate
      * R along the bond vector to be at a distance CR where CR
      * is covalent radious of the other atom (in this case C) 
      *
      */
    public void translateToCovalentRadius() {
         Point3 groupPoint = atom.getPoint3(CoordinateType.CARTESIAN);
         if (groupPoint == null) {
        	 throw new RuntimeException("atom has no coordinates: "+atom.getId());
         }
         CMLAtom rAtom = this.getSingleLigand();
         if (rAtom == null) {
             throw new RuntimeException("Expected 1 ligand for: "+atom.getId());
         }
         Point3 atomPoint = rAtom.getPoint3(CoordinateType.CARTESIAN);
         if (atomPoint == null) {
        	 throw new RuntimeException("atom has no coordinates: "+rAtom.getId());
         }
         Vector3 vector = groupPoint.subtract(atomPoint);
         vector = vector.normalize();
         String rElement = rAtom.getElementType();
         double covRad = 0.3;
         if (rElement == null) {
             covRad = 0.3;
         } else if (rElement.equals(AS.R)) {
             
         } else {
             ChemicalElement element = ChemicalElement.getChemicalElement(rElement);
             covRad = element.getCovalentRadius();
             if (covRad < 0.1) {
                 covRad = 1.0;
             }
         }
         vector = vector.multiplyBy(covRad);
         Point3 newGroupPoint = atomPoint.plus(vector);
         atom.setXYZ3(newGroupPoint);
     }

     
     /** returns a "g" element
      * this contains the text for symbol and charge
      * @param drawable
      * @return null if no symbol or charge
      */
     public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	g = null;
    	ensureMoleculeDisplay();
    	ensureAtomDisplay();
    	atomDisplay.ensureMoleculeDisplay(moleculeDisplay);
		if (false) {
		} else if (hasNo2DCoords()) {
		} else if (hideHydrogens()) {
		} else if (hideGroupRoot()) {
    	} else {
	    	 drawAtom(drawable);
    	 }
    	 return (g == null || g.getChildElements().size() == 0) ? null : g;
     }

	private void drawAtom(CMLDrawable drawable) {
		double x = atom.getX2();
		 double y = atom.getY2();
		 g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
		 g.setUserElement(atom);
		 g.addAttribute(new Attribute("class", "atom"));
		 g.addAttribute(new Attribute("id", "g"+S_UNDER+atom.getId()));
		 g.setTransform(new Transform2(
			 new double[]{
			 1., 0., x,
			 0.,-1., y,
			 0., 0., 1.
		 }));
		 String fill = getAtomFill(atom.getElementType());
		 TextDisplay elementDisplay = atomDisplay.getElementDisplay();
		 TextDisplay chargeDisplay = atomDisplay.getChargeDisplay();
		 TextDisplay groupDisplay = atomDisplay.getGroupDisplay();
		 TextDisplay idDisplay = atomDisplay.getIdDisplay();
		 TextDisplay isotopeDisplay = atomDisplay.getIsotopeDisplay();
		 TextDisplay labelDisplay = atomDisplay.getLabelDisplay();

		 String atomString = getAtomString();
 // always draw atom
		 elementDisplay.setFontSize(11.0);

//		 elementDisplay.setFontSize(atomDisplay.getFontSize());
		 elementDisplay.setFill(fill);
		 elementDisplay.setUserElement(atom);
		 elementDisplay.displayElement(g, atomString);
		 if (atomString.equals(S_EMPTY)) {
			 elementDisplay.setOpacity(0.0);
		 }
		 if (atom.getFormalChargeAttribute() != null) {
			 chargeDisplay.displaySignedInteger(g, atom.getFormalCharge());
		 }
		 if (atom.getIsotopeNumberAttribute() != null) {
			 isotopeDisplay.display(g, ""+atom.getIsotopeNumber());
		 }
		 if (atomDisplay.isDisplayLabels()) {
			 labelDisplay.displayLabel(g, getLabel());
		 }
		 if (atomDisplay.isDisplayIds()) {
			 idDisplay.displayId(g, atom.getId());
		 }
		 if (atomDisplay.isDisplayGroups()) {
			 groupDisplay.displayGroup(g, getGroup());
		 }
	}

	private boolean hasNo2DCoords() {
		return atom.getX2Attribute() == null || atom.getY2Attribute() == null;
	}

	private boolean hideHydrogens() {
		return atomDisplay != null &&
				atomDisplay.isOmitHydrogens() &&
				atom.hasElement("H");
	}

	private boolean hideGroupRoot() {
		return atomDisplay != null &&
				!atomDisplay.isDisplay() &&
				!this.isGroupRoot();
	}

  	private void ensureMoleculeDisplay() {
  		ensureMoleculeTool();
		moleculeDisplay = (moleculeTool == null) ? null : moleculeTool.getMoleculeDisplay();
	}

 	public void ensureMoleculeTool() {
 		if (moleculeTool == null) {
 			moleculeTool = MoleculeTool.getOrCreateTool(atom.getMolecule());
 		}
	}

	private CMLLabel getGroup() {
 		Nodes nodes = atom.query(".//cml:label", CMLConstants.CML_XPATH);
 		return (nodes.size() == 0) ? null : (CMLLabel) nodes.get(0);
 	}
 	
	public void ensureAtomDisplay() {
		if (atomDisplay == null) {
			if (moleculeDisplay == null) {
				CMLMolecule molecule = atom.getMolecule();
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				if (moleculeTool != null) {
					moleculeDisplay = moleculeTool.getMoleculeDisplay();
				}
			}
			if (moleculeDisplay != null) {
				AtomDisplay atomDisplayx = moleculeDisplay.getDefaultAtomDisplay();
				if (atomDisplayx != null) {
					atomDisplay = new AtomDisplay(atomDisplayx);
				}
			}
		}
	}
	
	public void setDisplay(boolean display) {
		ensureAtomDisplay();
		if (atomDisplay != null) {
//			new Exception().printStackTrace();
//			LOG.debug("ATOM DISPLAY "+display+" "+atom.getId());
			atomDisplay.setDisplay(display);
		}
	}

//	private void displayAtomText(String fill, double xOffsetFactor,
//			double yOffsetFactor, String atomString) {
//		double width = 0.0;
//		if (atomString.startsWith(RIGHT)) {
//			atomString = atomString.substring(RIGHT.length());
//			width = (atomString.length()-1)*fontSize*fontWidthFontSizeFactor;
//		}
//		SVGText text = new SVGText(
//				 new Real2(xOffsetFactor*fontSize - width, yOffsetFactor*fontSize), atomString);
//		 fill = getAtomFill(atomString);
//		 drawBackgroundCircle();
//		 text.setFill(fill);
//		 text.setFontSize(fontSize);
//		 text.setFontFamily(fontFamily);
//		 text.setFontStyle(fontStyle);
//		 text.setFontWeight(fontWeight);
//		 
//		 g.appendChild(text);
//	}

//	// get rid of this some time
//	private void drawBackgroundCircle() {
//		double rad = radiusFactor*fontSize;
//		LOG.debug("RAD "+rad);
//		SVGCircle circle = new SVGCircle(new Real2(0., 0.), rad);
//		circle.setStroke("none");
//		 // should be background
//		String circleFill = /*AS.C.equals(elementType) ? "none" :*/ "white";
//		g.appendChild(circle);
//		circle.setFill(circleFill);
//	}

	private static String getAtomFill(String atomString) {
		String fill = null;
		if (false) {
		 } else if(AS.C.equals(atomString)) {
			 fill = "black";
		 } else if (AS.N.equals(atomString)) {
			 fill = "blue";
		 } else if (AS.O.equals(atomString)) {
			 fill = "red";
		 } else if (AS.S.equals(atomString)) {
			 fill = "orange";
		 } else if (AS.Cl.equals(atomString)) {
			 fill = "green";
		 } else if (AS.F.equals(atomString)) {
			 fill = "#77ff00";
		 } else if (AS.Br.equals(atomString)) {
			 fill = "#ff7700";
		 } else if (AS.I.equals(atomString)) {
			 fill = "#ff00ff";
		 } else if (AS.H.equals(atomString)) {
			 fill = "gray";
		 } else if(AS.R.equals(atomString)) {
			 fill = "brown";
		 }
		return fill;
	}
     
     private String getAtomString() {
    	 
    	 String s = atom.getElementType();
    	 // omit carbons?
    	 if (!atomDisplay.isDisplayCarbons() && AS.C.equals(s)) {
//    		 s = S_EMPTY;
    		 
    	 } else if (moleculeDisplay.isShowChildLabels() && AS.R.equals(s)) {
    		 // R-groups
        	 CMLLabel label = getLabel();
    		 if (label != null) {
    			 s = label.getCMLValue();
    			 if ("cml:abbrevRight".equals(label.getAttributeValue("convention"))) {
    				 s = RIGHT+s;
    			 }
    		 }
    	 } else if (isGroupRoot()) {
    		 // contractable group
    		 List<CMLAtomSet> atomSetList = AtomSetTool.getChildAtomSetList(atom);
    		 if (isGroupRoot(atomSetList)) {
//				 List<CMLAtom> atomList = atomSetList.get(0).getAtoms();
    		 }
        	 CMLLabel label = getLabel();
			 s = label.getCMLValue();
    	 } else {
    		 
    	 }
    	 return s;
     }
     
     private boolean isGroupRoot(List<CMLAtomSet> atomSetList) {
		 return (atomSetList.size() == 1 && 
			Role.GROUP.toString().equals(atomSetList.get(0).getAttributeValue("role")));
		 
     }

     public boolean isGroupRoot() {
		 return isGroupRoot(AtomSetTool.getChildAtomSetList(this.getAtom()));
     }

	private CMLLabel getLabel() {
		CMLLabel label = null;
		CMLElements<CMLLabel> labels = atom.getLabelElements();
		 if (labels.size() == 1) {
			 label = labels.get(0);
		 }
		return label;
	}

	/**
//	 * @param xIdOffsetFactor
//	 * @param yIdOffsetFactor
//	 * @param idFontFactor
//	 * @param backgroundIdRadiusFactor
//	 */
//	private void drawId(double xIdOffsetFactor, double yIdOffsetFactor, double idFontFactor, double backgroundIdRadiusFactor) {
//		SVGCircle circle;
//		SVGText text;
//		String idS = atom.getId();
//		 double idFontSize = idFontFactor*fontSize;
//		 Real2 chargeXY = new Real2(xIdOffsetFactor*fontSize, yIdOffsetFactor*fontSize);
//		 circle = new SVGCircle(chargeXY, backgroundIdRadiusFactor*idFontSize);
//		 circle.setFill("white");
//		 circle.setStroke("black");
//		 circle.setStrokeWidth(0.05);
//		 // circle isn't centered properly yet
////		 g.appendChild(circle);
//		 Real2 idXYd = new Real2((xIdOffsetFactor-0.3)*(fontSize), (yIdOffsetFactor+0.3)*(fontSize));
//		 text = new SVGText(idXYd, idS);
//		 text.setFill("black");
//		 text.setStroke("black");
//		 text.setFontSize(idFontSize);
//		 g.appendChild(text);
//	}

//	/**
//	 * @param xChargeOffsetFactor
//	 * @param yChargeOffsetFactor
//	 * @param chargeFontFactor
//	 * @param backgroundChargeRadiusFactor
//	 */
//	private void drawCharge(double xChargeOffsetFactor, double yChargeOffsetFactor, double chargeFontFactor, double backgroundChargeRadiusFactor) {
//		SVGCircle circle;
//		SVGText text;
//		int formalCharge = atom.getFormalCharge();
//		 String chargeS = "";
//		 if (formalCharge == -1) {
//			 chargeS = "-";
//		 } else if (formalCharge == 1) {
//			 chargeS = "+";
//		 } else if (formalCharge > 1) {
//			 chargeS = "+"+formalCharge;
//		 } else if (formalCharge <  -1) {
//			 chargeS = ""+formalCharge;
//		 }
//		 // skip zero charge
//		 if (!chargeS.equals("")) {
//			 double chargeFontSize = chargeFontFactor*fontSize;
//			 Real2 chargeXY = new Real2(xChargeOffsetFactor*fontSize, yChargeOffsetFactor*fontSize);
//			 circle = new SVGCircle(chargeXY, backgroundChargeRadiusFactor*chargeFontSize);
//			 circle.setFill("white");
//			 circle.setStroke("black");
//			 circle.setStrokeWidth(0.05);
//			 g.appendChild(circle);
//			 Real2 chargeXYd = new Real2((xChargeOffsetFactor-0.3)*(fontSize), (yChargeOffsetFactor+0.3)*(fontSize));
//			 text = new SVGText(chargeXYd, chargeS);
//			 text.setFontSize(chargeFontSize);
//			 g.appendChild(text);
//		 }
//	}

	/**
	 * @return the atomDisplay
	 */
	public AtomDisplay getAtomDisplay() {
		return atomDisplay;
	}

	/**
	 * @param atomDisplay the atomDisplay to set
	 */
	public void setAtomDisplay(AtomDisplay atomDisplay) {
		this.atomDisplay = atomDisplay;
	}

	/**
	 * @return the moleculeTool
	 */
	public AbstractSVGTool getMoleculeTool() {
		return moleculeTool;
	}

	/**
	 * @param moleculeTool the moleculeTool to set
	 */
	public void setMoleculeTool(AbstractSVGTool moleculeTool) {
		this.moleculeTool = (MoleculeTool) moleculeTool;
	}

	/**
	 * @return the atom
	 */
	public CMLAtom getAtom() {
		return atom;
	}

	/**
	 * @return the coordinationSphereSet
	 */
	public CMLAtomSet getCoordinationSphereSet() {
		return coordinationSphereSet;
	}

	/**
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
	}

	/**
	 * @return the fontSize
	 */
	public double getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * @return the radiusFactor
	 */
	public double getRadiusFactor() {
		return radiusFactor;
	}

	/**
	 * @param radiusFactor the radiusFactor to set
	 */
	public void setRadiusFactor(double radiusFactor) {
		this.radiusFactor = radiusFactor;
	}

	/** gets list of atoms referenced by R group.
	 * scopeElement limits the search to its descendants
	 * current atom must have elementType='R' and
	 * CMLLabel child with a non-null value attribute. This value
	 * is used to match R groups in atoms under scopeElement
	 * 
	 * @param scopeElement
	 * @param label of atom in scope
	 * @return
	 */
	public List<CMLAtom> getReferencedAtoms(Element scopeElement, String label) {
		Nodes refAtomNodes = scopeElement.query(".//cml:atom[@elementType='R' and cml:label[@value='"+label+"']]", CMLConstants.CML_XPATH);
		List<CMLAtom> refAtomList = new ArrayList<CMLAtom>();
		for (int i = 0; i < refAtomNodes.size(); i++) {
			CMLAtom refAtom = (CMLAtom) refAtomNodes.get(i);
			if (!refAtom.equals(atom)) {
				refAtomList.add(refAtom);
			}
		}
		return refAtomList;
	}

	/**
	 * Gets the nonHydrogenLigandList attribute of the AtomImpl object
	 *
	 * @return The nonHydrogenLigandList value
	 */
	public List<CMLAtom> getNonHydrogenLigandList() {
		List<CMLAtom> newLigandList = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!AS.H.equals(ligand.getElementType())) {
				newLigandList.add(ligand);
			}
		}
		return newLigandList;
	}
	
	/**
	 * @return 
	 */
	public List<CMLBond> getNonHydrogenLigandBondList() {
		List<CMLBond> newLigandBondList = new ArrayList<CMLBond>();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		for (CMLBond ligandBond : ligandBondList) {
			CMLAtom other = ligandBond.getOtherAtom(atom);
			if (!AS.H.equals(other.getElementType())) {
				newLigandBondList.add(ligandBond);
			}
		}
		return newLigandBondList;
	}
	
	/**
	 * 
	 * @return hydrogen ligands
	 */
	public List<CMLAtom> getHydrogenLigandList() {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		List<CMLAtom> hatoms = new ArrayList<CMLAtom>();
		for (CMLAtom ligand : ligands) {
			if (AS.H.equals(ligand.getElementType())) {
				hatoms.add(ligand);
			}
		}
		return hatoms;
	}

	/**
	 * deletes a hydrogen from an atom.
	 *
	 * used for building up molecules. If there are implicit H atoms it reduces
	 * the hydrogenCount by 1. If H's are explicit it removes the first hydrogen
	 * ligand
	 *
	 * @param atom
	 * @exception RuntimeException
	 *                no hydrogen ligands on atom
	 */
	public void deleteHydrogen() {
		decrementHydrogenCountAttribute();
		Set<ChemicalElement> hSet = ChemicalElement.getElementSet(
				new String[] { AS.H.value });
		List<CMLAtom> hLigandVector = CMLAtom.filter(atom.getLigandAtoms(),
				hSet);
		if (hLigandVector.size() > 0) {
			molecule.deleteAtom(hLigandVector.get(0));
		}
	}

	private void decrementHydrogenCountAttribute() {
		if (atom.getHydrogenCountAttribute() != null) {
			if (atom.getHydrogenCount() > 0) {
				atom.setHydrogenCount(atom.getHydrogenCount() - 1);
			}
		}
	}

	private void incrementHydrogenCountAttribute() {
		if (atom.getHydrogenCountAttribute() != null) {
			if (atom.getHydrogenCount() > 0) {
				atom.setHydrogenCount(atom.getHydrogenCount() + 1);
			}
		}
	}

	/** adds hydrogen atom.
	 * if hydrogenCount is already present, increments it.
	 * id is created as atomId_hn where n first free integer 
	 * if n > 20 fails
	 */
	public CMLAtom addHydrogen() {
		CMLAtom hAtom = null;
		incrementHydrogenCountAttribute();
//		List<CMLAtom> hList = this.getHydrogenLigandList();
		// iterate through all hydrogens with similar name (not always ligands)
		for (int i = 1; i <= 20; i++) {
			String hId = atom.getId()+S_UNDER+"h"+i;
			hAtom = molecule.getAtomById(hId);
			if (hAtom == null) {
				hAtom = new CMLAtom(hId, AS.H);
				molecule.addAtom(hAtom);
				CMLBond bond = new CMLBond(atom, hAtom);
				bond.setOrder(CMLBond.SINGLE_S);
				molecule.addBond(bond);
				break;
			}
		}
		return hAtom;
	}

	/**
	 * gets lone electrons on atom. electrons not involved in bonding assumes
	 * accurate hydrogen count only currently really works for first row (C, N,
	 * O, F) calculated as getHydrogenValencyGroup() -
	 * (getSumNonHydrogenBondOrder() + getHydrogenCount()) -
	 * atom.getFormalCharge()
	 *
	 * @param atom TODO
	 * @return number of lone electrons (< 0 means cannot calculate)
	 */
	public int getLoneElectronCount(CMLAtom atom) {
		int loneElectronCount = -1;
		int group = getHydrogenValencyGroup();
		if (group == -1) {
			return -1;
		}
		int sumNonHBo = getSumNonHydrogenBondOrder();
		int nHyd = atom.getHydrogenCount();
		int formalCharge = 0;
		if (atom.getFormalChargeAttribute() != null) {
			formalCharge = atom.getFormalCharge();
		}
		loneElectronCount = group - (sumNonHBo + nHyd) - formalCharge;
		return loneElectronCount;
	}

	/**
	 * get the double bond equivalents.
	 *
	 * this is the number of double bonds the atom can make an sp2 atom has 1 an
	 * sp atom has 2
	 *
	 * @param atom TODO
	 * @param fcd
	 * @return the bond sum (0, 1, 2)
	 * @throws RuntimeException
	 *             if cannot get formal charges
	 */
	public int getDoubleBondEquivalents(FormalChargeControl fcd) {
		CMLMolecule molecule = atom.getMolecule();
		if (molecule == null) {
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
		int doubleBondEquivalents = maxBonds - this.getBondOrderSum();
		return doubleBondEquivalents;
	}

	/**
	 * gets list of ligands in 2D diagram in clockwise order.
	 *
	 * starting atom is arbitrary (makes smallest clockwise angle with xAxis).
	 * The 4 atoms can be compared to atomRefs4 given by author or other methods
	 * to see if they are of the same or alternative parity.
	 *
	 * use compareAtomRefs4(CMLAtom[] a, CMLAtom[] b) for comparison
	 *
	 * @param atom2 TODO
	 * @param atom4 the original list of 4 atoms
	 * @return ligands sorted into clockwise order
	 * @throws RuntimeException
	 */
	public CMLAtom[] getClockwiseLigands(CMLAtom[] atom4) throws RuntimeException {
		Vector2 vx = new Vector2(1.0, 0.0);
		Real2 thisxy = atom.getXY2();
		double[] angle = new double[4];
		Vector2 v = null;
		for (int i = 0; i < 4; i++) {
			try {
				v = new Vector2(atom4[i].getXY2().subtract(thisxy));
				// Angle class appears to be broken, hence the degrees
				angle[i] = vx.getAngleMadeWith(v).getDegrees();
			} catch (NullPointerException npe) {
				throw new RuntimeException(
						"Cannot compute clockwise ligands");
			}
			if (angle[i] < 0) {
				angle[i] += 360.;
			}
			if (angle[i] > 360.) {
				angle[i] -= 360.;
			}
		}
		// get atom4Refs sorted in cyclic order
		CMLAtom[] cyclicAtom4 = new CMLAtom[4];
		for (int i = 0; i < 4; i++) {
			double minAngle = Double.MAX_VALUE;
			int low = -1;
			for (int j = 0; j < 4; j++) {
				if (angle[j] >= 0 && angle[j] < minAngle) {
					low = j;
					minAngle = angle[j];
				}
			}
	
			if (low != -1) {
				cyclicAtom4[i] = atom4[low];
				angle[low] = -100.;
			} else {
				throw new RuntimeException(
						"Couldn't get AtomRefs4 sorted in cyclic order");
			}
		}
		// all 4 angles must be less than PI
		// the ligands in clockwise order
		for (int i = 0; i < 4; i++) {
			CMLAtom cyclicAtomNext = cyclicAtom4[(i < 3) ? i + 1 : 0];
			Real2 cyclicXy = cyclicAtom4[i].getXY2();
			Real2 cyclicXyNext = cyclicAtomNext.getXY2();
			v = new Vector2(cyclicXy.subtract(thisxy));
			Vector2 vNext = new Vector2(cyclicXyNext.subtract(thisxy));
			double ang = v.getAngleMadeWith(vNext).getDegrees();
			if (ang < 0) {
				ang += 360.;
			}
			if (ang > 360.) {
				ang -= 360.;
			}
			if (ang > 180.) {
				throw new RuntimeException("All 4 ligands on same side "
						+ atom.getId());
			}
		}
		return cyclicAtom4;
	}

	/** is this atom clode to another.
	 * 
	 * @param atom
	 * @param atom2 TODO
	 * @param atom1 TODO
	 * @return true if close
	 */
	public boolean hasCloseContact(CMLAtom atom1) {
		double valenceDist = atom.getChemicalElement().getCovalentRadius()+atom1.getChemicalElement().getVDWRadius();
		double dist = atom.getDistanceTo(atom1);
		if ((valenceDist/2) > dist) {
			return true;
		} else {
			return false;
		}
	}

//	/**
//	 * gets four atoms defining atomParity isomerism.
//	 * applies only to 4-coordinate atoms l1-X(l2)(l3)-l4
//	 * and possibly 3-coordinate atoms l1-X(l2)-l3
//	 * when central atom is added to list: l1-l2-l3-X
//	 *
//	 * @param atom
//	 * @return the four atoms or null
//	 */
//	private CMLAtom[] getAtomRefs4() throws RuntimeException {
//		CMLAtom[] atom4 = null;
//		List<CMLAtom> ligandList = atom.getLigandAtoms();
//		if (ligandList.size() < 3) {
//		} else {
//			atom4 = new CMLAtom[4];
//			atom4[0] = ligandList.get(0);
//			atom4[1] = ligandList.get(1);
//			atom4[2] = ligandList.get(2);
//			atom4[3] = (ligandList.size() == 3) ? atom : ligandList.get(3);
//		}
//		return atom4;
//	}

	public void createGroupLabelAndAtomSet() {
		if (false) {
		} else if ("C".equals(atom.getElementType())) {
			List<CMLAtom> hatoms = AtomTool.getOrCreateTool(atom).getHydrogenLigandList();
			if (hatoms.size() == 3) {
				addLabel(hatoms, "Me");
			} else if (hatoms.size() == 2) {
				addLabel(hatoms, "CH2");
			} else if (hatoms.size() == 1) {
				addLabel(hatoms, "CH");
			}
		} else {
			List<CMLAtom> hatoms = AtomTool.getOrCreateTool(atom).getHydrogenLigandList();
			String elementType = atom.getElementType();
			if (hatoms.size() == 3) {
				addLabel(hatoms, elementType+"H3");
			} else if (hatoms.size() == 2) {
				addLabel(hatoms, elementType+"H");
			} else if (hatoms.size() == 1) {
				addLabel(hatoms, elementType+"H");
			}
		}
	}
	
	public String createChargeString() {
		String ss = S_EMPTY;
		if (atom.getFormalChargeAttribute() != null) {
			int ch = atom.getFormalCharge();
			if (ch == 1) {
				ss = S_PLUS;
			} else if (ch == -1) {
				ss = S_MINUS;
			} else if (ch > 1) {
				ss = S_PLUS + ch;
			} else if (ch < -1) {
				ss = S_EMPTY + ch;
			}
		}
		return ss;
	}
	
	private void addLabel(List<CMLAtom> atoms, String text) {
		for (CMLAtom atom : atoms) {
			AtomTool.getOrCreateTool(atom).setDisplay(false);
		}
		CMLLabel label = new CMLLabel();
		label.setCMLValue(text);
		this.atom.addLabel(label);
		this.atom.setRole(Role.GROUP.toString());
	}

	/**
	 * get substituent ligands of one end of bond.
	 *
	 * gets all substituent atoms of atom (but not otherAtom in bond)
	 *
	 * @param bond
	 * @param atom at one end of bond
	 * @return the list of substituent atoms
	 */
	static List<CMLAtom> getSubstituentLigandList(CMLBond bond, CMLAtom atom) {
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
	 * a simple lookup for common atoms.
	 *
	 * examples are C, N, O, F, Si, P, S, Cl, Br, I if atom has electronegative
	 * ligands, (O, F, Cl...) returns -1
	 *
	 * @param atom
	 * @return group
	 */
	public static int getHydrogenValencyGroup(CMLAtom atom) {
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
			MoleculeTool.LOG.error("BUG " + e);
		}
		int g = (elNum == -1) ? -1 : group[elNum];
		return g;
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
	public Hybridization getGeometricHybridization() {
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
				MoleculeTool.LOG.error("BUG " + e);
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
				MoleculeTool.LOG.error("BUG " + e);
			}
		} else {
			geomHybridization = Hybridization.SP3;
		}
		return geomHybridization;
	}

	/**
	 * get sum of formal bondOrders. uses the actual ligands (i.e. implicit
	 * hydrogens are not used) aromatic bonds are counted as 1.5, 1,2,3 aromatic
	 * bonds add 1 double bond
	 *
	 * @param atom
	 * @return sum (-1 means cannot be certain)
	 */
	public int getBondOrderSum() {
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
			BondTool ligandBondTool = BondTool.getOrCreateTool(ligandBond);
			double numericOrder = ligandBondTool.getNumericOrder();
			if (order == null) {
			} else if (order.equals(CMLBond.AROMATIC)) {
				aromaticBondSum += 1;
			} else if (multipleBondSum >= 0.0){
				multipleBondSum += (int) Math.round(numericOrder);
			} else {
				MoleculeTool.LOG.info("Unknown bond order:" + order + S_COLON);
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
	 * gets cross product for 3 atoms in 3D.
	 *
	 * gets cross products of this->at1 X this->at2
	 *
	 * @param atom3 TODO
	 * @param atom1 first atom
	 * @param atom2 second atom
	 * @return the cross product (null if parameters are null; zero if atoms are
	 *         coincident or colinear)
	 */
	// should this really be a public function?
	Vector3 get3DCrossProduct(CMLAtom atom1, CMLAtom atom2) {
	    Vector3 cross = null;
	    Vector3 v1 = atom.getVector3(atom1);
	    Vector3 v2 = atom.getVector3(atom2);
	    cross = v1.cross(v2);
	    return cross;
	}

	/**
	 * gets cross product for 3 atoms in 2D.
	 *
	 * gets cross products of this->at1 X this->at2 the result is a 3D vector
	 * perpendicular to xy2 plane
	 *
	 * @param atom3 TODO
	 * @param atom1 first atom
	 * @param atom2 second atom
	 * @return the cross product (null if parameters are null; zero if atoms are
	 *         coincident or colinear)
	 */
	// should this really be a public function?
	Vector3 get2DCrossProduct(CMLAtom atom1, CMLAtom atom2) {
	    Vector3 cross = null;
	    if (atom1 != null && atom2 != null) {
	        Point3 p0 = atom.get2DPoint3();
	        Point3 p1 = atom1.get2DPoint3();
	        Point3 p2 = atom2.get2DPoint3();
	        if (p1 != null && p2 != null) {
	            Vector3 v1 = p1.subtract(p0);
	            Vector3 v2 = p2.subtract(p0);
	            cross = v1.cross(v2);
	        }
	    }
	    return cross;
	}
	
    /**
     * transform 3D coordinates. does NOT alter fractional or 2D coordinates
     *
     * @param transform
     *            the transformation
     */
    public void transformCartesians(CMLTransform3 transform) {
        Point3 point = atom.getXYZ3();
        point = point.transform(transform.getEuclidTransform3());
        atom.setXYZ3(point);
    }

    /**
     * transform fractional and 3D coordinates. does NOT alter 2D coordinates
     * transforms fractionals then applies orthogonalisation to result
     * @param transform
     *            the fractional symmetry transformation
     * @param orthTransform
     *            orthogonalisation transform
     */
    public void transformFractionalsAndCartesians(CMLTransform3 transform, Transform3 orthTransform) {
        Point3 point = atom.getXYZFract();
        point = point.transform(transform.getEuclidTransform3());
        atom.setXYZFract(point);
        point = point.transform(orthTransform);
        atom.setXYZ3(point);
    }

    /**
     * transform 3D fractional coordinates. modifies this does not modify x3,
     * y3, z3 (may need to re-generate cartesians)
     *
     * @param transform
     *            the transformation
     */
    public void transformFractionals(CMLTransform3 transform) {
        Point3 point = atom.getXYZFract();
        point = point.transform(transform.getEuclidTransform3());
        atom.setXYZFract(point);
    }

    /**
     * calculate the spaceGroup multiplicity of the atom. this is defined by
     * attribute spaceGroupMultiplicity and is the number of symmetry operators
     * that transform the atom onto itself with normalization of cell
     * translations.
     *
     * @param symmetry
     *            spaceGroup operators
     * @return the multiplicity (0 if no coordinates else 1 or more)
     */
    public int calculateSpaceGroupMultiplicity(CMLSymmetry symmetry) {
        int multiplicity = 0;
        if (symmetry != null && atom.hasCoordinates(CoordinateType.FRACTIONAL)) {
            Point3 xyz = atom.getXYZFract();
            multiplicity = symmetry.getSpaceGroupMultiplicity(xyz);
        }
        return multiplicity;
    }


    /** assume atom has correct count of hydrogens
     * overwrite all existing coordinates
     */
	public void addCalculated3DCoordinatesForExistingHydrogens() {
		Double length = 1.6; // default for unusual atoms
		if (ChemicalElement.AS.C.equals(atom.getElementType())) {
			length = 1.08;
		} else if (ChemicalElement.AS.N.equals(atom.getElementType())) {
			length = 1.03;
		} else if (ChemicalElement.AS.O.equals(atom.getElementType())) {
			length = 0.96;
		}
		if (length != null) {
			this.addCalculated3DCoordinatesForExistingHydrogens(length);
		}
	}

	private final static double TWOPI3  = Math.PI*2.0/3.0;
	private final static double COS2PI3 = Math.cos(TWOPI3);
	private final static double SIN2PI3 = Math.sin(TWOPI3);
	private final static double ROOT3   = Math.sqrt(3.0);
	private final static double TETANG  = 2*Math.atan(Math.sqrt(2.0));
	private final static double TETANG0  = Math.PI - TETANG;
	private final static double TWOPI30  = Math.PI-TWOPI3;

    /** assume atom has correct count of hydrogens
     * overwrite all existing coordinates
     */
	public void addCalculated3DCoordinatesForExistingHydrogens(double length) {
		List<CMLAtom> nonHydrogenLigandList = this.getNonHydrogenLigandList();
		List<CMLAtom> hydrogenLigandList = this.getHydrogenLigandList();
		int nonhCount = nonHydrogenLigandList.size();
		int hCount = hydrogenLigandList.size();
		int coordNumber = nonhCount + hCount;
		List<Vector3> vector3List = new ArrayList<Vector3>();
		if (nonhCount == 0) {
			vector3List = addCoords0(nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 1) {
			addCoords1(nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 2) {
			addCoords2(nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 3) {
			addCoords3(nonHydrogenLigandList, hydrogenLigandList, length);
		} else {
			// cannot add hydrogens
		}
		addCoords(vector3List, hydrogenLigandList);
	}

	private List<Vector3> addCoords0(List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		Vector3 vector0 = new Vector3(0.0, 0.0, length);
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			vector3List.add(vector0);
		} else if (hydrogenLigandList.size() == 2) {
			vector3List.add(vector0);
			vector3List.add(new Vector3(0.0, 0.0, -length));
		} else if (hydrogenLigandList.size() == 3) {
			vector3List.add(vector0);
			vector3List.add(new Vector3(0.0, length*COS2PI3, -length*SIN2PI3));
			vector3List.add(new Vector3(0.0, length*COS2PI3, length*SIN2PI3));
		} else if (hydrogenLigandList.size() == 4) {
			vector3List.add(new Vector3(length/ROOT3, length/ROOT3, length/ROOT3));
			vector3List.add(new Vector3(-length/ROOT3, length/ROOT3, -length/ROOT3));
			vector3List.add(new Vector3(length/ROOT3, -length/ROOT3, -length/ROOT3));
			vector3List.add(new Vector3(-length/ROOT3, -length/ROOT3, length/ROOT3));
		}
		return vector3List;
	}
	
	private List<Vector3> addCoords1(List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		CMLAtomSet atomSet = null;
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.LINEAR, length,  TWOPI30);
		} else if (hydrogenLigandList.size() == 2) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.TRIGONAL, length, TWOPI30);
		} else if (hydrogenLigandList.size() == 3) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.TETRAHEDRAL, length, TETANG0);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atomSet);
		}
		return vector3List;
	}
	
	private List<Vector3> addCoords2(List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		CMLAtomSet atomSet = null;
		List<Vector3> vector3List = new ArrayList<Vector3>();
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.TRIGONAL, length, TWOPI3);
		} else if (hydrogenLigandList.size() == 2) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.TETRAHEDRAL, length, 2*TETANG);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atomSet);
		}
		return vector3List;
	}
	
	private List<Vector3> addCoords3(List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		CMLAtomSet atomSet = null;
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			atomSet = this.calculate3DCoordinatesForLigands(AtomGeometry.TETRAHEDRAL, length, TETANG0);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atomSet);
		}
		return vector3List;
	}
	
	private List<Vector3> getVectorList(CMLAtomSet atomSet) {
		List<Vector3> vectorList = new ArrayList<Vector3>();
		for (CMLAtom atom1 : atomSet.getAtoms()) {
			Point3 xyz31 = atom1.getXYZ3();
			Vector3 vector3 = xyz31.subtract(this.atom.getXYZ3());
			vectorList.add(vector3);
		}
		return vectorList;
	}

	private void addCoords(List<Vector3> vector3List, List<CMLAtom> hydrogenLigandList) {
		Point3 atomxyz3 = atom.getXYZ3();
		for (int i = 0; i < vector3List.size(); i++) {
			Point3 xyz3 = atomxyz3.plus(vector3List.get(i));
			hydrogenLigandList.get(i).setXYZ3(xyz3);
		}
	}

}