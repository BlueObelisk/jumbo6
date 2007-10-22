package org.xmlcml.cml;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;

/**
 * 
 * @author pm286
 *
 */
public class JCampBuildTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void regressionTestJCampConverted() throws Exception {
		CMLBuilder builder = new CMLBuilder();
		builder.build(getClass().getClassLoader().getResourceAsStream(
				"nmr/dw9960.cml.xml"));
		builder.build(getClass().getClassLoader().getResourceAsStream(
				"nmr/ebprotfx.cml.xml"));
	}
}
