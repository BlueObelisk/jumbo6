/**
 * 
 */
package org.xmlcml.cml.tools.test;

import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.IndexableList;
import org.xmlcml.cml.element.test.AbstractTest;
import org.xmlcml.cml.tools.Catalog;
import org.xmlcml.cml.tools.CatalogManager;
import org.xmlcml.cml.tools.FragmentTool;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class FragmentToolTest extends AbstractTest {

	String molecule1S = 
	"    <molecule id='id1' "+CML_XMLNS+">" +
	"      <atomArray>" +
	"        <atom id='r1' elementType='R'/>"+
	"        <atom id='a1' elementType='C'/>"+
	"        <atom id='a2' elementType='O'/>"+
	"        <atom id='r2' elementType='R'/>"+
	"      </atomArray>" +
	"      <bondArray>" +
	"        <bond id='b1' atomRefs2='a1 r1'/>"+
	"        <bond id='b2' atomRefs2='a1 a2'/>"+
	"        <bond id='b3' atomRefs2='r2 a2'/>"+
	"      </bondArray>" +
	"    </molecule>" +
	"";
	CMLMolecule molecule1 = null;

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#setMolecule(org.xmlcml.cml.element.CMLMolecule)}.
	 */
	@Test
	public void testGetAndSetMolecule() {
		CMLFragment fragment = new CMLFragment();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		CMLMolecule molecule = null;
		CMLMolecule fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNull("fragment molecule", fragmentMolecule);
		
		molecule = new CMLMolecule();
		molecule.setId("foo");
		fragmentTool.setMolecule(molecule);
		fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNotNull("fragment molecule", fragmentMolecule);
		Assert.assertEquals("fragment molecule", "foo", fragmentMolecule.getId());
		molecule = new CMLMolecule();
		molecule.setId("bar");
		fragmentTool.setMolecule(molecule);
		fragmentMolecule = fragmentTool.getMolecule();
		Assert.assertNotNull("fragment molecule", fragmentMolecule);
		Assert.assertEquals("fragment molecule", "bar", fragmentMolecule.getId());
		Assert.assertEquals("fragment molecule", 1, fragment.getChildCMLElements(CMLMolecule.TAG).size());
		
	}

	private CMLFragment makeMol() {
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:oh'/>" +
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='g:benzene'/>"+
		"  </fragment>"+
		"  <join atomRefs2='r3 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='g:cl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessBasic() {
		CMLFragment fragment = makeMol();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <molecule ref='g:oh' id='1'>"+
		"    <arg name='idx'>"+
		"      <scalar dataType='xsd:string'>1</scalar>"+
		"    </arg>"+
		"  </molecule>"+
		"  <join atomRefs2='g:oh_1_r1 g:benzene_2_r1' moleculeRefs2='g:oh_1 g:benzene_2'/>"+
		"  <molecule ref='g:benzene' id='2'>"+
		"    <arg name='idx'>"+
		      "<scalar dataType='xsd:string'>2</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='g:benzene_2_r3 g:cl_3_r1' moleculeRefs2='g:benzene_2 g:cl_3'/>"+
		  "<molecule ref='g:cl' id='3'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>3</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		"</fragment>";
		CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
		AbstractTest.assertEqualsCanonically("fragment", intermediate, fragment, true);
		outputXML("intermediate", intermediate);
	}
	
	private void outputXML(String name, CMLElement element) {
		try {
			OutputStream os = new FileOutputStream(Util.getTEMP_DIRECTORY()+F_S+name+XML_SUFF);
			element.serialize(os, 0);
		} catch (Exception e) {
			throw new CMLRuntimeException("Cannot ouput file: "+e);
		}
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessIntermediate() {
		CMLFragment fragment = makeMol();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		List<String> prefixes = CMLUtil.getPrefixes(fragment, "ref");
		Assert.assertEquals("prefixes", 1, prefixes.size());
		Assert.assertEquals("prefixes", "g", prefixes.get(0));
		List<CMLNamespace> namespaces = CMLUtil.getNamespaces(fragment, prefixes);
		Assert.assertEquals("prefixes", 1, namespaces.size());
		CMLNamespace namespace = namespaces.get(0);
		Assert.assertEquals("prefixes", "http://www.xml-cml.org/mols/geom1", namespace.getNamespaceURI());
		Assert.assertEquals("prefixes", "g", namespace.getPrefix());
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		CMLMap cmlMap = moleculeCatalog.getCmlMap();
		String mapS = "" +
			  "<map xmlns='http://www.xml-cml.org/schema'>"+
			  "<!-- DIRECTORY -->"+
			  "  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/geom' " +
			  "    role='cml:moleculeList' to='./geom.xml'/>"+
			  "  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/frags' " +
			  "    role='cml:fragmentList' to='./fragments/frags.xml'/>"+
			  "  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/fragments' " +
			  "    role='cml:fragmentList' to='./fragments'/>"+
			  "  <link convention='cml:relativeUrl' from='http://www.xml-cml.org/mols/geom1' " +
			  "    role='cml:moleculeList' to='./geom1'/>"+
			  "</map>";
		CMLMap mapE = (CMLMap) parseValidString(mapS);
		assertEqualsCanonically("map", mapE, cmlMap, true);

		IndexableList moleculeList = moleculeCatalog.getIndexableList(
				namespace, IndexableList.Type.MOLECULE_LIST);
		Assert.assertNotNull("moleculeList", moleculeList);
		Assert.assertEquals("moleculeList", 30, moleculeList.getIndex().size());
		
		fragmentTool.processIntermediate(moleculeCatalog);
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		   "<molecule role='fragment' id='oh_1'>"+
		     "<atomArray>"+
		       "<atom elementType='R' x3='-1.0360501610646575' y3='0.23396440893831422' z3='0.0' id='oh_1_r1'/>"+
		       "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='oh_1_a5'/>"+
		       "<atom elementType='H' x3='-2.49' y3='-0.0050' z3='0.0' id='oh_1_a11'/>"+
		     "</atomArray>"+
		     "<bondArray>"+
		       "<bond order='1' id='oh_1_r1_oh_1_a5' atomRefs2='oh_1_r1 oh_1_a5'/>"+
		       "<bond order='1' id='oh_1_a5_oh_1_a11' atomRefs2='oh_1_a5 oh_1_a11'/>"+
		     "</bondArray>"+
		   "</molecule>"+
		 "<join atomRefs2='g:oh_1_r1 g:benzene_2_r1' moleculeRefs2='g:oh_1 g:benzene_2' order='S'/>"+
		   "<molecule role='fragment' id='benzene_2'>"+
		     "<atomArray>"+
		       "<atom elementType='C' x3='9.526706134000763' y3='3.869733600000001' z3='5.213518402229052' id='benzene_2_a1'>"+
		         "<label dictRef='cml:torsionEnd'>r6</label>"+
		       "</atom>"+
		       "<atom elementType='R' x3='9.59902744852545' y3='4.430492482197403' z3='4.690814754353756' id='benzene_2_r1'/>"+
		       "<atom elementType='C' x3='10.243299413197152' y3='3.932398500000001' z3='6.439022942911609' id='benzene_2_a2'>"+
		         "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "</atom>"+
		       "<atom elementType='C' x3='8.713504556428543' y3='2.7185301000000006' z3='5.01720505576243' id='benzene_2_a6'>"+
		         "<label dictRef='cml:torsionEnd'>r5</label>"+
		       "</atom>"+
		       "<atom elementType='R' x3='8.385888936961882' y3='2.655387420737078' z3='4.323244676535362' id='benzene_2_r6'/>"+
		       "<atom elementType='C' x3='10.119474056141831' y3='2.9008920000000007' z3='7.3834992125284815' id='benzene_2_a3'>"+
		         "<label dictRef='cml:torsionEnd'>r2</label>"+
		       "</atom>"+
		       "<atom elementType='R' x3='10.509388371349742' y3='2.947556560950007' z3='8.045835415334345' id='benzene_2_r3'/>"+
		       "<atom elementType='C' x3='9.320371405363035' y3='1.8151698000000005' z3='7.151684115065878' id='benzene_2_a4'>"+
		         "<label dictRef='cml:torsionEnd'>r3</label>"+
		       "</atom>"+
		       "<atom elementType='R' x3='9.280916015724046' y3='1.2657016684721403' z3='7.6896692864820775' id='benzene_2_r4'/>"+
		       "<atom elementType='C' x3='8.610030693701125' y3='1.7243409000000007' z3='5.934289686115539' id='benzene_2_a5'>"+
		         "<label dictRef='cml:torsionEnd'>r4</label>"+
		       "</atom>"+
		       "<atom elementType='R' x3='8.16522311995263' y3='1.112386804675892' z3='5.790907652540795' id='benzene_2_r5'/>"+
		       "<atom elementType='R' x3='10.697234803620145' y3='4.543958438540135' z3='6.552323882423661' id='benzene_2_r2'/>"+
		     "</atomArray>"+
		     "<bondArray>"+
		       "<bond order='1' id='benzene_2_a1_benzene_2_r1' atomRefs2='benzene_2_a1 benzene_2_r1'/>"+
		       "<bond order='2' id='benzene_2_a1_benzene_2_a2' atomRefs2='benzene_2_a1 benzene_2_a2'/>"+
		       "<bond order='1' id='benzene_2_a1_benzene_2_a6' atomRefs2='benzene_2_a1 benzene_2_a6'/>"+
		       "<bond order='1' id='benzene_2_a3_benzene_2_a2' atomRefs2='benzene_2_a3 benzene_2_a2'/>"+
		       "<bond order='1' id='benzene_2_a2_benzene_2_r2' atomRefs2='benzene_2_a2 benzene_2_r2'/>"+
		       "<bond order='1' id='benzene_2_r6_benzene_2_a6' atomRefs2='benzene_2_r6 benzene_2_a6'/>"+
		       "<bond order='2' id='benzene_2_a5_benzene_2_a6' atomRefs2='benzene_2_a5 benzene_2_a6'/>"+
		       "<bond order='1' id='benzene_2_a3_benzene_2_r3' atomRefs2='benzene_2_a3 benzene_2_r3'/>"+
		       "<bond order='2' id='benzene_2_a3_benzene_2_a4' atomRefs2='benzene_2_a3 benzene_2_a4'/>"+
		       "<bond order='1' id='benzene_2_a5_benzene_2_a4' atomRefs2='benzene_2_a5 benzene_2_a4'/>"+
		       "<bond order='1' id='benzene_2_a4_benzene_2_r4' atomRefs2='benzene_2_a4 benzene_2_r4'/>"+
		       "<bond order='1' id='benzene_2_a5_benzene_2_r5' atomRefs2='benzene_2_a5 benzene_2_r5'/>"+
		     "</bondArray>"+
		   "</molecule>"+
		 "<join atomRefs2='g:benzene_2_r3 g:cl_3_r1' moleculeRefs2='g:benzene_2 g:cl_3' order='S'/>"+
		   "<molecule role='fragment' id='cl_3'>"+
		     "<atomArray>"+
		       "<atom elementType='Cl' x3='1.998' y3='-0.064' z3='-0.0' formalCharge='0' hydrogenCount='0' id='cl_3_a1'/>"+
		       "<atom title='H' elementType='R' formalCharge='0' hydrogenCount='0' x3='1.195035329805453' y3='0.5150921674650439' z3='0.0' id='cl_3_r1'/>"+
		     "</atomArray>"+
		     "<bondArray>"+
		       "<bond order='S' id='cl_3_a1_cl_3_r1' atomRefs2='cl_3_a1 cl_3_r1'/>"+
		     "</bondArray>"+
		   "</molecule>"+
	   "</fragment>";
		CMLFragment explicit = (CMLFragment) parseValidString(explicitS);
		AbstractTest.assertEqualsCanonically("fragment", explicit, fragment, true);
		outputXML("explicit", explicit);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessExplicit() {
		CMLFragment fragment = makeMol();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		
		fragmentTool.processIntermediate(moleculeCatalog);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();

		String explicitS =
		"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='oh_1'>"+
		   "<atomArray>"+
		     "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='oh_1_a5'/>"+
		     "<atom elementType='H' x3='-2.49' y3='-0.0050' z3='0.0' id='oh_1_a11'/>"+
		     "<atom elementType='C' id='benzene_2_a1' x3='-0.3399386871191592' y3='-0.09516902272949146' z3='5.551115123125783E-17'>"+
		       "<label dictRef='cml:torsionEnd'>r6</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='benzene_2_a2' x3='0.7914362865750619' y3='0.6156438310221752' z3='0.4837664235504754'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='benzene_2_a6' x3='-0.09579880080599135' y3='-1.4070901918499383' z3='-0.49433166609449225'>"+
		       "<label dictRef='cml:torsionEnd'>r5</label>"+
		     "</atom>"+
		     "<atom elementType='R' id='benzene_2_r6' x3='-0.698091626839443' y3='-1.7584028345346565' z3='-0.8210161909782372'/>"+
		     "<atom elementType='C' id='benzene_2_a3' x3='2.0613739323115183' y3='0.016966666619527337' z3='0.46900731701644327'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='benzene_2_a4' x3='2.2457866247567373' y3='-1.2502340885616587' z3='-0.011919066538743861'>"+
		       "<label dictRef='cml:torsionEnd'>r3</label>"+
		     "</atom>"+
		     "<atom elementType='R' id='benzene_2_r4' x3='2.957251096418246' y3='-1.5447068498525005' z3='-0.00989461077069631'/>"+
		     "<atom elementType='C' id='benzene_2_a5' x3='1.138157602569676' y3='-1.970314839903435' z3='-0.511443512501981'>"+
		       "<label dictRef='cml:torsionEnd'>r4</label>"+
		     "</atom>"+
		     "<atom elementType='R' id='benzene_2_r5' x3='1.233840599902544' y3='-2.680590357693569' z3='-0.7929634102878301'/>"+
		     "<atom elementType='R' id='benzene_2_r2' x3='0.6752321872177487' y3='1.3316241226937084' z3='0.7421630745165873'/>"+
		     "<atom elementType='Cl' formalCharge='0' hydrogenCount='0' id='cl_3_a1' x3='3.4445888544438903' y3='0.9157252963063057' z3='1.0826435323377714'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='oh_1_a5_oh_1_a11' atomRefs2='oh_1_a5 oh_1_a11'/>"+
		     "<bond order='2' id='benzene_2_a1_benzene_2_a2' atomRefs2='benzene_2_a1 benzene_2_a2'/>"+
		     "<bond order='1' id='benzene_2_a1_benzene_2_a6' atomRefs2='benzene_2_a1 benzene_2_a6'/>"+
		     "<bond order='1' id='benzene_2_a3_benzene_2_a2' atomRefs2='benzene_2_a3 benzene_2_a2'/>"+
		     "<bond order='1' id='benzene_2_a2_benzene_2_r2' atomRefs2='benzene_2_a2 benzene_2_r2'/>"+
		     "<bond order='1' id='benzene_2_r6_benzene_2_a6' atomRefs2='benzene_2_r6 benzene_2_a6'/>"+
		     "<bond order='2' id='benzene_2_a5_benzene_2_a6' atomRefs2='benzene_2_a5 benzene_2_a6'/>"+
		     "<bond order='2' id='benzene_2_a3_benzene_2_a4' atomRefs2='benzene_2_a3 benzene_2_a4'/>"+
		     "<bond order='1' id='benzene_2_a5_benzene_2_a4' atomRefs2='benzene_2_a5 benzene_2_a4'/>"+
		     "<bond order='1' id='benzene_2_a4_benzene_2_r4' atomRefs2='benzene_2_a4 benzene_2_r4'/>"+
		     "<bond order='1' id='benzene_2_a5_benzene_2_r5' atomRefs2='benzene_2_a5 benzene_2_r5'/>"+
		     "<bond atomRefs2='oh_1_a5 benzene_2_a1' order='S' id='oh_1_a5_benzene_2_a1'/>"+
		     "<bond atomRefs2='benzene_2_a3 cl_3_a1' order='S' id='benzene_2_a3_cl_3_a1'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment fragmentE = (CMLFragment) parseValidString(explicitS);
		assertEqualsCanonically("explicit", fragmentE, fragment, true);
		outputXML("complete", fragment);
	}

	private CMLFragment makeMol1() {
		String fragmentS = "" +
		"<fragment convention='cml:PML-intermediate' " +
		"   xmlns='http://www.xml-cml.org/schema'" +
		"   xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:2pyr'/>" +
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment countExpression='*(2)'>"+
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"    <fragment>" +
		"      <molecule ref='g:po'/>" +
		"    </fragment>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='g:acetyl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * Tests fragmentList expansion
	 * form A-j-B-j-C
	 */
	@Test
	public void testProcessBasic1() {
		CMLFragment fragment = makeMol1();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:2pyr' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_1_r1 g:po_2_r2' moleculeRefs2='g:2pyr_1 g:po_2'/>"+
		 "<molecule ref='g:po' id='2'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_2_r1 g:po_3_r2' moleculeRefs2='g:po_2 g:po_3'/>"+
		 "<molecule ref='g:po' id='3'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>3</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_3_r1 g:acetyl_4_r1' moleculeRefs2='g:po_3 g:acetyl_4'/>"+
		 "<molecule ref='g:acetyl' id='4'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>4</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
		AbstractTest.assertEqualsCanonically("fragment", intermediate, fragment, true);
		outputXML("intermediate1", intermediate);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessIntermediate1() {
		CMLFragment fragment = makeMol1();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		fragmentTool.processIntermediate(moleculeCatalog);
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='2pyr_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'> </atom>"+
		     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_1_r1'> </atom>"+
		     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'> </atom>"+
		     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
		     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'> </atom>"+
		     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'> </atom>"+
		     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
		     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'> </atom>"+
		     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
		     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'> </atom>"+
		     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_1_r6'> </atom>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='2pyr_1_r1_2pyr_1_a1' atomRefs2='2pyr_1_r1 2pyr_1_a1'/>"+
		     "<bond order='2' id='2pyr_1_a1_2pyr_1_a2' atomRefs2='2pyr_1_a1 2pyr_1_a2'/>"+
		     "<bond order='1' id='2pyr_1_a1_2pyr_1_a3' atomRefs2='2pyr_1_a1 2pyr_1_a3'/>"+
		     "<bond order='1' id='2pyr_1_a2_2pyr_1_r2' atomRefs2='2pyr_1_a2 2pyr_1_r2'/>"+
		     "<bond order='1' id='2pyr_1_a2_2pyr_1_a4' atomRefs2='2pyr_1_a2 2pyr_1_a4'/>"+
		     "<bond order='2' id='2pyr_1_a6_2pyr_1_a3' atomRefs2='2pyr_1_a6 2pyr_1_a3'/>"+
		     "<bond order='1' id='2pyr_1_a4_2pyr_1_r4' atomRefs2='2pyr_1_a4 2pyr_1_r4'/>"+
		     "<bond order='2' id='2pyr_1_a4_2pyr_1_a5' atomRefs2='2pyr_1_a4 2pyr_1_a5'/>"+
		     "<bond order='1' id='2pyr_1_a5_2pyr_1_r5' atomRefs2='2pyr_1_a5 2pyr_1_r5'/>"+
		     "<bond order='1' id='2pyr_1_a5_2pyr_1_a6' atomRefs2='2pyr_1_a5 2pyr_1_a6'/>"+
		     "<bond order='1' id='2pyr_1_a6_2pyr_1_r6' atomRefs2='2pyr_1_a6 2pyr_1_r6'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_1_r1 g:po_2_r2' moleculeRefs2='g:2pyr_1 g:po_2' order='S'/>"+
		 "<molecule role='fragment' id='po_2'>"+
		   "<atomArray>"+
		     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_2_r1'/>"+
		     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_2_a2'/>"+
		     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_2_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_2_a4'/>"+
		     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_2_a5'/>"+
		     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_2_a7'/>"+
		     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_2_a8'/>"+
		     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_2_a9'/>"+
		     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_2_a10'/>"+
		     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_2_a11'/>"+
		     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_2_a12'/>"+
		     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_2_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='po_2_r1_po_2_a2' atomRefs2='po_2_r1 po_2_a2'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a3' atomRefs2='po_2_a2 po_2_a3'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a7' atomRefs2='po_2_a2 po_2_a7'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a8' atomRefs2='po_2_a2 po_2_a8'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a4' atomRefs2='po_2_a3 po_2_a4'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a5' atomRefs2='po_2_a3 po_2_a5'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a9' atomRefs2='po_2_a3 po_2_a9'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a10' atomRefs2='po_2_a4 po_2_a10'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a11' atomRefs2='po_2_a4 po_2_a11'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a12' atomRefs2='po_2_a4 po_2_a12'/>"+
		     "<bond order='1' id='po_2_a5_po_2_r2' atomRefs2='po_2_a5 po_2_r2'/>"+
		   "</bondArray>"+
		   "<length id='po_2_len23' atomRefs2='po_2_a2 po_2_a3'/>"+
		   "<angle id='po_2_ang234' atomRefs3='po_2_a2 po_2_a3 po_2_a4'/>"+
		   "<angle id='po_2_ang123' atomRefs3='po_2_r1 po_2_a2 po_2_a3'/>"+
		   "<angle id='po_2_ang352' atomRefs3='po_2_a3 po_2_a5 po_2_r2'/>"+
		   "<torsion id='po_2_tor1' atomRefs4='po_2_r1 po_2_a2 po_2_a3 po_2_a5'/>"+
		   "<torsion id='po_2_tor2' atomRefs4='po_2_a2 po_2_a3 po_2_a5 po_2_r2'/>"+
		   "<arg parameterName='len23'/>"+
		   "<arg parameterName='ang234'/>"+
		   "<arg parameterName='ang123'/>"+
		   "<arg parameterName='ang352'/>"+
		   "<arg parameterName='tor1'/>"+
		   "<arg parameterName='tor2'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_2_r1 g:po_3_r2' moleculeRefs2='g:po_2 g:po_3' order='S'/>"+
		 "<molecule role='fragment' id='po_3'>"+
		   "<atomArray>"+
		     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_3_r1'/>"+
		     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_3_a2'/>"+
		     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_3_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_3_a4'/>"+
		     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_3_a5'/>"+
		     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_3_a7'/>"+
		     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_3_a8'/>"+
		     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_3_a9'/>"+
		     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_3_a10'/>"+
		     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_3_a11'/>"+
		     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_3_a12'/>"+
		     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_3_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='po_3_r1_po_3_a2' atomRefs2='po_3_r1 po_3_a2'/>"+
		     "<bond order='1' id='po_3_a2_po_3_a3' atomRefs2='po_3_a2 po_3_a3'/>"+
		     "<bond order='1' id='po_3_a2_po_3_a7' atomRefs2='po_3_a2 po_3_a7'/>"+
		     "<bond order='1' id='po_3_a2_po_3_a8' atomRefs2='po_3_a2 po_3_a8'/>"+
		     "<bond order='1' id='po_3_a3_po_3_a4' atomRefs2='po_3_a3 po_3_a4'/>"+
		     "<bond order='1' id='po_3_a3_po_3_a5' atomRefs2='po_3_a3 po_3_a5'/>"+
		     "<bond order='1' id='po_3_a3_po_3_a9' atomRefs2='po_3_a3 po_3_a9'/>"+
		     "<bond order='1' id='po_3_a4_po_3_a10' atomRefs2='po_3_a4 po_3_a10'/>"+
		     "<bond order='1' id='po_3_a4_po_3_a11' atomRefs2='po_3_a4 po_3_a11'/>"+
		     "<bond order='1' id='po_3_a4_po_3_a12' atomRefs2='po_3_a4 po_3_a12'/>"+
		     "<bond order='1' id='po_3_a5_po_3_r2' atomRefs2='po_3_a5 po_3_r2'/>"+
		   "</bondArray>"+
		   "<length id='po_3_len23' atomRefs2='po_3_a2 po_3_a3'/>"+
		   "<angle id='po_3_ang234' atomRefs3='po_3_a2 po_3_a3 po_3_a4'/>"+
		   "<angle id='po_3_ang123' atomRefs3='po_3_r1 po_3_a2 po_3_a3'/>"+
		   "<angle id='po_3_ang352' atomRefs3='po_3_a3 po_3_a5 po_3_r2'/>"+
		   "<torsion id='po_3_tor1' atomRefs4='po_3_r1 po_3_a2 po_3_a3 po_3_a5'/>"+
		   "<torsion id='po_3_tor2' atomRefs4='po_3_a2 po_3_a3 po_3_a5 po_3_r2'/>"+
		   "<arg parameterName='len23'/>"+
		   "<arg parameterName='ang234'/>"+
		   "<arg parameterName='ang123'/>"+
		   "<arg parameterName='ang352'/>"+
		   "<arg parameterName='tor1'/>"+
		   "<arg parameterName='tor2'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_3_r1 g:acetyl_4_r1' moleculeRefs2='g:po_3 g:acetyl_4' order='S'/>"+
		 "<molecule role='fragment' id='acetyl_4'>"+
		   "<atomArray>"+
		     "<atom elementType='R' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0' x3='6.858584288887507' y3='7.368205459562712' z3='1.672120540673427' id='acetyl_4_r1'/>"+
		     "<atom elementType='C' x3='7.470354790502688' y3='7.21912165' z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_4_a71'/>"+
		     "<atom elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_4_a72'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.407334871591223' y3='8.375034459999998' z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_4_a73'/>"+
		     "<atom elementType='H' x3='9.290344900009062' y3='8.0103218' z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_4_a74'/>"+
		     "<atom elementType='H' x3='8.339122721687978' y3='8.830276' z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_4_a75'/>"+
		     "<atom elementType='H' x3='8.236429704800676' y3='8.9953799' z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_4_a76'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='S' id='acetyl_4_r1_acetyl_4_a71' atomRefs2='acetyl_4_r1 acetyl_4_a71'/>"+
		     "<bond order='D' id='acetyl_4_a71_acetyl_4_a72' atomRefs2='acetyl_4_a71 acetyl_4_a72'/>"+
		     "<bond order='S' id='acetyl_4_a71_acetyl_4_a73' atomRefs2='acetyl_4_a71 acetyl_4_a73'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a74' atomRefs2='acetyl_4_a73 acetyl_4_a74'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a75' atomRefs2='acetyl_4_a73 acetyl_4_a75'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a76' atomRefs2='acetyl_4_a73 acetyl_4_a76'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment explicit = (CMLFragment) parseValidString(explicitS);
		AbstractTest.assertEqualsCanonically("fragment", explicit, fragment, true);
		outputXML("explicit1", explicit);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessExplicit1() {
		CMLFragment fragment = makeMol1();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		
		fragmentTool.processIntermediate(moleculeCatalog);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();
//		fragment.debug("EXPLICIT1");
		
		String explicitS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			 "<molecule role='fragment' id='2pyr_1'>"+
			   "<atomArray>"+
			     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'> </atom>"+
			     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'> </atom>"+
			     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
			     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'> </atom>"+
			     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'> </atom>"+
			     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
			     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'> </atom>"+
			     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
			     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'> </atom>"+
			     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_1_r6'> </atom>"+
			     "<atom elementType='C' id='po_2_a2' x3='3.3542438726487402' y3='5.2116710941008835' z3='23.1531709976274'/>"+
			     "<atom elementType='C' id='po_2_a3' x3='2.3501744315778463' y3='4.654633633108781' z3='24.164977216504834'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='po_2_a4' x3='1.1628388226250186' y3='5.61118670899815' z3='24.28435629641084'/>"+
			     "<atom elementType='O' id='po_2_a5' x3='1.8886646153211668' y3='3.3758716014409758' z3='23.72235959839237'> </atom>"+
			     "<atom elementType='H' id='po_2_a7' x3='4.155428455882337' y3='4.488575506339001' z3='23.001203782254784'/>"+
			     "<atom elementType='H' id='po_2_a8' x3='3.7722817053401267' y3='6.143503238650041' z3='23.532678869513617'/>"+
			     "<atom elementType='H' id='po_2_a9' x3='2.8330213024279143' y3='4.549971854623113' z3='25.13628500059519'/>"+
			     "<atom elementType='H' id='po_2_a10' x3='0.44754580709370784' y3='5.214592184589298' z3='25.00471571027384'/>"+
			     "<atom elementType='H' id='po_2_a11' x3='1.5139271925270497' y3='6.5867576316133905' z3='24.621939369841936'/>"+
			     "<atom elementType='H' id='po_2_a12' x3='0.679991951774951' y3='5.715848487483818' z3='23.31304851232049'/>"+
			     "<atom elementType='C' id='po_3_a2' x3='2.4692336648838697' y3='7.051518487962176' z3='20.031240756080326'/>"+
			     "<atom elementType='C' id='po_3_a3' x3='3.1422309730558458' y3='6.726338369142317' z3='21.36672962294428'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='po_3_a4' x3='2.814474239189349' y3='7.821500363097912' z3='22.382705375804736'/>"+
			     "<atom elementType='O' id='po_3_a5' x3='2.6597704827929265' y3='5.46981219491672' z3='21.848920299773738'/>"+
			     "<atom elementType='H' id='po_3_a7' x3='2.62791627711132' y3='6.228222420183369' z3='19.3349208487413'/>"+
			     "<atom elementType='H' id='po_3_a8' x3='2.9007273013931725' y3='7.963758000816078' z3='19.62047888730469'/>"+
			     "<atom elementType='H' id='po_3_a9' x3='4.221413944764144' y3='6.670915946927663' z3='21.225923129163963'/>"+
			     "<atom elementType='H' id='po_3_a10' x3='3.2934771171863324' y3='7.5900289935016' z3='23.333928928881413'/>"+
			     "<atom elementType='H' id='po_3_a11' x3='3.1821401994423395' y3='8.780213673488358' z3='22.015752340456295'/>"+
			     "<atom elementType='H' id='po_3_a12' x3='1.7352912674810506' y3='7.876922785312566' z3='22.523511869585057'/>"+
			     "<atom elementType='C' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_4_a71' x3='0.9585439776031706' y3='7.252644110082241' z3='20.252521466650766'/>"+
			     "<atom elementType='O' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_4_a72' x3='0.12000986076284548' y3='6.528416212815902' z3='19.82715873705721'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='C' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_4_a73' x3='0.7227404744224499' y3='8.481327713724863' z3='21.058322231824498'/>"+
			     "<atom elementType='H' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_4_a74' x3='0.0582116662929415' y3='8.255963099269524' z3='21.71379590703374'/>"+
			     "<atom elementType='H' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_4_a75' x3='0.3779124391041868' y3='9.159863912566095' z3='20.47275989611913'/>"+
			     "<atom elementType='H' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_4_a76' x3='1.510055694491343' y3='8.80986559396381' z3='21.498994941400735'/>"+
			   "</atomArray>"+
			   "<bondArray>"+
			     "<bond order='2' id='2pyr_1_a1_2pyr_1_a2' atomRefs2='2pyr_1_a1 2pyr_1_a2'/>"+
			     "<bond order='1' id='2pyr_1_a1_2pyr_1_a3' atomRefs2='2pyr_1_a1 2pyr_1_a3'/>"+
			     "<bond order='1' id='2pyr_1_a2_2pyr_1_r2' atomRefs2='2pyr_1_a2 2pyr_1_r2'/>"+
			     "<bond order='1' id='2pyr_1_a2_2pyr_1_a4' atomRefs2='2pyr_1_a2 2pyr_1_a4'/>"+
			     "<bond order='2' id='2pyr_1_a6_2pyr_1_a3' atomRefs2='2pyr_1_a6 2pyr_1_a3'/>"+
			     "<bond order='1' id='2pyr_1_a4_2pyr_1_r4' atomRefs2='2pyr_1_a4 2pyr_1_r4'/>"+
			     "<bond order='2' id='2pyr_1_a4_2pyr_1_a5' atomRefs2='2pyr_1_a4 2pyr_1_a5'/>"+
			     "<bond order='1' id='2pyr_1_a5_2pyr_1_r5' atomRefs2='2pyr_1_a5 2pyr_1_r5'/>"+
			     "<bond order='1' id='2pyr_1_a5_2pyr_1_a6' atomRefs2='2pyr_1_a5 2pyr_1_a6'/>"+
			     "<bond order='1' id='2pyr_1_a6_2pyr_1_r6' atomRefs2='2pyr_1_a6 2pyr_1_r6'/>"+
			     "<bond order='1' id='po_2_a2_po_2_a3' atomRefs2='po_2_a2 po_2_a3'/>"+
			     "<bond order='1' id='po_2_a2_po_2_a7' atomRefs2='po_2_a2 po_2_a7'/>"+
			     "<bond order='1' id='po_2_a2_po_2_a8' atomRefs2='po_2_a2 po_2_a8'/>"+
			     "<bond order='1' id='po_2_a3_po_2_a4' atomRefs2='po_2_a3 po_2_a4'/>"+
			     "<bond order='1' id='po_2_a3_po_2_a5' atomRefs2='po_2_a3 po_2_a5'/>"+
			     "<bond order='1' id='po_2_a3_po_2_a9' atomRefs2='po_2_a3 po_2_a9'/>"+
			     "<bond order='1' id='po_2_a4_po_2_a10' atomRefs2='po_2_a4 po_2_a10'/>"+
			     "<bond order='1' id='po_2_a4_po_2_a11' atomRefs2='po_2_a4 po_2_a11'/>"+
			     "<bond order='1' id='po_2_a4_po_2_a12' atomRefs2='po_2_a4 po_2_a12'/>"+
			     "<bond atomRefs2='2pyr_1_a1 po_2_a5' order='S' id='2pyr_1_a1_po_2_a5'/>"+
			     "<bond order='1' id='po_3_a2_po_3_a3' atomRefs2='po_3_a2 po_3_a3'/>"+
			     "<bond order='1' id='po_3_a2_po_3_a7' atomRefs2='po_3_a2 po_3_a7'/>"+
			     "<bond order='1' id='po_3_a2_po_3_a8' atomRefs2='po_3_a2 po_3_a8'/>"+
			     "<bond order='1' id='po_3_a3_po_3_a4' atomRefs2='po_3_a3 po_3_a4'/>"+
			     "<bond order='1' id='po_3_a3_po_3_a5' atomRefs2='po_3_a3 po_3_a5'/>"+
			     "<bond order='1' id='po_3_a3_po_3_a9' atomRefs2='po_3_a3 po_3_a9'/>"+
			     "<bond order='1' id='po_3_a4_po_3_a10' atomRefs2='po_3_a4 po_3_a10'/>"+
			     "<bond order='1' id='po_3_a4_po_3_a11' atomRefs2='po_3_a4 po_3_a11'/>"+
			     "<bond order='1' id='po_3_a4_po_3_a12' atomRefs2='po_3_a4 po_3_a12'/>"+
			     "<bond atomRefs2='po_2_a2 po_3_a5' order='S' id='po_2_a2_po_3_a5'/>"+
			     "<bond order='D' id='acetyl_4_a71_acetyl_4_a72' atomRefs2='acetyl_4_a71 acetyl_4_a72'/>"+
			     "<bond order='S' id='acetyl_4_a71_acetyl_4_a73' atomRefs2='acetyl_4_a71 acetyl_4_a73'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a74' atomRefs2='acetyl_4_a73 acetyl_4_a74'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a75' atomRefs2='acetyl_4_a73 acetyl_4_a75'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a76' atomRefs2='acetyl_4_a73 acetyl_4_a76'/>"+
			     "<bond atomRefs2='po_3_a2 acetyl_4_a71' order='S' id='po_3_a2_acetyl_4_a71'/>"+
			   "</bondArray>"+
			   "<torsion id='po_2_tor2' atomRefs4='po_2_a2 po_2_a3 po_2_a5 po_2_r2'/>"+
			   "<torsion id='po_2_tor1' atomRefs4='po_2_r1 po_2_a2 po_2_a3 po_2_a5'/>"+
			   "<angle id='po_2_ang352' atomRefs3='po_2_a3 po_2_a5 po_2_r2'/>"+
			   "<angle id='po_2_ang123' atomRefs3='po_2_r1 po_2_a2 po_2_a3'/>"+
			   "<angle id='po_2_ang234' atomRefs3='po_2_a2 po_2_a3 po_2_a4'/>"+
			   "<length id='po_2_len23' atomRefs2='po_2_a2 po_2_a3'/>"+
			   "<torsion id='po_3_tor2' atomRefs4='po_3_a2 po_3_a3 po_3_a5 po_3_r2'/>"+
			   "<torsion id='po_3_tor1' atomRefs4='po_3_r1 po_3_a2 po_3_a3 po_3_a5'/>"+
			   "<angle id='po_3_ang352' atomRefs3='po_3_a3 po_3_a5 po_3_r2'/>"+
			   "<angle id='po_3_ang123' atomRefs3='po_3_r1 po_3_a2 po_3_a3'/>"+
			   "<angle id='po_3_ang234' atomRefs3='po_3_a2 po_3_a3 po_3_a4'/>"+
			   "<length id='po_3_len23' atomRefs2='po_3_a2 po_3_a3'/>"+
			 "</molecule>"+
			"</fragment>";
		CMLFragment fragmentE = (CMLFragment) parseValidString(explicitS);
		assertEqualsCanonically("explicit", fragmentE, fragment, true);
		outputXML("complete1", fragment);
	}

	private CMLFragment makeMol2() {
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:2pyr'/>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>" +
		"    <molecule ref='g:benzene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"        <fragment>"+
		"          <fragment>"+
		"            <molecule ref='g:po'/>"+
		"          </fragment>" +
		"        </fragment>" +
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"  <join atomRefs2='r4 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='g:acetyl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * Test child molecule with branches
	 * form A-j-B(-j-BB)-j-C
	 */
	@Test
	public void testProcessBasic2() {
		CMLFragment fragment = makeMol2();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		"  <molecule ref='g:2pyr' id='1'>"+
		"    <arg name='idx'>"+
		"      <scalar dataType='xsd:string'>1</scalar>"+
		"    </arg>"+
		"  </molecule>"+
		"  <join atomRefs2='g:2pyr_1_r1 g:benzene_2_r2' moleculeRefs2='g:2pyr_1 g:benzene_2'/>"+
		"  <molecule ref='g:benzene' id='2'>"+
		"    <join moleculeRefs2='g:benzene_2 g:po_3' atomRefs2='g:benzene_2_r3 g:po_3_r1'>"+
		"      <molecule ref='g:po' id='3'>"+
		"        <arg name='idx'>"+
		         "<scalar dataType='xsd:string'>3</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		   "</join>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:benzene_2_r4 g:acetyl_4_r1' moleculeRefs2='g:benzene_2 g:acetyl_4'/>"+
		 "<molecule ref='g:acetyl' id='4'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>4</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
		AbstractTest.assertEqualsCanonically("fragment", intermediate, fragment, true);
		outputXML("intermediate2", intermediate);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessIntermediate2() {
		CMLFragment fragment = makeMol2();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		fragmentTool.processIntermediate(moleculeCatalog);
		fragment = fragmentTool.getFragment();
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='2pyr_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'> </atom>"+
		     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_1_r1'> </atom>"+
		     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'> </atom>"+
		     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
		     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'> </atom>"+
		     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'> </atom>"+
		     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
		     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'> </atom>"+
		     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
		     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'> </atom>"+
		     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_1_r6'> </atom>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='2pyr_1_r1_2pyr_1_a1' atomRefs2='2pyr_1_r1 2pyr_1_a1'/>"+
		     "<bond order='2' id='2pyr_1_a1_2pyr_1_a2' atomRefs2='2pyr_1_a1 2pyr_1_a2'/>"+
		     "<bond order='1' id='2pyr_1_a1_2pyr_1_a3' atomRefs2='2pyr_1_a1 2pyr_1_a3'/>"+
		     "<bond order='1' id='2pyr_1_a2_2pyr_1_r2' atomRefs2='2pyr_1_a2 2pyr_1_r2'/>"+
		     "<bond order='1' id='2pyr_1_a2_2pyr_1_a4' atomRefs2='2pyr_1_a2 2pyr_1_a4'/>"+
		     "<bond order='2' id='2pyr_1_a6_2pyr_1_a3' atomRefs2='2pyr_1_a6 2pyr_1_a3'/>"+
		     "<bond order='1' id='2pyr_1_a4_2pyr_1_r4' atomRefs2='2pyr_1_a4 2pyr_1_r4'/>"+
		     "<bond order='2' id='2pyr_1_a4_2pyr_1_a5' atomRefs2='2pyr_1_a4 2pyr_1_a5'/>"+
		     "<bond order='1' id='2pyr_1_a5_2pyr_1_r5' atomRefs2='2pyr_1_a5 2pyr_1_r5'/>"+
		     "<bond order='1' id='2pyr_1_a5_2pyr_1_a6' atomRefs2='2pyr_1_a5 2pyr_1_a6'/>"+
		     "<bond order='1' id='2pyr_1_a6_2pyr_1_r6' atomRefs2='2pyr_1_a6 2pyr_1_r6'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_1_r1 g:benzene_2_r2' moleculeRefs2='g:2pyr_1 g:benzene_2' order='S'/>"+
		 "<molecule role='fragment' id='benzene_2'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='9.526706134000763' y3='3.869733600000001' z3='5.213518402229052' id='benzene_2_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r6</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='9.59902744852545' y3='4.430492482197403' z3='4.690814754353756' id='benzene_2_r1'/>"+
		     "<atom elementType='C' x3='10.243299413197152' y3='3.932398500000001' z3='6.439022942911609' id='benzene_2_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.713504556428543' y3='2.7185301000000006' z3='5.01720505576243' id='benzene_2_a6'>"+
		       "<label dictRef='cml:torsionEnd'>r5</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='8.385888936961882' y3='2.655387420737078' z3='4.323244676535362' id='benzene_2_r6'/>"+
		     "<atom elementType='C' x3='10.119474056141831' y3='2.9008920000000007' z3='7.3834992125284815' id='benzene_2_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='10.509388371349742' y3='2.947556560950007' z3='8.045835415334345' id='benzene_2_r3'/>"+
		     "<atom elementType='C' x3='9.320371405363035' y3='1.8151698000000005' z3='7.151684115065878' id='benzene_2_a4'>"+
		       "<label dictRef='cml:torsionEnd'>r3</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='9.280916015724046' y3='1.2657016684721403' z3='7.6896692864820775' id='benzene_2_r4'/>"+
		     "<atom elementType='C' x3='8.610030693701125' y3='1.7243409000000007' z3='5.934289686115539' id='benzene_2_a5'>"+
		       "<label dictRef='cml:torsionEnd'>r4</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='8.16522311995263' y3='1.112386804675892' z3='5.790907652540795' id='benzene_2_r5'/>"+
		     "<atom elementType='R' x3='10.697234803620145' y3='4.543958438540135' z3='6.552323882423661' id='benzene_2_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='benzene_2_a1_benzene_2_r1' atomRefs2='benzene_2_a1 benzene_2_r1'/>"+
		     "<bond order='2' id='benzene_2_a1_benzene_2_a2' atomRefs2='benzene_2_a1 benzene_2_a2'/>"+
		     "<bond order='1' id='benzene_2_a1_benzene_2_a6' atomRefs2='benzene_2_a1 benzene_2_a6'/>"+
		     "<bond order='1' id='benzene_2_a3_benzene_2_a2' atomRefs2='benzene_2_a3 benzene_2_a2'/>"+
		     "<bond order='1' id='benzene_2_a2_benzene_2_r2' atomRefs2='benzene_2_a2 benzene_2_r2'/>"+
		     "<bond order='1' id='benzene_2_r6_benzene_2_a6' atomRefs2='benzene_2_r6 benzene_2_a6'/>"+
		     "<bond order='2' id='benzene_2_a5_benzene_2_a6' atomRefs2='benzene_2_a5 benzene_2_a6'/>"+
		     "<bond order='1' id='benzene_2_a3_benzene_2_r3' atomRefs2='benzene_2_a3 benzene_2_r3'/>"+
		     "<bond order='2' id='benzene_2_a3_benzene_2_a4' atomRefs2='benzene_2_a3 benzene_2_a4'/>"+
		     "<bond order='1' id='benzene_2_a5_benzene_2_a4' atomRefs2='benzene_2_a5 benzene_2_a4'/>"+
		     "<bond order='1' id='benzene_2_a4_benzene_2_r4' atomRefs2='benzene_2_a4 benzene_2_r4'/>"+
		     "<bond order='1' id='benzene_2_a5_benzene_2_r5' atomRefs2='benzene_2_a5 benzene_2_r5'/>"+
		   "</bondArray>"+
		   "<join moleculeRefs2='g:benzene_2 g:po_3' atomRefs2='g:benzene_2_r3 g:po_3_r1' order='S'>"+
		     "<molecule role='fragment' id='po_3'>"+
		       "<atomArray>"+
		         "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_3_r1'/>"+
		         "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_3_a2'/>"+
		         "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_3_a3'>"+
		           "<label dictRef='cml:torsionEnd'>r1</label>"+
		           "<label dictRef='cml:torsionEnd'>r2</label>"+
		         "</atom>"+
		         "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_3_a4'/>"+
		         "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_3_a5'/>"+
		         "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_3_a7'/>"+
		         "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_3_a8'/>"+
		         "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_3_a9'/>"+
		         "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_3_a10'/>"+
		         "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_3_a11'/>"+
		         "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_3_a12'/>"+
		         "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_3_r2'/>"+
		       "</atomArray>"+
		       "<bondArray>"+
		         "<bond order='1' id='po_3_r1_po_3_a2' atomRefs2='po_3_r1 po_3_a2'/>"+
		         "<bond order='1' id='po_3_a2_po_3_a3' atomRefs2='po_3_a2 po_3_a3'/>"+
		         "<bond order='1' id='po_3_a2_po_3_a7' atomRefs2='po_3_a2 po_3_a7'/>"+
		         "<bond order='1' id='po_3_a2_po_3_a8' atomRefs2='po_3_a2 po_3_a8'/>"+
		         "<bond order='1' id='po_3_a3_po_3_a4' atomRefs2='po_3_a3 po_3_a4'/>"+
		         "<bond order='1' id='po_3_a3_po_3_a5' atomRefs2='po_3_a3 po_3_a5'/>"+
		         "<bond order='1' id='po_3_a3_po_3_a9' atomRefs2='po_3_a3 po_3_a9'/>"+
		         "<bond order='1' id='po_3_a4_po_3_a10' atomRefs2='po_3_a4 po_3_a10'/>"+
		         "<bond order='1' id='po_3_a4_po_3_a11' atomRefs2='po_3_a4 po_3_a11'/>"+
		         "<bond order='1' id='po_3_a4_po_3_a12' atomRefs2='po_3_a4 po_3_a12'/>"+
		         "<bond order='1' id='po_3_a5_po_3_r2' atomRefs2='po_3_a5 po_3_r2'/>"+
		       "</bondArray>"+
		       "<length id='po_3_len23' atomRefs2='po_3_a2 po_3_a3'/>"+
		       "<angle id='po_3_ang234' atomRefs3='po_3_a2 po_3_a3 po_3_a4'/>"+
		       "<angle id='po_3_ang123' atomRefs3='po_3_r1 po_3_a2 po_3_a3'/>"+
		       "<angle id='po_3_ang352' atomRefs3='po_3_a3 po_3_a5 po_3_r2'/>"+
		       "<torsion id='po_3_tor1' atomRefs4='po_3_r1 po_3_a2 po_3_a3 po_3_a5'/>"+
		       "<torsion id='po_3_tor2' atomRefs4='po_3_a2 po_3_a3 po_3_a5 po_3_r2'/>"+
		       "<arg parameterName='len23'/>"+
		       "<arg parameterName='ang234'/>"+
		       "<arg parameterName='ang123'/>"+
		       "<arg parameterName='ang352'/>"+
		       "<arg parameterName='tor1'/>"+
		       "<arg parameterName='tor2'/>"+
		     "</molecule>"+
		   "</join>"+
		 "</molecule>"+
		 "<join atomRefs2='g:benzene_2_r4 g:acetyl_4_r1' moleculeRefs2='g:benzene_2 g:acetyl_4' order='S'/>"+
		 "<molecule role='fragment' id='acetyl_4'>"+
		   "<atomArray>"+
		     "<atom elementType='R' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0' x3='6.858584288887507' y3='7.368205459562712' z3='1.672120540673427' id='acetyl_4_r1'/>"+
		     "<atom elementType='C' x3='7.470354790502688' y3='7.21912165' z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_4_a71'/>"+
		     "<atom elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_4_a72'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.407334871591223' y3='8.375034459999998' z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_4_a73'/>"+
		     "<atom elementType='H' x3='9.290344900009062' y3='8.0103218' z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_4_a74'/>"+
		     "<atom elementType='H' x3='8.339122721687978' y3='8.830276' z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_4_a75'/>"+
		     "<atom elementType='H' x3='8.236429704800676' y3='8.9953799' z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_4_a76'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='S' id='acetyl_4_r1_acetyl_4_a71' atomRefs2='acetyl_4_r1 acetyl_4_a71'/>"+
		     "<bond order='D' id='acetyl_4_a71_acetyl_4_a72' atomRefs2='acetyl_4_a71 acetyl_4_a72'/>"+
		     "<bond order='S' id='acetyl_4_a71_acetyl_4_a73' atomRefs2='acetyl_4_a71 acetyl_4_a73'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a74' atomRefs2='acetyl_4_a73 acetyl_4_a74'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a75' atomRefs2='acetyl_4_a73 acetyl_4_a75'/>"+
		     "<bond order='S' id='acetyl_4_a73_acetyl_4_a76' atomRefs2='acetyl_4_a73 acetyl_4_a76'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment explicit = (CMLFragment) parseValidString(explicitS);
		AbstractTest.assertEqualsCanonically("fragment", explicit, fragment, true);
		outputXML("explicit2", explicit);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessExplicit2() {
		CMLFragment fragment = makeMol2();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		
		fragmentTool.processIntermediate(moleculeCatalog);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();

		String explicitS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			  "<molecule role='fragment' id='2pyr_1'>"+
			    "<atomArray>"+
			      "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'></atom>"+
			      "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'></atom>"+
			      "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'></atom>"+
			      "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'></atom>"+
			      "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'></atom>"+
			      "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'></atom>"+
			      "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'></atom>"+
			      "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'></atom>"+
			      "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'></atom>"+
			      "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_1_r6'></atom>"+
			      "<atom elementType='C' id='benzene_2_a1' x3='1.9134044499930045' y3='4.779322854745493' z3='23.401718866756852'>"+
			        "<label dictRef='cml:torsionEnd'>r6</label>"+
			      "</atom>"+
			      "<atom elementType='R' id='benzene_2_r1' x3='1.4274846777630092' y3='5.253593983545907' z3='23.764828865713547'/>"+
			      "<atom elementType='C' id='benzene_2_a2' x3='1.9146811645262725' y3='3.3888780441460686' z3='23.694901054810863'>"+
			        "<label dictRef='cml:torsionEnd'>r1</label>"+
			      "</atom>"+
			      "<atom elementType='C' id='benzene_2_a6' x3='2.86406756831792' y3='5.221300392145023' z3='22.439429551597364'>"+
			        "<label dictRef='cml:torsionEnd'>r5</label>"+
			      "</atom>"+
			      "<atom elementType='R' id='benzene_2_r6' x3='2.907791535984223' y3='5.983430251237611' z3='22.338700744739405'/>"+
			      "<atom elementType='C' id='benzene_2_a3' x3='2.811972505936997' y3='2.5296322997947955' z3='23.040730739139857'>"+
			        "<label dictRef='cml:torsionEnd'>r2</label>"+
			      "</atom>"+
			      "<atom elementType='C' id='benzene_2_a4' x3='3.7050999055672755' y3='3.0009933793723422' z3='22.11810505005718'>"+
			        "<label dictRef='cml:torsionEnd'>r3</label>"+
			      "</atom>"+
			      "<atom elementType='C' id='benzene_2_a5' x3='3.7319209177744277' y3='4.381623993310667' z3='21.821418763688317'>"+
			        "<label dictRef='cml:torsionEnd'>r4</label>"+
			      "</atom>"+
			      "<atom elementType='R' id='benzene_2_r5' x3='4.237610230082442' y3='4.657551188339373' z3='21.310495577229688'/>"+
			      "<atom elementType='C' id='po_3_a2' x3='2.7896830871486165' y3='1.0251009115704068' z3='23.368585102627172'/>"+
			      "<atom elementType='C' id='po_3_a3' x3='2.304036646832878' y3='0.2430716663984791' z3='22.145974864364593'>"+
			        "<label dictRef='cml:torsionEnd'>r1</label>"+
			        "<label dictRef='cml:torsionEnd'>r2</label>"+
			      "</atom>"+
			      "<atom elementType='C' id='po_3_a4' x3='0.9484270407139386' y3='0.7901251452978366' z3='21.696457248444855'/>"+
			      "<atom elementType='O' id='po_3_a5' x3='3.2505797376550154' y3='0.38498757620623136' z3='21.08387785760847'/>"+
			      "<atom elementType='H' id='po_3_a7' x3='3.7942796453791634' y3='0.6962848001997717' z3='23.634148127339635'/>"+
			      "<atom elementType='H' id='po_3_a8' x3='2.115076220301686' y3='0.8456617526384145' z3='24.205125695452615'/>"+
			      "<atom elementType='H' id='po_3_a9' x3='2.2041760845936405' y3='-0.8107784582739312' z3='22.40474461305483'/>"+
			      "<atom elementType='H' id='po_3_a10' x3='0.6024638356164089' y3='0.23348668878252177' z3='20.825646080942455'/>"+
			      "<atom elementType='H' id='po_3_a11' x3='0.2258649284705312' y3='0.6822405106113019' z3='22.505919353517662'/>"+
			      "<atom elementType='H' id='po_3_a12' x3='1.0482876029531756' y3='1.8439752699702463' z3='21.437687499754617'/>"+
			      "<atom elementType='R' id='po_3_r2' x3='2.9913346252211275' y3='0.0028272865312581885' z3='20.51850586973263'/>"+
			      "<atom elementType='C' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_4_a71' x3='4.667333890346795' y3='2.002495401534097' z3='21.448248687253738'/>"+
			      "<atom elementType='O' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_4_a72' x3='4.464268196906201' y3='0.8363713781134425' z3='21.361657405013545'>"+
			        "<label dictRef='cml:torsionEnd'>r1</label>"+
			      "</atom>"+
			      "<atom elementType='C' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_4_a73' x3='5.872872751064367' y3='2.7171786824991018' z3='20.947763496328008'/>"+
			      "<atom elementType='H' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_4_a74' x3='6.05080305049709' y3='2.3727086426610393' z3='20.069290568246164'/>"+
			      "<atom elementType='H' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_4_a75' x3='6.60719062096423' y3='2.5059695057987366' z3='21.529458386884365'/>"+
			      "<atom elementType='H' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_4_a76' x3='5.764764645101332' y3='3.670237577412034' z3='20.903196561031553'/>"+
			    "</atomArray>"+
			    "<bondArray>"+
			      "<bond order='2' id='2pyr_1_a1_2pyr_1_a2' atomRefs2='2pyr_1_a1 2pyr_1_a2'/>"+
			      "<bond order='1' id='2pyr_1_a1_2pyr_1_a3' atomRefs2='2pyr_1_a1 2pyr_1_a3'/>"+
			      "<bond order='1' id='2pyr_1_a2_2pyr_1_r2' atomRefs2='2pyr_1_a2 2pyr_1_r2'/>"+
			      "<bond order='1' id='2pyr_1_a2_2pyr_1_a4' atomRefs2='2pyr_1_a2 2pyr_1_a4'/>"+
			      "<bond order='2' id='2pyr_1_a6_2pyr_1_a3' atomRefs2='2pyr_1_a6 2pyr_1_a3'/>"+
			      "<bond order='1' id='2pyr_1_a4_2pyr_1_r4' atomRefs2='2pyr_1_a4 2pyr_1_r4'/>"+
			      "<bond order='2' id='2pyr_1_a4_2pyr_1_a5' atomRefs2='2pyr_1_a4 2pyr_1_a5'/>"+
			      "<bond order='1' id='2pyr_1_a5_2pyr_1_r5' atomRefs2='2pyr_1_a5 2pyr_1_r5'/>"+
			      "<bond order='1' id='2pyr_1_a5_2pyr_1_a6' atomRefs2='2pyr_1_a5 2pyr_1_a6'/>"+
			      "<bond order='1' id='2pyr_1_a6_2pyr_1_r6' atomRefs2='2pyr_1_a6 2pyr_1_r6'/>"+
			      "<bond order='1' id='benzene_2_a1_benzene_2_r1' atomRefs2='benzene_2_a1 benzene_2_r1'/>"+
			      "<bond order='2' id='benzene_2_a1_benzene_2_a2' atomRefs2='benzene_2_a1 benzene_2_a2'/>"+
			      "<bond order='1' id='benzene_2_a1_benzene_2_a6' atomRefs2='benzene_2_a1 benzene_2_a6'/>"+
			      "<bond order='1' id='benzene_2_a3_benzene_2_a2' atomRefs2='benzene_2_a3 benzene_2_a2'/>"+
			      "<bond order='1' id='benzene_2_r6_benzene_2_a6' atomRefs2='benzene_2_r6 benzene_2_a6'/>"+
			      "<bond order='2' id='benzene_2_a5_benzene_2_a6' atomRefs2='benzene_2_a5 benzene_2_a6'/>"+
			      "<bond order='2' id='benzene_2_a3_benzene_2_a4' atomRefs2='benzene_2_a3 benzene_2_a4'/>"+
			      "<bond order='1' id='benzene_2_a5_benzene_2_a4' atomRefs2='benzene_2_a5 benzene_2_a4'/>"+
			      "<bond order='1' id='benzene_2_a5_benzene_2_r5' atomRefs2='benzene_2_a5 benzene_2_r5'/>"+
			      "<bond order='1' id='po_3_a2_po_3_a3' atomRefs2='po_3_a2 po_3_a3'/>"+
			      "<bond order='1' id='po_3_a2_po_3_a7' atomRefs2='po_3_a2 po_3_a7'/>"+
			      "<bond order='1' id='po_3_a2_po_3_a8' atomRefs2='po_3_a2 po_3_a8'/>"+
			      "<bond order='1' id='po_3_a3_po_3_a4' atomRefs2='po_3_a3 po_3_a4'/>"+
			      "<bond order='1' id='po_3_a3_po_3_a5' atomRefs2='po_3_a3 po_3_a5'/>"+
			      "<bond order='1' id='po_3_a3_po_3_a9' atomRefs2='po_3_a3 po_3_a9'/>"+
			      "<bond order='1' id='po_3_a4_po_3_a10' atomRefs2='po_3_a4 po_3_a10'/>"+
			      "<bond order='1' id='po_3_a4_po_3_a11' atomRefs2='po_3_a4 po_3_a11'/>"+
			      "<bond order='1' id='po_3_a4_po_3_a12' atomRefs2='po_3_a4 po_3_a12'/>"+
			      "<bond order='1' id='po_3_a5_po_3_r2' atomRefs2='po_3_a5 po_3_r2'/>"+
			      "<bond atomRefs2='benzene_2_a3 po_3_a2' order='S' id='benzene_2_a3_po_3_a2'/>"+
			      "<bond atomRefs2='2pyr_1_a1 benzene_2_a2' order='S' id='2pyr_1_a1_benzene_2_a2'/>"+
			      "<bond order='D' id='acetyl_4_a71_acetyl_4_a72' atomRefs2='acetyl_4_a71 acetyl_4_a72'/>"+
			      "<bond order='S' id='acetyl_4_a71_acetyl_4_a73' atomRefs2='acetyl_4_a71 acetyl_4_a73'/>"+
			      "<bond order='S' id='acetyl_4_a73_acetyl_4_a74' atomRefs2='acetyl_4_a73 acetyl_4_a74'/>"+
			      "<bond order='S' id='acetyl_4_a73_acetyl_4_a75' atomRefs2='acetyl_4_a73 acetyl_4_a75'/>"+
			      "<bond order='S' id='acetyl_4_a73_acetyl_4_a76' atomRefs2='acetyl_4_a73 acetyl_4_a76'/>"+
			      "<bond atomRefs2='benzene_2_a4 acetyl_4_a71' order='S' id='benzene_2_a4_acetyl_4_a71'/>"+
			    "</bondArray>"+
			    "<length id='po_3_len23' atomRefs2='po_3_a2 po_3_a3'/>"+
			    "<angle id='po_3_ang234' atomRefs3='po_3_a2 po_3_a3 po_3_a4'/>"+
			    "<angle id='po_3_ang123' atomRefs3='po_3_r1 po_3_a2 po_3_a3'/>"+
			    "<angle id='po_3_ang352' atomRefs3='po_3_a3 po_3_a5 po_3_r2'/>"+
			    "<torsion id='po_3_tor1' atomRefs4='po_3_r1 po_3_a2 po_3_a3 po_3_a5'/>"+
			    "<torsion id='po_3_tor2' atomRefs4='po_3_a2 po_3_a3 po_3_a5 po_3_r2'/>"+
			    "<arg parameterName='len23'/>"+
			    "<arg parameterName='ang234'/>"+
			    "<arg parameterName='ang123'/>"+
			    "<arg parameterName='ang352'/>"+
			    "<arg parameterName='tor1'/>"+
			    "<arg parameterName='tor2'/>"+
			  "</molecule>"+
			"</fragment>";
		CMLFragment fragmentE = (CMLFragment) parseValidString(explicitS);
//		assertEqualsCanonically("explicit", fragmentE, fragment, true);
		outputXML("complete2", fragment);
	}

	private CMLFragment makeMol3() {
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:propanoic'/>" +
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>" +
		"    <molecule ref='g:2pyr'>" +
		"      <join id='j1' moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:phenylethane'/>" +
		"          </fragment>"+
		"          <join id='j2' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"          <fragment countExpression='*(2)'>"+
		"            <join id='j22' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"            <fragment>" +
		"              <molecule ref='g:po'/>" +
		"            </fragment>"+
		"          </fragment>"+
		"          <join id='j3' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"          <fragment>"+
		"            <molecule ref='g:adgluc'/>" +
		"          </fragment>"+
		"        </fragment>"+
		"      </join>"+
		"    </molecule>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='g:acetyl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * Test child molecule with own branched chain
	 * form A-j-B(-j-X-(-Y)*n-Z)-j-C
	 */
	@Test
	public void testProcessBasic3() {
		CMLFragment fragment = makeMol3();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
//		fragment.debug("BASIC3");
		
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:propanoic' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:propanoic_1_r1 g:2pyr_2_r2' moleculeRefs2='g:propanoic_1 g:2pyr_2'/>"+
		 "<molecule ref='g:2pyr' id='2'>"+
		   "<join id='j1' atomRefs2='g:2pyr_2_r5 g:phenylethane_3_r1' moleculeRefs2='g:2pyr_2 g:phenylethane_3'>"+
		     "<molecule ref='g:phenylethane' id='3'>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>3</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		     "<join id='j2' atomRefs2='g:phenylethane_3_r2 g:po_4_r1' moleculeRefs2='g:phenylethane_3 g:po_4'/>"+
		     "<molecule ref='g:po' id='4'>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>4</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		     "<join id='j22' atomRefs2='g:po_4_r2 g:po_5_r1' moleculeRefs2='g:po_4 g:po_5'/>"+
		     "<molecule ref='g:po' id='5'>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>5</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		     "<join id='j3' atomRefs2='g:po_5_r2 g:adgluc_6_r1' moleculeRefs2='g:po_5 g:adgluc_6'/>"+
		     "<molecule ref='g:adgluc' id='6'>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>6</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		   "</join>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_2_r1 g:acetyl_7_r1' moleculeRefs2='g:2pyr_2 g:acetyl_7'/>"+
		 "<molecule ref='g:acetyl' id='7'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>7</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
		AbstractTest.assertEqualsCanonically("fragment", intermediate, fragment, true);
		outputXML("intermediate3", intermediate);
	}
	

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessIntermediate3() {
		CMLFragment fragment = makeMol3();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		fragmentTool.processIntermediate(moleculeCatalog);
		fragment = fragmentTool.getFragment();
//		fragment.debug("INTERMED3");
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='propanoic_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='1.998' y3='-0.064' z3='-0.0' id='propanoic_1_a1'/>"+
		     "<atom elementType='C' x3='0.757' y3='0.831' z3='-0.0' id='propanoic_1_a2'/>"+
		     "<atom elementType='C' x3='-0.482' y3='-0.028' z3='0.0' id='propanoic_1_a3'/>"+
		     "<atom elementType='O' formalCharge='-1' x3='-0.382' y3='-1.232' z3='0.0' id='propanoic_1_a4'/>"+
		     "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='propanoic_1_a5'/>"+
		     "<atom elementType='H' x3='1.992' y3='-0.694' z3='0.89' id='propanoic_1_a6'/>"+
		     "<atom elementType='H' x3='1.992' y3='-0.694' z3='-0.89' id='propanoic_1_a7'/>"+
		     "<atom elementType='H' x3='2.893' y3='0.557' z3='-0.0' id='propanoic_1_a8'/>"+
		     "<atom elementType='H' x3='0.763' y3='1.46' z3='0.89' id='propanoic_1_a9'/>"+
		     "<atom elementType='H' x3='0.763' y3='1.46' z3='-0.89' id='propanoic_1_a10'/>"+
		     "<atom elementType='R' x3='-2.2957376621755117' y3='0.1298092545860114' z3='0.0' id='propanoic_1_r1'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='propanoic_1_a1_propanoic_1_a2' atomRefs2='propanoic_1_a1 propanoic_1_a2'/>"+
		     "<bond order='1' id='propanoic_1_a1_propanoic_1_a6' atomRefs2='propanoic_1_a1 propanoic_1_a6'/>"+
		     "<bond order='1' id='propanoic_1_a1_propanoic_1_a7' atomRefs2='propanoic_1_a1 propanoic_1_a7'/>"+
		     "<bond order='1' id='propanoic_1_a1_propanoic_1_a8' atomRefs2='propanoic_1_a1 propanoic_1_a8'/>"+
		     "<bond order='1' id='propanoic_1_a2_propanoic_1_a3' atomRefs2='propanoic_1_a2 propanoic_1_a3'/>"+
		     "<bond order='1' id='propanoic_1_a2_propanoic_1_a9' atomRefs2='propanoic_1_a2 propanoic_1_a9'/>"+
		     "<bond order='1' id='propanoic_1_a2_propanoic_1_a10' atomRefs2='propanoic_1_a2 propanoic_1_a10'/>"+
		     "<bond order='2' id='propanoic_1_a3_propanoic_1_a4' atomRefs2='propanoic_1_a3 propanoic_1_a4'/>"+
		     "<bond order='1' id='propanoic_1_a3_propanoic_1_a5' atomRefs2='propanoic_1_a3 propanoic_1_a5'/>"+
		     "<bond order='1' id='propanoic_1_a5_propanoic_1_r1' atomRefs2='propanoic_1_a5 propanoic_1_r1'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		 "<join atomRefs2='g:propanoic_1_r1 g:2pyr_2_r2' moleculeRefs2='g:propanoic_1 g:2pyr_2' order='S'/>"+
		 "<molecule role='fragment' id='2pyr_2'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_2_a1'> </atom>"+
		     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_2_r1'> </atom>"+
		     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_2_a2'> </atom>"+
		     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_2_r2'> </atom>"+
		     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_2_a3'> </atom>"+
		     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_2_a4'> </atom>"+
		     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_2_r4'> </atom>"+
		     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_2_a5'> </atom>"+
		     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_2_r5'> </atom>"+
		     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_2_a6'> </atom>"+
		     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_2_r6'> </atom>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='2pyr_2_r1_2pyr_2_a1' atomRefs2='2pyr_2_r1 2pyr_2_a1'/>"+
		     "<bond order='2' id='2pyr_2_a1_2pyr_2_a2' atomRefs2='2pyr_2_a1 2pyr_2_a2'/>"+
		     "<bond order='1' id='2pyr_2_a1_2pyr_2_a3' atomRefs2='2pyr_2_a1 2pyr_2_a3'/>"+
		     "<bond order='1' id='2pyr_2_a2_2pyr_2_r2' atomRefs2='2pyr_2_a2 2pyr_2_r2'/>"+
		     "<bond order='1' id='2pyr_2_a2_2pyr_2_a4' atomRefs2='2pyr_2_a2 2pyr_2_a4'/>"+
		     "<bond order='2' id='2pyr_2_a6_2pyr_2_a3' atomRefs2='2pyr_2_a6 2pyr_2_a3'/>"+
		     "<bond order='1' id='2pyr_2_a4_2pyr_2_r4' atomRefs2='2pyr_2_a4 2pyr_2_r4'/>"+
		     "<bond order='2' id='2pyr_2_a4_2pyr_2_a5' atomRefs2='2pyr_2_a4 2pyr_2_a5'/>"+
		     "<bond order='1' id='2pyr_2_a5_2pyr_2_r5' atomRefs2='2pyr_2_a5 2pyr_2_r5'/>"+
		     "<bond order='1' id='2pyr_2_a5_2pyr_2_a6' atomRefs2='2pyr_2_a5 2pyr_2_a6'/>"+
		     "<bond order='1' id='2pyr_2_a6_2pyr_2_r6' atomRefs2='2pyr_2_a6 2pyr_2_r6'/>"+
		   "</bondArray>"+
		   "<join id='j1' atomRefs2='g:2pyr_2_r5 g:phenylethane_3_r1' moleculeRefs2='g:2pyr_2 g:phenylethane_3' order='S'>"+
		     "<molecule role='fragment' id='phenylethane_3'>"+
		       "<atomArray>"+
		         "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_3_a1'>"+
		           "<label dictRef='cml:torsionEnd'>r2</label>"+
		         "</atom>"+
		         "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_3_a2'>"+
		           "<label dictRef='cml:torsionEnd'>r1</label>"+
		         "</atom>"+
		         "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_3_a3'/>"+
		         "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_3_a4'/>"+
		         "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_3_a5'/>"+
		         "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_3_a6'/>"+
		         "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_3_a7'/>"+
		         "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_3_a8'/>"+
		         "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_3_a9'/>"+
		         "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_3_a10'/>"+
		         "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_3_r1'/>"+
		         "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_3_r2'/>"+
		         "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_3_a13'/>"+
		         "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_3_a14'/>"+
		         "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_3_a15'/>"+
		         "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_3_a16'/>"+
		         "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_3_a17'/>"+
		         "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_3_a18'/>"+
		       "</atomArray>"+
		       "<bondArray>"+
		         "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a2' atomRefs2='phenylethane_3_a1 phenylethane_3_a2'/>"+
		         "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a9' atomRefs2='phenylethane_3_a1 phenylethane_3_a9'/>"+
		         "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a10' atomRefs2='phenylethane_3_a1 phenylethane_3_a10'/>"+
		         "<bond order='1' id='phenylethane_3_a1_phenylethane_3_r1' atomRefs2='phenylethane_3_a1 phenylethane_3_r1'/>"+
		         "<bond order='1' id='phenylethane_3_a2_phenylethane_3_a3' atomRefs2='phenylethane_3_a2 phenylethane_3_a3'/>"+
		         "<bond order='1' id='phenylethane_3_a2_phenylethane_3_r2' atomRefs2='phenylethane_3_a2 phenylethane_3_r2'/>"+
		         "<bond order='1' id='phenylethane_3_a2_phenylethane_3_a13' atomRefs2='phenylethane_3_a2 phenylethane_3_a13'/>"+
		         "<bond order='2' id='phenylethane_3_a3_phenylethane_3_a8' atomRefs2='phenylethane_3_a3 phenylethane_3_a8'/>"+
		         "<bond order='1' id='phenylethane_3_a3_phenylethane_3_a4' atomRefs2='phenylethane_3_a3 phenylethane_3_a4'/>"+
		         "<bond order='2' id='phenylethane_3_a4_phenylethane_3_a5' atomRefs2='phenylethane_3_a4 phenylethane_3_a5'/>"+
		         "<bond order='1' id='phenylethane_3_a4_phenylethane_3_a14' atomRefs2='phenylethane_3_a4 phenylethane_3_a14'/>"+
		         "<bond order='1' id='phenylethane_3_a5_phenylethane_3_a6' atomRefs2='phenylethane_3_a5 phenylethane_3_a6'/>"+
		         "<bond order='1' id='phenylethane_3_a5_phenylethane_3_a15' atomRefs2='phenylethane_3_a5 phenylethane_3_a15'/>"+
		         "<bond order='2' id='phenylethane_3_a6_phenylethane_3_a7' atomRefs2='phenylethane_3_a6 phenylethane_3_a7'/>"+
		         "<bond order='1' id='phenylethane_3_a6_phenylethane_3_a16' atomRefs2='phenylethane_3_a6 phenylethane_3_a16'/>"+
		         "<bond order='1' id='phenylethane_3_a7_phenylethane_3_a8' atomRefs2='phenylethane_3_a7 phenylethane_3_a8'/>"+
		         "<bond order='1' id='phenylethane_3_a7_phenylethane_3_a17' atomRefs2='phenylethane_3_a7 phenylethane_3_a17'/>"+
		         "<bond order='1' id='phenylethane_3_a8_phenylethane_3_a18' atomRefs2='phenylethane_3_a8 phenylethane_3_a18'/>"+
		       "</bondArray>"+
		       "<torsion id='phenylethane_3_main' atomRefs4='phenylethane_3_r1 phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_r2'/>"+
		       "<torsion id='phenylethane_3_side' atomRefs4='phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_a3 phenylethane_3_a4'/>"+
		       "<arg parameterName='main'/>"+
		       "<arg parameterName='side'/>"+
		     "</molecule>"+
		     "<join id='j2' atomRefs2='g:phenylethane_3_r2 g:po_4_r1' moleculeRefs2='g:phenylethane_3 g:po_4' order='S'/>"+
		     "<molecule role='fragment' id='po_4'>"+
		       "<atomArray>"+
		         "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_4_r1'/>"+
		         "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_4_a2'/>"+
		         "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_4_a3'>"+
		           "<label dictRef='cml:torsionEnd'>r1</label>"+
		           "<label dictRef='cml:torsionEnd'>r2</label>"+
		         "</atom>"+
		         "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_4_a4'/>"+
		         "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_4_a5'/>"+
		         "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_4_a7'/>"+
		         "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_4_a8'/>"+
		         "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_4_a9'/>"+
		         "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_4_a10'/>"+
		         "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_4_a11'/>"+
		         "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_4_a12'/>"+
		         "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_4_r2'/>"+
		       "</atomArray>"+
		       "<bondArray>"+
		         "<bond order='1' id='po_4_r1_po_4_a2' atomRefs2='po_4_r1 po_4_a2'/>"+
		         "<bond order='1' id='po_4_a2_po_4_a3' atomRefs2='po_4_a2 po_4_a3'/>"+
		         "<bond order='1' id='po_4_a2_po_4_a7' atomRefs2='po_4_a2 po_4_a7'/>"+
		         "<bond order='1' id='po_4_a2_po_4_a8' atomRefs2='po_4_a2 po_4_a8'/>"+
		         "<bond order='1' id='po_4_a3_po_4_a4' atomRefs2='po_4_a3 po_4_a4'/>"+
		         "<bond order='1' id='po_4_a3_po_4_a5' atomRefs2='po_4_a3 po_4_a5'/>"+
		         "<bond order='1' id='po_4_a3_po_4_a9' atomRefs2='po_4_a3 po_4_a9'/>"+
		         "<bond order='1' id='po_4_a4_po_4_a10' atomRefs2='po_4_a4 po_4_a10'/>"+
		         "<bond order='1' id='po_4_a4_po_4_a11' atomRefs2='po_4_a4 po_4_a11'/>"+
		         "<bond order='1' id='po_4_a4_po_4_a12' atomRefs2='po_4_a4 po_4_a12'/>"+
		         "<bond order='1' id='po_4_a5_po_4_r2' atomRefs2='po_4_a5 po_4_r2'/>"+
		       "</bondArray>"+
		       "<length id='po_4_len23' atomRefs2='po_4_a2 po_4_a3'/>"+
		       "<angle id='po_4_ang234' atomRefs3='po_4_a2 po_4_a3 po_4_a4'/>"+
		       "<angle id='po_4_ang123' atomRefs3='po_4_r1 po_4_a2 po_4_a3'/>"+
		       "<angle id='po_4_ang352' atomRefs3='po_4_a3 po_4_a5 po_4_r2'/>"+
		       "<torsion id='po_4_tor1' atomRefs4='po_4_r1 po_4_a2 po_4_a3 po_4_a5'/>"+
		       "<torsion id='po_4_tor2' atomRefs4='po_4_a2 po_4_a3 po_4_a5 po_4_r2'/>"+
		       "<arg parameterName='len23'/>"+
		       "<arg parameterName='ang234'/>"+
		       "<arg parameterName='ang123'/>"+
		       "<arg parameterName='ang352'/>"+
		       "<arg parameterName='tor1'/>"+
		       "<arg parameterName='tor2'/>"+
		     "</molecule>"+
		     "<join id='j22' atomRefs2='g:po_4_r2 g:po_5_r1' moleculeRefs2='g:po_4 g:po_5' order='S'/>"+
		     "<molecule role='fragment' id='po_5'>"+
		       "<atomArray>"+
		         "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_5_r1'/>"+
		         "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_5_a2'/>"+
		         "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_5_a3'>"+
		           "<label dictRef='cml:torsionEnd'>r1</label>"+
		           "<label dictRef='cml:torsionEnd'>r2</label>"+
		         "</atom>"+
		         "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_5_a4'/>"+
		         "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_5_a5'/>"+
		         "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_5_a7'/>"+
		         "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_5_a8'/>"+
		         "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_5_a9'/>"+
		         "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_5_a10'/>"+
		         "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_5_a11'/>"+
		         "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_5_a12'/>"+
		         "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_5_r2'/>"+
		       "</atomArray>"+
		       "<bondArray>"+
		         "<bond order='1' id='po_5_r1_po_5_a2' atomRefs2='po_5_r1 po_5_a2'/>"+
		         "<bond order='1' id='po_5_a2_po_5_a3' atomRefs2='po_5_a2 po_5_a3'/>"+
		         "<bond order='1' id='po_5_a2_po_5_a7' atomRefs2='po_5_a2 po_5_a7'/>"+
		         "<bond order='1' id='po_5_a2_po_5_a8' atomRefs2='po_5_a2 po_5_a8'/>"+
		         "<bond order='1' id='po_5_a3_po_5_a4' atomRefs2='po_5_a3 po_5_a4'/>"+
		         "<bond order='1' id='po_5_a3_po_5_a5' atomRefs2='po_5_a3 po_5_a5'/>"+
		         "<bond order='1' id='po_5_a3_po_5_a9' atomRefs2='po_5_a3 po_5_a9'/>"+
		         "<bond order='1' id='po_5_a4_po_5_a10' atomRefs2='po_5_a4 po_5_a10'/>"+
		         "<bond order='1' id='po_5_a4_po_5_a11' atomRefs2='po_5_a4 po_5_a11'/>"+
		         "<bond order='1' id='po_5_a4_po_5_a12' atomRefs2='po_5_a4 po_5_a12'/>"+
		         "<bond order='1' id='po_5_a5_po_5_r2' atomRefs2='po_5_a5 po_5_r2'/>"+
		       "</bondArray>"+
		       "<length id='po_5_len23' atomRefs2='po_5_a2 po_5_a3'/>"+
		       "<angle id='po_5_ang234' atomRefs3='po_5_a2 po_5_a3 po_5_a4'/>"+
		       "<angle id='po_5_ang123' atomRefs3='po_5_r1 po_5_a2 po_5_a3'/>"+
		       "<angle id='po_5_ang352' atomRefs3='po_5_a3 po_5_a5 po_5_r2'/>"+
		       "<torsion id='po_5_tor1' atomRefs4='po_5_r1 po_5_a2 po_5_a3 po_5_a5'/>"+
		       "<torsion id='po_5_tor2' atomRefs4='po_5_a2 po_5_a3 po_5_a5 po_5_r2'/>"+
		       "<arg parameterName='len23'/>"+
		       "<arg parameterName='ang234'/>"+
		       "<arg parameterName='ang123'/>"+
		       "<arg parameterName='ang352'/>"+
		       "<arg parameterName='tor1'/>"+
		       "<arg parameterName='tor2'/>"+
		     "</molecule>"+
		     "<join id='j3' atomRefs2='g:po_5_r2 g:adgluc_6_r1' moleculeRefs2='g:po_5 g:adgluc_6' order='S'/>"+
		     "<molecule role='fragment' id='adgluc_6'>"+
		       "<atomArray>"+
		         "<atom elementType='C' x3='1.583' y3='0.519' z3='0.522' id='adgluc_6_a1'/>"+
		         "<atom elementType='H' x3='1.64' y3='0.34' z3='1.595' id='adgluc_6_a2'/>"+
		         "<atom elementType='O' x3='2.844' y3='0.997' z3='0.049' id='adgluc_6_a3'/>"+
		         "<atom elementType='C' x3='0.5' y3='1.56' z3='0.232' id='adgluc_6_a4'/>"+
		         "<atom elementType='H' x3='0.76' y3='2.499' z3='0.721' id='adgluc_6_a5'/>"+
		         "<atom elementType='R' x3='0.4493315255446285' y3='1.6726565017146027' z3='-0.5280271168305728' id='adgluc_6_r1'/>"+
		         "<atom elementType='O' x3='-0.757' y3='1.1' z3='0.727' id='adgluc_6_a7'/>"+
		         "<atom elementType='C' x3='-1.145' y3='-0.025' z3='-0.06' id='adgluc_6_a8'/>"+
		         "<atom elementType='H' x3='-1.105' y3='0.242' z3='-1.116' id='adgluc_6_a9'/>"+
		         "<atom elementType='C' x3='-2.572' y3='-0.435' z3='0.308' id='adgluc_6_a10'/>"+
		         "<atom elementType='O' x3='-3.476' y3='0.61' z3='-0.053' id='adgluc_6_a11'/>"+
		         "<atom elementType='C' x3='-0.196' y3='-1.195' z3='0.205' id='adgluc_6_a12'/>"+
		         "<atom elementType='H' x3='-0.216' y3='-1.449' z3='1.265' id='adgluc_6_a13'/>"+
		         "<atom elementType='O' x3='-0.606' y3='-2.327' z3='-0.565' id='adgluc_6_a14'/>"+
		         "<atom elementType='C' x3='1.228' y3='-0.788' z3='-0.198' id='adgluc_6_a15'/>"+
		         "<atom elementType='H' x3='1.274' y3='-0.636' z3='-1.276' id='adgluc_6_a16'/>"+
		         "<atom elementType='O' x3='2.147' y3='-1.815' z3='0.181' id='adgluc_6_a17'/>"+
		         "<atom elementType='H' x3='3.027' y3='1.819' z3='0.524' id='adgluc_6_a18'/>"+
		         "<atom elementType='H' x3='-2.837' y3='-1.347' z3='-0.227' id='adgluc_6_a20'/>"+
		         "<atom elementType='H' x3='-2.633' y3='-0.614' z3='1.382' id='adgluc_6_a21'/>"+
		         "<atom elementType='H' x3='-4.362' y3='0.312' z3='0.195' id='adgluc_6_a22'/>"+
		         "<atom elementType='R' x3='-0.1353034249184123' y3='-2.864831182745468' z3='-0.4163986774181526' id='adgluc_6_r2'/>"+
		         "<atom elementType='H' x3='3.026' y3='-1.517' z3='-0.09' id='adgluc_6_a24'/>"+
		       "</atomArray>"+
		       "<bondArray>"+
		         "<bond order='1' id='adgluc_6_a1_adgluc_6_a2' atomRefs2='adgluc_6_a1 adgluc_6_a2'/>"+
		         "<bond order='1' id='adgluc_6_a1_adgluc_6_a15' atomRefs2='adgluc_6_a1 adgluc_6_a15'/>"+
		         "<bond order='1' id='adgluc_6_a1_adgluc_6_a3' atomRefs2='adgluc_6_a1 adgluc_6_a3'/>"+
		         "<bond order='1' id='adgluc_6_a1_adgluc_6_a4' atomRefs2='adgluc_6_a1 adgluc_6_a4'/>"+
		         "<bond order='1' id='adgluc_6_a3_adgluc_6_a18' atomRefs2='adgluc_6_a3 adgluc_6_a18'/>"+
		         "<bond order='1' id='adgluc_6_a4_adgluc_6_a5' atomRefs2='adgluc_6_a4 adgluc_6_a5'/>"+
		         "<bond order='1' id='adgluc_6_a4_adgluc_6_r1' atomRefs2='adgluc_6_a4 adgluc_6_r1'/>"+
		         "<bond order='1' id='adgluc_6_a4_adgluc_6_a7' atomRefs2='adgluc_6_a4 adgluc_6_a7'/>"+
		         "<bond order='1' id='adgluc_6_a7_adgluc_6_a8' atomRefs2='adgluc_6_a7 adgluc_6_a8'/>"+
		         "<bond order='1' id='adgluc_6_a8_adgluc_6_a9' atomRefs2='adgluc_6_a8 adgluc_6_a9'/>"+
		         "<bond order='1' id='adgluc_6_a8_adgluc_6_a10' atomRefs2='adgluc_6_a8 adgluc_6_a10'/>"+
		         "<bond order='1' id='adgluc_6_a8_adgluc_6_a12' atomRefs2='adgluc_6_a8 adgluc_6_a12'/>"+
		         "<bond order='1' id='adgluc_6_a10_adgluc_6_a11' atomRefs2='adgluc_6_a10 adgluc_6_a11'/>"+
		         "<bond order='1' id='adgluc_6_a10_adgluc_6_a20' atomRefs2='adgluc_6_a10 adgluc_6_a20'/>"+
		         "<bond order='1' id='adgluc_6_a10_adgluc_6_a21' atomRefs2='adgluc_6_a10 adgluc_6_a21'/>"+
		         "<bond order='1' id='adgluc_6_a11_adgluc_6_a22' atomRefs2='adgluc_6_a11 adgluc_6_a22'/>"+
		         "<bond order='1' id='adgluc_6_a12_adgluc_6_a13' atomRefs2='adgluc_6_a12 adgluc_6_a13'/>"+
		         "<bond order='1' id='adgluc_6_a12_adgluc_6_a14' atomRefs2='adgluc_6_a12 adgluc_6_a14'/>"+
		         "<bond order='1' id='adgluc_6_a12_adgluc_6_a15' atomRefs2='adgluc_6_a12 adgluc_6_a15'/>"+
		         "<bond order='1' id='adgluc_6_a14_adgluc_6_r2' atomRefs2='adgluc_6_a14 adgluc_6_r2'/>"+
		         "<bond order='1' id='adgluc_6_a15_adgluc_6_a16' atomRefs2='adgluc_6_a15 adgluc_6_a16'/>"+
		         "<bond order='1' id='adgluc_6_a15_adgluc_6_a17' atomRefs2='adgluc_6_a15 adgluc_6_a17'/>"+
		         "<bond order='1' id='adgluc_6_a17_adgluc_6_a24' atomRefs2='adgluc_6_a17 adgluc_6_a24'/>"+
		       "</bondArray>"+
		       "<torsion id='adgluc_6_tau' atomRefs4='adgluc_6_a15 adgluc_6_a12 adgluc_6_a14 adgluc_6_r2'/>"+
		       "<arg parameterName='tau'/>"+
		     "</molecule>"+
		   "</join>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_2_r1 g:acetyl_7_r1' moleculeRefs2='g:2pyr_2 g:acetyl_7' order='S'/>"+
		 "<molecule role='fragment' id='acetyl_7'>"+
		   "<atomArray>"+
		     "<atom elementType='R' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0' x3='6.858584288887507' y3='7.368205459562712' z3='1.672120540673427' id='acetyl_7_r1'/>"+
		     "<atom elementType='C' x3='7.470354790502688' y3='7.21912165' z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_7_a71'/>"+
		     "<atom elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_7_a72'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.407334871591223' y3='8.375034459999998' z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_7_a73'/>"+
		     "<atom elementType='H' x3='9.290344900009062' y3='8.0103218' z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_7_a74'/>"+
		     "<atom elementType='H' x3='8.339122721687978' y3='8.830276' z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_7_a75'/>"+
		     "<atom elementType='H' x3='8.236429704800676' y3='8.9953799' z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_7_a76'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='S' id='acetyl_7_r1_acetyl_7_a71' atomRefs2='acetyl_7_r1 acetyl_7_a71'/>"+
		     "<bond order='D' id='acetyl_7_a71_acetyl_7_a72' atomRefs2='acetyl_7_a71 acetyl_7_a72'/>"+
		     "<bond order='S' id='acetyl_7_a71_acetyl_7_a73' atomRefs2='acetyl_7_a71 acetyl_7_a73'/>"+
		     "<bond order='S' id='acetyl_7_a73_acetyl_7_a74' atomRefs2='acetyl_7_a73 acetyl_7_a74'/>"+
		     "<bond order='S' id='acetyl_7_a73_acetyl_7_a75' atomRefs2='acetyl_7_a73 acetyl_7_a75'/>"+
		     "<bond order='S' id='acetyl_7_a73_acetyl_7_a76' atomRefs2='acetyl_7_a73 acetyl_7_a76'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		CMLFragment explicit = (CMLFragment) parseValidString(explicitS);
		AbstractTest.assertEqualsCanonically("fragment", explicit, fragment, true);
		outputXML("explicit3", explicit);
	}
	
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * * test basic concatenation
	 */
	@Test
	public void testProcessExplicit3() {
		CMLFragment fragment = makeMol3();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
		
		CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
		
		fragmentTool.processIntermediate(moleculeCatalog);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();
//		fragment.debug("EXPLICIT3");

		String explicitS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			 "<molecule role='fragment' id='propanoic_1'>"+
			   "<atomArray>"+
			     "<atom elementType='C' x3='1.998' y3='-0.064' z3='-0.0' id='propanoic_1_a1'/>"+
			     "<atom elementType='C' x3='0.757' y3='0.831' z3='-0.0' id='propanoic_1_a2'/>"+
			     "<atom elementType='C' x3='-0.482' y3='-0.028' z3='0.0' id='propanoic_1_a3'/>"+
			     "<atom elementType='O' formalCharge='-1' x3='-0.382' y3='-1.232' z3='0.0' id='propanoic_1_a4'/>"+
			     "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='propanoic_1_a5'/>"+
			     "<atom elementType='H' x3='1.992' y3='-0.694' z3='0.89' id='propanoic_1_a6'/>"+
			     "<atom elementType='H' x3='1.992' y3='-0.694' z3='-0.89' id='propanoic_1_a7'/>"+
			     "<atom elementType='H' x3='2.893' y3='0.557' z3='-0.0' id='propanoic_1_a8'/>"+
			     "<atom elementType='H' x3='0.763' y3='1.46' z3='0.89' id='propanoic_1_a9'/>"+
			     "<atom elementType='H' x3='0.763' y3='1.46' z3='-0.89' id='propanoic_1_a10'/>"+
			     "<atom elementType='H' x3='-2.49' y3='-0.0050' z3='0.0' id='propanoic_1_a11'/>"+
			     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_2_a1'> </atom>"+
			     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_2_a2'> </atom>"+
			     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_2_r2'> </atom>"+
			     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_2_a3'> </atom>"+
			     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_2_a4'> </atom>"+
			     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_2_a5'> </atom>"+
			     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_2_r5'> </atom>"+
			     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_2_a6'> </atom>"+
			     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_2_r6'> </atom>"+
			     "<atom elementType='C' id='phenylethane_3_a1' x3='-0.5117930928449101' y3='-0.2505993858491189' z3='26.318856737934468'>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='phenylethane_3_a2' x3='-1.1375615018286562' y3='-0.95934993942201' z3='25.11641607364867'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='phenylethane_3_a3' x3='-1.3919593483058188' y3='-2.4043219532310007' z3='25.46120385622887'/>"+
			     "<atom elementType='C' id='phenylethane_3_a4' x3='-2.6009986802806946' y3='-2.7788683278153634' z3='26.017969432592437'/>"+
			     "<atom elementType='C' id='phenylethane_3_a5' x3='-2.8342001783580253' y3='-4.103674404624375' z3='26.33419391005174'/>"+
			     "<atom elementType='C' id='phenylethane_3_a6' x3='-1.8604228823749802' y3='-5.054423739570174' z3='26.094370058176946'/>"+
			     "<atom elementType='C' id='phenylethane_3_a7' x3='-0.6517195513579213' y3='-4.679842095586196' z3='25.536663280758447'/>"+
			     "<atom elementType='C' id='phenylethane_3_a8' x3='-0.4201980580696736' y3='-3.3548596717790944' z3='25.215732798024508'/>"+
			     "<atom elementType='H' id='phenylethane_3_a9' x3='-1.1923920710514047' y3='-0.30608061397770314' z3='27.168542482306833'/>"+
			     "<atom elementType='H' id='phenylethane_3_a10' x3='0.43053540006670105' y3='-0.7347519103127831' z3='26.57629477805751'/>"+
			     "<atom elementType='R' id='phenylethane_3_r2' x3='-1.8030859917813482' y3='-0.6174146542992967' z3='24.934599094075093'/>"+
			     "<atom elementType='H' id='phenylethane_3_a13' x3='-0.4569625236221618' y3='-0.9038687112934263' z3='24.266730329276303'/>"+
			     "<atom elementType='H' id='phenylethane_3_a14' x3='-3.3622857806039836' y3='-2.036416527177157' z3='26.2060047340931'/>"+
			     "<atom elementType='H' id='phenylethane_3_a15' x3='-3.778672953981672' y3='-4.396600040491627' z3='26.769820754905833'/>"+
			     "<atom elementType='H' id='phenylethane_3_a16' x3='-2.042697233317224' y3='-6.090041398216721' z3='26.341627264518902'/>"+
			     "<atom elementType='H' id='phenylethane_3_a17' x3='0.10956754896536736' y3='-5.422293896224403' z3='25.348627979257788'/>"+
			     "<atom elementType='H' id='phenylethane_3_a18' x3='0.5240368283557013' y3='-3.062904112915166' z3='24.780154526176943'/>"+
			     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_4_r1'/>"+
			     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_4_a2'/>"+
			     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_4_a3'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_4_a4'/>"+
			     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_4_a5'/>"+
			     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_4_a7'/>"+
			     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_4_a8'/>"+
			     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_4_a9'/>"+
			     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_4_a10'/>"+
			     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_4_a11'/>"+
			     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_4_a12'/>"+
			     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_4_r2'/>"+
			     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_5_r1'/>"+
			     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_5_a2'/>"+
			     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_5_a3'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_5_a4'/>"+
			     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_5_a5'/>"+
			     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_5_a7'/>"+
			     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_5_a8'/>"+
			     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_5_a9'/>"+
			     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_5_a10'/>"+
			     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_5_a11'/>"+
			     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_5_a12'/>"+
			     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_5_r2'/>"+
			     "<atom elementType='C' x3='1.583' y3='0.519' z3='0.522' id='adgluc_6_a1'/>"+
			     "<atom elementType='H' x3='1.64' y3='0.34' z3='1.595' id='adgluc_6_a2'/>"+
			     "<atom elementType='O' x3='2.844' y3='0.997' z3='0.049' id='adgluc_6_a3'/>"+
			     "<atom elementType='C' x3='0.5' y3='1.56' z3='0.232' id='adgluc_6_a4'/>"+
			     "<atom elementType='H' x3='0.76' y3='2.499' z3='0.721' id='adgluc_6_a5'/>"+
			     "<atom elementType='R' x3='0.4493315255446285' y3='1.6726565017146027' z3='-0.5280271168305728' id='adgluc_6_r1'/>"+
			     "<atom elementType='O' x3='-0.757' y3='1.1' z3='0.727' id='adgluc_6_a7'/>"+
			     "<atom elementType='C' x3='-1.145' y3='-0.025' z3='-0.06' id='adgluc_6_a8'/>"+
			     "<atom elementType='H' x3='-1.105' y3='0.242' z3='-1.116' id='adgluc_6_a9'/>"+
			     "<atom elementType='C' x3='-2.572' y3='-0.435' z3='0.308' id='adgluc_6_a10'/>"+
			     "<atom elementType='O' x3='-3.476' y3='0.61' z3='-0.053' id='adgluc_6_a11'/>"+
			     "<atom elementType='C' x3='-0.196' y3='-1.195' z3='0.205' id='adgluc_6_a12'/>"+
			     "<atom elementType='H' x3='-0.216' y3='-1.449' z3='1.265' id='adgluc_6_a13'/>"+
			     "<atom elementType='O' x3='-0.606' y3='-2.327' z3='-0.565' id='adgluc_6_a14'/>"+
			     "<atom elementType='C' x3='1.228' y3='-0.788' z3='-0.198' id='adgluc_6_a15'/>"+
			     "<atom elementType='H' x3='1.274' y3='-0.636' z3='-1.276' id='adgluc_6_a16'/>"+
			     "<atom elementType='O' x3='2.147' y3='-1.815' z3='0.181' id='adgluc_6_a17'/>"+
			     "<atom elementType='H' x3='3.027' y3='1.819' z3='0.524' id='adgluc_6_a18'/>"+
			     "<atom elementType='H' x3='-2.837' y3='-1.347' z3='-0.227' id='adgluc_6_a20'/>"+
			     "<atom elementType='H' x3='-2.633' y3='-0.614' z3='1.382' id='adgluc_6_a21'/>"+
			     "<atom elementType='H' x3='-4.362' y3='0.312' z3='0.195' id='adgluc_6_a22'/>"+
			     "<atom elementType='R' x3='-0.1353034249184123' y3='-2.864831182745468' z3='-0.4163986774181526' id='adgluc_6_r2'/>"+
			     "<atom elementType='H' x3='3.026' y3='-1.517' z3='-0.09' id='adgluc_6_a24'/>"+
			     "<atom elementType='C' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_7_a71' x3='1.914681164526272' y3='3.388878044146068' z3='23.694901054810863'> </atom>"+
			     "<atom elementType='O' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_7_a72' x3='2.9081090547840707' y3='2.814218656137492' z3='23.39249970123778'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='C' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_7_a73' x3='1.453195965608384' y3='4.687446861794651' z3='23.133356755456333'/>"+
			     "<atom elementType='H' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_7_a74' x3='1.5779648093555518' y3='4.63487681817854' z3='22.18272401284795'/>"+
			     "<atom elementType='H' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_7_a75' x3='2.0111847192444827' y3='5.382001419118829' z3='23.491746925232146'/>"+
			     "<atom elementType='H' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_7_a76' x3='0.5335876734606375' y3='4.885333446140825' z3='23.326116502857605'/>"+
			   "</atomArray>"+
			   "<bondArray>"+
			     "<bond order='1' id='propanoic_1_a1_propanoic_1_a2' atomRefs2='propanoic_1_a1 propanoic_1_a2'/>"+
			     "<bond order='1' id='propanoic_1_a1_propanoic_1_a6' atomRefs2='propanoic_1_a1 propanoic_1_a6'/>"+
			     "<bond order='1' id='propanoic_1_a1_propanoic_1_a7' atomRefs2='propanoic_1_a1 propanoic_1_a7'/>"+
			     "<bond order='1' id='propanoic_1_a1_propanoic_1_a8' atomRefs2='propanoic_1_a1 propanoic_1_a8'/>"+
			     "<bond order='1' id='propanoic_1_a2_propanoic_1_a3' atomRefs2='propanoic_1_a2 propanoic_1_a3'/>"+
			     "<bond order='1' id='propanoic_1_a2_propanoic_1_a9' atomRefs2='propanoic_1_a2 propanoic_1_a9'/>"+
			     "<bond order='1' id='propanoic_1_a2_propanoic_1_a10' atomRefs2='propanoic_1_a2 propanoic_1_a10'/>"+
			     "<bond order='2' id='propanoic_1_a3_propanoic_1_a4' atomRefs2='propanoic_1_a3 propanoic_1_a4'/>"+
			     "<bond order='1' id='propanoic_1_a3_propanoic_1_a5' atomRefs2='propanoic_1_a3 propanoic_1_a5'/>"+
			     "<bond order='1' id='propanoic_1_a5_propanoic_1_a11' atomRefs2='propanoic_1_a5 propanoic_1_a11'/>"+
			     "<bond order='2' id='2pyr_2_a1_2pyr_2_a2' atomRefs2='2pyr_2_a1 2pyr_2_a2'/>"+
			     "<bond order='1' id='2pyr_2_a1_2pyr_2_a3' atomRefs2='2pyr_2_a1 2pyr_2_a3'/>"+
			     "<bond order='1' id='2pyr_2_a2_2pyr_2_r2' atomRefs2='2pyr_2_a2 2pyr_2_r2'/>"+
			     "<bond order='1' id='2pyr_2_a2_2pyr_2_a4' atomRefs2='2pyr_2_a2 2pyr_2_a4'/>"+
			     "<bond order='2' id='2pyr_2_a6_2pyr_2_a3' atomRefs2='2pyr_2_a6 2pyr_2_a3'/>"+
			     "<bond order='2' id='2pyr_2_a4_2pyr_2_a5' atomRefs2='2pyr_2_a4 2pyr_2_a5'/>"+
			     "<bond order='1' id='2pyr_2_a5_2pyr_2_r5' atomRefs2='2pyr_2_a5 2pyr_2_r5'/>"+
			     "<bond order='1' id='2pyr_2_a5_2pyr_2_a6' atomRefs2='2pyr_2_a5 2pyr_2_a6'/>"+
			     "<bond order='1' id='2pyr_2_a6_2pyr_2_r6' atomRefs2='2pyr_2_a6 2pyr_2_r6'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a2' atomRefs2='phenylethane_3_a1 phenylethane_3_a2'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a9' atomRefs2='phenylethane_3_a1 phenylethane_3_a9'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a10' atomRefs2='phenylethane_3_a1 phenylethane_3_a10'/>"+
			     "<bond order='1' id='phenylethane_3_a2_phenylethane_3_a3' atomRefs2='phenylethane_3_a2 phenylethane_3_a3'/>"+
			     "<bond order='1' id='phenylethane_3_a2_phenylethane_3_r2' atomRefs2='phenylethane_3_a2 phenylethane_3_r2'/>"+
			     "<bond order='1' id='phenylethane_3_a2_phenylethane_3_a13' atomRefs2='phenylethane_3_a2 phenylethane_3_a13'/>"+
			     "<bond order='2' id='phenylethane_3_a3_phenylethane_3_a8' atomRefs2='phenylethane_3_a3 phenylethane_3_a8'/>"+
			     "<bond order='1' id='phenylethane_3_a3_phenylethane_3_a4' atomRefs2='phenylethane_3_a3 phenylethane_3_a4'/>"+
			     "<bond order='2' id='phenylethane_3_a4_phenylethane_3_a5' atomRefs2='phenylethane_3_a4 phenylethane_3_a5'/>"+
			     "<bond order='1' id='phenylethane_3_a4_phenylethane_3_a14' atomRefs2='phenylethane_3_a4 phenylethane_3_a14'/>"+
			     "<bond order='1' id='phenylethane_3_a5_phenylethane_3_a6' atomRefs2='phenylethane_3_a5 phenylethane_3_a6'/>"+
			     "<bond order='1' id='phenylethane_3_a5_phenylethane_3_a15' atomRefs2='phenylethane_3_a5 phenylethane_3_a15'/>"+
			     "<bond order='2' id='phenylethane_3_a6_phenylethane_3_a7' atomRefs2='phenylethane_3_a6 phenylethane_3_a7'/>"+
			     "<bond order='1' id='phenylethane_3_a6_phenylethane_3_a16' atomRefs2='phenylethane_3_a6 phenylethane_3_a16'/>"+
			     "<bond order='1' id='phenylethane_3_a7_phenylethane_3_a8' atomRefs2='phenylethane_3_a7 phenylethane_3_a8'/>"+
			     "<bond order='1' id='phenylethane_3_a7_phenylethane_3_a17' atomRefs2='phenylethane_3_a7 phenylethane_3_a17'/>"+
			     "<bond order='1' id='phenylethane_3_a8_phenylethane_3_a18' atomRefs2='phenylethane_3_a8 phenylethane_3_a18'/>"+
			     "<bond atomRefs2='2pyr_2_a4 phenylethane_3_a1' id='j1' order='S'/>"+
			     "<bond order='1' id='po_4_r1_po_4_a2' atomRefs2='po_4_r1 po_4_a2'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a3' atomRefs2='po_4_a2 po_4_a3'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a7' atomRefs2='po_4_a2 po_4_a7'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a8' atomRefs2='po_4_a2 po_4_a8'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a4' atomRefs2='po_4_a3 po_4_a4'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a5' atomRefs2='po_4_a3 po_4_a5'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a9' atomRefs2='po_4_a3 po_4_a9'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a10' atomRefs2='po_4_a4 po_4_a10'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a11' atomRefs2='po_4_a4 po_4_a11'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a12' atomRefs2='po_4_a4 po_4_a12'/>"+
			     "<bond order='1' id='po_4_a5_po_4_r2' atomRefs2='po_4_a5 po_4_r2'/>"+
			     "<bond order='1' id='po_5_r1_po_5_a2' atomRefs2='po_5_r1 po_5_a2'/>"+
			     "<bond order='1' id='po_5_a2_po_5_a3' atomRefs2='po_5_a2 po_5_a3'/>"+
			     "<bond order='1' id='po_5_a2_po_5_a7' atomRefs2='po_5_a2 po_5_a7'/>"+
			     "<bond order='1' id='po_5_a2_po_5_a8' atomRefs2='po_5_a2 po_5_a8'/>"+
			     "<bond order='1' id='po_5_a3_po_5_a4' atomRefs2='po_5_a3 po_5_a4'/>"+
			     "<bond order='1' id='po_5_a3_po_5_a5' atomRefs2='po_5_a3 po_5_a5'/>"+
			     "<bond order='1' id='po_5_a3_po_5_a9' atomRefs2='po_5_a3 po_5_a9'/>"+
			     "<bond order='1' id='po_5_a4_po_5_a10' atomRefs2='po_5_a4 po_5_a10'/>"+
			     "<bond order='1' id='po_5_a4_po_5_a11' atomRefs2='po_5_a4 po_5_a11'/>"+
			     "<bond order='1' id='po_5_a4_po_5_a12' atomRefs2='po_5_a4 po_5_a12'/>"+
			     "<bond order='1' id='po_5_a5_po_5_r2' atomRefs2='po_5_a5 po_5_r2'/>"+
			     "<bond order='1' id='adgluc_6_a1_adgluc_6_a2' atomRefs2='adgluc_6_a1 adgluc_6_a2'/>"+
			     "<bond order='1' id='adgluc_6_a1_adgluc_6_a15' atomRefs2='adgluc_6_a1 adgluc_6_a15'/>"+
			     "<bond order='1' id='adgluc_6_a1_adgluc_6_a3' atomRefs2='adgluc_6_a1 adgluc_6_a3'/>"+
			     "<bond order='1' id='adgluc_6_a1_adgluc_6_a4' atomRefs2='adgluc_6_a1 adgluc_6_a4'/>"+
			     "<bond order='1' id='adgluc_6_a3_adgluc_6_a18' atomRefs2='adgluc_6_a3 adgluc_6_a18'/>"+
			     "<bond order='1' id='adgluc_6_a4_adgluc_6_a5' atomRefs2='adgluc_6_a4 adgluc_6_a5'/>"+
			     "<bond order='1' id='adgluc_6_a4_adgluc_6_r1' atomRefs2='adgluc_6_a4 adgluc_6_r1'/>"+
			     "<bond order='1' id='adgluc_6_a4_adgluc_6_a7' atomRefs2='adgluc_6_a4 adgluc_6_a7'/>"+
			     "<bond order='1' id='adgluc_6_a7_adgluc_6_a8' atomRefs2='adgluc_6_a7 adgluc_6_a8'/>"+
			     "<bond order='1' id='adgluc_6_a8_adgluc_6_a9' atomRefs2='adgluc_6_a8 adgluc_6_a9'/>"+
			     "<bond order='1' id='adgluc_6_a8_adgluc_6_a10' atomRefs2='adgluc_6_a8 adgluc_6_a10'/>"+
			     "<bond order='1' id='adgluc_6_a8_adgluc_6_a12' atomRefs2='adgluc_6_a8 adgluc_6_a12'/>"+
			     "<bond order='1' id='adgluc_6_a10_adgluc_6_a11' atomRefs2='adgluc_6_a10 adgluc_6_a11'/>"+
			     "<bond order='1' id='adgluc_6_a10_adgluc_6_a20' atomRefs2='adgluc_6_a10 adgluc_6_a20'/>"+
			     "<bond order='1' id='adgluc_6_a10_adgluc_6_a21' atomRefs2='adgluc_6_a10 adgluc_6_a21'/>"+
			     "<bond order='1' id='adgluc_6_a11_adgluc_6_a22' atomRefs2='adgluc_6_a11 adgluc_6_a22'/>"+
			     "<bond order='1' id='adgluc_6_a12_adgluc_6_a13' atomRefs2='adgluc_6_a12 adgluc_6_a13'/>"+
			     "<bond order='1' id='adgluc_6_a12_adgluc_6_a14' atomRefs2='adgluc_6_a12 adgluc_6_a14'/>"+
			     "<bond order='1' id='adgluc_6_a12_adgluc_6_a15' atomRefs2='adgluc_6_a12 adgluc_6_a15'/>"+
			     "<bond order='1' id='adgluc_6_a14_adgluc_6_r2' atomRefs2='adgluc_6_a14 adgluc_6_r2'/>"+
			     "<bond order='1' id='adgluc_6_a15_adgluc_6_a16' atomRefs2='adgluc_6_a15 adgluc_6_a16'/>"+
			     "<bond order='1' id='adgluc_6_a15_adgluc_6_a17' atomRefs2='adgluc_6_a15 adgluc_6_a17'/>"+
			     "<bond order='1' id='adgluc_6_a17_adgluc_6_a24' atomRefs2='adgluc_6_a17 adgluc_6_a24'/>"+
			     "<bond order='D' id='acetyl_7_a71_acetyl_7_a72' atomRefs2='acetyl_7_a71 acetyl_7_a72'/>"+
			     "<bond order='S' id='acetyl_7_a71_acetyl_7_a73' atomRefs2='acetyl_7_a71 acetyl_7_a73'/>"+
			     "<bond order='S' id='acetyl_7_a73_acetyl_7_a74' atomRefs2='acetyl_7_a73 acetyl_7_a74'/>"+
			     "<bond order='S' id='acetyl_7_a73_acetyl_7_a75' atomRefs2='acetyl_7_a73 acetyl_7_a75'/>"+
			     "<bond order='S' id='acetyl_7_a73_acetyl_7_a76' atomRefs2='acetyl_7_a73 acetyl_7_a76'/>"+
			     "<bond atomRefs2='2pyr_2_a1 acetyl_7_a71' order='S' id='2pyr_2_a1_acetyl_7_a71'/>"+
			   "</bondArray>"+
			   "<torsion id='adgluc_6_tau' atomRefs4='adgluc_6_a15 adgluc_6_a12 adgluc_6_a14 adgluc_6_r2'/>"+
			   "<arg parameterName='tau'/>"+
			   "<length id='po_5_len23' atomRefs2='po_5_a2 po_5_a3'/>"+
			   "<angle id='po_5_ang234' atomRefs3='po_5_a2 po_5_a3 po_5_a4'/>"+
			   "<angle id='po_5_ang123' atomRefs3='po_5_r1 po_5_a2 po_5_a3'/>"+
			   "<angle id='po_5_ang352' atomRefs3='po_5_a3 po_5_a5 po_5_r2'/>"+
			   "<torsion id='po_5_tor1' atomRefs4='po_5_r1 po_5_a2 po_5_a3 po_5_a5'/>"+
			   "<torsion id='po_5_tor2' atomRefs4='po_5_a2 po_5_a3 po_5_a5 po_5_r2'/>"+
			   "<arg parameterName='len23'/>"+
			   "<arg parameterName='ang234'/>"+
			   "<arg parameterName='ang123'/>"+
			   "<arg parameterName='ang352'/>"+
			   "<arg parameterName='tor1'/>"+
			   "<arg parameterName='tor2'/>"+
			   "<length id='po_4_len23' atomRefs2='po_4_a2 po_4_a3'/>"+
			   "<angle id='po_4_ang234' atomRefs3='po_4_a2 po_4_a3 po_4_a4'/>"+
			   "<angle id='po_4_ang123' atomRefs3='po_4_r1 po_4_a2 po_4_a3'/>"+
			   "<angle id='po_4_ang352' atomRefs3='po_4_a3 po_4_a5 po_4_r2'/>"+
			   "<torsion id='po_4_tor1' atomRefs4='po_4_r1 po_4_a2 po_4_a3 po_4_a5'/>"+
			   "<torsion id='po_4_tor2' atomRefs4='po_4_a2 po_4_a3 po_4_a5 po_4_r2'/>"+
			   "<arg parameterName='len23'/>"+
			   "<arg parameterName='ang234'/>"+
			   "<arg parameterName='ang123'/>"+
			   "<arg parameterName='ang352'/>"+
			   "<arg parameterName='tor1'/>"+
			   "<arg parameterName='tor2'/>"+
			   "<torsion id='phenylethane_3_main' atomRefs4='phenylethane_3_r1 phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_r2'/>"+
			   "<torsion id='phenylethane_3_side' atomRefs4='phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_a3 phenylethane_3_a4'/>"+
			   "<arg parameterName='main'/>"+
			   "<arg parameterName='side'/>"+
			 "</molecule>"+
			 "<join atomRefs2='g:propanoic_1_r1 g:2pyr_2_r2' moleculeRefs2='g:propanoic_1 g:2pyr_2' order='S'/>"+
			"</fragment>";
		CMLFragment fragmentE = (CMLFragment) parseValidString(explicitS);
//		assertEqualsCanonically("explicit", fragmentE, fragment, true);
		outputXML("complete3", fragment);
	}


	private CMLFragment makeMol4() {
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'>"+
		"  <fragment>"+
		"    <molecule ref='ref1'/>" +
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment countExpression='*(2)'>"+
		"    <join atomRefs2='r3 r4' moleculeRefs2='PREVIOUS NEXT'/>"+
		"    <fragment>"+
		"      <fragment countExpression='*(3)'>"+
		"        <join atomRefs2='r5 r6' moleculeRefs2='PREVIOUS NEXT'/>"+
		"        <fragment>" +
		"          <molecule ref='ref21'/>" +
		"        </fragment>"+
		"      </fragment>"+
		"      <join atomRefs2='r7 r8' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment countExpression='*(4)'>"+
		"        <join atomRefs2='r9 r10' moleculeRefs2='PREVIOUS NEXT'/>"+
		"        <fragment>" +
		"          <molecule ref='ref22'/>" +
		"        </fragment>"+
		"      </fragment>"+
		"    </fragment>"+
		"  </fragment>"+
		"  <join atomRefs2='r11 r12' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='ref3'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#processBasic(org.xmlcml.cml.element.CMLMolecule)}.
	 * Tests nested fragmentList expansion
	 * form A-j-(-(-j-B)*n-(-j-C)*m)*p-j-C
	 */
	@Test
	public void testProcessBasic4() {
		CMLFragment fragment = makeMol4();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processBasic();
//		fragment.debug("BASIC4");
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns='http://www.xml-cml.org/schema'>"+
		  "<molecule ref='ref1' id='1'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>1</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref1_1_r1 ref21_2_r2' moleculeRefs2='ref1_1 ref21_2'/>"+
		  "<molecule ref='ref21' id='2'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>2</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_2_r5 ref21_3_r6' moleculeRefs2='ref21_2 ref21_3'/>"+
		  "<molecule ref='ref21' id='3'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>3</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_3_r5 ref21_4_r6' moleculeRefs2='ref21_3 ref21_4'/>"+
		  "<molecule ref='ref21' id='4'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>4</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_4_r7 ref22_5_r8' moleculeRefs2='ref21_4 ref22_5'/>"+
		  "<molecule ref='ref22' id='5'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>5</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_5_r9 ref22_6_r10' moleculeRefs2='ref22_5 ref22_6'/>"+
		  "<molecule ref='ref22' id='6'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>6</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_6_r9 ref22_7_r10' moleculeRefs2='ref22_6 ref22_7'/>"+
		  "<molecule ref='ref22' id='7'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>7</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_7_r9 ref22_8_r10' moleculeRefs2='ref22_7 ref22_8'/>"+
		  "<molecule ref='ref22' id='8'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>8</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_8_r3 ref21_9_r4' moleculeRefs2='ref22_8 ref21_9'/>"+
		  "<molecule ref='ref21' id='9'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>9</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_9_r5 ref21_10_r6' moleculeRefs2='ref21_9 ref21_10'/>"+
		  "<molecule ref='ref21' id='10'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>10</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_10_r5 ref21_11_r6' moleculeRefs2='ref21_10 ref21_11'/>"+
		  "<molecule ref='ref21' id='11'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>11</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref21_11_r7 ref22_12_r8' moleculeRefs2='ref21_11 ref22_12'/>"+
		  "<molecule ref='ref22' id='12'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>12</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_12_r9 ref22_13_r10' moleculeRefs2='ref22_12 ref22_13'/>"+
		  "<molecule ref='ref22' id='13'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>13</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_13_r9 ref22_14_r10' moleculeRefs2='ref22_13 ref22_14'/>"+
		  "<molecule ref='ref22' id='14'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>14</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_14_r9 ref22_15_r10' moleculeRefs2='ref22_14 ref22_15'/>"+
		  "<molecule ref='ref22' id='15'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>15</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		  "<join atomRefs2='ref22_15_r11 ref3_16_r12' moleculeRefs2='ref22_15 ref3_16'/>"+
		  "<molecule ref='ref3' id='16'>"+
		    "<arg name='idx'>"+
		      "<scalar dataType='xsd:string'>16</scalar>"+
		    "</arg>"+
		  "</molecule>"+
		"</fragment>";
        CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
		AbstractTest.assertEqualsCanonically("fragment", intermediate, fragment, true);
	}


	public void testExplicit() {
		// this is an example of explicit
//	<molecule id="etoac" convention="cml:PML-explicit" xmlns:g="http://www.xml-cml.org/mols/geom" xmlns="http://www.xml-cml.org/schema">
//	  <molecule role="fragment" id="acetate_1">
//	    <atomArray>
//	      <atom elementType="C" hydrogenCount="3" id="acetate_1_a1"></atom>
//	      <atom elementType="C" hydrogenCount="0" id="acetate_1_a2"></atom>
//	      <atom elementType="O" hydrogenCount="0" id="acetate_1_a3"></atom>
//	      <atom elementType="O" hydrogenCount="0" id="acetate_1_a4"></atom>
//	      <atom elementType="R" id="acetate_1_r1"></atom>
//	    </atomArray>
//	    <bondArray>
//	      <bond order="1" id="acetate_1_a1_acetate_1_a2" atomRefs2="acetate_1_a1 acetate_1_a2"></bond>
//	      <bond order="2" id="acetate_1_a2_acetate_1_a3" atomRefs2="acetate_1_a2 acetate_1_a3"></bond>
//	      <bond order="1" id="acetate_1_a2_acetate_1_a4" atomRefs2="acetate_1_a2 acetate_1_a4"></bond>
//	      <bond order="1" id="acetate_1_a4_acetate_1_r1" atomRefs2="acetate_1_a4 acetate_1_r1"></bond>
//	    </bondArray>
//	    <name>acetate_1</name>
//	    <length atomRefs2="acetate_1_a1 acetate_1_a2">1.52</length>
//	    <length atomRefs2="acetate_1_a2 acetate_1_a3">1.25</length>
//	    <angle atomRefs3="acetate_1_a1 acetate_1_a2 acetate_1_a3">111</angle>
//	    <length atomRefs2="acetate_1_a2 acetate_1_a4">1.40</length>
//	    <angle atomRefs3="acetate_1_a1 acetate_1_a2 acetate_1_a4">111</angle>
//	    <torsion atomRefs4="acetate_1_a1 acetate_1_a4 acetate_1_a2 acetate_1_a3">180</torsion>
//	    <angle atomRefs3="acetate_1_a2 acetate_1_a4 acetate_1_r1">120</angle>
//	    <torsion atomRefs4="acetate_1_a1 acetate_1_a2 acetate_1_a4 acetate_1_r1">180</torsion>
//	  </molecule>
//	  <join atomRefs2="g:acetate_1_r1 g:et_2_r1" id="g:acetate_1_r1_g:et_2_r1" order="1">
//	    <length>1.35</length>
//	    <torsion>180.0</torsion>
//	  </join>
//	  <molecule role="fragment" id="et_2">
//	    <atomArray>
//	      <atom elementType="C" hydrogenCount="3" id="et_2_a1"></atom>
//	      <atom elementType="C" hydrogenCount="2" id="et_2_a2"></atom>
//	      <atom elementType="R" id="et_2_r1"></atom>
//	    </atomArray>
//	    <bondArray>
//	      <bond order="1" id="et_2_a1_et_2_a2" atomRefs2="et_2_a1 et_2_a2"></bond>
//	      <bond order="1" id="et_2_a2_et_2_r1" atomRefs2="et_2_a2 et_2_r1"></bond>
//	    </bondArray>
//	    <name>ethyl_2</name>
//	    <length atomRefs2="et_2_a1 et_2_a2">1.54</length>
//	    <angle atomRefs3="et_2_a1 et_2_a2 et_2_r1">111</angle>
//	  </molecule>
//	</molecule>
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#expandCountExpressions()}.
	 */
	@Test
	@Ignore
	public void testExpandCountExpressions() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#recursivelyCreateAtomsRefs2OnJoins(org.xmlcml.cml.element.CMLMolecule)}.
	 */
	@Test
	@Ignore
	public void testRecursivelyCreateAtomsRefs2OnJoins() {
		fail("Not yet implemented");
	}

	/** test expandJoinedList.
	 */
	@Test
	public final void testExpandJoinedList() {
		CMLFragment fragment = new CMLFragment();
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.expandJoinedList();
		
		CMLJoin join = new CMLJoin();
		join.setAtomRefs2(new String[]{"r1", "r2"});
		join.setMoleculeRefs2(new String[]{
				CMLJoin.PREVIOUS_S, CMLJoin.NEXT_S});
		fragment.appendChild(join);
		
		CMLFragment fragmentChild = new CMLFragment();
//		fragmentChild.setId("child");
		CMLMolecule molecule = new CMLMolecule();
		molecule.setRef("ref1");
		new FragmentTool(fragmentChild).setMolecule(molecule);
		fragment.appendChild(fragmentChild);
		
		fragment.setCountExpression("*(3)");
		
		// countExpression required
		fragmentTool.expandJoinedList();
		
		fragmentTool.expandJoinedList();
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'>"+
		"  <fragment>"+
		"    <molecule ref='ref1'/>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='ref1'/>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"  <fragment>"+
		"    <molecule ref='ref1'/>"+
		"  </fragment>"+
		"</fragment>";
		CMLFragment reference = (CMLFragment) parseValidString(fragmentS);
		CMLUtil.removeWhitespaceNodes(reference);
		AbstractTest.assertEqualsCanonically("fragment", reference, fragment);
	}
	
	/** */
	@Test
	public void testProcessRecursively() {
		String[] ATOMREFS2 = new String[]{"r2", "r1"};
		String[] MOLREFS2 = new String[]{CMLJoin.PREVIOUS_S, CMLJoin.NEXT_S};
		CMLFragment fragment = new CMLFragment();
		@SuppressWarnings("unused")
		FragmentTool fragmentTool = new FragmentTool(fragment);
		CMLFragment fragment1 = new CMLFragment();
		fragment1.setId("f1");
		fragment.appendChild(fragment1);
		CMLJoin join = new CMLJoin();
		join.setId("j1");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f2");
		fragment.appendChild(fragment1);
		CMLMolecule benzene = new CMLMolecule();
		fragment1.appendChild(benzene);
// add branches		
		join = new CMLJoin();
		join.setId("br1");
		join.setMoleculeRefs2(new String[]{CMLJoin.PARENT_S, CMLJoin.CHILD_S});
		join.setAtomRefs2(new String[]{"r3", "r1"});
		benzene.appendChild(join);
		CMLFragment branchFragment = new CMLFragment();
		branchFragment.setId("brf1");
		join.appendChild(branchFragment);
		
		join = new CMLJoin();
		join.setId("br2");
		join.setMoleculeRefs2(new String[]{CMLJoin.PARENT_S, CMLJoin.CHILD_S});
		join.setAtomRefs2(new String[]{"r4", "r1"});
		benzene.appendChild(join);
		branchFragment = new CMLFragment();
		branchFragment.setId("brf2");
		join.appendChild(branchFragment);
		
		fragment1 = new CMLFragment();
		fragment1.setId("f2");
		fragment.appendChild(fragment1);
		
		join = new CMLJoin();
		join.setId("j2");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f3");
		fragment.appendChild(fragment1);
		join = new CMLJoin();
		join.setId("j3");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment.appendChild(join);
		
		CMLFragment fragmentList11 = new CMLFragment();
		fragment.appendChild(fragmentList11);
		fragmentList11.setCountExpression("range(2,4)");
		join = new CMLJoin();
		join.setId("j11");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList11.appendChild(join);
		
		CMLFragment fragment11 = new CMLFragment();
		fragment11.setId("f11");
		fragmentList11.appendChild(fragment11);
		
		CMLFragment fragmentList = new CMLFragment();
		fragmentList.setCountExpression("range(1,4)");
		join = new CMLJoin();
		join.setId("j4");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f4");
		fragmentList.appendChild(fragment1);
		fragment11.appendChild(fragmentList);
		CMLMolecule glucose = new CMLMolecule();
		glucose.setId("glu");
		fragment1.appendChild(glucose);
//		 add branches		
		join = new CMLJoin();
		join.setId("br4");
		join.setMoleculeRefs2(new String[]{CMLJoin.PARENT_S, CMLJoin.CHILD_S});
		join.setAtomRefs2(new String[]{"r3", "r1"});
		glucose.appendChild(join);
		branchFragment = new CMLFragment();
		branchFragment.setId("brf4");
		join.appendChild(branchFragment);
		fragmentList = new CMLFragment();
		fragmentList.setId("f44");
		fragmentList.setCountExpression("*(4)");
		branchFragment.appendChild(fragmentList);
		join = new CMLJoin();
		join.setId("j44");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f4444");
		fragmentList.appendChild(fragment1);
		
		join = new CMLJoin();
		join.setId("j44");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragment11.appendChild(join);
		
		fragmentList = new CMLFragment();
		fragmentList.setCountExpression("range(1,4)");
		join = new CMLJoin();
		join.setId("j5");
		join.setAtomRefs2(ATOMREFS2);
		join.setMoleculeRefs2(MOLREFS2);
		fragmentList.appendChild(join);
		fragment1 = new CMLFragment();
		fragment1.setId("f5");
		fragmentList.appendChild(fragment1);
		fragment11.appendChild(fragmentList);
		
		String fragmentS = ""+
		"<fragment xmlns='http://www.xml-cml.org/schema'>"+
		 "<fragment id='f1'/>"+
		 "<join id='j1' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f2'>"+
		   "<molecule>"+
		     "<join id='br1' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf1'/>"+
		     "</join>"+
		     "<join id='br2' moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>"+
		       "<fragment id='brf2'/>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<fragment id='f2'/>"+
		 "<join id='j2' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f3'/>"+
		 "<join id='j3' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment countExpression='range(2,4)'>"+
		   "<join id='j11' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		   "<fragment id='f11'>"+
		     "<fragment countExpression='range(1,4)'>"+
		       "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		       "<fragment id='f4'>"+
		         "<molecule id='glu'>"+
		           "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		             "<fragment id='brf4'>"+
		               "<fragment id='f44' countExpression='*(4)'>"+
		                 "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		                 "<fragment id='f4444'/>"+
		               "</fragment>"+
		             "</fragment>"+
		           "</join>"+
		         "</molecule>"+
		       "</fragment>"+
		     "</fragment>"+
		     "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		     "<fragment countExpression='range(1,4)'>"+
		       "<join id='j5' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		       "<fragment id='f5'/>"+
		     "</fragment>"+
		   "</fragment>"+
		 "</fragment>"+
		"</fragment>";
		CMLFragment fragmentE = (CMLFragment) parseValidString(fragmentS);
		assertEqualsCanonically("fragment", fragmentE, fragment, true);
		
		// maybe defer this...
		fragmentTool.basic_processRecursively();
		fragment = fragmentTool.getFragment();

		String fragment1S = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'>"+
		 "<fragment id='f1'/>"+
		 "<join id='j1' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f2'>"+
		   "<molecule>"+
		     "<join id='br1' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf1'/>"+
		     "</join>"+
		     "<join id='br2' moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>"+
		       "<fragment id='brf2'/>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<fragment id='f2'/>"+
		 "<join id='j2' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f3'/>"+
		 "<join id='j3' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		 "<join id='j5' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		 "<join id='j11' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		 "<join id='j5' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		 "<join id='j11' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j4' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f4'>"+
		   "<molecule id='glu'>"+
		     "<join id='br4' moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>"+
		       "<fragment id='brf4'>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		         "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		         "<fragment id='f4444'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		 "<join id='j44' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		 "<join id='j5' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		 "<fragment id='f5'/>"+
		"</fragment>";
		
		CMLFragment fragment1E = (CMLFragment) parseValidString(fragment1S);
		// FIXME - this fails although the files seem to be identical
//		assertEqualsCanonically("fragment1", fragment1E, fragment, true);
	}

	 /** expand arguments (cml:arg).
	  * calls:
	  * CMLArg.substituteParameterName(molecule, name, value);
	  * CMLArg.substituteParentAttributes(molecule);
	  * CMLArg.substituteTextContent(molecule);
	 */
   @Test
	public void testSubstituteParameters() {
   	String fragmentS = ""+
   	"<fragment convention='cml:PML-explicit' " +
   	"  xmlns:g='http://www.xml-cml.org/mols/geom1' " +
   	"  xmlns='http://www.xml-cml.org/schema'>"+
   	"  <molecule ref='g:oh' id='1'>"+
   	"    <arg name='idx'>"+
   	"      <scalar dataType='xsd:string'>1</scalar>"+
   	"    </arg>"+
   	"    <atomArray>"+
   	"      <atom id='r1' elementType='R' x3='-1.0360501610646575' y3='0.23396440893831422' z3='0.0'>"+
   	"        <arg parentAttribute='id'>oh_{$idx}_r1</arg>"+
   	"      </atom>"+
   	"      <atom id='a5' elementType='O' x3='-1.696' y3='0.546' z3='-0.0'>"+
   	"        <arg parentAttribute='id'>oh_{$idx}_a5</arg>"+
   	"      </atom>"+
   	"      <atom id='a11' elementType='H' x3='-2.49' y3='-0.0050' z3='0.0'>"+
   	"        <arg parentAttribute='id'>oh_{$idx}_a11</arg>"+
   	"      </atom>"+
   	"    </atomArray>"+
   	"    <bondArray>"+
   	"      <bond atomRefs2='r1 a5' order='1'>"+
   	"        <arg parentAttribute='id'>oh_{$idx}_r1_oh_{$idx}_a5</arg>"+
   	"        <arg parentAttribute='atomRefs2'>oh_{$idx}_r1 oh_{$idx}_a5</arg>"+
   	"      </bond>"+
   	"      <bond atomRefs2='a5 a11' order='1'>"+
       "        <arg parentAttribute='id'>oh_{$idx}_a5_oh_{$idx}_a11</arg>"+
   	"        <arg parentAttribute='atomRefs2'>oh_{$idx}_a5 oh_{$idx}_a11</arg>"+
   	"      </bond>"+
   	"    </bondArray>"+
   	"    <arg parameterName='idx'/>"+
   	"    <arg parentAttribute='id'>oh_{$idx}</arg>"+
   	"  </molecule>"+
   	"</fragment>";
   	CMLFragment fragment= (CMLFragment) parseValidString(fragmentS);
   	FragmentTool fragmentTool = new FragmentTool(fragment);
   	CMLMolecule molecule = fragmentTool.getMolecule();
//   	MoleculeTool moleculeTool = new MoleculeTool(molecule);
	fragmentTool.substituteParameters();
		
   	String moleculeS = ""+
		"<molecule ref='g:oh' id='oh_1' xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		    "<atom elementType='R' x3='-1.0360501610646575' y3='0.23396440893831422' z3='0.0' id='oh_1_r1'></atom>"+
		    "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='oh_1_a5'></atom>"+
		    "<atom elementType='H' x3='-2.49' y3='-0.0050' z3='0.0' id='oh_1_a11'></atom>"+
		  "</atomArray>"+
		  "<bondArray>"+
		    "<bond order='1' id='oh_1_r1_oh_1_a5' atomRefs2='oh_1_r1 oh_1_a5'></bond>"+
		    "<bond order='1' id='oh_1_a5_oh_1_a11' atomRefs2='oh_1_a5 oh_1_a11'></bond>"+
		  "</bondArray>"+
		"</molecule>";
   	CMLMolecule moleculeE = (CMLMolecule) parseValidString(moleculeS);
   	assertEqualsCanonically("molecule", moleculeE, molecule, true);
	}

	 /** expand arguments (cml:arg).
	  * calls:
	  * CMLArg.substituteParameterName(molecule, name, value);
	  * CMLArg.substituteParentAttributes(molecule);
	  * CMLArg.substituteTextContent(molecule);
	 */
  @Test
	public void testSubstituteParameters1() {
	   // intermediate format for testing
	   	String fragmentS = ""+
	   	"<fragment convention='cml:PML-explicit' " +
	   	"  xmlns:g='http://www.xml-cml.org/mols/geom1' " +
	   	"  xmlns='http://www.xml-cml.org/schema'>"+
	   	"  <molecule ref='g:oh' id='1'>"+
	   	"    <arg name='idx'>"+
	   	"      <scalar dataType='xsd:string'>1</scalar>"+
	   	"    </arg>"+
	   	"    <arg name='l1'>"+
	   	"      <scalar dataType='xsd:string'>1.56</scalar>"+
	   	"    </arg>"+
	   	"    <atomArray>"+
	   	"      <atom id='r1' elementType='R' x3='-1.0360501610646575' y3='0.23396440893831422' z3='0.0'>"+
	   	"        <arg parentAttribute='id'>oh_{$idx}_r1</arg>"+
	   	"      </atom>"+
	   	"      <atom id='a5' elementType='O' x3='-1.696' y3='0.546' z3='-0.0'>"+
	   	"        <arg parentAttribute='id'>oh_{$idx}_a5</arg>"+
	   	"      </atom>"+
	   	"      <atom id='a11' elementType='H' x3='-2.49' y3='-0.0050' z3='0.0'>"+
	   	"        <arg parentAttribute='id'>oh_{$idx}_a11</arg>"+
	   	"      </atom>"+
	   	"    </atomArray>"+
	   	"    <bondArray>"+
	   	"      <bond atomRefs2='r1 a5' order='1'>"+
	   	"        <arg parentAttribute='id'>oh_{$idx}_r1_oh_{$idx}_a5</arg>"+
	   	"        <arg parentAttribute='atomRefs2'>oh_{$idx}_r1 oh_{$idx}_a5</arg>"+
	   	"      </bond>"+
	   	"      <bond atomRefs2='a5 a11' order='1'>"+
	    "        <arg parentAttribute='id'>oh_{$idx}_a5_oh_{$idx}_a11</arg>"+
	   	"        <arg parentAttribute='atomRefs2'>oh_{$idx}_a5 oh_{$idx}_a11</arg>"+
	   	"      </bond>"+
	   	"    </bondArray>" +
	    "    <length id='l1' atomRefs2='a1 a2'>"+
	    "      <arg parentAttribute='id'>m1_{$idx}_l1</arg>"+
	    "      <arg parentAttribute='atomRefs2'>m1_{$idx}_a1 m1_{$idx}_a2</arg>"+
	    "      <arg substitute='.'>{$l1}</arg>"+
	    "    </length>"+
	   	"    <arg parameterName='l1'/>"+
	   	"    <arg parameterName='idx'/>"+
	   	"    <arg parentAttribute='id'>oh_{$idx}</arg>"+
	   	"  </molecule>"+
	   	"</fragment>";
	   	CMLFragment fragment= (CMLFragment) parseValidString(fragmentS);
	   	FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.substituteParameters();
		
	   	fragmentS = ""+
	   	"<fragment convention='cml:PML-explicit' id='f_oh_1' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
	    "<molecule ref='g:oh' id='oh_1'>"+
	      "<atomArray>"+
	        "<atom elementType='R' x3='-1.0360501610646575' y3='0.23396440893831422' z3='0.0' id='oh_1_r1'> </atom>"+
	        "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='oh_1_a5'> </atom>"+
	        "<atom elementType='H' x3='-2.49' y3='-0.0050' z3='0.0' id='oh_1_a11'> </atom>"+
	      "</atomArray>"+
	      "<bondArray>"+
	        "<bond order='1' id='oh_1_r1_oh_1_a5' atomRefs2='oh_1_r1 oh_1_a5'> </bond>"+
	        "<bond order='1' id='oh_1_a5_oh_1_a11' atomRefs2='oh_1_a5 oh_1_a11'> </bond>"+
	      "</bondArray>"+
	      "<length id='m1_1_l1' atomRefs2='m1_1_a1 m1_1_a2'>1.56</length>"+
	    "</molecule>"+
	   "</fragment>";
	   	CMLFragment fragmentE = (CMLFragment) parseValidString(fragmentS);
	   	assertEqualsCanonically("fragment", fragmentE, fragment, true);
	}

	
}
