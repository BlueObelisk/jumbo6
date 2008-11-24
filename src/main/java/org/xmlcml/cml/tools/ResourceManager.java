package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.xmlcml.cml.element.CMLMolecule;

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
	private HashMap <String, HashMap<idTypes, HashMap<String, CMLElement>>> resourceIndex;
	private URI rootURI;
	public enum idTypes { UID, ID }
	
	
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
			resourceIndex = new HashMap<String, HashMap<idTypes,HashMap<String,CMLElement>>>();
			rootURI = uri;
		}
		else throw new RuntimeException("bad map at " + uri.toString());
	}

	
	public CMLMap getCmlMap() {
		return cmlMap;
	}

	
	/**
	 * Returns (a copy of) the index of IDs to Elements for a given namespace
	 * @param namespace
	 * @return
	 */
	public HashMap <idTypes, HashMap<String, CMLElement>> getIndex(String namespace) {
		
		if (resourceIndex.containsKey(namespace)) {
			return new HashMap<idTypes, HashMap<String,CMLElement>> (resourceIndex.get(namespace));
		}
		
		else {
			reindex(namespace);
		}
		
		return new HashMap<idTypes, HashMap<String,CMLElement>> (resourceIndex.get(namespace));
	}

	
	/**
	 * Rebuilds the index for a given namespace
	 * @param namespace
	 */
	public void reindex(String namespace) {
		resourceIndex.put(namespace, new HashMap<idTypes, HashMap<String,CMLElement>>());
		resourceIndex.get(namespace).put(idTypes.ID, new HashMap<String, CMLElement>());
		resourceIndex.get(namespace).put(idTypes.UID, new HashMap<String, CMLElement>());
		if (cmlMap.getToRef(namespace) == null) return;
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
		resourceIndex.get(namespace).get(idTypes.UID).put(uniqueID, (CMLElement) document.getRootElement());
		if (id != null) resourceIndex.get(namespace).get(idTypes.ID).put(id, (CMLElement) document.getRootElement());
	}

	
	static String getUniqueID(String filename) {
		if (filename.endsWith(".xml")) {
			return filename.substring(0, filename.length() - 4);
		}
		else return filename;
	}


	/**
	 * Returns (a copy of) the resource with the specified unique ID in the specified namespace
	 * @param namespace
	 * @param uid
	 * @return
	 */
	public CMLElement getResourceByUID(String namespace, String uid) {

		if (cmlMap.getToRef(namespace) == null) throw new RuntimeException("unknown namespace " + namespace);
		if (resourceIndex.get(namespace) == null) reindex(namespace);
		Map <String, CMLElement> foo = resourceIndex.get(namespace).get(idTypes.UID);
		if (foo == null) throw new RuntimeException("unknown namespace " + namespace);
		CMLElement resource = foo.get(uid);
		if (resource == null) return null;
		return (CMLElement) resource.copy();
	}


	public HashMap<idTypes, HashMap<String,CMLElement>> getIndex(CMLNamespace namespace) {
		return getIndex(namespace.getNamespaceURI());
	}


	/**
	 * Dereferences an element
	 * @param element
	 * @return
	 */
	public CMLElement deref(CMLElement element, idTypes idType) {

		String ref = element.getAttributeValue("ref");
		if (ref == null) throw new RuntimeException("must have ref to deref!");
		String [] split = ref.split(":");
		String prefix = split[0];
		String id = split[1];
		String namespace = element.getNamespaceURI(prefix);

		Class clazz = this.getClass();
		try {
			Class [] params = new Class [2];
			params [0] = params [1] = String.class;
			Method method = clazz.getMethod("getResourceBy" + idType, params);
			Object [] args = new String[2];
			args[0] = namespace;
			args[1] = id;
			return (CMLElement) method.invoke(this, args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}


	public CMLElement getResourceByID(String namespace, String id) {

		if (cmlMap.getToRef(namespace) == null) throw new RuntimeException("unknown namespace " + namespace);
		if (resourceIndex.get(namespace) == null) reindex(namespace);
		Map <String, CMLElement> foo = resourceIndex.get(namespace).get(idTypes.ID);
		if (foo == null) throw new RuntimeException("unknown namespace " + namespace);
		CMLElement resource = foo.get(id);
		if (resource == null) return null;
		return (CMLElement) resource.copy();
	}

}
