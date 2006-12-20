package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Vector3;

/**
 * user-modifiable class supporting zMatrix. * autogenerated from schema use as
 * a shell which can be edited
 * 
 */
public class CMLZMatrix extends AbstractZMatrix {

    //temporary storage
    List<CMLLength> lengthList = null;
    List<CMLAngle> angleList = null;
    List<CMLTorsion> torsionList = null;

    Map<String, CMLLength> lengthByAtomHashMap;
    Map<String, CMLAngle> angleByAtomHashMap;
    Map<String, List<CMLTorsion>> torsionByAtomMap;
    // 
    Set<String> currentAtomSet;
    // atoms no longer involved in additional torsions
    Set<String> deadAtomSet;
    // final atom set
    Set<String> finalAtomSet;
    
    // torsions involving current atoms
    Set<CMLTorsion> currentTorsionSet;
    
    List<CMLTorsion> deadTorsionList;
    List<CMLLength> deadLengthList;
    List<CMLAngle> deadAngleList;
    
    /**
     * contructor.
     */
    public CMLZMatrix() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLZMatrix(CMLZMatrix old) {
        super((AbstractZMatrix) old);

    }
    
    /** construct from element which has has geometric children.
     * use length, angle, torsion children.
     * reorder them so that they describe a ZMatrix
     * @param element
     * @exception CMLRuntimeException cannot form ZMatrix
     */
    public CMLZMatrix(CMLElement element) throws CMLRuntimeException {
        makeZMatrix(element);
    }
    
    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLZMatrix(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLZMatrix
     */
    public static CMLZMatrix makeElementInContext(Element parent) {
        return new CMLZMatrix();

    }
    
    /** calculate cartesians and adds to molecule.
     * 
     * @param molecule
     */
    public void addCartesiansTo(CMLMolecule molecule) {
        Elements lengthElements = this.getChildCMLElements(CMLLength.TAG);
        Elements angleElements = this.getChildCMLElements(CMLAngle.TAG);
        Elements torsionElements = this.getChildCMLElements(CMLTorsion.TAG);
        if (lengthElements.size() == 1) {
            CMLLength length = (CMLLength) lengthElements.get(0);
            String id = length.getAtomRefs2()[0];
            CMLAtom atom0 = molecule.getAtomById(id);
            if (atom0 == null) {
                throw new CMLRuntimeException("Cannot find atom: "+id);
            }
            id = length.getAtomRefs2()[1];
            CMLAtom atom1 = molecule.getAtomById(id);
            if (atom1 == null) {
                throw new CMLRuntimeException("Cannot find atom: "+id);
            }
            atom0.setXYZ3(new Point3(0, 0, 0));
            atom1.setXYZ3(new Point3(length.getXMLContent(), 0, 0));
        } else if (lengthElements.size() == 2) {
            setCoordinates((CMLAngle) angleElements.get(0), 
                (CMLLength) lengthElements.get(0), 
                (CMLLength) lengthElements.get(1),
                molecule);
        } else {
            setCoordinates((CMLAngle) angleElements.get(0), 
                (CMLLength) lengthElements.get(0), 
                (CMLLength) lengthElements.get(1),
                molecule);
            for (int itors = 0; itors < torsionElements.size(); itors++) {
                setXYZ3(
                    (CMLLength) lengthElements.get(itors+2), 
                    (CMLAngle) angleElements.get(itors+1), 
                    (CMLTorsion) torsionElements.get(itors), 
                    molecule);
            }
        }
//        molecule.debug();
    }
    
    private void setCoordinates(CMLAngle angle, CMLLength length0, CMLLength length1,
            CMLMolecule molecule) {
        int i0 = -1;
        int i2 = -1;
        if (CMLBond.atomHash(length0.getAtomRefs2()).equals(CMLBond.atomHash(
                angle.getAtomRefs3()[0], angle.getAtomRefs3()[1]))) {
            i0 = 0;
            i2 = 2;
        } else if (CMLBond.atomHash(length0.getAtomRefs2()).equals(CMLBond.atomHash(
                angle.getAtomRefs3()[2], angle.getAtomRefs3()[1]))) {
            i0 = 2;
            i2 = 0;
        } else {
            throw new CMLRuntimeException("Cannot match lengths to angle");
        }
        String id = angle.getAtomRefs3()[i0];
        CMLAtom atom0 = molecule.getAtomById(id);
        if (atom0 == null) {
            throw new CMLRuntimeException("Cannot find atom: "+id);
        }
        id = angle.getAtomRefs3()[1];
        CMLAtom atom1 = molecule.getAtomById(id);
        if (atom1 == null) {
            throw new CMLRuntimeException("Cannot find atom: "+id);
        }
        id = angle.getAtomRefs3()[i2];
        CMLAtom atom2 = molecule.getAtomById(id);
        if (atom0 == null) {
            throw new CMLRuntimeException("Cannot find atom: "+id);
        }
        atom0.setXYZ3(new Point3(0, 0, 0));
        double l0 = length0.getXMLContent();
        atom1.setXYZ3(new Point3(l0, 0, 0));
        double l1 = length1.getXMLContent();
        double anglex = angle.getXMLContent()*Math.PI / 180.;
        double sina = Math.sin(anglex);
        double cosa = Math.cos(anglex);
        atom2.setXYZ3(new Point3(l0 - l1*cosa, l1*sina, 0));
    }

    /** set cartesian coordinates on atom from internal coordinates.
     * all parameters must be organised in ZMatrix convention
     * coordinates on a4 are altered. atoms a1, a2, a3, a4 must exist
     * and a1, a2, a3 must have coordinates (XYZ3)
     * @param length a3-a4
     * @param angle a2-a3-a4
     * @param torsion a1-a2-a3-a4
     * @param molecule
     */
    public void setXYZ3(CMLLength length, CMLAngle angle, CMLTorsion torsion,
        CMLMolecule molecule) {
        String[] atomRefs4 = torsion.getAtomRefs4();
        CMLAtom atom0 = molecule.getAtomById(atomRefs4[0]);
        CMLAtom atom1 = molecule.getAtomById(atomRefs4[1]);
        CMLAtom atom2 = molecule.getAtomById(atomRefs4[2]);
        CMLAtom atom3 = molecule.getAtomById(atomRefs4[3]);
        Point3 p0 = atom0.getXYZ3();
        Point3 p1 = atom1.getXYZ3();
        Point3 p2 = atom2.getXYZ3();
        Point3 p3 = atom3.getXYZ3();
        if (p0 == null) {
            throw new CMLRuntimeException("should not be null p0 "+atom0.getId());
        }
        if (p1 == null) {
            throw new CMLRuntimeException("should not be null p1 "+atom1.getId());
        }
        if (p2 == null) {
            throw new CMLRuntimeException("should not be null p2 "+atom2.getId());
        }
        if (p3 != null) {
            throw new CMLRuntimeException("should be null p3 "+atom3.getId());
        }
        Vector3 v01 = p0.subtract(p1);
        Vector3 v12 = p2.subtract(p1);
        Vector3 cross1 = v01.cross(v12);
        cross1 = cross1.normalize();
        Vector3 cross2 = v12.cross(cross1);
        cross2 = cross2.normalize();
        double tangle = torsion.getXMLContent()*Math.PI / 180.;
        Vector3 cross1a = cross1.multiplyBy(Math.sin(-tangle)); 
        Vector3 cross2a = cross2.multiplyBy(Math.cos(tangle)); 
        Vector3 cross3 = cross1a.plus(cross2a);
        cross3 = cross3.normalize();
        Vector3 v12a = v12.normalize();
        double len = length.getXMLContent();
        double ang = angle.getXMLContent()*Math.PI / 180.;
        p3 = new Point3(p2);
        p3 = p3.plus(cross3.multiplyBy(len * Math.sin(ang)));
        p3 = p3.subtract(v12a.multiplyBy(len * Math.cos(ang)));
        atom3.setXYZ3(p3);
//        atom3.debug();
//        molecule.debug();
    }

    void makeZMatrix(CMLElement element) {
        lengthList = makeLengthList(element);
        angleList = makeAngleList(element, lengthList.size());
        torsionList = makeTorsionList(element, lengthList.size());
        currentAtomSet = new HashSet<String>();
        deadAtomSet = new HashSet<String>();
        torsionByAtomMap = new HashMap<String, List<CMLTorsion>>();
        currentTorsionSet = new HashSet<CMLTorsion>();
        finalAtomSet = new HashSet<String>();
        deadTorsionList = new ArrayList<CMLTorsion>();
        deadLengthList = new ArrayList<CMLLength>();
        deadAngleList = new ArrayList<CMLAngle>();

        lengthByAtomHashMap = new HashMap<String, CMLLength>();
        for (CMLLength length : lengthList) {
            lengthByAtomHashMap.put(length.atomHash(), length);
        }
//        System.out.println("LBYATOM "+lengthByAtomHashMap.size());
        angleByAtomHashMap = new HashMap<String, CMLAngle>();
        for (CMLAngle angle : angleList) {
            angleByAtomHashMap.put(angle.atomHash(), angle);
        }
        
        makeTorsionByAtomMap();
        
        if (lengthList.size() == 1) {
            this.appendChild(new CMLLength(lengthList.get(0)));
        } else if (lengthList.size() == 2) {
            this.appendChild(new CMLLength(lengthList.get(0)));
            this.appendChild(new CMLLength(lengthList.get(1)));
            this.appendChild(new CMLAngle((CMLAngle)angleList.get(0)));
        } else {
//            element.debug();
//            debugPrint();
            addFirstFullTorsion();
            while (true) {
                Sprout sprout = getNextSprout();
                if (sprout == null) {
//                    System.out.println("++++++++++++++++++++No more sprouts");
                    if (torsionList.size() != 0) {
//                        for (CMLTorsion torsion : torsionList) {
//                            System.out.println("UNUSED TOR: "+torsion.getString());
//                        }
//                        debugSprout();
                        throw new CMLRuntimeException("UNUSED TORSIONs");
                    }
                    break;
                }
                addSprout(sprout);
//                debugPrint();
            }
        }
//        this.debug();
    }
    
    // a full torsion is one with 2 angles and 3 lengths
    private void addFirstFullTorsion() {
//        System.out.println("=============FIRST FULL TORSION");
        CMLTorsion torsion = null;
        for (CMLTorsion t : torsionList) {
            FullTorsion ft = getFullTorsion(t);
            if (ft != null) {
                addFullTorsion(ft);
                torsion = t;
                break;
            }
        }
        if (torsion == null) {
            throw new CMLRuntimeException("Cannot find a first full torsion");
        }
    }
    
    private void addFullTorsion(FullTorsion ft) {
        CMLTorsion tNew = getNewTorsion(ft.torsion);
        String[] at = tNew.getAtomRefs4();
        addAtom(at[0]);
        addAtom(at[1]);
        addAtom(at[2]);
        addToMolecule(ft.length1);
        addToMolecule(ft.length2);
        addToMolecule(ft.angle12);
        addToMolecule(ft.length3);
        addToMolecule(ft.angle23);
        addToMolecule(ft.torsion);
//        debugPrint();
    }

    void addToMolecule(CMLLength l) {
        CMLLength l1 = getNewLength(l);
        lengthList.remove(l);
        deadLengthList.add(l);
        appendChild(l1);
    }

    void addToMolecule(CMLAngle a) {
        CMLAngle a1 = getNewAngle(a);
        angleList.remove(a);
        deadAngleList.add(a);
        appendChild(a1);
    }

    void addToMolecule(CMLTorsion tor) {
        CMLTorsion t1 = getNewTorsion(tor);
        String atomRef = t1.getAtomRefs4()[3];
        if (!finalAtomSet.contains(atomRef)) {
//            System.out.println("............. added "+atomRef);
            addAtom(atomRef);
        } else {
            throw new CMLRuntimeException("Atom already in atomSet: "+atomRef);
//            System.err.println("Added atom already in atomSet");
        }
        appendChild(t1);
//        System.out.println("added torsion "+tor.getString());
        torsionList.remove(tor);
        deadTorsionList.add(tor);
        List<String> removedAtomIds = removeTorsion(torsionByAtomMap, tor);
        for (String atomId : removedAtomIds) {
//            System.out.println(".............removedAtom: "+atomId);
            deadAtomSet.add(atomId);
            currentAtomSet.remove(atomId);
        }
        currentTorsionSet.remove(tor);
//        debugPrint();
    }
    
    void debugPrint() {
        System.out.println("------DEAD ATOMS------");
        for (String dead : deadAtomSet) {
            System.out.println(dead);
        }
        System.out.println("------DEAD LENGTH------");
        for (CMLLength dead : deadLengthList) {
            System.out.println(dead.getString());
        }
        System.out.println("------DEAD ANGLE------");
        for (CMLAngle dead : deadAngleList) {
            System.out.println(dead.getString());
        }
        System.out.println("------DEAD TORSION------");
        for (CMLTorsion dead : deadTorsionList) {
            System.out.println(dead.getString());
        }
        System.out.println("-------CURRENT ATOMS---------");
        for (String current : currentAtomSet) {
            System.out.println(current);
        }
        System.out.println("-------FINAL ATOM SET---------");
        for (String finalx : finalAtomSet) {
            System.out.println(finalx);
        }
        System.out.println("-------LENGTH LIST---------");
        for (CMLLength l : lengthList) {
            System.out.println(l.getString());
        }
        System.out.println("--------ANGLE LIST--------");
        for (CMLAngle a : angleList) {
            System.out.println(""+a.getString());
        }
        System.out.println("-------TORSION LIST---------");
        for (CMLTorsion t : torsionList) {
            System.out.println(t.getString());
        }
        System.out.println("-------CURRENT TORSION SET---------");
//        for (CMLTorsion t : currentTorsionSet) {
//            System.out.println(t.getString());
//        }
    }
    
    void addAtom(String atomRef) {
        currentAtomSet.add(atomRef);
        finalAtomSet.add(atomRef);
        List<CMLTorsion> tList = torsionByAtomMap.get(atomRef);
        if (tList != null) {
            for (CMLTorsion t : tList) {
                currentTorsionSet.add(t);
            }
        }
    }
    
    void addSprout(Sprout sprout) {
//        System.out.println(".........SPROUT "+sprout);
        addToMolecule(sprout.length);
        addToMolecule(sprout.angle);
        addToMolecule(sprout.torsion);
//        System.out.println("............TOR............."+sprout.torsion.getString());
        finalAtomSet.add(sprout.atom);
    }
    
    CMLLength getNewLength(CMLLength l) {
        String[] a = l.getAtomRefs2();
        CMLLength length = new CMLLength(l);
        if (finalAtomSet.contains(a[1])) {
            String[] aa = new String[2];
            aa[0] = a[1];
            aa[1] = a[0];
            length.setAtomRefs2(aa);
        }
        return length;
    }
    
    CMLAngle getNewAngle(CMLAngle ang) {
        String[] a = ang.getAtomRefs3();
        CMLAngle angle = new CMLAngle(ang);
        if (finalAtomSet.contains(a[2])) {
            String[] aa = new String[3];
            aa[0] = a[2];
            aa[1] = a[1];
            aa[2] = a[0];
            angle.setAtomRefs3(aa);
        }
        return angle;
    }
    
    CMLTorsion getNewTorsion(CMLTorsion tor) {
        String[] a = tor.getAtomRefs4();
        CMLTorsion torsion = new CMLTorsion(tor);
        if (finalAtomSet.contains(a[3])) {
            String[] aa = new String[4];
            aa[0] = a[3];
            aa[1] = a[2];
            aa[2] = a[1];
            aa[3] = a[0];
            torsion.setAtomRefs4(aa);
        }
        return torsion;
    }
    
    Sprout getNextSprout() {
        Sprout sprout = null;
//        System.out.println("SIZE "+currentTorsionSet.size());
        for (CMLTorsion t : currentTorsionSet) {
//            System.out.println("............TORS "+t.getString());
            List<String> freeAtomList = getFreeAtomList(t);
            if (freeAtomList.size()== 1) {
                String atom = freeAtomList.get(0);
//                System.out.println("ATOM "+atom);
                if (!deadAtomSet.contains(atom)) {
//                    System.out.println("+++++++ATOM "+atom);
                    sprout = getSprout(t, atom);
                    if (sprout != null) {
//                        System.out.println("?????ATOM "+atom);
                        break;
                    }
                }
            } else if (freeAtomList.size() > 1) {
//                for (String freeAtom  : freeAtomList) {
//                    System.out.println("FREE ATOM "+t.getString()+"..."+freeAtom);
//                }
//                System.out.println("...................");
            } else {
//                System.out.println("NO FREE ATOM "+t.getString());
            }
        }
        return sprout;
    }
    
    Sprout getSprout(CMLTorsion t, String atom) {
        String[] atomRefs4 = t.getAtomRefs4();
        CMLLength length = null;
        CMLAngle angle = null;
        if (atom.equals(atomRefs4[0])) {
//            System.out.println("00000000000000000");
            length = lengthByAtomHashMap.get(
                    CMLBond.atomHash(atomRefs4[0], atomRefs4[1]));
            angle = angleByAtomHashMap.get(
                    CMLAngle.atomHash(atomRefs4[0], atomRefs4[1], atomRefs4[2]));
        } else if (atom.equals(atomRefs4[3])) {
//            System.out.println("3... "+Util.concatenate(atomRefs4, S_SPACE+S_MINUS+S_SPACE));
            length = lengthByAtomHashMap.get(
                    CMLBond.atomHash(atomRefs4[2], atomRefs4[3]));
            if (length == null) {
//                for (String key : lengthByAtomHashMap.keySet()) {
//                    System.out.println("...K..."+key);
//                }
            }
            angle = angleByAtomHashMap.get(
                    CMLAngle.atomHash(atomRefs4[1], atomRefs4[2], atomRefs4[3]));
        } else {
            throw new CMLRuntimeException("Sprout cannot be in middle of torsion");
        }
//        System.out.println("L "+length+" -- A "+angle);
        
        return (length != null && angle != null) ?
            new Sprout(atom, length, angle, t) : null;
    }
    
    private List<String> getFreeAtomList(CMLTorsion t) {
        List<String> list = new ArrayList<String>();
        String[] atomRefs4 = t.getAtomRefs4();
        for (String atomRef : atomRefs4) {
            if (!finalAtomSet.contains(atomRef)) {
                list.add(atomRef);
            }
        }
        return list;
    }
    
    private FullTorsion getFullTorsion(CMLTorsion torsion) {
        String[] a = torsion.getAtomRefs4();
        boolean ok = true;
        CMLLength length1 = null;
        CMLLength length2 = null;
        CMLAngle angle12 = null;
        CMLLength length3 = null;
        CMLAngle angle23 = null;
        length1 = lengthByAtomHashMap.get(CMLBond.atomHash(a[0], a[1]));
        if (length1 == null) {
            ok = false;
        }
        if (ok) {
            length2 = lengthByAtomHashMap.get(CMLBond.atomHash(a[1], a[2]));
            if (length2 == null) {
                ok = false;
            }
        }
        if (ok) {
            angle12 = angleByAtomHashMap.get(CMLAngle.atomHash(a[0], a[1], a[2]));
            if (angle12 == null) {
                ok = false;
            }
        }
        if (ok) {
            length3 = lengthByAtomHashMap.get(CMLBond.atomHash(a[2], a[3]));
            if (length3 == null) {
                ok = false;
            }
        }
        if (ok) {
            angle23 = angleByAtomHashMap.get(CMLAngle.atomHash(a[1], a[2], a[3]));
            if (angle23 == null) {
                ok = false;
            }
        }
        return (!ok) ? null : 
            new FullTorsion(length1, length2, angle12, length3, angle23, torsion);
    }

    private void makeTorsionByAtomMap() {
        for (CMLTorsion torsion : torsionList) {
            String[] atomRefs4 = torsion.getAtomRefs4();
            addTorsion(torsionByAtomMap, atomRefs4[0], torsion);
            addTorsion(torsionByAtomMap, atomRefs4[1], torsion);
            addTorsion(torsionByAtomMap, atomRefs4[2], torsion);
            addTorsion(torsionByAtomMap, atomRefs4[3], torsion);
        }
        for (String atomId : torsionByAtomMap.keySet()) {
            List<CMLTorsion> torsionList = torsionByAtomMap.get(atomId);
            String s = atomId+": ";
            for (CMLTorsion torsion : torsionList) {
                s += " ("+torsion.getString()+S_RBRAK;
            }
//            System.out.println(s);
        }
    }
    
    private void addTorsion(Map<String, List<CMLTorsion>> torsionByAtomMap, String atomId, CMLTorsion torsion) {
        List<CMLTorsion> torsionList = torsionByAtomMap.get(atomId);
        if (torsionList == null) {
            torsionList = new ArrayList<CMLTorsion>();
            torsionByAtomMap.put(atomId, torsionList);
        }
        torsionList.add(torsion);
    }
    
    /** remove torsion from map
     * 
     * @param torsionByAtomMap
     * @param torsion
     * @return list of torsions?
     */
    private List<String> removeTorsion(
        Map<String, List<CMLTorsion>> torsionByAtomMap, 
        CMLTorsion torsion) {
        List<String> deadAtomList = new ArrayList<String>();
        for (String atomRef : torsion.getAtomRefs4()) {
            List<CMLTorsion> torsionList = removeTorsion(torsionByAtomMap, atomRef, torsion);
            if (torsionList == null || torsionList.size() == 0) {
                deadAtomList.add(atomRef);
            }
        }
        return deadAtomList;
    }
    
    private List<CMLTorsion> removeTorsion(
        Map<String, List<CMLTorsion>> torsionByAtomMap, 
        String atomId, CMLTorsion torsion) {
        List<CMLTorsion> torsionList = torsionByAtomMap.get(atomId);
        if (torsionList != null) {
            torsionList.remove(torsion);
        }
        return torsionList;
    }
    
    private List<CMLLength> makeLengthList(CMLElement element) {
        Elements lengthElements = element.getChildCMLElements(CMLLength.TAG);
        List<CMLLength> lengthList = new ArrayList<CMLLength>();
        for (int i = 0; i < lengthElements.size(); i++) {
            lengthList.add((CMLLength)lengthElements.get(i));
        }
        if (lengthList.size() == 0) {
            throw new CMLRuntimeException("no length elements in ZMatrix");
        }
        return lengthList;
    }

    private List<CMLAngle> makeAngleList(CMLElement element, int nlength) {
        Elements angleElements = element.getChildCMLElements(CMLAngle.TAG);
        List<CMLAngle> angleList = new ArrayList<CMLAngle>();
        for (int i = 0; i < angleElements.size(); i++) {
            angleList.add((CMLAngle)angleElements.get(i));
        }
        int nangle = angleList.size();
        if (nangle + 1 != nlength) {
            throw new CMLRuntimeException("wrong number of angle elements ("+nangle+
                    ") for length Elements ("+nlength+") in ZMatrix");
        }
        return angleList;
    }
    
    private List<CMLTorsion> makeTorsionList(CMLElement element, int nlength) {
        Elements torsionElements = element.getChildCMLElements(CMLTorsion.TAG);
        List<CMLTorsion> torsionList = new ArrayList<CMLTorsion>();
        for (int i = 0; i < torsionElements.size(); i++) {
            torsionList.add((CMLTorsion)torsionElements.get(i));
        }
        int ntorsion = torsionList.size();
        if (ntorsion + 2 != nlength) {
            throw new CMLRuntimeException("wrong number of torsion elements ("+ntorsion+
                    ") for length Elements ("+nlength+") in ZMatrix");
        }
        return torsionList;
    }
};

class Sprout {
    String atom;
    CMLLength length;
    CMLAngle angle;
    CMLTorsion torsion;
    /** constructor.
     * 
     * @param atom
     * @param length
     * @param angle
     * @param torsion
     */
    public Sprout(String atom, CMLLength length, CMLAngle angle, CMLTorsion torsion) {
        this.atom = atom;
        this.length = length;
        this.angle = angle;
        this.torsion = torsion;
    }

    /** to string.
     * @return string
     */
    public String toString() {
        return "Sprout: "+atom+"; "+length.getString()+"; "+
        angle.getString()+"; "+torsion.getString();
    }
};
class FullTorsion {
    CMLLength length1;
    CMLLength length2;
    CMLAngle angle12;
    CMLLength length3;
    CMLAngle angle23;
    CMLTorsion torsion;
    /** constructor.
     * 
     * @param l1
     * @param l2
     * @param a12
     * @param l3
     * @param a23
     * @param t
     */
    public FullTorsion(
            CMLLength l1, CMLLength l2, CMLAngle a12, CMLLength l3, CMLAngle a23, CMLTorsion t) {
        length1 = l1;
        length2 = l2;
        angle12 = a12;
        length3 = l3;
        angle23 = a23;
        torsion = t;
    }
}
