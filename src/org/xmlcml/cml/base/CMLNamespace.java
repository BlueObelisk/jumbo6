package org.xmlcml.cml.base;

/**
 * 
 * <p>
 * manages CML namespaces.
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public abstract class CMLNamespace implements CMLConstants {

    /**
     * default is CML.
     * 
     */
    protected static String currentNamespaceURI = CML;

    protected CMLNamespace() {
        super();
    }

    /**
     * is a namespace URI compatible with CML namespaces.
     * 
     * @param URI
     *            to compare
     * @return true if compatible
     */
    public static boolean isCMLNamespace(String URI) {
        return CML.equals(URI) || CML2.equals(URI) || CML3.equals(URI);
    }

    /**
     * get current namespaceURI.
     * 
     * @return the URI (default is CML)
     */
    public static String getCurrentNamespaceURI() {
        return currentNamespaceURI;
    }

    /**
     * get current namespaceURI.
     * 
     * @param n
     *            the URI
     */
    public static void setCurrentNamespaceURI(String n) {
        currentNamespaceURI = n;
    }

    /**
     * guess CML namespace. if correct namespace, returns it. if obsolete
     * returns correct namespace.
     * 
     * @param namespace
     * @return correct namespace or leave unchanged;
     */
    public static String guessNamespace(String namespace) {
        String namesp = namespace;
        if (CML_NS.equals(namespace)) {
            namesp = namespace;
        } else if (namespace != null) {
            for (String n : CMLConstants.OLD_NAMESPACES) {
                if (n.equals(namespace)) {
                    namesp = CML_NS;
                    break;
                }
            }
        }
        return namesp;
    }
}
