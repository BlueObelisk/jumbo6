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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSpectrum;

public class SpectrumToolTest {

	@Test
	public void checkIntegralAgainstProtonCount() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		InputStream mol1Stream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/molecule1.xml");
		CMLMolecule mol1 = (CMLMolecule) new CMLBuilder().build(mol1Stream).getRootElement();
		assertFalse(SpectrumTool.getOrCreateTool(spectrum).checkIntegralAgainstProtonCount(mol1));
		
		InputStream mol2Stream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1mol.xml");
		CMLMolecule mol2 = (CMLMolecule) new CMLBuilder().build(mol2Stream).getRootElement().getFirstChildElement(CMLMolecule.TAG, CMLMolecule.CML_NS);
		assertTrue(SpectrumTool.getOrCreateTool(spectrum).checkIntegralAgainstProtonCount(mol2));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void checkIntegralAgainstProtonCountNullMolecule() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		CMLMolecule mol = null;
		SpectrumTool.getOrCreateTool(spectrum).checkIntegralAgainstProtonCount(mol);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void checkIntegralAgainstProtonCountNoAtoms() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		CMLMolecule mol = new CMLMolecule();
		SpectrumTool.getOrCreateTool(spectrum).checkIntegralAgainstProtonCount(mol);
	}
	
	
	@Test
	public void calculateIntegralSum() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		SpectrumTool tool = SpectrumTool.getOrCreateTool(spectrum);
		Assert.assertEquals(11d, tool.calculateIntegralSum(), 0.0001);
	}
}
