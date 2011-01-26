package org.xmlcml.cml.graphics;

import nu.xom.Element;

import nu.xom.Node;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGAnimateTransform extends AbstractAnimate {

	public final static String TAG ="animateTransform";
	private static final String ATTRIBUTE_TYPE = "attributeType";
	private static final String XML = "XML";
	private static final String TYPE = "type";
	private static final String ADDITIVE = "additive";

	/** constructor
	 */
	public SVGAnimateTransform() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGAnimateTransform(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGAnimateTransform(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGAnimateTransform(this);
    }

	public void setTransform(String type, Real2 from, Real2 to) {
		setAttribute(TRANSFORM, from, to);
    	setAttribute(ATTRIBUTE_TYPE, XML);
    	setAttribute(TYPE, type);
	}

     public void setAttribute(String name, Real2 from, Real2 to) {
		this.setAttributeName(name);
    	this.setFrom(from);
    	this.setTo(to);
	}

 	public void setFrom(Real2 from) {
		this.setFrom(""+from.getX()+","+from.getY());
	}

	public void setTo(Real2 to) {
		this.setTo(""+to.getX()+","+to.getY());
	}

	public void setAdditive(String value) {
		this.setAttribute(ADDITIVE, value);
	}
}