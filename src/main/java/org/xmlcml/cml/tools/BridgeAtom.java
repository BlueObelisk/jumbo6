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
	final static Logger logger = Logger.getLogger(BridgeAtom.class.getName());
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
