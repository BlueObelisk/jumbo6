package org.xmlcml.cml.element;


import nu.xom.Attribute;
import nu.xom.Elements;

import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.attribute.ParentSIAttribute;
import org.xmlcml.cml.attribute.UnitTypeAttribute;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.BooleanSTAttribute;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.DoubleSTAttribute;
import org.xmlcml.cml.base.StringSTAttribute;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractUnit extends CMLElement {
    /** local name*/
    public final static String TAG = "unit";
    /** constructor. */    public AbstractUnit() {
        super("unit");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractUnit(AbstractUnit old) {
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "unit");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
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
            _att_units = (UnitsAttribute) attributeFactory.getAttribute("units", "unit");
            if (_att_units == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : units probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new UnitsAttribute(_att_units);
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
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "unit");
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_title);
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
            _att_abbreviation = (StringSTAttribute) attributeFactory.getAttribute("abbreviation", "unit");
            if (_att_abbreviation == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : abbreviation probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_abbreviation);
        super.addRemove(att, value);
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
            _att_symbol = (StringSTAttribute) attributeFactory.getAttribute("symbol", "unit");
            if (_att_symbol == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : symbol probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_symbol);
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
            _att_name = (StringSTAttribute) attributeFactory.getAttribute("name", "unit");
            if (_att_name == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : name probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_name);
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
            _att_parentsi = (ParentSIAttribute) attributeFactory.getAttribute("parentSI", "unit");
            if (_att_parentsi == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : parentSI probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new ParentSIAttribute(_att_parentsi);
        super.addRemove(att, value);
    }
// attribute:   isSI

    /** cache */
    BooleanSTAttribute _att_issi = null;
    /** indicates whether a unit is an SI or derived SI unit.
    * required on SI unit elements with value 'true'. 
    *                 Optional on other units with attribute 'false'. A unitList should contain either
    *                 SI units or non-SI units but not both.
    * @return CMLAttribute
    */
    public CMLAttribute getIsSIAttribute() {
        return (CMLAttribute) getAttribute("isSI");
    }
    /** indicates whether a unit is an SI or derived SI unit.
    * required on SI unit elements with value 'true'. 
    *                 Optional on other units with attribute 'false'. A unitList should contain either
    *                 SI units or non-SI units but not both.
    * @return boolean
    */
    public boolean getIsSI() {
        BooleanSTAttribute att = (BooleanSTAttribute) this.getIsSIAttribute();
        if (att == null) {
            throw new CMLRuntimeException("boolean attribute is unset: isSI");
        }
        return att.getBoolean();
    }
    /** indicates whether a unit is an SI or derived SI unit.
    * required on SI unit elements with value 'true'. 
    *                 Optional on other units with attribute 'false'. A unitList should contain either
    *                 SI units or non-SI units but not both.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setIsSI(String value) throws CMLRuntimeException {
        BooleanSTAttribute att = null;
        if (_att_issi == null) {
            _att_issi = (BooleanSTAttribute) attributeFactory.getAttribute("isSI", "unit");
            if (_att_issi == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : isSI probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new BooleanSTAttribute(_att_issi);
        super.addRemove(att, value);
    }
    /** indicates whether a unit is an SI or derived SI unit.
    * required on SI unit elements with value 'true'. 
    *                 Optional on other units with attribute 'false'. A unitList should contain either
    *                 SI units or non-SI units but not both.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setIsSI(boolean value) throws CMLRuntimeException {
        if (_att_issi == null) {
            _att_issi = (BooleanSTAttribute) attributeFactory.getAttribute("isSI", "unit");
           if (_att_issi == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : isSI probably incompatible attributeGroupName and attributeName ");
            }
        }
        BooleanSTAttribute att = new BooleanSTAttribute(_att_issi);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   unitType

    /** cache */
    UnitTypeAttribute _att_unittype = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getUnitTypeAttribute() {
        return (CMLAttribute) getAttribute("unitType");
    }
    /** null
    * @return String
    */
    public String getUnitType() {
        UnitTypeAttribute att = (UnitTypeAttribute) this.getUnitTypeAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setUnitType(String value) throws CMLRuntimeException {
        UnitTypeAttribute att = null;
        if (_att_unittype == null) {
            _att_unittype = (UnitTypeAttribute) attributeFactory.getAttribute("unitType", "unit");
            if (_att_unittype == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitType probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new UnitTypeAttribute(_att_unittype);
        super.addRemove(att, value);
    }
// attribute:   multiplierToData

    /** cache */
    DoubleSTAttribute _att_multipliertodata = null;
    /** The scale by which to multiply raw data or a unit.
    * The scale is applied *before* adding any constant.
    *                 The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    *                 a user-defined unit.
    * @return CMLAttribute
    */
    public CMLAttribute getMultiplierToDataAttribute() {
        return (CMLAttribute) getAttribute("multiplierToData");
    }
    /** The scale by which to multiply raw data or a unit.
    * The scale is applied *before* adding any constant.
    *                 The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    *                 a user-defined unit.
    * @return double
    */
    public double getMultiplierToData() {
        DoubleSTAttribute att = (DoubleSTAttribute) this.getMultiplierToDataAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return att.getDouble();
    }
    /** The scale by which to multiply raw data or a unit.
    * The scale is applied *before* adding any constant.
    *                 The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    *                 a user-defined unit.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMultiplierToData(String value) throws CMLRuntimeException {
        DoubleSTAttribute att = null;
        if (_att_multipliertodata == null) {
            _att_multipliertodata = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToData", "unit");
            if (_att_multipliertodata == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToData probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleSTAttribute(_att_multipliertodata);
        super.addRemove(att, value);
    }
    /** The scale by which to multiply raw data or a unit.
    * The scale is applied *before* adding any constant.
    *                 The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    *                 a user-defined unit.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMultiplierToData(double value) throws CMLRuntimeException {
        if (_att_multipliertodata == null) {
            _att_multipliertodata = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToData", "unit");
           if (_att_multipliertodata == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToData probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_multipliertodata);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   multiplierToSI

    /** cache */
    DoubleSTAttribute _att_multipliertosi = null;
    /** Multiplier to generate SI equivalent.
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.
    * @return CMLAttribute
    */
    public CMLAttribute getMultiplierToSIAttribute() {
        return (CMLAttribute) getAttribute("multiplierToSI");
    }
    /** Multiplier to generate SI equivalent.
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.
    * @return double
    */
    public double getMultiplierToSI() {
        DoubleSTAttribute att = (DoubleSTAttribute) this.getMultiplierToSIAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return att.getDouble();
    }
    /** Multiplier to generate SI equivalent.
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMultiplierToSI(String value) throws CMLRuntimeException {
        DoubleSTAttribute att = null;
        if (_att_multipliertosi == null) {
            _att_multipliertosi = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToSI", "unit");
            if (_att_multipliertosi == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToSI probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleSTAttribute(_att_multipliertosi);
        super.addRemove(att, value);
    }
    /** Multiplier to generate SI equivalent.
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMultiplierToSI(double value) throws CMLRuntimeException {
        if (_att_multipliertosi == null) {
            _att_multipliertosi = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToSI", "unit");
           if (_att_multipliertosi == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToSI probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_multipliertosi);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   constantToSI

    /** cache */
    DoubleSTAttribute _att_constanttosi = null;
    /** Additive constant to generate SI equivalent.
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
    * @return CMLAttribute
    */
    public CMLAttribute getConstantToSIAttribute() {
        return (CMLAttribute) getAttribute("constantToSI");
    }
    /** Additive constant to generate SI equivalent.
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
    * @return double
    */
    public double getConstantToSI() {
        DoubleSTAttribute att = (DoubleSTAttribute) this.getConstantToSIAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return att.getDouble();
    }
    /** Additive constant to generate SI equivalent.
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setConstantToSI(String value) throws CMLRuntimeException {
        DoubleSTAttribute att = null;
        if (_att_constanttosi == null) {
            _att_constanttosi = (DoubleSTAttribute) attributeFactory.getAttribute("constantToSI", "unit");
            if (_att_constanttosi == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : constantToSI probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleSTAttribute(_att_constanttosi);
        super.addRemove(att, value);
    }
    /** Additive constant to generate SI equivalent.
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setConstantToSI(double value) throws CMLRuntimeException {
        if (_att_constanttosi == null) {
            _att_constanttosi = (DoubleSTAttribute) attributeFactory.getAttribute("constantToSI", "unit");
           if (_att_constanttosi == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : constantToSI probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_constanttosi);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   power

    /** cache */
    DoubleSTAttribute _att_power = null;
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLAttribute
    */
    public CMLAttribute getPowerAttribute() {
        return (CMLAttribute) getAttribute("power");
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return double
    */
    public double getPower() {
        DoubleSTAttribute att = (DoubleSTAttribute) this.getPowerAttribute();
        if (att == null) {
            return Double.NaN;
        }
        return att.getDouble();
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setPower(String value) throws CMLRuntimeException {
        DoubleSTAttribute att = null;
        if (_att_power == null) {
            _att_power = (DoubleSTAttribute) attributeFactory.getAttribute("power", "unit");
            if (_att_power == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : power probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleSTAttribute(_att_power);
        super.addRemove(att, value);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setPower(double value) throws CMLRuntimeException {
        if (_att_power == null) {
            _att_power = (DoubleSTAttribute) attributeFactory.getAttribute("power", "unit");
           if (_att_power == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : power probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_power);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// element:   metadata

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param metadata child to add
    */
    public void addMetadata(AbstractMetadata metadata) {
        metadata.detach();
        this.appendChild(metadata);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLMetadata>
    */
    public CMLElements<CMLMetadata> getMetadataElements() {
        Elements elements = this.getChildElements("metadata", CML_NS);
        return new CMLElements<CMLMetadata>(elements);
    }
// element:   metadataList

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param metadataList child to add
    */
    public void addMetadataList(AbstractMetadataList metadataList) {
        metadataList.detach();
        this.appendChild(metadataList);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLMetadataList>
    */
    public CMLElements<CMLMetadataList> getMetadataListElements() {
        Elements elements = this.getChildElements("metadataList", CML_NS);
        return new CMLElements<CMLMetadataList>(elements);
    }
// element:   description

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param description child to add
    */
    public void addDescription(AbstractDescription description) {
        description.detach();
        this.appendChild(description);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLDescription>
    */
    public CMLElements<CMLDescription> getDescriptionElements() {
        Elements elements = this.getChildElements("description", CML_NS);
        return new CMLElements<CMLDescription>(elements);
    }
// element:   annotation

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param annotation child to add
    */
    public void addAnnotation(AbstractAnnotation annotation) {
        annotation.detach();
        this.appendChild(annotation);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLAnnotation>
    */
    public CMLElements<CMLAnnotation> getAnnotationElements() {
        Elements elements = this.getChildElements("annotation", CML_NS);
        return new CMLElements<CMLAnnotation>(elements);
    }
// element:   definition

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param definition child to add
    */
    public void addDefinition(AbstractDefinition definition) {
        definition.detach();
        this.appendChild(definition);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLDefinition>
    */
    public CMLElements<CMLDefinition> getDefinitionElements() {
        Elements elements = this.getChildElements("definition", CML_NS);
        return new CMLElements<CMLDefinition>(elements);
    }
// element:   unit

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param unit child to add
    */
    public void addUnit(AbstractUnit unit) {
        unit.detach();
        this.appendChild(unit);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLUnit>
    */
    public CMLElements<CMLUnit> getUnitElements() {
        Elements elements = this.getChildElements("unit", CML_NS);
        return new CMLElements<CMLUnit>(elements);
    }
// element:   unitType

    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @param unitType child to add
    */
    public void addUnitType(AbstractUnitType unitType) {
        unitType.detach();
        this.appendChild(unitType);
    }
    /** The power to which a dimension should be raised.
    * Normally an integer. Must be included, even if unity.
    * @return CMLElements<CMLUnitType>
    */
    public CMLElements<CMLUnitType> getUnitTypeElements() {
        Elements elements = this.getChildElements("unitType", CML_NS);
        return new CMLElements<CMLUnitType>(elements);
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
        } else if (name.equals("units")) {
            setUnits(value);
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("abbreviation")) {
            setAbbreviation(value);
        } else if (name.equals("symbol")) {
            setSymbol(value);
        } else if (name.equals("name")) {
            setName(value);
        } else if (name.equals("parentSI")) {
            setParentSI(value);
        } else if (name.equals("isSI")) {
            setIsSI(value);
        } else if (name.equals("unitType")) {
            setUnitType(value);
        } else if (name.equals("multiplierToData")) {
            setMultiplierToData(value);
        } else if (name.equals("multiplierToSI")) {
            setMultiplierToSI(value);
        } else if (name.equals("constantToSI")) {
            setConstantToSI(value);
        } else if (name.equals("power")) {
            setPower(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
