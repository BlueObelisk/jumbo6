package org.xmlcml.cml.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.neverThrow;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;

/** test ConnectionTableTool.
 * 
 * @author pm286
 *
 */
public class ConnectionTableToolTest  {

	static String MOLECULES = "org"+U_S+"xmlcml"+U_S+"cml"+U_S+"tools"+U_S+"examples"+U_S+"molecules";
    String ringMolS = S_EMPTY +
            "  <molecule id='m1' "+CML_XMLNS+">" +
            "    <atomArray>" +
            "      <atom id='a1' elementType='C'/>" +
            "      <atom id='a2' elementType='C'/>" +
            "      <atom id='a3' elementType='C'/>" +
            "      <atom id='a4' elementType='C'/>" +
            "      <atom id='a5' elementType='C'/>" +
            "      <atom id='a6' elementType='C'/>" +
            "      <atom id='a7' elementType='C'/>" +
            "      <atom id='a8' elementType='C'/>" +
            "      <atom id='a9' elementType='C'/>" +
            "      <atom id='a10' elementType='C'/>" +
            "      <atom id='a11' elementType='C'/>" +
            "    </atomArray>" +
            "    <bondArray>" +
            "      <bond id='b12' atomRefs2='a1 a2'/>" +
            "      <bond id='b14' atomRefs2='a1 a4'/>" +
            "      <bond id='b23' atomRefs2='a2 a3'/>" +
            "      <bond id='b34' atomRefs2='a3 a4'/>" +
            "      <bond id='b15' atomRefs2='a1 a5'/>" +
            "      <bond id='b56' atomRefs2='a5 a6'/>" +
            "      <bond id='b57' atomRefs2='a5 a7'/>" +
            "      <bond id='b67' atomRefs2='a6 a7'/>" +
            "      <bond id='b69' atomRefs2='a6 a9'/>" +
            "      <bond id='b78' atomRefs2='a7 a8'/>" +
            "      <bond id='b89' atomRefs2='a8 a9'/>" +
            "      <bond id='b910' atomRefs2='a9 a10' order='2'/>" +
            "      <bond id='b1011' atomRefs2='a10 a11'/>" +
            "    </bondArray>" +
            "  </molecule>" +
            S_EMPTY;

    CMLMolecule ringMol = null;
    ConnectionTableTool ringMolTool = null;

    String multiMolS = S_EMPTY +
    "  <molecule id='m2' "+CML_XMLNS+">" +
    "    <atomArray>" +
    "      <atom id='a1' elementType='C'/>" +
    "      <atom id='a2' elementType='C'/>" +
    "      <atom id='a3' elementType='C'/>" +
    "      <atom id='a4' elementType='C'/>" +
    "      <atom id='a5' elementType='C'/>" +
    "      <atom id='a6' elementType='C'/>" +
    "      <atom id='a7' elementType='C'/>" +
    "      <atom id='a8' elementType='C'/>" +
    "      <atom id='a9' elementType='C'/>" +
    "      <atom id='a10' elementType='C'/>" +
    "      <atom id='a11' elementType='C'/>" +
    "    </atomArray>" +
    "    <bondArray>" +
    "      <bond id='b12' atomRefs2='a1 a2'/>" +
    "      <bond id='b14' atomRefs2='a1 a4'/>" +
    "      <bond id='b23' atomRefs2='a2 a3'/>" +
    "      <bond id='b34' atomRefs2='a3 a4'/>" +
    "      <bond id='b15' atomRefs2='a1 a5'/>" +
    "      <bond id='b67' atomRefs2='a6 a7'/>" +
    "      <bond id='b69' atomRefs2='a6 a9'/>" +
    "      <bond id='b78' atomRefs2='a7 a8'/>" +
    "      <bond id='b89' atomRefs2='a8 a9'/>" +
    "      <bond id='b910' atomRefs2='a9 a10' order='2'/>" +
    "      <bond id='b1011' atomRefs2='a10 a11'/>" +
    "    </bondArray>" +
    "  </molecule>" +
    S_EMPTY;

