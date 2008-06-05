package org.xmlcml.cml.graphics;


import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.parseValidFile;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;

public class SVGElementTest {

	public final static String GRAPHICS_RESOURCE = "org/xmlcml/cml/graphics/examples";
	@Test
	public final void testSVGElementSVGElement() {
		//TODO
	}

	@Test
	public final void testcreateSVGElement() {
		Element oldElement = parseValidFile(GRAPHICS_RESOURCE+U_S+"image12.svg");
		SVGElement newSvg = SVGElement.createSVG(oldElement);
		Assert.assertEquals("class", SVGSVG.class, newSvg.getClass());
		assertEqualsCanonically("copy", parseValidFile(GRAPHICS_RESOURCE+U_S+"image12.svg"), newSvg, true);
	}

	@Test
	public final void testSVGElementElement() {
		//TODO
	}

	@Test
	public final void testSetScale() {
		//TODO
	}

}
