package org.xmlcml.cml.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JPanel;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/**
 * @author pm286
 *
 */
public class MoleculePanel extends JPanel implements CMLDrawable, CMLConstants {
	
	/**
	 */
	private static final long serialVersionUID = -8163558817500143947L;

//	private CMLMolecule molecule;
	MoleculeFrame moleculeFrame;
	private SVGElement g;
	private SVGElement svg;
	private GraphicsManager svgObject;

	private Transform2 svgTransform; 
	
	/**
	 */	
	public MoleculePanel() {
		this.addMouseMotionListener(new MoleculeMouseMotionListener());
		this.addMouseListener(new MoleculeMouseListener());
//		this.addKeyListener(new MoleculeKeyListener());
	}
	
	class MoleculeMouseMotionListener implements MouseMotionListener {

		/**
		 * @param arg0
		 */
		public void mouseDragged(MouseEvent arg0) {
			System.out.println("DRAGPanel "+arg0);
		}

		/**
		 * @param arg0
		 */
		public void mouseMoved(MouseEvent arg0) {
//			System.out.println("MOVE "+arg0);
		}
	}
	
	class MoleculeMouseListener implements MouseListener {

		/**
		 * @param e
		 */
		public void mouseClicked(MouseEvent e) {
			System.out.println("CLICK "+e);
		}

		/**
		 * @param e
		 */
		public void mouseEntered(MouseEvent e) {
//			System.out.println("ENTER "+e);
		}

		/**
		 * @param e
		 */
		public void mouseExited(MouseEvent e) {
//			System.out.println("EXIT "+e);
		}

		/**
		 * @param e
		 */
		public void mousePressed(MouseEvent e) {
			System.out.println("PRESS "+e);
		}

		/**
		 * @param e
		 */
		public void mouseReleased(MouseEvent e) {
			System.out.println("RELEASE "+e);
		}
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
	
	/**
	 * @param ggg
	 */
	public void paintComponent(Graphics ggg) {
// Clear off-screen bitmap
		setBackground(Color.WHITE);
		super.paintComponent(ggg);
  // Cast Graphics to Graphics2D
		Graphics2D g2d = (Graphics2D)ggg;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.drawMolecule(g2d);
		this.calculateAndDrawProperties(g2d);
	}
	
	void shift(int x, int y) {
		Real2 xy = new Real2((double) x, (double) y);
		Transform2 move = new Transform2(new double[] {
				1.0, 0.0, (double) x,
				0.0, 1.0, (double) y,
				0.0, 0.0, 1.0,
		});
		svgTransform = svg.getTransform2FromAttribute();
		if (svgTransform == null) {
			svgTransform = new Transform2();
		}
		svgTransform = svgTransform.concatenate(move);
		svg.setAttributeFromTransform2(svgTransform);
		svg.setCumulativeTransform(null);
		svg.setCumulativeTransformRecursively();
		this.repaint();
	}

	/** draws molecule
	 * @param g2d 
	 */
	public void drawMolecule(Graphics2D g2d) {
//		System.out.println("DRAW MOL");
		if (svgObject != null) {
			svg = svgObject.svg;
			svg.clearCumulativeTransformRecursively();
			svg.setAttributeFromTransform2(svgTransform);
			svg.setCumulativeTransformRecursively();
//			CMLUtil.debug(svg);
			svg.draw(g2d);
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
	
	/**
	 * 
	 * @param g2d
	 */
	public void calculateAndDrawProperties(Graphics2D g2d) {
		CMLMolecule molecule = moleculeFrame.getMolecule();
		if (molecule != null) {
			CMLFormula formula = molecule.getCalculatedFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
			String f = formula.getConcise();
			Font oldFont = g2d.getFont();
			g2d.setFont(new Font("helvetica", Font.BOLD, 12));
			g2d.drawString(f, 10, 10);
			double d = formula.getCalculatedMolecularMass();
			g2d.drawString(("MWt: "+d).substring(0, 10), 300, 10);
			g2d.setFont(oldFont);
		} else {
			System.out.println("Null molecule");
		}
	}
	
	/**
	 * @param svgObject
	 */
	public void setSVGObject(GraphicsManager svgObject) {
		this.svgObject = svgObject;
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

	/**
	 * @return the moleculeFrame
	 */
	public MoleculeFrame getMoleculeFrame() {
		return moleculeFrame;
	}

	/**
	 * @param moleculeFrame the moleculeFrame to set
	 */
	public void setMoleculeFrame(MoleculeFrame moleculeFrame) {
		this.moleculeFrame = moleculeFrame;
	}

}
