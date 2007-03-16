package org.xmlcml.cml.attribute;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLUnitType;

/**
 * user-modifiable class supporting UnitsAttribute. supports units attribute
 */
public class UnitTypeAttribute extends NamespaceRefAttribute {

    // types of unit list - these should be an enum
    /** */
    public final static String UNIT = "unit";

    /** */
    public final static String UNITTYPE = "unitType";

    /** */
    public final static String NAME = "unitType";

    /**
     * constructor.
     * 
     */
    public UnitTypeAttribute() {
        super(NAME);
    }

    /** constructor.
     * 
     * @param unitType
     */
    public UnitTypeAttribute(CMLUnitType unitType) {
        this(unitType.getLocalName(), unitType.getValue());
    }

    /**
     * constructor.
     * 
     * @param name
     * @param value
     */
    public UnitTypeAttribute(String name, String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public UnitTypeAttribute(Attribute att) {
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
            errorList.addAll(check((CMLElement)element, NAME, dictionaryMap));
        }
        // errorList.addAll(check(element, CMLArray.TAG, NAME, dictionaryMap));
        return errorList;
    }

}
