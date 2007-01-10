package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;

public class ValencyTool extends AbstractTool {
	
	public static String metalLigandDictRef = "jumbo:metalLigand";
	public final static int UNREALISTIC_CHARGE = 99999;
	
	private boolean isMetalComplex = false;
	private CMLMolecule molecule;
	private MoleculeTool moleculeTool;
	private String formulaS;
	
	private List<CMLAtom> alreadySetAtoms = new ArrayList<CMLAtom>();
	
	public ValencyTool(CMLMolecule molecule) {
		this.molecule = molecule;
		moleculeTool = new MoleculeTool(molecule);
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		formulaS = formula.getConcise();
		formulaS = CMLFormula.removeChargeFromConcise(formulaS);
		molecule.addToLog(Severity.INFO, "Formula " + formulaS);
	}
	/**
	 * adds charges and bond orders for common species.
	 * 
	 * @return true if identified as common molecule
	 */
	public boolean markupCommonMolecules() {
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
			addDoubleCharge("N", -1, "O", 2);
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
		} else {
			marked = false;
		}
		return marked;
	}
	
	void addCharge(String elementType, int charge) {
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
	 * @param tool TODO
	 * 
	 */
	public void markupSpecial() {
		List<CMLAtom> atoms = molecule.getAtoms();
		this.markCarboxyAnion(atoms);
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
		this.markAzide(atoms);
		this.markM_PN_C(atoms);
		this.markMCN(atoms);
		this.markMNN(atoms);
		this.markPNP(atoms);
		this.markMCC(atoms);
		this.markSNH(atoms);
		this.markSandwichLigands(atoms);
		this.markLoneNonMetalAnions(atoms);
		this.markEarthMetals(atoms);
		int count = atoms.size();
		if (count == 2) {
			this.markMetalCarbonylAndNitrile(atoms);
		}
		if (count == 3) {
			this.markN3(atoms);
			this.markThiocyanate(atoms);
			this.markNCN(atoms);
		}
		if (formulaS.equals("C 2 N 3")) {
			this.markNCNCN(atoms);
		}
		if (formulaS.equals("N 1 O 1")) {
			this.markNO(atoms);
		}
	}

	void addDoubleCharge(String centralS, int centralCharge,
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
	
	void markNO(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("O".equals(atom.getElementType())) {
				this.setAtomCharge(atom, 1);
				CMLBond bond = atom.getLigandBonds().get(0);
				this.setBondOrder(bond, CMLBond.TRIPLE);
			}
		}
	}
	
	void markEarthMetals(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			ChemicalElement ce = atom.getChemicalElement();
			if (ce.isChemicalElementType(Type.GROUP_A)) {
				this.setAtomCharge(atom, 1);
			} else if (ce.isChemicalElementType(Type.GROUP_B)) {
				this.setAtomCharge(atom, 2);
			}
		}
	}
	
	void markLoneNonMetalAnions(List<CMLAtom> atoms) {
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
	
	void markSNH(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 2) {
				for (CMLAtom ligand : atom.getLigandAtoms()) {
					if ("S".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
						molecule.getBond(atom, ligand).setOrder(CMLBond.DOUBLE);
					}
				}
			}
		}
	}
	
	void markMCC(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	
	void markMNN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	
	void markPNP(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	
	void markMCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	
	void markM_PN_C(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (atom.isBondedToMetal() && ("N".equals(atom.getElementType()) || "P".equals(atom.getElementType())) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					this.setAtomCharge(atom, -2);
				}
			}
		}
	}
	
	void markSandwichLigands(List<CMLAtom> atoms) {
		CMLAtomSet atomSet = new CMLAtomSet(atoms);
		CMLBondSet bondSet = moleculeTool.getBondSet(atomSet);
		CMLMolecule piSysMol = new CMLMolecule(atomSet, bondSet);
		ConnectionTableTool ctool = new ConnectionTableTool(piSysMol);
		List<CMLAtomSet> ringSetList = ctool.getRingNucleiAtomSets();
		for (CMLAtomSet ringSet : ringSetList) {
			// skip if the ring contains an even number of atoms
			if (ringSet.getSize() % 2 == 0) continue;
			int count = 0;
			List<CMLAtom> atomList = ringSet.getAtoms();
			List<CMLAtom> ringAtomList = new ArrayList<CMLAtom>();
			// if there is a ring with an odd number of atoms that all
			// have pi- electrons and are connected to a metal, then
			// mark one of them as negatively charged.
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

	void markAzide(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 1) {
				CMLAtom lig = atom.getLigandAtoms().get(0);
				List<CMLAtom> ligLigands = lig.getLigandAtoms();
				if (ligLigands.size() == 2) {
					for (CMLAtom at : ligLigands) {
						if (at != atom) {
							if (at.getLigandAtoms().size() == 2) {
								this.setAtomCharge(atom, -1);
								this.setAtomCharge(lig, 1);
								CMLBond bond1 = molecule.getBond(atom, lig);
								this.setBondOrder(bond1, CMLBond.DOUBLE);
								CMLBond bond2 = molecule.getBond(lig, at);
								this.setBondOrder(bond2, CMLBond.DOUBLE);
							}
						}
					}
				}
			}
		}
	}

	void markQuaternaryNPAsSb(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	void markOSQuatP(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void markTerminalCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 1) {
				CMLAtom lig = atom.getLigandAtoms().get(0);
				if ("C".equals(lig.getElementType()) && lig.getLigandAtoms().size() == 2) {
					CMLBond bond = atom.getLigandBonds().get(0);
					this.setBondOrder(bond, CMLBond.TRIPLE);
				}
			}
		}
	}

	void markNCNCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void markQuaternaryBAlGaIn(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (("B".equals(atom.getElementType()) ||
					"Al".equals(atom.getElementType()) ||
					"Ga".equals(atom.getElementType()) ||
					"In".equals(atom.getElementType())) &&
					atom.getLigandAtoms().size() == 4) {
				this.setAtomCharge(atom, -1);
			}
		}
	}

	void markNCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void markN3(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType())) {
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
						this.setAtomCharge(atom, 1);
					}
				}		
			}
		}
	}


	/**
	 * mark O=C-O(-)
	 */
	void markCarboxyAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
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
							this.setAtomCharge(atom, -1);
							for (CMLAtom oAt : oxyList) {
								if (oAt != oAtom) {
									CMLBond bond = molecule.getBond(atom, oAt);
									this.setBondOrder(bond, CMLBond.DOUBLE);
									break marker;
								}
							}
						}
					}
					CMLAtom at = oxyList.get(0);
					this.setAtomCharge(at, -1);
					CMLBond bond = molecule.getBond(atom, oxyList.get(1));
					this.setBondOrder(bond, CMLBond.DOUBLE);
				}
			}
		}
	}

	/**
	 * mark -C-(triple bond)-O and -C-(triple bond)-N
	 */
	void markMetalCarbonylAndNitrile(List<CMLAtom> atoms) {
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

	void markTerminalCarbyne(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void mark_CSi_anion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	void markNitro(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void markPAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
	void markSulfo(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("S".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& moleculeTool.getBondOrderSum(ligand) == 1) {
						oxyList.add(ligand);
					}
				}
				if (oxyList.size() > 0) {
					this.setBondOrder(molecule.getBond(atom, oxyList.get(0)), CMLBond.DOUBLE);
				}
				if (oxyList.size() > 1) {
					this.setBondOrder(molecule.getBond(atom, oxyList.get(1)), CMLBond.DOUBLE);
				}
				if (oxyList.size() == 3) {
					this.setAtomCharge(oxyList.get(2), -1);
				}
			}
		}
	}

	/**
	 * mark S=C-S(-)
	 */
	void markCS2(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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

	void markCOS(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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


	void markThiocyanate(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
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
					this.setAtomCharge(s, -1);
					this.setBondOrder(molecule.getBond(c, s), CMLBond.DOUBLE);
					this.setBondOrder(molecule.getBond(c, n), CMLBond.DOUBLE);
				}
			}
		}
	}

