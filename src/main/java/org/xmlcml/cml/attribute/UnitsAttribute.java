package org.xmlcml.cml.attribute;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting UnitsAttribute. supports units attribute
 */
public class UnitsAttribute extends NamespaceRefAttribute {

    /** */
    public final static String NAME = "units";

    /**
     * constructor.
     * 
     */
    public UnitsAttribute() {
        super(NAME);
    }

    /**
     * constructor.
     * 
     * @param value
     *            QName for units
     */
    public UnitsAttribute(String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public UnitsAttribute(Attribute att) {
        super(att);
    }

    /**
     * checks an element recursively for valid units attributes. checks that all
     * units in a element resolve.
     * 
     * @param element
     *            to check
     * @param dictionaryMap
     * @return list of errors (empty if none)
     */
    public List<String> checkAttribute(Element element,
            GenericDictionaryMap dictionaryMap) {
        List<String> errorList = new ArrayList<String>();
        if (element instanceof CMLElement) {
            errorList.addAll(check((CMLElement)element, NAME, dictionaryMap));
        }
        return errorList;
    }

}
