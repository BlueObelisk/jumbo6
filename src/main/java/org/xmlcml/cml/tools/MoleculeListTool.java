package org.xmlcml.cml.tools;

import java.io.IOException;
import java.util.logging.Logger;

import nu.xom.Attribute;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;

/**
 * additional tools for molecule. not fully developed
 *
 * @author pmr
 *
 */
public class MoleculeListTool extends AbstractTool {

	Logger logger = Logger.getLogger(MoleculeListTool.class.getName());

    /** dewisott */
	private CMLMoleculeList moleculeList;	
	private MoleculeDisplay moleculeDisplay;

	/**
	 * constructor
	 *
	 * @param moleculeList
	 * @deprecated use getOrCreateTool
	 */
	public MoleculeListTool(CMLMoleculeList moleculeList) {
		this.moleculeList = moleculeList;
		this.moleculeList.setTool(this);
	}

	/**
	 * get moleculeList.
	 *
	 * @return the moleculeList
	 */
	public CMLMoleculeList getMoleculeList() {
		return moleculeList;
	}
	
	
	/** gets MoleculeListTool associated with molecule.
	 * if null creates one and sets it in molecule
	 * @param molecule
	 * @return tool
	 */
	@SuppressWarnings("all")
	public static MoleculeListTool getOrCreateTool(CMLMoleculeList moleculeList) {
		MoleculeListTool moleculeListTool = (MoleculeListTool) moleculeList.getTool();
		if (moleculeListTool == null) {
			moleculeListTool = new MoleculeListTool(moleculeList);
			moleculeList.setTool(moleculeListTool);
		}
		return moleculeListTool;
	}


	
    /** returns a "g" element
     * will require to be added to an svg element
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement() throws IOException {
    	SVGElement svg = createGraphicsElement(null);
    	return svg;
    }
	
    /** returns a "g" element
     * will require to be added to an svg element
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) throws IOException {
    	SVGG g = new SVGG();
    	int i = 0;
    	for (CMLMolecule molecule : moleculeList.getMoleculeElements()) {
    		SVGElement molG = MoleculeTool.getOrCreateTool(molecule).createGraphicsElement();
    		molG.addAttribute(new Attribute("transform", "matrix(10, 0, 0, 10, 10, "+(i*10)+")"));
    		g.appendChild(molG);
    		i++;
    	}
    	return g;
    }

    private void enableMoleculeDisplay() {
    	if (moleculeDisplay == null) {
    		moleculeDisplay = MoleculeDisplay.getDEFAULT();
    	}
    }


	/**
	 * @return the MoleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = new MoleculeDisplay();
		}
		return moleculeDisplay;
	}

	/**
	 * @param MoleculeDisplay the MoleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay MoleculeDisplay) {
		this.moleculeDisplay = MoleculeDisplay;
	}
	
	
}