//	/**
//	 * mark benzene (later pyridine, etc.) BUGGY!
//	 *
//	 */
//	// FIXME
//	@SuppressWarnings("unused")
//	private void markMonocyclicBenzene() {
//		List<CMLAtom> atoms = molecule.getAtoms();
//		Set<CMLAtom> usedAtoms = new HashSet<CMLAtom>();
//		for (CMLAtom atom : atoms) {
//			if (usedAtoms.contains(atom)) {
//				continue;
//			}
//			if ("C".equals(atom.getElementType())) {
//				if (atom.getLigandAtoms().size() == 3) {
//					Set<CMLAtom> ringSet = new HashSet<CMLAtom>();
//					while (true) {
//						if (!addNextAtom(atom, ringSet)) {
//							break;
//						}
//					}
//					if (ringSet.size() == 6) {
//						for (CMLAtom rAtom : ringSet) {
//							usedAtoms.add(rAtom);
//						}
//						addDoubleBonds(ringSet, atom);
//					}
//				}
//			}
//			usedAtoms.add(atom);
//		}
//	}
	
	private Map<List<CMLAtom>, List<CMLBond>> removeMetalAtomsAndBonds(CMLMolecule mol) {
		List<CMLAtom> metalAtomList = new ArrayList<CMLAtom>();
		List<CMLBond> metalBondList = new ArrayList<CMLBond>();
		List<CMLAtom> atomList = mol.getAtoms();		
		for(CMLAtom atom : atomList) {
			ChemicalElement element = atom.getChemicalElement();
			if (element.isChemicalElementType(Type.METAL)) {
				isMetalComplex = true;
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
					metalLigand.addAttribute(new Attribute("dictRef", metalLigandDictRef));
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
	
	private void addMetalAtomsAndBonds(CMLMolecule mol, Map<List<CMLAtom>, List<CMLBond>> metalAtomAndBondMap) {
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
		Nodes nodes = molecule.query(".//"+CMLScalar.NS+"[@dictRef='"+metalLigandDictRef+"']", X_CML);
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
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
	 * @param piSystemManager
	 */
	public void adjustBondOrdersAndChargesToValency(
			PiSystemManager piSystemManager, CMLFormula moietyFormula) {	
		// get a list of formulas for the moieties. 
		List<CMLFormula> moietyFormulaList = new ArrayList<CMLFormula>();
		if (moietyFormula != null) {
			moietyFormulaList = moietyFormula.getFormulaElements().getList();
			if (moietyFormulaList.size() == 0) {
				moietyFormulaList.add(moietyFormula);
			}
		}
		List<CMLMolecule> mols = molecule.getDescendantsOrMolecule();
		for (CMLMolecule mol : mols) {
			int molCharge = UNREALISTIC_CHARGE;
			for (CMLFormula formula : moietyFormulaList) {
				CMLFormula molForm = mol.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
				if (molForm.getConciseNoCharge().equals(formula.getConciseNoCharge())) {
					molCharge = formula.getFormalCharge();
				}
			}
			// reset all bond orders to single
			mol.setBondOrders(CMLBond.SINGLE);
			// remove metal atoms so we can calculate bond orders on organic species, 
			// then we will reattach the metal atoms later in the method
			Map<List<CMLAtom>, List<CMLBond>> metalAtomAndBondMap = this.removeMetalAtomsAndBonds(molecule);
			// if the removing of metal atoms takes the molecules atom count 
			// to zero or one then don't bother calculating bonds
			if (mol.getAtomCount() > 1) {
				// now the metal atoms and bonds have been removed, partition 
				// molecule into submolecules and then calculate the bond orders
				// and charges
				ConnectionTableTool ctt = new ConnectionTableTool(mol);
				ctt.partitionIntoMolecules();
				List<CMLMolecule> subMols = mol.getDescendantsOrMolecule();
				for (CMLMolecule subMol : subMols) {
					MoleculeTool subMolTool = new MoleculeTool(subMol);
					ValencyTool valencyTool = new ValencyTool(subMol);
					boolean common = valencyTool.markupCommonMolecules();
					if (!common) {
						valencyTool.markupSpecial();
						List<CMLAtom> alreadySetAtoms = valencyTool.getAlreadySetAtoms();
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
						for (PiSystem piSys : piSList) {
							if (piSys.getSize() == 1) {
								CMLAtom piAtom = piSys.getAtomList().get(0);
								String atomElType = piAtom.getElementType();
								if ("O".equals(atomElType) || 
										"S".equals(atomElType)) {
									piAtom.setFormalCharge(-1);
									alreadySetAtoms.add(piAtom);
								} else if ("N".equals(atomElType)) {
									int ligandNum = piAtom.getLigandBonds().size();
									piAtom.setFormalCharge(-3+ligandNum);
									alreadySetAtoms.add(piAtom);
								} else if ("C".equals(atomElType)) {
									for (CMLAtom ligand : piAtom.getLigandAtoms()) {
										if ("N".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
											ligand.setFormalCharge(+1);
											CMLBond bond = subMol.getBond(piAtom, ligand);
											bond.setOrder(CMLBond.DOUBLE);
											singleBonds.remove(bond);
											alreadySetAtoms.add(piAtom);
											alreadySetAtoms.add(ligand);
											break;
										}
									}
									piAtom.setFormalCharge(-1);
								}
							} else {
								List<CMLAtom> piAtomList = piSys.getAtomList();
								for (CMLAtom atom : piAtomList) {
									// don't want to include atoms that have already had formal charge set
									// by markupCommonMolecules or markupSpecial
									if (alreadySetAtoms.contains(atom)) {
										continue;
									}
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
						// take all combinations of charges on the atoms found and attempt to 
						// get a completed pi-system.
						List<List<Integer>> n3ComboList = CMLUtil.generateCombinationList(n3List.size());
						List<List<Integer>> osComboList = CMLUtil.generateCombinationList(osList.size());
						List<List<Integer>> n2ComboList = CMLUtil.generateCombinationList(n2List.size());
						List<CMLMolecule> validMolList = new ArrayList<CMLMolecule>();
						List<CMLMolecule> finalMolList = new ArrayList<CMLMolecule>();
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
											// the least charged atoms.
											if (!piRemaining) {
												boolean add = true;
												for (CMLMolecule m : validMolList) {
													MoleculeTool mTool = new MoleculeTool(m);
													if (subMolTool.getFormalCharge() == mTool.getFormalCharge()) {
														if (subMolTool.getChargedAtoms().size() <= mTool.getChargedAtoms().size()) {
															finalMolList.remove(m);
														} else {
															add = false;
														}
													}
												}
												if (add) {
													CMLMolecule copy = (CMLMolecule)subMol.copy();
													validMolList.add(copy);
													finalMolList.add(copy);
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
							if (finalMolList.size() == 0) {
								int cCharge = -1;
								for (CMLAtom atom : subMolAtomList) {
									if ("C".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 3
											&& !alreadySetAtoms.contains(atom)) {
										atom.setFormalCharge(cCharge);
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
												// the least charged atoms.
												if (!piRemaining) {
													CMLMolecule copy = (CMLMolecule)subMol.copy();
													validMolList.add(copy);
													finalMolList.add(copy);
													break cAttempt;
												}
											}
										}
										atom.setFormalCharge(0);
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
						if (finalMolList.size() > 0) {
							// remember that molCharge is the charge given to the molecule from the CIF file
							CMLMolecule theMol = null;
							if (molCharge != UNREALISTIC_CHARGE && !isMetalComplex) {
								for (CMLMolecule n : finalMolList) {
									if (molCharge == n.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS).getFormalCharge()) {
										theMol = n;
									}
								}

							} 
							// if theMol not set above OR part of metal complex OR no corresponding formal charge
							if (theMol == null) {
								if (isMetalComplex) {
									int count = 0;
									int currentCharge2 = 0;
									for (CMLMolecule n : finalMolList) {
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
									for (CMLMolecule n : finalMolList) {
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
						}
					}
				}
				// return the mol to its original state before adding metal
				// atoms and bonds back in
				ctt.flattenMolecules();
			}
			// reattach metal atoms and bonds now bonds have been calculated
			this.addMetalAtomsAndBonds(molecule, metalAtomAndBondMap);
		}
	}
	
	/**
	 * add double bonds through PiSystemManager.
	 */
	public void adjustBondOrdersAndChargesToValency(CMLFormula moietyFormula) {
		PiSystemManager piSystemManager = new PiSystemManager();
		piSystemManager.setUpdateBonds(true);
		piSystemManager.setKnownUnpaired(0);
		piSystemManager.setDistributeCharge(true);
		this.adjustBondOrdersAndChargesToValency(piSystemManager, moietyFormula);
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
	
	public List<CMLAtom> getAlreadySetAtoms() {
		return this.alreadySetAtoms;
	}
}
