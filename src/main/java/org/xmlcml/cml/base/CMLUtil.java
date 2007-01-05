package org.xmlcml.cml.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.XPathContext;
import nu.xom.canonical.Canonicalizer;

import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.euclid.Util;

/*-- @SuppressWarnings("all")
 annotation is: 
 all :
 any warning
 boxing :
 autoboxing conversion
 dep-ann :
 missing @Deprecated annotation
 deprecation :
 deprecation outside deprecated code
 incomplete-switch :
 incomplete enum switch (enumSwitch)
 hiding : 
 field hiding another variable (fieldHiding)
 local variable hiding another variable (localHiding)
 type parameter hiding another type (typeHiding)
 hidden catch block (maskedCatchBlock)
 finally :
 finally block not completing normally
 static-access : 
 indirect reference to static member (indirectStatic)
 non-static reference to static member (staticReceiver)
 nls :
 string literal lacking non-nls tag //$NON-NLS-<n>$
 serial :
 missing serialVersionUID
 unqualified-field-access :
 unqualified reference to field (unQualifiedField)
 unchecked :
 unchecked type operation
 unused : 
 unread method parameter (unusedArgument)
 unread local variable (unusedLocal)
 unused private member declaration (unusedPrivate)
 unused declared thrown exception (unusedThrown)
 synthetic-access : 
 synthetic access for innerclass (syntheticAccess)
 */
