package org.xmlcml.cml.graphics;



/** grouping element
 * 
 * @author pm286
 *
 */
public class SVGG extends SVGElement {

	final static String TAG ="g";

	/** constructor
	 */
	public SVGG() {
		super(TAG);
	}
	
	/**
	 * @return tag
	 */
	
	public String getTag() {
		return TAG;
	}
	

}
