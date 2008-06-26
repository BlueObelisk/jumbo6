// /*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/
package org.xmlcml.cml.element;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.tools.AtomTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;

/** .
*
*
* \n EXPERIMENTAL. join will normally use atomRefs2 to identify 2 R atoms\n (i.e. elementType='R' that should be joined. The atoms to which the R atoms\n are attached are then joined by a new bond and the R groups are then deleted. It is currently \nan error if these atoms already have a connecting bond.
*
* user-modifiable class autogenerated from schema if no class exists
* use as a shell which can be edited
* the autogeneration software will not overwrite an existing class file

*/
public class CMLJoin extends org.xmlcml.cml.element.AbstractJoin {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** relationship of molecule in moleculeRefs2.
     *
     */
    public enum MoleculePointer {
        /** child.*/
        CHILD,
        /** next.*/
        NEXT,
        /** parent.*/
        PARENT,
        /** previous.*/
        PREVIOUS;
    }

    /** convenience*/
    public final static String CHILD_S = MoleculePointer.CHILD.toString();
    /** convenience*/
    public final static String PARENT_S = MoleculePointer.PARENT.toString();
    /** convenience*/
    public final static String PREVIOUS_S = MoleculePointer.PREVIOUS.toString();
    /** convenience*/
    public final static String NEXT_S = MoleculePointer.NEXT.toString();

    /** R elementType.
     */
    public final static String R_GROUP = "R";

    /** label to define torsion.
     */
    public final static String TORSION_END = C_A+"torsionEnd";
    /** find labels with torsions.
     */
    public final String TORSION_END_QUERY =
        CMLLabel.NS+"[@dictRef='"+TORSION_END+"']";

    /** convention attribute indicating that join contains fragments.
     */
    public final static String FRAGMENT_CONTAINER = "fragmentContainer";

    /** must give simple documentation.
    *

    */
    public CMLJoin() {
    }
    /** must give simple documentation.
    *
    * @param old CMLJoin to copy

    */

    public CMLJoin(CMLJoin old) {
        super((org.xmlcml.cml.element.AbstractJoin) old);
    }

