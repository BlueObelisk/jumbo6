package org.xmlcml.cml.tools;

import java.net.MalformedURLException;
import java.net.URL;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.lite.CMLScalar;
import org.xmlcml.cml.element.main.CMLLink;
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
