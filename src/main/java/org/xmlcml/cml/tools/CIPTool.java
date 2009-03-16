package org.xmlcml.cml.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement;

public class CIPTool {

	public final static String ATNUM = "atnum";
	public final static String GHOST = "ghost";
	public final static String ID = "id";
	public final static String NODE = "node";
	public final static String PARENT = "parent";
	private CMLMolecule molecule;
	private Queue<Element> nodeQueue;

	public CIPTool(CMLMolecule molecule) {
		this.molecule = molecule;
	}
	
	/** gets tree rooted at atom, pointing down bond.
	 * uses CIP strategy (cf. Wikipedia)
	 * @param atom0
	 * @param bond
	 * @return
	 */
	public Element getBreadthFirstCIPTree(String rootId, String atom1Id) {
		nodeQueue = new  LinkedList<Element>();
		Element elem1 = makeNode(rootId, atom1Id, getAtomicNumber(molecule.getAtomById(atom1Id)));
		nodeQueue.add(elem1);
		while (!nodeQueue.isEmpty()) {
			Element nextElement = nodeQueue.remove();
			processElement(nextElement);
		}
		sortNodesRecursively(elem1);
		return elem1;
	}
	
	private void sortNodesRecursively(Element node) {
		int nNodes = node.getChildCount();
		boolean change = true;
		for (int i = 0; i < nNodes; i++) {
			sortNodesRecursively((Element) node.getChild(i));
		}
		while (change) {
			change = false;
			for (int i = 0; i < nNodes - 1; i++) {
				Element nodei = (Element) node.getChild(i);
				for (int j = i+1 ; j < nNodes; j++) {
					Element nodej = (Element) node.getChild(j);
					if (compare(nodei, nodej) < 0) {
						swap(node, i, j);
						change = true;
						break;
					}
				}
			}
		}
	}
	
	public static int compare(Element nodei, Element nodej) {
		int compare = 0;
		// sort on atomic numbers
		int atnumi = Integer.parseInt(nodei.getAttributeValue(ATNUM));
		int atnumj = Integer.parseInt(nodej.getAttributeValue(ATNUM));
		if (atnumi < atnumj) {
			compare = -1;
		} else if (atnumi > atnumj) {
			compare = 1;
		}
		// compare on children recursively
		// whenever unequal break
		if (compare == 0) {
			int minChildCount = Math.min(nodei.getChildCount(), nodej.getChildCount());
			for (int i = 0; i < minChildCount; i++) {
				compare = compare((Element) nodei.getChild(i), (Element) nodej.getChild(i));
				if (compare != 0) {
					break;
				}
			}
			// still equal?
			if (compare == 0) {
				compare = nodei.getChildCount() - nodej.getChildCount();
			}
		}
		return compare;
	}
	
	// j > i
	private void swap(Element node, int i, int j) {
		Node nodei = node.getChild(i);
		Node nodej = node.getChild(j);
		nodej.detach();
		node.insertChild(nodej, i);
		nodei.detach();
		node.insertChild(nodei, j);
	}
	
	/*
	 * explore from atom
	 */
	private Element processElement(Element node) {
		String atomId = node.getAttributeValue(ID);
		String parentId = node.getAttributeValue(PARENT);
		CMLAtom atom = molecule.getAtomById(atomId);
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		for (int i = 0; i < ligandList.size(); i++) {
			CMLBond ligandBond = ligandBondList.get(i);
			CMLAtom ligandAtom = ligandList.get(i);
			int ligandAtomicNumber = getAtomicNumber(ligandAtom);
			String ligandId = ligandAtom.getId();
			String order = ligandBond.getOrder();
			// multiple bonds (regardless of whether already in tree)
			if (order.equals(CMLBond.DOUBLE)) {
				addGhost(node, ligandId, ligandAtomicNumber);
			} else if (order.equals(CMLBond.TRIPLE)) {
				addGhost(node, ligandId, ligandAtomicNumber);
				addGhost(node, ligandId, ligandAtomicNumber);
			}
			if (ligandId.equals(parentId)) {
				// no action
			} else if (isAncestorOfLigandId(node, ligandId)) {
				addGhost(node, ligandId, ligandAtomicNumber);
			} else {
				Element newNode = makeNode(atomId, ligandId, ligandAtomicNumber);
				node.appendChild(newNode);
				nodeQueue.add(newNode);
			}
		}
		return node;
	}

	/**
	 * @param ligandAtom
	 * @return
	 */
	private int getAtomicNumber(CMLAtom ligandAtom) {
		String ligandElementType = ligandAtom.getElementType();
		ChemicalElement ligandElement = ChemicalElement.getChemicalElement(ligandElementType);
		int ligandAtomicNumber = (ligandElement != null) ? ligandElement.getAtomicNumber() : -1;
		return ligandAtomicNumber;
	}
	
	private boolean isAncestorOfLigandId(Element node, String ligandId) {
		Nodes ancestors = node.query("./ancestor::node[@id='"+ligandId+"']");
		return (ancestors.size() > 0);
	}
	
	private void addGhost(Element node, String ligandId, int ligandAtomicNumber) {
		Element ghost = makeNode(node.getAttributeValue(ID), ligandId+CMLConstants.S_UNDER+GHOST, ligandAtomicNumber);
		ghost.addAttribute(new Attribute(GHOST, "true"));
		node.appendChild(ghost);
	}
	
	private Element makeNode(String parentId, String atomId, int atomAtomicNumber) {
		Element node = new Element(NODE);
		node.addAttribute(new Attribute(PARENT, parentId));
		node.addAttribute(new Attribute(ID, atomId));
		node.addAttribute(new Attribute(ATNUM, ""+atomAtomicNumber));
		return node;
	}

}
