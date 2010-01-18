package org.xmlcml.cml.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TstUtils;


/**
 * @author pm286
 * 
 */
public class FragmentToolTest {
	private static Logger LOG = Logger.getLogger(FragmentToolTest.class);

	private ResourceManager resourceManager = null;

	private CMLBuilder cmlBuilder;

	/**
	 */
	@Before
	public void setUp() {
		File CMLMapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		resourceManager = new ResourceManager(CMLMapFile.toURI()); 
		if (cmlBuilder == null) {
			cmlBuilder = new CMLBuilder();
		}
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.FragmentTool#setMolecule(org.xmlcml.cml.element.CMLMolecule)}.
	 */
	@Test
	public void testGetAndSetMolecule() {
		CMLFragment fragment = new CMLFragment();
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
		CMLMolecule molecule = null;
		CMLMolecule fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNull("fragment molecule", fragmentMolecule);

		molecule = new CMLMolecule();
		molecule.setId("foo");
		fragmentTool.setMolecule(molecule);
		fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNotNull("fragment molecule", fragmentMolecule);
		Assert.assertEquals("fragment molecule", "foo", fragmentMolecule.getId());
		molecule = new CMLMolecule();
		molecule.setId("bar");
		fragmentTool.setMolecule(molecule);
		fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNotNull("fragment molecule", fragmentMolecule);
		Assert.assertEquals("fragment molecule", "bar", fragmentMolecule.getId());
		Assert.assertEquals("fragment molecule", 1, fragment.getChildCMLElements(CMLMolecule.TAG).size());

	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.tools.Catalog)}. *
	 * test basic concatenation
	 */
	@Test
	public void testProcessIntermediate() {
		CMLFragment fragment = (CMLFragment) readElement0("mol");
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
		fragmentTool.processBasic(resourceManager);

		List<String> prefixes = CMLUtil.getPrefixes(fragment, "ref");
		Assert.assertEquals("prefixes", 1, prefixes.size());
		Assert.assertEquals("prefixes", "g", prefixes.get(0));
		List<CMLNamespace> namespaces = CMLUtil.getNamespaces(fragment, prefixes);
		Assert.assertEquals("prefixes", 1, namespaces.size());
		CMLNamespace namespace = namespaces.get(0);
		Assert.assertEquals("prefixes", "http://www.xml-cml.org/mols/geom1", namespace.getNamespaceURI());
		Assert.assertEquals("prefixes", "g", namespace.getPrefix());

		// CMLMap cmlMap = moleculeCatalog.getCmlMap();
		/*
		 * String mapS = "" + "<map xmlns='http://www.xml-cml.org/schema'>"+ "<!--
		 * DIRECTORY -->"+ " <link convention='cml:relativeUrl'
		 * from='http://www.xml-cml.org/mols/geom' " + " role='cml:moleculeList'
		 * to='./geom.xml'/>"+ " <link convention='cml:relativeUrl'
		 * from='http://www.xml-cml.org/mols/frags' " + "
		 * role='cml:fragmentList' to='./fragments/frags.xml'/>"+ " <link
		 * convention='cml:relativeUrl'
		 * from='http://www.xml-cml.org/mols/fragments' " + "
		 * role='cml:fragmentList' to='./fragments'/>"+ " <link
		 * convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/geom1' " + "
		 * role='cml:moleculeList' to='./geom1'/>"+ "</map>"; CMLMap mapE =
		 * (CMLMap)TestUtils.parseValidString(mapS);TestUtils.TestUtils.assertEqualsCanonically("map", mapE,
		 * cmlMap, true);
		 */// Duplicate Test from Test Catalog. Removes ability to update
		// catalog without changing hardcoded tests so commenting out. nwe23
		HashMap<String,CMLElement> moleculeList = resourceManager.getIndex(namespace).get(ResourceManager.IdTypes.ID);
		Assert.assertNotNull("moleculeList", moleculeList);
		Assert.assertTrue("moleculeList", 30 <= moleculeList.keySet().size());

		fragmentTool.processIntermediate(resourceManager);

		CMLElement explicit = readElement0("molE");
		TstUtils.assertEqualsCanonically("fragment", explicit, fragment, true);
	}

