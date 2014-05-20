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

import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGSVG;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class CMLXTool extends AbstractSVGTool {
	private static Logger LOG = Logger.getLogger(CMLXTool.class);

    private CMLCml cml;
	/**
     * constructor
     * 
     * @param bond
     * @deprecated use getOrCreateTool
     */
    public CMLXTool(CMLCml cml) {
        this.cml = cml;
    }

	/** gets BondTool associated with bond.
	 * if null creates one and sets it in bond
	 * @param bond
	 * @return tool
	 */
	public static CMLXTool getOrCreateTool(CMLCml cml) {
		CMLXTool cmlTool = (CMLXTool) cml.getTool();
		if (cmlTool == null) {
			cmlTool = new CMLXTool(cml);
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
		return (AbstractSVGTool) CMLXTool.getOrCreateTool(cml);
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
    			LOG.error("NO GRAPHICS TOOL: "+cmlChild);
    		}
    	}
		return g;
    }
    
	/** by default iterate down hierarchy.
	 * 
	 * @return
	 */
	protected Real2Range calculateBoundingBox2D() {
		Real2Range range = new Real2Range();
		List<CMLElement> childElements = cml.getChildCMLElements();
		for (CMLElement element : childElements) {
			AbstractSVGTool svgTool = AbstractSVGTool.getOrCreateSVGTool(element);
			try {
				Real2Range childRange = svgTool.calculateBoundingBox2D();
				range = range.plus(childRange);
			} catch (RuntimeException e) {
				LOG.debug("NO atoms?");
			}
		}
		userBoundingBox = range;
		return userBoundingBox;
	}

}