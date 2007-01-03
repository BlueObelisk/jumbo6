package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import nu.xom.Elements;
import java.util.HashMap;
import java.util.Map;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLTransform3;

/** Molecular, crystallographic or other symmetry.
*
* 
* \n \nsymmetry provides a label and/or symmetry operations for molecules\n or crystals. Point and spacegroups can be specified by strings, though these are not \nenumerated, because of variability in syntax (spaces, case-sensitivity, etc.),\n potential high symmetries (e.g. TMV disk is D17) and\n non-standard spacegroup settings. Provision is made for explicit symmetry operations\n through <matrix> child elements.\n By default the axes of symmetry are defined by the symbol - thus C2v requires\n z to be the unique axis, while P21/c requires b/y. Spacegroups imply the semantics\n defined in International Tables for Crystallography, (Int Union for Cryst., Munksgaard).\n Point groups are also defined therein. \nThe element may also be used to give a label for the symmetry species (irreducible\n representation) such as \"A1u\" for a vibration or orbital.\n \nThe matrices should be 3x3 for point group operators and 3x4 for spacegroup operators.\n The use of crystallographic notation (\"x,1/2+y,-z\") is not supported - this would\n be <matrix>1 0 0 0.0 0 1 0 0.5 0 0 1 0.0<matrix>.\n The default convention for point group symmetry is Schoenflies and for\n spacegroups is \"H-M\". Other conventions (e.g. \"Hall\") must be specfied through\n the convention attribute.\n This element implies that the Cartesians or fractional coordinates in a molecule\n are oriented appropriately. In some cases it may be useful to specify the symmetry of\n an arbitarily oriented molecule and the <molecule> element has the attribute\n symmetryOriented for this purpose.\n It may be better to use transform3 to hold the symmetry as they have fixed shape and\n have better defined mathematical operators.\n 
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractSymmetry extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "symmetry";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractSymmetry() {
        super("symmetry");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractSymmetry to copy

    */

    public AbstractSymmetry(AbstractSymmetry old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("pointGroup", "pointGroup");
        attributeGroupNameTable.put("spaceGroup", "spaceGroup");
        attributeGroupNameTable.put("irreducibleRepresentation", "irreducibleRepresentation");
        attributeGroupNameTable.put("number", "number");
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
    /** A point group.
    *
    * No fixed semantics, though Schoenflies is recommended over Hermann-Mauguin. We may provide a controlled-extensible list in the future.

    * @return CMLAttribute
    */
    public CMLAttribute getPointGroupAttribute() {
        return (CMLAttribute) getAttribute("pointGroup");
    }
    /** A point group.
    *
    * No fixed semantics, though Schoenflies is recommended over Hermann-Mauguin. We may provide a controlled-extensible list in the future.

    * @return String
    */
    public String getPointGroup() {
        CMLAttribute _att_pointGroup = (CMLAttribute) getAttribute("pointGroup");
        if (_att_pointGroup == null) {
            return null;
        }
        return ((StringAttribute)_att_pointGroup).getString();
    }
    /** A point group.
    *
    * No fixed semantics, though Schoenflies is recommended over Hermann-Mauguin. We may provide a controlled-extensible list in the future.

    * @param value pointGroup value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setPointGroup(String value) throws CMLRuntimeException {
            CMLAttribute _att_pointGroup = null;
            try {
        		_att_pointGroup = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("pointGroup"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_pointGroup == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : pointGroup; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_pointGroup);
        ((StringAttribute)_att_pointGroup).setCMLValue(value);
    }
    /** A space group.
    *
    * No fixed semantics, though Hermann-Mauguin or Hall is recommended over Schoenflies. We may provide a controlled-extensible list in the future.

    * @return CMLAttribute
    */
    public CMLAttribute getSpaceGroupAttribute() {
        return (CMLAttribute) getAttribute("spaceGroup");
    }
    /** A space group.
    *
    * No fixed semantics, though Hermann-Mauguin or Hall is recommended over Schoenflies. We may provide a controlled-extensible list in the future.

    * @return String
    */
    public String getSpaceGroup() {
        CMLAttribute _att_spaceGroup = (CMLAttribute) getAttribute("spaceGroup");
        if (_att_spaceGroup == null) {
            return null;
        }
        return ((StringAttribute)_att_spaceGroup).getString();
    }
    /** A space group.
    *
    * No fixed semantics, though Hermann-Mauguin or Hall is recommended over Schoenflies. We may provide a controlled-extensible list in the future.

    * @param value spaceGroup value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setSpaceGroup(String value) throws CMLRuntimeException {
            CMLAttribute _att_spaceGroup = null;
            try {
        		_att_spaceGroup = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("spaceGroup"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_spaceGroup == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : spaceGroup; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_spaceGroup);
        ((StringAttribute)_att_spaceGroup).setCMLValue(value);
    }
    /** A symmetry species.
    *
    * No fixed semantics, though we may provide a controlled-extensible list in the future.

    * @return CMLAttribute
    */
    public CMLAttribute getIrreducibleRepresentationAttribute() {
        return (CMLAttribute) getAttribute("irreducibleRepresentation");
    }
    /** A symmetry species.
    *
    * No fixed semantics, though we may provide a controlled-extensible list in the future.

    * @return String
    */
    public String getIrreducibleRepresentation() {
        CMLAttribute _att_irreducibleRepresentation = (CMLAttribute) getAttribute("irreducibleRepresentation");
        if (_att_irreducibleRepresentation == null) {
            return null;
        }
        return ((StringAttribute)_att_irreducibleRepresentation).getString();
    }
    /** A symmetry species.
    *
    * No fixed semantics, though we may provide a controlled-extensible list in the future.

    * @param value irreducibleRepresentation value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setIrreducibleRepresentation(String value) throws CMLRuntimeException {
            CMLAttribute _att_irreducibleRepresentation = null;
            try {
        		_att_irreducibleRepresentation = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("irreducibleRepresentation"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_irreducibleRepresentation == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : irreducibleRepresentation; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_irreducibleRepresentation);
        ((StringAttribute)_att_irreducibleRepresentation).setCMLValue(value);
    }
    /** A number determined by context.
    *
    * Used for isotope number in isotope, and rotational symmetry number in symmetry for calculation of entropy, etc.
    * --type info--
    * 
    * MinInclusive: 0

    * @return CMLAttribute
    */
    public CMLAttribute getNumberAttribute() {
        return (CMLAttribute) getAttribute("number");
    }
    /** A number determined by context.
    *
    * Used for isotope number in isotope, and rotational symmetry number in symmetry for calculation of entropy, etc.
    * --type info--
    * 
    * MinInclusive: 0

    * @return int
    */
    public int getNumber() {
        CMLAttribute _att_number = (CMLAttribute) getAttribute("number");
        if (_att_number == null) {
            throw new CMLRuntimeException("unset attribute: number");
        }
        return ((IntAttribute)_att_number).getInt();
    }
    /** A number determined by context.
    *
    * Used for isotope number in isotope, and rotational symmetry number in symmetry for calculation of entropy, etc.
    * --type info--
    * 
    * MinInclusive: 0

    * @param value number value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setNumber(int value) throws CMLRuntimeException {
            CMLAttribute _att_number = null;
            try {
        		_att_number = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("number"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_number == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : number; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_number);
        ((IntAttribute)_att_number).setCMLValue(value);
    }
    /** A number determined by context.
    *
    * Used for isotope number in isotope, and rotational symmetry number in symmetry for calculation of entropy, etc.
    * --type info--
    * 
    * MinInclusive: 0

    * @param value number value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setNumber(String value) throws CMLRuntimeException {
            CMLAttribute _att_number = new IntAttribute((IntAttribute)CMLAttributeList.getAttribute("number"));
            if (_att_number == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : number; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_number);
        ((IntAttribute)_att_number).setCMLValue(value);
    }
    /** add symmetry element.
    *
    * @param matrix matrix child to add

    */
    public void addMatrix(AbstractMatrix matrix) {
        matrix.detach();
        this.appendChild(matrix);
    }
    /** get symmetry child elements .
    *
    * @return CMLElements<CMLMatrix>
    */
    public CMLElements<CMLMatrix> getMatrixElements() {
        Elements elements = this.getChildElements("matrix", CML_NS);
        return new CMLElements<CMLMatrix>(elements);
    }
    /** add symmetry element.
    *
    * @param transform3 transform3 child to add

    */
    public void addTransform3(AbstractTransform3 transform3) {
        transform3.detach();
        this.appendChild(transform3);
    }
    /** get symmetry child elements .
    *
    * @return CMLElements<CMLTransform3>
    */
    public CMLElements<CMLTransform3> getTransform3Elements() {
        Elements elements = this.getChildElements("transform3", CML_NS);
        return new CMLElements<CMLTransform3>(elements);
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
        } else if (name.equals("pointGroup")) {
            setPointGroup(value);
        } else if (name.equals("spaceGroup")) {
            setSpaceGroup(value);
        } else if (name.equals("irreducibleRepresentation")) {
            setIrreducibleRepresentation(value);
        } else if (name.equals("number")) {
            setNumber(value);
        } else {
            super.addAttribute(att);
        }
    }
}
