package org.xmlcml.cml.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;

/**
 * @author pm286
 *
 */
public class MoleculePanel extends JPanel implements CMLDrawable, CMLConstants {
	
	/**
	 */
	private static final long serialVersionUID = -8163558817500143947L;

	private CMLMolecule molecule;
	private SVGElement g;
	private SVGObject svgObject; 
	
	/**
	 */	
	public MoleculePanel() {
		JFrame jFrame = new JFrame();
		jFrame.getContentPane().add(this);
		jFrame.setSize(new Dimension(500, 500));
		jFrame.setVisible(true);
	}

//	<?xml version="1.0" encoding="UTF-8"?>
//	<svg xmlns="http://www.w3.org/2000/svg">
//	  <g transform="matrix(16.146254327058795,0., 0.,-16.146254327058795,72.98344340225616,72.98344340225616)"
//	        font-style="normal" font-weight="normal" font-family="helvetica" 
//		    font-size="1.0" fill="black" opacity="1.0">
//	    <g style="stroke-width:0.2;">
//	      <line x1="4.817664105611036" y1="-0.2465136668648764" x2="7.134575966969313" y2="1.3758054838256792"
//          stroke="black" style="stroke-width:0.12;"/>
//	    </g>
//	    <g transform="matrix(1.0,0., 0.,-1.0,4.817664105611036,-0.2465136668648764)" fill="black">
//	      <circle cx="0.0" cy="0.0" r="0.4" fill="white"/>
//	      <text stroke="none" x="-0.34" y="0.35">U</text>
//	    </g>
//	  </g>
//	</svg>
	
//	/**
//	 * @param ggg
//	 */
//	public void paint(Graphics ggg) {
//		if (svgObject != null) {
//			Element g = svgObject.svg.getChildElements().get(0);
//			Transform2 transform0 = getTransform2(g);
//			Elements gList = g.getChildElements();
//			for (int i = 0; i < gList.size(); i++) {
//				draw(transform0, gList.get(i), ggg);
//			}
//		}
//	}

	/**
	 * @param ggg
	 */
	public void paintComponent(Graphics ggg) {
//		System.out.println("Graphics");
// Clear off-screen bitmap
		super.paintComponent(ggg);
  // Cast Graphics to Graphics2D
		Graphics2D g2d = (Graphics2D)ggg;
		if (svgObject != null) {
			Element g = svgObject.svg.getChildElements().get(0);
			Transform2 transform0 = getTransform2(g);
			Elements gList = g.getChildElements();
			for (int i = 0; i < gList.size(); i++) {
				g2d.fill(new Line2D.Double((double)10.*i, (double)15.*i, (double)20.*i, (double)25.*i));
				draw(transform0, gList.get(i), g2d);
			}
		}
		
	  // Set pen parameters
//	  g2d.setPaint(fillColorOrPattern);
//	  g2d.setStroke(penThicknessOrPattern);
//	  g2d.setComposite(someAlphaComposite);
//	  g2d.setFont(anyFont);
//	  g2d.translate(...);
//	  g2d.rotate(...);
//	  g2d.scale(...);
//	  g2d.shear(...);
//	  g2d.setTransform(someAffineTransform);
	  // Allocate a shape 
//	  SomeShape s = new SomeShape(...);
	  // Draw shape
//		Line2D line = new Line2D.Double(40., 150., 280., 390.);
		Stroke s = new BasicStroke(1.5f);
		g2d.setStroke(s);
//	  g2d.fill(s);  // solid
	}
	
	private void draw(Transform2 transform0, Element g, Graphics2D g2d) {
		Transform2 transform1 = getTransform2(g);
		Transform2 transform = (transform1 == null) ? transform0 : transform0.concatenate(transform1);
		Elements elements = g.getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			Element prim = elements.get(i);
			String tag = prim.getLocalName();
			if (tag.equals("line")) {
				drawLine(transform, prim, g2d);
			} else if (tag.equals("circle")) {
				drawCircle(transform, prim, g2d);
			} else if (tag.equals("text")) {
				drawText(transform, prim, g2d, g);
			}
		}
	}

