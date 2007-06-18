package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLDefinition;
import org.xmlcml.cml.element.CMLDescription;
import org.xmlcml.cml.element.CMLDictionary;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLModule;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLVector3;

/** additional tools for dictionary. not fully developed
 * 
 * @author pmr
 * 
 */
public class DictionaryTool extends AbstractTool {

    Logger logger = Logger.getLogger(DictionaryTool.class.getName());
    private CMLDictionary dictionary;
    private String prefix;
    private boolean failOnError;
    private String delimiter;
    private boolean ignoreCaseOfEnumerations;

    Map<CMLEntry, EntryTool> entryToolMap;
	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(CMLDictionary dictionary) {
		this.dictionary = dictionary;
		if (dictionary == null) {
			new Exception().printStackTrace();
			throw new CMLRuntimeException("NULL dictionary");
		}
	}
	
	private void ensureEntryToolMap() {
		if (entryToolMap == null) {
			entryToolMap = new HashMap<CMLEntry, EntryTool>();

		}
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

    /**
	 * @return the failOnError
	 */
	public boolean isFailOnError() {
		return failOnError;
	}

	/**
	 * @param failOnError the failOnError to set
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	

	/**
	 * @return the ignoreCaseOfEnumerations
	 */
	public boolean isIgnoreCaseOfEnumerations() {
		return ignoreCaseOfEnumerations;
	}

	/**
	 * @param ignoreCaseOfEnumerations the ignoreCaseOfEnumerations to set
	 */
	public void setIgnoreCaseOfEnumerations(boolean ignoreCaseOfEnumerations) {
		this.ignoreCaseOfEnumerations = ignoreCaseOfEnumerations;
	}

	/** create map of dictRef occurrences with occurrence counts.
     * map is indexed by dictRef name. Objects are maps of dictRef values 
     * against occurrence counts
     * @param cmlElement to analyse
     */
    public void extractAndAnalyseDictRef(CMLElement cmlElement) {

    	//property/parameter parents with scalar/array/matrix children
    	
    	// element has dictRef and child scalar does not
    	String xpath = "//"+"*[" +
			"(self::"+CMLProperty.NS+" or self::"+CMLParameter.NS+") " +
			"and @"+DictRefAttribute.NAME+" and " +
			"count(*[not(@"+DictRefAttribute.NAME+")]) > 0]";
    	List<Node> dictRefs = CMLUtil.getQueryNodes(cmlElement, xpath, X_CML);
    	for (Node node : dictRefs) {
    		CMLElement element = (CMLElement) node;
    		String parentTerm = EntryTool.createTerm(element);
    		analyzeDictRefOnParent(element, parentTerm);
    	}
    	
    	// element without dictRef on parent
    	xpath = "//"+"*[" +
		"(self::"+CMLProperty.NS+" or self::"+CMLParameter.NS+") " +
			"and not(@"+DictRefAttribute.NAME+")]/*" +
			"[@"+DictRefAttribute.NAME+"]";
    	dictRefs = CMLUtil.getQueryNodes(cmlElement, xpath, X_CML);
    	for (Node node : dictRefs) {
    		CMLElement element = (CMLElement) node;
    		analyzeDictRefOnChild(element);
    	}
    	
    	//non-property/parameter parents with scalar/array/matrix children
    	
    	// element has dictRef and child scalar does not
    	xpath = "//"+"*[" +
			"(not(self::"+CMLProperty.NS+" or" +
			" self::"+CMLParameter.NS+" or " +
			" self::"+CMLModule.NS+")) \n" +
			"and " +
			"count(*" +
			"[(self::"+CMLProperty.NS+" or" +
			" self::"+CMLParameter.NS+" or " +
			" self::"+CMLModule.NS+") and " +
			"@"+DictRefAttribute.NAME+"]) > 0]";
    	dictRefs = CMLUtil.getQueryNodes(cmlElement, xpath, X_CML);
    	for (Node node : dictRefs) {
    		CMLElement element = (CMLElement) node;
    		analyzeDictRefOnChild(element);
    	}
    	
    	//property/parameter parents with value attribute
    	
    	// element has dictRef and child scalar does not
    	xpath = "//"+"*[" +
			"(self::"+CMLProperty.NS+" or" +
			" self::"+CMLParameter.NS+") and " +
			"@value]";
    	dictRefs = CMLUtil.getQueryNodes(cmlElement, xpath, X_CML);
    	for (Node node : dictRefs) {
    		CMLElement element = (CMLElement) node;
    		analyzeValue(element);
    	}
    }
    
	private void analyzeDictRefOnParent(CMLElement cmlElement, String parentTerm) {
		
		String dictRef = cmlElement.getAttributeValue(DictRefAttribute.NAME);
		dictRef = CMLUtil.getLocalName(dictRef);
    	// children without dictRef
    	String nonDictRefChildS = "*[not(@"+
		DictRefAttribute.NAME+")]";
		List<Node> nonDictRefChilds = CMLUtil.getQueryNodes(cmlElement, 
			nonDictRefChildS, X_CML);
		for (Node node : nonDictRefChilds) {
			addEntryFromDictRef((CMLElement) node, dictRef, parentTerm);
		}
	}
	
	private void analyzeDictRefOnChild(CMLElement cmlElement) {
		String dictRef = cmlElement.getAttributeValue(DictRefAttribute.NAME);
		dictRef = CMLUtil.getLocalName(dictRef);
    	String nonDictRefChildS = "*[not(@"+
		DictRefAttribute.NAME+")]/*" +
		"[@"+DictRefAttribute.NAME+"]";
		List<Node> nonDictRefChilds = CMLUtil.getQueryNodes(cmlElement, 
			nonDictRefChildS, X_CML);
		String parentTerm = EntryTool.createTerm(cmlElement);
		for (Node node : nonDictRefChilds) {
			Element child = (Element) node;
			addEntryFromDictRef((CMLElement) node, 
				child.getAttributeValue(DictRefAttribute.NAME),
				parentTerm);
		}
	}
	    
	private void analyzeValue(CMLElement cmlElement) {
		String dictRef = cmlElement.getAttributeValue(DictRefAttribute.NAME);
		dictRef = CMLUtil.getLocalName(dictRef);
		addEntryFromDictRef(cmlElement, dictRef, null);
	}
	    
    private void addEntryFromDictRef(CMLElement cmlElement, 
		String dictRef, String parentTerm) {
    	
    	ensureEntryToolMap();
		String term = EntryTool.createTerm(cmlElement);
		if (term == null) {
			term = parentTerm;
		}
//		cmlElement.debug();
		CMLEntry entry = dictionary.getCMLEntry(CMLUtil.getLocalName(dictRef));
		EntryTool entryTool = null;
		if (entry == null) {
			entry = new CMLEntry();
			// id
			String id = CMLUtil.getLocalName(dictRef);
			if (id == null) {
				if (term != null) {
					id = term.toLowerCase();
					id = id.replace(S_EMPTY, S_SPACE);
				} else {
					throw new CMLRuntimeException("no id or term to create entry from");
				}
			}
			entry.setId(CMLUtil.getLocalName(dictRef).toLowerCase());
			// definition
			CMLDefinition definition = new CMLDefinition();
			String def = "created from analysis";
			if (term != null) {
				def = term;
			}
			definition.appendChild(new Text(def));
			entry.appendChild(definition);
			// description
			CMLDescription description = new CMLDescription();
			description.appendChild(new Text("created from analysis"));
			entry.appendChild(description);
			// add
			dictionary.addEntryInOrder(entry);
		}
		entry.checkAndSetTerm(term);
		
		// make entryTool if none exists
		if (entryToolMap.get(entry) == null) {
			entryTool = this.createEntryTool(entry);
			entryToolMap.put(entry, entryTool);
		}
		entryTool = entryToolMap.get(entry);
		// record values
		if (cmlElement instanceof CMLScalar ||
			cmlElement instanceof CMLArray ||
			cmlElement instanceof CMLMatrix) {
			String value = cmlElement.getValue();
			entryTool.addValue(value);
		} else if (cmlElement instanceof CMLProperty ||
			cmlElement instanceof CMLParameter) {
			String value = cmlElement.getAttributeValue("value");
			if (value != null) {
				entryTool.addValue(value);
			}
		}
    }

    /** analise the tempDictRefMap.
     * @param tempDictRefMap created as above.
     */
//    private void analyzeMap() {
//    	for (String key : tempDictRefMap.keySet()) {
//    		Map<String, ValueCount> map = 
//    			(Map<String, ValueCount>) tempDictRefMap.get(key);
//    		System.out.println("\n"+key);
//    		for (String s : map.keySet()) {
//    			ValueCount termCount = map.get(s);
//    			System.out.println(termCount.count+" ... "+s);
//    		}
//    	}
//    }

    /** update index in dictionary entries.
     */
	public void updateEntryIndex() {
		for (CMLEntry entry : dictionary.getEntryElements()) {
			entry.updateIndex();
		}
	}


    /** update current dictionary with observed dictRefs.
     * uses observed names and values of dictRefs to add dictionary
     * entries, and where possible add types or enuemrations to these
     */
    public void updateDictionary() {
    	ensureEntryToolMap();
    	Set<CMLEntry> entrySet = entryToolMap.keySet();
    	for (CMLEntry entry : entrySet) {
    		EntryTool entryTool = entryToolMap.get(entry);
    		entryTool.updateEnumerations();
		}
    }
    
    /** validate all dictRefs in CML object.
     * 
     * @param cml
     */
    public void validateDictRefsInCML(CMLElement cml) {
    	List<Node> dictRefList = CMLUtil.getQueryNodes(cml, ".//*[@dictRef]");
    	for (Node node : dictRefList) {
    		CMLElement element = (CMLElement) node;
//    		try {
    			validateElementWithDictRef(element);
//    		} catch (CMLRuntimeException e) {
//    			e.printStackTrace();
//    			System.err.println("Cannot validate: "+element.getAttributeValue("dictRef")+" / "+e);
//    		}
    	}
    }
    
    /** create element.
     * 
     * @param name
     * @param value
     * @return element
     */
    public CMLElement createTypedNameValue(String name, String value) {
    	name = name.toLowerCase();
    	if (dictionary == null) {
    		throw new CMLRuntimeException("Null dictionary; cannot add value");
    	}
		CMLEntry entry = dictionary.getCMLEntry(name);
		if (entry == null) {
			throw new CMLRuntimeException("Cannot find entry for: "+name);
		}
		EntryTool entryTool = this.createEntryTool(entry);
		String dataType = entry.getDataType();
		if (dataType == null) {
			dataType = XSD_STRING;
		}
		entryTool.setPrefix(this.getPrefix());
		entryTool.setDelimiter(this.getDelimiter());
		CMLElement element = null;
		if (XSD_DATE.equals(dataType)) {
			element = entryTool.createDate(name, value);
		} else if (XSD_FLOAT.equals(dataType) || XSD_DOUBLE.equals(dataType)) {
			element = entryTool.createDoubleScalarOrDoubleArray(name, value);
		} else if (XSD_INTEGER.equals(dataType)) {
			element = entryTool.createIntegerScalarOrIntegerArray(name, value);
		} else if (XSD_STRING.equals(dataType)) {
			element = entryTool.createStringScalarOrStringArray(name, value);
		} else if (CMLFormula.TAG.equals(CMLUtil.getLocalName(dataType))) {
			element = entryTool.createFormula(name, value);
		} else if (CMLMatrix.TAG.equals(CMLUtil.getLocalName(dataType))) {
			element = entryTool.createMatrix(name, value);
		} else if (CMLVector3.TAG.equals(CMLUtil.getLocalName(dataType))) {
			element = entryTool.createVector3(name, value);
		} else {
			throw new CMLRuntimeException("dataType not supported, assume string: "+dataType);
		}
		return element;
    }

    /** create entry tool.
     * 
     * @param entry
     * @return tool
     */
    public EntryTool createEntryTool(CMLEntry entry) {
    	EntryTool entryTool = new EntryTool(entry);
    	entryTool.setPrefix(this.getPrefix());
    	entryTool.setDelimiter(this.getDelimiter());
    	entryTool.setFailOnError(this.isFailOnError());
    	entryTool.setIgnoreCaseOfEnumerations(this.isIgnoreCaseOfEnumerations());
    	return entryTool;
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
    		EntryTool entryTool = this.createEntryTool(entry);
    		entryTool.validate(element);
    	}
    }
}
