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
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTableCell;
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
public class TableToolTest {
	TableFixture fix = new TableFixture();

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLTableContent#createArrayList(int, int, org.xmlcml.cml.element.CMLTableHeader)}
	 * .
	 */
	@Test
	public void testCreateArrayList() {
		CMLArrayList arrayList = (CMLArrayList) TableContentTool
				.getOrCreateTool(fix.tableContent).createArrayList(3, 2,
						fix.tableHeader);
		String rowS = CMLConstants.S_EMPTY
				+ "<arrayList "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <array title='foo' id='th1' dictRef='c:foo' dataType='xsd:string' size='3'>1 2 3</array>"
				+ "  <array title='bar' id='th2' dictRef='c:bar' dataType='xsd:string' size='3'>a b c</array>"
				+ "</arrayList>" + CMLConstants.S_EMPTY;
		CMLArrayList expected = (CMLArrayList) JumboTestUtils.parseValidString(rowS);
		boolean stripWhite = true;
		JumboTestUtils.assertEqualsCanonically("row list", expected, arrayList, stripWhite);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLTableRowList#addColumn(org.xmlcml.cml.element.lite.CMLArray)}
	 * .
	 */
	@Test
	public void testAddColumnCMLArray() {
		CMLArray array = new CMLArray(new int[] { 11, 12, 13 });
		CMLTableRowList tableRowList1 = new CMLTableRowList(fix.tableRowList);
		TableRowListTool.getOrCreateTool(tableRowList1).addColumn(array);
		String ss = "" + "<tableRowList " + CMLConstants.CML_XMLNS + ">" + "<tableRow>"
				+ "<tableCell>1</tableCell>" + "<tableCell>a</tableCell>"
				+ "<tableCell>11</tableCell>" + "</tableRow>" + "<tableRow>"
				+ "<tableCell>2</tableCell>" + "<tableCell>b</tableCell>"
				+ "<tableCell>12</tableCell>" + "</tableRow>" + "<tableRow>"
				+ "<tableCell>3</tableCell>" + "<tableCell>c</tableCell>"
				+ "<tableCell>13</tableCell>" + "</tableRow>"
				+ "</tableRowList>";
		CMLTableRowList expected = (CMLTableRowList)JumboTestUtils.parseValidString(ss);
		JumboTestUtils.assertEqualsCanonically("tablerow", expected, tableRowList1, true);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLArrayList#createTableRowList()}.
	 */
	@Test
	public final void testCreateTableRowList() {
		CMLTableRowList tableRowList1 = TableTool
				.createTableRowList(fix.arrayList);
		JumboTestUtils.assertEqualsCanonically("tableRowList", fix.tableRowList,
				tableRowList1, true);
	}
	
	@Test
	public void addColumn() {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		CMLArray array = new CMLArray(new String[]{"a", "b", "c"});
		tableTool.addArray(array);
		String expected = "<table xmlns='http://www.xml-cml.org/schema'>" +
				"<tableRowList>" +
				" <tableRow>" +
				"  <tableCell>a</tableCell>" +
				" </tableRow>" +
				" <tableRow>" +
				"  <tableCell>b</tableCell></tableRow>" +
				" <tableRow>" +
				"  <tableCell>c</tableCell>" +
				" </tableRow>" +
				"</tableRowList>" +
				"<tableHeader>" +
			    " <tableHeaderCell dataType='xsd:string'/>" +
			    " </tableHeader>" +
				"</table>";
		JumboTestUtils.assertEqualsIncludingFloat("table", CMLUtil.parseXML(expected), table, true, 0.00001);
		
	}
	
	@Test
	public void addColumns() {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		CMLArray array1 = new CMLArray(new String[]{"a", "b", "c"});
		CMLArray array2 = new CMLArray(new int[]{1, 2, 3});
		CMLArray array3 = new CMLArray(new double[]{1.1, 2.2, 3.3});
		tableTool.addArray(array1);
		tableTool.addArray(array2);
		tableTool.addArray(array3);
		String expected = "<table xmlns='http://www.xml-cml.org/schema'>" +
				"<tableRowList>" +
				" <tableRow>" +
				"  <tableCell>a</tableCell><tableCell>1</tableCell><tableCell>1.1</tableCell>" +
				" </tableRow>" +
				" <tableRow>" +
				"  <tableCell>b</tableCell><tableCell>2</tableCell><tableCell>2.2</tableCell>" +
				" </tableRow>" +
				" <tableRow>" +
				"  <tableCell>c</tableCell><tableCell>3</tableCell><tableCell>3.3</tableCell>" +
				" </tableRow>" +
				"</tableRowList>" +
				"<tableHeader>" +
			    " <tableHeaderCell dataType='xsd:string'/>" +
			    " <tableHeaderCell dataType='xsd:integer'/>" +
			    " <tableHeaderCell dataType='xsd:double'/>" +
			    " </tableHeader>" +
				"</table>";
		JumboTestUtils.assertEqualsIncludingFloat("table", CMLUtil.parseXML(expected), table, true, 0.00001);
		
	}
	@Test
	public void addColumnsReadRows() {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		CMLArray array1 = new CMLArray(new String[]{"a", "b", "c"});
		CMLArray array2 = new CMLArray(new int[]{1, 2, 3});
		CMLArray array3 = new CMLArray(new double[]{1.1, 2.2, 3.3});
		CMLArray array4 = new CMLArray(new double[]{1.11, 2.22, 3.33});
		tableTool.addArray(array1);
		tableTool.addArray(array2);
		tableTool.addArray(array3);
		tableTool.addArray(array4);
		CMLElements<CMLTableRow> rows = tableTool.getRowElements();
		Assert.assertEquals("size", 3, rows.size());
		CMLTableRow row = rows.get(0);
		String expected = "<tableRow xmlns='http://www.xml-cml.org/schema'>		  " +
				"<tableCell>a</tableCell>		  " +
				"<tableCell>1</tableCell>		  " +
				"<tableCell>1.1</tableCell>		" +
				"<tableCell>1.11</tableCell>		" +
				"</tableRow>	";
		JumboTestUtils.assertEqualsIncludingFloat("row", CMLUtil.parseXML(expected), row, true, 0.00001);
	}
	
	@Test
	public void addColumnsDictRefs() {
		TableTool tableTool = createTableTool1();
		TableHeaderTool tableHeaderTool = tableTool.getTableHeaderTool();
		CMLTableHeader tableHeader = tableHeaderTool.getTableHeader();
				String expected = 
				"<tableHeader xmlns='http://www.xml-cml.org/schema'>" +
				"  <tableHeaderCell dictRef='foo:bar1' dataType='xsd:string'/>" +
				"  <tableHeaderCell dictRef='foo:bar2' dataType='xsd:integer'/>" +
				"  <tableHeaderCell dictRef='foo:bar3' dataType='xsd:double'/>" +
				"  <tableHeaderCell dictRef='foo:bar4' dataType='xsd:double'/>" +
				"</tableHeader>";
		JumboTestUtils.assertEqualsIncludingFloat("row", CMLUtil.parseXML(expected), tableHeader, true, 0.00001);
	}

	public static TableTool createTableTool1() {
		CMLTable table = new CMLTable();
		TableTool tableTool = TableTool.getOrCreateTool(table);
		CMLArray array1 = new CMLArray(new String[]{"a", "b", "c"});
		array1.setDictRef("foo:bar1");
		CMLArray array2 = new CMLArray(new int[]{1, 2, 3});
		array2.setDictRef("foo:bar2");
		CMLArray array3 = new CMLArray(new double[]{1.1, 2.2, 3.3});
		array3.setDictRef("foo:bar3");
		CMLArray array4 = new CMLArray(new double[]{1.11, 2.22, 3.33});
		array4.setDictRef("foo:bar4");
		tableTool.addArray(array1);
		tableTool.addArray(array2);
		tableTool.addArray(array3);
		tableTool.addArray(array4);
		return tableTool;
	}
	
	@Test
	public void testGetRowCell() {
		TableTool tableTool = TableToolTest.createTableTool1();
		CMLTableCell cell = tableTool.getTableCell(2, "foo:bar1");
		Assert.assertNotNull(cell);
		String value = cell.getValue();
		Assert.assertEquals("cell", "c", value);
		cell = tableTool.getTableCell(0, "foo:bar3");
		Assert.assertNotNull(cell);
		value = cell.getValue();
		Assert.assertEquals("cell", "1.1", value);
	}
	
	@Test
	public void testGetRowCell1() {
		TableTool tableTool = TableToolTest.createTableTool1();
		CMLTableCell cell = tableTool.getTableCell(2, "foo:bar1zz");
		Assert.assertNull(cell);
		cell = tableTool.getTableCell(5, "foo:bar1");
		Assert.assertNull(cell);
	}

}
