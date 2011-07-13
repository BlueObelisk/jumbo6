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

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLElectron;

/**
 * tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class ElectronPair {
    static Logger logger = Logger.getLogger(ElectronPair.class.getName());

    static int currentReaction;
    static Element[] currentParent;
    
    CMLElectron electron1;
    CMLElectron electron2;
    int iReaction;
    /**
     * 
     * @param electron1
     * @param electron2
     * @param iReaction
     */
    public ElectronPair(CMLElectron electron1, CMLElectron electron2, int iReaction) {
        this.electron1 = electron1;
        this.electron2 = electron2;
        this.iReaction = iReaction;
    }
    
    /**
     * 
     * @param electron1
     * @param electron2
     */
    public ElectronPair(CMLElectron electron1, CMLElectron electron2) {
    	this(electron1, electron2, currentReaction);
    }
    
}
