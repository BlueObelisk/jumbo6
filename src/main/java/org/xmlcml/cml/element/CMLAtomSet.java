package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real3Range;
import org.xmlcml.euclid.RealMatrix;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * user-modifiable class supporting atomSet. 
 *
 * in CML documents an atomSet may simply contain the atom Ids.
 * in objects it also contains references to the atoms and molecule
 * the consistency between these is not yet worked out.
 */
public class CMLAtomSet extends AbstractAtomSet {

    final static Logger logger = Logger.getLogger(CMLAtomSet.class.getName());
	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    // arbitrary large distance to unmap atoms of different types
    /** */
    public final static int MAX_DIST = 999999;
    protected CMLMolecule molecule;
    protected LinkedHashSet<CMLAtom> set = new LinkedHashSet<CMLAtom>();
    protected Map<String, CMLAtom> idTable;
    protected boolean checkDuplicates = false;
    
    /**
     * constructor.
     */
    public CMLAtomSet() {
        super();
        checkDuplicates = true;
    }
    
    private void init() {
    	set = new LinkedHashSet<CMLAtom>();
    	idTable = new HashMap<String, CMLAtom>();
    	this.setXMLContent(S_EMPTY);
    	this.setSize(0);
    }

    /** copy constructor. copies references to atoms
     *
     * @param old to copy
     */
    public CMLAtomSet(CMLAtomSet old) {
        super(old);
        init();
        for (CMLAtom atom : old.set) {
            this.addAtom(atom);
        }
        this.molecule = old.molecule;
    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLAtomSet(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLAtom
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLAtomSet();

    }

    /** atomSet from a molecule with atomIds.
     *
     * @param mol the molecule
     * @param atomId the ids
     */
    public CMLAtomSet(CMLMolecule mol, String[] atomId) {
        this();
        for (int i = 0; i < atomId.length; i++) {
            CMLAtom atom = mol.getAtomById(atomId[i]);
            if (atom != null) {
                this.addAtom(atom);
            }
        }
    }

    /** adds atoms from different molecules.
     * fragile. May or may not be a good idea.
     * owner molecule is null
     * @param molecules
     * @return new atomSet
     */
    public static CMLAtomSet createFromMolecules(List<CMLMolecule> molecules) {
        CMLAtomSet atomSet = new CMLAtomSet();
        for (CMLMolecule mol : molecules) {
            atomSet.addAtoms(mol.getAtoms());
            atomSet.molecule = null;
        }
        return atomSet;
    }

    /** adds atoms.
     * fragile. May or may not be a good idea.
     * @param atoms
     * @return new atomSet
     */
    public static CMLAtomSet createFromAtoms(List<CMLAtom> atoms) {
        CMLAtomSet atomSet = new CMLAtomSet();
        atomSet.addAtoms(atoms);
        return atomSet;
    }

    /**
     * create from a molecule.
     *
     * @param mol
     *            the molecule
     */
    public CMLAtomSet(CMLMolecule mol) {
        this.checkDuplicates = false;
        List<CMLAtom> atomList = mol.getAtoms();
        addAtoms(atomList);
    }
    
    /**
     * @param molecules
     */
    public CMLAtomSet(List<CMLMolecule> molecules) {
    	this();
    	for (CMLMolecule molecule : molecules) {
    		this.addAtoms(molecule.getAtoms());
    	}
    }

    /**
     * creates atomSet from array of atoms.
     * @param atoms to add
     */
    public CMLAtomSet(CMLAtom[] atoms) {
        this();
        addAtoms(atoms);
    }

    /**
     * creates atomSet from set of atoms.
     *
     * @param atomSet
     *            to add
     */
    public CMLAtomSet(Set<CMLAtom> atomSet) {
        this();
        if (atomSet != null) {
            for (CMLAtom atom : atomSet) {
                addAtom(atom);
            }
        }
    }

    /**
     * add array of atoms. // *
     *
     * @deprecated use generics
     * @param atoms
     */
    public void addAtoms(CMLAtom[] atoms) {
        if (atoms != null) {
            for (int i = 0; i < atoms.length; i++) {
                this.addAtom(atoms[i]);
            }
        }
    }

    /**
     * add atoms.
     * always use this when adding a list of atoms to avoid QUADRATIC SLOW performance
     * @param atoms
     */
    public void addAtoms(List<CMLAtom> atoms) {
        boolean forceUpdate = false;
        String[] ids = new String[atoms.size()];
        int i = 0;
        for (CMLAtom atom : atoms) {
            this.addAtom(atom, forceUpdate);
            ids[i++] = atom.getId();
        }
        updateContent();
    }

    /** update the string content and size attribute.
     * only required for lazy addition of atoms.
     * most people won't use this.
     *
     */
    public void updateContent() {
        String[] ids = this.getAtomIDs();
        this.setXMLContent(ids);
        this.setSize(ids.length);
    }

    /**
     * adds atom to set.
     * SLOW. alters text content in XOM. Only use if adding single atoms
     * for lists of atoms, use addAtoms()
     * @param atom
     *            to add
     */
    public void addAtom(CMLAtom atom) {
        boolean forceUpdate = true;
        addAtom(atom, forceUpdate);
    }

    /**
     * adds atom to set with lazy option
     * avoids updating every addition. However it must be finished with
     * atomSet.update(); Use forceUpdate=false with care.
     * for lists of atoms, use addAtoms()
     * @param atom to add
     * @param forceUpdate if true forces update of text content (QUADRATIC and SLOW)
     */
    public void addAtom(CMLAtom atom, boolean forceUpdate) {
    	if (atom == null) {
    		throw new RuntimeException("Cannot add null atom");
    	}
        if (atom != null && !set.contains(atom)) {
            set.add(atom);
            if (idTable == null) {
                idTable = new HashMap<String, CMLAtom>();
            }
            idTable.put(atom.getId(), atom);
            if (forceUpdate) {
            	addAtomId(atom.getId());
            }
        }
        if (molecule == null) {
        	molecule = atom.getMolecule();
        } else if (checkDuplicates && molecule != atom.getMolecule()) {
        	throw new CMLRuntimeException("cannot add atoms from different molecules");
        }
    }

    /** add atom id.
     * SLOW: updates set content. Only use when adding a few single atoms
     * else use addAtoms();
     * @param id
     * @param updateXOM if false do not update XML content (valuable when adding many atoms)
     */
    private void addAtomId(String id) {
        String[] content = {};
        int size = 0;

        if (this.getSizeAttribute() != null) {
            content = this.getXMLContent();
            size = this.getSize();
        }

        this.setXMLContent(Util.addElementToStringArray(content, id));
        this.setSize(size + 1);

    }

    /**
     * adds atomSet to set.
     *
     * @param atomSet
     *            to add
     */
    public void addAtomSet(CMLAtomSet atomSet) {
        addAtoms(atomSet.getAtoms());
    }

    /**
     * gets i'th atom in set.
     * @param i serial
     * @return the atom
     */
    public CMLAtom getAtom(int i) {
        List<CMLAtom> atomList = this.getAtoms();
        return (atomList == null || i < 0 || i >= atomList.size()) ? null :
            atomList.get(i);
    }

    /**
     * gets all atoms in set.
     *
     * (not cached - need synchronising with addAtom)
     *
     * @return the atoms
     */
    public List<CMLAtom> getAtoms() {
    	if (molecule == null) {
    		throw new RuntimeException("molecule not set");
    	}
    	checkSetIsSet();
        List<CMLAtom> atoms = new ArrayList<CMLAtom>();

        for (Iterator<CMLAtom> e = set.iterator(); e.hasNext();) {
            atoms.add(e.next());
        }
        return atoms;
    }

    /**
     * gets size of set.
     *
     * @return the size
     */
    public int size() {
    	checkSetIsSet();
        // getAtoms();
        return set.size();
    }

    /**
     * gets all ids of atoms.
     *
     * @return the atomIds
     */
    public String[] getAtomIDs() {

    	checkSetIsSet();
        String[] atomIDs = new String[set.size()];
        int count = 0;
        for (Iterator<CMLAtom> ii = set.iterator(); ii.hasNext();) {
            atomIDs[count++] = ii.next().getId();
        }
        return atomIDs;
    }

    /**
     * gets atoms by ids.
     *
     * @param ids
     * @return the atoms
     */
    public CMLAtomSet getAtomSetById(List<String> ids) {
        CMLAtomSet atomSet = new CMLAtomSet();
        for (String id : ids) {
            CMLAtom atom = (CMLAtom) this.getAtomById(id);
            atomSet.addAtom(atom);
        }
        return atomSet;
    }

    /**
     * gets atoms by ids.
     * convenience
     * @param ids
     * @return the atoms
     */
    public CMLAtomSet getAtomSetById(String[] ids) {
        CMLAtomSet atomSet = new CMLAtomSet();
        for (String id : ids) {
            CMLAtom atom = (CMLAtom) this.getAtomById(id);
            if (atom == null) {
                throw new CMLRuntimeException("unknown atom: "+id);
            }
            atomSet.addAtom(atom);
        }
        return atomSet;
    }

    /**
     * gets atomSubset by elementType.
     *
     * @param elementType
     *            for selecting subset
     * @return the atomSet containing ony atoms with given elementType
     */
    public CMLAtomSet getAtomSetByElementType(String elementType) {

        CMLAtomSet atomSet = new CMLAtomSet();
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            if (elementType.equals(atom.getElementType())) {
                atomSet.addAtom((CMLAtom) atom);
            }
        }
        return atomSet;
    }

