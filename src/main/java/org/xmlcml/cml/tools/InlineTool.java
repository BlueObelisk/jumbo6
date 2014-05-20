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

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;


/**
 * additional tools for currentBond. not fully developed
 * 
 * @author pmr
 * 
 */
public class InlineTool extends AbstractTool {

	private CMLMolecule molecule;
	private String inline;
	private CMLAtom currentAtom;
	private CMLBond currentBond;
//	private Ring currentRing;
	
    /** constructor
     */
    public InlineTool() {
    }
    
    /**
     * @param inline
     */
    public void parseInline(String inline) {
    	molecule = new CMLMolecule();
    	currentAtom = null;
		currentBond = null; 
//		currentRing = null;
		int len = inline.length();
//		int ringSize = 0;
		for (int i = 0; i < len; i++) {
			char ch = inline.charAt(i);
			if (ch == '0') {
				// no clear message
			} else if (ch >= '1' && ch <= '2') {
				if (i == len-1) {
					throw new RuntimeException("trailing digit");
				} else if (!Character.isDigit(ch)) {
					throw new RuntimeException("cannot make 1- or 2-membered ring");
				}
//				ringSize = 10*(ch-'0') + (inline.charAt(++i)-'0');
			} else if (ch > '2' && ch <= '9') {
//				ringSize = ch-'0';
			} else if (ch == 'U') {
				incrementCurrentAtom();
				incrementCurrentBond();
			}
		}
    }
    
    private void incrementCurrentAtom() {
    	
    }
    
    private void incrementCurrentBond() {
    	
    }
    
    /**
     * @return molecule
     */
    public CMLMolecule getMolecule() {
    	return molecule;
    }

	/**
	 * @return the currentAtom
	 */
	public CMLAtom getCurrentAtom() {
		return currentAtom;
	}

	/**
	 * @return the currentBond
	 */
	public CMLBond getCurrentBond() {
		return currentBond;
	}

	/**
	 * @return the inline
	 */
	public String getInline() {
		return inline;
	}
}	
