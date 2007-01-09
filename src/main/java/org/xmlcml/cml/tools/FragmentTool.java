package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CountExpressionAttribute;
import org.xmlcml.cml.element.IdAttribute;
import org.xmlcml.cml.element.Indexable;
import org.xmlcml.cml.element.IndexableList;
import org.xmlcml.cml.element.RefAttribute;
import org.xmlcml.cml.element.CMLJoin.MoleculePointer;
import org.xmlcml.cml.tools.PolymerTool.Convention;
import org.xmlcml.euclid.Point3;
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
    private CMLFragment rootFragment;
    
	/**
	 * @param fragment the fragment to set
	 */
	public void setFragment(CMLFragment fragment) {
		this.rootFragment = fragment;
	}

	/**
     * constructor
     * 
     * @param fragment
     */
    public FragmentTool(CMLFragment fragment) {
        this.rootFragment = fragment;
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
        return rootFragment;
    }
    
    /** set first molecule child of fragment.
     * alters the XOM. If there is no molecule, inserts one
     * if there is replaces it
     * @return null if none
     */
    public void setMolecule(CMLMolecule molecule) {
    	List<Node> molecules = CMLUtil.getQueryNodes(rootFragment, CMLMolecule.NS, X_CML);
    	if (molecules.size() == 0) {
    		molecule.detach();
    		rootFragment.insertChild(molecule, 0);
    	} else {
    		rootFragment.replaceChild(molecules.get(0), molecule);
    	}
    }
	
    /** get first child molecule.
     * 
     * @return molecule or null
     */
    public CMLMolecule getMolecule() {
    	CMLMolecule molecule = null;
    	if (rootFragment != null) {
        	List<Node> molecules = CMLUtil.getQueryNodes(
        			rootFragment, CMLMolecule.NS, X_CML);
        	molecule = (molecules.size() == 0) ? null : (CMLMolecule) molecules.get(0); 
    	}
    	return molecule;
    }
    
    /** process basic convention.
	 * @param catalog to find molecules
	 * "return fragmentList for Markush; null for simple fragment
     */
    public CMLElement processBasic(Catalog catalog) {
    	BasicProcessor basicProcessor = new BasicProcessor(rootFragment);
    	CMLElement generatedElement = basicProcessor.process(catalog);
    	return generatedElement;
    }
    
	/** process intermediate format.
	 * @param catalog to find molecules
	 */
    public void processIntermediate(Catalog catalog) {
    	IntermediateProcessor intermediateProcessor = 
    		new IntermediateProcessor(rootFragment);
    	intermediateProcessor.process(catalog);
    }

    /**  final processing.
     */
    public void processExplicit() {
    	ExplicitProcessor explicitProcessor = new ExplicitProcessor(rootFragment);
    	explicitProcessor.process();
    }

    /** complete processing.
     * 
     * @param moleculeCatalog
     * @return fragmentList (Markush) or null
     */
    public CMLElement processAll(Catalog moleculeCatalog) {
		CMLElement generatedElement = this.processBasic(moleculeCatalog);
		if (generatedElement == null) {
			this.processIntermediate(moleculeCatalog);
			this.processExplicit();
		}
		return generatedElement;
    }
    
    public void substituteFragmentRefsRecursively(int limit) {
    	int count = 0;
    	List<Node> fragmentLists = CMLUtil.getQueryNodes(rootFragment, CMLFragmentList.NS, X_CML);
    	CMLFragmentList fragmentList = (fragmentLists.size() == 0) ? null : (CMLFragmentList) fragmentLists.get(0);
    	if (fragmentList != null) {
	    	while (/*fragmentList != null && */
    			substituteFragmentRefs(fragmentList) && count++ < limit) {
	    	}
	    	fragmentList.detach();
    	}
    	substituteHangingFragmentsByDummy();
    }
    
    /** replace fragment[@ref] by dummy.
     * when recursion is forceably terminated there may be hanging
     * fragments with unresolved references. These are replaced by a
     * molecule reference to a dummy atom.
     */
    private void substituteHangingFragmentsByDummy() {
    	List<Node> unresolvedNodes = CMLUtil.getQueryNodes(
			rootFragment, CMLFragment.NS+"[@ref]", X_CML);
    	for (Node unresolvedFragment : unresolvedNodes) {
    		CMLMolecule dummyMoleculeRef = FragmentTool.createMoleculeRef("g:dummy2");
    		rootFragment.replaceChild(unresolvedFragment, dummyMoleculeRef);
    	}
    }

    /** create a molecule ref to a named resource in the repository.
     * use with care as there is no guarantee that the molecule exists
     * @param ref of the form "g:dummy2"
     * @return
     */
    public static CMLMolecule createMoleculeRef(String ref) {
    	
//    	<molecule id="dummy" xmlns="http://www.xml-cml.org/schema" ref='g:dummy'/>
    	CMLMolecule dummyMolecule = new CMLMolecule();
    	dummyMolecule.setRef(ref);
    	return dummyMolecule;
    }

    /** creates a dummy fragment.
     * may be required if dummy cannot be resolved in repository.
     * 
     * @return
     */
    public static CMLMolecule createDummyMolecule() {
    	
//    	<molecule id="dummy" xmlns="http://www.xml-cml.org/schema"
//    		  title="dummy atom">
//    		  <atomArray>
//    		    <atom id="dummy" elementType="R" x3="1.0" y3="0.0" z3="0.0" formalCharge="0" hydrogenCount="0"/>
//    		    <atom id="r1" elementType="R" x3="0.0" y3="0.0" z3="0.0" formalCharge="0" hydrogenCount="0"/>
//    		  </atomArray>
//    		  <bondArray>
//    		    <bond atomRefs2="dummy r1" order="S"/>
//    		  </bondArray>
//    		</molecule>
    	CMLMolecule dummyMolecule = new CMLMolecule();
    	dummyMolecule.setId("dummy");
    	dummyMolecule.setTitle("dummy");
    	CMLAtom atom1 = new CMLAtom("dummy");
    	atom1.setElementType("R");
    	Point3 xyz = new Point3(1.0, 0., 0.0);
    	atom1.setXYZ3(xyz);
    	dummyMolecule.addAtom(atom1);
    	
    	CMLAtom atom2 = new CMLAtom("r1");
    	atom2.setElementType("R");
    	xyz = new Point3(0.0, 0., 0.0);
    	atom2.setXYZ3(xyz);
    	dummyMolecule.addAtom(atom2);

    	CMLBond bond = new CMLBond(atom1, atom2);
    	bond.setOrder(CMLBond.SINGLE_S);
    	dummyMolecule.addBond(bond);

    	return dummyMolecule;
    }
    
    public boolean substituteFragmentRefs(CMLFragmentList fragmentList) {
    	if (fragmentList == null) {
    		throw new CMLRuntimeException("NULL FRAGMENTLIST");
    	}
    	boolean change = false;
    	// find list of fragments/"ref. May have changed since last time
    	// as new fragments may have refs
    	// avoid fragments in fragmentList
		fragmentList.updateIndex();
    	List<Node> fragmentsWithRefs = CMLUtil.getQueryNodes(rootFragment, 
			"//"+
			CMLFragment.NS+"[@ref and not(../*[@role='markushMixture'])" +
					" and not(ancestor::"+CMLFragmentList.NS+")]", X_CML);
    	for (Node node : fragmentsWithRefs) {
    		CMLFragment refFragment = (CMLFragment) node;
    		String ref = refFragment.getRef();
    		CMLFragment refFragment0 = (CMLFragment) fragmentList.getById(ref);
    		if (refFragment0 == null) {
    			fragmentList.debug("FAILED DEREF");
    			throw new CMLRuntimeException("Cannot find ref: "+ref);
    		}
    		// does the referenced fragment describe a mixture?
    		List<Node> flNodes = CMLUtil.getQueryNodes(refFragment0, 
    				CMLFragmentList.NS+"[@role='markushMixture']", X_CML);
    		// if so, get random fragment
    		if (flNodes.size() == 1) {
    			CMLFragmentList randomFragmentList = (CMLFragmentList) flNodes.get(0);
    			refFragment0 = BasicProcessor.getRandomFragment(randomFragmentList);
    		}
    		// make copy of fragment so as not to deplete reference list
    		CMLFragment derefFragment = (CMLFragment) refFragment0.copy();
    		// remove copy attributes and remove ref attribute unless markush random fragment
    		if (flNodes.size() == 0) {
    			derefFragment.copyAttributesFrom(refFragment);
    			derefFragment.removeAttribute(RefAttribute.NAME);
    		}
    		// remove any id attributes - they will be replaced by enumerated numbers
    		derefFragment.removeAttribute(IdAttribute.NAME);
    		// replace the reference by the object
    		CMLElement parent = (CMLElement) refFragment.getParent();
    		parent.replaceChild(refFragment, derefFragment);
    		// replace parent by fragment unless parent or grandparent
    		// has countExpression
    		if (parent instanceof CMLFragment) {
    			ParentNode grandParentNode = parent.getParent();
    			if (grandParentNode instanceof CMLElement) {
    				CMLElement grandParent = (CMLElement) grandParentNode;
	    			if (parent.getAttribute(CountExpressionAttribute.NAME) != null) {
	    			} else if (grandParent != null && 
	    					grandParent.getAttribute(CountExpressionAttribute.NAME) != null) {
	    			} else if (true || flNodes.size() == 0) {
	    				parent.replaceByChildren();
	    			}
    			}
    		}
    		change = true;
    	}
    	return change;
    }
    
    /** public only for testing.
     */
    public void substituteParameters() {
    	new IntermediateProcessor(rootFragment).substituteParameters();
    }
    
    /** process fragment recursively.
     * public mainly for testing
     *
     */
	public void basic_processRecursively() {
		new CountExpander(rootFragment).process();
	}
}


