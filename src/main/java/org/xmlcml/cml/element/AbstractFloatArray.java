package org.xmlcml.cml.element;


import nu.xom.Attribute;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.IntSTAttribute;
import org.xmlcml.cml.base.StringSTAttribute;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractFloatArray extends CMLElement {
    /** local name*/
    public final static String TAG = "floatArray";
    /** constructor. */    public AbstractFloatArray() {
        super("floatArray");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractFloatArray(AbstractFloatArray old) {
        super((CMLElement) old);
    }
// attribute:   builtin

    /** cache */
    StringSTAttribute _att_builtin = null;
    /** builtin children.
    * CML1-only - now deprecated.
    * @return CMLAttribute
    */
    public CMLAttribute getBuiltinAttribute() {
        return (CMLAttribute) getAttribute("builtin");
    }
    /** builtin children.
    * CML1-only - now deprecated.
    * @return String
    */
    public String getBuiltin() {
        StringSTAttribute att = (StringSTAttribute) this.getBuiltinAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** builtin children.
    * CML1-only - now deprecated.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setBuiltin(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_builtin == null) {
            _att_builtin = (StringSTAttribute) attributeFactory.getAttribute("builtin", "floatArray");
            if (_att_builtin == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : builtin probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_builtin);
        super.addRemove(att, value);
    }
// attribute:   convention

    /** cache */
    StringSTAttribute _att_convention = null;
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @return CMLAttribute
    */
    public CMLAttribute getConventionAttribute() {
        return (CMLAttribute) getAttribute("convention");
    }
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @return String
    */
    public String getConvention() {
        StringSTAttribute att = (StringSTAttribute) this.getConventionAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setConvention(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_convention == null) {
            _att_convention = (StringSTAttribute) attributeFactory.getAttribute("convention", "floatArray");
            if (_att_convention == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : convention probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_convention);
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
            _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "floatArray");
            if (_att_dictref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DictRefAttribute(_att_dictref);
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "floatArray");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
    }
// attribute:   title

    /** cache */
    StringSTAttribute _att_title = null;
    /** A title on an element.
    * No controlled value.
    * @return CMLAttribute
    */
    public CMLAttribute getTitleAttribute() {
        return (CMLAttribute) getAttribute("title");
    }
    /** A title on an element.
    * No controlled value.
    * @return String
    */
    public String getTitle() {
        StringSTAttribute att = (StringSTAttribute) this.getTitleAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A title on an element.
    * No controlled value.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setTitle(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_title == null) {
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "floatArray");
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_title);
        super.addRemove(att, value);
    }
// attribute:   min

    /** cache */
    StringSTAttribute _att_min = null;
    /** The minimum value allowed for an element or attribute.
    * 
    * @return CMLAttribute
    */
    public CMLAttribute getMinAttribute() {
        return (CMLAttribute) getAttribute("min");
    }
    /** The minimum value allowed for an element or attribute.
    * 
    * @return String
    */
    public String getMin() {
        StringSTAttribute att = (StringSTAttribute) this.getMinAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The minimum value allowed for an element or attribute.
    * 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMin(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_min == null) {
            _att_min = (StringSTAttribute) attributeFactory.getAttribute("min", "floatArray");
            if (_att_min == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : min probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_min);
        super.addRemove(att, value);
    }
// attribute:   max

    /** cache */
    StringSTAttribute _att_max = null;
    /** Maximum value allowed for an element or attribute.
    * 
    * @return CMLAttribute
    */
    public CMLAttribute getMaxAttribute() {
        return (CMLAttribute) getAttribute("max");
    }
    /** Maximum value allowed for an element or attribute.
    * 
    * @return String
    */
    public String getMax() {
        StringSTAttribute att = (StringSTAttribute) this.getMaxAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Maximum value allowed for an element or attribute.
    * 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMax(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_max == null) {
            _att_max = (StringSTAttribute) attributeFactory.getAttribute("max", "floatArray");
            if (_att_max == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : max probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_max);
        super.addRemove(att, value);
    }
// attribute:   size

    /** cache */
    IntSTAttribute _att_size = null;
    /** The size of an array or matrix.
    * No description
    * @return CMLAttribute
    */
    public CMLAttribute getSizeAttribute() {
        return (CMLAttribute) getAttribute("size");
    }
    /** The size of an array or matrix.
    * No description
    * @return int
    */
    public int getSize() {
        IntSTAttribute att = (IntSTAttribute) this.getSizeAttribute();
        if (att == null) {
            throw new CMLRuntimeException("int attribute is unset: size");
        }
        return att.getInt();
    }
    /** The size of an array or matrix.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSize(String value) throws CMLRuntimeException {
        IntSTAttribute att = null;
        if (_att_size == null) {
            _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "floatArray");
            if (_att_size == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IntSTAttribute(_att_size);
        super.addRemove(att, value);
    }
    /** The size of an array or matrix.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSize(int value) throws CMLRuntimeException {
        if (_att_size == null) {
            _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "floatArray");
           if (_att_size == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName ");
            }
        }
        IntSTAttribute att = new IntSTAttribute(_att_size);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   units

    /** cache */
    UnitsAttribute _att_units = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getUnitsAttribute() {
        return (CMLAttribute) getAttribute("units");
    }
    /** null
    * @return String
    */
    public String getUnits() {
        UnitsAttribute att = (UnitsAttribute) this.getUnitsAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setUnits(String value) throws CMLRuntimeException {
        UnitsAttribute att = null;
        if (_att_units == null) {
            _att_units = (UnitsAttribute) attributeFactory.getAttribute("units", "floatArray");
            if (_att_units == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : units probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new UnitsAttribute(_att_units);
        super.addRemove(att, value);
    }
// attribute:   unitsRef

    /** cache */
    StringSTAttribute _att_unitsref = null;
    /** unitsRef attribute on CML1 elements.
    * CML1-only - now deprecated.
    * @return CMLAttribute
    */
    public CMLAttribute getUnitsRefAttribute() {
        return (CMLAttribute) getAttribute("unitsRef");
    }
    /** unitsRef attribute on CML1 elements.
    * CML1-only - now deprecated.
    * @return String
    */
    public String getUnitsRef() {
        StringSTAttribute att = (StringSTAttribute) this.getUnitsRefAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** unitsRef attribute on CML1 elements.
    * CML1-only - now deprecated.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setUnitsRef(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_unitsref == null) {
            _att_unitsref = (StringSTAttribute) attributeFactory.getAttribute("unitsRef", "floatArray");
            if (_att_unitsref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitsRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_unitsref);
        super.addRemove(att, value);
    }
    StringSTAttribute _xmlContent;
    /** 
    * 
    * @return String
    */
    public String getXMLContent() {
        String content = this.getValue();
        if (_xmlContent == null) {
            _xmlContent = new StringSTAttribute("_xmlContent");
        }
        _xmlContent.setCMLValue(content);
        return _xmlContent.getString();
    }
    /** 
    * 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setXMLContent(String value) throws CMLRuntimeException {
        if (_xmlContent == null) {
            _xmlContent = new StringSTAttribute("_xmlContent");
        }
        _xmlContent.setCMLValue(value);
        String attval = _xmlContent.getValue();
        this.removeChildren();
        this.appendChild(attval);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("builtin")) {
            setBuiltin(value);
        } else if (name.equals("convention")) {
            setConvention(value);
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("min")) {
            setMin(value);
        } else if (name.equals("max")) {
            setMax(value);
        } else if (name.equals("size")) {
            setSize(value);
        } else if (name.equals("units")) {
            setUnits(value);
        } else if (name.equals("unitsRef")) {
            setUnitsRef(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
