package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Builder;
import nu.xom.Document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.element.AbstractTest;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.MoleculeAtomBondTest;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.interfacex.Indexable;
import org.xmlcml.cml.interfacex.IndexableList;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.molutil.Molutils;

/**
 * tests moleculeTool.
 *
 * @author pmr
 *
 */
public class MoleculeToolTest extends MoleculeAtomBondTest {

    protected MoleculeTool moleculeTool1;
    protected MoleculeTool moleculeTool2;
    protected MoleculeTool moleculeTool3;
    protected MoleculeTool moleculeTool4;
    protected MoleculeTool moleculeTool5;
    protected MoleculeTool moleculeTool5a;
    protected MoleculeTool moleculeTool6;
    protected MoleculeTool moleculeTool7;
    protected MoleculeTool moleculeTool8;
    protected MoleculeTool moleculeTool9;
    protected MoleculeTool moleculeTool10;
    protected MoleculeTool moleculeToolXom0;
    protected MoleculeTool moleculeToolXml0;
    protected MoleculeTool moleculeToolBond0;
    protected MoleculeTool moleculeToolXmlBonds;

    String benzeneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='benzene'>"
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
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule benzene = null;

    String[] benzeneOrder = new String[] { "2", "1", "2", "1", "2", "1" };

    String nickS = S_EMPTY + "<molecule " + CML_XMLNS + ">" + " <atomArray> "
            + "  <atom id='a1' elementType='O'/>"
            + "  <atom id='a2' elementType='O'/>"
            + "  <atom id='a3' elementType='O'/>"
            + "  <atom id='a4' elementType='O'/>"
            + "  <atom id='a5' elementType='O'/>"
            + "  <atom id='a6' elementType='O'/>"
            + "  <atom id='a7' elementType='O'/>"
            + "  <atom id='a8' elementType='O'/>"
            + "  <atom id='a9' elementType='N'/>"
            + "  <atom id='a10' elementType='N'/>"
            + "  <atom id='a11' elementType='N'/>"
            + "  <atom id='a12' elementType='N'/>"
            + "  <atom id='a13' elementType='N'/>"
            + "  <atom id='a14' elementType='C'/>"
            + "  <atom id='a15' elementType='H'/>"
            + "  <atom id='a16' elementType='C'/>"
            + "  <atom id='a17' elementType='C'/>"
            + "  <atom id='a18' elementType='H'/>"
            + "  <atom id='a19' elementType='C'/>"
            + "  <atom id='a20' elementType='C'/>"
            + "  <atom id='a21' elementType='C'/>"
            + "  <atom id='a22' elementType='H'/>"
            + "  <atom id='a23' elementType='C'/>"
            + "  <atom id='a24' elementType='C'/>"
            + "  <atom id='a25' elementType='H'/>"
            + "  <atom id='a26' elementType='C'/>"
            + "  <atom id='a27' elementType='C'/>"
            + "  <atom id='a28' elementType='C'/>"
            + "  <atom id='a29' elementType='C'/>"
            + "  <atom id='a30' elementType='C'/>"
            + "  <atom id='a31' elementType='C'/>"
            + "  <atom id='a32' elementType='C'/>"
            + "  <atom id='a33' elementType='C'/>"
            + "  <atom id='a34' elementType='C'/>"
            + "  <atom id='a35' elementType='C'/>"
            + "  <atom id='a36' elementType='H'/>"
            + "  <atom id='a37' elementType='H'/>"
            + "  <atom id='a38' elementType='C'/>"
            + "  <atom id='a39' elementType='H'/>"
            + "  <atom id='a40' elementType='H'/>"
            + "  <atom id='a41' elementType='H'/>" + " </atomArray>"
            + " <bondArray> " + "  <bond atomRefs2='a1 a9' /> "
            + "  <bond atomRefs2='a2 a9' /> "
            + "  <bond atomRefs2='a3 a10' /> "
            + "  <bond atomRefs2='a4 a10' />" + "  <bond atomRefs2='a5 a34' />"
            + "  <bond atomRefs2='a6 a34' />" + "  <bond atomRefs2='a6 a35' />"
            + "  <bond atomRefs2='a7 a11' />"
            + "  <bond atomRefs2='a8 a11' /> "
            + "  <bond atomRefs2='a9 a16' /> "
            + "  <bond atomRefs2='a10 a19'/>"
            + "  <bond atomRefs2='a11 a23' />"
            + "  <bond atomRefs2='a12 a32' />"
            + "  <bond atomRefs2='a13 a33' />"
            + "  <bond atomRefs2='a14 a15' />"
            + "  <bond atomRefs2='a14 a16' />"
            + "  <bond atomRefs2='a14 a27' />"
            + "  <bond atomRefs2='a16 a17' />"
            + "  <bond atomRefs2='a17 a18' />"
            + "  <bond atomRefs2='a17 a19' />"
            + "  <bond atomRefs2='a19 a28' />"
            + "  <bond atomRefs2='a20 a21' />"
            + "  <bond atomRefs2='a20 a29' />"
            + "  <bond atomRefs2='a20 a34' />"
            + "  <bond atomRefs2='a21 a22' />"
            + "  <bond atomRefs2='a21 a23' />"
            + "  <bond atomRefs2='a23 a24' />"
            + "  <bond atomRefs2='a24 a25' />"
            + "  <bond atomRefs2='a24 a30' />"
            + "  <bond atomRefs2='a26 a27' />"
            + "  <bond atomRefs2='a26 a30' />"
            + "  <bond atomRefs2='a26 a31' />"
            + "  <bond atomRefs2='a27 a28' />"
            + "  <bond atomRefs2='a28 a29' />"
            + "  <bond atomRefs2='a29 a30' />"
            + "  <bond atomRefs2='a31 a32' />"
            + "  <bond atomRefs2='a31 a33' />"
            + "  <bond atomRefs2='a35 a36' />"
            + "  <bond atomRefs2='a35 a37' />"
            + "  <bond atomRefs2='a35 a38' />"
            + "  <bond atomRefs2='a38 a39' />"
            + "  <bond atomRefs2='a38 a40' />"
            + "  <bond atomRefs2='a38 a41' /> " + " </bondArray>"
            + " <formula formalCharge='0' concise='C 19 H 9 N 5 O 8'> "
            + "  <atomArray elementType='C H N O' count='19.0 9.0 5.0 8.0' />"
            + " </formula> " + "</molecule> " + S_EMPTY;

    CMLMolecule nick = null;

    String[] nickOrder = new String[] { "1", "1", "1", "1", "2", "1", "1", "1",
            "1", "1", "1", "1", "3", "3", "1", "1", "2", "2", "1", "1", "2",
            "1", "2", "1", "1", "2", "1", "1", "2", "1", "1", "2", "1", "1",
            "1", "1", "1", "1", "1", "1", "1", "1", "1" };

    String styreneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='styrene'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a8' elementType='C' hydrogenCount='2'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='A'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='A'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='A'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='A'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='A'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='A'/>"
            + "    <bond id='b7' atomRefs2='a6 a7' order='1'/>"
            + "    <bond id='b8' atomRefs2='a7 a8' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule styrene = null;

    String[] styreneOrder = new String[] { "2", "1", "2", "1", "2", "1", "1",
            "2", };

