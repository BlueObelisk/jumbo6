package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLConstants.Units;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.ReactionComponent;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGGBox;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.cml.test.ReactionFixture;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TstUtils;

/**
 * test reactionTool
 * 
 * @author pmr
 * 
 */
public class ReactionToolTest {
	private static Logger LOG = Logger.getLogger(ReactionToolTest.class);
	ReactionFixture fixture = new ReactionFixture();
	/** */
	public final static String REACTION_EXAMPLES = CMLAssert.TOOLS_EXAMPLES
			+CMLConstants.U_S + "reactions";

	public final static String REACTION_INPUT_PATH = JumboTestConstants.EXAMPLES_INPUT_PATH+"/reactions";
	public final static File REACTION_OUTPUT_DIR = new File(JumboTestUtils.OUTPUT_DIR_NAME, "org/xmlcml/cml/tools/examples/reactions");
	
	ReactionTool xmlReactTool1;

	String balancedS = S_EMPTY + "<reaction id='br' " + CMLConstants.CML_XMLNS + ">"
			+ "  <reactantList id='brl1'>" + "    <reactant id='br1'>"
			+ "     <molecule id='brm1'>" + "      <atomArray>"
			+ "        <atom id='a1' elementType='O'/>"
			+ "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </reactant>"
			+ "    <reactant id='br2'>" + "     <molecule id='brm2'>"
			+ "      <atomArray>" + "        <atom id='a3' elementType='S'/>"
			+ "        <atom id='a4' elementType='Zn'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </reactant>" + "  </reactantList>"
			+ "  <productList id='bpl1'>" + "    <product id='bp1'>"
			+ "     <molecule id='bpm1'>" + "      <atomArray>"
			+ "        <atom id='a3' elementType='S'/>"
			+ "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </product>" + "    <product id='bp2'>"
			+ "     <molecule id='bpm2'>" + "      <atomArray>"
			+ "        <atom id='a1' elementType='O'/>"
			+ "        <atom id='a4' elementType='Zn'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </product>" + "  </productList>"
			+ "</reaction>" + S_EMPTY;

	String unbalancedS = S_EMPTY + "<reaction " + CMLConstants.CML_XMLNS + ">"
			+ "  <reactantList>" + "    <reactant>" + "     <molecule>"
			+ "      <atomArray>" + "        <atom id='a1' elementType='O'/>"
			+ "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </reactant>"
			+ "    <reactant count='2'>" + "     <molecule>"
			+ "      <atomArray>" + "         <atom id='a3' elementType='Cl'/>"
			+ "         <atom id='a4' elementType='H'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </reactant>" + "  </reactantList>"
			+ "  <productList>" + "    <product>" + "     <molecule>"
			+ "      <atomArray>" + "        <atom id='a1' elementType='Cl'/>"
			+ "        <atom id='a3' elementType='Cl'/>"
			+ "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
			+ "     </molecule>" + "    </product>" + "  </productList>"
			+ "</reaction>" + S_EMPTY;

	CMLReaction balancedR = null;

	CMLReaction unbalancedR = null;

	private CMLReaction reaction1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		xmlReactTool1 = ReactionTool.getOrCreateTool(fixture.xmlReact1);

		balancedR = (CMLReaction)TstUtils.parseValidString(balancedS);
		unbalancedR = (CMLReaction)TstUtils.parseValidString(unbalancedS);

		InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/tools/reaction1.xml");
		reaction1 = (CMLReaction) new CMLBuilder().build(is).getRootElement();
		if (!REACTION_OUTPUT_DIR.exists()) {
			REACTION_OUTPUT_DIR.mkdirs();
		}
	}

	/** */
	@Test
	public void testCalculateDifferenceFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(balancedR)
				.calculateDifferenceFormula();
		CMLFormula expected = new CMLFormula();
		CMLAssert.assertEqualsConcise("empty", expected, formula, EPS);
		formula = ReactionTool.getOrCreateTool(unbalancedR)
				.calculateDifferenceFormula();
		expected = new CMLFormula();
		expected.add(AS.O.value, 1.);
		expected.add(AS.H.value, 2.);
		CMLAssert.assertEqualsConcise("non-empty", expected, formula, EPS);
	}

	/** */
	@Test
	public void testCreateAggregateProductFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(unbalancedR)
				.createAggregateProductFormula();
		// order matters
		CMLFormula expected = new CMLFormula();
		expected.add(AS.Cl.value, 2.);
		expected.add("Mg", 1.);
		CMLAssert.assertEqualsConcise("non-empty", expected, formula, EPS);
	}

	/** */
	@Test
	public void testCreateAggregateReactantFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(unbalancedR)
				.createAggregateReactantFormula();
		// order matters
		CMLFormula expected = new CMLFormula();
		expected.add(AS.H.value, 2.);
		expected.add(AS.Cl.value, 2.);
		expected.add("Mg", 1.);
		expected.add(AS.O.value, 1.);
		CMLAssert.assertEqualsConcise("non-empty", expected, formula, EPS);
	}

	/** */
	@Test
	public void testGetFormula() {
		CMLReactant reactant = ReactionTool.getOrCreateTool(unbalancedR).getReactant(0);
		CMLFormula formula = ReactionTool.getFormula(reactant);
		CMLFormula expected = new CMLFormula();
		expected.add("Mg", 1.0);
		expected.add(AS.O.value, 1.0);
		CMLAssert.assertEqualsConcise("formula", expected, formula, EPS);
	}

	/** */
	@Test
	public void testGetMolecules() {
		List<CMLMolecule> molecules = reaction1.getMolecules(Component.REACTANT);
		Assert.assertEquals("descendant reactant molecules", 2, molecules.size());
	}

	/** */
	@Test
	public void testGetAtoms() {
		List<CMLAtom> atoms = reaction1.getAtoms(Component.REACTANT);
		Assert.assertEquals("descendant reactant atoms", 12, atoms.size());
	}

	/** */
	@Test
	public void testGetBonds() {
		List<CMLBond> bonds = reaction1.getBonds(Component.REACTANT);
		Assert.assertEquals("descendant reactant bonds", 10, bonds.size());
	}

	// ===================================================
	/** 
	 */
	@Test
	public final void testGetMolecules1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLMolecule> molecules = reactionTool
				.getMolecules(Component.REACTANTLIST);
		Assert.assertEquals("reactant molecules", 2, molecules.size());
		molecules = reactionTool.getMolecules(Component.PRODUCTLIST);
		Assert.assertEquals("product molecules", 2, molecules.size());
	}

	/** 
	 */
	@Test
	public final void testGetAtoms1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLAtom> atoms = reactionTool.getAtoms(Component.REACTANTLIST);
		Assert.assertEquals("reactant atoms", 4, atoms.size());
		atoms = reactionTool.getAtoms(Component.PRODUCTLIST);
		Assert.assertEquals("reactant atoms", 4, atoms.size());
	}

	/** 
	 */
	@Test
	public final void testGetBonds1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLBond> bonds = reactionTool.getBonds(Component.REACTANTLIST);
		Assert.assertEquals("reactant bonds", 0, bonds.size());
		bonds = reactionTool.getBonds(Component.PRODUCTLIST);
		Assert.assertEquals("reactant bonds", 0, bonds.size());
	}

	/**
	 * A complex reaction with several reactants and products
	 */
	@Test
	public final void testComplexReaction() {
		Document doc = null;
		try {
			InputStream in = Util.getInputStreamFromResource(REACTION_EXAMPLES
					+CMLConstants.U_S + "reaction1.xml");
			doc = new CMLBuilder().build(in);
		} catch (Throwable e) {
			System.err.println("SKIPPED" + e);
		}
		Assert.assertNotNull("reaction not null", doc);
		// molecules are listed in a moleculeList and accessed by molecule@ref
		// first check the molecules are OK
		CMLMoleculeList moleculeList = (CMLMoleculeList) doc.query(
				".//cml:moleculeList", CMLConstants.CML_XPATH).get(0);
		CMLReaction reaction1 = (CMLReaction) doc.query(".//cml:reaction",
				CML_XPATH).get(0);
		// get MWt for each molecule
		CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
		Assert.assertEquals("molecules", 6, molecules.size());

		// now check their MWts
		double[] mwt = new double[] { 277.40178, 101.19, 215.87126, 396.361,
				102.198, 79.904 };
		for (int i = 0; i < molecules.size(); i++) {
			CMLMolecule molecule = molecules.get(i);
			CMLFormula formula = molecule.getFormulaElements().get(0);
			double d = formula.getCalculatedMolecularMass();
			// check MWts
			Assert.assertEquals("mw ", mwt[i], d, 0.001);
		}

		// check the reactants each of which contains molecule@ref
		CMLReactantList reactantList = reaction1.getReactantList();
		CMLElements<CMLReactant> reactants = reactantList.getReactantElements();
		Assert.assertEquals("reactants ", 3, reactants.size());
		calculateMolarAmounts(doc, reactants, new double[] { .00306, .00503,
				.00506 });

		// check the products each of which contains molecule@ref
		CMLProductList productList = reaction1.getProductList();
		CMLElements<CMLProduct> products = productList.getProductElements();
		Assert.assertEquals("products ", 3, products.size());
		calculateMolarAmounts(doc, products, new double[] { .00131, Double.NaN,
				Double.NaN });
		CMLFormula difference = ReactionTool.getOrCreateTool(reaction1)
				.calculateDifferenceFormula();
		CMLFormula expected = new CMLFormula();
		CMLAssert.assertEqualsConcise("non-empty", expected, difference, EPS);

	}

	/**
	 * @param doc
	 * @param components
	 */
	private static void calculateMolarAmounts(Document doc,
			CMLElements<?> components, double[] mol) {
		for (int i = 0; i < components.size(); i++) {
			ReactionComponent reactant = (ReactionComponent) components.get(i);
			// get reference to molecule
			CMLMolecule moleculeRef = reactant.getMolecules().get(0);
			String idRef = moleculeRef.getRefAttribute().getValue();
			// XPath location of molecule in list
			Nodes moleculeNodes = doc.query(
					".//cml:moleculeList/cml:molecule[@id='" + idRef + "']",
					CML_XPATH);
			Assert.assertEquals("moleculeRefs ", 1, moleculeNodes.size());
			// get referenced molecule
			CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(0);
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			// get mass in grams
			Nodes massAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.GRAM + "']", CMLConstants.CML_XPATH);
			CMLAmount massAmount = (massAmounts.size() == 0) ? null
					: (CMLAmount) massAmounts.get(0);
			// or volume in mL
			Nodes volAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.ML + "']", CMLConstants.CML_XPATH);
			CMLAmount volAmount = (volAmounts.size() == 0) ? null
					: (CMLAmount) volAmounts.get(0);
			// and get the molar amount (mmol)
			Nodes molarAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.MMOL + "']", CMLConstants.CML_XPATH);
			CMLAmount molarAmount = (molarAmounts.size() == 0) ? null
					: (CMLAmount) molarAmounts.get(0);
			// calculate molarAmount from either mass or volume and check it
			// agrees with molarAmount
			CMLAmount calcMolarAmount = null;
			if (massAmount != null) {
				calcMolarAmount = moleculeTool.getMolarAmount(massAmount);
			} else if (volAmount != null) {
				calcMolarAmount = moleculeTool
						.getMolarAmountFromVolume(volAmount);
			}
			if (calcMolarAmount == null) {
				if (!Double.isNaN(mol[i])) {
					Assert.fail("inconsistent null amount: " + mol[i]);
				}
			} else {
				Assert.assertEquals("calc molar", mol[i], calcMolarAmount
						.getXMLContent(), 0.001);
				if (molarAmount != null) {
					// Util.printmolarAmount.getXMLContent());
				}
			}
			// LOG.debug("("+molecule.getTitle()+")");
		}
	}

	/**
	 * @param doc
	 * @param components
	 */
	private static void exploreMolarAmounts(Document doc,
			CMLElements<?> components) {
		for (int i = 0; i < components.size(); i++) {
			ReactionComponent reactant = (ReactionComponent) components.get(i);
			// get reference to molecule
			CMLMolecule moleculeRef = reactant.getMolecules().get(0);
			String idRef = moleculeRef.getRefAttribute().getValue();
			// XPath location of molecule in list
			Nodes moleculeNodes = doc.query(
					".//cml:moleculeList/cml:molecule[@id='" + idRef + "']",
					CML_XPATH);
			if (moleculeNodes.size() == 0) {
				throw new RuntimeException("Cannot find molecule ref: " + idRef);
			}
			// get referenced molecule
			CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(0);
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			// get mass in grams
			Nodes massAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.GRAM + "']", CMLConstants.CML_XPATH);
			CMLAmount massAmount = (massAmounts.size() == 0) ? null
					: (CMLAmount) massAmounts.get(0);
			// or volume in mL
			Nodes volAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.ML + "']", CMLConstants.CML_XPATH);
			CMLAmount volAmount = (volAmounts.size() == 0) ? null
					: (CMLAmount) volAmounts.get(0);
			// and get the molar amount (mmol)
			Nodes molarAmounts = ((CMLElement) reactant).query(
					".//cml:amount[@units='" + Units.MMOL + "']", CMLConstants.CML_XPATH);
			CMLAmount molarAmount = (molarAmounts.size() == 0) ? null
					: (CMLAmount) molarAmounts.get(0);
			// calculate molarAmount from either mass or volume and check it
			// agrees with molarAmount
			CMLAmount calcMolarAmount = null;
			if (massAmount != null) {
				calcMolarAmount = moleculeTool.getMolarAmount(massAmount);
			} else if (volAmount != null) {
				calcMolarAmount = moleculeTool
						.getMolarAmountFromVolume(volAmount);
			}
			if (calcMolarAmount == null) {
			} else {
				if (molarAmount != null) {
					LOG.debug(molecule.getTitle() + ": "
							+ molarAmount.getXMLContent() + ": "
							+ calcMolarAmount.getXMLContent());
				}
			}
		}
	}

	/**
	 * A complex reaction with several reactants and products
	 */
	static void validate(String[] args) throws Exception {
		if (args.length < 2) {
			LOG.debug("SVG infile");
		} else {
			String infile = args[1];
			InputStream is = new FileInputStream(infile);
			Document doc = new CMLBuilder().build(is);
			doc = CMLBuilder.ensureCML(doc);
			// molecules are listed in a moleculeList and accessed by
			// molecule@ref
			// first check the molecules are OK
			CMLMoleculeList moleculeList = (CMLMoleculeList) doc.query(
					".//cml:moleculeList", CMLConstants.CML_XPATH).get(0);
			CMLReaction reaction1 = (CMLReaction) doc.query(".//cml:reaction",
					CML_XPATH).get(0);
			// get MWt for each molecule
			CMLElements<CMLMolecule> molecules = moleculeList
					.getMoleculeElements();
			// now check their MWts
			for (int i = 0; i < molecules.size(); i++) {
				CMLMolecule molecule = molecules.get(i);
				CMLProperty prop = MoleculeTool.getOrCreateTool(molecule)
						.getMolarMass();

				LOG.debug("Mol(" + (i + 1) + "): "
						+ molecule.getTitle() + " molarMass: "
						+ ((prop == null) ? "?" : prop.getDouble()));
			}

			// check the reactants each of which contains molecule@ref
			CMLReactantList reactantList = reaction1.getReactantList();
			CMLElements<CMLReactant> reactants = reactantList
					.getReactantElements();
			exploreMolarAmounts(doc, reactants);

			// check the products each of which contains molecule@ref
			CMLProductList productList = reaction1.getProductList();
			CMLElements<CMLProduct> products = productList.getProductElements();
			exploreMolarAmounts(doc, products);
			@SuppressWarnings("unused")
			CMLFormula difference = ReactionTool.getOrCreateTool(reaction1)
					.calculateDifferenceFormula();
			@SuppressWarnings("unused")
			CMLFormula expected = new CMLFormula();
		}

	}
	
	@Test
	public void testMapReactantsToProductsUsingAtomSets() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("CCC(=O)O");
		reactionTool.addReactant("OCC");
		reactionTool.addProduct("CCC(=O)OCC");
		reactionTool.addProduct("O");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testMapReactantsToProductsUsingAtomSets", cmlMap);
	}

	@Test
	@Ignore
	public void testMapReactantsToProductsUsingAtomSets1() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("CCC(=O)O");
		reactionTool.addReactant("NCC");
		reactionTool.addProduct("CCC(=O)NCC");
		reactionTool.addProduct("O");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testMapReactantsToProductsUsingAtomSets1", cmlMap);
		Element ref = JumboTestUtils.parseValidString(
"<map toType='atom' fromType='atom' xmlns='http://www.xml-cml.org/schema'>" +
"<link title='unique treeString N' fromSet='m2_a1' toSet='m1_a5'/>" +
"<link title='unique treeString C' fromSet='m2_a2' toSet='m1_a6'/>" +
"<link title='unique treeString C' fromSet='m1_a1' toSet='m1_a1'/>" +
"<link title='unique treeString C' fromSet='m1_a2' toSet='m1_a2'/>" +
"<link title='unique treeString C' fromSet='m2_a3' toSet='m1_a7'/>" +
"<link title='balanced commonAtomTree C' fromSet='m1_a3' toSet='m1_a3'/>" +
"<link title='balanced commonAtomTree H' fromSet='m1_a1_h1 m1_a1_h2 m1_a1_h3' toSet='m1_a1_h1 m1_a1_h2 m1_a1_h3'/>" +
"<link title='balanced commonAtomTree H' fromSet='m1_a2_h1 m1_a2_h2' toSet='m1_a2_h1 m1_a2_h2'/>" +
"<link title='balanced commonAtomTree H' fromSet='m2_a2_h1 m2_a2_h2' toSet='m1_a6_h1 m1_a6_h2'/>" +
"<link title='balanced commonAtomTree H' fromSet='m2_a3_h1 m2_a3_h2 m2_a3_h3' toSet='m1_a7_h1 m1_a7_h2 m1_a7_h3'/>" +
"<link title='unbalanced commonAtomTree H' fromSet='m2_a1_h2' toSet='' />" +
"<link title='unbalanced commonAtomTree H' fromSet='' toSet='m2_a1_h2'/>" +
"<link title='balanced commonAtomTree O' fromSet='m1_a4 m1_a5' toSet='m1_a4 m2_a1'/>" +
"<link title='unbalanced commonAtomTree O' fromSet='m1_a5' toSet=''/>" +
"<link title='de-orphan' fromSet='m2_a1_h1' toSet='m1_a5_h1'/>" +
"<link title='de-orphan' fromSet='m1_a5_h1' toSet='m2_a1_h1'/>" +
"<link title='de-orphan' fromSet='m1_a4' toSet='m1_a4'/>" +
"</map>");
		JumboTestUtils.assertEqualsCanonically("test", ref, cmlMap, true);
	}

	@Test
	public void testMapReactantsToProductsUsingAtomSets2() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("C1=CC=CC1");
		reactionTool.addReactant("C=C=O");
		reactionTool.addProduct("C12C=CCC1CC(=O)2");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testMapReactantsToProductsUsingAtomSets2", cmlMap);
	}

	@Test
	public void testMapReactantsToProductsUsingAtomSets2a() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("C1=CC=C(Cl)C1");
		reactionTool.addReactant("C=C=O");
		reactionTool.addProduct("C12C=C(Cl)CC1CC(=O)2");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testMapReactantsToProductsUsingAtomSets2a", cmlMap);
	}

	@Test
	public void testMapReactantsToProductsUsingAtomSets3() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("c1ccccc1C(=O)Cl");
		reactionTool.addReactant("NCC");
		reactionTool.addProduct("c1ccccc1C(=O)NCC");
		reactionTool.addProduct("Cl");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testMapReactantsToProductsUsingAtomSets3", cmlMap);
	}

	@Test
	public void testPolyinfo1() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("CCCCOC=CC");
		reactionTool.addProduct("CCCCO[CH][CH]C");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testPolyinfo1", cmlMap);
	}

	@Test
	public void testPolyinfo2() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("NCC(=O)O");
		reactionTool.addProduct("[NH]C[C](=O)");
		reactionTool.addProduct("O");
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Assert.assertNotNull("testPolyinfo2", cmlMap);
	}
	
	@Test
	public void testDrawReaction() {
		ReactionTool reactionTool = makeReaction();
		reactionTool.getReactionDisplay().setScale(0.5);
		reactionTool.getReactionDisplay().setId("dummyId");
		SVGG svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR,"reaction.svg"));
	}

	@Test
	public void testDrawReactants0() {
		ReactionTool reactionTool = makeReaction0();
		SVGG svgg = reactionTool.drawReactants();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "reactants0.svg"));
	}

	@Test
	public void testDrawReactants() {
		ReactionTool reactionTool = makeReaction();
		SVGG svgg = reactionTool.drawReactants();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "reactants.svg"));
	}

	@Test
	public void testDraw2Reactants() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("CC(=O)O");
		reactionTool.addReactant("C(Cl)C(=O)O");
		SVGG svgg = reactionTool.drawReactants();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "manyReactants2.svg"));
	}

	@Test
	public void testDraw3Reactants() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("CC(=O)O");
		reactionTool.addReactant("C(Cl)C(=O)O");
		reactionTool.addReactant("ClC(Cl)C(=O)O");
		SVGGBox svgg = reactionTool.drawReactants();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "manyReactants3.svg"));
	}

	private ReactionTool makeReaction0() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("c1ccccc1C(=O)OC(=O)C");
		reactionTool.addProduct("c1ccccc1C(=O)NCCc1ccccc1");
		return reactionTool;
	}

	private ReactionTool makeReaction() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant("c1ccccc1C(=O)OC(=O)C");
		reactionTool.addReactant("c1ccccc1CCN");
		reactionTool.addProduct("c1ccccc1C(=O)NCCc1ccccc1");
		reactionTool.addProduct("CC(=O)O");
		return reactionTool;
	}

	@Test
	public void testDrawProducts0() {
		ReactionTool reactionTool = makeReaction0();
		reactionTool.getReactionDisplay().setScale(0.7);
		SVGG svgg = reactionTool.drawProducts();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "products0.svg"));
	}

	@Test
	public void testDrawProducts() {
		ReactionTool reactionTool = makeReaction();
		reactionTool.getReactionDisplay().setScale(0.3);
//		reactionTool.getReactionDisplay().setProductOrientation(Orientation.VERTICAL);
		SVGG svgg = reactionTool.drawProducts();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "products.svg"));
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void testSVG(String[] args) throws Exception {
		if (args.length < 3) {
			LOG.debug("SVG infile outfile");
		} else {
			String infile = args[1];
			String outfile = args[2];
			InputStream is = Util.getInputStreamFromResource(infile);
			MoleculeDisplayList graphicsManager = new MoleculeDisplayList(
					outfile);
			CMLReaction reaction = (CMLReaction) new CMLBuilder().build(is)
					.getRootElement();

			graphicsManager.setAndProcess(ReactionTool
					.getOrCreateTool(reaction));
			graphicsManager.createOrDisplayGraphics();
		}
	}
	
	@Test
	public void testUgi() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.getReactionDisplay().setScale(0.5);
		reactionTool.addReactant("C1CCCCC1C(=O)O");
		reactionTool.addReactant("CC(C)N");
		reactionTool.addReactant("Cc1ccc(cc1)C(=O)C(F)(F)(F)");
		reactionTool.addReactant("CCCC[N+]#[C-]");
		reactionTool.addProduct("C1CCCCC1C(=O)N(CC(C)C)C(c1ccc(C)cc1(C(F)(F)(F))C(=O)N(CCCC)");
		SVGGBox svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "ugi.svg"));
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		cmlMap.debug("UGI");
		Assert.assertNotNull("testPolyinfo1", cmlMap);
	}

	@Test
	public void testWittigMap() {
		ReactionTool reactionTool = createWittigReaction();
		SVGGBox svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "wittig.svg"));
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		Element refMap = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/wittigmap.xml");
		// too sensitive to environment
