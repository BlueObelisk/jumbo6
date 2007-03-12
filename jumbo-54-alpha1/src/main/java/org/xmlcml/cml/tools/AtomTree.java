package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * An atom-centered tree.
 * 
 * Each tree is composed of subtrees recursively.
 * 
 * AtomTree starts at the atom and recursively adds new ligands, level by level.
 * LigandManager are ordered lexically. At any level the leaves may be equal,
 * but recursion to deeper levels may resolve this and so change the ordering of
 * the atoms (though not of their lexical reprsentations). bond orders are
 * ignored (InChI-like). <br/> The following are supported and can be used in
 * any combination:
 * <ul>
 * <li>formal charges</li>
 * <li>atom labels</li>
 * <li>implicit hydrogens</li>
 * <li>explicit hydrogens</li>
 * </ul>
 * 
 * <pre>
 *  thus for DMF - HC(O)N(C(H)(H)(H))(C(H)(H)(H))
 *  the carbon has the lexical representations
 *  level 0 C
 *  level 1 C(H)(O)(N)
 *  level 2 C(H)(O)(N(C)(C))
 *  level 3 C(H)(O)(N(C(H)(H)(H))(C(H)(H)(H))
 * </pre>
 * 
 * Typical usage:
 * 
 * <pre>
 *  CMLAtom atom = ...
 *  AtomTree atomTree = new AtomTree(atom);
 *  atomTree.setUseCharges(true)
 * </pre>
 * 
 * <br/>
 * 
 */
public class AtomTree extends AbstractTool implements Comparable {

    // the parent of each atomTree. null for top atom. prevents backrecursion
    CMLAtom parent;

    CMLAtom atom;

    List<CMLAtom> ligandList;

    // child trees
    List<AtomTree> atomTreeList = null;

    AtomTree[] atomTree;

    boolean charge = false;

    boolean implicitHydrogens = false;

    boolean explicitHydrogens = false;

    boolean label = false;

    CMLMolecule molecule;

    MoleculeTool moleculeTool;

    /**
     * create from given atom and parent.
     * 
     * @param parent
     *            either ligand or atom or null; if not a ligand, will be set to
     *            null
     * @param atom
     *            next node of recursive tree; if null no action
     */
    public AtomTree(CMLAtom parent, CMLAtom atom) {
        super();
        this.parent = parent;
        this.atom = atom;
        if (atom != null) {
            atomTreeList = new ArrayList<AtomTree>();
            charge = false;
            implicitHydrogens = false;
            explicitHydrogens = false;
        }
        molecule = atom.getMolecule();
        moleculeTool = new MoleculeTool(molecule);
    }

    /**
     * create from atom without parent.
     * 
     * @param atom
     *            root node
     */
    public AtomTree(CMLAtom atom) {
        this(null, atom);
    }

    /**
     * is charge to be included in string.
     * 
     * @param ch
     *            true if charge included (default false)
     */
    public void setUseCharge(final boolean ch) {
        this.charge = ch;
    }

    /**
     * is atom label to be included in string.
     * 
     * @param lab
     *            true if label included (default false)
     */
    public void setUseLabel(final boolean lab) {
        this.label = lab;
    }

    /**
     * are implicit hydrogens to be included in string.
     * 
     * @param hyd
     *            true if implict hydrogens used (default false)
     */
    public void setUseImplicitHydrogens(final boolean hyd) {
        implicitHydrogens = hyd;
    }

    /**
     * are explicit hydrogens to be included in tree.
     * 
     * @param hyd
     *            true if explict hydrogens used (default false)
     */
    public void setUseExplicitHydrogens(final boolean hyd) {
        explicitHydrogens = hyd;
    }

    /**
     * add layers of atomTrees. //FIXME - has hydrogen logic hardcoded
     * 
     * @param level
     *            number of levels (0 = bare atom)
     */
    public void expandTo(final int level) {
        if (atom != null && level > 0) {
            List<CMLAtom> ligandList = atom.getLigandAtoms();
            for (CMLAtom ligand : ligandList) {
                if (ligand != this.parent
                        && (explicitHydrogens || !ligand.getElementType()
                                .equals("H"))) {
                    AtomTree ligandTree = new AtomTree(this.atom, ligand);
                    ligandTree.setUseCharge(charge);
                    ligandTree.setUseLabel(label);
                    ligandTree.setUseImplicitHydrogens(implicitHydrogens);
                    ligandTree.setUseExplicitHydrogens(explicitHydrogens);
                    atomTreeList.add(ligandTree);
                    ligandTree.expandTo(level - 1);
                }
            }
            atomTree = (AtomTree[]) atomTreeList.toArray(new AtomTree[0]);
            Arrays.sort(atomTree);
        }
    }

    /**
     * compares atomTrees by lexical representation.
     * 
     * @param o
     *            the atomTree to compare
     * @return this.toString().compareTo(o.toString())
     * @throws ClassCastException
     *             o is not an AtomTree
     */
    public int compareTo(final Object o) throws ClassCastException {
        // AtomTree atomTree = (AtomTree) o;
        return this.toString().compareTo(o.toString());
    }

    /**
     * string representation. sorted recursive levels, enclosed in (...) atoms
     * have charges and hydrogens as set by flags
     * 
     * @return string
     */
    public String toString() {
        StringBuffer s = new StringBuffer(S_EMPTY);
        String elType = atom.getElementType();
        if (explicitHydrogens || !elType.equals("H")) {
            s.append(elType);
            if (label) {
                CMLLabel childLabel = (CMLLabel) atom.getFirstChildElement(
                        "label", CML_NS);
                if (childLabel != null) {
                    s.append(S_LCURLY);
                    s.append(childLabel.getValue());
                    s.append(S_RCURLY);
                }
            }
            if (atom.getHydrogenCountAttribute() != null) {
                int hCount = atom.getHydrogenCount();
                if (implicitHydrogens && hCount > 0) {
                    s.append("H");
                    s.append(((hCount == 1) ? S_EMPTY : S_EMPTY + hCount));
                }
            }

            if (charge & atom.getFormalChargeAttribute() != null) {
                int ch = atom.getFormalCharge();
                int nch = (ch > 0) ? ch : -ch;
                if (ch != 0) {
                    String chS = (ch > 0) ? S_PLUS : S_MINUS;
                    for (int i = 0; i < nch; i++) {
                        s.append(chS);
                    }
                }
            }
            if (atomTree != null) {
                for (int i = 0; i < atomTree.length; i++) {
                    s.append(S_LBRAK);
                    s.append(atomTree[i].toString());
                    s.append(S_RBRAK);
                }
            }
        }
        return s.toString();
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
    public static Map<String, Object> getAtomTreeLabelling(CMLAtomSet atomSet,
            AtomMatcher atomMatcher) {
        // FIXME this is broken - does not do atomsets
        Map<String, Object> atomMap = null;
        Map<String, Object> uniqueAtomMap = new HashMap<String, Object>();
        // iterate through levels
        List<CMLAtom> atoms = atomSet.getAtoms();
        int atomTreeLevel = atomMatcher.getAtomTreeLevel();
        boolean variableLevel = (atomTreeLevel < 0);
        int startLevel = (variableLevel) ? 0 : atomTreeLevel;
        int endLevel = (variableLevel) ? atomMatcher.maximumAtomTreeLevel
                : atomTreeLevel + 1;
        for (int level = startLevel; level < endLevel; level++) {
            atomMap = new HashMap<String, Object>();
            for (int i = 0; i < atoms.size(); i++) {
                CMLAtom atom = atoms.get(i);
                boolean omit = false;
                String elementType = atom.getElementType();
                for (int j = 0; j < atomMatcher.excludeElementTypes.length; j++) {
                    if (atomMatcher.excludeElementTypes[j].equals(elementType)) {
                        omit = true;
                        break;
                    }
                }
                if (!omit) {
                    // atom still not unique?
                    if (!variableLevel || !uniqueAtomMap.containsValue(atom)) {
                        AtomTree atomTree = new AtomTree(atom);
                        atomTree.setUseCharge(atomMatcher.isUseCharge());
                        atomTree.setUseLabel(atomMatcher.isUseLabel());
                        atomTree.setUseExplicitHydrogens(elementType
                                .equals("H"));
                        atomTree.expandTo(level);
                        String atval = atomTree.toString();
                        if (variableLevel) {
                            // logger.info((CMLAtom)atoms.get(i).getId()+"....."+atval);
                        }
                        List<CMLAtom> atomList = (List<CMLAtom>) atomMap.get(atval);
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
            List<CMLAtom> atomList = (List<CMLAtom>) atomMap.get(atomTreeString);
            // just mapped?
            if (atomList != null && atomList.size() == 1) {
                CMLAtom atom = atomList.get(0);
                finalMap.put(atomTreeString, atom);
            } else {
                CMLAtomSet atomSet1 = new CMLAtomSet(atomList);
                finalMap.put(atomTreeString, atomSet1);
            }
        }
        // }

        return finalMap;
    }

}
