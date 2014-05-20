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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;

/**
 * test AtomSetTool.
 * 
 * @author pm286
 * 
 */
public class BondSetToolTest {
	private static Logger LOG = Logger.getLogger(BondSetToolTest.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	MoleculeAtomBondFixture fixture = new MoleculeAtomBondFixture();
	/**
	 * setup.
	 * 
	 * @exception Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getBondSetTool() {
		CMLMolecule molecule = SMILESTool.createMolecule("CNO");
		CMLBondSet bondSet = new CMLBondSet(molecule);
		Assert.assertEquals("bonds", 7, bondSet.size());
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(bondSet);
		Assert.assertNotNull("bondSet", bondSetTool);
	}

	@Test
	public void includeBonds0() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetIncludingElementTypes(new String[] {});
		Assert.assertEquals("empty", 0, bondSet.size());
		bondSet = bondSetTool.getBondSetIncludingElementTypes(new String[] {"Pt", "Cl"});
		Assert.assertEquals("unknown elements", 0, bondSet.size());
	}

	@Test
	public void includeBonds() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetIncludingElementTypes(new String[] {"H", "C"});
		Assert.assertEquals("CH", 3, bondSet.size());
		bondSet = bondSetTool.getBondSetIncludingElementTypes(new String[] {"H", "N"});
		Assert.assertEquals("CH", 1, bondSet.size());
	}

	@Test
	public void includeBondsAll() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetIncludingElementTypes(new String[] {"H", "C", "O", "N"});
		Assert.assertEquals("all", 7, bondSet.size());
	}

	@Test
	public void excludeBonds0() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {});
		Assert.assertEquals("empty", 7, bondSet.size());
		bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {"Pt", "Cl"});
		Assert.assertEquals("unknown elements", 7, bondSet.size());
	}

	@Test
	public void excludeBonds() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {"H"});
		Assert.assertEquals("H", 2, bondSet.size());
		bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {"H", "N"});
		Assert.assertEquals("NH", 0, bondSet.size());
		bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {"C", "O"});
		Assert.assertEquals("CO", 1, bondSet.size());
	}

	@Test
	public void excludeBondsAll() {
		BondSetTool bondSetTool = BondSetTool.getOrCreateTool(
				new CMLBondSet(SMILESTool.createMolecule("CNO")));
		CMLBondSet bondSet = bondSetTool.getBondSetExcludingElementTypes(new String[] {"H", "C", "O", "N"});
		Assert.assertEquals("all", 0, bondSet.size());
	}

}
