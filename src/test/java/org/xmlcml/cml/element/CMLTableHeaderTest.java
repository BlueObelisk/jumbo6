/**
 * 
 */
package org.xmlcml.cml.element;

import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.util.TestUtils;

/**
 * @author pm286
 *
 */
public class CMLTableHeaderTest extends AbstractTableTest {

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableHeader#writeHTML(java.io.Writer)}.
     */
    @Test
    public final void testWriteHTML() {
        String expected = S_EMPTY +
                "\n<tr><th class='c:foo'>foo</th><th class='c:bar'>bar</th></tr>";
        TestUtils.assertWriteHTML(tableHeader, expected);
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLTableHeader#getColumnCount()}.
     */
    @Test
    public final void testGetColumnCount() {
        Assert.assertEquals("count", 2, tableHeader.getColumnCount());
    }

}
