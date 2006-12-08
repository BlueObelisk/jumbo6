package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting identifier. * autogenerated from schema use
 * as a shell which can be edited
 * 
 */
public class CMLIdentifier extends AbstractIdentifier {

    /**
     * constructor.
     */
    public CMLIdentifier() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLIdentifier(CMLIdentifier old) {
        super((AbstractIdentifier) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLIdentifier(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLIdentifier
     */
    public static CMLIdentifier makeElementInContext(Element parent) {
        return new CMLIdentifier();

    }
}
