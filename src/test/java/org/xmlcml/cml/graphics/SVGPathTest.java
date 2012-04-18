package org.xmlcml.cml.graphics;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.euclid.EuclidTestUtils;
import org.xmlcml.euclid.Real2Array;

public class SVGPathTest {

	@Test
	public void testCreatePolyline() {
		String d = "M379.558 218.898 L380.967 212.146 L380.134 212.146 L378.725 218.898 L379.558 218.898";
		SVGPath path = new SVGPath(d);
		SVGPolyline polyline = path.createPolyline();
		Assert.assertNotNull(polyline);
		Real2Array r2a = polyline.getReal2Array();
		String errmsg = EuclidTestUtils.testEquals("xarray", new double[] {379.558,380.967,380.134,378.725,379.558}, r2a.getXArray().getArray(), 0.001);
		Assert.assertNull(errmsg);
		errmsg = EuclidTestUtils.testEquals("xarray", new double[] {218.898,212.146,212.146,218.898,218.898}, r2a.getYArray().getArray(), 0.001);
		Assert.assertNull(errmsg);
	}
	
	@Test
	public void testBBScalefactor() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L2 8 L6 4");
		Double d = path1.getBoundingBoxScalefactor(path2);
		Assert.assertEquals("scale", 2.0, d, 0.00001);
	}
	
	@Test
	public void testScalefactor1() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L2 8 L6 4");
		Double d = path1.getScalefactor(path2, 0.00001);
		Assert.assertNull("cannot get scalefactor", d);
	}
	
	@Test
	public void testScalefactor2() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L6 8 L2 4");
		Double d = path1.getScalefactor(path2, 0.00001);
		Assert.assertEquals("scale", 2.0, d, 0.00001);
	}
	
}
