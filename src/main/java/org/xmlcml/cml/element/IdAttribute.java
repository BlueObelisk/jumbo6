package org.xmlcml.cml.element;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.StringAttribute;

/**
 * user-modifiable class supporting "id". 
 */
public class IdAttribute extends StringAttribute {

    public final static String NAME = "id";
    String argName = "null";
    int start = 0;
    int end = 0;
    /**
     * constructor.
     * 
     */
    public IdAttribute() {
        super(NAME);
    }

    /** constructor.
     * @param value
     */
    public IdAttribute(String value) {
        super(NAME);
        this.setCMLValue(value);
    }

    /**
     * constructor from element with IdAttribute
     * 
     * @param att
     * @exception CMLRuntimeException
     */
    public IdAttribute(Attribute att) throws CMLRuntimeException {
        super(att);
    }
    
    /** set value and process.
     * 
     * @param value
     * @exception CMLRuntimeException bad value
     */
    public void setCMLValue(String value) throws CMLRuntimeException {
        if (value == null) {
            throw new CMLRuntimeException("null IdAttribute value");
        } else if (value.trim().equals(S_EMPTY)) {
            // seems to get called with empty string initially
            // this is a bug
        } else {
            super.setCMLValue(value);
        }
    }
    
}
