package org.xmlcml.cml.element;

import java.util.List;
import java.util.Map;


/**
 * catalogable object
 * retrievable from a catalog.
 * still being worked out
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface IndexableList {
	/** possible indexables.
	 * this list may not be essential
	 * @author pm286
	 *
	 */
	public enum Type {
		/** fragmentList */
		FRAGMENT_LIST(CMLFragmentList.TAG, new CMLFragmentList().getClass()),
		/** molecule */
		MOLECULE_LIST(CMLMoleculeList.TAG, new CMLMoleculeList().getClass());
		public String value;
		public Class classx;
		private Type(String s, Class classx) {
			this.value = s;
			this.classx = classx;
		}
	}

	/** get an Indexable child.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method
	 * @param id
	 * @return the indexed child or null
	 */
	Indexable getById(String id);

	/** remove an Indexable child.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method (e.g. remove(CMLMolecule)
	 * @param indexable
	 */
	void removeIndexable(Indexable indexable);

	/** add an Indexable child.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method (e.g. add(CMLMolecule)
	 * @param indexable
	 */
	void addIndexable(Indexable indexable);

	/** add an Indexable child.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method (e.g. add(CMLMolecule)
	 * @param indexable
	 * @param position
	 */
	void insertIndexable(Indexable indexable, int position);

	/** add an Indexable child in order of id.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method (e.g. add(CMLMolecule)
	 * @param indexable
	 */
	void insertIndexableInOrder(Indexable indexable);

	/** add an Indexable child in order of id.
	 * Must be of consistent type (i.e. CMLMoleculeList
	 * requires CMLMolecule children). This method might be
	 * wrapped in a convenience method (e.g. add(CMLMolecule)
	 * @param indexable
	 */
	List<Indexable> getIndexables();
	
	/** get the index
	 * @return the index (may be empty but not null)
	 */
	Map<String, Indexable> getIndex();
	
	/** ensure integrity of list and children.
	 * @return class of child
	 */
	Class getIndexableClass();

	/** ensure integrity of list and children.
	 * @return localName of child
	 */
	String getIndexableLocalName();

	/** every IndexableList has an IndexableListManager to manage and 
	 * coordinate functionality.
	 * @return listManager
	 */
	IndexableListManager getIndexableListManager();

	/** update index.
	 */
	void updateIndex();
}
