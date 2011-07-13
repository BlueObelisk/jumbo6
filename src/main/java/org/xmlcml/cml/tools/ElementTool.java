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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
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