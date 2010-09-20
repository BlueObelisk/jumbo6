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
