package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSpectator;
import org.xmlcml.cml.element.CMLSpectatorList;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.IntSet;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.RealMatrix;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * manages information for matching atoms.
 * 
 * excludeElementTypes is list of elementTypes (CML) to exclude (empty => none)
 * includeElementTypes is list of elementTypes (CML) to include (empty => all)
 * 
 * include takes precedence over exclude useCharge determines whether formal
 * atom charge is used useLabel determines whether atom label is used
 * maximumAtomTreeLevel maximum level to explore trees to atomMatchStrategy
 * MapTool.MATCH_MORGAN, etc. atomSetExpansionStrategy MapTool.MATCH_GEOM, etc.
 */
public class AtomMatcher extends AbstractTool {

	static Logger LOG = Logger.getLogger(AtomMatcher.class);

	/** */
	public enum Strategy {

		/**
		 * geometry is to be matched deprecated in favour of more precise
		 * methods
		 * 
		 */
		MATCH_GEOM("match geometry"),

		/**
		 * distance matrix should be used
		 * 
		 */
		MATCH_DISTANCE_MATRIX("match via distance matrix"),

		/**
		 * minimum total distance should be used
		 * 
		 */
		MATCH_TOTAL_DISTANCE("match via minimum total distance"),

		/**
		 * atomicSymbol indicating that Morgan algorithm is to be used for
		 * matching
		 * 
		 */
		MATCH_MORGAN("match Morgan"),

		/**
		 * existing labels from Morgan algorithm are to be used for matching
		 * 
		 */
		MATCH_MORGAN_LABEL("match Morgan label"),

		/**
		 * atomicSymbol indicating that atomTreeLabelling is to be used for
		 * matching
		 * 
		 */
		MATCH_ATOM_TREE_LABEL("match atomTreeLabel"),

		/**
		 * mapping from overlap of 2D coordinates.
		 * 
		 */
		FROM_2DOVERLAP("from 2D overlap"),

		/**
		 * mapping from overlap of 2D coordinates.
		 * 
		 */
		FROM_UNIQUE_MATCHED_ATOMS("from unique matched atoms"),

		/**
		 * mapping from overlap of 2D coordinates.
		 * 
		 */
		FROM_DISTANCE_MATRIX_OVERLAP("from distance matrix overlap"),

		// mapping
		/** */
		FROM_OVERLAP_LIST("FROM OVERLAL LIST"),
		/** */
		DIFFERENT_SIZES("ATOM SETS OF DIFFERENT SIZES"),
		/** */
		REMAINING2DFIT("REMAINING 2D FIT"),
		/** */
		ALLATOM2DFIT("ALLATOM 2D FIT"),
		/** */
		INCOMPLETE_MAPPING("INCOMPLETE MAPPING");

		String value;

		private Strategy(String s) {
			this.value = s;
		}

		/**
		 * to string.
		 * 
		 * @return string
		 */
		public String toString() {
			return value;
		}
	}

	/**
	 * maximum level for recursion (to avoid infinite regress).
	 */
	public final static int MAX_ATOM_TREE_LEVEL = 10;

	/** */
	public final static String[] DEFAULT_INCLUDE_ELEMENT_TYPES = {};

	/** */
	public final static String[] DEFAULT_EXCLUDE_ELEMENT_TYPES = {};

	/** */
	public final static String[] DEFAULT_INCLUDE_LIGAND_ELEMENT_TYPES = {};

	/** */
	public final static String[] DEFAULT_EXCLUDE_LIGAND_ELEMENT_TYPES = {};

	/** */
	public final static boolean DEFAULT_USE_CHARGE = false;

	/** */
	public final static boolean DEFAULT_USE_LABEL = false;

	/** */
	public final static int DEFAULT_MAXIMUM_ATOM_TREE_LEVEL = MAX_ATOM_TREE_LEVEL;

	/**
	 * user-specified limit to apply to all atoms.
	 * 
	 * if
	 */
	public final static int DEFAULT_ATOM_TREE_LEVEL = -1;

	/** */
	public final static AtomMatcher.Strategy DEFAULT_ATOM_MATCH_STRATEGY = AtomMatcher.Strategy.MATCH_ATOM_TREE_LABEL;

	/** */
	public final static AtomMatcher.Strategy DEFAULT_ATOM_SET_EXPANSION = AtomMatcher.Strategy.MATCH_GEOM;

	protected String[] excludeElementTypes;
	protected String[] includeElementTypes;
	protected String[] excludeLigandElementTypes;
	protected String[] includeLigandElementTypes;
	protected boolean useCharge;
	protected boolean useLabel;

	/**
	 * the maximum level to explore atomTree labelling
	 * 
	 */
	protected int maximumAtomTreeLevel;

	/**
	 * the level to explore all atoms uniformly. if < 0 the explores until
	 * uniquification or maximumAtomTreeLevel reached
	 */
	protected int atomTreeLevel;

	protected AtomMatcher.Strategy atomMatchStrategy;

	protected AtomMatcher.Strategy atomSetExpansionStrategy;

	/**
	 * constructor.
	 */
	public AtomMatcher() {
		init();
	}

	void init() {
		this.includeElementTypes = DEFAULT_INCLUDE_ELEMENT_TYPES;
		this.excludeElementTypes = DEFAULT_EXCLUDE_ELEMENT_TYPES;
		this.includeLigandElementTypes = DEFAULT_INCLUDE_LIGAND_ELEMENT_TYPES;
		this.excludeLigandElementTypes = DEFAULT_EXCLUDE_LIGAND_ELEMENT_TYPES;
		this.useCharge = DEFAULT_USE_CHARGE;
		this.useLabel = DEFAULT_USE_LABEL;
		this.maximumAtomTreeLevel = DEFAULT_MAXIMUM_ATOM_TREE_LEVEL;
		this.atomTreeLevel = DEFAULT_ATOM_TREE_LEVEL;
		this.atomMatchStrategy = DEFAULT_ATOM_MATCH_STRATEGY;
		this.atomSetExpansionStrategy = DEFAULT_ATOM_SET_EXPANSION;
	}

	/**
	 * get the included elementTypes.
	 * 
	 * @return Returns the elementTypes to include.
	 */
	public String[] getIncludeElementTypes() {
		return includeElementTypes;
	}

	/**
	 * set the included elementTypes. resets excludeElement types to empty list
	 * 
	 * @param elementTypes
	 *            to include.
	 */
	public void setIncludeElementTypes(String[] elementTypes) {
		this.includeElementTypes = elementTypes;
		this.excludeElementTypes = new String[] {};
	}

	/**
	 * get the excluded elementTypes.
	 * 
	 * @return Returns the elementTypes to exclude.
	 */
	public String[] getExcludeElementTypes() {
		return excludeElementTypes;
	}

	/**
	 * set the excluded elementTypes. resets includeElement types to empty list
	 * 
	 * @param elementTypes
	 *            to exclude.
	 */
	public void setExcludeElementTypes(String[] elementTypes) {
		this.excludeElementTypes = elementTypes;
		this.includeElementTypes = new String[] {};
	}

	/**
	 * get the included ligand elementTypes.
	 * 
	 * @return Returns the elementTypes to include.
	 */
	public String[] getIncludeLigandElementTypes() {
		return includeLigandElementTypes;
	}

	/**
	 * set the included ligand elementTypes. resets excludeLigandElementTypes to
	 * empty list
	 * 
	 * @param elementTypes
	 *            to include.
	 */
	public void setIncludeLigandElementTypes(String[] elementTypes) {
		this.includeLigandElementTypes = elementTypes;
		this.excludeLigandElementTypes = new String[] {};
	}

	/**
	 * get the excluded ligand elementTypes.
	 * 
	 * @return Returns the elementTypes to exclude.
	 */
	public String[] getExcludeLigandElementTypes() {
		return excludeLigandElementTypes;
	}

	/**
	 * set the excluded ligand elementTypes. set includedLigand element types to
	 * empty list.
	 * 
	 * @param elementTypes
	 *            to exclude.
	 */
	public void setExcludeLigandElementTypes(String[] elementTypes) {
		this.excludeLigandElementTypes = elementTypes;
		this.includeLigandElementTypes = new String[] {};
	}

	/**
	 * @return Returns the atomTreeLevel.
	 */
	public int getAtomTreeLevel() {
		return atomTreeLevel;
	}

	/**
	 * @param atomTreeLevel
	 *            the maximum level to explore trees to
	 */
	public void setAtomTreeLevel(int atomTreeLevel) {
		this.atomTreeLevel = atomTreeLevel;
	}

	/**
	 * @return Returns the maximumAtomTreeLevel.
	 */
	public int getMaximumAtomTreeLevel() {
		return maximumAtomTreeLevel;
	}

	/**
	 * @param maximumAtomTreeLevel
	 *            the maximum level to explore trees to
	 */
	public void setMaximumAtomTreeLevel(int maximumAtomTreeLevel) {
		this.maximumAtomTreeLevel = maximumAtomTreeLevel;
	}

	/**
	 * @return is charge to be used
	 */
	public boolean isUseCharge() {
		return useCharge;
	}

	/**
	 * @param useCharge
	 *            is charge to be used
	 */
	public void setUseCharge(boolean useCharge) {
		this.useCharge = useCharge;
	}

	/**
	 * @return is label to be used
	 */
	public boolean isUseLabel() {
		return useLabel;
	}

	/**
	 * @param useLabel
	 *            is label to be used.
	 */
	public void setUseLabel(boolean useLabel) {
		this.useLabel = useLabel;
	}

	/**
	 * @return Returns the atomMatchStrategy.
	 */
	public AtomMatcher.Strategy getAtomMatchStrategy() {
		return atomMatchStrategy;
	}

	/**
	 * @param strategy
	 *            The atomMatchStrategy to set.
	 */
	public void setAtomMatchStrategy(AtomMatcher.Strategy strategy) {
		this.atomMatchStrategy = strategy;
	}

	/**
	 * @return Returns the atomSetExpansionStrategy.
	 */
	public AtomMatcher.Strategy getAtomSetExpansionStrategy() {
		return atomSetExpansionStrategy;
	}

