package org.xmlcml.cml.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElement.FormalChargeControl;
import org.xmlcml.cml.base.CMLElement.Hybridization;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLBuilder;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLJoin;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.Type;

/**
 * additional tools for molecule. not fully developed
 *
 * @author pmr
 *
 */
public class MoleculeTool extends AbstractTool {

	final static String IDX = "idx";
	Logger logger = Logger.getLogger(MoleculeTool.class.getName());

	String metalLigandDictRef = "jumbo:metalLigand";
	boolean containsEWithdrawingGroup = false;

	/**
	 * control operations for removing disorder.
	 */
	public enum RemoveDisorderControl {
		/** used with processDisorder to remove minor components. */
		REMOVE_MINOR_DISORDER;
		private RemoveDisorderControl() {
		}
	}

	/**
	 * control operations for removing disorder.
	 */
	public enum ProcessDisorderControl {
		/**
		 * used with processDisorder to handle disorder in a molecule. STRICT -
		 * if the disorder does not comply with the CIF specification, then an
		 * error is thrown. LOOSE - attempts to process disorder which deviates
		 * from the CIF specification
		 */

		STRICT,
		/** */
		LOOSE;
		private ProcessDisorderControl() {
		}
	}

	CMLMolecule molecule;

	/**
	 * constructor
	 *
	 * @param molecule
	 */
	public MoleculeTool(CMLMolecule molecule) {
		this.molecule = molecule;
	}

	/**
	 * make molecule tool from a molecule.
	 *
	 * @param molecule
	 * @return the tool
	 */
	static MoleculeTool createMoleculeTool(CMLMolecule molecule) {
		return new MoleculeTool(molecule);
	}

