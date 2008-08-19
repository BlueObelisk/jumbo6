package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.util.TestUtils;

/**
 * test AtomSetTool.
 *
 * @author pm286
 *
 */
public class AtomSetToolTest {

    String sproutS = S_EMPTY +
    "<molecule " + CML_XMLNS + " title='sprout'>"
    + "  <atomArray>"
    + "    <atom id='a1' elementType='C'/>"
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
    + "    <atom id='a13' elementType='O'/>" +
      "   </atomArray>" +
      "   <bondArray>" +
      "     <bond id='a1 a2' atomRefs2='a1 a2'/>" +
      "     <bond id='a2 a3' atomRefs2='a2 a3'/>" +
      "     <bond id='a3 a4' atomRefs2='a3 a4'/>" +
      "     <bond id='a4 a5' atomRefs2='a4 a5'/>" +
      "     <bond id='a5 a6' atomRefs2='a5 a6'/>" +
      "     <bond id='a1 a6' atomRefs2='a1 a6'/>" +
      "     <bond id='a1 a7' atomRefs2='a1 a7'/>" +
      "     <bond id='a2 a8' atomRefs2='a2 a8'/>" +
      "     <bond id='a3 a9' atomRefs2='a3 a9'/>" +
      "     <bond id='a4 a10' atomRefs2='a4 a10'/>" +
      "     <bond id='a5 a11' atomRefs2='a5 a11'/>" +
      "     <bond id='a6 a12' atomRefs2='a6 a12'/>" +
      "     <bond id='a12 a13' atomRefs2='a12 a13'/>" +
      "  </bondArray>" +
      "</molecule>" +
      S_EMPTY;
    CMLMolecule sprout = null;

    /**
     * setup.
     *
     * @exception Exception
     */
    @Before
    public void setUp() {
        sprout = (CMLMolecule) parseValidString(sproutS);
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
                    new StringReader("<molecule " + CML_XMLNS + ">"
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
                                            + CML_XMLNS
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

        AtomSetTool atomSetTool1 = AtomSetTool.getOrCreateTool(mol1.getAtomSet());
        CMLAtomSet atomSet = atomSetTool1.getOverlapping3DAtoms(mol2
                .getAtomSet(), CoordinateType.CARTESIAN);
        Assert.assertEquals("overlap", new String[]{"a1", "a2"}, atomSet
                .getXMLContent());
        
        atomSet = atomSetTool1.getOverlapping3DAtoms(mol2.getAtomSet(),
                CoordinateType.FRACTIONAL);
        Assert.assertEquals("overlap", 0, atomSet.size());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.AtomSetTool(CMLAtomSet)'
     */
    @Test
    @Ignore
    public void testAtomSetTool() {
        // TODO Auto-generated method stub

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
    	TestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);
    	
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='3' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	TestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);

    	// isolated bond
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7", "a5", "a11"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='4' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3 a5 a11" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	TestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);

    	// isolated atom, no bond
    	atomSet = new CMLAtomSet(
    			sprout, new String[]{"a1", "a2", "a3", "a7", "a5", "a11", "a10"});
    	atomSetTool = AtomSetTool.getOrCreateTool(atomSet);
    	bondSet = atomSetTool.extractBondSet();
    	bondSetS = "<bondSet size='4' xmlns='http://www.xml-cml.org/schema'>" +
    			"a1 a2 a1 a7 a2 a3 a5 a11" +
    			"</bondSet>";
    	bondSetRef = (CMLBondSet) new CMLBuilder().parseString(bondSetS);
    	TestUtils.assertEqualsCanonically("bondSet", bondSetRef, bondSet, true);
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.createValenceAngles(CMLAtomSet, boolean, boolean)'
     */
    @Test
    @Ignore
    public void testCreateValenceAngles() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.createValenceTorsions(CMLAtomSet, boolean, boolean)'
     */
    @Test
    @Ignore
    public void testCreateValenceTorsions() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.sprout()'
     */
    @Test
    public void testSprout() {

        List<CMLAtomSet> ringNucleiAtomSets =
            new ConnectionTableTool(sprout).getRingNucleiAtomSets();
        Assert.assertEquals("sprout size", 1, ringNucleiAtomSets.size());
        CMLAtomSet sproutAtomSet = ringNucleiAtomSets.get(0);
        Assert.assertEquals("pre sprout size", 6, sproutAtomSet.size());
        AtomSetTool sproutAtomSetTool = AtomSetTool.getOrCreateTool(sproutAtomSet);
        CMLAtomSet sproutAtomSprout = sproutAtomSetTool.sprout();
        Assert.assertEquals("sprout size", 12, sproutAtomSprout.size());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.areOrderedAtomSetsDifferent(CMLAtomSet[])'
     */
    @Test
    @Ignore
    public void testAreOrderedAtomSetsDifferent() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.createAtomSet(List<CMLMolecule>)'
     */
    @Test
    @Ignore
    public void testCreateAtomSet() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.resetParents()'
     */
    @Test
    @Ignore
    public void testResetParents() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.setParent(CMLAtom, CMLAtom)'
     */
    @Test
    @Ignore
    public void testSetParent() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.getParent(CMLAtom)'
     */
    @Test
    @Ignore
    public void testGetParent() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.getNearestAtom2OfSameElementType(CMLAtom)'
     */
    @Test
    @Ignore
    public void testGetNearestAtom2OfSameElementType() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.removeUnmatchedAtoms(CMLMap, CMLAtomSet, String)'
     */
    @Test
    @Ignore
    public void testRemoveUnmatchedAtoms() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.getOverlapping3DAtoms(CMLAtomSet, CoordinateType)'
     */
    @Test
    @Ignore
    public void testGetOverlapping3DAtoms() {
        // TODO Auto-generated method stub

    }

    /** Test method for 'org.xmlcml.cml.tools.AtomSetTool.matchNearestAtoms(CMLAtomSet, double)'
     */
    @Test
    @Ignore
    public void testMatchNearestAtoms() {
        // TODO Auto-generated method stub

    }



}
