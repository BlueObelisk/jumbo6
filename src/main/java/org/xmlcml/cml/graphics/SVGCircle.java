package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
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
public class SVGCircle extends SVGElement {

	final static String TAG ="circle";
	private Ellipse2D.Double circle2;

	public Ellipse2D.Double getCircle2() {
		return circle2;
	}

	public void setCircle2(Ellipse2D.Double circle2) {
		this.circle2 = circle2;
	}

	/** constructor
	 */
	public SVGCircle() {
		super(TAG);
		init();
	}
	
	protected void init() {
		super.setDefaultStyle();
		setDefaultStyle(this);
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
	 * 
	 * @param circle
	 */
	public static void setDefaultStyle(SVGCircle circle) {
		circle.setStroke("black");
		circle.setStrokeWidth(0.5);
		circle.setFill("#aaffff");
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
		circle2 = new Ellipse2D.Double(x1.getX(), x1.getY(), rad, rad);
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

	public Ellipse2D.Double createAndSetCircle2D() {
		double rad = this.getDouble("r");
		double x1 = this.getDouble("cx");
		double y1 = this.getDouble("cx");
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		float width = 5.0f;
		String style = this.getAttributeValue("style");
		if (style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) new Double(style).doubleValue();
			width *= 15.f;
		}
		circle2 = new Ellipse2D.Double(xy1.x - rad, xy1.y - rad, rad+rad, rad+rad);
		return circle2;
	}
	
}
