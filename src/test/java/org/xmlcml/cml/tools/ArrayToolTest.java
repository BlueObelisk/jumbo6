/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;


/**
 * test ArrayTool.
 * 
 * @author pmr
 * 
 */
public class ArrayToolTest {
	private static Logger LOG = Logger.getLogger(ArrayToolTest.class);


	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testCreateArray() {
		CMLArray array = new CMLArray();
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		CMLArray array1 = arrayTool.getArray();
		CMLXOMTestUtils.assertEqualsCanonically("empty array", array, array1);
	}

	@Test
	public void testCreateIntArray() {
		CMLArray array = new CMLArray(CMLConstants.XSD_DOUBLE);
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		CMLArray array1 = arrayTool.getArray();
		CMLArray refArray = new CMLArray();
		refArray.setDataType(CMLConstants.XSD_DOUBLE);
		CMLXOMTestUtils.assertEqualsCanonically("empty int array", refArray, array1);
	}

	@Test
	public void testCreateIntArrayValues() {
		CMLArray array = new CMLArray(new double[]{1, 2, 3});
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		CMLArray array1 = arrayTool.getArray();
		CMLArray refArray = new CMLArray();
		refArray.setDataType(CMLConstants.XSD_DOUBLE);
		refArray.setArray(new double[] {1, 2, 3});
		CMLXOMTestUtils.assertEqualsIncludingFloat("int array", refArray, array1, true, 0.00001);
	}

	@Test
	public void testCreateIntArrayValuesLater() {
		CMLArray array = new CMLArray();
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		arrayTool.createIntArray(4, 3, 2);
		CMLArray refArray = new CMLArray(new double[]{3, 5, 7, 9});
		CMLXOMTestUtils.assertEqualsIncludingFloat("int array", refArray, arrayTool.getArray(), true, 0.00001);
                                                                                                              	}


	@Test
	public void testCreateDoubleArray() {
		CMLArray array = new CMLArray(CMLConstants.XSD_DOUBLE);
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		CMLArray array1 = arrayTool.getArray();
		CMLArray refArray = new CMLArray();
		refArray.setDataType(CMLConstants.XSD_DOUBLE);
		CMLXOMTestUtils.assertEqualsCanonically("empty double array", refArray, array1);
	}

	@Test
	public void testCreateDoubleArrayValues() {
		CMLArray array = new CMLArray(new double[]{1., 2., 3.});
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		CMLArray array1 = arrayTool.getArray();
		CMLArray refArray = new CMLArray();
		refArray.setDataType(CMLConstants.XSD_DOUBLE);
		refArray.setArray(new double[] {1., 2., 3.});
		CMLXOMTestUtils.assertEqualsIncludingFloat("double array", refArray, array1, true, 0.00001);
	}

	@Test
	public void testCreateDoubleArrayValuesLater() {
		CMLArray array = new CMLArray();
		ArrayTool arrayTool = ArrayTool.getOrCreateTool(array);
		arrayTool.createDoubleArray(4, 3., 0.1);
		CMLArray refArray = new CMLArray(new double[]{3.0, 3.1, 3.2, 3.3});
		CMLXOMTestUtils.assertEqualsIncludingFloat("double array", refArray, arrayTool.getArray(), true, 0.00001);
	}

}
