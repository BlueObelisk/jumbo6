package org.xmlcml.cml.element;


import nu.xom.*;

import org.xmlcml.cml.base.*;
import org.xmlcml.cml.attribute.*;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractDimension extends CMLElement {
    /** local name*/
    public final static String TAG = "dimension";
    /** constructor. */    public AbstractDimension() {
        super("dimension");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractDimension(AbstractDimension old) {
        super((CMLElement) old);
    }
// attribute:   dimensionBasis

    /** cache */
    StringSTAttribute _att_dimensionbasis = null;
    /** The basis of the dimension.
    * Normally taken from the seven SI types but possibly expandable.
    * @return CMLAttribute
    */
    public CMLAttribute getDimensionBasisAttribute() {
        return (CMLAttribute) getAttribute("dimensionBasis");
    }
    /** The basis of the dimension.
    * Normally taken from the seven SI types but possibly expandable.
    * @return String
    */
    public String getDimensionBasis() {
        StringSTAttribute att = (StringSTAttribute) this.getDimensionBasisAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The basis of the dimension.
    * Normally taken from the seven SI types but possibly expandable.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDimensionBasis(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_dimensionbasis == null) {
            _att_dimensionbasis = (StringSTAttribute) attributeFactory.getAttribute("dimensionBasis", "dimension");
            if (_att_dimensionbasis == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dimensionBasis probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_dimensionbasis);
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "dimension");
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
            _att_name = (StringSTAttribute) attributeFactory.getAttribute("name", "dimension");
            if (_att_name == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : name probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_name);
        super.addRemove(att, value);
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
            _att_power = (DoubleSTAttribute) attributeFactory.getAttribute("power", "dimension");
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
            _att_power = (DoubleSTAttribute) attributeFactory.getAttribute("power", "dimension");
           if (_att_power == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : power probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_power);
        super.addAttribute(att);
        att.setCMLValue(value);
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
            _att_preserve = (BooleanSTAttribute) attributeFactory.getAttribute("preserve", "dimension");
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
            _att_preserve = (BooleanSTAttribute) attributeFactory.getAttribute("preserve", "dimension");
           if (_att_preserve == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : preserve probably incompatible attributeGroupName and attributeName ");
            }
        }
        BooleanSTAttribute att = new BooleanSTAttribute(_att_preserve);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
    /** overrides addAttribute(Attribute)
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
