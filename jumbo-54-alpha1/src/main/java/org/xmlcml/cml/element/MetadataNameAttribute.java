package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting DictRefAttribute. supports dictRef attribute
 */
public class MetadataNameAttribute extends NamespaceRefAttribute {

    final static String NAME = "name";

    /**
     * constructor.
     * 
     */
    public MetadataNameAttribute() {
        super(NAME);
    }

    /**
     * constructor.
     * 
     * @param name
     * @param value
     */
    public MetadataNameAttribute(String name, String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public MetadataNameAttribute(Attribute att) {
        super(att);
    }

    /**
     * checks an element recursively for valid metadata names. checks that all
     * metadata names in a element resolve.
     * 
     * @param element
     *            to check
     * @param dictionaryMap
     * @return list of errors (empty if none)
     */
    public List<String> checkAttribute(/*CML*/Element element,
            GenericDictionaryMap dictionaryMap) {
        List<String> errorList = new ArrayList<String>();
        if (element instanceof CMLElement) {
            errorList.addAll(check((CMLElement)element, CMLMetadata.TAG, NAME, dictionaryMap));
        }
        return errorList;
    }

}
