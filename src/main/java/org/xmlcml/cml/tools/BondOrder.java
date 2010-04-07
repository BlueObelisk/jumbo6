/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLBond;

/**
 *
 * @author pm286
 */
public class BondOrder
{
    public enum ORDER
    {
        S,
        D,
        T,
        A,
        unknown,
        hbond,
        partial01,
        partial12,
        partial23,
    }

    private String cmlBondOrder;
    private Double numericBondOrder;
    private ORDER _order;

    public static BondOrder UNKNOWN = new BondOrder(ORDER.unknown.toString(), 0.0);
    public static BondOrder HBOND = new BondOrder(ORDER.hbond.toString(), 0.0);
    public static BondOrder PARTIAL01 = new BondOrder(ORDER.partial01.toString(), 0.5);
    public static BondOrder SINGLE = new BondOrder(ORDER.S.toString(), 1.0);
    public static BondOrder PARTIAL12 = new BondOrder(ORDER.partial12.toString(), 1.5);
    public static BondOrder DOUBLE = new BondOrder(ORDER.D.toString(), 2.0);
    public static BondOrder PARTIAL23 = new BondOrder(ORDER.partial23.toString(), 2.5);
    public static BondOrder TRIPLE = new BondOrder(ORDER.T.toString(), 3.0);
    public static BondOrder AROMATIC = new BondOrder(ORDER.A.toString(), 1.5);

    // these shouldn't be necessary
    public final static String S_S = "S";
    public final static String D_S = "D";
    public final static String T_S = "T";

    /// <summary>
    /// returns order in bond
    /// </summary>
    /// <param name="bond"></param>
    /// <returns>SINGLE,DOUBLE,TRIPLE,AROMATIC or UNKNOWN (not null)</returns>
    public static BondOrder GetBondOrder(CMLBond bond)
    {
        String value = bond.getAttributeValue(CMLAttributeNew.ORDER);
        return (value == null) ? UNKNOWN : GetBondOrderFromCMLOrder(value);
    }

    public static String getCMLBondOrder(CMLBond bond) {
    	return GetBondOrder(bond).getCmlBondOrder();
    }
    
    public static void normalizeBondOrder(CMLBond bond) {
    	bond.setOrder(GetBondOrder(bond).getCmlBondOrder());
    }
    
    
    /// <summary>
    /// creates BondOrder
    /// if value is unknown, order = Order.UNKNOWN
    /// </summary>
    /// <param name="value"></param>
    private BondOrder(String value, double numericBondOrder)
    {
        this.numericBondOrder = numericBondOrder;
        cmlBondOrder = value;
        _order = ORDER.unknown;
        if (numericBondOrder == 1)
        {
            _order = ORDER.S;
        }
        if (numericBondOrder == 2)
        {
            _order = ORDER.D;
        }
        if (numericBondOrder == 3)
        {
            _order = ORDER.T;
        }
        //           ParseOrder(cmlBondOrder);
    }

    /// <summary>
    /// get BondOrder for numeric value
    /// if value = 1,2,3 returns BondOrder else null
    /// </summary>
    /// <param name="value"></param>
    /// <returns></returns>
    public static BondOrder GetBondOrder(int value)
    {
        BondOrder bondOrder = null;
        switch (value)
        {
            case 1:
                bondOrder = SINGLE;
                break;
            case 2:
                bondOrder = DOUBLE;
                break;
            case 3:
                bondOrder = TRIPLE;
                break;
        }
        return bondOrder;
    }

    /// <summary>
    /// get BondOrder from integer as String
    /// convenience method
    /// </summary>
    /// <param name="value">"1", "2", "3"</param>
    /// <returns></returns>
    public static BondOrder GetBondOrderFromJumboInteger(String value)
    {
        BondOrder bondOrder = BondOrder.UNKNOWN;
        int intValue = Integer.parseInt(value);
        bondOrder = BondOrder.GetBondOrder(intValue);
        return bondOrder;
    }

    /// <summary>
    /// get BondOrder for String value
    /// if value = CMLBond.SINGLE, DOUBLE, TRIPLE, AROMATIC returns BondOrder else UNKNOWN
    /// </summary>
    /// normalizes deprecated values (1,2,3)
    /// <param name="value"></param>
    /// <returns></returns>
    public static BondOrder GetBondOrderFromCMLOrder(String value)
    {
        BondOrder bondOrder = UNKNOWN;
        String s1 = CMLBond.DEPRECATED_SINGLE;
        if (value == null)
        {
        }
        else if (value.equals(BondOrder.SINGLE.getCmlBondOrder()) ||
//                value.equals(CMLBond.DEPRECATED_SINGLE))
                value.equals(s1))
        {
            bondOrder = SINGLE;
        }
        else if (value.equals(BondOrder.DOUBLE.getCmlBondOrder()) ||
                value.equals(CMLBond.DEPRECATED_DOUBLE))
        {
            bondOrder = DOUBLE;
        }
        else if (value.equals(BondOrder.TRIPLE.getCmlBondOrder()) ||
                value.equals(CMLBond.DEPRECATED_TRIPLE))
        {
            bondOrder = TRIPLE;
        }
        else if (value.equals(BondOrder.AROMATIC.toString()))
        {
            bondOrder = AROMATIC;
        }
        else if (value.equals(ORDER.hbond.toString()))
        {
            bondOrder = HBOND;
        }
        else if (value.equals(ORDER.partial01.toString()))
        {
            bondOrder = PARTIAL01;
        }
        else if (value.equals(ORDER.partial12.toString()))
        {
            bondOrder = PARTIAL12;
        }
        else if (value.equals(ORDER.partial23.toString()))
        {
            bondOrder = PARTIAL23;
        }
        else if (value.equals(ORDER.unknown.toString()))
        {
            bondOrder = UNKNOWN;
        }
        return bondOrder;
    }


    /*
     public Integer JumboIntegerBondOrder
    {
        get
        {
            int? o = null;
            if (numericBondOrder.HasValue)
            {
                o = (int) numericBondOrder;
                if (Math.Abs((double) numericBondOrder - (int) o) > 0.01)
                {
                    o = null;
                }
            }
            return o;
        }
    }
     */

    public Double getNumericBondOrder()
    {
        return numericBondOrder;
    }

    public String getCmlBondOrder()
    {
        return cmlBondOrder;
    }

    public String toString()
    {
        return cmlBondOrder+" "+_order+" "+numericBondOrder;
//            return ("cml: {0} ORDER: {1} numeric: {2}", cmlBondOrder, _order, numericBondOrder);
    }
}