class CountExpander implements CMLConstants {
	
	CMLFragment topFragment;
	
	public CountExpander(CMLFragment fragment) {
		this.topFragment = fragment;		
	}
	
    /** process fragment recursively.
     * public mainly for testing
     * This can probably be simplified when the syntax is frozen
     *
     */
	void process() {
		while (true) {
			List<Node> expandableFragments = CMLUtil.getQueryNodes(
					topFragment, ".//"+CMLFragment.NS+"[@"+CountExpressionAttribute.NAME+"]", X_CML);
			if (expandableFragments.size() == 0) {
				break;
			}
			for (Node node : expandableFragments) {
				expandCountExpression((CMLFragment) node);
			}
		}
	}
	
    /** return expanded countExpression.
     * @return evaluated expression (0 if missing attribute)
     */
    private int calculateCountExpression(CMLFragment fragment) {
        CountExpressionAttribute cea = 
        	(CountExpressionAttribute) fragment.getCountExpressionAttribute();
        return (cea == null) ? 0 : cea.calculateCountExpression();
    }

    
    /** expands join-fragment and @countExpression() to fragment.
     * starts with:
     *   fragment @countExpression()
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
     * which is converted to:
     *   fragment
     *   join
     *   fragment
     *   join
     *   fragment
     *       
     * where fragment and joins are clones
     * public mainly for testing
     *@return fragment (if countExpression evalues to 0, returns null; 
     *if expression is 1 returns the fragment)
     */
    void expandCountExpression(CMLFragment fragment) {
    	
    	int count = calculateCountExpression(fragment);
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
		fragment.replaceByChildren();
    	// countExpression might evaluate to zero
		fragment.removeAttribute(CountExpressionAttribute.NAME);
    }
}

