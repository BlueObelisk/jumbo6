/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.cml.test.CMLAssert.parseValidString;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;
import org.xmlcml.cml.tools.Morgan.Algorithm;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * @author pm286
 * 
 */
public class MorganTest {
	MoleculeAtomBondFixture fixture = new MoleculeAtomBondFixture();

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLMolecule)}
	 * .
	 */
	@Test
	public final void testMorganCMLMolecule() {
		fixture.makeMol5a();
		Morgan morgan = new Morgan(fixture.mol5a);
		List<Long> morganList = morgan.getMorganList();
		assertEquals("equivalence classes", new long[] { 6914834, 8549135,
				13376403, 15137814 }, morganList);

		List<CMLAtomSet> atomSets = morgan.getAtomSetList();
		assertEquals("equivalence classes", new String[][] {
				new String[] { "a3" }, new String[] { "a4", "a5" },
				new String[] { "a2" }, new String[] { "a1" }, }, atomSets);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}
	 * . shows how to get atom hashes
	 */
	@Test
	public final void testMorganCMLAtomSet() {
		fixture.makeMol5a();
		Morgan morgan = new Morgan(MoleculeTool.getOrCreateTool(fixture.mol5a)
				.getAtomSet());
		List<Long> morganList = morgan.getMorganList();
		assertEquals("mol5a",
				new long[] { 6914834, 8549135, 13376403, 15137814 }, morganList);
		List<CMLAtomSet> atomSets = morgan.getAtomSetList();
		assertEquals("mol5a", new String[][] { new String[] { "a3" },
				new String[] { "a4", "a5" }, new String[] { "a2" },
				new String[] { "a1" }, }, atomSets);

		SMILESTool smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[R]C1=[CH][NH][CH]=N1");
		CMLMolecule mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);

		morgan = new Morgan(MoleculeTool.getOrCreateTool(mol).getAtomSet());
		morganList = morgan.getMorganList();
		assertEquals("mol", new long[] { 95192965, 101393610, 110848579,
				133670440, 219535198, 232594957, 249236436, 268407298,
				280594395 }, morganList);

		long[] refMorganList = new long[] { 93040342, 93170804, 99371449,
				165653696, 165718927, 212568866, 212764559, 229406038 };

		CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(mol).getAtomSet();
		// remove R group
		atomSet.removeAtom(mol.getAtomById("a1"));
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// try in different order:
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=N[CH0]=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = MoleculeTool.getOrCreateTool(mol).getAtomSet();
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// and in a larger molecule (add a phenyl)
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=NC(c2ccccc2)=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = new CMLAtomSet(mol, new String[] { "a1", "a2", "a3", "a4",
				"a11", "a1_h1", "a2_h1", "a11_h1", });
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// and in a larger molecule (add a chlorine)
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=NC(Cl)=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = new CMLAtomSet(mol, new String[] { "a1", "a2", "a3", "a4",
				"a6", "a1_h1", "a2_h1", "a6_h1", });
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// and in a larger molecule (add a chlorinoxy)
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=NC(OCl)=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = new CMLAtomSet(mol, new String[] { "a1", "a2", "a3", "a4",
				"a7", "a1_h1", "a2_h1", "a7_h1", });
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// and in a larger molecule (add ethyloxy)
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=NC(OCC)=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = new CMLAtomSet(mol, new String[] { "a1", "a2", "a3", "a4",
				"a8", "a1_h1", "a2_h1", "a8_h1", });
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

		// and in a larger molecule (add ring)
		smilesTool = new SMILESTool();
		smilesTool.parseSMILES("[NH]1[CH]=NC(C2ONSC2)=[CH]1");
		mol = smilesTool.getMolecule();
		MoleculeTool.getOrCreateTool(mol).expandImplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		atomSet = new CMLAtomSet(mol, new String[] { "a1", "a2", "a3", "a4",
				"a10", "a1_h1", "a2_h1", "a10_h1", });
		morgan = new Morgan(atomSet);
		morganList = morgan.getMorganList();
		assertEquals("mol", refMorganList, morganList);

	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}
	 * .
	 */
	@Test
	public final void testAlgorithm() {
		fixture.makeMol5a();
		Morgan morgan = new Morgan(MoleculeTool.getOrCreateTool(fixture.mol5a)
				.getAtomSet());
		morgan.setAlgorithm(Algorithm.SPLIT);
		List<Long> morganList = morgan.getMorganList();
		assertEquals("Morgan list", new long[] { 6914834, 8549135, 8549136,
				13376403, 15137815 }, morganList);

		List<CMLAtomSet> atomSets = morgan.getAtomSetList();
		assertEquals("equivalence classes", new String[][] {
				new String[] { "a3" }, new String[] { "a5" },
				new String[] { "a4" }, new String[] { "a2" },
				new String[] { "a1" }, }, atomSets);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.Morgan#Morgan(org.xmlcml.cml.element.CMLAtomSet)}
	 * . test split various equivalences
	 */
	@Test
	// FIXME
	@Ignore
	public final void testAlgorithm1() {
		String benzeneS = S_EMPTY + "<molecule " + CML_XMLNS + ">"
				+ "  <atomArray>" + "    <atom id='a1' elementType='C'/>"
				+ "    <atom id='h1' elementType='H'/>"
				+ "    <atom id='a2' elementType='C'/>"
				+ "    <atom id='h2' elementType='H'/>"
				+ "    <atom id='a3' elementType='C'/>"
				+ "    <atom id='h3' elementType='H'/>"
				+ "    <atom id='a4' elementType='C'/>"
				+ "    <atom id='h4' elementType='H'/>"
				+ "    <atom id='a5' elementType='C'/>"
				+ "    <atom id='h5' elementType='H'/>"
				+ "    <atom id='a6' elementType='C'/>"
				+ "    <atom id='h6' elementType='H'/>" + "  </atomArray>"
				+ "  <bondArray>" + "    <bond atomRefs2='a1 a2'/>"
				+ "    <bond atomRefs2='a1 h1'/>"
				+ "    <bond atomRefs2='a2 a3'/>"
				+ "    <bond atomRefs2='a2 h2'/>"
				+ "    <bond atomRefs2='a3 a4'/>"
				+ "    <bond atomRefs2='a3 h3'/>"
				+ "    <bond atomRefs2='a4 a5'/>"
				+ "    <bond atomRefs2='a4 h4'/>"
				+ "    <bond atomRefs2='a5 a6'/>"
				+ "    <bond atomRefs2='a5 h5'/>"
				+ "    <bond atomRefs2='a6 a1'/>"
				+ "    <bond atomRefs2='a6 h6'/>" + "  </bondArray>"
				+ "</molecule>";
		CMLMolecule benzene = (CMLMolecule) parseValidString(benzeneS);
		Morgan morgan = new Morgan(MoleculeTool.getOrCreateTool(benzene)
				.getAtomSet());
		List<Long> morganList = morgan.getMorganList();
		assertEquals("benzene", new long[] { 8547372, 21466984 }, morganList);

		List<CMLAtomSet> atomSets = morgan.getAtomSetList();
		assertEquals("benzene", new String[][] {
				new String[] { "h1", "h2", "h3", "h4", "h5", "h6" },
				new String[] { "a1", "a2", "a3", "a4", "a5", "a6" }, },
				atomSets);

		// ==================================

		benzene = (CMLMolecule) parseValidString(benzeneS);
		morgan = new Morgan(MoleculeTool.getOrCreateTool(benzene).getAtomSet());
		List<CMLAtom> markedAtoms = morgan.getMarkedAtomList();
		Assert.assertNull("marked atoms null", markedAtoms);
		morgan.setAlgorithm(Algorithm.SPLIT);
		morganList = morgan.getMorganList();

		assertEquals("marked benzene", new long[] { 102962709, 102962723,
				102962783, 102962791, 102962818, 102962838, 248859457,
				248859476, 248859555, 248859579, 248859638, 248859657

		}, morganList);

		atomSets = morgan.getAtomSetList();
		assertEquals("markedBenzene", new String[][] { new String[] { "h5" },
				new String[] { "h6" }, new String[] { "h4" },
				new String[] { "h1" }, new String[] { "h3" },
				new String[] { "h2" }, new String[] { "a5" },
				new String[] { "a6" }, new String[] { "a4" },
				new String[] { "a1" }, new String[] { "a3" },
				new String[] { "a2" }, }, atomSets);

		markedAtoms = morgan.getMarkedAtomList();
		Assert.assertEquals("marked atoms", 7, markedAtoms.size());
		for (CMLAtom atom : markedAtoms) {
			System.out.println("A " + atom.getId());
		}

		// ==================================

		benzene = (CMLMolecule) parseValidString(benzeneS);
		morgan = new Morgan(MoleculeTool.getOrCreateTool(benzene).getAtomSet());
		benzene.getAtom(1).setProperty(Morgan.Annotation.MARKED.toString(),
				new Long(1));
		morganList = morgan.getMorganList();
		assertEquals("marked benzene", new long[] { 5707768002L, 5707768005L,
				5707768015L, 5707768026L, 13780200010L, 13780200018L,
				13780200035L, 13780200048L }, morganList);

		atomSets = morgan.getAtomSetList();
		assertEquals("markedBenzene", new String[][] { new String[] { "h4" },
				new String[] { "h3", "h5" }, new String[] { "h2", "h6" },
				new String[] { "h1" }, new String[] { "a4" },
				new String[] { "a3", "a5" }, new String[] { "a2", "a6" },
				new String[] { "a1" }, }, atomSets);

		markedAtoms = morgan.getMarkedAtomList();
		// // Assert.assertEquals("marked atoms", 7, markedAtoms.size());
		// for (CMLAtom atom : markedAtoms) {
		// System.out.println("A "+atom.getId());
		// }

		// ==================================

		benzene = (CMLMolecule) parseValidString(benzeneS);
		morgan = new Morgan(MoleculeTool.getOrCreateTool(benzene).getAtomSet());
		benzene.getAtom(1).setProperty(Morgan.Annotation.MARKED.toString(),
				new Long(1));
		benzene.getAtom(5).setProperty(Morgan.Annotation.MARKED.toString(),
				new Long(2));
		morganList = morgan.getMorganList();
		assertEquals("marked benzene", new long[] { 1671552003L, 1671552004L,
				1671552008L, 1671552012L, 1671552021L, 4036216012L,
				4036216015L, 4036216024L, 4036216033L, 4036216036L },
				morganList);

		atomSets = morgan.getAtomSetList();
		assertEquals("markedBenzene", new String[][] { new String[] { "h5" },
				new String[] { "h6" }, new String[] { "h4" },
				new String[] { "h1", "h2" }, new String[] { "h3" },

				new String[] { "a5" }, new String[] { "a6" },
				new String[] { "a1", "a4" }, new String[] { "a2", },
				new String[] { "a3" }, }, atomSets);

		markedAtoms = morgan.getMarkedAtomList();
		// Assert.assertEquals("marked atoms", 7, markedAtoms.size());
		// for (CMLAtom atom : markedAtoms) {
		// System.out.println("A "+atom.getId());
		// }

		// ==========================

		benzene = (CMLMolecule) parseValidString(benzeneS);
		morgan = new Morgan(MoleculeTool.getOrCreateTool(benzene).getAtomSet());
		benzene.getAtom(1).setElementType(AS.Br.value);
		benzene.getAtom(5).setElementType(AS.Cl.value);
		morganList = morgan.getMorganList();
		assertEquals("marked benzene", new long[] { 1671552003L, 1671552004L,
				1671552008L, 1671552012L, 1671552021L, 4036216012L,
				4036216015L, 4036216024L, 4036216033L, 4036216036L },
				morganList);

		atomSets = morgan.getAtomSetList();
		assertEquals("markedBenzene", new String[][] { new String[] { "h5" },
				new String[] { "h6" }, new String[] { "h4" },
				new String[] { "h1", "h2" }, new String[] { "h3" },

				new String[] { "a5" }, new String[] { "a6" },
				new String[] { "a1", "a4" }, new String[] { "a2", },
				new String[] { "a3" }, }, atomSets);

		markedAtoms = morgan.getMarkedAtomList();
		// Assert.assertEquals("marked atoms", 7, markedAtoms.size());
		// for (CMLAtom atom : markedAtoms) {
		// System.out.println("A "+atom.getId());
		// }
	}

	private void assertEquals(String message, long[] test, List<Long> morganList) {
		Assert.assertNotNull("Morgan list should be set", morganList);
		Assert.assertEquals("equivalence classes", test.length, morganList
				.size());
		for (int i = 0; i < test.length; i++) {
			Assert.assertEquals("class " + i, test[i], morganList.get(i)
					.longValue());
		}
	}

	private void assertEquals(String message, String[][] test,
			List<CMLAtomSet> atomSets) {
		Assert.assertNotNull("AtomSets list should be set", atomSets);
		Assert
				.assertEquals("equivalence classes", test.length, atomSets
						.size());
		for (int i = 0; i < test.length; i++) {
			CMLAssert.assertEquals("class " + i, test[i], atomSets.get(i)
					.getXMLContent());
		}
	}

}
