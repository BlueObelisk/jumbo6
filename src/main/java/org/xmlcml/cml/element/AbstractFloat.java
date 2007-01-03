// CONTENT of type :xsd:double
package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import java.util.HashMap;
import java.util.Map;

/** CML-1 dataType DEPRECATED.
*
* 
* 
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractFloat extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "float";
    /** content pseudoattribute*/
    protected CMLAttribute _xmlContent;
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractFloat() {
        super("float");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractFloat to copy

    */

    public AbstractFloat(AbstractFloat old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("builtin", "builtin");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("min", "min");
        attributeGroupNameTable.put("max", "max");
        attributeGroupNameTable.put("units", "units");
        attributeGroupNameTable.put("unitsRef", "unitsRef");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
    }
    /** builtin children.
    *
    * CML1-only - now deprecated.

    * @return CMLAttribute
    */
    public CMLAttribute getBuiltinAttribute() {
        return (CMLAttribute) getAttribute("builtin");
    }
    /** builtin children.
    *
    * CML1-only - now deprecated.

    * @return String
    */
    public String getBuiltin() {
        CMLAttribute _att_builtin = (CMLAttribute) getAttribute("builtin");
        if (_att_builtin == null) {
            return null;
        }
        return ((StringAttribute)_att_builtin).getString();
    }
    /** builtin children.
    *
    * CML1-only - now deprecated.

    * @param value builtin value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setBuiltin(String value) throws CMLRuntimeException {
            CMLAttribute _att_builtin = null;
            try {
        		_att_builtin = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("builtin"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_builtin == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : builtin; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_builtin);
        ((StringAttribute)_att_builtin).setCMLValue(value);
    }
    /** A reference to a convention.
    *
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *  if necessary by an explicit convention.
    *  It may be useful to create conventions with namespaces (e.g. iupac:name).
    *  Use of convention will normally require non-STMML semantics, and should be used with
    *  caution. We would expect that conventions prefixed with "ISO" would be useful,
    *  such as ISO8601 for dateTimes.
    *  There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * --type info--
    * 
    * A reference to an existing object.
    * The semantic of reference are normally identical to 
    * an idType (e.g. "a123b"). Howevere there are some cases where compound references
    *  are required, such as "a123b:pq456". It is likely that this will be superseded at
    *  by RDF or Xpointer, but as long as we have non-uniqueIds this is a problem
    * Pattern: ([A-Za-z_][A-Za-z0-9_\.\-]*:)?[A-Za-z_][A-Za-z0-9_\.\-]*

    * @return CMLAttribute
    */
    public CMLAttribute getConventionAttribute() {
        return (CMLAttribute) getAttribute("convention");
    }
    /** A reference to a convention.
    *
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *  if necessary by an explicit convention.
    *  It may be useful to create conventions with namespaces (e.g. iupac:name).
    *  Use of convention will normally require non-STMML semantics, and should be used with
    *  caution. We would expect that conventions prefixed with "ISO" would be useful,
    *  such as ISO8601 for dateTimes.
    *  There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * --type info--
    * 
    * A reference to an existing object.
    * The semantic of reference are normally identical to 
    * an idType (e.g. "a123b"). Howevere there are some cases where compound references
    *  are required, such as "a123b:pq456". It is likely that this will be superseded at
    *  by RDF or Xpointer, but as long as we have non-uniqueIds this is a problem
    * Pattern: ([A-Za-z_][A-Za-z0-9_\.\-]*:)?[A-Za-z_][A-Za-z0-9_\.\-]*

    * @return String
    */
    public String getConvention() {
        CMLAttribute _att_convention = (CMLAttribute) getAttribute("convention");
        if (_att_convention == null) {
            return null;
        }
        return ((StringAttribute)_att_convention).getString();
    }
    /** A reference to a convention.
    *
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *  if necessary by an explicit convention.
    *  It may be useful to create conventions with namespaces (e.g. iupac:name).
    *  Use of convention will normally require non-STMML semantics, and should be used with
    *  caution. We would expect that conventions prefixed with "ISO" would be useful,
    *  such as ISO8601 for dateTimes.
    *  There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * --type info--
    * 
    * A reference to an existing object.
    * The semantic of reference are normally identical to 
    * an idType (e.g. "a123b"). Howevere there are some cases where compound references
    *  are required, such as "a123b:pq456". It is likely that this will be superseded at
    *  by RDF or Xpointer, but as long as we have non-uniqueIds this is a problem
    * Pattern: ([A-Za-z_][A-Za-z0-9_\.\-]*:)?[A-Za-z_][A-Za-z0-9_\.\-]*

    * @param value convention value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setConvention(String value) throws CMLRuntimeException {
            CMLAttribute _att_convention = null;
            try {
        		_att_convention = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("convention"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_convention == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : convention; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_convention);
        ((StringAttribute)_att_convention).setCMLValue(value);
    }
    /** A reference to a dictionary entry.
    *
    * Elements in data instances such as _scalar_ may have a dictRef attribute to point to an entry in a dictionary. To avoid excessive use of (mutable) filenames and URIs we recommend a namespace prefix, mapped to a namespace URI in the normal manner. In this case, of course, the namespace URI must point to a real XML document containing _entry_ elements and validated against STMML Schema.
    *  Where there is concern about the dictionary becoming separated from the document the dictionary entries can be physically included as part of the data instance and the normal XPointer addressing mechanism can be used.
    *  This attribute can also be used on _dictionary_ elements to define the namespace prefix
    * --type info--
    * 
    * An XML QName with required prefix.
    * 
    *  The namespace prefix must start with an alpha character
    *  and can only contain alphanumeric and '_'. The suffix can 
    * have characters from the XML ID specification 
    * (alphanumeric, '_', '.' and '-'
    * Pattern: [A-Za-z][A-Za-z0-9_]*:[A-Za-z][A-Za-z0-9_\.\-]*

    * @return CMLAttribute
    */
    public CMLAttribute getDictRefAttribute() {
        return (CMLAttribute) getAttribute("dictRef");
    }
    /** A reference to a dictionary entry.
    *
    * Elements in data instances such as _scalar_ may have a dictRef attribute to point to an entry in a dictionary. To avoid excessive use of (mutable) filenames and URIs we recommend a namespace prefix, mapped to a namespace URI in the normal manner. In this case, of course, the namespace URI must point to a real XML document containing _entry_ elements and validated against STMML Schema.
    *  Where there is concern about the dictionary becoming separated from the document the dictionary entries can be physically included as part of the data instance and the normal XPointer addressing mechanism can be used.
    *  This attribute can also be used on _dictionary_ elements to define the namespace prefix
    * --type info--
    * 
    * An XML QName with required prefix.
    * 
    *  The namespace prefix must start with an alpha character
    *  and can only contain alphanumeric and '_'. The suffix can 
    * have characters from the XML ID specification 
    * (alphanumeric, '_', '.' and '-'
    * Pattern: [A-Za-z][A-Za-z0-9_]*:[A-Za-z][A-Za-z0-9_\.\-]*

    * @return String
    */
    public String getDictRef() {
        CMLAttribute _att_dictRef = (CMLAttribute) getAttribute("dictRef");
        if (_att_dictRef == null) {
            return null;
        }
        return ((StringAttribute)_att_dictRef).getString();
    }
    /** A reference to a dictionary entry.
    *
    * Elements in data instances such as _scalar_ may have a dictRef attribute to point to an entry in a dictionary. To avoid excessive use of (mutable) filenames and URIs we recommend a namespace prefix, mapped to a namespace URI in the normal manner. In this case, of course, the namespace URI must point to a real XML document containing _entry_ elements and validated against STMML Schema.
    *  Where there is concern about the dictionary becoming separated from the document the dictionary entries can be physically included as part of the data instance and the normal XPointer addressing mechanism can be used.
    *  This attribute can also be used on _dictionary_ elements to define the namespace prefix
    * --type info--
    * 
    * An XML QName with required prefix.
    * 
    *  The namespace prefix must start with an alpha character
    *  and can only contain alphanumeric and '_'. The suffix can 
    * have characters from the XML ID specification 
    * (alphanumeric, '_', '.' and '-'
    * Pattern: [A-Za-z][A-Za-z0-9_]*:[A-Za-z][A-Za-z0-9_\.\-]*

    * @param value dictRef value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setDictRef(String value) throws CMLRuntimeException {
            CMLAttribute _att_dictRef = null;
            try {
        		_att_dictRef = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("dictRef"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_dictRef == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_dictRef);
        ((StringAttribute)_att_dictRef).setCMLValue(value);
    }
    /** A unique ID for an element.
    *
    * Id is used for machine identification of elements and
    *  in general should not have application semantics. It is similar to the XML ID type
    *  as containing only alphanumerics, '_', ',' and '-' and and must start with an
    *  alphabetic character. Ids are case sensitive. Ids should be unique within local scope,
    *  thus all atoms within a molecule should have unique ids, but separated molecules within a 
    * document (such as a published article) might have identical ids. Software
    *  should be able to search local scope (e.g. all atoms within a molecule). 
    * However this is under constant review.
    * --type info--
    * 
    * A unique ID for an element.
    * 
    *  This is not formally of type ID (an XML NAME which must start with a letter and contain only letters, digits and .-_:). It is recommended that IDs start with a letter, and contain no punctuation or whitespace. The function in XSLT will generate semantically void unique IDs.
    *  It is difficult to ensure uniqueness when documents are merged. We suggest
    *  namespacing IDs, perhaps using the containing elements as the base.
    *  Thus mol3:a1 could be a useful unique ID. 
    * However this is still experimental.
    * Pattern: [A-Za-z][A-Za-z0-9\.\-_]*

    * @return CMLAttribute
    */
    public CMLAttribute getIdAttribute() {
        return (CMLAttribute) getAttribute("id");
    }
    /** A unique ID for an element.
    *
    * Id is used for machine identification of elements and
    *  in general should not have application semantics. It is similar to the XML ID type
    *  as containing only alphanumerics, '_', ',' and '-' and and must start with an
    *  alphabetic character. Ids are case sensitive. Ids should be unique within local scope,
    *  thus all atoms within a molecule should have unique ids, but separated molecules within a 
    * document (such as a published article) might have identical ids. Software
    *  should be able to search local scope (e.g. all atoms within a molecule). 
    * However this is under constant review.
    * --type info--
    * 
    * A unique ID for an element.
    * 
    *  This is not formally of type ID (an XML NAME which must start with a letter and contain only letters, digits and .-_:). It is recommended that IDs start with a letter, and contain no punctuation or whitespace. The function in XSLT will generate semantically void unique IDs.
    *  It is difficult to ensure uniqueness when documents are merged. We suggest
    *  namespacing IDs, perhaps using the containing elements as the base.
    *  Thus mol3:a1 could be a useful unique ID. 
    * However this is still experimental.
    * Pattern: [A-Za-z][A-Za-z0-9\.\-_]*

    * @return String
    */
    public String getId() {
        CMLAttribute _att_id = (CMLAttribute) getAttribute("id");
        if (_att_id == null) {
            return null;
        }
        return ((StringAttribute)_att_id).getString();
    }
    /** A unique ID for an element.
    *
    * Id is used for machine identification of elements and
    *  in general should not have application semantics. It is similar to the XML ID type
    *  as containing only alphanumerics, '_', ',' and '-' and and must start with an
    *  alphabetic character. Ids are case sensitive. Ids should be unique within local scope,
    *  thus all atoms within a molecule should have unique ids, but separated molecules within a 
    * document (such as a published article) might have identical ids. Software
    *  should be able to search local scope (e.g. all atoms within a molecule). 
    * However this is under constant review.
    * --type info--
    * 
    * A unique ID for an element.
    * 
    *  This is not formally of type ID (an XML NAME which must start with a letter and contain only letters, digits and .-_:). It is recommended that IDs start with a letter, and contain no punctuation or whitespace. The function in XSLT will generate semantically void unique IDs.
    *  It is difficult to ensure uniqueness when documents are merged. We suggest
    *  namespacing IDs, perhaps using the containing elements as the base.
    *  Thus mol3:a1 could be a useful unique ID. 
    * However this is still experimental.
    * Pattern: [A-Za-z][A-Za-z0-9\.\-_]*

    * @param value id value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setId(String value) throws CMLRuntimeException {
            CMLAttribute _att_id = null;
            try {
        		_att_id = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("id"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_id);
        ((StringAttribute)_att_id).setCMLValue(value);
    }
    /** A title on an element.
    *
    * No controlled value.

    * @return CMLAttribute
    */
    public CMLAttribute getTitleAttribute() {
        return (CMLAttribute) getAttribute("title");
    }
    /** A title on an element.
    *
    * No controlled value.

    * @return String
    */
    public String getTitle() {
        CMLAttribute _att_title = (CMLAttribute) getAttribute("title");
        if (_att_title == null) {
            return null;
        }
        return ((StringAttribute)_att_title).getString();
    }
    /** A title on an element.
    *
    * No controlled value.

    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setTitle(String value) throws CMLRuntimeException {
            CMLAttribute _att_title = null;
            try {
        		_att_title = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("title"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_title);
        ((StringAttribute)_att_title).setCMLValue(value);
    }
    /** The minimum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The minimum INCLUSIVE value of a quantity.
    * 
    *  The minimum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  min attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of min 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * minExclusive type in a dictionary should be used.
    *  The type of the minimum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @return CMLAttribute
    */
    public CMLAttribute getMinAttribute() {
        return (CMLAttribute) getAttribute("min");
    }
    /** The minimum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The minimum INCLUSIVE value of a quantity.
    * 
    *  The minimum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  min attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of min 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * minExclusive type in a dictionary should be used.
    *  The type of the minimum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @return String
    */
    public String getMin() {
        CMLAttribute _att_min = (CMLAttribute) getAttribute("min");
        if (_att_min == null) {
            return null;
        }
        return ((StringAttribute)_att_min).getString();
    }
    /** The minimum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The minimum INCLUSIVE value of a quantity.
    * 
    *  The minimum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  min attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of min 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * minExclusive type in a dictionary should be used.
    *  The type of the minimum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @param value min value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setMin(String value) throws CMLRuntimeException {
            CMLAttribute _att_min = null;
            try {
        		_att_min = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("min"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_min == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : min; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_min);
        ((StringAttribute)_att_min).setCMLValue(value);
    }
    /** Maximum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The maximum INCLUSIVE value of a quantity.
    * 
    *  The maximum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  max attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of max 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * maxExclusive type in a dictionary should be used.
    *  The type of the maximum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @return CMLAttribute
    */
    public CMLAttribute getMaxAttribute() {
        return (CMLAttribute) getAttribute("max");
    }
    /** Maximum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The maximum INCLUSIVE value of a quantity.
    * 
    *  The maximum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  max attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of max 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * maxExclusive type in a dictionary should be used.
    *  The type of the maximum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @return String
    */
    public String getMax() {
        CMLAttribute _att_max = (CMLAttribute) getAttribute("max");
        if (_att_max == null) {
            return null;
        }
        return ((StringAttribute)_att_max).getString();
    }
    /** Maximum value allowed for an element or attribute.
    *
    * 
    * --type info--
    * 
    * The maximum INCLUSIVE value of a quantity.
    * 
    *  The maximum INCLUSIVE value of a sortable quantity such as
    *  numeric, date or string. It should be ignored for dataTypes such as URL. 
    * The use of min and
    *  max attributes can be used to give a range for the quantity.
    *  The statistical basis of this range is not defined. The value of max 
    * is usually an observed 
    * quantity (or calculated from observations). To restrict a value, the 
    * maxExclusive type in a dictionary should be used.
    *  The type of the maximum is the same as the quantity to which it refers - numeric,
    *  date and string are currently allowed

    * @param value max value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setMax(String value) throws CMLRuntimeException {
            CMLAttribute _att_max = null;
            try {
        		_att_max = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("max"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_max == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : max; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_max);
        ((StringAttribute)_att_max).setCMLValue(value);
    }
    /** Scientific units on an element.
    *
    * These must be taken from a dictionary 
    * of units. There should be some mechanism for validating the type 
    * of the units against the possible values of the element.
    * --type info--
    * 
    * Scientific units.
    * These will be linked to dictionaries of 
    * units with conversion information, using namespaced references 
    * (e.g. si:m). Distinguish carefully from _unitType_ 
    * which is an element describing a type of a unit in a 
    * _unitList_.

    * @return CMLAttribute
    */
    public CMLAttribute getUnitsAttribute() {
        return (CMLAttribute) getAttribute("units");
    }
    /** Scientific units on an element.
    *
    * These must be taken from a dictionary 
    * of units. There should be some mechanism for validating the type 
    * of the units against the possible values of the element.
    * --type info--
    * 
    * Scientific units.
    * These will be linked to dictionaries of 
    * units with conversion information, using namespaced references 
    * (e.g. si:m). Distinguish carefully from _unitType_ 
    * which is an element describing a type of a unit in a 
    * _unitList_.

    * @return String
    */
    public String getUnits() {
        CMLAttribute _att_units = (CMLAttribute) getAttribute("units");
        if (_att_units == null) {
            return null;
        }
        return ((StringAttribute)_att_units).getString();
    }
    /** Scientific units on an element.
    *
    * These must be taken from a dictionary 
    * of units. There should be some mechanism for validating the type 
    * of the units against the possible values of the element.
    * --type info--
    * 
    * Scientific units.
    * These will be linked to dictionaries of 
    * units with conversion information, using namespaced references 
    * (e.g. si:m). Distinguish carefully from _unitType_ 
    * which is an element describing a type of a unit in a 
    * _unitList_.

    * @param value units value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setUnits(String value) throws CMLRuntimeException {
            CMLAttribute _att_units = null;
            try {
        		_att_units = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("units"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_units == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : units; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_units);
        ((StringAttribute)_att_units).setCMLValue(value);
    }
    /** unitsRef attribute on CML1 elements.
    *
    * CML1-only - now deprecated.

    * @return CMLAttribute
    */
    public CMLAttribute getUnitsRefAttribute() {
        return (CMLAttribute) getAttribute("unitsRef");
    }
    /** unitsRef attribute on CML1 elements.
    *
    * CML1-only - now deprecated.

    * @return String
    */
    public String getUnitsRef() {
        CMLAttribute _att_unitsRef = (CMLAttribute) getAttribute("unitsRef");
        if (_att_unitsRef == null) {
            return null;
        }
        return ((StringAttribute)_att_unitsRef).getString();
    }
    /** unitsRef attribute on CML1 elements.
    *
    * CML1-only - now deprecated.

    * @param value unitsRef value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setUnitsRef(String value) throws CMLRuntimeException {
            CMLAttribute _att_unitsRef = null;
            try {
        		_att_unitsRef = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("unitsRef"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_unitsRef == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitsRef; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_unitsRef);
        ((StringAttribute)_att_unitsRef).setCMLValue(value);
    }
    /** add float element.
    *
    * @param value add content
    * @throws CMLRuntimeException cannot add content; perhaps wrong value/type

    */
    public void setXMLContent(double value) throws CMLRuntimeException {
        if (_xmlContent == null) {
            _xmlContent = new DoubleAttribute("_xmlContent");
            _xmlContent.setSchemaType(CMLTypeList.getType("xsd:double"));
        }
        ((DoubleAttribute)_xmlContent).setCMLValue(value);
        String attval = (String)_xmlContent.getValue();
        this.removeChildren();
        this.appendChild(attval);
    }
    /** get content.
    *
    * @return double
    */
    public double getXMLContent() {
        String content = this.getValue();
        if (_xmlContent == null) {
        	_xmlContent = new DoubleAttribute("_xmlContent");
        	_xmlContent.setSchemaType(CMLTypeList.getType("xsd:double"));
        }
        _xmlContent.setCMLValue(content);
        return ((DoubleAttribute)_xmlContent).getDouble();
    }
    /** overrides addAttribute(Attribute).
    *
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
        } else if (name.equals("units")) {
            setUnits(value);
        } else if (name.equals("unitsRef")) {
            setUnitsRef(value);
        } else {
            super.addAttribute(att);
        }
    }
}