class BasicProcessor implements CMLConstants {

	private CMLFragment fragment;
	private FragmentTool fragmentTool;
	
	public BasicProcessor(CMLFragment fragment) {
		this.fragment = fragment;
		this.fragmentTool = new FragmentTool(fragment);
	}
	
	public CMLFragment getFragment() {
		return fragment;
	}

	/** manages single fragments and Markush
	 * 
	 * @param catalog
	 * "return fragmentList if Markush; null if fragment
	 */
    CMLElement process(Catalog catalog) {
    	CMLElement generatedElement = null;
        CMLUtil.removeWhitespaceNodes(fragment);
        List<Node> markushs = CMLUtil.getQueryNodes(
        		fragment, CMLFragmentList.NS+"[@role='markush']", X_CML);
        if (markushs.size() != 0) {
        	if (markushs.size() > 1) {
        		throw new CMLRuntimeException("Can only process one Markush child");
        	}
        	CMLFragmentList expandableMarkush = (CMLFragmentList)markushs.get(0);
        	generatedElement = generateFragmentListFromMarkushGroupsAndTarget(expandableMarkush, catalog);
	        fragment.setConvention(Convention.PML_PROCESSED.value);
        } else {
	        fragmentTool.substituteFragmentRefsRecursively(100);
	        new CountExpander(fragment).process();
	        CMLArg.addIdxArgsWithSerialNumber(fragment, CMLMolecule.TAG);
	        this.replaceFragmentsByChildMolecules();
	        this.createAtomsRefs2OnJoins();
	        fragment.setConvention(Convention.PML_INTERMEDIATE.value);
        }
        return generatedElement;
    }
    
