package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Util;
/**
 * tool to support a ring. not fully developed
 * all rings are indexed to provide cyclic list of bonds
 * 
 * @author pmr
 * 
 */
public class Ring extends AbstractTool implements Comparable<Ring> {
	final static Logger logger = Logger.getLogger(Ring.class.getName());
	private CMLAtomSet atomSet;
	private CMLBondSet bondSet;
	// cyclic array of bonds
	private CyclicBondList cyclicBondList;
	// cyclic array of atoms
	private CyclicAtomList cyclicAtomList;
	private Map<CMLAtom, List<CMLBond>> atomToBondMap;
	private String cyclicAtomIdString = null;
	private CMLBond canonicalBond = null;
	private CMLAtom canonicalAtom = null;
	private Map<Ring, List<Junction>> junctionMap = null;
	private MoleculeLayout moleculeDraw;
	private Map<CMLAtom, Real2> atomCoordMap;
	
	private static double RINGSCALE = 0.75;
	
	/** constructor.
	 * copies reference to sets
	 * 
	 * @param atomSet
	 * @param bondSet
	 */
	public Ring(CMLAtomSet atomSet,	CMLBondSet bondSet) {
		this.atomSet = atomSet;
		this.bondSet = bondSet;
		if (atomSet.size() != bondSet.size()) {
			throw new CMLRuntimeException("atomSet and bondset different sizes");
		}
		makeCyclicLists();
	}

	/** create from cyclic bonds.
	 * 
	 * @param bondSet
	 */
	public Ring(CMLBondSet bondSet) {
		this(bondSet.getAtomSet(), bondSet);
	}
	
	private void makeCyclicLists() {
		atomToBondMap = new HashMap<CMLAtom, List<CMLBond>>();
		List<CMLAtom> atoms = atomSet.getAtoms();
		List<CMLBond> bonds = bondSet.getBonds();
		int nb = 0;
		for (CMLAtom atom : atoms) {
			for (CMLBond bond : bonds) {
				nb = indexBondsByAtom(bond, atom, atomToBondMap);
				if (nb== 2) {
					break;
				}
			}
		}
		if (nb != 2) {
			throw new CMLRuntimeException("Bad list "+nb);
		}
		orderAtomsInCycle();
		cyclicAtomList.canonicalize();
		cyclicBondList.canonicalize();
	}
	
	private int indexBondsByAtom(CMLBond bond, CMLAtom atom, Map<CMLAtom, List<CMLBond>> atomToBondMap) {
		int i = 0;
		if (bond.getAtom(0).equals(atom) || bond.getAtom(1).equals(atom)) {
			List<CMLBond> bonds = atomToBondMap.get(atom);
			if (bonds == null) {
				bonds = new ArrayList<CMLBond>();
				atomToBondMap.put(atom, bonds);
			}
			bonds.add(bond);
			i = bonds.size();
		}
		return i;
	}

	private void orderAtomsInCycle() {
		cyclicAtomList = new CyclicAtomList();
		cyclicBondList = new CyclicBondList();
		// start at any atom
		CMLAtom currentAtom = atomSet.getAtoms().get(0);
		for (int i = 0; i < atomSet.size(); i++) {
			currentAtom = addAtomAndBond(currentAtom);
		}
		getCyclicAtomList();
		getCyclicBondList();
	}

	private CMLAtom addAtomAndBond(CMLAtom currentAtom) {
		List<CMLBond> bonds = atomToBondMap.get(currentAtom);
		CMLBond currentBond = bonds.get(0);
		bonds.remove(currentBond);
		cyclicAtomList.add(currentAtom);
		cyclicBondList.add(currentBond);
		currentAtom = currentBond.getOtherAtom(currentAtom);
		bonds = atomToBondMap.get(currentAtom);
		bonds.remove(currentBond);
		return currentAtom;
	}
	
	
	/**
	 * @return the atomSet
	 */
	public CMLAtomSet getAtomSet() {
		return atomSet;
	}

	/**
	 * @return the bondSet
	 */
	public CMLBondSet getBondSet() {
		return bondSet;
	}

	/**
	 * @return the cyclicBondList
	 */
	public CyclicList<CMLBond> getCyclicBondList() {
		return cyclicBondList;
	}

	/**
	 * @return the cyclicAtomList
	 */
	public CyclicAtomList getCyclicAtomList() {
		return cyclicAtomList;
	}
	
