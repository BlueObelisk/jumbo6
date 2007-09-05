package org.xmlcml.cml.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
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
public class GraphicsManager implements CMLDrawable {
	private String outfile;
	private CMLMolecule molecule;
	private MoleculeDisplay moleculeDisplay;
	
	SVGElement g;
	SVGSVG svg;
	private MoleculeTool moleculeTool;
	
	/** constructor
	 */
	public GraphicsManager() {
	}
	
	/** constructor
	 * @param outfile
	 */
	public GraphicsManager(String outfile) {
		setOutfile(outfile);
	}
	
	/**
	 * @param outfile
	 */
	public void setOutfile(String outfile) {
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
		
		moleculeTool = MoleculeTool.getOrCreateMoleculeTool(molecule);
		SelectionTool selectionTool = moleculeTool.getOrCreateSelectionTool();
		
		List<CMLAtom> atoms = molecule.getAtoms();
//		selectionTool.setSelected(atoms.get(0), true);
		selectionTool.setSelected(atoms.get(atoms.size()-1), true);
//		selectionTool.setSelected(atoms.get(3), true); // test only
		List<CMLBond> bonds = molecule.getBonds();
//		selectionTool.setSelected(bonds.get(0), true);
		selectionTool.setSelected(bonds.get(bonds.size()-1), true);
//		selectionTool.setSelected(bonds.get(2), true); // test only
		
//		System.out.println("MOL "+moleculeTool.getSelectionTool().isSelected(atoms.get(atoms.size()-1)));
		
		if (moleculeDisplay != null) {
			g.detach();
		    try {
				g = moleculeTool.createSVG(moleculeDisplay, this);
				g.detach();
				svg.appendChild(g);
			} catch (IOException e) {
				System.err.println("Bad io: "+e);
			}
			svg.clearCumulativeTransformRecursively();
			svg.setCumulativeTransformRecursively();
		}
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
		this.moleculeDisplay = moleculeDisplay;
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