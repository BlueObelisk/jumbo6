package org.xmlcml.cml.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.base.CMLElement.Hybridization;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLUnit.Units;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.molutil.ChemicalElement.Type;

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

