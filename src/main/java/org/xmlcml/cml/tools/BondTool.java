package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.euclid.Real2;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class BondTool extends AbstractTool {

    private CMLBond bond;
    private CMLMolecule molecule;
    Logger logger = Logger.getLogger(BondTool.class.getName());
    private BondDisplay bondDisplay;

    /**
     * constructor
     * 
     * @param bond
     */
    public BondTool(CMLBond bond) {
        this.bond = bond;
        molecule = bond.getMolecule();
        if (molecule == null) {
            throw new CMLRuntimeException("Bond must be in molecule");
        }
//        moleculeTool = new MoleculeTool(molecule);
    }

    /**
     * make bond tool from a bond.
     * 
     * @param bond
     * @return the tool
     */
    static BondTool createBondTool(CMLBond bond) {
        return new BondTool(bond);
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
     * @return null if problem
     */
    public SVGElement createSVG() {
    	SVGG g = new SVGG();
    	List<CMLAtom> atoms = bond.getAtoms();    	
    	Real2 xy0 = atoms.get(0).getXY2();
    	Real2 xy1 = atoms.get(1).getXY2();
    	double bondWidth = bondDisplay.getWidth();
//		boolean writeLabel = false;
//		double width = 1.0;
//		String dash = "";
//		String color = "black";
//		if (this.wedgeType == Type.SOLID_NEITHER) {
//			width = 2*width;
//		}
//		if (wedgeType == Type.HATCH_NEITHER) {
//			dash = "stroke-dasharray:2,2;";
//		}
		String order = bond.getOrder();
		if (order == null || order.equals(CMLBond.SINGLE)) {
			g.appendChild(createBond("black", bondWidth, xy0, xy1));
		} else if (order.equals(CMLBond.DOUBLE)) {
			g.appendChild(createBond("black", 3*bondWidth, xy0, xy1));
			g.appendChild(createBond("white", 1*bondWidth, xy0, xy1));
		} else if (order.equals(CMLBond.TRIPLE)) {
			g.appendChild(createBond("black", 5*bondWidth, xy0, xy1));
			g.appendChild(createBond("white", 3*bondWidth, xy0, xy1));
			g.appendChild(createBond("black", bondWidth, xy0, xy1));
		}
		return g;
    }
    
    private SVGElement createBond(String stroke, double width, Real2 xy0, Real2 xy1) {
    	SVGLine line = new SVGLine(xy0, xy1);
    	line.setStroke(stroke);
    	line.setStrokeWidth(width);
    	return line;
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
	
	/**
	 * 
	 * @return true =>omit
	 */
	public boolean omitFromDisplay(AtomDisplay atomDisplay) {
		boolean omit = false;
		if (atomDisplay != null && atomDisplay.isOmitHydrogens()) {
			List<CMLAtom> atoms = bond.getAtoms();
			if (atomDisplay.omitAtom(atoms.get(0)) ||
			    atomDisplay.omitAtom(atoms.get(1))) {
				omit = true;
			}
		}
		return omit;
	}
}