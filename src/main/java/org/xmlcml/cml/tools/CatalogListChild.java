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

import java.net.URL;

import org.xmlcml.cml.base.CMLConstants;

/**
 * possible children of CatalogList
 * may be Catalog or CatalogList
 * probably redundant.
* still being worked out
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface CatalogListChild extends CMLConstants {
	/** allowed child elements.
	 * not sure if this is required
	 * @author pm286
	 *
	 */
	public enum Type {
		/** catalog */
		CATALOG(C_A+"catalog"),
		/** catalogList */
		CATALOGLIST(C_A+"catalogList");
		/** value */
		public String value;
		private Type(String s) {
			this.value = s;
		}
	}
	/** types of addressing.
	 */
	public enum Address {
		/** absolute URL (protocol is http or file). */
		ABSOLUTE(C_A+"absoluteUrl"),
		/** relative to URL or catalogThing. */
		RELATIVE(C_A+"relativeUrl"),
		/** relative to classpath/resource*/
		RESOURCE(C_A+"resourceUrl");
		/** value */
		public String value;
		private Address(String s) {
			this.value = s;
		}
	}
	
	/** gets URL of catalog thing.
	 * @return URL
	 */
	URL getURL();
}
