package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlcml.cml.element.CMLAtom;

/**
 * divalentChain
 * @author pm286
 *
 */
public class AcyclicChain {
	public AcyclicChain() {
		
	}
	
	public static List<AcyclicChain> makeChainList(Set<CMLAtom> acyclicAtomSet) {
		Set<CMLAtom> unusedAtoms = new HashSet<CMLAtom>();
		for (CMLAtom atom : acyclicAtomSet) {
			unusedAtoms.add(atom);
		}
		List<AcyclicChain> chainList = new ArrayList<AcyclicChain>();
		for (CMLAtom atom : acyclicAtomSet) {
			addAtom(atom, chainList, unusedAtoms);
		}
		return chainList;
	}

	private static void addAtom(CMLAtom atom, List<AcyclicChain> chainList, Set<CMLAtom> acyclicAtomSet) {
//		List<CMLAtom> ligands = AtomTool.getOrCreateTool(atom).getNonHydrogenLigandList();
//		if (ligands.size() <= 2) {
//			AcyclicChain first = null;
//			for (CMLAtom ligand : ligands) {
//				for (AcyclicChain chain : chainList) {
//					if (chain.contains(ligand)) {
//						if (first != null) {
//							join(first, chain)
//						}
//					}
//						joined = true;
//					}
//				}
//			}
//		}
	}
	
}
