package org.xmlcml.cml.element;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLElement;

/** The definition for an entry.
*
* 
* The definition should be a short nounal phrase defining the subject of the entry. Definitions should not include commentary, implementations, equations or formulae (unless the subject is one of these) or examples.\n The definition can be in any markup language, but normally XHTML will be used, \nperhaps with links to other XML namespaces such as CML for chemistry.
* 
* NON-MOFIFIABLE class autogenerated from schema
* DO NOT EDIT; ADD FUNCTIONALITY TO SUBCLASS

*/
public abstract class AbstractDefinition extends CMLElement {

// fields;
    /** local name*/
    public final static String TAG = "definition";
    /** default constructor.
    *
    * creates element initially without parent


    */

    public AbstractDefinition() {
        super("definition");
    }
    /** copy constructor.
    *
    * deep copy using XOM copy()

    * @param old AbstractDefinition to copy

    */

    public AbstractDefinition(AbstractDefinition old) {
        super((CMLElement) old);
    }

    /** overrides addAttribute(Attribute).
    *
    * reroutes calls to setFoo()

    * @param att  attribute

    */
    public void addAttribute(Attribute att) {
            super.addAttribute(att);
    }
}
