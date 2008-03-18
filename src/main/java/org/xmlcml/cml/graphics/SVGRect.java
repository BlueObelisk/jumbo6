package org.xmlcml.cml.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGRect extends SVGElement {

	final static String TAG ="rect";

	/** constructor
	 */
	public SVGRect() {
		super(TAG);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGRect(double x, double y, double w, double h) {
		this();
		this.addAttribute(new Attribute("x", ""+x));
		this.addAttribute(new Attribute("y", ""+y));
		this.addAttribute(new Attribute("w", ""+w));
		this.addAttribute(new Attribute("h", ""+h));
	}
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGRect(Real2 x1, Real2 x2) {
		this(x1.getX(), x1.getY(), x2.getX() - x1.getX(), x2.getY() - x1.getY());
	}
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		double x1 = this.getDouble("x");
		double y1 = this.getDouble("y");
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		double w = this.getDouble("w");
		double h = this.getDouble("h");
		Real2 xy2 = new Real2(x1+w, y1+h);
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
