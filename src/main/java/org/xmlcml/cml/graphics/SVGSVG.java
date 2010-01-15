package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Node;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	public final static String TAG = "svg";
	private SVGLayout layout;
	
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
	
	public void setId(String id) {
		this.addAttribute(new Attribute("id", id));
	}
	
	public String getId() {
		return this.getAttributeValue("id");
	}

	public static void createHTMLDisplay(String dirname, List<SVGSVG> svgList) {
		
	}

	public void setLayout(SVGLayout layout) {
		this.layout = layout;
	}

	public void addSVG(SVGSVG reactantSVG) {
		throw new RuntimeException("NYI");
	}
}
