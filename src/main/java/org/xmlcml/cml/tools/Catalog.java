package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.Indexable;
import org.xmlcml.cml.element.IndexableList;
import org.xmlcml.cml.element.IndexableListManager;

/**
 * simple catalog for CML. 
 * links namespaces to files or resources (either file or directory)
 * 
 * <pre>
 *    <![CDATA[
 *    <map role='catalog'>
 *       <link 
 *          from='http://www.xml-cml.org/cml/dict/mydict'
 *          to='../mydict'>
 *       </link>
 *       <link 
 *          from='http://www.xml-cml.org/cml/dict/catalog'
 *          to='/org/catalog.xml'>
 *       </link>
 *    </map>
 *    ]]>
 * </pre>
 * 
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class Catalog implements CatalogListChild, CMLConstants {

	/** standard name of catalog files.
	 */
	public final static String CATALOG_XML = "catalog.xml";
	/** molecule */
	public final static String DICTIONARY_CATALOG = C_A+"dictionaryCatalog";
	public final static String FRAGMENT_CATALOG = C_A+"fragmentCatalog";
	public final static String MOLECULE_CATALOG = C_A+"moleculeCatalog";

	// this is the direct content of the catalog.xml file
	private CMLMap cmlMap;
	private Map<String, IndexableList> indexableListMap;
	
	private URL url;

	/**
	 * normal constructor.
	 * @param catalogUrl
	 */
	public Catalog(URL catalogUrl) {
		this.url = catalogUrl;
		InputStream in = null;
		Document document = null;
		try {
			in = catalogUrl.openStream();
			document = new CMLBuilder().build(in);
		} catch (Exception e) {
            System.err.println(catalogUrl);
			throw new CMLRuntimeException("error in "+catalogUrl, e);
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
		if (root instanceof CMLMap) {
			cmlMap = (CMLMap) root;
		} else {
			throw new CMLRuntimeException("BAD root element: " + root.getLocalName());
		}
	}
	
    /** constructor from file.
     * convenience method
     * @param file
     * @throws IOException
     */
	public Catalog(File file) throws IOException {
		this(file.toURL());
		
	}

	/**
	 * get catalog as map.
	 * 
	 * @return null if not a map
	 */
	public CMLMap getCmlMap() {
		return cmlMap;
	}

	/** get map of indexables under namespace.
	 *
	 * @param namespace
	 * @return map indexed by id
	 */
	public IndexableList getIndexableList(
		CMLNamespace namespace, IndexableList.Type type) {
		if (cmlMap == null) {
			throw new CMLRuntimeException("cannot get cmlMap");
		}
		String namespaceURI = namespace.getNamespaceURI();
		String to = cmlMap.getToRef(namespaceURI);
		if (to == null) {
			throw new CMLRuntimeException("Cannot find catalog entry for: "
					+ namespace.getNamespaceURI());
		}
		return getIndexableList(to, type.classx);
	}
	
	private IndexableList getIndexableList(String to, Class indexableListClass) {
		if (indexableListMap == null) {
			indexableListMap = new HashMap<String, IndexableList>();
		}
		IndexableList indexableList = indexableListMap.get(to);
		if (to != null) {
			URL toUrl = null;
			try {
				toUrl = new URL(this.getURL(), to);
			} catch (MalformedURLException e1) {
				throw new CMLRuntimeException("Bad catalogue reference: " + to, e1);
			}
			indexableList = IndexableListManager.createFrom(toUrl, indexableListClass);
		}
		return indexableList;
	}

	/** get referenced indexable
	 * @param ref (local "foo", or namespaced ("f:bar"))
	 * @param namespace
	 * @param elementType localName for type (e.g. CMLMolecule.TAG)
	 * @return the indexableor null
	 */
	public Indexable getIndexable (
			String ref, CMLNamespace namespace, IndexableList.Type type) {
		IndexableList indexableList = this.getIndexableList(namespace, type);
		if (indexableList == null) {
			throw new CMLRuntimeException("Cannot find indexableList "+type+" for: "+namespace);
		}
		
		String localName = CMLUtil.getLocalName(ref);
		if (localName.length() == 0) {
			throw new CMLRuntimeException(
					"Cannot have empty local name for mol ref");
		}
		return indexableList.getById(localName);
	}

	/** get URL for catalogMap.
	 * @return the URL
	 */
	public URL getURL() {
		return url;
	}
}
