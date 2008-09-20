package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Elements;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLKpoint;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLKpoint;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Real3Range;

/**
 * tool to support atom set. not sure if useful
 * 
 * @author pmr
 * 
 */
public class KpointTool extends AbstractTool {
	private static Logger LOG = Logger.getLogger(KpointTool.class);
	
	Logger logger = Logger.getLogger(KpointTool.class.getName());

	private CMLKpoint kpoint;

	/**
	 * constructor.
	 * 
	 * @param kpoint
	 * @deprecated use getOrCreateTool
	 */
	public KpointTool(CMLKpoint kpoint) {
		if (kpoint == null) {
			throw new CMLRuntimeException("Null kpoint");
		}
		this.kpoint = kpoint;
	}

	/** gets KpointTool associated with kpoint.
	 * if null creates one and sets it in kpoint
	 * @param kpoint
	 * @return tool
	 */
	public static KpointTool getOrCreateTool(CMLKpoint kpoint) {
		KpointTool kpointTool = null;
		if (kpoint != null) {
			kpointTool = (KpointTool) kpoint.getTool();
			if (kpointTool == null) {
				kpointTool = new KpointTool(kpoint);
				kpoint.setTool(kpointTool);
			}
		}
		return kpointTool;
	}

}
