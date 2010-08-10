package org.xmlcml.cml.tools;

import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector3;

/**
 * tool for managing electron
 *
 * @author pmr
 *
 */
public class ElectronTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ElectronTool.class.getName());
	public static final String LONE_ELECTRONS = "loneElectrons";
	public static final String ELECTRONS = "electrons";

	CMLElectron electron = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public ElectronTool(CMLElectron electron) throws RuntimeException {
		init();
		this.electron = electron;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLElectron getElectron() {
		return this.electron;
	}

    
	/** gets ElectronTool associated with electron.
	 * if null creates one and sets it in electron
	 * @param electron
	 * @return tool
	 */
	public static ElectronTool getOrCreateTool(CMLElectron electron) {
		ElectronTool electronTool = null;
		if (electron != null) {
			electronTool = (ElectronTool) electron.getTool();
			if (electronTool == null) {
				electronTool = new ElectronTool(electron);
				electron.setTool(electronTool);
			}
		}
		return electronTool;
	}

	public static int getElectronCount(CMLElement element, String electronType) {
		int count = 0;
		if (element != null) {
			String loneElectronCountS = element.getCMLXAttribute(electronType);
			if (loneElectronCountS != null && !loneElectronCountS.equals("")) {
				count = Math.max(0, Integer.parseInt(loneElectronCountS));
			}
		}
		return count;
	}

};