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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.attribute.RefAttribute;
import org.xmlcml.cml.attribute.main.CountExpressionAttribute;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLJoin.MoleculePointer;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLProperty.Type;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.map.Indexable;
import org.xmlcml.cml.map.IndexableByIdList;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * additional tools for fragment. not fully developed
 * 
 * @author pmr
 * 
 */
public class FragmentTool extends AbstractTool {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(FragmentTool.class);

	Logger logger = Logger.getLogger(FragmentTool.class.getName());

	int recursiveLimit = 100;

	public int getRecursiveLimit() {
		return recursiveLimit;
	}

	public void setRecursiveLimit(int recursiveLimit) {
		this.recursiveLimit = recursiveLimit;
	}

	/**
	 * polymer conventions.
	 * 
	 */
	public enum Convention {
		/** signifies a branch */
		BRANCH("branch"),
		/** concise formula string - obsolete. */
		PML_CONCISE(C_A + "PML-concise"),
		/** basic XML formula. */
		PML_BASIC(C_A + "PML-basic"),
		/** molecule references. */
		PML_INTERMEDIATE(C_A + "PML-intermediate"),
		/** explicit un-joined molecules. */
		PML_EXPLICIT(C_A + "PML-explicit"),
		/** complete molecules (includes cartesian coords). */
		PML_COMPLETE(C_A + "PML-complete"),
		/** inline atom obsolete. */
		PML_INLINE_ATOM(C_A + "PML-inline-atom"),
		/** default endpoint. */
		PML_DEFAULT_FINAL(PML_COMPLETE.v),
		/** processed (nothing further to do) Normally Markush. */
		PML_PROCESSED(C_A + "PML-processed"), ;
		String v;

		private Convention(String v) {
			this.v = v;
		}
	}

	final static String IDX = "idx";

	final static String F_PREFIX = "f_";

	private CMLFragment rootFragment;

	private long seed = 0L;

	private ResourceManager resourceManager;

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * Sets the seed for the random number generator used for markush mixture
	 * default is 0 which generates a random seed normally if seed is !=0 then
	 * that value is used giving predictable results
	 * 
	 * @param seed
	 * 
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	/**
	 * @param fragment
	 *            the fragment to set
	 */
	public void setFragment(CMLFragment fragment) {
		this.rootFragment = fragment;
	}

	/**
	 * constructor
	 * 
	 * @param fragment
	 * @deprecated use getOrCreateFragmentTool
	 */
	public FragmentTool(CMLFragment fragment) {
		this.rootFragment = fragment;
	}

	/**
	 * gets FragmentTool associated with fragment. if null creates one and sets
	 * it in fragment
	 * 
	 * @param fragment
	 * @return tool
	 */
	public static FragmentTool getOrCreateTool(CMLFragment fragment) {
		FragmentTool fragmentTool = null;
		if (fragment != null) {
			fragmentTool = (FragmentTool) fragment.getTool();
			if (fragmentTool == null) {
				fragmentTool = new FragmentTool(fragment);
				fragment.setTool(fragmentTool);
			}
		}
		return fragmentTool;
	}

	/**
	 * get fragment.
	 * 
	 * @return the fragment
	 */
	public CMLFragment getFragment() {
		return rootFragment;
	}

	/**
	 * set first molecule child of fragment. alters the XOM. If there is no
	 * molecule, inserts one if there is replaces it
	 * 
	 * @param molecule
	 */
	public void setMolecule(CMLMolecule molecule) {
		List<Node> molecules = CMLUtil.getQueryNodes(rootFragment, CMLMolecule.NS, CMLConstants.CML_XPATH);
		if (molecules.size() == 0) {
			molecule.detach();
			rootFragment.insertChild(molecule, 0);
		} else {
			rootFragment.replaceChild(molecules.get(0), molecule);
		}
	}

	/**
	 * get first child molecule.
	 * 
	 * @return molecule or null
	 */
	public CMLMolecule getMolecule() {
		CMLMolecule molecule = null;
		if (rootFragment != null) {
			List<Node> molecules = CMLUtil.getQueryNodes(rootFragment, CMLMolecule.NS, CMLConstants.CML_XPATH);
			molecule = (molecules.size() == 0) ? null : (CMLMolecule) molecules.get(0);
		}
		return molecule;
	}

	/**
	 * process basic convention.
	 * 
	 * @param resourceManager
	 *            to find molecules
	 * @return fragmentList for Markush; null for simple fragment
	 * @return element
	 */
	public CMLElement processBasic(ResourceManager resourceManager) {
		BasicProcessor basicProcessor = new BasicProcessor(rootFragment, seed);
		CMLElement generatedElement = basicProcessor.process(resourceManager);
		return generatedElement;
	}

	/**
	 * process basic convention.
	 * 
	 * @param resourceManager
	 *            to find molecules
	 * @return fragmentList for Markush; null for simple fragment
	 * @return element
	 */
	@Deprecated
	public CMLElement processBasic() {
		BasicProcessor basicProcessor = new BasicProcessor(rootFragment, seed);
		CMLElement generatedElement = basicProcessor.process();
		return generatedElement;
	}

	/**
	 * process intermediate format.
	 * 
	 * @param catalog
	 *            to find molecules
	 */
	public void processIntermediate(ResourceManager resourceManager) {
		IntermediateProcessor intermediateProcessor = new IntermediateProcessor(rootFragment);
		intermediateProcessor.setResourceManager(resourceManager);
		intermediateProcessor.process(resourceManager);
	}

	/**
	 * final processing.
	 */
	public void processExplicit() {
		ExplicitProcessor explicitProcessor = new ExplicitProcessor(rootFragment);
		explicitProcessor.process();
	}

	/**
	 * complete processing.
	 * 
	 * @param resourceManager
	 * @return fragmentList (Markush) or null
	 */
	public CMLElement processAll(ResourceManager resourceManager) {
		CMLElement generatedElement = this.processBasic(resourceManager);
		if (generatedElement == null) {
			this.processIntermediate(resourceManager);
			this.processExplicit();
		}
		return generatedElement;
	}

