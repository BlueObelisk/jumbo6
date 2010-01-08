package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
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

	static Map<String, CMLMolecule> concise2MoleculeMap;

	/**
	 * constructor
	 *
	 * @param formula
	 * @deprecated use getOrCreateTool
	 */
	public FormulaTool(CMLFormula formula) {
		if (formula == null) {
			throw new RuntimeException("null formula");
		}
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
//		Nodes chargedAtoms = formula.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge]", CMLConstants.CML_XPATH);
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
	
	static CMLMolecule calculateMolecule(SMILESTool smilesTool, String smiles) {
		smilesTool.parseSMILES(smiles);
		return smilesTool.getMolecule();
	}

	public static Map<String, CMLMolecule> ensureConcise2MoleculeMap() {
		if (FormulaTool.concise2MoleculeMap == null) {
			FormulaTool.concise2MoleculeMap = new HashMap<String, CMLMolecule>();
			SMILESTool smilesTool = new SMILESTool();
			FormulaTool.concise2MoleculeMap.put("H 2", FormulaTool.calculateMolecule(smilesTool, "[H][H]"));
			FormulaTool.concise2MoleculeMap.put("H 2 O 1", FormulaTool.calculateMolecule(smilesTool, "O"));
			FormulaTool.concise2MoleculeMap.put("H 1 Br 1", FormulaTool.calculateMolecule(smilesTool, "Br"));
			FormulaTool.concise2MoleculeMap.put("H 1 Cl 1", FormulaTool.calculateMolecule(smilesTool, "Cl"));
			FormulaTool.concise2MoleculeMap.put("H 1 F 1", FormulaTool.calculateMolecule(smilesTool, "F"));
			FormulaTool.concise2MoleculeMap.put("H 1 I 1", FormulaTool.calculateMolecule(smilesTool, "I"));
			FormulaTool.concise2MoleculeMap.put("Br 2", FormulaTool.calculateMolecule(smilesTool, "BrBr"));
			FormulaTool.concise2MoleculeMap.put("Cl 2", FormulaTool.calculateMolecule(smilesTool, "ClCl"));
			// this creates a bug - java.lang.RuntimeException: duplicate id: a20
			// at org.xmlcml.cml.tools.MoleculeTool.checkUnique(MoleculeTool.java:3021)
//			FormulaTool.concise2MoleculeMap.put("O 2 S 1", FormulaTool.calculateMolecule(smilesTool, "O=S=O"));
//			FormulaTool.concise2MoleculeMap.put("H 4 O 2", FormulaTool.calculateMolecule(smilesTool, "O.O"));
		}
		return FormulaTool.concise2MoleculeMap;
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

