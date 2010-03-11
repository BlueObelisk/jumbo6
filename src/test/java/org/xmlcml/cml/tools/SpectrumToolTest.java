package org.xmlcml.cml.tools;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSpectrum;

public class SpectrumToolTest {

	@Test
	public void cbeckProtonNmr() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		InputStream mol1Stream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/molecule1.xml");
		CMLMolecule mol1 = (CMLMolecule) new CMLBuilder().build(mol1Stream).getRootElement();
		assertFalse(SpectrumTool.getOrCreateTool(spectrum).checkProtonNmr(mol1));
		
		InputStream mol2Stream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1mol.xml");
		CMLMolecule mol2 = (CMLMolecule) new CMLBuilder().build(mol2Stream).getRootElement().getFirstChildElement(CMLMolecule.TAG, CMLMolecule.CML_NS);
		assertTrue(SpectrumTool.getOrCreateTool(spectrum).checkProtonNmr(mol2));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void checkProtonNmrNullMolecule() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		CMLMolecule mol = null;
		SpectrumTool.getOrCreateTool(spectrum).checkProtonNmr(mol);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void checkProtonNmrNoAtoms() throws Exception {
		InputStream spectrumStream = ClassLoader.getSystemResourceAsStream("org/xmlcml/cml/tools/spectrum1.xml");
		CMLSpectrum spectrum = (CMLSpectrum) new CMLBuilder().build(spectrumStream).getRootElement();
		CMLMolecule mol = new CMLMolecule();
		SpectrumTool.getOrCreateTool(spectrum).checkProtonNmr(mol);
	}
}
