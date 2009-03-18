package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement;

public class CIPTool {
	private static Logger LOG = Logger.getLogger(CIPTool.class);

	public final static String ATNUM = "atomicNumber";
	public final static String GHOST = "ghost";
	public final static String ID = "id";
	public final static String NODE = "node";
	public final static String PARENT = "parent";
	public final static String TRUE = "true";
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

	/** sort nodes on each atom.
	 * ordering is by atomic number, then ghosts
	 * should put nodes with more children first, but I am not sure of this
	 * @param node
	 */
	private void sortNodesRecursively(Element node) {
		int nNodes = node.getChildCount();
		boolean change = true;
		// sort children first
		for (int i = 0; i < nNodes; i++) {
			sortNodesRecursively((Element) node.getChild(i));
		}
		while (change) {
			change = false;
			for (int i = 0; i < nNodes - 1; i++) {
				Element nodei = (Element) node.getChild(i);
				for (int j = i+1 ; j < nNodes; j++) {
					Element nodej = (Element) node.getChild(j);
					int compare = compareWithGhosts(nodei, nodej);
					if (compare == 0) {
						compare = compareChildrenRecursively(nodei, nodej);
					}
					if (compare < 0) {
						swap(node, i, j);
						change = true;
						break;
					}
				}
			}
		}
	}
	
	public static int compareChildrenRecursively(Element nodei, Element nodej) {
		int compare = 0;
		// seed comparison
		List<Element> nodesi = new ArrayList<Element>();
		nodesi.add(nodei);
		List<Element> nodesj = new ArrayList<Element>();
		nodesj.add(nodej);
		while (true) {
			// compare at current level of tree
			int nij = Math.min(nodesi.size(), nodesj.size());
			// compare common children
			for (int ij = 0; ij < nij; ij++) {
				compare = compareWithoutGhosts(nodesi.get(ij), nodesj.get(ij));
				if (compare != 0) {
					break;
				}
			}
			// if common children match, are there any children unique to one tree?
			compare = (compare == 0) ? nodesi.size() - nodesj.size() : compare;
			// found inequality
			if (compare != 0) {
				break;
			}
			// recurse to next level of tree
			nodesi = getChildNodes(nodesi);
			nodesj = getChildNodes(nodesj);
			// no more levels in either tree?
			if (nodesi.size() == 0 && nodesj.size() == 0) {
				break;
			}
		}
		return compare;
	}
	
	private static List<Element> getChildNodes(List<Element> nodes) {
		List<Element> childNodeList = new ArrayList<Element>();
		for (Element node : nodes) {
			Nodes childNodes = node.query("node");
			for (int i = 0; i < childNodes.size(); i++) {
				childNodeList.add((Element) childNodes.get(i));
			}
		}
		return childNodeList;
	}
		
	/**
	 * compares nodes on atomic numbers
	 * if a tie then elements with ghosts have lower priority
	 * @param nodei
	 * @param nodej
	 * @return atnumi - atnumj
	 * @throws NumberFormatException
	 */
	private static int compareWithGhosts(Element nodei, Element nodej) {
		int compare = compareWithoutGhosts(nodei, nodej);
		if (compare == 0) {
			Attribute ghosti = nodei.getAttribute(GHOST);
			Attribute ghostj = nodej.getAttribute(GHOST);
			// ghosts have lower priority
			if (ghosti == null && ghostj != null) {
				compare = 1;
			}
			if (ghosti != null && ghostj == null) {
				compare = -1;
			}
		}
		return compare;
	}

	/**
	 * @param nodei
	 * @param nodej
	 * @return
	 * @throws NumberFormatException
	 */
	private static int compareWithoutGhosts(Element nodei, Element nodej)
			throws NumberFormatException {
		// sort on atomic numbers
		int compare = 
			Integer.parseInt(nodei.getAttributeValue(ATNUM)) -
			Integer.parseInt(nodej.getAttributeValue(ATNUM));
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
		Nodes ancestors = node.query("./ancestor::"+NODE+"[@"+ID+"='"+ligandId+"']");
		return (ancestors.size() > 0);
	}
	
	private void addGhost(Element node, String ligandId, int ligandAtomicNumber) {
		Element ghost = makeNode(node.getAttributeValue(ID), ligandId+CMLConstants.S_UNDER+GHOST, ligandAtomicNumber);
		ghost.addAttribute(new Attribute(GHOST, TRUE));
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
