package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLTableCell;
import org.xmlcml.cml.element.CMLTableRow;

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