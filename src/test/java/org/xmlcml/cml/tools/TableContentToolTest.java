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

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLTableRowList;
import org.xmlcml.cml.test.TableFixture;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;

/**
 * test TableTool.
 * 
 * @author pmr
 */
public class TableContentToolTest {

	TableFixture fixture;

	@Before
	public void setup() {
		fixture = new TableFixture();
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLTableContent#createTableRowList(int, int)}
	 */
	@Test
	public void createTableRowList() {
		CMLTableRowList rowList = TableContentTool.getOrCreateTool(
				fixture.tableContent).createTableRowList(3, 2);
		String rowS = CMLConstants.S_EMPTY + "<tableRowList " + CMLConstants.CML_XMLNS + ">"
				+ "  <tableRow>" + "    <tableCell>1</tableCell>"
				+ "    <tableCell>a</tableCell>" + "  </tableRow>"
				+ "  <tableRow>" + "    <tableCell>2</tableCell>"
				+ "    <tableCell>b</tableCell>" + "  </tableRow>"
				+ "  <tableRow>" + "    <tableCell>3</tableCell>"
				+ "    <tableCell>c</tableCell>" + "  </tableRow>"
				+ "</tableRowList>" + CMLConstants.S_EMPTY;
		CMLTableRowList expected = (CMLTableRowList)CMLXOMTestUtils.parseValidString(rowS);
		boolean stripWhite = true;
		CMLXOMTestUtils.assertEqualsCanonically("row list", expected, rowList, stripWhite);
	}

}
