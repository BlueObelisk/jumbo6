package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.main.CMLAtomSet;
import org.xmlcml.cml.element.main.CMLBondSet;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class ChainSet extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());

	private Set<Chain> chainSet;
	private Map<Sprout, Chain> sproutMap;
	private Map<CMLAtom, Chain> atomMap;
	private Map<CMLBond, Chain> bondMap;
	private MoleculeLayout moleculeDraw;
	
	/**
	 */
	public ChainSet() {
		init();
	}
	
	private void init() {
		this.chainSet = new HashSet<Chain>();
		this.sproutMap = new HashMap<Sprout, Chain>();
		this.atomMap = new HashMap<CMLAtom, Chain>();
		this.bondMap = new HashMap<CMLBond, Chain>();
	}

	/**
	 * @param moleculeDraw
	 */
	public ChainSet(MoleculeLayout moleculeDraw) {
		this();
		this.setMoleculeDraw(moleculeDraw);
	}
	
	/** adds new chain and indexes
	 * indexes on atoms, bond, sprouts
	 * @param chain
	 */
	public void add(Chain chain) {
		if (!chainSet.contains(chain)) {
			chainSet.add(chain);
			chain.setMoleculeDraw(moleculeDraw);
			for (Sprout sprout : chain.getSproutList()) {
				if (sproutMap.get(sprout) != null) {
					throw new RuntimeException("sprout in two chains: "+sprout);
				}
				sproutMap.put(sprout, chain);
			}
			CMLAtomSet atomSet = chain.getAtomSet();
			for (CMLAtom atom : atomSet.getAtoms()) {
				if (atomMap.get(atom) != null) {
					throw new RuntimeException("atom in two chains: "+atom.getId());
				}
//				System.out.println("adding: "+atom.getId());
				atomMap.put(atom, chain);
			}
			CMLBondSet bondSet = chain.getBondSet();
			for (CMLBond bond : bondSet.getBonds()) {
				if (bondMap.get(bond) != null) {
					throw new RuntimeException("bond in two chains: "+bond.getId());
				}
//				System.out.println("adding: "+bond.getId());
				bondMap.put(bond, chain);
			}
		}
	}
	
	/** finds chain when there are no ringNuclei.
	 * looks for first bond which is terminal and then expands
	 * @param bondSet
	 */
	public void findOrCreateAndAddChain(CMLBondSet bondSet) {
		for (CMLBond bond : bondSet.getBonds()) {
			CMLAtom atom0 = bond.getAtom(0);
			CMLAtom atom1 = bond.getAtom(1);
			if (atom0.getLigandBonds().size() == 1) {
				findOrCreateAndAddChain(atom0, bond, null);
				break;
			} else if (atom1.getLigandBonds().size() == 1) {
				findOrCreateAndAddChain(atom1, bond, null);
				break;
			}
		}
	}

	/** finds chain to which start belongs.
	 * if none, creates new chain and adds to set
	 * @param sprout
	 * @param ringNucleusSet
	 * @return chain
	 */
	public Chain findOrCreateAndAddChain(Sprout sprout, RingNucleusSet ringNucleusSet) {
		Chain chain = findOrCreateAndAddChain(sprout.getRingAtom(), sprout.getBond(), ringNucleusSet);
		sprout.setChain(chain);
		return chain;
	}
	
	/** finds chain to which start belongs.
	 * if none, creates new chain and adds to set
	 * @param startAtom
	 * @param startBond
	 * @param ringNucleusSet
	 * @return chain
	 */
	public Chain findOrCreateAndAddChain(CMLAtom startAtom, CMLBond startBond, RingNucleusSet ringNucleusSet) {
		Chain chain = this.getAtomMap().get(startAtom);
		if (chain == null) {
			chain = new Chain(moleculeDraw);
			this.expandUntilEndOrSprout(chain, startAtom, startBond, ringNucleusSet);
			this.add(chain);
		}
		return chain;
	}
	
	private void expandUntilEndOrSprout(Chain chain, CMLAtom usedAtom, CMLBond currentBond, 
			RingNucleusSet ringNucleusSet) {
		chain.addBond(currentBond);
		CMLAtom nextAtom = currentBond.getOtherAtom(usedAtom);
		// checkHydrogens
		List<CMLBond> bondList = nextAtom.getLigandBonds();
		int nadded = 0;
		for (CMLBond ligandBond : bondList) {
			CMLAtom otherAtom = ligandBond.getOtherAtom(nextAtom);
			// skip hydrogens if required
			if (((MoleculeDisplay)moleculeDraw.getDrawParameters()).isOmitHydrogens() && AS.H.equals(otherAtom.getElementType())) {
				continue;
			}
			// bond already used
			if (chain.getBondSet().contains(ligandBond)) {
			} else if (ringNucleusSet != null && 
					ringNucleusSet.getBondMap().get(ligandBond) != null) {
			} else if (ringNucleusSet != null && 
					ringNucleusSet.getBondSproutMap().get(ligandBond) != null) {
				// found a sprout
				Sprout otherSprout = ringNucleusSet.getBondSproutMap().get(ligandBond);
				if (chain.getSproutList().contains(otherSprout)) {
					throw new RuntimeException("Sprout detected twice");
				} else {
					chain.addSprout(otherSprout);
				}
			} else {
				nadded++;
				expandUntilEndOrSprout(chain, nextAtom, ligandBond, ringNucleusSet);
			}
		}
		if (nadded == 0) {
			chain.addTerminalBond(nextAtom, currentBond);
		}
	}
	
	/**
	 * @param moleculeDraw
	 */
	public void layout(MoleculeLayout moleculeDraw) {
		for (Chain chain : this.chainSet) {
			chain.calculate2DCoordinates(null, moleculeDraw);
		}
	}
	
	/**
	 * @return the atomMap
	 */
	public Map<CMLAtom, Chain> getAtomMap() {
		return atomMap;
	}

	/**
	 * @return the bondMap
	 */
	public Map<CMLBond, Chain> getBondMap() {
		return bondMap;
	}

	/**
	 * @return the chainSet
	 */
	public Set<Chain> getChainSet() {
		return chainSet;
	}

	/**
	 * @return the sproutMap
	 */
	public Map<Sprout, Chain> getSproutMap() {
		return sproutMap;
	}
	/**
	 * @return string
	 */
	public String toString() {
		String s = "chainSet: ";
		for (Chain chain : chainSet) {
			s += chain+"\n";
		}
		return s;
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

}

