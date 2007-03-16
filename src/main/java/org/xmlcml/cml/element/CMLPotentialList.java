package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting potentialList. * autogenerated from schema
 * use as a shell which can be edited
 *
 */
public class CMLPotentialList extends AbstractPotentialList {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /**
     * constructor.
     */
    public CMLPotentialList() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLPotentialList(CMLPotentialList old) {
        super((AbstractPotentialList) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLPotentialList(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLPotentialList
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLPotentialList();

    }
}
