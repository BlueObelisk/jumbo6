package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector3;

/**
 * tool for managing angles
 *
 * @author pmr
 *
 */
public class AngleTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(AngleTool.class);

	CMLAngle angle = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public AngleTool(CMLAngle angle) throws RuntimeException {
		init();
		this.angle = angle;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLAngle getAngle() {
		return this.angle;
	}

    
	/** gets AngleTool associated with angle.
	 * if null creates one and sets it in angle
	 * @param angle
	 * @return tool
	 */
	public static AngleTool getOrCreateTool(CMLAngle angle) {
		AngleTool angleTool = (angle == null) ? null : (AngleTool) angle.getTool();
		if (angleTool == null) {
			angleTool = new AngleTool(angle);
			angle.setTool(angleTool);
		}
		return angleTool;
	}

  /** adjusts coordinates in molecule to torsion angle.
   * moves atoms downstream of atom0/atom1
   * @param molecule
   */
  public void adjustCoordinates(CMLMolecule molecule) {
      String[] atomRefs3 = angle.getAtomRefs3();
      CMLAtomSet fixedAtomSet = new CMLAtomSet(molecule, atomRefs3);
      CMLAtom atom0 = fixedAtomSet.getAtom(0);
      CMLAtom atom1 = fixedAtomSet.getAtom(1);
      MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
      CMLAtomSet moveableSet = moleculeTool.getDownstreamAtoms(atom1, atom0);
      adjustCoordinates(fixedAtomSet, moveableSet);
  }

  /** applies transformation to reset angle.
  *
  * @param angle
  * @param atomSet to define torsion (could be the molecule)
  * @param moveableSet set of atoms which can be moved
  *   (normally those downstream of the rotatable bond)
  *   coordinates of these atoms will be altered
  */
 public void adjustCoordinates(Angle angle, CMLAtomSet atomSet, CMLAtomSet moveableSet) {
     // make sure there are exactly 3 atoms in order
     CMLAtomSet atom3Set = atomSet.getAtomSetById(this.angle.getAtomRefs3());
     CMLTransform3 transform = this.getTransformationToNewAngle(
             angle, atom3Set.getAtoms());
     AtomSetTool.getOrCreateTool(moveableSet).transformCartesians(transform);
 }

 /** applies transformation to reset torsion angle.
  *
  * @param amount
  * @param atomSet to define torsion (could be the molecule)
  * @param moveableSet set of atoms which can be moved
  *   (normally those downstream of the rotatable bond)
  *   coordinates of these atoms will be altered
  * @exception RuntimeException bad value for angle
  */
 void adjustCoordinates(CMLAtomSet atomSet, CMLAtomSet moveableSet)
     throws RuntimeException {
     if (angle.getValue().trim().length() == 0) {
         //
     } else {
         Double d = angle.getXMLContent();
         if (!Double.isNaN(d)) {
             Angle angle = new Angle(d, Angle.Units.DEGREES);
             // make sure there are exactly 3 atoms in order
             CMLAtomSet atom3Set = atomSet.getAtomSetById(this.angle.getAtomRefs3());
             CMLTransform3 transform = this.getTransformationToNewAngle(
                     angle, atom3Set.getAtoms());
             AtomSetTool.getOrCreateTool(moveableSet).transformCartesians(transform);
         }
     }
 }


 /** calculates transformation to reset angle.
  * T1 = translateion of atom2 to origin
  * R = rotation
  * T1' = -T1
  *
  * T = T1' * R * T1
  * @param angle
  * @param atomSet of 3 atoms a1 - a2 - a3
  * @return the transform (or null if problems)
  */
 private CMLTransform3 getTransformationToNewAngle(Angle angle, List<CMLAtom> atomList) {
     if (atomList == null) {
    	 throw new RuntimeException("Null atomList");
     }
     if (atomList.size() != 3) {
         throw new RuntimeException("Must have 3 atoms in angle: was "+atomList.size());
     }
     // NOT YET WORKING - REQUIRES TESTING
     Transform3 transform = null;
     double ang0 = this.angle.getCalculatedAngle(atomList);
     CMLAtom atom0 = atomList.get(0);
     CMLAtom atom1 = atomList.get(1);
     CMLAtom atom2 = atomList.get(2);
     // get cross product
     Vector3 v10 = atom0.getVector3(atom1);
     Vector3 v12 = atom2.getVector3(atom1);
     Vector3 vcross = v10.cross(v12);
     vcross = vcross.normalize();
     Point3 point1 = atom1.getPoint3(CoordinateType.CARTESIAN);
     double delta = angle.getDegrees() - ang0;
     // translate moveable atoms to origin
     Vector3 v2 = new Point3(0.0, 0.0, 0.0).subtract(point1);
     Transform3 t1 = new Transform3(v2);
     Transform3 t1prime = new Transform3(v2.negative());
     Transform3 r = new Transform3(vcross, new Angle(delta, Angle.Units.DEGREES));
     transform = r.concatenate(t1);
     // translate back
     transform = t1prime.concatenate(transform);
     return new CMLTransform3(transform);
 }

 /**
  * gets atoms as array of atoms.
  *
  * @param atomSet
  * @return the atoms (null if no atomRefs3)
  */
 public List<CMLAtom> getAtoms(CMLAtomSet atomSet) {
     List<CMLAtom> atomList = null;
     if (atomSet != null) {
         String[] atomIds = angle.getAtomRefs3();
         if (atomIds != null && atomIds.length == 3) {
             atomList = new ArrayList<CMLAtom>();
             for (String atomId : atomIds) {
                 CMLAtom atom = atomSet.getAtomById(atomId);
                 if (atom == null) {
                     throw new RuntimeException("cannot find atom " + atomId);
                 }
                 atomList.add(atom);
             }
         }
     }
     return atomList;
 }


};