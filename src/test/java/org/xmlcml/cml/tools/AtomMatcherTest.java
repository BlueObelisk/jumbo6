package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.cml.tools.AtomMatcher.Strategy;
import org.xmlcml.euclid.Real2;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TstUtils;

/**
 * test AtomMatcher
 * 
 * @author pm286
 * 
 */
public class AtomMatcherTest {

	String mol1S = CMLConstants.S_EMPTY + "<molecule id='m1' " + CMLConstants.CML_XMLNS + ">"
			+ "  <atomArray>"
			+ "    <atom id='a1' elementType='N' x2='0.0' y2='0.0'/>"
			+ "    <atom id='a2' elementType='C' x2='0.0' y2='1.0'/>"
			+ "    <atom id='a3' elementType='C' x2='1.0' y2='0.0'/>"
			+ "    <atom id='a4' elementType='O' x2='-1.0' y2='-1.0'/>"
			+ "  </atomArray>" + "  <bondArray>"
			+ "    <bond atomRefs2='a1 a2'/>" + "    <bond atomRefs2='a3 a2'/>"
			+ "    <bond atomRefs2='a4 a2' order='2'/>" + "  </bondArray>"
			+ "</molecule>" + CMLConstants.S_EMPTY;

	String mol2S = CMLConstants.S_EMPTY + "<molecule id='m1' " + CMLConstants.CML_XMLNS + ">"
			+ "  <atomArray>"
			+ "    <atom id='a1' elementType='N' x2='0.0', y2='0.0'/>"
			+ "    <atom id='a2' elementType='C' x2='0.0', y2='1.0'/>"
			+ "    <atom id='a3' elementType='C' x2='1.0', y2='0.0'/>"
			+ "    <atom id='a4' elementType='O' x2='-1.0', y2='-1.0'/>"
			+ "  </atomArray>" + "  <bondArray>"
			+ "    <bond atomRefs2='a1 a2'/>" + "    <bond atomRefs2='a3 a2'/>"
			+ "    <bond atomRefs2='a4 a2' order='2'/>" + "  </bondArray>"
			+ "</molecule>" + CMLConstants.S_EMPTY;

	CMLMolecule mol1;
	CMLMolecule mol2;
	AtomTreeTest att = new AtomTreeTest();

	/**
	 * setup.
	 * 
	 * @exception Exception
	 */
	@Before
	public void setUp() throws Exception {
		att.setUp();
	}

	private void makeMol1() {
		mol1 = (CMLMolecule)TstUtils.parseValidString(mol1S);
	}

