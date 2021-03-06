/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * manage counting and management of double bonding. A piSystem is a set of
 * atoms (not necessarily am AtomSet) which each have one or more
 * "doubleBondEquivalents". The purpose of piSystem is to allow these to be
 * joined into double bonds, ans optionally try to assign formal charges. Still
 * experimental
 * 
 * The assignment has the phases. - identify the connected islands (piSystems)
 * of neighboring atoms with one or more DBEs. - recursively prune any terminal
 * bonds where the start atom has only one DBE ligand - split the result into
 * further piSystems if disconnection has occurred - recursively assign double
 * bonds as as far as possible. Return either the first complete match or the
 * overall match with fewest unpaired centres - try to make a chemically
 * sensible assignment of charges. The overall charge may or may not be known.
 * 
 * @author pmr
 * 
 */
public class PiSystem implements CMLConstants {
	private static Logger LOG = Logger.getLogger(PiSystem.class);

    MoleculeTool moleculeTool;

    // atoms from which map is created
    List<CMLAtom> allAtoms;

    Map<CMLAtom, Integer> piMap = null;

    Map<CMLAtom, Integer> parentPiMap = null;

    Stack<CMLAtom> atomStack = null;

    private List<CMLAtom> atomList = null;

    List<AtomPair> finalAtomPairList = null;

    List<AtomPair> tempAtomPairList = null;

    int remainingPiCount = 0;

    FormalChargeControl fcd;

    PiSystemControls piSystemOptions;

    // ================== constructors and accessors ===============
    /**
     * constructor.
     * 
     * @param moleculeTool
     * @param atoms
     *            to create system from
     */
    public PiSystem(MoleculeTool moleculeTool, List<CMLAtom> atoms) {
        this.moleculeTool = moleculeTool;
        allAtoms = atoms;
        piSystemOptions = new PiSystemControls();
    }

    /**
     * constructor.
     * 
     * @param atoms
     *            to create system from
     */
    public PiSystem(List<CMLAtom> atoms) {
        this(MoleculeTool.getOrCreateTool(atoms.get(0).getMolecule()), atoms);
    }

    /**
     * constructor.
     * 
     * @param piMap
     *            to use for atoms
     */
    public PiSystem(Map<CMLAtom, Integer> piMap) {
        allAtoms = new ArrayList<CMLAtom>();
        CMLAtom atomx = null;
        List<CMLAtom> atomList = getSortedAtomList(piMap);
        for (CMLAtom atom : atomList) {
            allAtoms.add(atom);
            atomx = atom;
        }
        if (atomx != null) {
            this.moleculeTool = MoleculeTool.getOrCreateTool(atomx.getMolecule());
        }
        piSystemOptions = new PiSystemControls();
    }

    /**
     * get the piSystemManager.
     * 
     * @return the piSystemManager.
     */
    public PiSystemControls getPiSystemManager() {
        return piSystemOptions;
    }

    /**
     * set the piSystemManager.
     * 
     * @param piSystemManager
     *            The piSystemManager to set.
     */
    public void setPiSystemManager(PiSystemControls piSystemManager) {
        this.piSystemOptions = piSystemManager;
    }

    /**
     * allows iteration through a piSystem in order.
     * 
     * @return sorted list of atoms
     */
    public List<CMLAtom> getSortedAtomList() {
        if (piMap == null) {
            throw new RuntimeException("null piMap");
        }
        return getSortedAtomList(piMap);
    }
    
    /**
     * @return unsorted list of atoms in the pi system
     */
    public List<CMLAtom> getAtomList() {
    	return atomList;
    }

    /**
     * allows iteration through a piSystem in order.
     * 
     * @param piMap
     * @return sorted list of atoms
     */
    private List<CMLAtom> getSortedAtomList(Map<CMLAtom, Integer> piMap) {
        List<CMLAtom> atomList = new ArrayList<CMLAtom>(piMap.keySet());
        Collections.sort(atomList, new Comparator<CMLAtom>() {
            public int compare(CMLAtom a1, CMLAtom a2) {
                if (!valid(a1) || !valid(a2)) {
                    throw new RuntimeException("null atoms or ids");
                }
                return (a1.getId().compareTo(a2.getId()));
            }

            private boolean valid(CMLAtom a) {
                return (a != null && a.getId() != null);
            }
        });
        return atomList;
    }

    /**
     * get size of pi subsystem. uses piMap.
     * 
     * @return size, 0 if null;
     */
    public int getSize() {
        return (piMap == null) ? 0 : piMap.size();
    }

