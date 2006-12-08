package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import nu.xom.Elements;
import java.util.HashMap;
import java.util.Map;
import org.xmlcml.cml.element.CMLUnitType;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLUnit;

/** A container for several unit entries.
*
* 
* Usually forms the complete units dictionary \n(along with metadata). Note: this used to hold both units and unitTypes\n (though in separate files). This was unwieldy and unitTypeList has been\n created to hold unitTypes. Implementers are recommended to change\n any unitList/unitType to unitTypeList/unitType
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractUnitList extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "unitList";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractUnitList() {
        super("unitList");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractUnitList to copy

    */

    public AbstractUnitList(AbstractUnitList old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("type", "unitListType");
        attributeGroupNameTable.put("namespace", "namespace");
        attributeGroupNameTable.put("siNamespace", "siNamespace");
        attributeGroupNameTable.put("dictionaryPrefix", "dictionaryPrefix");
        attributeGroupNameTable.put("href", "href");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
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
    /** A reference to the type of a unit.
    *
    * Needed to differentiate the rather unhappy
    *  polymorphism of unitList/unit and unitList/unitType.
    * --type info--
    * 
    * Type of unitList.
    * child elements are unitType
    * Permitted values:
    *   unit
    *   unitType

    * @return CMLAttribute
    */
    public CMLAttribute getTypeAttribute() {
        return (CMLAttribute) getAttribute("type");
    }
    /** A reference to the type of a unit.
    *
    * Needed to differentiate the rather unhappy
    *  polymorphism of unitList/unit and unitList/unitType.
    * --type info--
    * 
    * Type of unitList.
    * child elements are unitType
    * Permitted values:
    *   unit
    *   unitType

    * @return String
    */
    public String getType() {
        CMLAttribute _att_type = (CMLAttribute) getAttribute("type");
        if (_att_type == null) {
            return null;
        }
        return ((StringAttribute)_att_type).getString();
    }
    /** A reference to the type of a unit.
    *
    * Needed to differentiate the rather unhappy
    *  polymorphism of unitList/unit and unitList/unitType.
    * --type info--
    * 
    * Type of unitList.
    * child elements are unitType
    * Permitted values:
    *   unit
    *   unitType

    * @param value type value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setType(String value) throws CMLRuntimeException {
            CMLAttribute _att_type = null;
            try {
        		_att_type = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("unitListType"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_type == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitListType; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_type);
        ((StringAttribute)_att_type).setCMLValue(value);
    }
    /** The namespace for a data item.
    *
    * The namespace is associated with elements such as dictionaries
    *  and units and allows them to be referenced through free namespace prefixes.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @return CMLAttribute
    */
    public CMLAttribute getNamespaceAttribute() {
        return (CMLAttribute) getAttribute("namespace");
    }
    /** The namespace for a data item.
    *
    * The namespace is associated with elements such as dictionaries
    *  and units and allows them to be referenced through free namespace prefixes.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @return String
    */
    public String getNamespace() {
        CMLAttribute _att_namespace = (CMLAttribute) getAttribute("namespace");
        if (_att_namespace == null) {
            return null;
        }
        return ((StringAttribute)_att_namespace).getString();
    }
    /** The namespace for a data item.
    *
    * The namespace is associated with elements such as dictionaries
    *  and units and allows them to be referenced through free namespace prefixes.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @param value namespace value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setNamespace(String value) throws CMLRuntimeException {
            CMLAttribute _att_namespace = null;
            try {
        		_att_namespace = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("namespace"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_namespace == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : namespace; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_namespace);
        ((StringAttribute)_att_namespace).setCMLValue(value);
    }
    /** The namespace for SI Units dictionary.
    *
    * Main use is on unitList to identify the 
    * dictionary holding the SI Units.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @return CMLAttribute
    */
    public CMLAttribute getSiNamespaceAttribute() {
        return (CMLAttribute) getAttribute("siNamespace");
    }
    /** The namespace for SI Units dictionary.
    *
    * Main use is on unitList to identify the 
    * dictionary holding the SI Units.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @return String
    */
    public String getSiNamespace() {
        CMLAttribute _att_siNamespace = (CMLAttribute) getAttribute("siNamespace");
        if (_att_siNamespace == null) {
            return null;
        }
        return ((StringAttribute)_att_siNamespace).getString();
    }
    /** The namespace for SI Units dictionary.
    *
    * Main use is on unitList to identify the 
    * dictionary holding the SI Units.
    * --type info--
    * 
    * A namespaceURI with required protocol.
    * 
    *  The namespace prefix must start with a protocol.
    *  
    * Pattern: http://[A-Za-z][A-Za-z0-9_\.\-]*(/[A-Za-z0-9_\.\-]+)+

    * @param value siNamespace value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setSiNamespace(String value) throws CMLRuntimeException {
            CMLAttribute _att_siNamespace = null;
            try {
        		_att_siNamespace = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("siNamespace"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_siNamespace == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : siNamespace; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_siNamespace);
        ((StringAttribute)_att_siNamespace).setCMLValue(value);
    }
    /** The namespacePrefix for a data item.
    *
    * The dictionaryPrefix is associated with elements 
    * such as dictionaries and units and allows them to be referenced namespaces.
    *  The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *  occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *  be identified and fixed.
    * --type info--
    * 
    * A dictionaryPrefix.
    * 
    *  The dictionary prefix must conform to XSD.
    *  
    * Pattern: [A-Za-z][A-Za-z0-9_\.\-]*

    * @return CMLAttribute
    */
    public CMLAttribute getDictionaryPrefixAttribute() {
        return (CMLAttribute) getAttribute("dictionaryPrefix");
    }
    /** The namespacePrefix for a data item.
    *
    * The dictionaryPrefix is associated with elements 
    * such as dictionaries and units and allows them to be referenced namespaces.
    *  The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *  occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *  be identified and fixed.
    * --type info--
    * 
    * A dictionaryPrefix.
    * 
    *  The dictionary prefix must conform to XSD.
    *  
    * Pattern: [A-Za-z][A-Za-z0-9_\.\-]*

    * @return String
    */
    public String getDictionaryPrefix() {
        CMLAttribute _att_dictionaryPrefix = (CMLAttribute) getAttribute("dictionaryPrefix");
        if (_att_dictionaryPrefix == null) {
            return null;
        }
        return ((StringAttribute)_att_dictionaryPrefix).getString();
    }
    /** The namespacePrefix for a data item.
    *
    * The dictionaryPrefix is associated with elements 
    * such as dictionaries and units and allows them to be referenced namespaces.
    *  The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *  occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *  be identified and fixed.
    * --type info--
    * 
    * A dictionaryPrefix.
    * 
    *  The dictionary prefix must conform to XSD.
    *  
    * Pattern: [A-Za-z][A-Za-z0-9_\.\-]*

    * @param value dictionaryPrefix value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setDictionaryPrefix(String value) throws CMLRuntimeException {
            CMLAttribute _att_dictionaryPrefix = null;
            try {
        		_att_dictionaryPrefix = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("dictionaryPrefix"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_dictionaryPrefix == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictionaryPrefix; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_dictionaryPrefix);
        ((StringAttribute)_att_dictionaryPrefix).setCMLValue(value);
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @return CMLAttribute
    */
    public CMLAttribute getHrefAttribute() {
        return (CMLAttribute) getAttribute("href");
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @return String
    */
    public String getHref() {
        CMLAttribute _att_href = (CMLAttribute) getAttribute("href");
        if (_att_href == null) {
            return null;
        }
        return ((StringAttribute)_att_href).getString();
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @param value href value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setHref(String value) throws CMLRuntimeException {
            CMLAttribute _att_href = null;
            try {
        		_att_href = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("href"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_href == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : href; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_href);
        ((StringAttribute)_att_href).setCMLValue(value);
    }
    /** add unitList element.
    *
    * @param unitType unitType child to add

    */
    public void addUnitType(AbstractUnitType unitType) {
        unitType.detach();
        this.appendChild(unitType);
    }
    /** get unitList child elements .
    *
    * @return CMLElements<CMLUnitType>
    */
    public CMLElements<CMLUnitType> getUnitTypeElements() {
        Elements elements = this.getChildElements("unitType", CML_NS);
        return new CMLElements<CMLUnitType>(elements);
    }
    /** add unitList element.
    *
    * @param metadataList metadataList child to add

    */
    public void addMetadataList(AbstractMetadataList metadataList) {
        metadataList.detach();
        this.appendChild(metadataList);
    }
    /** get unitList child elements .
    *
    * @return CMLElements<CMLMetadataList>
    */
    public CMLElements<CMLMetadataList> getMetadataListElements() {
        Elements elements = this.getChildElements("metadataList", CML_NS);
        return new CMLElements<CMLMetadataList>(elements);
    }
    /** add unitList element.
    *
    * @param unit unit child to add

    */
    public void addUnit(AbstractUnit unit) {
        unit.detach();
        this.appendChild(unit);
    }
    /** get unitList child elements .
    *
    * @return CMLElements<CMLUnit>
    */
    public CMLElements<CMLUnit> getUnitElements() {
        Elements elements = this.getChildElements("unit", CML_NS);
        return new CMLElements<CMLUnit>(elements);
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
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("convention")) {
            setConvention(value);
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("type")) {
            setType(value);
        } else if (name.equals("namespace")) {
            setNamespace(value);
        } else if (name.equals("siNamespace")) {
            setSiNamespace(value);
        } else if (name.equals("dictionaryPrefix")) {
            setDictionaryPrefix(value);
        } else if (name.equals("href")) {
            setHref(value);
        } else {
            super.addAttribute(att);
        }
    }
}
