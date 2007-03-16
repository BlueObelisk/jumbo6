package org.xmlcml.cml.element;


import nu.xom.*;

import org.xmlcml.cml.base.*;
import org.xmlcml.cml.attribute.*;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractAnnotation extends CMLElement {
    /** local name*/
    public final static String TAG = "annotation";
    /** constructor. */    public AbstractAnnotation() {
        super("annotation");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractAnnotation(AbstractAnnotation old) {
        super((CMLElement) old);
    }
// attribute:   id

    /** cache */
    IdAttribute _att_id = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getIdAttribute() {
        return (CMLAttribute) getAttribute("id");
    }
    /** null
    * @return String
    */
    public String getId() {
        IdAttribute att = (IdAttribute) this.getIdAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setId(String value) throws CMLRuntimeException {
        IdAttribute att = null;
        if (_att_id == null) {
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "annotation");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
    }
// element:   documentation

    /** null
    * @param documentation child to add
    */
    public void addDocumentation(AbstractDocumentation documentation) {
        documentation.detach();
        this.appendChild(documentation);
    }
    /** null
    * @return CMLElements<CMLDocumentation>
    */
    public CMLElements<CMLDocumentation> getDocumentationElements() {
        Elements elements = this.getChildElements("documentation", CML_NS);
        return new CMLElements<CMLDocumentation>(elements);
    }
// element:   appinfo

    /** null
    * @param appinfo child to add
    */
    public void addAppinfo(AbstractAppinfo appinfo) {
        appinfo.detach();
        this.appendChild(appinfo);
    }
    /** null
    * @return CMLElements<CMLAppinfo>
    */
    public CMLElements<CMLAppinfo> getAppinfoElements() {
        Elements elements = this.getChildElements("appinfo", CML_NS);
        return new CMLElements<CMLAppinfo>(elements);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("id")) {
            setId(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
