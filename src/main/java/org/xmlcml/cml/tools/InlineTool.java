package org.xmlcml.cml.tools;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLMolecule;


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
