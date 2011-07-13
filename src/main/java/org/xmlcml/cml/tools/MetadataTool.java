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

package org.xmlcml.cml.tools;



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLMetadata;

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
		MetadataTool metadataTool = null;
		if (metadata != null) {
			metadataTool = (MetadataTool) metadata.getTool();
			if (metadataTool == null) {
				metadataTool = new MetadataTool(metadata);
				metadata.setTool(metadataTool);
			}
		}
		return metadataTool;
	}


};