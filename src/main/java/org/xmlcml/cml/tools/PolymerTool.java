package org.xmlcml.cml.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.xslt.XSLTransform;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.main.CountExpressionAttribute;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.main.CMLFragment;
import org.xmlcml.cml.element.main.CMLJoin;
import org.xmlcml.cml.element.main.CMLMoleculeList;
import org.xmlcml.cml.element.main.CMLTorsion;
import org.xmlcml.cml.element.main.CMLTransform3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;


/**
 * tool to support polymer building. not fully developed
 * 
 * @author pmr
 * 
 */
public class PolymerTool extends AbstractTool {

    private static Logger LOG = Logger.getLogger(PolymerTool.class.getName());
    
    /** polymer conventions.
     * 
     */
    public enum Convention {
    	/** signifies a branch */
    	BRANCH("branch"),
        /** concise formula string - obsolete.*/
        PML_CONCISE(C_A+"PML-concise"),
        /** basic XML formula.*/
        PML_BASIC(C_A+"PML-basic"),
        /** molecule references.*/
        PML_INTERMEDIATE(C_A+"PML-intermediate"),
        /** explicit un-joined molecules.*/
        PML_EXPLICIT(C_A+"PML-explicit"),
        /** complete molecules (includes cartesian coords).*/
        PML_COMPLETE(C_A+"PML-complete"),
        /** inline atom obsolete.*/
        PML_INLINE_ATOM(C_A+"PML-inline-atom"),
        /** default endpoint.*/
        PML_DEFAULT_FINAL(PML_COMPLETE.v),
        /** processed (nothing further to do) Normally Markush.*/
        PML_PROCESSED(C_A+"PML-processed"),
        ;
        String v;
        private Convention(String v) {
            this.v = v;
        }
    }
    
//    Set<Convention> debugSet = new HashSet<Convention>();
    
    File OUTPUT_DIR = Util.getTestOutputDirectory(PolymerTool.class);

    // root might be a molecule
    private CMLMolecule molecule = null;
    // or a moleculeList
    private CMLMoleculeList moleculeList = null;
    // in which case it generates child polymerTools
    private List<PolymerTool> polymerToolList = new ArrayList<PolymerTool>();
    // target
    private Convention targetLevel = Convention.PML_COMPLETE;
    // debug
    private boolean debug = false;
    // catalog
    private Catalog moleculeCatalog = null;

    
    /** constructor.
     */
    public PolymerTool() {
    }

    /** constructor.
     * 
     * @param molecule
     */
    public PolymerTool(CMLMolecule molecule) {
        setMolecule(molecule);
    }

    /** constructor.
     * 
     * @param element molecule or moleculeList
     */
    public PolymerTool(CMLElement element) {
        setElement(element);
    }

    /** set molecule.
     * 
     * @param molecule
     */
    public void setMolecule(CMLMolecule molecule) {
        this.molecule = molecule;
    }

    /** set targetLevel.
     * 
     * @param targetLevel
     */
    public void setTargetLevel(Convention targetLevel) {
        this.targetLevel = targetLevel;
    }

    /** set debug.
     * 
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

//    /** set fileroot.
//     * 
//     * @param fileroot
//     */
//    public void setFileroot(String fileroot) {
//        this.fileroot = fileroot;
//    }

    /** set molecule or moleculeList
     * 
     * @param element
     */
    public void setElement(CMLElement element) {
        if (element instanceof CMLMolecule) {
            this.setMolecule((CMLMolecule) element);
        } else if (element instanceof CMLMoleculeList) {
                this.setMoleculeList((CMLMoleculeList) element);
        } else {
            throw new RuntimeException(
                "must pass PolymerTool a molecule or moleculeList: found "+element.getClass());
        }
    }

    /** constructor.
     * 
     * @param moleculeList
     */
    public PolymerTool(CMLMoleculeList moleculeList) {
        this.setMoleculeList(moleculeList);
    }

    /** set moleculeList.
     * 
     * @param moleculeList
     */
    public void setMoleculeList(CMLMoleculeList moleculeList) {
        this.moleculeList = moleculeList;
    }

