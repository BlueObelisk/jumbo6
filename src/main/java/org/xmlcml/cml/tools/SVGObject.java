package org.xmlcml.cml.tools;

import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;


/**
 * @author pm286
 *
 */
public class SVGObject implements CMLDrawable {
	private String outfile;
	private CMLMolecule molecule;
	SVGElement g;
	SVGSVG svg;
	
	/** constructor
	 */
	public SVGObject() {
	}
	
	/** constructor
	 * @param outfile
	 */
	public SVGObject(String outfile) {
		setOutfile(outfile);
	}
	
	/**
	 * @param outfile
	 */
	public void setOutfile(String outfile) {
		System.out.println("OUT "+outfile);
		this.outfile = outfile;
	}

	/**
	 * @return outfile
	 */
	public String getOutfile() {
		return outfile;
	}
	
	void setMolecule(CMLMolecule molecule) {
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
	
	/**
	 * @param g graphics element (?is this the best way??)
	 * @throws IOException
	 */
	public void output(GraphicsElement g) throws IOException {
		svg = new SVGSVG();
		svg.appendChild(g);
		if (outfile != null) {
			write();
		}
	}
	
	/** write to svgFile
	 * @throws IOException
	 */
	public void write() throws IOException {
		if (outfile != null) {
			FileOutputStream fos = new FileOutputStream(outfile);
			int indent = 2;
			if (svg == null) {
				System.err.println("Null SVG");
			} else {
				CMLUtil.debug(svg, fos, indent);
				fos.close();
				System.out.println("wrote SVG "+outfile);
			}
		}
	}
}