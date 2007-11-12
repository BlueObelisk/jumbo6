package org.xmlcml.cml.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;


/**
 * @author pm286
 *
 */
public class MoleculeDisplayList implements CMLDrawable {
	
	private String outfile;
//	private MoleculeTool moleculeTool;
//	private ReactionTool reactionTool;
	private AbstractTool abstractTool;
	private MoleculeDisplay moleculeDisplay;
	private SVGElement g;
	private SVGSVG svg;
	
	/** constructor
	 */
	public MoleculeDisplayList() {
	}
	
	/** constructor
	 * @param outfile
	 */
	public MoleculeDisplayList(String outfile) {
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
	
	void setAndProcess(MoleculeTool moleculeTool) {
		if (moleculeTool != null) {
			this.abstractTool = moleculeTool;
			SelectionTool selectionTool = moleculeTool.getOrCreateSelectionTool();
			CMLMolecule molecule = moleculeTool.getMolecule();
			
			List<CMLAtom> atoms = molecule.getAtoms();
			if (atoms.size() > 0) {
				selectionTool.setSelected(atoms.get(atoms.size()-1), true);
				List<CMLBond> bonds = molecule.getBonds();
				if (bonds.size() > 0) {
					selectionTool.setSelected(bonds.get(bonds.size()-1), true);
				}
			}
			if (moleculeDisplay != null) {
				g.detach();
			    try {
			    	System.out.println("REDRAW... MOL");
					g = moleculeTool.createGraphicsElement(this);
					g.detach();
					svg.appendChild(g);
//					CMLUtil.debug(svg);
				} catch (IOException e) {
					System.err.println("Bad io: "+e);
				}
				svg.clearCumulativeTransformRecursively();
				svg.setCumulativeTransformRecursively();
			}
		}
	}

	void setAndProcess(ReactionTool reactionTool) {
		if (reactionTool != null) {
			this.abstractTool = reactionTool;
			CMLReaction reaction = reactionTool.getReaction();
			List<CMLMolecule> molecules = reaction.getMolecules(Component.ANY);
			for (CMLMolecule molecule : molecules) {
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
				setAndProcess(moleculeTool);
			}
		}
	}

	/**
	 * @return element
	 */
	public SVGG createGraphicsElement() {
		return new SVGG();
	}

	/**
	 * @throws IOException
	 */
	public void createOrDisplayGraphics() throws IOException {
	    g = abstractTool.createGraphicsElement(this);
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

	/**
	 * @return the moleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		enableMoleculeDisplay();
		return moleculeDisplay;
	}
	
	private void enableMoleculeDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = MoleculeDisplay.getDEFAULT();
		}
	}

	/**
	 * @param moleculeDisplay the moleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}

	/**
	 * @return the abstractTool (at present MoleculeTool or ReactionTool)
	 */
	public AbstractTool getAbstractTool() {
		return abstractTool;
	}

	/**
	 * @param abstractTool the abstractTool to set
	 */
	public void setAbstractTool(AbstractTool abstractTool) {
		this.abstractTool = abstractTool;
	}

	/**
	 * @return the svg
	 */
	public SVGSVG getSvg() {
		return svg;
	}

	/**
	 * @param svg the svg to set
	 */
	public void setSvg(SVGSVG svg) {
		this.svg = svg;
	}
}