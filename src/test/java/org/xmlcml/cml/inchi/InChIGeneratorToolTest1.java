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

package org.xmlcml.cml.inchi;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Point3;
import org.xmlcml.molutil.ChemicalElement.AS;

public class InChIGeneratorToolTest1 {

	private final static Logger LOG = Logger.getLogger(InChIGeneratorToolTest1.class);
	@Test
	public void testDummy() {
		Assert.assertTrue(true);
	}

	// requires svn cml inchi on classpath
	@Test
	@Ignore
	public void inchiGeneratorTest() {
		InChIGeneratorTool inChIGeneratorTool = new InChIGeneratorTool();
		CMLMolecule co = createCO();
		String inchi = InChIGeneratorTool.generateInChI(co);
		LOG.debug(inchi);
	}
	
	private CMLMolecule createCO() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom a1 = new CMLAtom("a1", AS.C);
		a1.setXYZ3(new Point3(0.0, 0.0, 0.0));
		CMLAtom a2 = new CMLAtom("a2", AS.O);
		a1.setXYZ3(new Point3(1.0, 0.0, 0.0));
		molecule.addAtom(a1);
		molecule.addAtom(a2);
		CMLBond b12 = new CMLBond(a1, a2);
		b12.setOrder(CMLBond.DOUBLE_D);
		molecule.addBond(b12);
		molecule.debug("mol");
		return molecule;
	}

}
