package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.AtomMatcher.Strategy;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.molutil.ChemicalElement.AS;

/** test AtomMatcher
 *
 * @author pm286
 *
 */
public class AtomMatcherTest {

    String mol1S = S_EMPTY +
        "<molecule id='m1' "+CML_XMLNS+">" +
        "  <atomArray>" +
        "    <atom id='a1' elementType='N' x2='0.0' y2='0.0'/>"+
        "    <atom id='a2' elementType='C' x2='0.0' y2='1.0'/>"+
        "    <atom id='a3' elementType='C' x2='1.0' y2='0.0'/>"+
        "    <atom id='a4' elementType='O' x2='-1.0' y2='-1.0'/>"+
        "  </atomArray>" +
        "  <bondArray>" +
        "    <bond atomRefs2='a1 a2'/>"+
        "    <bond atomRefs2='a3 a2'/>"+
        "    <bond atomRefs2='a4 a2' order='2'/>"+
        "  </bondArray>" +
        "</molecule>" +
        S_EMPTY;

    String mol2S = S_EMPTY +
    "<molecule id='m1' "+CML_XMLNS+">" +
    "  <atomArray>" +
    "    <atom id='a1' elementType='N' x2='0.0', y2='0.0'/>"+
    "    <atom id='a2' elementType='C' x2='0.0', y2='1.0'/>"+
    "    <atom id='a3' elementType='C' x2='1.0', y2='0.0'/>"+
    "    <atom id='a4' elementType='O' x2='-1.0', y2='-1.0'/>"+
    "  </atomArray>" +
    "  <bondArray>" +
    "    <bond atomRefs2='a1 a2'/>"+
    "    <bond atomRefs2='a3 a2'/>"+
    "    <bond atomRefs2='a4 a2' order='2'/>"+
    "  </bondArray>" +
    "</molecule>" +
    S_EMPTY;

    CMLMolecule mol1;
    CMLMolecule mol2;
    AtomTreeTest att = new AtomTreeTest();
    CMLMolecule dmf;
    
    /** setup.
     *@exception Exception
     */
    @Before
    public void setUp() throws Exception {
        att.setUp();
        dmf = att.dmf;
    }

