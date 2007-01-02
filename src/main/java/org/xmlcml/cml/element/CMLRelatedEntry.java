package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting relatedEntry. * autogenerated from schema
 * use as a shell which can be edited
 * 
 */
public class CMLRelatedEntry extends AbstractRelatedEntry {

    /**
     * contructor.
     */
    public CMLRelatedEntry() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLRelatedEntry(CMLRelatedEntry old) {
        super((AbstractRelatedEntry) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLRelatedEntry(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLRelatedEntry
     */
    public static CMLRelatedEntry makeElementInContext(Element parent) {
        return new CMLRelatedEntry();

    }
}