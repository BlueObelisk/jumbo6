package org.xmlcml.cml.tools;


/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class BondDisplay extends AbstractDisplay {

	private String multipleColor = "white";
	private double width = 1.0;
	private double scale = 1.0;
	
	final static BondDisplay DEFAULT = new BondDisplay();
	static {
		
		DEFAULT.width = 0.08;
		DEFAULT.color = "black";
		DEFAULT.fill = DEFAULT.color;
		DEFAULT.stroke = null;
		DEFAULT.multipleColor = "white";
		
		DEFAULT.opacity = Double.NaN;
	};

	/** constructor.
	 */
	public BondDisplay() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/** copy constructor.
	 * 
	 * @param a
	 */
	public BondDisplay(BondDisplay a) {
		super(a);
		this.multipleColor = a.multipleColor;
		this.width = a.width;
	}
	/** super field constructor.
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
	public BondDisplay(double fontSize, String color, String fill, String stroke, double opacity, String fontStyle, String fontWeight, String fontFamily, boolean omitHydrogens) {
		super(fontSize, color, fill, stroke, opacity, fontStyle, fontWeight,
				fontFamily, omitHydrogens);
	}

	/** field constructor.
	 * 
	 * @param multipleColor
	 * @param width
	 */
	public BondDisplay(String multipleColor, double width) {
		super();
		this.multipleColor = multipleColor;
		this.width = width;
	}
	
	/**
	 * @return the multipleColor
	 */
	public String getMultipleColor() {
		return multipleColor;
	}
	/**
	 * @param multipleColor the multipleColor to set
	 */
	public void setMultipleColor(String multipleColor) {
		this.multipleColor = multipleColor;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * @return the scaled width
	 */
	public double getScaledWidth() {
//		System.out.println("WID "+width+" SCA "+scale);
		return width * scale;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
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
