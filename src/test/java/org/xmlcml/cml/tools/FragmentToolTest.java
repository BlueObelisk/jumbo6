package org.xmlcml.cml.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.AbstractTest;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.interfacex.IndexableByIdList;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;

// updated
/**
 * @author pm286
 * 
 */
public class FragmentToolTest extends AbstractTest {

	private CatalogManager catalogManager = null;

	private Catalog moleculeCatalog = null;

	private CMLBuilder cmlBuilder;

	/**
	 */
	@Before
	public void setUp() {
		catalogManager = CatalogManager.getTopCatalogManager();
		moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
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
		fragmentTool.processBasic(moleculeCatalog);

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
		 * (CMLMap) parseValidString(mapS); assertEqualsCanonically("map", mapE,
		 * cmlMap, true);
		 */// Duplicate Test from Test Catalog. Removes ability to update
		// catalog without changing hardcoded tests so commenting out. nwe23
		IndexableByIdList moleculeList = moleculeCatalog.getIndexableList(namespace, IndexableByIdList.Type.MOLECULE_LIST);
		Assert.assertNotNull("moleculeList", moleculeList);
		Assert.assertTrue("moleculeList", 30 <= moleculeList.getIndex().size());

		fragmentTool.processIntermediate(moleculeCatalog);

		CMLElement explicit = readElement0("molE");
		AbstractTest.assertEqualsCanonically("fragment", explicit, fragment, true);
	}

	/**
	 * no longer relevant
	 */
	@Test
	public void testProcessRecursively() {
		String[] ATOMREFS2 = new String[] { "r2", "r1" };
		String[] MOLREFS2 = new String[] { CMLJoin.PREVIOUS_S, CMLJoin.NEXT_S };
		CMLFragment fragment = new CMLFragment();
		@SuppressWarnings("unused")
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
		assertEqualsCanonically("fragment", fragmentE, fragment, true);

		// maybe defer this...
		fragmentTool.basic_processRecursively();
		fragment = fragmentTool.getFragment();

		// NOT REPEATABLE
		// CMLFragment fragment1E = readFragment0("recurse1");
		// FIXME - this fails although the files seem to be identical
		// assertEqualsCanonically("fragment1", fragment1E, fragment, true);
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
		assertEqualsCanonically("molecule", moleculeE, molecule, true);
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
		assertEqualsCanonically("fragment", fragmentE, fragment, true);
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
				+ CMLScalar.NS, X_CML);
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
	 * @throws CMLRuntimeException
	 */
	private Document readDocument(String fileroot) throws FileNotFoundException, CMLRuntimeException {
		Document doc = null;
		try {
			InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/tools/examples/molecules/fragments/"
					+ fileroot + ".xml");
			doc = cmlBuilder.build(is);
		} catch (FileNotFoundException ioe) {
			throw ioe;
		} catch (Exception ioe) {
			throw new CMLRuntimeException(ioe);
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
		if (!(fragment instanceof CMLFragment)) {
			throw new CMLRuntimeException("NOT A FRAG");
		}
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool((CMLFragment) fragment);
		fragmentTool.setSeed(seed);
		CMLElement generatedElement = fragmentTool.processBasic(moleculeCatalog);
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
			AbstractTest.assertEqualsCanonically(title, intermediate, fragment, true);
		}
		if (debug) {
			fragment.debug(title);
		}
		// intermediate -> explicit
		fragmentTool.processIntermediate(moleculeCatalog);
		title = "explicit" + serial;
		if (debug) {
			fragment.debug(title);
		}
		if (explicit == null) {
			dump(fragment, "mol" + serial + "E");
		} else if (check) {
			AbstractTest.assertEqualsCanonically(title, explicit, fragment, true);
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
			AbstractTest.assertEqualsCanonically(title, complete, fragment, true);
		}
	}

	private CMLFragment processFragment(CMLFragment fragment) {
		FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
		fragmentTool.processBasic(moleculeCatalog);
		fragmentTool.processIntermediate(moleculeCatalog);
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
			System.out.println("WROTE: " + outfile);
		} catch (Exception e) {
			throw new CMLRuntimeException(e);
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
				AbstractTest.assertEqualsCanonically(rootName, complete0, fragment, true);
			}
		}
		String title = "complete" + serial;
		if (debug) {
			fragmentList.debug(title);
		}
		if (complete == null) {
			dump(fragmentList, "mol" + serial + "C");
		} else if (check) {
			AbstractTest.assertEqualsCanonically(title, complete, fragmentList, true);
		}
	}

	/**
	 * intermediate result in processing Markush
	 */
	@Test
	public void testGeneratedMarkush() {

		CMLElement generatedFragment = readElement0("markush");
		FragmentTool generatedFragmentTool = FragmentTool.getOrCreateTool((CMLFragment) generatedFragment);
		generatedFragmentTool.processAll(moleculeCatalog);
		CMLElement generatedE = readElement0("markushE");
		assertEqualsCanonically("generated", generatedE, generatedFragment, true);
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
		System.out.println("Checking bumps: " + molname);
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
					double dist = atomi.getDistanceTo(atomj);
					System.out.println("BUMP " + atomi.getId() + "-" + atomj.getId() + ": " + dist);
				}
			}
		}
	}

	/**
	 * tests first ten examples
	 */
	@Test
	// @Ignore
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
	// @Ignore
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
	// @Ignore
	public void test20_() {
		boolean debug = false;
		boolean check = true;
		test(debug, check, 20, false);
		test(debug, check, 21, false);
		// stochastic so cannot test without setting seed
		long seed = 100;
		test(debug, check, 22, false, seed);
		test(debug, check, 23,false,seed);
		test(debug, check, 24,false,seed);
	}

	/**
	 * tests 50-57
	 */
	@Test
	// @Ignore
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
		System.out.println("Time for 25:" + (t1 - t0));
		System.out.println("Time for 50:" + (t2 - t1));
		System.out.println("Time for 100:" + (t3 - t2));
		System.out.println("Time for 200:" + (t4 - t3));
		System.out.println("Time for 1:" + (t5 - t4));
	}

	/**
	 * PoLyInfo test builds
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
				throw new CMLRuntimeException(ioe);
			}
			log.write("Reading " + fileroot + '\n');
			root = (CMLElement) doc.getRootElement();

			String basicXML = CMLUtil.getCanonicalString(root);
			CMLFragment fragment = null;
			try {
				fragment = (CMLFragment) new CMLBuilder().parseString(basicXML);
			} catch (Exception e) {
				throw new CMLRuntimeException("should not throw: " + e.getMessage(), e);
			}

			FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment);
			try {
				File file1 = new File(fileroot + ".basic.xml");
				FileOutputStream fos1 = new FileOutputStream(file1);

				CMLElement generatedElement = fragmentTool.processBasic(moleculeCatalog);

				// fragment.debug(fos1, 1);
				fos1.close();
				File file2 = new File(fileroot + ".intermediate.xml");
				FileOutputStream fos2 = new FileOutputStream(file2);
				if (generatedElement == null) {
					fragmentTool.processIntermediate(moleculeCatalog);
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