    /** set molecule catalog
     * @param catalog
     */
    public void setMoleculeCatalog(Catalog catalog) {
        this.moleculeCatalog = catalog;
    }
    /** process current molecule with its convention attribute.
     * the attribute can be re-adjusted by the process so this routine may be called 
     * several times in sequence.
     *
     */
    public void processConvention() {
        if (moleculeList != null) {
            LOG.debug("==========MOLLIST=========");
            List<Node> nodes = CMLUtil.getQueryNodes(
                    moleculeList, CMLMolecule.NS+"[@countExpression]", CML_XPATH);
            if (nodes.size() == 1) {
                CMLMolecule molecule0 = (CMLMolecule) nodes.get(0);
                CountExpressionAttribute.generateAndInsertClones(molecule0);
                molecule0.removeAttribute("countExpression");
            }
            CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
//            moleculeList.debug("MOLPOL");
            int i = 0;
            for (CMLMolecule molecule0 : molecules) {
                PolymerTool polymerTool = new PolymerTool(molecule0);
                polymerTool.setMoleculeCatalog(this.moleculeCatalog);
                polymerToolList.add(polymerTool);
                polymerTool.processConventionExhaustively();
//                molecule0.debug("MOL "+i++);
                LOG.debug("============ "+ i++ +" ================");
            }
        } else if (molecule != null){
            String convention = molecule.getConvention();
            if (convention == null) {
                throw new RuntimeException("no convention given for: "+molecule.getId());
            }
            LOG.debug("=========="+convention+"=========");
            if (false) {
                //
            } else if (convention.equals(Convention.PML_INLINE_ATOM.v)) {
                processInlineAtom();
            } else if (convention.equals(Convention.PML_CONCISE.v)) {
                processConcise();
            } else if (convention.equals(Convention.PML_BASIC.v)) {
                processBasic();
            } else if (convention.equals(Convention.PML_INTERMEDIATE.v)) {
                processIntermediate();
            } else if (convention.equals(Convention.PML_EXPLICIT.v)) {
                processExplicit();
            } else if (convention.equals(Convention.PML_COMPLETE.v)) {
                LOG.debug("**********COMPLETE cannot be futher processed now ********");
//                processZMatrix();
            }
            if (debug) {
                debug(convention, molecule);
            }
        }
    }
    
    /** read moleculeList.
     * 
     * @param filename
     * @return list
     * @throws Exception
     */
    public CMLMoleculeList readMoleculeList(String filename) throws Exception {
        CMLMoleculeList molList = null;
        InputStream in = null;
        in = Util.getInputStreamFromResource(filename);
        if (in == null) {
            molList = (CMLMoleculeList) new CMLBuilder().build(
                new StringReader(
                    S_EMPTY
                        + "<?xml version='1.0' encoding='UTF-8'?>"
                        + "<moleculeList  xmlns='http://www.xml-cml.org/schema'/>"
                        + S_EMPTY)).getRootElement();
        } else {
            molList = (CMLMoleculeList) new CMLBuilder().build(in)
                    .getRootElement();
        }
        return molList;
    }

    /** debug.
     * 
     * @param s
     * @param molTest
     */
    public void debug(String s, CMLElement molTest) {
        if (molTest instanceof CMLMolecule) {
            debug(s, (CMLMolecule) molTest) ;
        } else if (molTest instanceof CMLMoleculeList) {
            CMLElements<CMLMolecule> molecules = ((CMLMoleculeList) molTest).getMoleculeElements();
            for (CMLMolecule molecule : molecules) {
                debug(s, molecule) ;
            }
        }
    }
    
    /** connection table as inline atoms.
     * only for single molecule at present
     */
    @SuppressWarnings("unused")
    private void processInlineAtom() {
        if (molecule != null){
            String formula = molecule.getFormula();
            if (formula == null) {
                throw new RuntimeException("no formula given");
            }
            formula = formula.replace(S_NEWLINE, S_EMPTY);
            formula = formula.replace(S_SPACE, S_EMPTY);
            InlineMolecule inlineMolecule = new InlineMolecule(formula);
            CMLMolecule cmlMolecule = inlineMolecule.getCmlMolecule();
            molecule.setConvention(Convention.PML_COMPLETE.v);
            molecule.removeAttribute("formula");
        } else {
            throw new RuntimeException("must have molecule");
        }
    }
    
