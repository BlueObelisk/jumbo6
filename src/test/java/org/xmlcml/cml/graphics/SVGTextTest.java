package org.xmlcml.cml.graphics;

import static org.junit.Assert.fail;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.test.Real2Test;
import org.xmlcml.util.TestUtils;

public class SVGTextTest {
	private static Logger LOG = Logger.getLogger(SVGTextTest.class);

	static String STRING1 ="<text transform='translate(3,335.28) scale(1.0001,-0.99988) '" +
		" style='font-size:6.2023;stroke:none;fill:black;'" +
		">ppm</text>";
	
	@Test
	public void testSetup() {
		Element element = TestUtils.parseValidString(STRING1);
		SVGText text = (SVGText) SVGElement.createSVG(element);
		Assert.assertNotNull(text);
		Assert.assertEquals("class", SVGText.class, text.getClass());
		Assert.assertEquals("fontsize", 6.2023, text.getFontSize(), 0.0001);
		Assert.assertEquals("stroke", "none", text.getStroke());
		Assert.assertEquals("fill", "black", text.getFill());
		Assert.assertEquals("transform", "translate(3,335.28) scale(1.0001,-0.99988)",
				text.getAttributeValue("transform").trim());
	}

	@Test
	public void testApplyTransform() {
		Element element = TestUtils.parseValidString(STRING1);
		SVGText text = (SVGText) SVGElement.createSVG(element);
		text.applyTransformAttributeAndRemove();
		String expectedS = "<text " +
				"style='font-size:6.2023;stroke:none;fill:black;'" +
				" x='3.0' y='335.28' improper='true'" +
				" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		Element expected = TestUtils.parseValidString(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	public void testFormat() {
		Element element = TestUtils.parseValidString(STRING1);
		SVGText text = (SVGText) SVGElement.createSVG(element);
		text.applyTransformAttributeAndRemove();
		String expectedS = "<text " +
				"style='font-size:6.2023;stroke:none;fill:black;'" +
				" x='3.0' y='335.28' improper='true'" +
				" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		Element expected = TestUtils.parseValidString(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
		text.format(1);
		expectedS = "<text " +
		"style='font-size:6.2023;stroke:none;fill:black;'" +
		" x='3.0' y='335.3' improper='true'" +
		" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		expected = TestUtils.parseValidString(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	public void testGetXandY() {
		Element element = TestUtils.parseValidString(STRING1);
		SVGText text = (SVGText) SVGElement.createSVG(element);
		text.applyTransformAttributeAndRemove();
		Assert.assertEquals("x", 3.0, text.getX(), 0.01);
		Assert.assertEquals("y", 335.28, text.getY(), 0.01);
	}

	@Test
	public void testGetBoundingBox() {
		Element element = TestUtils.parseValidString(STRING1);
		SVGText text = (SVGText) SVGElement.createSVG(element);
		text.applyTransformAttributeAndRemove();
		Real2Range bb = text.getBoundingBox();
		Real2Range bbexpect = new Real2Range(new Real2(3.0, 335.28), new Real2(3.0, 335.28));
		Assert.assertTrue("bb", bbexpect.isEqualTo(bb, 0.01));
	}

