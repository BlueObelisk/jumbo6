package org.xmlcml.cml.element;


import nu.xom.Attribute;

import org.xmlcml.cml.attribute.DelimiterAttribute;
import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.attribute.RefAttribute;
import org.xmlcml.cml.attribute.UnitTypeAttribute;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.DoubleArraySTAttribute;
import org.xmlcml.cml.base.DoubleSTAttribute;
import org.xmlcml.cml.base.IntSTAttribute;
import org.xmlcml.cml.base.StringSTAttribute;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractArray extends CMLElement {
    /** local name*/
    public final static String TAG = "array";
    /** constructor. */    public AbstractArray() {
        super("array");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractArray(AbstractArray old) {
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
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "array");
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
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "array");
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
            _att_convention = (StringSTAttribute) attributeFactory.getAttribute("convention", "array");
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
            _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "array");
            if (_att_dictref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DictRefAttribute(_att_dictref);
        super.addRemove(att, value);
    }
// attribute:   dataType

    /** cache */
    StringSTAttribute _att_datatype = null;
    /** The data type of the object.
    * Normally applied to scalar/array 
    *                 objects but may extend to more complex one.
    * @return CMLAttribute
    */
    public CMLAttribute getDataTypeAttribute() {
        return (CMLAttribute) getAttribute("dataType");
    }
    /** The data type of the object.
    * Normally applied to scalar/array 
    *                 objects but may extend to more complex one.
    * @return String
    */
    public String getDataType() {
        StringSTAttribute att = (StringSTAttribute) this.getDataTypeAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The data type of the object.
    * Normally applied to scalar/array 
    *                 objects but may extend to more complex one.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDataType(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_datatype == null) {
            _att_datatype = (StringSTAttribute) attributeFactory.getAttribute("dataType", "array");
            if (_att_datatype == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dataType probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_datatype);
        super.addRemove(att, value);
    }
// attribute:   errorValueArray

    /** cache */
    DoubleArraySTAttribute _att_errorvaluearray = null;
    /** Array of error values.
    * Reports the author's estimate of 
    * 					the error in an array of values. Only meaningful for 
    * 					dataTypes mapping to real number.
    * @return CMLAttribute
    */
    public CMLAttribute getErrorValueArrayAttribute() {
        return (CMLAttribute) getAttribute("errorValueArray");
    }
    /** Array of error values.
    * Reports the author's estimate of 
    * 					the error in an array of values. Only meaningful for 
    * 					dataTypes mapping to real number.
    * @return double[]
    */
    public double[] getErrorValueArray() {
        DoubleArraySTAttribute att = (DoubleArraySTAttribute) this.getErrorValueArrayAttribute();
        if (att == null) {
            return null;
        }
        return att.getDoubleArray();
    }
    /** Array of error values.
    * Reports the author's estimate of 
    * 					the error in an array of values. Only meaningful for 
    * 					dataTypes mapping to real number.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setErrorValueArray(String value) throws CMLRuntimeException {
        DoubleArraySTAttribute att = null;
        if (_att_errorvaluearray == null) {
            _att_errorvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("errorValueArray", "array");
            if (_att_errorvaluearray == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : errorValueArray probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleArraySTAttribute(_att_errorvaluearray);
        super.addRemove(att, value);
    }
    /** Array of error values.
    * Reports the author's estimate of 
    * 					the error in an array of values. Only meaningful for 
    * 					dataTypes mapping to real number.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setErrorValueArray(double[] value) throws CMLRuntimeException {
        if (_att_errorvaluearray == null) {
            _att_errorvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("errorValueArray", "array");
           if (_att_errorvaluearray == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : errorValueArray probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleArraySTAttribute att = new DoubleArraySTAttribute(_att_errorvaluearray);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   errorBasis

    /** cache */
    StringSTAttribute _att_errorbasis = null;
    /** Basis of the error estimate.
    * 
    * @return CMLAttribute
    */
    public CMLAttribute getErrorBasisAttribute() {
        return (CMLAttribute) getAttribute("errorBasis");
    }
    /** Basis of the error estimate.
    * 
    * @return String
    */
    public String getErrorBasis() {
        StringSTAttribute att = (StringSTAttribute) this.getErrorBasisAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** Basis of the error estimate.
    * 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setErrorBasis(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_errorbasis == null) {
            _att_errorbasis = (StringSTAttribute) attributeFactory.getAttribute("errorBasis", "array");
            if (_att_errorbasis == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : errorBasis probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_errorbasis);
        super.addRemove(att, value);
    }
// attribute:   minValueArray

    /** cache */
    DoubleArraySTAttribute _att_minvaluearray = null;
    /** Minimum values for numeric _matrix_ or _array.
    * A whitespace-separated lists of the same length as the array in the parent element.
    * @return CMLAttribute
    */
    public CMLAttribute getMinValueArrayAttribute() {
        return (CMLAttribute) getAttribute("minValueArray");
    }
    /** Minimum values for numeric _matrix_ or _array.
    * A whitespace-separated lists of the same length as the array in the parent element.
    * @return double[]
    */
    public double[] getMinValueArray() {
        DoubleArraySTAttribute att = (DoubleArraySTAttribute) this.getMinValueArrayAttribute();
        if (att == null) {
            return null;
        }
        return att.getDoubleArray();
    }
    /** Minimum values for numeric _matrix_ or _array.
    * A whitespace-separated lists of the same length as the array in the parent element.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMinValueArray(String value) throws CMLRuntimeException {
        DoubleArraySTAttribute att = null;
        if (_att_minvaluearray == null) {
            _att_minvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("minValueArray", "array");
            if (_att_minvaluearray == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : minValueArray probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleArraySTAttribute(_att_minvaluearray);
        super.addRemove(att, value);
    }
    /** Minimum values for numeric _matrix_ or _array.
    * A whitespace-separated lists of the same length as the array in the parent element.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMinValueArray(double[] value) throws CMLRuntimeException {
        if (_att_minvaluearray == null) {
            _att_minvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("minValueArray", "array");
           if (_att_minvaluearray == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : minValueArray probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleArraySTAttribute att = new DoubleArraySTAttribute(_att_minvaluearray);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   maxValueArray

    /** cache */
    DoubleArraySTAttribute _att_maxvaluearray = null;
    /** Maximum values for numeric _matrix_ or _array.
    * A whitespace-separated list of the same length as the array in the parent element.
    * @return CMLAttribute
    */
    public CMLAttribute getMaxValueArrayAttribute() {
        return (CMLAttribute) getAttribute("maxValueArray");
    }
    /** Maximum values for numeric _matrix_ or _array.
    * A whitespace-separated list of the same length as the array in the parent element.
    * @return double[]
    */
    public double[] getMaxValueArray() {
        DoubleArraySTAttribute att = (DoubleArraySTAttribute) this.getMaxValueArrayAttribute();
        if (att == null) {
            return null;
        }
        return att.getDoubleArray();
    }
    /** Maximum values for numeric _matrix_ or _array.
    * A whitespace-separated list of the same length as the array in the parent element.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMaxValueArray(String value) throws CMLRuntimeException {
        DoubleArraySTAttribute att = null;
        if (_att_maxvaluearray == null) {
            _att_maxvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("maxValueArray", "array");
            if (_att_maxvaluearray == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : maxValueArray probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DoubleArraySTAttribute(_att_maxvaluearray);
        super.addRemove(att, value);
    }
    /** Maximum values for numeric _matrix_ or _array.
    * A whitespace-separated list of the same length as the array in the parent element.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setMaxValueArray(double[] value) throws CMLRuntimeException {
        if (_att_maxvaluearray == null) {
            _att_maxvaluearray = (DoubleArraySTAttribute) attributeFactory.getAttribute("maxValueArray", "array");
           if (_att_maxvaluearray == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : maxValueArray probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleArraySTAttribute att = new DoubleArraySTAttribute(_att_maxvaluearray);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   start

    /** cache */
    StringSTAttribute _att_start = null;
    /** The start value.
    * The start value in any allowable 
    *                 XSD representation 
    * @return CMLAttribute
    */
    public CMLAttribute getStartAttribute() {
        return (CMLAttribute) getAttribute("start");
    }
    /** The start value.
    * The start value in any allowable 
    *                 XSD representation 
    * @return String
    */
    public String getStart() {
        StringSTAttribute att = (StringSTAttribute) this.getStartAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The start value.
    * The start value in any allowable 
    *                 XSD representation 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setStart(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_start == null) {
            _att_start = (StringSTAttribute) attributeFactory.getAttribute("start", "array");
            if (_att_start == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : start probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_start);
        super.addRemove(att, value);
    }
// attribute:   end

    /** cache */
    StringSTAttribute _att_end = null;
    /** The end value.
    * The end value in any allowable XSD representation 
    *                 of data.
    * @return CMLAttribute
    */
    public CMLAttribute getEndAttribute() {
        return (CMLAttribute) getAttribute("end");
    }
    /** The end value.
    * The end value in any allowable XSD representation 
    *                 of data.
    * @return String
    */
    public String getEnd() {
        StringSTAttribute att = (StringSTAttribute) this.getEndAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The end value.
    * The end value in any allowable XSD representation 
    *                 of data.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setEnd(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_end == null) {
            _att_end = (StringSTAttribute) attributeFactory.getAttribute("end", "array");
            if (_att_end == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : end probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_end);
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
            _att_units = (UnitsAttribute) attributeFactory.getAttribute("units", "array");
            if (_att_units == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : units probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new UnitsAttribute(_att_units);
        super.addRemove(att, value);
    }
// attribute:   delimiter

    /** cache */
    DelimiterAttribute _att_delimiter = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getDelimiterAttribute() {
        return (CMLAttribute) getAttribute("delimiter");
    }
    /** null
    * @return String
    */
    public String getDelimiter() {
        DelimiterAttribute att = (DelimiterAttribute) this.getDelimiterAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDelimiter(String value) throws CMLRuntimeException {
        DelimiterAttribute att = null;
        if (_att_delimiter == null) {
            _att_delimiter = (DelimiterAttribute) attributeFactory.getAttribute("delimiter", "array");
            if (_att_delimiter == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : delimiter probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DelimiterAttribute(_att_delimiter);
        super.addRemove(att, value);
    }
// attribute:   size

    /** cache */
    IntSTAttribute _att_size = null;
    /** The size of an array or matrix.
    * No description
    * @return CMLAttribute
    */
    public CMLAttribute getSizeAttribute() {
        return (CMLAttribute) getAttribute("size");
    }
    /** The size of an array or matrix.
    * No description
    * @return int
    */
    public int getSize() {
        IntSTAttribute att = (IntSTAttribute) this.getSizeAttribute();
        if (att == null) {
            throw new CMLRuntimeException("int attribute is unset: size");
        }
        return att.getInt();
    }
    /** The size of an array or matrix.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSize(String value) throws CMLRuntimeException {
        IntSTAttribute att = null;
        if (_att_size == null) {
            _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "array");
            if (_att_size == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IntSTAttribute(_att_size);
        super.addRemove(att, value);
    }
    /** The size of an array or matrix.
    * No description
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSize(int value) throws CMLRuntimeException {
        if (_att_size == null) {
            _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "array");
           if (_att_size == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName ");
            }
        }
        IntSTAttribute att = new IntSTAttribute(_att_size);
        super.addAttribute(att);
        att.setCMLValue(value);
    }
// attribute:   ref

    /** cache */
    RefAttribute _att_ref = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getRefAttribute() {
        return (CMLAttribute) getAttribute("ref");
    }
    /** null
    * @return String
    */
    public String getRef() {
        RefAttribute att = (RefAttribute) this.getRefAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setRef(String value) throws CMLRuntimeException {
        RefAttribute att = null;
        if (_att_ref == null) {
            _att_ref = (RefAttribute) attributeFactory.getAttribute("ref", "array");
            if (_att_ref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : ref probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new RefAttribute(_att_ref);
        super.addRemove(att, value);
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
            _att_constanttosi = (DoubleSTAttribute) attributeFactory.getAttribute("constantToSI", "array");
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
            _att_constanttosi = (DoubleSTAttribute) attributeFactory.getAttribute("constantToSI", "array");
           if (_att_constanttosi == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : constantToSI probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_constanttosi);
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
            _att_multipliertosi = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToSI", "array");
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
            _att_multipliertosi = (DoubleSTAttribute) attributeFactory.getAttribute("multiplierToSI", "array");
           if (_att_multipliertosi == null) {
               throw new CMLRuntimeException("BUG: cannot process attributeGroupName : multiplierToSI probably incompatible attributeGroupName and attributeName ");
            }
        }
        DoubleSTAttribute att = new DoubleSTAttribute(_att_multipliertosi);
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
            _att_unittype = (UnitTypeAttribute) attributeFactory.getAttribute("unitType", "array");
            if (_att_unittype == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : unitType probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new UnitTypeAttribute(_att_unittype);
        super.addRemove(att, value);
    }
    StringSTAttribute _xmlContent;
    /** 
    * 
    * @return String
    */
    public String getXMLContent() {
        String content = this.getValue();
        if (_xmlContent == null) {
            _xmlContent = new StringSTAttribute("_xmlContent");
        }
        _xmlContent.setCMLValue(content);
        return _xmlContent.getString();
    }
    /** 
    * 
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setXMLContent(String value) throws CMLRuntimeException {
        if (_xmlContent == null) {
            _xmlContent = new StringSTAttribute("_xmlContent");
        }
        _xmlContent.setCMLValue(value);
        String attval = _xmlContent.getValue();
        this.removeChildren();
        this.appendChild(attval);
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
        } else if (name.equals("dataType")) {
            setDataType(value);
        } else if (name.equals("errorValueArray")) {
            setErrorValueArray(value);
        } else if (name.equals("errorBasis")) {
            setErrorBasis(value);
        } else if (name.equals("minValueArray")) {
            setMinValueArray(value);
        } else if (name.equals("maxValueArray")) {
            setMaxValueArray(value);
        } else if (name.equals("start")) {
            setStart(value);
        } else if (name.equals("end")) {
            setEnd(value);
        } else if (name.equals("units")) {
            setUnits(value);
        } else if (name.equals("delimiter")) {
            setDelimiter(value);
        } else if (name.equals("size")) {
            setSize(value);
        } else if (name.equals("ref")) {
            setRef(value);
        } else if (name.equals("constantToSI")) {
            setConstantToSI(value);
        } else if (name.equals("multiplierToSI")) {
            setMultiplierToSI(value);
        } else if (name.equals("unitType")) {
            setUnitType(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
