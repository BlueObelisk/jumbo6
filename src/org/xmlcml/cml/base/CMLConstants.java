package org.xmlcml.cml.base;

import nu.xom.XPathContext;

import org.xmlcml.euclid.EuclidConstants;

/**
 * 
 * <p>
 * Constants
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface CMLConstants extends EuclidConstants {

//  -------------------------------------------    
    /** xmlns attribute name */
    String XMLNS = "xmlns";

    /**
     * XSD namespace. no trailing slash
     */
    String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    /** XSD prefix = 'xsd' */
    String XSD_PREFIX = "xsd";

    /**
     * namespace declaration for XSD
     * xmlns:xsd='http://www.w3.org/2001/XMLSchema'
     */
    String XSD_XMLNS = XMLNS + S_COLON + XSD_PREFIX + S_EQUALS + S_APOS
            + XSD_NS + S_APOS;

    /** constant */
    String XSD_ANYURI = "xsd:anyURI";

    /** constant */
    String XSD_BOOLEAN = "xsd:boolean";

    /** constant */
    String XSD_DOUBLE = "xsd:double";

    /** constant */
    String XSD_FLOAT = "xsd:float";

    /** constant */
    String XSD_INTEGER = "xsd:integer";

    /** constant */
    String XSD_MAXEXCLUSIVE = "xsd:maxExclusive";

    /** constant */
    String XSD_MAXINCLUSIVE = "xsd:maxInclusive";

    /** constant */
    String XSD_MINEXCLUSIVE = "xsd:minExclusive";

    /** constant */
    String XSD_MININCLUSIVE = "xsd:minInclusive";

    /** constant */
    String XSD_NONNEGATIVEINTEGER = "xsd:nonNegativeInteger";

    /** constant */
    String XSD_POSITIVEINTEGER = "xsd:positiveInteger";

    /** constant */
    String XSD_POSITIVE_NUMBER = "xsd:positiveNumber";

    /** constant */
    String XSD_STRING = "xsd:string";

    /** constant */
    String XSD_QNAME = "xsd:QName";

    /** constant */
    String CMLXSD_ANNOTATION = "annotation";

    /** constant */
    String CMLXSD_ANY = "any";

    /** constant */
    String CMLXSD_APPINFO = "appinfo";

    /** constant */
    String CMLXSD_ATTRIBUTE = "attribute";

    /** constant */
    String CMLXSD_ATTRIBUTEGROUP = "attributeGroup";

    /** constant */
    String CMLXSD_BASE = "base";

    /** constant */
    String CMLXSD_CHOICE = "choice";

    /** constant */
    String CMLXSD_COMPLEXTYPE = "complexType";

    /** constant */
    String CMLXSD_DOCUMENTATION = "documentation";

    /** constant */
    String CMLXSD_ELEMENT = "element";

    /** constant */
    String CMLXSD_ENUMERATION = "enumeration";

    /** constant */
    String CMLXSD_EXTENSION = "extension";

    /** constant */
    String CMLXSD_ID = "id";

    /** constant */
    String CMLXSD_ITEMTYPE = "itemType";

    /** constant */
    String CMLXSD_LENGTH = "length";

    /** constant */
    String CMLXSD_LIST = "list";

    /** constant */
    String CMLXSD_MAXEXCLUSIVE = "maxExclusive";

    /** constant */
    String CMLXSD_MAXINCLUSIVE = "maxInclusive";

    /** constant */
    String CMLXSD_MINEXCLUSIVE = "minExclusive";

    /** constant */
    String CMLXSD_MININCLUSIVE = "minInclusive";

    /** constant */
    String CMLXSD_NAME = "name";

    /** constant */
    String CMLXSD_PATTERN = "pattern";

    /** constant */
    String CMLXSD_REF = "ref";

    /** constant */
    String CMLXSD_RESTRICTION = "restriction";

    /** constant */
    String CMLXSD_ROOT = "root";

    /** constant */
    String CMLXSD_SEQUENCE = "sequence";

    /** constant */
    String CMLXSD_SIMPLECONTENT = "simpleContent";

    /** constant */
    String CMLXSD_SIMPLETYPE = "simpleType";

    /** constant */
    String CMLXSD_TEXT = "text";

    /** constant */
    String CMLXSD_TYPE = "type";

    /** constant */
    String CMLXSD_UNBOUNDED = "unbounded";

    /** constant */
    String CMLXSD_UNION = "union";

    /** constant */
    String CMLXSD_VALUE = "value";

    /** constant */
    String CMLXSD_ATTPREFIX = "_att_";

    /** constant */
    String CMLXSD_XMLCONTENT = "_xmlContent";

    /** constant */
    String CML_NS = "http://www.xml-cml.org/schema";

    /** constant */
    String CML = CML_NS;

    /**
     * cml dictionary namespace reserved
     */
    String DICT_NS = "http://www.xml-cml.org/dict";

    /**
     * cml dictionary reserved
     */
    String CML_DICT_NS = DICT_NS+U_S+"cml";

    /** constant */
    String CML1 = CML + "/" + "cml1";

    /** constant */
    String CML2 = CML + "/" + "cml2";

    /** constant */
    String CML3 = CML + "/" + "cml3";

    /**
     * CML prefix (cml) reserved: for several uses
     */
    String CML_PREFIX = "cml";

    /**
     * namespace declaration for CML without prefix
     */
    String CML_XMLNS = XMLNS + S_EQUALS + S_APOS + CML_NS + S_APOS;

    /**
     * namespace declaration for CML with prefix
     */
    String CML_XMLNS_PREFIX = XMLNS + S_COLON + CML_PREFIX + S_EQUALS + S_APOS
            + CML_NS + S_APOS;

    /**
     * obsolete CML namespaces
     */
    String[] OLD_NAMESPACES = { CML1, CML2, CML3, "http://www.xmlcml.org/",
            "http://www.xmlcml.org/schema", };

    /** constant */
    String XHTML_NS = "http://www.w3.org/1999/xhtml";
    
    /** XPathContext for CML.
     */
    XPathContext X_CML = new XPathContext("cml", CML_NS);

    // subdirs of components
    /** constant */
    String TYPES = "types";

    /** constant */
    String ATTRIBUTES = "attributeGroups";

    /** constant */
    String ELEMENTS = "elements";

    /** constant */
    String TEXT_LIST = "CMLTextList";

    /** constant */
    String ATTRIBUTE_LIST = "CMLAttributeList";

    /** constant */
    String ELEMENT_LIST = "CMLElementList";

    /** constant */
    String TYPE_LIST = "CMLTypeList";

    /** constant */
    String ABSTRACT_NODEFACTORY = "CMLNodeFactory";

    /** constant */
    String DOUBLE = "Double";

    /** constant */
    String INTEGER = "Integer";

    /** constant */
    String STRING = "String";

    // sepcial CML dataTypes
    /** dataType */
    String CML_DATATYPETYPE = "dataTypeType";

    /** namespaceRef */
    String CML_NAMESPACEREFTYPE = "namespaceRefType";

    /** units */
    String CML_UNITSTYPE = "unitsType";

    /** dictRef value identifying a filename */
    String CML_FILENAME = "cml:filename";

