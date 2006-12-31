package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CountExpressionAttribute;
import org.xmlcml.cml.element.Indexable;
import org.xmlcml.cml.element.IndexableList;
import org.xmlcml.cml.tools.PolymerTool.Convention;
import org.xmlcml.euclid.Util;

/**
 * additional tools for fragment. not fully developed
 * 
 * @author pmr
 * 
 */
public class FragmentTool extends AbstractTool {

    Logger logger = Logger.getLogger(FragmentTool.class.getName());


	final static String IDX = "idx";
	final static String F_PREFIX = "f_";
//	private MoleculeFragmentLocator moleculeFragmentLocator;
    private CMLFragment fragment;
    
	/**
	 * @param fragment the fragment to set
	 */
	public void setFragment(CMLFragment fragment) {
		this.fragment = fragment;
	}

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
    	List<Node> molecules = CMLUtil.getQueryNodes(fragment, CMLMolecule.NS, X_CML);
    	if (molecules.size() == 0) {
    		molecule.detach();
    		fragment.insertChild(molecule, 0);
    	} else {
    		fragment.replaceChild(molecules.get(0), molecule);
    	}
    }
	
    /** process basic convention.
     * 
     */
    public void processBasic() {
    	new BasicProcessor(fragment).process();
    }
    
    
    
    /** get first child molecule.
     * 
     * @return molecule or null
     */
    public CMLMolecule getMolecule() {
    	CMLMolecule molecule = null;
    	if (fragment != null) {
        	List<Node> molecules = CMLUtil.getQueryNodes(
        			fragment, CMLMolecule.NS, X_CML);
        	molecule = (molecules.size() == 0) ? null : (CMLMolecule) molecules.get(0); 
    	}
    	return molecule;
    }
    
    /** return expanded countExpression.
     * @return evaluated expression (0 if missing attribute)
     */
    int calculateCountExpression() {
        CountExpressionAttribute cea = 
        	(CountExpressionAttribute) fragment.getCountExpressionAttribute();
        return (cea == null) ? 0 : cea.calculateCountExpression();
    }

    /** process fragment recursively.
     * public mainly for testing
     *
     */
	public void basic_processRecursively() {
		new RecursiveProcessor(fragment).process();
	}
	
	/** expand joins in list.
	 * public for testing
	 *
	 */
	public void expandJoinedList() {
		RecursiveProcessor recursiveProcessor = new RecursiveProcessor(fragment);
		recursiveProcessor.expandJoinedList();
	}
    
	/** process intermediate format.
	 * moved from polymerTool
	 * @param fragmentList
	 */
    public void processIntermediate(Catalog catalog) {
    	IntermediateProcessor intermediateProcessor = 
    		new IntermediateProcessor(fragment);
    	intermediateProcessor.process(catalog);
    }
    
    public void processExplicit() {
    	ExplicitProcessor explicitProcessor = new ExplicitProcessor(fragment);
    	explicitProcessor.process();
    }
    
    /** public only for testing.
     */
    public void substituteParameters() {
    	new IntermediateProcessor(fragment).substituteParameters();
    }
}
class RecursiveProcessor implements CMLConstants {
	
	CMLFragment fragment;
	FragmentTool fragmentTool;
	
	public RecursiveProcessor(CMLFragment fragment) {
		this.fragment = fragment;		
		fragmentTool = new FragmentTool(fragment);
	}
	
    /** process fragment recursively.
     * public mainly for testing
     *
     */
	void process() {
		if (fragment.getCountExpressionAttribute() != null) {
			expandJoinedList();
			this.process();
		} else {
	    	List<Node> nodes = CMLUtil.getQueryNodes(fragment, 
				CMLJoin.NS+X_OR+CMLFragment.NS+X_OR+CMLMolecule.NS, X_CML);
	    	for (Node node : nodes) {
	    		if (node instanceof CMLFragment) {
	    			new RecursiveProcessor((CMLFragment) node).process();
	    		} else if (node instanceof CMLJoin) {
	    			CMLJoin join = (CMLJoin) node;
	    			if (join.getAtomRefs2() == null) {
	    				throw new CMLRuntimeException("join must have atomRefs2");
	    			} else if (join.getMoleculeRefs2() == null) {
	    				throw new CMLRuntimeException("join must have moleculeRefs2");
	    			}
	    		} else if (node instanceof CMLMolecule) {
	    			process((CMLMolecule) node);
	    		}
	    	}
	    	nodes = CMLUtil.getQueryNodes(fragment, 
	    			CMLJoin.NS+X_OR+CMLFragment.NS, X_CML);
	    	if (nodes.size() > 0 && 
				nodes.size() == fragment.getChildElements().size()) {
	    		Node parent = fragment.getParent();
	    		if (parent instanceof CMLFragment) {
	    			fragment.replaceByChildren();
	    		}
	    	}
		}
	}
	
