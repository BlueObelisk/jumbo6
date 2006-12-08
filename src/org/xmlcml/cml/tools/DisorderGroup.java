package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;

/** based on IUCr Core CIF dictionary 
 * 
atom_site_disorder_group


_name _atom_site_disorder_group
_category atom_site
_type char
_list yes
_list_reference _atom_site_label

_example    _detail
1   unique disordered site in group 1
2   unique disordered site in group 2
-1  symmetry-independent disordered site

_definition  A code which identifies a group of positionally disordered atom
sites that are locally simultaneously occupied. Atoms that are
positionally disordered over two or more sites (e.g. the hydrogen
atoms of a methyl group that exists in two orientations) can
be assigned to two or more groups. Sites belonging to the same
group are simultaneously occupied, but those belonging to
different groups are not. A minus prefix (e.g. "-1") is used to
indicate sites disordered about a special position.
 * 
 * @see DisorderAssembly
 * 
 * Experimental.
 * 
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
public class DisorderGroup implements CMLConstants, Comparable<DisorderGroup> {

    DisorderAssembly assembly = null;
    List<CMLAtom> atomList = null;
    double occupancy = Double.NaN;
    String code = null;

    /** constructor.
     * a group must currently have a parent assembly
     * @param assembly
     */
    public DisorderGroup(DisorderAssembly assembly) {
        atomList = new ArrayList<CMLAtom>();
        this.assembly = assembly;
    }
    
    /** adds a atom with disorder flag to the group.
     * atom must have same disorderGroup flag and same disorderAssembly
     * @param atom
     * @exception Exception
     */
    public void addAtom(CMLAtom atom) {
    	String groupCode = getAtomCode(atom);
    	if (groupCode == null) {
    		groupCode = ".";
    		assembly.addCommonAtom(atom);
    	} else {
    		code = groupCode;
    		atomList.add(atom);
    		double occ = atom.getOccupancy();
    		occupancy = occ;
    	}
    }

    /** gets disorder_assembly code.
     * 
     * @see CrystalTool#isIndeterminate(String)
     * @param atom with child disorder flags
     * @return code or null if indterminate
     */
    public static String getAtomCode(CMLAtom atom) {
        return CrystalTool.getValue(atom, 
            "cml:scalar[@dictRef='"+CrystalTool.DISORDER_GROUP+"']");
    }
    
    /** gets group code
     * @return group code
     */
    public String getGroupCode() {
    	return code;
    }
    
    /** gets the atoms in this disorder group
     * @return list of atoms in this disorder group
     */
    public List<CMLAtom> getAtomList() {
    	return atomList;
    }

    /** gets occupancy of group.
     * @return occupancy (uses first value in group)
     */
    public double getOccupancy() {
        return occupancy;
    }
    
    /** compare by occupancy.
     * 
     * @param dg
     * @return -1 if this.occupancy < dg.occupancy, etc.
     */
    public int compareTo(DisorderGroup dg) {
        int result = 0;
        if (this.occupancy < dg.occupancy) {
            result = -1;
        } else if (this.occupancy > dg.occupancy) {
            result = 1;
        }
        return result;
    }
    
    /** remaove all atoms from parent.
     */
    public void detachAtoms() {
        for (CMLAtom atom : atomList) {
            atom.detach();
        }
    }
    
    /** string representation.
     * @return string
     */
    public String toString() {
        String s = "disorderGroup: "+code+S_NEWLINE;
        s += "occupancy: "+occupancy+S_NEWLINE;
        for (CMLAtom atom : atomList) {
            s += S_SPACE+S_SPACE+CrystalTool.getFullLabel(atom)+S_NEWLINE;
        }
        return s;
    }

};