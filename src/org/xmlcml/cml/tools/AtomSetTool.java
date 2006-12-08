package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Elements;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;

/**
 * tool to support atom set. not sure if useful
 * 
 * @author pmr
 * 
 */
public class AtomSetTool implements CMLConstants {

    Logger logger = Logger.getLogger(AtomSetTool.class.getName());

    CMLAtomSet atomSet;
    CMLMolecule molecule = null;

    Map<CMLAtom, CMLAtom> parentTable = null;

    /**
     * constructor.
     * 
     * @param atomSet
     */
    public AtomSetTool(CMLAtomSet atomSet) {
        if (atomSet == null) {
            throw new CMLRuntimeException("Null atomSet");
        }
        this.atomSet = atomSet;
        List<CMLAtom> atomList = atomSet.getAtoms();
        if (atomList.size() > 0) {
            molecule = atomList.get(0).getMolecule();
        }
    }

    // =============== ATOMSET =========================

    /**
     * gets bondset for all bonds in current atom set.
     * slow.
     * order of bond is
     * undetermined but probably in atom document order and iteration through
     * ligands
     * 
     * @param atomSet
     * @return the bondSet
     * @throws CMLException
     *             one or more bonds does not have an id
     */
    @SuppressWarnings("unused")
    // FIXME don't think this works
    public CMLBondSet getBondSet(/*CMLAtomSet atomSet*/) throws CMLException {
        List<CMLAtom> atoms = molecule.getAtoms();
        molecule.getBonds();
        CMLBondSet bondSet = new CMLBondSet();
        for (CMLAtom atom : atoms) {
            List<CMLAtom> ligandList = atom.getLigandAtoms();
            List<CMLBond> ligandBondList = atom.getLigandBonds();
            // Iterator<CMLAtom> ita = atom.getLigandList().iterator();
            // Iterator<CMLBond> itb = atom.getBondList().iterator();
            // loop through ligands and examine each for membership of this set;
            // if so add bond
            int i = 0;
            for (CMLAtom ligand : ligandList) {
                CMLBond ligandBond = ligandBondList.get(i++);
//                if (atomSet.contains(ligand)) {
                    bondSet.addBond(ligandBond);
//                }
            }
        }
        return bondSet;
    }

