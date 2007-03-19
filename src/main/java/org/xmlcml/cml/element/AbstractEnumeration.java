package org.xmlcml.cml.element;


import nu.xom.Attribute;
import nu.xom.Elements;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.StringSTAttribute;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractEnumeration extends CMLElement {
    /** local name*/
    public final static String TAG = "enumeration";
    /** constructor. */    public AbstractEnumeration() {
        super("enumeration");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractEnumeration(AbstractEnumeration old) {
        super((CMLElement) old);
    }
// attribute:   value

    /** cache */
    StringSTAttribute _att_value = null;
    /** Value of a scalar object.
    * The value must be consistent with the dataType of the object.
    * @return CMLAttribute
    */
    public CMLAttribute getCMLValueAttribute() {
        return (CMLAttribute) getAttribute("value");
    }
    /** Value of a scalar object.
    * The value must be consistent with the dataType of the object.
    * @return String
    */
    public String getCMLValue() {
        StringSTAttribute att = (StringSTAttribute) this.getCMLValueAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Value of a scalar object.
    * The value must be consistent with the dataType of the object.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setCMLValue(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_value == null) {
            _att_value = (StringSTAttribute) attributeFactory.getAttribute("value", "enumeration");
            if (_att_value == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : value probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_value);
        super.addRemove(att, value);
    }
// attribute:   id

    /** cache */
    IdAttribute _att_id = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getIdAttribute() {
        return (CMLAttribute) getAttribute("id");
    }
    /** null
    * @return String
    */
    public String getId() {
        IdAttribute att = (IdAttribute) this.getIdAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setId(String value) throws CMLRuntimeException {
        IdAttribute att = null;
        if (_att_id == null) {
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "enumeration");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
    }
// attribute:   dictRef

    /** cache */
    DictRefAttribute _att_dictref = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getDictRefAttribute() {
        return (CMLAttribute) getAttribute("dictRef");
    }
    /** null
    * @return String
    */
    public String getDictRef() {
        DictRefAttribute att = (DictRefAttribute) this.getDictRefAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDictRef(String value) throws CMLRuntimeException {
        DictRefAttribute att = null;
        if (_att_dictref == null) {
            _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "enumeration");
            if (_att_dictref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DictRefAttribute(_att_dictref);
        super.addRemove(att, value);
    }
// attribute:   default

    /** cache */
    StringSTAttribute _att_default = null;
    /** default value in an enumeration.
    * A non-whitespace string (value is irrelevant) indicates that the content of this enumeration is the default value (usually of a scalar). It is an error to have more than one default. If the scalar in an instance document has no value (i.e. is empty or contains only whitespace) its value is given by the default. If the scalar in the instance is empty and no enumerations have a default attribute, an application may throw an error.
    * @return CMLAttribute
    */
    public CMLAttribute getDefaultAttribute() {
        return (CMLAttribute) getAttribute("default");
    }
    /** default value in an enumeration.
    * A non-whitespace string (value is irrelevant) indicates that the content of this enumeration is the default value (usually of a scalar). It is an error to have more than one default. If the scalar in an instance document has no value (i.e. is empty or contains only whitespace) its value is given by the default. If the scalar in the instance is empty and no enumerations have a default attribute, an application may throw an error.
    * @return String
    */
    public String getDefault() {
        StringSTAttribute att = (StringSTAttribute) this.getDefaultAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** default value in an enumeration.
    * A non-whitespace string (value is irrelevant) indicates that the content of this enumeration is the default value (usually of a scalar). It is an error to have more than one default. If the scalar in an instance document has no value (i.e. is empty or contains only whitespace) its value is given by the default. If the scalar in the instance is empty and no enumerations have a default attribute, an application may throw an error.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDefault(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_default == null) {
            _att_default = (StringSTAttribute) attributeFactory.getAttribute("default", "enumeration");
            if (_att_default == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : default probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_default);
        super.addRemove(att, value);
    }
// element:   annotation

    /** default value in an enumeration.
    * A non-whitespace string (value is irrelevant) indicates that the content of this enumeration is the default value (usually of a scalar). It is an error to have more than one default. If the scalar in an instance document has no value (i.e. is empty or contains only whitespace) its value is given by the default. If the scalar in the instance is empty and no enumerations have a default attribute, an application may throw an error.
    * @param annotation child to add
    */
    public void addAnnotation(AbstractAnnotation annotation) {
        annotation.detach();
        this.appendChild(annotation);
    }
    /** default value in an enumeration.
    * A non-whitespace string (value is irrelevant) indicates that the content of this enumeration is the default value (usually of a scalar). It is an error to have more than one default. If the scalar in an instance document has no value (i.e. is empty or contains only whitespace) its value is given by the default. If the scalar in the instance is empty and no enumerations have a default attribute, an application may throw an error.
    * @return CMLElements<CMLAnnotation>
    */
    public CMLElements<CMLAnnotation> getAnnotationElements() {
        Elements elements = this.getChildElements("annotation", CML_NS);
        return new CMLElements<CMLAnnotation>(elements);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("value")) {
            setCMLValue(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("default")) {
            setDefault(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
