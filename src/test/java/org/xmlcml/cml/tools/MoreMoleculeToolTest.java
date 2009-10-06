package org.xmlcml.cml.tools;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TestUtils;

public class MoreMoleculeToolTest {

	MoleculeToolFixture tFix = new MoleculeToolFixture();

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.MoleculeTool.adjustBondOrdersToValency() { '
	 */
	@Test
	public void testAdjustBondOrdersToValency() {
		MoleculeToolFixture.abov(tFix.benzene, 0, tFix.benzeneOrder); // OK
		// abov(nick, 0, nickOrder);
		MoleculeToolFixture.abov(tFix.styrene, 0, tFix.styreneOrder); // OK
		MoleculeToolFixture.abov(tFix.pyrene, 0, tFix.pyreneOrder); // OK
		MoleculeToolFixture.abov(tFix.triphene, 1, tFix.tripheneOrder); //
		// abov(methyleneCyclohexene, 0, methyleneCyclohexeneOrder); // OK
		// abov(methyleneCyclohexadiene, 0, methyleneCyclohexadieneOrder); // OK
		MoleculeToolFixture.abov(tFix.co2, 0, tFix.co2Order); // OK
		MoleculeToolFixture.abov(tFix.azulene, 0, tFix.azuleneOrder);
		/*
		 * -- abov(conjugated); abov(formate1); abov(formate2); abov(formate3);
		 * abov(pyridine); abov(pyridinium); abov(pyridone4);
		 * abov(nitroMethane); abov(nitric); abov(oxalate); --
		 */
		// abov(munchnone, 0);
		// abov(oxalate2, 2); // OK
		// abov(benzophenone);
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.MoleculeTool.sprout()'
	 */
	@Test
	public void testSprout() {

		// self sprout
		MoleculeTool sproutTool = MoleculeTool.getOrCreateTool(tFix.sprout);
		CMLMolecule sproutMolecule = sproutTool.sprout();
		Assert
				.assertEquals("sprout AS size", 13, sproutMolecule
						.getAtomCount());
		Assert
				.assertEquals("sprout BS size", 13, sproutMolecule
						.getBondCount());

		// sub sprout
		List<CMLAtom> atoms = tFix.sprout.getAtoms();
		List<CMLAtom> atomList = new ArrayList<CMLAtom>();
		atomList.add(atoms.get(0));
		atomList.add(atoms.get(1));
		CMLAtomSet subAtomSet = CMLAtomSet.createFromAtoms(atomList);
		CMLMolecule subMolecule = sproutTool.sprout(subAtomSet);
		Assert.assertEquals("sub AS size", 6, subMolecule.getAtomCount());
		Assert.assertEquals("sub BS size", 5, subMolecule.getBondCount());

	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getAverageBondLength(CoordinateType)'
	 */
	@Test
	public void testGetAverageBondLength() {
		tFix.makeMoleculeTool5();
		try {
			tFix.moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		double length = tFix.moleculeTool5
				.getAverageBondLength(CoordinateType.CARTESIAN);
		Assert.assertEquals("average length", 1.2235, length, .0001);
	}

	/** */
	@Test
	public final void testGetTotalHydrogenCount() {
		Assert.assertEquals("benzene", 6, MoleculeTool.getOrCreateTool(
				tFix.benzene).getTotalHydrogenCount());
		String moleculeS = "" + "<molecule " + CMLConstants.CML_XMLNS + ">"
				+ "  <atomArray>" + "    <atom id='a1' elementType='C'/>"
				+ "    <atom id='a2' elementType='O'/>" + "  </atomArray>"
				+ "  <bondArray>" + "    <bond atomRefs2='a1 a2' order='1'/>"
				+ "  </bondArray>" + "</molecule>";
		CMLMolecule molecule = (CMLMolecule)TestUtils.parseValidString(moleculeS);
		int hydrogenCount = MoleculeTool.getOrCreateTool(molecule)
				.getTotalHydrogenCount();
		Assert.assertEquals("h count", 4, hydrogenCount);
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLAtom.getBondOrderSum()'
	 */
	@Test
	public void testGetBondOrderSum() {
		// makeMoleculeToolXml0();
		CMLAtom[] xmlAtom = tFix.getFixture().xmlAtom;
		String el = xmlAtom[0].getElementType();
		Assert.assertEquals("element type", AS.N.value, el);
		MoleculeTool moleculeToolXml0 = tFix.moleculeToolXml0;
		int bes = moleculeToolXml0.getBondOrderSum(xmlAtom[0]);
		el = xmlAtom[1].getElementType();
		Assert.assertEquals("element type", AS.C.value, el);
		bes = moleculeToolXml0.getBondOrderSum(xmlAtom[1]);
		Assert.assertEquals("bond order sum", 4, bes);
		CMLMolecule benzene = tFix.benzene;
		benzene.setBondOrders(CMLBond.SINGLE);
		int bes1 = MoleculeTool.getOrCreateTool(benzene).getBondOrderSum(
				benzene.getAtom(0));
		Assert.assertEquals("bond order sum", 3, bes1);
		CMLMolecule methyleneCyclohexene = tFix.methyleneCyclohexene;
		methyleneCyclohexene.setBondOrders(CMLBond.SINGLE);
		MoleculeTool methyleneCyclohexeneTool = MoleculeTool
				.getOrCreateTool(methyleneCyclohexene);
		bes1 = methyleneCyclohexeneTool.getBondOrderSum(methyleneCyclohexene
				.getAtom(0));
		Assert.assertEquals("bond order sum", 3, bes1);
		bes1 = methyleneCyclohexeneTool.getBondOrderSum(methyleneCyclohexene
				.getAtom(0));
		Assert.assertEquals("bond order sum", 3, bes1);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.getGeometricHybridization()'
	 */
	@Test
	public void testGetGeometricHybridization() {
		CMLAtom.Hybridization hyb = tFix.moleculeToolXml0
				.getGeometricHybridization(tFix.getFixture().xmlAtom[0]);
		Assert.assertEquals("hybrid", CMLAtom.Hybridization.SP3, hyb);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.getHydrogenValencyGroup()'
	 */
	@Test
	public void testGetHydrogenValencyGroup() {
		CMLAtom[] xmlAtom = tFix.getFixture().xmlAtom;
		MoleculeTool moleculeToolXml0 = tFix.moleculeToolXml0;
		int hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[0]);
		Assert.assertEquals("elementType", AS.N.value, xmlAtom[0]
				.getElementType());
		// atom attached to electronegative ligands
		Assert.assertTrue("hydrogen valency", hvg < 0);
		hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[1]);
		Assert.assertEquals("elementType", AS.C.value, xmlAtom[1]
				.getElementType());
		Assert.assertEquals("hydrogen valency", 4, hvg);
		hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[2]);
		Assert.assertEquals("elementType", AS.S.value, xmlAtom[2]
				.getElementType());
		Assert.assertEquals("hydrogen valency", 6, hvg);
		Assert.assertEquals("elementType", AS.O.value, xmlAtom[3]
				.getElementType());
		hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[3]);
		Assert.assertEquals("hydrogen valency", 6, hvg);
		Assert.assertEquals("elementType", AS.F.value, xmlAtom[4]
				.getElementType());
		hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[4]);
		Assert.assertEquals("hydrogen valency", 7, hvg);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.getSumNonHydrogenBondOrder()'
	 */
	@Test
	public void testGetSumNonHydrogenBondOrder() {
		int sum = tFix.moleculeToolXml0.getSumNonHydrogenBondOrder(tFix
				.getFixture().xmlAtom[0]);
		Assert.assertEquals("nonh bond order sum", 4, sum);
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLAtom.deleteHydrogen()'
	 */
	@Test
	public void testDeleteHydrogen() {
		CMLAtom atom = tFix.benzene.getAtom(0);
		Assert.assertEquals("before delete H", 1, atom.getHydrogenCount());
		MoleculeTool.getOrCreateTool(tFix.benzene).deleteHydrogen(atom);
		Assert.assertEquals("after delete H", 0, atom.getHydrogenCount());
	}

	/**
	 * test.
	 * 
	 */
	@Test
	public void testAddCoords() {
		// no ligands with coords
		// C-H
		Builder builder = new CMLBuilder();
		Document doc;
		try {
			String t01 = CMLConstants.S_EMPTY
					+ "<molecule id='t01' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "  <atomArray>"
					+ "    <atom id='a1' elementType='C' x3='10' y3='10' z3='10'/>"
					+ "    <atom id='h1' elementType='H'/>" + "  </atomArray>"
					+ "  <bondArray>"
					+ "    <bond atomRefs2='a1 h1' order='1'/>"
					+ "  </bondArray>" + "</molecule>";
			doc = builder.build(new StringReader(t01));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C(-H)-H
		try {
			// FIXME
			String t02 = "<molecule id='t02' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t02));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C(-H)(-H)-H
		try {
			String t03 = "<molecule id='t03' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t03));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C(-H)(-H)(-H)-H
		try {
			String t04 = "<molecule id='t04' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/><atom id='h4' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/><bond atomRefs2='a1 h4' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t04));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// one ligand
		// C#C-H
		try {
			String t11 = "<molecule id='t11' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='3'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t11));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.LINEAR, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C-C-O-H
		try {
			String t11a = "<molecule id='t11a' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='O' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8' y3='9' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t11a));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.TETRAHEDRAL, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C-C=C(-H)-H
		try {
			String t12 = "<molecule id='t12' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t12));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.TRIGONAL, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C-C-C(-H)(-H)-H
		try {
			String t13 = "<molecule id='t13' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t13));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// two ligands
		// C=C-H
		// |
		// C
		try {
			String t21 = "<molecule id='t21' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t21));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.TRIGONAL, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C-N-H
		// |
		// C
		try {
			String t21a = "<molecule id='t21a' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='N' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t21a));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.TETRAHEDRAL, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// C-C(-H)-H
		// |
		// C
		try {
			String t22 = "<molecule id='t22' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t22));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// three ligands
		// C
		// |
		// C-C-H
		// |
		// C
		try {
			String t31 = "<molecule id='t31' "
					+ CMLConstants.CML_XMLNS
					+ ">"
					+ "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='10.8' z3='10.8'/><atom id='a3' elementType='C' x3='10.8' y3='10.8' z3='9.2'/><atom id='a4' elementType='C' x3='10.8' y3='9.2' z3='10.8'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 a4' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
			doc = builder.build(new StringReader(t31));
			CMLMolecule mol = (CMLMolecule) doc.getRootElement();
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLAtom> atoms = mol.getAtoms();
			CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
					atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
			Assert.assertNotNull("atomset should not be null", as1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.adjustHydrogenCountsToValency(String)
	 * '
	 */
	@Test
	public void testAdjustHydrogenCountsToValency() {
		tFix.makeMoleculeTool5();
		MoleculeTool moleculeTool5 = tFix.moleculeTool5;
		try {
			moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		CMLMolecule mol5 = tFix.getFixture().mol5;
		Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
		Assert.assertEquals("calculated bonds", new String[] { "a1", "a2" }, mol5
				.getBonds().get(0).getAtomRefs2());
		Assert.assertEquals("calculated bonds", new String[] { "a1", "a4" }, mol5
				.getBonds().get(1).getAtomRefs2());
		Assert.assertEquals("calculated bonds", new String[] { "a1", "a5" }, mol5
				.getBonds().get(2).getAtomRefs2());
		Assert.assertEquals("calculated bonds", new String[] { "a2", "a3" }, mol5
				.getBonds().get(3).getAtomRefs2());
		List<CMLBond> bonds = mol5.getBonds();
		moleculeTool5.calculateBondOrdersFromXYZ3();
		Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
		Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
		Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
		Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
		moleculeTool5
				.adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_HYDROGEN_COUNT);
		CMLAtom a1 = mol5.getAtomById("a1");
		CMLAtom a2 = mol5.getAtomById("a2");
		CMLAtom a3 = mol5.getAtomById("a3");
		CMLAtom a4 = mol5.getAtomById("a4");
		Assert.assertEquals("a1 ", 2, a1.getHydrogenCount());
		Assert.assertNotNull("a2 ", a2.getHydrogenCountAttribute());
		Assert.assertEquals("a3 ", 0, a3.getHydrogenCount());
		Assert.assertNull("a4 ", a4.getHydrogenCountAttribute()); // this is a
		// hydrogen
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.expandImplicitHydrogens(String)'
	 */
	@Test
	public void testExpandImplicitHydrogens() {
		tFix.makeMoleculeTool10();
		CMLMolecule mol10 = tFix.getFixture().mol10;
		CMLAtom atom0 = mol10.getAtom(0);
		tFix.moleculeTool10.expandImplicitHydrogens(atom0,
				HydrogenControl.NO_EXPLICIT_HYDROGENS);
		Assert.assertEquals("after addition", 7, mol10.getAtomCount());
		CMLAtom atom5 = mol10.getAtom(5);
		Assert.assertEquals("added H", "a1_h1", atom5.getId());
		CMLAtom atom6 = mol10.getAtom(6);
		Assert.assertEquals("added H", "a1_h2", atom6.getId());
		CMLBond bond4 = mol10.getBonds().get(4);
		Assert.assertEquals("after addition", 6, mol10.getBondCount());
		Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h1" },
				bond4.getAtomRefs2());
		CMLBond bond5 = mol10.getBonds().get(5);
		Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h2" },
				bond5.getAtomRefs2());
		// do the whole molecule
		tFix.makeMoleculeTool10();
		tFix.moleculeTool10
				.expandImplicitHydrogens(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		mol10 = tFix.moleculeTool10.getMolecule();
		Assert.assertEquals("after addition", 9, mol10.getAtomCount());
		atom5 = mol10.getAtom(5);
		Assert.assertEquals("added H", "a1_h1", atom5.getId());
		atom6 = mol10.getAtom(6);
		Assert.assertEquals("added H", "a1_h2", atom6.getId());
		CMLAtom atom7 = mol10.getAtom(7);
		Assert.assertEquals("added H", "a2_h1", atom7.getId());
		CMLAtom atom8 = mol10.getAtom(8);
		Assert.assertEquals("added H", "a2_h2", atom8.getId());
		Assert.assertEquals("after addition", 8, mol10.getBondCount());
		bond4 = mol10.getBonds().get(4);
		Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h1" },
				bond4.getAtomRefs2());
		bond5 = mol10.getBonds().get(5);
		Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h2" },
				bond5.getAtomRefs2());
		CMLBond bond6 = mol10.getBonds().get(6);
		Assert.assertEquals("added bond to H", new String[] { "a2", "a2_h1" },
				bond6.getAtomRefs2());
		CMLBond bond7 = mol10.getBonds().get(7);
		Assert.assertEquals("added bond to H", new String[] { "a2", "a2_h2" },
				bond7.getAtomRefs2());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.setPreferredBondOrders()'
	 */
	@Test
	public void testSetPreferredBondOrders() {
		tFix.makeMoleculeTool5();
		tFix.moleculeTool5.calculateBondedAtoms();
		CMLMolecule mol5 = tFix.getFixture().mol5;
		Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
		Assert.assertNull("initial order", mol5.getBonds().get(0).getOrder());
		mol5.setBondOrders(CMLBond.SINGLE);
		// note that getOrder() will return the preferred order
		Assert.assertEquals("updated order", CMLBond.SINGLE, mol5.getBonds()
				.get(0).getOrderAttribute().getValue());
		mol5.setNormalizedBondOrders();
		Assert.assertEquals("perferred order", CMLBond.SINGLE_S, mol5.getBonds()
				.get(0).getOrderAttribute().getValue());
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getDoubleBonds()'
	 */
	@Test
	public void testGetDoubleBonds() {
		tFix.makeMoleculeTool5a();
		tFix.moleculeTool5a.calculateBondedAtoms();
		tFix.moleculeTool5a.calculateBondOrdersFromXYZ3();
		List<CMLBond> bonds = tFix.getFixture().mol5a.getDoubleBonds();
		Assert.assertEquals("double bonds", 1, bonds.size());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.setBondOrders(String)'
	 */
	@Test
	public void testSetBondOrders() {
		tFix.makeMoleculeTool5();
		tFix.moleculeTool5.calculateBondedAtoms();
		CMLMolecule mol5 = tFix.getFixture().mol5;
		Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
		Assert.assertNull("initial order", mol5.getBonds().get(0).getOrder());
		mol5.setBondOrders(CMLBond.SINGLE);
		Assert.assertEquals("updated order", CMLBond.SINGLE, mol5.getBonds()
				.get(0).getOrder());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.createValenceAngles(boolean,
	 * boolean)'
	 */
	@Test
	public void testCreateValenceAngles() {
		tFix.makeMoleculeTool5();
		tFix.moleculeTool5.calculateBondedAtoms();
		List<CMLAtom> atoms = tFix.getFixture().mol5.getAtoms();
		CMLAtom atom0 = atoms.get(0);
		List<CMLAtom> ligandList = atom0.getLigandAtoms();
		Assert.assertEquals("ligand list", 3, ligandList.size());
		new GeometryTool(tFix.getFixture().mol5)
				.createValenceAngles(true, true);
		List<CMLAngle> angles = tFix.moleculeTool5.getAngleElements();
		Assert.assertEquals("angles", 4, angles.size());
		CMLAngle angle = angles.get(0);
		Assert.assertEquals("angle 0 atoms", new String[] { "a2", "a1", "a4" },
				angle.getAtomRefs3());
		Assert.assertEquals("angle 0 value", 118.704, angle.getXMLContent(),
				0.001);
		angle = angles.get(3);
		Assert.assertEquals("angle 3 atoms", new String[] { "a1", "a2", "a3" },
				angle.getAtomRefs3());
		Assert.assertEquals("angle 3 value", 131.987, angle.getXMLContent(),
				0.001);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.createValenceLengths(boolean,
	 * boolean)'
	 */
	@Test
	public void testCreateValenceLengths() {
		tFix.makeMoleculeTool5();
		tFix.moleculeTool5.calculateBondedAtoms();
		List<CMLAtom> ligandList = tFix.getFixture().mol5.getAtoms().get(0)
				.getLigandAtoms();
		Assert.assertEquals("ligand list", 3, ligandList.size());
		new GeometryTool(tFix.getFixture().mol5).createValenceLengths(true,
				true);
		List<CMLLength> lengths = tFix.moleculeTool5.getLengthElements();
		Assert.assertEquals("lengths", 4, lengths.size());
		CMLLength length = lengths.get(0);
		Assert.assertEquals("length 0 atoms", new String[] { "a2", "a1" },
				length.getAtomRefs2());
		Assert.assertEquals("length 0 value", 1.3, length.getXMLContent(),
				0.001);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.createValenceTorsions(boolean,
	 * boolean)'
	 */
	@Test
	public void testCreateValenceTorsions() {
		tFix.makeMoleculeTool5();
		tFix.moleculeTool5.calculateBondedAtoms();
		MoleculeAtomBondFixture fixture = tFix.getFixture();
		List<CMLAtom> ligandList = fixture.mol5.getAtoms().get(0)
				.getLigandAtoms();
		Assert.assertEquals("ligand list", 3, ligandList.size());
		new GeometryTool(fixture.mol5).createValenceTorsions(true, true);
		List<CMLTorsion> torsions = MoleculeTool.getOrCreateTool(fixture.mol5)
				.getTorsionElements();
		Assert.assertEquals("torsions", 2, torsions.size());
		CMLTorsion torsion = torsions.get(0);
		Assert.assertEquals("torsion 0 atoms", new String[] { "a4", "a1", "a2",
				"a3" }, torsion.getAtomRefs4());
		Assert.assertEquals("torsion 0 value", 30.465, torsion.getXMLContent(),
				0.001);
		torsion = torsions.get(1);
		Assert.assertEquals("torsion 1 atoms", new String[] { "a5", "a1", "a2",
				"a3" }, torsion.getAtomRefs4());
		Assert.assertEquals("torsion 1 value", 149.534,
				torsion.getXMLContent(), 0.001);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateBondedAtoms()'
	 */
	@Test
	public void testCalculateBondedAtoms() {
		tFix.makeMoleculeTool5();
		try {
			tFix.moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		Assert.assertEquals("calculated bonds", 4, tFix.getFixture().mol5
				.getBondCount());
		List<CMLBond> bonds = tFix.getFixture().mol5.getBonds();
		Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a2"), bonds
				.get(0).atomHash());
		Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a4"), bonds
				.get(1).atomHash());
		Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a5"), bonds
				.get(2).atomHash());
		Assert.assertEquals("bond 0", CMLBond.atomHash("a2", "a3"), bonds
				.get(3).atomHash());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateBondOrdersFromXYZ3()'
	 */
	@Test
	public void testCalculateBondOrdersFromXYZ3() {
		tFix.makeMoleculeTool5();
		MoleculeTool moleculeTool5 = tFix.moleculeTool5;
		try {
			moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		Assert.assertEquals("calculated bonds", 4, tFix.getFixture().mol5
				.getBondCount());
		List<CMLBond> bonds = tFix.getFixture().mol5.getBonds();
		moleculeTool5.calculateBondOrdersFromXYZ3();
		Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
		Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
		Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
		Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateBondsFromXYZ3(double)'
	 */
	@Test
	public void testCalculateBondsFromXYZ3() {
		tFix.makeMoleculeTool5();
		try {
			tFix.moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		Assert.assertEquals("calculated bonds", 4, tFix.getFixture().mol5
				.getBondCount());
		List<CMLBond> bonds = tFix.getFixture().mol5.getBonds();
		tFix.moleculeTool5.calculateBondOrdersFromXYZ3();
		Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
		Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
		Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
		Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.getAverageBondDistance(int)'
	 */
	@Test
	public void testGetAverageBondDistance() {
		tFix.makeMoleculeTool5();
		try {
			tFix.moleculeTool5.calculateBondedAtoms();
		} catch (RuntimeException e) {
			Assert.fail("test bug " + e);
		}
		double length = tFix.moleculeTool5
				.getAverageBondLength(CoordinateType.CARTESIAN);
		Assert.assertEquals("average length", 1.2235, length, .0001);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getLoneElectronCount(CMLAtom)'
	 */
	@Test
	public void testGetLoneElectronCount() {
		// FIXME
		CMLMolecule nitroMethane = (CMLMolecule)TestUtils.parseValidString(tFix.nitroMethaneS);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(nitroMethane);
		int n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(0));
		Assert.assertEquals("lone pair", -6, n);
		n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(1));
		Assert.assertEquals("lone pair", 0, n);
		n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(2));
		Assert.assertEquals("lone pair", 6, n);
		n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(3));
		Assert.assertEquals("lone pair", 5, n);
	}

}
