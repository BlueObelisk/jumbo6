package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;

/**
 * user-modifiable class supporting bondStereo. * autogenerated from schema use
 * as a shell which can be edited
 *
 */
public class CMLBondStereo extends AbstractBondStereo {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

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
    public CMLElement makeElementInContext(Element parent) {
        return new CMLBondStereo();

    }
    
    /** matches parity for two exemplars of same bondStereo
     * example:
     * <bondStereo atomRefs4='a1 a2 a3 a4'>C</bondStereo>
     * and
     * <bondStereo atomRefs4='a4 a3 a2 a1'>C</bondStereo>
     * are identical - any other combination is not
     * @param bs
     * @return 1 if identical -1 if opposite 0 if neither
     */
    public int matchParity(CMLBondStereo bs) {
    	int res = 0;
    	String thisx = this.getXMLContent().trim();
    	String bsx = bs.getXMLContent().trim();
    	String[] this4 = this.getAtomRefs4();
    	String[] bs4 = bs.getAtomRefs4();
    	if (this4 == null || bs4 == null || this4.length != 4 || bs4.length != 4) {
    		throw new CMLRuntimeException("bondStereo has no atomRefs4");
    	}
    	if ( 
			(
			this4[0].equals(bs4[0]) &&
    	    this4[1].equals(bs4[1]) &&
    	    this4[2].equals(bs4[2]) &&
    	    this4[3].equals(bs4[3])
    	    ) ||
   			(
			this4[0].equals(bs4[3]) &&
    	    this4[1].equals(bs4[2]) &&
    	    this4[2].equals(bs4[1]) &&
    	    this4[3].equals(bs4[0])
    	    ) 
    	    )
    	{
    				res = (thisx.equals(bsx)) ? 1 : -1;
		}
    	return res;
    }

}
