package org.xmlcml.cml.element;

import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

/**
 * The enumerations are managed by the IndexableList mechanism
 */
public class CMLEntry extends AbstractEntry implements GenericEntry, IndexableList {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    private IndexableListManager indexableListManager;
    /**
     * constructor.
     */
    public CMLEntry() {
    	ensureManager();
    }
    
    private void ensureManager() {
    	if (this.indexableListManager == null) {
    		this.indexableListManager = new IndexableListManager(this);
    	}
    }

    /** get listManager
     * @return list manager
     */
    public IndexableListManager getIndexableListManager() {
    	ensureManager();
    	return indexableListManager;
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLEntry(CMLEntry old) {
        super((AbstractEntry) old);

    }

    /**
     * normal constructor.
     * 
     * @param id of entry (should be unique within dictionary);
     */
    public CMLEntry(String id) {
        this();
        this.setId(id);
    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLEntry(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent of element to be constructed (ignored by default)
     * @return CMLEntry
     */
    public static CMLEntry makeElementInContext(Element parent) {
        return new CMLEntry();

    }
    
    /** get list of enumerations.
     * @return list
     */
    public List<Indexable> getIndexables() {
    	ensureManager();
    	return indexableListManager.getIndexables();
    }

    /** add enumeration.
     * @param indexable to add
     */
    public void addIndexable(Indexable indexable) {
    	ensureManager();
    	indexableListManager.add(indexable);
    }

    /** insert molecule.
     * @param indexable to add
     * @param position
     */
    public void insertIndexable(Indexable indexable, int position) {
    	ensureManager();
    	indexableListManager.insert(indexable, position);
    }

    /** insert enumeration in order.
     * @param indexable to add
     */
    public void insertIndexableInOrder(Indexable indexable) {
    	ensureManager();
    	indexableListManager.insertInOrder(indexable);
    }

    /** remove enumeration.
     * @param indexable to remove
     */
    public void removeIndexable(Indexable indexable) {
    	ensureManager();
    	indexableListManager.remove(indexable);
    }

    /** get enumeration by id (from interface)
     * @param id
     * @return enumeration or null
     */
    public Indexable getById(String id) {
    	ensureManager();
    	return indexableListManager.getById(id);
    }
    
    /** get index
     * @return enumeration or null
     */
    public Map<String, Indexable> getIndex() {
    	ensureManager();
    	return indexableListManager.getIndex();
    }

    /** class of children.
     * @return CMLEnumeration.class
     */
    public Class getIndexableClass() {
    	return CMLEnumeration.class;
    }

    /** name of child element.
     * @return CMLEnumeration.TAG
     */
    public String getIndexableLocalName() {
    	return CMLEnumeration.TAG;
    }
    
    /** update the index.
     */
    public void updateIndex() {
    	ensureManager();
    	this.indexableListManager.indexList();
    }
    
}