    /**
     * gets atom by id.
     *
     * @param id
     * @return the atom
     */
    public CMLAtom getAtomById(String id) {
    	checkSetIsSet();
        return (CMLAtom) ((idTable == null) ? null : idTable.get(id));
    }

	private void checkSetIsSet() {
		if (set == null) {
    		throw new RuntimeException("set not set");
    	}
	}

    /**
     * does atomSet contain atom.
     *
     * @param atom
     *
     * @return true if contains atom
     */
    public boolean contains(CMLAtom atom) {
        return (set != null) && set.contains(atom);
    }

    /**
     * removes atom from set. does NOT remove atom from molecule
     *
     * @param atom
     *            to remove
     * @throws CMLRuntimeException
     *             atom not in set
     */
    public void removeAtom(CMLAtom atom) throws CMLRuntimeException {
    	checkSetIsSet();
        if (atom != null) {
            if (!set.contains(atom)) {
                throw new CMLRuntimeException("atom not in set:" + atom.getId() + S_COLON
                        + Util.concatenate(this.getXMLContent(), S_SLASH));
            }
            // remove from set
            set.remove(atom);
            // and from id table
            String id = atom.getId();
            idTable.remove(id);
            // and from XOM XMLContent
            String[] content = this.getXMLContent();
            content = Util.removeElementFromStringArray(content, id);
            this.setXMLContent(content);
            // and adjust size
            int c = this.getSize();
            this.setSize(c - 1);
            // atom.detach(); this kills the atoms relationship to its molecule!
        }
    }

