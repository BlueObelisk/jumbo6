package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLArrayList;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTableCell;
import org.xmlcml.cml.element.CMLTableContent;
import org.xmlcml.cml.element.CMLTableHeader;
import org.xmlcml.cml.element.CMLTableHeaderCell;
import org.xmlcml.cml.element.CMLTableRow;
import org.xmlcml.cml.element.CMLTableRowList;
import org.xmlcml.cml.element.CMLTable.TableType;
import org.xmlcml.cml.interfacex.HasArraySize;

/**
 * tool for managing table
 *
 * @author pmr
 *
 */
public class TableTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TableTool.class.getName());

	CMLTable table = null;

	private TableRowListTool tableRowListTool;
	private TableHeaderTool tableHeaderTool;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TableTool(CMLTable table) throws RuntimeException {
		init();
		this.table = table;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTable getTable() {
		return this.table;
	}

    
	/** gets TableTool associated with table.
	 * if null creates one and sets it in table
	 * @param table
	 * @return tool
	 */
	public static TableTool getOrCreateTool(CMLTable table) {
		TableTool tableTool = null;
		if (table != null) {
			tableTool = (TableTool) table.getTool();
			if (tableTool == null) {
				tableTool = new TableTool(table);
				table.setTool(tableTool);
			}
		}
		return tableTool;
	}


	/**
	 * constructor. copies metadat from tableHeaderCell
	 * 
	 * @param tableHeaderCell
	 * @param delimiter
	 */
	public static CMLArray createArray(CMLTableHeaderCell tableHeaderCell, String delimiter) {
		CMLArray array = new CMLArray();
		if (tableHeaderCell.getTitleAttribute() != null) {
			array.setTitle(tableHeaderCell.getTitle());
		}
		if (tableHeaderCell.getIdAttribute() != null) {
			array.setId(tableHeaderCell.getId());
		}
		if (tableHeaderCell.getDictRefAttribute() != null) {
			array.setDictRef(tableHeaderCell.getDictRef());
		}
		if (tableHeaderCell.getConventionAttribute() != null) {
			array.setConvention(tableHeaderCell.getConvention());
		}
		if (tableHeaderCell.getConstantToSIAttribute() != null) {
			array.setConstantToSI(tableHeaderCell.getConstantToSI());
		}
		if (tableHeaderCell.getMultiplierToSIAttribute() != null) {
			array.setMultiplierToSI(tableHeaderCell.getMultiplierToSI());
		}
		if (tableHeaderCell.getDataTypeAttribute() != null) {
			array.setDataType(tableHeaderCell.getDataType());
		}
		if (tableHeaderCell.getUnitTypeAttribute() != null) {
			array.setUnitType(tableHeaderCell.getUnitType());
		}
		if (tableHeaderCell.getUnitsAttribute() != null) {
			array.setUnits(tableHeaderCell.getUnits());
		}
		array.setDelimiter(array.getDelimiter());
		return array;
	}

	/**
	 * adds the array as a column to the tableRows.
	 * 
	 * @param tableRows
	 */
	public static void addColumnElementsTo(CMLArray array, CMLElements<CMLTableRow> tableRows) {
		if (array.getSize() != tableRows.size()) {
			throw new RuntimeException("inconsistent column size: "
					+ array.getSize() + " expected " + tableRows.size());
		}
		String dataType = array.getDataType();
		if (XSD_DOUBLE.equals(dataType)) {
			double[] dd = array.getDoubles();
			int j = 0;
			for (double d : dd) {
				tableRows.get(j++).appendChild(new CMLTableCell(d));
			}
		} else if (XSD_INTEGER.equals(dataType)) {
			int[] ii = array.getInts();
			int j = 0;
			for (int i : ii) {
				tableRows.get(j++).appendChild(new CMLTableCell(i));
			}
		} else if (XSD_STRING.equals(dataType) || dataType == null) {
			String[] ss = array.getStrings();
			int j = 0;
			for (String s : ss) {
				tableRows.get(j++).appendChild(new CMLTableCell(s));
			}
		} else {
			throw new RuntimeException("unknown datatype: " + dataType);
		}
	}
	
    /** adds the array as a column to the tableRows.
    *
    * @param tableRows
    */
   public static void addColumnElementsTo(CMLList list, CMLElements<CMLTableRow> tableRows) {
       List<Node> nodes = CMLUtil.getQueryNodes(list, S_STAR);
       if (nodes.size() != tableRows.size()) {
           throw new RuntimeException("inconsistent column size: "+
                   nodes.size()+" expected "+tableRows.size());
       }
       int j = 0;
       Class<?> classx = null;
       for (Node node : nodes) {
           if (classx == null) {
               classx = node.getClass();
           } else if (classx != node.getClass()) {
               throw new RuntimeException("incompatible classes "+
                       node.getClass() +" expected "+classx);
           }
           tableRows.get(j++).appendChild(new CMLTableCell((Element)node));
       }
   }


   private void convert(TableType from, TableType to) {
       if (!table.check(from)) {
           throw new RuntimeException("Inconsistent table");
       }
       CMLTableHeader tableHeader = (CMLTableHeader)
           table.getFirstCMLChild(CMLTableHeader.TAG);
       CMLArrayList arrayList = (CMLArrayList)
           table.getFirstCMLChild(CMLArrayList.TAG);
       CMLTableContent tableContent = (CMLTableContent)
           table.getFirstCMLChild(CMLTableContent.TAG);
       CMLTableRowList tableRowList = (CMLTableRowList)
           table.getFirstCMLChild(CMLTableRowList.TAG);
       TableRowListTool tableRowListTool = TableRowListTool.getOrCreateTool(tableRowList);

       if (from != to) {
           if (from == TableType.COLUMN_BASED) {
               tableHeader = arrayList.createTableHeader();
               arrayList.detach();
               table.appendChild(tableHeader);
               if (to == TableType.ROW_BASED) {
                   tableRowList = createTableRowList(arrayList);
                   table.appendChild(tableRowList);
               } else if (to == TableType.CONTENT_BASED) {
                   tableContent = arrayList.createTableContent();
                   table.appendChild(tableContent);
               }
               if (table.getColumnsAttribute() == null) {
                   table.setColumns(tableHeader.getColumnCount());
               }
               if (table.getRowsAttribute() == null) {
                   table.setRows(arrayList.getRowCount());
               }
           } else if (from == TableType.ROW_BASED) {
               tableRowList.detach();
               if (to == TableType.COLUMN_BASED) {
                   arrayList = tableRowListTool.createArrayList(
                           table.getRows(), table.getColumns(), tableHeader);
                   table.appendChild(arrayList);
                   tableHeader.detach();
               } else if (to == TableType.CONTENT_BASED) {
                   tableContent = tableRowListTool.createTableContent();
                   table.appendChild(tableContent);
               }
           } else if (from == TableType.CONTENT_BASED) {
               tableContent.detach();
               if (to == TableType.COLUMN_BASED) {
            	   TableContentTool tableContentTool = TableContentTool.getOrCreateTool(tableContent);
                   arrayList = tableContentTool.createArrayList(
                           table.getRows(), table.getColumns(), tableHeader);
                   table.appendChild(arrayList);
                   tableHeader.detach();
               } else if (to == TableType.ROW_BASED) {
            	   TableContentTool tableContentTool = TableContentTool.getOrCreateTool(tableContent);
                   tableRowList = tableContentTool.createTableRowList(
                           table.getRows(), table.getColumns());
                   table.appendChild(tableRowList);
               }
           }
       }
   }

   /** forces table to be of a given type.
    * if table is already of that type, no-op.
    * if table is of a different type, attempts to convert
    * to that type
    * @param tt (if null removes all containers and effectively clears table)
    */
   public void resetTableType(TableType tt) {
       if (tt == null) {
           // no-op
       } else if (tt.value.equals(table.getTableType())) {
               // no-op
       } else {
           convert(table.getTableTypeEnum(), tt);
           table.setTableType(tt.value);
       }
   }

   /**
    * output HTML.
    *
    * @param w
    * @throws IOException
    */
   public void writeHTML(Writer w) throws IOException {
       TableType tableType = table.getTableTypeEnum();
       if (TableType.COLUMN_BASED == tableType) {
           CMLTable newTable = new CMLTable(table);
           // convert to row based
           TableTool newTableTool = TableTool.getOrCreateTool(newTable);
           newTableTool.resetTableType(TableType.ROW_BASED);
           newTable.writeHTML(w);
       } else if (TableType.ROW_BASED == tableType) {
           w.write("<table border='1'>");
           CMLTableHeader tableHeader = table.getTableHeaderElements().get(0);
           tableHeader.writeHTML(w);
           CMLTableRowList tableRowList = table.getTableRowListElements().get(0);
           tableRowList.writeHTML(w);
           w.write("\n</table>");
       } else if (TableType.CONTENT_BASED == tableType) {
           CMLTable newTable = new CMLTable(table);
           // convert to row based
           TableTool newTableTool = TableTool.getOrCreateTool(newTable);
           newTableTool.resetTableType(TableType.ROW_BASED);
           newTable.writeHTML(w);
       } else {
           throw new RuntimeException("No tableType given");
       }
   }
   
	  /** create tableRowList.
	  * from arrayList
	  * @return tableRowList
	  */
	 public static CMLTableRowList createTableRowList(CMLArrayList arrayList) {
	     CMLTableRowList tableRowList = new CMLTableRowList();
	     TableRowListTool tableRowListTool = TableRowListTool.getOrCreateTool(tableRowList);
	     List<HasArraySize> listsAndArrays = arrayList.getArrays();
	     if (listsAndArrays.size() > 0) {
	         for (HasArraySize node : listsAndArrays) {
	             if (node instanceof CMLList) {
	                 tableRowListTool.addColumn(((CMLList)node));
	             } else if (node instanceof CMLArray) {
	                 tableRowListTool.addColumn(((CMLArray)node));
	             }
	         }
	     }
	     return tableRowList;
	 }


	 public void addArray(CMLArray array) {
		 ensureTableRowListTool();
		 ensureTableHeaderTool();
		 tableRowListTool.addColumn(array);
		 CMLTableHeaderCell tableHeaderCell = new CMLTableHeaderCell();
		 String dictRef = array.getDictRef();
		 tableHeaderCell.setDictRef(dictRef);
		 tableHeaderTool.getTableHeader().addTableHeaderCell(tableHeaderCell);
	 }


	private void ensureTableHeaderTool() {
		if (tableHeaderTool == null) {
			CMLTableHeader tableHeader = new CMLTableHeader();
			tableHeaderTool = TableHeaderTool.getOrCreateTool(tableHeader);
			table.addTableHeader(tableHeader);
		}
	}


	private void ensureTableRowListTool() {		
		if (tableRowListTool == null) {
			CMLTableRowList tableRowList = new CMLTableRowList();
			tableRowListTool = TableRowListTool.getOrCreateTool(tableRowList);
			table.addTableRowList(tableRowList);
		}
	}
	
	/**
	 * may be null
	 * @return
	 */
	public TableRowListTool getTableRowListTool() {
		return tableRowListTool;
	}

	/**
	 * 
	 * @return if missing zero-length rather than null
	 */
	public CMLElements<CMLTableRow> getRowElements() {
		ensureTableRowListTool();
		return tableRowListTool.getTableRowList().getTableRowElements();

	}

	public TableHeaderTool getTableHeaderTool() {
		return tableHeaderTool;
	}

	public int indexOfColumn(String dictRef) {
		ensureTableHeaderTool();
		return tableHeaderTool.indexOfColumn(dictRef);
	}
	public CMLTableCell getTableCell(int row, String dictRef) {
		CMLTableCell cell = null;
		CMLTableRow tableRow = this.getTableRow(row);
		int columnIndex = this.getTableHeaderTool().indexOfColumn(dictRef);
		if (columnIndex >= 0 && tableRow != null) {
			TableRowTool tableRowTool = TableRowTool.getOrCreateTool(tableRow);
			cell = tableRowTool.getTableCell(columnIndex);
		}
		return cell;
	}


	private CMLTableRow getTableRow(int row) {
		CMLTableRow tableRow = null;
		CMLElements<CMLTableRow> rows = this.getRowElements();
		if (row >= 0 && row < rows.size()) {
			tableRow = rows.get(row);
		}
		return tableRow;
	}


	public int getColumnCount() {
		ensureTableHeaderTool();
		return tableHeaderTool.size();
	}


	public String getColumnName(int icol) {
		ensureTableHeaderTool();
		return tableHeaderTool.getColumnName(icol);
	}

};