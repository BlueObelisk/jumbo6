package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting documentation. * autogenerated from schema
 * use as a shell which can be edited
 *
 */
public class CMLDocumentation extends AbstractDocumentation {

    /**
     * constructor.
     */
    public CMLDocumentation() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLDocumentation(CMLDocumentation old) {
        super((AbstractDocumentation) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLDocumentation(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLDocumentation
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLDocumentation();

    }
}
