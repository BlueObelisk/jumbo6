package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMap.Direction;

public class AtomTreeData {

	private static Logger LOG = Logger.getLogger(AtomTreeData.class);
	
	CMLAtomSet atomSet;
	AtomSetTool atomSetTool;
	Map<String, CMLAtomSet> atomSetByAtomTreeString;
	List<String> sortedAtomTreeString;
	List<String> sortedAtomSetValues;
	private CMLAtomSet linkedAtomSet;
	private CMLAtomSet unlinkedAtomSet;

	private List<CMLAtomSet> currentLigandSetList;

	List<String> currentAtomSetValueList;
	
	public AtomTreeData(CMLAtomSet atomSet) {
		this.atomSet = atomSet;
		atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
	}
	
	Map<String, CMLAtomSet> createAtomSetByAtomTreeString(AtomMatchObject atomMatchObject) {
		atomSetByAtomTreeString = atomSetTool.createAtomSetByAtomTreeStringAtomTreeLabelling(atomMatchObject);
		return atomSetByAtomTreeString;
	}
	

	public void makeSortedListAndAtomSetValues() {
		makeSortedList();
		makeSortedAtomSetValues();
	}
	
	List<String> makeSortedList() {
		Object[] ss = atomSetByAtomTreeString.keySet().toArray();
		sortedAtomTreeString = new ArrayList<String>();
		for (Object s : ss) {
			sortedAtomTreeString.add((String)s);
		}
		Collections.sort(sortedAtomTreeString);
		return sortedAtomTreeString;
	}

	List<String> makeSortedAtomSetValues() {
		sortedAtomSetValues = new ArrayList<String>();
		for (int i = 0; i < sortedAtomTreeString.size(); i++) {
			String atomTreeString = sortedAtomTreeString.get(i);
			CMLAtomSet atomSet = atomSetByAtomTreeString.get(atomTreeString);
			sortedAtomSetValues.add(atomSet.getValue());
		}
		return sortedAtomSetValues;
	}

	void removeUniqueAtoms(Set<String> uniqueAtomTreeStringSet) {
		for (String atomTreeString : uniqueAtomTreeStringSet) {
			if (!atomSetByAtomTreeString.containsKey(atomTreeString)) {
				throw new RuntimeException("BUG bad key: "+atomTreeString);
			}
			atomSetByAtomTreeString.remove(atomTreeString);
		}
	}

	public void debugAtomSetMap(String title) {
		System.out.println("=="+title+"==");
		for (String s : atomSetByAtomTreeString.keySet()) {
			System.out.println(s+" : "+atomSetByAtomTreeString.get(s).getValue());
		}
		System.out.println("=================");
	}

	void removeUniqueElementsFromMap(int index,
			List<Integer> largestIndexList) {
			for (int jcol = 0; jcol < largestIndexList.size(); jcol++) {
				int irow = largestIndexList.get(jcol);
				if (irow > -1) {
					if (index == 1) {
						irow = jcol;
					}
					String atomTreeString = sortedAtomTreeString.get(irow);
					atomSetByAtomTreeString.remove(atomTreeString);
				}
			}
		}

	List<CMLAtomSet> getNonUniqueLigandSetList() {
		currentLigandSetList = new ArrayList<CMLAtomSet>();
		currentAtomSetValueList = new ArrayList<String>();
		for (String atomTreeString : atomSetByAtomTreeString.keySet()) {
			String atomSetValue = atomSetByAtomTreeString.get(atomTreeString).getValue();
			currentAtomSetValueList.add(atomSetValue);
			CMLAtomSet ligandSet = getNonUniqueLigandSet(atomTreeString);
			currentLigandSetList.add(ligandSet);
			
		}
		return currentLigandSetList;
	}

	CMLAtomSet getNonUniqueLigandSet(String atomTreeString) {
		CMLAtomSet atomSetX = atomSetByAtomTreeString.get(atomTreeString);
		List<CMLAtom> atoms = atomSetX.getAtoms();
		CMLAtomSet ligandSet = new CMLAtomSet(); 
		boolean start = true;
		for (CMLAtom atom : atoms) {
			if (ligandSet == null) {
				break;
			}
			List<CMLAtom> ligands = atom.getLigandAtoms();
			if (start) {
				for (CMLAtom ligand : ligands) {
					if (linkedAtomSet.contains(ligand)) {
						ligandSet.addAtom(ligand);
					}
				}
				start = false;
			} else {
				if (ligands.size() != ligandSet.size()) {
					LOG.trace("............Unequal sets");
					ligandSet = null;
				} else {
					for (CMLAtom ligand : ligands) {
						if (!ligandSet.contains(ligand)) {
							ligandSet = null;
							break;
						}
					}
				}
			}
		}
		return ligandSet;
	}

	void makeLinkedAndUnlinkedAtomSets(Direction direction, CMLMap cmlMap) {
		linkedAtomSet = this.atomSetTool.getAtomSetFromMap(direction, cmlMap);
		unlinkedAtomSet = this.atomSet.complement(linkedAtomSet);
	}

	private void analyzeLinkage() {
		List<CMLAtom> unlinkedAtoms = unlinkedAtomSet.getAtoms();
		for (CMLAtom unlinkedAtom : unlinkedAtoms) {
			List<CMLAtom> ligands = unlinkedAtom.getLigandAtoms();
			int linked = 0;
			int unlinked = 0;
			for (CMLAtom ligand : ligands) {
				if (linkedAtomSet.contains(ligand)) {
					linked++;
				} else {
					unlinked++;
				}
			}
			System.out.println(unlinkedAtom.getId()+" L "+linked+" U "+unlinked);
		}
	}

	List<CMLAtomSet> getSetsFromKeys(
			Map<String, String> fromSetValue2ToSetValueMap) {
		List<CMLAtomSet> setList = new ArrayList<CMLAtomSet>();
		Set<String> keys = fromSetValue2ToSetValueMap.keySet();
		for (String key : keys) {
			CMLAtomSet atomSetX = atomSet.getAtomSetById(key.split(CMLConstants.S_WHITEREGEX));
			setList.add(atomSetX);
		}
		return setList;
	}

	// rather crude
	void removeAtoms(String atomIds) {
		String[] ids = atomIds.split(CMLConstants.S_WHITEREGEX);
		String removeString = null;
		for (String atomTreeString : atomSetByAtomTreeString.keySet()) {
			CMLAtomSet atomSet = atomSetByAtomTreeString.get(atomTreeString);
			for (String id : ids) {
				if (atomSet.getAtomById(id) != null) {
					removeString = atomTreeString;
					break;
				}
			}
		}
		if (removeString != null) {
			atomSetByAtomTreeString.remove(removeString);
		}
	}

}
