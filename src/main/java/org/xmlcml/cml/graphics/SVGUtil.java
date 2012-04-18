package org.xmlcml.cml.graphics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Transform2;

public class SVGUtil {

	/**
	 * adds a new svg:g between element and its children
	 * this can be used to set scales, rendering, etc.
	 * also copies ant transform attribute
	 * @param element to amend (is changed)
	 */
	public static SVGG interposeGBetweenChildren(SVGElement element) {
		SVGG g = new SVGG();
		element.appendChild(g);
		while (element.getChildCount() > 1) {
			Node child = element.getChild(0);
			child.detach();
			g.appendChild(child);
		}
		return g;
	}

	/** creates an SVGElement
	 * 
	 * @param is
	 * @return
	 */
	public static SVGElement parseToSVGElement(InputStream is) {
		Element element = null;
		try {
			element = new Builder().build(is).getRootElement();
			return SVGElement.createSVG(element);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse input stream", e);
		}
	}

	public static List<SVGElement> getQuerySVGElements(SVGSVG svg, String xpath) {
		List<Element> elements = CMLUtil.getQueryElements(svg, xpath, SVGConstants.SVG_XPATH);
		List<SVGElement> svgElements = new ArrayList<SVGElement>();
		for (Element element : elements) {
			svgElements.add((SVGElement)element);
		}
		return svgElements;
	}
	

}
