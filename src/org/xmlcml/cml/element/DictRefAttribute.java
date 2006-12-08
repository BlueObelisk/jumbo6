package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting DictRefAttribute. supports dictRef attribute
 */
public class DictRefAttribute extends NamespaceRefAttribute {

    /** */
    public final static String NAME = "dictRef";

    /**
     * constructor.
     * 
     */
    public DictRefAttribute() {
        super(NAME);
    }

    /**
     * constructor.
     * 
     * @param name
     * @param value
     */
    public DictRefAttribute(String name, String value) {
        super(NAME, value);
    }

    /**
     * constructor.
     * 
     * @param att
     */
    public DictRefAttribute(Attribute att) {
        super(att);
    }

    /**
     * gets dictRef attribute from element or its parent. elements which might
     * carry dictRef such as scalar may be contained within a parent such as
     * property. In this case the dictRef may be found on the parent. This
     * routine returns whichever is not null
     * 
     * @param el
     *            the element
     * @return the attribute
     */
    public static DictRefAttribute getDictRefFromElementOrParent(CMLElement el) {
        DictRefAttribute dictRefAttribute = 
            (DictRefAttribute) el.getAttribute(NAME);
        if (dictRefAttribute == null) {
            Node parent = el.getParent();
            if (parent instanceof CMLElement) {
                CMLElement parentElement = (CMLElement) parent;
                dictRefAttribute = (DictRefAttribute) 
                    parentElement.getAttribute(NAME);
            }
        }
        return dictRefAttribute;
    }

    /**
     * checks an element recursively for valid dictRefs. checks that all
     * dictRefs in a element resolve.
     * 
     * @param element
     *            to check (at present only CML elements are checked)
     * @param dictionaryMap
     * @return list of errors (empty if none)
     */
    public List<String> checkAttribute(Element element,
            GenericDictionaryMap dictionaryMap) {
        List<String> errorList = new ArrayList<String>();
        if (element instanceof CMLElement) {
            errorList.addAll(check((CMLElement)element, CMLScalar.TAG, NAME, dictionaryMap));
            errorList.addAll(check((CMLElement)element, CMLArray.TAG, NAME, dictionaryMap));
            errorList.addAll(check((CMLElement)element, CMLParameter.TAG, NAME, dictionaryMap));
            errorList.addAll(check((CMLElement)element, CMLProperty.TAG, NAME, dictionaryMap));
        }
        return errorList;
    }

}
