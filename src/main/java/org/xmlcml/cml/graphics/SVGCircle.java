package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGCircle extends SVGElement {

	final static String TAG ="circle";
	

	/** constructor
	 */
	public SVGCircle() {
		super(TAG);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGCircle(Real2 x1, double rad) {
		this();
		setX1(x1);
		setRad(rad);
	}
	
	protected void drawElement(Graphics2D g2d) {
		double x = this.getDouble("cx");
		double y = this.getDouble("cy");
		double r = this.getDouble("r");
		Real2 xy0 = new Real2(x, y);
		xy0 = transform(xy0, cumulativeTransform);
		double rad = r * cumulativeTransform.getMatrixAsArray()[0] * 0.5;
		
		Ellipse2D ellipse = new Ellipse2D.Double(xy0.x-rad, xy0.y-rad, rad+rad, rad+rad);
		Color color = this.getColor("fill");
		g2d.setColor(color);
		g2d.fill(ellipse);
	}
	
	/**
	 * @param x1 the x1 to set
	 */
	public void setX1(Real2 x1) {
		this.addAttribute(new Attribute("cx", ""+x1.getX()));
		this.addAttribute(new Attribute("cy", ""+x1.getY()));
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * @param rad the rad to set
	 */
	public void setRad(double rad) {
		this.addAttribute(new Attribute("r", ""+rad));
	}

	
}