    private void makeMol1() {
        mol1 = (CMLMolecule) parseValidString(mol1S);
    }
    void makeMol2() {
        mol2 = (CMLMolecule) parseValidString(mol2S);
    }
    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.AtomMatcher()'
     */
    @Test
    public void testAtomMatcher() {
        AtomMatcher atomMatcher = new AtomMatcher();
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeElementTypes());
        StringTestBase.assertEquals("INCLUDE_LIGAND_ELEMENT_TYPES", new String[]{}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_LIGAND_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeLigandElementTypes());
        Assert.assertEquals("USE_CHARGE", false, atomMatcher.isUseCharge());
        Assert.assertEquals("USE_LABEL", false, atomMatcher.isUseLabel());
        Assert.assertEquals("MAXIMUM_ATOM_TREE_LEVEL", 10, atomMatcher.getMaximumAtomTreeLevel());
        Assert.assertEquals("ATOM_TREE_LEVEL", -1, atomMatcher.getAtomTreeLevel());
        Assert.assertEquals("ATOM_MATCH_STRATEGY", AtomMatcher.Strategy.MATCH_ATOM_TREE_LABEL, atomMatcher.getAtomMatchStrategy());
        Assert.assertEquals("ATOM_SET_EXPANSION", AtomMatcher.Strategy.MATCH_GEOM, atomMatcher.getAtomSetExpansionStrategy());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getIncludeElementTypes()'
     */
    @Test
    public void testGetSetIncludeExcludeElementTypes() {
        AtomMatcher atomMatcher = new AtomMatcher();
        atomMatcher.setIncludeElementTypes(new String[]{AS.C.value, AS.N.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{AS.C.value, AS.N.value}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeElementTypes());
        atomMatcher.setExcludeElementTypes(new String[]{AS.N.value, AS.O.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{AS.N.value, AS.O.value}, atomMatcher.getExcludeElementTypes());
        atomMatcher.setIncludeElementTypes(new String[]{});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeElementTypes());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getIncludeLigandElementTypes()'
     */
    @Test
    public void testGetSetIncludeExcludeLigandElementTypes() {
        AtomMatcher atomMatcher = new AtomMatcher();
        atomMatcher.setIncludeLigandElementTypes(new String[]{AS.C.value, AS.N.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{AS.C.value, AS.N.value}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeLigandElementTypes());
        atomMatcher.setExcludeLigandElementTypes(new String[]{AS.N.value, AS.O.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{AS.N.value, AS.O.value}, atomMatcher.getExcludeLigandElementTypes());
        atomMatcher.setIncludeLigandElementTypes(new String[]{});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeLigandElementTypes());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.skipAtom(CMLAtom)'
     */
    @Test
    public void testSkipAtom() {
        makeMol1();
        AtomMatcher atomMatcher = new AtomMatcher();
        atomMatcher.setIncludeElementTypes(new String[]{AS.C.value, AS.N.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{AS.C.value, AS.N.value},
                atomMatcher.getIncludeElementTypes());
        CMLAtom atomO = mol1.getAtom(3);
        Assert.assertEquals(AS.O.value, AS.O.value, atomO.getElementType());
        Assert.assertTrue("skip O", atomMatcher.skipAtom(atomO));

        atomMatcher.setIncludeElementTypes(new String[]{AS.C.value, AS.O.value});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{AS.C.value, AS.O.value},
                atomMatcher.getIncludeElementTypes());
        Assert.assertFalse("include O", atomMatcher.skipAtom(atomO));

        atomMatcher.setExcludeElementTypes(new String[]{AS.C.value, AS.O.value});
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES ", new String[]{AS.C.value, AS.O.value},
                atomMatcher.getExcludeElementTypes());
        Assert.assertTrue("exclude O", atomMatcher.skipAtom(atomO));
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.skipLigandAtom(CMLAtom)'
     */
    @Ignore
    @Test
    public void testSkipLigandAtom() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.mapAtomSets(CMLAtomSet, CMLAtomSet)'
     */
    @Ignore
    @Test
    public void testMapAtomSets() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.mapMolecules(CMLMolecule, CMLMolecule)'
     */
    @Ignore
    @Test
    public void testMapMolecules() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getMoleculeMatch(List<CMLMolecule>, List<CMLMolecule>)'
     */
    @Ignore
    @Test
    public void testGetMoleculeMatch() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.createMapFrom2DOverlap(CMLAtomSet, CMLAtomSet)'
     */
    @Test
    public void testCreateMapFrom2DOverlap() {
    	String s1 =
   	 "<molecule "+CML_XMLNS+">"+
	  "<atomArray>"+
	   "<atom id='a18' elementType='C' x2='23.182952807534104' y2='35.36516011351481'/>"+
	   "<atom id='a20' elementType='C' x2='24.93099499223684' y2='36.37439437349255'/>"+
	   "<atom id='a22' elementType='C' x2='26.679053722848494' y2='35.36516011351481'/>"+
	   "<atom id='a24' elementType='C' x2='28.42709590755123' y2='36.37439437349255'/>"+
	   "<atom id='a26' elementType='C' x2='30.175138092253967' y2='35.36516011351481'/>"+
	   "<atom id='a28' elementType='C' x2='31.923196822865616' y2='36.37439437349255'/>"+
	   "<atom id='a30' elementType='O' hydrogenCount='0' x2='31.923196822865616' y2='38.392862893448026'/>"+
	   "<atom id='a73' elementType='O' hydrogenCount='0' x2='33.664786103092794' y2='36.37439437349255'/>"+
	   "<atom id='a74' elementType='C' x2='37.146773358105506' y2='36.37439437349255'/>"+
	   "<atom id='a75' elementType='C' x2='35.40577973059915' y2='35.36923040710709'/>"+
	  "</atomArray>"+
	  "<bondArray>"+
	   "<bond atomRefs2='a18 a20' id='a18_a20' order='1'/>"+
	   "<bond atomRefs2='a20 a22' id='a20_a22' order='2'/>"+
	   "<bond atomRefs2='a22 a24' id='a22_a24' order='1'/>"+
	   "<bond atomRefs2='a24 a26' id='a24_a26' order='2'/>"+
	   "<bond atomRefs2='a26 a28' id='a26_a28' order='1'/>"+
	   "<bond atomRefs2='a28 a30' id='a28_a30' order='2'/>"+
	   "<bond atomRefs2='a28 a73' order='1' id='a28_a73'/>"+
	   "<bond atomRefs2='a73 a75' id='a73_a75' order='1'/>"+
	   "<bond atomRefs2='a74 a75' id='a74_a75' order='1'/>"+
	  "</bondArray>"+
	 "</molecule>";
	
    	String s2 = 
	 "<molecule "+CML_XMLNS+">"+
	  "<atomArray>"+
	   "<atom id='a38' elementType='C' x2='52.882859304026375' y2='35.19970102439786'/>"+
	   "<atom id='a39' elementType='C' x2='54.63090148872911' y2='36.2089352843756'/>"+
	   "<atom id='a40' elementType='C' x2='56.378960219340755' y2='35.19970102439786'/>"+
	   "<atom id='a41' elementType='C' x2='58.127002404043495' y2='36.2089352843756'/>"+
	   "<atom id='a42' elementType='C' x2='59.875044588746235' y2='35.19970102439786'/>"+
	   "<atom id='a43' elementType='C' x2='61.62310331935788' y2='36.2089352843756'/>"+
	   "<atom id='a44' elementType='O' hydrogenCount='0' x2='61.62310331935788' y2='38.22740380433108'/>"+
	   "<atom id='a47' elementType='O' hydrogenCount='0' x2='63.36469259958506' y2='36.2089352843756'/>"+
	   "<atom id='a48' elementType='C' x2='66.84667985459777' y2='36.2089352843756'/>"+
	   "<atom id='a49' elementType='C' x2='65.10568622709141' y2='35.20377131799014'/>"+
	   "<atom id='a61' elementType='O' hydrogenCount='0' x2='56.378960219340755' y2='37.218169544353344'/>"+
	  "</atomArray>"+
	  "<bondArray>"+
	   "<bond atomRefs2='a38 a39' id='a38_a39' order='1'/>"+
	   "<bond atomRefs2='a39 a40' id='a39_a40' order='1'/>"+
	   "<bond atomRefs2='a40 a41' id='a40_a41' order='1'/>"+
	   "<bond atomRefs2='a41 a42' id='a41_a42' order='2'/>"+
	   "<bond atomRefs2='a42 a43' id='a42_a43' order='1'/>"+
	   "<bond atomRefs2='a43 a44' id='a43_a44' order='2'/>"+
	   "<bond atomRefs2='a47 a49' id='a47_a49' order='1'/>"+
	   "<bond atomRefs2='a48 a49' id='a48_a49' order='1'/>"+
	   "<bond atomRefs2='a39 a61' id='a39_a61' order='1'/>"+
	   "<bond atomRefs2='a40 a61' id='a40_a61' order='1'/>"+
	   "<bond atomRefs2='a43 a47' order='1' id='a43_a47'/>"+
	  "</bondArray>"+
	 "</molecule>";
    	
    	CMLMolecule mol1 = (CMLMolecule) parseValidString(s1);
    	CMLAtomSet atomSet1 = mol1.getAtomSet();
    	CMLMolecule mol2 = (CMLMolecule) parseValidString(s2);
    	CMLAtomSet atomSet2 = mol2.getAtomSet();
    	AtomMatcher atomMatcher = new AtomMatcher();
    	atomMatcher.setAtomMatchStrategy(Strategy.MATCH_DISTANCE_MATRIX);
        CMLMap map12 = atomMatcher.createMapFrom2DOverlap(atomSet1, atomSet2);
        map12.debug();
    	for (CMLLink link : map12.getLinkElements()) {
    		Real2 xy1 = mol1.getAtomById(link.getFrom()).getXY2();
    		Real2 xy2 = mol2.getAtomById(link.getTo()).getXY2();
    		System.out.println(xy1.subtract(xy2));
    	}
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getAtomsWithSameMappedNeighbours(CMLAtomSet, CMLAtomSet, CMLMap)'
     */
    @Ignore
    @Test
    public void testGetAtomsWithSameMappedNeighbours() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getAtomsWithSameMappedNeighbours00(CMLAtomSet, CMLAtomSet, CMLMap)'
     */
    @Ignore
    @Test
    public void testGetAtomsWithSameMappedNeighbours00() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.mapGeometricalNeighbours(CMLAtomSet, CMLAtomSet)'
     */
    @Ignore
    @Test
    public void testMapGeometricalNeighbours() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.splitAndProcessAtomSets(CMLMap, CMLAtomSet, CMLAtomSet)'
     */
    @Ignore
    @Test
    public void testSplitAndProcessAtomSetsCMLMapCMLAtomSetCMLAtomSet() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.overlap2D(CMLAtomSet, CMLAtomSet)'
     */
    @Ignore
    @Test
    public void testOverlap2DCMLAtomSetCMLAtomSet() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.splitAndProcessAtomSets(CMLMap, CMLMolecule, CMLMolecule)'
     */
    @Ignore
    @Test
    public void testSplitAndProcessAtomSetsCMLMapCMLMoleculeCMLMolecule() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.overlap2D(CMLMolecule, CMLMolecule)'
     */
    @Ignore
    @Test
    public void testOverlap2DCMLMoleculeCMLMolecule() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.matchProductsToNextReactants(CMLReaction, CMLReaction)'
     */
    @Ignore
    @Test
    public void testMatchProductsToNextReactants() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.translateProductsToReactants(CMLReaction)'
     */
    @Ignore
    @Test
    public void testTranslateProductsToReactants() {

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.moveReactantProductToSpectator(CMLReaction)'
     */
    @Ignore
    @Test
    public void testMoveReactantProductToSpectator() {

    }


    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getUniqueMatchedAtoms(CMLAtomSet, CMLAtomSet, AtomMatcher)'
     */
    @Test
    public void testGetUniqueMatchedAtomsCMLAtomSetCMLAtomSetAtomMatcher() {
//        CMLMap map = getUniqueMatchedAtoms(atomSet, targetAtomSet,
//                atomMatcher) throws CMLException {
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.getAtomTreeLabelling(CMLAtomSet, AtomMatcher)'
     */
    @SuppressWarnings("all")
    @Test
    public void testGetAtomTreeLabelling() {
        CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(dmf.getAtoms());
        Map<String, Object> map = new AtomMatcher().getAtomTreeLabelling(atomSet);
        Assert.assertNotNull("atom tree map not null", map);
        Assert.assertEquals("atom tree map size", 4, map.size());
        String[] treeS = new String[]{
            "O",
            "C(N(C)(C(O)))",
            "C(N)(O)",
            "N",
        };

        List list = new ArrayList();
        for (String t : treeS) {
            Object obj = map.get(t);
            list.add(obj);
        }
        for (Object obj : list) {
            if (obj instanceof CMLAtom) {
//                System.out.println("A "+((CMLAtom)obj).getId());
            } else if (obj instanceof CMLAtomSet) {
//                System.out.println("AS "+Util.concatenate(((CMLAtomSet)obj).getXMLContent(), S_SLASH));
            }
        }
        
        
    }

}
