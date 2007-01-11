package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.DisorderManager.ProcessControl;
import org.xmlcml.cml.tools.DisorderManager.RemoveControl;
import org.xmlcml.util.CombinationGenerator;
import org.xmlcml.util.Partition;

public class DisorderTool extends AbstractTool {

	private CMLMolecule molecule;

	private DisorderManager disorderManager;

	private DisorderTool() {
		;
	}

	/**
	 * constructor
	 * 
	 * @param molecule
	 */
	public DisorderTool(CMLMolecule molecule) {
		this(molecule, new DisorderManager());
	}

	public DisorderTool(CMLMolecule mol, DisorderManager disorderMan) {
		if (mol == null) {
			throw new IllegalArgumentException("Molecule cannot be null");
		}
		if (disorderMan == null) {
			throw new IllegalArgumentException(
					"Disorder manager cannot be null");
		}
		this.molecule = mol;
		this.disorderManager = disorderMan;
	}
	
	/**
	 * gets the cml molecule.
	 * 
	 * @return molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
	}

	/**
	 * Resolves crystallographic disorder.
	 * 
	 * At present the only RemoveDisorderControl is to remove minor disorder.
	 * These are components with occupancies <= 0.5.
	 * 
	 * Can provide two different kinds of ProcessDisorderControl: 1. STRICT -
	 * this tries to process the disorder as set out in the CIF specification
	 * (http://www.iucr.org/iucr-top/cif/#spec) If it comes across disorder
	 * which does not comply with the spec then throws an error.
	 * 
	 * 2. LOOSE - this tries to process the disorder as with STRICT, but does
	 * not throw an error if it comes across disorder not valid with respect to
	 * the CIF spec. Instead it tags the provided molecule with metadata stating
	 * that the disorder cannot currently be processed and does nothing more.
	 * 
	 * NOTE: if the disorder is successfully processed then the molecule is
	 * tagged with metadata stating that the molecule did contain disorder, but
	 * has now been removed.
	 * 
	 * @param pControl
	 * @param rControl
	 * @exception CMLRuntimeException
	 */
	public void resolveDisorder() {
		if (molecule == null) {
			throw new IllegalStateException(
					"Molecule property is null, no operations possible");
		}
		List<DisorderAssembly> disorderAssemblyList = checkDisorder();
		processDisorder(disorderAssemblyList);
	}
	
	/**
	 * 
	 * @param disorderAssemblyList
	 */
	private void processDisorder(List<DisorderAssembly> disorderAssemblyList) {
		if (RemoveControl.REMOVE_MINOR_DISORDER.equals(disorderManager
				.getRemoveControl())) {
			for (DisorderAssembly assembly : disorderAssemblyList) {
				assembly.removeMinorDisorder();
			}
		}
		// remove all atom disorder information after processing
		removeAtomDisorderInformation();
	}
	
