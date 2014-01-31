package org.xmlcml.cml.graphics;

/**
 * supports 'Z' command
 * @author pm286
 *
 */
/**
 * 
 * @author pm286
 * @deprecated "use SVG-DEV package"
 */
@Deprecated

public class ClosePrimitive extends SVGPathPrimitive {

	public final static String TAG = "Z";

	public ClosePrimitive() {
	}
	
	public String getTag() {
		return TAG;
	}
	
	public String toString() {
		return TAG;
	}
}
