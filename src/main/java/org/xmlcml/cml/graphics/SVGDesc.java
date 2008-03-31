package org.xmlcml.cml.graphics;

import nu.xom.Element;
import nu.xom.Node;


/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGDesc extends SVGElement {

	final static String TAG ="desc";

	/** constructor
	 */
	public SVGDesc() {
		super(TAG);
	}
	
	/** constructor
	 */
	public SVGDesc(SVGDesc element) {
        super((SVGDesc) element);
	}
	
	/** constructor
	 */
	public SVGDesc(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGDesc(this);
    }

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}
}