	/**
	 * no longer relevant
	 */
	@Test
	// FIXME needs CountAttribute fixed
	public void testProcessRecursively() {
		String[] ATOMREFS2 = new String[] { "r2", "r1" };
		String[] MOLREFS2 = new String[] { CMLJoin.PREVIOUS_S, CMLJoin.NEXT_S };
		CMLFragment fragment = new CMLFragment();
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
		CMLFragment fragment1 = new CMLFragment();
		fragment1.setId("f1");
		fragment.appendChild(fragment1);
		CMLJoin join = new CMLJoin();
		join.setId("j1");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f2");
		fragment.appendChild(fragment1);
		CMLMolecule benzene = new CMLMolecule();
		fragment1.appendChild(benzene);
		// add branches
		join = new CMLJoin();
		join.setId("br1");
		join.setMoleculeRefs2(new String[] { CMLJoin.PARENT_S, CMLJoin.CHILD_S });
		join.setAtomRefs2(new String[] { "r3", "r1" });
		benzene.appendChild(join);
		CMLFragment branchFragment = new CMLFragment();
		branchFragment.setId("brf1");
		join.appendChild(branchFragment);

		join = new CMLJoin();
		join.setId("br2");
		join.setMoleculeRefs2(new String[] { CMLJoin.PARENT_S, CMLJoin.CHILD_S });
		join.setAtomRefs2(new String[] { "r4", "r1" });
		benzene.appendChild(join);
		branchFragment = new CMLFragment();
		branchFragment.setId("brf2");
		join.appendChild(branchFragment);

		fragment1 = new CMLFragment();
		fragment1.setId("f2");
		fragment.appendChild(fragment1);

		join = new CMLJoin();
		join.setId("j2");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f3");
		fragment.appendChild(fragment1);
		join = new CMLJoin();
		join.setId("j3");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);

		CMLFragment fragmentList11 = new CMLFragment();
		fragment.appendChild(fragmentList11);
		fragmentList11.setCountExpression("range(2,4)");
		join = new CMLJoin();
		join.setId("j11");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList11.appendChild(join);

		CMLFragment fragment11 = new CMLFragment();
		fragment11.setId("f11");
		fragmentList11.appendChild(fragment11);

		CMLFragment fragmentList = new CMLFragment();
		fragmentList.setCountExpression("range(1,4)");
		join = new CMLJoin();
		join.setId("j4");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f4");
		fragmentList.appendChild(fragment1);
		fragment11.appendChild(fragmentList);
		CMLMolecule glucose = new CMLMolecule();
		glucose.setId("glu");
		fragment1.appendChild(glucose);
		// add branches
		join = new CMLJoin();
		join.setId("br4");
		join.setMoleculeRefs2(new String[] { CMLJoin.PARENT_S, CMLJoin.CHILD_S });
		join.setAtomRefs2(new String[] { "r3", "r1" });
		glucose.appendChild(join);
		branchFragment = new CMLFragment();
		branchFragment.setId("brf4");
		join.appendChild(branchFragment);
		fragmentList = new CMLFragment();
		fragmentList.setId("f44");
		fragmentList.setCountExpression("*(4)");
		branchFragment.appendChild(fragmentList);
		join = new CMLJoin();
		join.setId("j44");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f4444");
		fragmentList.appendChild(fragment1);

		join = new CMLJoin();
		join.setId("j44");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment11.appendChild(join);

		fragmentList = new CMLFragment();
		fragmentList.setCountExpression("range(1,4)");
		join = new CMLJoin();
		join.setId("j5");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f5");
		fragmentList.appendChild(fragment1);
		fragment11.appendChild(fragmentList);

		CMLElement fragmentE = readElement0("recurse");
		TstUtils.assertEqualsCanonically("fragment", fragmentE, fragment, true);

		// maybe defer this...
		fragmentTool.basic_processRecursively();
		fragment = fragmentTool.getFragment();

