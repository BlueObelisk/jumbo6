package org.xmlcml.cml.tools;



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLProperty;

/**
 * tool for managing property
 *
 * @author pmr
 *
 */
public class PropertyTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(PropertyTool.class.getName());

	CMLProperty property = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public PropertyTool(CMLProperty property) throws RuntimeException {
		init();
		this.property = property;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLProperty getProperty() {
		return this.property;
	}

    
	/** gets PropertyTool associated with property.
	 * if null creates one and sets it in property
	 * @param property
	 * @return tool
	 */
	public static PropertyTool getOrCreateTool(CMLProperty property) {
		PropertyTool propertyTool = null;
		if (property != null) {
			propertyTool = (PropertyTool) property.getTool();
			if (propertyTool == null) {
				propertyTool = new PropertyTool(property);
				property.setTool(propertyTool);
			}
		}
		return propertyTool;
	}


//    /**
//     * checks a file for valid dictRefs. checks that all dictRefs in a file
//     * resolve. ? Not used?
//     *
//     * @param file
//     *            to check
//     * @param dictionaryMap
//     * @return list of errors (empty if none)
//     */
//    public static List<String> checkPropertyNames(File file,
//            DictionaryMap dictionaryMap) {
//        List<String> errorList = new ArrayList<String>();
//
//        CMLCml cml = null;
//        try {
//            cml = (CMLCml) new CMLBuilder().build(file).getRootElement();
//        } catch (Exception e) {
//            e.printStackTrace();
//            errorList.add("should not throw " + e);
//        }
//        if (errorList.size() == 0) {
//            // scalars
//            List<CMLElement> propertys = cml.getElements(".//"+CMLProperty.NS);
//            for (CMLElement property : propertys) {
//                DictRefAttribute dictRefAttribute = (DictRefAttribute) ((CMLProperty) property)
//                        .getDictRefAttribute();
//                // LOG.debug("N"+dictRefAttribute);
//                if (dictRefAttribute == null) {
//                    errorList.add("NULL NAME: " + CMLConstants.S_LSQUARE + property.toXML() + CMLConstants.S_RSQUARE);
//                } else {
//                    CMLEntry entry = (CMLEntry) dictionaryMap
//                            .getEntry(dictRefAttribute);
//                    if (entry == null) {
//                        errorList.add("NOTFOUND "
//                                + dictRefAttribute.getQualifiedName() + CMLConstants.S_LSQUARE
//                                + property.toXML() + CMLConstants.S_RSQUARE);
//                    } else {
//                        // LOG.debug("FOUND "+dictRefAttribute);
//                    }
//                }
//            }
//        }
//        return errorList;
//    }

};