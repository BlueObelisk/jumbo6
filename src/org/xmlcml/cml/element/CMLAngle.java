package org.xmlcml.cml.element;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.euclid.Angle.Units;

/**
 * user-modifiable class supporting angle.
 */
public class CMLAngle extends AbstractAngle {

    final static Logger logger = Logger.getLogger(CMLAngle.class.getName());

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /**
     * constructor.
     */
    public CMLAngle() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLAngle(CMLAngle old) {
        super((AbstractAngle) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLAngle(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLAngle
     */
    public static CMLAngle makeElementInContext(Element parent) {
        return new CMLAngle();

    }

    /**
     * gets atomIds as list.
     * 
     * @return the atomIds (null if no atomRefs3)
     */
    public List<String> getAtomIds() {
        List<String> idList = null;
        String[] atomRefs3 = getAtomRefs3();
        if (atomRefs3 != null) {
            idList = new ArrayList<String>();
            for (String s : atomRefs3) {
                idList.add(s);
            }
        }
        return idList;
    }
    
    /**
     * gets atoms as array of atoms.
     * 
     * @param molecule
     * @return the atoms (null if no atomRefs3)
     */
    public List<CMLAtom> getAtoms(CMLMolecule molecule) {
        return (molecule == null) ? null : this.getAtoms(molecule.getAtomSet());
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
            String[] atomIds = super.getAtomRefs3();
            if (atomIds != null && atomIds.length == 3) {
                atomList = new ArrayList<CMLAtom>();
                for (String atomId : atomIds) {
                    CMLAtom atom = atomSet.getAtomById(atomId);
                    if (atom == null) {
                        throw new CMLRuntimeException("cannot find atom " + atomId);
                    }
                    atomList.add(atom);
                }
            }
        }
        return atomList;
    }

    /**
     * gets value calculated from coordinates. requires atomRefs3 ro be set and
     * valid. then gets the angle between atomRefs3 0-1-2
     * 
     * @param molecule
     *            owning molecule (all atoms must be in this)
     * @return the angle in degrees (NaN if cannot calculate)
     */
    public double getCalculatedAngle(CMLMolecule molecule) {
        return this.getCalculatedAngle(molecule.getAtomSet());
    }
    
    /**
     * gets value calculated from coordinates. requires atomRefs3 ro be set and
     * valid. then gets the angle between atomRefs3 0-1-2
     * 
     * @param atomSet
     * @return the angle in degrees (NaN if cannot calculate)
     */
    public double getCalculatedAngle(CMLAtomSet atomSet) {
        double calculatedAngle = Double.NaN;
        List<CMLAtom> atomList = this.getAtoms(atomSet);
        if (atomList != null) {
            Point3[] coord = new Point3[3];
            int i = 0;
            for (CMLAtom atom : atomList) {
                coord[i] = atom.getXYZ3();
                if (coord[i++] == null) {
                    break;
                }
            }
            try {
                Angle angle = Point3.getAngle(coord[0], coord[1], coord[2]);
                if (angle != null) {
                    calculatedAngle = angle.getDegrees();
                }
            } catch (Exception e) {
                throw new CMLRuntimeException("Bug: " + e);
            }
        }
        return calculatedAngle;
    }

    /** create key from atomRefs3 attribute and atomHash
     * 
     * @return the hash null if no atomRefs3
     */
    public String atomHash() {
        String[] a = this.getAtomRefs3();
        return (a == null) ? null : atomHash(a[0], a[1], a[2]);
    }

    /**
     * create key from three atoms. a1-a2-a3 and a3-a2-a1 are equivalent
     * 
     * @param atomId1
     * @param atomId2
     * @param atomId3
     * @return the hash
     */
    public static String atomHash(final String atomId1, final String atomId2,
            final String atomId3) {
        String result = null;
        if (!(atomId1 == null || atomId2 == null || atomId3 == null)) {
            String a1 = atomId1;
            String a3 = atomId3;
            if (atomId1.compareTo(atomId3) > 0) {
                a3 = atomId1;
                a1 = atomId3;
            } else if (atomId1.compareTo(atomId3) < 0) {
                a1 = atomId1;
                a3 = atomId3;
            }
            result = a1 + CMLBond.HASH_SYMB + atomId2 + CMLBond.HASH_SYMB + a3;
        }
        return result;
    }

    /** translates elements to list.
     * @param angleElements
     * @return the list of angles
     */
    public static List<CMLAngle> getList(CMLElements<CMLAngle> angleElements) {
        List<CMLAngle> angleList = new ArrayList<CMLAngle>();
        for (CMLAngle angle : angleElements) {
            angleList.add(angle);
        }
        return angleList;
    }
    
    /**
     * gets a Map of angles indexed by atoms. the map has the keys of atomHashs
     * for the angles
     * 
     * @param angleList
     *            list of the angles
     * @return the indexed table (keyed on atomHash)
     */
    public static Map<String, CMLAngle> getIndexedAngles(List<CMLAngle> angleList) {
        Map<String, CMLAngle> angleTable = new HashMap<String, CMLAngle>();
        for (CMLAngle angle : angleList) {
            String[] id = angle.getAtomRefs3();
            String key = atomHash(id[0], id[1], id[2]);
            angleTable.put(key, angle);
        }
        return angleTable;
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
        CMLAtomSet atom3Set = atomSet.getAtomSetById(this.getAtomRefs3()); 
        CMLTransform3 transform = this.getTransformationToNewAngle(
                angle, atom3Set);
        moveableSet.transformCartesians(transform);
    }

    /** adjusts coordinates in molecule to torsion angle.
     * moves atoms downstream of atom0/atom1
     * @param molecule
     */
    public void adjustCoordinates(CMLMolecule molecule) {
        String[] atomRefs3 = this.getAtomRefs3();
        CMLAtomSet fixedAtomSet = new CMLAtomSet(molecule, atomRefs3);
        CMLAtom atom0 = fixedAtomSet.getAtom(0);
        CMLAtom atom1 = fixedAtomSet.getAtom(1);
        MoleculeTool moleculeTool = new MoleculeTool(molecule);
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
     * @exception CMLRuntimeException bad value for angle
     */
    void adjustCoordinates(CMLAtomSet atomSet, CMLAtomSet moveableSet) 
        throws CMLRuntimeException {
        if (this.getValue().trim().length() == 0) {
            //
        } else {
            Double d = this.getXMLContent();
            if (!Double.isNaN(d)) {
                Angle angle = new Angle(d, Angle.Units.DEGREES);
                // make sure there are exactly 3 atoms in order
                CMLAtomSet atom3Set = atomSet.getAtomSetById(this.getAtomRefs3()); 
                CMLTransform3 transform = this.getTransformationToNewAngle(
                        angle, atom3Set);
                moveableSet.transformCartesians(transform);
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
    private CMLTransform3 getTransformationToNewAngle(Angle angle, CMLAtomSet atom3Set) {
        if (atom3Set.size() != 3) {
            atom3Set.debug("ATOM3SET??");
            throw new CMLRuntimeException("Must have 3 atoms in torsion: was "+atom3Set.size());
        }
        // NOT YET WORKING - REQUIRES TESTING
        Transform3 transform = null;
        double ang0 = this.getCalculatedAngle(atom3Set);
        List<CMLAtom> atoms = atom3Set.getAtoms();
        CMLAtom atom0 = atoms.get(0);
        CMLAtom atom1 = atoms.get(1);
        CMLAtom atom2 = atoms.get(2);
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
        Transform3 r = new Transform3(vcross, new Angle(delta, Units.DEGREES));
        transform = r.concatenate(t1);
        // translate back
        transform = t1prime.concatenate(transform);
        return new CMLTransform3(transform);
    }
    
    /** set atomRefs3 attribute.
     * 
     * @param atom0
     * @param atom1
     * @param atom2
     */
    public void setAtomRefs3( 
            CMLAtom atom0, CMLAtom atom1, CMLAtom atom2) {
            this.setAtomRefs3(
                new String[]{
                    atom0.getId(),
                    atom1.getId(),
                    atom2.getId()
                });
    }
    
    /** writes angles to an XHTML table.
     * columns are atom1.label atom2.label atom3.label angle in deg
     * @param w writer to output
     * @param angleList
     * @param molecule
     * @throws IOException
     */
    public static void outputHTML(
        Writer w, List<CMLAngle> angleList,
        CMLMolecule molecule) throws IOException {
        if (angleList.size() > 0) {
            w.write("<table border='1'>\n");
            w.write("<tr>");
            w.write("<th>");
            w.write("atom1 (id)");
            w.write("</th>");
            w.write("<th>");
            w.write("atom2 (id)");
            w.write("</th>");
            w.write("<th>");
            w.write("atom3 (id)");
            w.write("</th>");
            w.write("<th>");
            w.write("angle");
            w.write("</th>");
            w.write("</tr>\n");
            for (CMLAngle angle : angleList) {
                List<CMLAtom> atoms = angle.getAtoms(molecule);
                w.write("<tr>");
                for (int i = 0; i < 3; i++) {
                    w.write("<td>");
                    CMLAtom atom = atoms.get(i);
                    Nodes labelNodes = atom.query(
                        CMLScalar.NS+"[@dictRef='iucr:_atom_site_label']", X_CML);
                    String label = ((CMLScalar) labelNodes.get(0)).getXMLContent()+" ("+atom.getId()+S_RBRAK;
                    w.write( (label == null) ? atom.getId() : label);
                    w.write("</td>");
                }
                String s = ""+angle.getXMLContent();
                w.write("<td>"+s.substring(0, Math.min(6, s.length()))+"</td>");
                w.write("</tr>\n");
            }
            w.write("</table>\n");
        }
    }
    
    /** get string.
     * 
     * @return the string
     */
    public String getString() {
        String s = S_EMPTY;
        String[] a = getAtomRefs3();
        if (a != null) {
            s += Util.concatenate(a, S_MINUS);
        }
        s += S_SPACE;
        s += this.getXMLContent();
        return s;
    }
}