	/**
	 * substitute fragment refs.
	 * 
	 * @param limit
	 */
	public void substituteFragmentRefsRecursively(int limit) {
		int count = 0;
		List<Node> fragmentLists = CMLUtil.getQueryNodes(rootFragment, CMLFragmentList.NS, CMLConstants.CML_XPATH);
		CMLFragmentList fragmentList = (fragmentLists.size() == 0) ? null : (CMLFragmentList) fragmentLists.get(0);
		if (fragmentList != null) {
			while (/* fragmentList != null && */
			substituteFragmentRefs(fragmentList) && count++ < limit) {
			}
			fragmentList.detach();
		}
		substituteHangingFragmentsByDummy();
	}

	/**
	 * replace fragment[@ref] by dummy. when recursion is forceably terminated
	 * there may be hanging fragments with unresolved references. These are
	 * replaced by a molecule reference to a dummy atom.
	 */
	private void substituteHangingFragmentsByDummy() {
		List<Node> unresolvedNodes = CMLUtil.getQueryNodes(rootFragment, CMLFragment.NS + "[@ref]",
				CMLConstants.CML_XPATH);
		for (Node unresolvedFragment : unresolvedNodes) {
			CMLMolecule dummyMoleculeRef = FragmentTool.createMoleculeRef("g:dummy2");
			rootFragment.replaceChild(unresolvedFragment, dummyMoleculeRef);
		}
	}

	/**
	 * create a molecule ref to a named resource in the repository. use with
	 * care as there is no guarantee that the molecule exists
	 * 
	 * @param ref
	 *            of the form "g:dummy2"
	 * @return molecule
	 */
	public static CMLMolecule createMoleculeRef(String ref) {

		// <molecule id="dummy" xmlns="http://www.xml-cml.org/schema"
		// ref='g:dummy'/>
		CMLMolecule dummyMolecule = new CMLMolecule();
		dummyMolecule.setRef(ref);
		return dummyMolecule;
	}

	/**
	 * creates a dummy fragment. may be required if dummy cannot be resolved in
	 * repository.
	 * 
	 * @return molecule
	 */
	public static CMLMolecule createDummyMolecule() {

		// <molecule id="dummy" xmlns="http://www.xml-cml.org/schema"
		// title="dummy atom">
		// <atomArray>
		// <atom id="dummy" elementType="R" x3="1.0" y3="0.0" z3="0.0"
		// formalCharge="0" hydrogenCount="0"/>
		// <atom id="r1" elementType="R" x3="0.0" y3="0.0" z3="0.0"
		// formalCharge="0" hydrogenCount="0"/>
		// </atomArray>
		// <bondArray>
		// <bond atomRefs2="dummy r1" order="S"/>
		// </bondArray>
		// </molecule>
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

	/**
	 * substitute fragment refs.
	 * 
	 * @param fragmentList
	 * @return any change?
	 */
	public boolean substituteFragmentRefs(CMLFragmentList fragmentList) {
		if (fragmentList == null) {
			throw new RuntimeException("NULL FRAGMENTLIST");
		}
		boolean change = false;
		// find list of fragments/"ref. May have changed since last time
		// as new fragments may have refs
		// avoid fragments in fragmentList
		FragmentListTool fragmentListTool = FragmentListTool.getOrCreateTool(fragmentList);
		// fragmentListTool.updateIndex();
		List<Node> fragmentsWithRefs = CMLUtil.getQueryNodes(rootFragment, "//" + CMLFragment.NS
				+ "[@ref and not(../*[@role='markushMixture'])" + " and not(ancestor::" + CMLFragmentList.NS + ")]",
				CMLConstants.CML_XPATH);
		for (Node node : fragmentsWithRefs) {
			CMLFragment refFragment = (CMLFragment) node;
			String ref = refFragment.getRef();
			CMLFragment refFragment0 = (CMLFragment) fragmentListTool.getFragmentById(ref);
			if (refFragment0 == null) {
				fragmentList.debug("FAILED DEREF");
				throw new RuntimeException("Cannot find ref: " + ref);
			}
			// does the referenced fragment describe a mixture?
			List<Node> flNodes = CMLUtil.getQueryNodes(refFragment0, CMLFragmentList.NS + "[@role='markushMixture']",
					CMLConstants.CML_XPATH);
			// if so, get random fragment
			if (flNodes.size() == 1) {
				CMLFragmentList randomFragmentList = (CMLFragmentList) flNodes.get(0);
				BasicProcessor bp = new BasicProcessor(rootFragment, seed);
				bp.setResourceManager(this.resourceManager);
				refFragment0 = bp.getRandomFragment(randomFragmentList, fragmentList);

			}
			// make copy of fragment so as not to deplete reference list
			CMLFragment derefFragment = (CMLFragment) refFragment0.copy();
			// remove copy attributes and remove ref attribute unless markush
			// random fragment
			if (flNodes.size() == 0) {
				derefFragment.copyAttributesFrom(refFragment);
				derefFragment.removeAttribute(RefAttribute.NAME);
			}
			// remove any id attributes - they will be replaced by enumerated
			// numbers
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
					} else if (grandParent != null && grandParent.getAttribute(CountExpressionAttribute.NAME) != null) {
					} else if (true || flNodes.size() == 0) {
						parent.replaceByChildren();
					}
				}
			}
			change = true;
		}
		return change;
	}

	/**
	 * public only for testing.
	 */
	public void substituteParameters() {
		new IntermediateProcessor(rootFragment).substituteParameters();
	}

	/**
	 * process fragment recursively. public mainly for testing
	 * 
	 */
	public void basic_processRecursively() {
		new CountExpander(rootFragment).process();
	}

	/**
	 * pruneRtoH.
	 * 
	 * @author nwe23 used to add H atoms on to vacant R groups
	 */
	public void pruneRtoH() {
		List<Node> ratoms = CMLUtil.getQueryNodes(rootFragment, ".//" + CMLAtom.NS + "//@" + "elementType",
				CMLConstants.CML_XPATH);
		if (ratoms.size() == 0) {
			return;
		}

		for (Node node : ratoms) {
			if ("R".equals(node.getValue())) {
				Attribute a = (Attribute) node;
				a.setValue(AS.H.value);
			}
		}
	}

	/**
	 * 
	 * @param element
	 *            to be changed to R Changes all occurances of the specified
	 *            element to R
	 */
	public void ElementtoR(String element) {
		List<Node> ratoms = CMLUtil.getQueryNodes(rootFragment, ".//" + CMLAtom.NS + "//@" + "elementType",
				CMLConstants.CML_XPATH);
		if (ratoms.size() == 0) {
			return;
		}

		for (Node node : ratoms) {
			if (element.equals(node.getValue())) {
				Attribute a = (Attribute) node;
				a.setValue("R");
			}
		}
	}

	/**
	 * @return random seed
	 */
	public long getSeed() {
		return seed;
	}

}

