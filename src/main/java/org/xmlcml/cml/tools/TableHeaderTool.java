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
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLTableHeader;
import org.xmlcml.cml.element.CMLTableHeaderCell;

/**
 * tool for managing table
 *
 * @author pmr
 *
 */
public class TableHeaderTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TableHeaderTool.class.getName());

	private CMLTableHeader tableHeader;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TableHeaderTool(CMLTableHeader tableHeader) throws RuntimeException {
		init();
		this.tableHeader = tableHeader;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTableHeader getTableHeader() {
		return this.tableHeader;
	}

    
	/** gets TableTool associated with table.
	 * if null creates one and sets it in table
	 * @param table
	 * @return tool
	 */
	public static TableHeaderTool getOrCreateTool(CMLTableHeader tableHeader) {
		TableHeaderTool tableTool = null;
		if (tableHeader != null) {
			tableTool = (TableHeaderTool) tableHeader.getTool();
			if (tableTool == null) {
				tableTool = new TableHeaderTool(tableHeader);
				tableHeader.setTool(tableTool);
			}
		}
		return tableTool;
	}


	public int indexOfColumn(String dictRef) {
		if (dictRef == null) {
			return -1;
		}
		CMLElements<CMLTableHeaderCell> cells = tableHeader.getTableHeaderCellElements();
		for (int i = 0; i < cells.size(); i++) {
			CMLTableHeaderCell cell = cells.get(i);
			String dictRef1 = cell.getDictRef();
			if (dictRef.equals(dictRef1)) {
				return i;
			}
		}
		return -1;
	}


	public int size() {
		return tableHeader.getTableHeaderCellElements().size();
	}


	public String getColumnName(int icol) {
		CMLTableHeaderCell cell = this.getCell(icol);
		return (cell != null) ? cell.getAttributeValue(DictRefAttribute.NAME) : null;
	}


	private CMLTableHeaderCell getCell(int icol) {
		CMLElements<CMLTableHeaderCell> cells = tableHeader.getTableHeaderCellElements();
		return (icol >= 0 && icol < cells.size()) ? cells.get(icol) : null;
	}


};