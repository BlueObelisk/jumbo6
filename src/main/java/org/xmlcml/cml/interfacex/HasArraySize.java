package org.xmlcml.cml.interfacex;

import java.util.List;


/**
 * interface for CMLArray or CMLList
 */
public interface HasArraySize {

    /** get size of array.
     * @return size
     */
    int getArraySize();
    
    /** get array elements.
     * recalcuates each time so best cached for frequent use
     * @return elements as String
     */
    List<String> getStringValues();
}
