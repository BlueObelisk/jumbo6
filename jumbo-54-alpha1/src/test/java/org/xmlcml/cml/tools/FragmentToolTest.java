/**
 * 
 */
package org.xmlcml.cml.tools;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Node;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.AbstractTest;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.IndexableList;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Util;

/**
 * @author pm286
 *
 */
public class FragmentToolTest extends AbstractTest {

	CatalogManager catalogManager = null;
	Catalog moleculeCatalog = null;
	@Before
	public void setUp() {
		catalogManager = CatalogManager.getTopCatalogManager();
		moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
	}
	
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

	/** test 0	 */
//	@Test
	public void testAll0() {
		CMLFragment fragment = makeMol();
		boolean debug = false;
		boolean check = true;
		
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
		
		String completeS =
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
		
		testAll(fragment, debug, 0,
				intermediateS, explicitS, completeS, check);
	}

	private void testAll(CMLFragment fragment, boolean debug, int serial,
			String intermediateS, String explicitS, String completeS, boolean check) {
		FragmentTool fragmentTool = new FragmentTool(fragment);
		// basic -> intermediate
		String title = "basic"+serial;
		outputXML(title, fragment);
		CMLElement generatedElement = fragmentTool.processBasic(moleculeCatalog);
		if (generatedElement == null) {
			testSimple(fragment, fragmentTool, debug, serial, intermediateS, explicitS, completeS, check);
		} else if (generatedElement instanceof CMLFragmentList) {
			testMarkush((CMLFragmentList) generatedElement, debug, serial, 
					intermediateS, explicitS, completeS, check);
		}
	}
	
	private void testSimple(CMLFragment fragment, FragmentTool fragmentTool, boolean debug, int serial,
					String intermediateS, String explicitS, String completeS, boolean check) {
		String title = "intermediate"+serial;
		if (debug) {
			fragment.debug(title);
		}
		if (check) {
			CMLFragment intermediate = (CMLFragment) parseValidString(intermediateS);
			AbstractTest.assertEqualsCanonically(title, intermediate, fragment, true);
		}
		outputXML(title, fragment);
		if (debug) {
			fragment.debug(title);
		}
		// intermediate -> explicit
		fragmentTool.processIntermediate(moleculeCatalog);
		title = "explicit"+serial;
		if (debug) {
			fragment.debug(title);
		}
		if (check) {
			CMLFragment explicit = (CMLFragment) parseValidString(explicitS);
			AbstractTest.assertEqualsCanonically(title, explicit, fragment, true);
		}
		outputXML(title, fragment);
		// explicit -> complete
		fragmentTool.processExplicit();
		title = "complete"+serial;
		if (debug) {
			fragment.debug(title);
		}
		if (check) {
			CMLFragment complete = (CMLFragment) parseValidString(completeS);
			AbstractTest.assertEqualsCanonically(title, complete, fragment, true);
		}
		outputXML("complete"+serial, fragment);
	
	}