	@Test
	public void testSVGTextReal2String() {
		SVGText text = new SVGText(new Real2(1., 2.), "string");
		String expectedS = "<text style=' stroke : none; font-size : 7.654321;' " +
				"x='1.0' y='2.0' xmlns='http://www.w3.org/2000/svg'>string</text>";
		Element expected = TestUtils.parseValidString(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	public void testGetEstimatedHorizontalLength() {
		String test1S = "<text style=' stroke : none; font-size : 7.654321;' " +
		"x='1.0' y='2.0' xmlns='http://www.w3.org/2000/svg'>string</text>";
		SVGText text1 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(test1S));
		Assert.assertEquals("font", 7.654321, text1.getFontSize(), 0.001);
		double fontWidthFactor = 1.1;
		double length = text1.getEstimatedHorizontalLength(fontWidthFactor);
		Assert.assertEquals("length", 23.4, length, 0.1);
		
		fontWidthFactor = 1.0;
		String s = "" +
				"<svg>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"3.0\" y=\"335.28\">ppm</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"14.76\" y=\"335.28\"> (f</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"19.92\" y=\"335.28\">1)</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"370.86\" y=\"342.36\">1.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"376.08\" y=\"342.36\">0</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"316.08\" y=\"342.36\">2.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"321.24\" y=\"342.36\">0</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"261.24\" y=\"342.36\">3.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"266.4\" y=\"342.36\">0</text>" +
				"</svg>";
		SVGSVG svg = (SVGSVG) SVGElement.createSVG(TestUtils.parseValidString(s));
		List<SVGElement> elementList = SVGElement.getElementList(svg, "./svg:text");
		Assert.assertEquals("texts", 9, elementList.size());
		double[] lengths = new double[elementList.size()];
		for (int i = 0; i < elementList.size(); i++) {
			SVGText text = (SVGText) elementList.get(i);
			lengths[i] = text.getEstimatedHorizontalLength(fontWidthFactor);
//			System.out.println("        "+Util.format(lengths[i], 1)+",");
		}
//		for (int i = 1; i < elementList.size(); i++) {
//			SVGText text0 = (SVGText) elementList.get(i-1);
//			SVGText text = (SVGText) elementList.get(i);
////			System.out.println("-----------------------");
//			length[i-1] = text0.getEstimatedHorizontalLength(fontWidthFactor);
//			double dist = text.getX()-text0.getX();
////			System.out.println(""+Util.format(text0.getY(), 2) +" "+Util.format(text.getY(), 2)+"["+text0.getValue()+"] "+Util.format(dist, 1)+" "+length[i-1]+" "+dist/length[i-1]);
//		}
		
		double[] expectedLength = new double[]{
		        13.1,
		        5.8,
		        5.5,
		        5.2,
		        3.5,
		        5.2,
		        3.5,
		        5.2,
		        3.5,
	        };
		String msg = org.xmlcml.euclid.TestUtils.testEquals("lengths", expectedLength, lengths, 0.1);
		if (msg != null) {
			Assert.fail(msg);
		}
	}

	@Test
	public void testGetCalculatedTextEnd() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Real2 textEnd = text.getCalculatedTextEnd(fontWidthFactor);
		Assert.assertTrue("text end", new Real2(16.74119565,30.0).isEqualTo(textEnd, 0.1));
		
		text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
			"<text rotate='"+SVGElement.YPLUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEnd = text.getCalculatedTextEnd(fontWidthFactor);
		Assert.assertEquals("text end", 16.26, textEnd.getY(), 0.1);
	}

