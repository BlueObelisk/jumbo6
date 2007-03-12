package org.xmlcml.cml.element;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.StringAttribute;

/** A text string qualifying an object.
*
* 
* A label can be used to identify or distinguish elements, add keywords or classifications and similar processes. It is usually interpretable by domain-aware humans (e.g. C3'-endo, but not a34561). It is usually either built in a semantically rich fashion (e.g. C2'-alpha-H) or belongs to a controlled vocabulary. It is possibly accessed by software in a domain-specific manner. It differs from description which is free text. The distinction between titles, names and labels is fuzzy, but we think this is worth making. Labels may be necesssary to identify objects within programs, while names are more likely to be reserved for database searches. Titles are likely to be freer text and not recommended for precise object retrieval.
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractLabel extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "label";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractLabel() {
        super("label");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractLabel to copy

    */

    public AbstractLabel(AbstractLabel old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("value", "value");
        attributeGroupNameTable.put("objectClass", "objectClass");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
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
    /** Value of a scalar object.
    *
    * The value must be consistent with the dataType of the object.

    * @return CMLAttribute
    */
    public CMLAttribute getValueAttribute() {
        return (CMLAttribute) getAttribute("value");
    }
    /** Value of a scalar object.
    *
    * The value must be consistent with the dataType of the object.

    * @return String
    */
    public String getCMLValue() {
        CMLAttribute _att_value = (CMLAttribute) getAttribute("value");
        if (_att_value == null) {
            return null;
        }
        return ((StringAttribute)_att_value).getString();
    }
    /** Value of a scalar object.
    *
    * The value must be consistent with the dataType of the object.

    * @param value value value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setCMLValue(String value) throws CMLRuntimeException {
            CMLAttribute _att_value = null;
            try {
        		_att_value = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("value"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_value == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : value; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_value);
        ((StringAttribute)_att_value).setCMLValue(value);
    }
    /** The class of an object.
    *
    * The type of this information. This is not controlled, but examples might include:
    *  
    * label
    *  summary
    *  note
    *  usage
    *  qualifier
    *  
    * It might be used to control display or XSL filtering.

    * @return CMLAttribute
    */
    public CMLAttribute getObjectClassAttribute() {
        return (CMLAttribute) getAttribute("objectClass");
    }
    /** The class of an object.
    *
    * The type of this information. This is not controlled, but examples might include:
    *  
    * label
    *  summary
    *  note
    *  usage
    *  qualifier
    *  
    * It might be used to control display or XSL filtering.

    * @return String
    */
    public String getObjectClass() {
        CMLAttribute _att_objectClass = (CMLAttribute) getAttribute("objectClass");
        if (_att_objectClass == null) {
            return null;
        }
        return ((StringAttribute)_att_objectClass).getString();
    }
    /** The class of an object.
    *
    * The type of this information. This is not controlled, but examples might include:
    *  
    * label
    *  summary
    *  note
    *  usage
    *  qualifier
    *  
    * It might be used to control display or XSL filtering.

    * @param value objectClass value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setObjectClass(String value) throws CMLRuntimeException {
            CMLAttribute _att_objectClass = null;
            try {
        		_att_objectClass = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("objectClass"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_objectClass == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : objectClass; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_objectClass);
        ((StringAttribute)_att_objectClass).setCMLValue(value);
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
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("value")) {
            setCMLValue(value);
        } else if (name.equals("objectClass")) {
            setObjectClass(value);
        } else {
            super.addAttribute(att);
        }
    }
}
