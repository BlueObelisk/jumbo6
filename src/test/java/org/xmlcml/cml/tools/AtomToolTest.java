package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.cml.test.CMLAssert.assertEquals;
import static org.xmlcml.cml.test.CMLAssert.assertEqualsCanonically;
import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TestUtils;

/**
 * tests atomTool.
 * 
 * @author pmr
 * 
 */
public class AtomToolTest {
	private static Logger LOG = Logger.getLogger(AtomToolTest.class);
	
	Document xmlDocument = null;
	// build xom
	protected final int NATOM = 5;
	protected final int NBOND = 5;
	protected String[] elementTypes = { AS.C.value, AS.N.value, AS.O.value,
			AS.S.value, AS.B.value };
	protected int[] hCounts = { 2, 1, 0, 0, 1 };
	protected CMLMolecule xomMolecule;
	protected CMLAtom[] xomAtom;
	protected CMLBond[] xomBond;

	//
	// read into xom; not a stable molecule... (CH3)[N+](S-)(O)(F)
	// 2 1 3 4 5
	protected String xmlMolS = S_EMPTY + "  <molecule id='m1'  " + CML_XMLNS
			+ ">" + "    <atomArray>" + "      <atom id='a1' "
			+ "        elementType='N'" + "        hydrogenCount='0'"
			+ "        formalCharge='1'" + "        spinMultiplicity='1'"
			+ "        occupancy='1.0'" + "        x2='0.' y2='0.'"
			+ "        x3='0.' y3='0.' z3='0.'"
			+ "        xFract='0.1' yFract='0.2' zFract='0.3'" + "      />"
			+ "      <atom id='a2' " + "        elementType='C'"
			+ "        hydrogenCount='3'" + "        x2='1.' y2='1.'"
			+ "        x3='1.' y3='1.' z3='1.'" + "      />"
			+ "      <atom id='a3' " + "        elementType='S'"
			+ "        hydrogenCount='0'" + "        formalCharge='-1'"
			+ "        x2='1.' y2='-1.'" + "        x3='1.' y3='-1.' z3='-1.'"
			+ "      />" + "      <atom id='a4' " + "        elementType='O'"
			+ "        x2='-1.' y2='-1.'" + "        x3='-1.' y3='-1.' z3='1.'"
			+ "      />" + "      <atom id='a5' " + "        elementType='F'"
			+ "        x2='-1.' y2='1.'" + "        x3='-1.' y3='1.' z3='-1.'"
			+ "      />" + "    </atomArray>" + "    <bondArray>"
			+ "      <bond id='b1' atomRefs2='a1 a2' order='1'/>"
			+ "      <bond id='b2' atomRefs2='a1 a3' order='S'/>"
			+ "      <bond id='b3' atomRefs2='a1 a4' order='1'/>"
			+ "      <bond id='b4' atomRefs2='a1 a5' order='1'/>"
			+ "    </bondArray>" + "  </molecule>" + "  ";

