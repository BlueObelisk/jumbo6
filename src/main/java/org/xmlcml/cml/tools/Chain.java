package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/**
 * tool to support a ring. not fully developed
 * @author pmr
 * 
 */
public class Chain extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());

	private List<Sprout> sproutList;
	private CMLAtomSet atomSet;
	private CMLBondSet bondSet;
	private List<CMLBond> terminalBondList;
	private List<CMLAtom> terminalAtomList;
	private Map<CMLAtom, List<CMLBond>> bondMap;
	private Molecule2DCoordinates moleculeDraw;
	private IntMatrix distanceMatrix;
	private SpanningTree spanningTree;
	private SortedMap<CMLAtom, Real2> atomCoordinateMap;

	private CMLAtom startAtom;

	private CMLAtom nextAtom;

	private double bondLength;

	private static Transform2 rot60 = new Transform2(new Angle(Math.PI/3.));

	private static final Transform2 rot90 = new Transform2(new Angle(Math.PI/2.));
	
	/**
	 * @return the atomCoordinateMap
	 */
	public Map<CMLAtom, Real2> getAtomCoordinateMap() {
		return atomCoordinateMap;
	}

	/**
	 */
	public Chain() {
		init();
	}
	
	/**
	 * @param moleculeDraw
	 */
	public Chain(Molecule2DCoordinates moleculeDraw) {
		init();
		this.setMoleculeDraw(moleculeDraw);
	}
	
	private void init() {
		this.sproutList = new ArrayList<Sprout>();
		this.atomSet = new CMLAtomSet();
		this.bondSet = new CMLBondSet();
		this.terminalBondList = new ArrayList<CMLBond>();
		this.terminalAtomList = new ArrayList<CMLAtom>();
		this.bondMap = 	new HashMap<CMLAtom, List<CMLBond>>();

	}

	
	/** add bond
	 * also adds atoms
	 * @param bond
	 */
	public void addBond(CMLBond bond) {
		this.bondSet.addBond(bond);
		this.addAtom(bond.getAtom(0), bond);
		this.addAtom(bond.getAtom(1), bond);
	}
	
	private void addAtom(CMLAtom atom, CMLBond bond) {
		this.atomSet.addAtom(atom);
		List<CMLBond> bondList = this.bondMap.get(atom);
		if (bondList == null) {
			bondList = new ArrayList<CMLBond>();
			this.bondMap.put(atom, bondList);
		}
		if (!bondList.contains(bond)) {
			bondList.add(bond);
		}
	}
	
	/**
	 * gets distances (in bonds) between terminals.
	 * if bonds are identical returns 0, etc. 
	 * if bonds are touching returns 1, etc. 
	 * @return symmetric distanceMatrix
	 */
	public IntMatrix calculateDistanceMatrixFromSpanningTree() {
		if (spanningTree != null) {
			int nterm = terminalAtomList.size();
			distanceMatrix = new IntMatrix(nterm, nterm);
			for (int i = 0; i < nterm -1; i++) {
				CMLAtom atomi = terminalAtomList.get(i);
				distanceMatrix.setElementAt(i, i, 0);
				for (int j = i+1; j < nterm; j++) {
					CMLAtom atomj = terminalAtomList.get(j);
					AtomPath path = spanningTree.getPath(atomi, atomj);
					int dist = path.size();
					distanceMatrix.setElementAt(i, j, dist);
					distanceMatrix.setElementAt(j, i, dist);
				}
			}
		}
		return distanceMatrix;
	}

	/**
	 * gets distances (in bonds) between terminals.
	 * if bonds are identical returns 0, etc. 
	 * if bonds are touching returns 1, etc. 
	 * @return symmetric distanceMatrix
	 */
	public IntMatrix calculateDistanceMatrix() {
		int nterm = terminalBondList.size();
		distanceMatrix = new IntMatrix(nterm, nterm);
		for (int i = 0; i < nterm-1; i++) {
			CMLBond bondi = terminalBondList.get(i);
			CMLAtom atomi = terminalAtomList.get(i);
			distanceMatrix.setElementAt(i, i, 0);
			for (int j = i+1; j < nterm; j++) {
				CMLBond bondj = terminalBondList.get(j);
				int dist = getDistance(atomi, bondi, bondj);
				distanceMatrix.setElementAt(i, j, dist);
				distanceMatrix.setElementAt(j, i, dist);
			}
		}
		return distanceMatrix;
	}

	/**
	 * @param startAtom
	 * @param startBond
	 * @param targetBond
	 * @return distance
	 */
	public int getDistance(CMLAtom startAtom, CMLBond startBond, CMLBond targetBond) {
		int level = 0;
		CMLAtomSet usedAtomSet = new CMLAtomSet();
		CMLBondSet usedBondSet = new CMLBondSet();
		List<AtomBond> atomBondList = new ArrayList<AtomBond>();
		atomBondList.add(new AtomBond(startAtom, startBond));
		usedAtomSet.addAtom(startAtom);
		usedBondSet.addBond(startBond);
		while (!AtomBond.contains(atomBondList, targetBond)) {
			atomBondList = expand(atomBondList, usedAtomSet, usedBondSet);
			if (atomBondList.size() == 0) {
				break;
			}
			level++;
		}
		return level;
	}
	
	private List<AtomBond> expand(List<AtomBond> atomBondList, 
			CMLAtomSet usedAtomSet, CMLBondSet usedBondSet) {
		List<AtomBond> newAtomBondList = new ArrayList<AtomBond>();
		for (AtomBond atomBond : atomBondList) {
			CMLBond startBond = atomBond.bond;
			CMLAtom startAtom = startBond.getOtherAtom(atomBond.atom);
			for (CMLBond ligandBond : startAtom.getLigandBonds()) {
				if (usedBondSet.contains(ligandBond)) {
					continue;
				}
				newAtomBondList.add(new AtomBond(startAtom, ligandBond)); 
				usedAtomSet.addAtom(startAtom);
				usedBondSet.addBond(ligandBond);
			}
		}
		return newAtomBondList;
	}
	

	/**
	 * @param sprout (can be null)
	 * @param moleculeDraw
	 */
	public void calculate2DCoordinates(Sprout sprout, Molecule2DCoordinates moleculeDraw) {
		this.setMoleculeDraw(moleculeDraw);
		ensureAtomCoordinateMap();
		bondLength = moleculeDraw.getDrawParameters().getBondLength();
		spanningTree = new SpanningTree(atomSet, bondSet);
		spanningTree.setOmitHydrogens(moleculeDraw.getDrawParameters().isOmitHydrogens());
		spanningTree.setIncludedAtomSet(atomSet);
		spanningTree.setIncludedBondSet(bondSet);
		spanningTree.generate(terminalAtomList.get(0));
		spanningTree.generateTerminalPaths();
		
		List<AtomPath> pathList = null;
		startAtom = null;
		if (sprout == null) {
			this.calculateDistanceMatrixFromSpanningTree();
			// get longest path
			Int2 ij = distanceMatrix.indexOfLargestElement();
			startAtom = terminalAtomList.get(ij.getX());
//			CMLAtom endAtom = terminalAtomList.get(ij.getY());
//			System.out.println(startAtom.getId()+"<==>"+endAtom.getId()+"/"+spanningTree.getPath(startAtom, endAtom));
			atomCoordinateMap.put(startAtom, new Real2(0., 0.));
		} else {
			startAtom = sprout.getRingAtom();
			atomCoordinateMap.put(startAtom, startAtom.getXY2());
		}
		pathList = this.getPathsToTerminalAtoms(startAtom);
		Collections.sort(pathList);
		Collections.reverse(pathList);
		AtomPath path = (pathList.size() == 0) ? null : pathList.get(0);
		nextAtom = (path != null && path.size() > 1) ? path.get(1) : null;
		if (nextAtom == null) {
//			atom.setXY2(new Real2(0.0, 0.0));
		} else {
			if (sprout == null) {
				atomCoordinateMap.put(nextAtom, new Real2(bondLength, 0.));
			} else {
				atomCoordinateMap.put(nextAtom, nextAtom.getXY2());
			}
			System.out.println("PATHS "+pathList.size());
			for (int i = 0; i < pathList.size(); i++) {
				AtomPath atomPath = pathList.get(i);
				System.out.println(pathList.get(i));
				calculate2DCoordinates(atomPath, i);
			}
			for (CMLAtom atom : atomCoordinateMap.keySet()) {
				Real2 xy2 = atomCoordinateMap.get(atom);
				if (xy2 == null) {
					System.err.println("NULL coord: "+atom.getId());
				}
				atom.setXY2(xy2);
			}
		}
	}
	
	private void calculate2DCoordinates(AtomPath atomPath, int serial) {
		ensureAtomCoordinateMap();
		if (atomPath.size() < 2) {
			throw new CMLRuntimeException("Path must be at least 2 atoms");
		}
		if (atomPath.get(0) != startAtom || atomPath.get(1) != nextAtom) {
			throw new CMLRuntimeException("atomPath does not start with start/nextAtom");
		}
		Real2 xy0 = atomCoordinateMap.get(startAtom);
		Real2 xy1 = atomCoordinateMap.get(nextAtom);
//		System.out.println(">>>start "+startAtom.getId()+"/"+xy0);
//		System.out.println(">>>next "+nextAtom.getId()+"/"+xy1);
		if (xy0 == null || xy1 == null) {
			throw new CMLRuntimeException("First 2 atoms must have coordinates");
		}
		Real2[] vv = new Real2[2];
		vv[1] = xy1.subtract(xy0);
		vv[0] = new Real2(vv[1]);
		vv[0].transformBy(rot60);
		System.out.println(vv[0]+"/"+vv[1]);
		int start = -1;
		CMLAtom previousAtom = nextAtom;
		Real2 currentXY2 = atomCoordinateMap.get(previousAtom);
		// go down path
		for (int i = 2; i < atomPath.size(); i++) {
			CMLAtom atom = atomPath.get(i);
			Real2 newXY2 = atomCoordinateMap.get(atom);
			if (newXY2 != null) {
				if (start >= 0) {
					throw new CMLRuntimeException("wrong way down path?");
				}
				System.out.println(">SKIP> "+atom.getId());
				previousAtom = atom;
				continue;
			}
			// found first null atom
			if (start < 0) {
				start = i;
			}
			// are we branching?
			List<CMLAtom> ligandList = getLigandsInChain(previousAtom);
			if (ligandList.size() == 0) {
				throw new CMLRuntimeException("previous atom has no ligands");
			} else if (ligandList.size() == 1) {
				// no, flip vector with parity
				Real2 vvv = vv[(i) % 2];
				System.out.println("V1 "+vvv);
				currentXY2 = currentXY2.plus(vvv);
				atomCoordinateMap.put(atom, currentXY2);
//				System.out.println(">>"+atom.getElementType()+">> "+atom.getId()+"/"+currentXY2);
			} else if (ligandList.size() == 2) {
//				System.out.println(">>BRANCH>> "+atom.getId());
//				System.out.println("Midpoint for "+atom.getId()+" on "+previousAtom.getId());
				currentXY2 = atomCoordinateMap.get(previousAtom);
				CMLAtom prevLig0 = ligandList.get(0);
				CMLAtom prevLig1 = ligandList.get(1);
//				System.out.println(prevLig0.getId()+"["+previousAtom.getId()+"]"+prevLig1.getId());
				Real2 prevLigXY0 = atomCoordinateMap.get(prevLig0);
				Real2 prevLigXY1 = atomCoordinateMap.get(prevLig1);
//				System.out.println("MID "+prevLigXY0+"/"+prevLigXY1);
				Real2 centroid = prevLigXY0.getMidPoint(prevLigXY1);
//				System.out.println(centroid);
				Real2 vect = currentXY2.subtract(centroid);
//				System.out.println("V2 "+vect);
				if (vect.getLength() < 0.00001) {
					vect = prevLigXY0.subtract(centroid);
					vect.transformBy(rot90);
				}
				vect = vect.getUnitVector().multiplyBy(bondLength);
				int i0 = (i) % 2;
				int i1 = (i + 1) % 2;
				vv[i0] = vect;
				vv[i1] = new Real2(vv[i0]);
				vv[i1].transformBy(rot60);
//				System.out.println("V22 "+vect);
				currentXY2 = currentXY2.plus(vv[i0]);
				atomCoordinateMap.put(atom, currentXY2);
			}
			previousAtom = atom;
		}
		if (start == -1 && atomPath.size() > 2) {
			throw new CMLRuntimeException("path full of coordinates");
		}
	}
	
	private List<CMLAtom> getLigandsInChain(CMLAtom atom) {
		List<CMLAtom> chainLigandList = new ArrayList<CMLAtom>(); 
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!atomSet.contains(ligand)) {
				// atom must be in set
			} else if (atomCoordinateMap.get(ligand) != null) {
				// ligand needs coordinates
				chainLigandList.add(ligand);
			}
		}
		return chainLigandList;
	}
	
	private void applyCoordinates() {
		for (CMLAtom atom : atomCoordinateMap.keySet()) {
			atom.setXY2(atomCoordinateMap.get(atom));
		}
	}
	
	private void ensureAtomCoordinateMap() {
		if (atomCoordinateMap == null) {
			atomCoordinateMap = new TreeMap<CMLAtom, Real2>();
		}
	}

	/** gets all paths starting at single atom.
	 * 
	 * @param startAtom
	 * @return list of other termibal atoms
	 */
	public List<AtomPath> getPathsToTerminalAtoms(CMLAtom startAtom) {
		List<AtomPath> pathList = new ArrayList<AtomPath>();
		// TODO ensure paths. this is messy and should be refactored
		for (CMLAtom terminalAtom : terminalAtomList) {
			spanningTree.getPath(startAtom, terminalAtom);
		}
		Map<CMLAtom, Map<CMLAtom, AtomPath>> pathMap = spanningTree.getPathMap();
		Map<CMLAtom, AtomPath> paths = pathMap.get(startAtom);
		for (CMLAtom terminalAtom : paths.keySet()) {
			if (startAtom.equals(terminalAtom)) {
				continue;
			} else {
				AtomPath path = spanningTree.getPath(startAtom, terminalAtom);
				pathList.add(path);
			}
		}
		return pathList;
	}
	
	/**
	 * layout chain attached to ring
	 * recursively visit attachments
	 * @param sprout
	 */
	public void layout(Sprout sprout) {
		sprout.generateCoordinates();
		applyCoordinates();
		for (Sprout otherSprout : sproutList) {
			if (sprout != otherSprout) {
				RingNucleus nextNucleus = moleculeDraw.getSproutNucleusMap().get(otherSprout);
				nextNucleus.layout(otherSprout);
			}
		}
	}
	
	/**
	 * @return the sproutList
	 */
	public List<Sprout> getSproutList() {
		return sproutList;
	}

	/** add sprout
	 * 
	 * @param sprout
	 */
	public void addSprout(Sprout sprout) {
		if (this.sproutList.contains(sprout)) {
			throw new CMLRuntimeException("duplicate sprout in chain");
		}
		this.sproutList.add(sprout);
		this.addTerminalBond(sprout.getRingAtom(), sprout.getBond());
	}
	
	/**
	 * @return the terminalBondList
	 */
	public List<CMLBond> getTerminalBondList() {
		return terminalBondList;
	}

	/**
	 * mark bond as terminal (includes sprouts)
	 * @param atom  terminalAtom
	 * @param bond  terminalBond
	 */
	public void addTerminalBond(CMLAtom atom, CMLBond bond) {
		if (moleculeDraw.getDrawParameters().isOmitHydrogens() && "H".equals(atom.getElementType())) {
			// omit hydrogens
		} else {
			if (terminalBondList == null) {
				terminalBondList = new ArrayList<CMLBond>();
			}
			if (!terminalBondList.contains(bond)) {
				terminalBondList.add(bond);
			}
			if (terminalAtomList == null) {
				terminalAtomList = new ArrayList<CMLAtom>();
			}
			if (!terminalAtomList.contains(atom)) {
				terminalAtomList.add(atom);
			}
		}
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
	 * @return the distanceMatrix
	 */
	public IntMatrix getDistanceMatrix() {
		return distanceMatrix;
	}

	/**
	 * @return the terminalAtomList
	 */
	public List<CMLAtom> getTerminalAtomList() {
		return terminalAtomList;
	}

	/**
	 * @return string
	 */
	public String toString() {
		String s = "chain: ";
		for (CMLBond bond : bondSet.getBonds()) {
			s += "["+bond.getId()+"]";
		}
		s += "\n";
		for (Sprout sprout : sproutList) {
			s += "{"+sprout+"}";
		}
		s += "\n";
		for (int i = 0; i < terminalBondList.size(); i++) {
			s += "<"+terminalAtomList.get(i).getId()+"; "+terminalBondList.get(i).getId()+">";
		}
		return s;
	}

	/**
	 * @return the bondMap
	 */
	public Map<CMLAtom, List<CMLBond>> getBondMap() {
		return bondMap;
	}

	/**
	 * @return the moleculeDraw
	 */
	public Molecule2DCoordinates getMoleculeDraw() {
		return moleculeDraw;
	}

	/**
	 * @param moleculeDraw the moleculeDraw to set
	 */
	public void setMoleculeDraw(Molecule2DCoordinates moleculeDraw) {
		this.moleculeDraw = moleculeDraw;
	}

	/**
	 * @return the spanningTree
	 */
	public SpanningTree getSpanningTree() {
		return spanningTree;
	}

}
class AtomBond {
	CMLAtom atom;
	CMLBond bond;
	
	/**
	 * @param atom
	 * @param bond
	 */
	public AtomBond(CMLAtom atom, CMLBond bond) {
		this.atom = atom;
		this.bond = bond;
	}
	
	/**
	 * @param atomBondList
	 * @param bond
	 * @return true if in list
	 */
	public static boolean contains(List<AtomBond> atomBondList, CMLBond bond) {
		boolean contains = false;
		for (AtomBond atomBond : atomBondList) {
			if (atomBond.bond.equals(bond)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
}

