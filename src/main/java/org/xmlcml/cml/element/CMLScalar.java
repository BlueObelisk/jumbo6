package org.xmlcml.cml.element;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.attribute.DictRefAttribute;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLType;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.interfacex.HasScalar;
import org.xmlcml.cml.interfacex.HasUnits;
import org.xmlcml.cml.map.NamespaceToUnitListMap;

/**
 * user-modifiable class supporting scalar. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLScalar extends AbstractScalar implements HasUnits, HasScalar {

	/** namespaced element name. */
	public final static String NS = C_E + TAG;

	final static Logger logger = Logger.getLogger(CMLScalar.class.getName());

	/**
	 * default constructor. NOTE creates a CMLScalar with dataType = XSD_STRING
	 * and content S_EMPTY.
	 * 
	 */
	public CMLScalar() {
		init();
	}

	void init() {
		// setXMLContent(S_EMPTY);
	}

	/**
	 * contructor.
	 * 
	 * @param old
	 */
	public CMLScalar(CMLScalar old) {
		super((AbstractScalar) old);

	}

	/**
	 * copy node .
	 * 
	 * @return Node
	 */
	public Node copy() {
		return new CMLScalar(this);

	}

	/**
	 * create new instance in context of parent, overridable by subclasses.
	 * 
	 * @param parent
	 *            parent of element to be constructed (ignored by default)
	 * @return CMLScalar
	 */
	public CMLElement makeElementInContext(Element parent) {
		return new CMLScalar();

	}

	/**
	 * check scalar is OK.
	 * 
	 * @param parent
	 *            element
	 * @throws CMLRuntimeException
	 *             parsing error
	 */
	public void finishMakingElement(Element parent) throws CMLRuntimeException {
		String dataType = this.getDataType();
		if (dataType.equals(XSD_STRING)) {
		} else if (dataType.equals(XSD_BOOLEAN)) {
			this.getDouble();
		} else if (XSD_DOUBLE.equals(CMLType.getNormalizedValue(dataType))) {
			this.getDouble();
		} else if (dataType.equals(XSD_INTEGER)) {
			this.getInt();
		} else if (dataType.equals(XSD_DATE)) {
			System.out.println("skipped date");
			// this.getInt();
		} else {
			throw new CMLRuntimeException("scalar does not support dataType: "
					+ dataType);
		}
	}

	// =========================== additional constructors
	// ========================

	/**
	 * formed from components. sets dataType to xsd:string
	 * 
	 * @param scalar
	 */
	public CMLScalar(String scalar) {
		this.setValue(scalar);
	}

	/**
	 * formed from components. sets dataType to xsd:double
	 * 
	 * @param scalar
	 */
	public CMLScalar(double scalar) {
		this.setValue(scalar);
	}

	/**
	 * formed from components. sets dataType to xsd:integer
	 * 
	 * @param scalar
	 */
	public CMLScalar(int scalar) {
		this.setValue(scalar);
	}

	/**
	 * gets real value. dataType must be XSD_DOUBLE.
	 * 
	 * @return the value (NaN if not set)
	 */
	public double getDouble() {
		double result = Double.NaN;
		if (getDataType().equals(XSD_DOUBLE)) {
			String content = getXMLContent();
			if (content != null) {
				try {
					result = CMLUtil.parseFlexibleDouble(content);
				} catch (ParseException e) {
					throw new CMLRuntimeException("Bad double :" + content, e);
				}
			}
		}
		return result;
	}

	/**
	 * gets String value. dataType must be XSD_STRING.
	 * 
	 * @return the value (null if not set)
	 */
	public String getString() {
		String result = null;
		if (getDataType().equals(XSD_STRING)) {
			result = getXMLContent();
		}
		return result;
	}

	/**
	 * gets int value. dataType must be XSD_INTEGER.
	 * 
	 * @return the value
	 * @throws CMLRuntimeException
	 *             if different type
	 */
	public int getInt() {
		int result = Integer.MIN_VALUE;
		if (getDataType().equals(XSD_INTEGER)) {
			String content = getXMLContent();
			if (content != null && !content.trim().equals(S_EMPTY)) {
				try {
					result = Integer.parseInt(content);
				} catch (NumberFormatException e) {
					throw new CMLRuntimeException("bad integer content: "
							+ content);
				}
			}
		} else {
			throw new CMLRuntimeException("wrong dataType for int "
					+ getDataType());
		}
		return result;
	}

	// ====================== subsidiary accessors =====================

	/**
	 * sets value to String.. updates dataType.
	 * 
	 * @param scalar
	 *            no action if null
	 */
	public void setValue(String scalar) {
		if (scalar != null) {
			setXMLContent(scalar);
			super.setDataType(XSD_STRING);
		}
	}

	/**
	 * sets value to double.. updates dataType.
	 * 
	 * @param scalar
	 */
	public void setValue(double scalar) {
		setXMLContent(S_EMPTY + scalar);
		super.setDataType(XSD_DOUBLE);
	}

	/**
	 * sets value to int.. updates dataType.
	 * 
	 * @param scalar
	 */
	public void setValue(int scalar) {
		setXMLContent(S_EMPTY + scalar);
		super.setDataType(XSD_INTEGER);
	}

	/**
	 * get dataType. if attribute not set, reset to String.
	 * 
	 * @return dataType (default XSD_STRING)
	 */
	public String getDataType() {
		String dataType = super.getDataType();
		if (dataType == null) {
			dataType = XSD_STRING;
			super.setDataType(dataType);
		}
		return CMLType.getNormalizedValue(dataType);
	}

	/**
	 * set dataType. this may not be set independently of the value, so simply
	 * throws CMLRuntime only place it is usable is within copy constructor
	 * 
	 * @param dType
	 * @throws CMLRuntimeException
	 *             attempt to reset datatype
	 */
	public void setDataType(String dType) {
		if (this.getAttributeValue("dataType") == null) {
			super.setDataType(dType);
		} else {
			throw new CMLRuntimeException(
					"Must not reset dataType; use SetValue(...)");
		}
	}

	// ====================== functionality =====================

	/**
	 * can two scalars be used for arithmetic. checks that both scalars are
	 * numeric and of same dataType and of same size
	 * 
	 * @param scalar
	 *            the scalar to test; can have different owner
	 * @throws CMLException
	 *             if not of same numeric data type
	 */
	void checkNumericConformability(CMLScalar scalar) throws CMLException {
		if (!this.getDataType().equals(scalar.getDataType())) {
			throw new CMLException(
					"Unsuitable dataTypes for numeric operations / "
							+ this.getDataType() + S_SLASH
							+ scalar.getDataType());
		}
	}

	/**
	 * subtract an scalar from this..
	 * 
	 * result = this - scalar, owner document = this does not alter this only
	 * works if both scalars are numeric and of same dataType
	 * 
	 * @param scalar
	 *            the scalar to subtract; can have different owner
	 * @throws CMLException
	 *             inappropriate dataTypes
	 * @return new scalar
	 */
	public CMLScalar subtract(CMLScalar scalar) throws CMLException {
		checkNumericConformability(scalar);
		CMLScalar resultScalar = null;
		if (this.getDataType().equals(XSD_DOUBLE)) {
			resultScalar = new CMLScalar(this.getDouble() - scalar.getDouble());
		} else if (this.getDataType().equals(XSD_INTEGER)) {
			resultScalar = new CMLScalar(this.getInt() - scalar.getInt());
		}
		return resultScalar;
	}

	/**
	 * subtract an scalar from this..
	 * 
	 * this -= scalar, owner document = this alters this only works if both
	 * scalars are numeric and of same dataType
	 * 
	 * @param scalar
	 *            the scalar to subtract; can have different owner
	 * @throws CMLException
	 *             inappropriate dataTypes, unequal scalars
	 */
	public void subtractEquals(CMLScalar scalar) throws CMLException {
		checkNumericConformability(scalar);
		if (this.getDataType().equals(XSD_DOUBLE)) {
			this.setValue(this.getDouble() - scalar.getDouble());
		} else if (this.getDataType().equals(XSD_INTEGER)) {
			this.setValue(this.getInt() - scalar.getInt());
		}
	}

	/**
	 * add a scalar to this..
	 * 
	 * result = this + scalar does not alter this only works if both scalars are
	 * numeric and of same dataType
	 * 
	 * @param scalar
	 *            the scalar to add;
	 * @throws CMLException
	 *             inappropriate dataTypes
	 * @return new scalar
	 */
	public CMLScalar plus(CMLScalar scalar) throws CMLException {
		checkNumericConformability(scalar);
		CMLScalar resultScalar = null;
		if (this.getDataType().equals(XSD_DOUBLE)) {
			resultScalar = new CMLScalar(this.getDouble() + scalar.getDouble());
		} else if (this.getDataType().equals(XSD_INTEGER)) {
			resultScalar = new CMLScalar(this.getInt() + scalar.getInt());
		}
		return resultScalar;
	}

	/**
	 * subtract an scalar from this..
	 * 
	 * this += scalar, owner document = this alters this only works if both
	 * scalars are numeric and of same dataType
	 * 
	 * @param scalar
	 *            the scalar to subtract;
	 * @throws CMLException
	 *             inappropriate dataTypes, unequal scalars
	 */
	public void plusEquals(CMLScalar scalar) throws CMLException {
		checkNumericConformability(scalar);
		if (this.getDataType().equals(XSD_DOUBLE)) {
			this.setValue(this.getDouble() + scalar.getDouble());
		} else if (this.getDataType().equals(XSD_INTEGER)) {
			this.setValue(this.getInt() + scalar.getInt());
		}
	}

	/**
	 * gets dictRef OR from parent. see
	 * DictRefAttribute.getDictRefFromElementOrParent()
	 * 
	 * @return the attribute or null
	 */
	public DictRefAttribute getDictRefFromElementOrParent() {
		return DictRefAttribute.getDictRefFromElementOrParent(this);
	}

	/**
	 * gets unit for scalar.
	 * 
	 * @param unitListMap
	 * @return the unit (null if none)
	 */
	public CMLUnit getUnit(NamespaceToUnitListMap unitListMap) {
		UnitsAttribute unitsAttribute = (UnitsAttribute) this
				.getUnitsAttribute();
		CMLUnit unit = null;
		if (unitsAttribute != null) {
			unit = unitListMap.getUnit(unitsAttribute);
		}
		return unit;
	}

	/**
	 * converts a real scalar to SI. only affects scalar with units attribute
	 * and dataType='xsd:double' replaces the value with the converted value and
	 * the units with the SI Units
	 * 
	 * @param unitListMap
	 *            map to resolve the units attribute
	 */
	public void convertToSI(NamespaceToUnitListMap unitListMap) {
		CMLUnit unit = this.getUnit(unitListMap);
		if (unit != null && XSD_DOUBLE.equals(this.getDataType())
				&& unit.getMultiplierToSIAttribute() != null) {
			CMLUnit siUnit = unit.getParentSIUnit();
			if (siUnit != null) {
				if (unit.getMultiplierToSIAttribute() != null) {
					// multiply current value by int multiplier
					this.setValue(this.getDouble() * unit.getMultiplierToSI());
				}
				if (unit.getConstantToSIAttribute() != null) {
					// add constant
					this.setValue(this.getDouble() + unit.getConstantToSI());
				}
				siUnit.setUnitsOn(this);
			}
		} else {
			System.out.println("COULD NOT FIND UNIT ");
		}
	}

	/**
	 * sets units attribute. requires namespace for unit to be in scope.
	 * 
	 * @param prefix
	 *            for namespace
	 * @param id
	 *            for unit
	 * @param namespaceURI
	 *            sets units namespace if not present already
	 */
	public void setUnits(String prefix, String id, String namespaceURI) {
		NamespaceRefAttribute.setUnits((HasUnits) this, prefix, id,
				namespaceURI);
	}
}
