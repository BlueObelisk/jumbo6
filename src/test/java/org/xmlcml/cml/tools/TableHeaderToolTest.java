package org.xmlcml.cml.tools;


import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTableHeader;
import org.xmlcml.cml.element.CMLTableRow;
import org.xmlcml.cml.element.CMLTableRowList;
import org.xmlcml.cml.test.TableFixture;
import org.xmlcml.cml.testutil.JumboTestUtils;

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
