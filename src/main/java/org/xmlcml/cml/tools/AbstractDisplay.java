package org.xmlcml.cml.tools;

import java.awt.Color;


/** properties of a CML object.
 * currently motivated by graphics but could be extended
 * @author pm286
 *
 */
public class AbstractDisplay {

	protected final static String FONT_STYLE_NORMAL = "normal";
	protected final static String FONT_STYLE_ITALIC = "italic";

	protected final static String FONT_WEIGHT_NORMAL = "normal";
	protected final static String FONT_WEIGHT_BOLD = "bold";
	
	protected final static String FONT_SANS_SERIF = "helvetica";
	protected final static String FONT_SERIF = "timesRoman";
	protected final static String FONT_MONOSPACE = "monospace";
	
	final static AbstractDisplay DEFAULT = new AbstractDisplay();
	static {
		DEFAULT.setDefaults();
	};
	
	protected String color;
	protected String fill;
	protected double fontSize;
	protected String fontStyle;
	protected String fontWeight;
	protected String fontFamily;
	protected boolean omitHydrogens;
	protected double opacity;
	protected boolean showChildLabels;
	protected String stroke;
	protected String backgroundColor;

	public AbstractDisplay(String color, String fill, double fontSize,
			String fontStyle, String fontWeight, String fontFamily,
			boolean omitHydrogens, double opacity, boolean showChildLabels,
			String stroke, String backgroundColor) {
		super();
		this.color = color;
		this.fill = fill;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
		this.fontFamily = fontFamily;
		this.omitHydrogens = omitHydrogens;
		this.opacity = opacity;
		this.showChildLabels = showChildLabels;
		this.stroke = stroke;
		this.backgroundColor = backgroundColor;
	}

	/** do not use.
	 */
	public AbstractDisplay() {
		init();
	}
	
	protected void init() {
		setDefaults();
	}
	
	protected void setDefaults() {
		color = "black";
		fill = color;
		fontFamily = FONT_SANS_SERIF;
		fontSize = 1;
		fontStyle = FONT_STYLE_NORMAL;
		fontWeight = FONT_WEIGHT_NORMAL;
		omitHydrogens = false;
		opacity = Double.NaN;
		showChildLabels = false;
		stroke = color;
	}
	
	/** copy constructor.
	 * 
	 * @param a
	 */
	public AbstractDisplay(AbstractDisplay a) {
		this.color = a.color;
		this.fill = a.fill;
		this.opacity = a.opacity;
		this.fontFamily = a.fontFamily;
		this.fontSize = a.fontSize;
		this.fontStyle = a.fontStyle;
		this.fontWeight = a.fontWeight;
		this.omitHydrogens = a.omitHydrogens;
		this.showChildLabels = a.showChildLabels;
		this.stroke = a.stroke;
	}

//	/** field constructor
//	 * 
//	 * @param fontSize
//	 * @param color
//	 * @param fill
//	 * @param stroke
//	 * @param opacity
//	 * @param fontStyle
//	 * @param fontWeight
//	 * @param fontFamily
//	 * @param omitHydrogens
//	 */
//	public AbstractDisplay(double fontSize, String color, String fill, String stroke, double opacity, String fontStyle, String fontWeight, String fontFamily, boolean omitHydrogens) {
//		this.fontSize = fontSize;
//		this.color = color;
//		this.fill = fill;
//		this.stroke = stroke;
//		this.opacity = opacity;
//		this.fontStyle = fontStyle;
//		this.fontWeight = fontWeight;
//		this.fontFamily = fontFamily;
//		this.omitHydrogens = omitHydrogens;
//	}
	
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return the fill
	 */
	public String getFill() {
		return fill;
	}
	/**
	 * @param fill the fill to set
	 */
	public void setFillColor(String fill) {
		this.fill = fill;
	}
	/**
	 * @return the opacity
	 */
	public double getOpacity() {
		return opacity;
	}
	/**
	 * @param opacity the opacity to set
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	/**
	 * @return the stroke
	 */
	public String getStroke() {
		return stroke;
	}
	/**
	 * @param stroke the stroke to set
	 */
	public void setStroke(String stroke) {
		this.stroke = stroke;
	}
	/**
	 * @return the fontFamily
	 */
	public String getFontFamily() {
		return fontFamily;
	}
	/**
	 * @param fontFamily the fontFamilyto set
	 */
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}
	/**
	 * @return the fontSize
	 */
	public double getFontSize() {
		return fontSize;
	}
	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}
	/**
	 * @return the fontStyle
	 */
	public String getFontStyle() {
		return fontStyle;
	}
	/**
	 * @param fontStyle the fontStyle to set
	 */
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}
	/**
	 * @return the fontWeight
	 */
	public String getFontWeight() {
		return fontWeight;
	}
	/**
	 * @param fontWeight the fontWeight to set
	 */
	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}
	
	/**
	 * @return the omitHydrogens
	 */
	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}
	/**
	 * @param omitHydrogens 
	 */
	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}
	/**
	 * @param fill the fill to set
	 */
	public void setFill(String fill) {
		this.fill = fill;
	}
	public boolean isShowChildLabels() {
		return showChildLabels;
	}
	public void setShowChildLabels(boolean showChildLabels) {
		this.showChildLabels = showChildLabels;
	}

	// probably not used
	/** cascades through from calling program
	 * @param args
	 * @param i
	 * @return increased i if args found
	 */
	public int processArgs(String[] args, int i) {
		
		if (false) {
		} else if (args[i].equalsIgnoreCase("-FONTSIZE")) {
			this.setFontSize(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-COLOR")) {
			this.setColor(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-FILL")) {
			this.setFill(args[++i]);
		} else if (args[i].equalsIgnoreCase("-STROKE")) {
			this.setStroke(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-OPACITY")) {
			this.setOpacity(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-FONTSTYLE")) {
			this.setFontStyle(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-FONTWEIGHT")) {
			this.setFontWeight(args[++i]);
		} else if (args[i].equalsIgnoreCase("-FONTFAMILY")) {
			this.setFontFamily(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-OMITHYDROGENS")) {
			this.setOmitHydrogens(true); i++;
		} else if (args[i].equalsIgnoreCase("-SHOWCHILDLABELS")) {
			this.setShowChildLabels(true); i++;
		}
		return i;
	}

	// probably not used
	protected static void usage() {
		System.out.println("Display options ");
		System.out.println("  -FONTSIZE size(D)");
		System.out.println("  -COLOR fontColor");
		System.out.println("  -FILL areaFill (includes text)");
		System.out.println("  -STROKE stroke (line but not text)");
		System.out.println("  -OPACITY opacity(D 0-1)");
		System.out.println("  -FONTSTYLE fontStyle");
		System.out.println("  -FONTWEIGHT fontWeight");
		System.out.println("  -FONTFAMILY fontFamily");
		System.out.println("  -OMITHYDROGENS");
		System.out.println("  -SHOWCHILDLABELS");
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