class CountExpander implements CMLConstants {

	CMLFragment topFragment;

	CountExpander(CMLFragment fragment) {
		this.topFragment = fragment;
	}

	/**
	 * process fragment recursively. public mainly for testing This can probably
	 * be simplified when the syntax is frozen
	 * 
	 */
	void process() {
		while (true) {
			List<Node> expandableFragments = CMLUtil.getQueryNodes(topFragment, ".//" + CMLFragment.NS + "[@"
					+ CountExpressionAttribute.NAME + "]", CMLConstants.CML_XPATH);
			if (expandableFragments.size() == 0) {
				break;
			}
			for (Node node : expandableFragments) {
				expandCountExpression((CMLFragment) node);
			}
		}
	}

	/**
	 * return expanded countExpression.
	 * 
	 * @return evaluated expression (0 if missing attribute)
	 */
	private int calculateCountExpression(CMLFragment fragment) {
		CountExpressionAttribute cea = new CountExpressionAttribute(fragment.getCountExpressionAttribute());
		return (cea == null) ? 0 : cea.calculateCountExpression();
	}

	/**
	 * expands join-fragment and @countExpression() to fragment. starts with:
	 * fragment @countExpression() join fragment
	 * 
	 * and returns: fragment fragment join fragment join fragment
	 * 
	 * which is converted to: fragment join fragment join fragment
	 * 
	 * where fragment and joins are clones public mainly for testing if
	 * expression is 1 returns the fragment)
	 * 
	 * @param fragment
	 */
	void expandCountExpression(CMLFragment fragment) {

		int count = calculateCountExpression(fragment);
		List<Node> joins = CMLUtil.getQueryNodes(fragment, CMLJoin.NS, CMLConstants.CML_XPATH);
		List<Node> fragments = CMLUtil.getQueryNodes(fragment, CMLFragment.NS, CMLConstants.CML_XPATH);
		if (joins.size() != 1 || fragments.size() != 1) {
			System.err.println(fragment.toXML());
			throw new RuntimeException("wrong format; requires exactly 1 join and 1 fragment");
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
	private static Logger LOG = Logger.getLogger(BasicProcessor.class);

	private CMLFragment fragment;

	private FragmentTool fragmentTool;

	private Random randomGenerator;

	private ResourceManager resourceManager;

	/**
	 * constructor.
	 * 
	 * @param fragment
	 *            use the version which sets the seed instead.
	 */
	@Deprecated
	public BasicProcessor(CMLFragment fragment) {
		this.fragment = fragment;
		this.fragmentTool = FragmentTool.getOrCreateTool(fragment);
	}

	/**
	 * start processor with known seed
	 * 
	 * @param fragment
	 * @param seed
	 */
	public BasicProcessor(CMLFragment fragment, long seed) {
		this.fragment = fragment;
		this.fragmentTool = FragmentTool.getOrCreateTool(fragment);
		this.fragmentTool.setSeed(seed);
	}

	/**
	 * get fragment.
	 * 
	 * @return fragment
	 */
	public CMLFragment getFragment() {
		return fragment;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * manages single fragments and Markush
	 * 
	 * @param resourceManager
	 *            "return fragmentList if Markush; null if fragment
	 */
	CMLElement process(ResourceManager resourceManager) {
		CMLElement generatedElement = null;
		CMLUtil.removeWhitespaceNodes(fragment);
		List<Node> markushs = CMLUtil.getQueryNodes(fragment, CMLFragmentList.NS + "[@role='markush']",
				CMLConstants.CML_XPATH);
		if (markushs.size() != 0) {
			generatedElement = processMarkush(resourceManager, markushs);
		} else {
			processNonMarkush();
		}
		return generatedElement;
	}

	/**
	 * manages single fragments and Markush
	 * 
	 * @param resourceManager
	 *            "return fragmentList if Markush; null if fragment
	 */
	CMLElement process() {
		CMLElement generatedElement = null;
		CMLUtil.removeWhitespaceNodes(fragment);
		processNonMarkush();
		return generatedElement;
	}

	private void processNonMarkush() {
		fragmentTool.substituteFragmentRefsRecursively(fragmentTool.recursiveLimit);
		new CountExpander(fragment).process();
		CMLArg.addIdxArgsWithSerialNumber(fragment, CMLMolecule.TAG);
		this.replaceFragmentsByChildMolecules();
		this.createAtomsRefs2OnJoins();
		fragment.setConvention(FragmentTool.Convention.PML_INTERMEDIATE.v);
	}

	private CMLElement processMarkush(ResourceManager resourceManager, List<Node> markushs) {
		CMLElement generatedElement;
		if (markushs.size() > 1) {
			throw new RuntimeException("Can only process one Markush child");
		}
		CMLFragmentList expandableMarkush = (CMLFragmentList) markushs.get(0);
		generatedElement = generateFragmentListFromMarkushGroupsAndTarget(expandableMarkush, resourceManager);
		fragment.setConvention(FragmentTool.Convention.PML_PROCESSED.v);
		return generatedElement;
	}

	/**
	 * generate fragmentList from fragment. creates M * N * P ... fragments in a
	 * fragmentList
	 * 
	 * @param expandableMarkush
	 * @param resourceManager
	 * @return generated fragmentList
	 */
	private CMLFragmentList generateFragmentListFromMarkushGroupsAndTarget(CMLFragmentList expandableMarkush,
			ResourceManager resourceManager) {
		// fragment
		// fragmentList (refs)
		// fragmentList role='markush'
		// fragmentList role='markushList'
		// fragment role='markushTarget'
		// ...
		// fragment ref=...fragmentList => markushList

		// referenceFragmentList
		List<Node> referenceFragmentLists = CMLUtil.getQueryNodes(fragment, CMLFragmentList.NS
				+ "[not(@role='markush')]", CMLConstants.CML_XPATH);
		if (referenceFragmentLists.size() != 1) {
			throw new RuntimeException("Must have exactly one referenceFragmentList; was: "
					+ referenceFragmentLists.size());
		}

		// should have 1 or more fragmentLists and one fragment
		List<Node> markushListsOrMixtures = CMLUtil.getQueryNodes(expandableMarkush, CMLFragmentList.NS
				+ "[@role='markushList' or @role='markushMixture']", CMLConstants.CML_XPATH);
		List<Node> markushTargets = CMLUtil.getQueryNodes(expandableMarkush,
				CMLFragment.NS + "[@role='markushTarget']", CMLConstants.CML_XPATH);
		// checks
		if (markushListsOrMixtures.size() == 0) {
			throw new RuntimeException("No markushLists or markushMixtures given");
		}
		if (markushTargets.size() != 1) {
			throw new RuntimeException("Must have exactly one markushTarget");
		}
		String role = ((CMLFragmentList) markushListsOrMixtures.get(0)).getRole();
		if (role == null) {
			throw new RuntimeException("must give role attribute on fragmentList");
		}
		CMLFragmentList generatedFragmentList = null;
		if (role.equals("markushList")) {
			generatedFragmentList = multipleMarkush(markushListsOrMixtures, resourceManager);
		} else if (role.equals("markushMixture")) {
			CMLFragment markushTarget = (CMLFragment) markushTargets.get(0);
			CountExpressionAttribute cea = new CountExpressionAttribute(markushTarget.getCountExpressionAttribute());
			if (cea == null) {
				throw new RuntimeException("must have count expression for mixture");
			}
			cea.detach();
			int count = cea.calculateCountExpression();
			generatedFragmentList = markushMixture(markushListsOrMixtures, resourceManager, count);
		} else {
			throw new RuntimeException("Unknown role: " + role);
		}
		return generatedFragmentList;
	}

	private CMLFragmentList multipleMarkush(List<Node> markushLists, ResourceManager resourceManager) {

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
			FragmentTool newFragmentTool = FragmentTool.getOrCreateTool(newFragment);
			newFragmentTool.processAll(resourceManager);
			generatedFragmentList.addFragment(newFragment);
		}
		return generatedFragmentList;
	}

	private CMLFragmentList markushMixture(List<Node> markushLists, ResourceManager resourceManager, int count) {

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
				FragmentTool newFragmentTool = FragmentTool.getOrCreateTool(newFragment);
				newFragmentTool.processAll(resourceManager);
				generatedFragmentList.addFragment(newFragment);
			} else {
				LOG.debug("NULL " + i);
			}
		}
		return generatedFragmentList;
	}

