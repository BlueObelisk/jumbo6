package org.xmlcml.cml.tools;

import java.util.logging.Logger;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLFormula;

/**
 * additional tools for formula. not fully developed
 *
 * @author pmr
 *
 */
public class FormulaTool extends AbstractTool {

	Logger logger = Logger.getLogger(FormulaTool.class.getName());

	/** */
	public static String HYDROGEN_COUNT = "hydrogenCount";
	
	private CMLFormula formula;

	/**
	 * constructor
	 *
	 * @param formula
	 * @deprecated use getOrCreateTool
	 */
	public FormulaTool(CMLFormula formula) {
		this.formula = formula;
		this.formula.setTool(this);
	}

	/**
	 * get formula.
	 *
	 * @return the formula
	 */
	public CMLFormula getFormula() {
		return formula;
	}
	
	/** gets FormulaTool associated with formula.
	 * if null creates one and sets it in formula
	 * @param formula
	 * @return tool
	 */
	@SuppressWarnings("all")
	public static FormulaTool getOrCreateTool(CMLFormula formula) {
		FormulaTool formulaTool = (FormulaTool) formula.getTool();
		if (formulaTool == null) {
			formulaTool = new FormulaTool(formula);
			formula.setTool(formulaTool);
		}
		return formulaTool;
	}

//	/** get charge.
//	 * 
//	 * @return charge
//	 */
//	public int getFormalCharge() {
//		int formalCharge = 0;
//		Nodes chargedAtoms = formula.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge]", X_CML);
//		for (int i = 0; i < chargedAtoms.size(); i++) {
//			formalCharge += Integer.parseInt(((Element)chargedAtoms.get(i)).getAttributeValue("formalCharge"));
//		}
//		return formalCharge;
//	}
//	
	
	/**
	 * normalize all formulas which are descendant of node
	 * @param node
	 */
	public static void normalizeDescendantFormulas(Node node) {
		Nodes formulas = node.query(".//*[local-name()='formula']");
    	for (int i = 0; i < formulas.size(); i++) {
    		((CMLFormula)formulas.get(i)).normalize();
    	}
	}
	
}

