/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;


/**
 * @author pm286
 *
 */
public class MoleculeDisplayList implements CMLDrawable {
	private static Logger LOG = Logger.getLogger(MoleculeDisplayList.class);
	
	private String outfile;
	private AbstractSVGTool abstractSVGTool;
	private MoleculeDisplay moleculeDisplay;
	private SVGElement g;
	private SVGG svg;
	
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
			this.abstractSVGTool = moleculeTool;
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
		    	LOG.debug("REDRAW... MOL");
				g = moleculeTool.createGraphicsElement(this);
				g.detach();
				if (!svg.equals(g)) {
					svg.appendChild(g);
				}
//					CMLUtil.debug(svg);
				svg.clearCumulativeTransformRecursively();
				svg.setCumulativeTransformRecursively();
			}
		}
	}
	
	public void debugSVG() {
		if (svg != null) {
			CMLUtil.debug(svg, "SVG");
		} else {
			LOG.debug("NULL SVG in debug");
		}
	}

	void setAndProcess(ReactionTool reactionTool) {
		if (reactionTool != null) {
			this.abstractSVGTool = reactionTool;
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
	    g = abstractSVGTool.createGraphicsElement(this);
	}
	
	/**
	 * @param g graphics element (?is this the best way??)
	 * NO - it doesn't cater for multiple objects
	 * @throws IOException
	 */
	public void output(GraphicsElement g) throws IOException {
		if (svg == null) {
			svg = new SVGG();
			svg.appendChild(g);
		} else {
			// another object written - needs developing
		}
		if (outfile != null) {
			write();
		}
	}
	
	public void clear() {
		g = null;
		svg = null;
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
				LOG.debug("wrote SVG "+outfile);
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
		return abstractSVGTool;
	}

	/**
	 * @param abstractTool the abstractTool to set
	 */
	public void setAbstractTool(AbstractSVGTool abstractSVGTool) {
		this.abstractSVGTool = abstractSVGTool;
	}

	/**
	 * @return the svg
	 */
	public SVGG getSvg() {
		if (svg == null && g != null) {
			svg = (SVGG) g;
		}
		return svg;
	}

	/**
	 * @param svg the svg to set
	 */
	public void setSvg(SVGG svg) {
		this.svg = svg;
	}

	public void output(SVGElement g) {
		// TODO Auto-generated method stub
		
	}
}