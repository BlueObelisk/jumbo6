package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.test.StringTestBase;

/** test AtomMatcher
 *
 * @author pm286
 *
 */
public class AtomMatcherTest extends AbstractToolTest {

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
        super.setUp();
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
        atomMatcher.setIncludeElementTypes(new String[]{"C", "N"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{"C", "N"}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeElementTypes());
        atomMatcher.setExcludeElementTypes(new String[]{"N", "O"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{"N", "O"}, atomMatcher.getExcludeElementTypes());
        atomMatcher.setIncludeElementTypes(new String[]{});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeElementTypes());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomMatcher.getIncludeLigandElementTypes()'
     */
    @Test
    public void testGetSetIncludeExcludeLigandElementTypes() {
        AtomMatcher atomMatcher = new AtomMatcher();
        atomMatcher.setIncludeLigandElementTypes(new String[]{"C", "N"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{"C", "N"}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{}, atomMatcher.getExcludeLigandElementTypes());
        atomMatcher.setExcludeLigandElementTypes(new String[]{"N", "O"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{}, atomMatcher.getIncludeLigandElementTypes());
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES", new String[]{"N", "O"}, atomMatcher.getExcludeLigandElementTypes());
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
        atomMatcher.setIncludeElementTypes(new String[]{"C", "N"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{"C", "N"},
                atomMatcher.getIncludeElementTypes());
        CMLAtom atomO = mol1.getAtom(3);
        Assert.assertEquals("O", "O", atomO.getElementType());
        Assert.assertTrue("skip O", atomMatcher.skipAtom(atomO));

        atomMatcher.setIncludeElementTypes(new String[]{"C", "O"});
        StringTestBase.assertEquals("INCLUDE_ELEMENT_TYPES ", new String[]{"C", "O"},
                atomMatcher.getIncludeElementTypes());
        Assert.assertFalse("include O", atomMatcher.skipAtom(atomO));

        atomMatcher.setExcludeElementTypes(new String[]{"C", "O"});
        StringTestBase.assertEquals("EXCLUDE_ELEMENT_TYPES ", new String[]{"C", "O"},
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
    @Ignore
    @Test
    public void testCreateMapFrom2DOverlap() {

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
        CMLAtomSet atomSet = new CMLAtomSet(dmf.getAtoms());
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
//            System.out.println("T "+t);
            Object obj = map.get(t);
            list.add(obj);
        }
        for (Object obj : list) {
            if (obj instanceof CMLAtom) {
                System.out.println("A "+((CMLAtom)obj).getId());
            } else if (obj instanceof CMLAtomSet) {
                System.out.println("AS "+Util.concatenate(((CMLAtomSet)obj).getXMLContent(), S_SLASH));
            }
        }
        
        
    }

}
