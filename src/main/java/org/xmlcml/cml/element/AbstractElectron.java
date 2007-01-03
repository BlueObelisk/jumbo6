package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import java.util.HashMap;
import java.util.Map;

/** An electron.
*
* 
* Since there is very little use of electrons in current chemical information this is a fluid concept. I expect it to be used for electron counting, input and output of theochem operations, descriptions of orbitals, spin states, oxidation states, etc. Electrons can be associated with atoms, bonds and combinations of these. At present there is no hardcoded semantics. However, _atomRef_ and similar attributes can be used to associate electrons with atoms or bond.
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractElectron extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "electron";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractElectron() {
        super("electron");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractElectron to copy

    */

    public AbstractElectron(AbstractElectron old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("convention", "convention");
        attributeGroupNameTable.put("dictRef", "dictRef");
        attributeGroupNameTable.put("atomRef", "atomRef");
        attributeGroupNameTable.put("atomRefs", "atomRefs");
        attributeGroupNameTable.put("bondRef", "bondRef");
        attributeGroupNameTable.put("bondRefs", "bondRefs");
        attributeGroupNameTable.put("count", "count");
        attributeGroupNameTable.put("ref", "ref");
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
    /** A reference to an atom.
    *
    * Used by bond, electron, etc.
    * --type info--
    * 
    * A reference to an existing atom.

    * @return CMLAttribute
    */
    public CMLAttribute getAtomRefAttribute() {
        return (CMLAttribute) getAttribute("atomRef");
    }
    /** A reference to an atom.
    *
    * Used by bond, electron, etc.
    * --type info--
    * 
    * A reference to an existing atom.

    * @return String
    */
    public String getAtomRef() {
        CMLAttribute _att_atomRef = (CMLAttribute) getAttribute("atomRef");
        if (_att_atomRef == null) {
            return null;
        }
        return ((StringAttribute)_att_atomRef).getString();
    }
    /** A reference to an atom.
    *
    * Used by bond, electron, etc.
    * --type info--
    * 
    * A reference to an existing atom.

    * @param value atomRef value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setAtomRef(String value) throws CMLRuntimeException {
            CMLAttribute _att_atomRef = null;
            try {
        		_att_atomRef = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("atomRef"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_atomRef == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : atomRef; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_atomRef);
        ((StringAttribute)_att_atomRef).setCMLValue(value);
    }
    /** A reference to a list of atoms.
    *
    * Used by bonds, electrons, atomSets, etc.
    * --type info--
    * 
    * An array of atomRefs.
    * The atomRefs
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of bonds and atomParitys.
    *  It can also be used for arrays of atomIDTypes such as in complex stereochemistry,
    *  geometrical definitions, atom groupings, etc.

    * @return CMLAttribute
    */
    public CMLAttribute getAtomRefsAttribute() {
        return (CMLAttribute) getAttribute("atomRefs");
    }
    /** A reference to a list of atoms.
    *
    * Used by bonds, electrons, atomSets, etc.
    * --type info--
    * 
    * An array of atomRefs.
    * The atomRefs
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of bonds and atomParitys.
    *  It can also be used for arrays of atomIDTypes such as in complex stereochemistry,
    *  geometrical definitions, atom groupings, etc.

    * @return String[]
    */
    public String[] getAtomRefs() {
        CMLAttribute _att_atomRefs = (CMLAttribute) getAttribute("atomRefs");
        if (_att_atomRefs == null) {
            return null;
        }
        return ((StringArrayAttribute)_att_atomRefs).getStringArray();
    }
    /** A reference to a list of atoms.
    *
    * Used by bonds, electrons, atomSets, etc.
    * --type info--
    * 
    * An array of atomRefs.
    * The atomRefs
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of bonds and atomParitys.
    *  It can also be used for arrays of atomIDTypes such as in complex stereochemistry,
    *  geometrical definitions, atom groupings, etc.

    * @param value atomRefs value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setAtomRefs(String[] value) throws CMLRuntimeException {
            CMLAttribute _att_atomRefs = null;
            try {
        		_att_atomRefs = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("atomRefs"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_atomRefs == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : atomRefs; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_atomRefs);
        ((StringArrayAttribute)_att_atomRefs).setCMLValue(value);
    }
    /** A reference to a list of atoms.
    *
    * Used by bonds, electrons, atomSets, etc.
    * --type info--
    * 
    * An array of atomRefs.
    * The atomRefs
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of bonds and atomParitys.
    *  It can also be used for arrays of atomIDTypes such as in complex stereochemistry,
    *  geometrical definitions, atom groupings, etc.

    * @param value atomRefs value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setAtomRefs(String value) throws CMLRuntimeException {
            CMLAttribute _att_atomRefs = new StringArrayAttribute((StringArrayAttribute)CMLAttributeList.getAttribute("atomRefs"));
            if (_att_atomRefs == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : atomRefs; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_atomRefs);
        ((StringArrayAttribute)_att_atomRefs).setCMLValue(value);
    }
    /** A reference to a bond.
    *
    * used by electron, etc.
    * --type info--
    * 
    * A reference to an existing bond.
    * Pattern: [A-Za-z0-9_\-]+(:[A-Za-z0-9_\-]+)?

    * @return CMLAttribute
    */
    public CMLAttribute getBondRefAttribute() {
        return (CMLAttribute) getAttribute("bondRef");
    }
    /** A reference to a bond.
    *
    * used by electron, etc.
    * --type info--
    * 
    * A reference to an existing bond.
    * Pattern: [A-Za-z0-9_\-]+(:[A-Za-z0-9_\-]+)?

    * @return String
    */
    public String getBondRef() {
        CMLAttribute _att_bondRef = (CMLAttribute) getAttribute("bondRef");
        if (_att_bondRef == null) {
            return null;
        }
        return ((StringAttribute)_att_bondRef).getString();
    }
    /** A reference to a bond.
    *
    * used by electron, etc.
    * --type info--
    * 
    * A reference to an existing bond.
    * Pattern: [A-Za-z0-9_\-]+(:[A-Za-z0-9_\-]+)?

    * @param value bondRef value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setBondRef(String value) throws CMLRuntimeException {
            CMLAttribute _att_bondRef = null;
            try {
        		_att_bondRef = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("bondRef"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_bondRef == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : bondRef; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_bondRef);
        ((StringAttribute)_att_bondRef).setCMLValue(value);
    }
    /** A reference to a list of bonds.
    *
    * Used by electrons, bondSets, etc.
    * --type info--
    * 
    * An array of references to bonds.
    * The references cannot (yet)
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of electron counts, etc.
    *  It can also be used for arrays of bondIDTypes such as in complex stereochemistry,
    *  geometrical definitions, bond groupings, etc.

    * @return CMLAttribute
    */
    public CMLAttribute getBondRefsAttribute() {
        return (CMLAttribute) getAttribute("bondRefs");
    }
    /** A reference to a list of bonds.
    *
    * Used by electrons, bondSets, etc.
    * --type info--
    * 
    * An array of references to bonds.
    * The references cannot (yet)
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of electron counts, etc.
    *  It can also be used for arrays of bondIDTypes such as in complex stereochemistry,
    *  geometrical definitions, bond groupings, etc.

    * @return String[]
    */
    public String[] getBondRefs() {
        CMLAttribute _att_bondRefs = (CMLAttribute) getAttribute("bondRefs");
        if (_att_bondRefs == null) {
            return null;
        }
        return ((StringArrayAttribute)_att_bondRefs).getStringArray();
    }
    /** A reference to a list of bonds.
    *
    * Used by electrons, bondSets, etc.
    * --type info--
    * 
    * An array of references to bonds.
    * The references cannot (yet)
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of electron counts, etc.
    *  It can also be used for arrays of bondIDTypes such as in complex stereochemistry,
    *  geometrical definitions, bond groupings, etc.

    * @param value bondRefs value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setBondRefs(String[] value) throws CMLRuntimeException {
            CMLAttribute _att_bondRefs = null;
            try {
        		_att_bondRefs = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("bondRefs"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_bondRefs == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : bondRefs; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_bondRefs);
        ((StringArrayAttribute)_att_bondRefs).setCMLValue(value);
    }
    /** A reference to a list of bonds.
    *
    * Used by electrons, bondSets, etc.
    * --type info--
    * 
    * An array of references to bonds.
    * The references cannot (yet)
    *  cannot be schema- or schematron-validated. Instances of this type will
    *  be used in array-style representation of electron counts, etc.
    *  It can also be used for arrays of bondIDTypes such as in complex stereochemistry,
    *  geometrical definitions, bond groupings, etc.

    * @param value bondRefs value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setBondRefs(String value) throws CMLRuntimeException {
            CMLAttribute _att_bondRefs = new StringArrayAttribute((StringArrayAttribute)CMLAttributeList.getAttribute("bondRefs"));
            if (_att_bondRefs == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : bondRefs; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_bondRefs);
        ((StringArrayAttribute)_att_bondRefs).setCMLValue(value);
    }
    /** The count of the object.
    *
    * No fixed semantics or default, normally integers. 
    * It is presumed that the element can be multiplied by the count value.
    * --type info--
    * 
    * A positive number.
    * Note that we also provide nonNegativeNumber with inclusive zero. The maximum number is (quite large) since 'unbounded' is more difficult to implement.

    * @return CMLAttribute
    */
    public CMLAttribute getCountAttribute() {
        return (CMLAttribute) getAttribute("count");
    }
    /** The count of the object.
    *
    * No fixed semantics or default, normally integers. 
    * It is presumed that the element can be multiplied by the count value.
    * --type info--
    * 
    * A positive number.
    * Note that we also provide nonNegativeNumber with inclusive zero. The maximum number is (quite large) since 'unbounded' is more difficult to implement.

    * @return double
    */
    public double getCount() {
        CMLAttribute _att_count = (CMLAttribute) getAttribute("count");
        if (_att_count == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_count).getDouble();
    }
    /** The count of the object.
    *
    * No fixed semantics or default, normally integers. 
    * It is presumed that the element can be multiplied by the count value.
    * --type info--
    * 
    * A positive number.
    * Note that we also provide nonNegativeNumber with inclusive zero. The maximum number is (quite large) since 'unbounded' is more difficult to implement.

    * @param value count value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setCount(double value) throws CMLRuntimeException {
            CMLAttribute _att_count = null;
            try {
        		_att_count = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("count"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_count == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : count; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_count);
        ((DoubleAttribute)_att_count).setCMLValue(value);
    }
    /** The count of the object.
    *
    * No fixed semantics or default, normally integers. 
    * It is presumed that the element can be multiplied by the count value.
    * --type info--
    * 
    * A positive number.
    * Note that we also provide nonNegativeNumber with inclusive zero. The maximum number is (quite large) since 'unbounded' is more difficult to implement.

    * @param value count value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setCount(String value) throws CMLRuntimeException {
            CMLAttribute _att_count = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("count"));
            if (_att_count == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : count; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_count);
        ((DoubleAttribute)_att_count).setCMLValue(value);
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
        } else if (name.equals("atomRef")) {
            setAtomRef(value);
        } else if (name.equals("atomRefs")) {
            setAtomRefs(value);
        } else if (name.equals("bondRef")) {
            setBondRef(value);
        } else if (name.equals("bondRefs")) {
            setBondRefs(value);
        } else if (name.equals("count")) {
            setCount(value);
        } else if (name.equals("ref")) {
            setRef(value);
        } else {
            super.addAttribute(att);
        }
    }
}
