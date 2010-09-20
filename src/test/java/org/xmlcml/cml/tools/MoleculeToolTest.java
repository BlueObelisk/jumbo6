package org.xmlcml.cml.tools;

import static org.xmlcml.cml.element.AbstractTestBase.TOOL_MOLECULES_RESOURCE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.map.Indexable;
import org.xmlcml.cml.map.IndexableByIdList;
import org.xmlcml.cml.test.MoleculeAtomBondFixture;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real3Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tests moleculeTool.
 * 
 * @author pmr
 * 
 */
public class MoleculeToolTest {
	private static Logger LOG = Logger.getLogger(MoleculeToolTest.class);

	Document xmlDocument = null;

	// build xom
	protected final int NATOM = 5;
	protected final int NBOND = 5;
	protected String[] elementTypes = { AS.C.value, AS.N.value, AS.O.value,
			AS.S.value, AS.B.value };
	protected int[] hCounts = { 2, 1, 0, 0, 1 };
	protected CMLMolecule xomMolecule;
	protected CMLAtom[] xomAtom;
	protected CMLBond[] xomBond;

	//
	// read into xom; not a stable molecule... (CH3)[N+](S-)(O)(F)
	// 2 1 3 4 5
	protected String xmlMolS = CMLConstants.S_EMPTY + "  <molecule id='m1'  " + CMLConstants.CML_XMLNS
			+ ">" + "    <atomArray>" + "      <atom id='a1' "
			+ "        elementType='N'" + "        hydrogenCount='0'"
			+ "        formalCharge='1'" + "        spinMultiplicity='1'"
			+ "        occupancy='1.0'" + "        x2='0.' y2='0.'"
			+ "        x3='0.' y3='0.' z3='0.'"
			+ "        xFract='0.1' yFract='0.2' zFract='0.3'" + "      />"
			+ "      <atom id='a2' " + "        elementType='C'"
			+ "        hydrogenCount='3'" + "        x2='1.' y2='1.'"
			+ "        x3='1.' y3='1.' z3='1.'" + "      />"
			+ "      <atom id='a3' " + "        elementType='S'"
			+ "        hydrogenCount='0'" + "        formalCharge='-1'"
			+ "        x2='1.' y2='-1.'" + "        x3='1.' y3='-1.' z3='-1.'"
			+ "      />" + "      <atom id='a4' " + "        elementType='O'"
			+ "        x2='-1.' y2='-1.'" + "        x3='-1.' y3='-1.' z3='1.'"
			+ "      />" + "      <atom id='a5' " + "        elementType='F'"
			+ "        x2='-1.' y2='1.'" + "        x3='-1.' y3='1.' z3='-1.'"
			+ "      />" + "    </atomArray>" + "    <bondArray>"
			+ "      <bond id='b1' atomRefs2='a1 a2' order='1'/>"
			+ "      <bond id='b2' atomRefs2='a1 a3' order='S'/>"
			+ "      <bond id='b3' atomRefs2='a1 a4' order='1'/>"
			+ "      <bond id='b4' atomRefs2='a1 a5' order='1'/>"
			+ "    </bondArray>" + "  </molecule>" + "  ";

	protected CMLMolecule xmlMolecule;
	protected List<CMLAtom> xmlAtoms;
	protected CMLAtom[] xmlAtom;
	protected List<CMLBond> xmlBonds;
	protected int xmlNatoms;
	protected int xmlNbonds;
	
	protected CMLMolecule mol1 = null;
	protected CMLMolecule mol2 = null;
	protected CMLMolecule mol3 = null;
	protected CMLMolecule mol4 = null;
	protected CMLMolecule mol5 = null;
	protected CMLMolecule mol5a = null;
	protected CMLMolecule mol6 = null;
	protected CMLMolecule mol7 = null;
	protected CMLMolecule mol8 = null;
	protected CMLMolecule mol9 = null;
	protected CMLMolecule mol10 = null;
	protected CMLMolecule mol11 = null;

	protected CMLCrystal crystal = null;
	protected CMLCml cmlCryst = null;
	protected CMLMolecule cmlCrystMol = null;
	protected CMLCrystal cmlCrystCryst = null;
	
    protected AbstractTool moleculeTool1;
    protected AbstractTool moleculeTool2;
    protected AbstractTool moleculeTool3;
    protected AbstractTool moleculeTool4;
    protected MoleculeTool moleculeTool5;
    protected MoleculeTool moleculeTool5a;
    protected AbstractTool moleculeTool6;
    protected AbstractTool moleculeTool7;
    protected AbstractTool moleculeTool8;
    protected AbstractTool moleculeTool9;
    protected MoleculeTool moleculeTool10;
    protected AbstractTool moleculeToolXom0;
    protected AbstractSVGTool moleculeToolXml0;
    protected AbstractTool moleculeToolBond0;
    protected AbstractTool moleculeToolXmlBonds;
	protected static Logger logger = Logger.getLogger(MoleculeToolTest.class
			.getName());

    String benzeneS = CMLConstants.S_EMPTY + "<molecule " + CMLConstants.CML_XMLNS + " title='benzene'>"
    + "  <atomArray>"
    + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
    + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
    + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
    + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
    + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
    + "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
    + "  </atomArray>" + "  <bondArray>"
    + "    <bond id='b1' atomRefs2='a1 a2' order='A'/>"
    + "    <bond id='b2' atomRefs2='a2 a3' order='A'/>"
    + "    <bond id='b3' atomRefs2='a3 a4' order='A'/>"
    + "    <bond id='b4' atomRefs2='a4 a5' order='A'/>"
    + "    <bond id='b5' atomRefs2='a5 a6' order='A'/>"
    + "    <bond id='b6' atomRefs2='a6 a1' order='A'/>"
    + "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

    CMLMolecule benzene = null;

    String[] benzeneOrder = new String[] { "2", "1", "2", "1", "2", "1" };

    String methyleneCyclohexeneS = CMLConstants.S_EMPTY + "<molecule " + CMLConstants.CML_XMLNS
            + " title='methyleneCyclohexene'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='2'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b7' atomRefs2='a3 a7' order='1'/>"
            + "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

    CMLMolecule methyleneCyclohexene = null;

    String[] methyleneCyclohexeneOrder = new String[] { "2", "1", "1", "1",
            "1", "1", "2" };

    String methyleneCyclohexadieneS = CMLConstants.S_EMPTY + "<molecule " + CMLConstants.CML_XMLNS
            + " title='methyleneCyclohexadiene'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='2'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b7' atomRefs2='a3 a7' order='1'/>"
            + "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

    static String nitroMethaneS = ""
        + "<molecule "
        + CMLConstants.CML_XMLNS
        + " title='MeN+(-O)O-'>"
        + "  <atomArray>"
        + "    <atom id='a1' elementType='N' hydrogenCount='0' formalCharge='1'/>"
        + "    <atom id='a2' elementType='C' hydrogenCount='3'/>"
        + "    <atom id='a3' elementType='O' hydrogenCount='0' formalCharge='-1'/>"
        + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
        + "  </atomArray>" + "  <bondArray>"
        + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
        + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
        + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
        + "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

    CMLMolecule nitroMethane = null;

    
    String nitroMethane2S = CMLConstants.S_EMPTY + "<molecule " + CMLConstants.CML_XMLNS
    + " title='CH3N(O)O'>" + "  <atomArray>"
    + "    <atom id='a1' elementType='C' hydrogenCount='3'/>"
    + "    <atom id='a2' elementType='N' hydrogenCount='0'/>"
    + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
    + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
    + "  </atomArray>" + "  <bondArray>"
    + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
    + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
    + "    <bond id='b3' atomRefs2='a2 a4' order='1'/>"
    + "  </bondArray>" + "</molecule>" + CMLConstants.S_EMPTY;

    CMLMolecule nitroMethane2 = null;

    protected void makeMol1() {
		String s = "  <molecule id='m1' " + CMLConstants.CML_XMLNS + ">" + "    <atomArray>"
				+ "      <atom id='a1' x3='1.0' y3='2.0' z3='0.0'/>"
				+ "      <atom id='a2' x3='3.0' y3='4.0' z3='0.0'/>"
				+ "      <atom id='a3' x3='2.0' y3='3.0' z3='1.0'/>"
				+ "    </atomArray>" + "  </molecule>";
		mol1 = (CMLMolecule) JumboTestUtils.parseValidString(s);
	}

	protected void makeMol2() {
		mol2 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m2' "
				+ CMLConstants.CML_XMLNS + ">" + "    <atomArray>"
				+ "      <atom id='a11' x3='1.0' y3='2.0' z3='0.0'/>"
				+ "      <atom id='a12' x3='3.0' y3='4.0' z3='0.0'/>"
				+ "      <atom id='a13' x3='2.0' y3='3.0' z3='-1.0'/>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeMol3() {
		mol3 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m3' "
				+ CMLConstants.CML_XMLNS + ">" + "    <atomArray>"
				+ "      <atom id='a21' x3='21.0' y3='2.0' z3='0.0'/>"
				+ "      <atom id='a22' x3='23.0' y3='4.0' z3='0.0'/>"
				+ "      <atom id='a23' x3='22.0' y3='3.0' z3='1.0'/>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeMol4() {
		mol4 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m4' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "    <atomArray>"
				+ "      <atom id='a1' xFract='0.1' yFract='0.2' zFract='0.0'/>"
				+ "      <atom id='a2' xFract='0.3' yFract='0.4' zFract='0.0'/>"
				+ "      <atom id='a3' xFract='0.5' yFract='0.6' zFract='0.7'/>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeCrystal() {
		crystal = (CMLCrystal) JumboTestUtils.parseValidString("  <crystal id='c1' "
				+ CMLConstants.CML_XMLNS + ">"
				+ "    <scalar dictRef='iucr:_cell_length_a'>9.0</scalar>"
				+ "    <scalar dictRef='iucr:_cell_length_b'>10.0</scalar>"
				+ "    <scalar dictRef='iucr:_cell_length_c'>11.0</scalar>"
				+ "    <scalar dictRef='iucr:_cell_angle_alpha'>90.0</scalar>"
				+ "    <scalar dictRef='iucr:_cell_angle_beta'>90.0</scalar>"
				+ "    <scalar dictRef='iucr:_cell_angle_gamma'>90.0</scalar>"
				+ "  </crystal>" + "  ");
	}

