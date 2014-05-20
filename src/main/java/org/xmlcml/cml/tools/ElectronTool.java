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
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLElectron;

/**
 * tool for managing electron
 *
 * @author pmr
 *
 */
public class ElectronTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ElectronTool.class.getName());
	public static final String LONE_ELECTRONS = "loneElectrons";
	public static final String ELECTRONS = "electrons";

	CMLElectron electron = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public ElectronTool(CMLElectron electron) throws RuntimeException {
		init();
		this.electron = electron;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLElectron getElectron() {
		return this.electron;
	}

    
	/** gets ElectronTool associated with electron.
	 * if null creates one and sets it in electron
	 * @param electron
	 * @return tool
	 */
	public static ElectronTool getOrCreateTool(CMLElectron electron) {
		ElectronTool electronTool = null;
		if (electron != null) {
			electronTool = (ElectronTool) electron.getTool();
			if (electronTool == null) {
				electronTool = new ElectronTool(electron);
				electron.setTool(electronTool);
			}
		}
		return electronTool;
	}

	public static int getElectronCount(CMLElement element, String electronType) {
		int count = 0;
		if (element != null) {
			String loneElectronCountS = element.getCMLXAttribute(electronType);
			if (loneElectronCountS != null && !loneElectronCountS.equals("")) {
				count = Math.max(0, Integer.parseInt(loneElectronCountS));
			}
		}
		return count;
	}

};