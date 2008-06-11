package org.xmlcml.cml.element;

import static org.xmlcml.cml.base.CMLConstants.CML_NS;
import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.euclid.EuclidConstants.EPS;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_QUOT;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.getAssertFormat;
import static org.xmlcml.util.TestUtils.neverThrow;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Assert;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;

import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLSerializer;
import org.xmlcml.cml.base.DoubleSTAttribute;
import org.xmlcml.cml.element.CMLFormula.Sort;
import org.xmlcml.cml.element.CMLFormula.Type;
import org.xmlcml.euclid.test.DoubleTestBase;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.molutil.ChemicalElement.AS;
import org.xmlcml.util.TestUtils;


/**
 * test CMLFormula.
 *
 * @author pmr
 *
 */
public class CMLFormulaTest extends MoleculeAtomBondTest {

    // built in xom;
    CMLFormula xomForm1 = null;
    CMLFormula xomForm2 = null;
    CMLFormula xomForm3 = null;
    CMLFormula xomForm3a = null;
    CMLFormula xomForm3b = null;

    // read into xom;
    String xmlForm1S = "<formula id='f1' concise='C 2 H 2 O 2 Br 1' "
            + CML_XMLNS + " formalCharge='-1'/>" + S_EMPTY;

    Document xmlForm1Doc = null;

    CMLFormula xmlForm1 = null;
    static int count = 0;
	private CMLBuilder builder;