    /**
     * removes atom from set.
     *
     * @param id
     *            of atom to remove
     * @throws CMLRuntimeException
     *             atom not in set
     */
    public void removeAtomById(String id) throws CMLRuntimeException {
        removeAtom(this.getAtomById(id));
    }

    /**
     * removes atomSet from set.
     *
     * @param atomSet
     *            to remove
     * @throws CMLRuntimeException
     *             one or more atoms not in set
     */
    public void removeAtomSet(CMLAtomSet atomSet) throws CMLRuntimeException {
        if (atomSet != null) {
            for (CMLAtom atom : atomSet.getAtoms()) {
                if (this.contains(atom))
                    this.removeAtom(atom);
            }
        }
    }

    /** gets vector of 3D coordinates.
     * all atoms must have coordinates
     *
     * @param type
     * @return the vector (null if missing 3D coordinates)
     */
    public Point3Vector getCoordinates3(CoordinateType type) {
        List<CMLAtom> atoms = this.getAtoms();
        Point3Vector p3Vector = new Point3Vector();
        boolean ok = true;
        for (CMLAtom atom : atoms) {
            if (!atom.hasCoordinates(type)) {
                ok = false;
                break;
            }
            p3Vector.add(atom.getPoint3(type));
        }
        return (!ok) ? null : p3Vector;
    }

