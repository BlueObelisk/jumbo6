package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLAtomArray;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLBondArray;
import org.xmlcml.cml.element.lite.CMLFormula;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.lite.CMLScalar;
import org.xmlcml.cml.element.lite.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.main.CMLAtomSet;
import org.xmlcml.cml.element.main.CMLBondSet;
import org.xmlcml.cml.element.main.CMLElectron;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;

/**
 * @author pm286
 *
 */

public class ValencyTool extends AbstractTool {
	/** */
	public final static int UNKNOWN_CHARGE = 99999;

	private CMLMolecule molecule;
	private MoleculeTool moleculeTool;
	private String formulaS;

	private List<CMLAtom> commonGroupMarkedUpAtoms = new ArrayList<CMLAtom>();

	/** dewisott */
	public static String metalLigandDictRef = "jumbo:metalLigand";

	/** constructor
	 * @param molecule
	 */
	public ValencyTool(CMLMolecule molecule) {
		this.molecule = molecule;
		moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		formulaS = formula.getConcise();
		if (formulaS != null) {
			formulaS = CMLFormula.removeChargeFromConcise(formulaS);
		}
		molecule.addToLog(Severity.INFO, "Formula " + formulaS);
	}
	/**
	 * adds charges and bond orders for common species.
	 * 
	 * @return true if identified as common molecule
	 */
	private boolean markupCommonMolecules() {
		boolean marked = false;

		List<CMLAtom> atoms = molecule.getAtoms();
		int count = atoms.size();
		if (count == 2) {
			marked = this.markMetalCarbonylAndNitrile(atoms);
		}
		if (count == 3) {
			marked = this.markThiocyanate(atoms);
			marked = this.markNCN(atoms);
		}

		if (!marked) {
			if (formulaS.equals("N 1 O 1")) {
				this.markNO(atoms);
			} else if (formulaS.equals("C 2 N 3")) {
				this.markNCNCN(atoms);
			} else if (formulaS.equals("N 3")) {
				this.markN3(atoms);
				// SiF6
			} else if (formulaS.equals("F 6 Si 1")) {
				addCharge("Si", -2);
				// CO3
			} else if (formulaS.equals("C 1 O 3")) {
				addDoubleCharge("C", 0, "O", 2);
				// HCO3
			} else if (formulaS.equals("C 1 H 1 O 3")) {
				addDoubleCharge("C", 0, "O", 1);
				// H2CO3
			} else if (formulaS.equals("C 1 H 2 O 3")) {
				addDoubleCharge("C", 0, "O", 0);
				// NO3
			} else if (formulaS.equals("N 1 O 3")) {
				addDoubleCharge("N", 1, "O", 2);
				// HNO3
			} else if (formulaS.equals("H 1 N 1 O 3")) {
				addDoubleCharge("N", 1, "O", 1);
				// SO4
			} else if (formulaS.equals("O 4 S 1")) {
				addDoubleCharge("S", 0, "O", 2);
				// HSO4
			} else if (formulaS.equals("H 1 O 4 S 1")) {
				addDoubleCharge("S", 0, "O", 1);
				// H2SO4
			} else if (formulaS.equals("H 2 O 4 S 1")) {
				addDoubleCharge("S", 0, "O", 0);
				// PF6
			} else if (formulaS.equals("F 6 P 1")) {
				addCharge("P", -1);
				// PO4, etc.
			} else if (formulaS.equals("O 4 P 1")) {
				addDoubleCharge("P", 0, "O", 3);
				// HPO4, etc.
			} else if (formulaS.equals("H 1 O 4 P 1")) {
				addDoubleCharge("P", 0, "O", 2);
				// H2PO4, etc.
			} else if (formulaS.equals("H 2 O 4 P 1")) {
				addDoubleCharge("P", 0, "O", 1);
				// H3PO4, etc.
			} else if (formulaS.equals("H 3 O 4 P 1")) {
				addDoubleCharge("P", 0, "O", 0);
				// PO3
			} else if (formulaS.equals("O 3 P 1")) {
				addDoubleCharge("P", 0, "O", 3);
				// HPO3
			} else if (formulaS.equals("H 1 O 3 P 1")) {
				addDoubleCharge("P", 0, "O", 2);
				// H2PO3
			} else if (formulaS.equals("H 2 O 3 P 1")) {
				addDoubleCharge("P", 0, "O", 1);
				// H3PO3
			} else if (formulaS.equals("H 3 O 3 P 1")) {
				addDoubleCharge("P", 0, "O", 0);
				// ClO3, ClO4
			} else if (formulaS.equals("Cl 1 O 3") || formulaS.equals("Cl 1 O 4")) {
				addDoubleCharge("Cl", 0, "O", 1);
				// HClO3, HClO4
			} else if (formulaS.equals("H 1 Cl 1 O 3")
					|| formulaS.equals("H 1 Cl 1 O 4")) {
				addDoubleCharge("Cl", 0, "O", 0);
				// BrO3, BrO4
			} else if (formulaS.equals("Br 1 O 3") || formulaS.equals("O 4 Br 1")) {
				addDoubleCharge("Br", 0, "O", 1);
				// IO3, IO4
			} else if (formulaS.equals("I 1 O 3") || formulaS.equals("I 1 O 4")) {
				addDoubleCharge("I", 0, "O", 1);
			} else if (formulaS.equals("C 2 N 2")) { 
				for (CMLAtom atom : molecule.getAtoms()) {
					if ("N".equals(atom.getElementType())) {
						CMLBond bond = atom.getLigandBonds().get(0);
						this.setBondOrder(bond, CMLBond.TRIPLE);
					}
				}
			} else if (formulaS.equals("C 1 S 2")) {
				for (CMLBond bond : molecule.getBonds()) {
					this.setBondOrder(bond, CMLBond.DOUBLE);
				}
			} else {
				marked = false;
			}
		}
		return marked;
	}

	private void markMetalCarbons(List<CMLAtom> atoms) {
		// if carbon bonded to certain metal atoms, then add - to carbon atom
		for (CMLAtom atom : atoms) {
			if ("Ag".equals(atom.getElementType()) ||
					"Sn".equals(atom.getElementType()) ||
					"Hg".equals(atom.getElementType())) {
				for (CMLAtom ligand : atom.getLigandAtoms()) {
					if ("C".equals(ligand.getElementType())) {
						this.setAtomCharge(ligand, -1);
					}
				}
			}
		}
	}