	protected CMLMolecule xmlMolecule;
	protected List<CMLAtom> xmlAtoms;
	protected CMLAtom[] xmlAtom;
	protected List<CMLBond> xmlBonds;
	protected int xmlNatoms;
	protected int xmlNbonds;
	@Before
    public void setUp() throws Exception {
		
		// build reference molecule
		xomMolecule = new CMLMolecule();
		xomMolecule.setId("xom1");
		xomAtom = new CMLAtom[NATOM];
		for (int i = 0; i < NATOM; i++) {
			xomAtom[i] = new CMLAtom();
			xomAtom[i].setId("a" + (i + 1));
			xomMolecule.getOrCreateAtomArray().appendChild(xomAtom[i]);
			xomAtom[i].setElementType(elementTypes[i]);
			xomAtom[i].setX3((double) i);
			xomAtom[i].setY3((double) (i + 1));
			xomAtom[i].setZ3((double) (i + 2));
			xomAtom[i].setX2((double) i * 10);
			xomAtom[i].setY2((double) (i * 10 + 1));
			xomAtom[i].setHydrogenCount(hCounts[i]);
		}
		xomBond = new CMLBond[NBOND];
		for (int j = 0; j < NBOND; j++) {
			// form a cycle...
			// have to set id at this stage. Pehaps we should trap it
			xomBond[j] = new CMLBond(xomAtom[j], xomAtom[(j + 1) % NATOM]);
			xomBond[j].setId("b" + (j + 1));
			CMLBondArray bondArray = xomMolecule.getOrCreateBondArray();
			bondArray.appendChild(xomBond[j]);
			xomBond[j].setOrder((j == 0) ? "2" : "1");
		}
		
		try {
			xmlDocument = new CMLBuilder().build(new StringReader(xmlMolS));
		} catch (IOException e) {
			Assert.fail("Should not throw IOException");
		} catch (ParsingException e) {
			e.printStackTrace();
			LOG.error("Parse exception " + e);
			Assert.fail("Should not throw ParsingException " + e.getMessage());
		}
		xmlMolecule = (CMLMolecule) xmlDocument.getRootElement();

		xmlAtoms = xmlMolecule.getAtoms();
		xmlAtom = new CMLAtom[xmlAtoms.size()];
		for (int i = 0; i < xmlAtom.length; i++)
			xmlAtom[i] = (CMLAtom) xmlAtoms.get(i);
		xmlBonds = xmlMolecule.getBonds();

		xmlNatoms = 5;
		xmlNbonds = 4;

	}

	
	// protected AtomTool atomTool1;
	MoleculeAtomBondFixture fixture = new MoleculeAtomBondFixture();

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.AtomTool#getCoordinationSphereList(int)}.
	 */
	@Test
	public final void testGetCoordinationSphereList() {
		fixture.makeMol11();
		CMLAtom atom1 = fixture.mol11.getAtomById("a1");

		AtomTool atomTool1 = AtomTool.getOrCreateTool(atom1);
		CMLAtomSet atomSet = atomTool1.getCoordinationSphereSet(0);
		CMLAtomSet refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='1' xmlns='http://www.xml-cml.org/schema'>a1</atomSet>");
		assertEqualsCanonically("atomSet 0", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(1);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='2' xmlns='http://www.xml-cml.org/schema'>a1 a2</atomSet>");
		assertEqualsCanonically("atomSet 1", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(2);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='4' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4</atomSet>");
		assertEqualsCanonically("atomSet 2", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(3);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='5' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5</atomSet>");
		assertEqualsCanonically("atomSet 3", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(1);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='2' xmlns='http://www.xml-cml.org/schema'>a1 a2</atomSet>");
		assertEqualsCanonically("atomSet 1", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(4);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='6' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6</atomSet>");
		assertEqualsCanonically("atomSet 4", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(5);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='8' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 5", refAtomSet, atomSet);
		atomSet = atomTool1.getCoordinationSphereSet(6);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='8' xmlns='http://www.xml-cml.org/schema'>a1 a2 a3 a4 a5 a6 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 6", refAtomSet, atomSet);
		// now a different atom
		AtomTool atomTool5 = AtomTool.getOrCreateTool(fixture.mol11
				.getAtomById("a5"));
		atomSet = atomTool5.getCoordinationSphereSet(2);
		refAtomSet = (CMLAtomSet) TestUtils.parseValidString("<atomSet size='7' xmlns='http://www.xml-cml.org/schema'>a5 a3 a6 a2 a4 a7 a8</atomSet>");
		assertEqualsCanonically("atomSet 2", refAtomSet, atomSet);

	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.calculateSpaceGroupMultiplicity(CMLSymmet
	 * r y ) '
	 */
	@Test
	public final void testCalculateSpaceGroupMultiplicity() {
		String[] ss = { "x, y, z", "-x, -y, -z", "x, 1/2-y, 1/2-z",
				"-x, 1/2+y, 1/2+z" };
		CMLSymmetry symmetry = new CMLSymmetry(ss);
		CMLAtom atom1 = new CMLAtom("a1");
		CMLMolecule molecule = new CMLMolecule();
		molecule.addAtom(atom1);
		AtomTool atomTool1 = AtomTool.getOrCreateTool(atom1);
		atom1.setElementType(AS.C.value);
		atom1.setPoint3(new Point3(0.0, 0.0, 0.0), CoordinateType.CARTESIAN);
		int mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 0, mult);

		atom1.setPoint3(new Point3(0.1, 0.2, 0.3), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 1, mult);

		atom1.setPoint3(new Point3(0.0, 0.0, 0.0), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(1.0, 0.0, 0.0), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1
				.setPoint3(new Point3(0.25, 0.25, 0.25),
						CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		ss = new String[] { "x, y, z", "-x, -y, -z", "x, -y, -z", "-x, y, z" };

		symmetry = new CMLSymmetry(ss);
		atom1 = new CMLAtom("a1");
		atom1.setElementType(AS.C.value);
		atom1.setPoint3(new Point3(0.0, 0.0, 0.0), CoordinateType.CARTESIAN);
		// don;t forget to reset tool!
		// and molecule
		molecule = new CMLMolecule();
		molecule.addAtom(atom1);
		atomTool1 = AtomTool.getOrCreateTool(atom1);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		// 0 because no fractionals yet
		Assert.assertEquals("multiplicity", 0, mult);

		atom1.setPoint3(new Point3(0.1, 0.2, 0.3), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 1, mult);

		atom1.setPoint3(new Point3(0.0, 0.2, 0.3), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(0.1, 0.0, 0.0), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(0.1, 0.5, 0.0), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(0.1, 0.5, 0.5), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(0.1, 1.5, 3.5), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 2, mult);

		atom1.setPoint3(new Point3(0.0, 0.0, 0.0), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 4, mult);

		atom1.setPoint3(new Point3(1.0, -1.5, 2.5), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 4, mult);

		atom1.setPoint3(new Point3(0.5, 0.5, 0.5), CoordinateType.FRACTIONAL);
		mult = atomTool1.calculateSpaceGroupMultiplicity(symmetry);
		Assert.assertEquals("multiplicity", 4, mult);

	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLAtom.transform(CMLTransform3)'
	 */
	@Test
	public final void testTransformCMLTransform3() {
		CMLTransform3 t = new CMLTransform3("y, -x, y+z");
		fixture.xomAtom[0].setPoint3(new Point3(1.1, 1.2, 1.3),
				CoordinateType.CARTESIAN);
		Point3 p = fixture.xomAtom[0].getPoint3(CoordinateType.CARTESIAN);
		assertEquals("getPoint3", new double[] { 1.1, 1.2, 1.3 }, p, EPS);
		AtomTool.getOrCreateTool(fixture.xomAtom[0]).transformCartesians(t);
		p = fixture.xomAtom[0].getPoint3(CoordinateType.CARTESIAN);
		assertEquals("getPoint3", new double[] { 1.2, -1.1, 2.5 }, p, EPS);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.transformFractionalCoordinates(CMLTransfo
	 * r m 3 ) '
	 */
	@Test
	public final void testTransformFractionalCoordinatesCMLTransform3() {
		CMLTransform3 t = new CMLTransform3("y, 1/2-x, y+z");
		fixture.xomAtom[0].setPoint3(new Point3(0.1, 0.2, 0.3),
				CoordinateType.FRACTIONAL);
		Point3 p = fixture.xomAtom[0].getPoint3(CoordinateType.FRACTIONAL);
		assertEquals("getPoint3", new double[] { 0.1, 0.2, 0.3 }, p, EPS);
		AtomTool.getOrCreateTool(fixture.xomAtom[0]).transformFractionals(t);
		p = fixture.xomAtom[0].getPoint3(CoordinateType.FRACTIONAL);
		assertEquals("getPoint3", new double[] { 0.2, 0.4, 0.5 }, p, EPS);
	}

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.addCalculatedCoordinatesForHydrogens(HydrogenControl)'
     */
    @Ignore("NOT YET IMPLEMENTED")
    @Deprecated
    public final void testAddCalculatedCoordinatesForHydrogens() {
		LOG.trace("test");
        CMLMolecule molecule = new CMLMolecule();
        CMLAtom atom = new CMLAtom();
        atom.setElementType(AS.C.value);
        atom.setId("a1");
        atom.setHydrogenCount(4);
        atom.setPoint3(new Point3(0, 0, 0), CoordinateType.CARTESIAN);
        molecule.addAtom(atom);
        for (int i = 0; i < 4; i++) {
            atom = new CMLAtom();
            atom.setElementType(AS.H.value);
            atom.setId("h" + (i + 1));
            molecule.addAtom(atom);
            CMLBond bond = new CMLBond();
            bond.setId("b" + (i + 1));
            bond.setAtomRefs2(new String[] { "a1", "h" + (i + 1) });
            molecule.addBond(bond);
        }
        AtomTool atomTool = AtomTool.getOrCreateTool(atom);
        atomTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, 10.0);
    }

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#sortListByAtomicNumber(java.util.List)}.
	 */
	@Test
	@Ignore
	public final void testSortListByAtomicNumber() {
		Assert.fail("Not yet implemented"); // TODO
	}


	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#resetCoordinationSphereList()}.
	 */
	@Test
	@Ignore
	public final void testResetCoordinationSphereList() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getCoordinationSphereSet(int)}.
	 */
	@Test
	@Ignore
	public final void testGetCoordinationSphereSet() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSortedLigandList()}.
	 */
	@Test
	@Ignore
	public final void testGetSortedLigandList() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSingleLigand()}.
	 */
	@Test
	@Ignore
	public final void testGetSingleLigand() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getSproutedSet(int)}.
	 */
	@Test
	@Ignore
	public final void testGetSproutedSet() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#recursiveCompare(org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	@Ignore
	public final void testRecursiveCompare() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getLoneElectronCount()}.
	 */
	@Test
	@Ignore
	public final void testGetLoneElectronCount() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getAtomWithLowestId(java.util.List)}.
	 */
	@Test
	@Ignore
	public final void testGetAtomWithLowestId() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#translateToCovalentRadius()}.
	 */
	@Test
	@Ignore
	public final void testTranslateToCovalentRadius() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#getAtomDisplay()}.
	 */
	@Test
	@Ignore
	public final void testGetAtomDisplay() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.AtomTool#setAtomDisplay(org.xmlcml.cml.tools.AtomDisplay)}.
	 */
	@Test
	@Ignore
	public final void testSetAtomDisplay() {
		Assert.fail("Not yet implemented"); // TODO
	}

	/**
	 * 
	 * pointer...
     <atomArray>
       <atom elementType="R" id="a1" moleculeRef="otbs" x2="31.218704021442583" y2="-12.577617621516218">
         <label value="OTBS"/>
       </atom>
       
     * ... target ...
     * 
     * <moleculeList>
         <molecule id="otbs">
           <atomArray>
             <atom id="a1" elementType="R">
               <label value="OTBS"/>
             </atom>
	 */
	@Test
    public final void testGetReferencedAtoms() {
        Element cml = TestUtils.parseValidFile("org/xmlcml/cml/tools/rgroup1.xml");
        Assert.assertNotNull("rgoup1 should exist", cml);
        String otbs = "OTBS";
        CMLAtom atom = (CMLAtom) cml.query("./cml:molecule/cml:atomArray/cml:atom[@elementType='R' and cml:label[@value='"+otbs+"']]", CML_XPATH).get(0);
        AtomTool atomTool = AtomTool.getOrCreateTool(atom);
        // this uses the explicit pointer
        String molId =atom.getAttributeValue("moleculeRef");
        Element refMol = (CMLMolecule) cml.query(".//cml:molecule[@id='"+molId+"']", CML_XPATH).get(0);
    	List<CMLAtom> atomList = atomTool.getReferencedAtoms(refMol, otbs);
    	Assert.assertEquals("atom count", 1, atomList.size());
    	Assert.assertEquals("atom id", "a1", atomList.get(0).getId());
    	// this just scans the whole scope
    	atomList = atomTool.getReferencedAtoms(cml, otbs);
    	Assert.assertEquals("atom count", 1, atomList.size());
    	Assert.assertEquals("atom id", "a1", atomList.get(0).getId());

	}
	

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.get3DCrossProduct(CMLAtom, CMLAtom)'
     */
    @Test
    public void testGet3DCrossProduct() {
        Vector3 cross3d = AtomTool.getOrCreateTool(xmlAtom[0]).get3DCrossProduct(xmlAtom[1], xmlAtom[2]);
        CMLAssert.assertEquals("cross3d", new double[] { 0., 2., -2. },
                cross3d, EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.get2DCrossProduct(CMLAtom, CMLAtom)'
     */
    @Test
    public void testGet2DCrossProduct() {
        Vector3 cross2d = AtomTool.getOrCreateTool(xmlAtom[0]).get2DCrossProduct(xmlAtom[1], xmlAtom[2]);
        CMLAssert.assertEquals("cross2d", new double[] { 0., 0., -2. },
                cross2d, EPS);
    }
    
    /**
     * 
     */
    @Test
    public void testAddHydrogen() {
    	CMLMolecule molecule = new CMLMolecule();
    	CMLAtom atom = new CMLAtom("a1", AS.C);
    	molecule.addAtom(atom);
    	AtomTool atomTool = AtomTool.getOrCreateTool(atom);
    	CMLAtom hAtom1 = atomTool.addHydrogen();
    	Assert.assertNotNull("h atom", hAtom1);
    	hAtom1 = molecule.getAtomById("a1_h1");
    	Assert.assertNotNull("h atom", hAtom1);
    	Assert.assertEquals("h id", "a1_h1", hAtom1.getId());
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	CMLBond hBond1 = moleculeTool.getBond(atom.getId(), hAtom1.getId());
    	Assert.assertNotNull("h bond", hBond1);
    	Assert.assertEquals("h bond id", "a1_a1_h1", hBond1.getId());

    	CMLAtom hAtom2 = atomTool.addHydrogen();
    	Assert.assertNotNull("h atom", hAtom2);
    	hAtom2 = molecule.getAtomById("a1_h2");
    	Assert.assertNotNull("h atom", hAtom2);
    	Assert.assertEquals("h id", "a1_h2", hAtom2.getId());
    	CMLBond hBond2 = moleculeTool.getBond(atom.getId(), hAtom2.getId());
    	Assert.assertNotNull("h bond", hBond2);
    	Assert.assertEquals("h bond id", "a1_a1_h2", hBond2.getId());
//    	molecule.debug("MOLZZZ ");

    	// delete bond but not atom (generally bad idea)
    	molecule.deleteBond(hBond1);
//    	molecule.debug("MOL ");
    	moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	hBond1 = moleculeTool.getBond(atom.getId(), hAtom1.getId());
    	Assert.assertNull("h bond null", hBond1);
    	Assert.assertNotNull("h atom 1", molecule.getAtomById("a1_h1"));
    	Assert.assertNotNull("h atom 2", molecule.getAtomById("a1_h2"));
    	
    	atomTool.addHydrogen();
    	CMLAtom hAtom3 = molecule.getAtomById("a1_h3");
    	Assert.assertNotNull("h atom", hAtom3);
//    	molecule.debug("MMMMM");
    	
    	Assert.assertEquals("h ligands", 2, atomTool.getHydrogenLigandList().size());
    	// deletes bond as well
    	molecule.deleteAtom(hAtom2);
    	hAtom2 = molecule.getAtomById("a1_h2");
    	Assert.assertNull("h atom", hAtom2);
//    	molecule.debug("MOL ");
    }
}
