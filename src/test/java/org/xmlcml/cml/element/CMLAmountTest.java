/**
 * 
 */
package org.xmlcml.cml.element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.attribute.UnitTypeAttribute;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.molutil.ChemicalElement;

/**
 * @author pm286
 *
 */
public class CMLAmountTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.CMLAmount#getMolarAmount(org.xmlcml.cml.element.CMLMolecule, org.xmlcml.cml.element.CMLAmount)}.
	 */
	@Test
	public final void testGetMolarAmount() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom = new CMLAtom("a1", ChemicalElement.getChemicalElement("Na"));
		molecule.addAtom(atom);
		atom = new CMLAtom("a2", ChemicalElement.getChemicalElement("Cl"));
		molecule.addAtom(atom);
		double d = molecule.getCalculatedMolecularMass(HydrogenControl.NO_EXPLICIT_HYDROGENS);
		Assert.assertEquals("MW ", 58.44277, d);
		CMLAmount massAmount = new CMLAmount();
		massAmount.setUnits("units:g");
//		massAmount.setUnitType("unitType:mass");
		massAmount.addAttribute(new UnitTypeAttribute("unitType:mass"));
		massAmount.setXMLContent(100.0);
		CMLAmount molarAmount = massAmount.getMolarAmount(molecule);
		Assert.assertNotNull("molarAmount not null", molarAmount);
		Assert.assertEquals("molarAmount", 1.7110756386119275, molarAmount.getXMLContent(), 0.00001);
	}

}