	protected void makeMol5() {
		mol5 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m5' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='C' x3='0.0' y3='0.0' z3='0.0'>"
				+ "        <label value='C1'/>"
				+ "      </atom>"
				+ "      <atom id='a2' elementType='N' x3='0.0' y3='1.3' z3='0.0'/>"
				+ "      <atom id='a3' elementType='O' x3='1.0' y3='2.2' z3='0.0' formalCharge='-1'/>"
				+ "      <atom id='a4' elementType='H' x3='0.85' y3='-0.54' z3='0.5'>"
				+ "        <label value='H1a'/>"
				+ "      </atom>"
				+ "      <atom id='a5' elementType='H' x3='-0.85' y3='-0.54' z3='0.5'>"
				+ "        <label value='H1b'/>" + "      </atom>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeMol5a() {
		mol5a = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m5' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='C' x3='0.0' y3='0.0' z3='0.0'/>"
				+ "      <atom id='a2' elementType='N' x3='0.0' y3='1.3' z3='0.0'/>"
				+ "      <atom id='a3' elementType='C' x3='1.2' y3='2.2' z3='0.0'/>"
				+ "      <atom id='a4' elementType='H' x3='0.95' y3='-0.54' z3='0.0'/>"
				+ "      <atom id='a5' elementType='H' x3='-0.95' y3='-0.54' z3='0.0'/>"
				+ "    </atomArray>" + "    <bondArray>"
				+ "      <bond id='a1_a2' atomRefs2='a1 a2'/>"
				+ "      <bond id='a1_a4' atomRefs2='a1 a4'/>"
				+ "      <bond id='a1_a5' atomRefs2='a1 a5'/>"
				+ "      <bond id='a2_a3' atomRefs2='a2 a3'/>"
				+ "    </bondArray>" + "  </molecule>");
	}

	protected void makeMolCryst() {
		cmlCryst = (CMLCml) JumboTestUtils.parseValidString("<cml id='cml1' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <crystal id='c1' >"
				+ "    <scalar dictRef='cml:a'>9.0</scalar>"
				+ "    <scalar dictRef='cml:b'>10.0</scalar>"
				+ "    <scalar dictRef='cml:c'>11.0</scalar>"
				+ "    <scalar dictRef='cml:alpha'>90.0</scalar>"
				+ "    <scalar dictRef='cml:beta'>90.0</scalar>"
				+ "    <scalar dictRef='cml:gamma'>90.0</scalar>"
				+ "  </crystal>"
				+ "  <molecule id='m5' >"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='C' xFract='0.0' yFract='0.0' zFract='0.0'/>"
				+ "      <atom id='a2' elementType='N' xFract='0.0' yFract='0.1' zFract='0.0'/>"
				+ "      <atom id='a3' elementType='C' xFract='0.12' yFract='0.22' zFract='0.0'/>"
				+ "      <atom id='a4' elementType='H' xFract='0.2' yFract='-0.33' zFract='0.1'/>"
				+ "      <atom id='a5' elementType='H' xFract='0.25' yFract='-0.54' zFract='0.0'/>"
				+ "    </atomArray>" + "    <bondArray>"
				+ "      <bond id='a1_a2' atomRefs2='a1 a2'/>"
				+ "      <bond id='a1_a4' atomRefs2='a1 a4'/>"
				+ "      <bond id='a1_a5' atomRefs2='a1 a5'/>"
				+ "      <bond id='a2_a3' atomRefs2='a2 a3'/>"
				+ "    </bondArray>" + "  </molecule>" + "</cml>");
		cmlCrystMol = (CMLMolecule) CMLUtil.getQueryNodes(cmlCryst,
				".//" + CMLMolecule.NS, CMLConstants.CML_XPATH).get(0);
		cmlCrystCryst = (CMLCrystal) CMLUtil.getQueryNodes(cmlCryst,
				".//" + CMLCrystal.NS, CMLConstants.CML_XPATH).get(0);

	}

	protected void makeMol6() {
		mol6 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m6' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='C' x3='0.0' y3='0.0' z3='0.0'/>"
				+ "      <atom id='a2' elementType='N' x3='0.0' y3='1.3' z3='0.0'/>"
				+ "      <atom id='a3' elementType='C' x3='1.2' y3='2.2' z3='0.0'/>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeMol7() {
		mol7 = (CMLMolecule) JumboTestUtils.parseValidString("  <molecule id='m7' "
				+ CMLConstants.CML_XMLNS + ">" + "    <atomArray>"
				+ "      <atom id='a1' elementType='C' x2='0.0' y2='0.0'/>"
				+ "      <atom id='a2' elementType='N' x2='0.0' y2='1.3'/>"
				+ "      <atom id='a3' elementType='C' x2='1.2' y2='2.2'/>"
				+ "    </atomArray>" + "  </molecule>");
	}

	protected void makeMol8() {
		mol8 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule id='m8' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <molecule id='m8a'>"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='C' x2='0.0' y2='0.0'/>"
				+ "      <atom id='a2' elementType='N' x2='0.0' y2='1.3'/>"
				+ "      <atom id='a3' elementType='C' x2='1.2' y2='2.2'/>"
				+ "    </atomArray>"
				+ "  </molecule>"
				+ "  <molecule id='m8b'>"
				+ "    <atomArray>"
				+ "      <atom id='a1' elementType='H' x3='10.0' y3='0.0' z3='0.0'/>"
				+ "      <atom id='a2' elementType='Br' x3='10.0' y3='1.3' z3='0.0'/>"
				+ "      <atom id='a33' elementType='Cl' x3='11.2' y3='2.2' z3='0.0'/>"
				+ "    </atomArray>" + "  </molecule>" + "</molecule>");
	}

	protected void makeMol9() {
		mol9 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule id='m9' " + CMLConstants.CML_XMLNS
				+ ">" + "  <atomArray>"
				+ "    <atom id='a1' elementType='C' x2='0.0' y2='0.0'/>"
				+ "    <atom id='a2' elementType='N' x2='0.0' y2='1.3'/>"
				+ "    <atom id='a3' elementType='C' x2='1.2' y2='2.2'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2'/>"
				+ "    <bond atomRefs2='a2 a3'/>" + "  </bondArray>"
				+ "</molecule>");
	}

	protected void makeMol10() {
		mol10 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='N' hydrogenCount='2'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='2'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='0'/>"
				+ "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
				+ "    <atom id='a5' elementType='O' formalCharge='-1' hydrogenCount='0'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='S'/>"
				+ "    <bond atomRefs2='a2 a3' order='S'/>"
				+ "    <bond atomRefs2='a3 a4' order='D'/>"
				+ "    <bond atomRefs2='a3 a5' order='S'/>" + "  </bondArray>"
				+ "</molecule>" + CMLConstants.S_EMPTY);
	}

	protected void makeMol11() {
		mol11 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule " + CMLConstants.CML_XMLNS + ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='N' hydrogenCount='2'/>"
				+ "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
				+ "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
				+ "    <atom id='a5' elementType='S' hydrogenCount='0'/>"
				+ "    <atom id='a6' elementType='N' hydrogenCount='0'/>"
				+ "    <atom id='a7' elementType='Cl' hydrogenCount='0'/>"
				+ "    <atom id='a8' elementType='Br' hydrogenCount='0'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond atomRefs2='a1 a2' order='S'/>"
				+ "    <bond atomRefs2='a2 a3' order='S'/>"
				+ "    <bond atomRefs2='a2 a4' order='S'/>"
				+ "    <bond atomRefs2='a3 a4' order='S'/>"
				+ "    <bond atomRefs2='a3 a5' order='S'/>"
				+ "    <bond atomRefs2='a5 a6' order='S'/>"
				+ "    <bond atomRefs2='a6 a7' order='S'/>"
				+ "    <bond atomRefs2='a6 a8' order='S'/>" + "  </bondArray>"
				+ "</molecule>" + CMLConstants.S_EMPTY);
	}

	@Before
    public void setUp() throws Exception {
		
		// build reference molecule
		xomMolecule = new CMLMolecule();
		xomMolecule.setId("xom1");
		xomAtom = new CMLAtom[NATOM];
		for (int i = 0; i < NATOM; i++) {
			xomAtom[i] = new CMLAtom();
			xomAtom[i].setId("a" + (i + 1));
			xomMolecule.getOrCreateAtomArray().appendChild(xomAtom[i]);
			xomAtom[i].setElementType(elementTypes[i]);
			xomAtom[i].setX3((double) i);
			xomAtom[i].setY3((double) (i + 1));
			xomAtom[i].setZ3((double) (i + 2));
			xomAtom[i].setX2((double) i * 10);
			xomAtom[i].setY2((double) (i * 10 + 1));
			xomAtom[i].setHydrogenCount(hCounts[i]);
		}
		xomBond = new CMLBond[NBOND];
		for (int j = 0; j < NBOND; j++) {
			// form a cycle...
			// have to set id at this stage. Pehaps we should trap it
			xomBond[j] = new CMLBond(xomAtom[j], xomAtom[(j + 1) % NATOM]);
			xomBond[j].setId("b" + (j + 1));
			CMLBondArray bondArray = xomMolecule.getOrCreateBondArray();
			bondArray.appendChild(xomBond[j]);
			xomBond[j].setOrder((j == 0) ? "2" : "1");
		}

		// read reference moelcule

		try {
			xmlDocument = new CMLBuilder().build(new StringReader(xmlMolS));
		} catch (IOException e) {
			Assert.fail("Should not throw IOException");
		} catch (ParsingException e) {
			e.printStackTrace();
			LOG.error("Parse exception " + e);
			Assert.fail("Should not throw ParsingException " + e.getMessage());
		}
		xmlMolecule = (CMLMolecule) xmlDocument.getRootElement();

		xmlAtoms = xmlMolecule.getAtoms();
		xmlAtom = new CMLAtom[xmlAtoms.size()];
		for (int i = 0; i < xmlAtom.length; i++)
			xmlAtom[i] = (CMLAtom) xmlAtoms.get(i);
		xmlBonds = xmlMolecule.getBonds();

		xmlNatoms = 5;
		xmlNbonds = 4;

		Assert.assertEquals("check atoms in setup", xmlNatoms, xmlAtoms.size());
		Assert.assertEquals("check bonds in setup", xmlNbonds, xmlBonds.size());
        makeMol1();
        moleculeTool1 = MoleculeTool.getOrCreateTool(mol1);
        moleculeToolXom0 = MoleculeTool.getOrCreateTool(xomAtom[0].getMolecule());
        moleculeToolXml0 = MoleculeTool.getOrCreateTool(xmlAtom[0].getMolecule());
        moleculeToolBond0 = MoleculeTool.getOrCreateTool(xmlBonds.get(0).getMolecule());
        benzene = makeMol(benzene, benzeneS);
//        nick = makeMol(nick, nickS);
//        pyrene = makeMol(pyrene, pyreneS);
//        triphene = makeMol(triphene, tripheneS);
//        styrene = makeMol(styrene, styreneS);
        methyleneCyclohexene = makeMol(methyleneCyclohexene,
                methyleneCyclohexeneS);
//        methyleneCyclohexadiene = makeMol(methyleneCyclohexadiene,
//                methyleneCyclohexadieneS);
//        co2 = makeMol(co2, co2S);
//        azulene = makeMol(azulene, azuleneS);
//        conjugated = makeMol(conjugated, conjugatedS);
//        formate1 = makeMol(formate1, formate1S);
//        formate2 = makeMol(formate2, formate2S);
//        formate3 = makeMol(formate3, formate3S);
//        pyridine = makeMol(pyridine, pyridineS);
//        pyridinium = makeMol(pyridinium, pyridiniumS);
//        pyridone4 = makeMol(pyridone4, pyridone4S);
          nitroMethane = makeMol(nitroMethane, nitroMethaneS);
//        nitric = makeMol(nitric, nitricS);
//        oxalate = makeMol(oxalate, oxalateS);
//        benzophenone = makeMol(benzophenone, benzophenoneS);
//        methylammonium = makeMol(methylammonium, methylammoniumS);
//        munchnone = makeMol(munchnone, munchnoneS);
//        pyridinium1 = makeMol(pyridinium1, pyridinium1S);
//        oxalate2 = makeMol(oxalate2, oxalate2S);
//        diMethylIminium = makeMol(diMethylIminium, diMethylIminiumS);
//        nitric2 = makeMol(nitric2, nitric2S);
        nitroMethane2 = makeMol(nitroMethane2, nitroMethane2S);
//        carbonate2 = makeMol(carbonate2, carbonate2S);
//        hydrogenSulfate = makeMol(hydrogenSulfate, hydrogenSulfateS);
//        methaneSulfonate = makeMol(methaneSulfonate, methaneSulfonateS);
//        sprout = makeMol(sprout, sproutS);
    }

    private CMLMolecule makeMol(CMLMolecule mol, String s) {
        if (mol == null) {
            mol = (CMLMolecule) JumboTestUtils.parseValidString(s);
        }
        return mol;
    }

    protected void makeMoleculeTool1() {
        makeMol1();
        moleculeTool1 = MoleculeTool.getOrCreateTool(mol1);
    }

    protected void makeMoleculeTool2() {
        makeMol2();
        moleculeTool2 = MoleculeTool.getOrCreateTool(mol2);
    }

    protected void makeMoleculeTool3() {
        makeMol3();
        moleculeTool3 = MoleculeTool.getOrCreateTool(mol3);
    }

    protected void makeMoleculeTool4() {
        makeMol4();
        moleculeTool4 = MoleculeTool.getOrCreateTool(mol4);
    }

    protected void makeMoleculeTool5() {
        makeMol5();
        moleculeTool5 = MoleculeTool.getOrCreateTool(mol5);
    }

    protected void makeMoleculeTool5a() {
        makeMol5a();
        moleculeTool5a = MoleculeTool.getOrCreateTool(mol5a);
    }

    protected void makeMoleculeTool6() {
        makeMol6();
        moleculeTool6 = MoleculeTool.getOrCreateTool(mol6);
    }

    protected void makeMoleculeTool7() {
        makeMol7();
        moleculeTool7 = MoleculeTool.getOrCreateTool(mol7);
    }

    protected void makeMoleculeTool8() {
        makeMol8();
        moleculeTool8 = MoleculeTool.getOrCreateTool(mol8);
    }

    protected void makeMoleculeTool9() {
        makeMol9();
        moleculeTool9 = MoleculeTool.getOrCreateTool(mol9);
    }

    protected void makeMoleculeTool10() {
        makeMol10();
        moleculeTool10 = MoleculeTool.getOrCreateTool(mol10);
    }

    protected void makeMoleculeToolXomAtom0() {
        moleculeToolXom0 = MoleculeTool.getOrCreateTool(xomAtom[0].getMolecule());
    }

    protected void makeMoleculeToolXmlAtom0() {
        moleculeToolXml0 = MoleculeTool.getOrCreateTool(xmlAtom[0].getMolecule());
    }

    protected void makeMoleculeToolBond0() {
        moleculeToolBond0 = MoleculeTool.getOrCreateTool(xomBond[0].getMolecule());
    }

    protected void makeMoleculeToolXmlBonds() {
        moleculeToolXmlBonds = MoleculeTool.getOrCreateTool(xmlBonds.get(0).getMolecule());
    }

	static Catalog getMoleculeCatalog() throws IOException {
        Catalog catalogTool = null;
        catalogTool = new Catalog(Util
                .getResource(TOOL_MOLECULES_RESOURCE +CMLConstants.U_S + CC.CATALOG_XML));
        return catalogTool;
    }

	/**
	 * main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			Util.println("Args is 0");
			usage();
		} else {
			if (args[0].equalsIgnoreCase("-SVG")) {
				testSVG(args);
			}
		}
	}

	/**
	 * test display.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void testSVG(String[] args) throws Exception {
		if (args.length < 3) {
			Util.println("SVG infile/input_resource outfile");
		} else {
			String infile = args[1];
			String outfile = args[2];
			InputStream is = null;
			try {
				is = Util.getInputStreamFromResource(infile);
			} catch (Exception e) {
				is = new FileInputStream(infile);
			}

			MoleculeDisplayList graphicsManager = new MoleculeDisplayList(
					outfile);
			Document doc = new CMLBuilder().build(is);
			doc = CMLBuilder.ensureCML(doc);
			CMLMolecule molecule = (CMLMolecule) doc.getRootElement();
			graphicsManager.setAndProcess(MoleculeTool
					.getOrCreateTool(molecule));
			graphicsManager.createOrDisplayGraphics();
		}
	}

	static void usage() {
		Util.println("java org.xmlcml.cml.tools.MoleculeToolTest <options>");
		Util.println("... options ...");
		Util.println("-SVG inputfile outputfile <options>");
	}

	MoleculeAtomBondFixture fixture = new MoleculeAtomBondFixture();

	private MoleculeTool makeCompleteMol9() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		CMLAtom atom0 = mol9.getAtom(0);
		CMLAtom atom1 = mol9.getAtom(1);
		CMLAtom atom2 = mol9.getAtom(2);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		moleculeTool.adjustHydrogenCountsToValency(atom0,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		moleculeTool.adjustHydrogenCountsToValency(atom1,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		moleculeTool.adjustHydrogenCountsToValency(atom2,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		return moleculeTool;
	}

	/**
     * test.
     *
     */
    @Test
    public void testAddCoords() {
        // no ligands with coords
        // C-H
        Builder builder = new CMLBuilder();
        Document doc;
        try {
            String t01 = CMLConstants.S_EMPTY
                    + "<molecule id='t01' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "  <atomArray>"
                    + "    <atom id='a1' elementType='C' x3='10' y3='10' z3='10'/>"
                    + "    <atom id='h1' elementType='H'/>"
                    + "  </atomArray>" + "  <bondArray>"
                    + "    <bond atomRefs2='a1 h1' order='1'/>"
                    + "  </bondArray>" + "</molecule>";
            doc = builder.build(new StringReader(t01));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
            		Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)-H
        try {
            // FIXME
            String t02 = "<molecule id='t02' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t02));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
            		Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)(-H)-H
        try {
            String t03 = "<molecule id='t03' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t03));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)(-H)(-H)-H
        try {
            String t04 = "<molecule id='t04' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/><atom id='h4' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/><bond atomRefs2='a1 h4' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t04));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // one ligand
        // C#C-H
        try {
            String t11 = "<molecule id='t11' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='3'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t11));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.LINEAR, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C-O-H
        try {
            String t11a = "<molecule id='t11a' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='O' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8' y3='9' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t11a));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.TETRAHEDRAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C=C(-H)-H
        try {
            String t12 = "<molecule id='t12' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t12));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.TRIGONAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C-C(-H)(-H)-H
        try {
            String t13 = "<molecule id='t13' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t13));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // two ligands
        // C=C-H
        // |
        // C
        try {
            String t21 = "<molecule id='t21' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t21));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.TRIGONAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-N-H
        // |
        // C
        try {
            String t21a = "<molecule id='t21a' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='N' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t21a));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.TETRAHEDRAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C(-H)-H
        // |
        // C
        try {
            String t22 = "<molecule id='t22' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t22));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // three ligands
        // C
        // |
        // C-C-H
        // |
        // C
        try {
            String t31 = "<molecule id='t31' "
                    + CMLConstants.CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='10.8' z3='10.8'/><atom id='a3' elementType='C' x3='10.8' y3='10.8' z3='9.2'/><atom id='a4' elementType='C' x3='10.8' y3='9.2' z3='10.8'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 a4' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t31));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = AtomTool.getOrCreateTool(atoms.get(0)).calculate3DCoordinatesForLigands(
                    Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.addSuffixToAtomIDs(String)'
	 */
	@Test
	public void testAddSuffixToAtomIDs() {
		fixture.makeMol1();
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(fixture.mol1);
		moleculeTool.addSuffixToAtomIDs("FOO");
		Assert.assertEquals("new", 3, fixture.mol1.getAtomCount());
		Assert.assertEquals("new", "a3FOO", fixture.mol1.getAtom(2).getId());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.adjustHydrogenCountsToValency(CMLAtom,
	 * HydrogenControl)'
	 */
	@Test
	public void testAdjustHydrogenCountsToValencyCMLAtomHydrogenControl() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		Assert.assertEquals("new", 3, mol9.getAtomCount());
		Assert.assertEquals("new", 2, mol9.getBondCount());
		Assert.assertEquals("new", "a3", mol9.getAtom(2).getId());
		Assert.assertEquals("new", new String[] { "a2", "a3" }, mol9.getBonds()
				.get(1).getAtomRefs2());
		CMLAtom atom0 = mol9.getAtom(0);
		CMLAtom atom1 = mol9.getAtom(1);
		CMLAtom atom2 = mol9.getAtom(2);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		moleculeTool.adjustHydrogenCountsToValency(atom0,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		Assert.assertEquals("new", 6, mol9.getAtomCount());
		Assert.assertEquals("new", 5, mol9.getBondCount());
		Assert.assertEquals("new", "a1_h2", mol9.getAtom(4).getId());
		Assert.assertEquals("new", new String[] { "a1", "a1_h2" }, mol9
				.getBonds().get(3).getAtomRefs2());
		moleculeTool.adjustHydrogenCountsToValency(atom1,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		moleculeTool.adjustHydrogenCountsToValency(atom2,
				HydrogenControl.REPLACE_HYDROGEN_COUNT);
		Assert.assertEquals("new", 10, mol9.getAtomCount());
		Assert.assertEquals("new", 9, mol9.getBondCount());
		Assert.assertEquals("new", "a3_h3", mol9.getAtom(9).getId());
		Assert.assertEquals("new", new String[] { "a3", "a3_h3" }, mol9
				.getBonds().get(8).getAtomRefs2());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.adjustHydrogenCountsToValency(Hydrogen
	 * C o n t r o l ) '
	 */
	@Test
	public void testAdjustHydrogenCountsToValencyHydrogenControl() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		Assert.assertEquals("new", 3, mol9.getAtomCount());
		Assert.assertEquals("new", 2, mol9.getBondCount());
		Assert.assertEquals("new", "a3", mol9.getAtom(2).getId());
		Assert.assertEquals("new", new String[] { "a2", "a3" }, mol9.getBonds()
				.get(1).getAtomRefs2());
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		moleculeTool
				.adjustHydrogenCountsToValency(HydrogenControl.REPLACE_HYDROGEN_COUNT);
		Assert.assertEquals("new", 10, mol9.getAtomCount());
		Assert.assertEquals("new", 9, mol9.getBondCount());
		Assert.assertEquals("new", "a3_h3", mol9.getAtom(9).getId());
		Assert.assertEquals("new", new String[] { "a3", "a3_h3" }, mol9
				.getBonds().get(8).getAtomRefs2());
	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.MoleculeTool.appendToId(CMLAtom,
	 * String)'
	 */
	@Test
	public void testAppendToId() {
		fixture.makeMol9();
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(fixture.mol9);
		CMLAtom atom = fixture.mol9.getAtom(1);
		Assert.assertEquals("id", "a2", atom.getId());
		moleculeTool.appendToId(atom, "XXX");
		Assert.assertEquals("id", "a2XXX", atom.getId());

	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateAndAddFormula(HydrogenContro
	 * l ) '
	 */
	@Test
	public void testCalculateAndAddFormula() {
		fixture.makeMol5();
		MoleculeTool moleculeTool5 = MoleculeTool.getOrCreateTool(fixture.mol5);
		moleculeTool5
				.calculateAndAddFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		CMLElements<CMLFormula> formulaList = fixture.mol5.getFormulaElements();
		Assert.assertEquals("formula", 1, formulaList.size());
		CMLFormula formula = formulaList.get(0);
		// <formula formalCharge="-1" concise="C 1 H 2 N 1 O 1 -1"
		// xmlns="http://www.xml-cml.org/schema">
		// <atomArray elementType="C H N O" count="1.0 2.0 1.0 1.0"/>
		// </formula>
		Assert.assertEquals("formula", "C 1 H 2 N 1 O 1 -1", formula
				.getConcise());
		CMLAtomArray atomArray = (CMLAtomArray) formula.getChildCMLElements(
				CMLAtomArray.TAG).get(0);
		Assert.assertEquals("formula", new String[] { AS.C.value, AS.H.value,
				AS.N.value, AS.O.value }, atomArray.getElementType());
		JumboTestUtils.assertEquals("formula", new double[] { 1.0, 2.0, 1.0, 1.0 }, atomArray
				.getCount(), 0.000001);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateCentroid3(CoordinateType)'
	 */
	@Test
	public void testCalculateCentroid3() {
		fixture.makeMol5a();
		MoleculeTool moleculeTool5a = MoleculeTool
				.getOrCreateTool(fixture.mol5a);
		Point3 p = moleculeTool5a.calculateCentroid3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("p", new double[] { 0.24, 0.484, 0.0 }, p.getArray(),
				0.00001);
		fixture.makeMol7();
		MoleculeTool moleculeTool7 = MoleculeTool.getOrCreateTool(fixture.mol7);
		p = moleculeTool7.calculateCentroid3(CoordinateType.CARTESIAN);
		Assert.assertNull("centroid null", p);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.calculateFormula(HydrogenControl)'
	 */
	@Test
	public void testCalculateFormula() {
		fixture.makeMol5();
		CMLFormula formula = MoleculeTool.getOrCreateTool(fixture.mol5)
				.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
		// <formula formalCharge="-1" concise="C 1 H 2 N 1 O 1 -1"
		// xmlns="http://www.xml-cml.org/schema">
		// <atomArray elementType="C H N O" count="1.0 2.0 1.0 1.0"/>
		// </formula>
		Assert.assertEquals("formula", "C 1 H 2 N 1 O 1 -1", formula
				.getConcise());
		CMLAtomArray atomArray = (CMLAtomArray) formula.getChildCMLElements(
				CMLAtomArray.TAG).get(0);
		Assert.assertEquals("formula", new String[] { AS.C.value, AS.H.value,
				AS.N.value, AS.O.value }, atomArray.getElementType());
		JumboTestUtils.assertEquals("formula", new double[] { 1.0, 2.0, 1.0, 1.0 }, atomArray
				.getCount(), 0.000001);

	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#calculateRange3(org.xmlcml.cml.base.CMLElement.CoordinateType)}
	 * .
	 */
	@Test
	public final void testCalculateRange3() {
		fixture.makeMol5a();
		Real3Range r3 = MoleculeTool.getOrCreateTool(fixture.mol5a)
				.calculateRange3(CoordinateType.CARTESIAN);
		Assert.assertNotNull("range", r3);
		JumboTestUtils.assertEquals("r3", new Real3Range(new RealRange(-0.95, 1.2),
				new RealRange(-0.54, 2.2), new RealRange(0.0, 0.0)), r3,
				0.000001);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#CMLMolecule(org.xmlcml.cml.element.CMLAtomSet)}
	 * .
	 */
	@Test
	public final void testCMLMoleculeCMLAtomSet() {
		fixture.makeMol1();
		CMLAtomSet atomSet = new CMLAtomSet(fixture.mol1, new String[] { "a1",
				"a3" });
		Assert.assertEquals("atomSet", 2, atomSet.size());
		CMLMolecule mol = MoleculeTool.createMolecule(atomSet);
		Assert.assertEquals("atom count", 2, mol.getAtomCount());
		JumboTestUtils.assertEqualsCanonically("atom", fixture.mol1.getAtom(0), mol.getAtom(0));
		JumboTestUtils.assertEqualsCanonically("atom", fixture.mol1.getAtom(2), mol.getAtom(1));
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#CMLMolecule(org.xmlcml.cml.element.CMLAtomSet, org.xmlcml.cml.element.CMLBondSet)}
	 * .
	 */
	@Test
	public final void testCMLMoleculeCMLAtomSetCMLBondSet() {
		fixture.makeMol5a();
		CMLMolecule mol5a = fixture.mol5a;
		CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(mol5a).getAtomSet();
		CMLBondSet bondSet = null;
		bondSet = new CMLBondSet(mol5a.getBonds());
		CMLMolecule mol = MoleculeTool.createMolecule(atomSet, bondSet);
		Assert.assertEquals("atoms", 5, mol.getAtomCount());
		JumboTestUtils.assertEqualsCanonically("atom", mol5a.getAtom(2), mol.getAtom(2));
		Assert.assertEquals("bonds", 4, mol.getBondCount());
		JumboTestUtils.assertEqualsCanonically("bond", mol5a.getBonds().get(2), mol.getBonds()
				.get(2));

		CMLBond bond = bondSet.getBonds().get(0);
		bondSet.removeBond(bond);
		mol = MoleculeTool.createMolecule(atomSet, bondSet);
		Assert.assertEquals("atoms", 5, mol.getAtomCount());
		JumboTestUtils.assertEqualsCanonically("atom", mol5a.getAtom(2), mol.getAtom(2));
		Assert.assertEquals("bonds", 3, mol.getBondCount());
		JumboTestUtils.assertEqualsCanonically("bond", mol5a.getBonds().get(2), mol.getBonds()
				.get(1));
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.contractExplicitHydrogens(CMLAtom,
	 * HydrogenControl)'
	 */
	@Test
	public void testContractExplicitHydrogensCMLAtomHydrogenControl() {
		AbstractSVGTool moleculeTool = makeCompleteMol9();
		CMLMolecule mol9 = fixture.mol9;
		CMLAtom atom0 = mol9.getAtom(0);
		Assert.assertEquals("before", 10, mol9.getAtomCount());
		Assert.assertEquals("before", 9, mol9.getBondCount());
		Assert.assertNotNull("before", mol9.getAtom(0)
				.getHydrogenCountAttribute());
		Assert.assertNotNull("before", mol9.getAtom(1)
				.getHydrogenCountAttribute());
		Assert.assertNotNull("before", mol9.getAtom(2)
				.getHydrogenCountAttribute());
		Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
		Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
		Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());

		AtomTool atomTool0 = AtomTool.getOrCreateTool(atom0);
		atomTool0.contractExplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS);
		Assert.assertEquals("before", 7, mol9.getAtomCount());
		Assert.assertEquals("before", 6, mol9.getBondCount());
		Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
		Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
		Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.contractExplicitHydrogens(HydrogenCont
	 * r o l ) '
	 */
	@Test
	public void testContractExplicitHydrogensHydrogenControl() {
		MoleculeTool moleculeTool = makeCompleteMol9();
		CMLMolecule mol9 = fixture.mol9;
		Assert.assertEquals("before", 10, mol9.getAtomCount());
		Assert.assertEquals("before", 9, mol9.getBondCount());
		Assert.assertNotNull("before", mol9.getAtom(0)
				.getHydrogenCountAttribute());
		Assert.assertNotNull("before", mol9.getAtom(1)
				.getHydrogenCountAttribute());
		Assert.assertNotNull("before", mol9.getAtom(2)
				.getHydrogenCountAttribute());
		Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
		Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
		Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());

		moleculeTool.contractExplicitHydrogens(
				HydrogenControl.USE_EXPLICIT_HYDROGENS, true);
		Assert.assertEquals("before", 3, mol9.getAtomCount());
		Assert.assertEquals("before", 2, mol9.getBondCount());
		Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
		Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
		Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());

	}

	/**
	 * copies attributes on bonds and atoms to another molecule. for each
	 * atom/bond in this.molecule finds Id and hence corresponding atom/bond in
	 * 'to'. Copies all attributes from that atom to to.atom/@* If corresponding
	 * atom does not exist, throws exception. If target attribute exists throws
	 * exception
	 * 
	 * @exception RuntimeException
	 *                ids in molecules do not correspond or attributes are
	 *                already present
	 */
	@Test
	public void testCopyAtomAndBondAttributesById() {
		CMLMolecule from = new CMLMolecule();
		from.setId("from");
		CMLAtom atom0 = new CMLAtom();
		atom0.setElementType(AS.C.value);
		atom0.setId("a0");
		from.addAtom(atom0);
		CMLAtom atom1 = new CMLAtom();
		atom1.setElementType(AS.O.value);
		atom1.setId("a1");
		from.addAtom(atom1);
		CMLBond bond01 = new CMLBond(atom0, atom1);
		bond01.setId("b01");
		from.addBond(bond01);

		CMLMolecule to = (CMLMolecule) from.copy();
		to.setId("to");

		atom0.setXY2(new Real2(1., 2.));
		atom1.setFormalCharge(1);
		bond01.setOrder(CMLBond.DOUBLE);

		MoleculeTool fromTool = MoleculeTool.getOrCreateTool(from);
		boolean permitOverwrite = true;
		fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);

		to.setId("from"); // to allow comparison
		JumboTestUtils.assertEqualsCanonically("compare mols", from, to);

		permitOverwrite = false;
		try {
			fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);
			Assert.fail("Should fail on overwrite");
		} catch (RuntimeException e) {
			Assert.assertTrue("cannot overwrite", e.getMessage().startsWith(
					"cannot overwrite attribute:"));
		}

		// mimic atomId mismatch
		atom0.resetId("resetId");
		try {
			fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);
			Assert.fail("Should fail on atom mismatch");
		} catch (RuntimeException e) {
			Assert.assertEquals("atom mismatch",
					"Cannot find target atom: resetId", e.getMessage());
		}

	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.createCartesiansFromFractionals(RealS
	 * q u a r e M a t r i x ) '
	 */
	@Test
	public void testCreateCartesiansFromFractionals() {
		fixture.makeMol4();
		fixture.makeCrystal();
		MoleculeTool.getOrCreateTool(fixture.mol4)
				.createCartesiansFromFractionals(fixture.crystal);
		Assert.assertEquals("fractionals", 3, fixture.mol4.getAtomCount());
		JumboTestUtils.assertEquals("fractionals", new double[] { 0.5, 0.6, 0.7 },
				fixture.mol4.getAtom(2).getXYZFract(), CC.EPS);
		JumboTestUtils.assertEquals("cartesians", new double[] { 4.5, 6.0, 7.7 }, fixture.mol4
				.getAtom(2).getXYZ3(), CC.EPS);
	}

	/**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#createCartesiansFromFractionals(org.xmlcml.euclid.Transform3)}
	 * .
	 */
	@Test
	public final void testCreateCartesiansFromFractionalsTransform3() {
		fixture.makeMolCryst();
		Transform3 t3 = fixture.cmlCrystCryst.getOrthogonalizationTransform();
		CMLMolecule cmlCrystMol = fixture.cmlCrystMol;
		Assert.assertFalse("no 3d coords", cmlCrystMol
				.hasCoordinates(CoordinateType.CARTESIAN));
		Assert.assertTrue("fractional coords", cmlCrystMol
				.hasCoordinates(CoordinateType.FRACTIONAL));
		MoleculeTool.getOrCreateTool(cmlCrystMol)
				.createCartesiansFromFractionals(t3);
		Assert.assertTrue("3d coords", cmlCrystMol
				.hasCoordinates(CoordinateType.CARTESIAN));
		Assert.assertTrue("fractional coords", cmlCrystMol
				.hasCoordinates(CoordinateType.FRACTIONAL));
		Point3 xyz = cmlCrystMol.getAtom(2).getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("point", new double[] { 1.08, 2.2, 0.0 }, xyz,
				0.0000000000001);
	}

	@Test
	public void testCreateGraphicsElement1() throws Exception {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom1 = new CMLAtom("a1", ChemicalElement
				.getChemicalElement("C"));
		atom1.setX2(50.0);
		atom1.setY2(50.0);
		molecule.addAtom(atom1);
		CMLAtom atom2 = new CMLAtom("a2", ChemicalElement
				.getChemicalElement("O"));
		atom2.setX2(100.0);
		atom2.setY2(100.0);
		molecule.addAtom(atom2);
		CMLBond bond = new CMLBond(atom1, atom2);
		molecule.addBond(bond);
		bond.setOrder(CMLBond.DOUBLE);
		CMLAtom atom3 = new CMLAtom("a3", ChemicalElement.AS.N);
		atom3.setX2(5.0);
		atom3.setY2(50.0);
		molecule.addAtom(atom3);
		bond = new CMLBond(atom3, atom1);
		molecule.addBond(bond);
		AbstractSVGTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	SVGSVG svgsvg = createSvgSvg(moleculeTool);
        Element ref = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/molecule1.svg");
//        CMLUtil.debug(svgsvg, "SSVG");
        JumboTestUtils.assertEqualsIncludingFloat("svg", ref, svgsvg, true, 0.0000000001);
	}
	
	@Test
	@Ignore
	public void testCreateGraphicsElement2() throws Exception {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom1 = addAtom(molecule, "a1", ChemicalElement.AS.N, new Real2(50.0, 50.0));
		CMLAtom atom2 = addAtom(molecule, "a2", ChemicalElement.AS.C, new Real2(150.0, 50.0));
		AtomTool.getOrCreateTool(atom2).getAtomDisplay().setDisplayCarbons(false);
		CMLBond bond12 = new CMLBond(atom1, atom2);
		molecule.addBond(bond12);
		CMLAtom atom3 = addAtom(molecule, "a3", ChemicalElement.AS.O, new Real2(250.0, 150.0));
		CMLBond bond23 = new CMLBond(atom2, atom3);
		molecule.addBond(bond23);
		bond23.setOrder(CMLBond.DOUBLE);
		AbstractSVGTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	SVGSVG svgsvg = createSvgSvg(moleculeTool);
        Element ref = JumboTestUtils.parseValidFile("org/xmlcml/cml/tools/molecule2.svg");
        JumboTestUtils.assertEqualsIncludingFloat("svg", ref, svgsvg, true, 0.0000000001);
	}

	private CMLAtom addAtom(CMLMolecule molecule, String id, ChemicalElement.AS as, Real2 xy2) {
		CMLAtom atom1 = new CMLAtom(id, as);
		atom1.setXY2(xy2);
		molecule.addAtom(atom1);
		return atom1;
	}
	

	private SVGSVG createSvgSvg(AbstractSVGTool moleculeTool) {
		CMLDrawable drawable = new MoleculeDisplayList();
    	SVGG svgg = (SVGG) moleculeTool.createGraphicsElement(drawable);
    	SVGSVG svgsvg = SVGSVG.wrapAsSVG(svgg);
    	svgg.translate(new Real2(100., -200.));
		return svgsvg;
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.createMolecule(CMLMolecule, String[])'
	 */
	@Test
	public void testCreateMolecule() {
		fixture.makeMol1();
		CMLMolecule mol = MoleculeTool.createMolecule(fixture.mol1,
				new String[] { "a1", "a3" });
		Assert.assertEquals("new", 2, mol.getAtomCount());
		Assert.assertEquals("new", "a3", mol.getAtom(1).getId());

	}

	/**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.createValenceAngles(boolean,
     * boolean)'
     */
    @Test
    public void testCreateValenceAngles() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        List<CMLAtom> atoms = mol5.getAtoms();
        CMLAtom atom0 = atoms.get(0);
        List<CMLAtom> ligandList = atom0.getLigandAtoms();
        Assert.assertEquals("ligand list", 3, ligandList.size());
        new GeometryTool(mol5).createValenceAngles(true, true);
        List<CMLAngle> angles = moleculeTool5.getAngleElements();
        Assert.assertEquals("angles", 4, angles.size());
        CMLAngle angle = angles.get(0);
        Assert.assertEquals("angle 0 atoms", new String[] { "a2", "a1", "a4" },
                angle.getAtomRefs3());
        Assert.assertEquals("angle 0 value", 118.704, angle.getXMLContent(),
                0.001);
        angle = angles.get(3);
        Assert.assertEquals("angle 3 atoms", new String[] { "a1", "a2", "a3" },
                angle.getAtomRefs3());
        Assert.assertEquals("angle 3 value", 131.987, angle.getXMLContent(),
                0.001);
    }

	/**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.createValenceLengths(boolean,
     * boolean)'
     */
    @Test
    public void testCreateValenceLengths() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        List<CMLAtom> ligandList = mol5.getAtoms().get(0).getLigandAtoms();
        Assert.assertEquals("ligand list", 3, ligandList.size());
        new GeometryTool(mol5).createValenceLengths(true, true);
        List<CMLLength> lengths = moleculeTool5.getLengthElements();
        Assert.assertEquals("lengths", 4, lengths.size());
        CMLLength length = lengths.get(0);
        Assert.assertEquals("length 0 atoms", new String[] { "a2", "a1" },
                length.getAtomRefs2());
        Assert.assertEquals("length 0 value", 1.3, length.getXMLContent(),
                0.001);
    }

	/**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.createValenceTorsions(boolean,
     * boolean)'
     */
    @Test
    public void testCreateValenceTorsions() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        List<CMLAtom> ligandList = mol5.getAtoms().get(0).getLigandAtoms();
        Assert.assertEquals("ligand list", 3, ligandList.size());
        new GeometryTool(mol5).createValenceTorsions(true, true);
        List<CMLTorsion> torsions = moleculeTool5.getTorsionElements();
        Assert.assertEquals("torsions", 2, torsions.size());
        CMLTorsion torsion = torsions.get(0);
        Assert.assertEquals("torsion 0 atoms", new String[] { "a4", "a1", "a2",
                "a3" }, torsion.getAtomRefs4());
        Assert.assertEquals("torsion 0 value", 30.465, torsion.getXMLContent(),
                0.001);
        torsion = torsions.get(1);
        Assert.assertEquals("torsion 1 atoms", new String[] { "a5", "a1", "a2",
                "a3" }, torsion.getAtomRefs4());
        Assert.assertEquals("torsion 1 value", 149.534,
                torsion.getXMLContent(), 0.001);
    }

	/**
     * Test method for 'org.xmlcml.cml.element.CMLAtom.deleteHydrogen()'
     */
    @Test
    public void testDeleteHydrogen() {
        CMLAtom atom = benzene.getAtom(0);
        Assert.assertEquals("before delete H", 1, atom.getHydrogenCount());
        MoleculeTool.getOrCreateTool(benzene).deleteHydrogen(atom);
        Assert.assertEquals("after delete H", 0, atom.getHydrogenCount());
    }

	/**
     * test distributeMolecularChargeToN4.
     */
    @Test
    public void testDistributeMolecularChargeToN4() {
        /*--
         dmcn4(methylammonium);
         dmcn4(pyridinium1);
         --*/
    }

	/**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.expandImplicitHydrogens(String)'
     */
    @Test
    public void testExpandImplicitHydrogens() {
        makeMoleculeTool10();
        CMLAtom atom0 = mol10.getAtom(0);
        AtomTool.getOrCreateTool(atom0).expandImplicitHydrogens(HydrogenControl.NO_EXPLICIT_HYDROGENS);
        Assert.assertEquals("after addition", 7, mol10.getAtomCount());
        CMLAtom atom5 = mol10.getAtom(5);
        Assert.assertEquals("added H", "a1_h1", atom5.getId());
        CMLAtom atom6 = mol10.getAtom(6);
        Assert.assertEquals("added H", "a1_h2", atom6.getId());
        CMLBond bond4 = mol10.getBonds().get(4);
        Assert.assertEquals("after addition", 6, mol10.getBondCount());
        Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h1" },
                bond4.getAtomRefs2());
        CMLBond bond5 = mol10.getBonds().get(5);
        Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h2" },
                bond5.getAtomRefs2());
        // do the whole molecule
        makeMoleculeTool10();
        moleculeTool10.expandImplicitHydrogens(HydrogenControl.USE_EXPLICIT_HYDROGENS);
        Assert.assertEquals("after addition", 9, mol10.getAtomCount());
        atom5 = mol10.getAtom(5);
        Assert.assertEquals("added H", "a1_h1", atom5.getId());
        atom6 = mol10.getAtom(6);
        Assert.assertEquals("added H", "a1_h2", atom6.getId());
        CMLAtom atom7 = mol10.getAtom(7);
        Assert.assertEquals("added H", "a2_h1", atom7.getId());
        CMLAtom atom8 = mol10.getAtom(8);
        Assert.assertEquals("added H", "a2_h2", atom8.getId());
        Assert.assertEquals("after addition", 8, mol10.getBondCount());
        bond4 = mol10.getBonds().get(4);
        Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h1" },
                bond4.getAtomRefs2());
        bond5 = mol10.getBonds().get(5);
        Assert.assertEquals("added bond to H", new String[] { "a1", "a1_h2" },
                bond5.getAtomRefs2());
        CMLBond bond6 = mol10.getBonds().get(6);
        Assert.assertEquals("added bond to H", new String[] { "a2", "a2_h1" },
                bond6.getAtomRefs2());
        CMLBond bond7 = mol10.getBonds().get(7);
        Assert.assertEquals("added bond to H", new String[] { "a2", "a2_h2" },
                bond7.getAtomRefs2());
    }


    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.expandImplicitHydrogens(CMLAtom,
	 * HydrogenControl)'
	 */
	@Test
	public void testExpandImplicitHydrogensCMLAtomHydrogenControl() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		Assert.assertEquals("before", 3, mol9.getAtomCount());
		Assert.assertEquals("before", 2, mol9.getBondCount());
		moleculeTool
				.adjustHydrogenCountsToValency(HydrogenControl.NO_EXPLICIT_HYDROGENS);
		moleculeTool
				.expandImplicitHydrogens(HydrogenControl.NO_EXPLICIT_HYDROGENS);
		Assert.assertEquals("after", 10, mol9.getAtomCount());
		Assert.assertEquals("after", 9, mol9.getBondCount());
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.expandImplicitHydrogens(HydrogenContro
	 * l ) '
	 */
	@Test
	public void testExpandImplicitHydrogensHydrogenControl() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		mol9.getAtom(0).setHydrogenCount(3);
		mol9.getAtom(1).setHydrogenCount(1);
		mol9.getAtom(2).setHydrogenCount(3);
		Assert.assertEquals("before", 3, mol9.getAtomCount());
		Assert.assertEquals("before", 2, mol9.getBondCount());
		moleculeTool
				.expandImplicitHydrogens(HydrogenControl.NO_EXPLICIT_HYDROGENS);
		Assert.assertEquals("before", 10, mol9.getAtomCount());
		Assert.assertEquals("before", 9, mol9.getBondCount());

	}

	/**
	 * Test method for 'org.xmlcml.cml.tools.MoleculeTool.generateBondIds()'
	 */
	@Test
	public void testGenerateBondIds() {
		fixture.makeMol9();
		CMLMolecule mol9 = fixture.mol9;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol9);
		Assert.assertNull("no id", mol9.getBonds().get(0).getId());
		moleculeTool.generateBondIds();
		Assert.assertEquals("generated id", "a1_a2", mol9.getBonds().get(0)
				.getId());
	}

	/**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getAngle(CMLAtom,
	 * CMLAtom, CMLAtom)'
	 */
	@Test
	public void testGetAngle() {
		String s = "" + "<molecule xmlns='" + CMLConstants.CML_NS + "'>" + "  <atomArray>"
				+ "    <atom id='a1'/>" + "    <atom id='a2'/>"
				+ "    <atom id='a3'/>" + "    <atom id='a4'/>"
				+ "  </atomArray>" + "  <bondArray>"
				+ "    <bond id='b1' atomRefs2='a1 a2'/>"
				+ "    <bond id='b2' atomRefs2='a2 a3'/>"
				+ "    <bond id='b3' atomRefs2='a3 a4'/>" + "  </bondArray>"
				+ "  <angle atomRefs3='a1 a2 a3'>123</angle>"
				+ "  <angle atomRefs3='a3 a4 a5'>99</angle>" + "</molecule>"
				+ "";
		CMLMolecule mol = (CMLMolecule) JumboTestUtils.parseValidString(s);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
		CMLAtom a1 = mol.getAtom(0);
		CMLAtom a2 = mol.getAtom(1);
		CMLAtom a3 = mol.getAtom(2);
		CMLAtom a4 = mol.getAtom(3);
		CMLAngle angle = moleculeTool.getAngle(a1, a2, a3);
		Assert.assertNotNull("angle not null", angle);
		Assert.assertEquals("angle", 123., angle.getXMLContent(), 0.00001);
		angle = moleculeTool.getAngle(a1, a2, a4);
		Assert.assertNull("angle null", angle);
	}
	
    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getAtomSet(CMLBondSet)'
	 */
	@Test
	public void testGetAtomSet() {
		fixture.makeMol5a();
		CMLMolecule mol5a = fixture.mol5a;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol5a);
		// CMLAtomSet atomSet = mol5a.getAtomSet();
		CMLBondSet bondSet = new CMLBondSet(mol5a);
		Assert.assertEquals("before", 5, mol5a.getAtomCount());
		Assert.assertEquals("before", 4, mol5a.getBondCount());
		bondSet.removeBond(mol5a.getBonds().get(1));
		CMLAtomSet newAtomSet = moleculeTool.getAtomSet(bondSet);
		Assert.assertEquals("before", new String[] { "a1", "a2", "a5", "a3" },
				newAtomSet.getXMLContent());
	}

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.getAverageBondDistance(int)'
     */
    @Test
    public void testGetAverageBondDistance() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (RuntimeException e) {
            Assert.fail("test bug " + e);
        }
        double length = moleculeTool5
                .getAverageBondLength(CoordinateType.CARTESIAN);
        Assert.assertEquals("average length", 1.2235, length, .0001);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getAverageBondLength(CoordinateType)'
     */
    @Test
    public void testGetAverageBondLength() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (RuntimeException e) {
            Assert.fail("test bug " + e);
        }
        double length = moleculeTool5
                .getAverageBondLength(CoordinateType.CARTESIAN);
        Assert.assertEquals("average length", 1.2235, length, .0001);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLAtom.getBondOrderSum()'
     */
    @Test
    public void testGetBondOrderSum() {
        // makeMoleculeToolXml0();
        String el = xmlAtom[0].getElementType();
        Assert.assertEquals("element type", AS.N.value, el);
		AtomTool atomTool = AtomTool.getOrCreateTool(xmlAtom[0]);
        int bes = atomTool.getBondOrderSum();
        el = xmlAtom[1].getElementType();
        Assert.assertEquals("element type", AS.C.value, el);
		atomTool = AtomTool.getOrCreateTool(xmlAtom[1]);
        bes = atomTool.getBondOrderSum();
        Assert.assertEquals("bond order sum", 4, bes);
        benzene.setBondOrders(CMLBond.SINGLE);
        atomTool = AtomTool.getOrCreateTool(benzene.getAtom(0));
        int bes1 = atomTool.getBondOrderSum();
        Assert.assertEquals("bond order sum", 3, bes1);
        methyleneCyclohexene.setBondOrders(CMLBond.SINGLE);
        atomTool = AtomTool.getOrCreateTool(methyleneCyclohexene
                .getAtom(0));
        bes1 = atomTool.getBondOrderSum();
        Assert.assertEquals("bond order sum", 3, bes1);
        atomTool = AtomTool.getOrCreateTool(methyleneCyclohexene
                .getAtom(0));
        bes1 = atomTool.getBondOrderSum();
        Assert.assertEquals("bond order sum", 3, bes1);
    }

    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getBondSet(CMLAtomSet)'
	 */
	@Test
	public void testGetBondSet() {
		fixture.makeMol9();
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(fixture.mol9);
		moleculeTool.generateBondIds();
		CMLAtomSet atomSet = new CMLAtomSet(fixture.mol9);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		Assert.assertEquals("bonds", new String[] { "a1_a2", "a2_a3" }, bondSet
				.getXMLContent());
		atomSet.removeAtomById("a3");
		bondSet = moleculeTool.getBondSet(atomSet);
		Assert.assertEquals("bonds", new String[] { "a1_a2" }, bondSet
				.getXMLContent());
	}

    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getCentroid3D()'
	 */
	@Test
	public void testGetCentroid3() {
		fixture.makeMol1();
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(fixture.mol1);
		Point3 centroid = moleculeTool1
				.calculateCentroid3(CoordinateType.CARTESIAN);
		Assert.assertNotNull("centroid 1", centroid);
		JumboTestUtils.assertEquals("centroid 1", new double[] { 2., 3., 0.33333 }, centroid,
				.0001);
		fixture.makeMol7();
		MoleculeTool moleculeTool7 = MoleculeTool.getOrCreateTool(fixture.mol7);
		centroid = moleculeTool7.calculateCentroid3(CoordinateType.CARTESIAN);
		Assert.assertNull("centroid 7", centroid);
	}


    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.getCoordinates3(CoordinateType)'
	 */
	@Test
	public void testGetCoordinates3() {
		fixture.makeMol5a();
		MoleculeTool moleculeTool5a = MoleculeTool
				.getOrCreateTool(fixture.mol5a);
		Point3Vector p3v = moleculeTool5a
				.getCoordinates3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("p", new double[] { 0.0, 0.0, 0.0, 0.0, 1.3, 0.0, 1.2,
				2.2, 0.0, 0.95, -0.54, 0.0, -0.95, -0.54, 0.0, }, p3v
				.getArray(), 0.00001);
		fixture.makeMol7();
		MoleculeTool moleculeTool7 = MoleculeTool.getOrCreateTool(fixture.mol7);
		p3v = moleculeTool7.getCoordinates3(CoordinateType.CARTESIAN);
		Assert.assertNull("centroid null", p3v);
	}
    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLAtom.getDoubleBondEquivalents()'
	 */
	@Test
	public void testGetDoubleBondEquivalents() {
		CMLAtom[] xmlAtom = fixture.xmlAtom;
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(xmlAtom[0]
				.getMolecule());
		int nlig = xmlAtom[0].getLigandAtoms().size();
		Assert.assertEquals("ligand count", 4, nlig);
		nlig = xmlAtom[1].getLigandAtoms().size();
		Assert.assertEquals("ligand count", 1, nlig);
		String el = xmlAtom[0].getElementType();
		Assert.assertEquals("elem", AS.N.value, el);
		int bos = moleculeTool.getBondOrderSum(xmlAtom[0]);
		Assert.assertEquals("bondsum", 4, bos);
		int dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[0],
				FormalChargeControl.DEFAULT);
		Assert.assertEquals("doubleBond equivalents", 0, dbe);
		el = xmlAtom[1].getElementType();
		Assert.assertEquals("elem", AS.C.value, el);
		bos = moleculeTool.getBondOrderSum(xmlAtom[1]);
		Assert.assertEquals("bondsum", 4, bos);
		dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[1],
				FormalChargeControl.DEFAULT);
		Assert.assertEquals("doubleBond equivalents", 0, dbe);
		el = xmlAtom[2].getElementType();
		Assert.assertEquals("elem", AS.S.value, el);
		dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[2],
				FormalChargeControl.DEFAULT);
		Assert.assertEquals("doubleBond equivalents", 0, dbe);
	}

    /**
     * Test method for 'org.xmlcml.cml.element.CMLMolecule.getDoubleBonds()'
     */
    @Test
    public void testGetDoubleBonds() {
        makeMoleculeTool5a();
        moleculeTool5a.calculateBondedAtoms();
        moleculeTool5a.calculateBondOrdersFromXYZ3();
        List<CMLBond> bonds = mol5a.getDoubleBonds();
        Assert.assertEquals("double bonds", 1, bonds.size());
    }



    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getDownstreamAtoms(CMLAtom, CMLAtom)'
	 */
	@Test
	public void testGetDownstreamAtomsCMLAtomCMLAtomSetCMLAtom() {
		MoleculeTool moleculeTool = makeCompleteMol9();
		CMLAtom atom0 = fixture.mol9.getAtom(0);
		CMLAtom atom1 = fixture.mol9.getAtom(1);
		Assert.assertEquals("before", 10, fixture.mol9.getAtoms().size());
		CMLAtomSet downstreamAtoms = moleculeTool.getDownstreamAtoms(atom0,
				atom1);
		Assert.assertEquals("down", 4, downstreamAtoms.size());
		Assert.assertEquals("down", new String[] { "a1", "a1_h1", "a1_h2",
				"a1_h3" }, downstreamAtoms.getXMLContent());
		downstreamAtoms = moleculeTool.getDownstreamAtoms(atom1, atom0);
		Assert.assertEquals("down", 6, downstreamAtoms.size());
		Assert.assertEquals("down", new String[] { "a2", "a3", "a3_h1",
				"a3_h2", "a3_h3", "a2_h1" }, downstreamAtoms.getXMLContent());
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getDownstreamAtoms(CMLBond, CMLAtom)'
	 */
	@Test
	public void testGetDownstreamAtomsCMLBondCMLAtom() {
		MoleculeTool moleculeTool = makeCompleteMol9();
		CMLMolecule mol9 = fixture.mol9;
		CMLAtom atom0 = mol9.getAtom(0);
		CMLAtom atom1 = mol9.getAtom(1);
		CMLBond bond = mol9.getBond(atom0, atom1);
		CMLAtomSet downstreamAtoms = moleculeTool.getDownstreamAtoms(bond,
				atom1);
		Assert.assertEquals("down", 4, downstreamAtoms.size());
		Assert.assertEquals("down", new String[] { "a1", "a1_h1", "a1_h2",
				"a1_h3" }, downstreamAtoms.getXMLContent());
		downstreamAtoms = moleculeTool.getDownstreamAtoms(bond, atom0);
		Assert.assertEquals("down", 6, downstreamAtoms.size());
		Assert.assertEquals("down", new String[] { "a2", "a3", "a3_h1",
				"a3_h2", "a3_h3", "a2_h1" }, downstreamAtoms.getXMLContent());
	}

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getGeometricHybridization()'
     */
    @Test
    public void testGetGeometricHybridization() {
    	AtomTool atomTool = AtomTool.getOrCreateTool(xmlAtom[0]);
        CMLAtom.Hybridization hyb = atomTool
                .getGeometricHybridization();
        Assert.assertEquals("hybrid", CMLAtom.Hybridization.SP3, hyb);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getHydrogenValencyGroup()'
     */
    @Test
    public void testGetHydrogenValencyGroup() {
        int hvg = AtomTool.getHydrogenValencyGroup(xmlAtom[0]);
        Assert.assertEquals("elementType", AS.N.value, xmlAtom[0].getElementType());
        // atom attached to electronegative ligands
        Assert.assertTrue("hydrogen valency", hvg < 0);
        hvg = AtomTool.getHydrogenValencyGroup(xmlAtom[1]);
        Assert.assertEquals("elementType", AS.C.value, xmlAtom[1].getElementType());
        Assert.assertEquals("hydrogen valency", 4, hvg);
        hvg = AtomTool.getHydrogenValencyGroup(xmlAtom[2]);
        Assert.assertEquals("elementType", AS.S.value, xmlAtom[2].getElementType());
        Assert.assertEquals("hydrogen valency", 6, hvg);
        Assert.assertEquals("elementType", AS.O.value, xmlAtom[3].getElementType());
        hvg = AtomTool.getHydrogenValencyGroup(xmlAtom[3]);
        Assert.assertEquals("hydrogen valency", 6, hvg);
        Assert.assertEquals("elementType", AS.F.value, xmlAtom[4].getElementType());
        hvg = AtomTool.getHydrogenValencyGroup(xmlAtom[4]);
        Assert.assertEquals("hydrogen valency", 7, hvg);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getLoneElectronCount(CMLAtom)'
     */
    @Test
    public void testGetLoneElectronCount() {
        // FIXME
        CMLMolecule nitroMethane = (CMLMolecule) JumboTestUtils.parseValidString(nitroMethaneS);
        int n = AtomTool.getOrCreateTool(nitroMethane.getAtom(0)).getLoneElectronCount();
      //  Assert.assertEquals("lone pair", -6, n);
        n = AtomTool.getOrCreateTool(nitroMethane.getAtom(1)).getLoneElectronCount();
        Assert.assertEquals("lone pair", 0, n);
        n = AtomTool.getOrCreateTool(nitroMethane.getAtom(2)).getLoneElectronCount();
        Assert.assertEquals("lone pair", 6, n);
        n = AtomTool.getOrCreateTool(nitroMethane.getAtom(3)).getLoneElectronCount();
        Assert.assertEquals("lone pair", 5, n);
    }

    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getMap(CMLMolecule)'
	 */
	@Test
	public void testGetMap() {
		fixture.makeMol1();
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(fixture.mol1);
		CMLMolecule mol1a = fixture.makeMol1a();
		CMLMap map = moleculeTool1.getMap(mol1a);
		Assert.assertEquals("map", 3, map.getLinkElements().size());
		CMLLink link = map.getLinkElements().get(0);
		Assert.assertEquals("link", "a1", link.getFrom());
		Assert.assertEquals("link", "a11", link.getTo());
	}


    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getMappedAtom(CMLMap,
	 * CMLAtom, Direction)'
	 */
	@Test
	public void testGetMappedAtom() {
		fixture.makeMol1();
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(fixture.mol1);
		CMLMolecule mol1a = fixture.makeMol1a();
		MoleculeTool moleculeTool1a = MoleculeTool.getOrCreateTool(mol1a);
		CMLMap map = moleculeTool1.getMap(mol1a);
		CMLAtom atom1 = fixture.mol1.getAtom(1);
		CMLAtom atom1a = mol1a.getAtom(1);
		CMLAtom atom1x = moleculeTool1.getMappedAtom(map, atom1a,
				CMLMap.Direction.TO);
		Assert.assertNotNull("mapped atom not null", atom1x);
		Assert.assertEquals("to", "a2", atom1x.getId());
		atom1a = moleculeTool1
				.getMappedAtom(map, atom1a, CMLMap.Direction.FROM);
		Assert.assertNull("mapped atom null", atom1a);
		atom1x = moleculeTool1a
				.getMappedAtom(map, atom1, CMLMap.Direction.FROM);
		Assert.assertNotNull("mapped atom not null", atom1x);
		Assert.assertEquals("to", "a12", atom1x.getId());
		atom1a = moleculeTool1a.getMappedAtom(map, atom1, CMLMap.Direction.TO);
		Assert.assertNull("mapped atom null", atom1a);
	}

    /**
	 * Test method for
	 * {@link org.xmlcml.cml.element.CMLAmount#getMolarAmount(org.xmlcml.cml.element.lite.CMLMolecule)}
	 * .
	 */
	@Test
	public final void testGetMolarAmount() {
		CMLMolecule molecule = new CMLMolecule();
		CMLAtom atom = new CMLAtom("a1", ChemicalElement
				.getChemicalElement("Na"));
		molecule.addAtom(atom);
		atom = new CMLAtom("a2", ChemicalElement
				.getChemicalElement(AS.Cl.value));
		molecule.addAtom(atom);
		double d = MoleculeTool.getOrCreateTool(molecule)
				.getCalculatedMolecularMass(
						HydrogenControl.NO_EXPLICIT_HYDROGENS);
		Assert.assertEquals("MW ", 58.44277, d);
		CMLAmount massAmount = new CMLAmount();
		massAmount.setUnits(CMLConstants.Units.GRAM.value);
		massAmount.addAttribute(new NamespaceRefAttribute("unitType",
				"unitType:mass"));
		massAmount.setXMLContent(100.0);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		CMLAmount molarAmount = moleculeTool.getMolarAmount(massAmount);
		Assert.assertNotNull("molarAmount not null", molarAmount);
		Assert.assertEquals("molarAmount", 1.7110756386119275, molarAmount
				.getXMLContent(), 0.00001);
	}

    /** test get molecule from namespaceRef.
     * @throws IOException
     */
    @Test
    @Ignore // FIXME
    public void testGetMolecule() throws IOException {
        String molS = "<molecule " + CMLConstants.CML_XMLNS + " ref='p:oh' "
                + "xmlns:p='http://www.xml-cml.org/mols/frags'/>";
        Catalog catalog = getMoleculeCatalog();
        CMLMolecule mol = null;
        try {
            mol = (CMLMolecule) new CMLBuilder().parseString(molS);
        } catch (Exception e) {
            throw new RuntimeException("should never throw " + e);
        }
//        MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
        CMLNamespace namespace = CMLNamespace.createNamespace("p", mol);
        CMLMolecule refMol = (CMLMolecule) catalog.getIndexable(
        		"p:oh", namespace, IndexableByIdList.Type.MOLECULE_LIST);
        Assert.assertNotNull("refenced mol not null", refMol);
        Assert.assertEquals("refenced mol", "oh", refMol.getId());
    }

    /**
     * test gets mapping of namespaces onto molecule files.
     * @throws IOException
     *
     */
    @Test
    public void testGetMoleculeCatalog() throws IOException {
        Catalog catalogTool = getMoleculeCatalog();
        CMLMap map = catalogTool.getCmlMap();
        Assert.assertNotNull("catalog", map);
      //  Assert.assertEquals("map size", 4, map.getLinkElements().size()); This test causes errors whenever the catalog is updated so I have removed it nwe23
        CMLLink link0 = map.getLinkElements().get(0);
        Assert.assertEquals("link0", "http://www.xml-cml.org/mols/geom", link0
                .getFrom());
        Assert.assertEquals("link0", "./geom.xml", link0.getTo());
        CMLLink link1 = map.getLinkElements().get(1);
        Assert.assertEquals("link1", "http://www.xml-cml.org/mols/frags", link1
                .getFrom());
        Assert.assertEquals("link1", "./fragments/frags.xml", link1.getTo());
    }

    /**
     * test get map of molecules under namespace.
     * @throws IOException
     *
     */
    @Test
    @Ignore // FIXME
    public void testGetMolecules() throws IOException {
        Map<String, Indexable> map = null;
        try {
        	// FIXME
//            map = catalog.getIndexableList(namespace, IndexableByIdList.Type.MOLECULE_LIST);
        } catch (RuntimeException e) {
            Assert.fail("expected "+e);
        }
        Assert.assertNotNull("molecules", map);
        // Assert.assertEquals("molecules size", 6, map.size());
        CMLMolecule molecule = (CMLMolecule) map.get("oh");
        Assert.assertNotNull("oh", molecule);
        Assert.assertEquals("oh", "oh", molecule.getId());

        /*--
         namespace = "http://www.xml-cml.org/mols/frags";
         map = MoleculeTool.getMolecules(namespace);
         Assert.assertNotNull("molecules", map);
         Assert.assertEquals("molecules size", 6, map.size());
         --*/
    }

    /**
	 * Test method for
	 * 'org.xmlcml.cml.tools.MoleculeTool.getNonHydrogenLigandList(CMLAtom)'
	 */
	@Test
	public void testGetNonHydrogenLigandList() {
		AbstractSVGTool moleculeTool = makeCompleteMol9();
		CMLAtom atom0 = fixture.mol9.getAtom(0);
		CMLAtom atom1 = fixture.mol9.getAtom(1);
		List<CMLAtom> atomList = AtomTool.getOrCreateTool(atom0).getNonHydrogenLigandList();
		Assert.assertEquals("nonH", 1, atomList.size());
		atomList = AtomTool.getOrCreateTool(atom1).getNonHydrogenLigandList();
		Assert.assertEquals("nonH", 2, atomList.size());
	}

    /**
	 * test molecule range3.
	 */
	@Test
	public void testGetRange3() {
		fixture.makeMol5();
		MoleculeTool moleculeTool5 = MoleculeTool.getOrCreateTool(fixture.mol5);
		Real3Range mol5Range3 = moleculeTool5
				.calculateRange3(CoordinateType.CARTESIAN);
		Assert.assertEquals("x range min", -0.85, mol5Range3.getXRange()
				.getMin(), CC.EPS);
		Assert.assertEquals("x range max", 1.0,
				mol5Range3.getXRange().getMax(), CC.EPS);
		Assert.assertEquals("y range min", -0.54, mol5Range3.getYRange()
				.getMin(), CC.EPS);
		Assert.assertEquals("y range max", 2.2,
				mol5Range3.getYRange().getMax(), CC.EPS);
		Assert.assertEquals("z range min", 0., mol5Range3.getZRange().getMin(),
				CC.EPS);
		Assert.assertEquals("z range max", 0.5,
				mol5Range3.getZRange().getMax(), CC.EPS);
	}

    /** */
	@Test
	public final void testGetTotalHydrogenCount() {
		Assert.assertEquals("benzene", 6, MoleculeTool.getOrCreateTool(benzene).getTotalHydrogenCount());
		String moleculeS = "" +
				"<molecule "+CMLConstants.CML_XMLNS+">" +
				"  <atomArray>" +
				"    <atom id='a1' elementType='C'/>" +
				"    <atom id='a2' elementType='O'/>" +
				"  </atomArray>" +
				"  <bondArray>" +
				"    <bond atomRefs2='a1 a2' order='1'/>" +
				"  </bondArray>" +
				"</molecule>";
		CMLMolecule molecule = (CMLMolecule) JumboTestUtils.parseValidString(moleculeS);
		int hydrogenCount = MoleculeTool.getOrCreateTool(molecule).getTotalHydrogenCount();
		Assert.assertEquals("h count", 4, hydrogenCount);
	}

    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.getVector3D()'
	 */
	@Test
	public void testGetVector3D() {
		fixture.makeMol1();
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(fixture.mol1);
		Point3Vector vector = moleculeTool1
				.getCoordinates3(CoordinateType.CARTESIAN);
		Assert.assertNotNull("get vector3d", vector);
		Assert.assertEquals("get vector3d", 3, vector.size());
		Point3 p = vector.getPoint3(2);
		JumboTestUtils.assertEquals("point", new double[] { 2., 3., 1. }, p, CC.EPS);
		fixture.makeMol7();
		MoleculeTool moleculeTool7 = MoleculeTool.getOrCreateTool(fixture.mol7);
		vector = moleculeTool7.getCoordinates3(CoordinateType.CARTESIAN);
		Assert.assertNull("get vector3d should be null", vector);
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.removeOverlapping3DAtoms(CMLAtomSet
	 * otherSet)'
	 */
	@Test
	public void testRemoveOverlapping3DAtomsCMLAtomSet() {
		fixture.makeMol1();
		CMLMolecule mol1 = fixture.mol1;
		Assert.assertEquals("before remove overlap", 3, mol1.getAtomCount());
		fixture.makeMol2();
		CMLMolecule mol2 = fixture.mol2;
		Assert.assertEquals("before remove overlap", 3, mol2.getAtomCount());
		MoleculeTool.getOrCreateTool(mol1).removeOverlapping3DAtoms(mol2,
				CoordinateType.CARTESIAN);
		Assert.assertEquals("remove overlap", 1, mol1.getAtomCount());
		Assert.assertEquals("remove overlap", "a3", mol1.getAtom(0).getId());
		Assert.assertEquals("remove overlap", 3, mol2.getAtomCount());
		Assert.assertEquals("remove overlap", "a13", mol2.getAtom(2).getId());
	}

    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.roundCoords(double)'
	 */
	@Test
	public void testRoundCoords() {
		fixture.makeMol1();
		CMLMolecule mol1 = fixture.mol1;
		Point3 p = mol1.getAtom(2).getXYZ3();
		JumboTestUtils.assertEquals("original point", new double[] { 2., 3., 1. }, p, CC.EPS);
		MoleculeTool.getOrCreateTool(mol1).translate3D(
				new Vector3(0.000111, 0.999999, 0.012345));
		p = mol1.getAtom(2).getXYZ3();
		JumboTestUtils.assertEquals("moved point",
				new double[] { 2.000111, 3.999999, 1.012345 }, p, CC.EPS);
		mol1.roundCoords(.001, CoordinateType.CARTESIAN);
		p = mol1.getAtom(2).getXYZ3();
		JumboTestUtils.assertEquals("moved point", new double[] { 2.000, 3.999, 1.012 }, p,
				CC.EPS);
	}

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.setBondOrders(String)'
     */
    @Test
    public void testSetBondOrders() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        Assert.assertNull("initial order", mol5.getBonds().get(0).getOrder());
        mol5.setBondOrders(CMLBond.SINGLE);
        Assert.assertEquals("updated order", CMLBond.SINGLE, mol5.getBonds()
                .get(0).getOrder());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.setGeometricHybridization(String)'
     */
    @Test
    public void testSetGeometricHybridization() {
        // TODO
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.setPreferredBondOrders()'
     */
    @Test
    public void testSetPreferredBondOrders() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        Assert.assertNull("initial order", mol5.getBonds().get(0).getOrder());
        mol5.setBondOrders(CMLBond.SINGLE);
        // note that getOrder() will return the preferred order
        Assert.assertEquals("updated order", CMLBond.SINGLE, mol5.getBonds()
                .get(0).getOrderAttribute().getValue());
        mol5.setNormalizedBondOrders();
        Assert.assertEquals("perferred order", CMLBond.SINGLE_S, mol5.getBonds()
                .get(0).getOrderAttribute().getValue());
    }

    /**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#transformCartesians(org.xmlcml.cml.element.CMLTransform3)}
	 * .
	 */
	@Test
	public final void testTransformCartesiansCMLTransform3() {
		fixture.makeMol5();
		CMLTransform3 t3 = new CMLTransform3(new double[] { 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, });
		CMLAtom atom4 = fixture.mol5.getAtom(3);
		Point3 p = atom4.getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("orig", new Point3(0.85, -0.54, 0.5), p, 0.0000001);
		MoleculeTool.getOrCreateTool(fixture.mol5).transformCartesians(t3);
		p = atom4.getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("orig", new Point3(-0.54, 0.5, 0.85), p, 0.0000001);
	}

    /**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#transformCartesians(org.xmlcml.euclid.Transform3)}
	 * .
	 */
	@Test
	public final void testTransformCartesiansTransform3() {
		fixture.makeMol5();
		Transform3 t3 = new Transform3(new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, });
		CMLAtom atom4 = fixture.mol5.getAtom(3);
		Point3 p = atom4.getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("orig", new Point3(0.85, -0.54, 0.5), p, 0.0000001);
		MoleculeTool.getOrCreateTool(fixture.mol5).transformCartesians(t3);
		p = atom4.getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("orig", new Point3(-0.54, 0.5, 0.85), p, 0.0000001);
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.transformFractionalCoordinates(CMLSym
	 * m e t r y ) '
	 */
	@Test
	public void testTransformFractionalCoordinatesCMLSymmetry() {
		fixture.makeMolCryst();
		CMLSymmetry symmetry = new CMLSymmetry(new String[] { "x, y, z",
				"y, -x, 1/2+z" });
		List<CMLMolecule> molList = MoleculeTool.getOrCreateTool(
				fixture.cmlCrystMol).transformFractionalCoordinates(symmetry);
		Assert.assertEquals("after t", 2, molList.size());
		JumboTestUtils.assertEqualsCanonically("mols equals", molList.get(0),
				fixture.cmlCrystMol);
		Point3 xf = molList.get(1).getAtom(2).getPoint3(
				CoordinateType.FRACTIONAL);
		JumboTestUtils.assertEquals("mols equal", new double[] { 0.22, -0.12, 0.5 }, xf,
				0.0000001);
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.transformFractionalCoordinates(CMLTra
	 * n s f o r m 3 ) '
	 */
	@Test
	public void testTransformFractionalCoordinatesCMLTransform3() {
		fixture.makeMolCryst();
		CMLTransform3 transform = new CMLTransform3(new double[] { 0.0, 1.0,
				0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.25, 0.0, 0.0,
				0.0, 1.0, });

		Point3 pf = fixture.cmlCrystMol.getAtom(2).getPoint3(
				CoordinateType.FRACTIONAL);
		JumboTestUtils.assertEquals("before", new double[] { 0.12, 0.22, 0.0 }, pf, 0.000001);
		MoleculeTool.getOrCreateTool(fixture.cmlCrystMol)
				.transformFractionalCoordinates(transform);
		pf = fixture.cmlCrystMol.getAtom(2)
				.getPoint3(CoordinateType.FRACTIONAL);
		JumboTestUtils.assertEquals("before", new double[] { 0.22, 0.0, 0.37 }, pf, 0.000001);
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.transformFractionalCoordinates(Transf
	 * o r m 3 ) '
	 */
	@Test
	public void testTransformFractionalCoordinatesTransform3() {
		CMLMolecule mol0 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='N' x3='1.0' y3='2.0' z3='3.0' xFract='0.1' yFract='0.2' zFract='0.3'/>"
				+ "    <atom id='a2' elementType='O' x3='1.8' y3='2.8' z3='3.8' xFract='0.15' yFract='0.25' zFract='0.35'/>"
				+ "  </atomArray>" + "</molecule>" + "");
		CMLMolecule mol = new CMLMolecule(mol0);
		CMLTransform3 tr = new CMLTransform3(new double[] { 1, 0, 0, 0, 0, -1,
				0, 0.5, 0, 0, -1, 0.25, 0, 0, 0, 1 });
		MoleculeTool.getOrCreateTool(mol).transformFractionalCoordinates(tr);
		CMLAtom atom0 = mol.getAtom(0);
		JumboTestUtils.assertEquals("transform", new double[] { 1, 2, 3 }, atom0.getXYZ3(),
				CC.EPS);
		JumboTestUtils.assertEquals("transform", new double[] { 0.1, 0.3, -0.05 }, atom0
				.getXYZFract(), CC.EPS);
		Assert.assertEquals("transform", AS.N.value, atom0.getElementType());
		Assert.assertEquals("transform", "a1", atom0.getId());
		CMLAtom atom1 = mol.getAtom(1);
		JumboTestUtils.assertEquals("transform", new double[] { 0.15, 0.25, -0.10 }, atom1
				.getXYZFract(), CC.EPS);
	}

    /**
	 * Test method for
	 * {@link org.xmlcml.cml.element.lite.CMLMolecule#transformFractionalsAndCartesians(org.xmlcml.cml.element.CMLTransform3, org.xmlcml.euclid.Transform3)}
	 * .
	 */
	@Test
	public final void testTransformFractionalsAndCartesians() {
		fixture.makeMolCryst();
		Transform3 orthMat = fixture.cmlCrystCryst
				.getOrthogonalizationTransform();
		CMLTransform3 transform = new CMLTransform3("-y, x, 1/2+z");
		CMLAtom atom = fixture.cmlCrystMol.getAtom(2);
		Point3 px = atom.getPoint3(CoordinateType.CARTESIAN);
		Assert.assertNull("no 3dcart", px);
		Point3 pf = atom.getPoint3(CoordinateType.FRACTIONAL);
		JumboTestUtils.assertEquals("fract", new double[] { 0.12, 0.22, 0.0 }, pf, 0.0000001);
		MoleculeTool.getOrCreateTool(fixture.cmlCrystMol)
				.transformFractionalsAndCartesians(transform, orthMat);
		px = atom.getPoint3(CoordinateType.CARTESIAN);
		JumboTestUtils.assertEquals("3dcart", new double[] { -1.98, 1.2, 5.5 }, px, 0.0000001);
		pf = atom.getPoint3(CoordinateType.FRACTIONAL);
		JumboTestUtils.assertEquals("fract", new double[] { -0.22, 0.12, 0.5 }, pf, 0.0000001);
	}

    /**
	 * Test method for
	 * 'org.xmlcml.cml.element.CMLMolecule.transform(Transform3)'
	 */
	@Test
	public void testTransformTransform3() {
		CMLMolecule mol0 = (CMLMolecule) JumboTestUtils.parseValidString("<molecule "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='N' x3='1.0' y3='2.0' z3='3.0' xFract='0.1' yFract='0.2' zFract='0.3'/>"
				+ "    <atom id='a2' elementType='O' x3='1.8' y3='2.8' z3='3.8' xFract='0.15' yFract='0.25' zFract='0.35'/>"
				+ "  </atomArray>" + "</molecule>" + "");
		CMLMolecule mol = new CMLMolecule(mol0);
		CMLTransform3 tr = new CMLTransform3(new double[] { 1, 0, 0, 0, 0, -1,
				0, 0, 0, 0, -1, 0, 0, 0, 0, 1 });
		MoleculeTool.getOrCreateTool(mol).transformCartesians(tr);
		CMLAtom atom0 = mol.getAtom(0);
		JumboTestUtils.assertEquals("transform", new double[] { 1, -2, -3 }, atom0.getXYZ3(),
				CC.EPS);
		// not transformed
		JumboTestUtils.assertEquals("transform", new double[] { 0.1, 0.2, 0.3 }, atom0
				.getXYZFract(), CC.EPS);
		Assert.assertEquals("transform", AS.N.value, atom0.getElementType());
		Assert.assertEquals("transform", "a1", atom0.getId());
		CMLAtom atom1 = mol.getAtom(1);
		JumboTestUtils.assertEquals("transform", new double[] { 1.8, -2.8, -3.8 }, atom1
				.getXYZ3(), CC.EPS);
	}

    /**
	 * Test method for 'org.xmlcml.cml.element.CMLMolecule.translate3D(Vector3)'
	 */
	@Test
	public void testTranslate3D() {
		fixture.makeMol1();
		CMLMolecule mol1 = fixture.mol1;
		Point3 p = mol1.getAtom(2).getXYZ3();
		JumboTestUtils.assertEquals("original point", new double[] { 2., 3., 1. }, p, CC.EPS);
		MoleculeTool.getOrCreateTool(mol1).translate3D(
				new Vector3(0.000111, 0.999999, 0.012345));
		p = mol1.getAtom(2).getXYZ3();
		JumboTestUtils.assertEquals("moved point",
				new double[] { 2.000111, 3.999999, 1.012345 }, p, CC.EPS);
	}
	
	@Test
	public void testDraw() throws Exception {
		CMLMolecule molecule = SMILESTool.createMolecule("Clc1ccc(cc1)C(=O)N");
		SVGG svgg = MoleculeTool.getOrCreateTool(molecule).drawAndTranslateToRectCorner(MoleculeDisplay.DEFAULT);
		File outputFile = File.createTempFile("molecule", ".svg");
		SVGSVG.wrapAndWriteAsSVG(svgg, outputFile);
	}

	@Test
	public void testDraw1() throws Exception {
		CMLMolecule molecule = SMILESTool.createMolecule("OCCOCCO");
		SVGG svgg = MoleculeTool.getOrCreateTool(molecule).drawAndTranslateToRectCorner(MoleculeDisplay.DEFAULT);
		File outputFile = File.createTempFile("molecule", ".svg");
		SVGSVG.wrapAndWriteAsSVG(svgg, outputFile);
	}
	
	
	@Test
//	@Ignore
	public void getCalculatedIsotopomerMassesH2() {
		CMLMolecule mol = new CMLMolecule();
		mol.addAtom(new CMLAtom("h1", ChemicalElement.AS.H));
		mol.addAtom(new CMLAtom("h2", ChemicalElement.AS.H));
		List<double []> isotopomers = MoleculeTool.getOrCreateTool(mol).getCalculatedIsotopomerMasses();
		Assert.assertEquals(3, isotopomers.size());
		
		Assert.assertEquals(2.015650064, isotopomers.get(0)[0], 1E-12);
		Assert.assertEquals(0.999770013225, isotopomers.get(0)[1], 1E-12);

		Assert.assertEquals(3.02192681, isotopomers.get(1)[0], 1E-12);
		Assert.assertEquals(0.00022997355, isotopomers.get(1)[1], 1E-12);
		
		Assert.assertEquals(4.028203556, isotopomers.get(2)[0], 1E-12);
		Assert.assertEquals(0.000000013225, isotopomers.get(2)[1], 1E-12);
		
		Assert.assertEquals(1d, isotopomers.get(0)[1] + isotopomers.get(1)[1] + isotopomers.get(2)[1], 1E-12);
	}
	
	@Test
//	@Ignore
	public void getCalculatedIsotopomerMassesHCl() {
		CMLMolecule mol = new CMLMolecule();
		mol.addAtom(new CMLAtom("a1", ChemicalElement.AS.H));
		mol.addAtom(new CMLAtom("a2", ChemicalElement.AS.Cl));
		List<double []> isotopomers = MoleculeTool.getOrCreateTool(mol).getCalculatedIsotopomerMasses();
		Assert.assertEquals(4, isotopomers.size());
		
		Assert.assertEquals(35.976677753, isotopomers.get(0)[0], 1E-12);
		Assert.assertEquals(0.757712853, isotopomers.get(0)[1], 1E-12);
		
		Assert.assertEquals(37.973727652, isotopomers.get(1)[0], 1E-12);
		Assert.assertEquals(0.242172147, isotopomers.get(1)[1], 1E-12);
		
		Assert.assertEquals(36.982954499, isotopomers.get(2)[0], 1E-12);
		Assert.assertEquals(0.000087147, isotopomers.get(2)[1], 1E-12);
		
		Assert.assertEquals(38.980004398, isotopomers.get(3)[0], 1E-12);
		Assert.assertEquals(0.000027853, isotopomers.get(3)[1], 1E-12);
		
		Assert.assertEquals(1d, isotopomers.get(0)[1] + isotopomers.get(1)[1]+ isotopomers.get(2)[1]+ isotopomers.get(3)[1], 1E-12);
	}
	
	@Test
	public void getCalculatedIsotopomerMassesUnknownPreciseMasses() {
		//will need to be changed to a different element if the precise mass
		//of U is ever specfied in elementdata.xml (in CMLXOM)
		CMLMolecule mol = new CMLMolecule();
		mol.addAtom(new CMLAtom("a1", ChemicalElement.AS.H));
		mol.addAtom(new CMLAtom("a2", ChemicalElement.getChemicalElement("U")));
		List<double []> isotopomers = MoleculeTool.getOrCreateTool(mol).getCalculatedIsotopomerMasses();
		Assert.assertEquals(6, isotopomers.size());
		for (double[] ds : isotopomers) {
			Assert.assertTrue(Double.isNaN(ds[0]));
		}
		Assert.assertEquals(0.992630834325, isotopomers.get(0)[1], 1E-12);
	}
	
	@Test
	public void getCalculatedIsotopomerMassesNoMaxCount() {
		CMLMolecule mol = new CMLMolecule();
		mol.addAtom(new CMLAtom("a1", ChemicalElement.AS.H ));
		mol.addAtom(new CMLAtom("a2", ChemicalElement.AS.C ));
		mol.addAtom(new CMLAtom("a3", ChemicalElement.AS.O ));

		List<double []> isotopomers = MoleculeTool.getOrCreateTool(mol).getCalculatedIsotopomerMasses(Integer.MAX_VALUE);
		Assert.assertEquals(12, isotopomers.size());
		for (int i = 1; i < 12; i++) {
			Assert.assertTrue(isotopomers.get(i)[1] < isotopomers.get(i-1)[1]);
		}
		
		double totalAbundance = 0;
		for (double[] ds : isotopomers) {
			totalAbundance+=ds[1];
		}
		Assert.assertTrue(Math.abs(1d - totalAbundance) < 1E-12);
	}
		
	@Test
//	@Ignore
	public void getCalculatedIsotopomerMassesWithMaxCount() {
		CMLMolecule mol = new CMLMolecule();
		mol.addAtom(new CMLAtom("a1", ChemicalElement.AS.H ));
		mol.addAtom(new CMLAtom("a2", ChemicalElement.AS.C ));
		mol.addAtom(new CMLAtom("a3", ChemicalElement.AS.O ));
		
		List<double []> isotopomers = MoleculeTool.getOrCreateTool(mol).getCalculatedIsotopomerMasses(3);
		Assert.assertEquals(3, isotopomers.size());
		Assert.assertEquals(29.002739662, isotopomers.get(0)[0], 1E-12);
		Assert.assertEquals(30.006093662, isotopomers.get(1)[0], 1E-12);
		Assert.assertEquals(31.006985332, isotopomers.get(2)[0], 1E-12);
		
		double totalAbundance = 0;
		for (double[] ds : isotopomers) {
			totalAbundance+=ds[1];
		}
		Assert.assertFalse(Math.abs(1d - totalAbundance) < 1E-12);
	}
}
