package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLMolecule;

/** tests atomTree.
 * 
 * @author pm286
 *
 */
public class AtomTreeTest extends AbstractToolTest {

    String dmfS = S_EMPTY +
            "<molecule id='m1' "+CML_XMLNS + ">" +
            "  <atomArray>" +
            "    <atom id='a1' elementType='N' hydrogenCount='0'/>"+
            "    <atom id='a2' elementType='C' hydrogenCount='3'>"+
            "      <label>C1</label>"+
            "    </atom>"+
            "    <atom id='a3' elementType='C' hydrogenCount='3'>"+
            "      <label>C2</label>"+
            "    </atom>"+
            "    <atom id='a4' elementType='C' hydrogenCount='1'/>"+
            "    <atom id='a5' elementType='O' hydrogenCount='0'/>" +
            "  </atomArray>" +
            "  <bondArray>" +
            "    <bond atomRefs2='a1 a2'/>"+
            "    <bond atomRefs2='a1 a3'/>"+
            "    <bond atomRefs2='a1 a4'/>"+
            "    <bond atomRefs2='a5 a3' order='2'/>"+
            "  </bondArray>" +
            "</molecule>" +
            S_EMPTY;
    
    CMLMolecule dmf = null;

    /** set up.
     * @exception Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dmf = (CMLMolecule) parseValidString(dmfS);
    }
    /** Test method for 'org.xmlcml.cml.tools.AtomTree.AtomTree(CMLAtom, CMLAtom)'
     */
    @Test
    public void testAtomTreeCMLAtomCMLAtom() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0), dmf.getAtom(1));
        atomTree.expandTo(2);
        Assert.assertEquals("new AtomTree", "C", atomTree.toString());
        atomTree = new AtomTree(dmf.getAtom(1), dmf.getAtom(0));
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.AtomTree(CMLAtom)'
     */
    @Test
    public void testAtomTreeCMLAtom() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
        atomTree = new AtomTree(dmf.getAtom(1));
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "C(N(C)(C(O)))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.setUseCharge(boolean)'
     */
    @Test
    public void testSetUseCharge() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.expandTo(3);
        atomTree.setUseCharge(true);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.setUseLabel(boolean)'
     */
    @Test
    public void testSetUseLabel() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.setUseLabel(false);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
        atomTree.setUseLabel(true);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C)(C(O))(C{C1})(C{C2}(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.setUseImplicitHydrogens(boolean)'
     */
    @Test
    public void testSetUseImplicitHydrogens() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.setUseImplicitHydrogens(false);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
        atomTree.setUseImplicitHydrogens(true);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))(CH)(CH3)(CH3(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.setUseExplicitHydrogens(boolean)'
     */
    @Test
    public void testSetUseExplicitHydrogens() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.setUseExplicitHydrogens(false);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
        atomTree.setUseExplicitHydrogens(true);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C)(C)(C(O))(C(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.expandTo(int)'
     */
    @Test
    public void testExpandTo() {
        AtomTree atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.setUseExplicitHydrogens(false);
        atomTree.expandTo(0);
        Assert.assertEquals("new AtomTree", "N", atomTree.toString());
        atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.expandTo(1);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C)", atomTree.toString());
        atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.expandTo(2);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
        atomTree = new AtomTree(dmf.getAtom(0));
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "N(C)(C)(C(O))", atomTree.toString());
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.compareTo(Object)'
     */
    @Test
    public void testCompareTo() {
        AtomTree atomTree0 = new AtomTree(dmf.getAtom(0));
        atomTree0.expandTo(3);
        AtomTree atomTree1 = new AtomTree(dmf.getAtom(1));
        atomTree1.expandTo(3);
//        System.out.println("1: "+atomTree1.toString());
        Assert.assertTrue("compare 0 1 ", atomTree0.compareTo(atomTree1) > 0);
        AtomTree atomTree2 = new AtomTree(dmf.getAtom(2));
        atomTree2.expandTo(3);
//        System.out.println("2 "+atomTree2.toString());
        Assert.assertEquals("compare 0 1 ", -1, atomTree1.compareTo(atomTree2));
    }

    /** Test method for 'org.xmlcml.cml.tools.AtomTree.getAtomTreeLabelling(CMLAtomSet, AtomMatcher)'
     */
    @SuppressWarnings("all")
    @Test
    public void testGetAtomTreeLabelling() {
        CMLAtomSet atomSet = new CMLAtomSet(dmf.getAtoms());
        Map<String, Object> map = AtomTree.getAtomTreeLabelling(atomSet, new AtomMatcher());
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
  //              System.out.println("A "+((CMLAtom)obj).getId());
            } else if (obj instanceof CMLAtomSet) {
 //               System.out.println("AS "+Util.concatenate(((CMLAtomSet)obj).getXMLContent(), "/"));
            }
        }
    }

    /** typical example with symmetry.
     *
     */
    @Test
    public void testPhenyl() {
        String phenylS = "<molecule "+CML_XMLNS+">"+
            "  <atomArray>" +
            "    <atom id='a1' elementType='C' hydrogenCount='0'/>" +
            "    <atom id='a2' elementType='C' hydrogenCount='1'/>" +
            "    <atom id='a3' elementType='C' hydrogenCount='1'/>" +
            "    <atom id='a4' elementType='C' hydrogenCount='1'/>" +
            "    <atom id='a5' elementType='C' hydrogenCount='1'/>" +
            "    <atom id='a6' elementType='C' hydrogenCount='1'/>" +
            "    <atom id='a7' elementType='R'/>" +
            "  </atomArray>" +
            "  <bondArray>" +
            "    <bond atomRefs2='a1 a2' order='A'/>" +
            "    <bond atomRefs2='a1 a6' order='A'/>" +
            "    <bond atomRefs2='a1 a7' order='1'/>" +
            "    <bond atomRefs2='a3 a2' order='A'/>" +
            "    <bond atomRefs2='a3 a4' order='A'/>" +
            "    <bond atomRefs2='a4 a5' order='A'/>" +
            "    <bond atomRefs2='a5 a6' order='A'/>" +
            "  </bondArray>" +
            "</molecule>";
        CMLMolecule phenyl = null;
        try {
            phenyl = (CMLMolecule) new CMLBuilder().parseString(phenylS);
        } catch (Exception e) {
            neverThrow(e);
        }
        AtomTree atomTree = new AtomTree(phenyl.getAtom(6));
        atomTree.setUseExplicitHydrogens(false);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", "R(C(C(C))(C(C)))", atomTree.toString());
        atomTree.setUseExplicitHydrogens(true);
        atomTree.expandTo(3);
        Assert.assertEquals("new AtomTree", 
                "R(C(C(C))(C(C)))(C(C(C))(C(C)))", atomTree.toString());
        }
    
    /** typical example with symmetry.
    *
    */
   @Test
   public void testAnisole() {
       String anisoleS = "<molecule "+CML_XMLNS+">"+
           "  <atomArray>" +
           "    <atom id='a1' elementType='C' hydrogenCount='0'/>" +
           "    <atom id='a2' elementType='C' hydrogenCount='1'/>" +
           "    <atom id='a3' elementType='C' hydrogenCount='1'/>" +
           "    <atom id='a4' elementType='C' hydrogenCount='1'/>" +
           "    <atom id='a5' elementType='C' hydrogenCount='1'/>" +
           "    <atom id='a6' elementType='C' hydrogenCount='1'/>" +
           "    <atom id='a7' elementType='O'/>" +
           "    <atom id='a8' elementType='C' hydrogenCount='3'/>" +
           "  </atomArray>" +
           "  <bondArray>" +
           "    <bond atomRefs2='a1 a2' order='A'/>" +
           "    <bond atomRefs2='a1 a6' order='A'/>" +
           "    <bond atomRefs2='a1 a7' order='1'/>" +
           "    <bond atomRefs2='a3 a2' order='A'/>" +
           "    <bond atomRefs2='a3 a4' order='A'/>" +
           "    <bond atomRefs2='a4 a5' order='A'/>" +
           "    <bond atomRefs2='a5 a6' order='A'/>" +
           "    <bond atomRefs2='a8 a7' order='1'/>" +
                     "  </bondArray>" +
           "</molecule>";
       CMLMolecule anisole = null;
       try {
           anisole = (CMLMolecule) new CMLBuilder().parseString(anisoleS);
       } catch (Exception e) {
           neverThrow(e);
       }
       AtomTree atomTree = new AtomTree(anisole.getAtom(6));
       atomTree.setUseExplicitHydrogens(false);
       atomTree.expandTo(3);
       Assert.assertEquals("new AtomTree", "O(C)(C(C(C))(C(C)))", atomTree.toString());
       atomTree.setUseExplicitHydrogens(true);
       atomTree.expandTo(3);
       Assert.assertEquals("new AtomTree", 
               "O(C)(C)(C(C(C))(C(C)))(C(C(C))(C(C)))", atomTree.toString());
   }
   
}
