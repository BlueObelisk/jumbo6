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

/**
 * 
 */
package org.xmlcml.cml.tools;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.math.fraction.Fraction;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * @author pm286
 * 
 */
public class MorganMD5Test {
	private static Logger LOG = Logger.getLogger(MorganMD5Test.class);

	@Test
	public void testMorganWater() {
		CMLMolecule molecule = SMILESTool.createMolecule("O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("water", "ee698e506161ae1bdec66931b1d23e84", s);
	}

	@Test
	public void testMorgan() {
		CMLMolecule molecule = SMILESTool.createMolecule("O=CN");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "3e96d79b2176b4e17c29c7c6f54423f3", s);
	}
	@Test	
	public void testMorgan1() {
		CMLMolecule molecule = SMILESTool.createMolecule("NC=O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "3e96d79b2176b4e17c29c7c6f54423f3", s);
	}
	
	@Test	
	public void testMorganFormamideWater() {
		CMLMolecule molecule = SMILESTool.createMolecule("NC=O.O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84", s);
	}

	
	@Test	
	public void testMorganWaterFormamide() {
		CMLMolecule molecule = SMILESTool.createMolecule("O.NC=O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84", s);
	}

	@Test
	public void testMorganWater2() {
		CMLMolecule molecule = SMILESTool.createMolecule("O.O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "*2*ee698e506161ae1bdec66931b1d23e84", s);
	}

	@Test	
	public void testMorganFormamideWater2() {
		CMLMolecule molecule = SMILESTool.createMolecule("NC=O.O.O");
		String s = MorganMD5.createMorganMD5(molecule);
		Assert.assertEquals("formamide", "*1*3e96d79b2176b4e17c29c7c6f54423f3*2*ee698e506161ae1bdec66931b1d23e84", s);
	}

	@Test	
	public void testInterpretWater() {
		List<MorganMD5> morganList = MorganMD5.interpretCountedMorganString(
				"ee698e506161ae1bdec66931b1d23e84");
		Assert.assertNotNull(morganList);
		Assert.assertEquals("size", 1, morganList.size());
		Assert.assertEquals("count", new Integer(1), morganList.get(0).getCount());
		Assert.assertEquals("morgan", "ee698e506161ae1bdec66931b1d23e84", morganList.get(0).getEquivalenceString());
	}


	@Test	
	public void testInterpretWater2() {
		List<MorganMD5> morganList = MorganMD5.interpretCountedMorganString(
				"*2*ee698e506161ae1bdec66931b1d23e84");
		Assert.assertNotNull(morganList);
		Assert.assertEquals("size", 1, morganList.size());
		Assert.assertEquals("count", new Integer(2), morganList.get(0).getCount());
		Assert.assertEquals("morgan", "ee698e506161ae1bdec66931b1d23e84", morganList.get(0).getEquivalenceString());
	}

	@Test	
	public void testInterpretWaterFormamide() {
		List<MorganMD5> morganList = MorganMD5.interpretCountedMorganString(
				"*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84");
		Assert.assertNotNull(morganList);
		Assert.assertEquals("size", 2, morganList.size());
		Assert.assertEquals("count", new Integer(1), morganList.get(0).getCount());
		Assert.assertEquals("morgan", "3e96d79b2176b4e17c29c7c6f54423f3", morganList.get(0).getEquivalenceString());
		Assert.assertEquals("count", new Integer(1), morganList.get(1).getCount());
		Assert.assertEquals("morgan", "ee698e506161ae1bdec66931b1d23e84", morganList.get(1).getEquivalenceString());
	}

	@Test	
	public void testInterpretWater2Formamide() {
		List<MorganMD5> morganList = MorganMD5.interpretCountedMorganString(
				"*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84");
		Assert.assertNotNull(morganList);
		Assert.assertEquals("size", 2, morganList.size());
		Assert.assertEquals("count", new Integer(2), morganList.get(0).getCount());
		Assert.assertEquals("morgan", "3e96d79b2176b4e17c29c7c6f54423f3", morganList.get(0).getEquivalenceString());
		Assert.assertEquals("count", new Integer(1), morganList.get(1).getCount());
		Assert.assertEquals("morgan", "ee698e506161ae1bdec66931b1d23e84", morganList.get(1).getEquivalenceString());
	}

	@Test	
	public void testInterpretJunk() {
		try {
		List<MorganMD5> morganList = MorganMD5.interpretCountedMorganString(
				"3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84");
			Assert.fail("should catch badly formed string");
		} catch (Exception e) {
			Assert.assertTrue("should catch badly formed string", true);
			
		}
	}

	@Test	
	public void testInterpretJunk1() {
		try {
			MorganMD5.interpretCountedMorganString(
				"*1*ee698e506161ae1bdec66931b1d23e84GROT");
			Assert.fail("should catch badly formed string");
		} catch (Exception e) {
			Assert.assertTrue("should catch badly formed string", true);
			
		}
	}

	@Test	
	public void testInterpretJunk2() {
		try {
			MorganMD5.interpretCountedMorganString(
				"JUNK*1*ee698e506161ae1bdec66931b1d23e84");
			Assert.fail("should catch badly formed string");
		} catch (Exception e) {
			Assert.assertTrue("should catch badly formed string", true);
			
		}
	}

	@Test	
	public void testInterpretJunk3() {
		try {
			MorganMD5.interpretCountedMorganString(
				"ee698e506161ae1bdec66931b1d23e84GROT");
			Assert.fail("should catch badly formed string");
		} catch (Exception e) {
			Assert.assertTrue("should catch badly formed string", true);
			
		}
	}

	@Test	
	public void testHaveCommonComponents() {
		String morganString1 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		boolean hasCommonComponents = MorganMD5.haveIdenticalComponents(
				morganString1, morganString2);
		Assert.assertTrue("common components", hasCommonComponents);
	}

	@Test	
	public void testHaveCommonComponents1() {
		String morganString1 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		boolean hasCommonComponents = MorganMD5.haveIdenticalComponents(
				morganString1, morganString2);
		Assert.assertTrue("common components", hasCommonComponents);
	}

	@Test	
	public void testHaveCommonComponents2() {
		String morganString1 = "*2*3e96d79b217aaaaaaa29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		boolean hasCommonComponents = MorganMD5.haveIdenticalComponents(
				morganString1, morganString2);
		Assert.assertFalse("common components", hasCommonComponents);
	}

	@Test	
	public void testHaveCommonComponents3() {
		String morganString1 = "*1*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*1*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		boolean hasCommonComponents = MorganMD5.haveIdenticalComponents(
				morganString1, morganString2);
		Assert.assertFalse("common components", hasCommonComponents);
	}

