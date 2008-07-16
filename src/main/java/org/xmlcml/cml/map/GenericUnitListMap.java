package org.xmlcml.cml.map;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLUnit;
import org.xmlcml.cml.element.CMLUnitList;

/**
 * a map of unitLists by namespaceURI. required for looking up entries by
 * unitAttributes *
 */
public class GenericUnitListMap extends HashMap<String, CMLUnitList> implements CMLConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 4620097558217053150L;

    final static Logger logger = Logger.getLogger(GenericUnitListMap.class
            .getName());

    protected GenericUnitListMap() {
    }

    /**
     * gets all dictionaries in directory.
     * 
     * @param dir
     *            the directory
     * @param type
     * @param useSubdirectories
     *            recurse into subdirectories
     * @throws IOException
     */
    public GenericUnitListMap(File dir, String type, boolean useSubdirectories)
            throws IOException {
        getDictionaries(dir, type, useSubdirectories);
    }

    /**
     * gets a unitList from a unitAttribute and namespaceMap.
     * 
     * @param unitAttribute
     * @return the unitList or null
     */
    public CMLUnitList getUnitList(UnitsAttribute unitAttribute) {
        String namespaceURI = (unitAttribute == null) ? null : unitAttribute
                .getNamespaceURIString();
        return (namespaceURI == null) ? null : this.get(namespaceURI);
    }

    /**
     * gets an entry from a dictRef and namespaceMap.
     * 
     * @param unitAttribute
     * @return the entry or null
     */
    public CMLUnit getUnit(UnitsAttribute unitAttribute) {
        String entryId = (unitAttribute == null) ? null : unitAttribute
                .getIdRef();
        CMLUnitList unitList = (entryId == null) ? null : this
                .getUnitList(unitAttribute);
        return (CMLUnit) ((unitList == null) ? null : unitList.getUnit(entryId));
    }

    protected void getDictionaries(File dir, String type,
            boolean useSubdirectories) throws IOException {
        String[] files = dir.list();
        if (files != null) {
            for (String file : files) {
                if (file.endsWith(XML_SUFF)) {
                    File f = new File(dir, file);
                    CMLUnitList unitList = null;
                    try {
                        unitList = CMLUnitList.createUnitList(f);
                    } catch (CMLRuntimeException e) {
                        // non-wellformed unitList
                        System.out.println("Badly formed: " + f);
                    } catch (CMLException e) {
                        e.printStackTrace();
                        System.out.println("Cannot create unitList: " + e);
                    }
                    if (unitList != null) {
                        if (unitList.getType() != null
                                && unitList.getType().equals(type)) {
                            this.put(unitList.getNamespace(), unitList);
                        }
                    } else {
                        System.out.println("Cannot read units from: " + file);
                    }
                } else if (useSubdirectories) {
                    Map<String, CMLUnitList> childUnitListMap = new GenericUnitListMap(
                            new File(dir, file), type, useSubdirectories);
                    if (childUnitListMap != null) {
                        for (String s : childUnitListMap.keySet())
                            this.put(s, childUnitListMap.get(s));
                    }
                }
            }
        }
    }

    /**
     * gets a unitList from a namespace.
     * 
     * @param namespaceURI
     * @return unitList
     */
    public CMLUnitList getUnitList(String namespaceURI) {
        return this.get(namespaceURI);
    }
}
