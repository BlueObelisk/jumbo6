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
