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
public abstract class AbstractUnitList extends CMLElement {
    /** local name*/
    public final static String TAG = "unitList";
    /** constructor. */    public AbstractUnitList() {
        super("unitList");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractUnitList(AbstractUnitList old) {
        super((CMLElement) old);
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
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "unitList");
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_title);
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "unitList");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
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
            _att_convention = (StringSTAttribute) attributeFactory.getAttribute("convention", "unitList");
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
            _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "unitList");
            if (_att_dictref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DictRefAttribute(_att_dictref);
        super.addRemove(att, value);
    }
// attribute:   type

    /** cache */
    StringSTAttribute _att_type = null;
    /** A reference to the type of a unit.
    * Needed to differentiate the rather unhappy
    *                 polymorphism of unitList/unit and unitList/unitType.
    * @return CMLAttribute
    */
    public CMLAttribute getTypeAttribute() {
        return (CMLAttribute) getAttribute("type");
    }
    /** A reference to the type of a unit.
    * Needed to differentiate the rather unhappy
    *                 polymorphism of unitList/unit and unitList/unitType.
    * @return String
    */
    public String getType() {
        StringSTAttribute att = (StringSTAttribute) this.getTypeAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A reference to the type of a unit.
    * Needed to differentiate the rather unhappy
    *                 polymorphism of unitList/unit and unitList/unitType.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setType(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_type == null) {
            _att_type = (StringSTAttribute) attributeFactory.getAttribute("type", "unitList");
            if (_att_type == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : type probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_type);
        super.addRemove(att, value);
    }
// attribute:   namespace

    /** cache */
    StringSTAttribute _att_namespace = null;
    /** The namespace for a data item.
    * The namespace is associated with elements such as dictionaries
    *                 and units and allows them to be referenced through free namespace prefixes.
    * @return CMLAttribute
    */
    public CMLAttribute getNamespaceAttribute() {
        return (CMLAttribute) getAttribute("namespace");
    }
    /** The namespace for a data item.
    * The namespace is associated with elements such as dictionaries
    *                 and units and allows them to be referenced through free namespace prefixes.
    * @return String
    */
    public String getNamespace() {
        StringSTAttribute att = (StringSTAttribute) this.getNamespaceAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The namespace for a data item.
    * The namespace is associated with elements such as dictionaries
    *                 and units and allows them to be referenced through free namespace prefixes.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setNamespace(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_namespace == null) {
            _att_namespace = (StringSTAttribute) attributeFactory.getAttribute("namespace", "unitList");
            if (_att_namespace == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : namespace probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_namespace);
        super.addRemove(att, value);
    }
// attribute:   siNamespace

    /** cache */
    StringSTAttribute _att_sinamespace = null;
    /** The namespace for SI Units dictionary.
    * Main use is on unitList to identify the 
    *                 dictionary holding the SI Units.
    * @return CMLAttribute
    */
    public CMLAttribute getSiNamespaceAttribute() {
        return (CMLAttribute) getAttribute("siNamespace");
    }
    /** The namespace for SI Units dictionary.
    * Main use is on unitList to identify the 
    *                 dictionary holding the SI Units.
    * @return String
    */
    public String getSiNamespace() {
        StringSTAttribute att = (StringSTAttribute) this.getSiNamespaceAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The namespace for SI Units dictionary.
    * Main use is on unitList to identify the 
    *                 dictionary holding the SI Units.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSiNamespace(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_sinamespace == null) {
            _att_sinamespace = (StringSTAttribute) attributeFactory.getAttribute("siNamespace", "unitList");
            if (_att_sinamespace == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : siNamespace probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_sinamespace);
        super.addRemove(att, value);
    }
// attribute:   dictionaryPrefix

    /** cache */
    StringSTAttribute _att_dictionaryprefix = null;
    /** The namespacePrefix for a data item.
    * The dictionaryPrefix is associated with elements 
    *                 such as dictionaries and units and allows them to be referenced namespaces.
    *                 The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *                 occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *                 be identified and fixed.
    * @return CMLAttribute
    */
    public CMLAttribute getDictionaryPrefixAttribute() {
        return (CMLAttribute) getAttribute("dictionaryPrefix");
    }
    /** The namespacePrefix for a data item.
    * The dictionaryPrefix is associated with elements 
    *                 such as dictionaries and units and allows them to be referenced namespaces.
    *                 The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *                 occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *                 be identified and fixed.
    * @return String
    */
    public String getDictionaryPrefix() {
        StringSTAttribute att = (StringSTAttribute) this.getDictionaryPrefixAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The namespacePrefix for a data item.
    * The dictionaryPrefix is associated with elements 
    *                 such as dictionaries and units and allows them to be referenced namespaces.
    *                 The dictionaryPrefix is normally unbound but it may be necessary to hardcode them
    *                 occasionally. Thus if a value is fixed (e.g. "xsd:double") the prefix must
    *                 be identified and fixed.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDictionaryPrefix(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_dictionaryprefix == null) {
            _att_dictionaryprefix = (StringSTAttribute) attributeFactory.getAttribute("dictionaryPrefix", "unitList");
            if (_att_dictionaryprefix == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictionaryPrefix probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_dictionaryprefix);
        super.addRemove(att, value);
    }
// attribute:   href

    /** cache */
    StringSTAttribute _att_href = null;
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @return CMLAttribute
    */
    public CMLAttribute getHrefAttribute() {
        return (CMLAttribute) getAttribute("href");
    }
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @return String
    */
    public String getHref() {
        StringSTAttribute att = (StringSTAttribute) this.getHrefAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setHref(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_href == null) {
            _att_href = (StringSTAttribute) attributeFactory.getAttribute("href", "unitList");
            if (_att_href == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : href probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_href);
        super.addRemove(att, value);
    }
// element:   metadataList

    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @param metadataList child to add
    */
    public void addMetadataList(AbstractMetadataList metadataList) {
        metadataList.detach();
        this.appendChild(metadataList);
    }
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @return CMLElements<CMLMetadataList>
    */
    public CMLElements<CMLMetadataList> getMetadataListElements() {
        Elements elements = this.getChildElements("metadataList", CML_NS);
        return new CMLElements<CMLMetadataList>(elements);
    }
// element:   unitType

    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @param unitType child to add
    */
    public void addUnitType(AbstractUnitType unitType) {
        unitType.detach();
        this.appendChild(unitType);
    }
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @return CMLElements<CMLUnitType>
    */
    public CMLElements<CMLUnitType> getUnitTypeElements() {
        Elements elements = this.getChildElements("unitType", CML_NS);
        return new CMLElements<CMLUnitType>(elements);
    }
// element:   unit

    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @param unit child to add
    */
    public void addUnit(AbstractUnit unit) {
        unit.detach();
        this.appendChild(unit);
    }
    /** address of a resource.
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    *             address to be contained within the same file. We can anticipate that
    *             better mechanisms will arise - perhaps through XMLCatalogs.
    *             At least it works at present.
    * @return CMLElements<CMLUnit>
    */
    public CMLElements<CMLUnit> getUnitElements() {
        Elements elements = this.getChildElements("unit", CML_NS);
        return new CMLElements<CMLUnit>(elements);
    }
    /** overrides addAttribute(Attribute)
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