    /** copy node .
    *
    * @return Node
    */
    public Node copy() {
        return new CMLJoin(this);
    }
    /** create new instance in context of parent, overridable by subclasses.
    *
    * @param parent parent of element to be constructed (ignored by default)
    * @return CMLJoin
    */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLJoin();
    }

    /**
     * will process repeat attribute.
     *
     * @param parent
     *            element
     */
    public void finishMakingElement(Element parent) {
        super.finishMakingElement(parent);
//        RepeatAttribute.process(this);
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
     * @throws CMLRuntimeException if groups are not R or other problems
     */
    private void joinByAtomRefs2AndAdjustGeometry(
    		CMLMolecule staticMolecule, CMLAtomSet movableAtomSet,
            boolean takeLigandWithLowestId) throws CMLRuntimeException {
        String[] atomRefs2 = this.getAtomRefs2();
        if (atomRefs2 == null) {
            throw new CMLRuntimeException("missing atomRefs2 attribute");
        }
        String staticAtomId = CMLUtil.getLocalName(atomRefs2[0]);
        CMLAtom staticAtom =  staticMolecule.getAtomArray().getAtomById(staticAtomId);
        if (staticAtom == null) {
            throw new CMLRuntimeException("Cannot find existing atom: "+staticAtomId);
        }
        String movableAtomId = CMLUtil.getLocalName(atomRefs2[1]);
        CMLAtom movableAtom = movableAtomSet.getAtomById(movableAtomId);
        if (movableAtom == null) {
            throw new CMLRuntimeException("Cannot find movable atom: "+movableAtomId);
        }
        CMLAtom atom0 = AtomTool.getOrCreateTool(staticAtom).getSingleLigand();
        CMLAtom atom1 = AtomTool.getOrCreateTool(movableAtom).getSingleLigand();

        CMLMolecule molecule = atom0.getMolecule();
        if (molecule == null) {
            throw new CMLRuntimeException("no owner molecule: "+atom0.getId());
        }

        CMLMolecule molecule1 = atom1.getMolecule();
        if (molecule1 == null) {
            throw new CMLRuntimeException("no owner molecule: "+atom1.getId());
        } else if (molecule != molecule1) {
            throw new CMLRuntimeException("atoms not in same molecule; should have been flattened");
        }

        // create bond
        createAndAddBond(staticMolecule, atom0, atom1);

        removeOldElements(staticMolecule, staticAtom, movableAtom, atom0, atom1);

        CMLAtomSet moleculeAtomSet = molecule.getAtomSet();
        CMLAtomSet moveableAtomSet = AtomTool.getOrCreateTool(atom1).getDownstreamAtoms(atom0);

        adjustTorsion(staticAtom, atom0, atom1, movableAtom, moleculeAtomSet, moveableAtomSet);
        adjustLength(atom0, atom1, moveableAtomSet);

    }

    private void adjustLength(CMLAtom atom0, CMLAtom atom1, CMLAtomSet moveableAtomSet) {
        List<Node> lengths = CMLUtil.getQueryNodes(this, CMLLength.NS, CML_XPATH);
        if (lengths.size() == 1) {
            CMLLength length = (CMLLength) lengths.get(0);
            length.setAtomRefs2(atom0, atom1);
            length.adjustCoordinates(atom0, atom1, moveableAtomSet);
        }
    }

    private void adjustTorsion(
            CMLAtom rGroup0, CMLAtom atom0, CMLAtom atom1, CMLAtom rGroup1,
            CMLAtomSet moleculeAtomSet, CMLAtomSet moveableAtomSet) {
        List<Node> torsions = CMLUtil.getQueryNodes(this, CMLTorsion.NS, CML_XPATH);
        if (torsions.size() == 1) {
            CMLTorsion torsion = (CMLTorsion) torsions.get(0);
            CMLAtom atom00 = this.getUniqueLigand(rGroup0, atom0, atom1);
            if (atom00 == null && atom0.getLigandAtoms().size() > 1) {
                ((CMLElement)atom0.getParent()).debug("PPP");
                throw new CMLRuntimeException("Null ligand on "+atom0.getId()+" maybe needs a label for: "+rGroup0.getId());
            }
            CMLAtom atom11 = this.getUniqueLigand(rGroup1, atom1, atom0);
            if (atom11 == null && atom1.getLigandAtoms().size() > 1) {
//                ((CMLElement)atom1.getParent()).debug("PPP");
                throw new CMLRuntimeException(
            		"Null ligand on "+atom1.getId()+" maybe needs a label for: "+rGroup1.getId()+"\n" +
    				" find a ligand of "+atom1.getId()+" and give it a child ligand of the form:" +
   			        "<label dictRef='cml:torsionEnd'>"+rGroup1.getId()+"</label> (just the last component)");
            }
            if (atom00 != null && atom11 != null) {
                torsion.setAtomRefs4(atom00, atom0, atom1, atom11);
                torsion.adjustCoordinates(moleculeAtomSet, moveableAtomSet);
            }
        }
    }

    private CMLAtom getUniqueLigand(CMLAtom rGroup0, CMLAtom atom0, CMLAtom atom1) {
        List<CMLAtom> ligands = atom0.getLigandAtoms();
        if (!ligands.contains(atom1)) {
            throw new CMLRuntimeException("ligands should contain "+atom1.getId());
        }
        for (CMLAtom ligand : ligands) {
            if (ligand.equals(atom1)) {
                //
            } else if (ligands.size() == 2) {
                return ligand;
            } else {
                List<Node> labels = CMLUtil.getQueryNodes(
                        ligand, TORSION_END_QUERY, CML_XPATH);
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
        if (this.getId() != null) {
            bond.setId(this.getId());
        } else {
//            System.out.println("WARNING: join should have id: ");
        }
        String order = this.getOrder();
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
        this.detach();
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
            throw new CMLRuntimeException("cannot add to null molecule");
        }
        if (addedAtomSet == null) {
            throw new CMLRuntimeException("cannot add null molecule");
        }
        String[] atomRefs2 = this.getAtomRefs2();
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
//            existingMolecule.debug("ATOM SHOULD BE IN HERE");
            throw new CMLRuntimeException("Cannot find atom ("+staticAtomId+") in "+existingMolecule.getId()+";" +
            		" possibly because 2 or more links have been made to the same atom");
        }
        Point3 existingPoint = existingAtom.getPoint3(CoordinateType.CARTESIAN);
        CMLAtom existingLigand = AtomTool.getOrCreateTool(existingAtom).getSingleLigand();
        if (existingLigand == null) {
            throw new CMLRuntimeException("Expected 1 ligand for: "+existingAtom.getId());
        }
        Point3 existingLigandPoint = existingLigand.getPoint3(CoordinateType.CARTESIAN);
        if (existingLigandPoint == null) {
            existingMolecule.debug("EXISTMOL");
            existingLigand.debug("STATICLIGAND");
            throw new CMLRuntimeException("no coordinates for: "+existingLigand.getId());
        }

        // static mol->R
        Vector3 staticVector = existingLigandPoint.subtract(existingPoint);

        CMLAtom movableAtom = addedAtomSet.getAtomById(movableAtomId);
        if (movableAtom == null) {
            throw new CMLRuntimeException("Cannot find movable atom: "+movableAtomId);
        }
        Point3 movablePoint = movableAtom.getPoint3(CoordinateType.CARTESIAN);
        CMLAtom movableLigand = AtomTool.getOrCreateTool(movableAtom).getSingleLigand();
        if (movableLigand == null) {
            throw new CMLRuntimeException("expected single ligand for: "+movableAtom.getId());
        }
        Point3 movableLigandPoint = movableLigand.getPoint3(CoordinateType.CARTESIAN);
        // translate movablePoint to origin
        Vector3 toOrigin = new Point3().subtract(movablePoint);
        addedAtomSet.translate3D(toOrigin);
        // movable R->molecule
        Vector3 movableVector = movablePoint.subtract(movableLigandPoint);
        // align vectors
        Transform3 transform = new Transform3(movableVector, staticVector);
        addedAtomSet.transformCartesians(transform);

        Vector3 translateVector = new Vector3(existingPoint);
        addedAtomSet.translate3D(translateVector);

    }


    private String getId(CMLMolecule molecule) {
        String ref = molecule.getRef();
        String id = molecule.getId();
        return ref+S_UNDER+id;
    }

//    /** create atomRefs2 from moleculeRefs2 and atomRefs2.
//     *
//     * @param parent
//     * @param previousFragments
//     * @param nextFragments
//     */
//    private void processMoleculeRefs2AndAtomRefs2(
//            CMLFragment previousFragment, CMLFragment nextFragment) {
////      <join id="j1" order="1" moleculeRefs2="PARENT NEXT" atomRefs2="r1 r1">
////      <length>1.4</length>
////      <angle id="l2.1.1" atomRefs3="a2 r1 r1">115</angle>
////  </join>
////  <fragment>
////      <molecule ref="g:po" id="m3" />
////  </fragment>
//        String[] moleculeRefs2 = this.getMoleculeRefs2();
//        if (moleculeRefs2 == null) {
//            throw new CMLRuntimeException("No moleculeRefs2 on join");
//        }
//        if (CMLJoin.PARENT_S.equals(moleculeRefs2[0])) {
//            previousFragment = (CMLFragment) this.getParent();
//            if (previousFragment == null) {
//            	throw new CMLRuntimeException("No parent fragment");
//            }
//        } else if (CMLJoin.PREVIOUS_S.equals(moleculeRefs2[0])) {
//        }
//        CMLMolecule previousMolecule = FragmentTool.getOrCreateTool(previousFragment).getMolecule();
//        if (previousMolecule == null) {
//        	throw new CMLRuntimeException("Cannot find previous molecule to join");
//        }
//        CMLMolecule nextMolecule = null;
//        if (CMLJoin.NEXT_S.equals(moleculeRefs2[1])) {
//            nextMolecule = FragmentTool.getOrCreateTool(nextFragment).getMolecule();
//        }
//        if (nextMolecule == null) {
//        	throw new CMLRuntimeException("Cannot find next molecule to join");
//        }
//        processMoleculeRefs2AndAtomRefs2(previousMolecule, nextMolecule);
//    }

    /** creates atomRefs2 to join previous/parent molecule to next/child.
     *
     * @param previousMolecule
     * @param nextMolecule
     */
    public void processMoleculeRefs2AndAtomRefs2(
        CMLMolecule previousMolecule, CMLMolecule nextMolecule) {
//      <join id="j1" order="1" moleculeRefs2="PARENT NEXT" atomRefs2="r1 r1">
//      <length>1.4</length>
//      <angle id="l2.1.1" atomRefs3="a2 r1 r1">115</angle>
//  </join>
//  <fragment>
//      <molecule ref="g:po" id="m3" />
//  </fragment>
       	if (previousMolecule == null) {
    		throw new CMLRuntimeException("null PREVIOUS, check syntax");
    	}
       	if (nextMolecule == null) {
    		throw new CMLRuntimeException("null NEXT, check syntax");
    	}
        String[] atomRefs2 = this.getAtomRefs2();
        if (atomRefs2 == null) {
        	throw new CMLRuntimeException("No atomrefs2 on Join");
        }
        this.setAtomRefs2(new String[]{
                getId(previousMolecule)+S_UNDER+atomRefs2[0],
                getId(nextMolecule)+S_UNDER+atomRefs2[1]});
        this.setMoleculeRefs2(new String[]{
                getId(previousMolecule),
                getId(nextMolecule)});
    }

    /** get string.
     * 
     * @return string
     */
    public String getString() {
    	String s = S_EMPTY;
    	String id = this.getId();
    	if (id != null) {
    		s += id+S_SPACE;
    	}
    	String[] moleculeRefs2 = this.getMoleculeRefs2();
    	if (moleculeRefs2 != null) {
    		s += Util.concatenate(moleculeRefs2, S_SPACE)+S_SEMICOLON;
    	}
    	String[] atomRefs2 = this.getAtomRefs2();
    	if (atomRefs2 != null) {
    		s += Util.concatenate(atomRefs2, S_SPACE)+S_SEMICOLON;
    	}
    	return s;
    }
}
