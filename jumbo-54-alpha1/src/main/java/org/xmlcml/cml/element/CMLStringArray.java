package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting stringArray. * autogenerated from schema use
 * as a shell which can be edited
 * 
 */
public class CMLStringArray extends AbstractStringArray {

    /**
     * contructor.
     */
    public CMLStringArray() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLStringArray(CMLStringArray old) {
        super((AbstractStringArray) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLStringArray(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLStringArray
     */
    public static CMLStringArray makeElementInContext(Element parent) {
        return new CMLStringArray();

    }
}
