package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/**
 * user-modifiable class supporting reactionStepList. * autogenerated from
 * schema use as a shell which can be edited
 * 
 */
public class CMLReactionStepList extends AbstractReactionStepList {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /**
     * contructor.
     */
    public CMLReactionStepList() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLReactionStepList(CMLReactionStepList old) {
        super((AbstractReactionStepList) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLReactionStepList(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLReactionStepList
     */
    public static CMLReactionStepList makeElementInContext(Element parent) {
        return new CMLReactionStepList();

    }
}