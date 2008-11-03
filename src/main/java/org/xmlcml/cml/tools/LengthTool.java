package org.xmlcml.cml.tools;



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;

/**
 * tool for managing length
 *
 * @author pmr
 *
 */
public class LengthTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(LengthTool.class.getName());

	CMLLength length = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public LengthTool(CMLLength length) throws RuntimeException {
		init();
		this.length = length;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLLength getLength() {
		return this.length;
	}

    
	/** gets LengthTool associated with length.
	 * if null creates one and sets it in length
	 * @param length
	 * @return tool
	 */
	public static LengthTool getOrCreateTool(CMLLength length) {
		LengthTool lengthTool = (LengthTool) length.getTool();
		if (lengthTool == null) {
			lengthTool = new LengthTool(length);
			length.setTool(lengthTool);
		}
		return lengthTool;
	}

    /** moves atom1 and moveable atomSet.
     * atom0-> atom1 becomes d (content of length)
     * uses explicit atoms rather than atomRefs2
     * @param atom0
     * @param atom1 to be moved
     * @param moveableAtomSet
     */
    public void adjustCoordinates(CMLAtom atom0, CMLAtom atom1, CMLAtomSet moveableAtomSet) {
        if (length.getValue().trim().length() != 0) {
            Double dd = length.getXMLContent();
            double d = dd.doubleValue();
            if (atom0 == null || atom1 == null) {
                throw new RuntimeException("atom(s) in length null");
            }
            Point3 point0 = atom0.getPoint3(CoordinateType.CARTESIAN);
            Point3 point1 = atom1.getPoint3(CoordinateType.CARTESIAN);
            if (point0 == null || point1 == null) {
                throw new RuntimeException("atoms in length have no coordinates");
            }
            Vector3 v = point1.subtract(point0);
            Vector3 v1 = new Vector3(v);
            v1.normalize();
            v1 = v1.multiplyBy(d);
            Vector3 delta = v1.subtract(v);
            moveableAtomSet.translate3D(delta);
        }
    }
    
    /** adjusts coordinates in atomSet to length.
     *
     * @param molecule
     */
    public void adjustCoordinates(CMLMolecule molecule) {
        String[] atomRefs2 = length.getAtomRefs2();
        CMLAtomSet fixedAtomSet = new CMLAtomSet(molecule, atomRefs2);
        CMLAtom atom0 = fixedAtomSet.getAtom(0);
        CMLAtom atom1 = fixedAtomSet.getAtom(1);
        MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
        CMLAtomSet moveableSet = moleculeTool.getDownstreamAtoms(atom1, atom0);
        this.adjustCoordinates(atom0, atom1, moveableSet);
    }


};