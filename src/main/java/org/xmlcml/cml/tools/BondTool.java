package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.molutil.ChemicalElement.AS;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class BondTool extends AbstractSVGTool {
	@SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(BondTool.class);

    private CMLBond bond;
    private CMLMolecule molecule;
    private BondDisplay bondDisplay;
    private MoleculeTool moleculeTool;
    private MoleculeDisplay moleculeDisplay;
	private double width = 1.0;
	private double widthFactor;
	private List<GroupTool> groupToolList;

	/**
     * constructor
     * 
     * @param bond
     * @deprecated use getOrCreateTool
     */
    public BondTool(CMLBond bond) {
    	if (bond == null) {
    		throw new RuntimeException("null bond");
    	}
        this.bond = bond;
        setDefaults();
        molecule = bond.getMolecule();
        if (molecule == null) {
            throw new RuntimeException("Bond must be in molecule");
        }
    }

	private void setDefaults() {
//		this.ensureBondDisplay();
	}

	/** gets BondTool associated with bond.
	 * if null creates one and sets it in bond
	 * @param bond
	 * @return tool
	 */
	public static BondTool getOrCreateTool(CMLBond bond) {
		BondTool bondTool = (BondTool) bond.getTool();
		if (bondTool == null) {
			bondTool = new BondTool(bond);
			bond.setTool(bondTool);
		}
		return bondTool;
	}

	public CMLBond getBond() {
		return bond;
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}

	/** get conventional bond order.
	 * if order is given as unknown returns 0.5
	 * if order is absent null or otherwise unknown returns 0.0
	 * @return 1.0, 2.0, 3.0 for S,D,T; 1.5 for A, else 0.0
	 */
	public static double getNumericOrder(String order) {
		double orderD = 0.0;
		if (order == null) {
			//
		} else if (order.equals(CMLBond.DOUBLE) || order.equals(CMLBond.DOUBLE_D)) {
			orderD = 2.0;
		} else if (order.equals(CMLBond.TRIPLE) || order.equals(CMLBond.TRIPLE_T)) {
			orderD = 3.0;
		} else if (order.equals(CMLBond.SINGLE) || order.equals(CMLBond.SINGLE_S)) {
			orderD = 1.0;
		} else if (order.equals(CMLBond.AROMATIC)) {
			orderD = 1.5;
		} else if (order.equals(CMLBond.UNKNOWN_ORDER)) {
			orderD = 0.5;
		} else if (order.equals(CMLBond.ZERO)) {
			orderD = 0.0;
		} else {
			
		}
		return orderD;
	}

	/** get conventional bond order.
	 * if order is given as unknown returns 0.5
	 * if order is absent null or otherwise unknown returns 0.0
	 * @return 1.0, 2.0, 3.0 for S,D,T; 1.5 for A, else 0.0
	 */
	public double getNumericOrder() {
		return getNumericOrder(bond.getOrder());
	}

	/**
	 * 
	 * @param reaction
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLBond bond) {
		return (AbstractSVGTool) BondTool.getOrCreateTool(bond);
	}

    /** create a table to lookup bonds by atom Ids.
     * 
     * use lookupBond(Map, atom, atom) to retrieve
     * 
     * Map bondMap = BondToolImpl.createLookupTableByAtomIds();
     * ...
     * CMLBond bond = BondToolImpl.lookupBondMap(bondMap, atom1, atom2);
     * 
     * @param bonds array of bonds to index by atom IDs
     * @return Map indexed on atom IDs
     */
    @SuppressWarnings("all")
    public static Map createLookupTableByAtomIds(List<CMLBond> bonds) {
        Map map = new HashMap();
        for (CMLBond bond : bonds) {
            map.put(CMLBond.atomHash(bond), bond);
        }
        return map;
    }

    /** returns a "g" element
     * this contains the lines for bond
     * @param drawable
     * @return null if problem or atom has no coords
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	
    	g = null;
    	List<CMLAtom> atoms = bond.getAtoms();    	
    	Real2 xy0 = atoms.get(0).getXY2();
    	Real2 xy1 = atoms.get(1).getXY2();
    	
    	if (xy0 == null) {
    		// no coordinates
    	} else if (xy1 == null) {
    		// no coordinates
    	} else {
        	g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
        	g.setUserElement(bond);
	    	double bondWidth = (bondDisplay == null) ? 1.0 : bondDisplay.getScaledWidth();
	    	double middleFactor = (bondDisplay == null) ? 1.0 : bondDisplay.getDoubleMiddleFactor();
			String order = bond.getOrder();
			CMLBondStereo bondStereo = bond.getBondStereo();
			String bondStereoS = (bondStereo == null) ? null : bondStereo.getXMLContent();
	    	 // highlight
			// FIXME obsolete?
	    	 SelectionTool selectionTool = (moleculeTool == null) ? null : moleculeTool.getSelectionTool();
	    	 if (selectionTool != null) {
	    		 if (selectionTool.isSelected(bond)) {
		    		 double factor = 3.0;
		    	 	 if (order.equals(CMLBond.DOUBLE)) {
		    	 		 factor = 5.0;
		    	 	 } else if (order.equals(CMLBond.TRIPLE)) {
		    	 		 factor = 7.0;
		    	 	 }
		    		 SVGElement line = createBond("yellow", bondWidth*factor, xy0, xy1);
		    		 g.appendChild(line);
		    		 line.setFill("yellow");
		    		 line.setOpacity(0.40);
		    	 }
	    	 }
			if (false) {
			} else if (CMLBond.WEDGE.equals(bondStereoS) ||
					CMLBond.HATCH.equals(bondStereoS)) {
				SVGPath path = createWedgeHatch("black", bondDisplay.getHatchCount(),
						bondWidth, xy0, xy1, bondStereoS);
				g.appendChild(path);
			} else if (order == null || order.equals(CMLBond.SINGLE)) {
				g.appendChild(createBond("black", bondWidth, xy0, xy1));
			} else if (order.equals(CMLBond.AROMATIC)) {
				SVGElement line = createBond("black", bondWidth, xy0, xy1);
				line.addDashedStyle(bondWidth);
				g.appendChild(line);
			} else if (order.equals(CMLBond.DOUBLE)) {
				g.appendChild(createBond("black", 2.55*bondWidth, xy0, xy1));
				g.appendChild(createBond("white", 
						middleFactor*0.85*bondWidth, xy0, xy1));
			} else if (order.equals(CMLBond.TRIPLE)) {
				g.appendChild(createBond("black", 3.75*bondWidth, xy0, xy1));
				g.appendChild(createBond("white", 2.25*bondWidth, xy0, xy1));
				g.appendChild(createBond("black", 0.75*bondWidth, xy0, xy1));
			} else {
				g.appendChild(createBond("white", bondWidth, xy0, xy1));
			}
    	}
		return g;
    }

    private SVGElement createBond(String stroke, double width, Real2 xy0, Real2 xy1) {
		SVGElement line = new SVGLine(xy0, xy1);
    	line.setStroke(stroke);
    	line.setStrokeWidth(width);
    	line.setUserElement(bond);
    	line.addAttribute(new Attribute("id", bond.getId()));
    	return line;
    }

	private SVGPath createWedgeHatch(String fill, int nhatch, double width, Real2 xy0, Real2 xy1, String bondStereoS) {
		Real2Array array = new Real2Array();
		array.add(xy0);
		double wf = bondDisplay.getWedgeFactor() * 0.5;
		Real2 v1 = xy1.subtract(xy0);
		// rotate by PI/2
		Real2 v2 = new Real2(
				v1.getY() * wf,
				v1.getX() * (-wf)
		);
		SVGPath path = null;
		if (bondStereoS.equals(CMLBond.WEDGE)) {
			Real2 xy1a = xy1.plus(v2);
			array.add(xy1a);
			Real2 xy1b = xy1.subtract(v2);
			array.add(xy1b);
			path = new SVGPath(array);
			path.setFill(fill);
		} else {
			v1 = v1.multiplyBy(1./(double) nhatch);
			v2 = v2.multiplyBy(1./(double) nhatch);
			Real2 currentMid = xy0;
			path = new SVGPath();
			path.setStrokeWidth(width);
			path.setStroke(fill);
			path.setStrokeWidth(width);
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < nhatch; i++) {
				currentMid.plusEquals(v1);
				Real2 v22 = v2.multiplyBy((double) i);
				Real2 xyplus = currentMid.plus(v22);
				sb.append("M");
				sb.append(xyplus.getX()+" ");
				sb.append(xyplus.getY()+" ");
				Real2 xyminus = currentMid.subtract(v22);
				sb.append("L");
				sb.append(xyminus.getX()+" ");
				sb.append(xyminus.getY()+" ");
			}
			path.setDString(sb.toString());
		}
		return path;
	}
    
	/**
	 * @return the bondDisplay
	 */
	public BondDisplay getBondDisplay() {
		return bondDisplay;
	}

	/**
	 * @param bondDisplay the bondDisplay to set
	 */
	public void setBondDisplay(BondDisplay bondDisplay) {
		this.bondDisplay = bondDisplay;
	}
	
	public void ensureId() {
		String id = bond.getId();
		if (id == null) {
			bond.setId(bond.getAtomId(0)+CMLConstants.S_UNDER+bond.getAtomId(1));
		}
	}
	
	public void ensureBondDisplay() {
		if (bondDisplay == null) {
			ensureMoleculeDisplay();
			if (moleculeDisplay != null) {
				BondDisplay bondDisplayx = moleculeDisplay.getDefaultBondDisplay();
				if (bondDisplayx != null) {
					bondDisplay = new BondDisplay(bondDisplayx);
				}
			}
		}
	}

	public void ensureMoleculeDisplay() {
		if (moleculeDisplay == null) {
			ensureMoleculeTool();
			moleculeDisplay = moleculeTool.getMoleculeDisplay();
		}
	}

	private void ensureMoleculeTool() {
		if (moleculeTool == null) {
			CMLMolecule molecule = bond.getMolecule();
			moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		}
	}
	
	/**
	 * @param atomDisplay
	 * @return true =>omit
	 */
	public boolean omitFromDisplay(AtomDisplay atomDisplay) {
		boolean omit = false;
		if (atomDisplay != null &&
				atomDisplay.isOmitHydrogens()) {
			List<CMLAtom> atoms = bond.getAtoms();
			if (atomDisplay.omitAtom(atoms.get(0)) ||
			    atomDisplay.omitAtom(atoms.get(1))) {
				omit = true;
			}
		}
		return omit;
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
	public void setMoleculeTool(MoleculeTool moleculeTool) {
		this.moleculeTool = moleculeTool;
	}
	
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the widthFactor
	 */
	public double getWidthFactor() {
		return widthFactor;
	}

	/**
	 * @param widthFactor the widthFactor to set
	 */
	public void setWidthFactor(double widthFactor) {
		this.widthFactor = widthFactor;
	}

	/**
	 * gets atoms on one side of bond. only applicable to acyclic bonds
	 * or spiro systems (if allowSpiro==true)
	 * if bond is cyclic, whole molecule will be returned unless
	 * bond is in spiro system and allowSpiro is set
	 * 
	 *  returns atom and all
	 * descendant atoms.
	 *
	 * @param moleculeTool TODO
	 * @param atom defining side of bond
	 * @throws RuntimeException atom is not in bond
	 * @return atomSet of downstream atoms
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom atom, CMLAtomSet stopSet) {
		CMLAtomSet atomSet = new CMLAtomSet();
		CMLAtom otherAtom = bond.getOtherAtom(atom);
		if (otherAtom != null) {
			atomSet = AtomTool.getOrCreateTool(otherAtom).getDownstreamAtoms(atom, stopSet);
		}
		return atomSet;
	}

	/**
	 * gets atoms on one side of bond. only applicable to acyclic bonds if bond
	 * is cyclic, whole molecule will be returned! returns atom and all
	 * descendant atoms.
	 * calls getDownstreamAtoms(atom, stopSet = null)
	 * 
	 * @param atom defining side of bond
	 * @throws RuntimeException atom is not in bond
	 * @return atomSet of downstream atoms
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom atom) {
		return getDownstreamAtoms(atom, null);
	}

	/**
	 * transform describing the rotation and stretching of a bond.
	 * 
		Transform2 t = this.getTranformToRotateAndStretchBond(movingAtom, targetPoint) {
		
	 * The movingAtom is translated;
	 *  the fixedAtom is found by:
		
		CMLAtom pivotAtom = bond.getOtherAtom(movingAtom);
		
		A typical use is the dragging of an atom in an acyclic bond
		this carries all the downstream atoms:

		CMLAtomSet atomSet = this.getDownstreamAtoms(pivotAtom);
		atomSet.transform(t);
		
	 * @param movingAtom
	 * @param targetPoint point to translate mvingAtom to
	 * @return
	 */
	public Transform2 getTransformToRotateAndStretchBond(CMLAtom movingAtom, Real2 targetPoint) {
		CMLAtom pivotAtom = bond.getOtherAtom(movingAtom);
		Real2 pivotPoint = pivotAtom.getXY2();
		Real2 movingPoint = movingAtom.getXY2();
		Transform2 t = Transform2.getTransformToRotateAndStretchLine(pivotPoint, movingPoint, targetPoint);
		return t;
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
		List<CMLAtom> ligands0 = AtomTool.getSubstituentLigandList(bond, atomList.get(0));
		List<CMLAtom> ligands1 = AtomTool.getSubstituentLigandList(bond, atomList.get(1));
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
	
	/** get 2D mid point of bond.
	 * 
	 * @return null if either XY2 coordinate is missing
	 */
	public Real2 getMidPoint2D() {
		List<CMLAtom> atoms = bond.getAtoms();
		Real2 xy0 = atoms.get(0).getXY2();
		Real2 xy1 = atoms.get(1).getXY2();
		return (xy0 == null || xy1 == null) ? null :
			xy0.getMidPoint(xy1);
	}

	/** get 3D mid point of bond.
	 * 
	 * @return null if either XYZ3 coordinate is missing
	 */
	public Point3 getMidPoint3D() {
		List<CMLAtom> atoms = bond.getAtoms();
		Point3 xyz0 = atoms.get(0).getXYZ3();
		Point3 xyz1 = atoms.get(1).getXYZ3();
		return (xyz0 == null || xyz1 == null) ? null :
			xyz0.getMidPoint(xyz1);
	}

	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}

	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}

	public boolean hasCoordinates(CoordinateType type) {
		List<CMLAtom> atoms = bond.getAtoms();
		return (atoms != null && atoms.size() == 2 &&
				atoms.get(0).hasCoordinates(type) && 
				atoms.get(1).hasCoordinates(type)); 
	}

	public String getDownstreamMorganString(CMLAtom atom) {
		CMLAtomSet atomSet = this.getDownstreamAtoms(atom);
		Morgan morgan = new Morgan(atomSet);
		String s = morgan.getEquivalenceString();
		return s;
	}

	public boolean matchesGroupAgainstSMILES(int zeroOrOne, String groupSMILESString) {
		CMLAtom atom = bond.getAtom(zeroOrOne);
		if (atom == null) {
			throw new RuntimeException("Bad atom serial in bond: "+zeroOrOne);
		}
		String groupMorganString = Morgan.createMorganStringFromSMILES(groupSMILESString);
		return this.getDownstreamMorganString(atom).equals(groupMorganString);
	}

	public void addGroupTool(GroupTool groupToolNew) {
		ensureGroupToolList();
		boolean duplicate = false;
		for (GroupTool groupTool : groupToolList) {
			if (groupTool.isDuplicate(groupToolNew)) {
				duplicate = true;
				break;
			}
		}
		if (!duplicate) {
			groupToolList.add(groupToolNew);
		}
	}

	public List<GroupTool> getGroupToolList() {
		return groupToolList;
	}

	private void ensureGroupToolList() {
		if (this.groupToolList == null) {
			this.groupToolList = new ArrayList<GroupTool>();
		}
	}
	
	public void deleteDownstreamAtoms(CMLAtom rootAtom, CMLAtom otherAtom) {
		CMLAtomSet atomSet = this.getDownstreamAtoms(rootAtom);
		atomSet.removeAtom(otherAtom);
		atomSet.removeAtom(rootAtom);
		for (CMLAtom atom : atomSet.getAtoms()) {
			CMLMolecule molecule = atom.getMolecule();
			molecule.deleteAtom(atom);
		}
	}

	public int getElectronCount() {
		return (int) (2 * this.getNumericOrder()+0.01);
	}

}