	private CMLFragment getRandomFragment(CMLFragmentList markushList) {
		return getRandomFragment(markushList, null);
	}

	Integer getAmountForFragment(CMLFragment fragment, CMLFragmentList idFragmentList) {
		Integer amount = null;

		// Get a child rateDepend element
		Attribute att = (Attribute) fragment.getAttribute("rateDepend");
		if (att == null) {
			return null;
		}
		String ref = att.getValue();
		// Try to see if amount is present on referenced fragment
		FragmentListTool fragmentListTool = FragmentListTool.getOrCreateTool(idFragmentList);
		CMLFragment refFragment0 = (CMLFragment) fragmentListTool.getFragmentById(ref);
		if (refFragment0 == null) {
			idFragmentList.debug("FAILED DEREF");
			throw new RuntimeException("Cannot find ref: " + ref);
		}
		// Does referenced fragment have an amount?
		Elements elemens = refFragment0.getChildCMLElements("amount");
		if (elemens.size() == 1) {
			amount = Integer.valueOf(elemens.get(0).getValue());
		}
		return amount;
	}

	private void decrementAmmountForFragment(CMLFragment cmlFragment, CMLFragmentList idFragmentList) {
		Integer amount = null;

		// Get a child rateDepend element
		Attribute att = (Attribute) cmlFragment.getAttribute("rateDepend");
		if (att == null) {
			return;
		}
		String ref = att.getValue();
		// Try to see if amount is present on referenced fragment
		FragmentListTool fragmentListTool = FragmentListTool.getOrCreateTool(idFragmentList);
		CMLFragment refFragment0 = (CMLFragment) fragmentListTool.getFragmentById(ref);
		if (refFragment0 == null) {
			idFragmentList.debug("FAILED DEREF");
			throw new RuntimeException("Cannot find ref: " + ref);
		}
		// Does referenced fragment have an amount?
		Elements elemens = refFragment0.getChildCMLElements("amount");
		if (elemens.size() == 1) {
			amount = Integer.valueOf(elemens.get(0).getValue());
			amount--;
			elemens.get(0).removeChildren();
			elemens.get(0).appendChild(amount.toString());
		}
		return;

	}

