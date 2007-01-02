package org.xmlcml.cml.tools;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Serializer;
import nu.xom.xslt.XSLTransform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.test.ReactionAllTestBase;
import org.xmlcml.cml.tools.ReactionTool;
import org.xmlcml.euclid.Util;

/**
 * test reactionTool
 *
 * @author pmr
 *
 */
public class ReactionToolTest extends ReactionAllTestBase {

    /** */
    public final static String REACTION_EXAMPLES = 
        AbstractToolTest.TOOLS_EXAMPLES+U_S+"reactions";

    ReactionTool xmlReactTool1;

    String balancedS = S_EMPTY + "<reaction id='br' " + CML_XMLNS + ">"
            + "  <reactantList id='brl1'>" + "    <reactant id='br1'>"
            + "     <molecule id='brm1'>" + "      <atomArray>"
            + "        <atom id='a1' elementType='O'/>"
            + "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
            + "     </molecule>" + "    </reactant>"
            + "    <reactant id='br2'>" + "     <molecule id='brm2'>"
            + "      <atomArray>" + "        <atom id='a3' elementType='S'/>"
            + "        <atom id='a4' elementType='Zn'/>" + "      </atomArray>"
            + "     </molecule>" + "    </reactant>" + "  </reactantList>"
            + "  <productList id='bpl1'>" + "    <product id='bp1'>"
            + "     <molecule id='bpm1'>" + "      <atomArray>"
            + "        <atom id='a3' elementType='S'/>"
            + "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
            + "     </molecule>" + "    </product>" + "    <product id='bp2'>"
            + "     <molecule id='bpm2'>" + "      <atomArray>"
            + "        <atom id='a1' elementType='O'/>"
            + "        <atom id='a4' elementType='Zn'/>" + "      </atomArray>"
            + "     </molecule>" + "    </product>" + "  </productList>"
            + "</reaction>" + S_EMPTY;

    String unbalancedS = S_EMPTY + "<reaction " + CML_XMLNS + ">"
            + "  <reactantList>" + "    <reactant>" + "     <molecule>"
            + "      <atomArray>" + "        <atom id='a1' elementType='O'/>"
            + "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
            + "     </molecule>" + "    </reactant>"
            + "    <reactant count='2'>" + "     <molecule>"
            + "      <atomArray>" + "        <atom id='a3' elementType='Cl'/>"
            + "        <atom id='a4' elementType='H'/>" + "      </atomArray>"
            + "     </molecule>" + "    </reactant>" + "  </reactantList>"
            + "  <productList>" + "    <product>" + "     <molecule>"
            + "      <atomArray>" + "        <atom id='a1' elementType='Cl'/>"
            + "        <atom id='a3' elementType='Cl'/>"
            + "        <atom id='a2' elementType='Mg'/>" + "      </atomArray>"
            + "     </molecule>" + "    </product>" + "  </productList>"
            + "</reaction>" + S_EMPTY;

    CMLReaction balancedR = null;

    CMLReaction unbalancedR = null;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        xmlReactTool1 = new ReactionTool(xmlReact1);

