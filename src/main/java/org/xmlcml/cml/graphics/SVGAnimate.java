package org.xmlcml.cml.graphics;

import nu.xom.Element;
import nu.xom.Node;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGAnimate extends AbstractAnimate {

	public final static String TAG ="animate";
	public static final String SUM = "sum";

	/** constructor
	 */
	public SVGAnimate() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGAnimate(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGAnimate(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGAnimate(this);
    }

    public void setOpacity(double from, double to) {
    	setAttribute(OPACITY, from, to);
    }
    public void setAttribute(String name, double from, double to) {
		this.setAttributeName(name);
    	this.setFrom(from);
    	this.setTo(to);
	}
	public void setFrom(Double from) {
		this.setFrom(""+from);
	}

	public void setTo(Double to) {
		this.setTo(""+to);
	}
}