    /** generate fragmentList from fragment.
     * creates M * N * P ... fragments in a fragmentList
     * 
     * @param expandableMarkush
     * @param catalog
     * @return generated fragmentList
     */
    private CMLFragmentList generateFragmentListFromMarkushGroupsAndTarget(
		CMLFragmentList expandableMarkush, Catalog catalog) {
//    	fragment
//    		fragmentList (refs)
//    		fragmentList role='markush'
//    			fragmentList role='markushList'
//    			fragment role='markushTarget'
//    				...
//    				fragment ref=...fragmentList => markushList

    	// referenceFragmentList
        List<Node> referenceFragmentLists = CMLUtil.getQueryNodes(
        		fragment, CMLFragmentList.NS+"[not(@role='markush')]", X_CML);
        if (referenceFragmentLists.size() != 1) {
        	throw new CMLRuntimeException(
        		"Must have exactly one referenceFragmentList; was: "+referenceFragmentLists.size());
        }
        
    	// should have 1 or more fragmentLists and one fragment
        List<Node> markushListsOrMixtures = CMLUtil.getQueryNodes(
        		expandableMarkush, CMLFragmentList.NS+
        		    "[@role='markushList' or @role='markushMixture']", X_CML);
        List<Node> markushTargets = CMLUtil.getQueryNodes(
        		expandableMarkush, CMLFragment.NS+"[@role='markushTarget']", X_CML);
        // checks
        if (markushListsOrMixtures.size() == 0) {
        	throw new CMLRuntimeException("No markushLists or markushMixtures given");
        }
        if (markushTargets.size() != 1) {
        	throw new CMLRuntimeException("Must have exactly one markushTarget");
        }
    	String role = ((CMLFragmentList)markushListsOrMixtures.get(0)).getRole();
    	if (role == null) {
    		throw new CMLRuntimeException("must give role attribute on fragmentList");
    	}
    	CMLFragmentList generatedFragmentList = null;
    	if (role.equals("markushList")) {
    		generatedFragmentList = multipleMarkush(markushListsOrMixtures, catalog);
    	} else if (role.equals("markushMixture")) {
    		CMLFragment markushTarget = (CMLFragment) markushTargets.get(0);
    		CountExpressionAttribute cea = 
    			(CountExpressionAttribute) markushTarget.getCountExpressionAttribute();
    		if (cea == null) {
    			throw new CMLRuntimeException("must have count expression for mixture");
    		}
    		cea.detach();
    		int count = cea.calculateCountExpression();
    		generatedFragmentList = markushMixture(markushListsOrMixtures, catalog, count);
    	} else {
    		throw new CMLRuntimeException("Unknown role: "+role);
    	}
        return generatedFragmentList;
    }
    
    private CMLFragmentList multipleMarkush(List<Node> markushLists, Catalog catalog) {
    	
    	List<List<CMLFragment>> cartesianProductList = new ArrayList<List<CMLFragment>>();
    	// seed with new zero-length list
		cartesianProductList.add(new ArrayList<CMLFragment>());
		// iterate through markush lists in turn
    	for (Node node : markushLists) {
    		CMLFragmentList markushList = (CMLFragmentList) node;
        	List<List<CMLFragment>> newCartesianProductList = new ArrayList<List<CMLFragment>>();
        	// convolute with already generated lists
        	for (List<CMLFragment> cartesianProduct : cartesianProductList) {
        		// make copy and add markush for each list
        		for (CMLFragment markushGroup : markushList.getFragmentElements()) {
        			// clone list
        			List<CMLFragment> listCopy = new ArrayList<CMLFragment>(cartesianProduct);
        			listCopy.add(markushGroup);
        			newCartesianProductList.add(listCopy);
        		}
        	}
        	cartesianProductList = newCartesianProductList;
    	}
    	// generate the starting basic fragments
    	CMLFragmentList generatedFragmentList = new CMLFragmentList();
    	for (List<CMLFragment> markushGroupList : cartesianProductList) {
        	// clone the complete fragment
        	CMLFragment newFragment = createSingleBasicFragment(markushGroupList);
        	FragmentTool newFragmentTool = new FragmentTool(newFragment);
        	newFragmentTool.processAll(catalog);
        	generatedFragmentList.addFragment(newFragment);
        }
    	return generatedFragmentList;
    }
    
