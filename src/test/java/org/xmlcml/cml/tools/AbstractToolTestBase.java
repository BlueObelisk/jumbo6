package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.U_S;

/** superclass to manage resources etc.
 * 
 * @author pm286
 *
 */
public final class AbstractToolTestBase {
    /**
     * resource
     */
    public final static String TOOLS_RESOURCE = "org"+U_S+"xmlcml"+U_S+"cml"+U_S+"tools";
    /**
     * examples
     */
    public final static String TOOLS_EXAMPLES = TOOLS_RESOURCE+U_S+"examples";

    /**
     * crystal examples
     */
    public final static String CRYSTAL_EXAMPLES = TOOLS_EXAMPLES+U_S+"cryst";
}