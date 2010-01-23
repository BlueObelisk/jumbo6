package org.xmlcml.util;

import java.util.Map;


/**
 * some of these should be moved to CMLXOM later
 * @author pm286
 *
 */
public class ToolUtils {

	public static void debugMap(String title, Map map) {
		System.out.println("=="+title+"==");
		for (Object key : map.keySet()) {
			System.out.println(key.toString()+" => "+map.get(key));
		}
		System.out.println("=============");
	}
}