	void add(Ring ring, Junction junction) {
		if (junctionMap == null) {
			junctionMap = new HashMap<Ring, List<Junction>>();
		}
		List<Junction> junctionList = junctionMap.get(ring);
		if (junctionList == null) {
			junctionList = new ArrayList<Junction>();
			junctionMap.put(ring, junctionList);
		}
		// only add one copy
		if (!junctionList.contains(junction)) {
			junctionList.add(junction);
		}
	}
	
	/** add coordinates to isolated ring
	 * actually adds coordinates
	 * @param moleculeDraw
	 */
	public void calculate2DCoordinates(MoleculeLayout moleculeDraw) {
		ensureAtomCoordMap();
		this.setMoleculeDraw(moleculeDraw);
		Real2Vector points = Real2Vector.regularPolygon(size(), moleculeDraw.getDrawParameters().getBondLength()*RINGSCALE);
		List<CMLAtom> atoms = this.getCyclicAtomList();
		for (int i = 0; i < atoms.size(); i++) {
			atomCoordMap.put(atoms.get(i), points.get(i));
		}
	}
	
	/**
	 * transfers coordinates to atoms
	 */
	public void updateCoordinates() {
		Set<CMLAtom> atoms = atomCoordMap.keySet();
		for (CMLAtom atom : atoms) {
			atom.setXY2(atomCoordMap.get(atom));
		}
	}
	
	/**
	 * @param scale
	 */
	public void multiplyCoordMap(double scale) {
		for (CMLAtom atom : atomCoordMap.keySet()) {
			Real2 xy2 = atomCoordMap.get(atom);
			xy2.multiplyEquals(scale);
		}
	}
	
	/**
	 * @param trans
	 */
	public void translateCoordMap(Real2 trans) {
		for (CMLAtom atom : atomCoordMap.keySet()) {
			Real2 xy2 = atomCoordMap.get(atom);
			xy2.plusEquals(trans);
		}
	}
	
	/**
	 * @param transform
	 */
	public void transformBy(Transform2 transform) {
		for (CMLAtom atom : atomCoordMap.keySet()) {
			Real2 xy2 = atomCoordMap.get(atom);
			xy2.transformBy(transform);
		}
	}
	
	private void ensureAtomCoordMap() {
		if (atomCoordMap == null) {
			atomCoordMap = new HashMap<CMLAtom, Real2>();
		}
	}

	/** get list of junctions.
	 * normally should only be one element in list
	 * @param ring
	 * @return list of junctions or null
	 */
	public List<Junction> getJunctionList(Ring ring) {
		return (junctionMap == null) ? null : junctionMap.get(ring);
	}
	
	/** get junction map
	 * 
	 * @return map or null
	 */
	public Map<Ring, List<Junction>> getJunctionMap() {
		return junctionMap;
	}

	/**
	 * get the (cyclic) List of atomIds
	 * no duplicates. starts with canonical atom
	 * @return the (cyclic) List of atomIds
	 */
	public List<String> getCyclicAtomIdList() {
		List<String> idList = new ArrayList<String>();
		for (CMLAtom atom : cyclicAtomList) {
			idList.add(atom.getId());
		}
		return idList;
	}

	/** gets canonical string for ring.
	 * 
	 * @return concatenated atoms in cyclicAtomlist
	 */
	public String getCyclicAtomIdString() {
		if (cyclicAtomIdString == null) {
			String[] ss = getCyclicAtomIdList().toArray(new String[0]);
			cyclicAtomIdString = Util.concatenate(ss, S_SPACE);
		}
		return cyclicAtomIdString;
	}

	/**
	 * get the (cyclic) List of bondIds
	 * no duplicates. starts with canonical bond
	 * @return the (cyclic) List of bondIds
	 */
	public List<String> getCyclicBondIdList() {
		List<String> idList = new ArrayList<String>();
		for (CMLBond bond : cyclicBondList) {
			idList.add(bond.getId());
		}
		return idList;
	}

	/** get size.
	 * 
	 * @return size
	 */
	public int size() {
		return bondSet.size();
	}
	
