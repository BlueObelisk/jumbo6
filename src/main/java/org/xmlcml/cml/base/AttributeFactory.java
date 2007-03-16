/**
 * 
 */
package org.xmlcml.cml.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/**
 * @author pm286
 *
 */
public class AttributeFactory implements CMLConstants {

    final static Logger logger = Logger.getLogger(AttributeFactory.class.getName());
    // singleton
    /** singleton attribute factory */
	public final static AttributeFactory attributeFactory = new AttributeFactory();
    static {
    	attributeFactory.init();
    }
 	private Map<String, Element> attributeGroupElementMap;
    private Map<String, CMLAttribute> attributeGroupNameAttributeMap;
 	private Map<String, Map<String, String>> elementSynonymMap;
	private SchemaManager schemaManager;

	/** constructor.
	 */
	private AttributeFactory() {
	}
	
    void init() {
//        attributeGroupNameAttributeMap = new HashMap<String, CMLAttribute>();
        elementSynonymMap = new HashMap<String, Map<String, String>>();
        schemaManager = new SchemaManager();
    	schemaManager.readAndCreateIndexesFromSchemaFiles();
    	AttributeFactory.attributeFactory.setSchemaManager(schemaManager);
    	makeSynonymMap();
    }

	/** custom attributeFactory.
	 * 
	 * @param className
	 * @return attribute factory
	 * @throws Exception
	 */
	public static AttributeFactory createAttributeFactory(String className) throws Exception {
		return (AttributeFactory) Class.forName(className).newInstance();
	}
	
    /** lookup CMLAttributes by name.
    *
    * @param name to lookup
    * @return CMLAttribute
    */
    public CMLAttribute getAttribute(String name) {
        return (CMLAttribute) attributeGroupNameAttributeMap.get(name);
    }

