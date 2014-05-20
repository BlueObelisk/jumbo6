/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
