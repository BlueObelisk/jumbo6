package org.xmlcml.cml.graphics;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.testutil.TstUtils;

public class SVGElementTest {

	public final static String GRAPHICS_RESOURCE = "org/xmlcml/cml/graphics/examples";

	@Test
	public final void testcreateSVGElement() {
		Element oldElement =TstUtils.parseValidFile(GRAPHICS_RESOURCE + CMLConstants.U_S
				+ "image12.svg");
		SVGElement newSvg = SVGElement.createSVG(oldElement);
		Assert.assertEquals("class", SVGSVG.class, newSvg.getClass());
		TstUtils.assertEqualsCanonically("copy",TstUtils.parseValidFile(GRAPHICS_RESOURCE + CMLConstants.U_S
				+ "image12.svg"), newSvg, true);
	}

}