    private void makeSynonymMap() {
    	attributeGroupElementMap = new HashMap<String, Element>();
    	try {
    		// create special attributes
    		Map<String, CMLAttribute> attributeGroupMap = 
    			schemaManager.getAttributeGenerator().getAttributeGroupMap();
    		for (String attributeGroupName : attributeGroupMap.keySet()) {
    			CMLAttribute specialAttribute = createSpecialAttribute(attributeGroupName);
    			// replace by special attribute
    			if (specialAttribute != null) {
    				attributeGroupMap.put(attributeGroupName, specialAttribute);
    			}
    		}
    		
    		Element attributeTop = CMLUtil.getXMLResource(SchemaManager.ATTRIBUTEGROUPS_XSD).getRootElement();
    		List<Node> attributeGroups = CMLUtil.getQueryNodes(attributeTop, "./"+XSD_ATTRIBUTE_GROUP, XPATH_XSD);
// this is a mess - there is some duplication    		
    		attributeGroupNameAttributeMap = new HashMap<String, CMLAttribute>();
    		for (Node node : attributeGroups) {
    			Element xsdAttribute = (Element) node;
    			String attributeGroupName = xsdAttribute.getAttributeValue("name");
    			attributeGroupElementMap.put(attributeGroupName, xsdAttribute);
    			CMLAttribute att = schemaManager.getAttributeGenerator().createAttribute(attributeGroupName);
    			attributeGroupNameAttributeMap.put(attributeGroupName, att);
    		}
    		Element elementTop = CMLUtil.getXMLResource(SchemaManager.ELEMENTS_XSD).getRootElement();
    		List<Node> elements = CMLUtil.getQueryNodes(elementTop, "./"+XSD_ELEMENT, XPATH_XSD);
    		for (Node node : elements) {
    			Element xsdElement = (Element) node;
    			String elementName = xsdElement.getAttributeValue("name");
    			Map<String, String> synonymMap = new HashMap<String, String>();
    			elementSynonymMap.put(elementName, synonymMap);
        		List<Node> attributeGroupsx = CMLUtil.getQueryNodes(xsdElement, ".//"+XSD_ATTRIBUTE_GROUP, XPATH_XSD);
        		for (Node nodex : attributeGroupsx) {
        			String ref = ((Element)nodex).getAttributeValue("ref");
        			Element attributeGroupx = attributeGroupElementMap.get(ref);
        			List<Node> attNodes = CMLUtil.getQueryNodes(attributeGroupx, "./"+XSD_ATTRIBUTE, XPATH_XSD);
        			String attributeName = ((Element)attNodes.get(0)).getAttributeValue("name");
        			synonymMap.put(attributeName, ref);
        		}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new CMLRuntimeException("Cannot parse elements/attributes: "+e);
    	}
    }

    /** create attributes if class exists.
     * 
     * @param name of attribute
     * @return subclassed attribute
     */
    public static CMLAttribute createSpecialAttribute(String name) {
		CMLAttribute attribute = null;
		try {
			String attClassName = ATTRIBUTE_CLASS_BASE+S_PERIOD+
					CMLUtil.capitalize(name)+ATTRIBUTE;
			Class attClass = Class.forName(attClassName);
			if (attClass != null) {
				attribute = (CMLAttribute) attClass.newInstance();
			}
		} catch (Exception e) {
			// carry on...
		}
		return attribute;
    }
    
    String getAttributeGroupName(String attributeName, String elementName) {
    	String attributeGroupName= null;
    	Map<String, String> attNameToAttGroupNameMap = elementSynonymMap.get(elementName);
    	if (attNameToAttGroupNameMap != null) {
    		attributeGroupName = attNameToAttGroupNameMap.get(attributeName);
    	} else {
    		System.err.println("Cannot find synonym for: "+elementName);
    	}
    	return attributeGroupName;
    }
    
    /**
     * 
     * @param attributeName
     * @param elementName
     * @return attribute
     */
    public CMLAttribute getAttribute(String attributeName, String elementName) {
        String attributeGroupName = this.getAttributeGroupName(attributeName, elementName);
        if (attributeGroupName == null) {
        	throw new CMLRuntimeException("Cannot find attribute group for "+elementName+"@"+attributeName);
        }
        return attributeGroupNameAttributeMap.get(attributeGroupName);
    }
    
    /** new...
     * @param attributeGroupName
     * @param value
     * @param URI
     * @return
     */
	public CMLAttribute getAttributeByGroupName(String attributeGroupName) {
		Map<String, CMLAttribute> attributeGroupMap = 
			schemaManager.getAttributeGenerator().getAttributeGroupMap();
		CMLAttribute attribute = attributeGroupMap.get(attributeGroupName);
		if (attribute == null) {
			throw new CMLRuntimeException("Cannot find attributeGroup: "+attributeGroupName);
		}
		return attribute;
	}


    /** create subclassed attribute template with dummy value.
     * @param name
     * @param element
     * @return subclassed attribute
     */
    public CMLAttribute createCMLAttribute(String attributeName, CMLElement element) {
    	if (attributeName.equals(CMLXSD_XMLCONTENT)) {
    		throw new CMLRuntimeException("Cannot process "+CMLXSD_XMLCONTENT);
    	}
        return AttributeFactory.attributeFactory.getAttribute(attributeName, element.getLocalName());
    }

    /** create subclassed attribute template with dummay value.
     * 
     * @param name
     * @param type
     * @return
     */
    public static CMLAttribute createCMLAttribute(String name, CMLType type) {
        CMLAttribute newAtt = createSpecialAttribute(name);
        String typeS = type.getJavaType();
        if (newAtt != null) {
        } else if (XSD_BOOLEAN.equals(typeS) && !type.getIsList()) {
            newAtt = new BooleanSTAttribute(name);
        } else if (XSD_BOOLEAN.equals(typeS) && type.getIsList()) {
            newAtt = new BooleanArraySTAttribute(name);
        } else if (XSD_DOUBLE.equals(typeS) && !type.getIsList()) {
            newAtt = new DoubleSTAttribute(name);
        } else if (XSD_DOUBLE.equals(typeS) && type.getIsList()) {
            newAtt = new DoubleArraySTAttribute(name);
        } else if (XSD_INTEGER.equals(typeS) && !type.getIsList()) {
            newAtt = new IntSTAttribute(name);
        } else if (XSD_INTEGER.equals(typeS) && type.getIsList()) {
            newAtt = new IntArraySTAttribute(name);
        } else if (XSD_STRING.equals(typeS) && !type.getIsList()) {
            newAtt = new StringSTAttribute(name);
        } else if (XSD_STRING.equals(typeS) && type.getIsList()) {
            newAtt = new StringArraySTAttribute(name);
        } else {
            logger.severe("unknown attribute type " + type);
        }
        return newAtt;
    }
    
    /** get schemaManager.
     * 
     * @return {@link SchemaManager}
     */
	public SchemaManager getSchemaManager() {
		return schemaManager;
	}

	/** set schema manager.
	 * 
	 * @param schemaManager
	 */
	public void setSchemaManager(SchemaManager schemaManager) {
		this.schemaManager = schemaManager;
	}

}
