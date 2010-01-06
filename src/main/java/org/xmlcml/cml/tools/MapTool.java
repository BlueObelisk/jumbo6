package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLLink;
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
public class MapTool extends AbstractTool {

    protected CMLMap map;
    /**
     * constructor
     * 
     * @param map
     * @deprecated
     */
    public MapTool(CMLMap map) {
        this.map = map;
    }

    /** gets MapTool associated with map.
	 * if null creates one and sets it in map
	 * @param map
	 * @return tool
	 */
	public static MapTool getOrCreateTool(CMLMap map) {
		MapTool mapTool = null;
		if (map != null) {
			mapTool = (MapTool) map.getTool();
			if (mapTool == null) {
				mapTool = new MapTool(map);
				map.setTool(mapTool);
			}
		}
		return mapTool;
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

    /**
     * returns (FROM) fromSet values mappped to toSet
     * @param direction FROM or TO
     * @return
     */
	public Map<String, String> getFromSetToSetMap(Direction direction) {
		Map<String, String> linkMap = new HashMap<String, String>();
		CMLElements<CMLLink> links = map.getLinkElements();
		for (CMLLink link : links) {
			String fromSet = link.getFromSetAttribute().getValue();
			String toSet = link.getToSetAttribute().getValue();
			String key = (direction.equals(CMLMap.Direction.FROM)) ? fromSet : toSet;
			String value = (direction.equals(CMLMap.Direction.FROM)) ? toSet : fromSet;
			linkMap.put(key, value);
		}
		return linkMap;
	}

	public void translateIds(Direction direction, Map<String, String> idMap) {
		CMLElements<CMLLink> links = map.getLinkElements();
		for (CMLLink link : links) {
			if (direction.equals(Direction.FROM)) {
				String from = link.getFrom();
				if (from != null) {
					link.setFrom(translateIds(from, idMap));
				}
				String[] fromSet = link.getFromSet();
				if (fromSet != null) {
					link.setFromSet(translateIds(fromSet, idMap));
				}
			}
			if (direction.equals(Direction.TO)) {
				String to = link.getTo();
				if (to != null) {
					link.setTo(translateIds(to, idMap));
				}
				String[] toSet = link.getToSet();
				if (toSet != null) {
					link.setToSet(translateIds(toSet, idMap));
				}
			}
		}
	}

	private String[] translateIds(String[] values, Map<String, String> idMap) {
		String[] newValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = translateIds(values[i], idMap);
		}
		return newValues;
	}

	private String translateIds(String value, Map<String, String> idMap) {
		return idMap.get(value);
	}
    
}