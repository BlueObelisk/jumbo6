package org.xmlcml.cml.element;

import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLPeakList.Type;
import org.xmlcml.cml.interfacex.PeakOrGroup;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.molutil.ChemicalElement;

/**
 * @author pm286
 */
public class CMLPeakListTest extends AbstractTest {

	CMLPeakList peakList;
	CMLPeakList peakAndGroupList;
	String peakAndGroupListS = 
		"<peakList xmlns='http://www.xml-cml.org/schema'>" +
		"  <peak id='p1'/>" +
		"  <peakGroup id='p2p3'>" +
		"    <peak id='p2'/>" +
		"    <peak id='p3'/>" +
		"  </peakGroup>" +
		"  <peak id='p4'/>" +
		"</peakList>";
	String moleculeS = 
		"<molecule xmlns='http://www.xml-cml.org/schema'>" +
		"  <atomArray>" +
		"    <atom id='a1' elementType='C'/>" +
		"    <atom id='a2' elementType='O'/>" +
		"    <atom id='a3' elementType='Cl'/>" +
		"    <atom id='a4' elementType='Cl'/>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond atomRefs2='a1 a2' order='2'/>" +
		"    <bond atomRefs2='a1 a3' order='1'/>" +
		"    <bond atomRefs2='a1 a4' order='1'/>" +
		"  </bondArray>" +
		"</molecule>";
	CMLMolecule molecule;
	CMLPeakList toluenePeakList;
	CMLMolecule toluene;
	CMLMap tolueneMap;
	
	/** 
	 */
	@Before
	public void setup() {
		if (peakList == null) {
			peakList = new CMLPeakList();
			CMLPeak peak = new CMLPeak();
			peak.setXValue(3.0);
			peak.setYValue(10.0);
			peak.setId("p1");
			peakList.addPeak(peak);
			peak = new CMLPeak();
			peak.setXValue(1.0);
			peak.setYValue(7.0);
			peak.setId("p2");
			peakList.addPeak(peak);
			peak = new CMLPeak();
			peak.setXValue(2.0);
			peak.setYValue(5.0);
			peak.setId("p3");
			peakList.addPeak(peak);
			peak = new CMLPeak();
			peak.setXValue(2.0);
			peak.setYValue(3.0);
			peak.setId("p4");
			peakList.addPeak(peak);
		}
		if (peakAndGroupList == null) {
			peakAndGroupList = (CMLPeakList) parseValidString(peakAndGroupListS);
		}
		if (molecule == null) {
			molecule = (CMLMolecule) parseValidString(moleculeS);
		}
	}
	
