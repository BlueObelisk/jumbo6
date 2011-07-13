/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.map;

import java.util.List;
import java.util.Map;

import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLMoleculeList;


/**
 * catalogable object
 * retrievable from a catalog.
 * still being worked out
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface IndexableByIdList {
	/** possible indexables.
	 * this list may not be essential
	 * @author pm286
	 *
	 */
	
// TODO must override appendChild, removeChild, etc since these
// will not cause indexing	
	public enum Type {
		/** fragmentList */
		FRAGMENT_LIST(CMLFragmentList.TAG, new CMLFragmentList().getClass()),
		/** molecule */
		MOLECULE_LIST(CMLMoleculeList.TAG, new CMLMoleculeList().getClass());
		/** value */
		public String value;
		/** class .*/
		public Class<?> classx;
		private Type(String s, Class<?> classx) {
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
	Indexable getIndexableById(String id);

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
	 * @return list of indexable 
	 */
	List<Indexable> getIndexables();
	
	/** get the index
	 * @return the index (may be empty but not null)
	 */
	Map<String, Indexable> getIndex();
	
	/** ensure integrity of list and children.
	 * @return class of child
	 */
	Class<?> getIndexableClass();

	/** ensure integrity of list and children.
	 * @return localName of child
	 */
	String getIndexableLocalName();

	/** every IndexableByIdList has an IndexableByIdListManager to manage and 
	 * coordinate functionality.
	 * @return listManager
	 */
	IndexableByIdListManager getIndexableListManager();

	/** update index.
	 */
	void updateIndex();
}
