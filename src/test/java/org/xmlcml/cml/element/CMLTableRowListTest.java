/**
 * 
 */
package org.xmlcml.cml.element;

import org.junit.Test;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTableContent;
import org.xmlcml.cml.element.CMLTableRowList;

/**
 * @author pm286
 *
 */
public class CMLTableRowListTest extends AbstractTableTest {

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRowList#createTableContent()}.
     */
    @Test
    public final void testCreateTableContent() {
        CMLTableContent tableContent0 = tableRowList.createTableContent();
        boolean stripWhite = true;
        assertEqualsCanonically("table content", tableContent, tableContent0, stripWhite);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRowList#addColumn(org.xmlcml.cml.element.CMLArray)}.
     */
    @Test
    public final void testAddColumnCMLArray() {
        CMLArray array = new CMLArray(new int[]{11, 12, 13});
        CMLTableRowList tableRowList1 = new CMLTableRowList(tableRowList);
        tableRowList1.addColumn(array);
        String ss = "" +
            "<tableRowList "+CML_XMLNS+">"+
              "<tableRow>"+
                "<tableCell>1</tableCell>"+
                "<tableCell>a</tableCell>"+
                "<tableCell>11</tableCell>"+
              "</tableRow>"+
              "<tableRow>"+
                "<tableCell>2</tableCell>"+
                "<tableCell>b</tableCell>"+
                "<tableCell>12</tableCell>"+
              "</tableRow>"+
              "<tableRow>"+
                "<tableCell>3</tableCell>"+
                "<tableCell>c</tableCell>"+
                "<tableCell>13</tableCell>"+
              "</tableRow>"+
            "</tableRowList>";
        CMLTableRowList expected = (CMLTableRowList) parseValidString(ss);
        assertEqualsCanonically("tablerow", expected, tableRowList1, true);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRowList#addColumn(org.xmlcml.cml.element.CMLList)}.
     */
    @Test
    public final void testAddColumnCMLList() {
        CMLList cmlList = new CMLList();
        cmlList.appendChild(new CMLScalar(10.1));
        cmlList.appendChild(new CMLScalar(20.2));
        cmlList.appendChild(new CMLScalar(30.3));
        CMLTableRowList tableRowList1 = new CMLTableRowList(tableRowList);
        tableRowList1.addColumn(cmlList);
        String ss = "" +
            "<tableRowList "+CML_XMLNS+">"+
              "<tableRow>"+
                "<tableCell>1</tableCell>"+
                "<tableCell>a</tableCell>"+
                "<tableCell><scalar dataType='xsd:double'>10.1</scalar></tableCell>"+
              "</tableRow>"+
              "<tableRow>"+
                "<tableCell>2</tableCell>"+
                "<tableCell>b</tableCell>"+
                "<tableCell><scalar dataType='xsd:double'>20.2</scalar></tableCell>"+
              "</tableRow>"+
              "<tableRow>"+
                "<tableCell>3</tableCell>"+
                "<tableCell>c</tableCell>"+
                "<tableCell><scalar dataType='xsd:double'>30.3</scalar></tableCell>"+
              "</tableRow>"+
            "</tableRowList>";
        CMLTableRowList expected = (CMLTableRowList) parseValidString(ss);
        assertEqualsCanonically("tablerow", expected, tableRowList1, true);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRowList#createArrayList(int, int, org.xmlcml.cml.element.CMLTableHeader)}.
     */
    @Test
    public final void testCreateArrayList() {
        CMLArrayList arrayList1 = tableRowList.createArrayList(3, 2, tableHeader);
        assertEqualsCanonically("tablerow", arrayList, arrayList1, true);
    }

}
