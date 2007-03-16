/**
 * 
 */
package org.xmlcml.cml.schemagen;

import org.xmlcml.cml.base.CMLConstants;


/**
 * @author pm286
 *
 */
public interface SchemagenConstants extends CMLConstants {

	/** */
	String XSDSUFF = ".xsd";

	// commands
	/** */
	String HELP_OPT_NAME = "HELP";
	/** */
	String SCHEMADIR_ARG = "schemaDir";
	/** */
	String SCHEMADIR_OPT_NAME = "SCHEMADIR";
	/** */
	String TYPEDIR_ARG = "typeDir";
	/** */
	String TYPEDIR_OPT_NAME = "TYPEDIR";
	/** */
	String ATTRIBUTEDIR_ARG = "attDir";
	/** */
	String ATTRIBUTEDIR_OPT_NAME = "ATTDIR";
	/** */
	String ELEMENTDIR_ARG = "elemDir";
	/** */
	String ELEMENTDIR_OPT_NAME = "ELEMDIR";
	/** */
	String SCHEMAFILE_ARG = "schemaFile";
	/** */
	String SCHEMAFILE_OPT_NAME = "SCHEMAFILE";
	/** */
	String OUTDIR_ARG = "outputDir";
	/** */
	String OUTDIR_OPT_NAME = "OUTDIR";
	/** */
	String CODE_OUTDIR_ARG = "codeOutputDir";
	/** */
	String CODE_OUTDIR_OPT_NAME = "CODEDIR";
	
	/** */
	String DEFAULT_SCHEMAFILE = "schema.xsd";
	/** */
	String DEFAULT_TYPEDIR = "types";
	/** */
	String DEFAULT_ATTRIBUTEDIR = "attributeGroups";
	/** */
	String DEFAULT_ELEMENTDIR = "elements";
	/** */
	String SCHEMA_DIR = "org/xmlcml/cml/base/";
	/** */
	String SCHEMAGEN_DIR = "org/xmlcml/cml/schemagen/";
	/** */
	String ELEMENT1 = "element1.txt";

	/** */
	String ABSTRACT = "Abstract";
	
	/** */
	String ATTRIBUTES_XSD = "attributes.xsd";
	/** */
	String ELEMENTS_XSD   = "elements.xsd";
	/** */
	String TYPES_XSD      = "types.xsd";
	
}
