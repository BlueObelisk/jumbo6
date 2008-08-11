package org.xmlcml.cml.map;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.UnitTypeAttribute;
import org.xmlcml.cml.element.CMLUnitList;
import org.xmlcml.cml.element.CMLUnitType;
import org.xmlcml.cml.element.CMLUnitTypeList;

/**
 * a map of unitLists by namespaceURI. required for looking up entries by
 * unitAttributes *
 */
public class UnitTypeListMap extends org.xmlcml.cml.map.GenericUnitListMap {

    /**
     * 
     */
    private static final long serialVersionUID = 8720505563470997552L;

    final static Logger logger = Logger.getLogger(NamespaceToUnitListMap.class.getName());

    /**
     * constructor.
     */
    public UnitTypeListMap() {
    }

    /**
     * gets all dictionaries in directory.
     * 
     * @param dir
     *            the directory
     * @param useSubdirectories
     *            recurse into subdirectories
     * @throws IOException
     */
    public UnitTypeListMap(File dir, boolean useSubdirectories)
            throws IOException {
        getDictionaries(dir, "unitType", useSubdirectories);
    }

    /**
     * gets a unitList from a unitTypeAttribute and namespaceMap.
     * 
     * @param unitTypeAttribute
     * @return the unitList or null (if missing or not of type unitList)
     */
    public CMLUnitTypeList getUnitTypeList(UnitTypeAttribute unitTypeAttribute) {
        String namespaceURI = (unitTypeAttribute == null) ? null
                : unitTypeAttribute.getNamespaceURIString();
        CMLUnitTypeList unitTypeList = null;
        if (namespaceURI == null) {
            Object obj = this.get(namespaceURI);
            if (obj instanceof CMLUnitList) {
                unitTypeList = (CMLUnitTypeList) obj;
            }
        }
        return unitTypeList;
    }

    /**
     * gets an entry from a dictRef and namespaceMap.
     * 
     * @param unitTypeAttribute
     * @return the entry or null
     */
    public CMLUnitType getUnitType(UnitTypeAttribute unitTypeAttribute) {
        String unitTypeId = (unitTypeAttribute == null) ? null
                : unitTypeAttribute.getIdRef();
        CMLUnitTypeList unitTypeList = (unitTypeId == null) ? null : this
                .getUnitTypeList(unitTypeAttribute);
        return (unitTypeList == null) ? null : unitTypeList
                .getUnitType(unitTypeId);
    }
}
