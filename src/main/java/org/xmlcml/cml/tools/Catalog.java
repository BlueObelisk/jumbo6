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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.map.Indexable;
import org.xmlcml.cml.map.IndexableByIdList;
import org.xmlcml.cml.map.IndexableByIdListManager;

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
 * @deprecated use ResourceManager instead 
 */
public class Catalog implements CatalogListChild, CMLConstants {

	/** standard name of catalog files.
	 */
	public final static String CATALOG_XML = "catalog.xml";
	/** molecule */
	public final static String DICTIONARY_CATALOG = C_A+"dictionaryCatalog";
    /** dewisott */
	public final static String FRAGMENT_CATALOG = C_A+"fragmentCatalog";
    /** dewisott */
	public final static String MOLECULE_CATALOG = C_A+"moleculeCatalog";

	// this is the direct content of the catalog.xml file
	private CMLMap cmlMap;
	private Map<String, IndexableByIdList> indexableListMap;
	
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
			throw new RuntimeException("error in "+catalogUrl, e);
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
			throw new RuntimeException("BAD root element: " + root.getLocalName());
		}
	}
	
    /** constructor from file.
     * convenience method
     * @param file
     * @throws IOException
     */
	public Catalog(File file) throws IOException {
		this(file.toURI().toURL());
		
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
	 * also updates the index
	 * @param namespace
	 * @param type
	 * @return map indexed by id
	 */
	public IndexableByIdList getIndexableList(
		CMLNamespace namespace, IndexableByIdList.Type type) {
		if (cmlMap == null) {
			throw new RuntimeException("cannot get cmlMap");
		}
		String namespaceURI = namespace.getNamespaceURI();
		String to = cmlMap.getToRef(namespaceURI);
		if (to == null) {
			throw new RuntimeException("Cannot find catalog entry for: "
					+ namespace.getNamespaceURI());
		}
		IndexableByIdList indexableList = getIndexableList(to, type.classx);
		indexableList.updateIndex();
		return indexableList;
	}
	
	private IndexableByIdList getIndexableList(String to, Class<?> indexableListClass) {
		if (indexableListMap == null) {
			indexableListMap = new HashMap<String, IndexableByIdList>();
		}
		IndexableByIdList indexableList = indexableListMap.get(to);
		if (to != null) {
			URL toUrl = null;
			try {
//                LOG.debug("To URL is ctx: "+ url +" + "+ to);
				toUrl = new URL(this.getURL(), to);
			} catch (MalformedURLException e1) {
				throw new RuntimeException("Bad catalogue reference: " + to, e1);
			}
			indexableList = IndexableByIdListManager.createFrom(toUrl, indexableListClass);
		}
		return indexableList;
	}

	/** get referenced indexable
	 * @param ref (local "foo", or namespaced ("f:bar"))
	 * @param namespace
	 * @param type 
	 * @return the indexableor null
	 */
	public Indexable getIndexable (
			String ref, CMLNamespace namespace, IndexableByIdList.Type type) {
		IndexableByIdList indexableList = this.getIndexableList(namespace, type);
		if (indexableList == null) {
			throw new RuntimeException("Cannot find indexableList "+type+" for: "+namespace);
		}
		
		String localName = CMLUtil.getLocalName(ref);
		if (localName.length() == 0) {
			throw new RuntimeException(
					"Cannot have empty local name for mol ref");
		}
		return indexableList.getIndexableById(localName);
	}

	/** get URL for catalogMap.
	 * @return the URL
	 */
	public URL getURL() {
		return url;
	}

}
