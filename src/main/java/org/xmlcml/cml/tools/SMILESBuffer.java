package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.C_LBRAK;
import static org.xmlcml.euclid.EuclidConstants.C_RBRAK;
import static org.xmlcml.euclid.EuclidConstants.C_STAR;
import static org.xmlcml.euclid.EuclidConstants.S_DOLLAR;

import java.util.HashMap;
import java.util.Map;

import org.xmlcml.cml.element.CMLMolecule;

public class SMILESBuffer {

	private static Map<String, String> groupMap = new HashMap<String, String>();
	static {
		groupMap.put("Et", "(CC)");
		groupMap.put("iPr", "(C(C)C)");
		groupMap.put("tosyl", "(OS(=O)(=O)c1cccc(C)cc1)");
		groupMap.put("Ac", "(C(=O)C)");
	}
	
	public static String lookup(String s) {
		return (s == null) ? null : groupMap.get(s);
	}
	
	public static void addGroup(String name, String smiles) {
		groupMap.put(name, smiles);
	}
	
	private StringBuilder buffer;
	private int caret;
	private SMILESTool smilesTool;
	private CMLMolecule molecule;
	private String saveBuffer;
	
	public SMILESBuffer() {
		buffer = new StringBuilder();
		smilesTool = new SMILESTool();
		caret = 0;
	}
	
	public void addChar(char c) {
		insertChar(buffer.length(), c);
	}
	
	public void insertChar(int pos, char c) {
		setCaret(pos);
		insertChar(c);
	}
	
	public void insertChar(char c) {
		saveBuffer();
		if (c == C_LBRAK) {
			insertCharAndAdjustCaret(C_LBRAK);
			insertCharAndAdjustCaret(C_STAR);
			insertCharAndAdjustCaret(C_RBRAK);
		} else {
			insertCharAndAdjustCaret(c);
		}
	}

	public void clearBuffer() {
		buffer = new StringBuilder();
	}
	private void saveBuffer() {
		saveBuffer = buffer.toString();
	}
	
	public void addString(String s) {
		insertString(buffer.length(), s);
	}
	
	public void insertString(int pos, String s) {
		setCaret(pos);
		insertString(s);
	}
	
	public void insertString(String s) {
		saveBuffer();
		if (s.startsWith(S_DOLLAR)) {
			String ss = lookup(s.substring(1));
			insertStringAndAdjustCaret(ss);
		} else {
			insertStringAndAdjustCaret(s);
		}
	}
	
	private void insertCharAndAdjustCaret(char c) {
		insertStringAndAdjustCaret(""+c);
	}

	private void insertStringAndAdjustCaret(String s) {
		buffer.insert(caret, s);
		caret += s.length();
		molecule = null;
		// parse to SMILES. if invalid revert to last string
		try {
			smilesTool.parseSMILES(buffer.toString());
			molecule = smilesTool.getMolecule();
		} catch (RuntimeException e) {
			buffer = new StringBuilder(saveBuffer);
		}
	}
	
	public int getCaret() {
		return caret;
	}

	public void setCaret(int caret) {
		if (caret > buffer.length() || caret < 0) {
			throw new RuntimeException("Caret not in string: "+caret);
		}
		this.caret = caret;
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}
	
	public String getSMILES() {
		return buffer.toString();
	}
}
