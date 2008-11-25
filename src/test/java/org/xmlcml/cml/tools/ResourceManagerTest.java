package org.xmlcml.cml.tools;

import java.io.File;

import junit.framework.Assert;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.tests.XOMTestCase;

import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.TstBase;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;


public class ResourceManagerTest {

	@Test
	public void testResourceManager() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		Document doc = null;
		try {
			doc = new CMLBuilder().build(mapFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element map = doc.getRootElement();
		if (!(map instanceof CMLMap)) throw new RuntimeException("bad catalog.xml");

		ResourceManager manager = new ResourceManager(mapFile.toURI());
		XOMTestCase.assertEquals("", map, manager.getCmlMap());
	}
	
	
	@Test
	public void testGetUniqueID() {
		String foo = "foo";
		Assert.assertEquals("foo", ResourceManager.getUniqueID(foo));
		String bar = "bar.xml";
		Assert.assertEquals("bar", ResourceManager.getUniqueID(bar));
	}
	
	
	@Test
	public void testIndex() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		ResourceManager manager = new ResourceManager(mapFile.toURI());
		Assert.assertNotNull(manager.getIndex("http://www.xml-cml.org/mols/geom1"));
	}
	
	
	@Test
	public void testGetResourceByUid() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		ResourceManager manager = new ResourceManager(mapFile.toURI());
		CMLMolecule ethyl = (CMLMolecule) manager.getResourceByUID("http://www.xml-cml.org/mols/geom1", "ethyl");
		Assert.assertEquals(8, ethyl.getAtomArray().size());
	}
	
	
	@Test
	public void testDerefByUID() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		ResourceManager manager = new ResourceManager(mapFile.toURI());
		
		CMLMolecule molecule = new CMLMolecule();
		molecule.addNamespaceDeclaration("g", "http://www.xml-cml.org/mols/geom1");
		molecule.setRef("g:ethyl");
		
		CMLMolecule deref = (CMLMolecule) manager.deref(molecule, ResourceManager.idTypes.UID);
		Assert.assertEquals(8, deref.getAtomArray().size());
	}
	
	
	@Test
	public void testGetResourceById() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		ResourceManager manager = new ResourceManager(mapFile.toURI());
		CMLMolecule et = (CMLMolecule) manager.getResourceByID("http://www.xml-cml.org/mols/geom1", "et");
		Assert.assertEquals(8, et.getAtomArray().size());
	}
	
	
	@Test
	public void testDerefByID() {
		File mapFile = new File("src/test/resources/org/xmlcml/cml/tools/examples/molecules/catalog.xml");
		ResourceManager manager = new ResourceManager(mapFile.toURI());
		
		CMLMolecule molecule = new CMLMolecule();
		molecule.addNamespaceDeclaration("g", "http://www.xml-cml.org/mols/geom1");
		molecule.setRef("g:et");
		
		CMLMolecule deref = (CMLMolecule) manager.deref(molecule, ResourceManager.idTypes.ID);
		Assert.assertEquals(8, deref.getAtomArray().size());
	}
	
}