    private CMLFragmentList markushMixture(
    		List<Node> markushLists, Catalog catalog, int count) {
    	
    	CMLFragmentList generatedFragmentList = new CMLFragmentList();
    	for (int i = 0; i < count; i++) {
    		List<CMLFragment> markushGroupList = new ArrayList<CMLFragment>();
    		for (Node node : markushLists) {
    			CMLFragmentList markushList = new CMLFragmentList((CMLFragmentList) node);
    			CMLFragment randomFragment = getRandomFragment(markushList);
    			markushGroupList.add(randomFragment);
        	}
        	CMLFragment newFragment = createSingleBasicFragment(markushGroupList);
        	if (newFragment != null) {
	        	FragmentTool newFragmentTool = new FragmentTool(newFragment);
	        	newFragmentTool.processAll(catalog);
	        	generatedFragmentList.addFragment(newFragment);
        	} else {
        		System.out.println("NULL "+i);
        	}
    	}
    	return generatedFragmentList;
    }
    
    static CMLFragment getRandomFragment(CMLFragmentList markushList) {
    	CMLElements<CMLFragment> fragments = markushList.getFragmentElements();
    	double sum = 0.0;
    	int nFragments = fragments.size();
    	double[] ratios = new double[nFragments];
    	int i = 0;
    	for (CMLFragment fragment : fragments) {
    		CMLScalar scalar = (CMLScalar) fragment.getFirstCMLChild(CMLScalar.TAG);
    		if (scalar == null) {
    			throw new CMLRuntimeException("must have scalar child");
    		}
    		ratios[i] = scalar.getDouble();
    		if (Double.isNaN(ratios[i])) {
    			scalar.debug("DDDD");
    			throw new CMLRuntimeException("scalar must have double value");
    		}
    		sum += ratios[i];
    		i++;
    	}
    	// get random fragment
    	double rand = Math.random()*sum;
    	int j = nFragments - 1;
    	for (i = 0; i < nFragments; i++) {
    		if (rand < ratios[i]) {
    			j = i;
    			break;
    		}
    	}
    	CMLFragment newFragment = new CMLFragment(fragments.get(j));
    	newFragment.removeChildren();
    	return newFragment;
    }
    
    private CMLFragment createSingleBasicFragment(List<CMLFragment> markushGroupList) {
    	// copy the fragment
    	CMLFragment newFragment = new CMLFragment(fragment);
        CMLFragmentList referenceFragmentList = (CMLFragmentList) CMLUtil.getQueryNodes(
        		newFragment, CMLFragmentList.NS+"[not(@role='markush')]", X_CML).get(0);
        CMLFragmentList markush = (CMLFragmentList) CMLUtil.getQueryNodes(
        		newFragment, CMLFragmentList.NS+"[@role='markush']", X_CML).get(0);
        List<Node> markushGroupLists = CMLUtil.getQueryNodes(
        		markush, CMLFragmentList.NS+
        		"[@role='markushList' or @role='markushMixture']", X_CML);
        for (Node node : markushGroupLists) {
        	node.detach();
        }
        // add single markush group to reference
        int i = 0;
        String newFragmentId = S_EMPTY;
        boolean failed = false;
        for (CMLFragment markushFragment : markushGroupList) {
        	if (markushFragment == null) {
        		failed = true;
        		break;
        	}
        	CMLFragment referenceFragment = new CMLFragment();
	        String ref = markushFragment.getRef();
	        newFragmentId += (i == 0) ? ref : S_UNDER + ref;
	        String markushId = ((CMLFragmentList) markushGroupLists.get(i)).getId();
	        referenceFragment.setId(markushId);
	        markushFragment.detach();
//	        markushFragment.removeChildren();
	        referenceFragment.appendChild(markushFragment);
	        referenceFragmentList.appendChild(referenceFragment);
	        // replace the markush
	        markush.replaceByChildren();
	        i++;
        }
        newFragment.setId(newFragmentId);
        if (failed) {
        	newFragment = null;
        }
        return newFragment;
    }
    
