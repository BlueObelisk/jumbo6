package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


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

//	/**
//	 */
//	@Test
//	public void testSubtract() {
//		
//		CMLFormula f1 = CMLFormula.createFormula("C 2 H 6 O 1");
//		CMLFormula f2 = CMLFormula.createFormula("H 2 O 1");
//		FormulaTool formulaTool1 = FormulaTool.getOrCreateTool(f1);
//		CMLFormula f3 = formulaTool1.subtractFormula(f2);
//		CMLFormula f3Ref = new CMLFormula();
//		f3Ref.setConcise("C 2 H 4");
//		TestUtils.assertEqualsCanonically("subtract", f3Ref, f3, true);
//	}
	
	@Test 
	public void testDummy() {
		Assert.assertTrue(true);
	}

}
