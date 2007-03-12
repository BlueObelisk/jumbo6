package org.xmlcml.cml.element;

import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;

/**
 * user-modifiable class supporting special attributes. these are attributes
 * which have specific code assocaietd with them. and which created there own
 * subclassed attribute objects normally of type string but others are possible
 */
public class SpecialAttribute implements CMLConstants {

    // counter for whether each attribute has been processed once
    static Set<String> specialAttributeSet = new HashSet<String>();

    /**
     * make CMLAttributes.
     * Note at this stage the value is NOT passed to this routine and
     * will be processed later with setCMLValue(String);
     * @param parent
     *            of attribute to provide context
     * @param attribute
     * @throws CMLException
     *             bad attribute name/value
     * @return Attribute
     */
    public static Attribute createSubclassedAttribute(CMLElement parent,
            Attribute attribute) throws CMLException {
        String attributeGroupName = (attribute instanceof CMLAttribute) ? ((CMLAttribute) attribute)
                .getAttributeGroupName()
                : attribute.getLocalName();
        CMLAttribute newAttribute = null;
        if (false) {
        } else if (attribute.getLocalName().equals(CountExpressionAttribute.NAME)) {
            // countExpression
            newAttribute = new CountExpressionAttribute(attribute);
        } else if (attribute.getLocalName().equals(DictRefAttribute.NAME)) {
            // dictRef - universal
            newAttribute = new DictRefAttribute(attribute);
        } else if (attribute.getLocalName().equals(MetadataNameAttribute.NAME)
                && parent.getLocalName().equals(CMLMetadata.TAG)) {
            // metadata/@name
            newAttribute = new MetadataNameAttribute(attribute);
        } else if (attribute.getLocalName().equals(ParentSIAttribute.NAME)) {
            // */@parentSI
            newAttribute = new ParentSIAttribute(attribute);
        } else if (attribute.getLocalName().equals(RepeatAttribute.NAME)) {
            // */@repeat
            newAttribute = new RepeatAttribute(attribute);
        } else if (attribute.getLocalName().equals(UnitAttribute.NAME)
                &&
                // angle/@units or torsion/@units defaults to normal
                (parent.getLocalName().equals(CMLAngle.TAG) || parent
                        .getLocalName().equals(CMLTorsion.TAG))) {
            newAttribute = CMLAttribute
                    .createSubclassedAttribute(CMLAttributeList
                            .getAttribute(attributeGroupName));
        } else if (attribute.getLocalName().equals(RefAttribute.NAME)) {
            // */@ref
            newAttribute = new RefAttribute(attribute);
        } else if (attribute.getLocalName().equals(UnitAttribute.NAME) ||
        // somewhat messy - we should be able to work out the type from the
        // schema
                attribute.getLocalName().equals("xUnits")
                || attribute.getLocalName().equals("yUnits")) {
            // */@units oe */@xUnits or */@yUnits
            newAttribute = new UnitAttribute(attribute);
        } else if (attribute.getLocalName().equals(UnitTypeAttribute.NAME)) {
            // */@unitType
            newAttribute = new UnitTypeAttribute(attribute);
        } else {
            newAttribute = CMLAttribute
                    .createSubclassedAttribute(CMLAttributeList
                            .getAttribute(attributeGroupName));
        }
        return newAttribute;
    }

    /**
     * get summary.
     * 
     * @return String
     */
    public String getSummary() {
        return S_EMPTY;

    }

    /* public */
    static void updateCMLAttributeList(String attributeGroupName,
            String attributeName, CMLAttribute newAttribute) {
        if (!specialAttributeSet.contains(newAttribute.getLocalName())) {
            specialAttributeSet.add(newAttribute.getLocalName());
            CMLAttribute superAttribute = CMLAttributeList.attributeMap
                    .get(attributeGroupName);
            CMLAttributeList.attributeMap.remove(attributeGroupName);
            superAttribute = CMLAttributeList.attributeMap
                    .get(attributeGroupName);
            // copy values from this attribute
            CMLAttributeList.attributeMap.put(attributeGroupName, newAttribute);
            superAttribute = CMLAttributeList.attributeMap
                    .get(attributeGroupName);
            newAttribute.setSchemaType(superAttribute.getSchemaType());
            newAttribute.setAttributeGroupName(attributeGroupName);
            newAttribute.setSummary(superAttribute.getSummary());
            newAttribute.setDescription(superAttribute.getDescription());
        }
    }
}
