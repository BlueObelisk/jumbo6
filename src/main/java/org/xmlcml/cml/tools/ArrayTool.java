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
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.RealArray;

/**
 * tool for managing arrays
 *
 * @author pmr
 *
 */
public class ArrayTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ArrayTool.class);

	CMLArray array = null;

	/** constructor.
	 */
	public ArrayTool(CMLArray array) throws RuntimeException {
		init();
		this.array = array;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLArray getArray() {
		return this.array;
	}

    
	/** gets AngleTool associated with angle.
	 * if null creates one and sets it in angle
	 * @param array
	 * @return tool
	 */
	public static ArrayTool getOrCreateTool(CMLArray array) {
		ArrayTool arrayTool = (array == null) ? null : (ArrayTool) array.getTool();
		if (arrayTool == null) {
			arrayTool = new ArrayTool(array);
			array.setTool(arrayTool);
		}
		return arrayTool;
	}

	public void createIntArray(int npoints, int start, int delta) {
		IntArray intArray = new IntArray(npoints, start, delta);
		array = new CMLArray(intArray.getArray());
	}

	public void createDoubleArray(int npoints, double start, double delta) {
		RealArray realArray = new RealArray(npoints, start, delta);
		array = new CMLArray(realArray.getArray());
	}
};