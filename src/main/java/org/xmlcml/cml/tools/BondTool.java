package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
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
    private MoleculeTool moleculeTool;
	private SVGG g;
	private double width = 1.0;
	private double widthFactor;

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
    }

	/** gets BondTool associated with bond.
	 * if null creates one and sets it in bond
	 * @param bond
	 * @return tool
	 */
	public static BondTool getOrCreateBondTool(CMLBond bond) {
		BondTool bondTool = (BondTool) bond.getTool();
		if (bondTool == null) {
			bondTool = new BondTool(bond);
			bond.setTool(bondTool);
		}
		return bondTool;
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
     * @param drawable
     * @return null if problem or atom has no coords
     */
    public GraphicsElement createGraphicsElement(CMLDrawable drawable) {
    	g = null;
    	List<CMLAtom> atoms = bond.getAtoms();    	
    	Real2 xy0 = atoms.get(0).getXY2();
    	Real2 xy1 = atoms.get(1).getXY2();
    	if (xy0 == null) {
    		System.err.println("No bond coordinates for: "+atoms.get(0).getId());
    	} else if (xy1 == null) {
    		System.err.println("No bond coordinates for: "+atoms.get(1).getId());
    	} else {
        	g = drawable.createGraphicsElement();
	    	double bondWidth = bondDisplay.getWidth();
			String order = bond.getOrder();
	    	 // highlight
	    	 SelectionTool selectionTool = moleculeTool.getSelectionTool();
	    	 System.out.println("SELBONDS: "+selectionTool.getSelectedBonds().get(0).getId());
	    	 if (selectionTool != null) {
	    		 System.out.println("SEL BOND? : "+bond.getId());
	    		 if (selectionTool.isSelected(bond)) {
		    		 double factor = 3.0;
		    	 	 if (order.equals(CMLBond.DOUBLE)) {
		    	 		 factor = 5.0;
		    	 	 } else if (order.equals(CMLBond.TRIPLE)) {
		    	 		 factor = 7.0;
		    	 	 }
		    		 SVGLine line = createBond("yellow", bondWidth*factor, xy0, xy1);
		    		 System.out.println("BBBBBBBBBBBBBBBB");
		    		 g.appendChild(line);
		    		 line.setFill("yellow");
		    		 line.setOpacity(0.40);
		    	 }
	    	 }
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
    	}
		return g;
    }
    
    private SVGLine createBond(String stroke, double width, Real2 xy0, Real2 xy1) {
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
	 * @param atomDisplay
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

	/**
	 * @return the moleculeTool
	 */
	public MoleculeTool getMoleculeTool() {
		return moleculeTool;
	}

	/**
	 * @param moleculeTool the moleculeTool to set
	 */
	public void setMoleculeTool(MoleculeTool moleculeTool) {
		this.moleculeTool = moleculeTool;
	}
	
	/**
	 * @return the g
	 */
	public SVGElement getG() {
		return g;
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

}