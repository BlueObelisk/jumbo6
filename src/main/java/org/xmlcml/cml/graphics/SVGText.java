package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import nu.xom.Attribute;
import nu.xom.ParentNode;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/** draws text.
 * 
 * @author pm286
 *
 */
public class SVGText extends SVGElement {

	final static String TAG ="text";
	
	/** contructor
	 */
	public SVGText() {
		super(TAG);
//		this.setFontFamily("timesRoman");
//		this.setFontSize(0.1);
		this.setStroke("none");
//		this.setStrokeWidth(0.);
//		this.setFill("blue");
//		this.setFontStyle("normal");
//		this.setFontWeight("normal");
	}
	
	protected void drawElement(Graphics2D g2d) {
		double fontSize = this.getFontSize();
		fontSize *= cumulativeTransform.getMatrixAsArray()[0] * 0.3;
		fontSize = (fontSize < 8) ? 8 : fontSize;
		
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

	/** this has a special transform for inverting SVG which doesn't occur
	 * in Swing.
	 */
	protected void setCumulativeTransformRecursively(Object value) {
		if (cumulativeTransform == null && value != null) {
//			Transform2 thisTransform = this.getTransform2();
			ParentNode parentNode = this.getParent();
			Transform2 parentTransform = (parentNode instanceof GraphicsElement) ?
					((GraphicsElement) parentNode).getCumulativeTransform() : new Transform2();
			this.cumulativeTransform = parentTransform;
		}
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
		this.appendChild(text);
	}

}
