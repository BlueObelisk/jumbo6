/**
 * 
 */
package org.xmlcml.cml.element;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.parseValidString;

import org.junit.Test;
import org.xmlcml.euclid.test.StringTestBase;

/**
 * @author pm286
 *
 */
public class CMLTableContentTest extends AbstractTableTest {

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableContent#getStrings()}.
     */
    @Test
    public final void testGetStrings() {
        String[] ss = tableContent.getStrings();
        StringTestBase.assertEquals("strings", 
                new String[]{"1", "a", "2", "b", "3", "c"}, ss);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableContent#createTableRowList(int, int)}.
     */
    @Test
    public final void testCreateTableRowList() {
        CMLTableRowList rowList = tableContent.createTableRowList(3, 2);
        String rowS = S_EMPTY +
                "<tableRowList "+CML_XMLNS+">" +
                "  <tableRow>" +
                "    <tableCell>1</tableCell>" +
                "    <tableCell>a</tableCell>" +
                "  </tableRow>" +
                "  <tableRow>" +
                "    <tableCell>2</tableCell>" +
                "    <tableCell>b</tableCell>" +
                "  </tableRow>" +
                "  <tableRow>" +
                "    <tableCell>3</tableCell>" +
                "    <tableCell>c</tableCell>" +
                "  </tableRow>" +
                "</tableRowList>" +
                S_EMPTY;
        CMLTableRowList expected = (CMLTableRowList) parseValidString(rowS);
        boolean stripWhite = true;
        assertEqualsCanonically("row list", expected, rowList, stripWhite);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableContent#createArrayList(int, int, org.xmlcml.cml.element.CMLTableHeader)}.
     */
    @Test
    public final void testCreateArrayList() {
        CMLArrayList arrayList = (CMLArrayList) tableContent.createArrayList(3, 2, tableHeader);
        String rowS = S_EMPTY +
                "<arrayList "+CML_XMLNS+">" +
                "  <array title='foo' id='th1' dictRef='c:foo' dataType='xsd:string' size='3'>1 2 3</array>" +
                "  <array title='bar' id='th2' dictRef='c:bar' dataType='xsd:string' size='3'>a b c</array>" +
                "</arrayList>" +
                S_EMPTY;
        CMLArrayList expected = (CMLArrayList) parseValidString(rowS);
        boolean stripWhite = true;
        assertEqualsCanonically("row list", expected, arrayList, stripWhite);
    }

}
