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
import java.util.List;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;

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
public abstract class AtomMatcher extends AbstractTool {

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
	public final static String[] DEFAULT_INCLUDE_ELEMENT_TYPES = {};
	public final static String[] DEFAULT_EXCLUDE_ELEMENT_TYPES = {};
	public final static String[] DEFAULT_INCLUDE_LIGAND_ELEMENT_TYPES = {};
	public final static String[] DEFAULT_EXCLUDE_LIGAND_ELEMENT_TYPES = {};
	public final static boolean DEFAULT_USE_CHARGE = false;
	public final static boolean DEFAULT_USE_LABEL = false;
	public final static int DEFAULT_MAXIMUM_ATOM_TREE_LEVEL = MAX_ATOM_TREE_LEVEL;

	/**
	 * user-specified limit to apply to all atoms.
	 * 
	 * if
	 */
	public final static int DEFAULT_ATOM_TREE_LEVEL = -1;
	public final static AtomMatcher.Strategy DEFAULT_ATOM_MATCH_STRATEGY = AtomMatcher.Strategy.MATCH_ATOM_TREE_LABEL;
	public final static AtomMatcher.Strategy DEFAULT_ATOM_SET_EXPANSION = AtomMatcher.Strategy.MATCH_GEOM;
	
	protected AtomMatchObject atomMatchObject = new AtomMatchObject();
	public AtomMatchObject getAtomMatchObject() {
		return atomMatchObject;
	}

	public void setAtomMatchObject(AtomMatchObject atomMatchObject) {
		this.atomMatchObject = atomMatchObject;
	}

	/**
	 * the level to explore all atoms uniformly. if < 0 the explores until
	 * uniquification or maximumAtomTreeLevel reached
	 */
//	protected int atomTreeLevel;
//	protected AtomMatcher.Strategy atomMatchStrategy;
//	protected AtomMatcher.Strategy atomSetExpansionStrategy;

	// this is to overlap 2D atoms within a tolerance
	final static int RESOLUTION = 2;

	/**
	 * constructor.
	 */
	public AtomMatcher() {
		init();
	}

	void init() {
		atomMatchObject.setIncludeElementTypes(DEFAULT_INCLUDE_ELEMENT_TYPES);
		atomMatchObject.setExcludeElementTypes(DEFAULT_EXCLUDE_ELEMENT_TYPES);
		atomMatchObject.setIncludeLigandElementTypes(DEFAULT_INCLUDE_LIGAND_ELEMENT_TYPES);
		atomMatchObject.setExcludeLigandElementTypes(DEFAULT_EXCLUDE_LIGAND_ELEMENT_TYPES);
		atomMatchObject.setUseCharge(DEFAULT_USE_CHARGE);
		atomMatchObject.setUseLabel(DEFAULT_USE_LABEL);
		atomMatchObject.setMaximumAtomTreeLevel(DEFAULT_MAXIMUM_ATOM_TREE_LEVEL);
		atomMatchObject.setAtomTreeLevel(DEFAULT_ATOM_TREE_LEVEL);
		atomMatchObject.setAtomMatchStrategy(DEFAULT_ATOM_MATCH_STRATEGY);
		atomMatchObject.setAtomSetExpansionStrategy(DEFAULT_ATOM_SET_EXPANSION);
	}

	/**
	 * get the included elementTypes.
	 * 
	 * @return Returns the elementTypes to include.
	 */
	public String[] getIncludeElementTypes() {
		return atomMatchObject.getIncludeElementTypes();
	}

	/**
	 * set the included elementTypes. resets excludeElement types to empty list
	 * 
	 * @param elementTypes
	 *            to include.
	 */
	public void setIncludeElementTypes(String[] elementTypes) {
		atomMatchObject.setIncludeElementTypes(elementTypes);
		atomMatchObject.setExcludeElementTypes(new String[] {});
	}

	/**
	 * get the excluded elementTypes.
	 * 
	 * @return Returns the elementTypes to exclude.
	 */
	public String[] getExcludeElementTypes() {
		return atomMatchObject.getExcludeElementTypes();
	}

	/**
	 * set the excluded elementTypes. resets includeElement types to empty list
	 * 
	 * @param elementTypes
	 *            to exclude.
	 */
	public void setExcludeElementTypes(String[] elementTypes) {
		atomMatchObject.setExcludeElementTypes(elementTypes);
		atomMatchObject.setIncludeElementTypes(new String[] {});
	}

