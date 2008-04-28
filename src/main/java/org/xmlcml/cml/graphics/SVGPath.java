package org.xmlcml.cml.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPath extends SVGElement {

	final static String TAG ="path";

	/** constructor
	 */
	public SVGPath() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGPath(SVGPath element) {
        super((SVGElement) element);
	}
	
	
	/** constructor
	 */
	public SVGPath(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPath(this);
    }

	public SVGPath(String d) {
		this();
		setD(d);
	}
	
	protected void init() {
		super.setDefaultStyle();
		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGPath path) {
		path.setStroke("black");
		path.setStrokeWidth(0.5);
		path.setFill("none");
	}
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @deprecated
	 */
	public SVGPath(Real2 x1, Real2 x2, Real2 y1, Real2 y2) {
		this();
		setX12(x1, 1);
		setX12(x2, 2);
	}
	/**
	 * @param x12 coordinates of the atom
	 * @param serial 1 or 2
	 */
	private void setX12(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2/y2 in path: ");
		} else {
			this.addAttribute(new Attribute("x"+serial, ""+x12.getX()));
			this.addAttribute(new Attribute("y"+serial, ""+x12.getY()));
		}
	}
	
	public void setD(String d) {
		this.addAttribute(new Attribute("d", d));
	}
	
	public String getD() {
		return this.getAttributeValue("d");
	}
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		double x1 = this.getDouble("x1");
		double y1 = this.getDouble("y1");
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		double x2 = this.getDouble("x2");
		double y2 = this.getDouble("y2");
		Real2 xy2 = new Real2(x2, y2);
		xy2 = transform(xy2, cumulativeTransform);
		float width = 1.0f;
		String style = this.getAttributeValue("style");
		if (style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) new Double(style).doubleValue();
			width *= 15.f;
		}
		
		Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g2d.setStroke(s);
		
		String colorS = "black";
		String stroke = this.getAttributeValue("stroke");
		if (stroke != null) {
			colorS = stroke;
		}
		Color color = colorMap.get(colorS);
		g2d.setColor(color);
		Line2D line = new Line2D.Double(xy1.x, xy1.y, xy2.x, xy2.y);
		g2d.draw(line);
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}
}
