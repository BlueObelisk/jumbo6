package org.xmlcml.cml.element;

import java.util.List;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.StringAttribute;

/**
 * user-modifiable class supporting "ref", a pointer
 * to a CML object. default is to clone the object and then process it.
 * For that reason the referenced species should be pre-declared.
 */
public class RefAttribute extends StringAttribute {

    final static String NAME = "ref";
    /**
     * constructor.
     * 
     */
    public RefAttribute() {
        super(NAME);
    }

    /** constructor.
     * @param value
     */
    public RefAttribute(String value) {
        super(NAME);
        this.setCMLValue(value);
    }

    /**
     * constructor.
     * 
     * @param att
     * @exception CMLRuntimeException
     */
    public RefAttribute(Attribute att) throws CMLRuntimeException {
        super(att);
    }
    
    /** set value and process.
     * 
     * @param value
     * @exception CMLRuntimeException bad value
     */
    public void setCMLValue(String value) throws CMLRuntimeException {
        if (value == null) {
            throw new CMLRuntimeException("null ref attribute value");
        } else if (value.trim().equals(S_EMPTY)) {
            // seems to get called with empty string initially
            // this is a bug
        } else {
            super.setCMLValue(value);
        }
    }
    
    /** processes ref attribute on element.
     * 
     * @param element to process
     */
    public static void process(CMLElement element) {
        RefAttribute ref = (RefAttribute) element.getAttribute(RefAttribute.NAME);
        if (ref != null) {
            RefAttribute.process(element, ref);
        }
        List<CMLElement> childElems = element.getChildCMLElements();
        for (CMLElement child : childElems) {
            RefAttribute.process(child);
        }
    }
    
    /** process element with a ref attribute.
     * if the reference can be resolved copies the object and
     * replaces 'this' with the copy.
     * @param element to process
     * @param ref attribute
     * @throws CMLRuntimeException null element or bad reference
     */
    public static void process(CMLElement element, RefAttribute ref) throws CMLRuntimeException {
        if (element == null) {
            throw new CMLRuntimeException("Cannot process null ref attribute");
        }
        CMLElement oldest = element.getOldestCMLAncestor();
        if (oldest == element) {
            throw new CMLRuntimeException("Cannot reference elements from oldest ancestor");
        }
        String id = ref.getValue();
        List<CMLElement> elemList = oldest.getElementsById(id, true);
        if (elemList.size() == 0) {
            throw new CMLRuntimeException("Cannot find element: "+id);
        }
        if (elemList.size() > 1) {
            throw new CMLRuntimeException("Too many elements ("+elemList.size()+") with id: "+id);
        }
        CMLElement elemRef = elemList.get(0);
        CMLElement newElem = (CMLElement) elemRef.copy();
        // FIXME - use RefAttribute
        String idgen = element.getAttributeValue("idgen");
        if (idgen != null) {
            newElem.addAttribute(new Attribute("id", idgen));
        } else {
        }
        newElem.debug();
        // substitute all args
        // transfer any args from referring element
        CMLArg.transferArgs(element, newElem);
        CMLArg.substituteNameByValue(newElem);
        System.out.println("%%%%%%%%%%%%%%%%%%%%");
        newElem.debug();
        System.out.println("%%%%%%%%%%%%%%%%%%%%");
        CMLElement parent = (CMLElement) element.getParent();
        int idx = parent.indexOf(element);
        parent.insertChild(newElem, idx);
//        element.detach();
    }

}