	CMLFragment getRandomFragment(CMLFragmentList markushList, CMLFragmentList idFragmentList) {
		CMLElements<CMLFragment> fragments = markushList.getFragmentElements();
		double sum = 0.0;
		int nFragments = fragments.size();
		double[] ratios = new double[nFragments];
		Integer[] amounts = new Integer[nFragments];
		Double sumAmounts = null;
		double sumAmountsProb = 0.0;
		int i = 0;
		for (CMLFragment fragment : fragments) {
			CMLScalar scalar = (CMLScalar) fragment.getFirstCMLChild(CMLScalar.TAG);
			if (scalar == null) {
				throw new RuntimeException("must have scalar child");
			}
			if (idFragmentList != null) {
				Integer amount = getAmountForFragment(fragment, idFragmentList);
				amounts[i] = amount;
				if (amount != null) {
					if(sumAmounts==null){
						sumAmounts=0.0;
					}
					sumAmounts += amount;
				}

			}
			// rel.Probability of that fragment being added.
			ratios[i] = scalar.getDouble();
			if (Double.isNaN(ratios[i])) {
				scalar.debug("RANDOM FRAGMENT");
				throw new RuntimeException("scalar must have double value");
			}
			sum += ratios[i];
			if (amounts[i] != null) {
				sumAmountsProb += ratios[i];
			}
			i++;
		}
		// Now modify the probabilities according to the amounts if present
		Double[] amounts_scaled = new Double[nFragments];
		double sumScaled = 0.0;
		for (int n = 0; n < ratios.length; n++) {
			
			if (sumAmounts == null) {
				break;
			}

			if (amounts[n] != null) {
				if (amounts[n] == 0) {
					amounts_scaled[n] = 0.0;
				} else {
					amounts_scaled[n] = amounts[n] * ratios[n] / sumAmounts;
					sumScaled += amounts_scaled[n];
				}
			} else {
				amounts_scaled[n] = null;
			}
		}
		// Then re-scale back to keep the probabilities which don't depend on
		// conc the same
		for (int n = 0; n < ratios.length; n++) {
			if (amounts_scaled[n] == null) {
				continue;
			}
			if (sumScaled == 0.0)
				ratios[n] = 0.0;
			else
				ratios[n] = amounts_scaled[n] * sumAmountsProb / sumScaled;
		}
		//re-sum ratios to avoid error!
		sum=0;
		for(int n=0;n<ratios.length;n++){
			sum+=ratios[n];
		}
		
		Random r = getRandomGenerator();
		// get random fragment
		double rand = r.nextFloat() * sum;
		int j = nFragments - 1;
		for (i = 0; i < nFragments; i++) {
			if (rand < ratios[i]) {
				j = i;
				break;
			} else {
				rand -= ratios[i];
			}
		}
		// lower the count of the used up fragment if applicable
		decrementAmmountForFragment(fragments.get(j), idFragmentList);
		CMLFragment newFragment = new CMLFragment(fragments.get(j));
		newFragment.removeChildren();
		return newFragment;
	}

	private CMLFragment createSingleBasicFragment(List<CMLFragment> markushGroupList) {
		// copy the fragment
		CMLFragment newFragment = new CMLFragment(fragment);
		CMLFragmentList referenceFragmentList = (CMLFragmentList) CMLUtil.getQueryNodes(newFragment,
				CMLFragmentList.NS + "[not(@role='markush')]", CMLConstants.CML_XPATH).get(0);
		CMLFragmentList markush = (CMLFragmentList) CMLUtil.getQueryNodes(newFragment,
				CMLFragmentList.NS + "[@role='markush']", CMLConstants.CML_XPATH).get(0);
		List<Node> markushGroupLists = CMLUtil.getQueryNodes(markush, CMLFragmentList.NS
				+ "[@role='markushList' or @role='markushMixture']", CMLConstants.CML_XPATH);
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
			// markushFragment.removeChildren();
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

	/**
	 * replace fragment/molecule by molecule.
	 */
	private void replaceFragmentsByChildMolecules() {
		List<Node> childMols = CMLUtil.getQueryNodes(fragment, ".//" + CMLFragment.NS + S_SLASH + CMLMolecule.NS,
				CMLConstants.CML_XPATH);
		for (Node node : childMols) {
			CMLFragment parent = (CMLFragment) node.getParent();
			parent.replaceByChildren();
		}
		while (true) {
			childMols = CMLUtil.getQueryNodes(fragment, ".//" + CMLJoin.NS + S_SLASH + CMLFragment.NS + S_SLASH
					+ CMLMolecule.NS, CMLConstants.CML_XPATH);
			if (childMols.size() == 0) {
				break;
			}
			CMLFragment parent = (CMLFragment) childMols.get(0).getParent();
			parent.replaceByChildren();
		}

		int childMolCount = Integer.MAX_VALUE;
		while (true) {
			childMols = CMLUtil.getQueryNodes(fragment, ".//" + CMLFragment.NS + S_SLASH + CMLMolecule.NS,
					CMLConstants.CML_XPATH);
			if (childMolCount > childMols.size()) {
				for (Node node : childMols) {
					CMLElement parent = (CMLElement) node.getParent();
					if (parent instanceof CMLFragment) {
						parent.replaceByChildren();
					}
				}
				childMolCount = childMols.size();
			} else
				break;
		}

	}

	/**
	 * recursive processing.
	 */

	private void createAtomsRefs2OnJoins() {

		List<Node> joins = CMLUtil.getQueryNodes(fragment, ".//" + CMLJoin.NS, CMLConstants.CML_XPATH);
		for (Node node : joins) {
			createAtomRefs2OnJoin((CMLJoin) node);
		}
	}

	private void createAtomRefs2OnJoin(CMLJoin join) {
		String moleculeRefs2[] = join.getMoleculeRefs2();
		if (moleculeRefs2 == null) {
			throw new RuntimeException("Missing moleculeRefs2 on join");
		}
		if (MoleculePointer.PARENT.toString().equals(moleculeRefs2[0])
				&& MoleculePointer.CHILD.toString().equals(moleculeRefs2[1])) {
			joinParentAndChild(join);
		} else if (MoleculePointer.PREVIOUS.toString().equals(moleculeRefs2[0])
				&& MoleculePointer.NEXT.toString().equals(moleculeRefs2[1])) {
			joinPreviousAndNext(join);
		} else {
			throw new RuntimeException("Cannot proces atomRefs2: " + Util.concatenate(moleculeRefs2, S_SPACE));
		}
	}

	private void joinParentAndChild(CMLJoin join) {
		List<Node> nodes = CMLUtil.getQueryNodes(join, "parent::" + CMLMolecule.NS + "[1]", CMLConstants.CML_XPATH);
		if (nodes.size() == 0) {
			throw new RuntimeException("Cannot find parent for join");
		}
		CMLMolecule parentMolecule = (CMLMolecule) nodes.get(0);

		nodes = CMLUtil.getQueryNodes(join, "descendant::" + CMLMolecule.NS + "[1]", CMLConstants.CML_XPATH);
		if (nodes.size() == 0) {
			throw new RuntimeException("Cannot find child for join");
		}
		CMLMolecule childMolecule = (CMLMolecule) nodes.get(0);
		join.processMoleculeRefs2AndAtomRefs2(parentMolecule, childMolecule);
	}

	private void joinPreviousAndNext(CMLJoin join) {
		List<Node> nodes = CMLUtil.getQueryNodes(join, "preceding-sibling::" + CMLMolecule.NS + "[1]",
				CMLConstants.CML_XPATH);
		if (nodes.size() == 0) {
			((CMLElement) join.getParent()).debug("Cannot find join");
			throw new RuntimeException("Cannot find previous for join: " + join.getString());
		}
		CMLMolecule previousMolecule = (CMLMolecule) nodes.get(0);

		nodes = CMLUtil.getQueryNodes(join, "following-sibling::*/descendant-or-self::" + CMLMolecule.NS + "[1]",
				CMLConstants.CML_XPATH);
		if (nodes.size() == 0) {
			throw new RuntimeException("Cannot find next for join");
		}
		CMLMolecule nextMolecule = (CMLMolecule) nodes.get(0);
		join.processMoleculeRefs2AndAtomRefs2(previousMolecule, nextMolecule);
	}

	/**
	 * 
	 * @return random
	 */
	public Random getRandomGenerator() {
		if (randomGenerator == null) {
			randomGenerator = (fragmentTool.getSeed() == 0L) ? new Random() : new Random(fragmentTool.getSeed());
		}
		return randomGenerator;
	}
}

class IntermediateProcessor implements CMLConstants {
	// should consist of molecule (join, molecule)*
	// molecules may have child joins.
	// processing will be recursive
	// process branches first (includes all joining)
	// then join branches to molecule
	// then process chain

