package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class RingNucleus extends AbstractTool implements Comparable<RingNucleus> {
	final static Logger LOG = Logger.getLogger(RingNucleus.class);
	
	private CMLAtomSet atomSet;
	private CMLBondSet bondSet;
	private List<Ring> ringList = null;
	private List<Junction> junctionList = null;
	private Map<CMLAtom, BridgeAtom> bridgeAtomMap = null;
	private MoleculeLayout moleculeDraw;
	private List<RingNucleus> connectedNucleusList;
	private List<Sprout> sproutList;
	private List<Sprout> remoteSproutList;
	private List<Real2> coordinateList;

//	private RingNucleus() {
//		init();
//		// 
//	}
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
//				LOG.debug(""+newBondSet.size()+" < "+target.size());
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
	public void layout(Sprout sprout, int cycles) {
		if (cycles-- <= 0) {
			throw new RuntimeException("layout recurses too deeply");
		}
		Real2 oldSproutVector = null;
		Real2 oldRingXY2 = null;
		Real2 oldFirstXY2 = null;
		if (sprout != null) {
			oldRingXY2 = sprout.getRingAtom().getXY2();
			oldFirstXY2 = sprout.getFirstAtom().getXY2();
			if (oldFirstXY2 == null) {
				System.err.println("null sprout vector");
				return;
			} else {
				oldSproutVector = oldRingXY2.subtract(oldFirstXY2);
			}
		}
		this.add2DCoordinates();
		if (sprout != null && oldSproutVector != null) {
			Real2 sproutVector = sprout.getSproutVector();
			if (sproutVector != null) {
				Angle a = new Vector2(sproutVector).getAngleMadeWith(new Vector2(oldSproutVector));
				Transform2 transform2 = new Transform2(a.plus(new Angle(Math.PI)));
				atomSet.transform(transform2);
				Real2 delta2 = oldRingXY2.subtract(sprout.getRingAtom().getXY2());
				atomSet.translate2D(delta2);
			}
		}
		getSproutList(((MoleculeDisplay)moleculeDraw.getAbstractDisplay()).isOmitHydrogens());
		for (Sprout otherSprout : sproutList) {
			if (otherSprout != sprout) {
				Chain chain = moleculeDraw.getSproutMap().get(otherSprout);
				try {
					chain.layout(otherSprout, cycles);
				} catch (RuntimeException e) {
					// cut down on stack print
					if (e.getMessage().equals("layout recurses too deeply")) {
						e = new RuntimeException("layout recurses too deeply");
					}
					throw e;
				}
			}
		}
	}

	/** add 2D coordinates
	 */
	public void add2DCoordinates() {
		getSetOfSmallestRings(true);
		this.getJunctions();
		if (ringList.size() == 0) {
			LOG.error("ring nucleus has no rings");
		} else {
			// get largest ring
			Collections.sort(ringList);
			Collections.reverse(ringList);
			List<Ring> oldRingList = new ArrayList<Ring>();
			for (Ring ring : ringList) {
				oldRingList.add(ring);
			}
			List<Ring> newRingList = new ArrayList<Ring>();
	// add first ring
			Ring currentRing = ringList.get(0);
			currentRing.calculate2DCoordinates(moleculeDraw);
			currentRing.updateCoordinates();
			oldRingList.remove(currentRing);
			newRingList.add(currentRing);
			
			Junction junction = null;
	//		findSprouts(true);
			while (true) {
				junction = findNextJunctionUpdateListsAdd2DCoordinates(
					oldRingList, newRingList);
				if (junction == null) {
					break;
				}
			}
			if (oldRingList.size() > 0) {
				throw new RuntimeException("Undrawn rings ");
			}
		}
	}
	/**
	 * @param remainingRingList
	 * @param growingRingList
	 * @param change
	 * @return junction or null
	 * @throws RuntimeException
	 */
	private Junction findNextJunctionUpdateListsAdd2DCoordinates(List<Ring> remainingRingList, List<Ring> growingRingList) throws RuntimeException {
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
					throw new RuntimeException("Cannot layout ring pair with multiple junctions");
				}
				Junction junction = junctionList.get(0);
				if (junction.getCommonAtomList().size() > 2) {
					LOG.debug("complex junction");
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
//			for (CMLAtom atom : existingRing0.getCyclicAtomList()) {
//				LOG.debug("JUNCT: "+atom.getElementType()+"/"+atom.getXY2());
//			}
			try {
				calculate2DCoordinates(junction0, ringToBeAdded0, existingRing0, moleculeDraw);
				ringToBeAdded0.updateCoordinates();
				growingRingList.add(ringToBeAdded0);
				remainingRingList.remove(ringToBeAdded0);
			} catch (RuntimeException e) {
				throw e;
			}
		}
		return junction0;
	}
	
	private void calculate2DCoordinates(
		Junction junction, Ring ringToBeAdded, Ring existingRing, MoleculeLayout moleculeDraw) 
		throws RuntimeException {
		ringToBeAdded.calculate2DCoordinates(moleculeDraw);
		List<CMLAtom> junctionCommonAtomList = junction.getCommonAtomList();
		int commonAtomCount = junctionCommonAtomList.size();
		CMLAtom tailAtom = junctionCommonAtomList.get(0);
		CMLAtom leadAtom = junctionCommonAtomList.get(commonAtomCount-1);
// distance between existing bridge atoms	
		Real2 oldVector = leadAtom.getXY2().subtract(tailAtom.getXY2());
		Real2 tailXY2 = ringToBeAdded.getAtomCoordMap().get(tailAtom);
		Real2 leadXY2 = ringToBeAdded.getAtomCoordMap().get(leadAtom);
		// and distance in initial new ring
		Real2 newVector = leadXY2.subtract(tailXY2);
		// get scale and scale new ring to be of correct size
		double scale = oldVector.getLength() / newVector.getLength();
		ringToBeAdded.multiplyCoordMap(scale);
		
		tailXY2 = ringToBeAdded.getAtomCoordMap().get(tailAtom);
		leadXY2 = ringToBeAdded.getAtomCoordMap().get(leadAtom);
		// new vector should be of same length as old one
		newVector = leadXY2.subtract(tailXY2);
		// rotate new ring so old and new coordinates of bridge atoms 
		// are aligned.
		Angle angle = new Vector2(newVector).getAngleMadeWith(new Vector2(oldVector));
		
		Transform2 transform = new Transform2(angle);
		ringToBeAdded.transformBy(transform);
		// now find if the new ring is oriented "away" from old ring
		Sprout tailSprout = getSprout(ringToBeAdded, tailAtom);
		Sprout leadSprout = getSprout(ringToBeAdded, leadAtom);
		if (tailSprout == null || leadSprout == null) {
			LOG.error("Null sprout");
//			throw new RuntimeException("Null sprout - cannot draw");
		} else {
			//resultant sprout from old ring
			Real2 oldRingMedianSprout = tailSprout.getSproutVector().plus(leadSprout.getSproutVector());
			CMLAtom nextLeadAtom = getNextNewRingAtom(ringToBeAdded, existingRing, leadAtom);
			CMLAtom nextTailAtom = getNextNewRingAtom(ringToBeAdded, existingRing, tailAtom);
			Real2 nextTailXY2 = ringToBeAdded.getAtomCoordMap().get(nextTailAtom);
			Real2 nextLeadXY2 = ringToBeAdded.getAtomCoordMap().get(nextLeadAtom);
			
			Real2 nextLeadVector = leadXY2.subtract(nextLeadXY2);
			Real2 nextTailVector = tailXY2.subtract(nextTailXY2);
			//resultant sprout from new ring
			Real2 newRingMedianSprout = nextLeadVector.plus(nextTailVector);
			
			angle = new Vector2(oldRingMedianSprout).getAngleMadeWith(
					new Vector2(newRingMedianSprout));
			double angleRad = Math.abs(angle.getRadian());
			// if point in same direction flip the ring
			if (angleRad < Math.PI/2) {
				Transform2 tt = Transform2.flipAboutVector(oldVector);
				ringToBeAdded.transformBy(tt);
			}
			// and translate to merge the rings
			Real2 delta = leadAtom.getXY2().subtract(leadXY2);
			ringToBeAdded.translateCoordMap(delta);
		}
	}
	
	private CMLAtom getNextNewRingAtom(
			Ring ringToBeAdded, Ring existingRing, CMLAtom ringAtom) {
		List<CMLAtom> ligands = ringAtom.getLigandAtoms();
		CMLAtom nextAtom = null;
		for (CMLAtom ligand : ligands) {
			if (ringToBeAdded.getAtomSet().contains(ligand) &&
					!existingRing.getAtomSet().contains(ligand)) {
				nextAtom = ligand;
				break;
			}
		}
		return nextAtom;
	}
	/**
	 * @param ringToBeAdded
	 * @param ringAtom
	 * @return sprout
	 */
	private Sprout getSprout(Ring ringToBeAdded, CMLAtom ringAtom) {
		Sprout sprout = null;
		CMLBond sproutBond = null;
		List<CMLAtom> ligands = ringAtom.getLigandAtoms();
		List<CMLBond> ligandBonds = ringAtom.getLigandBonds();
		for (int i = 0; i < ligands.size(); i++) {
			CMLAtom ligand = ligands.get(i);
			if (ligand.getXY2() == null && 
				ringToBeAdded.getAtomSet().contains(ligand)) {
				sproutBond = ligandBonds.get(i);
			}
		}
		if (sproutBond != null) {
			sprout = new Sprout(ringAtom, sproutBond, this);
		}
		return sprout;
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
					if (!(omitHydrogens && AS.H.equals(otherAtom.getElementType()))) {
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
				throw new RuntimeException("calculate sprouts first");
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
	 * @return the coordinateList
	 */
	public List<Real2> getCoordinateList() {
		return coordinateList;
	}
}