	/**
	 * get molecule.
	 *
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
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
			int molCharge = 99;
			boolean isMetalComplex = false;
			for (CMLFormula formula : moietyFormulaList) {
				CMLFormula molForm = mol.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
				if (molForm.getConciseNoCharge().equals(formula.getConciseNoCharge())) {
					molCharge = formula.getFormalCharge();
					System.out.println("molcharge: "+molCharge);
				}
			}
			// reset all bond orders to single
			mol.setBondOrders(CMLBond.SINGLE);
			ConnectionTableTool ctt = new ConnectionTableTool(mol);
			ctt.getCyclicBonds();
			// remove metal atoms, calculate bond orders, then reattach 
			// the metal atoms
			List<CMLAtom> metalAtomList = new ArrayList<CMLAtom>();
			List<CMLBond> metalBondList = new ArrayList<CMLBond>();
			List<CMLAtom> atomList = mol.getAtoms();		
			for(CMLAtom atom : atomList) {
				ChemicalElement element = atom.getChemicalElement();
				if (MoleculeTool.isChemicalElementType(element, Type.METAL)) {
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
			//if the removing of metal atoms takes the molecules atom to zero
			//then don't bother calculating bonds
			if (mol.getAtomCount() > 1) {
				// now the metal atoms and bonds have been removed, partition 
				// molecule into submolecules and then calculate the bond orders
				// and charges
				ctt.partitionIntoMolecules();
				List<CMLMolecule> subMols = mol.getDescendantsOrMolecule();
				for (CMLMolecule subMol : subMols) {
					MoleculeTool subMolTool = new MoleculeTool(subMol);
					boolean common = subMolTool.markupCommonMolecules();
					if (!common) {
						subMolTool.markupSpecial();
						subMol.setNormalizedBondOrders();
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
							List<CMLAtom> piAtomList = piSys.getAtomList();
							for (CMLAtom atom : piAtomList) {
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
										if (ligand.getLigandAtoms().size() == 2) {
											int count = 0;
											for (CMLAtom ligLig : ligand.getLigandAtoms()) {
												if ("H".equals(ligLig.getElementType())) {
													count++;
												}
											}
											if (count == 0) {
												n2List.add(ligand);
											}
										}
									}
								}
							}
						}
						for (CMLAtom atom : subMolAtomList) {
							if ("O".equals(atom.getElementType()) || "S".equals(atom.getElementType())) {
								if (atom.getLigandAtoms().size() == 1) {
									osList.add(atom);
								}
							}
						}
						// take all combinations of charges on the atoms found and attempt to 
						// get a completed pi-system.
						List<List<Integer>> n3ComboList = generateCombinationList(n3List.size());
						List<List<Integer>> osComboList = generateCombinationList(osList.size());
						List<List<Integer>> n2ComboList = generateCombinationList(n2List.size());
						//System.out.println(n3List.size()+", combos: "+nComboList.size());
						//System.out.println(osList.size()+", combos: "+osComboList.size());
						//System.out.println(orList.size()+", combos: "+orComboList.size());
						List<CMLMolecule> validMolList = new ArrayList<CMLMolecule>();
						List<CMLMolecule> finalMolList = new ArrayList<CMLMolecule>();
						for (int i = 0; i < n3ComboList.size(); i++) {
							for (int j = osComboList.size()-1; j >= 0; j--) {
								//System.out.println("====================");
								List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
								for (Integer in : n3ComboList.get(i)) {
									CMLAtom atom = n3List.get(in);
									atom.setFormalCharge(1);
									//System.out.println("setting n3: "+atom.getId());
									chargedAtoms.add(atom);
								}
								for (Integer in : osComboList.get(j)) {
									CMLAtom atom = osList.get(in);
									atom.setFormalCharge(-1);
									chargedAtoms.add(atom);
									//System.out.println("setting os: "+atom.getId());
								}
								PiSystem newPiS = new PiSystem(subMolAtomList);
								newPiS.setPiSystemManager(piSystemManager);
								List<PiSystem> newPiSList = newPiS.generatePiSystemList();
								int sysCount = 0;
								//System.out.println("system count: "+newPiSList.size());
								boolean piRemaining = false;
								for (PiSystem system : newPiSList) {
									//System.out.println("system atoms: "+system.getAtomList().size());
									sysCount++;
									system.identifyDoubleBonds();
									for (CMLAtom a : system.getAtomList()) {
										Nodes nodes = a.query(".//cml:electron[@dictRef='cml:piElectron']", X_CML);
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
									atom.setFormalCharge(0);
								}
								// reset only those bonds that were single to start with
								for (CMLBond bond : singleBonds) {
									bond.setOrder(CMLBond.SINGLE);
								}
								// reset all pi-electrons
								Nodes piElectrons = subMol.query(".//cml:atom/cml:electron[@dictRef='cml:piElectron']", X_CML);
								for (int e = 0; e < piElectrons.size(); e++) {
									((CMLElectron)piElectrons.get(e)).detach();
								}
							}
						}
						n2:
						if (finalMolList.size() == 0) {
							System.out.println("MARKING UP N2s");
							for (int l = 0; l < n2ComboList.size(); l++) {
							for (int i = 0; i < n3ComboList.size(); i++) {
								for (int j = osComboList.size()-1; j >= 0; j--) {
										//System.out.println("====================");
										List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
										for (Integer in : n3ComboList.get(i)) {
											CMLAtom atom = n3List.get(in);
											atom.setFormalCharge(1);
											//System.out.println("setting n3: "+atom.getId());
											chargedAtoms.add(atom);
										}
										for (Integer in : osComboList.get(j)) {
											CMLAtom atom = osList.get(in);
											atom.setFormalCharge(-1);
											chargedAtoms.add(atom);
											//System.out.println("setting os: "+atom.getId());
										}
										for (Integer in : n2ComboList.get(l)) {
											CMLAtom atom = n2List.get(in);
											atom.setFormalCharge(-1);
											chargedAtoms.add(atom);
											System.out.println("setting n2: "+atom.getId());
										}
										PiSystem newPiS = new PiSystem(subMolAtomList);
										newPiS.setPiSystemManager(piSystemManager);
										List<PiSystem> newPiSList = newPiS.generatePiSystemList();
										int sysCount = 0;
										//System.out.println("system count: "+newPiSList.size());
										boolean piRemaining = false;
										for (PiSystem system : newPiSList) {
											//System.out.println("system atoms: "+system.getAtomList().size());
											sysCount++;
											system.identifyDoubleBonds();
											for (CMLAtom a : system.getAtomList()) {
												Nodes nodes = a.query(".//cml:electron[@dictRef='cml:piElectron']", X_CML);
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
													break n2;
												}
											}
										}
										// reset charges on charged atoms
										for (CMLAtom atom : chargedAtoms) {
											atom.setFormalCharge(0);
										}
										// reset only those bonds that were single to start with
										for (CMLBond bond : singleBonds) {
											bond.setOrder(CMLBond.SINGLE);
										}
										// reset all pi-electrons
										Nodes piElectrons = subMol.query(".//cml:atom/cml:electron[@dictRef='cml:piElectron']", X_CML);
										for (int e = 0; e < piElectrons.size(); e++) {
											((CMLElectron)piElectrons.get(e)).detach();
										}
								}
							}
							}
						}
						cAttempt:
							if (finalMolList.size() == 0) {
								System.out.println("MARKING UP C-");
								for (CMLAtom atom : subMolAtomList) {
									if ("C".equals(atom.getElementType())) {
										atom.setFormalCharge(-1);
										PiSystem newPiS = new PiSystem(subMolAtomList);
										newPiS.setPiSystemManager(piSystemManager);
										List<PiSystem> newPiSList = newPiS.generatePiSystemList();
										int sysCount = 0;
										boolean piRemaining = false;
										for (PiSystem system : newPiSList) {
											sysCount++;
											boolean inSystem = false;
											for (CMLAtom at : system.getAtomList()) {
												if (at.getId().equals(atom.getId())) {
													inSystem = true;
												}
											}
											if (!inSystem) {
												System.out
														.println("not in suystem");
												continue;
											}
											system.identifyDoubleBonds();
											for (CMLAtom a : system.getAtomList()) {
												Nodes nodes = a.query(".//cml:electron[@dictRef='cml:piElectron']", X_CML);
												System.out
														.println("pi es left: "+nodes.size());
												if (nodes.size() > 0) {
													piRemaining = true;
													for (int i = 0; i < nodes.size(); i++) {
														nodes.get(i).detach();
													}
												}
											}
											if (piRemaining) {
												atom.setFormalCharge(0);
											} else {
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
										}
									}
								}
								// reset only those bonds that were single to start with
								for (CMLBond bond : singleBonds) {
									bond.setOrder(CMLBond.SINGLE);
								}
								// reset all pi-electrons
								Nodes piElectrons = subMol.query(".//cml:atom/cml:electron[@dictRef='cml:piElectron']", X_CML);
								for (int e = 0; e < piElectrons.size(); e++) {
									((CMLElectron)piElectrons.get(e)).detach();
								}
							}
						if (finalMolList.size() > 0) {							
							CMLMolecule theMol = null;
							if (molCharge != 99 && !isMetalComplex) {
								for (CMLMolecule n : finalMolList) {
									if (molCharge == n.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS).getFormalCharge()) {
										theMol = n;
									}
								}

							} 
							// if theMol not set above OR part of metal complex OR no corresponding formal charge
							if (theMol == null) {
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
							for (Node node : subMol.getChildCMLElements()) {
								node.detach();
							}
							for (Node node : theMol.getChildCMLElements()) {
								subMol.appendChild(node.copy());
							}
						}
					}
				}
				// return the mol to its original state before adding metal
				// atoms and bonds back in
				ctt.flattenMolecules();
			}
			// reattach metal atoms and bonds now bonds have been calculated
			CMLAtomArray atomArray = mol.getAtomArray();
			CMLBondArray bondArray = mol.getBondArray();	
			for (CMLAtom atom : metalAtomList) {
				atomArray.appendChild(atom);
			}
			if (bondArray != null) {
				bondArray.indexBonds();
				for (CMLBond bond : metalBondList) {
					bondArray.appendChild(bond);
				}
			}
			// remove scalars signifying atoms attached to metal
			Nodes nodes = molecule.query(".//cml:scalar[@dictRef='"+metalLigandDictRef+"']", X_CML);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
	}
	
	public int getFormalCharge() {
		int formalCharge = 0;
		Nodes chargedAtoms = molecule.getAtomArray().query(".//cml:atom[@formalCharge]", X_CML);
		for (int i = 0; i < chargedAtoms.size(); i++) {
			formalCharge += Integer.parseInt(((Element)chargedAtoms.get(i)).getAttributeValue("formalCharge"));
		}
		return formalCharge;
	}
	
	public List<CMLAtom> getChargedAtoms() {
		List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
		Nodes atoms = molecule.getAtomArray().query(".//cml:atom[@formalCharge != 0]", X_CML);
		for (int i = 0; i < atoms.size(); i++) {
			chargedAtoms.add((CMLAtom)atoms.get(i));
		}
		return chargedAtoms;
	}

	private List<List<Integer>> generateCombinationList(int listSize) {
		List<List<Integer>> combinationList = new ArrayList<List<Integer>>();
		int count = (int) Math.pow(2.0, listSize);
		for (int i = 2; i <= count; i++) {
			int thisCount = i;
			List<Integer> intSet = new ArrayList<Integer>(listSize);
			for (int j = listSize; j >= 0; j--) {
				int minus = (int)Math.pow(2.0, j);
				int test = thisCount;
				if (test - minus > 0) {
					thisCount -= minus;
					intSet.add(j);
				}
			}
			combinationList.add(intSet);
		}
		// add entry with no values
		combinationList.add(new ArrayList<Integer>(0));
		
		/*
		 * the below version gets the largest combinations first, working down
		List<List<Integer>> combinationList = new ArrayList<List<Integer>>();
		int count = (int) Math.pow(2.0, listSize);
		for (int i = count; i >= 1; i--) {
			int thisCount = i;
			List<Integer> intSet = new ArrayList<Integer>(listSize);
			for (int j = listSize-1; j >= 0; j--) {
				int minus = (int)Math.pow(2.0, j);
				int test = thisCount;
				if (test - minus > 0) {
					thisCount -= minus;
					intSet.add(j);
				}
			}
			combinationList.add(intSet);
		}
		*/
		return combinationList;
	}

	/**
	 * Adjust bond orders to satisfy valence.
	 *
	 * in impossible systems appropriate atoms are marked as radicals
	 * (spinMultiplicity) assumes explicit hydrogens
	 *
	 * @param piSystemManager
	 */
	public void adjustBondOrdersToValency(PiSystemManager piSystemManager) {
		// normalize bond orders
		molecule.setNormalizedBondOrders();
		PiSystem piSystem2 = new PiSystem(molecule.getAtoms());
		piSystem2.setPiSystemManager(new PiSystemManager(piSystemManager));
		List<PiSystem> piSystemList2 = piSystem2.generatePiSystemList();
		for (PiSystem subPiSystem : piSystemList2) {
			List<CMLAtom> atomList = subPiSystem.getAtomList();
			int npi = atomList.size();
			if (npi < 2) {
				System.out.println("Cannot find pi system for " + npi);
			} else {
				subPiSystem.identifyDoubleBonds();
			}
		}
	}

	/**
	 * get the double bond equivalents.
	 *
	 * this is the number of double bonds the atom can make an sp2 atom has 1 an
	 * sp atom has 2
	 *
	 * @param atom
	 * @param fcd
	 * @return the bond sum (0, 1, 2)
	 * @throws CMLRuntimeException
	 *             if cannot get formal charges
	 */
	public int getDoubleBondEquivalents(CMLAtom atom, FormalChargeControl fcd) {
		if (atom.getMolecule() == null) {
			throw new CMLRuntimeException("WARNING skipping DBE");
		}
		int valenceElectrons = atom.getValenceElectrons();
		int formalCharge = 0;
		try {
			formalCharge = atom.getFormalCharge(fcd);
		} catch (CMLRuntimeException e) {
			e.printStackTrace();
		}
		valenceElectrons -= formalCharge;
		int maxBonds = (valenceElectrons < 4) ? valenceElectrons
				: 8 - valenceElectrons;
		// hydrogens are include in bondSum
		int doubleBondEquivalents = maxBonds - this.getBondOrderSum(atom);
		return doubleBondEquivalents;
	}

	/**
	 * get sum of formal bondOrders. uses the actual ligands (i.e. implicit
	 * hydrogens are not used) aromatic bonds are counted as 1.5, 1,2,3 aromatic
	 * bonds add 1 double bond
	 *
	 * @param atom
	 * @return sum (-1 means cannot be certain)
	 */
	public int getBondOrderSum(CMLAtom atom) {
		int multipleBondSum = 0;
		int aromaticBondSum = 0;
		boolean ok = true;
		List<CMLBond> ligandBonds = atom.getLigandBonds();
		for (CMLBond ligandBond : ligandBonds) {
			// if the bond is to a H atom then don't increment
			// multipleBondSum as H's are dealt with further down
			// the method.
			boolean hasH = false;
			for (CMLAtom bondAt : ligandBond.getAtoms()) {
				if ("H".equals(bondAt.getElementType()) && bondAt != atom) {
					hasH = true;
				}
			}
			if (hasH) {
				continue;
			}
			String order = ligandBond.getOrder();
			if (order == null) {
				multipleBondSum += 0;
			} else if (order.equals(CMLBond.DOUBLE)) {
				multipleBondSum += 2;
			} else if (order.equals(CMLBond.TRIPLE)) {
				multipleBondSum += 3;
			} else if (order.equals(CMLBond.SINGLE)) {
				multipleBondSum += 1;
			} else if (order.equals(CMLBond.AROMATIC)) {
				aromaticBondSum += 1;
			} else {
				logger.info("Unknown bond order:" + order + ":");
				ok = false;
			}
		}
		// any aromatic bonds dictate SP2, so...
		if (aromaticBondSum > 0) {
			multipleBondSum = atom.getLigandBonds().size() + 1;
		}
		int hydrogenCount = 0;
		try {
			hydrogenCount = atom.getHydrogenCount();
		} catch (CMLRuntimeException e) {
		}
		multipleBondSum += hydrogenCount;

		return ((ok) ? multipleBondSum : -1);
	}

	/**
	 * calculates the geometrical hybridization.
	 *
	 * calculates the geometrical hybridisation from 3D coords tetrahedron or
	 * pyramid gives SP3 trigonal gives SP2 bent gives BENT linear gives SP
	 * square planar gives SQ
	 *
	 * pyramidal and planar are distinguished by improper dihedral of 18 degrees
	 *
	 * @param atom
	 * @return hybridisation (SP3, SP2, BENT, SP, SQUARE_PLANAR)
	 */
	public Hybridization getGeometricHybridization(CMLAtom atom) {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		Hybridization geomHybridization = null;
		if (ligands.size() <= 1 || ligands.size() > 4) {
			return null;
		}
		Point3 thisXyz = atom.getXYZ3();
		Angle angle = null;
		if (thisXyz == null) {

		} else if (ligands.size() == 2) {
			Point3 thisXyz1 = ligands.get(0).getXYZ3();
			Point3 thisXyz2 = ligands.get(1).getXYZ3();
			try {
				angle = Point3.getAngle(thisXyz1, thisXyz, thisXyz2);
				if (angle.getDegrees() > 150) {
					geomHybridization = Hybridization.SP;
				} else {
					geomHybridization = Hybridization.BENT;
					// if (angle.getDegrees() > 115) return CMLAtom.SP2;
					// return CMLAtom.SP3;
				}
			} catch (Exception e) {
				logger.severe("BUG " + e);
			}
		} else if (ligands.size() == 3) {
			Point3 thisXyz1 = ligands.get(0).getXYZ3();
			Point3 thisXyz2 = ligands.get(1).getXYZ3();
			Point3 thisXyz3 = ligands.get(2).getXYZ3();
			// improper dihedral
			try {
				angle = Point3
				.getTorsion(thisXyz1, thisXyz2, thisXyz3, thisXyz);
				geomHybridization = Hybridization.SP2;
				if (Math.abs(angle.getDegrees()) > 18) {
					geomHybridization = Hybridization.SP3;
				}
			} catch (Exception e) {
				logger.severe("BUG " + e);
			}
		} else {
			geomHybridization = Hybridization.SP3;
		}
		return geomHybridization;
	}

	/**
	 * a simple lookup for common atoms.
	 *
	 * examples are C, N, O, F, Si, P, S, Cl, Br, I if atom has electronegative
	 * ligands, (O, F, Cl...) returns -1
	 *
	 * @param atom
	 * @return group
	 */
	public int getHydrogenValencyGroup(CMLAtom atom) {
		final int[] group = { 1, 4, 5, 6, 7, 4, 5, 6, 7, 7, 7 };
		final int[] eneg0 = { 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1 };
		final int[] eneg1 = { 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1 };
		int elNum = -1;
		try {
			String elType = atom.getElementType();
			elNum = CMLAtom.getCommonElementSerialNumber(elType);
			if (elNum == -1) {
				return -1;
			}
			if (eneg0[elNum] == 0) {
				return group[elNum];
			}
			// if atom is susceptible to enegative ligands, exit if they are
			// present
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			for (CMLAtom lig : ligandList) {
				int ligElNum = CMLAtom.getCommonElementSerialNumber(lig
						.getElementType());
				if (ligElNum == -1 || eneg1[ligElNum] == 1) {
					return -2;
				}
			}
		} catch (Exception e) {
			logger.severe("BUG " + e);
		}
		int g = (elNum == -1) ? -1 : group[elNum];
		return g;
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 *
	 * Uses algorithm: nH = 8 - group - sumbondorder + formalCharge, where group
	 * is 0-8 in first two rows
	 *
	 * @param atom
	 * @param control
	 *            specifies whether H are explicit or in hydrogenCount
	 */
	public void adjustHydrogenCountsToValency(CMLAtom atom,
			CMLMolecule.HydrogenControl control) {
		int group = this.getHydrogenValencyGroup(atom);
		if (group == -1) {
			return;
		} else if (group == -2) {
			return;
		}
		// hydrogen and metals
		if (group < 4) {
			return;
		}
		int sumBo = this.getSumNonHydrogenBondOrder(atom);
		int fc = (atom.getFormalChargeAttribute() == null) ? 0 : atom
				.getFormalCharge();
		int nh = 8 - group - sumBo + fc;
		// non-octet species
		if (group == 4 && fc == 1) {
			nh -= 2;
		}
		atom.setHydrogenCount(nh);
		this.expandImplicitHydrogens(atom, control);
	}

	/**
	 * Sums the formal orders of all bonds from atom to non-hydrogen ligands.
	 *
	 * Uses 1,2,3,A orders and creates the nearest integer. Thus 2 aromatic
	 * bonds sum to 3 and 3 sum to 4. Bonds without order are assumed to be
	 * single
	 *
	 * @param atom
	 * @exception CMLRuntimeException
	 *                null atom in argument
	 * @return sum of bond orders. May be 0 for isolated atom or atom with only
	 *         H ligands
	 */
	public int getSumNonHydrogenBondOrder(CMLAtom atom)
	throws CMLRuntimeException {
		float sumBo = 0.0f;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (ligand.getElementType().equals("H")) {
				continue;
			}
			CMLBond bond = molecule.getBond(atom, ligand);
			if (bond == null) {
				throw new CMLRuntimeException(
						"Serious bug in getSumNonHydrogenBondOrder");
			}
			String bo = bond.getOrder();
			if (bo != null) {
				if (bo.equals("1") || bo.equals("S")) {
					sumBo += 1.0;
				}
				if (bo.equals("2") || bo.equals("D")) {
					sumBo += 2.0;
				}
				if (bo.equals("3") || bo.equals("T")) {
					sumBo += 3.0;
				}
				if (bo.equals("A")) {
					sumBo += 1.4;
				}
			} else {
				// if no bond order, assume single
				sumBo += 1.0;
			}
		}
		return Math.round(sumBo);
	}

	/**
	 * deletes a hydrogen from an atom.
	 *
	 * used for building up molecules. If there are implicit H atoms it reduces
	 * the hydrogenCount by 1. If H's are explicit it removes the first hydrogen
	 * ligand
	 *
	 * @param atom
	 * @exception CMLRuntimeException
	 *                no hydrogen ligands on atom
	 */
	public void deleteHydrogen(CMLAtom atom) {
		if (atom.getHydrogenCountAttribute() != null) {
			if (atom.getHydrogenCount() > 0) {
				atom.setHydrogenCount(atom.getHydrogenCount() - 1);
			} else {
				throw new CMLRuntimeException("No hydrogens to delete");
			}
		} else {
			Set<ChemicalElement> hSet = ChemicalElement
			.getElementSet(new String[] { "H" });
			List<CMLAtom> hLigandVector = CMLAtom.filter(atom.getLigandAtoms(),
					hSet);
			if (hLigandVector.size() > 0) {
				molecule.deleteAtom(hLigandVector.get(0));
			} else {
				throw new CMLRuntimeException("No hydrogens to delete");
			}
		}
	}

	/**
	 * Gets the nonHydrogenLigandList attribute of the AtomImpl object
	 *
	 * @param atom
	 * @return The nonHydrogenLigandList value
	 */
	public List<CMLAtom> getNonHydrogenLigandList(CMLAtom atom) {
		List<CMLAtom> newLigandList = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!ligand.getElementType().equals("H")) {
				newLigandList.add(ligand);
			}
		}
		return newLigandList;
	}

	/**
	 * gets all atoms downstream of a bond.
	 *
	 * recursively visits all atoms in branch until all leaves have been
	 * visited. if branch is cyclic, halts when it rejoins atom or otherAtom
	 * Example:
	 *
	 * <pre>
	 *
	 *  MoleculeTool moleculeTool ... contains relevant molecule
	 *  CMLBond b ... (contains atom)
	 *  CMLAtom otherAtom = b.getOtherAtom(atom);
	 *  AtomSet ast = moleculeTool.getDownstreamAtoms(atom, otherAtom);
	 *
	 *  @param atom
	 *  @param otherAtom not to be visited
	 *  @return the atomSet (empty if none)
	 *
	 */
	public CMLAtomSet getDownstreamAtoms(CMLAtom atom, CMLAtom otherAtom) {
		CMLAtomSet atomSet = new CMLAtomSet();
		boolean forceUpdate = false;
		getDownstreamAtoms(atom, atomSet, otherAtom, forceUpdate);
		atomSet.updateContent();
		return atomSet;
	}

	/**
	 * gets all atoms downstream of a bond.
	 * VERY SLOW I THINK
	 * recursively visits all atoms in branch until all leaves have been
	 * visited. if branch is cyclic, halts when it rejoins atom or otherAtom the
	 * routine is passed a new AtomSetImpl to be populated
	 * <pre>
	 *   Example: CMLBond b ... (contains atom)
	 *     CMLAtom otherAtom = b.getOtherAtom(atom);
	 *     AtomSet ast = new AtomSetImpl();
	 *     atom.getDownstreamAtoms(ast, otherAtom);
	 * </pre>
	 * @param atom
	 * @param atomSet
	 *            to accumulate ligand atoms
	 * @param otherAtom
	 *            not to be visited
	 *
	 */
	private void getDownstreamAtoms(CMLAtom atom, CMLAtomSet atomSet,
			CMLAtom otherAtom, boolean forceUpdate) {

		atomSet.addAtom(atom, forceUpdate);
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			// do not revisit atoms
			if (atomSet.contains(ligandAtom)) {
				;
				// do not backtrack
			} else if (ligandAtom.equals(otherAtom)) {
				;
			} else {
				this.getDownstreamAtoms(ligandAtom, atomSet, atom, forceUpdate);
			}
		}
	}

	/**
	 * append to id creates new id, perhaps to disambiguate
	 *
	 * @param atom
	 * @param s
	 */
	public void appendToId(CMLAtom atom, final String s) {
		String id = atom.getId();
		if ((id != null) && (id.length() > 0)) {
			atom.renameId(id + s);
		} else {
			atom.renameId(s);
		}
	}

	/**
	 * Adds 3D coordinates for singly-bonded ligands of this.
	 *
	 * <pre>
	 *       this is labelled A.
	 *       Initially designed for hydrogens. The ligands of A are identified
	 *       and those with 3D coordinates used to generate the new points. (This
	 *       allows structures with partially known 3D coordinates to be used, as when
	 *       groups are added.)
	 *       &quot;Bent&quot; and &quot;non-planar&quot; groups can be formed by taking a subset of the
	 *       calculated points. Thus R-NH2 could use 2 of the 3 points calculated
	 *       from (1,iii)
	 *       nomenclature: &quot;this&quot; is point to which new ones are &quot;attached&quot;.
	 *           this may have ligands B, C...
	 *           B may have ligands J, K..
	 *           points X1, X2... are returned
	 *       The cases (see individual routines, which use idealised geometry by default):
	 *       (0) zero ligands of A. The resultant points are randomly oriented:
	 *          (i) 1 points  required; +x,0,0
	 *          (ii) 2 points: use +x,0,0 and -x,0,0
	 *          (iii) 3 points: equilateral triangle in xy plane
	 *          (iv) 4 points x,x,x, x,-x,-x, -x,x,-x, -x,-x,x
	 *       (1a) 1 ligand(B) of A which itself has a ligand (J)
	 *          (i) 1 points  required; vector along AB vector
	 *          (ii) 2 points: 2 vectors in ABJ plane, staggered and eclipsed wrt J
	 *          (iii) 3 points: 1 staggered wrt J, the others +- gauche wrt J
	 *       (1b) 1 ligand(B) of A which has no other ligands. A random J is
	 *       generated and (1a) applied
	 *       (2) 2 ligands(B, C) of this
	 *          (i) 1 points  required; vector in ABC plane bisecting AB, AC. If ABC is
	 *              linear, no points
	 *          (ii) 2 points: 2 vectors at angle ang, whose resultant is 2i
	 *       (3) 3 ligands(B, C, D) of this
	 *          (i) 1 points  required; if A, B, C, D coplanar, no points.
	 *             else vector is resultant of BA, CA, DA
	 *
	 *       The method identifies the ligands without coordinates, calculates them
	 *       and adds them. It assumes that the total number of ligands determines the
	 *       geometry. This can be overridden by the geometry parameter. Thus if there
	 *       are three ligands and TETRAHEDRAL is given a pyramidal geometry is created
	 *
	 *       Inappropriate cases throw exceptions.
	 *
	 *       fails if atom itself has no coordinates or &gt;4 ligands
	 *       see org.xmlcml.molutils.Molutils for more details
	 *
	 * </pre>
	 *
	 * @param atom
	 * @param geometry
	 *            from: Molutils.DEFAULT, Molutils.ANY, Molutils.LINEAR,
	 *            Molutils.TRIGONAL, Molutils.TETRAHEDRAL
	 * @param length
	 *            A-X length
	 * @param angle
	 *            B-A-X angle (used for some cases)
	 * @return atomSet with atoms which were calculated. If request could not be
	 *         fulfilled (e.g. too many atoms, or strange geometry) returns
	 *         empty atomSet (not null)
	 * @throws CMLException
	 */
	public CMLAtomSet calculate3DCoordinatesForLigands(CMLAtom atom,
			int geometry, double length, double angle) throws CMLException {

		Point3 thisPoint;
		// create sets of atoms with and without ligands
		CMLAtomSet noCoordsLigandsAS = new CMLAtomSet();
		if (atom.getX3Attribute() != null) {
			return noCoordsLigandsAS;
		} else {
			thisPoint = atom.getXYZ3();
		}
		CMLAtomSet coordsLigandsAS = new CMLAtomSet();

		// atomSet containing atoms without coordinates
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			if (ligandAtom.getX3Attribute() != null) {
				noCoordsLigandsAS.addAtom(ligandAtom);
			} else {
				coordsLigandsAS.addAtom(ligandAtom);
			}
		}
		int nWithoutCoords = noCoordsLigandsAS.size();
		int nWithCoords = coordsLigandsAS.size();
		if (geometry == Molutils.DEFAULT) {
			geometry = atom.getLigandAtoms().size();
		}

		// too many ligands at present
		if (nWithCoords > 3) {
			CMLAtomSet emptyAS = new CMLAtomSet();
			// FIXME??
			return emptyAS;
			// nothing needs doing
		} else if (nWithoutCoords == 0) {
			return noCoordsLigandsAS;
		}

		List<Point3> newPoints = null;
		List<CMLAtom> coordAtoms = coordsLigandsAS.getAtoms();
		List<CMLAtom> noCoordAtoms = noCoordsLigandsAS.getAtoms();
		if (nWithCoords == 0) {
			try {
				newPoints = Molutils.calculate3DCoordinates0(thisPoint,
						geometry, length);
			} catch (EuclidException je) {
				throw new CMLException(S_EMPTY + je);
			}
		} else if (nWithCoords == 1) {
			// ligand on A
			CMLAtom bAtom = (CMLAtom) coordAtoms.get(0);
			// does B have a ligand (other than A)
			CMLAtom jAtom = null;
			List<CMLAtom> bLigandList = bAtom.getLigandAtoms();
			for (CMLAtom bLigand : bLigandList) {
				if (!bLigand.equals(this)) {
					jAtom = bLigand;
					break;
				}
			}
			try {
				newPoints = Molutils.calculate3DCoordinates1(thisPoint, bAtom
						.getXYZ3(), (jAtom != null) ? jAtom.getXYZ3() : null,
								geometry, length, angle);
			} catch (EuclidException je) {
				throw new CMLException(S_EMPTY + je);
			}
		} else if (nWithCoords == 2) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			try {
				newPoints = Molutils.calculate3DCoordinates2(thisPoint, bPoint,
						cPoint, geometry, length, angle);
			} catch (EuclidException je) {
				throw new CMLException(S_EMPTY + je);
			}
		} else if (nWithCoords == 3) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			Point3 dPoint = ((CMLAtom) coordAtoms.get(2)).getXYZ3();
			newPoints = new ArrayList<Point3>(1);
			newPoints.set(0, Molutils.calculate3DCoordinates3(thisPoint,
					bPoint, cPoint, dPoint, length));
		}
		int np = Math.min(noCoordsLigandsAS.size(), newPoints.size());
		for (int i = 0; i < np; i++) {
			((CMLAtom) noCoordAtoms.get(i)).setXYZ3(newPoints.get(i));
		}
		return noCoordsLigandsAS;
	}

	/**
	 * Expand implicit hydrogen atoms.
	 *
	 * This needs looking at
	 *
	 * CMLMolecule.NO_EXPLICIT_HYDROGENS CMLMolecule.USE_HYDROGEN_COUNT // no
	 * action
	 *
	 * @param atom
	 * @param control
	 * @throws CMLRuntimeException
	 */
	public void expandImplicitHydrogens(CMLAtom atom,
			CMLMolecule.HydrogenControl control) throws CMLRuntimeException {
		if (control.equals(CMLMolecule.HydrogenControl.USE_HYDROGEN_COUNT)) {
			return;
		}
		if (atom.getHydrogenCountAttribute() == null
				|| atom.getHydrogenCount() == 0) {
			return;
		}
		int hydrogenCount = atom.getHydrogenCount();
		int currentHCount = 0;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (ligand.getElementType().equals("H")) {
				currentHCount++;
			}
		}
		// FIXME. This needs rethinking
		if (control.equals(CMLMolecule.HydrogenControl.NO_EXPLICIT_HYDROGENS)
				&& currentHCount != 0) {
			return;
		}
		String id = atom.getId();
		for (int i = 0; i < hydrogenCount - currentHCount; i++) {
			CMLAtom hatom = new CMLAtom(id + "_h" + (i + 1));
			molecule.addAtom(hatom);
			hatom.setElementType("H");
			CMLBond bond = new CMLBond(atom, hatom);
			molecule.addBond(bond);
			bond.setOrder("1");
		}
	}

	/**
	 * gets first bond which is not already a wedge/hatch.
	 *
	 * try to get an X-H or other acyclic bond if possible bond must have first
	 * atom equal to thisAtom so sharp end of bond can be managed
	 *
	 * @return the bond
	 */
	private CMLBond getFirstWedgeableBond(CMLAtom atom) {
		List<CMLAtom> atomList = atom.getLigandAtoms();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		CMLBond bond = null;
		for (int i = 0; i < ligandBondList.size(); i++) {
			CMLBond bondx = ligandBondList.get(i);
			CMLAtom atomx = atomList.get(i);
			if (bondx.getBondStereo() != null) {
				continue;
			}
			// select any H
			if (atomx.getElementType().equals("H")
					&& bondx.getAtom(0).equals(this)) {
				bond = bondx;
				break;
			}
		}
		if (bond == null) {
			for (int i = 0; i < atomList.size(); i++) {
				CMLBond bondx = ligandBondList.get(i);
				if (bondx.getBondStereo() != null) {
					continue;
				}
				// or any acyclic bond
				if (bondx.getCyclic().equals(CMLBond.ACYCLIC)
						&& bondx.getAtom(0).equals(atom)) {
					bond = bondx;
					break;
				}
			}
		}
		// OK, get first unmarked bond
		if (bond == null) {
			for (int i = 0; i < atomList.size(); i++) {
				CMLBond bondx = ligandBondList.get(i);
				if (bondx.getBondStereo() != null) {
					continue;
				}
				if (bondx.getAtom(0).equals(this)) {
					bond = bondx;
					break;
				}
			}
		}
		return bond;
	}

	/**
	 * uses atomParity to create wedge or hatch.
	 *
	 * @param atom
	 * @throws CMLRuntimeException
	 *             inconsistentencies in diagram, etc.
	 */
	public void addWedgeHatchBond(CMLAtom atom) throws CMLRuntimeException {
		CMLAtom[] atom4 = new CMLAtom[4];// getAtomRefs4();
		if (atom4 != null) {
			CMLBond bond = this.getFirstWedgeableBond(atom);
			int totalParity = 0;
			if (bond == null) {
				logger.info("Cannot find ANY free wedgeable bonds! "
						+ atom.getId());
			} else {
				final CMLAtomParity atomParity = (CMLAtomParity) atom
				.getFirstChildElement(CMLAtomParity.TAG, CML_NS);
				if (atomParity != null) {

					CMLAtom[] atomRefs4x = atomParity.getAtomRefs4(molecule);
					int atomParityValue = atomParity.getIntegerValue();
					// three explicit ligands
					if (atomRefs4x[3].equals(this)) {
						double d = getSenseOf3Ligands(atom, atomRefs4x);
						if (Math.abs(d) > 0.000001) {
							int sense = (d < 0) ? -1 : 1;
							totalParity = sense * atomParityValue;
						}
						// 4 explicit ligands
					} else {
						CMLAtom[] cyclicAtom4 = getClockwiseLigands(atom,
								atomRefs4x);
						// bond position matters here. must find bond before
						// compareAtomRefs4
						// as it scrambles the order
						int serial = -1;
						for (int i = 0; i < 4; i++) {
							if (cyclicAtom4[i].equals(bond.getOtherAtom(atom))) {
								serial = i;
								break;
							}
						}
						if (serial == -1) {
							throw new CMLRuntimeException(
									"Cannot find bond in cyclicAtom4 (bug)");
						}
						// how does this differ from the original atomRefs4?
						int sense = 1; // FIXME
						// CMLAtom.compareAtomRefs4(atomRefs4x,
						// cyclicAtom4);
						int positionParity = 1 - 2 * (serial % 2);
						totalParity = sense * atomParityValue * positionParity;
					}
					String bondType = (totalParity > 0) ? CMLBond.WEDGE
							: CMLBond.HATCH;
					CMLBondStereo bondStereo = new CMLBondStereo();
					bondStereo.setXMLContent(bondType);
					bond.addBondStereo(bondStereo);

				} else {
					// throw new CMLException ("BUG :: AddWedgeHatchBond ->
					// atomParity is null for atom " + atom.getId ());
				}
			}
		}
	}

	// FIXME
	/**
	 * not fully written.
	 *
	 * @param array
	 * @param atom
	 * @return value
	 */
	double getSenseOf3Ligands(CMLAtom atom, CMLAtom[] array) {
		return 0;
	}

	/**
	 * gets atomSet for ligand in prioritized order.
	 *
	 * ligand is expanded as a breadth-first atomSet. This is ordered so that
	 * atoms appear in CIP order. (stereochemistry within ligand is ignored as
	 * too dificult at this stage, so full CIP is not supported). the set for
	 * each ligand can be retrieved by getLigandAtomSet(ligandAtoms). Where
	 * ligands are cyclic the set may continue until it reaches thisatom or
	 * ligandAtoms. thus in some cases the whole molecule except this atom may
	 * be returned
	 *
	 * @param atom
	 * @param ligandAt
	 * @return the atomSet (icludes the immediate ligand atom, but not
	 *         thisatom).
	 */
	CMLAtomSet getPrioritizedLigand(CMLAtom atom, CMLAtom ligandAt) {
		CMLAtomSet[] prioritizedLigands = this.getPrioritizedLigands(atom);
		CMLAtomSet atomSet = null;
		if (ligandAt != null) {
			this.getPrioritizedLigands(atom);
			for (int i = 0; i < prioritizedLigands.length; i++) {
				List<CMLAtom> ligandList = atom.getLigandAtoms();
				if (ligandAt.equals(ligandList.get(i))) {
					atomSet = prioritizedLigands[i];
					break;
				}
			}
		}
		return atomSet;
	}

	/**
	 * gets all ligandAtomSets for atom.
	 *
	 * getLigandAtomSet(ligandAtom) is called for each ligand the results are
	 * returned *in ligand order*, not in priority, so they can be synchronized
	 * with ligandAtoms[] and ligandBonds[]
	 *
	 * @param atom
	 * @return ligands as array of atomSets
	 */
	CMLAtomSet[] getPrioritizedLigands(CMLAtom atom) {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		CMLAtomSet[] prioritizedLigands = new CMLAtomSet[ligands.size()];
		for (int i = 0; i < prioritizedLigands.length; i++) {
			CMLAtom atomi = ligands.get(i);
			prioritizedLigands[i] = this.getPrioritizedLigand(atom, atomi);
			if (prioritizedLigands[i] == null) {
				CMLAtomSet atomSet = new CMLAtomSet();
				this.createPrioritizedLigand(atom, atomSet, atomi);
				prioritizedLigands[i] = atomSet;
			}
		}
		return prioritizedLigands;
	}

	/**
	 * not fully developed.
	 *
	 * @deprecated
	 * @param atom
	 * @param atomSetTool
	 * @param upstreamAtom
	 */
	void createPrioritizedLigand(CMLAtom atom, CMLAtomSet atomSetTool,
			CMLAtom upstreamAtom) {

		// CMLAtom ligands
	}

	/**
	 * get ligands of this atom not in markedAtom set sorted in decreasing
	 * atomic number.
	 *
	 * sort the unvisisted atoms in decreasing atomic number. If atomic numbers
	 * are tied, any order is permitted
	 *
	 * @param atom
	 * @param markedAtoms
	 *            atoms already visited and not to be included
	 *
	 * @return the new ligands in decreasing order of atomic mass.
	 */
	CMLAtom[] getNewLigandsSortedByAtomicNumber(CMLAtom atom,
			CMLAtomSet markedAtoms) {
		List<CMLAtom> newLigandVector = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			if (!markedAtoms.contains(ligandAtom)) {
				newLigandVector.add(ligandAtom);
			}
		}
		CMLAtom[] newLigands = new CMLAtom[newLigandVector.size()];
		int count = 0;
		while (newLigandVector.size() > 0) {
			int heaviest = getHeaviestAtom(newLigandVector);
			CMLAtom heaviestAtom = newLigandVector.get(heaviest);
			newLigands[count++] = heaviestAtom;
			newLigandVector.remove(heaviest);
		}
		return newLigands;
	}

	/**
	 * get priority relative to other atom.
	 *
	 * uses simple CIP prioritization (does not include stereochemistry) if
	 * thisAtom has higher priority return 1 if thisAtom and otherAtom have
	 * equal priority return 0 if otherAtom has higher priority return -1
	 *
	 * compare atomic numbers. if thisAtom has higher atomicNumber return 1 if
	 * otherAtom has higher atomicNumber return -1 if equal, visit new ligands
	 * of each atom, sorted by priority until a mismatch is found or the whole
	 * of the ligand trees are visited
	 *
	 * example: CMLAtomSet thisSetTool = new AtomSetToolImpl(); CMLAtomSet
	 * otherSetTool = new AtomSetToolImpl(); int res =
	 * atomTool.compareByAtomicNumber(otherAtom, thisSetTool, otherSetTool);
	 *
	 * @param atom
	 * @param otherAtom
	 * @param markedAtoms
	 *            atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 * @param otherMarkedAtoms
	 *            atomSet to keep track of visited atoms (avoiding infinite
	 *            recursion)
	 *
	 * @return int the comparison
	 */
	// FIXME
	public int compareByAtomicNumber(CMLAtom atom, CMLAtom otherAtom,
			CMLAtomSet markedAtoms, CMLAtomSet otherMarkedAtoms) {
		// compare on atomic number
		int comp = atom.compareByAtomicNumber(otherAtom);
		if (comp == 0) {
			markedAtoms.addAtom(atom);
			otherMarkedAtoms.addAtom(otherAtom);
			CMLAtom[] thisSortedLigands = getNewLigandsSortedByAtomicNumber(
					atom, markedAtoms);
			CMLAtom[] otherSortedLigands = getNewLigandsSortedByAtomicNumber(
					otherAtom, otherMarkedAtoms);
			int length = Math.min(thisSortedLigands.length,
					otherSortedLigands.length);
			for (int i = 0; i < length; i++) {
				CMLAtom thisLigand = thisSortedLigands[i];
				comp = compareByAtomicNumber(thisLigand, otherSortedLigands[i],
						markedAtoms, otherMarkedAtoms);
				if (comp != 0) {
					break;
				}
			}
			if (comp == 0) {
				// if matched so far, prioritize longer list
				if (thisSortedLigands.length > otherSortedLigands.length) {
					comp = 1;
				} else if (thisSortedLigands.length < otherSortedLigands.length) {
					comp = -1;
				}
			}
		}
		return comp;
	}

	private int getHeaviestAtom(List<CMLAtom> newAtomVector) {
		int heaviestAtNum = -1;
		int heaviest = -1;
		for (int i = 0; i < newAtomVector.size(); i++) {
			CMLAtom atom = newAtomVector.get(i);
			int atnum = atom.getAtomicNumber();
			if (atnum > heaviestAtNum) {
				heaviest = i;
				heaviestAtNum = atnum;
			}
		}
		return heaviest;
	}

	/**
	 * gets lone electrons on atom. electrons not involved in bonding assumes
	 * accurate hydrogen count only currently really works for first row (C, N,
	 * O, F) calculated as getHydrogenValencyGroup() -
	 * (getSumNonHydrogenBondOrder() + getHydrogenCount()) -
	 * atom.getFormalCharge()
	 *
	 * @param atom
	 * @return number of lone electrons (< 0 means cannot calculate)
	 */
	public int getLoneElectronCount(CMLAtom atom) {
		int loneElectronCount = -1;
		int group = this.getHydrogenValencyGroup(atom);
		if (group == -1) {
			return -1;
		}
		int sumNonHBo = this.getSumNonHydrogenBondOrder(atom);
		int nHyd = atom.getHydrogenCount();
		int formalCharge = 0;
		if (atom.getFormalChargeAttribute() != null) {
			formalCharge = atom.getFormalCharge();
		}
		loneElectronCount = group - (sumNonHBo + nHyd) - formalCharge;
		return loneElectronCount;
	}

	@SuppressWarnings("unused")
	void getHybridizationFromConnectivty() {
		@SuppressWarnings("unused")
		// FIXME
		List<CMLAtom> atoms = molecule.getAtoms();
		// process atoms first. known terminal atoms or atoms with maximum
		// connectivity
		// gives precise information;
		int valence[] = new int[atoms.size()];
		int i = -1;
		for (CMLAtom atom : molecule.getAtoms()) {
			i++;
			int bondOrder = -1;
			int ligandCount = atom.getLigandAtoms().size();
			String elType = null;
			try {
				elType = atom.getElementType();
			} catch (Exception e) {
				logger.severe("BUG " + e);
			}
			valence[i] = -1;
			if ("H".equals(elType) || "F".equals(elType) || "Cl".equals(elType)
					|| "Br".equals(elType) || "I".equals(elType)) {
				valence[i] = 1;
			} else if ("O".equals(elType) || "S".equals(elType)) {
				valence[i] = 2;
			} else if ("N".equals(elType)) {
				valence[i] = 3;
			} else if ("C".equals(elType)) {
				valence[i] = 4;
			}
			int valLig = valence[i] - ligandCount;
			// FIXME many times!
			if (valLig >= 0) {
				if (valence[i] < 3) {
					if (ligandCount == 1 || valLig == 0) {
						// bondOrder = orderX[valLig];
					}
					// atom.setGeometricHybridization(spX[valLig]);
				} else if (valence[i] == 4) {
					if (ligandCount == 4) {
						// bondOrder = orderX[valLig];
						// atom.setGeometricHybridization(spX[valLig]);
					}
				}
			}
			if (bondOrder == -1) {
				continue;
			}
			// set hybridization and orders for ligand bonds for (
			// LigandIterator it = atom.getLigandIterator();
			// it.hasNext();
			// )
			{
				// CMLAtom ligand = (CMLAtom) it.next();
				// FIXME
				CMLAtom ligand = null;
				@SuppressWarnings("unused")
				CMLBond bond = molecule.getBond(atom, ligand);
				// FIXME
				// order = bond.getOrder();
			}
			if (bondOrder == -1) {

			}
			// bond.setOrder(S_EMPTY+bondOrder);
			//
			// bond.orderKnown = true;
		}

		boolean change = true;
		while (change) {
			change = false;
			// iterate using the known bond orders and filling in any gaps...
			i = -1;
			for (CMLAtom atom : atoms) {
				i++;
				if (valence[i] < 0) {
					continue;
				}
				int bondSum = 0;
				int aromSum = 0;
				CMLBond missingBond[] = new CMLBond[atoms.size()];
				int nMissing = 0;
				for (CMLAtom atom1 : atoms) {
					// CMLAtom ligand = (CMLAtom) it1.next();
					CMLAtom ligand = null;
					CMLBond bond = molecule.getBond(atom1, ligand);
					String order = null;
					try {
						order = bond.getOrder();
					} catch (Exception e) {
						logger.severe("BUG " + e);
					}
					if (order.equals(CMLBond.UNKNOWN_ORDER)) {
						missingBond[nMissing++] = bond;
					} else {
						if (order.equals(CMLBond.SINGLE)) {
							bondSum++;
						} else if (order.equals(CMLBond.DOUBLE)) {
							bondSum += 2;
						} else if (order.equals(CMLBond.TRIPLE)) {
							bondSum += 3;
						} else if (order.equals(CMLBond.AROMATIC)) {
							aromSum++;
						}
					}
				}
				change = assignMissingBonds(valence, i, bondSum, aromSum,
						missingBond, nMissing);
			}
		}
	}

	private boolean assignMissingBonds(int[] valence, int i, int bondSum,
			int aromSum, CMLBond[] missingBond, int nMissing) {
		boolean change = false;
		if (nMissing > 0) {
			if (aromSum == 0) {
				int delta = valence[i] - bondSum;
				if (nMissing == delta) {
					for (int j = 0; j < nMissing; j++) {
						try {
							missingBond[j].setOrder(CMLBond.SINGLE);
						} catch (Exception e) {
							logger.severe("BUG " + e);
						}
					}
					change = true;
				} else if (nMissing == 1) {
					String newOrder = null;
					if (delta == 1) {
						newOrder = CMLBond.SINGLE;
					} else if (delta == 2) {
						newOrder = CMLBond.DOUBLE;
					} else if (delta == 3) {
						newOrder = CMLBond.TRIPLE;
					} else if (newOrder != null) {
						try {
							missingBond[0].setOrder(newOrder);
						} catch (Exception e) {
							logger.severe("BUG " + e);
						}
						change = true;
					}
				}
			}
		}
		return change;
	}

	// =============== ATOMSET =========================
	/**
	 * gets bondset for all bonds in current atom set. slow order of bond is
	 * undetermined but probably in atom document order and iteration through
	 * ligands
	 *
	 * @param atomSet
	 * @return the bondSet
	 */
	public CMLBondSet getBondSet(CMLAtomSet atomSet) {
		// List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> atoms = atomSet.getAtoms();
		molecule.getBonds();
		CMLBondSet bondSet = new CMLBondSet();
		for (CMLAtom atom : atoms) {
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			List<CMLBond> ligandBondList = atom.getLigandBonds();
			// loop through ligands and examine each for membership of this set;
			// if so add bond
			int i = 0;
			for (CMLAtom ligand : ligandList) {
				CMLBond ligandBond = ligandBondList.get(i++);
				if (bondSet.contains(ligandBond)) {
					continue;
				}
				if (atomSet.contains(ligand)) {
					bondSet.addBond(ligandBond);
				}
			}
		}
		return bondSet;
	}

	/**
	 * Creates new Molecule one ligand shell larger than the atomSet.
	 *
	 * @param atomSet
	 *            to sprout from (must all be in this molecule).
	 * @return new Molecule (or null if no atoms)
	 */
	public CMLMolecule sprout(CMLAtomSet atomSet) {
		CMLMolecule newMolecule = null;
		if (atomSet.getAtoms().size() > 0) {
			AtomSetTool atomSetTool = new AtomSetTool(atomSet);
			CMLAtomSet newAtomSet = atomSetTool.sprout();
			CMLMolecule molecule = atomSet.getMolecule();
			CMLBondSet newBondSet = new MoleculeTool(molecule)
			.getBondSet(newAtomSet);
			newMolecule = new CMLMolecule(newAtomSet, newBondSet);
		}
		return newMolecule;
	}

	/**
	 * create a cluster of connected atoms of given types.
	 *
	 * @param typeList
	 *            list of elements allowed
	 * @return list of clusters (or empty list)
	 */
	public List<CMLMolecule> createClusters(List<ChemicalElement.Type> typeList) {
		List<CMLMolecule> clusterList = new ArrayList<CMLMolecule>();
		if (molecule.getMoleculeCount() > 0) {
			for (CMLMolecule subMolecule : molecule.getMoleculeElements()) {
				MoleculeTool subMoleculeTool = new MoleculeTool(subMolecule);
				List<CMLMolecule> subClusterList = subMoleculeTool
				.createClusters(typeList);
				for (CMLMolecule subCluster : subClusterList) {
					clusterList.add(subCluster);
				}
			}
		} else {
			CMLAtomSet unusedAtomSet = molecule.getAtomSet();
			while (unusedAtomSet.size() > 0) {
				CMLAtomSet clusterSet = new CMLAtomSet();
				CMLBondSet clusterBondSet = new CMLBondSet();
				expandCluster(clusterSet, clusterBondSet, unusedAtomSet,
						typeList);
				if (clusterSet.size() > 0) {
					CMLMolecule clusterMolecule = new CMLMolecule(clusterSet);
					clusterList.add(clusterMolecule);
					molecule.addToLog(Severity.INFO, "NEW CLUSTER SIZE "
							+ clusterSet.size());
				}
			}
		}
		return clusterList;
	}

	private boolean expandCluster(CMLAtomSet clusterSet,
			CMLBondSet clusterBondSet, CMLAtomSet unusedAtomSet,
			List<Type> typeList) {
		boolean change = false;
		List<CMLAtom> unusedAtomList = unusedAtomSet.getAtoms();
		for (CMLAtom currentAtom : unusedAtomList) {
			unusedAtomSet.removeAtom(currentAtom);
			if (sproutAtom(currentAtom, clusterSet, clusterBondSet,
					unusedAtomSet, typeList)) {
				change = true;
				break;
			}
		}
		return change;
	}

	private boolean sproutAtom(CMLAtom currentAtom, CMLAtomSet clusterSet,
			CMLBondSet clusterBondSet, CMLAtomSet unusedAtomSet,
			List<Type> typeList) {
		boolean change = false;
		if (atomIsCompatible(currentAtom, typeList)) {
			change = true;
			if (!clusterSet.contains(currentAtom)) {
				clusterSet.addAtom(currentAtom);
			}
			ChemicalElement element1 = ChemicalElement
			.getChemicalElement(currentAtom.getElementType());
			double radius1 = element1.getCovalentRadius();
			List<CMLAtom> ligandList = currentAtom.getLigandAtoms();
			List<CMLBond> ligandBondList = currentAtom.getLigandBonds();
			int lig = 0;
			for (CMLAtom ligand : ligandList) {
				// atom already used?
				if (!unusedAtomSet.contains(ligand)) {
					continue;
				}
				// atom not of right kind
				if (!atomIsCompatible(ligand, typeList)) {
					continue;
				}
				ChemicalElement element2 = ChemicalElement
				.getChemicalElement(ligand.getElementType());
				double radius2 = element2.getCovalentRadius();
				boolean bonded = CMLBond.areWithinBondingDistance(currentAtom,
						ligand, radius1, radius2, ChemicalElement
						.getBondingRadiusTolerance());
				if (bonded) {
					clusterSet.addAtom(ligand);
					CMLBond addBond = ligandBondList.get(lig);
					if (!clusterBondSet.contains(addBond)) {
						clusterBondSet.addBond(addBond);
					}
					// change = true;
				}
				lig++;
			}
		}
		return change;
	}

	/**
	 * is atom of given type. only does TM at present
	 *
	 * @param atom
	 *            to inspect
	 * @param typeList
	 * @return true if of type
	 */
	private boolean atomIsCompatible(CMLAtom atom, List<Type> typeList) {
		boolean isCompatible = false;
		ChemicalElement chemicalElement = ChemicalElement
		.getChemicalElement(atom.getElementType());
		for (Type type : typeList) {
			if (type != null && MoleculeTool.isChemicalElementType(chemicalElement, type)) {
				isCompatible = true;
			}
		}
		return isCompatible;
	}

	/**
	 * extract ligands from connected molecule components.
	 *
	 * @param typeList
	 *            list of elements allowed
	 * @return list of clusters (or empty list)
	 */
	public List<CMLMolecule> createLigands(List<ChemicalElement.Type> typeList) {
		new ConnectionTableTool(molecule).partitionIntoMolecules();
		List<CMLMolecule> ligandList = new ArrayList<CMLMolecule>();
		for (CMLMolecule splitMol : this.getMoleculeList()) {
			CMLMolecule subMolecule = new CMLMolecule(splitMol);
			subMolecule.setId("NEW_" + subMolecule.getId());
			boolean deleted = false;
			List<CMLAtom> atomList = subMolecule.getAtoms();
			for (CMLAtom atom : atomList) {
				if (atomIsCompatible(atom, typeList)) {
					deleted = true;
					subMolecule.deleteAtom(atom);
				}
			}
			if (deleted) {
				new ConnectionTableTool(subMolecule).partitionIntoMolecules();
				List<CMLMolecule> ligands = new MoleculeTool(subMolecule)
				.getMoleculeList();
				for (CMLMolecule ligand : ligands) {
					ligandList.add(ligand);
				}
			}
		}
		return ligandList;
	}

	/**
	 * Creates new Molecule one ligand shell larger than this one. uses
	 * molecule.getAtomSet()
	 *
	 * @see #sprout(CMLAtomSet)
	 * @return new Molecule
	 */
	public CMLMolecule sprout() {
		return sprout(molecule.getAtomSet());
	}

	// ====================== BOND ============

	/**
	 * get substituent ligands of one end of bond.
	 *
	 * gets all substituent atoms of atom (but not otherAtom in bond)
	 *
	 * @param bond
	 * @param atom
	 *            at one end of bond
	 * @throws CMLException
	 *             atom is not in bond
	 * @return the list of substituent atoms
	 */
	List<CMLAtom> getSubstituentLigandList(CMLBond bond, CMLAtom atom)
	throws CMLException {
		CMLAtom otherAtom = bond.getOtherAtom(atom);
		List<CMLAtom> substituent = new ArrayList<CMLAtom>(atom
				.getLigandAtoms().size() - 1);
		int count = 0;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!ligand.equals(otherAtom)) {
				substituent.set(count++, ligand);
			}
		}
		return substituent;
	}

	/**
	 * gets four atoms defining cis/trans isomerism.
	 *
	 * selects 4 atoms lig0-atom0-atom1-lig1, where atom0 and atom1 are the
	 * atoms in the bond and lig0 and lig1 are atoms bonded to each end
	 * respectively Returns null unless atom0 and atom 1 each have 1 or 2
	 * ligands (besides themselves) if atom0 and/or atom1 have 2 ligands the
	 * selection is deterministic but arbitrary - hydrogens are not used if
	 * possible - normally the first Id in lexical order (i.e. no implied
	 * semantics). Exceptions are thrown if ligands are linear with atom0-atom1
	 * or 2 ligands on same atom are on the same side these may be normal
	 * exceptions (e.g. in a C=O bond) so should be caught and analysed rather
	 * than dumping the program
	 *
	 * @param bond
	 * @exception CMLException
	 *                2 ligand atoms on same atom on same side too few or too
	 *                many ligands at either end (any) ligand is linear with
	 *                bond
	 * @return the four atoms
	 */
	CMLAtom[] getAtomRefs4(CMLBond bond) throws CMLException {
		List<CMLAtom> atomList = molecule.getAtoms();
		List<CMLAtom> ligands0 = getSubstituentLigandList(bond, atomList.get(0));
		List<CMLAtom> ligands1 = getSubstituentLigandList(bond, atomList.get(1));
		if (ligands0.size() == 0) {
			throw new CMLException("Too few ligands on atom: "
					+ atomList.get(0).getId());
		}
		if (ligands1.size() == 0) {
			throw new CMLException("Too few ligands on atom: "
					+ atomList.get(1).getId());
		}
		if (ligands0.size() > 2) {
			throw new CMLException("Too many ligands on atom: "
					+ atomList.get(0).getId());
		}
		if (ligands1.size() > 2) {
			throw new CMLException("Too many ligands on atom: "
					+ atomList.get(1).getId());
		}
		CMLAtom ligand0 = ligands0.get(0);
		if (ligand0.getElementType().equals("H") && ligands0.size() > 1
				&& ligands0.get(1) != null) {
			ligand0 = ligands0.get(1);
		} else if (ligands0.size() > 1
				&& ligands0.get(1).getId().compareTo(atomList.get(0).getId()) < 0) {
			ligand0 = ligands0.get(1);
		}
		CMLAtom ligand1 = ligands1.get(0);
		if (ligand1.getElementType().equals("H") && ligands1.size() > 1
				&& ligands1.get(1) != null) {
			ligand1 = ligands1.get(1);
		} else if (ligands1.size() > 1
				&& ligands1.get(1).getId().compareTo(atomList.get(1).getId()) < 0) {
			ligand1 = ligands1.get(1);
		}
		CMLAtom[] atom4 = new CMLAtom[4];
		atom4[0] = ligand0;
		atom4[1] = atomList.get(0);
		atom4[2] = atomList.get(1);
		atom4[3] = ligand1;
		return atom4;
	}

	/**
	 * gets atoms on one side of bond. only applicable to acyclic bonds if bond
	 * is cyclic, whole molecule will be returned! returns atom and all
	 * descendant atoms.
	 *
	 * @param bond
	 * @param atom
	 *            defining side of bond
	 * @throws CMLException
	 *             atom is not in bond
	 * @return atomSet of downstream atoms
	 */
	public CMLAtomSet getDownstreamAtoms(CMLBond bond, CMLAtom atom) {
		CMLAtomSet atomSet = new CMLAtomSet();
		CMLAtom otherAtom = bond.getOtherAtom(atom);
		if (otherAtom != null) {
			atomSet = getDownstreamAtoms(otherAtom, atom);
		}
		return atomSet;
	}

	// ==================== MOLECULE ====================

	/**
	 * add suffix to atom IDs.
	 *
	 * Add a distinguishing suffix to all atom IDs this allows multiple copies
	 * of a fragment in a molecule
	 *
	 * @param suffix
	 * @throws CMLException
	 */
	public void addSuffixToAtomIDs(String suffix) {
		for (CMLAtom atom : molecule.getAtoms()) {
			atom.renameId(atom.getId() + suffix);
		}
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 *
	 * does not use CDK
	 *
	 * @param control
	 *            specifies whether H are explicit or in hydorgenCount
	 */
	public void adjustHydrogenCountsToValency(HydrogenControl control) {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			this.adjustHydrogenCountsToValency(atom, control);
		}
	}

	/**
	 * dereference moleculeRef. will not process any args at this stage
	 *
	 * @param molRef
	 *            of form "abc:def"
	 * @return the molecule or null
	 */
	/*--
	     public static CMLMolecule dereferenceMolecule(String molRef) {
	     CMLMolecule molecule = null;
	     return molecule;
	     }
	     --*/

	/**
	 * try to distribute molecular charge over quaternary nitrogens Empirical!
	 *
	 * Interacts with adjustBondOrdersToValency() so these may be run in
	 * different order and iteratively. only run if molecule.formalCharge set
	 * adds +1 to each N.formalCharge where: (i) sum bond order = 4 (ii) not
	 * coordinated to unusual elements (NYI)
	 */
	public void distributeMolecularChargeToN4() {
		if (molecule.getFormalChargeAttribute() != null) {
			List<CMLAtom> atoms = molecule.getAtoms();
			MoleculeTool moleculeTool = new MoleculeTool(molecule);
			for (CMLAtom atom : atoms) {
				if (!"N".equals(atom.getElementType())) {
					continue;
				}
				if (atom.getFormalChargeAttribute() == null) {
					if (moleculeTool.getBondOrderSum(atom) == 4) {
						atom.setFormalCharge(1);
						// molecule.setFormalCharge(molecule.getFormalCharge() -
						// 1);
					}
				}
			}
		}
	}

	/**
	 * transfers molecular charge to atoms with free electrons. Double bond
	 * analysis may leave some atoms with unpaired pi electrons. This assumes
	 * they are negative or positive and transfers charge
	 */
	public void transferChargeToFreePiElectrons() {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			// has atom got free pi electrons after PiSystem analysis?
			CMLElements<CMLElectron> electrons = atom.getElectronElements();
			for (CMLElectron electron : electrons) {
				if (CMLElectron.PI.equals(electron.getDictRef())) {
					electron.detach();
					if (atom.getFormalChargeAttribute() == null) {
						atom.setFormalCharge(0);
					}
					if ("N".equals(atom.getElementType())) {
						atom.setFormalCharge(atom.getFormalCharge() + 1);
					}
					if ("O".equals(atom.getElementType())) {
						atom.setFormalCharge(atom.getFormalCharge() - 1);
					}
				}
			}
			// has carbon got missing ligands? add charge
			if ("C".equals(atom.getElementType())) {
				if (this.getBondOrderSum(atom) == 3) {
					if (atom.getFormalChargeAttribute() == null
							|| atom.getFormalCharge() == 0) {
						atom.setFormalCharge(1);
					}
				}
			}
		}
	}

	/**
	 * adds charges and bond orders for common species.
	 * 
	 * @return true if identified as common molecule
	 */
	public boolean markupCommonMolecules() {
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		String formulaS = formula.getConcise();
		formulaS = CMLFormula.removeChargeFromConcise(formulaS);
		molecule.addToLog(Severity.INFO, "Formula " + formulaS);
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

	private void addCharge(String elementType, int charge) {
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			if (atom.getElementType().equals(elementType)) {
				atom.setFormalCharge(charge);
			}
		}
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
		centralA.setFormalCharge(centralCharge);
		int count = 0;
		for (CMLAtom atom : atoms) {
			if (ligandS.equals(atom.getElementType())) {
				int bos = this.getBondOrderSum(atom);
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

	/**
	 * special routines. mark common groups with charges and 
	 * bond orders
	 * 
	 */
	public void markupSpecial() {
		CMLFormula formula = new CMLFormula(molecule);
		formula.normalize();
		String formulaS = formula.getConcise();
		formulaS = CMLFormula.removeChargeFromConcise(formulaS);

		List<CMLAtom> atoms = molecule.getAtoms();
		markCarboxyAnion(atoms);
		markCS2(atoms);
		markCOS(atoms);
		markNitro(atoms);
		markPAnion(atoms);
		markSulfo(atoms);
		markTerminalCarbyne(atoms);
		mark_CSi_anion(atoms);
		markQuaternaryBAlGaIn(atoms);
		markQuaternaryNPAsSb(atoms);
		markTerminalCN(atoms);
		markOSQuatP(atoms);
		markAzide(atoms);
		markM_PN_C(atoms);
		markMCN(atoms);
		markMNN(atoms);
		markPNP(atoms);
		markMCC(atoms);
		markSandwichLigands(atoms);
		int count = atoms.size();
		if (count == 2) {
			markMetalCarbonylAndNitrile(atoms);
		}
		if (count == 3) {
			markN3(atoms);
			markThiocyanate(atoms);
			markNCN(atoms);
		}
		if (formulaS.equals("C 2 N 3")) {
			markNCNCN(atoms);
		}
	}
	
	private void markMCC(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (isBondedToMetal(atom) && "C".equals(atom.getElementType()) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					atom.getLigandBonds().get(0).setOrder(CMLBond.DOUBLE);
					atom.setFormalCharge(-2);
				}
			}
		}
	}
	
	private void markMNN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (isBondedToMetal(atom) && "N".equals(atom.getElementType()) 
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
	
	private void markPNP(List<CMLAtom> atoms) {
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
	
	private void markMCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (isBondedToMetal(atom) && "C".equals(atom.getElementType()) 
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
	
	private void markM_PN_C(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if (isBondedToMetal(atom) && ("N".equals(atom.getElementType()) || "P".equals(atom.getElementType())) 
					&& atom.getLigandAtoms().size() == 1) {
				CMLAtom ligand = atom.getLigandAtoms().get(0);
				if ("C".equals(ligand.getElementType()) && ligand.getLigandAtoms().size() == 3) {
					atom.setFormalCharge(-2);
				}
			}
		}
	}
	
	private void markSandwichLigands(List<CMLAtom> atoms) {
		CMLAtomSet atomSet = new CMLAtomSet(atoms);
		CMLBondSet bondSet = this.getBondSet(atomSet);
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
				if (isBondedToMetal(atom)) {
					count++;
					ringAtomList.add(atom);
				}
			}
			if (count > 1 && count % 2 == 1) {
				molecule.getAtomById(ringAtomList.get(0).getId()).setFormalCharge(-1);
			}
		}
	}
	

	private void markAzide(List<CMLAtom> atoms) {
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

	private void markQuaternaryNPAsSb(List<CMLAtom> atoms) {
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
	private void markOSQuatP(List<CMLAtom> atoms) {
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

	private void markTerminalCN(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 1) {
				CMLAtom lig = atom.getLigandAtoms().get(0);
				if ("C".equals(lig.getElementType()) && lig.getLigandAtoms().size() == 2) {
					atom.getLigandBonds().get(0).setOrder(CMLBond.TRIPLE);
					containsEWithdrawingGroup = true;
				}
			}
		}
	}

	private void markNCNCN(List<CMLAtom> atoms) {
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

	private void markQuaternaryBAlGaIn(List<CMLAtom> atoms) {
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

	private void markNCN(List<CMLAtom> atoms) {
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

	private void markN3(List<CMLAtom> atoms) {
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
	private void markCarboxyAnion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
						oxyList.add(ligand);
					}
				}
				marker:
				if (oxyList.size() == 2) {
					for (CMLAtom oAtom : oxyList) {
						if (isBondedToMetal(oAtom)) {
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
	private void markMetalCarbonylAndNitrile(List<CMLAtom> atoms) {
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

	private void markTerminalCarbyne(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				if (isBondedToMetal(atom)) {
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

	private boolean isBondedToMetal(CMLAtom atom) {
		Nodes nodes = atom.query(".//cml:scalar[@dictRef='"+metalLigandDictRef+"']", X_CML);
		if (nodes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void mark_CSi_anion(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType()) || "Si".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				if (ligands.size() == 3 && isBondedToMetal(atom)) {
					boolean set = true;
					for (CMLAtom ligand : ligands) {
						if (isBondedToMetal(ligand)) {
							set = false;
						}
					}
					if (set) {
						atom.setFormalCharge(-1);
					}
				}
				// if carbon has only two ligands then check to see if it
				// can be marked as a carbanion.
				if (ligands.size() == 2  && isBondedToMetal(atom)) {
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
										System.out.println("**hellO&&&");
										System.out.println(at.getId());
										continue;
									}
									if (!"N".equals(at.getElementType())) {
										System.out.println("setting false");
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
	private void markNitro(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("N".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
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
					containsEWithdrawingGroup = true;
				}
			}
		}
	}

	private void markPAnion(List<CMLAtom> atoms) {
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
	private void markSulfo(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("S".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> oxyList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("O".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
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
	private void markCS2(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				List<CMLAtom> ligands = atom.getLigandAtoms();
				List<CMLAtom> sulfoList = new ArrayList<CMLAtom>();
				for (CMLAtom ligand : ligands) {
					if ("S".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
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

	private void markCOS(List<CMLAtom> atoms) {
		for (CMLAtom atom : atoms) {
			if ("C".equals(atom.getElementType())) {
				CMLAtom o = null;
				CMLAtom s = null;
				List<CMLAtom> ligands = atom.getLigandAtoms();
				for (CMLAtom ligand : ligands) {
					if ("S".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
						s = ligand;
					}
					if ("O".equals(ligand.getElementType())
							&& this.getBondOrderSum(ligand) == 1) {
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


	private void markThiocyanate(List<CMLAtom> atoms) {
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

	/**
	 * mark benzene (later pyridine, etc.) BUGGY!
	 *
	 */
	// FIXME
	@SuppressWarnings("unused")
	private void markMonocyclicBenzene() {
		List<CMLAtom> atoms = molecule.getAtoms();
		Set<CMLAtom> usedAtoms = new HashSet<CMLAtom>();
		for (CMLAtom atom : atoms) {
			if (usedAtoms.contains(atom)) {
				continue;
			}
			if ("C".equals(atom.getElementType())) {
				if (atom.getLigandAtoms().size() == 3) {
					Set<CMLAtom> ringSet = new HashSet<CMLAtom>();
					while (true) {
						if (!addNextAtom(atom, ringSet)) {
							break;
						}
					}
					if (ringSet.size() == 6) {
						for (CMLAtom rAtom : ringSet) {
							usedAtoms.add(rAtom);
						}
						addDoubleBonds(ringSet, atom);
					}
				}
			}
			usedAtoms.add(atom);
		}
	}

	private void addDoubleBonds(Set<CMLAtom> ringSet, CMLAtom atom) {
		boolean doubleB = true;
		while (ringSet.size() > 1) {
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			List<CMLBond> ligandBondList = atom.getLigandBonds();
			ringSet.remove(atom);
			int i = 0;
			for (CMLAtom ligand : ligandList) {
				if (ringSet.contains(ligand)) {
					if (doubleB) {
						CMLBond bond = ligandBondList.get(i);
						bond.setOrder(CMLBond.DOUBLE);
					}
					doubleB = !doubleB;
					atom = ligand;
					break;
				}
				i++;
			}
		}
	}

	private boolean addNextAtom(CMLAtom atom, Set<CMLAtom> ringSet) {
		if (atom == null) {
			throw new CMLRuntimeException("Null atom");
		}
		boolean added = false;
		if (!ringSet.contains(atom)) {
			added = true;
			ringSet.add(atom);
			List<CMLAtom> ligands = atom.getLigandAtoms();
			if ("C".equals(atom.getElementType()) && ligands.size() == 3) {
				List<CMLBond> bonds = atom.getLigandBonds();
				List<CMLAtom> cAtomList = new ArrayList<CMLAtom>();
				for (int i = 0; i < bonds.size(); i++) {
					CMLBond bond = bonds.get(i);
					if (CMLBond.CYCLIC.equals(bond.getCyclic())) {
						CMLAtom otherAtom = bond.getOtherAtom(atom);
						if (otherAtom == null) {
							System.out.println(bond.getString());
							throw new CMLRuntimeException("null atom in bond");
						}
						cAtomList.add(otherAtom);
					}
				}
				if (cAtomList.size() == 2) {
					for (CMLAtom cAtom : cAtomList) {
						addNextAtom(cAtom, ringSet);
					}
				}
			}
		}
		return added;
	}

	/**
	 * is element of given type.
	 *
	 * @param chemicalElement
	 * @param type
	 *            TRANSITION METAL, LANTHANIDE, ACTINIDE, METAL,
	 *            NON_METAL, PBLOCK, GROUP_A, GROUP_B
	 * @return true if of type
	 */
	public static boolean isChemicalElementType(
			ChemicalElement chemicalElement, Type type) {
		int atNum = chemicalElement.getAtomicNumber();
		// String symbol = chemicalElement.getSymbol();
		boolean isType = false;
		if (type.equals(Type.TRANSITION_METAL)) {
			isType = (atNum > 20 && atNum <= 30) ||
			(atNum > 38 && atNum <= 48) ||
			(atNum > 56 && atNum <= 80);
		} else if (type.equals(Type.LANTHANIDE)) {
			isType = (atNum >= 58 && atNum <= 71);
		} else if (type.equals(Type.ACTINIDE)) {
			isType = atNum >= 90 && atNum <= 103;
		} else if (type.equals(Type.METAL)) {
			isType = isChemicalElementType(chemicalElement, Type.TRANSITION_METAL) ||
			isChemicalElementType(chemicalElement, Type.LANTHANIDE) ||
			isChemicalElementType(chemicalElement, Type.ACTINIDE) ||
			isChemicalElementType(chemicalElement, Type.GROUP_A) ||
			isChemicalElementType(chemicalElement, Type.GROUP_B) ||
			// include metalloids on left of step
			(atNum == 13) ||
			(atNum >= 31 && atNum <= 32) ||
			(atNum >= 49 && atNum <= 51) ||
			(atNum >= 81 && atNum <=84);
		} else if (type.equals(Type.METAL_NOT_SEMI_METAL)) {
			isType = isChemicalElementType(chemicalElement, Type.TRANSITION_METAL) ||
			isChemicalElementType(chemicalElement, Type.LANTHANIDE) ||
			isChemicalElementType(chemicalElement, Type.ACTINIDE) ||
			isChemicalElementType(chemicalElement, Type.GROUP_A) ||
			isChemicalElementType(chemicalElement, Type.GROUP_B);
		} else if (type.equals(Type.NON_METAL)) {
			isType = atNum >=5 && atNum <= 10 ||
			atNum >=14 && atNum <= 18 ||
			atNum >=33 && atNum <= 36 ||
			atNum >=52 && atNum <= 54 ||
			atNum >=85 && atNum <= 86;
		} else if (type.equals(Type.PBLOCK)) {
			isType = atNum >= 5 && atNum <= 10 || // B, C, N, O, F,Ne
			atNum >= 14 && atNum <= 18 || // Si, P, S,Cl,Ar
			atNum >= 32 && atNum <= 36 || // Ge,As,Se,Br,Kr
			atNum >= 53 && atNum <= 54 // I,Xe
			;
		} else if (type.equals(Type.GROUP_A)) {
			isType = atNum == 3 || atNum == 11 || atNum == 19 ||
			atNum == 37 || atNum == 55 || atNum ==87;
		} else if (type.equals(Type.GROUP_B)) {
			isType = atNum == 4 || atNum == 12 || atNum == 20 ||
			atNum == 38 || atNum == 56 || atNum ==88;
		}else {
			throw new CMLRuntimeException("Bad type for " + type);
		}
		return isType;
	}

	/**
	 * is element a transition metal. FIXME - I haven't checked values
	 *
	 * @param chemicalElement
	 * @param type
	 *            GROUP_A, GROUP_B or ROW
	 * @param value
	 *            or row or group
	 * @return true if is TM
	 */
	public static boolean isChemicalElementType(
			ChemicalElement chemicalElement, Type type, int value) {
		int atNum = chemicalElement.getAtomicNumber();
		// String symbol = chemicalElement.getSymbol();
		boolean isType = false;
		if (type.equals(Type.ROW)) {
			isType = value == 1 && atNum >= 3 && atNum <= 10 || value == 2
			&& atNum >= 11 && atNum <= 18 || value == 3 && atNum >= 19
			&& atNum <= 36 || value == 4 && atNum >= 37 && atNum <= 54
			|| value == 5 && atNum >= 55 && atNum <= 86 || value == 6
			&& atNum >= 87;
		} else if (type.equals(Type.GROUP_A)) {
			if (value == 1 || value == 2) {
				isType = atNum == 2 + value || atNum == 10 + value
				|| atNum == 18 + value || atNum == 36 + value
				|| atNum == 54 + value;
			} else if (value >= 3 && value <= 8) {
				isType = atNum == 2 + value || atNum == 10 + value
				|| atNum == 28 + value || atNum == 46 + value
				|| atNum == 78 + value;
			}
		} else if (type.equals(Type.GROUP_B)) {
			if (value >= 1 || value <= 10) {
				isType = atNum == 18 + value || atNum == 36 + value
				|| atNum == 56 + value;
			}
		} else {
			throw new CMLRuntimeException("Bad type for " + type + ": " + value);
		}
		return isType;
	}

	/**
	 * Traverses all non-H atoms and contracts the hydrogens on each.
	 *
	 * @param control
	 * @throws CMLException
	 */
	public void contractExplicitHydrogens(HydrogenControl control) {
		if (molecule.isMoleculeContainer()) {
			CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
			for (CMLMolecule mol : molecules) {
				new MoleculeTool(mol).contractExplicitHydrogens(control);
			}
		} else {
			List<CMLAtom> atoms = molecule.getAtoms();
			for (CMLAtom atom : atoms) {
				if (!atom.getElementType().equals("H")) {
					this.contractExplicitHydrogens(atom, control);
				}
			}
		}
	}

	/**
	 * Contracts the hydrogens on an atom.
	 *
	 * @param atom
	 *            to contract
	 * @param control
	 */
	public void contractExplicitHydrogens(CMLAtom atom, HydrogenControl control) {
		int hCount = (atom.getHydrogenCountAttribute() == null) ? 0 : atom
				.getHydrogenCount();
		Set<ChemicalElement> hSet = ChemicalElement
		.getElementSet(new String[] { "H" });
		List<CMLAtom> ligands = CMLAtom.filter(atom.getLigandAtoms(), hSet);
		for (CMLAtom ligand : ligands) {
			molecule.deleteAtom(ligand);
		}
		atom.setHydrogenCount(Math.max(hCount, ligands.size()));
	}

	/**
	 * Traverses all non-H atoms and calls CMLAtom on each.
	 *
	 * @param control
	 *            as in CMLAtom
	 * @see CMLAtom
	 */
	public void expandImplicitHydrogens(HydrogenControl control) {
		for (CMLAtom atom : molecule.getAtoms()) {
			if (!atom.getElementType().equals("H")) {
				this.expandImplicitHydrogens(atom, control);
			}
		}
	}

	/**
	 * removes atoms from this with identical coordinates to those in mol. uses
	 * deleteAtom (which may delete bonds if present)
	 *
	 * @param mol
	 *            should be unaltered
	 * @param type
	 *            of coordinate to use
	 */
	public void removeOverlapping3DAtoms(CMLMolecule mol, CoordinateType type) {
		CMLAtomSet atomSet = new AtomSetTool(molecule.getAtomSet())
		.getOverlapping3DAtoms(mol.getAtomSet(), type);
		for (CMLAtom atom : atomSet.getAtoms()) {
			molecule.deleteAtom(atom);
		}
	}

	// atom

	/**
	 * gets list of ligands in 2D diagram in clockwise order.
	 *
	 * starting atom is arbitrary (makes smallest clockwise angle with xAxis).
	 * The 4 atoms can be compared to atomRefs4 given by author or other methods
	 * to see if they are of the same or alternative parity.
	 *
	 * use compareAtomRefs4(CMLAtom[] a, CMLAtom[] b) for comparison
	 *
	 * @param atom
	 * @param atom4
	 *            the original list of 4 atoms
	 * @return ligands sorted into clockwise order
	 * @throws CMLRuntimeException
	 */
	CMLAtom[] getClockwiseLigands(CMLAtom atom, CMLAtom[] atom4)
	throws CMLRuntimeException {
		Vector2 vx = new Vector2(1.0, 0.0);
		Real2 thisxy = atom.getXY2();
		double[] angle = new double[4];
		Vector2 v = null;
		for (int i = 0; i < 4; i++) {
			try {
				v = new Vector2(atom4[i].getXY2().subtract(thisxy));
				// Angle class appears to be broken, hence the degrees
				angle[i] = vx.getAngleMadeWith(v).getDegrees();
			} catch (NullPointerException npe) {
				throw new CMLRuntimeException(
						"Cannot compute clockwise ligands");
			}
			if (angle[i] < 0) {
				angle[i] += 360.;
			}
			if (angle[i] > 360.) {
				angle[i] -= 360.;
			}
		}
		// get atom4Refs sorted in cyclic order
		CMLAtom[] cyclicAtom4 = new CMLAtom[4];
		for (int i = 0; i < 4; i++) {
			double minAngle = 99999.;
			int low = -1;
			for (int j = 0; j < 4; j++) {
				if (angle[j] >= 0 && angle[j] < minAngle) {
					low = j;
					minAngle = angle[j];
				}
			}

			if (low != -1) {
				cyclicAtom4[i] = atom4[low];
				angle[low] = -100.;
			} else {
				throw new CMLRuntimeException(
						"Couldn't get AtomRefs4 sorted in cyclic order");
			}
		}
		// all 4 angles must be less than PI
		// the ligands in clockwise order
		for (int i = 0; i < 4; i++) {
			CMLAtom cyclicAtomNext = cyclicAtom4[(i < 3) ? i + 1 : 0];
			Real2 cyclicXy = cyclicAtom4[i].getXY2();
			Real2 cyclicXyNext = cyclicAtomNext.getXY2();
			v = new Vector2(cyclicXy.subtract(thisxy));
			Vector2 vNext = new Vector2(cyclicXyNext.subtract(thisxy));
			double ang = v.getAngleMadeWith(vNext).getDegrees();
			if (ang < 0) {
				ang += 360.;
			}
			if (ang > 360.) {
				ang -= 360.;
			}
			if (ang > 180.) {
				throw new CMLRuntimeException("All 4 ligands on same side "
						+ molecule.getId());
			}
		}
		return cyclicAtom4;
	}

	/**
	 * get bond length.
	 *
	 * uses 3D atom coordinates, else 2D atom coordinates, to generate length
	 *
	 * @param bond
	 * @param type
	 * @return the length
	 * @throws CMLException
	 *             if not computable (no coord, missing atoms...)
	 */
	public double calculateBondLength(CMLBond bond, CoordinateType type) {
		CMLAtom atom0 = null;
		CMLAtom atom1 = null;
		List<CMLAtom> atomList = bond.getAtoms();
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

	/**
	 * get new (cloned) molecule from atomset and bondset.
	 *
	 * the bonds must be between the atoms, but not all such bonds need to be in
	 * the bondSet (e.g. to create a spanning tree)
	 *
	 * @param atomSet
	 * @param bondSet
	 *            the bonds in the new molecule
	 * @exception CMLRuntimeException
	 *                bond not between atomset members
	 * @return cloned molecule
	 */
	public CMLMolecule getClonedMolecule(CMLAtomSet atomSet, CMLBondSet bondSet)
	throws CMLRuntimeException {
		if (bondSet == null) {
			bondSet = new CMLBondSet();
		}

		CMLMolecule newMol = new CMLMolecule();
		CMLAtomArray newAtomArray = new CMLAtomArray();
		newMol.addAtomArray(newAtomArray);
		CMLBondArray newBondArray = new CMLBondArray();
		newMol.addBondArray(newBondArray);

		// add clones of old atoms
		List<CMLAtom> atoms = atomSet.getAtoms();
		for (CMLAtom atom : atoms) {
			// AtomTool atomTool = AtomToolImpl.getTool((CMLAtom)atoms.get(i));
			// atomTool.setAtom(atoms[i]);
			// newAtomArray.appendAtom (atomTool.cloneAtom (ownerDoc));
			newAtomArray.appendChild(new CMLAtom(atom));
		}

		List<CMLBond> bonds = bondSet.getBonds();
		// add clones of old bonds
		for (CMLBond bond : bonds) {
			String atId0 = bond.getAtomId(0);
			if (molecule.getAtomById(atId0) == null) {
				throw new CMLRuntimeException("Atom in bond not in atomset: "
						+ atId0);
			}
			String atId1 = bond.getAtomId(1);
			if (molecule.getAtomById(atId1) == null) {
				throw new CMLRuntimeException("Atom in bond not in atomset: "
						+ atId1);
			}
			newBondArray.appendChild(new CMLBond(bond));
		}

		return newMol;
	}

	/**
	 * gets atomset corresponding to bondset.
	 *
	 * @param bondSet
	 * @return the atomset
	 */
	public CMLAtomSet getAtomSet(CMLBondSet bondSet) {
		CMLAtomSet atomSet = new CMLAtomSet();

		List<CMLBond> bonds = bondSet.getBonds();
		for (CMLBond bond : bonds) {
			CMLAtom atom0 = bond.getAtom(0);
			CMLAtom atom1 = bond.getAtom(1);
			atomSet.addAtom(atom0);
			atomSet.addAtom(atom1);
		}

		return atomSet;
	}

	/**
	 * gets average 2D bond length.
	 *
	 * if excludeElements is not null, exclude any bonds including those
	 * excludeElementTypes ELSE if includeElements is not null, include any
	 * bonds including only those excludeElementTypes ELSE use all bonds
	 *
	 * @param bondSet
	 * @param excludeElements
	 *            list of element symbols to exclude
	 * @param includeElements
	 *            list of element symbols to include
	 * @return average bond length (NaN if no bonds selected)
	 */
	@SuppressWarnings("unused")
	private double getAverage2DBondLength(CMLBondSet bondSet,
			String[] excludeElements, String[] includeElements) {
		double sum = 0.0;
		int count = 0;
		List<CMLBond> bonds = bondSet.getBonds();
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
				double length = this.calculateBondLength(bond,
						CoordinateType.TWOD);
				sum += length;
				count++;
			}
		}
		return (count == 0) ? Double.NaN : sum / (double) count;
	}

	/**
	 * calculate bondorders from coordinates if present.
	 *
	 * uses Pauling bond order/length formula. use with extreme care! many bonds
	 * will have fractional orders and these will be difficult to manage
	 */
	public void calculateBondOrdersFromXYZ3() {
		for (CMLBond bond : molecule.getBonds()) {
			String order = null;
			try {
				order = bond.getOrder();
			} catch (Exception e) {
				logger.severe("BUG " + e);
			}
			// order not available?
			if (order == null || order.equals(CMLBond.UNKNOWN_ORDER)) {
				CMLAtom at0 = bond.getAtom(0);
				if (at0 == null) {
					throw new CMLRuntimeException("NULL atom");
				}
				String elType = at0.getElementType();
				if (elType == null) {
					throw new CMLRuntimeException("missing elementType");
				}
				ChemicalElement el0 = ChemicalElement
				.getChemicalElement(elType);
				if (el0 == null) {
					continue;
				}
				double r0 = el0.getCovalentRadius();
				CMLAtom at1 = bond.getAtom(1);
				ChemicalElement el1 = ChemicalElement.getChemicalElement(at1
						.getElementType());
				if (el1 == null) {
					continue;
				}
				double r1 = el1.getCovalentRadius();
				double dlen = at0.getDistanceTo(at1);
				if (dlen < 0.0) {
					continue;
				}
				double paulingBO = Math.exp(2.303 * (-dlen + r0 + r1) / 0.7);
				try {
					if (paulingBO < 1.3) {
						bond.setOrder(CMLBond.SINGLE);
					} else if (paulingBO < 1.75) {
						bond.setOrder(CMLBond.AROMATIC);
					} else if (paulingBO < 2.5) {
						bond.setOrder(CMLBond.DOUBLE);
					} else if (paulingBO < 5) {
						bond.setOrder(CMLBond.TRIPLE);
					}
				} catch (Exception e) {
					Util.BUG(e);
				}
			}
		}
	}

	/**
	 * calculates which atoms are bonded by covalent radius criterion.
	 *
	 */
	public void calculateBondedAtoms() {
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule molecule : molecules) {
				new MoleculeTool(molecule).calculateBondedAtoms();
			}
		} else {
			List<CMLBond> bonds = molecule.getBonds();
			if (bonds == null || bonds.size() == 0) {
				List<CMLAtom> atoms = molecule.getAtoms();
				calculateBondedAtoms(atoms);
			}
		}
	}

	void calculateBondedAtoms(List<CMLAtom> atoms) {
		double[] radii = new double[atoms.size()];
		for (int i = 0; i < atoms.size(); i++) {
			String sym = ((CMLAtom) atoms.get(i)).getElementType();
			radii[i] = this.getCovalentRadius(sym);
		}

		for (int i = 0; i < atoms.size(); i++) {
			CMLAtom atomi = (CMLAtom) atoms.get(i);
			for (int j = i + 1; j < atoms.size(); ++j) {
				CMLAtom atomj = (CMLAtom) atoms.get(j);
				if (CMLBond.areWithinBondingDistance(atomi, atomj, radii[i],
						radii[j], ChemicalElement.getBondingRadiusTolerance())) {
					molecule.addBond(new CMLBond(atomi, atomj));
				}
			}
		}
	}

	double getCovalentRadius(String sym) {
		double radius = getCustomCovalentRadius(sym);
		if (radius <= 0.0) {
			ChemicalElement element = ChemicalElement.getChemicalElement(sym);
			if (element == null) {
				logger.severe("Unknown element: " + sym);
			} else {
				radius = element.getCovalentRadius();
			}
		}
		return radius;
	}

	double getCustomCovalentRadius(String sym) {
		double radius = 0.0;
		if (sym.equals("Li") || sym.equals("Na") || sym.equals("K")
				|| sym.equals("Rb") || sym.equals("Cs") ||
				//
				sym.equals("Ca") || sym.equals("Sr") || sym.equals("Ba")) {
			radius = 0.1;
		}
		return radius;
	}

	/**
	 * calculate all bonds from this to molecule and join. will transfer all
	 * atoms from molecule to this except overlapping ones, thereby corrupting
	 * molecule. Overlapping atoms are discarded
	 *
	 * @param molecule2
	 *            to join (will be corrupted)
	 * @return true if 1 or more bonds were made
	 */
	boolean calculateBondsToAndJoin(CMLMolecule molecule2) {
		List<CMLAtom> atoms = molecule.getAtoms();
		List<CMLAtom> atoms2 = molecule2.getAtoms();
		// List<CMLBond> bonds = getBonds();
		double[] radiusi = new double[atoms.size()];
		for (int i = 0; i < atoms.size(); i++) {
			String sym = ((CMLAtom) atoms.get(i)).getElementType();
			radiusi[i] = getCovalentRadius(sym);
		}
		double[] radiusj = new double[atoms2.size()];
		for (int j = 0; j < atoms2.size(); j++) {
			String sym = ((CMLAtom) atoms2.get(j)).getElementType();
			radiusj[j] = getCovalentRadius(sym);
		}
		boolean madeBond = false;
		int i = 0;
		int idMax = this.getMaximumId("a");
		for (CMLAtom atomi : atoms) {
			int j = 0;
			for (CMLAtom atomj : atoms2) {
				if (CMLBond
						.areWithinBondingDistance(atomi, atomj, radiusi[i],
								radiusj[j], ChemicalElement
								.getBondingRadiusTolerance())) {
					madeBond = true;
					if (CMLBond.areWithinBondingDistance(atomi, atomj, 0.1,
							0.1, ChemicalElement.getBondingRadiusTolerance())) {
						// remove overlapping atoms
						atomj.detach();
						System.out
						.println("OVERLAP........................... "
								+ atomj.getId());
					} else {
						// transfer atom and add new bond
						atomj.detach();
						String id = "a" + (++idMax);
						atomj.setId(id);
						molecule.addAtom(atomj);
						molecule.addBond(new CMLBond(atomi, atomj));
					}
				}
				j++;
			}
			i++;
		}
		if (madeBond) {
			// transfer rest of atoms from molecule 2
			idMax = this.getMaximumId("a");
			atoms2 = molecule2.getAtoms();
			for (CMLAtom atom2 : atoms2) {
				atom2.detach();
				String id = "a" + (++idMax);
				atom2.setId(id);
				molecule.addAtom(atom2);
			}
			calculateBondedAtoms(atoms2);
		}
		return madeBond;
	}

	/**
	 * iterates through atom ids and finds the largest numerically. assumes they
	 * are of form: "a123" and returns largest value of integer
	 *
	 * @param prefix
	 *            (e.g. "a")
	 * @return largest integer following prefix
	 */
	private int getMaximumId(String prefix) {
		int max = -1;
		int l = prefix.length();
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			String id = atom.getId();
			try {
				int ii = Integer.parseInt(id.substring(l));
				max = (max < ii) ? ii : max;
			} catch (NumberFormatException e) {
				;//
			}
		}
		return max;
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
	 * get the average atom-atom bond distance.
	 *
	 * This is primarily for scaling purposes and should not be taken too
	 * seriously.
	 *
	 * @param type
	 * @return average distance in origianl units. If no bonds returns negative.
	 */
	public double getAverageBondLength(CoordinateType type) {
		double bondSum = 0.0;
		int nBonds = 0;
		for (CMLBond bond : molecule.getBonds()) {
			double length = this.calculateBondLength(bond, type);
			bondSum += length;
			nBonds++;
		}
		return (nBonds == 0) ? -1.0 : bondSum / ((double) nBonds);
	}

	/**
	 * get matched bond using atom mapping. does not work with sets
	 *
	 * if bond atomRefs2="a1 a2" and link to="a1" from="b1" // atomId and link
	 * to="a2" from="b2" // atomId and toFrom = Direction.FROM then will return
	 * bond atomRefs2="b1 b2" or atomRefs2="b2 b1" in molecule1
	 *
	 * @param bond0
	 *            bond to search with. the values in must occur in a single
	 *            toFrom attribute
	 * @param map
	 *            with links
	 * @param toFrom
	 *            specifies attribute for search atoms in atomRefs2
	 * @return mapped bond or null
	 */
	@SuppressWarnings("unused")
	private CMLBond getMappedBondViaAtoms(CMLMap map, CMLBond bond0,
			Direction toFrom) {
		CMLBond targetBond = null;
		CMLAtom atom0 = bond0.getAtom(0);
		CMLAtom atom1 = bond0.getAtom(1);
		if (atom0 != null && atom1 != null) {
			String targetRef0 = map.getRef(atom0.getId(), toFrom);
			String targetRef1 = map.getRef(atom1.getId(), toFrom);
			targetBond = molecule.getBond(molecule.getAtomById(targetRef0),
					molecule.getAtomById(targetRef1));
		}
		return targetBond;
	}

	// ============ atom matcher ==============
	/**
	 * finds a pair with neighbours that map to each other can be called
	 * iteratively until returns null
	 *
	 * @param atomSet1
	 *            to match
	 * @param atomSet2
	 *            diminished if match found
	 * @param from1to2map
	 *            the mapping from atomSet1 to atomSet2 (i.e. from ids in
	 *            atomSet1, to in atomSet2)
	 * @param atomMatcher
	 * @return pair of matched atoms
	 *
	 */
	// FIXME
	@SuppressWarnings("unused")
	private AtomPair getAtomsWithSameMappedNeighbours00(
			AtomMatcher atomMatcher, CMLAtomSet atomSet1, CMLAtomSet atomSet2,
			CMLMap from1to2map) {

		Set<CMLAtom> matchingSet = new HashSet<CMLAtom>();
		AtomPair pair = null;
		for (CMLAtom atom : atomSet1.getAtoms()) {
			if (atomMatcher.skipAtom(atom)) {
				continue;
			}
			matchingSet.add(atom);
		}

		for (CMLAtom atom : atomSet2.getAtoms()) {
			String elementType2 = atom.getElementType();
			if (atomMatcher.skipAtom(atom)) {
				continue;
			}

			CMLAtomSet ligand2Set = new CMLAtomSet(atom.getLigandAtoms());

			// for each ligandList or atom2 loop through all atom1s to find
			// match
			// which has most ligands?
			// FIXME better - store all atoms with at least one ligand
			List<CMLAtom> matchAtomList = new ArrayList<CMLAtom>();
			int maxLigands = 0;
			CMLAtom mostLigandedAtom1 = null;
			for (int j = 0; j < atomSet1.size(); j++) {
				CMLAtom atom1 = (CMLAtom) atomSet1.getAtom(j);
				if (atomMatcher.skipAtom(atom1)) {
					continue;
				}
				String elementType1 = atom1.getElementType();
				// atoms must match type
				if (!elementType1.equals(elementType2)) {
					continue;
				}
				int ligandCount = 0;
				List<CMLAtom> ligandList = atom1.getLigandAtoms();
				for (CMLAtom ligandAtom1 : ligandList) {
					if (!atomMatcher.skipLigandAtom(ligandAtom1)) {
						String id1 = ligandAtom1.getId();
						String id2 = from1to2map.getRef(id1,
								CMLMap.Direction.FROM);
						// does id2 exist and is it a ligand of atom 2
						if (id2 != null && ligand2Set.getAtomById(id2) != null) {
							ligandCount++;
						}
					}
				}
				if (ligandCount > maxLigands) {
					maxLigands = ligandCount;
					mostLigandedAtom1 = atom1;
				}
				if (ligandCount > 0) {
					matchAtomList.add(atom1);
				}

			}
			// ligand atoms in common?
			//
			if (matchAtomList.size() == 1) {
				CMLAtom matchAtom1 = matchAtomList.get(0);
				pair = new AtomPair(matchAtom1, atom);
				atomSet1.removeAtom(matchAtom1);
				atomSet2.removeAtom(atom);
				break;
				// old method
			} else if (false && mostLigandedAtom1 != null) {
				pair = new AtomPair(mostLigandedAtom1, atom);
				atomSet1.removeAtom(mostLigandedAtom1);
				atomSet2.removeAtom(atom);
				break;
			} else {
				pair = null;
			}
		}
		return pair;
	}

	/**
	 * make sub molecule.
	 *
	 * @param molecule
	 * @param atomIds
	 * @return molecule
	 *
	 */
	public static CMLMolecule createMolecule(CMLMolecule molecule,
			String[] atomIds) {
		CMLMolecule newMolecule = new CMLMolecule();
		for (String atomId : atomIds) {
			CMLAtom atom = new CMLAtom(atomId);
			newMolecule.addAtom(atom);
		}
		return newMolecule;
	}

	/**
	 * gets mapping of namespaces onto molecule files.
	 *
	 * @return map of namespace->file
	 */
	/*--
	     public static CMLMap getMoleculeCatalog(CMLInputStreamContainer catalogISC) {
	     if (catalogISC == null) {
	     throw new CMLRuntime("NULL catalogISC");
	     }
	     CMLMap map = null;
	     try {
	     CMLCml cml = (CMLCml) new CMLBuilder().build(catalogISC.getInputStream()).getRootElement();
	     map = (CMLMap) cml.getFirstCMLChild(CMLMap.TAG);
	     } catch (Exception e) {
	     throw new CMLRuntime("BUG: maybe bad catalog: "+e+": "+catalogISC.getName());
	     }
	     return map;
	     }
	     --*/

	/**
	 * get map of molecules under namespace.
	 *
	 * @param namespace
	 * @param catalogTool
	 * @return map indexed by id
	 */
	public static Map<String, CMLMolecule> getMolecules(String namespace,
			CatalogTool catalogTool) {
		if (catalogTool == null) {
			throw new CMLRuntimeException("Null catalogTool");
		}
		CMLMap catalogMap = catalogTool.getCatalogMap();
		if (catalogMap == null) {
			throw new CMLRuntimeException("cannot get catalogMap");
		}
		String to = catalogMap.getToRef(namespace);
		if (to == null) {
			throw new CMLRuntimeException("Cannot find catalog entry for: "
					+ namespace);
		}

		URL toUrl = null;
		try {
			toUrl = new URL(catalogTool.getCatalogUrl(), to);
		} catch (MalformedURLException e1) {
			throw new CMLRuntimeException("Bad catalogue reference: " + to, e1);
		}

		Map<String, CMLMolecule> moleculeMap = null;
		File file = new File(toUrl.getFile());
		if (file.isDirectory()) {
			moleculeMap = getMoleculeMapFromDirectory(file);
		} else {
			moleculeMap = getMoleculeMapFromFile(toUrl);
		}
		return moleculeMap;
	}

	private static Map<String, CMLMolecule> getMoleculeMapFromDirectory(File dir) {
		File[] files = dir.listFiles();
		Map<String, CMLMolecule> moleculeMap = new HashMap<String, CMLMolecule>();
		for (File file : files) {
			if (!file.toString().endsWith(".xml")) {
				continue;
			}
			// File file1 = new File(dir, file.toString());
			CMLMolecule molecule = null;
			try {
				Document doc = new CMLBuilder().build(file);
				molecule = (CMLMolecule) doc.getRootElement();
			} catch (Exception e) {
				System.err.println("File: " + file);
				throw new CMLRuntimeException(e);
			}
			moleculeMap.put(molecule.getId(), molecule);
		}
		return moleculeMap;
	}

	private static Map<String, CMLMolecule> getMoleculeMapFromFile(URL toUrl) {
		CMLCml cml = null;
		InputStream cmlIn = null;
		try {
			cmlIn = toUrl.openStream();
			cml = (CMLCml) new CMLBuilder().build(cmlIn).getRootElement();
		} catch (Exception e) {
			throw new CMLRuntimeException("Cannot read molecule file: " + toUrl
					+ " (" + e + ")");
		} finally {
			if (cmlIn != null) {
				try {
					cmlIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, CMLMolecule> moleculeMap = new HashMap<String, CMLMolecule>();
		Elements elements = cml.getChildCMLElements(CMLMolecule.TAG);
		for (int i = 0; i < elements.size(); i++) {
			CMLMolecule molecule = (CMLMolecule) elements.get(i);
			moleculeMap.put(molecule.getId(), molecule);
		}
		return moleculeMap;
	}

	/**
	 * get referenced molecule. uses ref attribute on molecule
	 *
	 * @param moleculeCatalog
	 * @return the molecule or null
	 */
	public CMLMolecule getReferencedMolecule(CatalogTool moleculeCatalog) {
		return this.getReferencedMolecule(molecule.getRef(), moleculeCatalog);
	}

	/**
	 * ger referenced molecule.
	 *
	 * @param ref
	 *            (local "foo", or namespaced ("f:bar"))
	 * @param catalog
	 * @return the molecule or null
	 */
	public CMLMolecule getReferencedMolecule(String ref, CatalogTool catalog) {
		if (catalog == null) {
			new Exception().printStackTrace();
			throw new CMLRuntimeException("Null catalog tool");
		}
		CMLMolecule refMol = null;
		if (ref == null) {
			throw new CMLRuntimeException("ref must not be null");
		}
		int idx = ref.indexOf(S_COLON);
		if (idx == -1) {
			throw new CMLRuntimeException(
					"unprefixed reference for molecule NYI");
		}
		String prefix = ref.substring(0, idx);
		if (prefix.length() == 0) {
			throw new CMLRuntimeException(
					"Cannot have empty prefix for mol ref");
		}
		String namespace = molecule.getNamespaceForPrefix(prefix);
		if (namespace == null) {
			throw new CMLRuntimeException("Cannot find namespace for: " + ref);
		}
		Map<String, CMLMolecule> moleculeMap = MoleculeTool.getMolecules(
				namespace, catalog);
		if (moleculeMap == null) {
			throw new CMLRuntimeException("Cannot find molecule map for: "
					+ namespace);
		}
		String localRef = ref.substring(idx + 1);
		if (localRef.length() == 0) {
			throw new CMLRuntimeException(
					"Cannot have empty prefix for mol ref");
		}
		refMol = moleculeMap.get(localRef);
		return refMol;
	}

	/**
	 * creates bonds and partitions molecules and returns contacts. currently
	 * looks only for homo-molecule contacts.
	 *
	 * @param dist2Range
	 * @param crystalTool
	 * @return sorted contact list
	 */
	public List<Contact> getSymmetryContacts(RealRange dist2Range,
			CrystalTool crystalTool) {
		this.calculateBondedAtoms();
		molecule.createCartesiansFromFractionals(crystalTool.getCrystal());
		List<Contact> contactList = crystalTool
		.getSymmetryContactsToMolecule(dist2Range);
		Collections.sort(contactList);
		return contactList;
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

	/**
	 * analyze atoms with disorder flags.
	 *
	 * This may need to be rafactored to CIF Converter
	 *
	 * CIF currently provides two flags atom_disorder_assembly identifies
	 * different independent groups atom_disorder_group identifies the different
	 * instances of atoms within a single group from the CIF dictionary:
	 *
	 * ===atom_site_disorder_assembly
	 *
	 * _name _atom_site_disorder_assembly _category atom_site _type char _list
	 * yes _list_reference _atom_site_label
	 *
	 * _example _detail A disordered methyl assembly with groups 1 and 2 B
	 * disordered sites related by a mirror S disordered sites independent of
	 * symmetry
	 *
	 * _definition A code which identifies a cluster of atoms that show
	 * long-range positional disorder but are locally ordered. Within each such
	 * cluster of atoms, _atom_site_disorder_group is used to identify the sites
	 * that are simultaneously occupied. This field is only needed if there is
	 * more than one cluster of disordered atoms showing independent local
	 * order.
	 *
	 * ===atom_site_disorder_group
	 *
	 *
	 * _name _atom_site_disorder_group _category atom_site _type char _list yes
	 * _list_reference _atom_site_label
	 *
	 * _example _detail 1 unique disordered site in group 1 2 unique disordered
	 * site in group 2 -1 symmetry-independent disordered site
	 *
	 * _definition A code which identifies a group of positionally disordered
	 * atom sites that are locally simultaneously occupied. Atoms that are
	 * positionally disordered over two or more sites (e.g. the hydrogen atoms
	 * of a methyl group that exists in two orientations) can be assigned to two
	 * or more groups. Sites belonging to the same group are simultaneously
	 * occupied, but those belonging to different groups are not. A minus prefix
	 * (e.g. "-1") is used to indicate sites disordered about a special
	 * position.
	 *
	 * we analyse this as follows: if there is no disorder_assembly we assume a
	 * single disorder_assembly containing all disordered atoms. if there are
	 * several disorder_assemblies, each is treated separately. within a
	 * disorderAssembly there must be 2 or more disorder_groups each group is a
	 * separate locally disordered syste, which we describe by an atomSet. The
	 * occupancies within in group must be identical. We also check for whether
	 * the groups contain the same number of atoms and whether the occupancies
	 * of the groups sum to 1.0
	 *
	 * At this stage it may not be known whether the assemblies are in different
	 * chemical molecules. It is unusual for disorder_groups to belong to
	 * different subMolecules though it could happen with partial proton
	 * transfer
	 *
	 * @exception Exception
	 * @return list of disorderAssembly
	 */
	private List<DisorderAssembly> getDisorderAssemblyList() {
		List<CMLAtom> disorderedAtomList = DisorderAssembly
		.getDisorderedAtoms(molecule);
		List<DisorderAssembly> disorderAssemblyList = null;
		disorderAssemblyList = DisorderAssembly.getDisorderedAssemblyList(disorderedAtomList);

		return disorderAssemblyList;
	}

	/**
	 * convenience method to get molecule or its child molecules as a list.
	 *
	 * @return list of either just the molecule (if no children) or list of the
	 *         children
	 */
	public List<CMLMolecule> getMoleculeList() {
		List<CMLMolecule> moleculeList = molecule.getDescendantsOrMolecule();
		return moleculeList;
	}

	/**
	 * convenience method to calculate and add Cartesian coordinates. requires
	 * that molecule has descendant <crystal>
	 *
	 * @throws CMLRuntimeException
	 *             if no crystal
	 */
	public void createCartesiansFromFractionals() throws CMLRuntimeException {
		CMLCrystal crystal = CMLCrystal.getContainedCrystal(molecule);
		molecule.createCartesiansFromFractionals(crystal);
	}

	/**
	 * Processes crystallographic disorder.
	 *
	 * At present the only RemoveDisorderControl is to remove minor disorder.
	 * These are components with occupancies <= 0.5.
	 *
	 * Can provide two different kinds of ProcessDisorderControl:
	 * 1. STRICT - this tries to process the disorder as set out in the CIF
	 * specification (http://www.iucr.org/iucr-top/cif/#spec) If it comes
	 * across disorder which does not comply with the spec then throws an error.
	 *
	 * 2. LOOSE - this tries to process the disorder as with STRICT, but does
	 * not throw an error if it comes across disorder not valid with respect
	 * to the CIF spec.  Instead it tags the provided molecule with metadata
	 * stating that the disorder cannot currently be processed and does nothing
	 * more.
	 *
	 * NOTE: if the disorder is successfully processed then the molecule is
	 * tagged with metadata stating that the molecule did contain disorder,
	 * but has now been removed.
	 *
	 * @param pControl
	 * @param rControl
	 * @exception CMLRuntimeException
	 */
	public void processDisorder(ProcessDisorderControl pControl,
			RemoveDisorderControl rControl) throws CMLRuntimeException {

		List<DisorderAssembly> disorderAssemblyList = null;
		disorderAssemblyList = this.getDisorderAssemblyList();
		disorderAssemblyList = checkDisorder(disorderAssemblyList, pControl);
		if (pControl.equals(ProcessDisorderControl.STRICT)) {
			addDisorderMetadata(true);
		}
		if (rControl.equals(RemoveDisorderControl.REMOVE_MINOR_DISORDER)) {
			for (DisorderAssembly assembly : disorderAssemblyList) {
				assembly.removeMinorDisorder();
			}
		} else {
			throw new CMLRuntimeException("Illegal RemoveDisorderControl: "
					+ rControl);
		}
	}

	/**
	 * checks that the disorder complies with the CIF specification.
	 * http://www.iucr.org/iucr-top/cif/#spec
	 *
	 * called only from processDisorder().
	 *
	 * @param disorderAssemblyList
	 * @param pControl
	 * @return list of disorder assemblies in the given molecule
	 */
	private List<DisorderAssembly> checkDisorder(
			List<DisorderAssembly> disorderAssemblyList,
			ProcessDisorderControl pControl) {
		boolean metadataSet = false;
		for (DisorderAssembly da : disorderAssemblyList) {
			List<DisorderGroup> disorderGroupList = da.getDisorderGroupList();
			if (disorderGroupList.size() < 2) {
				if (pControl.equals(ProcessDisorderControl.LOOSE)) {
					if (!metadataSet) {
						addDisorderMetadata(false);
						metadataSet = true;
					}
				} else if (pControl.equals(ProcessDisorderControl.STRICT)) {
					throw new CMLRuntimeException(
							"Disorder assembly should contain at least 2 disorder groups: "
							+ da.toString());
				}
			}
			List<CMLAtom> commonAtoms = da.getCommonAtoms();
			for (CMLAtom commonAtom : commonAtoms) {
				if (!CrystalTool.hasUnitOccupancy(commonAtom)) {
					if (pControl.equals(ProcessDisorderControl.LOOSE)) {
						if (!metadataSet) {
							addDisorderMetadata(false);
							metadataSet = true;
						}
					} else if (pControl.equals(ProcessDisorderControl.STRICT)) {
						throw new CMLRuntimeException("Common atoms require unit occupancy: "+commonAtom.getId()+
								", in disorder assembly, "+da.getAssemblyCode());
					}
				}
			}
			for (DisorderGroup dg : disorderGroupList) {
				List<CMLAtom> atomList = dg.getAtomList();
				for (CMLAtom atom : atomList) {
					if (CrystalTool.hasUnitOccupancy(atom)) {
						if (pControl.equals(ProcessDisorderControl.LOOSE)) {
							if (!metadataSet) {
								addDisorderMetadata(false);
								metadataSet = true;
							}
						} else if (pControl.equals(ProcessDisorderControl.STRICT)) {
							throw new CMLRuntimeException("Atom, "+atom.getId()+
									", in disorder group, "+dg.getGroupCode()+", has unit occupancy");
						}
					}
					if (Math.abs(atom.getOccupancy() - dg.getOccupancy()) > CrystalTool.OCCUPANCY_EPS) {
						if (pControl.equals(ProcessDisorderControl.LOOSE)) {
							if (!metadataSet) {
								addDisorderMetadata(false);
								metadataSet = true;
							}
						} else if (pControl.equals(ProcessDisorderControl.STRICT)) {
							throw new CMLRuntimeException("Atom, "+atom.getId()+
									", in disorder group, "+dg.getGroupCode()+
							", has inconsistent occupancy with that of the disorder group.");
						}
					}
				}
			}
		}
		return disorderAssemblyList;
	}

	private void addDisorderMetadata(boolean processed) {
		CMLMetadataList metList = new CMLMetadataList();
		molecule.appendChild(metList);
		CMLMetadata met = new CMLMetadata();
		metList.appendChild(met);
		if (!processed) {
			met.setAttribute("dictRef", "cif:unprocessedDisorder");
		} else if (processed) {
			met.setAttribute("dictRef", "cif:processedDisorder");
		}
	}

	/**
	 * defaults to strict.
	 *
	 * @param rControl
	 * @throws CMLRuntimeException
	 */
	public void processDisorder(RemoveDisorderControl rControl)
	throws CMLRuntimeException {
		processDisorder(ProcessDisorderControl.STRICT, rControl);
	}

	/**
	 *  get linkers
	 *
	 *  @return linkerMolecules
	 */
	public List<CMLMolecule> getLinkerMolecules() {
		List<CMLMolecule> linkers = new ArrayList<CMLMolecule>();
		List<CMLMolecule> subMolList = molecule.getDescendantsOrMolecule();
		for (CMLMolecule subMol : subMolList) {
			List<CMLBond> acyclicBonds = new ArrayList<CMLBond>();
			int bondCount = subMol.getBondCount();
			try {
				ConnectionTableTool ct = new ConnectionTableTool(subMol);
				acyclicBonds = ct.getAcyclicBonds();
			} catch (CMLException e) {
				e.printStackTrace();
			}
			// if bond count equal to number of acyclic bonds then there
			// aren't any cyclic bonds in the molecule, hence no linkers
			if (bondCount != acyclicBonds.size()) {
				List<CMLAtom> acyclicAtoms = new ArrayList<CMLAtom>();
				for (CMLBond bond : acyclicBonds) {
					List<CMLAtom> atomList = bond.getAtoms();
					for (CMLAtom atom : atomList) {
						if (!acyclicAtoms.contains(atom)) {
							acyclicAtoms.add(atom);
						}
					}
				}
				CMLBondSet bondSet = new CMLBondSet();
				try {
					bondSet = new CMLBondSet(acyclicBonds);
				} catch (CMLException e) {
					e.printStackTrace();
				}
				CMLAtomSet atomSet = new CMLAtomSet(acyclicAtoms);
				CMLMolecule newMol = new CMLMolecule(atomSet, bondSet);
				new ConnectionTableTool(newMol).partitionIntoMolecules();
				List<CMLMolecule> linkerList = newMol.getDescendantsOrMolecule();
				for (CMLMolecule mol : linkerList) {
					// if the linker is just a H atom attached to an atom in
					// a ring then ignore, else add linker to molList to be
					// returned
					List<CMLAtom> atomList = mol.getAtoms();
					boolean add = true;
					if (atomList.size() == 2) {
						for (CMLAtom atom : atomList) {
							if ("H".equalsIgnoreCase(atom.getElementType())) {
								add = false;
							}
						}
					}
					if (add) {
						linkers.add(mol);
					}
				}
			}
		}
		return linkers;
	}

	/**
	 * get ring nuclei. calls connectionTableTool.getRingNucleiMolecules
	 *
	 * @return ringNucleiMolecules
	 */
	public List<CMLMolecule> getRingNucleiMolecules() {
		ConnectionTableTool connectionTableTool =
			// not sure whether we have to clone the molecule
			// new ConnectionTableTool(new CMLMolecule(subMolecule));
			new ConnectionTableTool(molecule);
		return connectionTableTool.getRingNucleiMolecules();
	}

	// FIXME refactor to fragment
	/**
	 * processes conventional molecule into fragment.
	 */
	public void convertToFragment() {
		// <molecule role="fragment" id="acet"
		// xmlns="http://www.xml-cml.org/schema"
		// xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		// <!-- acetate -->
		// <arg parameterName="idx"/>
		// <arg parentAttribute="id">acet_{$idx}</arg>
		String molId = molecule.getId();
		molecule.addAttribute(new Attribute("role", "fragment"));
		CMLArg arg = new CMLArg();
		arg.setParameterName(IDX);
		molecule.appendChild(arg);
		arg = new CMLArg();
		arg.setParentAttribute("id");
		arg.appendChild(molId + S_UNDER + S_LCURLY + S_DOLLAR + IDX + S_RCURLY);
		molecule.appendChild(arg);

		createAtomArguments(molId);
		createBondArguments(molId);
		createLengthArguments(molId);
		createAngleArguments(molId);
		createTorsionArguments(molId);

		// deal with R
		List<Node> rGroups = CMLUtil.getQueryNodes(molecule,
				".//cml:atom[@elementType='R']", X_CML);
		for (Node node : rGroups) {
			new AtomTool((CMLAtom) node).translateToCovalentRadius();
		}
	}

	private void createAtomArguments(String molId) {
		// <atomArray>
		// <atom id="a1" elementType="C" hydrogenCount="3" x3="0.0" y3="0.0"
		// z3="0.0">
		// <arg parentAttribute="id">acet_{$idx}_a1</arg>
		// </atom>
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			String atomId = atom.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg(molId) + atomId);
			atom.appendChild(arg);
		}
	}


	private void createBondArguments(String molId) {
		//		 ...
		//		 <atom id="r1" elementType="R" x3="2.83" y3="-1.0" z3="0.0">
		//		 <arg parentAttribute="id">acet_{$idx}_r1</arg>
		//		 </atom>
		//		 </atomArray>
		//		 <bondArray>
		//		 <bond atomRefs2="a1 a2" order="1"
		//		 ><arg parentAttribute="id">acet_{$idx}_a1_acet_{$idx}_a2</arg
		//		 ><arg parentAttribute="atomRefs2">acet_{$idx}_a1 acet_{$idx}_a2</arg>
		//		 </bond>
		//		 ...
		//		 <bond atomRefs2="a4 r1" order="1"
		//		 ><arg parentAttribute="id">acet_{$idx}_a4_acet_{$idx}_r1</arg
		//		 ><arg parentAttribute="atomRefs2">acet_{$idx}_a4 acet_{$idx}_r1</arg>
		//		 </bond>
		//		 </bondArray>
		//		 <name><arg substitute=".">acet_{$idx}</arg></name>
		List<CMLBond> bonds = molecule.getBonds();
		for (CMLBond bond : bonds) {
			// String bondId = bond.getId();
			String[] atomRefs2 = bond.getAtomRefs2();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(
					createMolIdArg(molId)+atomRefs2[0]+S_UNDER+
					createMolIdArg(molId)+atomRefs2[1]
			);
			bond.appendChild(arg);
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs2");
			arg.appendChild(
					createMolIdArg(molId)+atomRefs2[0]+S_SPACE+
					createMolIdArg(molId)+atomRefs2[1]
			);
			bond.appendChild(arg);
		}
	}

	private void createLengthArguments(String molId) {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <length atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs2">gly_{$idx}_a1 gly_{$idx}_a2</arg>
		//		 <arg substitute=".">{$psi}</arg>
		//		 </torsion>
		CMLElements<CMLLength> lengths = molecule.getLengthElements();
		for (CMLLength length : lengths) {
			// id
			String lengthId = length.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg(molId) + lengthId);
			length.appendChild(arg);
			// atomRefs4
			String[] atomRefs2 = length.getAtomRefs2();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs2");
			arg.appendChild(
					createMolIdArg(molId) + atomRefs2[0] +S_SPACE+
					createMolIdArg(molId) + atomRefs2[1]
			);
			length.appendChild(arg);
			addArg(length, lengthId);
		}
	}

	private void addArg(CMLElement element, String id) {
		// substitute
		CMLArg arg = new CMLArg();
		arg.setSubstitute(S_PERIOD);
		arg.appendChild(createIdArg(id));
		element.appendChild(arg);
		// append args to molecule
		arg = new CMLArg();
		arg.setParameterName(id);
		molecule.appendChild(arg);
	}

	private void createTorsionArguments(String molId) {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <torsion atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs4">gly_{$idx}_a1 gly_{$idx}_a2 gly_{$idx}_a3 gly_{$idx}_r2</arg>
		//		 <arg substitute=".">{$psi}</arg>
		//		 </torsion>
		CMLElements<CMLTorsion> torsions = molecule.getTorsionElements();
		for (CMLTorsion torsion : torsions) {
			// id
			String torsionId = torsion.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg(molId) + torsionId);
			torsion.appendChild(arg);
			// atomRefs4
			String[] atomRefs4 = torsion.getAtomRefs4();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs4");
			arg.appendChild(
					createMolIdArg(molId) + atomRefs4[0] +S_SPACE+
					createMolIdArg(molId) + atomRefs4[1] +S_SPACE+
					createMolIdArg(molId) + atomRefs4[2] +S_SPACE+
					createMolIdArg(molId) + atomRefs4[3]
			);
			torsion.appendChild(arg);
			addArg(torsion, torsionId);
		}
	}

	private void createAngleArguments(String molId) {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <angle atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs4">gly_{$idx}_a1 gly_{$idx}_a2 gly_{$idx}_a3 gly_{$idx}_r2</arg>
		//		 <arg substitute=".">{$psi}</arg>
		//		 </angle>
		CMLElements<CMLAngle> angles = molecule.getAngleElements();
		for (CMLAngle angle : angles) {
			// id
			String angleId = angle.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg(molId) + angleId);
			angle.appendChild(arg);
			// atomRefs4
			String[] atomRefs3 = angle.getAtomRefs3();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs3");
			arg.appendChild(
					createMolIdArg(molId) + atomRefs3[0] +S_SPACE+
					createMolIdArg(molId) + atomRefs3[1] +S_SPACE+
					createMolIdArg(molId) + atomRefs3[2]
			);
			angle.appendChild(arg);
			addArg(angle, angleId);
		}
	}

	// at present gly_{$idx}_
	private static String createMolIdArg(String molId) {
		return molId +
		S_UNDER + createIdArg(IDX) + S_UNDER;
	}

	// at present {$id}
	private static String createIdArg(String id) {
		return S_LCURLY + S_DOLLAR + id + S_RCURLY;
	}

	private void flattenJoinMoleculeChildren() {
		int idx = molecule.getParent().indexOf(molecule);
		Nodes moleculesAndJoin = molecule.query("cml:molecule | cml:join'",
				X_CML);
		for (int i = 0; i < moleculesAndJoin.size(); i++) {
			Node node = moleculesAndJoin.get(i);
			node.detach();
			molecule.getParent().insertChild(node, idx + 1 + i);
		}
	}

	/**
	 * finds descendant molecules and joins and flattens them. creates a single
	 * list of molecule-join-molecule-join...
	 */
	public void flattenJoinMoleculeDescendants() {
		Nodes molecules = molecule
		.query(".//cml:molecule[cml:molecule]", X_CML);
		for (int i = 0; i < molecules.size(); i++) {
			CMLMolecule molecule = (CMLMolecule) molecules.get(i);
			new MoleculeTool(molecule).flattenJoinMoleculeChildren();
		}

		// tidy join geometry
		this.tidyJoinGeometry();
	}

	/**
	 * tidy geometry after joining. a few molecules have still not transferred
	 * their torsion and other contents to previous join.
	 */
	private void tidyJoinGeometry() {
		Nodes molecules = molecule.query(
				"cml:molecule[cml:torsion | cml:length]", X_CML);
		for (int i = 0; i < molecules.size(); i++) {
			CMLMolecule mol = (CMLMolecule) molecules.get(i);
			Nodes torsionsAndLengths = mol.query("cml:torsion | cml:length",
					X_CML);
			int nnodes = torsionsAndLengths.size();
			Nodes previousSiblings = mol.query("./preceding-sibling::*[1]");
			// is the preceding-sibling a join?
			CMLJoin join = null;
			if (previousSiblings.size() == 1) {
				Node previousSibling = previousSiblings.get(0);
				if (previousSibling instanceof CMLJoin) {
					join = (CMLJoin) previousSibling;
					// if not a full join or populated with geometry ignore
					if (join.getAtomRefs2() == null
							|| join.getChildCMLElements(CMLLength.TAG).size() != 0
							|| join.getChildCMLElements(CMLTorsion.TAG).size() != 0) {
						join = null;
					}
				}
			}
			// detach node anyway
			for (int j = nnodes - 1; j >= 0; j--) {
				Node node = torsionsAndLengths.get(j);
				node.detach();
				// attach node if join is an explicit join and does not have
				// torsions or lengths
				if (join != null) {
					join.appendChild(node);
				}
			}
		}
	}

	/**
	 * finds submolecules with
	 *
	 * @countExpression and expands these.
	 *
	 */
	public void expandCountExpressions() {
		// must do this recursively (WHY??)
		while (true) {
			Nodes nodes = molecule.query(".//cml:molecule[@countExpression]",
					X_CML);
			if (nodes.size() == 0) {
				break;
			}
			CMLMolecule molecule = (CMLMolecule) nodes.get(0);
			new MoleculeTool(molecule).expandCountExpression();
		}
	}

	// move to fragment
	// FIXME
	private void expandCountExpression() {
		throw new CMLRuntimeException("NEEDS FIXING");
		//		 Node parent = molecule.getParent();
		//		 Element parentElement = (Element) parent;
		//		 // position of molecule
		//		 int idx = parentElement.indexOf(molecule);
		//		 int count = molecule.calculateCountExpression();
		//		 // detach any molecules without a reference (from hanging bond)
		//		 Nodes nodes = molecule.query("cml:molecule[@ref='']", X_CML);
		//		 if (nodes.size() == 1) {
		//		 nodes.get(0).detach();
		//		 }
		//		 // any child joins? if so detach and transfer to following sibling
		//		 CMLJoin subJoin = null;
		//		 Nodes joins = molecule.query("cml:join[not(@right) and not(@left)]",
		//		 X_CML);
		//		 if (joins.size() != 0) {
		//		 subJoin = (CMLJoin) joins.get(0);
		//		 subJoin.detach();
		//		 }
		//		 // clone count-1 molecules and append to existing molecule
		//		 for (int i = 1; i < count; i++) {
		//		 // add join to preceeding molecule
		//		 if (subJoin != null) {
		//		 CMLJoin subJoin1 = new CMLJoin(subJoin);
		//		 parentElement.insertChild(subJoin1, idx);
		//		 }
		//		 CMLMolecule molecule1 = new CMLMolecule(molecule);
		//		 parentElement.insertChild(molecule1, idx);
		//		 }
	}

	/**
	 * join one molecule to another. manages the XML but not yet the geometry
	 *
	 * @param addedMolecule
	 *            to be joined
	 * @param existingMolecule
	 * @param takeAtomWithLowestId
	 */
	public void addMoleculeTo(CMLMolecule addedMolecule,
			boolean takeAtomWithLowestId) {

		molecule.getOrCreateAtomArray();
		// bonds must be done first as atom.detach() destroys bonds
		List<CMLBond> bondList = addedMolecule.getBonds();
		for (CMLBond bond : bondList) {
			bond.detach();
			molecule.addBond(bond);
		}
		List<CMLAtom> atomList = addedMolecule.getAtoms();
		for (CMLAtom atom : atomList) {
			atom.detach();
			molecule.addAtom(atom);
		}
		// copy any remaining nodes except atomArray and bondArray
		Nodes nodes = addedMolecule.query(".//*", X_CML);
		int nnodes = nodes.size();
		for (int i = nnodes - 1; i >= 0; i--) {
			Node node = nodes.get(i);
			node.detach();
			if (node instanceof CMLAtomArray) {
			} else if (node instanceof CMLBondArray) {
			} else {
				molecule.appendChild(node);
			}
		}
		addedMolecule.detach();

	}

	/**
	 * get list of joins. looks for child join with linkOnParent attribute
	 *
	 * @return list of joins
	 */
	public List<CMLMoleculeList> getBranchingJoinList() {
		List<CMLMoleculeList> branchingJoinList = new ArrayList<CMLMoleculeList>();
		String link = "cml:PARENT";
		Nodes branchingJoinNodes = molecule.query("cml:moleculeList[cml:label[@dictRef='"+link+"']]",
				X_CML);
		for (int i = 0; i < branchingJoinNodes.size(); i++) {
			branchingJoinList.add((CMLMoleculeList) branchingJoinNodes.get(i));
		}
		return branchingJoinList;
	}

	/** adjust the cartesians to fit declared torsions.
	 */
	public void adjustTorsions() {
		CMLElements<CMLTorsion> torsions = molecule.getTorsionElements();
		for (CMLTorsion torsion : torsions) {
			torsion.adjustCoordinates(molecule);
		}
	}

	/** adjust the cartesians to fit declared angles.
	 */
	public void adjustAngles() {
		CMLElements<CMLAngle> angles = molecule.getAngleElements();
		for (CMLAngle angle : angles) {
			angle.adjustCoordinates(molecule);
		}
	}

	/** adjust the cartesians to fit declared lengths.
	 */
	public void adjustLengths() {
		CMLElements<CMLLength> lengths = molecule.getLengthElements();
		for (CMLLength length : lengths) {
			length.adjustCoordinates(molecule);
		}
	}

	/** expands this molecules with content from refMol.
	 * used when this contains only a @ref and no content.
	 * will also expand any args
	 * @param refMol
	 */
	public void expandRefFromFragment(CMLMolecule refMol) {
		// copy the molecule from the catalogue
		CMLMolecule copyMol = new CMLMolecule(refMol);
		Nodes nodes = molecule.query("cml:arg", X_CML);
		for (int i = 0; i < nodes.size(); i++) {
			CMLArg arg = (CMLArg) nodes.get(i);
			String name = arg.getName();
			String value = arg.getString();
			CMLArg.substituteParameterName(copyMol, name, value);
		}
		CMLArg.substituteParentAttributes(copyMol);
		CMLArg.substituteTextContent(copyMol);
		Element parent = (Element) molecule.getParent();
		int idx = parent.indexOf(molecule);
		parent.insertChild(copyMol, idx);
		molecule.detach();
	}

	/** iterates through torsions in molecule.
	 * if they contain atom0 and atom1, adjusts them to value of torsion
	 * @param atom0
	 * @param atom1
	 */
	public void adjustTorsions(CMLAtom atom0, CMLAtom atom1) {
		if (molecule.hasCoordinates(CoordinateType.CARTESIAN)) {
			// set torsions
			CMLAtomSet moleculeAtomSet = molecule.getAtomSet();
			Nodes torsions = molecule.query(".//cml:torsion", X_CML);
			int nTors = torsions.size();
			for (int i = 0; i < nTors; i++) {
				CMLTorsion torsion = (CMLTorsion) torsions.get(i);
				String[] atomRefs4 = torsion.getAtomRefs4();
				if (atomRefs4 != null
						&& (atom0.getId().equals(atomRefs4[1]) && atom1.getId().equals(atomRefs4[2]) ||
								atom0.getId().equals(atomRefs4[2]) && atom1.getId().equals(atomRefs4[1]))
				) {
					double d = Double.NaN;
					try {
						d = torsion.getXMLContent();
					} catch (CMLRuntimeException e) {
						// no value given
						continue;
						// empty torsion;
					}
					CMLAtomSet moveableAtomSet =
						this.getDownstreamAtoms(atom1, atom0);
					torsion.adjustCoordinates(new Angle(d, Angle.Units.DEGREES),
							moleculeAtomSet, moveableAtomSet);
					// this is to avoid the torsion being reused
					torsion.removeAttribute("atomRefs4");
				}
			}
		}
	}

	/** flatten molecules.
	 *
	 * @param parent
	 */
	public void flattenMoleculeDescendants(ParentNode parent) {
		Elements moleculeLists = molecule.getChildCMLElements(CMLMoleculeList.TAG);
		List<CMLMoleculeList> moleculeListList = new ArrayList<CMLMoleculeList>();
		for (int i = 0; i < moleculeLists.size(); i++) {
			moleculeListList.add((CMLMoleculeList) moleculeLists.get(i));
		}
		for (CMLMoleculeList moleculeList : moleculeListList) {
			flattenMoleculeListDescendants(moleculeList, molecule);
			if (parent != null) {
				moleculeList.detach();
			} else {
				CMLUtil.transferChildren(moleculeList, molecule);
				moleculeList.detach();
			}
		}
	}

	private void flattenMoleculeListDescendants(
			CMLMoleculeList moleculeList, CMLMolecule parentMolecule) {
		ParentNode grandParent = parentMolecule.getParent();
		int idx = grandParent.indexOf(parentMolecule);
		//		 moleculeList.debug("MOLL");
		Elements childs = moleculeList.getChildElements();
		for (int i = 0; i < childs.size(); i++) {
			Node child = childs.get(i);
			if (child instanceof Text) {
				child.detach();
			} else {
				if (grandParent instanceof CMLElement) {
					child.detach();
					grandParent.insertChild(child, ++idx);
				}
				if (child instanceof CMLMolecule) {
					MoleculeTool childTool = new MoleculeTool((CMLMolecule) child);
					childTool.flattenMoleculeDescendants(moleculeList);
				}
			}
		}
	}


}