package org.xmlcml.cml.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

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
public class SVGElement extends GraphicsElement {

	private Element userElement;
	private String strokeSave;
	private String fillSave;
	
	/** constructor.
	 * 
	 * @param name
	 */
	public SVGElement(String name) {
		super(name,SVG_NAMESPACE);
	}

	public SVGElement(SVGElement element) {
        super((GraphicsElement) element);
        this.userElement = element.userElement;
	}
	
	/** copy constructor from non-subclassed elements
	 */
	public static SVGElement createSVG(Element element) {
		SVGElement newElement = null;
		String tag = element.getLocalName();
		if (tag == null || tag.equals(S_EMPTY)) {
			throw new RuntimeException("no tag");
		} else if (tag.equals(SVGCircle.TAG)) {
			newElement = new SVGCircle();
		} else if (tag.equals(SVGDesc.TAG)) {
			newElement = new SVGDesc();
		} else if (tag.equals(SVGEllipse.TAG)) {
			newElement = new SVGEllipse();
		} else if (tag.equals(SVGG.TAG)) {
			newElement = new SVGG();
		} else if (tag.equals(SVGLine.TAG)) {
			newElement = new SVGLine();
		} else if (tag.equals(SVGPath.TAG)) {
			newElement = new SVGPath();
		} else if (tag.equals(SVGRect.TAG)) {
			newElement = new SVGRect();
		} else if (tag.equals(SVGSVG.TAG)) {
			newElement = new SVGSVG();
		} else if (tag.equals(SVGText.TAG)) {
			newElement = new SVGText();
		} else {
			throw new RuntimeException("unknown element "+tag);
		}
        newElement.copyAttributesFrom(element);
        createSubclassedChildren(element, newElement);
        return newElement;
		
	}
	
	private static void createSubclassedChildren(Element oldElement, SVGElement newElement) {
		for (int i = 0; i < oldElement.getChildCount(); i++) {
			Node node = oldElement.getChild(i);
			Node newNode = null;
			if (node instanceof Text) {
				newNode = new Text(node.getValue());
			} else {
				newNode = createSVG((Element) node);
			}
			newElement.appendChild(newNode);
		}
	}
	
	/**
	 * @return the sVG_NAMESPACE
	 */
	public static String getSVG_NAMESPACE() {
		return SVG_NAMESPACE;
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
		this.setFontWeight(moleculeDisplay.getFontWeight());
		this.setFontFamily(moleculeDisplay.getFontFamily());
		this.setFontSize(moleculeDisplay.getFontSize());
		this.setFill(moleculeDisplay.getFill());
		this.setStroke(moleculeDisplay.getStroke());
		this.setOpacity(moleculeDisplay.getOpacity());
		
		this.setProperties(moleculeDisplay.getDefaultAtomDisplay());
		this.setProperties(moleculeDisplay.getDefaultBondDisplay());
	}
	
	/** set properties.
	 * 
	 * @param abstractDisplay
	 */
	public void setProperties(AbstractDisplay abstractDisplay) {
		this.setFontStyle(abstractDisplay.getFontStyle());
		this.setFontWeight(abstractDisplay.getFontWeight());
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
		String attVal = this.getAttributeValue(attName);
		return getJava2DColor(attVal, this.getOpacity());
	}

	/**
	 * translate SVG string to Java2D
	 * opacity defaults to 1.0
	 * @param color
	 * @param colorS
	 * @return
	 */
	public static Color getJava2DColor(String colorS) {
		return getJava2DColor(colorS, 1.0);
	}

	/**
	 * 
	 * @param colorS common colors ("yellow"), etc or hexString
	 * @param opacity 0.0 to 1.0
	 * @return java Color or null
	 */
	public static Color getJava2DColor(String colorS, double opacity) {
		Color color = null;
		if ("none".equals(colorS)) {
		} else if (colorS != null) {
			color = colorMap.get(colorS);
			if (color == null) {
				if (colorS.length() == 7 && colorS.startsWith(S_HASH)) {
					try {
						int red = Integer.parseInt(colorS.substring(1, 3), 16);
						int green = Integer.parseInt(colorS.substring(3, 5), 16);
						int blue = Integer.parseInt(colorS.substring(5, 7), 16);
						color = new Color(red, green, blue, 0);
					} catch (Exception e) {
						throw new RuntimeException("Cannot parse: "+colorS);
					}
					colorS = colorS.substring(1);
				} else {
//					System.err.println("Unknown color: "+colorS);
				}
			}
		}
		if (color != null) {
			color = (Double.isNaN(opacity)) ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255.0 * opacity));
		} else {
			color = new Color(255, 255, 255, 0);
		}
		return color;
	}
	
	/**
	 * transforms xy
	 * messy
	 * @param xy is transformed
	 * @param transform
	 * @return transformed xy
	 */
	public static Real2 transform(Real2 xy, Transform2 transform) {
		xy.transformBy(transform);
//		xy = xy.plus(new Real2(250, 250));
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
				throw new RuntimeException("Bad transform: "+ts);
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

	public void addDashedStyle(double bondWidth) {
		String style = this.getAttributeValue("style");
		style += "stroke-dasharray : "+bondWidth*2+" "+bondWidth*2+";";
		this.addAttribute(new Attribute("style", style));
	}
	
	public void toggleFill(String fill) {
		this.fillSave = this.getFill();
		this.setFill(fill);
	}
    
	public void toggleFill() {
		this.setFill(fillSave);
	}
	
	public void toggleStroke(String stroke) {
		this.strokeSave = this.getStroke();
		this.setStroke(stroke);
	}
    
	public void toggleStroke() {
		this.setStroke(strokeSave);
	}
    
	public void applyAttributes(Graphics2D g2d) {
		applyStrokeColor(g2d);
//		applyFillColor(g2d);
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
		SVGElement line = new SVGLine(new Real2(100, 200), new Real2(300, 50));
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

	public Element getUserElement() {
		return userElement;
	}

	public void setUserElement(Element userElement) {
		this.userElement = userElement;
	}

	protected void applyStrokeColor(Graphics2D g2d) {
		String colorS = "black";
		String stroke = this.getStroke();
		if (stroke != null) {
			colorS = stroke;
		}
		Color color = colorMap.get(colorS);
		if (color != null && g2d != null) {
			g2d.setColor(color);
		}
	}
	
	protected void applyFillColor(Graphics2D g2d) {
		String colorS = "black";
		String fill = this.getFill();
		if (fill != null) {
			colorS = fill;
		}
		Color color = colorMap.get(colorS);
		if (color != null && g2d != null) {
			g2d.setColor(color);
		}
	}
}
