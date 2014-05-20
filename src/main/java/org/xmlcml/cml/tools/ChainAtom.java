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

import java.util.List;
import java.util.Set;

import org.xmlcml.cml.element.CMLAtom;

public class ChainAtom {
	@SuppressWarnings("unused")
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
