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
		FragmentListTool fragmentListTool = (FragmentListTool) fragmentList.getTool();
		if (fragmentListTool == null) {
			fragmentListTool = new FragmentListTool(fragmentList);
			fragmentList.setTool(fragmentListTool);
		}
		return fragmentListTool;
	}

	public void updateIndex() {
//		// no-op
//		throw new RuntimeException("NYI");
	}
	
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