    private void process(CMLMolecule molecule) {
    	List<Node> joins = CMLUtil.getQueryNodes(molecule, CMLJoin.NS, X_CML);
    	for (Node join : joins) {
    		process((CMLJoin) join);
    	}
    }
    
    /** process join recursively.
     * should move to CMLJoin
     * @param join
     */
    private void process(CMLJoin join) {
    	List<Node> frags = CMLUtil.getQueryNodes(join, CMLFragment.NS, X_CML);
    	if (frags.size() != 1) {
    		throw new CMLRuntimeException("exactly one frag child needed");
    	}
    	CMLFragment fragChild = (CMLFragment) frags.get(0);
    	RecursiveProcessor recursiveProcessor = new RecursiveProcessor(fragChild);
    	recursiveProcessor.process();
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
     * public mainly for testing
     *@return fragment (if countExpression evalues to 0, returns null; 
     *if expression is 1 returns the fragment)
     */
    void expandJoinedList() {
    	int count = fragmentTool.calculateCountExpression();
    	if (count != 0) {
    		fragment.removeAttribute("countExpression");
    		List<Node> joins = CMLUtil.getQueryNodes(fragment, CMLJoin.NS, X_CML);
    		List<Node> fragments = CMLUtil.getQueryNodes(fragment, CMLFragment.NS, X_CML);
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
}

class BasicProcessor implements CMLConstants {

	private CMLFragment fragment;
	
	public BasicProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}
	
	public CMLFragment getFragment() {
		return fragment;
	}
	
    void process() {
        CMLUtil.removeWhitespaceNodes(fragment);
        RecursiveProcessor recursiveProcessor = new RecursiveProcessor(fragment);
        recursiveProcessor.process();
        CMLArg.addIdxArgsWithSerialNumber(fragment, CMLMolecule.TAG);
        
        // create join/@atomRefs2
        this.recursivelyCreateAtomsRefs2OnJoins();
        this.replaceFragmentsByChildMolecules();
        fragment.setConvention(Convention.PML_INTERMEDIATE.value);
    }
    
    private void replaceFragmentsByChildMolecules() {
    	List<Node> childMols = CMLUtil.getQueryNodes(
    			fragment, ".//"+CMLMolecule.NS, X_CML);
    	for (Node node : childMols) {
    		CMLElement parent = (CMLElement) node.getParent();
    		if (parent instanceof CMLFragment) {
    			parent.replaceByChildren();
			}
    	}
    }
    
    /** recursive processing.
     */
    
    private void recursivelyCreateAtomsRefs2OnJoins() {

    	List<Node> joins = CMLUtil.getQueryNodes(fragment, CMLJoin.NS, X_CML);
    	List<Node> frags = CMLUtil.getQueryNodes(fragment, CMLFragment.NS, X_CML);
    	List<Node> mols = CMLUtil.getQueryNodes(fragment, CMLMolecule.NS, X_CML);
    	// first process descendants recursively
    	// frag-join-frag...
    	if (joins.size() > 0 && joins.size() + 1 == frags.size()) {
        	for (Node node : frags) {
        		CMLFragment subFragment = (CMLFragment) node;
        		BasicProcessor subProcessor = new BasicProcessor(subFragment);
        		subProcessor.recursivelyCreateAtomsRefs2OnJoins();
        	}
        	int i = 0;
        	for (Node node : joins) {
        		CMLJoin subJoin = (CMLJoin) node;
        		CMLFragment previousFragment = (CMLFragment) frags.get(i);
        		CMLFragment nextFragment = (CMLFragment) frags.get(i+1);
        		cleanMoleculeArgs(previousFragment, FragmentTool.IDX);
        		cleanMoleculeArgs(nextFragment, FragmentTool.IDX);
        		subJoin.processMoleculeRefs2AndAtomRefs2(previousFragment, nextFragment);
        		i++;
        	}
    	} else if (mols.size() == 1) {
    		CMLMolecule subMol = (CMLMolecule) mols.get(0);
        	List<Node> childJoins = CMLUtil.getQueryNodes(subMol, CMLJoin.NS, X_CML);
        	for (Node node : childJoins) {
        		CMLJoin childJoin = (CMLJoin) node;
            	List<Node> childs = CMLUtil.getQueryNodes(childJoin, CMLFragment.NS/*+X_OR+CMLMolecule.NS*/, X_CML);
            	if (childs.size() != 1) {
            		throw new CMLRuntimeException(
        				"branching join requires 1 child fragment; found "+
        				childs.size()+" on "+childJoin.getId());
            	}
            	CMLFragment newFragment = (CMLFragment)childs.get(0);
            	BasicProcessor newProcessor = new BasicProcessor(newFragment);
            	newProcessor.process();
            	newFragment = newProcessor.getFragment();
            	FragmentTool newFragmentTool = new FragmentTool(newFragment);
            	CMLMolecule newMolecule = newFragmentTool.getMolecule();
            	cleanMoleculeArgs(newFragment, FragmentTool.IDX);
            	cleanArgs(subMol, FragmentTool.IDX);
            	childJoin.processMoleculeRefs2AndAtomRefs2(subMol, newMolecule);
        	}
    	}
    }
    
    /** remove spurious IDs and underscores
     * this is really a bug 
     * @param molecule
     * @param argName
     */
    private void cleanMoleculeArgs(CMLFragment fragment, String argName) {
    	List<Node> mols = CMLUtil.getQueryNodes(fragment, CMLMolecule.NS, X_CML);
    	for (Node node : mols) {
    		CMLMolecule molecule = (CMLMolecule) node;
    		cleanArgs(molecule, argName);
    	}
    }
    
    /** remove spurious IDs and underscores
     * this is really a bug 
     * @param molecule
     * @param argName
     */
    private void cleanArgs(CMLMolecule molecule, String argName) {
    	// id may have acquired underscores as result of branches.
    	// remove them
    	String id = molecule.getId();
    	int idx = id.indexOf(S_UNDER);
    	if (idx != -1) {
    		molecule.setId(id.substring(0, idx));
    	}
    	List<Node> args = CMLUtil.getQueryNodes(molecule, CMLArg.NS+"[@name='"+argName+"']", X_CML);
    	while(args.size() > 1) {
    		args.get(1).detach();
    		args.remove(1);
    	}
    }
}

class IntermediateProcessor implements CMLConstants {
	//should consist of molecule (join, molecule)*
	// molecules may have child joins.
	// processing will be recursive
	// process branches first (includes all joining) 
	// then join branches to molecule
	// then process chain
	
	CMLFragment fragment;
	
	public IntermediateProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}
	
	void process(Catalog catalog) {
		List<Node> joins = CMLUtil.getQueryNodes(fragment, ".//"+CMLJoin.NS+"[not(@order)]", X_CML);
		for (Node node : joins) {
		    ((CMLJoin) node).setOrder(CMLBond.SINGLE_S);
		}
		expandTorsionsWithMinMaxValues();
		// lookup referenced molecules; replaces by fragments containing new molecules
		dereferenceMolecules(catalog);
		// this is not yet tested
		dereferenceFragments(catalog);
		fragment.setConvention(Convention.PML_EXPLICIT.value);
	}

	private void dereferenceMolecules(Catalog catalog) {
		List<Node> mols = CMLUtil.getQueryNodes(fragment, ".//"+CMLMolecule.NS+"[@ref]", X_CML);
		for (Node node : mols) {
		    CMLMolecule subMolecule = (CMLMolecule) node;
		    CMLMolecule dereferencedMol = (CMLMolecule) 
		        dereference(catalog, subMolecule, IndexableList.Type.MOLECULE_LIST);
		    if (dereferencedMol == null) {
		    	throw new CMLRuntimeException("Cannot dereference: "+subMolecule.getRef());
		    }
		    subMolecule.removeAttribute("ref");
		    // copy
		    FragmentConverter fragmentConverter = new FragmentConverter(dereferencedMol);
		    // make a new fragment
		    CMLFragment newFragment = fragmentConverter.convertToFragment();
		    FragmentTool newFragmentTool = new FragmentTool(newFragment); 
		    // its molecule becomes the new molecule
		    CMLMolecule newMolecule = newFragmentTool.getMolecule();
		    // transfer anything meaningful from original one
		    CMLUtil.transferChildren(subMolecule, newMolecule);
		    newMolecule.copyAttributesFrom(subMolecule);
		    // replace old molecule
		    subMolecule.getParent().replaceChild(subMolecule, newFragment);
		    // do parameter substitution
		    new IntermediateProcessor(newFragment).substituteParameters();
		    // replace fragments by child molecules
		    newMolecule.detach();
		    newFragment.getParent().replaceChild(newFragment, newMolecule);
		}
	}
	
	
	/** expand fragments.
	* not yet tested
	* @param catalog
	*/
	private void dereferenceFragments(Catalog catalog) {
		List<Node> mols = CMLUtil.getQueryNodes(fragment, ".//"+CMLFragment.NS+"[@ref]", X_CML);
		for (Node node : mols) {
		    CMLFragment subFragment = (CMLFragment) node;
		    CMLFragment newFragment = (CMLFragment) 
		        dereference(catalog, subFragment, IndexableList.Type.FRAGMENT_LIST);
		    subFragment.removeAttribute("ref");
		    // copy
		    FragmentTool newFragmentTool = new FragmentTool(newFragment); 
		    // transfer anything meaningful from original one
		    CMLUtil.transferChildren(subFragment, newFragment);
		    newFragment.copyAttributesFrom(subFragment);
		    // replace old molecule
		    subFragment.getParent().replaceChild(subFragment, newFragment);
		    // do parameter substitution
		    newFragmentTool.substituteParameters();
		    // replace fragment by molecule child
		}
	}
	
	/** expand arguments.
	* calls:
	* CMLArg.substituteParameterName(molecule, name, value);
	* CMLArg.substituteParentAttributes(molecule);
	* CMLArg.substituteTextContent(molecule);
	*/
	void substituteParameters() {
		CMLMolecule molecule = new FragmentTool(fragment).getMolecule();
		Nodes nodes = molecule.query(CMLArg.NS+"[@name]", X_CML);
		for (int i = 0; i < nodes.size(); i++) {
			CMLArg arg = (CMLArg) nodes.get(i);
			String name = arg.getName();
			String value = arg.getString();
			CMLArg.substituteParameterName(molecule, name, value);
			arg.detach();
		}
		CMLArg.substituteParentAttributes(molecule);
		CMLArg.substituteTextContent(molecule);
		fragment.setId(FragmentTool.F_PREFIX+molecule.getId());
	}
	
	
	/** returns a copy of a derefenceable element.
	* 
	* @param catalog
	* @param indexable
	* @param type
	* @return derefernced ellemnt or null
	*/
	private Indexable dereference(Catalog catalog, Indexable indexable, IndexableList.Type type) {
		Indexable deref = null;
		String ref = indexable.getRef();
		
		if (ref != null) {
			String prefix = CMLUtil.getPrefix(ref);
			CMLNamespace namespace = CMLNamespace.createNamespace(prefix, (CMLElement)indexable);
			if (namespace != null) {
				IndexableList indexableList = catalog.getIndexableList(namespace, type);
				if (indexableList != null) {
					deref = indexableList.getById(CMLUtil.getLocalName(ref));
				}
				if (deref == null) {
					((CMLElement)indexableList).debug("II");
				}
			} else {
				((CMLElement)indexable).debug("FAILS TO LOOKUP");
				throw new CMLRuntimeException(
						"Cannot create namespace for indexable lookup;" +
						" check that data namespaces are in scope");
			}
		} else {
			throw new CMLRuntimeException("Null ref on indexable");
		}
		return (deref == null) ? null : (Indexable) ((CMLElement)deref).copy();
	}
	
	private void expandTorsionsWithMinMaxValues() {
		List<Node> torsions = CMLUtil.getQueryNodes(fragment, ".//"+CMLTorsion.NS+"[@min and @max]", X_CML);
		for (Node node : torsions) {
		    CMLTorsion torsion = (CMLTorsion) node;
		    String countExpression = "range("+torsion.getMin()+S_COMMA+torsion.getMax()+S_RBRAK;
		    int value = new CountExpressionAttribute(countExpression).calculateCountExpression();
		    torsion.setXMLContent((double)value);
		    torsion.removeAttribute("min");
		    torsion.removeAttribute("max");
		    torsion.debug("MINMAX");
		}
	}
}

class ExplicitProcessor implements CMLConstants {
	
