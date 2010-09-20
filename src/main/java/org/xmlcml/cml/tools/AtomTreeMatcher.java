package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.molutil.ChemicalElement;

public class AtomTreeMatcher extends AtomMatcher {

	private static Logger LOG = Logger.getLogger(AtomTreeMatcher.class);

	public static String BALANCED = "balanced";
	public static String UNBALANCED = "unbalanced";
	public static String ORPHAN = "orphan";
	public static String COMMON_LIGANDS = "commonLigands";
	public static String COMMON_ATOMTREE = "commonAtomTree";
	public static String UNIQUE_TREE = "unique treeString";
	
	private static int SAFETY = 3;
	
	private AtomTreeData atomTreeData0;
	private AtomTreeData atomTreeData1;
	private CMLMap cmlMap = null;
  
	private Map<CMLAtom, CMLAtomSet> from2ToAtomAtomMap;

	private Map<CMLAtom, CMLAtomSet> to2FromAtomAtomMap;
	
	public CMLMap match(CMLAtomSet atomSet0, CMLAtomSet atomSet1, String title) {
		atomTreeData0 = new AtomTreeData(atomSet0);
		atomTreeData1 = new AtomTreeData(atomSet1);

		Map<String, CMLAtomSet> atomSetByAtomTreeString0 = atomTreeData0.createAtomSetByAtomTreeString(atomMatchObject);
		Map<String, CMLAtomSet> atomSetByAtomTreeString1 = atomTreeData1.createAtomSetByAtomTreeString(atomMatchObject);
		if (atomSetByAtomTreeString0.size() != 0 || atomSetByAtomTreeString1.size() != 0) {
			mapSingleMolecules(title);
		}
		return cmlMap;
	}

	private void mapSingleMolecules(String title) {
		cmlMap = makeMap();
		/*Set<String> uniqueAtomTreeStringSet = */
		mapUniqueAtomsByTreeString(UNIQUE_TREE);
		mapByUniqueLargestCommonAtomTreeStrings();
		addUniqueLigandsToUniqueAtoms();
		resolveAmbiguousLinks();
		addMissingIds();
		tidyOrphansAndMismatches();
	}

	private void tidyOrphansAndMismatches() {
		boolean change = true;
		int tries = SAFETY;
		while (tries-- > 0 || change) {
			CMLElements<CMLLink> links = cmlMap.getLinkElements();
			List<CMLLink> unequalLinkList = makeUnequalToFromList(links);
//			debugLinks("UNEQUAL", unequalLinkList);
			List<CMLLink> orphanList = makeOrphanList(links);
//			debugLinks("ORPHAN", orphanList);
			change = false;
			change |= deOrphanizeSingleToFrom(orphanList);
			change |= tryToResolveConnectivity(orphanList);
			change |= tryToResolveConnectivity(unequalLinkList);
//			change |= conflateUnbalanced();
		}
	}

	private boolean tryToResolveConnectivity(List<CMLLink> list) {
		boolean overallChange = false;
		makeAtomAtomMaps();
		for (CMLLink link : list) {
			int ii = SAFETY;
			boolean change = true;
			while (ii-- > 0 && change) {
				change = tryToResolveConnectivity(link);
				overallChange |= change;
			}
		}
		return overallChange;
	}

	private void makeAtomAtomMaps() {
		from2ToAtomAtomMap = new HashMap<CMLAtom, CMLAtomSet>();
		to2FromAtomAtomMap = new HashMap<CMLAtom, CMLAtomSet>();
		CMLElements<CMLLink> links = cmlMap.getLinkElements();
		for (CMLLink link : links) {
			if (link.getTitle().startsWith(ORPHAN) ||
				link.getTitle().contains(UNBALANCED)) {
				continue;
			}
			LinkTool linkTool = LinkTool.getOrCreateTool(link);
			List<CMLAtom> fromAtoms = linkTool.getSet(Direction.FROM, atomTreeData0.atomSet).getAtoms();
			List<CMLAtom> toAtoms = linkTool.getSet(Direction.TO, atomTreeData1.atomSet).getAtoms();
			addToAtomsToAtomSetIndexedByFrom(from2ToAtomAtomMap, fromAtoms,
					toAtoms);
			addToAtomsToAtomSetIndexedByFrom(to2FromAtomAtomMap, toAtoms,
					fromAtoms);
		}
//		debugAtomAtomMap("FROM", from2ToAtomAtomMap);
//		debugAtomAtomMap("TO", to2FromAtomAtomMap);
	}

