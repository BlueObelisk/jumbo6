package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.EC;

public class SMILESWriter {
	private CMLMolecule molecule;
	Element element;
	private Set<CMLAtom> usedAtoms;
	private Set<CMLBond> usedBonds;
	private Map<CMLAtom, Element> atomMap;
	private Set<CMLAtom> atomSet;
	private int nring;

	public SMILESWriter(CMLMolecule molecule) {
		this.molecule = molecule;
		convertToKekule(molecule);
		nring = 0;
	}
	
	 private void convertToKekule(CMLMolecule molecule) {
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.adjustBondOrdersToValency();
	}

	private String serialize(Element element) {
    	 StringBuilder sb = new StringBuilder();
    	 expand(element, sb);
    	 return sb.toString();
    }

	  
    private void expand(Element element, StringBuilder sb) {
    	String el = element.getAttributeValue("elementType").trim();
    	String h = element.getAttributeValue("hydrogenCount");
    	if (h != null && !h.equals(CC.S_EMPTY)) {
 //   		h = "H"+h.trim();
    	} else {
    		h = null;
    	}
    	h = null;
    	String ch = getCharge(element);
    	boolean addSquare = 
    		h != null ||
    		!ch.equals(CC.S_EMPTY) ||
    		// common element?
    		("C N O F P S Cl Br I ".indexOf(el+CC.S_SPACE) == -1)
		;
//    	StringBuilder sb = new StringBuilder();
    	if (addSquare) {
    		sb.append(EC.S_LSQUARE);
    	}
    	sb.append(el);
    	if (h != null) {
    		sb.append(h);
    	}
    	sb.append(ch);
    	if (addSquare) {
    		sb.append(EC.S_RSQUARE);
    	}
    	
    	String rings = element.getAttributeValue("rings");
    	if (rings != null) {
    		String[] rr = rings.trim().split(EC.S_SPACE);
    		for (String r : rr) {
    			int ir = Integer.parseInt(r);
    			sb.append(((ir > 9) ? CMLConstants.S_PERCENT : CMLConstants.S_EMPTY)+r);
    		}
    	}
    	Elements elements = element.getChildElements();
    	for (int i = 0; i < elements.size(); i++) {
    		Element child = (Element) elements.get(i);
    		if (i < elements.size()-1) {
    			sb.append(EC.S_LBRAK);
    		}
    		sb.append(getOrder(child.getAttributeValue("order")));
    		expand(child, sb);
    		if (i < elements.size()-1) {
    			sb.append(EC.S_RBRAK);
    		}
    	}
    }
	    	 
    private String getOrder(String order) {
    	String s = null;
    	if (order == null) {
    		s = CMLConstants.S_EMPTY;
    	} else if (CMLBond.isSingle(order)) {
    		s = CMLConstants.S_EMPTY;
    	} else if (CMLBond.isDouble(order)) {
    		s = CMLConstants.S_EQUALS;
    	} else if (CMLBond.isTriple(order)) {
    		s = CMLConstants.S_HASH;
    	}
    	return s;
    }
    
    private String getCharge(Element element) {
    	String ff = element.getAttributeValue("formalCharge");
    	int formalCharge = (ff == null) ? 0 : Integer.parseInt(ff);
    	String s = "";
    	if (formalCharge != 0) {
    		String ss = (formalCharge < 0) ? CMLConstants.S_MINUS : CMLConstants.S_PLUS;
    		formalCharge = (formalCharge < 0) ? -formalCharge : formalCharge;
    		for (int i = 0; i < formalCharge; i++) {
    			s += ss;
    		}
    	}
    	return s;
    }
    
    private Element getIsland(CMLAtom atom) {
    	usedAtoms = new TreeSet<CMLAtom>();
		usedBonds = new TreeSet<CMLBond>();
		atomMap = new HashMap<CMLAtom, Element>();
		return addAndExpandAtom(atom, null);
    }

	private Element addAndExpandAtom(CMLAtom atom, CMLAtom parent) {
		Element element = new Element("atom");
		atomMap.put(atom, element);
		int formalCharge = atom.getFormalCharge();
		if (formalCharge != 0) {
			element.addAttribute(new Attribute("formalCharge", ""+formalCharge));
		}
		element.addAttribute(new Attribute("elementType", atom.getElementType()));
		element.addAttribute(new Attribute("id", atom.getId()));
		int h = atom.getHydrogenCount();
		if (h > 0) {
			element.addAttribute(new Attribute("hydrogenCount", ""+h));
		}
		atomSet.remove(atom);
		usedAtoms.add(atom);
    	List<CMLAtom> ligandAtoms = atom.getLigandAtoms();
    	List<CMLBond> ligandBonds = atom.getLigandBonds();
    	int i = -1;
    	for (CMLAtom ligand : ligandAtoms) {
    		i++;
    		if (ligand.equals(parent)) {
    			continue;
    		}
    		CMLBond ligandBond = ligandBonds.get(i);
    		String order = ligandBond.getOrder();
    		// ring
    		if (usedAtoms.contains(ligand)) {
    			if (!usedBonds.contains(ligandBond)) {
	    			nring++;
	    			addRing(atomMap.get(atom), nring);
	    			addRing(atomMap.get(ligand), nring);
	    			usedBonds.add(ligandBond);
    			}
    		} else {
    			Element elementx = addAndExpandAtom(ligand, atom);
    			elementx.addAttribute(new Attribute("order", order));
    			element.appendChild(elementx);
    		}
    	}
    	return element;
	}
	
	private void addRing(Element element, int nring) {
		String attVal = element.getAttributeValue("rings");
		if (attVal == null) {
			attVal = "";
		}
		attVal += " "+nring;
		element.addAttribute(new Attribute("rings", attVal));

	}
    
	String getString() {
		String s = null;
    	List<CMLAtom> atomList = molecule.getAtoms();
    	atomSet = new TreeSet<CMLAtom>();
		for (CMLAtom atom : atomList) {
    		atomSet.add(atom);
    	}
    	while (atomSet.size() > 0) {
    		CMLAtom rootAtom = atomSet.iterator().next();
    		Element tree = getIsland(rootAtom);
    		String ss = serialize(tree);
    		if (s != null) {
    			s += CMLConstants.S_PERIOD+ss;
    		} else {
    			s = ss;
    		}
    	}
    	return s;
	}
}
