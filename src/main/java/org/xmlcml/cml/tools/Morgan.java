package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;

/**
 * class to apply Morgan algorithm.
 * 
 * <pre>
 *  The simple Morgan algorithm recursively adds neighbouring connectivity until the 
 *  number of equivalence classes no longer changes. Thus methyl cyclopropane
 *  a1 hydrogenCount = 3
 *  a2 hydrogenCount = 1
 *  a3 hydrogenCount = 2
 *  a4 hydrogenCount = 2
 *  a1-a2
 *  a2-a3
 *  a2-a4
 *  a3-a4
 *  gives equivalence classes for Carbons:
 *  a1, a2, (a3, a4)
 *  (each described by a unique number)
 *  the result is an ordered list of equivalence classes. Each class has an atomSet of the atoms 
 *  in that class. To map one molecule to another the equivalenceClassList for each is calculated and 
 *  then the atoms in each class can be mapped
 * </pre>
 * 
 * the output is to a CMLMap.
 * 
 * @author pm286 Copyright P.Murray-Rust, 29-May-2005 Artistic license
 * 
 */
public class Morgan extends AbstractTool {

    /** decides on type of algorithm.
     * no choice at present
     * @author pm286
     *
     */
    public enum Algorithm {
        /** simple morgan algorithm; returns equivalence classes
         */
        SIMPLE,
        /** tries to split equivalence classes
         */
        SPLIT;
    }
    
    /** annotation for atoms.
     * 
     * @author pm286
     *
     */
    public enum Annotation {
        /** marked for equivalence class */
        MARKED,
        /** existing morgan numbers */
        NUMBER,
        /** accumulator for next numbers; recursively transferredd to MORGAN0 */
        NEXTNUMBER;
    }

    final static Logger logger = Logger.getLogger(Morgan.class.getName());

    private Algorithm algorithm = null;

    private CMLAtomSet constantAtomSet;

    private List<Long> morganList = null;

    private Map<Long, CMLAtomSet> equivalenceMap = null;

    private int nClasses = -1;
    
    private List<CMLAtom> markedAtomsList = null;
    /**
     * constructor
     * 
     * @param molecule
     */
    public Morgan(CMLMolecule molecule) {
        if (molecule == null) {
            throw new CMLRuntimeException("Null molecule");
        }
        this.constantAtomSet = molecule.getAtomSet();
        init();
    }

    void init() {
        this.algorithm = Algorithm.SIMPLE;
        clean();
    }
    
    private void clean() {
        equivalenceMap = new HashMap<Long, CMLAtomSet>();
        morganList = null;
    }

    /**
     * constructor.
     * 
     * @param atomSet
     */
    public Morgan(CMLAtomSet atomSet) {
        this.constantAtomSet = new CMLAtomSet(atomSet);
        init();
    }

    /**
     * set algorithm.
     * @param alg
     */
    public void setAlgorithm(Algorithm alg) {
        algorithm = alg;
    }

    /** get map between morgan numbers and atomSets (equivalences).
     * 
     * @return map
     */
    public Map<Long, CMLAtomSet> getEquivalenceMap() {
        return equivalenceMap;
    }

    /** get list of atoms required to break equivalence classes.
     * should be null unless SPLIT is used.
     * @return list of marked atoms (or null)
     */
    public List<CMLAtom> getMarkedAtomList() {
        return markedAtomsList;
    }
    /**
     * return ordered (by atoms in molecule or set) list of morganNumbers
     * 
     * @return list of integers
     */
    public List<Long> getMorganList() {
        calculateMorganList();
        return morganList;
    }
    
    private void calculateMorganList() {
        if (morganList == null) {
            // iterate until number of equivalence classes is constant
            iterateTillConstantEquivalenceClassCount();
            //
            if (algorithm == Algorithm.SPLIT) {
                this.debug("split");
                repeatedlySplitEquivalences();
            }
        }
    }
    
    private void calculateMorganNumbers() {
        for (CMLAtom atom : constantAtomSet.getAtoms()) {
            // start with ligand count for each atom
            atom.setProperty(Annotation.NUMBER.toString(), new Long(Morgan
                    .getNumber(atom)));
        }
    }

    /** get largest atomSet.
     * only counts those with more than 1 atom.
     * @return largest set or null (if all sets are of size 1
     */
    private CMLAtomSet getLargestAtomSet() {
        CMLAtomSet atomSet = null;
        for (Long ii : equivalenceMap.keySet()) {
            CMLAtomSet atomSet0 = equivalenceMap.get(ii);
            if (atomSet0.size() <= 1) {
                //
            } else if (atomSet == null) {
                atomSet = atomSet0;
            } else if (atomSet0.size() > atomSet.size()) {
                atomSet = atomSet0;
            }
        }
        return atomSet;
    }
    
    private void addMarkedAtom(CMLAtom markedAtom) {
        if (markedAtomsList == null) {
            markedAtomsList = new ArrayList<CMLAtom>();
        }
        markedAtomsList.add(markedAtom);
        markedAtom.setProperty(Annotation.MARKED.toString(), new Long(markedAtomsList.size()));
    }
    
