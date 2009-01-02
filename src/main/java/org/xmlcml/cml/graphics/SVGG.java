package org.xmlcml.cml.graphics;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2Range;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;



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

	public SVGG(SVGG element) {
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

	/** traverse all children recursively
	 * 
	 * @return null by default
	 */
	public Real2Range getBoundingBox() {
		Real2Range boundingBox = null;
		Nodes childNodes = this.query("./svg:*", CMLConstants.SVG_XPATH);
		if (childNodes.size() > 0) {
			boundingBox = new Real2Range();
		}
		for (int i = 0; i < childNodes.size(); i++) {
			Real2Range childBoundingBox = ((SVGElement)childNodes.get(i)).getBoundingBox();
			if (boundingBox != null) {
				boundingBox = boundingBox.plus(childBoundingBox);
			}
		}
		return boundingBox;
	}
}