	private void debugAtomAtomMap(String title, Map<CMLAtom, CMLAtomSet> atomSetX) {
		System.out.println(title);
		for (CMLAtom fromAtom : atomSetX.keySet()) {
			CMLAtomSet atomSet = atomSetX.get(fromAtom);
			System.out.println(fromAtom.getId()+" "+((atomSet == null) ? 0 : atomSet.size()));
		}
	}

	private void addToAtomsToAtomSetIndexedByFrom(
			Map<CMLAtom, CMLAtomSet> from2ToAtomAtomMap,
			List<CMLAtom> fromAtoms, List<CMLAtom> toAtoms) {
		for (CMLAtom from : fromAtoms) {
			CMLAtomSet toAtomSet = from2ToAtomAtomMap.get(from);
			if (toAtomSet == null) {
				toAtomSet = new CMLAtomSet();
				from2ToAtomAtomMap.put(from, toAtomSet);
			}
			for (CMLAtom toAtom : toAtoms) {
				toAtomSet.addAtom(toAtom);
			}
		}
	}

	private boolean tryToResolveConnectivity(CMLLink orphanLink) {
		boolean match = false;
		LinkTool orphanLinkTool = LinkTool.getOrCreateTool(orphanLink);
		CMLAtomSet fromAtomSet = orphanLinkTool.getSet(Direction.FROM, atomTreeData0.atomSet);
		List<CMLAtom> fromAtoms = fromAtomSet.getAtoms();
		CMLAtomSet toAtomSet = orphanLinkTool.getSet(Direction.TO, atomTreeData1.atomSet);
		List<CMLAtom> toAtoms = toAtomSet.getAtoms();
		LOG.trace("checkingFromTo");
		for (CMLAtom fromAtom : fromAtoms) {
			for (CMLAtom toAtom : toAtoms) {
				match = doLigandsMatch(fromAtom, toAtom);
				if (match) {
					LOG.trace("MATCH!!!!!!!!!!!!!!"+fromAtom.getId()+" .. "+toAtom.getId());
					fromAtomSet.removeAtom(fromAtom);
					toAtomSet.removeAtom(toAtom);
					orphanLink.setFromSet(fromAtomSet.getAtomIDs());
					orphanLink.setToSet(toAtomSet.getAtomIDs());
					
					CMLLink link = new CMLLink();
					LinkTool linkTool = LinkTool.getOrCreateTool(link);
					linkTool.addSingleAtomsToSets(fromAtom, toAtom);
					cmlMap.addLink(link);
					link.setTitle("de-orphan");
					break;
				}
			}
			if (match) break;
		}
		return match;
	}

	private boolean doLigandsMatch(CMLAtom fromAtom, CMLAtom toAtom) {
		boolean match = false;
		List<CMLAtom> fromLigands = fromAtom.getLigandAtoms();
		List<CMLAtom> toLigands = toAtom.getLigandAtoms();
		if (fromLigands.size() == toLigands.size()) {
			for (CMLAtom fromLigand : fromLigands) {
				match = false;
				CMLAtomSet toAtomSet = from2ToAtomAtomMap.get(fromLigand);
				if (toAtomSet != null) {
					for (CMLAtom toLigand : toLigands) {
						if (toAtomSet.contains(toLigand)) {
							match = true;
							break;
						}
					}
				}
				if (!match) break;
			}
		}
		return match;
	}

	private void debugLinks(String title, List<CMLLink> linkList) {
		if (linkList.size() > 0) {
			System.out.println(title);
			for (CMLLink link : linkList) {
				link.debug();
			}
		}
	}

