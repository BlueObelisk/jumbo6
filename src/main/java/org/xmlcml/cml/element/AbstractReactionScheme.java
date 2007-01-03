package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import nu.xom.Elements;
import java.util.HashMap;
import java.util.Map;
import org.xmlcml.cml.element.CMLReactionStepList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLReactionScheme;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLName;

/** A container for two or more related reactions and their relationships.
*
* 
* \n The semantics of the content model are\n \nmetadataList for general metadata\n label for classifying or describing the reaction (e.g. \"hydrolysis\")\n identifier for unique identification. This could be a classification such as EC (enzyme commission) or an IChI-like string generated from the components.\n these are followed by the possible components of the reaction and/or a reactionList of further details.\n \n
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractReactionScheme extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "reactionScheme";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractReactionScheme() {
        super("reactionScheme");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractReactionScheme to copy

    */

    public AbstractReactionScheme(AbstractReactionScheme old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("ref", "ref");
        attributeGroupNameTable.put("role", "reactionRole");
        attributeGroupNameTable.put("type", "reactionType");
        attributeGroupNameTable.put("state", "state");
        attributeGroupNameTable.put("format", "reactionFormat");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
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
    /** A reference to an element of given type.
    *
    * ref modifies an element into a reference to an existing element of that type within the document. This is similar to a pointer and it can be thought of a strongly typed hyperlink. It may also be used for "subclassing" or "overriding" elements.
    *  When referring to an element most of the "data" such as attribute values and element content will be on the full instantiated element. Therefore ref (and possibly id) will normally be the only attributes on the pointing element. However there may be some attributes (title, count, etc.) which have useful semantics, but these are element-specific
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
    public CMLAttribute getRefAttribute() {
        return (CMLAttribute) getAttribute("ref");
    }
    /** A reference to an element of given type.
    *
    * ref modifies an element into a reference to an existing element of that type within the document. This is similar to a pointer and it can be thought of a strongly typed hyperlink. It may also be used for "subclassing" or "overriding" elements.
    *  When referring to an element most of the "data" such as attribute values and element content will be on the full instantiated element. Therefore ref (and possibly id) will normally be the only attributes on the pointing element. However there may be some attributes (title, count, etc.) which have useful semantics, but these are element-specific
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
    public String getRef() {
        CMLAttribute _att_ref = (CMLAttribute) getAttribute("ref");
        if (_att_ref == null) {
            return null;
        }
        return ((StringAttribute)_att_ref).getString();
    }
    /** A reference to an element of given type.
    *
    * ref modifies an element into a reference to an existing element of that type within the document. This is similar to a pointer and it can be thought of a strongly typed hyperlink. It may also be used for "subclassing" or "overriding" elements.
    *  When referring to an element most of the "data" such as attribute values and element content will be on the full instantiated element. Therefore ref (and possibly id) will normally be the only attributes on the pointing element. However there may be some attributes (title, count, etc.) which have useful semantics, but these are element-specific
    * --type info--
    * 
    * A reference to an existing object.
    * The semantic of reference are normally identical to 
    * an idType (e.g. "a123b"). Howevere there are some cases where compound references
    *  are required, such as "a123b:pq456". It is likely that this will be superseded at
    *  by RDF or Xpointer, but as long as we have non-uniqueIds this is a problem
    * Pattern: ([A-Za-z_][A-Za-z0-9_\.\-]*:)?[A-Za-z_][A-Za-z0-9_\.\-]*

    * @param value ref value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setRef(String value) throws CMLRuntimeException {
            CMLAttribute _att_ref = null;
            try {
        		_att_ref = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("ref"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_ref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : ref; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_ref);
        ((StringAttribute)_att_ref).setCMLValue(value);
    }
    /** Role of the reaction.
    *
    * 
    * --type info--
    * 
    * The role of the reaction within a reactionList.
    * Semantics are semi-controlled.

    * @return CMLAttribute
    */
    public CMLAttribute getRoleAttribute() {
        return (CMLAttribute) getAttribute("role");
    }
    /** Role of the reaction.
    *
    * 
    * --type info--
    * 
    * The role of the reaction within a reactionList.
    * Semantics are semi-controlled.

    * @return String
    */
    public String getRole() {
        CMLAttribute _att_role = (CMLAttribute) getAttribute("role");
        if (_att_role == null) {
            return null;
        }
        return ((StringAttribute)_att_role).getString();
    }
    /** Role of the reaction.
    *
    * 
    * --type info--
    * 
    * The role of the reaction within a reactionList.
    * Semantics are semi-controlled.

    * @param value role value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setRole(String value) throws CMLRuntimeException {
            CMLAttribute _att_role = null;
            try {
        		_att_role = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("reactionRole"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_role == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : reactionRole; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_role);
        ((StringAttribute)_att_role).setCMLValue(value);
    }
    /** Type of the reaction.
    *
    * 
    * --type info--
    * 
    * The semantic type of the reaction.

    * @return CMLAttribute
    */
    public CMLAttribute getTypeAttribute() {
        return (CMLAttribute) getAttribute("type");
    }
    /** Type of the reaction.
    *
    * 
    * --type info--
    * 
    * The semantic type of the reaction.

    * @return String
    */
    public String getType() {
        CMLAttribute _att_type = (CMLAttribute) getAttribute("type");
        if (_att_type == null) {
            return null;
        }
        return ((StringAttribute)_att_type).getString();
    }
    /** Type of the reaction.
    *
    * 
    * --type info--
    * 
    * The semantic type of the reaction.

    * @param value type value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setType(String value) throws CMLRuntimeException {
            CMLAttribute _att_type = null;
            try {
        		_att_type = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("reactionType"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_type == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : reactionType; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_type);
        ((StringAttribute)_att_type).setCMLValue(value);
    }
    /** The physical state of the substance.
    *
    * No fixed semantics or default.
    * --type info--
    * 
    * State of a substance or property.
    * The state(s) of matter appropriate to a substance or property. It follows a partially controlled vocabulary. It can be extended through namespace codes to dictionaries.

    * @return CMLAttribute
    */
    public CMLAttribute getStateAttribute() {
        return (CMLAttribute) getAttribute("state");
    }
    /** The physical state of the substance.
    *
    * No fixed semantics or default.
    * --type info--
    * 
    * State of a substance or property.
    * The state(s) of matter appropriate to a substance or property. It follows a partially controlled vocabulary. It can be extended through namespace codes to dictionaries.

    * @return String
    */
    public String getState() {
        CMLAttribute _att_state = (CMLAttribute) getAttribute("state");
        if (_att_state == null) {
            return null;
        }
        return ((StringAttribute)_att_state).getString();
    }
    /** The physical state of the substance.
    *
    * No fixed semantics or default.
    * --type info--
    * 
    * State of a substance or property.
    * The state(s) of matter appropriate to a substance or property. It follows a partially controlled vocabulary. It can be extended through namespace codes to dictionaries.

    * @param value state value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setState(String value) throws CMLRuntimeException {
            CMLAttribute _att_state = null;
            try {
        		_att_state = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("state"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_state == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : state; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_state);
        ((StringAttribute)_att_state).setCMLValue(value);
    }
    /** Format of the reaction component.
    *
    * Indicates how the components of reactionScheme, reactionStepList, etc. should be processed. No controlled vocabulary. One example is format="cmlSnap" asserts that the processor can assume that the reactants and products can be rendered using the CMLSnap design. Note that the reaction can be interpreted without reference to the format, which is primarily a processing instruction.
    * --type info--
    * 
    * The format of the reaction.

    * @return CMLAttribute
    */
    public CMLAttribute getFormatAttribute() {
        return (CMLAttribute) getAttribute("format");
    }
    /** Format of the reaction component.
    *
    * Indicates how the components of reactionScheme, reactionStepList, etc. should be processed. No controlled vocabulary. One example is format="cmlSnap" asserts that the processor can assume that the reactants and products can be rendered using the CMLSnap design. Note that the reaction can be interpreted without reference to the format, which is primarily a processing instruction.
    * --type info--
    * 
    * The format of the reaction.

    * @return String
    */
    public String getFormat() {
        CMLAttribute _att_format = (CMLAttribute) getAttribute("format");
        if (_att_format == null) {
            return null;
        }
        return ((StringAttribute)_att_format).getString();
    }
    /** Format of the reaction component.
    *
    * Indicates how the components of reactionScheme, reactionStepList, etc. should be processed. No controlled vocabulary. One example is format="cmlSnap" asserts that the processor can assume that the reactants and products can be rendered using the CMLSnap design. Note that the reaction can be interpreted without reference to the format, which is primarily a processing instruction.
    * --type info--
    * 
    * The format of the reaction.

    * @param value format value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setFormat(String value) throws CMLRuntimeException {
            CMLAttribute _att_format = null;
            try {
        		_att_format = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("reactionFormat"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_format == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : reactionFormat; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_format);
        ((StringAttribute)_att_format).setCMLValue(value);
    }
    /** add reactionScheme element.
    *
    * @param reactionStepList reactionStepList child to add

    */
    public void addReactionStepList(AbstractReactionStepList reactionStepList) {
        reactionStepList.detach();
        this.appendChild(reactionStepList);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLReactionStepList>
    */
    public CMLElements<CMLReactionStepList> getReactionStepListElements() {
        Elements elements = this.getChildElements("reactionStepList", CML_NS);
        return new CMLElements<CMLReactionStepList>(elements);
    }
    /** add reactionScheme element.
    *
    * @param reaction reaction child to add

    */
    public void addReaction(AbstractReaction reaction) {
        reaction.detach();
        this.appendChild(reaction);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLReaction>
    */
    public CMLElements<CMLReaction> getReactionElements() {
        Elements elements = this.getChildElements("reaction", CML_NS);
        return new CMLElements<CMLReaction>(elements);
    }
    /** add reactionScheme element.
    *
    * @param reactionScheme reactionScheme child to add

    */
    public void addReactionScheme(AbstractReactionScheme reactionScheme) {
        reactionScheme.detach();
        this.appendChild(reactionScheme);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLReactionScheme>
    */
    public CMLElements<CMLReactionScheme> getReactionSchemeElements() {
        Elements elements = this.getChildElements("reactionScheme", CML_NS);
        return new CMLElements<CMLReactionScheme>(elements);
    }
    /** add reactionScheme element.
    *
    * @param metadataList metadataList child to add

    */
    public void addMetadataList(AbstractMetadataList metadataList) {
        metadataList.detach();
        this.appendChild(metadataList);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLMetadataList>
    */
    public CMLElements<CMLMetadataList> getMetadataListElements() {
        Elements elements = this.getChildElements("metadataList", CML_NS);
        return new CMLElements<CMLMetadataList>(elements);
    }
    /** add reactionScheme element.
    *
    * @param label label child to add

    */
    public void addLabel(AbstractLabel label) {
        label.detach();
        this.appendChild(label);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLLabel>
    */
    public CMLElements<CMLLabel> getLabelElements() {
        Elements elements = this.getChildElements("label", CML_NS);
        return new CMLElements<CMLLabel>(elements);
    }
    /** add reactionScheme element.
    *
    * @param identifier identifier child to add

    */
    public void addIdentifier(AbstractIdentifier identifier) {
        identifier.detach();
        this.appendChild(identifier);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLIdentifier>
    */
    public CMLElements<CMLIdentifier> getIdentifierElements() {
        Elements elements = this.getChildElements("identifier", CML_NS);
        return new CMLElements<CMLIdentifier>(elements);
    }
    /** add reactionScheme element.
    *
    * @param name name child to add

    */
    public void addName(AbstractName name) {
        name.detach();
        this.appendChild(name);
    }
    /** get reactionScheme child elements .
    *
    * @return CMLElements<CMLName>
    */
    public CMLElements<CMLName> getNameElements() {
        Elements elements = this.getChildElements("name", CML_NS);
        return new CMLElements<CMLName>(elements);
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
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("convention")) {
            setConvention(value);
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("ref")) {
            setRef(value);
        } else if (name.equals("role")) {
            setRole(value);
        } else if (name.equals("type")) {
            setType(value);
        } else if (name.equals("state")) {
            setState(value);
        } else if (name.equals("format")) {
            setFormat(value);
        } else {
            super.addAttribute(att);
        }
    }
}
