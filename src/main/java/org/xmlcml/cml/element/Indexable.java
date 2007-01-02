package org.xmlcml.cml.element;


/**
 * catalogable object
 * retrievable from a catalog.
 * still being worked out
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface Indexable {
	public enum Type {
		/** fragment */
		FRAGMENT(CMLFragment.TAG),
		/** molecule */
		MOLECULE(CMLMolecule.TAG);
		public String value;
		private Type(String s) {
			this.value = s;
		}
	}
	
	/** ensure integrity of list and children.
	 * @return class of parent
	 */
	Class getIndexableListClass();

	/** get id.
	 * @return id
	 */
	String getId();

	/** get ref.
	 * @return ref
	 */
	String getRef();
}