    /**
     * get piSystems in overall piSystem. called by applications
     * 
     * @return list of piSystems (zero length if none)
     */
    public List<PiSystem> generatePiSystemList() {
        this.createPiMap();
        List<PiSystem> piSystemList = new ArrayList<PiSystem>();
        if (piMap.size() == 1) {
        	PiSystem piSystem = this.getPiSystem_createSubSystem(piMap.entrySet().iterator().next().getKey(), piMap);
    		piSystemList.add(piSystem);
    		piSystem.getPiSystem_deleteFromParentMap();
        } else {
        	while (piMap.size() > 0) {
        		// get next atom with free ligands
        		CMLAtom atom = this.getPiSystemList_nextAtom();
        		if (atom == null) {
        			break;
        		}
        		PiSystem piSystem = this.getPiSystem_createSubSystem(atom, piMap);
        		piSystemList.add(piSystem);
        		piSystem.getPiSystem_deleteFromParentMap();
        	}
        }
        return piSystemList;
    }

    /**
     * create map of atoms to their doubleBondEquivalents.
     * 
     * @return the map of atoms to Integer count
     */
    private Map<CMLAtom, Integer> createPiMap() {
        piMap = new HashMap<CMLAtom, Integer>();
        for (CMLAtom atom : allAtoms) {
        	AtomTool atomTool = AtomTool.getOrCreateTool(atom);
            int nPi = atomTool.getDoubleBondEquivalents(piSystemOptions.getFormalChargeControl());
            if (nPi > 0) {
                piMap.put(atom, new Integer(nPi));
            }
        }
        return piMap;
    }

    private CMLAtom getPiSystemList_nextAtom() {
        CMLAtom atomx = null;
        List<CMLAtom> atomList = getSortedAtomList();
        for (CMLAtom atom : atomList) {
                atomx = atom;
                break;
        }
        return atomx;
    }

    /**
     * delete atoms from parent map.
     * 
     */
    private void getPiSystem_deleteFromParentMap() {
        for (CMLAtom atom : atomList) {
            parentPiMap.remove(atom);
        }
    }

    /**
     * create piSystem containing this atom. recursively visits neightbours of
     * seed until no more found.
     * 
     * @param atom
     *            seed to start from
     * @param parentMap
     * @return the piSystem
     */
    private PiSystem getPiSystem_createSubSystem(CMLAtom atom,
            Map<CMLAtom, Integer> parentMap) {
        if (atom == null) {
            throw new RuntimeException("ATOM MUST NOT BE NULL");
        }
        if (parentMap == null) {
            throw new RuntimeException("Must calculate dbeMap");
        }
        PiSystem subSystem = new PiSystem(moleculeTool,
                new ArrayList<CMLAtom>());
        subSystem.setPiSystemManager(new PiSystemControls(piSystemOptions));
        subSystem.parentPiMap = parentMap;
        subSystem.getPiSystem_initializeContainers();
        subSystem.expand_addAtom(atom);
        subSystem.expand_system();
        return subSystem;
    }

    private void getPiSystem_initializeContainers() {
        piMap = new HashMap<CMLAtom, Integer>();
        atomStack = new Stack<CMLAtom>();
        atomList = new ArrayList<CMLAtom>();
    }

    // ================== set up the pi system ===================
    // create piSystem
    private void expand_system() {
        if (!atomStack.isEmpty()) {
            CMLAtom atom = atomStack.pop();
            if (atom != null) {
                List<CMLAtom> ligandList = atom.getLigandAtoms();
                for (CMLAtom ligand : ligandList) {
                    if (parentPiMap.get(ligand) != null) {
                        expand_addAtom(ligand);
                    }
                }
                expand_system();
            }
        }
    }

    /**
     * add atom if not already used. atom is added to atomStack, usedAtoms,
     * atomList and dbeMap.
     * 
     * @param atom
     */
    private void expand_addAtom(CMLAtom atom) {
        if (!atomList.contains(atom)) {
            atomList.add(atom);
            atomStack.push(atom);
            piMap.put(atom, parentPiMap.get(atom));
            allAtoms.add(atom);
        }
    }

    // ================== analyze the system ==================

