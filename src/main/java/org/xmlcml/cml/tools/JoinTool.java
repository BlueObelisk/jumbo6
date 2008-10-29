package org.xmlcml.cml.tools;

import java.util.List;

import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLAtomArray;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLLabel;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.main.CMLAtomSet;
import org.xmlcml.cml.element.main.CMLJoin;
import org.xmlcml.cml.element.main.CMLLength;
import org.xmlcml.cml.element.main.CMLTorsion;
import org.xmlcml.cml.element.main.CMLJoin.MoleculePointer;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector3;

/**
 * tool for managing join
 *
 * @author pmr
 *
 */
public class JoinTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(JoinTool.class.getName());

	CMLJoin join = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public JoinTool(CMLJoin join) throws RuntimeException {
		init();
		this.join = join;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLJoin getJoin() {
		return this.join;
	}

    
	/** gets JoinTool associated with join.
	 * if null creates one and sets it in join
	 * @param join
	 * @return tool
	 */
	public static JoinTool getOrCreateTool(CMLJoin join) {
		JoinTool joinTool = (JoinTool) join.getTool();
		if (joinTool == null) {
			joinTool = new JoinTool(join);
			join.setTool(joinTool);
		}
		return joinTool;
	}

  /** join the two R groups in atomRefs2.
   * first flattens subMolecules if not already done
   * finds the (single) atom attached to each R group
   * and deletes the R groups.
   * removes R groups from torsions, angles, lengths
   *
   * In some cases where two atoms are joined the torsion may be ambiguous
   * e.g. join a,b where b has ligands c,d
   * if takeLigandWithLowestId is true then the ligand with lowest id (e.g. c)
   * is taken. If false CMLRuntime is thrown
   *
   * will also set torsions, etc. if the molecule has coordinates
   *
   * @param takeLigandWithLowestId used to decide which fragment is rotated
   * when adjusting torsions
   *
   * @throws RuntimeException if groups are not R or other problems
   */
  private void joinByAtomRefs2AndAdjustGeometry(
  		CMLMolecule staticMolecule, CMLAtomSet movableAtomSet,
          boolean takeLigandWithLowestId) throws RuntimeException {
      String[] atomRefs2 = join.getAtomRefs2();
      if (atomRefs2 == null) {
          throw new RuntimeException("missing atomRefs2 attribute");
      }
      String staticAtomId = CMLUtil.getLocalName(atomRefs2[0]);
      CMLAtom staticAtom =  staticMolecule.getAtomArray().getAtomById(staticAtomId);
      if (staticAtom == null) {
          throw new RuntimeException("Cannot find existing atom: "+staticAtomId);
      }
      String movableAtomId = CMLUtil.getLocalName(atomRefs2[1]);
      CMLAtom movableAtom = movableAtomSet.getAtomById(movableAtomId);
      if (movableAtom == null) {
          throw new RuntimeException("Cannot find movable atom: "+movableAtomId);
      }
      CMLAtom atom0 = AtomTool.getOrCreateTool(staticAtom).getSingleLigand();
      CMLAtom atom1 = AtomTool.getOrCreateTool(movableAtom).getSingleLigand();

      CMLMolecule molecule = atom0.getMolecule();
      if (molecule == null) {
          throw new RuntimeException("no owner molecule: "+atom0.getId());
      }

      CMLMolecule molecule1 = atom1.getMolecule();
      if (molecule1 == null) {
          throw new RuntimeException("no owner molecule: "+atom1.getId());
      } else if (molecule != molecule1) {
          throw new RuntimeException("atoms not in same molecule; should have been flattened");
      }

      // create bond
      createAndAddBond(staticMolecule, atom0, atom1);

      removeOldElements(staticMolecule, staticAtom, movableAtom, atom0, atom1);

      MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
      CMLAtomSet moleculeAtomSet = moleculeTool.getAtomSet();
      CMLAtomSet moveableAtomSet = moleculeTool.getDownstreamAtoms(atom1, atom0);

      adjustTorsion(staticAtom, atom0, atom1, movableAtom, moleculeAtomSet, moveableAtomSet);
      adjustLength(atom0, atom1, moveableAtomSet);

  }

  private void adjustLength(CMLAtom atom0, CMLAtom atom1, CMLAtomSet moveableAtomSet) {
      List<Node> lengths = CMLUtil.getQueryNodes(join, CMLLength.NS, CML_XPATH);
      if (lengths.size() == 1) {
          CMLLength length = (CMLLength) lengths.get(0);
          length.setAtomRefs2(atom0, atom1);
          LengthTool lengthTool = LengthTool.getOrCreateTool(length);
          lengthTool.adjustCoordinates(atom0, atom1, moveableAtomSet);
      }
  }

  private void adjustTorsion(
          CMLAtom rGroup0, CMLAtom atom0, CMLAtom atom1, CMLAtom rGroup1,
          CMLAtomSet moleculeAtomSet, CMLAtomSet moveableAtomSet) {
      List<Node> torsions = CMLUtil.getQueryNodes(join, CMLTorsion.NS, CML_XPATH);
      if (torsions.size() == 1) {
          CMLTorsion torsion = (CMLTorsion) torsions.get(0);
          CMLAtom atom00 = this.getUniqueLigand(rGroup0, atom0, atom1);
          if (atom00 == null && atom0.getLigandAtoms().size() > 1) {
              ((CMLElement)atom0.getParent()).debug("PPP");
              throw new RuntimeException("Null ligand on "+atom0.getId()+" maybe needs a label for: "+rGroup0.getId());
          }
          CMLAtom atom11 = this.getUniqueLigand(rGroup1, atom1, atom0);
          if (atom11 == null && atom1.getLigandAtoms().size() > 1) {
//              ((CMLElement)atom1.getParent()).debug("PPP");
              throw new RuntimeException(
          		"Null ligand on "+atom1.getId()+" maybe needs a label for: "+rGroup1.getId()+"\n" +
  				" find a ligand of "+atom1.getId()+" and give it a child ligand of the form:" +
 			        "<label dictRef='cml:torsionEnd'>"+rGroup1.getId()+"</label> (just the last component)");
          }
          if (atom00 != null && atom11 != null) {
              torsion.setAtomRefs4(atom00, atom0, atom1, atom11);
              TorsionTool torsionTool = TorsionTool.getOrCreateTool(torsion);
              torsionTool.adjustCoordinates(moleculeAtomSet, moveableAtomSet);
          }
      }
  }

  private CMLAtom getUniqueLigand(CMLAtom rGroup0, CMLAtom atom0, CMLAtom atom1) {
      List<CMLAtom> ligands = atom0.getLigandAtoms();
      if (!ligands.contains(atom1)) {
          throw new RuntimeException("ligands should contain "+atom1.getId());
      }
      for (CMLAtom ligand : ligands) {
          if (ligand.equals(atom1)) {
              //
          } else if (ligands.size() == 2) {
              return ligand;
          } else {
              List<Node> labels = CMLUtil.getQueryNodes(
                      ligand, CMLJoin.TORSION_END_QUERY, CML_XPATH);
              // there may be multiple labels
              for (Node node : labels) {
                  CMLLabel label = (CMLLabel) node;
                  String labelS = label.getValue();
                  String rId = truncate(rGroup0.getId());
                  if (labelS.equals(rId)) {
                      return ligand;
                  }
              }
          }
      }
      return null;
  }

  private String truncate(String id) {
      int idx = id.lastIndexOf(S_UNDER);
      return id.substring(idx+1);
  }

  private CMLBond createAndAddBond(CMLMolecule parentMolecule, CMLAtom atom0, CMLAtom atom1) {
      CMLBond bond = new CMLBond(atom0, atom1);
      if (join.getId() != null) {
          bond.setId(join.getId());
      } else {
//          System.out.println("WARNING: join should have id: ");
      }
      String order = join.getOrder();
      if (order != null && !order.trim().equals(S_EMPTY)) {
          bond.setOrder(order);
      }
      // add bond to molecule
      parentMolecule.addBond(bond);
      return bond;
  }

  private void removeOldElements(
          CMLMolecule parentMolecule,
          CMLAtom rGroup0,
          CMLAtom rGroup1,
          CMLAtom atom0,
          CMLAtom atom1
          ) {
      // remove join
      join.detach();
      // remove RGroups and their bonds
      rGroup0.detach();
      // make sure children (such as labels) are transferred
      CMLUtil.transferChildren(rGroup0, atom1);
      rGroup1.detach();
      CMLUtil.transferChildren(rGroup1, atom0);
      CMLBond bond0 = parentMolecule.getBond(atom0, rGroup0);
      if (bond0 == null) {
      } else {
          bond0.detach();
      }
      CMLBond bond1 = parentMolecule.getBond(atom1, rGroup1);
      if (bond1 == null) {
      } else {
          bond1.detach();
      }
  }
  
  /** join one molecule to another.
   * manages the XML but not yet the geometry
   * @param existingMolecule
   * @param addedAtomSet to be joined
   * @param takeAtomWithLowestId
   */
  public void addMoleculeTo(
          CMLMolecule existingMolecule, CMLAtomSet addedAtomSet,
          boolean takeAtomWithLowestId) {
	        this.alignAndMoveBonds(existingMolecule, addedAtomSet);
	        this.joinByAtomRefs2AndAdjustGeometry(
      		existingMolecule, addedAtomSet, takeAtomWithLowestId);
  }

  private void alignAndMoveBonds(CMLMolecule existingMolecule, CMLAtomSet addedAtomSet) {
      if (existingMolecule == null) {
          throw new RuntimeException("cannot add to null molecule");
      }
      if (addedAtomSet == null) {
          throw new RuntimeException("cannot add null molecule");
      }
      String[] atomRefs2 = join.getAtomRefs2();
      String staticAtomId = CMLUtil.getLocalName(atomRefs2[0]);
      String movableAtomId = CMLUtil.getLocalName(atomRefs2[1]);
      // the transforms can be concatenated to improve efficiency.
      // first I just need to get it right

      // use atomArray since molecules also have children and
      // the normal method fails
      CMLAtomArray atomArray = existingMolecule.getAtomArray();
      CMLAtom existingAtom = atomArray.getAtomById(staticAtomId);
      if (existingAtom == null) {
      	// this happens when join is used twice
//          existingMolecule.debug("ATOM SHOULD BE IN HERE");
          throw new RuntimeException("Cannot find atom ("+staticAtomId+") in "+existingMolecule.getId()+";" +
          		" possibly because 2 or more links have been made to the same atom");
      }
      Point3 existingPoint = existingAtom.getPoint3(CoordinateType.CARTESIAN);
      CMLAtom existingLigand = AtomTool.getOrCreateTool(existingAtom).getSingleLigand();
      if (existingLigand == null) {
          throw new RuntimeException("Expected 1 ligand for: "+existingAtom.getId());
      }
      Point3 existingLigandPoint = existingLigand.getPoint3(CoordinateType.CARTESIAN);
      if (existingLigandPoint == null) {
          existingMolecule.debug("EXISTMOL");
          existingLigand.debug("STATICLIGAND");
          throw new RuntimeException("no coordinates for: "+existingLigand.getId());
      }

      // static mol->R
      Vector3 staticVector = existingLigandPoint.subtract(existingPoint);

      CMLAtom movableAtom = addedAtomSet.getAtomById(movableAtomId);
      if (movableAtom == null) {
          throw new RuntimeException("Cannot find movable atom: "+movableAtomId);
      }
      Point3 movablePoint = movableAtom.getPoint3(CoordinateType.CARTESIAN);
      CMLAtom movableLigand = AtomTool.getOrCreateTool(movableAtom).getSingleLigand();
      if (movableLigand == null) {
          throw new RuntimeException("expected single ligand for: "+movableAtom.getId());
      }
      Point3 movableLigandPoint = movableLigand.getPoint3(CoordinateType.CARTESIAN);
      // translate movablePoint to origin
      Vector3 toOrigin = new Point3().subtract(movablePoint);
      addedAtomSet.translate3D(toOrigin);
      // movable R->molecule
      Vector3 movableVector = movablePoint.subtract(movableLigandPoint);
      // align vectors
      Transform3 transform = new Transform3(movableVector, staticVector);
      AtomSetTool.getOrCreateTool(addedAtomSet).transformCartesians(transform);

      Vector3 translateVector = new Vector3(existingPoint);
      addedAtomSet.translate3D(translateVector);

  }

};