	private void makeToluene() {
		
		// deliberately misordered
		String tolueneS =
	    	"  <molecule id='toluene' xmlns='http://www.xml-cml.org/schema'>" +
	    	"    <atomArray>" +
	    	"      <atom id='a7' elementType='C'/>" +
	    	"      <atom id='a7_h1' elementType='H'/>" +
	    	"      <atom id='a7_h2' elementType='H'/>" +
	    	"      <atom id='a7_h3' elementType='H'/>" +
	    	"      <atom id='a1' elementType='C'/>" +
	    	"      <atom id='a2' elementType='C'/>" +
	    	"      <atom id='a2_h1' elementType='H'/>" +
	    	"      <atom id='a3' elementType='C'/>" +
	    	"      <atom id='a3_h1' elementType='H'/>" +
	    	"      <atom id='a4' elementType='C'/>" +
	    	"      <atom id='a4_h1' elementType='H'/>" +
	    	"      <atom id='a6' elementType='C'/>" +
	    	"      <atom id='a6_h1' elementType='H'/>" +
	    	"      <atom id='a5' elementType='C'/>" +
	    	"      <atom id='a5_h1' elementType='H'/>" +
	    	"    </atomArray>" +
	    	"    <bondArray>" +
	    	"      <bond id='a7_a7_h1' atomRefs2='a7 a7_h1'/>" +
	    	"      <bond id='a7_a7_h2' atomRefs2='a7 a7_h2'/>" +
	    	"      <bond id='a7_a7_h3' atomRefs2='a7 a7_h3'/>" +
	    	"      <bond id='a7_a1' atomRefs2='a7 a1'/>" +
	    	"      <bond id='a1_a2' atomRefs2='a1 a2'/>" +
	    	"      <bond id='a2_a2_h1' atomRefs2='a2 a2_h1'/>" +
	    	"      <bond id='a2_a3' atomRefs2='a2 a3'/>" +
	    	"      <bond id='a3_a3_h1' atomRefs2='a3 a3_h1'/>" +
	    	"      <bond id='a3_a4' atomRefs2='a3 a4'/>" +
	    	"      <bond id='a4_a4_h1' atomRefs2='a4 a4_h1'/>" +
	    	"      <bond id='a4_a5' atomRefs2='a4 a5'/>" +
	    	"      <bond id='a5_a5_h1' atomRefs2='a5 a5_h1'/>" +
	    	"      <bond id='a5_a6' atomRefs2='a5 a6'/>" +
	    	"      <bond id='a6_a7_h1' atomRefs2='a6 a7_h1'/>" +
	    	"      <bond id='a1_a6' atomRefs2='a1 a6'/>" +
	    	"    </bondArray>" +
	    	"  </molecule>";
		toluene = (CMLMolecule) parseValidString(tolueneS);
		
		// delieberately unordered - numbers meaningless
		String toluenePeakListS =
    	"<peakList xmlns='http://www.xml-cml.org/schema'>" +
    	"  <peak id='ph2' xValue='2.1'/>" +
    	"  <peak id='ph3' xValue='3.1'/>" +
    	"  <peak id='ph4' xValue='4.1'/>" +
    	"  <peak id='ph5' xValue='3.2'/>" +
    	"  <peak id='ph6' xValue='2.2'/>" +
    	"  <peak id='ph73' xValue='7.3'/>" +
    	"  <peak id='ph72' xValue='7.2'/>" +
    	"  <peak id='ph71' xValue='7.1'/>" +
    	
    	"  <peak id='pc1' xValue='61.1'/>" +
    	"  <peak id='pc2' xValue='62.1'/>" +
    	"  <peak id='pc3' xValue='63.1'/>" +
    	"  <peak id='pc4' xValue='64.1'/>" +
    	"  <peak id='pc5' xValue='63.2'/>" +
    	"  <peak id='pc6' xValue='62.2'/>" +
    	"</peakList>";
		
		toluenePeakList = (CMLPeakList) parseValidString(toluenePeakListS);
		
		String tolueneMapS = 
	    	"<map xmlns='http://www.xml-cml.org/schema'>" +
	    	"  <link from='a1' to='pc1'/>" +
	    	"  <link from='a2' to='pc2'/>" +
	    	"  <link from='a3' to='pc3'/>" +
	    	"  <link from='a4' to='pc4'/>" +
	    	"  <link from='a5' to='pc5'/>" +
	    	"  <link from='a6' to='pc6'/>" +
	    	"" +
	    	"  <link from='a1_h1' to='ph71'/>" +
	    	"  <link from='a1_h2' to='ph72'/>" +
	    	"  <link from='a1_h3' to='ph73'/>" +
	    	"  <link from='a2_h1' to='ph2'/>" +
	    	"  <link from='a3_h1' to='ph3'/>" +
	    	"  <link from='a4_h1' to='ph4'/>" +
	    	"  <link from='a5_h1' to='ph5'/>" +
	    	"  <link from='a6_h1' to='ph6'/>" +
	    	"</map>";
		tolueneMap = (CMLMap) parseValidString(tolueneMapS);
	}

	/**
	 */
	@Test
	public final void testGetPeaks() {
		List<CMLPeak> peaks = peakList.getPeakChildren();
		Assert.assertEquals("indexables", "p2", ((CMLPeak)peaks.get(1)).getId());
	}

	/**
	 */
	@Test
	public final void testGetPeakById() {
		CMLPeak peak = peakList.getPeakChildById("p3");
		Assert.assertEquals("indexables", "p3", peak.getId());
	}
	
	/**
	 */
	@Test
	public final void testSortByValue() {
		List<CMLPeak> peaks = peakList.getSortedPeakChildList(CMLPeakList.Type.XVALUE);
		String[] pp = new String[]{"p2", "p4", "p3", "p1"};
		for (int i = 0; i < peaks.size(); i++) {
			Assert.assertEquals("peak", pp[i], ((CMLPeak)peaks.get(i)).getId());
		}
	}
	
