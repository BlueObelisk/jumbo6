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
