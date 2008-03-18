package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import nu.xom.Attribute;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * NOT TESTED
 * @author pm286
 *
 */
public class SVGEllipse extends SVGElement {

	final static String TAG ="ellipse";

	/** constructor
	 */
	public SVGEllipse() {
		super(TAG);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGEllipse(double cx, double cy, double rx, double ry) {
		this();
		this.addAttribute(new Attribute("cx", ""+cx));
		this.addAttribute(new Attribute("cy", ""+cy));
		this.addAttribute(new Attribute("rx", ""+rx));
		this.addAttribute(new Attribute("ry", ""+ry));
	}
	
	protected void drawElement(Graphics2D g2d) {
		double cx = this.getDouble("cx");
		double cy = this.getDouble("cy");
		double rx = this.getDouble("rx");
		double ry = this.getDouble("rx");
		Real2 xy0 = new Real2(cx, cy);
		xy0 = transform(xy0, cumulativeTransform);
		double rrx = rx * cumulativeTransform.getMatrixAsArray()[0] * 0.5;
		double rry = ry * cumulativeTransform.getMatrixAsArray()[0] * 0.5;
		
		Ellipse2D ellipse = new Ellipse2D.Double(xy0.x-rrx, xy0.y-rry, rrx+rrx, rry+rry);
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
