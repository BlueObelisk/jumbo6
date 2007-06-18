package org.xmlcml.cml.graphics;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGCircle extends SVGElement {

	final static String TAG ="circle";
	

	/** constructor
	 */
	public SVGCircle() {
		super(TAG);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGCircle(Real2 x1, double rad) {
		this();
		setX1(x1);
		setRad(rad);
	}
//	/**
//	 * @return the x1
//	 */
//	public Real2 getX1() {
//		return x1;
//	}
	/**
	 * @param x1 the x1 to set
	 */
	public void setX1(Real2 x1) {
		this.addAttribute(new Attribute("cx", ""+x1.getX()));
		this.addAttribute(new Attribute("cy", ""+x1.getY()));
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

//	/**
//	 * @return the rad
//	 */
//	public double getRad() {
//		return ra
//	}

	/**
	 * @param rad the rad to set
	 */
	public void setRad(double rad) {
		this.addAttribute(new Attribute("r", ""+rad));
	}

	
}
