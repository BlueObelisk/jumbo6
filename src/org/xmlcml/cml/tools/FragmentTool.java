package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

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

    /**
     * constructor
     * 
     * @param fragment
     */
    public FragmentTool(CMLFragment fragment) {
        this.fragment = fragment;
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
    
    /** set first molecule child of fragment.
     * alters the XOM. If there is no molecule, inserts one
     * if there is replaces it
     * @return null if none
     */
    public void setMolecule(CMLMolecule molecule) {
    	List<Node> molecules = CMLUtil.getQueryNodes(fragment, "./cml:molecule", X_CML);
    	if (molecules.size() == 0) {
    		fragment.insertChild(molecule, 0);
    	} else {
    		fragment.replaceChild(molecules.get(0), molecule);
    	}
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
        CMLMolecule molecule = this.getMolecule();
        if (molecule == null) {
            throw new CMLRuntimeException("Expected child molecule in fragment");
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
    	CMLMolecule molecule = null;
    	if (fragment != null) {
        	List<Node> molecules = CMLUtil.getQueryNodes(
        			fragment, "./cml:molecule", X_CML);
        	molecule = (molecules.size() == 0) ? null : (CMLMolecule) molecules.get(0); 
    	}
    	return molecule;
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
    
    /** process children of form fragment-join-fragment.
     */
//    public void joinChildFragments() {
//    	List<CMLElement> childElements = fragment.getChildCMLElements();
//    	CMLFragment topFragment = null;
//    	for (CMLElement child : childElements) {
//    		if (child instanceof CMLFragmentList) {
//    			FragmentListTool fragmentListTool = 
//    				new FragmentListTool((CMLFragmentList) child);
//			}
//    	}
//    }
    
    public static void processRecursively(CMLMolecule molecule) {
    	List<Node> joins = CMLUtil.getQueryNodes(molecule, "./cml:join", X_CML);
    	for (Node join : joins) {
    		FragmentTool.processRecursively((CMLJoin) join);
    	}
    }
    
    /** return expanded countExpression.
     * @return evaluated expression (0 if missing attribute)
     */
    private int calculateCountExpression() {
//        Node parent = fragment.getParent();
//        Element parentElement = (Element) parent;
//        // position of fragment
//        int idx = parentElement.indexOf(fragment);
        CountExpressionAttribute cea = 
        	(CountExpressionAttribute) fragment.getCountExpressionAttribute();
        return (cea == null) ? 0 : cea.calculateCountExpression();
    }

    /** expands join-fragment and @countExpression() to fragment.
     * starts with:
     *   fragmentList @countExpression()
     *       join
     *       fragment
     *       
     * and returns:
     *   fragment
     *       fragment
     *       join
     *       fragment
     *       join
     *       fragment
     *       
     * where fragment and joins are clones
     *
     *@return fragment (if countExpression evalues to 0, returns null; 
     *if expression is 1 returns the fragment)
     */
    public void expandJoinedList() {
//    	CMLFragment parentFragment = null;
    	int count = calculateCountExpression();
    	if (count != 0) {
    		fragment.removeAttribute("countExpression");
    		List<Node> joins = CMLUtil.getQueryNodes(fragment, "./cml:join", X_CML);
    		List<Node> fragments = CMLUtil.getQueryNodes(fragment, "./cml:fragment", X_CML);
    		if (joins.size() != 1 || fragments.size() != 1) {
    			throw new CMLRuntimeException("wrong format; requires exactly 1 join and 1 fragment");
    		}
    		CMLJoin childJoin = (CMLJoin) joins.get(0);
    		CMLFragment childFragment = (CMLFragment) fragments.get(0);
    		for (int i = 0; i < count; i++) {
    			fragment.appendChild(childFragment.copy());
    			if (i < count - 1) {
        			fragment.appendChild(childJoin.copy());
    			}
    		}
    		childJoin.detach();
    		childFragment.detach();
    	}
    }
    
    /** process join recursively.
     * should move to CMLJoin
     * @param join
     */
    public static void processRecursively(CMLJoin join) {
    	List<Node> frags = CMLUtil.getQueryNodes(join, "./cml:fragment", X_CML);
    	if (frags.size() != 1) {
    		throw new CMLRuntimeException("exactly one frag child needed");
    	}
    	CMLFragment fragChild = (CMLFragment) frags.get(0);
    	new FragmentTool(fragChild).processRecursively();
    }
    
	public void processRecursively() {
		if (fragment.getCountExpressionAttribute() != null) {
			expandJoinedList();
			this.processRecursively();
		} else {
	    	List<Node> nodes = CMLUtil.getQueryNodes(fragment, 
				"./cml:join | ./cml:fragment | ./cml:molecule", X_CML);
	    	for (Node node : nodes) {
	    		if (node instanceof CMLFragment) {
	    			new FragmentTool((CMLFragment) node).processRecursively();
	    		} else if (node instanceof CMLJoin) {
	    			CMLJoin join = (CMLJoin) node;
	    			if (join.getAtomRefs2() == null) {
	    				throw new CMLRuntimeException("join must have atomRefs2");
	    			} else if (join.getMoleculeRefs2() == null) {
	    				throw new CMLRuntimeException("join must have moleculeRefs2");
	    			}
	    		} else if (node instanceof CMLMolecule) {
	    			FragmentTool.processRecursively((CMLMolecule) node);
	    		}
	    	}
	    	nodes = CMLUtil.getQueryNodes(fragment, 
	    			"./cml:join | ./cml:fragment", X_CML);
	    	if (nodes.size() > 0 && 
				nodes.size() == fragment.getChildElements().size()) {
	    		fragment.replaceByChildren();
	    	}
		}
	}
	
}