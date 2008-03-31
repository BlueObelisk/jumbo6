package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGCircle extends SVGElement {

	final static String TAG ="circle";
//	double cx;
//	double cy;
//	double r;
	

	/** constructor
	 */
	public SVGCircle() {
		super(TAG);
	}
	
	/** constructor
	 */
	public SVGCircle(SVGCircle element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGCircle(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGCircle(this);
    }

	
	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGCircle(Real2 x1, double rad) {
		this();
		setXY(x1);
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
	public void setXY(Real2 x1) {
		this.addAttribute(new Attribute("cx", ""+x1.getX()));
		this.addAttribute(new Attribute("cy", ""+x1.getY()));
	}

	/**
	 * @param x1 the x1 to set
	 */
	public Real2 getXY() {
		return new Real2(
			new Double(this.getAttributeValue("cx")).doubleValue(),
			new Double(this.getAttributeValue("cy")).doubleValue()
			);
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

//	public double getCx() {
//		return cx;
//	}
//
//	public void setCx(double cx) {
//		this.cx = cx;
//	}
//
//	public double getCy() {
//		return cy;
//	}
//
//	public void setCy(double cy) {
//		this.cy = cy;
//	}
//
//	public double getR() {
//		return r;
//	}
//
//	public void setR(double r) {
//		this.r = r;
//	}

	
}
