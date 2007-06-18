package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;
import org.xmlcml.molutil.ChemicalElement;

/**
 * user-modifiable class supporting this. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLBond extends AbstractBond {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /** messages.*/
    public enum Message {
        /** no grandparent */
        NULL_GRANDPARENT("null grandparent for bond"),
        ;
        /** value.*/
        public String value;
        private Message(String s) {
            value = s;
        }
    }
    
    final static Logger logger = Logger.getLogger(CMLBond.class.getName());

    /** unspecified bond */
    public final static String UNKNOWN_ORDER = "UNK";

    /** single bond */
    public final static String SINGLE = "1";

    /** double bond */
    public final static String DOUBLE = "2";

    /** triple bond */
    public final static String TRIPLE = "3";

    /** single bond */
    public final static String SINGLE_S = "S";

    /** double bond */
    public final static String DOUBLE_D = "D";

    /** triple bond */
    public final static String TRIPLE_T = "T";

    /** aromatic bond */
    public final static String AROMATIC = "A";

    // same as SMILES
    /** zero bond (disjoint == S_PERIOD in SMILES) */
    public final static String ZERO = S_PERIOD;

    /** wedge bond */
    public final static String WEDGE = "W";

    /** hatch bond */
    public final static String HATCH = "H";

    /** cis bond */
    public final static String CIS = "C";

    /** trans bond */
    public final static String TRANS = "T";

    /** linear bond */
    public final static String LINEAR = "L";

    /** nostereo bond */
    public final static String NOSTEREO = S_MINUS;

    /** acyclic bond */
    public final static String ACYCLIC = "ACYCLIC";

    /** cyclic bond */
    public final static String CYCLIC = "CYCLIC";

    /** unknown cyclicity bond */
    public final static String CYCLIC_UNKNOWN = "UNK";
    
    /** attribute denoting cyclic nature.
     * 
     */
    public final static String USER_CYCLIC = "userCyclic";

    /** bond type */
    public final static String[] bondType = {
    /** */
    SINGLE,
    /** */
    DOUBLE,
    /** */
    TRIPLE,
    /** */
    AROMATIC,
    /** */
    ZERO,
    /** */
    WEDGE,
    /** */
    HATCH,
    /** */
    NOSTEREO, };

    /** */
    public static final double[] bondOrders = { 1.0, 2.0, 3.0, 1.5, 0.0, 1.0,
            1.0, 1.0, };

    /** */
    public final static String HASH_SYMB = S_UNDER+S_UNDER;

    /** */
    public final static String BOND_LINK = S_MINUS;

    List<CMLAtom> atomList;
    
