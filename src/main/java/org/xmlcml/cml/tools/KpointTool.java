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
import org.xmlcml.cml.element.CMLKpoint;

/**
 * tool to support atom set. not sure if useful
 * 
 * @author pmr
 * 
 */
public class KpointTool extends AbstractTool {
	private static Logger LOG = Logger.getLogger(KpointTool.class);
	
	Logger logger = Logger.getLogger(KpointTool.class.getName());

	@SuppressWarnings("unused")
	private CMLKpoint kpoint;

	/**
	 * constructor.
	 * 
	 * @param kpoint
	 * @deprecated use getOrCreateTool
	 */
	public KpointTool(CMLKpoint kpoint) {
		if (kpoint == null) {
			throw new RuntimeException("Null kpoint");
		}
		this.kpoint = kpoint;
		LOG.trace("constructor");
	}

	/** gets KpointTool associated with kpoint.
	 * if null creates one and sets it in kpoint
	 * @param kpoint
	 * @return tool
	 */
	public static KpointTool getOrCreateTool(CMLKpoint kpoint) {
		KpointTool kpointTool = null;
		if (kpoint != null) {
			kpointTool = (KpointTool) kpoint.getTool();
			if (kpointTool == null) {
				kpointTool = new KpointTool(kpoint);
				kpoint.setTool(kpointTool);
			}
		}
		return kpointTool;
	}

}
