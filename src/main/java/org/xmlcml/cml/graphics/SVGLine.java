package org.xmlcml.cml.graphics;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGLine extends SVGElement {

	final static String TAG ="line";

	/** constructor
	 */
	public SVGLine() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}
	
	/** constructot.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGLine(Real2 x1, Real2 x2) {
		this();
		setX1(x1);
		setX2(x2);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param x1 the x1 to set
	 */
	public void setX1(Real2 x1) {
		this.addAttribute(new Attribute("x1", ""+x1.getX()));
		this.addAttribute(new Attribute("y1", ""+x1.getY()));
	}
	/**
	 * @param x2 the x2 to set
	 */
	public void setX2(Real2 x2) {
		this.addAttribute(new Attribute("x2", ""+x2.getX()));
		this.addAttribute(new Attribute("y2", ""+x2.getY()));
	}

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}
	
}
