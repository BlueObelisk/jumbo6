package org.xmlcml.cml.base;

import java.text.ParseException;

import nu.xom.Attribute;
import nu.xom.Node;

import org.xmlcml.euclid.Complex;

/**
 * attribute representing a double value.
 * 
 */

public class ComplexSTAttribute extends CMLAttribute {

	private static final String S_PLUS = "+";

    /** dewisott */
	public final static String JAVA_TYPE = "double";

    /** dewisott */
	public final static String JAVA_GET_METHOD = "getComplex";

    /** dewisott */
	public final static String JAVA_SHORT_CLASS = "ComplexSTAttribute";

	protected Complex d;

	/**
	 * constructor.
	 * 
	 * @param name
	 */
	public ComplexSTAttribute(String name) {
		super(name);
	}

	/**
	 * from DOM.
	 * 
	 * @param att
	 */
	public ComplexSTAttribute(Attribute att) {
		this(att.getLocalName());
		String v = att.getValue();
		if (v != null && !v.trim().equals(S_EMPTY)) {
			this.setCMLValue(v);
		}
	}

	/**
	 * from DOM.
	 * 
	 * @param att
	 *            to copy, except value
	 * @param value
	 */
	public ComplexSTAttribute(Attribute att, String value) {
		super(att, value.trim());
	}

	/**
	 * copy constructor
	 * 
	 * @param att
	 */
	public ComplexSTAttribute(ComplexSTAttribute att) {
		super(att);
		if (att.d != null) {
			this.d = new Complex(att.d);
		}
	}

	/**
	 * copy. uses copy constructor.
	 * 
	 * @return copy
	 */
	public Node copy() {
		return new ComplexSTAttribute(this);
	}

	/**
	 * get java type.
	 * 
	 * @return java type
	 */
	public String getJavaType() {
		return "double";
	}

	/**
	 * sets value. throws exception if of wrong type or violates restriction
	 * 
	 * @param s
	 *            the value
	 * @throws CMLRuntimeException
	 */
	public void setCMLValue(String s) {
		if (s != null && !s.trim().equals(S_EMPTY)) {
			Complex d;
			try {
				String ss = s.trim();
				if (ss.startsWith(S_PLUS)) {
					ss = ss.substring(1);
				}
				d = new Complex(ss);
			} catch (Exception nfe) {
				throw new RuntimeException("" + nfe, nfe);
			}
			this.setCMLValue(d);
		}
	}

	/**
	 * checks value of simpleType. throws CMLException if value does not check
	 * against SimpleType uses CMLType.checkvalue() fails if type is String or
	 * int or is a list
	 * 
	 * @param d the complex
	 * @throws CMLRuntimeException
	 *             wrong type or value fails
	 */
	public void checkValue(Complex d) throws CMLRuntimeException {
		if (cmlType != null) {
			cmlType.checkValue(d);
		}
	}

	/**
	 * checks value of simpleType. throws CMLException if value does not check
	 * against SimpleType uses CMLType.checkvalue() fails if type is String or
	 * int or is a list
	 * 
	 * @param d
	 *            the double
	 * @throws CMLRuntimeException
	 *             wrong type or value fails
	 */
	public void checkValue(double d) throws CMLRuntimeException {
		if (cmlType != null) {
			cmlType.checkValue(d);
		}
	}

	/**
	 * set and check value.
	 * 
	 * @param d
	 */
	public void setCMLValue(Complex d) {
		checkValue(d);
		this.d = new Complex(d);
		this.setValue("" + d);
	}

	/**
	 * get double.
	 * 
	 * @return value
	 */
	public Complex getComplex() {
		return d;
	}

	/**
	 * get java method.
	 * 
	 * @return java method
	 */
	public String getJavaGetMethod() {
		return JAVA_GET_METHOD;
	}

	/**
	 * get java short class name.
	 * 
	 * @return java short className
	 */
	public String getJavaShortClassName() {
		return JAVA_SHORT_CLASS;
	}

};