    /** process concise formula.
     * 
     * as of 2006-08-01 molecule with a typical concise formula looks like:
<molecule id='gly0'
xmlns='http://www.xml-cml.org/schema'
xmlns:g='http://www.xml-cml.org/mols/geom'
convention='cml:PML-concise'
formula='
 (g:oh
   }r1)-
[l(1.40)]
(r1
  {g:gly
    %phi=65,psi=55%
  }r2
  -[l(1.40)t(180)])
    *(10)
-[l(1.54)]
(r1{g:cl)'
/>
     * note that within @formula all whitespace including newlines will be ignored.
     * the molecule@formula is an inline description of the structure
     * consisting of the fragments to be joined and some joining instructions.
     * in some cases additional info is held, normally by macro variables (%foo);
     * The detailed structure is:
     * leftCap-bond-middle1(count)-bond-middle2(count)-bond-...
     *     middlen(count)-bond-rightCap
     * leftCap, middle and rightCap are normally links/pointers to molecules
     * in a dictionary/repository
     */
    private void processConcise() {
        if (moleculeList != null) {
        } else if (molecule != null){
            String formula = molecule.getFormula();
            if (formula == null) {
                throw new RuntimeException("no formula given");
            }
            formula = formula.replace(S_NEWLINE, S_EMPTY);
            formula = formula.replace(S_SPACE, S_EMPTY);
            boolean old = false;
            if (old) {
//                FragmentSequence fragmentSequence = new FragmentSequence(formula);
//                CMLJoin topJoin = fragmentSequence.getCMLJoin();
//                molecule.appendChild(topJoin);
//                molecule.setConvention(Convention.PML_BASIC.value);
//                molecule.removeAttribute("formula");
            } else {
                FragmentSequence fragmentSequence = new FragmentSequence(formula);
                CMLMoleculeList topMoleculeList = fragmentSequence.getCMLMoleculeList();
                molecule.appendChild(topMoleculeList);
                molecule.setConvention(Convention.PML_BASIC.v);
                molecule.removeAttribute("formula");
            }
        } else {
            throw new RuntimeException("must have molecule or moleculeList");
        }
    }
    
    
    private void processBasic() {
        CMLFragment fragment0 = (CMLFragment) molecule.getFirstCMLChild(CMLFragment.TAG);
        if (fragment0 == null) {
            throw new RuntimeException("expected fragment child");
        }
        FragmentTool fragmentTool = FragmentTool.getOrCreateTool(fragment0);
        fragmentTool.processBasic(moleculeCatalog);
    }
    
//    private static void recursiveProcessJoins(
//            CMLMoleculeList moleculeList, CMLMolecule parent) {
////      * moleculeList
////      *     complexMol?
////      *     (join
////      *     complexMol)?
////      *     (join
////      *     complexMol)?
//        List<CMLElement> childEntries = moleculeList.getChildCMLElements();        
//        CMLMolecule previousMolecule = parent;
//        CMLJoin join = null;
//        CMLLabel parentLabel = null;
//        if (parent != null) {
//            parentLabel = CMLLabel.getLabel(moleculeList, Position.PARENT);
//            if (parentLabel == null) {
//                throw new RuntimeException("moleculeList must have parent label: "+moleculeList.getId());
//            }
//        }
//        for (int i = 0; i < childEntries.size(); i++) {
//            CMLElement child = childEntries.get(i);
//            if (parent == null && i == 0) {
//                // only occurs at root of molecule
//                if (child instanceof CMLMolecule) {
//                    join.addAtomRefs2ToJoinAndProcessDescendants(previousMolecule, (CMLMolecule) child, null);
//                    previousMolecule = (CMLMolecule) child;
//                } else {
//                    throw new RuntimeException("Expected complex molecule");
//                }
//            } else if (child instanceof CMLJoin) {
//                // trawl through join+molecule to find next molecule
//                join = (CMLJoin) child;
//                CMLMolecule molecule = null;
//                for (int j = i+1; j < childEntries.size(); j++) {
//                    CMLElement nextChild = childEntries.get(j);
//                    i++;
//                    if (nextChild instanceof CMLMolecule) {
//                        molecule = (CMLMolecule) nextChild;
//                        break;
//                    } else if (nextChild instanceof CMLJoin) {
//                        throw new RuntimeException("Unexpected join");
//                    } else {
//                        LOG.debug("Skipped element: "+nextChild);
//                    }
//                }
//                if (molecule == null) {
//                    LOG.debug("Join has no following molecule sibling");
//                }
//                join.addAtomRefs2ToJoinAndProcessDescendants(previousMolecule, molecule, parentLabel);
//                parentLabel = null;
//                previousMolecule = molecule;
//            } else if (child instanceof CMLLabel) {
//            } else {
//                LOG.debug("Skipped element: "+child);
//            }
//        }
//    }
    
