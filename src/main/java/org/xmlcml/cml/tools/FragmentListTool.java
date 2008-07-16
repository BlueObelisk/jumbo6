package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLFragmentList;

/**
 * additional tools for fragmentList. not fully developed
 * 
 * @author pmr
 * 
 */
public class FragmentListTool extends AbstractTool {

    Logger logger = Logger.getLogger(FragmentTool.class.getName());

    CMLFragmentList fragmentList;

    /**
     * constructor
     * @param fragmentList 
     * @deprecated use getOrCreateTool
     */
    public FragmentListTool(CMLFragmentList fragmentList) {
        this.fragmentList = fragmentList;
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

    /**
     * get fragmentList.
     * 
     * @return the fragmentList
     */
    public CMLFragmentList getFragmentList() {
        return fragmentList;
    }


}