	/**
	 * get the included ligand elementTypes.
	 * 
	 * @return Returns the elementTypes to include.
	 */
	public String[] getIncludeLigandElementTypes() {
		return atomMatchObject.getIncludeLigandElementTypes();
	}

	/**
	 * set the included ligand elementTypes. resets excludeLigandElementTypes to
	 * empty list
	 * 
	 * @param elementTypes
	 *            to include.
	 */
	public void setIncludeLigandElementTypes(String[] elementTypes) {
		atomMatchObject.setIncludeLigandElementTypes(elementTypes);
		atomMatchObject.setExcludeLigandElementTypes(new String[] {});
	}

	/**
	 * get the excluded ligand elementTypes.
	 * 
	 * @return Returns the elementTypes to exclude.
	 */
	public String[] getExcludeLigandElementTypes() {
		return atomMatchObject.getExcludeLigandElementTypes();
	}

	/**
	 * set the excluded ligand elementTypes. set includedLigand element types to
	 * empty list.
	 * 
	 * @param elementTypes
	 *            to exclude.
	 */
	public void setExcludeLigandElementTypes(String[] elementTypes) {
		atomMatchObject.setExcludeLigandElementTypes(elementTypes);
		atomMatchObject.setIncludeLigandElementTypes(new String[] {});
	}

	/**
	 * @return Returns the atomTreeLevel.
	 */
	public int getAtomTreeLevel() {
		return atomMatchObject.getAtomTreeLevel();
	}

	/**
	 * @param atomTreeLevel
	 *            the maximum level to explore trees to
	 */
	public void setAtomTreeLevel(int atomTreeLevel) {
		atomMatchObject.setAtomTreeLevel(atomTreeLevel);
	}

	/**
	 * @return Returns the maximumAtomTreeLevel.
	 */
	public int getMaximumAtomTreeLevel() {
		return atomMatchObject.getMaximumAtomTreeLevel();
	}

	/**
	 * @param maximumAtomTreeLevel
	 *            the maximum level to explore trees to
	 */
	public void setMaximumAtomTreeLevel(int maximumAtomTreeLevel) {
		atomMatchObject.setMaximumAtomTreeLevel(maximumAtomTreeLevel);
	}

	/**
	 * @return is charge to be used
	 */
	public boolean isUseCharge() {
		return atomMatchObject.isUseCharge();
	}

	/**
	 * @param useCharge
	 *            is charge to be used
	 */
	public void setUseCharge(boolean useCharge) {
		atomMatchObject.setUseCharge(useCharge);
	}

	/**
	 * @return is label to be used
	 */
	public boolean isUseLabel() {
		return atomMatchObject.isUseLabel();
	}

	/**
	 * @param useLabel
	 *            is label to be used.
	 */
	public void setUseLabel(boolean useLabel) {
		atomMatchObject.setUseLabel(useLabel);
	}

	/**
	 * @return Returns the atomMatchStrategy.
	 */
	public AtomMatcher.Strategy getAtomMatchStrategy() {
		return atomMatchObject.getAtomMatchStrategy();
	}

	/**
	 * @param strategy
	 *            The atomMatchStrategy to set.
	 */
	public void setAtomMatchStrategy(AtomMatcher.Strategy strategy) {
		atomMatchObject.setAtomMatchStrategy(strategy);
	}

	/**
	 * @return Returns the atomSetExpansionStrategy.
	 */
	public AtomMatcher.Strategy getAtomSetExpansionStrategy() {
		return atomMatchObject.getAtomSetExpansionStrategy();
	}

	/**
	 * @param strategy
	 *            The atomSetExpansionStrategy to set.
	 */
	public void setAtomSetExpansionStrategy(AtomMatcher.Strategy strategy) {
		atomMatchObject.setAtomSetExpansionStrategy(strategy);
	}

	/**
	 * skip atom. uses previously set include and exclude atom criteria
	 * 
	 * @param atom
	 * @return true
	 */
	public boolean skipAtom(CMLAtom atom) {
		return skipAtom(atom, 
				atomMatchObject.getIncludeElementTypes(), 
				atomMatchObject.getExcludeElementTypes());
	}

