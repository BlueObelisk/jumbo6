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

import java.net.MalformedURLException;
import java.net.URL;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

/** utilities for managing catalogs.
 * 
 * @author pm286
 *
 */

public class CatalogUtil implements CMLConstants {

	/** getURL from scalar in CMLList.
	 * 
	 * @param catalogThing
	 * @param scalar
	 * @return url
	 */
	public static URL getURLFromScalar(CatalogListChild catalogThing, CMLScalar scalar) {
		String urlS = scalar.getValue();
		String convention = scalar.getConvention();
		return getURL(convention, urlS, catalogThing);
	}
	
	private static URL getURL(String convention, String urlS, CatalogListChild catalogThing) {
		URL catalogUrl = null;
		if (convention == null) {
			throw new RuntimeException("Must give convention");
		} else if (CatalogListChild.Address.RELATIVE.value.equals(convention)) {
			try {
				catalogUrl = new URL(catalogThing.getURL(), urlS);
			} catch (MalformedURLException e) {
				throw new RuntimeException("Cannot make url from: "+catalogUrl+"; "+urlS);
			}
		} else if (CatalogListChild.Address.RESOURCE.value.equals(convention)) {
			try {
				catalogUrl = Util.getResource(urlS);
			} catch (Exception e) {
				throw new RuntimeException("Cannot get URL resource from: "+urlS);
			}
		} else {
			throw new RuntimeException("Inappropriate convention: "+convention);
		}
		return catalogUrl;
	}
	
	/** getURL from scalar in CMLList.
	 * 
	 * @param catalogThing
	 * @param link 
	 * @return url
	 */
	public static URL getURLFromLink(CatalogListChild catalogThing, CMLLink link) {
		String urlS = link.getTo();
		String convention = link.getConvention();
		return getURL(convention, urlS, catalogThing);
	}
	
	/** gets URL from a resourceString.
	 * if string contains protocol, then turns into a URL.
	 * else assumes it is relative to the classpath and
	 * uses Util.getResource()
	 * @param resourceName
	 * @return url
	 */
	public static URL getURLFromResource(String resourceName) {
		URL catalogUrl = null;
		try {
			catalogUrl = new URL(resourceName);
		} catch (MalformedURLException e) {
			// no protocol, assume to be relative
		}
		if (catalogUrl == null) {
			try {
				catalogUrl = Util.getResource(resourceName);
			} catch (Exception e) {
				throw new RuntimeException("Cannot get URL resource from: "+resourceName);
			}
		}
		return catalogUrl;
	}
}