// -----------------------------------------------    
    // format
    /** constant */
    String BANNER_S = "**************************************";

    /** constant */
    String WARNING_S = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";

    /** constant */
    String AUTOGENERATED_DONOTEDIT_S = 
        "/*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/";

    /** catalog.*/
    String CATALOG_XML = "catalog.xml";
    
    /**
     * units prefix reserved: for several uses
     */
    String CML_UNITS = "units";
    /**
     * units root namespace reserved
     */
    String _UNIT_NS = "http://www.xml-cml.org/units";

    /**
     * units dictionary namespace reserved
     */
    String UNIT_NS = _UNIT_NS+U_S+"units";

    /**
     * siUnits dictionary namespace reserved
     */
    String SIUNIT_NS = _UNIT_NS+U_S+"siUnits";

    /**
     * unnitTypes dictionary namespace reserved
     */
    String UNITTYPES_NS = _UNIT_NS+U_S+"unitTypes";

    /**
     * siUnits prefix reserved: for several uses
     */
    String CML_SIUNITS = "siUnits";

    // These are IDs, and must match those on the test dictionaries
    /** angstrom. */
    String U_ANGSTROM = CML_UNITS + S_COLON + "ang";

    /** degree. */
    String U_DEGREE = CML_UNITS + S_COLON + "deg";

    /** degree. */
    String U_KCAL = CML_UNITS + S_COLON + "kcal";

    /** celsius. */
    String U_CELSIUS = CML_UNITS + S_COLON + "celsius";

    // ================== crystal ================

    /**
     * dictRef ids for 6 scalar children of crystal.
     */
    String CRYSTAL_DICT_REFS[] = { CML_PREFIX + S_COLON + "a",
            CML_PREFIX + S_COLON + "b", CML_PREFIX + S_COLON + "c",
            CML_PREFIX + S_COLON + "alpha", CML_PREFIX + S_COLON + "beta",
            CML_PREFIX + S_COLON + "gamma" };

    /**
     * unit refs for 6 scalar children of crystal.
     */
    String[] CRYSTAL_DICT_UNITS = { CML_UNITS + S_COLON + "ang",
            CML_UNITS + S_COLON + "ang", CML_UNITS + S_COLON + "ang",
            CML_UNITS + S_COLON + "degree", CML_UNITS + S_COLON + "degree",
            CML_UNITS + S_COLON + "degree" };

    // ======= test ==========
    /**
     * number of dictionaries. has to be altered every time new dictionaries are
     * added.
     */
    int NDICT = 4;

    /**
     * number of units dictionaries. has to be altered every time new units
     * dictionaries are added.
     */
    int NUNIT_DICT = 5;

    /**
     * number of unitType dictionaries. has to be altered every time new units
     * dictionaries are added.
     */
    int NUNIT_TYPE_DICT = 1;

}