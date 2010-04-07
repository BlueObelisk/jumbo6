package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlcml.cml.base.CMLConstants;
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
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.tools.AtomMatcher.Strategy;
import org.xmlcml.euclid.Int2;
import org.xmlcml.euclid.IntSet;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Vector;
import org.xmlcml.euclid.RealMatrix;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector2;

public class AtomMatcher2D extends AtomMatcher {

	public AtomMatcher2D() {
		super();
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
	private List<AtomPair> overlap2D(CMLAtomSet atomSet1, CMLAtomSet atomSet2) {

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
			String iii = CMLConstants.S_EMPTY + intDelta(delta);
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
	private CMLMap matchAtomsByCoordinates(CMLAtomSet atomSet1,
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

	
	/**
	 * map geometrical neighbours. needs further refactoring
	 * 
	 * @param atomSet
	 * @param atomSet2
	 * @return map
	 */
	private CMLMap mapGeometricalNeighbours(CMLAtomSet atomSet,
			CMLAtomSet atomSet2) {
		CMLMap cmlMap = makeMap();
		if (atomSet.size() != atomSet2.size()) {
			LOG.info(WARNING_S + CMLConstants.S_NL + AtomMatcher.Strategy.DIFFERENT_SIZES
					+ CMLConstants.S_NL + WARNING_S);
			cmlMap.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_INCOMPLETE);
		} else {
			cmlMap.setDictRef(CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE);
			LOG.info(BANNER_S + CMLConstants.S_NL
					+ CMLReaction.MAP_REACTION_ATOM_MAP_COMPLETE + CMLConstants.S_NL
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
		}
		for (int i = 0; i < nAtoms1; i++) {
			CMLAtom atom1 = (CMLAtom) atomSet.getAtoms().get(i);
			atom1Id[i] = atom1.getId();
			LOG.trace(S_SPACE + atom1Id[i] + CMLConstants.S_LBRAK + atom1.getElementType()
			 + CMLConstants.S_RBRAK);
			Real2 atom1Coord = new Real2(atom1.getX2(), atom1.getY2());
			for (int j = 0; j < nAtoms2; j++) {
				CMLAtom atom2 = (CMLAtom) atomSet2.getAtoms().get(j);
				Real2 atom2Coord = new Real2(atom2.getX2(), atom2.getY2());
				distanceMatrix[i][j] = (!atom1.getElementType().equals(
						atom2.getElementType())) ? CMLAtomSet.MAX_DIST
						: atom1Coord.getDistance(atom2Coord);
				 LOG.trace(("      " + (int) (10 * distanceMatrix[i][j])));
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

	public CMLMap match(CMLAtomSet atomSet1, CMLAtomSet atomSet2, String title) {
		CMLMap cmlMap = makeMap();
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
		CMLMap cmlMap = makeMap();
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
	 * simple overlap of 2D molecules.
	 * 
	 * very crude at present will ALTER coords of mol2
	 * 
	 * @param mol1
	 * @param mol2
	 *            molecule to overlap
	 * @return Vector of AtomPairs of matching thisAtom, otherAtom
	 */
	private List<AtomPair> overlap2D(CMLMolecule mol1, CMLMolecule mol2) {

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
			String iii = CMLConstants.S_EMPTY + intDelta(delta);
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
	 * translate reactants and products geometrically to get the best fit.
	 * 
	 * 
	 * experimental
	 * 
	 * @param reaction
	 * @return vector of atomPairs from product to reactant
	 * @throws RuntimeException
	 */
	private List<AtomPair> translateProductsToReactants(CMLReaction reaction) {
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
			// + ") to product (" + reactant.getAtomCount() + CMLConstants.S_RBRAK);
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
