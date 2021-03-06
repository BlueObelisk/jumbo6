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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.AS;
/**
 * tool to support connection table. not fully developed
 * 
 * @author pmr
 * 
 */
public class ConnectionTableTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ConnectionTableTool.class);
	private CMLMolecule molecule;

	// Used by ring detection methods
	private List<Set<CMLAtom>> ringNucleusAtoms = null;

	/**
	 * constructor with embedded molecule.
	 * 
	 * @param molecule
	 */
	public ConnectionTableTool(CMLMolecule molecule) {
		if (molecule == null) {
			throw new RuntimeException("null molecule");
		}
		this.molecule = molecule;
	}

	
	/** splits given connection table into separate fragment molecules.
	 * requires bonds to be assigned.
	 * @throws RuntimeException
	 */

	public void partitionIntoMolecules() throws RuntimeException {
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule : molecules) {
				new ConnectionTableTool(molecule).partitionIntoMolecules();
			}
		} else {
			if (molecule.getAtomCount() == 0) {
				LOG.trace("no atoms to split");
				return;
			}
			List<Set<CMLAtom>> atomSetList = new ArrayList<Set<CMLAtom>>();
			List<Set<CMLBond>> bondSetList = new ArrayList<Set<CMLBond>>();
			// get all atoms
			List<CMLAtom> oldAtomList = molecule.getAtoms();
			// set of old atoms
			Set<CMLAtom> oldAtomSet = new LinkedHashSet<CMLAtom>();
			for (CMLAtom atom : oldAtomList) {
				oldAtomSet.add(atom);
			}
			while (oldAtomSet.size() > 0) {
				// grow new molecule
				Stack<CMLAtom> atomStack = new Stack<CMLAtom>();
				CMLAtom rootAtom = oldAtomSet.iterator().next();
				Set<CMLAtom> newAtomSet = new HashSet<CMLAtom>();
				Set<CMLBond> newBondSet = new HashSet<CMLBond>();
				atomStack.push(rootAtom);
				while (!atomStack.isEmpty()) {
					CMLAtom atom = atomStack.pop();
					newAtomSet.add(atom);
					oldAtomSet.remove(atom);
					// iterate through ligands
					List<CMLAtom> ligandList = atom.getLigandAtoms();
					for (CMLAtom ligand : ligandList) {
						// is this a new atom?
						if (!newAtomSet.contains(ligand)) {
							atomStack.add(ligand);
							// add bonds
							List<CMLBond> ligandBondList = atom.getLigandBonds();
							for (CMLBond ligandBond : ligandBondList) {
								newBondSet.add(ligandBond);
							}
						}
					}
				} // Exit when sub-atom complete

				atomSetList.add(newAtomSet);
				bondSetList.add(newBondSet);
			}

			int size = atomSetList.size();
			if (size == 0) {
				throw new RuntimeException("No molecules found");
			} else if (size == 1) {
				// LOG.debug("no splitting required");
			} else {
				// More than one molecule found - delete current molecule, and
				// and new molecules
				// as its children
				List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
				for (int i = 0; i < atomSetList.size(); i++) {
					CMLMolecule molecule1 = createMolecule(atomSetList.get(i),
							bondSetList.get(i));
					moleculeList.add(molecule1);
				}

				Element atomArray, bondArray;
				// Destroy old molecule
				if ((atomArray = molecule.getFirstCMLChild(CMLAtomArray.TAG)) != null) {
					atomArray.detach();
				}
				if ((bondArray = molecule.getFirstCMLChild(CMLBondArray.TAG)) != null) {
					bondArray.detach();
				}

				// Add new molecules
				String oldId = molecule.getId();
				if (oldId == null) {
					oldId = "mol0";
				}
				if (oldId.length() > 0) {
					oldId += CMLConstants.S_UNDER;
				}

				for (int i = 0; i < size; i++) {
					CMLMolecule childMol = moleculeList.get(i);
					childMol.setId(oldId + "sub" + (i + 1));
					molecule.appendChild(childMol);
				}
			}
		}
	}

	/**
	 * adds atoms and bonds from sets. detaches atoms and bonds
	 * 
	 * @param atomSet
	 *            atoms to add; parentage destroyed
	 * @param bondSet
	 *            bonds to add; parentage destroyed
	 * @return new molecule
	 */
	private static CMLMolecule createMolecule(Set<CMLAtom> atomSet,
			Set<CMLBond> bondSet) {
		CMLMolecule molecule = new CMLMolecule();
		for (CMLAtom atom : atomSet) {
			atom.detach();
			molecule.addAtom(atom);
		}

		for (CMLBond bond : bondSet) {
			bond.detach();
			molecule.addBond(bond);
		}
		return molecule;
	}

	/** get unique id for atom.
	 * 
	 * @param atom
	 * @return new id
	 */
	public String getUniqueId(CMLAtom atom) {
		int i = 0;
		String newId = atom.getId();
		while (true) {
			CMLAtom atom1 = molecule.getAtomById(newId);
			if (atom1 == null) {
				break;
			}
			newId = atom.getId() + CMLConstants.S_PERIOD + (++i);
		}
		return newId;
	}


	/** merge child subMolecules into this.
	 * transfers atoms and bonds and all other elements to parent molecule
	 * then deletes existing subMolecules
	 * but keeps all other child elements of this
	 */
	public void flattenMolecules() {
		CMLElements<CMLMolecule> subMolecules = molecule.getMoleculeElements();
		List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
		for (CMLMolecule subMolecule : subMolecules) {
			moleculeList.add(subMolecule);
		}
//		CMLMolecule temp = CMLMolecule.createMoleculeWithId("temp");
		flattenMolecules(moleculeList, molecule);
	}

	public static void flattenMolecules(List<CMLMolecule> subMolecules, CMLMolecule temp) {
		for (CMLMolecule subMolecule : subMolecules) {
			transferAtomsBonds(subMolecule, temp);
			subMolecule.removeAtomArray();
			subMolecule.removeBondArray();
			// transfer non-atom/bond stuff
			Elements elements = subMolecule.getChildElements();
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
				element.detach();
				temp.appendChild(element);
			}
			subMolecule.detach();
		}
	}

	private static void transferAtomsBonds(CMLMolecule subMolecule, CMLMolecule temp) {
		// have to do the bonds first, as detaching atoms deletes bonds
		List<CMLBond> bonds = subMolecule.getBonds();
		List<CMLAtom> atoms = subMolecule.getAtoms();
		for (CMLBond bond : bonds) {
			bond.detach();
		}
		for (CMLAtom atom : atoms) {
			atom.detach();
		}
		for (CMLAtom atom : atoms) {
			temp.addAtom(atom);
		}
		for (CMLBond bond : bonds) {
			temp.addBond(bond);
		}
	}

	/** merge molecules.
	 * 
	 * @param fromMolecule
	 * @throws RuntimeException
	 */
	public void mergeMolecule(CMLMolecule fromMolecule) {
		if (fromMolecule != null) {
			for (CMLBond addBond : fromMolecule.getBonds()) {
				fromMolecule.deleteBond(addBond);
				if (molecule.getBond(addBond.getAtom(0), addBond.getAtom(1)) != null) {
					throw new RuntimeException("Duplicate bond in addMolecule: "
							+ addBond.getAtomRefs2());
				}
				this.transferToMolecule(addBond, molecule);
			}
			for (CMLAtom fromAtom : fromMolecule.getAtoms()) {
				this.transferToMolecule(fromAtom, molecule);
			}
		}
	}

	/** collect equivalent molecules.
	 * checks to see which molecules have equivalent connectivity
	 * @param moleculeList
	 * @return hashmap indexed by unique strings
	 * 
	 */
	public static Map<String, List<CMLMolecule>> createEquivalentSets(List<CMLMolecule> moleculeList) {
		Map<String, List<CMLMolecule>> moleculeMap = new HashMap<String, List<CMLMolecule>>();
		for (CMLMolecule molecule : moleculeList) {
			Morgan morgan = new Morgan(molecule);
			String uniqueString = morgan.getEquivalenceString();
			List<CMLMolecule> list = moleculeMap.get(uniqueString);
			if (list == null) {
				list = new ArrayList<CMLMolecule>();
				moleculeMap.put(uniqueString, list);
			}
			list.add(molecule);
		}
		return moleculeMap;
	}

	/** create new sub molecule.
	 * 
	 * @param atomIds
	 * @return new molecule
	 */
	public CMLMolecule createSubMolecule(String[] atomIds) {
		CMLMolecule newMolecule = new CMLMolecule();
		for (String atomId : atomIds) {
			CMLAtom oldAtom = molecule.getAtomById(atomId);
			if (oldAtom == null) {
				throw new RuntimeException("Atom: " + atomId + " not in molecule: "
						+ molecule.getId());
			}
			CMLAtom newAtom = new CMLAtom(oldAtom);
			newMolecule.addAtom(newAtom);
		}
		for (CMLBond oldBond : molecule.getBonds()) {
			String atomRef0 = oldBond.getAtom(0).getId();
			if (newMolecule.getAtomById(atomRef0) == null) {
				continue;
			}
			String atomRef1 = oldBond.getAtom(1).getId();
			if (newMolecule.getAtomById(atomRef1) == null) {
				continue;
			}
			CMLBond newBond = new CMLBond(oldBond);
			// this bond
			newMolecule.addBond(newBond);
		}
		return newMolecule;
	}

	/** create new unique id.
	 * 
	 * @return the id
	 */
	public String createUniqueAtomId() {
		String id = "a1";
		if (molecule.getAtomCount() >= 0) {
			id = molecule.getAtom(0).getId();
			if (id != null) {
				id = id.substring(0, 1);
				int i = 1;
				while (true) {
					String id0 = id + CMLConstants.S_EMPTY + (i);
					if (molecule.getAtomById(id0) == null) {
						id = id0;
						break;
					} else if (++i > 10000000) {
						break;
					}
				}
			}
		}
		return id;
	}

	/**
	 * Generates and adds unique bond ids.
	 * 
	 * Uses CMLBond.generateId.
	 */
	public void generateBondIds() {
		for (CMLBond bond : molecule.getBonds()) {
			bond.generateAndSetId();
		}
	}

	/**
	 * Transfer atom to different molecule in same document.
	 * 
	 * use with care. Normally only when merging two molecules As well as
	 * setting up the atom's owner molecule, it adds this atom to mol
	 * 
	 * @param atom
	 * @param mol
	 *            to add
	 * @throws RuntimeException
	 *             duplicate atom Ids
	 */
	private void transferToMolecule(CMLAtom atom, CMLMolecule mol) {
		String id = atom.getId();
		CMLMolecule thisMolecule = atom.getMolecule();
		if (id == null) {
			throw new RuntimeException("Missing atom id");
		} else if (mol == null) {
			throw new RuntimeException("Cannot add atom to null molecule");
		} else if (mol == thisMolecule) {
			// are molecules distinct?
			throw new RuntimeException("Cannot add atom to same molecule");
		} else if (mol.getAtomById(id) != null) {
			// does atom Id clash with target molecule atomIds?
			throw new RuntimeException("Duplicate atom ids: " + id);
		} else {
			if (thisMolecule != null) {
				thisMolecule.deleteAtom(atom);
			}
			mol.addAtom(atom);
		}
	}

	/**
	 * Set owner molecule.
	 * 
	 * use with care. Normally only when merging two molecules
	 * 
	 * @param bond
	 * @param mol
	 *            to add (if null exits without action)
	 * 
	 */
	private void transferToMolecule(CMLBond bond, CMLMolecule mol) {
		CMLMolecule molecule = bond.getMolecule();
		if (molecule != mol) {
			if (molecule != null) {
				molecule.deleteBond(bond);
			}

			if (mol != null) {
				molecule = mol;
				molecule.addBond(bond);
			} else {
				molecule = null;
			}
		}
	}

	/**
	 * add suffix to atom IDs.
	 * 
	 * Add a distinguishing suffix to all atom IDs this allows multiple copies
	 * of a fragment in a molecule
	 * 
	 * @param suffix
	 */
	public void addSuffixToAtomIDs(String suffix) {
		for (CMLAtom atom : molecule.getAtoms()) {
			atom.renameId(atom.getId() + suffix);
		}
	}

	/**
	 * expands a fragment to include immediate ligands.
	 * 
	 * normally takes a ring nucleus and decorates it with the ligands but could
	 * expand to the next ligand shell of any fragment. the fragment is described
	 * by its atom ids whose ligands are determined and added to the growing
	 * atom set . this is then returned as a new molecule.
	 * 
	 * It should really be renamed to "get next coordination sphere"
	 * 
	 * @param atomIds
	 *            AtomIds of nuclei
	 *            @throws RuntimeException
	 * @return atomSet containing nuclei and ligands
	 */
	public CMLAtomSet getNextCoordinationSphere(String[] atomIds) {
		CMLAtomSet atomSet = new CMLAtomSet(molecule, atomIds);
		// MoleculeTool moleculeTool = new MoleculeToolImpl(this, atomIds);
		List<CMLAtom> atoms = atomSet.getAtoms();
		for (CMLAtom atom : atoms) {
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			for (CMLAtom ligand : ligandList) {
				if (atomSet.contains(ligand)) {
					continue;
				}
				atomSet.addAtom(ligand);
			}
		}
		return atomSet;
	}

	/**
	 * expands a fragment to include immediate ligands.
	 * 
	 * normally takes a ring nucleus and decorates it with the ligands but could
	 * expand to the next ligand shell of any fragment. the fragment is described
	 * by an AtomSet, and the ligand atoms are added to this AtomSet.
	 * 
	 * @param atomSet AtomSet containing nuclei
	 * @return atomSet containing nuclei and ligands
	 */
	public CMLAtomSet getNextCoordinationSphere(CMLAtomSet atomSet) {
		Set<CMLAtom> atoms = new HashSet<CMLAtom>();
		atoms.addAll(atomSet.getAtoms());
		for (CMLAtom atom : atoms) {
			for (CMLAtom ligand : atom.getLigandAtoms()) {
				if (!atomSet.contains(ligand)) {
					atomSet.addAtom(ligand);
				}
			}
		}

		return(atomSet);
	}




	/**
	 * Gets ring nuclei in molecule.
	 * 
	 * @return List of CMLAtomSets - one atomset for each discrete ring system
	 */
	List<CMLAtomSet> getRingNucleiAtomSets() {
		findRingNuclei();
		List<CMLAtomSet> atomSetList = new ArrayList<CMLAtomSet>(ringNucleusAtoms.size());
		for (Set<CMLAtom> set : ringNucleusAtoms) {
			CMLAtomSet atomSet = new CMLAtomSet(set);
			atomSetList.add(atomSet);
		}
		return atomSetList;
	}

	/** Gets cyclic bonds in molecule.
	 * 
	 * @return List of CMLBondSets - one bondset for each ring system
	 */
	List<CMLBondSet> getRingNucleiBondSets() {
		findRingNuclei();
		List<CMLBondSet> bondSetList = new ArrayList<CMLBondSet>();
		for (Set<CMLAtom> set : ringNucleusAtoms) {
			CMLBondSet bondSet = new CMLBondSet();
			for (CMLBond bond : molecule.getBonds()) {
				String[] atomRefs2 = bond.getAtomRefs2();
				if (atomRefs2 == null) {
					throw new RuntimeException("Bond has no atomRefs2: " + bond);
				}
				CMLAtom at0 = molecule.getAtomById(atomRefs2[0]);
				CMLAtom at1 = molecule.getAtomById(atomRefs2[1]);
				if (set.contains(at0) && set.contains(at1)) {
					bondSet.addBond(bond);
				}
			}
			bondSetList.add(bondSet);
		}
		return(bondSetList);
	}


	/** creates a new molecule for each ring nucleus.
	 * 
	 * @return List of CMLMolecules - one molecule for each ring system
	 */
	public List<CMLMolecule> getRingNucleiMolecules() {
		List<CMLBondSet> bondSets = getRingNucleiBondSets();

		List<CMLMolecule> molecules = new ArrayList<CMLMolecule>();
		for (CMLBondSet bondSet : bondSets) {
			CMLAtomSet atomSet = bondSet.getAtomSet();
			molecules.add(MoleculeTool.createMolecule(atomSet, bondSet));
		}
		return molecules;
	}

	/** returns list of all atoms in cyclic bonds
	 * 
	 * @return cyclic atoms (or zero list)
	 */
	public List<CMLAtom> getCyclicAtoms() {
		List<CMLBond> bonds = getCyclicBonds();
		List<CMLAtom> atoms = new ArrayList<CMLAtom>();
		for (CMLBond bond : bonds) {
			for (CMLAtom atom : bond.getAtoms()) {
				atoms.add(atom);
			}
		}
		return atoms;
	}

	/** calculate ringNucleusAtoms and cyclic bonds. Returns list of all cyclic bonds
	 * in molecule, and sets CYCLIC or ACYCLIC flag on each bond, as appropriate
	 * 
	 * @return cyclic bonds (or zero array)
	 */
	public List<CMLBond> getCyclicBonds() {
		findRingNuclei();
		List<CMLBond> cyclicBondList = new ArrayList<CMLBond>();

		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule : molecules) {
				ConnectionTableTool connectionTable = new ConnectionTableTool(
						molecule);
				for (CMLBond bond : connectionTable.getCyclicBonds()) {
					cyclicBondList.add(bond);
				}
			}
			return cyclicBondList;
		} else {
			List<CMLBondSet> bondSets = getRingNucleiBondSets();
			for (CMLBondSet bondSet : bondSets) {
				for (CMLBond bond : bondSet.getBonds()) {
					cyclicBondList.add(bond);
					bond.setCyclic(CMLBond.CYCLIC);
				}
			}
			for (CMLBond bond : molecule.getBonds()) {
				String cyclic = bond.getCyclic();
				if (cyclic == null || cyclic.equals(CMLBond.UNKNOWN_ORDER)) {
					bond.setCyclic(CMLBond.ACYCLIC);
				}
			}
			return cyclicBondList;
		}
	}


	/**
	 * get acyclic bonds.
	 * 
	 * derived by calculating cyclic bonds
	 * @return acyclic bonds (or zero array)
	 */
	public List<CMLBond> getAcyclicBonds() {
		List<CMLBond> acyclicBonds = new ArrayList<CMLBond>();
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule1 : molecule.getMoleculeElements()) {
				ConnectionTableTool connectionTable = new ConnectionTableTool(molecule1);
				for (CMLBond bond: connectionTable.getAcyclicBonds()) {
					acyclicBonds.add(bond);
				}
			}
		} else {
			getCyclicBonds();
			for (CMLBond bond : molecule.getBonds()) {
				if (CMLBond.ACYCLIC.equals(bond.getCyclic())) {
					acyclicBonds.add(bond);
				}
			}
		}
		return acyclicBonds;
	}


	/**
	 * identify acyclic double bonds for stereochemistry.
	 * 
	 * @return the bonds (zero length if none)
	 */
	// FIXME
	public List<CMLBond> getAcyclicDoubleBonds() {
		molecule.setNormalizedBondOrders();
		getCyclicBonds();
		List<CMLBond> adbVector = new ArrayList<CMLBond>();
		for (CMLBond bond : molecule.getBonds()) {
			if (CMLBond.ACYCLIC.equals(bond.getCyclic())) {
				if (CMLBond.isDouble(bond.getOrder())) {
					adbVector.add(bond);
				}
			}
		}
		return adbVector;
	}
	/**
	 * joins atoms by removing a hydrogen from each.
	 * 
	 * bond has order="1"
	 * 
	 * @param a1
	 *            atom 1
	 * @param a2
	 *            atom 2
	 * 
	 * @exception atoms
	 *                belong to wrong molecule or do not have any Hydrogens
	 */
	// FIXME
	/*--
     public void joinAtomsAndRemoveHydrogens(CMLAtom a1, CMLAtom a2) {
     if (molecule != a1.getMolecule()) {
     throw new RuntimeException("atom "+a1+" is not owned by molecule: "+this);
     }
     if (molecule != a2.getMolecule()) {
     throw new RuntimeException("atom "+a2+" is not owned by molecule: "+this);
     }
     a1.deleteHydrogen();
     a2.deleteHydrogen();
     CMLBond bond = molecule.createAndAddBond(a1, a2);
     bond.setOrder("1");
     }
     --*/
	/**
	 * clear cyclic knowledge.
	 * 
	 * sets all bond cyclicity to UNKNOWN
	 */
	public void clearCyclicBonds() {
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule : molecules) {
				new ConnectionTableTool(molecule).clearCyclicBonds();
			}
		} else {
			for (CMLBond bond : molecule.getBonds()) {
				bond.setCyclic(CMLBond.UNKNOWN_ORDER);
			}
		}
	}
	/**
	 * Adds all of the elements on hMol to this molecule
	 * 
	 * @param hMol
	 *            The CMLMolecule whose contents are to be added to this one
	 */
	/**
	 * -- omitted until addAtom is fixed public void addExplicitHydrogens
	 * (CMLMolecule hMol) { this.getMolecules(); if (molecules.size() > 0) { for
	 * (int i = 0; i < molecules.size(); i++) {
	 * moleculesManager.getMolecule(i).addExplicitHydrogens(hMol); } } else {
	 * getMolecules (); if (molecules.size() > 0) {
	 * 
	 * CMLMolecule [] hSubMols = hMol.getMolecules ();
	 * 
	 * for (int i = 0; i < hSubMols.length; i++) { String hMolId = hSubMols
	 * [i].getId ();
	 * 
	 * for (int j = 0; j < molecules.size(); j ++) { if (hMolId.equals
	 * (molecules [j].getId ())) {
	 * 
	 * CMLAtomArray hAtoms = hSubMols [i].getAtomArray (); CMLAtomArray atoms =
	 * molecules [j].getAtomArray (); CMLBondArray hBonds = hSubMols
	 * [i].getBondArray (); CMLBondArray bonds = molecules [j].getBondArray ();
	 * 
	 * int numH;
	 * 
	 * if ((hAtoms != null) && ((numH = hAtoms.getAtomCount ()) > 0)) { if
	 * (atoms == null) { atoms = new CMLAtomArray (); }
	 *  // remove all hydrogenCounts for (int k = atoms.getAtomCount () - 1; k >=
	 * 0; -- k) { atoms.getAtom (k).setHydrogenCount (0); }
	 *  // add from hMol for (int k = numH - 1; k >= 0; -- k) { try {
	 * atoms.addAtom (hAtoms.getAtom (k)); //hAtoms.removeAtom (hAtoms.getAtom
	 * (k)); } catch (RuntimeException cmle) { System.err.println("BUG "+cmle); } } }
	 * 
	 * if ((hBonds != null) && ((numH = hBonds.getBondCount ()) > 0)) { if
	 * (bonds == null) { bonds = new CMLBondArray (); }
	 *  // add from hMol for (int k = numH - 1; k >= 0; -- k) { try {
	 * bonds.addBond (hBonds.getBond (k)); //hBonds.removeBond (hBonds.getBond
	 * (k)); } catch (RuntimeException cmle) { System.err.println("BUG "+cmle); } } }
	 * 
	 * 
	 * j = molecules.size(); // force exit from loop } } } } else {
	 * 
	 * CMLAtomArray hAtoms = hMol.getAtomArray (); CMLAtomArray atoms =
	 * getAtomArray (); CMLBondArray hBonds = hMol.getBondArray (); CMLBondArray
	 * bonds = getBondArray (); int numH;
	 * 
	 * if ((hAtoms != null) && ((numH = hAtoms.getAtomCount ()) > 0)) { if
	 * (atoms == null) { atoms = new CMLAtomArray (); }
	 *  // remove all hydrogenCounts for (int k = atoms.getAtomCount () - 1; k >=
	 * 0; -- k) { atoms.getAtom (k).setHydrogenCount (0); }
	 *  // add from hMol for (int k = numH - 1; k >= 0; -- k) { try {
	 * atoms.addAtom (hAtoms.getAtom (k)); //hAtoms.removeAtom (hAtoms.getAtom
	 * (k)); } catch (RuntimeException cmle) { System.err.println("BUG "+cmle); } } }
	 * 
	 * if ((hBonds != null) && ((numH = hBonds.getBondCount ()) > 0)) { if
	 * (bonds == null) { bonds = new CMLBondArray (); }
	 *  // add from hMol for (int k = numH - 1; k >= 0; -- k) { try {
	 * bonds.addBond (hBonds.getBond (k)); //hBonds.removeBond (hBonds.getBond
	 * (k)); } catch (RuntimeException cmle) { System.err.println("BUG "+cmle); } } } } } } --
	 */
	/**
	 * Recursively retrieves atom's parents, and returns the bond path.
	 * 
	 * Used by getRingNucleiBondSets
	 * 
	 * @param atom
	 * @param atomSetTool
	 *            containing spanning tree
	 * @return bondSet
	 */
	CMLBondSet getAncestors(CMLAtom atom, AtomSetTool atomSetTool) {
		CMLBondSet bondSet = new CMLBondSet();
		CMLAtom parentAtom = atomSetTool.getParent(atom);
		while (parentAtom != null) {
			CMLBond bond = molecule.getBond(atom, parentAtom);
			bondSet.addBond(bond);
			atom = parentAtom;
			parentAtom = atomSetTool.getParent(parentAtom);
		}
		return bondSet;
	}
	/** return the owning molecule.
	 * 
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
	}

	// *** Ring detection methods ***


	/**
	 * Identifies ringNucleusAtoms and ring systems.
	 * 
	 * @author Sam Adams
	 */
	private void findRingNuclei() {
		if (ringNucleusAtoms == null) {
			ringNucleusAtoms = new ArrayList<Set<CMLAtom>>();
			List<CMLAtom> path = new ArrayList<CMLAtom>();
			Set<CMLAtom> visitedAtoms = new HashSet<CMLAtom>();
	
			List<CMLAtom> atoms = molecule.getAtoms();
			Iterator<CMLAtom> iterator = atoms.iterator();
			while (visitedAtoms.size() < atoms.size()) {
				CMLAtom atom = iterator.next();
				if (!visitedAtoms.contains(atom)) {
					traceRingPaths(atom, null, visitedAtoms, path);
				}
			}
		}
	}
	
