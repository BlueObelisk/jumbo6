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

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Transform3;

/**
 * represents a pair of molecules (often matched and aligned).
 */
public class MoleculePair implements CMLConstants {

    private CMLMolecule[] molecules = new CMLMolecule[2];
	private CMLMap map;
	private Transform3 transform3;
	/**
	 * @return the atoms
	 */
	public CMLMolecule[] getMolecules() {
		return molecules;
	}

	/**
	 * @param molecule0
	 * @param molecule1
	 */
	public MoleculePair(CMLMolecule molecule0, CMLMolecule molecule1) {
		this.molecules = new CMLMolecule[2];
		this.molecules[0] = molecule0;
		this.molecules[1] = molecule1;
	}

    /**
     * get first atom.
     * 
     * @return atom
     */
    public CMLMolecule getMolecule1() {
        return molecules[0];
    }

    /**
     * get second atom.
     * 
     * @return atom
     */
    public CMLMolecule getMolecule2() {
        return molecules[1];
    }

    /**
     * to string.
     * 
     * @return the string
     */
    public String toString() {
        StringBuffer s = new StringBuffer(S_LCURLY);
        s.append((molecules[0].getId() == null) ? "null" : molecules[0].getId());
        s.append(S_SLASH);
        s.append((molecules[1].getId() == null) ? "null" : molecules[1].getId());
        s.append(S_RCURLY);
        return s.toString();
    }

	public CMLMap getMap() {
		return map;
	}

	public void setMap(CMLMap map) {
		this.map = map;
	}

	public Transform3 getTransform3() {
		return transform3;
	}

	public void setTransform3(Transform3 transform3) {
		this.transform3 = transform3;
	}

}
