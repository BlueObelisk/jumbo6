package org.xmlcml.cml.tools;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.lite.CMLFormula;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;

/**
 * additional tools for formula. not fully developed
 *
 * @author pmr
 *
 */
public class FormulaTool extends AbstractSVGTool {

	Logger logger = Logger.getLogger(FormulaTool.class.getName());

    /** dewisott */
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
//		Nodes chargedAtoms = formula.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge]", CML_XPATH);
//		for (int i = 0; i < chargedAtoms.size(); i++) {
//			formalCharge += Integer.parseInt(((Element)chargedAtoms.get(i)).getAttributeValue("formalCharge"));
//		}
//		return formalCharge;
//	}
//	
	
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	String s = formula.getConcise();
    	g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
    	SVGText text = new SVGText(new Real2(10., 10.), s);
    	text.setFontSize(6.);
    	text.setOpacity(0.5);
    	text.setFill("yellow");
    	g.appendChild(text);
		return g;
    }
	
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

