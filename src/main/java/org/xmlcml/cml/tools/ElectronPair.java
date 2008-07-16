package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;

import nu.xom.Element;

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
