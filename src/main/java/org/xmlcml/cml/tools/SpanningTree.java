package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tool to support a spanningtree
 * 
 * @author pmr
 * 
 */
public class SpanningTree extends AbstractTool {
	final static Logger logger = Logger.getLogger(SpanningTree.class.getName());

	private CMLAtom rootAtom;
	private CMLAtomSet includedAtomSet;
	private CMLBondSet includedBondSet;
	private boolean omitHydrogens;
	private CMLAtomSet usedAtomSet;
	private Map<CMLAtom, SpanningTreeElement> atomSpanningTreeElementMap;
	private CMLAtomSet branchPoints;
	private SpanningTreeElement rootElement;
	private Map<SpanningTreeElement, List<SpanningTreeElement>> elementAncestorMap;
	private Map<CMLAtom, List<CMLAtom>> atomAncestorMap;
	private Map<CMLAtom, Map<CMLAtom, AtomPath>> pathMap;
	private Map<CMLAtom, List<CMLAtom>> ligandAtomMap;
	private Map<CMLAtom, List<CMLBond>> ligandBondMap;
	private List<CMLAtom> terminalAtomList;
	
	/**
	 * bondSet calculated from atomSet
	 * omitHydrogens defaults to true
	 * @param includedAtomSet
	 */
	public SpanningTree(CMLAtomSet includedAtomSet) {
		init();
		this.setIncludedAtomSet(includedAtomSet);
		if (includedAtomSet.size() == 0) {
			throw new RuntimeException("Zero size atom set");
		}
		CMLMolecule molecule = includedAtomSet.getAtom(0).getMolecule();
		this.setIncludedBondSet(new CMLBondSet(molecule.getBonds()));
		this.setOmitHydrogens(omitHydrogens);
	}

	/**
	 * omitHydrogens defaults to true
	 * @param includedAtomSet
	 * @param includedBondSet
	 */
	public SpanningTree(CMLAtomSet includedAtomSet, CMLBondSet includedBondSet) {
		this(includedAtomSet, includedBondSet, true);
		this.setOmitHydrogens(omitHydrogens);
	}

	/**
	 * 
	 * @param includedAtomSet
	 * @param includedBondSet
	 * @param omitHydrogens
	 */
	public SpanningTree(CMLAtomSet includedAtomSet, CMLBondSet includedBondSet, boolean omitHydrogens) {
		init();
		this.setIncludedAtomSet(includedAtomSet);
		this.setIncludedBondSet(includedBondSet);
		this.setOmitHydrogens(omitHydrogens);
	}
	
	private void init() {
		branchPoints = new CMLAtomSet();
	}

	/** generate ST from given root atom.
	 * if included atoms and bonds are NOT given then expands to
	 * all neighbouring atoms and bonds
	 * if included atoms and/or bonds are given, then tree is restricted
	 * to these.
	 * @param rootAtom
	 */
	public void generate(CMLAtom rootAtom) {
		generateIncludedLigands();
		generateTerminals();
		this.setRootAtom(rootAtom);
		usedAtomSet = new CMLAtomSet();
		rootElement = new SpanningTreeElement(null, rootAtom, null);
		expand(rootAtom, rootElement);
//		LOG.debug("USED "+usedAtomSet.size());
	}

