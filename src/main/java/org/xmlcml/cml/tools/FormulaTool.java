/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

	public enum Type {
		ORGANIC(new String[]{"H", "B", "C", "N", "O", "F", "Si", "P", "S", "Cl", "Br", "I"}),
		GROUP1(new String[]{"Li", "Na", "K", "Rb", "Cs", "Fr"});
		private String[] elems;

		private Type(String[] elems) {
			this.elems = elems;
		}

		public String[] getElems() {
			return elems;
		}

		public boolean contains(String elementType) {
			if (elementType == null) {
				return false;
			}
			for (String elem : elems) {
				if (elem.equals(elementType)) {
					return true;
				}
			}
			return false;
		}
		
		public boolean includesAnyOf(String[] elementTypes) {
			if (elementTypes == null) {
				return false;
			}
			for (String elementType : elementTypes) {
				if (contains(elementType)) {
					return true;
				}
			}
			return false;
		}

		public boolean includesAllOf(String[] elementTypes) {
			if (elementTypes == null) {
				return false;
			}
			for (String elementType : elementTypes) {
				if (!contains(elementType)) {
					return false;
				}
			}
			return true;
		}

		public boolean includesNoneOf(String[] elementTypes) {
			if (elementTypes == null) {
				return true;
			}
			for (String elementType : elementTypes) {
				if (contains(elementType)) {
					return false;
				}
			}
			return true;
		}


	}

    /** dewisott */
	public static String HYDROGEN_COUNT = "hydrogenCount";
	
	private CMLFormula formula;
	private AbstractFormulaIdNameDisplay formulaDisplay = FormulaDisplay.DEFAULT;

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

	private void enableFormulaDisplay() {
    	if (formulaDisplay == null) {
    		formulaDisplay = FormulaDisplay.DEFAULT;
    	}
	}
	
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
		enableFormulaDisplay();
    	String s = formula.getConcise();
    	g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
    	SVGG svgg = new SVGG();
    	if (s != null) {
	    	String[] ss = s.split(CMLConstants.S_SPACE);
	    	// charge at end?
	    	int evenLength = (ss.length % 2 == 1) ? ss.length - 1 : ss.length;
	    	Real2 offset = new Real2(0.0, 0.0);
	    	for (int i = 0; i < evenLength; i += 2) {
	    		int isub = i+1;
	    		SVGText atomSVG = new SVGText(offset, ss[i]);
	    		atomSVG.setFontSize(formulaDisplay.getFontSizeAtom());
	    		svgg.appendChild(atomSVG);
	    		double widthFactorGuessingLowerCase = Math.min( ss[i].length(), 1.6);
	    		offset = offset.plus(new Real2(
	    				formulaDisplay.getFontSizeAtom() * formulaDisplay.getWidthFactor() * 
	    				widthFactorGuessingLowerCase,
	    				0.0));
	    		String countString = ss[isub];
	    		int count = Integer.parseInt(countString);
	    		if (count != 1) {
	        		offset = offset.plus(new Real2(0.0, formulaDisplay.getSubscriptShift()));
	        		SVGText countSVG = new SVGText(offset, countString);
	        		countSVG.setFontSize(formulaDisplay.getFontSizeCount());
	        		svgg.appendChild(countSVG);
	        		offset = offset.plus(new Real2(formulaDisplay.getFontSizeCount() * countString.length() * formulaDisplay.getWidthFactor(), -formulaDisplay.getSubscriptShift()));
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
	    		offset = offset.plus(new Real2(0.0, formulaDisplay.getSuperscriptShift()));
	    		SVGText chargeSVG = new SVGText(offset, chargeString);
	    		chargeSVG.setFontSize(formulaDisplay.getFontSizeCharge());
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
	
	public boolean hasAllElementsBelongingTo(Type type) {
		String[] elementTypes = formula.getElementTypes();
		return type.includesAllOf(elementTypes);		
	}
	
	public boolean hasAnyElementsBelongingTo(Type type) {
		String[] elementTypes = formula.getElementTypes();
		return type.includesAnyOf(elementTypes);		
	}

	public boolean hasNoElementsBelongingTo(Type type) {
		String[] elementTypes = formula.getElementTypes();
		return type.includesNoneOf(elementTypes);		
	}

	public boolean hasAnyElementsBelongingTo(Type[] types) {
		String[] elementTypes = formula.getElementTypes();
		for (Type type : types) {
			if (type.includesAnyOf(elementTypes)) {
				return true;
			}
		}
		return false;
	}


}