    private void iterateTillConstantEquivalenceClassCount() {
        calculateMorganNumbers();
        int newClassCount = -1;
        while (true) {
            clean();
            newClassCount = classifyMorganNumbers();
            if (newClassCount <= nClasses) {
                break;
            }
            nClasses = newClassCount;
            expandMorganNumbers();
        }
    }
    
    private void repeatedlySplitEquivalences() {
        int count = 0;
        while (true) {
            CMLAtomSet atomSet = getLargestAtomSet();
            if (atomSet == null) {
                break;
            }
            if (count++ > 10) {
                break;
            }
            
            CMLAtom markedAtom = atomSet.getAtom(0);
            addMarkedAtom(markedAtom);
            iterateTillConstantEquivalenceClassCount();
        }
    }

    /** returns ordered list (by morgan numbers) of atomSets.
     * each element is an atomSet with one or more atoms
     * 
     * @return the list of ordered atomSets
     */
    public List<CMLAtomSet> getAtomSetList() {
        calculateMorganList();
        List<CMLAtomSet> atomSetList = new ArrayList<CMLAtomSet>();
        for (int i = 0; i < morganList.size(); i++) {
            Long morganNumber = morganList.get(i);
            CMLAtomSet atomSet = equivalenceMap.get(morganNumber);
            atomSetList.add(atomSet);
        }
        return atomSetList;
    }

    /** puts morgan numbers into equivalence classes.
     * 
     * @return number of classes
     */
    private int classifyMorganNumbers() {
        for (CMLAtom atom : constantAtomSet.getAtoms()) {
            // start with ligand count for each atom
            Long morganNumber = (Long) atom.getProperty(Annotation.NUMBER.toString());
            CMLAtomSet atomSet = equivalenceMap.get(morganNumber);
            if (atomSet == null) {
                atomSet = new CMLAtomSet();
                add(morganNumber, atomSet);
            }
            atomSet.addAtom(atom);
        }
        Collections.sort(morganList);
        return equivalenceMap.size();
    }
    
    private void add(Long morganNumber, CMLAtomSet atomSet) {
        if (equivalenceMap == null) {
            equivalenceMap = new HashMap<Long, CMLAtomSet>();
        }
        equivalenceMap.put(morganNumber, atomSet);
        if (morganList == null) {
            morganList = new ArrayList<Long>();
        }
        morganList.add(morganNumber);
    }


    /** expand shell by adding numbers from ligands
     *
     */
    private void expandMorganNumbers() {
        for (CMLAtom atom : constantAtomSet.getAtoms()) {
            // iterate through all ligands
            long newMorgan = ((Long) atom.getProperty(Annotation.NUMBER.toString())).longValue();
            List<CMLAtom> ligandList = atom.getLigandAtoms();
            for (CMLAtom ligand : ligandList) {
                newMorgan += ((Long) (ligand).getProperty(Annotation.NUMBER.toString()))
                        .longValue();
            }
            atom.setProperty(Annotation.NEXTNUMBER.toString(), new Long(newMorgan));
        }
        // transfer new morgan values to old
        for (CMLAtom atom : constantAtomSet.getAtoms()) {
            atom.setProperty(Annotation.NUMBER.toString(), 
                    atom.getProperty(Annotation.NEXTNUMBER.toString()));
        }
    }

    /** prints debug.
     * @param message
     */
     public void debug(String message) {
         if (morganList == null) {
             System.out.println("NO atoms in list");
         } else {
             System.out.println("==========="+message+"==========");
             constantAtomSet.debug();
             for (int i = 0; i < morganList.size(); i++) {
                 if (i % 5 == 0) {
                     System.out.println();
                 }
                 Long morganNumber = (Long) morganList.get(i);
                 @SuppressWarnings("unused")
                 CMLAtomSet atomSet = (CMLAtomSet) equivalenceMap.get(morganNumber);
                 System.out.print(" I "+morganNumber);
             }
         }
         System.out.println();
     }

    /** calculate number for atom.
     * 
     * @param atom
     * @return int
     */
    private static long getNumber(CMLAtom atom) {
        ChemicalElement chemicalElement = ChemicalElement
                .getChemicalElement(atom.getElementType());
        int atomicNumber = (chemicalElement == null) ? 0 : chemicalElement
                .getAtomicNumber();
        List<CMLAtom> ligandList = atom.getLigandAtoms();
        int nLigand = ligandList.size();
        int np = 10;
        long morgan = nLigand;
        // atomic number
        morgan = atomicNumber + (Util.getPrime(np++) * morgan);
        // hydrogen count
        morgan = atom.getHydrogenCount() + (Util.getPrime(np++) * morgan);
        // formal charge
        morgan = 4 + atom.getFormalCharge() + (Util.getPrime(np++) * morgan);
        // property
        Long property = (Long) atom.getProperty(Annotation.MARKED.toString());
        int propertyValue = (property == null) ? 0 : property.hashCode();
        morgan = Util.getPrime(propertyValue) + (Util.getPrime(np++) * morgan);

        // uses labels as disambiguation
        CMLLabel label = (CMLLabel) atom.getFirstCMLChild(CMLLabel.TAG);
        String labelS = (label == null) ? S_EMPTY : label.getValue();
        @SuppressWarnings("unused")
        int labelHash = labelS.hashCode() % 1000;
        // and properties
        
        return morgan;
    }

}