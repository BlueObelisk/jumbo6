package org.xmlcml.cml.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Node;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAppinfo;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLEntry;
import org.xmlcml.cml.element.CMLEnumeration;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMatrix;
import org.xmlcml.cml.element.CMLParameter;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLVector3;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Util;

/** additional tools for entry.
 * manages much of the per-entry validation and processing
 * @author pmr
 * 
 */
public class EntryTool extends AbstractTool {

	/** */
	public final static String ESCAPED_WHITESPACE = S_BACKSLASH+S_BACKSLASH+"s";
	/** */
	public final static String DELIMITERS = 
		ESCAPED_WHITESPACE+S_PIPE+S_COMMA+S_PIPE+S_SLASH;
    Logger logger = Logger.getLogger(EntryTool.class.getName());
    private CMLEntry entry;

    private boolean ignoreCaseOfEnumerations;
    private String delimiter;
    private String prefix;
    private boolean failOnError;
    
    private Set<String> valueSet;
    
	/**
	 * @param entry the entry to set
	 */
	public void setEntry(CMLEntry entry) {
		this.entry = entry;
	}

	/** constructor only used in package
     * use dictionaryTool.createEntryTool(entry) for normal use
     * @param entry
     */
    EntryTool(CMLEntry entry) {
        this.setEntry(entry);
    }

    /**
     * get entry.
     * 
     * @return the entry
     */
    public CMLEntry getEntry() {
        return entry;
    }

    private void ensureValueSet() {
    	if (this.valueSet == null) {
    		valueSet = new HashSet<String>();
    	}
    }
    /**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return (delimiter == null) ? S_SPACE : delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the failOnError
	 */
	public boolean isFailOnError() {
		return failOnError;
	}

	/**
	 * @param failOnError the failOnError to set
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * @return the ignoreCaseOfEnumerations
	 */
	public boolean isIgnoreCaseOfEnumerations() {
		return ignoreCaseOfEnumerations;
	}

