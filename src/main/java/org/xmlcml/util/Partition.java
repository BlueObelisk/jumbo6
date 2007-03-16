package org.xmlcml.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author pm286
 *
 */
public class Partition {
	/**
	 * returns all the ways the integer provided can be divided
	 * into whole number parts.  This supplying 5 returns
	 * 
	 * 1 1 1 1 1
	 * 2 1 1 1
	 * 2 2 1
	 * 3 1 1
	 * 3 2
	 * 4 1
	 * 5
	 * 
	 * @param n
	 * @return list
	 */
	public static List<List<Integer>> partition(int n) {
		List<List<Integer>> partitionList = new ArrayList<List<Integer>>();
		List<Integer> partition = new ArrayList<Integer>();
		return partition(n, n, "", partitionList, partition);
	}

	private static List<List<Integer>> partition(int n, int max, String prefix,
			List<List<Integer>> partitionList,
			List<Integer> partition) {
		if (n == 0) {
			//System.out.println(prefix);
			partitionList.add(new ArrayList<Integer>(partition));
			partition.clear();
			return partitionList;
		}

		for (int i = Math.min(max, n); i >= 1; i--) {
			//System.out.println(prefix + " " + i);
			partition.add(i);
			partition(n-i, i, prefix + " " + i, partitionList, partition);
		}
		return partitionList;
	}

	/** main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<List<Integer>> partitionList = partition(5);
		for (List<Integer> partition : partitionList) {
			for (Integer in : partition) {
				System.out.print(in+" ");
			}
			System.out.println("");
		}
	}
}
