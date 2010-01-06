package org.xmlcml.cml.tools;

import java.util.List;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;

public class MorganAtomMatcher extends AtomMatcher {

	public MorganAtomMatcher() {
		super();
	}
	
	public CMLMap match(CMLAtomSet atomSet0, CMLAtomSet atomSet1, String title) {
		CMLLink cmlLink;
		CMLMap cmlMap = makeMap();
		Morgan morgan0 = new Morgan(atomSet0);
		List<Long> morganList0 = morgan0.getMorganList();
		List<CMLAtomSet> atomSetList0 = morgan0.getAtomSetList();
		Morgan morgan1 = new Morgan(atomSet1);
		List<Long> morganList1 = morgan1.getMorganList();
		List<CMLAtomSet> atomSetList1 = morgan1.getAtomSetList();
		if (morganList0.size() != morganList1.size()) {
			;// may be different as atomSets may have different
			// equivalences
		} else {
			for (int i = 0; i < morganList0.size(); i++) {
				if (morganList0.get(i).intValue() != morganList1.get(i)
						.intValue()) {
					throw new RuntimeException(
							"morgan numbers do not match; (Matching non-identical atomSets?)"
									+ " Failed.... " + i);
				}
				CMLAtomSet atomSetx0 = atomSetList0.get(i);
				CMLAtomSet atomSetx1 = atomSetList1.get(i);
				List<CMLAtom> atoms0 = atomSetx0.getAtoms();
				List<CMLAtom> atoms1 = atomSetx1.getAtoms();
				// atom sets are same size so we have an equivalence
				if (atomSetx0.size() == atomSetx1.size()) {
					cmlLink = new CMLLink();
					cmlLink.setTitle(title + CMLConstants.S_SPACE + atomSetx0.size());
					// if single atom we have exact match so add as link
					// if annotation is required add it outside
					if (atomSetx0.size() == 1) {
						cmlLink.setFrom(atoms0.get(0).getId());
						cmlLink.setTo(atoms1.get(0).getId());
					} else {
						String[] atomSet0S = atomSetx0.getXMLContent();
						String[] atomSet1S = atomSetx1.getXMLContent();
						cmlLink.setFromSet(atomSet0S);
						cmlLink.setToSet(atomSet1S);
					}
					cmlMap.addUniqueLink(cmlLink, CMLMap.Direction.EITHER);
				} else {
					throw new RuntimeException(
							"atom sets wrong size in Morga");
					// mismatched atom sets - match fails
				}
			}
		}
		return cmlMap;
	}

	
}
