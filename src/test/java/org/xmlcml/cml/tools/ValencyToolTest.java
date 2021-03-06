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

import java.io.IOException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;

/**
 * 
 * @author pm286
 * 
 */
public class ValencyToolTest {

	MoleculeToolFixture fix = new MoleculeToolFixture();

	/**
	 * Test method for 'org.xmlcml.cml.tools.valencyTool.markSpecial()' Not all
	 * methods are exhaustively tested, just the principle
	 */
	@Test
	public void testMarkSpecial() {
		CMLMolecule nitroMethane = (CMLMolecule)CMLXOMTestUtils.parseValidString(fix.nitroMethaneS);
		// MoleculeTool moleculeTool =
		// MoleculeTool.getOrCreateTool(nitroMethane);
		CMLAtom nAtom = nitroMethane.getAtom(0);
		Assert.assertEquals("nitro", "a1", nAtom.getId());
		Assert.assertNotNull("nitro", nAtom.getFormalChargeAttribute());
		Assert.assertEquals("nitro", 1, nAtom.getFormalCharge());
		CMLAtom cAtom = nitroMethane.getAtom(1);
		Assert.assertEquals("nitro", "a2", cAtom.getId());
		CMLAtom oAtom1 = nitroMethane.getAtom(2);
		Assert.assertEquals("nitro", "a3", oAtom1.getId());
		Assert.assertNotNull("nitro", oAtom1.getFormalChargeAttribute());
		Assert.assertEquals("nitro", -1, oAtom1.getFormalCharge());
		CMLAtom oAtom2 = nitroMethane.getAtom(3);
		Assert.assertEquals("nitro", "a4", oAtom2.getId());
		CMLBond bond = nitroMethane.getBonds().get(2);
		Assert.assertEquals("nitro", CMLBond.SINGLE_S, bond.getOrder());

		// moleculeTool.markSpecial(); // hope this is right
		new ValencyTool(nitroMethane).markupCommonGroups();
		Assert.assertEquals("nitro", CMLBond.DOUBLE_D, bond.getOrder());

	}

	/**
	 * Problem discovered 2007-05-16, causing an NPE when ValencyTool calls
	 * MoleculeTool. Pathology case includes a mol with sub-molecules.
	 * 
	 * @throws IOException
	 * @throws ParsingException
	 * @throws ValidityException
	 */
	@Test
	@Ignore
	// (problems finding resource)
	public void testRegression1() throws ValidityException, ParsingException,
			IOException {
		Document doc = new CMLBuilder().build(getClass().getClassLoader()
				.getResourceAsStream("valencytoolpathology1.cml.xml"));
		CMLMolecule mol = (CMLMolecule) doc.getRootElement();
		ValencyTool vt = new ValencyTool(mol);
		// This originally threw an NPE
		vt.adjustBondOrdersAndChargesToValency();
	}

	/**
	 * Problem discovered 2007-12-12 by ojd20 - in some cases ValencyTool
	 * creates invalid molecules. This was caused by ValencyTool extracting
	 * metals from a top level molecule, then trying to merge the metal bonds
	 * into a child molecule, which always fails if you have sibling molecules
	 * with metal atoms.
	 * 
	 * @throws ValidityException
	 * @throws ParsingException
	 * @throws IOException
	 */
	@Test
	public void testRegression2() throws ValidityException, ParsingException,
			IOException {
		Document doc = new CMLBuilder().build(getClass().getClassLoader()
				.getResourceAsStream("valencytoolpathology2.cml.xml"));
		CMLMolecule mol = (CMLMolecule) doc.getRootElement();
		ValencyTool vt = new ValencyTool(mol);
		vt.adjustBondOrdersAndChargesToValency();
		// In the original problem, this threw an error as the copy method tries
		// to rebuild the molecule.
		mol.copy();
	}
}