    /**
     * get list of bonds.
     * 
     * @return list of atomPairs forming double bonds (empty if impossible)
     */
    public List<CMLBond> identifyDoubleBonds() {
        atomStack = new Stack<CMLAtom>();
        finalAtomPairList = markTerminalBonds(false);
        List<PiSystem> subPiSystemList = null;
        // this may have split the system, so make list of sub systems
        if (finalAtomPairList.size() != 0) {
            PiSystem newPiSystem = new PiSystem(piMap);
            newPiSystem
                    .setPiSystemManager(new PiSystemControls(piSystemOptions));
            subPiSystemList = newPiSystem.generatePiSystemList();
        } else {
            subPiSystemList = new ArrayList<PiSystem>();
            subPiSystemList.add(this);
        }
        for (PiSystem subPiSystem : subPiSystemList) {
            subPiSystem.exploreDoubleBonds();
            for (AtomPair atomPair : subPiSystem.tempAtomPairList) {
                finalAtomPairList.add(atomPair);
            }
            annotateUnmarkedPi();
        }
        if (piSystemOptions.isUpdateBonds()) {
            this.incrementBondOrders();
        }
        if (piSystemOptions.isDistributeCharge()) {
            distributeCharge();
        }

        List<CMLBond> bondList = this.createBondList();
        return bondList;
    }

    private void distributeCharge() {
        CMLMolecule molecule = moleculeTool.getMolecule();
        if (molecule.getFormalChargeAttribute() != null) {
            List<CMLAtom> atoms = molecule.getAtoms();
            int knownUnpaired = piSystemOptions.getKnownUnpaired();
            int formalCharge = molecule.getFormalCharge();
            int tempCharge = formalCharge;
            // probably know the charge distribution
            if (knownUnpaired == Math.abs(tempCharge)) {
                for (CMLAtom atom : atoms) {
                    tempCharge = adjustChargeOnCON(atom, tempCharge);
                }
            } else {
                for (CMLAtom atom : atoms) {
                    tempCharge = adjustChargeOnCON(atom, tempCharge);
                }
                if (tempCharge != 0) {
                    // LOG.debug("Cannot distribute charge");
                }
            }
        }
    }

    private int adjustChargeOnCON(CMLAtom atom, int formalCharge) {
        CMLElectron electron = (CMLElectron) atom
                .getFirstCMLChild(CMLElectron.TAG);
        if (electron != null) {
            if (CMLElectron.PI.equals(electron.getDictRef())) {
                if (formalCharge < 0 && atom.getElementType().equals("O")) {
                    formalCharge = adjustChargeAndElectron(atom, electron,
                            formalCharge, -1);
                } else if (formalCharge > 0
                        && atom.getElementType().equals("N")) {
                    formalCharge = adjustChargeAndElectron(atom, electron,
                            formalCharge, 1);
                } else if (formalCharge != 0
                        && atom.getElementType().equals(AS.C.value)) {
                    int delta = (formalCharge < 0) ? -1 : 1;
                    formalCharge = adjustChargeAndElectron(atom, electron,
                            formalCharge, delta);
                }
            }
        }
        return formalCharge;
    }

    private int adjustChargeAndElectron(CMLAtom atom, CMLElectron electron,
            int formalCharge, int delta) {
        atom.setFormalCharge(delta);
        formalCharge -= delta;
        electron.detach();
        return formalCharge;
    }

    /**
     * recursively process terminal pi bonds. finds atom with only one pi-rich
     * neighbour. adds that as a bond and recurses until no further change. also
     * removes atoms from piMap if no further Pi
     * 
     * @param use
     *            this routine if true
     * @return list of bonds
     */
    private List<AtomPair> markTerminalBonds(boolean use) {
        remainingPiCount = 0;
        List<CMLAtom> atomList = getSortedAtomList();
        for (CMLAtom atom : atomList) {
            remainingPiCount += piMap.get(atom).intValue();
        }
        // debugMap();
        List<AtomPair> fullBondList = new ArrayList<AtomPair>();
        boolean change = use;
        while (change) {
            change = false;
            List<CMLAtom> atomList1 = getSortedAtomList();
            for (CMLAtom atom : atomList1) {
                if (lookupPiCount(atom) > 0) {
                    List<CMLAtom> piNeighbours = getPiNeighbours(atom);
                    if (piNeighbours.size() == 1) {
                        CMLAtom piNeighbour = piNeighbours.get(0);
                        markAtomPair(atom, piNeighbour, 0);
                        removeFromMapIfZero(atom);
                        removeFromMapIfZero(piNeighbour);
                        change = true;
                        break;
                    }
                }
            }
        }
        // LOG.debug("Terminal "+fullBondList.size());
        return fullBondList;
    }