		// NOT REPEATABLE
		// CMLFragment fragment1E = readFragment0("recurse1");
		// FIXME - this fails although the files seem to be identical
		//TestUtils.assertEqualsCanonically("fragment1", fragment1E, fragment, true);
	}

	/**
	 * expand arguments (cml:arg). calls:
	 * CMLArg.substituteParameterName(molecule, name, value);
	 * CMLArg.substituteParentAttributes(molecule);
	 * CMLArg.substituteTextContent(molecule);
	 */
	@Test
	public void testSubstituteParameters() {
		CMLElement fragment = readElement0("substitute0");
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool((CMLFragment) fragment);
		CMLMolecule molecule = fragmentTool.getMolecule();
		// MoleculeTool moleculeTool = new MoleculeTool(molecule);
		fragmentTool.substituteParameters();

		CMLMolecule moleculeE = (CMLMolecule) readElement0("substitute0M");
		TstUtils.assertEqualsCanonically("molecule", moleculeE, molecule, true);
	}

	/**
	 * expand arguments (cml:arg). calls:
	 * CMLArg.substituteParameterName(molecule, name, value);
	 * CMLArg.substituteParentAttributes(molecule);
	 * CMLArg.substituteTextContent(molecule);
	 */
	@Test
	public void testSubstituteParameters1() {
		// intermediate format for testing
		CMLElement fragment = readElement0("substitute");
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool((CMLFragment) fragment);
		fragmentTool.substituteParameters();
		CMLElement fragmentE = readElement0("substitute1");
		TstUtils.assertEqualsCanonically("fragment", fragmentE, fragment, true);
	}

	/**
	 */
	@Test
	public void testProperty() {

		CMLElement fragment = readElement0("property");
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool((CMLFragment) fragment);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();
		// <propertyList>
		// <property dictRef="cml:prop2" role="extensive">
		// <scalar units="unit:cm3" dataType="xsd:double">702.0</scalar>
		// </property>
		// <property dictRef="cml:prop1" role="intensive">
		// <scalar units="unit:g.cm-3" dataType="xsd:double">2.34</scalar>
		// </property>
		// </propertyList>
		List<Node> scalars = CMLUtil.getQueryNodes(fragment, CMLPropertyList.NS + "/" + CMLProperty.NS + "/"
				+ CMLScalar.NS, CMLConstants.CML_XPATH);
		Assert.assertEquals("scalars", 6, scalars.size());
		CMLScalar scalar = (CMLScalar) scalars.get(0);
		Assert.assertEquals("extensive", "intensive", ((CMLProperty) scalar.getParent()).getRole());
		Assert.assertEquals("extensive", "cml:prop1", ((CMLProperty) scalar.getParent()).getDictRef());
		Assert.assertEquals("extensive", 1.23, scalar.getDouble());
		scalar = (CMLScalar) scalars.get(1);
		Assert.assertEquals("intensive", "extensive", ((CMLProperty) scalar.getParent()).getRole());
		Assert.assertEquals("intensive", "cml:prop2", ((CMLProperty) scalar.getParent()).getDictRef());
		Assert.assertEquals("intensive", 123.0, scalar.getDouble());
	}

	private CMLElement readElement(String fileroot) throws FileNotFoundException {
		Document doc = readDocument(fileroot);
		return (CMLElement) doc.getRootElement();
	}

	/**
	 * @param fileroot
	 * @return document
	 * @throws FileNotFoundException
	 * @throws RuntimeException
	 */
	private Document readDocument(String fileroot) throws FileNotFoundException, RuntimeException {
		Document doc = null;
		try {
			InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/tools/examples/molecules/fragments/"
					+ fileroot + ".xml");
			doc = cmlBuilder.build(is);
		} catch (FileNotFoundException ioe) {
			throw ioe;
		} catch (Exception ioe) {
			throw new RuntimeException(ioe);
		}
		return doc;
	}

	private CMLElement readElement0(String fileRoot) {
		CMLElement element = null;
		if (fileRoot != null) {
			try {
				element = readElement(fileRoot);
			} catch (FileNotFoundException e) {
				System.err.println("FNF... " + e);
			} catch (Exception e) {
				// e.printStackTrace();
				System.err.println("EXC " + e + " (fileRoot: " + fileRoot + ")");
			}
		}
		return element;
	}

	private void test(boolean debug, boolean check, int serial, boolean checkIE) {
		long seed = 0;
		test(debug, check, serial, checkIE, seed);
	}

	private void test(boolean debug, boolean check, int serial, boolean checkIE, long seed) {
		String basicRoot = "mol" + serial;
		String intermediateRoot = null;
		String explicitRoot = null;
		String completeRoot = "mol" + serial + "C";
		if (checkIE) {
			intermediateRoot = "mol" + serial + "I";
			explicitRoot = "mol" + serial + "E";
		}
		generateElement(basicRoot, debug, serial, intermediateRoot, explicitRoot, completeRoot, check, seed);
	}

	private void generateElement(String basicRootName, boolean debug, int serial, String intermediateRootName,
			String explicitRootName, String completeRootName, boolean check, long seed) {
		System.err.println("================TEST=============== " + basicRootName);
		CMLElement fragment = null;
		CMLElement intermediate = null;
		CMLElement explicit = null;
		CMLElement complete = null;
		try {
			fragment = (CMLFragment) readElement(basicRootName);
		} catch (Exception e) {
			System.err.println("EXCEPTION " + e);
		}
		try {
			intermediate = readElement0(intermediateRootName);
		} catch (Exception e) {
			System.err.println("Missing document will be written ...");
		}
		try {
			explicit = readElement0(explicitRootName);
		} catch (Exception e) {
			System.err.println("Missing document will be written ...");
		}
		try {
			complete = readElement0(completeRootName);
		} catch (Exception e) {
			System.err.println("Missing document will be written ...");
		}
		generateElement(fragment, debug, serial, intermediate, explicit, complete, check, seed);
	}

	private void generateElement(CMLElement fragment, boolean debug, int serial, CMLElement intermediate,
			CMLElement explicit, CMLElement complete, boolean check, long seed) {
		if (fragment == null) {
			throw new RuntimeException("NULL FRAGMENT");
		}
		if (!(fragment instanceof CMLFragment)) {
			throw new RuntimeException("NOT A FRAG "+fragment);
		}
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool((CMLFragment) fragment);
		fragmentTool.setSeed(seed);
		CMLElement generatedElement = fragmentTool.processBasic(resourceManager);
		if (generatedElement == null) {
			runSimple((CMLFragment) fragment, fragmentTool, debug, serial, intermediate, explicit, complete, check);
		} else if (generatedElement instanceof CMLFragmentList) {
			runMarkush((CMLFragmentList) generatedElement, debug, serial, intermediate, explicit, complete, check);
		}
	}

	// As Java has no default arguments calls through adding a seed of 0 where
	// the behaviour is random if no seed given.
