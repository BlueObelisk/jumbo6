package org.xmlcml.cml.tools;

import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.testutil.JumboTestUtils;
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
    	bondTool.getBondDisplay().setWidth(5.0);
    	SVGSVG svgsvg = createSvgSvg(bondTool);
        Element ref = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/bond1.svg");
        JumboTestUtils.assertEqualsIncludingFloat("svg", ref, svgsvg, true, 0.0000000001);
    }

	private SVGSVG createSvgSvg(BondTool bondTool) {
		CMLDrawable drawable = new MoleculeDisplayList();
    	SVGG svgg = (SVGG) bondTool.createGraphicsElement(drawable);
    	svgg.translate(new Real2(100., -100.));
    	SVGSVG svgsvg = SVGSVG.wrapAsSVG(svgg);
		return svgsvg;
	}

}
