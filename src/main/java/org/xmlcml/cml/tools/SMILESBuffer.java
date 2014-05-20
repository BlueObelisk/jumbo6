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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.EC;

public class SMILESBuffer {
private static Logger LOG = Logger.getLogger(SMILESBuffer.class);

	private static Map<String, String> groupMap = new HashMap<String, String>();
	static {
		groupMap.put("Et", "(CC)");
		groupMap.put("iPr", "(C(C)C)");
		groupMap.put("tosyl", "(OS(=O)(=O)c1cccc(C)cc1)");
		groupMap.put("Ac", "(C(=O)C)");
	}
	
	public static char GROUP_DELIM = CMLConstants.C_DOLLAR;
	
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
	private boolean validateSMILES;

	private boolean saveValidate;
	private StringBuffer dollarBuffer = null;
	
	public SMILESBuffer() {
		buffer = new StringBuilder();
		smilesTool = new SMILESTool();
		caret = 0;
		validateSMILES = true;
		dollarBuffer = null;
	}

//	/** deletes end character.
//	 * normally backspace
//	 * if repeated leads to zero-length string and further operations are no-ops
//	 * does not check SMILES validity
//	 */
//	public void deleteEndChar() {
//		int len = buffer.length();
//		if (len > 0) {
//			buffer.deleteCharAt(len-1);
//		}
//		setCaret(caret);
//	}
	
	/** deletes character at specified position.
	 * does not check SMILES validity
	 * leaves caret before deleted character
	 */
	public void deleteCharAt(int i) {
		setCaret(i);
		deleteCurrentCharacter();
	}
	
	public void deleteCurrentCharacter() {
		if (caret > 0) {
			buffer.deleteCharAt(caret-1);
			setCaret(caret - 1);
		}
	}
	
	public void shiftCaret(int i) {
		setCaret(caret + i);
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
		try {
			if (c == EC.C_LBRAK) {
				saveValidate = validateSMILES;
				validateSMILES = false;
				insertCharAndAdjustCaret(EC.C_LBRAK);
				insertCharAndAdjustCaret(EC.C_RBRAK);
				validateSMILES = saveValidate;
				// position inside brackets
				setCaret(caret - 1);
			} else if (c == GROUP_DELIM) {
				if (dollarBuffer == null) {
					dollarBuffer = new StringBuffer();
				} else {
					String group = dollarBuffer.toString();
					String content = lookup(group);
					if (content != null) {
						insertStringAndAdjustCaret(content);
					}
					dollarBuffer = null;
				}
			} else if (dollarBuffer != null) {
				dollarBuffer.append(c);
			} else if (Character.isDigit(c)) {
				saveValidate = validateSMILES;
				validateSMILES = false;
				LOG.trace("?..."+caret);
				insertCharAndAdjustCaret(c);
				LOG.trace("a..."+caret);
				insertCharAndAdjustCaret('C');
				LOG.trace("b..."+caret);
				insertCharAndAdjustCaret(c);
				LOG.trace("c..."+caret);
				validateSMILES = saveValidate;
				setCaret(caret - 2);
			} else {
				insertCharAndAdjustCaret(c);
			}
		} catch (RuntimeException e) {
			if (validateSMILES) {
				throw e;
			} else {
				buffer = new StringBuilder(saveBuffer);
			}
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
		try {
			insertStringAndAdjustCaret(s);
		} catch (RuntimeException e) {
			if (validateSMILES) {
				throw e;
			} else {
				buffer = new StringBuilder(saveBuffer);
			}
		}
	}
	
	private void insertCharAndAdjustCaret(char c) {
		if (c >= 32) { 
			insertStringAndAdjustCaret(""+c);
		}
	}

	private void insertStringAndAdjustCaret(String s) throws RuntimeException {
		LOG.trace(">>"+buffer.toString()+"..("+caret+").."+s);
		buffer.insert(caret, s);
		LOG.trace("<<"+buffer.toString()+"..("+caret+").."+s);
		caret += s.length();
		molecule = null;
		// parse to SMILES. if invalid revert to last string
		if (validateSMILES) {
			smilesTool.parseSMILES(buffer.toString());
			molecule = smilesTool.getMolecule();
		}
//		LOG.debug(buffer.toString());
	}
	
	public int getCaret() {
		return caret;
	}

	/** sets caret.
	 * if outside range of buffer(0 ... length)
	 * sets to current limit
	 * @param caret
	 */
	public void setCaret(int caret) {
		if (caret < 0) {
			this.caret = 0;
		} else if (caret > buffer.length()) {
			this.caret = buffer.length();
		} else {
			this.caret = caret;
		}
	}

	public CMLMolecule getMolecule() {
		return molecule;
	}
	
	public String getSMILES() {
		return buffer.toString();
	}

	public boolean isValidateSMILES() {
		return validateSMILES;
	}

	public void setValidateSMILES(boolean validateSMILES) {
		this.validateSMILES = validateSMILES;
	}
}
