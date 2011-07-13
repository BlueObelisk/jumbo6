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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.euclid.Util;

/**
 * tool to support bond set. not sure if useful
 * 
 * @author pmr
 * 
 */
public class BondSetTool extends AbstractTool {

    Logger LOG = Logger.getLogger(BondSetTool.class);

    CMLBondSet bondSet;
    CMLMolecule molecule;

    Map<CMLBond, CMLBond> parentTable = null;

    /**
     * constructor.
     * 
     * @param bondSet
     * @deprecated use getOrCreateTool
     */
    private BondSetTool(CMLBondSet bondSet) {
        this.bondSet = bondSet;
        if (bondSet == null) {
            throw new RuntimeException("Null bondSet");
        }
        List<CMLBond> bondList = bondSet.getBonds();
        if (bondList.size() > 0) {
            molecule = bondList.get(0).getMolecule();
        }
    }

    /** gets BondSetTool associated with bondSet.
	 * if null creates one and sets it in bondSet
	 * @param bondSet
	 * @return tool
	 */
	public static BondSetTool getOrCreateTool(CMLBondSet bondSet) {
		BondSetTool bondSetTool = (BondSetTool) bondSet.getTool();
		if (bondSetTool == null) {
			bondSetTool = new BondSetTool(bondSet);
			bondSet.setTool(bondSetTool);
		}
		return bondSetTool;
	}


    /**
     * get matched bond using atom mapping.
     * 
     * if bond atomRefs2="a1 a2" and link to="a1" from="b1" // atomId and link
     * to="a2" from="b2" // atomId and toFrom = Direction.FROM then will return
     * bond atomRefs2="b1 b2" or atomRefs2="b2 b1" in bondSet1
     * 
     * @param map
     * @param bond0
     *            bond to search with. the values in must occur in a single
     *            toFrom attribute
     * @param toFrom
     *            specifies attribute for search atoms in atomRefs2
     * @return mapped bond or null
     */
     public CMLBond getMappedBondViaAtoms(CMLMap map, CMLBond bond0, Direction toFrom) {
// FIXME
         CMLBond targetBond = null;
         CMLAtom atom0 = null;
         CMLAtom atom1 = null;
//         CMLAtom atom0 = bondSet.getAtom(bond0, 0);
//         CMLAtom atom1 = bondSet.getAtom(bond0, 1);
         if (atom0 != null && atom1 != null) {
             String targetRef0 = map.getRef(atom0.getId(), toFrom);
             String targetRef1 = map.getRef(atom1.getId(), toFrom);
             targetBond = this.getBondByAtomRefs2(targetRef0, targetRef1);
         }
         return targetBond;
     }

    /**
     * gets bond by atomRefs2.
     * 
     * @param atomId1
     *            first atomId
     * @param atomId2
     *            second atomId
     * @return the bond or null
     */
     public CMLBond getBondByAtomRefs2(String atomId1, String atomId2) {
         // FIXME
         @SuppressWarnings("unused")
         String atomHash = CMLBond.atomHash(atomId1, atomId2);
         //     bondSet;
         CMLBond bond = null;
         //(CMLBond) ((atomHash == null) ? null : 
         //    atomRefs2Table.get(atomHash));
         return bond;
     }

     public CMLBondSet getBondSetIncludingElementTypes(String[] elementTypes) {
     	return createIncludedSet(elementTypes, true);
     }

    public CMLBondSet getBondSetExcludingElementTypes(String[] elementTypes) {
    	return createIncludedSet(elementTypes, false);
    }

	private CMLBondSet createIncludedSet(String[] elementTypes, boolean include) {
		CMLBondSet includedBondSet = new CMLBondSet();
		boolean ignoreCase = false;
    	List<CMLBond> bonds = bondSet.getBonds();
    	for (CMLBond bond : bonds) {
    		String elementType0 = bond.getAtom(0).getElementType();
    		String elementType1 = bond.getAtom(1).getElementType();
    		if (include) {
    			if (
    			Util.indexOf(elementType0, elementTypes, ignoreCase) != -1 &&
        		Util.indexOf(elementType1, elementTypes, ignoreCase) != -1
        		) {
    				includedBondSet.addBond(bond);
    			}
    		} else {
    			if (
    			Util.indexOf(elementType0, elementTypes, ignoreCase) == -1 &&
        		Util.indexOf(elementType1, elementTypes, ignoreCase) == -1
        		) {
    				includedBondSet.addBond(bond);
    			}
    		} 
    	}
		return includedBondSet;
	}
}