//	private void generateElement(CMLElement fragment, boolean debug, int serial, CMLElement intermediate,
//			CMLElement explicit, CMLElement complete, boolean check) {
//		long seed = 0;
//		generateElement(fragment, debug, serial, intermediate, explicit, complete, check, seed);
//	}

	private void runSimple(CMLFragment fragment, FragmentTool fragmentTool, boolean debug, int serial,
			CMLElement intermediate, CMLElement explicit, CMLElement complete, boolean check) {
		String title = "intermediate" + serial;
		if (debug) {
			fragment.debug(title);
		}
		if (intermediate == null) {
			dump(fragment, "mol" + serial + "I");
		} else if (check) {
			TstUtils.assertEqualsCanonically(title, intermediate, fragment, true);
		}
		if (debug) {
			fragment.debug(title);
		}
		// intermediate -> explicit
		fragmentTool.processIntermediate(resourceManager);
		title = "explicit" + serial;
		if (debug) {
			fragment.debug(title);
		}
		if (explicit == null) {
			dump(fragment, "mol" + serial + "E");
		} else if (check) {
			TstUtils.assertEqualsCanonically(title, explicit, fragment, true);
		}
		// explicit -> complete
		fragmentTool.processExplicit();
		title = "complete" + serial;
		if (debug) {
			fragment.debug(title);
		}
		if (complete == null) {
			dump(fragment, "mol" + serial + "C");
		} else if (check) {
			TstUtils.assertEqualsCanonically(title, complete, fragment, true);
		}
	}

	private CMLFragment processFragment(CMLFragment fragment) {
		if (fragment == null) {
			throw new RuntimeException("Null fragment");
		}
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
		fragmentTool.processBasic(resourceManager);
		fragmentTool.processIntermediate(resourceManager);
		fragmentTool.processExplicit();
		return fragmentTool.getFragment();
	}

	private void dump(Element element, String filename) {
		String dir = System.getProperty("user.dir");
		dir += File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "org"
				+ File.separator + "xmlcml" + File.separator + "cml" + File.separator + "tools" + File.separator
				+ "examples" + File.separator + "molecules" + File.separator + "fragments";
		try {
			String outfile = dir + File.separator + filename + ".xml";
			FileOutputStream fos = new FileOutputStream(outfile);
			CMLUtil.debug(element, fos, 2);
			fos.close();
			LOG.debug("WROTE: " + outfile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void runMarkush(CMLFragmentList fragmentList, boolean debug, int serial, CMLElement intermediate,
			CMLElement explicit, CMLElement complete, boolean check) {

		int serialx = 0;
		for (CMLFragment fragment : fragmentList.getFragmentElements()) {
			String rootName = "frag" + "_" + serial + "_" + (++serialx);
			CMLElement complete0 = null;
			try {
				complete0 = readElement0(rootName);
			} catch (Exception e) {
				System.err.println("Missing document will be written ...");
			}
			if (complete0 == null) {
				dump(fragment, rootName);
			} else if (check) {
				TstUtils.assertEqualsCanonically(rootName, complete0, fragment, true);
			}
		}
		String title = "complete" + serial;
		if (debug) {
			fragmentList.debug(title);
		}
		if (complete == null) {
			dump(fragmentList, "mol" + serial + "C");
		} else if (check) {
			TstUtils.assertEqualsCanonically(title, complete, fragmentList, true);
		}
	}

	/**
	 * intermediate result in processing Markush
	 */
	@Test
	public void testGeneratedMarkush() {

		CMLElement generatedFragment = readElement0("markush");
		FragmentTool generatedFragmentTool = FragmentTool.getOrCreateTool((CMLFragment) generatedFragment);
		generatedFragmentTool.processAll(resourceManager);
		CMLElement generatedE = readElement0("markushE");
		TstUtils.assertEqualsCanonically("generated", generatedE, generatedFragment, true);
	}

	/**
	 * test bump checker
	 */
	@Test
	public void testBumps() {
		runBumpsTest("mol6");
		runBumpsTest("mol10");
	}

	private void runBumpsTest(String molname) {
		LOG.debug("Checking bumps: " + molname);
		CMLElement fragment = readElement0(molname);
		CMLFragment fragmentx = processFragment((CMLFragment) fragment);
		CMLMolecule molecule = (CMLMolecule) fragmentx.getMoleculeElements().get(0);
		List<CMLAtom> atomList = molecule.getAtoms();
		CMLAtomSet allAtomSet = new CMLAtomSet(molecule);
		int natoms = atomList.size();
		for (int i = 0; i < natoms; i++) {
			CMLAtom atomi = atomList.get(i);
			if (AS.H.equals(atomi.getElementType())) {
				continue;
			}
			// get all atoms within 3 bonds
			CMLAtomSet atomSet13 = AtomTool.getOrCreateTool(atomi).getCoordinationSphereSet(3);
			CMLAtomSet nonBonded = allAtomSet.complement(atomSet13);
			List<CMLAtom> nonBondedAtomList = nonBonded.getAtoms();
			for (CMLAtom atomj : nonBondedAtomList) {
				if (AS.H.equals(atomj.getElementType())) {
					continue;
				}
				if (atomi.getId().compareTo(atomj.getId()) <= 0) {
					continue;
				}
				boolean bump = atomi.isWithinRadiusSum(atomj, ChemicalElement.RadiusType.VDW);
				if (bump) {
//					double dist = atomi.getDistanceTo(atomj);
//					LOG.debug("BUMP " + atomi.getId() + "-" + atomj.getId() + ": " + dist);
					// FIXME use CMLLength
				}
			}
		}
	}

	@Test
	public void moleculeByValue() {
		String s = ""+
		"<fragment  convention='cml:PML-intermediate' "+
		"	   xmlns='http://www.xml-cml.org/schema'"+
		"	   xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"	  <fragment>"+
		"<molecule id='2pyr'>"+
		"	<atomArray>"+
		"		<atom id='a1' elementType='C' x3='0.9130440201297267' y3='2.8881300000000003'"+
		"			z3='24.75205498269886'>"+
		"	        <label dictRef='cml:torsionEnd'>r6</label>"+
		"		</atom>"+
		"		<atom id='r1' elementType='R' x3='1.8668275372206224' y3='3.3649546'"+
		"			z3='23.74540701932392'>"+
		"		</atom>"+
		"		<atom id='a2' elementType='C' x3='0.6710034793604291' y3='1.5629880000000003'"+
		"			z3='24.99245916290205'>"+
		"        <label dictRef='cml:torsionEnd'>r1</label>"+
		"		</atom>"+
		"		<atom id='r2' elementType='R' x3='1.1172207931351144' y3='0.9038148000000001'"+
		"			z3='24.513083971081915'>"+
		"		</atom>"+
		"		<atom id='a3' elementType='N' x3='0.2765492911364054' y3='3.8757572000000002'"+
		"			z3='25.381508959946053'>"+
		"	        <label dictRef='cml:torsionEnd'>r2</label>"+
		"		</atom>"+
		"		<atom id='a4' elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002'"+
		"			z3='25.965836532665875'>"+
		"	        <label dictRef='cml:torsionEnd'>r3</label>"+
		"		</atom>"+
		"		<atom id='r4' elementType='R' x3='-0.4083535460107754' y3='0.334117'"+
		"			z3='26.17901490323389'>"+
		"		</atom>"+
		"		<atom id='a5' elementType='C' x3='-0.9274226661160216' y3='2.2244264'"+
		"			z3='26.61004448473895'>"+
		"	        <label dictRef='cml:torsionEnd'>r4</label>"+
		"		</atom>"+
		"		<atom id='r5' elementType='R' x3='-1.575899600097922' y3='2.0239562'"+
		"			z3='27.245950763184325'>"+
		"		</atom>"+
		"		<atom id='a6' elementType='C' x3='-0.638411881791494' y3='3.5031318000000002'"+
		"			z3='26.308615162238905'>"+
		"	        <label dictRef='cml:torsionEnd'>r5</label>"+
		"		</atom>"+
		"		<atom id='r6' elementType='R' x3='-1.0946942477565857' y3='4.1724984'"+
		"			z3='26.765175530265513'>"+
		"		</atom>"+
		"	</atomArray>"+
		"	<bondArray>"+
		"		<bond atomRefs2='r1 a1' id='r1-a1' order='1'/>"+
		"		<bond atomRefs2='a1 a2' id='a1-a2' order='2'/>"+
		"		<bond atomRefs2='a1 a3' id='a1-a3' order='1'/>"+
		"		<bond atomRefs2='a2 r2' id='a2-r2' order='1'/>"+
		"		<bond atomRefs2='a2 a4' id='a2-a4' order='1'/>"+
		"		<bond atomRefs2='a6 a3' id='a6-a3' order='2'/>"+
		"		<bond atomRefs2='a4 r4' id='a4-r4' order='1'/>"+
		"		<bond atomRefs2='a4 a5' id='a4-a5' order='2'/>"+
		"		<bond atomRefs2='a5 r5' id='a5-r5' order='1'/>"+
		"		<bond atomRefs2='a5 a6' id='a5-a6' order='1'/>"+
		"		<bond atomRefs2='a6 r6' id='a6-r6' order='1'/>"+
		"	</bondArray>"+
		"</molecule>"+
		"	  </fragment>"+
		"	  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"	  <fragment countExpression='*(2)'>"+
		"	    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"	    <fragment>"+
		"<molecule xmlns='http://www.xml-cml.org/schema' id='po'>"+
		"	<atomArray>"+
		"		<atom id='r1' elementType='R' x3='1.580000' y3='0.019000'"+
		"			z3='-0.554000' />"+
		"		<atom id='a2' elementType='C' x3='0.912000' y3='-0.145000'"+
		"			z3='0.699000' />"+
		"		<atom id='a3' elementType='C' x3='-0.599000' y3='-0.016000'"+
		"			z3='0.493000'>"+
		"			<label dictRef='cml:torsionEnd'>r1</label>"+
		"			<label dictRef='cml:torsionEnd'>r2</label>"+
		"		</atom>"+
		"		<atom id='a4' elementType='C' x3='-0.908000' y3='1.315000'"+
		"			z3='-0.194000' />"+
		"		<atom id='a5' elementType='O' x3='-1.061000' y3='-1.093000'"+
		"			z3='-0.326000' />"+
		"		<atom id='a7' elementType='H' x3='1.140000' y3='-1.130000'"+
		"			z3='1.106000' />"+
		"		<atom id='a8' elementType='H' x3='1.250000' y3='0.623000'"+
		"			z3='1.394000' />"+
		"		<atom id='a9' elementType='H' x3='-1.102000' y3='-0.053000'"+
		"			z3='1.459000' />"+
		"		<atom id='a10' elementType='H' x3='-1.984000' y3='1.407000'"+
		"			z3='-0.341000' />"+
		"		<atom id='a11' elementType='H' x3='-0.556000' y3='2.137000'"+
		"			z3='0.430000' />"+
		"		<atom id='a12' elementType='H' x3='-0.405000' y3='1.352000'"+
		"			z3='-1.160000' />"+
		"		<atom id='r2' elementType='R' x3='-2.015000' y3='-0.974000'"+
		"			z3='-0.431000' />"+
		"	</atomArray>"+
		"	<bondArray>"+
		"		<bond atomRefs2='r1 a2' order='1' />"+
		"		<bond atomRefs2='a2 a3' order='1' />"+
		"		<bond atomRefs2='a2 a7' order='1' />"+
		"		<bond atomRefs2='a2 a8' order='1' />"+
		"		<bond atomRefs2='a3 a4' order='1' />"+
		"		<bond atomRefs2='a3 a5' order='1' />"+
		"		<bond atomRefs2='a3 a9' order='1' />"+
		"		<bond atomRefs2='a4 a10' order='1' />"+
		"		<bond atomRefs2='a4 a11' order='1' />"+
		"		<bond atomRefs2='a4 a12' order='1' />"+
		"		<bond atomRefs2='a5 r2' order='1' />"+
		"	</bondArray>"+
		"	<length atomRefs2='a2 a3' id='len23'></length>"+
		"	<angle atomRefs3='a2 a3 a4' id='ang234'></angle>"+
		"	<angle atomRefs3='r1 a2 a3' id='ang123'></angle>"+
		"	<angle atomRefs3='a3 a5 r2' id='ang352'></angle>"+
		"	<torsion atomRefs4='r1 a2 a3 a5' id='tor1'></torsion>"+
		"	<torsion atomRefs4='a2 a3 a5 r2' id='tor2'></torsion>"+
		"</molecule>"+
		""+
		"	    </fragment>"+
		"	  </fragment>"+
		"	  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"	  <fragment>"+
		"<molecule id='acetyl'>"+
		"  <atomArray>"+
		"    <atom id='r1'  elementType='R' x3='6.393952073348176' y3='7.481432789999999'  z3='1.3355287286108608' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0'/>"+
		"    <atom id='a71' elementType='C' x3='7.470354790502688' y3='7.21912165'         z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0'/>"+
		"    <atom id='a72' elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0'>"+
		"      <label dictRef='cml:torsionEnd'>r1</label>"+
		"    </atom>"+
		"    <atom id='a73' elementType='C' x3='8.407334871591223' y3='8.375034459999998'  z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0'/>"+
		"    <atom id='a74' elementType='H' x3='9.290344900009062' y3='8.0103218'          z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0'/>"+
		"    <atom id='a75' elementType='H' x3='8.339122721687978' y3='8.830276'           z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0'/>"+
		"    <atom id='a76' elementType='H' x3='8.236429704800676' y3='8.9953799'          z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0'/>"+
		"  </atomArray>"+
		"  <bondArray>"+
		"    <bond id='r1-a71'  atomRefs2='r1  a71' order='S'/>"+
		"    <bond id='a71-a72' atomRefs2='a71 a72' order='D'/>"+
		"    <bond id='a71-a73' atomRefs2='a71 a73' order='S'/>"+
		"    <bond id='a73-a74' atomRefs2='a73 a74' order='S'/>"+
		"    <bond id='a73-a75' atomRefs2='a73 a75' order='S'/>"+
		"    <bond id='a73-a76' atomRefs2='a73 a76' order='S'/>"+
		"  </bondArray>"+
		"</molecule>"+
		""+
		"	  </fragment>"+
		"	</fragment>"+
		"";
		CMLElement element = null;
		try {
			element = (CMLElement) new CMLBuilder().build(new StringReader(s)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * tests first ten examples
	 */
	@Test
	@Ignore
	// FIXME needs getMoleculeList from catalog
	public void test0_9() {
		boolean debug = false;
		boolean check = true;
		test(debug, check, 0, true);
		test(debug, check, 1, true);
		test(debug, check, 2, true);
		test(debug, check, 3, true);
		test(debug, check, 4, true);
		test(debug, check, 5, true);
		test(debug, check, 6, true);
		test(debug, check, 7, true);
		test(debug, check, 8, true);
		test(debug, check, 9, true);
	}
	/**
	 * tests second ten examples
	 */
	@Test
	public void test10_() {
		boolean debug = false;
		boolean check = true;
		test(debug, check, 10, true);
		test(debug, check, 11, true);
		test(debug, check, 12, true);
		test(debug, check, 13, true);
		test(debug, check, 14, true);
	}

	/**
	 * tests 20_
	 */
	@Test
	public void test20_() {
		boolean debug = false;
		boolean check = true;
		test(debug, check, 20, false);
		test(debug, check, 21, false);
		// stochastic so cannot test without setting seed
		long seed = 100;
//		test(debug, check, 22, false, seed);		//I don't see how this one would have ever worked - dmj30
		test(debug, check, 23,false,seed);
		test(debug, check, 24,false,seed);
	}

	/**
	 * tests 50-57
	 */
	@Test
	public void test50_57() {
		boolean check = true;
		boolean debug = false;
		test(debug, check, 50, true);
		test(debug, check, 51, true);
		test(debug, check, 52, true);
		test(debug, check, 53, true);
		test(debug, check, 54, true);
		test(debug, check, 56, true);
		test(debug, check, 57, true);

	}

	/**
	 * Tests performance, do not run unless you want to wait ~ 45s
	 */
	@Test
	@Ignore
	public void testspeed() {
		boolean check = false;
		boolean debug = true;
		long t0 = System.currentTimeMillis();
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		long t4 = 0;
		long t5 = 0;
		test(check, debug, 1001, true);
		t1 = System.currentTimeMillis();
		test(check, debug, 1002, true);
		t2 = System.currentTimeMillis();
		test(check, debug, 1003, true);
		t3 = System.currentTimeMillis();
		test(check, debug, 1004, true);
		t4 = System.currentTimeMillis();
		test(check, debug, 1005, true);
		t5 = System.currentTimeMillis();
		LOG.debug("Time for 25:" + (t1 - t0));
		LOG.debug("Time for 50:" + (t2 - t1));
		LOG.debug("Time for 100:" + (t3 - t2));
		LOG.debug("Time for 200:" + (t4 - t3));
		LOG.debug("Time for 1:" + (t5 - t4));
	}

	/**
	 * PoLyInfo test builds
	 * 
	 * This isn't really a junit test - it builds the entire polyinfo
	 * database and takes around 8 hours to run
	 * We also seem to be missing some vital components
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	@Ignore
	public void testpolyinfo() throws Exception {
		List<String> filelist = new ArrayList<String>();
		File logfile = new File("log.txt");
		Writer log = new FileWriter(logfile);
		try {
			String line = null;
			InputStream is = Util
					.getInputStreamFromResource("org/xmlcml/cml/tools/examples/molecules/polyinfopolymers/list.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {
				filelist.add(line);
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		}
		for (String fileroot : filelist) {

			// String fileroot = new String("P010001");
			Document doc = null;
			CMLElement root = null;
			try {
				InputStream is = Util
						.getInputStreamFromResource("org/xmlcml/cml/tools/examples/molecules/polyinfopolymers/"
								+ fileroot);
				doc = cmlBuilder.build(is);
			} catch (FileNotFoundException ioe) {
				throw ioe;
			} catch (Exception ioe) {
				throw new RuntimeException(ioe);
			}
			log.write("Reading " + fileroot + '\n');
			root = (CMLElement) doc.getRootElement();

			String basicXML = CMLUtil.getCanonicalString(root);
			CMLFragment fragment = null;
			try {
				fragment = (CMLFragment) new CMLBuilder().parseString(basicXML);
			} catch (Exception e) {
				throw new RuntimeException("should not throw: " + e.getMessage(), e);
			}

			FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
			try {
				File file1 = new File(fileroot + ".basic.xml");
				FileOutputStream fos1 = new FileOutputStream(file1);

				CMLElement generatedElement = fragmentTool.processBasic(resourceManager);

				// fragment.debug(fos1, 1);
				fos1.close();
				File file2 = new File(fileroot + ".intermediate.xml");
				FileOutputStream fos2 = new FileOutputStream(file2);
				if (generatedElement == null) {
					fragmentTool.processIntermediate(resourceManager);
					// fragment.debug(fos2,1);
					try {
						fragmentTool.processExplicit();
					} catch (Exception e) {
						log.write("Error " + e + '\n');
					} finally {
						fos2.close();
					}
				}
			} catch (Exception e) {
				log.write("Exception caught: " + e);
				System.err.println("Error " + e);
				e.printStackTrace();
				continue;
			}
			fragmentTool.pruneRtoH();
			fragmentTool.ElementtoR("U");

			File file = new File("polyinfooutput/" + fileroot + ".pml.xml");
			log.write("Writing to" + file.getAbsolutePath() + '\n');
			log.flush();
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fragment.debug(fos, 1);
				fos.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
		log.close();
	}

}
