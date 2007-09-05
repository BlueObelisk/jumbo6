package org.xmlcml.cml.tools;

import java.util.logging.Logger;

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
     */
    public FragmentListTool(CMLFragmentList fragmentList) {
        this.fragmentList = fragmentList;
    }

    /**
     * make fragmentList tool from a fragmentList.
     * 
     * @param fragment
     * @return the tool
     */
    static FragmentListTool createFragmentListTool(CMLFragmentList fragmentList) {
        return new FragmentListTool(fragmentList);
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