/**
 * 
 */
package org.xmlcml.cml.tools;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.MoleculeAtomBondTest;
import org.xmlcml.cml.tools.Morgan.Algorithm;
import org.xmlcml.euclid.test.StringTest;

/**
 * @author pm286
 *
 */
public class MorganTest extends MoleculeAtomBondTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLMolecule)}.
     */
    @Test
    public final void testMorganCMLMolecule() {
        makeMol5a();
        Morgan morgan = new Morgan(mol5a);
        List<Long> morganList = morgan.getMorganList();
        assertEquals("equivalence classes", new long[] {
                 6914834 , 8549135 , 13376403 , 15137814
                }, morganList);
        
        List<CMLAtomSet> atomSets = morgan.getAtomSetList();
        assertEquals("equivalence classes", new String[][]{
                new String[]{"a3"},
                new String[]{"a4", "a5"},
                new String[]{"a2"},
                new String[]{"a1"},
        }, atomSets);
    }

    /**
     * Test method for {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}.
     */
    @Test
    public final void testMorganCMLAtomSet() {
        makeMol5a();
        Morgan morgan = new Morgan(mol5a.getAtomSet());
        List<Long> morganList = morgan.getMorganList();
        assertEquals("mol5a", new long[]{
                6914834 , 8549135 , 13376403 , 15137814
        }, morganList);
        List<CMLAtomSet> atomSets = morgan.getAtomSetList();
        assertEquals("mol5a", new String[][]{
                new String[]{"a3"},
                new String[]{"a4", "a5"},
                new String[]{"a2"},
                new String[]{"a1"},
        }, atomSets);
    }
    
    /**
     * Test method for {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}.
     */
    @Test
    public final void testAlgorithm() {
        makeMol5a();
        Morgan morgan = new Morgan(mol5a.getAtomSet());
        morgan.setAlgorithm(Algorithm.SPLIT);
        List<Long> morganList = morgan.getMorganList();
        assertEquals("Morgan list", new long[]{
                6914834 , 8549135 , 8549136 , 13376403 , 15137815                 },
            morganList);
        
        List<CMLAtomSet> atomSets = morgan.getAtomSetList();
        assertEquals("equivalence classes", new String[][]{
                new String[]{"a3"},
                new String[]{"a5"},
                new String[]{"a4"},
                new String[]{"a2"},
                new String[]{"a1"},
            },
            atomSets);
    }


    /**
     * Test method for {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}.
     * test split various equivalences
     */
    @Test
    // FIXME
    @Ignore
    public final void testAlgorithm1() {
        String benzeneS = S_EMPTY +
                "<molecule "+CML_XMLNS+">" +
                "  <atomArray>" +
                "    <atom id='a1' elementType='C'/>" +
                "    <atom id='h1' elementType='H'/>" +
                "    <atom id='a2' elementType='C'/>" +
                "    <atom id='h2' elementType='H'/>" +
                "    <atom id='a3' elementType='C'/>" +
                "    <atom id='h3' elementType='H'/>" +
                "    <atom id='a4' elementType='C'/>" +
                "    <atom id='h4' elementType='H'/>" +
                "    <atom id='a5' elementType='C'/>" +
                "    <atom id='h5' elementType='H'/>" +
                "    <atom id='a6' elementType='C'/>" +
                "    <atom id='h6' elementType='H'/>" +
                "  </atomArray>" +
                "  <bondArray>" +
                "    <bond atomRefs2='a1 a2'/>" +
                "    <bond atomRefs2='a1 h1'/>" +
                "    <bond atomRefs2='a2 a3'/>" +
                "    <bond atomRefs2='a2 h2'/>" +
                "    <bond atomRefs2='a3 a4'/>" +
                "    <bond atomRefs2='a3 h3'/>" +
                "    <bond atomRefs2='a4 a5'/>" +
                "    <bond atomRefs2='a4 h4'/>" +
                "    <bond atomRefs2='a5 a6'/>" +
                "    <bond atomRefs2='a5 h5'/>" +
                "    <bond atomRefs2='a6 a1'/>" +
                "    <bond atomRefs2='a6 h6'/>" +
                "  </bondArray>" +
                "</molecule>";
        CMLMolecule benzene = (CMLMolecule) parseValidString(benzeneS);
        Morgan morgan = new Morgan(benzene.getAtomSet());
        List<Long> morganList = morgan.getMorganList();
        assertEquals("benzene", new long[]{
                8547372 , 21466984
                }, morganList);
        
        List<CMLAtomSet> atomSets = morgan.getAtomSetList();
        assertEquals("benzene", new String[][]{
                new String[]{"h1", "h2", "h3", "h4", "h5", "h6"},
                new String[]{"a1", "a2", "a3", "a4", "a5", "a6"},
        }, atomSets);

        //==================================
        
        benzene = (CMLMolecule) parseValidString(benzeneS);
        morgan = new Morgan(benzene.getAtomSet());
        List<CMLAtom> markedAtoms = morgan.getMarkedAtomList();
        Assert.assertNull("marked atoms null", markedAtoms);
        morgan.setAlgorithm(Algorithm.SPLIT);
        morganList = morgan.getMorganList();
        
        assertEquals("marked benzene", new long[]{
                102962709 , 102962723 , 102962783 , 102962791 , 102962818
                , 102962838 , 248859457 , 248859476 , 248859555 , 248859579
                , 248859638 , 248859657
                
        }, morganList);
        
        atomSets = morgan.getAtomSetList();
        assertEquals("markedBenzene", 
                new String[][]{
                new String[]{"h5"},
                new String[]{"h6"},
                new String[]{"h4"},
                new String[]{"h1"},
                new String[]{"h3"},
                new String[]{"h2"},
                new String[]{"a5"},
                new String[]{"a6"},
                new String[]{"a4"},
                new String[]{"a1"},
                new String[]{"a3"},
                new String[]{"a2"},        
                }, atomSets);
        
        markedAtoms = morgan.getMarkedAtomList();
        Assert.assertEquals("marked atoms", 7, markedAtoms.size());
        for (CMLAtom atom : markedAtoms) {
            System.out.println("A "+atom.getId());
        }
        
        //==================================
        
        benzene = (CMLMolecule) parseValidString(benzeneS);
        morgan = new Morgan(benzene.getAtomSet());
        benzene.getAtom(1).setProperty(Morgan.Annotation.MARKED.toString(), new Long(1));
        morganList = morgan.getMorganList();
//        morgan.debug("benzene");
        assertEquals("marked benzene", new long[]{
                  5707768002L,  5707768005L,  5707768015L,  5707768026L,  
                  13780200010L,  13780200018L,  13780200035L,  13780200048L
        }, morganList);
        
        atomSets = morgan.getAtomSetList();
        assertEquals("markedBenzene", new String[][]{
                new String[]{"h4"},
                new String[]{"h3", "h5"},
                new String[]{"h2", "h6"},
                new String[]{"h1"},
                new String[]{"a4"},
                new String[]{"a3", "a5"},
                new String[]{"a2", "a6"},
                new String[]{"a1"},
                }, atomSets);
        
        markedAtoms = morgan.getMarkedAtomList();
////        Assert.assertEquals("marked atoms", 7, markedAtoms.size());
//        for (CMLAtom atom : markedAtoms) {
//            System.out.println("A "+atom.getId());
//        }

        //==================================
        
        benzene = (CMLMolecule) parseValidString(benzeneS);
        morgan = new Morgan(benzene.getAtomSet());
        benzene.getAtom(1).setProperty(Morgan.Annotation.MARKED.toString(), new Long(1));
        benzene.getAtom(5).setProperty(Morgan.Annotation.MARKED.toString(), new Long(2));
        morganList = morgan.getMorganList();
        assertEquals("marked benzene", new long[]{
               1671552003L, 1671552004L, 1671552008L, 1671552012L, 1671552021L,
               4036216012L, 4036216015L, 4036216024L, 4036216033L, 4036216036L
        }, morganList);
        
        atomSets = morgan.getAtomSetList();
        assertEquals("markedBenzene", new String[][]{
                new String[]{"h5"},
                new String[]{"h6"},
                new String[]{"h4"},
                new String[]{"h1", "h2"},
                new String[]{"h3"},
                
                new String[]{"a5"},
                new String[]{"a6"},
                new String[]{"a1", "a4"},
                new String[]{"a2",},
                new String[]{"a3"},
                }, atomSets);
        
        markedAtoms = morgan.getMarkedAtomList();
//        Assert.assertEquals("marked atoms", 7, markedAtoms.size());
//        for (CMLAtom atom : markedAtoms) {
//            System.out.println("A "+atom.getId());
//        }

        // ==========================
        
        benzene = (CMLMolecule) parseValidString(benzeneS);
        morgan = new Morgan(benzene.getAtomSet());
        benzene.getAtom(1).setElementType("Br");
        benzene.getAtom(5).setElementType("Cl");
//        benzene.debug();
        morganList = morgan.getMorganList();
//        morgan.debug("benzene");
        assertEquals("marked benzene", new long[]{
               1671552003L, 1671552004L, 1671552008L, 1671552012L, 1671552021L,
               4036216012L, 4036216015L, 4036216024L, 4036216033L, 4036216036L
        }, morganList);
        
        atomSets = morgan.getAtomSetList();
        assertEquals("markedBenzene", new String[][]{
                new String[]{"h5"},
                new String[]{"h6"},
                new String[]{"h4"},
                new String[]{"h1", "h2"},
                new String[]{"h3"},
                
                new String[]{"a5"},
                new String[]{"a6"},
                new String[]{"a1", "a4"},
                new String[]{"a2",},
                new String[]{"a3"},
                }, atomSets);
        
        markedAtoms = morgan.getMarkedAtomList();
//        Assert.assertEquals("marked atoms", 7, markedAtoms.size());
//        for (CMLAtom atom : markedAtoms) {
//            System.out.println("A "+atom.getId());
//        }
    }

    private void assertEquals(String message, long[] test, List<Long> morganList) {
        Assert.assertNotNull("Morgan list should be set", morganList);
        Assert.assertEquals("equivalence classes", test.length, morganList.size());
        for (int i = 0; i < test.length; i++) {
            Assert.assertEquals("class "+i, test[i], morganList.get(i).longValue());
        }
    }
    
    private void assertEquals(String message, String[][] test, List<CMLAtomSet>atomSets) {
        Assert.assertNotNull("AtomSets list should be set", atomSets);
        Assert.assertEquals("equivalence classes", test.length, atomSets.size());
        for (int i = 0; i < test.length; i++) {
            StringTest.assertEquals("class "+i, test[i], atomSets.get(i).getXMLContent());
        }
    }


}
