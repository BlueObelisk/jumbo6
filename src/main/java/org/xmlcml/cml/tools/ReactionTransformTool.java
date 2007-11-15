package org.xmlcml.cml.tools;

import java.util.List;
import java.util.logging.Logger;

import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;

/**
 * supports reaction transforms
 * a transform must be a balanced reaction with a 1-1 correspondence between
 * all atoms ids. A simple (chemically meaningless) example is:
 * R(r1)-S(a1)-Cl(a2) + R(r2)-O(a3)-Br(a4) -> R(r1)-Br(a4) + R(r2)-O(a3)-S(a1)-Cl(a2)
 * at present we insist on complete mass balance. 
 * we then map the particular reactants to the transform
 * 
 * if applied to the reactants C(a20)-S(a21)-Cl(a22) + C(a30)-O(a33)-Br(a34)
 * with the map:
 * a1->a21; a2->a22; a3->a33; a4->a34 
 * the R's are discarded and we get:
 * C(a20)-S(a21)-Cl(a22) + C(a30)-O(a33)-Br(a34) =>
 *     C(a20)-Br(a34) + C(a30)-O(a33)-S(a21)-Cl(a22) =>
 *  
 * @author pmr
 * 
 */
public class ReactionTransformTool extends ReactionTool {

    Logger logger = Logger.getLogger(ReactionTransformTool.class.getName());

    CMLReaction reaction = null;

    /**
     * constructor.
     * will interpret the reaction as a transform
     * @param transform
     */
//    @SuppressWarnings("deprecated")
    public ReactionTransformTool(CMLReaction transform) {
    	super(transform);
    	setup();
    }
    
    private void setup() {
    	
    	
    }

    /** match reactant list to transform
     * 
     * @param reactantList
     * @param reactantToTransform
     */
	public void addReactantList(CMLReactantList reactantList, List<CMLMap> reactantToTransform) {
    }
    
}
