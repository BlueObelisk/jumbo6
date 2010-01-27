package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
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
    	SVGG svgg = new SVGG();
    	if (s != null) {
	    	String[] ss = s.split(CMLConstants.S_SPACE);
	    	// charge at end?
	    	int evenLength = (ss.length % 2 == 1) ? ss.length - 1 : ss.length;
	    	Real2 offset = new Real2(0.0, 0.0);
	    	double fontSizeAtom = 12.;
	    	double widthFactor = 0.8;
	    	double fontSizeCount = 8.;
	    	double fontSizeCharge = 8.;
	    	double subscriptShift = fontSizeCount / 2.0;
	    	double superscriptShift = fontSizeCount / 2.0;
	    	for (int i = 0; i < evenLength; i += 2) {
	    		int isub = i+1;
	    		SVGText atomSVG = new SVGText(offset, ss[i]);
	    		atomSVG.setFontSize(fontSizeAtom);
	    		svgg.appendChild(atomSVG);
	    		double widthFactorGuessingLowerCase = Math.min( ss[i].length(), 1.6);
	    		offset = offset.plus(new Real2(fontSizeAtom * widthFactor * widthFactorGuessingLowerCase, 0.0));
	    		String countString = ss[isub];
	    		int count = Integer.parseInt(countString);
	    		if (count != 1) {
	        		offset = offset.plus(new Real2(0.0, subscriptShift));
	        		SVGText countSVG = new SVGText(offset, countString);
	        		countSVG.setFontSize(fontSizeCount);
	        		svgg.appendChild(countSVG);
	        		offset = offset.plus(new Real2(fontSizeCount * countString.length() * widthFactor, -subscriptShift));
	    		}
	    	}
	    	if (ss.length - evenLength == 1) {
	    		String chargeString = ss[evenLength];
	    		int charge = Integer.parseInt(chargeString);
	    		if (charge == 1) {
	    			chargeString = CMLConstants.S_PLUS;
	    		} else if (charge == -1) {
	    			chargeString = CMLConstants.S_MINUS;
	    		} 
	    		offset = offset.plus(new Real2(0.0, superscriptShift));
	    		SVGText chargeSVG = new SVGText(offset, chargeString);
	    		chargeSVG.setFontSize(fontSizeCharge);
	    		svgg.appendChild(chargeSVG);
	    	}
	    	g.appendChild(svgg);
    	}
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

//	public CMLFormula subtractFormula(CMLFormula form) {
//		form.setAllowNegativeCounts(true);
//		System.out.println(form.isAllowNegativeCounts());
//		CMLFormula aggregatedFormula = form.getAggregateFormula();
//		aggregatedFormula.setAllowNegativeCounts(true);
//		aggregatedFormula.multiplyBy(-1.0f);
//		aggregatedFormula.debug();
//		CMLFormula newFormula = formula.createAggregatedFormula(aggregatedFormula);
//		return newFormula;
//	}
}

