package org.xmlcml.cml.base;

import nu.xom.Attribute;

/**
 * attribute representing a string value.
 * 
 */

public class StringAttribute extends CMLAttribute {

    /** */
    public final static String JAVA_TYPE = "String";

    /** */
    public final static String JAVA_GET_METHOD = "getString";

    /** */
    public final static String JAVA_SHORT_CLASS = "StringAttribute";

    protected String s;

    /**
     * constructor.
     * 
     * @param name
     */
    public StringAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public StringAttribute(Attribute att) {
        this(att.getLocalName());
        this.setCMLValue(att.getValue());
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public StringAttribute(StringAttribute att) {
        super(att);
        if (att.s != null) {
            this.s = att.s;
        }
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public StringAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, S_SPACE));
    }

    /**
     * set and check value.
     * 
     * @param s
     */
    public void setCMLValue(String s) {
        checkValue(s.trim());
        this.s = s.trim();
        this.setValue(s.trim());
    }

    /**
     * checks value of simpleType. uses CMLType.checkvalue() fails if type is
     * int or double or is a list
     * 
     * @param s
     *            the value
     * @throws CMLRuntimeException
     *             wrong type or value fails
     */
    public void checkValue(String s) {
        if (schemaType != null) {
            schemaType.checkValue(s);
        }
    }

    /**
     * get value.
     * 
     * @return value
     */
    public String getString() {
        return s;
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
