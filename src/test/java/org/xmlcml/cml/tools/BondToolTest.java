/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Real2;
import org.xmlcml.molutil.ChemicalElement;

public class BondToolTest {
  
    @Test
    public void testCreateGraphicsElement1() {
      CMLMolecule molecule = new CMLMolecule();
      CMLAtom atom1 = new CMLAtom("a1", ChemicalElement.AS.O);
      molecule.addAtom(atom1);
      atom1.setXY2(new Real2(0.0, 0.0));
      CMLAtom atom2 = new CMLAtom("a2", ChemicalElement.AS.C);
      molecule.addAtom(atom2);
      atom2.setXY2(new Real2(50.0, 0.0));
      CMLBond bond = new CMLBond(atom1, atom2);
      molecule.addBond(bond);
      bond.setOrder(BondOrder.D_S);
      BondTool bondTool = BondTool.getOrCreateTool(bond);
      /*
       * NOT YET TESTED
      bondTool.getBondDisplay().setWidth(5.0);
      SVGSVG svgsvg = createSvgSvg(bondTool);
        Element ref = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/bond1.svg");
        JumboTestUtils.assertEqualsIncludingFloat("svg", ref, svgsvg, true, 0.0000000001);
        */
    }

  private SVGSVG createSvgSvg(BondTool bondTool) {
    CMLDrawable drawable = new MoleculeDisplayList();
      SVGG svgg = (SVGG) bondTool.createGraphicsElement(drawable);
      svgg.translate(new Real2(100., -100.));
      SVGSVG svgsvg = SVGSVG.wrapAsSVG(svgg);
    return svgsvg;
  }
  
  @Test
  public void getDownstreamMorganString() {
    CMLMolecule molecule = SMILESTool.createMolecule("CNO");
    CMLBond bond = molecule.getBondByAtomIds("a1", "a2");
    assertMorganString(bond, "group0", "6590442H1/6655673H1/11158549N2/");
  }

  @Test
  public void getDownstreamMorganString1() {
    CMLMolecule molecule = SMILESTool.createMolecule("FCNO");
    CMLBond bond = molecule.getBondByAtomIds("a2", "a3");
    assertMorganString(bond, "group1", "6590442H1/6655673H1/11158549N2/");
  }

  @Test
  public void getDownstreamMorganString2() {
    CMLMolecule molecule = SMILESTool.createMolecule("c1ccc(N)cc1CNO");
    CMLBond bond = molecule.getBondByAtomIds("a8", "a9");
    assertMorganString(bond, "group2", "6590442H1/6655673H1/11158549N2/");
  }

  @Test
  public void getDownstreamMorganString3() {
    CMLMolecule molecule = SMILESTool.createMolecule("[R]CNO");
    CMLBond bond = molecule.getBondByAtomIds("a2", "a3");
    assertMorganString(bond, "rGroup", "6590442H1/6655673H1/11158549N2/");
  }

  private void assertMorganString(CMLBond bond, String title, String expected) {
    BondTool bondTool = BondTool.getOrCreateTool(bond);
    CMLAtom atom0 = bond.getAtom(0);
    String morganString = bondTool.getDownstreamMorganString(atom0);
    Assert.assertEquals(title, expected, morganString);
  }

  @Test
  public void matchesGroup0() {
    CMLMolecule molecule = SMILESTool.createMolecule("[R]CNO");
    CMLBond bond = molecule.getBondByAtomIds("a2", "a3");
    try {
      BondTool.getOrCreateTool(bond).matchesGroupAgainstSMILES(-1, "junk");
      Assert.fail("Should trap bad arg");
    } catch (RuntimeException e) {
    }
  }
  
  @Test
  @Ignore
  public void matchesGroup1() {
    CMLMolecule molecule = SMILESTool.createMolecule("c1ccccc1CCNO");
    CMLBond bond = molecule.getBondByAtomIds("a8", "a9");
    BondTool bondTool = BondTool.getOrCreateTool(bond);
    Assert.assertTrue(bondTool.matchesGroupAgainstSMILES(0, "[R]CNO"));
  }

  @Test
  @Ignore
  public void matchesGroup1a() {
    CMLMolecule molecule = SMILESTool.createMolecule("FCNO");
    CMLBond bond = molecule.getBondByAtomIds("a2", "a3");
    Assert.assertTrue(BondTool.getOrCreateTool(bond).matchesGroupAgainstSMILES(0, "[R]CNO"));
  }

  @Test
  @Ignore
  public void matchesGroup1h() {
    CMLMolecule molecule = SMILESTool.createMolecule("CNO");
    CMLBond bond = molecule.getBondByAtomIds("a1", "a2");
    Assert.assertTrue(BondTool.getOrCreateTool(bond).matchesGroupAgainstSMILES(0, "[R]CNO"));
  }

  @Test
  public void notMatchesGroup1() {
    CMLMolecule molecule = SMILESTool.createMolecule("c1ccccc1CCNO");
    CMLBond bond = molecule.getBondByAtomIds("a8", "a9");
    Assert.assertFalse(BondTool.getOrCreateTool(bond).matchesGroupAgainstSMILES(0, "[R]CON"));
  }
  
  @Test
  @Ignore // not usre why
  public void testExpandAtomRefs2() {
    String moleculeS = "" +
        "<molecule xmlns='http://www.xml-cml.org/schema'>" +
        "  <atomArray>" +
        "    <atom id='a1'/>" +
        "    <atom id='a2'/>" +
        "  </atomArray>" +
        "  <bondArray>" +
        "    <bond id='a1_a2' atomRefs2='a1 a2'/>" +
        "  </bondArray>" +
        "</molecule>" +
        "";
	    CMLMolecule molecule = (CMLMolecule) CMLUtil.parseCML(moleculeS);
	    CMLBond bond = molecule.getBonds().get(0);
	    BondTool bondTool = BondTool.getOrCreateTool(bond);
	    bondTool.expandAtomRefs2();
	    String ref = 
	        "<molecule xmlns=\"http://www.xml-cml.org/schema\">" +
	        "  <atomArray>" +
	        "    <atom id=\"a1\"/>" +
	        "    <atom id=\"a2\"/>" +
	        "  </atomArray>" +
	        "  <bondArray>" +
	        "    <bond id=\"a1_a2\" atomRefs2=\"a1 a2\">" +
	        "      <atom ref=\"a1\"/>" +
	        "      <atom ref=\"a2\"/>" +
	        "    </bond>" +
	        "  </bondArray>" +
	        "</molecule>" +
	        "";

    JumboTestUtils.assertEqualsCanonically("expand", ref, bond, true);
  }
  
}
