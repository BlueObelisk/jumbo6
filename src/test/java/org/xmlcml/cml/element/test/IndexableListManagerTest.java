/**
 * 
 */
package org.xmlcml.cml.element.test;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.Indexable;
import org.xmlcml.cml.element.IndexableListManager;

/**
 * @author pm286
 * CMLMolecule and CMLMoleculeList are used as the test classes
 */
public class IndexableListManagerTest extends AbstractTest {

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#IndexableListManager(org.xmlcml.cml.element.IndexableList)}.
	 */
	@Test
	public void testIndexableListManager() {
		CMLMoleculeList moleculeList = new CMLMoleculeList();
		CMLMolecule molecule = new CMLMolecule();
		molecule.setId("m1");
		moleculeList.addIndexable(molecule);
		molecule = new CMLMolecule();
		molecule.setId("m2");
		moleculeList.addIndexable(molecule);
		Map <String, Indexable> map = moleculeList.getIndex();
		Assert.assertNotNull("index", map);
		Assert.assertEquals("index", 2, map.size());
		molecule = (CMLMolecule) map.get("m2");
		Assert.assertEquals("index", "m2", molecule.getId());
		String moleculeListS = ""+
		"<moleculeList xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule id='m1'/>"+
		"  <molecule id='m2'/>"+
		"</moleculeList>";
		CMLMoleculeList moleculeListE = (CMLMoleculeList) parseValidString(moleculeListS);
		assertEqualsCanonically("moleculeList", moleculeListE, moleculeList, true);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#createFrom(java.net.URL, java.lang.Class)}.
	 */
	@Test
	@Ignore
	public void testCreateFrom() {
		fail("Not yet implemented - requires directory fixtures");
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#getIndex()}.
	 */
	@Test
	public void testGetIndex() {
		// see above
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#add(org.xmlcml.cml.element.Indexable)}.
	 */
	@Test
	public void testAdd() {
		CMLMoleculeList moleculeList = new CMLMoleculeList();
		CMLMolecule molecule = new CMLMolecule();
		molecule.setId("m1");
		moleculeList.addIndexable(molecule);
		molecule = new CMLMolecule();
		molecule.setId("m2");
		moleculeList.addIndexable(molecule);
		Map <String, Indexable> map = moleculeList.getIndex();
		Assert.assertNotNull("index", map);
		Assert.assertEquals("index", 2, map.size());
		molecule = (CMLMolecule) map.get("m2");
		Assert.assertEquals("index", "m2", molecule.getId());
		String moleculeListS = ""+
		"<moleculeList xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule id='m1'/>"+
		"  <molecule id='m2'/>"+
		"</moleculeList>";
		CMLMoleculeList moleculeListE = (CMLMoleculeList) parseValidString(moleculeListS);
		assertEqualsCanonically("moleculeList", moleculeListE, moleculeList, true);
		
		IndexableListManager indexableListManager = moleculeList.getIndexableListManager();
		Assert.assertNotNull("index", indexableListManager);
		CMLMolecule molecule3 = new CMLMolecule();
		molecule3.setId("m3");
		indexableListManager.add(molecule3);
		moleculeListS = ""+
		"<moleculeList xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule id='m1'/>"+
		"  <molecule id='m2'/>"+
		"  <molecule id='m3'/>"+
		"</moleculeList>";
		moleculeListE = (CMLMoleculeList) parseValidString(moleculeListS);
		assertEqualsCanonically("moleculeList", moleculeListE, moleculeList, true);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#remove(org.xmlcml.cml.element.Indexable)}.
	 */
	@Test
	public void testRemoveIndexable() {
		CMLMoleculeList moleculeList = new CMLMoleculeList();
		CMLMolecule molecule1 = new CMLMolecule();
		molecule1.setId("m1");
		moleculeList.addIndexable(molecule1);
		CMLMolecule molecule2 = new CMLMolecule();
		molecule2.setId("m2");
		moleculeList.addIndexable(molecule2);
		moleculeList.removeIndexable(molecule1);
		Map <String, Indexable> map = moleculeList.getIndex();
		CMLMolecule molecule = (CMLMolecule) map.get("m2");
		Assert.assertNotNull("index", molecule);
		Assert.assertEquals("index", "m2", molecule.getId());
		molecule = (CMLMolecule) map.get("m1");
		Assert.assertNull("index", molecule);
		String moleculeListS = ""+
		"<moleculeList xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule id='m2'/>"+
		"</moleculeList>";
		CMLMoleculeList moleculeListE = (CMLMoleculeList) parseValidString(moleculeListS);
		assertEqualsCanonically("moleculeList", moleculeListE, moleculeList, true);
		moleculeList.addIndexable(molecule1);
		molecule = (CMLMolecule) map.get("m1");
		Assert.assertNotNull("index", molecule);
		Assert.assertEquals("index", "m1", molecule.getId());
		moleculeListS = ""+
		"<moleculeList xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule id='m2'/>"+
		"  <molecule id='m1'/>"+
		"</moleculeList>";
		moleculeListE = (CMLMoleculeList) parseValidString(moleculeListS);
		assertEqualsCanonically("moleculeList", moleculeListE, moleculeList, true);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.element.IndexableListManager#getById(java.lang.String)}.
	 */
	@Test
	public void testGetById() {
		CMLMoleculeList moleculeList = new CMLMoleculeList();
		CMLMolecule molecule1 = new CMLMolecule();
		molecule1.setId("m1");
		moleculeList.addIndexable(molecule1);
		CMLMolecule molecule2 = new CMLMolecule();
		molecule2.setId("m2");
		moleculeList.addIndexable(molecule2);
		
		IndexableListManager indexableListManager = moleculeList.getIndexableListManager();
		Assert.assertNotNull("index", indexableListManager);
		CMLMolecule molecule = (CMLMolecule) indexableListManager.getById("m2");
		Assert.assertNotNull("molecule", molecule);
		Assert.assertEquals("molecule", "m2", molecule.getId());
	}

}
