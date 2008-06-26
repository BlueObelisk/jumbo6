package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.xmlcml.euclid.Real2;

/** draws text.
 * 
 * @author pm286
 *
 */
public class SVGText extends SVGElement {

	final static String TAG ="text";
	
	/** constructor
	 */
	public SVGText() {
		super(TAG);
		init();
	}
	protected void init() {
		super.setDefaultStyle();
		setDefaultStyle(this);
	}

	public static void setDefaultStyle(SVGText text) {
		text.setStroke("none");
		text.setFontSize(7.654321);
	}
	
	/** constructor
	 */
	public SVGText(SVGText element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGText(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGText(this);
    }

    public double getX() {
    	String s = this.getAttributeValue("x");
    	return (s != null) ? new Double(s).doubleValue()  : Double.NaN;
    }

    public double getY() {
    	String s = this.getAttributeValue("y");
    	return (s != null) ? new Double(s).doubleValue() : Double.NaN;
    }
    
	protected void drawElement(Graphics2D g2d) {
		double fontSize = this.getFontSize();
		fontSize *= cumulativeTransform.getMatrixAsArray()[0] * 0.3;
		fontSize = (fontSize < 8) ? 8 : fontSize;
//		System.out.println("FONTSIZE "+fontSize);
		
		double x = this.getDouble("x");
		double y = this.getDouble("y");
		String text = this.getValue();
		Real2 xy = new Real2(x, y);
		xy = transform(xy, cumulativeTransform);
		xy.plusEquals(new Real2(fontSize*0.65, -0.65*fontSize));
		Color color = this.getColor("fill");
		color = (color == null) ? Color.DARK_GRAY : color;
		g2d.setColor(color);
		g2d.setFont(new Font("SansSerif", Font.PLAIN, (int)fontSize));
		g2d.drawString(text, (int)xy.x, (int)xy.y);
	}
	
	
	/** constructor.
	 * 
	 * @param x1
	 * @param text
	 */
	public SVGText(Real2 x1, String text) {
		this();
		setX1(x1);
		setText(text);
	}

	/**
	 * @return the x1
	 */
	public Real2 getX1() {
		return new Real2(
			new Double(getAttributeValue("x")).doubleValue(),
			new Double(getAttributeValue("y")).doubleValue()
		);
	}
	/**
9	 * @param x1 the x1 to set
	 * @param x1 
	 */
	public void setX1(Real2 x1) {
		this.addAttribute(new Attribute("x", ""+x1.getX()));
		this.addAttribute(new Attribute("y", ""+x1.getY()));
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return this.getValue();
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		if (this.getChildCount() > 0) {
			Node node = this.getChild(0);
			if (node instanceof Text) {
				node.detach();
			} else {
				System.out.println(node.getClass());
			}
		}
		this.appendChild(text);
	}

}
