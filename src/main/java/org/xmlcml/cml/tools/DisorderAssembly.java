package org.xmlcml.cml.tools;

/**
 * Disorder Assembly as defined in CIF.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;

/** based on IUCr Core CIF dictionary 
 * 
atom_site_disorder_assembly


_name _atom_site_disorder_assembly
_category atom_site
_type char
_list yes
_list_reference _atom_site_label

_example    _detail
A   disordered methyl assembly with groups 1 and 2
B   disordered sites related by a mirror
S   disordered sites independent of symmetry

_definition  A code which identifies a cluster of atoms that show long-range
positional disorder but are locally ordered. Within each such
cluster of atoms, _atom_site_disorder_group is used to identify
the sites that are simultaneously occupied. This field is only
needed if there is more than one cluster of disordered atoms
showing independent local order.
 * 
 * @see DisorderGroup
 * Experiemntal.
 * 
 * A disordered crysta; should have at least one DosrderAssembly. If
 * this is not explicit in the file, all disordered atoms are in a 
 * single DisorderAssembly. 
 * Each DisorderAssembly should contain at least 2 DisorderGroups. 
 * Within a given DisorderGroup all the occupancies should be identical
 * It is likely that the occupancies of the disorderGroups will sum to
 * 1.0. The DisorderGroups may have different chemical constitutions
 * (e.g. a CO2H might be partially replaced by a CONH2 group
 * 
 * @author pmr
 * 
 */
public class DisorderAssembly implements CMLConstants {

    private List<DisorderGroup> groupList = null;
    private Map<String, DisorderGroup> groupMap = null;
    private Set<CMLAtom> commonAtoms = null;
    // atoms common to all sub groups
    private String assemblyCode = "NO ATOMS ADDED YET";

    /** constructor.
     */
    public DisorderAssembly() {
        groupMap = new HashMap<String, DisorderGroup>();
        commonAtoms = new HashSet<CMLAtom>();
    }
    
    /** atoms common to all parts of assembly.
     * 
     * @return list of atoms
     */
    public List<CMLAtom> getCommonAtoms() {
    	return new ArrayList<CMLAtom>(commonAtoms);
    }

    /** finds all atoms with child flags (scalar) indicating disorder, or those atoms
     * with less than unit occupancy.
     * no analysis is done
     * @param molecule
     * @return list of atoms flagged as disordered
     */
    public static List<CMLAtom> getDisorderedAtoms(CMLMolecule molecule) {
    	if(molecule == null) {
    		throw new IllegalArgumentException("Molecule must not be null");
    	}
        Nodes nodes = molecule.query(
            ".//"+CMLAtom.NS+"["+CMLScalar.NS+"[" +
            "(@dictRef='"+CrystalTool.DISORDER_ASSEMBLY+"' and .!='.') or " +
            "(@dictRef='"+CrystalTool.DISORDER_GROUP+"' and .!='.')" +
            "]] | .//"+CMLAtom.NS+"[@occupancy[. < 1]]", CMLConstants.CML_XPATH);
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        for (int i = 0; i < nodes.size(); i++) {
            atomList.add((CMLAtom) nodes.get(i));
        }
        
        return atomList;
    }
    
    /** add atom to common atoms.
     * 
     * @param atom
     */
    public void addCommonAtom(CMLAtom atom) {
        if (commonAtoms.contains(atom)) {
            throw new RuntimeException("atom is already common to assembly "+
                    CrystalTool.getFullLabel(atom));
        }
        commonAtoms.add(atom);
    }

    /** get list of disorderAssemblys.  Throws an exception if it comes across disorder
     * which doesn't comply with the CIF specification.
     * 
     * @param disorderedAtomList atoms to use
     * @return list of assemblies (or empty list)
     */
    public static List<DisorderAssembly> getDisorderedAssemblyList
        (List<CMLAtom> disorderedAtomList) {

    	List<DisorderAssembly> assemblyList = new ArrayList<DisorderAssembly>();
    	Map<String, DisorderAssembly> assemblyMap = new HashMap<String, DisorderAssembly>();
    	for (CMLAtom atom : disorderedAtomList) {
    		String assemblyCode = DisorderAssembly.getAtomCode(atom);
    		if (assemblyCode == null) {
    			assemblyCode = CMLConstants.S_PERIOD;
    		}
    		
    		DisorderAssembly assembly = assemblyMap.get(assemblyCode);
			if (assembly == null) {
				assembly = new DisorderAssembly();
				assemblyMap.put(assemblyCode, assembly);
				assemblyList.add(assembly);
			}
			assembly.addAtom(atom);
    	}
    	return assemblyList;
    }

    /** gets disorder_assembly code.
     * 
     * @see CrystalTool#isIndeterminate(String)
     * @param atom with child disorder flags
     * @return code or null if indterminate
     */
    public static String getAtomCode(CMLAtom atom) {
    	String atomCode = CrystalTool.getValue(atom,
    			CMLScalar.NS+"[@dictRef='"+CrystalTool.DISORDER_ASSEMBLY+"']");
    	return (atomCode != null) ? atomCode : ".";
    }
    
    /** gets assembly code
     * @return assembly code
     */
    public String getAssemblyCode() {
    	return assemblyCode;
    }

    /** add atom.
     * no checking at present
     * @param atom
     */
    public void addAtom(CMLAtom atom) {
    	String groupCode = DisorderGroup.getAtomCode(atom);
    	String assCode = DisorderAssembly.getAtomCode(atom);
    	if (groupCode == null) {
    		groupCode = CMLConstants.S_PERIOD;
    	}
    	if (assCode == null) {
    		assCode = CMLConstants.S_PERIOD;
    		addCommonAtom(atom);
    	} else {
    		assemblyCode = assCode;
    		if (S_PERIOD.equals(groupCode)) {
    			this.addCommonAtom(atom);
    		} else {
    			DisorderGroup group = groupMap.get(groupCode);
    			if (group == null) {
    				group = new DisorderGroup(this);
    				groupMap.put(groupCode, group);
    			}
    			group.addAtom(atom);
    		}
    	}
    }

    /** get the disorderGroups sorted by occupancy.
     * 
     * @return the list
     */
    public List<DisorderGroup> getDisorderGroupList() {
        if (groupList == null) {
            groupList = new ArrayList<DisorderGroup>();
            for (String s : groupMap.keySet()) {
                groupList.add(groupMap.get(s));
            }
        }
        Collections.sort(groupList);
        return groupList;
    }

    /** remove all minor groups.
     * only the group with largets occupancy is retained
     */
    public void removeMinorDisorder() {
        getDisorderGroupList();
        Collections.sort(groupList);
        for (int i = 0; i < groupList.size()-1; i++) {
        	groupList.get(i).detachAtoms();
        }   
    }

    /** string representation.
     * @return string
     */
    public String toString() {
        String s = "disorderAssembly: "+assemblyCode+S_NEWLINE;
        s += "common atoms: "+S_NEWLINE;
        for (CMLAtom commonAtom : commonAtoms) {
            s += CMLConstants.S_SPACE+S_SPACE+CrystalTool.getFullLabel(commonAtom)+S_NEWLINE;
        }
        s += "disorderGroups: "+S_NEWLINE;
        for (String groupName : groupMap.keySet()) {
            s += groupMap.get(groupName);
        }
        return s;
    }
};