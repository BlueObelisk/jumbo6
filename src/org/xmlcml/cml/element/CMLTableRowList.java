// /*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/
package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;

/** List of rows in rowBased table.
*
* 
* \n Metadata for rows must be defined in tableHeader.\n 
* 
* user-modifiable class autogenerated from schema if no class exists
* use as a shell which can be edited
* the autogeneration software will not overwrite an existing class file

*/
public class CMLTableRowList extends org.xmlcml.cml.element.AbstractTableRowList {

    /** must give simple documentation.
    *

    */

    public CMLTableRowList() {
    }
    /** must give simple documentation.
    *
    * @param old CMLTableRowList to copy

    */

    public CMLTableRowList(CMLTableRowList old) {
        super((org.xmlcml.cml.element.AbstractTableRowList) old);
    }

    /** copy node .
    *
    * @return Node
    */
    public Node copy() {
        return new CMLTableRowList(this);
    }
    /** create new instance in context of parent, overridable by subclasses.
    *
    * @param parent parent of element to be constructed (ignored by default)
    * @return CMLTableRowList
    */
    public static CMLTableRowList makeElementInContext(Element parent) {
        return new CMLTableRowList();
    }

    /** translate rows to tab3eC6ntent w5th delimiter-se*arated str5ngs.
     * 
     * @return new tab3eContent
     */
    public CMLTableContent createTableContent() {
        CMLTableContent tableContent = new CMLTableContent();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (CMLTableRow row : this.getTableRowElements()) {
            if (count++ > 0) {
                sb.append(S_NL);
            }
            String s = row.getDelimitedString(S_SPACE);
            sb.append(s);
        }
        tableContent.setXMLContent(sb.toString());
        return tableContent;
    }
    
    /** adds column.
     * 
     * @param array
     */
    public void addColumn(CMLArray array) {
        CMLElements<CMLTableRow> tableRows = getOrCreateTableRows(array);
        array.addColumnElementsTo(tableRows);
    }
    
    private CMLElements<CMLTableRow> getOrCreateTableRows(CMLElement listArray) {
        CMLElements<CMLTableRow> tableRows = this.getTableRowElements();
        int size = -1;
        if (listArray instanceof CMLArray) {
            size = ((CMLArray)listArray).getSize();
        } else if (listArray instanceof CMLList) {
            size = CMLUtil.getQueryNodes(listArray, "*").size();
        }
        if (tableRows.size() == 0) {
            for (int iRow = 0; iRow < size; iRow++) {
                CMLTableRow tableRow = new CMLTableRow();
                this.appendChild(tableRow);
            }
            tableRows = this.getTableRowElements();
        } else if(tableRows.size() != size) {
            throw new CMLRuntimeException("inconsistent column length for rectangulat table: "
                    +size+" instead of "+tableRows.size());
                
        }
        return tableRows;
    }
    
    /** add column.
     * 
     * @param list
     */
    public void addColumn(CMLList list) {
        CMLElements<CMLTableRow> tableRows = getOrCreateTableRows(list);
        list.addColumnElementsTo(tableRows);
    }
    
    /** create populated arrayList.
     * 
     * @param rowCount
     * @param columnCount
     * @param tableHeader
     * @return arrayList may be empty
     */
    public CMLArrayList createArrayList(
            int rowCount, int columnCount, CMLTableHeader tableHeader) {
        CMLArrayList arrayList = new CMLArrayList();
        if (rowCount > 0) {
            List<Class> classList = new ArrayList<Class>();
            CMLElements<CMLTableHeaderCell> tableHeaderCells = tableHeader.getTableHeaderCellElements(); 
            CMLElements<CMLTableRow> tableRows = this.getTableRowElements();
            CMLTableRow firstTableRow = tableRows.get(0);
            int jCol = 0;
            for (CMLTableHeaderCell tableHeaderCell : tableHeaderCells) {
                String dataType = tableHeaderCell.getDataType();
                if (XSD_DOUBLE.equals(dataType) ||
                    XSD_INTEGER.equals(dataType) ||
                    XSD_STRING.equals(dataType) ||
                    dataType == null) {
                        CMLArray array = tableHeaderCell.createCMLArray();
                        classList.add(CMLArray.class);
                        arrayList.addArray(array);
                } else {
                    CMLTableCell tableCell = firstTableRow.getTableCellElements().get(jCol);
                    List<Node> nodeList = CMLUtil.getQueryNodes(tableCell, "*");
                    Class classx = (nodeList.size() == 0) ? null : nodeList.get(0).getClass();
                    classList.add(classx);
                    CMLList cmlList = new CMLList();
                    for (Node node : nodeList) {
                        if (node.getClass().equals(classx)) {
                            cmlList.appendChild(node);
                        } else {
                            throw new CMLRuntimeException("non-homogeneous list: "+
                                    node.getClass()+" expected "+classx);
                        }
                    }
                    arrayList.appendChild(cmlList);
                }
                jCol++;
            }
            List<String> dataTypeList = new ArrayList<String>();
            Elements arrays = arrayList.getChildCMLElements(CMLArray.TAG);
            for (CMLTableHeaderCell tableHeaderCell : tableHeaderCells) {
                dataTypeList.add(tableHeaderCell.getDataType());
            }
            for (CMLTableRow tableRow : tableRows) {
                CMLElements<CMLTableCell> tableCells = tableRow.getTableCellElements();
                for (int jColx = 0; jColx < columnCount; jColx++) {
                    CMLTableCell tableCell = tableCells.get(jColx);
                    String dataType = dataTypeList.get(jColx);
                    CMLArray cmlArray = (CMLArray) arrays.get(jColx);
                    tableCell.appendValueTo(dataType, cmlArray);
                }
            }
        }
        return arrayList;
    }
    
    /** write as HTML.
     * 
     * @param w writer
     * @throws IOException
     */
    public void writeHTML(Writer w) throws IOException {
        CMLElements<CMLTableRow> tableRows = this.getTableRowElements();
        for (CMLTableRow tableRow : tableRows) {
            tableRow.writeHTML(w);
        }
    }
    
}
