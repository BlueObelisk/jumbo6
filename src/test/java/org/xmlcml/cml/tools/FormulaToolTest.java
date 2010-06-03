package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.CMLFormula;


/**
 * @author pm286
 * 
 */
public class FormulaToolTest {
	private static Logger LOG = Logger.getLogger(FormulaToolTest.class);


	/**
	 */
	@Before
	public void setUp() {
	}

	@Test 
	public void testTypeContains1() {

		Assert.assertTrue(FormulaTool.Type.GROUP1.contains("Na"));

	}
	@Test 
	public void testTypeContains2() {

		Assert.assertFalse(FormulaTool.Type.GROUP1.contains("Ne"));

	}
	@Test 
	public void testTypeContainsAllOf1() {
		String[] elements = {"Na", "K"};
		Assert.assertTrue(FormulaTool.Type.GROUP1.includesAllOf(elements));

	}
	@Test 
	public void testTypeContainsAllOf2() {
		String[] elements = {"Na", "Pt"};
		Assert.assertFalse(FormulaTool.Type.GROUP1.includesAllOf(elements));

	}

	@Test 
	public void testTypeContainsAnyOf1() {
		String[] elements = {"Na", "K"};
		Assert.assertTrue(FormulaTool.Type.GROUP1.includesAnyOf(elements));

	}
	@Test 
	public void testTypeContainsAnyOf2() {
		String[] elements = {"Na", "Pt"};
		Assert.assertTrue(FormulaTool.Type.GROUP1.includesAnyOf(elements));

	}

	@Test 
	public void testTypeContainsAnyOf3() {
		String[] elements = {"Pd", "Pt"};
		Assert.assertFalse(FormulaTool.Type.GROUP1.includesAnyOf(elements));

	}

	@Test 
	public void testTypeContainsNoneOf1() {
		String[] elements = {"Pd", "Pt"};
		Assert.assertTrue(FormulaTool.Type.GROUP1.includesNoneOf(elements));

	}

	@Test 
	public void testTypeContainsNoneOf2() {
		String[] elements = {"Na", "Pt"};
		Assert.assertFalse(FormulaTool.Type.GROUP1.includesNoneOf(elements));

	}

	@Test 
	public void testFormulaIncludesAny1() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertFalse(formulaTool.hasAnyElementsBelongingTo(FormulaTool.Type.GROUP1));

	}
	
	@Test 
	public void testFormulaIncludesAny2() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertTrue(formulaTool.hasAnyElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}
	@Test 
	public void testFormulaIncludesAny3() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4 O 2 Cu 1");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertTrue(formulaTool.hasAnyElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}

	@Test 
	public void testFormulaIncludesAll0() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertTrue(formulaTool.hasAllElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}

	@Test 
	public void testFormulaIncludesAll1() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertFalse(formulaTool.hasAllElementsBelongingTo(FormulaTool.Type.GROUP1));

	}
	
	@Test 
	public void testFormulaIncludesAll2() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4 Se 1");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertFalse(formulaTool.hasAllElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}
	

	@Test 
	public void testFormulaIncludesNone1() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertTrue(formulaTool.hasNoElementsBelongingTo(FormulaTool.Type.GROUP1));

	}
	
	@Test 
	public void testFormulaIncludesNone2() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertFalse(formulaTool.hasNoElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}

	
	@Test 
	public void testFormulaIncludesNone3() {
		CMLFormula formula = new CMLFormula();
		formula.setConcise("C 3 H 4 Se 1");
		FormulaTool formulaTool = FormulaTool.getOrCreateTool(formula);
		Assert.assertFalse(formulaTool.hasNoElementsBelongingTo(FormulaTool.Type.ORGANIC));

	}


}
