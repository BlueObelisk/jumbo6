package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;

public class ValencyTool extends AbstractTool {

	CMLMolecule molecule;
	MoleculeTool moleculeTool;
	private String formulaS;
	
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
			addDoubleCharge("Si", -2, "F", 0);
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
		} else if (formulaS.equals("Cl 1") || formulaS.equals("Br 1")
				|| formulaS.equals("I 1")) {
			molecule.getAtoms().get(0).setFormalCharge(-1);
		} else {
			marked = false;
		}
		return marked;
	}
	
	void addCharge(String elementType, int charge) {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (atom.getElementType().equals(elementType)) {
				atom.setFormalCharge(charge);
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
		this.markCarbanion(atoms);
		this.markSandwichLigands(atoms);
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
		centralA.setFormalCharge(centralCharge);
		int count = 0;
		for (CMLAtom atom : atoms) {
			if (ligandS.equals(atom.getElementType())) {
				int bos = moleculeTool.getBondOrderSum(atom);
				if (bos == 1) {
					if (count++ < nChargeLigand) {
						atom.setFormalCharge(-1);
					} else {
						molecule.getBond(centralA, atom).setOrder(
								CMLBond.DOUBLE);
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
					atom.getLigandBonds().get(0).setOrder(CMLBond.DOUBLE);
					atom.setFormalCharge(-2);
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
						atom.getLigandBonds().get(0).setOrder(CMLBond.DOUBLE);
						atom.setFormalCharge(-1);
						ligand.setFormalCharge(1);
					}
					if (ligand.getLigandAtoms().size() == 2) {
						atom.setFormalCharge(-2);
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
					atom.setFormalCharge(-1);
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
					atom.getLigandBonds().get(0).setOrder(CMLBond.TRIPLE);
					atom.setFormalCharge(-1);
					ligand.setFormalCharge(1);
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
					atom.setFormalCharge(-2);
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
				molecule.getAtomById(ringAtomList.get(0).getId()).setFormalCharge(-1);
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
								atom.setFormalCharge(-1);
								lig.setFormalCharge(1);
								molecule.getBond(atom, lig).setOrder(CMLBond.DOUBLE);
								molecule.getBond(lig, at).setOrder(CMLBond.DOUBLE);
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
				atom.setFormalCharge(1);
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
						atom.setFormalCharge(-1);
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
					atom.getLigandBonds().get(0).setOrder(CMLBond.TRIPLE);
				}
			}
		}
	}

	void markNCNCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType())) {
				if (atom.getLigandAtoms().size() == 2) {
					atom.setFormalCharge(-1);
					for (CMLBond bond : atom.getLigandBonds()) {
						bond.setOrder(CMLBond.SINGLE);
					}
				} else if (atom.getLigandAtoms().size() == 1) {
					atom.getLigandBonds().get(0).setOrder(CMLBond.TRIPLE);
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
				atom.setFormalCharge(-1);
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
						negative.setFormalCharge(-1);
						molecule.getBond(atom, negative).setOrder(CMLBond.DOUBLE);
						molecule.getBond(atom, nList.get(1)).setOrder(CMLBond.DOUBLE);
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
						negative.setFormalCharge(-1);
						molecule.getBond(atom, negative).setOrder(CMLBond.DOUBLE);
						molecule.getBond(atom, nList.get(1)).setOrder(CMLBond.DOUBLE);
						atom.setFormalCharge(+1);
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
							oAtom.setFormalCharge(-1);
							for (CMLAtom oAt : oxyList) {
								if (oAt != oAtom) {
									molecule.getBond(atom, oAt).setOrder(
											CMLBond.DOUBLE);
									break marker;
								}
							}
						}
					}
					oxyList.get(0).setFormalCharge(-1);
					molecule.getBond(atom, oxyList.get(1)).setOrder(
							CMLBond.DOUBLE);
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
						bond.setOrder(CMLBond.TRIPLE);
						atom.setFormalCharge(-1);
						ligands.get(0).setFormalCharge(1);
					} else if ("N".equals(ligands.get(0).getElementType())) {
						CMLBond bond = atom.getLigandBonds().get(0);
						bond.setOrder(CMLBond.TRIPLE);
						atom.setFormalCharge(-1);
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
							atom.getLigandBonds().get(0).setOrder(CMLBond.TRIPLE);
							atom.setFormalCharge(-1);
						}
					}
				}
			}
		}
	}

	void markCarbanion(List<CMLAtom> atoms) {
		// doesn't take into account the possibility of the structure
		// being c=c=c
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3 && atom.isBondedToMetal()) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if (ligand.isBondedToMetal()) {
							set = false;
						}
					}
					if (set) {
						atom.setFormalCharge(-1);
					}
				}
				// if carbon has only two ligands then check to see if it
				// can be marked as a carbanion.
				if (ligands.size() == 2  && atom.isBondedToMetal()) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if ("C".equals(ligand.getElementType())) {
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
						atom.setFormalCharge(-1);
					}
				}
			}
		}
	}

	void mark_CSi_anion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType()) || "Si".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3 && atom.isBondedToMetal()) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if (ligand.isBondedToMetal()) {
							set = false;
						}
					}
					if (set) {
						atom.setFormalCharge(-1);
					}
				}
				// if carbon has only two ligands then check to see if it
				// can be marked as a carbanion.
				if (ligands.size() == 2  && atom.isBondedToMetal()) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if ("C".equals(ligand.getElementType())) {
							List<CMLAtom> ats = ligand.getLigandAtoms();
							if (ats.size() == 2) {
								// if one of the two ligands also only
								// has two ligands then stop as this is
								// most probably a carbyne.
								for (CMLAtom at : ats) {
									if (at.equals(atom)) {
										System.out.println(at.getId());
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
						atom.setFormalCharge(-1);
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
				if (oxyList.size() > 0) {
					// think setting as +1 does more harm than good
					//atom.setFormalCharge(1);
					oxyList.get(0).setFormalCharge(-1);
				}
				if (oxyList.size() == 2) {
					molecule.getBond(atom, oxyList.get(1)).setOrder(
							CMLBond.DOUBLE);
				}
			}
		}
	}

	void markPAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("P".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 2) {
					atom.setFormalCharge(-1);
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
					molecule.getBond(atom, oxyList.get(0)).setOrder(
							CMLBond.DOUBLE);
				}
				if (oxyList.size() > 1) {
					molecule.getBond(atom, oxyList.get(1)).setOrder(
							CMLBond.DOUBLE);
				}
				if (oxyList.size() == 3) {
					oxyList.get(2).setFormalCharge(-1);
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
					sulfoList.get(0).setFormalCharge(-1);
					molecule.getBond(atom, sulfoList.get(1)).setOrder(
							CMLBond.DOUBLE);
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
					s.setFormalCharge(-1);
					molecule.getBond(atom, o).setOrder(
							CMLBond.DOUBLE);
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
					s.setFormalCharge(-1);
					molecule.getBond(c, s).setOrder(CMLBond.DOUBLE);
					molecule.getBond(c, n).setOrder(CMLBond.DOUBLE);
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

}