	/**
	 * skip ligand atom. uses previously set include and exclude atom criteria
	 * 
	 * @param atom
	 * @return true
	 */
	public boolean skipLigandAtom(CMLAtom atom) {
		return skipAtom(atom, atomMatchObject.getIncludeLigandElementTypes(),
				atomMatchObject.getExcludeLigandElementTypes());
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
	 * CMLConstants.S_STAR character
	 * 
	 * @param elementType
	 * @param allowedTypes
	 * @return true if allowed
	 */
	private boolean matches(String elementType, String[] allowedTypes) {
		boolean matches = false;
		if (elementType == null) {
		} else if (allowedTypes.length == 0) {
		} else if (allowedTypes.length == 1 && CMLConstants.S_STAR.equals(allowedTypes[0])) {
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
		s += "useCharge: " + atomMatchObject.isUseCharge() + CMLConstants.S_SEMICOLON;
		s += "useLabel: " + atomMatchObject.isUseLabel() + CMLConstants.S_SEMICOLON;
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
	@SuppressWarnings("unused")
	public CMLMap mapAtomSets(CMLAtomSet atomSet0, CMLAtomSet atomSet1) {
		if (atomSet0 == null) {
			throw new RuntimeException("atomSet is null: " + atomSet0);
		}
		if (atomSet1 == null) {
			throw new RuntimeException("atomSet is null: " + atomSet1);
		}
		CMLLink cmlLink = null;
		if (atomSet0.size() != atomSet1.size() && false) {
			// FIXME may manage this later
			throw new RuntimeException("mapAtomSets wrong sizes:  "
					+ atomSet0.size() + CMLConstants.S_SLASH + atomSet1.size());
		}
		String title = this.getAtomMatchStrategy().toString();
		LOG.trace("Strategy: "+title);
	    CMLMap cmlMap = match(atomSet0, atomSet1, title);
		return cmlMap;
	}
	
	protected static CMLMap makeMap() {
		CMLMap cmlMap = new CMLMap();
		cmlMap.setToType(CMLAtom.TAG);
		cmlMap.setFromType(CMLAtom.TAG);
		return cmlMap;
	}
	
	public abstract CMLMap match(
			CMLAtomSet atomSet0,CMLAtomSet atomSet1, String title);

	public static AtomMatcher createAtomMatcher(Strategy strategy) {
		AtomMatcher atomMatcher = null;
		if (strategy == null) {
			throw new RuntimeException("Null strategy");
		} else if (strategy.equals(Strategy.MATCH_MORGAN)) {
			atomMatcher = new MorganAtomMatcher();
		} else if (strategy.equals(Strategy.MATCH_GEOM)) {
			atomMatcher = new AtomMatcher2D();
		} else if (strategy.equals(Strategy.MATCH_ATOM_TREE_LABEL)) {
			atomMatcher = new AtomTreeMatcher();
		} else {
			throw new RuntimeException("Unknown strategy");
		}
		return atomMatcher;
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
		if (molecule0 != null && molecule1 != null) {
			CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule0)
					.getAtomSet();
//			atomSet.debug("AtomSet to match");
			CMLAtomSet atomSet1 = MoleculeTool.getOrCreateTool(molecule1)
					.getAtomSet();
//			atomSet1.debug("AtomSet to match");
			cmlMap = this.mapAtomSets(atomSet, atomSet1);
		} else {
			throw new RuntimeException("Cannot match null molecules");
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
//					if (false) {
//						splitAndProcessAtomSets(mapij, mol0, mol1);
//					}
					if (mapij.getChildElements(CMLLink.TAG, CMLConstants.CML_NS).size() != mol0
							.getCMLChildCount(CMLAtom.TAG)) {
						// incomplete match; currently no action
					}
				}
				mapMatrix[i][j] = mapij;
			}
		}
		return mapMatrix;
	}


