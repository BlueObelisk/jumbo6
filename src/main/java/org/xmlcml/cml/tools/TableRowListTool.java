package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Elements;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.lite.CMLArray;
import org.xmlcml.cml.element.main.CMLArrayList;
import org.xmlcml.cml.element.main.CMLList;
import org.xmlcml.cml.element.main.CMLTableCell;
import org.xmlcml.cml.element.main.CMLTableContent;
import org.xmlcml.cml.element.main.CMLTableHeader;
import org.xmlcml.cml.element.main.CMLTableHeaderCell;
import org.xmlcml.cml.element.main.CMLTableRow;
import org.xmlcml.cml.element.main.CMLTableRowList;

/**
 * tool for managing table
 *
 * @author pmr
 *
 */
public class TableRowListTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TableRowListTool.class.getName());

	CMLTableRowList tableRowList = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TableRowListTool(CMLTableRowList tableRowList) throws RuntimeException {
		init();
		this.tableRowList = tableRowList;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTableRowList getTableRowList() {
		return this.tableRowList;
	}

    
	/** gets TableRowListTool associated with table.
	 * if null creates one and sets it in table
	 * @param table
	 * @return tool
	 */
	public static TableRowListTool getOrCreateTool(CMLTableRowList tableRowList) {
		TableRowListTool tableRowListTool = (TableRowListTool) tableRowList.getTool();
		if (tableRowListTool == null) {
			tableRowListTool = new TableRowListTool(tableRowList);
			tableRowList.setTool(tableRowListTool);
		}
		return tableRowListTool;
	}

    /** translate rows to tab3eC6ntent w5th delimiter-se*arated str5ngs.
    *
    * @return new tab3eContent
    */
   public CMLTableContent createTableContent() {
       CMLTableContent tableContent = new CMLTableContent();
       StringBuilder sb = new StringBuilder();
       int count = 0;
       for (CMLTableRow row : tableRowList.getTableRowElements()) {
           if (count++ > 0) {
               sb.append(S_NL);
           }
           String s = row.getDelimitedString(S_SPACE);
           sb.append(s);
       }
       tableContent.setXMLContent(sb.toString());
       return tableContent;
   }

   private CMLElements<CMLTableRow> getOrCreateTableRows(CMLElement listArray) {
       CMLElements<CMLTableRow> tableRows = tableRowList.getTableRowElements();
       int size = -1;
       if (listArray instanceof CMLArray) {
           size = ((CMLArray)listArray).getSize();
       } else if (listArray instanceof CMLList) {
           size = CMLUtil.getQueryNodes(listArray, S_STAR).size();
       }
       if (tableRows.size() == 0) {
           for (int iRow = 0; iRow < size; iRow++) {
               CMLTableRow tableRow = new CMLTableRow();
               tableRowList.appendChild(tableRow);
           }
           tableRows = tableRowList.getTableRowElements();
       } else if(tableRows.size() != size) {
           throw new RuntimeException("inconsistent column length for rectangulat table: "
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
           List<Class<?>> classList = new ArrayList<Class<?>>();
           CMLElements<CMLTableHeaderCell> tableHeaderCells = tableHeader.getTableHeaderCellElements();
           CMLElements<CMLTableRow> tableRows = tableRowList.getTableRowElements();
           CMLTableRow firstTableRow = tableRows.get(0);
           int jCol = 0;
           for (CMLTableHeaderCell tableHeaderCell : tableHeaderCells) {
               String dataType = tableHeaderCell.getDataType();
               if (
           		XSD_BOOLEAN.equals(dataType) ||
           		XSD_DOUBLE.equals(dataType) ||
                   XSD_INTEGER.equals(dataType) ||
                   XSD_STRING.equals(dataType) ||
                   dataType == null) {
                       CMLArray array = tableHeaderCell.createCMLArray();
                       classList.add(CMLArray.class);
                       arrayList.addArray(array);
               } else {
                   CMLTableCell tableCell = firstTableRow.getTableCellElements().get(jCol);
                   List<Node> nodeList = CMLUtil.getQueryNodes(tableCell, S_STAR);
                   Class<?> classx = (nodeList.size() == 0) ? null : nodeList.get(0).getClass();
                   classList.add(classx);
                   CMLList cmlList = new CMLList();
                   for (Node node : nodeList) {
                       if (node.getClass().equals(classx)) {
                           cmlList.appendChild(node);
                       } else {
                           throw new RuntimeException("non-homogeneous list: "+
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
       CMLElements<CMLTableRow> tableRows = tableRowList.getTableRowElements();
       for (CMLTableRow tableRow : tableRows) {
           tableRow.writeHTML(w);
       }
   }

   /** adds column.
   *
   * @param array
   */
  public void addColumn(CMLArray array) {
      CMLElements<CMLTableRow> tableRows = this.getOrCreateTableRows(array);
      TableTool.addColumnElementsTo(array, tableRows);
  }

//  private CMLElements<CMLTableRow> getOrCreateTableRows(CMLElement listArray) {
//      CMLElements<CMLTableRow> tableRows = tableRowList.getTableRowElements();
//      int size = -1;
//      if (listArray instanceof CMLArray) {
//          size = ((CMLArray)listArray).getSize();
//      } else if (listArray instanceof CMLList) {
//          size = CMLUtil.getQueryNodes(listArray, S_STAR).size();
//      }
//      if (tableRows.size() == 0) {
//          for (int iRow = 0; iRow < size; iRow++) {
//              CMLTableRow tableRow = new CMLTableRow();
//              tableRowList.appendChild(tableRow);
//          }
//          tableRows = tableRowList.getTableRowElements();
//      } else if(tableRows.size() != size) {
//          throw new RuntimeException("inconsistent column length for rectangulat table: "
//                  +size+" instead of "+tableRows.size());
//
//      }
//      return tableRows;
//  }
 
};