	void makeMol2() {
		mol2 = (CMLMolecule)TstUtils.parseValidString(mol2S);
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomMatcher.AtomMatcher()'
	 */
	@Test
	public void testAtomMatcher() {
		AtomMatcher atomMatcher = new AtomTreeMatcher();
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] {}, atomMatcher
				.getIncludeElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] {}, atomMatcher
				.getExcludeElementTypes());
		Assert.assertEquals("INCLUDE_LIGAND_ELEMENT_TYPES", new String[] {},
				atomMatcher.getIncludeLigandElementTypes());
		Assert.assertEquals("EXCLUDE_LIGAND_ELEMENT_TYPES", new String[] {},
				atomMatcher.getExcludeLigandElementTypes());
		Assert.assertEquals("USE_CHARGE", false, atomMatcher.isUseCharge());
		Assert.assertEquals("USE_LABEL", false, atomMatcher.isUseLabel());
		Assert.assertEquals("MAXIMUM_ATOM_TREE_LEVEL", 10, atomMatcher
				.getMaximumAtomTreeLevel());
		Assert.assertEquals("ATOM_TREE_LEVEL", -1, atomMatcher
				.getAtomTreeLevel());
		Assert.assertEquals("ATOM_MATCH_STRATEGY",
				AtomMatcher.Strategy.MATCH_ATOM_TREE_LABEL, atomMatcher
						.getAtomMatchStrategy());
		Assert.assertEquals("ATOM_SET_EXPANSION",
				AtomMatcher.Strategy.MATCH_GEOM, atomMatcher
						.getAtomSetExpansionStrategy());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.AtomMatcher.getIncludeElementTypes()'
	 */
	@Test
	public void testGetSetIncludeExcludeElementTypes() {
		AtomMatcher atomMatcher = new AtomTreeMatcher();
		atomMatcher.setIncludeElementTypes(new String[] { AS.C.value,
				AS.N.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] { AS.C.value,
				AS.N.value }, atomMatcher.getIncludeElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] {}, atomMatcher
				.getExcludeElementTypes());
		atomMatcher.setExcludeElementTypes(new String[] { AS.N.value,
				AS.O.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] {}, atomMatcher
				.getIncludeElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] { AS.N.value,
				AS.O.value }, atomMatcher.getExcludeElementTypes());
		atomMatcher.setIncludeElementTypes(new String[] {});
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] {}, atomMatcher
				.getIncludeElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] {}, atomMatcher
				.getExcludeElementTypes());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.AtomMatcher.getIncludeLigandElementTypes()'
	 */
	@Test
	public void testGetSetIncludeExcludeLigandElementTypes() {
		AtomMatcher atomMatcher = new AtomTreeMatcher();
		atomMatcher.setIncludeLigandElementTypes(new String[] { AS.C.value,
				AS.N.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] { AS.C.value,
				AS.N.value }, atomMatcher.getIncludeLigandElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] {}, atomMatcher
				.getExcludeLigandElementTypes());
		atomMatcher.setExcludeLigandElementTypes(new String[] { AS.N.value,
				AS.O.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] {}, atomMatcher
				.getIncludeLigandElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] { AS.N.value,
				AS.O.value }, atomMatcher.getExcludeLigandElementTypes());
		atomMatcher.setIncludeLigandElementTypes(new String[] {});
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] {}, atomMatcher
				.getIncludeLigandElementTypes());
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[] {}, atomMatcher
				.getExcludeLigandElementTypes());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.AtomMatcher.skipAtom(CMLAtom)'
	 */
	@Test
	public void testSkipAtom() {
		makeMol1();
		AtomMatcher atomMatcher = new AtomTreeMatcher();
		atomMatcher.setIncludeElementTypes(new String[] { AS.C.value,
				AS.N.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] { AS.C.value,
				AS.N.value }, atomMatcher.getIncludeElementTypes());
		CMLAtom atomO = mol1.getAtom(3);
		Assert.assertEquals(AS.O.value, AS.O.value, atomO.getElementType());
		Assert.assertTrue("skip O", atomMatcher.skipAtom(atomO));

		atomMatcher.setIncludeElementTypes(new String[] { AS.C.value,
				AS.O.value });
		Assert.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[] { AS.C.value,
				AS.O.value }, atomMatcher.getIncludeElementTypes());
		Assert.assertFalse("include O", atomMatcher.skipAtom(atomO));

		atomMatcher.setExcludeElementTypes(new String[] { AS.C.value,
				AS.O.value });
		Assert.assertEquals("EXCLUDE_ELEMENT_TYPES ", new String[] { AS.C.value,
				AS.O.value }, atomMatcher.getExcludeElementTypes());
		Assert.assertTrue("exclude O", atomMatcher.skipAtom(atomO));
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.AtomMatcher.createMapFrom2DOverlap(CMLAtomSet,
	 * CMLAtomSet)'
	 */
	@Test
	public void testCreateMapFrom2DOverlap() {
		String s1 = "<molecule "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "<atomArray>"
				+ "<atom id='a18' elementType='C' x2='23.182952807534104' y2='35.36516011351481'/>"
				+ "<atom id='a20' elementType='C' x2='24.93099499223684' y2='36.37439437349255'/>"
				+ "<atom id='a22' elementType='C' x2='26.679053722848494' y2='35.36516011351481'/>"
				+ "<atom id='a24' elementType='C' x2='28.42709590755123' y2='36.37439437349255'/>"
				+ "<atom id='a26' elementType='C' x2='30.175138092253967' y2='35.36516011351481'/>"
				+ "<atom id='a28' elementType='C' x2='31.923196822865616' y2='36.37439437349255'/>"
				+ "<atom id='a30' elementType='O' hydrogenCount='0' x2='31.923196822865616' y2='38.392862893448026'/>"
				+ "<atom id='a73' elementType='O' hydrogenCount='0' x2='33.664786103092794' y2='36.37439437349255'/>"
				+ "<atom id='a74' elementType='C' x2='37.146773358105506' y2='36.37439437349255'/>"
				+ "<atom id='a75' elementType='C' x2='35.40577973059915' y2='35.36923040710709'/>"
				+ "</atomArray>" + "<bondArray>"
				+ "<bond atomRefs2='a18 a20' id='a18_a20' order='1'/>"
				+ "<bond atomRefs2='a20 a22' id='a20_a22' order='2'/>"
				+ "<bond atomRefs2='a22 a24' id='a22_a24' order='1'/>"
				+ "<bond atomRefs2='a24 a26' id='a24_a26' order='2'/>"
				+ "<bond atomRefs2='a26 a28' id='a26_a28' order='1'/>"
				+ "<bond atomRefs2='a28 a30' id='a28_a30' order='2'/>"
				+ "<bond atomRefs2='a28 a73' order='1' id='a28_a73'/>"
				+ "<bond atomRefs2='a73 a75' id='a73_a75' order='1'/>"
				+ "<bond atomRefs2='a74 a75' id='a74_a75' order='1'/>"
				+ "</bondArray>" + "</molecule>";

		String s2 = "<molecule "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "<atomArray>"
				+ "<atom id='a38' elementType='C' x2='52.882859304026375' y2='35.19970102439786'/>"
				+ "<atom id='a39' elementType='C' x2='54.63090148872911' y2='36.2089352843756'/>"
				+ "<atom id='a40' elementType='C' x2='56.378960219340755' y2='35.19970102439786'/>"
				+ "<atom id='a41' elementType='C' x2='58.127002404043495' y2='36.2089352843756'/>"
				+ "<atom id='a42' elementType='C' x2='59.875044588746235' y2='35.19970102439786'/>"
				+ "<atom id='a43' elementType='C' x2='61.62310331935788' y2='36.2089352843756'/>"
				+ "<atom id='a44' elementType='O' hydrogenCount='0' x2='61.62310331935788' y2='38.22740380433108'/>"
				+ "<atom id='a47' elementType='O' hydrogenCount='0' x2='63.36469259958506' y2='36.2089352843756'/>"
				+ "<atom id='a48' elementType='C' x2='66.84667985459777' y2='36.2089352843756'/>"
				+ "<atom id='a49' elementType='C' x2='65.10568622709141' y2='35.20377131799014'/>"
				+ "<atom id='a61' elementType='O' hydrogenCount='0' x2='56.378960219340755' y2='37.218169544353344'/>"
				+ "</atomArray>" + "<bondArray>"
				+ "<bond atomRefs2='a38 a39' id='a38_a39' order='1'/>"
				+ "<bond atomRefs2='a39 a40' id='a39_a40' order='1'/>"
				+ "<bond atomRefs2='a40 a41' id='a40_a41' order='1'/>"
				+ "<bond atomRefs2='a41 a42' id='a41_a42' order='2'/>"
				+ "<bond atomRefs2='a42 a43' id='a42_a43' order='1'/>"
				+ "<bond atomRefs2='a43 a44' id='a43_a44' order='2'/>"
				+ "<bond atomRefs2='a47 a49' id='a47_a49' order='1'/>"
				+ "<bond atomRefs2='a48 a49' id='a48_a49' order='1'/>"
				+ "<bond atomRefs2='a39 a61' id='a39_a61' order='1'/>"
				+ "<bond atomRefs2='a40 a61' id='a40_a61' order='1'/>"
				+ "<bond atomRefs2='a43 a47' order='1' id='a43_a47'/>"
				+ "</bondArray>" + "</molecule>";

		CMLMolecule mol1 = (CMLMolecule)TstUtils.parseValidString(s1);
		CMLAtomSet atomSet1 = MoleculeTool.getOrCreateTool(mol1).getAtomSet();
		CMLMolecule mol2 = (CMLMolecule)TstUtils.parseValidString(s2);
		CMLAtomSet atomSet2 = MoleculeTool.getOrCreateTool(mol2).getAtomSet();
		AtomMatcher atomMatcher = new AtomMatcher2D();
		atomMatcher.setAtomMatchStrategy(Strategy.MATCH_DISTANCE_MATRIX);
		CMLMap map12 = atomMatcher.match(atomSet1, atomSet2, "distanceMatrix");
		Real2 expected = new Real2(-29.69990649649227, 0.1654590891169505);
		for (CMLLink link : map12.getLinkElements()) {
			Real2 xy1 = mol1.getAtomById(link.getFrom()).getXY2();
			Real2 xy2 = mol2.getAtomById(link.getTo()).getXY2();
			Real2 found = xy1.subtract(xy2);
			CMLAssert.assertEquals("delta", expected, found, 0.000001);
		}
	}

    /**
     * 
     */
    @Test
    public void testMatchesCMLAtomCMLAtom() {
    	AtomMatcher atomMatcher = new AtomTreeMatcher();
    	CMLAtom atom1 = null;
    	CMLAtom atom2 = null;
    	Assert.assertFalse("null does not match", atomMatcher.matches(atom1, atom2));
    	atom1 = new CMLAtom();
    	atom2 = new CMLAtom();
    	atom1.setElementType("O");
    	atom2.setElementType("C");
    	Assert.assertFalse("does not match", atomMatcher.matches(atom1, atom2));
    	atom2.setElementType("O");
    	Assert.assertTrue("match", atomMatcher.matches(atom1, atom2));
    }


}
