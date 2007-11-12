package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLElectron;

/** tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public abstract class AtomBondPair {
    int electronChange = 0;
    Map atomIdMap = new HashMap();
    
    double fullOpacity = 1;
    double zeroOpacity = 0;

    CMLElement spectator1 = null;
    	
	boolean containsAtomId(String id) {
        return atomIdMap.containsKey(id);
    }

    abstract CMLElectron createElectrons(int electronCount, String electronId);

    CMLElectron addElectron(CMLElement ab, int electronCount, String electronId) {
        CMLElectron electron = new CMLElectron();
        ab.appendChild(electron);
        electron.setCount(Math.abs(electronCount));
        electron.setId(electronId);
        return electron;
    }
    
}

