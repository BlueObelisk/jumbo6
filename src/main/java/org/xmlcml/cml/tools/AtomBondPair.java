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

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLElectron;

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

