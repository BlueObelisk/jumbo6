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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;

/**
 * selects atoms and bonds and maybe rings
 * @author pm286
 *
 */
public class SelectionTool implements CMLConstants {
	private static Logger LOG = Logger.getLogger(SelectionTool.class);

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
   		 SVGElement circle = new SVGCircle(new Real2(0., 0.), atomTool.getRadiusFactor() * atomTool.getFontSize()*2.3);
   		 circle.addAttribute(new Attribute("class", "highlight"));
   		 circle.setFill("yellow");
   		 circle.setOpacity(0.70);
   		 SVGElement g = atomTool.getG();
   		 if (g != null) {
   			 g.appendChild(circle);
   		 } else {
   			 LOG.trace("Atom has no SVGG child "+atom.getId());
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
//			LOG.debug("SET SEL BOND "+bond.getId());
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
		 if (bondTool.hasCoordinates(CoordinateType.TWOD)) {
			 List<CMLAtom> atoms = bond.getAtoms();
			 
	   		 SVGElement line = new SVGLine(atoms.get(0).getXY2(), atoms.get(1).getXY2());
	   		 line.setStrokeWidth(bondTool.getWidth() * 10.0);
	   		 line.addAttribute(new Attribute("class", "highlight"));
	   		 line.setFill("yellow");
	   		 line.setOpacity(0.70);
	   		 SVGElement g = bondTool.getG();
	   		 if (g != null) {
	   			 LOG.debug("HBO "+bond.getId());
	   			 g.appendChild(line);
	   		 } else {
	   			 LOG.trace("HI: Bond has no SVGG child "+bond.getId());
	   		 }
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
