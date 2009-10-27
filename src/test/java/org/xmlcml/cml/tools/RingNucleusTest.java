/**
 * 
 */
package org.xmlcml.cml.tools;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.util.TstUtils;

/**
 * @author pm286
 * 
 */
public class RingNucleusTest {

	static String MOLECULE = "org" +CMLConstants.U_S + "xmlcml" +CMLConstants.U_S + "cml" +CMLConstants.U_S
			+ "tools" +CMLConstants.U_S + "examples" +CMLConstants.U_S + "molecules";
	CMLMolecule molecule;
	RingNucleusSet ringNucleusSet;
	RingNucleus nucleus0;
	RingNucleus nucleus1;
	RingNucleus nucleus2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		makeMol();
	}

	void makeMol() {
		molecule = (CMLMolecule)TstUtils.parseValidFile(MOLECULE +CMLConstants.U_S + "bg2066.xml");
		ConnectionTableTool connectionTableTool = new ConnectionTableTool(
				molecule);
		ringNucleusSet = connectionTableTool.getRingNucleusSet();
		Iterator<RingNucleus> iterator = ringNucleusSet.iterator();
		nucleus0 = iterator.next();
		nucleus1 = iterator.next();
		nucleus2 = iterator.next();
	}

	void makeMol1() {
		molecule = (CMLMolecule)TstUtils.parseValidFile(MOLECULE +CMLConstants.U_S + "bv2018.xml");
		ConnectionTableTool connectionTableTool = new ConnectionTableTool(
				molecule);
		ringNucleusSet = connectionTableTool.getRingNucleusSet();
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.RingNucleus#getAtomSet()}.
	 */
	@Test
	public final void testGetAtomSet() {
		CMLAssert.assertEquals("ringNucleus0", new String[] { "a47", "a43", "a40", "a41",
				"a49", "a45" }, nucleus0.getAtomSet());
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.RingNucleus#getBondSet()}.
	 */
	@Test
	public final void testGetBondSet() {
		Assert.assertEquals("ringNucleus0", new String[] { "a40_a41",
				"a40_a49", "a41_a43", "a43_a45", "a45_a47", "a47_a49" },
				nucleus0.getBondSet().getXMLContent());
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.RingNucleus#getRings()}.
	 */
	@Test
	@Ignore
	// depends on randome ordering
	public final void testGetRings() {
		List<Ring> ringList = nucleus0.getRings();
		Assert.assertEquals("rings", 4, ringList.size());
		String[][] expected = new String[][] {
				{ "a34_a10", "a10_a9", "a2_a9", "a2_a8", "a34_a8" },
				{ "a20_a21", "a21_a23", "a23_a24", "a24_a26", "a26_a28",
						"a20_a28" },
				{ "a12_a14", "a14_a17", "a17_a20", "a20_a21", "a21_a23",
						"a23_a24", "a24_a26", "a26_a28", "a28_a29", "a12_a29" },
				{ "a12_a10", "a12_a14", "a14_a17", "a17_a20", "a20_a21",
						"a21_a23", "a23_a24", "a24_a26", "a26_a28", "a28_a29",
						"a29_a30", "a30_a31", "a34_a31", "a34_a10" }, };
		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}

		ringList = nucleus1.getRings();
		Assert.assertEquals("rings", 1, ringList.size());
		expected = new String[][] { new String[] { "a40_a41", "a41_a43",
				"a43_a45", "a45_a47", "a47_a49", "a40_a49" } };
		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}

		ringList = nucleus2.getRings();
		Assert.assertEquals("rings", 2, ringList.size());
		expected = new String[][] {
				new String[] { "a57_a59", "a57_a65", "a51_a65", "a51_a62",
						"a59_a62" },
				new String[] { "a52_a51", "a52_a54", "a57_a54", "a57_a59",
						"a59_a62", "a51_a62" }, };

		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.RingNucleus#getSetOfSmallestRings(boolean)}.
	 */
	@Test
	@Ignore
	// depends on randome ordering
	public final void testGetSetOfSmallestRings() {
		makeMol();
		boolean update = true;
		List<Ring> ringList = nucleus0.getSetOfSmallestRings(update);
		Assert.assertEquals("rings", 4, ringList.size());
		ringList = nucleus0.getRings();
		String[][] expected = new String[][] {
				new String[] { "a34_a10", "a10_a9", "a2_a9", "a2_a8", "a34_a8" },
				new String[] { "a12_a10", "a34_a10", "a34_a31", "a30_a31",
						"a29_a30", "a12_a29" },
				new String[] { "a12_a14", "a14_a17", "a17_a20", "a20_a28",
						"a28_a29", "a12_a29" },
				new String[] { "a20_a21", "a21_a23", "a23_a24", "a24_a26",
						"a26_a28", "a20_a28" }, };
		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}

		ringList = nucleus1.getSetOfSmallestRings(update);
		Assert.assertEquals("rings", 1, ringList.size());
		ringList = nucleus1.getRings();
		expected = new String[][] { new String[] { "a40_a41", "a41_a43",
				"a43_a45", "a45_a47", "a47_a49", "a40_a49" } };
		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}

		ringList = nucleus2.getSetOfSmallestRings(update);
		Assert.assertEquals("rings", 2, ringList.size());
		ringList = nucleus2.getRings();
		expected = new String[][] {
				new String[] { "a52_a51", "a52_a54", "a57_a54", "a57_a65",
						"a51_a65" },
				new String[] { "a57_a59", "a59_a62", "a51_a62", "a51_a65",
						"a57_a65" }

		};
		for (int i = 0; i < ringList.size(); i++) {
			Ring ring = ringList.get(i);
			RingTest.assertEqualCyclicBondList("ring ", expected[i], ring);
		}
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.RingNucleus#getJunctions()}.
	 */
	@Test
	public final void testGetJunctions() {
		makeMol();
		/* List<Ring> ringList = */nucleus0.getSetOfSmallestRings(true);
		List<Junction> junctionList;
		junctionList = nucleus2.getJunctions();
		makeMol1();
		Iterator<RingNucleus> iterator = ringNucleusSet.iterator();
		for (; iterator.hasNext();) {
			RingNucleus nucleus = iterator.next();
			junctionList = nucleus.getJunctions();
		}
		RingNucleus nucleus5 = ringNucleusSet.get(5);
		junctionList = nucleus5.getJunctions();
		Assert.assertEquals("junctions", 14, junctionList.size());
		// @SuppressWarnings("unused")
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.ConnectionTableTool#getRingNucleusSet()}.
	 */
	@Test
	public final void testGetRingNucleusSet() {
		CMLMolecule molecule = (CMLMolecule)TstUtils.parseValidFile(ConnectionTableToolTest.MOLECULES
				+CMLConstants.U_S + "bg2066.xml");
		ConnectionTableTool connectionTableTool = new ConnectionTableTool(
				molecule);
		RingNucleusSet ringNucleusSet = connectionTableTool.getRingNucleusSet();
		Assert.assertEquals("ring tools", 3, ringNucleusSet.size());
		String[][] expectedAtomIDs = new String[][] {
				new String[] { "a47", "a45", "a41", "a40", "a49", "a43" },
				new String[] { "a57", "a65", "a62", "a54", "a59", "a51", "a52" },
				new String[] { "a2", "a8", "a23", "a21", "a34", "a17", "a20",
						"a10", "a26", "a24", "a30", "a12", "a31", "a29", "a28",
						"a14", "a9" }, };
		Iterator<RingNucleus> iterator = ringNucleusSet.iterator();
		for (int i = 0; i < 3; i++) {
			CMLAtomSet atomSet = iterator.next().getAtomSet();
			CMLAssert.assertEquals("ringNucleus " + i, expectedAtomIDs[i], atomSet);
		}
		ringNucleusSet.first().getRings();
	}

}