    String pyreneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='pyrene'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a11' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a12' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a13' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a14' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a15' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a16' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a21' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a22' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a61' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a62' elementType='C' hydrogenCount='1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b11' atomRefs2='a11 a12' order='1'/>"
            + "    <bond id='b12' atomRefs2='a12 a13' order='1'/>"
            + "    <bond id='b13' atomRefs2='a13 a14' order='1'/>"
            + "    <bond id='b14' atomRefs2='a14 a15' order='1'/>"
            + "    <bond id='b15' atomRefs2='a15 a16' order='1'/>"
            + "    <bond id='b16' atomRefs2='a16 a11' order='1'/>"
            + "    <bond id='b17' atomRefs2='a1 a11' order='1'/>"
            + "    <bond id='b21' atomRefs2='a2 a21' order='1'/>"
            + "    <bond id='b22' atomRefs2='a21 a22' order='1'/>"
            + "    <bond id='b23' atomRefs2='a22 a12' order='1'/>"
            + "    <bond id='b61' atomRefs2='a6 a61' order='1'/>"
            + "    <bond id='b62' atomRefs2='a61 a62' order='1'/>"
            + "    <bond id='b63' atomRefs2='a62 a16' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule pyrene = null;

    String[] pyreneOrder = new String[] { "2", "1", "2", "1", "2", "1", "2",
            "1", "2", "1", "2", "1", "1", "1", "2", "1", "1", "2", "1" };

    String tripheneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='triphene'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a11' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a12' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a16' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a21' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a22' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a61' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a62' elementType='C' hydrogenCount='1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b11' atomRefs2='a11 a12' order='1'/>"
            + "    <bond id='b16' atomRefs2='a16 a11' order='1'/>"
            + "    <bond id='b17' atomRefs2='a1 a11' order='1'/>"
            + "    <bond id='b21' atomRefs2='a2 a21' order='1'/>"
            + "    <bond id='b22' atomRefs2='a21 a22' order='1'/>"
            + "    <bond id='b23' atomRefs2='a22 a12' order='1'/>"
            + "    <bond id='b61' atomRefs2='a6 a61' order='1'/>"
            + "    <bond id='b62' atomRefs2='a61 a62' order='1'/>"
            + "    <bond id='b63' atomRefs2='a62 a16' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule triphene = null;

    String[] tripheneOrder = new String[] { "2", "1", "2", "1", "2", "1", "1",
            "2", "1", "1", "1", "2", "1", "2", "1" };

    String methyleneCyclohexeneS = S_EMPTY + "<molecule " + CML_XMLNS
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
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule methyleneCyclohexene = null;

    String[] methyleneCyclohexeneOrder = new String[] { "2", "1", "1", "1",
            "1", "1", "2" };

    String methyleneCyclohexadieneS = S_EMPTY + "<molecule " + CML_XMLNS
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
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule methyleneCyclohexadiene = null;

    String[] methyleneCyclohexadieneOrder = new String[] { "2", "1", "1", "2",
            "1", "1", "2" };

    String co2S = S_EMPTY + "<molecule " + CML_XMLNS + " title='co2'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule co2 = null;

    String[] co2Order = new String[] { "2", "2" };

    String azuleneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='azulene'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a8' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a9' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a10' elementType='C' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a7' order='1'/>"
            + "    <bond id='b7' atomRefs2='a7 a8' order='1'/>"
            + "    <bond id='b8' atomRefs2='a8 a9' order='1'/>"
            + "    <bond id='b9' atomRefs2='a9 a10' order='1'/>"
            + "    <bond id='b10' atomRefs2='a1 a10' order='1'/>"
            + "    <bond id='b11' atomRefs2='a4 a10' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule azulene = null;

    String[] azuleneOrder = new String[] { "2", "1", "2", "1", "2", "1", "2",
            "1", "2", "1", "1" };

    String conjugatedS = S_EMPTY + "<molecule " + CML_XMLNS + " title='conjugated'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a8' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a9' elementType='C' hydrogenCount='2'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a7' order='1'/>"
            + "    <bond id='b7' atomRefs2='a7 a8' order='1'/>"
            + "    <bond id='b8' atomRefs2='a8 a9' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule conjugated = null;

    String formate1S = S_EMPTY
            + "<molecule "
            + CML_XMLNS
            + " title='formate1'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='0' formalCharge='-1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule formate1 = null;

    String formate2S = S_EMPTY + "<molecule " + CML_XMLNS + " title='formate2'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule formate2 = null;

    String formate3S = S_EMPTY + "<molecule " + CML_XMLNS + " title='formate3'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule formate3 = null;

    String pyridineS = S_EMPTY + "<molecule " + CML_XMLNS + " title='pyridine'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='N' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule pyridine = null;

    String pyridiniumS = S_EMPTY
            + "<molecule "
            + CML_XMLNS
            + " title='pyridinium'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='N' hydrogenCount='1' formalCharge='1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule pyridinium = null;

    String pyridone4S = S_EMPTY + "<molecule " + CML_XMLNS + " title='pyridone4'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='N' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a7' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b7' atomRefs2='a4 a7' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule pyridone4 = null;

    static String nitroMethaneS = ""
            + "<molecule "
            + CML_XMLNS
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
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule nitroMethane = null;

    String nitricS = S_EMPTY
            + "<molecule "
            + CML_XMLNS
            + " title='nitric'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='N' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0' formalCharge='-1'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule nitric = null;

    String oxalateS = S_EMPTY
            + "<molecule "
            + CML_XMLNS
            + " title='oxalate'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0' formalCharge='-1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a6' elementType='O' hydrogenCount='0' formalCharge='-1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a4 a6' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule oxalate = null;

    // =========== redistribute molecular charge ==============
    String methylammoniumS = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='methylammonium' formalCharge='1'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='3'/>"
            + "    <atom id='a2' elementType='N' hydrogenCount='3'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule methylammonium = null;

    String pyridinium1S = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='pyridinium1' formalCharge='1'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='N' hydrogenCount='1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='2'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='2'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='2'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule pyridinium1 = null;

    String oxalate2S = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='oxalate2' formalCharge='-2'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a6' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a4 a6' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule oxalate2 = null;

    String diMethylIminiumS = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='diMethylIminium Me2N-CH2(+)'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='2'/>"
            + "    <atom id='a2' elementType='N' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='3'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='3'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a2 a4' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule diMethylIminium = null;

    String munchnoneS = S_EMPTY + "<molecule " + CML_XMLNS + " title='munchnone'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='N' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a1 a5' order='1'/>"
            + "    <bond id='b6' atomRefs2='a1 a6' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule munchnone = null;

    // nitrogen molecules
    String nitric2S = S_EMPTY + "<molecule " + CML_XMLNS + " title='ON(O)O'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='N' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a2 a4' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule nitric2 = null;

    String nitroMethane2S = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='CH3N(O)O'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='3'/>"
            + "    <atom id='a2' elementType='N' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a2 a4' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule nitroMethane2 = null;

    // oxy anion
    String carbonate2S = S_EMPTY + "<molecule " + CML_XMLNS + " title='OC(O)O'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a2 a4' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule carbonate2 = null;

    // oxy anion
    String hydrogenSulfateS = S_EMPTY + "<molecule " + CML_XMLNS + " title='HSO4'>"
            + "  <atomArray>"
            + "    <atom id='a1' elementType='S' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a1 a5' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule hydrogenSulfate = null;

    // methaneSulfonate
    String methaneSulfonateS = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='methaneSulfonate'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='S' hydrogenCount='0'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='3'/>"
            + "    <atom id='a3' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a4' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a5' elementType='O' hydrogenCount='0'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a1 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a1 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a1 a5' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule methaneSulfonate = null;

