package org.xmlcml.cml.tools;

import java.util.List;

import nu.xom.Node;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.tools.DisorderManager.ProcessControl;
import org.xmlcml.cml.tools.DisorderManager.RemoveControl;

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
		List<DisorderAssembly> disorderAssemblyList = getDisorderAssemblyList();
		disorderAssemblyList = checkDisorder(disorderAssemblyList);
		processDisorder(disorderAssemblyList);
	}
	
	private void processDisorder(List<DisorderAssembly> disorderAssemblyList) {
		if (RemoveControl.REMOVE_MINOR_DISORDER.equals(disorderManager
				.getRemoveControl())) {
			for (DisorderAssembly assembly : disorderAssemblyList) {
				assembly.removeMinorDisorder();
			}
		}
	}
	
	private boolean	processAssemblyWithLessThan2Groups(DisorderAssembly disorderAssembly) {
		System.out.println(disorderAssembly.getAssemblyCode());
		System.out.println(disorderAssembly.toString());
		return true;
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
			List<DisorderAssembly> disorderAssemblyList) {
		ProcessControl pControl = disorderManager.getProcessControl();
		boolean metadataSet = false;
		for (DisorderAssembly da : disorderAssemblyList) {
			List<DisorderGroup> disorderGroupList = da.getDisorderGroupList();
			if (disorderGroupList.size() < 2) {
				if (ProcessControl.LOOSE.equals(pControl)) {
					boolean success = processAssemblyWithLessThan2Groups(da);
					if (!metadataSet && success) {
						addDisorderMetadata(false);
						metadataSet = true;
					}
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
						if (!metadataSet) {
							addDisorderMetadata(false);
							metadataSet = true;
						}
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
							if (!metadataSet) {
								addDisorderMetadata(false);
								metadataSet = true;
							}
						} else if (ProcessControl.STRICT.equals(pControl)) {
							throw new CMLRuntimeException("Atom, "
									+ atom.getId() + ", in disorder group, "
									+ dg.getGroupCode()
									+ ", has unit occupancy");
						}
					}
					if (Math.abs(atom.getOccupancy() - dg.getOccupancy()) > CrystalTool.OCCUPANCY_EPS) {
						if (ProcessControl.LOOSE.equals(pControl)) {
							if (!metadataSet) {
								addDisorderMetadata(false);
								metadataSet = true;
							}
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
		}
		// if the process reaches this point without an error being thrown then
		// the disorder can be processed. Add metadata to say so!
		if (ProcessControl.STRICT.equals(pControl)) {
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
