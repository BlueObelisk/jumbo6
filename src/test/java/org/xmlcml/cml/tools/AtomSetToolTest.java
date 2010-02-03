package org.xmlcml.cml.tools;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;
import org.xmlcml.cml.testutil.CMLAssert;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;

/**
 * test AtomSetTool.
 * 
 * @author pm286
 * 
 */
public class AtomSetToolTest {
	private static Logger LOG = Logger.getLogger(AtomSetToolTest.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	MoleculeAtomBondFixture fixture = new MoleculeAtomBondFixture();
	String sproutS = CMLConstants.S_EMPTY + "<molecule " + CMLConstants.CML_XMLNS + " title='sprout'>"
			+ "  <atomArray>" + "    <atom id='a1' elementType='C'/>"
			+ "    <atom id='a2' elementType='C'/>"
			+ "    <atom id='a3' elementType='C'/>"
			+ "    <atom id='a4' elementType='C'/>"
			+ "    <atom id='a5' elementType='C'/>"
			+ "    <atom id='a6' elementType='C'/>"
			+ "    <atom id='a7' elementType='F'/>"
			+ "    <atom id='a8' elementType='Cl'/>"
			+ "    <atom id='a9' elementType='Br'/>"
			+ "    <atom id='a10' elementType='I'/>"
			+ "    <atom id='a11' elementType='H'/>"
			+ "    <atom id='a12' elementType='C'/>"
			+ "    <atom id='a13' elementType='O'/>" + "   </atomArray>"
			+ "   <bondArray>" + "     <bond id='a1 a2' atomRefs2='a1 a2'/>"
			+ "     <bond id='a2 a3' atomRefs2='a2 a3'/>"
			+ "     <bond id='a3 a4' atomRefs2='a3 a4'/>"
			+ "     <bond id='a4 a5' atomRefs2='a4 a5'/>"
			+ "     <bond id='a5 a6' atomRefs2='a5 a6'/>"
			+ "     <bond id='a1 a6' atomRefs2='a1 a6'/>"
			+ "     <bond id='a1 a7' atomRefs2='a1 a7'/>"
			+ "     <bond id='a2 a8' atomRefs2='a2 a8'/>"
			+ "     <bond id='a3 a9' atomRefs2='a3 a9'/>"
			+ "     <bond id='a4 a10' atomRefs2='a4 a10'/>"
			+ "     <bond id='a5 a11' atomRefs2='a5 a11'/>"
			+ "     <bond id='a6 a12' atomRefs2='a6 a12'/>"
			+ "     <bond id='a12 a13' atomRefs2='a12 a13'/>"
			+ "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;
	CMLMolecule sprout = null;

	CMLAtomSet atomSet1 = null;
	CMLAtomSet atomSet2 = null;
	CMLMolecule dmf;

	/**
	 * setup.
	 * 
	 * @exception Exception
	 */
	@Before
	public void setUp() throws Exception {
		sprout = (CMLMolecule)JumboTestUtils.parseValidString(sproutS);
		atomSet1 = new CMLAtomSet(fixture.xmlMolecule, new String[] { "a1",
				"a2", "a3" });
		atomSet2 = new CMLAtomSet(fixture.xmlMolecule, new String[] { "a2",
				"a3", "a4", "a5" });
		AtomTreeTest att = new AtomTreeTest();
		att.setUp();
		dmf = att.dmf;
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtomSet.getOverlapping3DAtoms(CMLAtomSet
	 * otherSet)'
	 */
	@Test
	@Ignore("needs checking")
	public void testGetOverlapping3DAtomsCMLAtomSet() {
		CMLMolecule mol1 = null;
		try {
			mol1 = (CMLMolecule) new CMLBuilder().build(
					new StringReader("<molecule " + CMLConstants.CML_XMLNS + ">"
							+ "  <atomArray>"
							+ "    <atom id='a1' x3='1.0' y3='2.0' z3='0.0'/>"
							+ "    <atom id='a2' x3='3.0' y3='4.0' z3='0.0'/>"
							+ "    <atom id='a3' x3='2.0' y3='3.0' z3='1.0'/>"
							+ "  </atomArray>" + "</molecule>"))
					.getRootElement();
		} catch (Exception e) {
			Assert.fail("bug " + e);
		}
		CMLMolecule mol2 = null;
		try {
			mol2 = (CMLMolecule) new CMLBuilder()
					.build(
							new StringReader(
									"<molecule "
											+ CMLConstants.CML_XMLNS
											+ ">"
											+ "  <atomArray>"
											+ "    <atom id='a11' x3='1.0' y3='2.0' z3='0.0'/>"
											+ "    <atom id='a12' x3='3.0' y3='4.0' z3='0.0'/>"
											+ "    <atom id='a13' x3='2.0' y3='3.0' z3='-1.0'/>"
											+ "  </atomArray>" + "</molecule>"))
					.getRootElement();
		} catch (Exception e) {
			Assert.fail("bug " + e);
		}

		AtomSetTool atomSetTool1 = AtomSetTool.getOrCreateTool(MoleculeTool
				.getOrCreateTool(mol1).getAtomSet());
		CMLAtomSet atomSet = atomSetTool1.getOverlapping3DAtoms(MoleculeTool
				.getOrCreateTool(mol2).getAtomSet(), CoordinateType.CARTESIAN);
		Assert.assertEquals("overlap", new String[] { "a1", "a2" }, atomSet
				.getXMLContent());

		atomSet = atomSetTool1.getOverlapping3DAtoms(MoleculeTool
				.getOrCreateTool(mol2).getAtomSet(), CoordinateType.FRACTIONAL);
		Assert.assertEquals("overlap", 0, atomSet.size());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomSetTool.sprout()'
	 */
	@Test
	public void testSprout() {

		List<CMLAtomSet> ringNucleiAtomSets = new ConnectionTableTool(sprout)
				.getRingNucleiAtomSets();
		Assert.assertEquals("sprout size", 1, ringNucleiAtomSets.size());
		CMLAtomSet sproutAtomSet = ringNucleiAtomSets.get(0);
		Assert.assertEquals("pre sprout size", 6, sproutAtomSet.size());
		AtomSetTool sproutAtomSetTool = AtomSetTool
				.getOrCreateTool(sproutAtomSet);
		CMLAtomSet sproutAtomSprout = sproutAtomSetTool.sprout();
		Assert.assertEquals("sprout size", 12, sproutAtomSprout.size());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtomSet.transform(CMLTransform3)'
	 */
	@Test
	public final void testTransformCMLTransform3() {
		CMLTransform3 t = new CMLTransform3("y, -x, y+z");
		AtomSetTool.getOrCreateTool(atomSet1).transformCartesians(t);
		Point3Vector p3v = atomSet1.getCoordinates3(CoordinateType.CARTESIAN);
		Assert.assertEquals("point3vector", 3, p3v.size());
		JumboTestUtils.assertEquals("point3vector", new double[] { 0.0, 0.0, 0.0 },
				p3v.get(0), CC.EPS);
		JumboTestUtils.assertEquals("point3vector", new double[] { 1.0, -1.0, 2.0 }, p3v
				.get(1), CC.EPS);
		JumboTestUtils.assertEquals("point3vector", new double[] { -1.0, -1.0, -2.0 }, p3v
				.get(2), CC.EPS);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtomSet.transformFractionalCoordinates(CMLTran
	 * s f o r m 3 ) '
	 */
	@Test
	public final void testTransformFractionalCoordinatesCMLTransform3() {
		CMLTransform3 t = new CMLTransform3("y, -x, y+z");
		atomSet1.getAtom(0).setPoint3(new Point3(0.1, 0.2, 0.3),
				CoordinateType.FRACTIONAL);
		atomSet1.getAtom(1).setPoint3(new Point3(0.4, 0.5, 0.6),
				CoordinateType.FRACTIONAL);
		atomSet1.getAtom(2).setPoint3(new Point3(0.7, 0.8, 0.9),
				CoordinateType.FRACTIONAL);
		AtomSetTool atomSetTool1 = AtomSetTool.getOrCreateTool(atomSet1);
		atomSetTool1.transformFractionals(t);
		Point3Vector p3v = atomSet1.getCoordinates3(CoordinateType.FRACTIONAL);
		Assert.assertEquals("point3vector", 3, p3v.size());
		JumboTestUtils.assertEquals("point3vector", new double[] { 0.2, -0.1, 0.5 }, p3v
				.get(0), CC.EPS);
		JumboTestUtils.assertEquals("point3vector", new double[] { 0.5, -0.4, 1.1 }, p3v
				.get(1), CC.EPS);
		JumboTestUtils.assertEquals("point3vector", new double[] { 0.8, -0.7, 1.7 }, p3v
				.get(2), CC.EPS);
	}

    /**
     * 
     */
    @Test
    public void testClean2D() {
    	CMLMolecule molecule = new CMLMolecule();
    	CMLAtom atom1 = new CMLAtom("a1");
    	atom1.setXY2(new Real2(0., 0.));
    	molecule.addAtom(atom1);
    	CMLAtom atom2 = new CMLAtom("a2");
    	atom2.setXY2(new Real2(20., 30.));
    	molecule.addAtom(atom2);
    	CMLBond bond = new CMLBond(atom1, atom2);    	
    	molecule.addBond(bond);
    	if (Level.DEBUG.isGreaterOrEqual(LOG.getLevel())) {
    		molecule.debug("CLEAN2");
    	}
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	CMLAtomSet atomSet = moleculeTool.getAtomSet();
    	AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	atomSetTool.clean2D(20., 20);
    	double d = atom1.getDistance2(atom2);
    	Assert.assertEquals("length", 20., d, 0.001);

//    	if (true) return;
    	atom1.setXY2(new Real2(0., 0.));
    	atom2.setXY2(new Real2(20., 30.));
    	CMLAtom atom3 = new CMLAtom("a3");
    	atom3.setXY2(new Real2(30., 20.));
    	molecule.addAtom(atom3);
    	bond = new CMLBond(atom1, atom3);    	
    	molecule.addBond(bond);
    	if (Level.DEBUG.isGreaterOrEqual(LOG.getLevel())) {
    		molecule.debug("CLEAN2");
    	}
    	atomSet = moleculeTool.getAtomSet();
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	atomSetTool.clean2D(20., 20);
    	d = atom1.getDistance2(atom2);
    	Assert.assertEquals("length", 20., d, 0.1);
    	d = atom1.getDistance2(atom3);
    	Assert.assertEquals("length", 20., d, 0.1);
    	d = atom2.getDistance2(atom3);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 0.2);
    }
    
    /**
     * 
     */
    @Test
    public void testClean2Da() {

    	CMLMolecule molecule = new CMLMolecule();
    	CMLAtom atom1 = new CMLAtom("a1");
    	CMLAtom atom2 = new CMLAtom("a2");
    	CMLAtom atom3 = new CMLAtom("a3");
    	CMLAtom atom4 = new CMLAtom("a4");
    	CMLAtom atom5 = new CMLAtom("a5");
    	CMLAtom atom6 = new CMLAtom("a6");
    	molecule.addAtom(atom1);
    	molecule.addAtom(atom2);
    	molecule.addAtom(atom3);
    	molecule.addAtom(atom4);
    	molecule.addAtom(atom5);
    	molecule.addAtom(atom6);
    	atom1.setXY2(new Real2(0.0, 0.0));
    	atom2.setXY2(new Real2(20.0, 0.0));
    	atom3.setXY2(new Real2(30.0, 20.0));
    	atom4.setXY2(new Real2(20.0, 40.0));
    	atom5.setXY2(new Real2(0.0, 40.0));
    	atom6.setXY2(new Real2(-10.0, 20.0));
    	CMLBond bond12 = new CMLBond(atom1, atom2);    	
    	CMLBond bond23 = new CMLBond(atom2, atom3);    	
    	CMLBond bond34 = new CMLBond(atom3, atom4);    	
    	CMLBond bond45 = new CMLBond(atom4, atom5);    	
    	CMLBond bond56 = new CMLBond(atom5, atom6);    	
    	CMLBond bond16 = new CMLBond(atom1, atom6);    	
    	molecule.addBond(bond12);
    	molecule.addBond(bond23);
    	molecule.addBond(bond34);
    	molecule.addBond(bond45);
    	molecule.addBond(bond56);
    	molecule.addBond(bond16);
    	if (Level.DEBUG.isGreaterOrEqual(LOG.getLevel())) {
    		molecule.debug("CLEAN2");
    	}
    	
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	CMLAtomSet atomSet = moleculeTool.getAtomSet();
    	AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	atomSetTool.clean2D(20., 6);
    	double d = atom1.getDistance2(atom2);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	d = atom2.getDistance2(atom3);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	d = atom3.getDistance2(atom4);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	d = atom4.getDistance2(atom5);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	d = atom5.getDistance2(atom6);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	d = atom1.getDistance2(atom6);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20., d, 2);
    	
    	d = atom1.getDistance2(atom3);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	d = atom2.getDistance2(atom4);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	d = atom3.getDistance2(atom5);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	d = atom4.getDistance2(atom6);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	d = atom5.getDistance2(atom1);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	d = atom6.getDistance2(atom2);
    	LOG.debug(d);
    	Assert.assertEquals("length", 20. * Math.sqrt(3.), d, 3);
    	
    	
    }
    

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.getBondSet(CMLAtomSet)'
     */
    @Test
    public void testExtractBondSet() throws Exception {
    	CMLAtomSet atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3"});
    	AtomSetTool atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	CMLBondSet bondSet = atomSetTool.extractBondSet();
    	String bondSetS = "<bondSet size='2' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a2 a3" +
    			"</bondSet>";
    	CMLBondSet bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	JumboTestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);
    	
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='3' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	JumboTestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);

    	// isolated bond
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7", "a5", "a11"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='4' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3 a5 a11" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	JumboTestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);

    	// isolated atom, no bond
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7", "a5", "a11", "a10"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='4' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3 a5 a11" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	JumboTestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);
    }

	@Test
	public void testGetAtomTreeLabelling() {
		CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(dmf.getAtoms());
		AtomMatchObject atomMatchObject = new AtomMatchObject();
		Map<String, CMLAtomSet> map = 
			AtomSetTool.getOrCreateTool(atomSet).createAtomSetByAtomTreeStringAtomTreeLabelling(atomMatchObject);
		System.out.println(".........");
		for (String s : map.keySet()) {
			System.out.println(s+".."+map.get(s).getValue());
		}
		Assert.assertNotNull("atom tree map not null", map);
		Assert.assertEquals("atom tree map size", 4, map.size());
		String[] treeS = new String[] { "O", "C(N(C)(C(O)))", "C(N)(O)", "N", };

		List list = new ArrayList();
		for (String t : treeS) {
			Object obj = map.get(t);
			list.add(obj);
		}
		for (Object obj : list) {
			if (obj instanceof CMLAtom) {
				// LOG.debug("A "+((CMLAtom)obj).getId());
			} else if (obj instanceof CMLAtomSet) {
				// LOG.debug("AS "+Util.concatenate(((CMLAtomSet)obj).
				// getXMLContent(), CMLConstants.S_SLASH));
			}
		}

	}
    
}
