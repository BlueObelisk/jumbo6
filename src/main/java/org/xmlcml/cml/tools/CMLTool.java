package org.xmlcml.cml.tools;

import java.util.List;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.euclid.Real2Range;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class CMLTool extends AbstractSVGTool {

    private CMLCml cml;
    Logger logger = Logger.getLogger(CMLTool.class.getName());
	/**
     * constructor
     * 
     * @param bond
     * @deprecated use getOrCreateTool
     */
    public CMLTool(CMLCml cml) {
        this.cml = cml;
    }

	/** gets BondTool associated with bond.
	 * if null creates one and sets it in bond
	 * @param bond
	 * @return tool
	 */
	public static CMLTool getOrCreateTool(CMLCml cml) {
		CMLTool cmlTool = (CMLTool) cml.getTool();
		if (cmlTool == null) {
			cmlTool = new CMLTool(cml);
			cml.setTool(cmlTool);
		}
		return cmlTool;
	}

	/**
	 * 
	 * @param cml
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLCml cml) {
		return (AbstractSVGTool) CMLTool.getOrCreateTool(cml);
	}

    /** returns a "g" element
     * this contains the lines for bond
     * @param drawable
     * @return null if problem or atom has no coords
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {

    	g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
    	List<CMLElement> cmlChildList = cml.getChildCMLElements();
    	for (CMLElement cmlChild : cmlChildList) {
    		AbstractSVGTool abstractSVGTool = AbstractSVGTool.getOrCreateSVGTool(cmlChild);
    		if (abstractSVGTool != null) {
    			SVGElement gChild = abstractSVGTool.createGraphicsElement(drawable);
    			if (gChild instanceof SVGSVG) {
    				Elements elements = gChild.getChildElements();
    				for (int i = 0; i < elements.size(); i++) {
    					Element element = elements.get(i);
    					if (element instanceof SVGElement) {
    						((SVGElement)element).detach();
    						g.appendChild(element);
    					}
    				}
    			} else {
	    			gChild.detach();
	    			g.appendChild(gChild);
    			}
    		} else {
    			System.out.println("NO GRAPHICS TOOL: "+cmlChild);
    		}
    	}
		return g;
    }
    
	/** by default iterate down hierarchy.
	 * 
	 * @return
	 */
	protected Real2Range calculateBoundingBox() {
		Real2Range range = new Real2Range();
		List<CMLElement> childElements = cml.getChildCMLElements();
		for (CMLElement element : childElements) {
			AbstractSVGTool svgTool = AbstractSVGTool.getOrCreateSVGTool(element);
			try {
				Real2Range childRange = svgTool.calculateBoundingBox();
				range = range.plus(childRange);
			} catch (CMLRuntimeException e) {
				System.out.println("NO atoms?");
			}
		}
		userBoundingBox = range;
		return userBoundingBox;
	}

}