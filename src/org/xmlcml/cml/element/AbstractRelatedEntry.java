package org.xmlcml.cml.element;

import nu.xom.Attribute;
import org.xmlcml.cml.base.*;
import java.util.HashMap;
import java.util.Map;

/** An entry related in some way to a dictionary entry.
*
* 
* The range of relationships is not restricted but should include parents, aggregation, seeAlso and so on. DataCategories from ISO12620 can be referenced through the namespaced mechanism.
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractRelatedEntry extends CMLElement {

// fields;
    /** table mapping attribute names to attributegroup names*/
    public static Map<String, String> attributeGroupNameTable = new HashMap<String, String>();
    /** local name*/
    public final static String TAG = "relatedEntry";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractRelatedEntry() {
        super("relatedEntry");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractRelatedEntry to copy

    */

    public AbstractRelatedEntry(AbstractRelatedEntry old) {
        super((CMLElement) old);
    }

    static {
        attributeGroupNameTable.put("type", "relatedEntryType");
        attributeGroupNameTable.put("href", "href");
    };
    /** get attributeGroupName from attributeName.
    *
    * @param attributeName attribute name
    * @return String
    */
    public String getAttributeGroupName(String attributeName) {
            return attributeGroupNameTable.get(attributeName);
    }
    /** Type of relatedEntry.
    *
    * Type represents a the type of relationship in a relatedEntry element.
    * --type info--
    * 
    * Type of relatedEntry.
    * Type represents a the type of relationship in a relatedEntry element.

    * @return CMLAttribute
    */
    public CMLAttribute getTypeAttribute() {
        return (CMLAttribute) getAttribute("type");
    }
    /** Type of relatedEntry.
    *
    * Type represents a the type of relationship in a relatedEntry element.
    * --type info--
    * 
    * Type of relatedEntry.
    * Type represents a the type of relationship in a relatedEntry element.

    * @return String
    */
    public String getType() {
        CMLAttribute _att_type = (CMLAttribute) getAttribute("type");
        if (_att_type == null) {
            return null;
        }
        return ((StringAttribute)_att_type).getString();
    }
    /** Type of relatedEntry.
    *
    * Type represents a the type of relationship in a relatedEntry element.
    * --type info--
    * 
    * Type of relatedEntry.
    * Type represents a the type of relationship in a relatedEntry element.

    * @param value type value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setType(String value) throws CMLRuntimeException {
            CMLAttribute _att_type = null;
            try {
        		_att_type = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("relatedEntryType"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_type == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : relatedEntryType; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_type);
        ((StringAttribute)_att_type).setCMLValue(value);
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @return CMLAttribute
    */
    public CMLAttribute getHrefAttribute() {
        return (CMLAttribute) getAttribute("href");
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @return String
    */
    public String getHref() {
        CMLAttribute _att_href = (CMLAttribute) getAttribute("href");
        if (_att_href == null) {
            return null;
        }
        return ((StringAttribute)_att_href).getString();
    }
    /** address of a resource.
    *
    * Links to another element in the same or other file. For dictionary/@dictRef requires the prefix and the physical URI 
    * address to be contained within the same file. We can anticipate that
    *  better mechanisms will arise - perhaps through XMLCatalogs.
    *  At least it works at present.
    * --type info--
    * 
    * Pattern: [a-z]*:.*

    * @param value href value
    * @throws CMLRuntimeException attribute wrong value/type

    */
    public void setHref(String value) throws CMLRuntimeException {
            CMLAttribute _att_href = null;
            try {
        		_att_href = (CMLAttribute) org.xmlcml.cml.element.SpecialAttribute.createSubclassedAttribute(this, CMLAttributeList.getAttribute("href"));
        	} catch (CMLException e) {
        		throw new CMLRuntimeException("bug "+e);
        	}
            if (_att_href == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : href; probably incompatible attributeGroupName and attributeName");
            }
            super.addAttribute(_att_href);
        ((StringAttribute)_att_href).setCMLValue(value);
    }
    /** overrides addAttribute(Attribute).
    *
    * reroutes calls to setFoo()

    * @param att  attribute

    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("type")) {
            setType(value);
        } else if (name.equals("href")) {
            setHref(value);
        } else {
            super.addAttribute(att);
        }
    }
}
