package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class RingNucleusSet extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());

	private SortedSet<RingNucleus> nucleusSet;
	private Map<Sprout, List<RingNucleus>> sproutMap;
	private Map<CMLAtom, RingNucleus> atomMap;
	private Map<CMLBond, RingNucleus> bondMap;
	private List<RingNucleus> ringNucleusList;
	private Map<CMLBond, Sprout> bondSproutMap;
	private MoleculeDraw moleculeDraw;
	private MoleculeDrawParameters drawParameters;
	
	/**
	 */
	public RingNucleusSet() {
		init();
	}
	
	private void init() {
		this.nucleusSet = new TreeSet<RingNucleus>();
		this.sproutMap = new HashMap<Sprout, List<RingNucleus>>();
		this.atomMap = new HashMap<CMLAtom, RingNucleus>();
		this.bondMap = new HashMap<CMLBond, RingNucleus>();
		this.bondSproutMap = new HashMap<CMLBond, Sprout>();
	}

	/** adds new chain and indexes
	 * indexes on atoms, bond
	 * @param ringNucleus
	 */
	public void addRingNucleus(RingNucleus ringNucleus) {
		if (!nucleusSet.contains(ringNucleus)) {
			nucleusSet.add(ringNucleus);
			CMLAtomSet atomSet = ringNucleus.getAtomSet();
			for (CMLAtom atom : atomSet.getAtoms()) {
				if (atomMap.get(atom) != null) {
					throw new CMLRuntimeException("atom in two ringNuclei: "+atom.getId());
				}
				atomMap.put(atom, ringNucleus);
			}
			CMLBondSet bondSet = ringNucleus.getBondSet();
			for (CMLBond bond : bondSet.getBonds()) {
				if (bondMap.get(bond) != null) {
					throw new CMLRuntimeException("bond in two ringNuclei: "+bond.getId());
				}
				bondMap.put(bond, ringNucleus);
			}
		}
	}
	
	/** calculate sprouts on ring nucleus and index them
	 * 
	 * @param omitHydrogens
	 */
	public void findSprouts(boolean omitHydrogens) {
		for (RingNucleus ringNucleus : nucleusSet) {
			ringNucleus.findSprouts(omitHydrogens);
			for (Sprout sprout : ringNucleus.getSproutList(omitHydrogens)) {
				List<RingNucleus> ringNucleusList = sproutMap.get(sprout);
				if (ringNucleusList == null) {
					ringNucleusList = new ArrayList<RingNucleus>();
					sproutMap.put(sprout, ringNucleusList);
				}
				if (!ringNucleusList.contains(ringNucleus)) {
					ringNucleusList.add(ringNucleus);
				}
			}
		}
	}
	
	/**
	 * @param ringNucleus
	 * @return success
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(RingNucleus ringNucleus) {
		return nucleusSet.add(ringNucleus);
	}

	/**
	 * @param ringNucleus
	 * @return containment
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object ringNucleus) {
		return nucleusSet.contains(ringNucleus);
	}

	/**
	 * @return first in order
	 * @see java.util.SortedSet#first()
	 */
	public RingNucleus first() {
		return nucleusSet.first();
	}

	/**
	 * @return iterator
	 * @see java.util.Set#iterator()
	 */
	public Iterator<RingNucleus> iterator() {
		return nucleusSet.iterator();
	}

	/**
	 * @return last in order
	 * @see java.util.SortedSet#last()
	 */
	public RingNucleus last() {
		return nucleusSet.last();
	}

	/**
	 * @param ringNucleus
	 * @return success
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object ringNucleus) {
		return nucleusSet.remove(ringNucleus);
	}
	
	/**
	 * @return list
	 */
	public List<RingNucleus> getRingNucleusList() {
		this.ringNucleusList = new ArrayList<RingNucleus>();
		Iterator<RingNucleus> iterator = iterator();
		for (; iterator.hasNext(); ) {
			ringNucleusList.add(iterator.next());
		}
		return ringNucleusList;
	}
	
	/**
	 * 
	 * @param i
	 * @return nucleus
	 */
	public RingNucleus get(int i) {
		return getRingNucleusList().get(i);
	}

	/**
	 * @return size
	 * @see java.util.Set#size()
	 */
	public int size() {
		return nucleusSet.size();
	}

	/**
	 * @return the atomMap
	 */
	public Map<CMLAtom, RingNucleus> getAtomMap() {
		return atomMap;
	}

	/**
	 * @return the bondMap
	 */
	public Map<CMLBond, RingNucleus> getBondMap() {
		return bondMap;
	}

	/**
	 * @return the nucleusSet
	 */
	public Set<RingNucleus> getRingNucleusSet() {
		return nucleusSet;
	}

	/**
	 * @return the sproutMap
	 */
	public Map<Sprout, List<RingNucleus>> getSproutMap() {
		return sproutMap;
	}
	/**
	 * @return string
	 */
	public String toString() {
		String s = "chain";
		return s;
	}

	/**
	 * @return the bondSproutMap
	 */
	public Map<CMLBond, Sprout> getBondSproutMap() {
		return bondSproutMap;
	}

	/**
	 * @return the nucleusSet
	 */
	public SortedSet<RingNucleus> getNucleusSet() {
		return nucleusSet;
	}

	/**
	 * @return the drawParameters
	 */
	public MoleculeDrawParameters getDrawParameters() {
		return drawParameters;
	}

	/**
	 * @param drawParameters the drawParameters to set
	 */
	public void setDrawParameters(MoleculeDrawParameters drawParameters) {
		this.drawParameters = drawParameters;
		for (RingNucleus nucleus : nucleusSet) {
			nucleus.setMoleculeDraw(moleculeDraw);
		}
	}

	/**
	 * @return the moleculeDraw
	 */
	public MoleculeDraw getMoleculeDraw() {
		return moleculeDraw;
	}

	/**
	 * @param moleculeDraw the moleculeDraw to set
	 */
	public void setMoleculeDraw(MoleculeDraw moleculeDraw) {
		this.moleculeDraw = moleculeDraw;
	}

}

