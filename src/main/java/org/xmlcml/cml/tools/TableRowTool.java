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
import org.xmlcml.cml.element.CMLTableRow;
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
public class TableRowTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TableRowTool.class.getName());

	private CMLTableRow tableRow;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TableRowTool(CMLTableRow tableRow) throws RuntimeException {
		init();
		this.tableRow = tableRow;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTableRow getTableRow() {
		return this.tableRow;
	}

    
	/** gets TableTool associated with table.
	 * if null creates one and sets it in table
	 * @param table
	 * @return tool
	 */
	public static TableRowTool getOrCreateTool(CMLTableRow tableRow) {
		TableRowTool tableTool = null;
		if (tableRow != null) {
			tableTool = (TableRowTool) tableRow.getTool();
			if (tableTool == null) {
				tableTool = new TableRowTool(tableRow);
				tableRow.setTool(tableTool);
			}
		}
		return tableTool;
	}

	public CMLTableCell getTableCell(int columnIndex) {
		return (columnIndex >= 0 && columnIndex < tableRow.getTableCellElements().size()) ?
				tableRow.getTableCellElements().get(columnIndex) : null;
	}




};