    /**
     * setup.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        if (count++ > 0) {
//            throw new CMLRuntimeException("SKIP");
        }
        super.setUp();

        // build from scratch
        xomForm1 = new CMLFormula();
//        System.out.println("?????????????");
        xomForm1.add(AS.H.value, 1.0);
//        System.out.println("#############");
        xomForm1.add(AS.O.value, 3.0);
//        System.out.println("@@@@@@@@@@@@@");
        xomForm1.add(AS.N.value, 1.0);
        xomForm1.setCount(2.0);
        xomForm1.setId("xomForm1");
        // build from concise
        xomForm2 = new CMLFormula();
        xomForm2.setConcise("H 1 N 1 O 3");
        xomForm2.setId("xomForm2");

        // read reference moelcule

        builder = new CMLBuilder();
		try {
            xmlForm1Doc = builder.build(new StringReader(xmlForm1S));
        } catch (IOException e) {
            Assert.fail("Should not throw IOException");
        } catch (ParsingException e) {
            e.printStackTrace();
            logger.severe("Parse exception " + e.getMessage());
            Assert.fail("Should not throw ParsingException" + e.getCause());
        }
        xmlForm1 = (CMLFormula) xmlForm1Doc.getRootElement();
        // BUG the copy constructor adds spurious children!
        xomForm3 = new CMLFormula();
        xomForm3a = new CMLFormula();
        xomForm3a.setId("xomForm3a");
        String expectS = "<?xml version='1.0' encoding='UTF-8'?>"
                + (char) 13 + (char) 10 + "<formula id='xomForm3a'  "
                + CML_XMLNS + "/>";
        Element expectElem = parseValidString(expectS);
        TestUtils.assertEqualsCanonically("formula setup", expectElem, xomForm3a);

        xomForm3a.setConcise("H 2 S 1 O 4");
        expectS = "<?xml version='1.0' encoding='UTF-8'?>" + (char) 13
                + (char) 10
                + "<formula id='xomForm3a' concise='H 2 O 4 S 1'" + S_SPACE
                + CML_XMLNS + ">"
                + "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>"
                + "</formula>";
        expectElem = parseValidString(expectS);
        TestUtils.assertEqualsCanonically("formula setup", expectElem, xomForm3a);
        Assert.assertEquals("xom3a child", 1, xomForm3a.getChildCount());
        CMLFormula xomForm3aCopy = new CMLFormula(xomForm3a);
        Assert.assertEquals("xom3a children", 1, xomForm3aCopy
                .getAtomArrayElements().size());
        // should be
        expectS = "<?xml version='1.0' encoding='UTF-8'?>" + (char) 13
                + (char) 10
                + "<formula id='xomForm3aaa' concise='H 2 O 4 S 1'" + S_SPACE
                + CML_XMLNS + ">"
                + "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>"
                + "</formula>";
        // but is
        expectS = "<?xml version='1.0' encoding='UTF-8'?>" + (char) 13
                + (char) 10
                + "<formula id='xomForm3a' concise='H 2 O 4 S 1'" + S_SPACE
                + CML_XMLNS + ">"
                + "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>"
                + "</formula>";

        expectElem = parseValidString(expectS);

        TestUtils.assertEqualsCanonically("formula setup", expectElem, xomForm3aCopy);

        xomForm3.appendChild(xomForm3a);
        xomForm3b = new CMLFormula();
        xomForm3b.setConcise("Na 1 H 1 C 1 O 3");
        xomForm3.appendChild(xomForm3b);
    }

    /**
     * Assert.asserts equality of double arrays.
     *
     * checks for non-null, then equality of length, then individual elements
     *
     * @param message
     * @param formula1 expected formula
     * @param formula2 actual formula
     * @param eps tolerance for agreement
     */
    public static void assertEqualsConcise(String message, CMLFormula formula1,
            CMLFormula formula2, double eps) {
        if (formula1 == null) {
            Assert.fail(getAssertFormat(message, "formula", "null"));
        }
        if (formula2 == null) {
            Assert.fail(getAssertFormat(message, "formula", "null"));
        }
        Assert.assertEquals("equal concise", true, formula1.equals(formula2,
                eps));
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.copy()'
     */
    @Test
    public void testCopy() {
        Node copy = xmlForm1.copy();
        Assert.assertEquals("class should be CMLform: ", copy.getClass(),
                CMLFormula.class);
        CMLFormula copyForm = (CMLFormula) copy;
        Assert.assertEquals("formula is identical", copyForm
                .compareTo(xmlForm1), 0);
    }

    /**
     * test prohibition of formulaArray child.
     *
     */
    @Test
    public void testNoAtomArray() {
        // read into xom;
        String xmlForm1S = "<formula id='f1' " + CML_XMLNS + ">"
                + "  <atomArray>"
                + "    <atom id='a2' elementType='O' count='3'/>"
                + "  </atomArray>" + "</atom>" + S_EMPTY;

        try {
            builder.build(new StringReader(xmlForm1S));
            Assert
                    .fail("Should throw ParsingException due to forbidden atomArray child");
        } catch (IOException e) {
            Assert.fail("Should not throw IOException");
        } catch (ParsingException e) {
            Assert.assertEquals("ok", "ok");
        }
    }

    /* public */void /* test */Serialize() {
        /*-- cannot really test this as order of output may vary
         CMLSerializer serializer;
         String s;
         String expect;

         serializer = new CMLSerializer();
         s = serializer.getXML(xomForm1).trim();
         expect =
         "<?xml version='1.0' encoding='UTF-8'?>"+(char)13+(char)10+
         "<formula concise='H 1 N 1 O 3' count='2.0' xmlns=\S_EMPTY+CML_NS+"'>" +
         "<atomArray elementType='H O N' count='1.0 3.0 1.0'/>" +
         "</formula>";
         Assert.assertEquals("xom1 serializer2", expect, s);
         xomForm1.add(AS.H.value, 7.0);

         serializer = new CMLSerializer();
         s = serializer.getXML(xomForm1).trim();
         expect =
         "<?xml version='1.0' encoding='UTF-8'?>"+(char)13+(char)10+
         "<formula concise='H 8 O 3 N 1' count='2.0' xmlns=\S_EMPTY+CML_NS+"'>" +
         "<atomArray elementType='H O N' count='8.0 3.0 1.0'/>" +
         "</formula>";
         expect =
         "<?xml version='1.0' encoding='UTF-8'?>"+(char)13+(char)10+
         "<formula concise='H 8 N 1 O 3' count='2.0' xmlns=\S_EMPTY+CML_NS+"'>" +
         "<atomArray elementType='H N O' count='8.0 1.0 3.0'/>" +
         "<atomArray elementType='H O N' count='8.0 3.0 1.0'/>" +
         "</formula>";
         Assert.assertEquals("xom1 serializer2", expect, s);

         serializer = new CMLSerializer();
         s = serializer.getXML(xomForm3).trim();
         expect =
         "<?xml version='1.0' encoding='UTF-8'?>"+(char)13+(char)10+
         "<formula xmlns=\S_EMPTY+CML_NS+"'>" +
         "<formula concise='H 2 O 4 S 1'>" +
         "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>" +
         "</formula>" +
         "</formula>";
         expect =
         "<?xml version='1.0' encoding='UTF-8'?>"+(char)13+(char)10+
         "<formula xmlns=\S_EMPTY+CML_NS+"'>" +
         "<formula concise='H 2 O 4 S 1'>" +
         "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>" +
         "<atomArray elementType='H O S' count='2.0 4.0 1.0'/>" +
         "</formula>" +
         "<formula concise='C 1 H 1 Na 1 O 3'>" +
         "<atomArray elementType='C H Na O' count='1.0 1.0 1.0 3.0'/>" +
         "<atomArray elementType='C H Na O' count='1.0 1.0 1.0 3.0'/>" +
         "</formula>" +
         "</formula>";
         Assert.assertEquals("xom1 serializer2", expect, s);
         --*/
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getCount()'
     */
    @Test
    public void testGetCount() {
        double count = xomForm1.getCount();
        Assert.assertEquals("xomForm1 count", 2.0, count, 0.00001);

        count = xmlForm1.getCount();
        Assert.assertEquals("xmlForm1 count", 1.0, count, 0.00001);
    }
    
    /**
     * 
     */
    @Test
    public void testAddHydrogensIfExplicitAndPresent() {
    	CMLMolecule molecule;
    	CMLAtom atom;
    	CMLBond bond;
    	CMLAtom hatom;
    	CMLFormula formula;
    	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setHydrogenCount(4);
    	atom.setElementType("C");
    	for (int i = 0; i < 4; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+1));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    		bond = new CMLBond(atom, hatom);
    		molecule.addBond(bond);
    	}
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methane - explicit and count", "C 1 H 4", formula.getConcise());
    	
// test with only hydrogen count    	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setHydrogenCount(4);
    	atom.setElementType("C");
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methane - count", "C 1 H 4", formula.getConcise());

// some explicit H    	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("C");
    	atom.setHydrogenCount(4);
    	for (int i = 0; i < 2; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+1));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    		bond = new CMLBond(atom, hatom);
    		molecule.addBond(bond);
    	}
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methane - 2 explicit + count - carbon first", "C 1 H 4", formula.getConcise());
 
    	// methanol   	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("O");
    	CMLAtom atom2 = new CMLAtom();
    	atom2.setId("h1");
    	molecule.addAtom(atom2);
    	atom2.setElementType("H");
    	bond = new CMLBond(atom,atom2);
    	molecule.addBond(bond);
    	atom2 = new CMLAtom();
    	atom2.setId("a2");
    	molecule.addAtom(atom2);
    	atom2.setElementType("C");
    	atom2.setHydrogenCount(3);
    	bond =new CMLBond(atom, atom2);
    	molecule.addBond(bond);
    	
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methanol - oxygen first - ", "C 1 H 4 O 1", formula.getConcise());

    	molecule = new CMLMolecule();
    	for (int i = 0; i < 2; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+1));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    	}
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("C");
    	atom.setHydrogenCount(4);
    	for (int i = 0; i < 2; i++) {
    		hatom = molecule.getAtomById("h"+(i+1));
    		bond = new CMLBond(atom, hatom);
    		molecule.addBond(bond);
    	}
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methane - 2 explicit + count - hydrogens first - ", "C 1 H 4", formula.getConcise());
    	