	private void testMarkush(CMLFragmentList fragmentList, boolean debug, int serial,
			String intermediateS, String explicitS, String completeS, boolean check) {

		int serialx = 0;
		for (CMLFragment newFragment : fragmentList.getFragmentElements()) {
			outputXML("newFragment"+(++serialx), newFragment);
		}
		String title = "complete"+serial;
		if (debug) {
			fragmentList.debug(title);
		}
		if (check) {
			CMLFragment complete = (CMLFragment) parseValidString(completeS);
			AbstractTest.assertEqualsCanonically(title, complete, fragmentList, true);
		}
		outputXML("complete"+serial, fragmentList);
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
		fragmentTool.processBasic(moleculeCatalog);
		
		List<String> prefixes = CMLUtil.getPrefixes(fragment, "ref");
		Assert.assertEquals("prefixes", 1, prefixes.size());
		Assert.assertEquals("prefixes", "g", prefixes.get(0));
		List<CMLNamespace> namespaces = CMLUtil.getNamespaces(fragment, prefixes);
		Assert.assertEquals("prefixes", 1, namespaces.size());
		CMLNamespace namespace = namespaces.get(0);
		Assert.assertEquals("prefixes", "http://www.xml-cml.org/mols/geom1", namespace.getNamespaceURI());
		Assert.assertEquals("prefixes", "g", namespace.getPrefix());
		
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
		Assert.assertTrue("moleculeList", 30 <= moleculeList.getIndex().size());
		
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

	/** intermediate result in processing Markush
	 */
	@Test
	public void testGeneratedMarkush() {
		String generatedS = ""+
		"<fragment xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<fragmentList>"+
		   "<fragment id='f'>"+
		     "<molecule ref='g:f'/>"+
		   "</fragment>"+
		   "<fragment id='br'>"+
		     "<molecule ref='g:br'/>"+
		   "</fragment>"+
		   "<fragment id='cl'>"+
		     "<molecule ref='g:cl'/>"+
		   "</fragment>"+
		   "<fragment id='nsp2'>"+
		     "<molecule ref='g:nsp2'/>"+
		   "</fragment>"+
		   "<fragment id='oh'>"+
		     "<molecule ref='g:oh'/>"+
		   "</fragment>"+
		   "<fragment id='benzene'>"+
		     "<molecule ref='g:benzene'/>"+
		   "</fragment>"+
		   "<fragment id='halogen'>"+
		     "<fragment ref='f'/>"+
		   "</fragment>"+
		 "</fragmentList>"+
		 "<fragment role='markushTarget'>"+
		   "<molecule ref='g:benzene'>"+
		     "<join moleculeRefs2='PARENT CHILD' atomRefs2='r1 r1'>"+
		       "<torsion>45</torsion>"+
		       "<fragment>"+
		         "<fragment ref='halogen'/>"+
		       "</fragment>"+
		     "</join>"+
		   "</molecule>"+
		 "</fragment>"+
		"</fragment>";		
		
		CMLFragment generatedFragment = (CMLFragment) parseValidString(generatedS);
		FragmentTool generatedFragmentTool = new FragmentTool(generatedFragment);
		generatedFragmentTool.processAll(moleculeCatalog);
		
		String generatedES = "" +
		"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='benzene_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='9.526706134000763' y3='3.869733600000001' z3='5.213518402229052' id='benzene_1_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r6</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='10.243299413197152' y3='3.932398500000001' z3='6.439022942911609' id='benzene_1_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.713504556428543' y3='2.7185301000000006' z3='5.01720505576243' id='benzene_1_a6'>"+
		       "<label dictRef='cml:torsionEnd'>r5</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='8.385888936961882' y3='2.655387420737078' z3='4.323244676535362' id='benzene_1_r6'/>"+
		     "<atom elementType='C' x3='10.119474056141831' y3='2.9008920000000007' z3='7.3834992125284815' id='benzene_1_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='10.509388371349742' y3='2.947556560950007' z3='8.045835415334345' id='benzene_1_r3'/>"+
		     "<atom elementType='C' x3='9.320371405363035' y3='1.8151698000000005' z3='7.151684115065878' id='benzene_1_a4'>"+
		       "<label dictRef='cml:torsionEnd'>r3</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='9.280916015724046' y3='1.2657016684721403' z3='7.6896692864820775' id='benzene_1_r4'/>"+
		     "<atom elementType='C' x3='8.610030693701125' y3='1.7243409000000007' z3='5.934289686115539' id='benzene_1_a5'>"+
		       "<label dictRef='cml:torsionEnd'>r4</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='8.16522311995263' y3='1.112386804675892' z3='5.790907652540795' id='benzene_1_r5'/>"+
		     "<atom elementType='R' x3='10.697234803620145' y3='4.543958438540135' z3='6.552323882423661' id='benzene_1_r2'/>"+
		     "<atom elementType='F' formalCharge='0' hydrogenCount='0' id='f_2_a1' x3='9.665713335944316' y3='4.947555867080721' z3='4.208841260858353'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='2' id='benzene_1_a1_benzene_1_a2' atomRefs2='benzene_1_a1 benzene_1_a2'/>"+
		     "<bond order='1' id='benzene_1_a1_benzene_1_a6' atomRefs2='benzene_1_a1 benzene_1_a6'/>"+
		     "<bond order='1' id='benzene_1_a3_benzene_1_a2' atomRefs2='benzene_1_a3 benzene_1_a2'/>"+
		     "<bond order='1' id='benzene_1_a2_benzene_1_r2' atomRefs2='benzene_1_a2 benzene_1_r2'/>"+
		     "<bond order='1' id='benzene_1_r6_benzene_1_a6' atomRefs2='benzene_1_r6 benzene_1_a6'/>"+
		     "<bond order='2' id='benzene_1_a5_benzene_1_a6' atomRefs2='benzene_1_a5 benzene_1_a6'/>"+
		     "<bond order='1' id='benzene_1_a3_benzene_1_r3' atomRefs2='benzene_1_a3 benzene_1_r3'/>"+
		     "<bond order='2' id='benzene_1_a3_benzene_1_a4' atomRefs2='benzene_1_a3 benzene_1_a4'/>"+
		     "<bond order='1' id='benzene_1_a5_benzene_1_a4' atomRefs2='benzene_1_a5 benzene_1_a4'/>"+
		     "<bond order='1' id='benzene_1_a4_benzene_1_r4' atomRefs2='benzene_1_a4 benzene_1_r4'/>"+
		     "<bond order='1' id='benzene_1_a5_benzene_1_r5' atomRefs2='benzene_1_a5 benzene_1_r5'/>"+
		     "<bond atomRefs2='benzene_1_a1 f_2_a1' order='S' id='benzene_1_a1_f_2_a1'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		
		CMLFragment generatedE = (CMLFragment) parseValidString(generatedES);
		assertEqualsCanonically("generated", generatedE, generatedFragment, true);
 	}

	/** tests first ten examples
	 */
	@Test
	public void test0_9() {
		testAll0();
		testAll1();
		testAll2();
		testAll3();
		testAll4();
		testAll5();
		testAll6();
		testAll7();
		testAll8();
		testAll9();
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
	
	/** test 1*/
//	@Test
	public void testAll1() {
		CMLFragment fragment = makeMol1();
		boolean debug = false;
		boolean check = true;
		
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
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='2pyr_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r6</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_1_r1'> </atom>"+
		     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
		     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'>"+
		       "<label dictRef='cml:torsionEnd'>r3</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
		     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'>"+
		       "<label dictRef='cml:torsionEnd'>r4</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
		     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'>"+
		       "<label dictRef='cml:torsionEnd'>r5</label>"+
		     "</atom>"+
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
		
		String completeS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			 "<molecule role='fragment' id='2pyr_1'>"+
			   "<atomArray>"+
			     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'>"+
			       "<label dictRef='cml:torsionEnd'>r6</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
			     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'>"+
			       "<label dictRef='cml:torsionEnd'>r3</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
			     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'>"+
			       "<label dictRef='cml:torsionEnd'>r4</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
			     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'>"+
			       "<label dictRef='cml:torsionEnd'>r5</label>"+
			     "</atom>"+
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
		testAll(fragment, debug, 1,
				intermediateS, explicitS, completeS, check);
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
	
	/** test 2	 */
	@Test
	public void testAll2() {
		CMLFragment fragment = makeMol2();
		boolean debug = false;
		boolean check = true;

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
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='2pyr_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r6</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_1_r1'> </atom>"+
		     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
		     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'>"+
		       "<label dictRef='cml:torsionEnd'>r3</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
		     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'>"+
		       "<label dictRef='cml:torsionEnd'>r4</label>"+
		     "</atom>"+
		     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
		     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'>"+
		       "<label dictRef='cml:torsionEnd'>r5</label>"+
		     "</atom>"+
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
		   "<join atomRefs2='g:benzene_2_r3 g:po_3_r1' moleculeRefs2='g:benzene_2 g:po_3' order='S'>"+
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
		
		String completeS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			 "<molecule role='fragment' id='2pyr_1'>"+
			   "<atomArray>"+
			     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_1_a1'>"+
			       "<label dictRef='cml:torsionEnd'>r6</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_1_a2'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_1_r2'> </atom>"+
			     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_1_a3'>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_1_a4'>"+
			       "<label dictRef='cml:torsionEnd'>r3</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_1_r4'> </atom>"+
			     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_1_a5'>"+
			       "<label dictRef='cml:torsionEnd'>r4</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_1_r5'> </atom>"+
			     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_1_a6'>"+
			       "<label dictRef='cml:torsionEnd'>r5</label>"+
			     "</atom>"+
			     "<atom elementType='R' x3='-1.0162393566378534' y3='4.057405011259368' z3='26.686672838435875' id='2pyr_1_r6'> </atom>"+
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
			     "<atom elementType='C' id='po_3_a2' x3='2.7896830871486165' y3='1.025100911570407' z3='23.368585102627176'/>"+
			     "<atom elementType='C' id='po_3_a3' x3='1.3977759281552669' y3='0.4594498903611599' z3='23.077326827297533'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='po_3_a4' x3='0.3488797059599986' y3='1.2668578061538396' z3='23.843433982061025'/>"+
			     "<atom elementType='O' id='po_3_a5' x3='1.1335024584424414' y3='0.5451304077299939' z3='21.674846031653846'/>"+
			     "<atom elementType='H' id='po_3_a7' x3='3.527146672970428' y3='0.5085439782908068' z3='22.754439847603894'/>"+
			     "<atom elementType='H' id='po_3_a8' x3='3.0270886431800004' y3='0.8793908720974519' z3='24.421913250149217'/>"+
			     "<atom elementType='H' id='po_3_a9' x3='1.3561124227979136' y3='-0.5831935029060953' z3='23.391481099586613'/>"+
			     "<atom elementType='H' id='po_3_a10' x3='-0.642482892629459' y3='0.864338840719004' z3='23.636002835452746'/>"+
			     "<atom elementType='H' id='po_3_a11' x3='0.5496600777330873' y3='1.2020220557179957' z3='24.913222544243332'/>"+
			     "<atom elementType='H' id='po_3_a12' x3='0.3905432113173517' y3='2.309501199421095' z3='23.52927970977195'/>"+
			     "<atom elementType='R' id='po_3_r2' x3='0.465954749614784' y3='0.2692113053961904' z3='21.56925860150114'/>"+
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
			     "<bond atomRefs2='2pyr_1_a1 benzene_2_a2' order='S' id='2pyr_1_a1_benzene_2_a2'/>"+
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
			     "<bond order='D' id='acetyl_4_a71_acetyl_4_a72' atomRefs2='acetyl_4_a71 acetyl_4_a72'/>"+
			     "<bond order='S' id='acetyl_4_a71_acetyl_4_a73' atomRefs2='acetyl_4_a71 acetyl_4_a73'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a74' atomRefs2='acetyl_4_a73 acetyl_4_a74'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a75' atomRefs2='acetyl_4_a73 acetyl_4_a75'/>"+
			     "<bond order='S' id='acetyl_4_a73_acetyl_4_a76' atomRefs2='acetyl_4_a73 acetyl_4_a76'/>"+
			     "<bond atomRefs2='benzene_2_a4 acetyl_4_a71' order='S' id='benzene_2_a4_acetyl_4_a71'/>"+
			   "</bondArray>"+
			   "<torsion id='po_3_tor2' atomRefs4='po_3_a2 po_3_a3 po_3_a5 po_3_r2'/>"+
			   "<torsion id='po_3_tor1' atomRefs4='po_3_r1 po_3_a2 po_3_a3 po_3_a5'/>"+
			   "<angle id='po_3_ang352' atomRefs3='po_3_a3 po_3_a5 po_3_r2'/>"+
			   "<angle id='po_3_ang123' atomRefs3='po_3_r1 po_3_a2 po_3_a3'/>"+
			   "<angle id='po_3_ang234' atomRefs3='po_3_a2 po_3_a3 po_3_a4'/>"+
			   "<length id='po_3_len23' atomRefs2='po_3_a2 po_3_a3'/>"+
			 "</molecule>"+
			"</fragment>";

		testAll(fragment, debug, 2,
				intermediateS, explicitS, completeS, check);
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
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:phenylethane'/>" +
		"          </fragment>"+
		"          <join id='j2' atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
//		"          <fragment countExpression='*(2)'>"+
//		"            <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"            <fragment>" +
		"              <molecule ref='g:po'/>" +
		"            </fragment>"+
//		"          </fragment>"+
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
	
	/** test 2	 */
	@Test
	public void testAll3() {
		CMLFragment fragment = makeMol3();
		boolean debug = false;
		boolean check = true;

		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:propanoic' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:propanoic_1_r1 g:2pyr_2_r2' moleculeRefs2='g:propanoic_1 g:2pyr_2'/>"+
		 "<molecule ref='g:2pyr' id='2'>"+
		   "<join atomRefs2='g:2pyr_2_r5 g:phenylethane_3_r1' moleculeRefs2='g:2pyr_2 g:phenylethane_3'>"+
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
		     "<join id='j3' atomRefs2='g:po_4_r2 g:adgluc_5_r1' moleculeRefs2='g:po_4 g:adgluc_5'/>"+
		     "<molecule ref='g:adgluc' id='5'>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>5</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		   "</join>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:2pyr_2_r1 g:acetyl_6_r1' moleculeRefs2='g:2pyr_2 g:acetyl_6'/>"+
		 "<molecule ref='g:acetyl' id='6'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>6</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		
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
     "<atom elementType='C' x3='0.9130440201297267' y3='2.8881300000000003' z3='24.75205498269886' id='2pyr_2_a1'>"+
       "<label dictRef='cml:torsionEnd'>r6</label>"+
     "</atom>"+
     "<atom elementType='R' x3='1.413862592327999' y3='3.1385040220730342' z3='24.22347801875486' id='2pyr_2_r1'> </atom>"+
     "<atom elementType='C' x3='0.6710034793604291' y3='1.5629880000000003' z3='24.99245916290205' id='2pyr_2_a2'>"+
       "<label dictRef='cml:torsionEnd'>r1</label>"+
     "</atom>"+
     "<atom elementType='R' x3='1.0407687745715524' y3='1.0167533171021161' z3='24.595217048245413' id='2pyr_2_r2'> </atom>"+
     "<atom elementType='N' x3='0.2765492911364054' y3='3.8757572000000002' z3='25.381508959946053' id='2pyr_2_a3'>"+
       "<label dictRef='cml:torsionEnd'>r2</label>"+
     "</atom>"+
     "<atom elementType='C' x3='-0.2506677283610746' y3='1.2254732000000002' z3='25.965836532665875' id='2pyr_2_a4'>"+
       "<label dictRef='cml:torsionEnd'>r3</label>"+
     "</atom>"+
     "<atom elementType='R' x3='-0.3812304106029923' y3='0.48743690707544074' z3='26.14234663530017' id='2pyr_2_r4'> </atom>"+
     "<atom elementType='C' x3='-0.9274226661160216' y3='2.2244264' z3='26.61004448473895' id='2pyr_2_a5'>"+
       "<label dictRef='cml:torsionEnd'>r4</label>"+
     "</atom>"+
     "<atom elementType='R' x3='-1.4642755092148265' y3='2.0584636784491517' z3='27.136490493283194' id='2pyr_2_r5'> </atom>"+
     "<atom elementType='C' x3='-0.638411881791494' y3='3.5031318000000002' z3='26.308615162238905' id='2pyr_2_a6'>"+
       "<label dictRef='cml:torsionEnd'>r5</label>"+
     "</atom>"+
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
   "<join atomRefs2='g:2pyr_2_r5 g:phenylethane_3_r1' moleculeRefs2='g:2pyr_2 g:phenylethane_3' order='S'>"+
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
     "<join id='j3' atomRefs2='g:po_4_r2 g:adgluc_5_r1' moleculeRefs2='g:po_4 g:adgluc_5' order='S'/>"+
     "<molecule role='fragment' id='adgluc_5'>"+
       "<atomArray>"+
         "<atom elementType='C' x3='1.583' y3='0.519' z3='0.522' id='adgluc_5_a1'/>"+
         "<atom elementType='H' x3='1.64' y3='0.34' z3='1.595' id='adgluc_5_a2'/>"+
         "<atom elementType='O' x3='2.844' y3='0.997' z3='0.049' id='adgluc_5_a3'/>"+
         "<atom elementType='C' x3='0.5' y3='1.56' z3='0.232' id='adgluc_5_a4'/>"+
         "<atom elementType='H' x3='0.76' y3='2.499' z3='0.721' id='adgluc_5_a5'/>"+
         "<atom elementType='R' x3='0.4493315255446285' y3='1.6726565017146027' z3='-0.5280271168305728' id='adgluc_5_r1'/>"+
         "<atom elementType='O' x3='-0.757' y3='1.1' z3='0.727' id='adgluc_5_a7'/>"+
         "<atom elementType='C' x3='-1.145' y3='-0.025' z3='-0.06' id='adgluc_5_a8'/>"+
         "<atom elementType='H' x3='-1.105' y3='0.242' z3='-1.116' id='adgluc_5_a9'/>"+
         "<atom elementType='C' x3='-2.572' y3='-0.435' z3='0.308' id='adgluc_5_a10'/>"+
         "<atom elementType='O' x3='-3.476' y3='0.61' z3='-0.053' id='adgluc_5_a11'/>"+
         "<atom elementType='C' x3='-0.196' y3='-1.195' z3='0.205' id='adgluc_5_a12'/>"+
         "<atom elementType='H' x3='-0.216' y3='-1.449' z3='1.265' id='adgluc_5_a13'/>"+
         "<atom elementType='O' x3='-0.606' y3='-2.327' z3='-0.565' id='adgluc_5_a14'/>"+
         "<atom elementType='C' x3='1.228' y3='-0.788' z3='-0.198' id='adgluc_5_a15'/>"+
         "<atom elementType='H' x3='1.274' y3='-0.636' z3='-1.276' id='adgluc_5_a16'/>"+
         "<atom elementType='O' x3='2.147' y3='-1.815' z3='0.181' id='adgluc_5_a17'/>"+
         "<atom elementType='H' x3='3.027' y3='1.819' z3='0.524' id='adgluc_5_a18'/>"+
         "<atom elementType='H' x3='-2.837' y3='-1.347' z3='-0.227' id='adgluc_5_a20'/>"+
         "<atom elementType='H' x3='-2.633' y3='-0.614' z3='1.382' id='adgluc_5_a21'/>"+
         "<atom elementType='H' x3='-4.362' y3='0.312' z3='0.195' id='adgluc_5_a22'/>"+
         "<atom elementType='R' x3='-0.1353034249184123' y3='-2.864831182745468' z3='-0.4163986774181526' id='adgluc_5_r2'/>"+
         "<atom elementType='H' x3='3.026' y3='-1.517' z3='-0.09' id='adgluc_5_a24'/>"+
       "</atomArray>"+
       "<bondArray>"+
         "<bond order='1' id='adgluc_5_a1_adgluc_5_a2' atomRefs2='adgluc_5_a1 adgluc_5_a2'/>"+
         "<bond order='1' id='adgluc_5_a1_adgluc_5_a15' atomRefs2='adgluc_5_a1 adgluc_5_a15'/>"+
         "<bond order='1' id='adgluc_5_a1_adgluc_5_a3' atomRefs2='adgluc_5_a1 adgluc_5_a3'/>"+
         "<bond order='1' id='adgluc_5_a1_adgluc_5_a4' atomRefs2='adgluc_5_a1 adgluc_5_a4'/>"+
         "<bond order='1' id='adgluc_5_a3_adgluc_5_a18' atomRefs2='adgluc_5_a3 adgluc_5_a18'/>"+
         "<bond order='1' id='adgluc_5_a4_adgluc_5_a5' atomRefs2='adgluc_5_a4 adgluc_5_a5'/>"+
         "<bond order='1' id='adgluc_5_a4_adgluc_5_r1' atomRefs2='adgluc_5_a4 adgluc_5_r1'/>"+
         "<bond order='1' id='adgluc_5_a4_adgluc_5_a7' atomRefs2='adgluc_5_a4 adgluc_5_a7'/>"+
         "<bond order='1' id='adgluc_5_a7_adgluc_5_a8' atomRefs2='adgluc_5_a7 adgluc_5_a8'/>"+
         "<bond order='1' id='adgluc_5_a8_adgluc_5_a9' atomRefs2='adgluc_5_a8 adgluc_5_a9'/>"+
         "<bond order='1' id='adgluc_5_a8_adgluc_5_a10' atomRefs2='adgluc_5_a8 adgluc_5_a10'/>"+
         "<bond order='1' id='adgluc_5_a8_adgluc_5_a12' atomRefs2='adgluc_5_a8 adgluc_5_a12'/>"+
         "<bond order='1' id='adgluc_5_a10_adgluc_5_a11' atomRefs2='adgluc_5_a10 adgluc_5_a11'/>"+
         "<bond order='1' id='adgluc_5_a10_adgluc_5_a20' atomRefs2='adgluc_5_a10 adgluc_5_a20'/>"+
         "<bond order='1' id='adgluc_5_a10_adgluc_5_a21' atomRefs2='adgluc_5_a10 adgluc_5_a21'/>"+
         "<bond order='1' id='adgluc_5_a11_adgluc_5_a22' atomRefs2='adgluc_5_a11 adgluc_5_a22'/>"+
         "<bond order='1' id='adgluc_5_a12_adgluc_5_a13' atomRefs2='adgluc_5_a12 adgluc_5_a13'/>"+
         "<bond order='1' id='adgluc_5_a12_adgluc_5_a14' atomRefs2='adgluc_5_a12 adgluc_5_a14'/>"+
         "<bond order='1' id='adgluc_5_a12_adgluc_5_a15' atomRefs2='adgluc_5_a12 adgluc_5_a15'/>"+
         "<bond order='1' id='adgluc_5_a14_adgluc_5_r2' atomRefs2='adgluc_5_a14 adgluc_5_r2'/>"+
         "<bond order='1' id='adgluc_5_a15_adgluc_5_a16' atomRefs2='adgluc_5_a15 adgluc_5_a16'/>"+
         "<bond order='1' id='adgluc_5_a15_adgluc_5_a17' atomRefs2='adgluc_5_a15 adgluc_5_a17'/>"+
         "<bond order='1' id='adgluc_5_a17_adgluc_5_a24' atomRefs2='adgluc_5_a17 adgluc_5_a24'/>"+
       "</bondArray>"+
       "<torsion id='adgluc_5_tau' atomRefs4='adgluc_5_a15 adgluc_5_a12 adgluc_5_a14 adgluc_5_r2'/>"+
       "<arg parameterName='tau'/>"+
     "</molecule>"+
   "</join>"+
 "</molecule>"+
 "<join atomRefs2='g:2pyr_2_r1 g:acetyl_6_r1' moleculeRefs2='g:2pyr_2 g:acetyl_6' order='S'/>"+
 "<molecule role='fragment' id='acetyl_6'>"+
   "<atomArray>"+
     "<atom elementType='R' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0' x3='6.858584288887507' y3='7.368205459562712' z3='1.672120540673427' id='acetyl_6_r1'/>"+
     "<atom elementType='C' x3='7.470354790502688' y3='7.21912165' z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_6_a71'/>"+
     "<atom elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_6_a72'>"+
       "<label dictRef='cml:torsionEnd'>r1</label>"+
     "</atom>"+
     "<atom elementType='C' x3='8.407334871591223' y3='8.375034459999998' z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_6_a73'/>"+
     "<atom elementType='H' x3='9.290344900009062' y3='8.0103218' z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_6_a74'/>"+
     "<atom elementType='H' x3='8.339122721687978' y3='8.830276' z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_6_a75'/>"+
     "<atom elementType='H' x3='8.236429704800676' y3='8.9953799' z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_6_a76'/>"+
   "</atomArray>"+
   "<bondArray>"+
     "<bond order='S' id='acetyl_6_r1_acetyl_6_a71' atomRefs2='acetyl_6_r1 acetyl_6_a71'/>"+
     "<bond order='D' id='acetyl_6_a71_acetyl_6_a72' atomRefs2='acetyl_6_a71 acetyl_6_a72'/>"+
     "<bond order='S' id='acetyl_6_a71_acetyl_6_a73' atomRefs2='acetyl_6_a71 acetyl_6_a73'/>"+
     "<bond order='S' id='acetyl_6_a73_acetyl_6_a74' atomRefs2='acetyl_6_a73 acetyl_6_a74'/>"+
     "<bond order='S' id='acetyl_6_a73_acetyl_6_a75' atomRefs2='acetyl_6_a73 acetyl_6_a75'/>"+
     "<bond order='S' id='acetyl_6_a73_acetyl_6_a76' atomRefs2='acetyl_6_a73 acetyl_6_a76'/>"+
   "</bondArray>"+
 "</molecule>"+
"</fragment>";
		
		String completeS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
			 "<molecule role='fragment' id='propanoic_1'>"+
			   "<atomArray>"+
			     "<atom elementType='C' x3='1.998' y3='-0.064' z3='-0.0' id='propanoic_1_a1'/>"+
			     "<atom elementType='C' x3='0.757' y3='0.831' z3='-0.0' id='propanoic_1_a2'/>"+
			     "<atom elementType='C' x3='-0.482' y3='-0.028' z3='0.0' id='propanoic_1_a3'/>"+
			     "<atom elementType='O' formalCharge='-1' x3='-0.382' y3='-1.232' z3='0.0' id='propanoic_1_a4'/>"+
			     "<atom elementType='O' x3='-1.696' y3='0.546' z3='-0.0' id='propanoic_1_a5'> </atom>"+
			     "<atom elementType='H' x3='1.992' y3='-0.694' z3='0.89' id='propanoic_1_a6'/>"+
			     "<atom elementType='H' x3='1.992' y3='-0.694' z3='-0.89' id='propanoic_1_a7'/>"+
			     "<atom elementType='H' x3='2.893' y3='0.557' z3='-0.0' id='propanoic_1_a8'/>"+
			     "<atom elementType='H' x3='0.763' y3='1.46' z3='0.89' id='propanoic_1_a9'/>"+
			     "<atom elementType='H' x3='0.763' y3='1.46' z3='-0.89' id='propanoic_1_a10'/>"+
			     "<atom elementType='C' id='2pyr_2_a1' x3='-4.170829562859505' y3='0.25382293082701435' z3='-0.10776554165974116'>"+
			       "<label dictRef='cml:torsionEnd'>r6</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='2pyr_2_a2' x3='-2.9283376620044756' y3='-0.3091864631794285' z3='0.0'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='N' id='2pyr_2_a3' x3='-5.29708954249401' y3='-0.45780919954227356' z3='-0.15093387482406218'>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='2pyr_2_a4' x3='-2.832980053283631' y3='-1.6837544145830117' z3='0.11102897263054767'>"+
			       "<label dictRef='cml:torsionEnd'>r3</label>"+
			     "</atom>"+
			     "<atom elementType='R' id='2pyr_2_r4' x3='-2.147865206389991' y3='-2.0223851135864064' z3='0.20508691359448916'> </atom>"+
			     "<atom elementType='C' id='2pyr_2_a5' x3='-3.97598522674068' y3='-2.432610991308706' z3='0.0505175850640498'>"+
			       "<label dictRef='cml:torsionEnd'>r4</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='2pyr_2_a6' x3='-5.1588789179592665' y3='-1.8029194511378257' z3='-0.0666380781382423'>"+
			       "<label dictRef='cml:torsionEnd'>r5</label>"+
			     "</atom>"+
			     "<atom elementType='R' id='2pyr_2_r6' x3='-5.796752004526688' y3='-2.233749749051467' z3='-0.08671741658836374'> </atom>"+
			     "<atom elementType='C' id='phenylethane_3_a1' x3='-3.911362871358155' y3='-3.969853923154703' z3='0.11615385304403379'>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='phenylethane_3_a2' x3='-4.235298666522911' y3='-4.552955776015999' z3='-1.2603761429243432'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='phenylethane_3_a3' x3='-4.1707782146580135' y3='-6.057391763401713' z3='-1.196732831021831'/>"+
			     "<atom elementType='C' id='phenylethane_3_a4' x3='-5.303199253237199' y3='-6.784282067169023' z3='-0.8784529578405273'/>"+
			     "<atom elementType='C' id='phenylethane_3_a5' x3='-5.244015794071455' y3='-8.163619357495675' z3='-0.8199896558853236'/>"+
			     "<atom elementType='C' id='phenylethane_3_a6' x3='-4.05435478479529' y3='-8.817118373175063' z3='-1.0794655112439455'/>"+
			     "<atom elementType='C' id='phenylethane_3_a7' x3='-2.922178593212289' y3='-8.090086382637121' z3='-1.398704537597317'/>"+
			     "<atom elementType='C' id='phenylethane_3_a8' x3='-2.9825862873589553' y3='-6.710040658457306' z3='-1.4619636054128582'/>"+
			     "<atom elementType='H' id='phenylethane_3_a9' x3='-4.636063113209182' y3='-4.33321442834762' z3='0.8448771906194205'/>"+
			     "<atom elementType='H' id='phenylethane_3_a10' x3='-2.909530726657353' y3='-4.278903565122774' z3='0.4152887267107425'/>"+
			     "<atom elementType='H' id='phenylethane_3_a13' x3='-3.5105984246718838' y3='-4.189595270823082' z3='-1.98909948049973'/>"+
			     "<atom elementType='H' id='phenylethane_3_a14' x3='-6.233115169982339' y3='-6.274285732252402' z3='-0.6752040312611793'/>"+
			     "<atom elementType='H' id='phenylethane_3_a15' x3='-6.128621016870843' y3='-8.731906538523589' z3='-0.5707342052420263'/>"+
			     "<atom elementType='H' id='phenylethane_3_a16' x3='-4.008074539034705' y3='-9.89537058274886' z3='-1.033701864081956'/>"+
			     "<atom elementType='H' id='phenylethane_3_a17' x3='-1.9922626764671485' y3='-8.600082717553743' z3='-1.6019534641766648'/>"+
			     "<atom elementType='H' id='phenylethane_3_a18' x3='-2.097985449398667' y3='-6.142742893807961' z3='-1.7113640939925971'/>"+
			     "<atom elementType='C' id='po_4_a2' x3='-5.650397185002607' y3='-4.1164198824544' z3='-1.6829073212164825'/>"+
			     "<atom elementType='C' id='po_4_a3' x3='-5.7117966459848795' y3='-3.988550627059935' z3='-3.2067437821649505'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			       "<label dictRef='cml:torsionEnd'>r2</label>"+
			     "</atom>"+
			     "<atom elementType='C' id='po_4_a4' x3='-4.612035748602094' y3='-3.03857993735265' z3='-3.683286180367749'/>"+
			     "<atom elementType='O' id='po_4_a5' x3='-5.519638464129619' y3='-5.275075030289045' z3='-3.8000937573415436'/>"+
			     "<atom elementType='H' id='po_4_a7' x3='-6.37423963622966' y3='-4.860886064109518' z3='-1.3517272943829506'/>"+
			     "<atom elementType='H' id='po_4_a8' x3='-5.884406666616895' y3='-3.1541446890163476' z3='-1.228639762823446'/>"+
			     "<atom elementType='H' id='po_4_a9' x3='-6.685088886982166' y3='-3.595513267197184' z3='-3.4995881902840007'/>"+
			     "<atom elementType='H' id='po_4_a10' x3='-4.655430865559971' y3='-2.947522311364774' z3='-4.768493244744711'/>"+
			     "<atom elementType='H' id='po_4_a11' x3='-4.757851804964316' y3='-2.057070391246417' z3='-3.231245597444816'/>"+
			     "<atom elementType='H' id='po_4_a12' x3='-3.638743507604806' y3='-3.4316172972154018' z3='-3.390441772248699'/>"+
			     "<atom elementType='C' id='adgluc_5_a1' x3='-6.687704916997549' y3='-4.067363838929092' z3='-5.61590438407548'/>"+
			     "<atom elementType='H' id='adgluc_5_a2' x3='-6.757349090569135' y3='-3.942733829242192' z3='-6.695828478525167'/>"+
			     "<atom elementType='O' id='adgluc_5_a3' x3='-6.376770009451617' y3='-2.8117350688522267' z3='-5.008409630991422'/>"+
			     "<atom elementType='C' id='adgluc_5_a4' x3='-5.590262362000132' y3='-5.080932004126325' z3='-5.2857992296482035'/>"+
			     "<atom elementType='H' id='adgluc_5_a5' x3='-4.6321213613778545' y3='-4.708332979550344' z3='-5.6485357591176895'/>"+
			     "<atom elementType='O' id='adgluc_5_a7' x3='-5.8814156739073145' y3='-6.329424244426075' z3='-5.912832621802712'/>"+
			     "<atom elementType='C' id='adgluc_5_a8' x3='-7.04363943616303' y3='-6.867326649855479' z3='-5.283996451817689'/>"+
			     "<atom elementType='H' id='adgluc_5_a9' x3='-6.893727294171349' y3='-6.8870814697795755' z3='-4.204570170512091'/>"+
			     "<atom elementType='C' id='adgluc_5_a10' x3='-7.283409070301522' y3='-8.29044299304763' z3='-5.791050094230887'/>"+
			     "<atom elementType='O' id='adgluc_5_a11' x3='-6.205476004981949' y3='-9.131279343075773' z3='-5.3779925447754895'/>"+
			     "<atom elementType='C' id='adgluc_5_a12' x3='-8.259488754670706' y3='-5.999360615692347' z3='-5.612738597310996'/>"+
			     "<atom elementType='H' id='adgluc_5_a13' x3='-8.39789392384504' y3='-5.958363609491253' z3='-6.693330682525565'/>"+
			     "<atom elementType='O' id='adgluc_5_a14' x3='-9.42489148510251' y3='-6.558386704463496' z3='-5.00302529749269'/>"+
			     "<atom elementType='C' id='adgluc_5_a15' x3='-8.02609959008214' y3='-4.582164117822224' z3='-5.071519986879583'/>"+
			     "<atom elementType='H' id='adgluc_5_a16' x3='-7.992935595568447' y3='-4.607031434718124' z3='-3.9826739160236992'/>"+
			     "<atom elementType='O' id='adgluc_5_a17' x3='-9.08565527098334' y3='-3.723568534932657' z3='-5.499405934431639'/>"+
			     "<atom elementType='H' id='adgluc_5_a18' x3='-5.529093294340512' y3='-2.526272212038521' z3='-5.37549701028715'/>"+
			     "<atom elementType='H' id='adgluc_5_a20' x3='-8.219194421430988' y3='-8.669348664273699' z3='-5.380038818322031'/>"+
			     "<atom elementType='H' id='adgluc_5_a21' x3='-7.342393348635696' y3='-8.28299147441486' z3='-6.879950149231098'/>"+
			     "<atom elementType='H' id='adgluc_5_a22' x3='-6.3953168495332235' y3='-10.016909414673485' z3='-5.716996718823376'/>"+
			     "<atom elementType='R' id='adgluc_5_r2' x3='-9.98406655348473' y3='-6.12309590812205' z3='-5.1783682914222195'/>"+
			     "<atom elementType='H' id='adgluc_5_a24' x3='-8.897604943067488' y3='-2.8466646691783413' z3='-5.138071346555143'/>"+
			     "<atom elementType='C' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_6_a71' x3='-4.406181212057981' y3='1.7701799097726028' z3='-0.23765439269231925'> </atom>"+
			     "<atom elementType='O' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_6_a72' x3='-4.058770499097088' y3='2.570305588422381' z3='0.5671335662735945'>"+
			       "<label dictRef='cml:torsionEnd'>r1</label>"+
			     "</atom>"+
			     "<atom elementType='C' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_6_a73' x3='-5.1341828796600915' y3='2.0643852699702228' z3='-1.501789805188395'/>"+
			     "<atom elementType='H' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_6_a74' x3='-4.701750155976688' y3='2.8255054033140534' z3='-1.896418196566113'/>"+
			     "<atom elementType='H' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_6_a75' x3='-6.040107806781336' y3='2.291043937116801' z3='-1.2778994947598403'/>"+
			     "<atom elementType='H' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_6_a76' x3='-5.13255276447644' y3='1.3346873272090403' z3='-2.125915663107997'/>"+
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
			     "<bond order='2' id='2pyr_2_a1_2pyr_2_a2' atomRefs2='2pyr_2_a1 2pyr_2_a2'/>"+
			     "<bond order='1' id='2pyr_2_a1_2pyr_2_a3' atomRefs2='2pyr_2_a1 2pyr_2_a3'/>"+
			     "<bond order='1' id='2pyr_2_a2_2pyr_2_a4' atomRefs2='2pyr_2_a2 2pyr_2_a4'/>"+
			     "<bond order='2' id='2pyr_2_a6_2pyr_2_a3' atomRefs2='2pyr_2_a6 2pyr_2_a3'/>"+
			     "<bond order='1' id='2pyr_2_a4_2pyr_2_r4' atomRefs2='2pyr_2_a4 2pyr_2_r4'/>"+
			     "<bond order='2' id='2pyr_2_a4_2pyr_2_a5' atomRefs2='2pyr_2_a4 2pyr_2_a5'/>"+
			     "<bond order='1' id='2pyr_2_a5_2pyr_2_a6' atomRefs2='2pyr_2_a5 2pyr_2_a6'/>"+
			     "<bond order='1' id='2pyr_2_a6_2pyr_2_r6' atomRefs2='2pyr_2_a6 2pyr_2_r6'/>"+
			     "<bond atomRefs2='propanoic_1_a5 2pyr_2_a2' order='S' id='propanoic_1_a5_2pyr_2_a2'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a2' atomRefs2='phenylethane_3_a1 phenylethane_3_a2'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a9' atomRefs2='phenylethane_3_a1 phenylethane_3_a9'/>"+
			     "<bond order='1' id='phenylethane_3_a1_phenylethane_3_a10' atomRefs2='phenylethane_3_a1 phenylethane_3_a10'/>"+
			     "<bond order='1' id='phenylethane_3_a2_phenylethane_3_a3' atomRefs2='phenylethane_3_a2 phenylethane_3_a3'/>"+
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
			     "<bond atomRefs2='2pyr_2_a5 phenylethane_3_a1' order='S' id='2pyr_2_a5_phenylethane_3_a1'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a3' atomRefs2='po_4_a2 po_4_a3'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a7' atomRefs2='po_4_a2 po_4_a7'/>"+
			     "<bond order='1' id='po_4_a2_po_4_a8' atomRefs2='po_4_a2 po_4_a8'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a4' atomRefs2='po_4_a3 po_4_a4'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a5' atomRefs2='po_4_a3 po_4_a5'/>"+
			     "<bond order='1' id='po_4_a3_po_4_a9' atomRefs2='po_4_a3 po_4_a9'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a10' atomRefs2='po_4_a4 po_4_a10'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a11' atomRefs2='po_4_a4 po_4_a11'/>"+
			     "<bond order='1' id='po_4_a4_po_4_a12' atomRefs2='po_4_a4 po_4_a12'/>"+
			     "<bond atomRefs2='phenylethane_3_a2 po_4_a2' id='j2' order='S'/>"+
			     "<bond order='1' id='adgluc_5_a1_adgluc_5_a2' atomRefs2='adgluc_5_a1 adgluc_5_a2'/>"+
			     "<bond order='1' id='adgluc_5_a1_adgluc_5_a15' atomRefs2='adgluc_5_a1 adgluc_5_a15'/>"+
			     "<bond order='1' id='adgluc_5_a1_adgluc_5_a3' atomRefs2='adgluc_5_a1 adgluc_5_a3'/>"+
			     "<bond order='1' id='adgluc_5_a1_adgluc_5_a4' atomRefs2='adgluc_5_a1 adgluc_5_a4'/>"+
			     "<bond order='1' id='adgluc_5_a3_adgluc_5_a18' atomRefs2='adgluc_5_a3 adgluc_5_a18'/>"+
			     "<bond order='1' id='adgluc_5_a4_adgluc_5_a5' atomRefs2='adgluc_5_a4 adgluc_5_a5'/>"+
			     "<bond order='1' id='adgluc_5_a4_adgluc_5_a7' atomRefs2='adgluc_5_a4 adgluc_5_a7'/>"+
			     "<bond order='1' id='adgluc_5_a7_adgluc_5_a8' atomRefs2='adgluc_5_a7 adgluc_5_a8'/>"+
			     "<bond order='1' id='adgluc_5_a8_adgluc_5_a9' atomRefs2='adgluc_5_a8 adgluc_5_a9'/>"+
			     "<bond order='1' id='adgluc_5_a8_adgluc_5_a10' atomRefs2='adgluc_5_a8 adgluc_5_a10'/>"+
			     "<bond order='1' id='adgluc_5_a8_adgluc_5_a12' atomRefs2='adgluc_5_a8 adgluc_5_a12'/>"+
			     "<bond order='1' id='adgluc_5_a10_adgluc_5_a11' atomRefs2='adgluc_5_a10 adgluc_5_a11'/>"+
			     "<bond order='1' id='adgluc_5_a10_adgluc_5_a20' atomRefs2='adgluc_5_a10 adgluc_5_a20'/>"+
			     "<bond order='1' id='adgluc_5_a10_adgluc_5_a21' atomRefs2='adgluc_5_a10 adgluc_5_a21'/>"+
			     "<bond order='1' id='adgluc_5_a11_adgluc_5_a22' atomRefs2='adgluc_5_a11 adgluc_5_a22'/>"+
			     "<bond order='1' id='adgluc_5_a12_adgluc_5_a13' atomRefs2='adgluc_5_a12 adgluc_5_a13'/>"+
			     "<bond order='1' id='adgluc_5_a12_adgluc_5_a14' atomRefs2='adgluc_5_a12 adgluc_5_a14'/>"+
			     "<bond order='1' id='adgluc_5_a12_adgluc_5_a15' atomRefs2='adgluc_5_a12 adgluc_5_a15'/>"+
			     "<bond order='1' id='adgluc_5_a14_adgluc_5_r2' atomRefs2='adgluc_5_a14 adgluc_5_r2'/>"+
			     "<bond order='1' id='adgluc_5_a15_adgluc_5_a16' atomRefs2='adgluc_5_a15 adgluc_5_a16'/>"+
			     "<bond order='1' id='adgluc_5_a15_adgluc_5_a17' atomRefs2='adgluc_5_a15 adgluc_5_a17'/>"+
			     "<bond order='1' id='adgluc_5_a17_adgluc_5_a24' atomRefs2='adgluc_5_a17 adgluc_5_a24'/>"+
			     "<bond atomRefs2='po_4_a5 adgluc_5_a4' id='j3' order='S'/>"+
			     "<bond order='D' id='acetyl_6_a71_acetyl_6_a72' atomRefs2='acetyl_6_a71 acetyl_6_a72'/>"+
			     "<bond order='S' id='acetyl_6_a71_acetyl_6_a73' atomRefs2='acetyl_6_a71 acetyl_6_a73'/>"+
			     "<bond order='S' id='acetyl_6_a73_acetyl_6_a74' atomRefs2='acetyl_6_a73 acetyl_6_a74'/>"+
			     "<bond order='S' id='acetyl_6_a73_acetyl_6_a75' atomRefs2='acetyl_6_a73 acetyl_6_a75'/>"+
			     "<bond order='S' id='acetyl_6_a73_acetyl_6_a76' atomRefs2='acetyl_6_a73 acetyl_6_a76'/>"+
			     "<bond atomRefs2='2pyr_2_a1 acetyl_6_a71' order='S' id='2pyr_2_a1_acetyl_6_a71'/>"+
			   "</bondArray>"+
			   "<torsion id='phenylethane_3_side' atomRefs4='phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_a3 phenylethane_3_a4'/>"+
			   "<torsion id='phenylethane_3_main' atomRefs4='phenylethane_3_r1 phenylethane_3_a1 phenylethane_3_a2 phenylethane_3_r2'/>"+
			   "<torsion id='po_4_tor2' atomRefs4='po_4_a2 po_4_a3 po_4_a5 po_4_r2'/>"+
			   "<torsion id='po_4_tor1' atomRefs4='po_4_r1 po_4_a2 po_4_a3 po_4_a5'/>"+
			   "<angle id='po_4_ang352' atomRefs3='po_4_a3 po_4_a5 po_4_r2'/>"+
			   "<angle id='po_4_ang123' atomRefs3='po_4_r1 po_4_a2 po_4_a3'/>"+
			   "<angle id='po_4_ang234' atomRefs3='po_4_a2 po_4_a3 po_4_a4'/>"+
			   "<length id='po_4_len23' atomRefs2='po_4_a2 po_4_a3'/>"+
			   "<torsion id='adgluc_5_tau' atomRefs4='adgluc_5_a15 adgluc_5_a12 adgluc_5_a14 adgluc_5_r2'/>"+
			 "</molecule>"+
			"</fragment>";
		
		testAll(fragment, debug, 3,
				intermediateS, explicitS, completeS, check);
	}
	
	private CMLFragment makeMol4() {
		// EtO(C=O)-{{OCH(Me)CH2)4 (CH(Ph)CH2)4)2-Ac
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:coxy'/>" +
		"  </fragment>"+
		"  <join atomRefs2='a36 r2' moleculeRefs2='PREVIOUS NEXT'>"+
		"    <torsion>180</torsion>" +
		"  </join>"+
		"  <fragment countExpression='*(2)'>"+
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>"+
		"      <torsion>180</torsion>" +
		"    </join>"+
		"    <fragment>"+
		"      <fragment countExpression='*(3)'>"+
		"        <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>"+
		"          <torsion>180</torsion>" +
		"        </join>"+
		"        <fragment>" +
		"          <molecule ref='g:po'/>" +
		"        </fragment>"+
		"      </fragment>"+
		"      <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment countExpression='*(4)'>"+
		"        <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>" +
		"          <torsion>180</torsion>" +
		"        </join>"+
		"        <fragment>" +
		"          <molecule ref='g:phenylethane'/>" +
		"        </fragment>"+
		"      </fragment>"+
		"    </fragment>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'>"+
		"    <torsion>180</torsion>" +
		"  </join>"+
		"  <fragment>"+
		"    <molecule ref='g:acetyl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll4() {
		CMLFragment fragment = makeMol4();
		boolean debug = false;
		boolean check = true;
		
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:coxy' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:coxy_1_a36 g:po_2_r2' moleculeRefs2='g:coxy_1 g:po_2'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='2'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_2_r1 g:po_3_r2' moleculeRefs2='g:po_2 g:po_3'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='3'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>3</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_3_r1 g:po_4_r2' moleculeRefs2='g:po_3 g:po_4'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='4'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>4</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_4_r1 g:phenylethane_5_r2' moleculeRefs2='g:po_4 g:phenylethane_5'/>"+
		 "<molecule ref='g:phenylethane' id='5'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>5</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_5_r1 g:phenylethane_6_r2' moleculeRefs2='g:phenylethane_5 g:phenylethane_6'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='6'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>6</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_6_r1 g:phenylethane_7_r2' moleculeRefs2='g:phenylethane_6 g:phenylethane_7'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='7'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>7</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_7_r1 g:phenylethane_8_r2' moleculeRefs2='g:phenylethane_7 g:phenylethane_8'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='8'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>8</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_8_r1 g:po_9_r2' moleculeRefs2='g:phenylethane_8 g:po_9'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='9'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>9</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_9_r1 g:po_10_r2' moleculeRefs2='g:po_9 g:po_10'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='10'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>10</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_10_r1 g:po_11_r2' moleculeRefs2='g:po_10 g:po_11'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:po' id='11'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>11</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_11_r1 g:phenylethane_12_r2' moleculeRefs2='g:po_11 g:phenylethane_12'/>"+
		 "<molecule ref='g:phenylethane' id='12'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>12</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_12_r1 g:phenylethane_13_r2' moleculeRefs2='g:phenylethane_12 g:phenylethane_13'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='13'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>13</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_13_r1 g:phenylethane_14_r2' moleculeRefs2='g:phenylethane_13 g:phenylethane_14'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='14'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>14</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_14_r1 g:phenylethane_15_r2' moleculeRefs2='g:phenylethane_14 g:phenylethane_15'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:phenylethane' id='15'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>15</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_15_r1 g:acetyl_16_r1' moleculeRefs2='g:phenylethane_15 g:acetyl_16'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:acetyl' id='16'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>16</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='coxy_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='4.93139628509961' y3='5.5531378' z3='19.989337480078' id='coxy_1_a24'> </atom>"+
		     "<atom elementType='H' x3='5.831978812041215' y3='5.374187' z3='19.676542755222968' id='coxy_1_a25'/>"+
		     "<atom elementType='H' x3='4.963029306269459' y3='6.3357644' z3='20.56058067771481' id='coxy_1_a26'/>"+
		     "<atom elementType='C' x3='4.047109556942474' y3='5.8362878' z3='18.813438412503512' id='coxy_1_a27'>"+
		       "<label dictRef='cml:torsionEnd'>a36</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='3.6929155774800755' y3='7.322259' z3='17.008646424721643' id='coxy_1_a28'/>"+
		     "<atom elementType='H' x3='2.779871557350349' y3='7.526127' z3='17.26575326202278' id='coxy_1_a29'/>"+
		     "<atom elementType='H' x3='3.6775783550946946' y3='6.598527600000001' z3='16.36144426465531' id='coxy_1_a30'/>"+
		     "<atom elementType='C' x3='4.341392511461977' y3='8.5182846' z3='16.42358663627627' id='coxy_1_a31'/>"+
		     "<atom elementType='H' x3='3.84964281873069' y3='8.8036998' z3='15.65159923664948' id='coxy_1_a32'/>"+
		     "<atom elementType='H' x3='5.241495750204037' y3='8.301958' z3='16.17061683266916' id='coxy_1_a33'/>"+
		     "<atom elementType='H' x3='4.353374716450555' y3='9.225027' z3='17.07481540507809' id='coxy_1_a34'/>"+
		     "<atom elementType='R' x3='4.677896380307435' y3='4.950461087569808' z3='20.396054308552916' id='coxy_1_a36'/>"+
		     "<atom elementType='O' x3='3.1005153628447255' y3='5.1907058' z3='18.505638407159548' id='coxy_1_a41'/>"+
		     "<atom elementType='O' x3='4.476072495533605' y3='6.931512000000001' z3='18.188701445608753' id='coxy_1_a42'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a25' atomRefs2='coxy_1_a24 coxy_1_a25'/>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a26' atomRefs2='coxy_1_a24 coxy_1_a26'/>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a27' atomRefs2='coxy_1_a24 coxy_1_a27'/>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a36' atomRefs2='coxy_1_a24 coxy_1_a36'/>"+
		     "<bond order='2' id='coxy_1_a27_coxy_1_a41' atomRefs2='coxy_1_a27 coxy_1_a41'/>"+
		     "<bond order='1' id='coxy_1_a27_coxy_1_a42' atomRefs2='coxy_1_a27 coxy_1_a42'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a29' atomRefs2='coxy_1_a28 coxy_1_a29'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a30' atomRefs2='coxy_1_a28 coxy_1_a30'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a31' atomRefs2='coxy_1_a28 coxy_1_a31'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a42' atomRefs2='coxy_1_a28 coxy_1_a42'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a32' atomRefs2='coxy_1_a31 coxy_1_a32'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a33' atomRefs2='coxy_1_a31 coxy_1_a33'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a34' atomRefs2='coxy_1_a31 coxy_1_a34'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		 "<join atomRefs2='g:coxy_1_a36 g:po_2_r2' moleculeRefs2='g:coxy_1 g:po_2' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
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
		 "<join atomRefs2='g:po_2_r1 g:po_3_r2' moleculeRefs2='g:po_2 g:po_3' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
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
		 "<join atomRefs2='g:po_3_r1 g:po_4_r2' moleculeRefs2='g:po_3 g:po_4' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
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
		 "<join atomRefs2='g:po_4_r1 g:phenylethane_5_r2' moleculeRefs2='g:po_4 g:phenylethane_5' order='S'/>"+
		 "<molecule role='fragment' id='phenylethane_5'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_5_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_5_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_5_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_5_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_5_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_5_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_5_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_5_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_5_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_5_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_5_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_5_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_5_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_5_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_5_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_5_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_5_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_5_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a2' atomRefs2='phenylethane_5_a1 phenylethane_5_a2'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a9' atomRefs2='phenylethane_5_a1 phenylethane_5_a9'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a10' atomRefs2='phenylethane_5_a1 phenylethane_5_a10'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_r1' atomRefs2='phenylethane_5_a1 phenylethane_5_r1'/>"+
		     "<bond order='1' id='phenylethane_5_a2_phenylethane_5_a3' atomRefs2='phenylethane_5_a2 phenylethane_5_a3'/>"+
		     "<bond order='1' id='phenylethane_5_a2_phenylethane_5_r2' atomRefs2='phenylethane_5_a2 phenylethane_5_r2'/>"+
		     "<bond order='1' id='phenylethane_5_a2_phenylethane_5_a13' atomRefs2='phenylethane_5_a2 phenylethane_5_a13'/>"+
		     "<bond order='2' id='phenylethane_5_a3_phenylethane_5_a8' atomRefs2='phenylethane_5_a3 phenylethane_5_a8'/>"+
		     "<bond order='1' id='phenylethane_5_a3_phenylethane_5_a4' atomRefs2='phenylethane_5_a3 phenylethane_5_a4'/>"+
		     "<bond order='2' id='phenylethane_5_a4_phenylethane_5_a5' atomRefs2='phenylethane_5_a4 phenylethane_5_a5'/>"+
		     "<bond order='1' id='phenylethane_5_a4_phenylethane_5_a14' atomRefs2='phenylethane_5_a4 phenylethane_5_a14'/>"+
		     "<bond order='1' id='phenylethane_5_a5_phenylethane_5_a6' atomRefs2='phenylethane_5_a5 phenylethane_5_a6'/>"+
		     "<bond order='1' id='phenylethane_5_a5_phenylethane_5_a15' atomRefs2='phenylethane_5_a5 phenylethane_5_a15'/>"+
		     "<bond order='2' id='phenylethane_5_a6_phenylethane_5_a7' atomRefs2='phenylethane_5_a6 phenylethane_5_a7'/>"+
		     "<bond order='1' id='phenylethane_5_a6_phenylethane_5_a16' atomRefs2='phenylethane_5_a6 phenylethane_5_a16'/>"+
		     "<bond order='1' id='phenylethane_5_a7_phenylethane_5_a8' atomRefs2='phenylethane_5_a7 phenylethane_5_a8'/>"+
		     "<bond order='1' id='phenylethane_5_a7_phenylethane_5_a17' atomRefs2='phenylethane_5_a7 phenylethane_5_a17'/>"+
		     "<bond order='1' id='phenylethane_5_a8_phenylethane_5_a18' atomRefs2='phenylethane_5_a8 phenylethane_5_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_5_main' atomRefs4='phenylethane_5_r1 phenylethane_5_a1 phenylethane_5_a2 phenylethane_5_r2'/>"+
		   "<torsion id='phenylethane_5_side' atomRefs4='phenylethane_5_a1 phenylethane_5_a2 phenylethane_5_a3 phenylethane_5_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_5_r1 g:phenylethane_6_r2' moleculeRefs2='g:phenylethane_5 g:phenylethane_6' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_6'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_6_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_6_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_6_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_6_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_6_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_6_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_6_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_6_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_6_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_6_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_6_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_6_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_6_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_6_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_6_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_6_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_6_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_6_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a2' atomRefs2='phenylethane_6_a1 phenylethane_6_a2'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a9' atomRefs2='phenylethane_6_a1 phenylethane_6_a9'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a10' atomRefs2='phenylethane_6_a1 phenylethane_6_a10'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_r1' atomRefs2='phenylethane_6_a1 phenylethane_6_r1'/>"+
		     "<bond order='1' id='phenylethane_6_a2_phenylethane_6_a3' atomRefs2='phenylethane_6_a2 phenylethane_6_a3'/>"+
		     "<bond order='1' id='phenylethane_6_a2_phenylethane_6_r2' atomRefs2='phenylethane_6_a2 phenylethane_6_r2'/>"+
		     "<bond order='1' id='phenylethane_6_a2_phenylethane_6_a13' atomRefs2='phenylethane_6_a2 phenylethane_6_a13'/>"+
		     "<bond order='2' id='phenylethane_6_a3_phenylethane_6_a8' atomRefs2='phenylethane_6_a3 phenylethane_6_a8'/>"+
		     "<bond order='1' id='phenylethane_6_a3_phenylethane_6_a4' atomRefs2='phenylethane_6_a3 phenylethane_6_a4'/>"+
		     "<bond order='2' id='phenylethane_6_a4_phenylethane_6_a5' atomRefs2='phenylethane_6_a4 phenylethane_6_a5'/>"+
		     "<bond order='1' id='phenylethane_6_a4_phenylethane_6_a14' atomRefs2='phenylethane_6_a4 phenylethane_6_a14'/>"+
		     "<bond order='1' id='phenylethane_6_a5_phenylethane_6_a6' atomRefs2='phenylethane_6_a5 phenylethane_6_a6'/>"+
		     "<bond order='1' id='phenylethane_6_a5_phenylethane_6_a15' atomRefs2='phenylethane_6_a5 phenylethane_6_a15'/>"+
		     "<bond order='2' id='phenylethane_6_a6_phenylethane_6_a7' atomRefs2='phenylethane_6_a6 phenylethane_6_a7'/>"+
		     "<bond order='1' id='phenylethane_6_a6_phenylethane_6_a16' atomRefs2='phenylethane_6_a6 phenylethane_6_a16'/>"+
		     "<bond order='1' id='phenylethane_6_a7_phenylethane_6_a8' atomRefs2='phenylethane_6_a7 phenylethane_6_a8'/>"+
		     "<bond order='1' id='phenylethane_6_a7_phenylethane_6_a17' atomRefs2='phenylethane_6_a7 phenylethane_6_a17'/>"+
		     "<bond order='1' id='phenylethane_6_a8_phenylethane_6_a18' atomRefs2='phenylethane_6_a8 phenylethane_6_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_6_main' atomRefs4='phenylethane_6_r1 phenylethane_6_a1 phenylethane_6_a2 phenylethane_6_r2'/>"+
		   "<torsion id='phenylethane_6_side' atomRefs4='phenylethane_6_a1 phenylethane_6_a2 phenylethane_6_a3 phenylethane_6_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_6_r1 g:phenylethane_7_r2' moleculeRefs2='g:phenylethane_6 g:phenylethane_7' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_7'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_7_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_7_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_7_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_7_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_7_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_7_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_7_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_7_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_7_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_7_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_7_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_7_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_7_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_7_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_7_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_7_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_7_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_7_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a2' atomRefs2='phenylethane_7_a1 phenylethane_7_a2'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a9' atomRefs2='phenylethane_7_a1 phenylethane_7_a9'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a10' atomRefs2='phenylethane_7_a1 phenylethane_7_a10'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_r1' atomRefs2='phenylethane_7_a1 phenylethane_7_r1'/>"+
		     "<bond order='1' id='phenylethane_7_a2_phenylethane_7_a3' atomRefs2='phenylethane_7_a2 phenylethane_7_a3'/>"+
		     "<bond order='1' id='phenylethane_7_a2_phenylethane_7_r2' atomRefs2='phenylethane_7_a2 phenylethane_7_r2'/>"+
		     "<bond order='1' id='phenylethane_7_a2_phenylethane_7_a13' atomRefs2='phenylethane_7_a2 phenylethane_7_a13'/>"+
		     "<bond order='2' id='phenylethane_7_a3_phenylethane_7_a8' atomRefs2='phenylethane_7_a3 phenylethane_7_a8'/>"+
		     "<bond order='1' id='phenylethane_7_a3_phenylethane_7_a4' atomRefs2='phenylethane_7_a3 phenylethane_7_a4'/>"+
		     "<bond order='2' id='phenylethane_7_a4_phenylethane_7_a5' atomRefs2='phenylethane_7_a4 phenylethane_7_a5'/>"+
		     "<bond order='1' id='phenylethane_7_a4_phenylethane_7_a14' atomRefs2='phenylethane_7_a4 phenylethane_7_a14'/>"+
		     "<bond order='1' id='phenylethane_7_a5_phenylethane_7_a6' atomRefs2='phenylethane_7_a5 phenylethane_7_a6'/>"+
		     "<bond order='1' id='phenylethane_7_a5_phenylethane_7_a15' atomRefs2='phenylethane_7_a5 phenylethane_7_a15'/>"+
		     "<bond order='2' id='phenylethane_7_a6_phenylethane_7_a7' atomRefs2='phenylethane_7_a6 phenylethane_7_a7'/>"+
		     "<bond order='1' id='phenylethane_7_a6_phenylethane_7_a16' atomRefs2='phenylethane_7_a6 phenylethane_7_a16'/>"+
		     "<bond order='1' id='phenylethane_7_a7_phenylethane_7_a8' atomRefs2='phenylethane_7_a7 phenylethane_7_a8'/>"+
		     "<bond order='1' id='phenylethane_7_a7_phenylethane_7_a17' atomRefs2='phenylethane_7_a7 phenylethane_7_a17'/>"+
		     "<bond order='1' id='phenylethane_7_a8_phenylethane_7_a18' atomRefs2='phenylethane_7_a8 phenylethane_7_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_7_main' atomRefs4='phenylethane_7_r1 phenylethane_7_a1 phenylethane_7_a2 phenylethane_7_r2'/>"+
		   "<torsion id='phenylethane_7_side' atomRefs4='phenylethane_7_a1 phenylethane_7_a2 phenylethane_7_a3 phenylethane_7_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_7_r1 g:phenylethane_8_r2' moleculeRefs2='g:phenylethane_7 g:phenylethane_8' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_8'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_8_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_8_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_8_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_8_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_8_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_8_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_8_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_8_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_8_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_8_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_8_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_8_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_8_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_8_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_8_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_8_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_8_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_8_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a2' atomRefs2='phenylethane_8_a1 phenylethane_8_a2'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a9' atomRefs2='phenylethane_8_a1 phenylethane_8_a9'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a10' atomRefs2='phenylethane_8_a1 phenylethane_8_a10'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_r1' atomRefs2='phenylethane_8_a1 phenylethane_8_r1'/>"+
		     "<bond order='1' id='phenylethane_8_a2_phenylethane_8_a3' atomRefs2='phenylethane_8_a2 phenylethane_8_a3'/>"+
		     "<bond order='1' id='phenylethane_8_a2_phenylethane_8_r2' atomRefs2='phenylethane_8_a2 phenylethane_8_r2'/>"+
		     "<bond order='1' id='phenylethane_8_a2_phenylethane_8_a13' atomRefs2='phenylethane_8_a2 phenylethane_8_a13'/>"+
		     "<bond order='2' id='phenylethane_8_a3_phenylethane_8_a8' atomRefs2='phenylethane_8_a3 phenylethane_8_a8'/>"+
		     "<bond order='1' id='phenylethane_8_a3_phenylethane_8_a4' atomRefs2='phenylethane_8_a3 phenylethane_8_a4'/>"+
		     "<bond order='2' id='phenylethane_8_a4_phenylethane_8_a5' atomRefs2='phenylethane_8_a4 phenylethane_8_a5'/>"+
		     "<bond order='1' id='phenylethane_8_a4_phenylethane_8_a14' atomRefs2='phenylethane_8_a4 phenylethane_8_a14'/>"+
		     "<bond order='1' id='phenylethane_8_a5_phenylethane_8_a6' atomRefs2='phenylethane_8_a5 phenylethane_8_a6'/>"+
		     "<bond order='1' id='phenylethane_8_a5_phenylethane_8_a15' atomRefs2='phenylethane_8_a5 phenylethane_8_a15'/>"+
		     "<bond order='2' id='phenylethane_8_a6_phenylethane_8_a7' atomRefs2='phenylethane_8_a6 phenylethane_8_a7'/>"+
		     "<bond order='1' id='phenylethane_8_a6_phenylethane_8_a16' atomRefs2='phenylethane_8_a6 phenylethane_8_a16'/>"+
		     "<bond order='1' id='phenylethane_8_a7_phenylethane_8_a8' atomRefs2='phenylethane_8_a7 phenylethane_8_a8'/>"+
		     "<bond order='1' id='phenylethane_8_a7_phenylethane_8_a17' atomRefs2='phenylethane_8_a7 phenylethane_8_a17'/>"+
		     "<bond order='1' id='phenylethane_8_a8_phenylethane_8_a18' atomRefs2='phenylethane_8_a8 phenylethane_8_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_8_main' atomRefs4='phenylethane_8_r1 phenylethane_8_a1 phenylethane_8_a2 phenylethane_8_r2'/>"+
		   "<torsion id='phenylethane_8_side' atomRefs4='phenylethane_8_a1 phenylethane_8_a2 phenylethane_8_a3 phenylethane_8_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_8_r1 g:po_9_r2' moleculeRefs2='g:phenylethane_8 g:po_9' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='po_9'>"+
		   "<atomArray>"+
		     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_9_r1'/>"+
		     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_9_a2'/>"+
		     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_9_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_9_a4'/>"+
		     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_9_a5'/>"+
		     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_9_a7'/>"+
		     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_9_a8'/>"+
		     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_9_a9'/>"+
		     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_9_a10'/>"+
		     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_9_a11'/>"+
		     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_9_a12'/>"+
		     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_9_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='po_9_r1_po_9_a2' atomRefs2='po_9_r1 po_9_a2'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a3' atomRefs2='po_9_a2 po_9_a3'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a7' atomRefs2='po_9_a2 po_9_a7'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a8' atomRefs2='po_9_a2 po_9_a8'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a4' atomRefs2='po_9_a3 po_9_a4'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a5' atomRefs2='po_9_a3 po_9_a5'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a9' atomRefs2='po_9_a3 po_9_a9'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a10' atomRefs2='po_9_a4 po_9_a10'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a11' atomRefs2='po_9_a4 po_9_a11'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a12' atomRefs2='po_9_a4 po_9_a12'/>"+
		     "<bond order='1' id='po_9_a5_po_9_r2' atomRefs2='po_9_a5 po_9_r2'/>"+
		   "</bondArray>"+
		   "<length id='po_9_len23' atomRefs2='po_9_a2 po_9_a3'/>"+
		   "<angle id='po_9_ang234' atomRefs3='po_9_a2 po_9_a3 po_9_a4'/>"+
		   "<angle id='po_9_ang123' atomRefs3='po_9_r1 po_9_a2 po_9_a3'/>"+
		   "<angle id='po_9_ang352' atomRefs3='po_9_a3 po_9_a5 po_9_r2'/>"+
		   "<torsion id='po_9_tor1' atomRefs4='po_9_r1 po_9_a2 po_9_a3 po_9_a5'/>"+
		   "<torsion id='po_9_tor2' atomRefs4='po_9_a2 po_9_a3 po_9_a5 po_9_r2'/>"+
		   "<arg parameterName='len23'/>"+
		   "<arg parameterName='ang234'/>"+
		   "<arg parameterName='ang123'/>"+
		   "<arg parameterName='ang352'/>"+
		   "<arg parameterName='tor1'/>"+
		   "<arg parameterName='tor2'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_9_r1 g:po_10_r2' moleculeRefs2='g:po_9 g:po_10' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='po_10'>"+
		   "<atomArray>"+
		     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_10_r1'/>"+
		     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_10_a2'/>"+
		     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_10_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_10_a4'/>"+
		     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_10_a5'/>"+
		     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_10_a7'/>"+
		     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_10_a8'/>"+
		     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_10_a9'/>"+
		     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_10_a10'/>"+
		     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_10_a11'/>"+
		     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_10_a12'/>"+
		     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_10_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='po_10_r1_po_10_a2' atomRefs2='po_10_r1 po_10_a2'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a3' atomRefs2='po_10_a2 po_10_a3'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a7' atomRefs2='po_10_a2 po_10_a7'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a8' atomRefs2='po_10_a2 po_10_a8'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a4' atomRefs2='po_10_a3 po_10_a4'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a5' atomRefs2='po_10_a3 po_10_a5'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a9' atomRefs2='po_10_a3 po_10_a9'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a10' atomRefs2='po_10_a4 po_10_a10'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a11' atomRefs2='po_10_a4 po_10_a11'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a12' atomRefs2='po_10_a4 po_10_a12'/>"+
		     "<bond order='1' id='po_10_a5_po_10_r2' atomRefs2='po_10_a5 po_10_r2'/>"+
		   "</bondArray>"+
		   "<length id='po_10_len23' atomRefs2='po_10_a2 po_10_a3'/>"+
		   "<angle id='po_10_ang234' atomRefs3='po_10_a2 po_10_a3 po_10_a4'/>"+
		   "<angle id='po_10_ang123' atomRefs3='po_10_r1 po_10_a2 po_10_a3'/>"+
		   "<angle id='po_10_ang352' atomRefs3='po_10_a3 po_10_a5 po_10_r2'/>"+
		   "<torsion id='po_10_tor1' atomRefs4='po_10_r1 po_10_a2 po_10_a3 po_10_a5'/>"+
		   "<torsion id='po_10_tor2' atomRefs4='po_10_a2 po_10_a3 po_10_a5 po_10_r2'/>"+
		   "<arg parameterName='len23'/>"+
		   "<arg parameterName='ang234'/>"+
		   "<arg parameterName='ang123'/>"+
		   "<arg parameterName='ang352'/>"+
		   "<arg parameterName='tor1'/>"+
		   "<arg parameterName='tor2'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_10_r1 g:po_11_r2' moleculeRefs2='g:po_10 g:po_11' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='po_11'>"+
		   "<atomArray>"+
		     "<atom elementType='R' x3='1.2718481659602687' y3='-0.056654043087598735' z3='0.02401534139488537' id='po_11_r1'/>"+
		     "<atom elementType='C' x3='0.912' y3='-0.145' z3='0.699' id='po_11_a2'/>"+
		     "<atom elementType='C' x3='-0.599' y3='-0.016' z3='0.493' id='po_11_a3'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='-0.908' y3='1.315' z3='-0.194' id='po_11_a4'/>"+
		     "<atom elementType='O' x3='-1.061' y3='-1.093' z3='-0.326' id='po_11_a5'/>"+
		     "<atom elementType='H' x3='1.14' y3='-1.13' z3='1.106' id='po_11_a7'/>"+
		     "<atom elementType='H' x3='1.25' y3='0.623' z3='1.394' id='po_11_a8'/>"+
		     "<atom elementType='H' x3='-1.102' y3='-0.053' z3='1.459' id='po_11_a9'/>"+
		     "<atom elementType='H' x3='-1.984' y3='1.407' z3='-0.341' id='po_11_a10'/>"+
		     "<atom elementType='H' x3='-0.556' y3='2.137' z3='0.43' id='po_11_a11'/>"+
		     "<atom elementType='H' x3='-0.405' y3='1.352' z3='-1.16' id='po_11_a12'/>"+
		     "<atom elementType='R' x3='-1.7811041326297956' y3='-1.0031756899549835' z3='-0.40525674415736745' id='po_11_r2'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='po_11_r1_po_11_a2' atomRefs2='po_11_r1 po_11_a2'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a3' atomRefs2='po_11_a2 po_11_a3'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a7' atomRefs2='po_11_a2 po_11_a7'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a8' atomRefs2='po_11_a2 po_11_a8'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a4' atomRefs2='po_11_a3 po_11_a4'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a5' atomRefs2='po_11_a3 po_11_a5'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a9' atomRefs2='po_11_a3 po_11_a9'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a10' atomRefs2='po_11_a4 po_11_a10'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a11' atomRefs2='po_11_a4 po_11_a11'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a12' atomRefs2='po_11_a4 po_11_a12'/>"+
		     "<bond order='1' id='po_11_a5_po_11_r2' atomRefs2='po_11_a5 po_11_r2'/>"+
		   "</bondArray>"+
		   "<length id='po_11_len23' atomRefs2='po_11_a2 po_11_a3'/>"+
		   "<angle id='po_11_ang234' atomRefs3='po_11_a2 po_11_a3 po_11_a4'/>"+
		   "<angle id='po_11_ang123' atomRefs3='po_11_r1 po_11_a2 po_11_a3'/>"+
		   "<angle id='po_11_ang352' atomRefs3='po_11_a3 po_11_a5 po_11_r2'/>"+
		   "<torsion id='po_11_tor1' atomRefs4='po_11_r1 po_11_a2 po_11_a3 po_11_a5'/>"+
		   "<torsion id='po_11_tor2' atomRefs4='po_11_a2 po_11_a3 po_11_a5 po_11_r2'/>"+
		   "<arg parameterName='len23'/>"+
		   "<arg parameterName='ang234'/>"+
		   "<arg parameterName='ang123'/>"+
		   "<arg parameterName='ang352'/>"+
		   "<arg parameterName='tor1'/>"+
		   "<arg parameterName='tor2'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:po_11_r1 g:phenylethane_12_r2' moleculeRefs2='g:po_11 g:phenylethane_12' order='S'/>"+
		 "<molecule role='fragment' id='phenylethane_12'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_12_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_12_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_12_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_12_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_12_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_12_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_12_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_12_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_12_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_12_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_12_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_12_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_12_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_12_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_12_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_12_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_12_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_12_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a2' atomRefs2='phenylethane_12_a1 phenylethane_12_a2'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a9' atomRefs2='phenylethane_12_a1 phenylethane_12_a9'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a10' atomRefs2='phenylethane_12_a1 phenylethane_12_a10'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_r1' atomRefs2='phenylethane_12_a1 phenylethane_12_r1'/>"+
		     "<bond order='1' id='phenylethane_12_a2_phenylethane_12_a3' atomRefs2='phenylethane_12_a2 phenylethane_12_a3'/>"+
		     "<bond order='1' id='phenylethane_12_a2_phenylethane_12_r2' atomRefs2='phenylethane_12_a2 phenylethane_12_r2'/>"+
		     "<bond order='1' id='phenylethane_12_a2_phenylethane_12_a13' atomRefs2='phenylethane_12_a2 phenylethane_12_a13'/>"+
		     "<bond order='2' id='phenylethane_12_a3_phenylethane_12_a8' atomRefs2='phenylethane_12_a3 phenylethane_12_a8'/>"+
		     "<bond order='1' id='phenylethane_12_a3_phenylethane_12_a4' atomRefs2='phenylethane_12_a3 phenylethane_12_a4'/>"+
		     "<bond order='2' id='phenylethane_12_a4_phenylethane_12_a5' atomRefs2='phenylethane_12_a4 phenylethane_12_a5'/>"+
		     "<bond order='1' id='phenylethane_12_a4_phenylethane_12_a14' atomRefs2='phenylethane_12_a4 phenylethane_12_a14'/>"+
		     "<bond order='1' id='phenylethane_12_a5_phenylethane_12_a6' atomRefs2='phenylethane_12_a5 phenylethane_12_a6'/>"+
		     "<bond order='1' id='phenylethane_12_a5_phenylethane_12_a15' atomRefs2='phenylethane_12_a5 phenylethane_12_a15'/>"+
		     "<bond order='2' id='phenylethane_12_a6_phenylethane_12_a7' atomRefs2='phenylethane_12_a6 phenylethane_12_a7'/>"+
		     "<bond order='1' id='phenylethane_12_a6_phenylethane_12_a16' atomRefs2='phenylethane_12_a6 phenylethane_12_a16'/>"+
		     "<bond order='1' id='phenylethane_12_a7_phenylethane_12_a8' atomRefs2='phenylethane_12_a7 phenylethane_12_a8'/>"+
		     "<bond order='1' id='phenylethane_12_a7_phenylethane_12_a17' atomRefs2='phenylethane_12_a7 phenylethane_12_a17'/>"+
		     "<bond order='1' id='phenylethane_12_a8_phenylethane_12_a18' atomRefs2='phenylethane_12_a8 phenylethane_12_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_12_main' atomRefs4='phenylethane_12_r1 phenylethane_12_a1 phenylethane_12_a2 phenylethane_12_r2'/>"+
		   "<torsion id='phenylethane_12_side' atomRefs4='phenylethane_12_a1 phenylethane_12_a2 phenylethane_12_a3 phenylethane_12_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_12_r1 g:phenylethane_13_r2' moleculeRefs2='g:phenylethane_12 g:phenylethane_13' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_13'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_13_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_13_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_13_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_13_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_13_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_13_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_13_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_13_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_13_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_13_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_13_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_13_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_13_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_13_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_13_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_13_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_13_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_13_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a2' atomRefs2='phenylethane_13_a1 phenylethane_13_a2'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a9' atomRefs2='phenylethane_13_a1 phenylethane_13_a9'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a10' atomRefs2='phenylethane_13_a1 phenylethane_13_a10'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_r1' atomRefs2='phenylethane_13_a1 phenylethane_13_r1'/>"+
		     "<bond order='1' id='phenylethane_13_a2_phenylethane_13_a3' atomRefs2='phenylethane_13_a2 phenylethane_13_a3'/>"+
		     "<bond order='1' id='phenylethane_13_a2_phenylethane_13_r2' atomRefs2='phenylethane_13_a2 phenylethane_13_r2'/>"+
		     "<bond order='1' id='phenylethane_13_a2_phenylethane_13_a13' atomRefs2='phenylethane_13_a2 phenylethane_13_a13'/>"+
		     "<bond order='2' id='phenylethane_13_a3_phenylethane_13_a8' atomRefs2='phenylethane_13_a3 phenylethane_13_a8'/>"+
		     "<bond order='1' id='phenylethane_13_a3_phenylethane_13_a4' atomRefs2='phenylethane_13_a3 phenylethane_13_a4'/>"+
		     "<bond order='2' id='phenylethane_13_a4_phenylethane_13_a5' atomRefs2='phenylethane_13_a4 phenylethane_13_a5'/>"+
		     "<bond order='1' id='phenylethane_13_a4_phenylethane_13_a14' atomRefs2='phenylethane_13_a4 phenylethane_13_a14'/>"+
		     "<bond order='1' id='phenylethane_13_a5_phenylethane_13_a6' atomRefs2='phenylethane_13_a5 phenylethane_13_a6'/>"+
		     "<bond order='1' id='phenylethane_13_a5_phenylethane_13_a15' atomRefs2='phenylethane_13_a5 phenylethane_13_a15'/>"+
		     "<bond order='2' id='phenylethane_13_a6_phenylethane_13_a7' atomRefs2='phenylethane_13_a6 phenylethane_13_a7'/>"+
		     "<bond order='1' id='phenylethane_13_a6_phenylethane_13_a16' atomRefs2='phenylethane_13_a6 phenylethane_13_a16'/>"+
		     "<bond order='1' id='phenylethane_13_a7_phenylethane_13_a8' atomRefs2='phenylethane_13_a7 phenylethane_13_a8'/>"+
		     "<bond order='1' id='phenylethane_13_a7_phenylethane_13_a17' atomRefs2='phenylethane_13_a7 phenylethane_13_a17'/>"+
		     "<bond order='1' id='phenylethane_13_a8_phenylethane_13_a18' atomRefs2='phenylethane_13_a8 phenylethane_13_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_13_main' atomRefs4='phenylethane_13_r1 phenylethane_13_a1 phenylethane_13_a2 phenylethane_13_r2'/>"+
		   "<torsion id='phenylethane_13_side' atomRefs4='phenylethane_13_a1 phenylethane_13_a2 phenylethane_13_a3 phenylethane_13_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_13_r1 g:phenylethane_14_r2' moleculeRefs2='g:phenylethane_13 g:phenylethane_14' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_14'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_14_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_14_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_14_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_14_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_14_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_14_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_14_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_14_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_14_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_14_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_14_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_14_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_14_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_14_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_14_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_14_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_14_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_14_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a2' atomRefs2='phenylethane_14_a1 phenylethane_14_a2'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a9' atomRefs2='phenylethane_14_a1 phenylethane_14_a9'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a10' atomRefs2='phenylethane_14_a1 phenylethane_14_a10'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_r1' atomRefs2='phenylethane_14_a1 phenylethane_14_r1'/>"+
		     "<bond order='1' id='phenylethane_14_a2_phenylethane_14_a3' atomRefs2='phenylethane_14_a2 phenylethane_14_a3'/>"+
		     "<bond order='1' id='phenylethane_14_a2_phenylethane_14_r2' atomRefs2='phenylethane_14_a2 phenylethane_14_r2'/>"+
		     "<bond order='1' id='phenylethane_14_a2_phenylethane_14_a13' atomRefs2='phenylethane_14_a2 phenylethane_14_a13'/>"+
		     "<bond order='2' id='phenylethane_14_a3_phenylethane_14_a8' atomRefs2='phenylethane_14_a3 phenylethane_14_a8'/>"+
		     "<bond order='1' id='phenylethane_14_a3_phenylethane_14_a4' atomRefs2='phenylethane_14_a3 phenylethane_14_a4'/>"+
		     "<bond order='2' id='phenylethane_14_a4_phenylethane_14_a5' atomRefs2='phenylethane_14_a4 phenylethane_14_a5'/>"+
		     "<bond order='1' id='phenylethane_14_a4_phenylethane_14_a14' atomRefs2='phenylethane_14_a4 phenylethane_14_a14'/>"+
		     "<bond order='1' id='phenylethane_14_a5_phenylethane_14_a6' atomRefs2='phenylethane_14_a5 phenylethane_14_a6'/>"+
		     "<bond order='1' id='phenylethane_14_a5_phenylethane_14_a15' atomRefs2='phenylethane_14_a5 phenylethane_14_a15'/>"+
		     "<bond order='2' id='phenylethane_14_a6_phenylethane_14_a7' atomRefs2='phenylethane_14_a6 phenylethane_14_a7'/>"+
		     "<bond order='1' id='phenylethane_14_a6_phenylethane_14_a16' atomRefs2='phenylethane_14_a6 phenylethane_14_a16'/>"+
		     "<bond order='1' id='phenylethane_14_a7_phenylethane_14_a8' atomRefs2='phenylethane_14_a7 phenylethane_14_a8'/>"+
		     "<bond order='1' id='phenylethane_14_a7_phenylethane_14_a17' atomRefs2='phenylethane_14_a7 phenylethane_14_a17'/>"+
		     "<bond order='1' id='phenylethane_14_a8_phenylethane_14_a18' atomRefs2='phenylethane_14_a8 phenylethane_14_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_14_main' atomRefs4='phenylethane_14_r1 phenylethane_14_a1 phenylethane_14_a2 phenylethane_14_r2'/>"+
		   "<torsion id='phenylethane_14_side' atomRefs4='phenylethane_14_a1 phenylethane_14_a2 phenylethane_14_a3 phenylethane_14_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_14_r1 g:phenylethane_15_r2' moleculeRefs2='g:phenylethane_14 g:phenylethane_15' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='phenylethane_15'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='2.74' y3='0.0010' z3='0.725' id='phenylethane_15_a1'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='1.962' y3='-0.0010' z3='-0.592' id='phenylethane_15_a2'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='0.483' y3='-0.0010' z3='-0.302' id='phenylethane_15_a3'/>"+
		     "<atom elementType='C' x3='-0.195' y3='1.197' z3='-0.171' id='phenylethane_15_a4'/>"+
		     "<atom elementType='C' x3='-1.551' y3='1.197' z3='0.095' id='phenylethane_15_a5'/>"+
		     "<atom elementType='C' x3='-2.23' y3='0.0010' z3='0.23' id='phenylethane_15_a6'/>"+
		     "<atom elementType='C' x3='-1.552' y3='-1.197' z3='0.098' id='phenylethane_15_a7'/>"+
		     "<atom elementType='C' x3='-0.196' y3='-1.197' z3='-0.173' id='phenylethane_15_a8'/>"+
		     "<atom elementType='H' x3='2.483' y3='0.892' z3='1.298' id='phenylethane_15_a9'/>"+
		     "<atom elementType='H' x3='2.482' y3='-0.888' z3='1.301' id='phenylethane_15_a10'/>"+
		     "<atom elementType='R' x3='3.495585113556177' y3='2.9384568826525505E-4' z3='0.5767075945357036' id='phenylethane_15_r1'/>"+
		     "<atom elementType='R' x3='2.144213866713569' y3='0.6268609593347394' z3='-0.9988030512675026' id='phenylethane_15_r2'/>"+
		     "<atom elementType='H' x3='2.219' y3='-0.892' z3='-1.165' id='phenylethane_15_a13'/>"+
		     "<atom elementType='H' x3='0.335' y3='2.132' z3='-0.276' id='phenylethane_15_a14'/>"+
		     "<atom elementType='H' x3='-2.081' y3='2.133' z3='0.198' id='phenylethane_15_a15'/>"+
		     "<atom elementType='H' x3='-3.29' y3='0.0010' z3='0.438' id='phenylethane_15_a16'/>"+
		     "<atom elementType='H' x3='-2.082' y3='-2.132' z3='0.203' id='phenylethane_15_a17'/>"+
		     "<atom elementType='H' x3='0.333' y3='-2.133' z3='-0.276' id='phenylethane_15_a18'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a2' atomRefs2='phenylethane_15_a1 phenylethane_15_a2'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a9' atomRefs2='phenylethane_15_a1 phenylethane_15_a9'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a10' atomRefs2='phenylethane_15_a1 phenylethane_15_a10'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_r1' atomRefs2='phenylethane_15_a1 phenylethane_15_r1'/>"+
		     "<bond order='1' id='phenylethane_15_a2_phenylethane_15_a3' atomRefs2='phenylethane_15_a2 phenylethane_15_a3'/>"+
		     "<bond order='1' id='phenylethane_15_a2_phenylethane_15_r2' atomRefs2='phenylethane_15_a2 phenylethane_15_r2'/>"+
		     "<bond order='1' id='phenylethane_15_a2_phenylethane_15_a13' atomRefs2='phenylethane_15_a2 phenylethane_15_a13'/>"+
		     "<bond order='2' id='phenylethane_15_a3_phenylethane_15_a8' atomRefs2='phenylethane_15_a3 phenylethane_15_a8'/>"+
		     "<bond order='1' id='phenylethane_15_a3_phenylethane_15_a4' atomRefs2='phenylethane_15_a3 phenylethane_15_a4'/>"+
		     "<bond order='2' id='phenylethane_15_a4_phenylethane_15_a5' atomRefs2='phenylethane_15_a4 phenylethane_15_a5'/>"+
		     "<bond order='1' id='phenylethane_15_a4_phenylethane_15_a14' atomRefs2='phenylethane_15_a4 phenylethane_15_a14'/>"+
		     "<bond order='1' id='phenylethane_15_a5_phenylethane_15_a6' atomRefs2='phenylethane_15_a5 phenylethane_15_a6'/>"+
		     "<bond order='1' id='phenylethane_15_a5_phenylethane_15_a15' atomRefs2='phenylethane_15_a5 phenylethane_15_a15'/>"+
		     "<bond order='2' id='phenylethane_15_a6_phenylethane_15_a7' atomRefs2='phenylethane_15_a6 phenylethane_15_a7'/>"+
		     "<bond order='1' id='phenylethane_15_a6_phenylethane_15_a16' atomRefs2='phenylethane_15_a6 phenylethane_15_a16'/>"+
		     "<bond order='1' id='phenylethane_15_a7_phenylethane_15_a8' atomRefs2='phenylethane_15_a7 phenylethane_15_a8'/>"+
		     "<bond order='1' id='phenylethane_15_a7_phenylethane_15_a17' atomRefs2='phenylethane_15_a7 phenylethane_15_a17'/>"+
		     "<bond order='1' id='phenylethane_15_a8_phenylethane_15_a18' atomRefs2='phenylethane_15_a8 phenylethane_15_a18'/>"+
		   "</bondArray>"+
		   "<torsion id='phenylethane_15_main' atomRefs4='phenylethane_15_r1 phenylethane_15_a1 phenylethane_15_a2 phenylethane_15_r2'/>"+
		   "<torsion id='phenylethane_15_side' atomRefs4='phenylethane_15_a1 phenylethane_15_a2 phenylethane_15_a3 phenylethane_15_a4'/>"+
		   "<arg parameterName='main'/>"+
		   "<arg parameterName='side'/>"+
		 "</molecule>"+
		 "<join atomRefs2='g:phenylethane_15_r1 g:acetyl_16_r1' moleculeRefs2='g:phenylethane_15 g:acetyl_16' order='S'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule role='fragment' id='acetyl_16'>"+
		   "<atomArray>"+
		     "<atom elementType='R' xFract='0.853' yFract='0.40329' zFract='0.08749' formalCharge='0' hydrogenCount='0' x3='6.858584288887507' y3='7.368205459562712' z3='1.672120540673427' id='acetyl_16_r1'/>"+
		     "<atom elementType='C' x3='7.470354790502688' y3='7.21912165' z3='2.1153032017509776' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_16_a71'/>"+
		     "<atom elementType='O' x3='7.621770771606594' y3='6.2131009200000005' z3='2.7265105769419966' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_16_a72'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='8.407334871591223' y3='8.375034459999998' z3='2.09259931555679' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_16_a73'/>"+
		     "<atom elementType='H' x3='9.290344900009062' y3='8.0103218' z3='1.9961034580073869' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_16_a74'/>"+
		     "<atom elementType='H' x3='8.339122721687978' y3='8.830276' z3='2.9353942128717265' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_16_a75'/>"+
		     "<atom elementType='H' x3='8.236429704800676' y3='8.9953799' z3='1.3798877395986096' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_16_a76'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='S' id='acetyl_16_r1_acetyl_16_a71' atomRefs2='acetyl_16_r1 acetyl_16_a71'/>"+
		     "<bond order='D' id='acetyl_16_a71_acetyl_16_a72' atomRefs2='acetyl_16_a71 acetyl_16_a72'/>"+
		     "<bond order='S' id='acetyl_16_a71_acetyl_16_a73' atomRefs2='acetyl_16_a71 acetyl_16_a73'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a74' atomRefs2='acetyl_16_a73 acetyl_16_a74'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a75' atomRefs2='acetyl_16_a73 acetyl_16_a75'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a76' atomRefs2='acetyl_16_a73 acetyl_16_a76'/>"+
		   "</bondArray>"+
		 "</molecule>"+
		"</fragment>";
		
		String completeS = "" +
		"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule role='fragment' id='coxy_1'>"+
		   "<atomArray>"+
		     "<atom elementType='C' x3='4.93139628509961' y3='5.5531378' z3='19.989337480078' id='coxy_1_a24'> </atom>"+
		     "<atom elementType='H' x3='5.831978812041215' y3='5.374187' z3='19.676542755222968' id='coxy_1_a25'/>"+
		     "<atom elementType='H' x3='4.963029306269459' y3='6.3357644' z3='20.56058067771481' id='coxy_1_a26'/>"+
		     "<atom elementType='C' x3='4.047109556942474' y3='5.8362878' z3='18.813438412503512' id='coxy_1_a27'>"+
		       "<label dictRef='cml:torsionEnd'>a36</label>"+
		     "</atom>"+
		     "<atom elementType='C' x3='3.6929155774800755' y3='7.322259' z3='17.008646424721643' id='coxy_1_a28'/>"+
		     "<atom elementType='H' x3='2.779871557350349' y3='7.526127' z3='17.26575326202278' id='coxy_1_a29'/>"+
		     "<atom elementType='H' x3='3.6775783550946946' y3='6.598527600000001' z3='16.36144426465531' id='coxy_1_a30'/>"+
		     "<atom elementType='C' x3='4.341392511461977' y3='8.5182846' z3='16.42358663627627' id='coxy_1_a31'/>"+
		     "<atom elementType='H' x3='3.84964281873069' y3='8.8036998' z3='15.65159923664948' id='coxy_1_a32'/>"+
		     "<atom elementType='H' x3='5.241495750204037' y3='8.301958' z3='16.17061683266916' id='coxy_1_a33'/>"+
		     "<atom elementType='H' x3='4.353374716450555' y3='9.225027' z3='17.07481540507809' id='coxy_1_a34'/>"+
		     "<atom elementType='O' x3='3.1005153628447255' y3='5.1907058' z3='18.505638407159548' id='coxy_1_a41'/>"+
		     "<atom elementType='O' x3='4.476072495533605' y3='6.931512000000001' z3='18.188701445608753' id='coxy_1_a42'/>"+
		     "<atom elementType='C' id='po_2_a2' x3='4.9010228391049075' y3='3.013328217015097' z3='22.72121863957027'/>"+
		     "<atom elementType='C' id='po_2_a3' x3='5.349397106373887' y3='4.202547106480069' z3='21.868624392937853'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_2_a4' x3='6.75145991453614' y3='3.9358580551594042' z3='21.31898102504022'/>"+
		     "<atom elementType='O' id='po_2_a5' x3='4.43756530173823' y3='4.379092256304821' z3='20.78164299009407'/>"+
		     "<atom elementType='H' id='po_2_a7' x3='3.869188620908403' y3='3.1629349158474582' z3='23.038699319019106'/>"+
		     "<atom elementType='H' id='po_2_a8' x3='5.5433363799819375' y3='2.932934276262607' z3='23.597610749014613'/>"+
		     "<atom elementType='H' id='po_2_a9' x3='5.363249151138932' y3='5.103976231984972' z3='22.480807088337922'/>"+
		     "<atom elementType='H' id='po_2_a10' x3='7.070903638580747' y3='4.782547333972631' z3='20.711591551710914'/>"+
		     "<atom elementType='H' id='po_2_a11' x3='7.44758466635273' y3='3.801125724380909' z3='22.147365916833454'/>"+
		     "<atom elementType='H' id='po_2_a12' x3='6.737607869771098' y3='3.0344289296545006' z3='20.70679832964015'/>"+
		     "<atom elementType='C' id='po_3_a2' x3='4.642769448584559' y3='-0.6361732288246049' z3='21.99377495327486'/>"+
		     "<atom elementType='C' id='po_3_a3' x3='4.567582453981597' y3='0.6857225750481426' z3='22.761327166186916'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_3_a4' x3='5.4754430213216665' y3='0.6140823537639308' z3='23.99001258929704'/>"+
		     "<atom elementType='O' id='po_3_a5' x3='4.997750378114162' y3='1.7532928573435633' z3='21.913162530395518'/>"+
		     "<atom elementType='H' id='po_3_a7' x3='4.067175661776105' y3='-0.5538222066321099' z3='21.07194628061852'/>"+
		     "<atom elementType='H' id='po_3_a8' x3='4.232235144181747' y3='-1.4363527100882658' z3='22.608809409767435'/>"+
		     "<atom elementType='H' id='po_3_a9' x3='3.5401032501234653' y3='0.8646527052871784' z3='23.07724565904814'/>"+
		     "<atom elementType='H' id='po_3_a10' x3='5.4222242722464316' y3='1.5554502558119765' z3='24.53667815226999'/>"+
		     "<atom elementType='H' id='po_3_a11' x3='5.148035744842268' y3='-0.19987915956831426' z3='24.63749628030572'/>"+
		     "<atom elementType='H' id='po_3_a12' x3='6.502922225179798' y3='0.4351522235248968' z3='23.674094096435816'/>"+
		     "<atom elementType='C' id='po_4_a2' x3='7.513575042913234' y3='-2.555640722357187' z3='20.583509122136874'/>"+
		     "<atom elementType='C' id='po_4_a3' x3='6.0757072705774124' y3='-2.1824998335911987' z3='20.95161256739235'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_4_a4' x3='5.474717993044619' y3='-3.27784123720753' z3='21.833684048517554'/>"+
		     "<atom elementType='O' id='po_4_a5' x3='6.07356831541866' y3='-0.9429117514315397' z3='21.664038571577045'/>"+
		     "<atom elementType='H' id='po_4_a7' x3='7.971253989027139' y3='-1.7353012393499512' z3='20.030850896012005'/>"+
		     "<atom elementType='H' id='po_4_a8' x3='7.508338917776195' y3='-3.4526967368594152' z3='19.965159633709074'/>"+
		     "<atom elementType='H' id='po_4_a9' x3='5.482847081127192' y3='-2.0799530927628123' z3='20.043023022157254'/>"+
		     "<atom elementType='H' id='po_4_a10' x3='4.450795570753035' y3='-3.012224797805208' z3='22.096133625098986'/>"+
		     "<atom elementType='H' id='po_4_a11' x3='5.475917172369942' y3='-4.223638593596341' z3='21.29107007651086'/>"+
		     "<atom elementType='H' id='po_4_a12' x3='6.067578182494841' y3='-3.3803879780359174' z3='22.742273593752657'/>"+
		     "<atom elementType='C' id='phenylethane_5_a1' x3='9.235542705427124' y3='-1.630109214927709' z3='22.152126931292973'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_5_a2' x3='8.320248179866464' y3='-2.8222735265492047' z3='21.867948843512206'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_5_a3' x3='7.374257455311953' y3='-3.016620299346992' z3='23.025045157982145'/>"+
		     "<atom elementType='C' id='phenylethane_5_a4' x3='6.149946100817035' y3='-2.3739158544547103' z3='23.031503003638726'/>"+
		     "<atom elementType='C' id='phenylethane_5_a5' x3='5.282661576013256' y3='-2.552012755682947' z3='24.092441760970544'/>"+
		     "<atom elementType='C' id='phenylethane_5_a6' x3='5.637673855554661' y3='-3.371866671860774' z3='25.146712996233415'/>"+
		     "<atom elementType='C' id='phenylethane_5_a7' x3='6.861700093318953' y3='-4.015311961564768' z3='25.139646991539347'/>"+
		     "<atom elementType='C' id='phenylethane_5_a8' x3='7.727559034469604' y3='-3.8409192843950914' z3='24.075667439020094'/>"+
		     "<atom elementType='H' id='phenylethane_5_a9' x3='8.632538083640858' y3='-0.7313732063231682' z3='22.282196193388145'/>"+
		     "<atom elementType='H' id='phenylethane_5_a10' x3='9.806634448025093' y3='-1.8188743861033934' z3='23.061457966553196'/>"+
		     "<atom elementType='H' id='phenylethane_5_a13' x3='8.92325280165273' y3='-3.721009535153745' z3='21.737879581417037'/>"+
		     "<atom elementType='H' id='phenylethane_5_a14' x3='5.871988183918555' y3='-1.7328048689316706' z3='22.208176761693814'/>"+
		     "<atom elementType='H' id='phenylethane_5_a15' x3='4.326096868381994' y3='-2.0494619343537' z3='24.098066002751857'/>"+
		     "<atom elementType='H' id='phenylethane_5_a16' x3='4.959726579728917' y3='-3.5110388849570127' z3='25.97609986995541'/>"+
		     "<atom elementType='H' id='phenylethane_5_a17' x3='7.139658010217435' y3='-4.6564229470878065' z3='25.96297323348426'/>"+
		     "<atom elementType='H' id='phenylethane_5_a18' x3='8.683428221765212' y3='-4.343746773586538' z3='24.070706300817957'/>"+
		     "<atom elementType='C' id='phenylethane_6_a1' x3='11.118381523656453' y3='-0.24047860159124923' z3='21.254247331214643'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_6_a2' x3='10.202962475967734' y3='-1.4326046991237078' z3='20.970310129159845'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_6_a3' x3='11.03701614412515' y3='-2.67489547066571' z3='20.789730181284277'/>"+
		     "<atom elementType='C' id='phenylethane_6_a4' x3='11.343186032738645' y3='-3.465855228057059' z3='21.881834416920157'/>"+
		     "<atom elementType='C' id='phenylethane_6_a5' x3='12.107984630380486' y3='-4.604788605556834' z3='21.71628724339543'/>"+
		     "<atom elementType='C' id='phenylethane_6_a6' x3='12.566872387556465' y3='-4.954042644558369' z3='20.46045061287571'/>"+
		     "<atom elementType='C' id='phenylethane_6_a7' x3='12.259780936351998' y3='-4.163449673884166' z3='19.36821913772144'/>"+
		     "<atom elementType='C' id='phenylethane_6_a8' x3='11.490374525755287' y3='-3.0263502299701326' z3='19.53313011365418'/>"+
		     "<atom elementType='H' id='phenylethane_6_a9' x3='11.689603556503076' y3='-0.4288485280377241' z3='22.163357423558313'/>"+
		     "<atom elementType='H' id='phenylethane_6_a10' x3='11.803275339097587' y3='-0.10065344449293434' z3='20.417569145164048'/>"+
		     "<atom elementType='H' id='phenylethane_6_a13' x3='9.631740443121107' y3='-1.2442347726772311' z3='20.06120003681617'/>"+
		     "<atom elementType='H' id='phenylethane_6_a14' x3='10.985252787496636' y3='-3.1933711897420025' z3='22.863560570597464'/>"+
		     "<atom elementType='H' id='phenylethane_6_a15' x3='12.347900510638649' y3='-5.222781689370134' z3='22.5695925338741'/>"+
		     "<atom elementType='H' id='phenylethane_6_a16' x3='13.164783605114252' y3='-5.844335493318436' z3='20.331048837906255'/>"+
		     "<atom elementType='H' id='phenylethane_6_a17' x3='12.617714181594003' y3='-4.435933712199223' z3='18.386492984044132'/>"+
		     "<atom elementType='H' id='phenylethane_6_a18' x3='11.250841877759989' y3='-2.4092690183133687' z3='18.679677778274762'/>"+
		     "<atom elementType='C' id='phenylethane_7_a1' x3='11.181218905228224' y3='2.2212823986139094' z3='21.721288308577442'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_7_a2' x3='10.266018564293933' y3='1.0289923340074756' z3='21.437334548619'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_7_a3' x3='9.4757940404815' y3='1.290073441500554' z3='20.18078257704138'/>"+
		     "<atom elementType='C' id='phenylethane_7_a4' x3='9.980275423924429' y3='0.8992861231644165' z3='18.954066307650958'/>"+
		     "<atom elementType='C' id='phenylethane_7_a5' x3='9.255809243383137' y3='1.1387611164156555' z3='17.80198450462687'/>"+
		     "<atom elementType='C' id='phenylethane_7_a6' x3='8.027708993981282' y3='1.7681731588987946' z3='17.874732413162416'/>"+
		     "<atom elementType='C' id='phenylethane_7_a7' x3='7.522888683704469' y3='2.1580551136833703' z3='19.101704505618226'/>"+
		     "<atom elementType='C' id='phenylethane_7_a8' x3='8.245660230076368' y3='1.9140533026743283' z3='20.25506542396924'/>"+
		     "<atom elementType='H' id='phenylethane_7_a9' x3='11.86614451937778' y3='2.36144873156264' z3='20.88493353804668'/>"+
		     "<atom elementType='H' id='phenylethane_7_a10' x3='10.577780628323602' y3='3.120016142899469' z3='21.85090660531382'/>"+
		     "<atom elementType='H' id='phenylethane_7_a13' x3='9.581092950144377' y3='0.888826001058745' z3='22.27368931914976'/>"+
		     "<atom elementType='H' id='phenylethane_7_a14' x3='10.940058533914037' y3='0.4077164892883447' z3='18.89639762611914'/>"+
		     "<atom elementType='H' id='phenylethane_7_a15' x3='9.650015261804633' y3='0.8340258175262454' z3='16.84316073945881'/>"+
		     "<atom elementType='H' id='phenylethane_7_a16' x3='7.461407868750907' y3='1.9554321300188926' z3='16.974120891307486'/>"+
		     "<atom elementType='H' id='phenylethane_7_a17' x3='6.563105573714861' y3='2.6496247475594403' z3='19.159373187150045'/>"+
		     "<atom elementType='H' id='phenylethane_7_a18' x3='7.850853458912724' y3='2.2187876047263755' z3='21.21308975486913'/>"+
		     "<atom elementType='C' id='phenylethane_8_a1' x3='12.902809933394245' y3='3.1470357746204947' z3='23.29018857683561'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_8_a2' x3='11.987515453089335' y3='1.9548714290696096' z3='23.006010485631187'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_8_a3' x3='11.041188597501511' y3='1.7608357667417849' z3='24.162884136527545'/>"+
		     "<atom elementType='C' id='phenylethane_8_a4' x3='9.816918281943906' y3='2.40362302403791' z3='24.168862121322025'/>"+
		     "<atom elementType='C' id='phenylethane_8_a5' x3='8.949325566024072' y3='2.2258113731073585' z3='25.229596723414463'/>"+
		     "<atom elementType='C' id='phenylethane_8_a6' x3='9.303988737249021' y3='1.4061599838393442' z3='26.284142898338857'/>"+
		     "<atom elementType='C' id='phenylethane_8_a7' x3='10.52797405664769' y3='0.7626317702774535' z3='26.277556833778547'/>"+
		     "<atom elementType='C' id='phenylethane_8_a8' x3='11.394141791772805' y3='0.9367386398791779' z3='25.213781832856963'/>"+
		     "<atom elementType='H' id='phenylethane_8_a9' x3='12.299828841633236' y3='4.045839771108107' z3='23.41989662930962'/>"+
		     "<atom elementType='H' id='phenylethane_8_a10' x3='13.473635066245333' y3='2.958427653344863' z3='24.199719579217838'/>"+
		     "<atom elementType='H' id='phenylethane_8_a13' x3='12.59049654485034' y3='1.056067432581998' z3='22.87630243315718'/>"+
		     "<atom elementType='H' id='phenylethane_8_a14' x3='9.539233055625646' y3='3.0445758949270765' z3='23.3453207851503'/>"+
		     "<atom elementType='H' id='phenylethane_8_a15' x3='7.992792787471426' y3='2.72842702056569' z3='25.234845959968336'/>"+
		     "<atom elementType='H' id='phenylethane_8_a16' x3='8.62580053723941' y3='1.2672107612471628' z3='27.113370176537515'/>"+
		     "<atom elementType='H' id='phenylethane_8_a17' x3='10.805659282965946' y3='0.1216788993882869' z3='27.101098169950273'/>"+
		     "<atom elementType='H' id='phenylethane_8_a18' x3='12.349978846362175' y3='0.4338465130568172' z3='25.209195564875646'/>"+
		     "<atom elementType='C' id='po_9_a2' x3='15.659048176880962' y3='4.713469286079621' z3='21.324440186793925'/>"+
		     "<atom elementType='C' id='po_9_a3' x3='14.669188190591598' y3='4.46161202369943' z3='22.464152048156073'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_9_a4' x3='13.790863398407014' y3='5.6983623404741905' z3='22.65910130666053'/>"+
		     "<atom elementType='O' id='po_9_a5' x3='13.84543624817162' y3='3.3391007203844456' z3='22.139290367914455'/>"+
		     "<atom elementType='H' id='po_9_a7' x3='16.22343552717933' y3='3.802461063381371' z3='21.125973723633624'/>"+
		     "<atom elementType='H' id='po_9_a8' x3='16.34430300604739' y3='5.511273247307403' z3='21.609093763186966'/>"+
		     "<atom elementType='H' id='po_9_a9' x3='15.21761617739545' y3='4.255512961984749' z3='23.38300128500105'/>"+
		     "<atom elementType='H' id='po_9_a10' x3='13.085702428333898' y3='5.519158216967859' z3='23.47057089229136'/>"+
		     "<atom elementType='H' id='po_9_a11' x3='14.418337450941761' y3='6.554988960045853' z3='22.906954384181965'/>"+
		     "<atom elementType='H' id='po_9_a12' x3='13.24243541160316' y3='5.904461402188871' z3='21.74025206981555'/>"+
		     "<atom elementType='C' id='po_10_a2' x3='15.181372967382577' y3='5.75145367010167' z3='17.773504430350357'/>"+
		     "<atom elementType='C' id='po_10_a3' x3='15.884366362473296' y3='5.3353075857316385' z3='19.067653078273892'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_10_a4' x3='16.84141317769098' y3='6.4443064350026384' z3='19.5071914108426'/>"+
		     "<atom elementType='O' id='po_10_a5' x3='14.908735243398494' y3='5.115765049246666' z3='20.089454915308156'/>"+
		     "<atom elementType='H' id='po_10_a7' x3='14.435066117932612' y3='5.002538487136833' z3='17.50891014623067'/>"+
		     "<atom elementType='H' id='po_10_a8' x3='15.915228337065129' y3='5.83272640242305' z3='16.972290327023977'/>"+
		     "<atom elementType='H' id='po_10_a9' x3='16.444855007295338' y3='4.4163713353786935' z3='18.8975276927113'/>"+
		     "<atom elementType='H' id='po_10_a10' x3='17.34201880461167' y3='6.148258633183805' z3='20.428928198289853'/>"+
		     "<atom elementType='H' id='po_10_a11' x3='17.58590877068592' y3='6.612382604461683' z3='18.728448376670805'/>"+
		     "<atom elementType='H' id='po_10_a12' x3='16.280924532868944' y3='7.363242685355583' z3='19.677316796405197'/>"+
		     "<atom elementType='C' id='po_11_a2' x3='13.16759248673569' y3='8.758815933053041' z3='16.870625518882164'/>"+
		     "<atom elementType='C' id='po_11_a3' x3='13.87515657952465' y3='7.408564496813087' z3='16.73508174909665'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='po_11_a4' x3='14.928494498991475' y3='7.493758979262535' z3='15.629535672456932'/>"+
		     "<atom elementType='O' id='po_11_a5' x3='14.509782760023535' y3='7.077817443738491' z3='17.972817584365174'/>"+
		     "<atom elementType='H' id='po_11_a7' x3='12.481018387521264' y3='8.725472613362697' z3='17.716416408712462'/>"+
		     "<atom elementType='H' id='po_11_a8' x3='12.610812368757255' y3='8.969549900240798' z3='15.958112453059421'/>"+
		     "<atom elementType='H' id='po_11_a9' x3='13.14517927450633' y3='6.6392194763788694' z3='16.48453160702671'/>"+
		     "<atom elementType='H' id='po_11_a10' x3='15.432671429210103' y3='6.532338878958617' z3='15.53296438393431'/>"+
		     "<atom elementType='H' id='po_11_a11' x3='14.445319093928727' y3='7.745941110714415' z3='14.68512596236346'/>"+
		     "<atom elementType='H' id='po_11_a12' x3='15.65847180400979' y3='8.26310399969675' z3='15.880085814526872'/>"+
		     "<atom elementType='C' id='phenylethane_12_a1' x3='14.03782249651296' y3='10.960430383552955' z3='16.04725647973695'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_12_a2' x3='14.212874533467017' y3='9.865941322972299' z3='17.10140423058951'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_12_a3' x3='14.025203542866167' y3='10.456715794334995' z3='18.475196836215634'/>"+
		     "<atom elementType='C' id='phenylethane_12_a4' x3='12.76721864262072' y3='10.485574004455348' z3='19.04851192799598'/>"+
		     "<atom elementType='C' id='phenylethane_12_a5' x3='12.595133417988096' y3='11.027329662304261' z3='20.308027502592747'/>"+
		     "<atom elementType='C' id='phenylethane_12_a6' x3='13.678985355306095' y3='11.540038152873173' z3='20.995106087378044'/>"+
		     "<atom elementType='C' id='phenylethane_12_a7' x3='14.937155220936015' y3='11.51022342096703' z3='20.422016503646013'/>"+
		     "<atom elementType='C' id='phenylethane_12_a8' x3='15.110165272491006' y3='10.96368515418916' z3='19.1636284692908'/>"+
		     "<atom elementType='H' id='phenylethane_12_a9' x3='13.036652306928504' y3='11.38439596203846' z3='16.12580862640956'/>"+
		     "<atom elementType='H' id='phenylethane_12_a10' x3='14.777839633849192' y3='11.74423009032329' z3='16.210638412995877'/>"+
		     "<atom elementType='H' id='phenylethane_12_a13' x3='15.214044723051474' y3='9.441975744486795' z3='17.0228520839169'/>"+
		     "<atom elementType='H' id='phenylethane_12_a14' x3='11.91971853604716' y3='10.085442924257757' z3='18.512052527838136'/>"+
		     "<atom elementType='H' id='phenylethane_12_a15' x3='11.61212185167108' y3='11.050556545123886' z3='20.756083048704443'/>"+
		     "<atom elementType='H' id='phenylethane_12_a16' x3='13.544452453341187' y3='11.963596538741166' z3='21.97966852422489'/>"+
		     "<atom elementType='H' id='phenylethane_12_a17' x3='15.784655327509576' y3='11.910354501164619' z3='20.958475903803855'/>"+
		     "<atom elementType='H' id='phenylethane_12_a18' x3='16.09308621607029' y3='10.940670159911443' z3='18.716546006302586'/>"+
		     "<atom elementType='C' id='phenylethane_13_a1' x3='14.056004308760528' y3='11.451129888947724' z3='13.589365829138634'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_13_a2' x3='14.231008828842445' y3='10.356823953486527' z3='14.643711568241939'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_13_a3' x3='15.615045022142898' y3='9.77029145987466' z3='14.534329441859983'/>"+
		     "<atom elementType='C' id='phenylethane_13_a4' x3='16.655958941788153' y3='10.316291498789283' z3='15.262636498940976'/>"+
		     "<atom elementType='C' id='phenylethane_13_a5' x3='17.924936953810573' y3='9.778600366689592' z3='15.162261943076563'/>"+
		     "<atom elementType='C' id='phenylethane_13_a6' x3='18.154542898443637' y3='8.695770085559493' z3='14.33495202988993'/>"+
		     "<atom elementType='C' id='phenylethane_13_a7' x3='17.113253107156154' y3='8.14923627099976' z3='13.607402471369506'/>"+
		     "<atom elementType='C' id='phenylethane_13_a8' x3='15.84239573692263' y3='8.6842585248739' z3='13.711564520036756'/>"+
		     "<atom elementType='H' id='phenylethane_13_a9' x3='14.79576968072017' y3='12.234992684847782' z3='13.752354514798588'/>"+
		     "<atom elementType='H' id='phenylethane_13_a10' x3='14.192772367745182' y3='11.023801108306847' z3='12.595713167854244'/>"+
		     "<atom elementType='H' id='phenylethane_13_a13' x3='13.491243456882803' y3='9.572961157586473' z3='14.480722882581981'/>"+
		     "<atom elementType='H' id='phenylethane_13_a14' x3='16.477372018012066' y3='11.162693573284429' z3='15.909057599266845'/>"+
		     "<atom elementType='H' id='phenylethane_13_a15' x3='18.73868826543285' y3='10.205399117659255' z3='15.730818562072209'/>"+
		     "<atom elementType='H' id='phenylethane_13_a16' x3='19.146541253493993' y3='8.27548569929924' z3='14.256438984838598'/>"+
		     "<atom elementType='H' id='phenylethane_13_a17' x3='17.29184003093225' y3='7.302834196504614' z3='12.960981371043637'/>"+
		     "<atom elementType='H' id='phenylethane_13_a18' x3='15.02950651686023' y3='8.256958538318916' z3='13.143082473357662'/>"+
		     "<atom elementType='C' id='phenylethane_14_a1' x3='12.466019107445174' y3='13.143660048271927' z3='12.646162906875325'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_14_a2' x3='12.641279374543341' y3='12.049310562701478' z3='13.70042095982459'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_14_a3' x3='11.617824843728767' y3='10.966073790440294' z3='13.475298951623955'/>"+
		     "<atom elementType='C' id='phenylethane_14_a4' x3='11.914260070727735' y3='9.896661220331799' z3='12.650355371075563'/>"+
		     "<atom elementType='C' id='phenylethane_14_a5' x3='10.975863186853674' y3='8.903553144991648' z3='12.44386169313448'/>"+
		     "<atom elementType='C' id='phenylethane_14_a6' x3='9.741711035380586' y3='8.978099949267655' z3='13.06110819250615'/>"+
		     "<atom elementType='C' id='phenylethane_14_a7' x3='9.44576228806652' y3='10.047154934559568' z3='13.886848937162576'/>"+
		     "<atom elementType='C' id='phenylethane_14_a8' x3='10.386591570365093' y3='11.038475085816795' z3='14.097328435643785'/>"+
		     "<atom elementType='H' id='phenylethane_14_a9' x3='12.602647773302202' y3='12.71678123773966' z3='11.652499941486816'/>"+
		     "<atom elementType='H' id='phenylethane_14_a10' x3='11.464451445194216' y3='13.56714761431724' z3='12.724785418525371'/>"+
		     "<atom elementType='H' id='phenylethane_14_a13' x3='12.504650708686317' y3='12.47618937323375' z3='14.6940839252131'/>"+
		     "<atom elementType='H' id='phenylethane_14_a14' x3='12.878332669851346' y3='9.837914474363735' z3='12.167382449001021'/>"+
		     "<atom elementType='H' id='phenylethane_14_a15' x3='11.20698740986978' y3='8.06802860128595' z3='11.798814927077483'/>"+
		     "<atom elementType='H' id='phenylethane_14_a16' x3='9.008124378213344' y3='8.201799733635873' z3='12.899638097329863'/>"+
		     "<atom elementType='H' id='phenylethane_14_a17' x3='8.481689688942913' y3='10.105901680527634' z3='14.369821859237113'/>"+
		     "<atom elementType='H' id='phenylethane_14_a18' x3='10.15487074462945' y3='11.873197103241848' z3='14.742379296077473'/>"+
		     "<atom elementType='C' id='phenylethane_15_a1' x3='13.335916320265376' y3='15.345523207756143' z3='11.823107319916756'>"+
		       "<label dictRef='cml:torsionEnd'>r2</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_15_a2' x3='13.510968410143871' y3='14.251034138163382' z3='12.877255052623674'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' id='phenylethane_15_a3' x3='13.322799209107403' y3='14.841705820654816' z3='14.251023706831523'/>"+
		     "<atom elementType='C' id='phenylethane_15_a4' x3='12.06466024510995' y3='14.870229734401507' z3='14.824017366147215'/>"+
		     "<atom elementType='C' id='phenylethane_15_a5' x3='11.892118222369055' y3='15.411891147454147' z3='16.083510980262943'/>"+
		     "<atom elementType='C' id='phenylethane_15_a6' x3='12.975667246969142' y3='15.92483914770674' z3='16.770888504135435'/>"+
		     "<atom elementType='C' id='phenylethane_15_a7' x3='14.233991354807591' y3='15.895358748635498' z3='16.198120361076505'/>"+
		     "<atom elementType='C' id='phenylethane_15_a8' x3='14.407459096753481' y3='15.348914908960129' z3='14.939754328244634'/>"+
		     "<atom elementType='H' id='phenylethane_15_a9' x3='12.334621316492484' y3='15.769238233208737' z3='11.901420107028136'/>"+
		     "<atom elementType='H' id='phenylethane_15_a10' x3='14.07569788837198' y3='16.12949898398262' z3='11.98671115171732'/>"+
		     "<atom elementType='H' id='phenylethane_15_a13' x3='14.512263413916763' y3='13.827319112710793' z3='12.798942265512295'/>"+
		     "<atom elementType='H' id='phenylethane_15_a14' x3='11.217396602541372' y3='14.469911341088423' z3='14.287324247170782'/>"+
		     "<atom elementType='H' id='phenylethane_15_a15' x3='10.908986086160413' y3='15.434856802834144' z3='16.531315381119832'/>"+
		     "<atom elementType='H' id='phenylethane_15_a16' x3='12.840777249356046' y3='16.34832385902264' z3='17.75543377370186'/>"+
		     "<atom elementType='H' id='phenylethane_15_a17' x3='15.081254997376169' y3='16.295677141948588' z3='16.734813480052942'/>"+
		     "<atom elementType='H' id='phenylethane_15_a18' x3='15.39050030835959' y3='15.326161079772406' z3='14.492922995926396'/>"+
		     "<atom elementType='C' xFract='0.9966' yFract='0.38915' zFract='0.12895' formalCharge='0' hydrogenCount='0' id='acetyl_16_a71' x3='13.529611661979935' y3='14.742022151085418' z3='10.419587249096464'/>"+
		     "<atom elementType='O' xFract='1.0168' yFract='0.33492' zFract='0.15894' formalCharge='0' hydrogenCount='0' id='acetyl_16_a72' x3='13.450671324628285' y3='15.36176173494095' z3='9.410493479740682'>"+
		       "<label dictRef='cml:torsionEnd'>r1</label>"+
		     "</atom>"+
		     "<atom elementType='C' xFract='1.1216' yFract='0.45146' zFract='0.13125' formalCharge='0' hydrogenCount='0' id='acetyl_16_a73' x3='13.814035603129076' y3='13.285357150558328' z3='10.528268715169071'/>"+
		     "<atom elementType='H' xFract='1.2394' yFract='0.4318' zFract='0.1298' formalCharge='0' hydrogenCount='0' id='acetyl_16_a74' x3='14.534372608261116' y3='13.100608491795754' z3='9.920801829406747'/>"+
		     "<atom elementType='H' xFract='1.1125' yFract='0.476' zFract='0.1716' formalCharge='0' hydrogenCount='0' id='acetyl_16_a75' x3='13.029315093396388' y3='12.807102480637393' z3='10.24954449953688'/>"+
		     "<atom elementType='H' xFract='1.0988' yFract='0.4849' zFract='0.0963' formalCharge='0' hydrogenCount='0' id='acetyl_16_a76' x3='14.060616948229772' y3='13.006091547637253' z3='11.413256395728949'/>"+
		   "</atomArray>"+
		   "<bondArray>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a25' atomRefs2='coxy_1_a24 coxy_1_a25'/>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a26' atomRefs2='coxy_1_a24 coxy_1_a26'/>"+
		     "<bond order='1' id='coxy_1_a24_coxy_1_a27' atomRefs2='coxy_1_a24 coxy_1_a27'/>"+
		     "<bond order='2' id='coxy_1_a27_coxy_1_a41' atomRefs2='coxy_1_a27 coxy_1_a41'/>"+
		     "<bond order='1' id='coxy_1_a27_coxy_1_a42' atomRefs2='coxy_1_a27 coxy_1_a42'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a29' atomRefs2='coxy_1_a28 coxy_1_a29'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a30' atomRefs2='coxy_1_a28 coxy_1_a30'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a31' atomRefs2='coxy_1_a28 coxy_1_a31'/>"+
		     "<bond order='1' id='coxy_1_a28_coxy_1_a42' atomRefs2='coxy_1_a28 coxy_1_a42'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a32' atomRefs2='coxy_1_a31 coxy_1_a32'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a33' atomRefs2='coxy_1_a31 coxy_1_a33'/>"+
		     "<bond order='1' id='coxy_1_a31_coxy_1_a34' atomRefs2='coxy_1_a31 coxy_1_a34'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a3' atomRefs2='po_2_a2 po_2_a3'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a7' atomRefs2='po_2_a2 po_2_a7'/>"+
		     "<bond order='1' id='po_2_a2_po_2_a8' atomRefs2='po_2_a2 po_2_a8'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a4' atomRefs2='po_2_a3 po_2_a4'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a5' atomRefs2='po_2_a3 po_2_a5'/>"+
		     "<bond order='1' id='po_2_a3_po_2_a9' atomRefs2='po_2_a3 po_2_a9'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a10' atomRefs2='po_2_a4 po_2_a10'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a11' atomRefs2='po_2_a4 po_2_a11'/>"+
		     "<bond order='1' id='po_2_a4_po_2_a12' atomRefs2='po_2_a4 po_2_a12'/>"+
		     "<bond atomRefs2='coxy_1_a24 po_2_a5' order='S' id='coxy_1_a24_po_2_a5'/>"+
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
		     "<bond order='1' id='po_4_a2_po_4_a3' atomRefs2='po_4_a2 po_4_a3'/>"+
		     "<bond order='1' id='po_4_a2_po_4_a7' atomRefs2='po_4_a2 po_4_a7'/>"+
		     "<bond order='1' id='po_4_a2_po_4_a8' atomRefs2='po_4_a2 po_4_a8'/>"+
		     "<bond order='1' id='po_4_a3_po_4_a4' atomRefs2='po_4_a3 po_4_a4'/>"+
		     "<bond order='1' id='po_4_a3_po_4_a5' atomRefs2='po_4_a3 po_4_a5'/>"+
		     "<bond order='1' id='po_4_a3_po_4_a9' atomRefs2='po_4_a3 po_4_a9'/>"+
		     "<bond order='1' id='po_4_a4_po_4_a10' atomRefs2='po_4_a4 po_4_a10'/>"+
		     "<bond order='1' id='po_4_a4_po_4_a11' atomRefs2='po_4_a4 po_4_a11'/>"+
		     "<bond order='1' id='po_4_a4_po_4_a12' atomRefs2='po_4_a4 po_4_a12'/>"+
		     "<bond atomRefs2='po_3_a2 po_4_a5' order='S' id='po_3_a2_po_4_a5'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a2' atomRefs2='phenylethane_5_a1 phenylethane_5_a2'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a9' atomRefs2='phenylethane_5_a1 phenylethane_5_a9'/>"+
		     "<bond order='1' id='phenylethane_5_a1_phenylethane_5_a10' atomRefs2='phenylethane_5_a1 phenylethane_5_a10'/>"+
		     "<bond order='1' id='phenylethane_5_a2_phenylethane_5_a3' atomRefs2='phenylethane_5_a2 phenylethane_5_a3'/>"+
		     "<bond order='1' id='phenylethane_5_a2_phenylethane_5_a13' atomRefs2='phenylethane_5_a2 phenylethane_5_a13'/>"+
		     "<bond order='2' id='phenylethane_5_a3_phenylethane_5_a8' atomRefs2='phenylethane_5_a3 phenylethane_5_a8'/>"+
		     "<bond order='1' id='phenylethane_5_a3_phenylethane_5_a4' atomRefs2='phenylethane_5_a3 phenylethane_5_a4'/>"+
		     "<bond order='2' id='phenylethane_5_a4_phenylethane_5_a5' atomRefs2='phenylethane_5_a4 phenylethane_5_a5'/>"+
		     "<bond order='1' id='phenylethane_5_a4_phenylethane_5_a14' atomRefs2='phenylethane_5_a4 phenylethane_5_a14'/>"+
		     "<bond order='1' id='phenylethane_5_a5_phenylethane_5_a6' atomRefs2='phenylethane_5_a5 phenylethane_5_a6'/>"+
		     "<bond order='1' id='phenylethane_5_a5_phenylethane_5_a15' atomRefs2='phenylethane_5_a5 phenylethane_5_a15'/>"+
		     "<bond order='2' id='phenylethane_5_a6_phenylethane_5_a7' atomRefs2='phenylethane_5_a6 phenylethane_5_a7'/>"+
		     "<bond order='1' id='phenylethane_5_a6_phenylethane_5_a16' atomRefs2='phenylethane_5_a6 phenylethane_5_a16'/>"+
		     "<bond order='1' id='phenylethane_5_a7_phenylethane_5_a8' atomRefs2='phenylethane_5_a7 phenylethane_5_a8'/>"+
		     "<bond order='1' id='phenylethane_5_a7_phenylethane_5_a17' atomRefs2='phenylethane_5_a7 phenylethane_5_a17'/>"+
		     "<bond order='1' id='phenylethane_5_a8_phenylethane_5_a18' atomRefs2='phenylethane_5_a8 phenylethane_5_a18'/>"+
		     "<bond atomRefs2='po_4_a2 phenylethane_5_a2' order='S' id='po_4_a2_phenylethane_5_a2'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a2' atomRefs2='phenylethane_6_a1 phenylethane_6_a2'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a9' atomRefs2='phenylethane_6_a1 phenylethane_6_a9'/>"+
		     "<bond order='1' id='phenylethane_6_a1_phenylethane_6_a10' atomRefs2='phenylethane_6_a1 phenylethane_6_a10'/>"+
		     "<bond order='1' id='phenylethane_6_a2_phenylethane_6_a3' atomRefs2='phenylethane_6_a2 phenylethane_6_a3'/>"+
		     "<bond order='1' id='phenylethane_6_a2_phenylethane_6_a13' atomRefs2='phenylethane_6_a2 phenylethane_6_a13'/>"+
		     "<bond order='2' id='phenylethane_6_a3_phenylethane_6_a8' atomRefs2='phenylethane_6_a3 phenylethane_6_a8'/>"+
		     "<bond order='1' id='phenylethane_6_a3_phenylethane_6_a4' atomRefs2='phenylethane_6_a3 phenylethane_6_a4'/>"+
		     "<bond order='2' id='phenylethane_6_a4_phenylethane_6_a5' atomRefs2='phenylethane_6_a4 phenylethane_6_a5'/>"+
		     "<bond order='1' id='phenylethane_6_a4_phenylethane_6_a14' atomRefs2='phenylethane_6_a4 phenylethane_6_a14'/>"+
		     "<bond order='1' id='phenylethane_6_a5_phenylethane_6_a6' atomRefs2='phenylethane_6_a5 phenylethane_6_a6'/>"+
		     "<bond order='1' id='phenylethane_6_a5_phenylethane_6_a15' atomRefs2='phenylethane_6_a5 phenylethane_6_a15'/>"+
		     "<bond order='2' id='phenylethane_6_a6_phenylethane_6_a7' atomRefs2='phenylethane_6_a6 phenylethane_6_a7'/>"+
		     "<bond order='1' id='phenylethane_6_a6_phenylethane_6_a16' atomRefs2='phenylethane_6_a6 phenylethane_6_a16'/>"+
		     "<bond order='1' id='phenylethane_6_a7_phenylethane_6_a8' atomRefs2='phenylethane_6_a7 phenylethane_6_a8'/>"+
		     "<bond order='1' id='phenylethane_6_a7_phenylethane_6_a17' atomRefs2='phenylethane_6_a7 phenylethane_6_a17'/>"+
		     "<bond order='1' id='phenylethane_6_a8_phenylethane_6_a18' atomRefs2='phenylethane_6_a8 phenylethane_6_a18'/>"+
		     "<bond atomRefs2='phenylethane_5_a1 phenylethane_6_a2' order='S' id='phenylethane_5_a1_phenylethane_6_a2'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a2' atomRefs2='phenylethane_7_a1 phenylethane_7_a2'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a9' atomRefs2='phenylethane_7_a1 phenylethane_7_a9'/>"+
		     "<bond order='1' id='phenylethane_7_a1_phenylethane_7_a10' atomRefs2='phenylethane_7_a1 phenylethane_7_a10'/>"+
		     "<bond order='1' id='phenylethane_7_a2_phenylethane_7_a3' atomRefs2='phenylethane_7_a2 phenylethane_7_a3'/>"+
		     "<bond order='1' id='phenylethane_7_a2_phenylethane_7_a13' atomRefs2='phenylethane_7_a2 phenylethane_7_a13'/>"+
		     "<bond order='2' id='phenylethane_7_a3_phenylethane_7_a8' atomRefs2='phenylethane_7_a3 phenylethane_7_a8'/>"+
		     "<bond order='1' id='phenylethane_7_a3_phenylethane_7_a4' atomRefs2='phenylethane_7_a3 phenylethane_7_a4'/>"+
		     "<bond order='2' id='phenylethane_7_a4_phenylethane_7_a5' atomRefs2='phenylethane_7_a4 phenylethane_7_a5'/>"+
		     "<bond order='1' id='phenylethane_7_a4_phenylethane_7_a14' atomRefs2='phenylethane_7_a4 phenylethane_7_a14'/>"+
		     "<bond order='1' id='phenylethane_7_a5_phenylethane_7_a6' atomRefs2='phenylethane_7_a5 phenylethane_7_a6'/>"+
		     "<bond order='1' id='phenylethane_7_a5_phenylethane_7_a15' atomRefs2='phenylethane_7_a5 phenylethane_7_a15'/>"+
		     "<bond order='2' id='phenylethane_7_a6_phenylethane_7_a7' atomRefs2='phenylethane_7_a6 phenylethane_7_a7'/>"+
		     "<bond order='1' id='phenylethane_7_a6_phenylethane_7_a16' atomRefs2='phenylethane_7_a6 phenylethane_7_a16'/>"+
		     "<bond order='1' id='phenylethane_7_a7_phenylethane_7_a8' atomRefs2='phenylethane_7_a7 phenylethane_7_a8'/>"+
		     "<bond order='1' id='phenylethane_7_a7_phenylethane_7_a17' atomRefs2='phenylethane_7_a7 phenylethane_7_a17'/>"+
		     "<bond order='1' id='phenylethane_7_a8_phenylethane_7_a18' atomRefs2='phenylethane_7_a8 phenylethane_7_a18'/>"+
		     "<bond atomRefs2='phenylethane_6_a1 phenylethane_7_a2' order='S' id='phenylethane_6_a1_phenylethane_7_a2'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a2' atomRefs2='phenylethane_8_a1 phenylethane_8_a2'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a9' atomRefs2='phenylethane_8_a1 phenylethane_8_a9'/>"+
		     "<bond order='1' id='phenylethane_8_a1_phenylethane_8_a10' atomRefs2='phenylethane_8_a1 phenylethane_8_a10'/>"+
		     "<bond order='1' id='phenylethane_8_a2_phenylethane_8_a3' atomRefs2='phenylethane_8_a2 phenylethane_8_a3'/>"+
		     "<bond order='1' id='phenylethane_8_a2_phenylethane_8_a13' atomRefs2='phenylethane_8_a2 phenylethane_8_a13'/>"+
		     "<bond order='2' id='phenylethane_8_a3_phenylethane_8_a8' atomRefs2='phenylethane_8_a3 phenylethane_8_a8'/>"+
		     "<bond order='1' id='phenylethane_8_a3_phenylethane_8_a4' atomRefs2='phenylethane_8_a3 phenylethane_8_a4'/>"+
		     "<bond order='2' id='phenylethane_8_a4_phenylethane_8_a5' atomRefs2='phenylethane_8_a4 phenylethane_8_a5'/>"+
		     "<bond order='1' id='phenylethane_8_a4_phenylethane_8_a14' atomRefs2='phenylethane_8_a4 phenylethane_8_a14'/>"+
		     "<bond order='1' id='phenylethane_8_a5_phenylethane_8_a6' atomRefs2='phenylethane_8_a5 phenylethane_8_a6'/>"+
		     "<bond order='1' id='phenylethane_8_a5_phenylethane_8_a15' atomRefs2='phenylethane_8_a5 phenylethane_8_a15'/>"+
		     "<bond order='2' id='phenylethane_8_a6_phenylethane_8_a7' atomRefs2='phenylethane_8_a6 phenylethane_8_a7'/>"+
		     "<bond order='1' id='phenylethane_8_a6_phenylethane_8_a16' atomRefs2='phenylethane_8_a6 phenylethane_8_a16'/>"+
		     "<bond order='1' id='phenylethane_8_a7_phenylethane_8_a8' atomRefs2='phenylethane_8_a7 phenylethane_8_a8'/>"+
		     "<bond order='1' id='phenylethane_8_a7_phenylethane_8_a17' atomRefs2='phenylethane_8_a7 phenylethane_8_a17'/>"+
		     "<bond order='1' id='phenylethane_8_a8_phenylethane_8_a18' atomRefs2='phenylethane_8_a8 phenylethane_8_a18'/>"+
		     "<bond atomRefs2='phenylethane_7_a1 phenylethane_8_a2' order='S' id='phenylethane_7_a1_phenylethane_8_a2'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a3' atomRefs2='po_9_a2 po_9_a3'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a7' atomRefs2='po_9_a2 po_9_a7'/>"+
		     "<bond order='1' id='po_9_a2_po_9_a8' atomRefs2='po_9_a2 po_9_a8'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a4' atomRefs2='po_9_a3 po_9_a4'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a5' atomRefs2='po_9_a3 po_9_a5'/>"+
		     "<bond order='1' id='po_9_a3_po_9_a9' atomRefs2='po_9_a3 po_9_a9'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a10' atomRefs2='po_9_a4 po_9_a10'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a11' atomRefs2='po_9_a4 po_9_a11'/>"+
		     "<bond order='1' id='po_9_a4_po_9_a12' atomRefs2='po_9_a4 po_9_a12'/>"+
		     "<bond atomRefs2='phenylethane_8_a1 po_9_a5' order='S' id='phenylethane_8_a1_po_9_a5'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a3' atomRefs2='po_10_a2 po_10_a3'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a7' atomRefs2='po_10_a2 po_10_a7'/>"+
		     "<bond order='1' id='po_10_a2_po_10_a8' atomRefs2='po_10_a2 po_10_a8'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a4' atomRefs2='po_10_a3 po_10_a4'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a5' atomRefs2='po_10_a3 po_10_a5'/>"+
		     "<bond order='1' id='po_10_a3_po_10_a9' atomRefs2='po_10_a3 po_10_a9'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a10' atomRefs2='po_10_a4 po_10_a10'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a11' atomRefs2='po_10_a4 po_10_a11'/>"+
		     "<bond order='1' id='po_10_a4_po_10_a12' atomRefs2='po_10_a4 po_10_a12'/>"+
		     "<bond atomRefs2='po_9_a2 po_10_a5' order='S' id='po_9_a2_po_10_a5'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a3' atomRefs2='po_11_a2 po_11_a3'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a7' atomRefs2='po_11_a2 po_11_a7'/>"+
		     "<bond order='1' id='po_11_a2_po_11_a8' atomRefs2='po_11_a2 po_11_a8'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a4' atomRefs2='po_11_a3 po_11_a4'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a5' atomRefs2='po_11_a3 po_11_a5'/>"+
		     "<bond order='1' id='po_11_a3_po_11_a9' atomRefs2='po_11_a3 po_11_a9'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a10' atomRefs2='po_11_a4 po_11_a10'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a11' atomRefs2='po_11_a4 po_11_a11'/>"+
		     "<bond order='1' id='po_11_a4_po_11_a12' atomRefs2='po_11_a4 po_11_a12'/>"+
		     "<bond atomRefs2='po_10_a2 po_11_a5' order='S' id='po_10_a2_po_11_a5'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a2' atomRefs2='phenylethane_12_a1 phenylethane_12_a2'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a9' atomRefs2='phenylethane_12_a1 phenylethane_12_a9'/>"+
		     "<bond order='1' id='phenylethane_12_a1_phenylethane_12_a10' atomRefs2='phenylethane_12_a1 phenylethane_12_a10'/>"+
		     "<bond order='1' id='phenylethane_12_a2_phenylethane_12_a3' atomRefs2='phenylethane_12_a2 phenylethane_12_a3'/>"+
		     "<bond order='1' id='phenylethane_12_a2_phenylethane_12_a13' atomRefs2='phenylethane_12_a2 phenylethane_12_a13'/>"+
		     "<bond order='2' id='phenylethane_12_a3_phenylethane_12_a8' atomRefs2='phenylethane_12_a3 phenylethane_12_a8'/>"+
		     "<bond order='1' id='phenylethane_12_a3_phenylethane_12_a4' atomRefs2='phenylethane_12_a3 phenylethane_12_a4'/>"+
		     "<bond order='2' id='phenylethane_12_a4_phenylethane_12_a5' atomRefs2='phenylethane_12_a4 phenylethane_12_a5'/>"+
		     "<bond order='1' id='phenylethane_12_a4_phenylethane_12_a14' atomRefs2='phenylethane_12_a4 phenylethane_12_a14'/>"+
		     "<bond order='1' id='phenylethane_12_a5_phenylethane_12_a6' atomRefs2='phenylethane_12_a5 phenylethane_12_a6'/>"+
		     "<bond order='1' id='phenylethane_12_a5_phenylethane_12_a15' atomRefs2='phenylethane_12_a5 phenylethane_12_a15'/>"+
		     "<bond order='2' id='phenylethane_12_a6_phenylethane_12_a7' atomRefs2='phenylethane_12_a6 phenylethane_12_a7'/>"+
		     "<bond order='1' id='phenylethane_12_a6_phenylethane_12_a16' atomRefs2='phenylethane_12_a6 phenylethane_12_a16'/>"+
		     "<bond order='1' id='phenylethane_12_a7_phenylethane_12_a8' atomRefs2='phenylethane_12_a7 phenylethane_12_a8'/>"+
		     "<bond order='1' id='phenylethane_12_a7_phenylethane_12_a17' atomRefs2='phenylethane_12_a7 phenylethane_12_a17'/>"+
		     "<bond order='1' id='phenylethane_12_a8_phenylethane_12_a18' atomRefs2='phenylethane_12_a8 phenylethane_12_a18'/>"+
		     "<bond atomRefs2='po_11_a2 phenylethane_12_a2' order='S' id='po_11_a2_phenylethane_12_a2'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a2' atomRefs2='phenylethane_13_a1 phenylethane_13_a2'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a9' atomRefs2='phenylethane_13_a1 phenylethane_13_a9'/>"+
		     "<bond order='1' id='phenylethane_13_a1_phenylethane_13_a10' atomRefs2='phenylethane_13_a1 phenylethane_13_a10'/>"+
		     "<bond order='1' id='phenylethane_13_a2_phenylethane_13_a3' atomRefs2='phenylethane_13_a2 phenylethane_13_a3'/>"+
		     "<bond order='1' id='phenylethane_13_a2_phenylethane_13_a13' atomRefs2='phenylethane_13_a2 phenylethane_13_a13'/>"+
		     "<bond order='2' id='phenylethane_13_a3_phenylethane_13_a8' atomRefs2='phenylethane_13_a3 phenylethane_13_a8'/>"+
		     "<bond order='1' id='phenylethane_13_a3_phenylethane_13_a4' atomRefs2='phenylethane_13_a3 phenylethane_13_a4'/>"+
		     "<bond order='2' id='phenylethane_13_a4_phenylethane_13_a5' atomRefs2='phenylethane_13_a4 phenylethane_13_a5'/>"+
		     "<bond order='1' id='phenylethane_13_a4_phenylethane_13_a14' atomRefs2='phenylethane_13_a4 phenylethane_13_a14'/>"+
		     "<bond order='1' id='phenylethane_13_a5_phenylethane_13_a6' atomRefs2='phenylethane_13_a5 phenylethane_13_a6'/>"+
		     "<bond order='1' id='phenylethane_13_a5_phenylethane_13_a15' atomRefs2='phenylethane_13_a5 phenylethane_13_a15'/>"+
		     "<bond order='2' id='phenylethane_13_a6_phenylethane_13_a7' atomRefs2='phenylethane_13_a6 phenylethane_13_a7'/>"+
		     "<bond order='1' id='phenylethane_13_a6_phenylethane_13_a16' atomRefs2='phenylethane_13_a6 phenylethane_13_a16'/>"+
		     "<bond order='1' id='phenylethane_13_a7_phenylethane_13_a8' atomRefs2='phenylethane_13_a7 phenylethane_13_a8'/>"+
		     "<bond order='1' id='phenylethane_13_a7_phenylethane_13_a17' atomRefs2='phenylethane_13_a7 phenylethane_13_a17'/>"+
		     "<bond order='1' id='phenylethane_13_a8_phenylethane_13_a18' atomRefs2='phenylethane_13_a8 phenylethane_13_a18'/>"+
		     "<bond atomRefs2='phenylethane_12_a1 phenylethane_13_a2' order='S' id='phenylethane_12_a1_phenylethane_13_a2'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a2' atomRefs2='phenylethane_14_a1 phenylethane_14_a2'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a9' atomRefs2='phenylethane_14_a1 phenylethane_14_a9'/>"+
		     "<bond order='1' id='phenylethane_14_a1_phenylethane_14_a10' atomRefs2='phenylethane_14_a1 phenylethane_14_a10'/>"+
		     "<bond order='1' id='phenylethane_14_a2_phenylethane_14_a3' atomRefs2='phenylethane_14_a2 phenylethane_14_a3'/>"+
		     "<bond order='1' id='phenylethane_14_a2_phenylethane_14_a13' atomRefs2='phenylethane_14_a2 phenylethane_14_a13'/>"+
		     "<bond order='2' id='phenylethane_14_a3_phenylethane_14_a8' atomRefs2='phenylethane_14_a3 phenylethane_14_a8'/>"+
		     "<bond order='1' id='phenylethane_14_a3_phenylethane_14_a4' atomRefs2='phenylethane_14_a3 phenylethane_14_a4'/>"+
		     "<bond order='2' id='phenylethane_14_a4_phenylethane_14_a5' atomRefs2='phenylethane_14_a4 phenylethane_14_a5'/>"+
		     "<bond order='1' id='phenylethane_14_a4_phenylethane_14_a14' atomRefs2='phenylethane_14_a4 phenylethane_14_a14'/>"+
		     "<bond order='1' id='phenylethane_14_a5_phenylethane_14_a6' atomRefs2='phenylethane_14_a5 phenylethane_14_a6'/>"+
		     "<bond order='1' id='phenylethane_14_a5_phenylethane_14_a15' atomRefs2='phenylethane_14_a5 phenylethane_14_a15'/>"+
		     "<bond order='2' id='phenylethane_14_a6_phenylethane_14_a7' atomRefs2='phenylethane_14_a6 phenylethane_14_a7'/>"+
		     "<bond order='1' id='phenylethane_14_a6_phenylethane_14_a16' atomRefs2='phenylethane_14_a6 phenylethane_14_a16'/>"+
		     "<bond order='1' id='phenylethane_14_a7_phenylethane_14_a8' atomRefs2='phenylethane_14_a7 phenylethane_14_a8'/>"+
		     "<bond order='1' id='phenylethane_14_a7_phenylethane_14_a17' atomRefs2='phenylethane_14_a7 phenylethane_14_a17'/>"+
		     "<bond order='1' id='phenylethane_14_a8_phenylethane_14_a18' atomRefs2='phenylethane_14_a8 phenylethane_14_a18'/>"+
		     "<bond atomRefs2='phenylethane_13_a1 phenylethane_14_a2' order='S' id='phenylethane_13_a1_phenylethane_14_a2'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a2' atomRefs2='phenylethane_15_a1 phenylethane_15_a2'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a9' atomRefs2='phenylethane_15_a1 phenylethane_15_a9'/>"+
		     "<bond order='1' id='phenylethane_15_a1_phenylethane_15_a10' atomRefs2='phenylethane_15_a1 phenylethane_15_a10'/>"+
		     "<bond order='1' id='phenylethane_15_a2_phenylethane_15_a3' atomRefs2='phenylethane_15_a2 phenylethane_15_a3'/>"+
		     "<bond order='1' id='phenylethane_15_a2_phenylethane_15_a13' atomRefs2='phenylethane_15_a2 phenylethane_15_a13'/>"+
		     "<bond order='2' id='phenylethane_15_a3_phenylethane_15_a8' atomRefs2='phenylethane_15_a3 phenylethane_15_a8'/>"+
		     "<bond order='1' id='phenylethane_15_a3_phenylethane_15_a4' atomRefs2='phenylethane_15_a3 phenylethane_15_a4'/>"+
		     "<bond order='2' id='phenylethane_15_a4_phenylethane_15_a5' atomRefs2='phenylethane_15_a4 phenylethane_15_a5'/>"+
		     "<bond order='1' id='phenylethane_15_a4_phenylethane_15_a14' atomRefs2='phenylethane_15_a4 phenylethane_15_a14'/>"+
		     "<bond order='1' id='phenylethane_15_a5_phenylethane_15_a6' atomRefs2='phenylethane_15_a5 phenylethane_15_a6'/>"+
		     "<bond order='1' id='phenylethane_15_a5_phenylethane_15_a15' atomRefs2='phenylethane_15_a5 phenylethane_15_a15'/>"+
		     "<bond order='2' id='phenylethane_15_a6_phenylethane_15_a7' atomRefs2='phenylethane_15_a6 phenylethane_15_a7'/>"+
		     "<bond order='1' id='phenylethane_15_a6_phenylethane_15_a16' atomRefs2='phenylethane_15_a6 phenylethane_15_a16'/>"+
		     "<bond order='1' id='phenylethane_15_a7_phenylethane_15_a8' atomRefs2='phenylethane_15_a7 phenylethane_15_a8'/>"+
		     "<bond order='1' id='phenylethane_15_a7_phenylethane_15_a17' atomRefs2='phenylethane_15_a7 phenylethane_15_a17'/>"+
		     "<bond order='1' id='phenylethane_15_a8_phenylethane_15_a18' atomRefs2='phenylethane_15_a8 phenylethane_15_a18'/>"+
		     "<bond atomRefs2='phenylethane_14_a1 phenylethane_15_a2' order='S' id='phenylethane_14_a1_phenylethane_15_a2'/>"+
		     "<bond order='D' id='acetyl_16_a71_acetyl_16_a72' atomRefs2='acetyl_16_a71 acetyl_16_a72'/>"+
		     "<bond order='S' id='acetyl_16_a71_acetyl_16_a73' atomRefs2='acetyl_16_a71 acetyl_16_a73'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a74' atomRefs2='acetyl_16_a73 acetyl_16_a74'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a75' atomRefs2='acetyl_16_a73 acetyl_16_a75'/>"+
		     "<bond order='S' id='acetyl_16_a73_acetyl_16_a76' atomRefs2='acetyl_16_a73 acetyl_16_a76'/>"+
		     "<bond atomRefs2='phenylethane_15_a1 acetyl_16_a71' order='S' id='phenylethane_15_a1_acetyl_16_a71'/>"+
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
		   "<torsion id='po_4_tor2' atomRefs4='po_4_a2 po_4_a3 po_4_a5 po_4_r2'/>"+
		   "<torsion id='po_4_tor1' atomRefs4='po_4_r1 po_4_a2 po_4_a3 po_4_a5'/>"+
		   "<angle id='po_4_ang352' atomRefs3='po_4_a3 po_4_a5 po_4_r2'/>"+
		   "<angle id='po_4_ang123' atomRefs3='po_4_r1 po_4_a2 po_4_a3'/>"+
		   "<angle id='po_4_ang234' atomRefs3='po_4_a2 po_4_a3 po_4_a4'/>"+
		   "<length id='po_4_len23' atomRefs2='po_4_a2 po_4_a3'/>"+
		   "<torsion id='phenylethane_5_side' atomRefs4='phenylethane_5_a1 phenylethane_5_a2 phenylethane_5_a3 phenylethane_5_a4'/>"+
		   "<torsion id='phenylethane_5_main' atomRefs4='phenylethane_5_r1 phenylethane_5_a1 phenylethane_5_a2 phenylethane_5_r2'/>"+
		   "<torsion id='phenylethane_6_side' atomRefs4='phenylethane_6_a1 phenylethane_6_a2 phenylethane_6_a3 phenylethane_6_a4'/>"+
		   "<torsion id='phenylethane_6_main' atomRefs4='phenylethane_6_r1 phenylethane_6_a1 phenylethane_6_a2 phenylethane_6_r2'/>"+
		   "<torsion id='phenylethane_7_side' atomRefs4='phenylethane_7_a1 phenylethane_7_a2 phenylethane_7_a3 phenylethane_7_a4'/>"+
		   "<torsion id='phenylethane_7_main' atomRefs4='phenylethane_7_r1 phenylethane_7_a1 phenylethane_7_a2 phenylethane_7_r2'/>"+
		   "<torsion id='phenylethane_8_side' atomRefs4='phenylethane_8_a1 phenylethane_8_a2 phenylethane_8_a3 phenylethane_8_a4'/>"+
		   "<torsion id='phenylethane_8_main' atomRefs4='phenylethane_8_r1 phenylethane_8_a1 phenylethane_8_a2 phenylethane_8_r2'/>"+
		   "<torsion id='po_9_tor2' atomRefs4='po_9_a2 po_9_a3 po_9_a5 po_9_r2'/>"+
		   "<torsion id='po_9_tor1' atomRefs4='po_9_r1 po_9_a2 po_9_a3 po_9_a5'/>"+
		   "<angle id='po_9_ang352' atomRefs3='po_9_a3 po_9_a5 po_9_r2'/>"+
		   "<angle id='po_9_ang123' atomRefs3='po_9_r1 po_9_a2 po_9_a3'/>"+
		   "<angle id='po_9_ang234' atomRefs3='po_9_a2 po_9_a3 po_9_a4'/>"+
		   "<length id='po_9_len23' atomRefs2='po_9_a2 po_9_a3'/>"+
		   "<torsion id='po_10_tor2' atomRefs4='po_10_a2 po_10_a3 po_10_a5 po_10_r2'/>"+
		   "<torsion id='po_10_tor1' atomRefs4='po_10_r1 po_10_a2 po_10_a3 po_10_a5'/>"+
		   "<angle id='po_10_ang352' atomRefs3='po_10_a3 po_10_a5 po_10_r2'/>"+
		   "<angle id='po_10_ang123' atomRefs3='po_10_r1 po_10_a2 po_10_a3'/>"+
		   "<angle id='po_10_ang234' atomRefs3='po_10_a2 po_10_a3 po_10_a4'/>"+
		   "<length id='po_10_len23' atomRefs2='po_10_a2 po_10_a3'/>"+
		   "<torsion id='po_11_tor2' atomRefs4='po_11_a2 po_11_a3 po_11_a5 po_11_r2'/>"+
		   "<torsion id='po_11_tor1' atomRefs4='po_11_r1 po_11_a2 po_11_a3 po_11_a5'/>"+
		   "<angle id='po_11_ang352' atomRefs3='po_11_a3 po_11_a5 po_11_r2'/>"+
		   "<angle id='po_11_ang123' atomRefs3='po_11_r1 po_11_a2 po_11_a3'/>"+
		   "<angle id='po_11_ang234' atomRefs3='po_11_a2 po_11_a3 po_11_a4'/>"+
		   "<length id='po_11_len23' atomRefs2='po_11_a2 po_11_a3'/>"+
		   "<torsion id='phenylethane_12_side' atomRefs4='phenylethane_12_a1 phenylethane_12_a2 phenylethane_12_a3 phenylethane_12_a4'/>"+
		   "<torsion id='phenylethane_12_main' atomRefs4='phenylethane_12_r1 phenylethane_12_a1 phenylethane_12_a2 phenylethane_12_r2'/>"+
		   "<torsion id='phenylethane_13_side' atomRefs4='phenylethane_13_a1 phenylethane_13_a2 phenylethane_13_a3 phenylethane_13_a4'/>"+
		   "<torsion id='phenylethane_13_main' atomRefs4='phenylethane_13_r1 phenylethane_13_a1 phenylethane_13_a2 phenylethane_13_r2'/>"+
		   "<torsion id='phenylethane_14_side' atomRefs4='phenylethane_14_a1 phenylethane_14_a2 phenylethane_14_a3 phenylethane_14_a4'/>"+
		   "<torsion id='phenylethane_14_main' atomRefs4='phenylethane_14_r1 phenylethane_14_a1 phenylethane_14_a2 phenylethane_14_r2'/>"+
		   "<torsion id='phenylethane_15_side' atomRefs4='phenylethane_15_a1 phenylethane_15_a2 phenylethane_15_a3 phenylethane_15_a4'/>"+
		   "<torsion id='phenylethane_15_main' atomRefs4='phenylethane_15_r1 phenylethane_15_a1 phenylethane_15_a2 phenylethane_15_r2'/>"+
		 "</molecule>"+
		"</fragment>";
			
		testAll(fragment, debug, 4,
				intermediateS, explicitS, completeS, check);
	}

	/** adjusts length, angle, torsion */
	private CMLFragment makeMol5() {
		String fragmentS = "" +
		"<fragment convention='cml:PML-intermediate' " +
		"   xmlns='http://www.xml-cml.org/schema'" +
		"   xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"<!--" +
		"          adjustable quantities " +
		"	<length atomRefs2='a2 a3' id='len23'></length>"+
		"   <angle atomRefs3='a2 a3 a4' id='ang234'></angle>"+
		"   <angle atomRefs3='r1 a2 a3' id='ang123'></angle>"+
		"   <angle atomRefs3='a3 a5 r2' id='ang352'></angle>"+
		"   <torsion atomRefs4='r1 a2 a3 a5' id='tor1'></torsion>"+
		"   <torsion atomRefs4='a2 a3 a5 r2' id='tor2'></torsion>" +
		"-->" +
		"  <fragment>"+
		"    <molecule ref='g:2pyr'/>" +
		"  </fragment>"+
		"  <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>" +
		"    <torsion>121</torsion>" +
		"    <length>1.51</length>" +
		"  </join>"+
		"  <fragment countExpression='*(2)'>"+
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>" +
		"      <torsion>170</torsion>" +
		"      <length>1.41</length>" +
		"    </join>"+
		"    <fragment>" +
		"      <molecule ref='g:po'>" +
		"        <arg name='len23'>" +
		"          <scalar>1.53</scalar>" +
		"        </arg>" +
		"        <arg name='ang123'>" +
		"          <scalar>110.1</scalar>" +
		"        </arg>" +
		"        <arg name='ang234'>" +
		"          <scalar>111.2</scalar>" +
		"        </arg>" +
		"        <arg name='ang352'>" +
		"          <scalar>112.3</scalar>" +
		"        </arg>" +
		"        <arg name='tor1'>" +
		"          <scalar>-65</scalar>" +
		"        </arg>" +
		"        <arg name='tor2'>" +
		"          <scalar>165</scalar>" +
		"        </arg>" +
		"      </molecule>" +
		"    </fragment>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"    <torsion>23</torsion>" +
		"    <length>1.52</length>" +
		"  </join>"+
		"  <fragment>"+
		"    <molecule ref='g:acetyl'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}
	
	/** test 5*/
//	@Test
	public void testAll5() {
		CMLFragment fragment = makeMol5();
		boolean debug = false;
		boolean check = false;

		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		"</fragment>";
		
		String explicitS = "" +
		"<fragment convention='cml:PML-explicit' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
		"</fragment>";
		
		String completeS =
			"<fragment convention='cml:PML-complete' xmlns:g='http://www.xml-cml.org/mols/geom1' xmlns='http://www.xml-cml.org/schema'>"+
	"</fragment>";
		
		testAll(fragment, debug, 5,
				intermediateS, explicitS, completeS, check);
		
		CMLMolecule molecule = new FragmentTool(fragment).getMolecule();
//		"    <torsion>121</torsion>" +
//		"    <length>1.51</length>" +
		testTorsion(molecule, "2pyr_1_a2", "2pyr_1_a1", "po_2_a5", "po_2_a3", 121, 0.001);
		testLength(molecule, "2pyr_1_a1", "po_2_a5", 1.510, 0.001);
//		"        <arg name='len23'>" +
//		"          <scalar>1.53</scalar>" +
//		"        </arg>" +
		testLength(molecule, "po_2_a2", "po_2_a3", 1.530, 0.001);
//		"        <arg name='ang123'>" +
//		"          <scalar>110.1</scalar>" +
//		"        </arg>" +
//		"        <arg name='ang234'>" +
//		"          <scalar>111.2</scalar>" +
//		"        </arg>" +
//		"        <arg name='ang352'>" +
//		"          <scalar>112.3</scalar>" +
//		"        </arg>" +
		testAngle(molecule, "po_3_a5", "po_2_a2", "po_2_a3", 110.1, 0.001);
		testAngle(molecule, "po_2_a2", "po_2_a3", "po_2_a4", 111.2, 0.001);
		testAngle(molecule, "po_2_a3", "po_2_a5", "2pyr_1_a1", 112.3, 0.001);
//		"        <arg name='tor1'>" +
//		"          <scalar>-65</scalar>" +
//		"        </arg>" +
//		<torsion atomRefs4="r1 a2 a3 a5" id="tor1"></torsion>
//		"        <arg name='tor2'>" +
//		"          <scalar>165</scalar>" +
//		"        </arg>" +
//		<torsion atomRefs4="a2 a3 a5 r2" id="tor2"></torsion>
		testTorsion(molecule, "po_3_a5", "po_2_a2", "po_2_a3", "po_2_a5", -65, 0.001);
		testTorsion(molecule, "po_2_a2", "po_2_a3", "po_2_a5", "2pyr_1_a1", 165, 0.001);
//		"      <torsion>170</torsion>" +
//		"      <length>1.41</length>" +
		testTorsion(molecule, "po_2_a3", "po_2_a2", "po_3_a5", "po_3_a3", 170, 0.001);
		testLength(molecule, "po_2_a2", "po_3_a5", 1.410, 0.001);
//		"        <arg name='len23'>" +
//		"          <scalar>1.53</scalar>" +
//		"        </arg>" +
		testLength(molecule, "po_2_a2", "po_2_a3", 1.530, 0.001);
//		"        <arg name='ang123'>" +
//		"          <scalar>110.1</scalar>" +
//		"        </arg>" +
//		"        <arg name='ang234'>" +
//		"          <scalar>111.2</scalar>" +
//		"        </arg>" +
//		"        <arg name='ang352'>" +
//		"          <scalar>112.3</scalar>" +
//		"        </arg>" +
		testAngle(molecule, "acetyl_4_a71", "po_3_a2", "po_3_a3", 110.1, 0.001);
		testAngle(molecule, "po_3_a2", "po_3_a3", "po_3_a4", 111.2, 0.001);
		testAngle(molecule, "po_3_a3", "po_3_a5", "po_2_a2", 112.3, 0.001);
//		"        <arg name='tor1'>" +
//		"          <scalar>-65</scalar>" +
//		"        </arg>" +
//		<torsion atomRefs4="r1 a2 a3 a5" id="tor1"></torsion>
//		"        <arg name='tor2'>" +
//		"          <scalar>165</scalar>" +
//		"        </arg>" +
//		<torsion atomRefs4="a2 a3 a5 r2" id="tor2"></torsion>
		testTorsion(molecule, "acetyl_4_a71", "po_3_a2", "po_3_a3", "po_3_a5", -65, 0.001);
		testTorsion(molecule, "po_3_a2", "po_3_a3", "po_3_a5", "po_2_a2", 165, 0.001);
//		"    <torsion>23</torsion>" +
//		"    <length>1.52</length>" +
		testTorsion(molecule, "po_3_a3", "po_3_a2", "acetyl_4_a71", "acetyl_4_a72", 23, 0.001);
		testLength(molecule, "po_3_a2", "acetyl_4_a71", 1.52, 0.001);
	}
	
	private CMLFragment makeMol6() {
		// EtO(C=O)-(CH(CH3)C(=O)O(CH2CH(Ph).(CH(Me)CH2O)4.Ac)10-Et)3-2pyr
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:coxy'/>" +
		"  </fragment>"+
		"  <join atomRefs2='a36 r2' moleculeRefs2='PREVIOUS NEXT'>"+
		"    <torsion>180</torsion>" +
		"  </join>"+
		"  <fragment countExpression='*(2)'>"+
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>"+
		"      <torsion>60</torsion>" +
		"    </join>"+
		"    <fragment>" +
		"      <molecule ref='g:acryl'>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:phenylethane'/>" +
		"          </fragment>"+
		"          <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"            <torsion>180</torsion>" +
		"          </join>"+
		"          <fragment countExpression='*(4)'>"+
		"            <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>"+
		"              <torsion>180</torsion>" +
		"            </join>"+
		"            <fragment>" +
		"              <molecule ref='g:po'/>" +
		"            </fragment>"+
		"          </fragment>"+
		"          <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"          <fragment>"+
		"            <molecule ref='g:acetyl'/>" +
		"          </fragment>"+
		"        </fragment>"+
		"        </join>" +
		"      </molecule>"+
		"    </fragment>"+
		"  </fragment>"+
		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'>"+
		"    <torsion>180</torsion>" +
		"  </join>"+
		"  <fragment>"+
		"    <molecule ref='g:2pyr'/>"+
		"  </fragment>"+
		"</fragment>";
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll6() {
		CMLFragment fragment = makeMol6();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
				"<foo/>";
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 6,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol7() {
		// recursive 1,3,5, benzene
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
//		"  <fragment>"+
//		"    <molecule ref='g:dummy'/>" +
//		"  </fragment>"+
//		"  <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'>"+
//		"    <torsion>180</torsion>" +
//		"  </join>"+
		"  <fragment>"+
		"    <molecule ref='g:benzene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>90</torsion>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:benzene'>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"            </molecule>" +
		"          </fragment>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>" +
		"        <torsion>90</torsion>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:benzene'>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"            </molecule>" +
		"          </fragment>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r1'>" +
		"        <torsion>90</torsion>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:benzene'>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"              <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"                <torsion>90</torsion>" +
		"                <fragment>" +
		"                  <fragment>"+
		"                    <molecule ref='g:benzene'/>" +
		"                  </fragment>"+
		"                </fragment>"+
		"              </join>" +
		"            </molecule>" +
		"          </fragment>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll7() {
		CMLFragment fragment = makeMol7();
		boolean debug = false;
		boolean check = false;
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' " +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1' " +
		"  xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:dummy' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:dummy_1_r1 g:benzene_2_r2' moleculeRefs2='g:dummy_1 g:benzene_2'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:benzene' id='2'>"+
		   "<join atomRefs2='g:benzene_2_r1 g:benzene_3_r1' moleculeRefs2='g:benzene_2 g:benzene_3'>"+
		     "<molecule ref='g:benzene' id='3'>"+
		       "<join moleculeRefs2='PARENT NEXT' atomRefs2='r3 r1'>"+
		         "<molecule ref='g:et' id='4_2'>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>4</scalar>"+
		           "</arg>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>2</scalar>"+
		           "</arg>"+
		         "</molecule>"+
		       "</join>"+
		       "<join moleculeRefs2='PARENT NEXT' atomRefs2='r5 r1'>"+
		         "<molecule ref='g:me' id='5_3'>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>5</scalar>"+
		           "</arg>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>3</scalar>"+
		           "</arg>"+
		         "</molecule>"+
		       "</join>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>3</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		   "</join>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 7,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol8() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment>"+
		"            <molecule ref='g:benzene'>" +
		"            </molecule>" +
		"          </fragment>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll8() {
		CMLFragment fragment = makeMol8();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<fragment convention='cml:PML-intermediate' " +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1' " +
		"  xmlns='http://www.xml-cml.org/schema'>"+
		 "<molecule ref='g:dummy' id='1'>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>1</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		 "<join atomRefs2='g:dummy_1_r1 g:benzene_2_r2' moleculeRefs2='g:dummy_1 g:benzene_2'>"+
		   "<torsion>180</torsion>"+
		 "</join>"+
		 "<molecule ref='g:benzene' id='2'>"+
		   "<join atomRefs2='g:benzene_2_r1 g:benzene_3_r1' moleculeRefs2='g:benzene_2 g:benzene_3'>"+
		     "<molecule ref='g:benzene' id='3'>"+
		       "<join moleculeRefs2='PARENT NEXT' atomRefs2='r3 r1'>"+
		         "<molecule ref='g:et' id='4_2'>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>4</scalar>"+
		           "</arg>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>2</scalar>"+
		           "</arg>"+
		         "</molecule>"+
		       "</join>"+
		       "<join moleculeRefs2='PARENT NEXT' atomRefs2='r5 r1'>"+
		         "<molecule ref='g:me' id='5_3'>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>5</scalar>"+
		           "</arg>"+
		           "<arg name='idx'>"+
		             "<scalar dataType='xsd:string'>3</scalar>"+
		           "</arg>"+
		         "</molecule>"+
		       "</join>"+
		       "<arg name='idx'>"+
		         "<scalar dataType='xsd:string'>3</scalar>"+
		       "</arg>"+
		     "</molecule>"+
		   "</join>"+
		   "<arg name='idx'>"+
		     "<scalar dataType='xsd:string'>2</scalar>"+
		   "</arg>"+
		 "</molecule>"+
		"</fragment>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 8,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol9() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='po'>"+
		"      <molecule ref='g:po'/>" +
		"    </fragment>"+
		"    <fragment id='eocl'>"+
		"      <fragment>"+
		"        <molecule ref='g:eo'/>" +
		"      </fragment>"+
		"      <join moleculeRefs2='PREVIOUS NEXT' atomRefs2='r1 r1'/>"+
		"      <fragment>"+
		"        <molecule ref='g:cl'/>" +
		"      </fragment>"+
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='po'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='eocl'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
//	@Ignore
	public void testAll9() {
		CMLFragment fragment = makeMol9();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 9,
				intermediateS, explicitS, completeS, check);
	}
	
	/** tests 20_
	 */
	@Test
	public void test20_() {
		testAll20();
		testAll21();
		testAll22();
//		testAll23();
//		testAll24();
	}

	private CMLFragment makeMol20() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='f'>"+
		"      <molecule ref='g:f'/>" +
		"    </fragment>"+
		"    <fragment id='br'>"+
		"      <molecule ref='g:br'/>" +
		"    </fragment>"+
		"    <fragment id='cl'>"+
		"      <molecule ref='g:cl'/>" +
		"    </fragment>"+
		"    <fragment id='nsp2'>"+
		"      <molecule ref='g:nsp2'/>" +
		"    </fragment>"+
		"    <fragment id='oh'>"+
		"      <molecule ref='g:oh'/>" +
		"    </fragment>"+
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragmentList role='markush'>" +
		"    <fragmentList role='markushList' id='halogen'>" +
		"      <fragment ref='f'/>" +
		"      <fragment ref='cl'/>" +
		"      <fragment ref='br'/>" +
		"    </fragmentList>" +
//		"    <fragmentList role='markushList' id='polar'>" +
//		"      <fragment ref='nsp2'/>" +
//		"      <fragment ref='oh'/>" +
//		"    </fragmentList>" +
		"    <fragment role='markushTarget'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r1 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='halogen'/>"+
		"          </fragment>"+
		"        </join>" +
//		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>" +
//		"          <torsion>45</torsion>" +
//		"          <fragment>" +
//		"            <fragment ref='polar'/>"+
//		"          </fragment>"+
//		"        </join>" +
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll20() {
		CMLFragment fragment = makeMol20();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 20,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol21() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='f'>"+
		"      <molecule ref='g:f'/>" +
		"    </fragment>"+
		"    <fragment id='br'>"+
		"      <molecule ref='g:br'/>" +
		"    </fragment>"+
		"    <fragment id='cl'>"+
		"      <molecule ref='g:cl'/>" +
		"    </fragment>"+
		"    <fragment id='nsp2'>"+
		"      <molecule ref='g:nsp2'/>" +
		"    </fragment>"+
		"    <fragment id='oh'>"+
		"      <molecule ref='g:oh'/>" +
		"    </fragment>"+
		"    <fragment id='ethyl'>"+
		"      <molecule ref='g:et'/>" +
		"    </fragment>"+
		"    <fragment id='methyl'>"+
		"      <molecule ref='g:me'/>" +
		"    </fragment>"+
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragmentList role='markush'>" +
		"    <fragmentList role='markushList' id='halogen'>" +
		"      <fragment ref='f'/>" +
		"      <fragment ref='cl'/>" +
		"      <fragment ref='br'/>" +
		"    </fragmentList>" +
		"    <fragmentList role='markushList' id='polar'>" +
		"      <fragment ref='nsp2'/>" +
		"      <fragment ref='oh'/>" +
		"    </fragmentList>" +
		"    <fragmentList role='markushList' id='alkyl'>" +
		"      <fragment ref='ethyl'/>" +
		"      <fragment ref='methyl'/>" +
		"    </fragmentList>" +
		"    <fragment role='markushTarget'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r1 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='halogen'/>"+
		"          </fragment>"+
		"        </join>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='polar'/>"+
		"          </fragment>"+
		"        </join>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='alkyl'/>"+
		"          </fragment>"+
		"        </join>" +
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll21() {
		CMLFragment fragment = makeMol21();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 21,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol22() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='f'>"+
		"      <molecule ref='g:f'/>" +
		"    </fragment>"+
		"    <fragment id='br'>"+
		"      <molecule ref='g:br'/>" +
		"    </fragment>"+
		"    <fragment id='cl'>"+
		"      <molecule ref='g:cl'/>" +
		"    </fragment>"+
		"    <fragment id='nsp2'>"+
		"      <molecule ref='g:nsp2'/>" +
		"    </fragment>"+
		"    <fragment id='oh'>"+
		"      <molecule ref='g:oh'/>" +
		"    </fragment>"+
		"    <fragment id='ethyl'>"+
		"      <molecule ref='g:et'/>" +
		"    </fragment>"+
		"    <fragment id='methyl'>"+
		"      <molecule ref='g:me'/>" +
		"    </fragment>"+
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragmentList role='markush'>" +
		"    <fragmentList role='markushMixture' id='halogen'>" +
		"      <fragment ref='f'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.2</scalar>" +
		"      </fragment>" +
		"      <fragment ref='cl'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.5</scalar>" +
		"      </fragment>" +
		"      <fragment ref='br'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.3</scalar>" +
		"      </fragment>" +
		"    </fragmentList>" +
		"    <fragmentList role='markushMixture' id='polar'>" +
		"      <fragment ref='nsp2'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.7</scalar>" +
		"      </fragment>" +
		"      <fragment ref='oh'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.3</scalar>" +
		"      </fragment>" +
		"    </fragmentList>" +
		"    <fragmentList role='markushMixture' id='alkyl'>" +
		"      <fragment ref='ethyl'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.4</scalar>" +
		"      </fragment>" +
		"      <fragment ref='methyl'>" +
		"        <scalar dictRef='cml:ratio' dataType='xsd:double'>0.6</scalar>" +
		"      </fragment>" +
		"    </fragmentList>" +
		"    <fragment role='markushTarget' countExpression='*(15)'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r1 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='halogen'/>"+
		"          </fragment>"+
		"        </join>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='polar'/>"+
		"          </fragment>"+
		"        </join>" +
		"        <join moleculeRefs2='PARENT CHILD' atomRefs2='r5 r1'>" +
		"          <torsion>45</torsion>" +
		"          <fragment>" +
		"            <fragment ref='alkyl'/>"+
		"          </fragment>"+
		"        </join>" +
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll22() {
		CMLFragment fragment = makeMol22();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 22,
				intermediateS, explicitS, completeS, check);
	}
	
	// polyinfo examples
	private CMLFragment makeMol30() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='p129'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='p708'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>" +
		"  </fragmentList>"+
		"  <fragment countExpression='*(10)'>" +
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'>" +
		"      <torsion>180</torsion>" +
		"    </join>"+
		"    <fragment>" +
		"      <fragment>" +
		"        <molecule ref='g:ch'>" +
		"          <join moleculeRefs2='PARENT CHILD' atomRefs2='r3 r1'>" +
		"            <fragment ref='p708'/>"+
		"          </join>" +
		"        </molecule>"+
		"      </fragment>" +
		"      <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='p129'/>"+
		"    </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}


	// markov chain for single fragment
	private CMLFragment makeMol23() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='cl'>"+
		"      <molecule ref='g:cl'/>" +
		"    </fragment>"+
		"    <fragment id='eo'>"+
		"      <molecule ref='g:eo'/>" +
		"    </fragment>"+
		"    <fragment id='eoA'>" +
		"      <fragment ref='eo'/>" +
		"      <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='AAA'/>"+
		"    </fragment>"+
		"    <fragment id='AAA'>" +
		"      <fragmentList role='markushMixture'>" +
		"        <fragment ref='eo'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.05</scalar>" +
		"        </fragment>" +
		"        <fragment ref='eoA'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.95</scalar>" +
		"        </fragment>" +
		"      </fragmentList>" +
		"    </fragment>" +
		"  </fragmentList>" +
		"" +
		"  <fragment id='f0'>" +
		"    <fragment>"+
		"      <molecule ref='g:acetyl'/>" +
		"    </fragment>" +
		"    <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>" +
		"    <fragment ref='AAA'/>"+
		"    <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"    <fragment ref='cl'/>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll23() {
		CMLFragment fragment = makeMol23();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 23,
				intermediateS, explicitS, completeS, check);
	}

	// markov chain for double fragment
	private CMLFragment makeMol24() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='acetyl'>"+
		"      <molecule ref='g:acetyl'/>" +
		"    </fragment>"+
		"    <fragment id='cl'>"+
		"      <molecule ref='g:cl'/>" +
		"    </fragment>"+
		"    <fragment id='eo'>"+
		"      <molecule ref='g:eo'/>" +
		"    </fragment>"+
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"    <fragment id='eE'>" +
		"      <fragment ref='eo'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='EE'/>"+
		"    </fragment>"+
		"    <fragment id='eB'>" +
		"      <fragment ref='eo'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='BB'/>"+
		"    </fragment>"+
		"    <fragment id='EE'>" +
		"      <fragmentList role='markushMixture'>" +
		"        <fragment ref='eo'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.01</scalar>" +
		"        </fragment>" +
		"        <fragment ref='eE'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.84</scalar>" +
		"        </fragment>" +
		"        <fragment ref='eB'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.15</scalar>" +
		"        </fragment>" +
		"      </fragmentList>" +
		"    </fragment>" +
		"    <fragment id='bE'>" +
		"      <fragment ref='benzene'/>" +
		"      <join atomRefs2='r4 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='EE'/>"+
		"    </fragment>"+
		"    <fragment id='bB'>" +
		"      <fragment ref='benzene'/>" +
		"      <join atomRefs2='r4 r1' moleculeRefs2='PREVIOUS NEXT'/>"+
		"      <fragment ref='BB'/>"+
		"    </fragment>"+
		"    <fragment id='BB'>" +
		"      <fragmentList role='markushMixture'>" +
		"        <fragment ref='benzene'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.01</scalar>" +
		"        </fragment>" +
		"        <fragment ref='bB'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.84</scalar>" +
		"        </fragment>" +
		"        <fragment ref='bE'>" +
		"          <scalar dictRef='cml:ratio' dataType='xsd:double'>0.15</scalar>" +
		"        </fragment>" +
		"      </fragmentList>" +
		"    </fragment>" +
		"  </fragmentList>" +
		"" +
		"  <fragment id='f0'>" +
		"    <fragment ref='cl'/>"+
		"    <join atomRefs2='r1 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"    <fragment ref='EE'/>"+
		"    <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"    <fragment ref='acetyl'/>" +
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll24() {
		CMLFragment fragment = makeMol24();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 24,
				intermediateS, explicitS, completeS, check);
	}


	/** tests second ten examples
	 */
	@Test
	public void test10_() {
		testAll10();
		testAll11();
		testAll12();
		testAll13();
		testAll14();
	}


	private CMLFragment makeMol10() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='eo'>"+
		"      <molecule ref='g:eo'/>" +
		"    </fragment>"+
		"    <fragment id='po'>"+
		"      <molecule ref='g:po'/>" +
		"    </fragment>"+
		"    <fragment id='eocl'>"+
		"      <fragment>"+
		"        <molecule ref='g:eo'/>" +
		"      </fragment>"+
		"      <join moleculeRefs2='PREVIOUS NEXT' atomRefs2='r1 r1'/>"+
		"      <fragment>"+
		"        <molecule ref='g:cl'/>" +
		"      </fragment>"+
		"    </fragment>"+
		"    <fragment id='eopoeo'>"+
		"      <fragment ref='eo'/>" +
		"      <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='po'/>" +
		"      <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='eo'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='po'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='eocl'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='eopoeo'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
//	@Ignore
	public void testAll10() {
		CMLFragment fragment = makeMol10();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 10,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol11() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"    <fragment id='benzene3'>"+
		"      <fragment ref='benzene'/>" +
		"      <join atomRefs2='r4 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='benzene'/>" +
		"      <join atomRefs2='r4 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='benzene'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r2'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testAll11() {
		CMLFragment fragment = makeMol11();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 11,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol12() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"    <fragment id='benzene3'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment id='benzene'>"+
		"            <molecule ref='g:benzene'/>" +
		"          </fragment>"+
		"        </join>"+
		"        <join atomRefs2='r5 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment id='benzene'>"+
		"            <molecule ref='g:benzene'/>" +
		"          </fragment>"+
		"        </join>"+
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll12() {
		CMLFragment fragment = makeMol12();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 12,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol13() {
		// as 12 but uses references
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"    <fragment id='benzene3'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene'/>"+
		"        </join>"+
		"        <join atomRefs2='r5 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene'/>"+
		"        </join>"+
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

//	@Test
	public void testAll13() {
		CMLFragment fragment = makeMol13();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 13,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol14() {
		// as 12 but uses references
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='benzene'>"+
		"      <molecule ref='g:benzene'/>" +
		"    </fragment>"+
		"    <fragment id='benzene3'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene3a'/>"+
		"        </join>"+
		"        <join atomRefs2='r5 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene3a'/>"+
		"        </join>"+
		"      </molecule>"+
		"    </fragment>"+
		"    <fragment id='benzene3a'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene3b'/>"+
		"        </join>"+
		"        <join atomRefs2='r5 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene3b'/>"+
		"        </join>"+
		"      </molecule>"+
		"    </fragment>"+
		"    <fragment id='benzene3b'>"+
		"      <molecule ref='g:benzene'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene'/>"+
		"        </join>"+
		"        <join atomRefs2='r5 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='benzene'/>"+
		"        </join>"+
		"      </molecule>"+
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"    <molecule ref='g:triazene'>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r2 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r4 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"      <join moleculeRefs2='PARENT CHILD' atomRefs2='r6 r1'>" +
		"        <torsion>45</torsion>" +
		"        <fragment>" +
		"          <fragment ref='benzene3'/>"+
		"        </fragment>"+
		"      </join>" +
		"    </molecule>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testAll14() {
		CMLFragment fragment = makeMol14();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 14,
				intermediateS, explicitS, completeS, check);
	}

	
	/** tests 50-57
	 */
	@Test
	public void test50_57() {
		testPEO();
		testPEOFromScratch();
		testNylon3();
		testNylon3alternative();
		testNylon44();
		testPVC();
		testPVCHeadTail();
	}

	
	private CMLFragment makeMol50() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='A'>"+
		"      <molecule ref='g:eo'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"     <fragment countExpression='*(3)'>" +
		"       <join atomRefs2='r1 r2' moleculeRefs2='PREVIOUS NEXT'/>" +
		"       <fragment ref='A'>" +
//		"         <molecule ref='g:eo'/>" +
		"       </fragment>"+
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testPEO() {
		CMLFragment fragment = makeMol50();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 50,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol51() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"  <fragmentList>" +
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='O'>"+
		"      <molecule ref='g:o'/>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='O'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment>"+
		"     <fragment countExpression='*(4)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"       <fragment ref='A'>" +
		"       </fragment>"+
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testPEOFromScratch() {
		CMLFragment fragment = makeMol51();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 51,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol52() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"  <!-- nylon3 -->"+
		"  <fragmentList>" +
		"    <fragment id='CO'>"+
		"      <molecule ref='g:carbonyl'/>" +
		"    </fragment>"+
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='NH'>"+
		"      <molecule ref='g:nsp2'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment>"+
		"            <molecule ref='g:h'/>" +
		"          </fragment>"+
		"        </join>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='CO'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='NH'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment title='nylon3'>"+
		"     <fragment countExpression='*(4)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"       </join>" +
		"       <fragment ref='A'>" +
		"       </fragment>"+
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testNylon3() {
		CMLFragment fragment = makeMol52();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 52,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol53() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"  <!-- nylon3 alternative repeat-->"+
		"  <fragmentList>" +
		"    <fragment id='CO'>"+
		"      <molecule ref='g:carbonyl'/>" +
		"    </fragment>"+
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='NH'>"+
		"      <molecule ref='g:nsp2'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment>"+
		"            <molecule ref='g:h'/>" +
		"          </fragment>"+
		"        </join>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='NH'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"      </join>" +
		"      <fragment ref='CO'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment title='nylon3 alternative'>"+
		"     <fragment countExpression='*(4)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"       </join>" +
		"       <fragment ref='A'>" +
		"       </fragment>"+
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testNylon3alternative() {
		CMLFragment fragment = makeMol53();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 53,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol54() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"  <!-- nylon44 explicit-->"+
		"  <fragmentList>" +
		"    <fragment id='CO'>"+
		"      <molecule ref='g:carbonyl'/>" +
		"    </fragment>"+
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='NH'>"+
		"      <molecule ref='g:nsp2'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment>"+
		"            <molecule ref='g:h'/>" +
		"          </fragment>"+
		"        </join>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='CO'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CO'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"      </join>" +
		"      <fragment ref='NH'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='NH'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment title='nylon44 explicit'>"+
		"     <fragment countExpression='*(3)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"       </join>" +
		"       <fragment ref='A'/>" +
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testNylon44() {
		CMLFragment fragment = makeMol54();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 54,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol56() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"  <!-- PVC as (CH2CH(Cl))n -->"+
		"  <fragmentList>" +
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='Cl'>"+
		"      <molecule ref='g:cl'>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='CHCl'>"+
		"      <molecule ref='g:ch'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='Cl'/>" +
		"        </join>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CHCl'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment title='PVC'>"+
		"     <fragment countExpression='*(3)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"       </join>" +
		"       <fragment ref='A'/>" +
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testPVC() {
		CMLFragment fragment = makeMol56();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 56,
				intermediateS, explicitS, completeS, check);
	}

	private CMLFragment makeMol57() {
		// 
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>" +
		"  <!-- PVC as head-head etc.-->"+
		"  <fragmentList>" +
		"    <fragment id='CH2'>"+
		"      <molecule ref='g:ch2'/>" +
		"    </fragment>"+
		"    <fragment id='Cl'>"+
		"      <molecule ref='g:cl'>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='CHCl'>"+
		"      <molecule ref='g:ch'>" +
		"        <join atomRefs2='r3 r1' moleculeRefs2='PARENT CHILD'>" +
		"          <fragment ref='Cl'/>" +
		"        </join>" +
		"      </molecule>" +
		"    </fragment>"+
		"    <fragment id='A'>"+
		"      <fragment ref='CH2'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CHCl'/>" +
		"    </fragment>"+
		"    <fragment id='ATail'>"+
		"      <fragment ref='CHCl'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>" +
		"      <fragment ref='CH2'/>" +
		"    </fragment>"+
		"  </fragmentList>" +
		"  <fragment title='PVC'>"+
		"     <fragment countExpression='*(3)'>" +
		"       <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"         <torsion>180</torsion>" +
		"       </join>" +
		"    <fragment>"+
		"      <fragment ref='A'/>" +
		"      <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'>" +
		"        <torsion>180</torsion>" +
		"      </join>" +
		"      <fragment ref='ATail'/>" +
		"    </fragment>"+
		"     </fragment>"+
		"  </fragment>"+
		"</fragment>";
		
		return (CMLFragment) parseValidString(fragmentS);
	}

	@Test
	public void testPVCHeadTail() {
		CMLFragment fragment = makeMol57();
		boolean debug = false;
		boolean check = false;
		
		String intermediateS = "" +
		"<foo/>";
		
		String explicitS = "" +
				"<foo/>";
		String completeS = "" +
				"<foo/>";
			
		testAll(fragment, debug, 57,
				intermediateS, explicitS, completeS, check);
	}


	private void testLength(CMLMolecule molecule, String id0, String id1, double lengthE, double eps) {
		CMLAtom atom0 = molecule.getAtomById(id0);
		CMLAtom atom1 = molecule.getAtomById(id1);
		double length = GeometryTool.getCalculatedLength(atom0, atom1);
		Assert.assertEquals("length "+id0+" "+id1, lengthE, length, eps);
	}

	private void testAngle(
			CMLMolecule molecule, String id0, String id1, String id2, 
			double angleE, double eps) {
		CMLAtom atom0 = molecule.getAtomById(id0);
		CMLAtom atom1 = molecule.getAtomById(id1);
		CMLAtom atom2 = molecule.getAtomById(id2);
		Angle angle = GeometryTool.getCalculatedAngle(atom0, atom1, atom2);
		Assert.assertNotNull("angle not null", angle);
		Assert.assertEquals("angle "+id0+" "+id1+" "+id2, angleE, angle.getDegrees(), eps);
	}

	private void testTorsion(
			CMLMolecule molecule, String id0, String id1, String id2, 
			String id3, double angleE, double eps) {
		CMLAtom atom0 = molecule.getAtomById(id0);
		CMLAtom atom1 = molecule.getAtomById(id1);
		CMLAtom atom2 = molecule.getAtomById(id2);
		CMLAtom atom3 = molecule.getAtomById(id3);
		Angle angle = GeometryTool.getCalculatedTorsion(atom0, atom1, atom2, atom3);
		Assert.assertEquals("torsion "+id0+" "+id1+" "+id2+" "+id3, angleE, angle.getDegrees(), eps);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#expandCountExpressions()}.
	 */
	@Test
	@Ignore
	public void testExpandCountExpressions() {
		Assert.fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentTool#recursivelyCreateAtomsRefs2OnJoins(org.xmlcml.cml.element.CMLMolecule)}.
	 */
	@Test
	@Ignore
	public void testRecursivelyCreateAtomsRefs2OnJoins() {
		Assert.fail("Not yet implemented");
	}

	
	/** 
	 * no longer relevant
	 * */
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

     @Test
     public void testProperty() {
		String fragmentS = "" +
		"<fragment xmlns='http://www.xml-cml.org/schema'" +
		"  convention='PML-explicit'" +
		"  xmlns:g='http://www.xml-cml.org/mols/geom1'>"+
		"    <molecule id='m1'>" +
		"      <atomArray>" +
		"        <atom id='m1_1_r1' x3='0' y3='0' z3='0'/>" +
		"        <atom id='m1_1_r2' x3='0' y3='0' z3='1'/>" +
		"      </atomArray>" +
		"      <bondArray>" +
		"        <bond order='1' id='m1_1_r1_m1_1_r2'"+
		"          atomRefs2='m1_1_r1 m1_1_r2'/>" +
		"      </bondArray>"+
		"    </molecule>" +
		"  <join atomRefs2='m1_1_r2 m2_2_r1' moleculeRefs2='m1_1 m2_2'/>"+
		"    <molecule id='m2'>" +
		"      <atomArray>" +
		"        <atom id='m2_2_r1' x3='0' y3='0' z3='0'/>" +
		"        <atom id='m2_2_r2' x3='0' y3='0' z3='1'/>" +
		"      </atomArray>" +
		"      <bondArray>" +
		"        <bond order='1' id='m2_1_r1_m2_1_r2'"+
		"          atomRefs2='m2_2_r1 m2_2_r2'/>" +
		"      </bondArray>"+
		"    </molecule>" +
		"  <join atomRefs2='m2_2_r2 m3_3_r1' moleculeRefs2='m2_2 m3_3'/>"+
		"    <molecule id='m3'>" +
		"      <atomArray>" +
		"        <atom id='m3_3_r1' x3='0' y3='0' z3='0'/>" +
		"        <atom id='m3_3_r2' x3='0' y3='0' z3='1'/>" +
		"      </atomArray>" +
		"      <bondArray>" +
		"        <bond order='1' id='m3_3_r1_m3_3_r2'"+
		"          atomRefs2='m3_3_r1 m3_3_r2'/>" +
		"      </bondArray>"+
		"    </molecule>" +
		"      <propertyList>" +
		"        <property dictRef='cml:prop1' role='intensive'>" +
		"          <scalar units='unit:g.cm-3' dataType='xsd:double'>1.23</scalar>" +
		"        </property>" +
		"        <property dictRef='cml:prop2' role='extensive'>" +
		"          <scalar units='unit:cm3' dataType='xsd:double'>123</scalar>" +
		"        </property>" +
		"      </propertyList>" +
		"      <propertyList>" +
		"        <property dictRef='cml:prop1' role='intensive'>" +
		"          <scalar units='unit:g.cm-3' dataType='xsd:double'>2.34</scalar>" +
		"        </property>" +
		"        <property dictRef='cml:prop2' role='extensive'>" +
		"          <scalar units='unit:cm3' dataType='xsd:double'>234</scalar>" +
		"        </property>" +
		"      </propertyList>" +
		"      <propertyList>" +
		"        <property dictRef='cml:prop1' role='intensive'>" +
		"          <scalar units='unit:g.cm-3' dataType='xsd:double'>3.45</scalar>" +
		"        </property>" +
		"        <property dictRef='cml:prop2' role='extensive'>" +
		"          <scalar units='unit:cm3' dataType='xsd:double'>345</scalar>" +
		"        </property>" +
		"      </propertyList>" +
		"</fragment>";
		CMLFragment fragment = (CMLFragment) parseValidString(fragmentS);
		FragmentTool fragmentTool = new FragmentTool(fragment);
		fragmentTool.processExplicit();
		fragment = fragmentTool.getFragment();
//		  <propertyList>
//		    <property dictRef="cml:prop2" role="extensive">
//		      <scalar units="unit:cm3" dataType="xsd:double">702.0</scalar>
//		    </property>
//		    <property dictRef="cml:prop1" role="intensive">
//		      <scalar units="unit:g.cm-3" dataType="xsd:double">2.34</scalar>
//		    </property>
//		  </propertyList>
		List<Node> scalars = CMLUtil.getQueryNodes(fragment, 
				CMLPropertyList.NS+"/"+CMLProperty.NS+"/"+CMLScalar.NS, X_CML);
		Assert.assertEquals("scalars", 6, scalars.size());
		CMLScalar scalar = (CMLScalar) scalars.get(0);
		Assert.assertEquals("extensive", "intensive", ((CMLProperty)scalar.getParent()).getRole());
		Assert.assertEquals("extensive", "cml:prop1", ((CMLProperty)scalar.getParent()).getDictRef());
		Assert.assertEquals("extensive", 1.23, scalar.getDouble());
		scalar = (CMLScalar) scalars.get(1);
		Assert.assertEquals("intensive", "extensive", ((CMLProperty)scalar.getParent()).getRole());
		Assert.assertEquals("intensive", "cml:prop2", ((CMLProperty)scalar.getParent()).getDictRef());
		Assert.assertEquals("intensive", 123.0, scalar.getDouble());
     }
}
