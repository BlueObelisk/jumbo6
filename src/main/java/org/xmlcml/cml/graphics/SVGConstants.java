package org.xmlcml.cml.graphics;

import nu.xom.XPathContext;

import org.xmlcml.cml.base.CMLConstants;

public interface SVGConstants extends CMLConstants {

	/** standard namespace for SVG
	 * 
	 */
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    /** XPathContext for CML.
     */
    XPathContext SVG_XPATH = new XPathContext("svg", SVG_NAMESPACE);
    
}
