package org.xmlcml.cml.tools;



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.main.CMLMetadata;

/**
 * tool for managing metadata
 *
 * @author pmr
 *
 */
public class MetadataTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(MetadataTool.class.getName());

	CMLMetadata metadata = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public MetadataTool(CMLMetadata metadata) throws RuntimeException {
		init();
		this.metadata = metadata;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLMetadata getMetadata() {
		return this.metadata;
	}

    
	/** gets MetadataTool associated with metadata.
	 * if null creates one and sets it in metadata
	 * @param metadata
	 * @return tool
	 */
	public static MetadataTool getOrCreateTool(CMLMetadata metadata) {
		MetadataTool metadataTool = (MetadataTool) metadata.getTool();
		if (metadataTool == null) {
			metadataTool = new MetadataTool(metadata);
			metadata.setTool(metadataTool);
		}
		return metadataTool;
	}


};