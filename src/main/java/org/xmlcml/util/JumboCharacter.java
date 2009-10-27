/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.util;

/**
 *
 * @author pm286
 */
public class JumboCharacter
{
    char c;
    public JumboCharacter(char c)
    {
        this.c = c;
    }
    public JumboCharacter(String s)
    {
        c = s.charAt(0);
    }
    public char charValue()
    {
        return c;
    }
    public static boolean isDigit(char c)
    {
        return Character.isDigit(c);
    }

    public static boolean isWhitespace(char ch)
    {
        throw new RuntimeException("NYI");
    }

    public static boolean isISOControl(char ch)
    {
        throw new RuntimeException("NYI");
    }
}
