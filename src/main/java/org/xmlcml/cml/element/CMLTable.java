package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;

/**
 * user-modifiable class supporting table.
 *
 * A table is not valid unless it has a tableType attribute set to
 * one of the allowed values of the TableType enum. Each of these requires
 * further constraints
 * @see AbstractTable
 *
 */
public class CMLTable extends AbstractTable {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** types of table.
     * @author pm286
     *
     */
    public enum TableType {
        /** columns */
        COLUMN_BASED("columnBased"),
        /** rows */
        ROW_BASED("rowBased"),
        /** content */
        CONTENT_BASED("contentBased");
        /** string value */
        public String value;
        private TableType(String v) {
            this.value = v;
        }
        /** get type.
         *
         * @param t
         * @return type
         */
        public static TableType getTableType(String t) {
            TableType tt = null;
            for (TableType v : TableType.values()) {
                if (v.value.equals(t)) {
                    tt = v;
                    break;
                }
            }
            return tt;
        }
    }
    /**
     * contructor.
     */
    public CMLTable() {
//    	System.out.println("TABLEEEEEEEEEEEEEEE");
    }

    /**
     * contructor.
     *
     * @param old
     */
    public CMLTable(CMLTable old) {
        super((AbstractTable) old);
    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLTable(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLTable
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLTable();
    }

    /**
     * gets the row count. first from rows attribute and if missing from the
     * actual count.
     *
     * @return row count (0 if none or no columns)
     * @throws CMLRuntimeException
     *             irregular table
     */
    public int getRows() {
        int rows = 0;
        if (this.getRowsAttribute() != null) {
            rows = super.getRows();
        } else {
            CMLArrayList arrayList = (CMLArrayList) this.getFirstCMLChild(CMLArrayList.TAG);
            if (arrayList != null) {
                rows = arrayList.getRowCount();
            }
        }
        return rows;
    }

    /** old method for accessing table columns.
     * @deprecated
     * @return arrayElements or null
     */
    public CMLElements<CMLArray> getArrayElements() {
        CMLElements<CMLArray> arrayElements = null;
        CMLArrayList arrayList = (CMLArrayList) this.getFirstCMLChild(CMLArrayList.TAG);
        if (arrayList != null) {
            arrayElements = arrayList.getArrayElements();
        }
        return arrayElements;
    }

    /** set type.
     * overrides simple set and checks content
     * @param t
     * @throws CMLRuntimeException for inconsistent table
     */
    public void setTableType(String t) {
        TableType tt = TableType.getTableType(t);
        if (tt == null) {
            throw new CMLRuntimeException("Bad table type: "+t);
        }
        if (check(tt)) {
            super.setTableType(t);
        } else {
            throw new CMLRuntimeException("bad table");
        }
    }

    /** sets table type through enum.
     * This is the safest way to set the type as it is guaranteed
     * to use a known type.
     * @param tt
     */
    public void setTableType(TableType tt) {
        if (tt != null) {
            this.setTableType(tt.value);
        }
    }

    /** get type of table as enum.
     * @return null if tableType attribute is null or unknown
     */
    public TableType getTableTypeEnum() {
        String tableType =this.getTableType();
        return TableType.getTableType(tableType);
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
        } else if (tt.value.equals(this.getTableType())) {
                // no-op
        } else {
            convert(this.getTableTypeEnum(), tt);
            super.setTableType(tt.value);
        }
    }

