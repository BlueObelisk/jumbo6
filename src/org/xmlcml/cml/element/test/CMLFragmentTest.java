/**
 * 
 */
package org.xmlcml.cml.element.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CountExpressionAttribute;

/**
 * @author pm286
 *
 */
public class CMLFragmentTest extends MoleculeAtomBondTest {

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLFragment#calculateCountExpression()}.
     */
    @Test
    public final void testCalculateCountExpression() {
        String s = "" +
                "<fragment "+CML_XMLNS+
                "  countExpression='*(5)'/>";
        CMLFragment frag = (CMLFragment) parseValidString(s);
        CountExpressionAttribute cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
        int count = cea.calculateCountExpression();
        Assert.assertEquals("countE", 5, count);
        s = "<fragment "+CML_XMLNS+"/>";
        frag = (CMLFragment) parseValidString(s);
        // FIXME - must work out how to get 1 from null value
        cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
//        count = cea.calculateCountExpression();
        Assert.assertNull("countE", cea);
        // try to simulate randomness
        double sum = 0.0;
        int N = 10;
        for (int i = 0; i < N; i++) {
            s = "<fragment "+CML_XMLNS+" countExpression='range(2,5)'/>";
            frag = (CMLFragment) parseValidString(s);
            cea = (CountExpressionAttribute) frag.getCountExpressionAttribute();
            count = cea.calculateCountExpression();
            if (count <= 2 || count >= 5) {
                throw new CMLRuntimeException("bad value of count: "+count);
            }
            sum += count;
        }
        sum /= (double) N;
        Assert.assertTrue("range", sum < 5.0 & sum > 2.0 );
    }

    /**
     * Test method for {@link org.xmlcml.cml.element.CMLFragment#expandCountExpression()}.
     */
    @Test
    @Ignore
    public final void testExpandCountExpression() {
    }

}
