package org.xmlcml.cml.map;

import java.io.IOException;
import java.net.URL;
import org.apache.log4j.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.interfacex.GenericDictionary;
import org.xmlcml.cml.interfacex.IDictionary;


/**
 * a map of dictionaries by namespaceURI. required for looking up entries by
 * dictRefAttributes *
 */
public class DictionaryMap extends org.xmlcml.cml.attribute.GenericDictionaryMap {

    /**
     * 
     */
    private static final long serialVersionUID = -773150330417542521L;

    final static Logger logger = Logger.getLogger(DictionaryMap.class.getName());
    
    /**
     * constructor.
     */
    public DictionaryMap() {
    }

    /** dictionary map.
     * 
     * @param url
     * @param genericDictionary
     * @throws IOException
     */
    public DictionaryMap(URL url, GenericDictionary genericDictionary) throws IOException {
    	this();
    	if (!(genericDictionary instanceof IDictionary)) {
    		throw new CMLRuntimeException("Generic dictionary must be instance of IDictionary");
    	}
    	super.mapAllDictionariesInCatalog(url, genericDictionary);
    }
//    /**
//     * gets all dictionaries in directory.
//     * 
//     * @param dir
//     *            the directory
//     * @param useSubdirectories
//     *            recurse into subdirectories
//     * @throws IOException
//     */
//    public DictionaryMap(File dir, boolean useSubdirectories)
//            throws IOException {
//        super(dir, useSubdirectories, new CMLDictionary());
//        throw new CMLRuntimeException("OBSOLETE");
//    }

//    /** gets all dictionaries via catalog.
//     * @param catalogUrl
//     *            the catalog
//     * @throws IOException
//     */
//    public DictionaryMap(URL catalogUrl) throws IOException {
//        super(catalogUrl, new CMLDictionary());
//    }

}
