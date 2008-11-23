package org.xmlcml.cml.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector3;

/**
 * tool for managing torsion
 *
 * @author pmr
 *
 */
public class TorsionTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(TorsionTool.class.getName());

	CMLTorsion torsion = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public TorsionTool(CMLTorsion torsion) throws RuntimeException {
		init();
		this.torsion = torsion;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLTorsion getTorsion() {
		return this.torsion;
	}

    
	/** gets TorsionTool associated with torsion.
	 * if null creates one and sets it in torsion
	 * @param torsion
	 * @return tool
	 */
	public static TorsionTool getOrCreateTool(CMLTorsion torsion) {
		TorsionTool torsionTool = null;
		if (torsion != null) {
			torsionTool = (TorsionTool) torsion.getTool();
			if (torsionTool == null) {
				torsionTool = new TorsionTool(torsion);
				torsion.setTool(torsionTool);
			}
		}
		return torsionTool;
	}

  /** adjusts coordinates in atomSet to torsion angle.
   *
   * @param molecule
   */
  public void adjustCoordinates(CMLMolecule molecule) {
      String[] atomRefs4 = torsion.getAtomRefs4();
      CMLAtomSet fixedAtomSet = new CMLAtomSet(molecule, atomRefs4);
      CMLAtom atom0 = fixedAtomSet.getAtom(1);
      CMLAtom atom1 = fixedAtomSet.getAtom(2);
      MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
      CMLAtomSet moveableSet = moleculeTool.getDownstreamAtoms(atom1, atom0);
      adjustCoordinates(fixedAtomSet, moveableSet);
  }

  /** applies transformation to reset torsion angle.
   *
   * @param angle
   * @param atomSet to define torsion (could be the molecule)
   * @param moveableSet set of atoms which can be moved
   *   (normally those downstream of the rotatable bond)
   *   coordinates of these atoms will be altered
   */
  void adjustCoordinates(CMLAtomSet atomSet, CMLAtomSet moveableSet) {
      if (torsion.getValue().trim().length() != 0) {
          Double d = torsion.getXMLContent();
          if (!Double.isNaN(d)) {
              Angle angle = new Angle(d, Angle.Units.DEGREES);
              // make sure there are exactly 4 atoms in order
              CMLAtomSet atom4Set = atomSet.getAtomSetById(torsion.getAtomRefs4());
              CMLTransform3 transform = this.getTransformationToNewTorsion(
                      angle, atom4Set);
              AtomSetTool.getOrCreateTool(moveableSet).transformCartesians(transform);
          }
      }
  }


  /** applies transformation to reset torsion angle.
   *
   * @param angle
   * @param atomSet to define torsion (could be the molecule)
   * @param moveableSet set of atoms which can be moved
   *   (normally those downstream of the rotatable bond)
   *   coordinates of these atoms will be altered
   */
  public void adjustCoordinates(Angle angle, CMLAtomSet atomSet, CMLAtomSet moveableSet) {
      // make sure there are exactly 4 atoms in order
      CMLAtomSet atom4Set = atomSet.getAtomSetById(torsion.getAtomRefs4());
      CMLTransform3 transform = this.getTransformationToNewTorsion(
              angle, atom4Set);
      AtomSetTool.getOrCreateTool(moveableSet).transformCartesians(transform);
  }

	    /** calculates transformation to reset torsion angle.
	     * T1 = translateion of atom2 to origin
	     * R = rotation
	     * T1' = -T1
	     *
	     * T = T1' * R * T1
	     * @param angle
	     * @param atomSet of 4 atoms
	     * @return the transform (or null if problems)
	     */
	    public CMLTransform3 getTransformationToNewTorsion(Angle angle, CMLMolecule molecule) {
	    	CMLAtomSet atom4Set = new CMLAtomSet(molecule, torsion.getAtomRefs4());
	    	return this.getTransformationToNewTorsion(angle, atom4Set);
	    }
	    
	    /** calculates transformation to reset torsion angle.
	     * T1 = translateion of atom2 to origin
	     * R = rotation
	     * T1' = -T1
	     *
	     * T = T1' * R * T1
	     * @param angle
	     * @param atomSet of 4 atoms
	     * @return the transform (or null if problems)
	     */
	    public CMLTransform3 getTransformationToNewTorsion(Angle angle, CMLAtomSet atom4Set) {
	    	if (atom4Set == null || atom4Set.size() != 4) {
	    		throw new RuntimeException("must have 4 atoms in set");
	    	}
	        Transform3 transform = null;
	        double tor0 = torsion.getCalculatedTorsion(atom4Set.getAtoms());
	        List<CMLAtom> atoms = torsion.getAtoms(atom4Set);
	        CMLAtom atom1 = atoms.get(1);
	        CMLAtom atom2 = atoms.get(2);
	        Point3 point2 = atom2.getPoint3(CoordinateType.CARTESIAN);
	        double delta = tor0 - angle.getDegrees();
	        // translate moveable atoms to origin
	        Vector3 v2 = new Point3(0.0, 0.0, 0.0).subtract(point2);
	        Transform3 t1 = new Transform3(v2);
	        Transform3 t1prime = new Transform3(v2.negative());
	        Vector3 v = atom2.getVector3(atom1);
	        Transform3 r = new Transform3(v, new Angle(delta, Angle.Units.DEGREES));
	        transform = r.concatenate(t1);
	        // translate back
	        transform = t1prime.concatenate(transform);
	        return new CMLTransform3(transform);

	    }


};