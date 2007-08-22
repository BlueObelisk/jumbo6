package org.xmlcml.cml.graphics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;

/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public abstract class SVGElement extends GraphicsElement {

	/** standard namespace for SVG
	 * 
	 */
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

	/** constructor.
	 * 
	 * @param name
	 */
	public SVGElement(String name) {
		super(name,SVG_NAMESPACE);
		styleMap = new HashMap<String, String>();
		styleString = "";
//		setStroke("black");
//		setFill("red");
//		setStrokeWidth(1.0);
//		setOpacity(1.0);
	}

	/**
	 * @return the sVG_NAMESPACE
	 */
	public static String getSVG_NAMESPACE() {
		return SVG_NAMESPACE;
	}

	protected abstract String getTag();
	
	protected void addStyle(String style, String value) {
		styleMap.put(style, value);
		if (styleString.indexOf(style) != -1) {
			addStyles();
		} else {
			styleString += style+":"+value+";";
		}
		this.addAttribute(new Attribute("style", styleString));
	}
	
	protected void addStyles() {
		styleString = "";
		for (String style : styleMap.keySet()) {
			styleString += style+":"+styleMap.get(style)+";";
		}
	}

	/**
	 * @return the fontFamily
	 */
	public String getFontFamily() {
		return this.getAttributeValue("font-family");
	}

	/**
	 * @param fontFamily the fontFamily to set
	 */
	public void setFontFamily(String fontFamily) {
		this.addAttribute(new Attribute("font-family", fontFamily));
	}

	/**
	 * @return the fontSize
	 */
	public double getFontSize() {
		return new Double(this.getAttributeValue("font-size")).doubleValue();
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(double fontSize) {
	//		this.addAttribute(new Attribute("font-size", ""+fontSize));
			this.addStyle("font-size", ""+fontSize);
		}

	/**
	 * @return the fontStyle
	 */
	public String getFontStyle() {
		return styleMap.get("font-style");
	}

	/**
	 * @param fontStyle the fontStyle to set
	 */
	public void setFontStyle(String fontStyle) {
		this.addAttribute(new Attribute("font-style", fontStyle));
	}

	/**
	 * @return the fontWeight
	 */
	public String getFontWeight() {
		return this.getAttributeValue("font-weight");
	}

	/**
	 * @param fontWeight the fontWeight to set
	 */
	public void setFontWeight(String fontWeight) {
		this.addAttribute(new Attribute("font-weight", fontWeight));
	}

	/**
	 */
	public void draw() {
//		FileOutputStream fos = new FileOutputStream(outfile);
//		SVGElement g = new MoleculeTool(molecule).
//		    createSVG();
//		int indent = 2;
//		SVGSVG svg = new SVGSVG();
//		svg.appendChild(g);
//		CMLUtil.debug(svg, fos, indent);
//		fos.close();
//		System.out.println("wrote SVG "+outfile);
	}
	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static void test(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		SVGSVG svg = new SVGSVG();
		SVGG g = new SVGG();
		g.setFill("yellow");
		svg.appendChild(g);
		SVGLine line = new SVGLine(new Real2(100, 200), new Real2(300, 50));
		line.setFill("red");
		line.setStrokeWidth(3);
		line.setStroke("blue");
		g.appendChild(line);
		SVGCircle circle = new SVGCircle(new Real2(300, 150), 20);
		circle.setStroke("red");
		circle.setFill("yellow");
		circle.setStrokeWidth(3.);
		g.appendChild(circle);
		SVGElement text = new SVGText(new Real2(50, 100), "Foo");
		text.setFontFamily("TimesRoman");
		text.setStroke("green");
		text.setFill("red");
		text.setStrokeWidth(1.5);
		text.setFontSize(20);
		text.setFontStyle("italic");
		text.setFontWeight("bold");
		g.appendChild(text);
		CMLUtil.debug(svg, fos, 2);
		fos.close();		
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			test(args[0]);
		}
	}
}
