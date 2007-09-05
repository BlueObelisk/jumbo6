package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;

import nu.xom.Attribute;

import org.xmlcml.cml.tools.AbstractDisplay;
import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/** grouping element
 * 
 * @author pm286
 *
 */
public class SVGG extends G {

	/** constructor.
	 */
	public SVGG() {
		super();
	}
	/**
	 * @return tag
	 */
	
	public String getTag() {
		return TAG;
	}
	
	/** overrides normal append so as to include transformation
	 * @param element
	 */
	public void appendChild(SVGElement element) {
		super.appendChild(element);
		element.setCumulativeTransformRecursively();
	}

	/** draws children recursively
	 * 
	 * @param g2d
	 */
	protected void drawElement(Graphics2D g2d) {
//		System.out.println("G ID: "+this.getAttributeValue("id"));
		super.drawElement(g2d);
	}
	
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
	
	private void processTransform() {
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
	 * @param size
	 */
	public void setFontSize(double size) {
		this.addAttribute(new Attribute("font-size", ""+size));
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

	private void ensureTransform() {
		if (transform == null) {
			transform = new Transform2();
		}
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

}
