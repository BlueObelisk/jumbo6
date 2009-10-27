package org.xmlcml.util;

/**
 *
 * @author pm286
 */
public class JumboLong
{
    long l;
    public JumboLong(long l)
    {
        this.l = l;
    }
    public JumboLong(String s)
    {
        l = new Long(s).longValue();
    }

    public long longValue()
    {
        return l;
    }

    public static long parseLong(String p)
    {
        return new Long(p).longValue();
    }
}
