package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting enumeration. * autogenerated from schema use
 * as a shell which can be edited
 * 
 */
public class CMLEnumeration extends AbstractEnumeration {

    /**
     * constructor.
     */
    public CMLEnumeration() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLEnumeration(CMLEnumeration old) {
        super((AbstractEnumeration) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLEnumeration(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLEnumeration
     */
    public static CMLEnumeration makeElementInContext(Element parent) {
        return new CMLEnumeration();

    }
}