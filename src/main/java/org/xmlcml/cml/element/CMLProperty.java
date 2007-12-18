package org.xmlcml.cml.element;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLType;
import org.xmlcml.cml.interfacex.HasArraySize;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasDictRef;
import org.xmlcml.cml.interfacex.HasScalar;

/**
 * user-modifiable class supporting property. * autogenerated from schema use as
 * a shell which can be edited
 *
 */
public class CMLProperty extends AbstractProperty {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

	/** type of property */
	public enum Type {
		/** intensive properties do not depend on amoount*/
		INTENSIVE("intensive"),
		/** extensive properties depend on amount*/
		EXTENSIVE("extensive"),
		/** semintensive properties are intensive properties
		 * which also depend on polymer size*/
		SEMINTENSIVE("semintensive"),
		;
		/** value */
		public String value;
		private Type(String t) {
			this.value = t;
		}
	}
	
	/** common properties */
	public enum Prop {
		/** density*/
		DENSITY("cml:density"),
		/** molar mass*/
		MOLAR_MASS("cml:molarMass"),
		/** molar mass*/
		MOLAR_VOLUME("cml:molarVolume"),
		;
		/** value of dictRef */
		public final String value;
		private Prop(String s) {
			value = s;
		}
	}
	
	
	private HasDataType child;

    /**
     * constructor.
     */
    public CMLProperty() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLProperty(CMLProperty old) {
        super((AbstractProperty) old);

    }

    /**
     * construct as property with child scalar
     * @param dictRef
     * @param value
     * @param units
     */
    public CMLProperty(String dictRef, double value, String units) { 
    	this();
    	CMLScalar scalar = new CMLScalar(value);
    	scalar.setUnits(units);
    	this.appendChild(scalar);
    	this.setDictRef(dictRef);
    }
    
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLProperty(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLProperty
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLProperty();
    }
    
    /** gets descendant property elements.
     * may either thave the dictRef on the property element or on 
     * its child.
     * properties are normalized
     * @param parent
     * @param dictRef
     * @return propertyList containg references (normally 1 or 0 entries)
     */ 
    public static CMLPropertyList getPropertyList(CMLElement parent, String dictRef) {
    	CMLPropertyList propertyList = new CMLPropertyList();
    	Nodes nodes = parent.query("./cml:property", X_CML);
    	for (int i = 0; i < nodes.size(); i++ ) {
    		CMLProperty property = (CMLProperty) nodes.get(i);
    		property.canonicalize();
    		if (dictRef.equals(property.getAttributeValue("dictRef"))) {
	    		propertyList.addProperty(property);
    		}
    	}
    	return propertyList;
    }

    /** gets single property.
     * if zero or many properties with gievn dictRef returns null
     * @param parent
     * @param dictRef
     * @return property or null
     */
    public static CMLProperty getProperty(CMLElement parent, String dictRef) {
    	CMLPropertyList propertyList = CMLProperty.getPropertyList(parent, dictRef);
    	CMLElements<CMLProperty> properties = propertyList.getPropertyElements();
    	CMLProperty property = null;
    	if (properties.size() == 1) {
    		property = properties.get(0);
    	}
    	return property;
    }
    
    /** makes sure property has the structure:
     * <property @title @dictRef><scalar @dataType @units>...
     * if zero or many children (scalar, array, matrix) no-op
     *
     */
    public void canonicalize() {
    	getChild();
    	if (child != null) {
    		String thisDictRef = this.getDictRef();
    		String childDictRef = ((HasDictRef) child).getDictRef();
    		if (thisDictRef == null) {
    			if (childDictRef == null) {
    				throw new CMLRuntimeException("No dictRef attribute given: ");
    			}
    			// copy to property
    			this.setDictRef(childDictRef);
    		} else {
    			if (childDictRef == null) {
    			} else if (thisDictRef.equals(childDictRef)) {
    				// OK
    			} else {
    				throw new CMLRuntimeException("inconsistent dictRefs: "+thisDictRef+" // "+childDictRef);
    			}
    		}
    		String units = getUnits();
    		String dataType = CMLType.getNormalizedValue(child.getDataType());
    		if (units != null) {
    			if (!dataType.equals(XSD_DOUBLE)) {
    				((HasDataType) child).setDataType(XSD_DOUBLE);
    				throw new CMLRuntimeException("units require data type of double");
    			}
    		} else {
    			if (dataType.equals(XSD_DOUBLE)) {
    				throw new CMLRuntimeException("dataType not double");
    			}
    		}
    	}
    }

	/**
	 * @return units on child
	 */
	public String getUnits() {
		getChild();
		String units = ((CMLElement) child).getAttributeValue("units");
		return units;
	}
    
	/**
	 * gets real value of scalar child
	 * 
	 * @return the value (NaN if not set)
	 */
	public double getDouble() {
		getChild();
		double result = Double.NaN;
		String dataType = CMLType.getNormalizedValue(child.getDataType());
		if (XSD_DOUBLE.equals(dataType) && child instanceof HasScalar) {
			result = ((HasScalar) child).getDouble();
		}
		return result;
	}

	/**
	 * gets String value. dataType must be XSD_STRING.
	 * 
	 * @return the value (null if not set)
	 */
	public String getString() {
		getChild();
		String result = null;
		if (XSD_STRING.equals(child.getDataType()) &&
				(child instanceof HasScalar)
				) {
			result = ((HasScalar) child).getString();
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
		getChild();
		int result = Integer.MIN_VALUE;
		String dataType = CMLType.getNormalizedValue(child.getDataType());
		if (XSD_INTEGER.equals(dataType) && 
				(child instanceof HasScalar)) {
			result = ((HasScalar) child).getInt();
		}
		return result;
	}

    /** get array elements.
     * recalcuates each time so best cached for frequent use
     * @return elements as String
     */
    public List<String> getStringValues() {
		getChild();
		List<String> result = null;
		String dataType = CMLType.getNormalizedValue(child.getDataType());
		if (XSD_STRING.equals(dataType) && 
			child instanceof HasArraySize) {
			result = ((HasArraySize) child).getStringValues();
		}
		return result;
    }
    
    /**
     * gets values of element;
     * 
     * @return integer values
     */
    public int[] getInts() {
		getChild();
		int[] result = null;
		String dataType = CMLType.getNormalizedValue(child.getDataType());
		if (XSD_INTEGER.equals(dataType) && 
			child instanceof HasArraySize) {
			result = ((HasArraySize) child).getInts();
		}
		return result;
    }

    /**
     * gets values of element;
     * 
     * @return double values
     */
    double[] getDoubles() {
		getChild();
		double[] result = null;
		String dataType = CMLType.getNormalizedValue(child.getDataType());
		if (XSD_DOUBLE.equals(dataType) && 
			child instanceof HasArraySize) {
			result = ((HasArraySize) child).getDoubles();
		}
		return result;
    }

	
	/**
	 * requires exactly one child of type scalar array matrix
	 * @return the child
	 */
	public HasDataType getChild() {
		if (child == null) {
	    	Nodes nodes = this.query("cml:scalar | cml:array | cml:matrix", X_CML);
	    	if (nodes.size() == 1) {
	    		child = (HasDataType) nodes.get(0);
	    	}
		}
		return child;
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(HasDataType child) {
		this.child = child;
	}


	/** gets dataType
	 * @return dataType as string
	 */
	public String getDataType() {
		getChild();
		String dataType = (child == null) ? null : ((HasDataType) child).getDataType();
		return CMLType.getNormalizedValue(dataType);
	}
	
}
