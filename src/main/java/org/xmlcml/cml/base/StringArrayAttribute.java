package org.xmlcml.cml.base;

import nu.xom.Attribute;

import org.xmlcml.euclid.Util;

/**
 * attribute representing an array of Strings.
 */

public class StringArrayAttribute extends CMLAttribute {

    /** */
    public final static String JAVA_TYPE = "String[]";

    /** */
    public final static String JAVA_GET_METHOD = "getStringArray";

    /** */
    public final static String JAVA_SHORT_CLASS = "StringArrayAttribute";

    protected String[] ss = null;

    protected int length = -1;

    /**
     * constructor.
     * 
     * @param name
     */
    public StringArrayAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public StringArrayAttribute(Attribute att) {
        this(att.getLocalName());
        this.setCMLValue(att.getValue());
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public StringArrayAttribute(StringArrayAttribute att) {
        super(att);
        if (att.ss != null) {
            this.ss = new String[att.ss.length];
            for (int i = 0; i < ss.length; i++) {
                this.ss[i] = att.ss[i];
            }
        }
        this.length = att.length;
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public StringArrayAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, S_SPACE));
    }

    /**
     * sets value. throws exception if of wrong type or violates restriction
     * 
     * @param s
     *            the value
     */
    public void setCMLValue(String s) {
        String[] split = s.trim().split(S_WHITEREGEX);
        this.setCMLValue(split);
    }

    /**
     * set and check value.
     * 
     * @param ss
     */
    public void setCMLValue(String[] ss) {
        checkValue(ss);
        this.ss = ss;
        this.setValue(Util.concatenate(ss, S_SPACE));
    }

    /**
     * checks value of simpleType. throws CMLException if value does not check
     * against SimpleType uses CMLType.checkvalue() fails if type is int or
     * double or is not a list
     * 
     * @param ss
     *            the String array
     * @throws CMLRuntimeException
     *             wrong type or value fails
     */
    public void checkValue(String[] ss) {
        if (schemaType != null) {
            schemaType.checkValue(ss);
        }
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public Object getCMLValue() {
        return ss;
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public String[] getStringArray() {
        return ss;
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