	private boolean deOrphanizeSingleToFrom(List<CMLLink> orphanList) {
		boolean change = false;
		List<CMLLink> deOrphanList = new ArrayList<CMLLink>();
		for (CMLLink link : orphanList) {
			if (LinkTool.getLinkSetLength(link, Direction.TO) == 1 &&
				LinkTool.getLinkSetLength(link, Direction.FROM) == 1) {
				LOG.trace("de-orphanising");
				change = true;
				link.setTitle("de-"+link.getTitle());
				deOrphanList.add(link);
			}
		}
		orphanList.removeAll(deOrphanList);
		return change;
	}

	private List<CMLLink> makeUnequalToFromList(CMLElements<CMLLink> links) {
		List<CMLLink> unequalList = new ArrayList<CMLLink>();
		for (CMLLink link : links) {
			if (!link.getTitle().startsWith(ORPHAN) && 
				link.getToSet().length != link.getFromSet().length) {
				unequalList.add(link);
			}
		}
		return unequalList;
	}

	private List<CMLLink> makeOrphanList(CMLElements<CMLLink> links) {
		List<CMLLink> orphanList = new ArrayList<CMLLink>();
		for (CMLLink link : links) {
			if (link.getTitle().startsWith(ORPHAN)) {
				orphanList.add(link);
			}
		}
		return orphanList;
	}

	void addMissingIds() {
		CMLElements<CMLLink> links = cmlMap.getLinkElements();
		Map<ChemicalElement, List<String>> idListByElementMap0 = 
			getIdListByChemicalElement(links,  Direction.FROM,  atomTreeData0.atomSet);
		Map<ChemicalElement, List<String>> idListByElementMap1 = 
			getIdListByChemicalElement(links,  Direction.TO,  atomTreeData1.atomSet);
		Set<ChemicalElement> elementSet0 = idListByElementMap0.keySet();
		LOG.trace("set0 "+elementSet0.size());
		Set<ChemicalElement> elementSet1 = idListByElementMap1.keySet();
		LOG.trace("set1 "+elementSet1.size());
		addOrphanLinks(idListByElementMap0, idListByElementMap1, elementSet0, Direction.FROM);
		elementSet1.removeAll(elementSet0);
		addOrphanLinks(idListByElementMap1, idListByElementMap0, elementSet1, Direction.TO);
	}

	private void addOrphanLinks(
		Map<ChemicalElement, List<String>> idListByElementMap0,
		Map<ChemicalElement, List<String>> idListByElementMap1,
		Set<ChemicalElement> elementSet0, Direction direction) {
		for (ChemicalElement elem : elementSet0) {
			List<String> list0 = idListByElementMap0.get(elem);
			List<String> list1 = idListByElementMap1.get(elem);
			CMLLink link = new CMLLink();
			List<String> fromList = (direction.equals(Direction.FROM)) ? list0 : list1;
			List<String> toList = (direction.equals(Direction.FROM)) ? list1 : list0;
			if (fromList != null) {
				link.setFromSet(fromList.toArray(new String[0]));
			}
			if (toList != null) {
				link.setToSet(toList.toArray(new String[0]));
			}
			link.setTitle(ORPHAN+" "+elem.getSymbol());
			cmlMap.addLink(link);
		}
	}

	private Map<ChemicalElement, List<String>> getIdListByChemicalElement(
			CMLElements<CMLLink> links, Direction direction, CMLAtomSet atomSet) {
		Map<ChemicalElement, List<String>> idListByElementMap = new HashMap<ChemicalElement, List<String>>();
		List<String> orphanList = getAtomsWithoutLinks(links, direction, atomSet);
		for (String id : orphanList) {
			CMLAtom atom = atomSet.getAtomById(id);
			ChemicalElement elem = atom.getChemicalElement();
			List<String> idList = idListByElementMap.get(elem);
			if (idList == null) {
				idList = new ArrayList<String>();
				idListByElementMap.put(elem, idList);
			}
			idList.add(id);
		}
		return idListByElementMap;
	}