	/**
	 * @param strategy
	 *            The atomSetExpansionStrategy to set.
	 */
	public void setAtomSetExpansionStrategy(AtomMatcher.Strategy strategy) {
		this.atomSetExpansionStrategy = strategy;
	}

	/**
	 * skip atom. uses previously set include and exclude atom criteria
	 * 
	 * @param atom
	 * @return true
	 */
	public boolean skipAtom(CMLAtom atom) {
		return skipAtom(atom, includeElementTypes, excludeElementTypes);
	}

	/**
	 * skip ligand atom. uses previously set include and exclude atom criteria
	 * 
	 * @param atom
	 * @return true
	 */
	public boolean skipLigandAtom(CMLAtom atom) {
		return skipAtom(atom, includeLigandElementTypes,
				excludeLigandElementTypes);
	}

	private boolean skipAtom(CMLAtom atom, String[] include, String[] exclude) {
		boolean skip = false;
		String elementType = atom.getElementType();
		if (exclude.length > 0) {
			skip = matches(elementType, exclude);
		} else {
			skip = !matches(elementType, include);
		}
		return skip;
	}

	/**
	 * matches if elementType is in allowedTypes or allowedTypes is a single
	 * S_STAR character
	 * 
	 * @param elementType
	 * @param allowedTypes
	 * @return true if allowed
	 */
	private boolean matches(String elementType, String[] allowedTypes) {
		boolean matches = false;
		if (elementType == null) {
		} else if (allowedTypes.length == 0) {
		} else if (allowedTypes.length == 1 && S_STAR.equals(allowedTypes[0])) {
			matches = true;
		} else {
			for (String ss : allowedTypes) {
				if (elementType.equals(ss)) {
					matches = true;
					break;
				}
			}
		}
		return matches;
	}

	public boolean matches(CMLAtom atom1, CMLAtom atom2) {
		return (atom1 != null && atom1.getElementType() != null
				&& atom2 != null && atom1.getElementType().equals(
				atom2.getElementType()));
	}

	/**
	 * to string.
	 * 
	 * @return thr string
	 */
	public String toString() {
		String s = CMLElement.S_EMPTY;
		s += "useCharge: " + useCharge + S_SEMICOLON;
		s += "useLabel: " + useLabel + S_SEMICOLON;
		return s;
	}

	/**
	 * map two atomSets returns ordered list of equivalence classes. if several
	 * atoms in class uses atomSet Map may not be as large as atomSet if there
	 * are equivalence classes does not annotate links
	 * 
	 * currently has two methods, Morgan and UniqueAtomLabels
	 * 
	 * if the atomSets are of different lengths returns null if the Morgan
	 * fails, returns null
	 * 
	 * @param atomSet0
	 * @param atomSet1
	 * @throws RuntimeException
	 *             null atomSets, atom sets different sizes, bad Morgan
	 * @return map or null if molecules of different length.
	 */
	public CMLMap mapAtomSets(CMLAtomSet atomSet0, CMLAtomSet atomSet1)
			throws RuntimeException {
		if (atomSet0 == null) {
			throw new RuntimeException("atomSet is null: " + atomSet0);
		}
		if (atomSet1 == null) {
			throw new RuntimeException("atomSet is null: " + atomSet1);
		}
		CMLMap cmlMap = null;
		CMLLink cmlLink = null;
		cmlMap = new CMLMap();
		cmlMap.setToType(CMLAtom.TAG);
		cmlMap.setFromType(CMLAtom.TAG);
		if (atomSet0.size() != atomSet1.size()) {
			// FIXME may manage this later
			throw new RuntimeException("mapAtomSets wrong sizes:  "
					+ atomSet0.size() + S_SLASH + atomSet1.size());
		}
		String title = this.getAtomMatchStrategy().toString();
		int nMapped = 0;

		if (this.getAtomMatchStrategy().equals(
				AtomMatcher.Strategy.MATCH_MORGAN)) {
			Morgan morgan0 = new Morgan(atomSet0);
			List<Long> morganList0 = morgan0.getMorganList();
			List<CMLAtomSet> atomSetList0 = morgan0.getAtomSetList();
			Morgan morgan1 = new Morgan(atomSet1);
			List<Long> morganList1 = morgan1.getMorganList();
			List<CMLAtomSet> atomSetList1 = morgan1.getAtomSetList();
			if (morganList0.size() != morganList1.size()) {
				;// may be different as atomSets may have different
				// equivalences
			} else {
				for (int i = 0; i < morganList0.size(); i++) {
					if (morganList0.get(i).intValue() != morganList1.get(i)
							.intValue()) {
						throw new RuntimeException(
								"morgan numbers do not match; (Matching non-identical atomSets?)"
										+ " Failed.... " + i);
					}
					CMLAtomSet atomSetx0 = atomSetList0.get(i);
					CMLAtomSet atomSetx1 = atomSetList1.get(i);
					List<CMLAtom> atoms0 = atomSetx0.getAtoms();
					List<CMLAtom> atoms1 = atomSetx1.getAtoms();
					// atom sets are same size so we have an equivalence
					if (atomSetx0.size() == atomSetx1.size()) {
						cmlLink = new CMLLink();
						cmlLink.setTitle(title + S_SPACE + atomSetx0.size());
						// if single atom we have exact match so add as link
						// if annotation is required add it outside
						if (atomSetx0.size() == 1) {
							cmlLink.setFrom(atoms0.get(0).getId());
							cmlLink.setTo(atoms1.get(0).getId());
							nMapped++;
						} else {
							String[] atomSet0S = atomSetx0.getXMLContent();
							String[] atomSet1S = atomSetx1.getXMLContent();
							cmlLink.setFromSet(atomSet0S);
							cmlLink.setToSet(atomSet1S);
						}
						cmlMap.addUniqueLink(cmlLink, CMLMap.Direction.EITHER);
					} else {
						throw new RuntimeException(
								"atom sets wrong size in Morga");
						// mismatched atom sets - match fails
					}
				}
			}

		} else if (this.getAtomMatchStrategy().equals(
				AtomMatcher.Strategy.MATCH_ATOM_TREE_LABEL)) {

			// match by unique extended atom label

			Map<String, Object> uniqueAtomMap0 = this
					.getAtomTreeLabelling(atomSet0);
			Map<String, Object> uniqueAtomMap1 = this
					.getAtomTreeLabelling(atomSet1);
			if (uniqueAtomMap0.size() != 0 || uniqueAtomMap0.size() != 0) {
				Set<String> keySet0 = uniqueAtomMap0.keySet();
				for (Iterator<String> it = keySet0.iterator(); it.hasNext();) {
					String atomTreeString = it.next();
					Object object0 = uniqueAtomMap0.get(atomTreeString);
					Object object1 = uniqueAtomMap1.get(atomTreeString);
					cmlLink = null;
					if (object1 == null) {
						// do nothing
					} else if (object0 instanceof CMLAtom
							&& object1 instanceof CMLAtom) {
						cmlLink = new CMLLink();
						cmlLink.setTitle(title + S_SPACE + atomSet0.size());
						cmlLink.setFrom(((CMLAtom) object0).getId());
						cmlLink.setTo(((CMLAtom) object1).getId());
						nMapped++;
					} else if (object0 instanceof CMLAtomSet
							&& object1 instanceof CMLAtomSet) {
						CMLAtomSet atomSetx0 = (CMLAtomSet) object0;
						CMLAtomSet atomSetx1 = (CMLAtomSet) object1;
						if (atomSetx1.size() == atomSetx0.size()) {
							cmlLink = new CMLLink();
							cmlLink.setTitle(title + S_SPACE + atomSet0.size());
							cmlLink.setFromSet(atomSetx0.getXMLContent());
							cmlLink.setToSet(atomSetx1.getXMLContent());
						} else {
							throw new RuntimeException(
									"Unequal atomSets in AtomTreeMatching");
						}
						nMapped++;
					}
					if (cmlLink != null) {
						cmlMap.addUniqueLink(cmlLink, CMLMap.Direction.NEITHER);
					}
				}
				if (nMapped == 0) {
					cmlMap = null;
				}
			}
		}
		return cmlMap;
	}

