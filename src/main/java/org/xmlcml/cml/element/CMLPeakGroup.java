package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting peakGroup. * autogenerated from schema use
 * as a shell which can be edited
 *
 */
public class CMLPeakGroup extends AbstractPeakGroup {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /**
     * constructor.
     */
    public CMLPeakGroup() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLPeakGroup(CMLPeakGroup old) {
        super((AbstractPeakGroup) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLPeakGroup(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLPeakGroup
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLPeakGroup();

    }

}