    private void convert(TableType from, TableType to) {
        if (!this.check(from)) {
            throw new CMLRuntimeException("Inconsistent table");
        }
        CMLTableHeader tableHeader = (CMLTableHeader)
            this.getFirstCMLChild(CMLTableHeader.TAG);
        CMLArrayList arrayList = (CMLArrayList)
            this.getFirstCMLChild(CMLArrayList.TAG);
        CMLTableContent tableContent = (CMLTableContent)
            this.getFirstCMLChild(CMLTableContent.TAG);
        CMLTableRowList tableRowList = (CMLTableRowList)
            this.getFirstCMLChild(CMLTableRowList.TAG);

        if (from != to) {
            if (from == TableType.COLUMN_BASED) {
                tableHeader = arrayList.createTableHeader();
                arrayList.detach();
                this.appendChild(tableHeader);
                if (to == TableType.ROW_BASED) {
                    tableRowList = arrayList.createTableRowList();
                    this.appendChild(tableRowList);
                } else if (to == TableType.CONTENT_BASED) {
                    tableContent = arrayList.createTableContent();
                    this.appendChild(tableContent);
                }
                if (this.getColumnsAttribute() == null) {
                    this.setColumns(tableHeader.getColumnCount());
                }
                if (this.getRowsAttribute() == null) {
                    this.setRows(arrayList.getRowCount());
                }
            } else if (from == TableType.ROW_BASED) {
                tableRowList.detach();
                if (to == TableType.COLUMN_BASED) {
                    arrayList = tableRowList.createArrayList(
                            this.getRows(), this.getColumns(), tableHeader);
                    this.appendChild(arrayList);
                    tableHeader.detach();
                } else if (to == TableType.CONTENT_BASED) {
                    tableContent = tableRowList.createTableContent();
                    this.appendChild(tableContent);
                }
            } else if (from == TableType.CONTENT_BASED) {
                tableContent.detach();
                if (to == TableType.COLUMN_BASED) {
                    arrayList = tableContent.createArrayList(
                            this.getRows(), this.getColumns(), tableHeader);
                    this.appendChild(arrayList);
                    tableHeader.detach();
                } else if (to == TableType.ROW_BASED) {
                    tableRowList = tableContent.createTableRowList(
                            this.getRows(), this.getColumns());
                    this.appendChild(tableRowList);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void checkRowsAndColumns() {
        if (this.getRowsAttribute() == null ||
                this.getColumnsAttribute() == null) {
            throw new CMLRuntimeException("Must give rows and columns attributes");
        }
    }

    /** checkes compatibility of current table with table type.
     * See schema for details
     * @param t
     * @return validity
     */
    private boolean check(TableType t) {
        CMLTableHeader header = (CMLTableHeader)
            this.getFirstCMLChild(CMLTableHeader.TAG);
        CMLArrayList arrayList = (CMLArrayList)
            this.getFirstCMLChild(CMLArrayList.TAG);
        CMLTableContent content = (CMLTableContent)
            this.getFirstCMLChild(CMLTableContent.TAG);
        CMLTableRowList rowList = (CMLTableRowList)
            this.getFirstCMLChild(CMLTableRowList.TAG);

        boolean check = false;
        // attributes are set before content
        if (header == null &&
                arrayList == null &&
                content == null &&
                rowList == null) {
            check = true;
        } else if (t == null) {
            check =
                header == null &&
                arrayList == null &&
                content == null &&
                rowList == null;
        } else if (TableType.COLUMN_BASED.equals(t)) {
            check =
                header == null &&
                arrayList != null &&
                content == null &&
                rowList == null;
        } else if (TableType.CONTENT_BASED.equals(t)) {
            check =
                header != null &&
                arrayList == null &&
                content != null &&
                rowList == null;
        } else if (TableType.ROW_BASED.equals(t)) {
            check =
                header != null &&
                arrayList == null &&
                content == null &&
                rowList != null;
        }
        return check;
    }

    /** gets arrayList or creates one if allowed.
     * if arrayList does not exist, creates it if table is of
     * type columnBased
     */
    private CMLArrayList getOrCreateArrayList() {
        CMLArrayList arrayList = (CMLArrayList) this.getFirstCMLChild(CMLArrayList.TAG);
        if (arrayList == null &&
                TableType.COLUMN_BASED.value.equals(this.getTableType())) {
            if (this.getTableContentElements().size() == 0 &&
                    this.getTableRowListElements().size() == 0) {
                arrayList = new CMLArrayList();
                this.appendChild(arrayList);
            }
        }
        return arrayList;
    }

    /** gets tableRowList or creates one if allowed.
     * if tableRowList does not exist, creates it if table is of
     * type rowBased
     */
    private CMLTableRowList getOrCreateTableRowList() {
        CMLTableRowList tableRowList = (CMLTableRowList) this.getFirstCMLChild(CMLTableRowList.TAG);
        if (tableRowList == null &&
                TableType.ROW_BASED.value.equals(this.getTableType())) {
            if (this.getTableContentElements().size() == 0 &&
                    this.getTableRowListElements().size() == 0) {
                tableRowList = new CMLTableRowList();
                this.appendChild(tableRowList);
            }
        }
        return tableRowList;
    }

    /** old method for adding column.
     * @deprecated
     * @param array column to add
     */
    public void addArray(CMLArray array) {
        if (check(TableType.COLUMN_BASED)) {
            CMLArrayList arrayList = this.getOrCreateArrayList();
            arrayList.appendChild(array);
        }
    }


    /**
     * get column count. uses value of columns attribute if present else counts
     * arrays.
     *
     * @return column count
     */
    public int getColumns() {
        int columns = -1;
        if (this.getColumnsAttribute() != null) {
            columns = super.getColumns();
        } else {
            CMLArrayList arrayList = (CMLArrayList) this.getFirstCMLChild(CMLArrayList.TAG);
            CMLTableHeader tableHeader = (CMLTableHeader) this.getFirstCMLChild(CMLTableHeader.TAG);
            if (arrayList != null) {
                columns = arrayList.getArraysCount();
            } else if (tableHeader != null) {
                columns = tableHeader.getColumnCount();
            }
        }
        return columns;
    }

    /**
     * output HTML.
     *
     * @param w
     * @throws IOException
     */
    public void writeHTML(Writer w) throws IOException {
        TableType tableType = this.getTableTypeEnum();
        if (TableType.COLUMN_BASED == tableType) {
            CMLTable newTable = new CMLTable(this);
            // convert to row based
            newTable.resetTableType(TableType.ROW_BASED);
            newTable.writeHTML(w);
        } else if (TableType.ROW_BASED == tableType) {
            w.write("<table border='1'>");
            CMLTableHeader tableHeader = this.getTableHeaderElements().get(0);
            tableHeader.writeHTML(w);
            CMLTableRowList tableRowList = this.getTableRowListElements().get(0);
            tableRowList.writeHTML(w);
            w.write("\n</table>");
        } else if (TableType.CONTENT_BASED == tableType) {
            CMLTable newTable = new CMLTable(this);
            // convert to row based
            newTable.resetTableType(TableType.ROW_BASED);
            newTable.writeHTML(w);
        } else {
            throw new CMLRuntimeException("No tableType given");
        }
    }

    @SuppressWarnings("unused")
    private CMLElements<CMLTableRow> getTableRowElements() {
        CMLElements<CMLTableRow> tableRowElements = null;
        if (check(TableType.ROW_BASED)) {
            CMLTableRowList tableRowList = this.getOrCreateTableRowList();
            tableRowElements = tableRowList.getTableRowElements();
        }
        return tableRowElements;
    }
    /**
     * get values columnwise.
     *
     * @return List of column Values (zero length if none)
     * @throws CMLRuntimeException
     *             columns inconsistent
     */
    public List<List<String>> getColumnValuesList() {
        List<List<String>> stringListList = new ArrayList<List<String>>();
        CMLElements<CMLArray> arrays = this.getArrayElements();
        int rows = -1;
        for (CMLArray array : arrays) {
            List<String> strings = array.getStringValues();
            int nr = strings.size();
            if (rows == -1) {
                rows = nr;
            } else if (rows != nr) {
                throw new CMLRuntimeException("inconsistent row lengths: " + rows + S_SLASH
                        + nr);
            }
            stringListList.add(strings);
        }
        return stringListList;
    }

    @SuppressWarnings("unused")
    private void writeArrays(Writer w, List<List<String>> stringListList)
            throws IOException {
        if (check(TableType.COLUMN_BASED)) {
            int columnCount = stringListList.size();
            int rowCount = stringListList.get(0).size();
            CMLElements arrays = this.getArrayListElements();
            // titles
            w.write("\n<tr>");
            for (Object object : arrays) {
                w.write("<th>");
                w.write(((Element)object).getAttributeValue("title"));
                w.write("</th>");
            }
            w.write("</td>");
            for (int i = 0; i < rowCount; i++) {
                w.write("\n<tr>");
                for (int j = 0; j < columnCount; j++) {
                    w.write("<td>");
                    List<String> col = stringListList.get(j);
                    w.write(col.get(i));
                    w.write("</td>");
                }
                w.write("</tr>");
            }
        }
    }

    @SuppressWarnings("unused")
    private void writeRows(Writer w, CMLTableHeader header,
            CMLElements<CMLTableRow> rows) throws IOException {
        header.writeHTML(w);
        int ncols = -1;
        for (CMLTableRow row : rows) {
            int nc = row.getTableCellElements().size();
            if (ncols == -1) {
                nc = ncols;
            } else if (nc != ncols) {
                throw new CMLRuntimeException("inconsistent column length in rows: "
                        + nc + S_SLASH + ncols);
            }
            row.writeHTML(w);
        }
    }

    @SuppressWarnings("unused")
    private void writeContent(Writer w, CMLTableHeader header,
            CMLTableContent content) throws IOException, CMLRuntimeException {
        header.writeHTML(w);
        int ncols = header.getColumnCount();
        String[] strings = content.getStrings();
        if (strings.length % ncols != 0) {
            throw new CMLRuntimeException("non-rectangular table: " + strings.length
                    + S_SLASH + ncols);
        }
        int count = 0;
        for (String string : strings) {
            if (count % ncols == 0) {
                w.write("\n<tr>");
            }
            w.write("<td>" + string + "</td>");
            if (++count % ncols == 0) {
                w.write("</tr>");
            }
        }
    }

}
