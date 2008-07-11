/**
 * 
 */
package org.xmlcml.cml.element;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.attribute.CountExpressionAttribute;
import org.xmlcml.cml.base.RuntimeException;
import org.xmlcml.util.TestUtils;

/**
 * @author pm286
 *
 */
public class CMLFragmentTest extends MoleculeAtomBondTest {

    /**
     */
    @Test
    public final void testCalculateCountExpression() {
        String s = S_EMPTY +
                "<fragment "+CML_XMLNS+
                "  countExpression='*(5)'/>";
        CMLFragment frag = (CMLFragment) TestUtils.parseValidString(s);
        CountExpressionAttribute cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
        int count = cea.calculateCountExpression();
        Assert.assertEquals("countE", 5, count);
        s = "<fragment "+CML_XMLNS+"/>";
        frag = (CMLFragment) TestUtils.parseValidString(s);
        // FIXME - must work out how to get 1 from null value
        cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
//        count = cea.calculateCountExpression();
        Assert.assertNull("countE", cea);
        // try to simulate randomness
        double sum = 0.0;
        int N = 10;
        for (int i = 0; i < N; i++) {
            s = "<fragment "+CML_XMLNS+" countExpression='range(2,5)'/>";
            frag = (CMLFragment) TestUtils.parseValidString(s);
            cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
            count = cea.calculateCountExpression();
            if (count <= 2 || count >= 5) {
                throw new RuntimeException("bad value of count: "+count);
            }
            sum += count;
        }
        sum /= (double) N;
        Assert.assertTrue("range", sum < 5.0 & sum > 2.0 );
    }

}
