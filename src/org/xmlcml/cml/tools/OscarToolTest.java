/**
 * 
 */
package org.xmlcml.cml.tools;


import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.test.BaseTest;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class OscarToolTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /** tests the generation of quantity.
     */
    @Test
    public void testQuantity() {
//      <property type="quantity">
//      - <quantity type="mass">
//      - <value>
//        <point>11.9</point> 
//        </value>
//        <units>g</units> 
//        </quantity>
//        , 
//      - <quantity type="amount">
//      - <value>
//        <point>0.04</point> 
//        </value>
//        <units>mol</units> 
//        </quantity>
//        </property>
        String s =
          "<property type=\"quantity\">" +
          "  <quantity type=\"mass\">" +
          "    <value>" +
          "      <point>11.9</point>" +
          "    </value>" +
          "    <units>g</units>" +
          "  </quantity>" +
          "  ," +
          "  <quantity type=\"amount\">" +
          "    <value>" +
          "      <point>0.04</point>" +
          "    </value>" +
          "    <units>mol</units>" +
          "  </quantity>" +
          "</property>" +
          "";
        Element property = null;
        try {
            property = new CMLBuilder().parseString(s);
        } catch (Exception e) {
            Util.BUG(e);
        }
        Assert.assertNotNull("property not null", property);
        OscarTool.quantity(property);
    }

    /** test static void addScalar(Element quantity, String name, String units)
     */
    @Test
    public void testAddScalar() {
        System.out.println("=====testAddScalar====");
        String s =
            "  <quantity type=\"mass\">" +
            "    <value>" +
            "      <point>11.9</point>" +
            "    </value>" +
            "    <units>g</units>" +
            "  </quantity>" +
            "";
        Element quantity = null;
        try {
            quantity = new CMLBuilder().parseString(s);
        } catch (Exception e) {
            Util.BUG(e);
        }
        Assert.assertNotNull("quantity not null", quantity);
        CMLProperty property = new CMLProperty();
        OscarTool.createScalarAndAppendAsChildOfQuantity(
                property, quantity, "fooname", "barunits");
// result should be:
        Element expectedQuantity = null;
        try {
            expectedQuantity = new CMLBuilder().parseString(
                    "<quantity type=\"mass\">" +
                    "    <value>" +
                    "      <point>11.9</point>" +
                    "    </value>" +
                    "    <units>g</units>" +
                    "  </quantity>");
        } catch (Exception e) {
            Util.BUG(e);
        }
        BaseTest.assertEqualsCanonically(
                "quantity ", expectedQuantity, quantity);
        // property should be
        Element expectedProperty = null;
        try {
            expectedProperty = new CMLBuilder().parseString(
                    "<property xmlns=\"http://www.xml-cml.org/schema\">" +
                    "<scalar dataType=\"xsd:double\" dictRef=\"osc:fooname\"" +
                    " units=\"oscUnits:barunits\">11.9</scalar>" +
                    "</property>");
        } catch (Exception e) {
            Util.BUG(e);
        }
        BaseTest.assertEqualsCanonically(
                "before whitespace ", expectedProperty, property);
    }
    
}
