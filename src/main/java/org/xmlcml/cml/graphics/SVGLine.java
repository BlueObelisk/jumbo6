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
		setX12(x1, 1);
		setX12(x2, 2);
	}
	/**
	 * @param x12 coordinates of the atom
	 * @param serial 1 or 2
	 */
	private void setX12(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2: ");
		} else {
			this.addAttribute(new Attribute("x"+serial, ""+x12.getX()));
			this.addAttribute(new Attribute("y"+serial, ""+x12.getY()));
		}
	}
//	/**
//	 * @param x2 the x2 to set
//	 */
//	public void setX2(Real2 x2) {
//		if (x2 == null) {
//			System.err.println("null x2");
//		} else {
//			this.addAttribute(new Attribute("x2", ""+x2.getX()));
//			this.addAttribute(new Attribute("y2", ""+x2.getY()));
//		}
//	}

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}
	
}
