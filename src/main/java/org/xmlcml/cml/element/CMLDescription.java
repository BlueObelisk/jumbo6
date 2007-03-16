package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting description. * autogenerated from schema use
 * as a shell which can be edited
 *
 */
public class CMLDescription extends AbstractDescription {

    /**
     * constructor.
     */
    public CMLDescription() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLDescription(CMLDescription old) {
        super((AbstractDescription) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLDescription(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLDescription
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLDescription();

    }
}
