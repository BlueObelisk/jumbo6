package org.xmlcml.cml.attribute;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting DictRefAttribute. supports dictRef attribute
 */
public class ParentSIAttribute extends NamespaceRefAttribute {

	/** parentSI*/
    public final static String NAME = "parentSI";

    /**
     * constructor.
     * 
     */
    public ParentSIAttribute() {
        super(NAME);
    }

    /**
     * constructor.
     * 
     * @param name
     * @param value
     */
    public ParentSIAttribute(String name, String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public ParentSIAttribute(Attribute att) {
        super(att);
    }

    /**
     * checks an element recursively for valid parentSI. checks that all
     * dictRefs in a element resolve.
     * 
     * @param element
     *            to check
     * @param dictionaryMap
     * @return list of errors (empty if none)
     */
    public List<String> checkAttribute(/*CML*/Element element,
            GenericDictionaryMap dictionaryMap) {
        List<String> errorList = new ArrayList<String>();
        errorList.addAll(check((CMLElement)element, NAME, dictionaryMap));
        return errorList;
    }

}
