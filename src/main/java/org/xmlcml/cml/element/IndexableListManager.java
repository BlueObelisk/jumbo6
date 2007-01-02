package org.xmlcml.cml.element;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
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

	private IndexableList indexableList;
	private Map<String, Indexable> map;
	private String indexableLocalName;
	
	public IndexableListManager(IndexableList indexableList) {
		
		this.indexableList = indexableList;
    	indexList();
	}

	/** index all current indexable children.
	 * 
	 * @return
	 */
    public Map indexList() {
    	map = new HashMap<String, Indexable>();
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
    * @param URL
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
    public Map<String, Indexable> getIndex() {
    	return map;
    }
    
    /** add indexable.
     * 
     * @param indexable to add
     */
    public void add(Indexable indexable) {
    	((Element)indexableList).appendChild((Element)indexable);
		map.put(indexable.getId(), indexable);
    }

    /** remove indexable.
     * 
     * @param indexable to remove
     */
    public void remove(Indexable indexable) {
    	((Element)indexableList).removeChild((Element)indexable);
		map.remove(indexable.getId());
    }

    /** get indexable by id.
     * 
     * @param id
     * @return indexable or null
     */
    public Indexable getById(String id) {
    	return map.get(id);
    }
    
}