    String benzophenoneS = S_EMPTY + "<molecule " + CML_XMLNS
            + " title='benzophenone'>" + "  <atomArray>"
            + "    <atom id='a1' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a2' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a3' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a4' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a5' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a6' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a7' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a8' elementType='O' hydrogenCount='0'/>"
            + "    <atom id='a9' elementType='C' hydrogenCount='0'/>"
            + "    <atom id='a10' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a11' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a12' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a13' elementType='C' hydrogenCount='1'/>"
            + "    <atom id='a14' elementType='C' hydrogenCount='1'/>"
            + "  </atomArray>" + "  <bondArray>"
            + "    <bond id='b1' atomRefs2='a1 a2' order='1'/>"
            + "    <bond id='b2' atomRefs2='a2 a3' order='1'/>"
            + "    <bond id='b3' atomRefs2='a3 a4' order='1'/>"
            + "    <bond id='b4' atomRefs2='a4 a5' order='1'/>"
            + "    <bond id='b5' atomRefs2='a5 a6' order='1'/>"
            + "    <bond id='b6' atomRefs2='a6 a1' order='1'/>"
            + "    <bond id='b7' atomRefs2='a6 a7' order='1'/>"
            + "    <bond id='b8' atomRefs2='a7 a8' order='1'/>"
            + "    <bond id='b9' atomRefs2='a7 a9' order='1'/>"
            + "    <bond id='b10' atomRefs2='a9 a10' order='1'/>"
            + "    <bond id='b11' atomRefs2='a10 a11' order='1'/>"
            + "    <bond id='b12' atomRefs2='a11 a12' order='1'/>"
            + "    <bond id='b13' atomRefs2='a12 a13' order='1'/>"
            + "    <bond id='b14' atomRefs2='a13 a14' order='1'/>"
            + "    <bond id='b15' atomRefs2='a9 a14' order='1'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule benzophenone = null;

    String sproutS = S_EMPTY + "<molecule " + CML_XMLNS + " title='sprout'>"
            + "  <atomArray>" + "    <atom id='a1' elementType='C'/>"
            + "    <atom id='a2' elementType='C'/>"
            + "    <atom id='a3' elementType='C'/>"
            + "    <atom id='a4' elementType='C'/>"
            + "    <atom id='a5' elementType='C'/>"
            + "    <atom id='a6' elementType='C'/>"
            + "    <atom id='a7' elementType='F'/>"
            + "    <atom id='a8' elementType='Cl'/>"
            + "    <atom id='a9' elementType='Br'/>"
            + "    <atom id='a10' elementType='I'/>"
            + "    <atom id='a11' elementType='H'/>"
            + "    <atom id='a12' elementType='C'/>"
            + "    <atom id='a13' elementType='O'/>" + "   </atomArray>"
            + "   <bondArray>" + "     <bond id='a1 a2' atomRefs2='a1 a2'/>"
            + "     <bond id='a2 a3' atomRefs2='a2 a3'/>"
            + "     <bond id='a3 a4' atomRefs2='a3 a4'/>"
            + "     <bond id='a4 a5' atomRefs2='a4 a5'/>"
            + "     <bond id='a5 a6' atomRefs2='a5 a6'/>"
            + "     <bond id='a1 a6' atomRefs2='a1 a6'/>"
            + "     <bond id='a1 a7' atomRefs2='a1 a7'/>"
            + "     <bond id='a2 a8' atomRefs2='a2 a8'/>"
            + "     <bond id='a3 a9' atomRefs2='a3 a9'/>"
            + "     <bond id='a4 a10' atomRefs2='a4 a10'/>"
            + "     <bond id='a5 a11' atomRefs2='a5 a11'/>"
            + "     <bond id='a6 a12' atomRefs2='a6 a12'/>"
            + "     <bond id='a12 a13' atomRefs2='a12 a13'/>"
            + "  </bondArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule sprout = null;

    protected static Logger logger = Logger.getLogger(MoleculeToolTest.class
            .getName());

