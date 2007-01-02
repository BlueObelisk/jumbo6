package org.xmlcml.cml.legacy;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.EuclidConstants;

/**
 * factory for LegacyConverter
 * 
 * @author Administrator
 */
public class LegacyConverterFactoryOld implements EuclidConstants {
    
    /** create LegacyFactory from classname.
     * 
     * @param className fully qualified (e.g. "org.xmlcml.cml.legacy.cif.CIFConverter");
     * @return the legacyConverter
     * @throws CMLRuntimeException
     */
    public static LegacyConverterOld createLegacyConverter(String className) throws CMLRuntimeException {
        LegacyConverterOld legacyConverter = null;
        Class classx = null;
        try {
            classx = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new CMLRuntimeException("Cannot find LegacyConverter ("+e+S_RBRAK+className);
        }
        try {
            legacyConverter = (LegacyConverterOld) classx.newInstance();
        } catch (Exception e) {
            throw new CMLRuntimeException("Cannot instantiate LegacyConverter ("+e+S_RBRAK+className);
        }
        return legacyConverter;
    }
}