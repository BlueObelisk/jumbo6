package org.xmlcml.cml.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.Text;

/** user-modifiable class supporting NodeFactory.
*
* autogenerated from schema
* use as a shell which can be edited

*/
public class CMLNodeFactory extends NodeFactory implements CMLConstants {

// fields;
	
    /** current of current node*/
    private Element current;
    /** current element stack*/
    private Stack<Element> stack = new Stack<Element>();
    /** must give simple documentation.
     */
     private Map<String, CMLElement> factoryElementMap;

    // singleton
     /** singleton node factory.
      */
    public static final CMLNodeFactory nodeFactory = new CMLNodeFactory();
    static {
    	nodeFactory.init();
    }
    
    private CMLNodeFactory() {
    	factoryElementMap = new HashMap<String, CMLElement>();
    }
    
    void init() {
//    	System.out.println("NODE FACTORY INIT");
    }

    /** callback from element start tag.
    *
    * @param name element name
    * @param namespace namespace of element
    * @return Element
    */
    public Element startMakingElement(String name, String namespace) {
// fields;
        /** new element*/
        Element newElement;
        int idx = name.indexOf(CMLUtil.S_COLON);
        if (idx != -1) {
        	name = name.substring(idx+1);
        }
        // convert old namespaces
        namespace = CMLNamespace.guessNamespace(namespace);
        
        if (namespace.equals(null)) {
            newElement = new Element(name);
        } else if (namespace.trim().length() == 0) {
            // this seems to be what is passed if there is no namespace
            newElement = new Element(name);
        } else if (!namespace.equals(CMLUtil.CML_NS)) {
            newElement = new Element(name, namespace);
            
// end of part 1
        } else {
            CMLElement factoryElement = factoryElementMap.get(name);
            if (factoryElement == null) {
	            Class newClass = null;
	        	try {
		        	newClass = Class.forName(ELEMENT_CLASS_BASE+S_PERIOD+
		        		CMLUtil.makeCMLName(name));
	            } catch (Exception e) {
	                throw new CMLRuntimeException("Unknown CML Element : "+name);
	            }
	            try {
    	            factoryElement = (CMLElement) newClass.newInstance();
    	        } catch (Exception e) {
    	        	System.out.println("CLASS "+newClass);
    	        	System.out.println(newClass.getName());
    	        	e.printStackTrace();
    	            throw new CMLRuntimeException("Cannot instantiate because: "+name+"["+e+"]");
    	        }
    	        factoryElementMap.put(name, factoryElement);
    	    }
            newElement = factoryElement.makeElementInContext((Element)current);
        }
        stack.push(current);
        current = newElement;
        return newElement;
    }
    /** callback from element end tag.
    *
    * @param element the context element
    * @return Nodes
    */
    public Nodes finishMakingElement(Element element) {
        Element parent = stack.pop();
        if (current instanceof CMLElement) {
            ((CMLElement) current).finishMakingElement(parent);
        }
        current = parent;
        Nodes nodes = new Nodes();
        nodes.append(element);
        return nodes;
    }
    /** callback from each attribute.
    *
    * @param attributeName attribute name
    * @param URI attribute namespace
    * @param value attribute value
    * @param type attribute type (ignored)
    * @return Nodes
    */
    public Nodes makeAttribute(String attributeName, String URI, String value, Attribute.Type type) {
        Nodes nodes = new Nodes();
        Attribute attribute = null;
        int prefixLoc = attributeName.indexOf(":");
        if (URI != null && URI.trim().length() != 0 && prefixLoc != -1) {
    // namespaced non-cml attribute is allowed
            attribute = new Attribute(attributeName, URI, value);
        } else if (current instanceof CMLElement) {
            CMLElement currentCML = (CMLElement) current;
            String attributeGroupName = AttributeFactory.attributeFactory.getAttributeGroupName(attributeName, currentCML.getLocalName());
            if (attributeGroupName == null) {
            	attribute = new Attribute(attributeName, value);
            } else {
            	attribute = AttributeFactory.attributeFactory.getAttributeByGroupName(attributeGroupName);
            	((CMLAttribute)attribute).setCMLValue(value);
            }
        } else if (prefixLoc == -1) {
    // non-prefixed non-cml element
            attribute = new Attribute(attributeName, value);
        } else if (prefixLoc != -1) {
    // prefixed non-cml element
            attribute = new Attribute(attributeName, URI, value);
        }
        if (attribute != null) {
        	nodes.append(attribute);
        }
        return nodes;
    }
    
    /** FIXME text - needs to trap/check values.
    *
    * @param text String content
    * @return Nodes
    */
    public Nodes makeText(String text) {
        Nodes nodes = new Nodes();
        nodes.append(new Text(text));
        return nodes;
    }
    /** no-op.
    *
    * @param text String content

    */
    public static void main(String text) {
    }
}
// end of part 3
