package org.xmlcml.cml.tools;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.util.TstUtils;

public class SMILESBufferTest {

	@Test
	public void testSMILESBuffer() {
		SMILESBuffer buffer = new SMILESBuffer();
		Assert.assertEquals("caret", 0, buffer.getCaret());
		buffer.setCaret(1);
		Assert.assertEquals("normalize caret", 0, buffer.getCaret());
		buffer.setCaret(-1);
		Assert.assertEquals("normalize caret", 0, buffer.getCaret());
	}

	@Test
	public void testSMILESBuffer1() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addString("CO");
		CMLMolecule molecule = buffer.getMolecule();
		CMLMolecule expected = (CMLMolecule)TstUtils.parseValidString(
				"<molecule xmlns='http://www.xml-cml.org/schema' " +
				"xmlns:cmlx='http://www.xml-cml.org/schema/cmlx' cmlx:explicitHydrogens='true'>"+
				"<atomArray>"+
				"<atom id='a1' elementType='C'/>"+
				"<atom id='a2' elementType='O'/>"+
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
		TstUtils.assertEqualsIncludingFloat("CO", expected, molecule, true, 0.001);
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
	public void testLookup() {
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
	public void testRGroups() {
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
	
	@Test
	public void testBrackets() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addChar('C');
		Assert.assertEquals("add char", "C", buffer.getSMILES());
		Assert.assertEquals("add char", 1, buffer.getCaret());
		buffer.addChar('O');
		Assert.assertEquals("add char", "CO", buffer.getSMILES());
		Assert.assertEquals("add char", 2, buffer.getCaret());
		buffer.shiftCaret(-1);
		Assert.assertEquals("add char", 1, buffer.getCaret());
		buffer.insertChar('N');
		Assert.assertEquals("add char", "CNO", buffer.getSMILES());
		Assert.assertEquals("add char", 2, buffer.getCaret());
		buffer.insertChar('(');
		Assert.assertEquals("add char", "CN()O", buffer.getSMILES());
		// caret is positioned inside brackets
		Assert.assertEquals("add char", 3, buffer.getCaret());
	}
	
	@Test
	public void testRings() {
		SMILESBuffer buffer = new SMILESBuffer();
		buffer.addChar('C');
		Assert.assertEquals("add char", "C", buffer.getSMILES());
		Assert.assertEquals("add char", 1, buffer.getCaret());
		buffer.addChar('9');
		Assert.assertEquals("add char", "C9C9", buffer.getSMILES());
	}
	
	private Element parseValidString(String s) {
		Element element = null;
		try {
			element = new CMLBuilder().parseString(s);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return element;
	}

}
