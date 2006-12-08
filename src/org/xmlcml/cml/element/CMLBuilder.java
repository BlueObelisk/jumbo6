package org.xmlcml.cml.element;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLConstants;

/**
 * 
 * <p>
 * Class extending the XOM builder, constructs a XOM builder using either the
 * CML node factory as default or a passed node factory.
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CMLBuilder extends Builder implements CMLConstants {

    /**
     * Constructs a XOM builder using the (subclassed) CML node factory
     */
    public CMLBuilder() {
        this(new CMLNodeFactory());
    }

    /**
     * Constructs a XOM builder using the (subclassed) CML node factory
     * 
     * @param validate
     *            if true
     */
    public CMLBuilder(boolean validate) {
        this(validate, new CMLNodeFactory());
    }

    /**
     * Constructs a XOM builder using the passed node factory
     * 
     * @param nodeFactory
     *            to construct builder with
     */
    public CMLBuilder(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    /**
     * Constructs a XOM builder using the passed node factory
     * 
     * @param validate
     *            if true
     * @param nodeFactory
     *            to construct builder with
     */
    public CMLBuilder(boolean validate, NodeFactory nodeFactory) {
        super(validate, nodeFactory);
    }

    /**
     * convenience method to parse XML string.
     * 
     * @param xmlString to parse
     * @return the root element or null
     * @throws ValidityException
     * @throws ParsingException
     * @throws IOException
     */

    public Element parseString(String xmlString) throws ValidityException,
            ParsingException, IOException {
        Document doc = this.build(new StringReader(xmlString));
        return (doc == null) ? null : doc.getRootElement();
    }

}
