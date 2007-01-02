package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting UnitsAttribute. supports units attribute
 */
public class UnitAttribute extends NamespaceRefAttribute {

    /** */
    public final static String NAME = "units";

    /**
     * constructor.
     * 
     */
    public UnitAttribute() {
        super(NAME);
    }

    /**
     * constructor.
     * 
     * @param value
     *            QName for units
     */
    public UnitAttribute(/* String name, */String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public UnitAttribute(Attribute att) {
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
    public List<String> checkAttribute(/*CML*/Element element,
            GenericDictionaryMap dictionaryMap) {
        List<String> errorList = new ArrayList<String>();
        if (element instanceof CMLElement) {
            errorList.addAll(check((CMLElement)element, CMLScalar.TAG, NAME, dictionaryMap));
            errorList.addAll(check((CMLElement)element, CMLArray.TAG, NAME, dictionaryMap));
            errorList.addAll(check((CMLElement)element, CMLMatrix.TAG, NAME, dictionaryMap));
        }
        return errorList;
    }

}
