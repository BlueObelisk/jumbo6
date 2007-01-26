package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLEnumeration;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

/** additional tools for dictionary. not fully developed
 * 
 * @author pmr
 * 
 */
public class DictionaryTool extends AbstractTool {

    Logger logger = Logger.getLogger(DictionaryTool.class.getName());
    CMLDictionary dictionary;

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(CMLDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/** constructor
     * 
     * @param dictionary
     */
    public DictionaryTool(CMLDictionary dictionary) {
        this.setDictionary(dictionary);
    }

    /** make dictionary tool from a dictionary.
     * 
     * @param dictionary
     * @return the tool
     */
    static DictionaryTool createDictionaryTool(CMLDictionary dictionary) {
        return new DictionaryTool(dictionary);
    }

    /**
     * get dictionary.
     * 
     * @return the dictionary
     */
    public CMLDictionary getDictionary() {
        return dictionary;
    }

    /** create map of dictRef occurrences with occurrence counts.
     * map is indexed by dictRef name. Objects are maps of dictRef values 
     * against occurrence counts
     * @param cml to analyse
     * @param map indexed by dictRefName; can be used to aggregate examples of cml
     * into single map
     */
    public static void extractAndAnalyseDictRef(CMLCml cml, Map<String, Map<String, Integer>> dictRefMap) {
        // counts occurences of dictRef
    	List<Node> dictRefs = CMLUtil.getQueryNodes(cml, 
    			"//"+CMLScalar.NS+"[@dictRef]", X_CML);
    	for (Node node : dictRefs) {
    		CMLScalar scalar = (CMLScalar) node;
    		String name = scalar.getDictRef();
    		name = CMLUtil.getLocalName(name);
    		String value = scalar.getXMLContent();
    		Map<String, Integer> map = (Map<String, Integer>) dictRefMap.get(name);
    		if (map == null) {
    			map = new HashMap<String, Integer>();
    			dictRefMap.put(name, map);
    		}
    		Integer ii = map.get(value);
    		if (ii == null) {
    			ii = new Integer(0);
    		}
    		map.put(value, new Integer(ii.intValue()+1));
    	}
    }

    /** analise the dictRefMap.
     * @param dictRefMap created as above.
     */
    public void analyzeMap(Map<String, Map<String, Integer>> dictRefMap) {
    	for (String key : dictRefMap.keySet()) {
    		Map<String, Integer> map = (Map<String, Integer>) dictRefMap.get(key);
    		System.out.println("\n"+key);
    		for (String s : map.keySet()) {
    			Integer ii = map.get(s);
    			System.out.println(ii.intValue()+" ... "+s);
    		}
    	}
    }

    /** update current dictionary with observed dictRefs.
     * uses observed names and values of dictRefs to add dictionary
     * entries, and where possible add types or enuemrations to these
     * @param dictRefMap as above
     */
    public void updateDictionary(Map<String, Map<String, Integer>> dictRefMap) {
    	for (String key : dictRefMap.keySet()) {
    		Map<String, Integer> map = (Map<String, Integer>) dictRefMap.get(key);
    		CMLEntry entry = dictionary.getCMLEntry(key);
    		if (entry == null) {
    			entry = this.createEntry(map, key);
    			dictionary.addEntryInOrder(entry);
    		}
    		// if this is an enumerated entry add possible values
    		if (entry.getEnumerationElements().size() > 0) {
    			try {
    				entry.updateIndex();
    			} catch (CMLRuntimeException e) {
    				entry.debug("BAD INDEX");
    				throw e;
    			}
	    		for (String s : map.keySet()) {
	    			CMLEnumeration enumeration = (CMLEnumeration) entry.getById(s);
	    			if (enumeration == null) {
	    				enumeration = new CMLEnumeration();
	    				enumeration.setCMLValue(s);
	    				enumeration.setId(s);
	    		    	entry.insertIndexableInOrder(enumeration);
	    			}
	    		}
	    		// remove dummy entry if there are other enumerations
	    		if (entry.getEnumerationElements().size() > 1) {
	    			CMLEnumeration dummyEnumeration = (CMLEnumeration) 
	    				entry.getById(CMLEnumeration.DUMMY_ENUM);
	    			if (dummyEnumeration != null) {
	    				dummyEnumeration.detach();
	    			}
	    		}
    		}
		}
    }

    /** used by updateDictionary.
     * 
     * @param map
     * @param key
     * @return
     */
    private CMLEntry createEntry(Map<String, Integer> map, String key) {
		CMLEntry entry = new CMLEntry();
		entry.setId(key);
		int maxCount = 0;
		boolean couldBeInt = true;
		boolean couldBeDate = true;
		boolean couldBeFloat = true;
		for (String s : map.keySet()) {
			int count = map.get(s).intValue();
			if (count > maxCount) {
				maxCount = count;
			}
			couldBeInt &= (Util.isInt(s));
			couldBeDate &= (Util.getCanonicalDate(s) != null);
			couldBeFloat &= (Util.isFloat(s));
		}
		boolean couldBeEnum = maxCount > 5;
		String type = XSD_STRING;
		if (couldBeInt) {
			type = XSD_INTEGER;
		} else if (couldBeFloat) {
			type = XSD_FLOAT;
		} else if (couldBeDate) {
			type = XSD_DATE;
		} else if (couldBeEnum) {
			// assume string and add dummy enumeration to flag type
			type = XSD_STRING;
			if (entry.getEnumerationElements().size() == 0) {
				CMLEnumeration enumeration = new CMLEnumeration();
				enumeration.setId(CMLEnumeration.DUMMY_ENUM);
				enumeration.setCMLValue(CMLEnumeration.DUMMY_ENUM);
				entry.addEnumeration(enumeration);
			}
		}
		entry.setDataType(type);
		return entry;
    }

    /** validate all dictRefs in CML object.
     * 
     * @param cml
     */
    public void validateDictRefsInCML(CMLCml cml) {
    	List<Node> dictRefList = CMLUtil.getQueryNodes(cml, ".//*[@dictRef]");
    	for (Node node : dictRefList) {
    		CMLElement element = (CMLElement) node;
    		try {
    			validateElementWithDictRef(element);
    		} catch (CMLRuntimeException e) {
    			e.printStackTrace();
    			System.err.println("Cannot validate: "+element.getAttributeValue("dictRef")+" / "+e);
    		}
    	}
    }
    
    private void validateElementWithDictRef(CMLElement element) {
    	String dictRef = element.getAttributeValue("dictRef");
    	if (dictRef == null) {
    		element.debug("BAD NODE");
    		throw new CMLRuntimeException("null dictRef");
    	}
    	String localName = CMLUtil.getLocalName(dictRef);
    	if (dictionary == null) {
    		throw new CMLRuntimeException("null dictionary, cannot validate");
    	}
    	CMLEntry entry = dictionary.getCMLEntry(localName.toLowerCase());
    	
    	if (entry == null) {
    		throw new CMLRuntimeException("Cannot find entry for: "+localName);
    	} else {
    		EntryTool entryTool = new EntryTool(entry);
    		entryTool.validate(element);
    	}
    }
    
}
