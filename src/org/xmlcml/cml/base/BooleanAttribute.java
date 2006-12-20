package org.xmlcml.cml.base;

import nu.xom.Attribute;

/**
 * attribute representing an boolean value.
 */

public class BooleanAttribute extends CMLAttribute {

    /** */
    public final static String JAVA_TYPE = "boolean";

    /** */
    public final static String JAVA_GET_METHOD = "getBoolean";

    /** */
    public final static String JAVA_SHORT_CLASS = "BooleanAttribute";

    protected Boolean b;

    /**
     * constructor.
     * 
     * @param name
     */
    public BooleanAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public BooleanAttribute(Attribute att) {
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
    public BooleanAttribute(BooleanAttribute att) {
        super(att);
        if (att.b != null) {
            this.b = new Boolean(att.b.booleanValue());
        }
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public BooleanAttribute(Attribute att, String value) {
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
        boolean i;
        try {
            i = Boolean.parseBoolean(s.trim());
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
    public void setCMLValue(boolean i) {
        try {
            checkValue(i);
        } catch (CMLException e) {
            throw new CMLRuntimeException(S_EMPTY + e);
        }
        this.b = new Boolean(i);
        this.setValue(S_EMPTY + i);
    }

    /**
     * checks value of simpleType. throws CMLException if value does not check
     * against SimpleType uses CMLType.checkvalue() fails if type is String or
     * double or is a list
     * 
     * @param b
     *            the value
     * @throws CMLException
     *             wrong type or value fails
     */
    public void checkValue(boolean b) throws CMLException {
        if (schemaType != null) {
            schemaType.checkValue(b);
        }
    }

    /**
     * returns value as Booleaneger.
     * 
     * @return value
     */
    public Object getCMLValue() {
        return b;
    }

    /**
     * returns value as boolean.
     * 
     * @return value
     */
    public boolean getBoolean() {
        if (b == null) {
            throw new CMLRuntimeException("booleaneger attribute unset");
        }
        return b.booleanValue();
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