    /** replace fragment/molecule by molecule.
     */
    private void replaceFragmentsByChildMolecules() {
    	List<Node> childMols = CMLUtil.getQueryNodes(
    			fragment, ".//"+CMLFragment.NS+"/"+CMLMolecule.NS, X_CML);
    	for (Node node : childMols) {
    		CMLFragment parent = (CMLFragment) node.getParent();
			parent.replaceByChildren();
    	}
    	while (true) {
	    	childMols = CMLUtil.getQueryNodes(
    			fragment, ".//"+CMLJoin.NS+"/"+CMLFragment.NS+"/"+CMLMolecule.NS, X_CML);
	    	if (childMols.size() == 0) {
	    		break;
	    	}
    		CMLFragment parent = (CMLFragment) childMols.get(0).getParent();
			parent.replaceByChildren();
    	}
    	childMols = CMLUtil.getQueryNodes(
			fragment, ".//"+CMLFragment.NS+"/"+CMLMolecule.NS, X_CML);
    	for (Node node : childMols) {
    		CMLElement parent = (CMLElement) node.getParent();
    		if (parent instanceof CMLFragment) {
    			parent.replaceByChildren();
    		}
    	}
    }
    
    /** recursive processing.
     */
    
    private void createAtomsRefs2OnJoins() {

    	List<Node> joins = CMLUtil.getQueryNodes(fragment, ".//"+CMLJoin.NS, X_CML);
    	for (Node node : joins) {
    		createAtomRefs2OnJoin((CMLJoin) node);
    	}
    }

    private void createAtomRefs2OnJoin(CMLJoin join) {
    	String moleculeRefs2[] = join.getMoleculeRefs2();
    	if (moleculeRefs2 == null) {
    		throw new CMLRuntimeException("Missing moleculeRefs2 on join");
    	}
    	if (MoleculePointer.PARENT.toString().equals(moleculeRefs2[0]) &&
	    	MoleculePointer.CHILD.toString().equals(moleculeRefs2[1])) {
    		joinParentAndChild(join);
    	} else if (MoleculePointer.PREVIOUS.toString().equals(moleculeRefs2[0]) &&
	    	MoleculePointer.NEXT.toString().equals(moleculeRefs2[1])) {
    		joinPreviousAndNext(join);
    	} else {
    		throw new CMLRuntimeException("Cannot proces atomRefs2: "+Util.concatenate(moleculeRefs2, S_SPACE));
    	}
    }

    private void joinParentAndChild(CMLJoin join) {
    	List<Node> nodes = CMLUtil.getQueryNodes(join, 
    			"parent::"+CMLMolecule.NS+"[1]", X_CML);
    	if (nodes.size() == 0) {
    		throw new CMLRuntimeException("Cannot find parent for join");
    	}
    	CMLMolecule parentMolecule = (CMLMolecule) nodes.get(0);
    	
    	nodes = CMLUtil.getQueryNodes(join, "descendant::"+CMLMolecule.NS+"[1]", X_CML);
    	if (nodes.size() == 0) {
    		throw new CMLRuntimeException("Cannot find child for join");
    	}
    	CMLMolecule childMolecule = (CMLMolecule) nodes.get(0);
    	join.processMoleculeRefs2AndAtomRefs2(parentMolecule, childMolecule);
    }
    
