package org.xmlcml.cml.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.tools.PolymerTool.Convention;
import org.xmlcml.euclid.Util;

/**
 * test polymerTool.
 *
 * @author pm286
 *
 */
public class PolymerToolTest extends AbstractToolTest {

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.PolymerTool1.PolymerTool1(CMLMolecule)'
	 *
	 * @throws Exception
	 */
	@Test
//	@Ignore
	public void testExamples() throws Exception {
//// THESE WORK - some are commented out to save time
        /*--
      testExample("etoac");
      testExample("br");
      testExample("branch0");
      testExample("star");
      testExample("poly0");
      --*/
      //----
        System.err.println("NOT YET WORKING - PMR");
//        testExample("new1"); // fails
//        testExample("new2");   // works
//        testExample("new3a");
//        testExample("new5");    // works
//        testExample("new5a");
//        testExample("new6");
//          testExample("mixed");
//        testExample("polystyrene"); // works
        // FIXME
//        testExample("polystyreneList");
//      testExample("new7");
        
//        testExample("star1");
//        testExample("star2");
//        testExample("star3");
//          testExample("phen");
////        testExample("coxyacet");
//        testExample("naphthyl0");
////        testExample("branch2");
////          testExample("linear2");
//          testExample("linear3");
////          testExample("linear4");
////        testExample("branch00");
      // FIXME - multiplier fails
//		testExample("copoly");
////		testExample("poly");
//          testExample("gly0");
      // FIXME - multiplier fails
//        testExample("randomPoly");
//         testExample("adgluc");

        // =================== TESTING ===============
        // FIXME FAILS
//         testExample("branch");

        // fails
//        testExample("starPoly");
//        testExample("starPoly1");

        // NEW FAILS
//         testExample("randomBranchPoly");
        // NEW FAILS
//      testExample("naphthyl");
        // NEW FAILS
        // NEW FAILS
//        testExample("gly1");
        
	}

	/**
	 * typical example.
	 *
	 * @throws ParsingException
	 * @throws IOException
	 * @throws ValidityException
	 */
	@Test
	@Ignore
	public void testPoly1() throws ValidityException, IOException,
			ParsingException {
		String fileroot = "poly1";
        PolymerTool polymerTool = new PolymerTool();
		CMLMolecule mol = (CMLMolecule) CMLUtil.readElementFromResource(fileroot + "_concise.xml");
		polymerTool.setMolecule(mol);
		polymerTool.setMoleculeCatalog(MoleculeToolTest.getMoleculeCatalog());
		polymerTool.processConvention();
		// debug("basic", mol, fileroot);
		polymerTool.processConvention();
		System.out.println("-----------intermed----------");
		// debug("intermed", mol, fileroot);
		polymerTool.processConvention();
		System.out.println("-----------explicit----------");
		// debug("explicit", mol, fileroot);
		polymerTool.processConvention();
		System.out.println("-----------complete----------");
		// debug("complete", mol, fileroot);
		polymerTool.processConvention();
		// debug("cartesian", mol, fileroot);

		writeFile(mol, fileroot + "_cartesian.xml");
	}

	/**
	 * test .
     * @throws Exception
	 */
	@Test
	@Ignore
	public void testProcessConventionExhaustively() throws Exception { //added throws Exception
		String fileroot = "copoly1";
		String copolyS = S_EMPTY
				+ "<molecule id='"
				+ fileroot
				+ "'"
				+ "  xmlns='http://www.xml-cml.org/schema'"
				+ "  xmlns:f='http://www.xml-cml.org/mols/frags'"
				+ "  convention='cml:PML-concise'"
				+ "  formula='(f:oh}r1)-[l(1.39),t(180)](r1{f:po}r2-[l(1.41),t(60)])*(3)-[l(1.42),t(180)](r1{f:eo}r2-[l(1.43),t(-60)])*(4)-[l(1.44),t(165)](r1{f:me)'"
				+ "  />";
		CMLMolecule molecule = (CMLMolecule) parseValidString(copolyS);
		PolymerTool polymerTool = new PolymerTool(molecule);
		polymerTool.setMoleculeCatalog(MoleculeToolTest.getMoleculeCatalog()); //added get catalog
		try {
			polymerTool.processConventionExhaustively();
		} catch (CMLRuntimeException e) {
			System.err.println("ERROR " + e);
		}
		String convention = molecule.getConvention();
		convention = convention.substring(convention.indexOf(S_COLON) + 1);

        writeFile(molecule, fileroot + S_UNDER + convention + XML_SUFF);
    }
    
