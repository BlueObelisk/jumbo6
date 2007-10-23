package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Attribute;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;


/**
 * additional tools for atom. not fully developed
 * 
 * @author pmr
 * 
 */
public class AtomTool extends AbstractTool {

    private CMLAtom atom;
    private CMLMolecule molecule;
    private AbstractTool moleculeTool;
    private Logger logger = Logger.getLogger(AtomTool.class.getName());
    private AtomDisplay atomDisplay;
    private List<CMLAtomSet> coordinationSphereList;
    private CMLAtomSet coordinationSphereSet;
	private SVGG g;
	private double fontSize;
	private double radiusFactor;

	/**
     * constructor
     * 
     * @param atom
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
	public static AtomTool getOrCreateAtomTool(CMLAtom atom) {
		AtomTool atomTool = (AtomTool) atom.getTool();
		if (atomTool == null) {
			atomTool = new AtomTool(atom);
			atom.setTool(atomTool);
		}
		return atomTool;
	}


	/**
     * make atom tool from a atom.
     * 
     * @param atom
     * @return the tool
     */
    static AtomTool createAtomTool(CMLAtom atom) {
        return new AtomTool(atom);
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
         AtomTool otherAtomTool = new AtomTool(otherAtom);
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
                int compareResult = new AtomTool(thisAtomsLigands.get(i)).recursiveCompare(
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
     * @deprecated NOT YET IMPLEMENTED
     * @param control
     *            2D or 3D
     */
    public void addCalculatedCoordinatesForHydrogens(
            CMLMolecule.HydrogenControl control) {
        /** NYI */
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
         AtomTool atomTool = new AtomTool(atom);
         int group = atomTool.getHydrogenValencyGroup();
         if (group == -1) {
             return -1;
         }
         int sumNonHBo = MoleculeTool.getSumNonHydrogenBondOrder(molecule, atom);
         int nHyd = atom.getHydrogenCount();
         int loneElectronCount = group - (sumNonHBo + nHyd) - atom.getFormalCharge();
         return loneElectronCount;
     }

     static String[] elems  = {"H", "C", "N", "O", "F", "Si", "P", "S", "Cl", "Br", "I"};
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
         } else if (rElement.equals("R")) {
             
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
	    	 g = drawable.createGraphicsElement();
	    	 g.addAttribute(new Attribute("class", "atom"));
	    	 g.addAttribute(new Attribute("id", "g"+S_UNDER+atom.getId()));
	    	 g.setTransform(new Transform2(
	    			 new double[]{
	    			 1., 0., x,
	    			 0.,-1., y,
	    			 0., 0., 1.
	    	 }));
	    	 String elementType = atom.getElementType();
	    	 String fill = atomDisplay.getFill();
	    	 fontSize = atomDisplay.getFontSize();
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

	    	 SVGCircle circle;
	    	 SVGText text;
    		 circle = new SVGCircle(new Real2(0., 0.), radiusFactor*fontSize);
    		 String circleFill = elementType.equals("C") ? "none" : "white";
    		 g.appendChild(circle);
    		 circle.setFill(circleFill);
    		 text = new SVGText(
				 new Real2(xOffsetFactor*fontSize, yOffsetFactor*fontSize), atom.getElementType());
    		 if (elementType.equals("C")) {
    			 fill = "black";
    		 } else if (elementType.equals("N")) {
    			 fill = "blue";
    		 } else if (elementType.equals("O")) {
    			 fill = "red";
    		 } else if (elementType.equals("S")) {
    			 fill = "orange";
    		 } else if (elementType.equals("Cl")) {
    			 fill = "green";
    		 } else if (elementType.equals("F")) {
    			 fill = "#77ff00";
    		 } else if (elementType.equals("Br")) {
    			 fill = "#ff7700";
    		 } else if (elementType.equals("I")) {
    			 fill = "#ff00ff";
    		 } else if (elementType.equals("H")) {
    			 fill = "gray";
    		 }
    		 if (!elementType.equals("C")) {
        		 text.setFill(fill);
        		 text.setFontSize(fontSize);
    			 g.appendChild(text);
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

	/**
	 * @return the g
	 */
	public SVGElement getG() {
		return g;
	}

	
}