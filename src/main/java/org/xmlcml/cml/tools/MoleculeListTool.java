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

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
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
public class MoleculeListTool extends AbstractSVGTool {

	Logger logger = Logger.getLogger(MoleculeListTool.class.getName());

    /** dewisott */
	private CMLMoleculeList moleculeList;	
	private AbstractDisplay moleculeDisplay;

	/**
	 * constructor
	 *
	 * @param moleculeList
	 * @deprecated use getOrCreateTool
	 */
	public MoleculeListTool(CMLMoleculeList moleculeList) {
		if (moleculeList == null) {
			throw new RuntimeException("null moleculeList");
		}
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
		MoleculeListTool moleculeListTool = null;
		if (moleculeList != null) {
			moleculeListTool = (MoleculeListTool) moleculeList.getTool();
			if (moleculeListTool == null) {
				moleculeListTool = new MoleculeListTool(moleculeList);
				moleculeList.setTool(moleculeListTool);
			}
		}
		return moleculeListTool;
	}

	/** retrieve first molecule with given id.
	 * O(n) linear search - crude
	 * @param id
	 * @return null if not found
	 */
	public CMLMolecule getMoleculeById(String id) {
		CMLMolecule molecule = null;
		if (id != null) {
			for (CMLMolecule mol : moleculeList.getMoleculeElements()) {
				if (id.equals(mol.getId())) {
					molecule = mol;
					break;
				}
			}
		}
		return molecule;
	}

	/**
	 * 
	 * @param moleculeList
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLMoleculeList moleculeList) {
		return (AbstractSVGTool) MoleculeListTool.getOrCreateTool(moleculeList);
	}
	
    /** returns a "g" element
     * will require to be added to an svg element
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	SVGElement g = new SVGG();
    	int i = 0;
    	for (CMLMolecule molecule : moleculeList.getMoleculeElements()) {
    		SVGElement molG = MoleculeTool.getOrCreateTool(molecule).createGraphicsElement(drawable);
    		molG.addAttribute(new Attribute("transform", "matrix(10, 0, 0, 10, 10, "+(i*10)+")"));
    		molG.detach();
    		g.appendChild(molG);
    		i++;
    	}
    	return g;
    }

	/**
	 * @return the MoleculeDisplay
	 */
	public AbstractDisplay getMoleculeDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = new MoleculeDisplay();
		}
		return moleculeDisplay;
	}

	/**
	 * @param MoleculeDisplay the MoleculeDisplay to set
	 */
	public void setMoleculeDisplay(AbstractDisplay MoleculeDisplay) {
		this.moleculeDisplay = MoleculeDisplay;
	}
	
	/** find single toplevel molecule or moleculeList as descendant-or-self of rootound
	 * if a (single) molecule is found, wrap it in a moleculeList
	 * 
	 * @param root
	 * @return
	 */
	public static CMLMoleculeList ensureOrCreateRootMoleculeList(CMLElement root) {
		CMLMoleculeList moleculeList = null;
		Nodes moleculeNodes = root.query(
				"//*[local-name()='molecule' " +
				"and not(ancestor::*[local-name()='molecule'])" +
				"and not(ancestor::*[local-name()='moleculeList'])" +
				" ]");
		Nodes moleculeListNodes = root.query(
				"//*[local-name()='moleculeList' " +
				"and not(ancestor::*[local-name()='moleculeList'])]");
		if (moleculeNodes.size() == 1 && moleculeListNodes.size() == 0) {
			CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(0);
			moleculeList = new CMLMoleculeList();
			molecule.detach();
			moleculeList.appendChild(molecule);
		} else if (moleculeListNodes.size() == 1 && moleculeNodes.size() == 0) {
			moleculeList = (CMLMoleculeList) moleculeListNodes.get(0);
			moleculeList.detach();
		} else {
			throw new RuntimeException("Cannot find single molecule or moleculeList");
		}
		return moleculeList;
	}

}