	CMLFragment fragment;
	
	public ExplicitProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}
	
    void process() {
        //should consist of molecule (join, molecule)*
        CMLElements<CMLJoin> joinList = fragment.getJoinElements();
        CMLElements<CMLMolecule> moleculeList = fragment.getMoleculeElements();
        if (moleculeList.size() - joinList.size() != 1) {
        	throw new CMLRuntimeException("Must have molecule-join-molecule...");
        }
        // immediate children
        List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(
        	fragment, CMLJoin.NS+X_OR+CMLMolecule.NS, X_CML);
        CMLMolecule growingMolecule = (CMLMolecule) moleculeAndJoinList.get(0);
        moleculeAndJoinList.remove(0);
        
        processMoleculeAndJoin(growingMolecule, moleculeAndJoinList);
        // clean join bonds (they are left hanging)
        cleanJoinBonds();
        fragment.setConvention(Convention.PML_COMPLETE.value);
    }
    
    private void processMoleculeAndJoin(
		CMLMolecule growingMolecule, List<Node> moleculeAndJoinList) {
        boolean takeAtomWithLowestId = true;
    	MoleculeTool growingMoleculeTool = new MoleculeTool(growingMolecule);
    	// iterate through the list, removing join-mol
        while (moleculeAndJoinList.size() > 0) {
    		CMLJoin join = (CMLJoin) moleculeAndJoinList.get(0);
    		moleculeAndJoinList.remove(0);
        	CMLMolecule molecule = (CMLMolecule) moleculeAndJoinList.get(0);
    		moleculeAndJoinList.remove(0);
            adjustGeometry(molecule);
            List<Node> joins = CMLUtil.getQueryNodes(molecule, CMLJoin.NS, X_CML);
            detachJoins(joins);
            CMLAtomSet molAtomSet = molecule.getAtomSet();
            growingMoleculeTool.addMoleculeTo(molecule, takeAtomWithLowestId);
            join.addMoleculeTo(growingMolecule, molAtomSet, takeAtomWithLowestId);
        	processMoleculeWithJoinChildren(growingMolecule, joins);
        }
    }
    
