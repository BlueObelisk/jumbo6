package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;

import nu.xom.Node;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	final static String TAG = "svg";
	
	/** constructor.
	 * 
	 */
	public SVGSVG() {
		super(TAG);
	}
	
	/** constructor
	 */
	public SVGSVG(SVGSVG element) {
        super((SVGElement) element);
	}
	
//	/** constructor
//	 */
//	public SVGSVG(Element element) {
//        super(element);
//	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGSVG(this);
    }

	/**
	 * @return tag
	 */

	public String getTag() {
		return TAG;
	}

	/** just draw first g element
	 * 
	 */
	protected void drawElement(Graphics2D g2d) {
		if (this.getChildElements().size() > 0) {
			SVGElement g = (SVGElement) this.getChildElements().get(0);
			g.drawElement(g2d);
		}
	}
}
