package org.xmlcml.cml.tools;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAction;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;

/**
 * tool for managing actions
 *
 * @author pmr
 *
 */
public class ActionTool extends AbstractSVGTool {
	final static Logger LOG = Logger.getLogger(ActionTool.class);

	CMLAction action = null;

	/** constructor.
	 */
	public ActionTool(CMLAction action) throws RuntimeException {
		init();
		this.action = action;
	}


	void init() {
	}


	/**
	 * get action.
	 *
	 * @return the action or null
	 */
	public CMLAction getAction() {
		return this.action;
	}

	/** gets ActionTool associated with action.
	 * if null creates one and sets it in action
	 * @param action
	 * @return tool
	 */
	public static ActionTool getOrCreateTool(CMLAction action) {
		ActionTool actionTool = (action == null) ? null : (ActionTool) action.getTool();
		if (actionTool == null) {
			actionTool = new ActionTool(action);
			action.setTool(actionTool);
		}
		return actionTool;
	}
	
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	g = null;
//    	ensureMoleculeDisplay();
//    	ensureAtomDisplay();
//    	atomDisplay.ensureMoleculeDisplay(moleculeDisplay);
    	 drawAction(drawable);
    	 return (g == null || g.getChildElements().size() == 0) ? null : g;
	}
    
    private void drawAction(CMLDrawable drawable) {
		 g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
		Elements childElements = action.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			if (childElement instanceof CMLMolecule) {
				MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool((CMLMolecule)childElement);
				SVGElement gg = moleculeTool.createGraphicsElement();
				g.appendChild(gg);
			}
		}
    }
}