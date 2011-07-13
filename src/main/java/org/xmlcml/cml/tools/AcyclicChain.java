/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