	/**
	 */
	@Test
    public final void testCreatePeakGroupsCMLMap() {
    	CMLMap peaks2group = new CMLMap();
    	CMLLink link = new CMLLink();
    	link.setFrom("p1");
    	link.setTo("p3");
    	peaks2group.addLink(link);
    	link = new CMLLink();
    	link.setFromSet("p2 p4");
    	link.setTo("p3");
    	peaks2group.addLink(link);
    	link = new CMLLink();
    	link.setFrom("p3");
    	link.setTo("p1");
    	peaks2group.addLink(link);
    	CMLPeakList newPeakList = peakList.createPeakGroups(peaks2group);
    	String expectedS = 
    	"<peakList xmlns='http://www.xml-cml.org/schema'>"+
    	"  <peak xValue='3.0' yValue='10.0' id='p3'/>"+
    	"    <peakGroup id='p3'>"+
    	"      <peak xValue='1.0' yValue='7.0' id='p2'/>"+
    	"      <peak xValue='2.0' yValue='3.0' id='p4'/>"+
    	"    </peakGroup>"+
    	"  <peak xValue='2.0' yValue='5.0' id='p1'/>"+
    	"</peakList>";
    	CMLPeakList expectedPeakList = (CMLPeakList) parseValidString(expectedS);
    	AbstractTest.assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
    }

	/**
	 */
	@Test
    public void testAddAtomRefsCMLMapBoolean() {
    	boolean overwrite = false;
    	CMLMap atoms2Peaks = new CMLMap();
    	CMLLink link = new CMLLink();
//        * link@from = atomId or link@fromSet = atomIds (as ws-separated string)
//        * link@to = peakId or peakGroup Id (toSet forbidden as groups should be used)
    	
    	link.setTo("p1");
    	link.setFrom("a17");
    	atoms2Peaks.addLink(link);
    	
    	peakList.addAtomRefs(atoms2Peaks, overwrite);
    	CMLPeak peak = (CMLPeak) peakList.getPeakChildById("p1");
    	StringTestBase.assertEquals("addatomref", new String[]{"a17"}, peak.getAtomRefs());

    	link.setTo("p1");
    	link.setFrom("a2");
    	atoms2Peaks.addLink(link);
    	
    	peakList.addAtomRefs(atoms2Peaks, overwrite);
    	peak = (CMLPeak) peakList.getPeakChildById("p1");
    	StringTestBase.assertEquals("addatomref", new String[]{"a17", "a2"}, peak.getAtomRefs());

    	link.setTo("p1");
    	link.setFrom("a5");
    	atoms2Peaks.addLink(link);
    	
    	overwrite = true;
    	peakList.addAtomRefs(atoms2Peaks, overwrite);
    	peak = (CMLPeak) peakList.getPeakChildById("p1");
    	StringTestBase.assertEquals("addatomref", new String[]{"a5"}, peak.getAtomRefs());

    	peakList = null;
    	
    }
	
	/**
	 */
	@Test
	public void testMorganRoutines() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom1 = new CMLAtom("a1", ChemicalElement.getChemicalElement("O"));
		molecule.addAtom(atom1);
		CMLAtom atom2 = new CMLAtom("a2", ChemicalElement.getChemicalElement("C"));
		molecule.addAtom(atom2);
		CMLAtom atom3 = new CMLAtom("a3", ChemicalElement.getChemicalElement("Cl"));
		molecule.addAtom(atom3);
		CMLAtom atom4 = new CMLAtom("a4", ChemicalElement.getChemicalElement("Cl"));
		molecule.addAtom(atom4);
		molecule.addBond(new CMLBond(atom1, atom2));
		molecule.addBond(new CMLBond(atom3, atom2));
		molecule.addBond(new CMLBond(atom4, atom2));
		CMLMap map = peakList.getPeakGroupsFromMorgan(molecule);
		
	    String expectedS = "" +
			"<map xmlns='http://www.xml-cml.org/schema'>"+			"  <link to='p1' fromSet='p1'/>"+			"  <link to='p3p4' fromSet='p3 p4'/>"+			"  <link to='p2' fromSet='p2'/>"+			"</map>";
	    AbstractTest.assertEqualsCanonically("map", parseValidString(expectedS), map, true);
	    CMLPeakList newPeakList = peakList.createPeakListGroupedByMorgan(molecule);	
	    CMLPeakGroup peakGroup = newPeakList.getPeakGroupElements().get(0);
		Assert.assertEquals("pg1", "p1", peakGroup.getId());
		CMLPeak peak = peakGroup.getPeakElements().get(0);
		Assert.assertEquals("peak", "p1", peak.getId());

		peakGroup = newPeakList.getPeakGroupElements().get(1);
		Assert.assertEquals("pg2", "p3p4", peakGroup.getId());
		peak = peakGroup.getPeakElements().get(0);
		Assert.assertEquals("peak", "p3", peak.getId());
		peak = peakGroup.getPeakElements().get(1);
		Assert.assertEquals("peak", "p4", peak.getId());