// no hydrogen count    	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("C");
    	for (int i = 0; i < 4; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+1));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    		bond = new CMLBond(atom, hatom);
    		molecule.addBond(bond);
    	}
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methane - explicit", "C 1 H 4", formula.getConcise());
    	
    	
    	// inconsistent    	
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("C");
    	atom.setHydrogenCount(2);
    	for (int i = 0; i < 4; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+1));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    		bond = new CMLBond(atom, hatom);
    		molecule.addBond(bond);
    	}
    	formula = new CMLFormula(molecule);
    	formula.debug("BAD");
    	Assert.assertEquals("methane - all H explicit but count = 2", "C 1 H 2", formula.getConcise());

    	// methanol   	
    	molecule = new CMLMolecule();
    	atom2 = new CMLAtom();
    	atom2.setId("a2");
    	molecule.addAtom(atom2);
    	atom2.setElementType("C");
    	for (int i = 0; i < 3; i++) {
    		hatom = new CMLAtom();
    		hatom.setId("h"+(i+2));
    		hatom.setElementType("H");
    		molecule.addAtom(hatom);
    		bond = new CMLBond(atom2, hatom);
    		molecule.addBond(bond);
    	}
    	atom = new CMLAtom();
    	atom.setId("a1");
    	molecule.addAtom(atom);
    	atom.setElementType("O");
       	bond =new CMLBond(atom, atom2);
    	molecule.addBond(bond);
    	atom2 = new CMLAtom();
    	atom2.setId("h1");
    	molecule.addAtom(atom2);
    	atom2.setElementType("H");
    	bond = new CMLBond(atom,atom2);
    	molecule.addBond(bond);
    	
    	formula = new CMLFormula(molecule);
    	Assert.assertEquals("methanol - carbon first, explicit Hs- ", "C 1 H 4 O 1", formula.getConcise());
    	
    	// hydrogen molecule
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("h1");
    	molecule.addAtom(atom);
    	atom.setElementType("H");
    	atom2 = new CMLAtom();
    	atom2.setId("h2");
    	molecule.addAtom(atom2);
    	atom2.setElementType("H");
    	bond = new CMLBond(atom, atom2);
    	molecule.addBond(bond);
    	
    	formula = new CMLFormula(molecule);
       	Assert.assertEquals("hydrogen molecule - ", "H 2", formula.getConcise());
   	
       	// hydrogen chloride
    	molecule = new CMLMolecule();
    	atom = new CMLAtom();
    	atom.setId("h1");
    	molecule.addAtom(atom);
    	atom.setElementType("H");
    	atom.setFormalCharge(1);
    	atom2 = new CMLAtom();
    	atom2.setId("h2");
    	molecule.addAtom(atom2);
    	atom2.setElementType("Cl");
    	atom2.setFormalCharge(-1);
    	formula = new CMLFormula(molecule);
       	Assert.assertEquals("hydrogen chloride - ", "H 1 Cl 1", formula.getConcise());
   	
    }	

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.setCount(double)'
     */
    @Test
    public void testSetCount() {
        double cc = xmlForm1.getCount();
        Assert.assertEquals("count", 1.0, cc, 0.0000001);
        xmlForm1.setCount(1.5);
        cc = xmlForm1.getCount();
        Assert.assertEquals("count", 1.5, cc);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getFormalCharge()'
     */
    @Test
    public void testGetFormalCharge() {
        int fc = xmlForm1.getFormalCharge();
        Assert.assertEquals("formal charge", -1, fc);
        xmlForm1.setFormalCharge(2);
        fc = xmlForm1.getFormalCharge();
        Assert.assertEquals("formal charge", 2, fc);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.setFormalCharge(int)'
     */
    @Test
    public void testSetFormalCharge() {
        int fc = xmlForm1.getFormalCharge();
        Assert.assertEquals("formal charge", -1, fc);
        xmlForm1.setFormalCharge(-3);
        fc = xmlForm1.getFormalCharge();
        Assert.assertEquals("formal charge", -3, fc);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.CMLFormula()'
     */
    @Test
    public void testCMLFormula() {
        CMLFormula formula = new CMLFormula();
        Assert.assertNotNull("constructor ", formula);
        Assert.assertNull("no id attribute", formula.getIdAttribute());
        Assert.assertEquals("no children", formula.getChildCount(), 0);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.CMLFormula(CMLFormula)'
     */
    @Test
    public void testCMLFormulaCMLFormula() {
        // copy constructor
        CMLFormula xformula = xomForm1;
        CMLAttribute countAtt = xomForm1.getCountAttribute();
        Assert.assertNotNull("count attribute", countAtt);
        Assert.assertTrue("count class is subclass of CMLAttribute",
                CMLAttribute.class.isAssignableFrom(countAtt.getClass()));
        CMLFormula formula = new CMLFormula(xformula);
        Assert.assertNotNull("constructor ", formula);

        countAtt = formula.getCountAttribute();
        Assert.assertNotNull("copied count attribute", countAtt);
        Assert.assertTrue("count class is subclass of CMLAttribute",
                CMLAttribute.class.isAssignableFrom(countAtt.getClass()));
        Assert.assertEquals("count class is DoubleSTAttribute", countAtt
                .getClass(), DoubleSTAttribute.class);
        Assert.assertEquals("count value", formula.getCount(), xformula
                .getCount());
        // FIXME?
//        TestUtils.assertEqualsCanonically("compare Formula", formula, xformula);

        CMLFormula copyForm = new CMLFormula(xmlForm1);
        assertEqualsCanonically("compare Formula", copyForm, xmlForm1);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.createFormula(String)'
     */
    @Test
    public void testCreateFormulaString() {
        String s;
        CMLFormula form = null;
        s = "H 2 O 4 S 1";
        try {
            form = CMLFormula.createFormula(s);
        } catch (CMLRuntimeException e) {
            Assert.fail("parsing shouldn't fail for: " + s + " because:" + e);
        }
        Assert.assertEquals("formula string", "H 2 O 4 S 1", form.getConcise());
        s = "H2O4S";
        form = CMLFormula.createFormula(s);
        Assert.assertEquals("formula string", "H 2 O 4 S 1", form.getConcise());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.createFormula(String,
     * String)'
     */
    @Test
    public void testCreateFormulaStringConvention() {

        // CMLFormula.createFormula("C 2 H 4", Formula.Convention.)
        CMLFormula form = null;
        try {
            form = CMLFormula.createFormula("C2H4",
                    CMLFormula.Type.NOPUNCTUATION);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
             neverThrow(e);
        }
        // Assert.assertEquals("createFormula", "C 2 H 4", form.getConcise());
        try {
            form = CMLFormula.createFormula("C 2 H 4",
                    CMLFormula.Type.ELEMENT_WHITESPACE_COUNT);
        } catch (CMLRuntimeException e) {
            neverThrow(e);
        }
        Assert.assertEquals("createFormula", "C 2 H 4", form.getConcise());
        try {
            form = CMLFormula.createFormula("C2 H4",
                    CMLFormula.Type.ELEMENT_COUNT_WHITESPACE);
        } catch (CMLRuntimeException e) {
            neverThrow(e);
        }
        Assert.assertEquals("createFormula", "C 2 H 4", form.getConcise());
        Assert.assertEquals("createFormula", 1.0, form.getCount(), EPS);
        try {
            form = CMLFormula.createFormula("2(C2 H4)",
                    CMLFormula.Type.MULTIPLIED_ELEMENT_COUNT_WHITESPACE);
        } catch (CMLRuntimeException e) {
            neverThrow(e);
        }
        Assert.assertEquals("createFormula", "C 2 H 4", form.getConcise());
        Assert.assertEquals("createFormula", 2.0, form.getCount(), EPS);
        /*--
         try {
         form = CMLFormula.createFormula("3(C 2 H 4) 2(H 2 O 1)", CMLFormula.Type.NESTEDBRACKETS);
         } catch (CMLRuntime e) {
         neverThrow(e);
         } catch (CMLRuntimeException e) {
         neverThrow(e);
         }
         Assert.assertEquals("createFormula", "C 2 H 4", form.getConcise());
         Assert.assertEquals("createFormula", 2.0, form.getCount(), EPS);
         --*/
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.createFromString(String, String)'
     */
    @Test
    public void testCreateFromString() {

        String moiety = "C2 H6 N +, H1 S2 O4 -";
        CMLFormula[] f = new CMLFormula[2];
        try {
            f[0] = CMLFormula.createFormula("NC2H6");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        f[0].setFormalCharge(1);
        try {
            f[1] = CMLFormula.createFormula("S2O4H");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        f[1].setFormalCharge(-1);

        CMLFormula fMoiety = null;
        try {
            fMoiety = CMLFormula.createFormula(moiety, Type.MOIETY);
        } catch (CMLRuntimeException e) {
            e.printStackTrace();
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("moiety count", 2, fMoiety.getChildCount());
        for (int i = 0; i < 2; i++) {
            CMLFormula mf = (CMLFormula) fMoiety.getChild(i);
            Assert.assertTrue("moiety " + i, f[i].equals(mf, 0.0001));
        }
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.add(String, double)'
     */
    @Test
    public void testAdd() {
        // FIXME - sorting
        String[] elements = xomForm1.getElementTypes();
        StringTestBase.assertEquals("start xomForm1",
                new String[] { AS.H.value, AS.O.value, AS.N.value }, elements);
        double[] counts = xomForm1.getCounts();
        DoubleTestBase.assertEquals("start xomForm1",
                new double[] { 1.0, 3.0, 1.0 }, counts, 0.000001);
        xomForm1.add(AS.S.value, 2.0);
        elements = xomForm1.getElementTypes();
        StringTestBase.assertEquals("add1 xomForm1", new String[] { AS.H.value, AS.O.value, AS.N.value,
                AS.S.value }, elements);
        counts = xomForm1.getCounts();
        DoubleTestBase.assertEquals("add1 xomForm1", new double[] { 1.0, 3.0, 1.0,
                2.0 }, counts, 0.000001);
        xomForm1.add(AS.H.value, 2.0);
        elements = xomForm1.getElementTypes();
        StringTestBase.assertEquals("add1 xomForm1", new String[] { AS.H.value, AS.O.value, AS.N.value,
                AS.S.value }, elements);
        counts = xomForm1.getCounts();
        DoubleTestBase.assertEquals("add1 xomForm1", new double[] { 3.0, 3.0, 1.0,
                2.0 }, counts, 0.000001);

        xomForm1.setFormalCharge(-1);
        String concise = xomForm1.getConcise();
        Assert.assertEquals("xom in sync", "H 3 N 1 O 3 S 2 -1", concise);
        int fc = xomForm1.getFormalCharge();
        Assert.assertEquals("formal charge from concise", -1, fc);
        xomForm1.setFormalCharge(1);
        concise = xomForm1.getConcise();
        Assert.assertEquals("xom in sync", "H 3 N 1 O 3 S 2 1", concise);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getElementTypes()'
     */
    @Test
    public void testGetElementTypes() {
        String[] el = xomForm1.getElementTypes();
        StringTestBase.assertEquals("element types",
                new String[] { AS.H.value, AS.O.value, AS.N.value }, el);

        el = xmlForm1.getElementTypes();
        StringTestBase.assertEquals("element types", new String[] { AS.C.value, AS.H.value, AS.Br.value,
                AS.O.value }, el);

    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getElementCounts()'
     */
    @Test
    public void testGetElementCounts() {
        double[] c = xomForm1.getCounts();
        DoubleTestBase.assertEquals("element counts", new double[] { 1., 3., 1. },
                c, 0.00001);

        String concise = xmlForm1.getConcise();
        Assert.assertEquals("concise", "C 2 H 2 Br 1 O 2 -1", concise);
        c = xmlForm1.getCounts();
        DoubleTestBase.assertEquals("element counts",
                new double[] { 2., 2., 1., 2. }, c, 0.00001);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.addFormula(CMLFormula)'
     */
    @Test
    public void testAddFormulaCMLFormula() {
        CMLSerializer serializer = new CMLSerializer();
        String s = serializer.getXML(xomForm3a).trim();
        String expect = "<?xml version="+S_QUOT+"1.0"+S_QUOT+" encoding="+S_QUOT+"UTF-8"+S_QUOT+"?>"
                + (char) 13 + (char) 10
                + "<formula id="+S_QUOT+"xomForm3a"+S_QUOT+" concise="+S_QUOT+"H 2 O 4 S 1"+S_QUOT+" "
                + "xmlns="+S_QUOT+"" + CML_NS + ""+S_QUOT+">"
                + "<atomArray elementType="+S_QUOT+"H O S"+S_QUOT+" count="+S_QUOT+"2.0 4.0 1.0"+S_QUOT+"/>"
                + "</formula>";
        Assert.assertEquals("xom3a serializer", expect, s);
        Assert.assertEquals("child count", 1, xomForm3a.getChildCount());
        Assert.assertEquals("child count", 1, xomForm3b.getChildCount());
        Assert.assertEquals("concise", "H 2 O 4 S 1", xomForm3a.getConcise());
        Assert.assertEquals("concise", "C 1 H 1 Na 1 O 3", xomForm3b
                .getConcise());
        serializer = new CMLSerializer();
        s = serializer.getXML(xomForm3a).trim();
        expect = "<?xml version="+S_QUOT+"1.0"+S_QUOT+" encoding="+S_QUOT+"UTF-8"+S_QUOT+"?>" + (char) 13
                + (char) 10
                + "<formula id="+S_QUOT+"xomForm3a"+S_QUOT+" concise="+S_QUOT+"H 2 O 4 S 1"+S_QUOT+" "
                + "xmlns="+S_QUOT+"" + CML_NS + ""+S_QUOT+">"
                + "<atomArray elementType="+S_QUOT+"H O S"+S_QUOT+" count="+S_QUOT+"2.0 4.0 1.0"+S_QUOT+"/>"
                + "</formula>";
        Assert.assertEquals("xom3a serializer", expect, s);
        // now add the formula
        xomForm3a.addFormula(xomForm3b);
        Assert.assertEquals("child count", 2, xomForm3a.getChildCount());
        CMLElements<CMLFormula> childFormula = xomForm3a.getFormulaElements();
        Assert.assertEquals("formula child count", 2, childFormula.size());
        Assert.assertEquals("formula 0 concise", "H 2 O 4 S 1", childFormula
                .get(0).getConcise());
        Assert.assertEquals("formula 1 concise", "C 1 H 1 Na 1 O 3",
                childFormula.get(1).getConcise());
        serializer = new CMLSerializer();
        s = serializer.getXML(xomForm3a).trim();
        expect = "<?xml version="+S_QUOT+"1.0"+S_QUOT+" encoding="+S_QUOT+"UTF-8"+S_QUOT+"?>"
                + (char) 13
                + (char) 10
                + "<formula id="+S_QUOT+"xomForm3a"+S_QUOT+" "
                + "xmlns="+S_QUOT+""
                + CML_NS
                + ""+S_QUOT+">"
                + "<formula id="+S_QUOT+"xomForm3a"+S_QUOT+" concise="+S_QUOT+"H 2 O 4 S 1"+S_QUOT+">"
                + "<atomArray elementType="+S_QUOT+"H O S"+S_QUOT+" count="+S_QUOT+"2.0 4.0 1.0"+S_QUOT+"/>"
                + "</formula>"
                + "<formula concise="+S_QUOT+"C 1 H 1 Na 1 O 3"+S_QUOT+">"
                + "<atomArray elementType="+S_QUOT+"C H Na O"+S_QUOT+" count="+S_QUOT+"1.0 1.0 1.0 3.0"+S_QUOT+"/>"
                + "</formula>" + "</formula>";
        Assert.assertEquals("xom3a serializer", expect, s);

        // appended formula should be unaltered
        Assert.assertEquals("child count", 1, xomForm3b.getChildCount());
        Assert.assertEquals("concise", "C 1 H 1 Na 1 O 3", xomForm3b
                .getConcise());
        Assert.assertNull("concise", xomForm3a.getConcise());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getAggregateFormula()'
     */
    @Test
    public void testGetAggregateFormula() {
        CMLFormula f = xmlForm1.getAggregateFormula();
        Assert.assertEquals("aggregate formula count", 1.0, f.getCount());
        Assert.assertEquals("form3 children", 2, xomForm3.getChildCount());
        f = xomForm3.getAggregateFormula();

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.aggregateFormula(CMLFormula)'
     */
    @Test
    public void testAggregateFormula() {
        CMLFormula formula1 = null;
        try {
            formula1 = CMLFormula.createFormula("H 1 S 1 O 3",
                    CMLFormula.Type.ELEMENT_WHITESPACE_COUNT);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        formula1.setCount(3);
        formula1.setFormalCharge(-1);
        Assert.assertEquals("formula1 concise ", "H 1 O 3 S 1 -1", formula1
                .getConcise());
        Assert.assertEquals("formula1 charge ", -1, formula1.getFormalCharge());
        Assert.assertEquals("formula1 count ", 3, formula1.getCount(), EPS);

        CMLFormula formula2 = null;
        try {
            formula2 = CMLFormula.createFormula("Mg 1 O 6 H 12",
                    CMLFormula.Type.ELEMENT_WHITESPACE_COUNT);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        formula2.setFormalCharge(2);
        Assert.assertEquals("formula2 concise ", "H 12 Mg 1 O 6 2", formula2
                .getConcise());
        Assert.assertEquals("formula2 charge ", 2, formula2.getFormalCharge());
        Assert.assertEquals("formula2 count ", 1, formula2.getCount(), EPS);

        CMLFormula formula3 = formula1.createAggregatedFormula(formula2);
        Assert.assertEquals("formula3 concise ", "H 15 Mg 1 O 15 S 3 -1",
                formula3.getConcise());
        Assert.assertEquals("formula3 charge ", -1, formula3.getFormalCharge());
        Assert.assertEquals("formula3 count ", 1, formula3.getCount(), EPS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.divideBy(CMLFormula)'
     */
    @Test
    public void testDivideBy() {

        CMLFormula fTop = null;
        try {
            fTop = CMLFormula.createFormula("NC2H6");
        } catch (CMLRuntimeException e) {
        	e.printStackTrace();
            Assert.fail("should not throw " + e);
        }
        fTop.setFormalCharge(1);
        CMLFormula fBot = null;
        try {
            fBot = CMLFormula.createFormula("N2C4H12");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        fBot.setFormalCharge(2);
        double d = fTop.divideBy(fBot, 0.0001);
        Assert.assertEquals("divide top by bottom", 0.5, d, 0.0001);
        d = fBot.divideBy(fTop, 0.0001);
        Assert.assertEquals("divide top by bottom", 2.0, d, 0.0001);
        try {
            fBot = CMLFormula.createFormula("N2C4.1H12");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        d = fBot.divideBy(fTop, 0.0001);
        Assert.assertTrue("cannot divide top by bottom", Double.isNaN(d));

        try {
            fTop = CMLFormula.createFormula("Al10.0O20.Si1.0000Sr3.");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            fBot = CMLFormula.createFormula("Al2.5O5.Si0.25Sr0.75");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        d = fBot.divideBy(fTop, 0.0001);
        Assert.assertEquals("divide top by bottom", 0.25, d, 0.0001);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getCalculatedMolecularMass()'
     */
    @Test
    public void testGetCalculatedMolecularMass() {
        double m = xomForm1.getCalculatedMolecularMass();
        Assert.assertEquals("xomForm1 mw", 126.02568, m, 0.00001);

    }

    /**
     * Get formatted formula.
     *  @ param convention
     * <pre>
     *      NOPUNCTUATION			&quot;C2H4Cl2&quot; (default)
     *      ELEMENT_COUNT_WHITESPACE	&quot;C2 H4 Cl2&quot;
     *      ELEMENT_WHITESPACE_COUNT	&quot;C 2 H 4 Cl 2&quot;
     *      NESTEDBRACKETS			&quot;(Na2)(SO4).10(H2O)&quot;
     * </pre> @ param sort
     *
     * <pre>
     *      ALPHABETIC_ELEMENTS		&quot;Br C Cl H&quot;
     *      CHFIRST			=	&quot;C H Br Cl&quot;; (default)
     * </pre> @ param omitCount1 omit elements count if 1 (default false) @ return
     *        String the formatted formula
     */
    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getFormattedString(String, String,
     * boolean)'
     *
     */
    @Test
    public void testGetFormattedStringStringStringBoolean() {
        String s = xmlForm1.getFormattedString(Type.NOPUNCTUATION,
                Sort.CHFIRST, true);
        Assert.assertEquals("no punct, chfirst, omit1", "C2H2BrO2-", s);
        s = xmlForm1
                .getFormattedString(Type.NOPUNCTUATION, Sort.CHFIRST, false);
        Assert.assertEquals("no punct, chfirst, omit1", "C2H2Br1O2-", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_COUNT_WHITESPACE,
                Sort.CHFIRST, true);
        Assert.assertEquals("no punct, chfirst, omit1", "C2 H2 Br O2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_COUNT_WHITESPACE,
                Sort.CHFIRST, false);
        Assert.assertEquals("no punct, chfirst, omit1", "C2 H2 Br1 O2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_WHITESPACE_COUNT,
                Sort.CHFIRST, true);
        Assert.assertEquals("no punct, chfirst, omit1", "C 2 H 2 Br O 2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_WHITESPACE_COUNT,
                Sort.CHFIRST, false);
        Assert
                .assertEquals("no punct, chfirst, omit1", "C 2 H 2 Br 1 O 2 -",
                        s);
        s = xmlForm1.getFormattedString(Type.NOPUNCTUATION,
                Sort.ALPHABETIC_ELEMENTS, true);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "BrC2H2O2-", s);
        s = xmlForm1.getFormattedString(Type.NOPUNCTUATION,
                Sort.ALPHABETIC_ELEMENTS, false);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "Br1C2H2O2-", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_COUNT_WHITESPACE,
                Sort.ALPHABETIC_ELEMENTS, true);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "Br C2 H2 O2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_COUNT_WHITESPACE,
                Sort.ALPHABETIC_ELEMENTS, false);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "Br1 C2 H2 O2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_WHITESPACE_COUNT,
                Sort.ALPHABETIC_ELEMENTS, true);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "Br C 2 H 2 O 2 -", s);
        s = xmlForm1.getFormattedString(Type.ELEMENT_WHITESPACE_COUNT,
                Sort.ALPHABETIC_ELEMENTS, false);
        Assert.assertEquals("no punct, ALPHABETIC_ELEMENTS, omit1",
                "Br 1 C 2 H 2 O 2 -", s);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getFormattedString()'
     */
    @Test
    public void testGetFormattedString() {
        String s = xomForm1.getFormattedString();
        Assert.assertEquals("xomForm1 string", "HNO3", s);

        s = xomForm2.getFormattedString();
        Assert.assertEquals("xomForm2 string", "HNO3", s);

        s = xomForm3.getFormattedString();
        Assert.assertEquals("xomForm3 string", "H3O7SCNa", s);

        s = xmlForm1.getFormattedString();
        Assert.assertEquals("xmlForm1 string", "C2H2BrO2-", s);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getCalculatedMass(List, RealArray)'
     */
    @Test
    public void testGetCalculatedMass() {
        CMLFormula f1 = null;
        try {
            f1 = CMLFormula.createFormula("C 2 H 3 Cl 1");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        double d = f1.getCalculatedMolecularMass();
        Assert.assertEquals("Calculated mass ", 62.49822, d, 0.00000001);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.equalsFormula(Object)'
     */
    @Test
    public void testEqualsFormula() {
        CMLFormula f1 = null;
        try {
            f1 = CMLFormula.createFormula("C 2 H 3 Cl 1");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        CMLFormula f2 = null;
        try {
            f2 = CMLFormula.createFormula("C 2 H 3 Cl 1");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        boolean ff = f1.equals(f2, 0.0001);
        Assert.assertTrue("equal formulae", ff);

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getDifference(CMLFormula)'
     */
    @Test
    public void testGetDifference() {
        CMLFormula formula1 = null;
        try {
            formula1 = CMLFormula.createFormula("H 1 S 1 O 3",
                    CMLFormula.Type.ELEMENT_WHITESPACE_COUNT);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        formula1.setCount(3);
        formula1.setFormalCharge(-1);
        Assert.assertEquals("formula1 concise ", "H 1 O 3 S 1 -1", formula1
                .getConcise());
        Assert.assertEquals("formula1 charge ", -1, formula1.getFormalCharge());
        Assert.assertEquals("formula1 count ", 3, formula1.getCount(), EPS);

        CMLFormula formula2 = null;
        try {
            formula2 = CMLFormula.createFormula("Mg 1 O 6 H 12",
                    CMLFormula.Type.ELEMENT_WHITESPACE_COUNT);
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        formula2.setFormalCharge(2);
        Assert.assertEquals("formula2 concise ", "H 12 Mg 1 O 6 2", formula2
                .getConcise());
        Assert.assertEquals("formula2 charge ", 2, formula2.getFormalCharge());
        Assert.assertEquals("formula2 count ", 1, formula2.getCount(), EPS);

        CMLFormula formulaDiff = formula1.getDifference(formula2);
        CMLFormula expectedDiff = CMLFormula.createFormula("H -9.0 Mg -1.0 O 3.0 S 3.0 -5", true);
        assertEqualsConcise("difference ", expectedDiff, formulaDiff, 0.00001);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.equalsAggregate(CMLFormula)'
     * @exception Exception
     */
    @Test
    public void testEqualsAggregate() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 2 H 4'/>"
                );
        CMLFormula form2 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 2 H 4 O 1'/>"
                    );
        boolean equals = form1.equalsAggregate(form1);
        Assert.assertTrue("equality", equals);
        equals = form1.equalsAggregate(form2);
        Assert.assertFalse("equality", equals);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.toFormulaString()'
     */
    @Test
    public void testToFormulaString() {
        CMLFormula f1 = null;
        try {
            f1 = CMLFormula.createFormula("C 2 H 3 Cl 1");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        String formulaString = f1.toFormulaString();
        Assert.assertEquals("formula string",
                "count: 1.0; charge: 0: C(2.0)H(3.0)Cl", formulaString);
    }

    /** test */
    @Test
    public void testMultiplyBy() {
        CMLFormula f1 = null;
        try {
            f1 = CMLFormula.createFormula("C 1 H 1.5 Cl 0.5");
        } catch (CMLRuntimeException e) {
            Assert.fail("should not throw " + e);
        }
        f1.multiplyBy(2.0);
        String f1S = f1.getConcise();
        Assert.assertEquals("multiplied formula ", "C 2 H 3 Cl 1", f1S);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.writeHTML(Writer)'
     * @exception Exception
     */
    @Test
    public void testWriteHTML() throws Exception {
        StringWriter w = new StringWriter();
        xomForm1.writeHTML(w);
        String html = w.toString();
        w.close();
        Assert.assertEquals("html",
            "<span class='formula'>HO<sub>3</sub>N</span>", html);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.setConcise(String)'
     */
    @Test
    public void testSetConcise() {
        Assert.assertEquals("concise", "H 1 O 3 N 1", xomForm1.getConcise());
        try {
            xomForm1.setConcise("H 2 O 1");
            Assert.fail("should throw 'Cannot reset concise if atomArray is present'");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("cannot reset concise", "Cannot reset concise if atomArray is present", e.getMessage());
        }
        Assert.assertEquals("concise", "H 1 O 3 N 1", xomForm1.getConcise());
        CMLFormula form = new CMLFormula();
        form.setConcise("H 1 O 3 N 1");
        Assert.assertEquals("concise", "H 1 N 1 O 3", form.getConcise());
        try {
            form.setConcise("H 2 O 1");
            Assert.fail("should throw 'Cannot reset concise if atomArray is present'");
        } catch (CMLRuntimeException e) {
            Assert.assertEquals("cannot reset concise", "Cannot reset concise if atomArray is present", e.getMessage());
        }
        Assert.assertEquals("concise", "H 1 N 1 O 3", form.getConcise());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.CMLFormula(CMLMolecule)'
     */
    @Test
    public void testCMLFormulaCMLMolecule() {
        CMLFormula form = new CMLFormula(xmlMolecule);
        Assert.assertEquals("concise", "C 1 H 3 F 1 N 1 O 1 S 1", form.getConcise());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.normalize()'
     * @exception Exception
     */
    @Test
    public void testNormalize() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 2 H 4'/>"
            );
        form1.normalize();
        Assert.assertEquals("concise", "C 2 H 4", form1.getConcise());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.generateConcise(CMLAtomArray, int)'
     * @exception Exception
     */
    @Test
    public void testGenerateConcise() throws Exception {
        CMLAtomArray atomArray = new CMLAtomArray();
//        atomArray.setElementType(new String[]{AS.C.value, AS.H.value, AS.O.value});
//        atomArray.setCount(new double[]{1, 4, 2});
        atomArray.setElementTypeAndCount(
                new String[]{AS.C.value, AS.H.value, AS.O.value}, new double[]{1, 4, 2});
        String concise = atomArray.generateConcise(-2);
        Assert.assertEquals("concise", "C 1 H 4 O 2 -2", concise);
    }

    /** Test method for
     * 'org.xmlcml.cml.element.CMLFormula.removeChargeFromConcise(String)'
     * @exception Exception
     */
    @Test
    public void testRemoveChargeFromConcise() throws Exception {
        String concise = CMLFormula.removeChargeFromConcise("C 2 H 4 O 2 -2");
        Assert.assertEquals("concise", "C 2 H 4 O 2", concise);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getConciseNoCharge()'
     * @exception Exception
     */
    @Test
    public void testGetConciseNoCharge() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 2 H 4 O 2 -2'/>"
            );
            Assert.assertEquals("concise", "C 2 H 4 O 2", form1.getConciseNoCharge());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.createFormula(String,
     * Type)'
     * @exception Exception
     */
    @Test
    public void testCreateFormulaStringType() throws Exception {
        CMLFormula form = CMLFormula.createFormula("C 2 H 4 O 2");
        Assert.assertEquals("concise", "C 2 H 4 O 2", form.getConcise());
        form = CMLFormula.createFormula("C 2 H 4 O 2 -1");
        Assert.assertEquals("concise", "C 2 H 4 O 2 -1", form.getConcise());
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.getCounts()'
     * @exception Exception
     */
    @Test
    public void testGetCounts() throws Exception {
        CMLFormula form = CMLFormula.createFormula("C 2 H 4 O 2");
        DoubleTestBase.assertEquals("concise",
                new double[]{2, 4, 2}, form.getCounts(), EPS);
        form = CMLFormula.createFormula("C 2 H 4 O 2 -1");
        DoubleTestBase.assertEquals("concise",
                new double[]{2, 4, 2}, form.getCounts(), EPS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.createAggregatedFormula(CMLFormula)'
     * @exception Exception
     */
    @Test
    public void testCreateAggregatedFormula() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 2 H 4 O 2 -2'/>"
        );
        CMLFormula form2 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 3 H 3 Cl 3 1'/>"
                );
        CMLFormula form3 = form1.createAggregatedFormula(form2);
        Assert.assertEquals("concise", "C 5 H 7 Cl 3 O 2 -1", form3.getConcise());
        CMLFormula form4 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 3 H 3 Cl 3 -1' count='2'/>"
        );
        form3 = form1.createAggregatedFormula(form4);
        Assert.assertEquals("concise", "C 8 H 10 Cl 6 O 2 -4", form3.getConcise());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getFormattedString(Type, Sort,
     * boolean)'
     * @exception Exception
     */
    @Test
    public void testGetFormattedStringTypeSortBoolean() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
            "<formula "+CML_XMLNS+" concise='C 1 H 4 O 2 Br 1 -2'/>"
        );
        boolean omit1 = true;
        Sort sort = Sort.ALPHABETIC_ELEMENTS;
        String formS = form1.getFormattedString(
                Type.ELEMENT_COUNT_WHITESPACE, sort, omit1);
        Assert.assertEquals("formatted", "Br C H4 O2 --", formS);
        omit1 = false;
        formS = form1.getFormattedString(
                Type.ELEMENT_COUNT_WHITESPACE, sort, omit1);
        Assert.assertEquals("formatted", "Br1 C1 H4 O2 --", formS);
        sort = Sort.CHFIRST;
        omit1 = false;
        formS = form1.getFormattedString(
                Type.ELEMENT_COUNT_WHITESPACE, sort, omit1);
        Assert.assertEquals("formatted", "C1 H4 Br1 O2 --", formS);
        omit1 = true;
        formS = form1.getFormattedString(
                Type.ELEMENT_COUNT_WHITESPACE, sort, omit1);
        Assert.assertEquals("formatted", "C H4 Br O2 --", formS);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.getFormalChargeString()'
     * @exception Exception
     */
    @Test
    public void testGetFormalChargeString() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 1 H 4 O 2 Br 1 -2'/>"
            );
        String formS = form1.getFormalChargeString();
        Assert.assertEquals("formatted", "--", formS);
    }

    /**
     * Test method for 'org.xmlcml.cml.element.CMLFormula.equals(CMLFormula,
     * double)'
     * @exception Exception
     */
    @Test
    public void testEqualsCMLFormulaDouble() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 1 H 4 O 2 Br 1 -2'/>"
            );
        CMLFormula form2 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 1.001 H 3.99 O 2 Br 1 -2'/>"
            );
        Assert.assertFalse("equals", form1.equals(form2, 0.001));
        Assert.assertTrue("equals", form1.equals(form2, 0.011));
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLFormula.equalsConcise(CMLFormula, double)'
     * @exception Exception
     */
    @Test
    public void testEqualsConcise() throws Exception {
        CMLFormula form1 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 1 H 4 O 2 Br 1 -2'/>"
            );
        CMLFormula form2 = (CMLFormula) parseValidString(
                "<formula "+CML_XMLNS+" concise='C 1.001 H 3.99 O 2 Br 1 -2'/>"
            );
        Assert.assertTrue("equals", form1.equals(form1));
        Assert.assertFalse("equals", form1.equals(form2));
    }
 }
