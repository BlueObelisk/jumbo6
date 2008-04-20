package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.euclid.Real2;

/**
 * selects atoms and bonds and maybe rings
 * @author pm286
 *
 */
public class SelectionTool implements CMLConstants {

	Map<CMLAtom, Boolean> atomMap;
	Map<CMLBond, Boolean> bondMap;
	Map<Ring, Boolean> ringMap;
	
	/**
	 */
	public SelectionTool() {
		enableAtomMap();
		enableBondMap();
		enableRingMap();
	}

	/**
	 */
	public void clearAllSelections() {
		clearAtomSelections();
		clearBondSelections();
		atomMap = null;
		bondMap = null;
		ringMap = null;
	}
	
	void clearAtomSelections() {
		enableAtomMap();
		for (CMLAtom atom : atomMap.keySet()) {
			this.setSelected(atom, false);
		}
	}
	
	void clearBondSelections() {
		enableBondMap();
		for (CMLBond bond : bondMap.keySet()) {
			this.setSelected(bond, false);
		}
	}
	
	private void enableAtomMap() {
		if (atomMap == null) {
			atomMap = new HashMap<CMLAtom, Boolean>();
		}
	}
	
	private void enableBondMap() {
		if (bondMap == null) {
			bondMap = new HashMap<CMLBond, Boolean>();
		}
	}
	
	private void enableRingMap() {
		if (ringMap == null) {
			ringMap = new HashMap<Ring, Boolean>();
		}
	}

	/**
	 * @param atom
	 * @return true if selected
	 */
	public boolean isSelected(CMLAtom atom) {
		enableAtomMap();
		Boolean selected = atomMap.get(atom);
		if (selected == null) {
			selected = new Boolean(false);
			atomMap.put(atom, selected);
		}
		return selected.booleanValue();
	}

	/**
	 * @param atom
	 * @param selected
	 */
	public void setSelected(CMLAtom atom, boolean selected) {
		enableAtomMap();
		if (atom != null) {
			atomMap.put(atom, new Boolean(selected));
			if (selected) {
				highlightAtom(atom);
			} else {
				deHighlightAtom(atom);
			}
		}
	}

	/**
	 * draws circle or other thing on atom
	 * @param atom
	 */
	public void highlightAtom(CMLAtom atom) {
	 // highlight
		 AtomTool atomTool = AtomTool.getOrCreateTool(atom);
   		 SVGCircle circle = new SVGCircle(new Real2(0., 0.), atomTool.getRadiusFactor() * atomTool.getFontSize()*2.3);
   		 circle.addAttribute(new Attribute("class", "highlight"));
   		 circle.setFill("yellow");
   		 circle.setOpacity(0.70);
   		 SVGElement g = atomTool.getG();
   		 if (g != null) {
   			 g.appendChild(circle);
   		 } else {
   			 System.err.println("Atom has no SVGG child "+atom.getId());
   		 }
	 }
	
	/**
	 * undraws circle or other thing on atom
	 * @param atom
	 */
	public void deHighlightAtom(CMLAtom atom) {
	 // highlight
		 AtomTool atomTool = AtomTool.getOrCreateTool(atom);
   		 SVGElement g = atomTool.getG();
   		 if (g != null) {
   			 Nodes nodes = g.query(".//*[@class='highlight']");
   			 for (int i = 0; i < nodes.size(); i++ ) {
   				 nodes.get(i).detach();
   			 }
   		 } else {
   			 System.err.println("Atom has no SVGG: "+atom.getId());
   		 }
	 }
	
	/** gets selected atoms.
	 * @return list
	 */
	public List<CMLAtom> getSelectedAtoms() {
		List<CMLAtom> atoms = new ArrayList<CMLAtom>();
		for (CMLAtom atom : atomMap.keySet()) {
			if (atomMap.get(atom)) {
				atoms.add(atom);
			}
		}
		return atoms;
	}
	
	/**
	 * @param bond
	 * @return true if selected
	 */
	public boolean isSelected(CMLBond bond) {
		enableBondMap();
		Boolean selected = bondMap.get(bond);
		if (selected == null) {
			selected = new Boolean(false);
			bondMap.put(bond, selected);
		}
		return selected.booleanValue();
	}

	/**
	 * @param bond
	 * @param selected
	 */
	public void setSelected(CMLBond bond, boolean selected) {
		enableBondMap();
		if (bond != null) {
			bondMap.put(bond, new Boolean(selected));
//			System.out.println("SET SEL BOND "+bond.getId());
			if (selected) {
				highlightBond(bond);
			} else {
				deHighlightBond(bond);
			}
		}
	}
	
	/**
	 * draws circle or other thing on bond
	 * @param bond
	 */
	public void highlightBond(CMLBond bond) {
	 // highlight
		 BondTool bondTool = BondTool.getOrCreateTool(bond);
		 List<CMLAtom> atoms = bond.getAtoms();
   		 SVGLine line = new SVGLine(atoms.get(0).getXY2(), atoms.get(1).getXY2());
   		 line.setStrokeWidth(bondTool.getWidth() * 10.0);
   		 line.addAttribute(new Attribute("class", "highlight"));
   		 line.setFill("yellow");
   		 line.setOpacity(0.70);
   		 SVGElement g = bondTool.getG();
   		 if (g != null) {
   			 System.out.println("HBO "+bond.getId());
   			 g.appendChild(line);
   		 } else {
   			 System.err.println("HI: Bond has no SVGG child "+bond.getId());
   		 }
	 }
	
	/**
	 * undraws circle or other thing on bond
	 * @param bond
	 */
	public void deHighlightBond(CMLBond bond) {
	 // highlight
		 BondTool bondTool = BondTool.getOrCreateTool(bond);
   		 SVGElement g = bondTool.getG();
   		 if (g != null) {
   			 Nodes nodes = g.query(".//*[@class='highlight']");
   			 for (int i = 0; i < nodes.size(); i++ ) {
   				 nodes.get(i).detach();
   			 }
   		 } else {
   			 System.err.println("DEHI: Bond has no SVGG: "+bond.getId());
   		 }
	}

	/** gets selected bonds.
	 * @return list
	 */
	public List<CMLBond> getSelectedBonds() {
		List<CMLBond> bonds = new ArrayList<CMLBond>();
		for (CMLBond bond : bondMap.keySet()) {
			if (bondMap.get(bond)) {
				bonds.add(bond);
			}
		}
		return bonds;
	}
	

	/**
	 * @return string
	 */
	public String toString() {
		String s = "";
		for (CMLAtom atom : atomMap.keySet()) {
			s += " "+atom.getId()+" "+atomMap.get(atom);
		}
		return s;
	}

}
