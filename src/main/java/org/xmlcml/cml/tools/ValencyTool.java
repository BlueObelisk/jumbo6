package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;

/**
 * @author pm286
 *
 */

public class ValencyTool extends AbstractTool {
	/** */
	public final static int UNKNOWN_CHARGE = 99999;

	private boolean isMetalComplex = false;
	private CMLMolecule molecule;
	private MoleculeTool moleculeTool;
	private String formulaS;

	private List<CMLAtom> alreadySetAtoms = new ArrayList<CMLAtom>();

	/** constructor
	 * @param molecule
	 */
	public ValencyTool(CMLMolecule molecule) {
		this.molecule = molecule;
		moleculeTool = new MoleculeTool(molecule);
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
		boolean marked = true;
		// SiF6
		if (formulaS.equals("F 6 Si 1")) {
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
	 * special routines. mark common groups with charges and 
	 * bond orders
	 * 
	 */
	public void markupSpecial() {
		List<CMLAtom> atoms = molecule.getAtoms();
		int count = atoms.size();
		if (count == 2) {
			this.markMetalCarbonylAndNitrile(atoms);
		}
		if (count == 3) {
			this.markThiocyanate(atoms);
			this.markNCN(atoms);
		}
		if (formulaS.equals("N 3")) {
			this.markN3(atoms);
		}
		if (formulaS.equals("C 2 N 3")) {
			this.markNCNCN(atoms);
		}
		if (formulaS.equals("N 1 O 1")) {
			this.markNO(atoms);
		}
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
				int bos = moleculeTool.getBondOrderSum(atom);
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType()) && atom.isBondedToMetal()
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("O".equals(atom.getElementType()) && !atom.isBondedToMetal()
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType())) {
				this.setAtomCharge(atom, -1);
				CMLBond bond = atom.getLigandBonds().get(0);
				this.setBondOrder(bond, CMLBond.DOUBLE);
			}
		}
	}

	private void markHydride(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
			if ("H".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 0 
					&& atom.isBondedToMetal()) {
				this.setAtomCharge(atom, -1);
			}
		}
	}

	private void markEarthMetals(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
			if (atom.isBondedToMetal() && "C".equals(atom.getElementType()) 
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
			if (alreadySetAtoms.contains(atom)) continue;
			if (atom.isBondedToMetal() && "N".equals(atom.getElementType()) 
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
			if (atom.isBondedToMetal() && "C".equals(atom.getElementType()) 
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
			if (alreadySetAtoms.contains(atom)) continue;
			if (atom.isBondedToMetal() && ("N".equals(atom.getElementType()) || "P".equals(atom.getElementType())) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					this.setAtomCharge(atom, -2);
				}
			}
		}
	}

	private void markSandwichLigands(List<CMLAtom> atoms) {
		CMLAtomSet atomSet = new CMLAtomSet(atoms);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		CMLMolecule mol = new CMLMolecule(atomSet, bondSet);
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
				if (atom.isBondedToMetal()) {
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
		CMLAtomSet atomSet = new CMLAtomSet(atoms);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		CMLMolecule mol = new CMLMolecule(atomSet, bondSet);
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
					CMLMolecule sprout = new MoleculeTool(mol).sprout(ringSet);
					if (sprout.getAtomCount() == 11) {
						this.setAtomCharge(nAtom, 0);
					}
				}
			}
		}
	}

	private void markAzideGroup(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
			if (("B".equals(atom.getElementType()) ||
					"Al".equals(atom.getElementType()) ||
					"Ga".equals(atom.getElementType()) ||
					"In".equals(atom.getElementType())) &&
					atom.getLigandAtoms().size() == 4) {
				this.setAtomCharge(atom, -1);
			}
		}
	}

	private void markNCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
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
					}
				}
			}
		}
	}

	private void markN3(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3) {
					List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
					for (CMLAtom ligand : ligands) {
						if ("O".equals(ligand.getElementType())
								&& moleculeTool.getBondOrderSum(ligand) == 1) {
							oxyList.add(ligand);
						}
					}
					marker:
						if (oxyList.size() == 2) {
							for (CMLAtom oAtom : oxyList) {
								if (oAtom.isBondedToMetal()) {
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
	private void markMetalCarbonylAndNitrile(List<CMLAtom> atoms) {
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
					} else if ("N".equals(ligands.get(0).getElementType())) {
						CMLBond bond = atom.getLigandBonds().get(0);
						this.setBondOrder(bond, CMLBond.TRIPLE);
						this.setAtomCharge(atom, -1);
					}
				}
			}
		}
	}

	private void markTerminalCarbyne(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				if (atom.isBondedToMetal()) {
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType()) || "Si".equals(atom.getElementType())) {
				int hCount = 0;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3 && atom.isBondedToMetal()) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if ("H".equals(ligand.getElementType())) {
							hCount++;
						}
						if (ligand.isBondedToMetal()) {
							set = false;
						}
					}
					if (set && hCount == 2) {
						this.setAtomCharge(atom, -1);
					}
				}
				// if carbon has only two ligands then check to see if it
				// can be marked as a carbanion.
				if (ligands.size() == 2  && atom.isBondedToMetal()) {
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("N".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
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
			if (alreadySetAtoms.contains(atom)) continue;
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("S".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				int ligandCount = ligands.size();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> sulfoList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("S".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
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
			if (alreadySetAtoms.contains(atom)) continue;
			if ("C".equals(atom.getElementType())) {
				CMLAtom o = null;
				CMLAtom s = null;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				for (CMLAtom ligand : ligands) {
					if ("S".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
						s = ligand;
					}
					if ("O".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
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


	private void markThiocyanate(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (alreadySetAtoms.contains(atom)) continue;
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
				}
			}
		}
	}

	/**
	 * Adjust bond orders and charges to satisfy valence. empirical, and
	 * containing several special cases:
	 * <ul>
	 * <li>identifying N+</li>
	 * <li>identifying O-</li>
	 * <li>X=O and X-O(-) bonds</li>
	 * </ul>
	 * then adds double bonds to pi-systems in impossible systems appropriate
	 * atoms are marked as having piElectron childrens assumes explicit
	 * hydrogens
	 * 
	 * @TODO refactor me!
	 * @param piSystemManager
	 * @param knownMolCharge charge on molecule provided
	 * @return obsolete
	 */
	public boolean adjustBondOrdersAndChargesToValency(
			PiSystemControls piSystemManager, int knownMolCharge) {
		boolean success = true;

		List<CMLMolecule> mols = molecule.getDescendantsOrMolecule();
		for (CMLMolecule mol : mols) {
			// reset all bond orders to single
			mol.setBondOrders(CMLBond.SINGLE);
			ValencyTool vTool = new ValencyTool(mol);
			List<CMLAtom> molAtoms = mol.getAtoms();
			vTool.markMetalCarbons(molAtoms);
			vTool.markEarthMetals(molAtoms);
			// remove metal atoms so we can calculate bond orders on organic species, 
			// then we will reattach the metal atoms later in the method
			Map<List<CMLAtom>, List<CMLBond>> metalAtomAndBondMap = MoleculeTool.removeMetalAtomsAndBonds(molecule, true);
			for (Iterator it=metalAtomAndBondMap.keySet().iterator(); it.hasNext(); ) {
				List<CMLAtom> atomList = (List<CMLAtom>)it.next();
				if (atomList.size() > 0) {
					isMetalComplex = true;
					// as we can't take metals into account when calculating bonds and charges, then
					// the supplid molecular charge is useless.
					knownMolCharge = UNKNOWN_CHARGE;
				}
			}
			// if the removal of metal atoms takes the molecules atom count 
			// to zero or one then don't bother calculating bonds
			// now the metal atoms and bonds have been removed, partition 
			// molecule into submolecules and then calculate the bond orders
			// and charges
			if (isMetalComplex) {
				ConnectionTableTool ctt = new ConnectionTableTool(mol);
				ctt.partitionIntoMolecules();
			}
			if (mol.getAtomCount() != 0 ) {
				List<CMLMolecule> subMols = mol.getDescendantsOrMolecule();
				for (CMLMolecule subMol : subMols) {	
					if (success) {
						// if the removal of metal atoms takes the molecules atom count 
						// to zero or one then don't bother calculating bonds
						if (subMol.getAtomCount() == 1) {
							ValencyTool valencyTool = new ValencyTool(mol);
							valencyTool.markLoneNonMetalAnions(mol.getAtoms());
						} else {
							MoleculeTool subMolTool = new MoleculeTool(subMol);
							ValencyTool valencyTool = new ValencyTool(subMol);
							boolean common = valencyTool.markupCommonMolecules();
							if (!common) {
								valencyTool.markupSpecial();
								List<CMLAtom> alreadySetAtoms = valencyTool.getAlreadySetAtoms();
								List<String> alreadySetAtomIds = new ArrayList<String>();
								for (CMLAtom alreadySetAtom : alreadySetAtoms) {
									alreadySetAtomIds.add(alreadySetAtom.getId());
								}
								subMol.setNormalizedBondOrders();
								// get list of bonds that have not been set by 
								// markupSpecial or markupCommonMolecules
								// do this so these are are the only bonds that are
								// reset during iteration further down the method
								List<CMLBond> singleBonds = new ArrayList<CMLBond>();
								for (CMLBond bond : subMol.getBonds()) {
									if (CMLBond.SINGLE.equals(bond.getOrder())) {
										singleBonds.add(bond);
									}
								}
								List<CMLAtom> subMolAtomList = subMol.getAtoms();
								PiSystem piS = new PiSystem(subMolAtomList);
								piS.setPiSystemManager(piSystemManager);
								List<PiSystem> piSList = piS.generatePiSystemList();
								List<CMLAtom> n3List = new ArrayList<CMLAtom>();
								List<CMLAtom> osList = new ArrayList<CMLAtom>();
								List<CMLAtom> n2List = new ArrayList<CMLAtom>();
								boolean hasPiElectrons = false;
								if (piSList.size() > 0) {
									hasPiElectrons = true;
								}
								for (PiSystem piSys : piSList) {
									if (piSys.getSize() == 1) {
										CMLAtom piAtom = piSys.getAtomList().get(0);
										String atomElType = piAtom.getElementType();
										if ("O".equals(atomElType) || 
												"S".equals(atomElType)) {
											piAtom.setFormalCharge(-1);
											alreadySetAtomIds.add(piAtom.getId());
										} else if ("N".equals(atomElType)) {
											int ligandNum = piAtom.getLigandBonds().size();
											piAtom.setFormalCharge(-3+ligandNum);
											alreadySetAtomIds.add(piAtom.getId());
										}
									} else {
										List<CMLAtom> piAtomList = piSys.getAtomList();
										for (CMLAtom atom : piAtomList) {
											// don't want to include atoms that have already had formal charge set
											// by markupCommonMolecules or markupSpecial
											if (!alreadySetAtomIds.contains(atom.getId())) {
												if ("O".equals(atom.getElementType()) || "S".equals(atom.getElementType())) {
													if (atom.getLigandAtoms().size() == 1) {
														osList.add(atom);
													}
												}
												if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 2
														&& atom.isBondedToMetal()) {
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
								List<List<Integer>> n3ComboList = CMLUtil.generateCombinationList(n3List.size());
								List<List<Integer>> osComboList = CMLUtil.generateCombinationList(osList.size());
								List<List<Integer>> n2ComboList = CMLUtil.generateCombinationList(n2List.size());
								List<CMLMolecule> validMolList = new ArrayList<CMLMolecule>();
								first: 
									for (int l = 0; l < n2ComboList.size(); l++) {
										for (int i = 0; i < n3ComboList.size(); i++) {
											for (int j = osComboList.size()-1; j >= 0; j--) {
												List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
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

												// if the charge is known and the charges set on the atoms don't
												// add up to it, then don't try to calculate the bonds and move on
												// to the next combination
												int fCharge = subMol.calculateFormalCharge();
												if (knownMolCharge != UNKNOWN_CHARGE && knownMolCharge != fCharge) {
													// reset charges on charged atoms
													for (CMLAtom atom : chargedAtoms) {
														if (!alreadySetAtoms.contains(atom)) {
															atom.setFormalCharge(0);
														}
													}
													// reset only those bonds that were single to start with
													for (CMLBond bond : singleBonds) {
														bond.setOrder(CMLBond.SINGLE);
													}
													// reset all pi-electrons
													Nodes piElectrons = subMol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
													for (int e = 0; e < piElectrons.size(); e++) {
														((CMLElectron)piElectrons.get(e)).detach();
													}
													continue;
												}


												PiSystem newPiS = new PiSystem(subMolAtomList);
												newPiS.setPiSystemManager(piSystemManager);
												List<PiSystem> newPiSList = newPiS.generatePiSystemList();
												int sysCount = 0;
												boolean piRemaining = false;
												for (PiSystem system : newPiSList) {
													sysCount++;
													system.identifyDoubleBonds();
													for (CMLAtom a : system.getAtomList()) {
														Nodes nodes = a.query(".//"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
														if (nodes.size() > 0) {
															piRemaining = true;
														}
													}
													if (sysCount == newPiSList.size()) {
														// when a valid pi-system found, check whether a molecule with the same
														// overall charge has already been found.  If so, take the system with 
														// the fewest charged atoms.
														if (!piRemaining) {
															boolean add = true;
															List<CMLMolecule> tempMolList = new ArrayList<CMLMolecule>(validMolList);
															for (CMLMolecule m : tempMolList) {
																MoleculeTool mTool = new MoleculeTool(m);
																if (subMolTool.getFormalCharge() == mTool.getFormalCharge()) {
																	if (subMolTool.getChargedAtoms().size() <= mTool.getChargedAtoms().size()) {
																		validMolList.remove(m);
																	} else {
																		add = false;
																	}
																}
															}
															if (add) {
																CMLMolecule copy = (CMLMolecule)subMol.copy();
																validMolList.add(copy);
																if (validMolList.size() > 2) {
																	break first;
																}
															}
														}
													}
												}
												// reset charges on charged atoms
												for (CMLAtom atom : chargedAtoms) {
													if (!alreadySetAtoms.contains(atom)) {
														atom.setFormalCharge(0);
													}
												}
												// reset only those bonds that were single to start with
												for (CMLBond bond : singleBonds) {
													bond.setOrder(CMLBond.SINGLE);
												}
												// reset all pi-electrons
												Nodes piElectrons = subMol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
												for (int e = 0; e < piElectrons.size(); e++) {
													((CMLElectron)piElectrons.get(e)).detach();
												}
											}
										}
									}
								cAttempt:
									if (validMolList.size() == 0  && knownMolCharge != UNKNOWN_CHARGE) {
										for (int l = 0; l < n2ComboList.size(); l++) {
											for (int i = 0; i < n3ComboList.size(); i++) {
												for (int j = osComboList.size()-1; j >= 0; j--) {
													List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
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

													// if the charge is known and is within + or - 1 of the charge set on
													// the atoms, then add a +/- to carbon atoms until a valid structure
													// is found
													int fCharge = subMol.calculateFormalCharge();
													if (fCharge-1 == knownMolCharge) {
														for (CMLAtom ato : subMol.getAtoms()) {
															if ("C".equals(ato.getElementType())) {
																ato.setFormalCharge(-1);
																chargedAtoms.add(ato);
																PiSystem newPiS = new PiSystem(subMolAtomList);
																newPiS.setPiSystemManager(piSystemManager);
																List<PiSystem> newPiSList = newPiS.generatePiSystemList();
																int sysCount = 0;
																boolean piRemaining = false;
																for (PiSystem system : newPiSList) {
																	sysCount++;
																	system.identifyDoubleBonds();
																	for (CMLAtom a : system.getAtomList()) {
																		Nodes nodes = a.query(".//"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
																		if (nodes.size() > 0) {
																			piRemaining = true;
																		}
																	}
																	if (sysCount == newPiSList.size()) {
																		// when a valid pi-system found, check whether a molecule with the same
																		// overall charge has already been found.  If so, take the system with 
																		// the fewest charged atoms.
																		if (!piRemaining) {
																			boolean add = true;
																			List<CMLMolecule> tempMolList = new ArrayList<CMLMolecule>(validMolList);
																			for (CMLMolecule m : tempMolList) {
																				MoleculeTool mTool = new MoleculeTool(m);
																				if (subMolTool.getFormalCharge() == mTool.getFormalCharge()) {
																					if (subMolTool.getChargedAtoms().size() <= mTool.getChargedAtoms().size()) {
																						validMolList.remove(m);
																					} else {
																						add = false;
																					}
																				}
																			}
																			if (add) {
																				CMLMolecule copy = (CMLMolecule)subMol.copy();
																				validMolList.add(copy);
																				break cAttempt;
																			}
																		}
																	}
																}
																// reset charges on charged atoms
																for (CMLAtom atom : chargedAtoms) {
																	if (!alreadySetAtoms.contains(atom)) {
																		atom.setFormalCharge(0);
																	}
																}
																// reset only those bonds that were single to start with
																for (CMLBond bond : singleBonds) {
																	bond.setOrder(CMLBond.SINGLE);
																}
																// reset all pi-electrons
																Nodes piElectrons = subMol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
																for (int e = 0; e < piElectrons.size(); e++) {
																	((CMLElectron)piElectrons.get(e)).detach();
																}
															}
														}
													} else if (fCharge+1 == knownMolCharge) {
														for (CMLAtom ato : subMol.getAtoms()) {
															if ("C".equals(ato.getElementType())) {													
																ato.setFormalCharge(1);
																chargedAtoms.add(ato);

																PiSystem newPiS = new PiSystem(subMolAtomList);
																newPiS.setPiSystemManager(piSystemManager);
																List<PiSystem> newPiSList = newPiS.generatePiSystemList();
																int sysCount = 0;
																boolean piRemaining = false;
																for (PiSystem system : newPiSList) {
																	sysCount++;
																	system.identifyDoubleBonds();
																	for (CMLAtom a : system.getAtomList()) {
																		Nodes nodes = a.query(".//"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
																		if (nodes.size() > 0) {
																			piRemaining = true;
																		}
																	}
																	if (sysCount == newPiSList.size()) {
																		// when a valid pi-system found, check whether a molecule with the same
																		// overall charge has already been found.  If so, take the system with 
																		// the fewest charged atoms.
																		if (!piRemaining) {
																			boolean add = true;
																			List<CMLMolecule> tempMolList = new ArrayList<CMLMolecule>(validMolList);
																			for (CMLMolecule m : tempMolList) {
																				MoleculeTool mTool = new MoleculeTool(m);
																				if (subMolTool.getFormalCharge() == mTool.getFormalCharge()) {
																					if (subMolTool.getChargedAtoms().size() <= mTool.getChargedAtoms().size()) {
																						validMolList.remove(m);
																					} else {
																						add = false;
																					}
																				}
																			}
																			if (add) {
																				CMLMolecule copy = (CMLMolecule)subMol.copy();
																				validMolList.add(copy);
																				break cAttempt;
																			}
																		}
																	}
																}
																// reset charges on charged atoms
																for (CMLAtom atom : chargedAtoms) {
																	if (!alreadySetAtoms.contains(atom)) {
																		atom.setFormalCharge(0);
																	}
																}
																// reset only those bonds that were single to start with
																for (CMLBond bond : singleBonds) {
																	bond.setOrder(CMLBond.SINGLE);
																}
																// reset all pi-electrons
																Nodes piElectrons = subMol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
																for (int e = 0; e < piElectrons.size(); e++) {
																	((CMLElectron)piElectrons.get(e)).detach();
																}
															}
														}
													} else {
														// reset charges on charged atoms
														for (CMLAtom atom : chargedAtoms) {
															if (!alreadySetAtoms.contains(atom)) {
																atom.setFormalCharge(0);
															}
														}
														// reset only those bonds that were single to start with
														for (CMLBond bond : singleBonds) {
															bond.setOrder(CMLBond.SINGLE);
														}
														// reset all pi-electrons
														Nodes piElectrons = subMol.query(".//"+CMLAtom.NS+"/"+CMLElectron.NS+"[@dictRef='"+CMLElectron.PI+"']", X_CML);
														for (int e = 0; e < piElectrons.size(); e++) {
															((CMLElectron)piElectrons.get(e)).detach();
														}
														continue;
													}
												}
											}
										}
									}
									if (validMolList.size() > 0) {
										// remember that molCharge is the charge given to the molecule from the CIF file
										CMLMolecule theMol = null;
										if (knownMolCharge != UNKNOWN_CHARGE && !isMetalComplex) {
											for (CMLMolecule n : validMolList) {
												if (knownMolCharge == n.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS).getFormalCharge()) {
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
													MoleculeTool nTool = new MoleculeTool(n);
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
													MoleculeTool nTool = new MoleculeTool(n);
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
										MoleculeTool theMolTool = new MoleculeTool(theMol);
										theMolTool.copyAtomAndBondAttributesById(subMol, true);
									} else {
										if (hasPiElectrons) {
											success = false;
										}
									}
							}
						}
					}
				}
			}
			// return the mol to its original state before adding metal
			// atoms and bonds back in
			if (isMetalComplex) {
				ConnectionTableTool ctt = new ConnectionTableTool(mol);
				ctt.flattenMolecules();
			}
			// reattach metal atoms and bonds now bonds have been calculated
			MoleculeTool.addMetalAtomsAndBonds(mol, metalAtomAndBondMap);
		}
		if (!success) {
			// if couldn't find a reasonable structure, then reset bond orders and formal charges
			for (CMLAtom atom : molecule.getAtoms()) {
				atom.setFormalCharge(0);
			}
			for (CMLBond bond : molecule.getBonds()) {
				bond.setOrder(CMLBond.SINGLE);
			}
		}
		return success;
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
		alreadySetAtoms.add(atom);
	}

	private void setBondOrder(CMLBond bond, String order) {
		bond.setOrder(order);
		// add to alreadySetAtoms
		for (CMLAtom at : bond.getAtoms()) {
			alreadySetAtoms.add(at);
		}
	}

	/**
	 * @return list of atoms
	 */
	public List<CMLAtom> getAlreadySetAtoms() {
		return this.alreadySetAtoms;
	}
}
