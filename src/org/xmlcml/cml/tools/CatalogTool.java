package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.euclid.EuclidConstants;
import org.xmlcml.euclid.Util;

/**
 * simple catalog for CML. links namespaces to files or resources or further
 * catalogs reads a CML catalog format is:
 * 
 * <pre>
 *    &lt;![CDATA[
 *    &lt;map role=&quot;catalog&quot;&gt;
 *       &lt;link 
 *          from=&quot;http://www.xml-cml.org/cml/dict/mydict&quot;
 *          to=&quot;../mydict&quot;&gt;
 *       &lt;/link&gt;
 *       &lt;link 
 *          from=&quot;http://www.xml-cml.org/cml/dict/catalog&quot;
 *          to=&quot;/org/catalog.xml&quot;&gt;
 *       &lt;/link&gt;
 *    &lt;/map&gt;
 *    ]]&gt;
 * </pre>
 * 
 * There can also be top-level catalog(s) - normally one - which point to the
 * sub catalogs. They are of the form:
 * 
 * <pre>
 *    &lt;![CDATA[
 *    &lt;list role=&quot;catalogList&quot;&gt;
 *       &lt;map role=&quot;dictionary&quot; 
 *          ref=&quot;../org/cml/dict/catalog&quot;&gt;
 *       &lt;/map&gt;
 *       &lt;map role=&quot;unitList&quot; 
 *          ref=&quot;../org/cml/units/catalog.xml&quot;&gt;
 *       &lt;/map&gt;
 *    &lt;/list&gt;
 *    ]]&gt;
 * </pre>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CatalogTool implements CMLConstants {

	/**
	 * folder located in user.dir. if this exists, then it should contain a
	 * catlog.xml pointing to other catalogs
	 */
	public final static String DOT_JUMBO = ".jumbo";

	/**
	 * standard name of catalog files.
	 */
	public final static String CATALOG_XML = "catalog.xml";

	String name;

	Document document;

	CMLMap catalogMap;

	CMLList catalogList;

	private URL catalogUrl;

	/**
	 * normal constructor.
	 * 
	 * @param catalogUrl
	 * @throws IOException
	 */
	public CatalogTool(URL catalogUrl) throws IOException {
		this.catalogUrl = catalogUrl;
		InputStream in = null;
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
		name = catalogUrl.getFile();
		Element root = document.getRootElement();
		if (root instanceof CMLMap) {
			catalogMap = (CMLMap) root;
		} else if (root instanceof CMLList) {
			catalogList = (CMLList) root;
		} else {
			throw new CMLRuntimeException("BAD root element: " + root.getLocalName());
		}
	}

    /** constructor from file.
     * convenience method
     * @param file
     * @throws IOException
     */
	public CatalogTool(File file) throws IOException {
		this(file.toURL());
	}

	/**
	 * gets name of resource used to create catalog.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * get catalog as map. only works for catalog maps
	 * 
	 * @return null if not a map
	 */
	public CMLMap getCatalogMap() {
		return catalogMap;
	}

	/**
	 * get catalog as list. only works for catalog lists
	 * 
	 * @return null if not a list
	 */
	public CMLList getCatalogList() {
		return catalogList;
	}

	/**
	 * gets the top catalog. first looks for <user.dir>/.jumbo/catalog.xml if
	 * this is missing, looks for resource under S_PERIOD - this will be in the
	 * directory immediately above org/cml... *.class usually "bin" or "classes"
	 * 
	 * @return null means not found, else a list of CMLMap which are the actual
	 *         catalogs
	 */
	public static List<CMLMap> getTopCatalog() {
		List<CMLMap> catalogList = null;
		String userDir = System.getProperty("user.dir");
		File dotJumbo = new File(userDir + File.separator + DOT_JUMBO);
		if (dotJumbo.exists()) {
			try {
				catalogList = getDotJumboList(dotJumbo);
			} catch (Exception e) {
				throw new CMLRuntimeException("BAD .jumbo/catalog.xml: " + e);
			}
		} else {
			catalogList = getMapListFromResource();
		}
		return catalogList;
	}

	private static CMLMap getReferencedMap(String ref) {
		CMLMap map = null;
		// URL url = new URL(ref);
		return map;
	}

	private static List<CMLMap> getDotJumboList(File dotJumbo) throws Exception {
		File catalogFile = new File(dotJumbo + File.separator + CATALOG_XML);
		Document document = new CMLBuilder().build(catalogFile);
		return getCatalogList(document);
	}

	private static List<CMLMap> getCatalogList(Document document) {
		List<CMLMap> catalogList = null;
		Element root = document.getRootElement();
		if (root instanceof CMLList) {
			Elements mapElements = ((CMLList) root).getChildCMLElements("map");
			catalogList = new ArrayList<CMLMap>();
			for (int i = 0; i < mapElements.size(); i++) {
				CMLMap map = (CMLMap) mapElements.get(i);
				// FIXME - need ref attribute
				String ref = map.getRef();
				CMLMap refMap = getReferencedMap(ref);
				catalogList.add(refMap);
			}
		}
		return catalogList;
	}

	private static List<CMLMap> getMapListFromResource() {
		List<CMLMap> catalogList = null;
		String context = S_PERIOD; // might change later
		InputStream in = null;
		try {
			in = Util
					.getInputStreamFromResource(context + U_S + CATALOG_XML);
			Document doc = null;
			try {
				doc = new CMLBuilder().build(in);
			} catch (Exception e) {
				throw new CMLRuntimeException("Cannot read resource " + e);
			}
			catalogList = new ArrayList<CMLMap>();
			Nodes nodes = doc.query(".//cml:map", X_CML);
			for (int i = 0; i < nodes.size(); i++) {
				String ref = ((CMLMap) nodes.get(i)).getRef();
				CMLMap map = getMap(context, ref);
				catalogList.add(map);
			}
		} catch (IOException e) {
			throw new CMLRuntimeException("ERROR " + e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return catalogList;
	}

	private static CMLMap getMap(String context, String ref) throws IOException {
		CMLMap map = null;
		InputStream in = null;
		String resource = context + U_S + ref;
		try {
			in = Util.getInputStreamFromResource(resource);
			Document document = null;
			document = new CMLBuilder().build(in);
			if (document.getRootElement() instanceof CMLMap) {
				map = (CMLMap) document.getRootElement();
			}
			if (map == null) {
				throw new CMLRuntimeException("Cannot create map from: " + ref);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new CMLRuntimeException("Cannot find/parse map from resource: " + e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return map;
	}

	/**
	 * main.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: java org.xmlcml.cml.base.CMLCatalog");
			CatalogTool.getTopCatalog();
		} else {

		}
	}

    /** get the URL.
     * this is the commonest way of refrencing the resource
     * @return url or null
     */
	public URL getCatalogUrl() {
		return catalogUrl;
	}
    
    /** lookup up referenced molecule and expands it into full molecule.
     * I think this relates to fragments
     * @param molecule
     */
    public void lookupAndExpandMolecule(CMLMolecule molecule) {
        Nodes nodes = molecule.query("cml:arg[@name='"+CMLMoleculeList.IDX+"']", X_CML);
        if (nodes.size() ==0) {
            molecule.debug("EXPAND");
            throw new CMLRuntimeException("must have calling arg for: "+CMLMoleculeList.IDX);
        }
        CMLArg callingArg = (CMLArg) nodes.get(0);
        /* String argVal = */ callingArg.getString();
        String ref = molecule.getRef();
        CMLMolecule refMol = 
            getReferencedMolecule1(ref, new MoleculeTool(molecule));
        if (refMol == null) {
            throw new CMLRuntimeException("Cannot find molecule: "+ref);
        }
        if (!"fragment".equals(refMol.getRole())) {
            new MoleculeTool(refMol).convertToFragment();
        }
        MoleculeTool moleculeTool = new MoleculeTool(molecule);
        moleculeTool.expandRefFromFragment(refMol);
    }

	/**
	 * get map of molecules under namespace.
	 *
	 * @param namespace
	 * @return map indexed by id
	 */
	public Map<String, CMLMolecule> getMolecules(String namespace) {
		if (this == null) {
			throw new CMLRuntimeException("Null catalogTool");
		}
		CMLMap catalogMap = getCatalogMap();
		if (catalogMap == null) {
			throw new CMLRuntimeException("cannot get catalogMap");
		}
		String to = catalogMap.getToRef(namespace);
		if (to == null) {
			throw new CMLRuntimeException("Cannot find catalog entry for: "
					+ namespace);
		}
	
		URL toUrl = null;
		try {
			toUrl = new URL(getCatalogUrl(), to);
		} catch (MalformedURLException e1) {
			throw new CMLRuntimeException("Bad catalogue reference: " + to, e1);
		}
	
		Map<String, CMLMolecule> moleculeMap = null;
		File file = new File(toUrl.getFile());
		if (file.isDirectory()) {
			moleculeMap = CatalogTool.getMoleculeMapFromDirectory(file);
		} else {
			moleculeMap = CatalogTool.getMoleculeMapFromFile(toUrl);
		}
		return moleculeMap;
	}

	private static Map<String, CMLMolecule> getMoleculeMapFromDirectory(File dir) {
	File[] files = dir.listFiles();
	Map<String, CMLMolecule> moleculeMap = new HashMap<String, CMLMolecule>();
	for (File file : files) {
		if (!file.toString().endsWith(".xml")) {
			continue;
		}
		// File file1 = new File(dir, file.toString());
		CMLMolecule molecule = null;
		try {
			Document doc = new CMLBuilder().build(file);
			molecule = (CMLMolecule) doc.getRootElement();
		} catch (Exception e) {
			System.err.println("File: " + file);
			throw new CMLRuntimeException(e);
		}
		moleculeMap.put(molecule.getId(), molecule);
	}
	return moleculeMap;
}
	
	private static Map<String, CMLMolecule> getMoleculeMapFromFile(URL toUrl) {
		CMLCml cml = null;
		InputStream cmlIn = null;
		try {
			cmlIn = toUrl.openStream();
			cml = (CMLCml) new CMLBuilder().build(cmlIn).getRootElement();
		} catch (Exception e) {
			throw new CMLRuntimeException("Cannot read molecule file: " + toUrl
					+ " (" + e + ")");
		} finally {
			if (cmlIn != null) {
				try {
					cmlIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, CMLMolecule> moleculeMap = new HashMap<String, CMLMolecule>();
		Elements elements = cml.getChildCMLElements(CMLMolecule.TAG);
		for (int i = 0; i < elements.size(); i++) {
			CMLMolecule molecule = (CMLMolecule) elements.get(i);
			moleculeMap.put(molecule.getId(), molecule);
		}
		return moleculeMap;
	}
	/**
	 * get referenced molecule. uses ref attribute on molecule
	 *
	 * @param tool TODO
	 * @return the molecule or null
	 */
	public CMLMolecule getReferencedMolecule(MoleculeTool tool) {
		return getReferencedMolecule1(tool.molecule.getRef(), tool);
	}

	/**
		 * ger referenced molecule.
		 *
		 * @param ref
		 *            (local "foo", or namespaced ("f:bar"))
	 * @param tool TODO
	 * @return the molecule or null
		 */
		public CMLMolecule getReferencedMolecule1(String ref, MoleculeTool tool) {
			if (this == null) {
				new Exception().printStackTrace();
				throw new CMLRuntimeException("Null catalog tool");
			}
			CMLMolecule refMol = null;
			if (ref == null) {
				throw new CMLRuntimeException("ref must not be null");
			}
			int idx = ref.indexOf(EuclidConstants.S_COLON);
			if (idx == -1) {
				throw new CMLRuntimeException(
						"unprefixed reference for molecule NYI");
			}
			String prefix = ref.substring(0, idx);
			if (prefix.length() == 0) {
				throw new CMLRuntimeException(
						"Cannot have empty prefix for mol ref");
			}
			String namespace = tool.molecule.getNamespaceForPrefix(prefix);
			if (namespace == null) {
				throw new CMLRuntimeException("Cannot find namespace for: " + ref);
			}
			Map<String, CMLMolecule> moleculeMap = getMolecules(
					namespace);
			if (moleculeMap == null) {
				throw new CMLRuntimeException("Cannot find molecule map for: "
						+ namespace);
			}
			String localRef = ref.substring(idx + 1);
			if (localRef.length() == 0) {
				throw new CMLRuntimeException(
						"Cannot have empty prefix for mol ref");
			}
			refMol = moleculeMap.get(localRef);
			return refMol;
		}
}
