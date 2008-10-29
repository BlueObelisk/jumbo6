package org.xmlcml.cml.tools;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.lite.CMLAtom;

/**
 * represents a set of atoms.
 */
/* */public class AtomPair implements CMLConstants {

    CMLAtom[] atoms = new CMLAtom[2];
	double distance2;
	double distance3;
	
	/**
	 * @return the atoms
	 */
	public CMLAtom[] getAtoms() {
		return atoms;
	}

	/**
	 * @param atom0
	 * @param atom1
	 */
	public AtomPair(CMLAtom atom0, CMLAtom atom1) {
		this.atoms = new CMLAtom[2];
		this.atoms[0] = atom0;
		this.atoms[1] = atom1;
	}

	/**
	 * @param distance3 the distance3 to set
	 */
	public void setDistance3(double distance3) {
		this.distance3 = distance3;
	}

	/**
	 * @param distance2 the distance2 to set
	 */
	public void setDistance2(double distance2) {
		this.distance2 = distance2;
	}

	/**
	 * @param dist
	 * @param type
	 */
	public void setDistance(double dist, CoordinateType type) {
		if (type == CoordinateType.TWOD) {
			setDistance2(dist);
		} else if (type == CoordinateType.CARTESIAN) {
				setDistance3(dist);
		}
	}
    /**
     * get first atom.
     * 
     * @return atom
     */
    public CMLAtom getAtom1() {
        return atoms[0];
    }

    /**
     * get second atom.
     * 
     * @return atom
     */
    public CMLAtom getAtom2() {
        return atoms[1];
    }

	/**
	 * @return the distance2
	 */
	public double getDistance2() {
		return distance2;
	}

	/**
	 * @return the distance3
	 */
	public double getDistance3() {
		return distance3;
	}


    /**
     * to string.
     * 
     * @return the string
     */
    public String toString() {
        StringBuffer s = new StringBuffer(S_LCURLY);
        s.append((atoms[0].getId() == null) ? "null" : atoms[0].getId());
        s.append(S_SLASH);
        s.append((atoms[1].getId() == null) ? "null" : atoms[1].getId());
        s.append(S_RCURLY);
        return s.toString();
    }
}
