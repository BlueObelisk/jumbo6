package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.cml.test.CMLAssert.assertEqualsCanonically;
import static org.xmlcml.cml.test.CMLAssert.parseValidString;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.element.main.CMLTableRowList;
import org.xmlcml.cml.test.TableFixture;

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
		String rowS = S_EMPTY + "<tableRowList " + CML_XMLNS + ">"
				+ "  <tableRow>" + "    <tableCell>1</tableCell>"
				+ "    <tableCell>a</tableCell>" + "  </tableRow>"
				+ "  <tableRow>" + "    <tableCell>2</tableCell>"
				+ "    <tableCell>b</tableCell>" + "  </tableRow>"
				+ "  <tableRow>" + "    <tableCell>3</tableCell>"
				+ "    <tableCell>c</tableCell>" + "  </tableRow>"
				+ "</tableRowList>" + S_EMPTY;
		CMLTableRowList expected = (CMLTableRowList) parseValidString(rowS);
		boolean stripWhite = true;
		assertEqualsCanonically("row list", expected, rowList, stripWhite);
	}

}