//		JumboTestUtils.assertEqualsIncludingFloat("amide and amount", refMap, cmlMap, true, EPS);
		Assert.assertNotNull(refMap);
	}

	private ReactionTool createWittigReaction() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.getReactionDisplay().setScale(0.5);
		reactionTool.addReactant("CCC=P(c1ccccc1)(c1ccccc1)(c1ccccc1)");
		reactionTool.addReactant("c1ccccc1C(=O)C(C)C");
		reactionTool.addProduct("O=P(c1ccccc1)(c1ccccc1)(c1ccccc1)");
		reactionTool.addProduct("c1ccccc1C(=CCC)C(C)C");
		return reactionTool;
	}
	
	@Test
	public void testWittigMapPatterns() {
		ReactionTool reactionTool = createWittigReaction();
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		SVGGBox svggReactants = reactionTool.drawReactants();
		SVGGBox svggProducts = reactionTool.drawProducts();
		reactionTool.addPatternsToLinkedAtoms(svggReactants, svggProducts, cmlMap);
		SVGGBox svgg = new SVGGBox();
		svgg.addSVGG(svggReactants);
		svgg.addSVGG(svggProducts);
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "wittigpattern.svg"));
		Assert.assertNotNull(cmlMap);
	}
	
	@Test
	public void testAmide() {
		ReactionTool reactionTool = createAmideReactionAndEnsureIds();
		SVGGBox svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "amide.svg"));
		CMLMap cmlMap = reactionTool.mapReactantsToProductsUsingAtomSets();
		cmlMap.debug("AMIDE");
		Assert.assertNotNull("testPolyinfo1", cmlMap);
	}

	private ReactionTool createAmideReactionAndEnsureIds() {
		String[] reactants = {"C1CCCCC1C(=O)OC(=O)C", "CC(C)N"};
		double[] reactantAmounts = {0.002, 0.0015};
		String[] products = {"C1CCCCC1C(=O)NC(C)C", "CC(=O)O"};
		double[] productAmounts = {Double.NaN, Double.NaN};
		return createReactionAddAmountsEnsureIds(reactants, reactantAmounts, products, productAmounts);
	}

	private ReactionTool createReactionAddAmountsEnsureIds(
			String[] reactantStrings, double[] reactantAmounts, String[] productStrings, double[] productAmounts
			) {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		for (int i = 0; i < reactantStrings.length; i++) {
			CMLReactant reactant = reactionTool.addReactant(reactantStrings[i]);
			if (reactantAmounts != null && !Double.isNaN(reactantAmounts[i])) {
				reactant.addAmount(AmountTool.createMolarAmount(reactantAmounts[i]));
			}
		}
		for (int i = 0; i < productStrings.length; i++) {
			CMLProduct product = reactionTool.addProduct(productStrings[i]);
			if (productAmounts != null && !Double.isNaN(productAmounts[i])) {
				product.addAmount(AmountTool.createMilliMolarAmount(productAmounts[i]));
			}
		}
		reactionTool.ensureIds();
		return reactionTool;
	}

	@Test
	@Ignore ("spurious NoSuchMethod")
	public void testEnsureIds() {
		ReactionTool reactionTool = createAmideReactionAndEnsureIds();
		Element ref = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/examples/reactions/reactionAmount.xml");
		JumboTestUtils.assertEqualsIncludingFloat("amide and amount", ref, reactionTool.getReaction(), true, EPS);
	}

	@Test
	public void testFindLimitingReagent() {
		ReactionTool reactionTool = createAmideReactionAndEnsureIds();
		CMLReactant reactant = reactionTool.findLimitingReactant();
		Assert.assertNotNull(reactant);
		Assert.assertEquals("limiting reactant", "reactant1", reactant.getId());
	}

	@Test
	public void testFindLimitingMolesPerCount() {
		ReactionTool reactionTool = createAmideReactionAndEnsureIds();
		CMLReactant reactant = reactionTool.findLimitingReactant();
		ReactantTool reactantTool = ReactantTool.getOrCreateTool(reactant);
		double molesPerCount = reactantTool.getMolesPerCount();
		Assert.assertEquals("limiting molesPerCount", 0.0015, molesPerCount);
	}
	
	@Test
	public void testPatent1() 
	{
		CMLReaction reaction = (CMLReaction) JumboTestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/reactions/example1.cml").getChildElements().get(0);
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		CMLReactant reactant = reactionTool.findLimitingReactant();
		ReactantTool reactantTool = ReactantTool.getOrCreateTool(reactant);
		double molesPerCount = reactantTool.getMolesPerCount();
		Assert.assertEquals("limiting molesPerCount", 1.02, molesPerCount);
	}
	@Test
	public void testPatent1a() 
	{
		CMLReaction reaction = (CMLReaction) JumboTestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/reactions/example1.cml").getChildElements().get(0);
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		SVGGBox svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "exampleReaction1.svg"));
	}

	@Test
	public void testPatent1aa() 
	{
		CMLReaction reaction = (CMLReaction) JumboTestUtils.parseValidFile(
				"org/xmlcml/cml/tools/examples/reactions/example1.cml").getChildElements().get(0);
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		SVGGBox svgg = reactionTool.drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "exampleReaction1.svg"));
	}

	@Test
	public void testSpectator1() 
	{
		CMLReaction reaction = createSpectatorReaction();
		CMLReaction reactionRef = (CMLReaction) JumboTestUtils.parseValidFile(
			"org/xmlcml/cml/tools/examples/reactions/spectator1.cml");
		JumboTestUtils.assertEqualsCanonically("spectator1", reactionRef, reaction, true);
	}

	private CMLReaction createSpectatorReaction() {
		CMLReaction reaction = new CMLReaction();
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
		reactionTool.addReactant(new CMLMolecule());
		reactionTool.addReactant("CCNO");
		reactionTool.addSpectator("O");
		reactionTool.addSpectator(new CMLMolecule());
		reactionTool.addProduct(new CMLMolecule());
		reactionTool.addProduct("c1ccccc1N");
		return reaction;
	}

	@Test
	public void testSpectator() 
	{
		CMLReaction reaction = createSpectatorReaction();
		SVGGBox svgg = ReactionTool.getOrCreateTool(reaction).drawSVG();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(REACTION_OUTPUT_DIR, "spectatorReaction.svg"));
	}

	static void usage() {
		Util.println("java org.xmlcml.cml.tools.ReactionToolTest <options>");
		Util.println("... options ...");
		Util.println("-SVG inputfile outputfile <options>");
		Util.println("-VALIDATE inputfile");
	}

	/**
	 * main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			Util.println("Args is 0");
			usage();
		} else {
			if (args[0].equalsIgnoreCase("-SVG")) {
				testSVG(args);
			} else if (args[0].equalsIgnoreCase("-VALIDATE")) {
				validate(args);
			}
		}
	}

}
