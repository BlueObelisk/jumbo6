package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import org.apache.log4j.Logger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmlcml.cml.attribute.GenericDictionaryMap;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.interfacex.GenericDictionary;
import org.xmlcml.cml.interfacex.IDictionary;

/**
 * user-modifiable class supporting dictionary. *
 */
public class CMLDictionary extends AbstractDictionary implements
		GenericDictionary, IDictionary {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

	final static Logger logger = Logger
			.getLogger(CMLDictionary.class.getName());

	protected Map<String, CMLEntry> entryMap = null;

	/**
	 * constructor.
	 */
	public CMLDictionary() {
	}

	/**
	 * constructor.
	 *
	 * @param old
	 */
	public CMLDictionary(CMLDictionary old) {
		super((AbstractDictionary) old);
	}

	/**
	 * copy node .
	 *
	 * @return Node
	 */
	public Node copy() {
		return new CMLDictionary(this);

	}

	/**
	 * create new instance in context of parent, overridable by subclasses.
	 *
	 * @param parent
	 *            parent of element to be constructed (ignored by default)
	 * @return CMLDictionary
	 */
	public CMLElement makeElementInContext(Element parent) {
		return new CMLDictionary();
	}

	/**
	 * index entries by id.
	 *
	 */
	public void indexEntries() {
		if (entryMap == null) {
			entryMap = new HashMap<String, CMLEntry>();
			CMLElements<CMLEntry> entryNodes = this.getEntryElements();
			for (CMLEntry entry : entryNodes) {
				String id = entry.getId();
				if (id != null) {
					entryMap.put(id, entry);
				}
			}
		}
	}

	/**
	 * creates dictionary from file;
	 * effectively static but requires to be called from a dictionary object
	 * @param file
	 *            file to create from
	 * @return the dictionary or null
	 * @throws IOException
	 * @throws CMLRuntimeException
	 *             if file is not a well-formed dictionary
	 */
	public CMLDictionary createDictionary(File file) throws IOException {
		return createDictionary(file.toURL());
	}

	/**
	 * creates dictionary from file;
	 * effectively static
	 * @param url
	 *            to create from
	 * @return the dictionary or null
	 * @throws IOException
	 * @throws CMLRuntimeException
	 *             if file is not a well-formed dictionary
	 */
	public CMLDictionary createDictionary(URL url) throws IOException {
		Document dictDoc = CMLDictionary.createDictionary0(url);
		CMLDictionary dictionary1 = null;
		if (dictDoc != null) {
			Element root = dictDoc.getRootElement();
			if (root instanceof CMLDictionary) {
				dictionary1 = new CMLDictionary((CMLDictionary) root);
			} else {
				throw new CMLRuntimeException(
						"Expected CMLDictionary root element, found: "
								+ root.getClass().getName() + S_SLASH
								+ root.getLocalName());
			}
		}
		if (dictionary1 != null) {
			dictionary1.indexEntries();
		}
		return dictionary1;
	}

	static Document createDictionary0(File file) throws IOException {
		return createDictionary0(file.toURL());
	}

	static Document createDictionary0(URL url)
			throws IOException {
		Document dictDoc = null;
		InputStream in = null;
		// this will fail if dictionary is badly formed
		try {
			in = url.openStream();
			dictDoc = new CMLBuilder().build(in);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new CMLRuntimeException("NULL " + e.getMessage() + S_SLASH + e.getCause()
					+ " in " + url);
		} catch (ValidityException e) {
			throw new CMLRuntimeException(S_EMPTY + e.getMessage() + S_SLASH + e.getCause()
					+ " in " + url);
		} catch (ParsingException e) {
			System.err.println("ERR at line/col " + e.getLineNumber() + S_SLASH
					+ e.getColumnNumber());
			throw new CMLRuntimeException(" in " + url, e);
		}
		finally {
			try {
				in.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return dictDoc;
	}

	/**
	 * get Entry by id.
	 *
	 * @deprecated use getCMLEntry()
	 * @param id
	 *            the entryId (null if absent)
	 * @return entry
	 */
	public CMLEntry getGenericEntry(String id) {
		return getCMLEntry(id);
		// throw new CMLRuntime("deprecated, use getCMLEntry()");
	}

	/** get Entry by id.
	 * do not ignore case
	 * @param id the entryId (null if absent)
	 * @return entry
	 */
	public CMLEntry getCMLEntry(String id) {
		this.indexEntries();
		return entryMap.get(id);
	}

	/**
	 * add new Entry.
	 *
	 * @param entry
	 *            to add
	 * @throws CMLRuntimeException
	 *             entry already present.
	 */
	public void addEntry(CMLEntry entry) {
		String id = entry.getId();
		if (id == null) {
			throw new CMLRuntimeException("Entry has no id");
		}
		if (getCMLEntry(id) != null) {
			throw new CMLRuntimeException("Entry for " + id + " already present");
		}
		entryMap.put(id, entry);
		this.appendChild(entry);
	}

	/**
	 * add new Entry in order of id
	 *
	 * @param entry to add
	 * @throws CMLRuntimeException
	 *             entry already present.
	 */
	public void addEntryInOrder(CMLEntry entry) {
		String id = entry.getId();
		if (id == null) {
			throw new CMLRuntimeException("Entry has no id");
		}
		if (getCMLEntry(id) == null) {
			entryMap.put(id, entry);
			CMLElements<CMLEntry> entries = this.getEntryElements();
			int idx = entries.size();
			for (CMLEntry entry0 : entries) {
				if (id.compareTo(entry0.getId()) < 0) {
					idx = this.indexOf(entry0);
					break;
				}
			}
			this.insertChild(entry, idx);
		}
	}

	/**
	 * remove Entry. calls removeEntryById()
	 *
	 * @param entry
	 *            to remove, no action if not present
	 *
	 */
	public void removeEntry(CMLEntry entry) {
		String id = entry.getId();
		removeEntryById(id);
	}

	/**
	 * remove Entry by id. the preferred method
	 *
	 * @param id
	 *            of entry to remove, no action if null or not present
	 *
	 */
	public void removeEntryById(String id) {
		if (id != null) {
			CMLEntry entry1 = (CMLEntry) this.getCMLEntry(id);
			if (entry1 != null) {
				entryMap.remove(id);
				entry1.detach();
			}
		}
	}

	/**
	 * create dictionaryMap.
	 *
	 * @param file
	 * @param useSubdirectories
	 * @return dictionaryMap
	 */
	public GenericDictionaryMap createDictionaryMap(File file,
			boolean useSubdirectories) {
		return null;
	}

	/** sort entries.
	 * also sorts enumerations in each entry
	 */
    public void sortEntries() {
    	TreeSet<CMLEntry> treeSet = new TreeSet<CMLEntry>();
    	for (CMLEntry entry : this.getEntryElements()) {
    		treeSet.add(entry);
    		entry.detach();
    	}
    	Iterator<CMLEntry> iterator = treeSet.iterator();
    	while (iterator.hasNext()) {
    		CMLEntry entry = (CMLEntry) iterator.next();
    		this.appendChild(entry);
    		entry.sortEnumerations();
    	}
    }
}
