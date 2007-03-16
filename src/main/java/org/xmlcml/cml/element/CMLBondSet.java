package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Util;

/**
 * user-modifiable class supporting bondSet. * autogenerated from schema use as
 * a shell which can be edited
 *
 */
public class CMLBondSet extends AbstractBondSet {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    protected Set<CMLBond> set = new LinkedHashSet<CMLBond>();

    protected HashMap<String, CMLBond> idTable;

    protected Map<String, CMLBond> atomRefs2Table;

    protected CMLMolecule molecule = null;

    /**
     * default constructor.
     *
     */
    public CMLBondSet() {
        super();
        init();

    }

    /**
     * copy constructor.
     *
     * @param old
     */
    public CMLBondSet(CMLBondSet old) {
        super(old);
        init();
    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLBondSet(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLBond
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLBondSet();

    }

    /**
     * creates bondSet from list of bond IDs.
     *
     * useful when the ids have been produced elsewhere
     *
     * @param ids
     *            the ids to use
     * @throws CMLException
     *             cannot find bond with id
     */
    // public CMLBondSet(/*CMLMolecule mol, */String[] ids) throws CMLException
    // {
    // CMLBondSet bondSet = this.getBondsById(ids);
    // this.addBondSet(bondSet);
    // }
    /**
     * create from a molecule. gets all bonds in the molecule
     *
     * @param mol
     *            the molecule
     */
    public CMLBondSet(CMLMolecule mol) {
        try {
            for (CMLBond bond : mol.getBonds()) {
                this.addBond(bond);
            }
        } catch (Exception e) {
            ;
        }
    }

    /**
     * creates bondSet from list of bonds.
     *
     * @param bonds
     *            must all be in same molecule
     * @throws CMLException
     *             one or more bonds has no id
     *
     */
    public CMLBondSet(List<CMLBond> bonds) throws CMLException {
        this();
        addBonds(bonds);
    }

    /**
     * creates bondSet from list of bonds.
     *
     * @param bonds
     *            must all be in same molecule
     * @throws CMLException
     *             one or more bonds has no id
     *
     */
    public CMLBondSet(CMLBond[] bonds) throws CMLException {
        addBonds(bonds);
    }

    void init() {
        set = new LinkedHashSet<CMLBond>();
        idTable = new HashMap<String, CMLBond>();
    }

    /** bondSet from a molecule with bondIds.
     *
     * @param mol the molecule
     * @param bondId the ids
     */
    public CMLBondSet(CMLMolecule mol, String[] bondId) {
        this();
        for (int i = 0; i < bondId.length; i++) {
            CMLBond bond = mol.getBondById(bondId[i]);
            if (bond != null) {
                this.addBond(bond);
            }
        }
    }

    /**
     * add bobds to bond set.
     *
     * @param bonds
     *            to add
     */
    public void addBonds(CMLBond[] bonds) {
        if (bonds != null) {
            for (CMLBond b : bonds) {
                this.addBond(b);
            }
        }
    }

    /**
     * create from List of bonds.
     *
     * @param bonds
     *            to add
     * @throws CMLRuntimeException
     *             one or more bonds has no id
     */
    public void addBonds(List<CMLBond> bonds) throws CMLRuntimeException {
        for (CMLBond b : bonds) {
            this.addBond(b);
        }
    }

    /**
     * adds bond to set.
     *
     * @param bond
     *            to add (if null throws Exception)
     * @throws CMLRuntimeException
     *             Bond must have id or duplicate bond
     */
    public void addBond(CMLBond bond) throws CMLRuntimeException {
        /*
         * set.add(bond); idTable.put(bond.getId(), bond);
         */
        if (bond == null) {
            throw new CMLRuntimeException("Cannot add null bond");
        } else if (set.contains(bond)) {
            throw new CMLRuntimeException("duplicate bond in bondSet: " + bond.getId());
        } else {
            set.add(bond);
            if (idTable == null) {
                idTable = new HashMap<String, CMLBond>();
            }
            if (atomRefs2Table == null) {
                atomRefs2Table = new HashMap<String, CMLBond>();
            }
            String id = bond.getId();
            if (id == null) {
                throw new CMLRuntimeException("Bond in bondSet must have id");
            }
            if (this.getBondById(id) != null) {
                throw new CMLRuntimeException("duplicate bond in bondSet: " + id);
            } else {
            }
            idTable.put(bond.getId(), bond);
            addBondId(bond.getId());
            atomRefs2Table.put(CMLBond.atomHash(bond), bond);
        }
    }

    void addBondId(String id) {
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
     * adds bondSet to set.
     *
     * @param bondSet
     *            to add
     * @throws CMLRuntimeException
     *             duplicate bond (should use boolean operations)
     */
    public void addBondSet(CMLBondSet bondSet) throws CMLRuntimeException {
        addBonds(bondSet.getBonds());
    }

    /**
     * gets i'th bond in set.
     * @param i serial
     * @return the bond
     */
    public CMLBond getBond(int i) {
        List<CMLBond> bondList = this.getBonds();
        return (bondList == null || i < 0 || i >= bondList.size()) ? null :
            bondList.get(i);
    }


    /** gets all bonds in set.
     *
     * @return the bonds
     */
    public List<CMLBond> getBonds() {
        List<CMLBond> bonds = new ArrayList<CMLBond>();
        for (CMLBond bond : set) {
            bonds.add(bond);
        }
        return bonds;
    }

    /** gets all atoms in set as atomSet.
     *
     * @return the atomSet
     */
    public CMLAtomSet getAtomSet() {
        getMolecule();
        List<CMLBond> bonds = getBonds();
        CMLAtomSet atomSet = new CMLAtomSet();
        for (CMLBond bond : bonds) {
            List<CMLAtom> atoms = null;
            atoms = bond.getAtoms();
            atomSet.addAtoms(atoms);
        }
        return atomSet;
    }

    /**
     * does bondSet contain bond.
     *
     * @param bond
     *
     * @return true if contains bond
     */
    public boolean contains(CMLBond bond) {
        return set.contains(bond);
    }

    /**
     * gets size of set.
     *
     * @return the size
     */
    public int size() {
        getBonds();
        return set.size();
    }

    /**
     * gets all ids of bonds.
     *
     * @return the bondIds
     */
    public List<String> getBondIDs() {
        List<CMLBond> bonds = getBonds();
        List<String> bondIDs = new ArrayList<String>();
        if (bonds != null) {
            for (CMLBond bond : bonds) {
                bondIDs.add(bond.getId());
            }
        }
        return bondIDs;
    }

    /**
     * gets bonds by ids.
     *
     * @param ids
     *            the ids
     * @return the bonds
     * @throws CMLRuntimeException
     *             cannot find bond for id or incomplete bond info
     */
    public CMLBondSet getBondsById(String[] ids) throws CMLRuntimeException {
        CMLBondSet bondSet = new CMLBondSet();
        for (String id : ids) {
            CMLBond bond = this.getBondById(id);
            if (bond != null) {
                bondSet.addBond(bond);
            } else {
                throw new CMLRuntimeException("Cannot find bond: " + id);
            }
        }
        return bondSet;
    }

    /**
     * gets bond by id.
     *
     * @param id
     * @return the bond
     */
    public CMLBond getBondById(String id) {
        return idTable.get(id);
    }

    /**
     * compare two bond sets for content.
     *
     * compare unordered bonds
     * @param otherBondSet to compare.
     *
     * @return true if identical and non null
     */
    public boolean hasContentEqualTo(CMLBondSet otherBondSet) {
        boolean result = false;
        if (otherBondSet != null && this.size() == otherBondSet.size()) {
            CMLBondSet bondSet = this.complement(otherBondSet);
            result = bondSet.size() == 0;
        }
        return result;
    }

    /**
     * Returns complement of this bondSet with another.
     *
     * Creates new bondSet containing the bonds that are in this bondSet, and
     * not the one supplied.
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) complement (as2) = {a1, a3}<br>
     * (as2) complement (as1) = {a4}
     *
     * @param bondSet2
     *            bondSet to complement; if null assumed empty
     * @return bondSet
     */
    public CMLBondSet complement(CMLBondSet bondSet2) {
        if (bondSet2 == null) {
            return this;
        }
        CMLBondSet newBondSet = new CMLBondSet();

        List<CMLBond> bonds = this.getBonds();
        for (int i = 0; i < bonds.size(); i++) {
            if (!bondSet2.contains(bonds.get(i))) {
                newBondSet.addBond(bonds.get(i));
            }
        }
        return newBondSet;
    }

    /**
     * Returns union of this bondSet with another.
     *
     * Creates new bondSet containing the bonds that are in this bondSet, and/or
     * the one supplied. (Inclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) union (as2) = (as2) union (as1) = {a1, a2, a3, a4}
     *
     * @param bondSet2
     *            bondSet to unite with
     * @throws CMLRuntimeException
     * @return bond set
     */
    public CMLBondSet union(CMLBondSet bondSet2) throws CMLRuntimeException {
        CMLBondSet newBondSet = new CMLBondSet();

        List<CMLBond> bonds = this.getBonds();
        newBondSet.addBonds(bonds);
        List<CMLBond> bonds2 =  bondSet2.getBonds();
        for (CMLBond bond2 : bonds2) {
            if (!newBondSet.contains(bond2)) {
                newBondSet.addBond(bond2);
            }
        }
        return newBondSet;
    }

    /**
     * Returns symmetric difference of this bondSet with another.
     *
     * Creates new bondSet containing the bonds that are in either bondSet, or
     * the one supplied, but not both. (Exclusive or)
     * <p>
     * eg. as1 = {a1, a2, a3}; as2 = {a2, a4}<br>
     * (as1) symmetric difference (as2) = {a1, a3, a4}
     *
     * @param bondSet2
     *            bondSet to xor with
     * @throws CMLException
     * @return bondSet
     *
     */
    public CMLBondSet symmetricDifference(CMLBondSet bondSet2)
            throws CMLException {
        CMLBondSet newBondSet = new CMLBondSet();

        List<CMLBond> bonds = this.getBonds();
        for (int i = 0; i < bonds.size(); i++) {
            if (!bondSet2.contains(bonds.get(i))) {
                newBondSet.addBond(bonds.get(i));
            }
        }
        List<CMLBond> bonds2 = bondSet2.getBonds();
        for (int i = 0; i < bonds2.size(); i++) {
            CMLBond bond = bonds2.get(i);
            if (!this.contains(bond)) {
                newBondSet.addBond(bond);
            }
        }

        return newBondSet;
    }

    /** get corresponding molecule.
     * @return the molecule (null if none)
     */
    public CMLMolecule getMolecule() {
        if (molecule == null) {
            List<CMLBond> bonds = this.getBonds();
            if (bonds.size() > 0) {
                molecule = CMLMolecule.getMoleculeAncestor(bonds.get(0));
            } else {
                throw new CMLRuntimeException("NO bonds in set...");
            }
        }
        return molecule;
    }


    /**
     * removes bond from set. does NOT remove bond from molecule
     *
     * @param bond
     *            to remove
     * @throws CMLRuntimeException
     *             bond not in set
     */
    public void removeBond(CMLBond bond) throws CMLRuntimeException {
        if (bond != null) {
            if (!set.contains(bond)) {
                throw new CMLRuntimeException("Bond not in set:" + bond.getId() + S_COLON
                        + Util.concatenate(this.getXMLContent(), S_SLASH));
            }
            // remove from set
            set.remove(bond);
            // and from id table
            String id = bond.getId();
            idTable.remove(id);
            // and from XOM XMLContent
            String[] content = this.getXMLContent();
            content = Util.removeElementFromStringArray(content, id);
            this.setXMLContent(content);
            // and adjust size
            int c = this.getSize();
            this.setSize(c - 1);
            // bond.detach(); this kills the bonds relationship to its molecule!
        }
    }

    /**
     * removes bond from set.
     *
     * @param id
     *            of bond to remove
     * @throws CMLRuntimeException
     *             bond not in set
     */
    public void removeBondById(String id) throws CMLRuntimeException {
        removeBond(this.getBondById(id));
    }

    /**
     * removes bondSet from set.
     *
     * @param bondSet
     *            to remove
     * @throws CMLRuntimeException
     *             one or more bonds not in set
     */
    public void removeBondSet(CMLBondSet bondSet) throws CMLRuntimeException {
        if (bondSet != null) {
            for (CMLBond bond : bondSet.getBonds()) {
                if (this.contains(bond))
                    this.removeBond(bond);
            }
        }
    }

	/**
	 * gets average 2D bond length.
	 *
	 * if excludeElements is not null, exclude any bonds including those
	 * excludeElementTypes ELSE if includeElements is not null, include any
	 * bonds including only those excludeElementTypes ELSE use all bonds
	 *
	 * @param excludeElements
	 *            list of element symbols to exclude
	 * @param includeElements
	 *            list of element symbols to include
	 * @return average bond length (NaN if no bonds selected)
	 */
	public double getAverage2DBondLength(String[] excludeElements, String[] includeElements) {
		double sum = 0.0;
		int count = 0;
		List<CMLBond> bonds = getBonds();
		for (CMLBond bond : bonds) {
			String elem0 = bond.getAtom(0).getElementType();
			String elem1 = bond.getAtom(1).getElementType();
			boolean skip = false;
			if (excludeElements != null) {
				skip = Util.containsString(excludeElements, elem0)
				|| Util.containsString(excludeElements, elem1);
			} else if (includeElements != null) {
				skip = !Util.containsString(includeElements, elem0)
				|| !Util.containsString(excludeElements, elem1);
			}
			if (!skip) {
				double length = bond.calculateBondLength(CoordinateType.TWOD);
				sum += length;
				count++;
			}
		}
		return (count == 0) ? Double.NaN : sum / (double) count;
	}


}
