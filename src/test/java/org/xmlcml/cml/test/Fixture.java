package org.xmlcml.cml.test;

import org.xmlcml.cml.base.CMLConstants;

public class Fixture {
	/** root of tests. */
	public final static String TEST_RESOURCE = "org/xmlcml/cml/element";

	/** root of examples. */
	public final static String EXAMPLES_RESOURCE = TEST_RESOURCE + CMLConstants.S_SLASH
			+ "examples";

	/** root of complex examples. */
	public final static String COMPLEX_RESOURCE = EXAMPLES_RESOURCE + CMLConstants.S_SLASH
			+ "complex";

	/** root of experimental examples. */
	public final static String EXPERIMENTAL_RESOURCE = EXAMPLES_RESOURCE
			+ CMLConstants.S_SLASH + "experimental";

	/** root of xsd examples. */
	public final static String SIMPLE_RESOURCE = EXAMPLES_RESOURCE + CMLConstants.S_SLASH
			+ "xsd";

	/** root of dictionary examples. */
	public final static String DICT_RESOURCE = EXAMPLES_RESOURCE + CMLConstants.S_SLASH
			+ "dict";

	/** root of unit examples. */
	public final static String UNIT_RESOURCE = EXAMPLES_RESOURCE + CMLConstants.S_SLASH
			+ "units";

	/** root of tool tests. */
	public final static String TOOL_TEST_RESOURCE = "org/xmlcml/cml/tools";

	/** root of tool test examples. */
	public final static String TOOL_EXAMPLES_RESOURCE = TOOL_TEST_RESOURCE
			+ CMLConstants.S_SLASH + "examples";

	/** root of tool test molecules. */
	public final static String TOOL_MOLECULES_RESOURCE = TOOL_EXAMPLES_RESOURCE
			+ CMLConstants.S_SLASH + "molecules";

	/** index in each directory. */
	public final static String INDEX = "index.xml";

	static String TEST_INDEX = TEST_RESOURCE + CMLConstants.S_SLASH + INDEX;

	/** final string in dictionary namespaces */
	public final static String CML_DICT = "cml";

	/** alternative namespace for cml dictionary :-( */
	public final static String CML_DICT_DICT = CML_DICT + "Dict";

	/** cml comp dictionary */
	public final static String CML_COMP_DICT = "cmlComp";

	final static double EPS = 0.0000000001;

}
