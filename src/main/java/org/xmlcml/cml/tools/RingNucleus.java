package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class RingNucleus extends AbstractTool implements Comparable<RingNucleus> {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());
	private CMLAtomSet atomSet;
	private CMLBondSet bondSet;
	private List<Ring> ringList = null;
	private List<Junction> junctionList = null;
	private Map<CMLAtom, BridgeAtom> bridgeAtomMap = null;
	private MoleculeDraw moleculeDraw;
	private List<RingNucleus> connectedNucleusList;
	private List<Sprout> sproutList;
	private List<Sprout> remoteSproutList;
	private List<Real2> coordinateList;

	private RingNucleus() {
		init();
		// 
	}
	/**
	 * @return the ringList
	 */
	public List<Ring> getRingList() {
		return ringList;
	}
	/**
	 * @return the sproutList
	 */
	public List<Sprout> getSproutList() {
		return sproutList;
	}
	/** constructor.
	 * copies reference to sets
	 * 
	 * @param atomSet
	 * @param bondSet
	 */
	public RingNucleus(CMLAtomSet atomSet,	CMLBondSet bondSet) {
		this.atomSet = atomSet;
		this.bondSet = bondSet;
		init();
	}

	private void init() {
		bridgeAtomMap = new HashMap<CMLAtom, BridgeAtom>();
//		sproutList = new ArrayList<Sprout>();
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

	/** get list of rings.
	 * not necessarily SSSR
	 * @return list of rings
	 */
	public List<Ring> getRings() {
		if (ringList == null) {
			ringList = new ArrayList<Ring>();
			Set<CMLAtom> usedAtoms = new HashSet<CMLAtom>();
			CMLAtom root = atomSet.getAtom(0);
			CMLAtom parentAtom = null;
			Map<CMLAtom, CMLAtom> atomToParentMap = new HashMap<CMLAtom, CMLAtom>();
			Set<CMLBond> linkBondSet = new HashSet<CMLBond>();
			expand(root, parentAtom, usedAtoms, atomToParentMap, linkBondSet);
			for (CMLBond bond : linkBondSet) {
				Ring ring = getRing(bond, atomToParentMap);
				ringList.add(ring);
			}
			Collections.sort(ringList);
		}
		return ringList;
	}
	
	private void expand(CMLAtom atom, CMLAtom parentAtom, 
			Set<CMLAtom> usedAtoms, Map<CMLAtom, CMLAtom> atomToParentMap,
			Set<CMLBond> linkBondSet) {
		usedAtoms.add(atom);
		atomToParentMap.put(atom, parentAtom);
		List<CMLAtom> ligandAtomList = atom.getLigandAtoms();
		CMLMolecule molecule = atom.getMolecule();
		for (int i = 0; i < ligandAtomList.size(); i++) {
			CMLAtom ligandAtom = ligandAtomList.get(i);
			if (!atomSet.contains(ligandAtom)) {
				// skip atoms outside set
			} else if (ligandAtom.equals(parentAtom)) {
				// skip existing bond
			} else if (usedAtoms.contains(ligandAtom)) {
				CMLBond linkBond = molecule.getBond(atom, ligandAtom);
				linkBondSet.add(linkBond);
				// already treated
			} else {
				expand(ligandAtom, atom, usedAtoms, atomToParentMap, linkBondSet);
			}
		}
	}
	
	private Ring getRing(CMLBond bond, Map<CMLAtom, CMLAtom> atomToParentMap) {
		CMLAtomSet atomSet0 = getAncestors(bond.getAtom(0), atomToParentMap);
		CMLAtomSet atomSet1 = getAncestors(bond.getAtom(1), atomToParentMap);
		CMLBondSet bondSet0 = getAncestors1(bond.getAtom(0), atomToParentMap, atomSet1);
		CMLBondSet bondSet1 = getAncestors1(bond.getAtom(1), atomToParentMap, atomSet0);
		CMLBondSet mergedBondSet = bondSet0.symmetricDifference(bondSet1);
		mergedBondSet.addBond(bond);
		Ring ring = new Ring(mergedBondSet.getAtomSet(), mergedBondSet);
		return ring;
	}
	
	private CMLAtomSet getAncestors(CMLAtom atom, Map<CMLAtom, CMLAtom> atomToParentMap) {
		CMLAtomSet newAtomSet = new CMLAtomSet();
		while (true) {
			atom = (CMLAtom) atomToParentMap.get(atom);
			if (atom == null) {
				break;
			}
			newAtomSet.addAtom(atom);
			if (atomSet != null && atomSet.contains(atom)) {
				break;
			}
		}
		return newAtomSet;
	}
	
	private CMLBondSet getAncestors1(CMLAtom atom, Map<CMLAtom, CMLAtom> atomToParentMap, CMLAtomSet atomSet) {
		CMLMolecule molecule =atom.getMolecule();
		CMLBondSet newBondSet = new CMLBondSet();
		while (true) {
			CMLAtom atom1 = (CMLAtom) atomToParentMap.get(atom);
			if (atom1 == null) {
				break;
			}
			CMLBond bond = molecule.getBond(atom, atom1);
			if (newBondSet.contains(bond)) {
				break;
			}
			newBondSet.addBond(bond);
			atom = atom1;
		}
		return newBondSet;
	}

	/** get set of smallest rings.
	 * crude
	 * @param update replace current list
	 * @return list of rings
	 */
	public List<Ring> getSetOfSmallestRings(boolean update) {
		getRings();
		List<Ring> newList = new ArrayList<Ring>();
		for (Ring ring : ringList) {
			newList.add(ring);
		}
		if (newList.size() > 1) {
			boolean change = true;
			while (change) {
				for (int i = 0; i < newList.size(); i++) {
					Ring ring = newList.get(i);
					change = reduceRingSizes(ring, newList);
				}
			}
		}
		if (update) {
			ringList = newList;
		}
		Collections.sort(newList);
		return newList;
	}

	/**
	 * @param ring
	 */
	private boolean reduceRingSizes(Ring ring, List<Ring> newList) {
		boolean change = false;
		for (int i = 0; i < newList.size(); i++) {
			Ring target = newList.get(i);
			if (target == ring) {
				continue;
			}
			CMLBondSet newBondSet = target.getBondSet().symmetricDifference(ring.getBondSet());
			if (newBondSet.size() < target.size()) {
//				System.out.println(""+newBondSet.size()+" < "+target.size());
				Ring newRing = new Ring(newBondSet);
				newList.set(i, newRing);
				change = true;
			}
		}
		return change;
	}

	/** get list of junctions
	 * 
	 * @return list
	 */
	public List<Junction> getJunctions() {
		if (junctionList == null) {
			junctionList = new ArrayList<Junction>();
			// do this anyway
			getSetOfSmallestRings(true);
			for (int i = 0; i < ringList.size()-1; i++) {
				Ring ringi = ringList.get(i);
				for (int j = i+1; j < ringList.size(); j++) {
					Ring ringj = ringList.get(j);
					Junction junction = Junction.createJunction(ringi, ringj);
					if (junction != null) {
						junctionList.add(junction);
						add(junction.getBridgeAtomList().get(0));
						add(junction.getBridgeAtomList().get(1));
					}
				}
			}
		}
		return junctionList;
	}
	
	private void add(BridgeAtom bridgeAtom) {
		CMLAtom atom = bridgeAtom.getAtom();
		BridgeAtom existingBridgeAtom = bridgeAtomMap.get(atom);
		if (existingBridgeAtom == null) {
			existingBridgeAtom = bridgeAtom;
			bridgeAtomMap.put(atom, existingBridgeAtom);
		}
		// add rings to existing rings
		if (existingBridgeAtom != bridgeAtom) {
			for (Ring ring : bridgeAtom.getRingSet()) {
				existingBridgeAtom.addRing(ring);
			}
			existingBridgeAtom.addJunction(bridgeAtom.getJunctionList().get(0));
		}
	}
	
	/** layout starting with this nucleus.
	 * 
	 * @param sprout
	 */
	public void layout(Sprout sprout) {
		Real2 oldSproutVector = null;
		Real2 oldRingXY2 = null;
		Real2 oldFirstXY2 = null;
		if (sprout != null) {
			oldRingXY2 = sprout.getRingAtom().getXY2();
			oldFirstXY2 = sprout.getFirstAtom().getXY2();
			oldSproutVector = oldRingXY2.subtract(oldFirstXY2);
		}
		this.add2DCoordinates();
		if (sprout != null) {
			Real2 sproutVector = sprout.getSproutVector();
			Angle a = new Vector2(sproutVector).getAngleMadeWith(new Vector2(oldSproutVector));
			Transform2 transform2 = new Transform2(a.plus(new Angle(Math.PI)));
			atomSet.transform(transform2);
			Real2 delta2 = oldRingXY2.subtract(sprout.getRingAtom().getXY2());
			atomSet.translate2D(delta2);
		}
		getSproutList(moleculeDraw.getDrawParameters().isOmitHydrogens());
		for (Sprout otherSprout : sproutList) {
			if (otherSprout != sprout) {
				Chain chain = moleculeDraw.getSproutMap().get(otherSprout);
				chain.layout(otherSprout);
			}
		}
	}

	/** add 2D coordinates
	 */
	public void add2DCoordinates() {
		getSetOfSmallestRings(true);
		this.getJunctions();
		if (ringList.size() == 0) {
			throw new CMLRuntimeException("ring nucleus has no rings");
		}
		// get largest ring
		Collections.sort(ringList);
		List<Ring> oldRingList = new ArrayList<Ring>();
		for (Ring ring : ringList) {
			oldRingList.add(ring);
		}
		List<Ring> newRingList = new ArrayList<Ring>();
// add first ring
		Ring currentRing = ringList.get(0);
		currentRing.add2DCoordinates(moleculeDraw.getDrawParameters());
		oldRingList.remove(currentRing);
		newRingList.add(currentRing);
		
		Junction junction = null;
		while (true) {
			junction = findNextJunctionUpdateListsAdd2DCoordinates(oldRingList, newRingList);
			if (junction == null) {
				break;
			}
		}
		if (oldRingList.size() > 0) {
			throw new CMLRuntimeException("Undrawn rings ");
		}
	}
	/**
	 * @param remainingRingList
	 * @param growingRingList
	 * @param change
	 * @return junction or null
	 * @throws CMLRuntimeException
	 */
	private Junction findNextJunctionUpdateListsAdd2DCoordinates(List<Ring> remainingRingList, List<Ring> growingRingList) throws CMLRuntimeException {
		int commonAtoms = 0;
		Junction junction0 = null;
		Ring ringToBeAdded0 = null;
		Ring existingRing0 = null;
		for (Ring existingRing : growingRingList) {
			for (Ring nextRemainingRing : remainingRingList) {
				List<Junction> junctionList = nextRemainingRing.getJunctionList(existingRing);
				if (junctionList == null) {
					continue;
				} else if (junctionList.size() > 1) {
					throw new CMLRuntimeException("Cannot layout ring pair with multiple junctions");
				}
				Junction junction = junctionList.get(0);
				if (junction.getCommonAtomList().size() > 2) {
					System.out.println("complex junction");
				}
				// record largest junction
				if (commonAtoms < atomSet.size()) {
					junction0 = junction;
					ringToBeAdded0 = nextRemainingRing;
					existingRing0 = existingRing;
					commonAtoms = atomSet.size();
				}
			}
		}
		// did we find any junctions?
		if (junction0 != null) {
			for (CMLAtom atom : existingRing0.getCyclicAtomList()) {
				System.out.println(atom.getElementType()+"/"+atom.getXY2());
			}
			try {
				add2DCoordinates(junction0, ringToBeAdded0, existingRing0);
				growingRingList.add(ringToBeAdded0);
				remainingRingList.remove(ringToBeAdded0);
			} catch (CMLRuntimeException e) {
				throw e;
			}
		}
		return junction0;
	}
	
	private void add2DCoordinates(
			Junction junction, Ring ringToBeAdded, Ring existingRing) 
			throws CMLRuntimeException {
		coordinateList = new ArrayList<Real2>();
		CyclicAtomList existingRingCyclicAtomList = existingRing.getCyclicAtomList();
		existingRingCyclicAtomList.debug();
		CyclicAtomList ringToBeAddedCyclicAtomList = ringToBeAdded.getCyclicAtomList();
		ringToBeAddedCyclicAtomList.debug();
		
		int nsides = ringToBeAdded.size();
		List<CMLAtom> junctionCommonAtomList = junction.getCommonAtomList();
		int commonAtomCount = junctionCommonAtomList.size();
		CMLAtom tailAtom = junctionCommonAtomList.get(0);
		CMLAtom leadAtom = junctionCommonAtomList.get(commonAtomCount-1);
		existingRingCyclicAtomList.getIndexOfAndCache(tailAtom);
		int steps = existingRingCyclicAtomList.getStepCount(tailAtom, leadAtom);
		if (steps == -1) {
			throw new CMLRuntimeException("dir not determined");
		}
		// swap atoms to ensure direction is always positive
		if (steps > nsides) {
			CMLAtom temp = tailAtom;
			tailAtom = leadAtom;
			leadAtom = temp;
		}
		steps = existingRingCyclicAtomList.getStepCount(tailAtom, leadAtom);
		if (steps > nsides) {
			existingRingCyclicAtomList.debug();
			System.err.println(tailAtom.getId()+"/"+leadAtom.getId());
			throw new CMLRuntimeException("DIR must be positive...");
		}
		// reset to start at leadAtom
		existingRingCyclicAtomList.getIndexOfAndCache(leadAtom);
		CMLAtom atom11 = existingRingCyclicAtomList.getNext();
		if (junctionCommonAtomList.contains(atom11)) {
			throw new CMLRuntimeException("WRONG DIRECTION");
		}
		
		int toBeAddedSteps = ringToBeAddedCyclicAtomList.getStepCount(tailAtom, leadAtom);
		CMLAtom startAtom = leadAtom;
		CMLAtom endAtom = tailAtom;
		int addedDir = 1;
		if (toBeAddedSteps != steps) {
			startAtom = tailAtom;
			endAtom = leadAtom;
			addedDir = -1;
		}
		
		Vector2 junctionVector = new Vector2(startAtom.getXY2().subtract(endAtom.getXY2()));
		double dist0n = startAtom.getXY2().getDistance(endAtom.getXY2());
		int pointn = commonAtomCount-1;
		Real2Vector polyPoints = Real2Vector.partOfRegularPolygon(
				nsides, pointn, dist0n);
		Vector2 polygonVector = alignPolygon(startAtom, endAtom, pointn, polyPoints, junctionVector);
		
		Real2 polyCentroid = polyPoints.getCentroid();
		Real2 existingCentroid = existingRing.getAtomSet().getCentroid2D();
		double interCentroid = polyCentroid.getDistance(existingCentroid);

		Transform2 tt = Transform2.flipAboutVector(polygonVector);
		Real2Vector flipPolyPoints = new Real2Vector(polyPoints);
		flipPolyPoints.transformBy(tt);
		
//		Vector2 flipPolygonVector = alignPolygon(startAtom, endAtom, pointn, flipPolyPoints, junctionVector);
		Real2 flipPolyCentroid = flipPolyPoints.getCentroid();
		double interFlipCentroid = flipPolyCentroid.getDistance(existingCentroid);
		if (interFlipCentroid > interCentroid) {
			polyPoints = flipPolyPoints;
		}
		
		ringToBeAddedCyclicAtomList.getIndexOfAndCache(startAtom);
		double distStartPointN = startAtom.getXY2().getDistance(polyPoints.get(pointn));
		if (distStartPointN > 0.1) {
			System.err.println("DIST TOO LARGE "+distStartPointN);
			System.err.println("DIST "+startAtom.getXY2().getDistance(polyPoints.get(0)));
			addedDir *= -1;
		}
		ringToBeAddedCyclicAtomList.getIndexOfAndCache(startAtom);
		CMLAtom atom1 = (addedDir == 1) ? ringToBeAddedCyclicAtomList.getNext() : ringToBeAddedCyclicAtomList.getPrevious();
		if (junctionCommonAtomList.contains(atom1)) {
			addedDir *= -1;
		}
		ringToBeAddedCyclicAtomList.getIndexOfAndCache(startAtom);
		for (int i = polyPoints.size() - 1; i >= 2; i--) {
			CMLAtom atom = (addedDir == 1) ? ringToBeAddedCyclicAtomList.getNext() : ringToBeAddedCyclicAtomList.getPrevious();
			if (junctionCommonAtomList.contains(atom)) {
				System.err.println("START "+startAtom.getId());
				System.err.println("END "+endAtom.getId());
				throw new CMLRuntimeException("WRONG DIR: "+atom.getId());
			}
//			atom.setXY2(polyPoints.get(i)); 
			coordinateList.add(polyPoints.get(i));
		}
	}
	
	/**
	 * @param startAtom
	 * @param endAtom
	 * @param pointn
	 * @param polyPoints
	 * @return vector
	 * @throws CMLRuntimeException
	 */
	private Vector2 alignPolygon(CMLAtom startAtom, CMLAtom endAtom, int pointn, Real2Vector polyPoints, Vector2 junctionVector) throws CMLRuntimeException {
		double dist0n;
		Vector2 polygonVector = new Vector2(polyPoints.get(0).subtract(polyPoints.get(pointn)));
		Angle rot = polygonVector.getAngleMadeWith(junctionVector);
		System.out.println("ROT"+rot);
		Transform2 rotPoly = new Transform2(rot);
		polyPoints.transformBy(rotPoly);
		polygonVector = new Vector2(polyPoints.get(0).subtract(polyPoints.get(pointn)));
		rot = junctionVector.getAngleMadeWith(polygonVector);
		if (Math.abs(rot.getRadian()) > 0.000001) {
			throw new CMLRuntimeException("BAD ALIGN");
		}
		System.out.println("ROT"+rot);
		System.out.println("EXIST"+startAtom.getXY2()+"/"+endAtom.getXY2());
		dist0n = startAtom.getXY2().getDistance(endAtom.getXY2());
		System.out.println(dist0n);
		System.out.println("POLY"+polyPoints.get(0)+"/"+polyPoints.get(pointn));
		double dpoly = polyPoints.get(0).getDistance(polyPoints.get(pointn));
		System.out.println(dpoly);
//		Real2 shift = points.get(0).subtract(leadAtom.getXY2());
		Real2 shift = startAtom.getXY2().subtract(polyPoints.get(0));
		System.out.println(shift);
		polyPoints.translateBy(shift);
		System.out.println(polyPoints.get(0)+"/"+polyPoints.get(pointn));
		return polygonVector;
	}

	/**
	 * @param omitHydrogens
	 */
	public void findSprouts(boolean omitHydrogens) {
		sproutList = new ArrayList<Sprout>();
		for (CMLAtom atom : this.atomSet.getAtoms()) {
			List<CMLBond> ligandBondList = atom.getLigandBonds();
			for (CMLBond ligandBond : ligandBondList) {
				if (!bondSet.contains(ligandBond)) {
					CMLAtom otherAtom = ligandBond.getOtherAtom(atom);
					if (!(omitHydrogens && "H".equals(otherAtom.getElementType()))) {
						Sprout sprout = new Sprout(atom, ligandBond, this);
						sproutList.add(sprout);
					}
				}
			}
		}
	}
	/** debug.
	 */
	public void debug() {
		this.getBondSet().debug();
	}
	
	/**
	 * @return the connectedNucleusList
	 */
	public List<RingNucleus> getConnectedNucleusList() {
		if (connectedNucleusList == null) {
			getRemoteSproutList();
		}
		return connectedNucleusList;
	}
	/**
	 * @return the junctionList
	 */
	public List<Junction> getJunctionList() {
		return junctionList;
	}
	/**
	 * @param omitHydrogens
	 * @return the sproutList
	 */
	public List<Sprout> getSproutList(boolean omitHydrogens) {
		if (sproutList == null) {
			findSprouts(omitHydrogens);
		}
		return sproutList;
	}
	
	/**
	 * @return sprouts
	 */
	public List<Sprout> getRemoteSproutList() {
		if (remoteSproutList == null) {
			if (sproutList == null) {
				throw new CMLRuntimeException("calculate sprouts first");
			}
			remoteSproutList = new ArrayList<Sprout>();
			for (Sprout sprout : sproutList) {
				Chain chain = sprout.getChain();
				if (chain != null) {
					for (Sprout remoteSprout : chain.getSproutList()) {
						if (!sproutList.contains(remoteSprout)) {
							remoteSproutList.add(remoteSprout);
						}
					}
				}
			}
		}
		return remoteSproutList;
	}

	/**
	 * @return ring count
	 */
	public int size() {
		return this.getRings().size();
	}
	/**
	 * first sorts on ring count, then compares ordered rings
	 * @param ringNucleus
	 * @return -1 0 1
	 */
	public int compareTo(RingNucleus ringNucleus) {
		int result = 0;
		if (this.size() != ringNucleus.size()) {
			result = (this.size() < ringNucleus.size()) ? -1 : 1;
		} else {
			List<Ring> otherRings = ringNucleus.getRings();
			for (int i = 0; i < ringList.size(); i++) {
				result = ringList.get(i).compareTo(otherRings.get(i));
				if (result != 0) {
					break;
				}
			}
		}
		return result;
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
	/**
	 * @return the coordinateList
	 */
	public List<Real2> getCoordinateList() {
		return coordinateList;
	}
}

