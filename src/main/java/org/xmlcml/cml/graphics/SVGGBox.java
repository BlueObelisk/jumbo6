package org.xmlcml.cml.graphics;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

import nu.xom.Nodes;

public class SVGGBox extends SVGG {

	protected SVGLayout layout;
	private String id;
	
	public SVGGBox() {
		
	}
	public static SVGGBox copy(SVGGBox box) {
		SVGGBox newBox = new SVGGBox();
	    newBox.copyAttributesFrom(box);
	    createSubclassedChildren(box, newBox);
		return newBox;
	}
	
	public static SVGGBox createSVGGBox(SVGG svgg) {
		SVGGBox newBox = new SVGGBox();
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
	private SVGRect getRect() {
		Nodes rects = this.query("./*[local-name()='"+SVGRect.TAG+"']");
		return (rects.size() == 1) ? (SVGRect) rects.get(0) : null;
	}
	protected Real2 getOffset(SVGLayout layout) {
		SVGRect rect = this.getRect();
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
	
	public void addSVGG(SVGGBox childSvg) {
		Transform2 childTransform = childSvg.getTransform2FromAttribute();
		if (childTransform == null) {
			childTransform = new Transform2();
		}
		Real2 totalDelta = new Real2();
		List<SVGGBox> previousChildSvgs = this.getSVGGBoxChildren();
		if (previousChildSvgs.size() > 0) {
			for (SVGGBox previousChildSvg : previousChildSvgs) {
				Real2 delta = previousChildSvg.getOffset(this.layout);
				totalDelta.plusEquals(delta);
			}
			childTransform = childTransform.concatenate(new Transform2(new Vector2(totalDelta)));
		}
		SVGGBox g = SVGGBox.copy(childSvg);
		g.setTransform(childTransform);
		this.appendChild(g);
	}
	
	public List<SVGGBox> getSVGGBoxChildren() {
		// "./g"
		Nodes gNodes = this.query("./*[local-name()='"+SVGG.TAG+"']");
		List<SVGGBox> gList = new ArrayList<SVGGBox>(gNodes.size());
		for (int i = 0; i < gNodes.size(); i++) {
			gList.add(SVGGBox.createSVGGBox((SVGG)gNodes.get(i)));
		}
		return gList;
	}
	
	public void detachRect() {
		SVGRect rect = getRect();
		if (rect != null) {
			rect.detach();
		} else {
			this.debug("DDDDDDDDDDDDDDDDDD");
		}
	}
}
