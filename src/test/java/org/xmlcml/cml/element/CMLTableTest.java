package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLTable.TableType;

/**
 * test CMLTable.
 *
 * @author pmr
 *
 */
public class CMLTableTest extends AbstractTableTest {

    
    /**
	 * Test method for 'org.xmlcml.cml.element.CMLTable.getRows()'
	 */
    @Test
    public void testGetRows() {
        Assert.assertEquals("rows ", 3, columnTable1.getRows());
        Assert.assertEquals("rows ", 3, contentTable1.getRows());
        Assert.assertEquals("rows ", 3, rowTable1.getRows());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLTable.getColumns()'
     */
    @Test
    public void testGetColumns() {
        Assert.assertEquals("columns ", 2, columnTable1.getColumns());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLTable.writeHTML(Writer)'
     */
    @Test
    public void testWriteHTML() {
        StringWriter sw = new StringWriter();
        try {
            columnTable1.writeHTML(sw);
            sw.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        String ss = "<table border='1'>\n"
                + "<tr><th>d</th><th>s</th></tr>\n"
                + "<tr><td>1.0</td><td>a</td></tr>\n"
                + "<tr><td>2.0</td><td>b</td></tr>\n"
                + "<tr><td>3.0</td><td>c</td></tr>\n" + "</table>";
        String s = sw.toString();
        Assert.assertEquals("HTML output ", ss, s);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLTable.copy()'
     */
    @Test
    public void testCopy() {
        CMLTable tableX = (CMLTable) columnTable1.copy();
        Assert.assertNotNull("copy not null", tableX);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLTable.getColumnValuesList()'
     */
    @Test
    public void testGetColumnValuesList() {
        List<List <String>> sListList = ((CMLTable) columnTable1).getColumnValuesList();
        Assert.assertEquals("column values", 2, sListList.size());
        List<String> sList0 = sListList.get(0);
        Assert.assertEquals("col 0", new String[]{"1.0", "2.0", "3.0"},
                (String[]) sList0.toArray(new String[0]));
        List<String> sList1 = sListList.get(1);
        Assert.assertEquals("col 1", new String[]{"a", "b", "c"},
                (String[]) sList1.toArray(new String[0]));
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTable#setTableType(java.lang.String)}.
     */
    @Test
    public final void testSetTableTypeString() {
        columnTable1.setTableType(TableType.COLUMN_BASED.value);
        try {
            columnTable1.setTableType(TableType.CONTENT_BASED.value);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("set table fails", "bad table", e.getMessage());
        }
        try {
            columnTable1.setTableType(TableType.ROW_BASED.value);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("set table fails", "bad table", e.getMessage());
        }
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTable#setTableType(org.xmlcml.cml.element.CMLTable.TableType)}.
     */
    @Test
    public final void testSetTableTypeTableType() {
        columnTable1.setTableType(TableType.COLUMN_BASED);
        try {
            columnTable1.setTableType(TableType.CONTENT_BASED);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("set table fails", "bad table", e.getMessage());
        }
        try {
            columnTable1.setTableType(TableType.ROW_BASED);
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("set table fails", "bad table", e.getMessage());
        }
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTable#getTableTypeEnum()}.
     */
    @Test
    public final void testGetTableTypeEnum() {
        TableType tt = columnTable1.getTableTypeEnum();
        Assert.assertTrue("type", tt == TableType.COLUMN_BASED);
        tt = contentTable1.getTableTypeEnum();
        Assert.assertTrue("type", tt == TableType.CONTENT_BASED);
        tt = rowTable1.getTableTypeEnum();
        Assert.assertTrue("type", tt == TableType.ROW_BASED);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTable#resetTableType(org.xmlcml.cml.element.CMLTable.TableType)}.
     */
    @Test
    public final void testResetTableType() {
        // exercises convert
        
        String BASE = "org/xmlcml/cml/element/examples/misc";
        String COLUMN_TABLE_TO_COLUMN =   BASE+U_S+"columnTableToColumn1.xml";
        String COLUMN_TABLE_TO_CONTENT =  BASE+U_S+"columnTableToContent1.xml";
        String COLUMN_TABLE_TO_ROW =      BASE+U_S+"columnTableToRow1.xml";
        
        String CONTENT_TABLE_TO_COLUMN =  BASE+U_S+"contentTableToColumn1.xml";
        String CONTENT_TABLE_TO_CONTENT = BASE+U_S+"contentTableToContent1.xml";
        String CONTENT_TABLE_TO_ROW =     BASE+U_S+"contentTableToRow1.xml";
        
        String ROW_TABLE_TO_COLUMN =      BASE+U_S+"rowTableToColumn1.xml";
        String ROW_TABLE_TO_CONTENT =     BASE+U_S+"rowTableToContent1.xml";
        String ROW_TABLE_TO_ROW =         BASE+U_S+"rowTableToRow1.xml";
        
        testResetType("column", columnTable1, 
                COLUMN_TABLE_TO_COLUMN,
                COLUMN_TABLE_TO_CONTENT, 
                COLUMN_TABLE_TO_ROW);
        testResetType("content", contentTable1, 
                CONTENT_TABLE_TO_COLUMN,
                CONTENT_TABLE_TO_CONTENT, 
                CONTENT_TABLE_TO_ROW);
        testResetType("row",     rowTable1, 
                ROW_TABLE_TO_COLUMN,
                ROW_TABLE_TO_CONTENT, 
                ROW_TABLE_TO_ROW);
 
    }
    
    private void testResetType(String start, CMLTable table0,
            String file1, String file2, String file3
            ) {
        System.out.println("++++"+start+"++++");

        testResetType(start, table0, file1, TableType.COLUMN_BASED);
        testResetType(start, table0, file2, TableType.CONTENT_BASED);
        testResetType(start, table0, file3, TableType.ROW_BASED);
    }
    
    private void testResetType(String start, CMLTable table0, 
            String file, TableType tableType) {
        boolean stripWhite = true;
        CMLTable tableTest = new CMLTable(table0);
        try {
            tableTest.resetTableType(tableType);
        } catch (CMLRuntimeException e) {
        	// Exception expected.
            Assert.assertTrue(true);
        }
        CMLTable expected = (CMLTable) parseValidFile(file);
        assertEqualsCanonically(start+" model", expected, tableTest, stripWhite);
    }
 }
