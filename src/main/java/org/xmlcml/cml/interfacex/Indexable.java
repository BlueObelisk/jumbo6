package org.xmlcml.cml.interfacex;


/**
 * catalogable object
 * retrievable from a catalog.
 * still being worked out
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface Indexable {
//	public enum Type {
//	/** fragment */
//	FRAGMENT(ICMLFragment.TAGX),
//	/** molecule */
//	MOLECULE(ICMLMolecule.TAGX);
//	/** peak */
//	PEAK(ICMLPeak.TAGX);
//		public String value;
//		private Type(String s) {
//			this.value = s;
//		}
//	}
	
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