        balancedR = (CMLReaction) parseValidString(balancedS);
        unbalancedR = (CMLReaction) parseValidString(unbalancedS);

    }

    /**
     * Test method for 'org.xmlcml.cml.tools.ReactionTool.outputBalance(Writer)'
     */
    @Test
    public void testOutputBalance() {

        StringWriter w = new StringWriter();
        try {
            new ReactionTool(balancedR).outputBalance(w);
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        } catch (CMLException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            w.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("output balance", "MgOSZn = MgOSZn ; difference: ",
                w.toString());
        // = MgOSZn ; difference: Mg -1.0 O -1.0 S -1.0 Zn -1.0
        w = new StringWriter();
        try {
            new ReactionTool(unbalancedR).outputBalance(w);
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        } catch (CMLException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            w.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("output unbalance",
                "HClMgO = Cl2Mg ; difference: Cl -1.0 O 1.0 H 1.0", w
                        .toString());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.outputReaction(Writer)'
     */
    @Test
    public void testOutputReaction() {

        StringWriter w = new StringWriter();
        try {
            new ReactionTool(balancedR).outputReaction(w);
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        } catch (CMLException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            w.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("output balance", "MgO + SZn = MgS + OZn", w
                .toString());

        w = new StringWriter();
        try {
            new ReactionTool(unbalancedR).outputReaction(w);
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        } catch (CMLException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            w.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("output unbalance", "MgO + HCl = Cl2Mg", w
                .toString());

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.getMoleculesIncludingSpectators(String)'
     */
    @Test
    @Ignore
    public void testGetMoleculesIncludingSpectators() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.splitAndReorganizeMolecules(CMLElement,
     * Elements, int, String)'
     */
    @Test
    @Ignore
    public void testSplitAndReorganizeMolecules() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.partitionIntoMolecules()'
     */
    @Test
    @Ignore
    public void testPartitionIntoMolecules() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.mapReactantsToProducts(CMLReaction,
     * String)'
     */
    @Test
    @Ignore
    public void testMapReactantsToProducts() {

    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLReaction.translateProductsToReactants()'
     */
    @Test
    @Ignore
    public void testTranslateProductsToReactants() {
        // TODO
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLReaction.matchProductsToNextReactants(CMLReaction,
     * AtomMatcher)'
     */
    @Test
    @Ignore
    public void testMatchProductsToNextReactants() {
        // TODO
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.CMLReaction.moveReactantProductToSpectator(AtomMatcher)'
     */
    @Test
    @Ignore
    public void testMoveReactantProductToSpectator() {
        /*--
         <substance id="sub1">
         <name>Cys178</name>
         <label dictRef="macie:sideChain" value="Cys178"/>
         <label dictRef="macie:protonDonor" value="Cys178"/>
         <label dictRef="macie:acid" value="Cys178"/>
         </substance>
         <reactant>
         <molecule id="0001.stg03.r.6">
         <atomArray>
         <atom id="a39" elementType="R" x2="-0.1833" y2="-0.9292">
         <label value="Cys178"/>
         </atom>
         </atomArray>
         --*/
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.element.ProductReactant.getFormula(Element)'
     */
    @Test
    @Ignore
    public void testProductReactantGetFormula() {
        // CMLFormula formula;
        /*--
         CMLElements<CMLReactant> reactants = xmlReact1.getReactantList().getReactantElements();
         Assert.assertEquals("reactants", 2, reactants.size());
         formula = ReactionTool.getFormula(reactants.get(0));
         Assert.assertNotNull("formula", formula);
         // FIXME formula needs to count hydrogens
         Assert.assertEquals("formula", "C 1 Cl 1", formula.getConcise());
         formula = ReactionTool.getFormula(reactants.get(1));
         Assert.assertNotNull("formula", formula);
         // FIXME formula needs to count hydrogens
         Assert.assertEquals("formula", "O 1", formula.getConcise());

         CMLElements<CMLProduct> products = xmlReact1.getProductList().getProductElements();
         Assert.assertEquals("products", 2, products.size());
         formula = ReactionTool.getFormula(products.get(0));
         Assert.assertNotNull("formula", formula);
         // FIXME formula needs to count hydrogens
         Assert.assertEquals("formula", "C 1 O 1", formula.getConcise());
         // FIXME formula needs to fix charge
         //		formula = ProductReactant.getFormula(products.get(1));
         //		Assert.assertNotNull("formula", formula);
         //		Assert.assertEquals("formula", "Cl 1", formula.getConcise());
         --*/
    }

    /** test create from reaction scheme in literature.
     * 
     */
    @Test
    @Ignore
    public void testCreateFromOSCAR() {
        Document doc = null;
        try {
            InputStream in = Util.getInputStreamFromResource(
                    REACTION_EXAMPLES + U_S+ "oscar.xml");
            doc = new Builder().build(in);
        } catch (Throwable e) {
            System.err.println("SKIPPED" + e);
        }
        assertNotNull("oscar not null", doc);
        /*List<CMLReactionScheme> schemeList =*/ ReactionTool.createFromOSCAR(doc);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("C:\\temp\\test.xml");
            Serializer serializer = new Serializer(fos);
            serializer.write(doc);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** test create from reaction scheme in literature.
     * 
     */
    @Test
    @Ignore
    public void testCreateFromOSCAR1() {
        String dirS = "D:\\wwmm\\demos\\for_ram\\obc";
        String fileS = "markedup.xml";
//        String dirS = "C:\\oscar3\\reduced_corpus\\rsc";
//        String fileS = null;
//        String dirS = "C:\\pmr\\jumbo53\\src\\org\\xmlcml\\cml\\tools\\test\\examples\\reactions";
//        String fileS = null;
        int max = 999;
        createFromOscar(dirS, fileS,max);
    }
    
    /** test create from reaction scheme in literature.
     * 
     */
    @Test
    @Ignore
    public void testCreateFromOSCAR2() {
        String dirS = "D:\\wwmm\\demos\\markedup-scixml";
        createFromOscar(dirS, null, 10);
    }
    
    private void createFromOscar(String dirS, String xmlName, int max) {
        File dir = new File(dirS);
        String[] files = dir.list();
        String oscar3tohtml = "C:\\pmr\\cmlsvg\\oscar3toHtml.xsl";
        Document xslDoc = null;
        XSLTransform transform = null;
        try {
            xslDoc = new Builder().build(new FileInputStream(oscar3tohtml));
            transform = new XSLTransform(xslDoc);
        } catch (Exception e) {
            throw new CMLRuntimeException(e.getMessage()+"|"+e.getCause());
        }
        int count = 0;
        for (String fileS : files) {
            fileS = getFileNameIn(fileS, xmlName);
            try {
                if (!fileS.endsWith(XML_SUFF) || 
                    fileS.endsWith("out.xml")) {
                    continue;
                }
                if (count++ >= max) continue;
                @SuppressWarnings("unused")
              String sss = "b512762a";
//              if (fileS.indexOf(sss) == -1) continue;
                
                File file = new File(dir, fileS);
                System.out.println("========="+file+"=========");
                System.err.println("========="+file+"=========");
                Document doc = null;
                try {
                    doc = new Builder().build(new FileInputStream(file));
                } catch (FileNotFoundException fnfe) {
                    continue;
                } catch (Throwable e) {
                    System.err.println("SKIPPED" + e);
                    continue;
                }
                assertNotNull("oscar not null", doc);
                /*List<CMLReactionScheme> schemeList =*/ ReactionTool.createFromOSCAR(doc);
                FileOutputStream fos = null;
                File outFile = new File(dir, fileS.substring(0, fileS.length()-4)+".out.xml");
                try {
                    fos = new FileOutputStream(outFile);
                    Serializer serializer = new Serializer(fos);
                    serializer.write(doc);
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
                Nodes nodes = transform.transform(doc);
                Document htmlDoc = null;
                if (nodes.size() != 1) {
                    throw new CMLRuntimeException("Must be one root node fro HTML");
                }
                htmlDoc = new Document((Element)nodes.get(0));
                File htmlFile = new File(dir, fileS.substring(0, fileS.length()-4)+".out.html");
                Serializer serializer = new Serializer(new FileOutputStream(htmlFile));
                serializer.write(htmlDoc);
                
            } catch (Throwable t) {
                t.printStackTrace();
                System.err.println("++++++++++"+t+"/"+t.getCause()+"/"+t.getMessage());
            }
        }
    }
    
    private String getFileNameIn(String fileS, String xmlName) {
        String fname = fileS;
        if (xmlName != null) {
            fname = fileS+File.separator+xmlName;
        }
        return fname;
    }
 }
