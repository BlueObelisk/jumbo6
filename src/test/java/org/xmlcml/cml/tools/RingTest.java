/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.U_S;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLAtomSetTest;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLBondSetTest;
import org.xmlcml.euclid.test.StringTestBase;

/**
 * @author pm286
 *
 */
public class RingTest  {

	static String MOLECULE = "org"+U_S+"xmlcml"+U_S+"cml"+U_S+"tools"+U_S+"examples"+U_S+"molecules";

	List<Ring> ringList = null;
	Ring ring = null;
	RingNucleusTest ringNucleusTest = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ringNucleusTest = new RingNucleusTest();
		ringNucleusTest.makeMol();
		ringList = ringNucleusTest.nucleus0.getRings();
		ring = ringNucleusTest.nucleus1.getRings().get(0);
	}
	
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#Ring(org.xmlcml.cml.element.CMLAtomSet, org.xmlcml.cml.element.CMLBondSet)}.
	 */
	@Test
	@Ignore // test depends on set order
	public final void testRingCMLAtomSetCMLBondSet() {
		CMLAtomSet atomSet = new CMLAtomSet(ring.getAtomSet());
		CMLBondSet bondSet = new CMLBondSet(ring.getBondSet());
		// shuffle them a bit
		CMLAtom atom = atomSet.getAtoms().get(2);
		atomSet.removeAtom(atom);
		atomSet.addAtom(atom);
		atom = atomSet.getAtoms().get(3);
		atomSet.removeAtom(atom);
		atomSet.addAtom(atom);
		
		// shuffle them a bit
		CMLBond bond = bondSet.getBonds().get(2);
		bondSet.removeBond(bond);
		bondSet.addBond(bond);
		bond = bondSet.getBonds().get(3);
		bondSet.removeBond(bond);
		bondSet.addBond(bond);
		Ring newRing = new Ring(atomSet, bondSet);
		assertEqualCyclicAtomList("ring constructor", 
				new String[]{
			"a51", "a65", "a57", "a59", "a62"},
			newRing); //
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#Ring(org.xmlcml.cml.element.CMLBondSet)}.
	 */
	@Test
	@Ignore // depends on order
	public final void testRingCMLBondSet() {
		CMLBondSet bondSet = new CMLBondSet(ring.getBondSet());
		
		// shuffle them a bit
		CMLBond bond = bondSet.getBonds().get(2);
		bondSet.removeBond(bond);
		bondSet.addBond(bond);
		bond = bondSet.getBonds().get(3);
		bondSet.removeBond(bond);
		bondSet.addBond(bond);
		Ring newRing = new Ring(bondSet);
		assertEqualCyclicAtomList("ring constructor", 
				new String[]{
			"a51", "a65", "a57", "a59", "a62"},
			newRing);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getAtomSet()}.
	 */
	@Test
	@Ignore // order of sets is a problem
	public final void testGetAtomSet() {
		CMLAtomSet atomSet = new CMLAtomSet(ring.getAtomSet());
		CMLAtomSetTest.assertEquals("atom set", 
			new String[] {"a52", "a54", "a51", "a65", "a57"},
			atomSet);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getBondSet()}.
	 */
	
	@Test
	@Ignore // order of bonds is a problem
	public final void testGetBondSet() {
		CMLBondSet bondSet = new CMLBondSet(ring.getBondSet());
		CMLBondSetTest.assertEquals("bond set", 
			new String[] {"a57_a65", "a57_a59", "a59_a62", "a51_a62", "a51_a65"},
			bondSet);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getCyclicBondList()}.
	 */
	@Test
	@Ignore // order not predictable
	public final void testGetCyclicBondList() {
		List<CMLBond> bondList = ring.getCyclicBondList();
		List<String> bondIdList = new ArrayList<String>();
		for (CMLBond b : bondList) {
			bondIdList.add(b.getId());
		}
		
		Assert.assertEquals("cyclic bond list", 
			new String[] {"a57_a59", "a59_a62", "a51_a62", "a51_a65", "a57_a65"},
			bondIdList.toArray(new String[0]));

	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getCyclicAtomList()}.
	 */
	@Test
	@Ignore // depends on randome ordering
	public final void testGetCyclicAtomList() {
		List<CMLAtom> atomList = ring.getCyclicAtomList();
		List<String> atomIdList = new ArrayList<String>();
		for (CMLAtom a : atomList) {
			atomIdList.add(a.getId());
		}
		Assert.assertEquals("cyclic atom list", 
			new String[] {"a40", "a41", "a43", "a45", "a47", "a49"},
			atomIdList.toArray(new String[0]));
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getCyclicAtomIdList()}.
	 */
	@Test
	@Ignore // depends on randome ordering
	public final void testGetCyclicAtomIdList() {
		String[] atomIds = ring.getCyclicAtomIdList().toArray(new String[0]);
		StringTestBase.assertEquals("atomids", 
			new String[]{"a40", "a41", "a43", "a45", "a47", "a49"}, atomIds);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#size()}.
	 */
	@Test
	public final void testSize() {
		Assert.assertEquals("size", 5, ring.size());
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getCanonicalStartBond()}.
	 */
	@Test
	@Ignore // depends on order
	public final void testGetCanonicalStartBond() {
		CMLBond bond = ring.getCanonicalStartBond();
		Assert.assertEquals("canonical start bond", "a57_a59", bond.getId());
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Ring#getCanonicalStartAtom()}.
	 */
	@Test
	@Ignore // random result
	public final void testGetCanonicalStartAtom() {
		CMLAtom atom = ring.getCanonicalStartAtom();
		Assert.assertEquals("canonical start atom", "a59", atom.getId());
	}

	/** tests atom ids in ring
	 * order is important
	 * @param message 
	 * @param expectedAtomIds
	 * @param ring
	 */
	public static void assertEqualCyclicAtomList(String message,
			String[] expectedAtomIds, Ring ring) {
		String[] atomIds = (String[]) ring.getCyclicAtomIdList().toArray(new String[0]);
		StringTestBase.assertEquals(message, expectedAtomIds, atomIds);
	}

	/** tests bond ids in ring
	 * order is important
	 * @param message 
	 * @param expectedBondIds
	 * @param ring
	 */
	public static void assertEqualCyclicBondList(String message,
			String[] expectedBondIds, Ring ring) {
		String[] bondIds = (String[]) ring.getCyclicBondIdList().toArray(new String[0]);
		StringTestBase.assertEquals(message, expectedBondIds, bondIds);
	}

}
