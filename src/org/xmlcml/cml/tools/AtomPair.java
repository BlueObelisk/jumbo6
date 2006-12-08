package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLAtom;

/**
 * represents a set of atoms.
 */
/* */public class AtomPair {

    CMLAtom[] atom = new CMLAtom[2];

    /**
     * constructor.
     * 
     * @param at1
     * @param at2
     */
    public AtomPair(CMLAtom at1, CMLAtom at2) {
        super();
        atom[0] = at1;
        atom[1] = at2;
    }

    /**
     * get first atom.
     * 
     * @return atom
     */
    public CMLAtom getAtom1() {
        return atom[0];
    }

    /**
     * get second atom.
     * 
     * @return atom
     */
    public CMLAtom getAtom2() {
        return atom[1];
    }

    /**
     * get both atoms.
     * 
     * @return atoms
     */
    public CMLAtom[] getAtoms() {
        return atom;
    }

    /**
     * to string.
     * 
     * @return the string
     */
    public String toString() {
        StringBuffer s = new StringBuffer("{");
        s.append((atom[0].getId() == null) ? "null" : atom[0].getId());
        s.append("/");
        s.append((atom[1].getId() == null) ? "null" : atom[1].getId());
        s.append("}");
        return s.toString();
    }
}