    private void detachJoins(List<Node> joinList) {
    	for (Node node : joinList) {
    		node.detach();
    	}
    }
    
    private void adjustGeometry(CMLMolecule molecule) {
        MoleculeTool moleculeTool = new MoleculeTool(molecule);
        moleculeTool.adjustTorsions();
        moleculeTool.adjustAngles();
        moleculeTool.adjustLengths();
        List<Node> args = CMLUtil.getQueryNodes(molecule, CMLArg.NS, X_CML);
        for (Node arg : args) {
        	arg.detach();
        }
    }
    
	private void processMoleculeWithJoinChildren(
		CMLMolecule growingMolecule, List<Node> joinList) {
		for (Node node : joinList) {
			CMLJoin join = (CMLJoin) node;
	        List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(
	        	join, CMLJoin.NS+X_OR+CMLMolecule.NS, X_CML);
	        moleculeAndJoinList.add(0, join);
	        processMoleculeAndJoin(growingMolecule, moleculeAndJoinList);
		}
	}
	
    private void cleanJoinBonds() {
//        List<Node> bondArrays = CMLUtil.getQueryNodes(fragment, "./cml"+CMLBondArray.TAG, X_CML);
//        CMLElements<CMLMolecule> molecules = fragment.getMoleculeElements();
//        if (bondArrays.size() == 1 && molecules.size() == 1) {
//            CMLBondArray bondArray = (CMLBondArray) bondArrays.get(0);
//            CMLMolecule subMolecule = molecules.get(0);
//            List<CMLBond> bonds = bondArray.getBonds();
//            for (CMLBond bond : bonds) {
//                bond.detach();
//                subMolecule.addBond(bond);
//            }
//            bondArray.detach();
//        }
    }
    
}
