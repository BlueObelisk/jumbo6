package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.molutil.ChemicalElement;

/**
 * tool for managing crystals
 * 
 * @author pmr
 * 
 */
public abstract class ElementTool extends AbstractTool {
    final static Logger logger = Logger.getLogger(ElementTool.class.getName());

    /** filter atoms by element set.
     * 
     * @param atomList
     * @param elementSet
     * @return atoms whose elements are in set
     */
    public static List<CMLAtom> filterList(List<CMLAtom> atomList, Set<CMLElement> elementSet) {
        List<CMLAtom> newAtomList = new ArrayList<CMLAtom>();
        for (CMLAtom atom : atomList) {
            ChemicalElement element = atom.getChemicalElement();
            if (element != null) {
                if (elementSet.contains(element)) {
                    newAtomList.add(atom);
                }
            }
        }
        return newAtomList;
    }
};