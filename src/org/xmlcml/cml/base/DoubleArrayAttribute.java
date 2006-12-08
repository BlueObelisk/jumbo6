package org.xmlcml.cml.base;

import nu.xom.Attribute;

import org.xmlcml.euclid.Util;

/**
 * attribute representing an array of doubles.
 */

public class DoubleArrayAttribute extends CMLAttribute {

    /** */
    public final static String JAVA_TYPE = "double[]";

    /** */
    public final static String JAVA_GET_METHOD = "getDoubleArray";

    /** */
    public final static String JAVA_SHORT_CLASS = "DoubleArrayAttribute";

    protected double[] dd = null;

    protected int length = -1;

    /**
     * constructor.
     * 
     * @param name
     */
    public DoubleArrayAttribute(String name) {
        super(name);
    }

    /**
     * construct from existing attribute.
     * 
     * @param att
     */
    public DoubleArrayAttribute(Attribute att) {
        this(att.getLocalName());
        this.setCMLValue(att.getValue());
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public DoubleArrayAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, S_SPACE));
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public DoubleArrayAttribute(DoubleArrayAttribute att) {
        super(att);
        if (att.dd != null) {
            this.dd = new double[att.dd.length];
            for (int i = 0; i < dd.length; i++) {
                this.dd[i] = att.dd[i];
            }
        }
        this.length = att.length;
    }

    /**
     * sets value. throws exception if of wrong type or violates restriction
     * 
     * @param s
     *            the value
     */
    public void setCMLValue(String s) {
        if (s != null && !s.trim().equals(S_EMPTY)) {
            double[] dd = split(s.trim().replace(S_WHITEREGEX, S_SPACE),
                    S_WHITEREGEX);
            this.setCMLValue(dd);
        }
    }

    /**
     * set and check value.
     * 
     * @param dd
     * @throws CMLRuntimeException
     */
    public void setCMLValue(double[] dd) throws CMLRuntimeException {
        checkValue(dd);
        this.dd = new double[dd.length];
        for (int i = 0; i < dd.length; i++) {
            this.dd[i] = dd[i];
        }
        this.setValue(Util.concatenate(dd, S_SPACE));
    }

    /**
     * checks value of simpleType. throws CMLException if value does not check
     * against SimpleType uses CMLType.checkvalue() fails if type is String or
     * int or is not a list
     * 
     * @param dd
     *            the double array
     * @throws CMLRuntimeException
     *             wrong type or value fails
     */
    public void checkValue(double[] dd) throws CMLRuntimeException {
        if (schemaType != null) {
            schemaType.checkValue(dd);
        }
    }

    /**
     * splits string into doubles.
     * 
     * @param s
     *            the string
     * @param delim
     *            delimiter (if null defaults to " ");
     * @return array
     * @throws CMLRuntimeException
     *             cannot parse as doubles
     */
    public static double[] split(String s, String delim) {
        String sss = s;
        if (delim == null || delim.trim().equals(S_EMPTY)
                || delim.equals(S_WHITEREGEX)) {
            delim = S_WHITEREGEX;
            sss = sss.trim();
        } else {
        }
        String[] ss = sss.split(delim);
        double[] dd = new double[ss.length];
        for (int i = 0; i < ss.length; i++) {
            try {
                dd[i] = new Double(ss[i]).doubleValue();
            } catch (NumberFormatException nfe) {
                throw new CMLRuntimeException("" + nfe);
            }
        }
        return dd;
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public Object getCMLValue() {
        return dd;
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public double[] getDoubleArray() {
        return dd;
    }

    /**
     * get Java type.
     * 
     * @return type
     */
    public String getJavaType() {
        return JAVA_TYPE;
    }

    /**
     * get method.
     * 
     * @return method
     */
    public String getJavaGetMethod() {
        return JAVA_GET_METHOD;
    }

    /**
     * get short class name.
     * 
     * @return classname
     */
    public String getJavaShortClassName() {
        return JAVA_SHORT_CLASS;
    }

};