    // ************************************************************************
    /**
     * constructor.
     *
     */
    public MoleculeToolTest() {

    }

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        makeMol1();
        moleculeTool1 = new MoleculeTool(mol1);
        moleculeToolXom0 = new MoleculeTool(xomAtom[0].getMolecule());
        moleculeToolXml0 = new MoleculeTool(xmlAtom[0].getMolecule());
        moleculeToolBond0 = new MoleculeTool(xmlBonds.get(0).getMolecule());
        benzene = makeMol(benzene, benzeneS);
        nick = makeMol(nick, nickS);
        pyrene = makeMol(pyrene, pyreneS);
        triphene = makeMol(triphene, tripheneS);
        styrene = makeMol(styrene, styreneS);
        methyleneCyclohexene = makeMol(methyleneCyclohexene,
                methyleneCyclohexeneS);
        methyleneCyclohexadiene = makeMol(methyleneCyclohexadiene,
                methyleneCyclohexadieneS);
        co2 = makeMol(co2, co2S);
        azulene = makeMol(azulene, azuleneS);
        conjugated = makeMol(conjugated, conjugatedS);
        formate1 = makeMol(formate1, formate1S);
        formate2 = makeMol(formate2, formate2S);
        formate3 = makeMol(formate3, formate3S);
        pyridine = makeMol(pyridine, pyridineS);
        pyridinium = makeMol(pyridinium, pyridiniumS);
        pyridone4 = makeMol(pyridone4, pyridone4S);
        nitroMethane = makeMol(nitroMethane, nitroMethaneS);
        nitric = makeMol(nitric, nitricS);
        oxalate = makeMol(oxalate, oxalateS);
        benzophenone = makeMol(benzophenone, benzophenoneS);
        methylammonium = makeMol(methylammonium, methylammoniumS);
        munchnone = makeMol(munchnone, munchnoneS);
        pyridinium1 = makeMol(pyridinium1, pyridinium1S);
        oxalate2 = makeMol(oxalate2, oxalate2S);
        diMethylIminium = makeMol(diMethylIminium, diMethylIminiumS);
        nitric2 = makeMol(nitric2, nitric2S);
        nitroMethane2 = makeMol(nitroMethane2, nitroMethane2S);
        carbonate2 = makeMol(carbonate2, carbonate2S);
        hydrogenSulfate = makeMol(hydrogenSulfate, hydrogenSulfateS);
        methaneSulfonate = makeMol(methaneSulfonate, methaneSulfonateS);
        sprout = makeMol(sprout, sproutS);
    }

    private CMLMolecule makeMol(CMLMolecule mol, String s) {
        if (mol == null) {
            mol = (CMLMolecule) parseValidString(s);
        }
        return mol;
    }

    protected void makeMoleculeTool1() {
        makeMol1();
        moleculeTool1 = new MoleculeTool(mol1);
    }

    protected void makeMoleculeTool2() {
        makeMol2();
        moleculeTool2 = new MoleculeTool(mol2);
    }

    protected void makeMoleculeTool3() {
        makeMol3();
        moleculeTool3 = new MoleculeTool(mol3);
    }

    protected void makeMoleculeTool4() {
        makeMol4();
        moleculeTool4 = new MoleculeTool(mol4);
    }

    protected void makeMoleculeTool5() {
        makeMol5();
        moleculeTool5 = new MoleculeTool(mol5);
    }

    protected void makeMoleculeTool5a() {
        makeMol5a();
        moleculeTool5a = new MoleculeTool(mol5a);
    }

    protected void makeMoleculeTool6() {
        makeMol6();
        moleculeTool6 = new MoleculeTool(mol6);
    }

    protected void makeMoleculeTool7() {
        makeMol7();
        moleculeTool7 = new MoleculeTool(mol7);
    }

    protected void makeMoleculeTool8() {
        makeMol8();
        moleculeTool8 = new MoleculeTool(mol8);
    }

    protected void makeMoleculeTool9() {
        makeMol9();
        moleculeTool9 = new MoleculeTool(mol9);
    }

    protected void makeMoleculeTool10() {
        makeMol10();
        moleculeTool10 = new MoleculeTool(mol10);
    }

    protected void makeMoleculeToolXomAtom0() {
        moleculeToolXom0 = new MoleculeTool(xomAtom[0].getMolecule());
    }

    protected void makeMoleculeToolXmlAtom0() {
        moleculeToolXml0 = new MoleculeTool(xmlAtom[0].getMolecule());
    }

    protected void makeMoleculeToolBond0() {
        moleculeToolBond0 = new MoleculeTool(xomBond[0].getMolecule());
    }

    protected void makeMoleculeToolXmlBonds() {
        moleculeToolXmlBonds = new MoleculeTool(xmlBonds.get(0).getMolecule());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.MoleculeTool.adjustBondOrdersToValency()'
     */
    @Test
    public void testAdjustBondOrdersAndChargesToValency() {
        // abocv(benzene);
        /*--
         abocv(pyrene);
         abocv(styrene);
         abocv(methyleneCyclohexene);
         abocv(methyleneCyclohexadiene);
         abocv(co2);
         abocv(azulene);
         abocv(conjugated);
         abocv(formate1);
         abocv(formate2);
         abocv(formate3);
         abocv(pyridine);
         abocv(pyridinium);
         abocv(pyridone4);
         abocv(nitroMethane);
         abocv(nitric);
         abocv(oxalate);
         --*/
        // abocv(oxalate2);
        /*--
         abocv(benzophenone);
         abocv(methylammonium);
         abocv(pyridinium1);
         abocv(oxalate2);
         abocv(diMethylIminium);
         --*/
        // abocv(munchnone);
        /*--
         abocv(nitric2);
         abocv(nitroMethane2);
         abocv(nitric2);
         abocv(carbonate2);
         abocv(hydrogenSulfate);
         abocv(nitroMethane2);
         abocv(methaneSulfonate);
         --*/
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.MoleculeTool.adjustBondOrdersToValency() { '
     */
    @Test
    public void testAdjustBondOrdersToValency() {
        abov(benzene, 0, benzeneOrder); // OK
        // abov(nick, 0, nickOrder);
        abov(styrene, 0, styreneOrder); // OK
        abov(pyrene, 0, pyreneOrder); // OK
        abov(triphene, 1, tripheneOrder); //
        // abov(methyleneCyclohexene, 0, methyleneCyclohexeneOrder); // OK
        // abov(methyleneCyclohexadiene, 0, methyleneCyclohexadieneOrder); // OK
        abov(co2, 0, co2Order); // OK
        abov(azulene, 0, azuleneOrder);
        /*--
         abov(conjugated);
         abov(formate1);
         abov(formate2);
         abov(formate3);
         abov(pyridine);
         abov(pyridinium);
         abov(pyridone4);
         abov(nitroMethane);
         abov(nitric);
         abov(oxalate);
         --*/
        // abov(munchnone, 0);
        // abov(oxalate2, 2); // OK
        // abov(benzophenone);
    }

    private void abov(CMLMolecule mol, int knownUnpaired, String[] expected) {
        // System.out.println("====adjustBondOrders===== "+mol.getTitle()+"
        // ======================");
        mol.setBondOrders(CMLBond.SINGLE);
        PiSystemControls piSystemManager = new PiSystemControls();
        piSystemManager.setUpdateBonds(true);
        piSystemManager.setKnownUnpaired(knownUnpaired);
        piSystemManager.setDistributeCharge(true);
        new MoleculeTool(mol).adjustBondOrdersToValency(piSystemManager);
        List<CMLBond> bonds = mol.getBonds();
        String[] found = new String[bonds.size()];
        int i = 0;
        for (CMLBond bond : bonds) {
            found[i++] = bond.getOrder();
        }
        StringTestBase.assertEquals("expected orders", found, expected);
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

//    private void dmcn4(CMLMolecule mol) {
//        System.out.println("====distributeCharge====== " + mol.getTitle()
//                + " ======================");
//        new MoleculeTool(mol).distributeMolecularChargeToN4();
//    }
//

//    private void tcfe(CMLMolecule mol, int knownUnpaired) {
//        System.out.println("====transferUnpairedPi====== " + mol.getTitle()
//                + " ======================");
//        MoleculeTool moleculeTool = new MoleculeTool(mol);
//        mol.setBondOrders(CMLBond.SINGLE);
//        PiSystemManager piSystemManager = new PiSystemManager();
//        piSystemManager.setUpdateBonds(true);
//        piSystemManager.setKnownUnpaired(knownUnpaired);
//        moleculeTool.adjustBondOrdersToValency(piSystemManager);
////        moleculeTool.transferChargeToFreePiElectrons();
//    }

//    /**
//     * common molecules.
//     */
//    @Test
//    public void testMarkupCommonMolecules() {
//        /*--
//         mcm(nitric2);
//         mcm(carbonate2);
//         mcm(hydrogenSulfate);
//         mcm(nitroMethane2);
//         mcm(methaneSulfonate);
//         --*/
//    }

//    private void mcm(CMLMolecule mol) {
//        System.out.println("====MarkupCommonMolecules====== " + mol.getTitle()
//                + " ======================");
//        MoleculeTool moleculeTool = new MoleculeTool(mol);
//        moleculeTool.markupCommonMolecules();
//    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLAtom.getBondOrderSum()'
     */
    @Test
    public void testGetBondOrderSum() {
        // makeMoleculeToolXml0();
        String el = xmlAtom[0].getElementType();
        Assert.assertEquals("element type", "N", el);
        int bes = moleculeToolXml0.getBondOrderSum(xmlAtom[0]);
        el = xmlAtom[1].getElementType();
        Assert.assertEquals("element type", "C", el);
        bes = moleculeToolXml0.getBondOrderSum(xmlAtom[1]);
        Assert.assertEquals("bond order sum", 4, bes);
        benzene.setBondOrders(CMLBond.SINGLE);
        int bes1 = new MoleculeTool(benzene)
                .getBondOrderSum(benzene.getAtom(0));
        Assert.assertEquals("bond order sum", 3, bes1);
        methyleneCyclohexene.setBondOrders(CMLBond.SINGLE);
        MoleculeTool methyleneCyclohexeneTool = new MoleculeTool(
                methyleneCyclohexene);
        bes1 = methyleneCyclohexeneTool.getBondOrderSum(methyleneCyclohexene
                .getAtom(0));
        Assert.assertEquals("bond order sum", 3, bes1);
        bes1 = methyleneCyclohexeneTool.getBondOrderSum(methyleneCyclohexene
                .getAtom(0));
        Assert.assertEquals("bond order sum", 3, bes1);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getDoubleBondEquivalents()'
     */
    @Test
    public void testGetDoubleBondEquivalents() {
        MoleculeTool moleculeTool = new MoleculeTool(xmlAtom[0].getMolecule());
        int nlig = xmlAtom[0].getLigandAtoms().size();
        Assert.assertEquals("ligand count", 4, nlig);
        nlig = xmlAtom[1].getLigandAtoms().size();
        Assert.assertEquals("ligand count", 1, nlig);
        String el = xmlAtom[0].getElementType();
        Assert.assertEquals("elem", "N", el);
        int bos = moleculeTool.getBondOrderSum(xmlAtom[0]);
        Assert.assertEquals("bondsum", 4, bos);
        int dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[0],
                FormalChargeControl.DEFAULT);
        Assert.assertEquals("doubleBond equivalents", 0, dbe);
        el = xmlAtom[1].getElementType();
        Assert.assertEquals("elem", "C", el);
        bos = moleculeTool.getBondOrderSum(xmlAtom[1]);
        Assert.assertEquals("bondsum", 4, bos);
        dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[1],
                FormalChargeControl.DEFAULT);
        Assert.assertEquals("doubleBond equivalents", 0, dbe);
        el = xmlAtom[2].getElementType();
        Assert.assertEquals("elem", "S", el);
        dbe = moleculeTool.getDoubleBondEquivalents(xmlAtom[2],
                FormalChargeControl.DEFAULT);
        Assert.assertEquals("doubleBond equivalents", 0, dbe);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getGeometricHybridization()'
     */
    @Test
    public void testGetGeometricHybridization() {
        CMLAtom.Hybridization hyb = moleculeToolXml0
                .getGeometricHybridization(xmlAtom[0]);
        Assert.assertEquals("hybrid", CMLAtom.Hybridization.SP3, hyb);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.setGeometricHybridization(String)'
     */
    @Test
    @Ignore
    public void testSetGeometricHybridization() {
        // TODO
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getHydrogenValencyGroup()'
     */
    @Test
    public void testGetHydrogenValencyGroup() {
        int hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[0]);
        Assert.assertEquals("elementType", "N", xmlAtom[0].getElementType());
        // atom attached to electronegative ligands
        Assert.assertTrue("hydrogen valency", hvg < 0);
        hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[1]);
        Assert.assertEquals("elementType", "C", xmlAtom[1].getElementType());
        Assert.assertEquals("hydrogen valency", 4, hvg);
        hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[2]);
        Assert.assertEquals("elementType", "S", xmlAtom[2].getElementType());
        Assert.assertEquals("hydrogen valency", 6, hvg);
        Assert.assertEquals("elementType", "O", xmlAtom[3].getElementType());
        hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[3]);
        Assert.assertEquals("hydrogen valency", 6, hvg);
        Assert.assertEquals("elementType", "F", xmlAtom[4].getElementType());
        hvg = moleculeToolXml0.getHydrogenValencyGroup(xmlAtom[4]);
        Assert.assertEquals("hydrogen valency", 7, hvg);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLAtom.getSumNonHydrogenBondOrder()'
     */
    @Test
    public void testGetSumNonHydrogenBondOrder() {
        int sum = moleculeToolXml0.getSumNonHydrogenBondOrder(xmlAtom[0]);
        Assert.assertEquals("nonh bond order sum", 4, sum);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLAtom.deleteHydrogen()'
     */
    @Test
    public void testDeleteHydrogen() {
        CMLAtom atom = benzene.getAtom(0);
        Assert.assertEquals("before delete H", 1, atom.getHydrogenCount());
        new MoleculeTool(benzene).deleteHydrogen(atom);
        Assert.assertEquals("after delete H", 0, atom.getHydrogenCount());
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
            String t01 = S_EMPTY
                    + "<molecule id='t01' "
                    + CML_XMLNS
                    + ">"
                    + "  <atomArray>"
                    + "    <atom id='a1' elementType='C' x3='10' y3='10' z3='10'/>"
                    + "    <atom id='h1' elementType='H'/>"
                    + "  </atomArray>" + "  <bondArray>"
                    + "    <bond atomRefs2='a1 h1' order='1'/>"
                    + "  </bondArray>" + "</molecule>";
            doc = builder.build(new StringReader(t01));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)-H
        try {
            // FIXME
            String t02 = "<molecule id='t02' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t02));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)(-H)-H
        try {
            String t03 = "<molecule id='t03' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t03));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C(-H)(-H)(-H)-H
        try {
            String t04 = "<molecule id='t04' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/><atom id='h4' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/><bond atomRefs2='a1 h4' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t04));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // one ligand
        // C#C-H
        try {
            String t11 = "<molecule id='t11' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='3'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t11));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.LINEAR, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C-O-H
        try {
            String t11a = "<molecule id='t11a' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='O' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8' y3='9' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t11a));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.TETRAHEDRAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C=C(-H)-H
        try {
            String t12 = "<molecule id='t12' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t12));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.TRIGONAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C-C(-H)(-H)-H
        try {
            String t13 = "<molecule id='t13' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='8.6' y3='10' z3='10'/><atom id='a3' elementType='C' x3='8.' y3='8.7' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/><atom id='h3' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a2 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/><bond atomRefs2='a1 h3' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t13));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
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
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t21));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.TRIGONAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-N-H
        // |
        // C
        try {
            String t21a = "<molecule id='t21a' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='N' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='2'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t21a));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.TETRAHEDRAL, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // C-C(-H)-H
        // |
        // C
        try {
            String t22 = "<molecule id='t22' "
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='9' z3='10'/><atom id='a3' elementType='C' x3='9.2' y3='11' z3='10'/><atom id='h1' elementType='H'/><atom id='h2' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='2'/><bond atomRefs2='a1 h1' order='1'/><bond atomRefs2='a1 h2' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t22));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
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
                    + CML_XMLNS
                    + ">"
                    + "<atomArray><atom id='a1' elementType='C' x3='10' y3='10' z3='10'/><atom id='a2' elementType='C' x3='9.2' y3='10.8' z3='10.8'/><atom id='a3' elementType='C' x3='10.8' y3='10.8' z3='9.2'/><atom id='a4' elementType='C' x3='10.8' y3='9.2' z3='10.8'/><atom id='h1' elementType='H'/></atomArray><bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 a4' order='1'/><bond atomRefs2='a1 h1' order='1'/></bondArray></molecule>";
            doc = builder.build(new StringReader(t31));
            CMLMolecule mol = (CMLMolecule) doc.getRootElement();
            MoleculeTool moleculeTool = new MoleculeTool(mol);
            List<CMLAtom> atoms = mol.getAtoms();
            CMLAtomSet as1 = moleculeTool.calculate3DCoordinatesForLigands(
                    atoms.get(0), Molutils.DEFAULT, 1.1, 1.9);
            Assert.assertNotNull("atomset should not be null", as1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.adjustHydrogenCountsToValency(String)'
     */
    @Test
    public void testAdjustHydrogenCountsToValency() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        StringTestBase.assertEquals("calculated bonds",
                new String[] { "a1", "a2" }, mol5.getBonds().get(0)
                        .getAtomRefs2());
        StringTestBase.assertEquals("calculated bonds",
                new String[] { "a1", "a4" }, mol5.getBonds().get(1)
                        .getAtomRefs2());
        StringTestBase.assertEquals("calculated bonds",
                new String[] { "a1", "a5" }, mol5.getBonds().get(2)
                        .getAtomRefs2());
        StringTestBase.assertEquals("calculated bonds",
                new String[] { "a2", "a3" }, mol5.getBonds().get(3)
                        .getAtomRefs2());
        List<CMLBond> bonds = mol5.getBonds();
        moleculeTool5.calculateBondOrdersFromXYZ3();
        Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
        Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
        Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
        Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
        moleculeTool5
                .adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_HYDROGEN_COUNT);
        CMLAtom a1 = mol5.getAtomById("a1");
        CMLAtom a2 = mol5.getAtomById("a2");
        CMLAtom a3 = mol5.getAtomById("a3");
        CMLAtom a4 = mol5.getAtomById("a4");
        Assert.assertEquals("a1 ", 2, a1.getHydrogenCount());
        Assert.assertNull("a2 ", a2.getHydrogenCountAttribute());
        Assert.assertEquals("a3 ", 0, a3.getHydrogenCount());
        Assert.assertNull("a4 ", a4.getHydrogenCountAttribute()); // this is a
        // hydrogen
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.expandImplicitHydrogens(String)'
     */
    @Test
    public void testExpandImplicitHydrogens() {
        makeMoleculeTool10();
        CMLAtom atom0 = mol10.getAtom(0);
        moleculeTool10.expandImplicitHydrogens(atom0,
                HydrogenControl.NO_EXPLICIT_HYDROGENS);
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
     * 'org.xmlcml.cml.element.CMLMolecule.setPreferredBondOrders()'
     */
    @Test
    public void testSetPreferredBondOrders() {
        makeMoleculeTool5();
        moleculeTool5.calculateBondedAtoms();
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        Assert.assertNull("initial order", mol5.getBonds().get(0).getOrder());
        mol5.setBondOrders(CMLBond.SINGLE_S);
        // note that getOrder() will return the preferred order
        Assert.assertEquals("updated order", CMLBond.SINGLE_S, mol5.getBonds()
                .get(0).getOrderAttribute().getValue());
        mol5.setNormalizedBondOrders();
        Assert.assertEquals("perferred order", CMLBond.SINGLE, mol5.getBonds()
                .get(0).getOrderAttribute().getValue());
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
        CMLElements<CMLAngle> angles = mol5.getAngleElements();
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
        CMLElements<CMLLength> lengths = mol5.getLengthElements();
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
        CMLElements<CMLTorsion> torsions = mol5.getTorsionElements();
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
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.removeOverlapping3DAtoms(CMLAtomSet
     * otherSet)'
     */
    @Test
    public void testRemoveOverlapping3DAtomsCMLAtomSet() {
        makeMol1();
        Assert.assertEquals("before remove overlap", 3, mol1.getAtomCount());
        makeMol2();
        Assert.assertEquals("before remove overlap", 3, mol2.getAtomCount());
        new MoleculeTool(mol1).removeOverlapping3DAtoms(mol2,
                CoordinateType.CARTESIAN);
        Assert.assertEquals("remove overlap", 1, mol1.getAtomCount());
        Assert.assertEquals("remove overlap", "a3", mol1.getAtom(0)
                .getId());
        Assert.assertEquals("remove overlap", 3, mol2.getAtomCount());
        Assert.assertEquals("remove overlap", "a13", mol2.getAtom(2)
                .getId());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.calculateBondedAtoms()'
     */
    @Test
    public void testCalculateBondedAtoms() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        List<CMLBond> bonds = mol5.getBonds();
        Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a2"), bonds
                .get(0).atomHash());
        Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a4"), bonds
                .get(1).atomHash());
        Assert.assertEquals("bond 0", CMLBond.atomHash("a1", "a5"), bonds
                .get(2).atomHash());
        Assert.assertEquals("bond 0", CMLBond.atomHash("a2", "a3"), bonds
                .get(3).atomHash());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.calculateBondOrdersFromXYZ3()'
     */
    @Test
    public void testCalculateBondOrdersFromXYZ3() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        List<CMLBond> bonds = mol5.getBonds();
        moleculeTool5.calculateBondOrdersFromXYZ3();
        Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
        Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
        Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
        Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLMolecule.calculateBondsFromXYZ3(double)'
     */
    @Test
    public void testCalculateBondsFromXYZ3() {
        makeMoleculeTool5();
        try {
            moleculeTool5.calculateBondedAtoms();
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        Assert.assertEquals("calculated bonds", 4, mol5.getBondCount());
        List<CMLBond> bonds = mol5.getBonds();
        moleculeTool5.calculateBondOrdersFromXYZ3();
        Assert.assertEquals("bond 0", "2", bonds.get(0).getOrder());
        Assert.assertEquals("bond 1", "1", bonds.get(1).getOrder());
        Assert.assertEquals("bond 2", "1", bonds.get(2).getOrder());
        Assert.assertEquals("bond 3", "A", bonds.get(3).getOrder());
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
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        double length = moleculeTool5
                .getAverageBondLength(CoordinateType.CARTESIAN);
        Assert.assertEquals("average length", 1.2235, length, .0001);
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
        Assert.assertEquals("map size", 4, map.getLinkElements().size());
        CMLLink link0 = map.getLinkElements().get(0);
        Assert.assertEquals("link0", "http://www.xml-cml.org/mols/geom", link0
                .getFrom());
        Assert.assertEquals("link0", "./geom.xml", link0.getTo());
        CMLLink link1 = map.getLinkElements().get(1);
        Assert.assertEquals("link1", "http://www.xml-cml.org/mols/frags", link1
                .getFrom());
        Assert.assertEquals("link1", "./fragments/frags.xml", link1.getTo());
    }

    static Catalog getMoleculeCatalog() throws IOException {
        Catalog catalogTool = null;
        catalogTool = new Catalog(Util
                .getResource(TOOL_MOLECULES_RESOURCE + U_S + CATALOG_XML));
        return catalogTool;
    }

    /**
     * test get map of molecules under namespace.
     * @throws IOException
     *
     */
    @Test
    @Ignore
    public void testGetMolecules() throws IOException {
        CMLNamespace namespace = new CMLNamespace(
        		"foo", "http://www.xml-cml.org/mols/frags");
        Catalog catalog = getMoleculeCatalog();
        Map<String, Indexable> map = null;
        try {
        	// FIXME
//            map = catalog.getIndexableList(namespace, IndexableList.Type.MOLECULE_LIST);
        } catch (CMLRuntimeException e) {
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

    /** test get molecule from namespaceRef.
     * @throws IOException
     */
    @Test
    @Ignore
    public void testGetMolecule() throws IOException {
        String molS = "<molecule " + CML_XMLNS + " ref='p:oh' "
                + "xmlns:p='http://www.xml-cml.org/mols/frags'/>";
        Catalog catalog = getMoleculeCatalog();
        CMLMolecule mol = null;
        try {
            mol = (CMLMolecule) new CMLBuilder().parseString(molS);
        } catch (Exception e) {
            neverThrow(e);
        }
//        MoleculeTool moleculeTool = new MoleculeTool(mol);
        CMLNamespace namespace = CMLNamespace.createNamespace("p", mol);
        CMLMolecule refMol = (CMLMolecule) catalog.getIndexable(
        		"p:oh", namespace, IndexableList.Type.MOLECULE_LIST);
        Assert.assertNotNull("refenced mol not null", refMol);
        Assert.assertEquals("refenced mol", "oh", refMol.getId());
    }

    /**
     * Test method for 'org.xmlcml.cml.tools.MoleculeTool.generateBondIds()'
     */
    @Test
    public void testGenerateBondIds() {
        makeMol9();
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        Assert.assertNull("no id", mol9.getBonds().get(0).getId());
        moleculeTool.generateBondIds();
        Assert.assertEquals("generated id", "a1_a2", mol9.getBonds().get(0).getId());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.createMolecule(CMLMolecule, String[])'
     */
    @Test
    public void testCreateMolecule() {
        makeMol1();
        CMLMolecule mol = MoleculeTool.createMolecule(mol1, 
                new String[]{"a1", "a3"});
        Assert.assertEquals("new", 2, mol.getAtomCount());
        Assert.assertEquals("new", "a3", mol.getAtom(1).getId());

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.addSuffixToAtomIDs(String)'
     */
    @Test
    public void testAddSuffixToAtomIDs() {
        makeMol1();
        MoleculeTool moleculeTool = new MoleculeTool(mol1);
        moleculeTool.addSuffixToAtomIDs("FOO");
        Assert.assertEquals("new", 3, mol1.getAtomCount());
        Assert.assertEquals("new", "a3FOO", mol1.getAtom(2).getId());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.adjustHydrogenCountsToValency(CMLAtom,
     * HydrogenControl)'
     */
    @Test
    public void testAdjustHydrogenCountsToValencyCMLAtomHydrogenControl() {
        makeMol9();
        Assert.assertEquals("new", 3, mol9.getAtomCount());
        Assert.assertEquals("new", 2, mol9.getBondCount());
        Assert.assertEquals("new", "a3", mol9.getAtom(2).getId());
        Assert.assertEquals("new", new String[]{"a2", "a3"},
                mol9.getBonds().get(1).getAtomRefs2());
        CMLAtom atom0 = mol9.getAtom(0);
        CMLAtom atom1 = mol9.getAtom(1);
        CMLAtom atom2 = mol9.getAtom(2);
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        moleculeTool.adjustHydrogenCountsToValency(atom0, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        Assert.assertEquals("new", 6, mol9.getAtomCount());
        Assert.assertEquals("new", 5, mol9.getBondCount());
        Assert.assertEquals("new", "a1_h2", mol9.getAtom(4).getId());
        Assert.assertEquals("new", new String[]{"a1", "a1_h2"},
                mol9.getBonds().get(3).getAtomRefs2());
        moleculeTool.adjustHydrogenCountsToValency(atom1, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        moleculeTool.adjustHydrogenCountsToValency(atom2, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        Assert.assertEquals("new", 10, mol9.getAtomCount());
        Assert.assertEquals("new", 9, mol9.getBondCount());
        Assert.assertEquals("new", "a3_h3", mol9.getAtom(9).getId());
        Assert.assertEquals("new", new String[]{"a3", "a3_h3"},
                mol9.getBonds().get(8).getAtomRefs2());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getNonHydrogenLigandList(CMLAtom)'
     */
    @Test
    public void testGetNonHydrogenLigandList() {
        MoleculeTool moleculeTool = makeCompleteMol9();
        CMLAtom atom0 = mol9.getAtom(0);
        CMLAtom atom1 = mol9.getAtom(1);
        List<CMLAtom> atomList = moleculeTool.getNonHydrogenLigandList(atom0);
        Assert.assertEquals("nonH", 1, atomList.size());
        atomList = moleculeTool.getNonHydrogenLigandList(atom1);
        Assert.assertEquals("nonH", 2, atomList.size());
    }
    
    private MoleculeTool makeCompleteMol9() {
        makeMol9();
        CMLAtom atom0 = mol9.getAtom(0);
        CMLAtom atom1 = mol9.getAtom(1);
        CMLAtom atom2 = mol9.getAtom(2);
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        moleculeTool.adjustHydrogenCountsToValency(atom0, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        moleculeTool.adjustHydrogenCountsToValency(atom1, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        moleculeTool.adjustHydrogenCountsToValency(atom2, HydrogenControl.REPLACE_HYDROGEN_COUNT);
        return moleculeTool;
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getDownstreamAtoms(CMLAtom, CMLAtom)'
     */
    @Test
    public void testGetDownstreamAtomsCMLAtomCMLAtomSetCMLAtom() {
        MoleculeTool moleculeTool = makeCompleteMol9();
        CMLAtom atom0 = mol9.getAtom(0);
        CMLAtom atom1 = mol9.getAtom(1);
        Assert.assertEquals("before", 10, mol9.getAtoms().size());
        CMLAtomSet downstreamAtoms = moleculeTool.getDownstreamAtoms(atom0, atom1);
        Assert.assertEquals("down", 4, downstreamAtoms.size());
        Assert.assertEquals("down", new String[]{"a1", "a1_h1", "a1_h2", "a1_h3"},
                downstreamAtoms.getXMLContent());
        downstreamAtoms = moleculeTool.getDownstreamAtoms(atom1, atom0);
        Assert.assertEquals("down", 6, downstreamAtoms.size());
        Assert.assertEquals("down", new String[]{"a2", "a3", "a3_h1", "a3_h2", "a3_h3", "a2_h1"},
                downstreamAtoms.getXMLContent());
    }

    /**
     * Test method for 'org.xmlcml.cml.tools.MoleculeTool.appendToId(CMLAtom,
     * String)'
     */
    @Test
    public void testAppendToId() {
        makeMol9();
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        CMLAtom atom = mol9.getAtom(1);
        Assert.assertEquals("id", "a2", atom.getId());
        moleculeTool.appendToId(atom, "XXX");
        Assert.assertEquals("id", "a2XXX", atom.getId());

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.calculate3DCoordinatesForLigands(CMLAtom,
     * int, double, double)'
     */
    @Test
    @Ignore
    public void testCalculate3DCoordinatesForLigands() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.expandImplicitHydrogens(CMLAtom,
     * HydrogenControl)'
     */
    @Test
    public void testExpandImplicitHydrogensCMLAtomHydrogenControl() {
        makeMol9();
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        Assert.assertEquals("before", 3, mol9.getAtomCount());
        Assert.assertEquals("before", 2, mol9.getBondCount());
        moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.NO_EXPLICIT_HYDROGENS);
        moleculeTool.expandImplicitHydrogens(HydrogenControl.NO_EXPLICIT_HYDROGENS);
        Assert.assertEquals("after", 10, mol9.getAtomCount());
        Assert.assertEquals("after", 9, mol9.getBondCount());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.addWedgeHatchBond(CMLAtom)'
     */
    @Test
    @Ignore
    public void testAddWedgeHatchBond() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getLoneElectronCount(CMLAtom)'
     */
    @Test
    public void testGetLoneElectronCount() {
        // FIXME
        CMLMolecule nitroMethane = (CMLMolecule) parseValidString(nitroMethaneS);
        MoleculeTool moleculeTool = new MoleculeTool(nitroMethane);
        int n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(0));
        Assert.assertEquals("lone pair", -6, n);
        n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(1));
        Assert.assertEquals("lone pair", 0, n);
        n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(2));
        Assert.assertEquals("lone pair", 6, n);
        n = moleculeTool.getLoneElectronCount(nitroMethane.getAtom(3));
        Assert.assertEquals("lone pair", 5, n);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getBondSet(CMLAtomSet)'
     */
    @Test
    public void testGetBondSet() {
        makeMol9();
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        moleculeTool.generateBondIds();
        CMLAtomSet atomSet = new CMLAtomSet(mol9);
        CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
        Assert.assertEquals("bonds", new String[]{"a1_a2", "a2_a3"},
                bondSet.getXMLContent());
        atomSet.removeAtomById("a3");
        bondSet = moleculeTool.getBondSet(atomSet);
        Assert.assertEquals("bonds", new String[]{"a1_a2"},
                bondSet.getXMLContent());
    }

    /**
     * Test method for 'org.xmlcml.cml.tools.MoleculeTool.sprout()'
     */
    @Test
    public void testSprout() {

        // self sprout
        MoleculeTool sproutTool = new MoleculeTool(sprout);
        CMLMolecule sproutMolecule = sproutTool.sprout();
        Assert
                .assertEquals("sprout AS size", 13, sproutMolecule
                        .getAtomCount());
        Assert
                .assertEquals("sprout BS size", 13, sproutMolecule
                        .getBondCount());

        // sub sprout
        List<CMLAtom> atoms = sprout.getAtoms();
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        atomList.add(atoms.get(0));
        atomList.add(atoms.get(1));
        CMLAtomSet subAtomSet = new CMLAtomSet(atomList);
        CMLMolecule subMolecule = sproutTool.sprout(subAtomSet);
        Assert.assertEquals("sub AS size", 6, subMolecule.getAtomCount());
        Assert.assertEquals("sub BS size", 5, subMolecule.getBondCount());

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getDownstreamAtoms(CMLBond, CMLAtom)'
     */
    @Test
    public void testGetDownstreamAtomsCMLBondCMLAtom() {
        MoleculeTool moleculeTool = makeCompleteMol9();
        CMLAtom atom0 = mol9.getAtom(0);
        CMLAtom atom1 = mol9.getAtom(1);
        CMLBond bond = mol9.getBond(atom0, atom1);
        CMLAtomSet downstreamAtoms = moleculeTool.getDownstreamAtoms(bond, atom1);
        Assert.assertEquals("down", 4, downstreamAtoms.size());
        Assert.assertEquals("down", new String[]{"a1", "a1_h1", "a1_h2", "a1_h3"},
                downstreamAtoms.getXMLContent());
        downstreamAtoms = moleculeTool.getDownstreamAtoms(bond, atom0);
        Assert.assertEquals("down", 6, downstreamAtoms.size());
        Assert.assertEquals("down", new String[]{"a2", "a3", "a3_h1", "a3_h2", "a3_h3", "a2_h1"},
                downstreamAtoms.getXMLContent());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.adjustHydrogenCountsToValency(HydrogenControl)'
     */
    @Test
    public void testAdjustHydrogenCountsToValencyHydrogenControl() {
           makeMol9();
            Assert.assertEquals("new", 3, mol9.getAtomCount());
            Assert.assertEquals("new", 2, mol9.getBondCount());
            Assert.assertEquals("new", "a3", mol9.getAtom(2).getId());
            Assert.assertEquals("new", new String[]{"a2", "a3"},
                    mol9.getBonds().get(1).getAtomRefs2());
            MoleculeTool moleculeTool = new MoleculeTool(mol9);
            moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.REPLACE_HYDROGEN_COUNT);
            Assert.assertEquals("new", 10, mol9.getAtomCount());
            Assert.assertEquals("new", 9, mol9.getBondCount());
            Assert.assertEquals("new", "a3_h3", mol9.getAtom(9).getId());
            Assert.assertEquals("new", new String[]{"a3", "a3_h3"},
                    mol9.getBonds().get(8).getAtomRefs2());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.contractExplicitHydrogens(HydrogenControl)'
     */
    @Test
    public void testContractExplicitHydrogensHydrogenControl() {
        MoleculeTool moleculeTool = makeCompleteMol9();
        Assert.assertEquals("before", 10, mol9.getAtomCount());
        Assert.assertEquals("before", 9, mol9.getBondCount());
        Assert.assertNotNull("before", mol9.getAtom(0).getHydrogenCountAttribute());
        Assert.assertNotNull("before", mol9.getAtom(1).getHydrogenCountAttribute());
        Assert.assertNotNull("before", mol9.getAtom(2).getHydrogenCountAttribute());
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
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.contractExplicitHydrogens(CMLAtom,
     * HydrogenControl)'
     */
    @Test
    public void testContractExplicitHydrogensCMLAtomHydrogenControl() {
        MoleculeTool moleculeTool = makeCompleteMol9();
        CMLAtom atom0 = mol9.getAtom(0);
        Assert.assertEquals("before", 10, mol9.getAtomCount());
        Assert.assertEquals("before", 9, mol9.getBondCount());
        Assert.assertNotNull("before", mol9.getAtom(0).getHydrogenCountAttribute());
        Assert.assertNotNull("before", mol9.getAtom(1).getHydrogenCountAttribute());
        Assert.assertNotNull("before", mol9.getAtom(2).getHydrogenCountAttribute());
        Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
        Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
        Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());
        
        moleculeTool.contractExplicitHydrogens(atom0,
            HydrogenControl.USE_EXPLICIT_HYDROGENS);
        Assert.assertEquals("before", 7, mol9.getAtomCount());
        Assert.assertEquals("before", 6, mol9.getBondCount());
        Assert.assertEquals("before", 3, mol9.getAtom(0).getHydrogenCount());
        Assert.assertEquals("before", 1, mol9.getAtom(1).getHydrogenCount());
        Assert.assertEquals("before", 3, mol9.getAtom(2).getHydrogenCount());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.expandImplicitHydrogens(HydrogenControl)'
     */
    @Test
    public void testExpandImplicitHydrogensHydrogenControl() {
        makeMol9();
        MoleculeTool moleculeTool = new MoleculeTool(mol9);
        mol9.getAtom(0).setHydrogenCount(3);
        mol9.getAtom(1).setHydrogenCount(1);
        mol9.getAtom(2).setHydrogenCount(3);
        Assert.assertEquals("before", 3, mol9.getAtomCount());
        Assert.assertEquals("before", 2, mol9.getBondCount());
        moleculeTool.expandImplicitHydrogens(HydrogenControl.NO_EXPLICIT_HYDROGENS);
        Assert.assertEquals("before", 10, mol9.getAtomCount());
        Assert.assertEquals("before", 9, mol9.getBondCount());

    }


    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getAtomSet(CMLBondSet)'
     */
    @Test
    public void testGetAtomSet() {
        makeMol5a();
        MoleculeTool moleculeTool = new MoleculeTool(mol5a);
//        CMLAtomSet atomSet = mol5a.getAtomSet();
        CMLBondSet bondSet = new CMLBondSet(mol5a);
        Assert.assertEquals("before", 5, mol5a.getAtomCount());
        Assert.assertEquals("before", 4, mol5a.getBondCount());
        bondSet.removeBond(mol5a.getBonds().get(1));
        CMLAtomSet newAtomSet = moleculeTool.getAtomSet(bondSet);
        Assert.assertEquals("before", new String[]{"a1", "a2", "a5", "a3"},
                newAtomSet.getXMLContent());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.calculateBondedAtoms(List<CMLAtom>)'
     */
    @Test
    @Ignore
    public void testCalculateBondedAtomsListOfCMLAtom() {

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
        } catch (CMLRuntimeException e) {
            Assert.fail("test bug " + e);
        }
        double length = moleculeTool5
                .getAverageBondLength(CoordinateType.CARTESIAN);
        Assert.assertEquals("average length", 1.2235, length, .0001);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.MoleculeTool.getSymmetryContacts(RealRange,
     * CrystalTool)'
     */
    @Test
    @Ignore
    public void testGetSymmetryContacts() {

    }
    
	/** copies attributes on bonds and atoms to another molecule.
	 * for each atom/bond in this.molecule finds Id and hence corresponding 
	 * atom/bond in 'to'. Copies all attributes from that atom to to.atom/@*
	 * If corresponding atom does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @exception CMLRuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
    @Test
	public void testCopyAtomAndBondAttributesById() {
		CMLMolecule from = new CMLMolecule();
		from.setId("from");
		CMLAtom atom0 = new CMLAtom();
		atom0.setElementType("C");
		atom0.setId("a0");
		from.addAtom(atom0);
		CMLAtom atom1 = new CMLAtom();
		atom1.setElementType("O");
		atom1.setId("a1");
		from.addAtom(atom1);
		CMLBond bond01 = new CMLBond(atom0, atom1);
		bond01.setId("b01");
		from.addBond(bond01);
		
		CMLMolecule to = (CMLMolecule) from.copy();
		to.setId("to");

		
		atom0.setXY2(new Real2(1.,2.));
		atom1.setFormalCharge(1);
		bond01.setOrder(CMLBond.DOUBLE);

		MoleculeTool fromTool = new MoleculeTool(from);
		boolean permitOverwrite = true;
		fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);
		
		to.setId("from"); // to allow comparison
		AbstractTest.assertEqualsCanonically("compare mols", from, to);
		
		permitOverwrite = false;
		try {
			fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);
			Assert.fail("Should fail on overwrite");
		} catch (CMLRuntimeException e) {
			Assert.assertTrue("cannot overwrite", e.getMessage().startsWith(
					"cannot overwrite attribute:"));
		}

		// mimic atomId mismatch
		atom0.resetId("resetId");
		try {
			fromTool.copyAtomAndBondAttributesById(to, permitOverwrite);
			Assert.fail("Should fail on atom mismatch");
		} catch (CMLRuntimeException e) {
			Assert.assertEquals("atom mismatch", "Cannot find target atom: resetId", e.getMessage());
		}
		
	}
    

 }
