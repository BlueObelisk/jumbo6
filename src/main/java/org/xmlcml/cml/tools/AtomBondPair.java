package org.xmlcml.cml.tools;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.main.CMLElectron;

/** tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public abstract class AtomBondPair {
    protected int electronChange = 0;
//    private Map atomIdMap = new HashMap();
    
    protected double fullOpacity = 1;
    protected double zeroOpacity = 0;

    protected CMLElement spectator1 = null;
    	
//	private boolean containsAtomId(String id) {
//        return atomIdMap.containsKey(id);
//    }

    abstract CMLElectron createElectrons(int electronCount, String electronId);

    CMLElectron addElectron(CMLElement ab, int electronCount, String electronId) {
        CMLElectron electron = new CMLElectron();
        ab.appendChild(electron);
        electron.setCount(Math.abs(electronCount));
        electron.setId(electronId);
        return electron;
    }
    
}

