package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting latticeVector. * autogenerated from schema
 * use as a shell which can be edited
 *
 */
public class CMLLatticeVector extends AbstractLatticeVector {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /**
     * constructor.
     */
    public CMLLatticeVector() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLLatticeVector(CMLLatticeVector old) {
        super((AbstractLatticeVector) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLLatticeVector(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLLatticeVector
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLLatticeVector();
    }

    /**
     * constructor.
     *
     * @param v
     *            vector
     */
    public CMLLatticeVector(double[] v) {
        this.setXMLContent(v);
    }

    /**
     * constructor.
     *
     * @param v3
     *            vector3
     */
    public CMLLatticeVector(CMLVector3 v3) {
        this.setXMLContent(v3.getXMLContent());
    }

    /**
     * get vector from latticeVector.
     *
     * @return vector
     */
    public CMLVector3 getCMLVector3() {
        return new CMLVector3(this.getXMLContent());
    }
}
