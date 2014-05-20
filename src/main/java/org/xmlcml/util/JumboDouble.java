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
public class JumboDouble
{
    public final static double NaN = Double.NaN;
    public final static double MAX_VALUE = Double.MAX_VALUE;
    public final static double MaxValue = Double.MAX_VALUE;
    public final static double MIN_VALUE = Double.MAX_VALUE;
    public final static double MinValue = Double.MAX_VALUE;
    public final static double PositiveInfinity = Double.POSITIVE_INFINITY;
    public final static double NegativeInfinity = Double.NEGATIVE_INFINITY;
    public final static double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public final static double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    double d;
    public JumboDouble(String s)
    {
        d = new Double(d).doubleValue();
    }

    public JumboDouble(double d)
    {
        this.d = d;
    }

    public double doubleValue()
    {
        return d;
    }

    public static double longBitsToDouble(long accum)
    {
        throw new RuntimeException("NYI");
    }

    public static long doubleToLongBits(double d)
    {
        throw new RuntimeException("NYI");
    }

    public static double Parse(String p)
    {
        return new Double(p).doubleValue();
    }
    public static boolean isNaN(double d)
    {
        return Double.isNaN(d);
    }
    public static boolean isInfinity(double d)
    {
        return Double.isInfinite(d);
    }

    public static boolean isInfinite(double p)
    {
        return Double.isInfinite(p);
    }

    public String toString()
    {
        return this.toString();
    }

    public static String toString(double value)
    {
        return new JumboDouble(value).toString();
    }
    public static double valueOf(String value)
    {
        return new JumboDouble(value).doubleValue();
    }
}
