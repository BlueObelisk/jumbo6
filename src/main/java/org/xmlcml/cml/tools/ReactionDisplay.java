package org.xmlcml.cml.tools;

import java.awt.Color;

import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.RealInterval;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class ReactionDisplay extends AbstractDisplay {

	final static ReactionDisplay DEFAULT = new ReactionDisplay();
	static {
		DEFAULT.moleculeDisplay = MoleculeDisplay.DEFAULT;
	};
	
	private MoleculeDisplay moleculeDisplay = new MoleculeDisplay(MoleculeDisplay.DEFAULT);
	
	public ReactionDisplay() {
		super();
	}
	public ReactionDisplay(ReactionDisplay a) {
		super(a);
		this.moleculeDisplay = new MoleculeDisplay(a.moleculeDisplay);
	}
	/**
	 * @return the moleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}
	/**
	 * @param moleculeDisplay the moleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
	public static ReactionDisplay getDEFAULT() {
		return DEFAULT;
	}
	
}