    private void removeFromMapIfZero(CMLAtom atom) {
        if (lookupPiCount(atom) == 0) {
            piMap.remove(atom);
        }
    }

    private void exploreDoubleBonds() {
    	// copy the piMap and remainingPiCount so that we can use these values 
    	// to reset the variables later on.
    	Map<CMLAtom, Integer> piMapCopy = new HashMap<CMLAtom, Integer>(piMap);
    	int startRemainingPiCount = remainingPiCount;
    	
        int knownUnpaired = piSystemOptions.getKnownUnpaired();
        int oldPiCount = -1;
        int count = 0;
        int iterationMax = 5;
        while (count < atomList.size() && remainingPiCount > 0 && count < iterationMax) {
        	while (remainingPiCount > knownUnpaired
        			&& remainingPiCount != oldPiCount) {
        		oldPiCount = remainingPiCount;
        		//List<CMLAtom> atomList = getSortedAtomList();
        		for (CMLAtom startAtom : atomList) {
        			// reset stack
        			atomStack = new Stack<CMLAtom>();
        			tempAtomPairList = new ArrayList<AtomPair>();
        			exploreStart1(startAtom, 0);
        			if (remainingPiCount <= knownUnpaired) {
        				break;
        			}
        		}
        	}
        	if (remainingPiCount > 0) {
        		// reset piMap and remainingPiCount
        		piMap = new HashMap<CMLAtom, Integer>(piMapCopy);
        		remainingPiCount = startRemainingPiCount;
        		// put the first item in the list to the end, to
        		// change startAtom for next iteration
        		CMLAtom atom = atomList.get(0);
        		atomList.remove(atom);
        		atomList.add(atom);
        	}
        	count++;
        	iterationMax++;
        }
    }

    /**
     * explore from this atom. use counts on this and other atoms to decide
     * which to include
     * 
     * @param atom
     * @return true if matched (may not be used later)
     */
    private int exploreStart1(CMLAtom atom, int level) {
        int knownUnpaired = piSystemOptions.getKnownUnpaired();
        // LOG.debug(spaces(2*level)+">> "+atom.getId());
        // store any bonds formed here
        Stack<AtomPair> atomPairStack = new Stack<AtomPair>();
        List<CMLAtom> ligands = atom.getLigandAtoms();
        int count = calculatePiCount(ligands);
        // if any electrons left iterate through ligands
        int oldPiCount = -1;
        while (oldPiCount != remainingPiCount && remainingPiCount > 1) {
            oldPiCount = remainingPiCount;
            for (CMLAtom ligand : ligands) {
                // find next ligand with spare pi
                if (lookupPiCount(ligand) > 0) {
                    // LOG.debug(spaces(2*level)+".. "+ligand.getId());
                    AtomPair atomPair = null;
                    // if atom still has pi, add new bond
                    if (lookupPiCount(atom) > 0) {
                        atomPair = markAtomPair(atom, ligand, level);
                        atomPairStack.push(atomPair);
                    }
                    // anything more to do?
                    int count1 = exploreStart1(ligand, level + 1);
                    if (remainingPiCount <= knownUnpaired) {
                        // LOG.debug("FINISHED EXPLORE
                        // "+remainingPiCount);
                        break;
                    }
                    if (count1 > 0) {
                        count += 1;
                        unmarkAtomPair(atomPair, "UP", level);
                        atomPairStack.remove(atomPair);
                        atomPair = null;
                    } else {
                        if (count == 0) {
                            break;
                        }
                    }
                }
            }
        }
        if (level == 0 && remainingPiCount > knownUnpaired) {
            // LOG.debug("FAILED, remaining: "+remainingPiCount);
        }
        // LOG.debug(spaces(2*level)+"<< "+atom.getId());
        return count;
    }

    private int calculatePiCount(List<CMLAtom> ligands) {
        int count = 0;
        for (CMLAtom ligand : ligands) {
            count += lookupPiCount(ligand);
        }
        return count;
    }

    private void annotateUnmarkedPi() {
        // LOG.debug("Anotate unmarked pi");
        List<CMLAtom> atomList = getSortedAtomList();
        for (CMLAtom atom : atomList) {
            int pi = piMap.get(atom).intValue();
            while (pi-- > 0) {
                CMLElectron electron = new CMLElectron();
                atom.appendChild(electron);
                electron.setDictRef(CMLElectron.PI);
            }
        }
    }

