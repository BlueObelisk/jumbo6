package org.xmlcml.cml.element;

import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.interfacex.Indexable;
import org.xmlcml.cml.interfacex.IndexableList;
import org.xmlcml.cml.map.IndexableListManager;

/** A container for one or more molecules.
*
*
* moleculeList can contain several molecules.
* These may be related in many ways and there is/are controlled
* semantics. However it should not be used for a molecule
* consisting of descendant molecules for which molecule
* should be used.
* A moleculeList can contain nested moleculeLists.
*
* user-modifiable class autogenerated from schema if no class exists
* use as a shell which can be edited
* the autogeneration software will not overwrite an existing class file

*/
public class CMLMoleculeList extends AbstractMoleculeList implements IndexableList {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** argument name to identify id.
     */
    public final static String IDX = "idx";
    private IndexableListManager indexableListManager;

    /** constructor.
    *
    */
    public CMLMoleculeList() {
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
    /** must give simple documentation.
    *
    * @param old CMLMoleculeList to copy
    */
    public CMLMoleculeList(CMLMoleculeList old) {
        super((org.xmlcml.cml.element.AbstractMoleculeList) old);
    }

    /** copy node .
    *
    * @return Node
    */
    public Node copy() {
        return new CMLMoleculeList(this);
    }
    /** create new instance in context of parent, overridable by subclasses.
    *
    * @param parent parent of element to be constructed (ignored by default)
    * @return CMLMoleculeList
    */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLMoleculeList();
    }

    /** get list of molecules.
     * @return list
     */
    public List<Indexable> getIndexables() {
    	ensureManager();
    	return indexableListManager.getIndexables();
    }

    /** add molecule.
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

    /** insert molecule in order.
     * @param indexable to add
     */
    public void insertIndexableInOrder(Indexable indexable) {
    	ensureManager();
    	indexableListManager.insertInOrder(indexable);
    }

    /** remove molecule.
     * @param indexable to remove
     */
    public void removeIndexable(Indexable indexable) {
    	ensureManager();
    	indexableListManager.remove(indexable);
    }

    /** get molecule by id (from interface)
     * @param id
     * @return molecule or null
     */
    public Indexable getIndexableById(String id) {
    	ensureManager();
    	return indexableListManager.getById(id);
    }

    /** get index
     * @return molecule or null
     */
    public Map<String, Indexable> getIndex() {
    	ensureManager();
    	return indexableListManager.getIndex();
    }

    /** class of children.
     * @return CMLMolecule.class
     */
    public Class getIndexableClass() {
    	return CMLMolecule.class;
    }

    /** name of child element.
     * @return CMLMolecule.TAG
     */
    public String getIndexableLocalName() {
    	return CMLMolecule.TAG;
    }

    /** update the index.
     */
    public void updateIndex() {
    	ensureManager();
    	this.indexableListManager.indexList();
    }
}
