package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;

public class GroupTool {
	final static Logger LOG = Logger.getLogger(GroupTool.class);

	private static Map<String, GroupTool> groupToolMap;
	private static List<String> nameList;
	public static Map<String, GroupTool> getCommonGroupMap() {
		if (groupToolMap == null) {
			groupToolMap = new HashMap<String, GroupTool>();
			nameList = new ArrayList<String>();
// the order of these may matter and is preserved in nameList
// this is heaviest first
			// aromatic
			addGroupTool(new GroupTool("Bn", "[R]Cc1ccccc1"));
			addGroupTool(new GroupTool("Ph", "[R]c1ccccc1"));
			addGroupTool(new GroupTool("pTol", "[R]c1ccc(C)cc1"));
			// acids and derivatives
			addGroupTool(new GroupTool("CO2Et", "[R]C(=O)OCC"));
			addGroupTool(new GroupTool("CO2Me", "[R]C(=O)OC"));
			addGroupTool(new GroupTool("CONH2", "[R]C(=O)N"));
			addGroupTool(new GroupTool("CO2H", "[R]C(=O)O"));
			addGroupTool(new GroupTool("OAc", "[R]OC(=O)C"));
			addGroupTool(new GroupTool("NHAc", "[R]NC(=O)C"));
			// sulfur
			addGroupTool(new GroupTool("SMe", "[R]SC"));
			addGroupTool(new GroupTool("SO3H", "[R]S(=O)(=O)O"));
			addGroupTool(new GroupTool("SO2NH2", "[R]S(=O)(=O)N"));
			// nitrogen
			addGroupTool(new GroupTool("NO2", "[R]N(=O)=O"));
			addGroupTool(new GroupTool("NHOH", "[R]NO"));
			addGroupTool(new GroupTool("NMe2", "[R]N(C)C"));
			// phosphorus
			addGroupTool(new GroupTool("PPh2", "[R]P(c1ccccc1)(c1ccccc1)"));
			addGroupTool(new GroupTool("PO4H", "[R]OP(=O)(O)O"));
			addGroupTool(new GroupTool("PO3H", "[R]P(=O)(O)O"));
			// ketones
			addGroupTool(new GroupTool("Ac", "[R]C(=O)C"));
			// alkoxy
			addGroupTool(new GroupTool("OEt", "[R]OCC"));
			addGroupTool(new GroupTool("OMe", "[R]OC"));
			// alkyl groups
			addGroupTool(new GroupTool("sBu", "[R]C(C)CC"));
			addGroupTool(new GroupTool("tBu", "[R]C(C)(C)C"));
			addGroupTool(new GroupTool("Bu", "[R]CCCC"));
			addGroupTool(new GroupTool("Pr", "[R]CCC"));
			addGroupTool(new GroupTool("iPr", "[R]C(C)C"));
			addGroupTool(new GroupTool("Et", "[R]CC"));
			addGroupTool(new GroupTool("Me", "[R]C"));
			// misc
			addGroupTool(new GroupTool("CF3", "[R]C(F)(F)F"));
			addGroupTool(new GroupTool("CN", "[R]C#N"));
		}
		return groupToolMap;
	}
	
	public static List<String> getNameList() {
		return nameList;
	}

	private static void addGroupTool(GroupTool groupTool) {
		String name = groupTool.getName();
		if (name == null) {
			throw new RuntimeException("group name cannot be null");
		}
		if (groupToolMap.containsKey(name)) {
			throw new RuntimeException("duplicate group name: "+name);
		}
		nameList.add(name);
		groupToolMap.put(name, groupTool);
	}

	private CMLAtom rootAtom;
	private CMLBond rootBond;
	private CMLAtomSet matchedAtomSet;
	private String morganString;
	private String smilesString;
	private String name;
	
