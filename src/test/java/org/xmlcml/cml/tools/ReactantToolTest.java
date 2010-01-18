package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLReactant;

public class ReactantToolTest {

	@Test
	public void testGetMolarAmount() {
		CMLAmount amount = AmountTool.createMolarAmount(0.2);
		CMLReactant reactant = new CMLReactant();
		reactant.addAmount(amount);
		CMLAmount amountTest = ReactantTool.getOrCreateTool(reactant).getMolarAmount();
		Assert.assertEquals("amount", 0.2, amountTest.getXMLContent(), 0.00001);
	}
	
}
