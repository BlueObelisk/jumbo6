/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.util;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.euclid.EuclidConstants;

/**
 *
 * @author pm286
 */
public abstract class CMLUtilNew {
    private static Logger LOG = Logger.getLogger(CMLUtilNew.class);

    public final static double DOUBLE_MAX_VALUE = Double.MAX_VALUE;
    public final static double DOUBLE_MIN_VALUE = Double.MIN_VALUE;
    public final static int INTEGER_MAX_VALUE = Integer.MAX_VALUE;
    public final static int INTEGER_MIN_VALUE = Integer.MIN_VALUE;

    // ========================== utilities ====================== //

    public static void debug(String s) {
        System.out.println(s);
    }
    /**
     * checks that name is QName.
     *
     * @param name
     *            of XMLName
     * @throws CMLException
     *             not colonized
     */
    public static void checkPrefixedName(String name) {
        if (name == null || name.indexOf(EuclidConstants.S_COLON) < 1) {
            throw new JumboException("Unprefixed name (" + name + EuclidConstants.S_RBRAK);
        }
    }

    /**
     * get prefix from qualified name.
     *
     * @param s
     * @return prefix (or empty String)
     */
    public static String getPrefix(String s) {
        int idx = s.indexOf(EuclidConstants.S_COLON);
        return (idx == -1) ? EuclidConstants.S_EMPTY : s.substring(0, idx);
    }

    /**
     * get localName from qualified name.
     *
     * @param s
     * @return localName (or empty String)
     */
    public static String getLocalName(String s) {
        String ss = null;
        if (s != null) {
            int idx = s.indexOf(EuclidConstants.S_COLON);
            ss = (idx == -1) ? s : s.substring(idx + 1);
        }
        return ss;
    }

    /**
     * convenience method to extract value of exactly one node.
     * uses element.query(xpath, string);
     * @param element
     * @param xpath
     * @param string defines prefix/namespace used in query
     * @return value if exactly 1 node (0 or many returns null)
     */
    public static String getSingleStringValue(Element element, String xpath, String namespaceUri)
    {
        String s = null;
        if (element == null)
        {
            LOG.warn("Null element");
        }
        else
        {
            Element newElement = CMLUtilNew.getSingleNamespacedElement(element, namespaceUri, xpath);
            s = (newElement != null) ? getXMLContent(element) : null;
        }
        return s;
    }

    /*
    private static String GetXMLContent(XElement elementX)
    {
        String s = null;
        XNode node = elementX.FirstNode;
        if (node == null)
        {
        }
        else if (node instanceof XText)
        {
            s = ((XText)node).Value;
        }
        return s;
    }
     */

    public static String getSingleStringValue(Element element, String xpath)
    {
        return getSingleStringValue(element, xpath, null);
    }

    public static List<String> getAttributeValues(Element element, String xpath, String namespaceUri)
    {
        if (element == null)
        {
            throw new RuntimeException("Null element");
        }
        if (xpath.indexOf("@") == -1) {
            throw new RuntimeException("cannot get attribute values, no @... "+xpath);
        }
        return queryNodeValues(element, xpath, namespaceUri);
    }

    public static List<String> getAttributeValues(Element element, String xpath)
    {
        return getAttributeValues(element, xpath, null);
    }

    public static String getSingleAttributeValue(Element element, String xpath, String namespaceUri)
    {
        String s = null;
        List<String> ss = getAttributeValues(element, xpath, namespaceUri);
        if (ss.size() > 1)
        {
            s = ss.get(0);
        }
        return s;
    }

    public static List<String> queryNodeValues(Element element, String xpath, String namespaceUri) {
        xpath = addNamespaceUriToXPath(xpath, namespaceUri);
        Nodes nodes = element.query(xpath);
        List<String> stringList = new ArrayList<String>(10);
        for (int i = 0; i < nodes.size(); i++) {
            stringList.add(nodes.get(i).getValue());
        }
        return stringList;
    }

