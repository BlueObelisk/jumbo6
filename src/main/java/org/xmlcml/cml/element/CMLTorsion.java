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
 * user-modifiable class supporting torsion. * autogenerated from schema use as
 * a shell which can be edited
 * 
 */
public class CMLTorsion extends AbstractTorsion {

    final static Logger logger = Logger.getLogger(CMLTorsion.class.getName());

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /**
     * contructor.
     */
    public CMLTorsion() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLTorsion(CMLTorsion old) {
        super((AbstractTorsion) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLTorsion(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLTorsion
     */
    public static CMLTorsion makeElementInContext(Element parent) {
        return new CMLTorsion();

    }

    /**
     * gets atomIds as list.
     * 
     * @return the atomIds (null if no atomRefs3)
     */
    public List<String> getAtomIds() {
        List<String> idList = null;
        String[] atomRefs4 = getAtomRefs4();
        if (atomRefs4 != null) {
            idList = new ArrayList<String>();
            for (String s : atomRefs4) {
                idList.add(s);
            }
        }
        return idList;
    }
    
    /**
     * gets atomRefs4 as list of atoms.
     * 
     * uses the value in <torsion> element
     * 
     * @param molecule
     * @return the atoms (null if no atomRefs4)
     */
    public List<CMLAtom> getAtoms(CMLMolecule molecule) {
        List<CMLAtom> atomList = null;
        String[] atomRefs4 = this.getAtomRefs4();
        if (atomRefs4 == null || molecule == null) {
            throw new CMLRuntimeException(
                    "torsion must have molecule and atomRefs4 to get atoms");
        } else {
            atomList = new ArrayList<CMLAtom>();
            for (String atomRef : atomRefs4) {
                CMLAtom atom = molecule.getAtomById(atomRef);
                if (atom == null) {
                    throw new CMLRuntimeException("cannot find atom " + atomRef);
                }
                atomList.add(atom);
            }
        }
        return atomList;
    }

    /**
     * gets atomRefs4 as list of atoms.
     * 
     * uses the value in <torsion> element
     * 
     * @param atomSet
     * @return the atoms (null if no atomRefs4)
     */
    public List<CMLAtom> getAtoms(CMLAtomSet atomSet) {
        List<CMLAtom> atomList = null;
        String[] atomRefs4 = this.getAtomRefs4();
        if (atomRefs4 == null || atomSet == null) {
            throw new CMLRuntimeException(
                    "torsion must have atomSet and atomRefs4 to get atoms");
        } else {
            atomList = new ArrayList<CMLAtom>();
            for (String atomRef : atomRefs4) {
                CMLAtom atom = atomSet.getAtomById(atomRef);
                if (atom == null) {
                    throw new CMLRuntimeException("cannot find atom " + atomRef);
                }
                atomList.add(atom);
            }
        }
        return atomList;
    }

    /**
     * gets value calculated from coordinates.
     * 
     * @param molecule
     * @return the torsion (NaN if cannot calculate)
     */
    public double getCalculatedTorsion(CMLMolecule molecule) {
        return this.getCalculatedTorsion(molecule.getAtomSet());
    }

    /**
     * gets value calculated from coordinates.
     * 
     * @param atomSet
     * @return the torsion (NaN if cannot calculate)
     */
    public double getCalculatedTorsion(CMLAtomSet atomSet) {
        double calculatedTorsion = Double.NaN;
        if (atomSet == null) {
            throw new CMLRuntimeException("molecule required for calculating torsion");
        }
        List<CMLAtom> atoms = this.getAtoms(atomSet);
        if (atoms != null) {
            Point3[] coord = new Point3[4];
            for (int i = 0; i < 4; i++) {
                coord[i] = atoms.get(i).getXYZ3();
                if (coord[i] == null) {
                    break;
                }
            }
            try {
                Angle torsion = Point3.getTorsion(coord[0], coord[1], coord[2],
                        coord[3]);
                calculatedTorsion = torsion.getDegrees();
            } catch (Exception e) {
                throw new CMLRuntimeException("ERROR in torsion " + e);
            }
        }
        return calculatedTorsion;
    }

    /**
     * hash for atoms.
     * 
     * @param atomId1
     * @param atomId2
     * @param atomId3
     * @param atomId4
     * @return hash
     */
    public static String atomHash(String atomId1, String atomId2,
            String atomId3, String atomId4) {
        if (atomId1 == null || atomId2 == null || atomId3 == null
                || atomId4 == null) {
            return null;
        }
        if (atomId1.compareTo(atomId4) < 0) {
            String temp = atomId4;
            atomId4 = atomId1;
            atomId1 = temp;
            temp = atomId3;
            atomId3 = atomId2;
            atomId2 = temp;
        }
        return atomId1 + CMLBond.HASH_SYMB + atomId2 + CMLBond.HASH_SYMB + atomId3
                + CMLBond.HASH_SYMB + atomId4;
    }

    /** translates elements to list.
     * @param torsionElements
     * @return the list of lengths
     */
    public static List<CMLTorsion> getList(CMLElements<CMLTorsion> torsionElements) {
        List<CMLTorsion> torsionList = new ArrayList<CMLTorsion>();
        for (CMLTorsion torsion : torsionElements) {
            torsionList.add(torsion);
        }
        return torsionList;
    }
    
    /**
     * torsions indexed by atom hash.
     * 
     * @param torsions
     * @return map
     */
    public static Map<String, CMLTorsion> getIndexedTorsions(List<CMLTorsion> torsions) {
        Map<String, CMLTorsion> torsionTable = new HashMap<String, CMLTorsion>();
        for (CMLTorsion torsion : torsions) {
            String[] id = torsion.getAtomRefs4();
            String key = atomHash(id[0], id[1], id[2], id[3]);
            torsionTable.put(key, torsion);
        }
        return torsionTable;
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
    private CMLTransform3 getTransformationToNewTorsion(Angle angle, CMLAtomSet atom4Set) {
        if (atom4Set.size() != 4) {
            atom4Set.debug("ATOM4SET??");
            throw new CMLRuntimeException("Must have 4 atoms in torsion: was "+atom4Set.size());
        }
        Transform3 transform = null;
        double tor0 = this.getCalculatedTorsion(atom4Set);
        List<CMLAtom> atoms = this.getAtoms(atom4Set);
        CMLAtom atom1 = atoms.get(1);
        CMLAtom atom2 = atoms.get(2);
        Point3 point2 = atom2.getPoint3(CoordinateType.CARTESIAN);
        double delta = tor0 - angle.getDegrees();
        // translate moveable atoms to origin
        Vector3 v2 = new Point3(0.0, 0.0, 0.0).subtract(point2);
        Transform3 t1 = new Transform3(v2);
        Transform3 t1prime = new Transform3(v2.negative());
        Vector3 v = atom2.getVector3(atom1);
        Transform3 r = new Transform3(v, new Angle(delta, Units.DEGREES));
        transform = r.concatenate(t1);
        // translate back
        transform = t1prime.concatenate(transform);
        return new CMLTransform3(transform);
        
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
        CMLAtomSet atom4Set = atomSet.getAtomSetById(this.getAtomRefs4()); 
        CMLTransform3 transform = this.getTransformationToNewTorsion(
                angle, atom4Set);
        moveableSet.transformCartesians(transform);
    }

    /** adjusts coordinates in atomSet to torsion angle.
     * 
     * @param molecule
     */
    public void adjustCoordinates(CMLMolecule molecule) {
        String[] atomRefs4 = this.getAtomRefs4();
        CMLAtomSet fixedAtomSet = new CMLAtomSet(molecule, atomRefs4);
        CMLAtom atom0 = fixedAtomSet.getAtom(1);
        CMLAtom atom1 = fixedAtomSet.getAtom(2);
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
     */
    void adjustCoordinates(CMLAtomSet atomSet, CMLAtomSet moveableSet) {
        if (this.getValue().trim().length() != 0) { 
            Double d = this.getXMLContent();
            if (!Double.isNaN(d)) {
                Angle angle = new Angle(d, Angle.Units.DEGREES);
                // make sure there are exactly 4 atoms in order
                CMLAtomSet atom4Set = atomSet.getAtomSetById(this.getAtomRefs4()); 
                CMLTransform3 transform = this.getTransformationToNewTorsion(
                        angle, atom4Set);
                moveableSet.transformCartesians(transform);
            }
        }
    }

    /** set atomRefs4 attribute.
     * 
     * @param atom0
     * @param molecule0
     * @param rGroup0
     * @param atom1
     * @param molecule1
     * @param rGroup1
     * @param takeLigandWithLowestId
     */
    public void setAtomRefs4( 
        CMLAtom rGroup0, CMLAtom atom0, CMLAtom atom1, CMLAtom rGroup1) {
        this.setAtomRefs4(
            new String[]{ 
                    rGroup0.getId(),atom0.getId(),
                    atom1.getId(),rGroup1.getId(),
            });
    }

    /** writes torsions to an XHTML table.
     * columns are atom1.label atom2.label atom3.label atom4.label torsion in deg
     * @param w writer to output
     * @param torsionList
     * @param molecule
     * @throws IOException
     */
    public static void outputHTML(
        Writer w, List<CMLTorsion> torsionList,
        CMLMolecule molecule) throws IOException {
        if (torsionList.size() > 0) {
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
            w.write("atom4 (id)");
            w.write("</th>");
            w.write("<th>");
            w.write("torsion");
            w.write("</th>");
            w.write("</tr>\n");
            for (CMLTorsion torsion : torsionList) {
                List<CMLAtom> atoms = torsion.getAtoms(molecule);
                w.write("<tr>");
                for (int i = 0; i < 4; i++) {
                    w.write("<td>");
                    CMLAtom atom = atoms.get(i);
                    Nodes labelNodes = atom.query(
                        CMLScalar.NS+"[@dictRef='iucr:_atom_site_label']", X_CML);
                    String label = ((CMLScalar) labelNodes.get(0)).getXMLContent()+" ("+atom.getId()+S_RBRAK;
                    w.write( (label == null) ? atom.getId() : label);
                    w.write("</td>");
                }
                String s = "UNSET";
                try {
                    s = S_EMPTY+torsion.getXMLContent();
                } catch (CMLRuntimeException e) {
                    //
                }
                w.write("<td>"+s.substring(0, Math.min(6, s.length()))+"</td>");
                w.write("</tr>\n");
            }
            w.write("</table>\n");
        }
    }
    
    /** string representation.
     * 
     * @return string
     */
    public String getString() {
        String s = S_EMPTY;
        String[] aa = this.getAtomRefs4();
        if (aa != null) {
            s += Util.concatenate(aa, S_MINUS);
        }
        // torsion might be unset
        String ss = "UNSET";
        try {
            double dd = this.getXMLContent();
            ss += dd;
        } catch (CMLRuntimeException e) {
            //
        }
        s += ": "+ss;
        return s;
    }
}