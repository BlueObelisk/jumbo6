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