//    <g style="stroke-width:0.2;">
//    <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//    <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//  </g>
	private void drawLine(Transform2 transform, Element prim, Graphics2D g2d) {
		double x1 = getDouble(prim, "x1");
		double y1 = getDouble(prim, "y1");
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, transform);
		double x2 = getDouble(prim, "x2");
		double y2 = getDouble(prim, "y2");
		Real2 xy2 = new Real2(x2, y2);
		xy2 = transform(xy2, transform);
		float width = 1.0f;
		String style = prim.getAttributeValue("style");
		if (style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) new Double(style).doubleValue();
			width *= 30.f;
		}
		
		Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		g2d.setStroke(s);
		
		String colorS = "black";
		String stroke = prim.getAttributeValue("stroke");
		if (stroke != null) {
			colorS = stroke;
		}
		Color color = colorMap.get(colorS);
		g2d.setColor(color);
		Line2D line = new Line2D.Double(xy1.x, xy1.y, xy2.x, xy2.y);
		g2d.draw(line);
	}
	
	private void drawCircle(Transform2 transform, Element prim, Graphics2D g2d) {
		double x = getDouble(prim, "cx");
		double y = getDouble(prim, "cy");
		double r = getDouble(prim, "r");
		Real2 xy0 = new Real2(x, y);
		xy0 = transform(xy0, transform);
		Real2 xy1 = new Real2(r, 0);
		xy1 = transform(xy1, transform);
		Ellipse2D ellipse = new Ellipse2D.Double(xy0.x, xy0.y, r, r);
		g2d.fill(ellipse);
	}
	
	private void drawText(Transform2 transform, Element prim, Graphics2D g2d, Element g) {
		double x = getDouble(prim, "x");
		double y = getDouble(prim, "y");
		String text = prim.getValue();
		Real2 xy = new Real2(x, y);
		xy = transform(xy, transform);
		Color color = getColor(g, "fill");
		if (color != null) {
			g2d.setColor(color);
//			ggg.setFont(new Font("helvetica", Font.BOLD, 0));
		}
		g2d.drawString(text, (int)xy.x, (int)xy.y);
	}
	
	private static Map<String, Color> colorMap;
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
	private Color getColor(Element element, String attName) {
		Color color = null;
		String attVal = element.getAttributeValue(attName);
		if (attVal != null) {
			color = colorMap.get(attVal);
			if (color == null) {
				System.err.println("Unknown color: "+attVal);
			}
		}
		return color;
	}
	
	private Real2 transform(Real2 xy, Transform2 transform) {
		xy.transformBy(transform);
		xy = xy.plus(new Real2(250, 250));
		return xy;
	}

	private double getDouble(Element prim, String attName) {
		String attVal = prim.getAttributeValue(attName);
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

	private Transform2 getTransform2(Element element) {
		Transform2 t = null;
		if (element != null) {
			String ts = element.getAttributeValue("transform");
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
		}
		return t;
	}
	
	/**
	 * @param svgObject
	 */
	public void setSVGObject(SVGObject svgObject) {
		this.svgObject = svgObject;
	}
	
	/**
	 * @param molecule
	 */
	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	/**
	 * @return element
	 */
	public SVGG createGraphicsElement() {
		return new SVGG();
	}
	

	/**
	 * @param moleculeTool
	 * @param moleculeDisplay
	 * @throws IOException
	 */
	public void createOrDisplayGraphics(
		MoleculeTool moleculeTool, MoleculeDisplay moleculeDisplay) 
	    throws IOException {
	    g = moleculeTool.createSVG(moleculeDisplay, this);
	}

	/** dummy
	 * @param g
	 * @throws IOException
	 */
	public  void output(GraphicsElement g) throws IOException {
		
	}
}