	ResourceManager resourceManager;

	CMLFragment fragment;

	/**
	 * constructor.
	 * 
	 * @param fragment
	 */
	public IntermediateProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;

	}

	void process(ResourceManager resourceManager) {
		List<Node> joins = CMLUtil
				.getQueryNodes(fragment, ".//" + CMLJoin.NS + "[not(@order)]", CMLConstants.CML_XPATH);
		for (Node node : joins) {
			((CMLJoin) node).setOrder(CMLBond.SINGLE_S);
		}
		expandTorsionsWithMinMaxValues();
		// lookup referenced molecules; replaces by fragments containing new
		// molecules
		dereferenceMolecules(resourceManager);
		// this is not yet tested
		dereferenceFragments(resourceManager);
		fragment.setConvention(FragmentTool.Convention.PML_EXPLICIT.v);
	}

	private void dereferenceMolecules(ResourceManager resourceManager) {
		List<Node> mols = CMLUtil.getQueryNodes(fragment, ".//" + CMLMolecule.NS + "[@ref]", CMLConstants.CML_XPATH);
		for (Node node : mols) {
			CMLMolecule subMolecule = (CMLMolecule) node;
			CMLMolecule dereferencedMol = (CMLMolecule)
			// dereference(resourceManager, subMolecule,
			// IndexableByIdList.Type.MOLECULE_LIST);
			resourceManager.deref(subMolecule, ResourceManager.IdTypes.ID);
			if (dereferencedMol == null) {
				throw new RuntimeException("Cannot dereference: " + subMolecule.getRef());
			}
			subMolecule.removeAttribute("ref");
			// copy
			FragmentConverter fragmentConverter = new FragmentConverter(dereferencedMol);
			// make a new fragment
			CMLFragment newFragment = fragmentConverter.convertToFragment();
			FragmentTool newFragmentTool = FragmentTool.getOrCreateTool(newFragment);
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

	/**
	 * expand fragments. not yet tested
	 * 
	 * @param resourceManager
	 */
	private void dereferenceFragments(ResourceManager resourceManager) {
		List<Node> mols = CMLUtil.getQueryNodes(fragment, ".//" + CMLFragment.NS + "[@ref]", CMLConstants.CML_XPATH);
		for (Node node : mols) {
			CMLFragment subFragment = (CMLFragment) node;
			CMLFragment newFragment = (CMLFragment)
			// dereference(resourceManager, subFragment,
			// IndexableByIdList.Type.FRAGMENT_LIST);
			resourceManager.deref(subFragment, ResourceManager.IdTypes.ID);
			subFragment.removeAttribute("ref");
			// copy
			FragmentTool newFragmentTool = FragmentTool.getOrCreateTool(newFragment);
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

	/**
	 * expand arguments. calls: CMLArg.substituteParameterName(molecule, name,
	 * value); CMLArg.substituteParentAttributes(molecule);
	 * CMLArg.substituteTextContent(molecule);
	 */
	void substituteParameters() {
		CMLMolecule molecule = FragmentTool.getOrCreateTool(fragment).getMolecule();
		Nodes nodes = molecule.query(CMLArg.NS + "[@name]", CMLConstants.CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			CMLArg arg = (CMLArg) nodes.get(i);
			String name = arg.getName();
			String value = arg.getString();
			CMLArg.substituteParameterName(molecule, name, value);
			arg.detach();
		}
		CMLArg.substituteParentAttributes(molecule);
		CMLArg.substituteTextContent(molecule);
		fragment.setId(FragmentTool.F_PREFIX + molecule.getId());
	}

	/**
	 * returns a copy of a derefenceable element.
	 * 
	 * @param catalog
	 * @param indexable
	 * @param type
	 * @return derefernced ellemnt or null
	 */
	private Indexable dereference(Catalog catalog, CMLFragment fragment, IndexableByIdList.Type type) {
		Indexable deref = null;
		String ref = fragment.getRef();

		if (ref != null) {
			String prefix = CMLUtil.getPrefix(ref);
			if (S_EMPTY.equals(prefix)) {
//				((CMLElement) deref).debug("FT");
				throw new RuntimeException("Cannot dereference empty prefix");
			}
			CMLNamespace namespace = CMLNamespace.createNamespace(prefix, fragment);
			if (namespace != null) {
				IndexableByIdList indexableList = catalog.getIndexableList(namespace, type);
				String localRef = S_EMPTY;
				if (indexableList != null) {
					// indexableList.updateIndex();
					localRef = CMLUtil.getLocalName(ref);
					deref = indexableList.getIndexableById(localRef);
				}
				if (deref == null) {
					((CMLElement) indexableList).debug("cannot dereference: " + ref + S_SLASH + localRef);
				}
			} else {
				((CMLElement) fragment).debug("FAILS TO LOOKUP");
				throw new RuntimeException("Cannot create namespace for indexable lookup;"
						+ " check that data namespaces are in scope");
			}
		} else {
			throw new RuntimeException("Null ref on indexable");
		}
		return (deref == null) ? null : (Indexable) ((CMLElement) deref).copy();
	}

	/**
	 * returns a copy of a derefenceable element.
	 * 
	 * @param catalog
	 * @param molecule
	 * @param type
	 * @return derefernced ellemnt or null
	 */
	private Indexable dereference(Catalog catalog, CMLMolecule molecule, IndexableByIdList.Type type) {
		Indexable deref = null;
		String ref = molecule.getRef();

		if (ref != null) {
			String prefix = CMLUtil.getPrefix(ref);
			if (S_EMPTY.equals(prefix)) {
				((CMLElement) deref).debug("FT");
				throw new RuntimeException("Cannot dereference empty prefix");
			}
			CMLNamespace namespace = CMLNamespace.createNamespace(prefix, molecule);
			if (namespace != null) {
				IndexableByIdList indexableList = catalog.getIndexableList(namespace, type);
				String localRef = S_EMPTY;
				if (indexableList != null) {
					// indexableList.updateIndex();
					localRef = CMLUtil.getLocalName(ref);
					deref = indexableList.getIndexableById(localRef);
				}
				if (deref == null) {
					((CMLElement) indexableList).debug("cannot dereference: " + ref + S_SLASH + localRef);
				}
			} else {
				molecule.debug("FAILS TO LOOKUP");
				throw new RuntimeException("Cannot create namespace for indexable lookup;"
						+ " check that data namespaces are in scope");
			}
		} else {
			throw new RuntimeException("Null ref on molecule");
		}
		return (deref == null) ? null : (Indexable) ((CMLElement) deref).copy();
	}

	private void expandTorsionsWithMinMaxValues() {
		List<Node> torsions = CMLUtil.getQueryNodes(fragment, ".//" + CMLTorsion.NS + "[@min and @max]",
				CMLConstants.CML_XPATH);
		for (Node node : torsions) {
			CMLTorsion torsion = (CMLTorsion) node;
			String countExpression = "range(" + torsion.getMin() + S_COMMA + torsion.getMax() + S_RBRAK;
			int value = new CountExpressionAttribute(countExpression).calculateCountExpression();
			torsion.setXMLContent((double) value);
			torsion.removeAttribute("min");
			torsion.removeAttribute("max");
			// torsion.debug("MINMAX");
		}
	}

}

class ExplicitProcessor implements CMLConstants {
	private static Logger LOG = Logger.getLogger(ExplicitProcessor.class);

	CMLFragment fragment;

	CMLMolecule growingMolecule;

	MoleculeTool growingMoleculeTool;

	boolean takeAtomWithLowestId = true;

	/**
	 * constructor.
	 * 
	 * @param fragment
	 */
	public ExplicitProcessor(CMLFragment fragment) {
		this.fragment = fragment;
	}

	void process() {
		// fragment.debug("RAW EXPLICIT");
		// should consist of molecule (join, molecule)*
		List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(fragment, CMLJoin.NS + X_OR + CMLMolecule.NS,
				CMLConstants.CML_XPATH);
		checkMoleculeAndJoinList(moleculeAndJoinList);
		// fragment.debug(" EXPLICIT 1");
		// immediate children
		growingMolecule = (CMLMolecule) moleculeAndJoinList.get(0);
		growingMoleculeTool = MoleculeTool.getOrCreateTool(growingMolecule);
		moleculeAndJoinList.remove(0);
		processMolecule(growingMolecule, null);
		processMoleculeAndJoin(moleculeAndJoinList);
		// fragment.debug("COMPLETE");
		processProperties();
		fragment.setConvention(FragmentTool.Convention.PML_COMPLETE.v);
	}

	private void checkMoleculeAndJoinList(List<Node> moleculeAndJoinList) {
		if (moleculeAndJoinList.size() % 2 != 1) {
			throw new RuntimeException("must have molecule-(join-molecule)*");
		}
		String[] names = new String[] { CMLMolecule.TAG, CMLJoin.TAG };
		int i = 1;
		int j = 0;
		for (Node node : moleculeAndJoinList) {
			i = 1 - i;
			if (!((CMLElement) node).getLocalName().equals(names[i])) {
				throw new RuntimeException("expected " + names[i] + " in element: " + j);
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
		List<Node> joins = CMLUtil.getQueryNodes(molecule, CMLJoin.NS, CMLConstants.CML_XPATH);
		detachJoins(joins);
		if (join != null) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			CMLAtomSet molAtomSet = moleculeTool.getAtomSet();
			growingMoleculeTool.addMoleculeTo(molecule, takeAtomWithLowestId);
			JoinTool joinTool = JoinTool.getOrCreateTool(join);
			joinTool.addMoleculeTo(growingMolecule, molAtomSet, takeAtomWithLowestId);
		}
		processMoleculeWithJoinChildren(growingMolecule, joins);
	}

	private void processMoleculeWithJoinChildren(CMLMolecule growingMolecule, List<Node> joinList) {
		for (Node node : joinList) {
			CMLJoin join = (CMLJoin) node;
			List<Node> moleculeAndJoinList = CMLUtil.getQueryNodes(join, CMLJoin.NS + X_OR + CMLMolecule.NS,
					CMLConstants.CML_XPATH);
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
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		// do them in this order to avoid interaction
		moleculeTool.adjustLengths();
		moleculeTool.adjustAngles();
		moleculeTool.adjustTorsions();
	}

	private void processProperties() {
		// there is a bug in that propertys are separated from propertyList and
		// the same for scalar
		// <scalar units="units:g.cm-3" dataType="xsd:string">1.23</scalar>
		// <property dictRef="pml:density" title="mass density">
		//
		// </property>
		// <scalar units="units:cm3" dataType="xsd:string">123</scalar>
		// <property dictRef="pml:volume" title="molar volume">
		//
		// </property>
		// <propertyList dictRef="pml:vanKrevelen">
		CMLMolecule molecule = fragment.getMoleculeElements().get(0);
		// group scalar into property
		List<Node> scalarNodes = CMLUtil.getQueryNodes(molecule, CMLScalar.NS, CMLConstants.CML_XPATH);
		List<Node> propertyListNodes = CMLUtil.getQueryNodes(molecule, CMLPropertyList.NS, CMLConstants.CML_XPATH);
		for (Node node : scalarNodes) {
			CMLScalar scalar = (CMLScalar) node;
			List<Node> fsList = CMLUtil.getQueryNodes(scalar, "following-sibling::*", CMLConstants.CML_XPATH);
			if (fsList.size() == 0) {
				throw new RuntimeException("Expected following-sibling");
			}
			if (!(fsList.get(0) instanceof CMLProperty)) {
				throw new RuntimeException("Expected following property sibling");
			}
			CMLProperty property = (CMLProperty) fsList.get(0);
			scalar.detach();
			property.appendChild(scalar);
		}
		for (Node node : propertyListNodes) {
			CMLPropertyList propertyList = (CMLPropertyList) node;
			List<Node> psList = CMLUtil.getQueryNodes(propertyList, "preceding-sibling::" + CMLProperty.NS,
					CMLConstants.CML_XPATH);
			if (psList.size() == 0) {
				LOG.debug("Expected preceding-sibling");
			}
			while (psList.size() > 0) {
				Node node0 = psList.get(0);
				if (node0 instanceof CMLProperty) {
					CMLProperty property = (CMLProperty) node0;
					property.detach();
					propertyList.insertChild(property, 0);
					psList.remove(0);
				}
			}
		}
		//    	
		// <propertyList dictRef="pml:vanKrevelen">
		// <property dictRef="pml:volume" title="molar volume">
		// <scalar units="units:cm3" dataType="xsd:string">
		// 123
		// </scalar>
		// </property>
		// <property dictRef="pml:density" title="mass density">
		// <scalar units="units:g.cm-3" dataType="xsd:string">
		// 1.23
		// </scalar>
		// </property>
		// </propertyList>
		// VERY crude - go through all fragments and add all similar ones:
		// CMLElement parent = fragment;
		CMLElement parent = molecule;
		List<Node> dictRefs = CMLUtil.getQueryNodes(parent,
				CMLPropertyList.NS + S_SLASH + CMLProperty.NS + "/@dictRef", CMLConstants.CML_XPATH);
		Set<String> propertySet = new HashSet<String>();
		for (Node node : dictRefs) {
			propertySet.add(node.getValue());
		}
		List<CMLProperty> newPropertyList = new ArrayList<CMLProperty>();
		for (String propertyS : propertySet) {
			double sum = 0.0;
			int count = 0;
			String role = S_EMPTY;
			String units = S_EMPTY;
			String dictRef = S_EMPTY;
			String title = S_EMPTY;
			List<Node> nodes = CMLUtil.getQueryNodes(parent, CMLPropertyList.NS + S_SLASH + CMLProperty.NS
					+ "[@dictRef='" + propertyS + "']/" + CMLScalar.NS, CMLConstants.CML_XPATH);
			for (Node node : nodes) {
				CMLScalar scalar = (CMLScalar) node;
				units = scalar.getUnits();
				CMLProperty prop = (CMLProperty) scalar.getParent();
				role = prop.getRole();
				if (role != null && units != null) {
					dictRef = prop.getDictRef();
					title = prop.getTitle();
					sum += scalar.getDouble();
				} else {
					LOG.debug("ROLE UNITS " + role + S_SLASH + units);
					sum = Double.NaN;
				}
				count++;
			}
			if (Type.INTENSIVE.value.equals(role) && count > 0) {
				sum /= (double) count;
			} else if (Type.SEMINTENSIVE.value.equals(role) && count > 0) {
				// FIXME replace this later
				sum /= (double) count;
			}

			if (!Double.isNaN(sum))
				sum = (Math.round(sum * 100)) / 100; // round the number to 2dp

			CMLProperty property = new CMLProperty();
			CMLScalar scalar = new CMLScalar();
			property.appendChild(scalar);
			property.setDictRef(dictRef);
			property.setTitle(title);
			if (role != null) {
				property.setRole(role);
			}
			if (units != null) {
				scalar.setUnits(units);
			}
			scalar.setValue(sum);
			newPropertyList.add(property);
		}
		propertyListNodes = CMLUtil.getQueryNodes(parent, CMLPropertyList.NS, CMLConstants.CML_XPATH);
		for (Node node : propertyListNodes) {
			node.detach();
		}
		if (newPropertyList.size() > 0) {
			CMLPropertyList propertyList = new CMLPropertyList();
			for (CMLProperty property : newPropertyList) {
				propertyList.appendChild(property);
			}
			parent.appendChild(propertyList);
		}
	}
}
