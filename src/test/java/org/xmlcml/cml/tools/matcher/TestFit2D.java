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

package org.xmlcml.cml.tools.matcher;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;
import org.xmlcml.cml.tools.AtomTreeMatcher;
import org.xmlcml.euclid.Transform2;


public class TestFit2D {

	private static final Logger LOG = Logger.getLogger(TestFit2D.class);
	public TestFit2D() {}
	
	@Test
	public void matchAtoms1() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		CMLMap refMap = (CMLMap) readCML("org/xmlcml/cml/tools/matcher/map1.xml");
		CMLXOMTestUtils.assertEqualsCanonically("map", refMap, cmlMap);
	}

	@Test
	public void fitAtoms2D() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
//		LOG.trace(test);
		Transform2 expected = new Transform2(new double[]{-0.9999999999729805,7.3511284464652065E-6,-8.136727333654608E-6,
				-7.3511284464652065E-6,-0.9999999999729805,2.8559189798344974E-5,
				0.0,0.0,1.0});
		CMLXOMTestUtils.assertEquals("fit2d", expected, test, 0.0001);
		
 	}
	
	@Test
	public void fitAtoms2DB() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/2carboxy.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
		Transform2 expected = new Transform2(new double[]{0.983077626540501,0.18318946529616598,-3.570670927524805,
				-0.18318946529616598,0.983077626540501,3.0463031621387975,
				0.0,0.0,1.0,});
//		LOG.trace(test);
		CMLXOMTestUtils.assertEquals("fit2d", expected, test, 0.0001);
	}
	
	@Test
	public void fitAtoms2DC() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/2carboxy.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
		Transform2 expected = new Transform2(new double[]{-0.9830956282690666,-0.18309283349778954,3.5706653198667855,
				0.18309283349778954,-0.9830956282690666,-3.0461985876806974,
				0.0,0.0,1.0,});
//		LOG.trace(test);
		CMLXOMTestUtils.assertEquals("fit2d", expected, test, 0.0001);
	}
	
	private CMLElement readCML(String filename) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
		CMLElement cml = null;
		try {
			cml = (CMLElement) new CMLBuilder().build(is).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read cml "+filename,e);
		}
		return cml;
	}
	
	private CMLMolecule readMolecule(String filename) {
		CMLElement cml = readCML(filename);
		return (cml == null) ? null : (CMLMolecule) cml.getFirstCMLChild(CMLMolecule.TAG);
	}
}
