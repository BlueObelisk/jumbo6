package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting xaxis. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLXaxis extends AbstractXaxis {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /**
     * contructor.
     */
    public CMLXaxis() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLXaxis(CMLXaxis old) {
        super((AbstractXaxis) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLXaxis(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLXaxis
     */
    public static CMLXaxis makeElementInContext(Element parent) {
        return new CMLXaxis();

    }
}
