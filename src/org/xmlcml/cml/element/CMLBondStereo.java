package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting bondStereo. * autogenerated from schema use
 * as a shell which can be edited
 * 
 */
public class CMLBondStereo extends AbstractBondStereo {

    /**
     * constructor.
     */
    public CMLBondStereo() {
        super();

    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLBondStereo(CMLBondStereo old) {
        super((AbstractBondStereo) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLBondStereo(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLBond
     */
    public static CMLBondStereo makeElementInContext(Element parent) {
        return new CMLBondStereo();

    }

    /**
     * match parity. not written at all NYI
     * 
     * @param bondStereo
     * @param molecule
     * @return parity
     */
    public int matchParity(CMLBondStereo bondStereo, CMLMolecule molecule) {
        // FIXME
        return 0;
    }
}
