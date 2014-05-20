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
