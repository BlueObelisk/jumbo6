package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Attribute;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.euclid.Angle.Range;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;


/**
 * additional tools for atom. not fully developed
 * 
 * @author pmr
 * 
 */
public class AtomTool extends AbstractSVGTool {

	public final static String RIGHT = "RIGHT__";
	public final double fontWidthFontSizeFactor = 0.8;
	
	private static final Transform2 ROT90 = new Transform2(new Angle(Math.PI/2.));
	
    private CMLAtom atom;
    private CMLMolecule molecule;
    private AbstractTool moleculeTool;
    private Logger logger = Logger.getLogger(AtomTool.class.getName());
    private AtomDisplay atomDisplay;
    private List<CMLAtomSet> coordinationSphereList;
    private CMLAtomSet coordinationSphereSet;
	private double fontSize;
	private double radiusFactor;

	/**
     * constructor
     * 
     * @param atom
     * @deprecated use getOrCreateTool
     */
    public AtomTool(CMLAtom atom) {
        this.atom = atom;
        this.atom.setTool(this);
        molecule = atom.getMolecule();
        if (molecule == null) {
            throw new CMLRuntimeException("Atom must be in molecule");
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
//				throw new CMLRuntimeException("Missing 3D coordinates");
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
    	} else {
    		throw new CMLRuntimeException("THREED H coords nyi");
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
    		throw new CMLRuntimeException("inconsistent hydrogen count in add coordinates");
    	}
    	List<Vector2> vectorList = addCoords(nonHydrogenLigandHydrogenList, ligandHydrogenList, bondLength);
    	Real2 xy2 = atom.getXY2();
    	for (int i = 0; i < ligandHydrogenList.size(); i++) {
    		ligandHydrogenList.get(i).setXY2(xy2.plus(vectorList.get(i)));
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
	    		vectors[i] = new Vector2(vectors[i].getUnitVector());
	    		bisectVector = (bisectVector == null) ? vectors[i] : new Vector2(bisectVector.plus(vectors[i]));
    		}
    		bisectVector = new Vector2(bisectVector.multiplyBy(-1.0));
    		// short vector
    		try {
    			bisectVector = new Vector2(bisectVector.getUnitVector());
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

    /** gets lone electrons on atom.
     * electrons not involved in bonding
     * assumes accurate hydrogen count
     * only currently really works for first row (C, N, O, F)
     * calculated as  getHydrogenValencyGroup() -
     *   (getSumNonHydrogenBondOrder() + getHydrogenCount()) - atom.getFormalCharge()
     *   @throws CMLException
     * @return number of lone electrons (< 0 means cannot calculate)
     */
     public int getLoneElectronCount() throws CMLException {
         AtomTool atomTool = AtomTool.getOrCreateTool(atom);
         int group = atomTool.getHydrogenValencyGroup();
         if (group == -1) {
             return -1;
         }
         int sumNonHBo = MoleculeTool.getSumNonHydrogenBondOrder(molecule, atom);
         int nHyd = atom.getHydrogenCount();
         int loneElectronCount = group - (sumNonHBo + nHyd) - atom.getFormalCharge();
         return loneElectronCount;
     }

     static String[] elems  = {AS.H.value, AS.C.value, AS.N.value, AS.O.value, AS.F.value, AS.Si.value, AS.P.value, AS.S.value, AS.Cl.value, AS.Br.value, AS.I.value};
     static int[]    group  = { 1,   4,   5,   6,   7,   4,    5,   6,   7,    7,    7};
     static int[]    eneg0  = { 0,   0,   1,   0,   1,   0,    0,   1,   1,    1,    1};
     static int[]    eneg1  = { 0,   0,   0,   1,   1,   0,    0,   0,   1,    1,    1};
     /** a simple lookup for common atoms.
     *
     * examples are C, N, O, F, Si, P, S, Cl, Br, I
     * if atom has electronegative ligands, (O, F, Cl...) returns -1
     *
     */
     int getHydrogenValencyGroup() {
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
             logger.severe("BUG "+e);
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
//         System.out.println("LOWEST ATOM "+lowestAtom.getId());
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
        	 throw new CMLRuntimeException("atom has no coordinates: "+atom.getId());
         }
         CMLAtom rAtom = this.getSingleLigand();
         if (rAtom == null) {
             throw new CMLRuntimeException("Expected 1 ligand for: "+atom.getId());
         }
         Point3 atomPoint = rAtom.getPoint3(CoordinateType.CARTESIAN);
         if (atomPoint == null) {
        	 throw new CMLRuntimeException("atom has no coordinates: "+rAtom.getId());
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
//    	g = null;
		if (atom.getX2Attribute() == null || atom.getY2Attribute() == null) {
    		 System.err.println("No coordinates for "+atom.getId());
    	 } else {
	    	 double x = atom.getX2();
	    	 double y = atom.getY2();
	    	 g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
	    	 g.addAttribute(new Attribute("class", "atom"));
	    	 g.addAttribute(new Attribute("id", "g"+S_UNDER+atom.getId()));
	    	 g.setTransform(new Transform2(
	    			 new double[]{
	    			 1., 0., x,
	    			 0.,-1., y,
	    			 0., 0., 1.
	    	 }));
	    	 String fill = atomDisplay.getFill();
	    	 fontSize = atomDisplay.getScaledFontSize();
 			 double xOffsetFactor = atomDisplay.getXOffsetFactor();
	    	 double yOffsetFactor = atomDisplay.getYOffsetFactor();
	    	 radiusFactor = atomDisplay.getBackgroundRadiusFactor();
	    	 
 			 double xChargeOffsetFactor = atomDisplay.getXChargeOffsetFactor();
	    	 double yChargeOffsetFactor = atomDisplay.getYChargeOffsetFactor();
	    	 double chargeFontFactor = atomDisplay.getChargeFontFactor();
	    	 double backgroundChargeRadiusFactor = atomDisplay.getBackgroundChargeRadiusFactor();

 			 double xIdOffsetFactor = atomDisplay.getXIdOffsetFactor();
	    	 double yIdOffsetFactor = atomDisplay.getYIdOffsetFactor();
	    	 double idFontFactor = atomDisplay.getIdFontFactor();
	    	 double backgroundIdRadiusFactor = atomDisplay.getBackgroundIdRadiusFactor();

	    	 String atomString = getAtomString();
    		 if (!atomString.equals(S_EMPTY)) {
        		 displayAtomText(fill, xOffsetFactor, yOffsetFactor, atomString);
	    	 }
	    	 if (atom.getFormalChargeAttribute() != null) {
	    		 drawCharge(xChargeOffsetFactor, yChargeOffsetFactor, chargeFontFactor, backgroundChargeRadiusFactor);
	    	 }
	    	 if (atomDisplay.isDisplayLabels()) {
	    		 drawLabel(xIdOffsetFactor, yIdOffsetFactor, idFontFactor, backgroundIdRadiusFactor);
	    	 }
    	 }
    	 return (g == null || g.getChildElements().size() == 0) ? null : g;
     }

	private void displayAtomText(String fill, double xOffsetFactor,
			double yOffsetFactor, String atomString) {
		double width = 0.0;
		if (atomString.startsWith(RIGHT)) {
			atomString = atomString.substring(RIGHT.length());
			width = (atomString.length()-1)*fontSize*fontWidthFontSizeFactor;
		}
		SVGText text = new SVGText(
				 new Real2(xOffsetFactor*fontSize - width, yOffsetFactor*fontSize), atomString);
		 fill = getAtomFill(fill, atomString);
		 SVGCircle circle = new SVGCircle(new Real2(0., 0.), radiusFactor*fontSize);
		 circle.setStroke("none");
		 // should be background
		 String circleFill = /*AS.C.equals(elementType) ? "none" :*/ "white";
		 g.appendChild(circle);
		 circle.setFill(circleFill);
		 text.setFill(fill);
		 text.setFontSize(fontSize);
		 g.appendChild(text);
	}

	private String getAtomFill(String fill, String atomString) {
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
		 } else if(AS.R.equals(atom.getElementType())) {
			 fill = "brown";
		 }
		return fill;
	}
     
     private String getAtomString() {
    	 String s = atom.getElementType();
    	 // omit carbons?
    	 if (!atomDisplay.isDisplayCarbons() && AS.C.equals(s)) {
    		 s = S_EMPTY;
    	 } else if (atomDisplay.isShowChildLabels() && AS.R.equals(s)) {
    		 CMLElements<CMLLabel> labels = atom.getLabelElements();
    		 if (labels.size() == 1) {
    			 CMLLabel label = labels.get(0);
    			 s = label.getCMLValue();
    			 if ("cml:abbrevRight".equals(label.getAttributeValue("convention"))) {
    				 s = RIGHT+s;
    			 }
    		 }
    	 }
    	 return s;
     }

	/**
	 * @param xIdOffsetFactor
	 * @param yIdOffsetFactor
	 * @param idFontFactor
	 * @param backgroundIdRadiusFactor
	 */
	private void drawLabel(double xIdOffsetFactor, double yIdOffsetFactor, double idFontFactor, double backgroundIdRadiusFactor) {
		SVGCircle circle;
		SVGText text;
		String idS = atom.getId();
		 double idFontSize = idFontFactor*fontSize;
		 Real2 chargeXY = new Real2(xIdOffsetFactor*fontSize, yIdOffsetFactor*fontSize);
		 circle = new SVGCircle(chargeXY, backgroundIdRadiusFactor*idFontSize);
		 circle.setFill("white");
		 circle.setStroke("black");
		 circle.setStrokeWidth(0.05);
		 // circle isn't centered properly yet
//		 g.appendChild(circle);
		 Real2 idXYd = new Real2((xIdOffsetFactor-0.3)*(fontSize), (yIdOffsetFactor+0.3)*(fontSize));
		 text = new SVGText(idXYd, idS);
		 text.setFill("black");
		 text.setStroke("black");
		 text.setFontSize(idFontSize);
		 g.appendChild(text);
	}

	/**
	 * @param xChargeOffsetFactor
	 * @param yChargeOffsetFactor
	 * @param chargeFontFactor
	 * @param backgroundChargeRadiusFactor
	 */
	private void drawCharge(double xChargeOffsetFactor, double yChargeOffsetFactor, double chargeFontFactor, double backgroundChargeRadiusFactor) {
		SVGCircle circle;
		SVGText text;
		int formalCharge = atom.getFormalCharge();
		 String chargeS = "";
		 if (formalCharge == -1) {
			 chargeS = "-";
		 } else if (formalCharge == 1) {
			 chargeS = "+";
		 } else if (formalCharge > 1) {
			 chargeS = "+"+formalCharge;
		 } else if (formalCharge <  -1) {
			 chargeS = ""+formalCharge;
		 }
		 // skip zero charge
		 if (!chargeS.equals("")) {
			 double chargeFontSize = chargeFontFactor*fontSize;
			 Real2 chargeXY = new Real2(xChargeOffsetFactor*fontSize, yChargeOffsetFactor*fontSize);
			 circle = new SVGCircle(chargeXY, backgroundChargeRadiusFactor*chargeFontSize);
			 circle.setFill("white");
			 circle.setStroke("black");
			 circle.setStrokeWidth(0.05);
			 g.appendChild(circle);
			 Real2 chargeXYd = new Real2((xChargeOffsetFactor-0.3)*(fontSize), (yChargeOffsetFactor+0.3)*(fontSize));
			 text = new SVGText(chargeXYd, chargeS);
			 text.setFontSize(chargeFontSize);
			 g.appendChild(text);
		 }
	}

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
	public AbstractTool getMoleculeTool() {
		return moleculeTool;
	}

	/**
	 * @param moleculeTool the moleculeTool to set
	 */
	public void setMoleculeTool(AbstractTool moleculeTool) {
		this.moleculeTool = moleculeTool;
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

	
}