	/** get consistent bond for starting algorithms.
	 * return bond with lowest atomHash()
	 * @return bond
	 */
	public CMLBond getCanonicalStartBond() {
		if (canonicalBond == null) {
			canonicalBond = (CMLBond) cyclicBondList.get(0);
			for (CMLBond bondx : cyclicBondList) {
				if (bondx.atomHash().compareTo(canonicalBond.atomHash()) < 0) {
					canonicalBond = bondx;
				}
			}
		}
		return canonicalBond;
	}

	/** get consistent atom for starting algorithms.
	 * return atom in canonicalBond which does NOT occur in next bond 
	 * i.e. atom which is start and end of cycle
	 * since bonds may be in either direction it might look like:
	 * a3-a1 a3-a7 a2-a7 a1-a2 with a1 as canonical atom
	 * @return atom
	 */
	public CMLAtom getCanonicalStartAtom() {
		if (canonicalAtom == null) {
			getCanonicalStartBond();
			// set the pointer
			cyclicBondList.getIndexOfAndCache(canonicalBond);
			CMLBond nextBond = (CMLBond) cyclicBondList.getNext();
			CMLAtom atom0 = canonicalBond.getAtom(0);
			CMLAtom atom1 = canonicalBond.getAtom(1);
			canonicalAtom = atom1;
			try {
				// atom0 in next bond?
				nextBond.getOtherAtom(atom0);
			} catch (CMLRuntimeException e) {
				// no, use atom0
				canonicalAtom = atom0;
			}
		}
		return canonicalAtom;
	}

	
	/**
	 * @return the atomCoordMap
	 */
	public Map<CMLAtom, Real2> getAtomCoordMap() {
		return atomCoordMap;
	}

	/**
	 * @return the atomToBondMap
	 */
	public Map<CMLAtom, List<CMLBond>> getAtomToBondMap() {
		return atomToBondMap;
	}

	/**
	 * @return the canonicalAtom
	 */
	public CMLAtom getCanonicalAtom() {
		return canonicalAtom;
	}

	/**
	 * @return the canonicalBond
	 */
	public CMLBond getCanonicalBond() {
		return canonicalBond;
	}

	/** get difference between canonical starts.
	 * 
	 * @return atomStart - bondStart
	 */
	public int getCanonicalBondToAtomOffset() {
		getCanonicalStartBond();
		getCanonicalStartAtom();
		int bondIdx = cyclicBondList.getIndexOfAndCache(canonicalBond);
		int atomIdx = cyclicAtomList.getIndexOfAndCache(canonicalAtom);
		return atomIdx - bondIdx;
	}
	
	/** compare rings.
	 * first uses ring sizes, then uses canonicalised cyclicAtomLists
	 * @param theRing
	 * @return -1, 0, 1
	 */
	public int compareTo(Ring theRing) {
		int compare = 0;
		if (this.size() < theRing.size()) {
			compare = -1;
		} else if (this.size() > theRing.size()) {
			compare = 1;
		}
		if (compare == 0) {
			compare = this.getCyclicAtomIdString().compareTo(theRing.getCyclicAtomIdString());
		}
		return compare;
	}
	
	/** debug.
	 * not sure this works
	 */
	public void debug() {
		ensureAtomCoordMap();
		System.out.print("atoms ");
		for (CMLAtom atom : cyclicAtomList) {
			System.out.print(" .. "+atom.getId());
			System.out.print("["+atom.getXY2()+"]");
			System.out.print(" <xy2> ["+atomCoordMap.get(atom)+"]");
		}
		System.out.println();
		if (cyclicAtomList.get(0).getXY2() != null) {
			for (int i = 0; i < cyclicAtomList.size(); i++) {
				int j = (i+1) % cyclicAtomList.size();
				try {
				System.out.print(" .. "+cyclicAtomList.get(i).getXY2().getDistance(cyclicAtomList.get(j).getXY2()));
				} catch (Throwable t) {
					System.err.println("DEBUG"+t);
				}
			}
		}
		System.out.println();
		System.out.print("bonds ");
		for (CMLBond bond : cyclicBondList) {
			System.out.print(" .. "+bond.getId());
		}
		System.out.println();
	}

	/**
	 * @return the moleculeDraw
	 */
	public MoleculeLayout getMoleculeDraw() {
		return moleculeDraw;
	}

	/**
	 * @param moleculeDraw the moleculeDraw to set
	 */
	public void setMoleculeDraw(MoleculeLayout moleculeDraw) {
		this.moleculeDraw = moleculeDraw;
	}

};
/** list supporting cycli structures
 * 
 * @author pm286
 *
 * @param <E>
 */
