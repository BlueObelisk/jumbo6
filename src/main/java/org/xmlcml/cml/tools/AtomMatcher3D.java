package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Transform3;

public class AtomMatcher3D extends AtomMatcher {

	public CMLMap match(
			CMLAtomSet atomSet0,CMLAtomSet atomSet1, String title) {
		CMLMap cmlMap = makeMap();
		throw new RuntimeException("NYI");
	}
 
    /** pairwise atom-atom alignment of molecules.
     * 
     * @param identicalMoleculeList list of identical molecules (e.g. by morgan)
     * @return upper triangle of transformations from mol(j) onto mol(i)
     */
	private static List<List<MoleculePair>> matchAndAlignMolecules(
			List<CMLMolecule> identicalMoleculeList) {
		AtomMatcher atomMatcher = new MorganAtomMatcher();
//		atomMatcher.setAtomMatchStrategy(Strategy.MATCH_MORGAN);
		CMLMap[][] mapMatrix = atomMatcher.getMoleculeMatch(identicalMoleculeList, identicalMoleculeList);
		List<List<MoleculePair>> moleculePairListList = new ArrayList<List<MoleculePair>>();
		int i = 0;
		for (CMLMap[] mapx : mapMatrix) {
			List<MoleculePair> moleculePairList = new ArrayList<MoleculePair>();
			moleculePairListList.add(moleculePairList);
			int j = 0;
			for (CMLMap map : mapx) {
				if (j > i) {
					MoleculeTool moleculeTooli = MoleculeTool.getOrCreateTool(identicalMoleculeList.get(i));
					MoleculePair moleculePair = moleculeTooli.fitToMoleculeTool(map, identicalMoleculeList.get(j));
					moleculePairList.add(moleculePair);
				}
				j++;
			}
			i++;
		}
		return moleculePairListList;
	}

	public CMLMap match(CMLAtomSet atomSet0, CMLAtomSet atomSet1, Transform3 t3) {
		throw new RuntimeException("NYI");
	}
}
