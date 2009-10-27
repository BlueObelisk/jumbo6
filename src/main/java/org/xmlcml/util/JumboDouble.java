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
