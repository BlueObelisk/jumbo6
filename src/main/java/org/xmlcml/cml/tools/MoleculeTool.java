package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import nu.xom.Attribute;
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
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondArray;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
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
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Util;
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

	Logger logger = Logger.getLogger(MoleculeTool.class.getName());

	public static String metalLigandDictRef = "jumbo:metalLigand";

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
	
	public final static int UNREALISTIC_CHARGE = 99;

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
			int molCharge = UNREALISTIC_CHARGE;
			boolean isMetalComplex = false;
			for (CMLFormula formula : moietyFormulaList) {
				CMLFormula molForm = mol.calculateFormula(HydrogenControl.USE_EXPLICIT_HYDROGENS);
				if (molForm.getConciseNoCharge().equals(formula.getConciseNoCharge())) {
					molCharge = formula.getFormalCharge();
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
			//if the removing of metal atoms takes the molecules atom to zero
			//then don't bother calculating bonds
			if (mol.getAtomCount() > 1) {
				// now the metal atoms and bonds have been removed, partition 
				// molecule into submolecules and then calculate the bond orders
				// and charges
				ctt.partitionIntoMolecules();
				List<CMLMolecule> subMols = mol.getDescendantsOrMolecule();
				for (CMLMolecule subMol : subMols) {
					System.out.println("-----------");
					MoleculeTool subMolTool = new MoleculeTool(subMol);
					ValencyTool valencyTool = new ValencyTool(subMol);
					boolean common = valencyTool.markupCommonMolecules();
					if (!common) {
						valencyTool.markupSpecial();
						subMol.setNormalizedBondOrders();
						// get list of bonds that have not been set by 
						// markupSpecial or markupCommonMolecules
						// do this so these are are the only bonds that are
						// reset further down the method
						List<CMLBond> singleBonds = new ArrayList<CMLBond>();
						for (CMLBond bond : subMol.getBonds()) {
							if (CMLBond.SINGLE.equals(bond.getOrder())) {
								singleBonds.add(bond);
							}
						}
						// get list of atoms whose charge has been set by
						// markupSpecial or markupCommonMolecules
						// do this so these are not reset further down the
						// method
						List<CMLAtom> alreadySetChargedAtoms = new ArrayList<CMLAtom>();
						for (CMLAtom atom : subMol.getAtoms()) {
							Nodes nodes = atom.query(".//@formalCharge[.!=0]", X_CML);
							if (nodes.size() > 0) {
								alreadySetChargedAtoms.add(atom);
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
									alreadySetChargedAtoms.add(piAtom);
								} else if ("N".equals(atomElType)) {
									int ligandNum = piAtom.getLigandBonds().size();
									piAtom.setFormalCharge(-3+ligandNum);
								} else if ("C".equals(atomElType)) {
									//FIXME - this surely can't always work
									piAtom.setFormalCharge(1);
								}
							} else {
								List<CMLAtom> piAtomList = piSys.getAtomList();
								for (CMLAtom atom : piAtomList) {
									// don't want to include atoms that have already had formal charge set
									// by markupCommonMolecules or markupSpecial
									if (alreadySetChargedAtoms.contains(atom)) {
										continue;
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
								// don't want to include atoms that have already had formal charge set
								// by markupCommonMolecules or markupSpecial
								if (alreadySetChargedAtoms.contains(atom)) {
									continue;
								}
								if ("O".equals(atom.getElementType()) || "S".equals(atom.getElementType())) {
									if (atom.getLigandAtoms().size() == 1) {
										osList.add(atom);
									}
								}
							}
						}
						
						// take all combinations of charges on the atoms found and attempt to 
						// get a completed pi-system.
						List<List<Integer>> n3ComboList = CMLUtil.generateCombinationList(n3List.size());
						List<List<Integer>> osComboList = CMLUtil.generateCombinationList(osList.size());
						List<List<Integer>> n2ComboList = CMLUtil.generateCombinationList(n2List.size());
						//System.out.println(n3ComboList.size()+","+osComboList.size()+","+n2ComboList.size());
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
									if (!alreadySetChargedAtoms.contains(atom)) {
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
						n2:
						if (finalMolList.size() == 0) {
							System.out.println("Trying N2-");
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
											//System.out.println("setting n2: "+atom.getId());
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
													break n2;
												}
											}
										}
//										reset charges on charged atoms
										for (CMLAtom atom : chargedAtoms) {
											if (!alreadySetChargedAtoms.contains(atom)) {
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
						}
						cAttempt:
							if (finalMolList.size() == 0) {
								int cCharge = -1;
								System.out.println("Trying carbanions....");
								for (CMLAtom atom : subMolAtomList) {
									if ("C".equals(atom.getElementType()) && atom.getLigandAtoms().size() == 3) {
										atom.setFormalCharge(cCharge);
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
			Nodes nodes = molecule.query(".//"+CMLScalar.NS+"[@dictRef='"+metalLigandDictRef+"']", X_CML);
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).detach();
			}
		}
		System.out.println("finished");
	}
	
	public int getFormalCharge() {
		int formalCharge = 0;
		Nodes chargedAtoms = molecule.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge]", X_CML);
		for (int i = 0; i < chargedAtoms.size(); i++) {
			formalCharge += Integer.parseInt(((Element)chargedAtoms.get(i)).getAttributeValue("formalCharge"));
		}
		return formalCharge;
	}
	
	public List<CMLAtom> getChargedAtoms() {
		List<CMLAtom> chargedAtoms = new ArrayList<CMLAtom>();
		Nodes atoms = molecule.getAtomArray().query(".//"+CMLAtom.NS+"[@formalCharge != 0]", X_CML);
		for (int i = 0; i < atoms.size(); i++) {
			chargedAtoms.add((CMLAtom)atoms.get(i));
		}
		return chargedAtoms;
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
				logger.info("Unknown bond order:" + order + S_COLON);
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
	private void getHybridizationFromConnectivty() {
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
			for (int j = 0; j < atoms.size(); j++) {
				if (valence[j] < 0) {
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
				change = assignMissingBonds(valence, j, bondSum, aromSum,
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
		if (currentAtom.atomIsCompatible(typeList)) {
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
				if (!ligand.atomIsCompatible(typeList)) {
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
				if (atom.atomIsCompatible(typeList)) {
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
	private List<CMLAtom> getSubstituentLigandList(CMLBond bond, CMLAtom atom)
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


	 /* Traverses all non-H atoms and contracts the hydrogens on each.
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

	private void calculateBondedAtoms(List<CMLAtom> atoms) {
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

	private double getCovalentRadius(String sym) {
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

	private double getCustomCovalentRadius(String sym) {
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
			double length = bond.calculateBondLength(type);
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
			RemoveDisorderControl rControl) {

		List<DisorderAssembly> disorderAssemblyList = null;
		disorderAssemblyList = this.getDisorderAssemblyList();
		disorderAssemblyList = checkDisorder(disorderAssemblyList, pControl);
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
		// if the process reaches this point without an error being thrown then
		// the disorder can be processed.  Add metadata to say so!
		if (pControl.equals(ProcessDisorderControl.STRICT)) {
			addDisorderMetadata(true);
			metadataSet = true;
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

//	private void flattenJoinMoleculeChildren() {
//		int idx = molecule.getParent().indexOf(molecule);
//		Nodes moleculesAndJoin = molecule.query(CMLMolecule.NS+X_OR+CMLJoin.NS+"'",
//				X_CML);
//		for (int i = 0; i < moleculesAndJoin.size(); i++) {
//			Node node = moleculesAndJoin.get(i);
//			node.detach();
//			molecule.getParent().insertChild(node, idx + 1 + i);
//		}
//	}
//

	/**
	 * join one molecule to another. 
	 * manages the XML but not yet the geometry
	 * empties the added molecule of elements and copies them
	 * to this.molecule and then detaches the addedMolecule
	 * @param addedMolecule to be joined
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
				// don't copy
			} else if (node instanceof CMLBondArray) {
				// don't copy
			} else {
				molecule.appendChild(node);
			}
		}
		addedMolecule.detach();
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

	/** iterates through torsions in molecule.
	 * if they contain atom0 and atom1, adjusts them to value of torsion
	 * @param atom0
	 * @param atom1
	 */
	public void adjustTorsions(CMLAtom atom0, CMLAtom atom1) {
		if (molecule.hasCoordinates(CoordinateType.CARTESIAN)) {
			// set torsions
			CMLAtomSet moleculeAtomSet = molecule.getAtomSet();
			Nodes torsions = molecule.query(".//"+CMLTorsion.NS, X_CML);
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

	/** copies attributes on bonds and atoms to another molecule.
	 * for each atom/bond in this.molecule finds Id and hence corresponding 
	 * atom/bond in 'to'. Copies all attributes from that atom to to.atom/@*
	 * If corresponding atom does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception CMLRuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	public void copyAtomAndBondAttributesById(CMLMolecule to, boolean permitOverwrite) {
		copyAtomAttributesById(to, permitOverwrite);
		copyBondAttributesById(to, permitOverwrite);
	}
	
	/** copies attributes on atoms to another molecule.
	 * for each atom in this.molecule finds Id and hence corresponding 
	 * atom in 'to'. Copies all attributes from that atom to to.atom/@*
	 * If corresponding atom does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception CMLRuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	
	public void copyAtomAttributesById(CMLMolecule to, boolean permitOverwrite) {
		List<CMLAtom> fromAtoms = molecule.getAtoms();
		for (CMLAtom fromAtom : fromAtoms) {
			String fromId = fromAtom.getId();
			if (fromId != null) {
				CMLAtom toAtom = to.getAtomById(fromId);
				if (toAtom == null) {
					throw new CMLRuntimeException("Cannot find target atom: "+fromId);
				}
				copyAttributes(fromAtom, toAtom, permitOverwrite);
			}
		}
	}
	
	
	/** copies attributes on bonds to another molecule.
	 * for each bond in this.molecule finds Id and hence corresponding 
	 * bond in 'to'. Copies all attributes from that bond to to.bond/@*
	 * If corresponding bond does not exist, throws exception.
	 * If target attribute exists throws exception
	 * @param to
	 * @param permitOverwrite allow existing attributes on target to be overwritten
	 * @exception CMLRuntimeException ids in molecules do not correspond or
	 * attributes are already present
	 */
	
	public void copyBondAttributesById(CMLMolecule to, boolean permitOverwrite) {
		List<CMLBond> fromBonds = molecule.getBonds();
		for (CMLBond fromBond : fromBonds) {
			String fromId = fromBond.getId();
			if (fromId != null) {
				CMLBond toBond = to.getBondById(fromId);
				if (toBond == null) {
					throw new CMLRuntimeException("Cannot find target bond: "+fromId);
				}
				copyAttributes(fromBond, toBond, permitOverwrite);
			}
		}
	}

	private void copyAttributes(Element from, Element to, boolean permitOverwrite) {
		List<Node> nodes = CMLUtil.getQueryNodes(from, "./@*");
		for (Node node : nodes) {
			Attribute attribute = (Attribute) node;
			String name = attribute.getLocalName();
			if ("id".equals(name)) {
				continue;
			}
			if (!permitOverwrite && to.getAttribute(name) != null) {
				throw new CMLRuntimeException("cannot overwrite attribute: "+name);
			}
			Attribute newAttribute = new Attribute(name, attribute.getValue());
			to.addAttribute(newAttribute);
		}
	}

	public static boolean isDisordered(CMLMolecule molecule) {
		for (CMLAtom atom : molecule.getAtoms()) {
			List<Node> nodes = CMLUtil.getQueryNodes(atom,
    				".//"+CMLScalar.NS+"[@dictRef='"+CrystalTool.DISORDER_ASSEMBLY+"'] | "+
    				".//"+CMLScalar.NS+"[@dictRef='"+CrystalTool.DISORDER_GROUP+"']", X_CML);
			if (nodes.size() > 0) {
				return true;
			}
		}
		return false;
	}
}