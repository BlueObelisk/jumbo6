package org.xmlcml.cml.attribute;

import nu.xom.Attribute;
import nu.xom.Element;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.StringSTAttribute;
import org.xmlcml.cml.element.CMLArg;

/**
 * user-modifiable class supporting "repeat". Takes the value:
 * repeat='ij 2 10' (argName, startVal, endVal)
 */
public class RepeatAttribute extends StringSTAttribute {

	/** */
    public final static String NAME = "repeat";
    String argName = "null";
    int start = 0;
    int end = 0;
    /**
     * constructor.
     * 
     */
    public RepeatAttribute() {
        super(NAME);
    }

    /** constructor.
     * @param value
     */
    public RepeatAttribute(String value) {
        super(NAME);
        this.setCMLValue(value);
    }

    /**
     * constructor.
     * 
     * @param att
     * @exception CMLRuntimeException
     */
    public RepeatAttribute(Attribute att) throws CMLRuntimeException {
        super(att);
    }
    
    /** set value and process.
     * 
     * @param value
     * @exception CMLRuntimeException bad value
     */
    public void setCMLValue(String value) throws CMLRuntimeException {
        if (value == null) {
            throw new CMLRuntimeException("null repeat attribute value");
        } else if (value.trim().equals(S_EMPTY)) {
            // seems to get called with empty string initially
            // this is a bug
        } else {
            super.setCMLValue(value);
            String[] values = value.trim().split("\\s+");
            if (values.length != 3) {
                throw new CMLRuntimeException("repeat must have 3 whitespaced values: ("+value+S_RBRAK);
            }
            argName = values[0];
            try {
                start = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                throw new CMLRuntimeException("Cannot parse start value in: "+value+" ("+e+S_RBRAK);
            }
            try {
                end = Integer.parseInt(values[2]);
            } catch (NumberFormatException e) {
                throw new CMLRuntimeException("Cannot parse end value in: "+value+" ("+e+S_RBRAK);
            }
    //        end may be <=, ==, or >= start
        }
    }
    
    void resetAttribute() {
        setValue(argName+S_SPACE+start+S_SPACE+end);
//        Element parent = (Element) this.getParent();
//        if (parent != null) {
            
//        }
    }
    /** set argName.
     * 
     * @param arg
     */
    public void setArgName(String arg) {
        this.argName = arg;
        resetAttribute();
    }
    
    /** get argName.
     * 
     * @return argName
     */
    public String getArgName() {
        return argName;
    }

    /** set start value.
     * 
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
        resetAttribute();
    }

    /** get start value.
     * 
     * @return start value
     */
    public int getStart() {
        return start;
    }

    /** set end value,
     * 
     * @param end
     */
    public void setEnd(int end) {
        this.end = end;
        resetAttribute();
    }

    /** get end value.
     * 
     * @return end value
     */
    public int getEnd() {
        return end;
    }

    /** processes repeat attribute on element.
     * 
     * @param element to process
     */
    public static void process(CMLElement element) {
        RepeatAttribute repeat = (RepeatAttribute) element.getAttribute(RepeatAttribute.NAME);
        if (repeat != null) {
            // this expands the repeat values
            RepeatAttribute.process(element, repeat);
        }
    }
    
    /** process element with a repeat attribute.
     * clones the element and adds serial number as arg to each
     * @param element to process
     * @param repeat attribute
     * @throws CMLRuntimeException null element or bad attribute
     */
    public static void process(CMLElement element, RepeatAttribute repeat) throws CMLRuntimeException {
        if (element == null) {
            throw new CMLRuntimeException("Cannot process null repeat attribute");
        }
        Element parent = (Element) element.getParent();
        if (parent == null) {
            throw new CMLRuntimeException("Cannot process repeat attribute without parent");
        }
        int childIdx = parent.indexOf(element); 
        String argName = repeat.getArgName();
        for (int i = repeat.getStart(); i <= repeat.getEnd(); i++) {
            CMLElement clone = (CMLElement) element.copy();
            parent.insertChild(clone, childIdx + i);
            // add index as arg
            CMLArg arg = new CMLArg(argName, i);
            // delete old attribute
            arg.setDelete("true");
            arg.setSubstitute(".//@*");
            CMLArg.addArg(clone, arg, 0);
            // remove repeat attribute so it doesn't get reprocessed
            Attribute newRepeat = clone.getAttribute(RepeatAttribute.NAME);
            newRepeat.detach();
//            newRepeat.setLocalName("oldRepeat");
        }
        // remove the original element
        element.detach();
    }

}
