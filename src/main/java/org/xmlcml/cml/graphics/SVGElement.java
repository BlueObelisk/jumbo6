package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.tools.AbstractDisplay;
import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;

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
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		drawElement(g2d);
	}
	
	/** draws children recursively
	 * 
	 * @param g2d
	 */
	protected void drawElement(Graphics2D g2d) {
		Elements gList = this.getChildElements();
		for (int i = 0; i < gList.size(); i++) {
			SVGElement svge = (SVGElement) gList.get(i);
			svge.drawElement(g2d);
		}
	}
	
	Transform2 transform = new Transform2();

	/**
	 * @return the transform
	 */
	public Transform2 getTransform() {
		return transform;
	}
	/**
	 * @param transform the transform to set
	 */
	public void setTransform(Transform2 transform) {
		this.transform = transform;
		processTransform();
	}
	
	protected void processTransform() {
		double[] matrix = transform.getMatrixAsArray();
		this.addAttribute(new Attribute("transform", "matrix(" +
			matrix[0] +"," +
			"0., 0.," +
			matrix[4] +"," +
			matrix[2]+","+matrix[5]+
			")"));
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setScale(double s) {
		ensureTransform();
		Transform2 t = new Transform2(
				new double[]{
				s, 0., 0.,
				0., s, 0.,
				0., 0., 1.
				});
		transform = transform.concatenate(t);
		processTransform();
	}

	protected void ensureTransform() {
		if (transform == null) {
			transform = new Transform2();
		}
	}

	/** set moleculeDisplay properties.
	 * 
	 * @param moleculeDisplay
	 */
	public void setProperties(MoleculeDisplay moleculeDisplay) {
		this.setFontStyle(moleculeDisplay.getFontStyle());
		this.setFontWeight(moleculeDisplay.getFontStyle());
		this.setFontFamily(moleculeDisplay.getFontFamily());
		this.setFontSize(moleculeDisplay.getFontSize());
		this.setFill(moleculeDisplay.getFill());
		this.setStroke(moleculeDisplay.getStroke());
		this.setOpacity(moleculeDisplay.getOpacity());
		
		this.setProperties(moleculeDisplay.getAtomDisplay());
		this.setProperties(moleculeDisplay.getBondDisplay());
	}
	
	/** set properties.
	 * 
	 * @param abstractDisplay
	 */
	public void setProperties(AbstractDisplay abstractDisplay) {
		this.setFontStyle(abstractDisplay.getFontStyle());
		this.setFontWeight(abstractDisplay.getFontStyle());
		this.setFontFamily(abstractDisplay.getFontFamily());
		this.setFontSize(abstractDisplay.getFontSize());
		this.setFill(abstractDisplay.getFill());
		this.setStroke(abstractDisplay.getStroke());
		this.setOpacity(abstractDisplay.getOpacity());
		
	}
	
	/**
	 */
	public void setCumulativeTransformRecursively() {
		setCumulativeTransformRecursively("set");
	}

	/**
	 */
	public void clearCumulativeTransformRecursively() {
		setCumulativeTransformRecursively(null);
	}
	
	/**
	 * @param value if null clear the transform else concantenate
	 * may be overridden by children such as Text
	 */
	protected void setCumulativeTransformRecursively(Object value) {
		if (value != null) {
			Transform2 thisTransform = this.getTransform2FromAttribute();
			ParentNode parentNode = this.getParent();
			Transform2 parentTransform = (parentNode instanceof GraphicsElement) ?
					((GraphicsElement) parentNode).getCumulativeTransform() : new Transform2();
			this.cumulativeTransform = (thisTransform == null) ? parentTransform : parentTransform.concatenate(thisTransform);
			for (int i = 0; i < this.getChildElements().size(); i++) {
				Node child = this.getChild(i);
				if (child instanceof SVGElement) {
					((SVGElement) child).setCumulativeTransformRecursively(value);
				}
			}
		}
	}
	
	static Map<String, Color> colorMap;
	static {
		colorMap = new HashMap<String, Color>();
		colorMap.put("black", new Color(0, 0, 0));
		colorMap.put("white", new Color(255, 255, 255));
		colorMap.put("red", new Color(255, 0, 0));
		colorMap.put("green", new Color(0, 255, 0));
		colorMap.put("blue", new Color(0, 0, 255));
		colorMap.put("yellow", new Color(255, 255, 0));
		colorMap.put("orange", new Color(255, 127, 0));
		colorMap.put("#ff00ff", new Color(255, 0, 255));
	}

	/**
	 * 
	 * @param attName
	 * @return color
	 */
	public Color getColor(String attName) {
		Color color = null;
		String attVal = this.getAttributeValue(attName);
		if ("none".equals(attVal)) {
		} else if (attVal != null) {
			color = colorMap.get(attVal);
			if (color == null) {
				System.err.println("Unknown color: "+attVal);
			}
		}
		if (color != null) {
			double opacity = this.getOpacity();
			color = (Double.isNaN(opacity)) ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255.0 * opacity));
		} else {
			color = new Color(255, 255, 255, 0);
		}
		return color;
	}
	
	/**
	 * transforms xy to fit on page
	 * messy
	 * @param xy is transformed
	 * @param transform
	 * @return transformed xy
	 */
	public static Real2 transform(Real2 xy, Transform2 transform) {
		xy.transformBy(transform);
		xy = xy.plus(new Real2(250, 250));
		return xy;
	}

	protected double getDouble(String attName) {
		String attVal = this.getAttributeValue(attName);
		double xx = Double.NaN;
		if (attVal != null) {
			try {
				xx = new Double(attVal).doubleValue();
			} catch (NumberFormatException e) {
				throw e;
			}
		}
		return xx;
	}

	/**
	 * uses attribute value to calculate transform
	 * @return current transform
	 */
	public Transform2 getTransform2FromAttribute() {
		Transform2 t = null;
		String ts = this.getAttributeValue("transform");
		if (ts != null) {
			if (!ts.startsWith("matrix(")) {
				throw new CMLRuntimeException("Bad transform: "+ts);
			}
			ts = ts.substring("matrix(".length());
			ts = ts.substring(0, ts.length()-1);
			ts = ts.replace(S_COMMA, S_SPACE);
			RealArray realArray = new RealArray(ts);
			double[] dd = new double[9];
			dd[0] = realArray.elementAt(0);
			dd[1] = realArray.elementAt(1);
			dd[2] = realArray.elementAt(4);
			dd[3] = realArray.elementAt(2);
			dd[4] = realArray.elementAt(3);
			dd[5] = realArray.elementAt(5);
			dd[6] = 0.0;
			dd[7] = 0.0;
			dd[8] = 1.0;
			t = new Transform2(dd);
		}
		return t;
	}
	
	/**
	 * sets attribute value from transform
	 * @param transform
	 */
	public void setAttributeFromTransform2(Transform2 transform) {
		if (transform != null) {
			double[] dd = transform.getMatrixAsArray();
			String ts = "matrix"+
			S_LBRAK+
			dd[0]+S_COMMA+
			dd[1]+S_COMMA+
			dd[3]+S_COMMA+
			dd[4]+S_COMMA+
			dd[2]+S_COMMA+
			dd[5]+
			S_RBRAK;
			this.addAttribute(new Attribute("transform", ts));
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
		double fontSize = 40.0;
		if (this.getAttribute("font-size") != null) {
			String fontSizeS = this.getAttributeValue("font-size");
			try {
				fontSize = new Double(fontSizeS).doubleValue();
			} catch (NumberFormatException nfe) {
				System.err.println(nfe);
				// bad font
			}
		}
		return fontSize;
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(double fontSize) {
			this.addAttribute(new Attribute("font-size", ""+fontSize));
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
//		SVGElement g = MoleculeTool.getOrCreateTool(molecule).
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
	 * @param xy
	 */
	public void translate(Real2 xy) {
		ensureTransform();
		Transform2 t = new Transform2(
			new double[] {
			1., 0., xy.getX(),
			0., 1., xy.getY(),
			0., 0., 1.
		});
		transform = transform.concatenate(t);
		processTransform();
	}

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static void test(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		SVGSVG svg = new SVGSVG();
		SVGElement g = new SVGG();
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
