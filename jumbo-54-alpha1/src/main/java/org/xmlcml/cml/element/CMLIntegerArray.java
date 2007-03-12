package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting integerArray. * autogenerated from schema
 * use as a shell which can be edited
 * 
 */
public class CMLIntegerArray extends AbstractIntegerArray {

    /**
     * constructor.
     */
    public CMLIntegerArray() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLIntegerArray(CMLIntegerArray old) {
        super((AbstractIntegerArray) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLIntegerArray(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLIntegerArray
     */
    public static CMLIntegerArray makeElementInContext(Element parent) {
        return new CMLIntegerArray();

    }
}