    private void processIntermediate() {
// probably obsolete        
        //should consist of molecule (join, molecule)*
        // give join default bond orders
        List<Node> nodes = CMLUtil.getQueryNodes(molecule, ".//"+CMLJoin.NS+"[not(@order)]", CML_XPATH);
        for (Node node : nodes) {
            ((CMLJoin) node).setOrder(CMLBond.SINGLE_S);
        }
        // expand random torsions
        List<Node> torsions = CMLUtil.getQueryNodes(molecule, ".//"+CMLTorsion.NS+"[@min and @max]", CML_XPATH);
        for (Node node : torsions) {
            CMLTorsion torsion = (CMLTorsion) node;
            String countExpression = "range("+torsion.getMin()+S_COMMA+torsion.getMax()+S_RBRAK;
            int value = new CountExpressionAttribute(countExpression).calculateCountExpression();
            torsion.setXMLContent((double)value);
            torsion.removeAttribute("min");
            torsion.removeAttribute("max");
            torsion.debug("MINMAX");
        }
        // lookup referenced molecules
        if (moleculeCatalog == null) {
            new Exception().printStackTrace();
            throw new RuntimeException("Cannot processIntermediate without moleculeCatalog");
        }
        CMLElements<CMLMolecule> subMoleculeList = molecule.getMoleculeElements();
        for (int i = 0; i < subMoleculeList.size(); i++) {
        }
        molecule.setConvention(Convention.PML_EXPLICIT.v);
    }
    
    /** convenience method to generate detailed molecules.
     * iteratively calls processConvention until nothing more
     * can be done.
     * @throws RuntimeException
     */
    public void processConventionExhaustively() throws RuntimeException {
        processConventionExhaustively((Convention) null);
    }

    /** convenience method to generate detailed molecules.
     * iteratively calls processConvention until either noting more
     * can be done or the convention reached is equal to convention
     * @param convention terminating convention
     * @throws RuntimeException
     */
    private void processConventionExhaustively(Convention convention) throws RuntimeException {
        int i = 0;
        if( convention == null ){
        	convention = Convention.PML_DEFAULT_FINAL;
        	LOG.debug("Assuming target level: "+convention);
        }
        if (moleculeList != null) {
            processConvention();
            tidyMoleculeList();
        } else if (molecule != null) {
            while (true) {
                if (i++ > 7) break;
                String moleculeConvention = molecule.getConvention();
                if (moleculeConvention == null || 
                    moleculeConvention.equals(S_EMPTY) || 
                    moleculeConvention.equals(convention.v) ||
                    moleculeConvention.equals(Convention.PML_COMPLETE.v)) {
                    break;
                }
                try {
                    this.processConvention();
                    moleculeConvention = molecule.getConvention();
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.err.println("Unexpected throwable "+t+S_SPACE+molecule.getConvention());
                    throw new RuntimeException("cannot processConvention "+molecule.getConvention()+"; "+t);
                }
            }
        } else {
            throw new RuntimeException("null molecule and moleculeList");
        }
    }
    
    
    private void tidyMoleculeList() {
        CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
        Transform3 fullTransform = new Transform3();
        for (CMLMolecule molecule : molecules) {
            List<Node> transforms = CMLUtil.getQueryNodes(molecule, CMLTransform3.NS, CML_XPATH);
            if (transforms.size() == 1) {   
            	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
                moleculeTool.transformCartesians(fullTransform);
                CMLTransform3 transform = (CMLTransform3) transforms.get(0);
                fullTransform = fullTransform.concatenate(transform.getEuclidTransform3());
            }
        }
        // replace moleculelist by molecule (for Jmol)
        molecule = new CMLMolecule();
        CMLUtil.transferChildren(moleculeList, molecule);
        ParentNode parent = moleculeList.getParent();
        parent.replaceChild(moleculeList, molecule);
        // obsolete moleculeList
        moleculeList = null;
    }

