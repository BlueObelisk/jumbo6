package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting region. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLRegion extends AbstractRegion {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /**
     * contructor.
     */
    public CMLRegion() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLRegion(CMLRegion old) {
        super((AbstractRegion) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLRegion(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLRegion
     */
    public static CMLRegion makeElementInContext(Element parent) {
        return new CMLRegion();

    }
}