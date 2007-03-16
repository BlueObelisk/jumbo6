package org.xmlcml.cml.attribute;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.interfacex.GenericDictionary;
import org.xmlcml.cml.interfacex.GenericEntry;
import org.xmlcml.cml.map.DictionaryMap;

/**
 * a map of dictionaries by namespaceURI. required for looking up entries by
 * dictRefAttributes *
 */
public abstract class GenericDictionaryMap extends
		HashMap<String, GenericDictionary> implements CMLConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = -773150330417542521L;

	final static Logger logger = Logger
			.getLogger(DictionaryMap.class.getName());

	/**
	 * constructor.
	 */
	public GenericDictionaryMap() {
	}

	/**
	 * gets all dictionaries in directory. this is AWFUL - we need a catalog
	 * 
	 * @param dir
	 *            the directory
	 * @param useSubdirectories
	 *            recurse into subdirectories
	 * @param genericDictionary
	 *            type of dictionaryMap to create
	 * @throws IOException
	 */
	public GenericDictionaryMap(File dir, boolean useSubdirectories,
			GenericDictionary genericDictionary) throws IOException {
		if (dir.isDirectory()) {
			String[] files = dir.list();
			for (String file : files) {
				if (file.endsWith(XML_SUFF)) {
					File f = new File(dir, file);
					GenericDictionary dict = null;
					try {
						dict = genericDictionary.createDictionary(f);
					} catch (CMLRuntimeException e) {
						// non-wellformed dictionary
						throw new CMLRuntimeException("Badly formed dictionary: " + f
								+ " \n " + e);
					}
					if (dict != null) {
						this.put(dict.getNamespace(), dict);
					}
				} else if (useSubdirectories) {
					Map<String, GenericDictionary> childDictionaryMap = genericDictionary
							.createDictionaryMap(new File(dir, file),
									useSubdirectories);
					if (childDictionaryMap != null) {
						for (String s : childDictionaryMap.keySet())
							this.put(s, childDictionaryMap.get(s));
					}
				}
			}
		}
	}

	/**
	 * gets all dictionaries in a catalog.
	 * 
	 * @param catalogUrl
	 * @param genericDictionary type of dictionaryMap to create
	 * @throws IOException
	 */
	public void mapAllDictionariesInCatalog(URL catalogUrl,
			GenericDictionary genericDictionary) throws IOException {
//		GenericDictionaryMap theMap = new GenericDictionaryMap();
		Document doc = null;
		InputStream in = null;
		try {
			in = catalogUrl.openStream();
			doc = new CMLBuilder().build(in);
		} catch (IOException e1) {
			throw e1;
		} catch (Exception e1) {
			throw new CMLRuntimeException("" + e1 + " in " + in);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Element root = doc.getRootElement();
		if (!(root instanceof CMLMap)) {
			throw new CMLRuntimeException("Catalog (" + catalogUrl
					+ ") must be a CMLMap");
		}
		CMLMap map = (CMLMap) doc.getRootElement();
		for (CMLLink link : map.getLinkElements()) {
			String namespace = link.getFrom();
			if (namespace == null) {
				throw new CMLRuntimeException("Missing namespace (from) in catalog");
			}
			String resource = link.getTo();
			if (resource == null) {
				throw new CMLRuntimeException("Missing resource (to) in catalog");
			}
			GenericDictionary dict = null;
			URL linkUrl = new URL(catalogUrl, resource);
			dict = genericDictionary.createDictionary(linkUrl);
			if (dict != null) {
				String dictNamespace = dict.getNamespace();
				if (!namespace.equals(dictNamespace)) {
					throw new CMLRuntimeException("namespace in catalog (" + namespace
							+ ") does not match namespace in dictionary: "
							+ dictNamespace);
				}
				this.put(dict.getNamespace(), dict);
			}
		}
	}

	/**
	 * gets a dictionary from a namespace.
	 * 
	 * @param namespaceURI
	 * @return the dictionary
	 */
	public GenericDictionary getDictionary(String namespaceURI) {
		return this.get(namespaceURI);
	}

	/**
	 * gets an dictionary from a dictRef and namespaceMap.
	 * 
	 * @param namespaceRefAttribute
	 * @return the dictionary or null
	 */
	public GenericDictionary getDictionary(
			NamespaceRefAttribute namespaceRefAttribute) {
		String namespaceURI = (namespaceRefAttribute == null) ? null
				: namespaceRefAttribute.getNamespaceURIString();
		GenericDictionary gd = (namespaceURI == null) ? null : this
				.get(namespaceURI);
		return gd;
	}

	/**
	 * gets an entry from a dictRef and namespaceMap.
	 * 
	 * @param namespaceRefAttribute
	 * @return the entry or null
	 */
	public GenericEntry getEntry(NamespaceRefAttribute namespaceRefAttribute) {
		String entryId = (namespaceRefAttribute == null) ? null
				: namespaceRefAttribute.getIdRef();
		GenericDictionary dictionary = (entryId == null) ? null : this
				.getDictionary(namespaceRefAttribute);
		return (dictionary == null) ? null : dictionary
				.getGenericEntry(entryId);
	}

	/**
	 * some debugging output. probably to sysout
	 */
	public void debug() {
		System.out.println("Dictionary map: " + this.size());
		for (String s : this.keySet()) {
			GenericDictionary gd = this.get(s);
			System.out.println(s + S_COLON);
			gd.debug();
		}
	}
}
