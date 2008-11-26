package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.element.CMLMap;

/**
 * First bash at a replacement class for Catalog
 * Doesn't yet support e.g. nicknames for resource files
 * Feel free to add functionality
 * 
 * @author dmj30
 *
 */
public class ResourceManager {

	private CMLMap cmlMap;
	// hashmap of namespace to hashmap of types of id to hashmap of ids to resources with said ids
	private HashMap <String, HashMap<IdTypes, HashMap<String, CMLElement>>> resourceIndex;
	private URI rootURI;
	public enum IdTypes { UID, ID }
	
	
	/**
	 * 
	 * @param uri the uri to catalog.xml
	 */
	public ResourceManager(URI uri) {
		File mapFile = new File(uri);
		Document doc = null;
		try {
			doc = new CMLBuilder().build(mapFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element map = doc.getRootElement();
		if (map instanceof CMLMap) {
			this.cmlMap = (CMLMap) map;
			resourceIndex = new HashMap<String, HashMap<IdTypes,HashMap<String,CMLElement>>>();
			rootURI = uri;
		}
		else throw new RuntimeException("bad map at " + uri.toString());
	}

	public ResourceManager(File file) {
		this(file.toURI());
	}
	
	public CMLMap getCmlMap() {
		return cmlMap;
	}

	
	/**
	 * Returns (a copy of) the index of IDs to Elements for a given namespace
	 * @param namespace
	 * @return
	 */
	public HashMap <IdTypes, HashMap<String, CMLElement>> getIndex(String namespace) {
		
		if (resourceIndex.containsKey(namespace)) {
			return new HashMap<IdTypes, HashMap<String,CMLElement>> (resourceIndex.get(namespace));
		}
		
		else {
			reindex(namespace);
		}
		
		return new HashMap<IdTypes, HashMap<String,CMLElement>> (resourceIndex.get(namespace));
	}

	
	/**
	 * Rebuilds the index for a given namespace
	 * @param namespace
	 */
	public void reindex(String namespace) {
		if (cmlMap.getToRef(namespace) == null) throw new IllegalArgumentException("not a known namespace");
		resourceIndex.put(namespace, new HashMap<IdTypes, HashMap<String,CMLElement>>());
		for (IdTypes idType : IdTypes.values()) {
			resourceIndex.get(namespace).put(idType, new HashMap<String, CMLElement>());	
		}
		File dir = null;
		try {
			dir = new File(new URL(rootURI.toURL(), cmlMap.getToRef(namespace)).toURI());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (!dir.isDirectory()) {
			throw new RuntimeException("not a directory");
		}
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".xml")) index(file, namespace);
		}
	}


	private void index(File file, String namespace) {
		Document document = null;
		try {
			document = new CMLBuilder().build(file);
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String uniqueID = getUniqueID(file.getName());
		String id = document.getRootElement().getAttributeValue("id");
		resourceIndex.get(namespace).get(IdTypes.UID).put(uniqueID, (CMLElement) document.getRootElement());
		if (id != null) resourceIndex.get(namespace).get(IdTypes.ID).put(id, (CMLElement) document.getRootElement());
	}

	
	static String getUniqueID(String filename) {
		if (filename.endsWith(".xml")) {
			return filename.substring(0, filename.length() - 4);
		}
		else return filename;
	}


	public HashMap<IdTypes, HashMap<String,CMLElement>> getIndex(CMLNamespace namespace) {
		return getIndex(namespace.getNamespaceURI());
	}


	/**
	 * Dereferences an element, returning a new object NOT modifying the argument
	 * @param element
	 * @return
	 */
	public CMLElement deref(CMLElement element, IdTypes idType) {

		String ref = element.getAttributeValue("ref");
		if (ref == null) throw new RuntimeException("must have ref to deref!");
		String [] split = ref.split(":");
		String prefix = split[0];
		String id = split[1];
		String namespace = element.getNamespaceURI(prefix);

		return getResourceByID(namespace, id, idType);
	}


	/**
	 * Returns the resource with the specified id of the specified type in the specified namespace
	 */
	public CMLElement getResourceByID(String namespace, String id, ResourceManager.IdTypes idType) {

		if (cmlMap.getToRef(namespace) == null) throw new RuntimeException("unknown namespace " + namespace);
		if (resourceIndex.get(namespace) == null) reindex(namespace);
		Map <String, CMLElement> foo = resourceIndex.get(namespace).get(idType);
		if (foo == null) throw new RuntimeException("unknown namespace " + namespace);
		CMLElement resource = foo.get(id);
		if (resource == null) return null;
		return (CMLElement) resource.copy();
	}

}