    public void setMolecule(CMLMolecule molecule) {
    	if (this.molecule == molecule) {
    		// no-op
    	} else if (this.molecule == null) {
    		this.molecule = molecule;
    		String[] ids = this.getXMLContent();
    		for (String id : ids) {
    			CMLAtom atom = molecule.getAtomById(id);
    			if (atom == null) {
    				throw new RuntimeException("Cannot find atom with id: "+id);
    			} else {
    				addAtom(atom);
    			}
    		}
    	} else if (this.molecule != molecule) {
    		throw new RuntimeException("Cannot reset molecule");
    	}
    }
    /**
     * translate molecule in 3D.
     *
     * @param delta3 add to all 3D coordinates
     * @deprecated
     */
    public void translate3D(Vector3 delta3) {
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            if (atom.getX3Attribute() != null && atom.getY3Attribute() != null
                    && atom.getZ3Attribute() != null) {
                atom.setX3(atom.getX3() + delta3.getArray()[0]);
                atom.setY3(atom.getY3() + delta3.getArray()[1]);
                atom.setZ3(atom.getZ3() + delta3.getArray()[2]);
            }
        }
    }

    /**
     * translate molecule in 3D.
     *
     * @param delta3 add to all 3D coordinates
     */
    public void translate3D(Vector3 delta3, CoordinateType type) {
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            if (type.equals(CoordinateType.CARTESIAN)) {
	            if (atom.getX3Attribute() != null && atom.getY3Attribute() != null
	                    && atom.getZ3Attribute() != null) {
	                atom.setX3(atom.getX3() + delta3.getArray()[0]);
	                atom.setY3(atom.getY3() + delta3.getArray()[1]);
	                atom.setZ3(atom.getZ3() + delta3.getArray()[2]);
	            }
            } else if (type.equals(CoordinateType.FRACTIONAL)) {
	            if (atom.getXFractAttribute() != null &&
	            		atom.getYFractAttribute() != null &&
	            		atom.getZFractAttribute() != null) {
	                atom.setXFract(atom.getXFract() + delta3.getArray()[0]);
	                atom.setYFract(atom.getYFract() + delta3.getArray()[1]);
	                atom.setZFract(atom.getZFract() + delta3.getArray()[2]);
	            }
            }
        }
    }

    /** get 3D centroid.
     *
     * @param type
     *            CARTESIAN or FRACTIONAL
     * @return centroid of 3D coords or null
     */
    public Point3 getCentroid3(CoordinateType type) {
        Point3 centroid3 = null;
        Point3Vector p3Vector = this.getCoordinates3(type);
        if (p3Vector != null) {
            centroid3 = p3Vector.getCentroid();
        }
        return centroid3;
    }

    /** get 3D range.
     *
     * @param type CARTESIAN or FRACTIONAL
     * @return range of 3D coords or null if no coords
     */
    public Real3Range calculateRange3(CoordinateType type) {
        Real3Range range3 = null;
        Point3Vector p3Vector = this.getCoordinates3(type);
        if (p3Vector != null) {
            range3 = p3Vector.getRange3();
        }
        return range3;
    }


    /**
     * get corresponding molecule.
     *
     * uses parent molecule
     *
     * @return the molecule (null if none)
     */
    public CMLMolecule getMoleculeOrAncestor() {
        if (molecule == null) {

            List<CMLAtom> atoms = this.getAtoms();
            if (atoms.size() > 0) {
                molecule = CMLMolecule.getMoleculeAncestor(atoms.get(0));
            } else {
                throw new CMLRuntimeException("NO atoms in set...");
            }
        }
        return molecule;
    }

    /**
     * get corresponding molecule.
     *
     * uses parent molecule
     *
     * @return the molecule (null if none)
     */
    public CMLMolecule getMolecule() {
        return molecule;
    }

    /**
     * apply crystallographic symmetry.
     *
     *
     * @param orthMat
     * @param transMat
     * @param x
     * @param y
     * @param z
     */
    /*--
     public boolean applySymmetry (final RealSquareMatrix orthMat, final RealMatrix transMat, final int x, final int y, final int z) {
     List<CMLAtom> atoms = this.getAtoms();

     if (atoms != null) {
     RealArray oldCoord = new RealArray (4);
     RealArray newCoord = null;
     RealArray cartCoord = new RealArray (3);
     double [] newData;

     for (int i = atoms.size() - 1; i >= 0; -- i) {
     CMLAtom atom = atoms.get(i);
     oldCoord.setElementAt (0, atom.getXFract ());
     oldCoord.setElementAt (1, atom.getYFract ());
     oldCoord.setElementAt (2, atom.getZFract ());
     oldCoord.setElementAt (3, 1.0);

     try {
     newCoord = transMat.multiply (oldCoord);
     } catch (EuclidRuntimeException ume) {
     logger.info("AtomSetToolImpl :: applySymmetry: UnequalMatricesException");
     logger.info("matrix " + transMat.getRows () + "x" + transMat.getCols ());
     logger.info ("coord " + oldCoord.size ());

     return false;
     }

     newCoord.setElementAt (0, newCoord.elementAt (0) + x);
     newCoord.setElementAt (1, newCoord.elementAt (1) + y);
     newCoord.setElementAt (2, newCoord.elementAt (2) + z);

     try {
     cartCoord = orthMat.multiply (newCoord);
     } catch (EuclidRuntimeException ume) {
     logger.info ("AtomSetToolImpl :: applySymmetry -> Couldn't apply orthMat" + orthMat + " to " + newCoord);

     return false;
     }

     newData = newCoord.getArray ();
     atom.setXFract (newData [0]);
     atom.setYFract (newData [1]);
     atom.setZFract (newData [2]);

     newData = cartCoord.getArray ();
     atom.setX3 (newData [0]);
     atom.setY3 (newData [1]);
     atom.setZ3 (newData [2]);

     }
     } else {
     throw new CMLRuntime ("null atoms");
     }

     return true;
     }
     --*/

    /**
     * transforms all 2D atom coordinates by t. SEEMS IDENTICAL TO transform()
     *
     * @param t2
     *            the transformation
     *
     */
    /*--
     public void transformBy(Transform2 t2) {

     List<CMLAtom> atoms = this.getAtoms();
     for (int i = 0; i < atoms.size(); i++) {
     CMLAtom atom = atoms.get(i);
     Real2 xy2 = atom.getXY2();
     // atom may have no coordinates, skip it
     if (xy2 != null) {
     xy2.transformBy(t2);
     atom.setXY2(xy2);
     }
     }
     }
     --*/

    /**
     * excludes atoms by excludeElementTypes.
     *
     * creates new AtomSet.
     *
     * @param elementTypes
     *            to exclude
     * @return atomSet
     */
    public CMLAtomSet excludeElementTypes(String[] elementTypes) {
        CMLAtomSet newAtomSet = new CMLAtomSet();

        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            String elementType = atom.getElementType();
            boolean exclude = false;
            for (int j = 0; j < elementTypes.length; j++) {
                if (elementType == null || elementType.equals(S_EMPTY)
                        || elementType.equals(elementTypes[j])) {
                    exclude = true;
                    break;
                }
            }
            if (!exclude) {
                newAtomSet.addAtom(atoms.get(i));
            }
        }
        return newAtomSet;
    }

    /**
     * compare two atom sets for priority.
     *
     * assumes sets are in order (i.e. compares each set with each other without
     * normalization or sorting testing is done with atomTool.compareTo(atom)
     * (i.e. no recursion) compare atom[i] with otherAtom[i] until a mismatch is
     * found return atom[i].compareTo(otherAtom[i]); if atomSets are of
     * different length, but match to the lengtn of the shortest, then if
     * atomSet.length < otherAtomSet return -1 else 1 if otherAtomSet is null,
     * return 1 (even if this contains zero atoms)
     *
     * @param otherAtomSet
     *            to compare. If null returns 1
     *
     * @return are sets different (by ordered atom equality)
     */
    public int compareTo(CMLAtomSet otherAtomSet) {
        int result = 0;
        // boolean equals = false;
        if (otherAtomSet == null) {
            result = 1;
        } else {

            List<CMLAtom> atoms = this.getAtoms();
            List<CMLAtom> otherAtoms = otherAtomSet.getAtoms();
            int length = Math.min(atoms.size(), otherAtoms.size());
            for (int i = 0; i < length; i++) {
                int compare = atoms.get(i).compareTo(otherAtoms.get(i));
                if (compare != 0) {
                    result = compare;
                    break;
                }
            }
            if (atoms.size() > length) {
                result = 1;
            } else if (otherAtoms.size() > length) {
                result = -1;
            }
        }
        return result;
    }

    /**
     * compare two atom sets for content.
     *
     * compare unordered atoms
     * @param otherAtomSet
     *            to compare.
     *
     * @return true if identical and non null
     */
    public boolean hasContentEqualTo(CMLAtomSet otherAtomSet) {
        boolean result = false;
        if (otherAtomSet != null && this.size() == otherAtomSet.size()) {
        	if (this.size() == 0) {
        		result = true;
        	} else {
	        	System.out.println("OTHER..........."+otherAtomSet.getMolecule());
	        	System.out.println("THIS..........."+this.getMolecule());
	            CMLAtomSet atomSet = this.complement(otherAtomSet);
	        	System.out.println("ATOM..........."+atomSet.getMolecule());
	            result = atomSet.size() == 0;
	        }
        }
        return result;
    }

    /**
     * includes atoms by excludeElementTypes.
     *
     * creates new AtomSet.
     *
     * @param elementTypes
     *            to include
     * @return atomSet
     */
    public CMLAtomSet includeElementTypes(String[] elementTypes) {
        CMLAtomSet newAtomSet = new CMLAtomSet();

        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            String elementType = atom.getElementType();
            boolean include = false;
            for (int j = 0; j < elementTypes.length; j++) {
                if (elementType.equals(elementTypes[j])) {
                    include = true;
                    break;
                }
            }
            if (include) {
                newAtomSet.addAtom(atom);
            }
        }
        return newAtomSet;
    }

    /**
     * get formula.
     *
     * @param control
     * @return calculated formula
     * @throws CMLRuntimeException
     */
    public CMLFormula getCalculatedFormula(CMLMolecule.HydrogenControl control)
            throws CMLRuntimeException {
        CMLFormula formula = new CMLFormula();
        if (control.equals(CMLMolecule.HydrogenControl.USE_HYDROGEN_COUNT)
        	|| control.equals(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS)
            || control.equals(CMLMolecule.HydrogenControl.NO_EXPLICIT_HYDROGENS)) {
        } else {
            throw new CMLRuntimeException("No hydrogen count control on Formula");
        }
        double hCount = 0;
        for (CMLAtom atom : getAtoms()) {
            String elementType = atom.getElementType();
            double occupancy = (atom.getOccupancyAttribute() == null) ? 1.0
                    : atom.getOccupancy();
            int multiplicity = (atom.getSpaceGroupMultiplicityAttribute() == null) ? 1
                    : atom.getSpaceGroupMultiplicity();
            if (AS.H.equals(elementType)) {
                if (CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS
                        .equals(control)) {
                    hCount += occupancy / (double) multiplicity;
                }
            } else {
                if (CMLMolecule.HydrogenControl.USE_HYDROGEN_COUNT
                        .equals(control)) {
                    hCount += atom.getHydrogenCount() * occupancy;
                }
                formula.add(elementType, occupancy / (double) multiplicity);
            }
        }
        if (hCount > 0.000001) {
            formula.add(AS.H.value, hCount);
        }
        int charge = getCalculatedFormalCharge();
        formula.setFormalCharge(charge);
        formula.normalize();
        return formula;
    }

    /**
     * Calculate formalCharge from atomCharges.
     *
     * @return calculated formal charge
     * @throws CMLRuntimeException
     */
    public int getCalculatedFormalCharge() throws CMLRuntimeException {

        List<CMLAtom> atoms = this.getAtoms();
        int charge = 0;
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            try {
                charge += atom.getFormalCharge();
            } catch (CMLRuntimeException e) {
            }
        }
        return charge;
    }

    /**
     * Returns intersection of this atomSet with another.
     *
     * Creates new atomSet containing the atoms that are in both this atomSet,
     * and the one supplied.
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) intersection (as2) = (as2) intersection (as1) = {a2}
     *
     * @param atomSet2
     *            atomSet to insersect with
     * @return intersection
     * @throws CMLRuntimeException
     */
    public CMLAtomSet intersection(CMLAtomSet atomSet2) throws CMLRuntimeException {
        CMLAtomSet newAtomSet = new CMLAtomSet();

        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            if (atomSet2.contains(atoms.get(i))) {
                newAtomSet.addAtom(atoms.get(i));
            }
        }

        return newAtomSet;
    }

    /**
     * Returns complement of this atomSet with another.
     *
     * Creates new atomSet containing the atoms that are in this atomSet, and
     * not the one supplied.
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) complement (as2) = {a1, a3}<br>
     * (as2) complement (as1) = {a4}
     *
     * @param atomSet2
     *            atomSet to complement; if null assumed empty
     * @return atomSet
     */
    public CMLAtomSet complement(CMLAtomSet atomSet2) {
        if (atomSet2 == null) {
            return this;
        }
        CMLAtomSet newAtomSet = new CMLAtomSet();
        CMLMolecule molecule = this.getMolecule();
        newAtomSet.setMolecule(molecule);

        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            if (!atomSet2.contains(atoms.get(i))) {
                newAtomSet.addAtom(atoms.get(i));
            }
        }
        return newAtomSet;
    }

    /**
     * Returns union of this atomSet with another.
     *
     * Creates new atomSet containing the atoms that are in this atomSet, and/or
     * the one supplied. (Inclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) union (as2) = (as2) union (as1) = {a1, a2, a3, a4}
     *
     * @param atomSet2
     *            atomSet to unite with
     * @throws CMLRuntimeException
     * @return atom set
     */
    public CMLAtomSet union(CMLAtomSet atomSet2) throws CMLRuntimeException {
        CMLAtomSet newAtomSet = new CMLAtomSet();

        List<CMLAtom> atoms = this.getAtoms();
        newAtomSet.addAtoms(atoms);
        newAtomSet.addAtoms(atomSet2.getAtoms());

        return newAtomSet;
    }

    /**
     * Returns symmetric difference of this atomSet with another.
     *
     * Creates new atomSet containing the atoms that are in either atomSet, or
     * the one supplied, but not both. (Exclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) symmetric difference (as2) = {a1, a3, a4}
     *
     * @param atomSet2
     *            atomSet to xor with
     * @throws CMLException
     * @return atomSet
     *
     */
    public CMLAtomSet symmetricDifference(CMLAtomSet atomSet2)
            throws CMLException {
        CMLAtomSet newAtomSet = new CMLAtomSet();

        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            if (!atomSet2.contains(atoms.get(i))) {
                newAtomSet.addAtom(atoms.get(i));
            }
        }
        List<CMLAtom> atoms2 = atomSet2.getAtoms();
        for (int i = 0; i < atoms2.size(); i++) {
            CMLAtom atom = atoms2.get(i);
            if (!this.contains(atom)) {
                newAtomSet.addAtom(atom);
            }
        }

        return newAtomSet;
    }

    /**
     * Returns intersection of this atomSet's atomIds with another's.
     *
     * Creates new atomSet containing the atoms that are in both this atomSet,
     * and the one supplied.
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) intersection (as2) = (as2) intersection (as1) = {a2}
     *
     * @param atomSet2
     *            atomSet to insersect with
     * @return String array of atom Ids
     * @throws CMLException
     */
    public String[] intersectionByAtomId(CMLAtomSet atomSet2)
            throws CMLException {
        CMLAtomSet result = intersection(atomSet2);

        return result.getAtomIDs();
    }

    /**
     * Returns compliment of this atomSet's atomIds with another's.
     *
     * Creates new atomSet containing the atoms that are in this atomSet, and
     * not the one supplied.
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) compliment (as2) = {a1, a3}<br>
     * (as2) compliment (as1) = {a4}
     *
     * @param atomSet2
     *            atomSet to compliment
     * @return String array of atom Ids
     * @throws CMLException
     */
    public String[] complementByAtomId(CMLAtomSet atomSet2) throws CMLException {
        CMLAtomSet result = complement(atomSet2);
        return result.getAtomIDs();
    }

    /**
     * Returns union of this atomSet's atomIds with another's.
     *
     * Creates new atomSet containing the atoms that are in this atomSet, and/or
     * the one supplied. (Inclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) union (as2) = (as2) union (as1) = {a1, a2, a3, a4}
     *
     * @param atomSet2
     *            atomSet to unite with
     * @return ids
     * @throws CMLException
     */
    public String[] unionByAtomId(CMLAtomSet atomSet2) throws CMLException {
        CMLAtomSet result = union(atomSet2);
        return result.getAtomIDs();
    }

    /**
     * Returns symmetric difference of this atomSet's atomIds with another's.
     *
     * Creates new atomSet containing the atoms that are in either atomSet, or
     * the one supplied, but not both. (Exclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) symmetric difference (as2) = {a1, a3, a4}
     *
     * @param atomSet2
     *            atomSet to xor with
     * @return ids
     * @throws CMLException
     */
    public String[] symmetricDifferenceByAtomId(CMLAtomSet atomSet2)
            throws CMLException {
        CMLAtomSet result = symmetricDifference(atomSet2);
        return result.getAtomIDs();
    }

    /**
     * transform 2D coordinates.
     *
     * @param transform
     *            the transformation
     *
     */
    public void transform(Transform2 transform) {
        List<CMLAtom> atoms = this.getAtoms();
        for (CMLAtom atom : atoms) {
            atom.transform(transform);
        }
    }

    /**
     * gets vector of 2D coordinates.
     *
     * all atoms must have coordinates COPIES coordinates so operations on
     * vector do not affect atomset
     *
     * @return the vector (null if missing 2D coordinates)
     */
    public List<Real2> getVector2D() {
        List<CMLAtom> atoms = this.getAtoms();
        List<Real2> p2Vector = new ArrayList<Real2>();
        boolean ok = true;
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            if (atom.getX2Attribute() == null || atom.getY2Attribute() == null) {
                ok = false;
                break;
            }
            Real2 p2 = new Real2(atom.getX2(), atom.getY2());
            p2Vector.add(p2);
        }
        if (!ok) {
            p2Vector = null;
        }
        return p2Vector;
    }

    /**
     * sets vector of 2D coordinates.
     *
     * must be same length as atom set
     *
     * @param p2Vector
     *            the vector
     * @throws CMLException
     *             vector of wrong length
     */
    public void setVector2D(List<Real2> p2Vector) throws CMLException {

        List<CMLAtom> atoms = this.getAtoms();
        if (p2Vector.size() != atoms.size()) {
            throw new CMLException("Vector (" + p2Vector.size()
                    + ") not same length as atoms (" + atoms.size() + S_RBRAK);
        }
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            Real2 coord = p2Vector.get(i);
            atom.setX2(coord.getX());
            atom.setY2(coord.getY());
        }
    }

    /**
     * translate molecule in 2D.
     *
     * @param delta2
     *            add to all 2D coordinates
     */
    public void translate2D(Real2 delta2) {
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            if (atom.getX2Attribute() != null && atom.getY2Attribute() != null) {
                atom.setX2(atom.getX2() + delta2.getX());
                atom.setY2(atom.getY2() + delta2.getY());
            }
        }
    }

    /** multiply all 2D coordinates by given factor.
     * will usually alter centroid of molecule
     *
     * @param scale all 2D coordinates
     */
    public void scale2D(double scale) {
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            Real2 xy = atom.getXY2();
            if (xy != null) {
            	xy = new Real2(xy.multiplyBy(scale));
                atom.setXY2(xy);
            }
        }
    }

    /**
     * get 2D centroid.
     *
     * @return centroid of 2D coords or null
     */
    public Real2 getCentroid2D() {
        Real2 centroid2D = null;
        List<Real2> p2Vector = getVector2D();
        if (p2Vector != null) {
            centroid2D = Real2.getCentroid(p2Vector);
        }
        return centroid2D;
    }
    
    public void translateCentroidToOrigin3(CoordinateType type) {
    	Point3 centroid = this.getCentroid3(type);
    	Vector3 v3 = new Vector3(centroid).multiplyBy(-1.0);
    	this.translate3D(v3, type);
    }

    /**
     * transform 3D cartesian coordinates. modifies this
     *
     * @param transform - if null no-op
     */
    public void transformCartesians(Transform3 transform) {
    	if (transform != null) {
	        for (CMLAtom atom : getAtoms()) {
	            atom.transformCartesians(transform);
	        }
	    }
    }

    /** transform 3D cartesian coordinates. modifies this
     *
     * @param transform
     *            the transformation
     */
    public void transformCartesians(CMLTransform3 transform) {
        for (CMLAtom atom : getAtoms()) {
            atom.transformCartesians(transform);
        }
    }

    /**
     * transform 3D fractional coordinates. modifies this does not affect x3,
     * y3, z3 (may need to re-generate cartesians)
     *
     * @param transform
     *            the transformation
     */
    public void transformFractionals(Transform3 transform) {
        for (CMLAtom atom : getAtoms()) {
            atom.transformFractionals(transform);
        }
    }

    /**
     * transform 3D fractional coordinates. modifies this does not affect x3,
     * y3, z3 (may need to re-generate cartesians)
     *
     * @param transform
     *            the transformation
     */
    public void transformFractionals(CMLTransform3 transform) {
        for (CMLAtom atom : getAtoms()) {
            atom.transformFractionals(transform);
        }
    }

    /**
     * transform fractional and 3D coordinates. does NOT alter 2D coordinates
     * transforms fractionals then applies orthogonalisation to result
     * @param transform
     *            the fractional symmetry transformation
     * @param orthTransform
     *            orthogonalisation transform
     */
    public void transformFractionalsAndCartesians(CMLTransform3 transform, Transform3 orthTransform) {
        for (CMLAtom atom : getAtoms()) {
            atom.transformFractionalsAndCartesians(transform, orthTransform);
        }
    }
    /**
     * translate centroid of atomSet2 to centroid of this.
     *
     * will ALTER coords of atomSet2 (should save and restore if required)
     *
     * @param atomSet2
     *            atomSet to overlap
     * @return shift
     */
    public Real2 overlap2DCentroids(CMLAtomSet atomSet2) {
        Real2 centroid = this.getCentroid2D();
        Real2 centroid2 = atomSet2.getCentroid2D();
        Real2 shift = centroid.subtract(centroid2);
        atomSet2.translate2D(shift);
        centroid2 = atomSet2.getCentroid2D();
        return shift;
    }

    /**
     * get distance matrix.
     *
     * @param atomSet2
     * @return distance matrix
     */
    public RealMatrix getDistanceMatrix(CMLAtomSet atomSet2) {
        List<Real2> coords = this.getVector2D();
        List<Real2> coords2 = atomSet2.getVector2D();
        RealMatrix matrix = null;
        if (coords != null && coords2 != null) {
            matrix = Real2.getDistanceMatrix(coords, coords2);
        }
        return matrix;
    }

    /**
     * Sets chemical element of all atoms in set.
     *
     * @param elementType
     */
    public void setChemicalElements(String elementType) {
        List<CMLAtom> atoms = this.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            atom.setElementType(elementType);
        }
    }

    /**
     * Applies label to all atoms in set.
     *
     * @param labelValue
     * @throws CMLException
     */
    public void labelAtoms(String labelValue) throws CMLException {
        List<CMLAtom> atoms = this.getAtoms();

        if (atoms.size() > 0) {
            for (int i = 0; i < atoms.size(); i++) {
                CMLAtom atom = atoms.get(i);
                CMLLabel label = new CMLLabel();
                label.setCMLValue(labelValue);
                atom.addLabel(label);
            }
        }
    }

    /**
     * creates map from this to atomSet2. sets must be of same length else null
     * result has owner documnent from this from links == this, to links =
     * atomSet2
     *
     * @param atomSet2
     *            other atom set; if null returns null
     * @return map of both or null
     */
    public CMLMap getMap(CMLAtomSet atomSet2) {
        CMLMap map = null;
        List<CMLAtom> fromAtoms = this.getAtoms();
        List<CMLAtom> toAtoms = atomSet2.getAtoms();
        if (atomSet2 != null) {
            toAtoms = atomSet2.getAtoms();
        }
        if (toAtoms != null && toAtoms.size() == fromAtoms.size()) {
            map = new CMLMap();
            for (int i = 0; i < fromAtoms.size(); i++) {
                CMLLink link = new CMLLink();
                link.setTitle("from getMap");
                link.setFrom(fromAtoms.get(i).getId());
                link.setTo(toAtoms.get(i).getId());
                map.addLink(link);
            }
        }
        return map;
    }

    /**
     * split into atoms sets which contain only single elemenTypes.
     *
     * @return atomSetTools mapped by element atomicSymbol (empty map if none)
     */
    public Map<String, CMLAtomSet> splitByElements() {
        Map<String, CMLAtomSet> map = new HashMap<String, CMLAtomSet>();
        List<CMLAtom> atoms = getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            CMLAtom atom = atoms.get(i);
            String el = atom.getElementType();
            CMLAtomSet atomSet = map.get(el);
            if (atomSet == null) {
                atomSet = new CMLAtomSet();
                map.put(el, atomSet);
            }
            atomSet.addAtom(atom);
        }
        return map;
    }

    /**
     * get matched atom. does not work with sets
     *
     * if atom id="a1" and link to="a1" from="b1" and toFrom = Direction.FROM
     * then will return atom id="b1" in atomSet1
     *
     * @param atom0
     *            atom to search with. Its id must occur in a single toFrom
     *            attribute
     * @param map
     *            with links
     * @param toFrom
     *            specifies attribute for target atom
     * @return mapped atom or null
     */
    public CMLAtom getMappedAtom(CMLMap map, CMLAtom atom0, Direction toFrom) {
        CMLAtom targetAtom = null;
        String targetId = (atom0 == null || map == null) ? null : map.getRef(
                atom0.getId(), toFrom);
        if (targetId != null) {
            targetAtom = this.getAtomById(targetId);
        }
        return targetAtom;
    }

    /**
     * get matched atomSet.
     *
     * iterates through atomSet0 finding matches in atomSet1.
     *
     * if atomSet1 = {a0, a1, a2, a3} and atomSet1 = {b2, b3, b4} and link
     * to="a1" from="b1" link to="a2" from="b2" link to="a3" from="b3" link
     * to="a4" from="b4" and toFrom = Direction.FROM then will return {a2, a3}
     *
     * @param map
     *            to search with. Its ids must occur in toFrom attributes
     * @param atomSet1
     *            containing result atoms
     * @param toFrom
     *            specifies attribute for target atom
     * @return mapped atomSet or null
     */
    // FIXME not checked
    public CMLAtomSet getMappedAtomSet(CMLMap map, CMLAtomSet atomSet1,
            Direction toFrom) {
        CMLAtomSet targetAtomSet = new CMLAtomSet();
        List<CMLAtom> atoms1 = this.getAtoms();
        for (CMLAtom atom0 : atoms1) {
            CMLAtom targetAtom = null;
            String targetId = (atom0 == null) ? null : map.getRef(
                    atom0.getId(), toFrom);
            if (targetId != null) {
                targetAtom = atomSet1.getAtomById(targetId);
                targetAtomSet.addAtom(targetAtom);
            }
        }
        return targetAtomSet;

    }

    /**
     * remove any atoms which occur in Direction.FROM or Direction.TO links..
     * probably works with sets
     *
     * @param map
     *            from links may point
     * @param toAtomSet
     *            to which to links may point
     * @throws CMLException
     *             atom not in set
     */
    public void removeAtoms(CMLMap map, CMLAtomSet toAtomSet)
            throws CMLException {
        this.removeAtoms(map, Direction.FROM);
        toAtomSet.removeAtoms(map, Direction.TO);
    }

    /**
     * remove any atoms which occur in links..
     *
     * @param map
     *            to which from/to links may point
     * @param control
     *            Direction.TO or Direction.FROM controls which end of link to
     *            use
     * @throws CMLException
     */
    public void removeAtoms(CMLMap map, Direction control) throws CMLException {
        for (String ref : map.getRefs(control)) {
            this.removeAtomById(ref);
        }
    }

}
