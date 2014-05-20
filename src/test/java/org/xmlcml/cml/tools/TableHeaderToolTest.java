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
import org.xmlcml.cml.test.TableFixture;

/**
 * test TableTool.
 * 
 * @author pmr
 * 
 */
public class TableHeaderToolTest {
	TableFixture fix = new TableFixture();

	@Test
	public void testCellIndex() {
		TableTool tableTool = TableToolTest.createTableTool1();
		TableHeaderTool tableHeaderTool = tableTool.getTableHeaderTool();
		int i = tableHeaderTool.indexOfColumn("grot");
		Assert.assertEquals("index", -1, i);
	}
	
	@Test
	public void testCellIndex1() {
		TableTool tableTool = TableToolTest.createTableTool1();
		TableHeaderTool tableHeaderTool = tableTool.getTableHeaderTool();
		int i = tableHeaderTool.indexOfColumn("foo:bar1");
		Assert.assertEquals("index", 0, i);
	}
}
