package org.xmlcml.cml.base;

import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;

/**
 * generic subclassed Attribute for CML elements. often further subclassed into
 * strongly typed attributes
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CMLAttribute extends Attribute implements CMLConstants {

    final static Logger logger = Logger.getLogger(CMLAttribute.class.getName());

    protected CMLType schemaType;

    protected String summary;

    protected String description;

    protected String attributeGroupName; // used in code generation

    /**
     * creates attribute without value. do not use directly
     * 
     * @param name
     */
    public CMLAttribute(String name) {
        super(name, "");
    }

    /**
     * creates attribute.
     * 
     * @param name
     * @param value
     */
    public CMLAttribute(String name, String value) {
        super(name, value);
    }

    /**
     * creates attribute.
     * 
     * @param name
     *            must be qualified (colonized)
     * @param URI
     *            namespace
     * @param value
     * @throws NamespaceConflictException
     *             probably no prefix in name
     */
    protected CMLAttribute(String name, String URI, String value)
            throws nu.xom.NamespaceConflictException {
        super(name, URI, value);
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public CMLAttribute(CMLAttribute att) {
        super(att);
        this.schemaType = att.schemaType;
        // if (att.getLocalName().equals("dictRef")) {
        // new Exception().printStackTrace();
        // }
    }

    /**
     * semi copy constructor
     * 
     * @param att
     */
    public CMLAttribute(Attribute att) {
        super(att);
        // if (att.getLocalName().equals("dictRef")) {
        // new Exception().printStackTrace();
        // }
    }

    /**
     * copy constructor from empty attribute.
     * 
     * @param att
     *            to copy
     * @param value
     *            to add (may throw CMLRuntime)
     */
    protected CMLAttribute(Attribute att, String value) {
        this(att.getLocalName());
        this.setCMLValue(value);
    }

    /**
     * makes copy of correct class.
     * 
     * @return copy of node
     */
    public Node copy() {
        return CMLAttribute.createSubclassedAttribute(this, this.getValue());
    }

    /**
     * sets attributeGroup name. normally only useful when generating code when
     * the attributeGroup name may be different from the attribute name. it is
     * required for lookup
     * 
     * @param agn
     *            attributeGroup name
     */
    public void setAttributeGroupName(String agn) {
        attributeGroupName = agn;
    }

    /**
     * gets attributeGroup name. normally only useful when generating code when
     * the attributeGroup name may be different from the attribute name. it is
     * required for lookup
     * 
     * @return attributeGroup name
     */
    public String getAttributeGroupName() {
        return attributeGroupName;
    }

    /**
     * compares attributes. As we cannot override Node.equals() which compares
     * identity we have to compare components. order of sorting is: attribute
     * class schemaType name name value
     * 
     * null values of any component return -1
     * 
     * @param att
     *            to compare
     * @return 0 if all content is identical, -1 if this less than att, 1 if
     *         greater value
     * 
     */
    public int compareTo(Attribute att) {
        if (att == null) {
            return -1;
        }
        // same attribute?
        if (this == att) {
            return 0;
        }
        int order = 0;
        if (!(att instanceof CMLAttribute)) {
            order = -1;
        }
        CMLAttribute cmlAtt = (CMLAttribute) att;
        // schemas must either bosth be null or equal
        if (order == -1) {
        } else if (schemaType == null && cmlAtt.schemaType == null) {
        } else if (schemaType != null && cmlAtt.schemaType != null) {
            order = this.schemaType.compareTo(cmlAtt.schemaType);
        } else {
            order = -1;
        }
        if (order == 0) {
            order = this.getClass().getName().compareTo(
                    att.getClass().getName());
        }
        if (order == 0) {
            order = this.getLocalName().compareTo(cmlAtt.getLocalName());
        }
        if (order == 0) {
            order = this.getValue().compareTo(cmlAtt.getValue());
        }
        return (order == 0) ? 0 : order / Math.abs(order);
    }

    /**
     * get JavaType.
     * 
     * @return "String"
     */
    public String getJavaType() {
        return "String";
    }

    /**
     * get Java set method.
     * 
     * @return "setCMLValue"
     */
    public String getJavaSetMethod() {
        return "setCMLValue";
    }

    /**
     * get Java get method.
     * 
     * @return "getCMLValue"
     */
    public String getJavaGetMethod() {
        return "getCMLValue";
    }

    /**
     * get Java ShortClassName.
     * 
     * @return "CMLAttribute"
     */
    public String getJavaShortClassName() {
        return "CMLAttribute";
    }

    /**
     * get schema type.
     * 
     * @return "CMLAttribute"
     */
    public CMLType getSchemaType() {
        return schemaType;
    }

    /**
     * set schema type.
     * 
     * @param schemaType -
     *            could be null
     */
    public void setSchemaType(CMLType schemaType) {
        this.schemaType = schemaType;
    }

    /**
     * returns value as a typed object. if object is a primitive, return in
     * wrapper (e.g. Integer) object might be an array of primitives (e.g.
     * int[]) types are: String, String[], Integer, int[], Double, double[]
     * 
     * @return the value
     */
    public Object getCMLValue() {
        return getValue();
    }

    /**
     * sets value. often subclassed which will throw exception if of wrong type
     * 
     * @param s
     *            the value
     */
    public void setCMLValue(String s) {
        this.setValue(s);
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            String value) {
        CMLAttribute newAtt = createSubclassedAttribute(att);
        newAtt.setCMLValue(value);
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att) {
        CMLAttribute newAtt = null;
        if (att instanceof DoubleAttribute) {
            newAtt = new DoubleAttribute(att);
        } else if (att instanceof DoubleArrayAttribute) {
            newAtt = new DoubleArrayAttribute(att);
        } else if (att instanceof IntAttribute) {
            newAtt = new IntAttribute(att);
        } else if (att instanceof IntArrayAttribute) {
            newAtt = new IntArrayAttribute(att);
        } else if (att instanceof StringAttribute) {
            newAtt = new StringAttribute(att);
        } else if (att instanceof StringArrayAttribute) {
            newAtt = new StringArrayAttribute(att);
        } else if (att instanceof Attribute) {
            newAtt = new CMLAttribute(att);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            double value) {
        DoubleAttribute newAtt = null;
        if (att instanceof DoubleAttribute) {
            newAtt = new DoubleAttribute(att);
            newAtt.setCMLValue(value);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            String[] value) {
        StringArrayAttribute newAtt = null;
        if (att instanceof StringArrayAttribute) {
            newAtt = new StringArrayAttribute(att);
            newAtt.setCMLValue(value);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            double[] value) {
        DoubleArrayAttribute newAtt = null;
        if (att instanceof DoubleArrayAttribute) {
            newAtt = new DoubleArrayAttribute(att);
            newAtt.setCMLValue(value);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            int[] value) {
        IntArrayAttribute newAtt = null;
        if (att instanceof IntArrayAttribute) {
            newAtt = new IntArrayAttribute(att);
            newAtt.setCMLValue(value);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * create subclassed attribute from attribute. essentially a copy
     * constructor.
     * 
     * @param att
     *            to copy
     * @param value
     * @return an attribute of same class as att (null if cannot find class)
     */
    public static CMLAttribute createSubclassedAttribute(Attribute att,
            int value) {
        IntAttribute newAtt = null;
        if (att instanceof IntAttribute) {
            newAtt = new IntAttribute(att);
            newAtt.setCMLValue(value);
        } else {
            logger.severe("unknown attribute type " + att.getClass());
        }
        return newAtt;
    }

    /**
     * get documentation summary.
     * 
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * set documentation summary.
     * 
     * @param s
     *            the summary
     */
    public void setSummary(String s) {
        if (s != null) {
            summary = s;
            if (!summary.endsWith(S_PERIOD)) {
                summary += S_PERIOD;
            }
        }
    }

    /**
     * get Documentation.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set Documentation.
     * 
     * @param d
     *            the description
     */
    public void setDescription(String d) {
        description = d;
    }

}