abstract class CyclicList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -589276046564365497L;
	int counter;
	/** constructor
	 */
	public CyclicList() {
		super();
	}

	/** get index of element
	 * resets counter if found
	 * @param e element to find
	 * @return index or -1
	 */
	public int getIndexOfAndCache(E e) {
		int idx = super.indexOf(e);
		if (idx != -1) {
			counter = idx;
		}
		return idx;
	}

	/** get element in cycle mod size()
	 * @param counter
	 * @return element
	 */
	public E get(int counter) {
		this.counter = counter % size();
		return super.get(counter);
	}
	
	/** get next element in cycle.
	 * increment counter mod size()
	 * @return element
	 */
	public E getNext() {
		counter++;
		if (counter >= size()) {
			counter = 0;
		}
		return super.get(counter);
	}
	
	/** get previous element in cycle.
	 * decrement counter mod size()
	 * @return element
	 */
	public E getPrevious() {
		counter--;
		if (counter < 0) {
			counter = size()-1;
		}
		return get(counter);
	}
	
	/** get current element in cycle.
	 * do not alter counter
	 * @return element
	 */
	public E getCurrent() {
		return get(counter);
	}
	
	/** set index 0 to canonical element.
	 */
	public void canonicalize() {
		String s = "";
		int idx = 0;
		for (int i = 0; i < size(); i++) {
			E e = get(i);
			if (i == 0) {
				s = stringId(e);
			} else if (stringId(e).compareTo(s) < 0) {
				idx = i;
				s = stringId(e);
			}
		}
		List<E> list = new ArrayList<E>();
		E e = get(idx);
		for (int i = 0; i < size(); i++) {
			list.add(e);
			e = getNext();
		}
		for (int i = 0; i < size(); i++) {
			super.set(i, list.get(i));
		}
	}
	
	/** get direction relative to two neighbouring atoms
	 * 
	 * @param a0
	 * @param a1
	 * @return 1 if a0->a1 is same as direction of ring
	 */
	public int getDirection(E a0, E a1) {
		int dir = 0;
		int counter0 = counter;
		getIndexOfAndCache(a0);
		if (getNext().equals(a1)) {
			dir = 1;
		}
		if (dir == 0) {
			getIndexOfAndCache(a1);
			if (getNext().equals(a0)) {
				dir = -1;
			}
		}
		counter = counter0;
		return dir;
	}
	
	/** get number of steps between atoms
	 * for a0-a1-a2-a3-a4 
	 * a0-a2 = 2
	 * a0-a1 = 1
	 * a1-a0 = 4
	 * a1-a1 = 0
	 * a1-a99 = -1
	 * @param a0
	 * @param a1
	 * @return nsteps 
	 */
	public int getStepCount(E a0, E a1) {
		int nsteps = 0;
		E a = a0;
		getIndexOfAndCache(a0);
		while (!a1.equals(a)) {
			a = getNext();
			nsteps++;
		}
		if (nsteps >= size()) {
			nsteps = -1;
		}
		return nsteps;
	}
	
	/** concatenated values
	 * @return whitespace concatenated values
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int counter0 = counter;
		for (int i = 0; i < size(); i++) {
			if (i != 0) {
				sb.append(" ");
			}
			if (counter0 == i) {
				sb.append("^");
			}
			sb.append(((CMLElement)get(i)).getAttributeValue("id"));
			if (counter0 == i) {
				sb.append("^");
			}
		}
		return sb.toString();
	}
	
	
	protected abstract String stringId(E e);
	
	protected void debug() {
		for (E e : this) {
			System.out.print(" .. "+stringId(e));
		}
		System.out.println();
	}
};

/** special routines for atoms
 * 
 * @author pm286
 *
 */
class CyclicAtomList extends CyclicList<CMLAtom> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 47311612331052340L;

	/** constructor
	 */
	public CyclicAtomList() {
		super();
	}
	
	protected String stringId(CMLAtom e) {
		return e.getId();
	}

}

/** special routines for atoms
 * 
 * @author pm286
 *
 */
class CyclicBondList extends CyclicList<CMLBond> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1279106530802984344L;

	/** constructor
	 */
	public CyclicBondList() {
		super();
	}
	
	protected String stringId(CMLBond e) {
		return e.atomHash();
	}

}