    /** primary way of of running.
     * 
     * @param infile
     * @param debug
     * @param fragments
     * @param targetLevel
     * @param outfileName
     * @exception Exception
     */
    public void processConventionExhaustively(
            String infile, String fragments, String outfileName, 
            Convention targetLevel, boolean debug) throws Exception {
    
        Document doc = new CMLBuilder().build(new File(infile));
        Nodes nodes = doc.query(CMLMolecule.NS+X_OR+CMLMoleculeList.NS, CML_XPATH);
        if (nodes.size() == 0) {
            throw new RuntimeException("No CML Molecule(List) in file: "+infile);
        }
        CMLElement molecule = (CMLElement) nodes.get(0);
        LOG.debug("Read input: "+infile+" ("+
                molecule.getAttributeValue("title")+S_RBRAK);
            
        this.setElement(molecule);
        
        this.setMoleculeCatalog(new Catalog(new File(fragments)));
        LOG.debug("Processing to level: "+targetLevel.v);
        this.processConventionExhaustively(targetLevel);

        write(outfileName);
    }
    
    /** process.
     */
    public void processExplicit() {
    	throw new RuntimeException("probably obsolete");
    }
    
    /** write.
     * 
     * @param outfileName
     * @throws Exception
     */
    public void write(String outfileName) throws Exception {
        if (!outfileName.equals(S_EMPTY)) {
            File outfile = new File(outfileName);
            if (!outfile.exists()) {
                File dir = outfile.getParentFile();
                if (!dir.exists()) {
                    boolean ok = dir.mkdirs();
                    if (!ok) {
                        throw new RuntimeException("Cannot make new directory for: "+outfileName);
                    }
                }
                outfile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outfileName);
            LOG.debug("Writing (level = "+targetLevel.v+"): "+outfileName);
            ((molecule != null) ? molecule : moleculeList).serialize(fos, 1);
        }
    }
    
    /** create from template.
     * 
     * @param stylesheetFilename
     * @param parameters
     * @param catalog
     * @return fragment
     */
    public static CMLFragment createFromTemplate(
		String stylesheetFilename, List<XSLParam> parameters, Catalog catalog) {
    	
    	LOG.debug("XSL "+stylesheetFilename);
    	for (XSLParam param : parameters) {
    		LOG.debug(param.name + "=" + param.value);
    	}
    	Document dummyDoc = new Document(new Element("dummy"));
    	Element element = null;
    	Builder builder = new Builder();
    	try {
	        Document stylesheet = builder.build(stylesheetFilename);
	        XSLTransform transform = new XSLTransform(stylesheet);
	    	for (XSLParam param : parameters) {
	    		transform.setParameter(param.name, param.value);
	    	}
	        element = (Element) transform.transform(dummyDoc).get(0);
    	} catch (Exception e) {
    		throw new RuntimeException("should never throw "+e);
    	}
    	String basicXML = CMLUtil.getCanonicalString(element);
    	CMLFragment fragment = null;
    	try {
    		fragment = (CMLFragment) new CMLBuilder().parseString(basicXML);
    	} catch (Exception e) {
    		throw new RuntimeException("should not throw: "+e.getMessage());
    	}
    	FragmentTool.getOrCreateTool(fragment).processAll(catalog);
    	return fragment;
    }
    
    private static void usage() {
        System.out.println("java org.xmlcml.cml.tools.PolymerTool1 [args]");
        System.out.println("    -INFILE filename // XML input file; must include convention ");
        System.out.println("    -OUTFILE filename // XML output file");
        System.out.println("    -BASIC, -INTERMEDIATE, -EXPLICIT, -COMPLETE");
        System.out.println("    -DEBUG");
        System.out.println("  OR;  -TEMPLATE [-PARAM name value]* -CATALOG catalog");
    }
    
