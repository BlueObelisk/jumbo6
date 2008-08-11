package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Vector2;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class Sprout extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());

	private CMLAtom ringAtom;
	private CMLAtom firstAtom;
	private CMLBond bond;
	private Chain chain;
	private CMLAtomSet ringAtomSet;
	private RingNucleus ringNucleus;
	private MoleculeLayout moleculeDraw;
	
//	private Sprout() {
//		// 
//	}
	/** constructor.
	 * @param ringAtom
	 * @param bond
	 * @param ringNucleus
	 */
	public Sprout(CMLAtom ringAtom,	CMLBond bond, 
			RingNucleus ringNucleus) {
		this.bond = bond;
		this.ringAtom = ringAtom;
		this.firstAtom = bond.getOtherAtom(ringAtom);
		this.ringNucleus = ringNucleus;
		this.moleculeDraw = ringNucleus.getMoleculeDraw();
	}

	/**
	 */
	public void generateCoordinates() {
		if (chain == null) {
			throw new CMLRuntimeException("null chain");
		}
		Real2 sproutVector = getSproutVector();
		firstAtom.setXY2(ringAtom.getXY2().plus(sproutVector));
		chain.calculate2DCoordinates(this, moleculeDraw);
	}
	/**
	 * @return vector
	 * @throws CMLRuntimeException
	 */
	Real2 getSproutVector() throws CMLRuntimeException {
		double bondLength = ((MoleculeDisplay)ringNucleus.getMoleculeDraw().getDrawParameters()).getBondLength();
		if (ringAtom.getX2Attribute() == null) {
			throw new CMLRuntimeException("ringAtom has no coordinates; "+ringAtom.getId());
		}
		if (ringAtomSet == null) {
			ringAtomSet = new CMLAtomSet();
			for (CMLAtom atom : ringAtom.getLigandAtoms()) {
				if (ringNucleus.getAtomSet().contains(atom)) {
					if (atom.getX2Attribute() == null) {
						// this happens when adding sprout to ring within
						// ringSet
//						System.err.println("ring has no coordinates: ");
					} else {
						ringAtomSet.addAtom(atom);
					}
				}
			}
		}
		if (ringAtomSet.size() < 2) {
			throw new CMLRuntimeException("Must have at list 2 ring atoms");
		}
		// calculate sprout coordinates and then recurse to chain
		Real2 centroid = ringAtomSet.getCentroid2D();
		Vector2 v = new Vector2(ringAtom.getXY2().subtract(centroid));
		if (v.getLength() < 0.00001) {
			v = new Vector2(1., 0.);
		}
		Real2 vv = v.getUnitVector().multiplyBy(bondLength);
		return vv;
	}

	/**
	 * @return the bond
	 */
	public CMLBond getBond() {
		return bond;
	}
	/**
	 * @return the chain
	 */
	public Chain getChain() {
		return chain;
	}
	/**
	 * @return the firstAtom
	 */
	public CMLAtom getFirstAtom() {
		return firstAtom;
	}
	/**
	 * @return the ringAtom
	 */
	public CMLAtom getRingAtom() {
		return ringAtom;
	}
	/**
	 * @return string
	 */
	public String toString() {
		String s = ringAtom.getId()+" -> "+firstAtom.getId();
		return s;
	}
	/**
	 * @return the ringAtomSet
	 */
	public CMLAtomSet getRingAtomSet() {
		return ringAtomSet;
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
	 * @return the ringNucleus
	 */
	public RingNucleus getRingNucleus() {
		return ringNucleus;
	}
	/**
	 * @param chain the chain to set
	 */
	public void setChain(Chain chain) {
		this.chain = chain;
	}
}