	@Test	
	public void testFraction() {
		String morganString1 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		List<Fraction> multiplierList = MorganMD5.getMultiplierList(morganString1, morganString2);
		Assert.assertEquals("fraction size", 2, multiplierList.size());
		Assert.assertTrue("fraction 0", new Fraction(1,1).equals(multiplierList.get(0)));
		Assert.assertTrue("fraction 1", new Fraction(1,1).equals(multiplierList.get(1)));
		
	}

	@Test	
	public void testFraction1() {
		String morganString1 = "*4*3e96d79b2176b4e17c29c7c6f54423f3*2*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		List<Fraction> multiplierList = MorganMD5.getMultiplierList(morganString1, morganString2);
		Assert.assertEquals("fraction size", 2, multiplierList.size());
		Assert.assertTrue("fraction 0", new Fraction(2,1).equals(multiplierList.get(0)));
		Assert.assertTrue("fraction 1", new Fraction(2,1).equals(multiplierList.get(1)));
		
	}

	@Test	
	public void testCommonFraction() {
		String morganString1 = "*4*3e96d79b2176b4e17c29c7c6f54423f3*2*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		Fraction commonFraction = MorganMD5.getCommonMultiplier(morganString1, morganString2);
		LOG.trace(commonFraction);
		Assert.assertTrue("fraction ",new Fraction(2,1).equals(commonFraction));
	}

	@Test	
	public void testCommonFraction1() {
		String morganString1 = "*4*3e96d79b2176b4e17c29c7c6f54423f3*3*ee698e506161ae1bdec66931b1d23e84";
		String morganString2 = "*2*3e96d79b2176b4e17c29c7c6f54423f3*1*ee698e506161ae1bdec66931b1d23e84";
		Fraction commonFraction = MorganMD5.getCommonMultiplier(morganString1, morganString2);
		Assert.assertNull("fraction ",commonFraction);
	}




}
