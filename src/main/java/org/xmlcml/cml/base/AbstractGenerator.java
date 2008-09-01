/**
 * 
 */
package org.xmlcml.cml.base;

import java.io.File;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * @author pm286
 *
 */
public abstract class AbstractGenerator implements CMLConstants {
	private static Logger LOG = Logger.getLogger(AbstractGenerator.class);
	protected List<String> nameList;
	protected SchemaManager schemaManager;
	protected Element schema;

	/** read and assemble.
	 * 
	 * @param dir
	 * @return root element
	 * @throws Exception
	 */
	public Element readAndAssembleSchemaComponents(String dir) throws Exception {
		schema = null;
		if (dir == null) {
			throw new CMLRuntimeException("null schema directory");
		}
		schema = new Element("schema", XSD_NS);
		File file = new File(dir);
		if (!file.isDirectory()) {
			throw new CMLRuntimeException("missing or bad schema directory: "+dir);
		}
		
		File[] files = file.listFiles();
		for (File f : files) {
			if (!f.toString().endsWith(XSD_SUFF)) {
				continue;
			}
			Document doc = new Builder().build(f);
			Element elem = doc.getRootElement();
			doc.replaceChild(elem, new Element("s_dummy"));
			schema.appendChild(elem);
		}
		LOG.debug("CHILD "+schema.getChildCount());
		
		return schema;
	}

	/**
	 * @return the schema
	 */
	public Element getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(Element schema) {
		this.schema = schema;
	}

	public String getValue() {
		return null;
	}

}
