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
		DEFAULT.setDefaults();
	}

	// atom
	protected double backgroundChargeRadiusFactor;
	protected double backgroundIdRadiusFactor;
	protected double backgroundRadiusFactor;
	protected double chargeFontFactor;
	protected boolean displayCarbons;
	protected boolean displayLabels;
	protected double idFontFactor;
	protected double scale;
	protected double xChargeOffsetFactor;
	protected double xIdOffsetFactor;
	protected double xOffsetFactor;
	protected double yChargeOffsetFactor;
	protected double yIdOffsetFactor;
	protected double yOffsetFactor;
	
	
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
		super();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();
		backgroundChargeRadiusFactor = 0.4;
		backgroundIdRadiusFactor = 0.4;
		backgroundRadiusFactor = 0.4;
		chargeFontFactor = 0.5;
		chargeFontFactor = 0.8; // which?
		displayCarbons = false;
		displayLabels = false;
		idFontFactor = 0.5;
		scale = 1.0;
		xChargeOffsetFactor = -0.5;
		xChargeOffsetFactor = 0.7; // which?
		xIdOffsetFactor = -0.1;
		xOffsetFactor = -0.5;
		yChargeOffsetFactor = -0.7;
		yChargeOffsetFactor = -0.5; // which?
		yIdOffsetFactor = -0.0;
		yOffsetFactor = 0.7;
		// charge
		
		
		//
		color = "black";
		fill = color;
		fontFamily = FONT_SANS_SERIF;
		fontSize = 0.35;
		fontStyle = FONT_STYLE_NORMAL;
		fontWeight = FONT_WEIGHT_NORMAL;
		stroke = null;
				
		xOffsetFactor = -0.34;
		yOffsetFactor = 0.35;
		// charge


	}
	
	
	/** copy constructor.
	 * 
	 * @param a
	 */
	public AtomDisplay(AtomDisplay a) {
		super(a);
		this.backgroundChargeRadiusFactor= a.backgroundChargeRadiusFactor ;
		this.backgroundIdRadiusFactor= a.backgroundIdRadiusFactor ;
		this.backgroundRadiusFactor= a.backgroundRadiusFactor ;
		this.chargeFontFactor= a.chargeFontFactor ;
		this.displayCarbons= a.displayCarbons ;
		this.displayLabels= a.displayLabels ;
		this.idFontFactor= a.idFontFactor ;
		this.scale= a.scale ;
		this.xChargeOffsetFactor= a.xChargeOffsetFactor ;
		this.xIdOffsetFactor= a.xIdOffsetFactor ;
		this.xOffsetFactor= a.xOffsetFactor ;
		this.yChargeOffsetFactor= a.yChargeOffsetFactor ;
		this.yIdOffsetFactor= a.yIdOffsetFactor ;
		this.yOffsetFactor= a.yOffsetFactor ;
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
	
	/** cascades through from calling program
	 * @param args
	 * @param i
	 * @return increased i if args found
	 */
	public int processArgs(String[] args, int i) {
		// charge
		
		if (false) {
		} else if (args[i].equalsIgnoreCase("-ATOM_XOFFSETFACTOR")) {
			this.setXOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_YOFFSETFACTOR")) {
			this.setYOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_BACKGROUNDRADIUSFACTOR")) {
			this.setBackgroundRadiusFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_CHARGEFONTFACTOR")) {
			this.setChargeFontFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_XCHARGEOFFSETFACTOR")) {
			this.setXChargeOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_YCHARGEOFFSETFACTOR")) {
			this.setYChargeOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_BACKGROUNDCHARGERADIUSFACTOR")) {
			this.setBackgroundChargeRadiusFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_XIDOFFSETFACTOR")) {
			this.setXIdOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_YIDOFFSETFACTOR")) {
			this.setYIdOffsetFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_BACKGROUNDIDOFFSETFACTOR")) {
			this.setBackgroundIdRadiusFactor(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_DISPLAYCARBONS")) {
			this.setDisplayCarbons(true); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_DISPLAYLABELS")) {
			this.setDisplayLabels(true); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_FONTSIZE")) {
			this.setFontSize(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_COLOR")) {
			this.setColor(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_FILL")) {
			this.setFill(args[++i]);
		} else if (args[i].equalsIgnoreCase("-ATOM_STROKE")) {
			this.setStroke(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_OPACITY")) {
			this.setOpacity(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_FONTSTYLE")) {
			this.setFontStyle(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_FONTWEIGHT")) {
			this.setFontWeight(args[++i]);
		} else if (args[i].equalsIgnoreCase("-ATOM_FONTFAMILY")) {
			this.setFontFamily(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_SHOWHYDROGENS")) {
			this.setOmitHydrogens(false); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_OMITHYDROGENS")) {
			this.setOmitHydrogens(true); i++;
		} else if (args[i].equalsIgnoreCase("-ATOM_SHOWCHILDLABELS")) {
			this.setShowChildLabels(true); i++;
		}
		return i;
	}

	public static void usage() {
		
		System.out.println(" AtomDisplay options ");
	    System.out.println("    -ATOM_XOFFSETFACTOR value");
	    System.out.println("    -ATOM_YOFFSETFACTOR value");
	    System.out.println("    -ATOM_BACKGROUNDRADIUSFACTOR value");
	    System.out.println("    -ATOM_CHARGEFONTFACTOR value");
	    System.out.println("    -ATOM_XCHARGEOFFSETFACTOR value");
	    System.out.println("    -ATOM_YCHARGEOFFSETFACTOR value");
	    System.out.println("    -ATOM_BACKGROUNDCHARGERADIUSFACTOR value");
	    System.out.println("    -ATOM_XIDOFFSETFACTOR value");
	    System.out.println("    -ATOM_YIDOFFSETFACTOR value");
	    System.out.println("    -ATOM_BACKGROUNDIDOFFSETFACTOR value");
	    System.out.println("    -ATOM_DISPLAYCARBONS");
	    System.out.println("    -ATOM_DISPLAYLABELS");
		System.out.println("    -ATOM_FONTSIZE size(D)");
		System.out.println("    -ATOM_COLOR fontColor");
		System.out.println("    -ATOM_FILL areaFill (includes text)");
		System.out.println("    -ATOM_STROKE stroke (line but not text)");
		System.out.println("    -ATOM_OPACITY opacity(D 0-ATOM_1)");
		System.out.println("    -ATOM_FONTSTYLE fontStyle");
		System.out.println("    -ATOM_FONTWEIGHT fontWeight");
		System.out.println("    -ATOM_FONTFAMILY fontFamily");
		System.out.println("    -ATOM_OMITHYDROGENS");
		System.out.println("    -ATOM_SHOWHYDROGENS");
		System.out.println("    -ATOM_SHOWCHILDLABELS");
		System.out.println();
	}
}
