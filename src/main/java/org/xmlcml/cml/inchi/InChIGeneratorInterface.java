package org.xmlcml.cml.inchi;

public interface InChIGeneratorInterface {

	/**
	 * did generation succeed?
	 * @return true if INCHI_RET.OKAY returned in JNIInchi
	 */
	boolean isOK();
	
	/** relays any message fromJNInchi
	 * 
	 * @return message (not controlled in JUMBO)
	 */
	String getMessage();
	
	/** the satisfactorily generated InChI.
	 * 
	 * @return InChI
	 */
	String getInchi();
	
}
