package org.xmlcml.cml.graphics;

public class SVGLayout {

	private static final String LEFT2RIGHTS = null;
	private static final String TOP2BOTTOMS = null;
	public static final SVGLayout LEFT2RIGHT = new SVGLayout(LEFT2RIGHTS);
	public static final SVGLayout TOP2BOTTOM = new SVGLayout(TOP2BOTTOMS);
	
	private String layoutS;
	private SVGLayout(String layout) {
		this.layoutS = layout;
	}

	
}