    CMLMolecule multiMol = null;
    ConnectionTableTool multiMolTool = null;
    
    String simpleMolS =
    "  <molecule id='m2' "+CML_XMLNS+">" +
    "    <atomArray>" +
    "      <atom id='a91' elementType='N'/>" +
    "      <atom id='a92' elementType='O'/>" +
    "      <atom id='a93' elementType='S'/>" +
    "    </atomArray>" +
    "    <bondArray>" +
    "      <bond id='b92' atomRefs2='a91 a92'/>" +
    "      <bond id='b93' atomRefs2='a92 a93'/>" +
    "    </bondArray>" +
    "  </molecule>" +
    S_EMPTY;
 
    /** setup
     * @exception Exception
     */
    @Before
    public void setUp() throws Exception {
        ringMol = makeMol(ringMolS);
        ringMolTool = new ConnectionTableTool(ringMol);
    }
    
    private CMLMolecule makeMol(String molS) {
        CMLMolecule mol = (CMLMolecule) parseValidString(molS);
        return mol;
    }
    
    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#ConnectionTableTool(org.xmlcml.cml.element.CMLMolecule)}.
     */
    @Test
    public final void testConnectionTableTool() {
        ;// tested in setUp()
    }


    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getNucleusWithLigands(String[])'
     */
    @Test
    public void testGetNucleusWithLigands() {
        String[] atomIds = new String[]{"a1"};
        CMLAtomSet atomSet = null;
        try {
            atomSet = ringMolTool.getNextCoordinationSphere(atomIds);
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLAtomSet atomSet1 = new CMLAtomSet(ringMol, new String[]{"a1", "a4", "a5", "a2"});
        Assert.assertTrue("atomSet should have equal content", atomSet.hasContentEqualTo(atomSet1));
        
        atomIds = new String[]{"a3"};
        try {
            atomSet = ringMolTool.getNextCoordinationSphere(atomIds);
        } catch (CMLException e) {
            neverThrow(e);
        }
        atomSet1 = new CMLAtomSet(ringMol, new String[]{"a3", "a4", "a2"});
        Assert.assertTrue("atomSet ", atomSet.hasContentEqualTo(atomSet1));
        
        atomIds = new String[]{"a5", "a6", "a7", "a8", "a9"};
        try {
            atomSet = ringMolTool.getNextCoordinationSphere(atomIds);
        } catch (CMLException e) {
            neverThrow(e);
        }
        atomSet1 = new CMLAtomSet(ringMol, new String[]{
                "a1", "a5", "a6", "a7", "a8", "a9", "a10"});
        Assert.assertTrue("atomSet ", atomSet.hasContentEqualTo(atomSet1));
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getRingNucleiAtomSets()'
     */
    @Test
    public void testGetRingNucleiAtomSets() {
        List<CMLAtomSet> ringNuclei = null;
        ringNuclei = ringMolTool.getRingNucleiAtomSets();
        Assert.assertEquals("ring nuclei count", 2, ringNuclei.size());
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getRingNucleiBondSets()'
     */
    @Test
    public void testGetRingNucleiBondSets() {
        List<CMLBondSet> ringNuclei = null;
        ringNuclei = ringMolTool.getRingNucleiBondSets();
        Assert.assertEquals("ring nuclei count", 2, ringNuclei.size());
        CMLBondSet bondSet0 = new CMLBondSet(ringMol, new String[]{"b34", "b23", "b12", "b14"});
        
        Assert.assertTrue("cyclicBondSet 0", 
            ringNuclei.get(0).hasContentEqualTo(bondSet0));
        CMLBondSet bondSet1 = new CMLBondSet(ringMol, new String[]{"b67", "b56", "b57", "b89", "b78", "b69"});
        Assert.assertTrue("cyclicBondSet 1", 
            ringNuclei.get(1).hasContentEqualTo(bondSet1));
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getRingNucleiMolecules()'
     */
    @Test
    public void testGetRingNucleiMolecules() {
        List<CMLMolecule> ringMoleculeList = null;
        ringMoleculeList = ringMolTool.getRingNucleiMolecules();
        Assert.assertEquals("molecule count", 2, ringMoleculeList.size());
        Assert.assertEquals("molecule 0 atom count", 4, ringMoleculeList.get(0).getAtomCount());
        Assert.assertEquals("molecule 0 bond count", 4, ringMoleculeList.get(0).getBondCount());
        Assert.assertEquals("molecule 1 atom count", 5, ringMoleculeList.get(1).getAtomCount());
        Assert.assertEquals("molecule 1 bond count", 6, ringMoleculeList.get(1).getBondCount());
        
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getCyclicBonds()'
     */
    @Test
    public void testGetCyclicBonds() {
        
        List<CMLBond> cyclicBondList = null;
        
        cyclicBondList = ringMolTool.getCyclicBonds();
        Assert.assertEquals("cyclic bond count", 10, cyclicBondList.size());
        CMLBondSet bondSet = null;
        bondSet = new CMLBondSet(cyclicBondList);
        CMLBondSet bondSet1 = new CMLBondSet(ringMolTool.getMolecule(), 
            new String[]{"b12", "b23", "b34", "b14", "b56", "b57", "b67", "b78", "b69", "b89"});
        Assert.assertTrue("bond set bonds ", bondSet.hasContentEqualTo(bondSet1));
        
        String triangleMolS =
            "  <molecule id='m1' "+CML_XMLNS+">" +
            "    <atomArray>" +
            "      <atom id='a1' elementType='C'/>" +
            "      <atom id='a2' elementType='N'/>" +
            "      <atom id='a3' elementType='O'/>" +
            "      <atom id='a4' elementType='S'/>" +
            "    </atomArray>" +
            "    <bondArray>" +
            "      <bond id='b12' atomRefs2='a1 a2'/>" +
            "      <bond id='b13' atomRefs2='a1 a3'/>" +
            "      <bond id='b14' atomRefs2='a1 a4'/>" +
            "      <bond id='b23' atomRefs2='a2 a3'/>" +
            "      <bond id='b24' atomRefs2='a2 a4'/>" +
            "      <bond id='b34' atomRefs2='a3 a4'/>" +
            "    </bondArray>" +
            "  </molecule>" +
            S_EMPTY;
        CMLMolecule triangleMol = makeMol(triangleMolS);
        ConnectionTableTool triangleMolTool = new ConnectionTableTool(triangleMol);
        cyclicBondList = triangleMolTool.getCyclicBonds();
        assertEquals("cyclic ", 6, cyclicBondList.size());
        
        
        String triangle1MolS =
            "  <molecule id='m1' "+CML_XMLNS+">" +
            "    <atomArray>" +
            "      <atom id='a1' elementType='C'/>" +
            "      <atom id='a2' elementType='N'/>" +
            "      <atom id='a3' elementType='O'/>" +
            "      <atom id='a4' elementType='S'/>" +
            "    </atomArray>" +
            "    <bondArray>" +
            "      <bond id='b12' atomRefs2='a1 a2'/>" +
            "      <bond id='b14' atomRefs2='a1 a3'/>" +
            "      <bond id='b23' atomRefs2='a1 a4'/>" +
            "      <bond id='b34' atomRefs2='a2 a3'/>" +
            "      <bond id='b15' atomRefs2='a2 a4'/>" +
            "      <bond id='b56' atomRefs2='a3 a4'/>" +
            "    </bondArray>" +
            "  </molecule>" +
            S_EMPTY;
        CMLMolecule triangle1Mol = makeMol(triangle1MolS);
        ConnectionTableTool triangle1MolTool = new ConnectionTableTool(triangle1Mol);
        cyclicBondList = triangle1MolTool.getCyclicBonds();
        assertEquals("cyclic ", 6, cyclicBondList.size());
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getAcyclicBonds()'
     */
    @Test
    public void testGetAcyclicBonds() {
        List<CMLBond> acyclicBondList = null;
        acyclicBondList = ringMolTool.getAcyclicBonds();
        Assert.assertEquals("cyclic bond count", 3, acyclicBondList.size());
        CMLBondSet bondSet = null;
        bondSet = new CMLBondSet(acyclicBondList);
        CMLBondSet bondSet1 = new CMLBondSet(ringMolTool.getMolecule(), 
            new String[]{"b15", "b910", "b1011"});
        Assert.assertTrue("bond set bonds ", bondSet.hasContentEqualTo(bondSet1));
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.getAcyclicDoubleBonds()'
     */
    @Test
    public void testGetAcyclicDoubleBonds() {
        List<CMLBond> acyclicDoubleBondList = null;
        acyclicDoubleBondList = ringMolTool.getAcyclicDoubleBonds();
        Assert.assertEquals("cyclic bond count", 1, acyclicDoubleBondList.size());
        CMLBondSet bondSet = null;
        bondSet = new CMLBondSet(acyclicDoubleBondList);
        CMLBondSet bondSet1 = new CMLBondSet(ringMolTool.getMolecule(), 
            new String[]{"b910"});
        Assert.assertTrue("bond set bonds ", bondSet.hasContentEqualTo(bondSet1));
    }

    /** Test method for 'org.xmlcml.cml.tools.ConnectionTableTool.clearCyclicBonds()'
     */
    @Test
    public void testClearCyclicBonds() {
        List<CMLBond> cyclicBondList = null;
        cyclicBondList = ringMolTool.getCyclicBonds();
        Assert.assertEquals("cyclic bond count", 10, cyclicBondList.size());
        CMLBondSet bondSet = null;
        bondSet = new CMLBondSet(cyclicBondList);
        CMLBondSet bondSet1 = new CMLBondSet(ringMolTool.getMolecule(), 
            new String[]{"b12", "b23", "b34", "b14", "b56", "b57", "b67", "b78", "b69", "b89"});
        Assert.assertTrue("bond set bonds ", bondSet.hasContentEqualTo(bondSet1));
        Assert.assertEquals("is cyclic", "b12", bondSet.getBonds().get(0).getId());
        Assert.assertEquals("is cyclic", CMLBond.CYCLIC, bondSet.getBonds().get(0).getCyclic());
        ringMolTool.clearCyclicBonds();
        Assert.assertEquals("is cyclic", CMLBond.CYCLIC_UNKNOWN, bondSet.getBonds().get(0).getCyclic());
        
    }
    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#partitionIntoMolecules()}.
     */
    @Test
    public final void testPartitionIntoMolecules() {
        multiMol = makeMol(multiMolS);
        multiMolTool = new ConnectionTableTool(multiMol);
        multiMolTool.partitionIntoMolecules();
        CMLMolecule molecule = multiMolTool.getMolecule();
        assertNotNull("after partition", molecule);
        CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
        assertEquals("after partition", 2, molecules.size());
        assertEquals("after partition", 5, molecules.get(0).getAtoms().size());
        assertEquals("after partition", 6, molecules.get(1).getAtoms().size());
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#getUniqueId(org.xmlcml.cml.element.CMLAtom)}.
     */
    @Test
    public final void testGetUniqueId() {
        String id = ringMolTool.getUniqueId(ringMol.getAtom(0));
        assertEquals("unique id", "a1.1", id);
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#flattenMolecules()}.
     */
    @Test
    public final void testFlattenMolecules() {
        multiMol = makeMol(multiMolS);
        multiMolTool = new ConnectionTableTool(multiMol);
        multiMolTool.partitionIntoMolecules();
        // see above
        CMLMolecule molecule = multiMolTool.getMolecule();
        molecule.setTitle("before flatten");
        CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
        assertEquals("after partition", 2, molecules.size());
        multiMolTool.flattenMolecules();
        molecule.setTitle("after flatten");
        assertEquals("after flattening", 11, molecule.getAtoms().size());
        assertEquals("after flattening", 11, molecule.getBonds().size());
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#mergeMolecule(org.xmlcml.cml.element.CMLMolecule)}.
     */
    @Test
    public final void testMergeMolecule() {
        assertEquals("ring mol", 11, ringMol.getAtoms().size());
        assertEquals("ring mol", 13, ringMol.getBonds().size());
        CMLMolecule simpleMol = makeMol(simpleMolS);
        assertEquals("simple mol", 3, simpleMol.getAtoms().size());
        assertEquals("simple mol", 2, simpleMol.getBonds().size());
        try {
            ringMolTool.mergeMolecule(simpleMol);
        } catch (CMLException e) {
            fail("should not throw "+e);
        }
        assertEquals("ring mol", 14, ringMol.getAtoms().size());
        assertEquals("ring mol", 15, ringMol.getBonds().size());
        
    }

    /**
     */
    @Test
    public final void testCreateSubMolecule() {
        String[] atomIds = {"a1", "a2", "a3", "a4", "a5"};
        CMLMolecule subMolecule = ringMolTool.createSubMolecule(atomIds);
        assertEquals("submol", 5, subMolecule.getAtoms().size());
        assertEquals("submol", 5, subMolecule.getBonds().size());
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#createUniqueAtomId()}.
     */
    @Test
    public final void testCreateUniqueAtomId() {
        String id = ringMolTool.createUniqueAtomId();
        assertEquals("new id", "a12", id);
        assertNull("id does not exist", ringMol.getAtomById(id));
        CMLAtom atom = new CMLAtom(id);
        ringMol.addAtom(atom);
        String id1 = ringMolTool.createUniqueAtomId();
        assertEquals("new id", "a13", id1);
        assertNotNull("id does exist", ringMol.getAtomById(id));
        assertNull("id does not exist", ringMol.getAtomById(id1));
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#generateBondIds()}.
     */
    @Test
    public final void testGenerateBondIds() {
        ringMolTool.generateBondIds();
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#addSuffixToAtomIDs(java.lang.String)}.
     */
    @Test
    public final void testAddSuffixToAtomIDs() {
        ringMolTool.addSuffixToAtomIDs(".x");
        CMLAtom a1 = ringMol.getAtom(0);
        assertEquals("atom 1", "a1.x", a1.getId());
        CMLAtom a2 = ringMol.getAtom(1);
        assertEquals("atom 2", "a2.x", a2.getId());
        CMLBond b1 = ringMol.getBonds().get(0);
        String[] atrefs2 = {"a1.x", "a2.x"};
        assertEquals("bond 1", atrefs2[0], b1.getAtomRefs2()[0]);
        assertEquals("bond 1", atrefs2[1], b1.getAtomRefs2()[1]);
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#getNextCoordinationSphere(java.lang.String[])}.
     */
    @Test
    public final void testGetNextCoordinationSphereStringArray() {
        String[] atomIds = new String[]{"a1"};
        CMLAtomSet atomSet = null;
        try {
            atomSet = ringMolTool.getNextCoordinationSphere(atomIds);
        } catch (CMLException e) {
            neverThrow(e);
        }
        CMLAtomSet atomSet1 = new CMLAtomSet(ringMol, new String[]{"a1", "a4", "a5", "a2"});
        Assert.assertTrue("atomSet should have equal content", atomSet.hasContentEqualTo(atomSet1));
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.ConnectionTableTool#getNextCoordinationSphere(org.xmlcml.cml.element.CMLAtomSet)}.
     */
    @Test
    public final void testGetNextCoordinationSphereCMLAtomSet() {
        CMLAtomSet atomSet1 = new CMLAtomSet(ringMol, new String[]{"a3", "a4", "a2"});
        CMLAtomSet atomSet = ringMolTool.getNextCoordinationSphere(atomSet1);
        CMLAtomSet atomSet2 = new CMLAtomSet(ringMol, new String[]{"a3", "a4", "a2", "a1"});
        Assert.assertTrue("atomSet should have equal content", atomSet2.hasContentEqualTo(atomSet));
    }
    
}