	private GroupTool(String name, CMLBond rootBond, CMLAtom rootAtom, String morganString, CMLAtomSet matchedAtomSet, String smilesString) {
		this.rootBond = rootBond;
		this.rootAtom = rootAtom;
		this.morganString = morganString;
		this.matchedAtomSet = matchedAtomSet;
		this.smilesString = smilesString;
		this.name = name;
	}

	public GroupTool(String name, CMLMolecule rMolecule) {
		this(name, null, null, null, null, null);
		morganString = Morgan.createMorganStringFromRGroupMolecule(rMolecule);
	}

	public GroupTool(String name, String rSmiles) {
		this(name, null, null, null, null, rSmiles);
		CMLMolecule rMolecule = SMILESTool.createMolecule(rSmiles);
		morganString = Morgan.createMorganStringFromRGroupMolecule(rMolecule);
	}

	public GroupTool(GroupTool groupToolTemplate) {
		this.rootBond = groupToolTemplate.rootBond;
		this.rootAtom = groupToolTemplate.rootAtom;
		this.morganString = groupToolTemplate.morganString;
		this.matchedAtomSet = groupToolTemplate.matchedAtomSet;
		this.smilesString = groupToolTemplate.smilesString;
		this.name = groupToolTemplate.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matchedAtomSet == null) ? 0 : matchedAtomSet.hashCode());
		result = prime * result
				+ ((morganString == null) ? 0 : morganString.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((rootAtom == null) ? 0 : rootAtom.hashCode());
		result = prime * result
				+ ((rootBond == null) ? 0 : rootBond.hashCode());
		result = prime * result
				+ ((smilesString == null) ? 0 : smilesString.hashCode());
		return result;
	}

	public CMLAtom getRootAtom() {
		return rootAtom;
	}

	public CMLBond getRootBond() {
		return rootBond;
	}

	public String getMorganString() {
		return morganString;
	}

	public String getSmilesString() {
		return smilesString;
	}

	public String getName() {
		return name;
	}

	public void setRootAtom(CMLAtom rootAtom) {
		this.rootAtom = rootAtom;
	}

	public void setRootBond(CMLBond rootBond) {
		this.rootBond = rootBond;
	}

	public void setMorganString(String morganString) {
		this.morganString = morganString;
	}

	public void setSmilesString(String smilesString) {
		this.smilesString = smilesString;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupTool other = (GroupTool) obj;
		if (matchedAtomSet == null) {
			if (other.matchedAtomSet != null)
				return false;
		} else if (!matchedAtomSet.equals(other.matchedAtomSet))
			return false;
		if (morganString == null) {
			if (other.morganString != null)
				return false;
		} else if (!morganString.equals(other.morganString))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rootAtom == null) {
			if (other.rootAtom != null)
				return false;
		} else if (!rootAtom.equals(other.rootAtom))
			return false;
		if (rootBond == null) {
			if (other.rootBond != null)
				return false;
		} else if (!rootBond.equals(other.rootBond))
			return false;
		if (smilesString == null) {
			if (other.smilesString != null)
				return false;
		} else if (!smilesString.equals(other.smilesString))
			return false;
		return true;
	}

	public boolean isDuplicate(GroupTool groupToolNew) {
		boolean duplicate = true;
		duplicate &= !isDuplicate(groupToolNew.getName(), name);
		duplicate &= !isDuplicate(groupToolNew.getMorganString(), morganString);
		duplicate &= !isDuplicate(groupToolNew.getSmilesString(), smilesString);
		return duplicate;
	}

	private boolean isDuplicate(String name, String name1) {
		return (name == null && name1 == null) || name.equals(name1);
	}

	private static String[] alkylNames = {
		"Bug",
		"Me",
		"Et",
		"Pr",
		"Bu",
	};
	/*
	 * length or Me is 1, Et is 2, etc.
	 */
	public static String getAlkylName(int len) {
		return (len < 5) ? alkylNames[len] : "(CH2)"+(len)+"CH3";
	}

	
}
