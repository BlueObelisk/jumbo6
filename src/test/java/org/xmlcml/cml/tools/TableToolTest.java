package org.xmlcml.cml.tools;


import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.cml.test.CMLAssert.assertEqualsCanonically;
import static org.xmlcml.cml.test.CMLAssert.parseValidString;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import org.junit.Test;
import org.xmlcml.cml.element.lite.CMLArray;
import org.xmlcml.cml.element.main.CMLArrayList;
import org.xmlcml.cml.element.main.CMLTableRowList;
import org.xmlcml.cml.test.TableFixture;

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
		String rowS = S_EMPTY
				+ "<arrayList "
				+ CML_XMLNS
				+ ">"
				+ "  <array title='foo' id='th1' dictRef='c:foo' dataType='xsd:string' size='3'>1 2 3</array>"
				+ "  <array title='bar' id='th2' dictRef='c:bar' dataType='xsd:string' size='3'>a b c</array>"
				+ "</arrayList>" + S_EMPTY;
		CMLArrayList expected = (CMLArrayList) parseValidString(rowS);
		boolean stripWhite = true;
		assertEqualsCanonically("row list", expected, arrayList, stripWhite);
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
		String ss = "" + "<tableRowList " + CML_XMLNS + ">" + "<tableRow>"
				+ "<tableCell>1</tableCell>" + "<tableCell>a</tableCell>"
				+ "<tableCell>11</tableCell>" + "</tableRow>" + "<tableRow>"
				+ "<tableCell>2</tableCell>" + "<tableCell>b</tableCell>"
				+ "<tableCell>12</tableCell>" + "</tableRow>" + "<tableRow>"
				+ "<tableCell>3</tableCell>" + "<tableCell>c</tableCell>"
				+ "<tableCell>13</tableCell>" + "</tableRow>"
				+ "</tableRowList>";
		CMLTableRowList expected = (CMLTableRowList) parseValidString(ss);
		assertEqualsCanonically("tablerow", expected, tableRowList1, true);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLArrayList#createTableRowList()}.
	 */
	@Test
	public final void testCreateTableRowList() {
		CMLTableRowList tableRowList1 = TableTool
				.createTableRowList(fix.arrayList);
		assertEqualsCanonically("tableRowList", fix.tableRowList,
				tableRowList1, true);
	}
}
