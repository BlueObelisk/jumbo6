package org.xmlcml.cml.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

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
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLFormulaTest;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.ReactionAllTestBase;
import org.xmlcml.cml.element.ReactionComponent;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.element.CMLUnit.Units;
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
            + "  <reactantList>" + 
            "    <reactant>" + 
            "     <molecule>" +
            "      <atomArray>" + 
            "        <atom id='a1' elementType='O'/>" +
            "        <atom id='a2' elementType='Mg'/>" +
            "      </atomArray>" +
            "     </molecule>" +
            "    </reactant>" +
            "    <reactant count='2'>" +
            "     <molecule>" +
            "      <atomArray>" +
            "         <atom id='a3' elementType='Cl'/>" +
            "         <atom id='a4' elementType='H'/>" +
            "      </atomArray>" +
            "     </molecule>" +
            "    </reactant>" +
            "  </reactantList>" +
            "  <productList>" +
            "    <product>" +
            "     <molecule>" +
            "      <atomArray>" +
            "        <atom id='a1' elementType='Cl'/>" +
            "        <atom id='a3' elementType='Cl'/>" +
            "        <atom id='a2' elementType='Mg'/>" +
            "      </atomArray>" +
            "     </molecule>" +
            "    </product>" +
            "  </productList>" +
            "</reaction>" + S_EMPTY;

    CMLReaction balancedR = null;

    CMLReaction unbalancedR = null;

	private CMLReaction reaction1;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        xmlReactTool1 = ReactionTool.getOrCreateTool(xmlReact1);

        balancedR = (CMLReaction) parseValidString(balancedS);
        unbalancedR = (CMLReaction) parseValidString(unbalancedS);
        
        InputStream is = Util.getInputStreamFromResource("org/xmlcml/cml/tools/reaction1.xml");
        reaction1 = (CMLReaction) new CMLBuilder().build(is).getRootElement();

    }

    /**
     * Test method for 'org.xmlcml.cml.tools.ReactionTool.outputBalance(Writer)'
     */
    @Ignore
    @Test
    public void testOutputBalance() {

        StringWriter w = new StringWriter();
        try {
            ReactionTool.getOrCreateTool(balancedR).outputBalance(w);
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
            ReactionTool.getOrCreateTool(unbalancedR).outputBalance(w);
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
                "HClMgO = Cl2Mg ; difference: H 1 Cl -1 O 1", w.toString());
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.ReactionTool.outputReaction(Writer)'
     */
    @Ignore
    @Test
    public void testOutputReaction() {

        StringWriter w = new StringWriter();
        try {
            ReactionTool.getOrCreateTool(balancedR).outputReaction(w);
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
            ReactionTool.getOrCreateTool(unbalancedR).outputReaction(w);
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
    
    /** */
	@Test
    @Ignore
	public void testCreateGraphicsElement() {
		fail("Not yet implemented");
	}

    /** */
	@Test
	public void testCalculateDifferenceFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(balancedR).calculateDifferenceFormula();
		CMLFormula expected = new CMLFormula();
		CMLFormulaTest.assertEqualsConcise("empty", expected, formula, EPS);
		formula = ReactionTool.getOrCreateTool(unbalancedR).calculateDifferenceFormula();
		expected = new CMLFormula();
		expected.add("Cl", -1.);
		expected.add("O", 1.);
		expected.add("H", 1.);
		CMLFormulaTest.assertEqualsConcise("non-empty", expected, formula, EPS);
	}

    /** */
	@Test
	public void testCreateAggregateProductFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(balancedR).createAggregateProductFormula();
		formula.debug();
	}

    /** */
	@Test
	public void testCreateAggregateReactantFormula() {
		CMLFormula formula = ReactionTool.getOrCreateTool(balancedR).createAggregateReactantFormula();
		formula.debug();
	}

    /** */
	@Test
    @Ignore
	public void testAnalyzeReaction() {
		fail("Not yet implemented");
	}

    /** */
	@Test
    @Ignore
	public void testGetFormula() {
		fail("Not yet implemented");
	}

    /** */
	@Test
    @Ignore
	public void testGetAggregateFormula() {
		fail("Not yet implemented");
	}

    /** */
	@Test
	public void testGetMolecules() {
		List<CMLMolecule> molecules = reaction1.getMolecules(Component.REACTANT);
		Assert.assertEquals("descendant reactant molecules", 2, molecules.size());
	}

    /** */
	@Test
	public void testGetAtoms() {
		List<CMLAtom> atoms = reaction1.getAtoms(Component.REACTANT);
		Assert.assertEquals("descendant reactant atoms", 12, atoms.size());
	}

    /** */
	@Test
	public void testGetBonds() {
		List<CMLBond> bonds = reaction1.getBonds(Component.REACTANT);
		Assert.assertEquals("descendant reactant bonds", 10, bonds.size());
	}

    /** */
	@Test
    @Ignore
	public void testTranslateSpectatorProductsToReactants() {
		fail("Not yet implemented");
	}

    
// ===================================================    
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
                System.err.println("++++++++++"+t+S_SLASH+t.getCause()+S_SLASH+t.getMessage());
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

	/** 
	 */
	@Test
	public final void testGetMolecules1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLMolecule> molecules = reactionTool.getMolecules(Component.REACTANTLIST);
		Assert.assertEquals("reactant molecules", 2, molecules.size());
		molecules = reactionTool.getMolecules(Component.PRODUCTLIST);
		Assert.assertEquals("product molecules", 2, molecules.size());
	}

	/** 
	 */
	@Test
	public final void testGetAtoms1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLAtom> atoms = reactionTool.getAtoms(Component.REACTANTLIST);
		Assert.assertEquals("reactant atoms", 4, atoms.size());
		atoms = reactionTool.getAtoms(Component.PRODUCTLIST);
		Assert.assertEquals("reactant atoms", 4, atoms.size());
	}

	/** 
	 */
	@Test
	public final void testGetBonds1() {
		ReactionTool reactionTool = ReactionTool.getOrCreateTool(balancedR);
		List<CMLBond> bonds = reactionTool.getBonds(Component.REACTANTLIST);
		Assert.assertEquals("reactant bonds", 0, bonds.size());
		bonds = reactionTool.getBonds(Component.PRODUCTLIST);
		Assert.assertEquals("reactant bonds", 0, bonds.size());
	}

	/** 
	 * A complex reaction with several reactants and products
	 */
	@Test
	public final void testComplexReaction() {
        Document doc = null;
        try {
            InputStream in = Util.getInputStreamFromResource(
                    REACTION_EXAMPLES + U_S+ "reaction1.xml");
            doc = new CMLBuilder().build(in);
        } catch (Throwable e) {
            System.err.println("SKIPPED" + e);
        }
        assertNotNull("reaction not null", doc);
        CMLCml cml = (CMLCml) doc.getRootElement();
        
        // molecules are listed in a moleculeList and accessed by molecule@ref
        // first check the molecules are OK
        CMLMoleculeList moleculeList = (CMLMoleculeList) 
            doc.query(".//cml:moleculeList", X_CML).get(0);
        CMLReaction reaction1 = (CMLReaction) 
            doc.query(".//cml:reaction", X_CML).get(0);
        // get MWt for each molecule
        CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
        Assert.assertEquals("molecules", 6, molecules.size());
        
        //  now check their MWts
        double[] mwt = new double[]{277.40178,101.19, 215.87126, 396.361, 102.198, 79.904};
        for (int i = 0; i < molecules.size(); i++) {
        	CMLMolecule molecule = molecules.get(i);
        	CMLFormula formula = molecule.getFormulaElements().get(0);
        	double d = formula.getCalculatedMolecularMass();
            // check MWts
        	Assert.assertEquals("mw ", mwt[i], d, 0.001);
        }
        
        // check the reactants each of which contains molecule@ref
        CMLReactantList reactantList = reaction1.getReactantList();
        CMLElements<CMLReactant> reactants = reactantList.getReactantElements();
    	Assert.assertEquals("reactants ", 3, reactants.size());
        calculateMolarAmounts(doc, reactants);
        
        System.out.println("======products=====");
        
        // check the products each of which contains molecule@ref
        CMLProductList productList = reaction1.getProductList();
        CMLElements<CMLProduct> products = productList.getProductElements();
    	Assert.assertEquals("products ", 3, products.size());
        calculateMolarAmounts(doc, products);
        
        StringWriter w = new StringWriter();
        try {
            ReactionTool.getOrCreateTool(reaction1).outputBalance(w);
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
        System.out.println("BALANCE "+w);
        
	}
	

	/**
	 * @param doc
	 * @param reactants
	 */
	private void calculateMolarAmounts(Document doc, CMLElements reactants) {
		for (int i = 0; i < reactants.size(); i++) {
        	ReactionComponent reactant = (ReactionComponent) reactants.get(i);
        	// get reference to molecule
        	CMLMolecule moleculeRef = reactant.getMolecules().get(0);
        	String idRef = moleculeRef.getRefAttribute().getValue();
        	// XPath location of molecule in list
        	Nodes moleculeNodes = doc.query(
        			".//cml:moleculeList/cml:molecule[@id='"+idRef+"']", X_CML);
        	Assert.assertEquals("moleculeRefs ", 1, moleculeNodes.size());
        	// get referenced molecule
        	CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(0);
        	// get mass in grams
        	Nodes massAmounts = ((CMLElement)reactant).query(".//cml:amount[@units='"+Units.GRAM+"']", X_CML);
        	CMLAmount massAmount = (massAmounts.size() == 0) ? null : (CMLAmount) massAmounts.get(0);
        	// or volume in mL
        	Nodes volAmounts = ((CMLElement)reactant).query(".//cml:amount[@units='"+Units.ML+"']", X_CML);
        	CMLAmount volAmount = (volAmounts.size() == 0) ? null : (CMLAmount) volAmounts.get(0);
        	// and get the molar amount (mmol)
        	Nodes molarAmounts = ((CMLElement)reactant).query(".//cml:amount[@units='"+Units.MMOL+"']", X_CML);
        	CMLAmount molarAmount = (molarAmounts.size() == 0) ? null : (CMLAmount) molarAmounts.get(0);
        	// calculate molarAmount from either mass or volume and check it agrees with molarAmount
        	CMLAmount calcMolarAmount = null;
        	if (massAmount != null) {
        		calcMolarAmount = massAmount.getMolarAmount(molecule);
        	} else if (volAmount != null) {
        		calcMolarAmount = volAmount.getMolarAmountFromVolume(molecule);
        	}
        	if (calcMolarAmount == null) {
        		System.out.print("Null calculated amount ");
        	} else {
        		System.out.print("CALC "+((int)(calcMolarAmount.getXMLContent() * 100000))/100. + " // ");
        		if (molarAmount != null) {
        			System.out.print(molarAmount.getXMLContent());
        		}
        	}
        	System.out.println("("+molecule.getTitle()+")");
        }
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
    public static void testSVG(String[] args) throws Exception {
    	if (args.length < 3 ) {
    		System.out.println("SVG infile outfile");
    	} else {
    		String infile = args[1];
    		String outfile = args[2];
	    	InputStream is = Util.getInputStreamFromResource(infile);
	    	MoleculeDisplayList graphicsManager = new MoleculeDisplayList(outfile);
	    	CMLReaction reaction = (CMLReaction) new CMLBuilder().build(is).getRootElement();
	    	
			graphicsManager.setAndProcess(ReactionTool.getOrCreateTool(reaction));
	    	graphicsManager.createOrDisplayGraphics();
    	}
    } 
    
    static void usage() {
    	System.out.println("java org.xmlcml.cml.tools.ReactionToolTest <options>");
    	System.out.println("... options ...");
    	System.out.println("-SVG inputfile outputfile <options>");
    }
    
    /** main
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	if (args.length == 0) {
    		System.out.println("Args is 0");
    		usage();
    	} else {
    		if (args[0].equalsIgnoreCase("-SVG")) {
    			testSVG(args);
    		}
    	}
    }
    
 }