    public static List<String> queryNodeValues(Element element, String xpath) {
        return queryNodeValues(element, xpath, null);
    }

    public static String addNamespaceUriToXPath(String xpath, String namespaceUri) {
        xpath = xpath.replace("]", " and namespace-uri()='" + namespaceUri + "']");
        return xpath;
    }

    public static String getSingleAttributeValue(Element element, String xpath)
    {
        return getSingleAttributeValue(element, xpath, null);
    }

    /**
         * convenience method to extract value of the first of one-or-more nodes.
         * uses element.query(xpath, string);
         * @param element
         * @param xpath
         * @param string defines prefix/namespace used in query
         * @return value if exactly 1 node (0 or many returns null)
         */
    public static String getFirstValue(Element element, String xpath, String namespaceUri) {
        String  s = null;
        if (element == null) {
            LOG.warn("Null element");
        } else {
            List<Element> nodes = queryNamespacedElements(element, xpath, namespaceUri);
               s = (nodes.size() >= 1) ? nodes.get(0).getValue() : null;
        }
        return s;
    }

    public static List<Element> queryNamespacedElements(Element element, String xpath, String namespaceUri) {
        xpath = addNamespaceUriToXPath(xpath, namespaceUri);
        Nodes nodes = element.query(xpath);
        List<Element> elementList = new ArrayList<Element>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element) {
                elementList.add((Element) node);
            }
        }
        return elementList;
    }
    public static String getFirstValue(Element element, String xpath)
    {
        return getFirstValue(element, xpath, null);
    }

    public static Element getSingleNamespacedElement(Element element, String namespaceUri, String localName)
    {
        return CMLUtilNew.getSingleElement(element, "*[" + namespaceUri + " and local-name()='" + localName + "']");
    }

    public static List<Element> getNamespacedElements(Element element, String namespaceUri, String localName)
    {
        return CMLUtilNew.getElements(element, "*[" + namespaceUri + " and local-name()='" + localName + "']");
    }

    public static String getSingleNamespacedChildElementAttributeValue(Element element, String namespaceUri, String childElementName, String attributeName)
    {
        return CMLUtilNew.getSingleStringValue(element,
                "*[" + namespaceUri + " and local-name()='" + childElementName + "']/@" + attributeName);
    }

    /**
     * convenience method to get exactly one element.
     * uses element.query(xpath, String);
     * @param element
     * @param xpath
     * @param string defines prefix/namespace used in query
     * @return value if exactly 1 element (0 or many returns null)
     */
    public static Element getSingleElement(Element element, String xpath, String namespaceUri) {
        List<Element> nodes = queryNamespacedElements(element, xpath, namespaceUri);
        return (nodes.size() == 1) ? (Element) nodes.get(0) : null;
    }

    public static Element getSingleElement(Element element, String xpath)
    {
        return getSingleElement(element, xpath, null);
    }

    public static List<Element> getElements(Element element, String xpath)
    {
        return queryNamespacedElements(element, xpath, null);
    }


    /**
         * convenience routine to get query CMLelements (iterating thorugh get(i) is
         * fragile if nodes are removed)
         * if query result is not a CMLElement it is omitted form list, so be careful
         *
         * @param element
         * @param xpath xpath relative to node
         * @param context
         * @return list of CMLelements - empty if none
         */
    public static List<CMLElement> getCMLElements(Element element, String xpath,
            String context) {
        List<CMLElement> nodeList = new ArrayList<CMLElement>();
        if (element != null) {
            List<Element> elements = queryNamespacedElements(element, xpath, context);
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i) instanceof CMLElement) {
                    nodeList.add((CMLElement)elements.get(i));
                }
            }
        }
        return nodeList;
    }

    public static List<CMLElement> getCMLElements(Element node, String xpath)
    {
        return getCMLElements(node, xpath, null);
    }
    /**
     * converts an Elements to a java array. we might convert code to use
     * Elements through later so this would be unneeded
     *
     * @param elements
     * @param obj
     *            type of array (e.g. "new CMLAtom[0]"
     * @return the java array 0f objects
     *
    public static object[] toArray(Elements elements, object[] obj) {
        List<Element> list = new List<Element>();
        for (int i = 0; i < elements.size(); i++) {
            list.Add(elements.get(i));
        }
        return list.ToArray();
    }
    */
    /**
     * convenience routine to get child nodes (iterating through getChild(i) is
     * fragile if children are removed)
     *
     * @param el
     *            may be null
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

    /**
     * parses XML string into element. convenience method to avoid trapping
     * exceptions when string is known to be valid
     *
     * @param xmlString
     * @return root element
     * @throws RuntimeException
     */
    public static Element parseXML(String xmlString) {
        Element root = null;
        /*
        try {
            Document doc = new Builder().build(new StringReader(xmlString));
            root = doc.getRootElement();
        } catch (Exception e) {
            throw new Exception(e);
        }
         */
        return root;
    }

    /**
     * parses CML string into element. convenience method to avoid trapping
     * exceptions when string is known to be valid
     *
     * @param cmlString
     * @return root element
     * @throws RuntimeException
     */
    public static CMLElement parseCML(String cmlString) {
        CMLElement root = null;
        /*
        try {
            Document doc = new CMLBuilder().build(new StringReader(cmlString));
            root = (CMLElement) doc.getRootElement();
        } catch (Exception e) {
            throw new Exception(e);
        }
         */
        return root;
    }

    /**
     * convenience routine to get query nodes (iterating thorugh get(i) is
     * fragile if nodes are removed)
     *
     * @param node
     *            (can be null)
     * @param xpath
     *            xpath relative to node
     * @param context
     * @return list of nodes (immutable) - empty if none
     */
    /*
    public static List<Node> getQueryNodes(Node node, String xpath,
            string context) {
        List<Node> nodeList = new List<Node>();
        if (node != null) {
            // TODO
//            Nodes nodes = node.query(xpath, context);
//            for (int i = 0; i < nodes.size(); i++) {
//                nodeList.add(nodes.get(i));
//            }
        }
        return nodeList;
    }
     */

    /**
     * convenience routine to get query nodes (iterating through get(i) is
     * fragile if nodes are removed)
     *
     * @param node
     * @param xpath
     * @return list of nodes (immutable) - empty if none or null node
     *
    public static List<Node> getQueryNodes(Node node, String xpath) {
        List<Node> nodeList = new List<Node>();
        if (node != null) {
            Nodes nodes = node.query(xpath);
            for (int i = 0; i < nodes.size(); i++) {
                nodeList.add(nodes.get(i));
            }
        }
        return nodeList;
    }
     */

    /**
     * get next sibling.
     *
     * @author Eliotte Rusty Harold
     * @param current
     *            may be null
     * @return following sibling or null
     */
    public static Node getFollowingSibling(Node current) {
        Node node = null;
        /*
        if (current != null) {
            ParentNode parent = current.getParent();
            if (parent != null) {
                int index = parent.indexOf(current);
                if (index + 1 < parent.getChildCount()) {
                    node = parent.getChild(index + 1);
                }
            }
        }
         */
        return node;
    }

    /**
     * get previous sibling.
     *
     * @param current
     * @return previous sibling
     *
    public static Node getPrecedingSibling(Node current) {
        Node node = null;
        if (current != null) {
            ParentNode parent = current.getParent();
            if (parent != null) {
                int index = parent.indexOf(current);
                if (index > 0) {
                    node = parent.getChild(index - 1);
                }
            }
        }
        return node;
    }
     */

    /**
     * gets last text descendant of element. this might be referenced from the
     * following-sibling and will therefore be the immediately preceding chunk
     * of text in document order if the node is a text node returns itself
     *
     * @param node
     * @return Text node or null
     *
    public static Text getLastTextDescendant(Node node) {
        List<Node> l = CMLUtil.getQueryNodes(node, ".//text() | self::text()");
        return (l.size() == 0) ? null : (Text) l.get(l.size() - 1);
    }
     */

    /**
     * gets first text descendant of element. this might be referenced from the
     * preceding-sibling and will therefore be the immediately following chunk
     * of text in document order if the node is a text node returns itself
     *
     * @param node
     * @return Text node or null
     */
    public static Text getFirstTextDescendant(Node node) {
// TODO        List<Node> l = CMLUtil.getQueryNodes(node, ".//text() | self::text()");
//        return (l.size() == 0) ? null : (Text) l.get(0);
        return null;
    }

    /**
     * transfers children of 'from' to 'to'.
     *
     * @param from
     *            (will be left with no children)
     * @param to
     *            (will gain 'from' children appended after any existing
     *            children
     */
    public static void transferChildren(Element from, Element to) {
        int nc = from.getChildCount();
        int tc = to.getChildCount();
        for (int i = nc - 1; i >= 0; i--) {
            Node child = from.getChild(i);
// TODO            child.detach();
            to.insertChild(child, tc);
        }
    }

    /**
     * transfers children of element to its parent. element is left in place and
     * children come immediately before normally element will be deleted
     *
     * @param element
     *            (will be left with no children)
     */
    public static void transferChildrenToParent(Element element) {
        int nc = element.getChildCount();
        Element parent = (Element) element.getParent();
        int ii = parent.indexOf(element);
        for (int i = nc - 1; i >= 0; i--) {
            Node child = element.getChild(i);
// TODO            child.detach();
            parent.insertChild(child, ii);
        }
    }

    /**
     * get XOM default canonical string.
     *
     * @param node
     * @return the string
     *
    public static String getCanonicalString(Node node) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Canonicalizer canon = new Canonicalizer(baos);
        try {
            canon.write(node);
        } catch (IOException e) {
            throw new Exception("should never throw " + e);
        }
        return baos.toString();
    }
     */

    /**
     * remeoves all whitespace-only text nodes.
     *
     * @param element
     *            to strip whitespace from
     */
    public static void removeWhitespaceNodes(Element element) {
        int nChild = element.getChildCount();
        List<Node> nodeList = new ArrayList<Node>();
        for (int i = 0; i < nChild; i++) {
            Node node = element.getChild(i);
            if (node instanceof Text) {
// TODO                if (node.getValue().trim().length() == 0) {
//                    nodeList.add(node);
//                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                removeWhitespaceNodes(childElement);
            } else {
            }
        }
        for (Node node : nodeList) {
// TODO            node.detach();
        }
    }

    /**
     * sets text content of element. Does not support mixed content.
     *
     * @param element
     * @param s
     * @throws RuntimeException
     *             if element already has element content
     */

    public static void setXMLContent(Element element, String s) {
        /* TODO
        List<Node> elements = CMLUtil.getQueryNodes(element, EuclidConstants.S_STAR);
        if (elements.size() > 0) {
            throw new Exception(
                    "Cannot set text with element children");
        }
        Text text = CMLUtil.getFirstTextDescendant(element);
        if (text == null) {
            text = new Text(s);
            element.appendChild(text);
        } else {
            text.setValue(s);
        }
         */
    }

    /**
     * sets text content of element. Does not support mixed content.
     *
     * @param element
     * @return text value
     * @throws RuntimeException
     *             if element already has element content
     */

    public static String getXMLContent(Element element) {
        /* TODO
        List<Node> elements = CMLUtil.getQueryNodes(element, EuclidConstants.S_STAR);
        if (elements.size() > 0) {
            throw new Exception(
                    "Cannot get text with element children");
        }
        return element.getValue();
         */
        return null;
    }

    /*
    public static String toXMLString(Element element) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            CMLUtil.debug(element, baos, 0);
        } catch (IOException e) {
        }
        return new String(baos.toByteArray());
    }
     */

    /**
     * returns all prefixes in attributes in descendants. currently accesses all
     * elements
     *
     * @param element
     * @param attName
     *            attribute name (e.g. ref, dictRef)
     * @return prefixes
     *
    public static List<String> getPrefixes(Element element, String attName) {
        List<String> prefixList = new List<String>();
        List<Node> refs = CMLUtil.getQueryNodes(element, ".//@" + attName, CMLConstants.CML_XPATH);
        foreach (Node node in refs) {
            Attribute attribute = (Attribute) node;
            String value = attribute.getValue();
            String prefix = CMLUtil.getPrefix(value);
            if (!prefixList.contains(prefix)) {
                prefixList.add(prefix);
            }
        }
        return prefixList;
    }
     */


    /**
     * make id from string. convert to lowercase and replace space by underscore
     *
     * @param s
     * @return new id (null if s is null)
     */
    public static String makeId(String s) {
        String id = null;
        if (s != null) {
            id = s.toLowerCase();
            id = id.replace(EuclidConstants.S_SPACE, EuclidConstants.S_UNDER);
        }
        return id;
    }

    /**
     * create local CML class name. e.g. CMLFooBar from fooBar
     *
     * @param name
     * @return name
     */
    public static String makeCMLName(String name) {
        return "CML" + capitalize(name);
    }

    /**
     * create local Abstract class name. e.g. AbstractFooBar from fooBar
     *
     * @param name
     * @return name
     */
    public static String makeAbstractName(String name) {
        return "Abstract" + capitalize(name);
    }

    /**
     * capitalize name e.g. FooBar from fooBar
     *
     * @param name
     * @return name
     */
    public static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Parses double, taking account of lexical forms of special cases allowed
     * by the XSD spec: INF, -INF and NaN.
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static double parseFlexibleDouble(String value) {
        //LOG.debug("Parsing "+ value);
        if (value != null) {
            // 0, -0, INF, -INF and NaN : Special cases from the XSD spec.
            if ("INF".equals(value)) {
                return JumboDouble.POSITIVE_INFINITY;
            } else if ("-INF".equals(value)) {
                return JumboDouble.NEGATIVE_INFINITY;
            } else if ("NaN".equals(value)) {
                return Double.NaN;
            } else {
                return JumboDouble.valueOf(value);
            }
        } else {
            throw new JumboException("Null double string not allowed");
        }
    }

    /**
     * tests 2 XML objects for equality using recursive descent.
     * includes namespace testing
     *
     * @param refString xml serialization of first Element
     * @param testNode second Element
     * @param stripWhite if true remove w/s nodes
     * @return message of where elements differ (null if identical)
     */
    public static String equalsCanonically(String refNodeXML, Element testElement,
            boolean stripWhite) {
        String message = null;
        /*
        Element refElement = null;
        try {
            refElement = new Builder().build(new StringReader(refNodeXML)).getRootElement();
        } catch (Exception e) {
            throw new Exception("Parsing failed: "+refNodeXML);
        }
        message = equalsCanonically(refElement, testElement, stripWhite, "/");
        LOG.trace("EQCAN "+message);
         */
        return message;
    }

    /**
     * tests 2 XML objects for equality using recursive descent.
     * includes namespace testing
     *
     * @param refNode first node
     * @param testNode second node
     * @param stripWhite if true remove w/s nodes
     * @return message of where elements differ (null if identical)
     */
    public static String equalsCanonically(Element refElement, Element testElement,
            boolean stripWhite) {
        return equalsCanonically(refElement, testElement, stripWhite, "./");
    }
    /**
     * tests 2 XML objects for equality using recursive descent.
     * includes namespace testing
     *
     * @param refElement first node
     * @param testElement second node
     * @param stripWhite if true remove w/s nodes
     * @return message of where elements differ (null if identical)
     */
    public static String equalsCanonically(Element refElement, Element testElement,
            boolean stripWhite, String xpath) {
        String message = null;
        // check if they are different objects
        if (refElement != testElement) {
            if (stripWhite) {
                refElement = new Element(refElement);
                removeWhitespaceNodes(refElement);
                testElement = new Element(testElement);
                removeWhitespaceNodes(testElement);
            }
            xpath = xpath+"*[local-name()='"+refElement.getLocalName()+"']/";
            message = equalsCanonically(refElement, testElement, xpath);
        }
        return message;
    }

    private static String equalsCanonically(Element refElement, Element testElement, String xpath) {
        String message;
        message = CMLUtilNew.compareNamespacesCanonically(refElement, testElement, xpath);
        if (message != null) {
            return message;
        }
        String refName = refElement.getLocalName();
        String testName = testElement.getLocalName();
        if (message == null && !refName.equals(testName)) {
            message = "element names differ at "+xpath+": "+refName+" != "+testName;
        }
        String refNamespace = refElement.getNamespaceURI();
        String testNamespace = testElement.getNamespaceURI();
        if (message == null && !refNamespace.equals(testNamespace)) {
            message = "element namespaces differ at "+xpath+": "+refNamespace+" != "+testNamespace;
        }
        if (message == null) {
            message = CMLUtilNew.compareAttributesCanonically(refElement, testElement, xpath);
        }
        if (message == null) {
            message = CMLUtilNew.compareChildNodesCanonically(refElement, testElement, xpath);
        }
        return message;
    }

    public static String getCommonLeadingString(String s1, String s2) {
        int l = Math.min(s1.length(), s2.length());
        int i;
        for (i = 0; i < l; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        return s1.substring(0, i);
    }

    /** compare namespaces on two elements
     *
     * @param refNode
     * @param testNode
     * @param xpath current ancestry of refNode
     * @return
     */
    public static String compareNamespacesCanonically(Element refNode, Element testNode, String xpath) {
        String message = null;
        List<String> refNamespaceURIList = getNamespaceURIList(refNode);
        List<String> testNamespaceURIList = getNamespaceURIList(testNode);
        if (refNamespaceURIList.size() != testNamespaceURIList.size()) {
                message = "unequal namespace count;" +
                " ref "+refNamespaceURIList.size()+";" +
                " testCount "+testNamespaceURIList.size();
        } else {
            for (String refNamespaceURI : refNamespaceURIList) {
                if (!testNamespaceURIList.contains(refNamespaceURI)) {
                    message = "Cannot find "+refNamespaceURI+
                    " in test namespaces ";
                    break;
                }
            }
        }
        return message;
    }

    /**
     * @param node
     * @param count
     */
    private static List<String> getNamespaceURIList(Element node) {
        List<String> namespaceURIList = new ArrayList<String>();
        for (int i = 0; i < node.getNamespaceDeclarationCount(); i++) {
            String prefix = node.getNamespacePrefix(i);
            String refNamespaceURI = node.getNamespaceURI(prefix);
            namespaceURIList.add(refNamespaceURI);
        }
        return namespaceURIList;
    }

    /** compare attributes on two elements.
     * includes normalizing attribute values
     *
     * @param refNode
     * @param testNode
     * @param xpath current ancestry of refNode
     * @return
     */
    public static String compareAttributesCanonically(Element refNode, Element testNode, String xpath) {
        String message = null;
        int refCount = refNode.getAttributeCount();
        int testCount = testNode.getAttributeCount();
        if (refCount != testCount) {
            message = "unequal attribute count at "+xpath+" ("+refCount+" != "+testCount+")";
        }
        if (message == null) {
            for (int i = 0; i < refCount; i++) {
                Attribute attribute = refNode.getAttribute(i);
                String name = attribute.getLocalName();
                String namespacex = attribute.getNamespaceURI();
                String value = attribute.getValue();
                Attribute testAttribute = (namespacex == null) ?
                    testNode.getAttribute(name) :
                    testNode.getAttribute(name, namespacex);
                if (testAttribute == null) {
                    message = "no attribute in test ("+xpath+") for "+CMLUtilNew.printName(name, namespacex);
                    break;
                }
                String refValue = CMLUtilNew.normalizeSpace(value);
                String testValue = CMLUtilNew.normalizeSpace(testAttribute.getValue());
                if (!refValue.equals(testValue)) {
                    message = "normalized attribute values for ("+xpath+"@"+CMLUtilNew.printName(name, namespacex)+") "+refValue+" != "+testValue;
                    break;
                }
            }
        }
        LOG.trace("ATT MS "+message);
        return message;
    }

    private static String printName(String name, String namespacex) {
        return name+((namespacex == null || namespacex.equals(EuclidConstants.S_EMPTY)) ? "" : "["+namespacex+"]");
    }

    private static String normalizeSpace(String value) {
        value.replaceAll(EuclidConstants.S_WHITEREGEX, EuclidConstants.S_SPACE);
        return value.trim();
    }

    /** compare child nodes recursively
     *
     * @param refNode
     * @param testNode
     * @param xpath current ancestry of refNode
     * @return
     */
    public static String compareChildNodesCanonically(Element refNode, Element testNode, String xpath) {
        String message = null;
        int refCount = refNode.getChildCount();
        int testCount = testNode.getChildCount();
        if (refCount != testCount) {
            message = "unequal child node count at "+xpath+" ("+refCount+" != "+testCount+")";
        }
        if (message == null) {
            for (int i = 0; i < refCount; i++) {
                String xpathChild = xpath+"node()[position()="+(i+1)+"]";
                Node refChildNode = refNode.getChild(i);
                Node testChildNode = testNode.getChild(i);
                Class<?> refClass = refChildNode.getClass();
                Class<?> testClass = testChildNode.getClass();
                if (!refClass.equals(testClass)) {
                    message = "child node classes differ at "+xpathChild+" "+refClass+"/"+testClass;
                    break;
                } else if (refChildNode instanceof Element) {
                    message = CMLUtilNew.equalsCanonically((Element) refChildNode, (Element) testChildNode,
                        xpathChild);
                } else {
                    message = CMLUtilNew.compareNonElementNodesCanonically(refNode, testNode, xpath);
                    if (message != null) {
                        break;
                    }
                }
            }
        }
        return message;
    }


    /** compare non-element nodes.
     * not yet tuned for normalizing adjacent CDATA and other horrors
     * @param refNode
     * @param testNode
     * @param xpath current ancestry of refNode
     * @return
     */
    public static String compareNonElementNodesCanonically(Node refNode, Node testNode, String xpath) {
        String message = null;
        String refValue = refNode.getValue();
        String testValue = testNode.getValue();
        if (refNode instanceof Comment) {
            if (!refValue.equals(testValue)) {
                message = "comments at ("+xpath+") differ: "+refValue+" != "+testValue;
            }
        } else if (refNode instanceof Text) {
            if (!refValue.equals(testValue)) {
                message = "text contents at ("+xpath+") differ: ["+refValue+"] != ["+testValue+"]";
            }
        } else if (refNode instanceof ProcessingInstruction) {
            String refTarget = ((ProcessingInstruction) refNode).getTarget();
            String testTarget = ((ProcessingInstruction) testNode).getTarget();
            if (!refTarget.equals(testTarget)) {
                message = "PI targets at ("+xpath+") differ: "+refTarget+" != "+testTarget;
            }
        } else {
            LOG.warn("Unknown XML element in comparison");
        }
        return message;
    }
}
