package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.AbstractTest;

/** superclass to manage resources etc.
 * 
 * @author pm286
 *
 */
public abstract class AbstractToolTest extends AbstractTest {
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