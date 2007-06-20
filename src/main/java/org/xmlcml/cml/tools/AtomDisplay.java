package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLAtom;


/** display parameters
 * 
 * @author pm286
 *
 */
public class AtomDisplay extends AbstractDisplay {

	final static AtomDisplay DEFAULT = new AtomDisplay();
	static {
		DEFAULT.fontStyle = FONT_STYLE_NORMAL;
		DEFAULT.fontWeight = FONT_WEIGHT_NORMAL;
		DEFAULT.fontFamily = FONT_SANS_SERIF;
		
		DEFAULT.fontSize = 1.0;
		DEFAULT.color = "black";
		DEFAULT.fill = DEFAULT.color;
		DEFAULT.stroke = null;
		
		DEFAULT.opacity = 1.;
		
		DEFAULT.xOffsetFactor = -0.34;
		DEFAULT.yOffsetFactor = 0.35;
		DEFAULT.backgroundRadiusFactor = 0.4;
		// charge
		DEFAULT.chargeFontFactor = 0.8;
		DEFAULT.xChargeOffsetFactor = 0.7;
		DEFAULT.yChargeOffsetFactor = -0.5;
		DEFAULT.backgroundChargeRadiusFactor = 0.4;

	}

	// atom
	private double xOffsetFactor = -0.5;
	private double yOffsetFactor = 0.7;
	private double backgroundRadiusFactor = 0.4;
	// charge
	private double chargeFontFactor = 0.5;
	private double xChargeOffsetFactor = -0.5;
	private double yChargeOffsetFactor = -0.7;
	private double backgroundChargeRadiusFactor = 0.4;

	/** constructor.
	 */
	public AtomDisplay() {
		
	}
	
	/** field constructor.
	 * 
	 * @param offsetFactor
	 * @param offsetFactor2
	 * @param backgroundRadiusFactor
	 * @param chargeFontFactor
	 * @param chargeOffsetFactor
	 * @param chargeOffsetFactor2
	 * @param backgroundChargeRadiusFactor
	 */
	public AtomDisplay(double offsetFactor, double offsetFactor2, double backgroundRadiusFactor, double chargeFontFactor, double chargeOffsetFactor, double chargeOffsetFactor2, double backgroundChargeRadiusFactor) {
		super();
		xOffsetFactor = offsetFactor;
		yOffsetFactor = offsetFactor2;
		this.backgroundRadiusFactor = backgroundRadiusFactor;
		this.chargeFontFactor = chargeFontFactor;
		xChargeOffsetFactor = chargeOffsetFactor;
		yChargeOffsetFactor = chargeOffsetFactor2;
		this.backgroundChargeRadiusFactor = backgroundChargeRadiusFactor;
	}

	
	/** copy constructor.
	 * 
	 * @param a
	 */
	public AtomDisplay(AtomDisplay a) {
		super(a);
		xOffsetFactor = a.xOffsetFactor;
		yOffsetFactor = a.yOffsetFactor;
		this.backgroundRadiusFactor = a.backgroundRadiusFactor;
		this.chargeFontFactor = a.chargeFontFactor;
		xChargeOffsetFactor = a.xChargeOffsetFactor;
		yChargeOffsetFactor = a.yChargeOffsetFactor;
		this.backgroundChargeRadiusFactor = a.backgroundChargeRadiusFactor;
	}
	
	/**
	 * @return the dEFAULT
	 */
	public static AtomDisplay getDEFAULT() {
		return DEFAULT;
	}
	/**
	 * @return the backgroundChargeRadiusFactor
	 */
	public double getBackgroundChargeRadiusFactor() {
		return backgroundChargeRadiusFactor;
	}
	/**
	 * @param backgroundChargeRadiusFactor the backgroundChargeRadiusFactor to set
	 */
	public void setBackgroundChargeRadiusFactor(double backgroundChargeRadiusFactor) {
		this.backgroundChargeRadiusFactor = backgroundChargeRadiusFactor;
	}
	/**
	 * @return the backgroundRadiusFactor
	 */
	public double getBackgroundRadiusFactor() {
		return backgroundRadiusFactor;
	}
	/**
	 * @param backgroundRadiusFactor the backgroundRadiusFactor to set
	 */
	public void setBackgroundRadiusFactor(double backgroundRadiusFactor) {
		this.backgroundRadiusFactor = backgroundRadiusFactor;
	}
	/**
	 * @return the chargeFontFactor
	 */
	public double getChargeFontFactor() {
		return chargeFontFactor;
	}
	/**
	 * @param chargeFontFactor the chargeFontFactor to set
	 */
	public void setChargeFontFactor(double chargeFontFactor) {
		this.chargeFontFactor = chargeFontFactor;
	}
	/**
	 * @return the xChargeOffsetFactor
	 */
	public double getXChargeOffsetFactor() {
		return xChargeOffsetFactor;
	}
	/**
	 * @param chargeOffsetFactor the xChargeOffsetFactor to set
	 */
	public void setXChargeOffsetFactor(double chargeOffsetFactor) {
		xChargeOffsetFactor = chargeOffsetFactor;
	}
	/**
	 * @return the xOffsetFactor
	 */
	public double getXOffsetFactor() {
		return xOffsetFactor;
	}
	/**
	 * @param offsetFactor the xOffsetFactor to set
	 */
	public void setXOffsetFactor(double offsetFactor) {
		xOffsetFactor = offsetFactor;
	}
	/**
	 * @return the yChargeOffsetFactor
	 */
	public double getYChargeOffsetFactor() {
		return yChargeOffsetFactor;
	}
	/**
	 * @param chargeOffsetFactor the yChargeOffsetFactor to set
	 */
	public void setYChargeOffsetFactor(double chargeOffsetFactor) {
		yChargeOffsetFactor = chargeOffsetFactor;
	}
	/**
	 * @return the yOffsetFactor
	 */
	public double getYOffsetFactor() {
		return yOffsetFactor;
	}
	/**
	 * @param offsetFactor the yOffsetFactor to set
	 */
	public void setYOffsetFactor(double offsetFactor) {
		yOffsetFactor = offsetFactor;
	}

	/** can atom be omitted?
	 * 
	 * @param atom
	 * @return omit
	 */
	public boolean omitAtom(CMLAtom atom) {
		boolean omit = false;
		if (this.isOmitHydrogens()) {
			String elementType = atom.getElementType();
			if (elementType != null && elementType.equals("H")) {
				omit = true;
			}
		}
		return omit;
	}
	
}