	private void addCharge(String elementType, int charge) {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (atom.getElementType().equals(elementType)) {
				this.setAtomCharge(atom, charge);
			}
		}
	}
	
	/**
	 * @deprecated Renamed to markupCommonGroups.
	 */
	public void markupSpecial() {
		markupCommonGroups();
	}
	
	/**
	 * special routines. mark common groups with charges and 
	 * bond orders
	 * 
	 */
	public void markupCommonGroups() {
		List<CMLAtom> atoms = molecule.getAtoms();
		this.markMetalCNN(atoms);
		this.markCarboxyAnion(atoms);
		this.markKetone(atoms);
		this.markCS2(atoms);
		this.markCOS(atoms);
		this.markNitro(atoms);
		this.markPAnion(atoms);
		this.markSulfo(atoms);
		this.markTerminalCarbyne(atoms);
		this.mark_CSi_anion(atoms);
		this.markQuaternaryBAlGaIn(atoms);
		this.markQuaternaryNPAsSb(atoms);
		this.markTerminalCN(atoms);
		this.markOSQuatP(atoms);
		this.markAzideGroup(atoms);
		this.markM_PN_C(atoms);
		this.markMCN(atoms);
		this.markMNN(atoms);
		this.markPNP(atoms);
		this.markMCC(atoms);
		this.markSNS(atoms);
		this.markSandwichLigands(atoms);
		this.markPyridineN(atoms);
		this.markHydride(atoms);
	}

	private void addDoubleCharge(String centralS, int centralCharge,
			String ligandS, int nChargeLigand) {
		CMLAtom centralA = getCentralAtom(centralS);
		addDoubleCharge(centralA, centralCharge, ligandS, nChargeLigand);
	}

	private CMLAtom getCentralAtom(String centralS) {
		CMLAtom centralA = null;
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (centralS.equals(atom.getElementType())) {
				centralA = atom;
				break;
			}
		}
		return centralA;
	}

	private void addDoubleCharge(CMLAtom centralA, int centralCharge,
			String ligandS, int nChargeLigand) {
		List<CMLAtom> atoms = molecule.getAtoms();
		this.setAtomCharge(centralA, centralCharge);
		int count = 0;
		for (CMLAtom atom : atoms) {
			if (ligandS.equals(atom.getElementType())) {
				int bos = AtomTool.getOrCreateTool(atom).getBondOrderSum();
				if (bos == 1) {
					if (count++ < nChargeLigand) {
						this.setAtomCharge(atom, -1);
					} else {
						molecule.getBond(centralA, atom).setOrder(
								CMLBond.DOUBLE);
					}
				}
			}
		}
	}

	private void markMetalCNN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType()) && isBondedToMetal(atom)
					&& atom.getLigandAtoms().size() == 2) {
				int nCount = 0;
				for (CMLAtom ligand : atom.getLigandAtoms()) {
					if ("N".equals(ligand.getElementType()) &&
							ligand.getLigandAtoms().size() == 3) {
						nCount++;
					}
				}
				if (nCount == 2) {
					this.setAtomCharge(atom, -1);
					CMLAtom nAtom = atom.getLigandAtoms().get(0);
					this.setAtomCharge(nAtom, 1);
					this.setBondOrder(molecule.getBond(atom, nAtom), CMLBond.DOUBLE);
				}
			}
		}
	}

	private void markKetone(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			// may already have been set in markCarboxyAnion if so
			// atoms will be in alreadySetAtoms list so just make sure
			// they are not before continuing
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("O".equals(atom.getElementType()) && !isBondedToMetal(atom)
					&& atom.getLigandAtoms().size() ==1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) &&
						ligand.getLigandAtoms().size() == 3) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.DOUBLE);
				}
			}
		}
	}

	private void markNO(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType())) {
				this.setAtomCharge(atom, -1);
				CMLBond bond = atom.getLigandBonds().get(0);
				this.setBondOrder(bond, CMLBond.DOUBLE);
			}
		}
	}

	private void markHydride(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("H".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 0 
					&& isBondedToMetal(atom)) {
				this.setAtomCharge(atom, -1);
			}
		}
	}

	private void markEarthMetals(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			ChemicalElement ce = atom.getChemicalElement();
			if (ce.isChemicalElementType(Type.GROUP_A)) {
				this.setAtomCharge(atom, 1);
			} else if (ce.isChemicalElementType(Type.GROUP_B)) {
				this.setAtomCharge(atom, 2);
			}
		}
	}

	private void markLoneNonMetalAnions(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (("F".equals(atom.getElementType()) 
					|| "Cl".equals(atom.getElementType())
					|| "Br".equals(atom.getElementType())
					|| "I".equals(atom.getElementType()))
					&& atom.getLigandAtoms().size() == 0) {
				this.setAtomCharge(atom, -1);
			}
			if (("O".equals(atom.getElementType()) 
					|| "S".equals(atom.getElementType()))
					&& atom.getLigandAtoms().size() == 0) {
				this.setAtomCharge(atom, -2);
			}
			if (("N".equals(atom.getElementType()))
					&& atom.getLigandAtoms().size() == 0) {
				this.setAtomCharge(atom, -3);
			}	
		}
	}

	private void markSNS(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 2) {
				int sCount = 0;
				for (CMLAtom ligand : atom.getLigandAtoms()) {
					if ("S".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
						sCount++;
					}
				}
				if (sCount == 2) {
					this.setAtomCharge(atom, -1);
					for (CMLAtom ligand : atom.getLigandAtoms()) {
						this.setAtomCharge(ligand, 1);
					}
				}
			}
		}
	}

	private void markMCC(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (isBondedToMetal(atom) && "C".equals(atom.getElementType()) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.DOUBLE);
					this.setAtomCharge(atom, -2);
				}
			}
		}
	}

	private void markMNN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (isBondedToMetal(atom) && "N".equals(atom.getElementType()) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("N".equals(ligand.getElementType())){
					if (ligand.getLigandAtoms().size() == 3) {
						CMLBond bond = atom.getLigandBonds().get(0);
						this.setBondOrder(bond, CMLBond.DOUBLE);
						this.setAtomCharge(atom, -1);
						this.setAtomCharge(ligand, 1);
					}
					if (ligand.getLigandAtoms().size() == 2) {
						this.setAtomCharge(atom, -2);
					}
				}
			}
		}
	}

	private void markPNP(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 2) {
				int pCount = 0;
				for (CMLAtom at : atom.getLigandAtoms()) {
					if ("P".equals(at.getElementType())) {
						pCount++;
					}
				}
				if (pCount == 2) {
					this.setAtomCharge(atom, -1);
				}
			}
		}
	}

	private void markMCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (isBondedToMetal(atom) && "C".equals(atom.getElementType()) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("N".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 2) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.TRIPLE);
					this.setAtomCharge(atom, -1);
					this.setAtomCharge(ligand, 1);
				}
			}
		}
	}

	private void markM_PN_C(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (isBondedToMetal(atom) && ("N".equals(atom.getElementType()) || "P".equals(atom.getElementType())) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					this.setAtomCharge(atom, -2);
				}
			}
		}
	}

	private void markSandwichLigands(List<CMLAtom> atoms) {
		CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(atoms);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		CMLMolecule mol = MoleculeTool.createMolecule(atomSet, bondSet);
		ConnectionTableTool ctool = new ConnectionTableTool(mol);
		List<CMLAtomSet> ringSetList = ctool.getRingNucleiAtomSets();
		for (CMLAtomSet ringSet : ringSetList) {
			// skip if the ring contains an even number of atoms - even
			// numbered rings are handled in adjustBondOrdersAndChargeToValency
			if (ringSet.getSize() % 2 == 0) continue;
			int count = 0;
			List<CMLAtom> atomList = ringSet.getAtoms();
			List<CMLAtom> ringAtomList = new ArrayList<CMLAtom>();
			for (CMLAtom atom : atomList) {
				if (isBondedToMetal(atom)) {
					count++;
					ringAtomList.add(atom);
				}
			}
			if (count > 1 && count % 2 == 1) {
				CMLAtom atom = molecule.getAtomById(ringAtomList.get(0).getId());
				this.setAtomCharge(atom, -1);
			}
		}
	}

	private void markPyridineN(List<CMLAtom> atoms) {
		CMLAtomSet atomSet = CMLAtomSet.createFromAtoms(atoms);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		CMLMolecule mol = MoleculeTool.createMolecule(atomSet, bondSet);
		ConnectionTableTool ctool = new ConnectionTableTool(mol);
		List<CMLAtomSet> ringSetList = ctool.getRingNucleiAtomSets();
		for (CMLAtomSet ringSet : ringSetList) {
			if (ringSet.size() == 6) {
				int nCount = 0;
				CMLAtom nAtom = null;
				for (CMLAtom ringAtom : ringSet.getAtoms()) {
					if ("N".equals(ringAtom.getElementType())) {
						nCount++;
						nAtom = ringAtom;
					}
				}
				if (nCount == 1) {
					CMLMolecule sprout = MoleculeTool.getOrCreateTool(mol).sprout(ringSet);
					if (sprout.getAtomCount() == 11) {
						this.setAtomCharge(nAtom, 0);
					}
				}
			}
		}
	}

	private void markAzideGroup(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 1) {
				CMLAtom lig = atom.getLigandAtoms().get(0);
				if ("N".equals(lig.getElementType())) {
					List<CMLAtom> ligLigs = lig.getLigandAtoms();
					if (ligLigs.size() == 2) {
						int count = 0;
						for (CMLAtom ligLig : ligLigs) {
							if ("N".equals(ligLig.getElementType())) {
								count++;
							}
						}
						if (count == 2) {
							setAtomCharge(atom, -1);
							setAtomCharge(lig, 1);
							for (CMLAtom ligLig : ligLigs) {
								setBondOrder(molecule.getBond(lig, ligLig), CMLBond.DOUBLE);
							}
						}
					}
				}
			}
		}
	}

	private void markQuaternaryNPAsSb(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (("N".equals(atom.getElementType()) ||
					"P".equals(atom.getElementType()) ||
					"As".equals(atom.getElementType()) ||
					"Sb".equals(atom.getElementType())) && 
					atom.getLigandAtoms().size() == 4) {
				this.setAtomCharge(atom, 1);
			}
		}
	}

	/*
	 * mark O or S next to a quaternary P as negative
	 */
	private void markOSQuatP(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("O".equals(atom.getElementType()) || "S".equals(atom.getElementType())) {
				if (atom.getLigandAtoms().size() == 1) {
					CMLAtom ligand = atom.getLigandAtoms().get(0);
					if ("P".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 4) {
						this.setAtomCharge(atom, -1);
					}
				}
			}
		}
	}

	private void markTerminalCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 1) {
				CMLAtom lig = atom.getLigandAtoms().get(0);
				if ("C".equals(lig.getElementType()) && lig.getLigandAtoms().size() == 2) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.TRIPLE);
				}
			}
		}
	}

	private void markNCNCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType())) {
				if (atom.getLigandAtoms().size() == 2) {
					this.setAtomCharge(atom, -1);
					for (CMLBond bond : atom.getLigandBonds()) {
						this.setBondOrder(bond, CMLBond.SINGLE);
					}
				} else if (atom.getLigandAtoms().size() == 1) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.TRIPLE);
				}
			}
		}
	}

	private void markQuaternaryBAlGaIn(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (("B".equals(atom.getElementType()) ||
					"Al".equals(atom.getElementType()) ||
					"Ga".equals(atom.getElementType()) ||
					"In".equals(atom.getElementType())) &&
					atom.getLigandAtoms().size() == 4) {
				this.setAtomCharge(atom, -1);
			}
		}
	}

	private boolean markNCN(List<CMLAtom> atoms) {
		boolean marked = false;
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> nList = new ArrayList<CMLAtom>(2);
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 2) {
					for (CMLAtom ligand : ligands) {
						if ("N".equals(ligand.getElementType())) {
							nList.add(ligand);
						}
					}
					if (nList.size() == 2) {
						CMLAtom negative = nList.get(0);
						this.setAtomCharge(negative, -1);
						CMLBond bond1 = molecule.getBond(atom, negative);
						CMLBond bond2 = molecule.getBond(atom, nList.get(1));
						this.setBondOrder(bond1, CMLBond.DOUBLE);
						this.setBondOrder(bond2, CMLBond.DOUBLE);
						marked = true;
					}
				}
			}
		}
		return marked;
	}

	private void markN3(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if (atom.getLigandAtoms().size() == 2) {
				setAtomCharge(atom, 1);
				for (CMLAtom ligand : atom.getLigandAtoms()) {
					setBondOrder(molecule.getBond(atom, ligand), CMLBond.DOUBLE);
					setAtomCharge(ligand, -1);
				}
			}
		}
	}


	/**
	 * mark O=C-O(-)
	 */
	private void markCarboxyAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3) {
					List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
					for (CMLAtom ligand : ligands) {
						AtomTool ligandTool = AtomTool.getOrCreateTool(ligand);
						if ("O".equals(ligand.getElementType())
								&& ligandTool.getBondOrderSum() == 1) {
							oxyList.add(ligand);
						}
					}
					marker:
						if (oxyList.size() == 2) {
							for (CMLAtom oAtom : oxyList) {
								if (isBondedToMetal(oAtom)) {
									this.setAtomCharge(oAtom, -1);
									for (CMLAtom oAt : oxyList) {
										if (oAt != oAtom) {
											CMLBond bond = molecule.getBond(atom, oAt);
											this.setBondOrder(bond, CMLBond.DOUBLE);
											break marker;
										}
									}
								}
							}
							for (CMLAtom oAtom : oxyList) {
								this.setAtomCharge(oAtom, -1);
								for (CMLAtom oAt : oxyList) {
									if (oAt != oAtom) {
										CMLBond bond = molecule.getBond(atom, oAt);
										this.setBondOrder(bond, CMLBond.DOUBLE);
										break marker;
									}
								}
							}
						}
				}
			}
		}
	}

	/**
	 * mark -C-(triple bond)-O and -C-(triple bond)-N
	 */
	private boolean markMetalCarbonylAndNitrile(List<CMLAtom> atoms) {
		boolean marked = false;
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 1) {
					if ("O".equals(ligands.get(0).getElementType())) {
						CMLBond bond = atom.getLigandBonds().get(0);
						this.setBondOrder(bond, CMLBond.TRIPLE);
						this.setAtomCharge(atom, -1);
						CMLAtom lig = ligands.get(0);
						this.setAtomCharge(lig, 1);
						marked =  true;
					} else if ("N".equals(ligands.get(0).getElementType())) {
						CMLBond bond = atom.getLigandBonds().get(0);
						this.setBondOrder(bond, CMLBond.TRIPLE);
						this.setAtomCharge(atom, -1);
						marked = true;
					}
				}
			}
		}
		return marked;
	}

	private void markTerminalCarbyne(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				if (isBondedToMetal(atom)) {
					List<CMLAtom> ligands = atom.getLigandAtoms();
					if (ligands.size() == 1) {
						CMLAtom lig = ligands.get(0);
						if ("C".equals(lig.getElementType()) && lig.getLigandBonds().size() < 3) {
							CMLBond bond = atom.getLigandBonds().get(0);
							this.setAtomCharge(atom, -1);
							this.setBondOrder(bond, CMLBond.TRIPLE);
						}
					}
				}
			}
		}
	}

	private void mark_CSi_anion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType()) || "Si".equals(atom.getElementType())) {
				int hCount = 0;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3 && isBondedToMetal(atom)) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if ("H".equals(ligand.getElementType())) {
							hCount++;
						}
						if (isBondedToMetal(ligand)) {
							set = false;
						}
					}
					if (set && hCount == 2) {
						this.setAtomCharge(atom, -1);
					}
				}
				// if carbon has only two ligands then check to see if it
				// can be marked as a carbanion.
				if (ligands.size() == 2  && isBondedToMetal(atom)) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if ("C".equals(ligand.getElementType()) || "Si".equals(ligand.getElementType())) {
							List<CMLAtom> ats = ligand.getLigandAtoms();
							if (ats.size() == 2) {
								// if one of the two ligands also only
								// has two ligands then stop as this is
								// most probably a carbyne.
								for (CMLAtom at : ats) {
									if (at.equals(atom)) {
										continue;
									}
									if (!"N".equals(at.getElementType())) {
										set = false;
									}
								}
							}
							if (ats.size() < 2) {
								set = false;
							}
						} else {
							set = false;
							break;
						}
					} if (set) {
						this.setAtomCharge(atom, -1);
					}
				}
			}
		}
	}

	/**
	 * mark N-O compounds. X-NO2, X3N(+)-(O-) where X is not O
	 */
	private void markNitro(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					AtomTool ligandTool = AtomTool.getOrCreateTool(ligand);
					if ("O".equals(ligand.getElementType())
							&& ligandTool.getBondOrderSum() == 1) {
						oxyList.add(ligand);
					}
				}
				if (oxyList.size() == 2) {
					this.setAtomCharge(oxyList.get(0), -1);
					this.setBondOrder(molecule.getBond(atom, oxyList.get(1)), CMLBond.DOUBLE);
				}
			}
		}
	}

	private void markPAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("P".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 2) {
					this.setAtomCharge(atom, -1);
				}
			}
		}
	}

	/**
	 * mark S-O compounds. X-SO-Y and X-S)2-Y where X, Y are not O
	 */
	private void markSulfo(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("S".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				int ligandCount = ligands.size();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					AtomTool ligandTool = AtomTool.getOrCreateTool(ligand);
					if ("O".equals(ligand.getElementType())
							&& ligandTool.getBondOrderSum() == 1) {
						oxyList.add(ligand);
					}
				}
				int oxyCount = oxyList.size();
				if (ligandCount == 3) {
					if (oxyCount > 0) {
						this.setBondOrder(molecule.getBond(atom, oxyList.get(0)), CMLBond.DOUBLE);
					} else if (oxyCount == 2) {
						this.setAtomCharge(oxyList.get(1), -1);
					}
				} else if (ligandCount == 4) {			
					if (oxyList.size() == 2) {
						for (CMLAtom oxy : oxyList) {
							this.setBondOrder(molecule.getBond(atom, oxy), CMLBond.DOUBLE);
						}
					} else if (oxyList.size() == 3) {
						this.setBondOrder(molecule.getBond(atom, oxyList.get(0)), CMLBond.DOUBLE);
						this.setBondOrder(molecule.getBond(atom, oxyList.get(1)), CMLBond.DOUBLE);
						this.setAtomCharge(oxyList.get(2), -1);
					}
				}
			}
		}
	}

	/**
	 * mark S=C-S(-)
	 */
	private void markCS2(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> sulfoList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					AtomTool ligandTool = AtomTool.getOrCreateTool(ligand);
					if ("S".equals(ligand.getElementType())
							&& ligandTool.getBondOrderSum() == 1) {
						sulfoList.add(ligand);
					}
				}
				if (sulfoList.size() == 2) {
					this.setAtomCharge(sulfoList.get(0), -1);
					this.setBondOrder(molecule.getBond(atom, sulfoList.get(1)), CMLBond.DOUBLE);
				}
			}
		}
	}

	private void markCOS(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				CMLAtom o = null;
				CMLAtom s = null;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				for (CMLAtom ligand : ligands) {
					AtomTool ligandTool = AtomTool.getOrCreateTool(ligand);
					if ("S".equals(ligand.getElementType())
							&& ligandTool.getBondOrderSum() == 1) {
						s = ligand;
					}
					if ("O".equals(ligand.getElementType())
							&& ligandTool.getBondOrderSum() == 1) {
						o = ligand;
					}
				}
				if (s != null && o != null) {
					this.setAtomCharge(s, -1);
					this.setBondOrder(molecule.getBond(atom, o), CMLBond.DOUBLE);
				}
			}
		}
	}


	private boolean markThiocyanate(List<CMLAtom> atoms) {
		boolean marked = false;
		for (CMLAtom atom : atoms) {
			if (commonGroupMarkedUpAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				CMLAtom c = null;
				CMLAtom s = null;
				CMLAtom n = null;
				c = atom;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 2) {
					for (CMLAtom ligand : ligands) {
						if ("S".equals(ligand.getElementType())) {
							s = ligand;
						}
						if ("N".equals(ligand.getElementType())) {
							n = ligand;
						}
					}
				}
				if (s != null && n != null) {
					this.setAtomCharge(n, -1);
					this.setBondOrder(molecule.getBond(c, s), CMLBond.DOUBLE);
					this.setBondOrder(molecule.getBond(c, n), CMLBond.DOUBLE);
					marked = true;
				}
			}
		}
		return marked;
	}

	/**
	 * 
	 * @param piSystemManager
	 * @param knownMolCharge charge on molecule provided
	 * @return obsolete
	 */
	public boolean adjustBondOrdersAndChargesToValency(
			PiSystemControls piSystemManager, int knownMolCharge) {

		boolean isMetalComplex = false;
		boolean success = false;

		List<CMLMolecule> mols = molecule.getDescendantsOrMolecule();
		for (CMLMolecule mol : mols) {
			mol.setBondOrders(CMLBond.SINGLE);
			ValencyTool vTool = new ValencyTool(mol);
			List<CMLAtom> molAtoms = mol.getAtoms();
			vTool.markMetalCarbons(molAtoms);
			vTool.markEarthMetals(molAtoms);
			// remove metal atoms so we can calculate bond orders on organic species, 
			// then we will reattach the metal atoms later in the method
			Map<List<CMLAtom>, List<CMLBond>> metalAtomAndBondMap = ValencyTool.removeMetalAtomsAndBonds(mol);
			for (Iterator<List<CMLAtom>> it=metalAtomAndBondMap.keySet().iterator(); it.hasNext(); ) {
				List<CMLAtom> atomList = it.next();
				if (atomList.size() > 0) {
					isMetalComplex = true;
					// as we can't take metals into account when calculating bonds and charges, then
					// the supplied molecular charge cannot be used.
					knownMolCharge = UNKNOWN_CHARGE;
				}
			}
			// now the metal atoms and bonds have been removed, partition 
			// molecule into submolecules and then calculate the bond orders
			// and charges for each
			if (isMetalComplex) {
				ConnectionTableTool ctt = new ConnectionTableTool(mol);
				ctt.partitionIntoMolecules();
			}
			if (mol.getAtomCount() != 0 ) {
				List<CMLMolecule> subMols = mol.getDescendantsOrMolecule();
				for (CMLMolecule subMol : subMols) {	
					success = adjustBondOrdersAndChargesToValencyNED(subMol, piSystemManager, knownMolCharge, isMetalComplex);
					// if can't correctly add bond orders and charges to one component, then don't do any - reset all bond orders and charges
					if (!success) {
						resetAllBondOrdersAndCharges(molecule);
						break;
					}
				}
			}
			// return the mol to its original state before adding metal atoms and bonds back in
			if (isMetalComplex) {
				ConnectionTableTool ctt = new ConnectionTableTool(mol);
				ctt.flattenMolecules();
			}
			// reattach metal atoms and bonds now bonds have been calculated
			ValencyTool.addMetalAtomsAndBonds(mol, metalAtomAndBondMap);
		}
		return success;
	}

	private boolean adjustBondOrdersAndChargesToValencyNED(CMLMolecule mol, PiSystemControls piSystemManager, int knownMolCharge, boolean isMetalComplex) {
		// if the removal of metal atoms takes the molecules atom count 
		// to zero or one then don't bother calculating bonds
		if (mol.getAtomCount() == 1) {
			ValencyTool valencyTool = new ValencyTool(mol);
			valencyTool.markLoneNonMetalAnions(mol.getAtoms());
		} else {
			ValencyTool valencyTool = new ValencyTool(mol);
			boolean isCommonMol = valencyTool.markupCommonMolecules();
			if (!isCommonMol) {
				HeteroAtomManager ham = new HeteroAtomManager(valencyTool, piSystemManager, isMetalComplex, knownMolCharge);
				boolean success = ham.manipulateHeteroatoms();
				if (!success) {
					return false;
				}
			}
		}
		return true;
	}

	private void resetAllBondOrdersAndCharges(CMLMolecule molecule) {
		for (CMLAtom atom : molecule.getAtoms()) {
			atom.setFormalCharge(0);
		}
		for (CMLBond bond : molecule.getBonds()) {
			bond.setOrder(CMLBond.SINGLE);
		}
	}

	/**
	 * add double bonds through PiSystemManager.
	 * @param molCharge 
	 * @return obsolete
	 */
	public boolean adjustBondOrdersAndChargesToValency(int molCharge) {
		PiSystemControls piSystemManager = new PiSystemControls();
		piSystemManager.setUpdateBonds(true);
		piSystemManager.setKnownUnpaired(0);
		piSystemManager.setDistributeCharge(true);
		boolean success = this.adjustBondOrdersAndChargesToValency(piSystemManager, molCharge);
		return success;
	}

	/**
	 * add double bonds through PiSystemManager.
	 * @return obsolete
	 */
	public boolean adjustBondOrdersAndChargesToValency() {
		PiSystemControls piSystemManager = new PiSystemControls();
		piSystemManager.setUpdateBonds(true);
		piSystemManager.setKnownUnpaired(0);
		piSystemManager.setDistributeCharge(true);
		boolean success = this.adjustBondOrdersAndChargesToValency(piSystemManager, ValencyTool.UNKNOWN_CHARGE);
		return success;
	}

	private void setAtomCharge(CMLAtom atom, int charge) {
		atom.setFormalCharge(charge);
		commonGroupMarkedUpAtoms.add(atom);
	}

	private void setBondOrder(CMLBond bond, String order) {
		bond.setOrder(order);
		// add to alreadySetAtoms
		for (CMLAtom at : bond.getAtoms()) {
			commonGroupMarkedUpAtoms.add(at);
		}
	}

	/**
	 * @return list of atoms
	 */
	public List<CMLAtom> getCommonGroupMarkedupAtoms() {
		return this.commonGroupMarkedUpAtoms;
	}
	/**
	 * removes metal atoms and bonds from the supplied molecule.  Optional to tag atoms that were metal ligands
	 * with a 'bonded to metal tag'.
	 * returns a map containing the list of metal atoms and bonds removed.
	 * @param mol 
	 * @return map 
	 */
	public static Map<List<CMLAtom>, List<CMLBond>> removeMetalAtomsAndBonds(CMLMolecule mol) {
		List<CMLAtom> metalAtomList = new ArrayList<CMLAtom>();
		List<CMLBond> metalBondList = new ArrayList<CMLBond>();
		List<CMLAtom> atomList = mol.getAtoms();		
		for(CMLAtom atom : atomList) {
			ChemicalElement element = atom.getChemicalElement();
			if (element.isChemicalElementType(Type.METAL)) {
				metalAtomList.add(atom);
				List<CMLBond> bonds = atom.getLigandBonds();
				for (CMLBond bond : bonds) {
					metalBondList.add(bond);
				}
				// tag atoms bonded to metals as being so.  This can then
				// be used later to figure out charges and bond orders
				List<CMLAtom> ligands = atom.getLigandAtoms();
				for (CMLAtom ligand : ligands) {
					CMLScalar metalLigand = new CMLScalar();
					ligand.appendChild(metalLigand);
					metalLigand.addAttribute(new Attribute("dictRef", ValencyTool.metalLigandDictRef));
				}
			}
		}		
		for (CMLAtom metalAtom : metalAtomList)	 {
			metalAtom.detach();
		}
		// remove duplicates from metal bond list
		Set<CMLBond> set = new HashSet<CMLBond>();
		set.addAll(metalBondList);
		if(set.size() < metalBondList.size()) {
			metalBondList.clear();
			metalBondList.addAll(set);
		} 
		for (CMLBond metalBond : metalBondList)	 {
			metalBond.detach();
		}
		Map<List<CMLAtom>, List<CMLBond>> map = new HashMap<List<CMLAtom>, List<CMLBond>>(1);
		map.put(metalAtomList, metalBondList);
		return map;
	}

	/** add atoms.
	 * 
	 * @param mol
	 * @param metalAtomAndBondMap
	 */
	public static void addMetalAtomsAndBonds(CMLMolecule mol, Map<List<CMLAtom>, List<CMLBond>> metalAtomAndBondMap) {
		Entry<List<CMLAtom>, List<CMLBond>> entry = metalAtomAndBondMap.entrySet().iterator().next();
		CMLAtomArray atomArray = mol.getAtomArray();
		for (CMLAtom atom : entry.getKey()) {
			atomArray.appendChild(atom);
		}
		CMLBondArray bondArray = mol.getBondArray();
		if (bondArray != null) {
			bondArray.indexBonds();
		}
		for (CMLBond bond : entry.getValue()) {
			mol.addBond(bond);
		}
		// remove scalars signifying atoms attached to metal
		Nodes nodes = mol.query(".//"+CMLScalar.NS+"[@dictRef='"+metalLigandDictRef+"']", CML_XPATH);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	/** is bonded to metal.
	 * @return is bonded
	 */
	public boolean isBondedToMetal(CMLAtom atom) {
		Nodes nodes = atom.query(".//"+CMLScalar.NS+"[@dictRef='"+ValencyTool.metalLigandDictRef+"']", CMLConstants.CML_XPATH);
		if (nodes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public CMLMolecule getMolecule() {
		return this.molecule;
	}

	private class HeteroAtomManager implements CMLConstants {

		// instance variables set in constructor
		ValencyTool valencyTool;
		PiSystemControls piSystemManager;
		boolean isMetalComplex;
		int knownMolCharge;
		CMLMolecule mol;
		MoleculeTool molTool;

		//variables set in manipulateHeteroAtoms
		List<String> commonGroupMarkedupAtomIds;
		List<CMLAtom> commonGroupMarkedupAtoms;
		boolean hasPiElectrons = false;
		List<CMLAtom> molAtomList;
		List<CMLBond> resettableBonds;
		List<CMLMolecule> validMolList;

		//variables set in populateHeteroAtomLists
		List<CMLAtom> n3List;
		List<CMLAtom> osList;
		List<CMLAtom> n2List;
		List<List<Integer>> n3ComboList;
		List<List<Integer>> osComboList;
		List<List<Integer>> n2ComboList;

		public HeteroAtomManager(ValencyTool valencyTool, PiSystemControls piSystemManager, boolean isMetalComplex, int knownMolCharge ) {
			this.valencyTool = valencyTool;
			this.mol = valencyTool.getMolecule();
			this.molTool = MoleculeTool.getOrCreateTool(mol);
			this.piSystemManager = piSystemManager;
			this.isMetalComplex = isMetalComplex;
			this.knownMolCharge = knownMolCharge;
		}

		private List<CMLAtom> getMolAtomList() {
			if (molAtomList == null) {
				molAtomList = mol.getAtoms();
			}
			return molAtomList;
		}

		private boolean manipulateHeteroatoms() {
			boolean success = true;
			ValencyTool valencyTool = new ValencyTool(mol);
			valencyTool.markupCommonGroups();
			commonGroupMarkedupAtoms = valencyTool.getCommonGroupMarkedupAtoms();
			commonGroupMarkedupAtomIds = new ArrayList<String>();
			for (CMLAtom alreadySetAtom : commonGroupMarkedupAtoms) {
				commonGroupMarkedupAtomIds.add(alreadySetAtom.getId());
			}
			mol.setNormalizedBondOrders();
			// get list of bonds that have not been set by 
			// markupSpecial or markupCommonMolecules
			// do this so these are the only bonds that are
			// reset during iteration further down the method
			resettableBonds = new ArrayList<CMLBond>();
			for (CMLBond bond : mol.getBonds()) {
				if (CMLBond.SINGLE.equals(bond.getOrder())) {
					resettableBonds.add(bond);
				}
			}

			populateHeteroAtomLists();

			// if there are too many heteroatoms, then stop, as it will take too long
			if (n3List.size() + osList.size() + n2List.size() > 15) {
				return false;
			}

			// try and find a set of bonds and charges first by trying different combinations of charges on the heteroatoms,
			// and then if that fails, try also looking at charges on carbons.
			findBondsAndChargesByManipulatingHeteroatomCharges();
			if (validMolList.size() == 0  && knownMolCharge != UNKNOWN_CHARGE) {
				findBondsAndChargesByManipulatingHeteroatomAndCarbonCharges();
			}

			if (validMolList.size() > 0) {
				addBondsAndChargesToStartingMolecule();			
			} else {
				if (hasPiElectrons) {
					success = false;
				}
			}
			return success;
		}

		private void findBondsAndChargesByManipulatingHeteroatomCharges() {
			validMolList = new ArrayList<CMLMolecule>();
			for (int l = 0; l < n2ComboList.size(); l++) {
				for (int i = 0; i < n3ComboList.size(); i++) {
					for (int j = osComboList.size()-1; j >= 0; j--) {
						List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
						setHeteroatoms(i, j, l, chargedAtoms);

						// if the charge is known and the charges set on the atoms don't
						// add up to it, then don't try to calculate the bonds and move on
						// to the next combination
						int fCharge = mol.calculateFormalCharge();
						if (knownMolCharge != UNKNOWN_CHARGE && knownMolCharge != fCharge) {
							resetMolecule(chargedAtoms);
							continue;
						}

						boolean finished = tryPiSystem(3);
						if (!finished) {
							resetMolecule(chargedAtoms);
						} else {
							return;
						}
					}
				}
			}
		}

		private void findBondsAndChargesByManipulatingHeteroatomAndCarbonCharges() {
			for (int l = 0; l < n2ComboList.size(); l++) {
				for (int i = 0; i < n3ComboList.size(); i++) {
					for (int j = osComboList.size()-1; j >= 0; j--) {
						List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
						setHeteroatoms(i, j, l, chargedAtoms);

						// if the charge is known and is within + or - 1 of the charge set on
						// the atoms, then add a +/- to carbon atoms until a valid structure
						// is found
						int fCharge = mol.calculateFormalCharge();
						if (fCharge-1 == knownMolCharge) {
							manipulateCarbons(-1, chargedAtoms);
						} else if (fCharge+1 == knownMolCharge) {
							manipulateCarbons(1, chargedAtoms);
						} else {
							resetMolecule(chargedAtoms);
							continue;
						}
					}
				}
			}
		}

		private boolean tryPiSystem(int numValidMolsToFindBeforeStopping) {
			PiSystem newPiS = new PiSystem(getMolAtomList());
			newPiS.setPiSystemManager(piSystemManager);
			List<PiSystem> newPiSList = newPiS.generatePiSystemList();
			int sysCount = 0;
			boolean piRemaining = false;
			for (PiSystem system : newPiSList) {
				sysCount++;
				system.identifyDoubleBonds();
				if (hasUnassignedElectrons(system)) {
					piRemaining = true;
				}
				if (sysCount == newPiSList.size()) {
					if (!piRemaining) {
						addToValidMolList();
						if (validMolList.size() >= numValidMolsToFindBeforeStopping) {
							return true;
						}
					}
				}
			}
			return false;
		}

		private void manipulateCarbons(int carbonCharge, List<CMLAtom> chargedAtoms) {
			for (CMLAtom ato : mol.getAtoms()) {
				if ("C".equals(ato.getElementType())) {
					ato.setFormalCharge(carbonCharge);
					chargedAtoms.add(ato);
					boolean finished = tryPiSystem(1);
					if (!finished) {
						resetMolecule(chargedAtoms);
					} else {
						return;
					}
				}
			}
		}

		private boolean hasUnassignedElectrons(PiSystem system) {
			for (CMLAtom a : system.getAtomList()) {
				Nodes nodes = a.query(".//"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", CML_XPATH);
				if (nodes.size() > 0) {
					return true;
				}
			}
			return false;
		}

		private void setHeteroatoms(int i, int j, int l, List<CMLAtom> chargedAtoms) {
			for (Integer in : n3ComboList.get(i)) {
				CMLAtom atom = n3List.get(in);
				atom.setFormalCharge(1);
				chargedAtoms.add(atom);
			}
			for (Integer in : osComboList.get(j)) {
				CMLAtom atom = osList.get(in);
				atom.setFormalCharge(-1);
				chargedAtoms.add(atom);
			}
			for (Integer in : n2ComboList.get(l)) {
				CMLAtom atom = n2List.get(in);
				atom.setFormalCharge(-1);
				chargedAtoms.add(atom);
			}
		}

		private void addToValidMolList() {
			boolean add = true;
			List<CMLMolecule> tempMolList = new ArrayList<CMLMolecule>(validMolList);
			for (CMLMolecule m : tempMolList) {
				MoleculeTool mTool = MoleculeTool.getOrCreateTool(m);
				if (molTool.getFormalCharge() == mTool.getFormalCharge()) {
					if (molTool.getChargedAtoms().size() <= mTool.getChargedAtoms().size()) {
						validMolList.remove(m);
					} else {
						add = false;
					}
				}
			}
			if (add) {
				CMLMolecule copy = (CMLMolecule)mol.copy();
				validMolList.add(copy);
			}
		}

		private void resetMolecule(List<CMLAtom> chargedAtoms) {
			// reset charges on charged atoms
			for (CMLAtom atom : chargedAtoms) {
				if (!commonGroupMarkedupAtoms.contains(atom)) {
					atom.setFormalCharge(0);
				}
			}
			// reset only those bonds that were single to start with
			for (CMLBond bond : resettableBonds) {
				bond.setOrder(CMLBond.SINGLE);
			}
			// reset all pi-electrons
			Nodes piElectrons = mol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", CML_XPATH);
			for (int e = 0; e < piElectrons.size(); e++) {
				((CMLElectron)piElectrons.get(e)).detach();
			}
		}

		private void populateHeteroAtomLists() {
			List<CMLAtom> atomList = mol.getAtoms();
			PiSystem piS = new PiSystem(atomList);
			piS.setPiSystemManager(piSystemManager);
			List<PiSystem> piSList = piS.generatePiSystemList();

			n3List = new ArrayList<CMLAtom>();
			osList = new ArrayList<CMLAtom>();
			n2List = new ArrayList<CMLAtom>();

			if (piSList.size() > 0) {
				this.hasPiElectrons = true;
			}
			for (PiSystem piSys : piSList) {
				if (piSys.getSize() == 1) {
					CMLAtom piAtom = piSys.getAtomList().get(0);
					String atomElType = piAtom.getElementType();
					if ("O".equals(atomElType) || 
							"S".equals(atomElType)) {
						piAtom.setFormalCharge(-1);
						commonGroupMarkedupAtomIds.add(piAtom.getId());
					} else if ("N".equals(atomElType)) {
						int ligandNum = piAtom.getLigandBonds().size();
						piAtom.setFormalCharge(-3+ligandNum);
						commonGroupMarkedupAtomIds.add(piAtom.getId());
					}
				} else {
					List<CMLAtom> piAtomList = piSys.getAtomList();
					for (CMLAtom atom : piAtomList) {
						// don't want to include atoms that have already had formal charge set
						// by markupCommonMolecules or markupCommonGroups
						if (!commonGroupMarkedupAtomIds.contains(atom.getId())) {
							if ("O".equals(atom.getElementType()) || "S".equals(atom.getElementType())) {
								if (atom.getLigandAtoms().size() == 1) {
									osList.add(atom);
								}
							}
							if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 2
									&& isBondedToMetal(atom)) {
								int count = 0;
								for (CMLAtom ligand : atom.getLigandAtoms()) {
									if ("H".equals(ligand.getElementType())) {
										count++;
									}
								}
								if (count == 0) {
									n2List.add(atom);
								}
							}
							// see if there are any Ns with 3 ligands next to the pi-system that
							// may be positively charged
							for (CMLAtom ligand : atom.getLigandAtoms()) {
								if ("N".equals(ligand.getElementType())) {
									if (ligand.getLigandAtoms().size() == 3) {
										int count = 0;
										for (CMLAtom ligLig : ligand.getLigandAtoms()) {
											if ("H".equals(ligLig.getElementType())) {
												count++;
											}
										}
										if (count < 2 && !n3List.contains(ligand)) {
											n3List.add(ligand);
										}
									}
								}
							}
						}
					}
				}
			}
			// take all combinations of charges on the atoms found and attempt to 
			// get a completed pi-system.
			n3ComboList = CMLUtil.generateCombinationList(n3List.size());
			osComboList = CMLUtil.generateCombinationList(osList.size());
			n2ComboList = CMLUtil.generateCombinationList(n2List.size());
		}

		private void addBondsAndChargesToStartingMolecule() {
			// remember that molCharge is the charge given to the molecule from the CIF file
			CMLMolecule theMol = null;
			if (knownMolCharge != UNKNOWN_CHARGE && !isMetalComplex) {
				for (CMLMolecule n : validMolList) {
					if (knownMolCharge == MoleculeTool.getOrCreateTool(n).calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS).getFormalCharge()) {
						theMol = n;
					}
				}

			} 
			// if theMol not set above OR part of metal complex OR no corresponding formal charge
			if (theMol == null) {
				if (isMetalComplex) {
					int count = 0;
					int currentCharge2 = 0;
					for (CMLMolecule n : validMolList) {
						MoleculeTool nTool = MoleculeTool.getOrCreateTool(n);
						if (count == 0) {
							theMol = n;
							currentCharge2 = nTool.getFormalCharge();
						} else {
							int nCharge = nTool.getFormalCharge();
							if (currentCharge2 < 0) {
								if (nCharge > currentCharge2 && nCharge <= 0) {
									currentCharge2 = nCharge;
									theMol = n;
								}
							}
							if (currentCharge2 >= 0) {
								if (nCharge < currentCharge2) {
									currentCharge2 = nCharge;
									theMol = n;
								}
							}
						}
						count++;
					}
				} else {
					int count = 0;
					int currentCharge2 = 0;
					for (CMLMolecule n : validMolList) {
						MoleculeTool nTool = MoleculeTool.getOrCreateTool(n);
						if (count == 0) {
							theMol = n;
							currentCharge2 = (int) Math.pow(nTool.getFormalCharge(), 2);
						} else {
							int nCharge = (int)Math.pow(nTool.getFormalCharge(), 2);
							if (nCharge < currentCharge2) {
								currentCharge2 = nCharge;
								theMol = n;
							}
						}
						count++;
					}
				}
			}
			MoleculeTool theMolTool = MoleculeTool.getOrCreateTool(theMol);
			theMolTool.copyAtomAndBondAttributesById(mol, true);
		}

	}

}
