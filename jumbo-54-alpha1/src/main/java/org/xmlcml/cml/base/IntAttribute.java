package org.xmlcml.cml.base;

import nu.xom.Attribute;

/**
 * attribute representing an int value.
 */

public class IntAttribute extends CMLAttribute {

    /** */
    public final static String JAVA_TYPE = "int";

    /** */
    public final static String JAVA_GET_METHOD = "getInt";

    /** */
    public final static String JAVA_SHORT_CLASS = "IntAttribute";

    protected Integer i;

    /**
     * constructor.
     * 
     * @param name
     */
    public IntAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public IntAttribute(Attribute att) {
        this(att.getLocalName());
        String v = att.getValue();
        if (v != null && !v.trim().equals(S_EMPTY)) {
            this.setCMLValue(v);
        }
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public IntAttribute(IntAttribute att) {
        super(att);
        if (att.i != null) {
            this.i = new Integer(att.i.intValue());
        }
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public IntAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, S_SPACE));
    }

    /**
     * sets value. throws exception if of wrong type or violates restriction
     * 
     * @param s
     *            the value
     * @throws CMLRuntimeException
     */
    public void setCMLValue(String s) {
        int i;
        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            throw new CMLRuntimeException(S_EMPTY + nfe);
        }
        this.setCMLValue(i);
    }

    /**
     * set and check value.
     * 
     * @param i
     */
    public void setCMLValue(int i) {
        try {
            checkValue(i);
        } catch (CMLException e) {
            throw new CMLRuntimeException(S_EMPTY + e);
        }
        this.i = new Integer(i);
        this.setValue(S_EMPTY + i);
    }

    /**
     * checks value of simpleType. throws CMLException if value does not check
     * against SimpleType uses CMLType.checkvalue() fails if type is String or
     * double or is a list
     * 
     * @param i
     *            the value
     * @throws CMLException
     *             wrong type or value fails
     */
    public void checkValue(int i) throws CMLException {
        if (schemaType != null) {
            schemaType.checkValue(i);
        }
    }

    /**
     * returns value as Integer.
     * 
     * @return value
     */
    public Object getCMLValue() {
        return i;
    }

    /**
     * returns value as int.
     * 
     * @return int
     */
    public int getInt() {
        if (i == null) {
            throw new CMLRuntimeException("integer attribute unset");
        }
        return i.intValue();
    }

    /**
     * get java type.
     * 
     * @return java type
     */
    public String getJavaType() {
        return JAVA_TYPE;
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
