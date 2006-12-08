package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CountExpressionAttribute;
import org.xmlcml.cml.tools.PolymerTool.Convention;

/**
 * additional tools for fragment. not fully developed
 * 
 * @author pmr
 * 
 */
public class FragmentTool extends AbstractTool {

    Logger logger = Logger.getLogger(FragmentTool.class.getName());

    CMLFragment fragment;
    CMLMolecule molecule;

    /**
     * constructor
     * 
     * @param fragment
     */
    public FragmentTool(CMLFragment fragment) {
        this.fragment = fragment;
        CMLElements<CMLMolecule> moleculeList = fragment.getMoleculeElements();
        this.molecule = (moleculeList.size() != 1) ? null : moleculeList.get(0);
    }

    /**
     * make fragment tool from a fragment.
     * 
     * @param fragment
     * @return the tool
     */
    static FragmentTool createFragmentTool(CMLFragment fragment) {
        return new FragmentTool(fragment);
    }

    /**
     * get fragment.
     * 
     * @return the fragment
     */
    public CMLFragment getFragment() {
        return fragment;
    }

    /** process basic convention.
     * 
     * @param parent
     */
    public void processBasic(CMLMolecule parent) {
        CMLUtil.removeWhitespaceNodes(fragment);
        // will have to process repeat count here before args
        this.expandCountExpressions();
        // add args first so every molecule has unique id
        CMLArg.addArgs(fragment, CMLMolecule.TAG);
        
        // create join/@atomRefs2
        this.recursivelyCreateAtomsRefs2OnJoins(parent);
        
        // flatten molecules
        List<Node> nonFragments = CMLUtil.getQueryNodes(
                parent, ".//*[self::cml:fragment or " +
                "self::cml:fragmentList]/*[not(self::cml:fragment) " +
                "and not(self::cml:fragmentList)]", X_CML);
        for (Node nonFragment : nonFragments) {
            nonFragment.detach();
            parent.appendChild(nonFragment);
        }
        parent.setConvention(Convention.PML_INTERMEDIATE.value);
    }
    
    /**
     * finds subfragments with
     * 
     * @countExpression and expands these.
     * 
     */
    public void expandCountExpressions() {
        // must do this recursively (WHY??)
        while (true) {
            Nodes nodes = fragment.query(".//cml:fragment[@countExpression]",
                    X_CML);
            if (nodes.size() == 0) {
                break;
            }
            CMLFragment fragment = (CMLFragment) nodes.get(0);
            new FragmentTool(fragment).expandCountExpression();
        }
    }

    // move to fragment
    private void expandCountExpression() {
        Node parent = fragment.getParent();
        Element parentElement = (Element) parent;
        // position of fragment
        int idx = parentElement.indexOf(fragment);
        CountExpressionAttribute cea = (CountExpressionAttribute) fragment.getCountExpressionAttribute();
        int count = cea.calculateCountExpression();
        // detach any fragments without a reference (from hanging bond)
        Nodes nodes = fragment.query("cml:fragment[@ref='']", X_CML);
        if (nodes.size() == 1) {
            nodes.get(0).detach();
        }
        // any child joins? if so detach and transfer to following sibling
        CMLJoin subJoin = null;
        Nodes joins = fragment.query("./cml:join", X_CML);
        if (joins.size() != 0) {
            subJoin = (CMLJoin) joins.get(0);
            System.out.println("FOUND JOIN");
            subJoin.detach();
        } else {
            throw new CMLRuntimeException("EXPECTED JOIN CHILD");
        }
        // clone count-1 fragments and append to existing fragment
        for (int i = 1; i < count; i++) {
            // add join to preceeding fragment
            if (subJoin != null) {
                CMLJoin subJoin1 = new CMLJoin(subJoin);
                parentElement.insertChild(subJoin1, idx);
            }
            CMLFragment fragment1 = new CMLFragment(fragment);
            parentElement.insertChild(fragment1, idx);
        }
    }

    /**
     * get list of joins. looks for child join with linkOnParent attribute
     * 
     * @return list of joins
     */
    public List<CMLFragmentList> getBranchingJoinList() {
        List<CMLFragmentList> branchingJoinList = new ArrayList<CMLFragmentList>();
        String link = "cml:PARENT";
        Nodes branchingJoinNodes = fragment.query(
                "cml:fragmentList[cml:label[@dictRef='"+link+"']]",
                X_CML);
        for (int i = 0; i < branchingJoinNodes.size(); i++) {
            branchingJoinList.add((CMLFragmentList) branchingJoinNodes.get(i));
        }
        return branchingJoinList;
    }
    
    /** expands this fragments with content from refMol.
     * used when this contains only a @ref and no content.
     * will also expand any args
     * @param refMol
     */
    public void expandRefFromFragment(CMLFragment refMol) {
        // copy the fragment from the catalogue
        CMLFragment copyMol = new CMLFragment(refMol);
        
        Nodes nodes = fragment.query("cml:arg", X_CML);
        for (int i = 0; i < nodes.size(); i++) {
            CMLArg arg = (CMLArg) nodes.get(i);
            String name = arg.getName();
            String value = arg.getString();
            CMLArg.substituteParameterName(copyMol, name, value);
        }
        CMLArg.substituteParentAttributes(copyMol);
        CMLArg.substituteTextContent(copyMol);
        Element parent = (Element) fragment.getParent();
        int idx = parent.indexOf(fragment);
        parent.insertChild(copyMol, idx);
        fragment.detach();
    }
    
    /** recursive processing.
     * 
     * @param parent
     */
    
    public void recursivelyCreateAtomsRefs2OnJoins(CMLMolecule parent) {
//    <fragment>
//        <molecule ref="g:benzene" id="m1" />
//        <fragmentList>
//            <join id="j1" order="1" moleculeRefs2="PARENT NEXT" atomRefs2="r1 r1">
//                <length>1.4</length>
//                <angle id="l2.1.1" atomRefs3="a2 r1 r1">115</angle>
//            </join>
//            <fragment>
//                <molecule ref="g:po" id="m3" />
//            </fragment>
        
        @SuppressWarnings("unused")
        CMLMolecule previousMolecule = parent;
        if (this.molecule == null) {
            throw new CMLRuntimeException("Expected child molecule");
        }
        CMLElements<CMLFragmentList> fragmentLists = fragment.getFragmentListElements();
        for (CMLFragmentList fragmentList : fragmentLists) {
            fragmentList.recursivelyCreateAtomsRefs2OnJoins(molecule);
        }
    }

    /** get first child molecule.
     * 
     * @return molecule or null
     */
    public CMLMolecule getMolecule() {
//      <fragment>
//      <molecule ref="g:po" id="m3" />
//  </fragment>
        CMLElements<CMLMolecule> molecules = fragment.getMoleculeElements();
        return (molecules.size() == 0) ? null : molecules.get(0);
        
    }
        
    /** flatten recursively.
     *
     */
    public void flattenFragmentListDescendants() {
        CMLElements<CMLFragmentList> fragmentLists = fragment.getFragmentListElements();
        for (CMLFragmentList fragmentList : fragmentLists) {
            fragmentList.flattenFragmentDescendants();
            CMLUtil.transferChildren(fragmentList, fragment);
        }
    }
}