	/**
	 * removes all disorder elements and attributes from each disordered atom.
	 *
	 */
	private void removeAtomDisorderInformation() {
		List<Node> nodes = CMLUtil.getQueryNodes(molecule, ".//"+CMLAtom.NS+"/@occupancy | "
				+".//"+CMLAtom.NS+"/"+CMLScalar.NS+"[contains(@dictRef, '"+CrystalTool.DISORDER_ASSEMBLY+"') or " +
	            "contains(@dictRef, '"+CrystalTool.DISORDER_GROUP+"')" +
	            "]", X_CML);
		for (Node node : nodes) {
			node.detach();
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
	private List<DisorderAssembly> checkDisorder() {
		List<DisorderAssembly> disorderAssemblyList = getDisorderAssemblyList();
		ProcessControl pControl = disorderManager.getProcessControl();
		boolean isMetadataSet = false;
		List<DisorderAssembly> failedAssemblyList = new ArrayList<DisorderAssembly>();
		List<DisorderAssembly> finishedAssemblyList = new ArrayList<DisorderAssembly>();
		for (DisorderAssembly da : disorderAssemblyList) {
			List<DisorderGroup> disorderGroupList = da.getDisorderGroupList();
			if (disorderGroupList.size() < 2) {
				if (ProcessControl.LOOSE.equals(pControl)) {
					failedAssemblyList.add(da);
					continue;
				} else if (ProcessControl.STRICT.equals(pControl)) {
					throw new CMLRuntimeException(
							"Disorder assembly should contain at least 2 disorder groups: "
									+ da.toString());
				}
			}
			List<CMLAtom> commonAtoms = da.getCommonAtoms();
			for (CMLAtom commonAtom : commonAtoms) {
				if (!CrystalTool.hasUnitOccupancy(commonAtom)) {
					if (ProcessControl.LOOSE.equals(pControl)) {
						failedAssemblyList.add(da);
						continue;
					} else if (ProcessControl.STRICT.equals(pControl)) {
						throw new CMLRuntimeException(
								"Common atoms require unit occupancy: "
										+ commonAtom.getId()
										+ ", in disorder assembly, "
										+ da.getAssemblyCode());
					}
				}
			}
			for (DisorderGroup dg : disorderGroupList) {
				List<CMLAtom> atomList = dg.getAtomList();
				for (CMLAtom atom : atomList) {
					if (CrystalTool.hasUnitOccupancy(atom)) {
						if (ProcessControl.LOOSE.equals(pControl)) {
							failedAssemblyList.add(da);
							continue;
						} else if (ProcessControl.STRICT.equals(pControl)) {
							throw new CMLRuntimeException("Atom, "
									+ atom.getId() + ", in disorder group, "
									+ dg.getGroupCode()
									+ ", has unit occupancy");
						}
					}
					if (Math.abs(atom.getOccupancy() - dg.getOccupancy()) > CrystalTool.OCCUPANCY_EPS) {
						if (ProcessControl.LOOSE.equals(pControl)) {
							failedAssemblyList.add(da);
							continue;
						} else if (ProcessControl.STRICT.equals(pControl)) {
							throw new CMLRuntimeException(
									"Atom "
											+ atom.getId()
											+ ", in disorder group, "
											+ dg.getGroupCode()
											+ ", has inconsistent occupancy ("
											+ atom.getOccupancy()
											+ ") with that of the disorder group ("
											+ dg.getOccupancy()+").");
						}
					}
				}
			}
			finishedAssemblyList.add(da);
		}
		// attempt to reconcile errors in disorder assemblies
		boolean fixed = false;
		if (failedAssemblyList.size() > 0) {
			fixed = fixFailedAssemblies(failedAssemblyList);
			if (fixed) {
				// if process reaches this point then failed assemblies have been
				// fixed - add them to finished list
				//molecule.debug();
				DisorderTool newDt = new DisorderTool(molecule);
				List<DisorderAssembly> newDA = newDt.getDisorderAssemblyList();
				
				// if the process reaches this point without an error being thrown then
				// the disorder can be processed. Add metadata to say so!
				addDisorderMetadata(true);
				isMetadataSet = true;
				return newDA;
			} else {
				if (!isMetadataSet) {
					addDisorderMetadata(false);
					isMetadataSet = true;
				}
				throw new CMLRuntimeException("Could resolve invalid disorder.");
			}
		}
		return finishedAssemblyList;
	}

	/**
	 * takes a list of disorder assemblies that are invalid with respect to the CIF
	 * specification and tries to create a set of assemblies that are valid so that 
	 * they can be parsed.
	 * 
	 * @param failedAssemblyList
	 * @return whether a set of valid assemblies has been found.
	 */
	private boolean fixFailedAssemblies(List<DisorderAssembly> failedAssemblyList) {
		// aggregate all disordered atoms from the failed assemblies
		List<CMLAtom> disorderedAtoms = new ArrayList<CMLAtom>();
		for (DisorderAssembly failedAssembly : failedAssemblyList) {
			disorderedAtoms.addAll(failedAssembly.getCommonAtoms());
			for (DisorderGroup group : failedAssembly.getDisorderGroupList()) {
				disorderedAtoms.addAll(group.getAtomList());
			}
		}
		// attempt to resolve failed assemblies, by first splitting the atoms into
		// lists of similar occupancy.  Then test to see if there are 2 or more that
		// add up to unity (fail if not so or if any lists left over)
		List<CMLAtom> unityAtoms = new ArrayList<CMLAtom>();
		Map<Double, List<CMLAtom>> atomMap = new LinkedHashMap<Double, List<CMLAtom>>();
		for (CMLAtom disorderedAtom : disorderedAtoms) {
			//System.out.println("processing: "+disorderedAtom.getId());
			boolean added = false;
			double occupancy = disorderedAtom.getOccupancy();
			// test to see if any of the lists has occupancy of 0.5.  If so then throw
			// exception as we cannot be confident of getting the proper structure if there
			// are more than one assemblies with this occupancy
			if (occupancy == 0.5) {
				throw new CMLRuntimeException("Cannot fix invalid disorder - found atom"
						+" with occupancy of 0.5");
				
			}
			if (occupancy == Double.NaN) {
				throw new CMLRuntimeException("Atom "+disorderedAtom+" has no occupancy set.  Cannot resolve disorder.");
			}
			//System.out.println("occupancy is: "+occupancy);
			// if atom has unit occupancy then we can ignore it for the rest of 
			// the method
			if (Math.abs(1.0 - occupancy) < CrystalTool.OCCUPANCY_EPS) {
						unityAtoms.add(disorderedAtom);
						continue;
					}
			// counter to make sure the same atom isn't added to two different
			// lists
			int addedCount = 0;
			for (Iterator it=atomMap.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry entry = (Map.Entry) it.next();
				if (Math.abs((Double)entry.getKey() - occupancy) < CrystalTool.OCCUPANCY_EPS) {
					((List<CMLAtom>)entry.getValue()).add(disorderedAtom);
					added = true;
					addedCount++;
					//System.out.println("adding to current list of occupancy: "+(Double)entry.getKey());
				}
			}
			// if atom added to more than one list then throw exception
			if (addedCount > 1) throw new CMLRuntimeException("Ambiguous atom occupancy.  Cannot resolve disorder");
			if (added) continue;
			List<CMLAtom> newList = new ArrayList<CMLAtom>();
			newList.add(disorderedAtom);
			atomMap.put(occupancy, newList);
		}
		boolean fixed =  reassignDisorderGroups(atomMap);
		if (fixed) {
			//molecule.debug();
			// clear disorder information from unity atoms
			for (CMLAtom atom : unityAtoms) {
				List<Node> nodes = CMLUtil.getQueryNodes(atom, ".//"+CMLScalar.NS+"[" +
			            "contains(@dictRef, '"+CrystalTool.DISORDER_ASSEMBLY+"') or " +
			            "contains(@dictRef, '"+CrystalTool.DISORDER_GROUP+"')" +
			            "]", X_CML);
				for (Node node : nodes) {
					node.detach();
				}
			}
		}
		return fixed;
	}
	
	/**
	 * Takes a map of occupancies related to their atoms with that occupancy.  Tries
	 * to decipher the correct disorder assemblies and groups from the occupancies
	 * supplied.
	 * 
	 * @param atomMap
	 * @return whether or not the disorder groups have been successfully reassigned
	 */
	private boolean reassignDisorderGroups(Map<Double, List<CMLAtom>> atomMap) {
		if (atomMap.size() < 2) {
			throw new CMLRuntimeException("Cannot resolve disorder. Only one non-unit occupancy value found in molecule.");
		}
		List<Double> occupancyList = new LinkedList<Double>(atomMap.keySet());
		List<List<Integer>> partitionList = Partition.partition(atomMap.size());
		for (List<Integer> partition : partitionList) {
			for (Integer in : partition) {
				String assemblyCode = "";
				int groupCode = 1;
				CombinationGenerator cg = new CombinationGenerator(atomMap.size(), in);
				// list to keep track of the occupancies used in this iteration
				List<Double> dList = new ArrayList<Double>();
				while (cg.hasMore()) {
					double occCount = 0.0;
					int[] indices = cg.getNext();
					for (int i : indices) {
						double occ = occupancyList.get(i);
						// check if already contained in dList
						boolean inDList = false;
						for (Double d : dList) {
							if (d.compareTo(occ) == 0) {
								inDList = true;
							}
						}
						if (!inDList) dList.add(occ);
						occCount += occ;
					}
					if (Math.abs(1.0 - occCount) < CrystalTool.OCCUPANCY_EPS) {
						assemblyCode += "z";
						for (int i : indices) {
							List<CMLAtom> atoms = atomMap.get(occupancyList.get(i));
							for (CMLAtom atom : atoms) {
								List<Node> assemblyNodes = CMLUtil.getQueryNodes(atom, ".//"+CMLScalar.NS+"[" +
										"contains(@dictRef, '"+CrystalTool.DISORDER_ASSEMBLY+"')]", X_CML);
								if (assemblyNodes.size() > 1) {
									throw new CMLRuntimeException("Atom "+atom.getId()+" contains more than one"
											+" disorder assembly.");
								} else if (assemblyNodes.size() > 0) {
									((CMLScalar)assemblyNodes.get(0)).setValue(assemblyCode.toString());
								} else if (assemblyNodes.size() == 0) {
									CMLElement scalar = new CMLElement(CMLScalar.TAG);
									atom.appendChild(scalar);
									scalar.addAttribute(new CMLAttribute("dictRef", CrystalTool.DISORDER_ASSEMBLY));
									scalar.addAttribute(new CMLAttribute("dataType", "xsd:string"));
									scalar.setStringContent(assemblyCode.toString());
								}
								List<Node> groupNodes = CMLUtil.getQueryNodes(atom, ".//"+CMLScalar.NS+"[" +
										"contains(@dictRef, '"+CrystalTool.DISORDER_GROUP+"')]", X_CML);
								if (groupNodes.size() > 1) {
									throw new CMLRuntimeException("Atom "+atom.getId()+" contains more than one"
											+" disorder group.");
								} else if (groupNodes.size() > 0) {
									((CMLScalar)groupNodes.get(0)).setValue(groupCode);
								} else if (groupNodes.size() == 0) {
									CMLElement scalar = new CMLElement(CMLScalar.TAG);
									atom.appendChild(scalar);
									scalar.addAttribute(new CMLAttribute("dictRef", CrystalTool.DISORDER_GROUP));
									scalar.addAttribute(new CMLAttribute("dataType", "xsd:string"));
									scalar.setStringContent(String.valueOf(groupCode));
								}
							}
							groupCode++;
						}
					}
				}
				// if the number of occupancies used is the same as the total 
				// number then we have found a solution
				if (dList.size() == occupancyList.size()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * adds metadata to molecule stating whether or not the disorder 
	 * has been successfully processed.
	 * 
	 * @param processed - whether or not the disorder has been successfully processed
	 */
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
	 * returns whether or not this molecule is disordered.
	 * 
	 * @param molecule
	 * @return
	 */
	public static boolean isDisordered(CMLMolecule molecule) {
		for (CMLAtom atom : molecule.getAtoms()) {
			List<Node> nodes = CMLUtil.getQueryNodes(atom, ".//" + CMLScalar.NS
					+ "[@dictRef='" + CrystalTool.DISORDER_ASSEMBLY + "'] | "
					+ ".//" + CMLScalar.NS + "[@dictRef='"
					+ CrystalTool.DISORDER_GROUP + "']", X_CML);
			if (nodes.size() > 0) {
				return true;
			}
		}
		return false;
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
		if (molecule == null) {
			throw new IllegalStateException(
					"Molecule is null, no operations possible");
		}
		List<CMLAtom> disorderedAtomList = DisorderAssembly
				.getDisorderedAtoms(molecule);
		List<DisorderAssembly> disorderAssemblyList = DisorderAssembly
				.getDisorderedAssemblyList(disorderedAtomList);
		return disorderAssemblyList;
	}
}