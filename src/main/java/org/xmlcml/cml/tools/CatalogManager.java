package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;

/**
 * list of catalogs for CML.
 * Basis is:
 *   a top level catalogListUrl can be found from JUMBO. 
 *   This refers to EITHER 
 *     a catalogList.xml file (specific name) 
 *     in either 
 *       {user.dir}.jumbo 
 *     or
 *       in toplevel of project
 *     or 
 *       in toplevel of resource(class) tree
 *   OR
 *     a full URL obtained from elsewhere
 * 
 * the catalogList.xml file can contain addresses/urls for
 *   nested catalogList.xml files
 *   catalog.xml files
 *    
 *  the address is one of:
 *    full URL (requires file:// or http:// protocol)
 *    relative to resource 
 *      (no protocol accompanied by convention=C_A+"resourceUrl")
 *    relative to current catalog(List) 
 *      (no protocol accompanied by convention=C_A+"relativeUrl")
 *      
 * Current format is: something like
 * 
 * <pre>
 *    <![CDATA[
<list xmlns="http://www.xml-cml.org/schema">
  <scalar role='cml:dictionaryCatalog' convention='cml:resourceUrl'
    >org/xmlcml/cml/element/examples/dict/catalog.xml</scalar>
  <scalar role='cml:moleculeCatalog' convention='cml:resourceUrl'
    >org/xmlcml/cml/tools/examples/molecules/catalog.xml</scalar>
  <scalar role='cml:unitsCatalog' convention='cml:resourceUrl'
    >org/xmlcml/cml/element/examples/units/catalog.xml</scalar>
  <scalar role='cml:catalogList' convention='cml:resourceUrl'
    >org/xmlcml/cml/element/examples/foo/catalogList.xml</scalar>
</list>
 *    ]]>
 * </pre>
 * 
 * caralogList can include catalogs and further catalogLists. 
 * A catalog may point to directories (with many entries) or indivdual files. 
 * Within a directory the components are all assumed to be of a 
 * similar type (e.g. molecules)
 * 
 * The catalogs are managed by CatalogTool. 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CatalogManager implements CatalogListChild, CMLConstants {
	private static Logger LOG = Logger.getLogger(CatalogManager.class);

	/**
	 * folder located in user.dir. if this exists, then it should contain a
	 * catlog.xml pointing to other catalogs
	 */
	public final static String DOT_JUMBO = ".jumbo";

	/**
	 * standard name of catalogList file.
	 */
	public final static String CATALOGLIST_XML = "catalogList.xml";
	// the URL used to construct this tool
	private URL url;
	// the XML contents of this tool
	private CMLList catalogList;

	/**
	 * normal constructor.
	 * @param catalogListUrl
	 */
	public CatalogManager(URL catalogListUrl) {
		this.url = catalogListUrl;
		InputStream in = null;
		Document document = null;
		try {
			in = catalogListUrl.openStream();
			document = new CMLBuilder().build(in);
		} catch (Exception e) {
			throw new RuntimeException("error in "+catalogListUrl+": "+e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Element root = document.getRootElement();
		if (root instanceof CMLList) {
			catalogList = (CMLList) root;
		} else {
			throw new RuntimeException("BAD root element for catalogList: "
					+ root.getLocalName());
		}
	}
	
    /** constructor from file.
     * convenience method
     * @param file
     * @throws IOException
     */
	public CatalogManager(File file) throws IOException {
		this(file.toURI().toURL());
		
	}

	/**
	 * get catalog as list.
	 * 
	 * @return null if not a list
	 */
	public CMLList getCatalogList() {
		return catalogList;
	}

	/**
	 * gets the URL for top catalog. 
	 * first looks for <user.dir>/.jumbo/catalogList.xml
	 * if this is missing, looks for resource under "." - this will be in the
	 * directory immediately above org/cml... *.class usually "bin" or "classes"
	 * 
	 * @return null means not found, else a list of CMLMap which are the actual
	 *         catalogs
	 */
	public static URL getTopCatalogUrl() {
		String homeDir = System.getProperty("user.home");
		File dotJumbo = new File(homeDir + File.separator + DOT_JUMBO);
		URL catalogListUrl = null;
		// try dotJumbo dir
		if (dotJumbo.exists()) {
			try {
				catalogListUrl = new URL(dotJumbo.toURI().toURL(), CATALOGLIST_XML);
				if (new File(catalogListUrl.getFile()).exists()) {
					LOG.debug("DJ EXISTS");
				} else {
					catalogListUrl = null;
				}
			} catch (Exception e) {
				throw new RuntimeException("BAD .jumbo/catalogList.xml: " + e);
			}
		}
		// try root of project
		if (catalogListUrl == null) {
			String userDir = System.getProperty("user.dir");
			File catalogListFile = new File(userDir + File.separator + CATALOGLIST_XML);
			if (catalogListFile.exists()) {
				try {
					catalogListUrl = catalogListFile.toURI().toURL();
				} catch (MalformedURLException e) {
					CMLUtil.BUG("Cannot have malformed URL"+e);
				}
			}
		}
		// try jar file
		if (catalogListUrl == null) {
			LOG.debug("No DOT JUMBO");
			catalogListUrl = CatalogUtil.getURLFromResource(CATALOGLIST_XML);
		}
		return catalogListUrl;
	}
	
	/**
	 * gets the top catalog. first looks for <user.dir>/.jumbo/catalog.xml if
	 * this is missing, looks for resource under CMLConstants.S_PERIOD - this will be in the
	 * directory immediately above org/cml... *.class usually "bin" or "classes"
	 * 
	 * @return null means not found, else a list of CMLMap which are the actual
	 *         catalogs
	 */
	public static CatalogManager getTopCatalogManager() {
		URL topCatalogListUrl = CatalogManager.getTopCatalogUrl();
		if (topCatalogListUrl == null) {
			throw new RuntimeException("Null top level catalogList");
		}
		return new CatalogManager(topCatalogListUrl);
	}
	
	/**
	 * main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			Util.println("Usage: java org.xmlcml.cml.base.CMLCatalog");
			CatalogManager.getTopCatalogManager();
		} else {

		}
	}

	/** get catalogTool from list of catalogTools.
	 * this.catalogTool must be of listForm
	 * @param role of tool (e.g. "molecules", "units");
	 * @return catalogTool or null
	 */
	public Catalog getCatalog(String role) {
		Catalog catalog = null;
		if (this.catalogList == null) {
			throw new RuntimeException("catalogManager must be of form list");
		}
		List<Node> mapNodes = CMLUtil.getQueryNodes(catalogList, CMLScalar.NS, CMLConstants.CML_XPATH);
		CMLScalar theScalar = null;
		for (Node node : mapNodes) {
			CMLScalar scalar = (CMLScalar) node;
			String dictRef = scalar.getDictRef();
			if (role.equals(dictRef)) {
				theScalar = scalar;
				break;
			}
		}
		if (theScalar != null) {
			URL catalogUrl = CatalogUtil.getURLFromScalar(this, theScalar);
			catalog = new Catalog(catalogUrl);
		}
		return catalog;
	}

	/** get URL.
	 * @return url
	 */
	public URL getURL() {
		return this.url;
	}
}
