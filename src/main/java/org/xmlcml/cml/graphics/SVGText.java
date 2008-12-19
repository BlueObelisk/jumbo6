package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;

/** draws text.
 * 
 * @author pm286
 *
 */
public class SVGText extends SVGElement {
	private static Logger LOG = Logger.getLogger(SVGText.class);
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
		
		String text = this.getValue();
//		double x = this.getDouble("x");
//		double y = this.getDouble("y");
//		Real2 xy = new Real2(x, y);
		Real2 xy = this.getXY();
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
	 * @param xy
	 * @param text
	 */
	public SVGText(Real2 xy, String text) {
		this();
		setXY(xy);
		setText(text);
	}

	/**
	 * @return the coordinates
	 */
	public Real2 getXY() {
		return new Real2(
			new Double(getAttributeValue("x")).doubleValue(),
			new Double(getAttributeValue("y")).doubleValue()
		);
	}
	/**
	 * @param xy the coordinates
	 */
	public void setXY(Real2 x1) {
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
				LOG.debug(node.getClass());
			}
		}
		this.appendChild(text);
	}

}
