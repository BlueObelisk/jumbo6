package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.xmlcml.euclid.Real2Array;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGTitle extends SVGElement {

	final static String TAG ="title";

	protected Real2Array real2Array;
	
	/** constructor
	 */
	public SVGTitle() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGTitle(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGTitle(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGTitle(String title) {
		this();
		this.appendChild(new Text(title));
	}
	
	protected void init() {
		super.setDefaultStyle();
		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGElement line) {
		line.setStroke("black");
		line.setStrokeWidth(1.0);
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGTitle(this);
    }
	
	protected void drawElement(Graphics2D g2d) {
//		Path2D path = createAndSetPath2D();
		applyAttributes(g2d);
//		g2d.draw(path);
	}

	public void applyAttributes(Graphics2D g2d) {
		if (g2d != null) {
//			float width = (float) this.getStrokeWidth();
//			Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
//			g2d.setStroke(s);
			super.applyAttributes(g2d);
		}
	}

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

}
