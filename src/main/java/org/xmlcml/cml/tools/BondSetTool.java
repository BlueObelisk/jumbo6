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

/**
 * tool to support bond set. not sure if useful
 * 
 * @author pmr
 * 
 */
public class BondSetTool extends AbstractTool {

    Logger logger = Logger.getLogger(BondSetTool.class.getName());

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

    
}