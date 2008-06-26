package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class CmlTool extends AbstractSVGTool {

    private CMLCml cml;

	/**
     * constructor
     * 
     * @param bond
     * @deprecated use getOrCreateTool
     */
    public CmlTool(CMLCml cml) {
        this.cml = cml;
    }

	/** gets BondTool associated with bond.
	 * if null creates one and sets it in bond
	 * @param bond
	 * @return tool
	 */
	public static CmlTool getOrCreateTool(CMLCml cml) {
		CmlTool cmlTool = (CmlTool) cml.getTool();
		if (cmlTool == null) {
			cmlTool = new CmlTool(cml);
			cml.setTool(cmlTool);
		}
		return cmlTool;
	}

	/**
	 * 
	 * @param reaction
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLCml cml) {
		return (AbstractSVGTool) CmlTool.getOrCreateTool(cml);
	}

    /** returns a "g" element
     * this contains the lines for bond
     * @param drawable
     * @return null if problem or atom has no coords
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	g = null;
		return g;
    }

}