package org.xmlcml.cml.element;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Elements;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.DoubleAttribute;
import org.xmlcml.cml.base.StringAttribute;

/** Power of unit used to create new one.
*
* 
* \n Only allowed on child units
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractUnit extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "unit";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractUnit() {
        super("unit");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractUnit to copy

    */

    public AbstractUnit(AbstractUnit old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("units", "units");
        attributeGroupNameTable.put("title", "title");
        attributeGroupNameTable.put("abbreviation", "abbreviation");
        attributeGroupNameTable.put("symbol", "symbol");
        attributeGroupNameTable.put("name", "name");
        attributeGroupNameTable.put("parentSI", "parentSI");
        attributeGroupNameTable.put("isSI", "isSI");
        attributeGroupNameTable.put("unitType", "unitType");
        attributeGroupNameTable.put("multiplierToData", "multiplierToData");
        attributeGroupNameTable.put("multiplierToSI", "multiplierToSI");
        attributeGroupNameTable.put("constantToSI", "constantToSI");
        attributeGroupNameTable.put("power", "power");
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
    /** Abbreviation.
    *
    * Abbreviation for units, terms, etc.

    * @return CMLAttribute
    */
    public CMLAttribute getAbbreviationAttribute() {
        return (CMLAttribute) getAttribute("abbreviation");
    }
    /** Abbreviation.
    *
    * Abbreviation for units, terms, etc.

    * @return String
    */
    public String getAbbreviation() {
        CMLAttribute _att_abbreviation = (CMLAttribute) getAttribute("abbreviation");
        if (_att_abbreviation == null) {
            return null;
        }
        return ((StringAttribute)_att_abbreviation).getString();
    }
    /** Abbreviation.
    *
    * Abbreviation for units, terms, etc.

    * @param value abbreviation value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setAbbreviation(String value) throws CMLRuntimeException {
            CMLAttribute _att_abbreviation = null;
            try {
        		_att_abbreviation = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("abbreviation"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_abbreviation == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : abbreviation; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_abbreviation);
        ((StringAttribute)_att_abbreviation).setCMLValue(value);
    }
    /** A symbol.
    *
    * No semantics. However it should contain only 
    * ASCII characters and we may have to develop an escaping mechanism.
    *  Used on _atomicBasisFunction_, _unit_, etc.

    * @return CMLAttribute
    */
    public CMLAttribute getSymbolAttribute() {
        return (CMLAttribute) getAttribute("symbol");
    }
    /** A symbol.
    *
    * No semantics. However it should contain only 
    * ASCII characters and we may have to develop an escaping mechanism.
    *  Used on _atomicBasisFunction_, _unit_, etc.

    * @return String
    */
    public String getSymbol() {
        CMLAttribute _att_symbol = (CMLAttribute) getAttribute("symbol");
        if (_att_symbol == null) {
            return null;
        }
        return ((StringAttribute)_att_symbol).getString();
    }
    /** A symbol.
    *
    * No semantics. However it should contain only 
    * ASCII characters and we may have to develop an escaping mechanism.
    *  Used on _atomicBasisFunction_, _unit_, etc.

    * @param value symbol value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setSymbol(String value) throws CMLRuntimeException {
            CMLAttribute _att_symbol = null;
            try {
        		_att_symbol = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("symbol"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_symbol == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : symbol; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_symbol);
        ((StringAttribute)_att_symbol).setCMLValue(value);
    }
    /** Name of the object.
    *
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.

    * @return CMLAttribute
    */
    public CMLAttribute getNameAttribute() {
        return (CMLAttribute) getAttribute("name");
    }
    /** Name of the object.
    *
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.

    * @return String
    */
    public String getName() {
        CMLAttribute _att_name = (CMLAttribute) getAttribute("name");
        if (_att_name == null) {
            return null;
        }
        return ((StringAttribute)_att_name).getString();
    }
    /** Name of the object.
    *
    * A string by which the object is known. Often a required attribute. The may or may not be a semi-controlled vocabulary.

    * @param value name value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setName(String value) throws CMLRuntimeException {
            CMLAttribute _att_name = null;
            try {
        		_att_name = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("name"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_name == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : name; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_name);
        ((StringAttribute)_att_name).setCMLValue(value);
    }
    /** A dictRef-like reference to the id of the parent SI unit.
    *
    * This parent should occur in this or another dictionary 
    * and be accessible through the dictRef mechanism. This attribute is forbidden 
    * for SI Units themselves. The mechanism holds for base SI units (7) and 
    * all compound (derived) units made by combinations of base Units.
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
    public CMLAttribute getParentSIAttribute() {
        return (CMLAttribute) getAttribute("parentSI");
    }
    /** A dictRef-like reference to the id of the parent SI unit.
    *
    * This parent should occur in this or another dictionary 
    * and be accessible through the dictRef mechanism. This attribute is forbidden 
    * for SI Units themselves. The mechanism holds for base SI units (7) and 
    * all compound (derived) units made by combinations of base Units.
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
    public String getParentSI() {
        CMLAttribute _att_parentSI = (CMLAttribute) getAttribute("parentSI");
        if (_att_parentSI == null) {
            return null;
        }
        return ((StringAttribute)_att_parentSI).getString();
    }
    /** A dictRef-like reference to the id of the parent SI unit.
    *
    * This parent should occur in this or another dictionary 
    * and be accessible through the dictRef mechanism. This attribute is forbidden 
    * for SI Units themselves. The mechanism holds for base SI units (7) and 
    * all compound (derived) units made by combinations of base Units.
    * --type info--
    * 
    * An XML QName with required prefix.
    * 
    *  The namespace prefix must start with an alpha character
    *  and can only contain alphanumeric and '_'. The suffix can 
    * have characters from the XML ID specification 
    * (alphanumeric, '_', '.' and '-'
    * Pattern: [A-Za-z][A-Za-z0-9_]*:[A-Za-z][A-Za-z0-9_\.\-]*

    * @param value parentSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setParentSI(String value) throws CMLRuntimeException {
            CMLAttribute _att_parentSI = null;
            try {
        		_att_parentSI = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("parentSI"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_parentSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : parentSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_parentSI);
        ((StringAttribute)_att_parentSI).setCMLValue(value);
    }
    /** indicates whether a unit is an SI or derived SI unit.
    *
    * required on SI unit elements with value 'true'. 
    * Optional on other units with attribute 'false'. A unitList should contain either
    *  SI units or non-SI units but not both.
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @return CMLAttribute
    */
    public CMLAttribute getIsSIAttribute() {
        return (CMLAttribute) getAttribute("isSI");
    }
    /** indicates whether a unit is an SI or derived SI unit.
    *
    * required on SI unit elements with value 'true'. 
    * Optional on other units with attribute 'false'. A unitList should contain either
    *  SI units or non-SI units but not both.
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @return String
    */
    public String getIsSI() {
        CMLAttribute _att_isSI = (CMLAttribute) getAttribute("isSI");
        if (_att_isSI == null) {
            return null;
        }
        return ((StringAttribute)_att_isSI).getString();
    }
    /** indicates whether a unit is an SI or derived SI unit.
    *
    * required on SI unit elements with value 'true'. 
    * Optional on other units with attribute 'false'. A unitList should contain either
    *  SI units or non-SI units but not both.
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @param value isSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setIsSI(String value) throws CMLRuntimeException {
            CMLAttribute _att_isSI = null;
            try {
        		_att_isSI = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("isSI"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_isSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : isSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_isSI);
        ((StringAttribute)_att_isSI).setCMLValue(value);
    }
    /** A reference to the type of a unit.
    *
    * Used in defining the unit and doing 
    * symbolic algebra on the dimensionality.

    * @return CMLAttribute
    */
    public CMLAttribute getUnitTypeAttribute() {
        return (CMLAttribute) getAttribute("unitType");
    }
    /** A reference to the type of a unit.
    *
    * Used in defining the unit and doing 
    * symbolic algebra on the dimensionality.

    * @return String
    */
    public String getUnitType() {
        CMLAttribute _att_unitType = (CMLAttribute) getAttribute("unitType");
        if (_att_unitType == null) {
            return null;
        }
        return ((StringAttribute)_att_unitType).getString();
    }
    /** A reference to the type of a unit.
    *
    * Used in defining the unit and doing 
    * symbolic algebra on the dimensionality.

    * @param value unitType value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setUnitType(String value) throws CMLRuntimeException {
            CMLAttribute _att_unitType = null;
            try {
        		_att_unitType = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("unitType"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_unitType == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitType; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_unitType);
        ((StringAttribute)_att_unitType).setCMLValue(value);
    }
    /** The scale by which to multiply raw data or a unit.
    *
    * The scale is applied *before* adding any constant.
    *  The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    * a user-defined unit.

    * @return CMLAttribute
    */
    public CMLAttribute getMultiplierToDataAttribute() {
        return (CMLAttribute) getAttribute("multiplierToData");
    }
    /** The scale by which to multiply raw data or a unit.
    *
    * The scale is applied *before* adding any constant.
    *  The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    * a user-defined unit.

    * @return double
    */
    public double getMultiplierToData() {
        CMLAttribute _att_multiplierToData = (CMLAttribute) getAttribute("multiplierToData");
        if (_att_multiplierToData == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_multiplierToData).getDouble();
    }
    /** The scale by which to multiply raw data or a unit.
    *
    * The scale is applied *before* adding any constant.
    *  The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    * a user-defined unit.

    * @param value multiplierToData value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setMultiplierToData(double value) throws CMLRuntimeException {
            CMLAttribute _att_multiplierToData = null;
            try {
        		_att_multiplierToData = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("multiplierToData"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_multiplierToData == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToData; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_multiplierToData);
        ((DoubleAttribute)_att_multiplierToData).setCMLValue(value);
    }
    /** The scale by which to multiply raw data or a unit.
    *
    * The scale is applied *before* adding any constant.
    *  The attribute may be found on a data item (scalar, array, matrix, etc.) or 
    * a user-defined unit.

    * @param value multiplierToData value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setMultiplierToData(String value) throws CMLRuntimeException {
            CMLAttribute _att_multiplierToData = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("multiplierToData"));
            if (_att_multiplierToData == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToData; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_multiplierToData);
        ((DoubleAttribute)_att_multiplierToData).setCMLValue(value);
    }
    /** Multiplier to generate SI equivalent.
    *
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.

    * @return CMLAttribute
    */
    public CMLAttribute getMultiplierToSIAttribute() {
        return (CMLAttribute) getAttribute("multiplierToSI");
    }
    /** Multiplier to generate SI equivalent.
    *
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.

    * @return double
    */
    public double getMultiplierToSI() {
        CMLAttribute _att_multiplierToSI = (CMLAttribute) getAttribute("multiplierToSI");
        if (_att_multiplierToSI == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_multiplierToSI).getDouble();
    }
    /** Multiplier to generate SI equivalent.
    *
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.

    * @param value multiplierToSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setMultiplierToSI(double value) throws CMLRuntimeException {
            CMLAttribute _att_multiplierToSI = null;
            try {
        		_att_multiplierToSI = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("multiplierToSI"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_multiplierToSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_multiplierToSI);
        ((DoubleAttribute)_att_multiplierToSI).setCMLValue(value);
    }
    /** Multiplier to generate SI equivalent.
    *
    * The factor by which the non-SI unit should be multiplied to convert a quantity to its representation in SI Units. This is applied *before* _constantToSI_. Necessarily unity for SI unit.

    * @param value multiplierToSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setMultiplierToSI(String value) throws CMLRuntimeException {
            CMLAttribute _att_multiplierToSI = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("multiplierToSI"));
            if (_att_multiplierToSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_multiplierToSI);
        ((DoubleAttribute)_att_multiplierToSI).setCMLValue(value);
    }
    /** Additive constant to generate SI equivalent.
    *
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.

    * @return CMLAttribute
    */
    public CMLAttribute getConstantToSIAttribute() {
        return (CMLAttribute) getAttribute("constantToSI");
    }
    /** Additive constant to generate SI equivalent.
    *
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.

    * @return double
    */
    public double getConstantToSI() {
        CMLAttribute _att_constantToSI = (CMLAttribute) getAttribute("constantToSI");
        if (_att_constantToSI == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_constantToSI).getDouble();
    }
    /** Additive constant to generate SI equivalent.
    *
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.

    * @param value constantToSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setConstantToSI(double value) throws CMLRuntimeException {
            CMLAttribute _att_constantToSI = null;
            try {
        		_att_constantToSI = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("constantToSI"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_constantToSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : constantToSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_constantToSI);
        ((DoubleAttribute)_att_constantToSI).setCMLValue(value);
    }
    /** Additive constant to generate SI equivalent.
    *
    * The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.

    * @param value constantToSI value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setConstantToSI(String value) throws CMLRuntimeException {
            CMLAttribute _att_constantToSI = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("constantToSI"));
            if (_att_constantToSI == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : constantToSI; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_constantToSI);
        ((DoubleAttribute)_att_constantToSI).setCMLValue(value);
    }
    /** The power to which a dimension should be raised.
    *
    * Normally an integer. Must be included, even if unity.

    * @return CMLAttribute
    */
    public CMLAttribute getPowerAttribute() {
        return (CMLAttribute) getAttribute("power");
    }
    /** The power to which a dimension should be raised.
    *
    * Normally an integer. Must be included, even if unity.

    * @return double
    */
    public double getPower() {
        CMLAttribute _att_power = (CMLAttribute) getAttribute("power");
        if (_att_power == null) {
            return Double.NaN;
        }
        return ((DoubleAttribute)_att_power).getDouble();
    }
    /** The power to which a dimension should be raised.
    *
    * Normally an integer. Must be included, even if unity.

    * @param value power value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setPower(double value) throws CMLRuntimeException {
            CMLAttribute _att_power = null;
            try {
        		_att_power = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("power"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_power == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : power; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_power);
        ((DoubleAttribute)_att_power).setCMLValue(value);
    }
    /** The power to which a dimension should be raised.
    *
    * Normally an integer. Must be included, even if unity.

    * @param value power value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    private void setPower(String value) throws CMLRuntimeException {
            CMLAttribute _att_power = new DoubleAttribute((DoubleAttribute)CMLAttributeList.getAttribute("power"));
            if (_att_power == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : power; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_power);
        ((DoubleAttribute)_att_power).setCMLValue(value);
    }
    /** add unit element.
    *
    * @param unitType unitType child to add

    */
    public void addUnitType(AbstractUnitType unitType) {
        unitType.detach();
        this.appendChild(unitType);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLUnitType>
    */
    public CMLElements<CMLUnitType> getUnitTypeElements() {
        Elements elements = this.getChildElements("unitType", CML_NS);
        return new CMLElements<CMLUnitType>(elements);
    }
    /** add unit element.
    *
    * @param annotation annotation child to add

    */
    public void addAnnotation(AbstractAnnotation annotation) {
        annotation.detach();
        this.appendChild(annotation);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLAnnotation>
    */
    public CMLElements<CMLAnnotation> getAnnotationElements() {
        Elements elements = this.getChildElements("annotation", CML_NS);
        return new CMLElements<CMLAnnotation>(elements);
    }
    /** add unit element.
    *
    * @param metadataList metadataList child to add

    */
    public void addMetadataList(AbstractMetadataList metadataList) {
        metadataList.detach();
        this.appendChild(metadataList);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLMetadataList>
    */
    public CMLElements<CMLMetadataList> getMetadataListElements() {
        Elements elements = this.getChildElements("metadataList", CML_NS);
        return new CMLElements<CMLMetadataList>(elements);
    }
    /** add unit element.
    *
    * @param unit unit child to add

    */
    public void addUnit(AbstractUnit unit) {
        unit.detach();
        this.appendChild(unit);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLUnit>
    */
    public CMLElements<CMLUnit> getUnitElements() {
        Elements elements = this.getChildElements("unit", CML_NS);
        return new CMLElements<CMLUnit>(elements);
    }
    /** add unit element.
    *
    * @param definition definition child to add

    */
    public void addDefinition(AbstractDefinition definition) {
        definition.detach();
        this.appendChild(definition);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLDefinition>
    */
    public CMLElements<CMLDefinition> getDefinitionElements() {
        Elements elements = this.getChildElements("definition", CML_NS);
        return new CMLElements<CMLDefinition>(elements);
    }
    /** add unit element.
    *
    * @param description description child to add

    */
    public void addDescription(AbstractDescription description) {
        description.detach();
        this.appendChild(description);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLDescription>
    */
    public CMLElements<CMLDescription> getDescriptionElements() {
        Elements elements = this.getChildElements("description", CML_NS);
        return new CMLElements<CMLDescription>(elements);
    }
    /** add unit element.
    *
    * @param metadata metadata child to add

    */
    public void addMetadata(AbstractMetadata metadata) {
        metadata.detach();
        this.appendChild(metadata);
    }
    /** get unit child elements .
    *
    * @return CMLElements<CMLMetadata>
    */
    public CMLElements<CMLMetadata> getMetadataElements() {
        Elements elements = this.getChildElements("metadata", CML_NS);
        return new CMLElements<CMLMetadata>(elements);
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