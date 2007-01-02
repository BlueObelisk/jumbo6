package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.euclid.Util;


/**
 * additional tools for bond. not fully developed
 * 
 * @author pmr
 * 
 */
public class MapTool {

    protected CMLMap map;
    /**
     * constructor
     * 
     * @param map
     */
    public MapTool(CMLMap map) {
        this.map = map;
    }

    /**
     * make bond tool from a bond.
     * 
     * @param bond
     * @return the tool
     */
    static MapTool createMapTool(CMLMap map) {
        return new MapTool(map);
    }

    
    /** get matched bond using atom mapping.
     * does  not work with sets
     * @deprecated
     * NYI
     * if bond atomRefs2="a1 a2"
     * and link to="a1" from="b1" // atomId
     * and link to="a2" from="b2" // atomId
     * and toFrom = FROM
     * then will return bond atomRefs2="b1 b2" or atomRefs2="b2 b1" in molecule1
     * 
     * @param bond0 bond to search with. the values in  must occur in a single toFrom attribute
     * @param molecule1 containing result bond
     * @param toFrom specifies attribute for search atoms in atomRefs2
     * @return mapped bond or null
     */
    public CMLBond getMappedBondViaAtoms(CMLBond bond0, CMLMolecule molecule1, Direction toFrom) {
        CMLBond targetBond = null;
        Util.throwNYI();
//        BondTool bondTool = (bond0 == null || molecule1 == null) ? (BondTool) null : (BondTool) BondToolImpl.getTool(bond0);
//        CMLAtom atom0 = bondTool.getAtom(0);
//        CMLAtom atom1 = bondTool.getAtom(1);
//        if (atom0 != null && atom1 != null) {
//            String targetRef0 = getRef(atom0.getId(), toFrom);
//            String targetRef1 = getRef(atom1.getId(), toFrom);
//            targetBond = MoleculeToolImpl.getTool(molecule1).getBond(targetRef0, targetRef1);
//        }
        return targetBond;
    }
    
    /** get matched bond using atom mapping.
     * NYI
     * @deprecated
     * if bond atomRefs2="a1 a2"
     * and link to="a1" from="b1" // atomId
     * and link to="a2" from="b2" // atomId
     * and toFrom = FROM
     * then will return bond atomRefs2="b1 b2" or atomRefs2="b2 b1" in bondSet1
     * 
     * @param bond0 bond to search with. the values in  must occur in a single toFrom attribute
     * @param bondSet1 containing result bond
     * @param toFrom specifies attribute for search atoms in atomRefs2
     * @return mapped bond or null
     */
    public CMLBond getMappedBondViaAtoms(CMLBond bond0, CMLBondSet bondSet1, String toFrom) {
        CMLBond targetBond = null;
        Util.throwNYI();
//        BondTool bondTool = (bond0 == null || bondSet1 == null) ? (BondTool) null : (BondTool) BondToolImpl.getTool(bond0);
//        CMLAtom atom0 = bondTool.getAtom(0);
//        CMLAtom atom1 = bondTool.getAtom(1);
//        if (atom0 != null && atom1 != null) {
//            String targetRef0 = getRef(atom0.getId(), toFrom);
//            String targetRef1 = getRef(atom1.getId(), toFrom);
//            targetBond = BondSetToolImpl.getTool(bondSet1).getBondByAtomRefs2(targetRef0, targetRef1);
//        }
        return targetBond;
    }
    
}