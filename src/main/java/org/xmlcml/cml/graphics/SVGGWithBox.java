package org.xmlcml.cml.graphics;

import org.xmlcml.euclid.Real2;

import nu.xom.Nodes;

public class SVGGWithBox extends SVGG {

	protected SVGLayout layout;
	private String id;
	
	public SVGGWithBox() {
		
	}
	public static SVGGWithBox createSVGGWithBox(SVGG svgg) {
		SVGGWithBox newBox = new SVGGWithBox();
	    newBox.copyAttributesFrom(svgg);
	    createSubclassedChildren(svgg, newBox);
		return newBox;
	}
	public void setLayout(SVGLayout layout) {
		this.layout = layout;
	}
	public SVGLayout getLayout() {
		return layout;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	private SVGRect getRect(SVGG svgg) {
		Nodes rects = svgg.query("./*[local-name()='"+SVGRect.TAG+"']");
		return (rects.size() == 1) ? (SVGRect) rects.get(0) : null;
	}
	protected Real2 getOffset(SVGG svgg, SVGLayout layout) {
		SVGRect rect = getRect(svgg);
		Real2 offset = new Real2();
		if (rect != null) {
			if (SVGLayout.LEFT2RIGHT.equals(layout)) {
				offset.x = rect.getWidth();
			} else {
				offset.y = rect.getHeight();
			}
		}
		return offset;
	}
}
