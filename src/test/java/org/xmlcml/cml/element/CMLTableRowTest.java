/**
 * 
 */
package org.xmlcml.cml.element;

import static org.xmlcml.euclid.EuclidConstants.S_COMMA;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.util.TestUtils;

/**
 * @author pm286
 *
 */
public class CMLTableRowTest extends AbstractTableTest {

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRow#writeHTML(java.io.Writer)}.
     */
    @Test
    public final void testWriteHTML() {
        String tableRowS = "\n<tr><td>2</td><td>b</td></tr>";
        TestUtils.assertWriteHTML(tableRow, tableRowS);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableRow#getDelimitedString(java.lang.String)}.
     */
    @Test
    public final void testGetDelimitedString() {
        String tableRowS = tableRow.getDelimitedString(S_COMMA);
        Assert.assertEquals("comma", "2,b", tableRowS);
        tableRowS = tableRow.getDelimitedString(S_SPACE);
        Assert.assertEquals("comma", "2 b", tableRowS);
        tableRowS = tableRow.getDelimitedString(S_EMPTY);
        Assert.assertEquals("comma", "2 b", tableRowS);
        tableRowS = tableRow.getDelimitedString(null);
        Assert.assertEquals("comma", "2 b", tableRowS);
    }

}
