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

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
/**
 * an atom belonging to 2 or more rings
 * 
 * @author pmr
 * 
 */
public class BridgeAtom extends AbstractTool {
	final static Logger LOG = Logger.getLogger(BridgeAtom.class);
	private CMLAtom atom;
	private List<Junction> junctionList;
	private Set<Ring> ringSet;

	/** constructor.
	 * copies reference to sets
	 * @param atom
	 * @param junction
	 */
	public BridgeAtom(Junction junction, CMLAtom atom) {
		this.atom = atom;
		this.junctionList = new ArrayList<Junction>();
		addJunction(junction);
		ringSet = new HashSet<Ring>();
		ringSet.add(junction.getRingList().get(0));
		ringSet.add(junction.getRingList().get(1));
	}
	
	/** add ring
	 * @param ring
	 */
	public void addRing(Ring ring) {
		ringSet.add(ring);
	}

	/**
	 * @return the atom
	 */
	public CMLAtom getAtom() {
		return atom;
	}

	/**
	 * @return the junction
	 */
	public List<Junction> getJunctionList() {
		return junctionList;
	}

	/**
	 * @return the ringSet
	 */
	public Set<Ring> getRingSet() {
		return ringSet;
	}
	/**
	 * @param junction
	 */
	public void addJunction(Junction junction) {
		if (junctionList == null) {
			junctionList = new ArrayList<Junction>();
		}
		junctionList.add(junction);
	}
}