		peakGroup = newPeakList.getPeakGroupElements().get(2);
		Assert.assertEquals("pg3", "p2", peakGroup.getId());
		peak = peakGroup.getPeakElements().get(0);
		Assert.assertEquals("peak", "p2", peak.getId());
		expectedS = "" +
			"<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"  <peakGroup id='p1'>"+
			"    <peak xValue='3.0' yValue='10.0' id='p1'/>"+
			"  </peakGroup>"+
			"  <peakGroup id='p3p4'>"+
			"    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
			"    <peak xValue='2.0' yValue='3.0' id='p4'/>"+
			"  </peakGroup>"+
			"  <peakGroup id='p2'>"+
			"    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
			"  </peakGroup>"+
			"</peakList>";
		Element expectedPeakList = parseValidString(expectedS);
    	AbstractTest.assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
	}
	/**
	 */
	@Test
    public void testRemoveAtomRefsOnPeaksAndGroupsCMLAtomSetBoolean() {
    	CMLAtomSet atomSet = new CMLAtomSet();
    	atomSet.addAtom(new CMLAtom("a1"));
    	atomSet.addAtom(new CMLAtom("a3"));
    	atomSet.addAtom(new CMLAtom("a5"));
    	String peakListS =
    	"<peakList xmlns='http://www.xml-cml.org/schema'>"+
    	"  <peak id='p1' atomRefs='a1'/>"+
    	"    <peakGroup id='p2p3' atomRefs='a2 a3'>"+
    	"      <peak id='p2' atomRefs='a2'/>"+
    	"      <peak id='p3' atomRefs='a3'/>"+
    	"    </peakGroup>"+
    	"    <peakGroup id='p3'>"+
    	"      <peak id='p5' atomRefs='a5'/>"+
    	"      <peak id='p3' atomRefs='a3'/>"+
    	"    </peakGroup>"+
    	"</peakList>";
    	CMLPeakList newPeakList = (CMLPeakList) parseValidString(peakListS);
        newPeakList.removeAtomRefsOnPeaksAndGroups(atomSet, false);
        String expectedPeakListS =
        "        <peakList xmlns='http://www.xml-cml.org/schema'>"+
        "          <peak id='p1'/>"+
        "          <peakGroup id='p2p3' atomRefs='a2'>"+
        "            <peak id='p2' atomRefs='a2'/>"+
        "            <peak id='p3'/>"+
        "          </peakGroup>"+
        "          <peakGroup id='p3'>"+
        "            <peak id='p5'/>"+
        "            <peak id='p3'/>"+
        "          </peakGroup>"+
        "        </peakList>";
        
    	CMLElement expectedPeakList = (CMLPeakList) parseValidString(expectedPeakListS);
    	AbstractTest.assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
    	
    	newPeakList = (CMLPeakList) parseValidString(peakListS);
        newPeakList.removeAtomRefsOnPeaksAndGroups(atomSet, true);
        expectedPeakListS =
        	"        <peakList xmlns='http://www.xml-cml.org/schema'> "+
        	"          <peakGroup id='p2p3' atomRefs='a2'>"+
        	"            <peak id='p2' atomRefs='a2'/> "+
        	"          </peakGroup>"+
        	"        </peakList>";
        expectedPeakList = (CMLPeakList) parseValidString(expectedPeakListS);
    	AbstractTest.assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
	}

	/**
	 */
	@Test
	public void testRemoveAtomsByElementType() {
		makeToluene();
		CMLAtomSet tolueneAtomSet = toluene.getAtomSet();
		CMLAtomSet tolueneCarbonSet = tolueneAtomSet.getAtomSetByElementType("C");
		CMLAtomSet tolueneHydrogenSet = tolueneAtomSet.getAtomSetByElementType("H");
		toluenePeakList.addAtomRefs(tolueneMap, true);
    	
        toluene = null;
	}
	/** 
	 */
	@Test
	public final void testGetPeakChildren() {
		List<CMLPeak> peaks = peakList.getPeakChildren();
		Assert.assertEquals("peaks", 4, peaks.size());
		peaks = peakAndGroupList.getPeakChildren();
		Assert.assertEquals("peaks", 2, peaks.size());
	}

	/** 
	 */
	@Test
	public final void testGetPeakDescendants() {
		List<CMLPeak> peaks = peakList.getPeakDescendants();
		Assert.assertEquals("peaks", 4, peaks.size());
		peaks = peakAndGroupList.getPeakDescendants();
		Assert.assertEquals("peaks", 4, peaks.size());
	}

	/** 
	 */
	@Test
	public final void testGetPeakOrGroupChildren() {
		List<PeakOrGroup> peaks = peakList.getPeakOrGroupChildren();
		Assert.assertEquals("peakOrGroup", 4, peaks.size());
		peaks = peakAndGroupList.getPeakOrGroupChildren();
		Assert.assertEquals("peaks", 3, peaks.size());
	}

	/** 
	 */
	@Test
	public final void testGetPeakChildById() {
		CMLPeak peak = peakList.getPeakChildById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakList.getPeakChildById("p5");
		Assert.assertNull("peakById", peak);
		peak = peakAndGroupList.getPeakChildById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakAndGroupList.getPeakChildById("p2");
		Assert.assertNull("peakById", peak);
	}

	/** 
	 */
	@Test
	public final void testGetPeakDescendantById() {
		CMLPeak peak = peakList.getPeakDescendantById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakList.getPeakDescendantById("p5");
		Assert.assertNull("peakById", peak);
		peak = peakAndGroupList.getPeakDescendantById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakAndGroupList.getPeakDescendantById("p2");
		Assert.assertNotNull("peakById", peak);
	}

	/** 
	 */
	@Test
	public final void testGetPeakOrGroupChildById() {
		PeakOrGroup peakOrGroup = peakList.getPeakOrGroupChildById("p1");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeak);
		peakOrGroup = peakList.getPeakDescendantById("p5");
		Assert.assertNull("peakById", peakOrGroup);
		peakOrGroup = peakAndGroupList.getPeakOrGroupChildById("p1");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeak);
		peakOrGroup = peakAndGroupList.getPeakOrGroupChildById("p2");
		Assert.assertNull("peakById", peakOrGroup);
		peakOrGroup = peakAndGroupList.getPeakOrGroupChildById("p2p3");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeakGroup);
	}

	/** 
	 */
	@Test
	public final void testGetSortedPeakChildList() {
		List<CMLPeak> peaks = peakList.getSortedPeakChildList(Type.XVALUE);
		CMLPeakList newPeakList = new CMLPeakList(peaks);
		String expectedS =
			"		<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"		  <peak xValue='1.0' yValue='7.0' id='p2'/>"+
			"		  <peak xValue='2.0' yValue='3.0' id='p4'/>"+
			"		  <peak xValue='2.0' yValue='5.0' id='p3'/>"+
			"		  <peak xValue='3.0' yValue='10.0' id='p1'/>"+
			"		</peakList>";
		Element expected = parseValidString(expectedS);
		AbstractTest.assertEqualsCanonically("group", expected, newPeakList, true);
	}

	/** 
	 */
	@Test
	public final void testCreatePeakGroups() {
		String peaks2GroupS =
			"<map xmlns='http://www.xml-cml.org/schema'>" +
			"  <link from='p1' to='p1'/>"+
			"  <link fromSet='p2 p3' to='p2p3'/>"+
			"  <link from='p4' to='p3'/>" +
			"</map>";
		CMLMap peaks2Group = (CMLMap) parseValidString(peaks2GroupS);
		
		CMLPeakList peakList1 = (CMLPeakList) peakList.createPeakGroups(peaks2Group);
		String expectedS =
			"		<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"		  <peak xValue='3.0' yValue='10.0' id='p1'/>"+
			"		  <peakGroup id='p2p3'>"+
			"		    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
			"		    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
			"		  </peakGroup>"+
			"		  <peak xValue='2.0' yValue='3.0' id='p3'/>"+
			"		</peakList>";
		Element expected = parseValidString(expectedS);
		AbstractTest.assertEqualsCanonically("group", expected, peakList1, true);

		// omit peaks through map
		peaks2GroupS =
			"<map xmlns='http://www.xml-cml.org/schema'>" +
			"  <link fromSet='p2 p3' to='p2p3'/>"+
			"  <link from='p4' to='p3'/>" +
			"</map>";
		peaks2Group = (CMLMap) parseValidString(peaks2GroupS);
		peakList1 = (CMLPeakList) peakList.createPeakGroups(peaks2Group);
		expectedS =
			"		<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"		  <peakGroup id='p2p3'>"+
			"		    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
			"		    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
			"		  </peakGroup>"+
			"		  <peak xValue='2.0' yValue='3.0' id='p3'/>"+
			"		</peakList>";
		expected = parseValidString(expectedS);
		AbstractTest.assertEqualsCanonically("group", expected, peakList1, true);
	}

	/** 
	 */
	@Test
	public final void testCreateAtom2PeakMap() {
		CMLMolecule molecule = new CMLMolecule();
		for (int i = 0; i < 4; i++) {
			molecule.addAtom(new CMLAtom("a"+(i+1)));
		}
		CMLMap map = peakList.createAtom2PeakMap(molecule);
		String expectedS = 
		"<map xmlns='http://www.xml-cml.org/schema'>"+
		"  <link to='p1' from='a1'/>"+
		"  <link to='p2' from='a2'/>"+
		"  <link to='p3' from='a3'/>"+
		"  <link to='p4' from='a4'/>"+
		"</map>";
		AbstractTest.assertEqualsCanonically("atommap", 
				parseValidString(expectedS), map, true);
	}

	/** 
	 */
	@Test
	public final void testAddAtomRefsCMLMolecule() {
		CMLMolecule molecule = new CMLMolecule();
		for (int i = 0; i < 4; i++) {
			molecule.addAtom(new CMLAtom("a"+(i+1)));
		}
		CMLMap map = peakList.addAtomRefs(molecule);
		String expectedS = 
			"<map xmlns='http://www.xml-cml.org/schema'>"+
			"  <link to='p1' from='a1'/>"+
			"  <link to='p2' from='a2'/>"+
			"  <link to='p3' from='a3'/>"+
			"  <link to='p4' from='a4'/>"+
			"</map>";
		AbstractTest.assertEqualsCanonically("atomRefs", 
				parseValidString(expectedS), map, true);
		expectedS =
			"<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"  <peak xValue='3.0' yValue='10.0' id='p1' atomRefs='a1'/>"+
			"  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"+
			"  <peak xValue='2.0' yValue='5.0' id='p3' atomRefs='a3'/>"+
			"  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"+
			"</peakList>";
		AbstractTest.assertEqualsCanonically("atomRefs", 
				parseValidString(expectedS), peakList, true);
		peakList = null;
	}

	/** 
	 */
	@Test
	public final void testGetPeakGroupsFromMorgan() {
		CMLMap map = peakList.getPeakGroupsFromMorgan(molecule);
		String expectedS =
		"	<map xmlns='http://www.xml-cml.org/schema'>"+
		"		  <link to='p2' fromSet='p2'/>"+
		"		  <link to='p3p4' fromSet='p3 p4'/>"+
		"		  <link to='p1' fromSet='p1'/>"+
		"		</map>";
		AbstractTest.assertEqualsCanonically("atomRefs", 
				parseValidString(expectedS), map, true);
	}

	/** 
	 */
	@Test
	public final void testCreatePeakListGroupedByMorgan() {
		CMLPeakList newPeakList = peakList.createPeakListGroupedByMorgan(molecule);
		String expectedS =
		"<peakList xmlns='http://www.xml-cml.org/schema'>"+
		"  <peakGroup id='p2'>"+
		"    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
		"  </peakGroup>"+
		"  <peakGroup id='p3p4'>"+
		"    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
		"    <peak xValue='2.0' yValue='3.0' id='p4'/>"+
		"  </peakGroup>"+
		"  <peakGroup id='p1'>"+
		"    <peak xValue='3.0' yValue='10.0' id='p1'/>"+
		"  </peakGroup>"+
		"</peakList>";
		AbstractTest.assertEqualsCanonically("atomRefs", 
				parseValidString(expectedS), newPeakList, true);
	}
	
	/**
	 */
	public final void testSelectPeakChildrenByAtomId() {
		String peakList1S =
			"<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"  <peak xValue='3.0' yValue='10.0' id='p1' atomRefs='a1'/>"+
			"  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"+
			"  <peak xValue='2.0' yValue='5.0' id='p3' atomRefs='a3'/>"+
			"  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"+
			"</peakList>";
		CMLPeakList peakList1 = (CMLPeakList) parseValidString(peakList1S);
		String[] atomId = new String[]{"a1", "a3"};
		CMLPeakList newPeakList = peakList1.createPeakListFromPeakChildrenByAtomId(atomId);
		String expectedS =
			"<peakList xmlns='http://www.xml-cml.org/schema'>"+
			"  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"+
			"  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"+
			"</peakList>";
		AbstractTest.assertEqualsCanonically("select", 
				parseValidString(expectedS), newPeakList, true);
		}
}