//	/** get the ringNucleusAtoms as atomsets.
//	 * maybe rewrite as RingTools
//	 * @return list of ringNucleusAtoms
//	 * 
//	 */
//	private List<Set<CMLAtom>> calculateRings() {
//		findRingNuclei();
//		return ringNucleusAtoms;
//	}
	
	/** get ringNucleusSet.
	 * @return ringNucleusSet
	 */
	public RingNucleusSet getRingNucleusSet() {
		List<CMLAtomSet> atomSetLists = this.getRingNucleiAtomSets();
		List<CMLBondSet> bondSetLists = this.getRingNucleiBondSets();
		RingNucleusSet ringNucleusSet = new RingNucleusSet();
		for (int i = 0; i < atomSetLists.size(); i++) {
			RingNucleus ringNucleus = new RingNucleus(atomSetLists.get(i), bondSetLists.get(i));
			ringNucleusSet.addRingNucleus(ringNucleus);
		}
		return ringNucleusSet;
	}


	/**
	 * Iterative part of ring finder.
	 * 
	 * @param nextAtom          Atom to move to
	 * @param lastAtom          Last atom in path
	 */
	protected void traceRingPaths(
			CMLAtom nextAtom, CMLAtom lastAtom, 
			Set<CMLAtom> visitedAtoms, List<CMLAtom> path) {
		if (ringNucleusAtoms.size() > 1000) {
			throw new RuntimeException("too many ringNucleusAtoms");
		}
		if (path.contains(nextAtom)) {

			// Found ring... extract ring path from full path
			boolean inring = false;
			Set<CMLAtom> newRing = new HashSet<CMLAtom>();

			for (CMLAtom atom : path) {
				if (inring) {
					newRing.add(atom);
				} else if (atom == nextAtom) {
					inring = true;
					newRing.add(atom);
				}
			}

			// Check whether new ring, or merge with existing
			for (int i = 0; i < ringNucleusAtoms.size(); i ++) {
				Set<CMLAtom> existingRing = ringNucleusAtoms.get(i);

				boolean overlap = false;
				for (CMLAtom at : newRing) {
					if (existingRing.contains(at)) {
						overlap = true;
						break;
					}
				}
				if (overlap) {
					newRing.addAll(existingRing);
					ringNucleusAtoms.remove(i);
					i --;
				}
			}
			ringNucleusAtoms.add(newRing);
		} else if (!visitedAtoms.contains(nextAtom)) {
			visitedAtoms.add(nextAtom);
			// Add atom to path
			path.add(nextAtom);

			// Proceed to each neighbour in turn
			for (CMLAtom atom : nextAtom.getLigandAtoms()) {
				if (atom != lastAtom) {
					traceRingPaths(atom, nextAtom, visitedAtoms, path);
				}
			}

			// Terminate path, backtrace
			if (path.remove(path.size() - 1) != nextAtom) {
				throw new RuntimeException("path sync error");
			}
		}
	}


	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}
	
	public List<BondTool> getAcyclicBondToolsWithDownstreamAtomsMatchingRMolecule(GroupTool groupToolTemplate) {
		List<BondTool> matchedBondTools = new ArrayList<BondTool>();
		CMLBondSet acyclicBondSet = new CMLBondSet(this.getAcyclicBonds());
		CMLBondSet nonHAcyclicBondSet = BondSetTool.getOrCreateTool(acyclicBondSet)
			.getBondSetExcludingElementTypes(new String[]{"H"});
		List<CMLBond> nonHAcyclicBonds =  nonHAcyclicBondSet.getBonds();
		for (CMLBond bond : nonHAcyclicBonds) {
			BondTool bondTool = BondTool.getOrCreateTool(bond);
			addMatchingBondTool(matchedBondTools, groupToolTemplate, bondTool, 0);
			addMatchingBondTool(matchedBondTools, groupToolTemplate, bondTool, 1);
		}
		return matchedBondTools;
	}

	private void addMatchingBondTool(List<BondTool> matchedBondTools, GroupTool groupToolTemplate, BondTool bondTool, int serial) {
		CMLBond rootBond = bondTool.getBond();
		CMLAtom rootAtom = rootBond.getAtom(serial);
		String downstreamMorganString = bondTool.getDownstreamMorganString(rootAtom);
		if (downstreamMorganString.equals(groupToolTemplate.getMorganString())) {
			matchedBondTools.add(bondTool);
			GroupTool groupTool = new GroupTool(groupToolTemplate);
			groupTool.setRootBond(rootBond);
			groupTool.setRootAtom(rootAtom);
			groupTool.setMorganString(downstreamMorganString);
			bondTool.addGroupTool(groupTool);
		}
	}
	
	public List<List<BondTool>> identifyGroupsOnAcyclicBonds() {
		Map<String, GroupTool> groupToolMap= GroupTool.getCommonGroupMap();
		List<List<BondTool>> bondToolListList = new ArrayList<List<BondTool>>();
		for (String name : groupToolMap.keySet()) {
			GroupTool groupTool = groupToolMap.get(name);
			List<BondTool> bondList = this.getAcyclicBondToolsWithDownstreamAtomsMatchingRMolecule(groupTool);
			if (bondList.size() > 0) {
				bondToolListList.add(bondList);
			}
		}
		return bondToolListList;
	}

	public static void outputGroups(List<List<BondTool>> bondToolListList) {
		for (List<BondTool> bondToolList : bondToolListList) {
			for (BondTool bondTool : bondToolList) {
				List<GroupTool> groupToolList = bondTool.getGroupToolList();
				for (GroupTool groupTool : groupToolList) {
					CMLBond bond = groupTool.getRootBond();
				}
			}
		}
	}
	
	public static void pruneGroupsAndReLabel(List<List<BondTool>> bondToolListList) {
		for (List<BondTool> bondToolList : bondToolListList) {
			for (BondTool bondTool : bondToolList) {
				CMLBond bond = bondTool.getBond();
				List<GroupTool> groupToolList = bondTool.getGroupToolList();
				for (GroupTool groupTool : groupToolList) {
					CMLAtom rootAtom = groupTool.getRootAtom();
					CMLAtom otherAtom = bond.getOtherAtom(rootAtom);
//					rootAtom.setElementType("R");
					otherAtom.setElementType(groupTool.getName());
					CMLLabel label = new CMLLabel();
					label.setCMLValue(groupTool.getName());
					otherAtom.addLabel(label);
					bondTool.deleteDownstreamAtoms(rootAtom, otherAtom);
				}
			}
		}
	}


	public List<CMLAtom> getMethylGroups() {
		List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> methylGroupList = new ArrayList<CMLAtom>();
		for (CMLAtom atom : atoms) {
			if (AS.C.equals(atom.getElementType())) {
				if (atom.getLigandHydrogenAtoms().size() == 3 && atom.getLigandAtoms().size() == 4) {
					methylGroupList.add(atom);
				}
			}
		}
		return methylGroupList;
	}

	public void contractNAlkylGroups() {
		ConnectionTableTool connectionTableTool = new ConnectionTableTool(molecule);
		List<CMLAtom> methylGroups = connectionTableTool.getMethylGroups();
		for (CMLAtom atom : methylGroups) {
			CMLAtom startAtom = atom;
			CMLAtom currentAtom = atom;
			CMLAtom lastAtom = null;
			CMLAtom nextAtom;
			int len = 1;
			while (true) {
				nextAtom = connectionTableTool.getNextPotentialMethylene(currentAtom, lastAtom);
				if (nextAtom == null) {
					// deal with methyl
					if (lastAtom == null) {
						lastAtom = startAtom;
					}
					break;
				}
				len++;
				lastAtom = currentAtom;
				currentAtom = nextAtom;
			}
			nextAtom = connectionTableTool.getNextAtom(currentAtom, lastAtom);
			LOG.trace("len "+len+
				" last/"+((lastAtom!=null) ? lastAtom.getId() : "null")+
				" curr/"+((currentAtom!=null) ? currentAtom.getId() : "null")+
				" next/"+((nextAtom!=null) ? nextAtom.getId() : "null")
			);
			String alkylName = GroupTool.getAlkylName(len);
			if (len > 1) {
				CMLBond deleteBond = molecule.getBond(currentAtom, nextAtom);
				if (deleteBond != null) {
					BondTool.getOrCreateTool(deleteBond).deleteDownstreamAtoms(nextAtom, currentAtom);
				} else {
					throw new RuntimeException("NO bond to delete");
				}
			}
			currentAtom.setElementType(alkylName);
		}
	}

	private CMLAtom getNextAtom(CMLAtom currentAtom, CMLAtom lastAtom) {
		List<CMLAtom> ligands = currentAtom.getLigandAtoms();
		CMLAtom nextAtom = null;
		for (CMLAtom ligand : ligands) {
			String elementType = ligand.getElementType();
			if (AS.H.equals(elementType)) {
				// ignore
			} else if (ligand.equals(lastAtom)) {
				// ignore
			} else {
				nextAtom = ligand;
				break;
			}
		}
		return nextAtom;
	}
	
	private CMLAtom getNextPotentialMethylene(CMLAtom currentAtom, CMLAtom lastAtom) {
		List<CMLAtom> ligands = currentAtom.getLigandAtoms();
		CMLAtom nextAtom = null;
		for (CMLAtom ligand : ligands) {
			String elementType = ligand.getElementType();
			if (AS.H.equals(elementType)) {
				// ignore
			} else if (ligand.equals(lastAtom)) {
				// ignore
			} else if (!AS.C.equals(elementType)) {
				nextAtom = null;
				break;
			} else if (AS.C.equals(elementType)) {
				if (nextAtom == null) {
					nextAtom = ligand;
				} else {
					// too many ligands
					nextAtom = null;
					break;
				}
			}
		}
		return nextAtom;
	}

	public List<CMLAtom> getFullyAcyclicAtoms() {
		List<CMLAtom> atoms = molecule.getAtoms();
		
		List<CMLAtom> acyclicAtoms = new ArrayList<CMLAtom>();
		List<CMLBond> acyclicBonds = getAcyclicBonds();
		Set<CMLBond> acyclicBondSet = new HashSet<CMLBond>();
		for (CMLBond acyclicBond : acyclicBonds) {
			acyclicBondSet.add(acyclicBond);
		}
		for (CMLAtom atom : atoms) {
			List<CMLBond> ligandBonds = atom.getLigandBonds();
			boolean acyclic = true;
			for (CMLBond ligandBond : ligandBonds) {
				if (!acyclicBondSet.contains(ligandBond)) {
					acyclic = false;
					break;
				}
			}
			if (acyclic) {
				acyclicAtoms.add(atom);
			}
		}
		return acyclicAtoms;
	}
}
