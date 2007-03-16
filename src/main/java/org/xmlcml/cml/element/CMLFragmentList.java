// /*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/
package org.xmlcml.cml.element;

import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.interfacex.Indexable;
import org.xmlcml.cml.interfacex.IndexableList;
import org.xmlcml.cml.map.IndexableListManager;

/** A container for one or more fragments and joins.
*
*
* \n \nfragmentList can contain several fragments and joins. \nThe normal content model is\n \njoin fragment join fragment...\n \n
*
* user-modifiable class autogenerated from schema if no class exists
* use as a shell which can be edited
* the autogeneration software will not overwrite an existing class file

*/
public class CMLFragmentList extends AbstractFragmentList implements IndexableList {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    private IndexableListManager indexableListManager;

    /** constructor
    *
    */
    public CMLFragmentList() {
    	ensureManager();
    }

    void ensureManager() {
    	if (this.indexableListManager == null) {
    		this.indexableListManager = new IndexableListManager(this);
    	}
    }
    /** copy constructor.
    *
    * @param old CMLFragmentList to copy
    */
    public CMLFragmentList(CMLFragmentList old) {
        super((org.xmlcml.cml.element.AbstractFragmentList) old);
    }

    /** copy node .
    *
    * @return Node
    */
    public Node copy() {
        return new CMLFragmentList(this);
    }
    /** create new instance in context of parent, overridable by subclasses.
    *
    * @param parent parent of element to be constructed (ignored by default)
    * @return CMLFragmentList
    */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLFragmentList();
    }

    /** get listManager
     * @return list manager
     */
    public IndexableListManager getIndexableListManager() {
    	ensureManager();
    	return indexableListManager;
    }

    /** get list of fragments.
     * @return list
     */
    public List<Indexable> getIndexables() {
    	ensureManager();
    	return indexableListManager.getIndexables();
    }

    /** add fragment.
     * @param indexable to add
     */
    public void addIndexable(Indexable indexable) {
    	ensureManager();
    	indexableListManager.add(indexable);
    }

    /** insert fragment.
     * @param indexable to add
     * @param position
     */
    public void insertIndexable(Indexable indexable, int position) {
    	ensureManager();
    	indexableListManager.insert(indexable, position);
    }

    /** insert fragment in order.
     * @param indexable to add
     */
    public void insertIndexableInOrder(Indexable indexable) {
    	ensureManager();
    	indexableListManager.insertInOrder(indexable);
    }

    /** remove fragment.
     * @param indexable to remove
     */
    public void removeIndexable(Indexable indexable) {
    	ensureManager();
    	indexableListManager.remove(indexable);
    }

    /** get fragment by id.
     * @param id
     * @return molecule or null
     */
    public Indexable getIndexableById(String id) {
    	ensureManager();
    	return indexableListManager.getById(id);
    }

    /** get index
     * @return index
     */
    public Map<String, Indexable> getIndex() {
    	ensureManager();
    	return indexableListManager.getIndex();
    }

    /** class of children.
     * @return CMLFragment.class
     */
    public Class getIndexableClass() {
    	return CMLFragment.class;
    }

    /** name of child element.
     * @return CMLFragment.TAG
     */
    public String getIndexableLocalName() {
    	return CMLFragment.TAG;
    }

    /** update the index.
     */
    public void updateIndex() {
    	ensureManager();
    	this.indexableListManager.indexList();
    }
}
