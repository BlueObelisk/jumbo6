package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tool to support a ring. not fully developed
 * @author pmr
 * 
 */
public class Chain extends AbstractTool {
	final static Logger LOG = Logger.getLogger(RingNucleus.class);

	private List<Sprout> sproutList;
	private CMLAtomSet atomSet;
	private CMLBondSet bondSet;
	private List<CMLBond> terminalBondList;
	private List<CMLAtom> terminalAtomList;
	private Map<CMLAtom, List<CMLBond>> bondMap;
	private MoleculeLayout moleculeDraw;
	private IntMatrix distanceMatrix;
	private SpanningTree spanningTree;
	private SortedMap<CMLAtom, Real2> atomCoordinateMap;

	private CMLAtom startAtom;

	private double bondLength;

	private static Transform2 ROT60 = new Transform2(new Angle(Math.PI/3.));
	private static Transform2 ROT300 = new Transform2(new Angle(-Math.PI/3.));
	private static Transform2 ROT30 = new Transform2(new Angle(Math.PI/6.));
	private static Transform2 ROT330 = new Transform2(new Angle(Math.PI/6.));

	private static final Transform2 ROT90 = new Transform2(new Angle(Math.PI/2.));
	private static final Transform2 ROT270 = new Transform2(new Angle(-Math.PI/2.));

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
	public Chain(MoleculeLayout moleculeDraw) {
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
	 * @param moleculeLayout
	 */
	public void calculate2DCoordinates(Sprout sprout, MoleculeLayout moleculeLayout) {
		this.setMoleculeDraw(moleculeLayout);
		ensureAtomCoordinateMap();
		bondLength = moleculeLayout.getMoleculeDisplay().getBondLength();
		spanningTree = new SpanningTree(atomSet, bondSet);
		spanningTree.setOmitHydrogens(moleculeLayout.getMoleculeDisplay().isOmitHydrogens());
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
		// sort on path lengths
		Collections.sort(pathList);
		// longest paths first
		Collections.reverse(pathList);
		// longest path
        @SuppressWarnings("unused")
		AtomPath path = (pathList.size() == 0) ? null : pathList.get(0);
		expandPaths(pathList);
//		expandPathsOld(sprout, pathList, path);
	}

//	private void expandPathsOld(Sprout sprout, List<AtomPath> pathList, AtomPath path) {
//		nextAtom = (path != null && path.size() > 1) ? path.get(1) : null;
//		if (nextAtom == null) {
////			atom.setXY2(new Real2(0.0, 0.0));
//		} else {
//			if (sprout == null) {
//				atomCoordinateMap.put(nextAtom, new Real2(bondLength, 0.));
//			} else {
//				atomCoordinateMap.put(nextAtom, nextAtom.getXY2());
//			}
//			System.out.println("PATHS "+pathList.size());
//			for (int i = 0; i < pathList.size(); i++) {
//				AtomPath atomPath = pathList.get(i);
//				System.out.println(">"+i+">"+pathList.get(i));
//				try {
//					calculate2DCoordinates(atomPath, i);
//				} catch (RuntimeException e) {
//					System.err.println("ERROR-CHAIN: "+e);
//				}
//			}
//			for (CMLAtom atom : atomCoordinateMap.keySet()) {
//				Real2 xy2 = atomCoordinateMap.get(atom);
//				if (xy2 == null) {
//					System.err.println("NULL coord: "+atom.getId());
//				}
//				atom.setXY2(xy2);
//			}
//		}
//	}
	
	private void expandPaths(List<AtomPath> pathList) {
		AtomPath atomPath0 = pathList.get(0);
		CMLAtom atom0 = atomPath0.get(0);
		Real2 xy0 = new Real2(0., 0.);
		atom0.setXY2(xy0);
		for (AtomPath atomPath : pathList) {
			expandPath(atomPath, 1);
		}
	}
	
	private void expandPath(AtomPath atomPath, int start) {
		for (int i = start; i < atomPath.size(); i++) {
			CMLAtom node = atomPath.get(i);
			Real2 xy = node.getXY2();
			if (xy == null) {
				Real2 direction = getDirection(atomPath, i, node);
				xy = atomPath.get(i-1).getXY2().plus(direction);
				node.setXY2(xy);
			}
		}
	}
	
	private Real2 getDirection(AtomPath atomPath, int position, CMLAtom node) {
		Real2 direction = null;
		CMLAtom parent = (position == 0) ? null : atomPath.get(position-1); 
		CMLAtom grandParent = (position <= 1) ? null : atomPath.get(position-2); 
		CMLAtom greatGrandParent = (position <= 2) ? null : atomPath.get(position-3); 
		List<CMLAtom> ligandList = parent.getLigandAtoms();
		int freeLigand = -1;
		int nlig = ligandList.size();
		for (int i = 0; i < nlig; i++) {
			CMLAtom ligand = ligandList.get(i);
			if (ligand.equals(grandParent)) {
				continue;
			}
			freeLigand++;
			if (ligand.equals(node)) {
				break;
			}
		}
		Real2 parentDirection = null;
		if (grandParent != null) {
			parentDirection = parent.getXY2().subtract(grandParent.getXY2());
		} else {
			parentDirection = new Real2(bondLength, 0.0);
		}
		direction = new Real2(parentDirection);
		if (nlig == 2) {
			// may also need to think about CH2 groups
			Real2 grandDirection = null;
			if (greatGrandParent != null) {
				grandDirection = grandParent.getXY2().subtract(greatGrandParent.getXY2());
			}
			direction = parent.getXY2().subtract(grandParent.getXY2());
			if (grandDirection == null) {
				direction.transformBy(ROT330);
			} else {
				Real2 direction1 = new Real2(direction);
				direction1.transformBy(ROT30);
				double dot1 = Math.abs(direction1.dotProduct(grandDirection));
				Real2 direction2 = new Real2(direction);
				direction2.transformBy(ROT330);
				double dot2 = Math.abs(direction2.dotProduct(grandDirection));
				direction = (dot1 > dot2) ? direction1 : direction2;
			}
//			direction = parent.getXY2().subtract(grandParent.getXY2());
			// linear at present - change later when we understand bond orders
		} else if (nlig == 3) {
			if (freeLigand == 0) {
				direction.transformBy(ROT60);
			} else if (freeLigand == 1) {
				direction.transformBy(ROT300);
			}
		} else if (nlig == 4) {
			if (freeLigand == 0) {
				// straight on
			} else if (freeLigand == 1){
				direction.transformBy(ROT90);
			} else if (freeLigand == 2){
				direction.transformBy(ROT270);
			}
		}
		return direction;
	}
	
//	private void calculate2DCoordinates(AtomPath atomPath, int serial) {
//		ensureAtomCoordinateMap();
//		if (atomPath.size() < 2) {
//			throw new RuntimeException("Path must be at least 2 atoms");
//		}
//		if (atomPath.get(0) != startAtom || atomPath.get(1) != nextAtom) {
//			throw new RuntimeException("atomPath does not start with start/nextAtom");
//		}
//		Real2 xy0 = atomCoordinateMap.get(startAtom);
//		Real2 xy1 = atomCoordinateMap.get(nextAtom);
////		System.out.println(">>>start "+startAtom.getId()+"/"+xy0);
////		System.out.println(">>>next "+nextAtom.getId()+"/"+xy1);
//		if (xy0 == null || xy1 == null) {
//			throw new RuntimeException("First 2 atoms must have coordinates");
//		}
//		Real2[] vv = new Real2[2];
//		vv[1] = xy1.subtract(xy0);
//		vv[0] = new Real2(vv[1]);
//		vv[0].transformBy(ROT60);
//		System.out.println(vv[0]+"/"+vv[1]);
//		int start = -1;
//		CMLAtom previousAtom = nextAtom;
//		Real2 currentXY2 = atomCoordinateMap.get(previousAtom);
//		// go down path
//		for (int atomPathi = 2; atomPathi < atomPath.size(); atomPathi++) {
//			CMLAtom atom = atomPath.get(atomPathi);
//			Real2 newXY2 = atomCoordinateMap.get(atom);
//			if (newXY2 != null) {
//				if (start >= 0) {
//					throw new RuntimeException("wrong way down path?");
//				}
//				System.out.println(">SKIP> "+atom.getId());
//				previousAtom = atom;
//				continue;
//			}
//			// found first null atom
//			if (start < 0) {
//				start = atomPathi;
//			}
//			// are we branching?
//			List<CMLAtom> ligandList = getLigandsInChain(previousAtom);
//			if (ligandList.size() == 0) {
//				throw new RuntimeException("previous atom has no ligands");
//			} else if (ligandList.size() == 1) {
//				// no, flip vector with parity
//				Real2 vvv = vv[(atomPathi) % 2];
//				System.out.println("V1 "+vvv);
//				currentXY2 = currentXY2.plus(vvv);
//				atomCoordinateMap.put(atom, currentXY2);
////				System.out.println(">>"+atom.getElementType()+">> "+atom.getId()+"/"+currentXY2);
//			} else if (ligandList.size() == 2) {
//				currentXY2 = process2LigandList(vv, previousAtom, atomPathi, atom, ligandList);
//			} else if (ligandList.size() == 3) {
//				System.out.println("BUG 3LigandList not processed");
//				for (CMLAtom ligand : ligandList) {
//					System.out.println("LIG "+ligand.getId()+" / "+ligand.getXY2());
//				}
//				currentXY2 = process3LigandList(vv, previousAtom, atomPathi, atom, ligandList);
//			}
//			previousAtom = atom;
//		}
//		if (start == -1 && atomPath.size() > 2) {
//			throw new RuntimeException("path full of coordinates");
//		}
//	}

//	private Real2 process2LigandList(Real2[] vv, CMLAtom previousAtom, int i,
//			CMLAtom atom, List<CMLAtom> ligandList) {
//		Real2 currentXY2;
//		System.out.println(">>BRANCH>> "+atom.getId());
//		System.out.println("Midpoint for "+atom.getId()+" on "+previousAtom.getId());
//		currentXY2 = atomCoordinateMap.get(previousAtom);
//		CMLAtom prevLig0 = ligandList.get(0);
//		CMLAtom prevLig1 = ligandList.get(1);
//		System.out.println(prevLig0.getId()+"["+previousAtom.getId()+"]"+prevLig1.getId());
//		Real2 prevLigXY0 = atomCoordinateMap.get(prevLig0);
//		Real2 prevLigXY1 = atomCoordinateMap.get(prevLig1);
//		System.out.println("MID "+prevLigXY0+"/"+prevLigXY1);
//		Real2 centroid = prevLigXY0.getMidPoint(prevLigXY1);
//		System.out.println(centroid);
//		Real2 vect = currentXY2.subtract(centroid);
//		System.out.println("V2 "+vect);
//		if (vect.getLength() < 0.00001) {
//			vect = prevLigXY0.subtract(centroid);
//			vect.transformBy(ROT90);
//		}
//		vect = vect.getUnitVector().multiplyBy(bondLength);
//		int i0 = (i) % 2;
//		int i1 = (i + 1) % 2;
//		vv[i0] = vect;
//		vv[i1] = new Real2(vv[i0]);
//		vv[i1].transformBy(ROT60);
////				System.out.println("V22 "+vect);
//		currentXY2 = currentXY2.plus(vv[i0]);
//		atomCoordinateMap.put(atom, currentXY2);
//		return currentXY2;
//	}
	
//	private Real2 process3LigandList(Real2[] vv, CMLAtom previousAtom, int i,
//			CMLAtom atom, List<CMLAtom> ligandList) {
//		Real2 currentXY2;
//		System.out.println(">>BRANCH>> "+atom.getId());
//		System.out.println("Midpoint for "+atom.getId()+" on "+previousAtom.getId());
//		currentXY2 = atomCoordinateMap.get(previousAtom);
//		CMLAtom prevLig0 = ligandList.get(0);
//		CMLAtom prevLig1 = ligandList.get(1);
//		System.out.println(prevLig0.getId()+"["+previousAtom.getId()+"]"+prevLig1.getId());
//		Real2 prevLigXY0 = atomCoordinateMap.get(prevLig0);
//		Real2 prevLigXY1 = atomCoordinateMap.get(prevLig1);
//		System.out.println("MID "+prevLigXY0+"/"+prevLigXY1);
//		Real2 centroid = prevLigXY0.getMidPoint(prevLigXY1);
//		System.out.println(centroid);
//		Real2 vect = currentXY2.subtract(centroid);
//		System.out.println("V2 "+vect);
//		if (vect.getLength() < 0.00001) {
//			vect = prevLigXY0.subtract(centroid);
//			vect.transformBy(ROT90);
//		}
//		vect = vect.getUnitVector().multiplyBy(bondLength);
//		int i0 = (i) % 2;
//		int i1 = (i + 1) % 2;
//		vv[i0] = vect;
//		vv[i1] = new Real2(vv[i0]);
//		vv[i1].transformBy(ROT30);
////				System.out.println("V22 "+vect);
//		currentXY2 = currentXY2.plus(vv[i0]);
//		atomCoordinateMap.put(atom, currentXY2);
//		return currentXY2;
//	}
	
//	private List<CMLAtom> getLigandsInChain(CMLAtom atom) {
//		List<CMLAtom> chainLigandList = new ArrayList<CMLAtom>(); 
//		List<CMLAtom> ligandList = atom.getLigandAtoms();
//		for (CMLAtom ligand : ligandList) {
//			if (!atomSet.contains(ligand)) {
//				// atom must be in set
//			} else if (atomCoordinateMap.get(ligand) != null) {
//				// ligand needs coordinates
//				chainLigandList.add(ligand);
//			}
//		}
//		return chainLigandList;
//	}
	
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
			throw new RuntimeException("duplicate sprout in chain");
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
		if (((MoleculeDisplay)moleculeDraw.getDrawParameters()).isOmitHydrogens() && AS.H.equals(atom.getElementType())) {
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
	public MoleculeLayout getMoleculeDraw() {
		return moleculeDraw;
	}

	/**
	 * @param moleculeDraw the moleculeDraw to set
	 */
	public void setMoleculeDraw(MoleculeLayout moleculeDraw) {
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

