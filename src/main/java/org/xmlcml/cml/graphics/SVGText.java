package org.xmlcml.cml.graphics;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws text.
 * 
 * @author pm286
 *
 */
public class SVGText extends SVGElement {

	final static String TAG ="text";
	
	/** contructor
	 */
	public SVGText() {
		super(TAG);
//		this.setFontFamily("timesRoman");
//		this.setFontSize(0.1);
		this.setStroke("none");
//		this.setStrokeWidth(0.);
//		this.setFill("blue");
//		this.setFontStyle("normal");
//		this.setFontWeight("normal");
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param text
	 */
	public SVGText(Real2 x1, String text) {
		this();
		setX1(x1);
		setText(text);
	}
	/**
	 * @return the x1
	 */
	public Real2 getX1() {
		return new Real2(
			new Double(getAttributeValue("x")).doubleValue(),
			new Double(getAttributeValue("y")).doubleValue()
		);
	}
	/**
	 * @param x1 the x1 to set
	 */
	public void setX1(Real2 x1) {
		this.addAttribute(new Attribute("x", ""+x1.getX()));
		this.addAttribute(new Attribute("y", ""+x1.getY()));
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.getValue();
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.appendChild(text);
	}

}
