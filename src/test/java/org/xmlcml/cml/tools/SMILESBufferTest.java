package org.xmlcml.cml.tools;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.TstBase;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.util.TestUtils;

public class SMILESBufferTest {

	@Test
	public void testSMILESBuffer() {
		SMILESBuffer buffer = new SMILESBuffer();
		Assert.assertEquals("caret", 0, buffer.getCaret());
		try {
			buffer.setCaret(1);
			Assert.fail("should trap illegal caret");
		} catch (RuntimeException e) {
			
		}
		try {
			buffer.setCaret(-1);
			Assert.fail("should trap illegal caret");
		} catch (RuntimeException e) {
			
		}
	}

	@Test
	public void testSMILESBuffer1() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addString("CO");
		CMLMolecule molecule = buffer.getMolecule();
		CMLMolecule expected = (CMLMolecule) TstBase.parseValidString(
				"<molecule xmlns='http://www.xml-cml.org/schema'>"+
				"<atomArray>"+
				"<atom id='a1' elementType='C' hydrogenCount='3'/>"+
				"<atom id='a2' elementType='O' hydrogenCount='1'/>"+
				"<atom id='a1_h1' elementType='H'/>"+
				"<atom id='a1_h2' elementType='H'/>"+
				"<atom id='a1_h3' elementType='H'/>"+
				"<atom id='a2_h1' elementType='H'/>"+
				"</atomArray>"+
				"<bondArray>"+
				"<bond atomRefs2='a1 a2' id='a1_a2' order='1'/>"+
				"<bond atomRefs2='a1 a1_h1' id='a1_a1_h1' order='1'/>"+
				"<bond atomRefs2='a1 a1_h2' id='a1_a1_h2' order='1'/>"+
				"<bond atomRefs2='a1 a1_h3' id='a1_a1_h3' order='1'/>"+
				"<bond atomRefs2='a2 a2_h1' id='a2_a2_h1' order='1'/>"+
				"</bondArray>"+
				"</molecule>"
		);
		TestUtils.assertEqualsIncludingFloat("CO", expected, molecule, true, 0.001);
	}

	@Test
	public void testSMILESBuffer2() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addString("CO");
		buffer.insertString(1, "N");
		String smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CNO", smiles);
		buffer.insertString("S");
		smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CNSO", smiles);
		buffer.addString("Cl");
		smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CNSOCl", smiles);
		buffer.setCaret(1);
		buffer.insertString("(I)");
		smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "C(I)NSOCl", smiles);
	}
	
	@Test
	public void testSMILESBuffer3() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addString("CO");
		buffer.insertString(1, "N");
		String smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CNO", smiles);
		buffer.insertString(SMILESBuffer.lookup("Ac"));
		smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CN(C(=O)C)O", smiles);
		buffer.addString(SMILESBuffer.lookup("tosyl"));
		smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "CN(C(=O)C)O(OS(=O)(=O)c1cccc(C)cc1)", smiles);
	}
	
	@Test
	public void testSMILESBuffer4() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addString("CO");
		try {
			buffer.insertString(1, "X");
			Assert.fail("should throw bad element");
		} catch (RuntimeException e) {
			// bad element
		}
		try {
			buffer.insertString(1, ")");
			Assert.fail("should throw unbalanced brackets");
		} catch (RuntimeException e) {
		}
		buffer = new SMILESBuffer();
		buffer.addString("CO");
		buffer.insertString(0, "[R]");
		String smiles = buffer.getSMILES();
		Assert.assertEquals("insert", "[R]CO", smiles);
		try {
			buffer.addString("R");
			Assert.fail("should throw bad element");
		} catch (RuntimeException e) {
			// bad element
		}
	}
}
