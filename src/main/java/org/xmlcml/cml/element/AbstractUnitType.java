package org.xmlcml.cml.element;


import nu.xom.*;

import org.xmlcml.cml.base.*;
import org.xmlcml.cml.attribute.*;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractUnitType extends CMLElement {
    /** local name*/
    public final static String TAG = "unitType";
    /** constructor. */    public AbstractUnitType() {
        super("unitType");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractUnitType(AbstractUnitType old) {
        super((CMLElement) old);
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "unitType");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
    }
// attribute:   name

    /** cache */
    StringSTAttribute _att_name = null;
    /** Name of the object.
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.
    * @return CMLAttribute
    */
    public CMLAttribute getNameAttribute() {
        return (CMLAttribute) getAttribute("name");
    }
    /** Name of the object.
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.
    * @return String
    */
    public String getName() {
        StringSTAttribute att = (StringSTAttribute) this.getNameAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Name of the object.
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setName(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_name == null) {
            _att_name = (StringSTAttribute) attributeFactory.getAttribute("name", "unitType");
            if (_att_name == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : name probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_name);
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
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "unitType");
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_title);
        super.addRemove(att, value);
    }
// attribute:   parentSI

    /** cache */
    ParentSIAttribute _att_parentsi = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getParentSIAttribute() {
        return (CMLAttribute) getAttribute("parentSI");
    }
    /** null
    * @return String
    */
    public String getParentSI() {
        ParentSIAttribute att = (ParentSIAttribute) this.getParentSIAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setParentSI(String value) throws CMLRuntimeException {
        ParentSIAttribute att = null;
        if (_att_parentsi == null) {
            _att_parentsi = (ParentSIAttribute) attributeFactory.getAttribute("parentSI", "unitType");
            if (_att_parentsi == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : parentSI probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new ParentSIAttribute(_att_parentsi);
        super.addRemove(att, value);
    }
// attribute:   abbreviation

    /** cache */
    StringSTAttribute _att_abbreviation = null;
    /** Abbreviation.
    * Abbreviation for units, terms, etc.
    * @return CMLAttribute
    */
    public CMLAttribute getAbbreviationAttribute() {
        return (CMLAttribute) getAttribute("abbreviation");
    }
    /** Abbreviation.
    * Abbreviation for units, terms, etc.
    * @return String
    */
    public String getAbbreviation() {
        StringSTAttribute att = (StringSTAttribute) this.getAbbreviationAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Abbreviation.
    * Abbreviation for units, terms, etc.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setAbbreviation(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_abbreviation == null) {
            _att_abbreviation = (StringSTAttribute) attributeFactory.getAttribute("abbreviation", "unitType");
            if (_att_abbreviation == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : abbreviation probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_abbreviation);
        super.addRemove(att, value);
    }
// attribute:   preserve

    /** cache */
    BooleanSTAttribute _att_preserve = null;
    /** Is the dimension preserved during algebra.
    * No description
    * @return CMLAttribute
    */
    public CMLAttribute getPreserveAttribute() {
        return (CMLAttribute) getAttribute("preserve");
    }
    /** Is the dimension preserved during algebra.
    * No description
    * @return boolean
    */
    public boolean getPreserve() {
        BooleanSTAttribute att = (BooleanSTAttribute) this.getPreserveAttribute();
        if (att == null) {
            throw new CMLRuntimeException("boolean attribute is unset: preserve");
        }
        return att.getBoolean();
    }
    /** Is the dimension preserved during algebra.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setPreserve(String value) throws CMLRuntimeException {
        BooleanSTAttribute att = null;
        if (_att_preserve == null) {
            _att_preserve = (BooleanSTAttribute) attributeFactory.getAttribute("preserve", "unitType");
            if (_att_preserve == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : preserve probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new BooleanSTAttribute(_att_preserve);
        super.addRemove(att, value);
    }
    /** Is the dimension preserved during algebra.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setPreserve(boolean value) throws CMLRuntimeException {
        if (_att_preserve == null) {
            _att_preserve = (BooleanSTAttribute) attributeFactory.getAttribute("preserve", "unitType");
           if (_att_preserve == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : preserve probably incompatible attributeGroupName and attributeName ");
            }
        }
        BooleanSTAttribute att = new BooleanSTAttribute(_att_preserve);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   symbol

    /** cache */
    StringSTAttribute _att_symbol = null;
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return CMLAttribute
    */
    public CMLAttribute getSymbolAttribute() {
        return (CMLAttribute) getAttribute("symbol");
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return String
    */
    public String getSymbol() {
        StringSTAttribute att = (StringSTAttribute) this.getSymbolAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSymbol(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_symbol == null) {
            _att_symbol = (StringSTAttribute) attributeFactory.getAttribute("symbol", "unitType");
            if (_att_symbol == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : symbol probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_symbol);
        super.addRemove(att, value);
    }
// element:   annotation

    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @param annotation child to add
    */
    public void addAnnotation(AbstractAnnotation annotation) {
        annotation.detach();
        this.appendChild(annotation);
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return CMLElements<CMLAnnotation>
    */
    public CMLElements<CMLAnnotation> getAnnotationElements() {
        Elements elements = this.getChildElements("annotation", CML_NS);
        return new CMLElements<CMLAnnotation>(elements);
    }
// element:   dimension

    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @param dimension child to add
    */
    public void addDimension(AbstractDimension dimension) {
        dimension.detach();
        this.appendChild(dimension);
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return CMLElements<CMLDimension>
    */
    public CMLElements<CMLDimension> getDimensionElements() {
        Elements elements = this.getChildElements("dimension", CML_NS);
        return new CMLElements<CMLDimension>(elements);
    }
// element:   description

    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @param description child to add
    */
    public void addDescription(AbstractDescription description) {
        description.detach();
        this.appendChild(description);
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return CMLElements<CMLDescription>
    */
    public CMLElements<CMLDescription> getDescriptionElements() {
        Elements elements = this.getChildElements("description", CML_NS);
        return new CMLElements<CMLDescription>(elements);
    }
// element:   definition

    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @param definition child to add
    */
    public void addDefinition(AbstractDefinition definition) {
        definition.detach();
        this.appendChild(definition);
    }
    /** A symbol.
    * No semantics. However it should contain only 
    *                 ASCII characters and we may have to develop an escaping mechanism.
    *                 Used on _atomicBasisFunction_, _unit_, etc.
    * @return CMLElements<CMLDefinition>
    */
    public CMLElements<CMLDefinition> getDefinitionElements() {
        Elements elements = this.getChildElements("definition", CML_NS);
        return new CMLElements<CMLDefinition>(elements);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("name")) {
            setName(value);
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("parentSI")) {
            setParentSI(value);
        } else if (name.equals("abbreviation")) {
            setAbbreviation(value);
        } else if (name.equals("preserve")) {
            setPreserve(value);
        } else if (name.equals("symbol")) {
            setSymbol(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
