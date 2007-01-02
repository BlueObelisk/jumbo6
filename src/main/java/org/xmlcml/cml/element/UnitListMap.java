package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;

/**
 * a map of unitLists by namespaceURI. required for looking up entries by
 * unitAttributes *
 */
public class UnitListMap extends org.xmlcml.cml.element.GenericDictionaryMap {

    /**
     * 
     */
    private static final long serialVersionUID = -7565950580665146408L;

    final static Logger logger = Logger.getLogger(UnitListMap.class.getName());

    /**
     * constructor.
     */
    public UnitListMap() {
    }

    /**
     * gets all dictionaries in directory. this will process BOTH unit and
     * unitType dictionaries
     * 
     * @param dir
     *            the directory
     * @param useSubdirectories
     *            recurse into subdirectories
     * @throws IOException
     */
    public UnitListMap(File dir, boolean useSubdirectories) throws IOException {
        super(dir, useSubdirectories, new CMLUnitList());
    }

    /**
     * gets a unitList from a unitAttribute and namespaceMap.
     * 
     * @param unitAttribute
     * @return the unitList or null
     */
    public CMLUnitList getUnitList(UnitAttribute unitAttribute) {
        String namespaceURI = (unitAttribute == null) ? null : unitAttribute
                .getNamespaceURIString();
        return (CMLUnitList) ((namespaceURI == null) ? null : this
                .get(namespaceURI));
    }

    /**
     * gets a unitTypeList from a unitTypeAttribute and namespaceMap.
     * 
     * @param unitTypeAttribute
     * @return the unitList or null
     */
    public CMLUnitTypeList getUnitTypeList(UnitTypeAttribute unitTypeAttribute) {
        String namespaceURI = (unitTypeAttribute == null) ? null
                : unitTypeAttribute.getNamespaceURIString();
        return (CMLUnitTypeList) ((namespaceURI == null) ? null : this
                .get(namespaceURI));
    }

    /**
     * gets an entry from a dictRef and namespaceMap. side effect: If the
     * unitList containing the unit does not have a non-null unitListMap this
     * method will set the unitListMap to this.
     * 
     * @param unitAttribute
     * @return the unit or null
     */
    public CMLUnit getUnit(UnitAttribute unitAttribute) {
        String entryId = (unitAttribute == null) ? null : unitAttribute
                .getIdRef();
        CMLUnitList unitList = (entryId == null) ? null : this
                .getUnitList(unitAttribute);
        if (unitList.getUnitListMap() == null) {
            throw new CMLRuntimeException("FIX units");
//            unitList.setUnitListMap(this);
        }
        return ((unitList == null) ? null : unitList.getUnit(entryId));
    }

    /**
     * gets an entry from a unitTypeAttribute.
     * 
     * @param unitTypeAttribute
     * @return the unitType or null
     */
    public CMLUnitType getUnitType(UnitTypeAttribute unitTypeAttribute) {
        String entryId = (unitTypeAttribute == null) ? null : unitTypeAttribute
                .getIdRef();
        CMLUnitTypeList unitTypeList = null;
        if (entryId == null) {
            unitTypeList = this.getUnitTypeList(unitTypeAttribute);
        }
        return ((unitTypeList == null) ? null : unitTypeList
                .getUnitType(entryId));
    }

}
