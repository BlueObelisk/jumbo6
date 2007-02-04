package org.xmlcml.cml.element;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;

/** A container for one or more indexables.
*
* indexableList can contain several indexables. 
* These may be related in many ways and there is are controlled
* semantics. However it should not be used for a indexable
* consisting of descendant indexables for which indexable
* should be used. 
* A indexableList can contain nested indexableLists. 
* 
*/
public class IndexableListManager implements CMLConstants {

	/** duplicate id exception */
	public final static String DUPLICATE_ID = "duplicate id in indexableList: ";
	private IndexableList indexableList;
	private Map<String, Indexable> map;
	private Map<String, Indexable> lowerCaseMap;
	private String indexableLocalName;	// XML name
	
	IndexableListManager(IndexableList indexableList) {
		this.indexableList = indexableList;
		ensureMap();
//    	indexList();
	}
	
	private void ensureMap() {
		if (map == null) {
			map = new HashMap<String, Indexable>();
		}
		if (lowerCaseMap == null) {
			lowerCaseMap = new HashMap<String, Indexable>();
		}
	}

	/** index all current indexable children.
	 * @return map
	 */
    Map indexList() {
    	ensureMap();
    	map.clear();
    	lowerCaseMap.clear();
    	indexableLocalName = indexableList.getIndexableLocalName();
    	List<Node> indexables = CMLUtil.getQueryNodes((Node)indexableList,
			C_E+indexableLocalName, X_CML);
    	for (Node node : indexables) {
    		indexableList.addIndexable((Indexable) node);
    	}
    	return map;
    }
    
    /** make indexableList from URL.
     * either contains a directory with indexable in *.xml
     * or an indexableList in a single XML file.
    * @param url 
    * @param indexableListClass
    */
    public static IndexableList createFrom(URL url, Class indexableListClass) {
    	File file = new File(url.getFile());
    	IndexableList indexableList = null;
    	if (file.isDirectory()) {
    		indexableList = createFromDirectory(file, indexableListClass);
    	} else if (file.toString().endsWith(XML_SUFF)){
    		try {
				indexableList = (IndexableList) new CMLBuilder().build(file).getRootElement();
			} catch (Exception e) {
				throw new CMLRuntimeException("Cannot parse ("+file+") as "+indexableList+";"+e);
			}
    	} else {
    		throw new CMLRuntimeException("exptected either a directory ot *.xml; found: "+url);
    	}
    	return indexableList;
    }
    
    private static IndexableList createFromDirectory(File dir,Class indexableListClass) {
    	IndexableList indexableList = null;
    	try {
			indexableList = (IndexableList) indexableListClass.newInstance();
		} catch (Exception e1) {
			CMLUtil.BUG(""+e1);
		}
    	File[] files = dir.listFiles();
    	CMLBuilder builder = new CMLBuilder();
    	for (File file : files) {
    		if (file.toString().endsWith(XML_SUFF)) {
    			try {
					Document document = builder.build(file);
					Element element = document.getRootElement();
					if (element instanceof Indexable) {
						indexableList.addIndexable((Indexable) element);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Cannot parse XML file: "+file+" /"+e);
				}
    		}
    	}
    	return indexableList;
    }

    /** get map of id to indexable.
     * 
     * @return map
     */
    Map<String, Indexable> getIndex() {
    	ensureMap();
    	return map;
    }
    
    /** get case-insensitive map of id to indexable.
     * 
     * @return map
     */
    Map<String, Indexable> getLowerCaseIndex() {
    	ensureMap();
    	return lowerCaseMap;
    }
    
    /** add indexable.
     * 
     * @param indexable to add
     * @throws CMLRuntimeException if id already in map
     */
    void add(Indexable indexable) throws CMLRuntimeException{
    	ensureMap();
    	String id = indexable.getId();
    	if (id == null) {
    		throw new CMLRuntimeException("indexable has no id: "+indexable.getClass());
    	}
    	if (map.containsKey(id)) {
    		throw new CMLRuntimeException(DUPLICATE_ID + id);
    	}
    	((Element)indexableList).appendChild((Element)indexable);
		map.put(indexable.getId(), indexable);
    }

    /** insert indexable.
     * 
     * @param indexable to add
     * @param position
     * @throws CMLRuntimeException if id already in map
     */
    void insert(Indexable indexable, int position) throws CMLRuntimeException{
    	ensureMap();
    	String id = indexable.getId();
    	if (id == null) {
    		throw new CMLRuntimeException("indexable has no id: "+indexable.getClass());
    	}
    	if (map.containsKey(id)) {
    		throw new CMLRuntimeException(DUPLICATE_ID + id);
    	}
    	((Element)indexableList).insertChild((Element)indexable, position);
		map.put(indexable.getId(), indexable);
    }

    /** insert indexable.
     * 
     * @param indexable to add
     * @param position
     * @throws CMLRuntimeException if id already in map
     */
    void insertInOrder(Indexable indexable) throws CMLRuntimeException{
    	String id = indexable.getId();
    	if (id == null) {
    		throw new CMLRuntimeException("indexable has no id: "+indexable.getClass());
    	}
    	if (map.containsKey(id)) {
    		throw new CMLRuntimeException(DUPLICATE_ID + id);
    	}
    	boolean added = false;
    	Elements elements = ((Element) indexableList).getChildElements();
    	for (int i = 0; i < elements.size(); i++) {
    		if (elements.get(i) instanceof Indexable) {
	    		Indexable indexable0 = (Indexable) elements.get(i);
	    		String id0 = indexable0.getId();
	    		if (id.compareTo(id0) < 0) {
	    	    	((Element)indexableList).insertChild((Element)indexable, i);
	    	    	added = true;
	    	    	break;
	    		}
    		}
    	}
		if (!added) {
	    	((Element)indexableList).appendChild((Element)indexable);
		}
		map.put(indexable.getId(), indexable);
    }

    /** get indexable children.
     * 
     * @return indexables
     */
    List<Indexable> getIndexables() {
    	List<Indexable> list = new ArrayList<Indexable>();
    	Elements elements = ((Element) indexableList).getChildElements();
    	for (int i = 0; i < elements.size(); i++) {
    		Node element = elements.get(i);
    		if (element instanceof Indexable) {
    			list.add((Indexable) element);
    		}
    	}
    	return list;
    }

    /** remove indexable.
     * removes BOTH from map and from parent indexableList
     * @param indexable to remove
     */
    void remove(Indexable indexable) {
    	ensureMap();
    	String id = indexable.getId();
    	if (map.containsKey(id)) {
    		((Element)indexableList).removeChild((Element)indexable);
    		map.remove(id);
    	}
    }

    /** get indexable by id.
     * 
     * @param id
     * @return indexable or null
     */
    Indexable getById(String id) {
    	ensureMap();
    	return map.get(id);
    }
    
}