    /** runs Polymer Tool including building polymers.
     * Reads an input file describing the polymer, links to fragment library and outputs
     * to file at given level (default CARTESIAN).
     * 
     * The commandline args are:
     * <ul>
     *   <li>INFILE - input (must by in CML with a PML convention</li>
     *   <li>FRAGMENTS - the <b>catalog</b> describing the location of fragments</li>
     *   <li>OUTFILE - the output</li>
     *   <li>BASIC | INTERMEDIATE | EXPLICIT | COMPLETE - target output level</li>
     *   <li>DEBUG</li>
     * </ul>
     * Typical usage could be:
     * <pre>
     *  java org.xmlcml.cml.tools.PolymerTool1
     * -INFILE C:\some\where\examples\experimental\branch2_concise.xml
     * -FRAGMENTS C:\some\where\else\examples\molecules\catalog.xml
     * -OUTFILE C:\temp\branch2.xml
     * -DEBUG
     * 
     * </pre>
     * @param args
     * -FILE filename //input tool
     * [level] //output level (BASIC, INTERMEDIATE, EXPLICIT, COMPLETE, CARTESIAN)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
//            System.exit(0);
        } else {
            String infile = S_EMPTY;
            String outfileName = S_EMPTY;
            String fragments = S_EMPTY;
            Convention targetLevel = null;
            String template = S_EMPTY;
            List<XSLParam> paramList = new ArrayList<XSLParam>();
            int i = 0;
            boolean debug = true;
        	@SuppressWarnings("unused")
            List<String> debugList = new ArrayList<String>();
            while (i < args.length) {
                if (args[i].equalsIgnoreCase("-INFILE")) {
                    infile = args[++i]; i++;
                } else if (args[i].equalsIgnoreCase("-OUTFILE")) {
                    outfileName = args[++i]; i++;
                } else if (args[i].equalsIgnoreCase("-BASIC")) {
                    targetLevel = Convention.PML_BASIC; i++;
                } else if (args[i].equalsIgnoreCase("-INTERMEDIATE")) {
                    targetLevel = Convention.PML_INTERMEDIATE; i++;
                } else if (args[i].equalsIgnoreCase("-EXPLICIT")) {
                    targetLevel = Convention.PML_EXPLICIT; i++;
                } else if (args[i].equalsIgnoreCase("-COMPLETE")) {
                    targetLevel = Convention.PML_COMPLETE; i++;
                } else if("-FRAGMENTS".equalsIgnoreCase(args[i])) {
                    fragments = args[++i];
                    i++;
                } else if("-DEBUG".equalsIgnoreCase(args[i])) {
                    debug = true;
                    i++;
                } else if (args[i].equalsIgnoreCase("-TEMPLATE")) {
                    template = args[++i]; i++;
//                } else if (args[i].equalsIgnoreCase("-CATALOG")) {
//                    catalog = args[++i]; i++;
                } else if (args[i].equalsIgnoreCase("-PARAM")) {
                    paramList.add(new XSLParam(args[++i], args[++i])); i++;
                }else {
                	System.err.println("Bad arg "+ args[i]);
                	i++;
                }
            }
            
            if (!S_EMPTY.equals(template)) {
  //          	CatalogManager catalogManager = CatalogManager.getTopCatalogManager();
//        		Catalog moleculeCatalog = catalogManager.getCatalog(Catalog.MOLECULE_CATALOG);
            	if (!S_EMPTY.equals(outfileName)) {
//            		FileWriter fw = new FileWriter(outfileName);
            	}
            } else {
	            if (targetLevel == null){
	            	targetLevel = Convention.PML_DEFAULT_FINAL;
	            	System.out.println("No level specified. Assuming level: "+targetLevel);
	            }
	            if(S_EMPTY.equals(fragments)) {
	                System.err.println("No fragments found; please give -FRAGMENTS");
	            }
	            if (!infile.equals(S_EMPTY)) {
	                try {
	                    PolymerTool polymerTool = new PolymerTool();
	                    polymerTool.processConventionExhaustively(
	                        infile, fragments, outfileName, targetLevel, debug);
	                } catch (Exception e) {
	                    System.err.println("ERROR... "+e);
	                    e.printStackTrace();
	                }
	            }
	        }
        }
    }
}
class XSLParam {
	String name;
	String value;
	
	XSLParam(String name, String value) {
		this.name = name;
		this.value = value;
	}
}