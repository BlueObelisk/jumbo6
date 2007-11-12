package org.xmlcml.cml.base;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class CMLSchemaValidatorTest {

    @Test
    @Ignore // this is a mess anyway
    public void testCMLSchemaValidatorSuccess() throws IOException {
        String cml = "<molecule id='m1' xmlns='" + CMLConstants.CML_NS + "' />";
        try {
            CMLSchemaValidator.getInstance().validateCML(new StringReader(cml));
        } catch (CMLException e) {
            Assert.fail("failed to validate"+e);
        }
    }

    @Test
    public void testCMLSchemaValidatorFailure() throws IOException {
        String cml = "<molecule id='m1' xmlns='" + CMLConstants.CML_NS + "' foo='bar' />";
        try {
            CMLSchemaValidator.getInstance().validateCML(new StringReader(cml));
            Assert.fail("failed to validate");
        } catch (CMLException e) {
            // PASS
        }
    }

}
