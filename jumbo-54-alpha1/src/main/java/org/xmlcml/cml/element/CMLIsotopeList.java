package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting isotopeList. * autogenerated from schema use
 * as a shell which can be edited
 * 
 */
public class CMLIsotopeList extends AbstractIsotopeList {

    /**
     * constructor.
     */
    public CMLIsotopeList() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLIsotopeList(CMLIsotopeList old) {
        super((AbstractIsotopeList) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLIsotopeList(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLIsotopeList
     */
    public static CMLIsotopeList makeElementInContext(Element parent) {
        return new CMLIsotopeList();

    }
}
