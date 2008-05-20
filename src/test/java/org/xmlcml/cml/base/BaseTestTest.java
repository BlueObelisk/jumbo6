package org.xmlcml.cml.base;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;

public class BaseTestTest extends BaseTest {

	@Test
	public void testAssertEqualsIncludingFloat() {
    	String s1 = "<foo a='a' b='1.23'><bar>3.45</bar></foo>";
    	String s2 = "<foo a='a' b='1.231'><bar>3.452</bar></foo>";
    	Element e1 = parseValidString(s1);
    	Element e2 = parseValidString(s2);
    	assertNotEqualsCanonically("ok", e1, e2);
    	assertEqualsIncludingFloat("ok", e1, e2, true, 0.01);
    	try {
    		assertEqualsIncludingFloat("ok", e1, e2, true, 0.001);
    		Assert.fail("should throw non-equality");
    	} catch (Throwable t) {
    	}
    }

}