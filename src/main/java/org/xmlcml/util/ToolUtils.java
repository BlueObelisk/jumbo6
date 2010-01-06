package org.xmlcml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlcml.euclid.IntMatrix;

/**
 * some of these should be moved to CMLXOM later
 * @author pm286
 *
 */
public class ToolUtils {

	/**
	 * should really be in IntMatrix
	 */
	public static List<Integer> findLargestUniqueElementsInRowColumn(IntMatrix intMatrix) {
		List<Integer> intList = new ArrayList<Integer>();
		for (int jcol = 0, max = intMatrix.getCols(); jcol < max; jcol++) {
			int irow = intMatrix.indexOfLargestElementInColumn(jcol);
			int maxval = intMatrix.elementAt(irow, jcol);
			if (maxval == -1) {
				irow = -1;
			} else {
				for (int ii = irow + 1, maxrow = intMatrix.getRows(); ii < maxrow; ii++) {
					int val = intMatrix.elementAt(ii, jcol);
					if (val >= maxval) {
						irow = -1;
						break;
					}
				}
			}
			intList.add(irow);
		}
		return intList;
	}

	public static void debugMap(String title, Map map) {
		System.out.println("=="+title+"==");
		for (Object key : map.keySet()) {
			System.out.println(key.toString()+" => "+map.get(key));
		}
		System.out.println("=============");
	}
}