    private void writeFile(CMLMolecule molecule, String filename) {
		File outfile = new File(Util.getTEMP_DIRECTORY() + File.separator
				+ filename);
		try {
			Serializer serializer = new Serializer(
					new FileOutputStream(outfile));
			serializer.write(new Document(new CMLMolecule(molecule)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Wrote: " + outfile.getAbsolutePath());
	}

	@SuppressWarnings("unused")
	private void testExample(String fileroot) throws Exception {
        
        PolymerTool polymerTool = new PolymerTool();
        File file = new File(fileroot + "_basic.xml");
        CMLElement root = (CMLElement) CMLUtil.readElementFromResource(file.toString());
        
        polymerTool.setElement(root);
        polymerTool.setTargetLevel(Convention.PML_COMPLETE);
        polymerTool.setMoleculeCatalog(MoleculeToolTest.getMoleculeCatalog());
        polymerTool.setDebug(true);
        polymerTool.processConventionExhaustively();
        polymerTool.write(fileroot + "final.xml");
	}
    

	/**
	 * test moleculeList ensemble.
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testMoleculeList() throws Exception {
		testMoleculeList("gly1");
	}

	private void testMoleculeList(String fileroot) throws Exception {
		System.out.println("Running example " + fileroot);
        PolymerTool polymerTool = new PolymerTool();
		CMLMoleculeList molList = polymerTool.readMoleculeList(EXPERIMENTAL_RESOURCE + U_S
                + fileroot + "_concise.xml");
		polymerTool.setMoleculeList(molList);
		polymerTool.setMoleculeCatalog(MoleculeToolTest.getMoleculeCatalog());
		polymerTool.processConvention();
		/*--

		 polymerTool.processConvention();
		 debug("intermed", molList, fileroot);

		 polymerTool.processConvention();
		 debug("explicit", molList, fileroot);

		 polymerTool.processConvention();
		 debug("complete", molList, fileroot);

		 polymerTool.processConvention();
		 try {
		 debug("cartesian", mol, fileroot);
		 } catch (EuclidRuntime e) {
		 System.out.println("ERROR "+e.getMessage());
		 }
		 --*/
	}

//    /** debug.
//     * 
//     * @param s
//     * @param molTest
//     */
//    public void debug(String s, CMLMolecule molTest) {
//        String f = fileroot + S_UNDER + s + XML_SUFF;
//        System.out.println("=== debug " + s + "== to == "+fileroot);
//        
//        try {
//            CMLMolecule molRef = (CMLMolecule) CMLUtil.readElementFromResource(f);
//            try {
//                BaseTest.assertEqualsCanonically("comparing with previous result",
//                        molRef, molTest, true);
//            } catch (AssertionError e) {
//                try {
//                    Assert.fail(e + " for " + fileroot + S_SLASH + s);
//                } catch (Throwable e1) {
//                    System.err.println("Molecules are different: "+e1);
//                }
//            }
//        } catch (CMLRuntimeException e) {
//            System.out.println("Comparison files does not exist, skipped:  "+f);
//        }
//        File testFile = new File(OUTPUT_DIR, fileroot+S_UNDER+s+XML_SUFF);
//        System.out.println("wrote..: "+testFile.getAbsolutePath());
//        try {
//            CMLUtil.debug(molTest, new FileOutputStream(testFile));
//        } catch (IOException e) {
//            throw new CMLRuntimeException("IO Exception: "+e);
//        }
//        System.out.println("== end ==" + s + "==========");
//    }
//
//    /** debug.
//     * 
//     * @param s
//     * @param molListTest
//     * @param fileroot
//     * @throws Exception
//     */
//    public void debug(String s, CMLMoleculeList molListTest, String fileroot)
//            throws Exception {
//        Assert.assertNotNull("molListTest", molListTest);
//        System.out.println("============" + s + "==========");
//        String f = fileroot + S_UNDER + s + XML_SUFF;
//        CMLMoleculeList molListRef = readMoleculeList(f);
//        Assert.assertNotNull("molListRef", molListRef);
//        try {
//            BaseTest.assertEqualsCanonically("comparing with previous result",
//                    molListRef, molListTest, true);
//        } catch (AssertionError e) {
//            Assert.fail(e + " for " + fileroot + S_SLASH + s);
//        }
//        System.out.println("============" + s + "==========");
//    }

 }
