package org.xmlcml.cml.tools;

import java.awt.Color;

import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.RealInterval;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class MoleculeDisplay extends AbstractDisplay {

	final static MoleculeDisplay DEFAULT = new MoleculeDisplay();
	static {
		DEFAULT.fontStyle = FONT_STYLE_NORMAL;
		DEFAULT.fontWeight = FONT_WEIGHT_NORMAL;
		DEFAULT.fontFamily = FONT_SANS_SERIF;
		
		DEFAULT.fontSize = 0.7;
		DEFAULT.color = "black";
		DEFAULT.fill = DEFAULT.color;
		DEFAULT.stroke = null;
		
		DEFAULT.opacity = Double.NaN;
	};
	
	private Color backgroundColor = Color.white;
	private Real2Interval screenExtent = new Real2Interval(
			new RealInterval(0., 200.), new RealInterval(0., 150.));

	
	private AtomDisplay atomDisplay = new AtomDisplay(AtomDisplay.DEFAULT);
	private BondDisplay bondDisplay = new BondDisplay(BondDisplay.DEFAULT);
	
	private double bondLength = 2.0;
	private boolean omitHydrogens = true;

	/**
	 */
	public MoleculeDisplay() {
		super();
	}

	/** copy
	 * @param a
	 */
	public MoleculeDisplay(MoleculeDisplay a) {
		super(a.fontSize, a.color, a.fill, a.stroke, a.opacity, a.fontStyle, a.fontWeight,
				a.fontFamily, a.omitHydrogens);
		this.atomDisplay = new AtomDisplay(a.atomDisplay);
		this.bondDisplay = new BondDisplay(a.bondDisplay);
	}
	
	/**
	 * @return the bondLength
	 */
	public double getBondLength() {
		return bondLength;
	}
	/**
	 * @param bondLength the bondLength to set
	 */
	public void setBondLength(double bondLength) {
		this.bondLength = bondLength;
	}

	/**
	 * @return the omitHydrogens
	 */
	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}

	/**
	 * @param omitHydrogens the omitHydrogens to set
	 */
	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	/**
	 * @return the screenExtent
	 */
	public Real2Interval getScreenExtent() {
		return screenExtent;
	}
	/**
	 * @param screenExtent the screenExtent to set
	 */
	public void setScreenExtent(Real2Interval screenExtent) {
		this.screenExtent = screenExtent;
	}
	/**
	 * @return the dEFAULT
	 */
	public static MoleculeDisplay getDEFAULT() {
		return DEFAULT;
	}
	/**
	 * @return the bondDisplay
	 */
	public BondDisplay getBondDisplay() {
		return bondDisplay;
	}
	/**
	 * @param bondDisplay the bondDisplay to set
	 */
	public void setBondDisplay(BondDisplay bondDisplay) {
		this.bondDisplay = bondDisplay;
	}
	/**
	 * @return the atomDisplay
	 */
	public AtomDisplay getAtomDisplay() {
		return atomDisplay;
	}
	/**
	 * @param atomDisplay the atomDisplay to set
	 */
	public void setAtomDisplay(AtomDisplay atomDisplay) {
		this.atomDisplay = atomDisplay;
	}
	
	
}
