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