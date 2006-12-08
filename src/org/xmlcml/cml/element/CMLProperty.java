package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting property. * autogenerated from schema use as
 * a shell which can be edited
 * 
 */
public class CMLProperty extends AbstractProperty {

    /**
     * constructor.
     */
    public CMLProperty() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLProperty(CMLProperty old) {
        super((AbstractProperty) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLProperty(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLProperty
     */
    public static CMLProperty makeElementInContext(Element parent) {
        return new CMLProperty();

    }
}
