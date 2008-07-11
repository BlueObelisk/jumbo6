package org.xmlcml.cml.element;


import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.RuntimeException;
import org.xmlcml.cml.base.StringSTAttribute;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractAppinfo extends CMLElement {
    /** local name*/
    public final static String TAG = "appinfo";
    /** constructor. */    public AbstractAppinfo() {
        super("appinfo");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractAppinfo(AbstractAppinfo old) {
        super((CMLElement) old);
    }
// attribute:   role

    /** cache */
    StringSTAttribute _att_role = null;
    /** Role of the object.
    * How the object functions or its position in the architecture. No controlled vocabulary.
    * @return CMLAttribute
    */
    public CMLAttribute getRoleAttribute() {
        return (CMLAttribute) getAttribute("role");
    }
    /** Role of the object.
    * How the object functions or its position in the architecture. No controlled vocabulary.
    * @return String
    */
    public String getRole() {
        StringSTAttribute att = (StringSTAttribute) this.getRoleAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Role of the object.
    * How the object functions or its position in the architecture. No controlled vocabulary.
    * @param value title value
    * @throws RuntimeException attribute wrong value/type
    */
    public void setRole(String value) throws RuntimeException {
        StringSTAttribute att = null;
        if (_att_role == null) {
            _att_role = (StringSTAttribute) attributeFactory.getAttribute("role", "appinfo");
            if (_att_role == null) {
                throw new RuntimeException("BUG: cannot process attributeGroupName : role probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_role);
        super.addRemove(att, value);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("role")) {
            setRole(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