	/**
	 * map two molecules. returns ordered list of equivalence classes. Map may
	 * not be as large as molecule if molecules have equivalence classes does
	 * not annotate links
	 * 
	 * @param molecule0
	 * @param molecule1
	 * @throws RuntimeException
	 *             molecule different sizes (maybe fix later?), bad Morgan
	 * @return map or null if molecules of different length.
	 */
	public CMLMap mapMolecules(CMLMolecule molecule0, CMLMolecule molecule1)
			throws RuntimeException {
		CMLMap cmlMap = null;
		if (molecule1 != null) {
			CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule0)
					.getAtomSet();
			CMLAtomSet atomSet1 = MoleculeTool.getOrCreateTool(molecule1)
					.getAtomSet();
			cmlMap = this.mapAtomSets(atomSet, atomSet1);
		}
		return cmlMap;
	}

	/**
	 * matches molecules pairwise from two arrays. Heuristics currently being
	 * worked out. at present only matches molecules of equal size. each match
	 * returns a CMLMap in the matrix cell, else null. matches can be partial.
	 * array may have null elements (this is a way of deleting molecules from
	 * the problem) any match with null element gives a null CMLMap
	 * 
	 * @param molecule0
	 *            first array of molecules
	 * @param molecule1
	 *            other array (could be a different length)
	 * @throws RuntimeException
	 *             problems in molecule atom-atom matching
	 * @return rectangular array of maps (row = molecule0, cols=molecule1)
	 */
	public CMLMap[][] getMoleculeMatch(List<CMLMolecule> molecule0,
			List<CMLMolecule> molecule1) {
		CMLMap[][] mapMatrix = new CMLMap[molecule0.size()][molecule1.size()];

		for (int i = 0; i < molecule0.size(); i++) {
			if (molecule0.get(i) == null) {
				continue;
			}
			for (int j = 0; j < molecule1.size(); j++) {
				if (molecule1.get(j) == null) {
					continue;
				}
				if (molecule0.get(i).getCMLChildCount(CMLAtom.TAG) != molecule1
						.get(j).getCMLChildCount(CMLAtom.TAG)) {
					continue; // molecules different sizes
				}
				CMLMolecule mol0 = molecule0.get(i);
				CMLMolecule mol1 = molecule1.get(j);
				CMLMap mapij = this.mapMolecules(mol0, mol1);
				if (mapij != null) {
					mapij.setToType(CMLAtom.TAG); // needed for split
					mapij.setFromType(CMLAtom.TAG);
					mapij
							.setTitle(AtomMatcher.Strategy.FROM_UNIQUE_MATCHED_ATOMS
									.toString());
					if (false) {
						splitAndProcessAtomSets(mapij, mol0, mol1);
					}
					if (mapij.getChildElements(CMLLink.TAG, CML_NS).size() != mol0
							.getCMLChildCount(CMLAtom.TAG)) {
						// incomplete match; currently no action
					}
				}
				mapMatrix[i][j] = mapij;
			}
		}
		return mapMatrix;
	}

	/**
	 * creates a list of interatomic vectors and sorts them by frequency.
	 * includes all atoms in set (should use excludeElements if required) WILL
	 * PROBABLY CORRUPT THE COORDINATES SO USE A COPY OF YOUR MOLECULES! (copies
	 * of AtomSets will not help this) we might later save and restore
	 * coordinates
	 * 
	 * @param atomSet1
	 * @param atomSet2
	 * @param eps
	 *            below which vectors are assumed identical
	 * @param minCount
	 *            do not add map if fewer equivalences
	 * @return
	 */
	public List<CMLMap> getSortedListOfAtomMaps(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2, double eps, int minCount) {
		List<Vector2> vectorList = makeAllVectors(atomSet1, atomSet2);

		List<CountReal2> cVectorList = getSortedListOfUniqueVectors(eps,
				vectorList);

		List<CMLMap> mapList = getSortedListOfAtomMaps(atomSet1, atomSet2, eps,
				minCount, cVectorList);
		return mapList;
	}

	private List<CMLMap> getSortedListOfAtomMaps(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2, double eps, int minCount,
			List<CountReal2> cVectorList) {
		List<CMLMap> mapList = new ArrayList<CMLMap>();
		for (CountReal2 creal2 : cVectorList) {
			if (creal2.getCount() >= minCount) {
				CMLMap map = new CMLMap();
				atomSet2.translate2D(creal2);
				// map without H
				for (CMLAtom atom1 : atomSet1.getAtoms()) {
					Real2 xy1 = atom1.getXY2();
					if (xy1 != null) {
						for (CMLAtom atom2 : atomSet2.getAtoms()) {
							Real2 xy2 = atom2.getXY2();
							if (xy2 != null) {
								double dist = xy1.getDistance(xy2);
								if (dist < eps) {
									CMLLink link = new CMLLink();
									link.setFrom(atom1.getId());
									link.setTo(atom2.getId());
									map.addLink(link);
								}
							}
						}
					}
				}
				if (map.getLinkElements().size() >= minCount) {
					mapList.add(map);
				}
			}
		}
		return mapList;
	}

	private List<CountReal2> getSortedListOfUniqueVectors(double eps,
			List<Vector2> vectorList) {
		List<CountReal2> cVectorList = new ArrayList<CountReal2>();
		for (Real2 v1 : vectorList) {
			boolean found = false;
			for (CountReal2 cv : cVectorList) {
				Real2 delta = v1.subtract(cv);
				double dd = delta.getLength();
				if (dd < eps) {
					cv.increment();
					found = true;
					break;
				}
			}
			if (!found) {
				CountReal2 cr = new CountReal2(v1);
				cVectorList.add(cr);
			}
		}
		Collections.sort(cVectorList);
		Collections.reverse(cVectorList);
		return cVectorList;
	}

	private List<Vector2> makeAllVectors(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2) {
		List<Vector2> vectorList = new ArrayList<Vector2>();
		for (CMLAtom atom1 : atomSet1.getAtoms()) {
			Real2 xy1 = atom1.getXY2();
			if (xy1 != null) {
				for (CMLAtom atom2 : atomSet2.getAtoms()) {
					Real2 xy2 = atom2.getXY2();
					if (xy2 != null) {
						Vector2 vector = new Vector2(xy1.subtract(xy2));
						vectorList.add(vector);
					}
				}
			}
		}
		return vectorList;
	}

	/**
	 * creates a map by overlapping atoms between this and atomSet.
	 * 
	 * @param atomSet1
	 *            to overlap
	 * @param atomSet2
	 *            target AtomSet to overlap
	 * @return map from this to atomSet
	 */

	// FIXME
	public CMLMap createMapFrom2DOverlap(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2) {
		CMLMap cmlMap = new CMLMap();
		if (atomSet1 == null || atomSet2 == null) {
			throw new RuntimeException("NULL ATOM SET...");
		}
		// save coords, manipulate molecule and then retranslate
		List<Real2> coords1 = atomSet1.getVector2D();
		List<Real2> coords2 = atomSet2.getVector2D();

		List<AtomPair> atom2atomVector = this.overlap2D(atomSet1, atomSet2);
		if (this.getAtomMatchStrategy().equals(Strategy.MATCH_DISTANCE_MATRIX)) {
			cmlMap = this.overlap2Dnew(atomSet1, atomSet2);
			// old approach
		} else if (this.getAtomMatchStrategy().equals(
				Strategy.MATCH_TOTAL_DISTANCE)
				|| this.getAtomMatchStrategy().equals(Strategy.MATCH_GEOM)) {
			LOG.info("A2A vector " + atom2atomVector.size());
			for (int i = 0; i < atom2atomVector.size(); i++) {
				AtomPair atomPair = atom2atomVector.get(i);
				CMLAtom prodAtom = atomPair.getAtom1();
				CMLAtom reactAtom = atomPair.getAtom2();
				CMLLink link = new CMLLink();
				link.setTitle("from 2D overlap x");
				link.setFrom(prodAtom.getId());
				link.setTo(reactAtom.getId());
				link.setTitle(Strategy.FROM_2DOVERLAP.value);
				cmlMap.addUniqueLink(link, Direction.EITHER);
			}
		} else {
			throw new RuntimeException(
					"MUST give geometrical atomMatchStrategy ("
							+ this.getAtomMatchStrategy() + ")");
		}

		atomSet1.setVector2D(coords1);
		atomSet2.setVector2D(coords2);
		return cmlMap;
	}

	/**
	 * create map on basis of inter atom distances. creates rectangular distance
	 * matrix and iterates by finding the smallest distance, recording it as a
	 * link and deleting row and column. if distances are exactly equal the
	 * result may be arbitary. fromRef is this, toRef is atomSet2
	 * 
	 * @param atomSet
	 *            to overlap
	 * @param atomSet2
	 *            need not be of same size
	 * @return map of as many links as the smallest atomSet
	 * @throws RuntimeException
	 */
	private CMLMap overlap2Dnew(CMLAtomSet atomSet, CMLAtomSet atomSet2) {
		CMLMap cmlMap = new CMLMap();
		RealMatrix distMatrix = atomSet.getDistanceMatrix(atomSet2);
		if (distMatrix == null) {
			return cmlMap;
		}
		int rows = distMatrix.getRows();
		int cols = distMatrix.getCols();
		List<CMLAtom> rowAtoms = atomSet.getAtoms();
		List<CMLAtom> colAtoms = atomSet2.getAtoms();
		List<String> rowIdList = new ArrayList<String>();
		List<String> colIdList = new ArrayList<String>();
		for (int i = 0; i < rowAtoms.size(); i++) {
			String id = rowAtoms.get(i).getId();
			rowIdList.add(id);
		}
		for (int i = 0; i < colAtoms.size(); i++) {
			String id = colAtoms.get(i).getId();
			colIdList.add(id);
		}
		cmlMap.setToType(CMLAtom.TAG);
		cmlMap.setFromType(CMLAtom.TAG);
		while (rows > 0 && cols > 0) {
			Int2 ij = distMatrix.indexOfSmallestElement();
			int irow = ij.getX();
			int jcol = ij.getY();
			CMLLink cmlLink = new CMLLink();
			String rowId = rowIdList.get(irow);
			String colId = colIdList.get(jcol);
			cmlLink.setFrom(rowId);
			cmlLink.setTo(colId);
			cmlLink.setTitle(AtomMatcher.Strategy.FROM_DISTANCE_MATRIX_OVERLAP
					.toString());
			cmlMap.addUniqueLink(cmlLink, CMLMap.Direction.NEITHER);
			rowIdList.remove(irow);
			colIdList.remove(jcol);
			distMatrix.deleteRow(irow);
			distMatrix.deleteColumn(jcol);
			rows--;
			cols--;
		}
		return cmlMap;
	}

	/**
	 * gets mapping between atoms in this and target. generates a list of unique
	 * atom labels (this.getAtomTreeLabelling) for each molecule and maps
	 * through them. if several atoms have the same label the link includes an
	 * atomSet instead of atom
	 * 
	 * @param atomSet
	 * @param targetAtomSet
	 *            to match
	 * @return the map
	 * @throws RuntimeException
	 */
	public CMLMap getUniqueMatchedAtoms(CMLAtomSet atomSet,
			CMLAtomSet targetAtomSet) {
		// note unique atoms
		Map<String, Object> uniqueAtomMap0 = this.getAtomTreeLabelling(atomSet);
		Map<String, Object> uniqueAtomMap1 = this
				.getAtomTreeLabelling(targetAtomSet);
		CMLMap cmlMap = new CMLMap();
		cmlMap.setToType(CMLAtom.TAG);
		cmlMap.setFromType(CMLAtom.TAG);
		// reverse mapping - key on atoms
		Set<String> keySet0 = uniqueAtomMap0.keySet();
		for (Iterator<String> it = keySet0.iterator(); it.hasNext();) {
			String atomTreeString = it.next();
			CMLAtomSet atomSet0 = (CMLAtomSet) uniqueAtomMap0
					.get(atomTreeString);
			CMLAtomSet atomSet1 = (CMLAtomSet) uniqueAtomMap1
					.get(atomTreeString);
			if (atomSet1.size() == atomSet0.size()) {
				CMLLink cmlLink = new CMLLink();
				if (atomSet1.size() == 1) {
					cmlLink.setFrom(atomSet0.getXMLContent()[0]);
					cmlLink.setTo(atomSet1.getXMLContent()[0]);
				} else {
					cmlLink.setFromSet(atomSet0.getXMLContent());
					cmlLink.setToSet(atomSet1.getXMLContent());
				}
			}
		}

		// FIXME
		/*
		 * -- this is broken
		 * 
		 * // try to map rest of atoms by elementType boolean change = true;
		 * while (change) { AtomPair pair =
		 * getUniqueMatchedAtoms(reverseAtomMap1, uniqueAtomMap2,
		 * atomMatcher.excludeElementTypes); if (pair == null) { change = false;
		 * } else { CMLAtom fromAtom = pair.getAtom1(); CMLAtom toAtom =
		 * pair.getAtom2(); // remove mappings reverseAtomMap1.remove(fromAtom);
		 * uniqueAtomMap2.remove(uniqueAtomMap2.get(toAtom)); try {
		 * this.removeAtom(fromAtom); } catch (RuntimeException e) { <<<<<<<
		 * .working LOG.error("bugA " + e + fromAtom.getId()); =======
		 * logger.error("bugA " + e + fromAtom.getId()); >>>>>>>
		 * .merge-right.r915 } try { targetAtomSetTool.removeAtom(toAtom); }
		 * catch (RuntimeException e) { <<<<<<< .working LOG.error("bug " + e);
		 * ======= logger.error("bug " + e); >>>>>>> .merge-right.r915 } CMLLink
		 * link = new CMLLink(); link.setFrom(fromAtom.getId());
		 * link.setTo(toAtom.getId()); cmlMap.appendChild(link); } } --
		 */
		return cmlMap;
	}

	/**
	 * finds unique pair but does not remove them from atomSets. uses AtomTree
	 * to distinguish elements at present uses charge and labels but not
	 * hydrogens can be called iteratively until returns null might be useful
	 * later
	 * 
	 * @param reverseAtomMap1
	 * @param uniqueAtomMap2
	 * @param excludeElements
	 * @return atompair
	 */
	// private AtomPair getUniqueMatchedAtoms(Map reverseAtomMap1, Map
	// uniqueAtomMap2, String[] excludeElements) {
	// AtomPair pair = null;
	//
	// // iterate through labelling and try to find first unique equivalence
	// Set keySet1 = reverseAtomMap1.keySet();
	// Iterator it = keySet1.iterator();
	// while (it.hasNext()) {
	// CMLAtom atom1 = (CMLAtom) it.next();
	// String elementType = atom1.getElementType();
	// boolean omit = false;
	// for (String e : excludeElements) {
	// if (e.equals(elementType)) {
	// omit = true;
	// }
	// }
	// if (!omit) {
	// String atomTreeString = (String) reverseAtomMap1.get(atom1);
	// CMLAtom atom2 = (CMLAtom) uniqueAtomMap2.get(atomTreeString);
	// if (atom2 != null) {
	// pair = new AtomPair(atom1, atom2);
	// break;
	// }
	// }
	// }
	// return pair;
	// }
	/**
	 * get map of atoms with same neightbours. can be used recursively
	 * 
	 * @param atomSet00
	 * @param atomSet
	 *            target
	 * @param currentMap
	 * @return map or null if no new atoms
	 * @throws RuntimeException
	 */
	// FIXME
	public CMLMap getAtomsWithSameMappedNeighbours(CMLAtomSet atomSet00,
			CMLAtomSet atomSet, CMLMap currentMap) {
		// clone tools so as to avoid corruption
		CMLAtomSet this0 = new CMLAtomSet(atomSet00);
		CMLAtomSet atomSet0 = new CMLAtomSet(atomSet);
		CMLMap map = null;
		// try to map rest of atoms by neighbours
		while (true) {
			AtomPair pair = this.getAtomsWithSameMappedNeighbours00(this0,
					atomSet0, currentMap);
			// AtomPair pair = this.getAtomsWithSameMappedNeighbours(this0,
			// atomSet0, currentMap);
			LOG.info("PAIR " + pair);
			if (pair == null) {
				break;
			}
			CMLAtom fromAtom = pair.getAtom1();
			CMLAtom toAtom = pair.getAtom2();
			CMLLink link = new CMLLink();
			link.setFrom(fromAtom.getId());
			link.setTo(toAtom.getId());
			link.setTitle("sameMappedNeighbours");
			if (map == null) {
				map = new CMLMap();
			}
			map.addLink(link);
		}
		return map;
	}

	AtomPair getAtomsWithSameMappedNeighbours00(CMLAtomSet atomSet00,
			CMLAtomSet atomSet, CMLMap currentMap) {
		// FIXME
		if (true)
			throw new RuntimeException("NYI");
		return new AtomPair(new CMLAtom(), new CMLAtom());
	}

	/**
	 * map geometrical neighbours. needs further refactoring
	 * 
	 * @param atomSet
	 * @param atomSet2
	 * @return map
	 */
	public CMLMap mapGeometricalNeighbours(CMLAtomSet atomSet,
			CMLAtomSet atomSet2) {
		CMLMap cmlMap = new CMLMap();
		if (atomSet.size() != atomSet2.size()) {
			LOG.info(WARNING_S + S_NL + AtomMatcher.Strategy.DIFFERENT_SIZES
					+ S_NL + WARNING_S);
			cmlMap.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_INCOMPLETE);
		} else {
			cmlMap.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE);
			LOG.info(BANNER_S + S_NL
					+ CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE + S_NL
					+ BANNER_S);
		}
		int nAtoms1 = atomSet.size();
		int nAtoms2 = atomSet2.size();
		LOG.debug("==========" + nAtoms1 + "===" + nAtoms2
				+ "=============");
		double[][] distanceMatrix = new double[nAtoms1][nAtoms2];
		String atom1Id[] = new String[nAtoms1];
		String atom2Id[] = new String[nAtoms2];
		for (int j = 0; j < nAtoms2; j++) {
			CMLAtom atom2 = (CMLAtom) atomSet2.getAtoms().get(j);
			atom2Id[j] = atom2.getId();
			// Util.print(" "+atom2Id[j]+S_LBRAK+atom2.getElementType()+
			// S_RBRAK);
		}
		// LOG.debug();
		for (int i = 0; i < nAtoms1; i++) {
			CMLAtom atom1 = (CMLAtom) atomSet.getAtoms().get(i);
			atom1Id[i] = atom1.getId();
			// LOG.info(S_SPACE + atom1Id[i] + S_LBRAK + atom1.getElementType()
			// + S_RBRAK);
			Real2 atom1Coord = new Real2(atom1.getX2(), atom1.getY2());
			for (int j = 0; j < nAtoms2; j++) {
				CMLAtom atom2 = (CMLAtom) atomSet2.getAtoms().get(j);
				Real2 atom2Coord = new Real2(atom2.getX2(), atom2.getY2());
				distanceMatrix[i][j] = (!atom1.getElementType().equals(
						atom2.getElementType())) ? CMLAtomSet.MAX_DIST
						: atom1Coord.getDistance(atom2Coord);
				// LOG.info(("      " + (int) (10 * distanceMatrix[i][j])));
			}
			// LOG.info(S_EMPTY);
		}
		// crude
		if (nAtoms1 == 2) {
			map2(cmlMap, distanceMatrix, atom1Id, atom2Id);
		} else if (nAtoms1 == 3) {
			map3(cmlMap, distanceMatrix, atom1Id, atom2Id);
		}
		return cmlMap;
	}

	private void map2(CMLMap cmlMap, double[][] distanceMatrix,
			String[] atom1Id, String[] atom2Id) {
		double[] dist = new double[2];
		dist[0] = distanceMatrix[0][0] + distanceMatrix[1][1];
		dist[1] = distanceMatrix[1][0] + distanceMatrix[0][1];
		int ii = 0;
		int jj = 1;
		if (dist[0] > dist[1]) {
			ii = 1;
			jj = 0;
		}
		CMLLink link = new CMLLink();
		link.setTitle("Geom neighbours 1");
		link.setFrom(atom1Id[0]);
		link.setTo(atom2Id[ii]);
		link.setRole(AtomMatcher.Strategy.REMAINING2DFIT.toString());
		cmlMap.addUniqueLink(link, CMLMap.Direction.EITHER);
		link.setTitle("Geom neighbours 2");
		link = new CMLLink();
		link.setFrom(atom1Id[1]);
		link.setTo(atom2Id[jj]);
		link.setRole(AtomMatcher.Strategy.REMAINING2DFIT.toString());
		cmlMap.addUniqueLink(link, CMLMap.Direction.EITHER);
	}

	private void map3(CMLMap cmlMap, double[][] distanceMatrix,
			String[] atom1Id, String[] atom2Id) {
		double mindist = 9999999.;
		int ii = -1;
		int jj = -1;
		int kk = -1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; (j < 3); j++) {
				if (j != i) {
					int k = 3 - i - j;
					double dist = distanceMatrix[i][0] + distanceMatrix[j][1]
							+ distanceMatrix[k][2];
					if (dist < mindist) {
						ii = i;
						jj = j;
						kk = k;
						mindist = dist;
					}
				}
			}
		}
		CMLLink link = new CMLLink();
		link.setTitle("Geom neighbours 3");
		link.setFrom(atom1Id[ii]);
		link.setTo(atom2Id[0]);
		link.setRole(AtomMatcher.Strategy.REMAINING2DFIT.toString());
		cmlMap.addUniqueLink(link, CMLMap.Direction.EITHER);
		link = new CMLLink();
		link.setTitle("Geom neighbours 4");
		link.setFrom(atom1Id[jj]);
		link.setTo(atom2Id[1]);
		link.setRole(AtomMatcher.Strategy.REMAINING2DFIT.toString());
		cmlMap.addUniqueLink(link, CMLMap.Direction.EITHER);
		link = new CMLLink();
		link.setTitle("Geom neighbours 5");
		link.setFrom(atom1Id[kk]);
		link.setTo(atom2Id[2]);
		link.setRole(AtomMatcher.Strategy.REMAINING2DFIT.toString());
		cmlMap.addUniqueLink(link, CMLMap.Direction.EITHER);
	}

	/**
	 * split links containing sets of atoms into individual links. iterates
	 * through all links containing toSet and fromSet for each generates to and
	 * from atomSets. Uses atomMatchStrategy to compare and link them. if
	 * successful removes original link and adds atomSet.size() new ones some
	 * strategies may abort if sets are unequal. Currently no warning if
	 * geometrical manipulation, saves coordinates and then reapplies
	 * 
	 * @param map
	 *            context for from links
	 * @param fromAtomSet
	 *            atomSet context for from links
	 * @param toAtomSet
	 *            atomSet context for to links
	 */
	public void splitAndProcessAtomSets(CMLMap map, CMLAtomSet fromAtomSet,
			CMLAtomSet toAtomSet) {

		// iterate through all links which contain toSet and fromSet

		List<CMLLink> atomSetLinks = map.getElementLinks(CMLAtom.TAG);
		for (CMLLink atomSetLink : atomSetLinks) {

			// linkTool.splitAndProcessAtomSet(fromMolecule, toMolecule,
			// atomMatchStrategy); // need to develop linkTool
			String fromSet = Util
					.concatenate(atomSetLink.getFromSet(), S_SPACE);
			String toSet = Util.concatenate(atomSetLink.getToSet(), S_SPACE);
			AtomMatcher.Strategy strategy = this.getAtomSetExpansionStrategy();
			if (strategy.equals(AtomMatcher.Strategy.MATCH_GEOM)
					|| strategy
							.equals(AtomMatcher.Strategy.MATCH_DISTANCE_MATRIX)
					|| strategy
							.equals(AtomMatcher.Strategy.MATCH_TOTAL_DISTANCE)) {

				// match geometry - will always come up with a result, even if
				// scientifically unlikely
				List<String> toStrings = new ArrayList<String>();
				for (String s : toSet.split(S_WHITEREGEX)) {
					toStrings.add(s);
				}
				CMLAtomSet toSubAtomSet = toAtomSet.getAtomSetById(toStrings);
				List<String> fromStrings = new ArrayList<String>();
				for (String s : fromSet.split(S_WHITEREGEX)) {
					fromStrings.add(s);
				}
				CMLAtomSet fromSubAtomSet = fromAtomSet
						.getAtomSetById(fromStrings);
				if (fromSubAtomSet == null || fromSubAtomSet == null) {
				}
				if (toSubAtomSet == null || toSubAtomSet == null) {
				}
				// save coords, map atoms and restore coords
				List<Real2> toCoords = toSubAtomSet.getVector2D();
				List<Real2> fromCoords = fromSubAtomSet.getVector2D();
				AtomMatcher matcher2d = new AtomMatcher();
				// matcher2d.setAtomMatchStrategy(atomMatcher.
				// getAtomSetExpansionStrategy());
				// FIXME
				matcher2d
						.setAtomMatchStrategy(AtomMatcher.Strategy.MATCH_TOTAL_DISTANCE);
				CMLMap overlapMap = matcher2d.createMapFrom2DOverlap(
						toSubAtomSet, fromSubAtomSet);
				// LOG.info("...MATCH_GEOM");
				toSubAtomSet.setVector2D(toCoords);
				fromSubAtomSet.setVector2D(fromCoords);

				map.removeLink(atomSetLink); // remove original link
				map.mergeMap(overlapMap, Direction.NEITHER); // merge maps,
				// without
				// overwriting
			} else {
				LOG.error("*** No atomSet expansion strategy");
			}
		}
	}

	/**
	 * simple overlap of 2D atomSets.
	 * 
	 * very crude at present will ALTER coords of atomSet2 (should save and
	 * restore if required)
	 * 
	 * @param atomSet1
	 * @param atomSet2
	 *            atomSet to overlap
	 * @return Vector of AtomPairs of matching thisAtom, otherAtom
	 */
	public List<AtomPair> overlap2D(CMLAtomSet atomSet1, CMLAtomSet atomSet2) {

		List<Real2> p2Vector = atomSet1.getVector2D();

		List<AtomPair> atomPairVector = new ArrayList<AtomPair>();
		Map<String, Long> deltaTable = new HashMap<String, Long>();
		Map<String, CMLAtomSet> atomSetTable = new HashMap<String, CMLAtomSet>();
		Map<String, CMLAtomSet> otherAtomSetTable = new HashMap<String, CMLAtomSet>();
		Map<CMLAtomSet, CMLAtomSet> otherAtomSetTable1 = new HashMap<CMLAtomSet, CMLAtomSet>();
		// move atoms to overlap centroids
		Real2 centroid2D = atomSet1.getCentroid2D();
		Real2 atomSet2Centroid2D = atomSet2.getCentroid2D();
		if (centroid2D == null || atomSet2Centroid2D == null) {
			return atomPairVector;
		}
		Real2 delta2D = centroid2D.subtract(atomSet2Centroid2D);
		atomSet2.translate2D(delta2D);
		atomSet2Centroid2D = atomSet2.getCentroid2D();
		// can use this to save the coords
		List<Real2> atomSet2Vector = atomSet2.getVector2D();
		CMLAtomSet atomSet = null;
		CMLAtomSet otherAtomSet = null;
		// index on displacement vectors between atoms (to one decimal place)
		// The is some bug in indexing the atomSetTable... have kludged it with
		// atomSetTable1
		List<CMLAtom> atoms = atomSet1.getAtoms();
		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom thisAtom = atoms.get(i);
			Real2 thisCoords2 = p2Vector.get(i);
			int nearestIndex = Real2.getSerialOfNearestPoint(atomSet2Vector,
					thisCoords2);
			if (nearestIndex < 0) {
				continue;
			}
			Real2 otherCoords2 = atomSet2Vector.get(nearestIndex);
			CMLAtom otherAtom = (CMLAtom) atomSet2.getAtoms().get(nearestIndex);
			Real2 delta = thisCoords2.subtract(otherCoords2);
			// index on most common vector (crude)
			String iii = S_EMPTY + intDelta(delta);
			// save count of frequency in table
			Long count = deltaTable.get(iii);
			count = (count == null) ? new Long(1) : new Long(
					count.intValue() + 1);
			deltaTable.put(iii, count);
			// increment atomSet for this delta
			atomSet = atomSetTable.get(iii);
			if (atomSet == null) {
				atomSet = new CMLAtomSet();
				atomSetTable.put(iii, atomSet);
			}
			try {
				atomSet.addAtom(thisAtom);
			} catch (Exception e) {
				e.printStackTrace();
			}
			otherAtomSet = otherAtomSetTable.get(iii);
			if (otherAtomSet == null) {
				otherAtomSet = new CMLAtomSet();
				otherAtomSetTable.put(iii, otherAtomSet);
			}
			try {
				otherAtomSet.addAtom(otherAtom);
			} catch (Exception e) {
				e.printStackTrace();
			}
			otherAtomSetTable1.put(atomSet, otherAtomSet);
		}

		// find the most frequent displacement vector
		// Enumeration<String> deltas = deltaTable.keySet();
		String theIntDelta = null;
		int theCount = 0;
		atomSet = null;
		for (String delta : deltaTable.keySet()) {
			int count = deltaTable.get(delta).intValue();
			CMLAtomSet atomSetx = atomSetTable.get(delta);
			if (count > theCount) {
				theCount = count;
				theIntDelta = delta;
				atomSet = atomSetx;
			}
		}

		if (atomSet != null && otherAtomSetTable1 != null) {
			otherAtomSet = otherAtomSetTable1.get(atomSet);
			Real2 theDelta = deltaInt(theIntDelta);
			// move to zero overlap of maximum atoms
			atomSet2.translate2D(theDelta);
			List<CMLAtom> atomsx = atomSet.getAtoms();
			List<CMLAtom> otherAtoms = otherAtomSet.getAtoms();
			if (atomsx.size() != otherAtoms.size()) {
				// LOG.info("warning: AtomSets are not the same size in
				// overlap2d");
			}
			for (int i = 0; i < atomsx.size(); i++) {
				CMLAtom thisAtom = atomsx.get(i);
				Real2 thisCoords2 = new Real2(thisAtom.getX2(), thisAtom
						.getY2());
				int nearestIndex = Real2.getSerialOfNearestPoint(
						atomSet2Vector, thisCoords2);
				if (nearestIndex < 0) {
					continue;
				}
				CMLAtom otherAtom = (CMLAtom) atomSet2.getAtoms().get(
						nearestIndex);
				atomPairVector.add(new AtomPair(thisAtom, otherAtom));
			}
		}
		return atomPairVector;
	}

	/**
	 * finds best mapping of atoms in equal-atom atomSets using geometrical
	 * criteria. for two atoms of equal size matches all permutations of atoms
	 * to find lowest RMS difference. Assumes some degree of pre-alignment by
	 * other means NO alterations of coordinates or optimisation of location or
	 * orientation. does NOT check atom/element types or other properties.
	 * 
	 * @param atomSet1
	 * @param atomSet2
	 * @return best map of matches (or null if not complete)
	 */
	public CMLMap matchAtomsByCoordinates(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2, Transform3 transform3) {
		CMLMap map = null;
		if (atomSet1 == null) {
			throw new RuntimeException("atomSet1 is null");
		}
		if (atomSet1 == null) {
			throw new RuntimeException("atomSet2 is null");
		}
		if (atomSet1.size() != atomSet2.size()) {
			throw new RuntimeException("atomSet1 (" + atomSet1.size()
					+ ") and atomSet2 (" + atomSet2.size()
					+ ") are different sizes");
		}
		if (atomSet1.size() != 0) {
			List<CMLAtom> atoms1 = atomSet1.getAtoms();
			String[] atomIds2 = atomSet2.getAtomIDs();
			Point3Vector p3v1 = atomSet1
					.getCoordinates3(CoordinateType.CARTESIAN);
			if (p3v1 == null) {
				throw new RuntimeException(
						"Cannot find Coordinates for all atoms in atomSet1");
			}
			Point3Vector p3v2 = atomSet2
					.getCoordinates3(CoordinateType.CARTESIAN);
			if (p3v2 == null) {
				throw new RuntimeException(
						"Cannot find Coordinates for all atoms in atomSet2");
			}
			if (transform3 != null) {
				p3v1.transform(transform3);
			}
			int[] serials = findBestFit(atomSet2, atomIds2, p3v1, IntSet
					.getPermutations(atomSet1.size()));
			map = createLinksInMap(atoms1, atomIds2, serials);
		}
		return map;
	}

	private int[] findBestFit(CMLAtomSet atomSet2, String[] atomIds2,
			Point3Vector p3v1, List<int[]> permutations) {
		int[] serials = null;
		double rmsmin = Double.MAX_VALUE;
		for (int[] permutation : permutations) {
			double rms = rms(permutation, p3v1, atomSet2, atomIds2);
			if (rms < rmsmin) {
				rmsmin = rms;
				serials = permutation;
			}
		}
		return serials;
	}

	private CMLMap createLinksInMap(List<CMLAtom> atoms1, String[] atomIds2,
			int[] serials) {
		CMLMap map;
		CMLMap map1 = new CMLMap();
		for (int i = 0; i < serials.length; i++) {
			CMLLink link = new CMLLink();
			link.setFrom(atoms1.get(i).getId());
			link.setTo(atomIds2[serials[i] - 1]);
			map1.addLink(link);
		}
		map = map1;
		return map;
	}

	@SuppressWarnings("unused")
	private CMLMap matchAtomsByCoordinates2(CMLAtomSet atomSet1,
			CMLAtomSet atomSet2) {
		CMLMap map = null;
		return map;
	}

	private double rms(int[] permutation, Point3Vector p3v1,
			CMLAtomSet atomSet2, String[] atomIds2) {
		double rms = 0.0;
		for (int i = 0; i < p3v1.size(); i++) {
			CMLAtom atom2 = atomSet2.getAtomById(atomIds2[i]);
			double dist = p3v1.elementAt(i).getSquaredDistanceFromPoint(
					atom2.getXYZ3());
			rms += dist;
		}
		return rms;
	}

	/**
	 * split links containing sets of atoms into individual links. iterates
	 * through all links containing toSet and fromSet for each generates to and
	 * from atomSets. Uses atomMatchStrategy to compare and link them. if
	 * successful removes original link and adds atomSet.size() new ones some
	 * strategies may abort if sets are unequal. Currently no warning
	 * 
	 * @param map
	 *            with links
	 * @param fromMolecule
	 *            molecule context for from links
	 * @param toMolecule
	 *            molecule context for to links
	 */
	public void splitAndProcessAtomSets(CMLMap map, CMLMolecule fromMolecule,
			CMLMolecule toMolecule) {
		this.splitAndProcessAtomSets(map, MoleculeTool.getOrCreateTool(
				fromMolecule).getAtomSet(), MoleculeTool.getOrCreateTool(
				toMolecule).getAtomSet());
	}

	/**
	 * simple overlap of 2D molecules.
	 * 
	 * very crude at present will ALTER coords of mol2
	 * 
	 * @param mol1
	 * @param mol2
	 *            molecule to overlap
	 * @return Vector of AtomPairs of matching thisAtom, otherAtom
	 */
	public List<AtomPair> overlap2D(CMLMolecule mol1, CMLMolecule mol2) {

		List<AtomPair> atomPairVector = new ArrayList<AtomPair>();
		Map<String, Long> deltaTable = new HashMap<String, Long>();
		Map<String, CMLAtomSet> atomSetTable = new HashMap<String, CMLAtomSet>();
		Map<String, CMLAtomSet> otherAtomSetTable = new HashMap<String, CMLAtomSet>();
		Map<CMLAtomSet, CMLAtomSet> otherAtomSetTable1 = new HashMap<CMLAtomSet, CMLAtomSet>();
		// move atoms to overlap centroids
		Real2 centroid2D = mol1.calculateCentroid2D();
		Real2 mol2Centroid2D = mol2.calculateCentroid2D();
		if (centroid2D == null || mol2Centroid2D == null) {
			return atomPairVector;
		}
		Real2 delta2D = centroid2D.subtract(mol2Centroid2D);
		mol2.translate2D(delta2D);
		mol2Centroid2D = mol2.calculateCentroid2D();
		Real2Vector p2Vector = mol1.getCoordinates2D();
		Real2Vector mol2Vector = mol2.getCoordinates2D();
		CMLAtomSet atomSet = null;
		CMLAtomSet otherAtomSet = null;
		// index on displacement vectors between atoms (to one decimal place)
		// The is some bug in indexing the atomSetTable... have kludged it with
		// atomSetTable1
		List<CMLAtom> atoms = mol1.getAtoms();
		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom thisAtom = atoms.get(i);
			Real2 thisCoords2 = (Real2) p2Vector.get(i);
			int nearestIndex = mol2Vector.getSerialOfNearestPoint(thisCoords2);
			if (nearestIndex < 0) {
				continue;
			}
			Real2 otherCoords2 = (Real2) mol2Vector.get(nearestIndex);
			CMLAtom otherAtom = (CMLAtom) (mol2.getAtoms()).get(nearestIndex);
			Real2 delta = thisCoords2.subtract(otherCoords2);
			// index on most common vector (crude)
			String iii = S_EMPTY + intDelta(delta);
			Long count = deltaTable.get(iii);
			count = (count == null) ? new Long(1) : new Long(
					count.intValue() + 1);
			deltaTable.put(iii, count);
			atomSet = atomSetTable.get(iii);
			if (atomSet == null) {
				atomSet = new CMLAtomSet();
				atomSetTable.put(iii, atomSet);
			}
			try {
				atomSet.addAtom(thisAtom);
			} catch (Exception e) {
				e.printStackTrace();
			}
			otherAtomSet = otherAtomSetTable.get(iii);
			if (otherAtomSet == null) {
				otherAtomSet = new CMLAtomSet();
				otherAtomSetTable.put(iii, otherAtomSet);
			}
			try {
				otherAtomSet.addAtom(otherAtom);
			} catch (Exception e) {
				e.printStackTrace();
			}
			otherAtomSetTable1.put(atomSet, otherAtomSet);
		}
		// find the most frequent displacement vector
		String theIntDelta = null;
		int theCount = 0;
		atomSet = null;
		for (String intD : deltaTable.keySet()) {
			int count = deltaTable.get(intD).intValue();
			CMLAtomSet atomSetToolx = atomSetTable.get(intD);
			if (count > theCount) {
				theCount = count;
				theIntDelta = intD;
				atomSet = atomSetToolx;
			}
		}
		if (atomSet != null && otherAtomSetTable1 != null) {
			otherAtomSet = otherAtomSetTable1.get(atomSet);
			Real2 theDelta = deltaInt(theIntDelta);
			// move to zero overlap of maximum atoms
			mol2.translate2D(theDelta);
			List<CMLAtom> atomsx = atomSet.getAtoms();
			List<CMLAtom> otherAtoms = otherAtomSet.getAtoms();
			if (atomsx.size() != otherAtoms.size()) {
				LOG
						.info("warning: AtomSets are not the same size in overlap2d");
			}
			for (int i = 0; i < atomsx.size(); i++) {
				CMLAtom thisAtom = atomsx.get(i);
				Real2 thisCoords2 = (Real2) p2Vector.get(i);
				int nearestIndex = mol2Vector
						.getSerialOfNearestPoint(thisCoords2);
				if (nearestIndex < 0) {
					continue;
				}
				// Real2 otherCoords2 = (Real2) mol2Vector.get(nearestIndex);
				CMLAtom otherAtom = (CMLAtom) mol2.getAtom(nearestIndex);
				atomPairVector.add(new AtomPair(thisAtom, otherAtom));
			}
		}
		return atomPairVector;
	}

	/**
	 * match the products of a reaction to the reactants of another.
	 * 
	 * @param reaction0
	 *            from reaction
	 * @param reaction1
	 * @return map
	 * @throws RuntimeException
	 */
	public CMLMap matchProductsToNextReactants(CMLReaction reaction0,
			CMLReaction reaction1) {

		LOG.info("----- mapping products " + reaction0.getId()
				+ " to reactants " + reaction1.getId() + "  -----");
		CMLMap map = new CMLMap();
		// annotate map
		map.setFromType(CMLAtom.TAG);
		map.setFromContext(reaction0.getId(Component.PRODUCT.name));
		map.setToType(CMLAtom.TAG);
		map.setToContext(reaction1.getId(Component.REACTANT.name));
		map.setTitle("Product N to reactant N+1");

		// get all molecules in product of reaction 0

		CMLProductList productList0 = (CMLProductList) reaction0
				.getFirstCMLChild(CMLProductList.TAG);
		if (productList0 == null) {
			return map;
		}
		Elements productMolecules0 = productList0
				.getChildCMLElements(CMLMolecule.TAG);
		List<CMLMolecule> spectatorMolecule0 = reaction0
				.getSpectatorMolecules(Component.PRODUCT.number);
		List<CMLMolecule> molecule0 = concat(productMolecules0,
				spectatorMolecule0);
		// LOG.info("PRODUCTS " + productMolecules0.size()
		// + " [cmlSpectator " + spectatorMolecule0.size() + "] ="
		// + molecule0.size());

		// get all molecules in reactant of reaction 1

		CMLReactantList reactantList1 = (CMLReactantList) reaction1
				.getFirstCMLChild(CMLReactantList.TAG);
		if (reactantList1 == null) {
			return map;
		}
		Elements reactantMolecules1 = reactantList1
				.getChildCMLElements(CMLMolecule.TAG);
		List<CMLMolecule> spectatorMolecule1 = reaction1
				.getSpectatorMolecules(Component.REACTANT.number);
		List<CMLMolecule> molecule1 = concat(reactantMolecules1,
				spectatorMolecule1);
		LOG.info("REACTANTS " + reactantMolecules1.size() + " [cmlSpectator "
				+ spectatorMolecule1.size() + "] =" + molecule1.size());

		// find all maps between equal-atom molecules, incuding partial ones
		CMLMap[][] mapMatrix = this.getMoleculeMatch(molecule0, molecule1);

		// count how many maps might satisfy each row and column
		int[][] linkCount = new int[molecule0.size()][molecule1.size()];
		int[] rowCount = new int[molecule0.size()];
		int[] colCount = new int[molecule1.size()];
		for (int j = 0; j < molecule1.size(); j++) {
			colCount[j] = 0;
		}

		// select unique matches
		for (int i = 0; i < molecule0.size(); i++) {
			rowCount[i] = 0;
			for (int j = 0; j < molecule1.size(); j++) {
				linkCount[i][j] = (mapMatrix[i][j] == null) ? 0
						: mapMatrix[i][j].getCMLChildCount(CMLLink.TAG);
				LOG.info(linkCount[i][j] + S_SPACE);
				if (linkCount[i][j] > 0) {
					rowCount[i]++;
					colCount[j]++;
				}
			}
			LOG.info("..." + rowCount[i]);
		}
		LOG.info(S_EMPTY);
		for (int j = 0; j < molecule1.size(); j++) {
			LOG.info(colCount[j] + S_SPACE);
		}
		LOG.info(S_EMPTY);

		// extract best matches
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < molecule0.size(); i++) {
				// row/col with exactly one match
				if (rowCount[i] != 1) {
					continue;
				}
				for (int j = 0; j < molecule1.size(); j++) {
					if (colCount[j] != 1) {
						continue;
					}
					if (linkCount[i][j] > 0) {
						map.mergeMap(mapMatrix[i][j], CMLMap.Direction.NEITHER);
						mapMatrix[i][j] = null;
						linkCount[i][j] = -1;
						change = true;
						rowCount[i] = -1;
						colCount[j] = -1;
						break;
					}
				}
				if (change) {
					break;
				}
			}
		}
		IntMatrix linkMatrix = new IntMatrix(linkCount);
		LOG.info("LINKS-1 " + linkMatrix);

		// extract next best matches

		for (int i = 0; i < molecule0.size(); i++) {
			rowCount[i] = 0;
		}
		for (int j = 0; j < molecule1.size(); j++) {
			colCount[j] = 0;
		}

		// get counts of complete matches in each row and column
		for (int i = 0; i < molecule0.size(); i++) {
			CMLMolecule mol0 = molecule0.get(i);
			if (mol0 == null) {
				LOG.error("Null molecule: molecule0[" + i + S_RSQUARE);
				continue;
			}
			for (int j = 0; j < molecule1.size(); j++) {
				CMLMolecule mol1 = molecule1.get(j);
				if (mol1 == null) {
					LOG.error("Null molecule: molecule1[" + j + S_RSQUARE);
					continue;
				}
				// exact match with molecule?
				if (linkCount[i][j] == mol0.getCMLChildCount(CMLAtom.TAG)
						&& linkCount[i][j] == mol1
								.getCMLChildCount(CMLAtom.TAG)) {
					rowCount[i]++;
					colCount[j]++;
				}
			}
		}

		// now mark the perfect matches and remove the less than perfect matches
		for (int i = 0; i < molecule0.size(); i++) {
			if (rowCount[i] != 1) {
				continue;
			}
			for (int j = 0; j < molecule1.size(); j++) {
				if (colCount[j] != 1) {
					continue;
				}
				if (linkCount[i][j] != molecule0.get(i).getCMLChildCount(
						CMLAtom.TAG)) {
					continue;
				}
				map.mergeMap(mapMatrix[i][j], CMLMap.Direction.NEITHER);
				// set rest of row and column to zero and mark match
				for (int ii = 0; ii < molecule0.size(); ii++) {
					linkCount[ii][j] = 0;
				}
				for (int jj = 0; jj < molecule1.size(); jj++) {
					linkCount[i][jj] = 0;
				}
				linkCount[i][j] = -1;
			}
		}

		linkMatrix = new IntMatrix(linkCount);
		LOG.info("LINKS-2 " + linkMatrix);

		LOG.info("**********************PROD2REACT");
		// CMLElement.debug(mapTool.getMap());
		// map.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE);
		// FIXME this may not be true

		// tidyUnmappedAtoms(mapTool, fromAtomSetTool, toAtomSetTool);
		return map;

	}

	/**
	 * combine molecules.
	 * 
	 * @param moleculeNodes1
	 * @param m2
	 * @return list of molecules
	 */
	private static List<CMLMolecule> concat(Elements moleculeNodes1,
			List<CMLMolecule> m2) {
		List<CMLMolecule> m = new ArrayList<CMLMolecule>();
		for (int i = 0; i < moleculeNodes1.size(); i++) {
			m.add((CMLMolecule) moleculeNodes1.get(i));
		}
		for (int i = 0; i < m2.size(); i++) {
			m.add(m2.get(i));
		}
		return m;
	}

	/**
	 * translate reactants and products geometrically to get the best fit.
	 * 
	 * 
	 * experimental
	 * 
	 * @param reaction
	 * @return vector of atomPairs from product to reactant
	 * @throws RuntimeException
	 */
	public List<AtomPair> translateProductsToReactants(CMLReaction reaction) {
		List<AtomPair> atomPairVector = new ArrayList<AtomPair>();
		CMLReactantList reactantList = (CMLReactantList) reaction
				.getFirstCMLChild(CMLReactantList.TAG);
		CMLProductList productList = (CMLProductList) reaction
				.getFirstCMLChild(CMLProductList.TAG);
		if (productList == null || reactantList == null) {
			return atomPairVector;
		}
		int reactantCount = reactantList.getCMLChildCount(CMLReactant.TAG);
		int productCount = productList.getCMLChildCount(CMLProduct.TAG);
		if (productCount == 0 || reactantCount == 0) {
			return atomPairVector;
		}
		// LOG.info("R/P" + reactantCount+S_SLASH+productCount);
		// start easy...
		if (reactantCount == 1 && productCount == 1) {
			CMLReactant reactant = (CMLReactant) reactantList
					.getFirstCMLChild(CMLReactant.TAG);
			CMLMolecule reactantMolecule = (CMLMolecule) reactant
					.getFirstCMLChild(CMLMolecule.TAG);

			CMLProduct product = (CMLProduct) productList
					.getFirstCMLChild(CMLProduct.TAG);
			CMLMolecule productMolecule = (CMLMolecule) product
					.getFirstCMLChild(CMLMolecule.TAG);
			atomPairVector = this.overlap2D(reactantMolecule, productMolecule);
			for (int i = 0; i < atomPairVector.size(); i++) {
				;
				// AtomPair atomPair = (AtomPair) atomPairVector.get(i);
			}
			// LOG.info("Mapped " + atomPairVector.size()
			// + " atoms from reactant (" + reactant.getAtomCount()
			// + ") to product (" + reactant.getAtomCount() + S_RBRAK);
		} else {
			ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
			// general translate to centroids
			List<CMLAtom> reactantAtoms = reactionTool
					.getAtoms(Component.REACTANTLIST);
			List<CMLAtom> productAtoms = reactionTool
					.getAtoms(Component.PRODUCTLIST);
			// LOG.info("XXR/P" +
			// reactantAtoms.length+S_SLASH+productAtoms.length);
			CMLAtomSet reactantAtomSet = CMLAtomSet
					.createFromAtoms(reactantAtoms);
			CMLAtomSet productAtomSet = CMLAtomSet
					.createFromAtoms(productAtoms);
			reactantAtomSet.overlap2DCentroids(productAtomSet);
		}
		return atomPairVector;
	}

	/**
	 * moves the molecule children from a reactant+product pair into a
	 * spectator. the molecules are identified by labelRefs in the spectator,
	 * e.g. "Gly23". the connection tables in the molecules are expected to be
	 * identical but might vary if hydrogen decoration was variable. The
	 * coordinates are matched, so an atomMatcher is necessary
	 * 
	 * @param reaction
	 * @throws RuntimeException
	 */
	public void moveReactantProductToSpectator(CMLReaction reaction) {
		/*
		 * -- spectators in Macie have two child molecules, a reactant and a
		 * product
		 */
		/*
		 * -- <spectatorList> <cmlSpectator> <molecule ref="Ser8"/> <label
		 * dictRef="macie:sideChain" value="Ser8"/> <label dictRef="macie:hbd"
		 * value="Ser8"/> <label dictRef="macie:chargeStabiliser" value="Ser8"/>
		 * </cmlSpectator> ... </spectatorList> <reactantList> <reactant>
		 * <molecule id="0001.stg02.r.1"> <atomArray> <atom id="a1"
		 * elementType="C" x2="-6.3957" y2="3.5395"/> <atom id="a34"
		 * elementType="R" x2="-5.7576" y2="3.8594"> <label value="Ser8"/>
		 * </atom> </atomArray> </molecule> </reactant> </reactantList>
		 * <productList> <product> <molecule id="0001.stg02.p.1"> <atomArray>
		 * <atom id="a1" elementType="C" x2="6.4415" y2="3.5187"/> <atom
		 * id="a34" elementType="R" x2="7.0716" y2="3.8386"> <label
		 * value="Ser8"/> </atom> </atomArray> </molecule> </product> --
		 */
		// do we have any spectators?
		CMLSpectatorList spectatorList = (CMLSpectatorList) reaction
				.getFirstCMLChild(CMLSpectatorList.TAG);
		if (spectatorList == null) {
			return;
		}
		// catches any empty lists
		CMLReactantList reactantList = (CMLReactantList) reaction
				.getFirstCMLChild(CMLReactantList.TAG);
		Elements reactants = reactantList.getChildCMLElements(CMLReactant.TAG);
		CMLProductList productList = (CMLProductList) reaction
				.getFirstCMLChild(CMLProductList.TAG);
		Elements products = productList.getChildCMLElements(CMLProduct.TAG);
		Elements spectators = spectatorList.getChildCMLElements("spectator");

		// iterate through spectators and transfer

		for (int i = 0; i < spectators.size(); i++) {
			CMLSpectator spectator = (CMLSpectator) spectators.get(i);
			String ref = ((CMLMolecule) spectator
					.getFirstCMLChild(CMLMolecule.TAG)).getRef();
			if (ref != null) {
				spectator.getFirstCMLChild(CMLMolecule.TAG).detach();
				spectator.moveLabelledReactantsProducts(reactants, ref);
				spectator.moveLabelledReactantsProducts(products, ref);
				if (spectator.getCMLChildCount(CMLMolecule.TAG) < 2) {
					LOG.error("Cannot find 2 spectators for "
							+ reaction.getId() + "; only found "
							+ spectator.getCMLChildCount(CMLMolecule.TAG)
							+ " cmlSpectator molecules");
					break;
				}

				// spectatorList now contains reactant molecule followed by
				// product molecule
				// match on connectivity, then split atom sets and append final
				// map to cmlSpectator

				CMLMolecule reactantMolecule = (CMLMolecule) spectator
						.getFirstCMLChild(CMLMolecule.TAG);
				CMLMolecule productMolecule = (CMLMolecule) spectator
						.getChildCMLElement(CMLMolecule.TAG, 1);
				// LOG.error("Reactant
				// (
				// "+reactantMolecule.getId()+S_SLASH+reactantTool.getAtomCount()+"
				// )
				// and product
				//("+productMolecule.getId()+S_SLASH+productTool.getAtomCount()+"
				// )
				// spectators of different lengths");
				// continue;
				// }
				// CMLMap spectatorMap = matchMoleculePair(reactantTool,
				// productTool, MATCH_MORGAN);
				// CMLElement.debug(reactantMolecule);
				// CMLElement.debug(productMolecule);
				CMLMap spectatorMap = null;
				try {
					spectatorMap = this.mapMolecules(productMolecule,
							reactantMolecule);
				} catch (RuntimeException cmle) {
					// molecules of different lengths, create a zero length map
					LOG.error("Reactant (" + reactantMolecule.getId() + S_SLASH
							+ reactantMolecule.getAtomCount()
							+ ") and product (" + productMolecule.getId()
							+ S_SLASH + productMolecule.getAtomCount()
							+ ") spectators " + cmle);
					spectatorMap = new CMLMap();
				}
				// if (spectatorMap != null) {

				spectatorMap
						.setTitle(CMLReaction.FROM_SPECTATOR_PRODUCT_TO_REACTANT);
				CMLMolecule mol1 = (CMLMolecule) ((CMLSpectator) spectators
						.get(i)).getChildCMLElement(CMLMolecule.TAG, 1);
				spectatorMap.setFromContext(mol1.getId());
				CMLMolecule mol0 = (CMLMolecule) ((CMLSpectator) spectators
						.get(i)).getFirstCMLChild(CMLMolecule.TAG);
				spectatorMap.setToContext(mol0.getId());
				spectatorMap.setFromType(CMLAtom.TAG);
				spectatorMap.setToType(CMLAtom.TAG);

				this.splitAndProcessAtomSets(spectatorMap, productMolecule,
						reactantMolecule);
				spectators.get(i).appendChild(spectatorMap);

				if (spectatorMap.getCMLChildCount(CMLLink.TAG) != reactantMolecule
						.getCMLChildCount(CMLAtom.TAG)) {
					LOG.error("Unequal cmlSpectator map and molecule");
					LOG
							.error("**ERROR**(move reactant to spectator) Unequal cmlSpectator map and molecule***");
					// CMLElement.debug(spectatorMap);
					// CMLElement.debug(reactant.getMolecule());
					CMLAtomSet fromAtomSet = MoleculeTool.getOrCreateTool(
							productMolecule).getAtomSet();
					CMLAtomSet toAtomSet = MoleculeTool.getOrCreateTool(
							reactantMolecule).getAtomSet();
					fromAtomSet.removeAtoms(spectatorMap, toAtomSet);
					// have to do an atomOverlap
					AtomMatcher geomAtomMatcher = new AtomMatcher();
					geomAtomMatcher
							.setAtomMatchStrategy(AtomMatcher.Strategy.MATCH_TOTAL_DISTANCE);
					CMLMap overlapMap = geomAtomMatcher.createMapFrom2DOverlap(
							fromAtomSet, toAtomSet);
					spectatorMap.mergeMap(overlapMap, CMLMap.Direction.NEITHER);
					fromAtomSet.removeAtoms(overlapMap, toAtomSet);

					spectatorMap
							.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE);
					// FIXME - replace by subroutine

				}
				// }
			}
		}

		// removes empty spectatorLists

		if (spectatorList.getChildElements().size() == 0) {
			spectatorList.detach();
		}
	}

	/**
	 * returns containing atomTree labelling (was in AtomSet) the atomTree is
	 * calculated for each atom and expanded until that atom can be seen to be
	 * unique in the atomSet or until maxAtomTreeLevel is reached For those
	 * atoms which have unique atomTreeStrings and are keyed by these. If there
	 * are sets of atoms which have the same string they are mapped to atomSets.
	 * Example (maxAtomTreeLevel = 1: O1-C2-O3 has map = {"C", "a2"}, {"O(C)",
	 * atomSet(a1, a3)}
	 * 
	 * @param atomSet
	 * @param atomMatcher
	 *            to use
	 * @return the maps
	 */
	@SuppressWarnings("all")
	// FIXME at present map elements are either atoms or lists of atoms; messy
	public Map<String, Object> getAtomTreeLabelling(CMLAtomSet atomSet) {
		// FIXME this is broken - does not do atomsets
		Map<String, Object> atomMap = null;
		Map<String, Object> uniqueAtomMap = new HashMap<String, Object>();
		// iterate through levels
		List<CMLAtom> atoms = atomSet.getAtoms();
		int atomTreeLevel = this.getAtomTreeLevel();
		boolean variableLevel = (atomTreeLevel < 0);
		int startLevel = (variableLevel) ? 0 : atomTreeLevel;
		int endLevel = (variableLevel) ? this.maximumAtomTreeLevel
				: atomTreeLevel + 1;
		for (int level = startLevel; level < endLevel; level++) {
			atomMap = new HashMap<String, Object>();
			for (int i = 0; i < atoms.size(); i++) {
				CMLAtom atom = atoms.get(i);
				boolean omit = false;
				String elementType = atom.getElementType();
				for (int j = 0; j < this.excludeElementTypes.length; j++) {
					if (this.excludeElementTypes[j].equals(elementType)) {
						omit = true;
						break;
					}
				}
				if (!omit) {
					// atom still not unique?
					if (!variableLevel || !uniqueAtomMap.containsValue(atom)) {
						AtomTree atomTree = new AtomTree(atom);
						atomTree.setUseCharge(this.isUseCharge());
						atomTree.setUseLabel(this.isUseLabel());
						atomTree.setUseExplicitHydrogens(AS.H
								.equals(elementType));
						atomTree.expandTo(level);
						String atval = atomTree.toString();
						if (variableLevel) {
							// LOG.info((CMLAtom)atoms.get(i).getId()+"....."+
							// atval);
						}
						List<CMLAtom> atomList = (List<CMLAtom>) atomMap
								.get(atval);
						if (atomList == null) {
							atomList = new ArrayList<CMLAtom>();
							atomMap.put(atval, atomList);
						}
						atomList.add(atom);
					}
				}
			}

			// mark any unique atoms
			Set<String> keySet = atomMap.keySet();
			for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
				String atomTreeString = it.next();
				List list = (List) atomMap.get(atomTreeString);
				// if only one element, mark as unique
				if (list.size() == 1) {
					uniqueAtomMap.put(atomTreeString, (CMLAtom) list.get(0));
				}
			}
		}
		Map<String, Object> finalMap = (variableLevel) ? uniqueAtomMap
				: atomMap;
		Set<String> keySet = atomMap.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String atomTreeString = it.next();
			List<CMLAtom> atomList = (List<CMLAtom>) atomMap
					.get(atomTreeString);
			// just mapped?
			if (atomList != null && atomList.size() == 1) {
				CMLAtom atom = atomList.get(0);
				finalMap.put(atomTreeString, atom);
			} else {
				CMLAtomSet atomSet1 = CMLAtomSet.createFromAtoms(atomList);
				finalMap.put(atomTreeString, atomSet1);
			}
		}
		// }

		return finalMap;
	}

	// this is to overlap 2D atoms within a tolerance
	final static int RESOLUTION = 2;

	/**
	 * allow mapping of delta.
	 * 
	 * @param p
	 * @return hash
	 */
	static Long intDelta(Real2 p) {
		int i = (int) Math.round((int) RESOLUTION * (p.y + 10));
		int ii = (int) Math.round((int) RESOLUTION * (p.x + 10));
		int iii = 10000 * ii + i;
		return new Long(iii);
	}

	/**
	 * allow mapping of delta.
	 * 
	 * @param iiii
	 * @return hash
	 */
	static Real2 deltaInt(String iiii) {
		long iii = new Long(iiii).longValue();
		long ii = iii / 10000;
		long i = iii - 10000 * ii;
		double x = ((double) ii) / RESOLUTION - 10.;
		double y = ((double) i) / RESOLUTION - 10.;
		return new Real2(x, y);
	}

}

class CountReal2 extends Real2 implements Comparable<CountReal2> {

	private int count = 0;

	public CountReal2(Real2 r2) {
		super(r2);
		increment();
	}

	public void increment() {
		count++;
	}

	public int compareTo(CountReal2 r) {
		int compare = 0;
		if (this.count > r.count) {
			compare = 1;
		} else if (this.count < r.count) {
			compare = -1;
		}
		return compare;
	}

	public String toString() {
		return super.toString() + " / " + count;
	}

	public int getCount() {
		return count;
	}

	public boolean matches(CMLAtom searchAtom, CMLAtom targetAtom) {
		boolean matches = false;
		if (searchAtom != null && targetAtom != null) {
			matches = true;
		}
		if (matches) {
			matches = searchAtom.getElementType() != null
					&& searchAtom.getElementType().equals(
							targetAtom.getElementType());
		}
		// add more here later
		return matches;
	}
}