	private List<String> getAtomsWithoutLinks(CMLElements<CMLLink> links, Direction direction, CMLAtomSet atomSet) {
		Map<String, List<CMLLink>> linkListMap = getLinkListById(links, direction);
		List<String> missing = new ArrayList<String>();
		List<CMLAtom> atoms = atomSet.getAtoms();
		for (CMLAtom atom : atoms) {
			String id = atom.getId();
			if (!linkListMap.containsKey(id)) {
				missing.add(id);
			}
		}
		return missing;
	}

	private void resolveAmbiguousLinks() {
		CMLElements<CMLLink> links = cmlMap.getLinkElements();
		Map<String, List<CMLLink>> fromLinks = getAmbiguousLinksById(links, Direction.FROM);
		Map<String, List<CMLLink>> toLinks = getAmbiguousLinksById(links, Direction.TO);
	}

	private Map<String, List<CMLLink>> getAmbiguousLinksById(CMLElements<CMLLink> links, Direction direction) {
		Map<String, List<CMLLink>> linkListMap = getLinkListById(links, direction);
		List<String> uniqueIds = getUniqueIdsAsInOneLinkAndEqualFromToSets(
				direction, linkListMap);
		for (String id : uniqueIds) {
			linkListMap.remove(id);
		}
//		ToolUtils.debugMap("Ambig "+direction, linkListMap);
		return linkListMap;
	}

	private Map<String, List<CMLLink>> getLinkListById(
			CMLElements<CMLLink> links, Direction direction) {
		Map<String, List<CMLLink>> linkListMap = new HashMap<String, List<CMLLink>>();
		for (CMLLink link : links) {
			String[] ids = (direction.equals(Direction.FROM)) ? link.getFromSet() : link.getToSet();
			for (String id : ids) {
				List<CMLLink> linkList = linkListMap.get(id);
				if (linkList == null) {
					linkList = new ArrayList<CMLLink>();
					linkListMap.put(id, linkList);
				}
				linkList.add(link);
			}
		}
		return linkListMap;
	}

	private List<String> getUniqueIdsAsInOneLinkAndEqualFromToSets(
			Direction direction, Map<String, List<CMLLink>> linkListMap) {
		List<String> uniqueIds = new ArrayList<String>();
		for (String id : linkListMap.keySet()) {
			List<CMLLink> linkList = linkListMap.get(id);
			if (linkList.size() == 1) {
				CMLLink link = linkList.get(0);
				String[] sourceIds = (direction.equals(Direction.FROM)) ? link.getFromSet() : link.getToSet();
				String[] targetIds = (direction.equals(Direction.FROM)) ? link.getToSet() : link.getFromSet();
				if (sourceIds.length == targetIds.length) {
					uniqueIds.add(id);
				}
			}
		}
		return uniqueIds;
	}

	private Set<String> mapUniqueAtomsByTreeString(String title) {
		CMLLink cmlLink;
		Set<String> uniqueAtomTreeStringSet = new HashSet<String>();
		for (String atomTreeString : atomTreeData0.atomSetByAtomTreeString.keySet()) {
			CMLAtomSet atomSetx0 = atomTreeData0.atomSetByAtomTreeString.get(atomTreeString);
			CMLAtomSet atomSetx1 = atomTreeData1.atomSetByAtomTreeString.get(atomTreeString);
			cmlLink = null;
			if (atomSetx1 == null || atomSetx1.size() == 0) {
				// do nothing
			} else if (atomSetx1.size() == atomSetx0.size()) {
				String elementType  = getElementTypeFrom(atomSetx0, atomSetx1);
				cmlLink = LinkTool.makeLink(title+" "+elementType, atomSetx0, atomSetx1);
			} else {
//				atomSetx0.debug("unequal set 0");
//				atomSetx1.debug("unequal set 1");
//				LOG.info(
//					"BUG: Unequal atomSets for link in AtomTreeMatching");
			}
			if (cmlLink != null) {
				cmlMap.addUniqueLink(cmlLink, CMLMap.Direction.NEITHER);
				uniqueAtomTreeStringSet.add(atomTreeString);
			}
		}
		atomTreeData0.removeUniqueAtoms(uniqueAtomTreeStringSet);
		atomTreeData1.removeUniqueAtoms(uniqueAtomTreeStringSet);
		return uniqueAtomTreeStringSet;
	}

