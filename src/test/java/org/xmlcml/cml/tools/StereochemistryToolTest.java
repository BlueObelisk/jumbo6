/**
 * 
 */
package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import nu.xom.Document;
import nu.xom.Node;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.MoleculeAtomBondTest;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.test.StringTest;

/**
 * @author pm286
 *
 */
public class StereochemistryToolTest extends MoleculeAtomBondTest {

	
	private CMLMolecule makeMolecule1() {
		URL url = Util.getResource(CRYSTAL_EXAMPLES+U_S+"ci6746_1.cml.xml");
		Document document = null;
		try {
			document = new CMLBuilder().build(url.openStream());
		} catch (Exception e) {
			throw new CMLRuntimeException("should not throw "+e.getMessage());
		}
		CMLMolecule molecule = (CMLMolecule) CMLUtil.getQueryNodes(document, "//"+CMLMolecule.NS, X_CML).get(0);
		List<Node> scalars = CMLUtil.getQueryNodes(molecule, "//"+CMLScalar.NS, X_CML);
		for (Node node : scalars) {
			node.detach();
		}
		return molecule;

	}
	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#StereochemistryTool(org.xmlcml.cml.element.CMLMolecule)}.
	 */
	@Test
	public final void testStereochemistryTool() {
		// not tested
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#add2DStereo()}.
	 */
	@Test
	public final void testAdd2DStereo() {
		String cisMolS = "" +
			"<molecule "+CML_XMLNS+" >" +
			"  <atomArray>" +
			"    <atom id='a1' elementType='Cl' x2='-5' y2='10'/>" +
			"    <atom id='a2' elementType='N' x2='0' y2='0'/>" +
			"    <atom id='a3' elementType='Cl' x2='10' y2='0'/>" +
			"    <atom id='a4' elementType='Cl' x2='15' y2='10'/>" +
			"  </atomArray>" +
			"  <bondArray>" +
			"    <bond id='b12' atomRefs2='a1 a2' order='S'/>" +
			"    <bond id='b23' atomRefs2='a2 a3' order='D'/>" +
			"    <bond id='b34' atomRefs2='a3 a4' order='S'/>" +
//			"    <bond id='b13' atomRefs2='a1 a3' order='S'/>" +
			"  </bondArray>" +
			"</molecule>";
		CMLMolecule cisMol = (CMLMolecule) parseValidString(cisMolS);
		CMLAtom atom1 = cisMol.getAtomById("a1");
		CMLAtom atom2 = cisMol.getAtomById("a2");
		CMLBond bond = cisMol.getBond(atom1, atom2);
		ConnectionTableTool ctTool = new ConnectionTableTool(cisMol);
		List<CMLBond> bonds = ctTool.getCyclicBonds();
		Assert.assertEquals("cyclic", 0, bonds.size());
		bonds = ctTool.getAcyclicBonds();
		Assert.assertEquals("acyclic", 3, bonds.size());
		bonds = ctTool.getAcyclicDoubleBonds();
		Assert.assertEquals("acyclic double", 1, bonds.size());
		StereochemistryTool cisMolTool = new StereochemistryTool(cisMol);
		cisMolTool.add2DStereo();
//		  <bondArray>
//		    <bond id="b12" atomRefs2="a1 a2" userCyclic="ACYCLIC" order="1"/>
//		    <bond id="b23" atomRefs2="a2 a3" userCyclic="ACYCLIC" order="2">
//		      <bondStereo atomRefs4="a1 a2 a3 a4">C</bondStereo>
//		    </bond>
//		    <bond id="b34" atomRefs2="a3 a4" userCyclic="ACYCLIC" order="1"/>
//		  </bondArray>
		List<Node> bondStereos = CMLUtil.getQueryNodes(cisMol, CMLBondArray.NS+"/"+CMLBond.NS+"/"+CMLBondStereo.NS, X_CML);
		Assert.assertEquals("bondStereo", 1, bondStereos.size());
		CMLBondStereo bondStereo = (CMLBondStereo) bondStereos.get(0);
		String[] atomRefs4 = bondStereo.getAtomRefs4();
		StringTest.assertEquals("atomRefs4", new String[]{"a1", "a2", "a3", "a4"}, atomRefs4);
		String value = bondStereo.getXMLContent();
		Assert.assertEquals("cid", CMLBond.CIS, value);
		
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#addWedgeHatch()}.
	 */
	@Test
	public final void testAddWedgeHatch() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#addWedgeHatchBonds()}.
	 */
	@Test
	public final void testAddWedgeHatchBonds() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#calculateAtomParity(org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testCalculateAtomParity() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#isChiralCentre(org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testIsChiralCentre() {
		CMLMolecule molecule1 = makeMolecule1();
		StereochemistryTool stereochemistryTool1 = new StereochemistryTool(molecule1);
		CMLAtom atom = molecule1.getAtomById("a1");
		Assert.assertFalse("atom1 O is not chiral", stereochemistryTool1.isChiralCentre(atom));
		atom = molecule1.getAtomById("a9");
		Assert.assertTrue("atom9 C is chiral", stereochemistryTool1.isChiralCentre(atom));
		atom = molecule1.getAtomById("a5");
		Assert.assertFalse("atom5 C(Me)(Me) is not chiral", stereochemistryTool1.isChiralCentre(atom));
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#getChiralAtoms()}.
	 */
	@Test
	public final void testGetChiralAtoms() {
		CMLMolecule molecule1 = makeMolecule1();
		StereochemistryTool stereochemistryTool1 = new StereochemistryTool(molecule1);
		List<CMLAtom> chiralAtoms = stereochemistryTool1.getChiralAtoms();
		Assert.assertEquals("chiral atoms", 5, chiralAtoms.size());
		CMLAtomSet chiralSet = new CMLAtomSet(chiralAtoms);
		CMLAtomSet refSet = new CMLAtomSet(molecule1, new String[]{"a9", "a10", "a19", "a21", "a28"});
		Assert.assertTrue("chiral atoms", chiralSet.hasContentEqualTo(refSet));
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#get2DBondStereo(org.xmlcml.cml.element.CMLBond)}.
	 */
	@Test
	public final void testGet2DBondStereo() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#layoutDoubleBond(org.xmlcml.cml.element.CMLBond)}.
	 */
	@Test
	public final void testLayoutDoubleBond() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#flip2D(org.xmlcml.cml.element.CMLBond)}.
	 */
	@Test
	public final void testFlip2D() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#get3DBondStereo(org.xmlcml.cml.element.CMLBond)}.
	 */
	@Test
	public final void testGet3DBondStereo() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#calculate3DStereo(org.xmlcml.cml.element.CMLBond, org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testCalculate3DStereo() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#add3DStereo()}.
	 */
	@Test
	public final void testAdd3DStereo() {
		CMLMolecule molecule1 = makeMolecule1();
		StereochemistryTool stereochemistryTool = new StereochemistryTool(molecule1);
		stereochemistryTool.add3DStereo();
		String molS = "<molecule id='ci6746' xmlns='http://www.xml-cml.org/schema'>"+
		 "<atomArray>"+
		   "<atom id='a1' z3='1.4001067350000007' y3='11.09125056' x3='1.24111968' zFract='0.06345' yFract='1.07224' xFract='0.1356' elementType='O'/>"+
		   "<atom id='a2' z3='0.5931421440000005' y3='10.81837584' x3='-0.9602202479999999' zFract='0.02688' yFract='1.04586' xFract='-0.10491' elementType='O'/>"+
		   "<atom id='a3' z3='4.600161560999999' y3='1.8206474400000001' x3='5.345052144' zFract='0.20847' yFract='0.17601' xFract='0.58398' elementType='O'/>"+
		   "<atom id='a4' formalCharge='0' z3='5.915754367' y3='3.6059184000000006' x3='5.103052111999999' zFract='0.26809' yFract='0.3486' xFract='0.55754' elementType='O'/>"+
		   "<atom id='a5' z3='0.3005430060000007' y3='11.295648' x3='0.34963695999999994' zFract='0.01362' yFract='1.092' xFract='0.0382' elementType='C'/>"+
		   "<atom id='a6' z3='1.1214093660000002' y3='9.4947576' x3='-1.00772328' zFract='0.05082' yFract='0.9179' xFract='-0.1101' elementType='C'/>"+
		   "<atom id='a7' z3='0.4810453400000005' y3='8.8741176' x3='-0.6251362399999999' zFract='0.0218' yFract='0.8579' xFract='-0.0683' elementType='H'/>"+
		   "<atom id='a8' z3='1.2776387700000003' y3='9.2382264' x3='-1.9303255199999998' zFract='0.0579' yFract='0.8931' xFract='-0.2109' elementType='H'/>"+
		   "<atom id='a9' z3='2.4317062600000003' y3='9.426487199999999' x3='-0.226714856' zFract='0.1102' yFract='0.9113' xFract='-0.02477' elementType='C'>"+
		     "<atomParity atomRefs4='a6 a10 a28 a49'>11.158571879456787</atomParity>"+
		   "</atom>"+
		   "<atom id='a10' z3='3.1340765889999997' y3='8.033150399999998' x3='-0.24309836799999998' zFract='0.14203' yFract='0.7766' xFract='-0.02656' elementType='C'>"+
		     "<atomParity atomRefs4='a9 a11 a12 a21'>7.957964065552208</atomParity>"+
		   "</atom>"+
		   "<atom id='a11' z3='4.040339530000001' y3='8.2162392' x3='0.08329048' zFract='0.1831' yFract='0.7943' xFract='0.0091' elementType='H'/>"+
		   "<atom id='a12' z3='3.3487816880000003' y3='7.47354' x3='-1.6557415199999999' zFract='0.15176' yFract='0.7225' xFract='-0.1809' elementType='C'/>"+
		   "<atom id='a13' z3='3.66741906' y3='8.179000799999999' x3='-2.24152072' zFract='0.1662' yFract='0.7907' xFract='-0.2449' elementType='H'/>"+
		   "<atom id='a14' z3='2.5067316800000006' y3='7.144600799999999' x3='-2.00812432' zFract='0.1136' yFract='0.6907' xFract='-0.2194' elementType='H'/>"+
		   "<atom id='a15' z3='4.367582758999999' y3='6.3398376' x3='-1.6172997599999999' zFract='0.19793' yFract='0.6129' xFract='-0.1767' elementType='C'/>"+
		   "<atom id='a16' z3='5.240746249999999' y3='6.7060151999999995' x3='-1.4104464799999998' zFract='0.2375' yFract='0.6483' xFract='-0.1541' elementType='H'/>"+
		   "<atom id='a17' z3='4.417673259999999' y3='5.9302152' x3='-2.4959685599999997' zFract='0.2002' yFract='0.5733' xFract='-0.2727' elementType='H'/>"+
		   "<atom id='a18' z3='4.025113782999999' y3='5.2847496' x3='-0.6040848' zFract='0.18241' yFract='0.5109' xFract='-0.066' elementType='C'/>"+
		   "<atom id='a19' z3='3.719936854' y3='5.82760272' x3='0.7743268799999998' zFract='0.16858' yFract='0.56338' xFract='0.0846' elementType='C'>"+
		     "<atomParity atomRefs4='a18 a20 a21 a30'>-7.85269065764569</atomParity>"+
		   "</atom>"+
		   "<atom id='a20' z3='4.5346246500000005' y3='6.2777736' x3='1.08094568' zFract='0.2055' yFract='0.6069' xFract='0.1181' elementType='H'/>"+
		   "<atom id='a21' z3='2.6009547810000004' y3='6.930376559999999' x3='0.71117256' zFract='0.11787' yFract='0.66999' xFract='0.0777' elementType='C'>"+
		     "<atomParity atomRefs4='a10 a19 a22 a53'>-11.408273230609858</atomParity>"+
		   "</atom>"+
		   "<atom id='a22' z3='2.4219970880000004' y3='7.537672799999999' x3='2.102764272' zFract='0.10976' yFract='0.7287' xFract='0.22974' elementType='C'/>"+
		   "<atom id='a23' z3='3.2900853300000006' y3='7.7797224' x3='2.4584420799999998' zFract='0.1491' yFract='0.7521' xFract='0.2686' elementType='H'/>"+
		   "<atom id='a24' z3='2.0345128600000004' y3='6.8715192' x3='2.69275376' zFract='0.0922' yFract='0.6643' xFract='0.2942' elementType='H'/>"+
		   "<atom id='a25' z3='1.5250019930000007' y3='8.773780799999999' x3='2.0840925599999998' zFract='0.06911' yFract='0.8482' xFract='0.2277' elementType='C'/>"+
		   "<atom id='a26' z3='1.4166564600000005' y3='9.1120296' x3='2.9865586399999997' zFract='0.0642' yFract='0.8809' xFract='0.3263' elementType='H'/>"+
		   "<atom id='a27' z3='0.6487492200000006' y3='8.5358688' x3='1.7436083999999998' zFract='0.0294' yFract='0.8252' xFract='0.1905' elementType='H'/>"+
		   "<atom id='a28' z3='2.1364591660000007' y3='9.842316' x3='1.21000016' zFract='0.09682' yFract='0.9515' xFract='0.1322' elementType='C'>"+
		     "<atomParity atomRefs4='a1 a9 a25 a29'>7.426493938737071</atomParity>"+
		   "</atom>"+
		   "<atom id='a29' z3='3.007636690000001' y3='10.041955199999999' x3='1.6108927999999998' zFract='0.1363' yFract='0.9708' xFract='0.176' elementType='H'/>"+
		   "<atom id='a30' z3='3.4522726350000004' y3='4.7292768' x3='1.7518459199999996' zFract='0.15645' yFract='0.4572' xFract='0.1914' elementType='C'/>"+
		   "<atom id='a31' z3='2.64133611' y3='4.2793128' x3='1.6831999199999998' zFract='0.1197' yFract='0.4137' xFract='0.1839' elementType='H'/>"+
		   "<atom id='a32' z3='4.284172144999999' y3='4.3506864' x3='2.7092287999999995' zFract='0.19415' yFract='0.4206' xFract='0.296' elementType='C'/>"+
		   "<atom id='a33' z3='5.04435618' y3='4.8689208' x3='2.8437749599999997' zFract='0.2286' yFract='0.4707' xFract='0.3107' elementType='H'/>"+
		   "<atom id='a34' z3='4.108524396999999' y3='3.191124' x3='3.5648325439999997' zFract='0.18619' yFract='0.3085' xFract='0.38948' elementType='C'/>"+
		   "<atom id='a35' z3='3.268239693' y3='2.1794808' x3='3.5155904799999997' zFract='0.14811' yFract='0.2107' xFract='0.3841' elementType='C'/>"+
		   "<atom id='a36' z3='2.61706318' y3='2.0656967999999996' x3='2.86116528' zFract='0.1186' yFract='0.1997' xFract='0.3126' elementType='H'/>"+
		   "<atom id='a37' z3='3.4977292130000004' y3='1.2505896' x3='4.63772376' zFract='0.15851' yFract='0.1209' xFract='0.5067' elementType='C'/>"+
		   "<atom id='a38' z3='3.7159649200000002' y3='0.36307440000000024' x3='4.31371464' zFract='0.1684' yFract='0.0351' xFract='0.4713' elementType='H'/>"+
		   "<atom id='a39' z3='2.7141549' y3='1.1936976000000001' x3='5.206112639999999' zFract='0.123' yFract='0.1154' xFract='0.5688' elementType='H'/>"+
		   "<atom id='a40' z3='4.987425126000001' y3='2.9542464000000006' x3='4.709115599999999' zFract='0.22602' yFract='0.2856' xFract='0.5145' elementType='C'/>"+
		   "<atom id='a41' z3='-0.9892322289999993' y3='10.687420799999998' x3='0.869516' zFract='-0.04483' yFract='1.0332' xFract='0.095' elementType='C'/>"+
		   "<atom id='a42' z3='-0.9025116699999992' y3='9.7316352' x3='0.89972024' zFract='-0.0409' yFract='0.9408' xFract='0.0983' elementType='H'/>"+
		   "<atom id='a43' z3='-1.1673072699999991' y3='11.021531999999999' x3='1.7518459199999996' zFract='-0.0529' yFract='1.0655' xFract='0.1914' elementType='H'/>"+
		   "<atom id='a44' z3='-1.7123448799999992' y3='10.9242984' x3='0.28465208' zFract='-0.0776' yFract='1.0561' xFract='0.0311' elementType='H'/>"+
		   "<atom id='a45' z3='0.1924181360000008' y3='12.800699999999999' x3='0.21692135999999998' zFract='0.00872' yFract='1.2375' xFract='0.0237' elementType='C'/>"+
		   "<atom id='a46' z3='1.0062232800000008' y3='13.148258399999998' x3='-0.1555976' zFract='0.0456' yFract='1.2711' xFract='-0.017' elementType='H'/>"+
		   "<atom id='a47' z3='-0.5428309799999992' y3='13.018958399999999' x3='-0.35970504' zFract='-0.0246' yFract='1.2586' xFract='-0.0393' elementType='H'/>"+
		   "<atom id='a48' z3='0.04633923000000087' y3='13.1886' x3='1.08369152' zFract='0.0021' yFract='1.275' xFract='0.1184' elementType='H'/>"+
		   "<atom id='a49' z3='3.3904869950000003' y3='10.462956' x3='-0.8383964799999999' zFract='0.15365' yFract='1.0115' xFract='-0.0916' elementType='C'/>"+
		   "<atom id='a50' z3='3.03632288' y3='11.3452992' x3='-0.7047655999999999' zFract='0.1376' yFract='1.0968' xFract='-0.077' elementType='H'/>"+
		   "<atom id='a51' z3='4.247762750000001' y3='10.394685599999999' x3='-0.41370655999999995' zFract='0.1925' yFract='1.0049' xFract='-0.0452' elementType='H'/>"+
		   "<atom id='a52' z3='3.4842687700000003' y3='10.2953832' x3='-1.7793043199999998' zFract='0.1579' yFract='0.9953' xFract='-0.1944' elementType='H'/>"+
		   "<atom id='a53' z3='1.2809487150000003' y3='6.278808' x3='0.25536312' zFract='0.05805' yFract='0.607' xFract='0.0279' elementType='C'/>"+
		   "<atom id='a54' z3='0.6178564000000004' y3='6.9604776' x3='0.11715584' zFract='0.028' yFract='0.6729' xFract='0.0128' elementType='H'/>"+
		   "<atom id='a55' z3='1.4254829800000002' y3='5.8019495999999995' x3='-0.5647277599999999' zFract='0.0646' yFract='0.5609' xFract='-0.0617' elementType='H'/>"+
		   "<atom id='a56' z3='0.9753304600000005' y3='5.668512' x3='0.9308397599999999' zFract='0.0442' yFract='0.548' xFract='0.1017' elementType='H'/>"+
		   "<atom id='a57' z3='4.044090800999999' y3='3.997956' x3='-0.89972024' zFract='0.18327' yFract='0.3865' xFract='-0.0983' elementType='C'/>"+
		   "<atom id='a58' z3='4.27424231' y3='3.7207368' x3='-1.7573375999999998' zFract='0.1937' yFract='0.3597' xFract='-0.192' elementType='H'/>"+
		   "<atom id='a59' z3='3.82629642' y3='3.3680063999999996' x3='-0.25078671999999996' zFract='0.1734' yFract='0.3256' xFract='-0.0274' elementType='H'/>"+
		 "</atomArray>"+
		 "<metadataList>"+
		   "<metadata dictRef='cif:processedDisorder'/>"+
		 "</metadataList>"+
		 "<bondArray>"+
		   "<bond id='a1_a5' atomRefs2='a1 a5' order='1'/>"+
		   "<bond id='a1_a28' atomRefs2='a1 a28' order='1'/>"+
		   "<bond id='a2_a5' atomRefs2='a2 a5' order='1'/>"+
		   "<bond id='a2_a6' atomRefs2='a2 a6' order='1'/>"+
		   "<bond id='a3_a37' atomRefs2='a3 a37' order='1'/>"+
		   "<bond id='a3_a40' atomRefs2='a3 a40' order='1'/>"+
		   "<bond id='a4_a40' atomRefs2='a4 a40' order='2'/>"+
		   "<bond id='a5_a41' atomRefs2='a5 a41' order='1'/>"+
		   "<bond id='a5_a45' atomRefs2='a5 a45' order='1'/>"+
		   "<bond id='a6_a7' atomRefs2='a6 a7' order='1'/>"+
		   "<bond id='a6_a8' atomRefs2='a6 a8' order='1'/>"+
		   "<bond id='a6_a9' atomRefs2='a6 a9' order='1'/>"+
		   "<bond id='a9_a10' atomRefs2='a9 a10' order='1'/>"+
		   "<bond id='a9_a28' atomRefs2='a9 a28' order='1'/>"+
		   "<bond id='a9_a49' atomRefs2='a9 a49' order='1'/>"+
		   "<bond id='a10_a11' atomRefs2='a10 a11' order='1'/>"+
		   "<bond id='a10_a12' atomRefs2='a10 a12' order='1'/>"+
		   "<bond id='a10_a21' atomRefs2='a10 a21' order='1'/>"+
		   "<bond id='a12_a13' atomRefs2='a12 a13' order='1'/>"+
		   "<bond id='a12_a14' atomRefs2='a12 a14' order='1'/>"+
		   "<bond id='a12_a15' atomRefs2='a12 a15' order='1'/>"+
		   "<bond id='a15_a16' atomRefs2='a15 a16' order='1'/>"+
		   "<bond id='a15_a17' atomRefs2='a15 a17' order='1'/>"+
		   "<bond id='a15_a18' atomRefs2='a15 a18' order='1'/>"+
		   "<bond id='a18_a19' atomRefs2='a18 a19' order='1'/>"+
		   "<bond id='a18_a57' atomRefs2='a18 a57' order='2'>"+
		     "<bondStereo atomRefs4='a15 a18 a57 a59'>T</bondStereo>"+
		   "</bond>"+
		   "<bond id='a19_a20' atomRefs2='a19 a20' order='1'/>"+
		   "<bond id='a19_a21' atomRefs2='a19 a21' order='1'/>"+
		   "<bond id='a19_a30' atomRefs2='a19 a30' order='1'/>"+
		   "<bond id='a21_a22' atomRefs2='a21 a22' order='1'/>"+
		   "<bond id='a21_a53' atomRefs2='a21 a53' order='1'/>"+
		   "<bond id='a22_a23' atomRefs2='a22 a23' order='1'/>"+
		   "<bond id='a22_a24' atomRefs2='a22 a24' order='1'/>"+
		   "<bond id='a22_a25' atomRefs2='a22 a25' order='1'/>"+
		   "<bond id='a25_a26' atomRefs2='a25 a26' order='1'/>"+
		   "<bond id='a25_a27' atomRefs2='a25 a27' order='1'/>"+
		   "<bond id='a25_a28' atomRefs2='a25 a28' order='1'/>"+
		   "<bond id='a28_a29' atomRefs2='a28 a29' order='1'/>"+
		   "<bond id='a30_a31' atomRefs2='a30 a31' order='1'/>"+
		   "<bond id='a30_a32' atomRefs2='a30 a32' order='2'>"+
		     "<bondStereo atomRefs4='a19 a30 a32 a34'>T</bondStereo>"+
		   "</bond>"+
		   "<bond id='a32_a33' atomRefs2='a32 a33' order='1'/>"+
		   "<bond id='a32_a34' atomRefs2='a32 a34' order='1'/>"+
		   "<bond id='a34_a35' atomRefs2='a34 a35' order='2'>"+
		     "<bondStereo atomRefs4='a32 a34 a35 a37'>T</bondStereo>"+
		   "</bond>"+
		   "<bond id='a34_a40' atomRefs2='a34 a40' order='1'/>"+
		   "<bond id='a35_a36' atomRefs2='a35 a36' order='1'/>"+
		   "<bond id='a35_a37' atomRefs2='a35 a37' order='1'/>"+
		   "<bond id='a37_a38' atomRefs2='a37 a38' order='1'/>"+
		   "<bond id='a37_a39' atomRefs2='a37 a39' order='1'/>"+
		   "<bond id='a41_a42' atomRefs2='a41 a42' order='1'/>"+
		   "<bond id='a41_a43' atomRefs2='a41 a43' order='1'/>"+
		   "<bond id='a41_a44' atomRefs2='a41 a44' order='1'/>"+
		   "<bond id='a45_a46' atomRefs2='a45 a46' order='1'/>"+
		   "<bond id='a45_a47' atomRefs2='a45 a47' order='1'/>"+
		   "<bond id='a45_a48' atomRefs2='a45 a48' order='1'/>"+
		   "<bond id='a49_a50' atomRefs2='a49 a50' order='1'/>"+
		   "<bond id='a49_a51' atomRefs2='a49 a51' order='1'/>"+
		   "<bond id='a49_a52' atomRefs2='a49 a52' order='1'/>"+
		   "<bond id='a53_a54' atomRefs2='a53 a54' order='1'/>"+
		   "<bond id='a53_a55' atomRefs2='a53 a55' order='1'/>"+
		   "<bond id='a53_a56' atomRefs2='a53 a56' order='1'/>"+
		   "<bond id='a57_a58' atomRefs2='a57 a58' order='1'/>"+
		   "<bond id='a57_a59' atomRefs2='a57 a59' order='1'/>"+
		 "</bondArray>"+
		"</molecule>";
		CMLMolecule molecule = (CMLMolecule) parseValidString(molS);
		assertEqualsCanonically("bonds and atoms", molecule, molecule1, true);
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#get2DLigandScalarProduct(org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testGet2DLigandScalarProduct() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#get3DLigandScalarProduct(org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom, org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testGet3DLigandScalarProduct() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.StereochemistryTool#addWedgeHatchBond(org.xmlcml.cml.element.CMLAtom)}.
	 */
	@Test
	public final void testAddWedgeHatchBond() {
		fail("Not yet implemented"); // TODO
	}

}