    private AtomPair markAtomPair(CMLAtom atom, CMLAtom ligand, int level) {
        // LOG.debug(spaces(2*level+2)+"++
        // "+atom.getId()+S_MINUS+ligand.getId());
        AtomPair bond = new AtomPair(atom, ligand);
        addToPi(atom, -1);
        addToPi(ligand, -1);
        tempAtomPairList.add(bond);
        return bond;
    }

    private void unmarkAtomPair(AtomPair atomPair, String s, int level) {
        if (atomPair != null) {
            // LOG.debug(spaces(2*level+2)+"--
            // "+atomPair.getAtom1().getId()+S_MINUS+atomPair.getAtom2().getId());
            CMLAtom atom = atomPair.getAtom1();
            CMLAtom ligand = atomPair.getAtom2();
            addToPi(atom, 1);
            addToPi(ligand, 1);
            tempAtomPairList.remove(atomPair);
        }
    }

    private void addToPi(CMLAtom atom, int delta) {
        Integer ii = piMap.get(atom);
        if (ii != null) {
            int iii = ii.intValue() + delta;
            if (iii < 0) {
                throw new RuntimeException("Negative Pi count");
            }
            piMap.put(atom, new Integer(iii));
        } else {
            throw new RuntimeException("Null Pi count");
        }
        remainingPiCount += delta;
    }

    /**
     * apply tempaorary bond orders permanently.
     * 
     */
    private void incrementBondOrders() {
        // LOG.debug("ATOM PAIR LIST "+finalAtomPairList.size());
        List<CMLBond> bondList = this.createBondList();
        // LOG.debug("BOND LIST "+bondList.size());
        if (bondList == null) {
            throw new RuntimeException("NULL BOND LIST");
        } else {
            for (CMLBond bond : bondList) {
                bond.incrementOrder(1);
            }
        }
    }

    /**
     * creates a bondList from the atomPairList.
     * 
     * @return list of bonds
     */
    private List<CMLBond> createBondList() {
        List<CMLBond> bondList = new ArrayList<CMLBond>();
        for (AtomPair atomPair : tempAtomPairList) {
            bondList.add(this.getBond(atomPair));
        }
        return bondList;
    }

    private CMLBond getBond(AtomPair atomPair) {
        CMLAtom atom1 = atomPair.getAtom1();
        CMLAtom atom2 = atomPair.getAtom2();
        if (atom1 == null || atom2 == null) {
            throw new RuntimeException("Null atom in atomPair " + atomPair);
        }
        CMLMolecule molecule = atom1.getMolecule();
        CMLBond bond = molecule.getBond(atom1, atom2);
        if (bond == null) {
            throw new RuntimeException("Cannot find bond " + atomPair);
        }
        return bond;
    }

    /**
     * string representation.
     * 
     * @return the string
     */
    public String toString() {
        String s = "Atoms: ";
        for (CMLAtom atom : atomStack) {
            s += CMLConstants.S_SLASH + atom.getId();
        }
        // for (AtomPair atomPair : fullBondList) {
        // s += CMLConstants.S_SLASH+atomPair;
        // }
        return s;
    }

    private void debugMap() {
        int count = 0;
        LOG.debug("===debug>>> " + piMap.size());
        List<CMLAtom> atomList = getSortedAtomList();
        for (CMLAtom atom : atomList) {
            int cc = piMap.get(atom).intValue();
            LOG.debug(".." + atom.getId() + ".." + cc);
            count += cc;
        }
        LOG.debug("===debug<<< " + count);
    }

    // =================== utilities ====================

    /**
     * gets pi count from map.
     * 
     * @param atom
     * @return the pi count
     */
    private int lookupPiCount(CMLAtom atom) {
        return (piMap.get(atom) == null) ? 0 : piMap.get(atom).intValue();
    }

    private List<CMLAtom> getPiNeighbours(CMLAtom atom) {
        List<CMLAtom> piNeighbours = new ArrayList<CMLAtom>();
        List<CMLAtom> ligandList = atom.getLigandAtoms();
        for (CMLAtom ligand : ligandList) {
            if (lookupPiCount(ligand) > 0) {
                piNeighbours.add(ligand);
            }
        }
        return piNeighbours;
    }

    private String spaces(int level) {
        return "                                 ".substring(0, level);
    }

    void meaninglessCodeToGetRidOfWarnings() {
        debugMap();
        spaces(3);
    }

}