	private CMLAtomSet makeAtomSet(Object object) {
		CMLAtomSet atomSet = null;
		if (object == null) {
			atomSet = new CMLAtomSet();
		} else if (object instanceof CMLAtomSet){
			atomSet = (CMLAtomSet) object;
		} else if (object instanceof CMLAtom){
			atomSet = new CMLAtomSet();
			atomSet.addAtom((CMLAtom) object);
		} else {
			throw new RuntimeException("Unknown type: "+object);
		}
		return atomSet;
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
		if (true) {
			throw new RuntimeException("NYI");
		}
		return null;
//		return new AtomPair(new CMLAtom(), new CMLAtom());
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
					.concatenate(atomSetLink.getFromSet(), CMLConstants.S_SPACE);
			String toSet = Util.concatenate(atomSetLink.getToSet(), CMLConstants.S_SPACE);
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
				AtomMatcher matcher2d = new AtomMatcher2D();
				// matcher2d.setAtomMatchStrategy(atomMatcher.
				// getAtomSetExpansionStrategy());
				CMLMap overlapMap = matcher2d.match(toSubAtomSet, fromSubAtomSet, "2d");
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

	protected CMLMap createLinksInMap(List<CMLAtom> atoms1, String[] atomIds2,
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
				LOG.info(linkCount[i][j] + CMLConstants.S_SPACE);
				if (linkCount[i][j] > 0) {
					rowCount[i]++;
					colCount[j]++;
				}
			}
			LOG.info("..." + rowCount[i]);
		}
		LOG.info(S_EMPTY);
		for (int j = 0; j < molecule1.size(); j++) {
			LOG.info(colCount[j] + CMLConstants.S_SPACE);
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
				LOG.error("Null molecule: molecule0[" + i + CMLConstants.S_RSQUARE);
				continue;
			}
			for (int j = 0; j < molecule1.size(); j++) {
				CMLMolecule mol1 = molecule1.get(j);
				if (mol1 == null) {
					LOG.error("Null molecule: molecule1[" + j + CMLConstants.S_RSQUARE);
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

//	/**
//	 * moves the molecule children from a reactant+product pair into a
//	 * spectator. the molecules are identified by labelRefs in the spectator,
//	 * e.g. "Gly23". the connection tables in the molecules are expected to be
//	 * identical but might vary if hydrogen decoration was variable. The
//	 * coordinates are matched, so an atomMatcher is necessary
//	 * 
//	 * @param reaction
//	 * @throws RuntimeException
//	 */
//	public void moveReactantProductToSpectator(CMLReaction reaction) {
//		/*
//		 * -- spectators in Macie have two child molecules, a reactant and a
//		 * product
//		 */
//		/*
//		 * -- <spectatorList> <cmlSpectator> <molecule ref="Ser8"/> <label
//		 * dictRef="macie:sideChain" value="Ser8"/> <label dictRef="macie:hbd"
//		 * value="Ser8"/> <label dictRef="macie:chargeStabiliser" value="Ser8"/>
//		 * </cmlSpectator> ... </spectatorList> <reactantList> <reactant>
//		 * <molecule id="0001.stg02.r.1"> <atomArray> <atom id="a1"
//		 * elementType="C" x2="-6.3957" y2="3.5395"/> <atom id="a34"
//		 * elementType="R" x2="-5.7576" y2="3.8594"> <label value="Ser8"/>
//		 * </atom> </atomArray> </molecule> </reactant> </reactantList>
//		 * <productList> <product> <molecule id="0001.stg02.p.1"> <atomArray>
//		 * <atom id="a1" elementType="C" x2="6.4415" y2="3.5187"/> <atom
//		 * id="a34" elementType="R" x2="7.0716" y2="3.8386"> <label
//		 * value="Ser8"/> </atom> </atomArray> </molecule> </product> --
//		 */
//		// do we have any spectators?
//		CMLSpectatorList spectatorList = (CMLSpectatorList) reaction
//				.getFirstCMLChild(CMLSpectatorList.TAG);
//		if (spectatorList == null) {
//			return;
//		}
//		// catches any empty lists
//		CMLReactantList reactantList = (CMLReactantList) reaction
//				.getFirstCMLChild(CMLReactantList.TAG);
//		Elements reactants = reactantList.getChildCMLElements(CMLReactant.TAG);
//		CMLProductList productList = (CMLProductList) reaction
//				.getFirstCMLChild(CMLProductList.TAG);
//		Elements products = productList.getChildCMLElements(CMLProduct.TAG);
//		Elements spectators = spectatorList.getChildCMLElements("spectator");//
//		// iterate through spectators and transfer
//
//		for (int i = 0; i < spectators.size(); i++) {
//			CMLSpectator spectator = (CMLSpectator) spectators.get(i);
//			String ref = ((CMLMolecule) spectator
//					.getFirstCMLChild(CMLMolecule.TAG)).getRef();
//			if (ref != null) {
//				spectator.getFirstCMLChild(CMLMolecule.TAG).detach();
//				spectator.moveLabelledReactantsProducts(reactants, ref);
//				spectator.moveLabelledReactantsProducts(products, ref);
//				if (spectator.getCMLChildCount(CMLMolecule.TAG) < 2) {
//					LOG.error("Cannot find 2 spectators for "
//							+ reaction.getId() + "; only found "
//							+ spectator.getCMLChildCount(CMLMolecule.TAG)
//							+ " cmlSpectator molecules");
//					break;
//				}
//
//				// spectatorList now contains reactant molecule followed by
//				// product molecule
//				// match on connectivity, then split atom sets and append final
//				// map to cmlSpectator
//
//				CMLMolecule reactantMolecule = (CMLMolecule) spectator
//						.getFirstCMLChild(CMLMolecule.TAG);
//				CMLMolecule productMolecule = (CMLMolecule) spectator
//						.getChildCMLElement(CMLMolecule.TAG, 1);
//				// LOG.error("Reactant
//				// (
//				// "+reactantMolecule.getId()+S_SLASH+reactantTool.getAtomCount()+"
//				// )
//				// and product
//				//("+productMolecule.getId()+S_SLASH+productTool.getAtomCount()+"
//				// )
//				// spectators of different lengths");
//				// continue;
//				// }
//				// CMLMap spectatorMap = matchMoleculePair(reactantTool,
//				// productTool, MATCH_MORGAN);
//				// CMLElement.debug(reactantMolecule);
//				// CMLElement.debug(productMolecule);
//				CMLMap spectatorMap = null;
//				try {
//					spectatorMap = this.mapMolecules(productMolecule,
//							reactantMolecule);
//				} catch (RuntimeException cmle) {
//					// molecules of different lengths, create a zero length map
//					LOG.error("Reactant (" + reactantMolecule.getId() + CMLConstants.S_SLASH
//							+ reactantMolecule.getAtomCount()
//							+ ") and product (" + productMolecule.getId()
//							+ CMLConstants.S_SLASH + productMolecule.getAtomCount()
//							+ ") spectators " + cmle);
//					spectatorMap = new CMLMap();
//				}
//				// if (spectatorMap != null) {
//
//				spectatorMap
//						.setTitle(CMLReaction.FROM_SPECTATOR_PRODUCT_TO_REACTANT);
//				CMLMolecule mol1 = (CMLMolecule) ((CMLSpectator) spectators
//						.get(i)).getChildCMLElement(CMLMolecule.TAG, 1);
//				spectatorMap.setFromContext(mol1.getId());
//				CMLMolecule mol0 = (CMLMolecule) ((CMLSpectator) spectators
//						.get(i)).getFirstCMLChild(CMLMolecule.TAG);
//				spectatorMap.setToContext(mol0.getId());
//				spectatorMap.setFromType(CMLAtom.TAG);
//				spectatorMap.setToType(CMLAtom.TAG);
//
//				this.splitAndProcessAtomSets(spectatorMap, productMolecule,
//						reactantMolecule);
//				spectators.get(i).appendChild(spectatorMap);
//
//				if (spectatorMap.getCMLChildCount(CMLLink.TAG) != reactantMolecule
//						.getCMLChildCount(CMLAtom.TAG)) {
//					LOG.error("Unequal cmlSpectator map and molecule");
//					LOG
//							.error("**ERROR**(move reactant to spectator) Unequal cmlSpectator map and molecule***");
//					// CMLElement.debug(spectatorMap);
//					// CMLElement.debug(reactant.getMolecule());
//					CMLAtomSet fromAtomSet = MoleculeTool.getOrCreateTool(
//							productMolecule).getAtomSet();
//					CMLAtomSet toAtomSet = MoleculeTool.getOrCreateTool(
//							reactantMolecule).getAtomSet();
//					fromAtomSet.removeAtoms(spectatorMap, toAtomSet);
//					// have to do an atomOverlap
//					AtomMatcher geomAtomMatcher = new AtomMatcher();
//					geomAtomMatcher
//							.setAtomMatchStrategy(AtomMatcher.Strategy.MATCH_TOTAL_DISTANCE);
//					CMLMap overlapMap = geomAtomMatcher.createMapFrom2DOverlap(
//							fromAtomSet, toAtomSet);
//					spectatorMap.mergeMap(overlapMap, CMLMap.Direction.NEITHER);
//					fromAtomSet.removeAtoms(overlapMap, toAtomSet);
//
//					spectatorMap
//							.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE);
//					// FIXME - replace by subroutine
//
//				}
//				// }
//			}
//		}
//
//		// removes empty spectatorLists
//
//		if (spectatorList.getChildElements().size() == 0) {
//			spectatorList.detach();
//		}
//	}

}
