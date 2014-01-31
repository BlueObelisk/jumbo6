package org.xmlcml.cml.graphics;

/**
 * @deprecated "use SVG-DEV package"
 */
@Deprecated

public class UnknownPrimitive extends SVGPathPrimitive {

	private String TAG = "?";

	public UnknownPrimitive(char cc) {
		this.TAG = ""+cc;
	}

	public String getTag() {
		return TAG;
	}
	
	public String toString() {
		return TAG;
	}

}
