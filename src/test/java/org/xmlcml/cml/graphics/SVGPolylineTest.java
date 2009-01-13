package org.xmlcml.cml.graphics;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.RealArray;

public class SVGPolylineTest {

	@Test
	@Ignore
	public void testSVGPolylineReal2Array() {
		Real2Array r2a = Real2Array.createFromPairs(new RealArray(new double[] {
				1., 2., 3., 4., 5., 3.,
		}));
		@SuppressWarnings("unused")
		SVGPolyline polyline = new SVGPolyline(r2a);
	}

	@Test
	@Ignore
	public void testGetReal2Array() {
		Assert.fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetPath2() {
		Assert.fail("Not yet implemented");
	}

}
