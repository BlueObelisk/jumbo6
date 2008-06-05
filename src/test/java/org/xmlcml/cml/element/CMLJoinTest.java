package org.xmlcml.cml.element;

import static org.xmlcml.cml.element.AbstractTest.COMPLEX_RESOURCE;
import static org.xmlcml.cml.element.AbstractTest.EXPERIMENTAL_RESOURCE;
import static org.xmlcml.euclid.EuclidConstants.U_S;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Util;

/** tests CMLJoin.
 * 
 * @author pm286
 *
 */
public class CMLJoinTest {

    /** Test method for 'org.xmlcml.cml.element.CMLJoin.joinAtomRefs2()'
     */
    @Test
    @Ignore
    public void testJoinAtomRefs2() {
        CMLMolecule peo0Mol = null;
        try {
            InputStream in = Util.getInputStreamFromResource(
                    COMPLEX_RESOURCE + U_S +"peo0mid.xml");
            peo0Mol = (CMLMolecule) new CMLBuilder().build(in).getRootElement();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CMLRuntimeException("EXC"+e);
        }
        CMLElements<CMLJoin> joinList = peo0Mol.getJoinElements();
        Assert.assertEquals("joins ", 4, joinList.size());
//        int i = 0;
//        for (CMLJoin join : joinList) {
//            ++i;
//            System.out.println("==========="+i);
//            boolean takeAtomWithLowestId = true;
//            join.joinByAtomRefs2AndAdjustGeometry(takeAtomWithLowestId);
//            System.out.println("===================="+i);
//        }
    }

    /** Test method for 'org.xmlcml.cml.element.CMLJoin.joinAtomRefs2()'
     */
    @Test
    public void testJoinAtomRefs21() {
        CMLMolecule peo1Mol = null;
        try {
            InputStream in =Util.getInputStreamFromResource(
                    EXPERIMENTAL_RESOURCE+U_S+"peo1mid.xml");
            peo1Mol = (CMLMolecule) new CMLBuilder().build(in).getRootElement();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CMLRuntimeException("EXC",e);
        }
        CMLElements<CMLJoin> joinList = peo1Mol.getJoinElements();
        Assert.assertEquals("joins ", 4, joinList.size());
//        int i = 0;
//        for (CMLJoin join : joinList) {
//            ++i;
//            boolean takeAtomWithLowestId = true;
//            join.joinByAtomRefs2AndAdjustGeometry(takeAtomWithLowestId);
//        }
    }

}