	@Test
	public void testGetCalculatedTextEndCoordinate() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		double textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.74119565, textEndCoord, 0.1);
		
		text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
			"<text rotate='"+SVGElement.YPLUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.26, textEndCoord, 0.1);
		
		text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
			"<text rotate='"+SVGElement.YMINUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 43.74, textEndCoord, 0.1);
	}

	@Test
	public void testSetCalculatedTextEndCoordinate() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		double textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.74119565, textEndCoord, 0.1);
		text.setCalculatedTextEndCoordinate(10.0);
		Assert.assertEquals("text end", 10., text.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
	}

	@Test
	public void testGetCurrentFontSize() {
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
			"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Assert.assertEquals("font size", 6.20, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 6.20, text.getCurrentFontSize(), 0.1);
		text.setFontSize(10.0);
		Assert.assertEquals("font size", 10.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 6.20, text.getCurrentFontSize(), 0.1);
		text.setCurrentFontSize(5.0);
		Assert.assertEquals("font size", 10.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 5.0, text.getCurrentFontSize(), 0.1);
		text.setFontSize(15.0);
		Assert.assertEquals("font size", 15.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 5.0, text.getCurrentFontSize(), 0.1);
	}

	@Test
	public void testGetCurrentBaseY() {
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
			Assert.assertEquals("baseY", 30., text.getCurrentBaseY(), 0.1);
			text.setCurrentBaseY(10.0);
			Assert.assertEquals("baseY", 10., text.getCurrentBaseY(), 0.1);
			text.setXY(new Real2(5., 15.));
			Assert.assertEquals("baseY", 10., text.getCurrentBaseY(), 0.1);
			text.setRotate(SVGElement.YPLUS);
			Assert.assertEquals("baseY", 5., text.getCurrentBaseY(), 0.1);
			text.setCurrentBaseY(20.0);
			Assert.assertEquals("baseY", 20., text.getCurrentBaseY(), 0.1);
	}

	@Test
	public void testGetSetRotate() {
		SVGText text = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Assert.assertNull("rotate", text.getRotate());
		text.setRotate(SVGElement.YPLUS);
		Assert.assertEquals("rotate", SVGElement.YPLUS, text.getRotate());
	}

	@Test
	public void testConcatenateText() {
		double fontWidthFactor = 1.0;
		double fontHeightFactor = 1.0;
		SVGText text0 = testConcatenate(fontWidthFactor, fontHeightFactor, "<svg>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"3.0\" y=\"335.28\">ppm</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"14.76\" y=\"335.28\"> (f</text>" +
				"</svg>", true, 20.59, "ppm (f");
		SVGText text1 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"19.92\" y=\"335.28\">1)</text>"));
		SVGText text2 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 25.44, "ppm (f1)", text0, text1);

		SVGText text3 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"370.86\" y=\"342.36\">1.</text>"));
		SVGText text4 = testConcatenate(fontWidthFactor, fontHeightFactor, false, 25.44, null, text2, text3);
		
		SVGText text5 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
			"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"376.08\" y=\"342.36\">0</text>"));
		SVGText text6 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 379.55, "1.0", text3, text5);
		
		SVGText text7 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
		"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"316.08\" y=\"342.36\">2.</text>"));
		SVGText text8 = testConcatenate(fontWidthFactor, fontHeightFactor, false, 25.44, null, text6, text7);
		
		SVGText text9 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
		"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"321.24\" y=\"342.36\">0</text>"));

		SVGText text10 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 324.71, "2.0", text7, text9);
		
	}

	/**
	 * @param fontWidthFactor
	 * @param fontHeightFactor
	 * @param s
	 */
	private SVGText testConcatenate(double fontWidthFactor,
			double fontHeightFactor, String s, boolean mergedExpected, double endExpected, String textExpected) {
		SVGSVG svg = (SVGSVG) SVGElement.createSVG(TestUtils.parseValidString(s));
		List<SVGElement> elementList = SVGElement.getElementList(svg, "./svg:text");
		Assert.assertEquals("texts", 2, elementList.size());
		SVGText text0 = ((SVGText)elementList.get(0));
		SVGText text1 = ((SVGText)elementList.get(1));
		SVGText text2 = testConcatenate(fontWidthFactor, fontHeightFactor,
				mergedExpected, endExpected, textExpected, text0, text1);
		return text2;
	}

	/**
	 * @param fontWidthFactor
	 * @param fontHeightFactor
	 * @param mergedExpected
	 * @param endExpected
	 * @param textExpected
	 * @param text0
	 * @param text1
	 * @return
	 */
	private SVGText testConcatenate(double fontWidthFactor,
			double fontHeightFactor, boolean mergedExpected,
			double endExpected, String textExpected, SVGText text0,
			SVGText text1) {
		boolean merged = text0.concatenateText(fontWidthFactor, fontHeightFactor, text1, 0.5, -0.5, 0.1);
		Assert.assertTrue("merged", merged == mergedExpected);
		if (merged) {
			String newText = text0.getValue();
			Assert.assertEquals("text", textExpected, newText);
			double end = text0.getCalculatedTextEndCoordinate(fontWidthFactor);
			Assert.assertEquals("extent", endExpected, end, 0.1);
		}
		return text0;
	}
	
	@Test
	public void testConcatenate2() {
		double fontWidthFactor = 1.0;
		double fontHeightFactor = 1.0;
/**
<g class="peak">
  <line style="stroke-width:0.131;stroke-linecap:round;" x1="89.22" y1="121.98" x2="89.22" y2="125.88" /> 
  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"306.72\">17</text> 
  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"300.54\">8</text> 
  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"297.54\">.</text> 
  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"295.74\">616</text> 
  <line style="stroke-width:0.131;stroke-linecap:round;" x1="89.22" y1="277.86" x2="89.22" y2="304.14" /> 
  </g>
  */
		SVGText text0 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"306.72\">17</text>"));
		Assert.assertEquals("text0", 300.25, text0.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		
		SVGText text1 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"300.54\">8</text> "));
		Assert.assertEquals("text1", 297.30, text1.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		SVGText text01 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 297.30, "178", text0, text1);
		
		SVGText text2 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"297.54\">.</text> "));
		Assert.assertEquals("text2", 297.30, text1.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		SVGText text02 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 295.92, "178.", text01, text2);
		
		SVGText text3 = (SVGText) SVGElement.createSVG(TestUtils.parseValidString(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"295.74\">616</text>"));
		Assert.assertEquals("text2", 297.30, text1.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		SVGText text03 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 286.03, "178.616", text01, text3);
	}
}
