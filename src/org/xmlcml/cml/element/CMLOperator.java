package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting operator. * autogenerated from schema use as
 * a shell which can be edited
 * 
 */
public class CMLOperator extends AbstractOperator {

    /**
     * constructor.
     */
    public CMLOperator() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLOperator(CMLOperator old) {
        super((AbstractOperator) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLOperator(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLOperator
     */
    public static CMLOperator makeElementInContext(Element parent) {
        return new CMLOperator();

    }
}
