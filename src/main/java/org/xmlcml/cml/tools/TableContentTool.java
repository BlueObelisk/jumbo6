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



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLTableCell;
import org.xmlcml.cml.element.CMLTableContent;
import org.xmlcml.cml.element.CMLTableHeader;
import org.xmlcml.cml.element.CMLTableHeaderCell;
import org.xmlcml.cml.element.CMLTableRow;
import org.xmlcml.cml.element.CMLTableRowList;

/**
 * tool for managing tableContent
 *
 * @author pmr
 *
 */
public class TableContentTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TableContentTool.class.getName());

	CMLTableContent tableContent = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TableContentTool(CMLTableContent tableContent) throws RuntimeException {
		init();
		this.tableContent = tableContent;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTableContent getTableContent() {
		return this.tableContent;
	}

    
	/** gets TableContentTool associated with tableContent.
	 * if null creates one and sets it in tableContent
	 * @param tableContent
	 * @return tool
	 */
	public static TableContentTool getOrCreateTool(CMLTableContent tableContent) {
		TableContentTool tableContentTool = null;
		if (tableContent != null) {
			tableContentTool = (TableContentTool) tableContent.getTool();
			if (tableContentTool == null) {
				tableContentTool = new TableContentTool(tableContent);
				tableContent.setTool(tableContentTool);
			}
		}
		return tableContentTool;
	}



   private void checkRectangular(int rowCount, int columnCount, int total) {
       if (rowCount * columnCount != total) {
           throw new RuntimeException("non rectangular table: " +
                   +rowCount +" * "+ columnCount +" != "+total);
       }
   }

   private void addToArray(CMLArray array, String dataType, String s) {
       try {
           if (XSD_DOUBLE.equals(dataType)) {
               double d = new Double(s).doubleValue();
               array.append(d);
           } else if (XSD_INTEGER.equals(dataType)) {
               int i = Integer.parseInt(s);
               array.append(i);
           } else {
               array.append(s);
           }
       } catch (Exception e) {
           throw new RuntimeException(
                   "wrong data type ["+s+"]["+dataType+S_RSQUARE+e.getMessage());
       }
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
      String[] strings = tableContent.getStrings();
      checkRectangular(rowCount, columnCount, strings.length);
      CMLElements<CMLTableHeaderCell> tableHeaderCells =
          tableHeader.getTableHeaderCellElements();
      int headerColumnCount = tableHeaderCells.size();
      if (headerColumnCount != columnCount) {
          throw new RuntimeException(
              "inconsistent column count ("+columnCount+" != "+headerColumnCount);
      }
      for (int j = 0; j < columnCount; j++) {
          CMLArray array = TableTool.createArray(tableHeaderCells.get(j), tableContent.getDelimiter());
          String dataType = tableHeaderCells.get(j).getDataType();
          arrayList.appendChild(array);
          for (int i = 0; i < rowCount; i++) {
              addToArray(array, dataType, strings[j + i * columnCount]);
          }
      }
      return arrayList;
  }
   
  /** create populated tableRowList.
  *
  * @param rowCount
  * @param columnCount
  * @return tableRowList (may be empty)
  */
 public CMLTableRowList createTableRowList(int rowCount, int columnCount) {
     CMLTableRowList tableRowList = new CMLTableRowList();
     String[] strings = tableContent.getStrings();
     this.checkRectangular(rowCount, columnCount, strings.length);
     int count = 0;
     for (int i = 0; i < rowCount; i++) {
         CMLTableRow tableRow = new CMLTableRow();
         tableRowList.appendChild(tableRow);
         for (int j = 0; j < columnCount; j++) {
             CMLTableCell tableCell = new CMLTableCell();
             CMLUtil.setXMLContent(tableCell, strings[count++]);
             tableRow.appendChild(tableCell);
         }
     }
     return tableRowList;
 }
 

};