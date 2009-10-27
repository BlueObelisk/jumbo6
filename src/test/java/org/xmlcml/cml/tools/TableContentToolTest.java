package org.xmlcml.cml.tools;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLTableRowList;
import org.xmlcml.cml.test.TableFixture;
import org.xmlcml.util.TstUtils;

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
		CMLTableRowList expected = (CMLTableRowList)TstUtils.parseValidString(rowS);
		boolean stripWhite = true;
		TstUtils.assertEqualsCanonically("row list", expected, rowList, stripWhite);
	}

}
