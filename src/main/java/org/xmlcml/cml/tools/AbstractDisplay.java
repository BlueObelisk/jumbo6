package org.xmlcml.cml.tools;


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
	
	protected double fontSize = 1;
	protected String color = "black";
	protected String fill = color;
	protected String stroke = color;
	
	protected double opacity = 1.0;
	protected String fontStyle = FONT_STYLE_NORMAL;
	protected String fontWeight = FONT_WEIGHT_NORMAL;
	protected String fontFamily = FONT_SANS_SERIF;
	
	protected boolean omitHydrogens = false;
	protected boolean showChildLabels = false;

	/** do not use.
	 */
	public AbstractDisplay() {
		
	}
	/** copy constructor.
	 * 
	 * @param a
	 */
	public AbstractDisplay(AbstractDisplay a) {
		this.fontSize = a.fontSize;
		this.color = a.color;
		this.fill = a.fill;
		this.stroke = a.stroke;
		this.opacity = a.opacity;
		this.fontStyle = a.fontStyle;
		this.fontWeight = a.fontWeight;
		this.fontFamily = a.fontFamily;
		this.omitHydrogens = a.omitHydrogens;
		this.showChildLabels = a.showChildLabels;
	}

	/** field constructor
	 * 
	 * @param fontSize
	 * @param color
	 * @param fill
	 * @param stroke
	 * @param opacity
	 * @param fontStyle
	 * @param fontWeight
	 * @param fontFamily
	 * @param omitHydrogens
	 */
	public AbstractDisplay(double fontSize, String color, String fill, String stroke, double opacity, String fontStyle, String fontWeight, String fontFamily, boolean omitHydrogens) {
		this.fontSize = fontSize;
		this.color = color;
		this.fill = fill;
		this.stroke = stroke;
		this.opacity = opacity;
		this.fontStyle = fontStyle;
		this.fontWeight = fontWeight;
		this.fontFamily = fontFamily;
		this.omitHydrogens = omitHydrogens;
	}
	
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

}
