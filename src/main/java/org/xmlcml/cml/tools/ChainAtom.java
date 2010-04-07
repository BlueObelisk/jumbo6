package org.xmlcml.cml.tools;

import java.util.List;
import java.util.Set;

import org.xmlcml.cml.element.CMLAtom;

public class ChainAtom {
	private CMLAtom atom;
	
	private ChainAtom(CMLAtom atom) {
		this.atom = atom;
	}
	
	public static ChainAtom createAtom(CMLAtom atom, Set<CMLAtom> acyclicAtomSet) {
		ChainAtom chainAtom = null;
		List<CMLAtom> ligands = atom.getLigandAtoms();
		if (ligands.size() <= 2) {
			chainAtom = new ChainAtom(atom);
			for (CMLAtom ligand : ligands) {
				if (!acyclicAtomSet.contains(ligand)) {
					chainAtom = null;
					break;
				}
			}
		}
		return chainAtom;
	}
}
