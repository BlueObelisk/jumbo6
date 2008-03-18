package org.xmlcml.cml.graphics;


/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGDesc extends SVGElement {

	final static String TAG ="desc";

	/** constructor
	 */
	public SVGDesc() {
		super(TAG);
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}
}
