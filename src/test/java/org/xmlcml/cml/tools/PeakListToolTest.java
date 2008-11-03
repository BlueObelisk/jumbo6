package org.xmlcml.cml.tools;

import static org.xmlcml.cml.test.CMLAssert.assertEquals;
import static org.xmlcml.cml.test.CMLAssert.assertEqualsCanonically;
import static org.xmlcml.cml.test.CMLAssert.parseValidString;

import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakGroup;
import org.xmlcml.cml.element.CMLPeakList;
import org.xmlcml.cml.element.CMLPeakStructure;
import org.xmlcml.cml.element.CMLSpectrum;
import org.xmlcml.cml.element.CMLPeakList.Type;
import org.xmlcml.cml.interfacex.PeakOrGroup;
import org.xmlcml.cml.test.SpectrumFixture;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * test AngleTool.
 * 
 * @author pmr
 * 
 */
public class PeakListToolTest {
	SpectrumFixture fixture = new SpectrumFixture();
	CMLPeakList peakList;
	PeakListTool peakListTool;
	CMLPeakList peakAndGroupList;
	PeakListTool peakAndGroupListTool;
	String peakAndGroupListS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
			+ "  <peak id='p1'/>"
			+ "  <peakGroup id='p2p3'>"
			+ "    <peak id='p2'/>"
			+ "    <peak id='p3'/>"
			+ "  </peakGroup>"
			+ "  <peak id='p4'/>" + "</peakList>";
	String moleculeS = "<molecule xmlns='http://www.xml-cml.org/schema'>"
			+ "  <atomArray>" + "    <atom id='a1' elementType='C'/>"
			+ "    <atom id='a2' elementType='O'/>"
			+ "    <atom id='a3' elementType='Cl'/>"
			+ "    <atom id='a4' elementType='Cl'/>" + "  </atomArray>"
			+ "  <bondArray>" + "    <bond atomRefs2='a1 a2' order='2'/>"
			+ "    <bond atomRefs2='a1 a3' order='1'/>"
			+ "    <bond atomRefs2='a1 a4' order='1'/>" + "  </bondArray>"
			+ "</molecule>";
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
			peakListTool = PeakListTool.getOrCreateTool(peakList);
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
			peakAndGroupListTool = PeakListTool
					.getOrCreateTool(peakAndGroupList);
		}
		if (molecule == null) {
			molecule = (CMLMolecule) parseValidString(moleculeS);
		}
	}

	/**
	 */
	@Test
	public void testMorganRoutines() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom1 = new CMLAtom("a1", ChemicalElement
				.getChemicalElement(AS.O.value));
		molecule.addAtom(atom1);
		CMLAtom atom2 = new CMLAtom("a2", ChemicalElement
				.getChemicalElement(AS.C.value));
		molecule.addAtom(atom2);
		CMLAtom atom3 = new CMLAtom("a3", ChemicalElement
				.getChemicalElement(AS.Cl.value));
		molecule.addAtom(atom3);
		CMLAtom atom4 = new CMLAtom("a4", ChemicalElement
				.getChemicalElement(AS.Cl.value));
		molecule.addAtom(atom4);
		molecule.addBond(new CMLBond(atom1, atom2));
		molecule.addBond(new CMLBond(atom3, atom2));
		molecule.addBond(new CMLBond(atom4, atom2));
		PeakListTool peakListTool = PeakListTool.getOrCreateTool(peakList);
		CMLMap map = peakListTool.getPeakGroupsFromMorgan(molecule);

		String expectedS = "" + "<map xmlns='http://www.xml-cml.org/schema'>"
				+ "  <link to='p1' fromSet='p1'/>"
				+ "  <link to='p3p4' fromSet='p3 p4'/>"
				+ "  <link to='p2' fromSet='p2'/>" + "</map>";
		assertEqualsCanonically("map", parseValidString(expectedS), map, true);
		CMLPeakList newPeakList = peakListTool
				.createPeakListGroupedByMorgan(molecule);
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
		expectedS = "" + "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peakGroup id='p1'>"
				+ "    <peak xValue='3.0' yValue='10.0' id='p1'/>"
				+ "  </peakGroup>" + "  <peakGroup id='p3p4'>"
				+ "    <peak xValue='2.0' yValue='5.0' id='p3'/>"
				+ "    <peak xValue='2.0' yValue='3.0' id='p4'/>"
				+ "  </peakGroup>" + "  <peakGroup id='p2'>"
				+ "    <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "  </peakGroup>" + "</peakList>";
		Element expectedPeakList = parseValidString(expectedS);
		assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
	}

	/** 
	 */
	@Test
	public final void testCreatePeakListGroupedByMorgan() {
		PeakListTool peakListTool = PeakListTool.getOrCreateTool(peakList);
		CMLPeakList newPeakList = peakListTool
				.createPeakListGroupedByMorgan(molecule);
		String expectedS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peakGroup id='p2'>"
				+ "    <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "  </peakGroup>" + "  <peakGroup id='p3p4'>"
				+ "    <peak xValue='2.0' yValue='5.0' id='p3'/>"
				+ "    <peak xValue='2.0' yValue='3.0' id='p4'/>"
				+ "  </peakGroup>" + "  <peakGroup id='p1'>"
				+ "    <peak xValue='3.0' yValue='10.0' id='p1'/>"
				+ "  </peakGroup>" + "</peakList>";
		assertEqualsCanonically("atomRefs", parseValidString(expectedS),
				newPeakList, true);
	}

	/** 
	 */
	@Test
	public final void testGetPeakGroupsFromMorgan() {
		CMLMap map = PeakListTool.getOrCreateTool(peakList)
				.getPeakGroupsFromMorgan(molecule);
		String expectedS = "	<map xmlns='http://www.xml-cml.org/schema'>"
				+ "		  <link to='p2' fromSet='p2'/>"
				+ "		  <link to='p3p4' fromSet='p3 p4'/>"
				+ "		  <link to='p1' fromSet='p1'/>" + "		</map>";
		assertEqualsCanonically("atomRefs", parseValidString(expectedS), map,
				true);
	}

	/**
	 * test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCouplingsTo() throws Exception {
		CMLSpectrum spectrum = fixture.getSpectrum();
		CMLMolecule molecule = fixture.getMolecule();
		List<CMLPeak> peaks = spectrum.getCouplingsTo(molecule
				.getAtomById("a2"));
		Assert.assertEquals("couplings to a2", 2, peaks.size());
		Assert.assertEquals("p1", "p1", peaks.get(0).getId());
		Assert.assertEquals("p3", "p3", peaks.get(1).getId());
	}

	/**
	 */
	@Test
	public void testAddAtomRefsCMLMapBoolean() {
		boolean overwrite = false;
		CMLMap atoms2Peaks = new CMLMap();
		CMLLink link = new CMLLink();
		// * link@from = atomId or link@fromSet = atomIds (as ws-separated
		// string)
		// * link@to = peakId or peakGroup Id (toSet forbidden as groups should
		// be used)

		link.setTo("p1");
		link.setFrom("a17");
		atoms2Peaks.addLink(link);

		peakListTool.addAtomRefs(atoms2Peaks, overwrite);
		CMLPeak peak = (CMLPeak) peakListTool.getPeakChildById("p1");
		assertEquals("addatomref", new String[] { "a17" }, peak.getAtomRefs());

		link.setTo("p1");
		link.setFrom("a2");
		atoms2Peaks.addLink(link);

		peakListTool.addAtomRefs(atoms2Peaks, overwrite);
		peak = (CMLPeak) peakListTool.getPeakChildById("p1");
		assertEquals("addatomref", new String[] { "a17", "a2" }, peak
				.getAtomRefs());

		link.setTo("p1");
		link.setFrom("a5");
		atoms2Peaks.addLink(link);

		overwrite = true;
		peakListTool.addAtomRefs(atoms2Peaks, overwrite);
		peak = (CMLPeak) peakListTool.getPeakChildById("p1");
		assertEquals("addatomref", new String[] { "a5" }, peak.getAtomRefs());

		peakList = null;

	}

	// FIXME move to tools
	// /**
	// */
	// @Test
	// public void testMorganRoutines() {
	// CMLMolecule molecule = new CMLMolecule();
	// CMLAtom atom1 = new CMLAtom("a1",
	// ChemicalElement.getChemicalElement(AS.O.value));
	// molecule.addAtom(atom1);
	// CMLAtom atom2 = new CMLAtom("a2",
	// ChemicalElement.getChemicalElement(AS.C.value));
	// molecule.addAtom(atom2);
	// CMLAtom atom3 = new CMLAtom("a3",
	// ChemicalElement.getChemicalElement(AS.Cl.value));
	// molecule.addAtom(atom3);
	// CMLAtom atom4 = new CMLAtom("a4",
	// ChemicalElement.getChemicalElement(AS.Cl.value));
	// molecule.addAtom(atom4);
	// molecule.addBond(new CMLBond(atom1, atom2));
	// molecule.addBond(new CMLBond(atom3, atom2));
	// molecule.addBond(new CMLBond(atom4, atom2));
	// CMLMap map = peakList.getPeakGroupsFromMorgan(molecule);
	//		
	// String expectedS = "" +
	// "<map xmlns='http://www.xml-cml.org/schema'>"+
	// "  <link to='p1' fromSet='p1'/>"+
	// "  <link to='p3p4' fromSet='p3 p4'/>"+
	// "  <link to='p2' fromSet='p2'/>"+
	// "</map>";
	// AbstractTest.assertEqualsCanonically("map", parseValidString(expectedS),
	// map, true);
	//
	// CMLPeakList newPeakList =
	// peakList.createPeakListGroupedByMorgan(molecule);
	// CMLPeakGroup peakGroup = newPeakList.getPeakGroupElements().get(0);
	// Assert.assertEquals("pg1", "p1", peakGroup.getId());
	// CMLPeak peak = peakGroup.getPeakElements().get(0);
	// Assert.assertEquals("peak", "p1", peak.getId());
	//
	// peakGroup = newPeakList.getPeakGroupElements().get(1);
	// Assert.assertEquals("pg2", "p3p4", peakGroup.getId());
	// peak = peakGroup.getPeakElements().get(0);
	// Assert.assertEquals("peak", "p3", peak.getId());
	// peak = peakGroup.getPeakElements().get(1);
	// Assert.assertEquals("peak", "p4", peak.getId());
	//
	// peakGroup = newPeakList.getPeakGroupElements().get(2);
	// Assert.assertEquals("pg3", "p2", peakGroup.getId());
	// peak = peakGroup.getPeakElements().get(0);
	// Assert.assertEquals("peak", "p2", peak.getId());
	// expectedS = "" +
	// "<peakList xmlns='http://www.xml-cml.org/schema'>"+
	// "  <peakGroup id='p1'>"+
	// "    <peak xValue='3.0' yValue='10.0' id='p1'/>"+
	// "  </peakGroup>"+
	// "  <peakGroup id='p3p4'>"+
	// "    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
	// "    <peak xValue='2.0' yValue='3.0' id='p4'/>"+
	// "  </peakGroup>"+
	// "  <peakGroup id='p2'>"+
	// "    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
	// "  </peakGroup>"+
	// "</peakList>";
	// Element expectedPeakList = parseValidString(expectedS);
	// AbstractTest.assertEqualsCanonically("peakList", expectedPeakList,
	// newPeakList, true);
	// }

	/** 
	 */
	@Test
	public final void testAddAtomRefsCMLMolecule() {
		CMLMolecule molecule = new CMLMolecule();
		for (int i = 0; i < 4; i++) {
			molecule.addAtom(new CMLAtom("a" + (i + 1)));
		}
		CMLMap map = peakListTool.addAtomRefs(molecule);
		String expectedS = "<map xmlns='http://www.xml-cml.org/schema'>"
				+ "  <link to='p1' from='a1'/>" + "  <link to='p2' from='a2'/>"
				+ "  <link to='p3' from='a3'/>" + "  <link to='p4' from='a4'/>"
				+ "</map>";
		assertEqualsCanonically("atomRefs", parseValidString(expectedS), map,
				true);
		expectedS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peak xValue='3.0' yValue='10.0' id='p1' atomRefs='a1'/>"
				+ "  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"
				+ "  <peak xValue='2.0' yValue='5.0' id='p3' atomRefs='a3'/>"
				+ "  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"
				+ "</peakList>";
		assertEqualsCanonically("atomRefs", parseValidString(expectedS),
				peakList, true);
		peakList = null;
	}

	// FIXME move to tools
	// /**
	// */
	// @Test
	// public final void testGetPeakGroupsFromMorgan() {
	// CMLMap map = peakList.getPeakGroupsFromMorgan(molecule);
	// String expectedS =
	// "	<map xmlns='http://www.xml-cml.org/schema'>"+
	// "		  <link to='p2' fromSet='p2'/>"+
	// "		  <link to='p3p4' fromSet='p3 p4'/>"+
	// "		  <link to='p1' fromSet='p1'/>"+
	// "		</map>";
	// AbstractTest.assertEqualsCanonically("atomRefs",
	// parseValidString(expectedS), map, true);
	// }

	// FIXME move to tools
	// /**
	// */
	// @Test
	// public final void testCreatePeakListGroupedByMorgan() {
	// CMLPeakList newPeakList =
	// peakList.createPeakListGroupedByMorgan(molecule);
	// String expectedS =
	// "<peakList xmlns='http://www.xml-cml.org/schema'>"+
	// "  <peakGroup id='p2'>"+
	// "    <peak xValue='1.0' yValue='7.0' id='p2'/>"+
	// "  </peakGroup>"+
	// "  <peakGroup id='p3p4'>"+
	// "    <peak xValue='2.0' yValue='5.0' id='p3'/>"+
	// "    <peak xValue='2.0' yValue='3.0' id='p4'/>"+
	// "  </peakGroup>"+
	// "  <peakGroup id='p1'>"+
	// "    <peak xValue='3.0' yValue='10.0' id='p1'/>"+
	// "  </peakGroup>"+
	// "</peakList>";
	// AbstractTest.assertEqualsCanonically("atomRefs",
	// parseValidString(expectedS), newPeakList, true);
	// }

	/** 
	 */
	@Test
	public final void testCreateAtom2PeakMap() {
		CMLMolecule molecule = new CMLMolecule();
		for (int i = 0; i < 4; i++) {
			molecule.addAtom(new CMLAtom("a" + (i + 1)));
		}
		CMLMap map = PeakListTool.getOrCreateTool(peakList).createAtom2PeakMap(
				molecule);
		String expectedS = "<map xmlns='http://www.xml-cml.org/schema'>"
				+ "  <link to='p1' from='a1'/>" + "  <link to='p2' from='a2'/>"
				+ "  <link to='p3' from='a3'/>" + "  <link to='p4' from='a4'/>"
				+ "</map>";
		assertEqualsCanonically("atommap", parseValidString(expectedS), map,
				true);
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
		CMLPeakList newPeakList = peakListTool.createPeakGroups(peaks2group);
		String expectedS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peak xValue='3.0' yValue='10.0' id='p3'/>"
				+ "    <peakGroup id='p3'>"
				+ "      <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "      <peak xValue='2.0' yValue='3.0' id='p4'/>"
				+ "    </peakGroup>"
				+ "  <peak xValue='2.0' yValue='5.0' id='p1'/>" + "</peakList>";
		CMLPeakList expectedPeakList = (CMLPeakList) parseValidString(expectedS);
		assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
	}

	/** 
	 */
	@Test
	public final void testCreatePeakGroups() {
		String peaks2GroupS = "<map xmlns='http://www.xml-cml.org/schema'>"
				+ "  <link from='p1' to='p1'/>"
				+ "  <link fromSet='p2 p3' to='p2p3'/>"
				+ "  <link from='p4' to='p3'/>" + "</map>";
		CMLMap peaks2Group = (CMLMap) parseValidString(peaks2GroupS);

		CMLPeakList peakList1 = (CMLPeakList) peakListTool
				.createPeakGroups(peaks2Group);
		String expectedS = "		<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "		  <peak xValue='3.0' yValue='10.0' id='p1'/>"
				+ "		  <peakGroup id='p2p3'>"
				+ "		    <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "		    <peak xValue='2.0' yValue='5.0' id='p3'/>"
				+ "		  </peakGroup>"
				+ "		  <peak xValue='2.0' yValue='3.0' id='p3'/>"
				+ "		</peakList>";
		Element expected = parseValidString(expectedS);
		assertEqualsCanonically("group", expected, peakList1, true);

		// omit peaks through map
		peaks2GroupS = "<map xmlns='http://www.xml-cml.org/schema'>"
				+ "  <link fromSet='p2 p3' to='p2p3'/>"
				+ "  <link from='p4' to='p3'/>" + "</map>";
		peaks2Group = (CMLMap) parseValidString(peaks2GroupS);
		peakList1 = (CMLPeakList) peakListTool.createPeakGroups(peaks2Group);
		expectedS = "		<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "		  <peakGroup id='p2p3'>"
				+ "		    <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "		    <peak xValue='2.0' yValue='5.0' id='p3'/>"
				+ "		  </peakGroup>"
				+ "		  <peak xValue='2.0' yValue='3.0' id='p3'/>"
				+ "		</peakList>";
		expected = parseValidString(expectedS);
		assertEqualsCanonically("group", expected, peakList1, true);
	}

	/**
	 */
	public final void testSelectPeakChildrenByAtomId() {
		String peakList1S = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peak xValue='3.0' yValue='10.0' id='p1' atomRefs='a1'/>"
				+ "  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"
				+ "  <peak xValue='2.0' yValue='5.0' id='p3' atomRefs='a3'/>"
				+ "  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"
				+ "</peakList>";
		CMLPeakList peakList1 = (CMLPeakList) parseValidString(peakList1S);
		String[] atomId = new String[] { "a1", "a3" };
		CMLPeakList newPeakList = PeakListTool.getOrCreateTool(peakList1)
				.createPeakListFromPeakChildrenByAtomId(atomId);
		String expectedS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peak xValue='1.0' yValue='7.0' id='p2' atomRefs='a2'/>"
				+ "  <peak xValue='2.0' yValue='3.0' id='p4' atomRefs='a4'/>"
				+ "</peakList>";
		assertEqualsCanonically("select", parseValidString(expectedS),
				newPeakList, true);
	}

	/**
	 */
	@Test
	public final void testSortByValue() {
		List<CMLPeak> peaks = peakListTool
				.getSortedPeakChildList(CMLPeakList.Type.XVALUE);
		String[] pp = new String[] { "p2", "p4", "p3", "p1" };
		for (int i = 0; i < peaks.size(); i++) {
			Assert
					.assertEquals("peak", pp[i], ((CMLPeak) peaks.get(i))
							.getId());
		}
	}

	/** 
	 */
	@Test
	public final void testGetSortedPeakChildList() {
		List<CMLPeak> peaks = peakListTool.getSortedPeakChildList(Type.XVALUE);
		CMLPeakList newPeakList = new CMLPeakList(peaks);
		String expectedS = "		<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "		  <peak xValue='1.0' yValue='7.0' id='p2'/>"
				+ "		  <peak xValue='2.0' yValue='3.0' id='p4'/>"
				+ "		  <peak xValue='2.0' yValue='5.0' id='p3'/>"
				+ "		  <peak xValue='3.0' yValue='10.0' id='p1'/>"
				+ "		</peakList>";
		Element expected = parseValidString(expectedS);
		assertEqualsCanonically("group", expected, newPeakList, true);
	}

	/**
	 */
	@Test
	public void testRemoveAtomRefsOnPeaksAndGroupsCMLAtomSetBoolean() {
		CMLAtomSet atomSet = new CMLAtomSet();
		atomSet.addAtom(new CMLAtom("a1"));
		atomSet.addAtom(new CMLAtom("a3"));
		atomSet.addAtom(new CMLAtom("a5"));
		String peakListS = "<peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "  <peak id='p1' atomRefs='a1'/>"
				+ "    <peakGroup id='p2p3' atomRefs='a2 a3'>"
				+ "      <peak id='p2' atomRefs='a2'/>"
				+ "      <peak id='p3' atomRefs='a3'/>" + "    </peakGroup>"
				+ "    <peakGroup id='p3'>"
				+ "      <peak id='p5' atomRefs='a5'/>"
				+ "      <peak id='p3' atomRefs='a3'/>" + "    </peakGroup>"
				+ "</peakList>";
		CMLPeakList newPeakList = (CMLPeakList) parseValidString(peakListS);
		PeakListTool.getOrCreateTool(newPeakList)
				.removeAtomRefsOnPeaksAndGroups(atomSet, false);
		String expectedPeakListS = "        <peakList xmlns='http://www.xml-cml.org/schema'>"
				+ "          <peak id='p1'/>"
				+ "          <peakGroup id='p2p3' atomRefs='a2'>"
				+ "            <peak id='p2' atomRefs='a2'/>"
				+ "            <peak id='p3'/>"
				+ "          </peakGroup>"
				+ "          <peakGroup id='p3'>"
				+ "            <peak id='p5'/>"
				+ "            <peak id='p3'/>"
				+ "          </peakGroup>" + "        </peakList>";

		CMLElement expectedPeakList = (CMLPeakList) parseValidString(expectedPeakListS);
		assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);

		newPeakList = (CMLPeakList) parseValidString(peakListS);
		PeakListTool.getOrCreateTool(newPeakList)
				.removeAtomRefsOnPeaksAndGroups(atomSet, true);
		expectedPeakListS = "        <peakList xmlns='http://www.xml-cml.org/schema'> "
				+ "          <peakGroup id='p2p3' atomRefs='a2'>"
				+ "            <peak id='p2' atomRefs='a2'/> "
				+ "          </peakGroup>" + "        </peakList>";
		expectedPeakList = (CMLPeakList) parseValidString(expectedPeakListS);
		assertEqualsCanonically("peakList", expectedPeakList, newPeakList, true);
	}

	// peak structure
	/**
	 * test
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCouplingsFrom() throws Exception {
		CMLMolecule molecule = new CMLMolecule();
		molecule.addAtom(new CMLAtom("a1"));
		molecule.addAtom(new CMLAtom("a2"));
		molecule.addAtom(new CMLAtom("a3"));
		molecule.addAtom(new CMLAtom("a4"));
		// peak1
		CMLElements<CMLPeakStructure> peakStructures1 = fixture
				.getPeakStructures(0);
		List<CMLAtom> couplingsFrom1 = peakStructures1.get(0).getCouplingsFrom(
				molecule);
		Assert.assertEquals("couplings from p1", "a2", couplingsFrom1.get(0)
				.getId());
		// peak 2
		CMLElements<CMLPeakStructure> peakStructures2 = fixture
				.getPeakStructures(1);
		List<CMLAtom> couplingsFrom21 = peakStructures2.get(0)
				.getCouplingsFrom(molecule);
		Assert.assertEquals("coupling 1 from p2", "a1", couplingsFrom21.get(0)
				.getId());
		List<CMLAtom> couplingsFrom22 = peakStructures2.get(1)
				.getCouplingsFrom(molecule);
		Assert.assertEquals("coupling 2 from p2", "a3", couplingsFrom22.get(0)
				.getId());
		Assert.assertEquals("coupling 2 from p2", "a4", couplingsFrom22.get(1)
				.getId());
		// peak3
		CMLElements<CMLPeakStructure> peakStructures3 = fixture
				.getPeakStructures(2);
		List<CMLAtom> couplingsFrom3 = peakStructures3.get(0).getCouplingsFrom(
				molecule);
		Assert.assertEquals("couplings from p3", "a2", couplingsFrom3.get(0)
				.getId());
	}

	/**
	 */
	@Test
	public final void testGetPeakById() {
		CMLPeak peak = peakListTool.getPeakChildById("p3");
		Assert.assertEquals("indexables", "p3", peak.getId());
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
		CMLPeak peak = peakListTool.getPeakChildById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakListTool.getPeakChildById("p5");
		Assert.assertNull("peakById", peak);
		peak = peakAndGroupListTool.getPeakChildById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakAndGroupListTool.getPeakChildById("p2");
		Assert.assertNull("peakById", peak);
	}

	/** 
	 */
	@Test
	public final void testGetPeakDescendantById() {
		CMLPeak peak = peakListTool.getPeakDescendantById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakListTool.getPeakDescendantById("p5");
		Assert.assertNull("peakById", peak);
		peak = peakAndGroupListTool.getPeakDescendantById("p1");
		Assert.assertNotNull("peakById", peak);
		peak = peakAndGroupListTool.getPeakDescendantById("p2");
		Assert.assertNotNull("peakById", peak);
	}

	/** 
	 */
	@Test
	public final void testGetPeakOrGroupChildById() {
		PeakOrGroup peakOrGroup = peakListTool.getPeakOrGroupChildById("p1");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeak);
		peakOrGroup = peakListTool.getPeakDescendantById("p5");
		Assert.assertNull("peakById", peakOrGroup);
		peakOrGroup = peakAndGroupListTool.getPeakOrGroupChildById("p1");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeak);
		peakOrGroup = peakAndGroupListTool.getPeakOrGroupChildById("p2");
		Assert.assertNull("peakById", peakOrGroup);
		peakOrGroup = peakAndGroupListTool.getPeakOrGroupChildById("p2p3");
		Assert.assertNotNull("peakById", peakOrGroup);
		Assert.assertTrue("peakById", peakOrGroup instanceof CMLPeakGroup);
	}

}
