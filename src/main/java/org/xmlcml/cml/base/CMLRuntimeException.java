package org.xmlcml.cml.base;

import nu.xom.ParsingException;

/**
 * 
 * <p>
 * runtime exception for CML
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CMLRuntimeException extends RuntimeException implements CMLConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 3191502795045376413L;

    protected CMLRuntimeException() {
        super();
    }

    /**
     * creates CMLRuntime with message.
     * 
     * @param msg
     */
    public CMLRuntimeException(String msg) {
        super(msg);
    }

    /**
     * creates CMLRuntime from CMLException.
     * 
     * @param exception
     */
    public CMLRuntimeException(CMLException exception) {
        this(S_EMPTY + exception);
    }

    /**
     * creates CMLRuntime from Exception.
     * 
     * @param exception
     */
    public CMLRuntimeException(Exception exception) {
        this(S_EMPTY + exception);
    }

    /**
     * parsing exception.
     * @param msg
     *            additional message
     * @param e
     *            exception
     */
    public CMLRuntimeException(String msg, ParsingException e) {
        this("PARSE_ERROR [at " + e.getLineNumber() + S_COLON + e.getColumnNumber()
                + "] " + e.getMessage() + " | " + msg, (Exception) e);
    }

    /** constructor from exception.
     * 
     * @param message
     * @param e
     */
	public CMLRuntimeException(String message, Exception e) {
		super(message, e);
	}

}
