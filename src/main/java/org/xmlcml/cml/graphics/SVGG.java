package org.xmlcml.cml.graphics;


import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;



/** grouping element
 * 
 * @author pm286
 *
 */
public class SVGG extends SVGElement {

	public final static String TAG ="g";
	private SVGLayout layout;
	private String id;
	
	/** constructor
	 */
	public SVGG() {
		super(TAG);
	}

	public SVGG(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGG(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGG(this);
    }

	protected void copyAttributes(SVGElement element) {
		for (int i = 0; i < element.getAttributeCount(); i++) {
			this.addAttribute(new Attribute(element.getAttribute(i)));
		}
	}
	
	/**
	 * @return tag
	 */
	
	public String getTag() {
		return TAG;
	}

	/**
	 * 
	 * @param width
	 */
	public void setWidth(double width) {
		this.addAttribute(new Attribute("width", ""+width+"px"));
	}

	/**
	 * 
	 * @param height
	 */
	public void setHeight(double height) {
		this.addAttribute(new Attribute("height", ""+height+"px"));
	}

	/**
	 * 
	 * @param scale
	 */
	public void setScale(double scale) {
		this.addAttribute(new Attribute("transform", "scale("+scale+","+scale+")"));
	}
	
	public void setLayout(SVGLayout layout) {
		this.layout = layout;
	}
	
	public List<SVGG> getSVGGChildren() {
		// "./g"
		Nodes gNodes = this.query("./*[local-name()='"+SVGG.TAG+"']");
		List<SVGG> gList = new ArrayList<SVGG>(gNodes.size());
		for (int i = 0; i < gNodes.size(); i++) {
			gList.add((SVGG) gNodes.get(i));
		}
		return gList;
	}

	public void addSVGG(SVGG childSvg) {
		Transform2 childTransform = childSvg.getTransform2FromAttribute();
		if (childTransform == null) {
			childTransform = new Transform2();
		}
		List<SVGG> childSvgs = this.getSVGGChildren();
		if (childSvgs.size() > 0) {
			SVGG lastSVGG = childSvgs.get(childSvgs.size()-1);
			Transform2 lastTransform = lastSVGG.getTransform2FromAttribute();
			Real2 delta = getOffset(lastSVGG, this.layout);
			childTransform = childTransform.concatenate(new Transform2(new Vector2(delta)));
		}
		SVGG g = new SVGG(childSvg);
		g.setTransform(childTransform);
		this.appendChild(g);
	}

	private Real2 getOffset(SVGG svgg, SVGLayout layout) {
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

	private SVGRect getRect(SVGG svgg) {
		Nodes rects = svgg.query("./*[local-name()='"+SVGRect.TAG+"']");
		return (rects.size() == 1) ? (SVGRect) rects.get(0) : null;
	}
	
	public static void createHTMLDisplay(String dirname, List<SVGG> svgList) {
		throw new RuntimeException("NYI");
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