    private void joinPreviousAndNext(CMLJoin join) {
    	List<Node> nodes = CMLUtil.getQueryNodes(join, 
			"preceding-sibling::"+CMLMolecule.NS+"[1]", X_CML);
    	if (nodes.size() == 0) {
    		((CMLElement) join.getParent()).debug("Cannot find join");
    		throw new CMLRuntimeException("Cannot find previous for join: "+join.getString());
    	}
    	CMLMolecule previousMolecule = (CMLMolecule) nodes.get(0);
    	
    	nodes = CMLUtil.getQueryNodes(join, 
    			"following-sibling::*/descendant-or-self::"+CMLMolecule.NS+"[1]", X_CML);
    	if (nodes.size() == 0) {
    		throw new CMLRuntimeException("Cannot find next for join");
    	}
    	CMLMolecule nextMolecule = (CMLMolecule) nodes.get(0);
    	join.processMoleculeRefs2AndAtomRefs2(previousMolecule, nextMolecule);
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
					((CMLElement)indexableList).debug("cannot dereference: "+ref);
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
//		    torsion.debug("MINMAX");
		}
	}
}

class ExplicitProcessor implements CMLConstants {
	
	CMLFragment fragment;
	CMLMolecule growingMolecule;
	MoleculeTool growingMoleculeTool;
    boolean takeAtomWithLowestId = true;
	
	public ExplicitProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}
	
    void process() {
        //should consist of molecule (join, molecule)*
        List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(
            	fragment, CMLJoin.NS+X_OR+CMLMolecule.NS, X_CML);
        checkMoleculeAndJoinList(moleculeAndJoinList);
        // immediate children
        growingMolecule = (CMLMolecule) moleculeAndJoinList.get(0);
    	growingMoleculeTool = new MoleculeTool(growingMolecule);
        moleculeAndJoinList.remove(0);
        processMolecule(growingMolecule, null);
        processMoleculeAndJoin(moleculeAndJoinList);
        fragment.setConvention(Convention.PML_COMPLETE.value);
    }
    
    private void checkMoleculeAndJoinList(List<Node> moleculeAndJoinList) {
    	if (moleculeAndJoinList.size() %2 != 1) {
    		throw new CMLRuntimeException("must have molecule-(join-molecule)*");
    	}
    	String[] names = new String[]{CMLMolecule.TAG, CMLJoin.TAG};
    	int i = 1;
    	int j = 0;
    	for (Node node : moleculeAndJoinList) {
			i = 1 - i;
    		if (!((CMLElement) node).getLocalName().equals(names[i])) {
    			throw new CMLRuntimeException("expected "+names[i]+" in element: "+j);
    		}
    		j++;
    	}
    }
    
    private void processMoleculeAndJoin(List<Node> moleculeAndJoinList) {
    	// iterate through the list, removing join-mol
        while (moleculeAndJoinList.size() > 0) {
    		CMLJoin join = (CMLJoin) moleculeAndJoinList.get(0);
    		moleculeAndJoinList.remove(0);
        	CMLMolecule molecule = (CMLMolecule) moleculeAndJoinList.get(0);
    		moleculeAndJoinList.remove(0);
    		processMolecule(molecule, join);
        }
    }
    
    private void processMolecule(CMLMolecule molecule, CMLJoin join) {
        adjustGeometry(molecule);
        CMLArg.removeArgs(molecule);
        List<Node> joins = CMLUtil.getQueryNodes(molecule, CMLJoin.NS, X_CML);
        detachJoins(joins);
        if (join != null) {
	        CMLAtomSet molAtomSet = molecule.getAtomSet();
	        growingMoleculeTool.addMoleculeTo(molecule, takeAtomWithLowestId);
        	join.addMoleculeTo(growingMolecule, molAtomSet, takeAtomWithLowestId);
        }
    	processMoleculeWithJoinChildren(growingMolecule, joins);
    }
    
	private void processMoleculeWithJoinChildren(
		CMLMolecule growingMolecule, List<Node> joinList) {
		for (Node node : joinList) {
			CMLJoin join = (CMLJoin) node;
	        List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(
	        	join, CMLJoin.NS+X_OR+CMLMolecule.NS, X_CML);
	        moleculeAndJoinList.add(0, join);
	        processMoleculeAndJoin(moleculeAndJoinList);
		}
	}
	
    private void detachJoins(List<Node> joinList) {
    	for (Node node : joinList) {
    		node.detach();
    	}
    }
    
    private void adjustGeometry(CMLMolecule molecule) {
        MoleculeTool moleculeTool = new MoleculeTool(molecule);
        // do them in this order to avoid interaction
        moleculeTool.adjustLengths();
        moleculeTool.adjustAngles();
        moleculeTool.adjustTorsions();
    }
}