/**
 * 
 * <p>
 * static utilities to help manage common constructs.
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public abstract class CMLUtil implements CMLConstants {

    /** messages */
    public enum Message {
        /** not yet implemented */
        NYI("not yet implemented"),
        ;
        /** value*/
        public String value;
        private Message(String v) {
            value = v;
        }
    }
    
	// ========================== utilities ====================== //

	/** checks that name is QName.
	 * @param name
	 *            of XMLName
	 * @throws CMLException
	 *             not colonized
	 */
	public final static void checkPrefixedName(String name) throws CMLException {
		if (name == null || name.indexOf(S_COLON) < 1) {
			throw new CMLException("Unprefixed name (" + name + S_RBRAK);
		}
	}

    /** get prefix from qualified name.
     * 
     * @param s
     * @return prefix (or empty string)
     */
    public static String getPrefix(String s) {
    	int idx = s.indexOf(S_COLON);
    	return (idx == -1) ? S_EMPTY : s.substring(0, idx);
    }

    /** get localName from qualified name.
     * 
     * @param s
     * @return localName (or empty string)
     */
    public static String getLocalName(String s) {
    	int idx = s.indexOf(S_COLON);
    	return (idx == -1) ? s : s.substring(idx+1);
    }

	/**
	 * converts an Elements to a java array. we might convert code to use
	 * Elements through later so this would be unneeded
	 * 
	 * @param elements
	 * @param obj
	 *            type of array (e.g. "new CMLAtom[0]"
	 * @return the java array 0f objects
	 */
	public final static Object[] toArray(Elements elements, Object[] obj) {
		List<Element> list = new ArrayList<Element>();
		for (int i = 0; i < elements.size(); i++) {
			list.add(elements.get(i));
		}
		return list.toArray(obj);
	}

    /**
     * debug an element. outputs XML to sysout
     * 
     * @param el
     *            the element
     */
    public static void debug(Element el) {
        try {
            debug(el, System.out);
        } catch (IOException e) {
            throw new CMLRuntimeException("BUG " + e);
        }
    }

    /**
     * debug an element. outputs XML to syserr
     * 
     * @param el
     *            the element
     */
    public static void debugToErr(Element el) {
        try {
            debug(el, System.err);
        } catch (IOException e) {
            throw new CMLRuntimeException("BUG " + e);
        }
    }

	/**
	 * debug an element.
	 * 
	 * @param el
	 *            the element
	 * @param os
	 *            output stream
	 * @throws IOException
	 */
	public static void debug(Element el, OutputStream os) throws IOException {
		Document document;
		Node parent = el.getParent();
		if (parent instanceof Document) {
			document = (Document) parent;
		} else {
			Element copyElem = new Element(el);
			document = new Document(copyElem);
		}
		Serializer serializer = new Serializer(os);
		// serializer.setLineSeparator("\r\n");
		serializer.write(document);
	}

	/**
	 * convenience method to get resource from XMLFile. the resource is packaged
	 * with the classes for distribution. typical filename is
	 * org/xmlcml/molutil/elementdata.xml for file elementdata.xml in class
	 * hierarchy org.xmlcml.molutil
	 * 
	 * @param filename
	 *            relative to current class hierarchy.
	 * @return document for resource
	 * @throws IOException
	 */
	public static Document getXMLResource(String filename) throws IOException {
		Document document = null;
		InputStream in = null;
		try {
			in = Util.getInputStreamFromResource(filename);
			document = (Document) new Builder().build(in);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CMLRuntimeException("" + e + " in " + filename);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return document;
	}

    /** convenience routine to get child nodes
     * (iterating through getChild(i) is fragile if children are removed)
     * @param el may be null
     * @return list of children (immutable) - empty if none
     */
    public static List<Node> getChildNodes(Element el) { 
        List<Node> childs = new ArrayList<Node>();
        if (el != null) {
            for (int i = 0; i < el.getChildCount(); i++) {
                childs.add(el.getChild(i));
            }
        }
        return childs;
    }

    /** parses XML string into element.
     * convenience method to avoid trapping exceptions when string
     * is known to be valid
     * @param xmlString
     * @return root element
     * @throws CMLRuntimeException
     */
    public static Element parseXML(String xmlString) throws CMLRuntimeException {
        Element root = null;
        try {
            Document doc = new Builder().build(new StringReader(xmlString));
            root = doc.getRootElement();
        } catch (Exception e) {
            throw new CMLRuntimeException(e);
        }
        return root;
    }

    /** convenience routine to get query nodes
     * (iterating thorugh get(i) is fragile if nodes are removed)
     * @param node (can be null)
     * @param xpath xpath relative to node
     * @param context 
     * @return list of nodes (immutable) - empty if none
     */
    public static List<Node> getQueryNodes(Node node, String xpath, XPathContext context) { 
        List<Node> nodeList = new ArrayList<Node>();
        if (node != null) {
            Nodes nodes = node.query(xpath, context);
            for (int i = 0; i < nodes.size(); i++) {
                nodeList.add(nodes.get(i));
            }
        }
        return nodeList;
    }

    /** convenience routine to get query nodes
     * (iterating thorugh get(i) is fragile if nodes are removed)
     * @param node
     * @param xpath
     * @return list of nodes (immutable) - empty if none or null node
     */
    public static List<Node> getQueryNodes(Node node, String xpath) { 
        List<Node> nodeList = new ArrayList<Node>();
        if (node != null) {
            Nodes nodes = node.query(xpath);
            for (int i = 0; i < nodes.size(); i++) {
                nodeList.add(nodes.get(i));
            }
        }
        return nodeList;
    }

    /** get next sibling.
     * @author Eliotte Rusty Harold
     * @param current may be null
     * @return following sibling or null 
     */
    public static Node getFollowingSibling(Node current) {
        Node node = null;
        if (current != null) {
            ParentNode parent = current.getParent();
            if (parent != null) {
                int index = parent.indexOf(current);
                if (index+1 < parent.getChildCount()) {
                    node = parent.getChild(index+1);
                }
            }
        }
        return node;
    }
    
    /** get previous sibling.
     * @param current
     * @return previous sibling
     */
    public static Node getPrecedingSibling(Node current) {
        Node node = null;
        if (current != null) {
            ParentNode parent = current.getParent();
            if (parent != null) {
                int index = parent.indexOf(current);
                if (index > 0) {
                    node = parent.getChild(index-1);
                }
            }
        }
        return node;
    }

    /** gets last text descendant of element.
     * this might be referenced from the following-sibling and will therefore
     * be the immediately preceding chunk of text in document order
     * if the node is a text node returns itself
     * @param node
     * @return Text node or null
     */
    public static Text getLastTextDescendant(Node node) {
        List<Node> l = CMLUtil.getQueryNodes(
                node, ".//text() | self::text()");
        return (l.size() == 0) ? null : (Text) l.get(l.size()-1);
    }


    /** gets first text descendant of element.
     * this might be referenced from the preceding-sibling and will therefore
     * be the immediately following chunk of text in document order
     * if the node is a text node returns itself
     * @param node
     * @return Text node or null
     */
    public static Text getFirstTextDescendant(Node node) {
        List<Node> l = CMLUtil.getQueryNodes(
                node, ".//text() | self::text()");
        return (l.size() == 0) ? null : (Text) l.get(0);
    }

    /** transfers children of 'from' to 'to'.
     * 
     * @param from (will be left with no children)
     * @param to (will gain 'from' children appended after any
     * existing children
     */
    public static void transferChildren(Element from, Element to) {
        int nc = from.getChildCount();
        int tc = to.getChildCount();
        for (int i = nc-1; i >=0; i--) {
            Node child = from.getChild(i);
            child.detach();
            to.insertChild(child, tc);
        }
    }
    
    /**
     * get XOM default canonical string.
     * 
     * @param node
     * @return the string
     */
    public static String getCanonicalString(Node node) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer canon = new Canonicalizer(baos);
        try {
            canon.write(node);
        } catch (IOException e) {
            throw new CMLRuntimeException("should never throw " + e);
        }
        return baos.toString();
    }

    /** remeoves all whitespace-only text nodes.
     * @param element to strip whitespace from
     */
    public static void removeWhitespaceNodes(Element element) {
        int nChild = element.getChildCount();
        List<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < nChild; i++) {
            Node node = element.getChild(i);
            if (node instanceof Text) {
                if (node.getValue().trim().length() == 0) {
                    nodeList.add(node);
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                removeWhitespaceNodes(childElement);
            } else {
            }
        }
        for (Node node : nodeList) {
            node.detach();
        }
    }

    /** sets text content of element.
     * Does not support mixed content.
     * @param element
     * @param s
     * @throws CMLRuntimeException if element already has element content
     */
    public static void setXMLContent(Element element, String s) {
        List<Node> elements = CMLUtil.getQueryNodes(element, S_STAR);
        if (elements.size() > 0) {
            throw new CMLRuntimeException("Cannot set text with element children");
        }
        Text text = CMLUtil.getFirstTextDescendant(element);
        if (text == null) {
            text = new Text(s);
            element.appendChild(text);
        } else {
            text.setValue(s);
        }
    }
    
    /** sets text content of element.
     * Does not support mixed content.
     * @param element
     * @return text value
     * @throws CMLRuntimeException if element already has element content
     */
    public static String getXMLContent(Element element) {
        List<Node> elements = CMLUtil.getQueryNodes(element, S_STAR);
        if (elements.size() > 0) {
            throw new CMLRuntimeException("Cannot get text with element children");
        }
        return element.getValue();
    }
    
    /** read CML element.
     * convenience method
     * @param filename
     * @return element
     */
    public static CMLElement readElementFromResource(String filename) {
        CMLElement element = null;
        try {
            InputStream in = Util
            .getInputStreamFromResource(filename);
            element = (CMLElement) new CMLBuilder().build(in).getRootElement();
            in.close();
        } catch (Exception e) {
            throw new CMLRuntimeException("parse/read exception in "+filename+"; "+e);
        }
        return element;
    }

    /** bug report.
     * @param message
     */
    public static void BUG(String message) {
        Util.BUG(message);
    }
    
    /** returns all prefixes in attributes in descendants.
     * currently accesses all elements
     * @param attName attribute name (e.g. ref, dictRef)
     * @return prefixes
     */
    public static List<String> getPrefixes(Element element, String attName) {
    	List<String> prefixList = new ArrayList<String>();
    	List<Node> refs = CMLUtil.getQueryNodes(element, ".//@"+attName, X_CML);
    	for (Node node : refs) {
    		Attribute attribute = (Attribute) node;
    		String value = attribute.getValue();
    		String prefix = CMLUtil.getPrefix(value);
    		if (!prefixList.contains(prefix)) {
    			prefixList.add(prefix);
    		}
    	}
    	return prefixList;
    }

    /** get namespace for list of prefixes.
     * 
     * @param element in which namespaces are in scope
     * @param prefixes
     * @return list of namespaces
     * @exception CMLRuntimeException if any prefix does not map to a namespace
     */
	public static List<CMLNamespace> getNamespaces(
			Element element, List<String> prefixes) {
		List<CMLNamespace> namespaceList = new ArrayList<CMLNamespace>();
		for (String prefix : prefixes) {
			String namespaceURI = element.getNamespaceURI(prefix);
			if (namespaceURI == null) {
				throw new CMLRuntimeException("Missing namespace :"+prefix+":");
			}
			CMLNamespace namespace = new CMLNamespace(prefix, namespaceURI);
			namespaceList.add(namespace);
		}
		return namespaceList;
	}
    
	public static List<List<Integer>> generateCombinationList(int listSize) {
		List<List<Integer>> combinationList = new ArrayList<List<Integer>>();
		int count = (int) Math.pow(2.0, listSize);
		for (int i = 2; i <= count; i++) {
			int thisCount = i;
			List<Integer> intSet = new ArrayList<Integer>(listSize);
			for (int j = listSize; j >= 0; j--) {
				int minus = (int)Math.pow(2.0, j);
				int test = thisCount;
				if (test - minus > 0) {
					thisCount -= minus;
					intSet.add(j);
				}
			}
			combinationList.add(intSet);
		}
		// add entry with no values
		combinationList.add(new ArrayList<Integer>(0));

		return combinationList;
	}
}
