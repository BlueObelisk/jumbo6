package org.xmlcml.cml.graphics;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGLine extends SVGElement {

	final static String TAG ="line";

	private Line2D.Double line2;
	private Line2 euclidLine;
	
	/** constructor
	 */
	public SVGLine() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGLine(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGLine(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGLine(Real2 x1, Real2 x2) {
		this();
		setXY(x1, 0);
		setXY(x2, 1);
		euclidLine = new Line2(x1, x2);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGLine(Line2 line) {
		this(line.getXY(0), line.getXY(1));
		this.euclidLine = line;
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
        return new SVGLine(this);
    }

	/**
	 * @param xy coordinates of the atom
	 * @param serial 0 or 1
	 */
	public void setXY(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2/y2 in line: ");
		} else {
			this.addAttribute(new Attribute("x"+(serial+1), ""+x12.getX()));
			this.addAttribute(new Attribute("y"+(serial+1), ""+x12.getY()));
		}
	}
	
	public Real2 getXY(int serial) {
		Real2 xy = null;
		if (serial == 0) {
			xy = new Real2(this.getDouble("x1"), this.getDouble("y1"));
		} else if (serial == 1) {
			xy = new Real2(this.getDouble("x2"), this.getDouble("y2"));
		}
		return xy;
	}
	
	/**
	 * @param x12 coordinates of the atom
	 * @param serial 1 or 2
	 */
	@Deprecated
	public void setX12(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2/y2 in line: ");
		} else {
			this.addAttribute(new Attribute("x"+serial, ""+x12.getX()));
			this.addAttribute(new Attribute("y"+serial, ""+x12.getY()));
		}
	}
	
	@Deprecated //use getXY
	public Real2 getX12(int serial) {
		Real2 xy = null;
		if (serial == 1) {
			xy = new Real2(this.getDouble("x1"), this.getDouble("y1"));
		} else if (serial == 2) {
			xy = new Real2(this.getDouble("x2"), this.getDouble("y2"));
		}
		return xy;
	}
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		Line2D line = createAndSetLine2D();
		applyAttributes(g2d);
		g2d.draw(line);
	}

	public void applyAttributes(Graphics2D g2d) {
		int width = (int) this.getStrokeWidth();
		Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g2d.setStroke(s);
		super.applyAttributes(g2d);
	}

	public Line2D.Double createAndSetLine2D() {
		double x1 = this.getDouble("x1");
		double y1 = this.getDouble("y1");
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		double x2 = this.getDouble("x2");
		double y2 = this.getDouble("y2");
		Real2 xy2 = new Real2(x2, y2);
		xy2 = transform(xy2, cumulativeTransform);
		float width = 5.0f;
		String style = this.getAttributeValue("style");
		if (style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) new Double(style).doubleValue();
			width *= 15.f;
		}
		line2 = new Line2D.Double(xy1.x, xy1.y, xy2.x, xy2.y);
		return line2;
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public Line2D.Double getLine2() {
		return line2;
	}

	public void setLine2(Line2D.Double line2) {
		this.line2 = line2;
	}
	
	public Line2 getEuclidLine() {
		return euclidLine;
	}

	public void setEuclidLine(Line2 euclidLine) {
		this.euclidLine = euclidLine;
	}

}
