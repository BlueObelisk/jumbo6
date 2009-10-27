/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.util;

/**
 *
 * @author pm286
 */

    public class JumboInteger
    {
        public static int MAX_VALUE = Integer.MAX_VALUE;
        public static int MaxValue = Integer.MAX_VALUE;
        public static int MIN_VALUE = Integer.MIN_VALUE;
        public static int MinValue = Integer.MIN_VALUE;
        int i;
        public JumboInteger(String s)
        {
            i = new Integer(s).intValue();
        }
        public JumboInteger(int i)
        {
            this.i = i;
        }
        public static String toHexString(int ii)
        {
            return Integer.toHexString(ii);
        }
        public static int parseInt(String s)
        {
            return Integer.parseInt(s);
        }
        public boolean equals(int i)
        {
            return this.i == i;
        }
        public int intValue()
        {
            return i;
        }
        public static String toString(int i)
        {
            return "" + i;
        }
        public static int ValueOf(String s)
        {
            return new Integer(s).intValue();
        }


        public int compareTo(JumboInteger i2)
        {
            return (this.i - i2.i);
        }

        public static int valueOf(String p)
        {
            return new Integer(p).intValue();
        }
    }
