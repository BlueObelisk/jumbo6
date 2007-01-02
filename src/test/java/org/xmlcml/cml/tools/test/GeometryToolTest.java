package org.xmlcml.cml.tools.test;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.test.MoleculeAtomBondTest;
/**
 * test geometry tool
 *
 * @author pmr
 *
 */
public class GeometryToolTest extends MoleculeAtomBondTest {
    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtomSet.createValenceLengths(boolean,
     * boolean)'
     */
    @Test
    @Ignore
    public void testCreateValenceLengthsAtomSet() {
        /*--
        CMLAtomSet atomSet = new CMLAtomSet(xomAtom);
        MoleculeTool moleculeTool = new MoleculeTool(xomAtom[0].getMolecule());
        boolean calculate = true;
        boolean add = true;
        List<CMLLength> lengths = moleculeTool.createValenceLengths(atomSet,
                calculate, add);
        Assert.assertNotNull("length", lengths);
        Assert.assertEquals("length", 5, lengths.size());
        Assert.assertEquals("length", Math.sqrt(3.), lengths.get(0)
                .getCalculatedLength(atomSet.getMolecule()));
                --*/
    }
 }
