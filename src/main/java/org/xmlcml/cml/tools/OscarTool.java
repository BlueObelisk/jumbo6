package org.xmlcml.cml.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.lite.CMLCml;
import org.xmlcml.cml.element.lite.CMLFormula;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.lite.CMLName;
import org.xmlcml.cml.element.lite.CMLProperty;
import org.xmlcml.cml.element.lite.CMLPropertyList;
import org.xmlcml.cml.element.lite.CMLScalar;
import org.xmlcml.cml.element.main.CMLAction;
import org.xmlcml.cml.element.main.CMLConditionList;
import org.xmlcml.cml.element.main.CMLLink;
import org.xmlcml.cml.element.main.CMLModule;
import org.xmlcml.cml.element.main.CMLMoleculeList;
import org.xmlcml.cml.element.main.CMLObject;
import org.xmlcml.cml.element.main.CMLProduct;
import org.xmlcml.cml.element.main.CMLProductList;
import org.xmlcml.cml.element.main.CMLReaction;
import org.xmlcml.cml.element.main.CMLReactionScheme;
import org.xmlcml.cml.element.main.CMLSpectrum;
import org.xmlcml.cml.element.main.CMLSubstance;
import org.xmlcml.euclid.Util;

/**
 * tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
@SuppressWarnings("unchecked")

public class OscarTool implements CMLConstants {

    static String OSCAR_CONTAINER = "container";
    static String OSCAR_DATASECTION = "datasection";
    static String OSCAR_NSP = "osc";
    static String OSCAR_NSPUNIT = "oscUnits";
    static String OSCAR_ROLE = "role";
    static String OSCAR_SENTENCE_END = "sentenceEnd";
    
    // this should be read from file...
    static List<String> badFormulas = new ArrayList<String>();
    // strings that are not chemical formulae
    static {
        badFormulas.add("CC");
        badFormulas.add("CD");
        badFormulas.add("SCC");
        badFormulas.add("VIS");
    };
    
    // mappings
    static String OSCAR_DIR = "org/xmlcml/cml/tools/oscar";
    static Map<Pattern, String> actionMap = null;
    static Map<Integer, String> charactersMap = null;
    static Map<Pattern, String> conditionMap = null;
    static Map<Pattern, String> mergeableMoleculeMap = null;
    static Map<Pattern, String> objectMap = null;
    static Map<String, String> powerUnitsMap = null;
    static Map<String, String> rawUnitTypeMap = null;
    static Map<Pattern, String> statePropertyMap = null;
    static Map<Pattern, String> substanceMap = null;
    static Map<Pattern, String> temperatureMap = null;
    static Map<String, String> unitsMap = null;
    static Map<String, String> unitTypeMap = null;
    static Map<String, String> verbMap = null;
    static {
//    <states>
//    <!--  order may be important -->
//        <state term="acid(ic|ified)" dictRef=C_A+"acidic"/>
        actionMap = getLexicalMap(OSCAR_DIR+U_S+"action.xml");
        charactersMap = getLexicalMap(OSCAR_DIR+U_S+"characters.xml");
        conditionMap = getLexicalMap(OSCAR_DIR+U_S+"condition.xml");
        mergeableMoleculeMap = getLexicalMap(OSCAR_DIR+U_S+"mergeableMolecule.xml");
        objectMap = getLexicalMap(OSCAR_DIR+U_S+"object.xml");
        powerUnitsMap = getLexicalMap(OSCAR_DIR+U_S+"powerUnits.xml");
        rawUnitTypeMap = getLexicalMap(OSCAR_DIR+U_S+"rawUnitType.xml");
        statePropertyMap = getLexicalMap(OSCAR_DIR+U_S+"state.xml");
        substanceMap = getLexicalMap(OSCAR_DIR+U_S+"substance.xml");
        temperatureMap = getLexicalMap(OSCAR_DIR+U_S+"temperature.xml");
        unitsMap = getLexicalMap(OSCAR_DIR+U_S+"units.xml");
        unitTypeMap = getLexicalMap(OSCAR_DIR+U_S+"unitType.xml");
        verbMap = getLexicalMap(OSCAR_DIR+U_S+"verb.xml");
    };

    static Map getLexicalMap(String file) {
        Map map = null;
        try {
            InputStream is = Util.getInputStreamFromResource(file);
            Document doc = new Builder().build(is);
            Element root = doc.getRootElement();
            Elements entrys = root.getChildElements();
            String key = root.getAttributeValue("key");
            boolean regex = "regex".equals(key);
            boolean intKey = "intValue".equals(key);
            String value = root.getAttributeValue("value");
            if (intKey) {
                map = new LinkedHashMap<Integer, String>();
            } else if (regex){
                map = new LinkedHashMap<Pattern, String>();
            } else {
                map = new LinkedHashMap<String, String>();
            }
            for (int i = 0; i < entrys.size(); i++) {
                Element entry = (Element) entrys.get(i);
                if (intKey) {
                    map.put(new Integer(entry.getAttributeValue(key)), entry.getAttributeValue(value));
                } else if (regex){
                    Pattern pattern = Pattern.compile(entry.getAttributeValue("regex"),
                            Pattern.CASE_INSENSITIVE);
                    map.put(pattern, entry.getAttributeValue(value));
                } else {
                    map.put(entry.getAttributeValue(key), entry.getAttributeValue(value));
                }
            }
        } catch (Exception e) {
            Util.BUG("Error in static for entryMap: ", e);
        }
        return map;
    };
    
    Document doc = null;
    
    CMLCml cml;
    
    CMLModule abstractM;
    CMLModule authorListM;
    CMLModule bodyM;
    CMLModule conclusionsM;
    CMLModule experimentalM;
    CMLModule introductionM;
    CMLModule metadataM;
    CMLModule resultsM;
    CMLModule discussionM;
    CMLModule titleM;
    
    int ncontain = 0;
    /** constructor.
     * 
     * @param doc
     */
    public OscarTool(Document doc) {
        this.doc = doc;
    }


    /** get reaction schemes from Experimental.
     * 
      */
    public void convertToCML() {
        Element elem;
        cml = new CMLCml();
        Element root = doc.getRootElement();
        doc.replaceChild(root, cml);
        CMLUtil.transferChildren(root, cml);
        

        tidyParseBugs();
//        flattenInlineMarkup();
        
        processNamedEntities();
//        processNamedEntities();
        
        processSpectra();
        
        additionalMarkup();
        
        Nodes metadatas = doc.query("/*/METADATA");
        elem = (metadatas.size() == 1) ? (Element) metadatas.get(0) : null;
        processMetadata(elem);
        
        Nodes titles = doc.query("/*/TITLE");
        elem = (titles.size() == 1) ? (Element) titles.get(0) : null;
        processTitle(elem);
        
        Nodes authorLists = doc.query("/*/AUTHORLIST");
        elem = (authorLists.size() == 1) ? (Element) authorLists.get(0) : null;
        processAuthorList(elem);
        
        Nodes abstracts = doc.query("/*/ABSTRACT");
        elem = (abstracts.size() == 1) ? (Element) abstracts.get(0) : null;
        processAbstract(elem);
        
        Nodes bodys = doc.query("/*/BODY");
        elem = (bodys.size() == 1) ? (Element) bodys.get(0) : null;
        processBody(elem);
        
        // tidy containers ending with ( or ending with )
        processBrackets();

        processConjunctions();
        processSentences();
        
        aggregatePropertyAndMolecules();
        aggregateMolecules();

//        tidyIsolatedUnits();
        findSolvents();

        processAmounts();
        
        processDataSections();
        
        removeEmptyContainers();
        flattenModules();
        
        
//        Nodes nodes = cml.query(".//*[local-name()='module']");
//        Nodes mods = cml.query(".//"+CMLModule.NS, CML_XPATH);
//        if (nodes.size() != mods.size()) {
//            System.out.println("MODULES....."+nodes.size()+S_SLASH+mods.size());
//        }
    }

    private void tidyParseBugs() {
//        <quantity type="time"><value><point>5</point></value> <units>h</units>our<units>s</units></quantity>
//        Bad units for time
//        <?xml version="1.0" encoding="UTF-8"?>
//        <quantity type="time"><value><point>30</point></value> <units>min</units>ute<units>s</units></quantity>
        Nodes units = cml.query(".//units");
        int nn = units.size();
        for (int i = nn - 1; i >= 0; i--) {
            Element unit = (Element) units.get(i);
            if (unit == null) continue;
            // <units>h</units>our<units>s</units>
            detach(unit, "h", "our", "s");
            // <units>min</units>ute<units>s</units>
            detach(unit, "min", "ute", "s");
        }

//        <units>
//        <ne type="CM" provenance="oscarLexicon" Element="K">K</ne> 
//        </units>
        units = cml.query(".//units");
        nn = units.size();
        for (int i = nn - 1; i >= 0; i--) {
            Element unit = (Element) units.get(i);
            if (unit == null) continue;
            Element ne = unit.getFirstChildElement("ne");
            if (ne != null && "K".equals(ne.getValue())) {
                ne.detach();
                unit.appendChild(new Text("K"));
            }
        }
        
        // "No", "As" are stopwords...
        remove2LetterElements();
        
        redeemVerbsFromAdjectives();
    }

    // not yet used
    @SuppressWarnings("unused")
    private void tidyIsolatedUnits() {
        List<Node> units = CMLUtil.getQueryNodes(cml, ".//unit");
        for (Node node : units) {
            Element unit = (Element) node;
            CMLUtil.debug(unit, "OSCAR");
        }
    }
    
    @SuppressWarnings("unused")
    private void identifyReactants() {
        // To [molecule]
        
    }

    // not used
    @SuppressWarnings("unused")
    private void flattenInlineMarkup() {
        List<Node> nodeList = CMLUtil.getQueryNodes(doc, ".//SB | .//SP | .//IT");
        for (Node node : nodeList) {
            String name = ((Element)node).getLocalName();
            String value = "__"+name+node.getValue()+name+"__";
            ParentNode parent = node.getParent();
            int idx = parent.indexOf(node);
            parent.replaceChild(node, new Text(value));
        }
    }
    
    private void remove2LetterElements() {
        //<ne type="CM" provenance="oscarLexicon" Element="In">In</ne> 
        Nodes nodes = cml.query(".//ne");
        remove2LetterElements(nodes, "Element");
        
        //<ne type="CM" provenance="oscarLexicon">As</ne> 
        remove2LetterElements(nodes, "EL");
    }

    private void redeemVerbsFromAdjectives() {
//        was  <property type="state">dried</property>        
        List<Node> propList = CMLUtil.getQueryNodes(doc, 
                ".//property[@type='state']");
        for (Node node : propList) {
            Element prop = (Element) node;
            List<Node> pSibs = CMLUtil.getQueryNodes(prop, 
                    "./preceding-sibling::node()[" +
                    "position()=1 and " +
                    "self::text()]");
            if (pSibs.size() > 0 ) {
                Text pSib = (Text) pSibs.get(0);
                String s = pSib.getValue().trim();
                if (s.endsWith(" was") ||
                        s.endsWith(S_COMMA) ||
                        s.endsWith("and")) {
                    for (String verb : verbMap.keySet()) {
                        if (verb.equalsIgnoreCase(prop.getValue())) {
                            CMLAction action = new CMLAction();
                            action.setTitle(verb);
                            prop.getParent().replaceChild(prop, action);
//                            System.out.println("replaced state by verb: "+verb);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void remove2LetterElements(Nodes nodes, String elem) {
        for (int i = 0; i < nodes.size(); i++) {
            
            Element molecule = (Element) nodes.get(i);
            String title = (elem.equals("EL")) ? molecule.getValue() :
                molecule.getAttributeValue(elem);
            if (
                "As".equals(title) || 
                "At".equals(title) || 
                "Be".equals(title) || 
                "He".equals(title) || 
                "In".equals(title) || 
                "No".equals(title) || 
                false) {
                Text text = new Text(title);
                ParentNode parent = molecule.getParent();
                if (parent != null) {
                    parent.replaceChild(molecule, text);
                }
            }
        }
    }
    
    private void processBrackets() {
        Nodes nodes = bodyM.query(".//"+CMLModule.NS+"[@role='container']", CML_XPATH);
        List<CMLModule> moduleList = new ArrayList<CMLModule>();
        for (int i = 0; i < nodes.size(); i++) {
            CMLModule module = (CMLModule) nodes.get(i);
            moduleList.add(module);
        }
        for (CMLModule module : moduleList) {
            // value may contain inline markup so only take first bit
            if (module.getChildCount() > 0) {
                processLeadingRightBracket(module);
                processTrailingLeftBracket(module);
            }
        }
        groupBrackets();

//        removeEmptyContainers();
        
        conflateMoleculeAndFollowingBalancedBrackets();
        conflatePropertyAndFollowingMolecule();
        
        removeBalancedBracketParentFromMolecule();
        removeBalancedBracketChildFromMolecule();
        
    }
    
    private void groupBrackets() {
        // this is not yet fully developed as some non-semantic RBRACKETS are picked up
        // now group brackets
        List<Node> lbrakList = CMLUtil.getQueryNodes(bodyM, ".//"+CMLModule.NS+"[@role='lbracket']", CML_XPATH);
        for (Node lbrak : lbrakList) {
            Nodes nodes = lbrak.query("./following-sibling::"+CMLModule.NS+"[@role='rbracket']",
                    CML_XPATH);
            if (nodes.size() == 0) {
                // debug stuff
                Nodes nn = lbrak.query("./preceding-sibling::*[1]");
                if (nn.size() > 0) {
                } else {
                    System.out.println("no preceding siblling");
                }
                nn = lbrak.query("./following-sibling::*[1]");
                if (nn.size() > 0) {
                } else {
                }
                continue;
            }
            CMLModule rbrak = (CMLModule) nodes.get(0);
            CMLElement parent = (CMLElement) lbrak.getParent();
            CMLModule module1 = new CMLModule();
            module1.setRole("balancedBrackets");
            int idx0 = parent.indexOf(lbrak);
            int idx1 = parent.indexOf(rbrak);
            // transfer brackets nodes to new container
            for (int i = idx1-1; i > idx0; i--) {
                CMLElement elem = (CMLElement) parent.getChild(i);
                elem.detach();
                module1.insertChild(elem, 0);
            }
            parent.replaceChild(lbrak, module1);
            rbrak.detach();
        }
    }
    
    private void removeEmptyContainers() {
        // remove empty containers
        List<Node> emptyList = CMLUtil.getQueryNodes(
                doc, ".//"+CMLModule.NS+"[@role='container']", CML_XPATH);
        for (Node empty : emptyList) {
            CMLModule container = (CMLModule) empty;
            if (container.getChildCMLElements().size() == 0 &&
                    container.getValue().trim().equals(S_EMPTY)) {
                empty.detach();
//                System.out.println("DETACH.............");
            }
        }
    }
    
    private void flattenModules() {
        List<Node> modList = CMLUtil.getQueryNodes(
                doc, ".//"+CMLModule.NS+"[@role='container']",
                CML_XPATH);
        for (Node mod : modList) {
            CMLModule module = (CMLModule) mod;
            Nodes childs = module.query(CMLModule.NS, CML_XPATH);
            List<Node> texts = CMLUtil.getQueryNodes(module, "./text()");
            // has at least one module child
            if (childs.size() > 0) {
                if (texts.size() > 0) {
                    wrapTextNodes(module, texts);
                    childs = module.query(CMLModule.NS, CML_XPATH);
                }
                // all are modules
                if (childs.size() == module.getChildCount()) {
//                    for (int i = 0; i < module.getChildCount(); i++) {
//                        System.out.print(((CMLModule)module.getChild(i)).getId()+S_SPACE);
//                    }
//                    System.out.println("\nREPLACEBYCHILDREN............"+module.getId());
                    module.replaceByChildren();
                } else {
                    // non-module child
                    for (int i = 0; i < module.getChildCount(); i++) {
                        Node child = module.getChild(i);
                        if (child instanceof Text) {
//                            System.err.println("UNWRAPPED "+child.getValue());
                        } else if (child instanceof CMLModule) {
                        } else if (child instanceof CMLElement) {
                            System.err.println("CMLELEMENT "+((Element)child).getLocalName());
                        } else if (child instanceof Element) {
                            // normally IT, SB, SP
//                            System.err.println("ELEMENT "+((Element)child).getLocalName());
                        } else {
                            System.err.println("UNEXPECTED "+child.getClass());
                        }
                    }
                }
            }
        }
    }
    
    private void wrapTextNodes(CMLModule module, List<Node> texts) {
        for (Node n : texts) {
            CMLModule mod = new CMLModule();
            mod.setRole(OSCAR_CONTAINER);
            mod.setId(getContainerId());
            module.replaceChild(n, mod);
            mod.appendChild(n);
        }
    }

    private void processLeadingRightBracket(CMLModule module) {
        Node firstChild = module.getChild(0);
        if (firstChild instanceof Text) {
            String value = firstChild.getValue().trim();
            // add new module to represent bracket
            if (value.startsWith(S_RBRAK)) {
                CMLModule rbrak = new CMLModule();
                rbrak.setRole("rbracket");
                ((Text)firstChild).setValue(value.substring(1));
                ParentNode parent = module.getParent();
                parent.insertChild(rbrak, parent.indexOf(module));
            }
        }
    }
    
    private void processTrailingLeftBracket(CMLModule module) {
        Node lastChild = module.getChild(module.getChildCount() - 1);
        if (lastChild instanceof Text) {
            String value = lastChild.getValue().trim();
            if (value.endsWith(S_LBRAK)) {
                CMLModule lbrak = new CMLModule();
                lbrak.setRole("lbracket");
                ((Text)lastChild).setValue(value.substring(0, value.length()-1));
                ParentNode parent = module.getParent();
                parent.insertChild(lbrak, parent.indexOf(module)+1);
            }
        }
    }

    /** turns balancedBrackets after molecule into child 
     */
    private void conflateMoleculeAndFollowingBalancedBrackets() {
        List<Node> molList = CMLUtil.getQueryNodes(
                doc, ".//"+CMLMolecule.NS+"[following-sibling::"+CMLModule.NS+"[@role='balancedBrackets']]",
                CML_XPATH);
        for (Node node : molList) {
            CMLMolecule mol = (CMLMolecule) node;
            @SuppressWarnings("unused")
            String title = mol.getTitle();
            List<Node> mods = CMLUtil.getQueryNodes(mol, 
                    "./following-sibling::"+CMLModule.NS+"[@role='balancedBrackets'][1]", CML_XPATH);
            if (mods.size() > 0) {
                CMLModule module = (CMLModule) mods.get(0);
                module.detach();
                mol.appendChild(module);
            } else {
//                System.err.println("Cannot find sibling for ..."+title);
            }
        }
    }

    /** turns state preceding molecule into child 
     * requires cml:property@state as immediately preceding sibling
     */
    private void conflatePropertyAndFollowingMolecule() {
        String subquery = "preceding-sibling::node()[" +
                "position()=1 and " +
                "self::"+CMLProperty.NS+" and " +
                "@state]";
        List<Node> molList = CMLUtil.getQueryNodes(
                doc, ".//"+CMLMolecule.NS+"["+subquery+S_RSQUARE, CML_XPATH);
        for (Node node : molList) {
            CMLMolecule mol = (CMLMolecule) node;
            @SuppressWarnings("unused")
            String title = mol.getTitle();
//            System.out.println("PPPPP "+title);
            List<Node> props = CMLUtil.getQueryNodes(mol, subquery, CML_XPATH);
            if (props.size() > 0) {
                CMLProperty property = (CMLProperty) props.get(0);
                property.detach();
                mol.appendChild(property);
            } else {
//                System.err.println("Cannot find state sibling for ..."+title);
            }
        }
    }

    private void removeBalancedBracketParentFromMolecule() {
        List<Node> brackMolList = CMLUtil.getQueryNodes(
            doc, ".//"+CMLModule.NS+"[@role='balancedBrackets' and " +
            "count(*) = 1 and count("+CMLMolecule.NS+") = 1]", CML_XPATH);
        for (Node brackMol : brackMolList) {
            ((CMLElement)brackMol).replaceByChildren();
        }
    }
    
    private void removeBalancedBracketChildFromMolecule() {
        List<Node> brackMolList = CMLUtil.getQueryNodes(
            doc, ".//"+CMLMolecule.NS+S_SLASH+CMLModule.NS+"[@role='balancedBrackets']", CML_XPATH);
        for (Node brackMol : brackMolList) {
            ((CMLElement)brackMol).replaceByChildren();
        }
    }

    private void processConjunctions() {
        List<Node> textList = CMLUtil.getQueryNodes(doc, ".//"+CMLModule.NS+"[@role='container']", CML_XPATH);
        for (Node node : textList) {
            CMLModule module = (CMLModule) node;
            String value = module.getValue().trim();
            if (value.equals("with") ||
                    value.equals("and") ||
                    value.equals(S_COMMA)
                    ) {
                module.setRole("conjunction");
                ((Text)module.getChild(0)).setValue(value);
            }
        }
    }
    
    private void processDataSections() {
        List<Node> nodeList = CMLUtil.getQueryNodes(doc, ".//datasection");
        for (Node node : nodeList) {
            Element datasection = (Element) node;
            CMLModule module = new CMLModule();
            module.setRole(OSCAR_DATASECTION);
            datasection.getParent().replaceChild(datasection, module);
            CMLUtil.transferChildren(datasection, module);
        }
    }
    
    /**
     * detect <module role='container'>yxzzy bar. Foo abc. Plugh</module>
     * as containing sentence end. Change to 
     * <module role='container'>yxzzy bar"</module>
     * <module role='sentenceEnd'/>
     * <module role='container'>Foo abc"</module>
     * <module role='sentenceEnd'/>
     * <module role='container'>Plugh</module>
     * 
     * This is harder than it looks because the text may have embedded inline
     * markup and this causes problems with module structure.
     * 
     */
    private void processSentences() {
        // for all modules look for end of sentence
        List<Node> contList = CMLUtil.getQueryNodes(doc, ".//"+CMLModule.NS+"[@role='container']", CML_XPATH);
        for (Node cont : contList) {
            CMLModule module = (CMLModule) cont;
            List<Node> textList = CMLUtil.getQueryNodes(module, "./text()");
            List<Text> splitList = new ArrayList<Text>();
            if (textList.size() > 1) {
//                System.err.println("SIZE "+textList.size());
            }
            for (Node t : textList) {
                Text text = (Text) t;
                int nsplit = splitSentence(module, text);
                if (nsplit >0) {
                    splitList.add(text);
                } else {
                }
            }
            // remove original text if split.
            for (Text t : splitList) {
                t.detach();
            }
        }
        // split modules at sentence end
        List<Node> sentenceEndList = CMLUtil.getQueryNodes(doc, 
                ".//"+CMLModule.NS+"[@role='container']/"+CMLModule.NS+"[@role='sentenceEnd']",
                CML_XPATH);
        for (Node n : sentenceEndList) {
            CMLModule sentenceEnd = (CMLModule) n;
            splitParentModule(sentenceEnd);
        }
        wrapSentencesAsModules();
    }
    
    private int splitSentence(CMLModule textParent, Text text) {
        if (!(textParent.equals(text.getParent()))) {
            System.err.println("Bad parent: "+textParent.getClass());
            return 0;
        }
        ParentNode moduleParent = textParent.getParent();
        @SuppressWarnings("unused")
        int ipar = moduleParent.indexOf(textParent);
        int ipos = textParent.indexOf(text);
        String value = text.getValue().trim();
        // normalize string to end with space after period
        if (value.endsWith(S_PERIOD)) {
            value += S_SPACE;
        }
        int nsplit = 0;
        int ioff = 0;
        while (value.length() > 0) {
            value += S_SPACE;
            int idx = value.indexOf(S_PERIOD+S_SPACE);
            String value0 = null;
            if (idx == -1) {
                value0 = value;
                if (nsplit == 0) {
                    break;
                }
            } else {
                value0 = value.substring(0, idx);
                value = value.substring(idx+2).trim();
                nsplit++;
            }
            value0 = value0.trim();
            if (value0.trim().length() > 0) {
                CMLModule newModule = new CMLModule();
                newModule.setRole(OSCAR_CONTAINER);
                newModule.setId(getContainerId());
                newModule.appendChild(new Text(value0));
                textParent.insertChild(newModule, ipos+(++ioff));
            }
            if (idx != -1) {
                CMLModule stop = new CMLModule();
                stop.setRole(OSCAR_SENTENCE_END);
                textParent.insertChild(stop, ipos+(++ioff));
            } else {
                break;
            }
        }
        return nsplit;
    }
    
    private void wrapSentencesAsModules() {
        List<Node> sentenceEnds = CMLUtil.getQueryNodes(
                cml, ".//"+CMLModule.NS+"[@role='sentenceEnd']", CML_XPATH);
        int ii = 0;
        for (Node node : sentenceEnds) {
            CMLModule sentenceEnd = (CMLModule) node;
            sentenceEnd.setId("S"+(++ii));
            ParentNode parent = sentenceEnd.getParent();
            List<Node> precedingSE = CMLUtil.getQueryNodes(sentenceEnd, 
                    "./preceding-sibling::"+CMLModule.NS+"[@role='sentenceEnd'][position()=1]", CML_XPATH);
            int idx = (precedingSE.size()==0) ? -1 : 
                parent.indexOf(precedingSE.get(0));
            wrapSiblingsInSentence(sentenceEnd, idx);
        }
        for (Node node : sentenceEnds) {
            ((CMLModule)node).setRole("sentence");
        }
    }
    
    private void wrapSiblingsInSentence(
            CMLModule sentenceEnd, int indexOfPrecedingSE) {
        ParentNode parent = sentenceEnd.getParent();
        int thisIndex = parent.indexOf(sentenceEnd);
        List<Element> siblingList = new ArrayList<Element>();
        for (int i = indexOfPrecedingSE+1; i < thisIndex; i++) {
            Element sibling = (Element) parent.getChild(i);
            if (sibling instanceof CMLModule && 
                    "sentenceEnd".equals(sibling.getAttribute("role"))) {
                System.out.println("SEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                continue;
            }
            siblingList.add(sibling);
        }
        for (Element sibling : siblingList) {
            sibling.detach();
            if (sibling instanceof CMLModule && 
                    "sentenceEnd".equals(sibling.getAttribute("role"))) {
                System.out.println("SXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                continue;
            }
            sentenceEnd.appendChild(sibling);
        }
    }
    
    private String getContainerId() {
        return "c"+(++ncontain);
    }

    private void splitParentModule(CMLModule sentenceEnd) {
        CMLModule parentModule = (CMLModule) sentenceEnd.getParent();
        int ipos = parentModule.indexOf(sentenceEnd);
        ParentNode grandparent = parentModule.getParent();
        int ipar = grandparent.indexOf(parentModule);
        sentenceEnd.detach();
        grandparent.insertChild(sentenceEnd, ipar+1);
        CMLModule newModule = new CMLModule();
        newModule.setRole(OSCAR_CONTAINER);
        grandparent.insertChild(newModule, ipar+2);
        
        // transfer trailing children to new module
        // ipos is where the sentenceEnd was, but now closed up
        int nchild = parentModule.getChildCount();
        for (int i = nchild-1; i >= ipos; i--) {
            Node node = parentModule.getChild(i);
            node.detach();
            newModule.insertChild(node, 0);
        }
    }
    
    private void aggregateMolecules() {
        // find molecule conjunction molecule
        List<Node> molList = CMLUtil.getQueryNodes(doc, ".//"+CMLMolecule.NS, CML_XPATH);
        for (Node molNode : molList) {
            Node conj = CMLUtil.getPrecedingSibling(molNode);
            if (conj instanceof CMLModule) {
                CMLModule conjMod = (CMLModule) conj;
                if ("conjunction".equals(conjMod.getRole())) {
                    Node molSib = CMLUtil.getPrecedingSibling(conjMod);
                    if (molSib instanceof CMLMolecule) {
                        ParentNode parent = conj.getParent();
                        CMLMoleculeList moleculeList = new CMLMoleculeList();
                        molSib.detach();
                        moleculeList.addMolecule((CMLMolecule)molSib);
                        molNode.detach();
                        moleculeList.addMolecule((CMLMolecule)molNode);
                        parent.replaceChild(conj, moleculeList);
                    } else if (molSib instanceof CMLMoleculeList) {
                        ((CMLMoleculeList)molSib).addMolecule((CMLMolecule)molNode);
                        conjMod.detach();
                    }
                }
            }
        }
    }
    
    private void findSolvents() {
        // wrap possible molecules in substance
        List<Node> moleculeList = 
            CMLUtil.getQueryNodes(doc, ".//"+CMLMolecule.NS, CML_XPATH);
        for (Node node : moleculeList) {
            if (node instanceof CMLMolecule) {
                CMLMolecule molecule = (CMLMolecule) node;
                CMLModule module = getPrecedingModuleContainer(molecule, " in");
                if (module != null) {
                    CMLSubstance substance = new CMLSubstance();
                    molecule.getParent().replaceChild(molecule, substance);
                    substance.appendChild(molecule);
                }
            }
        }
        // set role=solvent
        List<Node> substanceList = 
            CMLUtil.getQueryNodes(doc, ".//"+CMLSubstance.NS, CML_XPATH);
        for (Node node : substanceList) {
            CMLSubstance substance = (CMLSubstance) node;
            @SuppressWarnings("unused")
            CMLModule module = getPrecedingModuleContainer(substance, " in");
            substance.setRole(C_A+"solvent");
        }
    }
    
    private CMLModule getPrecedingModuleContainer(
            CMLElement element, String endString) {
        CMLModule module = null;
        Node sib = CMLUtil.getPrecedingSibling(element);
        boolean ok = false;
        if (sib != null && sib instanceof CMLModule) {
            module = (CMLModule) sib;
            if (OSCAR_CONTAINER.equals(module.getAttributeValue(OSCAR_ROLE))) {
                String value = module.getValue().trim();
                if (value.endsWith(endString)) {
                    ok = true;
                }
            }
        }
        return ok ? module : null;
    }

    /** property + molecule => molecule/property
     */
    private void aggregatePropertyAndMolecules() {
        List<Node> nodeList = CMLUtil.getQueryNodes(doc,
                CMLMolecule.NS+"[preceding-sibling::"+CMLProperty.NS+"]", CML_XPATH);
        for (Node node : nodeList) {
            CMLMolecule molecule = (CMLMolecule) node;
            Node propSib = CMLUtil.getPrecedingSibling(molecule);
            if (propSib != null) {
                propSib.detach();
                molecule.appendChild(propSib);
            }
        }
    }
    
    /** tidy this...
    <molecule ref="chem11" title="11"/>
    <module role=CONTAINER> (</module>
        <property dictRef="osc:quantity">
    <scalar dataType="xsd:double" dictRef="osc:mass" units="oscUnits:g">0.132</scalar>
    </property>
    <module role=CONTAINER>, total amount </module>
        <property dictRef="osc:quantity">
    <scalar dataType="xsd:double" dictRef="osc:mass" units="oscUnits:g">0.372</scalar>
    </property>
    <module role=CONTAINER>, 45%),
    ...*/
    private void processAmounts() {
        Nodes nodes = cml.query(
                ".//"+CMLMolecule.NS+"[following-sibling::"+CMLModule.NS+"[@role='container' and position()=1]]",
                CML_XPATH);
//        System.err.println("NODES "+nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            CMLMolecule molecule = (CMLMolecule) nodes.get(i);
            CMLModule module = (CMLModule) molecule.query("following-sibling::"+CMLModule.NS, CML_XPATH).get(0);
            String value = module.getValue().trim();
            if (value.startsWith(S_LBRAK)) {
//                System.err.println("VALUE "+value);
                Nodes properties = module.query(CMLProperty.NS, CML_XPATH);
                if (properties.size() != 2) {
                    @SuppressWarnings("unused")
                    Element fs0 = (Element)module.query("./preceding-sibling::*[1]").get(0);
                    Nodes fs = module.query("./following-sibling::*");
                    if (fs.size() >= 1) {
                        @SuppressWarnings("unused")
                    Element fs1 = (Element)module.query("./following-sibling::*[1]").get(0);
                    }
                    if (fs.size() >= 2) {
                        @SuppressWarnings("unused")
                    Element fs2 = (Element)module.query("./following-sibling::*[2]").get(0);
                    }
                    if (fs.size() >= 3) {
                        @SuppressWarnings("unused")
                    Element fs3 = (Element)module.query("./following-sibling::*[3]").get(0);
                    }
                } else {
                    System.out.println("OK PROPRTY");
                    CMLPropertyList propertyList = new CMLPropertyList();
                    CMLUtil.transferChildren(module, propertyList);
                    module.getParent().replaceChild(module, propertyList);
                }
            }
        }
    }
    private void processSpectra() {
        Nodes ne = doc.query(".//spectrum");
        for (int i = 0; i < ne.size(); i++) {
            processSpectrum((Element) ne.get(i));
        }
    }
    
    private void processSpectrum(Element el) {
        String type = el.getAttributeValue("type");
        if (
                "cnmr".equals(type) ||
                "hnmr".equals(type) ||
                "ir".equals(type) ||
                "massSpec".equals(type) ||
                "uv".equals(type) ||
            false) {
            CMLSpectrum spectrum = new CMLSpectrum();
            spectrum.setType(type);
            el.getParent().replaceChild(el, spectrum);
            CMLUtil.transferChildren(el, spectrum);
            
            List<Node> childs = CMLUtil.getChildNodes(el);
            // some children may get removed so ignore them
            for (Node child : childs) {
                if (child.getParent() != null) {
                    processSpectrumDescendant(child);
                }
            }
        } else {
            System.err.println("UNKNOWN SPECTRUM "+type);
        }
    }
    
    private void processSpectrumDescendant(Node node) {
        if (node instanceof Text) {
            // leave as is
        } else if (node instanceof Element) {
            Element element = (Element) node;
            String name = element.getLocalName();
            if ("SB".equals(name)) {
                node.getParent().replaceChild(node, new Text(S_UNDER+element.getValue()+S_UNDER));
            } else if ("SP".equals(name)) {
                node.getParent().replaceChild(node, new Text("^"+element.getValue()+"^"));
            } else if ("peaks".equals(name)) {
                processPeaks(element);
            } else {
                System.err.println("FAILED TO PROCESS: "+name);
            }
        }
    }
    
    private void processPeaks(Element element) {
        
//        for ()
    }

    private void processNamedEntities() {
        Nodes ne = doc.query("//ne");
        List<Element> neList = new ArrayList<Element>();
        for (int i = 0; i < ne.size(); i++) {
            neList.add((Element) ne.get(i));
        }
        for (Element namedEntity : neList) {
            processNamedEntity(namedEntity);
        }
        List<Node> nodeList = CMLUtil.getQueryNodes(doc, ".//"+CMLMolecule.NS, CML_XPATH);
        @SuppressWarnings("unused")
        List<CMLMolecule> molList = new ArrayList<CMLMolecule>();
        for (Node node : nodeList) {
            System.out.println("NNNNNNN"+node);
//            CMLMolecule molecule = (CMLMolecule) node;
//            tryMergeWithPreviousSibling(molecule);
        }
    }
    
    private CMLElement processNamedEntity(Element el) {
        String type = el.getAttributeValue("type");
        CMLElement cmlElement = null;
        if ("CM".equals(type) ||
            "CMS".equals(type) ||
            "OX".equals(type) ||
            "CJ".equals(type) ||
            "RN".equals(type) ||
            "ASES".equals(type) ||
            false) {
            CMLMolecule molecule = new CMLMolecule();
            molecule.setTitle(el.getValue());
            molecule.setRole(type);
            el.getParent().replaceChild(el, molecule);
            cmlElement = molecule;
        } else {
            System.err.println("UNKNOWN NE "+type);
        }
        return cmlElement;
    }
    
    @SuppressWarnings("unused")
    private void tryMergeWithPreviousSibling(CMLMolecule molecule) {
        String title = molecule.getTitle();
        for (Pattern pattern : mergeableMoleculeMap.keySet()) {
            Matcher matcher = pattern.matcher(title);
            if (matcher.matches()) {
                ParentNode parent = molecule.getParent();
                int idx = parent.indexOf(molecule);
                List<Node> nodes = CMLUtil.getQueryNodes(molecule, 
                    "./preceding-sibling::"+CMLMolecule.NS, CML_XPATH);
                if (nodes.size() > 0) {
                    CMLMolecule preceding = (CMLMolecule) nodes.get(0);
                    int prevIdx = parent.indexOf(preceding);
                    if (idx == prevIdx + 1) {
                        preceding.setTitle(preceding.getTitle()+"++"+title);
//                        String tValue = text.getValue();
//                        System.out.println("XXXXXXXXXXXXXX "+tValue);
//                        text.setValue(tValue+"++"+title);
//                        molecule.setRole("detach");
                        System.out.println("============== "+preceding.getTitle());
                        break;
                    }
                }
            }
        }
    }

    /** adds markup that OSCAR missed.
     */
    private void additionalMarkup() {
      List<Node> texts = CMLUtil.getQueryNodes(cml, ".//text()");
      for (Node n : texts) {
          markup(actionMap, (Text) n, CMLAction.class);
      }
      texts = CMLUtil.getQueryNodes(cml, ".//text()");
      for (Node n : texts) {
          markup(conditionMap, (Text) n, CMLConditionList.class);
      }
      // rather crude to do it this way, but safe
      texts = CMLUtil.getQueryNodes(cml, ".//text()");
      for (Node n : texts) {
          markup(objectMap, (Text) n, CMLObject.class);
      }
      texts = CMLUtil.getQueryNodes(cml, ".//text()");
      for (Node n : texts) {
          markup(substanceMap, (Text) n, CMLSubstance.class);
      }
      // markup missed units (e.g. cm<SP>3</SP>
      markupUnits();
      markupPropertiesWithUnits();
  }
    
    /** find keys and uinsert new marked elements into text.
     * Example: if text value is: "this is a foo action"
     * and foo is a key, might create:
     * Text("this is a ") <action class="foo">foo</action> Text(" action");
     * recurses through the next texts so that all instances of keys are 
     * found and marked
     * @param parent
     * @param text
     */
    
    private void markup(Map<Pattern, String> map, Text text, Class cmlClass) {
        ParentNode parent = text.getParent();
        String textS = text.getValue();
        int pos = parent.indexOf(text);
        for (Pattern pattern : map.keySet()) {
            Matcher matcher = pattern.matcher(textS);
            int start = 0;
            int end = 0;
            List<Text> textList = new ArrayList<Text>();
            while (matcher.find()) {
                if (end == 0) {
                    text.detach();
                }
                start = matcher.start();
                if (start > end) {
                    Text tt = new Text(textS.substring(end, start));
                    textList.add(tt);
                    parent.insertChild(tt, pos++);
                }
                end = matcher.end();
                if (end > start) {
                    String dictRef = map.get(pattern);
                    CMLElement cmlElement = null;
                    try {
                        cmlElement = (CMLElement) cmlClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Util.BUG("cannot create CML object", e);
                    }
                    String title = textS.substring(start, end);
                    if (!(cmlClass.equals(CMLAction.class))) {
//                        System.out.println("Made "+cmlClass+S_SLASH+title);
                    }
                    cmlElement.addAttribute(new Attribute("title", title));
                    cmlElement.appendChild(new Text(title));
                    cmlElement.addAttribute(new Attribute(OSCAR_ROLE, dictRef));
                    parent.insertChild(cmlElement, pos++);
                }
            }
            if (end > 0) {
                String endS = textS.substring(end, textS.length());
                Text tt = new Text(endS);
                textList.add(tt);
                parent.insertChild(tt, pos++);
                for (Text t : textList) {
                    markup(map, t, cmlClass);
                }
                break;
            }
        }
    }

    private void markupUnits() {
        List<Node> spNodes = CMLUtil.getQueryNodes(doc, ".//SP");
        for (Node node : spNodes) {
            Element sp = (Element) node;
            String s = sp.getValue().trim();
            try {
                new Integer(s);
            } catch (NumberFormatException nfe) {
                continue;
            }
            Node psib = CMLUtil.getPrecedingSibling(node);
            if (psib != null) {
                String v = psib.getValue().trim();
                for (String u : powerUnitsMap.keySet()) {
                    if (v.endsWith(u)) {
                        Text t = CMLUtil.getLastTextDescendant(psib);
                        String vv = t.getValue();
                        if (!(v.endsWith(u))) {
                            throw new RuntimeException("Bad units");
                        }
                        t.setValue(vv.substring(0, vv.length()-u.length()));
                        Element unit = new Element("units");
                        unit.appendChild(new Text((u + s).trim()));
                        sp.getParent().replaceChild(sp, unit);
                    }
                }
            }
        }
    }


    
//  from
//  30<units>min</units>
// form     
//    <property type="quantity" saf="yes">
//      <quantity type="time">
//        <value><point>30</point></value>
//        <units>min</units>
//      </quantity>
//    </property>
    private void markupPropertiesWithUnits() {
        List<Node> unitNodes = CMLUtil.getQueryNodes(doc, ".//units");
        for (Node node : unitNodes) {
            Element unit = (Element) node;
            ParentNode parent = unit.getParent();
            if (parent != null || 
                !(parent instanceof Element) ||
                !(((Element)parent).getLocalName().equals("quantity"))) {
                    markupIsolatedUnit(parent, unit);
            }
            standardizeUnits(parent, unit);
        }
    }
    
    private void markupIsolatedUnit(ParentNode parent, Element unit) {
        int idx = parent.indexOf(unit);
        Node psib = CMLUtil.getPrecedingSibling(unit);
        Text t = CMLUtil.getLastTextDescendant(psib);
        if (t != null) {
            String vvv = t.getValue().trim();
            String[] vv = vvv.split(S_SPACE);
            String v = vv[vv.length-1].trim();
            Double d = null;
            if (v.length() > 0) {
                try {
                    d = new Double(v);
                } catch (NumberFormatException e) {
    //                System.out.println("Isolated unit: Cannot parse as double: "+vector);
                }
            }
            if (d != null) {
                t.setValue(vvv.substring(0, vvv.length()-v.length()));
                @SuppressWarnings("unused")
                String u = unit.getValue();
                Element point = new Element("point");
                point.appendChild(new Text(v));
                Element value = new Element("value");
                value.appendChild(point);
                Element quantity = new Element("quantity");
                quantity.appendChild(value);
                unit.detach();
                quantity.appendChild(unit);
                Element property = new Element("property");
                property.appendChild(quantity);
                property.addAttribute(new Attribute("type", "quantity"));
                parent.insertChild(property, idx);
            }
        }
    }
    
    private void standardizeUnits(ParentNode parent, Element unit) {
        parent = unit.getParent();
        if (parent != null && 
                parent instanceof Element &&
                ((Element)parent).getLocalName().equals("quantity")) {
            Element quantity = (Element) parent;
            Text text = CMLUtil.getFirstTextDescendant(unit);
            if (text != null) {
                String u = text.getValue();
                String standardUnit = unitsMap.get(u);
                if (standardUnit == null) {
                    CMLUtil.debug(unit, "OSCAR1");
                    System.out.println("\nCannot find unit for ["+u+S_RSQUARE);
                } else {
                    text.setValue(standardUnit);
                    String uType = unitTypeMap.get(standardUnit);
                    if (uType == null) {
                        System.out.println("Cannot find type for: "+standardUnit);
                    } else {
                        Attribute type = quantity.getAttribute("type");
                        if (type == null) {
                            quantity.addAttribute(new Attribute("type", uType));
                        } else {
                            String t = type.getValue();
                            String tt = rawUnitTypeMap.get(t);
                            if (tt == null) {
                                System.out.println("Unknown raw unit type: "+t);
                            } else {
                                if (tt.equals(uType)) {
                                } else {
                                    System.out.println("original type ("+tt+") incomaptible with ("+uType+S_RBRAK);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        
    private void detach(Node unit, String s1, String s2, String s3) {
        Element parent = (Element) unit.getParent();
        if (parent != null) {
            int idx = parent.indexOf(unit);
            if (s3.equals(unit.getValue())) {
                Node n1 = parent.getChild(idx-1);
                Node n2 = (idx > 1) ? parent.getChild(idx-2) : null;
                if (n1 != null && s2.equals(n1.getValue()) &&
                    n2 != null && s1.equals(n2.getValue())
                        ) {
                    unit.detach();
                    n1.detach();
                }
            }
        }
    }
    private void processMetadata(Element metadata) {
        if (metadata != null) {
            metadataM = new CMLModule();
            metadataM.setRole("metadata");
            CMLUtil.transferChildren(metadata, metadataM);
            metadata.getParent().replaceChild(metadata, metadataM);
            processElements(metadataM);
        } else {
            error("No metadata");
        }
    }
    
    private void processTitle(Element title) {
        if (title != null) {
            titleM = new CMLModule();
            titleM.setRole("title");
            CMLUtil.transferChildren(title, titleM);
            title.getParent().replaceChild(title, titleM);
            processElements(titleM);
        } else {
            error("No title");
        }
    }
    
    private void processAuthorList(Element authorList) {
        if (authorList != null) {
            authorListM = new CMLModule();
            authorListM.setRole("authorList");
            CMLUtil.transferChildren(authorList, authorListM);
            authorList.getParent().replaceChild(authorList, authorListM);
            processElements(authorListM);
        } else {
            error("No authorList");
        }
    }
    
    private void processAbstract(Element abstractx) {
        if (abstractx != null) {
            abstractM = new CMLModule();
            abstractM.setRole("abstract");
            CMLUtil.transferChildren(abstractx, abstractM);
            abstractx.getParent().replaceChild(abstractx, abstractM);
            processElements(abstractM);
            processParagraphs(abstractM);
        } else {
            error("No abstract");
        }
    }
    
    private void processBody(Element body) {
        if (body != null) {
            bodyM = new CMLModule();
            bodyM.setRole("body");
            CMLUtil.transferChildren(body, bodyM);
            body.getParent().replaceChild(body, bodyM);
            processElements(bodyM);
            processParagraphs(bodyM);
            processBodySections();
            markSynthesizedCompounds();
        } else {
            error("No body");
        }
    }
    
    private void processExperimental(Element experimental) {
        if (experimental != null) {
            experimentalM = new CMLModule();
            experimentalM.setRole("experimental");
            CMLUtil.transferChildren(experimental, experimentalM);
            experimental.getParent().replaceChild(experimental, experimentalM);
            processElements(experimentalM);
            removeHeader(experimentalM);
        } else {
            error("No experimental");
        }
    }
    
    private void processConclusions(Element conclusions) {
        if (conclusions != null) {
            conclusionsM = new CMLModule();
            conclusionsM.setRole("conclusions");
            CMLUtil.transferChildren(conclusions, conclusionsM);
            conclusions.getParent().replaceChild(conclusions, conclusionsM);
            processElements(conclusionsM);
            removeHeader(conclusionsM);
        } else {
//            error("No conclusions");
        }
    }
    
    private void processDiscussion(Element discussion) {
        if (discussion != null) {
            discussionM = new CMLModule();
            discussionM.setRole("discussion");
            CMLUtil.transferChildren(discussion, discussionM);
            discussion.getParent().replaceChild(discussion, discussionM);
            processElements(discussionM);
            removeHeader(discussionM);
        } else {
//            error("No discussions");
        }
    }
    
    private void processIntroduction(Element introduction) {
        if (introduction != null) {
            introductionM = new CMLModule();
            introductionM.setRole("introduction");
            CMLUtil.transferChildren(introduction, introductionM);
            introduction.getParent().replaceChild(introduction, introductionM);
            processElements(introductionM);
            removeHeader(introductionM);
        } else {
            error("No introduction");
        }
    }
    
    private void processResults(Element results) {
        if (results != null) {
            resultsM = new CMLModule();
            resultsM.setRole("results");
//            System.err.println("RES "+results.getChildCount());
            CMLUtil.transferChildren(results, resultsM);
            results.getParent().replaceChild(results, resultsM);
            processElements(resultsM);
            removeHeader(resultsM);
//            System.err.println("RES "+resultsM.getChildCount());
        } else {
            error("No results");
        }
    }
    
    private void processParagraphs(CMLModule module) {
        Nodes paras = module.query(".//P");
        for (int i = 0; i < paras.size(); i++) {
            Element para = (Element) paras.get(i);
            CMLModule paraM = new CMLModule();
            paraM.setRole("para");
            CMLUtil.transferChildren(para, paraM);
            para.getParent().replaceChild(para, paraM);
            processParagraph(paraM);
        }
    }
    
    private void processParagraph(CMLModule paraM) {
        int nn = paraM.getChildCount();
        CMLModule container = null;
        List<Node> childList = new ArrayList<Node>();
        for (int i = 0; i < nn; i++) {
            childList.add((Node)paraM.getChild(i));
        }
        int ipos = 0;
        for (Node child : childList) {
            if (child instanceof CMLElement) {
                if (container != null) {
                    paraM.insertChild(container, ipos++);
                }
                child.detach();
                paraM.insertChild(child, ipos++);
                container = null;
            } else {
                child.detach();
                container = ensure(container);
                container.appendChild(child);
            }
        }
        if (container != null) {
            paraM.insertChild(container, ipos++);
        }
    }

    private CMLModule ensure(CMLModule container) {
        if (container == null) {
            container = new CMLModule();
            container.setRole(OSCAR_CONTAINER);
            container.setId(getContainerId());
        }
        return container;
    }
    private void processElements(Element elem) {

        // try to deal with encodings
        tidyCharacters(elem);
        
        badChemicals(elem);
        
        //<XREF ID="chem4" TYPE="COMPOUND">4</XREF> to <molecule ref="" title=""/>
        xRef2Mol(elem);
        
        // formula[chemical] or formula[text(elem)]
        formula2Mol(elem);

        processProperty(elem);
        
        processChemical(elem);
        
        // 'chemical followed by XREF
        chemicalXref(elem);
        
        // molecule/REF
        moleculeREF(elem);
        
        // tidy things OSCAR has missed
        markText(elem);
        
    }
    
    // remove SB, SP, IT, etc.
    private static void unmark(Element div, String name) {
        Nodes inline = div.query(".//"+name);
        for (int i = 0; i < inline.size(); i++) {
            unmark((Element) inline.get(i));
        }
    }
    
    private static void unmark(Element elem) {
        ParentNode parent = elem.getParent();
        int idx = parent.indexOf(elem);
        elem.detach();
        for (int i = 0; i < elem.getChildCount(); i++) {
            Node node = elem.getChild(i);
            node.detach();
            parent.insertChild(node, idx + i);
        }
    }

    // tidy encodings
    // FIXME move to CMLUtil
    private void tidyCharacters(Element elem) {
        Nodes texts = elem.query(".//text()");
        for (int i = 0; i < texts.size(); i++) {
            Text text = (Text) texts.get(i);
            StringBuilder sb = new StringBuilder(text.getValue());
            boolean change = false;
            for (int j = 0; j < sb.length(); j++) {
                char c = sb.charAt(j);
                int ii = (int) c;
                if (ii > 255) {
                    String s = charactersMap.get(new Integer(ii));
                    if (s != null) {
                        sb.replace(j, j+1, s);
                        change = true;
                    } else {
                        System.out.println(">unknown>"+ii+">>"+c);
                        System.out.println(">>>"+text.getParent().getValue());
                    }
                }
            }
            if (change) {
                text.setValue(sb.toString());
            }
        }
    }
    
    private void processBodySections() {
        Nodes divs = bodyM.query("DIV[HEADER]");
        for (int i = 0; i < divs.size(); i++) {
            Element div = (Element) divs.get(i);
            Element header = (Element) div.getFirstChildElement("HEADER");
            String title = header.getValue();
            if (title == null || title.trim().equals(S_EMPTY)) {
                try {
//                CMLUtil.debug(div, System.err);
                } catch (Exception e) {}
                error("HEADER must have child text");
            } else if (title.equals("Conclusions") || 
                    title.equals("Conclusion")) {
                processConclusions(div);
            } else if (title.equals("Discussion")) {
                processDiscussion(div);
            } else if (title.equals("Experimental")) {
                processExperimental(div);
            } else if (title.equals("Introduction")) {
                processIntroduction(div);
            } else if (title.indexOf("Results") != -1) {
                processResults(div);
            } else {
                error("unknown section: "+title);
            }
        }
    }
    
    private void removeHeader(Element div) {
        Element header = div.getFirstChildElement("HEADER");
        header.detach();
    }

    
    //<XREF ID="chem4" TYPE="COMPOUND">4</XREF> to <molecule ref="" title=""/>
    private void xRef2Mol(Element elem) {
        Nodes xrefs = elem.query(".//XREF[@TYPE='COMPOUND']");
        for (int i = 0; i < xrefs.size(); i++) {
            Element xref = (Element) xrefs.get(i);
            ParentNode parent = xref.getParent();
            CMLMolecule mol = new CMLMolecule();
            parent.replaceChild(xref, mol);
            mol.setRef(xref.getAttributeValue("ID"));
            mol.setTitle(xref.getValue());
        }
    }
    
//    - <formula>
//    - <chemical SMILES="[Na+].[H+].O=C([O-])[O-]" InChI="InChI=1/CH2O3.Na/c2-1(3)4;/h(H2,2,3,4);/q;+1/p-1">
//      NaHCO 
//      <SB>3</SB> 
//      </chemical>
//      </formula>
    // or
//    <formula>
//    <chemical>SCC</chemical> 
//    </formula>    
    // or
//  <formula>VIS</formula> 
    private void formula2Mol(Element elem) {
        Nodes formulas = elem.query(".//formula");
        for (int i = 0; i < formulas.size(); i++) {
            Element formula = (Element) formulas.get(i);
            String value = formula.getValue();
            ParentNode parent = formula.getParent();
            int idx = parent.indexOf(formula);
            formula.detach();
            if (badFormulas.contains(value)) {
    //            System.out.println("Bad: "+value);
                parent.insertChild(new Text(value), idx);
    //            CMLUtil.debug((Element)parent);
            } else {
                CMLMolecule mol = new CMLMolecule();
                parent.insertChild(mol, idx);
                Elements chemicals = formula.getChildElements("chemical");
                if (chemicals.size() == 0) {
                    mol.setTitle(formula.getValue());
                } else {
                    Element chemical = (Element) chemicals.get(0);
                    mol.setTitle(chemical.getValue());
                    String smiles = formula.getAttributeValue("SMILES");
                    if (smiles != null) {
                        CMLScalar scalar = new CMLScalar();
                        scalar.setValue(smiles);
                        scalar.setDictRef(OSCAR_NSP+S_COLON+"smiles");
                        mol.appendChild(scalar);
                    }
                    String inchi = formula.getAttributeValue("InChI");
                    if (smiles != null) {
                        CMLScalar scalar = new CMLScalar();
                        scalar.setValue(inchi);
                        scalar.setDictRef(OSCAR_NSP+S_COLON+"inchi");
                        mol.appendChild(scalar);
                    }
                }
            }
        }
    }

    // deals with chemical/formula/text()
//    private static void badChemicalFormulas(Element chemicalFormula) {
//        if (chemicalFormula.getChildElements().size() == 0) {
//            String value = chemicalFormula.getValue();
//            if (badFormulas.contains(value)) {
//                ParentNode parent = chemicalFormula.getParent();
//                parent.replaceChild(chemicalFormula, new Text(chemicalFormula.getValue()));
//            }
//        }
//    }
    private void badChemicals(Element elem) {
        Nodes chemicals = elem.query(".//chemical");
        for (int i = 0; i < chemicals.size(); i++) {
            Element chemical = (Element) chemicals.get(i);
            if (chemical.getChildElements().size() == 0) {
                String value = chemical.getValue();
                if (badFormulas.contains(value)) {
                    ParentNode parent = chemical.getParent();
                    parent.replaceChild(chemical, new Text(chemical.getValue()));
                }
            }
        }
    }
    
    private void processChemical(Element elem) {
        Nodes chemicals = elem.query(".//chemical");
        for (int i = 0; i < chemicals.size(); i++) {
            Element chemical = (Element) chemicals.get(i);
            unmark(chemical, "SB");
            unmark(chemical, "SP");
            unmark(chemical, "IT");
            Elements elems = chemical.getChildElements();
            if (elems.size() > 0) {
    //            CMLUtil.debug((Element)chemical);
    //            System.out.println(((CMLMolecule)elems.get(0)).getTitle());
    //            error("unexpected chemical child: "+elems.get(0).getLocalName());
                error("unexpected chemical child: "+elems.get(0).getLocalName());
            }
            String value = chemical.getValue();
            CMLMolecule mol = new CMLMolecule();
            mol.setTitle(value);
            mol.setRole("chemical");
            ParentNode parent = chemical.getParent();
            parent.replaceChild(chemical, mol);
    //        System.out.println(">>>chemical>>>"+value);
        }
    }
    
    // ".//CMLMolecule.NS+"[@role='chemical']/following-sibling::CMLMolecule.NS+"[position()=1 and@ref]", CML_XPATH);
    private void chemicalXref(Element elem) {
        Nodes molecules = elem.query(
                ".//"+CMLMolecule.NS+"[@role='chemical']/following-sibling::"+CMLMolecule.NS+"[position()=1 and @ref]", CML_XPATH);
        for (int i = 0; i < molecules.size(); i++) {
            Element molecule = (CMLMolecule) molecules.get(i);
            CMLMolecule previous = (CMLMolecule) molecule.query(
                    "./preceding-sibling::"+CMLMolecule.NS+"[position()=1 and @role='chemical']", CML_XPATH).get(0);
            CMLName name = new CMLName();
            name.setXMLContent(previous.getTitle());
            previous.detach();
            molecule.insertChild(name, 0);
        }
    }

    // molecule followed by REF
    private void moleculeREF(Element elem) {
        Nodes nodes = elem.query(".//"+CMLMolecule.NS+"[following-sibling::*[position()=1" +
                " and self::REF]]", CML_XPATH);
        for (int i = 0; i < nodes.size(); i++) {
            CMLMolecule molecule = (CMLMolecule) nodes.get(i);
            Element ref = (Element) molecule.query("./following-sibling::REF").get(0);
            @SuppressWarnings("unused")
            String type = ref.getAttributeValue("TYPE");
            String id = ref.getAttributeValue("ID");
            String value = ref.getValue();
            CMLLink link = new CMLLink();
            link.setTitle(value);
            link.setTo(id);
            molecule.appendChild(link);
            ref.detach();
        }
    }
    
//    <property type="state">saturated</property> 
    private void processProperty(Element elem) {
        Nodes propertys = elem.query(".//property");
        for (int i = 0; i < propertys.size(); i++) {
            Element prop = (Element) propertys.get(i);
            String type = prop.getAttributeValue("type");
            if (type == null) {
                error("null type");
            }
            if (false) {
            } else if (type.equals("hrms")) {
                hrms(prop);
            } else if (type.equals("nature")) {
                nature(prop);
            } else if (type.equals("state")) {
                state(prop);
            } else if (type.equals("quantity")) {
                quantity(prop);
            } else if (type.equals("yield")) {
                yield(prop);
            } else if (type.equals("elemAnal")) {
                elementalAnalysis(prop);
            } else if (type.equals("mp")) {
                meltingPoint(prop);
            } else if (type.equals("rf")) {
                rf(prop);
            } else if (type.equals("bp")) {
                boilingPoint(prop);
            } else if (type.equals("refractiveindex")) {
                refractiveIndex(prop);
            } else if (type.equals("optRot")) {
                optRot(prop);
            } else {
                error("Type not found "+type);
            }
            prop.detach();
        }
    }

//  mark up text that OSCAR has missed
    private void markText(Element elem) {
        Nodes texts = elem.query(".//text()");
        boolean change = true;
        while (change) {
            change = false;
            for (int i = 0; i < texts.size(); i++) {
                if (markText((Text)texts.get(i))) {
                    change = true;
                }
            }
        }
    }
    
    private boolean markText(Text text) {
        boolean change = false;
        @SuppressWarnings("unused")
        String s = text.getValue();
        
        return change;
    }

//    <DIV>
//    - <HEADER>
//    - <molecule ref="chem26" title="26" xmlns="http://www.xml-cml.org/schema">
//      <name>(2E,4E,6E,8E,10E,12E)-12-Hydroxy-14-[(1R,4S)-4-hydroxy-1,2,2-trimethylcyclopentyl]-2,7,11-trimethyl-14-oxotetradeca-2,4,6,8,10,12-hexaenal</name> 
//      </molecule>
//      </HEADER>
//      <P>...

    private void markSynthesizedCompounds() {
        Nodes divs = experimentalM.query(".//DIV[HEADER[count("+CMLMolecule.NS+") = 1" +
                "and count(text()[string-length(normalize-space()) > 0]) = 0]]", CML_XPATH);
        for (int i = 0; i < divs.size(); i++) {
            Element div = (Element) divs.get(i);
            Element header = (Element) div.getFirstChildElement("HEADER");
            CMLMolecule molecule = (CMLMolecule) header.getFirstChildElement("molecule", CML_NS);
            CMLReactionScheme reactionScheme = createReactionScheme(div);
            addProduct(reactionScheme, molecule);
        }
        
        divs = experimentalM.query(".//DIV[HEADER[count("+CMLMolecule.NS+") = 2]]", CML_XPATH);
        for (int i = 0; i < divs.size(); i++) {
            Element div = (Element) divs.get(i);
            Element header = (Element) div.getFirstChildElement("HEADER");
            Nodes molecules = header.query(CMLMolecule.NS, CML_XPATH);
            int idx0 = header.indexOf(molecules.get(0));
            int idx1 = header.indexOf(molecules.get(1));
//            System.out.println("==============");
            if (idx1 == idx0 + 1) {
                error("Cannot interpret header "+header.getValue());
            } else if (idx1 == idx0 + 2) {
                Node node = header.getChild(idx0+1);
                String s = node.getValue().trim();
                if ("and".equals(s)) {
                    CMLReactionScheme reactionScheme = createReactionScheme(div);
                    addProduct(reactionScheme, (CMLMolecule) molecules.get(0));
                    addProduct(reactionScheme, (CMLMolecule) molecules.get(1));
                } else {
//                    System.out.println("NODE "+s);
                }
            } else {
                for (int j = idx0 + 1; j < idx1; j++) {
                    @SuppressWarnings("unused")
                    Node node = header.getChild(j);
//                    System.out.println("NODE.. "+node.getValue());
                }
            }
        }
    }
    
    private CMLReactionScheme createReactionScheme(Element div) {
                
        CMLReactionScheme reactionScheme = new CMLReactionScheme();
        CMLReaction reaction = new CMLReaction();
        reaction.setRole("overallReaction");
        reactionScheme.addReaction(reaction);
        CMLUtil.transferChildren(div, reactionScheme);
        div.getParent().replaceChild(div, reactionScheme);
        return reactionScheme;
    }

    private void addProduct(CMLReactionScheme reactionScheme, CMLMolecule molecule) {
        CMLReaction reaction = 
            reactionScheme.getReactionElements().get(0);
        CMLProductList productList = 
            reaction.getProductList();
        if (productList == null) {
            productList = new CMLProductList();
            reaction.addProductList(productList);
        }
        CMLProduct product = new CMLProduct();
        productList.addProduct(product);
        molecule.detach();
        product.addMolecule(molecule);
    }

//  (Found: M 
//  <SP>+</SP> 
//  , 
//- <quantity type="found">
//- <value>
//  <point>340.2416</point> 
//  </value>
//  </quantity>
//  . 
//- <quantity type="formula">
//  C 
//  <SB>19</SB> 
//  H 
//  <SB>36</SB> 
//  O 
//  <SB>3</SB> 
//  Si 
//  </quantity>
//  requires 
//  <IT>M</IT> 
//  , 
//- <quantity type="required">
//- <value>
//  <point>340.2435</point> 
//  </value>
//  </quantity>
//  ) 
    private static void hrms(Element prop) {
//        System.out.println(">>>hrms");
        Elements quantities = prop.getChildElements("quantity");
        CMLScalar found = null;
        CMLFormula formula = null;
        CMLScalar required = null;
        List<CMLScalar> ionList = new ArrayList<CMLScalar>();
        for (int i = 0; i < quantities.size(); i++) {
            Element quantity = quantities.get(i);
            int idx = prop.indexOf(quantity);
            String type = quantity.getAttributeValue("type");
            if (type == null) {
                error("null type");
            } else if (type.equals("formula")) {
// <quantity type="formula">C<SB>19</SB>H<SB>36</SB>O<SB>3</SB>Si</quantity>
                String val = quantity.getValue();
                try {
                    formula = CMLFormula.createFormula(val);
                } catch (RuntimeException e) {
                    error("Bad formula: "+e.getMessage());
                }
                if (formula == null) {
                    error("null formula");
                } else {
                    quantity.detach();
                    prop.insertChild(formula, idx);
                }
            } else if (type.equals("found")) {
// <quantity type="found"><value><point>340.2416</point></value></quantity>
                found = getScalar(quantity, "found");
                quantity.detach();
            } else if (type.equals("required")) {
                required = getScalar(quantity, "required");
                quantity.detach();
            } else if (type.equals("ion")) {
//                [Found: ( 
// <quantity type="ion">M – Ac</quantity> 
//                        ) <SP>+</SP> , 497.3463. <quantity type="formula">
                //...
// </quantity> requires <quantity type="ion"> <IT>M</IT> Ac</quantity>, 497.3485]
                CMLScalar ion = new CMLScalar();
                ion.setValue(quantity.getValue());
                ionList.add(ion);
                if (ionList.size() == 1) {
                    ion.setDictRef(OSCAR_NSP+S_COLON+"ionFound");
                } else if (ionList.size() == 2) {
                    ion.setDictRef(OSCAR_NSP+S_COLON+"ionRequired");
                } else {
                    error("Too many ion children");
                }
                quantity.detach();
            } else {
                System.out.println("????????"+type);
            }
        }
        unmark(prop, "SB");
        unmark(prop, "SP");
        unmark(prop, "IT");
        if (formula == null) {
//            CMLUtil.debug((Element)prop.getParent());
//            error("no 'formula' field in hrms ");
            return;
        }
        if (ionList.size() == 0) {
            if (found == null) {
                error("no 'found' field in hrms ");
                return;
            }
            if (required == null) {
                Nodes texts = prop.query("./text()");
                // might have something like: ', 252.0569, found' 
                if (texts.size() > 0) {
                    String value = texts.get(texts.size()-1).getValue();
                    value = value.replaceAll("[)(:, ;]", S_EMPTY);
                    value = value.replaceAll("(\\.)?(F|f)ound", S_EMPTY);
                    try {
                        double d = new Double(value).doubleValue();
                        required = new CMLScalar(d);
                        required.setDictRef(OSCAR_NSP+S_COLON+"required");
//                        System.out.println("req "+d);
                    } catch (NumberFormatException e) {
//                        CMLUtil.debug(prop);
                        System.out.println("Couldn't interpret as hrms required value: "+value);
                    }
                }
                if (required == null) {
                    error(" no 'required' field in hrms ");
                    return;
                }
            }
        } else if (ionList.size() == 2) {
            found = ionList.get(0);
            required = ionList.get(1);
        } else {
//            CMLUtil.debug(prop);
//            error("Wrong number of ions: "+ionList.size());
            return;
        }
        
        formula.appendChild(found);
        formula.appendChild(required);
        int idx = prop.indexOf(formula);
        // tidy text nodes
        String ss = S_EMPTY;
        for (int i = 0; i < idx; i++) {
            Node node = prop.getChild(i);
            if (node instanceof Text) {
                ss += node.getValue();
            }
        }
        CMLScalar foundSc = new CMLScalar();
        foundSc.setDictRef(OSCAR_NSP+S_COLON+"foundString");
        foundSc.setValue(ss);
        formula.appendChild(foundSc);
        ss = S_EMPTY;
        for (int i = idx; i < prop.getChildCount(); i++) {
            Node node = prop.getChild(i);
            if (node instanceof Text) {
                ss += node.getValue();
            }
        }
        CMLScalar requiredSc = new CMLScalar();
        requiredSc.setDictRef(OSCAR_NSP+S_COLON+"requiredString");
        requiredSc.setValue(ss);
        formula.appendChild(requiredSc);
        int nc = prop.getChildCount();
        for (int i = nc-1; i >= 0; i--) {
            if (i != idx) {
                Node node = prop.getChild(i);
                if (node instanceof Text) {
                    node.detach();
                }
            }
        }
    }
    
    private static CMLScalar getScalar(Element quantity, String name) {
        CMLScalar scalar = new CMLScalar();
        scalar.setDictRef(OSCAR_NSP+S_COLON+name);
        double d = getValuePoint(quantity, name);
        scalar.setValue(d);
        return scalar;
    }

//    <property type="yield">
//    - <quantity type="mass">
//    - <value>
//      <point>(71</point> 
//      </value>
//      <units>mg</units> 
//      </quantity>
//      , 
//    - <quantity type="percent">
//    - <value>
//      <point>36</point> 
//      </value>
//      </quantity>
//      %) 
//      </property>
    private static void yield(Element div) {
        CMLProperty yield = new CMLProperty();
        yield.setDictRef(OSCAR_NSP+S_COLON+"yield");
        Elements quantities = div.getChildElements("quantity");
        CMLScalar amount = null;
        CMLScalar mass = null;
        CMLScalar percent = null;
        for (int i = 0; i < quantities.size(); i++) {
            Element quantity = quantities.get(i);
            String type = quantity.getAttributeValue("type");
            if (type == null) {
                error("Must give type on yield");
            } else if (type.equals("mass")) {
                mass = new CMLScalar();
                double d = getValuePoint(quantity, "mass");
                mass.setValue(d);
                mass.setDictRef(OSCAR_NSP+S_COLON+"mass");
                String units = getUnits(quantity, "mass");
                mass.setUnits(OSCAR_NSPUNIT+S_COLON+units);
                yield.appendChild(mass);
            } else if (type.equals("percent")) {
                percent = new CMLScalar();
                double d = getValuePoint(quantity, "percent");
                percent.setValue(d);
                percent.setDictRef(OSCAR_NSP+S_COLON+"percent");
                String units = "percent";
                percent.setUnits(OSCAR_NSPUNIT+S_COLON+units);
                yield.appendChild(percent);
            } else if (type.equals("amount")) {
                amount = new CMLScalar();
                double d = getValuePoint(quantity, "amount");
                amount.setValue(d);
                amount.setDictRef(OSCAR_NSP+S_COLON+"amount");
                String units = getUnits(quantity, "amount");
                amount.setUnits(OSCAR_NSPUNIT+S_COLON+units);
                yield.appendChild(amount);
            } else if (type.equals("quantity")) {
                error("yield/amount not yet implemented");
            } else {
                error("Unknown type on yield: "+type);
            }
        }
        if (mass == null) {
            // maybe the div/preceding-sibling is mass
            Nodes nodes = div.query("./preceding-sibling::*");
            if (nodes.size() > 0 && nodes.get(0) instanceof Element) {
                Element div0 = (Element) nodes.get(0);
                if (div0 instanceof CMLMolecule) {
//                    CMLMolecule molecule = (CMLMolecule) div0;
                } else if (div0.getLocalName().equals("property")) {
                    Element quantity = div0.getFirstChildElement("quantity");
                    if (quantity != null) {
                        mass = getScalar(quantity, "mass");
                    }
                }
            }
            // this may not be an error after all...
            if (mass == null) {
//            try {
//                CMLUtil.debug(div, System.err);
//            } catch (Exception e) {}
//                CMLUtil.debug(div);
//                error("no mass given in yield");
            }
        }
        if (percent == null) {
            error("*no percent given in yield");
        }
    }
    
    private static void elementalAnalysis(Element div) {
//        error("No element analysis");
    }
    
    private static void meltingPoint(Element div) {
//        error("No melting point");
    }
    
    private static void boilingPoint(Element div) {
//      error("No boiling point");
  }
  
    private static void refractiveIndex(Element div) {
//      error("No refractive index");
  }
  
    private static void rf(Element div) {
//      error("No rf");
  }
  
    private static void optRot(Element div) {
//      error("No optRot");
  }
  
    static double getValuePoint(Element elem, String ss) {
        double d = Double.NaN;
        Nodes points = elem.query("value/point");
        Nodes texts = elem.query("value/text()");
        // some constructs <value><point>3</point> × <point>100</point> ...
        if (points.size() == 2 && 
            texts.size() == 1 &&
            texts.get(0).getValue().trim().equals("×")) {
            try {
                d = new Double(points.get(1).getValue()).doubleValue();
            } catch (NumberFormatException nfe) {
                error("Bad double "+nfe+" for "+ss);
            }
            elem.addAttribute(new Attribute("count", points.get(0).getValue()));
        } else if (points.size() == 1){
            String s = ((Element) points.get(0)).getValue();
            // sometimes starts with (
            if (s.startsWith(S_LBRAK)) {
                s = s.substring(1);
            }
            try {
                d = new Double(s).doubleValue();
            } catch (NumberFormatException nfe) {
                error("Bad double "+nfe+" for "+ss);
            }
        } else if (elem.getParent() == null) {
            System.out.println("null parent");
        } else {
            CMLUtil.debug((Element)elem.getParent(), "OSCARTOOL2");
            error("Bad value/point for "+ss+S_SLASH+elem.getParent().getValue());
        }
        if (Double.isNaN(d)) {
            throw new RuntimeException("Unexpected NaN");
        }
        return d;
    }
    
    private static String getUnits(Element elem, String ss) {
        Nodes units = elem.query("units");
        if (units.size() != 1) {
            try {
//                CMLUtil.debug(elem, System.err);
            } catch (Exception e) {}
            error("Bad units for "+ss);
        }
        String s = null;
        if (!ss.equals(elem.getAttributeValue("type"))) {
            System.out.println("no quantity of type found: "+ss);
        } else {
            s = ((Element) units.get(0)).getValue();
            // trim bracket
            if (s.startsWith(S_LBRAK) || s.startsWith(S_MINUS)) {
                s = s.substring(1);
            }
        }
        return s;
    }
    
//    <property type="nature">
//    <quantity type="colour">colorless</quantity> 
//    <quantity type="nonsolidstate">oil</quantity> 
//    </property>
    private static void nature(Element div) {
//        System.out.println(">>>nature");
        CMLProperty nature = new CMLProperty();
        nature.setDictRef(OSCAR_NSP+S_COLON+"nature");
        Elements quantities = div.getChildElements("quantity");
        for (int i = 0; i < quantities.size(); i++) {
            Element quantity = quantities.get(i);
            CMLScalar scalar = new CMLScalar();
            String type = quantity.getAttributeValue("type");
            String value = quantity.getValue();
            if (type == null) {
                error("Must give type on nature");
            } else if (type.equals("colour")) {
                scalar.setDictRef(OSCAR_NSP+S_COLON+type);
                scalar.setValue(value);
            } else if (type.equals("nonsolidstate")) {
                scalar.setDictRef(OSCAR_NSP+S_COLON+type);
                scalar.setValue(value);
            } else if (type.equals("solidstate")) {
                scalar.setDictRef(OSCAR_NSP+S_COLON+type);
                scalar.setValue(value);
            } else if (type.equals("statemodifier")) {
                scalar.setDictRef(OSCAR_NSP+S_COLON+type);
                scalar.setValue(value);
            } else {
                error("Unknown type on nature: "+type);
            }
            nature.appendChild(scalar);
        }
        ParentNode parent = div.getParent();
        parent.replaceChild(div, nature);
    }
    
    private void state(Element prop) {
        CMLProperty property = null;
        if (prop.getChildElements().size() == 0) {
            String val = prop.getValue().toLowerCase();
            for (Pattern pattern : statePropertyMap.keySet()) {
                if (pattern.matcher(val).matches()) {
                    String propertyS = statePropertyMap.get(pattern);
                    if (propertyS == null) {
                        error("Unknown property: "+val);
                    } else {
                        property = new CMLProperty();
                        property.setState(propertyS);
                        property.appendChild(new Text(val));
                    }
                    break;
                }
            }
            if (property == null) {
                System.out.println("Unknown property: "+prop.getValue());
            }
        } else {
//            System.out.println("TYPE "+type);
//                property.setDictRef(OSCAR_NSP+S_COLON+propertyS);
        }
        if (property != null) {
            ParentNode parent = prop.getParent();
            int idx = parent.indexOf(prop);
            parent.insertChild(property, idx);
        } else {
//            CMLUtil.debug(prop);
//            System.out.println("Cannot add state: ");
        }
    }

//    <property type="quantity">
//    - <quantity type="mass">
//    - <value>
//      <point>11.9</point> 
//      </value>
//      <units>g</units> 
//      </quantity>
//      , 
//    - <quantity type="amount">
//    - <value>
//      <point>0.04</point> 
//      </value>
//      <units>mol</units> 
//      </quantity>
//      </property>


	static void quantity(Element property) {
//        System.out.println(">>>quantity>>>");
        CMLProperty cmlProperty = new CMLProperty();
        cmlProperty.setDictRef(OSCAR_NSP+S_COLON+"quantity");
        Elements quantities = property.getChildElements("quantity");
        CMLScalar temperature = null;
        int nQuant = quantities.size();
        for (int i = 0; i < nQuant; i++) {
            String units = null;
            Element oldQuantity = quantities.get(i);
            String oldValue = oldQuantity.getValue();
            String type = oldQuantity.getAttributeValue("type");
            Element unit = oldQuantity.getFirstChildElement("units");
            if ("temperature".equals(type)) {
                unit = parseNonNumericTemperatures(type, unit, oldQuantity);
            }
            if (unit == null) {
                Node node = CMLUtil.getLastTextDescendant(oldQuantity);
                unit = makeUnitFrom(node);
                if (unit != null) {
//                    System.out.println("Interpreted text as unit: "+unit.getValue());
                    node.getParent().replaceChild(node, unit);
                } else {
                    System.out.println("No units given: "+property.getValue()+" :: ");
                }
            }
            if (unit != null) {
                units = unit.getValue();
                if (type == null) {
                    error("Must give type on quantity");
                } else if (
                        type.equals("amount") ||
                        type.equals("conc") || 
                        type.equals("equiv") || 
                        type.equals("integral") || 
                        type.equals("mass") ||
                        type.equals("percent") ||
                        type.equals("time") ||
                        type.equals("volume") ||
                        false) {
                    try {
                        createScalarAndAppendAsChildOfQuantity(cmlProperty, oldQuantity, type, units);
                    } catch (RuntimeException e) {
                        error("unpexcted NaN: "+oldValue);
                    }
                } else if (type.equals("temperature")) {
                    temperature = new CMLScalar();
                    temperature.setDictRef(OSCAR_NSP+S_COLON+"temperature");
                    if ("rt".equalsIgnoreCase(oldQuantity.getValue()) ||
                        "ambient temperature".equals(oldQuantity.getValue()) ||
                        "room temperature".equals(oldQuantity.getValue())) {
                        units = "k";
                        temperature.setUnits(OSCAR_NSPUNIT+S_COLON+units);
                        temperature.setValue(298.15);
                    } else {
                        double d = getValuePoint(oldQuantity, "temperature");
                        temperature.setValue(d);
                        temperature.setDictRef(OSCAR_NSP+S_COLON+"temperature");
                    }
                    temperature.setUnits(OSCAR_NSPUNIT+S_COLON+units);
                    cmlProperty.appendChild(temperature);
               } else {
                    error("Unknown type on quantity: "+type);
               }
            }
        }
        ParentNode parent = property.getParent();
        parent.replaceChild(property,cmlProperty);
    }
    
    private static Element parseNonNumericTemperatures(
        String type, Element unit, Element quant) {
        Node node = CMLUtil.getFirstTextDescendant(quant);
        if (node == null) {
            System.out.println("no text in quantity:");
            CMLUtil.debug(quant, "OSCAR4");
        } else {
            String s = node.getValue();
            for (Pattern temperaturePattern : temperatureMap.keySet()) {
                Matcher matcher = temperaturePattern.matcher(s);
                if (matcher.matches()) {
                    node.detach();
                    Element value = new Element("value");
                    Element point = new Element("point");
                    value.appendChild(point);
                    point.appendChild(new Text(temperatureMap.get(temperaturePattern)));
                    unit = new Element("units");
                    unit.appendChild(new Text("K"));
                    quant.appendChild(value);
                    quant.appendChild(unit);
                    break;
                }
            }
        }
        return unit;
    }

    /** create new CMLScalar and append as child to quantity.
     * 
     * @param quantity
     * @param name
     * @param units
     */
    static void createScalarAndAppendAsChildOfQuantity(CMLProperty property, Element quantity, String name, String units) {
        CMLScalar scalar = new CMLScalar();
        double d = getValuePoint(quantity, name);
        if (Double.isNaN(d)) {
            throw new RuntimeException("Bad quantity: ");
        }
        scalar.setValue(d);
        scalar.setDictRef(OSCAR_NSP+S_COLON+name);
        scalar.setUnits(OSCAR_NSPUNIT+S_COLON+units);
//        quantity.appendChild(scalar);
        property.appendChild(scalar);
    }
    
    private static Element makeUnitFrom(Node node) {
        Element unit = null;
        if (node == null) {
            System.out.println("Null units text");
        } else if (node instanceof Text) {
            String s = node.getValue().trim();
            if (s.equals(S_EMPTY)) {
//                CMLUtil.debug((Element) node.getParent());
                System.out.println("No trailing units given");
            } else {
                String type = unitsMap.get(s);
                if (type == null) {
                    System.out.println("Cannot interpret text as unit:"+s+S_COLON);
                } else {
                    unit = new Element("units");
                    unit.appendChild(new Text(s));
                }
            }
        } else {
            CMLUtil.debug((Element) node, "OSCAR5");
        }
        return unit;
    }
    
    
    private static void error(String s) {
        System.out.println("***ERROR**>>>"+s);
    }
}
