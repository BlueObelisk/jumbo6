/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;
import static org.xmlcml.cml.test.CMLAssert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author pm286
 * 
 */
public class JunctionTest {

	List<Ring> ringList = null;
	Ring ring = null;
	Ring ring0;
	Ring ring1;
	RingNucleusTest ringNucleusTest = null;
	Junction junction = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ringNucleusTest = new RingNucleusTest();
		ringNucleusTest.makeMol();
		ringList = ringNucleusTest.nucleus0.getRings();
		ring = ringNucleusTest.nucleus1.getRings().get(0);

		// should be a norbornane
		ring0 = ringNucleusTest.nucleus2.getRings().get(0);
		ring1 = ringNucleusTest.nucleus2.getRings().get(1);
		junction = Junction.createJunction(ring0, ring1);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#getBridgeAtomList()}
	 * .
	 */
	@Test
	@Ignore
	public final void testGetBridgeAtomList() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#isSpiro()}.
	 */
	@Test
	@Ignore
	public final void testIsSpiro() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#isFusion()}.
	 */
	@Test
	@Ignore
	public final void testIsFusion() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#isBridge()}.
	 */
	@Test
	@Ignore
	public final void testIsBridge() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#getCommonAtomSet()}.
	 */
	@Test
	@Ignore
	public final void testGetCommonAtomSet() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#getCommonBondSet()}.
	 */
	@Test
	@Ignore
	public final void testGetCommonBondSet() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Junction#getRingList()}.
	 */
	@Test
	@Ignore
	public final void testGetRingList() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.tools.Junction#createJunction(org.xmlcml.cml.tools.Ring, org.xmlcml.cml.tools.Ring)}
	 * .
	 */
	@Test
	@Ignore
	// depends on history
	public final void testCreateJunction() {
		// should be a norbornane
		assertEquals("junction atoms", new String[] { "a51", "a62", "a59" },
				junction.getCommonAtomSet());
		assertEquals("junction bonds", new String[] { "a51_a62", "a59_a62" },
				junction.getCommonBondSet());
	}

}
