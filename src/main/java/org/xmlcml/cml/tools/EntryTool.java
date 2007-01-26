package org.xmlcml.cml.tools;

import java.util.List;
import java.util.logging.Logger;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.euclid.Util;

/** additional tools for entry. not fully developed
 * 
 * @author pmr
 * 
 */
public class EntryTool extends AbstractTool {

    Logger logger = Logger.getLogger(EntryTool.class.getName());
    CMLEntry entry;

	/**
	 * @param entry the entry to set
	 */
	public void setEntry(CMLEntry entry) {
		this.entry = entry;
	}

	/** constructor
     * 
     * @param entry
     */
    public EntryTool(CMLEntry entry) {
        this.setEntry(entry);
    }

    /** make entry tool from a entry.
     * 
     * @param entry
     * @return the tool
     */
    static EntryTool createEntryTool(CMLEntry entry) {
        return new EntryTool(entry);
    }

    /**
     * get entry.
     * 
     * @return the entry
     */
    public CMLEntry getEntry() {
        return entry;
    }

    void validate(CMLElement element) {
    	if (element instanceof CMLScalar) {
    		validate((CMLScalar) element);
    	} else if (element instanceof CMLArray) {
    		validate((CMLArray) element);
    	} else if (element instanceof CMLFormula) {
    		validate((CMLFormula) element);
    	} else if (element instanceof CMLTable) {
    		validate((CMLTable) element);
    	} else {
    		System.out.println("Cannot validate: "+element.getClass());
    	}
    }
    
    private void validate(CMLScalar scalar) {
    	String entryDataType = entry.getDataType();
    	String scalarDataType = scalar.getDataType();
    	validate(scalar, entryDataType, scalarDataType);
    	validateValue(scalarDataType, scalar.getDictRef(), scalar.getValue());
    }
    
    private void validateValue(String dataType, String name, String value) {
    	if (XSD_DOUBLE.equals(dataType)) {
    		if (!Util.isFloat(value)) {
	    		throw new CMLRuntimeException(name+
    				": expected number, found: "+value);
    		}
    	}
    }
    
    private void validate(CMLArray array) {
    	String entryDataType = entry.getDataType();
    	String scalarDataType = array.getDataType();
    	validate(array, entryDataType, scalarDataType);
    	
    }
    
    private void validate(CMLFormula formula) {
//    	String entryDataType = entry.getDataType();
//    	String scalarDataType = array.getDataType();
//    	validate(array, entryDataType, scalarDataType);
    }
    
    private void validate(CMLTable table) {
//    	String entryDataType = entry.getDataType();
//    	String scalarDataType = array.getDataType();
//    	validate(array, entryDataType, scalarDataType);
    }
    
    private void validate(CMLElement element, String entryDataType, String dataDataType) {
    	String id = element.getAttributeValue("id");
    	String dictRef = element.getAttributeValue("dictRef");
    	if (dataDataType == null) {
    		if (entryDataType == null || entryDataType.equals(XSD_STRING)) {
    			// OK
    		} else {
    			throw new CMLRuntimeException("data for ("+id+") must have data type");
    		}
    	} else {
    		if (!dataDataType.equals(entryDataType)) {
    			throw new CMLRuntimeException(id+"/"+dictRef+": entry ("+entryDataType+") and data {"+dataDataType+"} are of different types");
    		}
    	}
    }
}