    /**
     * create all valence angles for molecule.
     * 
     * @param atomSet
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of angles (zero length if none)
     */
    public List<CMLAngle> createValenceAngles(CMLAtomSet atomSet,
            boolean calculate, boolean add) {
        List<CMLAngle> angleVector = new ArrayList<CMLAngle>();
        for (CMLAtom atomi : atomSet.getAtoms()) {
            Set<CMLAtom> usedAtomSetj = new HashSet<CMLAtom>();
            List<CMLAtom> ligandListI = atomi.getLigandAtoms();
            for (CMLAtom atomj : ligandListI) {
                usedAtomSetj.add(atomj);
                for (CMLAtom atomk : ligandListI) {
                    if (usedAtomSetj.contains(atomk))
                        continue;
                    CMLAngle angle = new CMLAngle();
                    // angle.setMolecule(molecule);
                    angle.setAtomRefs3(new String[] { atomj.getId(),
                            atomi.getId(), atomk.getId() });
                    if (calculate) {
                        double angleVal = angle.getCalculatedAngle(molecule);
                        angle.setXMLContent(angleVal);
                    }
                    if (add) {
                        try {
                            molecule.appendChild(angle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    angleVector.add(angle);
                }
            }
        }
        return angleVector;
    }

    /**
     * get all valence torsions for molecule.
     * 
     * @param atomSet
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of torsions (zero length if none)
     */
    public List<CMLTorsion> createValenceTorsions(CMLAtomSet atomSet,
            boolean calculate, boolean add) {
        List<CMLTorsion> torsionVector = new ArrayList<CMLTorsion>();
        for (CMLBond bond : molecule.getBonds()) {
            CMLAtom at0 = bond.getAtom(0);
            if (!atomSet.contains(at0)) {
                continue;
            }
            CMLAtom at1 = bond.getAtom(1);
            if (!atomSet.contains(at1)) {
                continue;
            }

            List<CMLAtom> ligandList0 = at1.getLigandAtoms();
            for (CMLAtom ligand0 : ligandList0) {
                if (!atomSet.contains(ligand0)) {
                    continue;
                }
                if (ligand0.equals(at1)) {
                    continue;
                }
                List<CMLAtom> ligandList1 = at1.getLigandAtoms();
                for (CMLAtom ligand1 : ligandList1) {
                    if (!atomSet.contains(ligand1)) {
                        continue;
                    }
                    if (ligand1.equals(at0)) {
                        continue;
                    }
                    CMLTorsion torsion = new CMLTorsion();
                    torsion.setAtomRefs4(new String[] { ligand0.getId(),
                            at0.getId(), at1.getId(), ligand1.getId() });
                    if (calculate) {
                        double torsionVal = torsion
                                .getCalculatedTorsion(molecule);
                        torsion.setXMLContent(torsionVal);
                    }
                    if (add) {
                        try {
                            molecule.appendChild(torsion);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    torsionVector.add(torsion);
                }
            }
        }
        return torsionVector;
    }

    /** Creates new AtomSet one ligand shell larger than this one.
     * 
     * @return set
     */
    public CMLAtomSet sprout() {
        CMLAtomSet newAtomSet = new CMLAtomSet();
        List<CMLAtom> atoms = atomSet.getAtoms();
        for (CMLAtom atomi : atoms) {
            newAtomSet.addAtom(atomi);
            List<CMLAtom> ligandList = atomi.getLigandAtoms();
            for (CMLAtom ligandj : ligandList) {
                if (!atomSet.contains(ligandj)) {
                    newAtomSet.addAtom(ligandj);
                }
            }
        }
        return newAtomSet;
    }

    /**
     * tests whether an array of atomSets are all different.
     * 
     * assumes all sets are in prioritized order (i.e. simply compares each set
     * with each other without normalization or sorting uses compareTo
     * 
     * @param atomSets
     *            array of ordered atomSets (null returns true)
     * 
     * @return are all sets different (by ordered atom equality)
     */
    public static boolean areOrderedAtomSetsDifferent(CMLAtomSet[] atomSets) {
        boolean different = true;
        if (atomSets != null) {
            for (int i = 0; i < atomSets.length; i++) {
                for (int j = i; j < atomSets.length; j++) {
                    // boolean diff = false;
                    if (atomSets[i] == null || atomSets[j] == null) {
                        throw new CMLRuntimeException("Null atom set component: " + i
                                + "/" + j);
                    } else if (atomSets[i].compareTo(atomSets[j]) == 0) {
                        different = false;
                        break;
                    }
                }
                if (!different) {
                    break;
                }
            }
        }
        return different;
    }

    /**
     * create from an array of molecules. we seem not to be able to use two List<>
     * constructors
     * 
     * @param molecules
     *            the molecules
     * @return atomSet
     */
    // public CMLAtomSet(List<CMLMolecule> molecule) {
    public static CMLAtomSet createAtomSet(List<CMLMolecule> molecules) {
        CMLAtomSet atomSet = new CMLAtomSet();
        for (CMLMolecule mol : molecules) {
            for (CMLAtom atom : mol.getAtoms()) {
                atomSet.addAtom(atom);
            }
        }
        return atomSet;
    }

    /**
     * Wipes parent atom references.
     * 
     * For use with spanning trees.
     * 
     */
    public void resetParents() {
        parentTable = new HashMap<CMLAtom, CMLAtom>();
    }

    /**
     * Sets parent atom reference.
     * 
     * For use with spanning trees.
     * 
     * @param atom
     *            atom whose parent should be set
     * @param parentAtom
     * @throws CMLException
     *             child atom not in atom set
     */
    public void setParent(CMLAtom atom, CMLAtom parentAtom) throws CMLException {
        if (!atomSet.contains(atom)) {
            throw new CMLException("Child atom not in atom set");
        }
        if (parentTable == null) {
            parentTable = new HashMap<CMLAtom, CMLAtom>();
        }
        parentTable.put(atom, parentAtom);
    }

    /**
     * Gets parent atom reference.
     * 
     * For use with spanning trees. Returns null if parent atom is not known.
     * 
     * @param atom
     *            atom whose parent should be found
     * @return parent atom, or null
     * @throws CMLRuntimeException
     *             child atom not in atom set
     */
    public CMLAtom getParent(CMLAtom atom) throws CMLRuntimeException {
        if (!atomSet.contains(atom)) {
            throw new CMLRuntimeException("Child atom not in atom set");
        }
        if (parentTable == null) {
            return null;
        }
        CMLAtom parentAtom = parentTable.get(atom);
        return parentAtom;
    }

    /**
     * get nearest atom.
     * 
     * iterates though this.atoms of same elementType as atom comparing 2D
     * distance to atom.
     * 
     * @param atom
     * @return nearest atom of same type as atom or null
     */
    public CMLAtom getNearestAtom2OfSameElementType(CMLAtom atom) {
        CMLAtom closestAtom = null;
        double maxDist = 999999.;
        if (atom != null) {
            String elementType = atom.getElementType();
            Real2 xy2 = atom.getXY2();
            if (xy2 != null) {
                List<CMLAtom> thisAtoms = atomSet.getAtoms();
                for (int i = 0; i < thisAtoms.size(); i++) {
                    CMLAtom thisAtom = thisAtoms.get(i);
                    if (elementType.equals(thisAtom.getElementType())) {
                        Real2 thisXY2 = thisAtom.getXY2();
                        double dist = thisXY2.getDistance(xy2);
                        if (dist < maxDist) {
                            maxDist = dist;
                            closestAtom = thisAtom;
                        }
                    }
                }
            }
        }
        return closestAtom;
    }

    /**
     * remove any links pointing to atoms which differ in generic ways. this
     * looks awful
     * 
     * @param map
     *            with from links may point
     * @param toAtomSet
     *            to which to links may point
     * @param attribute
     *            such as "elementType", "formalCharge",
     */
    public void removeUnmatchedAtoms(CMLMap map, CMLAtomSet toAtomSet,
            String attribute) {
        Elements links = map.getChildElements(CMLLink.TAG, CML_NS);
        for (int i = 0; i < links.size(); i++) {
            String fromId = ((CMLLink) links.get(i)).getFrom();
            String toId = ((CMLLink) links.get(i)).getTo();
            CMLAtom fromAtom = molecule.getAtomById(fromId);
            CMLAtom toAtom = toAtomSet.getAtomById(toId);
            String fromAttribute = (fromAtom == null) ? null : fromAtom
                    .getAttribute(attribute).getValue();
            String toAttribute = (toAtom == null) ? null : toAtom.getAttribute(
                    attribute).getValue();
            // no match
            if (toAttribute != null && fromAttribute != null
                    && !toAttribute.equals(fromAttribute)) {
                links.get(i).detach();
            }
        }
    }

    /**
     * creates an atomSet of those atoms which have identical 3D coordinates.
     * returns atoms in 'this', i.e. if a1 in this overlaps a2 in otherSet uses
     * a1. If IDs in this clash with atomSet the serialization is fragile
     * 
     * @param otherSet
     *            to compare to
     * @param type
     *            whether cartesian or fractional
     * @return referring to atoms in this which overlap with otherSet. if empty
     *         returns empty atomSet, not null
     */
    public CMLAtomSet getOverlapping3DAtoms(CMLAtomSet otherSet,
            CoordinateType type) {
        CMLAtomSet newAtomSet = new CMLAtomSet();
        for (CMLAtom thisAtom : atomSet.getAtoms()) {
            Point3 thisPoint = thisAtom.getPoint3(type);
            if (thisPoint == null) {
                continue;
            }
            for (CMLAtom otherAtom : otherSet.getAtoms()) {
                Point3 otherPoint = otherAtom.getPoint3(type);
                if (otherPoint == null) {
                    continue;
                }
                if (thisPoint.isEqualTo(otherPoint, EPS)) {
                    newAtomSet.addAtom(thisAtom);
                }
            }
        }
        return newAtomSet;
    }

    /** get nearest atom in atomSet1 to this.
     * may get 1:n and 0:n mappings
     * @param atomSet1
     * @param delta
     * @return mapping
     * @throws CMLException
     */
   public CMLMap matchNearestAtoms(
           CMLAtomSet atomSet1, double delta) throws CMLException {
       CMLMap map = new CMLMap();
       boolean match = false;
       Real2 centroid0 = atomSet.getCentroid2D();
       Real2 centroid1 = atomSet1.getCentroid2D();
       Real2 deltaM = centroid0.subtract(centroid1);
       atomSet1.translate2D(deltaM);
       List<CMLAtom> atoms0 = atomSet.getAtoms();
       List<CMLAtom> atoms1 = atomSet1.getAtoms();
       Map<CMLAtom, CMLAtom> atomMatchTable = new HashMap<CMLAtom, CMLAtom>();
       for (CMLAtom atom0 : atoms0) {
           Real2 point0 = atom0.getXY2();
           for (CMLAtom atom1 : atoms1) {
               if (atomMatchTable.containsKey(atom1)) {
                   continue;
               }
               Real2 point1 = atom1.getXY2();
               double d = point1.getDistance(point0);
               if (d < delta) {
                   atomMatchTable.put(atom0, atom1);
                   break;
               }
           }
       }
       match = true;
       /** not sure this is required...
       for (int i = 0; i < atoms.size(); i++) {
           if (!atomMatchTable.containsKey(atoms[i])) {
               match = false;
           }
       }
       for (int i = 0; i < atoms.size(); i++) {
           if (!atomMatchTable.contains(atoms1[i])) {
               match = false;
           }
       }
       --*/
       if (match) {
           // FIXME
//           map = createMap(atomSet, atomSet1, atomMatchTable);
       } else {
           //            System. out.println("FAILED to match unordered atoms");
       }

       return map;
   }
    
}