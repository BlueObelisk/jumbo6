package org.xmlcml.cml.element;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.DoubleAttribute;
import org.xmlcml.cml.base.StringAttribute;

/** A dimension supporting scientific unit.
*
* 
* This will be primarily used within the definition of units.\n Two dimensions are of the same type if their 'name' attributes are (case-sensitive) \nidentical. Dimensions of the same typecan be algebraically combined using the 'power' attributes.\n Normally dimensions will be aggregated and cancelled algebraically, but the 'preserve'\n attribute can be used to prevent this. Thus a velocity gradient over length can be \ndefined as:\n \n\n\n\n\n\n\nwhereas cancelling the dimensions would give:\n \n\n\n\n\n
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractDimension extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "dimension";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractDimension() {
        super("dimension");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractDimension to copy

    */

    public AbstractDimension(AbstractDimension old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("dimensionBasis", "dimensionBasis");
        attributeGroupNameTable.put("id", "id");
        attributeGroupNameTable.put("name", "name");
        attributeGroupNameTable.put("power", "power");
        attributeGroupNameTable.put("preserve", "preserve");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
    }
    /** The basis of the dimension.
    *
    * Normally taken from the seven SI types but possibly expandable.
    * --type info--
    * 
    * An angl.
    * (formally dimensionless, but useful to have units).
    * Permitted values:
    *   mass
    *   length
    *   time
    *   current
    *   amount
    *   luminosity
    *   temperature
    *   dimensionless
    *   angle

    * @return CMLAttribute
    */
    public CMLAttribute getDimensionBasisAttribute() {
        return (CMLAttribute) getAttribute("dimensionBasis");
    }
    /** The basis of the dimension.
    *
    * Normally taken from the seven SI types but possibly expandable.
    * --type info--
    * 
    * An angl.
    * (formally dimensionless, but useful to have units).
    * Permitted values:
    *   mass
    *   length
    *   time
    *   current
    *   amount
    *   luminosity
    *   temperature
    *   dimensionless
    *   angle

    * @return String
    */
    public String getDimensionBasis() {
        CMLAttribute _att_dimensionBasis = (CMLAttribute) getAttribute("dimensionBasis");
        if (_att_dimensionBasis == null) {
            return null;
        }
        return ((StringAttribute)_att_dimensionBasis).getString();
    }
    /** The basis of the dimension.
    *
    * Normally taken from the seven SI types but possibly expandable.
    * --type info--
    * 
    * An angl.
    * (formally dimensionless, but useful to have units).
    * Permitted values:
    *   mass
    *   length
    *   time
    *   current
    *   amount
    *   luminosity
    *   temperature
    *   dimensionless
    *   angle

    * @param value dimensionBasis value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setDimensionBasis(String value) throws CMLRuntimeException {
            CMLAttribute _att_dimensionBasis = null;
            try {
        		_att_dimensionBasis = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("dimensionBasis"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_dimensionBasis == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dimensionBasis; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_dimensionBasis);
        ((StringAttribute)_att_dimensionBasis).setCMLValue(value);
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
    /** Is the dimension preserved during algebra.
    *
    * 
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @return CMLAttribute
    */
    public CMLAttribute getPreserveAttribute() {
        return (CMLAttribute) getAttribute("preserve");
    }
    /** Is the dimension preserved during algebra.
    *
    * 
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @return String
    */
    public String getPreserve() {
        CMLAttribute _att_preserve = (CMLAttribute) getAttribute("preserve");
        if (_att_preserve == null) {
            return null;
        }
        return ((StringAttribute)_att_preserve).getString();
    }
    /** Is the dimension preserved during algebra.
    *
    * 
    * --type info--
    * 
    * Permitted values:
    *   true
    *   false

    * @param value preserve value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setPreserve(String value) throws CMLRuntimeException {
            CMLAttribute _att_preserve = null;
            try {
        		_att_preserve = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("preserve"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_preserve == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : preserve; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_preserve);
        ((StringAttribute)_att_preserve).setCMLValue(value);
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
        } else if (name.equals("dimensionBasis")) {
            setDimensionBasis(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("name")) {
            setName(value);
        } else if (name.equals("power")) {
            setPower(value);
        } else if (name.equals("preserve")) {
            setPreserve(value);
        } else {
            super.addAttribute(att);
        }
    }
}
