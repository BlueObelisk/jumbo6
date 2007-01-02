package org.xmlcml.cml.tools;

import java.net.MalformedURLException;
import java.net.URL;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
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
	 * @return
	 */
	public static URL getURLFromScalar(CatalogListChild catalogThing, CMLScalar scalar) {
		String urlS = scalar.getValue();
		String convention = scalar.getConvention();
		return getURL(convention, urlS, catalogThing);
	}
	
	private static URL getURL(String convention, String urlS, CatalogListChild catalogThing) {
		URL catalogUrl = null;
		if (convention == null) {
			throw new CMLRuntimeException("Must give convention");
		} else if (CatalogListChild.Address.RELATIVE.value.equals(convention)) {
			try {
				catalogUrl = new URL(catalogThing.getURL(), urlS);
			} catch (MalformedURLException e) {
				throw new CMLRuntimeException("Cannot make url from: "+catalogUrl+"; "+urlS);
			}
		} else if (CatalogListChild.Address.RESOURCE.value.equals(convention)) {
			try {
				catalogUrl = Util.getResource(urlS);
			} catch (Exception e) {
				throw new CMLRuntimeException("Cannot get URL resource from: "+urlS);
			}
		} else {
			throw new CMLRuntimeException("Inappropriate convention: "+convention);
		}
		return catalogUrl;
	}
	
	/** getURL from scalar in CMLList.
	 * 
	 * @param catalogThing
	 * @param scalar
	 * @return
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
	 * @return
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
				throw new CMLRuntimeException("Cannot get URL resource from: "+resourceName);
			}
		}
		return catalogUrl;
	}

}
