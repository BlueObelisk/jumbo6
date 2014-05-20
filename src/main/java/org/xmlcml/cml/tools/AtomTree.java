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
import java.util.Arrays;
import java.util.List;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * An atom-centered tree.
 * 
 * Each tree is composed of subtrees recursively.
 * 
 * AtomTree starts at the atom and recursively adds new ligands, level by level.
 * LigandManager are ordered lexically. At any level the leaves may be equal,
 * but recursion to deeper levels may resolve this and so change the ordering of
 * the atoms (though not of their lexical representations). bond orders are
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
public class AtomTree extends AbstractTool implements Comparable<AtomTree> {

    // the parent of each atomTree. null for top atom. prevents backrecursion
    private CMLAtom parent;
    private CMLAtom atom;

	// child trees
    private List<AtomTree> atomTreeList = null;
    private AtomTree[] atomTree;
    private AtomMatchObject atomMatchObject;
	/**
	 * the maximum level to explore atomTree labelling
	 * 
	 */
	protected int maximumAtomTreeLevel;

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
        atomMatchObject = new AtomMatchObject();
        this.parent = parent;
        this.atom = atom;
        if (atom != null) {
            atomTreeList = new ArrayList<AtomTree>();
            atomMatchObject.setUseCharge(false);
            atomMatchObject.setUseImplicitHydrogens(false);
            atomMatchObject.setExplicitHydrogens(false);
        }
//        molecule = atom.getMolecule();
    }

    /** not directly called. used when AtomTree is
     * primarily a DTO
     */
    private AtomTree() {
    	
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

    /** generate atomtree as DTO (i.e. not based on atom)
     * 
     * @return
     */
    public static AtomTree createDefaultAtomTree() {
    	return new AtomTree();
    }
    
    /**
     * is charge to be included in string.
     * 
     * @param ch
     *            true if charge included (default false)
     */
    public void setUseCharge(final boolean ch) {
        atomMatchObject.setUseCharge(ch);
    }

    /**
     * is atom label to be included in string.
     * 
     * @param lab
     *            true if label included (default false)
     */
    public void setUseLabel(final boolean lab) {
    	atomMatchObject.setUseLabel(lab);
    }

    public int getAtomTreeLevel() {
		return atomMatchObject.getAtomTreeLevel();
	}

	public void setAtomTreeLevel(int atomTreeLevel) {
		atomMatchObject.setAtomTreeLevel(atomTreeLevel);
	}

    /**
     * are implicit hydrogens to be included in string.
     * 
     * @param hyd
     *            true if implict hydrogens used (default false)
     */
    public void setUseImplicitHydrogens(final boolean hyd) {
    	atomMatchObject.setUseImplicitHydrogens(hyd);
    }

    /**
     * are explicit hydrogens to be included in tree.
     * 
     * @param hyd
     *            true if explict hydrogens used (default false)
     */
    public void setUseExplicitHydrogens(final boolean hyd) {
    	atomMatchObject.setUseExplicitHydrogens(hyd);
    }

    /**
     * add layers of atomTrees. //FIXME - has hydrogen logic hardcoded
     * 
     * @param level
     *            number of levels (0 = bare atom)
     */
    public void expandTo(final int level) {
    	atomMatchObject.setAtomTreeLevel(level);
        if (atom != null && level > 0) {
            List<CMLAtom> ligandList = atom.getLigandAtoms();
            for (CMLAtom ligand : ligandList) {
                if (ligand != this.parent
                        && (atomMatchObject.isUseExplicitHydrogens() || !AS.H.equals(ligand.getElementType()))) {
                    AtomTree ligandTree = new AtomTree(this.atom, ligand);
                    ligandTree.setAtomMatchObject(this.atomMatchObject);
                    ligandTree.setUseCharge(atomMatchObject.isUseCharge());
//                    ligandTree.setUseLabel(label);
//                    ligandTree.setUseImplicitHydrogens(implicitHydrogens);
//                    ligandTree.setUseExplicitHydrogens(explicitHydrogens);
                    atomTreeList.add(ligandTree);
                    ligandTree.expandTo(level - 1);
                }
            }
            atomTree = (AtomTree[]) atomTreeList.toArray(new AtomTree[0]);
            Arrays.sort(atomTree);
        }
    }

    private void setAtomMatchObject(AtomMatchObject atomMatchObject) {
    	this.atomMatchObject = atomMatchObject;
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
    public int compareTo(AtomTree o) {
        return this.toString().compareTo(o.toString());
    }

    /** finds maximum depth of atomTreeString
     * 
     * @param s
     * @return
     */
    public static int getLevelOfAtomTreeString(String s) {
		int maxDepth = -1;
    	if (s != null) {
    		maxDepth = 0;
    		int depth = 0;
    		for (int i = 0; i < s.length(); i++) {
    			char c = s.charAt(i);
    			if (c == CMLConstants.C_LBRAK) {
    				depth++;
    				if (maxDepth < depth) {
    					maxDepth = depth;
    				}
    			} else if (c == CMLConstants.C_RBRAK) {
    				depth--;
    			}
    		}
    	}
    	return maxDepth;
    }
    
    /** removes leaves and branches until tree is of
     * specified depth. No effect is depth is already that
     * size or smaller
     * @param s
     * @param level
     * @return
     */
    public static String trimToLevel(String s, int level) {
    	if (s == null) return null;
    	StringBuilder sb = new StringBuilder();
    	int depth = 0;
    	for (int i = 0; i < s.length(); i++) {
    		char c = s.charAt(i);
			if (c == CMLConstants.C_LBRAK) {
				if (depth++ <= level-1) {
					sb.append(c);
				}
			} else if (c == CMLConstants.C_RBRAK) {
				if (--depth <= level-1) {
					sb.append(c);
				}
			} else if (depth <= level) {
				sb.append(c);
			}
    	}
    	return sb.toString();
    }
    
    public static ChemicalElement getRootElement(String atomTreeString) {
    	String s = AtomTree.trimToLevel(atomTreeString, 0);
    	return ChemicalElement.getChemicalElement(s);
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
        if (atomMatchObject.isUseExplicitHydrogens() || !AS.H.equals(elType)) {
            s.append(elType);
            if (atomMatchObject.isUseLabel()) {
                CMLLabel childLabel = (CMLLabel) atom.getFirstChildElement(
                        "label", CMLConstants.CML_NS);
                if (childLabel != null) {
                    s.append(S_LCURLY);
                    s.append(childLabel.getValue());
                    s.append(S_RCURLY);
                }
            }
            if (atom.getHydrogenCountAttribute() != null) {
                int hCount = atom.getHydrogenCount();
                if (atomMatchObject.isUseImplicitHydrogens() && hCount > 0) {
                    s.append(AS.H.value);
                    s.append(((hCount == 1) ? CMLConstants.S_EMPTY : CMLConstants.S_EMPTY + hCount));
                }
            }

            if (atomMatchObject.isUseCharge() & atom.getFormalChargeAttribute() != null) {
                int ch = atom.getFormalCharge();
                int nch = (ch > 0) ? ch : -ch;
                if (ch != 0) {
                    String chS = (ch > 0) ? CMLConstants.S_PLUS : CMLConstants.S_MINUS;
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

	public static IntMatrix createSimilarityMatrix(
			List<String> sortedAtomTreeStringi,
			List<String> sortedAtomTreeStringj) {
		int maxLen = -1;
		int rows = sortedAtomTreeStringi.size();
		int cols = sortedAtomTreeStringj.size();
		IntMatrix intMatrix = new IntMatrix(rows, cols);
		for (int irow = 0; irow < rows; irow++) {
			String si = sortedAtomTreeStringi.get(irow);
			for (int jcol = 0; jcol < cols; jcol++) {
				String sj = sortedAtomTreeStringj.get(jcol);
				int compare = maximumCommonLevel(si, sj);
				if (compare > maxLen) maxLen = compare;
				intMatrix.setElementAt(irow, jcol, compare);
			}
		}
		return intMatrix;
	}

	public static int maximumCommonLevel(String si, String sj) {
		int leni = getLevelOfAtomTreeString(si);
		int lenj = getLevelOfAtomTreeString(sj);
		int len = Math.min(leni, lenj);
		while (len >= 0) {
			String sii = trimToLevel(si, len);
			String sjj = trimToLevel(sj, len);
			if (sii.equals(sjj)) {
				return len;
			}
			len--;
		}
		return len;
	}

}
