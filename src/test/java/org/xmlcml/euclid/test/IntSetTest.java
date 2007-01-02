package org.xmlcml.euclid.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.IntSet;

/**
 * test IntSet.
 *
 * @author pmr
 *
 */
public class IntSetTest extends EuclidTest {

    IntSet i0;

    IntSet i1;

    IntSet i2;

    IntSet i3;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        i0 = new IntSet();
        i1 = new IntSet(new int[] { 3, 4, 1, 2 });
        i2 = new IntSet(4);
        i3 = new IntSet(2, 5);
    }

    /**
     * equality test. true if both args not null and equal
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     */
    public static void assertEquals(String msg, IntSet test, IntSet expected) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        IntTest.assertEquals(msg, test.getElements(), expected.getElements());
    }

    /**
     * equality test. true if both args not null and equal
     *
     * @param msg
     *            message
     * @param test
     * @param expected
     */
    public static void assertEquals(String msg, int[] test, IntSet expected) {
        Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
        Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
                expected);
        Assert.assertEquals("must be of equal length ", test.length, expected
                .getElements().length);
        IntTest.assertEquals(msg, test, expected.getElements());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.IntSet()'
     */
    @Test
    public void testIntSet() {
        Assert.assertEquals("empty", "()", i0.toString());
        Assert.assertFalse("int, int ", i0.contains(0));
        Assert.assertFalse("int, int ", i0.contains(1));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.IntSet(int)'
     */
    @Test
    public void testIntSetInt() {
        Assert.assertEquals("int[]", "(3,4,1,2)", i1.toString());
        Assert.assertFalse("int, int ", i1.contains(0));
        Assert.assertTrue("int, int ", i1.contains(1));
        Assert.assertTrue("int, int ", i1.contains(2));
        Assert.assertTrue("int, int ", i1.contains(3));
        Assert.assertTrue("int, int ", i1.contains(4));
        Assert.assertFalse("int, int ", i1.contains(5));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.IntSet(int, int)'
     */
    @Test
    public void testIntSetIntInt() {
        Assert.assertEquals("int", "(2,3,4,5)", i3.toString());
        Assert.assertFalse("int, int ", i3.contains(0));
        Assert.assertFalse("int, int ", i3.contains(1));
        Assert.assertTrue("int, int ", i3.contains(2));
        Assert.assertTrue("int, int ", i3.contains(3));
        Assert.assertTrue("int, int ", i3.contains(4));
        Assert.assertTrue("int, int ", i3.contains(5));
        Assert.assertFalse("int, int ", i3.contains(6));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.IntSet(IntSet)'
     */
    @Test
    public void testIntSetIntSet() {
        IntSet ii = new IntSet(i1);
        Assert.assertEquals("copy", "(3,4,1,2)", ii.toString());
        Assert.assertFalse("int, int ", ii.contains(0));
        Assert.assertTrue("int, int ", ii.contains(1));
        Assert.assertTrue("int, int ", ii.contains(2));
        Assert.assertTrue("int, int ", ii.contains(3));
        Assert.assertTrue("int, int ", ii.contains(4));
        Assert.assertFalse("int, int ", ii.contains(5));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.IntSet(int[])'
     */
    @Test
    public void testIntSetIntArray() {
        IntSetTest.assertEquals("int", new int[] { 0, 1, 2, 3 }, i2);
    }

    /**
     * Test method for
     * 'org.xmlcml.euclid.IntSet.IntSet.getSubcriptedIntSet(IntSet)'
     */
    @Test
    public void testIntSetIntSetIntSet() {
        IntSet is0 = null;
        try {
            is0 = new IntSet(new int[] { 0, 1, 2, 3 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSet is = null;
        try {
            is = i1.getSubscriptedIntSet(is0);
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSetTest.assertEquals("copy", new int[] { 3, 4, 1, 2 }, is);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.isEqualTo(IntSet)'
     */
    @Test
    public void testIsEqualTo() {
        Assert.assertTrue("isEqualsTo", i1.isEqualTo(i1));
        Assert.assertFalse("isEqualsTo", i1.isEqualTo(i2));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.getElements()'
     */
    @Test
    public void testGetElements() {
        IntTest.assertEquals("getElements", new int[] {}, i0.getElements());
        IntTest.assertEquals("getElements", new int[] { 3, 4, 1, 2 }, i1
                .getElements());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.setMax(int)'
     */
    @Test
    public void testSetMax() {
        i1.setMax(7);
        i1.addElement(6);
        IntSetTest.assertEquals("getElements", new int[] { 3, 4, 1, 2, 6 }, i1);
        i1.addElement(7);
        IntTest.assertEquals("getElements", new int[] { 3, 4, 1, 2, 6, 7 }, i1
                .getElements());
        try {
            i1.addElement(8);
        } catch (EuclidRuntimeException e) {
            Assert
                    .assertEquals(
                            "addElement",
                            "org.xmlcml.euclid.EuclidRuntimeException: value (8)outside range (-2147483648...7)",
                            S_EMPTY + e);
        }
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.setMin(int)'
     */
    @Test
    public void testSetMin() {
        i1.setMin(-3);
        i1.addElement(-2);
        IntSetTest
                .assertEquals("getElements", new int[] { 3, 4, 1, 2, -2 }, i1);
        i1.addElement(-3);
        IntSetTest.assertEquals("getElements",
                new int[] { 3, 4, 1, 2, -2, -3 }, i1);
        try {
            i1.addElement(-4);
        } catch (EuclidRuntimeException e) {
            Assert
                    .assertEquals(
                            "addElement",
                            "org.xmlcml.euclid.EuclidRuntimeException: value (-4)outside range (-3...2147483647)",
                            S_EMPTY + e);
        }
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.size()'
     */
    @Test
    public void testSize() {
        Assert.assertEquals("size", 4, i1.size());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.addElement(int)'
     */
    @Test
    public void testAddElement() {
        i1.addElement(6);
        IntSetTest.assertEquals("addElement", new int[] { 3, 4, 1, 2, 6 }, i1);
        try {
            i1.addElement(4);
        } catch (EuclidRuntimeException e) {
            Assert.assertEquals("addElement",
                    "org.xmlcml.euclid.EuclidRuntimeException: value already in set: 4",
                    S_EMPTY + e);
        }
        IntSetTest.assertEquals("addElement", new int[] { 3, 4, 1, 2, 6 }, i1);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.contains(int)'
     */
    @Test
    public void testContains() {
        Assert.assertTrue("contains", i1.contains(4));
        Assert.assertFalse("contains", i1.contains(5));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.elementAt(int)'
     */
    @Test
    public void testElementAt() {
        Assert.assertEquals("elementAt", 4, i1.elementAt(1));
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.getIntArray()'
     */
    @Test
    public void testGetIntArray() {
        IntTest.assertEquals("getIntArray", new int[] { 3, 4, 1, 2 }, i1
                .getIntArray().getArray());
        IntTest.assertEquals("getIntArray", new int[] {}, i0.getIntArray()
                .getArray());
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.sortAscending()'
     */
    @Test
    public void testSortAscending() {
        i1.sortAscending();
        IntSetTest.assertEquals("sort ascending", new int[] { 1, 2, 3, 4 }, i1);
        i0.sortAscending();
        IntSetTest.assertEquals("sort ascending", new int[] {}, i0);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.addSet(IntSet)'
     */
    @Test
    public void testAddSet() {
        try {
            i1.addSet(new IntSet(new int[] { 5, 19, 8, 33 }));
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSetTest.assertEquals("addSet",
                new int[] { 3, 4, 1, 2, 5, 19, 8, 33 }, i1);
        IntSetTest.assertEquals("addSet", new int[] { 0, 1, 2, 3 }, i2);
        IntSet newIs = null;
        try {
            newIs = new IntSet(new int[] { 3, 4, 5, 6 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        try {
            i2.addSet(newIs);
        } catch (EuclidRuntimeException e) {
            Assert.assertEquals("addSet",
                    "org.xmlcml.euclid.EuclidRuntimeException: duplicate element 3", S_EMPTY
                            + e);
        }
        IntSetTest.assertEquals("addSet", new int[] { 0, 1, 2, 3 }, i2);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.intersectionWith(IntSet)'
     */
    @Test
    public void testIntersectionWith() {
        IntSet is1 = null;
        IntSet is2 = null;
        try {
            is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
            is2 = new IntSet(new int[] { 4, 5, 6, 7, 3 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSet is = is1.intersectionWith((is2));
        IntSetTest.assertEquals("intersection", new int[] { 4, 5, 3 }, is);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.notIn(IntSet)'
     */
    @Test
    public void testNotIn() {
        IntSet is1 = null;
        IntSet is2 = null;
        try {
            is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
            is2 = new IntSet(new int[] { 4, 5, 6, 7, 3 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSet is = is1.notIn(is2);
        IntSetTest.assertEquals("notIn", new int[] { 1, 2 }, is);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.addRange(IntRange)'
     */
    @Test
    public void testAddRange() {
        IntSet is1 = null;
        try {
            is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        is1.addRange(new IntRange(-2, 0));
        IntSetTest.assertEquals("addRange", new int[] { 1, 2, 3, 4, 5, -2, -1,
                0 }, is1);
    }

    /**
     * Test method for 'org.xmlcml.euclid.IntSet.inverseMap()'
     */
    @Test
    public void testInverseMap() {
        IntSet is1 = null;
        try {
            is1 = new IntSet(new int[] { 4, 0, 1, 3, 2 });
        } catch (EuclidException e) {
            neverThrow(e);
        }
        IntSet is = is1.inverseMap();
        IntSetTest.assertEquals("inverse", new int[] { 1, 2, 4, 3, 0 }, is);
    }


}
