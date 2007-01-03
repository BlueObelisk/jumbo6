package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import java.util.HashMap;
import java.util.Map;

/** An object in space carrying a set of properties.
*
* 
* \n particles have many of the characteristics of atoms \nbut without an atomic nucleus. It does not have an elementType and cannot be \ninvolved in bonding, etc. It has coordinates, may carry charge and might have a \nmass. It represents some aspect of a computational model and should not be used \nfor purely geometrical concepts such as centroid. Examples of particles are \n\"shells\" (e.g. in GULP) which are linked to atoms for modelling polarizability \nor lonepairs and approximations to multipoles. Properties such as charge, mass \nshould be scalar/array/matrix children.
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractParticle extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "particle";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractParticle() {
        super("particle");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractParticle to copy

    */

    public AbstractParticle(AbstractParticle old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("type", "type");
        attributeGroupNameTable.put("x3", "x3");
        attributeGroupNameTable.put("y3", "y3");
        attributeGroupNameTable.put("z3", "z3");
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
    /** Type of the object.
    *
    * A qualifier which may affect the semantics of the object.

    * @return CMLAttribute
    */
    public CMLAttribute getTypeAttribute() {
        return (CMLAttribute) getAttribute("type");
    }
    /** Type of the object.
    *
    * A qualifier which may affect the semantics of the object.

    * @return String
    */
    public String getType() {
        CMLAttribute _att_type = (CMLAttribute) getAttribute("type");
        if (_att_type == null) {
            return null;
        }
        return ((StringAttribute)_att_type).getString();
    }
    /** Type of the object.
    *
    * A qualifier which may affect the semantics of the object.

    * @param value type value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setType(String value) throws CMLRuntimeException {
            CMLAttribute _att_type = null;
            try {
        		_att_type = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("type"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_type == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : type; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_type);
        ((StringAttribute)_att_type).setCMLValue(value);
    }
    /** The x coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The provision 
    * for other units is weak at present.) Objects are always described 
    * with a right-handed coordinate system.

    * @return CMLAttribute
    */
    public CMLAttribute getX3Attribute() {
        return (CMLAttribute) getAttribute("x3");
    }
    /** The x coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The provision 
    * for other units is weak at present.) Objects are always described 
    * with a right-handed coordinate system.

    * @return double
    */
    public double getX3() {
        CMLAttribute _att_x3 = (CMLAttribute) getAttribute("x3");
        if (_att_x3 == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_x3).getDouble();
    }
    /** The x coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The provision 
    * for other units is weak at present.) Objects are always described 
    * with a right-handed coordinate system.

    * @param value x3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setX3(double value) throws CMLRuntimeException {
            CMLAttribute _att_x3 = null;
            try {
        		_att_x3 = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("x3"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_x3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : x3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_x3);
        ((DoubleAttribute)_att_x3).setCMLValue(value);
    }
    /** The x coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The provision 
    * for other units is weak at present.) Objects are always described 
    * with a right-handed coordinate system.

    * @param value x3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setX3(String value) throws CMLRuntimeException {
            CMLAttribute _att_x3 = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("x3"));
            if (_att_x3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : x3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_x3);
        ((DoubleAttribute)_att_x3).setCMLValue(value);
    }
    /** The y coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @return CMLAttribute
    */
    public CMLAttribute getY3Attribute() {
        return (CMLAttribute) getAttribute("y3");
    }
    /** The y coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @return double
    */
    public double getY3() {
        CMLAttribute _att_y3 = (CMLAttribute) getAttribute("y3");
        if (_att_y3 == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_y3).getDouble();
    }
    /** The y coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @param value y3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setY3(double value) throws CMLRuntimeException {
            CMLAttribute _att_y3 = null;
            try {
        		_att_y3 = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("y3"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_y3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : y3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_y3);
        ((DoubleAttribute)_att_y3).setCMLValue(value);
    }
    /** The y coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @param value y3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setY3(String value) throws CMLRuntimeException {
            CMLAttribute _att_y3 = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("y3"));
            if (_att_y3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : y3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_y3);
        ((DoubleAttribute)_att_y3).setCMLValue(value);
    }
    /** The z coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @return CMLAttribute
    */
    public CMLAttribute getZ3Attribute() {
        return (CMLAttribute) getAttribute("z3");
    }
    /** The z coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @return double
    */
    public double getZ3() {
        CMLAttribute _att_z3 = (CMLAttribute) getAttribute("z3");
        if (_att_z3 == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_z3).getDouble();
    }
    /** The z coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @param value z3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setZ3(double value) throws CMLRuntimeException {
            CMLAttribute _att_z3 = null;
            try {
        		_att_z3 = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("z3"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_z3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : z3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_z3);
        ((DoubleAttribute)_att_z3).setCMLValue(value);
    }
    /** The z coordinate of a 3 dimensional object.
    *
    * The default units are Angstrom. (The 
    * provision for other units is weak at present.) Objects are always 
    * described with a right-handed coordinate system.

    * @param value z3 value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setZ3(String value) throws CMLRuntimeException {
            CMLAttribute _att_z3 = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("z3"));
            if (_att_z3 == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : z3; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_z3);
        ((DoubleAttribute)_att_z3).setCMLValue(value);
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
        } else if (name.equals("x3")) {
            setX3(value);
        } else if (name.equals("y3")) {
            setY3(value);
        } else if (name.equals("z3")) {
            setZ3(value);
        } else {
            super.addAttribute(att);
        }
    }
}