	private void generateTerminals() {
		terminalAtomList = new ArrayList<CMLAtom>();
		List<CMLAtom> atomList = includedAtomSet.getAtoms();
		for (CMLAtom atom : atomList) {
			if (includeAtom(atom)) {
				if (ligandAtomMap.get(atom).size() == 1) {
					terminalAtomList.add(atom);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param element to start at
	 * @return list of ancestors (root is last)
	 */
	private List<SpanningTreeElement> getAncestors(SpanningTreeElement element) {
		if (element == null) {
			throw new RuntimeException("null spanningTreeElement");
		}
		ensureElementAncestorMap();
		List<SpanningTreeElement> ancestors = elementAncestorMap.get(element);
		if (ancestors == null) {
			ancestors = new ArrayList<SpanningTreeElement>();
			ancestors.add(element);
			SpanningTreeElement spe = element;
			while (spe.getParent() != null) {
				spe = spe.getParent();
				ancestors.add(spe);
			}
			if (elementAncestorMap == null) {
				elementAncestorMap = new HashMap<SpanningTreeElement, List<SpanningTreeElement>>();
			}
			elementAncestorMap.put(element, ancestors);
		}
		return ancestors;
	}
	
	/**
	 * return list of atom ancestors
	 * @param atom to start list at
	 * @return list finishing at root
	 */
	private List<CMLAtom> getAncestorAtoms(CMLAtom atom) {
		ensureAtomAncestorMap();
		List<CMLAtom> ancestorAtoms = atomAncestorMap.get(atom);
		if (ancestorAtoms == null) {
			SpanningTreeElement startElement = atomSpanningTreeElementMap.get(atom);
			List<SpanningTreeElement> ancestorElements = getAncestors(startElement);
			ancestorAtoms = new ArrayList<CMLAtom>();
			for (SpanningTreeElement spanningTreeElement : ancestorElements) {
				ancestorAtoms.add(spanningTreeElement.getAtom());
			}
			ensureAtomAncestorMap();
			atomAncestorMap.put(atom, ancestorAtoms);
		}
		return ancestorAtoms;
	}
	
	private void ensurePathMap() {
		if (pathMap == null) {
			pathMap = new HashMap<CMLAtom, Map<CMLAtom, AtomPath>>();
		}
	}
	
	private void ensureAtomAncestorMap() {
		if (atomAncestorMap == null) {
			atomAncestorMap = new HashMap<CMLAtom, List<CMLAtom>>();
		}
	}
	
	private void ensureAtomSpanningTreeElementMap() {
		if (atomSpanningTreeElementMap == null) {
			atomSpanningTreeElementMap = new HashMap<CMLAtom, SpanningTreeElement>();
		}
	}

	private void ensureElementAncestorMap() {
		if (elementAncestorMap == null) {
			elementAncestorMap = new HashMap<SpanningTreeElement, List<SpanningTreeElement>>();
		}
	}

	/**
	 */
	public void generateTerminalPaths() {
		ensurePathMap();
		for (int i = 0; i < terminalAtomList.size()-1; i++) {
			CMLAtom atomi = terminalAtomList.get(i);			
			for (int j = i+1; j < terminalAtomList.size(); j++) {
				CMLAtom atomj = terminalAtomList.get(j);			
				getPath(atomi, atomj);
			}
		}
	}

	/**
	 * gets path between two atoms
	 * @param startAtom
	 * @param endAtom
	 * @return path as list of atoms
	 */
	public AtomPath getPath(CMLAtom startAtom, CMLAtom endAtom) {
//		LOG.debug("XX "+startAtom.getId()+"/"+endAtom.getId());
		ensurePathMap();
		Map<CMLAtom, AtomPath> endAtomMap = pathMap.get(startAtom);
		if (endAtomMap == null) {
			endAtomMap = new HashMap<CMLAtom, AtomPath>();
			pathMap.put(startAtom, endAtomMap);
		}
		AtomPath path = endAtomMap.get(endAtom);
		if (path == null) {
			path = new AtomPath();
			List<CMLAtom> ancestorListi = getAncestorAtoms(startAtom); 
			int lengthi = ancestorListi.size();
			List<CMLAtom> ancestorListj = getAncestorAtoms(endAtom);
			int lengthj = ancestorListj.size();
			int common = 0;
			CMLAtom atomij = null;
			for (int i = lengthi - 1, j = lengthj -1; i >= 0 && j >= 0; i--, j--) {
				CMLAtom ai = ancestorListi.get(i);
				CMLAtom aj = ancestorListj.get(j);
				if (ai.equals(aj)) {
					atomij = ai;
					common++;
				} else {
					break;
				}
			}
			for (int i = 0; i < lengthi - common; i++) {
				CMLAtom ai = ancestorListi.get(i);
				path.add(ai);
			}
			path.add(atomij);
			for (int j = lengthj - common - 1; j >= 0; j--) {
				CMLAtom aj = ancestorListj.get(j);
				path.add(aj);
			}
			endAtomMap.put(endAtom, path);
			// and add the reverse path
		}
		Map<CMLAtom, AtomPath> startAtomMap = pathMap.get(endAtom);
		if (startAtomMap == null) {
			startAtomMap = new HashMap<CMLAtom, AtomPath>();
			pathMap.put(endAtom, startAtomMap);
		}
		AtomPath reversePath = startAtomMap.get(startAtom);
		if (reversePath == null) {
			reversePath = path.getReversePath();
			startAtomMap.put(startAtom, reversePath);
		}
		return path;
	}
	
	private void expand(CMLAtom atom, SpanningTreeElement element) {
		ensureAtomSpanningTreeElementMap();
		usedAtomSet.addAtom(atom);
		atomSpanningTreeElementMap.put(atom, element);
//		LOG.debug("PUT "+atom.getId());
		List<CMLAtom> ligands = ligandAtomMap.get(atom);
		List<CMLBond> ligandBonds = ligandBondMap.get(atom);
		int nbranches = 0;
		for (int i = 0; i < ligands.size(); i++) {
			CMLAtom ligand = ligands.get(i);
			CMLBond ligandBond = ligandBonds.get(i);
			if (!includeAtom(ligand) || !includeBond(ligandBond)) {
				continue;
			}
			nbranches++;
			SpanningTreeElement spe = new SpanningTreeElement(element, ligand, ligandBond);
			expand(ligand, spe);
		}
		if (nbranches > 1) {
			branchPoints.addAtom(atom);
		}
	}
	
	private void generateIncludedLigands() {
		if (includedAtomSet == null || includedBondSet == null) {
			throw new RuntimeException("Must define atom and bond sets");
		}
		ligandAtomMap = new HashMap<CMLAtom, List<CMLAtom>>();
		ligandBondMap = new HashMap<CMLAtom, List<CMLBond>>();
		for (CMLAtom atom : includedAtomSet.getAtoms()) {
			generateIncludedLigands(atom);
		}
	}
	private void generateIncludedLigands(CMLAtom atom) {
		List<CMLAtom> includedLigandList = new ArrayList<CMLAtom>();
		List<CMLBond> includedLigandBondList = new ArrayList<CMLBond>();
		List<CMLAtom> ligands = atom.getLigandAtoms();
		List<CMLBond> ligandBonds = atom.getLigandBonds();
		for (int i = 0; i < ligands.size(); i++) {
			if (includeAtom(ligands.get(i)) && includeBond(ligandBonds.get(i))) {
				includedLigandList.add(ligands.get(i));
				includedLigandBondList.add(ligandBonds.get(i));
			}
		}
		ligandAtomMap.put(atom, includedLigandList);
		ligandBondMap.put(atom, includedLigandBondList);
	}
	
	private boolean includeAtom(CMLAtom atom) {
		boolean include = true;
		if (includedAtomSet != null && !includedAtomSet.contains(atom)) {
			include = false;
		}
		if (include && usedAtomSet != null && usedAtomSet.contains(atom)) {
			include = false;
		}
		if (include && omitHydrogens && AS.H.equals(atom.getElementType())) {
			include = false;
		}
		return include;
	}
	
	private boolean includeBond(CMLBond bond) {
		boolean include = true;
		if (includedBondSet != null && !includedBondSet.contains(bond)) {
			include = false;
		}
		return include;
	}
	
	/**
	 * @return the rootAtom
	 */
	public CMLAtom getRootAtom() {
		return rootAtom;
	}

	/**
	 * @param rootAtom the rootAtom to set
	 */
	public void setRootAtom(CMLAtom rootAtom) {
		this.rootAtom = rootAtom;
	}

	/**
	 * @return the includedAtomSet
	 */
	public CMLAtomSet getIncludedAtomSet() {
		return includedAtomSet;
	}

	/**
	 * @param includedAtomSet the includedAtomSet to set
	 */
	public void setIncludedAtomSet(CMLAtomSet includedAtomSet) {
		this.includedAtomSet = includedAtomSet;
	}

	/**
	 * @return the includedBondSet
	 */
	public CMLBondSet getIncludedBondSet() {
		return includedBondSet;
	}

	/**
	 * @param includedBondSet the includedBondSet to set
	 */
	public void setIncludedBondSet(CMLBondSet includedBondSet) {
		this.includedBondSet = includedBondSet;
	}

	/**
	 * @return the omitHydrogens
	 */
	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}

	/**
	 * @param omitHydrogens the omitHydrogens to set
	 */
	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}

	/**
	 * @return the atomSpanningTreeElementMap
	 */
	public Map<CMLAtom, SpanningTreeElement> getAtomMap() {
		return atomSpanningTreeElementMap;
	}

	/**
	 * @return the usedAtomSet
	 */
	public CMLAtomSet getUsedAtomSet() {
		return usedAtomSet;
	}

	/**
	 * @return string
	 */
	public String toString() {
		String s = "Spanning tree:\n";
		s += rootElement.toString();
		return s;
	}

	/**
	 * @return the atomAncestorMap
	 */
	public Map<CMLAtom, List<CMLAtom>> getAtomAncestorMap() {
		return atomAncestorMap;
	}

	/**
	 * @return the branchPoints
	 */
	public CMLAtomSet getBranchPoints() {
		return branchPoints;
	}

	/**
	 * @return the elementAncestorMap
	 */
	public Map<SpanningTreeElement, List<SpanningTreeElement>> getElementAncestorMap() {
		return elementAncestorMap;
	}

	/**
	 * @return the pathMap
	 */
	public Map<CMLAtom, Map<CMLAtom, AtomPath>> getPathMap() {
		return pathMap;
	}

	/**
	 * @return the rootElement
	 */
	public SpanningTreeElement getRootElement() {
		return rootElement;
	}

	/**
	 * @return the terminalAtomList
	 */
	public List<CMLAtom> getTerminalAtomList() {
		return terminalAtomList;
	}
	
}

