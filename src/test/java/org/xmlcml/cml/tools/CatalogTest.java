/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.AbstractTest;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.IndexableListManager;
import org.xmlcml.cml.element.CMLMap.Direction;

/**
 * @author pm286
 *
 */
public class CatalogTest extends AbstractTest {

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Catalog#CatalogTool(java.net.URL)}.
	 */
	@Test
	@Ignore
	public void testCatalogToolURL() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Catalog#CatalogTool(java.io.File)}.
	 */
	@Test
	@Ignore
	public void testCatalogToolFile() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Catalog#getCatalogList()}.
	 */
	@Test
	public void testGetTopCatalogManager() {
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		CMLList catalogList = catalogManager.getCatalogList();
		Assert.assertNotNull("catalogList", catalogList);
		String expectedS = "" +
		"<list xmlns='http://www.xml-cml.org/schema'>"+
		"  <scalar convention='cml:resourceUrl' dictRef='cml:dictionaryCatalog' dataType='xsd:string'>org/xmlcml/cml/element/examples/dict/catalog.xml</scalar>"+
		"  <scalar convention='cml:resourceUrl' dictRef='cml:moleculeCatalog' dataType='xsd:string'>org/xmlcml/cml/tools/examples/molecules/catalog.xml</scalar>"+
		"  <scalar convention='cml:resourceUrl' dictRef='cml:unitsCatalog' dataType='xsd:string'>org/xmlcml/cml/element/examples/units/catalog.xml</scalar>"+
		"  <scalar convention='cml:absoluteUrl' dictRef='cml:foo' dataType='xsd:string'>http://www.sf.net/projects/cml/org/xmlcml/cml/element/examples/foo/catalog.xml</scalar>"+
		"  <scalar convention='cml:relativeUrl' dictRef='cml:moleculeCatalog' dataType='xsd:string'>src/org/xmlcml/cml/tools/examples/molecules/catalog.xml</scalar>"+
		"</list>"+
				"";
		CMLList expected = (CMLList) parseValidString(expectedS);
		AbstractTest.assertEqualsCanonically("list", expected, catalogList, true);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Catalog#getTopCatalogTool()}.
	 */
	@Test
	public void testGetTopCatalogUrl() {
		URL topCatalogUrl = CatalogManager.getTopCatalogUrl();
		URL expectedUrl = null;
		try {
			expectedUrl = new File(System.getProperty("user.dir"), 
					F_S+CatalogManager.CATALOGLIST_XML).toURL();
		} catch (MalformedURLException e) {
			throw new CMLRuntimeException("Malformed url "+e);
		}
		// comment out appropriate tests according to where catalogList is
		// based on catalogList at top of project
		Assert.assertEquals("Top catalog url", expectedUrl.toString(), topCatalogUrl.toString());
		// based on catalogList at top of project
//		Assert.assertEquals("Top catalog url", expectedUrl.toString(), topCatalogUrl.toString());
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.Catalog#getCatalog(java.lang.String)}.
	 */
	@Test
	public void testGetCatalogToolFromListString() {
		// top catalogManager
		CatalogManager topCatalogManager = CatalogManager.getTopCatalogManager();
		// find the molecules catalogMap
		Catalog moleculeCatalog = topCatalogManager.
		    getCatalog(Catalog.MOLECULE_CATALOG);
		Assert.assertNotNull("moleculeCatalog", moleculeCatalog);
		CMLMap moleculeCatalogMap = moleculeCatalog.getCmlMap();
		Assert.assertNotNull("moleculeCatalogMap", moleculeCatalogMap);
		// all possible namespaces to directory mappings
		String expectedS = "" +
		"<map xmlns='http://www.xml-cml.org/schema'>"+
		"  <!-- DIRECTORY -->"+
		"  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/geom' role='cml:moleculeList' to='./geom.xml'/>"+
		"  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/frags' role='cml:fragmentList' to='./fragments/frags.xml'/>"+
		"  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/fragments' role='cml:fragmentList' to='./fragments'/>"+
		"  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/geom1' role='cml:moleculeList' to='./geom1'/>"+
		"</map>"+
				"";
		CMLMap expected = (CMLMap) parseValidString(expectedS);
		AbstractTest.assertEqualsCanonically("list", expected, moleculeCatalogMap, true);
		// find one specific namespace as string ...
		String ref = moleculeCatalogMap.getToRef("http://www.xml-cml.org/mols/geom1");
		Assert.assertEquals("ref for geom1", "./geom1", ref);
		// ... and link
		CMLLink link = moleculeCatalogMap.getLink("http://www.xml-cml.org/mols/geom1", Direction.FROM);
		Assert.assertNotNull("link for geom1", link);
		Assert.assertEquals("link for geom1", "./geom1", link.getTo());
		// make a valid url from it.
		URL moleculeCatalogUrl = CatalogUtil.getURLFromLink(moleculeCatalog, link);
        String expectedEnd = U_S+"org/xmlcml/cml/tools/examples/molecules/geom1";
		Assert.assertTrue("Geom url", moleculeCatalogUrl.toString().endsWith(expectedEnd));
		// this is where we would now get the moleculeList

		CMLMoleculeList moleculeList = (CMLMoleculeList) IndexableListManager.createFrom(moleculeCatalogUrl, CMLMoleculeList.class);
		Assert.assertNotNull("moleculeList", moleculeList);
		Assert.assertTrue("moleculeList", 30 <= moleculeList.getChildCount());
		CMLMolecule molecule = (CMLMolecule) moleculeList.getIndexableById("oh");
		Assert.assertNotNull("molecule", molecule);
		String ohS = ""+
		"<molecule id='oh' xmlns='http://www.xml-cml.org/schema'>"+
		"  <atomArray>"+
		"    <atom id='r1' elementType='R' x3='-0.482' y3='-0.028' z3='0.0'/>"+
		"    <atom id='a5' elementType='O' x3='-1.696' y3='0.546' z3='-0.0'/>"+
		"    <atom id='a11' elementType='H' x3='-2.49' y3='-0.0050' z3='0.0'/>"+
		"  </atomArray>"+
		"  <bondArray>"+
		"    <bond atomRefs2='r1 a5' order='1'/>"+
		"    <bond atomRefs2='a5 a11' order='1'/>"+
		"  </bondArray>"+
		"</molecule>"+
		"";
		CMLMolecule expectedMolecule = (CMLMolecule) parseValidString(ohS);
		AbstractTest.assertEqualsCanonically("list", expectedMolecule, molecule, true);
	}

	
}
