package org.xmlcml.cml.graphics;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.euclid.Real2Array;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPolygon extends SVGPoly {

	final static String TAG ="polygon";
	
	/** constructor
	 */
	public SVGPolygon() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGPolygon(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGPolygon(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGPolygon(Real2Array real2Array) {
		this();
		setReal2Array(real2Array);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPolygon(this);
    }
		
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

}