//    protected String cyclic;

    /**
     * no-arg constructor. This should not normally be used in applications as
     * it does not enforce atom and molecule references. Use CMLBond(CMLAtom,
     * CMLAtom) or molecule.createAndAddBond(...);
     * 
     */
    public CMLBond() {
        super();

    }

    /** preferred constructor.
     * @param id
     * @param atom1
     * @param atom2
     * @throws CMLRuntimeException
     *             if atoms are null, identical or don't have molecule owner or
     *             have different molecule owners.or don't have ids.
     */
    public CMLBond(String id, CMLAtom atom1, CMLAtom atom2) throws CMLRuntimeException {
        this(atom1, atom2);
        this.setId(id);
    }
    
    /** Constructor.
     * @param id
     * @param atom1
     * @param atom2
     * @param order
     * @throws CMLRuntimeException
     *             if atoms are null, identical or don't have molecule owner or
     *             have different molecule owners.or don't have ids.
     */
    public CMLBond(String id, CMLAtom atom1, CMLAtom atom2, String order) throws CMLRuntimeException {
        this(id, atom1, atom2);
        setOrder(order);
    }
    
    /**
     * normal constructor.
     * 
     * @param atom1
     * @param atom2
     * @throws CMLRuntimeException
     *             if atoms are null, identical or don't have molecule owner or
     *             have different molecule owners.or don't have ids.
     */
    public CMLBond(CMLAtom atom1, CMLAtom atom2) throws CMLRuntimeException {
        super();
        if (atom1 == null || atom2 == null) {
            throw new CMLRuntimeException("Atoms in bond muct not be null");
        }
        if (atom1 == atom2) {
            throw new CMLRuntimeException("Atoms in bond must be distinct");
        }
        if (atom1.getMolecule() == null || atom2.getMolecule() == null) {
            throw new CMLRuntimeException(
                    "Atoms in bond must have owner molecules");
        }
        if (!atom1.getMolecule().equals(atom2.getMolecule())) {
            throw new CMLRuntimeException(
                    "Atoms in bond must have identical owner molecule");
        }

        String atomId1 = atom1.getId();
        String atomId2 = atom2.getId();
        if (atomId1 == null || atomId2 == null) {
            throw new CMLRuntimeException("Atoms in bond must have ids");
        }
        this.setAtomRefs2(new String[] { atomId1, atomId2 });

    }
    
    /**
     * constructor.
     * 
     * @param atom1
     * @param atom2
     * @param order
     * @throws CMLRuntimeException
     *             if atoms are null, identical or don't have molecule owner or
     *             have different molecule owners.or don't have ids.
     */
    public CMLBond(CMLAtom atom1, CMLAtom atom2, String order) throws CMLRuntimeException {
        this(atom1, atom2);
        setOrder(order);
    }

    /**
     * copy constructor.
     * 
     * @param old
     *            to copy
     */
    public CMLBond(CMLBond old) {
        super((AbstractBond) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLBond(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLBond
     */
    public CMLBond makeElementInContext(Element parent) {
        CMLBond bond = null;
        if (parent == null) {
            bond = new CMLBond();
        } else if (parent instanceof CMLBondArray) {
            Element grandParent = (Element) parent.getParent();
            if (!(grandParent instanceof CMLMolecule)) {
                throw new CMLRuntimeException("Bond needs molecule grandparent");
            } else {
                bond = new CMLBond();
            }
        } else {
            throw new CMLRuntimeException("Bond needs bondArray parent");
        }
        return bond;
    }

    /**
     * callback when constructing from XML. manages atomRefs2
     * 
     * @param parent
     *            element
     */
    public void finishMakingElement(Element parent) {
        getMolecule();
        // check attributes are present
        if (this.getAtomRefs2() != null) {
        } else if (this.getAtomRefs() != null) {
        } else if (this.getChildCMLElements(CMLString.TAG).size() > 1) {
            // CML-1 syntax
        } else {
            throw new CMLRuntimeException("bond must have AtomRefs2 or atomRefs");
        }
        // we need checks that atoms are actually in molecule..
    }

    /** set id.
     * this will index the bond if it has a parent.
     * id cannot be reset.
     * @param id
     */
    public void setId(String id) {
        /*
        if (this.getId() != null) {
            throw new CMLRuntimeException("Cannot reindex id");
        }
        */
        super.setId(id);
        ParentNode parent = this.getParent();
        if (parent != null && parent instanceof CMLBondArray) {
            CMLBondArray bondArray = (CMLBondArray) parent;
            bondArray.getBondIdMap().put(id, this);
        }
    }

    
    /*-----------end  required for each subclass ------------------*/

    /**
     * gets normalised order.
     * 
     * since CML allows both "1" and "S" for single, gets a consistent
     * representation
     * 
     * @return SINGLE, DOUBLE, etc. as appropriate; null returns null
     */
    public String getOrder() {
        String order = super.getOrder();
        if (order == null) {
            order = null;
        } else if (order.equals(CMLBond.SINGLE)
                | order.equals(CMLBond.SINGLE_S)) {
            order = CMLBond.SINGLE;
        } else if (order.equals(CMLBond.DOUBLE)
                | order.equals(CMLBond.DOUBLE_D)) {
            order = CMLBond.DOUBLE;
        } else if (order.equals(CMLBond.TRIPLE)
                | order.equals(CMLBond.TRIPLE_T)) {
            order = CMLBond.TRIPLE;
        } else if (order.equals(CMLBond.AROMATIC)) {
        } else {
            order = UNKNOWN_ORDER;
        }
        return order;
    }
    
    CMLBondArray getBondArray() {
        ParentNode parent = this.getParent();
        return (parent == null || !(parent instanceof CMLBondArray)) ? null :
            (CMLBondArray) parent;
    }
    
    /** remove bond.
     * routed to bondArray.removeBond()
     */
    public void detach() {
        CMLBondArray bondArray = getBondArray();
        if (bondArray != null) {
            bondArray.removeBond(this);
        }
    }
    
    /**
     * gets the owner molecule.
     * 
     * @return the molecule (null if none)
     */
    public CMLMolecule getMolecule() {
        CMLMolecule molecule = null;
        Node bondArray = this.getParent();
        if (bondArray != null) {
            Node grandParent = bondArray.getParent();
            if (grandParent != null) {
                if (grandParent instanceof CMLMolecule) {
                    molecule = (CMLMolecule) grandParent;
                } else {
                    throw new CMLRuntimeException("grandParent of bond is not a molecule: "+molecule.getClass());
                }
            } else {
                throw new CMLRuntimeException(Message.NULL_GRANDPARENT.value);
            }
        }
        return molecule;
    }

    /** Gets id of n'th atom in bond.
     * @param i serial of atom
     * @return id or null
     */
    public String getAtomId(int i) {
        CMLAtom atom = this.getAtom(i);
        return (atom == null) ? null : atom.getId();
    }
    
    
    /**
     * Gets n'th atom in bond.
     * @param i serial of atom
     * @return atom or null
     */
    public CMLAtom getAtom(int i) {
        List<CMLAtom> atomList = this.getAtoms();
        return (atomList == null || i < 0 || i >= atomList.size()) ? null :
            atomList.get(i);
    }
    
    /** Gets a typed List of all the atoms involved in the bond
     * 
     * @return a list of 2 atoms
     * @throws CMLRuntimeException
     *             if molecule is null, no atomRefs2 attribute or atom is not in
     *             bond
     */
    public List<CMLAtom> getAtoms() throws CMLRuntimeException {
        if (atomList == null) {
            atomList = new ArrayList<CMLAtom>();
    
            String[] atomRefs2 = this.getAtomRefs2();
            if (atomRefs2 == null) {
                throw new CMLRuntimeException("bond has no atomRefs2");
            }
            CMLMolecule molecule = this.getMolecule();
            if (molecule == null) {
                throw new CMLRuntimeException("bond has no parent molecule");
            }
            addAtom(atomList, molecule, atomRefs2[0]);
            addAtom(atomList, molecule, atomRefs2[1]);
        }
        return atomList;
    }

    /** get calculated bond length.
     * independent of any CMLLength element that might describe this bond.
     * @return length (or Double.NaN if impossible)
     */
    public double getBondLength() {
        double d = Double.NaN;
        List<CMLAtom> atoms = this.getAtoms();
        if (atoms.size() == 2) {
            d = atoms.get(0).getDistanceTo(atoms.get(1));
        }
        return d;
    }
    
    private void addAtom(List<CMLAtom> atomList, CMLMolecule molecule, String id) {
        CMLAtom atom = molecule.getAtomById(id);
        if (atom == null) {
//            CMLMolecule molecule = this.getMolecule();
            String molId = (molecule == null) ? null : molecule.getId();
            throw new CMLRuntimeException("Non-existent atom in bond/mol " + id+S_SLASH+molId);
        }
        atomList.add(atom);
    }
    

    /** gets id of other atom in bond.
     * 
     * @param id known id
     * @return the other id (null if id = null)
     */
    public String getOtherAtomId(String id) {
        String id1 = null;
        if (id != null) {
            String[] atomRefs2 = this.getAtomRefs2();
            if (atomRefs2 != null) {
                if (id.equals(atomRefs2[0])) {
                    id1 = atomRefs2[1];
                } else if (id.equals(atomRefs2[1])) {
                    id1 = atomRefs2[0];
                } else {
                }
            }
        }
        return id1;
    }
    
    /** gets other atom in bond.
     * 
     * @param atom
     * @return the other atom (null if atom = null)
     */
    public CMLAtom getOtherAtom(CMLAtom atom) {
        CMLAtom otherAtom = null;
        if (atom != null) {
            atomList = getAtoms();
            if (atomList != null) {
                int idx = atomList.indexOf(atom);
                if (idx == -1) {
                    throw new CMLRuntimeException("atom not in bond: "+atom.getId());
                }
                otherAtom = atomList.get(1 - idx);
            }
        }
        return otherAtom;
    }
    
    /**
     * append string to id. perhaps to identify molecule
     * 
     * @param s
     * @param updateRefs
     */
    public void appendToId(String s, boolean updateRefs) {
        String id = this.getId();
        if ((id != null) && (id.length() > 0)) {
            this.resetId(id + s);

            if (updateRefs) {
                String[] refs = this.getAtomRefs2();

                if (refs != null) {
                    for (int j = refs.length - 1; j >= 0; --j) {
                        refs[j] += s;
                    }
                }
                this.setAtomRefs2(refs);
            }
        }
    }
    
    /** adds bond info as ligands to atoms.
     * 
     * @param bond
     */
    void updateLigands() {
        List<CMLAtom> atoms = this.getAtoms();
        atoms.get(0).addLigandBond(this, atoms.get(1));
        atoms.get(1).addLigandBond(this, atoms.get(0));
    }


    /** sets atomRefs in bond. convenience method
     * be careful
     * @param atomId1
     * @param atomId2
     * @throws CMLRuntimeException
     */
    void setAtomRefs2(String atomId1, String atomId2)
            throws CMLRuntimeException {
        this.setAtomRefs2(new String[] { atomId1, atomId2 });
    }

    /**
     * get hash from two atoms.
     * 
     * @param atomId1
     * @param atomId2
     * @return the hash
     */
    public static String atomHash(String atomId1, String atomId2) {
        if (atomId1 == null || atomId2 == null) {
            return null;
        }
        if (atomId1 == atomId2) {
            return null;
        }
        if (atomId1.compareTo(atomId2) < 0) {
            String temp = atomId2;
            atomId2 = atomId1;
            atomId1 = temp;
        }
        return atomId1 + HASH_SYMB + atomId2;
    }

    /**
     * get atom hash.
     * 
     * @param atomRefs2
     * @return atom hash
     */
    public static String atomHash(String[] atomRefs2) {
        return (atomRefs2 == null) ? null
                : atomHash(atomRefs2[0], atomRefs2[1]);
    }

    /**
     * uses atomRefs2 to create hash.
     * 
     * @return null if no atomRefs2
     */
    public String atomHash() {
        return atomHash(this.getAtomRefs2());
    }

    /**
     * get unique hash for pair of atoms. normal use is to identify bond
     * 
     * @param atom1
     * @param atom2
     * @return hash string or null
     */
    public static String atomHash(CMLAtom atom1, CMLAtom atom2) {
        String hash = null;
        if (atom1 != null && atom2 != null) {
            hash = atomHash(atom1.getId(), atom2.getId());
        }
        return hash;
    }

    /**
     * get atom hash.
     * 
     * @param bond
     * @return hash
     * @throws CMLRuntimeException
     *             no atomRefs2 attribute
     */
    public static String atomHash(CMLBond bond) throws CMLRuntimeException {
        String hash = null;
        if (bond != null) {
            String[] atomRefs2 = bond.getAtomRefs2();
            if (atomRefs2 == null) {
                throw new CMLRuntimeException("no atomRefs2 attribute");
            }
            hash = atomHash(atomRefs2[0], atomRefs2[1]);
        }
        return hash;
    }

    /**
     * set cyclicity.
     * 
     * @param c
     *            cyclicity (UNKNOWN, ACYCLIC or CYCLIC)
     */
    public void setCyclic(String c) {
        this.addAttribute(new Attribute(USER_CYCLIC, c));
    }

    /**
     * get cyclicity.
     * 
     * @return cyclicity (UNKNOWN, ACYCLIC or CYCLIC)
     */
    public String getCyclic() {
        return this.getAttributeValue(USER_CYCLIC);
    }

    /**
     * get stereo.
     * 
     * @return stereo
     */
    public CMLBondStereo getBondStereo() {
        return (CMLBondStereo) this.getFirstCMLChild(CMLBondStereo.TAG);
    }

    /**
     * set CMLBondStereo child.
     * 
     * remove any previous CMLBondStereo and then append this one
     * 
     * @param stereo
     *            to add
     */
    public void setBondStereo(CMLBondStereo stereo) {
        clearBondStereo();
        try {
            this.addBondStereo(stereo);
        } catch (Exception e) {
            throw new CMLRuntimeException("BUG " + e);
        }
    }

    /**
     * removes all bondStereo children.
     * 
     */
    public void clearBondStereo() {
        while (this.getBondStereoElements().size() > 0) {
            this.removeChild(this.getBondStereo());
        }
    }

    /**
     * are atoms bonded. is interatom distance less than radius1 + radius2 +
     * tolerance?
     * 
     * @param atom1
     * @param atom2
     * @return true if within sum of radii + tolerance
     */
    public static boolean areWithinBondingDistance(final CMLAtom atom1,
            final CMLAtom atom2) {
    	List<CMLAtom> atomList = new ArrayList<CMLAtom>(2);
    	atomList.add(atom1);
    	atomList.add(atom2);
    	
    	double covd = 0.0;
    	for (CMLAtom atom : atomList) {
    		ChemicalElement el = atom.getChemicalElement();
    		if (el == null) {
    			throw new CMLRuntimeException("cannot find chemicalElement for atom ("+
    					atom.getId()+"):"+atom.getElementType());
    		}
    		double radius = el.getTypeAdjustedCovalentRadius();
    		covd += radius;
    	}

    final double dx = atom1.getX3() - atom2.getX3();
        final double dy = atom1.getY3() - atom2.getY3();
        final double dz = atom1.getZ3() - atom2.getZ3();
        final double d2 = dx * dx + dy * dy + dz * dz;
        
        double covd2 = covd * covd;
        
        //System.out.println(atom1.getId()+"/"+atom2.getId()+" : d2 "+
        //		d2+", covd2 "+covd2);
        return (d2 < covd2);
    }

    /**
     * increments bond order by given amount. use with care. Only really useful
     * when generating bond orders
     * 
     * @param delta
     *            (should really only be 1 or -1)
     */
    public void incrementOrder(int delta) {
        String order = this.getOrder();
        if (delta == 1) {
            if (order == null || order.equals(CMLBond.SINGLE)) {
                order = CMLBond.DOUBLE;
            } else if (order.equals(CMLBond.DOUBLE)) {
                order = CMLBond.TRIPLE;
            } else if (order.equals(CMLBond.TRIPLE)) {
                order = "4";
            } else {
                throw new CMLRuntimeException("Cannot increment bond order " + order);
            }
        } else if (delta == 2) {
            if (order == null) {
                order = CMLBond.DOUBLE;
            } else if (order.equals(CMLBond.SINGLE)) {
                order = CMLBond.TRIPLE;
            } else if (order.equals(CMLBond.DOUBLE)) {
                order = "4";
            } else {
                throw new CMLRuntimeException("Cannot increment bond order " + order);
            }
        } else if (delta == -1) {
            if (order == null) {
                throw new CMLRuntimeException("Cannot decrement bond order " + order);
            } else if (order.equals(CMLBond.DOUBLE)) {
                order = CMLBond.SINGLE;
            } else if (order.equals(CMLBond.TRIPLE)) {
                order = CMLBond.DOUBLE;
            } else {
                throw new CMLRuntimeException("Cannot decrement bond order " + order);
            }
        } else if (delta == 0) {
            // no-op
        } else {
            throw new CMLRuntimeException("Cannot change bond order by " + delta);
        }
        this.setOrder(order);
    }

    /** create id for potential bond.
     * for is atom1.getId()-atom2.getId()
     * @param atom1
     * @param atom2
     * @return the id
     */
    public static String createId(CMLAtom atom1, CMLAtom atom2) {
        String[] atomR2 = new String[2];
        atomR2[0] = atom1.getId();
        atomR2[1] = atom2.getId();
        return createId(atomR2);
    }
    
    private static String createId(String[] atomR2) {
        return atomR2[0]+BOND_LINK+atomR2[1];
    }
    
    /** create id for bond.
     * for is atom1.getId()-atom2.getId()
     * @return the id
     */
    public String createId() {
        return createId(this.getAtomRefs2());
    }
    
    /** string for bond.
     * 
     * @return the string
     */
    public String getString() {
//        CMLMolecule molecule = this.getMolecule();
        String s = S_EMPTY;
        String[] atomRefs2 = this.getAtomRefs2();
        if (atomRefs2 != null) {
            s = atomHash();
//            s += atomRefs2[0]+S_UNDER+atomRefs2[1];
        }
        return s;
    }
    
    /** renames atomRef in bond
     * 
     * no checks are made for uniqueness, etc. not recommended for general use
     * 
     * @param oldId
     *            the old atomRef id
     * @param newId
     *            the new atomRef id
     * @throws CMLRuntimeException
     *             oldId not found in atomRefs2
     */
    void renameAtomRef(String oldId, String newId) {
        String[] atomRefs2 = this.getAtomRefs2();
        // int idx = atomRefs2.indexOf(S_SPACE);
        // String atomRef0 = atomRefs2.substring(0, idx);
        // String atomRef1 = atomRefs2.substring(idx+1);
        String newAtomRef0 = S_EMPTY;
        String newAtomRef1 = S_EMPTY;
        if (oldId.equals(atomRefs2[0])) {
            newAtomRef0 = newId;
            newAtomRef1 = atomRefs2[1];
        } else if (oldId.equals(atomRefs2[1])) {
            newAtomRef0 = atomRefs2[0];
            newAtomRef1 = newId;
        } else {
            throw new CMLRuntimeException("Cannot find find atomRef: " + oldId
                    + " in atomRefs2: " + atomRefs2);
        }
        // getMolecule().moleculeBonds.reKeyBond(this, newAtomRef0,
        // newAtomRef1);
        this.setAtomRefs2(new String[] { newAtomRef0, newAtomRef1 });
    }

    /**  new id and set it.
     * 
     * @return the id
     */
    public String generateAndSetId() {
        String bondId = this.getId();
        if (bondId != null) {
            this.removeAttribute(IdAttribute.NAME);
        }
        List<CMLAtom> atomList = this.getAtoms();
        String at0id = atomList.get(0).getId();
        String at1id = atomList.get(1).getId();

        bondId = at0id + S_UNDER + at1id;
        this.setId(bondId);
        return bondId;
    }

	/**
	 * get bond length.
	 *
	 * uses 3D atom coordinates, else 2D atom coordinates, to generate length
	 *
	 * @param type
	 * @return the length
	 * @throws CMLRuntimeException if not computable (no coord, missing atoms...)
	 */
	public double calculateBondLength(CoordinateType type) {
		CMLAtom atom0 = null;
		CMLAtom atom1 = null;
		List<CMLAtom> atomList = getAtoms();
		atom0 = atomList.get(0);
		atom1 = atomList.get(1);
		if (atom0 == null || atom1 == null) {
			throw new CMLRuntimeException("missing atoms");
		}
		double length = -1.0;
		if (type.equals(CoordinateType.CARTESIAN)) {
			Point3 p0 = atom0.getXYZ3();
			Point3 p1 = atom1.getXYZ3();
			if (p0 == null || p1 == null) {
				throw new CMLRuntimeException(
						"atoms do not have 3D coordinates");
			}
			length = p0.getDistanceFromPoint(p1);
		} else if (type.equals(CoordinateType.TWOD)) {
			Real2 p0 = atom0.getXY2();
			Real2 p1 = atom1.getXY2();
			if (p0 == null || p1 == null) {
				throw new CMLRuntimeException(
						"atoms do not have 2D coordinates");
			}
			length = p0.getDistance(p1);
		}
		return length;
	}

    
}
