package org.xmlcml.cml.graphics;


import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.element.AbstractTest;

public class SVGElementTest extends AbstractTest {

	public final static String GRAPHICS_RESOURCE = "org/xmlcml/cml/graphics/examples";
	@Test
	public final void testSVGElementSVGElement() {
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
	}

	@Test
	public final void testSetScale() {
	}

}
