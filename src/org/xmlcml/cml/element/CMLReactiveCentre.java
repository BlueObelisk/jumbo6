package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting reactiveCentre. * autogenerated from schema
 * use as a shell which can be edited
 * 
 */
public class CMLReactiveCentre extends AbstractReactiveCentre {

    /**
     * contructor.
     */
    public CMLReactiveCentre() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLReactiveCentre(CMLReactiveCentre old) {
        super((AbstractReactiveCentre) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLReactiveCentre(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLReactiveCentre
     */
    public static CMLReactiveCentre makeElementInContext(Element parent) {
        return new CMLReactiveCentre();

    }
}