	private String getElementTypeFrom(CMLAtomSet atomSetx0, CMLAtomSet atomSetx1) {
		String elementType = null;
		elementType = getElementTypeFromAtomSet(atomSetx0, elementType);
		elementType = getElementTypeFromAtomSet(atomSetx1, elementType);
		return elementType;
	}

	private String getElementTypeFromAtomSet(CMLAtomSet atomSetx0,
			String elementType) {
		for (CMLAtom atom : atomSetx0.getAtoms()) {
			String newElementType = atom.getElementType();
			if (elementType == null) {
				elementType = newElementType;
			} else if (!elementType.equals(newElementType)){
				LOG.error("atomSets : "+elementType+" != "+newElementType);
//				throw new RuntimeException("atomSets : "+elementType+" != "+newElementType);
			}
		}
		return elementType;
	}

	private void mapByUniqueLargestCommonAtomTreeStrings() {
		atomTreeData0.makeSortedListAndAtomSetValues();
		List<String> sortedAtomTreeString0 = atomTreeData0.makeSortedList();
		List<String> sortedAtomTreeString1 = atomTreeData1.makeSortedList();
		List<String> sortedAtomSetValues0 = atomTreeData0.makeSortedAtomSetValues();
		List<String> sortedAtomSetValues1 = atomTreeData1.makeSortedAtomSetValues();
		
//		LOG.debug("sortedAtomTreeString0 "+sortedAtomTreeString0.size());
//		LOG.debug("sortedAtomTreeString1 "+sortedAtomTreeString1.size());
//		LOG.debug("sortedAtomSetValues0 "+sortedAtomSetValues0.size());
//		LOG.debug("sortedAtomSetValues1 "+sortedAtomSetValues1.size());
		
		IntMatrix intMatrix = AtomTree.createSimilarityMatrix(sortedAtomTreeString0, sortedAtomTreeString1);
		LOG.trace("IM "+intMatrix);
		List<Integer> largestIndexList = IntMatrix.findLargestUniqueElementsInRowColumn(intMatrix);
		addMatrixElementsToMap(
				largestIndexList, sortedAtomSetValues0, sortedAtomSetValues1);
		atomTreeData0.removeUniqueElementsFromMap(0, largestIndexList);
		atomTreeData1.removeUniqueElementsFromMap(1, largestIndexList);
	}

	private void addUniqueLigandsToUniqueAtoms() {
		int safetyCount = SAFETY;
		while (safetyCount-- > 0) {
			atomTreeData0.makeLinkedAndUnlinkedAtomSets(CMLMap.Direction.FROM, cmlMap);
			atomTreeData1.makeLinkedAndUnlinkedAtomSets(CMLMap.Direction.TO, cmlMap);
			if (!expandLigandsFromAtomSets()) break;
		}
	}

	private boolean expandLigandsFromAtomSets() {
		
		MapTool mapTool = MapTool.getOrCreateTool(cmlMap);
		Map<String, String> fromSetValue2ToSetValueMap = mapTool.getFromSetToSetMap(Direction.FROM);
		List<CMLAtomSet> fromSets = atomTreeData0.getSetsFromKeys(fromSetValue2ToSetValueMap);
		List<CMLAtomSet> ligandSetList0 = atomTreeData0.getNonUniqueLigandSetList();
		List<CMLAtomSet> ligandSetList1 = atomTreeData1.getNonUniqueLigandSetList();
		List<Set<String>> matchedTargetSetList = createSetOfTargetIdsForEachUnmatchedAtomSet(
				fromSetValue2ToSetValueMap, fromSets, ligandSetList0);
		boolean change = mapTargetLigandsOntoFromLigandsAndUpdateMap(
				ligandSetList1, matchedTargetSetList);
		return change;
	}

