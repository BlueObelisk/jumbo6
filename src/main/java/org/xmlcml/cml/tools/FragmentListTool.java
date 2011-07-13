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
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;

/**
 * tool for managing fragmentLists
 *
 * @author pmr
 *
 */
public class FragmentListTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(FragmentListTool.class.getName());

	CMLFragmentList fragmentList = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public FragmentListTool(CMLFragmentList fragmentList) throws RuntimeException {
		init();
		this.fragmentList = fragmentList;
	}


	void init() {
	}


	/**
	 * get fragmentList.
	 *
	 * @return the fragmentList or null
	 */
	public CMLFragmentList getFragmentList() {
		return this.fragmentList;
	}

    
	/** gets FragmentListTool associated with fragmentList.
	 * if null creates one and sets it in fragmentList
	 * @param fragmentList
	 * @return tool
	 */
	public static FragmentListTool getOrCreateTool(CMLFragmentList fragmentList) {
		FragmentListTool fragmentListTool = null;
		if (fragmentList != null) {
			fragmentListTool = (FragmentListTool) fragmentList.getTool();
			if (fragmentListTool == null) {
				fragmentListTool = new FragmentListTool(fragmentList);
				fragmentList.setTool(fragmentListTool);
			}
		}
		return fragmentListTool;
	}

	@Deprecated
	public void updateIndex() {
//		// no-op
//		throw new RuntimeException("NYI");
	}
	
	@Deprecated
	public void addFragment(CMLFragment fragment) {
		// FIXME
		throw new RuntimeException("NYI");
	}
	public CMLFragment getFragmentById(String id) {
		CMLFragment fragment = null;
		if (id != null) {
			for (CMLFragment frag : fragmentList.getFragmentElements()) {
				if (id.equals(frag.getId())) {
					fragment = frag;
					break;
				}
			}
		}
		return fragment;
	}

};