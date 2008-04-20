package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.molutil.ChemicalElement.AS;


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
		
		DEFAULT.fontSize = 0.35;
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
	
	private boolean displayCarbons = false;
	private boolean displayLabels = false;
	private double idFontFactor = 0.5;
	private double xIdOffsetFactor = -0.1;
	private double yIdOffsetFactor = -0.0;
	private double backgroundIdRadiusFactor = 0.4;
	
	private double scale = 1.0;
	/**
	 * @return the displayLabels
	 */
	public boolean isDisplayLabels() {
		return displayLabels;
	}

	/**
	 * @param displayLabels the displayLabels to set
	 */
	public void setDisplayLabels(boolean displayLabels) {
		this.displayLabels = displayLabels;
	}

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
			if (AS.H.equals(atom.getElementType())) {
				System.out.println("OMIT H");
				omit = true;
			}
		}
		return omit;
	}

	/**
	 * @return the backgroundIdRadiusFactor
	 */
	public double getBackgroundIdRadiusFactor() {
		return backgroundIdRadiusFactor;
	}

	/**
	 * @param backgroundIdRadiusFactor the backgroundIdRadiusFactor to set
	 */
	public void setBackgroundIdRadiusFactor(double backgroundIdRadiusFactor) {
		this.backgroundIdRadiusFactor = backgroundIdRadiusFactor;
	}

	/**
	 * @return the idFontFactor
	 */
	public double getIdFontFactor() {
		return idFontFactor;
	}

	/**
	 * @param idFontFactor the idFontFactor to set
	 */
	public void setIdFontFactor(double idFontFactor) {
		this.idFontFactor = idFontFactor;
	}

	/**
	 * @return the xIdOffsetFactor
	 */
	public double getXIdOffsetFactor() {
		return xIdOffsetFactor;
	}

	/**
	 * @param idOffsetFactor the xIdOffsetFactor to set
	 */
	public void setXIdOffsetFactor(double idOffsetFactor) {
		xIdOffsetFactor = idOffsetFactor;
	}

	/**
	 * @return the yIdOffsetFactor
	 */
	public double getYIdOffsetFactor() {
		return yIdOffsetFactor;
	}

	/**
	 * @param idOffsetFactor the yIdOffsetFactor to set
	 */
	public void setYIdOffsetFactor(double idOffsetFactor) {
		yIdOffsetFactor = idOffsetFactor;
	}

	/**
	 * @return the displayCarbons
	 */
	public boolean isDisplayCarbons() {
		return displayCarbons;
	}

	/**
	 * @param displayCarbons the displayCarbons to set
	 */
	public void setDisplayCarbons(boolean displayCarbons) {
		this.displayCarbons = displayCarbons;
	}
	
	/**
	 */
	public double getScaledFontSize() {
		return scale * fontSize;
	}
	
	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}
	/**
	 * @param width the width to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
}
