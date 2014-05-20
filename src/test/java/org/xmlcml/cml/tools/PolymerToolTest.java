/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CATALOG_XML;
import static org.xmlcml.cml.base.CMLConstants.XML_SUFF;
import static org.xmlcml.cml.element.AbstractTestBase.EXPERIMENTAL_RESOURCE;
import static org.xmlcml.cml.element.AbstractTestBase.TOOL_MOLECULES_RESOURCE;
import static org.xmlcml.euclid.EuclidConstants.S_COLON;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.testutil.JumboTestUtils;
import org.xmlcml.euclid.Util;

/**
 * test polymerTool.
 *
 * @author pm286
 *
 */
public class PolymerToolTest {
	private static Logger LOG = Logger.getLogger(PolymerTool.class);

	private static ResourceManager getMoleculeCatalog() throws IOException {
        ResourceManager catalogTool = null;
        try {
        	catalogTool = new ResourceManager(Util
        		    .getResource(TOOL_MOLECULES_RESOURCE +CMLConstants.U_S + CATALOG_XML).toURI());
        }
        catch (URISyntaxException e) {
        	e.printStackTrace();
		}
            
        return catalogTool;
    }

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.PolymerTool1.PolymerTool1(CMLMolecule)'
	 *
	 * @throws Exception
	 */
	@Test
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
        LOG.trace("NOT YET WORKING - PMR");
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
		polymerTool.setResourceManager(getMoleculeCatalog());
		polymerTool.processConvention();
		// debug("basic", mol, fileroot);
		polymerTool.processConvention();
		LOG.debug("-----------intermed----------");
		// debug("intermed", mol, fileroot);
		polymerTool.processConvention();
		LOG.debug("-----------explicit----------");
		// debug("explicit", mol, fileroot);
		polymerTool.processConvention();
		LOG.debug("-----------complete----------");
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
		String copolyS = CMLConstants.S_EMPTY
				+ "<molecule id='"
				+ fileroot
				+ "'"
				+ "  xmlns='http://www.xml-cml.org/schema'"
				+ "  xmlns:f='http://www.xml-cml.org/mols/frags'"
				+ "  convention='cml:PML-concise'"
				+ "  formula='(f:oh}r1)-[l(1.39),t(180)](r1{f:po}r2-[l(1.41),t(60)])*(3)-[l(1.42),t(180)](r1{f:eo}r2-[l(1.43),t(-60)])*(4)-[l(1.44),t(165)](r1{f:me)'"
				+ "  />";
		CMLMolecule molecule = (CMLMolecule)JumboTestUtils.parseValidString(copolyS);
		PolymerTool polymerTool = new PolymerTool(molecule);
		polymerTool.setResourceManager(getMoleculeCatalog()); //added get catalog
		try {
			polymerTool.processConventionExhaustively();
		} catch (RuntimeException e) {
			System.err.println("ERROR " + e);
		}
		String convention = molecule.getConvention();
		convention = convention.substring(convention.indexOf(S_COLON) + 1);

        writeFile(molecule, fileroot + CMLConstants.S_UNDER + convention + XML_SUFF);
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
		LOG.debug("Wrote: " + outfile.getAbsolutePath());
	}

	@SuppressWarnings("unused")
	private void testExample(String fileroot) throws Exception {
        
        PolymerTool polymerTool = new PolymerTool();
        File file = new File(fileroot + "_basic.xml");
        CMLElement root = (CMLElement) CMLUtil.readElementFromResource(file.toString());
        
        polymerTool.setElement(root);
        polymerTool.setTargetLevel(FragmentTool.Convention.PML_COMPLETE);
        polymerTool.setResourceManager(getMoleculeCatalog());
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
		LOG.debug("Running example " + fileroot);
        PolymerTool polymerTool = new PolymerTool();
		CMLMoleculeList molList = polymerTool.readMoleculeList(EXPERIMENTAL_RESOURCE +CMLConstants.U_S
                + fileroot + "_concise.xml");
		polymerTool.setMoleculeList(molList);
		polymerTool.setResourceManager(getMoleculeCatalog());
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
		 LOG.debug("ERROR "+e.getMessage());
		 }
		 --*/
	}


 }
