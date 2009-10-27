/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.util;

/**
 *
 * @author pm286
 */
public class JumboException extends RuntimeException {
    public JumboException() {
        super();
    }
    public JumboException(Exception e) {
        super(e);
    }
    public JumboException(String message, Exception e) {
        super(message, e);
    }
    public JumboException(String message) {
        super(message);
    }
}