	/**
	 * @param ignoreCaseOfEnumerations the ignoreCaseOfEnumerations to set
	 */
	public void setIgnoreCaseOfEnumerations(boolean ignoreCaseOfEnumerations) {
		this.ignoreCaseOfEnumerations = ignoreCaseOfEnumerations;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	void validate(CMLElement element) {
    	if (element instanceof CMLScalar) {
    		validate((CMLScalar) element);
    	} else if (element instanceof CMLArray) {
    		validate((CMLArray) element);
    	} else if (element instanceof CMLFormula) {
    		validate((CMLFormula) element);
    	} else if (element instanceof CMLTable) {
    		validate((CMLTable) element);
    	} else {
    		System.out.println("Cannot validate: "+element.getClass());
    	}
    }
    
    private void validate(CMLScalar scalar) {
    	String entryDataType = entry.getDataType();
    	String scalarDataType = scalar.getDataType();
    	validate(scalar, entryDataType, scalarDataType);
    	validateValue(scalarDataType, scalar.getDictRef(), scalar.getValue());
    }
    
    private void validateValue(String dataType, String name, String value) {
    	if (XSD_DOUBLE.equals(dataType)) {
    		if (!Util.isFloat(value)) {
	    		throw new CMLRuntimeException(name+
    				": expected number, found: "+value);
    		}
    	}
    }
    
    private void validate(CMLArray array) {
    	String entryDataType = entry.getDataType();
    	String scalarDataType = array.getDataType();
    	validate(array, entryDataType, scalarDataType);
    	
    }
    
    private void validate(CMLFormula formula) {
//    	String entryDataType = entry.getDataType();
//    	String scalarDataType = array.getDataType();
//    	validate(array, entryDataType, scalarDataType);
    }
    
    private void validate(CMLTable table) {
//    	String entryDataType = entry.getDataType();
//    	String scalarDataType = array.getDataType();
//    	validate(array, entryDataType, scalarDataType);
    }
    
    private void validate(CMLElement element, String entryDataType, String dataDataType) {
    	String id = element.getAttributeValue("id");
    	String dictRef = element.getAttributeValue("dictRef");
    	if (dataDataType == null) {
    		if (entryDataType == null || entryDataType.equals(XSD_STRING)) {
    			// OK
    		} else {
    			throw new CMLRuntimeException("data for ("+id+") must have data type");
    		}
    	} else {
    		if (!dataDataType.equals(entryDataType)) {
    			throw new CMLRuntimeException(id+S_SLASH+dictRef+": entry ("+entryDataType+") and data {"+dataDataType+"} are of different types");
    		}
    	}
    }
    
	/** add scalar string to a CMLElement.
	 * checks against enumerations and pattern
	 * @param name localname
	 * @param value 
	 * @return scalar
	 */
    public CMLScalar createStringScalar(String name, String value) {
    	EntryTool.checkEmptyName(name);
    	if (failOnError) {
			this.checkValueAgainstEnumerations(value);
			this.checkPattern(value);
    	}
    	CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(prefix+S_COLON+name.toLowerCase());
		scalar.setDataType(XSD_STRING);
		scalar.setXMLContent(value);
		return scalar;
    }
    
	/** add scalar float to a CMLElement.
	 * NO checking
	 * @param name localname
	 * @param value 
	 * @return TODO
	 */
    public CMLScalar createDoubleScalar(String name, double value) {
    	EntryTool.checkEmptyName(name);
    	if (failOnError) {
			this.checkNumericValue((double) value);
    	}
    	CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(prefix+S_COLON+name.toLowerCase());
		scalar.setValue(value);
		return scalar;
    }

    
	/** add scalar int to a CMLElement.
	 * checks values
	 * @param name localname
	 * @param value 
	 * @return TODO
	 */
    public CMLScalar createIntegerScalar(String name, int value) {
    	EntryTool.checkEmptyName(name);
		if (failOnError) {
			this.checkValueAgainstEnumerations(value);
			this.checkNumericValue((double) value);
		}
    	CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(prefix+S_COLON+name.toLowerCase());
		scalar.setValue(value);
		return scalar;
    }
    
	/** add real array to a CMLElement.
	 * @param name localname
	 * @param realArray
	 * @return TODO
	 */
    public CMLArray createDoubleArray(String name, RealArray realArray) {
    	EntryTool.checkEmptyName(name);
    	if (failOnError) {
			this.checkDoubleRange(realArray.getMin(),realArray.getMax());
    	}
    	double[] values = realArray.getArray();
    	CMLArray array = new CMLArray(values);
		array.setDictRef(prefix+S_COLON+name.toLowerCase());
		return array;
    }
    
	/** add integer array to a CMLElement.
	 * @param name localname
	 * @param intArray
	 * @return TODO
	 */
    public CMLArray createIntegerArray(String name, IntArray intArray) {
    	EntryTool.checkEmptyName(name);
    	int[] values = intArray.getArray();
    	CMLArray array = new CMLArray();
		array.setDictRef(prefix+S_COLON+name.toLowerCase());
		array.setDataType(XSD_FLOAT);
		array.setArray(values);
		return array;
    }
    
	/** add string array to a CMLElement.
	 * @param name localname
	 * @param values
	 * @return Array
	 */
    public CMLArray createStringArray(String name, String[] values) {
    	EntryTool.checkEmptyName(name);
    	CMLArray array = new CMLArray();
		array.setDictRef(prefix+S_COLON+name.toLowerCase());
		array.setDataType(XSD_FLOAT);
		array.setArray(values);
		return array;
    }
    
    /** create data.
     * 
     * @param name
     * @param value
     * @return data
     */
    public CMLElement createDoubleScalarOrDoubleArray(String name, String value) {

    	CMLElement element = null;
    	CMLElements<CMLEnumeration> enumerations = entry.getEnumerationElements();
		if (enumerations != null && enumerations.size() > 0) {
			throw new CMLRuntimeException("enumerated float not supported: "+value+" for "+name);
		}
		Attribute minLengthAttribute = entry.getMinLengthAttribute();
		Attribute maxLengthAttribute = entry.getMaxLengthAttribute();
		Attribute lengthAttribute = entry.getLengthAttribute();
		
		// arrays
		double minInclusive = Double.NaN;
		double maxInclusive = Double.NaN;
		if (lengthAttribute != null ||
				minLengthAttribute != null || 
				maxLengthAttribute != null) {
			String[] ss = value.split(delimiter);
			this.checkArrayLength(ss, name);
			// add arrays
			try {
				RealArray ra = new RealArray(ss);
				element = this.createDoubleArray(name, ra);
			} catch (EuclidRuntimeException e) {
				if (failOnError) {
					throw new CMLRuntimeException(e+" for "+name);
				} else {
					// add string if fails
					element = this.createStringScalar(name, value);
				}
			}
		} else {
			// known to be scalar
			double valueD = Double.NaN;
			try {
				valueD = new Double(value);
				element = this.createDoubleScalar(name, valueD);
				minInclusive = Math.min(minInclusive, valueD);
				maxInclusive = Math.max(maxInclusive, valueD);
			} catch (NumberFormatException e) {
				if (failOnError) {
					throw new CMLRuntimeException("expected double for "+name+" found: "+value);
				} else {
					// add string if fails
					element = this.createStringScalar(name, value);
				}
			}
		}
		return element;
    }
    
    /** create integer data.
     * 
     * @param name
     * @param value
     * @return integer
     */
	public CMLElement createIntegerScalarOrIntegerArray(String name, String value) {

		CMLElement element = null;
		Attribute minLengthAttribute = entry.getMinLengthAttribute();
		Attribute maxLengthAttribute = entry.getMaxLengthAttribute();
		Attribute lengthAttribute = entry.getLengthAttribute();
		
		int minInclusive = Integer.MAX_VALUE;
		int maxInclusive = -Integer.MAX_VALUE;
		if (lengthAttribute != null || 
			minLengthAttribute != null || 
			maxLengthAttribute != null) {
			String[] ss = value.split(delimiter);
			this.checkArrayLength(ss, name);
			// fields in keywords should not be parsed
			try {
				IntArray ia = new IntArray(ss);
				element = this.createIntegerArray(name, ia);
				minInclusive = ia.getMin();
				maxInclusive = ia.getMax();
				this.checkIntegerRange(minInclusive, maxInclusive);
			} catch (EuclidRuntimeException e) {
				if (failOnError) {
					throw(e);
				} else {
					element = this.createStringScalar(name, value);
				}
			}
		} else {
			try {
				int ivalue = Integer.parseInt(value);
				element = this.createIntegerScalar(name, ivalue);
			} catch (NumberFormatException e) {
				throw new CMLRuntimeException("Bad integer: "+value);
			}
		}
		return element;
	}

	/** create string data.
	 * 
	 * @param name
	 * @param value
	 * @return string
	 */
	public CMLElement createStringScalarOrStringArray(String name, String value) {
		
		CMLElement element = null;
		Attribute minLengthAttribute = entry.getMinLengthAttribute();
		Attribute maxLengthAttribute = entry.getMaxLengthAttribute();
		Attribute lengthAttribute = entry.getLengthAttribute();
		
		if (lengthAttribute != null ||
				minLengthAttribute != null || 
				maxLengthAttribute != null) {
			String[] ss = value.split(delimiter);
			this.checkArrayLength(ss, name);
			// fields in keywords should not be parsed
			try {
				element = this.createStringArray(name, ss);
			} catch (EuclidRuntimeException e) {
				if (failOnError) {
					throw(e);
				} else {
					element = this.createStringScalar(name, value);
				}
			}
		} else {
			element = this.createStringScalar(name, value);
		}
		return element;
	}
	
	/** create parameter.
	 * 
	 * @param name
	 * @param value
	 * @return parameter
	 */
	public CMLParameter createParameter(
    		String name, String value) {
		CMLParameter parameter = new CMLParameter();

		CMLElement element = null;
		if (XSD_STRING.equals(entry.getDataType())) {
			element = createStringScalarOrStringArray(name, value);
		} else if (XSD_INTEGER.equals(entry.getDataType())) {
			element = createIntegerScalarOrIntegerArray(name, value);
		} else if (XSD_FLOAT.equals(entry.getDataType())) {
			element = createDoubleScalarOrDoubleArray(name, value);
		}
		parameter.appendChild(element);
		return parameter;
	}
	
	/** add scalar date to a CMLElement.
	 * will try to parse and canonicalise the date. If fails, throws
	 * CMLRuntimeException
	 * @param name localname
	 * @param value of date 
	 * @return data 
	 */
    public CMLScalar createDate(String name, String value) {
    	EntryTool.checkEmptyName(name);
    	CMLScalar scalar = new CMLScalar();
    	String canonicalValue = Util.getCanonicalDate(value);
    	if (canonicalValue == null) {
    		throw new CMLRuntimeException("Cannot parse as date: "+value);
    	}
		scalar.setDictRef(prefix+S_COLON+name.toLowerCase());
		scalar.setDataType(XSD_DATE);
		scalar.setXMLContent(value);
		return scalar;
    }

	/** create formula
	 * will try to parse and canonicalise the formula.
	 * If fails, throws CMLRuntimeException
	 * @param name localname
	 * @param value of formula 
	 * @return formula 
	 */
    public CMLFormula createFormula(String name, String value) {
    	EntryTool.checkEmptyName(name);
    	CMLFormula formula = CMLFormula.createFormula(value);
    	if (formula == null) {
    		throw new CMLRuntimeException("Cannot parse as formula: "+value);
    	}
		formula.setDictRef(prefix+S_COLON+name.toLowerCase());
		return formula;
    }

	/** create Vector3 from string.
	 * @param name localname
	 * @param value of vector
	 * @return vector 
 */
    public CMLVector3 createVector3(String name, String value) {
    	EntryTool.checkEmptyName(name);
    	RealArray array = new RealArray(value.split(this.getDelimiter()));
    	CMLVector3 vector3 = new CMLVector3(array.getArray());
		vector3.setDictRef(prefix+S_COLON+name.toLowerCase());
		return vector3;
    }

	/** create Vector3 from string.
	 * @param name localname
	 * @param value of vector
	 * @return matrix 
 */
    public CMLMatrix createMatrix(String name, String value) {
    	// FIXME needs to return property
    	EntryTool.checkEmptyName(name);
    	CMLMatrix matrix = null;
    	List<Node> propertys = CMLUtil.getQueryNodes(entry, 
    			CMLAppinfo.NS+"/*/"+CMLProperty.NS+"["+CMLMatrix.NS+"]", X_CML);
    	if (propertys.size() != 1) {
    		throw new CMLRuntimeException(
    				"Cannot find property/matrix template in entry: "+entry.getId());
    	}
    	CMLProperty property = (CMLProperty) propertys.get(0);
    	CMLMatrix template = (CMLMatrix) property.getChildCMLElement(CMLMatrix.TAG, 0);
    	String matrixType = template.getMatrixType();
    	int rows = (template.getRowsAttribute() == null) ? 0 : template.getRows();
    	int columns = (template.getColumnsAttribute() == null) ? 0 : template.getColumns();
    	RealArray array = new RealArray(value.split(this.getDelimiter()));
    	if (rows != 0 && rows == columns && rows * columns == array.size()) {
    		matrix = new CMLMatrix(rows, columns, array.getArray());
    		matrix.copyAttributesFrom(template);
    	} else if (CMLMatrix.Type.SQUARE_SYMMETRIC_LT.value.equals(matrixType)) {
    		matrix = CMLMatrix.createSquareMatrix(
    				array, rows, CMLMatrix.Type.SQUARE_SYMMETRIC_LT);
    		matrix.copyAttributesFrom(template);
    	} else if (CMLMatrix.Type.SQUARE_SYMMETRIC_UT.value.equals(matrixType)) {
    		matrix = CMLMatrix.createSquareMatrix(
    				array, rows, CMLMatrix.Type.SQUARE_SYMMETRIC_UT);
    		matrix.copyAttributesFrom(template);
    	}
		return matrix;
    }

    /** check length.
     * 
     * @param ss
     * @param name
     */
	public void checkArrayLength(String[] ss, String name) {
		if (entry.getLengthAttribute() != null) {
			int l = entry.getLength();
			if (l != ss.length) {
				throw new CMLRuntimeException("Expected array of size "+l+"; found: "+ss.length+" for "+name);
			}
		} 
		if (entry.getMinLengthAttribute() != null) {
			int l = entry.getMinLength();
			if (l > ss.length) {
				throw new CMLRuntimeException("Expected array of size >= "+l+"); found: "+ss.length+" for "+name);
			}
		}
		if (entry.getMaxLengthAttribute() != null) {
			int l = entry.getMaxLength();
			if (l < ss.length) {
				throw new CMLRuntimeException("Expected array of size =< "+l+"); found: "+ss.length+" for "+name);
			}
		}
	}
	/** check value of data.
	 * 
	 * @param value
	 */
    public void checkNumericValue(double value) {
    	if (entry.getMinInclusiveAttribute() != null) {
    		if (value < entry.getMinInclusive()) {
    			throw new CMLRuntimeException("value ("+value+") less than minimum: "+
    					entry.getMinInclusiveAttribute()+" for "+entry.getId());
    		}
    	}
    	if (entry.getMaxInclusiveAttribute() != null) {
    		if (value > entry.getMaxInclusive()) {
    			throw new CMLRuntimeException("value ("+value+") greater than maximum: "+
    					entry.getMaxInclusiveAttribute());
    		}
    	}
    }

    /** check against range.
     * 
     * @param minInclusive
     * @param maxInclusive
     */
    public void checkDoubleRange(double minInclusive, double maxInclusive) {
		Attribute minInclusiveAttribute = (entry == null) ?
				null : entry.getMinInclusiveAttribute();
		Attribute maxInclusiveAttribute = (entry == null) ?
				null : entry.getMaxInclusiveAttribute();
		if (minInclusiveAttribute != null && !Double.isNaN(minInclusive)) {
			if (minInclusive < entry.getMinInclusive()) {
				throw new CMLRuntimeException("value ("+
						minInclusive+") outside minimum: "+entry.getMinInclusive()+
						" for "+entry.getId());
			}
		}
		if (maxInclusiveAttribute != null && !Double.isNaN(maxInclusive)) {
			if (maxInclusive > entry.getMaxInclusive()) {
				throw new CMLRuntimeException("value ("+
						maxInclusive+") outside maximum: "+entry.getMaxInclusive()+
						" for "+entry.getId());
			}
		}
    }
    
    /** check integer range.
     * 
     * @param minInclusive
     * @param maxInclusive
     */
    public void checkIntegerRange(int minInclusive, int maxInclusive) {
		Attribute minInclusiveAttribute = (entry == null) ?
				null : entry.getMinInclusiveAttribute();
		Attribute maxInclusiveAttribute = (entry == null) ?
				null : entry.getMaxInclusiveAttribute();
		if (minInclusiveAttribute != null && !Double.isNaN(minInclusive)) {
			if (minInclusive < entry.getMinInclusive()) {
				throw new CMLRuntimeException("value ("+
						minInclusive+") outside minimum: "+entry.getMinInclusive()+
						" for "+entry.getId());
			}
		}
		if (maxInclusiveAttribute != null && !Double.isNaN(maxInclusive)) {
			if (maxInclusive > entry.getMaxInclusive()) {
				throw new CMLRuntimeException("value ("+
						maxInclusive+") outside maximum: "+entry.getMaxInclusive()+
						" for "+entry.getId());
			}
		}
    }
        
    /** check against enumerations.
     * 
     * @param value
     */
    public void checkValueAgainstEnumerations(int value) {
    	checkValueAgainstEnumerations(""+value);
    }
	
    /** check value.
     * 
     * @param value
     */
    public void checkValueAgainstEnumerations(String value) {
    	CMLElements<CMLEnumeration> enumerations = entry.getEnumerationElements();
    	if (enumerations != null && enumerations.size() > 0) {
	    	for (CMLEnumeration enumeration : enumerations) {
	    		if (this.ignoreCaseOfEnumerations) {
	    			if (enumeration.getCMLValue().equalsIgnoreCase(value)) {
	    				return;
	    			}
	    		} else if (enumeration.getCMLValue().equals(value)) {
	    			return;
	    		}
	    	}
    		throw new CMLRuntimeException("Value ("+value+") does not match" +
    				" enumeration for: "+entry.getId());
    	}
    }
    
    /** check pattern.
     * 
     * @param value
     */
    public void checkPattern(String value) {
    	String patternS = entry.getPattern();
    	if (patternS != null) {
    		Pattern pattern = Pattern.compile(patternS);
    		Matcher matcher = pattern.matcher(value);
	    	if (!matcher.matches()) {
    			throw new CMLRuntimeException("Value ("+value+
    					") does not match enumeration for: "+entry.getId());
	    	}
    	}
    }

    /** check empty.
     * 
     * @param name
     */
    public static void checkEmptyName(String name) {
		if (name == null) {
			throw new CMLRuntimeException("null name");
		}
		if (S_EMPTY.equals(name.trim())) {
			throw new CMLRuntimeException("empty name");
		}
		if (name.indexOf(S_COLON) != -1) {
			throw new CMLRuntimeException("name cannot contain colon");
		}
    }

    /** add value.
     * 
     * @param value
     */
    public void addValue(String value) {
    	ensureValueSet();
    	valueSet.add(value);
    }

	 private String guessDataType() {
		 ensureValueSet();
		int count = 0;
		boolean couldBeBoolean = true;
		boolean couldBeInt = true;
		boolean couldBeIntArray = false;
		boolean couldBeDate = true;
		boolean couldBeFloat = true;
		boolean couldBeFloatArray = false;
		for (String s : valueSet) {
			count++;
			couldBeBoolean &= 
					s.equalsIgnoreCase("true") ||
					s.equalsIgnoreCase("false");
			couldBeDate &= (Util.getCanonicalDate(s) != null);
			// might be array
			couldBeIntArray &= (Util.isIntArray(s, DELIMITERS));
			couldBeFloatArray &= (Util.isFloatArray(s, DELIMITERS));
			couldBeInt &= (Util.isInt(s));
			couldBeFloat &= (Util.isFloat(s));
			
		}
		
		boolean couldBeEnum = count > 5;
		String type = XSD_STRING;
		if (false) {
		} else if(couldBeBoolean) {
			type = XSD_BOOLEAN;
		} else if (couldBeInt) {
				type = XSD_INTEGER;
		} else if (couldBeFloat) {
			type = XSD_FLOAT;
		} else if (couldBeDate) {
			type = XSD_DATE;
		} else if (couldBeEnum) {
			// assume string and add dummy enumeration to flag type
			type = XSD_STRING;
		}
		entry.setDataType(type);
		return type;
	 }

	 /** update enumerations.
	  */
	public void updateEnumerations() {
		ensureValueSet();
		System.out.println("===="+entry.getId()+"===");
		String dataType = entry.getDataType();
		if (dataType == null || XSD_STRING.equals(dataType)) {
			dataType = guessDataType();
		}
		// if this is an enumerated entry add possible values
		CMLElements<CMLEnumeration> enumerations = entry.getEnumerationElements();
		if (dataType.equals(XSD_STRING) &&
			entry.getEnumerationElements().size() == 0) {
			CMLEnumeration enumeration = new CMLEnumeration();
			enumeration.setId(CMLEnumeration.DUMMY_ENUM);
			enumeration.setCMLValue(CMLEnumeration.DUMMY_ENUM);
			entry.addEnumeration(enumeration);
		}
		for (CMLEnumeration enumeration : enumerations) {
			try {
				entry.updateIndex();
			} catch (CMLRuntimeException e) {
				entry.debug("BAD INDEX");
				throw e;
			}
			String s = enumeration.getCMLValue();
			if (ignoreCaseOfEnumerations) {
				s = s.toLowerCase();
			}
		}
		// remove enumerations if type is float or boolean
		if (dataType.equals(XSD_FLOAT) || dataType.equals(XSD_BOOLEAN)) {
			for (CMLEnumeration enumeration : enumerations) {
				enumeration.detach();
			}
		} else if (entry.getEnumerationElements().size() > 0) {
			// if there are already enumerations, add more if distinct
			for (String s : valueSet) {
				String id = CMLUtil.makeId(s);
				if (entry.getIndexableById(id) == null) {
					CMLEnumeration enumeration = new CMLEnumeration();
					enumeration.setId(id);
					enumeration.setCMLValue(s);
					entry.addIndexable(enumeration);
				}
			}
		}
		// remove dummy entry if there are other enumerations
		if (entry.getEnumerationElements().size() > 1) {
			CMLEnumeration dummyEnumeration = (CMLEnumeration) 
				entry.getIndexableById(CMLEnumeration.DUMMY_ENUM);
			if (dummyEnumeration != null) {
				dummyEnumeration.detach();
			}
		}
	}

	/** create term from title or name.
	 * term = title else = name
	 * @param element
	 * @return term
	 */
	public static String createTerm(CMLElement element) {
		String term = null;
		String title = element.getAttributeValue("title");
		String name = element.getAttributeValue("name");
		if (name != null) {
			term = name;
		} else if (title != null) {
			term = title;
		}
		return term;
	}
    
}
