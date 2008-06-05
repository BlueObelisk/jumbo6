package org.xmlcml.cml.element;

import static org.xmlcml.cml.base.CMLConstants.CATALOG_XML;
import static org.xmlcml.cml.element.AbstractTest.UNIT_RESOURCE;
import static org.xmlcml.euclid.EuclidConstants.U_S;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.xmlcml.cml.map.NamespaceToUnitListMap;
import org.xmlcml.euclid.Util;

/**
 * tests CMLScalar.
 *
 * @author pmr
 *
 */
public class NumericTest {

	/**
	 * making this static speeds reading greatly. because file is only read
	 * once.
	 */
	protected static NamespaceToUnitListMap unitsUnitListMap = null;

	/**
	 * setup.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (unitsUnitListMap == null) {
			try {
				unitsUnitListMap = new NamespaceToUnitListMap(Util
						.getResource(UNIT_RESOURCE + U_S + CATALOG_XML), new CMLUnitList());
			} catch (IOException e) {
				Assert.fail("should not throw " + e);
			}
		}
	}

 }