	private boolean mapTargetLigandsOntoFromLigandsAndUpdateMap(
			List<CMLAtomSet> ligandSetList1,
			List<Set<String>> matchedTargetSetList) {
		boolean change = false;
		for (int i = 0; i < matchedTargetSetList.size(); i++) {
			String[] matchedIds = matchedTargetSetList.get(i).toArray(new String[0]);
			CMLAtomSet matchedSet = AtomSetTool.createAtomSet(atomTreeData1.atomSet, matchedIds);
			for (int j = 0; j < ligandSetList1.size(); j++) {
				CMLAtomSet ligandSet1 = ligandSetList1.get(j);
				if (ligandSet1 != null) {
					if (matchedSet.size() == ligandSet1.size()) {
						if (ligandSet1.size() > 0 && ligandSet1.complement(matchedSet).size() == 0) {
							String fromSetS = atomTreeData0.currentAtomSetValueList.get(i);
							String toSetS = atomTreeData1.currentAtomSetValueList.get(j);
							addFromSetToSetLink(fromSetS, toSetS, COMMON_LIGANDS);
							atomTreeData0.removeAtoms(fromSetS);
							atomTreeData1.removeAtoms(toSetS);
							change = true;
							break;
						}
					}
				}
			}
		}
		LOG.trace("....... change "+change);
		return change;
	}

	private List<Set<String>> createSetOfTargetIdsForEachUnmatchedAtomSet(
			Map<String, String> fromSetValue2ToSetValueMap,
			List<CMLAtomSet> fromSets, List<CMLAtomSet> ligandSetList0) {
		List<Set<String>> matchedTargetSetList = new ArrayList<Set<String>>();
		for (int i = 0, max = ligandSetList0.size(); i < max; i++) {
			CMLAtomSet ligandSet0 = ligandSetList0.get(i);
			if (ligandSet0 != null) {
				List<CMLAtom> ligands0 = ligandSet0.getAtoms();
				Set<String> targetAtomSetValueSet = new HashSet<String>();
				String toSetValue = null;
				for (CMLAtom ligand0 : ligands0) {
					for (CMLAtomSet fromSet : fromSets) {
						if (fromSet.contains(ligand0)) {
							String fromSetValue = fromSet.getValue();
							toSetValue = fromSetValue2ToSetValueMap.get(fromSetValue);
							break;
						}
					}
					if (toSetValue == null) {
						LOG.error("**********Cannot find match for "+ligand0.getId());
						targetAtomSetValueSet = null;
						break;
					} else {
						// currently flatten atomSets
						String[] ids = toSetValue.split(CMLConstants.S_WHITEREGEX);
						for (String id : ids) {
							targetAtomSetValueSet.add(id);
						}
					}
				}
				matchedTargetSetList.add(targetAtomSetValueSet);
			}
		}
		return matchedTargetSetList;
	}

	private void addMatrixElementsToMap(
			List<Integer> largestIndexList,
			List<String> sortedAtomTreeStringi,
			List<String> sortedAtomTreeStringj) {
		for (int jcol = 0; jcol < largestIndexList.size(); jcol++) {
			int irow = largestIndexList.get(jcol);
			if (irow > -1) {
				String si = sortedAtomTreeStringi.get(irow);
				String sj = sortedAtomTreeStringj.get(jcol);
				addFromSetToSetLink(si, sj, COMMON_ATOMTREE);
			}
		}
//		cmlMap.debug("LINK");
	}

	private CMLLink addFromSetToSetLink(String si, String sj, String title) {
		CMLLink link = new CMLLink();
		link.setFromSet(si);
		link.setToSet(sj);
		cmlMap.addLink(link);
		CMLAtomSet fromSet = AtomSetTool.createAtomSet(atomTreeData0.atomSet, si.split(CMLConstants.S_WHITEREGEX));
		CMLAtomSet toSet = AtomSetTool.createAtomSet(atomTreeData1.atomSet, sj.split(CMLConstants.S_WHITEREGEX));
		boolean balanced = fromSet.getAtoms().size() == toSet.getAtoms().size();
		String balancedS = (balanced) ? BALANCED : UNBALANCED;
		String elementType = getElementTypeFrom(fromSet, toSet);
		if (elementType == null) {
			fromSet.debug("FROM");
			toSet.debug("TO");
			throw new RuntimeException("inconsistent atom types");
		}
		link.setTitle(balancedS+" "+title+" "+elementType);
		return link;
	}


}
