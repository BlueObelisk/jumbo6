package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;


/**
 * a map of dictionaries by namespaceURI. required for looking up entries by
 * dictRefAttributes *
 */
public class DictionaryMap extends org.xmlcml.cml.element.GenericDictionaryMap {

    /**
     * 
     */
    private static final long serialVersionUID = -773150330417542521L;

    final static Logger logger = Logger
            .getLogger(DictionaryMap.class.getName());
    
    /**
     * constructor.
     */
    public DictionaryMap() {
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
    public DictionaryMap(File dir, boolean useSubdirectories)
            throws IOException {
        super(dir, useSubdirectories, new CMLDictionary());
        throw new CMLRuntimeException("OBSOLETE");
    }

    /** gets all dictionaries via catalog.
     * @param catalogUrl
     *            the catalog
     * @throws IOException
     */
    public DictionaryMap(URL catalogUrl)
            throws IOException {
        super(catalogUrl, new CMLDictionary());
    }

}
