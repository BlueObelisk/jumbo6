package org.xmlcml.cml.tools.matcher;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.AtomTreeMatcher;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.util.TestUtils;

public class TestFit2D {

	private static final Logger LOG = Logger.getLogger(TestFit2D.class);
	
	private static final double EPS = 0.001;
	
	public TestFit2D() {}
	
	@Test
	public void matchAtoms1() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		CMLMap refMap = (CMLMap) readCML("org/xmlcml/cml/tools/matcher/map1.xml");
		TestUtils.assertEqualsCanonically("map", refMap, cmlMap, true);
	}

	@Test
	public void fitAtoms2D() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
		Transform2 expected = new Transform2(new double[]{-0.9999999999074332,-1.3606383073029906E-5,4.3414483446047776E-5,
				1.3606383073029906E-5,-0.9999999999074332,-1.254011572237606E-4,
				0.0,0.0,1.0,});
		TestUtils.assertEquals("fit2d", expected, test, EPS);
		
 	}
	
	@Test
	public void fitAtoms2DB() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/2acetoxybenzoic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/2carboxy.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
		Transform2 expected = new Transform2(new double[]{0.9830239632543735,0.18347721293845792,-3.5699492578022154,
				-0.18347721293845792,0.9830239632543735,3.0464093503943506,
				0.0,0.0,1.0,});
		System.out.println(test);
		TestUtils.assertEquals("fit2d", expected, test, EPS);
	}
	
	@Test
	public void fitAtoms2DC() {
		CMLMolecule mol1 = readMolecule("org/xmlcml/cml/tools/matcher/acetylsalicylic.xml");
		CMLMolecule mol2 = readMolecule("org/xmlcml/cml/tools/matcher/2carboxy.xml");
		CMLMap cmlMap = new AtomTreeMatcher().mapMolecules(mol1, mol2);
		Transform2 test = new Matcher2D().fit2D(mol1, mol2, cmlMap);
		Transform2 expected = new Transform2(new double[]{-0.9831192965130704,-0.182965703954718,3.5706041359369074,
				0.182965703954718,-0.9831192965130704,-3.0459281597142356,
				0.0,0.0,1.0});
		System.out.println(test);
		TestUtils.assertEquals("fit2d", expected, test, EPS);
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
