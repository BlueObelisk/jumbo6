package org.xmlcml.cml.base;

import nu.xom.ParsingException;

/**
 * 
 * <p>
 * schema-derived atom class
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CMLException extends Exception implements CMLConstants {

    /**
     * 
     */
    private static final long serialVersionUID = -5929440785347378130L;

    /** constructor.
     * @param message
     * @param cause
     */
    public CMLException(String message, Throwable cause) {
        super(message, cause);
    }

    /** constructor
     * 
     * @param cause
     */
    public CMLException(Throwable cause) {
        super(cause);
    }

    /**
     * constructor.
     */
    public CMLException() {
        super();
    }

    /**
     * creates CMLException with message.
     * 
     * @param msg
     */
    public CMLException(String msg) {
        super(msg);
    }

    /**
     * constructor from ParsingException.
     * 
     * @param e
     * @param msg
     */
    public CMLException(ParsingException e, String msg) {
        this("PARSE_ERROR [at " + e.getLineNumber() + S_COLON + e.getColumnNumber()
                + "] " + e.getMessage() + " | " + msg);
    }
}
