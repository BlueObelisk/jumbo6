package org.xmlcml.cml.graphics;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	final static String TAG = "svg";
	
	/** constructor.
	 * 
	 */
	public SVGSVG() {
		super(TAG);
	}
	/**
	 * @return tag
	 */

	public String getTag() {
		return TAG;
	}
}
